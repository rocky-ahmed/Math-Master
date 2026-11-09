# Math Master — Room Database v9 & Migration Validation Report

## 1. Executive Summary
- **Database Class:** `com.example.data.db.MathDatabase` (`app/src/main/java/com/example/data/db/MathDatabase.kt`)
- **Database File Name:** `"math_master_db"`
- **Active Schema Version:** **`9`** (`exportSchema = false`)
- **Total Application `@Entity` Tables:** **`20` physical application tables** (plus 3 SQLite/Room internal metadata tables: `android_metadata`, `room_master_table`, and `sqlite_sequence` = **23 total tables in `sqlite_master`**)
- **Migration Chain:** Complete, non-destructive forward migration chain `MIGRATION_1_2` through `MIGRATION_8_9` (`1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9`)
- **Destructive Migration Policy:** `fallbackToDestructiveMigration` is **NOT** enabled; all user data is preserved across upgrades.

---

## 2. Entity Count Reconciliation (Why Previous Reports Differed)

During Phase F of the engineering audit, three historical numbers (`13`, `16`, and `20`) were reconciled against the actual source code in `MathDatabase.kt` and `Entities.kt`:
- **Why `13` appeared in an earlier migration note:** An early migration test (`Phase3MigrationAndIntegrityTest`) seeded 13 core user tables created through Room v1–v6 to test row survival across migrations.
- **Why `16` appeared in an earlier draft summary:** An intermediate summary grouped the 5 Book entities into a single line item (`16` instead of `20`).
- **Actual Verified Room v9 Schema (`MathDatabase.kt` lines 31–52):** Exactly **20 `@Entity` classes** are registered in the `@Database(entities = [...], version = 9)` annotation, producing **20 application tables** in SQLite (`auditRegression_phaseF_roomDatabaseV9All20EntityTablesVerified`).

---

## 3. Complete Inventory of All 20 Room v9 `@Entity` Tables

