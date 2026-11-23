# Math Master — Phase 4 Audit Master Report (Goal 4.0)
## Comprehensive Functionality, Concurrency, Mathematical Accuracy, Content & Data Integrity Audit

**Audit Type**: Strictly Read-Only Audit & Planning (Goal 4.0 — Zero Production/Test/Gradle/Asset Modifications)  
**Reference GitHub Baseline**: `https://github.com/rocky-ahmed/Math-Master`, branch `main`, commit `18c7b22558ebfd6eb317768ea9078fca2bc2055c` (`"phase 3 complete"`)  
**Authoritative Execution Workspace**: Google AI Studio Build workspace (`206 / 206` byte-identical parity + `2` untouched local signing artifacts)  
**Fresh Automated Test Baseline**: `gradle :app:testDebugUnitTest --rerun-tasks` → **`340 / 340` passed across `29 / 29` suites** (`0` failures, `0` errors, `0` skipped)  
**Audit Date**: `2026-11-19` (UTC)

---

## 1. Executive Summary

This report documents the independent source-level and diagnostic verification of the Math Master Android application across all **14 Home modules**, **31 navigation routes**, **21 Room entities / 107 DAO methods / 10 `@Transaction` methods**, **4 bundled NCTB textbooks (47 chapters, 894 PDF pages)**, and the **10 prioritized Phase 4 audit leads (`F4-01` through `F4-10`)**.

### Headline Classification Summary

| Finding ID | Title | Classification | Severity | Roadmap WP |
| :--- | :--- | :--- | :-: | :-: |
| **`F4-01`** | Number System Question Progression & Completion Idempotency | `Confirmed source-level defect` | **`P1 — High`** | `WP-4.1` |
| **`F4-02`** | Premature BCS Study Goal Recording on Exam/Practice Launch | `Confirmed source-level defect` | **`P1 — High`** | `WP-4.1` |
| **`F4-03`** | BCS Unified Mistake Key Duplication & Orphaned Unresolved Rows | `Confirmed source-level defect` | **`P1 — High`** | `WP-4.1` |
| **`F4-04`** | Book Practice Double Spaced-Repetition Advancement in `unified_mistake_notebook` | `Confirmed source-level defect` | **`P1 — High`** | `WP-4.1` |
| **`F4-05`** | BCS Concurrent Read-Modify-Write Lost Updates, `deleteExamAttempt` Lock Gap & Split Session-Completion Writes | `Confirmed source-level defect` | **`P1 — High`** | `WP-4.2` |
| **`F4-06`** | Immediate-Feedback Quiz Correct-Answer Double Resolution & Submit→Finish Coroutine Race | `Confirmed source-level defect` | **`P1 — High`** | `WP-4.2` |
| **`F4-07`** | Cold-Launch Streak Fabrication & Dashboard 7-Day Activity Strip Contrast / Time-Only Mismatch | `Confirmed source-level defect` | **`P2 — Medium`** | `WP-4.3` |
| **`F4-08`** | NCTB Page Companion Provenance Misattribution & Gated Visual PDF Boundary Limitation | `Confirmed source-level defect` *(provenance)* / `Requires additional evidence` *(visual PDF alignment: `Unverified`)* | **`P2 — Medium`** | `WP-4.3` |
| **`F4-09`** | Context-Unaware Global Numeric/Boolean Conflation in `SharedMathEngine.isAnswerCorrect` *(+ Solver Substring & Book Tolerance Observations)* | `Confirmed source-level defect` | **`P1 — High`** | `WP-4.4` |
| **`F4-10`** | Bookmark Category Routing, `SquareCubeModels.kt` KDoc Inaccuracies & Historical Documentation Drift | **Mixed**: Bookmark routing = `Not reproducible / disproven` *(for all 8 producible categories)*; KDoc & Doc Drift = `Confirmed source-level defect` | **`P3 — Low`** | `WP-4.4` |

**Totals**: `9` findings contain confirmed source-level defects (`6` `P1 — High`, `2` `P2 — Medium`, `1` `P3 — Low`), `0` `P0 — Critical` crashes/data-loss-on-launch defects, `1` sub-hypothesis disproven (`F4-10` producible bookmark routing), and `1` visual verification scope explicitly retained as `Unverified` (`F4-08` PDF page-image visual inspection due to workspace-sandboxed `view_file`).

**Frozen Study Timer**: Verified strictly untouched and passing (`14 / 14` tests across `StudyTimerCompactLayoutRegressionTest` and `StudyTimerCrashSafePersistenceUnitTest`). Excluded from all remediation recommendations.

---

## 2. Verified Workspace Configuration & GitHub Parity

| Attribute | Verified Value | Anchor |
| :--- | :--- | :--- |
| **GitHub Parity** | `206 / 206` byte-identical blobs vs. `18c7b22558ebfd6eb317768ea9078fca2bc2055c` (`0` modified, `0` missing, `2` local signing artifacts) | `PHASE_4_AUDIT_EVIDENCE.md §1` |
| **Application ID / Namespace** | `com.aistudio.applet.ghqqzu` / `com.example` | `app/build.gradle.kts:12,16` |
| **Version** | `versionCode = 2`, `versionName = "2.0"` | `app/build.gradle.kts:19–20` |
| **SDK / Build Toolchain** | `minSdk 24`, `targetSdk 36`, `compileSdk 36.1`, AGP `9.1.1`, Kotlin `2.2.10`, KSP `2.3.5`, Room `2.7.0`, Compose BOM `2024.09.00` | `app/build.gradle.kts`, `gradle/libs.versions.toml` |
| **Database** | Room `v10` (`math_master_db`), `21` entities, `9` migrations (`1→2`..`9→10`), `107` DAO methods, `10` `@Transaction` methods | `MathDatabase.kt`, `Entities.kt`, `MathDao.kt` |
| **Offline / Permissions** | `0` `<uses-permission>` entries; zero cloud/network runtime libraries | `AndroidManifest.xml`, `app/build.gradle.kts` |

---

## 3. Historical Documentation Reuse & Drift Triage Summary

All 59 Markdown documentation files (`README.md`, `MATH_MASTER_*`, and `docs/**`) were inventoried to avoid redundant re-audits while flagging stale metadata (full matrix in `PHASE_4_AUDIT_EVIDENCE.md §8`):

- **Reusable Still-Valid Baselines**:
  - `docs/audits/FINAL_MATHEMATICAL_ACCURACY_REPORT.md` & `docs/audits/MATHEMATICAL_ENGINE_VALIDATION_REPORT.md` — Core deterministic math engine formulas and generators remain valid; fresh Goal 4.0 verification concentrated on answer-grading semantics (`F4-09`), cross-module call sites, and edge cases.
  - `docs/book/BOOK_CONTENT_MANIFEST.md` & `docs/book/BOOK_NCTB_SOURCE_AND_CONTENT_AUDIT.md` — Accurate 47-chapter structural baseline; Goal 4.0 audited live `getStructuredPageContent()` provenance (`F4-08`) and verified PDF `/Root → /Pages → /Kids → /XObject` stream integrity across all `894` pages.
  - `docs/audits/FINAL_NAVIGATION_STATE_RESTORATION_AUDIT.md` & `docs/audits/OFFLINE_DEPENDENCY_AUDIT.md` — Verified still accurate (`0` permissions, offline posture intact).
- **Verified Stale / Drifted Documents (Cataloged under `F4-10`; NOT edited in Goal 4.0)**:
  - `README.md`: Line 8 cites `245 / 245` across `13` suites; line 52 cites `215 / 215` across `10` suites (both stale and mutually contradictory; actual: `340 / 340` across `29` suites).
  - `docs/qa/FINAL_REGRESSION_TEST_REPORT.md`: Cites `215 / 215` across `10` suites.
  - `docs/database/FINAL_DATABASE_INTEGRITY_REPORT.md`: Cites Room `v9` and `20` entities (actual: Room `v10`, `21` entities, `9` migrations).
  - `docs/qa/FINAL_DEVICE_QA_REPORT.md`: Dated `2026-11-12` (`PENDING PHYSICAL HARDWARE` as of that date; superseded in practice by later user-confirmed Phase 3 physical device QA).

---

## 4. 14-Module & 31-Route Functional Triage Summary

All 14 Home modules (`HomeScreen.kt`) and 31 `Screen` routes (`MathViewModel.kt`) were triaged against source code and the 29 passing unit test suites:

