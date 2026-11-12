# Math Master — Final Database Integrity Report

**Database Name**: `"math_master_db"` (`MathDatabase.kt`)  
**Current Schema Version**: `version = 9`  
**Total `@Entity` Tables**: `20`  
**Migration Chain**: `MIGRATION_1_2` through `MIGRATION_8_9` (100% Non-Destructive, Canonical **`BUG-10`**)

---

## 1. Complete Inventory of All 20 Room `@Entity` Tables (`Entities.kt` & `MathDatabase.kt`)

| # | Table Name | Entity Class | Primary Key | Introduced / Upgraded | Purpose |
| :- | :--- | :--- | :--- | :--- | :--- |
| 01 | `bookmarks` | `BookmarkEntity` | `id: String` | `v1` (Indexed `v9`, `BUG-10`) | Saved formulas, concepts, and study items (`index_bookmarks_timestamp`, `index_bookmarks_category`) |
| 02 | `quiz_scores` | `QuizScoreEntity` | `id: Int` (Auto) | `v1` (Indexed `v9`, `BUG-10`) | Completed quiz & exam score records (`index_quiz_scores_topic`) |
| 03 | `user_progress` | `ProgressEntity` | `itemKey: String` | `v1` | Mastered tables, primes, squares, and formulas |
| 04 | `practice_history` | `PracticeHistoryEntity` | `id: Int` (Auto) | `v2` (`v3` cols) | Detailed practice session logs across modules |
| 05 | `topic_analytics` | `TopicAnalyticsEntity` | `topicId: String` | `v2` (`v3` cols) | Aggregated accuracy, streak, and time per topic |
| 06 | `recent_formulas` | `RecentFormulaEntity` | `formulaId: String` | `v2` | Recently viewed formulas in Formula Library |
| 07 | `exam_attempts` | `ExamAttemptEntity` | `attemptId: String` | `v3` (`v8` col) | In-progress and completed exam snapshots & diagnostics (`examProfileId`) |
| 08 | `solver_history` | `SolverHistoryEntity` | `id: Int` (Auto) | `v4` | Saved step-by-step problem solver computations |
| 09 | `daily_study_activity` | `DailyStudyActivityEntity` | `dateKey: String` (`yyyy-MM-dd`) | `v4` | Daily study targets, questions solved, and study time |
| 10 | `book_lesson_progress` | `BookLessonProgressEntity` | `lessonKey: String` | `v5` | Per-lesson completion, mini-check, and time spent |
| 11 | `book_reading_state` | `BookReadingStateEntity` | `bookId: String` | `v5` (`v8` cols) | Last printed/PDF page, `preferredViewMode`, `lastZoomScale`, `activeEditionId`, and bookmarks CSV |
| 12 | `book_notes` | `BookNoteEntity` | `id: Int` (Auto) | `v5` | Student personal notes per textbook and chapter |
| 13 | `book_mistakes` | `BookMistakeEntity` | `questionId: String` | `v5` | Textbook practice mistakes with explanation & resolution state |
| 14 | `book_spaced_revision` | `BookSpacedRevisionEntity` | `itemId: String` | `v5` | 1-3-7-14 day spaced repetition schedule for textbook items |
| 15 | `bcs_user_profile` | `BcsUserProfileEntity` | `profileId: String` | `v6` (`v8` cols) | Active BCS exam profile (`selectedSyllabusVersionId`, `selectedExamProfileId`), daily targets, and diagnostic state |
| 16 | `bcs_topic_progress` | `BcsTopicProgressEntity` | `topicId: String` | `v6` (`v8` col) | Profile-scoped BCS guide completion and practice stats (`examProfileId`) |
| 17 | `bcs_mistake_notebook` | `BcsMistakeEntity` | `questionId: String` | `v6` (`v8` cols) | Profile-scoped BCS mistakes, `shortcutTipBn`, `provenanceBadgeBn`, and spaced retry schedule |
| 18 | `bcs_study_goals` | `BcsStudyPlanGoalEntity` | `goalKey: String` | `v8` | Profile-scoped daily BCS study goals (`examProfileId_dateKey`) |
| 19 | `unified_mistake_notebook` | `UnifiedMistakeEntity` | `mistakeKey: String` | `v7` (Indexed `v7`) | Cross-module unified mistake notebook (`PRACTICE_LAB`, `MENTAL_MATH`, `NUMBER_SYSTEM`, `BOOK`, `QUIZ`, `BCS`) |
| 20 | `formula_mastery_state` | `FormulaMasteryEntity` | `formulaId: String` | `v7` (Indexed `v7`) | Per-formula mastery status (`UNSEEN`, `LEARNING`, `MASTERED`, `REVISION_NEEDED`) |

