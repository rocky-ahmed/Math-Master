# Math Master — Bug Fix Implementation Report (`BUG-01` through `BUG-21`)

## 1. Executive Summary
All **21 confirmed Phase 1 engineering defects (`BUG-01` through `BUG-21`)** across the Exam Engine (`BUG-01`: **Numerical Keypad Scoring and Negative Marking**; `BUG-02`–`BUG-04`), BCS Preparation System (`BUG-05`, `BUG-06`, `BUG-13`, `BUG-21`), Mathematical Engines (`BUG-07`, `BUG-08`), NCTB Book & PDF Reader (`BUG-09` `+5` page offset), Room v9 Migrations (`BUG-10`), Home Grid & Backstack (`BUG-11`, `BUG-12`), Untimed/Review/Question-Type Quizzes (`BUG-14`–`BUG-16`), Number System Practice (`BUG-17`), Lifecycle State Collection (`BUG-18`), Bookmarks Deep-Linking (`BUG-19`), and Dashboard Analytics (`BUG-20`) have been fixed at their root causes without breaking existing user data or module functionality.

> **Forward Traceability Note (`BUG-22` – `BUG-36`)**: For Phase 2 (`BUG-22`–`BUG-28`), Phase 3 (`BUG-29`–`BUG-35`, including **`BUG-35`: Book PDF Reader concurrency, bitmap memory management, `Mutex` serialization and `LruCache` optimization**), and Phase 4 (**`BUG-36`: Home Screen Scroll Position Restoration**), see [`FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md`](FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md) and [`../audits/FINAL_ROOT_CAUSE_DEFECT_REGISTER.md`](../audits/FINAL_ROOT_CAUSE_DEFECT_REGISTER.md).

---

## 2. Detailed Root-Cause Fixes Implemented in Final Audit (`BUG-14` through `BUG-21`)

### 2.1 `BUG-14` — Untimed Quiz & BCS Practice Countdown / Auto-Submit Elimination
- **Files Modified:**
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
  - `app/src/main/java/com/example/ui/screens/QuizzesScreen.kt`
- **Root Cause:**
  1. `MathViewModel.startQuiz()` initialized `QuizMode.UNTIMED` with `val durationSeconds = if (mode == QuizMode.TIMED) questions.size * 45 else 1`, causing `tickQuizTimer()` to decrement `1 -> 0` and immediately terminate untimed quizzes after 1 second.
  2. `MathViewModel.startBcsModeExam()` forced `val durationMinutes = mode.durationMinutes.coerceAtLeast(5)` and a positive `configuredDurationSeconds` even for `BcsPracticeMode.TOPIC_PRACTICE` and `BcsPracticeMode.MISTAKE_PRACTICE` (`immediateFeedback = true`), causing practice sessions to count down and auto-submit.
- **Fix Implemented:**
  - Set `configuredDurationSeconds = 0`, `timeRemainingSeconds = 0`, and `deadlineElapsedRealtimeMs = 0L` whenever `mode != QuizMode.TIMED` or `config.immediateFeedback` is `true` (`TOPIC_PRACTICE`, `MISTAKE_PRACTICE`, `PRACTICE_EXAM`).
  - Updated `tickQuizTimer()` so only timed sessions (`current.mode == QuizMode.TIMED && current.configuredDurationSeconds > 0`) compute remaining countdown seconds and auto-submit on expiry; untimed/practice modes track upward `elapsedSeconds` only via `SystemClock.elapsedRealtime()`.
  - Updated `QuizzesScreen.kt` top timer pill to display upward elapsed time (`সময়: MM:SS`) for untimed/practice sessions instead of `00:00`.

### 2.2 `BUG-15` — Strict Unanswered vs. Wrong Separation in Review Incorrect & Review Cards
- **Files Modified:**
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
  - `app/src/main/java/com/example/ui/screens/QuizzesScreen.kt`
