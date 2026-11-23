# Math Master — Phase 4 Audit Evidence Ledger (Goal 4.0)

**Purpose**: Reproducible raw evidence backing `PHASE_3_FINAL_CLOSURE_REVIEW.md`, `PHASE_4_AUDIT_MASTER_REPORT.md`, and `PHASE_4_IMPLEMENTATION_ROADMAP.md`.  
**Audit Execution Date**: `2026-11-19` (UTC)  
**Environment**: Google AI Studio Android Build Container (Linux, system `/opt/gradle/gradle-9.3.1/bin/gradle`, `/usr/bin/python3`, no `.git`, no ADB/Android emulator, no third-party PDF packages installed).

---

## 1. GitHub-to-Workspace Parity Log

Read-only comparison against GitHub Git Tree API for `https://github.com/rocky-ahmed/Math-Master`, branch `main`, commit `18c7b22558ebfd6eb317768ea9078fca2bc2055c` (`"phase 3 complete"`):

- **API Endpoint**: `GET https://api.github.com/repos/rocky-ahmed/Math-Master/git/trees/18c7b22558ebfd6eb317768ea9078fca2bc2055c?recursive=1` (`HTTP 200`, `truncated = false`)
- **Total Committed Blobs in Tree**: `206`
- **Local Git Blob SHA-1 Exact Matches**: **`206 / 206` (`100.0%`)**
- **Modified Committed Files**: **`0`**
- **Missing Committed Files**: **`0`**
- **AI Studio Local-Only Non-Build Files**: **`2`** (`debug.keystore`, `debug.keystore.base64` — expected signing artifacts excluded from Git; strictly untouched)
- **No Pull / Clone / Reset / Overwrite Performed**: Authoritative AI Studio workspace remained read-only throughout.

---

## 2. Raw Fresh Automated Test Baseline (`gradle :app:testDebugUnitTest --rerun-tasks`)

- **Command**: `gradle :app:testDebugUnitTest --rerun-tasks` (no `gradle clean`, no code/test edits)
- **Result**: **`BUILD SUCCESSFUL in 4m 22s`** (`33 actionable tasks: 33 executed`)
- **Aggregate JUnit XML Totals**: **`29 / 29` suites passed, `340 / 340` tests passed, `0` failures, `0` errors, `0` skipped, JUnit sum time = `87.901s`**

| # | Test Suite (`com.example.*`) | Tests | Passed | Failures | Errors | Skipped | Time (s) | Notes |
| :-: | :--- | :-: | :-: | :-: | :-: | :-: | :-: | :--- |
| 1 | `ApplicationWideScrollRestorationUnitTest` | 10 | 10 | 0 | 0 | 0 | 27.848 | Scroll restoration |
| 2 | `BcsPreparationCenterUnitTest` | 12 | 12 | 0 | 0 | 0 | 0.815 | BCS models/questions |
| 3 | `EcosystemUpgradeV7UnitTest` | 27 | 27 | 0 | 0 | 0 | 2.436 | Includes `F4-08` non-empty page assertions (`:883–889`) |
| 4 | `ExampleRobolectricTest` | 59 | 59 | 0 | 0 | 0 | 3.695 | Includes `F4-07` `streakDays >= 1` (`:158–159`) |
| 5 | `ExampleUnitTest` | 1 | 1 | 0 | 0 | 0 | 0.001 | Sanity test |
| 6 | `HomeNavigationAndOrderingUnitTest` | 14 | 14 | 0 | 0 | 0 | 8.802 | Home 14-module navigation |
| 7 | `MathProblemSolverUnitTest` | 50 | 50 | 0 | 0 | 0 | 0.152 | Deterministic solver math |
| 8 | `NumberSystemUnitTest` | 17 | 17 | 0 | 0 | 0 | 0.013 | Number system conversions |
| 9 | `Phase1ProblemSolverUiIntegrationTest` | 4 | 4 | 0 | 0 | 0 | 4.179 | Solver UI integration |
| 10 | `Phase1SolverPersistenceVisibilityTest` | 10 | 10 | 0 | 0 | 0 | 2.932 | Solver persistence |
| 11 | `Phase1ValidationAndIntegrityUnitTest` | 8 | 8 | 0 | 0 | 0 | 0.038 | Valid `SharedMathEngine` inputs |
| 12 | `Phase2AccessibilityAndContrastTest` | 8 | 8 | 0 | 0 | 0 | 1.385 | Accessibility & contrast |
| 13 | `Phase2CustomizationEngineTest` | 4 | 4 | 0 | 0 | 0 | 0.860 | Customization engine |
| 14 | `Phase2ResponsiveLayoutTest` | 7 | 7 | 0 | 0 | 0 | 1.287 | Responsive layout |
| 15 | `Phase2StateRestorationUnitTest` | 8 | 8 | 0 | 0 | 0 | 1.840 | State restoration |
| 16 | `Phase3ConcurrencyAndSolverCancellationTest` | 4 | 4 | 0 | 0 | 0 | 1.262 | CAS guards (omits Number System `F4-01`) |
| 17 | `Phase3DatabaseTransactionAndHistoryTest` | 4 | 4 | 0 | 0 | 0 | 3.743 | Includes `F4-03` 3-row assertion (`:357–358`) |
| 18 | `Phase3KeyIsolationAndNavigationTest` | 4 | 4 | 0 | 0 | 0 | 0.985 | Key isolation & nav stack |
| 19 | `Phase3MemoryAndPdfCacheTest` | 4 | 4 | 0 | 0 | 0 | 1.190 | PDF cache budget/trim |
| 20 | `PrincipalAuditBug87To92RegressionTest` | 6 | 6 | 0 | 0 | 0 | 1.438 | Historical regression |
| 21 | `PrincipalAuditBug94To97RegressionTest` | 5 | 5 | 0 | 0 | 0 | 2.850 | Includes `F4-08` page stability (`:331–358`) |
| 22 | `ProblemSolverLearningStatePersistenceTest` | 4 | 4 | 0 | 0 | 0 | 5.603 | Solver learning state |
| 23 | `ProblemSolverModernUiRedesignTest` | 3 | 3 | 0 | 0 | 0 | 7.734 | Solver redesign UI |
| 24 | `ProblemSolverPostRedesignAcceptanceTest` | 4 | 4 | 0 | 0 | 0 | 2.168 | Solver acceptance |
| 25 | `StudyTimerCompactLayoutRegressionTest` | 3 | 3 | 0 | 0 | 0 | 1.312 | **FROZEN Study Timer** |
| 26 | `StudyTimerCrashSafePersistenceUnitTest` | 11 | 11 | 0 | 0 | 0 | 1.505 | **FROZEN Study Timer** |
| 27 | `UiSpacingAndLayoutAuditUnitTest` | 8 | 8 | 0 | 0 | 0 | 0.415 | UI spacing & layout |
| 28 | `UiUxBugRemediationRegressionTest` | 5 | 5 | 0 | 0 | 0 | 1.191 | UI/UX regressions |
| 29 | `VisualMathExplorerUnitTest` | 36 | 36 | 0 | 0 | 0 | 0.222 | 2D/3D geometry math |
| — | **TOTAL (`29 / 29` suites)** | **340** | **340** | **0** | **0** | **0** | **87.901** | **100% Pass** |

