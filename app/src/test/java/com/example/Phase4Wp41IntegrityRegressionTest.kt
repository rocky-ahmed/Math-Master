package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsMistakeEntity
import com.example.data.model.BookMistakeEntity
import com.example.data.model.BookSpacedRevisionEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.UnifiedMistakeEntity
import com.example.data.repository.MathRepository
import com.example.math.BcsExamProfile
import com.example.math.BcsGoalCreditCalculator
import com.example.math.BcsGoalCreditFaultStage
import com.example.math.BcsGoalCreditReceiptStore
import com.example.math.BcsGoalCreditState
import com.example.math.BcsPracticeMode
import com.example.math.BcsSyllabusUnit
import com.example.math.BookId
import com.example.math.BookPracticeMode
import com.example.math.BookQuestionFormat
import com.example.math.ExamQuestionBank
import com.example.math.MonotonicClock
import com.example.math.NumberSystemDifficulty
import com.example.math.NumberSystemTopic
import com.example.math.PracticeDifficulty
import com.example.math.QuestionInputFormat
import com.example.math.QuizMode
import com.example.math.QuizQuestion
import com.example.math.QuizQuestionType
import com.example.ui.MathViewModel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class Phase4Wp41IntegrityRegressionTest {

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

    private fun waitUntilMain(maxIterations: Int = 200, condition: () -> Boolean) {
        var iter = 0
        while (!condition() && iter < maxIterations) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (condition()) break
            Thread.sleep(15)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            iter++
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

    // =========================================================================
    // F4-01: NUMBER SYSTEM PROGRESSION & SINGLE-COMPLETION PERSISTENCE
    // =========================================================================

    @Test
    fun f401_numberSystem_unansweredSkipBlocked_repeatedSubmitAndFinishPersistStrictlyOnce() {
        val vm = MathViewModel(app, savedStateHandle = SavedStateHandle())

        // Empty question list boundary: nextNumberSystemQuestion() before starting must not persist anything.
        vm.nextNumberSystemQuestion()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        Thread.sleep(80)
        runBlocking(Dispatchers.IO) {
            assertEquals(0, db.mathDao().getAllQuizScores().first().size)
            assertEquals(0, db.mathDao().getAllPracticeHistory().first().size)
        }

        vm.startNumberSystemPractice(
            topic = NumberSystemTopic.ALL,
            difficulty = NumberSystemDifficulty.EASY,
            inputFormat = QuestionInputFormat.MCQ
        )
        val initial = vm.numberSystemPracticeState.value
        assertTrue("Number System session must generate questions", initial.questions.isNotEmpty())
        val totalQuestions = initial.questions.size

        // Unanswered question cannot be skipped.
        vm.nextNumberSystemQuestion()
        assertEquals(0, vm.numberSystemPracticeState.value.currentIndex)
        assertFalse(vm.numberSystemPracticeState.value.isCompleted)

        // Rapid concurrent submit calls on question 0 must score & record strictly once.
        val q0 = initial.questions[0]
        vm.selectNumberSystemOption(q0.correctIndex)
        val submitThreads = 8
        val submitLatch = CountDownLatch(1)
        val submitExecutor = Executors.newFixedThreadPool(submitThreads)
        val submitDone = CountDownLatch(submitThreads)
        repeat(submitThreads) {
            submitExecutor.submit {
                try {
                    submitLatch.await(3, TimeUnit.SECONDS)
                    vm.submitNumberSystemAnswer()
                } finally {
                    submitDone.countDown()
                }
            }
        }
        submitLatch.countDown()
        assertTrue(submitDone.await(5, TimeUnit.SECONDS))
        submitExecutor.shutdownNow()

        assertEquals(1, vm.numberSystemPracticeState.value.score)
        assertEquals(1, vm.numberSystemPracticeState.value.userAnswers.size)
        assertTrue(vm.numberSystemPracticeState.value.isSubmitted)

        // Complete questions 0..last-1 cleanly.
        for (idx in 0 until totalQuestions - 1) {
            vm.nextNumberSystemQuestion()
            assertEquals(idx + 1, vm.numberSystemPracticeState.value.currentIndex)
            val q = vm.numberSystemPracticeState.value.questions[idx + 1]
            // Unanswered skip blocked at intermediate index too.
            vm.nextNumberSystemQuestion()
            assertEquals(idx + 1, vm.numberSystemPracticeState.value.currentIndex)

            vm.selectNumberSystemOption(q.correctIndex)
            vm.submitNumberSystemAnswer()
            // Sequential repeated submit is a no-op.
            vm.submitNumberSystemAnswer()
        }
        assertEquals(totalQuestions, vm.numberSystemPracticeState.value.score)
        assertEquals(totalQuestions - 1, vm.numberSystemPracticeState.value.currentIndex)

        // Concurrent + repeated nextNumberSystemQuestion() on final question must persist strictly ONCE.
        val finishThreads = 8
        val finishLatch = CountDownLatch(1)
        val finishExecutor = Executors.newFixedThreadPool(finishThreads)
        val finishDone = CountDownLatch(finishThreads)
        repeat(finishThreads) {
            finishExecutor.submit {
                try {
                    finishLatch.await(3, TimeUnit.SECONDS)
                    vm.nextNumberSystemQuestion()
                } finally {
                    finishDone.countDown()
                }
            }
        }
        finishLatch.countDown()
        assertTrue(finishDone.await(5, TimeUnit.SECONDS))
        finishExecutor.shutdownNow()

        // Extra sequential calls after completion are strict no-ops.
        repeat(5) { vm.nextNumberSystemQuestion() }

        assertTrue(vm.numberSystemPracticeState.value.isCompleted)
        waitUntilMain {
            runBlocking(Dispatchers.IO) {
                db.mathDao().getAllQuizScores().first().isNotEmpty() &&
                    db.mathDao().getAllPracticeHistory().first().isNotEmpty()
            }
        }
        Thread.sleep(100)
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        runBlocking(Dispatchers.IO) {
            val quizScores = db.mathDao().getAllQuizScores().first()
            val history = db.mathDao().getAllPracticeHistory().first()
            val analytics = db.mathDao().getAllTopicAnalytics().first()
                .filter { it.module == "NUMBER_SYSTEM" }
            assertEquals("QuizScoreEntity must be persisted strictly once", 1, quizScores.size)
            assertEquals(totalQuestions, quizScores.first().score)
            assertEquals(totalQuestions, quizScores.first().totalQuestions)
            assertEquals("PracticeHistoryEntity must be persisted strictly once", 1, history.size)
            assertEquals(totalQuestions, history.first().correctCount)
            assertEquals(1, analytics.size)
            assertEquals(totalQuestions, analytics.first().totalAttempted)
            assertEquals(totalQuestions, analytics.first().totalCorrect)
        }
    }

    // =========================================================================
    // F4-02: BCS DAILY STUDY GOAL COMPLETION-ONLY CREDIT, ROUNDING & LIFECYCLE
    // =========================================================================

    @Test
    fun f402_calculator_elapsedTimeRoundingPolicyAndZeroAttemptGuard() {
        // Zero attempted questions or zero elapsed seconds -> 0 minutes.
        assertEquals(0, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 95, attemptedQuestions = 0))
        assertEquals(0, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 0, attemptedQuestions = 3))
        assertEquals(0, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = -5, attemptedQuestions = 3))
        // Rounding to nearest whole minute with 1-minute floor for answered work:
        assertEquals(1, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 1, attemptedQuestions = 1))
        assertEquals(1, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 25, attemptedQuestions = 2))
        assertEquals(1, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 89, attemptedQuestions = 2))
        assertEquals(2, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 90, attemptedQuestions = 2))
        assertEquals(2, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 95, attemptedQuestions = 2))
        assertEquals(3, BcsGoalCreditCalculator.computeStudyTimeMinutesDelta(elapsedSeconds = 150, attemptedQuestions = 2))
    }

    @Test
    fun f402_launchWritesZeroGoalCredit_abandonedAndZeroAttemptWriteZeroCredit() {
        val fakeClock = FakeMonotonicClock(50_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())

        // 1. Launch startBcsModeExam -> ZERO goal rows created at launch.
        vm.startBcsModeExam(mode = BcsPracticeMode.TOPIC_PRACTICE, unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC)
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        Thread.sleep(80)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)
        runBlocking(Dispatchers.IO) {
            assertEquals(0, db.mathDao().getAllBcsStudyGoals().first().size)
        }

        // 2. Launch startBcsMockExam -> ZERO goal rows created at launch.
        vm.startBcsMockExam(questionCount = 5, durationMinutes = 10)
        val mockAttemptId = vm.quizSession.value.attemptId
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        Thread.sleep(80)
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)
        runBlocking(Dispatchers.IO) {
            assertEquals(0, db.mathDao().getAllBcsStudyGoals().first().size)
        }

        // 3. Finish BCS mock exam with ZERO answered questions -> ZERO goal credit and ZERO ledger row.
        fakeClock.advanceSeconds(45)
        vm.finishQuizSession()
        awaitConditionWithMainLooper {
            runBlocking(Dispatchers.IO) {
                db.mathDao().getExamAttemptById(mockAttemptId)?.status == "COMPLETED"
            }
        }
        assertEquals(BcsGoalCreditState.NOT_STARTED, vm.bcsGoalCreditStatus.value.state)
        assertNull(receiptStore.readReceipt(mockAttemptId))
        runBlocking(Dispatchers.IO) {
            assertNull(db.mathDao().getBcsGoalCreditRecordByAttemptId(mockAttemptId))
            assertEquals(0, db.mathDao().getAllBcsStudyGoals().first().size)
        }
    }

    @Test
    fun f402_timedMockAndUntimedModesCreditExactDeltasAndReceipts() {
        val fakeClock = FakeMonotonicClock(100_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayKey = MathRepository.todayDateKey()

        // A. Timed BCS Mock Exam: answer 3 out of 5 questions, advance clock 95s -> rounded 2 min, mockExamsCompleted = 1.
        vm.startBcsMockExam(questionCount = 5, durationMinutes = 10)
        val mockAttemptId = vm.quizSession.value.attemptId
        val mockQs = vm.quizSession.value.questions
        assertTrue(mockQs.size >= 3)
        for (i in 0 until 3) {
            vm.jumpToQuizQuestion(i)
            vm.selectQuizOption(mockQs[i].correctIndex)
        }
        fakeClock.advanceSeconds(95)
        vm.finishQuizSession()

        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == mockAttemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(mockAttemptId)
        }
        val statusA = vm.bcsGoalCreditStatus.value
        assertEquals(mockAttemptId, statusA.attemptId)
        assertEquals(3, statusA.appliedDelta?.questionsDelta)
        assertEquals(2, statusA.appliedDelta?.studyTimeMinutesDelta)
        assertEquals(1, statusA.appliedDelta?.mockExamsDelta)
        assertEquals(0, statusA.appliedDelta?.revisionsDelta)
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for timed mock attempt",
            receiptStore.readReceipt(mockAttemptId)
        )

        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_$todayKey"
        runBlocking(Dispatchers.IO) {
            val mockLedger = db.mathDao().getBcsGoalCreditRecordByAttemptId(mockAttemptId)
            assertNotNull("Timed mock Room ledger record must be persisted", mockLedger)
            assertEquals(mockAttemptId, mockLedger!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, mockLedger.status)
            assertNotNull("Timed mock creditedAtMs must be persisted", mockLedger.creditedAtMs)
            assertEquals(3, mockLedger.questionsDelta)
            assertEquals(2, mockLedger.studyTimeMinutesDelta)
            assertEquals(1, mockLedger.mockExamsDelta)
            assertEquals(0, mockLedger.revisionsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)
            assertNotNull(goal)
            assertEquals(3, goal!!.questionsSolved)
            assertEquals(2, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
            assertEquals(0, goal.revisionsCompleted)
        }

        // B. Untimed TOPIC_PRACTICE: answer 2 questions, advance clock 25s -> rounded 1 min, mockExamsCompleted MUST remain 1.
        vm.startBcsModeExam(mode = BcsPracticeMode.TOPIC_PRACTICE, unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC)
        val topicAttemptId = vm.quizSession.value.attemptId
        val topicQs = vm.quizSession.value.questions
        assertTrue(topicQs.size >= 2)
        for (i in 0 until 2) {
            vm.jumpToQuizQuestion(i)
            vm.selectQuizOption(topicQs[i].correctIndex)
            vm.submitQuizAnswer()
        }
        fakeClock.advanceSeconds(25)
        vm.finishQuizSession()

        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == topicAttemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(topicAttemptId)
        }
        assertEquals(0, vm.bcsGoalCreditStatus.value.appliedDelta?.mockExamsDelta)
        assertEquals(2, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
        assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.studyTimeMinutesDelta)
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for topic practice attempt",
            receiptStore.readReceipt(topicAttemptId)
        )

        // C. Untimed MISTAKE_PRACTICE: answer 1 question, advance clock 35s -> rounded 1 min, mockExamsDelta = 0, revisionsDelta = 1.
        vm.startBcsModeExam(mode = BcsPracticeMode.MISTAKE_PRACTICE)
        val mistakeAttemptId = vm.quizSession.value.attemptId
        val mistakeQs = vm.quizSession.value.questions
        assertTrue(mistakeQs.isNotEmpty())
        vm.selectQuizOption(mistakeQs[0].correctIndex)
        vm.submitQuizAnswer()
        fakeClock.advanceSeconds(35)
        vm.finishQuizSession()

        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == mistakeAttemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(mistakeAttemptId)
        }
        assertEquals(0, vm.bcsGoalCreditStatus.value.appliedDelta?.mockExamsDelta)
        assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.revisionsDelta)
        assertNull(
            "BCS-GC-02 must NOT write a new SharedPreferences receipt for mistake practice attempt",
            receiptStore.readReceipt(mistakeAttemptId)
        )

        runBlocking(Dispatchers.IO) {
            val topicLedger = db.mathDao().getBcsGoalCreditRecordByAttemptId(topicAttemptId)
            assertNotNull("Topic practice Room ledger record must be persisted", topicLedger)
            assertEquals(topicAttemptId, topicLedger!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, topicLedger.status)
            assertNotNull(topicLedger.creditedAtMs)
            assertEquals(2, topicLedger.questionsDelta)
            assertEquals(1, topicLedger.studyTimeMinutesDelta)
            assertEquals(0, topicLedger.mockExamsDelta)
            assertEquals(0, topicLedger.revisionsDelta)

            val mistakeLedger = db.mathDao().getBcsGoalCreditRecordByAttemptId(mistakeAttemptId)
            assertNotNull("Mistake practice Room ledger record must be persisted", mistakeLedger)
            assertEquals(mistakeAttemptId, mistakeLedger!!.attemptId)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, mistakeLedger.status)
            assertNotNull(mistakeLedger.creditedAtMs)
            assertEquals(1, mistakeLedger.questionsDelta)
            assertEquals(1, mistakeLedger.studyTimeMinutesDelta)
            assertEquals(0, mistakeLedger.mockExamsDelta)
            assertEquals(1, mistakeLedger.revisionsDelta)

            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals(6, goal.questionsSolved)
            assertEquals(4, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
            assertEquals(1, goal.revisionsCompleted)
        }
    }

    @Test
    fun f402_faultBeforeMutation_allowsSameViewModelRetry_andBlocksUnsafeRetryAfterRecreation() {
        val fakeClock = FakeMonotonicClock(200_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayKey = MathRepository.todayDateKey()
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_$todayKey"

        try {
            // -----------------------------------------------------------------
            // Path 1: Same-ViewModel explicit retry after BEFORE_DB_MUTATION
            // -----------------------------------------------------------------
            vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
                if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                    throw IllegalStateException("Injected failure strictly before Room mutation (Path 1)")
                }
            }

            vm.startBcsMockExam(questionCount = 4, durationMinutes = 10)
            val attempt1Id = vm.quizSession.value.attemptId
            val qs1 = vm.quizSession.value.questions
            vm.selectQuizOption(qs1[0].correctIndex)
            vm.jumpToQuizQuestion(1)
            vm.selectQuizOption(qs1[1].correctIndex)
            fakeClock.advanceSeconds(60)
            vm.finishQuizSession()

            awaitConditionWithMainLooper {
                vm.bcsGoalCreditStatus.value.attemptId == attempt1Id &&
                    vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE &&
                    !vm.isBcsGoalCreditInFlightForTesting(attempt1Id)
            }
            assertNull(receiptStore.readReceipt(attempt1Id))
            runBlocking(Dispatchers.IO) {
                val completed1 = db.mathDao().getExamAttemptById(attempt1Id)
                assertNotNull(completed1)
                assertEquals("COMPLETED", completed1!!.status)

                val pendingLedger1 = db.mathDao().getBcsGoalCreditRecordByAttemptId(attempt1Id)
                assertNotNull("Attempt 1 PENDING ledger record must survive BEFORE_DB_MUTATION fault", pendingLedger1)
                assertEquals(attempt1Id, pendingLedger1!!.attemptId)
                assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, pendingLedger1.status)
                assertNull("PENDING ledger must have null creditedAtMs before mutation", pendingLedger1.creditedAtMs)
                assertEquals(2, pendingLedger1.questionsDelta)
                assertEquals(1, pendingLedger1.studyTimeMinutesDelta)
                assertEquals(1, pendingLedger1.mockExamsDelta)
                assertEquals(0, pendingLedger1.revisionsDelta)

                assertNull(
                    "Pre-mutation failure must perform zero Room DB goal mutation",
                    db.mathDao().getBcsStudyGoalByKey(todayGoalKey)
                )
            }

            // Clear fault and trigger explicit retry in the SAME ViewModel -> succeeds strictly once.
            vm.setBcsGoalCreditFaultInjectorForTesting(null)
            vm.retryBcsGoalCredit()
            awaitConditionWithMainLooper {
                vm.bcsGoalCreditStatus.value.attemptId == attempt1Id &&
                    vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                    !vm.isBcsGoalCreditInFlightForTesting(attempt1Id)
            }

            assertNull(receiptStore.readReceipt(attempt1Id))
            val attempt1CreditedAtMs = runBlocking(Dispatchers.IO) {
                val creditedLedger1 = db.mathDao().getBcsGoalCreditRecordByAttemptId(attempt1Id)!!
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, creditedLedger1.status)
                assertNotNull(creditedLedger1.creditedAtMs)
                val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
                assertEquals(2, goal.questionsSolved)
                assertEquals(1, goal.studyTimeMinutes)
                assertEquals(1, goal.mockExamsCompleted)
                creditedLedger1.creditedAtMs!!
            }

            // Duplicate same-ViewModel retry is a strict no-op.
            vm.retryBcsGoalCredit()
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            runBlocking(Dispatchers.IO) {
                assertEquals(attempt1CreditedAtMs, db.mathDao().getBcsGoalCreditRecordByAttemptId(attempt1Id)!!.creditedAtMs)
                val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
                assertEquals(2, goal.questionsSolved)
                assertEquals(1, goal.studyTimeMinutes)
                assertEquals(1, goal.mockExamsCompleted)
            }

            // -----------------------------------------------------------------
            // Path 2: Process/ViewModel recreation recovers durable Room PENDING
            // via Stage 4B2A startup sweep exactly once (BCS-GC-02)
            // -----------------------------------------------------------------
            vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
                if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                    throw IllegalStateException("Injected failure strictly before Room mutation (Path 2)")
                }
            }

            vm.startBcsMockExam(questionCount = 4, durationMinutes = 10)
            val attempt2Id = vm.quizSession.value.attemptId
            val qs2 = vm.quizSession.value.questions
            for (i in 0 until 3) {
                vm.jumpToQuizQuestion(i)
                vm.selectQuizOption(qs2[i].correctIndex)
            }
            fakeClock.advanceSeconds(95)
            vm.finishQuizSession()

            awaitConditionWithMainLooper {
                vm.bcsGoalCreditStatus.value.attemptId == attempt2Id &&
                    vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.RETRYABLE_FAILURE &&
                    !vm.isBcsGoalCreditInFlightForTesting(attempt2Id)
            }
            assertNull(receiptStore.readReceipt(attempt2Id))

            val savedCompleted2 = runBlocking(Dispatchers.IO) {
                val completed2 = db.mathDao().getExamAttemptById(attempt2Id)
                assertNotNull(completed2)
                assertEquals("COMPLETED", completed2!!.status)

                val pendingLedger2 = db.mathDao().getBcsGoalCreditRecordByAttemptId(attempt2Id)
                assertNotNull("Attempt 2 PENDING ledger record must be durably persisted", pendingLedger2)
                assertEquals(attempt2Id, pendingLedger2!!.attemptId)
                assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, pendingLedger2.status)
                assertNull("Attempt 2 creditedAtMs must remain null after BEFORE_DB_MUTATION fault", pendingLedger2.creditedAtMs)
                assertEquals(3, pendingLedger2.questionsDelta)
                assertEquals(2, pendingLedger2.studyTimeMinutesDelta)
                assertEquals(1, pendingLedger2.mockExamsDelta)
                assertEquals(0, pendingLedger2.revisionsDelta)

                // Goal totals must still reflect ONLY Attempt 1 (no increment for Attempt 2 yet).
                val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
                assertEquals(2, goal.questionsSolved)
                assertEquals(1, goal.studyTimeMinutes)
                assertEquals(1, goal.mockExamsCompleted)
                completed2
            }

            // Clear fault before simulating process/ViewModel recreation.
            vm.setBcsGoalCreditFaultInjectorForTesting(null)

            // Recreate ViewModel -> Stage 4B2A startup sweep automatically recovers the durable PENDING record.
            val recreatedVm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
            recreatedVm.getBcsGoalCreditStartupRecoveryJobForTesting()?.let { awaitJobWithMainLooper(it) }
            awaitConditionWithMainLooper {
                runBlocking(Dispatchers.IO) {
                    db.mathDao().getBcsGoalCreditRecordByAttemptId(attempt2Id)?.status ==
                        BcsGoalCreditRecordEntity.STATUS_CREDITED
                }
            }

            assertNull(
                "BCS-GC-02 startup recovery must NOT write a new SharedPreferences receipt",
                receiptStore.readReceipt(attempt2Id)
            )
            val attempt2CreditedAtMs = runBlocking(Dispatchers.IO) {
                val creditedLedger2 = db.mathDao().getBcsGoalCreditRecordByAttemptId(attempt2Id)!!
                assertEquals(attempt2Id, creditedLedger2.attemptId)
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, creditedLedger2.status)
                assertNotNull("Recovered ledger must persist non-null creditedAtMs", creditedLedger2.creditedAtMs)
                assertEquals(3, creditedLedger2.questionsDelta)
                assertEquals(2, creditedLedger2.studyTimeMinutesDelta)
                assertEquals(1, creditedLedger2.mockExamsDelta)
                assertEquals(0, creditedLedger2.revisionsDelta)

                // Goal totals now reflect Attempt 1 (2q, 1m, 1 mock) + Attempt 2 (3q, 2m, 1 mock) strictly once.
                val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
                assertEquals(5, goal.questionsSolved)
                assertEquals(3, goal.studyTimeMinutes)
                assertEquals(2, goal.mockExamsCompleted)
                creditedLedger2.creditedAtMs!!
            }

            // Reopening the completed attempt in the recreated ViewModel reconciles cleanly to CREDITED.
            recreatedVm.openCompletedExamResult(savedCompleted2)
            awaitConditionWithMainLooper {
                recreatedVm.bcsGoalCreditStatus.value.attemptId == attempt2Id &&
                    recreatedVm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
            }
            assertEquals(3, recreatedVm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
            assertEquals(2, recreatedVm.bcsGoalCreditStatus.value.appliedDelta?.studyTimeMinutesDelta)
            assertEquals(1, recreatedVm.bcsGoalCreditStatus.value.appliedDelta?.mockExamsDelta)

            // Duplicate retry, duplicate recovery sweep, and reopening in a third fresh ViewModel perform ZERO second credit.
            recreatedVm.retryBcsGoalCredit()
            recreatedVm.triggerBcsGoalCreditStartupRecoveryForTesting()?.let { awaitJobWithMainLooper(it) }

            val thirdVm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
            thirdVm.getBcsGoalCreditStartupRecoveryJobForTesting()?.let { awaitJobWithMainLooper(it) }
            thirdVm.openCompletedExamResult(savedCompleted2)
            awaitConditionWithMainLooper {
                thirdVm.bcsGoalCreditStatus.value.attemptId == attempt2Id &&
                    thirdVm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
            }
            thirdVm.retryBcsGoalCredit()
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

            assertNull(receiptStore.readReceipt(attempt2Id))
            runBlocking(Dispatchers.IO) {
                val ledgerAfterReopen = db.mathDao().getBcsGoalCreditRecordByAttemptId(attempt2Id)!!
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerAfterReopen.status)
                assertEquals(attempt2CreditedAtMs, ledgerAfterReopen.creditedAtMs)

                val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
                assertEquals("No second credit may occur after reopen or duplicate recovery", 5, goal.questionsSolved)
                assertEquals(3, goal.studyTimeMinutes)
                assertEquals(2, goal.mockExamsCompleted)
            }
        } finally {
            vm.setBcsGoalCreditFaultInjectorForTesting(null)
        }
    }

    @Test
    fun f402_faultAfterCommit_transitionsToUnknownAndPreventsDuplicateRetry() {
        val fakeClock = FakeMonotonicClock(300_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_${MathRepository.todayDateKey()}"

        try {
            vm.setBcsGoalCreditFaultInjectorForTesting { stage ->
                if (stage == BcsGoalCreditFaultStage.AFTER_DB_COMMIT) {
                    throw IllegalStateException("Injected post-commit failure")
                }
            }

            vm.startBcsMockExam(questionCount = 3, durationMinutes = 10)
            val attemptId = vm.quizSession.value.attemptId
            val qs = vm.quizSession.value.questions
            vm.selectQuizOption(qs[0].correctIndex)
            fakeClock.advanceSeconds(60)
            vm.finishQuizSession()

            // Under BCS-GC-02, post-fault Room ledger verification proves the commit succeeded -> CREDITED.
            awaitConditionWithMainLooper {
                vm.bcsGoalCreditStatus.value.attemptId == attemptId &&
                    vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                    !vm.isBcsGoalCreditInFlightForTesting(attemptId)
            }
            assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
            assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.studyTimeMinutesDelta)
            assertEquals(1, vm.bcsGoalCreditStatus.value.appliedDelta?.mockExamsDelta)
            assertNull(
                "BCS-GC-02 must NOT write a new SharedPreferences receipt after post-commit verification",
                receiptStore.readReceipt(attemptId)
            )

            // Actual Room ledger record is CREDITED with non-null creditedAtMs, and DB goal mutated strictly once.
            val (savedCompleted, originalCreditedAtMs) = runBlocking(Dispatchers.IO) {
                val ledger = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)
                assertNotNull("Room ledger record must exist after AFTER_DB_COMMIT fault", ledger)
                assertEquals(attemptId, ledger!!.attemptId)
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledger.status)
                assertNotNull("creditedAtMs must be persisted on committed Room ledger row", ledger.creditedAtMs)
                assertEquals(1, ledger.questionsDelta)
                assertEquals(1, ledger.studyTimeMinutesDelta)
                assertEquals(1, ledger.mockExamsDelta)
                assertEquals(0, ledger.revisionsDelta)

                val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
                assertEquals(1, goal.questionsSolved)
                assertEquals(1, goal.studyTimeMinutes)
                assertEquals(1, goal.mockExamsCompleted)
                db.mathDao().getExamAttemptById(attemptId)!! to ledger.creditedAtMs!!
            }

            // Clearing fault and invoking retry or startup recovery MUST be a strict no-op (zero duplicate credit).
            vm.setBcsGoalCreditFaultInjectorForTesting(null)
            vm.retryBcsGoalCredit()
            vm.triggerBcsGoalCreditStartupRecoveryForTesting()?.let { awaitJobWithMainLooper(it) }
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)

            // Reopening the completed attempt on both the original and a fresh cold-start ViewModel resolves to CREDITED.
            vm.openCompletedExamResult(savedCompleted)
            assertEquals(attemptId, vm.bcsGoalCreditStatus.value.attemptId)
            assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)

            val reopenedVm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
            reopenedVm.getBcsGoalCreditStartupRecoveryJobForTesting()?.let { awaitJobWithMainLooper(it) }
            reopenedVm.openCompletedExamResult(savedCompleted)
            awaitConditionWithMainLooper {
                reopenedVm.bcsGoalCreditStatus.value.attemptId == attemptId &&
                    reopenedVm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED
            }
            assertEquals(1, reopenedVm.bcsGoalCreditStatus.value.appliedDelta?.questionsDelta)
            assertEquals(1, reopenedVm.bcsGoalCreditStatus.value.appliedDelta?.studyTimeMinutesDelta)
            assertEquals(1, reopenedVm.bcsGoalCreditStatus.value.appliedDelta?.mockExamsDelta)
            assertNull(receiptStore.readReceipt(attemptId))

            runBlocking(Dispatchers.IO) {
                val ledgerAfter = db.mathDao().getBcsGoalCreditRecordByAttemptId(attemptId)!!
                assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledgerAfter.status)
                assertEquals("creditedAtMs must be preserved across retries and reopens", originalCreditedAtMs, ledgerAfter.creditedAtMs)

                val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
                assertEquals(1, goal.questionsSolved)
                assertEquals(1, goal.studyTimeMinutes)
                assertEquals(1, goal.mockExamsCompleted)
            }
        } finally {
            vm.setBcsGoalCreditFaultInjectorForTesting(null)
        }
    }

    @Test
    fun f402_legacyPreWp41InProgressAttempt_resumedAndFinishedDoesNotDoubleCredit() {
        val fakeClock = FakeMonotonicClock(400_000L)
        val todayKey = MathRepository.todayDateKey()
        val nowMs = System.currentTimeMillis()
        val sampleQ = QuizQuestion(
            id = "bcs_legacy_inprog_q1",
            questionBn = "২ + ৩ = কত?",
            optionsBn = listOf("৪", "৫", "৬", "৭"),
            correctIndex = 1,
            correctAnswerText = "৫",
            explanationBn = "২ + ৩ = ৫",
            topicTitleBn = "পাটিগণিত",
            questionType = QuizQuestionType.MCQ
        )

        // Seed a pre-WP4.1 IN_PROGRESS BCS attempt directly in Room WITHOUT WP4.1 launch-origin marker
        // and seed the goal row simulating the launch-time credit it already received before upgrade.
        val legacyAttempt = ExamAttemptEntity(
            attemptId = "bcs_mock_legacy_pre_wp41_001",
            examTitleBn = "পূর্বের ইন-প্রগ্রেস বিসিএস মক",
            examType = "BCS_PRELIMINARY_MOCK",
            selectedTopicsCsv = "arithmetic",
            difficulty = "medium",
            questionType = "mcq",
            totalQuestions = 1,
            durationLimitSeconds = 300,
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = 0.0,
            status = "IN_PROGRESS",
            currentQuestionIndex = 0,
            elapsedSeconds = 60,
            remainingSeconds = 240,
            questionsJson = ExamQuestionBank.serializeQuestions(listOf(sampleQ)),
            answersJson = ExamQuestionBank.serializeAnswersWithTyped(mapOf(0 to 1), emptyMap()),
            markedForReviewCsv = "",
            visitedIndicesCsv = "0",
            questionTimeSecondsJson = "{}",
            correctCount = 1,
            wrongCount = 0,
            unansweredCount = 0,
            earnedMarks = 1.0,
            negativeMarksDeducted = 0.0,
            finalScore = 1.0,
            percentage = 100.0,
            accuracyPercent = 100.0,
            startedAt = nowMs - 60_000L,
            updatedAt = nowMs - 10_000L,
            completedAt = null,
            examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id
        )
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_$todayKey"
        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertExamAttempt(legacyAttempt)
            MathRepository(db.mathDao()).recordBcsStudyGoalDelta(
                examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                questionsDelta = 1,
                studyTimeMinutesDelta = 5,
                mockExamsDelta = 1
            )
        }

        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        vm.resumeActiveExamAttempt(legacyAttempt)
        fakeClock.advanceSeconds(30)
        vm.finishQuizSession()

        awaitConditionWithMainLooper {
            runBlocking(Dispatchers.IO) {
                db.mathDao().getExamAttemptById(legacyAttempt.attemptId)?.status == "COMPLETED"
            } && vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN
        }
        assertNull(receiptStore.readReceipt(legacyAttempt.attemptId))

        // Goal totals MUST remain strictly at the pre-existing values (no double credit) and zero ledger row enqueued.
        runBlocking(Dispatchers.IO) {
            assertNull(db.mathDao().getBcsGoalCreditRecordByAttemptId(legacyAttempt.attemptId))
            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals(1, goal.questionsSolved)
            assertEquals(5, goal.studyTimeMinutes)
            assertEquals(1, goal.mockExamsCompleted)
        }
    }

    @Test
    fun f402_concurrentGoalDeltaWrites_loseZeroIncrements_andCancellationPreservesTerminalState() {
        val repo = MathRepository(db.mathDao())
        val todayGoalKey = "${BcsExamProfile.BCS_50TH_PRELIM.id}_${MathRepository.todayDateKey()}"

        // Concurrent goal delta increments across 12 threads -> zero lost updates via bcsStudyGoalMutex + @Transaction.
        val workerCount = 12
        val latch = CountDownLatch(1)
        val done = CountDownLatch(workerCount)
        val pool = Executors.newFixedThreadPool(workerCount)
        repeat(workerCount) {
            pool.submit {
                try {
                    latch.await(3, TimeUnit.SECONDS)
                    runBlocking(Dispatchers.IO) {
                        repo.recordBcsStudyGoalDelta(
                            examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
                            questionsDelta = 2,
                            studyTimeMinutesDelta = 1,
                            mockExamsDelta = 1,
                            revisionsDelta = 1
                        )
                    }
                } finally {
                    done.countDown()
                }
            }
        }
        latch.countDown()
        assertTrue(done.await(10, TimeUnit.SECONDS))
        pool.shutdownNow()

        runBlocking(Dispatchers.IO) {
            val goal = db.mathDao().getBcsStudyGoalByKey(todayGoalKey)!!
            assertEquals(workerCount * 2, goal.questionsSolved)
            assertEquals(workerCount, goal.studyTimeMinutes)
            assertEquals(workerCount, goal.mockExamsCompleted)
            assertEquals(workerCount, goal.revisionsCompleted)
        }

        // Cancellation after terminal CREDITED state must NOT overwrite CREDITED.
        val fakeClock = FakeMonotonicClock(500_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        vm.startBcsMockExam(questionCount = 2, durationMinutes = 5)
        val cancelAttemptId = vm.quizSession.value.attemptId
        vm.selectQuizOption(vm.quizSession.value.questions[0].correctIndex)
        fakeClock.advanceSeconds(60)
        vm.finishQuizSession()
        awaitConditionWithMainLooper {
            vm.bcsGoalCreditStatus.value.attemptId == cancelAttemptId &&
                vm.bcsGoalCreditStatus.value.state == BcsGoalCreditState.CREDITED &&
                !vm.isBcsGoalCreditInFlightForTesting(cancelAttemptId)
        }

        vm.cancelActiveBcsGoalCreditForTesting()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(BcsGoalCreditState.CREDITED, vm.bcsGoalCreditStatus.value.state)
        assertNull(receiptStore.readReceipt(cancelAttemptId))
        runBlocking(Dispatchers.IO) {
            val ledger = db.mathDao().getBcsGoalCreditRecordByAttemptId(cancelAttemptId)
            assertNotNull(ledger)
            assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, ledger!!.status)
            assertNotNull(ledger.creditedAtMs)
        }
    }

    // =========================================================================
    // F4-03: BCS MISTAKE INTEGRITY, SINGLE CANONICAL KEY & PROFILE ISOLATION
    // =========================================================================

    @Test
    fun f403_bcsImmediateFeedbackAndFullExam_writeSingleCanonicalKey_preserveLegacyQuizAndIsolateProfiles() {
        val fakeClock = FakeMonotonicClock(600_000L)
        val vm = MathViewModel(app, clock = fakeClock, savedStateHandle = SavedStateHandle())
        val legacyCreatedAt = System.currentTimeMillis() - 500_000L

        // Seed a pre-existing historical generic QUIZ_ mistake AND a legacy unscoped BCS_ mistake.
        val legacyGenericKey = "QUIZ_পাটিগণিত_999999"
        val legacyUnscopedBcsKey = "BCS_shared_q_99"
        runBlocking(Dispatchers.IO) {
            db.mathDao().upsertUnifiedMistake(
                UnifiedMistakeEntity(
                    mistakeKey = legacyGenericKey,
                    sourceModule = "QUIZ",
                    subjectId = "পাটিগণিত",
                    curriculumTrack = "NCTB",
                    questionPromptBn = "ঐতিহাসিক কুইজ প্রশ্ন",
                    userAnswerBn = "ক",
                    correctAnswerBn = "খ",
                    diagnosticExplanationBn = "ব্যাখ্যা",
                    lastFailedAtMillis = legacyCreatedAt
                )
            )
            db.mathDao().upsertUnifiedMistake(
                UnifiedMistakeEntity(
                    mistakeKey = legacyUnscopedBcsKey,
                    sourceModule = "BCS",
                    subjectId = "algebra",
                    curriculumTrack = "BCS",
                    questionPromptBn = "ঐতিহাসিক আনস্কোপড বিসিএস প্রশ্ন",
                    userAnswerBn = "১",
                    correctAnswerBn = "২",
                    diagnosticExplanationBn = "ব্যাখ্যা",
                    lastFailedAtMillis = legacyCreatedAt
                )
            )
        }

        // 1. Immediate-feedback BCS session (TOPIC_PRACTICE) -> wrong answer creates 1 BcsMistakeEntity + 1 canonical BCS unified key (ZERO new QUIZ_ rows).
        vm.setBcsExamProfile(BcsExamProfile.BCS_50TH_PRELIM)
        vm.startBcsModeExam(mode = BcsPracticeMode.TOPIC_PRACTICE, unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC)
        val immQ = vm.quizSession.value.questions[0]
        val wrongImmIdx = (immQ.correctIndex + 1) % immQ.optionsBn.size
        vm.selectQuizOption(wrongImmIdx)
        vm.submitQuizAnswer()

        val rawImmId = MathRepository.extractCanonicalBcsQuestionId(
            immQ.id.ifBlank { "bcs_q_${kotlin.math.abs(immQ.questionBn.hashCode())}" }
        )
        val canonicalImmUnifiedKey = "BCS_${BcsExamProfile.BCS_50TH_PRELIM.id}_$rawImmId"
        waitUntilMain {
            runBlocking(Dispatchers.IO) {
                db.mathDao().getUnifiedMistakeByKey(canonicalImmUnifiedKey) != null
            }
        }
        runBlocking(Dispatchers.IO) {
            val allUnified = db.mathDao().getAllUnifiedMistakes().first()
            val quizKeys = allUnified.filter { it.mistakeKey.startsWith("QUIZ_") }.map { it.mistakeKey }
            assertEquals("Only pre-existing legacy QUIZ_ row may exist; zero duplicate QUIZ_ rows created", listOf(legacyGenericKey), quizKeys)
            assertNotNull(db.mathDao().getUnifiedMistakeByKey(canonicalImmUnifiedKey))
            assertEquals(1, db.mathDao().getAllBcsMistakes().first().size)
        }

        // 2. Full-exam BCS session (startBcsMockExam) -> wrong answer creates 1 BcsMistakeEntity + 1 canonical BCS unified key, zero generic QUIZ_ duplicate.
        vm.startBcsMockExam(questionCount = 2, durationMinutes = 5)
        val fullQ = vm.quizSession.value.questions[0]
        val wrongFullIdx = (fullQ.correctIndex + 1) % fullQ.optionsBn.size
        vm.selectQuizOption(wrongFullIdx)
        fakeClock.advanceSeconds(60)
        vm.finishQuizSession()

        val rawFullId = MathRepository.extractCanonicalBcsQuestionId(
            fullQ.id.ifBlank { "bcs_q_${kotlin.math.abs(fullQ.questionBn.hashCode())}" }
        )
        val canonicalFullUnifiedKey = "BCS_${BcsExamProfile.BCS_50TH_PRELIM.id}_$rawFullId"
        waitUntilMain {
            runBlocking(Dispatchers.IO) {
                db.mathDao().getUnifiedMistakeByKey(canonicalFullUnifiedKey) != null
            }
        }
        runBlocking(Dispatchers.IO) {
            val allUnified = db.mathDao().getAllUnifiedMistakes().first()
            val quizKeys = allUnified.filter { it.mistakeKey.startsWith("QUIZ_") }.map { it.mistakeKey }
            assertEquals("Historical generic QUIZ_ row preserved and zero new QUIZ_ rows added", listOf(legacyGenericKey), quizKeys)
        }

        // 3. Profile isolation: record same raw questionId under BCS_50TH_PRELIM and BCS_WRITTEN_008_MATH,
        // then resolve ONLY BCS_WRITTEN_008_MATH -> BCS_50TH_PRELIM and legacy unscoped BCS_shared_q_99 MUST remain unresolved!
        val repo = MathRepository(db.mathDao())
        runBlocking(Dispatchers.IO) {
            repo.recordBcsMistake(
                questionId = "shared_q_99",
                subjectTrack = "MATH_REASONING",
                topicId = "algebra",
                questionPromptBn = "প্রোফাইল আইসোলেশন প্রশ্ন",
                wrongAnswerBn = "১",
                correctAnswerBn = "২",
                explanationBn = "ব্যাখ্যা",
                examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id
            )
            repo.recordBcsMistake(
                questionId = "shared_q_99",
                subjectTrack = "MATH_REASONING",
                topicId = "algebra",
                questionPromptBn = "প্রোফাইল আইসোলেশন প্রশ্ন",
                wrongAnswerBn = "৩",
                correctAnswerBn = "২",
                explanationBn = "ব্যাখ্যা",
                examProfileId = BcsExamProfile.BCS_WRITTEN_008_MATH.id
            )

            val writtenBefore = db.mathDao().getBcsMistakeById("${BcsExamProfile.BCS_WRITTEN_008_MATH.id}::shared_q_99")!!
            val writtenUnifiedBefore = db.mathDao().getUnifiedMistakeByKey("BCS_${BcsExamProfile.BCS_WRITTEN_008_MATH.id}_shared_q_99")!!
            val originalBcsMistakeAt = writtenBefore.lastMistakeAt
            val originalUnifiedFailedAt = writtenUnifiedBefore.lastFailedAtMillis

            Thread.sleep(25)
            // Resolve Written 008 twice -> idempotent + preserves lastMistakeAt & lastFailedAtMillis + does NOT touch 50th or legacy unscoped key.
            repo.markBcsMistakeResolved(writtenBefore.questionId, examProfileId = BcsExamProfile.BCS_WRITTEN_008_MATH.id)
            repo.markBcsMistakeResolved(writtenBefore.questionId, examProfileId = BcsExamProfile.BCS_WRITTEN_008_MATH.id)

            val writtenAfter = db.mathDao().getBcsMistakeById(writtenBefore.questionId)!!
            val writtenUnifiedAfter = db.mathDao().getUnifiedMistakeByKey(writtenUnifiedBefore.mistakeKey)!!
            val prelim50After = db.mathDao().getBcsMistakeById("shared_q_99")!!
            val prelim50UnifiedAfter = db.mathDao().getUnifiedMistakeByKey("BCS_${BcsExamProfile.BCS_50TH_PRELIM.id}_shared_q_99")!!
            val legacyUnscopedAfter = db.mathDao().getUnifiedMistakeByKey(legacyUnscopedBcsKey)!!
            val legacyGenericAfter = db.mathDao().getUnifiedMistakeByKey(legacyGenericKey)!!

            assertTrue(writtenAfter.isResolved)
            assertEquals("lastMistakeAt must be preserved on resolve", originalBcsMistakeAt, writtenAfter.lastMistakeAt)
            assertTrue(writtenUnifiedAfter.resolved)
            assertEquals("Review interval must advance strictly once (1 -> 3)", 3, writtenUnifiedAfter.reviewIntervalDays)
            assertEquals("lastFailedAtMillis must be preserved on resolve", originalUnifiedFailedAt, writtenUnifiedAfter.lastFailedAtMillis)

            assertFalse("50th profile BcsMistake must remain unresolved", prelim50After.isResolved)
            assertFalse("50th profile UnifiedMistake must remain unresolved", prelim50UnifiedAfter.resolved)
            assertFalse("Legacy unscoped BCS_ key must NOT be resolved by non-default Written profile", legacyUnscopedAfter.resolved)
            assertFalse("Historical generic QUIZ_ row must NOT be mutated or deleted", legacyGenericAfter.resolved)
        }
    }

    // =========================================================================
    // F4-04: BOOK PRACTICE & UNIFIED MISTAKE SINGLE-ADVANCEMENT & IDEMPOTENCY
    // =========================================================================

    @Test
    fun f404_bookPracticeCorrectRetry_advancesIntervalStrictlyOnceAndPreservesLastFailedTimestamp() {
        val vm = MathViewModel(app, savedStateHandle = SavedStateHandle())
        vm.startBookPractice(
            bookId = BookId.CLASS_9_10,
            chapterNumber = 1,
            mode = BookPracticeMode.STANDARD,
            questionCount = 5
        )
        val q = vm.bookPracticeSession.value.questions.first()
        val bookMistakeId = "mistake_${q.id}"
        val unifiedKey = "BOOK_${q.id}"

        // Seed initial mistake in all 3 tables (simulating prior wrong attempt / historical record).
        val repo = MathRepository(db.mathDao())
        runBlocking(Dispatchers.IO) {
            repo.recordBookQuestionMistake(
                questionId = bookMistakeId,
                bookId = q.bookId.id,
                chapterNumber = q.chapterNumber,
                questionPromptBn = q.promptBn,
                wrongAnswerBn = "ভুল উত্তর",
                correctAnswerBn = q.correctTextAnswer,
                explanationBn = q.explanationBn,
                sourceProvenance = q.provenance.name,
                chapterTitleBn = "অধ্যায় ১"
            )
        }

        val initialBookMistake = runBlocking(Dispatchers.IO) { db.mathDao().getBookMistakeById(bookMistakeId)!! }
        val initialSpaced = runBlocking(Dispatchers.IO) { db.mathDao().getBookSpacedRevisionById(bookMistakeId)!! }
        val initialUnified = runBlocking(Dispatchers.IO) { db.mathDao().getUnifiedMistakeByKey(unifiedKey)!! }
        assertEquals(0, initialSpaced.intervalStage)
        assertEquals(1, initialUnified.reviewIntervalDays)

        Thread.sleep(30)
        // Submit the CORRECT answer in Book Practice.
        if (q.correctOptionIndices.isNotEmpty()) {
            q.correctOptionIndices.forEach { idx -> vm.toggleBookPracticeOption(idx) }
        } else {
            vm.setBookPracticeTextInput(q.correctTextAnswer)
        }
        vm.submitBookPracticeAnswer()

        waitUntilMain {
            runBlocking(Dispatchers.IO) {
                db.mathDao().getBookMistakeById(bookMistakeId)?.isResolved == true
            }
        }
        Thread.sleep(80)

        runBlocking(Dispatchers.IO) {
            val resolvedBook = db.mathDao().getBookMistakeById(bookMistakeId)!!
            val resolvedSpaced = db.mathDao().getBookSpacedRevisionById(bookMistakeId)!!
            val resolvedUnified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!

            assertTrue(resolvedBook.isResolved)
            assertEquals("book_mistakes.lastMistakeAt must be preserved on resolve", initialBookMistake.lastMistakeAt, resolvedBook.lastMistakeAt)
            assertEquals("book_spaced_revision.intervalStage must advance strictly ONCE (0 -> 1)", 1, resolvedSpaced.intervalStage)
            assertTrue(resolvedUnified.resolved)
            assertEquals("unified_mistake_notebook.reviewIntervalDays must advance strictly ONCE (1 -> 3, NOT 7)", 3, resolvedUnified.reviewIntervalDays)
            assertEquals("unified_mistake_notebook.lastFailedAtMillis must be preserved on resolve", initialUnified.lastFailedAtMillis, resolvedUnified.lastFailedAtMillis)
        }
    }

    @Test
    fun f404_repeatedResolution_retryAfterFailure_unresolveReresolve_dashboardSync_andConcurrency() {
        val repo = MathRepository(db.mathDao())
        val rawId = "book_f404_cycle_q1"
        val bookMistakeId = "mistake_$rawId"
        val unifiedKey = "BOOK_$rawId"

        runBlocking(Dispatchers.IO) {
            // 1. Initial failure at t1.
            repo.recordBookQuestionMistake(
                questionId = bookMistakeId,
                bookId = "class_8",
                chapterNumber = 2,
                questionPromptBn = "চক্র পরীক্ষার প্রশ্ন",
                wrongAnswerBn = "১০",
                correctAnswerBn = "২০",
                explanationBn = "সঠিক ২০",
                sourceProvenance = "ORIGINAL_TEXTBOOK",
                chapterTitleBn = "অধ্যায় ২"
            )
            val t1Book = db.mathDao().getBookMistakeById(bookMistakeId)!!.lastMistakeAt
            val t1Unified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!.lastFailedAtMillis

            Thread.sleep(25)
            // 2. Resolve via Dashboard unified path -> coordinates ALL 3 tables once.
            repo.resolveUnifiedMistake(unifiedKey)
            val afterFirstResolveUnified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!
            val afterFirstResolveSpaced = db.mathDao().getBookSpacedRevisionById(bookMistakeId)!!
            val afterFirstResolveBook = db.mathDao().getBookMistakeById(bookMistakeId)!!
            assertTrue(afterFirstResolveUnified.resolved)
            assertTrue(afterFirstResolveBook.isResolved)
            assertEquals(3, afterFirstResolveUnified.reviewIntervalDays)
            assertEquals(1, afterFirstResolveSpaced.intervalStage)
            assertEquals(t1Unified, afterFirstResolveUnified.lastFailedAtMillis)
            assertEquals(t1Book, afterFirstResolveBook.lastMistakeAt)

            // 3. Repeated resolution through both entry points -> strict NO-OP (idempotent).
            Thread.sleep(25)
            repo.resolveUnifiedMistake(unifiedKey)
            repo.markBookMistakeResolved(bookMistakeId, resolved = true)
            val afterDupUnified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!
            val afterDupSpaced = db.mathDao().getBookSpacedRevisionById(bookMistakeId)!!
            assertEquals(3, afterDupUnified.reviewIntervalDays)
            assertEquals(afterFirstResolveUnified.nextReviewAtMillis, afterDupUnified.nextReviewAtMillis)
            assertEquals(1, afterDupSpaced.intervalStage)
            assertEquals(afterFirstResolveSpaced.nextReviewEpochMs, afterDupSpaced.nextReviewEpochMs)
            assertEquals(t1Unified, afterDupUnified.lastFailedAtMillis)

            // 4. Unresolve -> re-resolve cycle: unresolve preserves lastFailedAtMillis; re-resolve advances 3 -> 7 and stage 1 -> 2.
            Thread.sleep(25)
            repo.unresolveUnifiedMistake(unifiedKey)
            val afterUnresolveUnified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!
            val afterUnresolveBook = db.mathDao().getBookMistakeById(bookMistakeId)!!
            assertFalse(afterUnresolveUnified.resolved)
            assertFalse(afterUnresolveBook.isResolved)
            assertEquals(t1Unified, afterUnresolveUnified.lastFailedAtMillis)
            assertEquals(t1Book, afterUnresolveBook.lastMistakeAt)

            repo.resolveUnifiedMistake(unifiedKey)
            val afterSecondResolveUnified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!
            val afterSecondResolveSpaced = db.mathDao().getBookSpacedRevisionById(bookMistakeId)!!
            assertTrue(afterSecondResolveUnified.resolved)
            assertEquals(7, afterSecondResolveUnified.reviewIntervalDays)
            assertEquals(2, afterSecondResolveSpaced.intervalStage)
            assertEquals(t1Unified, afterSecondResolveUnified.lastFailedAtMillis)

            // 5. Retry-after-failure cycle: fail again at t3 -> resets interval to 1, updates lastFailedAtMillis to t3, failureCount = 2.
            Thread.sleep(25)
            repo.recordBookQuestionMistake(
                questionId = bookMistakeId,
                bookId = "class_8",
                chapterNumber = 2,
                questionPromptBn = "চক্র পরীক্ষার প্রশ্ন",
                wrongAnswerBn = "৩০",
                correctAnswerBn = "২০",
                explanationBn = "সঠিক ২০",
                sourceProvenance = "ORIGINAL_TEXTBOOK",
                chapterTitleBn = "অধ্যায় ২"
            )
            val failedAgainUnified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!
            val t3Unified = failedAgainUnified.lastFailedAtMillis
            assertFalse(failedAgainUnified.resolved)
            assertEquals(2, failedAgainUnified.failureCount)
            assertEquals(1, failedAgainUnified.reviewIntervalDays)
            assertTrue("New failure must update lastFailedAtMillis", t3Unified > t1Unified)

            // 6. Concurrent resolution race across 10 threads -> advances interval strictly ONCE (1 -> 3, stage 0 -> 1) preserving t3.
            val threads = 10
            val startLatch = CountDownLatch(1)
            val doneLatch = CountDownLatch(threads)
            val executor = Executors.newFixedThreadPool(threads)
            val counter = AtomicInteger(0)
            repeat(threads) { idx ->
                executor.submit {
                    try {
                        startLatch.await(3, TimeUnit.SECONDS)
                        runBlocking(Dispatchers.IO) {
                            if (idx % 2 == 0) {
                                repo.markBookMistakeResolved(bookMistakeId, resolved = true)
                            } else {
                                repo.resolveUnifiedMistake(unifiedKey)
                            }
                            counter.incrementAndGet()
                        }
                    } finally {
                        doneLatch.countDown()
                    }
                }
            }
            startLatch.countDown()
            assertTrue(doneLatch.await(10, TimeUnit.SECONDS))
            executor.shutdownNow()
            assertEquals(threads, counter.get())

            val finalUnified = db.mathDao().getUnifiedMistakeByKey(unifiedKey)!!
            val finalSpaced = db.mathDao().getBookSpacedRevisionById(bookMistakeId)!!
            val finalBook = db.mathDao().getBookMistakeById(bookMistakeId)!!
            assertTrue(finalUnified.resolved)
            assertTrue(finalBook.isResolved)
            assertEquals("Concurrent resolutions must advance reviewIntervalDays strictly ONCE (1 -> 3)", 3, finalUnified.reviewIntervalDays)
            assertEquals("Concurrent resolutions must advance intervalStage strictly ONCE (0 -> 1)", 1, finalSpaced.intervalStage)
            assertEquals("lastFailedAtMillis must remain at t3 after concurrent resolution", t3Unified, finalUnified.lastFailedAtMillis)
        }
    }

    @Test
    fun f404_resolvingNonExistentBookMistake_isNoOp_andHistoricalRecordResolvesCleanly() {
        val repo = MathRepository(db.mathDao())
        runBlocking(Dispatchers.IO) {
            // Resolving a question that was never missed must not insert phantom rows.
            repo.markBookMistakeResolved("mistake_never_missed_q", resolved = true)
            assertNull(db.mathDao().getBookMistakeById("mistake_never_missed_q"))
            assertNull(db.mathDao().getBookSpacedRevisionById("mistake_never_missed_q"))
            assertNull(db.mathDao().getUnifiedMistakeByKey("BOOK_never_missed_q"))

            // Pre-existing historical Book mistake row inserted directly in Room resolves cleanly without double-advancement.
            val histTime = System.currentTimeMillis() - 1_000_000L
            db.mathDao().upsertBookMistake(
                BookMistakeEntity(
                    questionId = "mistake_hist_book_1",
                    bookId = "class_7",
                    chapterNumber = 4,
                    questionPromptBn = "ঐতিহাসিক বই প্রশ্ন",
                    previousWrongAnswerBn = "৫",
                    correctAnswerBn = "১০",
                    explanationBn = "সঠিক ১০",
                    sourceProvenance = "ORIGINAL_TEXTBOOK",
                    lastMistakeAt = histTime
                )
            )
            db.mathDao().upsertBookSpacedRevision(
                BookSpacedRevisionEntity(
                    itemId = "mistake_hist_book_1",
                    itemType = "QUESTION",
                    bookId = "class_7",
                    chapterNumber = 4,
                    titleBn = "ঐতিহাসিক বই প্রশ্ন",
                    subtitleBn = "১০",
                    intervalStage = 0,
                    nextReviewEpochMs = histTime + 86_400_000L,
                    lastReviewedAt = histTime
                )
            )
            db.mathDao().upsertUnifiedMistake(
                UnifiedMistakeEntity(
                    mistakeKey = "BOOK_hist_book_1",
                    sourceModule = "BOOK",
                    subjectId = "class_7",
                    curriculumTrack = "NCTB",
                    questionPromptBn = "ঐতিহাসিক বই প্রশ্ন",
                    userAnswerBn = "৫",
                    correctAnswerBn = "১০",
                    diagnosticExplanationBn = "সঠিক ১০",
                    reviewIntervalDays = 1,
                    lastFailedAtMillis = histTime
                )
            )

            repo.markBookMistakeResolved("mistake_hist_book_1", resolved = true)
            val bookAfter = db.mathDao().getBookMistakeById("mistake_hist_book_1")!!
            val spacedAfter = db.mathDao().getBookSpacedRevisionById("mistake_hist_book_1")!!
            val unifiedAfter = db.mathDao().getUnifiedMistakeByKey("BOOK_hist_book_1")!!

            assertTrue(bookAfter.isResolved)
            assertEquals(histTime, bookAfter.lastMistakeAt)
            assertEquals(1, spacedAfter.intervalStage)
            assertTrue(unifiedAfter.resolved)
            assertEquals(3, unifiedAfter.reviewIntervalDays)
            assertEquals(histTime, unifiedAfter.lastFailedAtMillis)
        }
    }
}
