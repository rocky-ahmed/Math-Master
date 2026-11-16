package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.repository.MathRepository
import com.example.ui.MathViewModel
import com.example.ui.screens.ProblemSolverScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathMasterTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Focused regression suite verifying per-step Learning Mode UI state persistence
 * across LEARNING -> QUICK -> BCS -> LEARNING mode round-trips, clean reset on
 * clearing or solving a different problem, and global Expand All / Collapse All behavior.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class ProblemSolverLearningStatePersistenceTest {

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

    @Test
    fun allFourPanelsHintsAndSelfCheckAttempt_persistAcrossLearningQuickBcsLearningRoundTrip() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Solve explicit expression once so history count baseline = 1
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১২ + ৮) × ৩")
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            runBlocking(Dispatchers.IO) {
                var retries = 0
                while (repository.allSolverHistory.first().isEmpty() && retries < 20) {
                    Thread.sleep(25)
                    retries++
                }
            }
            assertEquals(1, runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size })

            // Customize all 4 panels on Step 1 + Step 2 simple panel
            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsNotSelected().performClick()
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsSelected().performClick() // explicitly turn OFF default
            composeTestRule.onNodeWithTag("step_1_chip_analogy").performScrollTo().assertIsNotSelected().performClick()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsNotSelected().performClick()
            composeTestRule.waitForIdle()

            // Reveal Hint 1 and Hint 2 inside Step 1 Try-It workspace
            composeTestRule.onNodeWithTag("step_1_hint_1_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_1_hint_2_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            assertTrue(composeTestRule.onAllNodesWithText("ইঙ্গিত ১:", substring = true).fetchSemanticsNodes().isNotEmpty())
            assertTrue(composeTestRule.onAllNodesWithText("ইঙ্গিত ২:", substring = true).fetchSemanticsNodes().isNotEmpty())

            // Enter self-check attempt and verify
            composeTestRule.onNodeWithText("নিজে ধাপটির মান লিখুন").performScrollTo().performTextReplacement("২০")
            composeTestRule.onNodeWithText("যাচাই").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            assertTrue(
                composeTestRule.onAllNodesWithText("সঠিক! আপনি এই ধাপটি নিজে সমাধান করেছেন।", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            )

            // Also expand Step 2's simple panel
            composeTestRule.onNodeWithTag("step_2_chip_simple").performScrollTo().assertIsNotSelected().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_2_chip_simple").assertIsSelected()

            // Mode Round-Trip: LEARNING -> QUICK -> BCS -> LEARNING
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_quick").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_quick").assertIsSelected()

            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_bcs").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_bcs").assertIsSelected()

            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_learning").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_learning").assertIsSelected()

            // Verify zero duplicate Solver History entries created across mode round-trips
            assertEquals(1, runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().size })

            // Verify ALL four Step 1 detail panels, hints 1 & 2, user attempt, feedback, and Step 2 state persisted!
            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_1_chip_analogy").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_2_chip_simple").performScrollTo().assertIsSelected()

            assertTrue(composeTestRule.onAllNodesWithText("ইঙ্গিত ১:", substring = true).fetchSemanticsNodes().isNotEmpty())
            assertTrue(composeTestRule.onAllNodesWithText("ইঙ্গিত ২:", substring = true).fetchSemanticsNodes().isNotEmpty())
            composeTestRule.onNodeWithText("নিজে ধাপটির মান লিখুন").performScrollTo().assertTextContains("২০")
            assertTrue(
                composeTestRule.onAllNodesWithText("সঠিক! আপনি এই ধাপটি নিজে সমাধান করেছেন।", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            )
        } finally {
            store.clear()
        }
    }

    @Test
    fun solvingDifferentProblem_resetsOldExpansionHintsAndSelfCheckState() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Problem A: expand panels, reveal hints, type self-check attempt
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১২ + ৮) × ৩")
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_chip_analogy").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_hint_1_btn").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_hint_2_btn").performScrollTo().performClick()
            composeTestRule.onNodeWithText("নিজে ধাপটির মান লিখুন").performScrollTo().performTextReplacement("২০")
            composeTestRule.waitForIdle()

            // Switch to genuinely different Problem B and solve
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১৫ + ৫) × ৪")
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            // Problem B must start fresh without inheriting Problem A's expansions or hints
            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_analogy").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsNotSelected()
            composeTestRule.onAllNodesWithText("ইঙ্গিত ১:", substring = true).assertCountEquals(0)
            composeTestRule.onAllNodesWithText("ইঙ্গিত ২:", substring = true).assertCountEquals(0)
        } finally {
            store.clear()
        }
    }

    @Test
    fun clearingActiveProblem_resetsRelatedLocalLearningState() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Solve Problem A and customize Step 1 state
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১২ + ৮) × ৩")
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_hint_1_btn").performScrollTo().performClick()
            composeTestRule.onNodeWithText("নিজে ধাপটির মান লিখুন").performScrollTo().performTextReplacement("২০")
            composeTestRule.waitForIdle()

            // Clear active problem via Reset button
            scrollToSection(3)
            composeTestRule.onNodeWithTag("btn_reset_solver_input").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_input_validation_banner").performScrollTo().assertIsDisplayed()

            // Re-enter and solve the exact same problem -> must open clean, not carrying over pre-reset hints/panels
            composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১২ + ৮) × ৩")
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsNotSelected()
            composeTestRule.onAllNodesWithText("ইঙ্গিত ১:", substring = true).assertCountEquals(0)
        } finally {
            store.clear()
        }
    }

    @Test
    fun globalExpandAllAndCollapseAll_workCorrectlyAndPersistAcrossModeSwitch() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Global Expand All
            scrollToSection(4)
            composeTestRule.onNodeWithTag("btn_expand_all_steps").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_analogy").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_2_chip_simple").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_2_chip_analogy").performScrollTo().assertIsSelected()

            // Also manually open Step 1 Try-It and turn off Step 1 simple, then click Expand All again
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("step_1_chip_simple").assertIsNotSelected()

            composeTestRule.onNodeWithTag("btn_expand_all_steps").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsSelected()

            // Switch LEARNING -> QUICK -> BCS -> LEARNING: all expanded states remain
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_quick").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_bcs").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_learning").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsSelected()

            // Global Collapse All -> closes all four chips across steps & survives mode round-trip
            composeTestRule.onNodeWithTag("btn_collapse_all_steps").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_1_chip_analogy").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsNotSelected()

            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_quick").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_learning").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsNotSelected()
        } finally {
            store.clear()
        }
    }
}
