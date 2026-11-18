package com.example

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.ui.MathViewModel
import com.example.ui.components.BanglaNumericKeypad
import com.example.ui.components.LearnedStatusBadge
import com.example.ui.customization.ColorUtils
import com.example.ui.customization.HomeModuleItem
import com.example.ui.customization.MathModuleGridCard
import com.example.ui.customization.SectionCustomConfig
import com.example.ui.customization.SectionKey
import com.example.ui.screens.CustomizeScreen
import com.example.ui.screens.PracticeTutorScreen
import com.example.ui.screens.ProblemSolverScreen
import com.example.ui.theme.AmberOnTertiary
import com.example.ui.theme.AmberOnTertiaryContainer
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.AmberTertiaryContainer
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkOnBackground
import com.example.ui.theme.DarkOnPrimary
import com.example.ui.theme.DarkOnPrimaryContainer
import com.example.ui.theme.DarkOnSecondary
import com.example.ui.theme.DarkOnSecondaryContainer
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.DarkOnSurfaceVariant
import com.example.ui.theme.DarkOnTertiary
import com.example.ui.theme.DarkOnTertiaryContainer
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkPrimary
import com.example.ui.theme.DarkPrimaryContainer
import com.example.ui.theme.DarkSecondary
import com.example.ui.theme.DarkSecondaryContainer
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTertiary
import com.example.ui.theme.DarkTertiaryContainer
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightOnBackground
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutline
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceVariant
import com.example.ui.theme.MathMasterTheme
import com.example.ui.theme.SlateOnPrimary
import com.example.ui.theme.SlateOnPrimaryContainer
import com.example.ui.theme.SlatePrimary
import com.example.ui.theme.SlatePrimaryContainer
import com.example.ui.theme.TealOnSecondary
import com.example.ui.theme.TealOnSecondaryContainer
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.TealSecondaryContainer
import kotlinx.coroutines.Dispatchers
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
class Phase2AccessibilityAndContrastTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            MathDatabase.getInstance(app).clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun wcagReferenceRatios_matchMathematicalLinearization() {
        assertEquals(
            10.36,
            ColorUtils.calculateContrastRatio(Color.White, Color(0xFF1E3A8A), Color(0xFF1E3A8A)),
            0.05
        )
        assertEquals(
            8.49,
            ColorUtils.calculateContrastRatio(Color(0xFF1E3A8A), Color(0xFFDBEAFE), Color(0xFFDBEAFE)),
            0.05
        )
        assertEquals(
            5.47,
            ColorUtils.calculateContrastRatio(Color.White, Color(0xFF0F766E), Color(0xFF0F766E)),
            0.05
        )
        assertEquals(
            6.73,
            ColorUtils.calculateContrastRatio(Color(0xFF115E59), Color(0xFFCCFBF1), Color(0xFFCCFBF1)),
            0.05
        )
        assertEquals(
            4.54,
            ColorUtils.calculateContrastRatio(Color(0xFF767676), Color.White, Color.White),
            0.05
        )
        assertEquals(
            4.48,
            ColorUtils.calculateContrastRatio(Color(0xFF777777), Color.White, Color.White),
            0.05
        )
    }

    @Test
    fun defaultLightAndDarkThemePairsAndRemediatedOutlinesMeetWcagThresholds() {
        val lightTextPairs = listOf(
            SlateOnPrimary to SlatePrimary,
            SlateOnPrimaryContainer to SlatePrimaryContainer,
            TealOnSecondary to TealSecondary,
            TealOnSecondaryContainer to TealSecondaryContainer,
            AmberOnTertiary to AmberTertiary,
            AmberOnTertiaryContainer to AmberTertiaryContainer,
            LightOnBackground to LightBackground,
            LightOnSurface to LightSurface,
            LightOnSurfaceVariant to LightSurface,
            LightOnSurfaceVariant to LightSurfaceVariant
        )
        lightTextPairs.forEach { (fg, bg) ->
            assertTrue(
                "Light text pair failed >= 4.5:1",
                ColorUtils.hasAdequateContrast(fg, bg, bg, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO)
            )
        }

        val darkTextPairs = listOf(
            DarkOnPrimary to DarkPrimary,
            DarkOnPrimaryContainer to DarkPrimaryContainer,
            DarkOnSecondary to DarkSecondary,
            DarkOnSecondaryContainer to DarkSecondaryContainer,
            DarkOnTertiary to DarkTertiary,
            DarkOnTertiaryContainer to DarkTertiaryContainer,
            DarkOnBackground to DarkBackground,
            DarkOnSurface to DarkSurface,
            DarkOnSurfaceVariant to DarkSurface,
            DarkOnSurfaceVariant to DarkSurfaceVariant
        )
        darkTextPairs.forEach { (fg, bg) ->
            assertTrue(
                "Dark text pair failed >= 4.5:1",
                ColorUtils.hasAdequateContrast(fg, bg, bg, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO)
            )
        }

        // Remediated outline tokens >= 3.0:1 across Light & Dark surfaces
        assertEquals(4.76, ColorUtils.calculateContrastRatio(LightOutline, LightSurface, LightSurface), 0.05)
        assertEquals(4.55, ColorUtils.calculateContrastRatio(LightOutline, LightBackground, LightBackground), 0.05)
        assertEquals(4.34, ColorUtils.calculateContrastRatio(LightOutline, LightSurfaceVariant, LightSurfaceVariant), 0.05)
        assertEquals(4.85, ColorUtils.calculateContrastRatio(DarkOutline, DarkSurface, DarkSurface), 0.05)
        assertEquals(4.00, ColorUtils.calculateContrastRatio(DarkOutline, DarkSurfaceVariant, DarkSurfaceVariant), 0.05)

        assertTrue(ColorUtils.hasAdequateNonTextContrast(LightOutline, LightSurface, LightSurface))
        assertTrue(ColorUtils.hasAdequateNonTextContrast(LightOutline, LightSurfaceVariant, LightSurfaceVariant))
        assertTrue(ColorUtils.hasAdequateNonTextContrast(DarkOutline, DarkSurface, DarkSurface))
        assertTrue(ColorUtils.hasAdequateNonTextContrast(DarkOutline, DarkSurfaceVariant, DarkSurfaceVariant))
    }

    @Test
    fun parentSurfaceOpaqueInvariantAndTranslucentCompositingAreEnforced() {
        var threwForTranslucentParent = false
        try {
            ColorUtils.calculateContrastRatio(
                foreground = Color.Black,
                background = Color.White,
                parentSurface = Color(0x80FFFFFF)
            )
        } catch (_: IllegalArgumentException) {
            threwForTranslucentParent = true
        }
        assertTrue("Expected IllegalArgumentException for translucent parentSurface", threwForTranslucentParent)

        // Same translucent card color produces opposite contrast over Light vs Dark opaque parentSurface
        val translucentSlateCard = Color(0xFF1E293B).copy(alpha = 0.2f)
        val lightRatio = ColorUtils.calculateContrastRatio(
            foreground = Color(0xFF0F172A),
            background = translucentSlateCard,
            parentSurface = LightBackground
        )
        val darkRatio = ColorUtils.calculateContrastRatio(
            foreground = Color(0xFF0F172A),
            background = translucentSlateCard,
            parentSurface = DarkBackground
        )
        assertTrue("Dark ink on translucent card over light backdrop passes 4.5:1", lightRatio >= 4.5)
        assertTrue("Dark ink on translucent card over dark backdrop fails 4.5:1", darkRatio < 2.0)
    }

    @Test
    fun suggestAccessibleColor_preservesHueWhenPossibleAndFallsBackDeterministically() {
        // Hue-preserving lightness adjustment for blue on light gray
        val weakBlue = Color(0xFF93C5FD)
        val suggestedText = ColorUtils.suggestAccessibleColor(
            foreground = weakBlue,
            background = LightSurface,
            parentSurface = LightSurface,
            minRatio = ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO
        )
        assertTrue(
            ColorUtils.calculateContrastRatio(suggestedText, LightSurface, LightSurface) >= 4.5
        )
        assertTrue("Adjusted blue should keep blue channel dominant", suggestedText.blue >= suggestedText.red)

        // Saturated mid-luminance background where deterministic fallback may be needed
        val trickyBg = Color(0xFFDC2626)
        val adjustedOnRed = ColorUtils.suggestAccessibleColor(
            foreground = Color(0xFFEA580C),
            background = trickyBg,
            parentSurface = LightBackground,
            minRatio = ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO
        )
        assertTrue(
            ColorUtils.calculateContrastRatio(adjustedOnRed, trickyBg, LightBackground) >= 4.5
        )

        val nonTextAdjusted = ColorUtils.suggestAccessibleColor(
            foreground = Color(0xFFCBD5E1),
            background = LightSurface,
            parentSurface = LightSurface,
            minRatio = ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO
        )
        assertTrue(
            ColorUtils.calculateContrastRatio(nonTextAdjusted, LightSurface, LightSurface) >= 3.0
        )
    }

    @Test
    fun coreComponents_exposeRolesTouchTargetsAndBengaliLabels() {
        var learned by mutableStateOf(false)
        var keypadText by mutableStateOf("")

        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                Column {
                    MathModuleGridCard(
                        item = HomeModuleItem(
                            sectionKey = SectionKey.TABLES,
                            titleBn = "গুণ ও নামতা",
                            subtitleBn = "১-৪০",
                            badgeBn = "৪০",
                            defaultIcon = SectionKey.TABLES.defaultIcon.toImageVector(),
                            defaultGradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                            testTag = "a11y_grid_card",
                            onClick = {}
                        ),
                        global = com.example.ui.customization.GlobalCustomConfig(),
                        sectionConfig = SectionCustomConfig()
                    )
                    LearnedStatusBadge(
                        isLearned = learned,
                        onToggle = { learned = !learned }
                    )
                    BanglaNumericKeypad(
                        currentValue = keypadText,
                        onValueChange = { keypadText = it },
                        onClear = { keypadText = "" }
                    )
                }
            }
        }

        // Card exposes Role.Button
        composeTestRule.onNodeWithTag("a11y_grid_card")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))

        // LearnedStatusBadge exposes Role.Checkbox, ToggleableState, >= 48dp height
        val badgeNode = composeTestRule.onNodeWithTag("learned_toggle_chip")
        badgeNode.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
        badgeNode.assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
        assertTrue(badgeNode.getUnclippedBoundsInRoot().height >= 48.dp)
        badgeNode.performClick()
        composeTestRule.waitForIdle()
        badgeNode.assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))

        // Keypad BACK button has Bengali contentDescription and Role.Button
        val backKey = composeTestRule.onNodeWithTag("keypad_btn_BACK")
        backKey.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        assertEquals(
            listOf("মুছে ফেলুন"),
            backKey.fetchSemanticsNode().config[SemanticsProperties.ContentDescription]
        )
        assertTrue(backKey.getUnclippedBoundsInRoot().height >= 48.dp)
    }

    @Test
    fun problemSolver_fractionRadioButtonsAndValidationBannerExposeSemantics() {
        val viewModel = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                ProblemSolverScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithTag("solver_content_list").performScrollToIndex(2)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("solver_topics_row")
            .performScrollToNode(hasTestTag("solver_topic_fractions_decimals"))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("solver_topic_fractions_decimals").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("fraction_op_+")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assertIsSelected()
        assertTrue(composeTestRule.onNodeWithTag("fraction_op_+").getUnclippedBoundsInRoot().height >= 48.dp)

        // Enter invalid denominator 0 -> SolverInputValidationBanner exposes Polite liveRegion
        composeTestRule.onNodeWithTag("fraction_den1").performTextReplacement("0")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("solver_input_validation_banner")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
    }

    @Test
    fun tutorFeedbackBanner_exposesPoliteLiveRegion() {
        val viewModel = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                PracticeTutorScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithTag("tutor_check_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("tutor_feedback_banner")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
    }

    @Test
    fun customizeSwatches_exposeRadioButtonRole() {
        val viewModel = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                CustomizeScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithTag("custom_tab_2").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("customize_list")
            .performScrollToNode(hasTestTag("icon_picker_row"))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("icon_picker_row")
            .performScrollToNode(hasTestTag("icon_pick_calculate"))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("icon_pick_calculate")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
    }
}
