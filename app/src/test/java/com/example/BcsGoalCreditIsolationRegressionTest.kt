package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.repository.MathRepository
import com.example.math.BcsExamProfile
import com.example.math.BcsGoalCreditDelta
import com.example.math.BcsGoalCreditFaultStage
import com.example.math.BcsGoalCreditReceiptStore
import com.example.math.BcsGoalCreditState
import com.example.math.MonotonicClock
import com.example.ui.MathViewModel
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
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
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class BcsGoalCreditIsolationRegressionTest {

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
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
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

    private fun awaitConditionWithMainLooper(timeoutMs: Long = 10_000L, condition: () -> Boolean) {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (!condition()) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (condition()) break
            if (System.nanoTime() > deadline) {
                throw AssertionError("Timed out after ${timeoutMs}ms waiting for condition")
            }
            Thread.yield()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun startAndAnswerBcsMock(
        vm: MathViewModel,
        clock: FakeMonotonicClock,
        questionCount: Int,
        answersToProvide: Int,
        elapsedSeconds: Long = 60L
    ): String {
        vm.startBcsMockExam(questionCount = questionCount, durationMinutes = 10)
        val attemptId = vm.quizSession.value.attemptId
        val questions = vm.quizSession.value.questions
        assertTrue(questions.size >= answersToProvide)
        for (i in 0 until answersToProvide) {
            vm.jumpToQuizQuestion(i)
            vm.selectQuizOption(questions[i].correctIndex)
        }
        clock.advanceSeconds(elapsedSeconds)
        return attemptId
    }

    // =========================================================================
    // TEST A: Attempt A finishes in background after Attempt B is selected
    // =========================================================================
    @Test
    fun testA_attemptAProcessing_attemptBSelected_attemptACompletesWithoutClobberingB() {
        val fakeClock = FakeMonotonicClock(100_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_${MathRepository.todayDateKey()}"

        val attemptAEntered = CompletableDeferred<Unit>()
        val releaseAttemptA = CompletableDeferred<Unit>()
        var attemptAId = ""

        vm.bcsGoalCreditLedgerExecutorForTesting = { attemptId, creditedAtMs, defaultExecute ->
            if (attemptId == attemptAId) {
                attemptAEntered.complete(Unit)
                releaseAttemptA.await()
            }
            defaultExecute(attemptId, creditedAtMs)
        }

        // Start and finish Attempt A (2 answered questions -> 2 questions, 1 min, 1 mock).
        attemptAId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 60L)
        vm.finishQuizSession()

        // Deterministically wait until Attempt A is in-flight inside goal-credit execution.
        awaitWithMainLooper(attemptAEntered)
        val jobA = vm.getActiveBcsGoalCreditJobForTesting(attemptAId)
        assertNotNull("Attempt A must have an active tracked job while paused", jobA)
        assertTrue(vm.isBcsGoalCreditInFlightForTesting(attemptAId))
        assertEquals(attemptAId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.IN_PROGRESS, vm.bcsGoalCreditStatus.value.state)

        // Attempt B becomes the current selected attempt while Attempt A is still in-flight.
        vm.startBcsMockExam(questionCount = 4, durationMinutes = 10)
        val attemptBId = vm.quizSession.value.attemptId
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)

        // Release Attempt A so it enters and completes its persistence & receipt path.
        releaseAttemptA.complete(Unit)
        awaitJobWithMainLooper(jobA!!)

        // Attempt B's visible status MUST remain completely unchanged.
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)

        // Attempt A completed its own persistence, durable receipt, and per-attempt state update.
        assertFalse(vm.isBcsGoalCreditInFlightForTesting(attemptAId))
        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptAId))
        val storedA = vm.getBcsGoalCreditStatusForAttemptForTesting(attemptAId)
        assertNotNull(storedA)
        assertEquals(BcsGoalCreditState.CREDITED, storedA!!.state)
        assertEquals(2, storedA.appliedDelta?.questionsDelta)
        assertEquals(1, storedA.appliedDelta?.mockExamsDelta)

        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for Attempt A",
            receiptStore.readReceipt(attemptAId)
        )

        runBlocking(Dispatchers.IO) {
            val ledgerA = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptAId)
            assertNotNull("Attempt A Room ledger record must be persisted", ledgerA)
            assertEquals(attemptAId, ledgerA!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerA.status)
            assertNotNull("Attempt A creditedAtMs must be recorded", ledgerA.creditedAtMs)
            assertEquals(2, ledgerA.questionsDelta)
            assertEquals(1, ledgerA.studyTimeMinutesDelta)
            assertEquals(1, ledgerA.mockExamsDelta)
            assertEquals(0, ledgerA.revisionsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)
            assertNotNull("Attempt A goal delta must be persisted to Room", goal)
            assertEquals(2, goal!!.questionsSolved)
            assertEquals(1, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
        }
    }

    // =========================================================================
    // TEST B: Attempt B in RETRYABLE_FAILURE + Attempt A Committed -> B retries cleanly
    // =========================================================================
    @Test
    fun testB_attemptBRetryableFailure_attemptACommits_preservesAttemptBRetryAndPayload() {
        val fakeClock = FakeMonotonicClock(200_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_${MathRepository.todayDateKey()}"

        val attemptAEntered = CompletableDeferred<Unit>()
        val releaseAttemptA = CompletableDeferred<Unit>()
        val attemptBFirstFailed = CompletableDeferred<Unit>()
        val attemptBRetryFinished = CompletableDeferred<Unit>()
        val attemptBExecCount = AtomicInteger(0)
        var attemptAId = ""
        var attemptBId = ""

        vm.bcsGoalCreditLedgerExecutorForTesting = { attemptId, creditedAtMs, defaultExecute ->
            when (attemptId) {
                attemptAId -> {
                    attemptAEntered.complete(Unit)
                    releaseAttemptA.await()
                    vm.setBcsGoalCreditFaultInjectorForTesting(null)
                    defaultExecute(attemptId, creditedAtMs)
                }
                attemptBId -> {
                    val callNum = attemptBExecCount.incrementAndGet()
                    if (callNum == 1) {
                        vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
                            if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                                throw IllegalStateException("Injected pre-mutation fault for Attempt B")
                            }
                        }
                        try {
                            defaultExecute(attemptId, creditedAtMs)
                        } finally {
                            vm.setBcsGoalCreditFaultInjectorForTesting(null)
                            attemptBFirstFailed.complete(Unit)
                        }
                    } else {
                        vm.setBcsGoalCreditFaultInjectorForTesting(null)
                        try {
                            defaultExecute(attemptId, creditedAtMs)
                        } finally {
                            attemptBRetryFinished.complete(Unit)
                        }
                    }
                }
                else -> defaultExecute(attemptId, creditedAtMs)
            }
        }

        // Start and finish Attempt A (2 questions) and pause it in-flight.
        attemptAId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitWithMainLooper(attemptAEntered)
        val jobA = vm.getActiveBcsGoalCreditJobForTesting(attemptAId)!!

        // Start and finish Attempt B (3 questions) which fails before DB mutation -> RETRYABLE_FAILURE.
        attemptBId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 4, answersToProvide = 3, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitWithMainLooper(attemptBFirstFailed)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptBId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptBId)
        }
        assertTrue("Attempt B must retain pending retry payload", vm.hasPendingBcsGoalCreditForTesting(attemptBId))

        // Release Attempt A to complete with Committed.
        releaseAttemptA.complete(Unit)
        awaitJobWithMainLooper(jobA)

        // Verify Attempt A committed in Room ledger and cleared ONLY its own pending payload.
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(attemptAId)?.state)
        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptAId))
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for Attempt A",
            receiptStore.readReceipt(attemptAId)
        )
        runBlocking(Dispatchers.IO) {
            val ledgerA = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptAId)
            assertNotNull(ledgerA)
            assertEquals(attemptAId, ledgerA!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerA.status)
            assertNotNull(ledgerA.creditedAtMs)
            assertEquals(2, ledgerA.questionsDelta)
            assertEquals(1, ledgerA.studyTimeMinutesDelta)
            assertEquals(1, ledgerA.mockExamsDelta)
            assertEquals(0, ledgerA.revisionsDelta)

            val ledgerBBeforeRetry = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptBId)
            assertNotNull(ledgerBBeforeRetry)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, ledgerBBeforeRetry!!.status)
            assertNull(ledgerBBeforeRetry.creditedAtMs)
        }

        // Verify Attempt B's pending payload and RETRYABLE_FAILURE visible state remain intact.
        assertTrue("Attempt B pending payload must NOT be cleared by Attempt A commit", vm.hasPendingBcsGoalCreditForTesting(attemptBId))
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.RETRYABLE_FAILURE, vm.bcsGoalCreditStatus.value.state)

        // Explicit retry for Attempt B MUST still succeed.
        vm.retryBcsGoalCredit()
        awaitWithMainLooper(attemptBRetryFinished)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptBId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptBId)
        }

        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptBId))
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for Attempt B",
            receiptStore.readReceipt(attemptBId)
        )
        assertEquals(2, attemptBExecCount.get())

        // Room ledger & goal reflect BOTH Attempt A (2q, 1m, 1 mock) + Attempt B (3q, 1m, 1 mock).
        runBlocking(Dispatchers.IO) {
            val ledgerB = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptBId)
            assertNotNull(ledgerB)
            assertEquals(attemptBId, ledgerB!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerB.status)
            assertNotNull(ledgerB.creditedAtMs)
            assertEquals(3, ledgerB.questionsDelta)
            assertEquals(1, ledgerB.studyTimeMinutesDelta)
            assertEquals(1, ledgerB.mockExamsDelta)
            assertEquals(0, ledgerB.revisionsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals(5, goal.questionsSolved)
            assertEquals(2, goal.studyTimeMinutes)
            assertEquals(2, goal.mockExamsCompleted)
        }
    }

    // =========================================================================
    // TEST C: Attempt B in RETRYABLE_FAILURE + Attempt A AmbiguousOutcome -> B untouched
    // =========================================================================
    @Test
    fun testC_attemptBRetryableFailure_attemptAAmbiguousOutcome_preservesAttemptBRetryAndPayload() {
        val fakeClock = FakeMonotonicClock(300_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_${MathRepository.todayDateKey()}"

        val attemptAEntered = CompletableDeferred<Unit>()
        val releaseAttemptA = CompletableDeferred<Unit>()
        val attemptBFirstFailed = CompletableDeferred<Unit>()
        val attemptBRetryFinished = CompletableDeferred<Unit>()
        val attemptBExecCount = AtomicInteger(0)
        var attemptAId = ""
        var attemptBId = ""

        vm.bcsGoalCreditLedgerExecutorForTesting = { attemptId, creditedAtMs, defaultExecute ->
            when (attemptId) {
                attemptAId -> {
                    attemptAEntered.complete(Unit)
                    releaseAttemptA.await()
                    vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
                        if (stage == BcsGoalCreditFaultStage.AFTER_DB_COMMIT) {
                            throw IllegalStateException("Injected post-commit fault for Attempt A")
                        }
                    }
                    try {
                        defaultExecute(attemptId, creditedAtMs)
                    } finally {
                        vm.setBcsGoalCreditFaultInjectorForTesting(null)
                    }
                }
                attemptBId -> {
                    val callNum = attemptBExecCount.incrementAndGet()
                    if (callNum == 1) {
                        vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
                            if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                                throw IllegalStateException("Injected pre-mutation fault for Attempt B")
                            }
                        }
                        try {
                            defaultExecute(attemptId, creditedAtMs)
                        } finally {
                            vm.setBcsGoalCreditFaultInjectorForTesting(null)
                            attemptBFirstFailed.complete(Unit)
                        }
                    } else {
                        vm.setBcsGoalCreditFaultInjectorForTesting(null)
                        try {
                            defaultExecute(attemptId, creditedAtMs)
                        } finally {
                            attemptBRetryFinished.complete(Unit)
                        }
                    }
                }
                else -> defaultExecute(attemptId, creditedAtMs)
            }
        }

        // Start and finish Attempt A (2 questions), pause in-flight.
        attemptAId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitWithMainLooper(attemptAEntered)
        val jobA = vm.getActiveBcsGoalCreditJobForTesting(attemptAId)!!

        // Start and finish Attempt B (3 questions) -> enters RETRYABLE_FAILURE.
        attemptBId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 4, answersToProvide = 3, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitWithMainLooper(attemptBFirstFailed)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptBId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptBId)
        }
        assertTrue(vm.hasPendingBcsGoalCreditForTesting(attemptBId))

        // Release Attempt A -> post-commit fault verifies Room ledger and resolves to CREDITED (BCS-GC-02).
        releaseAttemptA.complete(Unit)
        awaitJobWithMainLooper(jobA)

        // Verify Attempt A resolved to CREDITED via Room ledger and cleared ONLY Attempt A's pending payload.
        assertEquals(BcsGoalCreditState.CREDITED, vm.getBcsGoalCreditStatusForAttemptForTesting(attemptAId)?.state)
        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptAId))
        assertFalse(vm.isBcsGoalCreditInFlightForTesting(attemptAId))
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for Attempt A",
            receiptStore.readReceipt(attemptAId)
        )
        runBlocking(Dispatchers.IO) {
            val ledgerA = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptAId)
            assertNotNull(ledgerA)
            assertEquals(attemptAId, ledgerA!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerA.status)
            assertNotNull(ledgerA.creditedAtMs)
            assertEquals(2, ledgerA.questionsDelta)
            assertEquals(1, ledgerA.studyTimeMinutesDelta)
            assertEquals(1, ledgerA.mockExamsDelta)
            assertEquals(0, ledgerA.revisionsDelta)

            val ledgerBBeforeRetry = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptBId)
            assertNotNull(ledgerBBeforeRetry)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, ledgerBBeforeRetry!!.status)
            assertNull(ledgerBBeforeRetry.creditedAtMs)
        }

        // Verify Attempt B's retry state and pending payload remain completely unchanged.
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.RETRYABLE_FAILURE, vm.bcsGoalCreditStatus.value.state)
        assertTrue("Attempt B pending payload must NOT be cleared by Attempt A post-commit resolution", vm.hasPendingBcsGoalCreditForTesting(attemptBId))

        // Verify explicit retry for Attempt B still works cleanly.
        vm.retryBcsGoalCredit()
        awaitWithMainLooper(attemptBRetryFinished)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptBId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for Attempt B",
            receiptStore.readReceipt(attemptBId)
        )

        // Attempt A mutated Room once before post-commit fault (2q, 1m, 1 mock) + Attempt B retried (3q, 1m, 1 mock).
        runBlocking(Dispatchers.IO) {
            val ledgerB = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptBId)
            assertNotNull(ledgerB)
            assertEquals(attemptBId, ledgerB!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerB.status)
            assertNotNull(ledgerB.creditedAtMs)
            assertEquals(3, ledgerB.questionsDelta)
            assertEquals(1, ledgerB.studyTimeMinutesDelta)
            assertEquals(1, ledgerB.mockExamsDelta)
            assertEquals(0, ledgerB.revisionsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals(5, goal.questionsSolved)
            assertEquals(2, goal.studyTimeMinutes)
            assertEquals(2, goal.mockExamsCompleted)
        }
    }

    // =========================================================================
    // TEST D: Two concurrent requests for the same attemptId -> strictly one accepted
    // =========================================================================
    @Test
    fun testD_sameAttemptConcurrentRequests_onlyOneOperationAccepted() {
        val fakeClock = FakeMonotonicClock(400_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_${MathRepository.todayDateKey()}"

        vm.startBcsMockExam(questionCount = 4, durationMinutes = 10)
        val attemptId = vm.quizSession.value.attemptId
        val delta = BcsGoalCreditDelta(
            attemptId = attemptId,
            examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
            questionsDelta = 3,
            studyTimeMinutesDelta = 2,
            mockExamsDelta = 1,
            revisionsDelta = 0
        )

        // Under BCS-GC-02, Room PENDING is the sole authorization for a new goal credit.
        runBlocking(Dispatchers.IO) {
            db.mathDao().insertBcsGoalCreditRecordStrict(
                BcsGoalCreditRecordEntity(
                    attemptId = attemptId,
                    examProfileId = delta.examProfileId,
                    targetDateKey = MathRepository.todayDateKey(),
                    questionsDelta = delta.questionsDelta,
                    studyTimeMinutesDelta = delta.studyTimeMinutesDelta,
                    mockExamsDelta = delta.mockExamsDelta,
                    revisionsDelta = delta.revisionsDelta,
                    status = BcsGoalCreditRecordEntity.STATUS_PENDING,
                    createdAtMs = 400_000L,
                    creditedAtMs = null
                )
            )
        }

        val executorInvocations = AtomicInteger(0)
        val firstEntered = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()

        vm.bcsGoalCreditLedgerExecutorForTesting = { reqAttemptId, creditedAtMs, defaultExecute ->
            executorInvocations.incrementAndGet()
            firstEntered.complete(Unit)
            releaseFirst.await()
            defaultExecute(reqAttemptId, creditedAtMs)
        }

        runBlocking {
            withTimeout(10_000L) {
                val request1 = async(Dispatchers.Default) {
                    vm.executeBcsCompletionGoalCreditForTesting(delta = delta, isExplicitRetry = false)
                }
                firstEntered.await()
                assertTrue(vm.isBcsGoalCreditInFlightForTesting(attemptId))

                // Concurrent second & third requests for the exact same attemptId while Request 1 is in-flight.
                val request2 = async(Dispatchers.Default) {
                    vm.executeBcsCompletionGoalCreditForTesting(delta = delta, isExplicitRetry = false)
                }
                val request3 = async(Dispatchers.Default) {
                    vm.executeBcsCompletionGoalCreditForTesting(delta = delta, isExplicitRetry = true)
                }
                request2.await()
                request3.await()

                assertEquals("Duplicate concurrent requests for same attemptId must be rejected", 1, executorInvocations.get())
                assertTrue(vm.isBcsGoalCreditInFlightForTesting(attemptId))

                releaseFirst.complete(Unit)
                request1.await()
            }
        }

        assertEquals(1, executorInvocations.get())
        assertFalse(vm.isBcsGoalCreditInFlightForTesting(attemptId))
        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptId))
        assertEquals(attemptId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)

        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for Attempt 1",
            receiptStore.readReceipt(attemptId)
        )
        runBlocking(Dispatchers.IO) {
            val ledger1 = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)
            assertNotNull(ledger1)
            assertEquals(attemptId, ledger1!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledger1.status)
            assertNotNull(ledger1.creditedAtMs)
            assertEquals(3, ledger1.questionsDelta)
            assertEquals(2, ledger1.studyTimeMinutesDelta)
            assertEquals(1, ledger1.mockExamsDelta)
            assertEquals(0, ledger1.revisionsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals("Room DB must be credited strictly once", 3, goal.questionsSolved)
            assertEquals(2, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
        }

        // Also verify duplicate concurrent retryBcsGoalCredit() calls on a RETRYABLE_FAILURE attempt accept strictly one retry.
        val retryExecInvocations = AtomicInteger(0)
        val retryFirstEntered = CompletableDeferred<Unit>()
        val releaseRetry = CompletableDeferred<Unit>()
        var retryAttemptId = ""
        val firstFailDone = CompletableDeferred<Unit>()

        vm.bcsGoalCreditLedgerExecutorForTesting = { reqAttemptId, creditedAtMs, defaultExecute ->
            if (reqAttemptId == retryAttemptId) {
                val callNum = retryExecInvocations.incrementAndGet()
                if (callNum == 1) {
                    vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
                        if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                            throw IllegalStateException("Injected fault before retry")
                        }
                    }
                    try {
                        defaultExecute(reqAttemptId, creditedAtMs)
                    } finally {
                        vm.setBcsGoalCreditFaultInjectorForTesting(null)
                        firstFailDone.complete(Unit)
                    }
                } else {
                    retryFirstEntered.complete(Unit)
                    releaseRetry.await()
                    defaultExecute(reqAttemptId, creditedAtMs)
                }
            } else {
                defaultExecute(reqAttemptId, creditedAtMs)
            }
        }

        retryAttemptId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 3, answersToProvide = 2, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitWithMainLooper(firstFailDone)
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == retryAttemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE &&
                !vm.isBcsGoalCreditInFlightForTesting(retryAttemptId)
        }

        // Rapid double-tap on retryBcsGoalCredit() must execute strictly once.
        vm.retryBcsGoalCredit()
        vm.retryBcsGoalCredit()
        awaitWithMainLooper(retryFirstEntered)
        val retryJob = vm.getActiveBcsGoalCreditJobForTesting(retryAttemptId)!!
        // Third call while actively in-flight must also be rejected.
        vm.retryBcsGoalCredit()
        assertEquals(2, retryExecInvocations.get()) // 1 initial failed call + 1 active retry call

        releaseRetry.complete(Unit)
        awaitJobWithMainLooper(retryJob)

        assertEquals(2, retryExecInvocations.get())
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for retryAttemptId",
            receiptStore.readReceipt(retryAttemptId)
        )
        runBlocking(Dispatchers.IO) {
            val ledgerRetry = db.mathDao().getBcsGoalCreditRecordByAttemptId(retryAttemptId)
            assertNotNull(ledgerRetry)
            assertEquals(retryAttemptId, ledgerRetry!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerRetry.status)
            assertNotNull(ledgerRetry.creditedAtMs)
            assertEquals(2, ledgerRetry.questionsDelta)
            assertEquals(1, ledgerRetry.studyTimeMinutesDelta)
            assertEquals(1, ledgerRetry.mockExamsDelta)
            assertEquals(0, ledgerRetry.revisionsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals("Total must reflect attempt 1 (3q) + retry attempt (2q) strictly once", 5, goal.questionsSolved)
            assertEquals(3, goal.studyTimeMinutes)
            assertEquals(2, goal.mockExamsCompleted)
        }
    }

    // =========================================================================
    // TEST E: Switching between historical Attempt A and Attempt B always projects selected attempt state
    // =========================================================================
    @Test
    fun testE_switchBetweenHistoricalAttemptAAndAttemptB_uiAlwaysShowsSelectedAttemptState() {
        val fakeClock = FakeMonotonicClock(500_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_${MathRepository.todayDateKey()}"

        // 1. Complete Attempt A -> CREDITED (2 questions, 1 mock).
        val attemptAId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptAId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }

        // 2. Complete Attempt B with BEFORE_DB_MUTATION fault -> RETRYABLE_FAILURE (3 questions, 1 mock).
        vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
            if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                throw IllegalStateException("Injected pre-mutation fault for Attempt B")
            }
        }
        val attemptBId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 4, answersToProvide = 3, elapsedSeconds = 120L)
        vm.finishQuizSession()
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptBId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptBId)
        }
        vm.setBcsGoalCreditFaultInjectorForTesting(null)

        // 3. Complete Attempt C with AFTER_DB_COMMIT fault -> BCS-GC-02 verifies Room ledger and resolves to CREDITED (1 question, 1 mock).
        vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
            if (stage == BcsGoalCreditFaultStage.AFTER_DB_COMMIT) {
                throw IllegalStateException("Injected post-commit fault for Attempt C")
            }
        }
        val attemptCId = startAndAnswerBcsMock(vm, fakeClock, questionCount = 3, answersToProvide = 1, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptCId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptCId)
        }
        vm.setBcsGoalCreditFaultInjectorForTesting(null)
        assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
        val attemptCCreditedAtMs = runBlocking(Dispatchers.IO) {
            val ledgerC = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptCId)
            assertNotNull("Attempt C Room ledger record must be persisted", ledgerC)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerC!!.status)
            assertNotNull("Attempt C creditedAtMs must be recorded", ledgerC.creditedAtMs)
            ledgerC.creditedAtMs!!
        }

        val entityA = runBlocking(Dispatchers.IO) { db.mathDao().getExamAttemptById(attemptAId)!! }
        val entityB = runBlocking(Dispatchers.IO) { db.mathDao().getExamAttemptById(attemptBId)!! }
        val entityC = runBlocking(Dispatchers.IO) { db.mathDao().getExamAttemptById(attemptCId)!! }

        // Switch to historical Attempt A -> MUST display CREDITED for Attempt A.
        vm.openCompletedExamResult(entityA)
        assertEquals(attemptAId, vm.quizSession.value.attemptId)
        assertEquals(attemptAId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertEquals(2, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)

        // Calling retry while Attempt A is selected MUST be a no-op and MUST NOT retry Attempt B.
        vm.retryBcsGoalCredit()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertEquals(BcsGoalCreditState.RETRYABLE_FAILURE, vm.getBcsGoalCreditStatusForAttemptForTesting(attemptBId)?.state)
        assertTrue(vm.hasPendingBcsGoalCreditForTesting(attemptBId))

        // Switch to historical Attempt B -> MUST display RETRYABLE_FAILURE for Attempt B.
        vm.openCompletedExamResult(entityB)
        assertEquals(attemptBId, vm.quizSession.value.attemptId)
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.RETRYABLE_FAILURE, vm.bcsGoalCreditStatus.value.state)

        // Switch to historical Attempt C -> MUST display CREDITED for Attempt C (post-commit Room ledger resolved).
        vm.openCompletedExamResult(entityC)
        assertEquals(attemptCId, vm.quizSession.value.attemptId)
        assertEquals(attemptCId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)

        // Switch back to Attempt A -> CREDITED.
        vm.openCompletedExamResult(entityA)
        assertEquals(attemptAId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)

        // Switch back to Attempt B -> STILL RETRYABLE_FAILURE, and retry now succeeds!
        vm.openCompletedExamResult(entityB)
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.RETRYABLE_FAILURE, vm.bcsGoalCreditStatus.value.state)
        vm.retryBcsGoalCredit()
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptBId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }
        assertEquals(3, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
        assertEquals(2, vm.bcsGoalCreditStatus.value.appliedDelta?.studyTimeMinutesDelta)

        // Switch across A -> C -> B to confirm each shows its exact state.
        vm.openCompletedExamResult(entityA)
        assertEquals(attemptAId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertEquals(2, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)

        vm.openCompletedExamResult(entityC)
        assertEquals(attemptCId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)

        vm.openCompletedExamResult(entityB)
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertEquals(3, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)

        // Exiting quiz clears visible projection without stale attempt state.
        vm.exitQuizToTopicList()
        assertEquals("", vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)

        // BCS-GC-02 must NOT write new SharedPreferences receipts for any of A, B, or C.
        assertNull(receiptStore.readReceipt(attemptAId))
        assertNull(receiptStore.readReceipt(attemptBId))
        assertNull(receiptStore.readReceipt(attemptCId))

        // Final Room ledger records & goal totals: Attempt A (2q, 1m, 1 mock) + Attempt C (1q, 1m, 1 mock) + Attempt B retried (3q, 2m, 1 mock).
        runBlocking(Dispatchers.IO) {
            val ledgerA = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptAId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerA.status)
            assertNotNull(ledgerA.creditedAtMs)
            assertEquals(2, ledgerA.questionsDelta)
            assertEquals(1, ledgerA.studyTimeMinutesDelta)
            assertEquals(1, ledgerA.mockExamsDelta)

            val ledgerB = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptBId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerB.status)
            assertNotNull(ledgerB.creditedAtMs)
            assertEquals(3, ledgerB.questionsDelta)
            assertEquals(2, ledgerB.studyTimeMinutesDelta)
            assertEquals(1, ledgerB.mockExamsDelta)

            val ledgerC = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptCId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerC.status)
            assertEquals(attemptCCreditedAtMs, ledgerC.creditedAtMs)
            assertEquals(1, ledgerC.questionsDelta)
            assertEquals(1, ledgerC.studyTimeMinutesDelta)
            assertEquals(1, ledgerC.mockExamsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals(6, goal.questionsSolved)
            assertEquals(4, goal.studyTimeMinutes)
            assertEquals(3, goal.mockExamsCompleted)
        }

        // Cold-start verification: reopening Attempt C on a fresh MathViewModel reconciles from Room to CREDITED
        // with preserved creditedAtMs and zero additional goal increment.
        val coldStartVm = MathViewModel(app, clock = FakeMonotonicClock(600_000L), savedStateHandle = SavedStateHandle())
        coldStartVm.openCompletedExamResult(entityC)
        awaitConditionWithMainLooper {
            coldStartVm.bcsGoalCreditStatus.value.attemptId == attemptCId &&
                coldStartVm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
        }
        assertEquals(1, coldStartVm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
        assertEquals(1, coldStartVm.bcsGoalCreditStatus.value.appliedDelta?.studyTimeMinutesDelta)
        assertEquals(1, coldStartVm.bcsGoalCreditStatus.value.appliedDelta?.mockExamsDelta)
        assertNull(receiptStore.readReceipt(attemptCId))
        runBlocking(Dispatchers.IO) {
            val ledgerCAfterColdStart = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptCId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerCAfterColdStart.status)
            assertEquals(attemptCCreditedAtMs, ledgerCAfterColdStart.creditedAtMs)

            val goalAfterColdStart = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals("Cold-start reopen of Attempt C must NOT double-credit", 6, goalAfterColdStart.questionsSolved)
            assertEquals(4, goalAfterColdStart.studyTimeMinutes)
            assertEquals(3, goalAfterColdStart.mockExamsCompleted)
        }
    }
}
