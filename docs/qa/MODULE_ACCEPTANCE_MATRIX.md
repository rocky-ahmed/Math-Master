# MODULE ACCEPTANCE & REGRESSION VERIFICATION MATRIX
**Project**: Math Master (`com.example`)

---

## 1. Protected Modules 01–04 Zero-Change Verification

| Protected Module | Protected Files | Verification Criterion |
| :--- | :--- | :--- |
| **01: `TABLES`** | `TablesScreen.kt`, `TableDetailScreen.kt`, `MultiplicationModels.kt` | `git diff` = 0 lines changed; existing unit tests pass 100% |
| **02: `PRIMES`** | `PrimesScreen.kt`, `PrimeNumberModels.kt` | `git diff` = 0 lines changed; existing unit tests pass 100% |
| **03: `SQUARES`** | `SquaresCubesScreen.kt`, `SquareCubeModels.kt` | `git diff` = 0 lines changed; existing unit tests pass 100% |
| **04: `FORMULAS`** | `FormulasScreen.kt`, `FormulaDetailScreen.kt`, `AlgebraFormulaModels.kt` | `git diff` = 0 lines changed; existing unit tests pass 100% |

---

## 2. Upgraded Modules 05–14 Acceptance Criteria

| Module | Functional & Mathematical Acceptance Criteria | Automated Test Verification |
| :--- | :--- | :--- |
| **05: `FORMULA_LIBRARY`** | • Track filtering (`ALL`, `NCTB`, `BDMO`, `BCS`) returns non-empty verified formulas<br>• Interactive sandbox evaluates exact results by `formula.id`<br>• Formula mastery state persists in Room v7 | `SecondaryModulesUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **06: `PRACTICE_LAB`** | • Track filtering (`NCTB`, `BDMO`, `BCS`) works<br>• Daily Interleaved Challenge generates mixed-category questions with verified answers<br>• Diagnostic distractor feedback explains wrong choices | `SecondaryModulesUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **07: `MENTAL_MATH`** | • Every generated `MentalMathQuestion` includes a non-blank `mentalStrategyTipBn`<br>• Speed tiers and combo scoring compute deterministically | `SecondaryModulesUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **08: `NUMBER_SYSTEM`** | • Divisibility rules cover `2..12` plus `13, 15, 16, 18, 25` with exact proofs<br>• Modular exponentiation (`a^n mod m`) and Divisor Analyzer (`d(N)`, `σ(N)`, `φ(N)`) compute exact values | `SecondaryModulesUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **09: `PROBLEM_SOLVER`** | • All 16 solver topics + simultaneous linear helper compute exact steps and common mistake warnings<br>• Cross-module action chips navigate to Formula Library & Visual Explorer | `SolverAndVisualUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **10: `VISUAL_EXPLORER`** | • All 10 visual tabs include guided experiment verification challenges and knowledge checks | `SolverAndVisualUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **11: `QUIZZES`** | • All 5 exam blueprints generate valid questions<br>• Exam question bookmarking and unified mistake logging work | `ExamAndAnalyticsUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **12: `DASHBOARD`** | • `LearningAnalyticsEngine` aggregates Book, BCS, Formula Mastery, and Unified Mistake Notebook counts alongside existing KPIs | `ExamAndAnalyticsUpgradeUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **13: `BCS_MATH`** | • 50th BCS Preliminary (35 marks) & Written (Code 008/009) include descriptive step-by-step model rubrics and NCTB Book chapter links | `BcsPreparationUnitTest` + `EcosystemUpgradeV7UnitTest` |
| **14: `BOOK`** | • All 4 NCTB books (47 chapters) remain 100% page-mapped and expose 1-tap Problem Solver & BCS bridges | `BookModuleUnitTest` + `EcosystemUpgradeV7UnitTest` |