| # | Home Module | Primary Route(s) | Functional Audit Outcome | Linked Findings |
| :-: | :--- | :--- | :--- | :--- |
| 01 | **Multiplication Tables** | `Tables` | Core table/practice generation clean; bookmark category `TABLE` routes properly. | — |
| 02 | **Practice Lab** | `PracticeLabSetup`, `PracticeLabPlay` | Phase 3 CAS completion guard present; uses `SharedMathEngine.isAnswerCorrect` (`F4-09`); split `recordQuizScore` + `recordSessionAndAnalytics` (`F4-05`). | `F4-05`, `F4-09` |
| 03 | **Mental Math Challenge** | `MentalMathSetup`, `MentalMathPlay` | Phase 3 CAS guard present; typed validation uses strict `toIntOrNull()` (immune to `F4-09`); split `recordQuizScore` + `recordSessionAndAnalytics` (`F4-05`). | `F4-05` |
| 04 | **Square & Cube** | `SquareCube` | Math values (`1..20` squares, `1..10`/`1..20` cubes) accurate; `SquareCubeModels.kt` KDoc has range/translation errors (`F4-10`). | `F4-10` |
| 05 | **Prime Numbers** | `PrimeNumbers` | Sieve/factorization/primality verified; bookmark category `PRIME` routes properly. | — |
| 06 | **Number System** | `NumberSystem` (+ practice play) | **Missing submission guard & CAS completion guard** in `nextNumberSystemQuestion()` (`F4-01`); split completion writes (`F4-05`); uses `SharedMathEngine.isAnswerCorrect` (`F4-09`). | `F4-01`, `F4-05`, `F4-09` |
| 07 | **Formula Library** | `FormulaLibrary` | Browse, search, mastery tracking (`recordFormulaMastery` two-table sequential write `F4-05`); bookmarks `FORMULA` & `FORMULA_LIBRARY` route properly. | `F4-05` |
| 08 | **Math Problem Solver** | `ProblemSolver` | Phase 3 solver cancellation/generation guard passes (`Phase3ConcurrencyAndSolverCancellationTest`); minor substring match looseness in interactive pedagogy step check (`F4-09` secondary). | `F4-09` (minor) |
| 09 | **Quiz & Exam Center** | `QuizSetup`, `QuizPlay`, `ExamCenter` | Phase 3 `finalizeExamAttemptTransaction` & CAS guard present; **immediate-feedback correct answers double-resolved + Submit→Finish race (`F4-06`)**; `deleteExamAttempt` bypasses `dailyStudyWriteMutex` (`F4-05`); uses `SharedMathEngine.isAnswerCorrect` (`F4-09`). | `F4-05`, `F4-06`, `F4-09` |
| 10 | **Visual Math Explorer** | `VisualMathExplorer` (+ 2D/3D canvases) | 36/36 `VisualMathExplorerUnitTest` passing; GL lifecycle cleanup verified in Phase 3. | — |
| 11 | **Dashboard & Analytics** | `Dashboard` | Room-backed `LearningAnalyticsEngine` returns accurate `0` on empty history, but `MathViewModel.initStreakAndActivity` fabricates `1` on cold launch and `DashboardScreen.kt:419–432` has 7-day circle contrast/time-only mismatch (`F4-07`). | `F4-07` |
| 12 | **Bookmarks & Smart Tutor** | `Bookmarks`, `SmartTutor` | All 8 producible bookmark categories route cleanly (`F4-10` routing disproven); Smart Tutor & unified mistake counts impacted by `F4-03` orphaned BCS rows. | `F4-03`, `F4-10` |
| 13 | **BCS Math Preparation Center** | `BcsPrepHome`, `BcsTopicGuide`, `BcsPracticeSetup`, `BcsMistakeNotebook`, `BcsStudyPlanner` | **Premature study-goal crediting at launch (`F4-02`)**; **dual unified mistake keys + orphaned rows (`F4-03`)**; **concurrent RMW lost updates in `recordBcsStudyGoalDelta` & `recordBcsTopicProgress` (`F4-05`)**. | `F4-02`, `F4-03`, `F4-05` |
| 14 | **NCTB Textbooks (Class 6–10)** | `BookHub`, `BookDetail`, `BookChapterDetail`, `BookReader`, `BookPractice`, `BookInteractiveLab`, `BookSearch`, `BookMistakes` | **Double `unified_mistake_notebook` advancement on Book Practice retry (`F4-04`)**; **unattributed formula/example rotation in page companion (`F4-08`)**; split `finishBookPracticeSession` writes (`F4-05`); `BookQuestionEngine` `0.01` tolerance & `SharedMathEngine` fallback (`F4-09`). | `F4-04`, `F4-05`, `F4-08`, `F4-09` |
| — | **Home & Frozen Study Timer** | `Home`, `Settings`, `Appearance` | Study Timer strictly frozen and passing (`14/14`); Home navigation & ordering passing (`14/14`). | Frozen (exempt) |

---

## 5. NCTB 47-Chapter & Gated PDF Verification Summary

- **47-Chapter Curriculum Inventory**: All 47 chapters (`Class 6`: 8, `Class 7`: 11, `Class 8`: 11, `Class 9–10`: 17) are structurally present with non-empty titles, page ranges, formulas, lessons, and interactive lab links (complete 47-row table in `PHASE_4_AUDIT_EVIDENCE.md §6`).
- **Structural PDF Verification (`Verified`)**: Direct `/Root → /Pages → /Kids → <Page>` and `/Resources /XObject` parsing across all 4 bundled PDFs (`class_6.pdf`: 161, `class_7.pdf`: 181, `class_8.pdf`: 190, `class_9_10.pdf`: 362; total **`894`** pages) verified:
  - **`894 / 894`** reachable pages map **1:1** to a single `/Subtype /Image` XObject (`/Im1`).
  - **`894 / 894`** `/FlateDecode` `/DeviceRGB` 8-bit image streams decompress cleanly using their exact PDF `/Length` header with exact `Width × Height × 3` RGB byte counts (`0` corrupt streams).
  - **`0 / 894`** pages declare `/Font` resources — all 4 PDFs are pure scanned raster images with **no embedded text layer**.
- **Gated Visual Preflight & Deterministic 42-Page Boundary Sampling (`Unverified`)**:
  - Mandatory 1-page preflight (`class_6.pdf`, PDF page `6`, printed page `1`, `927×1211`) decompressed cleanly to `/tmp/nctb_preflight/class_6_pdfpage_6.png` (`49,371` bytes; cleaned up immediately).
  - Calling `view_file` on `/tmp/nctb_preflight/class_6_pdfpage_6.png` failed because `view_file` is strictly sandboxed to workspace root (`/app/applet/`), and writing temporary PNG files into the workspace is prohibited under read-only audit rules.
  - Accordingly, per Plan Section 7, visual inspection across all **42 deterministic boundary sample pages** retains explicit **`Unverified`** status. Full printed-wording and page-offset (`pdfPageOffset = 5`) visual confirmation requires physical-device or external PDF inspection (detailed in `PHASE_4_IMPLEMENTATION_ROADMAP.md` and `PHASE_4_AUDIT_EVIDENCE.md §7`).

---

## 6. Classified Finding Register (12 Mandatory Fields per Finding)

---

### Finding `F4-01` — Number System Question Progression & Completion Idempotency

1. **Unique ID**: `F4-01`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — direct code inspection & contrast with sibling CAS guards)
3. **Severity**: `P1 — High`
4. **Actual Source Path & Line Range**:
   - `app/src/main/java/com/example/ui/MathViewModel.kt:3321–3369` (`nextNumberSystemQuestion()`)
   - `app/src/main/java/com/example/ui/screens/NumberSystemPracticePlayScreen.kt:434–449` (Next / Finish CTA button)
5. **Complete Caller Chain**:
   `NumberSystemPracticePlayScreen` (`Button(onClick = { viewModel.nextNumberSystemQuestion() })` at `:434–449`) → `MathViewModel.nextNumberSystemQuestion()` (`:3321–3369`) → `MathRepository.recordQuizScore()` + `MathRepository.recordSessionAndAnalytics()` (`MathDao.recordSessionAndAnalyticsTransaction`) + `MathViewModel.recordDailyActivity(3)` → Room (`quiz_scores`, `practice_history`, `topic_analytics`, `daily_study_activity`) + `SharedPreferences`.
6. **Expected vs. Actual Behavior**:
   - **Expected**: (a) Advancing to the next question must require `current.isSubmitted == true` and `!current.isCompleted`; (b) completing the final question must atomically claim completion via a `compareAndSet` loop (matching Quiz, PracticeLab, MentalMath, and BookPractice) **before** launching persistence so rapid double-taps or concurrent calls persist exactly once.
   - **Actual**: (a) `nextNumberSystemQuestion()` never checks `if (!current.isSubmitted || current.isCompleted) return`, allowing direct callers or rapid taps to skip unanswered questions; (b) on the final question (`else` branch at `:3330–3368`), it reads `val current = _numberSystemPracticeState.value`, launches `viewModelScope.launch { recordQuizScore(...); recordSessionAndAnalytics(...); recordDailyActivity(3) }` **before** assigning `_numberSystemPracticeState.value = current.copy(isCompleted = true)` (`:3366`), and has **no `compareAndSet` ownership guard**. Rapid double-taps or concurrent invocations launch multiple persistence coroutines, writing duplicate `QuizScoreEntity`, `PracticeHistoryEntity`, `TopicAnalyticsEntity`, and `DailyStudyActivityEntity` increments.
7. **Evidence & Reproduction / Disproving Status**:
   - Verified directly in `MathViewModel.kt:3321–3369`. Contrast with `nextBookPracticeQuestion()` (`MathViewModel.kt:4532–4534`, which checks `if (!current.isAnswerSubmitted) return`) and `finishBookPracticeSession()` / `finishPracticeSessionNow()` / `finishMentalMathSession()` / `finishQuizSession()`, all of which use `compareAndSet` loops.
8. **Existing Regression Coverage**:
   - `NumberSystemUnitTest` (17 tests) covers mathematical conversions/operations only.
   - `Phase3ConcurrencyAndSolverCancellationTest` (`:122–182`) covers CAS guards for Quiz, PracticeLab, MentalMath, and BookPractice, **explicitly omitting Number System**.
9. **Missing Regression Coverage**:
   - Unit/Robolectric tests asserting: (a) `nextNumberSystemQuestion()` is a no-op when `isSubmitted == false` or `isCompleted == true`; (b) rapid double/concurrent calls to `nextNumberSystemQuestion()` on the final question insert exactly 1 `QuizScoreEntity`, 1 `PracticeHistoryEntity`, and 1 `DailyStudyActivityEntity` increment.