| # | SQLite Table Name | Kotlin `@Entity` Class | Introduced In | Primary Key | Key Columns & Purpose |
|---|---|---|---|---|---|
| 1 | `bookmarks` | `BookmarkEntity` | v1 | `id: String` | `category`, `titleBn`, `subtitleBn`, `contentBn`, `timestamp` — Saved formulas, lessons, solver results, and book pages |
| 2 | `quiz_scores` | `QuizScoreEntity` | v1 | `id: Int` (auto) | `topic`, `score`, `totalQuestions`, `timeTakenSeconds`, `timestamp` — Quiz score history |
| 3 | `user_progress` | `ProgressEntity` | v1 | `itemKey: String` | `isLearned`, `updatedAt` — Fine-grained completion flags across tables, primes, squares/cubes, formulas, and lessons |
| 4 | `practice_history` | `PracticeHistoryEntity` | v2 | `id: Int` (auto) | `module`, `category`, `difficulty`, `score`, `totalQuestions`, `correctAnswers`, `wrongAnswers`, `unansweredCount`, `accuracyPercent`, `durationSeconds`, `timestamp` |
| 5 | `topic_analytics` | `TopicAnalyticsEntity` | v2 (v3 cols) | `topicId: String` | `totalAttempts`, `totalQuestions`, `correctAnswers`, `wrongAnswers`, `unansweredQuestions`, `totalTimeSeconds`, `bestAccuracyPercent`, `lastPracticedAt` |
| 6 | `recent_formulas` | `RecentFormulaEntity` | v2 | `formulaId: String` | `categoryId`, `titleBn`, `formulaText`, `viewedAt` — Recently inspected formulas |
| 7 | `exam_attempts` | `ExamAttemptEntity` | v3 (v8 col) | `attemptId: String` | `examTitleBn`, `examType`, `selectedTopicsCsv`, `difficulty`, `questionType`, `totalQuestions`, `durationLimitSeconds`, `marksPerCorrect`, `negativeMarksPerWrong`, `status`, `currentQuestionIndex`, `elapsedSeconds`, `remainingSeconds`, `answersPayload`, `questionIdsCsv`, `correctCount`, `wrongCount`, `unansweredCount`, `rawMarks`, `negativeMarksDeducted`, `finalScore`, `percentage`, `accuracyPercent`, `startedAt`, `updatedAt`, `completedAt`, `examProfileId` |
| 8 | `solver_history` | `SolverHistoryEntity` | v4 | `id: Int` (auto) | `topicId`, `topicTitleBn`, `modeId`, `problemBn`, `formulaBn`, `finalAnswerBn`, `stepsSummaryBn`, `verificationBn`, `bcsShortcutBn`, `isBookmarked`, `timestamp` |
| 9 | `daily_study_activity` | `DailyStudyActivityEntity` | v4 | `dateKey: String` | `sessionsCompleted`, `problemsSolved`, `studyMinutes`, `updatedAt` — Daily streak and study timer tracking |
| 10 | `book_lesson_progress` | `BookLessonProgressEntity` | v5 | `lessonKey: String` | `bookId`, `chapterNumber`, `lessonId`, `isCompleted`, `miniCheckPassed`, `timeSpentSeconds`, `lastVisitedAt` |
| 11 | `book_reading_state` | `BookReadingStateEntity` | v5 (v9 cols) | `bookId: String` | `lastChapterNumber`, `lastLessonId`, `lastPracticeMode`, `lastPrintedPage`, `lastPdfPage`, `totalPagesRead`, `bookmarkedPagesCsv`, `updatedAt`, `preferredViewMode`, `lastZoomScale`, `activeEditionId` |
| 12 | `book_notes` | `BookNoteEntity` | v5 | `id: Int` (auto) | `bookId`, `chapterNumber`, `lessonId`, `printedPage`, `noteTitleBn`, `noteBodyBn`, `createdAt`, `updatedAt` |
| 13 | `book_mistakes` | `BookMistakeEntity` | v5 | `questionId: String` | `bookId`, `chapterNumber`, `lessonId`, `questionType`, `questionPromptBn`, `wrongAnswerBn`, `correctAnswerBn`, `explanationBn`, `mistakeCount`, `isResolved`, `lastMistakeAt` |
| 14 | `book_spaced_revision` | `BookSpacedRevisionEntity` | v5 | `itemId: String` | `itemType`, `bookId`, `chapterNumber`, `titleBn`, `subtitleBn`, `intervalStage`, `nextReviewEpochMs`, `lastReviewedAt` |
| 15 | `bcs_user_profile` | `BcsUserProfileEntity` | v6 (v8 cols) | `profileId: String` | `targetCadreBn`, `currentStageIndex`, `dailyTargetMinutes`, `dailyTargetQuestions`, `preferredNegativeMarking`, `diagnosticCompleted`, `diagnosticScorePercent`, `updatedAt`, `selectedSyllabusVersionId`, `selectedExamProfileId` |
| 16 | `bcs_topic_progress` | `BcsTopicProgressEntity` | v6 (v8 col) | `topicId: String` | `subjectTrack`, `isGuideCompleted`, `miniCheckPassed`, `questionsAttempted`, `questionsCorrect`, `totalTimeSeconds`, `masteryPercent`, `lastStudiedAt`, `examProfileId` |
| 17 | `bcs_mistake_notebook` | `BcsMistakeEntity` | v6 (v8 cols) | `questionId: String` | `subjectTrack`, `topicId`, `questionPromptBn`, `wrongAnswerBn`, `correctAnswerBn`, `explanationBn`, `mistakeCount`, `retrySuccessCount`, `isResolved`, `nextReviewEpochMs`, `intervalStage`, `lastMistakeAt`, `shortcutTipBn`, `provenanceBadgeBn`, `examProfileId` |
| 18 | `bcs_study_goals` | `BcsStudyPlanGoalEntity` | v8 | `goalKey: String` | `examProfileId`, `dateKey`, `lessonsCompleted`, `questionsSolved`, `revisionsCompleted`, `studyTimeMinutes`, `mockExamsCompleted`, `updatedAt` |
| 19 | `unified_mistake_notebook` | `UnifiedMistakeEntity` | v7 | `mistakeKey: String` | `sourceModule`, `subjectId`, `curriculumTrack`, `questionPromptBn`, `userAnswerBn`, `correctAnswerBn`, `diagnosticExplanationBn`, `errorType`, `failureCount`, `reviewIntervalDays`, `nextReviewAtMillis`, `resolved`, `lastFailedAtMillis` |
| 20 | `formula_mastery_state` | `FormulaMasteryEntity` | v7 | `formulaId: String` | `subjectId`, `masteryLevel`, `practiceCount`, `lastPracticedAtMillis` |

