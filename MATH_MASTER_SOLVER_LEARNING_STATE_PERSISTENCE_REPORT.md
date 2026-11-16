# MATH MASTER — MATH PROBLEM SOLVER
# LEARNING MODE STATE-PERSISTENCE REFINEMENT REPORT

**Date:** 2026-11-16  
**Scope:** Narrowly scoped Compose state-hoisting refinement in the Math Problem Solver module (`ProblemSolverPedagogyComponents.kt`, `ProblemSolverScreen.kt`)  
**Build & Test Verdict:**
- **`compile_applet`:** **PASS**
- **Targeted Solver Regression Suites:** **11 / 11 PASS** (`ProblemSolverLearningStatePersistenceTest` [4], `ProblemSolverPostRedesignAcceptanceTest` [4], `ProblemSolverModernUiRedesignTest` [3]; 0 failed, 0 skipped)
- **Full Application Regression Suite (`gradle :app:testDebugUnitTest`):** **275 / 275 PASS** (271 existing tests preserved + 4 new Learning state-persistence tests; 0 failed, 0 errors, 0 skipped across 18 test suites)

---

## 1. Pre-Change Compose State Ownership Inspection

Prior to this refinement:
1. `ProblemSolverScreen` owned `solverResult` above the `LazyColumn`, while `SolutionDisplayCard` (inside `LazyColumn` item 4) owned `forceExpandAllSteps` and `learningState` via `remember(result.originalProblemBn)`.
2. `InteractiveLearningStepCard` (rendered exclusively inside the `when (result.mode) { SolverMode.LEARNING -> ... }` branch of `SolutionDisplayCard`) owned all 7 per-step Learning UI states locally via `remember(step.stepIndex, ...)`:
   - `showSimple` (`সহজ ব্যাখ্যা`)
   - `showRuleAndWhy` (`কেন এই ধাপ?`)
   - `showAnalogyAndTrap` (`উদাহরণ`)
   - `showTryItYourself` (`নিজে চেষ্টা করুন`)
   - `hintLevel` (Hint 1 / Hint 2 progression)
   - `userAttempt` (self-check text input)
   - `attemptFeedbackBn` (self-check verification message)
3. Switching to `SolverMode.QUICK` or `SolverMode.BCS` exited the `SolverMode.LEARNING` branch (and scrolling up to `LearningModeSelector` in `ProblemSolverScreen` could also recycle `LazyColumn` item 4), causing all 7 per-step states to dispose and reset to defaults upon returning to `LEARNING`.

---

## 2. Minimal Hoisted State Architecture

Implemented cleanly in `ProblemSolverPedagogyComponents.kt` and `ProblemSolverScreen.kt` without modifying math engines, Room schema/migrations, Solver History behavior, test tags, or unrelated screens:

1. **Problem Identity Key (`SolverProblemIdentity`):**
   - `@Immutable data class SolverProblemIdentity(val topic: SolverTopic, val originalProblemBn: String, val finalAnswerBn: String)`
   - Invariant across `LEARNING`, `QUICK`, and `BCS` for the same solved problem, preventing collisions between distinct problems or topics.
2. **Per-Step Immutable State (`StepLearningUiState`):**
   - Stores `showSimple`, `showRuleAndWhy`, `showAnalogyAndTrap`, `showTryItYourself`, `hintLevel`, `userAttempt`, and `attemptFeedbackBn`.
   - `StepLearningUiState.defaultFor(...)` preserves existing defaults (Step 1 `"কেন এই ধাপ?"` open initially in `LEARNING` mode; all other panels closed; `hintLevel = 0`).