10. **User Impact**:
    - Double-tapping "ফলাফল দেখুন" / "পরবর্তী" on the last Number System question inflates Dashboard question totals, study time, quiz scores, and topic analytics, and permits skipping unanswered questions.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Add `if (!current.isSubmitted || current.isCompleted) return` at the top of `nextNumberSystemQuestion()`, and transition the final-question completion to an atomic `while (true) { ... compareAndSet(...) }` loop setting `isCompleted = true` **before** launching persistence (identical to existing Phase 3 CAS patterns).
    - **Data Safety**: Zero schema change; zero mutation of historical rows.
12. **Unresolved Uncertainty**:
    - None. Defect is fully proven at source level.

---

### Finding `F4-02` — Premature BCS Study Goal Recording on Exam/Practice Launch

1. **Unique ID**: `F4-02`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — direct caller-chain verification)
3. **Severity**: `P1 — High`
4. **Actual Source Path & Line Range**:
   - `app/src/main/java/com/example/ui/MathViewModel.kt:3896–3983` (`startBcsModeExam()`, premature call at `:3974–3981`)
   - `app/src/main/java/com/example/ui/MathViewModel.kt:3985–4061` (`startBcsMockExam()`, premature call at `:4052–4059`)
   - `app/src/main/java/com/example/ui/MathViewModel.kt:1674–1914` (`finishQuizSession()` — never calls `recordBcsStudyGoalDelta`)
   - `app/src/main/java/com/example/data/repository/MathRepository.kt:922–958` (`recordBcsStudyGoalDelta()`)
   - `app/src/main/java/com/example/data/model/Entities.kt:418–432` (`BcsStudyPlanGoalEntity`)
5. **Complete Caller Chain**:
   `BcsPrepHome` / `BcsPracticeSetup` / `BcsMistakeNotebook` → `MathViewModel.startBcsModeExam()` (`:3974–3981`) OR `MathViewModel.startBcsMockExam()` (`:4052–4059`) → `MathRepository.recordBcsStudyGoalDelta(examProfileId, mockExamsDelta = 1, questionsDelta = questions.size, studyTimeMinutesDelta = (durationMinutes / 2).coerceAtLeast(5))` → `MathDao.getBcsStudyGoalByKey` / `upsertBcsStudyGoal` → Room (`bcs_study_plan_goals`).
   *(Other verified callers: `completeBcsTopicGuide` `:3799` `[lessonsDelta=1, studyTimeMinutesDelta=5]`, `recordBcsMiniCheckResult` `:3821` `[questionsDelta=1, studyTimeMinutesDelta=2]`, `recordBcsMistake` `:3867` `[questionsDelta=1, studyTimeMinutesDelta=1]`, `markBcsMistakeResolved` `:3879` `[revisionsDelta=1, questionsDelta=1, studyTimeMinutesDelta=2]`.)*
6. **Expected vs. Actual Behavior**:
   - **Expected**: BCS daily study goals (`mockExamsCompleted`, `questionsSolved`, `studyTimeMinutes`) should update **only upon actual session completion** (or actual answered work), crediting actual answered questions (`correctCount + wrongCount`), real elapsed study minutes, and `mockExamsDelta = 1` **only** when a timed Mock Exam preset is actually finished — never when starting a session, never when immediately exiting/abandoning, and never counting untimed `TOPIC_PRACTICE` or `MISTAKE_PRACTICE` as a completed Mock Exam.
   - **Actual**: Immediately on starting ANY BCS exam or practice session, both `startBcsModeExam()` (`:3974–3981`) and `startBcsMockExam()` (`:4052–4059`) unconditionally credit `mockExamsDelta = 1`, `questionsDelta = questions.size` (all generated questions regardless of answers), and `studyTimeMinutesDelta = (durationMinutes / 2).coerceAtLeast(5)`. Opening and immediately pressing Back increments daily BCS mock exams by 1, solved questions by 15–50, and study minutes by 5–30. Conversely, `finishQuizSession()` never calls `recordBcsStudyGoalDelta()`.
7. **Evidence & Reproduction / Disproving Status**:
   - Verified directly at `MathViewModel.kt:3974–3981`, `:4052–4059`, and `:1674–1914`.
8. **Existing Regression Coverage**:
   - `BcsPreparationCenterUnitTest` (12 tests) validates BCS models/questions/profile logic but does not assert that launching and abandoning a BCS exam leaves `BcsStudyPlanGoalEntity` unchanged.
9. **Missing Regression Coverage**:
   - Tests asserting: (a) launching & abandoning a BCS Mock/Topic/Mistake session leaves `bcs_study_plan_goals` unchanged (`0` deltas); (b) completing a BCS Mock Exam credits `mockExamsDelta = 1`, actual answered count, and real elapsed minutes; (c) completing untimed `TOPIC_PRACTICE` or `MISTAKE_PRACTICE` credits actual answered count (and `revisionsDelta` where applicable) with `mockExamsDelta = 0`.
10. **User Impact**:
    - BCS Study Planner daily progress rings and completion targets are falsely completed merely by opening exam screens, undermining study accountability and misleading learners preparing for BCS exams.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Remove the start-time `repository.recordBcsStudyGoalDelta(...)` calls from `startBcsModeExam()` and `startBcsMockExam()`.
    - Inside `finishQuizSession()` (after CAS completion ownership is won for BCS sessions), compute `attemptedCount = correctCount + wrongCount`; if `attemptedCount > 0`, call `recordBcsStudyGoalDelta(...)` with `questionsDelta = attemptedCount`, `studyTimeMinutesDelta = (elapsedSeconds / 60).coerceAtLeast(1)`, and `mockExamsDelta = if (isMockExamPreset) 1 else 0`.
    - **Data Safety**: Zero schema migration; existing historical `bcs_study_plan_goals` rows are preserved untouched.
12. **Unresolved Uncertainty**:
    - Confirm whether `recordBcsMistake` (`:3867`) should continue adding `questionsDelta = 1` if `finishQuizSession()` also records `attemptedCount` for BCS sessions — ensure no double-counting of wrong answers between `submitQuizAnswer` / `finishQuizSession` and `recordBcsMistake`.

---

### Finding `F4-03` — BCS Unified Mistake Key Duplication & Orphaned Unresolved Rows

1. **Unique ID**: `F4-03`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — verified across both immediate-feedback and full-exam paths and existing test assertions)
3. **Severity**: `P1 — High`
4. **Actual Source Path & Line Range**:
   - `app/src/main/java/com/example/ui/MathViewModel.kt:1505–1546` (`submitQuizAnswer()`)
   - `app/src/main/java/com/example/ui/MathViewModel.kt:1851–1902` (`finishQuizSession()`)
   - `app/src/main/java/com/example/data/db/MathDao.kt:698–796` (`recordBcsMistakeTransaction`, `markBcsMistakeResolvedTransaction`)
   - `app/src/main/java/com/example/data/db/MathDao.kt:1036–1144` (`finalizeExamAttemptTransaction`)
   - `app/src/test/java/com/example/Phase3DatabaseTransactionAndHistoryTest.kt:357–358`
5. **Complete Caller Chain**:
   - *Immediate-feedback wrong BCS answer*: `QuizPlay` → `MathViewModel.submitQuizAnswer()` (`:1513–1536`) → calls `repository.recordUnifiedMistake(id = "QUIZ_${q.topicTitleBn}_${abs(q.questionBn.hashCode())}", sourceModule = "BCS")` **AND** `repository.recordBcsMistake(...)` → `MathDao.recordBcsMistakeTransaction()` (`:698–764`, which ALSO inserts `"BCS_${safeProfile}_$rawQId"` into `unified_mistake_notebook`).
   - *Full-exam wrong BCS answer*: `QuizPlay` → `MathViewModel.finishQuizSession()` (`:1851–1883`) → creates `unifiedMistakePayloads` with `"QUIZ_${q.topicTitleBn}_${abs(q.questionBn.hashCode())}"` **AND** `bcsMistakePayloads` → `MathDao.finalizeExamAttemptTransaction()` (`:1036–1102`) inserts **BOTH** `"QUIZ_..."` AND `"BCS_${safeProfile}_$rawQId"` into `unified_mistake_notebook`.
   - *Resolution from BCS Mistake Notebook*: `BcsMistakeNotebookScreen` → `MathViewModel.markBcsMistakeResolved()` → `MathRepository.markBcsMistakeResolved()` → `MathDao.markBcsMistakeResolvedTransaction()` (`:794–795`) resolves **ONLY** `"BCS_${safeProfile}_$rawQId"` and `"BCS_$rawQId"` — leaving `"QUIZ_..."` permanently `resolved = false`.
   - *Resolution from Dashboard*: `DashboardScreen.kt:679` resolves `"QUIZ_..."` in `unified_mistake_notebook` only, leaving `bcs_mistakes` and `"BCS_${safeProfile}_$rawQId"` unresolved.
6. **Expected vs. Actual Behavior**:
   - **Expected**: Each wrong BCS question should correspond to **one** canonical profile-scoped row in `unified_mistake_notebook` (`"BCS_${safeProfile}_$rawQId"`), staying synchronized with `bcs_mistakes` regardless of whether resolved from BCS Mistake Notebook, Quiz retry, or Dashboard.
   - **Actual**: Every wrong BCS answer creates **two** rows in `unified_mistake_notebook` (`"QUIZ_..."` and `"BCS_${safeProfile}_$rawQId"`). Dashboard unified mistake counts and Smart Tutor weak-topic lists double-count every BCS mistake, and resolving the mistake in BCS Mistake Notebook leaves the `"QUIZ_..."` twin permanently orphaned and unresolved.
