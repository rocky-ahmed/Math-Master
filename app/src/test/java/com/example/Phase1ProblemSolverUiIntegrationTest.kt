package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BookLessonProgressEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.repository.MathRepository
import com.example.math.MathProblemSolverEngine
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.ui.MathViewModel
import com.example.ui.screens.ProblemSolverScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathMasterTheme
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class Phase1ProblemSolverUiIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var repository: MathRepository

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

    private fun scrollToSection(idx: Int) {
        composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(idx)
        composeTestRule.waitForIdle()
    }

    private fun selectTopic(topicTag: String) {
        scrollToSection(2)
        composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag(topicTag))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(topicTag).performClick()
        composeTestRule.waitForIdle()
    }

    private fun waitUntilMain(maxIterations: Int = 80, condition: () -> Boolean) {
        var iter = 0
        while (!condition() && iter < maxIterations) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (condition()) break
            Thread.sleep(20)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            iter++
        }
    }

    @Test
    fun staleAnswerClearedAndSolveDisabledOnInvalidInput_acrossAllThreeModes() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Initial valid problem displays answer banner and enabled Solve button
            scrollToSection(3)
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().assertIsEnabled()
            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            // Test across LEARNING, QUICK, and BCS
            val modeTags = listOf("solver_mode_learning", "solver_mode_quick", "solver_mode_bcs")
            for (modeTag in modeTags) {
                scrollToSection(1)
                composeTestRule.onNodeWithTag(modeTag).performScrollTo().performClick()
                composeTestRule.waitForIdle()

                // Valid solve first
                scrollToSection(3)
                composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১২ + ৮) × ৩")
                composeTestRule.waitForIdle()
                // Editing cleared stale solution immediately
                assertEquals(0, composeTestRule.onAllNodesWithTag("solver_answer_banner", useUnmergedTree = true).fetchSemanticsNodes().size)
                composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().assertIsEnabled().performClick()
                composeTestRule.waitForIdle()

                scrollToSection(4)
                composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

                // Enter invalid unbalanced expression -> clears stale answer, shows banner, disables Solve button
                scrollToSection(3)
                composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১২ + ৮")
                composeTestRule.waitForIdle()

                assertEquals(0, composeTestRule.onAllNodesWithTag("solver_answer_banner", useUnmergedTree = true).fetchSemanticsNodes().size)
                composeTestRule.onNodeWithTag("solver_input_validation_banner").performScrollTo().assertIsDisplayed()
                composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().assertIsNotEnabled()
            }
        } finally {
            store.clear()
        }
    }

    @Test
    fun subtypeChangeClearsStaleSolutionAndFourSimpleInterestFieldsRejected() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Switch to SIMPLE_INTEREST topic (under COMMERCIAL category)
            scrollToSection(2)
            composeTestRule.onNodeWithTag("solver_category_commercial").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            selectTopic("solver_topic_simple_interest")

            // Initial Simple Interest (3 fields) solves and renders answer banner
            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            // Supplying the 4th field I simultaneously must clear stale answer, show validation banner, and disable Solve
            scrollToSection(3)
            composeTestRule.onNodeWithTag("si_input_i").performScrollTo().performTextReplacement("1500")
            composeTestRule.waitForIdle()

            assertEquals(0, composeTestRule.onAllNodesWithTag("solver_answer_banner", useUnmergedTree = true).fetchSemanticsNodes().size)
            composeTestRule.onNodeWithTag("solver_input_validation_banner").performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().assertIsNotEnabled()

            // Clear 4th field -> Solve re-enables and solves cleanly
            composeTestRule.onNodeWithTag("si_input_i").performScrollTo().performTextReplacement("")
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().assertIsEnabled().performClick()
            composeTestRule.waitForIdle()
            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            // Switching to Compound mode clears stale Simple Interest answer
            scrollToSection(3)
            composeTestRule.onNodeWithTag("interest_mode_compound").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            assertEquals(0, composeTestRule.onAllNodesWithTag("solver_answer_banner", useUnmergedTree = true).fetchSemanticsNodes().size)
        } finally {
            store.clear()
        }
    }

    @Test
    fun asyncHistoryPersistence_testsAToE_completableDeferredInFlightGuard() {
        val (store, viewModel) = createViewModel()
        try {
            val gate1 = CompletableDeferred<Unit>()
            val gate2 = CompletableDeferred<Unit>()
            val persistCalls = AtomicInteger(0)

            viewModel.solverHistoryPersistDelegateForTesting = { entity ->
                val callNum = persistCalls.incrementAndGet()
                if (callNum == 1) {
                    gate1.await()
                } else {
                    gate2.await()
                }
                repository.saveSolvedProblemHistory(entity)
            }

            val sampleResult = MathProblemSolverEngine.solveArithmetic("১২ + ৮", SolverMode.QUICK)
            assertTrue(sampleResult.isSuccess)

            // Launch first Solve + persist (suspends on gate1)
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { sampleResult }
            )
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

            // Test B: In-flight flag MUST remain true while persistence is suspended
            assertTrue("isSolverPersistenceInFlight must be true while pending", viewModel.isSolverPersistenceInFlight.value)

            // Test A: Concurrent/re-entrant second Solve callback while suspended MUST be rejected
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { sampleResult }
            )
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertEquals("Concurrent callback must not invoke second persist", 1, persistCalls.get())

            // Complete first persistence and drain main + IO
            gate1.complete(Unit)
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size >= 1 }
            }

            // Test C: Guard releases after persistence completes; 1 row written
            assertFalse("isSolverPersistenceInFlight must be false after completion", viewModel.isSolverPersistenceInFlight.value)
            assertEquals(1, runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size })

            // Test E: Deliberate subsequent Solve with IDENTICAL inputs after completion creates a second History entry
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { sampleResult }
            )
            gate2.complete(Unit)
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size >= 2 }
            }
            assertFalse(viewModel.isSolverPersistenceInFlight.value)
            assertEquals(2, runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size })

            // Test D: Persistence failure MUST still release guard via finally
            viewModel.solverHistoryPersistDelegateForTesting = {
                throw IllegalStateException("Simulated DB write failure")
            }
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { sampleResult }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value
            }
            assertFalse("Guard must release in finally when persistence fails", viewModel.isSolverPersistenceInFlight.value)

            // Subsequent valid save after failure still works (2 + 1 = 3 rows)
            viewModel.solverHistoryPersistDelegateForTesting = null
            viewModel.submitSolverCalculationAndPersist(
                topic = SolverTopic.BASIC_ARITHMETIC,
                solveAction = { sampleResult }
            )
            waitUntilMain {
                !viewModel.isSolverPersistenceInFlight.value &&
                    runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size >= 3 }
            }
            assertEquals(3, runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size })
        } finally {
            viewModel.solverHistoryPersistDelegateForTesting = null
            store.clear()
        }
    }

    @Test
    fun resetClearsOnlyActiveTopicInputsAndSolution_preservingHistoryBookmarksProgressAndMode() {
        runBlocking(Dispatchers.IO) {
            db.mathDao().insertSolverHistory(
                SolverHistoryEntity(
                    topicId = SolverTopic.BASIC_ARITHMETIC.name,
                    topicTitleBn = SolverTopic.BASIC_ARITHMETIC.titleBn,
                    categoryTitleBn = SolverTopic.BASIC_ARITHMETIC.titleEn,
                    inputSummaryBn = "problem = 5 + 5",
                    inputsJson = "{}",
                    finalAnswerBn = "১০",
                    stepsJson = "[]",
                    formulaUsedBn = "",
                    verificationBn = ""
                )
            )
            db.mathDao().insertBookmark(
                BookmarkEntity(
                    id = "keep_bookmark_1",
                    category = "SOLVER",
                    titleBn = "সংরক্ষিত বুকমার্ক",
                    subtitleBn = "৫ + ৫",
                    detailBn = "১০"
                )
            )
            db.mathDao().upsertProgress(ProgressEntity(itemId = "table_7", isLearned = true))
            db.mathDao().upsertBookLessonProgress(
                BookLessonProgressEntity(
                    lessonKey = "class6:1:lesson_1",
                    bookId = "class6",
                    chapterNumber = 1,
                    lessonId = "lesson_1",
                    isCompleted = true,
                    miniCheckPassed = true,
                    timeSpentSeconds = 120
                )
            )
        }

        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Switch to QUICK mode and edit Ratio topic input first (non-active topic data to preserve)
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_quick").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            selectTopic("solver_topic_ratio_proportion")
            scrollToSection(3)
            composeTestRule.onNodeWithTag("ratio_input_total").performScrollTo().performTextReplacement("9876")
            composeTestRule.waitForIdle()

            // Switch back to BASIC_ARITHMETIC, verify active solution displayed, then click Reset
            selectTopic("solver_topic_basic_arithmetic")
            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            scrollToSection(3)
            composeTestRule.onNodeWithTag("btn_reset_solver_input").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            // Active topic solution cleared and validation banner shown
            assertEquals(0, composeTestRule.onAllNodesWithTag("solver_answer_banner", useUnmergedTree = true).fetchSemanticsNodes().size)
            composeTestRule.onNodeWithTag("solver_input_validation_banner").performScrollTo().assertIsDisplayed()

            // History, Bookmarks, Progress, BookLessonProgress untouched in DB
            runBlocking(Dispatchers.IO) {
                assertEquals(1, db.mathDao().getAllSolverHistory().first().size)
                assertEquals(1, db.mathDao().getAllBookmarks().first().size)
                assertEquals(1, db.mathDao().getAllProgress().first().size)
                assertEquals(1, db.mathDao().getAllBookLessonProgress().first().size)
            }

            // Other topic's edited value (ratioTotal = "9876") & QUICK mode preserved in UI
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_quick").performScrollTo().assertIsSelected()

            selectTopic("solver_topic_ratio_proportion")
            scrollToSection(3)
            composeTestRule.onNodeWithTag("ratio_input_total").performScrollTo().assertTextContains("9876")
        } finally {
            store.clear()
        }
    }
}