3. **Session State Holder (`SolverLearningSessionHolder`):**
   - `@Stable class` holding `activeIdentity`, `forceExpandAllSteps`, `learningState`, and `mutableStateMapOf<Int, StepLearningUiState>()` keyed by `stepIndex`.
   - **`ProblemSolverScreen` hoisting:** Owned above `LazyColumn` (`val learningSessionHolder = remember { SolverLearningSessionHolder() }`), updated safely outside composition in `solveCurrentTopic()`, `resetCurrentTopicInputs()` (`clear()`), and history-item restoration. Thus `LazyColumn` scrolling and `LEARNING -> QUICK -> BCS -> LEARNING` transitions preserve all state cleanly.
   - **Standalone `SolutionDisplayCard` fallback:** When `SolutionDisplayCard` is invoked standalone with `sessionHolder = null`, `remember(problemIdentity) { SolverLearningSessionHolder(problemIdentity) }` (placed *above* the `!result.isSuccess` early return) provides zero-flash, leak-free mode round-trip persistence for direct callers as well.
   - **Clean Reset Semantics:**
     - Calling `resetCurrentTopicInputs()` (`btn_reset_solver_input`) invokes `learningSessionHolder.clear()` alongside `solverResult = null`, clearing all step panels, hints, and self-check attempts—even if the user subsequently re-solves the same expression.
     - Solving a genuinely different problem produces a new `SolverProblemIdentity` (`!= activeIdentity`), discarding the previous problem's `_stepStates`, `forceExpandAllSteps`, and `learningState`.
   - **Global `Expand All / Collapse All`:** `expandAll(steps)` and `collapseAll(steps)` directly update every step entry in `_stepStates` while preserving `hintLevel` and self-check progress, ensuring manual per-panel toggles after `Expand All` can also be re-expanded cleanly and survive mode round-trips.
4. **Backward-Compatible `InteractiveLearningStepCard` API:**
   - Added optional defaulted parameters `stepUiState: StepLearningUiState? = null` and `onStepUiStateChange: ((StepLearningUiState) -> Unit)? = null`.
   - Preserves existing callers (such as `UiUxBugRemediationRegressionTest`) with zero signature breakage and preserves every `Modifier.testTag(...)`.

---

## 3. Verification & Test Results

### Sequential Execution
1. **`compile_applet`:** **PASS** (Clean debug compilation).
2. **Targeted Solver Suites:**
   `gradle :app:testDebugUnitTest --tests "com.example.ProblemSolverLearningStatePersistenceTest" --tests "com.example.ProblemSolverPostRedesignAcceptanceTest" --tests "com.example.ProblemSolverModernUiRedesignTest"`
   - **`ProblemSolverLearningStatePersistenceTest`**: **4 passed, 0 failed, 0 skipped**
     - `allFourPanelsHintsAndSelfCheckAttempt_persistAcrossLearningQuickBcsLearningRoundTrip`
     - `solvingDifferentProblem_resetsOldExpansionHintsAndSelfCheckState`
     - `clearingActiveProblem_resetsRelatedLocalLearningState`
     - `globalExpandAllAndCollapseAll_workCorrectlyAndPersistAcrossModeSwitch`
   - **`ProblemSolverPostRedesignAcceptanceTest`**: **4 passed, 0 failed, 0 skipped**
   - **`ProblemSolverModernUiRedesignTest`**: **3 passed, 0 failed, 0 skipped**
   - **Targeted Total:** **11 passed, 0 failed, 0 skipped**
3. **Full Suite (`gradle :app:testDebugUnitTest`):**
   - **275 passed, 0 failed, 0 errors, 0 skipped** across all 18 test suites (JUnit XML timestamps `2026-11-16T11:10:43Z`–`2026-11-16T11:11:35Z`).

---

## 4. Invariants & Scope Verification

- **Math Engines:** Untouched (`MathProblemSolverEngine`, `SolverAtomicArithmeticEngine`, `SolverAdvancedAlgebraAndInterestEngine`, `SolverPedagogyEnricher`).
- **Database & Schema:** Untouched (Room v10, 21 entities, migrations intact).
- **Solver History:** `saveSolverResultToHistory(...)` remains triggered solely by explicit `solve_problem_btn` clicks; verified history count stays `1` across `LEARNING -> QUICK -> BCS -> LEARNING` round-trips.
- **Test Tags & Behavior:** All existing test tags (`step_${i}_chip_simple`, `_why`, `_analogy`, `_try`, `_hint_1_btn`, `_hint_2_btn`, `btn_expand_all_steps`, `btn_collapse_all_steps`, etc.) preserved unchanged.