7. **Evidence & Reproduction / Disproving Status**:
   - Directly verified in `MathViewModel.kt:1513–1536`, `:1851–1883`, and `MathDao.kt:735–764,794–795,1036–1102`.
   - Notably, `Phase3DatabaseTransactionAndHistoryTest.kt:357–358` currently asserts `assertEquals(3, dao.getAllUnifiedMistakes().first().size)` for 1 non-BCS + 1 BCS wrong question — explicitly confirming the duplicate insertion behavior in the existing test suite!
8. **Existing Regression Coverage**:
   - `Phase3DatabaseTransactionAndHistoryTest.kt:357–358` tests transaction persistence but currently locks in the duplicated 3-row count instead of canonical single-key behavior.
9. **Missing Regression Coverage**:
   - Tests asserting: (a) a single wrong BCS question (in both immediate-feedback and full-exam modes) writes exactly **1** `UnifiedMistakeEntity` (`"BCS_${safeProfile}_$rawQId"`) and **1** `BcsMistakeEntity`; (b) resolving from BCS Mistake Notebook leaves `0` unresolved unified rows for that question; (c) resolving a pre-existing legacy `"QUIZ_..."` + `"BCS_..."` pair non-destructively reconciles both without deleting history.
10. **User Impact**:
    - Every BCS mistake appears twice in Dashboard / Smart Tutor unified mistake counts, and resolving mistakes in the BCS Mistake Notebook fails to clear them from Dashboard/Smart Tutor due-review queues.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - For BCS questions (`isBcsSession`), omit the duplicate `"QUIZ_..."` `UnifiedMistakeEntity` write in both `submitQuizAnswer()` and `finishQuizSession()`, relying solely on the canonical profile-scoped `"BCS_${safeProfile}_$rawQId"` key managed inside `recordBcsMistakeTransaction()` / `finalizeExamAttemptTransaction()` (and resolve canonical `"BCS_${safeProfile}_$rawQId"` on correct answers).
    - **Non-Destructive Legacy Reconciliation**: Never delete existing user rows. When resolving a BCS mistake in `markBcsMistakeResolvedTransaction()`, also mark any legacy `"QUIZ_..."` row with matching `sourceModule = "BCS"` and identical `questionPromptBn` as `resolved = true` (and when resolving a `"BCS_..."` row from Dashboard, synchronize `bcs_mistakes`). Update `Phase3DatabaseTransactionAndHistoryTest.kt:357–358` to expect canonical count `2` instead of `3`.
12. **Unresolved Uncertainty**:
    - None. Both immediate-feedback and full-exam paths and existing test impacts are fully traced.

---

### Finding `F4-04` — Book Practice Double Spaced-Repetition Advancement

1. **Unique ID**: `F4-04`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — verified on both independent counters)
3. **Severity**: `P1 — High`
4. **Actual Source Path & Line Range**:
   - `app/src/main/java/com/example/ui/MathViewModel.kt:4524–4528` (`submitBookPracticeAnswer()`)
   - `app/src/main/java/com/example/data/repository/MathRepository.kt:724–734` (`markBookMistakeResolved()`)
   - `app/src/main/java/com/example/data/repository/MathRepository.kt:1008–1032` (`resolveUnifiedMistake()`)
   - `app/src/main/java/com/example/data/db/MathDao.kt:640–684` (`markBookMistakeResolvedTransaction()`)
5. **Complete Caller Chain**:
   `BookPracticeScreen` → `MathViewModel.submitBookPracticeAnswer()` (`:4524–4528`) →
   1. `repository.markBookMistakeResolved("book_mistake_${winningQ.id}", resolved = true)` → `MathDao.markBookMistakeResolvedTransaction()` (`:640–684`):
      - Updates **`book_spaced_revision` (`BookSpacedRevisionEntity`)** `intervalStage` (`+1`, max `4`) & `nextReviewEpochMs` **ONCE** (`:661–671`).
      - Calls `resolveUnifiedMistakeInsideTransaction("BOOK_$rawId", nowMs)` (`:672`) → advances **`unified_mistake_notebook` (`UnifiedMistakeEntity`)** `reviewIntervalDays` (`1 → 3`) and `nextReviewAtMillis` **ONCE**.
   2. Immediately afterward (`MathViewModel.kt:4527`), calls `repository.resolveUnifiedMistake("BOOK_${winningQ.id}")` (`MathRepository.kt:1008–1032`) → reads the already-updated row and advances **`unified_mistake_notebook` (`UnifiedMistakeEntity`)** `reviewIntervalDays` a **SECOND TIME** (`3 → 7`, or `3 → 7 → 14`) and pushes `nextReviewAtMillis` out by the skipped interval! *(Secondary observation: `MathRepository.kt:1029` also overwrites `lastFailedAtMillis = nowMs` during resolution.)*
6. **Expected vs. Actual Behavior**:
   - **Expected**: A single successful retry of a previously missed Book Practice question should advance `book_spaced_revision.intervalStage` by `+1` **once** and advance `unified_mistake_notebook.reviewIntervalDays` by **one** step (`1 → 3`) **once**.
   - **Actual**: `book_spaced_revision.intervalStage` advances correctly **once**, **but `unified_mistake_notebook.reviewIntervalDays` advances TWICE** in a single correct retry (`1 → 3 → 7` days, completely skipping the 3-day review window in Dashboard/Smart Tutor spaced repetition).
7. **Evidence & Reproduction / Disproving Status**:
   - Directly verified across `MathViewModel.kt:4526–4527`, `MathDao.kt:661–673`, and `MathRepository.kt:1008–1032`. Notice `rawId` normalization at `MathDao.kt:647` strips `"book_mistake_"` so `"BOOK_$rawId"` equals `"BOOK_${winningQ.id}"` identically.
8. **Existing Regression Coverage**:
   - `Phase3DatabaseTransactionAndHistoryTest` verifies `markBookMistakeResolvedTransaction()` in isolation (which advances once), so it did not catch the ViewModel's duplicate second call at `MathViewModel.kt:4527`.
9. **Missing Regression Coverage**:
   - ViewModel-level test calling `submitBookPracticeAnswer()` once with a correct answer for a previously recorded Book Practice mistake and asserting `book_spaced_revision.intervalStage == 1` (single step) **AND** `unified_mistake_notebook.reviewIntervalDays == 3` (single step, NOT `7`).
10. **User Impact**:
    - Students reviewing Book Practice mistakes skip the 3-day spaced-repetition stage in Unified Mistake Notebook / Smart Tutor and are prematurely pushed to 7-day or 14-day review intervals, weakening memory retention.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Remove the redundant `repository.resolveUnifiedMistake("BOOK_${winningQ.id}")` call at `MathViewModel.kt:4527` when `markBookMistakeResolvedTransaction()` already resolves `"BOOK_$rawId"` atomically (confirming `markBookMistakeResolvedTransaction()` still calls `resolveUnifiedMistakeInsideTransaction("BOOK_$rawId", nowMs)` even if `book_mistakes` has no matching row, or only calling `resolveUnifiedMistake` if no `book_mistakes` row existed).
    - Also review `MathRepository.kt:1029` so resolving a mistake does not overwrite `lastFailedAtMillis`.
    - **Data Safety**: Zero schema change; existing revision schedules remain intact.
12. **Unresolved Uncertainty**:
    - None. Exact dual-counter behavior is proven line by line.

---

### Finding `F4-05` — BCS Concurrent Read-Modify-Write Lost Updates, `deleteExamAttempt` Lock Gap & Split Session-Completion Writes

1. **Unique ID**: `F4-05`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — direct lock/transaction/RMW analysis across `MathRepository.kt`, `MathDao.kt`, and `MathViewModel.kt`)
3. **Severity**: `P1 — High` *(P1 for BCS concurrent RMW lost updates & `deleteExamAttempt` race; P2 for split session-completion writes; P3 for low-frequency two-table metadata writes)*
4. **Actual Source Path & Line Range**:
   - **Sub-defect 5A (BCS Concurrent RMW Lost Updates)**:
     - `app/src/main/java/com/example/data/repository/MathRepository.kt:922–958` (`recordBcsStudyGoalDelta()`)
     - `app/src/main/java/com/example/data/repository/MathRepository.kt:805–869` (`recordBcsTopicProgress()`)
     - `app/src/main/java/com/example/data/db/MathDao.kt:926–998` (competing `bcs_topic_progress` update inside `finalizeExamAttemptTransaction`)
   - **Sub-defect 5B (`deleteExamAttempt` vs. `dailyStudyWriteMutex` Race)**:
     - `app/src/main/java/com/example/data/repository/MathRepository.kt:305–310` (`deleteExamAttempt()`) vs. `:411–496` (`recordDailyStudyDelta()`, `updateDailyStudyTargets()`)
   - **Sub-defect 5C (Split Non-Atomic Session-Completion Persistence)**:
     - `app/src/main/java/com/example/ui/MathViewModel.kt:2483–2508` (`finishPracticeSessionNow`), `:2853–2878` (`finishMentalMathSession`), `:3338–3362` (`nextNumberSystemQuestion`), `:4593–4617` (`finishBookPracticeSession`)
   - **Sub-defect 5D (Remaining Sequential Two-Table Writes — Lower Risk, P2/P3)**:
     - `MathRepository.kt:617–665` (`toggleBookPageBookmark` — `book_reading_state` + `bookmarks`), `:510–565` (`upsertBookLessonProgress` / `toggleBookLessonCompleted` — `book_lesson_progress` + `user_progress`), `:1051–1075` (`recordFormulaMastery` — `formula_mastery_state` + `user_progress`), `:796–803` (`saveBcsUserProfile` — two `bcs_user_profile` rows).
