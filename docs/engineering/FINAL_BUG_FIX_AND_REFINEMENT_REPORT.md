# Math Master — Final Bug Fix and Refinement Report

**Phase**: Final Independent Adversarial Bug Hunt & Stabilization Pass  
**Scope**: Surgical root-cause fixes (`BUG-22` through `BUG-35`) across Domain Engines, ViewModel State Management, Repository Mistake Synchronization, Compose PDF Rendering, and Repository Organization.

---

## 1. Engineering Principles Followed

1. **Root-Cause Resolution Only**: Every change targets a verified state, scoring, validation, concurrency, or UX defect without introducing speculative features or architectural churn.
2. **Strict Module & Data Preservation**: Protected Modules 01–04 (`Tables`, `Primes`, `Squares`, `Formulas`), Room Database `v9` (`20` tables, `MIGRATION_1_2`..`MIGRATION_8_9`), and all 4 bundled NCTB PDFs (`894` pages) remain 100% untouched.
3. **Deterministic State & Scoring**: All session counters (`correctCount`, `wrongCount`, `currentStreak`, `highestStreak`, `elapsedSeconds`, `pendingOptionSelections`, `pendingTypedAnswers`) are derived deterministically from authoritative session state.

---

## 2. Detailed Root-Cause Fixes Implemented

### 2.1 `BUG-22` — Book Practice Bidirectional Navigation & Score Deduplication
- **Files Modified**:
  - `app/src/main/java/com/example/math/BookModels.kt`
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
- **Problem**:
  - `BookPracticeSessionState` previously tracked `answeredResults: Map<Int, Boolean>` but did not store per-question `selectedOptionsByIndex: Map<Int, Set<Int>>` or `userTextInputsByIndex: Map<Int, String>`.
  - Calling `previousBookPracticeQuestion()` reset `selectedOptionIndices = emptySet()` and `userTextInput = ""`, erasing the student's displayed answer on previous questions.
  - Calling `nextBookPracticeQuestion()` unconditionally set `isAnswerSubmitted = false` and `isCurrentCorrect = null` even when moving forward onto a question that had already been answered, forcing the user to submit it again and incrementing `correctCount`/`wrongCount` a second time.
- **Fix**:
  - Added `selectedOptionsByIndex`, `userTextInputsByIndex`, and `sessionStartEpochMs` to `BookPracticeSessionState`.
  - Updated `toggleBookPracticeOption()` and `setBookPracticeTextInput()` to persist selections/text per question index.
  - Guarded `submitBookPracticeAnswer()` with `if (current.isAnswerSubmitted || current.isCompleted || current.answeredResults.containsKey(current.currentIndex)) return` and computed `correctCount = updatedResults.values.count { it }` and `wrongCount = updatedResults.values.count { !it }`.
  - Updated both `previousBookPracticeQuestion()` and `nextBookPracticeQuestion()` to restore `selectedOptionIndices`, `userTextInput`, `isAnswerSubmitted`, and `isCurrentCorrect` from the per-index maps.

### 2.2 `BUG-23` — Number System Practice True Streak, Elapsed Duration & Mistake Logging
- **Files Modified**:
  - `app/src/main/java/com/example/math/NumberSystemModels.kt`
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
- **Problem**:
  - `nextNumberSystemQuestion()` previously recorded `durationSeconds = 30L` and `highestStreak = current.score` (conflating total score with consecutive streak), and wrong answers in Number System Practice were not logged to `unified_mistake_notebook`.
- **Fix**:
  - Added `currentStreak: Int = 0`, `highestStreak: Int = 0`, and `sessionStartEpochMs: Long = System.currentTimeMillis()` to `NumberSystemPracticeState`.
  - Updated `submitNumberSystemAnswer()` to increment `currentStreak` on correct answers, reset `currentStreak = 0` on wrong answers, track `highestStreak = maxOf(current.highestStreak, nextStreak)`, and record/resolve `NUMBER_SYSTEM_${currentQ.id}` in `unified_mistake_notebook`.
  - Updated `nextNumberSystemQuestion()` to compute real elapsed seconds from `sessionStartEpochMs` and persist `bestStreak`.

