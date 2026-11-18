package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MathViewModel
import com.example.ui.customization.AppCustomizationState
import com.example.ui.customization.CustomFontWeight
import com.example.ui.customization.CustomLayoutType
import com.example.ui.customization.CustomizationPreferences
import com.example.ui.customization.GlobalCustomConfig
import com.example.ui.customization.SectionCustomConfig
import com.example.ui.customization.SectionKey
import com.example.ui.screens.CustomizeScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathMasterTheme
import org.junit.After
import org.junit.Assert.assertEquals
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
class Phase2CustomizationEngineTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun sanitizedClamping_boundsAllNumericFieldsAndNormalizesOrRejectsHex() {
        val dirtyGlobal = GlobalCustomConfig(
            gridColumns = 99,
            horizontalGapDp = 1,
            verticalGapDp = 99,
            cornerRadiusDp = 100,
            iconSizeDp = 5,
            iconTextSpacingDp = 100,
            interfaceScalePercent = 40,
            titleFontSizeSp = 99,
            subtitleFontSizeSp = 3,
            borderThicknessDp = 25,
            customAccentHex = "abc",
            customPrimaryActionHex = "not-a-hex",
            customLightCardSurfaceHex = "  #12345z ",
            customDarkTextColorHex = "#f8fafc"
        ).sanitized()

        assertEquals(4, dirtyGlobal.gridColumns)
        assertEquals(4, dirtyGlobal.horizontalGapDp)
        assertEquals(24, dirtyGlobal.verticalGapDp)
        assertEquals(32, dirtyGlobal.cornerRadiusDp)
        assertEquals(20, dirtyGlobal.iconSizeDp)
        assertEquals(28, dirtyGlobal.iconTextSpacingDp)
        assertEquals(80, dirtyGlobal.interfaceScalePercent)
        assertEquals(24, dirtyGlobal.titleFontSizeSp)
        assertEquals(10, dirtyGlobal.subtitleFontSizeSp)
        assertEquals(6, dirtyGlobal.borderThicknessDp)
        assertEquals("#AABBCC", dirtyGlobal.customAccentHex)
        assertNull(dirtyGlobal.customPrimaryActionHex)
        assertNull(dirtyGlobal.customLightCardSurfaceHex)
        assertEquals("#F8FAFC", dirtyGlobal.customDarkTextColorHex)