5. **Complete Caller Chain**:
   - **5A**: `MathViewModel` (`completeBcsTopicGuide`, `recordBcsMiniCheckResult`, `recordBcsMistake`, `markBcsMistakeResolved`, `finishQuizSession`) → `MathRepository.recordBcsStudyGoalDelta()` (`:922–958`) / `MathRepository.recordBcsTopicProgress()` (`:805–869`) → unlocked `dao.get*` → Kotlin arithmetic → `dao.upsert*` → Room (`bcs_study_plan_goals`, `bcs_topic_progress`).
   - **5B**: `ExamCenterScreen` / `DashboardScreen` delete action → `MathViewModel.deleteExamAttempt()` → `MathRepository.deleteExamAttempt()` (`:305–310`, **NO `dailyStudyWriteMutex`**) → `MathDao.deleteExamAttemptSafely()` (`:191–265`, `@Transaction` mutating `daily_study_activity`) racing against `recordDailyStudyDelta()` / `updateDailyStudyTargets()` (`:411–496`, holding `dailyStudyWriteMutex` with non-transactional `get` + `upsert`).
   - **5C**: PracticeLab / MentalMath / NumberSystem / BookPractice completion → `MathViewModel` launches coroutine → separate `repository.recordQuizScore(...)` / `recordPractice(...)` → separate `repository.recordSessionAndAnalytics(...)` (`MathDao.recordSessionAndAnalyticsTransaction`).
6. **Expected vs. Actual Behavior**:
   - **Expected**:
     - Concurrent deltas to the same `goalKey` (`recordBcsStudyGoalDelta`) or profile-scoped `topicId` (`recordBcsTopicProgress`) — including accumulation onto pre-existing seeded records and parallel execution with `finalizeExamAttemptTransaction` — must preserve **every accepted delta exactly once** with strict profile isolation.
     - Deleting an exam attempt must serialize cleanly with all other `daily_study_activity` read-modify-write paths.
     - Logical session completion (`quiz_scores` + `practice_history` + `topic_analytics` + `daily_study_activity`) should commit atomically without partial-write drift on cancellation/crash.
   - **Actual**:
     - **5A (`recordBcsStudyGoalDelta` & `recordBcsTopicProgress`)**: Both perform unlocked Kotlin-memory Read-Modify-Write (`suspend get` → compute → `suspend upsert`) with **no mutex, no `@Transaction`, and no atomic SQL increment**. Under parallel coroutines (e.g., rapid mini-check + guide completion + mistake resolution, or concurrent exam finalization + topic guide update), coroutine B reads the same pre-update row as coroutine A before A's upsert commits, causing **lost updates** on `questionsSolved`, `studyTimeMinutes`, `lessonsCompleted`, `revisionsCompleted`, `attemptedQuestions`, `correctQuestions`, `totalTimeSeconds`, `isGuideCompleted`, and `miniCheckPassed`.
     - **5B (`deleteExamAttempt`)**: Invokes `dao.deleteExamAttemptSafely()` **outside `dailyStudyWriteMutex`**, allowing it to interleave between `getDailyStudyActivityByDate()` and `upsertDailyStudyActivity()` inside `recordDailyStudyDelta()` or `updateDailyStudyTargets()`, overwriting decremented exam totals or dropping study deltas.
     - **5C (Split Session Completion)**: `recordQuizScore()` executes in a separate Room transaction before `recordSessionAndAnalyticsTransaction()`; cancellation or error between them leaves orphaned `quiz_scores` without matching analytics/history.
7. **Evidence & Reproduction / Disproving Status**:
   - Verified directly in `MathRepository.kt:305–310`, `:411–496`, `:510–565`, `:617–665`, `:796–869`, `:922–958`, `:1051–1075`, `MathDao.kt:191–265,492–566,926–998`, and `MathViewModel.kt:2483–2508,2853–2878,3338–3362,4593–4617`.
   - Note: `recordDailyStudyDelta()` and `updateDailyStudyTargets()` already use `dailyStudyWriteMutex`; `applyStudyTimerFlushOperation()` (frozen) also acquires `dailyStudyWriteMutex`.
8. **Existing Regression Coverage**:
   - `Phase3DatabaseTransactionAndHistoryTest` validates rollback for `recordSessionAndAnalyticsTransaction` and `finalizeExamAttemptTransaction`, but does **not** test concurrent RMW on `recordBcsStudyGoalDelta()`, `recordBcsTopicProgress()`, or `deleteExamAttempt()` racing `recordDailyStudyDelta()`.
9. **Missing Regression Coverage**:
   - **Diagnostic Concurrency Tests (Mandatory in `WP-4.2`)**:
     - Seeded pre-existing `BcsStudyPlanGoalEntity` + `N` parallel coroutines calling `recordBcsStudyGoalDelta()` on the same `goalKey` (plus separate profile `goalKey` for isolation) asserting exact sum of every delta with zero lost updates.
     - Seeded pre-existing `BcsTopicProgressEntity` + `N` parallel coroutines calling `recordBcsTopicProgress()` on the same profile-scoped `topicId` (and racing with `finalizeExamAttempt()`) asserting exact `attemptedQuestions`, `correctQuestions`, `totalTimeSeconds`, and sticky boolean flags (`isGuideCompleted`, `miniCheckPassed`) plus profile isolation.
     - Concurrent `deleteExamAttempt()` + `recordDailyStudyDelta()` verifying zero lost updates or resurrected exam metrics.
     - Cancellation/fault-injection test confirming session-completion atomic persistence.
10. **User Impact**:
    - Parallel BCS interactions silently drop study goal increments and topic mastery progress; concurrent exam deletion and study activity recording corrupt daily study totals; mid-completion interruption splits quiz scores from history/analytics.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - **5A (Narrowest Safe Fix — Preserve Existing Lock Ordering & 10 DAO Transactions)**:
      - First execute the diagnostic concurrency tests to confirm the exact lost-update failure on Room's coroutine dispatcher.
      - Narrowest safe correction: wrap the read-modify-write inside Room `@Transaction` DAO methods (`recordBcsStudyGoalDeltaTransaction` and `recordBcsTopicProgressTransaction`, or atomic SQL `INSERT ... ON CONFLICT DO UPDATE` / Room `@Transaction` serialized by SQLite's write lock), ensuring `finalizeExamAttemptTransaction` calls the same inner logic without nesting conflicting mutexes or altering `dailyStudyWriteMutex` lock ordering.
    - **5B**: Wrap `MathRepository.deleteExamAttempt()` in `dailyStudyWriteMutex.withLock { dao.deleteExamAttemptSafely(attemptId, dateKey) }` — strictly preserving the existing single-mutex hierarchy (`dailyStudyWriteMutex` → Room `@Transaction`, identical to `recordSessionAndAnalytics` and `finalizeExamAttempt`).
    - **5C**: Optionally include optional `QuizScoreEntity?` insertion inside `recordSessionAndAnalyticsTransaction()` (extending the existing transaction parameter list with default `null`, avoiding duplicate DAO transactions).
    - **5D**: Keep `toggleBookPageBookmark` / `recordFormulaMastery` / `upsertBookLessonProgress` as low-risk optional `@Transaction` cleanups only if non-disruptive.
    - **Data Safety**: Zero schema version bump (`Room v10` preserved); zero historical data deletion.
12. **Unresolved Uncertainty**:
    - Exact SQLite busy-timeout / coroutine-dispatcher contention behavior under Robolectric vs. physical Android SQLite should be observed directly when running the WP-4.2 diagnostic concurrency tests before finalizing whether Room `@Transaction` alone suffices or requires an explicit repository mutex (never nested inconsistently with `dailyStudyWriteMutex`).

---

### Finding `F4-06` — Immediate-Feedback Quiz Correct-Answer Double Resolution & Submit→Finish Coroutine Race

1. **Unique ID**: `F4-06`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — direct source verification of asymmetric guard & uncoordinated coroutine launch)
3. **Severity**: `P1 — High`
4. **Actual Source Path & Line Range**:
   - `app/src/main/java/com/example/ui/MathViewModel.kt:1456–1551` (`submitQuizAnswer()`)
   - `app/src/main/java/com/example/ui/MathViewModel.kt:1674–1914` (`finishQuizSession()`, specifically `:1851–1902`)
   - `app/src/main/java/com/example/data/db/MathDao.kt:1036–1144` (`finalizeExamAttemptTransaction()`)
5. **Complete Caller Chain**:
   - *Step 1 (Immediate feedback)*: `QuizPlayScreen` → `MathViewModel.submitQuizAnswer()` (`:1456–1551`) → synchronously records `userAnswers[currentIndex]` at `:1502` → launches **untracked, unmutexed** `viewModelScope.launch` (`:1513–1545`) calling `recordUnifiedMistake` / `recordBcsMistake` (if wrong) OR `resolveUnifiedMistake` / `markBcsMistakeResolved` (if correct, `:1538–1544`).
   - *Step 2 (Session finish)*: `QuizPlayScreen` → `MathViewModel.finishQuizSession()` (`:1674–1914`) → acquires CAS completion + `examPersistenceMutex` → builds mistake payloads (`:1851–1902`) → calls `repository.finalizeExamAttempt(...)` → `MathDao.finalizeExamAttemptTransaction()` (`:1036–1144`).