*(Not executed: `app/src/androidTest/java/com/example/ExampleInstrumentedTest.kt` — no emulator/ADB available in container.)*

---

## 3. Exact Source Line Citations (`F4-01` through `F4-10`)

| Finding | Primary File(s) & Exact Line Ranges | Key Verified Code Anchors |
| :---: | :--- | :--- |
| **`F4-01`** | `MathViewModel.kt:3321–3369`; `NumberSystemPracticePlayScreen.kt:434–449` | No `if (!current.isSubmitted \|\| current.isCompleted) return`; launches persistence coroutine before setting `isCompleted = true` (`:3366`) without CAS loop. |
| **`F4-02`** | `MathViewModel.kt:3896–3983` (`:3974–3981`), `:3985–4061` (`:4052–4059`), `:1674–1914`; `MathRepository.kt:922–958` | Both `startBcsModeExam` and `startBcsMockExam` call `recordBcsStudyGoalDelta(..., mockExamsDelta = 1, questionsDelta = questions.size, studyTimeMinutesDelta = (durationMinutes / 2).coerceAtLeast(5))` at session start; `finishQuizSession` never calls `recordBcsStudyGoalDelta`. |
| **`F4-03`** | `MathViewModel.kt:1505–1546`, `:1851–1902`; `MathDao.kt:698–796`, `:1036–1144`; `Phase3DatabaseTransactionAndHistoryTest.kt:357–358` | Wrong BCS question inserts both `"QUIZ_${q.topicTitleBn}_${abs(q.questionBn.hashCode())}"` and `"BCS_${safeProfile}_$rawQId"` into `unified_mistake_notebook`; `markBcsMistakeResolvedTransaction` (`:794–795`) resolves only `"BCS_..."`. |
| **`F4-04`** | `MathViewModel.kt:4524–4528`; `MathDao.kt:640–684` (`:661–673`); `MathRepository.kt:724–734`, `:1008–1032` | `markBookMistakeResolvedTransaction` advances `book_spaced_revision.intervalStage` once and `unified_mistake_notebook.reviewIntervalDays` once (`:672`); `MathViewModel.kt:4527` immediately calls `resolveUnifiedMistake("BOOK_${winningQ.id}")`, advancing `reviewIntervalDays` a second time (`1→3→7`) and overwriting `lastFailedAtMillis` (`:1029`). |
| **`F4-05`** | `MathRepository.kt:305–310`, `:411–496`, `:510–565`, `:617–665`, `:796–869`, `:922–958`, `:1051–1075`; `MathDao.kt:191–265`, `:492–566`, `:926–998`; `MathViewModel.kt:2483–2508`, `:2853–2878`, `:3338–3362`, `:4593–4617` | `recordBcsStudyGoalDelta` (`:922–958`) and `recordBcsTopicProgress` (`:805–869`) execute unlocked non-transactional `get → compute → upsert`; `deleteExamAttempt` (`:305–310`) bypasses `dailyStudyWriteMutex`; 4 session-completion methods call `recordQuizScore` outside `recordSessionAndAnalyticsTransaction`. |
| **`F4-06`** | `MathViewModel.kt:1456–1551` (`:1502`, `:1513–1545`), `:1674–1914` (`:1857–1860`, `:1884–1901`); `MathDao.kt:1036–1144` | `finishQuizSession` skips already-submitted wrong answers (`:1857–1860`) but lacks the guard on already-submitted correct answers (`:1884–1901`), causing double resolution; `submitQuizAnswer` launches unmutexed/untracked `viewModelScope.launch` (`:1513`) after synchronous `userAnswers` update (`:1502`). |
| **`F4-07`** | `MathViewModel.kt:362–369`, `:402`, `:3003–3090`; `LearningAnalyticsEngine.kt:634–854` (`:775–796`); `DashboardScreen.kt:419–432`; `ExampleRobolectricTest.kt:155–162` | `initStreakAndActivity` writes `streak_current_days = 1` & `act_$today = 1` on cold launch with zero study; `DashboardScreen.kt:419–432` checks `dayPt.questionsAttempted > 0` for text/active fill, causing dark-on-dark-green text on time-only goal-met days and grey circles on time-only active days. |
| **`F4-08`** | `BookCurriculumRepository.kt:39–152` (`:94–111`); `BookReaderAndLabsScreens.kt:640–801` (`:691`); `BookModels.kt:594–608` | Modulo rotation of `chapter.formulas` (`:94–96`) and `chapter.workedExamples` (`:99–104`) and `theorems.take(1)` (`:107–111`) rendered under hard-coded page-specific heading `"এই পৃষ্ঠার সংশ্লিষ্ট গাণিতিক সূত্র:"` (`:691`) with no provenance tag. |
| **`F4-09`** | `SharedMathEngine.kt:410–441` (`:417–420`); `ProblemSolverPedagogyComponents.kt:1023–1024`; `BookQuestionEngine.kt:395–399` | Global `"1"` in `trueTokens` and `"0"` in `falseTokens` evaluated prior to numeric comparison; `ProblemSolverPedagogyComponents:1024` allows short substring `contains`; `BookQuestionEngine:395` uses `0.01` tolerance. |
| **`F4-10`** | `BookmarksAndTutorScreens.kt:134–166`; `BookHubAndDetailScreens.kt:1179,1202–1210`; `SquareCubeModels.kt:4–7,16–19`; `README.md:8,52`; `docs/qa/FINAL_REGRESSION_TEST_REPORT.md`; `docs/database/FINAL_DATABASE_INTEGRITY_REPORT.md` | All 8 producible bookmark categories (`BOOK_PAGE`, `FORMULA`, `FORMULA_LIBRARY`, `NUMBER_SYSTEM`, `PRIME`, `SOLVER`, `SQUARE_CUBE`, `TABLE`) handled (only `else` absent); `SquareCubeModels.kt` KDoc says `2 to 20` / `বর্গ (Cube)` / `2 to 10`; docs cite stale `245/215` tests & `Room v9`. |

