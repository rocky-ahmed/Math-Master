package com.example

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.math.SolverTopic
import com.example.ui.MathViewModel
import com.example.ui.customization.CustomLayoutType
import com.example.ui.customization.resolveEffectiveGridColumns
import com.example.ui.screens.CustomizeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuizzesScreen
import com.example.ui.screens.SimultaneousLinearSystemAndBridgeCard
import com.example.ui.screens.TablesScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathMasterTheme
import kotlinx.coroutines.Dispatchers
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w320dp-h640dp", fontScale = 1.5f)
class Phase2ResponsiveLayoutTest {

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
    fun resolveEffectiveGridColumns_preventsDoublePaddingAndDropsToSingleColumnOn320dp150Percent() {
        // On 320dp width: availableGridContentWidthDp = 320 - 32 = 288dp. At fontScale 1.5x -> 1 column
        assertEquals(
            1,
            resolveEffectiveGridColumns(
                requestedColumns = 2,
                availableGridContentWidthDp = 288,
                horizontalGapDp = 14,
                fontScale = 1.5f,
                interfaceScalePercent = 100
            )
        )
        // On 360dp @ 1.0x: availableGridContentWidthDp = 328dp >= 2 * 136 + 14 = 286dp -> 2 columns
        assertEquals(
            2,
            resolveEffectiveGridColumns(
                requestedColumns = 2,
                availableGridContentWidthDp = 328,
                horizontalGapDp = 14,
                fontScale = 1.0f,
                interfaceScalePercent = 100
            )
        )
        // On 600dp @ 1.0x -> 3 columns; on 840dp @ 1.0x -> 4 columns
        assertEquals(
            3,
            resolveEffectiveGridColumns(
                requestedColumns = 3,
                availableGridContentWidthDp = 568,
                horizontalGapDp = 14,
                fontScale = 1.0f,
                interfaceScalePercent = 100
            )
        )
        assertEquals(
            4,
            resolveEffectiveGridColumns(
                requestedColumns = 4,
                availableGridContentWidthDp = 808,
                horizontalGapDp = 14,
                fontScale = 1.0f,
                interfaceScalePercent = 100
            )
        )
    }

