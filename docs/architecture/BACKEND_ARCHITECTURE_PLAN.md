# BACKEND & DATABASE ARCHITECTURE PLAN (ROOM v6 → v7)
**Project**: Math Master (`com.example`)  
**Database**: `MathDatabase` (Room SQLite with Incremental Migration `MIGRATION_6_7`)

---

## 1. Non-Destructive Room Database Migration (`v6 -> v7`)

All 17 existing Room entities from `v1..v6` (`CalculationHistory`, `BookmarkEntity`, `QuizScoreEntity`, `StudyStreakEntity`, `StudyProgressEntity`, `FormulaNoteEntity`, `CustomModuleEntity`, `PracticeLabScoreEntity`, `MentalMathScoreEntity`, `StudySessionLogEntity`, `QuestionAttemptLogEntity`, `BookReadingStateEntity`, `BookBookmarkEntity`, `BookLessonProgressEntity`, `BookPracticeAttemptEntity`, `BookSpacedRevisionEntity`, `BcsTopicProgressEntity`, `BcsMistakeEntity`, `BcsMockExamAttemptEntity`) remain **100% untouched**.

### New Tables Added in `MIGRATION_6_7`

1. **`unified_mistake_notebook` (`UnifiedMistakeEntity`)**:
   - Captures wrong answers across Practice Lab (Module 06), Mental Math (Module 07), Number System Practice (Module 08), Quizzes (Module 11), BCS (Module 13), and Book Practice (Module 14) with Ebbinghaus spaced repetition scheduling:
   ```sql
   CREATE TABLE IF NOT EXISTS `unified_mistake_notebook` (
       `mistakeKey` TEXT NOT NULL,
       `sourceModule` TEXT NOT NULL,
       `subjectId` TEXT NOT NULL,
       `curriculumTrack` TEXT NOT NULL,
       `questionPromptBn` TEXT NOT NULL,
       `userAnswerBn` TEXT NOT NULL,
       `correctAnswerBn` TEXT NOT NULL,
       `diagnosticExplanationBn` TEXT NOT NULL,
       `errorType` TEXT NOT NULL,
       `failureCount` INTEGER NOT NULL,
       `reviewIntervalDays` INTEGER NOT NULL,
       `nextReviewAtMillis` INTEGER NOT NULL,
       `resolved` INTEGER NOT NULL,
       `lastFailedAtMillis` INTEGER NOT NULL,
       PRIMARY KEY(`mistakeKey`)
   )
   ```
   - Indexed on `(sourceModule, resolved)` and `(nextReviewAtMillis)` for O(1) due-queue queries.

2. **`formula_mastery_state` (`FormulaMasteryEntity`)**:
   - Tracks per-formula mastery (`UNSEEN`, `LEARNING`, `MASTERED`), sandbox evaluation count, and review timestamp in Module 05 (`FORMULA_LIBRARY`):
   ```sql
   CREATE TABLE IF NOT EXISTS `formula_mastery_state` (
       `formulaId` TEXT NOT NULL,
       `subjectId` TEXT NOT NULL,
       `masteryLevel` TEXT NOT NULL,
       `practiceCount` INTEGER NOT NULL,
       `lastPracticedAtMillis` INTEGER NOT NULL,
       PRIMARY KEY(`formulaId`)
   )
   ```

---

## 2. Deterministic Offline Mathematical Engine (`SharedMathEngine` & `SecondaryModuleUpgrades`)

- **Curriculum Track Taxonomy (`CurriculumTrack`)**:
  - `ALL("all", "সকল ট্র্যাক")`
  - `NCTB("nctb", "NCTB পাঠ্যক্রম")`
  - `BDMO("bdmo", "BdMO অলিম্পিয়াড")`
  - `BCS("bcs", "বিসিএস প্রস্তুতি")`
- **Arbitrary-Precision & Exact Number Theory Helpers**:
  - `Rational` (`BigInteger` numerator/denominator with automatic GCD reduction)
  - `modPowStepsBn(base: Long, exp: Long, mod: Long)`: Binary exponentiation with Bengali step-by-step trace for BdMO/BCS modular remainder problems (`a^n mod m`)
  - `divisorAnalysisBn(n: Long)`: Prime factorization `p₁^a₁ · p₂^a₂ …`, total divisor count `d(N) = Π(aᵢ + 1)`, divisor sum `σ(N) = Π((pᵢ^(aᵢ+1) - 1)/(pᵢ - 1))`, and Euler's Totient `φ(N)`
  - `solveSimultaneousLinear(a1, b1, c1, a2, b2, c2)`: Determinant/Cramer's rule & elimination steps with unique, infinite, or inconsistent system detection
- **Exact Formula-ID Interactive Evaluator (`FormulaInteractiveEngine`)**:
  - Evaluates specific `FormulaLibraryEntry.id` formulas with exact mathematical formulas before falling back to subject-level calculators, guaranteeing zero placeholder approximations.
