# PRODUCT_QUALITY_AUDIT.md
# Math Master Android v3.2 — Comprehensive Engineering Quality Audit

Date: 2026-11-02
Audited Commit: 7fb8c5713dbbb36480f1bde3487f940f74775a0
Target Device: Xiaomi Redmi Note 9 (M2003J15SC, Android 12, API 31)
Environment: Windows 11, JDK 21 LTS, Android SDK 36.1, Gradle 9.3.1, 4 GB RAM Host

---

## 1. Executive Summary

A multi-agent engineering audit across the Mathematics Engine, UI/UX Presentation & Navigation, and Data Persistence & Performance revealed critical bugs, mathematical calculation fallacies, UI thread freezing, and data consistency risks.

| Category | Critical | High | Medium | Low | Total |
| :--- | :---: | :---: | :---: | :---: | :---: |
| Mathematics Engine | 4 | 4 | 4 | 3 | 15 |
| UI/UX & Navigation | 2 | 4 | 5 | 4 | 15 |
| Data & Performance | 3 | 4 | 6 | 4 | 17 |
| **Total** | **9** | **12** | **15** | **11** | **47** |

---

## 2. Priority 1: Critical Findings

### [MATH-CRIT-01] Mental Math Mixed Subtraction Result Clamped to 1
- **File:** `app/src/main/java/com/example/math/MentalMathModels.kt:501`
- **Issue:** When `prod1 < prod2` in hard mixed arithmetic, `(prod1 - prod2).coerceAtLeast(1)` sets answer to 1. For example, `(30) - (56) = ?` has correct answer set to `1` and explanation says `(30) - (56) = 1`.
- **Impact:** Teaches mathematically false equations.
- **Fix:** Ensure `prod1 > prod2` by swapping terms before constructing subtraction problems.

### [MATH-CRIT-02] Crash on Negative Numbers in Divisibility Rules
- **File:** `app/src/main/java/com/example/math/MathProblemSolverEngine.kt:1685, 1761`
- **Issue:** Calling `digitToInt()` on `'-'` character in negative integers throws `IllegalArgumentException: Char - is not a decimal digit`.
- **Impact:** Immediate crash on evaluating negative numbers in divisibility engine.
- **Fix:** Use `abs(num)` prior to string conversion and digit mapping.

### [MATH-CRIT-03] Tokenizer Crash on Negated Parentheses in BODMAS
- **File:** `app/src/main/java/com/example/math/MathProblemSolverEngine.kt:2420, 2471`
- **Issue:** Expression `-(2 - 7)` evaluates inside to `-5.0`, yielding `--5.0` which is tokenized as a single token and fails in `toDouble()` with `NumberFormatException`.
- **Impact:** Crash on standard expressions with unary minus on brackets.
- **Fix:** Normalize double signs (`--` -> `+`, `+-` -> `-`, `-+` -> `-`, `++` -> `+`).

### [MATH-CRIT-04] Division-by-Zero Evaluated as 5.0 in Visual Fractions
- **File:** `app/src/main/java/com/example/math/VisualMathModels.kt:408` & `VisualMathExplorerUnitTest.kt:167`
- **Issue:** When denominator is 0, code substitutes `den = 1` and calculates `5 / 0 = 5.0`, labeling it improper fraction.
- **Impact:** Severe mathematical inaccuracy codified in code and unit tests.
- **Fix:** Add `isDefined: Boolean` to `FractionVisualModel`; return `isDefined = false` and `Double.NaN` with undefined error explanation.

### [UI-CRIT-01] Algebra Quiz Options Never Shuffled (100% Option A Exploit)
- **File:** `app/src/main/java/com/example/math/QuizModels.kt:313-356`
- **Issue:** In `generateAlgebraQuestions`, all questions have static option lists with `correctIndex = 0`. Options are never shuffled.
- **Impact:** 100% of algebra quiz questions have option 1 as the correct answer.
- **Fix:** Shuffle options dynamically upon generating the questions and update `correctIndex`.