---

## 2. Non-Destructive Migration Chain Verification (`MIGRATION_1_2` – `MIGRATION_8_9`, Canonical `BUG-10`)

- **`MIGRATION_1_2` (`MathDatabase.kt:63–99`)**: Creates `practice_history`, `topic_analytics`, and `recent_formulas`.
- **`MIGRATION_2_3` (`MathDatabase.kt:101–150`)**: Adds `wrongAnswers` and `unansweredCount`/`unansweredQuestions` to `practice_history` and `topic_analytics`, and creates `exam_attempts`.
- **`MIGRATION_3_4` (`MathDatabase.kt:152–182`)**: Creates `solver_history` and `daily_study_activity`.
- **`MIGRATION_4_5` (`MathDatabase.kt:184–273`)**: Creates `book_lesson_progress`, `book_reading_state`, `book_notes`, `book_mistakes`, and `book_spaced_revision`.
- **`MIGRATION_5_6` (`MathDatabase.kt:275–338`)**: Creates `bcs_user_profile`, `bcs_topic_progress`, and `bcs_mistake_notebook`.
- **`MIGRATION_6_7` (`MathDatabase.kt:340–392`)**: Creates `unified_mistake_notebook` and `formula_mastery_state` along with 5 secondary indexes (`index_unified_mistake_notebook_nextReviewAtMillis`, `index_unified_mistake_notebook_resolved`, `index_unified_mistake_notebook_subjectId`, `index_formula_mastery_state_masteryLevel`, `index_formula_mastery_state_subjectId`).
- **`MIGRATION_7_8` (`MathDatabase.kt:420–459`)**: Uses idempotent `addColumnIfMissing` (`PRAGMA table_info(...)`) to add `preferredViewMode`, `lastZoomScale`, and `activeEditionId` to `book_reading_state`; adds `selectedSyllabusVersionId` and `selectedExamProfileId` to `bcs_user_profile`; adds `examProfileId` to `bcs_topic_progress`, `bcs_mistake_notebook`, and `exam_attempts`; adds `shortcutTipBn` and `provenanceBadgeBn` to `bcs_mistake_notebook`; and creates `bcs_study_goals`.
- **`MIGRATION_8_9` (`MathDatabase.kt:461–467`, Canonical `BUG-10`)**: Creates secondary performance indexes `index_bookmarks_timestamp` and `index_bookmarks_category` on `bookmarks` and `index_quiz_scores_topic` on `quiz_scores`, matching the `@Entity(indices = [...])` declarations in `Entities.kt:12–16, 31–34`. *(Note: In the early 8-item report `MASTER_ENGINEERING_AUDIT_REPORT.md`, `MIGRATION_8_9` was listed under a local 8-item report alias `BUG-07`; in the canonical 73-defect register `FINAL_ROOT_CAUSE_DEFECT_REGISTER.md`, all Room `v1 -> v9` migration and index parity work belongs to **`BUG-10`**, while canonical `BUG-07` is `SharedMathEngine` `Rational` answer validation and `BigInteger` overflow protection.)*

### Verification Results
- Verified by `ExampleRobolectricTest.roomMigrations1To9PreserveAllUserDataAndCreateIndexes` (`lines 581–755`), `roomMigration6To7_preservesAll17ExistingV6TablesAndPopulatedData_andSupportsNewV7Tables`, and `auditRegression_phaseF_roomDatabaseV9All20EntityTablesVerified` in `EcosystemUpgradeV7UnitTest.kt`:
  - All `20` application tables are created with exact column definitions and secondary indexes.
  - Pre-existing rows across all tables survive `v1 -> v9` upgrades with zero data loss.