---

## 4. `F4-09` Numeric & Boolean Answer Validation Truth Table

### Verified Call-Site Inventory (19 Call Sites)
- `MathViewModel.kt`: Quiz numerical/fill-blank (`:328, :342, :1360, :1469, :1700, :1717`), PracticeLab (`:2322, :2325, :2339, :2342`), NumberSystem (`:3268`).
- `BookQuestionEngine.kt:398` (numeric check at `:395` uses `< 0.01` before delegating to `SharedMathEngine.isAnswerCorrect`).
- `ExamEngineModels.kt:434, :442`; `QuizModels.kt:124, :132`; `ProblemSolverPedagogyComponents.kt:1023`.
- **Unaffected**: `MentalMath` typed answers (`MathViewModel.kt:2647–2655`) use strict `BengaliNumberFormatter.toEnglishDigits(typed).toIntOrNull()`.

### Truth Table (`SharedMathEngine.isAnswerCorrect(userInput, expectedAnswer)`)

| Question Context | `userInput` | `expectedAnswer` | Current Output (`SharedMathEngine.kt:417–420`) | Expected Output (Post-`WP-4.4`) | Status |
| :--- | :---: | :---: | :---: | :---: | :--- |
| Numeric (English) | `"true"` | `"1"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Numeric (Bengali) | `"সত্য"` | `"1"` / `"১"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Numeric (Bengali Yes) | `"হ্যাঁ"` / `"ঠিক"` | `"1"` / `"১"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Numeric (English Yes) | `"yes"` | `"1"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Numeric (English) | `"false"` | `"0"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Numeric (Bengali) | `"মিথ্যা"` | `"0"` / `"০"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Numeric (Bengali No) | `"না"` / `"ভুল"` | `"0"` / `"০"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Numeric (English No) | `"no"` | `"0"` | **`true` (BUG)** | **`false`** | Misgraded (`P1`) |
| Boolean (Bengali) | `"1"` / `"১"` | `"সত্য"` | `true` (cross-type) | **`false`** *(unless explicit boolean-bit mode)* | Cross-type leak |
| Boolean (Bengali) | `"0"` / `"০"` | `"মিথ্যা"` | `true` (cross-type) | **`false`** *(unless explicit boolean-bit mode)* | Cross-type leak |
| Numeric (Exact/Bn) | `"১"` / `"1"` / `"1.0"` | `"1"` / `"১"` | `true` | **`true`** | Preserve |
| Numeric (Zero/Bn) | `"০"` / `"0"` / `"0.0"` | `"0"` / `"০"` | `true` | **`true`** | Preserve |
| Numeric (Fraction) | `"1/2"` / `"১/২"` | `"0.5"` / `"০.৫"` | `true` | **`true`** | Preserve |
| Boolean (Synonyms) | `"সত্য"` / `"হ্যাঁ"` / `"true"` | `"সত্য"` / `"true"` | `true` | **`true`** | Preserve |
| Boolean (Synonyms) | `"মিথ্যা"` / `"না"` / `"false"` | `"মিথ্যা"` / `"false"` | `true` | **`true`** | Preserve |
| Solver Pedagogy (`:1024`) | `"1"` | `"125"` | **`true` (BUG via `.contains`)** | **`false`** | Secondary (`F4-09B`) |
| Book Numeric (`:395`) | `"0.509"` | `"0.50"` | **`true` (`< 0.01`)** | **`false` (`< 1e-4`)** | Secondary (`F4-09C`) |

---

## 5. DAO (`10` `@Transaction` Methods) & Repository Concurrency Matrix

### Existing 10 Phase 3 `@Transaction` DAO Methods (`MathDao.kt`)

| # | Method Name | Line | Tables Mutated | Guarded by `dailyStudyWriteMutex`? | Audit Verdict |
| :-: | :--- | :-: | :--- | :-: | :--- |
| 1 | `deleteExamAttemptSafely` | `:191` | `exam_attempts`, `daily_study_activity` | **NO** (`MathRepository.kt:305–310`) | **Defect (`F4-05B`)**: Races with `recordDailyStudyDelta` / `updateDailyStudyTargets` |
| 2 | `applyStudyTimerFlushIdempotently` | `:436` | `study_timer_flush_receipts`, `daily_study_activity` | YES (`:387`) | **FROZEN (Read-Only — Verified Intact)** |
| 3 | `recordSessionAndAnalyticsTransaction` | `:492` | `practice_history`, `topic_analytics`, `daily_study_activity` | YES (`:498`) | Clean, but callers execute `recordQuizScore` separately (`F4-05C`) |
| 4 | `recordBookQuestionMistakeTransaction` | `:568` | `book_mistakes`, `book_spaced_revision`, `unified_mistake_notebook` | N/A | Clean |
| 5 | `markBookMistakeResolvedTransaction` | `:640` | `book_mistakes`, `book_spaced_revision`, `unified_mistake_notebook` | N/A | Clean internally; double-called by `MathViewModel.kt:4527` (`F4-04`) |
| 6 | `deleteBookMistakeTransaction` | `:686` | `book_mistakes`, `book_spaced_revision`, `unified_mistake_notebook` | N/A | Clean |
| 7 | `recordBcsMistakeTransaction` | `:698` | `bcs_mistakes`, `unified_mistake_notebook` | N/A | Clean internally; ViewModel also inserts duplicate `"QUIZ_..."` (`F4-03`) |
| 8 | `markBcsMistakeResolvedTransaction` | `:766` | `bcs_mistakes`, `unified_mistake_notebook` | N/A | Clean internally; misses legacy `"QUIZ_..."` twin (`F4-03`) |
| 9 | `deleteBcsMistakeTransaction` | `:798` | `bcs_mistakes`, `unified_mistake_notebook` | N/A | Clean |
| 10 | `finalizeExamAttemptTransaction` | `:813` | `exam_attempts`, `quiz_scores`, `practice_history`, `topic_analytics`, `daily_study_activity`, `bcs_topic_progress`, `unified_mistake_notebook`, `bcs_mistakes` | YES (`:990`) | Clean atomically; receives duplicate BCS unified payload (`F4-03`), double-resolves immediate-feedback correct answers (`F4-06`), and races unlocked `recordBcsTopicProgress` (`F4-05A`) |

### Unprotected Repository Read-Modify-Write & Multi-Table Write Paths (`MathRepository.kt`)

| Repository Method | Lines | Tables Touched | Protection | Audit Verdict |
| :--- | :-: | :--- | :--- | :--- |
| `recordBcsStudyGoalDelta` | `:922–958` | `bcs_study_plan_goals` | **NONE** (unlocked `get → compute → upsert`) | **`P1` Defect (`F4-05A`)**: Lost updates under parallel coroutines |
| `recordBcsTopicProgress` | `:805–869` | `bcs_topic_progress` | **NONE** (unlocked `get → compute → upsert`) | **`P1` Defect (`F4-05A`)**: Lost updates & race vs. `finalizeExamAttemptTransaction` |
| `deleteExamAttempt` | `:305–310` | `exam_attempts`, `daily_study_activity` | `@Transaction` only (**no `dailyStudyWriteMutex`**) | **`P1` Defect (`F4-05B`)**: Interleaves with `recordDailyStudyDelta` / `updateDailyStudyTargets` |
| `recordDailyStudyDelta` | `:411–440` | `daily_study_activity` | `dailyStudyWriteMutex` (no `@Transaction`) | Safe once `deleteExamAttempt` acquires `dailyStudyWriteMutex` |
| `updateDailyStudyTargets` | `:442–496` | `daily_study_activity` | `dailyStudyWriteMutex` (no `@Transaction`) | Safe once `deleteExamAttempt` acquires `dailyStudyWriteMutex` |
| `toggleBookPageBookmark` | `:617–665` | `book_reading_state`, `bookmarks` | Sequential (no `@Transaction`) | `P3` minor desync window on crash/cancel |
| `upsertBookLessonProgress` / `toggleBookLessonCompleted` | `:510–565` | `book_lesson_progress`, `user_progress` | Sequential (no `@Transaction`) | `P3` minor desync window |
| `recordFormulaMastery` | `:1051–1075` | `formula_mastery_state`, `user_progress` | Sequential (no `@Transaction`) | `P3` minor desync window |
| `saveBcsUserProfile` | `:796–803` | `bcs_user_profile` (2 rows) | Sequential (no `@Transaction`) | `P3` low-frequency profile switch |

---

## 6. Complete 47-Chapter NCTB Curriculum & UI Consumption Matrix

Verified directly from `BookCurriculumClass6.kt`, `BookCurriculumClass7.kt`, `BookCurriculumClass8.kt`, `BookCurriculumClass910.kt`, `BookModels.kt` (`pdfPageOffset = 5` for all 4 books), and `BookReaderAndLabsScreens.kt`:
- **UI Field Consumption Verified**: `BookReaderAndLabsScreens.kt` consumes `chapterTitleBn`, `sectionHeadingBn`, `keyDefinitionsBn`, `rulesOnPageBn`, `formulasOnPage`, `workedExamplesOnPage`, `theoremsOnPage`, `exerciseSummaryBn`, and `answerKeyOnPageBn`.
- **Provenance Finding (`F4-08`)**: Formulas (`1–3` per chapter) have no per-page pin and rotate modulo page offset across all 47 chapters; worked examples (`0–2` per chapter) fall back to modulo rotation on unpinned pages; theorems (`0–2` per chapter) fall back to `take(1)`.

| Book | Ch | Title (Bengali) | Printed Pages | Declared PDF Pages | Lessons | Formulas | Worked Examples | Theorems | Linked Interactive Lab(s) |
| :--- | :-: | :--- | :-: | :-: | :-: | :-: | :-: | :-: | :--- |
| `CLASS_6` | 1 | স্বাভাবিক সংখ্যা ও ভগ্নাংশ | 1–37 | 6–42 | 4 | 3 | 2 | 0 | `NUMBER_SYSTEM_EXPLORER` |
| `CLASS_6` | 2 | অনুপাত ও শতকরা | 38–58 | 43–63 | 2 | 3 | 2 | 0 | `FRACTION_AND_RATIO_LAB` |
| `CLASS_6` | 3 | পূর্ণসংখ্যা | 59–75 | 64–80 | 2 | 2 | 2 | 0 | `NUMBER_SYSTEM_EXPLORER` |
| `CLASS_6` | 4 | বীজগাণিতিক রাশি | 76–94 | 81–99 | 2 | 2 | 2 | 0 | `ALGEBRA_INTERACTIVE_LAB` |
| `CLASS_6` | 5 | সরল সমীকরণ | 95–105 | 100–110 | 2 | 1 | 2 | 0 | `EQUATION_SOLVER_VISUALIZER` |
| `CLASS_6` | 6 | জ্যামিতির মৌলিক ধারণা | 106–123 | 111–128 | 2 | 1 | 1 | 2 | `GEOMETRY_INTERACTIVE_LAB` |
| `CLASS_6` | 7 | ব্যবহারিক জ্যামিতি | 124–136 | 129–141 | 1 | 1 | 1 | 1 | `GEOMETRY_INTERACTIVE_LAB` |
| `CLASS_6` | 8 | তথ্য ও উপাত্ত | 137–149 | 142–154 | 1 | 2 | 1 | 0 | `DATA_AND_STATISTICS_LAB` |
| `CLASS_7` | 1 | মূলদ ও অমূলদ সংখ্যা | 1–17 | 6–22 | 2 | 2 | 2 | 0 | `NUMBER_SYSTEM_EXPLORER` |
| `CLASS_7` | 2 | সমানুপাত ও লাভ-ক্ষতি | 18–37 | 23–42 | 2 | 2 | 2 | 0 | `PROFIT_LOSS_PERCENTAGE_CALC` |
| `CLASS_7` | 3 | পরিমাপ | 38–49 | 43–54 | 1 | 2 | 1 | 0 | `MENSURATION_CALCULATOR` |
| `CLASS_7` | 4 | বীজগাণিতিক রাশি গঠন ও ভাগ | 50–68 | 55–73 | 2 | 1 | 2 | 0 | `ALGEBRA_INTERACTIVE_LAB` |
| `CLASS_7` | 5 | বীজগাণিতিক সূত্রাবলি ও প্রয়োগ | 69–88 | 74–93 | 2 | 2 | 2 | 0 | `ALGEBRA_INTERACTIVE_LAB` |
| `CLASS_7` | 6 | বীজগাণিতিক ভগ্নাংশ | 89–101 | 94–106 | 1 | 1 | 1 | 0 | `ALGEBRA_INTERACTIVE_LAB` |
| `CLASS_7` | 7 | সরল সমীকরণ | 102–118 | 107–123 | 2 | 1 | 1 | 0 | `EQUATION_SOLVER_VISUALIZER` |
| `CLASS_7` | 8 | সমান্তরাল সরলরেখা | 119–126 | 124–131 | 1 | 1 | 1 | 1 | `GEOMETRY_INTERACTIVE_LAB` |
| `CLASS_7` | 9 | ত্রিভুজ | 127–144 | 132–149 | 2 | 1 | 1 | 1 | `GEOMETRY_INTERACTIVE_LAB` |
| `CLASS_7` | 10 | সর্বসমতা ও সদৃশতা | 145–161 | 150–166 | 1 | 1 | 1 | 1 | `GEOMETRY_INTERACTIVE_LAB` |
| `CLASS_7` | 11 | তথ্য ও উপাত্ত | 162–169 | 167–174 | 1 | 1 | 1 | 0 | `DATA_AND_STATISTICS_LAB` |
| `CLASS_8` | 1 | প্যাটার্ন | 1–11 | 6–16 | 2 | 3 | 2 | 1 | `PATTERN_AND_SEQUENCE_LAB` |
| `CLASS_8` | 2 | মুনাফা | 12–27 | 17–32 | 2 | 3 | 2 | 0 | `PROFIT_INTEREST_LAB` |
| `CLASS_8` | 3 | পরিমাপ | 28–46 | 33–51 | 1 | 3 | 1 | 0 | `MENSURATION_3D_LAB` |
| `CLASS_8` | 4 | বীজগাণিতিক সূত্রাবলি ও প্রয়োগ | 47–74 | 52–79 | 2 | 3 | 2 | 0 | `ALGEBRA_IDENTITY_LAB` |
| `CLASS_8` | 5 | বীজগাণিতিক ভগ্নাংশ | 75–96 | 80–101 | 1 | 2 | 1 | 0 | `ALGEBRA_IDENTITY_LAB` |
| `CLASS_8` | 6 | সরল সহসমীকরণ | 97–114 | 102–119 | 1 | 1 | 1 | 0 | `EQUATION_BALANCER_LAB`, `COORDINATE_GRAPH_LAB` |
| `CLASS_8` | 7 | সেট | 115–124 | 120–129 | 1 | 2 | 1 | 0 | `SET_VENN_LAB` |
| `CLASS_8` | 8 | চতুর্ভুজ | 125–140 | 130–145 | 1 | 2 | 1 | 2 | `GEOMETRY_CANVAS_LAB`, `MENSURATION_3D_LAB` |
| `CLASS_8` | 9 | পিথাগোরাসের উপপাদ্য | 141–147 | 146–152 | 1 | 1 | 1 | 1 | `PYTHAGORAS_THEOREM_VISUALIZER`, `GEOMETRY_CANVAS_LAB` |
| `CLASS_8` | 10 | বৃত্ত | 148–158 | 153–163 | 1 | 2 | 1 | 1 | `GEOMETRY_INTERACTIVE_LAB`, `PYTHAGORAS_THEOREM_VISUALIZER` |
| `CLASS_8` | 11 | তথ্য ও উপাত্ত | 159–174 | 164–179 | 1 | 3 | 1 | 0 | `STATISTICS_HISTOGRAM_LAB` |
| `CLASS_9_10` | 1 | বাস্তব সংখ্যা | 1–20 | 6–25 | 1 | 2 | 1 | 1 | `NUMBER_LINE_LAB`, `PRIME_FACTOR_SIEVE_LAB` |
| `CLASS_9_10` | 2 | সেট ও ফাংশন | 21–42 | 26–47 | 1 | 3 | 1 | 0 | `SET_VENN_LAB`, `COORDINATE_GRAPH_LAB` |
| `CLASS_9_10` | 3 | বীজগাণিতিক রাশি | 43–74 | 48–79 | 1 | 3 | 2 | 0 | `ALGEBRA_IDENTITY_LAB` |
| `CLASS_9_10` | 4 | সূচক ও লগারিদম | 75–92 | 80–97 | 1 | 3 | 1 | 0 | `ALGEBRA_IDENTITY_LAB` |
| `CLASS_9_10` | 5 | এক চলকবিশিষ্ট সমীকরণ | 93–110 | 98–115 | 1 | 1 | 1 | 0 | `EQUATION_BALANCER_LAB` |
| `CLASS_9_10` | 6 | রেখা, কোণ ও ত্রিভুজ | 111–135 | 116–140 | 1 | 2 | 1 | 1 | `GEOMETRY_CANVAS_LAB` |
| `CLASS_9_10` | 7 | ব্যবহারিক জ্যামিতি | 136–151 | 141–156 | 1 | 1 | 0 | 1 | `GEOMETRY_CANVAS_LAB` |
| `CLASS_9_10` | 8 | বৃত্ত | 152–173 | 157–178 | 1 | 3 | 1 | 2 | `CIRCLE_THEOREM_LAB` |
| `CLASS_9_10` | 9 | ত্রিকোণমিতিক অনুপাত | 174–196 | 179–201 | 1 | 3 | 1 | 0 | `TRIG_UNIT_CIRCLE_LAB` |
| `CLASS_9_10` | 10 | দূরত্ব ও উচ্চতা | 197–204 | 202–209 | 1 | 1 | 1 | 0 | `TRIG_UNIT_CIRCLE_LAB` |
| `CLASS_9_10` | 11 | বীজগাণিতিক অনুপাত ও সমানুপাত | 205–223 | 210–228 | 1 | 3 | 1 | 0 | `ALGEBRA_IDENTITY_LAB` |
| `CLASS_9_10` | 12 | দুই চলকবিশিষ্ট সরল সহসমীকরণ | 224–248 | 229–253 | 1 | 2 | 1 | 0 | `COORDINATE_GRAPH_LAB`, `EQUATION_BALANCER_LAB` |
| `CLASS_9_10` | 13 | সসীম ধারা | 249–265 | 254–270 | 1 | 3 | 1 | 0 | `SERIES_PROGRESSION_LAB` |
| `CLASS_9_10` | 14 | অনুপাত, সদৃশতা ও প্রতিসমতা | 266–284 | 271–289 | 1 | 3 | 1 | 1 | `GEOMETRY_CANVAS_LAB` |
| `CLASS_9_10` | 15 | ক্ষেত্রফল সম্পর্কিত উপপাদ্য ও সম্পাদ্য | 285–293 | 290–298 | 1 | 1 | 0 | 2 | `GEOMETRY_CANVAS_LAB`, `MENSURATION_3D_LAB` |
| `CLASS_9_10` | 16 | পরিমিতি | 294–325 | 299–330 | 1 | 3 | 2 | 0 | `MENSURATION_3D_LAB` |
| `CLASS_9_10` | 17 | পরিসংখ্যান | 326–344 | 331–349 | 1 | 3 | 1 | 0 | `STATISTICS_HISTOGRAM_LAB` |

*(Total: **47 chapters**, **64 lessons**, **94 formulas**, **60 worked examples**, **19 theorems/constructions**.)*

---

## 7. Gated NCTB PDF Verification Log (Structural `Verified` / Visual `Unverified`)

### 7.1 Mandatory 1-Page Preflight Result
- **Target**: `app/src/main/assets/books/class_6.pdf`, PDF page `6` (declared printed page `1` under `pdfPageOffset = 5`).
- **Object Trace**: `/Root → /Pages → /Kids` → Page Obj `32` → `/Resources /XObject /Im1` → Image Obj `29`.
- **Stream Metadata**: `/Subtype /Image`, `/Filter /FlateDecode`, `/ColorSpace /DeviceRGB`, `/BitsPerComponent 8`, `/Width 927`, `/Height 1211`, `/DecodeParms None`, `/Length 46044` → decompressed `3,367,791` RGB bytes (`927 × 1211 × 3`).
- **Extraction**: Cleanly encoded to `/tmp/nctb_preflight/class_6_pdfpage_6.png` (`49,371` bytes) using Python stdlib `zlib` + `struct` (zero packages installed), then deleted cleanly after preflight.
- **Visual Preview Gate**: **`BLOCKED / FAILED`** — `view_file` resolves paths strictly inside workspace root (`/app/applet/`) and rejects `/tmp/...`; placing temporary PNGs inside the workspace is forbidden under read-only audit rules. Per Plan Section 7, visual verification across all 42 deterministic boundary sample pages **retains `Unverified` status**.

### 7.2 Full 4-PDF Page-Tree & XObject Structural Verification (`Verified`)

| PDF Asset | Declared Pages (`BookModels`) | Reachable `/Root→/Pages→/Kids` Pages | 1:1 `/Im1` Image XObject | Clean `/FlateDecode` RGB Decompression (`W×H×3`) | Pages With `/Font` (Text Layer) | Structural Status |
| :--- | :-: | :-: | :-: | :-: | :-: | :-: |
| `app/src/main/assets/books/class_6.pdf` | 161 | **161** | **161 / 161** | **161 / 161 (`0` errors)** | `0` | **`Verified`** |
| `app/src/main/assets/books/class_7.pdf` | 181 | **181** | **181 / 181** | **181 / 181 (`0` errors)** | `0` | **`Verified`** |
| `app/src/main/assets/books/class_8.pdf` | 190 | **190** | **190 / 190** | **190 / 190 (`0` errors)** | `0` | **`Verified`** |
| `app/src/main/assets/books/class_9_10.pdf` | 362 | **362** | **362 / 362** | **362 / 362 (`0` errors)** | `0` | **`Verified`** |
| **TOTAL** | **894** | **894** | **894 / 894** | **894 / 894 (`0` errors)** | **`0` (pure scanned rasters)** | **`Verified`** |

### 7.3 Deterministic 42-Page Boundary Sample Table (Structural Stream `Verified` / Visual Content `Unverified`)

| Book ID | Declared Printed Page | PDF Page | Page Obj | Image Obj | Dimensions (`W×H`) | Compressed `/Length` (B) | Stream Integrity | Visual Inspection Status |
| :--- | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: |
| `CLASS_6` | Front | 1 | 7 | 4 | `962×1216` | 29,193 | `Verified` | **`Unverified`** |
| `CLASS_6` | Front | 5 | 27 | 24 | `906×1188` | 16,621 | `Verified` | **`Unverified`** |
| `CLASS_6` | 1 | 6 | 32 | 29 | `927×1211` | 46,044 | `Verified` | **`Unverified`** |
| `CLASS_6` | 38 | 43 | 217 | 214 | `914×1199` | 32,220 | `Verified` | **`Unverified`** |
| `CLASS_6` | 95 | 100 | 502 | 499 | `906×1188` | 53,010 | `Verified` | **`Unverified`** |
| `CLASS_6` | 100 | 105 | 527 | 524 | `906×1188` | 27,518 | `Verified` | **`Unverified`** |
| `CLASS_6` | 137 | 142 | 712 | 709 | `906×1188` | 44,170 | `Verified` | **`Unverified`** |
| `CLASS_6` | 150 | 155 | 777 | 774 | `906×1188` | 30,553 | `Verified` | **`Unverified`** |
| `CLASS_6` | 155 | 160 | 802 | 799 | `906×1188` | 10,235 | `Verified` | **`Unverified`** |
| `CLASS_6` | 156 | 161 | 807 | 804 | `913×1220` | 16,951 | `Verified` | **`Unverified`** |
| `CLASS_7` | Front | 1 | 7 | 4 | `925×1217` | 32,166 | `Verified` | **`Unverified`** |
| `CLASS_7` | Front | 5 | 27 | 24 | `906×1188` | 21,513 | `Verified` | **`Unverified`** |
| `CLASS_7` | 1 | 6 | 32 | 29 | `1033×1462` | 39,762 | `Verified` | **`Unverified`** |
| `CLASS_7` | 18 | 23 | 117 | 114 | `1033×1462` | 47,848 | `Verified` | **`Unverified`** |
| `CLASS_7` | 89 | 94 | 472 | 469 | `1033×1462` | 41,096 | `Verified` | **`Unverified`** |
| `CLASS_7` | 95 | 100 | 502 | 499 | `1033×1462` | 30,649 | `Verified` | **`Unverified`** |
| `CLASS_7` | 162 | 167 | 837 | 834 | `1033×1462` | 34,045 | `Verified` | **`Unverified`** |
| `CLASS_7` | 170 | 175 | 877 | 874 | `1033×1462` | 43,950 | `Verified` | **`Unverified`** |
| `CLASS_7` | 175 | 180 | 902 | 899 | `1033×1462` | 10,931 | `Verified` | **`Unverified`** |
| `CLASS_7` | 176 | 181 | 907 | 904 | `925×1217` | 16,624 | `Verified` | **`Unverified`** |
| `CLASS_8` | Front | 1 | 7 | 4 | `966×1218` | 45,294 | `Verified` | **`Unverified`** |
| `CLASS_8` | Front | 5 | 27 | 24 | `906×1188` | 21,212 | `Verified` | **`Unverified`** |
| `CLASS_8` | 1 | 6 | 32 | 29 | `1031×1469` | 39,271 | `Verified` | **`Unverified`** |
| `CLASS_8` | 12 | 17 | 87 | 84 | `1031×1469` | 48,440 | `Verified` | **`Unverified`** |
| `CLASS_8` | 97 | 102 | 512 | 509 | `1031×1469` | 42,387 | `Verified` | **`Unverified`** |
| `CLASS_8` | 105 | 110 | 552 | 549 | `1031×1469` | 24,181 | `Verified` | **`Unverified`** |
| `CLASS_8` | 159 | 164 | 822 | 819 | `1031×1469` | 51,239 | `Verified` | **`Unverified`** |
| `CLASS_8` | 175 | 180 | 902 | 899 | `1031×1469` | 25,484 | `Verified` | **`Unverified`** |
| `CLASS_8` | 184 | 189 | 947 | 944 | `1031×1469` | 12,565 | `Verified` | **`Unverified`** |
| `CLASS_8` | 185 | 190 | 952 | 949 | `916×1218` | 16,818 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | Front | 1 | 7 | 4 | `938×1219` | 335,243 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | Front | 5 | 27 | 24 | `906×1188` | 27,464 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 1 | 6 | 32 | 29 | `906×1188` | 49,150 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 21 | 26 | 132 | 129 | `906×1188` | 40,742 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 174 | 179 | 897 | 894 | `906×1188` | 46,863 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 185 | 190 | 952 | 949 | `906×1188` | 22,461 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 326 | 331 | 1657 | 1654 | `906×1188` | 48,395 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 345 | 350 | 1752 | 1749 | `906×1188` | 24,945 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 354 | 359 | 1797 | 1794 | `906×1188` | 28,592 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 355 | 360 | 1802 | 1799 | `906×1188` | 99,815 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 356 | 361 | 1807 | 1804 | `906×1188` | 68,782 | `Verified` | **`Unverified`** |
| `CLASS_9_10` | 357 | 362 | 1812 | 1809 | `938×1219` | 15,354 | `Verified` | **`Unverified`** |

*Limitation Note*: Because all 4 bundled PDFs contain `0` embedded fonts (pure scanned rasters) and container `view_file` cannot render images outside `/app/applet/`, full printed wording and visual page-offset alignment verification remains impossible inside this read-only audit environment and is honestly retained as `Unverified`.

---

## 8. Historical Documentation Reuse & Drift Matrix

| Document Path | Era / Date | Reuse Disposition | Verified Still-Valid Coverage | Verified Drift / Stale Claims |
| :--- | :--- | :--- | :--- | :--- |
| `README.md` | Pre-Phase-3 | Sync in `WP-4.4` | App overview, offline design | Line `8` cites `245/245 (13 suites)` & line `52` cites `215/215 (10 suites)` (actual: `340/340`, `29` suites) |
| `docs/qa/FINAL_REGRESSION_TEST_REPORT.md` | Pre-Phase-3 | Historical record | Early 10-suite baseline | Cites `215/215` across `10` suites (superseded by `29` suites / `340` tests) |
| `docs/database/FINAL_DATABASE_INTEGRITY_REPORT.md` | Room `v9` era | Historical record | Migrations `1→9`, 20 core entities | Cites Room `v9` & `20` entities (actual: Room `v10`, `21` entities, `MIGRATION_9_10`) |
| `docs/qa/FINAL_DEVICE_QA_REPORT.md` | `2026-11-12` | Historical dated guide | 11-point GPU validation checklist | Records `PENDING PHYSICAL HARDWARE` as of `2026-11-12`; superseded by later user-confirmed Phase 3 physical device QA |
| `docs/audits/FINAL_MATHEMATICAL_ACCURACY_REPORT.md` | Pre-Phase-3 | Reused baseline | Core deterministic engine math | Missed `F4-09` global `"1"`/`"0"` boolean conflation in `SharedMathEngine.isAnswerCorrect` |
| `docs/audits/MATHEMATICAL_ENGINE_VALIDATION_REPORT.md` | Pre-Phase-3 | Reused baseline | Solver/number-system formulas | Same `F4-09` gap |
| `docs/book/BOOK_CONTENT_MANIFEST.md` & `BOOK_NCTB_SOURCE_AND_CONTENT_AUDIT.md` | `2026-11-07` | Reused baseline | 47-chapter NCTB structure | Did not flag `F4-08` unattributed modulo fallback in `BookReaderAndLabsScreens.kt:691` |
| `docs/audits/OFFLINE_DEPENDENCY_AUDIT.md` | Pre-Phase-3 | Reused (still valid) | Zero permissions, zero cloud SDKs | Verified still 100% accurate at commit `18c7b22558ebfd6eb317768ea9078fca2bc2055c` |
| `docs/audits/FINAL_NAVIGATION_STATE_RESTORATION_AUDIT.md` | Phase 2 | Reused (still valid) | Scroll/nav restoration patterns | Verified still passing (`ApplicationWideScrollRestorationUnitTest` 10/10, `Phase2StateRestorationUnitTest` 8/8) |

---

## 9. Pre-Audit & Post-Audit SHA-256 Source Integrity Verification

### 9.1 Audited `.aistudio/` Inventory & Exclusion Policy
A read-only inventory of `.aistudio/` confirmed it contains **only two runtime AI Studio Plan Mode artifact files** and zero user-authored source/config files:
- `.aistudio/artifacts/brain/222a7ea8-e0d3-4357-a4b3-fd9217c6aa67/implementation_plan.md`
- `.aistudio/artifacts/brain/222a7ea8-e0d3-4357-a4b3-fd9217c6aa67/implementation_plan.md.metadata.json`

**Hashed Protected File Scope (`208` files total)**:
- All `206` GitHub-committed user-authored files: Kotlin production sources (`app/src/main/java/**`), XML resources (`app/src/main/res/**`), `app/src/main/AndroidManifest.xml`, unit & instrumented tests (`app/src/test/**`, `app/src/androidTest/**`), Gradle configs & version catalogs (`build.gradle.kts`, `app/build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`, `gradle/wrapper/gradle-wrapper.properties`, `app/proguard-rules.pro`), workspace metadata (`metadata.json`, `.env.example`, `.gitignore`, `app/.gitignore`), bundled NCTB PDF assets (`app/src/main/assets/books/*.pdf`), and all existing user-authored documentation (`README.md`, `MATH_MASTER_*`, `docs/**`, `RELEASE_APK/**`).
- Both local Android signing artifacts (`debug.keystore`, `debug.keystore.base64`) verified via secret-safe metadata & SHA-256 digest only.

**Explicitly Documented Unavoidable Exclusions (Existing Contents Preserved Untouched)**:
- `.gradle/`, `build/`, `app/build/`, `.build-outputs/` — generated Gradle build outputs, caches, and JUnit XML results written by `gradle :app:testDebugUnitTest`.
- `.aistudio/artifacts/brain/222a7ea8-e0d3-4357-a4b3-fd9217c6aa67/implementation_plan.md` and `implementation_plan.md.metadata.json` — platform-managed runtime Plan Mode artifacts.

### 9.2 Secret-Safe Signing Artifact Metadata (Contents & Base64 NEVER Exposed)

| Relative Path | Pre-Audit Size (B) | Pre-Audit UTC mtime | Pre-Audit SHA-256 | Post-Audit Status |
| :--- | :-: | :--- | :--- | :-: |
| `debug.keystore` | `2650` | `2026-11-19T05:06:29.497614+00:00` | `8781b7b6acfee336d912dbda7c983937751e2e4cb0a8c6ca06dbca1b2f16744b` | **`UNCHANGED` (size, mtime & SHA-256 match)** |
| `debug.keystore.base64` | `3536` | `2026-11-19T05:49:16.678003+00:00` | `3ee13ed444d3d9fe5a49c96d25ef20d278bce0d38f6e837f3207a71046dbe1c2` | **`UNCHANGED` (size, mtime & SHA-256 match)** |

### 9.3 Post-Audit SHA-256 Diff Result (`2026-11-19T06:08:46.669734+00:00`)

| Integrity Check | Verified Count / Result | Status |
| :--- | :---: | :-: |
| **Pre-Audit Protected Manifest Files** | `208` (`206` GitHub-committed + `2` signing artifacts) | `PASS` |
| **Post-Audit Protected Files Byte-Identical** | **`208 / 208` (`100.0%`)** | **`PASS`** |
| **Protected Files Modified** | **`0`** | **`PASS`** |
| **Protected Files Deleted** | **`0`** | **`PASS`** |
| **Unapproved Files Created** | **`0`** | **`PASS`** |
| **Approved Audit Reports Created** | **`4`** (`PHASE_3_FINAL_CLOSURE_REVIEW.md`, `PHASE_4_AUDIT_MASTER_REPORT.md`, `PHASE_4_IMPLEMENTATION_ROADMAP.md`, `PHASE_4_AUDIT_EVIDENCE.md`) | **`PASS`** |

#### Authorized Report File Digests (Post-Creation Snapshot)

| Approved Deliverable | Size (Bytes) | SHA-256 Digest |
| :--- | :-: | :--- |
| `PHASE_3_FINAL_CLOSURE_REVIEW.md` | `11,372` | `521c863c25eb7a0737d34cac6bd994250404e7b5abfcf3d7f5ec60073a67937a` |
| `PHASE_4_AUDIT_MASTER_REPORT.md` | `69,277` | `dad0c83fe0884ef4ffe514374ae53548452a5e695504b26d08d40e42f218269f` |
| `PHASE_4_IMPLEMENTATION_ROADMAP.md` | `28,081` | `998fdcca7addc3cba72555d653c1c9ecebbd68c3a77573d32b48ea6a71573532` |
| `PHASE_4_AUDIT_EVIDENCE.md` | *(self-updated with this table)* | *(re-verified unchanged protected set after final save)* |