- **Root Cause:**
  - `MathViewModel.startReviewIncorrectQuiz()` filtered questions with `ans == null || ans != q.correctIndex`, treating skipped/unanswered questions (`null` or `-1`) as wrong answers.
  - `QuizAnswerReviewCard` in `QuizzesScreen.kt` only branched on `if (isCorrect)` vs `else`, styling unanswered questions with the red wrong-answer container and close icon.
- **Fix Implemented:**
  - Updated `startReviewIncorrectQuiz()` to filter strictly with `ExamScoringEngine.isOptionAnswered(ans) && ans != q.correctIndex`.
  - Updated `QuizAnswerReviewCard` to detect `val isUnanswered = ExamScoringEngine.isOptionUnanswered(userChoice)` and render a distinct neutral surface container with `Icons.Default.HelpOutline` and badge `"উত্তর দেওয়া হয়নি (স্কিপড)"`.

### 2.3 `BUG-16` — Strict `QuizQuestionType.NUMERICAL` and `TRUE_FALSE` Enforcement Across All Topics & `BCS_PRELIMINARY_MOCK`
- **Files Modified:**
  - `app/src/main/java/com/example/math/QuizModels.kt`
  - `app/src/main/java/com/example/math/ExamEngineModels.kt`
- **Root Cause:**
  - `QuizGenerator.generateQuiz()` converted questions whose answer was not a pure number back to `QuizQuestionType.MCQ` instead of filtering/regenerating numeric-answer questions when `questionType == QuizQuestionType.NUMERICAL`.
  - `ExamQuestionBank.generateExamQuestions()` returned early for `ExamPresetType.BCS_PRELIMINARY_MOCK` before applying `config.questionType` (`NUMERICAL` / `TRUE_FALSE`).
- **Fix Implemented:**
  - Updated `QuizGenerator.generateQuiz()` to filter for `isNumericAnswerCompatible(q.correctAnswerText)` when `questionType == QuizQuestionType.NUMERICAL` and draw from numeric fallback generators if a conceptual topic pool has fewer numeric items than `count`.
  - Extracted `ExamQuestionBank.applyQuestionType()` and applied it uniformly to `BCS_PRELIMINARY_MOCK` as well as all other exam presets.

### 2.4 `BUG-17` — Number System Practice `KEYPAD` and `TRUE_FALSE` Support
- **Files Modified:**
  - `app/src/main/java/com/example/math/NumberSystemModels.kt`
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
  - `app/src/main/java/com/example/ui/screens/NumberSystemScreen.kt`
  - `app/src/main/java/com/example/ui/screens/NumberSystemPracticePlayScreen.kt`
- **Root Cause:**
  - `NumberSystemPracticeState` exposed `inputFormat: QuestionInputFormat`, and `NumberSystemPracticeHubTab` offered `MCQ`, `KEYPAD`, and `TRUE_FALSE` chips, but `NumberSystemPracticePlayScreen.kt` only rendered MCQ options and `MathViewModel` only validated option indices.
- **Fix Implemented:**
  - Added `NumberSystemProvider.formatQuestionForInputFormat()` to convert questions into `সত্য (True)` / `মিথ্যা (False)` verification statements when `QuestionInputFormat.TRUE_FALSE` is active.
  - Updated `MathViewModel.submitNumberSystemAnswer()` to validate typed answers via `SharedMathEngine.isAnswerCorrect(state.typedInput, currentQ.correctAnswerBn)` when `QuestionInputFormat.KEYPAD` is active.
  - Updated `NumberSystemPracticePlayScreen.kt` to render `BanglaNumericKeypad` and typed answer preview in `KEYPAD` mode and True/False cards in `TRUE_FALSE` mode.

### 2.5 `BUG-18` — Lifecycle-Aware State Collection Across All Compose Screens
- **Files Modified:**
  - `app/src/main/java/com/example/ui/screens/FormulaLibraryScreen.kt`
  - `app/src/main/java/com/example/ui/screens/NumberSystemScreen.kt`
  - `app/src/main/java/com/example/ui/screens/NumberSystemPracticePlayScreen.kt`
