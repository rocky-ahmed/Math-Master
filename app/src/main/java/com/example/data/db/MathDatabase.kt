package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.RecentFormulaEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.model.TopicAnalyticsEntity

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
        DailyStudyActivityEntity::class
    ],
    version = 4,
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

        fun getDatabase(context: Context): MathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MathDatabase::class.java,
                    "math_master_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): MathDatabase = getDatabase(context)
    }
}
