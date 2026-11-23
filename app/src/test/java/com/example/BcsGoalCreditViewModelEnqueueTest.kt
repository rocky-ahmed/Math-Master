package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsStudyPlanGoalEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.repository.MathRepository
import com.example.math.BcsExamProfile
import com.example.math.BcsGoalCreditDelta
import com.example.math.BcsGoalCreditFaultStage
import com.example.math.BcsGoalCreditLedgerTxnResult
import com.example.math.BcsGoalCreditReceiptStore
import com.example.math.BcsGoalCreditState
import com.example.math.ExamPresetType
import com.example.math.ExamQuestionBank
import com.example.math.MonotonicClock
import com.example.math.QuizQuestion
import com.example.math.QuizQuestionType
import com.example.ui.MathViewModel
import java.util.Calendar
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
class BcsGoalCreditViewModelEnqueueTest {

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
    // TEST 1: Eligible WP4.1 attempt atomically enqueues PENDING ledger row on finalization
    // =========================================================================
    @Test
    fun test1_wp41EligibleAttempt_enqueuesPendingLedgerRecordAtomicallyDuringFinalization() {
        val clock = FakeMonotonicClock(100_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())

        val creditEntered = CompletableDeferred<Unit>()
        val releaseCredit = CompletableDeferred<Unit>()
        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            creditEntered.complete(Unit)
            releaseCredit.await()
            defaultExecute(id, ts)
        }

        val attemptId = startAndAnswerBcsMock(vm, clock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 60L)
        assertTrue("WP4.1 launch-origin marker must exist synchronously on launch", receiptStore.isWp41Attempt(attemptId))
        vm.finishQuizSession()

        // Pause right as credit job begins: finalization has already committed atomically with PENDING row.
        awaitWithMainLooper(creditEntered)
        runBlocking(Dispatchers.IO) {
            val persistedAttempt = db.mathDao().getExamAttemptById(attemptId)
            assertNotNull("Completed attempt must already be committed before credit job starts", persistedAttempt)
            assertEquals("COMPLETED", persistedAttempt!!.status)

            val pendingRows = db.mathDao().getPendingBcsGoalCreditRecords()
            assertEquals("Exactly one PENDING ledger row must exist", 1, pendingRows.size)
            assertEquals(attemptId, pendingRows.single().attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, pendingRows.single().status)
            assertNull(pendingRows.single().creditedAtMs)
        }

        val job = vm.getActiveBcsGoalCreditJobForTesting(attemptId)!!
        releaseCredit.complete(Unit)
        awaitJobWithMainLooper(job)

