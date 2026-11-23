package com.example

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDao
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsStudyPlanGoalEntity
import com.example.data.model.ExamAttemptEntity
import com.example.math.BcsGoalCreditLedgerTxnResult
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
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
class BcsGoalCreditDaoTransactionTest {

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var dao: MathDao
    private val testDbName = "bcs_gc02_stage2_dao_txn_test.db"

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.deleteDatabase(testDbName)
        db = Room.databaseBuilder(app, MathDatabase::class.java, testDbName)
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
        dao = db.mathDao()
    }

    @After
    fun tearDown() {
        db.close()
        app.deleteDatabase(testDbName)
    }

    private fun sampleCompletedAttempt(
        attemptId: String,
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        correctCount: Int = 6,
        wrongCount: Int = 2,
        elapsedSeconds: Int = 180,
        completedAt: Long = 1_763_800_000_000L
    ): ExamAttemptEntity = ExamAttemptEntity(
        attemptId = attemptId,
        examTitleBn = "৫০তম বিসিএস মক টেস্ট",
        examType = "BCS_PRELIMINARY_MOCK",
        selectedTopicsCsv = "arithmetic",
        difficulty = "medium",
        questionType = "mcq",
        totalQuestions = correctCount + wrongCount,
        durationLimitSeconds = 600,
        marksPerCorrect = 1.0,
        negativeMarksPerWrong = 0.5,
        status = "COMPLETED",
        currentQuestionIndex = 0,
        elapsedSeconds = elapsedSeconds,
        remainingSeconds = 0,
        questionsJson = "[]",
        answersJson = "{}",
        markedForReviewCsv = "",
        visitedIndicesCsv = "0",
        questionTimeSecondsJson = "{}",
        correctCount = correctCount,
        wrongCount = wrongCount,
        unansweredCount = 0,
        earnedMarks = correctCount.toDouble(),
        negativeMarksDeducted = wrongCount * 0.5,
        finalScore = correctCount - (wrongCount * 0.5),
        percentage = 75.0,
        accuracyPercent = 75.0,
        startedAt = completedAt - (elapsedSeconds * 1000L),
        updatedAt = completedAt,
        completedAt = completedAt,
        examProfileId = examProfileId
    )

    private fun samplePendingRecord(
        attemptId: String,
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        targetDateKey: String = "2026-11-22",
        questionsDelta: Int = 8,
        studyTimeMinutesDelta: Int = 3,
        mockExamsDelta: Int = 1,
        revisionsDelta: Int = 0,
        createdAtMs: Long = 1_763_800_000_000L
    ): BcsGoalCreditRecordEntity = BcsGoalCreditRecordEntity(
        attemptId = attemptId,
        examProfileId = examProfileId,
        targetDateKey = targetDateKey,
        questionsDelta = questionsDelta,
        studyTimeMinutesDelta = studyTimeMinutesDelta,
        mockExamsDelta = mockExamsDelta,
        revisionsDelta = revisionsDelta,
        status = BcsGoalCreditRecordEntity.STATUS_PENDING,
        createdAtMs = createdAtMs,
        creditedAtMs = null
    )

    // =========================================================================
    // TEST 1: Atomic pending insertion + consistency & rollback guarantees
    // =========================================================================
    @Test
    fun test1_atomicPendingInsertion_persistsWithFinalizedAttempt_rejectsInconsistentAndDuplicate_andRollsBackOnFailure() = runBlocking {
        val attemptId = "bcs_mock_t1_001"
        val attempt = sampleCompletedAttempt(attemptId = attemptId)
        val pending = samplePendingRecord(attemptId = attemptId)

        dao.finalizeExamAttemptTransaction(
            completedAttempt = attempt,
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = pending
        )

        val persistedAttempt = dao.getExamAttemptById(attemptId)
        assertNotNull(persistedAttempt)
        assertEquals("COMPLETED", persistedAttempt!!.status)

        val persistedLedger = dao.getBcsGoalCreditRecordByAttemptId(attemptId)
        assertNotNull("Pending ledger row must persist atomically with finalized attempt", persistedLedger)
        assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, persistedLedger!!.status)
        assertEquals("BCS_50TH_PRELIM_2025", persistedLedger.examProfileId)
        assertEquals("2026-11-22", persistedLedger.targetDateKey)
        assertEquals(8, persistedLedger.questionsDelta)
        assertEquals(3, persistedLedger.studyTimeMinutesDelta)
        assertEquals(1, persistedLedger.mockExamsDelta)
        assertEquals(0, persistedLedger.revisionsDelta)
        assertNull(persistedLedger.creditedAtMs)

        // Null pending record preserves existing behavior with zero ledger insert
        val nonPendingAttemptId = "bcs_mock_t1_no_pending"
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = nonPendingAttemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList()
        )
        assertNotNull(dao.getExamAttemptById(nonPendingAttemptId))
        assertNull(dao.getBcsGoalCreditRecordByAttemptId(nonPendingAttemptId))

        // Reject mismatched attemptId without persisting either attempt or ledger row
        val mismatchIdAttempt = "bcs_mock_t1_mismatch_id"
        try {
            dao.finalizeExamAttemptTransaction(
                completedAttempt = sampleCompletedAttempt(attemptId = mismatchIdAttempt),
                perTopicStats = emptyList(),
                bcsTopicUpdates = emptyList(),
                questionMistakeOutcomes = emptyList(),
                pendingBcsGoalCredit = samplePendingRecord(attemptId = "different_attempt_id")
            )
            fail("Mismatched attemptId must be rejected")
        } catch (_: IllegalArgumentException) {
            // Expected
        }
        assertNull(dao.getExamAttemptById(mismatchIdAttempt))
        assertNull(dao.getBcsGoalCreditRecordByAttemptId("different_attempt_id"))

        // Reject mismatched examProfileId without persisting either attempt or ledger row
        val mismatchProfileAttempt = "bcs_mock_t1_mismatch_profile"
        try {
            dao.finalizeExamAttemptTransaction(
                completedAttempt = sampleCompletedAttempt(
                    attemptId = mismatchProfileAttempt,
                    examProfileId = "BCS_50TH_PRELIM_2025"
                ),
                perTopicStats = emptyList(),
                bcsTopicUpdates = emptyList(),
                questionMistakeOutcomes = emptyList(),
                pendingBcsGoalCredit = samplePendingRecord(
                    attemptId = mismatchProfileAttempt,
                    examProfileId = "BCS_WRITTEN_008_MATH"
                )
            )
            fail("Mismatched examProfileId must be rejected")
        } catch (_: IllegalArgumentException) {
            // Expected
        }
        assertNull(dao.getExamAttemptById(mismatchProfileAttempt))
        assertNull(dao.getBcsGoalCreditRecordByAttemptId(mismatchProfileAttempt))

        // Conflicting duplicate attemptId must fail with SQLiteConstraintException (not REPLACE or IGNORE)
        try {
            dao.finalizeExamAttemptTransaction(
                completedAttempt = attempt,
                perTopicStats = emptyList(),
                bcsTopicUpdates = emptyList(),
                questionMistakeOutcomes = emptyList(),
                pendingBcsGoalCredit = samplePendingRecord(
                    attemptId = attemptId,
                    questionsDelta = 99
                )
            )
            fail("Conflicting duplicate attemptId must throw SQLiteConstraintException")
        } catch (_: SQLiteConstraintException) {
            // Expected ABORT conflict behavior
        }
        assertEquals("Existing ledger row must remain unaltered", 8, dao.getBcsGoalCreditRecordByAttemptId(attemptId)!!.questionsDelta)

        // If finalization fails later in the transaction, the pending ledger row must roll back atomically
        val rollbackAttemptId = "bcs_mock_t1_rollback"
        val sqlite = db.openHelper.writableDatabase
        sqlite.execSQL(
            """
            CREATE TEMP TRIGGER fail_quiz_scores_insert
            BEFORE INSERT ON quiz_scores
            BEGIN
                SELECT RAISE(ABORT, 'Injected finalization failure after ledger insert');
            END;
            """.trimIndent()
        )
        try {
            try {
                dao.finalizeExamAttemptTransaction(
                    completedAttempt = sampleCompletedAttempt(attemptId = rollbackAttemptId),
                    perTopicStats = emptyList(),
                    bcsTopicUpdates = emptyList(),
                    questionMistakeOutcomes = emptyList(),
                    pendingBcsGoalCredit = samplePendingRecord(attemptId = rollbackAttemptId)
                )
                fail("Injected quiz_scores failure must abort finalization transaction")
            } catch (e: SQLiteConstraintException) {
                assertTrue(
                    "Expected injected quiz_scores trigger failure, got: ${e.message}",
                    e.message?.contains("Injected finalization failure after ledger insert") == true
                )
            }
        } finally {
            sqlite.execSQL("DROP TRIGGER IF EXISTS fail_quiz_scores_insert")
        }
        assertNull("Rolled-back finalization must not leave ExamAttemptEntity committed", dao.getExamAttemptById(rollbackAttemptId))
        assertNull("Rolled-back finalization must not leave PENDING ledger row committed", dao.getBcsGoalCreditRecordByAttemptId(rollbackAttemptId))
    }

    // =========================================================================
    // TEST 2: No pending record -> NoPendingRecord, zero ledger/goal mutation
    // =========================================================================
    @Test
    fun test2_noPendingRecord_doesNotInsertLedgerOrModifyAnyGoal() = runBlocking {
        val result = dao.applyBcsCompletionGoalCreditIdempotently(
            attemptId = "unknown_attempt_999",
            creditedAtMs = 1_763_801_000_000L
        )
        assertTrue("Expected NoPendingRecord, got $result", result is BcsGoalCreditLedgerTxnResult.NoPendingRecord)
        assertEquals("unknown_attempt_999", (result as BcsGoalCreditLedgerTxnResult.NoPendingRecord).attemptId)

        assertNull("No ledger record may be created", dao.getBcsGoalCreditRecordByAttemptId("unknown_attempt_999"))
        assertTrue("No BCS study goal rows may be created", dao.getAllBcsStudyGoals().first().isEmpty())

        val blankResult = dao.applyBcsCompletionGoalCreditIdempotently(
            attemptId = "   ",
            creditedAtMs = 1_763_801_000_000L
        )
        assertTrue(blankResult is BcsGoalCreditLedgerTxnResult.NoPendingRecord)
        assertTrue(dao.getAllBcsStudyGoals().first().isEmpty())
    }

    // =========================================================================
    // TEST 3: Successful credit transitions PENDING -> CREDITED & increments goal
    // =========================================================================
    @Test
    fun test3_successfulCredit_transitionsPendingToCredited_andIncrementsPersistedGoalValues() = runBlocking {
        // Seed an existing daily goal row to verify additive increment preserves lessonsCompleted
        dao.upsertBcsStudyGoal(
            BcsStudyPlanGoalEntity(
                goalKey = "BCS_50TH_PRELIM_2025_2026-11-22",
                examProfileId = "BCS_50TH_PRELIM_2025",
                dateKey = "2026-11-22",
                lessonsCompleted = 3,
                questionsSolved = 10,
                revisionsCompleted = 1,
                studyTimeMinutes = 15,
                mockExamsCompleted = 1,
                updatedAt = 1_763_799_000_000L
            )
        )

        val attemptId = "bcs_mode_mistake_practice_t3_001"
        val pending = samplePendingRecord(
            attemptId = attemptId,
            examProfileId = "BCS_50TH_PRELIM_2025",
            targetDateKey = "2026-11-22",
            questionsDelta = 7,
            studyTimeMinutesDelta = 4,
            mockExamsDelta = 0,
            revisionsDelta = 1
        )
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = pending
        )

        val creditedAt = 1_763_802_500_000L
        val result = dao.applyBcsCompletionGoalCreditIdempotently(
            attemptId = attemptId,
            creditedAtMs = creditedAt
        )
        assertTrue("Expected Applied, got $result", result is BcsGoalCreditLedgerTxnResult.Applied)
        val applied = result as BcsGoalCreditLedgerTxnResult.Applied
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, applied.record.status)
        assertEquals(creditedAt, applied.record.creditedAtMs)

        // Inspect actual persisted SQLite state
        val persistedLedger = dao.getBcsGoalCreditRecordByAttemptId(attemptId)!!
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, persistedLedger.status)
        assertEquals(creditedAt, persistedLedger.creditedAtMs)
        assertTrue(dao.getPendingBcsGoalCreditRecords().isEmpty())

        val persistedGoal = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-22")!!
        assertEquals("Existing lessonsCompleted must be preserved", 3, persistedGoal.lessonsCompleted)
        assertEquals(17, persistedGoal.questionsSolved)
        assertEquals(2, persistedGoal.revisionsCompleted)
        assertEquals(19, persistedGoal.studyTimeMinutes)
        assertEquals(1, persistedGoal.mockExamsCompleted)
        assertEquals(creditedAt, persistedGoal.updatedAt)
    }

    // =========================================================================
    // TEST 4: Duplicate credit returns AlreadyCredited and does not re-increment
    // =========================================================================
    @Test
    fun test4_duplicateCredit_returnsAlreadyCredited_andNeverIncrementsGoalTwice() = runBlocking {
        val attemptId = "bcs_mock_t4_dup"
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attemptId,
                questionsDelta = 5,
                studyTimeMinutesDelta = 2,
                mockExamsDelta = 1,
                revisionsDelta = 0
            )
        )

        val firstCreditedAt = 1_763_803_000_000L
        val firstResult = dao.applyBcsCompletionGoalCreditIdempotently(attemptId, firstCreditedAt)
        assertTrue(firstResult is BcsGoalCreditLedgerTxnResult.Applied)

        val secondResult = dao.applyBcsCompletionGoalCreditIdempotently(attemptId, firstCreditedAt + 60_000L)
        assertTrue("Second call must return AlreadyCredited, got $secondResult", secondResult is BcsGoalCreditLedgerTxnResult.AlreadyCredited)
        val already = secondResult as BcsGoalCreditLedgerTxnResult.AlreadyCredited
        assertEquals(firstCreditedAt, already.record.creditedAtMs)

        // Verify persisted ledger and goal remained strictly at single-credit totals
        val persistedLedger = dao.getBcsGoalCreditRecordByAttemptId(attemptId)!!
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, persistedLedger.status)
        assertEquals("Original creditedAtMs must not be altered on duplicate call", firstCreditedAt, persistedLedger.creditedAtMs)

        val persistedGoal = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-22")!!
        assertEquals(5, persistedGoal.questionsSolved)
        assertEquals(2, persistedGoal.studyTimeMinutes)
        assertEquals(1, persistedGoal.mockExamsCompleted)
        assertEquals(0, persistedGoal.revisionsCompleted)
    }

    // =========================================================================
    // TEST 5: Profile and targetDateKey isolation across bcs_study_goals rows
    // =========================================================================
    @Test
    fun test5_profileAndDateIsolation_updatesOnlyMatchingGoalRow() = runBlocking {
        val a1 = "bcs_mock_p1_day1"
        val a2 = "bcs_mock_p1_day2"
        val a3 = "bcs_mock_p2_day1"

        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = a1, examProfileId = "BCS_50TH_PRELIM_2025"),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = a1,
                examProfileId = "BCS_50TH_PRELIM_2025",
                targetDateKey = "2026-11-21",
                questionsDelta = 4,
                studyTimeMinutesDelta = 2,
                mockExamsDelta = 1,
                revisionsDelta = 0
            )
        )
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = a2, examProfileId = "BCS_50TH_PRELIM_2025"),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = a2,
                examProfileId = "BCS_50TH_PRELIM_2025",
                targetDateKey = "2026-11-22",
                questionsDelta = 9,
                studyTimeMinutesDelta = 5,
                mockExamsDelta = 0,
                revisionsDelta = 1
            )
        )
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = a3, examProfileId = "BCS_WRITTEN_008_MATH"),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = a3,
                examProfileId = "BCS_WRITTEN_008_MATH",
                targetDateKey = "2026-11-21",
                questionsDelta = 11,
                studyTimeMinutesDelta = 8,
                mockExamsDelta = 1,
                revisionsDelta = 0
            )
        )

        assertTrue(dao.applyBcsCompletionGoalCreditIdempotently(a1, 1_763_804_001_000L) is BcsGoalCreditLedgerTxnResult.Applied)
        assertTrue(dao.applyBcsCompletionGoalCreditIdempotently(a2, 1_763_804_002_000L) is BcsGoalCreditLedgerTxnResult.Applied)
        assertTrue(dao.applyBcsCompletionGoalCreditIdempotently(a3, 1_763_804_003_000L) is BcsGoalCreditLedgerTxnResult.Applied)

        val allGoals = dao.getAllBcsStudyGoals().first()
        assertEquals(3, allGoals.size)

        val g1 = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-21")!!
        assertEquals(4, g1.questionsSolved)
        assertEquals(2, g1.studyTimeMinutes)
        assertEquals(1, g1.mockExamsCompleted)
        assertEquals(0, g1.revisionsCompleted)

        val g2 = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-22")!!
        assertEquals(9, g2.questionsSolved)
        assertEquals(5, g2.studyTimeMinutes)
        assertEquals(0, g2.mockExamsCompleted)
        assertEquals(1, g2.revisionsCompleted)

        val g3 = dao.getBcsStudyGoalByKey("BCS_WRITTEN_008_MATH_2026-11-21")!!
        assertEquals(11, g3.questionsSolved)
        assertEquals(8, g3.studyTimeMinutes)
        assertEquals(1, g3.mockExamsCompleted)
        assertEquals(0, g3.revisionsCompleted)
    }

    // =========================================================================
    // TEST 6: Safe deletion removes PENDING row but preserves CREDITED row
    // =========================================================================
    @Test
    fun test6_safeDeletion_removesPendingLedgerRow_andPreservesCreditedLedgerRow() = runBlocking {
        val pendingAttemptId = "bcs_mock_t6_pending_del"
        val creditedAttemptId = "bcs_mock_t6_credited_del"

        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = pendingAttemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(attemptId = pendingAttemptId)
        )
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = creditedAttemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(attemptId = creditedAttemptId, questionsDelta = 6)
        )

        val creditedAt = 1_763_805_000_000L
        assertTrue(dao.applyBcsCompletionGoalCreditIdempotently(creditedAttemptId, creditedAt) is BcsGoalCreditLedgerTxnResult.Applied)

        // Delete attempt with PENDING ledger row -> both attempt and PENDING ledger row removed
        dao.deleteExamAttemptSafely(pendingAttemptId, "2026-11-22")
        assertNull(dao.getExamAttemptById(pendingAttemptId))
        assertNull("PENDING ledger row must be removed when attempt is deleted", dao.getBcsGoalCreditRecordByAttemptId(pendingAttemptId))

        // Delete attempt with CREDITED ledger row -> attempt removed, CREDITED ledger row preserved verbatim
        dao.deleteExamAttemptSafely(creditedAttemptId, "2026-11-22")
        assertNull(dao.getExamAttemptById(creditedAttemptId))
        val preserved = dao.getBcsGoalCreditRecordByAttemptId(creditedAttemptId)
        assertNotNull("CREDITED ledger row must be preserved on attempt deletion", preserved)
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, preserved!!.status)
        assertEquals(creditedAt, preserved.creditedAtMs)
        assertEquals(6, preserved.questionsDelta)
    }

    // =========================================================================
    // TEST 7: Goal mutation failure inside transaction rolls back BOTH tables
    // =========================================================================
    @Test
    fun test7_transactionRollback_goalMutationFailureLeavesLedgerPendingAndGoalUnchanged() = runBlocking {
        val goalKey = "BCS_50TH_PRELIM_2025_2026-11-22"
        dao.upsertBcsStudyGoal(
            BcsStudyPlanGoalEntity(
                goalKey = goalKey,
                examProfileId = "BCS_50TH_PRELIM_2025",
                dateKey = "2026-11-22",
                lessonsCompleted = 1,
                questionsSolved = 10,
                revisionsCompleted = 0,
                studyTimeMinutes = 12,
                mockExamsCompleted = 1,
                updatedAt = 1_763_800_000_000L
            )
        )

        val attemptId = "bcs_mock_t7_rollback"
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attemptId,
                questionsDelta = 5,
                studyTimeMinutesDelta = 3,
                mockExamsDelta = 1
            )
        )

        // Force upsertBcsStudyGoal (INSERT OR REPLACE on bcs_study_goals) to abort inside the SQLite transaction
        val sqlite = db.openHelper.writableDatabase
        sqlite.execSQL(
            """
            CREATE TEMP TRIGGER fail_bcs_study_goals_insert
            BEFORE INSERT ON bcs_study_goals
            BEGIN
                SELECT RAISE(ABORT, 'Injected bcs_study_goals insert failure');
            END;
            """.trimIndent()
        )
        sqlite.execSQL(
            """
            CREATE TEMP TRIGGER fail_bcs_study_goals_update
            BEFORE UPDATE ON bcs_study_goals
            BEGIN
                SELECT RAISE(ABORT, 'Injected bcs_study_goals update failure');
            END;
            """.trimIndent()
        )

        try {
            try {
                dao.applyBcsCompletionGoalCreditIdempotently(attemptId, 1_763_806_000_000L)
                fail("Goal mutation trigger must throw SQLiteConstraintException")
            } catch (e: SQLiteConstraintException) {
                assertTrue(
                    "Expected injected bcs_study_goals trigger failure, got: ${e.message}",
                    e.message?.contains("Injected bcs_study_goals insert failure") == true ||
                        e.message?.contains("Injected bcs_study_goals update failure") == true
                )
            }
        } finally {
            sqlite.execSQL("DROP TRIGGER IF EXISTS fail_bcs_study_goals_insert")
            sqlite.execSQL("DROP TRIGGER IF EXISTS fail_bcs_study_goals_update")
        }

        // Verify complete rollback: ledger is STILL PENDING (creditedAtMs == null) and goal counters are unchanged
        val ledgerAfterAbort = dao.getBcsGoalCreditRecordByAttemptId(attemptId)!!
        assertEquals("Ledger must remain PENDING after rolled-back goal mutation", BcsGoalCreditRecordEntity.STATUS_PENDING, ledgerAfterAbort.status)
        assertNull("creditedAtMs must roll back to null", ledgerAfterAbort.creditedAtMs)

        val goalAfterAbort = dao.getBcsStudyGoalByKey(goalKey)!!
        assertEquals(10, goalAfterAbort.questionsSolved)
        assertEquals(12, goalAfterAbort.studyTimeMinutes)
        assertEquals(1, goalAfterAbort.mockExamsCompleted)

        // Subsequent retry after transient failure succeeds cleanly once
        val retryResult = dao.applyBcsCompletionGoalCreditIdempotently(attemptId, 1_763_806_100_000L)
        assertTrue(retryResult is BcsGoalCreditLedgerTxnResult.Applied)
        val goalAfterRetry = dao.getBcsStudyGoalByKey(goalKey)!!
        assertEquals(15, goalAfterRetry.questionsSolved)
        assertEquals(15, goalAfterRetry.studyTimeMinutes)
        assertEquals(2, goalAfterRetry.mockExamsCompleted)
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, dao.getBcsGoalCreditRecordByAttemptId(attemptId)!!.status)
    }

    // =========================================================================
    // TEST 8: Concurrent duplicate requests apply strictly once
    // =========================================================================
    @Test
    fun test8_concurrentDuplicateRequests_applyStrictlyOnce() = runBlocking {
        val attemptId = "bcs_mock_t8_concurrent"
        dao.finalizeExamAttemptTransaction(
            completedAttempt = sampleCompletedAttempt(attemptId = attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attemptId,
                questionsDelta = 6,
                studyTimeMinutesDelta = 4,
                mockExamsDelta = 1,
                revisionsDelta = 1
            )
        )

        val workerCount = 6
        val startBarrier = CountDownLatch(1)
        val doneLatch = CountDownLatch(workerCount)
        val pool = Executors.newFixedThreadPool(workerCount)
        val results = CopyOnWriteArrayList<BcsGoalCreditLedgerTxnResult>()
        val errors = CopyOnWriteArrayList<Throwable>()

        repeat(workerCount) { idx ->
            pool.submit {
                try {
                    startBarrier.await(5, TimeUnit.SECONDS)
                    val res = runBlocking(Dispatchers.IO) {
                        dao.applyBcsCompletionGoalCreditIdempotently(
                            attemptId = attemptId,
                            creditedAtMs = 1_763_807_000_000L + idx
                        )
                    }
                    results.add(res)
                } catch (t: Throwable) {
                    errors.add(t)
                } finally {
                    doneLatch.countDown()
                }
            }
        }
        startBarrier.countDown()
        assertTrue("Concurrent workers timed out", doneLatch.await(15, TimeUnit.SECONDS))
        pool.shutdownNow()

        assertTrue("Unexpected worker exceptions: $errors", errors.isEmpty())
        assertEquals(workerCount, results.size)
        assertEquals(
            "Exactly one concurrent call must return Applied",
            1,
            results.count { it is BcsGoalCreditLedgerTxnResult.Applied }
        )
        assertEquals(
            "All remaining concurrent calls must return AlreadyCredited",
            workerCount - 1,
            results.count { it is BcsGoalCreditLedgerTxnResult.AlreadyCredited }
        )

        val persistedLedger = dao.getBcsGoalCreditRecordByAttemptId(attemptId)!!
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, persistedLedger.status)
        assertNotNull(persistedLedger.creditedAtMs)

        val persistedGoal = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-22")!!
        assertEquals("Goal questionsSolved must be incremented strictly once", 6, persistedGoal.questionsSolved)
        assertEquals(4, persistedGoal.studyTimeMinutes)
        assertEquals(1, persistedGoal.mockExamsCompleted)
        assertEquals(1, persistedGoal.revisionsCompleted)
    }

    // =========================================================================
    // TEST 9: Invalid or unexpected status fails closed with zero goal mutation
    // =========================================================================
    @Test
    fun test9_invalidStatusOrCorruptPendingFields_failsClosedWithoutGoalIncrement() = runBlocking {
        val sqlite = db.openHelper.writableDatabase

        // 1. Unexpected status string (e.g. "UNKNOWN" and lowercase "pending")
        sqlite.execSQL(
            """
            INSERT INTO `bcs_goal_credit_records` (
                `attemptId`, `examProfileId`, `targetDateKey`,
                `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                `status`, `createdAtMs`, `creditedAtMs`
            ) VALUES (
                'attempt_bad_status', 'BCS_50TH_PRELIM_2025', '2026-11-22',
                5, 2, 1, 0,
                'UNKNOWN_STATUS', 1763808000000, NULL
            )
            """.trimIndent()
        )
        sqlite.execSQL(
            """
            INSERT INTO `bcs_goal_credit_records` (
                `attemptId`, `examProfileId`, `targetDateKey`,
                `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                `status`, `createdAtMs`, `creditedAtMs`
            ) VALUES (
                'attempt_lowercase_status', 'BCS_50TH_PRELIM_2025', '2026-11-22',
                5, 2, 1, 0,
                'pending', 1763808001000, NULL
            )
            """.trimIndent()
        )
        // 2. PENDING row with invalid negative delta and blank profileId
        sqlite.execSQL(
            """
            INSERT INTO `bcs_goal_credit_records` (
                `attemptId`, `examProfileId`, `targetDateKey`,
                `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                `status`, `createdAtMs`, `creditedAtMs`
            ) VALUES (
                'attempt_negative_delta', 'BCS_50TH_PRELIM_2025', '2026-11-22',
                -2, 1, 0, 0,
                'PENDING', 1763808002000, NULL
            )
            """.trimIndent()
        )
        sqlite.execSQL(
            """
            INSERT INTO `bcs_goal_credit_records` (
                `attemptId`, `examProfileId`, `targetDateKey`,
                `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                `status`, `createdAtMs`, `creditedAtMs`
            ) VALUES (
                'attempt_blank_profile', '', '2026-11-22',
                4, 1, 0, 0,
                'PENDING', 1763808003000, NULL
            )
            """.trimIndent()
        )

        val r1 = dao.applyBcsCompletionGoalCreditIdempotently("attempt_bad_status", 1_763_808_100_000L)
        val r2 = dao.applyBcsCompletionGoalCreditIdempotently("attempt_lowercase_status", 1_763_808_100_000L)
        val r3 = dao.applyBcsCompletionGoalCreditIdempotently("attempt_negative_delta", 1_763_808_100_000L)
        val r4 = dao.applyBcsCompletionGoalCreditIdempotently("attempt_blank_profile", 1_763_808_100_000L)

        assertTrue(r1 is BcsGoalCreditLedgerTxnResult.InvalidLedgerState)
        assertTrue(r2 is BcsGoalCreditLedgerTxnResult.InvalidLedgerState)
        assertTrue(r3 is BcsGoalCreditLedgerTxnResult.InvalidLedgerState)
        assertTrue(r4 is BcsGoalCreditLedgerTxnResult.InvalidLedgerState)

        // Rows remain unaltered and ZERO bcs_study_goals rows were created or modified
        assertEquals("UNKNOWN_STATUS", dao.getBcsGoalCreditRecordByAttemptId("attempt_bad_status")!!.status)
        assertEquals("pending", dao.getBcsGoalCreditRecordByAttemptId("attempt_lowercase_status")!!.status)
        assertEquals("PENDING", dao.getBcsGoalCreditRecordByAttemptId("attempt_negative_delta")!!.status)
        assertNull(dao.getBcsGoalCreditRecordByAttemptId("attempt_negative_delta")!!.creditedAtMs)
        assertTrue("Invalid ledger states must never create or mutate bcs_study_goals", dao.getAllBcsStudyGoals().first().isEmpty())
    }
}
