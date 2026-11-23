package com.example

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class BcsGoalCreditMigration10To11Test {

    private lateinit var app: Application
    private val testDbName = "bcs_gc02_migration_v10_v11_test.db"

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.deleteDatabase(testDbName)
    }

    @After
    fun tearDown() {
        app.deleteDatabase(testDbName)
    }

    private fun createSeededV10Database(dbName: String): SupportSQLiteOpenHelper {
        val v1Callback = object : SupportSQLiteOpenHelper.Callback(1) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bookmarks` (
                        `id` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `titleBn` TEXT NOT NULL,
                        `subtitleBn` TEXT NOT NULL,
                        `detailBn` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `quiz_scores` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `topic` TEXT NOT NULL,
                        `topicTitleBn` TEXT NOT NULL,
                        `score` INTEGER NOT NULL,
                        `totalQuestions` INTEGER NOT NULL,
                        `percentage` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_progress` (
                        `itemId` TEXT NOT NULL,
                        `isLearned` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`itemId`)
                    )
                    """.trimIndent()
                )
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(app)
                .name(dbName)
                .callback(v1Callback)
                .build()
        )
        val rawDb = helper.writableDatabase
        MathDatabase.MIGRATION_1_2.migrate(rawDb)
        MathDatabase.MIGRATION_2_3.migrate(rawDb)
        MathDatabase.MIGRATION_3_4.migrate(rawDb)
        MathDatabase.MIGRATION_4_5.migrate(rawDb)
        MathDatabase.MIGRATION_5_6.migrate(rawDb)
        MathDatabase.MIGRATION_6_7.migrate(rawDb)
        MathDatabase.MIGRATION_7_8.migrate(rawDb)
        MathDatabase.MIGRATION_8_9.migrate(rawDb)
        MathDatabase.MIGRATION_9_10.migrate(rawDb)

        // Seed realistic historical v10 exam_attempts records (COMPLETED + IN_PROGRESS)
        rawDb.execSQL(
            """
            INSERT INTO `exam_attempts` (
                `attemptId`, `examTitleBn`, `examType`, `selectedTopicsCsv`, `difficulty`, `questionType`,
                `totalQuestions`, `durationLimitSeconds`, `marksPerCorrect`, `negativeMarksPerWrong`,
                `status`, `currentQuestionIndex`, `elapsedSeconds`, `remainingSeconds`,
                `questionsJson`, `answersJson`, `markedForReviewCsv`, `visitedIndicesCsv`, `questionTimeSecondsJson`,
                `correctCount`, `wrongCount`, `unansweredCount`, `earnedMarks`, `negativeMarksDeducted`,
                `finalScore`, `percentage`, `accuracyPercent`, `startedAt`, `updatedAt`, `completedAt`, `examProfileId`
            ) VALUES (
                'bcs_mock_hist_001', '৫০তম বিসিএস মক টেস্ট', 'BCS_PRELIMINARY_MOCK', 'arithmetic,algebra', 'medium', 'mcq',
                20, 1200, 1.0, 0.5,
                'COMPLETED', 19, 845, 0,
                '[]', '{}', '', '0,1', '{}',
                15, 3, 2, 15.0, 1.5,
                13.5, 67.5, 83.33, 1763700000000, 1763700845000, 1763700845000, 'BCS_50TH_PRELIM_2025'
            )
            """.trimIndent()
        )
        rawDb.execSQL(
            """
            INSERT INTO `exam_attempts` (
                `attemptId`, `examTitleBn`, `examType`, `selectedTopicsCsv`, `difficulty`, `questionType`,
                `totalQuestions`, `durationLimitSeconds`, `marksPerCorrect`, `negativeMarksPerWrong`,
                `status`, `currentQuestionIndex`, `elapsedSeconds`, `remainingSeconds`,
                `questionsJson`, `answersJson`, `markedForReviewCsv`, `visitedIndicesCsv`, `questionTimeSecondsJson`,
                `correctCount`, `wrongCount`, `unansweredCount`, `earnedMarks`, `negativeMarksDeducted`,
                `finalScore`, `percentage`, `accuracyPercent`, `startedAt`, `updatedAt`, `completedAt`, `examProfileId`
            ) VALUES (
                'bcs_mode_topic_practice_hist_002', 'টপিক অনুশীলন', 'BCS_PRELIMINARY_MOCK', 'geometry', 'hard', 'mcq',
                10, 0, 1.0, 0.0,
                'IN_PROGRESS', 4, 210, 0,
                '[]', '{}', '2', '0,1,2,3,4', '{}',
                4, 1, 5, 4.0, 0.0,
                4.0, 40.0, 80.0, 1763705000000, 1763705210000, NULL, 'BCS_WRITTEN_008_MATH'
            )
            """.trimIndent()
        )

        // Seed realistic historical v10 bcs_study_goals records across two profiles
        rawDb.execSQL(
            """
            INSERT INTO `bcs_study_goals` (
                `goalKey`, `examProfileId`, `dateKey`, `lessonsCompleted`, `questionsSolved`,
                `revisionsCompleted`, `studyTimeMinutes`, `mockExamsCompleted`, `updatedAt`
            ) VALUES (
                'BCS_50TH_PRELIM_2025_2026-11-21', 'BCS_50TH_PRELIM_2025', '2026-11-21', 2, 18, 1, 24, 1, 1763700850000
            )
            """.trimIndent()
        )
        rawDb.execSQL(
            """
            INSERT INTO `bcs_study_goals` (
                `goalKey`, `examProfileId`, `dateKey`, `lessonsCompleted`, `questionsSolved`,
                `revisionsCompleted`, `studyTimeMinutes`, `mockExamsCompleted`, `updatedAt`
            ) VALUES (
                'BCS_WRITTEN_008_MATH_2026-11-21', 'BCS_WRITTEN_008_MATH', '2026-11-21', 1, 9, 2, 15, 0, 1763705220000
            )
            """.trimIndent()
        )

        rawDb.version = 10
        return helper
    }

    private fun openRoomV11(dbName: String): MathDatabase {
        return Room.databaseBuilder(app, MathDatabase::class.java, dbName)
            .addMigrations(
                MathDatabase.MIGRATION_1_2,
                MathDatabase.MIGRATION_2_3,
                MathDatabase.MIGRATION_3_4,
                MathDatabase.MIGRATION_4_5,
                MathDatabase.MIGRATION_5_6,
                MathDatabase.MIGRATION_6_7,
                MathDatabase.MIGRATION_7_8,
                MathDatabase.MIGRATION_8_9,
                MathDatabase.MIGRATION_9_10,
                MathDatabase.MIGRATION_10_11
            )
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun migration10To11_preservesHistoricalExamAttemptsAndBcsGoals_andCreatesValidLedgerSchema() = runBlocking {
        val helper = createSeededV10Database(testDbName)
        val rawDb = helper.writableDatabase

        // Verify v10 database does not yet contain bcs_goal_credit_records
        assertEquals(10, rawDb.version)
        rawDb.query("SELECT name FROM sqlite_master WHERE type='table' AND name='bcs_goal_credit_records'").use { c ->
            assertEquals(0, c.count)
        }

        // Verify aborted/rolled-back MIGRATION_10_11 leaves v10 intact
        rawDb.beginTransaction()
        try {
            MathDatabase.MIGRATION_10_11.migrate(rawDb)
            // Omit setTransactionSuccessful() to verify rollback safety
        } finally {
            rawDb.endTransaction()
        }
        assertEquals(10, rawDb.version)
        rawDb.query("SELECT name FROM sqlite_master WHERE type='table' AND name='bcs_goal_credit_records'").use { c ->
            assertEquals("Aborted MIGRATION_10_11 must roll back table creation", 0, c.count)
        }
        helper.close()

        // Open as Room v11 -> runs MIGRATION_10_11 and Room's full generated schema validation
        val roomDb = openRoomV11(testDbName)
        try {
            val sqlite = roomDb.openHelper.writableDatabase
            assertEquals(11, sqlite.version)
            sqlite.query("PRAGMA integrity_check").use { c ->
                assertTrue(c.moveToFirst())
                assertEquals("ok", c.getString(0).lowercase())
            }

            // 1. Verify historical exam_attempts records survived completely unchanged
            val dao = roomDb.mathDao()
            val histCompleted = dao.getExamAttemptById("bcs_mock_hist_001")
            assertNotNull("Historical COMPLETED exam attempt must be preserved", histCompleted)
            assertEquals("COMPLETED", histCompleted!!.status)
            assertEquals("BCS_50TH_PRELIM_2025", histCompleted.examProfileId)
            assertEquals(15, histCompleted.correctCount)
            assertEquals(3, histCompleted.wrongCount)
            assertEquals(845, histCompleted.elapsedSeconds)
            assertEquals(13.5, histCompleted.finalScore, 0.001)
            assertEquals(1763700845000L, histCompleted.completedAt)

            val histInProgress = dao.getExamAttemptById("bcs_mode_topic_practice_hist_002")
            assertNotNull("Historical IN_PROGRESS exam attempt must be preserved", histInProgress)
            assertEquals("IN_PROGRESS", histInProgress!!.status)
            assertEquals("BCS_WRITTEN_008_MATH", histInProgress.examProfileId)
            assertEquals(4, histInProgress.correctCount)
            assertEquals(1, histInProgress.wrongCount)
            assertNull(histInProgress.completedAt)

            // 2. Verify historical bcs_study_goals records survived completely unchanged
            val goalPrelim = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-21")
            assertNotNull("Historical BCS 50th study goal must be preserved", goalPrelim)
            assertEquals(2, goalPrelim!!.lessonsCompleted)
            assertEquals(18, goalPrelim.questionsSolved)
            assertEquals(1, goalPrelim.revisionsCompleted)
            assertEquals(24, goalPrelim.studyTimeMinutes)
            assertEquals(1, goalPrelim.mockExamsCompleted)
            assertEquals(1763700850000L, goalPrelim.updatedAt)

            val goalWritten = dao.getBcsStudyGoalByKey("BCS_WRITTEN_008_MATH_2026-11-21")
            assertNotNull("Historical BCS Written study goal must be preserved", goalWritten)
            assertEquals(1, goalWritten!!.lessonsCompleted)
            assertEquals(9, goalWritten.questionsSolved)
            assertEquals(2, goalWritten.revisionsCompleted)
            assertEquals(15, goalWritten.studyTimeMinutes)
            assertEquals(0, goalWritten.mockExamsCompleted)

            // 3. Verify bcs_goal_credit_records table exists with exact columns, nullability, PK, and indexes
            sqlite.query("SELECT name FROM sqlite_master WHERE type='table' AND name='bcs_goal_credit_records'").use { c ->
                assertEquals("bcs_goal_credit_records table must exist after MIGRATION_10_11", 1, c.count)
            }

            val columns = mutableMapOf<String, Triple<String, Int, Int>>() // name -> (type, notNull, pk)
            sqlite.query("PRAGMA table_info(`bcs_goal_credit_records`)").use { c ->
                while (c.moveToNext()) {
                    val colName = c.getString(1)
                    val colType = c.getString(2)
                    val notNull = c.getInt(3)
                    val pk = c.getInt(5)
                    columns[colName] = Triple(colType, notNull, pk)
                }
            }
            assertEquals(10, columns.size)
            assertEquals(Triple("TEXT", 1, 1), columns["attemptId"])
            assertEquals(Triple("TEXT", 1, 0), columns["examProfileId"])
            assertEquals(Triple("TEXT", 1, 0), columns["targetDateKey"])
            assertEquals(Triple("INTEGER", 1, 0), columns["questionsDelta"])
            assertEquals(Triple("INTEGER", 1, 0), columns["studyTimeMinutesDelta"])
            assertEquals(Triple("INTEGER", 1, 0), columns["mockExamsDelta"])
            assertEquals(Triple("INTEGER", 1, 0), columns["revisionsDelta"])
            assertEquals(Triple("TEXT", 1, 0), columns["status"])
            assertEquals(Triple("INTEGER", 1, 0), columns["createdAtMs"])
            assertEquals(Triple("INTEGER", 0, 0), columns["creditedAtMs"])

            val indexNames = mutableSetOf<String>()
            sqlite.query("PRAGMA index_list(`bcs_goal_credit_records`)").use { c ->
                while (c.moveToNext()) {
                    indexNames.add(c.getString(1))
                }
            }
            assertTrue(
                "Missing status/createdAtMs composite index: $indexNames",
                indexNames.contains("index_bcs_goal_credit_records_status_createdAtMs")
            )
            assertTrue(
                "Missing examProfileId/targetDateKey composite index: $indexNames",
                indexNames.contains("index_bcs_goal_credit_records_examProfileId_targetDateKey")
            )
        } finally {
            roomDb.close()
        }
    }

    @Test
    fun migration10To11_enforcesAttemptIdUniqueness_andSupportsPendingAndCreditedStatuses() {
        val helper = createSeededV10Database(testDbName)
        helper.close()

        val roomDb = openRoomV11(testDbName)
        try {
            val sqlite = roomDb.openHelper.writableDatabase

            // Insert PENDING record with NULL creditedAtMs
            sqlite.execSQL(
                """
                INSERT INTO `bcs_goal_credit_records` (
                    `attemptId`, `examProfileId`, `targetDateKey`,
                    `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                    `status`, `createdAtMs`, `creditedAtMs`
                ) VALUES (
                    'attempt_pending_001', 'BCS_50TH_PRELIM_2025', '2026-11-22',
                    12, 10, 1, 0,
                    '${BcsGoalCreditRecordEntity.STATUS_PENDING}', 1763800000000, NULL
                )
                """.trimIndent()
            )

            // Insert CREDITED record with non-null creditedAtMs
            sqlite.execSQL(
                """
                INSERT INTO `bcs_goal_credit_records` (
                    `attemptId`, `examProfileId`, `targetDateKey`,
                    `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                    `status`, `createdAtMs`, `creditedAtMs`
                ) VALUES (
                    'attempt_credited_002', 'BCS_50TH_PRELIM_2025', '2026-11-22',
                    8, 6, 0, 1,
                    '${BcsGoalCreditRecordEntity.STATUS_CREDITED}', 1763800100000, 1763800105000
                )
                """.trimIndent()
            )

            // Verify attemptId PRIMARY KEY uniqueness rejects duplicate insert
            try {
                sqlite.execSQL(
                    """
                    INSERT INTO `bcs_goal_credit_records` (
                        `attemptId`, `examProfileId`, `targetDateKey`,
                        `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                        `status`, `createdAtMs`, `creditedAtMs`
                    ) VALUES (
                        'attempt_pending_001', 'BCS_50TH_PRELIM_2025', '2026-11-22',
                        99, 99, 1, 0,
                        '${BcsGoalCreditRecordEntity.STATUS_CREDITED}', 1763800200000, 1763800205000
                    )
                    """.trimIndent()
                )
                fail("Duplicate attemptId insert must fail with SQLiteConstraintException")
            } catch (_: SQLiteConstraintException) {
                // Expected primary key uniqueness violation
            }

            // Verify PENDING row values and NULL creditedAtMs
            sqlite.query(
                "SELECT `status`, `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`, `creditedAtMs` " +
                    "FROM `bcs_goal_credit_records` WHERE `attemptId` = 'attempt_pending_001'"
            ).use { c ->
                assertTrue(c.moveToFirst())
                assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, c.getString(0))
                assertEquals(12, c.getInt(1))
                assertEquals(10, c.getInt(2))
                assertEquals(1, c.getInt(3))
                assertEquals(0, c.getInt(4))
                assertTrue("PENDING record must allow NULL creditedAtMs", c.isNull(5))
            }

            // Verify CREDITED row values and non-null creditedAtMs
            sqlite.query(
                "SELECT `status`, `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`, `creditedAtMs` " +
                    "FROM `bcs_goal_credit_records` WHERE `attemptId` = 'attempt_credited_002'"
            ).use { c ->
                assertTrue(c.moveToFirst())
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, c.getString(0))
                assertEquals(8, c.getInt(1))
                assertEquals(6, c.getInt(2))
                assertEquals(0, c.getInt(3))
                assertEquals(1, c.getInt(4))
                assertFalse("CREDITED record must store non-null creditedAtMs", c.isNull(5))
                assertEquals(1763800105000L, c.getLong(5))
            }

            // Verify atomic-style conditional transition PENDING -> CREDITED
            sqlite.execSQL(
                "UPDATE `bcs_goal_credit_records` SET `status` = '${BcsGoalCreditRecordEntity.STATUS_CREDITED}', " +
                    "`creditedAtMs` = 1763800300000 WHERE `attemptId` = 'attempt_pending_001' " +
                    "AND `status` = '${BcsGoalCreditRecordEntity.STATUS_PENDING}'"
            )
            sqlite.query(
                "SELECT `status`, `creditedAtMs` FROM `bcs_goal_credit_records` WHERE `attemptId` = 'attempt_pending_001'"
            ).use { c ->
                assertTrue(c.moveToFirst())
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, c.getString(0))
                assertEquals(1763800300000L, c.getLong(1))
            }
        } finally {
            roomDb.close()
        }

        // Reopen DB to verify migrated table & records persist across close/reopen
        val reopened = openRoomV11(testDbName)
        try {
            val sqlite = reopened.openHelper.writableDatabase
            assertEquals(11, sqlite.version)
            sqlite.query("SELECT COUNT(*) FROM `bcs_goal_credit_records`").use { c ->
                assertTrue(c.moveToFirst())
                assertEquals(2, c.getInt(0))
            }
        } finally {
            reopened.close()
        }
    }
}
