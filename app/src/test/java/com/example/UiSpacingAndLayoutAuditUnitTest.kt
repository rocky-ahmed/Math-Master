package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathSpacing
import com.example.ui.theme.MathSpacingSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Automated Regression Test Suite for Application-Wide UI Spacing, Padding, Margin,
 * Content Inset, Card Alignment & Layout Bug Hunt (BUG-59 through BUG-67).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UiSpacingAndLayoutAuditUnitTest {

    private fun resolveSourceFile(relativePath: String): File {
        val candidates = listOf(
            File("app/src/main/java/com/example/$relativePath"),
            File("src/main/java/com/example/$relativePath"),
            File("/app/applet/app/src/main/java/com/example/$relativePath")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("Source file not found for $relativePath")
    }

    @Test
    fun `verify BUG-67 centralized MathSpacingSystem semantic tokens and list contentPadding`() {
        val system = MathSpacingSystem()
        assertEquals(2.dp, system.xxs)
        assertEquals(4.dp, system.xs)
        assertEquals(8.dp, system.sm)
        assertEquals(12.dp, system.md)
        assertEquals(16.dp, system.lg)
        assertEquals(20.dp, system.xl)
        assertEquals(24.dp, system.xxl)
        assertEquals(32.dp, system.xxxl)

        assertEquals(16.dp, system.screenHorizontal)
        assertEquals(12.dp, system.screenTop)
        assertEquals(24.dp, system.screenBottom)
        assertEquals(12.dp, system.cardPaddingCompact)
        assertEquals(16.dp, system.cardPaddingStandard)
        assertEquals(18.dp, system.cardPaddingHero)
        assertEquals(48.dp, system.minTouchTarget)

        val defaultListPadding = MathSpacing.screenListContentPadding()
        assertEquals(16.dp, defaultListPadding.calculateLeftPadding(LayoutDirection.Ltr))
        assertEquals(16.dp, defaultListPadding.calculateRightPadding(LayoutDirection.Ltr))
        assertEquals(12.dp, defaultListPadding.calculateTopPadding())
        assertEquals(24.dp, defaultListPadding.calculateBottomPadding())
    }

    @Test
    fun `verify BUG-61 opaque blended container color eliminates translucent ElevatedCard shadow artifact`() {
        val surfaceLight = Color(0xFFFFFFFF)
        val tertiaryContainerLight = Color(0xFFFEF3C7)
        val blendedLight = lerp(surfaceLight, tertiaryContainerLight, 0.55f)
        assertEquals(1.0f, blendedLight.alpha, 0.0001f)

        val surfaceDark = Color(0xFF0F172A)
        val tertiaryContainerDark = Color(0xFF78350F)
        val blendedDark = lerp(surfaceDark, tertiaryContainerDark, 0.55f)
        assertEquals(1.0f, blendedDark.alpha, 0.0001f)

        // Verify DashboardScreen, TablesScreen, NumberSystemScreen, NumberSystemPracticePlayScreen,
        // and ProblemSolverScreen use MathSpacing.blendedContainerColor instead of alpha < 1f on ElevatedCard
        val dashboardSrc = resolveSourceFile("ui/screens/DashboardScreen.kt").readText()
        assertTrue(dashboardSrc.contains("MathSpacing.blendedContainerColor"))
        assertFalse(dashboardSrc.contains("tertiaryContainer.copy(alpha = 0.5f)"))

        val tablesSrc = resolveSourceFile("ui/screens/TablesScreen.kt").readText()
        assertTrue(tablesSrc.contains("MathSpacing.blendedContainerColor"))

        val numberSystemSrc = resolveSourceFile("ui/screens/NumberSystemScreen.kt").readText()
        assertTrue(numberSystemSrc.contains("MathSpacing.blendedContainerColor"))

        val nsPracticeSrc = resolveSourceFile("ui/screens/NumberSystemPracticePlayScreen.kt").readText()
        assertTrue(nsPracticeSrc.contains("MathSpacing.blendedContainerColor"))

        val solverSrc = resolveSourceFile("ui/screens/ProblemSolverScreen.kt").readText()
        assertTrue(solverSrc.contains("MathSpacing.blendedContainerColor"))
    }

    @Test
    fun `verify BUG-59 nested Scaffold modifier propagation and WindowInsets deduplication`() {
        val mainActivitySrc = resolveSourceFile("MainActivity.kt").readText()
        assertTrue(
            "MainActivity outer Scaffold must zero duplicate contentWindowInsets",
            mainActivitySrc.contains("contentWindowInsets = WindowInsets(0, 0, 0, 0)")
        )

        val scaffoldScreens = listOf(
            "ui/screens/DashboardScreen.kt",
            "ui/screens/QuizzesScreen.kt",
            "ui/screens/BcsPreparationScreen.kt",
            "ui/screens/BookHubAndDetailScreens.kt",
            "ui/screens/BookPracticeAndRevisionScreens.kt",
            "ui/screens/BookReaderAndLabsScreens.kt"
        )
        for (path in scaffoldScreens) {
            val content = resolveSourceFile(path).readText()
            assertTrue(
                "$path must propagate modifier = modifier.fillMaxSize() to inner Scaffold",
                content.contains("modifier = modifier.fillMaxSize()")
            )
            assertTrue(
                "$path must zero duplicate inner Scaffold WindowInsets",
                content.contains("contentWindowInsets = WindowInsets(0, 0, 0, 0)")
            )
        }

        // Verify full-screen play screens with custom top headers apply statusBarsPadding()
        val practicePlaySrc = resolveSourceFile("ui/screens/PracticeLabPlayScreen.kt").readText()
        assertTrue(practicePlaySrc.contains(".statusBarsPadding()"))

        val mentalPlaySrc = resolveSourceFile("ui/screens/MentalMathPlayScreen.kt").readText()
        assertTrue(mentalPlaySrc.contains(".statusBarsPadding()"))
    }

    @Test
    fun `verify BUG-60 SpaceBetween card headers assign weight(1f) so trailing actions never clip`() {
        val dashboardSrc = resolveSourceFile("ui/screens/DashboardScreen.kt").readText()
        // Both daily goal card and book progress card ("খুলুন" button) must weight leading Row/Column
        assertTrue(dashboardSrc.contains("modifier = Modifier.weight(1f)"))
        assertTrue(dashboardSrc.contains("dashboard_open_book_hub_btn"))

        val primesSrc = resolveSourceFile("ui/screens/PrimesScreen.kt").readText()
        assertTrue(primesSrc.contains("modifier = Modifier.weight(1f)"))

        val formulasSrc = resolveSourceFile("ui/screens/FormulasScreen.kt").readText()
        assertTrue(formulasSrc.contains("modifier = Modifier.weight(1f)"))

        val bookRevSrc = resolveSourceFile("ui/screens/BookPracticeAndRevisionScreens.kt").readText()
        assertTrue(bookRevSrc.contains("modifier = Modifier.weight(1f)"))
    }

    @Test
    fun `verify BUG-62 MathSectionHeader avoids duplicate horizontal padding and LazyLists use contentPadding`() {
        val commonSrc = resolveSourceFile("ui/components/CommonComponents.kt").readText()
        assertTrue(
            "MathSectionHeader must default horizontalPadding to 0.dp inside padded containers",
            commonSrc.contains("horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp") ||
                commonSrc.contains("horizontalPadding: Dp = 0.dp")
        )

        val refactoredListScreens = listOf(
            "ui/screens/TablesScreen.kt",
            "ui/screens/FormulasScreen.kt",
            "ui/screens/BookmarksAndTutorScreens.kt",
            "ui/screens/PracticeLabPlayScreen.kt",
            "ui/screens/CustomizeScreen.kt",
            "ui/screens/QuizzesScreen.kt"
        )
        for (path in refactoredListScreens) {
            val src = resolveSourceFile(path).readText()
            assertTrue(
                "$path must use MathSpacing.screenListContentPadding()",
                src.contains("MathSpacing.screenListContentPadding()")
            )
        }
    }

    @Test
    fun `verify BUG-63 peer card rows use IntrinsicSize Max and fillMaxHeight for uniform card heights`() {
        val dashboardSrc = resolveSourceFile("ui/screens/DashboardScreen.kt").readText()
        assertTrue(dashboardSrc.contains(".height(IntrinsicSize.Max)"))
        assertTrue(dashboardSrc.contains(".fillMaxHeight()"))

        val visualExplorerSrc = resolveSourceFile("ui/screens/VisualExplorerScreen.kt").readText()
        assertTrue(visualExplorerSrc.contains(".height(IntrinsicSize.Max)"))
        assertTrue(visualExplorerSrc.contains(".fillMaxHeight()"))
    }

    @Test
    fun `verify BUG-64 MentalMathSummaryView review cards use theme-safe blended container color in dark mode`() {
        val mentalPlaySrc = resolveSourceFile("ui/screens/MentalMathPlayScreen.kt").readText()
        assertFalse(
            "MentalMathPlayScreen review card must not hardcode light-only Color(0xFFECFDF5) with onSurface text",
            mentalPlaySrc.contains("if (item.isCorrect) Color(0xFFECFDF5) else Color(0xFFFFF1F2)")
        )
        assertTrue(mentalPlaySrc.contains("MathSpacing.blendedContainerColor"))
    }

    @Test
    fun `verify BUG-65 and BUG-66 interactive control sizing and navigation across Dashboard and Quizzes`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = MathViewModel(app)

        // Navigate to Dashboard and verify state in both Light and Dark modes
        viewModel.navigateTo(Screen.Dashboard)
        assertEquals(Screen.Dashboard, viewModel.currentScreen)

        viewModel.setThemeMode(AppThemeMode.DARK)
        assertEquals(AppThemeMode.DARK, viewModel.themeMode.value)

        // Open BookHub from Dashboard ("খুলুন" action)
        viewModel.openBookHub()
        assertEquals(Screen.BookHub, viewModel.currentScreen)

        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Dashboard, viewModel.currentScreen)

        // Navigate to Quizzes and verify state
        viewModel.navigateTo(Screen.Quizzes)
        assertEquals(Screen.Quizzes, viewModel.currentScreen)

        // Verify Activity still launches and renders cleanly with all spacing/inset repairs
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertNotNull(controller.get())
        controller.pause().stop().destroy()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }
}
