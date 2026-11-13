package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.StudySessionTimerState
import com.example.ui.components.StudySessionTimerSection
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
class StudyTimerCompactLayoutRegressionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private data class TimerRenderSpec(
        val containerWidthDp: Dp = 360.dp,
        val fontScale: Float = 1.0f,
        val themeMode: AppThemeMode = AppThemeMode.LIGHT,
        val isPanelEnabled: Boolean = false
    )

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

    private fun saveViewScreenshot(rootView: View?, cardWidthPx: Int, cardHeightPx: Int, fileName: String) {
        if (rootView == null || rootView.width <= 0 || rootView.height <= 0) return
        runCatching {
            val fullBmp = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(fullBmp)
            rootView.draw(canvas)
            val cropW = cardWidthPx.coerceIn(1, fullBmp.width)
            val cropH = cardHeightPx.coerceIn(1, fullBmp.height)
            val cropped = Bitmap.createBitmap(fullBmp, 0, 0, cropW, cropH)
            val outDir = File("build/test-screenshots").apply { mkdirs() }
            FileOutputStream(File(outDir, fileName)).use { fos ->
                cropped.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            cropped.recycle()
            fullBmp.recycle()
            println("SCREENSHOT_SAVED: build/test-screenshots/$fileName (${cropW}x${cropH})")
        }.onFailure { err ->
            println("SCREENSHOT_FAILED[$fileName]: ${err.message}")
        }
    }

    @Test
    fun verifyCompactTimerLayoutAcrossScreenWidthsFontScalesAndThemes() {
        val specState = mutableStateOf(TimerRenderSpec())
        var measuredDensity = 2.0f
        var hostView: View? = null

        composeRule.setContent {
            val spec = specState.value
            val currentDensity = LocalDensity.current
            measuredDensity = currentDensity.density
            hostView = LocalView.current
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = currentDensity.density,
                    fontScale = spec.fontScale
                )
            ) {
                MathMasterTheme(themeMode = spec.themeMode) {
                    Box(
                        modifier = Modifier
                            .width(spec.containerWidthDp)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        StudySessionTimerSection(
                            timerState = StudySessionTimerState(
                                isVisibleOnHome = spec.isPanelEnabled,
                                isRunning = false,
                                currentSessionSeconds = 0,
                                todayTotalSeconds = 125,
                                allTimeTotalSeconds = 950,
                                dailyGoalMinutes = 30,
                                completedSessionsToday = 1
                            ),
                            onToggleVisibility = {},
                            onToggleRunning = {},
                            onFinishSession = {},
                            onResetSession = {},
                            onSelectGoalMinutes = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        fun assertSpecGeometry(
            spec: TimerRenderSpec,
            expectNoOverflow: Boolean = false,
            screenshotName: String? = null
        ): Float {
            composeRule.runOnIdle {
                specState.value = spec
            }
            composeRule.waitForIdle()

            val cardNode = composeRule.onNodeWithTag("study_session_timer_card").assertIsDisplayed().fetchSemanticsNode()
            val titleNode = composeRule.onNodeWithTag("study_timer_title_text").assertIsDisplayed().fetchSemanticsNode()
            val subtitleNode = composeRule.onNodeWithTag("study_timer_status_text").assertIsDisplayed().fetchSemanticsNode()
            val switchNode = composeRule.onNodeWithTag("study_timer_toggle_switch").assertIsDisplayed().fetchSemanticsNode()
            val iconNode = composeRule.onAllNodesWithTag("study_timer_icon_badge").fetchSemanticsNodes().firstOrNull()

            val d = measuredDensity
            val cardBounds = cardNode.boundsInRoot
            val titleBounds = titleNode.boundsInRoot
            val subtitleBounds = subtitleNode.boundsInRoot
            val switchBounds = switchNode.boundsInRoot
            val iconBounds = iconNode?.boundsInRoot

            if (screenshotName != null) {
                composeRule.runOnIdle {
                    val cropW = (spec.containerWidthDp.value * d).toInt()
                    val cropH = (cardBounds.bottom + (12f * d)).toInt()
                    saveViewScreenshot(hostView, cropW, cropH, screenshotName)
                }
            }

            val cardHeightDp = cardBounds.height / d
            val cardWidthDp = cardBounds.width / d
            val titleHeightDp = titleBounds.height / d
            val subtitleHeightDp = subtitleBounds.height / d
            val verticalGapDp = (subtitleBounds.top - titleBounds.bottom) / d

            println(
                "GEOMETRY[width=${spec.containerWidthDp.value}dp, fontScale=${spec.fontScale}, theme=${spec.themeMode}, on=${spec.isPanelEnabled}] -> " +
                    "card=${cardWidthDp}x${cardHeightDp}dp, " +
                    "icon=${iconBounds?.let { "[${it.left / d}..${it.right / d}, ${it.top / d}..${it.bottom / d}]" } ?: "none"}, " +
                    "title=[${titleBounds.left / d}..${titleBounds.right / d}, ${titleBounds.top / d}..${titleBounds.bottom / d}] (h=${titleHeightDp}dp), " +
                    "subtitle=[${subtitleBounds.left / d}..${subtitleBounds.right / d}, ${subtitleBounds.top / d}..${subtitleBounds.bottom / d}] (h=${subtitleHeightDp}dp), " +
                    "gap=${verticalGapDp}dp, " +
                    "switch=[${switchBounds.left / d}..${switchBounds.right / d}, ${switchBounds.top / d}..${switchBounds.bottom / d}]"
            )

            assertTrue(
                "Card width (${cardWidthDp}dp) must fit inside container (${spec.containerWidthDp.value}dp)",
                cardWidthDp <= spec.containerWidthDp.value - 31f
            )
            assertTrue("Title width must be > 0", titleBounds.width > 0f)
            assertTrue("Subtitle width must be > 0", subtitleBounds.width > 0f)

            if (iconBounds != null) {
                assertTrue("Icon must stay inside card", iconBounds.left >= cardBounds.left && iconBounds.right <= cardBounds.right && iconBounds.top >= cardBounds.top && iconBounds.bottom <= cardBounds.bottom)
                assertTrue("Title must start right of icon", titleBounds.left >= iconBounds.right - 0.5f)
                assertTrue("Subtitle must start right of icon", subtitleBounds.left >= iconBounds.right - 0.5f)
            }
            assertTrue("Title must stay left of switch", titleBounds.right <= switchBounds.left + 0.5f)
            assertTrue("Subtitle must stay left of switch", subtitleBounds.right <= switchBounds.left + 0.5f)
            assertTrue("Title must stay inside card", titleBounds.left >= cardBounds.left && titleBounds.right <= cardBounds.right && titleBounds.top >= cardBounds.top && titleBounds.bottom <= cardBounds.bottom)
            assertTrue("Subtitle must stay inside card", subtitleBounds.left >= cardBounds.left && subtitleBounds.right <= cardBounds.right && subtitleBounds.top >= cardBounds.top && subtitleBounds.bottom <= cardBounds.bottom)
            assertTrue("Switch must stay inside card", switchBounds.left >= cardBounds.left && switchBounds.right <= cardBounds.right && switchBounds.top >= cardBounds.top && switchBounds.bottom <= cardBounds.bottom)

            assertTrue("Switch touch target width >= 48dp", switchBounds.width / d >= 48f - 0.5f)
            // Material 3 Switch uses TouchBoundsExpansion via minimumInteractiveComponentSize();
            // visual track height is ~32dp centered inside >= 48dp header Row touch height.
            assertTrue("Header card height must accommodate >= 48dp touch height", cardHeightDp >= 48f - 0.5f)
            assertTrue("Switch visual track height must be >= 28dp", switchBounds.height / d >= 28f)

            assertTrue(
                "Title bottom (${titleBounds.bottom / d}dp) and subtitle top (${subtitleBounds.top / d}dp) must NOT overlap (gap=${verticalGapDp}dp)",
                subtitleBounds.top >= titleBounds.bottom + (1.0f * d)
            )

            if (!spec.isPanelEnabled) {
                val cardCenterY = (cardBounds.top + cardBounds.bottom) / 2f
                val switchCenterY = (switchBounds.top + switchBounds.bottom) / 2f
                val textGroupCenterY = (titleBounds.top + subtitleBounds.bottom) / 2f
                assertTrue("Switch center must align with card center", abs(switchCenterY - cardCenterY) <= 3f * d)
                assertTrue("Text column center must align with card center", abs(textGroupCenterY - cardCenterY) <= 4f * d)
                if (iconBounds != null) {
                    val iconCenterY = (iconBounds.top + iconBounds.bottom) / 2f
                    assertTrue("Icon center must align with card center", abs(iconCenterY - cardCenterY) <= 3f * d)
                }
            }

            val titleLayout = getTextLayoutResult(titleNode)
            val subtitleLayout = getTextLayoutResult(subtitleNode)
            if (titleLayout != null) {
                println("TITLE_LAYOUT[${spec.containerWidthDp.value}dp/${spec.fontScale}x]: lines=${titleLayout.lineCount}, size=${titleLayout.size}, multiW=${titleLayout.multiParagraph.width}, multiH=${titleLayout.multiParagraph.height}, overflowW=${titleLayout.didOverflowWidth}, overflowH=${titleLayout.didOverflowHeight}, ellipsized0=${titleLayout.isLineEllipsized(0)}")
                assertEquals("Title must avoid unnecessary line wrapping", 1, titleLayout.lineCount)
                if (expectNoOverflow) {
                    assertFalse("Title must not truncate at ${spec.containerWidthDp.value}dp / ${spec.fontScale}x", titleLayout.isLineEllipsized(0))
                }
            }
            if (subtitleLayout != null) {
                println("SUB_LAYOUT[${spec.containerWidthDp.value}dp/${spec.fontScale}x]: lines=${subtitleLayout.lineCount}, size=${subtitleLayout.size}, multiW=${subtitleLayout.multiParagraph.width}, multiH=${subtitleLayout.multiParagraph.height}, overflowW=${subtitleLayout.didOverflowWidth}, overflowH=${subtitleLayout.didOverflowHeight}, ellipsized0=${subtitleLayout.isLineEllipsized(0)}")
                assertEquals("Subtitle must avoid unnecessary line wrapping", 1, subtitleLayout.lineCount)
                if (expectNoOverflow && !spec.isPanelEnabled) {
                    assertFalse("Subtitle must not truncate at ${spec.containerWidthDp.value}dp / ${spec.fontScale}x", subtitleLayout.isLineEllipsized(0))
                }
            }

            return cardHeightDp
        }

        // Standard phone width (360dp) at 1.0x - Light & Dark
        val stdLightH = assertSpecGeometry(
            TimerRenderSpec(360.dp, 1.0f, AppThemeMode.LIGHT, false),
            expectNoOverflow = true,
            screenshotName = "study_timer_360dp_1x_light.png"
        )
        assertTrue("Compact OFF height (${stdLightH}dp) must be 64..72dp", stdLightH in 64f..72f)

        val stdDarkH = assertSpecGeometry(
            TimerRenderSpec(360.dp, 1.0f, AppThemeMode.DARK, false),
            expectNoOverflow = true,
            screenshotName = "study_timer_360dp_1x_dark.png"
        )
        assertTrue("Dark theme compact OFF height (${stdDarkH}dp) must be 64..72dp", stdDarkH in 64f..72f)

        // Narrow phone width (320dp) at 1.0x
        assertSpecGeometry(
            TimerRenderSpec(320.dp, 1.0f, AppThemeMode.LIGHT, false),
            screenshotName = "study_timer_320dp_1x_light.png"
        )

        // Larger phone width (411dp) at 1.0x
        assertSpecGeometry(
            TimerRenderSpec(411.dp, 1.0f, AppThemeMode.LIGHT, false),
            expectNoOverflow = true,
            screenshotName = "study_timer_411dp_1x_light.png"
        )

        // Font scales: 1.15x, 1.30x, 1.50x
        val h115 = assertSpecGeometry(TimerRenderSpec(360.dp, 1.15f, AppThemeMode.LIGHT, false))
        val h130 = assertSpecGeometry(TimerRenderSpec(360.dp, 1.30f, AppThemeMode.LIGHT, false))
        val h150 = assertSpecGeometry(
            TimerRenderSpec(360.dp, 1.50f, AppThemeMode.LIGHT, false),
            screenshotName = "study_timer_360dp_1_5x_light.png"
        )
        assertTrue(
            "Card height must grow naturally with font scale (1.0x=$stdLightH, 1.15x=$h115, 1.3x=$h130, 1.5x=$h150)",
            h150 >= h130 && h130 >= stdLightH
        )

        // Expanded ON state
        assertSpecGeometry(
            TimerRenderSpec(360.dp, 1.0f, AppThemeMode.LIGHT, true),
            expectNoOverflow = true,
            screenshotName = "study_timer_360dp_1x_expanded.png"
        )
    }

    @Test
    @Config(qualifiers = "w360dp-h520dp-xhdpi")
    fun verifyCompactScreenHeightAndDarkExpandedState() {
        var measuredDensity = 2.0f
        composeRule.setContent {
            val currentDensity = LocalDensity.current
            measuredDensity = currentDensity.density
            CompositionLocalProvider(
                LocalDensity provides Density(density = currentDensity.density, fontScale = 1.3f)
            ) {
                MathMasterTheme(themeMode = AppThemeMode.DARK) {
                    Box(
                        modifier = Modifier
                            .width(360.dp)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        StudySessionTimerSection(
                            timerState = StudySessionTimerState(
                                isVisibleOnHome = true,
                                isRunning = true,
                                currentSessionSeconds = 75,
                                todayTotalSeconds = 300,
                                allTimeTotalSeconds = 1200,
                                dailyGoalMinutes = 30,
                                completedSessionsToday = 2
                            ),
                            onToggleVisibility = {},
                            onToggleRunning = {},
                            onFinishSession = {},
                            onResetSession = {},
                            onSelectGoalMinutes = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val titleBounds = composeRule.onNodeWithTag("study_timer_title_text").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val subtitleBounds = composeRule.onNodeWithTag("study_timer_status_text").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        assertTrue(
            "Expanded dark mode at 1.3x fontScale must keep non-overlapping title & subtitle",
            subtitleBounds.top >= titleBounds.bottom + (1.0f * measuredDensity)
        )
    }
}
