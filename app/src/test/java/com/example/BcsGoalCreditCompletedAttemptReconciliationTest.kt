package com.example

import android.app.Application
import android.content.Context
import android.os.Looper
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
import com.example.math.ExamPresetType
import com.example.math.MonotonicClock
import com.example.ui.MathViewModel
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
class BcsGoalCreditCompletedAttemptReconciliationTest {

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
        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = true
        app = ApplicationProvider.getApplicationContext()
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        receiptStore = BcsGoalCreditReceiptStore(prefs)
        db = MathDatabase.getInstance(app)
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = false
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    private fun awaitJobWithMainLooper(job: Job, timeoutMs: Long = 10_000L) {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (!job.isCompleted) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            if (job.isCompleted) break
            if (System.nanoTime() > deadline) {
                throw AssertionError("Timed out after ${timeoutMs}ms waiting for Job completion")
            }
            Thread.yield()
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    private fun <T> awaitWithMainLooper(deferred: CompletableDeferred<T>, timeoutMs: Long = 10_000L): T {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (!deferred.isCompleted) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            if (deferred.isCompleted) break
            if (System.nanoTime() > deadline) {
                throw AssertionError("Timed out after ${timeoutMs}ms waiting for CompletableDeferred")
            }
            Thread.yield()
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        return runBlocking { deferred.await() }
    }

    private fun awaitConditionWithMainLooper(timeoutMs: Long = 10_000L, condition: () -> Boolean) {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (!condition()) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            if (condition()) break
            if (System.nanoTime() > deadline) {
                throw AssertionError("Timed out after ${timeoutMs}ms waiting for condition")
            }
            Thread.yield()
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    private fun sampleCompletedAttempt(
        attemptId: String,
        examProfileId: String = BcsExamProfile.BCS_50TH_PRELIM.id,
        examType: String = ExamPresetType.BCS_PRELIMINARY_MOCK.id,
        correctCount: Int = 7,
        wrongCount: Int = 3,
        elapsedSeconds: Int = 240,
        completedAt: Long = 1_763_800_000_000L
    ): ExamAttemptEntity = ExamAttemptEntity(
        attemptId = attemptId,
        examTitleBn = "৫০তম বিসিএস মক টেস্ট",
        examType = examType,
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
        percentage = 70.0,
        accuracyPercent = 70.0,
        startedAt = completedAt - (elapsedSeconds * 1000L),
        updatedAt = completedAt,
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
        status: String = BcsGoalCreditRecordEntity.STATUS_CREDITED,
        createdAtMs: Long = 1_763_800_000_000L,
        creditedAtMs: Long? = if (status == BcsGoalCreditRecordEntity.STATUS_CREDITED) 1_763_800_005_000L else null
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
        questionsSolved: Int = 12,
        studyTimeMinutes: Int = 18,
        mockExamsCompleted: Int = 2,
        revisionsCompleted: Int = 1,
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
    // TEST 1: Room CREDITED on cold start -> CREDITED off main thread, zero goal mutation, no prefs write
    // =========================================================================
    @Test
    fun test1_roomCreditedOnColdStart_reconcilesOffMainThreadWithZeroGoalMutation() {
        val attemptId = "stage4b2b_t1_room_credited"
        val persistedCreditedAtMs = 1_763_800_111_222L
        val baselineGoal = sampleGoal()
        val entity = sampleCompletedAttempt(attemptId = attemptId)
        val creditedRecord = sampleLedgerRecord(
            attemptId = attemptId,
            questionsDelta = 9,
            studyTimeMinutesDelta = 6,
            mockExamsDelta = 1,
            revisionsDelta = 2,
            status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
            creditedAtMs = persistedCreditedAtMs
        )

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(entity)
            db.mathDao().insertBcsGoalCreditRecordStrict(creditedRecord)
        }
        assertNull("Precondition: no SharedPreferences receipt exists", receiptStore.readReceipt(attemptId))

        // Fresh cold-start ViewModel
        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val lookupRanOnMainThread = AtomicBoolean(true)
        vm.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { id, defaultLookup ->
            lookupRanOnMainThread.set(Thread.currentThread() === Looper.getMainLooper().thread)
            defaultLookup(id)
        }

        vm.openCompletedExamResult(entity)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }

        assertFalse("Cold-start Room lookup must run off the main thread", lookupRanOnMainThread.get())
        val status = vm.bcsGoalCreditStatus.value
        assertEquals(attemptId, status.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, status.state)
        assertEquals(
            BcsGoalCreditDelta(
                attemptId = attemptId,
                examProfileId = creditedRecord.examProfileId,
                questionsDelta = 9,
                studyTimeMinutesDelta = 6,
                mockExamsDelta = 1,
                revisionsDelta = 2
            ),
            status.appliedDelta
        )
        assertNull("Pending retry payload must be cleared for CREDITED", vm.getPendingBcsGoalCreditForTesting(attemptId))
        assertNull("Must not write a SharedPreferences receipt", receiptStore.readReceipt(attemptId))

        runBlocking(Dispatchers.IO) {
            assertEquals("Persisted creditedAtMs must remain unchanged", persistedCreditedAtMs, db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)?.creditedAtMs)
            assertEquals("Zero goal mutation allowed", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
    }

    // =========================================================================
    // TEST 2: Room PENDING on cold start -> restores exact persisted delta as RETRYABLE_FAILURE & explicit retry succeeds once
    // =========================================================================
    @Test
    fun test2_roomPendingOnColdStart_restoresPersistedDeltaAsRetryableFailureAndAllowsSafeRetry() {
        val attemptId = "stage4b2b_t2_room_pending"
        val dateKey = "2026-11-21"
        val baselineGoal = sampleGoal(
            dateKey = dateKey,
            questionsSolved = 10,
            studyTimeMinutes = 10,
            mockExamsCompleted = 1,
            revisionsCompleted = 0
        )
        // Intentionally make ExamAttemptEntity counts differ from the persisted PENDING ledger
        // to prove reconciliation restores the persisted Room record verbatim without recomputing.
        val entity = sampleCompletedAttempt(attemptId = attemptId, correctCount = 1, wrongCount = 0, elapsedSeconds = 30)
        val pendingRecord = sampleLedgerRecord(
            attemptId = attemptId,
            targetDateKey = dateKey,
            questionsDelta = 14,
            studyTimeMinutesDelta = 7,
            mockExamsDelta = 1,
            revisionsDelta = 3,
            status = BcsGoalCreditRecordEntity.STATUS_PENDING,
            creditedAtMs = null
        )

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(entity)
            db.mathDao().insertBcsGoalCreditRecordStrict(pendingRecord)
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        vm.openCompletedExamResult(entity)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE
        }

        val expectedDelta = BcsGoalCreditDelta(
            attemptId = attemptId,
            examProfileId = pendingRecord.examProfileId,
            questionsDelta = 14,
            studyTimeMinutesDelta = 7,
            mockExamsDelta = 1,
            revisionsDelta = 3
        )
        assertEquals(expectedDelta, vm.getPendingBcsGoalCreditForTesting(attemptId))
        runBlocking(Dispatchers.IO) {
            assertEquals("Opening PENDING must not mutate goals before retry", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)?.status)
        }

        // Explicit retry executes cleanly through the persisted Room ledger
        vm.retryBcsGoalCredit()
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }
        runBlocking(Dispatchers.IO) {
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)?.status)
            val updatedGoal = db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey)!!
            assertEquals(24, updatedGoal.questionsSolved)
            assertEquals(17, updatedGoal.studyTimeMinutes)
            assertEquals(2, updatedGoal.mockExamsCompleted)
            assertEquals(3, updatedGoal.revisionsCompleted)
        }
    }

    // =========================================================================
    // TEST 3: Exact legacy receipt fallback when no Room record exists
    // =========================================================================
    @Test
    fun test3_exactLegacyReceiptFallback_marksCreditedWithZeroGoalMutationAndNoNewWrites() {
        val matchedAttemptId = "stage4b2b_t3_legacy_match"
        val unmatchedAttemptId = "stage4b2b_t3_legacy_other"
        val baselineGoal = sampleGoal()
        val matchedEntity = sampleCompletedAttempt(attemptId = matchedAttemptId)
        val unmatchedEntity = sampleCompletedAttempt(attemptId = unmatchedAttemptId)
        val legacyDelta = BcsGoalCreditDelta(
            attemptId = matchedAttemptId,
            examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
            questionsDelta = 11,
            studyTimeMinutesDelta = 5,
            mockExamsDelta = 1,
            revisionsDelta = 1
        )
        receiptStore.recordCreditedReceipt(legacyDelta, creditedAtMs = 1_763_800_333_000L)

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(matchedEntity)
            db.mathDao().upsertExamAttempt(unmatchedEntity)
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val prefsSnapshotBefore = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).all.toMap()
        vm.openCompletedExamResult(matchedEntity)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }

        assertEquals(matchedAttemptId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(legacyDelta, vm.bcsGoalCreditStatus.value.appliedDelta)

        // Another attempt without its own matching receipt must NOT borrow matchedAttemptId's receipt
        vm.openCompletedExamResult(unmatchedEntity)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == unmatchedAttemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN
        }

        val prefsSnapshotAfter = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).all.toMap()
        assertEquals("No new SharedPreferences writes permitted", prefsSnapshotBefore, prefsSnapshotAfter)
        runBlocking(Dispatchers.IO) {
            assertNull("Must not fabricate a Room ledger row for legacy receipt", db.mathDao().getBcsGoalCreditRecordByAttemptId(matchedAttemptId))
            assertNull("Must not fabricate a Room ledger row for unmatched attempt", db.mathDao().getBcsGoalCreditRecordByAttemptId(unmatchedAttemptId))
            assertEquals("Zero goal mutation allowed", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
    }

    // =========================================================================
    // TEST 4: No Room row and no legacy receipt -> CREDIT_OUTCOME_UNKNOWN, zero goal mutation
    // =========================================================================
    @Test
    fun test4_noRoomRowAndNoLegacyReceipt_resolvesUnknownWithZeroMutation() {
        val attemptId = "stage4b2b_t4_historical_unknown"
        val baselineGoal = sampleGoal()
        val entity = sampleCompletedAttempt(attemptId = attemptId)

        // Even if a WP4.1 launch-origin marker exists, it must NOT be treated as a credit receipt
        receiptStore.markWp41AttemptLaunched(attemptId)

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(entity)
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        vm.openCompletedExamResult(entity)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN
        }

        assertEquals(attemptId, vm.bcsGoalCreditStatus.value.attemptId)
        assertNull(vm.bcsGoalCreditStatus.value.appliedDelta)
        assertNull(vm.getPendingBcsGoalCreditForTesting(attemptId))
        runBlocking(Dispatchers.IO) {
            assertNull("Must never fabricate a PENDING record", db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId))
            assertEquals("Zero goal mutation allowed", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
    }

    // =========================================================================
    // TEST 5: Non-BCS completed attempt -> NOT_STARTED synchronously, zero BCS recovery lookup
    // =========================================================================
    @Test
    fun test5_nonBcsCompletedAttempt_remainsNotStartedWithoutBcsLedgerLookup() {
        val attemptId = "stage4b2b_t5_non_bcs"
        val baselineGoal = sampleGoal()
        val nonBcsEntity = sampleCompletedAttempt(
            attemptId = attemptId,
            examProfileId = "",
            examType = ExamPresetType.QUICK_TEST.id
        )
        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(nonBcsEntity)
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val lookupCount = AtomicInteger(0)
        vm.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { id, defaultLookup ->
            lookupCount.incrementAndGet()
            defaultLookup(id)
        }

        vm.openCompletedExamResult(nonBcsEntity)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(attemptId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)
        assertNull("Non-BCS attempt must not schedule async BCS reconciliation", vm.getActiveBcsGoalCreditReconcileJobForTesting(attemptId))
        assertEquals("Non-BCS attempt must never query BCS credit ledger", 0, lookupCount.get())
        runBlocking(Dispatchers.IO) {
            assertEquals(baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
    }

    // =========================================================================
    // TEST 6: Attempt selection race -> delayed lookup for A never overwrites selected attempt B
    // =========================================================================
    @Test
    fun test6_attemptSelectionRace_delayedLookupForANeverOverwritesSelectedAttemptB() {
        val attemptA = "stage4b2b_t6_race_A"
        val attemptB = "stage4b2b_t6_race_B"
        val entityA = sampleCompletedAttempt(attemptId = attemptA)
        val entityB = sampleCompletedAttempt(attemptId = attemptB)

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertExamAttempt(entityA)
            db.mathDao().upsertExamAttempt(entityB)
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = attemptA,
                    status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
                    questionsDelta = 10
                )
            )
            // Attempt B has no Room record and no legacy receipt -> CREDIT_OUTCOME_UNKNOWN
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val lookupAEntered = CompletableDeferred<Unit>()
        val releaseLookupA = CompletableDeferred<Unit>()
        vm.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { id, defaultLookup ->
            if (id == attemptA) {
                lookupAEntered.complete(Unit)
                releaseLookupA.await()
            }
            defaultLookup(id)
        }

        // Open A and pause its Room lookup in flight
        vm.openCompletedExamResult(entityA)
        val jobA = vm.getActiveBcsGoalCreditReconcileJobForTesting(attemptA)
        assertNotNull("Attempt A must launch async reconciliation job", jobA)
        awaitWithMainLooper(lookupAEntered)

        // Immediately select attempt B before A's lookup finishes
        vm.openCompletedExamResult(entityB)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptB &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN
        }

        // Now release A's delayed Room lookup and wait for A's job to finish
        releaseLookupA.complete(Unit)
        awaitJobWithMainLooper(jobA!!)

        // A's per-attempt state map MUST be updated to CREDITED
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(attemptA)?.state)

        // B's selected UI projection MUST remain completely unchanged
        assertEquals(attemptB, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN, vm.bcsGoalCreditStatus.value.state)
        assertNull(vm.bcsGoalCreditStatus.value.appliedDelta)
    }

    // =========================================================================
    // TEST 7: Startup recovery already in-flight -> preserves IN_PROGRESS, zero duplicate credit
    // =========================================================================
    @Test
    fun test7_startupRecoveryInFlight_preservesInProgressWithoutDuplicateCreditJob() {
        val attemptId = "stage4b2b_t7_startup_inflight"
        val dateKey = "2026-11-22"
        val baselineGoal = sampleGoal(dateKey = dateKey, questionsSolved = 5, studyTimeMinutes = 5, mockExamsCompleted = 0, revisionsCompleted = 0)
        val entity = sampleCompletedAttempt(attemptId = attemptId)

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(entity)
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(
                    attemptId = attemptId,
                    targetDateKey = dateKey,
                    questionsDelta = 6,
                    studyTimeMinutesDelta = 4,
                    mockExamsDelta = 1,
                    revisionsDelta = 1,
                    status = BcsGoalCreditRecordEntity.STATUS_PENDING,
                    creditedAtMs = null
                )
            )
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val creditEntered = CompletableDeferred<Unit>()
        val releaseCredit = CompletableDeferred<Unit>()
        val creditExecCount = AtomicInteger(0)
        val completedLookupCount = AtomicInteger(0)

        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            creditExecCount.incrementAndGet()
            creditEntered.complete(Unit)
            releaseCredit.await()
            defaultExecute(id, ts)
        }
        vm.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { id, defaultLookup ->
            completedLookupCount.incrementAndGet()
            defaultLookup(id)
        }

        // Start Stage 4B2A startup recovery sweep and pause while attemptId is actively in-flight
        val startupJob = vm.triggerBcsGoalCreditStartupRecoveryForTesting()!!
        awaitWithMainLooper(creditEntered)

        // Open the same completed attempt while startup recovery is actively processing it
        vm.openCompletedExamResult(entity)
        assertEquals(attemptId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.IN_PROGRESS, vm.bcsGoalCreditStatus.value.state)
        assertNull("Must not schedule duplicate reconcile job while credit is in-flight", vm.getActiveBcsGoalCreditReconcileJobForTesting(attemptId))
        assertEquals("Must not issue redundant completed-attempt DB lookup while in-flight", 0, completedLookupCount.get())

        // Explicit retry while in-flight must also be a safe no-op
        vm.retryBcsGoalCredit()

        releaseCredit.complete(Unit)
        awaitJobWithMainLooper(startupJob)

        assertEquals("Credit must execute strictly once", 1, creditExecCount.get())
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        runBlocking(Dispatchers.IO) {
            val goal = db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey)!!
            assertEquals(11, goal.questionsSolved)
            assertEquals(9, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
            assertEquals(1, goal.revisionsCompleted)
        }
    }

    // =========================================================================
    // TEST 8: Known in-session CREDITED and RETRYABLE_FAILURE retain synchronous projection on reopen
    // =========================================================================
    @Test
    fun test8_existingKnownInSessionState_retainsSynchronousProjectionWithoutRedundantDbQuery() {
        val creditedId = "stage4b2b_t8_known_credited"
        val retryableId = "stage4b2b_t8_known_retryable"
        val otherNonBcsId = "stage4b2b_t8_other_non_bcs"
        val creditedEntity = sampleCompletedAttempt(attemptId = creditedId)
        val retryableEntity = sampleCompletedAttempt(attemptId = retryableId)
        val otherEntity = sampleCompletedAttempt(
            attemptId = otherNonBcsId,
            examProfileId = "",
            examType = ExamPresetType.QUICK_TEST.id
        )

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertExamAttempt(creditedEntity)
            db.mathDao().upsertExamAttempt(retryableEntity)
            db.mathDao().upsertExamAttempt(otherEntity)
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(attemptId = creditedId, status = BcsGoalCreditRecordEntity.STATUS_CREDITED, questionsDelta = 5)
            )
            db.mathDao().insertBcsGoalCreditRecordStrict(
                sampleLedgerRecord(attemptId = retryableId, status = BcsGoalCreditRecordEntity.STATUS_PENDING, questionsDelta = 7, creditedAtMs = null)
            )
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val lookupCount = AtomicInteger(0)
        vm.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { id, defaultLookup ->
            lookupCount.incrementAndGet()
            defaultLookup(id)
        }

        // First cold-start opens populate in-session known states (1 lookup each)
        vm.openCompletedExamResult(creditedEntity)
        awaitConditionWithMainLooper { vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED }
        vm.openCompletedExamResult(retryableEntity)
        awaitConditionWithMainLooper { vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE }
        assertEquals(2, lookupCount.get())

        // Navigate away to another completed attempt
        vm.openCompletedExamResult(otherEntity)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)

        // Reopen known CREDITED attempt: must project SYNCHRONOUSLY with ZERO extra DB lookup
        vm.openCompletedExamResult(creditedEntity)
        assertEquals(creditedId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertEquals(5, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
        assertNull(vm.getActiveBcsGoalCreditReconcileJobForTesting(creditedId))
        assertEquals("Known CREDITED reopen must not trigger redundant DB lookup", 2, lookupCount.get())

        // Reopen known RETRYABLE_FAILURE attempt: must project SYNCHRONOUSLY with ZERO extra DB lookup
        vm.openCompletedExamResult(retryableEntity)
        assertEquals(retryableId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.RETRYABLE_FAILURE, vm.bcsGoalCreditStatus.value.state)
        assertEquals(7, vm.getPendingBcsGoalCreditForTesting(retryableId)?.questionsDelta)
        assertNull(vm.getActiveBcsGoalCreditReconcileJobForTesting(retryableId))
        assertEquals("Known RETRYABLE_FAILURE reopen must not trigger redundant DB lookup", 2, lookupCount.get())
    }

    // =========================================================================
    // TEST 9: Ordinary Room lookup failure -> no fabricated credit, no mutation, future retry works
    // =========================================================================
    @Test
    fun test9_ordinaryRoomLookupFailure_noFabricatedCreditAndFutureReconciliationSucceeds() {
        val attemptId = "stage4b2b_t9_lookup_failure"
        val baselineGoal = sampleGoal()
        val entity = sampleCompletedAttempt(attemptId = attemptId)
        val creditedRecord = sampleLedgerRecord(
            attemptId = attemptId,
            status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
            questionsDelta = 12
        )

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(entity)
            db.mathDao().insertBcsGoalCreditRecordStrict(creditedRecord)
        }

        val vm = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val injectedError = IllegalStateException("Injected Room read I/O failure")
        vm.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { _, _ ->
            throw injectedError
        }

        vm.openCompletedExamResult(entity)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN &&
                vm.getLastBcsGoalCreditFailureForTesting(attemptId) === injectedError
        }

        assertSame(injectedError, vm.getLastBcsGoalCreditFailureForTesting(attemptId))
        assertEquals(BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN, vm.bcsGoalCreditStatus.value.state)
        runBlocking(Dispatchers.IO) {
            assertEquals("Ledger row must not be deleted on lookup failure", creditedRecord, db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId))
            assertEquals("Zero goal mutation on lookup failure", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }

        // Clear fault and reopen: future reconciliation must succeed cleanly
        vm.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = null
        vm.openCompletedExamResult(entity)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }
        assertEquals(12, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
        assertNull(vm.getLastBcsGoalCreditFailureForTesting(attemptId))
        runBlocking(Dispatchers.IO) {
            assertEquals(baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
    }

    // =========================================================================
    // TEST 10: CancellationException propagates and is never converted to CREDITED
    // =========================================================================
    @Test
    fun test10_cancellationException_propagatesAndNeverConvertsToCredited() {
        val attemptId = "stage4b2b_t10_cancellation"
        val baselineGoal = sampleGoal()
        val entity = sampleCompletedAttempt(attemptId = attemptId)
        val pendingRecord = sampleLedgerRecord(
            attemptId = attemptId,
            status = BcsGoalCreditRecordEntity.STATUS_PENDING,
            creditedAtMs = null
        )

        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertBcsStudyGoal(baselineGoal)
            db.mathDao().upsertExamAttempt(entity)
            db.mathDao().insertBcsGoalCreditRecordStrict(pendingRecord)
        }

        // Sub-case A: Direct CancellationException during Room lookup propagates out without swallowing
        val vmA = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        vmA.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { _, _ ->
            throw CancellationException("Injected reconciliation lookup cancellation")
        }
        try {
            runBlocking(Dispatchers.IO) {
                vmA.reconcileCompletedAttemptFromRoomForTesting(entity)
            }
            fail("Expected CancellationException to propagate out of reconcileCompletedAttemptFromRoom")
        } catch (expected: CancellationException) {
            assertTrue(expected.message?.contains("Injected reconciliation lookup cancellation") == true)
        }
        assertTrue(vmA.getBcsGoalCreditStatusForAttemptForTesting(attemptId)?.state != BcsGoalCreditState.CREDITED)

        // Sub-case B: Cancelling an in-flight async reconciliation job never marks CREDITED or mutates goals
        val vmB = MathViewModel(app, clock = FakeMonotonicClock(), savedStateHandle = SavedStateHandle())
        val lookupEntered = CompletableDeferred<Unit>()
        val neverRelease = CompletableDeferred<Unit>()
        vmB.bcsGoalCreditCompletedAttemptLedgerLookupForTesting = { id, defaultLookup ->
            lookupEntered.complete(Unit)
            neverRelease.await()
            defaultLookup(id)
        }
        vmB.openCompletedExamResult(entity)
        val job = vmB.getActiveBcsGoalCreditReconcileJobForTesting(attemptId)
        assertNotNull(job)
        awaitWithMainLooper(lookupEntered)

        job!!.cancel(CancellationException("User/ViewModel cancelled reconciliation"))
        awaitJobWithMainLooper(job)

        assertTrue("Cancelled reconciliation job must report isCancelled", job.isCancelled)
        assertTrue(
            "Cancellation must never be converted to CREDITED",
            vmB.bcsGoalCreditStatus.value.state != BcsGoalCreditState.CREDITED
        )
        runBlocking(Dispatchers.IO) {
            assertEquals("PENDING ledger record must remain unchanged", pendingRecord, db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId))
            assertEquals("Zero goal mutation allowed on cancellation", baselineGoal, db.mathDao().getBcsStudyGoalByKey(baselineGoal.goalKey))
        }
    }
}
