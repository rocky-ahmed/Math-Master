# Math Master — Phase 4 Implementation Roadmap (Post-Goal 4.0)

**Roadmap Basis**: Confirmed defects verified during Phase 4 Goal 4.0 (`PHASE_4_AUDIT_MASTER_REPORT.md`)  
**Target Baseline**: GitHub `rocky-ahmed/Math-Master` (`main` @ `18c7b22558ebfd6eb317768ea9078fca2bc2055c`) / Authoritative AI Studio Workspace  
**Status**: **PLANNING DELIVERABLE ONLY** — Zero production, test, Gradle, Room, or asset modifications have been made during Goal 4.0. Execution of `WP-4.1`–`WP-4.4` requires explicit user authorization in a subsequent implementation turn.

---

## 1. Non-Negotiable Architecture, Preservation & Safety Guardrails

1. **Frozen Study Timer Boundary (STRICTLY READ-ONLY across all WPs)**:
   - Never modify `StudySessionTimerCard.kt:1–549`, Home mount (`HomeScreen.kt:114,263–273`), ViewModel Study Timer & outbox journal block (`MathViewModel.kt:222–265,372–373,403,443,3388,4634–5255`), `MathRepository.kt:363–400`, `Entities.kt:473–500`, `MathDatabase.kt:471–490` (`MIGRATION_9_10`), or `MathDao.kt:426–484`.
   - Frozen regression suites (`StudyTimerCompactLayoutRegressionTest` `3/3` and `StudyTimerCrashSafePersistenceUnitTest` `11/11`) must remain green after every Work Package.
2. **Room Schema Stability (`v10` Preserved)**:
   - All four Work Packages (`WP-4.1` through `WP-4.4`) require **ZERO Room schema changes** — Room version `10`, all `21` entities, all `9` migrations (`1→2`..`9→10`), and all `10` existing Phase 3 `@Transaction` DAO methods remain preserved intact.
3. **Lock-Ordering Invariant (`dailyStudyWriteMutex`)**:
   - Preserve the Phase 3 lock hierarchy (`dailyStudyWriteMutex` acquired outside Room `@Transaction` calls; never acquire `dailyStudyWriteMutex` from inside a Room transaction or reverse lock order with `examPersistenceMutex`).
4. **Non-Destructive Historical User Data Safety**:
   - Never delete, truncate, or wipe existing user rows (`bcs_study_plan_goals`, `unified_mistake_notebook`, `bcs_mistakes`, `book_mistakes`, `book_spaced_revision`, `daily_study_activity`, `quiz_scores`, `practice_history`). Legacy duplicate records (e.g., `F4-03` `"QUIZ_..."` BCS twins) are reconciled non-destructively on resolution.
5. **Provisional Scope Adjustments Applied from Goal 4.0 Evidence**:
   - **Removed Disproven Claim**: Producible bookmark category routing (`F4-10A`) was disproven (`8/8` producible categories already route properly in `BookmarksAndTutorScreens.kt:134–166`). Replaced in `WP-4.4` with only a lightweight 1-line defensive `else` fallback.
   - **Promoted Severity**: `F4-09` (`SharedMathEngine.isAnswerCorrect`) confirmed as **`P1 — High`** (numeric answers `"0"`/`"1"` falsely accept `"মিথ্যা"`/`"সত্য"` across 5+ modules); `F4-05` BCS concurrent RMW (`recordBcsStudyGoalDelta` & `recordBcsTopicProgress`) confirmed as **`P1 — High`**.
   - **Added Confirmed Secondary Defects**: `DashboardScreen.kt:419–432` 7-day circle contrast & time-only activity mismatch (`WP-4.3` / `F4-07`) and `ProblemSolverPedagogyComponents.kt:1024` single-char substring match (`WP-4.4` / `F4-09`).

---

## 2. Work Package Dependency & Execution Order