        val dirtySection = SectionCustomConfig(
            iconSizeDp = -1,
            titleFontSizeSp = 90,
            subtitleFontSizeSp = 5,
            cornerRadiusDp = 0,
            borderThicknessDp = 99,
            paddingDp = 2,
            cardHeightDp = 40,
            iconColorHex = "bad-hex",
            borderColorHex = "#1e3a8a"
        ).sanitized()
        assertEquals(0, dirtySection.iconSizeDp)
        assertEquals(24, dirtySection.titleFontSizeSp)
        assertEquals(10, dirtySection.subtitleFontSizeSp)
        assertEquals(4, dirtySection.cornerRadiusDp)
        assertEquals(6, dirtySection.borderThicknessDp)
        assertEquals(8, dirtySection.paddingDp)
        assertEquals(120, dirtySection.cardHeightDp)
        assertNull(dirtySection.iconColorHex)
        assertEquals("#1E3A8A", dirtySection.borderColorHex)
    }

    @Test
    fun all15NewlyWiredFields_persistAndRestoreAcrossViewModelRestart() {
        val vm = MathViewModel(app)
        vm.updatePreviewGlobal {
            it.copy(
                iconSizeDp = 36,
                iconTextSpacingDp = 22,
                interfaceScalePercent = 115,
                customLightCardSurfaceHex = "#F1F5F9",
                customLightTextColorHex = "#0F172A",
                customDarkCardSurfaceHex = "#1E293B",
                customDarkTextColorHex = "#F8FAFC"
            )
        }
        vm.updatePreviewSection(SectionKey.TABLES) {
            it.copy(
                iconColorHex = "#1E3A8A",
                titleColorHex = "#0F172A",
                subtitleColorHex = "#334155",
                titleFontSizeSp = 21,
                subtitleFontSizeSp = 15,
                borderThicknessDp = 3,
                borderColorHex = "#1E3A8A",
                cardHeightDp = 180
            )
        }
        vm.saveCustomization()

        val restartedVm = MathViewModel(app)
        val restored = restartedVm.customizationState.value
        assertEquals(36, restored.global.iconSizeDp)
        assertEquals(22, restored.global.iconTextSpacingDp)
        assertEquals(115, restored.global.interfaceScalePercent)
        assertEquals("#F1F5F9", restored.global.customLightCardSurfaceHex)
        assertEquals("#0F172A", restored.global.customLightTextColorHex)
        assertEquals("#1E293B", restored.global.customDarkCardSurfaceHex)
        assertEquals("#F8FAFC", restored.global.customDarkTextColorHex)

        val sec = restored.getSectionConfig(SectionKey.TABLES)
        assertEquals("#1E3A8A", sec.iconColorHex)
        assertEquals("#0F172A", sec.titleColorHex)
        assertEquals("#334155", sec.subtitleColorHex)
        assertEquals(21, sec.titleFontSizeSp)
        assertEquals(15, sec.subtitleFontSizeSp)
        assertEquals(3, sec.borderThicknessDp)
        assertEquals("#1E3A8A", sec.borderColorHex)
        assertEquals(180, sec.cardHeightDp)
    }

    @Test
    fun categoryAndSectionResets_restoreExactDefaultsAndWiredUiControlsWork() {
        val vm = MathViewModel(app)
        vm.updatePreviewGlobal {
            it.copy(
                layoutType = CustomLayoutType.COMPACT_LIST,
                gridColumns = 3,
                iconSizeDp = 38,
                iconTextSpacingDp = 24,
                fontWeight = CustomFontWeight.BOLD,
                titleFontSizeSp = 22,
                subtitleFontSizeSp = 16,
                interfaceScalePercent = 125,
                customLightBackgroundHex = "#FFFFFF",
                customLightCardSurfaceHex = "#F8FAFC",
                customLightTextColorHex = "#0F172A",
                customDarkBackgroundHex = "#0B0F19",
                customDarkCardSurfaceHex = "#111827",
                customDarkTextColorHex = "#F8FAFC"
            )
        }
        vm.updatePreviewSection(SectionKey.PRIMES) {
            it.copy(iconId = "star", iconSizeDp = 40, iconColorHex = "#DC2626", backgroundHex = "#FEF3C7")
        }
        vm.updatePreviewSection(SectionKey.FORMULAS) {
            it.copy(titleFontSizeSp = 20)
        }

        vm.resetPreviewCategory("LAYOUT")
        assertEquals(28, vm.previewCustomizationState.value.global.iconSizeDp)
        assertEquals(16, vm.previewCustomizationState.value.global.iconTextSpacingDp)

        vm.resetPreviewCategory("TYPOGRAPHY")
        assertEquals(100, vm.previewCustomizationState.value.global.interfaceScalePercent)
        assertEquals(16, vm.previewCustomizationState.value.global.titleFontSizeSp)

        vm.resetPreviewCategory("ICONS")
        assertNull(vm.previewCustomizationState.value.getSectionConfig(SectionKey.PRIMES).iconId)
        assertEquals(0, vm.previewCustomizationState.value.getSectionConfig(SectionKey.PRIMES).iconSizeDp)
        assertNull(vm.previewCustomizationState.value.getSectionConfig(SectionKey.PRIMES).iconColorHex)

        vm.resetPreviewCategory("GLOBAL_THEME")
        assertNull(vm.previewCustomizationState.value.global.customLightCardSurfaceHex)
        assertNull(vm.previewCustomizationState.value.global.customDarkTextColorHex)

        vm.resetPreviewCategory("SECTIONS")
        assertTrue(vm.previewCustomizationState.value.sections.isEmpty())

        // Verify CustomizeScreen renders wired controls & "Reset All Sections" button
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                CustomizeScreen(viewModel = vm)
            }
        }
        composeTestRule.onNodeWithTag("custom_tab_3").performClick()
        composeTestRule.waitForIdle()
        vm.updatePreviewSection(SectionKey.TABLES) { it.copy(titleFontSizeSp = 22) }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("btn_reset_all_sections").performClick()
        composeTestRule.waitForIdle()
        assertTrue(vm.previewCustomizationState.value.sections.isEmpty())
    }

    @Test
    fun contrastWarningBanner_appearsAndAutoAdjustResolvesInLightAndDarkModes() {
        val vm = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                CustomizeScreen(viewModel = vm)
            }
        }

        // Section low contrast: yellow text on white-ish background in Light Mode
        composeTestRule.onNodeWithTag("custom_tab_3").performClick()
        composeTestRule.waitForIdle()
        vm.updatePreviewSection(SectionKey.TABLES) {
            it.copy(
                backgroundHex = "#FFFFFF",
                titleColorHex = "#E2E8F0"
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("customize_list")
            .performScrollToNode(hasTestTag("section_contrast_warning_card"))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("section_contrast_warning_card").assertIsDisplayed()

        // Tap one-tap fix button -> resolves section warning
        composeTestRule.onNodeWithTag("btn_fix_section_contrast").performClick()
        composeTestRule.waitForIdle()
        assertTrue(
            composeTestRule.onAllNodesWithTag("section_contrast_warning_card").fetchSemanticsNodes().isEmpty()
        )

        // Global Dark Theme low contrast: near-black text on Dark card surface
        composeTestRule.onNodeWithTag("custom_tab_4").performClick()
        composeTestRule.waitForIdle()
        vm.updatePreviewGlobal {
            it.copy(
                customDarkCardSurfaceHex = "#111827",
                customDarkTextColorHex = "#1F2937"
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("customize_list")
            .performScrollToNode(hasTestTag("contrast_warning_global_dark"))
        composeTestRule.onNodeWithTag("contrast_warning_global_dark").performScrollTo()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("contrast_warning_global_dark").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_fix_contrast_global_dark").performClick()
        composeTestRule.waitForIdle()
        assertTrue(
            composeTestRule.onAllNodesWithTag("contrast_warning_global_dark").fetchSemanticsNodes().isEmpty()
        )

        // Global Light Theme low contrast: light gray text on white card surface
        vm.updatePreviewGlobal {
            it.copy(
                customLightCardSurfaceHex = "#FFFFFF",
                customLightTextColorHex = "#CBD5E1"
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("customize_list")
            .performScrollToNode(hasTestTag("contrast_warning_global_light"))
        composeTestRule.onNodeWithTag("contrast_warning_global_light").performScrollTo()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("contrast_warning_global_light").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_fix_contrast_global_light").performClick()
        composeTestRule.waitForIdle()
        assertTrue(
            composeTestRule.onAllNodesWithTag("contrast_warning_global_light").fetchSemanticsNodes().isEmpty()
        )
    }
}
