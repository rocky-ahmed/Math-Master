# MATH MASTER — UI/UX AND PERFORMANCE AUDIT REPORT

## 1. Lifecycle & State Management Audit (`BUG-18` Resolved)

### 1.1 100% `collectAsStateWithLifecycle()` Adoption
- **Audit Finding**: Previously, 15 UI screen files used `androidx.compose.runtime.collectAsState()`, which keeps upstream Kotlin `Flow` collectors active even when the Android Activity moves to `STOPPED` (background).
- **Remediation**: Every `@Composable` screen in `app/src/main/java/com/example/ui/` now exclusively uses `androidx.lifecycle.compose.collectAsStateWithLifecycle()`.
- **Verification**: `grep -rnI "collectAsState(" app/src/main/java/` returns **0 matches**.

### 1.2 Monotonic Timer Accuracy
- **Mental Math (`MentalMathPlayScreen.kt` / `MathViewModel.kt`)** and **Exam Engine (`QuizzesScreen.kt` / `MathViewModel.kt`)**:
  - Timed sessions compute remaining time from `SystemClock.elapsedRealtime()` (`deadlineElapsedRealtimeMs`), preventing timer drift during GC pauses or recompositions.
  - Untimed and practice sessions (`QuizMode.TOPIC`, `QuizMode.PRACTICE`, `QuizMode.MIXED`, `PRACTICE_EXAM`, `TOPIC_PRACTICE`, `MISTAKE_PRACTICE`) track upward `elapsedSeconds` (`(nowRealtime - sessionStartElapsedRealtimeMs) / 1000L`) and display a neutral timer badge (`quiz_timer_badge`) without auto-submitting (`BUG-14`).

---

## 2. Recomposition & Memory Efficiency

### 2.1 Lazy List Keys & Derived Computations
- **Stable Keys**: `LazyColumn` and `LazyVerticalGrid` across `HomeScreen.kt`, `DashboardScreen.kt`, `FormulaLibraryScreen.kt`, `BookHubAndDetailScreens.kt`, `BcsPreparationScreen.kt`, and `BookmarksAndTutorScreens.kt` use deterministic `key = { ... }` lambdas to prevent unnecessary item recomposition.
- **Memoized Filtering (`remember(...)`)**:
  - `FormulaLibraryScreen`: Memoizes `filteredFormulas` and `availableSubCategories` via `remember(searchQuery, selectedSubject, ...)`.
  - `DashboardScreen`: Memoizes `analyticsData` via `remember(quizScores, practiceHistory, progressList, ...)` and deduplicates multi-profile BCS guide completions via `rawTopicId` (`BUG-20`).
  - `BookHubScreen`: Memoizes full-text search across all 47 chapters (`BookCurriculumRepository.searchAllBooks`).

### 2.2 PDF Bitmap Caching & Background Rendering (`BookReaderAndLabsScreens.kt`)
- `BookPdfReaderScreen` renders `PdfRenderer.Page` bitmaps on `Dispatchers.IO` inside a mutex-synchronized renderer and caches up to 5 rendered page bitmaps in an `android.util.LruCache<String, Bitmap>(5)`, preventing UI thread jank and `OutOfMemoryError` when flipping through 362-page textbooks.

---

## 3. Navigation, Backstack & Accessibility Compliance

### 3.1 Deterministic Back Handling
- Every secondary screen registers `BackHandler { ... }` (or delegates to `viewModel.navigateBack()`), ensuring hardware/gesture back navigation never traps the user or loses active exam state (full exams prompt confirmation before exit).
- Bookmark deep-links in `BookmarksScreen` now navigate directly to the target Table (`openTable`), Algebra Formula (`openFormula`), Formula Library card (`openLibraryFormulaFromBookmark`, `BUG-19`), Book Formula chapter (`openBookChapter`), or Book PDF page (`openBookPdfReader`).

### 3.2 Material 3 Design, Bengali Typography & Testability
- **Edge-to-Edge**: `enableEdgeToEdge()` enabled in `MainActivity.kt` with `Scaffold` window insets handled across all screens.
- **Bengali Typography**: Uses bundled `HindSiliguri` font family (`res/font/hind_siliguri_*.ttf`) with generous line heights (`20.sp–24.sp`) for complex Bengali conjuncts and mathematical symbols.
- **Compose TestTags**: All primary interactive controls, cards, tabs, chips, and inputs expose deterministic `Modifier.testTag(...)` identifiers for automated Robolectric verification.