### 2.3 `BUG-24` — Cross-Module Unified Mistake Notebook Synchronization & Auto-Resolution
- **File Modified**: `app/src/main/java/com/example/ui/MathViewModel.kt`
- **Problem**:
  - `UnifiedMistakeEntity` supports `"PRACTICE_LAB"`, `"MENTAL_MATH"`, `"NUMBER_SYSTEM"`, and `"BOOK"`, but wrong answers in Mental Math, Number System, and Book Practice were not consistently populating `unified_mistake_notebook`, and answering a previously missed question correctly did not auto-resolve its unified mistake entry.
- **Fix**:
  - Connected `submitPracticeAnswer()`, `submitMentalMathAnswer()`, `submitNumberSystemAnswer()`, and `submitBookPracticeAnswer()` to both `repository.recordUnifiedMistake(...)` (on wrong answers) and `repository.resolveUnifiedMistake(...)` (on correct answers).

### 2.4 `BUG-25` — Dashboard KPI Deduplication for `BOOK_` Sessions
- **File Modified**: `app/src/main/java/com/example/math/LearningAnalyticsEngine.kt`
- **Problem**:
  - `finishBookPracticeSession()` writes both a `PracticeHistoryEntity(module = "BOOK")` and a legacy `QuizScoreEntity(topicId = "BOOK_...")`. Because `LearningAnalyticsEngine.computeSeparatedKpis()` only excluded `PRACTICE_`, `MENTAL_MATH_`, and `NUMBER_SYSTEM` from `standaloneLegacyQuizzes`, every Book Practice session was counted twice on the Dashboard (once as a Practice session and once as a standalone Exam).
- **Fix**:
  - Added `!legacy.topicId.startsWith("BOOK_")` to `standaloneLegacyQuizzes` in `LearningAnalyticsEngine.computeSeparatedKpis()`.

### 2.5 `BUG-26` — Textbook Question Engine Fractional, Ratio & Unit Equivalence
- **File Modified**: `app/src/main/java/com/example/math/BookQuestionEngine.kt`
- **Problem**:
  - `BookQuestionEngine.isTextEquivalent()` only checked lowercase string equality and `toDoubleOrNull()`, failing when students entered equivalent fractions (`১/২` vs `0.5` or `২/৪`), ratios (`৩ : ৪` vs `6 : 8`), or unit-suffixed numbers (`৩৬টি` vs `36`).
- **Fix**:
  - Delegated fallback evaluation in `BookQuestionEngine.isTextEquivalent()` to `SharedMathEngine.isAnswerCorrect(userRaw, expectedRaw)`.

### 2.6 `BUG-27` — Multi-Book Filter Bar in `BookRevisionScreen`
- **File Modified**: `app/src/main/java/com/example/ui/screens/BookPracticeAndRevisionScreens.kt`
- **Problem**:
  - `BookRevisionScreen` displayed items from all books without a filter bar while binding new personal notes to `selectedBookId`.
- **Fix**:
  - Added a horizontal filter chip bar (`সব বই` + `BookId.entries`) to `BookRevisionScreen` that filters Spaced Revisions, Mistake Notebook entries, Personal Notes, and Bookmarked PDF Pages by textbook and synchronizes `viewModel.selectBook(bId)` when a specific textbook chip is selected.

### 2.7 `BUG-29` — Dotless Textbook Unit Suffixes & Mixed Fraction Equivalence
- **File Modified**: `app/src/main/java/com/example/math/SharedMathEngine.kt`
- **Problem**:
  - `stripCommonUnitSuffixes()` stripped dotted units (`"সে.মি."`, `"কি.মি."`, `"মি."`) but missed dotless textbook units (`"সেমি"`, `"বর্গসেমি"`, `"ঘনসেমি"`, `"মিটার"`, `"বর্গমিটার"`, `"কিমি"`). Additionally, `normalizeAnswerToken()` stripped all spaces before fraction parsing, turning mixed fractions (`"1 1/2"`, `"১ ১/২"`, `"১ সমস্ত ১/২"`, `"১ পূর্ণ ১/২"`) into `"11/2"` instead of `"3/2"`.
- **Fix**:
  - Expanded `stripCommonUnitSuffixes()` with iterative longest-first unit stripping and converted mixed fractions into improper fractions `"a/b"` inside `normalizeAnswerToken()` prior to removing whitespace.

### 2.8 `BUG-30` — Distributive Parenthesized Linear & Quadratic Algebraic Simplification
- **File Modified**: `app/src/main/java/com/example/math/SharedMathEngine.kt`
- **Problem**:
  - `SharedMathEngine.simplify()` documented support for `2(x + 3) + 4x - 1`, but `parseQuadraticExpression()` lacked distributive parenthesis expansion.
