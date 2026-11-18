package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.math.BookId
import com.example.math.BookPracticeMode
import com.example.math.MathProblemSolverEngine
import com.example.math.MentalMathCategory
import com.example.math.MentalMathDifficulty
import com.example.math.MentalMathMode
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.math.QuizMode
import com.example.math.QuizQuestionType
import com.example.math.QuizTopic
import com.example.math.SharedMathEngine
import com.example.math.SolutionStep
import com.example.math.SolverMode
import com.example.math.SolverResult
import com.example.math.SolverTopic
import com.example.ui.MathViewModel
import com.example.ui.SolverPersistenceOutcome
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class Phase3ConcurrencyAndSolverCancellationTest {

    private lateinit var app: Application
    private lateinit var db: MathDatabase

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        db = MathDatabase.getInstance(app)
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun waitUntilMain(maxIterations: Int = 150, condition: () -> Boolean) {
        var iter = 0
        while (!condition() && iter < maxIterations) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (condition()) break
            Thread.sleep(20)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            iter++
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun examPersistenceMutex_rapidInProgressThenFinish_alwaysPersistsCompleted() {
        val viewModel = MathViewModel(app, savedStateHandle = SavedStateHandle())
        viewModel.startQuiz(
            topic = QuizTopic.ALGEBRA,
            count = 5,
            mode = QuizMode.TOPIC,
            difficulty = PracticeDifficulty.EASY,
            questionType = QuizQuestionType.MCQ
        )
        val startedState = viewModel.quizSession.value
        val attemptId = startedState.attemptId
        assertTrue(attemptId.isNotBlank())

        repeat(15) { tick ->
            viewModel.persistActiveExamStateAsync(
                startedState.copy(
                    elapsedSeconds = tick + 1,
                    timeRemainingSeconds = 120 - tick
                )
            )
        }
        viewModel.finishQuizSession()

        waitUntilMain {
            val saved = runBlocking(Dispatchers.IO) { db.mathDao().getExamAttemptById(attemptId) }
            saved?.status == "COMPLETED"
        }

        val finalRow = runBlocking(Dispatchers.IO) { db.mathDao().getExamAttemptById(attemptId) }
        assertNotNull(finalRow)
        assertEquals("COMPLETED", finalRow!!.status)
    }

    @Test
    fun concurrentFinishCalls_recordExactlyOneCompletionEntryAcrossAllModules() {
        // 1) finishQuizSession
        val vmQuiz = MathViewModel(app, savedStateHandle = SavedStateHandle())
        vmQuiz.startQuiz(QuizTopic.ARITHMETIC_PERCENTAGE, 3, QuizMode.TOPIC, PracticeDifficulty.EASY, QuizQuestionType.MCQ)
        runTwoThreadRace(
            actionA = { vmQuiz.finishQuizSession() },
            actionB = { vmQuiz.finishQuizSession() }
        )
        waitUntilMain { runBlocking(Dispatchers.IO) { db.mathDao().getAllQuizScores().first().size >= 1 } }
        assertEquals(1, runBlocking(Dispatchers.IO) { db.mathDao().getAllQuizScores().first().size })

        // 2) finishPracticeSessionNow
        runBlocking(Dispatchers.IO) { db.clearAllTables() }
        val vmPractice = MathViewModel(app, savedStateHandle = SavedStateHandle())
        vmPractice.startPracticeSession(PracticeCategory.BASIC_ALGEBRA, PracticeDifficulty.EASY, count = 3)
        runTwoThreadRace(
            actionA = { vmPractice.finishPracticeSessionNow() },
            actionB = { vmPractice.finishPracticeSessionNow() }
        )
        waitUntilMain { runBlocking(Dispatchers.IO) { db.mathDao().getAllPracticeHistory().first().size >= 1 } }
        assertEquals(1, runBlocking(Dispatchers.IO) { db.mathDao().getAllPracticeHistory().first().size })
        assertEquals(1, runBlocking(Dispatchers.IO) { db.mathDao().getAllQuizScores().first().size })

        // 3) finishMentalMathSession
        runBlocking(Dispatchers.IO) { db.clearAllTables() }
        val vmMental = MathViewModel(app, savedStateHandle = SavedStateHandle())
        vmMental.startMentalMathChallenge(
            MentalMathCategory.ADDITION,
            MentalMathDifficulty.EASY,
            MentalMathMode.FIXED_QUESTIONS,
            durationOrCount = 3
        )
        runTwoThreadRace(
            actionA = { vmMental.finishMentalMathSession() },
            actionB = { vmMental.finishMentalMathSession() }
        )
        waitUntilMain { runBlocking(Dispatchers.IO) { db.mathDao().getAllPracticeHistory().first().size >= 1 } }
        assertEquals(1, runBlocking(Dispatchers.IO) { db.mathDao().getAllPracticeHistory().first().size })
        assertEquals(1, runBlocking(Dispatchers.IO) { db.mathDao().getAllQuizScores().first().size })

        // 4) submitBookPracticeAnswer + finishBookPracticeSession
        runBlocking(Dispatchers.IO) { db.clearAllTables() }
        val vmBook = MathViewModel(app, savedStateHandle = SavedStateHandle())
        vmBook.startBookPractice(bookId = BookId.CLASS_6, chapterNumber = 1, mode = BookPracticeMode.STANDARD, questionCount = 2)
        vmBook.toggleBookPracticeOption(0)
        vmBook.setBookPracticeTextInput("1")
        runTwoThreadRace(
            actionA = { vmBook.submitBookPracticeAnswer() },
            actionB = { vmBook.submitBookPracticeAnswer() }
        )
        waitUntilMain { vmBook.bookPracticeSession.value.isAnswerSubmitted }
        assertEquals(1, vmBook.bookPracticeSession.value.answeredResults.size)

        runTwoThreadRace(
            actionA = { vmBook.finishBookPracticeSession() },
            actionB = { vmBook.finishBookPracticeSession() }
        )
        waitUntilMain { runBlocking(Dispatchers.IO) { db.mathDao().getAllPracticeHistory().first().size >= 1 } }
        assertEquals(1, runBlocking(Dispatchers.IO) { db.mathDao().getAllPracticeHistory().first().size })
        assertEquals(1, runBlocking(Dispatchers.IO) { db.mathDao().getAllQuizScores().first().size })
    }

    private fun runTwoThreadRace(actionA: () -> Unit, actionB: () -> Unit) {
        val ready = CountDownLatch(2)
        val go = CountDownLatch(1)
        val done = CountDownLatch(2)
        val t1 = Thread {
            ready.countDown()
            go.await(5, TimeUnit.SECONDS)
            actionA()
            done.countDown()
        }
        val t2 = Thread {
            ready.countDown()
            go.await(5, TimeUnit.SECONDS)
            actionB()
            done.countDown()
        }
        t1.start()
        t2.start()
        assertTrue(ready.await(5, TimeUnit.SECONDS))
        go.countDown()
        assertTrue(done.await(5, TimeUnit.SECONDS))
    }

    @Test
    fun solverCancellation_gen1CancelledGen2ReplacedByGen3_preservesNewerInFlightOwnershipAndCommitsOnlyGen3() {
        val viewModel = MathViewModel(app, savedStateHandle = SavedStateHandle())
        val workerExecutor = Executors.newFixedThreadPool(2)
        val originalDispatcher = viewModel.solverComputeDispatcher
        viewModel.solverComputeDispatcher = workerExecutor.asCoroutineDispatcher()

        val committedAnswers = Collections.synchronizedList(mutableListOf<String>())
        val calc1Started = CountDownLatch(1)
        val calc1Interrupted = CountDownLatch(1)
        val calc1FinallyExited = CountDownLatch(1)
        val calc2CanProceed = CountDownLatch(1)

        try {
            // Calculation #1 -> Generation 1
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = {
                    calc1Started.countDown()
                    try {
                        while (!Thread.currentThread().isInterrupted) {
                            Thread.sleep(5)
                        }
                        throw CancellationException("Interrupted loop")
                    } catch (ex: InterruptedException) {
                        calc1Interrupted.countDown()
                        throw CancellationException("Worker A interrupted", ex)
                    } catch (ex: CancellationException) {
                        calc1Interrupted.countDown()
                        throw ex
                    } finally {
                        calc1FinallyExited.countDown()
                    }
                },
                onResultCommitted = { committedAnswers.add("STALE_${it.finalAnswerBn}") }
            )

            assertTrue("Calc #1 must start on Worker Thread A", calc1Started.await(5, TimeUnit.SECONDS))
            assertEquals(1L, viewModel.currentSolverCalculationGenerationForTesting())
            assertEquals(1L, viewModel.currentActiveInFlightGenerationForTesting())
            assertTrue(viewModel.isSolverPersistenceInFlight.value)

            // Cancel -> Generation 2
            viewModel.cancelActiveSolverCalculation()
            assertEquals(2L, viewModel.currentSolverCalculationGenerationForTesting())
            assertEquals(0L, viewModel.currentActiveInFlightGenerationForTesting())
            assertFalse(viewModel.isSolverPersistenceInFlight.value)

            // Immediately start Calculation #2 -> Generation 3 on Worker Thread B
            val calc2Result = SolverResult(
                topic = SolverTopic.BASIC_ARITHMETIC,
                mode = SolverMode.LEARNING,
                originalProblemBn = "২ + ৩",
                steps = listOf(SolutionStep(1, "যোগফল", "২ + ৩ = ৫", "দুটি সংখ্যার যোগফল")),
                finalAnswerBn = "৫",
                formulaUsedBn = "যোগ",
                verificationBn = "সঠিক",
                isSuccess = true
            )
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = {
                    assertTrue("Calc #2 paused until Calc #1 exit verified", calc2CanProceed.await(5, TimeUnit.SECONDS))
                    calc2Result
                },
                onResultCommitted = { committedAnswers.add("GEN3_${it.finalAnswerBn}") }
            )

            assertEquals(3L, viewModel.currentSolverCalculationGenerationForTesting())
            assertEquals(3L, viewModel.currentActiveInFlightGenerationForTesting())
            assertTrue(viewModel.isSolverPersistenceInFlight.value)

            // Verify Worker Thread A exits with CancellationException while Gen 3 owns in-flight state.
            assertTrue("Worker A must receive interrupt via runInterruptible", calc1Interrupted.await(5, TimeUnit.SECONDS))
            assertTrue(calc1FinallyExited.await(5, TimeUnit.SECONDS))

            // Calc #1's finally must NOT reset activeInFlightGeneration (3) or isSolverPersistenceInFlight (true).
            assertEquals(3L, viewModel.currentActiveInFlightGenerationForTesting())
            assertTrue(viewModel.isSolverPersistenceInFlight.value)

            // Now let Calc #2 (Gen 3) finish and commit cleanly.
            calc2CanProceed.countDown()
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value == SolverPersistenceOutcome.SUCCESS
            }

            assertEquals(0L, viewModel.currentActiveInFlightGenerationForTesting())
            assertEquals(3L, viewModel.currentSolverCalculationGenerationForTesting())
            assertEquals(listOf("GEN3_৫"), committedAnswers.toList())

            val historyRows = runBlocking(Dispatchers.IO) { db.mathDao().getAllSolverHistory().first() }
            assertEquals(1, historyRows.size)
            assertEquals("৫", historyRows.first().finalAnswerBn)
        } finally {
            calc2CanProceed.countDown()
            viewModel.solverComputeDispatcher = originalDispatcher
            workerExecutor.shutdownNow()
        }
    }

    @Test
    fun multiThreadedDecimalFormat_16Threads200Iterations_zeroExceptionsDeterministicOutput() {
        val pool = Executors.newFixedThreadPool(16)
        val failure = AtomicReference<Throwable?>(null)
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(16)
        val expectedShared = SharedMathEngine.formatDoubleBn(1234.5678)
        val expectedSolver = MathProblemSolverEngine.solveArithmetic("২৫ × ৩ + ২.৫", SolverMode.LEARNING).finalAnswerBn

        try {
            repeat(16) {
                pool.submit {
                    try {
                        startLatch.await(5, TimeUnit.SECONDS)
                        repeat(200) { iter ->
                            val v = 1234.5678
                            val formatted = SharedMathEngine.formatDoubleBn(v)
                            if (formatted != expectedShared) {
                                error("Non-deterministic formatDoubleBn at iter=$iter: $formatted vs $expectedShared")
                            }
                            val res = MathProblemSolverEngine.solveArithmetic("২৫ × ৩ + ২.৫", SolverMode.LEARNING)
                            if (!res.isSuccess || res.finalAnswerBn != expectedSolver) {
                                error("Non-deterministic solver result at iter=$iter: ${res.finalAnswerBn}")
                            }
                        }
                    } catch (t: Throwable) {
                        failure.compareAndSet(null, t)
                    } finally {
                        doneLatch.countDown()
                    }
                }
            }
            startLatch.countDown()
            assertTrue(doneLatch.await(20, TimeUnit.SECONDS))
            val thrown = failure.get()
            assertTrue("Expected zero exceptions in 16-thread stress test, got: $thrown", thrown == null)
        } finally {
            pool.shutdownNow()
        }
    }
}
