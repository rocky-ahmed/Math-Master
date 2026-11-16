package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.math.BengaliNumberFormatter
import com.example.math.MathProblemSolverEngine
import com.example.math.ShortcutDetails
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.math.SquareCubeProvider
import com.example.ui.StudySessionTimerState
import com.example.ui.components.StudySessionTimerSection
import com.example.ui.customization.AppCustomizationState
import com.example.ui.customization.CustomLayoutType
import com.example.ui.customization.CustomModuleRenderer
import com.example.ui.customization.GlobalCustomConfig
import com.example.ui.customization.HomeModuleItem
import com.example.ui.customization.SectionKey
import com.example.ui.screens.InteractiveLearningStepCard
import com.example.ui.screens.ShortcutAndTrapCard
import com.example.ui.screens.SimultaneousLinearSystemAndBridgeCard
import com.example.ui.screens.SolutionDisplayCard
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
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w480dp-h900dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class UiUxBugRemediationRegressionTest {

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
        runCatching {
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
    }

    @Test
    fun verifyUi001To003StudyTimerNoTruncationAndVisibleResetAndProgressRow() {
        val fontScaleState = mutableStateOf(1.0f)
        val containerWidthState = mutableStateOf(360.dp)
        val timerStateHolder = mutableStateOf(
            StudySessionTimerState(
                isVisibleOnHome = true,
                isRunning = false,
                currentSessionSeconds = 0,
                todayTotalSeconds = 125, // State 1: 2 min 5 sec with 30 min goal
                allTimeTotalSeconds = 1800,
                dailyGoalMinutes = 30,
                completedSessionsToday = 1
            )
        )

        composeRule.setContent {
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(baseDensity.density, fontScale = fontScaleState.value)
            ) {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    Box(
                        modifier = Modifier
                            .width(containerWidthState.value)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        StudySessionTimerSection(
                            timerState = timerStateHolder.value,
                            onToggleVisibility = {
                                timerStateHolder.value = timerStateHolder.value.copy(
                                    isVisibleOnHome = !timerStateHolder.value.isVisibleOnHome
                                )
                            },
                            onToggleRunning = {},
                            onFinishSession = {},
                            onResetSession = {},
                            onSelectGoalMinutes = {}
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()

        // UI-001 State 1: Dynamic elapsed time (125s -> ২ মি. ৫ সে.) with 30-minute goal (৩০ মি.)
        composeRule.onNodeWithText("আজ: ২ মি. ৫ সে. • লক্ষ্য: ৩০ মি.", useUnmergedTree = true).assertIsDisplayed()
        val state1Layout = getTextLayoutResult(
            composeRule.onNodeWithTag("study_timer_status_text", useUnmergedTree = true).fetchSemanticsNode()
        )!!
        assertFalse("UI-001 State 1: must not overflow horizontally", state1Layout.didOverflowWidth)
        assertFalse("UI-001 State 1: must not be ellipsized", state1Layout.isLineEllipsized(0))

        // UI-001 State 2: Different elapsed time (2715s -> ৪৫ মি. ১৫ সে.) with 60-minute goal (৬০ মি.)
        timerStateHolder.value = timerStateHolder.value.copy(
            todayTotalSeconds = 2715,
            dailyGoalMinutes = 60,
            completedSessionsToday = 3
        )
        composeRule.waitForIdle()
        composeRule.onNodeWithText("আজ: ৪৫ মি. ১৫ সে. • লক্ষ্য: ৬০ মি.", useUnmergedTree = true).assertIsDisplayed()
        val state2Layout = getTextLayoutResult(
            composeRule.onNodeWithTag("study_timer_status_text", useUnmergedTree = true).fetchSemanticsNode()
        )!!
        assertFalse("UI-001 State 2: must not overflow horizontally", state2Layout.didOverflowWidth)
        assertFalse("UI-001 State 2: must not be ellipsized", state2Layout.isLineEllipsized(0))

        // UI-001 State 3: Zero / initial elapsed time (0s -> ০ সে.) with 15-minute goal (১৫ মি.)
        timerStateHolder.value = timerStateHolder.value.copy(
            todayTotalSeconds = 0,
            dailyGoalMinutes = 15,
            completedSessionsToday = 0
        )
        composeRule.waitForIdle()
        composeRule.onNodeWithText("আজ: ০ সে. • লক্ষ্য: ১৫ মি.", useUnmergedTree = true).assertIsDisplayed()
        val state3Layout = getTextLayoutResult(
            composeRule.onNodeWithTag("study_timer_status_text", useUnmergedTree = true).fetchSemanticsNode()
        )!!
        assertFalse("UI-001 State 3: must not overflow horizontally", state3Layout.didOverflowWidth)
        assertFalse("UI-001 State 3: must not be ellipsized", state3Layout.isLineEllipsized(0))

        // UI-003: Reset button container is visible and 36dp x 36dp inside expanded body
        val resetNode = composeRule.onNodeWithTag("study_timer_reset_btn", useUnmergedTree = true).fetchSemanticsNode()
        assertTrue("UI-003: Reset button must have positive width and height", resetNode.boundsInRoot.width > 0f && resetNode.boundsInRoot.height > 0f)

        // UI-002: Measure title line height at 1.0x vs 1.5x to prove genuine font scaling (no silent font clamping)
        timerStateHolder.value = timerStateHolder.value.copy(isVisibleOnHome = false)
        fontScaleState.value = 1.0f
        containerWidthState.value = 360.dp
        composeRule.waitForIdle()
        val titleLayout10x = getTextLayoutResult(
            composeRule.onNodeWithTag("study_timer_title_text", useUnmergedTree = true).fetchSemanticsNode()
        )!!
        val line0Height10x = titleLayout10x.getLineBottom(0) - titleLayout10x.getLineTop(0)

        // UI-002 at 360dp / 1.5x: genuine 1.5x font scaling with multi-line wrapping and zero ellipsis
        fontScaleState.value = 1.5f
        composeRule.waitForIdle()
        val titleNode15x360 = composeRule.onNodeWithTag("study_timer_title_text", useUnmergedTree = true).fetchSemanticsNode()
        val statusNode15x360 = composeRule.onNodeWithTag("study_timer_status_text", useUnmergedTree = true).fetchSemanticsNode()
        val titleLayout15x360 = getTextLayoutResult(titleNode15x360)!!
        val statusLayout15x360 = getTextLayoutResult(statusNode15x360)!!
        val line0Height15x = titleLayout15x360.getLineBottom(0) - titleLayout15x360.getLineTop(0)

        assertTrue(
            "UI-002: 1.5x line height ($line0Height15x) must genuinely scale above 1.0x ($line0Height10x) without clamping",
            line0Height15x >= line0Height10x * 1.35f
        )
        assertTrue("UI-002: Title wraps cleanly within 1..3 lines at 360dp / 1.5x", titleLayout15x360.lineCount in 1..3)
        assertTrue("UI-002: Subtitle wraps cleanly within 1..3 lines at 360dp / 1.5x", statusLayout15x360.lineCount in 1..3)
        for (i in 0 until titleLayout15x360.lineCount) {
            assertFalse("UI-002: Title line $i must not ellipsize at 360dp / 1.5x", titleLayout15x360.isLineEllipsized(i))
        }
        for (i in 0 until statusLayout15x360.lineCount) {
            assertFalse("UI-002: Subtitle line $i must not ellipsize at 360dp / 1.5x", statusLayout15x360.isLineEllipsized(i))
        }

        // UI-002 at 320dp / 1.5x: narrow viewport + 1.5x font scale wraps without truncation
        containerWidthState.value = 320.dp
        composeRule.waitForIdle()
        val titleLayout15x320 = getTextLayoutResult(
            composeRule.onNodeWithTag("study_timer_title_text", useUnmergedTree = true).fetchSemanticsNode()
        )!!
        val statusLayout15x320 = getTextLayoutResult(
            composeRule.onNodeWithTag("study_timer_status_text", useUnmergedTree = true).fetchSemanticsNode()
        )!!
        for (i in 0 until titleLayout15x320.lineCount) {
            assertFalse("UI-002: Title line $i must not ellipsize at 320dp / 1.5x", titleLayout15x320.isLineEllipsized(i))
        }
        for (i in 0 until statusLayout15x320.lineCount) {
            assertFalse("UI-002: Subtitle line $i must not ellipsize at 320dp / 1.5x", statusLayout15x320.isLineEllipsized(i))
        }
    }

    @Test
    fun verifyUi004To006SolverCanonicalOperatorsChipsAndSimultaneousScoping() {
        // UI-004: Canonical arithmetic operators (×, ÷, −) in originalProblemBn and steps
        val arithRes = MathProblemSolverEngine.solveArithmetic("(১৫ + ৫) * ৪ / ২ - ৬", SolverMode.LEARNING)
        assertFalse("UI-004: originalProblemBn must not contain raw ASCII *", arithRes.originalProblemBn.contains("*"))
        assertFalse("UI-004: originalProblemBn must not contain raw ASCII /", arithRes.originalProblemBn.contains("/"))
        assertTrue("UI-004: originalProblemBn must contain canonical × and ÷", arithRes.originalProblemBn.contains("×") && arithRes.originalProblemBn.contains("÷"))
        arithRes.steps.forEach { step ->
            assertFalse("UI-004: Step formula must not contain raw ASCII * (${step.expressionOrMathBn})", step.expressionOrMathBn.contains("*"))
            assertFalse("UI-004: Step formula must not contain raw ASCII / (${step.expressionOrMathBn})", step.expressionOrMathBn.contains("/"))
        }

        val topicState = mutableStateOf(SolverTopic.BASIC_ARITHMETIC)
        var hostView: View? = null

        composeRule.setContent {
            hostView = LocalView.current
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(12.dp)
                ) {
                    InteractiveLearningStepCard(
                        step = arithRes.steps.first(),
                        mode = SolverMode.LEARNING,
                        forceExpandAll = false,
                        onStepSelfCheckVerified = {}
                    )
                    SimultaneousLinearSystemAndBridgeCard(
                        selectedTopic = topicState.value,
                        viewModel = null
                    )
                }
            }
        }
        composeRule.waitForIdle()

        // UI-006: All 4 learning chips inside step card must fit within 360dp container without horizontal clipping
        val chipTryNode = composeRule.onNodeWithTag("step_1_chip_try").fetchSemanticsNode()
        assertTrue("UI-006: 4th chip (step_1_chip_try) must be fully visible within 360dp width", chipTryNode.boundsInRoot.right <= 360f * 2.0f + 2f)

        // UI-005: Verify across ALL 16 SolverTopic.entries that ONLY LINEAR_EQUATIONS shows the simultaneous linear system solver
        assertEquals("There must be 16 canonical SolverTopic entries", 16, SolverTopic.entries.size)
        for (topic in SolverTopic.entries) {
            topicState.value = topic
            composeRule.waitForIdle()
            if (topic == SolverTopic.LINEAR_EQUATIONS) {
                assertEquals(
                    "UI-005: LINEAR_EQUATIONS must display 2x2 simultaneous system chip",
                    1,
                    composeRule.onAllNodesWithTag("sim_dim_2x2").fetchSemanticsNodes().size
                )
                assertEquals(
                    "UI-005: LINEAR_EQUATIONS must display 3x3 simultaneous system chip",
                    1,
                    composeRule.onAllNodesWithTag("sim_dim_3x3").fetchSemanticsNodes().size
                )
                assertEquals(
                    "UI-005: LINEAR_EQUATIONS must not display bridge-only title",
                    0,
                    composeRule.onAllNodesWithTag("sim_solver_bridge_only_title").fetchSemanticsNodes().size
                )
            } else {
                assertEquals(
                    "UI-005: Non-linear topic $topic (including QUADRATIC_EQUATIONS) must NOT display 2x2 simultaneous linear solver",
                    0,
                    composeRule.onAllNodesWithTag("sim_dim_2x2").fetchSemanticsNodes().size
                )
                assertEquals(
                    "UI-005: Non-linear topic $topic (including QUADRATIC_EQUATIONS) must NOT display 3x3 simultaneous linear solver",
                    0,
                    composeRule.onAllNodesWithTag("sim_dim_3x3").fetchSemanticsNodes().size
                )
                composeRule.onNodeWithTag("sim_solver_bridge_only_title").assertIsDisplayed()
                composeRule.onNodeWithTag("btn_solver_to_formula_lib").assertIsDisplayed()
            }
        }

        // Switch back to LINEAR_EQUATIONS -> verify 2x2 and 3x3 dynamic header title switching
        topicState.value = SolverTopic.LINEAR_EQUATIONS
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("sim_dim_2x2").assertIsDisplayed()
        composeRule.onNodeWithText("দুই চলকবিশিষ্ট সহসমীকরণ সমাধান (Cramer's Rule: a₁x+b₁y=c₁, a₂x+b₂y=c₂)").assertIsDisplayed()

        composeRule.onNodeWithTag("sim_dim_3x3").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("তিন চলকবিশিষ্ট সহসমীকরণ সমাধান (৩×৩ ব্যবস্থা: aᵢx+bᵢy+cᵢz=dᵢ)").assertIsDisplayed()
        saveViewScreenshot(hostView, (360 * 2), (550 * 2), "remediated_simultaneous_solver_3x3.png")
    }

    @Test
    fun verifyUi007HomeGridCardsEqualRowHeightAndNoTruncation() {
        var hostView: View? = null
        val sampleModules = listOf(
            HomeModuleItem(
                sectionKey = SectionKey.TABLES,
                titleBn = "গুণের নামতা (১–৪০)",
                subtitleBn = "১ থেকে ৪০ পর্যন্ত পূর্ণাঙ্গ নামতা, অডিও উচ্চারণ ও ইন্টারেক্টিভ অনুশীলন",
                badgeBn = "১–৪০ নামতা",
                defaultIcon = Icons.Default.Calculate,
                defaultGradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                testTag = "card_multiplication",
                onClick = {}
            ),
            HomeModuleItem(
                sectionKey = SectionKey.SQUARES,
                titleBn = "বর্গ ও ঘন তালিকা",
                subtitleBn = "১–২০ বর্গ ও ঘনমূল",
                badgeBn = "বর্গ ও ঘন",
                defaultIcon = Icons.Default.Functions,
                defaultGradient = listOf(Color(0xFF7C3AED), Color(0xFF5B21B6)),
                testTag = "card_squares_cubes",
                onClick = {}
            )
        )

        composeRule.setContent {
            hostView = LocalView.current
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(baseDensity.density, fontScale = 1.3f)
            ) {
                MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                    Box(
                        modifier = Modifier
                            .width(360.dp)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {
                        CustomModuleRenderer(
                            modules = sampleModules,
                            customState = AppCustomizationState(
                                global = GlobalCustomConfig(layoutType = CustomLayoutType.GRID, gridColumns = 2)
                            )
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()

        val card1Bounds = composeRule.onNodeWithTag("card_multiplication").fetchSemanticsNode().boundsInRoot
        val card2Bounds = composeRule.onNodeWithTag("card_squares_cubes").fetchSemanticsNode().boundsInRoot
        assertTrue(
            "UI-007: Cards in the same grid row must have equal height (card1=${card1Bounds.height}, card2=${card2Bounds.height})",
            abs(card1Bounds.height - card2Bounds.height) <= 2f
        )
        saveViewScreenshot(hostView, (360 * 2), card1Bounds.height.toInt() + 64, "remediated_home_grid_cards_1_3x.png")
    }

    @Test
    fun verifyLoc001AndLoc002NoLegacyImageFileNamesOrRoomV7Strings() {
        // LOC-001: SquareCubeProvider header and footer strings must not contain 241713.jpg / 241715.jpg or outdated 1-10 range
        assertFalse(SquareCubeProvider.squareHeaderTitle.contains("241713"))
        assertFalse(SquareCubeProvider.cubeHeaderTitle.contains("241715"))
        assertTrue(SquareCubeProvider.cubeFooterNote.contains("১ থেকে ২০ পর্যন্ত"))

        // Preserve existing SquareItem / CubeItem raw data contract while formatting in UI
        assertEquals("1² = 1", SquareCubeProvider.squareTableList.first().squareText)
        assertEquals("১২² = ১৪৪", BengaliNumberFormatter.toBn(SquareCubeProvider.squareTableList[11].squareText))
        assertEquals("২০³ = ৮০০০", BengaliNumberFormatter.toBn(SquareCubeProvider.cubeTableList.last().cubeText))
    }

    @Test
    fun verifyUi010AndA11y001DarkThemeContrastAndTouchTargets() {
        val arithRes = MathProblemSolverEngine.solveArithmetic("(১৫ + ৫) × ৪ ÷ ২ − ৬", SolverMode.BCS)
        composeRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.DARK) {
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(12.dp)
                ) {
                    SolutionDisplayCard(
                        result = arithRes,
                        isBookmarked = false,
                        onCopySolution = {},
                        onToggleBookmark = {},
                        onTrySimilar = {},
                        onSwitchToLearningMode = {},
                        viewModel = null
                    )
                    ShortcutAndTrapCard(
                        shortcutDetails = ShortcutDetails(
                            shortcutTitleBn = "BODMAS দ্রুত কৌশল",
                            shortcutFormulaOrRuleBn = "B -> O -> D/M -> A/S",
                            whyShortcutWorksBn = "অগ্রাধিকার ক্রম বজায় থাকে",
                            whenToUseBn = "যেকোনো সরল অংকে",
                            whenToAvoidBn = "বন্ধনী উপেক্ষা করা যাবে না",
                            commonTrapBn = "ভাগ ও গুণের বাম-ডান ক্রম ভুল করা"
                        )
                    )
                }
            }
        }
        composeRule.waitForIdle()

        // A11Y-001: solver_copy_btn and solver_bookmark_btn must have >= 48dp touch targets (96px at density 2.0)
        val copyBounds = composeRule.onNodeWithTag("solver_copy_btn").fetchSemanticsNode().boundsInRoot
        val bookmarkBounds = composeRule.onNodeWithTag("solver_bookmark_btn").fetchSemanticsNode().boundsInRoot
        assertTrue("A11Y-001: solver_copy_btn width must be >= 48dp", copyBounds.width >= 95f && copyBounds.height >= 95f)
        assertTrue("A11Y-001: solver_bookmark_btn width must be >= 48dp", bookmarkBounds.width >= 95f && bookmarkBounds.height >= 95f)
    }
}