- **Fix Implemented:**
  - Replaced all remaining `collectAsState()` calls with `collectAsStateWithLifecycle()` (`androidx.lifecycle.compose.collectAsStateWithLifecycle`). Zero `collectAsState(` usages remain anywhere in `app/src/main/java`.

### 2.6 `BUG-19` — Formula Library Bookmark Deep-Linking
- **Files Modified:**
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
  - `app/src/main/java/com/example/ui/screens/BookmarksAndTutorScreens.kt`
  - `app/src/main/java/com/example/ui/screens/FormulaLibraryScreen.kt`
- **Fix Implemented:**
  - Added `selectedLibraryFormulaId: StateFlow<String?>`, `openLibraryFormulaFromBookmark(formulaId: String)`, and `clearSelectedLibraryFormulaId()` in `MathViewModel.kt`.
  - Wired `BookmarksScreen` to invoke `viewModel.openLibraryFormulaFromBookmark(bm.id)` for `FORMULA_LIBRARY` bookmarks, and updated `FormulaLibraryScreen.kt` (`LaunchedEffect(selectedLibraryFormulaId)`) to automatically select the formula's subject filter and expand its detail card.

### 2.7 `BUG-20` — Dashboard Multi-Profile BCS Deduplication & `BOOK` History Filter Chip
- **Files Modified:**
  - `app/src/main/java/com/example/ui/screens/DashboardScreen.kt`
- **Fix Implemented:**
  - Deduplicated completed BCS guides across profiles using `bcsTopicProgress.filter { it.isGuideCompleted }.map { it.rawTopicId }.distinct().size`.
  - Added `"BOOK" to "পাঠ্যবই অনুশীলন"` to the module filter chips on `DashboardScreen.kt`.

### 2.8 `BUG-21` — Multi-Profile BCS Mistake Resolution, Deletion, Auto-Resolve & Lesson Count Deduplication
- **Files Modified:**
  - `app/src/main/java/com/example/data/repository/MathRepository.kt`
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
  - `app/src/main/java/com/example/ui/screens/BcsPreparationScreen.kt`
- **Root Cause:**
  1. `MathRepository.recordBcsMistake()` stores non-default profile mistakes with `questionId = "$examProfileId::$rawQId"`, whereas `markBcsMistakeResolved()` and `deleteBcsMistake()` only queried the passed `questionId` directly, failing when given a raw `questionId` for a non-default profile.
  2. `MathViewModel.finishQuizSession()` recorded new BCS mistakes for wrong answers but did not resolve existing BCS mistakes when the user answered the question **correctly** in a BCS session (`MISTAKE_PRACTICE` or mock exam).
  3. `MathViewModel.startBcsModeExam()` used `(it.examProfileId == activeProfile.id || !it.questionId.contains("::"))` instead of `it.belongsToProfile(activeProfile.id)`, allowing default-profile unprefixed mistakes to leak into non-default profiles.
  4. `BcsPreparationScreen.kt` counted `profileTopicProgress.count { it.isGuideCompleted }` without `.map { it.rawTopicId }.distinct()`.
- **Fix Implemented:**
  - Updated `MathRepository.markBcsMistakeResolved()` and `deleteBcsMistake()` to accept `examProfileId` and resolve both `questionId` and `"$examProfileId::$rawQId"`.
  - Updated `MathViewModel.finishQuizSession()` to call `repository.markBcsMistakeResolved(questionId = q.id, examProfileId = activeBcsProfileId)` whenever a BCS question is answered correctly.
  - Updated `MathViewModel.startBcsModeExam()` to filter mistakes with `it.belongsToProfile(activeProfile.id)`.
  - Deduplicated `completedLessonsCount` by `rawTopicId` in `BcsPreparationScreen.kt`.