6. **Expected vs. Actual Behavior**:
   - **Expected**: (a) In immediate-feedback mode (`!isFullExamMode`), an already-submitted answer (whether wrong **or** correct) must be persisted/resolved **exactly once**; (b) `finishQuizSession()` must coordinate with any in-flight `submitQuizAnswer()` persistence so fast Submit→Finish cannot drop or reorder mistake writes.
   - **Actual**:
     1. **Asymmetric Double-Resolution Bug**: In `finishQuizSession()` at `:1857–1860`, already-submitted **wrong** answers are skipped (`if (!winningCurrent.isFullExamMode && winningCurrent.userAnswers.containsKey(index)) return@mapIndexedNotNull null`), **but the `else` (correct answer) branch at `:1884–1901` has NO such guard**! Consequently, every already-submitted correct answer in immediate-feedback mode resolves the mistake ONCE in `submitQuizAnswer()` (`:1538–1544`) and resolves it a **SECOND TIME** inside `finalizeExamAttemptTransaction()` (`MathDao.kt:1104–1143`), double-advancing `reviewIntervalDays` (`1 → 3 → 7` days) on a single correct answer!
     2. **Uncoordinated Submit→Finish Race**: Because `submitQuizAnswer()` mutates `userAnswers` synchronously (`:1502`) and launches a detached `viewModelScope.launch` (`:1513`) outside `examPersistenceMutex`, a rapid Submit→Finish causes `finishQuizSession()` to skip that wrong answer (`:1857–1860`) assuming `submitQuizAnswer()` persisted it; if that detached coroutine runs after or is cancelled before completing, mistake state can be dropped or interleaved out of order with `finalizeExamAttemptTransaction()`.
7. **Evidence & Reproduction / Disproving Status**:
   - Verified directly at `MathViewModel.kt:1502`, `:1513–1545`, `:1857–1860`, and `:1884–1901`.
8. **Existing Regression Coverage**:
   - `Phase3DatabaseTransactionAndHistoryTest` tests `finalizeExamAttemptTransaction()` directly (bypassing `submitQuizAnswer()`), and `Phase3ConcurrencyAndSolverCancellationTest` tests Quiz CAS completion without checking mistake interval double-advancement or Submit→Finish ordering.
9. **Missing Regression Coverage**:
   - Tests simulating immediate-feedback mode (`isFullExamMode = false`): (a) submitting a correct answer for an existing mistake and then finishing the quiz, asserting `reviewIntervalDays` advances **once** (`1 → 3`, not `7`); (b) rapid Submit(wrong)→Finish, asserting the wrong answer is persisted cleanly and ordered without race or loss; (c) unsubmitted draft answers in full-exam mode remain persisted properly.
10. **User Impact**:
    - Immediate-feedback Quiz learners who correctly retry a previously missed question skip spaced-repetition intervals (`1 → 7` days), and rapid Submit→Finish taps risk losing or reordering mistake updates.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Add the matching `if (!winningCurrent.isFullExamMode && winningCurrent.userAnswers.containsKey(index))` skip guard to the correct-answer resolution branch (`MathViewModel.kt:1884–1901`) — OR better yet, serialize `submitQuizAnswer()` mistake persistence under `examPersistenceMutex` (or track pending submit jobs and `join()` them before `finalizeExamAttemptTransaction()` inside `examPersistenceMutex`) so immediate-feedback writes complete deterministically before finalization, while skipping already-persisted indices symmetrically for both wrong and correct branches.
    - **Data Safety**: Zero schema change; zero historical data mutation.
12. **Unresolved Uncertainty**:
    - None. Both the missing guard on `:1884–1901` and the untracked coroutine at `:1513` are verified directly in source.

---

### Finding `F4-07` — Cold-Launch Streak Fabrication & Dashboard 7-Day Strip Contrast / Time-Only Mismatch

1. **Unique ID**: `F4-07`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — verified in `MathViewModel.kt`, `LearningAnalyticsEngine.kt`, `DashboardScreen.kt`, and `ExampleRobolectricTest.kt`)
3. **Severity**: `P2 — Medium`
4. **Actual Source Path & Line Range**:
   - `app/src/main/java/com/example/ui/MathViewModel.kt:362–369, 402, 3003–3090` (`initStreakAndActivity()`, `recordDailyActivity()`)
   - `app/src/main/java/com/example/math/LearningAnalyticsEngine.kt:634–854` (`computeDailyGoalsAndStreaks()`)
   - `app/src/main/java/com/example/ui/screens/DashboardScreen.kt:335–444` (specifically `:419–432` 7-day circle styling)
   - `app/src/test/java/com/example/ExampleRobolectricTest.kt:155–162`
5. **Complete Caller Chain**:
   - *Sub-defect 7A (Cold-Launch SharedPreferences Fabrication)*: `MathViewModel.init {}` (`:402`) → `initStreakAndActivity()` (`:3003–3054`) → writes `streak_current_days = 1`, `streak_longest_days = 1`, `act_$today = 1` to `SharedPreferences` on cold launch with **zero study activity**, updating `streakDays` / `longestStreak` / `weeklyActivities` StateFlows (meanwhile `DashboardScreen` reads Room-backed `LearningAnalyticsEngine.computeDailyGoalsAndStreaks()`, which accurately computes `currentStreakDays = 0` on empty history — creating semantic split-brain between ViewModel's SharedPreferences streak and Dashboard's Room study streak).
   - *Sub-defect 7B (Dashboard 7-Day Activity Strip Visual & Semantic Mismatch)*: `LearningAnalyticsEngine.computeDailyGoalsAndStreaks()` (`:775–796`, counts a day active when `questionsAttempted > 0 || studyTimeSec > 0`) → `DashboardScreen.kt:419–432`:
     - Circle fill checks `dayPt.goalMet -> Color(0xFF2E7D32)` (dark green), else `dayPt.questionsAttempted > 0 -> primary`, else `surfaceContainerHigh`.
     - Text color checks `if (dayPt.questionsAttempted > 0) Color.White else onSurface`.
     - **Defect 1 (Contrast)**: If a user achieves `dayPt.goalMet == true` via Study Timer minutes alone (`studyTimeSeconds >= targetMinutes * 60`, `questionsAttempted == 0`), the circle renders **dark green (`0xFF2E7D32`) with dark `onSurface` text** (failing WCAG contrast).
     - **Defect 2 (Semantic Mismatch)**: If a user studies via Study Timer (`studyTimeSeconds > 0`, `< targetMinutes`, `questionsAttempted == 0`), `LearningAnalyticsEngine` counts the day as active in `currentStreakDays` and `activeDaysLast7`, yet `DashboardScreen.kt:421` renders the circle as **inactive grey** because it checks only `dayPt.questionsAttempted > 0` instead of `dayPt.questionsAttempted > 0 || dayPt.studyTimeSeconds > 0`.
6. **Expected vs. Actual Behavior**:
   - **Expected**: (a) **App Visit Streak** (opening the app) and **Actual Study Streak** (completing questions or study time) must remain explicitly separated so opening the app with zero study never fabricates study activity (`act_$today = 1`) or inflates study streak (`1` on empty launch); (b) Dashboard's 7-day strip must treat time-only study days consistently with `LearningAnalyticsEngine` and render high-contrast `Color.White` text whenever `dayPt.goalMet || isActiveDay`.
   - **Actual**: (a) `initStreakAndActivity()` seeds `streak_current_days = 1` and `act_$today = 1` on cold launch with zero study; (b) `DashboardScreen.kt:419–432` miscolors dark-on-dark-green text on time-only goal-met days and hides sub-goal time-only active days as grey.
7. **Evidence & Reproduction / Disproving Status**:
   - Verified directly at `MathViewModel.kt:3013–3050`, `LearningAnalyticsEngine.kt:775–796`, and `DashboardScreen.kt:419–432`.
   - Note: `ExampleRobolectricTest.kt:158–159` currently asserts `assertTrue(viewModel.streakDays.value >= 1)` reflecting the cold-launch fabrication.
8. **Existing Regression Coverage**:
   - `ExampleRobolectricTest.kt:155–162` currently enforces the fabricated `>= 1` SharedPreferences value.
   - No unit/UI test covers time-only Study Timer days on the Dashboard 7-day strip.
9. **Missing Regression Coverage**:
   - Tests asserting: (a) cold launch with zero Room activity produces `Actual Study Streak == 0` (while any explicit App Visit counter, if retained, is clearly named and separate from study activity `act_$today`); (b) Dashboard 7-day circle uses white text when `goalMet == true` regardless of `questionsAttempted == 0`, and highlights days with `studyTimeSeconds > 0`.
10. **User Impact**:
    - Conflicting streak semantics between SharedPreferences (`1` on fresh install) and Room Dashboard (`0`), plus illegible dark-on-dark-green day badges and missing visual activity indicators for Study-Timer-only days.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Explicitly rename or isolate `initStreakAndActivity()`'s SharedPreferences tracking as **App Visit** metadata without seeding `act_$today = 1` as study activity, and align `streakDays`/`longestStreak` StateFlows (or explicitly separate `appVisitStreakDays` vs. `studyStreakDays`) so zero-study launches report `0` study streak and `0` study activity. Update `ExampleRobolectricTest.kt:158–159` accordingly.
    - In `DashboardScreen.kt:419–432`, define `val hasStudyActivity = dayPt.questionsAttempted > 0 || dayPt.studyTimeSeconds > 0`, use `dayPt.goalMet -> Color(0xFF2E7D32)`, `hasStudyActivity -> MaterialTheme.colorScheme.primary`, and text color `if (dayPt.goalMet || hasStudyActivity) Color.White else MaterialTheme.colorScheme.onSurface`.
    - **Data Safety**: Zero Room schema changes; existing `DailyStudyActivityEntity` records untouched.