```text
WP-4.1 (P1 Session Progression, BCS Goals & Mistake Consistency: F4-01, F4-02, F4-03, F4-04)
   │
   ▼  [F4-02 & F4-03 clarify BCS goal/mistake callers before WP-4.2 hardens BCS RMW & Quiz finish]
WP-4.2 (P1 Concurrency, BCS RMW Safety & Quiz Ordering: F4-05, F4-06)
   │
   ▼  [Independent UI/Analytics & Textbook Provenance Layer]
WP-4.3 (P2 Streak Semantics, Dashboard Strip & NCTB Provenance: F4-07, F4-08)
   │
   ▼  [Independent Math Validation, Minor KDoc & Doc Sync Layer]
WP-4.4 (P1 Context-Aware Validation & P3 KDoc/Docs Cleanup: F4-09, F4-10)
```

*(Note: `WP-4.4`'s `F4-09` math validation fix has zero dependency on `WP-4.1`–`WP-4.3` and may optionally be executed immediately after `WP-4.1` if prioritizing `P1` fixes ahead of `P2` items.)*

---

## 3. Work Package Details

---

### `WP-4.1` — Session Progression, Completion Idempotency, BCS Goal Integrity & Mistake Consistency

- **Covered Confirmed Findings**: `F4-01` (`P1`), `F4-02` (`P1`), `F4-03` (`P1`), `F4-04` (`P1`)
- **Dependencies**: None (executes directly on verified Phase 3 baseline)
- **Exact Affected Files**:
  - `app/src/main/java/com/example/ui/MathViewModel.kt` (`:1505–1546`, `:1674–1914`, `:3321–3369`, `:3896–4061`, `:4524–4528`)
  - `app/src/main/java/com/example/data/db/MathDao.kt` (`:640–684`, `:698–796`, `:1036–1144`)
  - `app/src/main/java/com/example/data/repository/MathRepository.kt` (`:724–734`, `:1008–1032`)
  - `app/src/test/java/com/example/Phase3DatabaseTransactionAndHistoryTest.kt` (`:357–358` — update expected canonical row count from `3` to `2`)
  - New/extended unit test file: `app/src/test/java/com/example/Phase4SessionAndMistakeIntegrityTest.kt`

#### Minimal Architecture-Preserving Fixes
1. **`F4-01` (Number System Progression & CAS Guard)**:
   - At `MathViewModel.kt:3321` (`nextNumberSystemQuestion()`): add `if (!current.isSubmitted || current.isCompleted) return` guard.
   - On the final question branch (`:3330–3368`): claim completion atomically via a `while (true) { ... _numberSystemPracticeState.compareAndSet(cur, cur.copy(isCompleted = true)) }` loop **before** launching persistence, matching existing Phase 3 CAS patterns.
2. **`F4-02` (BCS Study Goal Recording at Actual Completion)**:
   - Remove premature `repository.recordBcsStudyGoalDelta(...)` calls from `startBcsModeExam()` (`:3974–3981`) and `startBcsMockExam()` (`:4052–4059`).
   - Inside `finishQuizSession()` (`:1674–1914`, after winning CAS completion ownership for a BCS session): compute `attemptedCount = correctCount + wrongCount`; if `attemptedCount > 0`, invoke `repository.recordBcsStudyGoalDelta(...)` with:
     - `questionsDelta = attemptedCount`
     - `studyTimeMinutesDelta = (elapsedSeconds / 60).coerceAtLeast(1)`
     - `mockExamsDelta = if (isFullMockExamPreset) 1 else 0` (never `1` for untimed `TOPIC_PRACTICE` or `MISTAKE_PRACTICE`)
     - `revisionsDelta = if (isMistakePracticePreset) 1 else 0`
   - Ensure `recordBcsMistake()` (`:3867`) does not double-count `questionsDelta` when invoked during an active BCS quiz/exam session whose total `attemptedCount` is credited on completion.
3. **`F4-03` (Canonical Single BCS Unified Mistake Key + Non-Destructive Legacy Reconciliation)**:
   - For BCS questions in both `submitQuizAnswer()` (`:1513–1536`) and `finishQuizSession()` (`:1851–1902`), stop inserting the duplicate `"QUIZ_${q.topicTitleBn}_${abs(q.questionBn.hashCode())}"` `UnifiedMistakeEntity`; rely exclusively on the canonical profile-scoped `"BCS_${safeProfile}_$rawQId"` row managed by `recordBcsMistakeTransaction()` and `finalizeExamAttemptTransaction()`.
   - On correct BCS answers, resolve the canonical `"BCS_${safeProfile}_$rawQId"` row cleanly.
   - **Non-Destructive Legacy Reconciliation**: Inside `markBcsMistakeResolvedTransaction()` (`MathDao.kt:766–796`), after resolving `"BCS_${safeProfile}_$rawQId"` and `"BCS_$rawQId"`, also mark any pre-existing legacy `"QUIZ_..."` row where `sourceModule = 'BCS'` and `questionPromptBn = :questionText` as `resolved = 1` without deleting any historical row. When Dashboard resolves a `"BCS_..."` unified mistake, synchronize `bcs_mistakes.isResolved = true`.
   - Update `Phase3DatabaseTransactionAndHistoryTest.kt:357–358` to assert canonical unified row count `2` (1 non-BCS + 1 canonical BCS) instead of `3`.
4. **`F4-04` (Eliminate Double Book Practice Unified Spaced-Repetition Advancement)**:
   - Remove the duplicate `repository.resolveUnifiedMistake("BOOK_${winningQ.id}")` call at `MathViewModel.kt:4527` so `markBookMistakeResolved("book_mistake_${winningQ.id}", resolved = true)` (`MathDao.markBookMistakeResolvedTransaction`) advances `book_spaced_revision.intervalStage` once and `unified_mistake_notebook.reviewIntervalDays` **once**.
   - Ensure `markBookMistakeResolvedTransaction()` still invokes `resolveUnifiedMistakeInsideTransaction("BOOK_$rawId", nowMs)` safely when a unified row exists even if `book_mistakes` has no row, and stop overwriting `lastFailedAtMillis = nowMs` in `MathRepository.resolveUnifiedMistake()` (`:1029`).

#### Required Regression Tests (`WP-4.1`)
- `F4-01`: Unsubmitted `nextNumberSystemQuestion()` no-op test; final-question rapid double/concurrent invocation test asserting exactly `1` `QuizScoreEntity`, `1` `PracticeHistoryEntity`, and `1` `DailyStudyActivityEntity` delta.
- `F4-02`: Launch + abandon BCS Mock / Topic / Mistake sessions → assert `0` deltas in `bcs_study_plan_goals`; complete Mock Exam with 10/15 answered → assert `mockExamsCompleted == 1`, `questionsSolved == 10`; complete Topic Practice → assert `mockExamsCompleted == 0`, `questionsSolved == attempted`.
- `F4-03`: Wrong BCS answer in immediate-feedback & full-exam modes → assert exactly `1` `UnifiedMistakeEntity` (`"BCS_${safeProfile}_$rawQId"`) and `1` `BcsMistakeEntity`; resolve from BCS Mistake Notebook → `0` unresolved unified rows; seeded legacy `"QUIZ_..."` + `"BCS_..."` pair reconciled non-destructively (both preserved, both `resolved == true`).
- `F4-04`: ViewModel `submitBookPracticeAnswer()` correct retry on existing Book Practice mistake → assert `book_spaced_revision.intervalStage == 1` (single step) AND `unified_mistake_notebook.reviewIntervalDays == 3` (single step, NOT `7`), with original `lastFailedAtMillis` preserved.

#### Acceptance Criteria & Physical-Device Verification (`WP-4.1`)
- **Automated**: `gradle :app:testDebugUnitTest` passes `100%` (all 29 existing suites + new `WP-4.1` tests, including frozen Study Timer suites `14/14`).
- **Physical Device Verification**:
  1. Open Number System Practice, tap Next before submitting (stays on current question), submit last question and double-tap Finish rapidly → verify Dashboard increments by 1 session only.
  2. Open BCS Mock Exam and immediately press Back → verify BCS Study Planner shows `0` mock exams, `0` questions, `0` minutes added. Complete a 5-question BCS Topic Practice → verify `questionsSolved +5`, `mockExamsCompleted +0`.
  3. Intentionally answer 1 BCS question wrong → verify Dashboard & Smart Tutor show **1** mistake (not 2); resolve it in BCS Mistake Notebook → verify it clears immediately from Dashboard.
  4. Miss 1 Book Practice question, then retry it correctly once → verify review interval shows 3 days (not 7 days).

---

### `WP-4.2` — BCS Concurrent Read-Modify-Write Safety, `deleteExamAttempt` Serialization, Session Atomicity & Quiz Ordering

- **Covered Confirmed Findings**: `F4-05` (`P1`), `F4-06` (`P1`)
- **Dependencies**: Execute after `WP-4.1` so `F4-02` & `F4-03` caller paths in `finishQuizSession()` and `submitQuizAnswer()` are settled first.
- **Exact Affected Files**:
  - `app/src/main/java/com/example/data/repository/MathRepository.kt` (`:305–310`, `:805–869`, `:922–958`)
  - `app/src/main/java/com/example/data/db/MathDao.kt` (`:492–566` optional `QuizScoreEntity?`, plus narrowest safe transactional RMW methods for BCS goals/topic progress if established by diagnostic tests)
  - `app/src/main/java/com/example/ui/MathViewModel.kt` (`:1456–1551`, `:1674–1914`, `:2483–2508`, `:2853–2878`, `:3338–3362`, `:4593–4617`)
  - New test file: `app/src/test/java/com/example/Phase4ConcurrencyAndAtomicityTest.kt`

#### Diagnostic-First Methodology for BCS Concurrent RMW (`F4-05A`)
Per approved Goal 4.0 governance, **do NOT preemptively introduce a new mutex or duplicate DAO transaction before proving the exact failure mode**:
1. **Step 1 (Write & Run Diagnostic Concurrency Tests First)**:
   - Seed an existing `BcsStudyPlanGoalEntity` row in Room (`goalKey = "${profile}_$dateKey"`, e.g., `questionsSolved = 5, studyTimeMinutes = 10, mockExamsCompleted = 1`) and launch `N` (e.g., 16) parallel coroutines on `Dispatchers.IO` calling `repository.recordBcsStudyGoalDelta(...)` for the same `goalKey` (plus parallel writes for a second `examProfileId` to verify strict profile isolation). Assert final counters equal exact initial + sum of all `N` deltas with zero lost updates.
   - Seed an existing `BcsTopicProgressEntity` row in Room (`topicId = "${profile}_$topic"`, e.g., `attemptedQuestions = 4, correctQuestions = 3, totalTimeSeconds = 60`) and launch `N` parallel coroutines calling `repository.recordBcsTopicProgress(...)` (mixing question/time increments, `markGuideCompleted = true`, `miniCheckPassed = true`) alongside concurrent `finalizeExamAttempt()` updates on the same profile/topic (plus a second profile for isolation). Assert every accepted delta and sticky flag is preserved **exactly once**.
2. **Step 2 (Apply Narrowest Safe Correction)**:
   - Once diagnostic tests demonstrate lost updates under unlocked `get → compute → upsert`, implement the **narrowest safe correction** that preserves `dailyStudyWriteMutex` lock ordering and all 10 existing DAO transactions:
     - Move the read-modify-write inside Room `@Transaction` DAO methods (`recordBcsStudyGoalDeltaTransaction` / `recordBcsTopicProgressTransaction`) serialized by SQLite's write lock (or paired with an independent, non-nested `bcsWriteMutex` ONLY if Robolectric/SQLite multi-connection contention still reproduces lost updates under `@Transaction` alone; never acquire `dailyStudyWriteMutex` inside it or vice versa).
     - Ensure `finalizeExamAttemptTransaction()` reuses the exact same topic-progress merge logic without changing `dailyStudyWriteMutex` ordering.
3. **`F4-05B` (`deleteExamAttempt` Serialization)**:
   - Wrap `MathRepository.deleteExamAttempt()` (`:305–310`) in `dailyStudyWriteMutex.withLock { dao.deleteExamAttemptSafely(attemptId, dateKey) }`, matching `recordDailyStudyDelta()`, `updateDailyStudyTargets()`, `recordSessionAndAnalytics()`, and `finalizeExamAttempt()`.
4. **`F4-05C` (Atomic Session Completion Consolidation)**:
   - Extend existing `MathDao.recordSessionAndAnalyticsTransaction()` (`:492`) with an optional `quizScore: QuizScoreEntity? = null` parameter (preserving backward compatibility and avoiding a duplicate transaction method), and pass the session's `QuizScoreEntity` from `finishPracticeSessionNow()`, `finishMentalMathSession()`, `nextNumberSystemQuestion()`, and `finishBookPracticeSession()` so score + history + analytics + daily study activity commit atomically in one transaction.
   - *(Low-risk two-table metadata writes like `toggleBookPageBookmark` / `recordFormulaMastery` may optionally be wrapped in `@Transaction` only if clean and zero-risk.)*
5. **`F4-06` (Immediate-Feedback Quiz Ordering & Double-Resolution Fix)**:
   - Add the missing `if (!winningCurrent.isFullExamMode && winningCurrent.userAnswers.containsKey(index))` guard to the correct-answer resolution branch in `finishQuizSession()` (`MathViewModel.kt:1884–1901`) so already-submitted correct answers are never resolved twice.
   - Track in-flight immediate-feedback persistence jobs launched by `submitQuizAnswer()` (`:1513`) — or execute them sequentially under `examPersistenceMutex` — so `finishQuizSession()` awaits (`join()`s) any pending submit write inside `examPersistenceMutex` before running `finalizeExamAttemptTransaction()`, preventing Submit→Finish races or dropped writes on fast taps.

#### Required Regression Tests (`WP-4.2`)
- Seeded-row parallel concurrency tests for `recordBcsStudyGoalDelta()` (exact delta sum + profile isolation + cancellation boundary).
- Seeded-row parallel concurrency tests for `recordBcsTopicProgress()` + concurrent `finalizeExamAttempt()` (exact counts, sticky flags, profile isolation).
- Concurrent `deleteExamAttempt()` + `recordDailyStudyDelta()` test (zero lost updates, no resurrected deleted exam metrics).
- Immediate-feedback Quiz (`isFullExamMode = false`):
  - Submit correct answer on existing mistake + Finish → assert `reviewIntervalDays` advances **once** (`1 → 3`, not `7`).
  - Rapid Submit(wrong) → Finish → assert mistake persisted cleanly and deterministically without loss or duplication.
- Fault-injection rollback test confirming `quiz_scores` is not left orphaned if `recordSessionAndAnalyticsTransaction()` fails.

#### Acceptance Criteria & Physical-Device Verification (`WP-4.2`)
- **Automated**: All diagnostic concurrency tests pass deterministically across repeated runs; `gradle :app:testDebugUnitTest` passes `100%` (including frozen Study Timer `14/14`).
- **Physical Device Verification**:
  1. Complete a BCS topic guide + mini-check rapidly back-to-back → verify BCS Study Planner reflects every lesson, question, and minute accurately after app restart.
  2. Delete an exam attempt from Exam History immediately after finishing a short practice session → verify Dashboard daily totals remain consistent after cold restart.
  3. In Immediate-Feedback Quiz mode, retry a previously wrong question correctly and finish → verify review schedule advances one stage only.

---

### `WP-4.3` — Analytics/Streak Semantics, Dashboard Strip Contrast & NCTB Page Companion Provenance

- **Covered Confirmed Findings**: `F4-07` (`P2`), `F4-08` (`P2`)
- **Dependencies**: None (independent UI/Analytics & Textbook layers)
- **Exact Affected Files**:
  - `app/src/main/java/com/example/ui/MathViewModel.kt` (`:362–369`, `:402`, `:3003–3090`)
  - `app/src/main/java/com/example/ui/screens/DashboardScreen.kt` (`:419–432`)
  - `app/src/main/java/com/example/math/BookModels.kt` (`:594–608`)
  - `app/src/main/java/com/example/math/BookCurriculumRepository.kt` (`:39–152`)
  - `app/src/main/java/com/example/ui/screens/BookReaderAndLabsScreens.kt` (`:640–801`)
  - `app/src/test/java/com/example/ExampleRobolectricTest.kt` (`:155–162` — update streak expectation to match honest zero-study initial state)
  - New/extended unit test file: `app/src/test/java/com/example/Phase4AnalyticsAndBookProvenanceTest.kt`

#### Minimal Architecture-Preserving Fixes
1. **`F4-07` (Honest Study Streak vs. App Visit Streak + Dashboard 7-Day Strip Fix)**:
   - Stop `initStreakAndActivity()` (`MathViewModel.kt:3003–3054`) from writing synthetic study activity `act_$today = 1` or setting `streak_current_days = 1` on cold launch with zero study. Clearly separate **App Visit** tracking (if retained, under distinct `app_visit_*` keys) from **Actual Study Streak** (derived from actual recorded study activity, starting at `0` on fresh install with zero completed questions/study time). Update `ExampleRobolectricTest.kt:158–159` to assert `0` study streak before study and `>= 1` after `recordDailyActivity(...)`.
   - Fix `DashboardScreen.kt:419–432`:
     ```kotlin
     val hasStudyActivity = dayPt.questionsAttempted > 0 || dayPt.studyTimeSeconds > 0
     val circleBg = when {
         dayPt.goalMet -> Color(0xFF2E7D32)
         hasStudyActivity -> MaterialTheme.colorScheme.primary
         else -> MaterialTheme.colorScheme.surfaceContainerHigh
     }
     val circleTextColor = if (dayPt.goalMet || hasStudyActivity) Color.White else MaterialTheme.colorScheme.onSurface
     ```
2. **`F4-08` (NCTB Page Companion Provenance Attribution)**:
   - Add `enum class BookCompanionAttribution { PAGE_PINNED, LESSON_REFERENCE, CHAPTER_FALLBACK }` to `BookModels.kt` and add defaulted fields (`formulaAttribution`, `workedExampleAttribution`, `theoremAttribution`) to `StructuredTextbookPageContent` (preserving source compatibility with all existing callers/tests).
   - In `BookCurriculumRepository.getStructuredPageContent()` (`:39–152`), populate the exact attribution enum based on whether each item matched `printedPageRef == printedPage`, matched the active `BookLessonSection` range, or used chapter-level modulo/`take(1)` fallback — **while preserving non-empty companion lists** so `EcosystemUpgradeV7UnitTest.kt:883–889` remains green.
   - In `BookReaderAndLabsScreens.kt` (`:691`, `:727–785`), render truthful Bengali headings and small provenance chips (`"এই পৃষ্ঠার সূত্র/উদাহরণ"` only for `PAGE_PINNED`; `"পাঠভিত্তিক সহায়ক"` for `LESSON_REFERENCE`; `"অধ্যায়ভিত্তিক সহায়ক (রিভিশন)"` for `CHAPTER_FALLBACK`).

#### Required Regression Tests (`WP-4.3`)
- `F4-07`: Cold-start `MathViewModel` with empty DB/SharedPreferences asserts `0` study streak and `0` today study activity; completing study activity advances streak to `1`; Dashboard Compose test verifying time-only goal-met day (`questionsAttempted = 0, studyTimeSeconds = 1800, goalMet = true`) renders white text on dark green and time-only active day (`studyTimeSeconds = 300, goalMet = false`) renders active primary tint.
- `F4-08`: Unit tests across Class 6–10 verifying: (a) page with exact `printedPageRef` reports `PAGE_PINNED`; (b) unpinned page using modulo fallback reports `CHAPTER_FALLBACK` (never mislabeled as page-pinned); (c) existing `EcosystemUpgradeV7UnitTest` and `PrincipalAuditBug94To97RegressionTest` invariants remain `100%` passing.

#### Acceptance Criteria & Physical-Device Verification (`WP-4.3`)
- **Automated**: `gradle :app:testDebugUnitTest` passes `100%`.
- **Physical Device Verification**:
  1. Fresh install (or cleared app storage): open Dashboard without studying → verify Current Streak is `০ দিন` (not `১`). Complete a 1-minute Study Timer session (with `0` questions) → verify today's 7-day strip circle highlights cleanly with high-contrast white text.
  2. Open NCTB Class 6–10 Book Reader Hybrid Text Companion on both a worked-example page and an unpinned chapter page → verify provenance labels distinguish page-pinned vs. chapter-level companion items accurately.
  3. **Optional Human/Device NCTB Visual Boundary Audit (Resolves `F4-08` `Unverified` Status)**: Visually spot-check the 42 deterministic boundary pages listed in `PHASE_4_AUDIT_EVIDENCE.md §7` on-device in Book Reader PDF view (confirming cover, `pdfPageOffset = 5` printed page 1 alignment, chapter starts, mid-chapter, answer key start/end, and back cover for `class_6.pdf`, `class_7.pdf`, `class_8.pdf`, and `class_9_10.pdf`).

---

### `WP-4.4` — Context-Aware Answer Validation, Defensive Hardening, KDoc & Documentation Synchronization

- **Covered Confirmed Findings**: `F4-09` (`P1`), `F4-10B` (`P3`), `F4-10C` (`P3`), plus optional 1-line defensive hardening for `F4-10A`
- **Dependencies**: None (can be executed independently or right after `WP-4.1` given `F4-09`'s `P1` severity)
- **Exact Affected Files**:
  - `app/src/main/java/com/example/math/SharedMathEngine.kt` (`:410–441`)
  - `app/src/main/java/com/example/math/BookQuestionEngine.kt` (`:395–399` — review/align `0.01` vs. `1e-4` numeric tolerance)
  - `app/src/main/java/com/example/ui/components/ProblemSolverPedagogyComponents.kt` (`:1023–1025` — tighten substring `contains` check)
  - `app/src/main/java/com/example/ui/screens/BookmarksAndTutorScreens.kt` (`:134–166` — optional 1-line defensive `else` fallback only)
  - `app/src/main/java/com/example/math/SquareCubeModels.kt` (`:5–6, 17` — KDoc fixes only)
  - `README.md`, `docs/qa/FINAL_REGRESSION_TEST_REPORT.md`, `docs/database/FINAL_DATABASE_INTEGRITY_REPORT.md` (sync verified test/schema counts and cross-link Phase 3/4 reports)
  - New/extended unit test file: `app/src/test/java/com/example/Phase4AnswerValidationTruthTableTest.kt`

#### Minimal Architecture-Preserving Fixes
1. **`F4-09` (Context-Aware Numeric & Boolean Validation)**:
   - Remove `"1"` and `"0"` from `trueTokens` and `falseTokens` in `SharedMathEngine.kt:417–420`:
     - `val trueTokens = setOf("true", "সত্য", "হ্যাঁ", "yes", "ঠিক")`
     - `val falseTokens = setOf("false", "মিথ্যা", "না", "no", "ভুল")`
   - Keep `isAnswerCorrect(userInput, expectedAnswer)` backward-compatible while ensuring numeric expected answers (`"0"`, `"1"`, `"০"`, `"১"`) are validated strictly numerically (exact/fraction/epsilon) and NEVER match boolean words, while boolean expected answers (`"সত্য"`, `"মিথ্যা"`, `"true"`, `"false"`, etc.) continue matching all valid Bengali/English boolean synonyms.
   - Optionally expose `enum class AnswerValidationMode { AUTO, NUMERIC_ONLY, BOOLEAN_ONLY }` overload for explicit call sites (`QuizModels`, `ExamEngineModels`, `PracticeLab`, `NumberSystem`, `BookQuestionEngine`).
   - Tighten `ProblemSolverPedagogyComponents.kt:1024` so numeric expected answers do not match trivial 1-character substrings (`expected.contains(attempt)` only for non-numeric multi-word descriptive hints with minimum meaningful length, or strict `SharedMathEngine.isAnswerCorrect`).
   - Audit `BookQuestionEngine.kt:395` (`0.01` tolerance) and tighten to `1e-4` (unless a specific rounding-to-2-decimal textbook question requires `0.01`, in which case document and scope it explicitly).
2. **`F4-10` (KDoc, Defensive Fallback & Historical Documentation Sync)**:
   - Fix `SquareCubeModels.kt:5` (`1 to 20`), `:6` (`বর্গ (Square)`), and `:17` (`1 to 10` / `1 to 20`).
   - Add a 1-line defensive `else -> onNavigate(Screen.FormulaLibrary)` (or safe snackbar) in `BookmarksAndTutorScreens.kt:166` without modifying any of the 8 verified working category branches.
   - Update `README.md` (`:8` & `:52`) to report unified, verified post-Phase-4 suite/test counts and Room `v10` / `21` entities / `9` migrations, and add accurate supersession banners to stale `/docs/**` summaries while preserving historical audit logs intact.

#### Required Regression Tests (`WP-4.4`)
- Complete `SharedMathEngine.isAnswerCorrect` truth-table test suite (`PHASE_4_AUDIT_EVIDENCE.md §4`):
  - Must return **`false`**: `("সত্য", "1")`, `("true", "1")`, `("হ্যাঁ", "১")`, `("yes", "1")`, `("ঠিক", "1")`, `("মিথ্যা", "0")`, `("false", "0")`, `("না", "০")`, `("no", "0")`, `("ভুল", "0")`, `("1", "সত্য")`, `("0", "মিথ্যা")`, `("১", "true")`, `("০", "false")`.
  - Must return **`true`**: `("1", "1")`, `("১", "1")`, `("1.0", "১")`, `("0", "0")`, `("০", "0")`, `("সত্য", "true")`, `("হ্যাঁ", "সত্য")`, `("মিথ্যা", "false")`, `("না", "মিথ্যা")`, `("1/2", "0.5")`, `("০.৫", "1/2")`.
- Pedagogy step test asserting typing `"1"` does NOT pass expected numeric answer `"125"`.

#### Acceptance Criteria & Physical-Device Verification (`WP-4.4`)
- **Automated**: `gradle :app:testDebugUnitTest` passes `100%` (including `Phase1ValidationAndIntegrityUnitTest` and all new truth-table tests).
- **Physical Device Verification**:
  1. In Quiz / Practice Lab / Number System / Book Practice numerical input for a question with answer `1` or `0`, type `"সত্য"` or `"মিথ্যা"` → verify it is graded **Incorrect**, while `"১"`/`"1"` and `"০"`/`"0"` are graded **Correct**.
  2. On a True/False question, select `"সত্য"` / `"মিথ্যা"` → verify accurate grading.
  3. Open Bookmarks and tap bookmarks from all 8 categories → verify every route opens cleanly.

---

## 4. Roadmap Summary Matrix

| Work Package | Findings | Highest Severity | Room Schema Bump? | Frozen Study Timer Touched? | Existing Tests Requiring Expected-Value Update |
| :---: | :--- | :-: | :-: | :-: | :--- |
| **`WP-4.1`** | `F4-01`, `F4-02`, `F4-03`, `F4-04` | `P1 — High` | **No (`v10`)** | **No** | `Phase3DatabaseTransactionAndHistoryTest.kt:357–358` (`3 → 2`) |
| **`WP-4.2`** | `F4-05` (incl. BCS RMW), `F4-06` | `P1 — High` | **No (`v10`)** | **No** | None (new concurrency & ordering tests added) |
| **`WP-4.3`** | `F4-07`, `F4-08` | `P2 — Medium` | **No (`v10`)** | **No** | `ExampleRobolectricTest.kt:158–159` (`>= 1 → 0` pre-study) |
| **`WP-4.4`** | `F4-09`, `F4-10` (KDoc + docs + defensive `else`) | `P1 — High` (`F4-09`) | **No (`v10`)** | **No** | None (verify `Phase1ValidationAndIntegrityUnitTest` stays green) |