### [UI-CRIT-02] Practice Tutor Step 10 Infinite Activity Farming
- **File:** `app/src/main/java/com/example/ui/MathViewModel.kt:765-785`, `BookmarksAndTutorScreens.kt:335`
- **Issue:** Completing step 10 in tutor does not transition to a finished state or disable input, allowing infinite repeated submissions that award daily activity and streak counts on each tap.
- **Impact:** State corruption, infinite point farming, lack of completion feedback.
- **Fix:** Add `isCompleted` flag and show celebratory completion dialog with actions to navigate or retry.

### [PERF-CRIT-01] State Mutation & Heavy Math Solving Synchronously in Composition
- **File:** `app/src/main/java/com/example/ui/screens/ProblemSolverScreen.kt:180-280, 294-296`
- **Issue:** Synchronous call to `solveCurrentTopic()` inside Composable mutates `solverResult` state directly in composition and runs heavy parsing on Main thread.
- **Impact:** Recomposition churn, UI thread freezing, potential ANR.
- **Fix:** Execute solver asynchronously in coroutine on `Dispatchers.Default` via `LaunchedEffect`.

### [PERF-CRIT-02] Non-Atomic Read-Modify-Write Concurrency in Progress Tracking
- **File:** `app/src/main/java/com/example/data/repository/MathRepository.kt:32-57`
- **Issue:** `toggleLearned` and `recordPractice` perform check-then-act without transaction or atomic upsert.
- **Impact:** Lost practice counts and race conditions under rapid user interactions.
- **Fix:** Add atomic SQL upsert in `MathDao`.

### [PERF-CRIT-03] Disabled Room Schema Export & Destructive Migration Risk
- **File:** `app/src/main/java/com/example/data/db/MathDatabase.kt:14`
- **Issue:** `exportSchema = false` prevents schema verification and migration test helpers.
- **Impact:** High risk of data loss on future database upgrades.
- **Fix:** Enable `exportSchema = true` and configure KSP schema export directory.

---

## 3. High & Medium Findings Summary

- **[HIGH-01] Flow Collection Lifecycle:** 63 locations collect StateFlow with `collectAsState()` instead of `collectAsStateWithLifecycle()`, keeping Room DB active in background.
- **[HIGH-02] Missing Indexes:** `BookmarkEntity` and `QuizScoreEntity` missing indexes on `timestamp` and `topicId`.
- **[HIGH-03] Unused Firebase Bloat:** 100% unused Firebase suite (`firebase.ai`, `firebase.appcheck`) and Google Services plugin registered in build and AndroidManifest, slowing startup.
- **[HIGH-04] Gradle Memory Allocation:** `gradle.properties` specified `-Xmx4g` and `workers.max=4`, which threatens to crash 4 GB RAM Windows host.
- **[HIGH-05] Missing "SOLVER" Bookmark Navigation:** Dead click in `BookmarksScreen` when tapping bookmarked solver solutions.
- **[HIGH-06] Typography Scale Distortion:** `MathMasterTheme` sets `titleLarge = 16.sp` based on `globalConfig.titleFontSizeSp`, shrinking all app bar headers app-wide from 22sp to 16sp.
- **[HIGH-07] Unbounded Navigation Stack:** Bottom navigation bar taps append to backstack indefinitely without single-top behavior.
- **[HIGH-08] Missing Learned UI for Primes, Squares, Formulas:** Dashboard mastery permanently 0% for those topics because no UI existed to mark items learned.
- **[MED-01] Unkeyed Lazy Lists:** Missing `key` in `LazyColumn` and `LazyRow` leads to full list recomposition on item modifications.
- **[MED-02] $O(N \times M)$ Bookmark Scans:** Large lists perform linear scan on bookmarks list; needs indexed `Set<String>` lookup.
- **[MED-03] Dark Mode Glare:** Hardcoded light background in `FormulasScreen` proof card glares in dark theme.
- **[MED-04] Leaked Scanned Filenames:** `241713.jpg` and `241715.jpg` appear in subtitles and bookmark entries.
- **[LOW-01] Unnecessary INTERNET Permission:** App is 100% offline but declares `INTERNET` permission.
