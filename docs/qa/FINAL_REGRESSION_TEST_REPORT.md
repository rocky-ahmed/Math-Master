# Math Master — Final Regression Test Report

**Validation Command**: `gradle :app:testDebugUnitTest`  
**Execution Policy**: Strictly local JVM & Robolectric unit testing (`0` APK/AAB builds executed during audit).  
**Latest Verified Result**: **`175 / 175` Passed** (`0` Failures, `0` Errors, `0` Skipped)

---

## 1. Historical Baseline vs. Latest Regression Test Progression

| Audit Phase | Defects Covered | `EcosystemUpgradeV7UnitTest` Count | `HomeNavigationAndOrderingUnitTest` Count | Total Automated Tests Across 8 Suites | Pass Rate | Evidence Reference |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| **Phase 1: Core Engineering Audit Baseline** | `BUG-01` – `BUG-21` | `17` | `7` | **`165 / 165`** | `100%` | [`FINAL_DEFECT_REGISTER.md`](FINAL_DEFECT_REGISTER.md), [`../release/FINAL_RELEASE_CANDIDATE_REPORT.md`](../release/FINAL_RELEASE_CANDIDATE_REPORT.md) |
| **Phase 2: Pre-Freeze Stabilization Baseline** | `BUG-22` – `BUG-28` (`+2` tests) | `19` | `7` | **`167 / 167`** | `100%` | `auditRegression_bug22_*` & `auditRegression_bug23_bug25_bug26_*` |
| **Phase 3: Independent Adversarial Bug Hunt** | `BUG-29` – `BUG-35` (`+5` tests) | `24` | `7` | **`172 / 172`** | `100%` | `adversarialAudit_bug29_*` through `adversarialAudit_bug35_*` |
| **Phase 4: Real-Device UX & Scroll Restoration (Latest)** | `BUG-36` (`+3` tests) | `25` | `9` | **`175 / 175`** | `100%` | `bug36_homeScreenScrollPosition*` & `testHomeScreen*Scroll*` |

---

## 2. Automated Test Suite Inventory (`app/src/test/java/com/example/`)

| # | Test Suite File | Runner | Historical Baseline (`165`) | Adversarial Baseline (`172`) | Latest Verified (`175`) | Focus Area | Status |
| :- | :--- | :--- | :-: | :-: | :-: | :--- | :--- |
| 01 | `ExampleRobolectricTest.kt` | Robolectric (`sdk = [36]`) | `53` | `53` | **`53`** | Core Modules 01–14, Room `v1 -> v9` migrations, Book PDF/curriculum/labs, early module regressions | **PASS** |
| 02 | `MathProblemSolverUnitTest.kt` | JUnit4 | `41` | `41` | **`41`** | 14 Solver Topics (`LEARNING`, `SHORTCUT`, `BCS`), domain validation (`BUG-08`), non-linear & linear systems | **PASS** |
| 03 | `EcosystemUpgradeV7UnitTest.kt` | Robolectric (`sdk = [36]`) | `17` | `24` | **`25`** | Protected Modules 01–04 lock, Room `v6 -> v9` live SQLite migrations (`BUG-10`), `SharedMathEngine` overflow/boundary tests (`BUG-07`, `BUG-08`), all 4 PDFs (`894` pages, `BUG-09`), `BUG-01`..`BUG-36` regressions | **PASS** |
| 04 | `NumberSystemUnitTest.kt` | JUnit4 | `17` | `17` | **`17`** | Base Converter, Divisibility (`2..19`), Euclidean GCD/LCM, Modular Arithmetic, `MCQ`/`KEYPAD`/`TRUE_FALSE` (`BUG-17`) | **PASS** |
| 05 | `VisualMathExplorerUnitTest.kt` | JUnit4 | `17` | `17` | **`17`** | 14 Interactive Visual Labs, Geometry/Trig/Coordinate/Venn/Chart calculations | **PASS** |
| 06 | `BcsPreparationCenterUnitTest.kt` | Robolectric (`sdk = [36]`) | `12` | `12` | **`12`** | 6 BCS Exam Profiles (`BUG-05`), 1,060+ BCS Questions, 50 Topic Guides, Provenance (`BUG-06`) & Profile Isolation (`BUG-13`) | **PASS** |
| 07 | `HomeNavigationAndOrderingUnitTest.kt` | Robolectric (`sdk = [36]`) | `7` | `7` | **`9`** | 14 Home Modules ordering (`BUG-11`), customization persistence, backstack navigation (`BUG-12`), and Home Screen scroll position & pixel offset restoration (`BUG-36`) | **PASS** |
| 08 | `ExampleUnitTest.kt` | JUnit4 | `1` | `1` | **`1`** | Baseline JUnit sanity check | **PASS** |
| **Total** | **8 Test Suites** | **JVM + Robolectric** | **`165`** | **`172`** | **`175`** | **100% Pass Rate (`0` Failures, `0` Errors, `0` Skipped)** | **PASS** |

