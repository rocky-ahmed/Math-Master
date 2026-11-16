package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.repository.MathRepository
import com.example.math.SolverTopic
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.screens.ProblemSolverScreen
import com.example.ui.screens.SolverTopicCategory
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
 * Post-Redesign Functional Acceptance Regression Suite for Math Problem Solver.
 * Covers:
 * 1. Mode-switching state safety across LEARNING / QUICK / BCS (no duplicate history, preserved input/topic/result/expansion, post-reset switch safety, saved history restore across topics).
 * 2. Exhaustive 16-topic category mapping under ALL and category filters + cross-topic input isolation.
 * 3. Reset / Clear backward compatibility (clears only active topic inputs, preserves subtypes & other topics).
 * 4. Absence of unverified "২০–৩০ সেকেন্ড" BCS time badge + preservation of valid shortcuts/traps/syllabus.
 * 5. Step-level & global expansion controls, BCS companion module routing, and 2x2/3x3 simultaneous system scoping.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class ProblemSolverPostRedesignAcceptanceTest {

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

    /** Scroll main LazyColumn ('solver_content_list') so recycled items at index [idx] are composed. */
    private fun scrollToSection(idx: Int) {
        composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(idx)
        composeTestRule.waitForIdle()
    }

    @Test
    fun section1_modeSwitchingPreservesStateExpansionAndCreatesNoDuplicateHistory() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Initial auto-solve must NOT insert Room history
            val initialHistory = runBlocking(Dispatchers.IO) { repository.allSolverHistory.first() }
            assertEquals("Initial auto-solve must not pollute Room history", 0, initialHistory.size)

            // Modify user input and explicitly solve once
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(১২ + ৮) × ৩")
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solve_problem_btn").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            // Wait for coroutine Room insert and verify exactly 1 history row exists
            runBlocking(Dispatchers.IO) {
                var retries = 0
                while (repository.allSolverHistory.first().isEmpty() && retries < 20) {
                    Thread.sleep(25)
                    retries++
                }
            }
            val afterSolveHistory = runBlocking(Dispatchers.IO) { repository.allSolverHistory.first() }
            assertEquals("Explicit Solve click must save 1 history entry", 1, afterSolveHistory.size)
            assertEquals("৬০", afterSolveHistory.first().finalAnswerBn)

            // Expand all steps in LEARNING mode
            scrollToSection(4)
            composeTestRule.onNodeWithTag("btn_expand_all_steps").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsSelected()

            // Switch LEARNING -> QUICK
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_quick").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_quick").assertIsSelected()
            scrollToSection(2)
            composeTestRule.onNodeWithTag("solver_topic_basic_arithmetic").assertIsSelected()
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").assertTextContains("(১২ + ৮) × ৩")
            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("btn_quick_to_learning_mode").performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithText("৬০", useUnmergedTree = true).assertIsDisplayed()

            // Switch QUICK -> BCS
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_bcs").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_bcs").assertIsSelected()
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").assertTextContains("(১২ + ৮) × ৩")
            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_bcs_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("solver_shortcut_details_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("solver_bcs_syllabus_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            // Switch BCS -> LEARNING: expansion preference and input/result remain intact
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_learning").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_mode_learning").assertIsSelected()
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").assertTextContains("(১২ + ৮) × ৩")
            scrollToSection(4)
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsSelected()

            // History count MUST still be 1 across all mode switches
            val afterSwitchesHistory = runBlocking(Dispatchers.IO) { repository.allSolverHistory.first() }
            assertEquals("Mode switching must never create duplicate Solver History entries", 1, afterSwitchesHistory.size)

            // Reset input -> clears active result and switching mode does NOT unexpectedly show an error card
            scrollToSection(3)
            composeTestRule.onNodeWithTag("btn_reset_solver_input").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_input_validation_banner").performScrollTo().assertIsDisplayed()
            assertEquals(0, composeTestRule.onAllNodesWithTag("solver_error_card").fetchSemanticsNodes().size)

            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_quick").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            assertEquals(
                "Switching mode when result is cleared must not produce an error card",
                0,
                composeTestRule.onAllNodesWithTag("solver_error_card").fetchSemanticsNodes().size
            )
        } finally {
            store.clear()
        }
    }

    @Test
    fun section1b_restoringHistoryItemFromDifferentTopicPreservesSavedSolution() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Solve custom arithmetic expression "(১২ + ৮) × ৩" -> ৬০ and save to history
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
            val savedId = runBlocking(Dispatchers.IO) { repository.allSolverHistory.first().first().id }

            // Switch to HCF_LCM topic under NUMBER_THEORY category so category fallback is also tested
            scrollToSection(2)
            composeTestRule.onNodeWithTag("solver_category_number_theory").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag("solver_topic_hcf_lcm"))
            composeTestRule.onNodeWithTag("solver_topic_hcf_lcm").performClick()
            composeTestRule.waitForIdle()

            // Restore the BASIC_ARITHMETIC history item while currently on HCF_LCM
            composeTestRule.onNodeWithTag("solver_content_list").performScrollToNode(hasTestTag("solver_history_card"))
            composeTestRule.onNodeWithTag("solver_history_item_$savedId").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            // Restored topic & saved answer ("৬০") must remain displayed without being overwritten by auto-solve
            scrollToSection(2)
            composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag("solver_topic_basic_arithmetic"))
            composeTestRule.onNodeWithTag("solver_topic_basic_arithmetic").assertIsSelected()
            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithText("৬০", useUnmergedTree = true).assertIsDisplayed()
        } finally {
            store.clear()
        }
    }

    @Test
    fun section2And3_exhaustiveTopicCategoryMappingAndResetCompatibility() {
        // Verify domain completeness across all 16 canonical SolverTopic entries
        assertEquals(16, SolverTopic.entries.size)
        assertEquals(16, SolverTopicCategory.ALL.topics.size)
        assertEquals(SolverTopic.entries.toSet(), SolverTopicCategory.ALL.topics.toSet())

        val partitioned = SolverTopicCategory.entries
            .filter { it != SolverTopicCategory.ALL }
            .flatMap { it.topics }
        assertEquals("Every topic must belong to exactly one non-ALL category", 16, partitioned.size)
        assertEquals("Zero duplicate topics allowed across categories", 16, partitioned.toSet().size)
        assertEquals(SolverTopic.entries.toSet(), partitioned.toSet())

        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            scrollToSection(2)
            // Verify every category renders its exact assigned topics and hides unassigned topics
            for (category in SolverTopicCategory.entries) {
                composeTestRule.onNodeWithTag("solver_category_${category.id}").performScrollTo().performClick()
                composeTestRule.waitForIdle()

                for (topic in category.topics) {
                    composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag("solver_topic_${topic.id}"))
                    composeTestRule.onNodeWithTag("solver_topic_${topic.id}").assertIsDisplayed()
                }
                if (category != SolverTopicCategory.ALL) {
                    val excluded = SolverTopic.entries.filter { it !in category.topics }
                    for (other in excluded) {
                        assertEquals(
                            "Topic ${other.id} must not appear under category ${category.id}",
                            0,
                            composeTestRule.onAllNodesWithTag("solver_topic_${other.id}").fetchSemanticsNodes().size
                        )
                    }
                }
            }

            // Input isolation & Reset backward compatibility:
            // 1. Edit BASIC_ARITHMETIC input
            composeTestRule.onNodeWithTag("solver_category_all").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag("solver_topic_basic_arithmetic"))
            composeTestRule.onNodeWithTag("solver_topic_basic_arithmetic").performClick()
            composeTestRule.waitForIdle()
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").performScrollTo().performTextReplacement("(৯ + ১) × ৫")
            composeTestRule.waitForIdle()

            // 2. Switch category to ALGEBRA without destroying BASIC_ARITHMETIC input, then choose QUADRATIC_EQUATIONS
            scrollToSection(2)
            composeTestRule.onNodeWithTag("solver_category_algebra").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag("solver_topic_quadratic_equations"))
            composeTestRule.onNodeWithTag("solver_topic_quadratic_equations").performClick()
            composeTestRule.waitForIdle()

            // Select subtype inequality (<) and modify quadA
            scrollToSection(3)
            composeTestRule.onNodeWithText("ax²+bx+c < 0").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("quad_input_a").performScrollTo().performTextReplacement("২")
            composeTestRule.waitForIdle()

            // 3. Click Reset on QUADRATIC_EQUATIONS
            composeTestRule.onNodeWithTag("btn_reset_solver_input").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            // Quadratic numeric fields cleared, subtype preserved, validation guidance shown
            composeTestRule.onNodeWithTag("quad_input_a").assertTextContains("")
            composeTestRule.onNodeWithText("ax²+bx+c < 0").assertIsSelected()
            composeTestRule.onNodeWithTag("solver_input_validation_banner").performScrollTo().assertIsDisplayed()

            // 4. Return to BASIC_ARITHMETIC -> its input is completely untouched
            scrollToSection(2)
            composeTestRule.onNodeWithTag("solver_category_all").performScrollTo().performClick()
            composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag("solver_topic_basic_arithmetic"))
            composeTestRule.onNodeWithTag("solver_topic_basic_arithmetic").performClick()
            composeTestRule.waitForIdle()
            scrollToSection(3)
            composeTestRule.onNodeWithTag("arithmetic_input_field").assertTextContains("(৯ + ১) × ৫")
        } finally {
            store.clear()
        }
    }

    @Test
    fun section4And5_stepControlsBcsBadgeAbsenceSyllabusNavigationAndSimultaneousScoping() {
        val (store, viewModel) = createViewModel()
        try {
            composeTestRule.setContent {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    ProblemSolverScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // 1. Global Expand All & Collapse All in LEARNING mode
            scrollToSection(4)
            composeTestRule.onNodeWithTag("btn_expand_all_steps").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsSelected()
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsSelected()

            composeTestRule.onNodeWithTag("btn_collapse_all_steps").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_1_chip_why").performScrollTo().assertIsNotSelected()
            composeTestRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsNotSelected()

            // 2. Step-level chevron toggle + collapsing Try-It panel when chevron is collapsed
            composeTestRule.onNodeWithTag("step_1_chip_try").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_1_chip_try").assertIsSelected()
            composeTestRule.onAllNodesWithContentDescription("ধাপের বিস্তারিত সংক্ষিপ্ত করুন")[0].performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("step_1_chip_try").assertIsNotSelected()

            // 3. Simultaneous 2x2 / 3x3 controls must NOT appear under BASIC_ARITHMETIC
            scrollToSection(5)
            assertEquals(0, composeTestRule.onAllNodesWithTag("sim_dim_2x2").fetchSemanticsNodes().size)
            assertEquals(0, composeTestRule.onAllNodesWithTag("sim_dim_3x3").fetchSemanticsNodes().size)

            // 4. Switch to BCS Mode: assert old fixed time estimate badge is completely removed
            scrollToSection(1)
            composeTestRule.onNodeWithTag("solver_mode_bcs").performScrollTo().performClick()
            composeTestRule.waitForIdle()

            scrollToSection(4)
            composeTestRule.onNodeWithTag("solver_bcs_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("solver_shortcut_details_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("solver_bcs_syllabus_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

            composeTestRule.onAllNodesWithText("২০–৩০ সেকেন্ড", substring = true).assertCountEquals(0)
            composeTestRule.onAllNodesWithText("৩০–৪৫ সেকেন্ড", substring = true).assertCountEquals(0)
            composeTestRule.onAllNodesWithText("সেকেন্ডে সমাধান", substring = true).assertCountEquals(0)

            // Expand Shortcut & Trap card explanation and verify conditions + traps preserved
            composeTestRule.onNodeWithText("কেন কাজ করে?").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onAllNodesWithText("কখন ব্যবহার করবেন:", substring = true).apply {
                assertTrue("Shortcut applicability must be visible", fetchSemanticsNodes().isNotEmpty())
            }
            composeTestRule.onAllNodesWithText("পরীক্ষার সাধারণ ফাঁদ:", substring = true).apply {
                assertTrue("Common exam trap must be visible", fetchSemanticsNodes().isNotEmpty())
            }

            // Expand BCS Syllabus Companion Navigator & verify real Screen navigation routing
            composeTestRule.onNodeWithTag("btn_toggle_bcs_companion_navigator").performScrollTo().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onAllNodesWithText("ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার ও ফর্মুলা লাইব্রেরি")[0].performScrollTo().performClick()
            composeTestRule.waitForIdle()
            assertEquals(
                "Companion module 'visual_explorer' must route to Screen.VisualExplorer (not Quizzes fallback)",
                Screen.VisualExplorer,
                viewModel.currentScreen
            )

            viewModel.navigateBack()
            composeTestRule.waitForIdle()
            composeTestRule.onAllNodesWithText("ফর্মুলা লাইব্রেরি (পরিমিতি বিভাগ)")[0].performScrollTo().performClick()
            composeTestRule.waitForIdle()
            assertEquals(
                "Companion module 'formula_library' must route to Screen.FormulaLibrary",
                Screen.FormulaLibrary,
                viewModel.currentScreen
            )

            // Return to Solver & switch to LINEAR_EQUATIONS -> 2x2 / 3x3 controls appear exclusively there
            viewModel.navigateBack()
            composeTestRule.waitForIdle()
            scrollToSection(2)
            composeTestRule.onNodeWithTag("solver_topics_row").performScrollToNode(hasTestTag("solver_topic_linear_equations"))
            composeTestRule.onNodeWithTag("solver_topic_linear_equations").performClick()
            composeTestRule.waitForIdle()
            scrollToSection(5)
            composeTestRule.onNodeWithTag("sim_dim_2x2").performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithTag("sim_dim_3x3").performScrollTo().assertIsDisplayed()
        } finally {
            store.clear()
        }
    }
}
