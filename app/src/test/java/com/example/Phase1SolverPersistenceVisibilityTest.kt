package com.example

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.repository.MathRepository
import com.example.math.MathProblemSolverEngine
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.SolverPersistenceEvent
import com.example.ui.SolverPersistenceOutcome
import com.example.ui.screens.ProblemSolverScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathMasterTheme
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class Phase1SolverPersistenceVisibilityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var repository: MathRepository

    private class TestLifecycleOwner(initialState: Lifecycle.State = Lifecycle.State.RESUMED) : LifecycleOwner {
        private val registry = LifecycleRegistry(this).apply {
            currentState = initialState
        }

        override val lifecycle: Lifecycle
            get() = registry

        fun moveTo(state: Lifecycle.State) {
            if (registry.currentState != Lifecycle.State.DESTROYED) {
                registry.currentState = state
            }
        }
    }

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        db = MathDatabase.getInstance(app)
        repository = MathRepository(db.mathDao())
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

    private fun createViewModel(): Pair<ViewModelStore, MathViewModel> {
        val store = ViewModelStore()
        val vm = ViewModelProvider(
            store,
            ViewModelProvider.AndroidViewModelFactory(app)
        )[MathViewModel::class.java]
        return store to vm
    }

    private fun waitUntilMain(maxIterations: Int = 100, condition: () -> Boolean) {
        var iter = 0
        while (!condition() && iter < maxIterations) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (condition()) break
            Thread.sleep(20)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            iter++
        }
    }

    private fun sampleValidResult() =
        MathProblemSolverEngine.solveArithmetic("(২৫ + ১৫) × ২ - ৩০ ÷ ৩", SolverMode.LEARNING)

    // -----------------------------------------------------------------
    // A. SUCCESS & LOCAL CACHE FAILURE ISOLATION
    // -----------------------------------------------------------------

    @Test
    fun persistenceSuccess_insertsHistoryOnceUpdatesAnalyticsOnceAndEmitsNoFailureEvent() {
        val (store, viewModel) = createViewModel()
        val collectedEvents = mutableListOf<SolverPersistenceEvent>()
        val collectorJob = runBlocking {
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main.immediate).launch {
                viewModel.solverPersistenceEvents.collect { collectedEvents.add(it) }
            }
        }
        try {
            val result = sampleValidResult()
            assertTrue(result.isSuccess)

            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { result }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value != null
            }

            assertEquals(SolverPersistenceOutcome.SUCCESS, viewModel.lastSolverPersistenceOutcome.value)
            assertTrue("Success must not emit failure notification events", collectedEvents.isEmpty())

            runBlocking(Dispatchers.IO) {
                val historyRows = repository.allSolverHistory.first()
                assertEquals(1, historyRows.size)
                val todayActivity = repository.allDailyStudyActivity.first()
                assertEquals(1, todayActivity.size)
                assertEquals(1, todayActivity.first().questionsAttempted)
                assertEquals(1, todayActivity.first().practiceSessionsCompleted)
            }
        } finally {
            collectorJob.cancel()
            store.clear()
        }
    }

    @Test
    fun localCacheUpdateFailure_remainsIsolatedFromRoomPersistenceOutcomeAndIsSuccess() {
        val (store, viewModel) = createViewModel()
        val collectedEvents = mutableListOf<SolverPersistenceEvent>()
        val collectorJob = runBlocking {
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main.immediate).launch {
                viewModel.solverPersistenceEvents.collect { collectedEvents.add(it) }
            }
        }
        try {
            viewModel.solverLocalCacheUpdateDelegateForTesting = {
                throw IllegalStateException("Simulated SharedPreferences local cache failure")
            }

            val result = sampleValidResult()
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { result }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value != null
            }

            assertEquals(SolverPersistenceOutcome.SUCCESS, viewModel.lastSolverPersistenceOutcome.value)
            assertTrue("Local cache failure must not emit Room failure notification", collectedEvents.isEmpty())

            runBlocking(Dispatchers.IO) {
                assertEquals(1, repository.allSolverHistory.first().size)
                assertEquals(1, repository.allDailyStudyActivity.first().size)
            }
        } finally {
            collectorJob.cancel()
            viewModel.solverLocalCacheUpdateDelegateForTesting = null
            store.clear()
        }
    }

    // -----------------------------------------------------------------
    // B. HISTORY FAILURE
    // -----------------------------------------------------------------

    @Test
    fun historyPersistenceFailure_emitsHistorySaveFailedSkipsAnalyticsAndReleasesGuard() {
        val (store, viewModel) = createViewModel()
        val collectedEvents = mutableListOf<SolverPersistenceEvent>()
        val collectorJob = runBlocking {
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main.immediate).launch {
                viewModel.solverPersistenceEvents.collect { collectedEvents.add(it) }
            }
        }
        val analyticsCalls = AtomicInteger(0)
        try {
            viewModel.solverHistoryPersistDelegateForTesting = {
                throw IllegalStateException("SQLite disk full")
            }
            viewModel.solverAnalyticsPersistDelegateForTesting = {
                analyticsCalls.incrementAndGet()
            }

            val validResult = sampleValidResult()
            var returnedResult: com.example.math.SolverResult? = null
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = {
                    returnedResult = validResult
                    validResult
                }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    collectedEvents.isNotEmpty()
            }

            assertEquals(SolverPersistenceOutcome.HISTORY_SAVE_FAILED, viewModel.lastSolverPersistenceOutcome.value)
            assertEquals(1, collectedEvents.size)
            assertEquals(SolverPersistenceOutcome.HISTORY_SAVE_FAILED, collectedEvents.first().outcome)
            assertEquals("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।", collectedEvents.first().outcome.userMessageBn)
            assertEquals(0, analyticsCalls.get())
            assertTrue("Valid SolverResult must remain intact on History failure", returnedResult?.isSuccess == true)
            assertFalse("In-flight guard must release on History failure", viewModel.isSolverPersistenceInFlight.value)
            assertNotEquals(SolverPersistenceOutcome.SUCCESS, viewModel.lastSolverPersistenceOutcome.value)

            runBlocking(Dispatchers.IO) {
                assertEquals(0, repository.allSolverHistory.first().size)
                assertEquals(0, repository.allDailyStudyActivity.first().size)
            }
        } finally {
            collectorJob.cancel()
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.solverAnalyticsPersistDelegateForTesting = null
            store.clear()
        }
    }

    // -----------------------------------------------------------------
    // C. ANALYTICS FAILURE & PARTIAL SUCCESS PROTECTION
    // -----------------------------------------------------------------

    @Test
    fun analyticsUpdateFailure_preservesCommittedHistoryRowWithoutDuplicateAndEmitsAccurateFeedback() {
        val (store, viewModel) = createViewModel()
        val collectedEvents = mutableListOf<SolverPersistenceEvent>()
        val collectorJob = runBlocking {
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main.immediate).launch {
                viewModel.solverPersistenceEvents.collect { collectedEvents.add(it) }
            }
        }
        try {
            viewModel.solverAnalyticsPersistDelegateForTesting = {
                throw IllegalStateException("Simulated Room analytics update error")
            }

            val validResult = sampleValidResult()
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { validResult }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    collectedEvents.isNotEmpty()
            }

            assertEquals(
                SolverPersistenceOutcome.HISTORY_SAVED_ANALYTICS_UPDATE_FAILED,
                viewModel.lastSolverPersistenceOutcome.value
            )
            assertEquals(1, collectedEvents.size)
            assertEquals(
                "সমাধান ও ইতিহাস সংরক্ষিত হয়েছে, কিন্তু দৈনিক অগ্রগতি আপডেট করা যায়নি।",
                collectedEvents.first().outcome.userMessageBn
            )
            assertFalse(viewModel.isSolverPersistenceInFlight.value)

            // Committed History row preserved without duplicate retry row
            runBlocking(Dispatchers.IO) {
                assertEquals(1, repository.allSolverHistory.first().size)
                assertEquals(0, repository.allDailyStudyActivity.first().size)
            }

            // A subsequent deliberate Solve after recovery is allowed to create a new second row
            viewModel.solverAnalyticsPersistDelegateForTesting = null
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { validResult }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value == SolverPersistenceOutcome.SUCCESS
            }

            runBlocking(Dispatchers.IO) {
                assertEquals(2, repository.allSolverHistory.first().size)
            }
        } finally {
            collectorJob.cancel()
            viewModel.solverAnalyticsPersistDelegateForTesting = null
            store.clear()
        }
    }

    // -----------------------------------------------------------------
    // D. CONCURRENCY & SUBSEQUENT IDENTICAL SOLVE
    // -----------------------------------------------------------------

    @Test
    fun concurrencyAndIdenticalSubsequentSolve_blocksReentrancyAndAllowsPostCompletionInsert() {
        val (store, viewModel) = createViewModel()
        val gate1 = CompletableDeferred<Unit>()
        val persistCalls = AtomicInteger(0)
        try {
            viewModel.solverHistoryPersistDelegateForTesting = { entity ->
                val callNum = persistCalls.incrementAndGet()
                if (callNum == 1) {
                    gate1.await()
                }
                repository.saveSolvedProblemHistory(entity)
            }

            val validResult = sampleValidResult()
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { validResult }
            )
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertTrue(viewModel.isSolverPersistenceInFlight.value)

            // Concurrent duplicate solve rejected while first is pending
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { validResult }
            )
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertEquals(1, persistCalls.get())

            // Release first operation
            gate1.complete(Unit)
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size >= 1 }
            }
            assertFalse(viewModel.isSolverPersistenceInFlight.value)
            assertEquals(SolverPersistenceOutcome.SUCCESS, viewModel.lastSolverPersistenceOutcome.value)

            // Subsequent deliberate solve with identical inputs creates a new History row
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { validResult }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size >= 2 }
            }
            assertEquals(2, persistCalls.get())
            assertEquals(2, runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size })
        } finally {
            viewModel.solverHistoryPersistDelegateForTesting = null
            store.clear()
        }
    }

    // -----------------------------------------------------------------
    // E. CANCELLATION
    // -----------------------------------------------------------------

    @Test
    fun cancellationException_isRethrownWithoutFailureOutcomeAndReleasesInFlightGuard() {
        val (store, viewModel) = createViewModel()
        val collectedEvents = mutableListOf<SolverPersistenceEvent>()
        val collectorJob = runBlocking {
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main.immediate).launch {
                viewModel.solverPersistenceEvents.collect { collectedEvents.add(it) }
            }
        }
        try {
            val validResult = sampleValidResult()

            // Direct Stage 1 CancellationException must rethrow, not convert to HISTORY_SAVE_FAILED
            viewModel.solverHistoryPersistDelegateForTesting = {
                throw CancellationException("Stage 1 cancelled")
            }
            var stage1Rethrown = false
            runBlocking {
                try {
                    viewModel.persistSolverResultToHistory(
                        topic = SolverTopic.BASIC_ARITHMETIC,
                        inputs = mapOf("problem" to validResult.originalProblemBn),
                        result = validResult
                    )
                } catch (_: CancellationException) {
                    stage1Rethrown = true
                }
            }
            assertTrue("Stage 1 CancellationException must be rethrown", stage1Rethrown)

            // Direct Stage 2 CancellationException must rethrow, not convert to HISTORY_SAVED_ANALYTICS_UPDATE_FAILED
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.solverAnalyticsPersistDelegateForTesting = {
                throw CancellationException("Stage 2 cancelled")
            }
            var stage2Rethrown = false
            runBlocking {
                try {
                    viewModel.persistSolverResultToHistory(
                        topic = SolverTopic.BASIC_ARITHMETIC,
                        inputs = mapOf("problem" to validResult.originalProblemBn),
                        result = validResult
                    )
                } catch (_: CancellationException) {
                    stage2Rethrown = true
                }
            }
            assertTrue("Stage 2 CancellationException must be rethrown", stage2Rethrown)

            // ViewModel submit path: CancellationException must NOT emit failure notification and MUST release guard
            viewModel.solverAnalyticsPersistDelegateForTesting = null
            viewModel.solverHistoryPersistDelegateForTesting = {
                throw CancellationException("ViewModel persistence cancelled")
            }
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { validResult }
            )
            waitUntilMain { !viewModel.isSolverPersistenceInFlight.value }

            assertFalse("In-flight guard must release on CancellationException", viewModel.isSolverPersistenceInFlight.value)
            assertNull("Cancellation must not record a failure outcome", viewModel.lastSolverPersistenceOutcome.value)
            assertTrue("Cancellation must not emit a Snackbar failure event", collectedEvents.isEmpty())
        } finally {
            collectorJob.cancel()
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.solverAnalyticsPersistDelegateForTesting = null
            store.clear()
        }
    }

    // -----------------------------------------------------------------
    // F. UI & LIFECYCLE SESSION VERIFICATION
    // -----------------------------------------------------------------

    @Test
    fun uiVerification_startedFailuresShowBengaliSnackbarKeepSolutionAndSuccessRemainsSilent() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // 1. Normal successful solve -> solution visible, NO warning/error snackbar
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(3)
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value == SolverPersistenceOutcome.SUCCESS
            }
            composeTestRule.waitForIdle()
            assertEquals(0, composeTestRule.onAllNodesWithTag("solver_persistence_snackbar").fetchSemanticsNodes().size)
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            // 2. History failure while STARTED -> Bengali Snackbar shown AND solution remains visible & interactive
            viewModel.solverHistoryPersistDelegateForTesting = {
                throw IllegalStateException("Injected History write failure")
            }
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(3)
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").assertIsDisplayed()
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("solver_copy_btn", useUnmergedTree = true).assertIsEnabled()
            composeTestRule.onNodeWithTag("solver_bookmark_btn", useUnmergedTree = true).assertIsEnabled()

            // 3. Analytics failure after History succeeds -> accurate Bengali Snackbar shown, History preserved
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.solverAnalyticsPersistDelegateForTesting = {
                throw IllegalStateException("Injected analytics update failure")
            }
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(3)
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    composeTestRule.onAllNodesWithText("সমাধান ও ইতিহাস সংরক্ষিত হয়েছে, কিন্তু দৈনিক অগ্রগতি আপডেট করা যায়নি।").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("সমাধান ও ইতিহাস সংরক্ষিত হয়েছে, কিন্তু দৈনিক অগ্রগতি আপডেট করা যায়নি।").assertIsDisplayed()
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            runBlocking(Dispatchers.IO) {
                assertEquals(2, repository.allSolverHistory.first().size)
            }
        } finally {
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.solverAnalyticsPersistDelegateForTesting = null
            store.clear()
        }
    }

    @Test
    fun uiLifecycle_failureWhileStoppedDeliveredOnceOnStartAndRecompositionDoesNotReplay() {
        val (store, viewModel) = createViewModel()
        val testLifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        var recompositionTrigger by mutableIntStateOf(0)
        try {
            composeTestRule.setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides testLifecycleOwner) {
                    MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                        Column {
                            Text(
                                text = "recomp_$recompositionTrigger",
                                modifier = Modifier.testTag("recomp_marker")
                            )
                            ProblemSolverScreen(
                                viewModel = viewModel,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            composeTestRule.waitForIdle()

            // Temporarily move lifecycle to STOPPED (CREATED) without navigating away
            composeTestRule.runOnUiThread {
                testLifecycleOwner.moveTo(Lifecycle.State.CREATED)
            }
            composeTestRule.waitForIdle()

            viewModel.solverHistoryPersistDelegateForTesting = {
                throw IllegalStateException("Stopped-state DB write failure")
            }
            val result = sampleValidResult()
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { result }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value == SolverPersistenceOutcome.HISTORY_SAVE_FAILED
            }

            // While STOPPED, Snackbar must not yet be shown
            assertEquals(
                0,
                composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().size
            )

            // Return lifecycle to STARTED / RESUMED -> buffered notification delivered once
            composeTestRule.runOnUiThread {
                testLifecycleOwner.moveTo(Lifecycle.State.RESUMED)
            }
            composeTestRule.waitUntil(timeoutMillis = 5_000L) {
                composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeTestRule.onNodeWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").assertIsDisplayed()

            composeTestRule.mainClock.autoAdvance = false
            try {
                // Force recomposition while Snackbar is visible -> must remain displayed once (no duplicate/reset)
                composeTestRule.runOnUiThread {
                    recompositionTrigger++
                    androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
                }
                composeTestRule.mainClock.advanceTimeBy(100L)
                Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
                composeTestRule.onNodeWithTag("recomp_marker").assertTextEquals("recomp_1")
                assertEquals(
                    1,
                    composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().size
                )

                // Advance clock so Snackbar auto-dismisses, then force recomposition -> must NOT replay
                composeTestRule.mainClock.advanceTimeBy(6_000L)
                Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
                assertEquals(
                    0,
                    composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().size
                )

                composeTestRule.runOnUiThread {
                    recompositionTrigger++
                    androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
                }
                composeTestRule.mainClock.advanceTimeBy(100L)
                Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
                composeTestRule.onNodeWithTag("recomp_marker").assertTextEquals("recomp_2")
                assertEquals(
                    0,
                    composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().size
                )
            } finally {
                composeTestRule.mainClock.autoAdvance = true
            }
            composeTestRule.waitForIdle()
            assertEquals(
                0,
                composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().size
            )
        } finally {
            composeTestRule.mainClock.autoAdvance = true
            composeTestRule.runOnUiThread {
                testLifecycleOwner.moveTo(Lifecycle.State.DESTROYED)
            }
            viewModel.solverHistoryPersistDelegateForTesting = null
            store.clear()
        }
    }

    @Test
    fun uiLifecycle_configurationRecreationDeliversPendingOnceAndNeverReplaysConsumedNotification() {
        val (store, viewModel) = createViewModel()
        val lifecycle1 = TestLifecycleOwner(Lifecycle.State.RESUMED)
        val lifecycle2 = TestLifecycleOwner(Lifecycle.State.CREATED)
        val lifecycle3 = TestLifecycleOwner(Lifecycle.State.RESUMED)
        var activeHostPhase by mutableIntStateOf(1)

        try {
            composeTestRule.setContent {
                val activeOwner = when (activeHostPhase) {
                    1 -> lifecycle1
                    2 -> lifecycle2
                    else -> lifecycle3
                }
                CompositionLocalProvider(LocalLifecycleOwner provides activeOwner) {
                    MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                        // Keying via if-branches simulates full UI disposal & recreation while retaining ViewModel
                        when (activeHostPhase) {
                            1 -> ProblemSolverScreen(viewModel = viewModel)
                            2 -> ProblemSolverScreen(viewModel = viewModel)
                            else -> ProblemSolverScreen(viewModel = viewModel)
                        }
                    }
                }
            }
            composeTestRule.waitForIdle()

            // Simulate STOPPED during configuration recreation while persistence fails
            composeTestRule.runOnUiThread {
                lifecycle1.moveTo(Lifecycle.State.CREATED)
            }
            viewModel.solverHistoryPersistDelegateForTesting = {
                throw IllegalStateException("Failure during configuration recreation")
            }
            val result = sampleValidResult()
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { result }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value == SolverPersistenceOutcome.HISTORY_SAVE_FAILED
            }

            // Recreate UI tree with retained ViewModel (phase 2 starts CREATED then becomes RESUMED)
            composeTestRule.runOnUiThread {
                activeHostPhase = 2
                lifecycle1.moveTo(Lifecycle.State.DESTROYED)
            }
            composeTestRule.waitForIdle()
            composeTestRule.runOnUiThread {
                lifecycle2.moveTo(Lifecycle.State.RESUMED)
            }
            composeTestRule.waitUntil(timeoutMillis = 5_000L) {
                composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeTestRule.onNodeWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").assertIsDisplayed()

            // Dismiss via clock, then recreate UI tree a second time -> consumed notification must NOT replay
            composeTestRule.mainClock.autoAdvance = false
            try {
                composeTestRule.mainClock.advanceTimeBy(6_000L)
            } finally {
                composeTestRule.mainClock.autoAdvance = true
            }
            composeTestRule.waitForIdle()
            composeTestRule.runOnUiThread {
                activeHostPhase = 3
                lifecycle2.moveTo(Lifecycle.State.DESTROYED)
            }
            composeTestRule.waitForIdle()
            assertEquals(
                0,
                composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().size
            )
        } finally {
            composeTestRule.mainClock.autoAdvance = true
            composeTestRule.runOnUiThread {
                lifecycle1.moveTo(Lifecycle.State.DESTROYED)
                lifecycle2.moveTo(Lifecycle.State.DESTROYED)
                lifecycle3.moveTo(Lifecycle.State.DESTROYED)
            }
            viewModel.solverHistoryPersistDelegateForTesting = null
            store.clear()
        }
    }

    @Test
    fun uiLifecycle_navigatingAwayBeforeFailureOrWithPendingEventPreventsStaleSnackbarOnReopen() {
        val (store, viewModel) = createViewModel()
        val gate = CompletableDeferred<Unit>()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    MainAppContent(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // 1. Open Problem Solver session A -> submit persistence that stays pending
            viewModel.navigateTo(Screen.ProblemSolver)
            composeTestRule.waitForIdle()

            viewModel.solverHistoryPersistDelegateForTesting = {
                gate.await()
                throw IllegalStateException("Late failure after navigation away")
            }
            val result = sampleValidResult()
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { result }
            )
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertTrue(viewModel.isSolverPersistenceInFlight.value)

            // 2. Navigate away permanently BEFORE persistence completes
            assertTrue(viewModel.navigateBack())
            composeTestRule.waitForIdle()
            assertEquals(Screen.Home, viewModel.currentScreen)

            // 3. Persistence fails after leaving Solver screen -> outcome recorded, NO event for new session
            gate.complete(Unit)
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    viewModel.lastSolverPersistenceOutcome.value == SolverPersistenceOutcome.HISTORY_SAVE_FAILED
            }

            // 4. Reopen Problem Solver (new session) -> stale notification must NOT display
            viewModel.navigateTo(Screen.ProblemSolver)
            composeTestRule.waitForIdle()
            composeTestRule.mainClock.autoAdvance = false
            try {
                composeTestRule.mainClock.advanceTimeBy(500L)
            } finally {
                composeTestRule.mainClock.autoAdvance = true
            }
            composeTestRule.waitForIdle()
            assertEquals(
                0,
                composeTestRule.onAllNodesWithText("সমাধান হয়েছে, কিন্তু ইতিহাস সংরক্ষণ করা যায়নি।").fetchSemanticsNodes().size
            )

            // 5. Verify current-session notification STILL works normally after reopening
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.solverAnalyticsPersistDelegateForTesting = {
                throw IllegalStateException("Current session analytics failure")
            }
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(3)
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    composeTestRule.onAllNodesWithText("সমাধান ও ইতিহাস সংরক্ষিত হয়েছে, কিন্তু দৈনিক অগ্রগতি আপডেট করা যায়নি।").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithText("সমাধান ও ইতিহাস সংরক্ষিত হয়েছে, কিন্তু দৈনিক অগ্রগতি আপডেট করা যায়নি।").assertIsDisplayed()

            // 6. Also verify reopening after leaving with an unconsumed event does not replay the old event
            assertTrue(viewModel.navigateBack())
            composeTestRule.waitForIdle()
            viewModel.navigateTo(Screen.ProblemSolver)
            composeTestRule.waitForIdle()
            assertEquals(
                0,
                composeTestRule.onAllNodesWithText("সমাধান ও ইতিহাস সংরক্ষিত হয়েছে, কিন্তু দৈনিক অগ্রগতি আপডেট করা যায়নি।").fetchSemanticsNodes().size
            )
        } finally {
            if (!gate.isCompleted) gate.complete(Unit)
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.solverAnalyticsPersistDelegateForTesting = null
            store.clear()
        }
    }
}