12. **Unresolved Uncertainty**:
    - None. All sources (`SharedPreferences`, Room, `LearningAnalyticsEngine`, `DashboardScreen`) are verified.

---

### Finding `F4-08` — NCTB Page Companion Provenance Misattribution & Gated Visual PDF Verification Limitation

1. **Unique ID**: `F4-08`
2. **Classification & Confidence**:
   - **Page Companion Provenance Misattribution**: `Confirmed source-level defect` (High confidence — verified in `BookCurriculumRepository.kt` and `BookReaderAndLabsScreens.kt`)
   - **Visual PDF Page-Boundary & Text-Alignment Verification**: `Requires additional evidence` (`Unverified` — gated by sandboxed `view_file` path restriction)
3. **Severity**: `P2 — Medium`
4. **Actual Source Path & Line Range**:
   - `app/src/main/java/com/example/math/BookCurriculumRepository.kt:39–152` (`getStructuredPageContent()`)
   - `app/src/main/java/com/example/ui/screens/BookReaderAndLabsScreens.kt:640–801` (specifically `:691` formula heading, `:727–785` worked examples & theorems)
   - `app/src/main/java/com/example/math/BookModels.kt:594–608` (`StructuredTextbookPageContent`)
   - `app/src/main/java/com/example/math/BookCurriculumClass6/7/8/910.kt` (47 chapters)
   - `app/src/main/assets/books/class_{6,7,8,9_10}.pdf` (894 raster pages)
5. **Complete Caller Chain**:
   `BookReaderScreen` (`BookReaderAndLabsScreens.kt:640–801`) → `BookCurriculumRepository.getStructuredPageContent(bookId, printedPage)` (`:39–152`) → reads `BookChapterSpec` from `BookCurriculumClass{6,7,8,910}.kt` → populates `StructuredTextbookPageContent` → renders headings in `BookReaderAndLabsScreens.kt`.
6. **Expected vs. Actual Behavior**:
   - **Expected**: When a textbook page does not have page-pinned formulas, worked examples, or theorems, any lesson-level or chapter-level study companion fallback must be **explicitly labeled with provenance** (e.g., `"অধ্যায়ভিত্তিক সহায়ক সূত্র"` / `"পাঠভিত্তিক সহায়ক উদাহরণ"` vs. `"এই পৃষ্ঠার সূত্র/উদাহরণ"`), never mispresenting a modulo-rotated chapter formula as printed on that specific page.
   - **Actual**:
     - `BookCurriculumRepository.kt:94–96` unconditionally selects `formulasOnPage` via `chapter.formulas[pageOffsetInChapter % chapter.formulas.size]` (modulo rotation across every page of the chapter), yet `BookReaderAndLabsScreens.kt:691` renders the hard-coded header `"এই পৃষ্ঠার সংশ্লিষ্ট গাণিতিক সূত্র:"` ("Mathematical formulas related to this page:") with **zero provenance badge**.
     - `BookCurriculumRepository.kt:99–104` falls back to `chapter.workedExamples[pageOffsetInChapter % size]` on every page lacking an exact `printedPageRef == printedPage` match (affecting ~85%–95% of pages across all 4 books, since each chapter defines 1–2 worked examples across 8–37 pages), and `:107–111` falls back to `theoremsAndConstructions.take(1)` — all displayed without distinguishing page-pinned vs. chapter-fallback provenance.
     - Note: All 4 bundled PDFs (`161`/`181`/`190`/`362` = `894` pages) were structurally verified as 1:1 `/FlateDecode` `/DeviceRGB` raster scans with `0` text fonts and `0` corrupt streams, while visual confirmation of `pdfPageOffset = 5` across the 42 deterministic boundary sample pages remains `Unverified` in this container.
7. **Evidence & Reproduction / Disproving Status**:
   - Provenance defect verified directly at `BookCurriculumRepository.kt:94–111` and `BookReaderAndLabsScreens.kt:691`.
   - Existing tests inspected: `EcosystemUpgradeV7UnitTest.kt:883–889` asserts `chapterTitleBn.isNotBlank()`, `sectionHeadingBn.isNotBlank()`, `keyDefinitionsBn.isNotEmpty()` across sampled pages; `PrincipalAuditBug94To97RegressionTest.kt:331–358` verifies structured page companion stability.
   - Structural PDF verification: `894/894` pages verified clean (`PHASE_4_AUDIT_EVIDENCE.md §7`). Visual verification: **`Unverified`** (sandboxed `view_file` cannot read `/tmp/nctb_preflight/class_6_pdfpage_6.png`).
8. **Existing Regression Coverage**:
   - `EcosystemUpgradeV7UnitTest.kt:883–889` and `PrincipalAuditBug94To97RegressionTest.kt:331–358` verify non-empty companion data but do not assert provenance accuracy or distinguish page-pinned vs. chapter-level fallbacks.
9. **Missing Regression Coverage**:
   - Unit tests verifying `StructuredTextbookPageContent` provenance enum/flags (`PAGE_PINNED`, `LESSON_LEVEL`, `CHAPTER_FALLBACK`) for pinned pages vs. unpinned pages while preserving existing non-empty companion invariants (`keyDefinitionsBn.isNotEmpty()`, etc.).
   - Physical-device / human visual verification checklist for the 42 deterministic PDF boundary pages (`PHASE_4_IMPLEMENTATION_ROADMAP.md`).
10. **User Impact**:
    - Students using the Hybrid Text Companion alongside a scanned NCTB textbook page see chapter-rotated formulas and worked examples presented as if they belong specifically to the current printed page, risking confusion when the scanned PDF page shows different content.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Add an explicit provenance enum (e.g., `CompanionAttributionScope { PAGE_PINNED, LESSON_REFERENCE, CHAPTER_FALLBACK }`) to `StructuredTextbookPageContent` for formulas, worked examples, and theorems (with safe default values so existing constructors/tests remain source-compatible).
    - Update `BookReaderAndLabsScreens.kt` to display truthful Bengali section headers/badges based on provenance (e.g., `"অধ্যায়ভিত্তিক সহায়ক সূত্র (রিভিশন)"` for chapter-level fallback vs. `"এই পৃষ্ঠার উদাহরণ"` only when `printedPageRef == printedPage`), while keeping non-empty companion content so `EcosystemUpgradeV7UnitTest` continues passing without breaking study utility.
    - **Data Safety**: Zero Room schema or user data impact.
12. **Unresolved Uncertainty**:
    - Exact visual alignment of `pdfPageOffset = 5`, chapter start/end printed page numbers, answer-key ranges, and verbatim textbook wording across all 42 deterministic boundary sample pages remains **`Unverified`** until visually inspected on-device or with an external PDF viewer.

---

### Finding `F4-09` — Context-Unaware Numeric & Boolean Answer Validation (`SharedMathEngine.isAnswerCorrect`)

1. **Unique ID**: `F4-09`
2. **Classification & Confidence**: `Confirmed source-level defect` (High confidence — verified in `SharedMathEngine.kt` and across all 19 call sites)
3. **Severity**: `P1 — High` *(direct mathematical grading corruption on any numeric question with answer `0` or `1`)*
4. **Actual Source Path & Line Range**:
   - **Primary**: `app/src/main/java/com/example/math/SharedMathEngine.kt:410–441` (`isAnswerCorrect()`)
   - **All Verified Call Sites (19 total)**:
     - `MathViewModel.kt`: Quiz numerical/fill-blank (`:328, :342, :1360, :1469, :1700, :1717`), PracticeLab (`:2322, :2325, :2339, :2342`), NumberSystem (`:3268`)
     - `BookQuestionEngine.kt:398` *(plus secondary tolerance check `abs(uNum - eNum) < 0.01` at `:395`)*
     - `ExamEngineModels.kt:434, :442`
     - `QuizModels.kt:124, :132`
     - `ProblemSolverPedagogyComponents.kt:1023` *(plus secondary substring match `expected.contains(userAttempt.trim())` at `:1024`)*
   - *(Verified unaffected: `MentalMath` typed answers at `MathViewModel.kt:2647–2655` use strict `toIntOrNull()`.)*
5. **Complete Caller Chain**:
   `QuizPlayScreen` / `PracticeLabPlayScreen` / `NumberSystemPracticePlayScreen` / `BookPracticeScreen` / `ProblemSolver` → `MathViewModel` / `BookQuestionEngine` / `ExamScoringEngine` / `QuizModels` → `SharedMathEngine.isAnswerCorrect(userInput, expectedAnswer)` (`SharedMathEngine.kt:410–441`).