        // Atomicity check: if finalization fails mid-transaction, NEITHER attempt NOR PENDING row survives and credit job does not run
        val sqlite = db.openHelper.writableDatabase
        val creditInvocationsAfterFail = AtomicInteger(0)
        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            creditInvocationsAfterFail.incrementAndGet()
            defaultExecute(id, ts)
        }
        sqlite.execSQL(
            """
            CREATE TEMP TRIGGER fail_quiz_scores_stage4a_t1
            BEFORE INSERT ON quiz_scores
            BEGIN
                SELECT RAISE(ABORT, 'Injected finalization rollback in stage4a test1');
            END;
            """.trimIndent()
        )
        val rolledBackAttemptId = startAndAnswerBcsMock(vm, clock, questionCount = 3, answersToProvide = 2, elapsedSeconds = 60L)
        try {
            vm.finishQuizSession()
            // Wait briefly for the finalization coroutine to run and roll back
            Thread.sleep(250)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        } finally {
            sqlite.execSQL("DROP TRIGGER IF EXISTS fail_quiz_scores_stage4a_t1")
        }
        runBlocking(Dispatchers.IO) {
            val rolledBackAttempt = db.mathDao().getExamAttemptById(rolledBackAttemptId)
            assertNotNull("Pre-existing IN_PROGRESS attempt must remain persisted after rollback", rolledBackAttempt)
            assertEquals("Rolled-back finalization must keep attempt IN_PROGRESS", "IN_PROGRESS", rolledBackAttempt!!.status)
            assertNull("Rolled-back finalization must keep completedAt null", rolledBackAttempt.completedAt)
            assertNull("Rolled-back finalization must not leave orphaned PENDING ledger row", db.mathDao().getBcsGoalCreditRecordByAttemptId(rolledBackAttemptId))
        }
        assertEquals("Credit job must NOT launch if finalization failed", 0, creditInvocationsAfterFail.get())
    }

    // =========================================================================
    // TEST 2: Exact PENDING ledger payload fields
    // =========================================================================
    @Test
    fun test2_exactPendingPayload_persistsAllRequiredFieldsVerbatim() {
        val clock = FakeMonotonicClock(200_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())
        val completionMs = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 22, 14, 30, 15)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val expectedDateKey = MathRepository.todayDateKey(completionMs)
        vm.wallClockNowMsProviderForTesting = { completionMs }

        val creditEntered = CompletableDeferred<Unit>()
        val releaseCredit = CompletableDeferred<Unit>()
        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            creditEntered.complete(Unit)
            releaseCredit.await()
            defaultExecute(id, ts)
        }

        // 3 answered questions, 150s elapsed -> studyTimeMinutesDelta = (150 + 30) / 60 = 3, mockExamsDelta = 1, revisionsDelta = 0
        val attemptId = startAndAnswerBcsMock(vm, clock, questionCount = 5, answersToProvide = 3, elapsedSeconds = 150L)
        vm.finishQuizSession()

        awaitWithMainLooper(creditEntered)
        runBlocking(Dispatchers.IO) {
            val record = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)
            assertNotNull("PENDING ledger record must exist", record)
            assertEquals(attemptId, record!!.attemptId)
            assertEquals(BcsExamProfile.BCS_50TH_PRELIM.id, record.examProfileId)
            assertEquals(expectedDateKey, record.targetDateKey)
            assertEquals(3, record.questionsDelta)
            assertEquals(3, record.studyTimeMinutesDelta)
            assertEquals(1, record.mockExamsDelta)
            assertEquals(0, record.revisionsDelta)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, record.status)
            assertEquals(completionMs, record.createdAtMs)
            assertNull(record.creditedAtMs)
        }

        val job = vm.getActiveBcsGoalCreditJobForTesting(attemptId)!!
        releaseCredit.complete(Unit)
        awaitJobWithMainLooper(job)

        assertNull("No new SharedPreferences credit receipt may be written", receiptStore.readReceipt(attemptId))
    }

    // =========================================================================
    // TEST 3: Midnight boundary preserves Day 1 targetDateKey when credited on Day 2
    // =========================================================================
    @Test
    fun test3_midnightBoundary_capturesDay1TargetDateKeyAndCreditsDay1OnDay2() {
        val clock = FakeMonotonicClock(300_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())

        val day1At235950Ms = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 21, 23, 59, 50)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val day2At000015Ms = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 22, 0, 0, 15)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val day1Key = MathRepository.todayDateKey(day1At235950Ms)
        val day2Key = MathRepository.todayDateKey(day2At000015Ms)
        assertTrue("Day 1 and Day 2 keys must differ ($day1Key vs $day2Key)", day1Key != day2Key)

        // Finalize at 23:59:50 on Day 1, hold credit job until wall clock crosses midnight to Day 2
        val currentWallMs = AtomicLong(day1At235950Ms)
        vm.wallClockNowMsProviderForTesting = { currentWallMs.get() }

        val creditEntered = CompletableDeferred<Unit>()
        val releaseCredit = CompletableDeferred<Unit>()
        vm.bcsGoalCreditLedgerExecutorForTesting = { id, _, defaultExecute ->
            creditEntered.complete(Unit)
            releaseCredit.await()
            // Execute credit with Day 2 timestamp (00:00:15)
            defaultExecute(id, day2At000015Ms)
        }

        val attemptId = startAndAnswerBcsMock(vm, clock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 90L)
        vm.finishQuizSession()

        awaitWithMainLooper(creditEntered)
        val job = vm.getActiveBcsGoalCreditJobForTesting(attemptId)!!

        // Verify PENDING row persisted Day 1 date key before midnight rollover
        runBlocking(Dispatchers.IO) {
            val pending = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)!!
            assertEquals(day1Key, pending.targetDateKey)
            assertEquals(day1At235950Ms, pending.createdAtMs)
        }

        // Cross midnight to Day 2 and let credit apply
        currentWallMs.set(day2At000015Ms)
        releaseCredit.complete(Unit)
        awaitJobWithMainLooper(job)

        runBlocking(Dispatchers.IO) {
            val credited = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, credited.status)
            assertEquals("targetDateKey must remain Day 1", day1Key, credited.targetDateKey)
            assertEquals(day1At235950Ms, credited.createdAtMs)
            assertEquals(day2At000015Ms, credited.creditedAtMs)

            val day1Goal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_$day1Key")
            val day2Goal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_$day2Key")
            assertNotNull("Day 1 goal row must receive the credit", day1Goal)
            assertEquals(2, day1Goal!!.questionsSolved)
            assertEquals(2, day1Goal.studyTimeMinutes)
            assertEquals(1, day1Goal.mockExamsCompleted)
            assertNull("Day 2 goal row must NOT be credited for a Day 1 attempt", day2Goal)
        }
    }

    // =========================================================================
    // TEST 4: Legacy/non-WP4.1 attempt never creates a PENDING ledger record
    // =========================================================================
    @Test
    fun test4_nonWp41Attempt_doesNotCreatePendingLedgerRecordOrMutateGoals() {
        val clock = FakeMonotonicClock(400_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())

        val creditExecCount = AtomicInteger(0)
        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            creditExecCount.incrementAndGet()
            defaultExecute(id, ts)
        }

        val legacyAttemptId = "legacy_pre_wp41_attempt_001"
        val sampleQuestions = listOf(
            QuizQuestion(
                id = "legacy_q1",
                questionBn = "২ + ৩ = ?",
                optionsBn = listOf("৪", "৫", "৬", "৭"),
                correctIndex = 1,
                correctAnswerText = "৫",
                explanationBn = "২ + ৩ = ৫",
                topicTitleBn = "পাটিগণিত",
                questionType = QuizQuestionType.MCQ
            )
        )
        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertExamAttempt(
                ExamAttemptEntity(
                    attemptId = legacyAttemptId,
                    examTitleBn = "লিগ্যাসি বিসিএস মক",
                    examType = ExamPresetType.BCS_PRELIMINARY_MOCK.id,
                    selectedTopicsCsv = "arithmetic",
                    difficulty = "medium",
                    questionType = "mcq",
                    totalQuestions = 1,
                    durationLimitSeconds = 600,
                    marksPerCorrect = 1.0,
                    negativeMarksPerWrong = 0.0,
                    status = "IN_PROGRESS",
                    currentQuestionIndex = 0,
                    elapsedSeconds = 45,
                    remainingSeconds = 555,
                    questionsJson = ExamQuestionBank.serializeQuestions(sampleQuestions),
                    answersJson = "{}",
                    markedForReviewCsv = "",
                    visitedIndicesCsv = "0",
                    questionTimeSecondsJson = "{}",
                    correctCount = 0,
                    wrongCount = 0,
                    unansweredCount = 1,
                    earnedMarks = 0.0,
                    negativeMarksDeducted = 0.0,
                    finalScore = 0.0,
                    percentage = 0.0,
                    accuracyPercent = 0.0,
                    startedAt = 1_763_700_000_000L,
                    updatedAt = 1_763_700_045_000L,
                    completedAt = null,
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id
                )
            )
        }
        assertFalse("Legacy attempt must NOT have WP4.1 origin marker", receiptStore.isWp41Attempt(legacyAttemptId))

        vm.resumeActiveExamAttempt()
        awaitConditionWithMainLooper { vm.quizSession.value.attemptId == legacyAttemptId }
        vm.selectQuizOption(1)
        clock.advanceSeconds(30L)
        vm.finishQuizSession()

        awaitConditionWithMainLooper {
            runBlocking(Dispatchers.IO) {
                db.mathDao().getExamAttemptById(legacyAttemptId)?.status == "COMPLETED"
            } && vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN
        }

        assertEquals("Credit ledger API must never run for non-WP4.1 attempt", 0, creditExecCount.get())
        runBlocking(Dispatchers.IO) {
            assertNull("Legacy attempt must NOT create a PENDING ledger row", db.mathDao().getBcsGoalCreditRecordByAttemptId(legacyAttemptId))
            assertTrue("Legacy attempt must NOT mutate bcs_study_goals", db.mathDao().getAllBcsStudyGoals().first().isEmpty())
        }
    }

    // =========================================================================
    // TEST 5: ViewModel delegates strictly via applyBcsCompletionGoalCreditFromLedger
    //         and never authorizes credit using in-memory delta values
    // =========================================================================
    @Test
    fun test5_creditDelegation_usesLedgerApiOnly_andIgnoresForgedInMemoryDelta() {
        val clock = FakeMonotonicClock(500_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())
        val attemptId = "vm_t5_persisted_authority"
        val creditedAtMs = 1_763_850_000_000L
        vm.wallClockNowMsProviderForTesting = { creditedAtMs }

        // Seed WP4.1 marker + authoritative persisted PENDING row in Room
        receiptStore.markWp41AttemptLaunched(attemptId)
        runBlocking(Dispatchers.IO) {
            db.mathDao().insertBcsGoalCreditRecordStrict(
                BcsGoalCreditRecordEntity(
                    attemptId = attemptId,
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                    targetDateKey = "2026-11-20",
                    questionsDelta = 4,
                    studyTimeMinutesDelta = 2,
                    mockExamsDelta = 1,
                    revisionsDelta = 0,
                    status = BcsGoalCreditRecordEntity.STATUS_PENDING,
                    createdAtMs = 1_763_800_000_000L,
                    creditedAtMs = null
                )
            )
        }

        val delegatedCalls = mutableListOf<Pair<String, Long>>()
        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            delegatedCalls.add(id to ts)
            defaultExecute(id, ts)
        }

        // Invoke execution with a forged in-memory delta carrying completely different profile & numbers
        val forgedInMemoryDelta = BcsGoalCreditDelta(
            attemptId = attemptId,
            examProfileId = BcsExamProfile.BCS_WRITTEN_REGULAR.id,
            questionsDelta = 999,
            studyTimeMinutesDelta = 888,
            mockExamsDelta = 77,
            revisionsDelta = 66
        )
        runBlocking {
            vm.executeBcsCompletionGoalCreditForTesting(delta = forgedInMemoryDelta, isExplicitRetry = false)
        }

        assertEquals(listOf(attemptId to creditedAtMs), delegatedCalls)
        val status = vm.getBcsGoalCreditStatusForAttemptForTesting(attemptId)!!
        assertEquals(BcsGoalCreditState.CREDITED, status.state)
        assertEquals(BcsExamProfile.BCS_50TH_PRELIM.id, status.examProfileId)
        assertEquals(4, status.appliedDelta?.questionsDelta)
        assertEquals(2, status.appliedDelta?.studyTimeMinutesDelta)
        assertEquals(1, status.appliedDelta?.mockExamsDelta)
        assertEquals(0, status.appliedDelta?.revisionsDelta)
        assertNull("No SharedPreferences receipt may be written", receiptStore.readReceipt(attemptId))

        runBlocking(Dispatchers.IO) {
            val goals = db.mathDao().getAllBcsStudyGoals().first()
            assertEquals("Only the persisted ledger profile+date row may be updated", 1, goals.size)
            val goal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_2026-11-20")!!
            assertEquals(4, goal.questionsSolved)
            assertEquals(2, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
            assertEquals(0, goal.revisionsCompleted)
        }
    }

    // =========================================================================
    // TEST 6: Applied result marks selected attempt CREDITED end-to-end
    // =========================================================================
    @Test
    fun test6_appliedResult_marksSelectedAttemptCreditedWithoutSharedPreferencesReceipt() {
        val clock = FakeMonotonicClock(600_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())
        val completionMs = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 22, 10, 15, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val dateKey = MathRepository.todayDateKey(completionMs)
        vm.wallClockNowMsProviderForTesting = { completionMs }

        val attemptId = startAndAnswerBcsMock(vm, clock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 60L)
        vm.finishQuizSession()

        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptId)
        }

        val uiStatus = vm.bcsGoalCreditStatus.value
        assertEquals(BcsExamProfile.BCS_50TH_PRELIM.id, uiStatus.examProfileId)
        assertEquals(2, uiStatus.appliedDelta?.questionsDelta)
        assertEquals(1, uiStatus.appliedDelta?.studyTimeMinutesDelta)
        assertEquals(1, uiStatus.appliedDelta?.mockExamsDelta)
        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptId))
        assertNull("SharedPreferences receipt must NOT be written for Room-ledger credits", receiptStore.readReceipt(attemptId))

        runBlocking(Dispatchers.IO) {
            val ledger = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledger.status)
            assertEquals(completionMs, ledger.creditedAtMs)

            val goal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_$dateKey")!!
            assertEquals(2, goal.questionsSolved)
            assertEquals(1, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
        }
    }

    // =========================================================================
    // TEST 7: AlreadyCredited updates UI without second goal increment
    // =========================================================================
    @Test
    fun test7_alreadyCredited_updatesUiStateWithoutSecondGoalIncrementAndPreservesCreditedAtMs() {
        val clock = FakeMonotonicClock(700_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())
        val attemptId = "vm_t7_already_credited"
        val originalCreditedAt = 1_763_811_000_000L

        // Pre-seed an already-CREDITED ledger row and matching goal row in Room (no SharedPreferences receipt)
        receiptStore.markWp41AttemptLaunched(attemptId)
        runBlocking(Dispatchers.IO) {
            db.mathDao().insertBcsGoalCreditRecordStrict(
                BcsGoalCreditRecordEntity(
                    attemptId = attemptId,
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                    targetDateKey = "2026-11-22",
                    questionsDelta = 5,
                    studyTimeMinutesDelta = 2,
                    mockExamsDelta = 1,
                    revisionsDelta = 0,
                    status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
                    createdAtMs = originalCreditedAt - 5_000L,
                    creditedAtMs = originalCreditedAt
                )
            )
            db.mathDao().upsertBcsStudyGoal(
                BcsStudyPlanGoalEntity(
                    goalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_2026-11-22",
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                    dateKey = "2026-11-22",
                    lessonsCompleted = 0,
                    questionsSolved = 5,
                    revisionsCompleted = 0,
                    studyTimeMinutes = 2,
                    mockExamsCompleted = 1,
                    updatedAt = originalCreditedAt
                )
            )
        }

        vm.wallClockNowMsProviderForTesting = { originalCreditedAt + 120_000L }
        runBlocking {
            vm.executeBcsCompletionGoalCreditForTesting(
                delta = BcsGoalCreditDelta(
                    attemptId = attemptId,
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                    questionsDelta = 5,
                    studyTimeMinutesDelta = 2,
                    mockExamsDelta = 1
                ),
                isExplicitRetry = false
            )
        }

        val status = vm.getBcsGoalCreditStatusForAttemptForTesting(attemptId)!!
        assertEquals(BcsGoalCreditState.CREDITED, status.state)
        assertEquals(5, status.appliedDelta?.questionsDelta)
        assertEquals(2, status.appliedDelta?.studyTimeMinutesDelta)
        assertEquals(1, status.appliedDelta?.mockExamsDelta)
        assertNull(receiptStore.readReceipt(attemptId))

        runBlocking(Dispatchers.IO) {
            val ledger = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)!!
            assertEquals("Original creditedAtMs must be preserved", originalCreditedAt, ledger.creditedAtMs)
            val goal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_2026-11-22")!!
            assertEquals("Goal must NOT be incremented a second time", 5, goal.questionsSolved)
            assertEquals(2, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
        }

        // Also verify AFTER_DB_COMMIT repository post-fault verification resolves as CREDITED with single increment
        val postFaultAttemptId = startAndAnswerBcsMock(vm, clock, questionCount = 3, answersToProvide = 2, elapsedSeconds = 60L)
        val postFaultTs = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 23, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val postFaultDateKey = MathRepository.todayDateKey(postFaultTs)
        vm.wallClockNowMsProviderForTesting = { postFaultTs }
        vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
            if (stage == BcsGoalCreditFaultStage.AFTER_DB_COMMIT) {
                throw IllegalStateException("Injected post-commit fault in test7")
            }
        }
        try {
            vm.finishQuizSession()
            awaitConditionWithMainLooper {
                vm.bcsGoalCreditStatus.value.attemptId == postFaultAttemptId &&
                    vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                    !vm.isBcsGoalCreditInFlightForTesting(postFaultAttemptId)
            }
        } finally {
            vm.setBcsGoalCreditFaultInjectorForTesting(null)
        }
        runBlocking(Dispatchers.IO) {
            val postFaultGoal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_$postFaultDateKey")!!
            assertEquals(2, postFaultGoal.questionsSolved)
            assertEquals(1, postFaultGoal.mockExamsCompleted)
        }
    }

    // =========================================================================
    // TEST 8: Failed credit with persisted PENDING row restores RETRYABLE_FAILURE
    //         from Room and succeeds on explicit retry without re-finalizing
    // =========================================================================
    @Test
    fun test8_pendingFailure_restoresRetryableStateFromPersistedRow_andRetriesCleanlyOnce() {
        val clock = FakeMonotonicClock(800_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())
        val completionMs = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 22, 16, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val dateKey = MathRepository.todayDateKey(completionMs)
        vm.wallClockNowMsProviderForTesting = { completionMs }

        vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
            if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                throw IllegalStateException("Injected pre-mutation failure for test8")
            }
        }

        val attemptId = startAndAnswerBcsMock(vm, clock, questionCount = 4, answersToProvide = 3, elapsedSeconds = 120L)
        vm.finishQuizSession()

        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptId)
        }
        vm.setBcsGoalCreditFaultInjectorForTesting(null)

        // Verify persisted PENDING row intact, diagnostic failure preserved, and retry payload matches Room row
        assertNotNull(vm.getLastBcsGoalCreditFailureForTesting(attemptId))
        val pendingRetryPayload = vm.getPendingBcsGoalCreditForTesting(attemptId)!!
        assertEquals(attemptId, pendingRetryPayload.attemptId)
        assertEquals(BcsExamProfile.BCS_50TH_PRELIM.id, pendingRetryPayload.examProfileId)
        assertEquals(3, pendingRetryPayload.questionsDelta)
        assertEquals(2, pendingRetryPayload.studyTimeMinutesDelta)
        assertEquals(1, pendingRetryPayload.mockExamsDelta)

        runBlocking(Dispatchers.IO) {
            val ledger = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, ledger.status)
            assertNull(ledger.creditedAtMs)
            assertTrue("Goal must remain untouched while PENDING", db.mathDao().getAllBcsStudyGoals().first().isEmpty())
            assertEquals("Finalization wrote exactly 1 quiz score", 1, db.mathDao().getAllQuizScores().first().size)
        }

        // Explicit retry applies from persisted Room PENDING row without a second finalization
        vm.retryBcsGoalCredit()
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == attemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(attemptId)
        }

        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptId))
        assertNull(receiptStore.readReceipt(attemptId))
        runBlocking(Dispatchers.IO) {
            assertEquals("Retry must NOT perform a second exam finalization", 1, db.mathDao().getAllQuizScores().first().size)
            val ledger = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledger.status)
            val goal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_$dateKey")!!
            assertEquals(3, goal.questionsSolved)
            assertEquals(2, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
        }
    }

    // =========================================================================
    // TEST 9: Missing or invalid ledger fails closed; CancellationException propagates
    // =========================================================================
    @Test
    fun test9_missingOrInvalidLedger_failsClosedWithNoGoalMutation_andCancellationPropagates() {
        val clock = FakeMonotonicClock(900_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())

        // 9a. Missing ledger record
        val missingAttemptId = "vm_t9_missing_ledger"
        receiptStore.markWp41AttemptLaunched(missingAttemptId)
        runBlocking {
            vm.executeBcsCompletionGoalCreditForTesting(
                delta = BcsGoalCreditDelta(
                    attemptId = missingAttemptId,
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                    questionsDelta = 5,
                    studyTimeMinutesDelta = 3,
                    mockExamsDelta = 1
                ),
                isExplicitRetry = false
            )
        }
        assertEquals(BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN, vm.getBcsGoalCreditStatusForAttemptForTesting(missingAttemptId)?.state)
        assertFalse(vm.hasPendingBcsGoalCreditForTesting(missingAttemptId))
        assertNull(receiptStore.readReceipt(missingAttemptId))
        runBlocking(Dispatchers.IO) {
            assertNull("Must not fabricate a ledger row from memory", db.mathDao().getBcsGoalCreditRecordByAttemptId(missingAttemptId))
            assertTrue("Must not mutate bcs_study_goals", db.mathDao().getAllBcsStudyGoals().first().isEmpty())
        }

        // 9b. Corrupt/invalid ledger status fails closed with zero goal mutation
        val corruptAttemptId = "vm_t9_corrupt_ledger"
        receiptStore.markWp41AttemptLaunched(corruptAttemptId)
        db.openHelper.writableDatabase.execSQL(
            """
            INSERT INTO `bcs_goal_credit_records` (
                `attemptId`, `examProfileId`, `targetDateKey`,
                `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                `status`, `createdAtMs`, `creditedAtMs`
            ) VALUES (
                '$corruptAttemptId', '${BcsExamProfile.BCS_50TH_PRELIM.id}', '2026-11-22',
                5, 2, 1, 0,
                'CORRUPT_STATUS', 1763800000000, NULL
            )
            """.trimIndent()
        )
        runBlocking {
            vm.executeBcsCompletionGoalCreditForTesting(
                delta = BcsGoalCreditDelta(
                    attemptId = corruptAttemptId,
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                    questionsDelta = 5,
                    studyTimeMinutesDelta = 2,
                    mockExamsDelta = 1
                ),
                isExplicitRetry = false
            )
        }
        assertEquals(BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN, vm.getBcsGoalCreditStatusForAttemptForTesting(corruptAttemptId)?.state)
        runBlocking(Dispatchers.IO) {
            assertEquals("CORRUPT_STATUS", db.mathDao().getBcsGoalCreditRecordByAttemptId(corruptAttemptId)!!.status)
            assertTrue("Invalid ledger state must never mutate goals", db.mathDao().getAllBcsStudyGoals().first().isEmpty())
        }

        // 9c. CancellationException is never swallowed or converted to CREDITED
        val cancelAttemptId = "vm_t9_cancel"
        receiptStore.markWp41AttemptLaunched(cancelAttemptId)
        runBlocking(Dispatchers.IO) {
            db.mathDao().insertBcsGoalCreditRecordStrict(
                BcsGoalCreditRecordEntity(
                    attemptId = cancelAttemptId,
                    examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                    targetDateKey = "2026-11-22",
                    questionsDelta = 4,
                    studyTimeMinutesDelta = 2,
                    mockExamsDelta = 1,
                    revisionsDelta = 0,
                    status = BcsGoalCreditRecordEntity.STATUS_PENDING,
                    createdAtMs = 1_763_800_000_000L,
                    creditedAtMs = null
                )
            )
        }
        vm.bcsGoalCreditLedgerExecutorForTesting = { _, _, _ ->
            throw CancellationException("Injected cancellation during credit")
        }
        try {
            runBlocking {
                vm.executeBcsCompletionGoalCreditForTesting(
                    delta = BcsGoalCreditDelta(
                        attemptId = cancelAttemptId,
                        examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                        questionsDelta = 4,
                        studyTimeMinutesDelta = 2,
                        mockExamsDelta = 1
                    ),
                    isExplicitRetry = false
                )
            }
            fail("CancellationException must propagate")
        } catch (ce: CancellationException) {
            assertEquals("Injected cancellation during credit", ce.message)
        } finally {
            vm.bcsGoalCreditLedgerExecutorForTesting = null
        }
        assertFalse(vm.isBcsGoalCreditInFlightForTesting(cancelAttemptId))
        assertTrue(
            "Cancellation must never convert status to CREDITED",
            vm.getBcsGoalCreditStatusForAttemptForTesting(cancelAttemptId)?.state != BcsGoalCreditState.CREDITED
        )
    }

    // =========================================================================
    // TEST 10: BCS-GC-01 isolation preserved — Attempt A background job cannot
    //          overwrite Attempt B's selected UI status
    // =========================================================================
    @Test
    fun test10_bcsGc01Isolation_backgroundAttemptCompletionDoesNotOverwriteSelectedAttemptUi() {
        val clock = FakeMonotonicClock(1_000_000L)
        val vm = MathViewModel(app, clock = clock, savedStateHandle = SavedStateHandle())
        val completionMs = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 22, 18, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val dateKey = MathRepository.todayDateKey(completionMs)
        vm.wallClockNowMsProviderForTesting = { completionMs }

        val attemptAEntered = CompletableDeferred<Unit>()
        val releaseAttemptA = CompletableDeferred<Unit>()
        var attemptAId = ""

        vm.bcsGoalCreditLedgerExecutorForTesting = { id, ts, defaultExecute ->
            if (id == attemptAId) {
                attemptAEntered.complete(Unit)
                releaseAttemptA.await()
            }
            defaultExecute(id, ts)
        }

        // Start & finish Attempt A, pause its credit job in-flight
        attemptAId = startAndAnswerBcsMock(vm, clock, questionCount = 4, answersToProvide = 2, elapsedSeconds = 60L)
        vm.finishQuizSession()
        awaitWithMainLooper(attemptAEntered)
        val jobA = vm.getActiveBcsGoalCreditJobForTesting(attemptAId)!!
        assertTrue(vm.isBcsGoalCreditInFlightForTesting(attemptAId))
        assertEquals(attemptAId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.IN_PROGRESS, vm.bcsGoalCreditStatus.value.state)

        // Start Attempt B while Attempt A is in-flight -> UI selects Attempt B (NOT_STARTED)
        vm.startBcsMockExam(questionCount = 4, durationMinutes = 10)
        val attemptBId = vm.quizSession.value.attemptId
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)

        // Release Attempt A -> completes CREDITED in background
        releaseAttemptA.complete(Unit)
        awaitJobWithMainLooper(jobA)

        // Attempt B's visible UI state MUST NOT be clobbered
        assertEquals(attemptBId, vm.bcsGoalCreditStatus.value.attemptId)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)

        // Attempt A's own per-attempt state and Room rows completed cleanly
        assertFalse(vm.isBcsGoalCreditInFlightForTesting(attemptAId))
        assertFalse(vm.hasPendingBcsGoalCreditForTesting(attemptAId))
        val storedA = vm.getBcsGoalCreditStatusForAttemptForTesting(attemptAId)!!
        assertEquals(BcsGoalCreditState.CREDITED, storedA.state)
        assertEquals(2, storedA.appliedDelta?.questionsDelta)
        assertEquals(1, storedA.appliedDelta?.mockExamsDelta)
        assertNull(receiptStore.readReceipt(attemptAId))

        runBlocking(Dispatchers.IO) {
            val ledgerA = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptAId)!!
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerA.status)
            val goal = db.mathDao().getBcsStudyGoalByKey("${BcsExamProfile.BCS_50TH_PRELIM.id}_$dateKey")!!
            assertEquals(2, goal.questionsSolved)
            assertEquals(1, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
        }
    }
}
