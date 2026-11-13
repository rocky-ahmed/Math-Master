package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BookLessonProgressEntity
import com.example.data.model.BookMistakeEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.BookReadingStateEntity
import com.example.data.model.BookSpacedRevisionEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.BcsMistakeEntity
import com.example.data.model.BcsStudyPlanGoalEntity
import com.example.data.model.BcsTopicProgressEntity
import com.example.data.model.BcsUserProfileEntity
import com.example.data.model.FormulaMasteryEntity
import com.example.data.model.RecentFormulaEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.model.StudyTimerFlushReceiptEntity
import com.example.data.model.TopicAnalyticsEntity
import com.example.data.model.UnifiedMistakeEntity

@Database(
    entities = [
        BookmarkEntity::class,
        QuizScoreEntity::class,
        ProgressEntity::class,
        PracticeHistoryEntity::class,
        TopicAnalyticsEntity::class,
        RecentFormulaEntity::class,
        ExamAttemptEntity::class,
        SolverHistoryEntity::class,
        DailyStudyActivityEntity::class,
        BookLessonProgressEntity::class,
        BookReadingStateEntity::class,
        BookNoteEntity::class,
        BookMistakeEntity::class,
        BookSpacedRevisionEntity::class,
        BcsUserProfileEntity::class,
        BcsTopicProgressEntity::class,
        BcsMistakeEntity::class,
        BcsStudyPlanGoalEntity::class,
        UnifiedMistakeEntity::class,
        FormulaMasteryEntity::class,
        StudyTimerFlushReceiptEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class MathDatabase : RoomDatabase() {
    abstract fun mathDao(): MathDao

    companion object {
        @Volatile
        private var INSTANCE: MathDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quiz_scores_timestamp` ON `quiz_scores` (`timestamp`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_progress_isLearned` ON `user_progress` (`isLearned`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `practice_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `module` TEXT NOT NULL,
                        `topicId` TEXT NOT NULL,
                        `topicTitleBn` TEXT NOT NULL,
                        `difficulty` TEXT NOT NULL,
                        `mode` TEXT NOT NULL,
                        `score` INTEGER NOT NULL,
                        `correctCount` INTEGER NOT NULL,
                        `wrongCount` INTEGER NOT NULL,
                        `totalQuestions` INTEGER NOT NULL,
                        `accuracyPercent` INTEGER NOT NULL,
                        `bestStreak` INTEGER NOT NULL,
                        `durationSeconds` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_practice_history_module` ON `practice_history` (`module`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_practice_history_timestamp` ON `practice_history` (`timestamp`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `topic_analytics` (
                        `analyticsKey` TEXT NOT NULL,
                        `module` TEXT NOT NULL,
                        `topicId` TEXT NOT NULL,
                        `topicTitleBn` TEXT NOT NULL,
                        `difficulty` TEXT NOT NULL,
                        `totalAttempted` INTEGER NOT NULL,
                        `totalCorrect` INTEGER NOT NULL,
                        `totalWrong` INTEGER NOT NULL,
                        `bestStreak` INTEGER NOT NULL,
                        `totalTimeSeconds` INTEGER NOT NULL,
                        `lastPracticedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`analyticsKey`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_topic_analytics_module_topicId` ON `topic_analytics` (`module`, `topicId`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recent_formulas` (
                        `formulaId` TEXT NOT NULL,
                        `titleBn` TEXT NOT NULL,
                        `subjectId` TEXT NOT NULL,
                        `viewedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`formulaId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_recent_formulas_viewedAt` ON `recent_formulas` (`viewedAt`)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `exam_attempts` (
                        `attemptId` TEXT NOT NULL,
                        `examTitleBn` TEXT NOT NULL,
                        `examType` TEXT NOT NULL,
                        `selectedTopicsCsv` TEXT NOT NULL,
                        `difficulty` TEXT NOT NULL,
                        `questionType` TEXT NOT NULL,
                        `totalQuestions` INTEGER NOT NULL,
                        `durationLimitSeconds` INTEGER NOT NULL,
                        `marksPerCorrect` REAL NOT NULL,
                        `negativeMarksPerWrong` REAL NOT NULL,
                        `status` TEXT NOT NULL,
                        `currentQuestionIndex` INTEGER NOT NULL,
                        `elapsedSeconds` INTEGER NOT NULL,
                        `remainingSeconds` INTEGER NOT NULL,
                        `questionsJson` TEXT NOT NULL,
                        `answersJson` TEXT NOT NULL,
                        `markedForReviewCsv` TEXT NOT NULL,
                        `visitedIndicesCsv` TEXT NOT NULL,
                        `questionTimeSecondsJson` TEXT NOT NULL,
                        `correctCount` INTEGER NOT NULL,
                        `wrongCount` INTEGER NOT NULL,
                        `unansweredCount` INTEGER NOT NULL,
                        `earnedMarks` REAL NOT NULL,
                        `negativeMarksDeducted` REAL NOT NULL,
                        `finalScore` REAL NOT NULL,
                        `percentage` REAL NOT NULL,
                        `accuracyPercent` REAL NOT NULL,
                        `startedAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `completedAt` INTEGER,
                        PRIMARY KEY(`attemptId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_exam_attempts_status` ON `exam_attempts` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_exam_attempts_startedAt` ON `exam_attempts` (`startedAt`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_exam_attempts_examType` ON `exam_attempts` (`examType`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `solver_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `topicId` TEXT NOT NULL,
                        `topicTitleBn` TEXT NOT NULL,
                        `categoryTitleBn` TEXT NOT NULL,
                        `inputSummaryBn` TEXT NOT NULL,
                        `inputsJson` TEXT NOT NULL,
                        `finalAnswerBn` TEXT NOT NULL,
                        `stepsJson` TEXT NOT NULL,
                        `formulaUsedBn` TEXT NOT NULL,
                        `verificationBn` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_solver_history_timestamp` ON `solver_history` (`timestamp`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_solver_history_topicId` ON `solver_history` (`topicId`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_study_activity` (
                        `dateKey` TEXT NOT NULL,
                        `questionsAttempted` INTEGER NOT NULL,
                        `correctCount` INTEGER NOT NULL,
                        `wrongCount` INTEGER NOT NULL,
                        `studyTimeSeconds` INTEGER NOT NULL,
                        `examsCompleted` INTEGER NOT NULL,
                        `practiceSessionsCompleted` INTEGER NOT NULL,
                        `dailyTargetQuestions` INTEGER NOT NULL,
                        `dailyTargetMinutes` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`dateKey`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `book_lesson_progress` (
                        `lessonKey` TEXT NOT NULL,
                        `bookId` TEXT NOT NULL,
                        `chapterNumber` INTEGER NOT NULL,
                        `lessonId` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `miniCheckPassed` INTEGER NOT NULL,
                        `timeSpentSeconds` INTEGER NOT NULL,
                        `lastVisitedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`lessonKey`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_lesson_progress_bookId_chapterNumber` ON `book_lesson_progress` (`bookId`, `chapterNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_lesson_progress_isCompleted` ON `book_lesson_progress` (`isCompleted`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `book_reading_state` (
                        `bookId` TEXT NOT NULL,
                        `lastChapterNumber` INTEGER NOT NULL,
                        `lastLessonId` TEXT NOT NULL,
                        `lastPracticeMode` TEXT NOT NULL,
                        `lastPrintedPage` INTEGER NOT NULL,
                        `lastPdfPage` INTEGER NOT NULL,
                        `totalPagesRead` INTEGER NOT NULL,
                        `bookmarkedPagesCsv` TEXT NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`bookId`)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `book_notes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `bookId` TEXT NOT NULL,
                        `chapterNumber` INTEGER NOT NULL,
                        `lessonId` TEXT NOT NULL,
                        `printedPage` INTEGER NOT NULL,
                        `titleBn` TEXT NOT NULL,
                        `contentBn` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_notes_bookId_chapterNumber` ON `book_notes` (`bookId`, `chapterNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_notes_updatedAt` ON `book_notes` (`updatedAt`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `book_mistakes` (
                        `questionId` TEXT NOT NULL,
                        `bookId` TEXT NOT NULL,
                        `chapterNumber` INTEGER NOT NULL,
                        `questionPromptBn` TEXT NOT NULL,
                        `previousWrongAnswerBn` TEXT NOT NULL,
                        `correctAnswerBn` TEXT NOT NULL,
                        `explanationBn` TEXT NOT NULL,
                        `sourceProvenance` TEXT NOT NULL,
                        `mistakeCount` INTEGER NOT NULL,
                        `isResolved` INTEGER NOT NULL,
                        `lastMistakeAt` INTEGER NOT NULL,
                        PRIMARY KEY(`questionId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_mistakes_bookId_chapterNumber` ON `book_mistakes` (`bookId`, `chapterNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_mistakes_isResolved` ON `book_mistakes` (`isResolved`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_mistakes_lastMistakeAt` ON `book_mistakes` (`lastMistakeAt`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `book_spaced_revision` (
                        `itemId` TEXT NOT NULL,
                        `itemType` TEXT NOT NULL,
                        `bookId` TEXT NOT NULL,
                        `chapterNumber` INTEGER NOT NULL,
                        `titleBn` TEXT NOT NULL,
                        `subtitleBn` TEXT NOT NULL,
                        `intervalStage` INTEGER NOT NULL,
                        `nextReviewEpochMs` INTEGER NOT NULL,
                        `lastReviewedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`itemId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_spaced_revision_nextReviewEpochMs` ON `book_spaced_revision` (`nextReviewEpochMs`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_book_spaced_revision_bookId_chapterNumber` ON `book_spaced_revision` (`bookId`, `chapterNumber`)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bcs_user_profile` (
                        `profileId` TEXT NOT NULL,
                        `selectedExamProfileId` TEXT NOT NULL,
                        `onboardingCompleted` INTEGER NOT NULL,
                        `currentStageIndex` INTEGER NOT NULL,
                        `dailyTargetMinutes` INTEGER NOT NULL,
                        `dailyTargetQuestions` INTEGER NOT NULL,
                        `targetCompletionEpochMs` INTEGER,
                        `officialExamDateEpochMs` INTEGER,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`profileId`)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bcs_topic_progress` (
                        `topicId` TEXT NOT NULL,
                        `subjectTrack` TEXT NOT NULL,
                        `isGuideCompleted` INTEGER NOT NULL,
                        `miniCheckPassed` INTEGER NOT NULL,
                        `questionsAttempted` INTEGER NOT NULL,
                        `questionsCorrect` INTEGER NOT NULL,
                        `totalTimeSeconds` INTEGER NOT NULL,
                        `masteryPercent` INTEGER NOT NULL,
                        `lastStudiedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`topicId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_topic_progress_subjectTrack` ON `bcs_topic_progress` (`subjectTrack`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_topic_progress_lastStudiedAt` ON `bcs_topic_progress` (`lastStudiedAt`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bcs_mistake_notebook` (
                        `questionId` TEXT NOT NULL,
                        `subjectTrack` TEXT NOT NULL,
                        `topicId` TEXT NOT NULL,
                        `questionPromptBn` TEXT NOT NULL,
                        `wrongAnswerBn` TEXT NOT NULL,
                        `correctAnswerBn` TEXT NOT NULL,
                        `explanationBn` TEXT NOT NULL,
                        `shortcutTipBn` TEXT NOT NULL,
                        `provenanceBadgeBn` TEXT NOT NULL,
                        `mistakeCount` INTEGER NOT NULL,
                        `retrySuccessCount` INTEGER NOT NULL,
                        `isResolved` INTEGER NOT NULL,
                        `nextReviewEpochMs` INTEGER NOT NULL,
                        `intervalStage` INTEGER NOT NULL,
                        `lastMistakeAt` INTEGER NOT NULL,
                        PRIMARY KEY(`questionId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_mistake_notebook_subjectTrack` ON `bcs_mistake_notebook` (`subjectTrack`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_mistake_notebook_isResolved` ON `bcs_mistake_notebook` (`isResolved`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_mistake_notebook_nextReviewEpochMs` ON `bcs_mistake_notebook` (`nextReviewEpochMs`)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
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
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_unified_mistake_notebook_sourceModule_resolved` ON `unified_mistake_notebook` (`sourceModule`, `resolved`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_unified_mistake_notebook_nextReviewAtMillis` ON `unified_mistake_notebook` (`nextReviewAtMillis`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `formula_mastery_state` (
                        `formulaId` TEXT NOT NULL,
                        `subjectId` TEXT NOT NULL,
                        `masteryLevel` TEXT NOT NULL,
                        `practiceCount` INTEGER NOT NULL,
                        `lastPracticedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`formulaId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_formula_mastery_state_subjectId` ON `formula_mastery_state` (`subjectId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_formula_mastery_state_masteryLevel` ON `formula_mastery_state` (`masteryLevel`)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `exam_attempts` ADD COLUMN `examProfileId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_exam_attempts_examProfileId` ON `exam_attempts` (`examProfileId`)")

                db.execSQL("ALTER TABLE `book_reading_state` ADD COLUMN `preferredViewMode` TEXT NOT NULL DEFAULT 'STRUCTURED'")
                db.execSQL("ALTER TABLE `book_reading_state` ADD COLUMN `lastZoomScale` REAL NOT NULL DEFAULT 1.0")
                db.execSQL("ALTER TABLE `book_reading_state` ADD COLUMN `activeEditionId` TEXT NOT NULL DEFAULT 'NCTB_2026_STANDARD'")

                db.execSQL("ALTER TABLE `bcs_user_profile` ADD COLUMN `preparationLevel` TEXT NOT NULL DEFAULT 'BEGINNER'")
                db.execSQL("ALTER TABLE `bcs_user_profile` ADD COLUMN `weeklyTargetTopics` INTEGER NOT NULL DEFAULT 3")
                db.execSQL("ALTER TABLE `bcs_user_profile` ADD COLUMN `weeklyTargetMocks` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `bcs_user_profile` ADD COLUMN `targetAccuracyPercent` INTEGER NOT NULL DEFAULT 75")

                db.execSQL("ALTER TABLE `bcs_topic_progress` ADD COLUMN `examProfileId` TEXT NOT NULL DEFAULT 'BCS_50TH_PRELIM_2025'")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_topic_progress_examProfileId` ON `bcs_topic_progress` (`examProfileId`)")

                db.execSQL("ALTER TABLE `bcs_mistake_notebook` ADD COLUMN `examProfileId` TEXT NOT NULL DEFAULT 'BCS_50TH_PRELIM_2025'")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_mistake_notebook_examProfileId` ON `bcs_mistake_notebook` (`examProfileId`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bcs_study_goals` (
                        `goalKey` TEXT NOT NULL,
                        `examProfileId` TEXT NOT NULL,
                        `dateKey` TEXT NOT NULL,
                        `lessonsCompleted` INTEGER NOT NULL,
                        `questionsSolved` INTEGER NOT NULL,
                        `revisionsCompleted` INTEGER NOT NULL,
                        `studyTimeMinutes` INTEGER NOT NULL,
                        `mockExamsCompleted` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`goalKey`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_study_goals_examProfileId_dateKey` ON `bcs_study_goals` (`examProfileId`, `dateKey`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bcs_study_goals_updatedAt` ON `bcs_study_goals` (`updatedAt`)")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bookmarks_timestamp` ON `bookmarks` (`timestamp`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bookmarks_category` ON `bookmarks` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quiz_scores_topic` ON `quiz_scores` (`topic`)")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `study_timer_flush_receipts` (
                        `operationId` TEXT NOT NULL,
                        `studyDateKey` TEXT NOT NULL,
                        `pendingSeconds` INTEGER NOT NULL,
                        `sessionId` TEXT NOT NULL,
                        `completedSessionsDelta` INTEGER NOT NULL,
                        `createdAtEpochMs` INTEGER NOT NULL,
                        `appliedAtEpochMs` INTEGER NOT NULL,
                        PRIMARY KEY(`operationId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_study_timer_flush_receipts_studyDateKey` ON `study_timer_flush_receipts` (`studyDateKey`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_study_timer_flush_receipts_appliedAtEpochMs` ON `study_timer_flush_receipts` (`appliedAtEpochMs`)")
            }
        }

        fun getDatabase(context: Context): MathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MathDatabase::class.java,
                    "math_master_db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): MathDatabase = getDatabase(context)
    }
}