    @Test
    fun simultaneousEquationInputs_all2x2And3x3FieldsAreAtLeast96dpWideWithoutOverlapOn320dp() {
        val viewModel = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SimultaneousLinearSystemAndBridgeCard(
                        selectedTopic = SolverTopic.LINEAR_EQUATIONS,
                        viewModel = viewModel
                    )
                }
            }
        }
        composeTestRule.waitForIdle()

        val twoByTwoTags = listOf("sim2_a1", "sim2_b1", "sim2_c1", "sim2_a2", "sim2_b2", "sim2_c2")
        twoByTwoTags.forEach { tag ->
            val bounds = composeTestRule.onNodeWithTag(tag).getUnclippedBoundsInRoot()
            assertTrue(
                "2x2 field $tag width ${bounds.width} must be >= 96.dp",
                bounds.width >= 96.dp
            )
        }
        val a1 = composeTestRule.onNodeWithTag("sim2_a1").getUnclippedBoundsInRoot()
        val b1 = composeTestRule.onNodeWithTag("sim2_b1").getUnclippedBoundsInRoot()
        assertTrue("a1 and b1 must not overlap horizontally", a1.right <= b1.left)

        // Switch to 3x3 and verify all 12 fields are >= 96.dp without overlap
        composeTestRule.onNodeWithTag("sim_dim_3x3").performClick()
        composeTestRule.waitForIdle()

        val threeByThreeTags = listOf(
            "sim3_a1", "sim3_b1", "sim3_c1", "sim3_d1",
            "sim3_a2", "sim3_b2", "sim3_c2", "sim3_d2",
            "sim3_a3", "sim3_b3", "sim3_c3", "sim3_d3"
        )
        threeByThreeTags.forEach { tag ->
            val bounds = composeTestRule.onNodeWithTag(tag).getUnclippedBoundsInRoot()
            assertTrue(
                "3x3 field $tag width ${bounds.width} must be >= 96.dp",
                bounds.width >= 96.dp
            )
        }
        val s3a1 = composeTestRule.onNodeWithTag("sim3_a1").getUnclippedBoundsInRoot()
        val s3b1 = composeTestRule.onNodeWithTag("sim3_b1").getUnclippedBoundsInRoot()
        assertTrue("3x3 a1 and b1 must not overlap horizontally", s3a1.right <= s3b1.left)
    }

    @Test
    fun homeGrid_collapsesToSingleFullWidthColumnOn320dpAt150PercentFontScale() {
        val viewModel = MathViewModel(app)
        viewModel.updatePreviewGlobal { it.copy(layoutType = CustomLayoutType.GRID, gridColumns = 2) }
        viewModel.saveCustomization()

        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                HomeScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithTag("home_module_grid")
            .performScrollToNode(hasTestTag("module_tables_card"))
        composeTestRule.waitForIdle()

        val homeCardBounds = composeTestRule.onNodeWithTag("module_tables_card").getUnclippedBoundsInRoot()
        assertTrue(
            "Home grid card on 320dp @ 1.5x should span full single column (>= 260.dp), was ${homeCardBounds.width}",
            homeCardBounds.width >= 260.dp
        )
    }

    @Test
    fun tablesGrid_collapsesToSingleFullWidthColumnOn320dpAt150PercentFontScale() {
        val viewModel = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                TablesScreen(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()
        val tableCardBounds = composeTestRule.onNodeWithTag("table_item_1").getUnclippedBoundsInRoot()
        assertTrue(
            "Tables grid card on 320dp @ 1.5x should collapse to 1 column (>= 260.dp), was ${tableCardBounds.width}",
            tableCardBounds.width >= 260.dp
        )
    }

    @Test
    @Config(sdk = [36], qualifiers = "w840dp-h1000dp", fontScale = 1.0f)
    fun homeGrid_rendersMultiColumnCardsOnTabletViewport() {
        val viewModel = MathViewModel(app)
        viewModel.updatePreviewGlobal { it.copy(layoutType = CustomLayoutType.GRID, gridColumns = 4) }
        viewModel.saveCustomization()

        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                HomeScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithTag("home_module_grid")
            .performScrollToNode(hasTestTag("module_tables_card"))
        composeTestRule.waitForIdle()

        val card1 = composeTestRule.onNodeWithTag("module_tables_card").getUnclippedBoundsInRoot()
        val card2 = composeTestRule.onNodeWithTag("module_primes_card").getUnclippedBoundsInRoot()
        assertTrue(
            "Adjacent Home cards on 840dp tablet should be side-by-side: card1.right=${card1.right}, card2.left=${card2.left}",
            card1.right <= card2.left
        )
    }

    @Test
    fun quizzesChips_wrapCleanlyInside320dpViewport() {
        val viewModel = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                QuizzesScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithTag("quiz_diff_hard").assertIsDisplayed()
        val hardChipBounds = composeTestRule.onNodeWithTag("quiz_diff_hard").getUnclippedBoundsInRoot()
        assertTrue("Quiz diff chip stays within 320dp bounds: ${hardChipBounds.right}", hardChipBounds.right <= 320.dp)
    }

    @Test
    fun customizeChips_wrapCleanlyInside320dpViewport() {
        val viewModel = MathViewModel(app)
        composeTestRule.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                CustomizeScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithTag("chip_layout_table_list").assertIsDisplayed()
        val tableChipBounds = composeTestRule.onNodeWithTag("chip_layout_table_list").getUnclippedBoundsInRoot()
        assertTrue("Customize layout chip stays within 320dp bounds: ${tableChipBounds.right}", tableChipBounds.right <= 320.dp)
    }
}
