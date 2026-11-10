package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.repository.MathRepository
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.customization.AvailableIcon
import com.example.ui.customization.SectionKey
import com.example.ui.screens.HomeScreenDefaults
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class HomeNavigationAndOrderingUnitTest {

    private lateinit var viewModel: MathViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        viewModel = MathViewModel(app)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testCanonicalHomeModulesOrderingPlacingBookLast() {
        var bookHubOpened = false
        var navigatedScreen: Screen? = null

        val modules = HomeScreenDefaults.createHomeModules(
            onOpenBookHub = { bookHubOpened = true },
            onNavigate = { navigatedScreen = it }
        )

        // 1. Exactly 14 modules in the canonical collection
        assertEquals("Home modules must contain exactly 14 modules", 14, modules.size)

        // 2. No duplicate modules
        val uniqueKeys = modules.map { it.sectionKey }.toSet()
        assertEquals("All 14 modules must have unique section keys", 14, uniqueKeys.size)

        val uniqueTags = modules.map { it.testTag }.toSet()
        assertEquals("All 14 modules must have unique test tags", 14, uniqueTags.size)

        // 3. BOOK module must be at the absolute LAST position (index 13 of 14)
        val lastItem = modules.last()
        assertEquals("Final module must be BOOK", SectionKey.BOOK, lastItem.sectionKey)
        assertEquals("Final module testTag must be module_book_card", "module_book_card", lastItem.testTag)
        assertTrue("Final module title must reference পাঠ্যবই", lastItem.titleBn.contains("পাঠ্যবই"))

        // 4. Exactly one BOOK module exists in the list
        val bookModules = modules.filter { it.sectionKey == SectionKey.BOOK }
        assertEquals("Exactly one BOOK module should exist", 1, bookModules.size)

        // 5. First 13 modules retain exact relative order
        val expectedFirst13Keys = listOf(
            SectionKey.TABLES,
            SectionKey.PRIMES,
            SectionKey.SQUARES,
            SectionKey.FORMULAS,
            SectionKey.FORMULA_LIBRARY,
            SectionKey.PRACTICE_LAB,
            SectionKey.MENTAL_MATH,
            SectionKey.NUMBER_SYSTEM,
            SectionKey.PROBLEM_SOLVER,
            SectionKey.VISUAL_EXPLORER,
            SectionKey.QUIZZES,
            SectionKey.DASHBOARD,
            SectionKey.BCS_MATH
        )

        for (i in 0 until 13) {
            assertEquals("Module at index $i mismatch", expectedFirst13Keys[i], modules[i].sectionKey)
        }

        // 6. Test callback on the final BOOK module invokes onOpenBookHub
        lastItem.onClick()
        assertTrue("Clicking the final BOOK card must open BookHub", bookHubOpened)

        // 7. Test callback on other modules invokes onNavigate with correct destination
        modules[0].onClick()
        assertEquals("First module must navigate to Tables", Screen.Tables, navigatedScreen)

        modules[12].onClick()
        assertEquals("13th module must navigate to BCS Preparation", Screen.BcsPreparation, navigatedScreen)
    }

    @Test
    fun testBottomNavigationRetainsExactlyFiveCoreDestinationsExcludingBook() {
        // Core Bottom Navigation screens list
        val coreBottomNavScreens = listOf(
            Screen.Home,
            Screen.Tables,
            Screen.Dashboard,
            Screen.Bookmarks,
            Screen.Quizzes
        )

        // 1. Exactly 5 core destinations
        assertEquals("Bottom Navigation must retain exactly 5 core destinations", 5, coreBottomNavScreens.size)

        // 2. BookHub is NOT in bottom navigation destinations
        assertFalse(
            "Screen.BookHub must not be in Bottom Navigation",
            coreBottomNavScreens.contains(Screen.BookHub)
        )

        // 3. Bookmarks IS in bottom navigation destinations
        assertTrue(
            "Screen.Bookmarks must remain present in Bottom Navigation",
            coreBottomNavScreens.contains(Screen.Bookmarks)
        )

        // 4. Verify all 5 destinations navigate cleanly and maintain backstack
        viewModel.navigateToTab(Screen.Tables)
        assertEquals(Screen.Tables, viewModel.currentScreen)

        viewModel.navigateToTab(Screen.Dashboard)
        assertEquals(Screen.Dashboard, viewModel.currentScreen)

        viewModel.navigateToTab(Screen.Bookmarks)
        assertEquals(Screen.Bookmarks, viewModel.currentScreen)

        viewModel.navigateToTab(Screen.Quizzes)
        assertEquals(Screen.Quizzes, viewModel.currentScreen)

        viewModel.navigateToHome()
        assertEquals(Screen.Home, viewModel.currentScreen)
    }

    @Test
    fun testBookHubNavigationAndBackstackDeduplication() {
        // Navigate to BookHub (simulate opening from module_book_card at position 14)
        viewModel.openBookHub()
        assertEquals(Screen.BookHub, viewModel.currentScreen)

        // Navigate deeper into book details
        viewModel.openBookDetail(com.example.math.BookId.CLASS_6)
        assertTrue(viewModel.currentScreen is Screen.BookDetail)

        // Calling openBookHub again deduplicates the backstack
        viewModel.openBookHub()
        assertEquals(Screen.BookHub, viewModel.currentScreen)

        val bookHubCount = viewModel.screenStack.value.count { it == Screen.BookHub }
        assertEquals("Backstack must not duplicate BookHub", 1, bookHubCount)

        // Back navigation safely returns to Home
        val backSuccess = viewModel.navigateBack()
        assertTrue("Back navigation should succeed", backSuccess)
        assertEquals(Screen.Home, viewModel.currentScreen)
    }

    @Test
    fun testSectionKeyIntegrity() {
        val keys = SectionKey.entries
        assertEquals("SectionKey must contain 14 items", 14, keys.size)

        // BOOK is the final entry in SectionKey enum
        assertEquals("Final SectionKey must be BOOK", SectionKey.BOOK, keys.last())
        assertEquals("13th SectionKey must be BCS_MATH", SectionKey.BCS_MATH, keys[12])

        assertNotNull(SectionKey.BOOK.id)
        assertTrue(SectionKey.BOOK.titleBn.contains("পাঠ্যবই"))
    }

    @Test
    fun testSingleBookEntryAtPosition14AndNoDuplicateBookCards() {
        var bookOpened = false
        val modules = HomeScreenDefaults.createHomeModules(
            onOpenBookHub = { bookOpened = true },
            onNavigate = {}
        )

        // Exactly 14 modules in the grid
        assertEquals(14, modules.size)

        // Final item (index 13) is BOOK
        val finalModule = modules[13]
        assertEquals(SectionKey.BOOK, finalModule.sectionKey)
        assertEquals("module_book_card", finalModule.testTag)
        finalModule.onClick()
        assertTrue(bookOpened)

        // All non-14th items must NOT be BOOK
        val other13 = modules.take(13)
        assertFalse(other13.any { it.sectionKey == SectionKey.BOOK })
        assertFalse(other13.any { it.testTag == "module_book_card" })
    }

    @Test
    fun testAllFourteenHomeModulesHaveUniqueIconsAndValidMappings() {
        val modules = HomeScreenDefaults.createHomeModules(
            onOpenBookHub = {},
            onNavigate = {}
        )

        // 1. Verify exactly 14 modules
        assertEquals("Home screen modules must contain exactly 14 modules", 14, modules.size)

        // 2. Verify all modules have non-null icons
        for (module in modules) {
            assertNotNull("Module ${module.sectionKey.id} must have a non-null defaultIcon", module.defaultIcon)
        }

        // 3. Verify all 14 module defaultIcon names are 100% UNIQUE (no duplicate icons in Home Grid)
        val iconNames = modules.map { it.defaultIcon.name }
        val uniqueIconNames = iconNames.toSet()
        assertEquals(
            "Every Home Grid module must have a distinct icon glyph. Found duplicates: ${iconNames.groupBy { it }.filter { it.value.size > 1 }.keys}",
            14,
            uniqueIconNames.size
        )

        // 4. Verify all 14 SectionKey defaultIcon enums are 100% UNIQUE
        val sectionDefaultIcons = SectionKey.entries.map { it.defaultIcon }
        val uniqueSectionIcons = sectionDefaultIcons.toSet()
        assertEquals(
            "Every SectionKey must map to a unique default AvailableIcon",
            14,
            uniqueSectionIcons.size
        )

        // 5. Explicitly assert the resolution of historical duplicate pairs
        // Pair A: Module 01 (TABLES) vs Module 14 (BOOK)
        val tablesModule = modules.first { it.sectionKey == SectionKey.TABLES }
        val bookModule = modules.first { it.sectionKey == SectionKey.BOOK }
        assertNotEquals("Module 01 (TABLES) and Module 14 (BOOK) must NOT share an icon", tablesModule.defaultIcon.name, bookModule.defaultIcon.name)
        assertEquals(AvailableIcon.MULTIPLICATION_TABLE, SectionKey.TABLES.defaultIcon)
        assertEquals(AvailableIcon.MENU_BOOK, SectionKey.BOOK.defaultIcon)

        // Pair B: Module 03 (SQUARES) vs Module 10 (VISUAL_EXPLORER)
        val squaresModule = modules.first { it.sectionKey == SectionKey.SQUARES }
        val visualExplorerModule = modules.first { it.sectionKey == SectionKey.VISUAL_EXPLORER }
        assertNotEquals("Module 03 (SQUARES) and Module 10 (VISUAL_EXPLORER) must NOT share Straighten", squaresModule.defaultIcon.name, visualExplorerModule.defaultIcon.name)
        assertEquals(AvailableIcon.VIEW_IN_AR, SectionKey.SQUARES.defaultIcon)
        assertEquals(AvailableIcon.CATEGORY, SectionKey.VISUAL_EXPLORER.defaultIcon)

        // Pair C: Module 05 (FORMULA_LIBRARY) vs Module 13 (BCS_MATH)
        val formulaLibModule = modules.first { it.sectionKey == SectionKey.FORMULA_LIBRARY }
        val bcsMathModule = modules.first { it.sectionKey == SectionKey.BCS_MATH }
        assertNotEquals("Module 05 (FORMULA_LIBRARY) and Module 13 (BCS_MATH) must NOT share School", formulaLibModule.defaultIcon.name, bcsMathModule.defaultIcon.name)
        assertEquals(AvailableIcon.LIBRARY_BOOKS, SectionKey.FORMULA_LIBRARY.defaultIcon)
        assertEquals(AvailableIcon.SCHOOL, SectionKey.BCS_MATH.defaultIcon)

        // 6. Verify each module's defaultIcon in createHomeModules matches its SectionKey defaultIcon vector
        for (module in modules) {
            val expectedVector = module.sectionKey.defaultIcon.toImageVector()
            assertEquals(
                "Module ${module.sectionKey.name} defaultIcon must match SectionKey defaultIcon",
                expectedVector.name,
                module.defaultIcon.name
            )
        }

        // 7. Verify actual vector path artwork geometry (not just icon name) is 100% unique across all 14 modules
        fun extractPathGeometry(group: androidx.compose.ui.graphics.vector.VectorGroup): String {
            val sb = StringBuilder()
            for (i in 0 until group.size) {
                when (val node = group[i]) {
                    is androidx.compose.ui.graphics.vector.VectorPath -> {
                        sb.append(node.pathData.joinToString(",") { it.toString() }).append(";")
                    }
                    is androidx.compose.ui.graphics.vector.VectorGroup -> {
                        sb.append(extractPathGeometry(node))
                    }
                }
            }
            return sb.toString()
        }

        val vectorGeometries = modules.map { module ->
            val geometry = extractPathGeometry(module.defaultIcon.root)
            assertTrue("Module ${module.sectionKey.id} icon vector pathData must not be empty", geometry.isNotBlank())
            geometry
        }
        val uniqueVectorGeometries = vectorGeometries.toSet()
        assertEquals(
            "All 14 module icons must have 100% distinct underlying SVG/VectorPath artwork (no alias duplicates)",
            14,
            uniqueVectorGeometries.size
        )
    }

    @Test
    fun testPersistentStudySessionTimerLifecycleAndHomePersistence() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()

        // 1. Initial default state
        val initial = viewModel.studySessionTimer.value
        assertTrue("Study session timer should be visible on Home by default", initial.isVisibleOnHome)
        assertFalse("Study session timer should be paused initially", initial.isRunning)
        assertEquals(0, initial.currentSessionSeconds)
        assertEquals(30, initial.dailyGoalMinutes)

        // 2. Toggle visibility off and back on
        viewModel.toggleStudyTimerVisibility()
        assertFalse("Study timer should collapse when toggled", viewModel.studySessionTimer.value.isVisibleOnHome)
        viewModel.toggleStudyTimerVisibility()
        assertTrue("Study timer should expand when toggled back on", viewModel.studySessionTimer.value.isVisibleOnHome)

        // 3. Start timer and tick 900 seconds (15 minutes)
        viewModel.setStudyDailyGoalMinutes(15)
        viewModel.toggleStudySessionRunning()
        assertTrue("Timer should be running after toggle", viewModel.studySessionTimer.value.isRunning)

        viewModel.tickStudySessionTimer(900)
        val runningState = viewModel.studySessionTimer.value
        assertEquals(900, runningState.currentSessionSeconds)
        assertEquals(900, runningState.todayTotalSeconds)
        assertEquals(900, runningState.allTimeTotalSeconds)
        assertTrue("Daily goal of 15m (900s) should be reached", runningState.isDailyGoalReached)
        assertEquals(1.0f, runningState.dailyGoalProgressFraction, 0.001f)
        assertEquals("১৫:০০", com.example.ui.components.formatStudyTimerClockBn(runningState.currentSessionSeconds))

        // 4. Finish and save study session
        viewModel.finishAndSaveStudySession()
        val savedState = viewModel.studySessionTimer.value
        assertFalse("Timer should stop after finishing session", savedState.isRunning)
        assertEquals(0, savedState.currentSessionSeconds)
        assertEquals(900, savedState.todayTotalSeconds)
        assertEquals(1, savedState.completedSessionsToday)

        // 5. Verify persistence across a newly created ViewModel instance
        val restoredViewModel = MathViewModel(app)
        val restoredState = restoredViewModel.studySessionTimer.value
        assertEquals(900, restoredState.todayTotalSeconds)
        assertEquals(900, restoredState.allTimeTotalSeconds)
        assertEquals(15, restoredState.dailyGoalMinutes)
        assertEquals(1, restoredState.completedSessionsToday)
        assertTrue(restoredState.isVisibleOnHome)
    }

    @Test
    fun testHomeScreenScrollPositionPreservedAcrossAllModuleNavigationScenarios() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val freshViewModel = MathViewModel(app)

        // Scenario G — First Launch: initial scroll position is (0, 0)
        assertEquals(0, freshViewModel.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(0, freshViewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)
        assertEquals(Screen.Home, freshViewModel.currentScreen)

        // Simulate user scrolling down on Home Screen to lower module section (index = 9, pixel offset = 128)
        freshViewModel.updateHomeScrollPosition(firstVisibleItemIndex = 9, firstVisibleItemScrollOffset = 128)
        assertEquals(9, freshViewModel.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(128, freshViewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // Scenario A & B — Navigate from Home Grid to every single module and return via Back
        val allModuleScreens = listOf(
            Screen.Tables,
            Screen.Primes,
            Screen.SquaresCubes,
            Screen.Formulas,
            Screen.FormulaLibrary,
            Screen.PracticeLab,
            Screen.MentalMath,
            Screen.NumberSystem,
            Screen.ProblemSolver,
            Screen.VisualExplorer,
            Screen.Quizzes,
            Screen.Dashboard,
            Screen.BcsPreparation
        )

        for (moduleScreen in allModuleScreens) {
            freshViewModel.navigateTo(moduleScreen)
            assertEquals(moduleScreen, freshViewModel.currentScreen)
            // Scroll position must remain intact while HomeScreen is out of composition
            assertEquals(9, freshViewModel.homeScrollPosition.value.firstVisibleItemIndex)
            assertEquals(128, freshViewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)

            // Return to Home via navigateBack() (Toolbar Back / System Back)
            assertTrue(freshViewModel.navigateBack())
            assertEquals(Screen.Home, freshViewModel.currentScreen)
            assertEquals(
                "Scroll index must be preserved after returning from $moduleScreen",
                9,
                freshViewModel.homeScrollPosition.value.firstVisibleItemIndex
            )
            assertEquals(
                "Pixel scroll offset must be preserved after returning from $moduleScreen",
                128,
                freshViewModel.homeScrollPosition.value.firstVisibleItemScrollOffset
            )
        }

        // Scenario C — Multiple Module Navigation from restored position (including Module 14 BookHub)
        freshViewModel.openBookHub()
        assertEquals(Screen.BookHub, freshViewModel.currentScreen)
        assertTrue(freshViewModel.navigateBack())
        assertEquals(Screen.Home, freshViewModel.currentScreen)
        assertEquals(9, freshViewModel.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(128, freshViewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // Scenario D — Deep Module Navigation (Home -> BookHub -> BookDetail -> BookChapterOverview -> BookLesson -> Back to Home)
        freshViewModel.openBookHub()
        freshViewModel.openBookDetail(com.example.math.BookId.CLASS_9_10)
        freshViewModel.openBookChapter(com.example.math.BookId.CLASS_9_10, 1)
        freshViewModel.openBookLesson(com.example.math.BookId.CLASS_9_10, 1, "c910_1_l1")
        assertEquals(Screen.BookLesson, freshViewModel.currentScreen)
        assertEquals(5, freshViewModel.screenStack.value.size)

        while (freshViewModel.currentScreen != Screen.Home) {
            assertTrue(freshViewModel.navigateBack())
        }
        assertEquals(Screen.Home, freshViewModel.currentScreen)
        assertEquals(9, freshViewModel.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(128, freshViewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // Negative values are safely clamped to >= 0
        freshViewModel.updateHomeScrollPosition(-3, -50)
        assertEquals(0, freshViewModel.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(0, freshViewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)
    }

    @Test
    @Config(sdk = [36])
    fun testHomeScreenLiveComposeCompositionDisposalAndExactScrollRestoration() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val vm = androidx.lifecycle.ViewModelProvider(activity)[MathViewModel::class.java]
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        // 1. Scenario G — First launch: starts on Home at (0, 0)
        assertEquals(Screen.Home, vm.currentScreen)
        assertEquals(0, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(0, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // 2. Simulate scrolling HomeScreen LazyVerticalGrid down to lower modules (index = 8, pixel offset = 116)
        vm.updateHomeScrollPosition(firstVisibleItemIndex = 8, firstVisibleItemScrollOffset = 116)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        // 3. Scenario A & B — Navigate to Module 10 (VisualExplorer); HomeScreen leaves composition
        vm.navigateTo(Screen.VisualExplorer)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(Screen.VisualExplorer, vm.currentScreen)
        assertEquals(8, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(116, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // Return to Home via Back navigation -> HomeScreen re-enters composition and restores (8, 116)
        assertTrue(vm.navigateBack())
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(Screen.Home, vm.currentScreen)
        assertEquals(8, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(116, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // 4. Scenario C — Open a second module (Module 09 ProblemSolver) from restored position and return
        vm.navigateTo(Screen.ProblemSolver)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(Screen.ProblemSolver, vm.currentScreen)
        assertTrue(vm.navigateBack())
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(Screen.Home, vm.currentScreen)
        assertEquals(8, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(116, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // 5. Scenario E — Trigger Compose recompositions on HomeScreen (timer tick, theme update)
        vm.toggleStudySessionRunning()
        vm.tickStudySessionTimer(10)
        vm.setThemeMode(com.example.ui.theme.AppThemeMode.DARK)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(8, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(116, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)
        vm.setThemeMode(com.example.ui.theme.AppThemeMode.SYSTEM)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }
}