- **Fix**:
  - Added `expandAlgebraicParentheses()` in `SharedMathEngine` to expand `k(ax + b)` and `±(ax^2 + bx + c)` terms and include an explicit distributive expansion step in `simplify()`.

### 2.9 `BUG-31` — Book Mistake & Spaced Revision ID Normalization + Cross-Table Sync
- **Files Modified**:
  - `app/src/main/java/com/example/data/repository/MathRepository.kt`
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
- **Problem**:
  - `deleteBookMistake(questionId)` called `deleteBookSpacedRevisionById("mistake_$questionId")`, producing `"mistake_mistake_<id>"` when `questionId` already had a `"mistake_"` prefix, leaving orphaned entries in `book_spaced_revision`. Furthermore, answering a Book question correctly in `submitBookPracticeAnswer()` did not call `markBookMistakeResolved()` on `book_mistakes`.
- **Fix**:
  - Normalized `rawId` and `revItemId = "mistake_$rawId"` across `recordBookQuestionMistake`, `markBookMistakeResolved`, and `deleteBookMistake`, synchronized all three with `unified_mistake_notebook` (`"BOOK_$rawId"`), and invoked `markBookMistakeResolved` in `submitBookPracticeAnswer()` when `isCorrect == true`.

### 2.10 `BUG-32` — Canonical BCS Question ID Resolution & Quiz/BCS Unified Mistake Sync
- **Files Modified**:
  - `app/src/main/java/com/example/data/repository/MathRepository.kt`
  - `app/src/main/java/com/example/ui/MathViewModel.kt`
- **Problem**:
  - BCS exam questions generated with mode-specific prefixes (`"bcs_exam_1_<id>"`, `"bcs_prev_1_<id>"`, `"bcs_mistake_retry_1_<id>"`, `"bcs_ma_exam_1_<id>"`) did not match canonical question IDs when retried in `MISTAKE_PRACTICE` or Question Bank, and Quiz/BCS sessions did not sync with `unified_mistake_notebook`.
- **Fix**:
  - Added `extractCanonicalBcsQuestionId()` in `MathRepository`, synchronized `recordBcsMistake`/`markBcsMistakeResolved`/`deleteBcsMistake` with `unified_mistake_notebook`, and wired `submitQuizAnswer()` and `finishQuizSession()` to record and resolve mistakes across both tables.

### 2.11 `BUG-33` — Unsubmitted Quiz Draft Preservation Across Navigation & Timeout
- **File Modified**: `app/src/main/java/com/example/ui/MathViewModel.kt`
- **Problem**:
  - In non-`isFullExamMode` quizzes (`TOPIC`, `TIMED`, `PRACTICE`, `MIXED`), selecting an option or typing a numerical answer without clicking "উত্তর যাচাই করুন" was lost when navigating via `jumpToQuizQuestion()` or `previousQuizQuestion()`, or when the timer expired on another question.
- **Fix**:
  - Added `pendingOptionSelections` and `pendingTypedAnswers` to `QuizSessionState`, restored pending drafts on navigation, and rescued unsubmitted drafts in `finishQuizSession()`.

### 2.12 `BUG-34` — Exclude Skipped Zero-Attempt Topics from `weakTopics`
- **File Modified**: `app/src/main/java/com/example/math/ExamEngineModels.kt`
- **Problem**:
  - `ExamScoringEngine.evaluateExam()` marked topics with `attemptedCount == 0` (all questions skipped) as `weakTopics`.
- **Fix**:
  - Added `it.attemptedCount > 0` to the `weakTopics` filter in `ExamScoringEngine.evaluateExam()`.

### 2.13 `BUG-35` — Book PDF Reader `Mutex` + `LruCache(12)` + `RGB_565` & Synchronized Extraction
- **File Modified**: `app/src/main/java/com/example/ui/screens/BookReaderAndLabsScreens.kt`
- **Problem**:
  - `BookPdfReaderScreen` rendered `ARGB_8888` bitmaps without `Mutex` serialization or `LruCache(12)`, and `resolveOfflinePdfFile()` wrote to a shared `cached_${bookId.id}.pdf.tmp` without synchronization.
- **Fix**:
  - Added `BookPdfRenderCache` (`Mutex` + bounded `LruCache<String, Bitmap>(12)` + `Bitmap.Config.RGB_565` + OOM cache-eviction fallback) and synchronized atomic temp-file extraction in `resolveOfflinePdfFile()`.

