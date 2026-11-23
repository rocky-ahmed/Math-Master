package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsStudyPlanGoalEntity
import com.example.data.model.ExamAttemptEntity
import com.example.math.BcsExamProfile
import com.example.math.BcsGoalCreditDelta
import com.example.math.BcsGoalCreditReceiptStore
import com.example.math.BcsGoalCreditState
import com.example.math.MonotonicClock
import com.example.ui.MathViewModel
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class BcsGoalCreditStartupRecoveryTest {

    private class FakeMonotonicClock(initialMs: Long = 10_000L) : MonotonicClock {
        private val now = AtomicLong(initialMs)
        override fun elapsedRealtime(): Long = now.get()
        fun advanceSeconds(seconds: Long) {
            now.addAndGet(seconds * 1_000L)
        }
    }

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var receiptStore: BcsGoalCreditReceiptStore

    @Before
    fun setUp() {
        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = false
        app = ApplicationProvider.getApplicationContext()
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        receiptStore = BcsGoalCreditReceiptStore(prefs)
        db = MathDatabase.getInstance(app)
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = false
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun awaitJobWithMainLooper(job: Job, timeoutMs: Long = 10_000L) {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (!job.isCompleted) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (job.isCompleted) break
            if (System.nanoTime() > deadline) {
                throw AssertionError("Timed out after ${timeoutMs}ms waiting for Job completion")
            }
            Thread.yield()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun <T> awaitWithMainLooper(deferred: CompletableDeferred<T>, timeoutMs: Long = 10_000L): T {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (!deferred.isCompleted) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (deferred.isCompleted) break
            if (System.nanoTime() > deadline) {
                throw AssertionError("Timed out after ${timeoutMs}ms waiting for CompletableDeferred")
            }
            Thread.yield()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        return runBlocking { deferred.await() }
    }

    private fun sampleExamAttempt(
        attemptId: String,
        status: String = "COMPLETED",
        examProfileId: String = BcsExamProfile.BCS_50TH_PRELIM.id,
        completedAt: Long? = if (status == "COMPLETED") 1_763_800_000_000L else null
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
        status = status,
        currentQuestionIndex = 0,
        elapsedSeconds = 240,
        remainingSeconds = 360,
        questionsJson = "[]",
        answersJson = "{}",
        markedForReviewCsv = "",
        visitedIndicesCsv = "0",
        questionTimeSecondsJson = "{}",
        correctCount = 7,
        wrongCount = 3,
        unansweredCount = 0,
        earnedMarks = 7.0,
        negativeMarksDeducted = 1.5,
        finalScore = 5.5,
        percentage = 55.0,
        accuracyPercent = 70.0,
        startedAt = 1_763_799_760_000L,
        updatedAt = 1_763_800_000_000L,
        completedAt = completedAt,
        examProfileId = examProfileId
    )

    private fun sampleLedgerRecord(
        attemptId: String,
        examProfileId: String = BcsExamProfile.BCS_50TH_PRELIM.id,
        targetDateKey: String = "2026-11-22",
        questionsDelta: Int = 8,
        studyTimeMinutesDelta: Int = 4,
        mockExamsDelta: Int = 1,
        revisionsDelta: Int = 1,
        status: String = BcsGoalCreditRecordEntity.STATUS_PENDING,
        createdAtMs: Long = 1_763_800_000_000L,
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

    private fun sampleGoal(
        examProfileId: String = BcsExamProfile.BCS_50TH_PRELIM.id,
        dateKey: String = "2026-11-22",
        questionsSolved: Int = 10,
        studyTimeMinutes: Int = 12,
        mockExamsCompleted: Int = 1,
        revisionsCompleted: Int = 2,
        updatedAt: Long = 1_763_799_000_000L
    ): BcsStudyPlanGoalEntity = BcsStudyPlanGoalEntity(
        goalKey = "${examProfileId}_$dateKey",
        examProfileId = examProfileId,
        dateKey = dateKey,
        lessonsCompleted = 0,
        questionsSolved = questionsSolved,
        revisionsCompleted = revisionsCompleted,
        studyTimeMinutes = studyTimeMinutes,
        mockExamsCompleted = mockExamsCompleted,
        updatedAt = updatedAt
    )

    // =========================================================================
    // TEST 1: COMPLETED attempt + PENDING row + no legacy receipt recovers once via Room ledger
    // =========================================================================
    @Test
    fun test1_completedAttemptWithPendingRowAndNoLegacyReceipt_recoversOnceViaRoomLedger() {
        val attemptId = "stage4b2a_t1_recover_once"
        val dateKey = "2026-11-21"
        val baselineGoal = sampleGoal(
            dateKey = dateKey,
            questionsSolved = 10,
            studyTimeMinutes = 15,
            mockExamsCompleted = 1,
            revisionsCompleted = 2
        )
        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = attemptId, status = "COMPLETED"))
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = attemptId,
                    targetDateKey = dateKey,
                    questionsDelta = 6,
                    studyTimeMinutesDelta = 4,
                    mockExamsDelta = 1,
                    revisionsDelta = 1
                )
            )
        }
        // Deliberately do NOT set any SharedPreferences WP4.1 marker or legacy receipt:
        // the durable Room PENDING row alone authorizes recovery.
        assertFalse(receiptStore.isWp41Attempt(attemptId))
        assertNull(receiptStore.readReceipt(attemptId))

        // Cold restart: ViewModel init automatically triggers startup recovery sweep.
        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val recoveryJob = vm.getBcsGoalCreditStartupRecoveryJobForTesting()
        assertNotNull("Startup recovery job must launch in init", recoveryJob)
        awaitJobWithMainLooper(recoveryJob!!)

        runBlocking(Dispatchers.IO) {
            val record = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)
            assertNotNull(record)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, record!!.status)
            assertNotNull(record.creditedAtMs)

            val updatedGoal = db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey)!!
            assertEquals(16, updatedGoal.questionsSolved)
            assertEquals(19, updatedGoal.studyTimeMinutes)
            assertEquals(2, updatedGoal.mockExamsCompleted)
            assertEquals(3, updatedGoal.revisionsCompleted)
        }
        assertNull("Startup recovery must not write new SharedPreferences receipt", receiptStore.readReceipt(attemptId))
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(attemptId)?.state)

        // Second simulated app restart must NOT increment goals again (idempotent).
        val vm2 = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        awaitJobWithMainLooper(vm2.getBcsGoalCreditStartupRecoveryJobForTesting()!!)
        runBlocking(Dispatchers.IO) {
            val goalAfterSecondRestart = db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey)!!
            assertEquals(16, goalAfterSecondRestart.questionsSolved)
            assertEquals(19, goalAfterSecondRestart.studyTimeMinutes)
            assertEquals(2, goalAfterSecondRestart.mockExamsCompleted)
            assertEquals(3, goalAfterSecondRestart.revisionsCompleted)
        }
    }

    // =========================================================================
    // TEST 2: COMPLETED attempt + exact legacy receipt marks CREDITED without goal mutation
    // =========================================================================
    @Test
    fun test2_completedAttemptWithExactLegacyReceipt_marksCreditedWithoutGoalMutation() {
        val legacyAttemptId = "stage4b2a_t2_exact_legacy"
        val otherPendingAttemptId = "stage4b2a_t2_other_no_receipt"
        val dateKey = "2026-11-22"
        val verifiedLegacyTs = 1_763_800_123_456L

        val baselineGoal = sampleGoal(
            dateKey = dateKey,
            questionsSolved = 20,
            studyTimeMinutes = 30,
            mockExamsCompleted = 2,
            revisionsCompleted = 1
        )
        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = legacyAttemptId, status = "COMPLETED"))
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = otherPendingAttemptId, status = "COMPLETED"))
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = legacyAttemptId,
                    targetDateKey = dateKey,
                    questionsDelta = 10,
                    studyTimeMinutesDelta = 5,
                    mockExamsDelta = 1,
                    revisionsDelta = 1
                )
            )
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = otherPendingAttemptId,
                    targetDateKey = dateKey,
                    questionsDelta = 3,
                    studyTimeMinutesDelta = 2,
                    mockExamsDelta = 1,
                    revisionsDelta = 0
                )
            )
        }

        // Seed exact legacy receipt ONLY for legacyAttemptId (not for otherPendingAttemptId).
        receiptStore.recordCreditedReceipt(
            delta = BcsGoalCreditDelta(
                attemptId = legacyAttemptId,
                examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                questionsDelta = 10,
                studyTimeMinutesDelta = 5,
                mockExamsDelta = 1,
                revisionsDelta = 1
            ),
            creditedAtMs = verifiedLegacyTs
        )

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        awaitJobWithMainLooper(vm.getBcsGoalCreditStartupRecoveryJobForTesting()!!)

        runBlocking(Dispatchers.IO) {
            val legacyRecord = db.mathDao().getBcsGoalCreditRecordByAttemptId(legacyAttemptId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, legacyRecord.status)
            assertEquals(verifiedLegacyTs, legacyRecord.creditedAtMs)

            val otherRecord = db.mathDao().getBcsGoalCreditRecordByAttemptId(otherPendingAttemptId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, otherRecord.status)
            assertNotNull(otherRecord.creditedAtMs)

            // Goal counters must reflect ONLY otherPendingAttemptId (+3 q, +2 min, +1 mock, +0 rev),
            // proving legacyAttemptId was reconciled WITHOUT mutating bcs_study_goals while
            // legacyAttemptId's receipt did NOT suppress otherPendingAttemptId.
            val finalGoal = db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey)!!
            assertEquals(23, finalGoal.questionsSolved)
            assertEquals(32, finalGoal.studyTimeMinutes)
            assertEquals(3, finalGoal.mockExamsCompleted)
            assertEquals(1, finalGoal.revisionsCompleted)
        }
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(legacyAttemptId)?.state)
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(otherPendingAttemptId)?.state)
        assertNull(receiptStore.readReceipt(otherPendingAttemptId))
    }

    // =========================================================================
    // TEST 3: Deleted exam attempt deletes ONLY its PENDING row; CREDITED & goals untouched
    // =========================================================================
    @Test
    fun test3_deletedExamAttempt_removesOnlyPendingRowWithoutGoalCredit() {
        val staleAttemptId = "stage4b2a_t3_deleted_pending"
        val creditedOrphanId = "stage4b2a_t3_deleted_credited"
        val baselineGoal = sampleGoal(dateKey = "2026-11-22")

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            // No ExamAttemptEntity seeded for either attemptId
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = staleAttemptId,
                    status = BcsGoalCreditRecordEntity.STATUS_PENDING,
                    creditedAtMs = null
                )
            )
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = creditedOrphanId,
                    status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
                    creditedAtMs = 1_763_800_111_000L
                )
            )
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        awaitJobWithMainLooper(vm.getBcsGoalCreditStartupRecoveryJobForTesting()!!)

        runBlocking(Dispatchers.IO) {
            assertNull("Stale PENDING ledger row must be deleted", db.mathDao().getBcsGoalCreditRecordByAttemptId(staleAttemptId))
            val preservedCredited = db.mathDao().getBcsGoalCreditRecordByAttemptId(creditedOrphanId)
            assertNotNull("CREDITED ledger row must never be deleted", preservedCredited)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, preservedCredited!!.status)
            assertEquals("No goal mutation may occur for deleted attempt", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
    }

    // =========================================================================
    // TEST 4: Existing IN_PROGRESS attempt with PENDING row fails closed
    // =========================================================================
    @Test
    fun test4_inProgressAttemptWithPendingRow_failsClosedLeavingPendingIntact() {
        val attemptId = "stage4b2a_t4_in_progress"
        val baselineGoal = sampleGoal(dateKey = "2026-11-22")
        val pending = sampleLedgerRecord(attemptId = attemptId)

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(
                sampleExamAttempt(attemptId = attemptId, status = "IN_PROGRESS", completedAt = null)
            )
            db.mathDao().insertBcsGoalCreditRecordStrict(pending)
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        awaitJobWithMainLooper(vm.getBcsGoalCreditStartupRecoveryJobForTesting()!!)

        runBlocking(Dispatchers.IO) {
            val persisted = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)
            assertEquals("PENDING row must remain intact for diagnosis on non-COMPLETED attempt", pending, persisted)
            assertEquals("No goal mutation may occur for IN_PROGRESS attempt", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
        assertEquals(
            BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN,
            vm.getBcsGoalCreditStatusForAttemptForTesting(attemptId)?.state
        )
    }

    // =========================================================================
    // TEST 5: Ordinary lookup failure preserves PENDING row and does not block other records
    // =========================================================================
    @Test
    fun test5_ordinaryLookupFailure_preservesPendingRowAndRecoversIndependentValidRecords() {
        val failingAttemptId = "stage4b2a_t5_lookup_fail"
        val validAttemptId = "stage4b2a_t5_valid_next"
        val dateKey = "2026-11-22"
        val baselineGoal = sampleGoal(
            dateKey = dateKey,
            questionsSolved = 10,
            studyTimeMinutes = 10,
            mockExamsCompleted = 0,
            revisionsCompleted = 0
        )
        val failingPending = sampleLedgerRecord(
            attemptId = failingAttemptId,
            targetDateKey = dateKey,
            questionsDelta = 9,
            studyTimeMinutesDelta = 5,
            mockExamsDelta = 1,
            revisionsDelta = 1,
            createdAtMs = 1_763_800_000_001L
        )
        val validPending = sampleLedgerRecord(
            attemptId = validAttemptId,
            targetDateKey = dateKey,
            questionsDelta = 4,
            studyTimeMinutesDelta = 3,
            mockExamsDelta = 1,
            revisionsDelta = 0,
            createdAtMs = 1_763_800_000_002L
        )

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = failingAttemptId, status = "COMPLETED"))
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = validAttemptId, status = "COMPLETED"))
            db.mathDao().insertBcsGoalCreditRecordStrict(failingPending)
            db.mathDao().insertBcsGoalCreditRecordStrict(validPending)
        }

        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = true
        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val injectedLookupFailure = IllegalStateException("Injected transient exam lookup I/O failure")
        vm.bcsGoalCreditStartupExamLookupForTesting = { id, defaultLookup ->
            if (id == failingAttemptId) {
                throw injectedLookupFailure
            }
            defaultLookup(id)
        }

        val job = vm.triggerBcsGoalCreditStartupRecoveryForTesting()!!
        awaitJobWithMainLooper(job)

        runBlocking(Dispatchers.IO) {
            // Failing attempt row must remain PENDING untouched
            assertEquals(failingPending, db.mathDao().getBcsGoalCreditRecordByAttemptId(failingAttemptId))

            // Independent valid attempt must still recover cleanly
            val recovered = db.mathDao().getBcsGoalCreditRecordByAttemptId(validAttemptId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, recovered.status)
            assertNotNull(recovered.creditedAtMs)

            // Only validPending's delta applied
            val finalGoal = db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey)!!
            assertEquals(14, finalGoal.questionsSolved)
            assertEquals(13, finalGoal.studyTimeMinutes)
            assertEquals(1, finalGoal.mockExamsCompleted)
            assertEquals(0, finalGoal.revisionsCompleted)
        }
        assertSame(injectedLookupFailure, vm.getLastBcsGoalCreditFailureForTesting(failingAttemptId))
        assertEquals(BcsGoalCreditState.RETRYABLE_FAILURE, vm.getBcsGoalCreditStatusForAttemptForTesting(failingAttemptId)?.state)
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(validAttemptId)?.state)
    }

    // =========================================================================
    // TEST 6: Duplicate startup sweep invocation does not launch duplicate credit jobs
    // =========================================================================
    @Test
    fun test6_duplicateStartupSweepInvocation_deduplicatesSweepAndInFlightJobs() {
        val attemptId = "stage4b2a_t6_dedup"
        val dateKey = "2026-11-22"
        val baselineGoal = sampleGoal(dateKey = dateKey, questionsSolved = 5, studyTimeMinutes = 5, mockExamsCompleted = 0, revisionsCompleted = 0)
        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = attemptId, status = "COMPLETED"))
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = attemptId,
                    targetDateKey = dateKey,
                    questionsDelta = 7,
                    studyTimeMinutesDelta = 3,
                    mockExamsDelta = 1,
                    revisionsDelta = 1
                )
            )
        }

        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = true
        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())

        val creditEntered = CompletableDeferred<Unit>()
        val releaseCredit = CompletableDeferred<Unit>()
        val ledgerExecCount = AtomicInteger(0)
        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            ledgerExecCount.incrementAndGet()
            creditEntered.complete(Unit)
            releaseCredit.await()
            defaultExecute(id, ts)
        }

        val firstJob = vm.triggerBcsGoalCreditStartupRecoveryForTesting()
        assertNotNull(firstJob)
        awaitWithMainLooper(creditEntered)

        // Duplicate trigger calls must return the exact same guarded Job and never launch another sweep
        val secondJob = vm.triggerBcsGoalCreditStartupRecoveryForTesting()
        val thirdJob = vm.triggerBcsGoalCreditStartupRecoveryForTesting()
        assertSame("Duplicate startup sweep invocation must return existing guarded Job", firstJob, secondJob)
        assertSame("Duplicate startup sweep invocation must return existing guarded Job", firstJob, thirdJob)

        // Even a direct internal sweep pass while attemptId is in-flight must skip launching a second job
        runBlocking(Dispatchers.IO) {
            vm.runBcsGoalCreditStartupRecoveryOnceForTesting()
        }
        assertEquals("Already in-flight attempt must never execute a duplicate ledger credit job", 1, ledgerExecCount.get())

        releaseCredit.complete(Unit)
        awaitJobWithMainLooper(firstJob!!)

        assertEquals("Ledger credit must execute exactly once", 1, ledgerExecCount.get())
        runBlocking(Dispatchers.IO) {
            val goal = db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey)!!
            assertEquals(12, goal.questionsSolved)
            assertEquals(8, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
            assertEquals(1, goal.revisionsCompleted)
        }
    }

    // =========================================================================
    // TEST 7: Background startup recovery does not overwrite selected attempt's UI state
    // =========================================================================
    @Test
    fun test7_backgroundStartupRecovery_doesNotOverwriteSelectedAttemptUiState() {
        val bgLedgerAttemptId = "stage4b2a_t7_bg_ledger"
        val bgLegacyAttemptId = "stage4b2a_t7_bg_legacy"
        val dateKey = "2026-11-22"

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = bgLedgerAttemptId, status = "COMPLETED"))
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = bgLegacyAttemptId, status = "COMPLETED"))
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(attemptId = bgLedgerAttemptId, targetDateKey = dateKey)
            )
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(attemptId = bgLegacyAttemptId, targetDateKey = dateKey)
            )
        }
        receiptStore.recordCreditedReceipt(
            delta = BcsGoalCreditDelta(
                attemptId = bgLegacyAttemptId,
                examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                questionsDelta = 8,
                studyTimeMinutesDelta = 4,
                mockExamsDelta = 1,
                revisionsDelta = 1
            ),
            creditedAtMs = 1_763_800_222_000L
        )

        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = true
        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())

        // Start a foreground BCS mock exam so another attempt is actively selected in the UI.
        vm.startBcsMockExam(questionCount = 3, durationMinutes = 10)
        val selectedAttemptId = vm.quizSession.value.attemptId
        assertTrue(selectedAttemptId.isNotBlank())
        assertEquals(selectedAttemptId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)

        val job = vm.triggerBcsGoalCreditStartupRecoveryForTesting()!!
        awaitJobWithMainLooper(job)

        // Selected attempt's published UI status must remain completely untouched
        assertEquals(selectedAttemptId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)
        assertNull(vm.bcsGoalCreditStatus.value.appliedDelta)

        // Both background attempts must have recovered to CREDITED in per-attempt state and Room
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(bgLedgerAttemptId)?.state)
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(bgLegacyAttemptId)?.state)
        runBlocking(Dispatchers.IO) {
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, db.mathDao().getBcsGoalCreditRecordByAttemptId(bgLedgerAttemptId)?.status)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, db.mathDao().getBcsGoalCreditRecordByAttemptId(bgLegacyAttemptId)?.status)
        }
    }

    // =========================================================================
    // TEST 8: CancellationException propagates and never converts to CREDITED
    // =========================================================================
    @Test
    fun test8_cancellationException_propagatesAndNeverConvertsToCredited() {
        val lookupCancelId = "stage4b2a_t8_lookup_cancel"
        val creditCancelId = "stage4b2a_t8_credit_cancel"
        val dateKey = "2026-11-22"
        val baselineGoal = sampleGoal(dateKey = dateKey)

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = lookupCancelId, status = "COMPLETED"))
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(attemptId = lookupCancelId, targetDateKey = dateKey)
            )
        }

        // Sub-case A: CancellationException during startup lookup propagates directly
        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = true
        val vmA = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        vmA.bcsGoalCreditStartupExamLookupForTesting = { _, _ ->
            throw CancellationException("Injected startup lookup cancellation")
        }
        try {
            runBlocking(Dispatchers.IO) {
                vmA.runBcsGoalCreditStartupRecoveryOnceForTesting()
            }
            fail("Expected CancellationException to propagate out of startup recovery")
        } catch (expected: CancellationException) {
            assertTrue(expected.message?.contains("Injected startup lookup cancellation") == true)
        }
        runBlocking(Dispatchers.IO) {
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, db.mathDao().getBcsGoalCreditRecordByAttemptId(lookupCancelId)?.status)
            assertEquals(baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
        assertTrue(vmA.getBcsGoalCreditStatusForAttemptForTesting(lookupCancelId)?.state != BcsGoalCreditState.CREDITED)

        // Sub-case B: CancellationException during launched recovery credit job propagates & never marks CREDITED
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(sampleExamAttempt(attemptId = creditCancelId, status = "COMPLETED"))
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(attemptId = creditCancelId, targetDateKey = dateKey)
            )
        }
        val vmB = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val creditEntered = CompletableDeferred<Unit>()
        vmB.bcsGoalCreditLedgerExecutorForTesting = { _, _, _ ->
            creditEntered.complete(Unit)
            throw CancellationException("Injected credit execution cancellation")
        }
        val recoveryJob = vmB.triggerBcsGoalCreditStartupRecoveryForTesting()!!
        awaitWithMainLooper(creditEntered)
        awaitJobWithMainLooper(recoveryJob)

        runBlocking(Dispatchers.IO) {
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, db.mathDao().getBcsGoalCreditRecordByAttemptId(creditCancelId)?.status)
            assertEquals(baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
        assertFalse("In-flight set must be cleaned after cancellation", vmB.isBcsGoalCreditInFlightForTesting(creditCancelId))
        assertEquals(
            BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN,
            vmB.getBcsGoalCreditStatusForAttemptForTesting(creditCancelId)?.state
        )
        assertTrue(
            "Cancellation must never be converted to CREDITED",
            vmB.getBcsGoalCreditStatusForAttemptForTesting(creditCancelId)?.state != BcsGoalCreditState.CREDITED
        )
        assertTrue(db.mathDao().getAllBcsStudyGoals().let { flow -> runBlocking(Dispatchers.IO) { flow.first().size == 1 } })
    }
}