---

## 4. Migration Chain Verification (`MIGRATION_1_2` -> `MIGRATION_8_9`)

| Migration Object | Source -> Target | DDL Operations | Idempotency & Data Preservation |
|---|---|---|---|
| `MIGRATION_1_2` | `1 -> 2` | `CREATE TABLE IF NOT EXISTS` for `practice_history`, `topic_analytics`, `recent_formulas` | Preserves all `bookmarks`, `quiz_scores`, `user_progress` rows |
| `MIGRATION_2_3` | `2 -> 3` | `ALTER TABLE practice_history ADD COLUMN wrongAnswers/unansweredCount`; `ALTER TABLE topic_analytics ADD COLUMN wrongAnswers/unansweredQuestions`; `CREATE TABLE IF NOT EXISTS exam_attempts` | Default `0` values on added integer columns; zero data loss |
| `MIGRATION_3_4` | `3 -> 4` | `CREATE TABLE IF NOT EXISTS solver_history`, `daily_study_activity` | Additive table creation only |
| `MIGRATION_4_5` | `4 -> 5` | `CREATE TABLE IF NOT EXISTS book_lesson_progress`, `book_reading_state`, `book_notes`, `book_mistakes`, `book_spaced_revision` | Additive table creation for Book module |
| `MIGRATION_5_6` | `5 -> 6` | `CREATE TABLE IF NOT EXISTS bcs_user_profile`, `bcs_topic_progress`, `bcs_mistake_notebook` | Additive table creation for BCS Preparation module |
| `MIGRATION_6_7` | `6 -> 7` | `CREATE TABLE IF NOT EXISTS unified_mistake_notebook`, `formula_mastery_state` | Additive table creation for cross-module mistake intelligence & formula mastery |
| `MIGRATION_7_8` | `7 -> 8` | `ALTER TABLE` on `bcs_user_profile`, `bcs_topic_progress`, `bcs_mistake_notebook`, `exam_attempts` for multi-profile support; `CREATE TABLE IF NOT EXISTS bcs_study_goals` | Safe `DEFAULT` values matching `@Entity` field defaults |
| `MIGRATION_8_9` | `8 -> 9` | `ALTER TABLE book_reading_state ADD COLUMN preferredViewMode/lastZoomScale/activeEditionId` | Safe `DEFAULT 'STRUCTURED'`, `1.0`, `'NCTB_2026_STANDARD'` |

---

## 5. Multi-Profile BCS Key Scoping & Mistake Resolution (`BUG-21` Verified)

- **Topic Progress Key Scoping:** `BcsTopicProgressEntity.buildScopedKey(examProfileId, rawTopicId)` stores non-default profile rows under `"$examProfileId::$rawTopicId"` while keeping default `BCS_50TH_PRELIM_2025` rows backward-compatible. Both `DashboardScreen.kt` (`BUG-20`) and `BcsPreparationScreen.kt` (`BUG-21`) deduplicate completed lessons by `rawTopicId`.
- **Mistake Notebook Key Scoping (`BUG-21`):** `MathRepository.recordBcsMistake` prefixes non-default profile mistakes as `"$examProfileId::$rawQId"`. `MathRepository.markBcsMistakeResolved` and `MathRepository.deleteBcsMistake` resolve both raw `questionId` and profile-scoped `"$examProfileId::$rawQId"` keys, and `MathViewModel.finishQuizSession` automatically marks previously mistaken questions resolved when answered correctly in a BCS session.
