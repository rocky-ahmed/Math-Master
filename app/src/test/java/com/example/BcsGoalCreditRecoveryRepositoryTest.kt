package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDao
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsStudyPlanGoalEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.repository.MathRepository
import com.example.math.BcsGoalCreditLedgerTxnResult
import com.example.math.BcsGoalCreditReceiptStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class BcsGoalCreditRecoveryRepositoryTest {

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var dao: MathDao
    private lateinit var repository: MathRepository
    private val testDbName = "bcs_gc02_stage4b1_recovery_repo_test.db"

    private class GatedMathDao(
        private val delegate: MathDao,
        val applyEntered: CompletableDeferred<Unit>,
        val releaseApply: CompletableDeferred<Unit>,
        val deleteEnteredBeforeRelease: AtomicBoolean = AtomicBoolean(false)
    ) : MathDao by delegate {
        @Volatile
        private var applyReleased = false

        override suspend fun applyBcsCompletionGoalCreditIdempotently(
            attemptId: String,
            creditedAtMs: Long
        ): BcsGoalCreditLedgerTxnResult {
            applyEntered.complete(Unit)
            releaseApply.await()
            val result = delegate.applyBcsCompletionGoalCreditIdempotently(attemptId, creditedAtMs)
            applyReleased = true
            return result
        }

        override suspend fun deletePendingBcsGoalCreditRecordByAttemptId(attemptId: String): Int {
            if (!applyReleased) {
                deleteEnteredBeforeRelease.set(true)
            }
            return delegate.deletePendingBcsGoalCreditRecordByAttemptId(attemptId)
        }
    }

    private val receiptPrefsName = "math_master_bcs_goal_credit_receipts"

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.deleteDatabase(testDbName)
        app.getSharedPreferences(receiptPrefsName, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
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
        repository = MathRepository(dao)
    }

    @After
    fun tearDown() {
        db.close()
        app.deleteDatabase(testDbName)
        app.getSharedPreferences(receiptPrefsName, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    private fun sampleGoal(
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        dateKey: String = "2026-11-22",
        lessonsCompleted: Int = 2,
        questionsSolved: Int = 15,
        revisionsCompleted: Int = 3,
        studyTimeMinutes: Int = 20,
        mockExamsCompleted: Int = 1,
        updatedAt: Long = 1_763_800_000_000L
    ): BcsStudyPlanGoalEntity = BcsStudyPlanGoalEntity(
        goalKey = "${examProfileId}_$dateKey",
        examProfileId = examProfileId,
        dateKey = dateKey,
        lessonsCompleted = lessonsCompleted,
        questionsSolved = questionsSolved,
        revisionsCompleted = revisionsCompleted,
        studyTimeMinutes = studyTimeMinutes,
        mockExamsCompleted = mockExamsCompleted,
        updatedAt = updatedAt
    )

    private fun sampleLedgerRecord(
        attemptId: String,
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        targetDateKey: String = "2026-11-22",
        questionsDelta: Int = 10,
        studyTimeMinutesDelta: Int = 5,
        mockExamsDelta: Int = 1,
        revisionsDelta: Int = 2,
        status: String = BcsGoalCreditRecordEntity.STATUS_PENDING,
        createdAtMs: Long = 1_763_800_100_000L,
        creditedAtMs: Long? = null
    ): BcsGoalCreditRecordEntity = BcsGoalCreditRecordEntity(
        attemptId = attemptId,
        examProfileId = examProfileId,
        targetDateKey = targetDateKey,
        questionsDelta = questionsDelta,
        studyTimeMinutesDelta = studyTimeMinutesDelta,
        mockExamsDelta = mockExamsDelta,
        revisionsDelta = revisionsDelta,
        status = status,
        createdAtMs = createdAtMs,
        creditedAtMs = creditedAtMs
    )

    private fun sampleCompletedAttempt(
        attemptId: String,
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        completedAt: Long = 1_763_800_100_000L
    ): ExamAttemptEntity = ExamAttemptEntity(
        attemptId = attemptId,
        examTitleBn = "৫০তম বিসিএস মক টেস্ট",
        examType = "BCS_PRELIMINARY_MOCK",
        selectedTopicsCsv = "arithmetic",
        difficulty = "medium",
        questionType = "mcq",
        totalQuestions = 10,
        durationLimitSeconds = 600,
        marksPerCorrect = 1.0,
        negativeMarksPerWrong = 0.5,
        status = "COMPLETED",
        currentQuestionIndex = 0,
        elapsedSeconds = 300,
        remainingSeconds = 300,
        questionsJson = "[]",
        answersJson = "{}",
        markedForReviewCsv = "",
        visitedIndicesCsv = "0",
        questionTimeSecondsJson = "{}",
        correctCount = 8,
        wrongCount = 2,
        unansweredCount = 0,
        earnedMarks = 8.0,
        negativeMarksDeducted = 1.0,
        finalScore = 7.0,
        percentage = 70.0,
        accuracyPercent = 80.0,
        startedAt = completedAt - 300_000L,
        updatedAt = completedAt,
        completedAt = completedAt,
        examProfileId = examProfileId
    )

    // =========================================================================
    // TEST 1: Legacy receipt reconciliation marks PENDING -> CREDITED without goal mutation
    // =========================================================================
    @Test
    fun test1_legacyReceiptReconciliation_marksPendingCreditedWithoutGoalMutation() = runBlocking {
        val attemptId = "stage4b1_t1_legacy_reconcile"
        val baselineGoal = sampleGoal()
        val pendingRecord = sampleLedgerRecord(attemptId = attemptId)
        val reconciledAtMs = 1_763_800_555_000L

        dao.upsertBcsStudyGoal(baselineGoal)
        dao.insertBcsGoalCreditRecordStrict(pendingRecord)

        val transitioned = repository.markPendingBcsGoalCreditCreditedWithoutGoalMutation(
            attemptId = attemptId,
            creditedAtMs = reconciledAtMs
        )
        assertTrue("Expected PENDING -> CREDITED legacy reconciliation to return true", transitioned)

        val persistedRecord = dao.getBcsGoalCreditRecordByAttemptId(attemptId)
        assertNotNull("Persisted ledger record must exist", persistedRecord)
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, persistedRecord!!.status)
        assertEquals(reconciledAtMs, persistedRecord.creditedAtMs)
        assertEquals(pendingRecord.examProfileId, persistedRecord.examProfileId)
        assertEquals(pendingRecord.targetDateKey, persistedRecord.targetDateKey)
        assertEquals(pendingRecord.questionsDelta, persistedRecord.questionsDelta)
        assertEquals(pendingRecord.studyTimeMinutesDelta, persistedRecord.studyTimeMinutesDelta)
        assertEquals(pendingRecord.mockExamsDelta, persistedRecord.mockExamsDelta)
        assertEquals(pendingRecord.revisionsDelta, persistedRecord.revisionsDelta)
        assertEquals(pendingRecord.createdAtMs, persistedRecord.createdAtMs)

        // All BCS study goal counters and updatedAt must remain completely unchanged
        val persistedGoal = dao.getBcsStudyGoalByKey(baselineGoal.goalKey)
        assertEquals("Legacy reconciliation must never mutate bcs_study_goals", baselineGoal, persistedGoal)
        assertEquals(1, dao.getAllBcsStudyGoals().first().size)

        // No SharedPreferences receipt should be written by Repository reconciliation
        val receiptStore = BcsGoalCreditReceiptStore(app.getSharedPreferences(receiptPrefsName, Context.MODE_PRIVATE))
        assertNull(receiptStore.readReceipt(attemptId))

        // Subsequent normal ledger credit call must observe AlreadyCredited and not increment goals
        val followUp = repository.applyBcsCompletionGoalCreditFromLedger(
            attemptId = attemptId,
            creditedAtMs = reconciledAtMs + 10_000L
        )
        assertTrue("Follow-up credit must resolve as AlreadyCredited", followUp is BcsGoalCreditLedgerTxnResult.AlreadyCredited)
        assertEquals(baselineGoal, dao.getBcsStudyGoalByKey(baselineGoal.goalKey))
    }

    // =========================================================================
    // TEST 2: Already CREDITED returns true without changing creditedAtMs or mutating goals
    // =========================================================================
    @Test
    fun test2_alreadyCredited_returnsTrueWithoutChangingCreditedAtOrMutatingGoals() = runBlocking {
        val attemptId = "stage4b1_t2_already_credited"
        val originalCreditedAtMs = 1_763_800_222_000L
        val baselineGoal = sampleGoal()
        val creditedRecord = sampleLedgerRecord(
            attemptId = attemptId,
            status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
            creditedAtMs = originalCreditedAtMs
        )

        dao.upsertBcsStudyGoal(baselineGoal)
        dao.insertBcsGoalCreditRecordStrict(creditedRecord)

        val result = repository.markPendingBcsGoalCreditCreditedWithoutGoalMutation(
            attemptId = attemptId,
            creditedAtMs = originalCreditedAtMs + 999_999L
        )
        assertTrue("Already CREDITED record must return true", result)

        val persistedRecord = dao.getBcsGoalCreditRecordByAttemptId(attemptId)
        assertEquals(creditedRecord, persistedRecord)
        assertEquals(originalCreditedAtMs, persistedRecord!!.creditedAtMs)
        assertEquals(baselineGoal, dao.getBcsStudyGoalByKey(baselineGoal.goalKey))
    }

    // =========================================================================
    // TEST 3: Missing ledger returns false, creates no row, and does not mutate goals
    // =========================================================================
    @Test
    fun test3_missingLedger_returnsFalseWithoutFabricatingRowOrMutatingGoals() = runBlocking {
        val baselineGoal = sampleGoal()
        dao.upsertBcsStudyGoal(baselineGoal)

        val missingResult = repository.markPendingBcsGoalCreditCreditedWithoutGoalMutation(
            attemptId = "stage4b1_t3_unknown_attempt",
            creditedAtMs = 1_763_800_333_000L
        )
        val blankResult = repository.markPendingBcsGoalCreditCreditedWithoutGoalMutation(
            attemptId = "   ",
            creditedAtMs = 1_763_800_333_000L
        )

        assertFalse("Missing attemptId must return false", missingResult)
        assertFalse("Blank attemptId must return false", blankResult)
        assertNull(dao.getBcsGoalCreditRecordByAttemptId("stage4b1_t3_unknown_attempt"))
        assertTrue("No ledger rows may be fabricated", dao.getAllBcsGoalCreditRecords().first().isEmpty())
        assertEquals(baselineGoal, dao.getBcsStudyGoalByKey(baselineGoal.goalKey))
    }

    // =========================================================================
    // TEST 4: Invalid/unknown ledger status fails closed without mutation
    // =========================================================================
    @Test
    fun test4_invalidLedgerStatus_failsClosedWithoutModifyingRecordOrGoals() = runBlocking {
        val attemptId = "stage4b1_t4_invalid_status"
        val baselineGoal = sampleGoal()
        val invalidRecord = sampleLedgerRecord(
            attemptId = attemptId,
            status = "UNKNOWN_CORRUPTED_STATUS",
            creditedAtMs = null
        )

        dao.upsertBcsStudyGoal(baselineGoal)
        dao.insertBcsGoalCreditRecordStrict(invalidRecord)

        val result = repository.markPendingBcsGoalCreditCreditedWithoutGoalMutation(
            attemptId = attemptId,
            creditedAtMs = 1_763_800_444_000L
        )

        assertFalse("Unexpected ledger status must fail closed with false", result)
        val persistedRecord = dao.getBcsGoalCreditRecordByAttemptId(attemptId)
        assertEquals("Invalid status record must remain unmodified", invalidRecord, persistedRecord)
        assertEquals(baselineGoal, dao.getBcsStudyGoalByKey(baselineGoal.goalKey))
    }

    // =========================================================================
    // TEST 5: Stale cleanup deletes PENDING only, preserves CREDITED, exam history, and goals
    // =========================================================================
    @Test
    fun test5_cleanupDeletesPendingOnly_preservesCreditedExamHistoryAndGoals() = runBlocking {
        val pendingAttemptId = "stage4b1_t5_pending_stale"
        val creditedAttemptId = "stage4b1_t5_credited_keep"
        val unknownStatusAttemptId = "stage4b1_t5_unknown_keep"
        val baselineGoal = sampleGoal()

        val pendingRecord = sampleLedgerRecord(
            attemptId = pendingAttemptId,
            status = BcsGoalCreditRecordEntity.STATUS_PENDING,
            creditedAtMs = null
        )
        val creditedRecord = sampleLedgerRecord(
            attemptId = creditedAttemptId,
            status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
            creditedAtMs = 1_763_800_500_000L
        )
        val unknownRecord = sampleLedgerRecord(
            attemptId = unknownStatusAttemptId,
            status = "CORRUPTED_STATUS",
            creditedAtMs = null
        )
        val persistedExamAttempt = sampleCompletedAttempt(attemptId = pendingAttemptId)

        dao.upsertBcsStudyGoal(baselineGoal)
        dao.upsertExamAttempt(persistedExamAttempt)
        dao.insertBcsGoalCreditRecordStrict(pendingRecord)
        dao.insertBcsGoalCreditRecordStrict(creditedRecord)
        dao.insertBcsGoalCreditRecordStrict(unknownRecord)

        val deletedPending = repository.deletePendingBcsGoalCreditRecordByAttemptId(pendingAttemptId)
        val deletedCredited = repository.deletePendingBcsGoalCreditRecordByAttemptId(creditedAttemptId)
        val deletedUnknown = repository.deletePendingBcsGoalCreditRecordByAttemptId(unknownStatusAttemptId)
        val deletedMissing = repository.deletePendingBcsGoalCreditRecordByAttemptId("stage4b1_t5_missing")
        val deletedBlank = repository.deletePendingBcsGoalCreditRecordByAttemptId("   ")

        assertEquals("PENDING record must be deleted (1 row)", 1, deletedPending)
        assertEquals("CREDITED record must never be deleted (0 rows)", 0, deletedCredited)
        assertEquals("Non-PENDING status record must not be deleted (0 rows)", 0, deletedUnknown)
        assertEquals("Missing attemptId must delete 0 rows", 0, deletedMissing)
        assertEquals("Blank attemptId must delete 0 rows", 0, deletedBlank)

        assertNull("PENDING ledger row must be gone", dao.getBcsGoalCreditRecordByAttemptId(pendingAttemptId))
        assertEquals("CREDITED ledger row must remain intact", creditedRecord, dao.getBcsGoalCreditRecordByAttemptId(creditedAttemptId))
        assertEquals("Unknown status ledger row must remain intact", unknownRecord, dao.getBcsGoalCreditRecordByAttemptId(unknownStatusAttemptId))
        assertEquals("Cleanup must never delete exam attempt history", persistedExamAttempt, dao.getExamAttemptById(pendingAttemptId))
        assertEquals("Cleanup must never mutate bcs_study_goals", baselineGoal, dao.getBcsStudyGoalByKey(baselineGoal.goalKey))
    }

    // =========================================================================
    // TEST 6: Concurrent serialization of cleanup and credit via bcsStudyGoalMutex
    // =========================================================================
    @Test
    fun test6_concurrentCleanupAndCredit_serializedWithoutDoubleIncrementOrCreditedDeletion() = runBlocking {
        // Part A: Deterministic mutex serialization proof using GatedMathDao
        val gatedAttemptId = "stage4b1_t6_deterministic_mutex"
        val gatedGoal = sampleGoal(
            dateKey = "2026-11-21",
            questionsSolved = 10,
            studyTimeMinutes = 5,
            mockExamsCompleted = 0,
            revisionsCompleted = 1
        )
        val gatedPending = sampleLedgerRecord(
            attemptId = gatedAttemptId,
            targetDateKey = "2026-11-21",
            questionsDelta = 6,
            studyTimeMinutesDelta = 3,
            mockExamsDelta = 1,
            revisionsDelta = 1
        )
        dao.upsertBcsStudyGoal(gatedGoal)
        dao.insertBcsGoalCreditRecordStrict(gatedPending)

        val applyEntered = CompletableDeferred<Unit>()
        val releaseApply = CompletableDeferred<Unit>()
        val gatedDao = GatedMathDao(dao, applyEntered, releaseApply)
        val gatedRepo = MathRepository(gatedDao)

        val creditDeferred = async(Dispatchers.Default) {
            gatedRepo.applyBcsCompletionGoalCreditFromLedger(
                attemptId = gatedAttemptId,
                creditedAtMs = 1_763_800_600_000L
            )
        }
        applyEntered.await()

        val cleanupDeferred = async(Dispatchers.Default) {
            gatedRepo.deletePendingBcsGoalCreditRecordByAttemptId(gatedAttemptId)
        }
        delay(50)
        assertFalse(
            "Cleanup must wait behind bcsStudyGoalMutex while credit holds lock",
            gatedDao.deleteEnteredBeforeRelease.get()
        )
        assertFalse("Cleanup must still be suspended while credit holds bcsStudyGoalMutex", cleanupDeferred.isCompleted)

        releaseApply.complete(Unit)
        val creditResult = creditDeferred.await()
        val cleanupDeleted = cleanupDeferred.await()

        assertTrue("Credit that entered first must apply cleanly", creditResult is BcsGoalCreditLedgerTxnResult.Applied)
        assertEquals("Cleanup serialized after credit must delete 0 rows", 0, cleanupDeleted)
        assertFalse(
            "Cleanup must never have overlapped with credit DAO transaction",
            gatedDao.deleteEnteredBeforeRelease.get()
        )
        val gatedRecord = dao.getBcsGoalCreditRecordByAttemptId(gatedAttemptId)
        assertNotNull("CREDITED record must never be deleted", gatedRecord)
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, gatedRecord!!.status)
        val updatedGatedGoal = dao.getBcsStudyGoalByKey(gatedGoal.goalKey)!!
        assertEquals(16, updatedGatedGoal.questionsSolved)
        assertEquals(8, updatedGatedGoal.studyTimeMinutes)
        assertEquals(1, updatedGatedGoal.mockExamsCompleted)
        assertEquals(2, updatedGatedGoal.revisionsCompleted)

        // Part B: Multi-iteration concurrent race between credit, duplicate credit, and cleanup
        for (i in 1..10) {
            val dateKey = "2026-10-%02d".format(i)
            val raceAttemptId = "stage4b1_t6_race_$i"
            val baseGoal = sampleGoal(
                dateKey = dateKey,
                lessonsCompleted = 0,
                questionsSolved = 5,
                revisionsCompleted = 0,
                studyTimeMinutes = 10,
                mockExamsCompleted = 0
            )
            val pending = sampleLedgerRecord(
                attemptId = raceAttemptId,
                targetDateKey = dateKey,
                questionsDelta = 4,
                studyTimeMinutesDelta = 2,
                mockExamsDelta = 1,
                revisionsDelta = 1
            )
            dao.upsertBcsStudyGoal(baseGoal)
            dao.insertBcsGoalCreditRecordStrict(pending)

            val creditJob1 = async(Dispatchers.Default) {
                repository.applyBcsCompletionGoalCreditFromLedger(
                    attemptId = raceAttemptId,
                    creditedAtMs = 1_763_800_700_000L + i
                )
            }
            val cleanupJob = async(Dispatchers.Default) {
                repository.deletePendingBcsGoalCreditRecordByAttemptId(raceAttemptId)
            }
            val creditJob2 = async(Dispatchers.Default) {
                repository.applyBcsCompletionGoalCreditFromLedger(
                    attemptId = raceAttemptId,
                    creditedAtMs = 1_763_800_800_000L + i
                )
            }

            val results = awaitAll(creditJob1, creditJob2)
            val deletedCount = cleanupJob.await()
            val finalRecord = dao.getBcsGoalCreditRecordByAttemptId(raceAttemptId)
            val finalGoal = dao.getBcsStudyGoalByKey(baseGoal.goalKey)!!

            if (deletedCount == 1) {
                // Cleanup ran first: both credit attempts must see NoPendingRecord and goals must not increment
                assertTrue(
                    "When cleanup wins first, credit calls must see NoPendingRecord",
                    results.all { it is BcsGoalCreditLedgerTxnResult.NoPendingRecord }
                )
                assertNull("Ledger row must remain absent when cleanup won", finalRecord)
                assertEquals(5, finalGoal.questionsSolved)
                assertEquals(10, finalGoal.studyTimeMinutes)
                assertEquals(0, finalGoal.mockExamsCompleted)
                assertEquals(0, finalGoal.revisionsCompleted)
            } else {
                // Credit ran first: exactly one Applied and one AlreadyCredited, CREDITED row preserved, single increment
                assertEquals("Deleted count must be 0 when credit won", 0, deletedCount)
                assertEquals(1, results.count { it is BcsGoalCreditLedgerTxnResult.Applied })
                assertEquals(1, results.count { it is BcsGoalCreditLedgerTxnResult.AlreadyCredited })
                assertNotNull("CREDITED row must never be deleted", finalRecord)
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, finalRecord!!.status)
                assertEquals(9, finalGoal.questionsSolved)
                assertEquals(12, finalGoal.studyTimeMinutes)
                assertEquals(1, finalGoal.mockExamsCompleted)
                assertEquals(1, finalGoal.revisionsCompleted)
            }
        }
    }
}
