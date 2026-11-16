package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.math.MathProblemSolverEngine
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.ui.screens.LearningModeSelector
import com.example.ui.screens.SimultaneousLinearSystemAndBridgeCard
import com.example.ui.screens.SolutionDisplayCard
import com.example.ui.screens.SolverTopicCategory
import com.example.ui.screens.SolverWorkspaceHeader
import com.example.ui.screens.TopicInputCard
import com.example.ui.screens.TopicSelectorCarousel
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathMasterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w480dp-h1200dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ProblemSolverModernUiRedesignTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun getTextLayoutResult(node: SemanticsNode): TextLayoutResult? {
        val action = if (node.config.contains(SemanticsActions.GetTextLayoutResult)) {
            node.config[SemanticsActions.GetTextLayoutResult]
        } else {
            null
        } ?: return null
        val results = mutableListOf<TextLayoutResult>()
        action.action?.invoke(results)
        return results.firstOrNull()
    }

    private fun saveViewScreenshot(rootView: View?, widthPx: Int, heightPx: Int, fileName: String) {
        if (rootView == null || rootView.width <= 0 || rootView.height <= 0) return
        val fullBmp = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(fullBmp)
        rootView.draw(canvas)
        val cropW = widthPx.coerceIn(1, fullBmp.width)
        val cropH = heightPx.coerceIn(1, fullBmp.height)
        val cropped = Bitmap.createBitmap(fullBmp, 0, 0, cropW, cropH)
        val outDir = File("build/test-screenshots").apply { mkdirs() }
        FileOutputStream(File(outDir, fileName)).use { fos ->
            cropped.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        cropped.recycle()
        fullBmp.recycle()
    }

    @Test
    fun verifyHeaderModeToggleCategoryFilteringResetAndValidation() {
        val selectedMode = mutableStateOf(SolverMode.LEARNING)
        val selectedCategory = mutableStateOf(SolverTopicCategory.ALL)
        val selectedTopic = mutableStateOf(SolverTopic.BASIC_ARITHMETIC)
        val arithmeticExpr = mutableStateOf("(২৫ + ১৫) × ২ - ৩০ ÷ ৩")

        composeRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SolverWorkspaceHeader()
                    LearningModeSelector(
                        currentMode = selectedMode.value,
                        onModeSelected = { selectedMode.value = it }
                    )
                    TopicSelectorCarousel(
                        currentTopic = selectedTopic.value,
                        currentCategory = selectedCategory.value,
                        onCategorySelected = { selectedCategory.value = it },
                        onTopicSelected = { selectedTopic.value = it }
                    )
                    TopicInputCard(
                        topic = selectedTopic.value,
                        arithmeticExpr = arithmeticExpr.value,
                        onArithmeticChange = { arithmeticExpr.value = it },
                        fractionNum1 = "৩",
                        fractionDen1 = "৪",
                        fractionOp = "+",
                        fractionNum2 = "২",
                        fractionDen2 = "৫",
                        onFractionChange = { _, _, _, _, _ -> },
                        percentageSubType = 0,
                        percentVal1 = "১৫",
                        percentVal2 = "৫০০",
                        onPercentageChange = { _, _, _ -> },
                        ratioSubType = 0,
                        ratioTotal = "১২০০",
                        ratioR1 = "৩",
                        ratioR2 = "৫",
                        ratioR3 = "",
                        onRatioChange = { _, _, _, _, _ -> },
                        profitLossSubType = 0,
                        plCp = "৫০০",
                        plSp = "৫৫০",
                        plPercent = "১০",
                        plMp = "৬০০",
                        onProfitLossChange = { _, _, _, _, _ -> },
                        averageInput = "১০, ২০, ৩০",
                        onAverageChange = {},
                        siP = "৫০০০",
                        siR = "১০",
                        siT = "৩",
                        siI = "",
                        onInterestChange = { _, _, _, _ -> },
                        workSubType = 0,
                        workTimeA = "১০",
                        workTimeB = "১৫",
                        workTimeC = "১২",
                        onWorkChange = { _, _, _, _ -> },
                        sdtSpeed = "",
                        sdtDistance = "১০০",
                        sdtTime = "২",
                        onSdtChange = { _, _, _ -> },
                        divNumber = "৭৫২১",
                        divDivisor = 3,
                        onDivChange = { _, _ -> },
                        hcfLcmInput = "২৪, ৩৬",
                        onHcfLcmChange = {},
                        sqCubeNumber = "১৪৪",
                        sqCubeOp = 1,
                        onSqCubeChange = { _, _ -> },
                        linearEqInput = "৩x + ১২ = ৪৫",
                        onLinearEqChange = {},
                        onResetClick = { arithmeticExpr.value = "" },
                        onSolveClick = {}
                    )
                }
            }
        }
        composeRule.waitForIdle()

        // 1. Workspace Header
        composeRule.onNodeWithTag("solver_workspace_header").assertIsDisplayed()
        composeRule.onNodeWithText("গণিত বুঝে সমাধান করুন").assertIsDisplayed()
        composeRule.onNodeWithText("ধাপে ধাপে শিখুন, অনুশীলন করুন এবং পরীক্ষার জন্য প্রস্তুত হোন।").assertIsDisplayed()

        // 2. Mode Segmented Control labels & subtitles
        composeRule.onNodeWithText("শেখার মোড").assertIsDisplayed()
        composeRule.onNodeWithText("ধাপে ধাপে বুঝুন").assertIsDisplayed()
        composeRule.onNodeWithText("দ্রুত সমাধান").assertIsDisplayed()
        composeRule.onNodeWithText("দ্রুত ফলাফল দেখুন").assertIsDisplayed()
        composeRule.onNodeWithText("BCS প্রস্তুতি").assertIsDisplayed()
        composeRule.onNodeWithText("শর্টকাট ও পরীক্ষার কৌশল").assertIsDisplayed()

        composeRule.onNodeWithTag("solver_mode_learning").assertIsSelected()
        composeRule.onNodeWithTag("solver_mode_quick").performClick()
        composeRule.waitForIdle()
        assertEquals(SolverMode.QUICK, selectedMode.value)
        composeRule.onNodeWithTag("solver_mode_quick").assertIsSelected()

        composeRule.onNodeWithTag("solver_mode_bcs").performClick()
        composeRule.waitForIdle()
        assertEquals(SolverMode.BCS, selectedMode.value)
        composeRule.onNodeWithTag("solver_mode_bcs").assertIsSelected()

        // 3. Topic category filtering across all 16 topics
        assertEquals(16, SolverTopicCategory.ALL.topics.size)
        assertEquals(16, SolverTopicCategory.entries.filter { it != SolverTopicCategory.ALL }.sumOf { it.topics.size })

        composeRule.onNodeWithTag("solver_category_algebra").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("solver_topic_linear_equations").assertIsDisplayed()
        assertEquals(0, composeRule.onAllNodesWithTag("solver_topic_basic_arithmetic").fetchSemanticsNodes().size)

        composeRule.onNodeWithTag("solver_category_all").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("solver_topic_basic_arithmetic").assertIsDisplayed()

        // 4. Reset action clears input & displays topic-aware validation banner
        assertEquals(0, composeRule.onAllNodesWithTag("solver_input_validation_banner").fetchSemanticsNodes().size)
        composeRule.onNodeWithTag("btn_reset_solver_input").performClick()
        composeRule.waitForIdle()
        assertEquals("", arithmeticExpr.value)
        composeRule.onNodeWithTag("solver_input_validation_banner").assertIsDisplayed()

        // 5. Unbalanced parenthesis validation
        arithmeticExpr.value = "(২৫ + ১৫"
        composeRule.waitForIdle()
        composeRule.onNodeWithText("বন্ধনী () সঠিকভাবে মেলেনি। দয়া করে পরীক্ষা করুন।").assertIsDisplayed()

        // 6. Verify >= 48dp touch targets on key controls (density 2.0 -> 95f px)
        val solveBounds = composeRule.onNodeWithTag("solve_problem_btn").fetchSemanticsNode().boundsInRoot
        val resetBounds = composeRule.onNodeWithTag("btn_reset_solver_input").fetchSemanticsNode().boundsInRoot
        val modeLearningBounds = composeRule.onNodeWithTag("solver_mode_learning").fetchSemanticsNode().boundsInRoot
        assertTrue("Solve button height must be >= 48dp", solveBounds.height >= 95f)
        assertTrue("Reset button height must be >= 48dp", resetBounds.height >= 95f)
        assertTrue("Mode segment height must be >= 48dp", modeLearningBounds.height >= 95f)
    }

    @Test
    fun verifyLearningModeIndependentChipsProgressiveHintsQuickAndBcsLayouts() {
        val activeMode = mutableStateOf(SolverMode.LEARNING)
        var hostView: View? = null

        composeRule.setContent {
            hostView = LocalView.current
            val result = MathProblemSolverEngine.solveArithmetic("(১৫ + ৫) × ৪ ÷ ২ - ৬", activeMode.value)
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.background)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SolutionDisplayCard(
                        result = result,
                        isBookmarked = false,
                        onToggleBookmark = {},
                        onCopySolution = {},
                        onTrySimilar = {},
                        onSwitchToLearningMode = { activeMode.value = SolverMode.LEARNING },
                        viewModel = null
                    )
                }
            }
        }
        composeRule.waitForIdle()

        // Learning Mode sections & compact defaults on Step 2
        composeRule.onNodeWithTag("solver_problem_understanding_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("solver_formula_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("solver_formula_breakdown_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("solver_inline_visual_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("solver_learner_feedback_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()

        // Step 1 chips independent multi-panel expansion
        composeRule.onNodeWithTag("step_1_chip_simple").performScrollTo().assertIsNotSelected().performClick()
        composeRule.onNodeWithTag("step_1_chip_analogy").performScrollTo().assertIsNotSelected().performClick()
        composeRule.onNodeWithTag("step_1_chip_try").performScrollTo().assertIsNotSelected().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("step_1_chip_simple").assertIsSelected()
        composeRule.onNodeWithTag("step_1_chip_why").assertIsSelected()
        composeRule.onNodeWithTag("step_1_chip_analogy").assertIsSelected()
        composeRule.onNodeWithTag("step_1_chip_try").assertIsSelected()

        // Progressive hints inside Step 1 Try-It workspace
        composeRule.onNodeWithTag("step_1_hint_1_btn").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("step_1_hint_2_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Step 2 started compact (why not selected)
        composeRule.onNodeWithTag("step_2_chip_why").performScrollTo().assertIsNotSelected()
        saveViewScreenshot(hostView, (360 * 2), (900 * 2), "solver_solution_learning_light.png")

        // Switch to QUICK mode: Hero Answer first + Quick-to-Learning button + compact summary steps
        activeMode.value = SolverMode.QUICK
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("solver_answer_banner", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("btn_quick_to_learning_mode").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("solver_step_card_1", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        assertEquals(0, composeRule.onAllNodesWithTag("step_1_chip_simple").fetchSemanticsNodes().size)
        saveViewScreenshot(hostView, (360 * 2), (760 * 2), "solver_solution_quick_light.png")

        // Click Quick-to-Learning bridge button
        composeRule.onNodeWithTag("btn_quick_to_learning_mode").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(SolverMode.LEARNING, activeMode.value)

        // Switch to BCS mode: shortcut, trap & syllabus cards
        activeMode.value = SolverMode.BCS
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("solver_bcs_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("solver_shortcut_details_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("solver_bcs_syllabus_card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        saveViewScreenshot(hostView, (360 * 2), (820 * 2), "solver_solution_bcs_light.png")
    }

    @Test
    fun verifyResponsiveWidthsFontScalesAndDarkThemeScreenshots() {
        val widthState = mutableStateOf(320.dp)
        val fontScaleState = mutableStateOf(1.5f)
        val themeState = mutableStateOf(AppThemeMode.LIGHT)
        val topicState = mutableStateOf(SolverTopic.SIMPLE_INTEREST)
        val interestMode = mutableStateOf(com.example.math.InterestCalculationMode.SIMPLE)
        val compoundingFreq = mutableIntStateOf(1)
        var hostView: View? = null

        composeRule.setContent {
            hostView = LocalView.current
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(baseDensity.density, fontScale = fontScaleState.value)
            ) {
                MathMasterTheme(themeMode = themeState.value) {
                    Column(
                        modifier = Modifier
                            .width(widthState.value)
                            .verticalScroll(rememberScrollState())
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SolverWorkspaceHeader()
                        LearningModeSelector(
                            currentMode = SolverMode.LEARNING,
                            onModeSelected = {}
                        )
                        TopicSelectorCarousel(
                            currentTopic = topicState.value,
                            currentCategory = SolverTopicCategory.ALL,
                            onCategorySelected = {},
                            onTopicSelected = { topicState.value = it }
                        )
                        TopicInputCard(
                            topic = topicState.value,
                            arithmeticExpr = "(২৫ + ১৫) × ২",
                            onArithmeticChange = {},
                            fractionNum1 = "৩",
                            fractionDen1 = "৪",
                            fractionOp = "+",
                            fractionNum2 = "২",
                            fractionDen2 = "৫",
                            onFractionChange = { _, _, _, _, _ -> },
                            percentageSubType = 0,
                            percentVal1 = "১৫",
                            percentVal2 = "৫০০",
                            onPercentageChange = { _, _, _ -> },
                            ratioSubType = 0,
                            ratioTotal = "১২০০",
                            ratioR1 = "৩",
                            ratioR2 = "৫",
                            ratioR3 = "",
                            onRatioChange = { _, _, _, _, _ -> },
                            profitLossSubType = 0,
                            plCp = "৫০০",
                            plSp = "৫৫০",
                            plPercent = "১০",
                            plMp = "৬০০",
                            onProfitLossChange = { _, _, _, _, _ -> },
                            averageInput = "১০, ২০, ৩০",
                            onAverageChange = {},
                            interestMode = interestMode.value,
                            compoundingFreq = compoundingFreq.intValue,
                            onInterestModeChange = { m, f ->
                                interestMode.value = m
                                compoundingFreq.intValue = f
                            },
                            siP = "৫০০০",
                            siR = "১০",
                            siT = "৩",
                            siI = "",
                            onInterestChange = { _, _, _, _ -> },
                            workSubType = 0,
                            workTimeA = "১০",
                            workTimeB = "১৫",
                            workTimeC = "১২",
                            onWorkChange = { _, _, _, _ -> },
                            sdtSpeed = "",
                            sdtDistance = "১০০",
                            sdtTime = "২",
                            onSdtChange = { _, _, _ -> },
                            divNumber = "৭৫২১",
                            divDivisor = 3,
                            onDivChange = { _, _ -> },
                            hcfLcmInput = "২৪, ৩৬",
                            onHcfLcmChange = {},
                            sqCubeNumber = "১৪৪",
                            sqCubeOp = 1,
                            onSqCubeChange = { _, _ -> },
                            linearEqInput = "৩x + ১২ = ৪৫",
                            onLinearEqChange = {},
                            onResetClick = {},
                            onSolveClick = {}
                        )
                        SimultaneousLinearSystemAndBridgeCard(
                            selectedTopic = SolverTopic.LINEAR_EQUATIONS,
                            viewModel = null
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()

        // 1. 320dp @ 1.5x Light: verify no horizontal overflow or line ellipsis on header & segmented buttons
        val headerTitleNode = composeRule.onNodeWithText("গণিত বুঝে সমাধান করুন").fetchSemanticsNode()
        val headerLayout = getTextLayoutResult(headerTitleNode)!!
        assertFalse("320dp @ 1.5x header must not overflow horizontally", headerLayout.didOverflowWidth)
        for (i in 0 until headerLayout.lineCount) {
            assertFalse("320dp @ 1.5x header line $i must not ellipsize", headerLayout.isLineEllipsized(i))
        }
        val modeLearningBounds320 = composeRule.onNodeWithTag("solver_mode_learning").fetchSemanticsNode().boundsInRoot
        val modeBcsBounds320 = composeRule.onNodeWithTag("solver_mode_bcs").fetchSemanticsNode().boundsInRoot
        assertTrue("Mode tabs fit cleanly inside 320dp width", modeBcsBounds320.right <= 320f * 2.0f + 2f)
        assertTrue("Mode tab height >= 48dp at 320dp @ 1.5x", modeLearningBounds320.height >= 95f)
        saveViewScreenshot(hostView, (320 * 2), (860 * 2), "solver_redesign_320dp_1_5x_light.png")

        // 2. 360dp @ 1.0x Light
        widthState.value = 360.dp
        fontScaleState.value = 1.0f
        themeState.value = AppThemeMode.LIGHT
        composeRule.waitForIdle()
        saveViewScreenshot(hostView, (360 * 2), (860 * 2), "solver_redesign_360dp_1_0x_light.png")

        // 3. 360dp @ 1.3x Dark + Compound Interest segmented switch
        widthState.value = 360.dp
        fontScaleState.value = 1.3f
        themeState.value = AppThemeMode.DARK
        composeRule.onNodeWithTag("interest_mode_compound").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("interest_mode_compound").assertIsSelected()
        saveViewScreenshot(hostView, (360 * 2), (960 * 2), "solver_redesign_360dp_1_3x_dark.png")

        // 4. 411dp @ 1.0x Light + 3x3 Simultaneous system segmented switch
        widthState.value = 411.dp
        fontScaleState.value = 1.0f
        themeState.value = AppThemeMode.LIGHT
        composeRule.onNodeWithTag("sim_dim_3x3").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("sim_dim_3x3").assertIsSelected()
        saveViewScreenshot(hostView, (411 * 2), (960 * 2), "solver_redesign_411dp_1_0x_light.png")
    }
}