---

## 3. Stabilization (`+2`), Adversarial (`+5`) & Scroll Restoration (`+3`) Regression Tests Added

### 3.1 Phase 2 Stabilization Regression Tests (`165 -> 167`)
1. **`auditRegression_bug22_bookPracticeBidirectionalNavigationPreservesStateAndPreventsScoreDoubleCounting`**:
   - Starts a 5-question `BookPractice` session, answers `Q0` correctly (`correctCount = 1, wrongCount = 0`) and `Q1` wrongly (`correctCount = 1, wrongCount = 1`).
   - Navigates back to `Q0` (`previousBookPracticeQuestion()`) and verifies `isAnswerSubmitted == true`, `isCurrentCorrect == true`, and the exact selected option/text input is preserved.
   - Verifies calling `submitBookPracticeAnswer()` again on `Q0` is a no-op (`correctCount = 1, wrongCount = 1`).
   - Navigates forward to `Q1` (`nextBookPracticeQuestion()`) and verifies `Q1` remains submitted with `isCurrentCorrect == false` and unchanged counts (`1` correct, `1` wrong), then advances to unanswered `Q2` and verifies `isAnswerSubmitted == false`.
2. **`auditRegression_bug23_bug25_bug26_streakTrackingKpiDeduplicationAndBookTextEquivalence`**:
   - **`BUG-26`**: Verifies `BookQuestionEngine.isTextEquivalent` accepts `"১/২"` vs `"0.5"`, `"২/৪"` vs `"১/২"`, `"৩ : ৪"` vs `"6 : 8"`, and `"৩৬টি"` vs `"36"`, while rejecting `"১/৩"` vs `"১/২"`.
   - **`BUG-25`**: Verifies `LearningAnalyticsEngine.computeSeparatedKpis` excludes `QuizScoreEntity(topicId = "BOOK_class_6_C1")` from `totalExamsCompleted` (`0` exams, `1` practice session, `10` total questions attempted instead of `20`).
   - **`BUG-23`**: Verifies `NumberSystemPracticeState` increments `currentStreak` (`1 -> 2`), preserves `highestStreak = 2` when `Q2` is answered wrongly (`currentStreak = 0`), and maintains accurate `score = 2`.

### 3.2 Phase 3 Independent Adversarial Bug Hunt Regression Tests (`167 -> 172`)
3. **`adversarialAudit_bug29_dotlessUnitSuffixesAndMixedFractionEquivalence`**:
   - Verifies `SharedMathEngine.isAnswerCorrect` strips dotless textbook unit suffixes (`"২৪ সেমি"`, `"১৫০ বর্গসেমি"`, `"৬৪ ঘনসেমি"`, `"১২ মিটার"`, `"৪৮ বর্গমিটার"`, `"৫ কিমি"`) and equates mixed fractions (`"1 1/2"`, `"১ ১/২"`, `"১ সমস্ত ১/২"`, `"১ পূর্ণ ১/২ সেমি"`, `"2 3/4"`) with their decimal and improper fraction equivalents.