6. **Expected vs. Actual Behavior**:
   - **Expected**:
     - On a **numeric** question whose expected answer is `1` (`"1"` or `"১"`) or `0` (`"0"` or `"০"`), boolean words (`"সত্য"`, `"true"`, `"হ্যাঁ"`, `"yes"`, `"ঠিক"`, `"মিথ্যা"`, `"false"`, `"না"`, `"no"`, `"ভুল"`) must be graded **INCORRECT**.
     - On a **True/False** question whose expected answer is boolean (`"সত্য"`, `"মিথ্যা"`, `"true"`, `"false"`, etc.), equivalent Bengali/English boolean tokens must be graded **CORRECT** (and `"1"`/`"0"` accepted only if explicitly permitted in boolean mode, never when expected answer is numeric).
   - **Actual**:
     - `SharedMathEngine.kt:417–420` unconditionally includes `"1"` in `trueTokens = setOf("true", "সত্য", "হ্যাঁ", "yes", "1", "ঠিক")` and `"0"` in `falseTokens = setOf("false", "মিথ্যা", "না", "no", "0", "ভুল")`, and executes `if (uNorm in trueTokens && eNorm in trueTokens) return true` **before** numeric parsing!
     - Consequently:
       - Expected `"1"` (or `"১"`), user types `"সত্য"`, `"true"`, `"হ্যাঁ"`, `"yes"`, or `"ঠিক"` → **FALSELY GRADED `true` (CORRECT)**!
       - Expected `"0"` (or `"০"`), user types `"মিথ্যা"`, `"false"`, `"না"`, `"no"`, or `"ভুল"` → **FALSELY GRADED `true` (CORRECT)**!
       - Expected `"সত্য"`, user types `"1"` or `"১"` → graded `true` even on non-numeric True/False prompts.
     - **Secondary Observation 9B (`ProblemSolverPedagogyComponents.kt:1024`)**: `expected.contains(userAttempt.trim(), ignoreCase = true)` allows a 1-character substring like `"1"` or `"2"` to pass a practice step whose expected answer is `"125"`!
     - **Secondary Observation 9C (`BookQuestionEngine.kt:395`)**: Uses `abs(uNum - eNum) < 0.01`, which is 100× looser than `SharedMathEngine`'s `1e-4` (accepting e.g. `0.509` for `0.50`).
7. **Evidence & Reproduction / Disproving Status**:
   - Proven directly from `SharedMathEngine.kt:410–441` (complete Bengali & English truth table in `PHASE_4_AUDIT_EVIDENCE.md §4`).
8. **Existing Regression Coverage**:
   - `Phase1ValidationAndIntegrityUnitTest` (8 tests) tests valid numeric/fraction/Bengali digit parsing in `SharedMathEngine` but has **no negative test** asserting `isAnswerCorrect("সত্য", "1") == false` or `isAnswerCorrect("মিথ্যা", "0") == false`.
9. **Missing Regression Coverage**:
   - Full truth-table unit test suite for `SharedMathEngine.isAnswerCorrect` covering Bengali & English numeric (`0`, `1`, `০`, `১`, decimals, fractions, negatives) vs. boolean (`সত্য`, `মিথ্যা`, `হ্যাঁ`, `না`, `true`, `false`, `yes`, `no`, `ঠিক`, `ভুল`) across numeric mode and explicit boolean mode, plus `ProblemSolverPedagogyComponents` exact-token / non-trivial match test.
10. **User Impact**:
    - Mathematically invalid text answers (`"সত্য"` for `1`, `"মিথ্যা"` for `0`, `"হ্যাঁ"` for `১`, `"না"` for `০`) are graded correct across Quiz, Practice Lab, Number System, Book Practice, and Solver — directly violating mathematical accuracy in an educational math app.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Remove `"1"` and `"0"` from `trueTokens` and `falseTokens` in `SharedMathEngine.kt:417–420` (keeping `trueTokens = setOf("true", "সত্য", "হ্যাঁ", "yes", "ঠিক")` and `falseTokens = setOf("false", "মিথ্যা", "না", "no", "ভুল")`), and optionally add an overloaded `isAnswerCorrect(userInput, expectedAnswer, mode: AnswerValidationMode = AnswerValidationMode.AUTO)` where `AUTO` only uses boolean equivalence when `eNorm` is a non-numeric boolean word, while numeric expected answers (`"0"`, `"1"`, `"০"`, `"১"`) strictly require numeric/fraction equivalence.
    - Tighten `ProblemSolverPedagogyComponents.kt:1024` so numeric expected answers do not fall back to arbitrary short substring `contains(...)`.
    - Align `BookQuestionEngine.kt:395` tolerance (`1e-4` or explicit rounding rule) if no existing test relies on `0.01`.
    - **Data Safety**: Pure logic fix; zero database or persisted data impact.
12. **Unresolved Uncertainty**:
    - Verify whether any existing test in `ExampleRobolectricTest` or `Phase1ValidationAndIntegrityUnitTest` intentionally passes `"1"`/`"0"` for a boolean expected answer (none found during inspection; confirm during `WP-4.4` implementation run).

---

### Finding `F4-10` — Bookmark Category Routing, `SquareCubeModels.kt` KDoc Inaccuracies & Historical Documentation Drift

1. **Unique ID**: `F4-10`
2. **Classification & Confidence**:
   - **Sub-item 10A (Bookmark Category Routing for Producible Categories)**: **`Not reproducible / disproven`** (High confidence — all 12 creation sites and all 8 producible categories verified)
   - **Sub-item 10B (`SquareCubeModels.kt` KDoc Inaccuracies)**: **`Confirmed source-level defect`** (High confidence)
   - **Sub-item 10C (Historical Documentation Drift)**: **`Confirmed source-level defect`** (High confidence)
3. **Severity**: `P3 — Low`
4. **Actual Source Path & Line Range**:
   - **10A**: `app/src/main/java/com/example/ui/screens/BookmarksAndTutorScreens.kt:134–166`; `app/src/main/java/com/example/ui/screens/BookHubAndDetailScreens.kt:1179,1202–1210`; plus all 11 other `BookmarkEntity(...)` creation sites across `MathViewModel.kt`, `MathRepository.kt`, and UI screens.
   - **10B**: `app/src/main/java/com/example/math/SquareCubeModels.kt:4–7, 16–19`
   - **10C**: `README.md:8,52`; `docs/qa/FINAL_REGRESSION_TEST_REPORT.md`; `docs/database/FINAL_DATABASE_INTEGRITY_REPORT.md`; `docs/qa/FINAL_DEVICE_QA_REPORT.md`
5. **Complete Caller Chain**:
   - **10A**: All 12 `BookmarkEntity(...)` persistence sites produce exactly **8 distinct categories**: `BOOK_PAGE`, `FORMULA` (including `book_formula_*` at `BookHubAndDetailScreens.kt:1179,1205`), `FORMULA_LIBRARY`, `NUMBER_SYSTEM`, `PRIME`, `SOLVER`, `SQUARE_CUBE`, and `TABLE`. Clicking any bookmark invokes `BookmarksAndTutorScreens.kt:134–166`, which handles **all 8 producible categories** (plus `"PROBLEM_SOLVER"` and `"BCS_PREP"` defensively). Only an unrecognized/corrupted future category has no `else` branch.
   - **10B**: `SquareCubeModels.kt` KDoc comments (developer documentation only; runtime math lists use `1..20` and `1..10`/`1..20` accurately).
   - **10C**: Repository documentation files (`README.md`, `docs/**`).
6. **Expected vs. Actual Behavior**:
   - **10A Expected vs. Actual**: Every producible bookmark category should route to its corresponding screen — **Actual**: All 8 producible categories route properly (hypothesis of broken producible category routing is **disproven**; only a minor defensive `else` fallback is absent).
   - **10B Expected vs. Actual**: KDoc should accurately describe data ranges and English translations — **Actual**: `SquareCubeModels.kt:5` says `সংখ্যা (2 to 20)` (actual: `1..20`), `:6` says `বর্গ (Cube)` (mistranslated — `বর্গ` is `Square`), and `:17` says `সংখ্যা (2 to 10)` (actual: `1..10` / `1..20`).
   - **10C Expected vs. Actual**: Top-level documentation should reflect current verified metrics — **Actual**: `README.md` simultaneously claims `245/245 (13 suites)` (`:8`) and `215/215 (10 suites)` (`:52`); `FINAL_REGRESSION_TEST_REPORT.md` claims `215/215 (10 suites)`; `FINAL_DATABASE_INTEGRITY_REPORT.md` claims Room `v9` / `20` entities (actual: `340/340`, `29` suites, Room `v10`, `21` entities, `9` migrations).
7. **Evidence & Reproduction / Disproving Status**:
   - **10A**: Disproven via exhaustive enumeration of all 12 `BookmarkEntity(` creation sites in `app/src/main/java/` cross-checked against `BookmarksAndTutorScreens.kt:134–166`.
   - **10B**: Confirmed directly at `SquareCubeModels.kt:5–6,17`.
   - **10C**: Confirmed directly in `README.md:8,52` and `docs/**`.
8. **Existing Regression Coverage**:
   - `HomeNavigationAndOrderingUnitTest` and `ExampleRobolectricTest` exercise core navigation and bookmark persistence; no test checks unknown-category defensive fallback.
9. **Missing Regression Coverage**:
   - Optional lightweight unit/UI test verifying a defensive `else` branch in `BookmarksAndTutorScreens.kt` (e.g., fallback snackbar or safe navigation to Home/FormulaLibrary) for corrupted/unknown category strings.
10. **User Impact**:
    - **10A**: Zero impact for all normal user flows (all 8 producible categories work).
    - **10B / 10C**: Zero runtime impact; affects engineering accuracy and documentation trustworthiness.
11. **Recommended Minimal Correction & Data Migration / Historical Data Safety Implications**:
    - Add a 1-line defensive `else` branch in `BookmarksAndTutorScreens.kt:134–166` (optional hardening; do **not** refactor working category handlers).
    - Fix the 3 KDoc comment lines in `SquareCubeModels.kt:5–6,17`.
    - Synchronize `README.md` (and add current-baseline notes to stale `/docs/**` summary headers) during `WP-4.4` (not during Goal 4.0).
    - **Data Safety**: Zero database or user data impact.
12. **Unresolved Uncertainty**:
    - None.