4. **`adversarialAudit_bug30_parenthesizedDistributiveAlgebraicSimplification`**:
   - Verifies `SharedMathEngine.simplify` expands and simplifies distributive parenthesized linear and quadratic expressions (`"2(x + 3) + 4x - 1"` -> `"৬x + ৫"`, `"3(2x - 1) - 2(x - 4)"` -> `"৪x + ৫"`, `"-(x + 5) + 3x"` -> `"২x − ৫"`, `"2(x^2 + 3x - 1) - x^2"` -> `"x² + ৬x − ২"`).
5. **`adversarialAudit_bug31_and_bug32_crossTableMistakeSyncAndResolution`**:
   - Verifies Book mistakes sync across `book_mistakes`, `book_spaced_revision`, and `unified_mistake_notebook` without double-prefixing or leaving orphaned rows on deletion (`BUG-31`), and verifies BCS mistakes canonicalize mode-prefixed IDs (`"bcs_exam_3_bcs_hist_45_arith_99"` vs `"bcs_mistake_retry_1_bcs_hist_45_arith_99"`) and auto-resolve across `bcs_mistake_notebook` and `unified_mistake_notebook` (`BUG-32`).
6. **`adversarialAudit_bug33_and_bug34_unsubmittedDraftPreservationAndSkippedWeakTopics`**:
   - Verifies unsubmitted draft selections in non-`isFullExamMode` quizzes are preserved across `jumpToQuizQuestion()` and `previousQuizQuestion()` and scored on timer expiry (`BUG-33`), and verifies `ExamScoringEngine.evaluateExam()` excludes skipped zero-attempt topics (`attemptedCount == 0`) from `weakTopicTitlesBn` (`BUG-34`).
7. **`adversarialAudit_bug35_pdfRenderCacheAndSynchronizedOfflinePdfExtraction`**:
   - Verifies `BookPdfRenderCache` bounded `LruCache(12)` put/get/evictAll lifecycle and synchronized atomic extraction of bundled NCTB PDFs via `resolveOfflinePdfFile()` (`BUG-35`).

### 3.3 Phase 4 Home Screen Scroll Position Restoration Regression Tests (`172 -> 175`)
8. **`testHomeScreenScrollPositionPreservedAcrossAllModuleNavigationScenarios` (`HomeNavigationAndOrderingUnitTest.kt`)**:
   - Verifies fresh launch starts at `(0, 0)` (Scenario G), scrolls to `(firstVisibleItemIndex = 9, firstVisibleItemScrollOffset = 128)`, navigates to all 14 Home Screen modules (`Tables`, `Primes`, `SquaresCubes`, `Formulas`, `FormulaLibrary`, `PracticeLab`, `MentalMath`, `NumberSystem`, `ProblemSolver`, `VisualExplorer`, `Quizzes`, `Dashboard`, `BcsPreparation`, and `BookHub`), returns via `navigateBack()` (Scenarios A, B, C), and traverses a 5-level deep stack (`Home -> BookHub -> BookDetail -> BookChapterOverview -> BookLesson -> Back to Home`, Scenario D), confirming `(9, 128)` is preserved across every transition.
9. **`testHomeScreenLiveComposeCompositionDisposalAndExactScrollRestoration` (`HomeNavigationAndOrderingUnitTest.kt`)**:
   - Launches `MainActivity` under Robolectric (`sdk = [36]`), verifies `HomeScreen` leaves the live Compose composition tree during module navigation (`VisualExplorer` and `ProblemSolver`) and re-enters composition upon `navigateBack()` with both `firstVisibleItemIndex = 8` and `firstVisibleItemScrollOffset = 116` intact, and verifies normal recompositions (timer ticks, theme switches, Scenario E) do not reset the scroll state.
10. **`bug36_homeScreenScrollPositionAndOffsetRestoredAfterModuleNavigationDisposal` (`EcosystemUpgradeV7UnitTest.kt`)**:
    - Verifies `LazyGridState.Saver` serialization/restoration (used by `SaveableStateHolder` in `MainAppContent`) and `MathViewModel.homeScrollPosition` restoration of both `firstVisibleItemIndex = 8` and `firstVisibleItemScrollOffset = 94` across module disposal and multi-screen BCS exam navigation.

