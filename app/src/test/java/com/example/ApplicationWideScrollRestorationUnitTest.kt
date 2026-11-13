package com.example

import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.example.math.BookId
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.theme.AppThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Comprehensive Automated Regression Suite for BUG-36, BUG-37, BUG-38, and BUG-39:
 * Complete Application-Wide Navigation & Scroll State Restoration Architecture.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ApplicationWideScrollRestorationUnitTest {

    private lateinit var viewModel: MathViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        viewModel = MathViewModel(app)
    }

    @Test
    fun testScenarioA_FirstLaunchStartsAllDestinationsAtTopZeroOffset() {
        assertEquals(Screen.Home, viewModel.currentScreen)
        assertEquals(0, viewModel.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(0, viewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)

        val sampleKeys = listOf(
            "home:grid",
            "tables:grid",
            "primes:main",
            "squares_cubes:main",
            "formulas:main",
            "formula_library:main",
            "practice_lab:setup",
            "mental_math:setup",
            "number_system:tab_0",
            "problem_solver:mode_0",
            "visual_explorer:main",
            "quizzes:mode_0",
            "dashboard:main",
            "bcs_preparation:PRELIM_50:tab_0",
            "book_hub:main"
        )
        for (key in sampleKeys) {
            val pos = viewModel.getScrollPosition(key)
            assertEquals("Initial index for $key must be 0", 0, pos.firstVisibleItemIndex)
            assertEquals("Initial offset for $key must be 0", 0, pos.firstVisibleItemScrollOffset)
        }
    }

    @Test
    fun testScenarioB_HomeToModuleToBackPreservesExactScrollAcrossAll14Modules() {
        viewModel.updateScrollPosition("home:grid", 10, 144)
        assertEquals(10, viewModel.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(144, viewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)

        val modules = listOf(
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
            Screen.BcsPreparation,
            Screen.BookHub
        )

        for (module in modules) {
            if (module == Screen.BookHub) {
                viewModel.openBookHub()
            } else {
                viewModel.navigateTo(module)
            }
            assertEquals(module, viewModel.currentScreen)
            assertTrue(viewModel.navigateBack())
            assertEquals(Screen.Home, viewModel.currentScreen)
            assertEquals(10, viewModel.getScrollPosition("home:grid").firstVisibleItemIndex)
            assertEquals(144, viewModel.getScrollPosition("home:grid").firstVisibleItemScrollOffset)
            assertEquals(10, viewModel.homeScrollPosition.value.firstVisibleItemIndex)
            assertEquals(144, viewModel.homeScrollPosition.value.firstVisibleItemScrollOffset)
        }
    }

    @Test
    fun testScenarioC_ParentModuleToChildDestinationAndBackRestoresScrollAndContext() {
        // 1. Tables -> TableDetail(17) -> Back
        viewModel.navigateTo(Screen.Tables)
        viewModel.updateScrollPosition("tables:grid", 8, 92)
        viewModel.updateUiContextState("tables:selectedRangeFilter", 2)
        viewModel.openTable(17)
        assertEquals(Screen.TableDetail, viewModel.currentScreen)
        viewModel.updateScrollPosition("table_detail:17", 5, 48)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Tables, viewModel.currentScreen)
        assertEquals(8, viewModel.getScrollPosition("tables:grid").firstVisibleItemIndex)
        assertEquals(92, viewModel.getScrollPosition("tables:grid").firstVisibleItemScrollOffset)
        assertEquals(2, viewModel.getUiContextState("tables:selectedRangeFilter", 0))

        // 2. FormulaLibrary -> FormulaDetail -> Back
        viewModel.navigateTo(Screen.FormulaLibrary)
        viewModel.updateScrollPosition("formula_library:main", 14, 180)
        val firstFormulaId = "algebra_square_sum"
        viewModel.openFormula(firstFormulaId)
        assertEquals(Screen.FormulaDetail, viewModel.currentScreen)
        viewModel.updateScrollPosition("formula_detail:$firstFormulaId", 3, 64)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.FormulaLibrary, viewModel.currentScreen)
        assertEquals(14, viewModel.getScrollPosition("formula_library:main").firstVisibleItemIndex)
        assertEquals(180, viewModel.getScrollPosition("formula_library:main").firstVisibleItemScrollOffset)

        // 3. PracticeLab -> PracticeLabPlay -> Back
        viewModel.navigateTo(Screen.PracticeLab)
        viewModel.updateScrollPosition("practice_lab:setup", 6, 112)
        viewModel.startPracticeSession(PracticeCategory.entries.first(), PracticeDifficulty.MEDIUM)
        assertEquals(Screen.PracticeLabPlay, viewModel.currentScreen)
        viewModel.exitPracticeToCategories()
        assertEquals(Screen.PracticeLab, viewModel.currentScreen)
        assertEquals(6, viewModel.getScrollPosition("practice_lab:setup").firstVisibleItemIndex)
        assertEquals(112, viewModel.getScrollPosition("practice_lab:setup").firstVisibleItemScrollOffset)

        // 4. MentalMath -> MentalMathPlay -> Back
        viewModel.navigateTo(Screen.MentalMath)
        viewModel.updateScrollPosition("mental_math:setup", 5, 88)
        viewModel.startMentalMathChallenge(
            category = com.example.math.MentalMathCategory.entries.first(),
            difficulty = com.example.math.MentalMathDifficulty.EASY,
            mode = com.example.math.MentalMathMode.QUICK_CHALLENGE,
            durationOrCount = 60
        )
        assertEquals(Screen.MentalMathPlay, viewModel.currentScreen)
        viewModel.exitMentalMathToSetup()
        assertEquals(Screen.MentalMath, viewModel.currentScreen)
        assertEquals(5, viewModel.getScrollPosition("mental_math:setup").firstVisibleItemIndex)
        assertEquals(88, viewModel.getScrollPosition("mental_math:setup").firstVisibleItemScrollOffset)

        // 5. NumberSystem (Tab 3) -> NumberSystemPractice -> Back
        viewModel.navigateTo(Screen.NumberSystem)
        viewModel.updateUiContextState("number_system:selectedTabIndex", 3)
        viewModel.updateScrollPosition("number_system:tab_3", 7, 130)
        viewModel.startNumberSystemPractice()
        assertEquals(Screen.NumberSystemPractice, viewModel.currentScreen)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.NumberSystem, viewModel.currentScreen)
        assertEquals(3, viewModel.getUiContextState("number_system:selectedTabIndex", 0))
        assertEquals(7, viewModel.getScrollPosition("number_system:tab_3").firstVisibleItemIndex)
        assertEquals(130, viewModel.getScrollPosition("number_system:tab_3").firstVisibleItemScrollOffset)

        // 6. Bookmarks -> PracticeTutor -> Back
        viewModel.navigateTo(Screen.Bookmarks)
        viewModel.updateScrollPosition("bookmarks:main", 4, 76)
        viewModel.navigateTo(Screen.PracticeTutor)
        assertEquals(Screen.PracticeTutor, viewModel.currentScreen)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Bookmarks, viewModel.currentScreen)
        assertEquals(4, viewModel.getScrollPosition("bookmarks:main").firstVisibleItemIndex)
        assertEquals(76, viewModel.getScrollPosition("bookmarks:main").firstVisibleItemScrollOffset)
    }

    @Test
    fun testScenarioD_MultiLevelGrandchildNavigationChainRestoresEveryLevelOnBack() {
        // Level 0: Home
        viewModel.updateScrollPosition("home:grid", 11, 160)

        // Level 1: BookHub
        viewModel.openBookHub()
        assertEquals(Screen.BookHub, viewModel.currentScreen)
        viewModel.updateScrollPosition("book_hub:main", 3, 84)

        // Level 2: BookDetail (Class 8)
        viewModel.openBookDetail(BookId.CLASS_8)
        assertEquals(Screen.BookDetail, viewModel.currentScreen)
        assertEquals("book_detail:class_8", viewModel.getDestinationStateKey(Screen.BookDetail))
        viewModel.updateScrollPosition("book_detail:class_8:chapters", 6, 120)

        // Level 3: BookChapterOverview (Class 8, Chapter 4, Tab 2)
        viewModel.openBookChapter(BookId.CLASS_8, 4)
        assertEquals(Screen.BookChapterOverview, viewModel.currentScreen)
        assertEquals("book_chapter_overview:class_8:4", viewModel.getDestinationStateKey(Screen.BookChapterOverview))
        viewModel.updateUiContextState("book_chapter_overview:class_8:4:selectedTab", 2)
        viewModel.updateScrollPosition("book_chapter_overview:class_8:4:tab_2", 5, 95)

        // Level 4: BookLesson (Class 8, Chapter 4, Lesson 1)
        viewModel.openBookLesson(BookId.CLASS_8, 4, "c8_ch4_l1")
        assertEquals(Screen.BookLesson, viewModel.currentScreen)
        assertEquals("book_lesson:class_8:4:c8_ch4_l1", viewModel.getDestinationStateKey(Screen.BookLesson))
        viewModel.updateScrollPosition("book_lesson:class_8:4:c8_ch4_l1", 4, 70)

        // Level 5: BookPdfReader (Class 8, Page 52)
        viewModel.openBookPdfReader(BookId.CLASS_8, printedPage = 52, chapterNumber = 4)
        assertEquals(Screen.BookPdfReader, viewModel.currentScreen)
        assertEquals("book_pdf_reader:class_8:52", viewModel.getDestinationStateKey(Screen.BookPdfReader))
        viewModel.updateScrollPosition("book_pdf_reader:class_8:52", 2, 44)

        // Pop Level 5 -> Level 4 (BookLesson)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.BookLesson, viewModel.currentScreen)
        assertEquals(BookId.CLASS_8, viewModel.selectedBookId.value)
        assertEquals(4, viewModel.selectedBookChapterNumber.value)
        assertEquals("c8_ch4_l1", viewModel.selectedBookLessonId.value)
        assertEquals(4, viewModel.getScrollPosition("book_lesson:class_8:4:c8_ch4_l1").firstVisibleItemIndex)
        assertEquals(70, viewModel.getScrollPosition("book_lesson:class_8:4:c8_ch4_l1").firstVisibleItemScrollOffset)

        // Pop Level 4 -> Level 3 (BookChapterOverview)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.BookChapterOverview, viewModel.currentScreen)
        assertEquals(BookId.CLASS_8, viewModel.selectedBookId.value)
        assertEquals(4, viewModel.selectedBookChapterNumber.value)
        assertEquals(2, viewModel.getUiContextState("book_chapter_overview:class_8:4:selectedTab", 0))
        assertEquals(5, viewModel.getScrollPosition("book_chapter_overview:class_8:4:tab_2").firstVisibleItemIndex)
        assertEquals(95, viewModel.getScrollPosition("book_chapter_overview:class_8:4:tab_2").firstVisibleItemScrollOffset)

        // Pop Level 3 -> Level 2 (BookDetail)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.BookDetail, viewModel.currentScreen)
        assertEquals(BookId.CLASS_8, viewModel.selectedBookId.value)
        assertEquals(6, viewModel.getScrollPosition("book_detail:class_8:chapters").firstVisibleItemIndex)
        assertEquals(120, viewModel.getScrollPosition("book_detail:class_8:chapters").firstVisibleItemScrollOffset)

        // Pop Level 2 -> Level 1 (BookHub)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.BookHub, viewModel.currentScreen)
        assertEquals(3, viewModel.getScrollPosition("book_hub:main").firstVisibleItemIndex)
        assertEquals(84, viewModel.getScrollPosition("book_hub:main").firstVisibleItemScrollOffset)

        // Pop Level 1 -> Level 0 (Home)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen)
        assertEquals(11, viewModel.getScrollPosition("home:grid").firstVisibleItemIndex)
        assertEquals(160, viewModel.getScrollPosition("home:grid").firstVisibleItemScrollOffset)
    }

    @Test
    fun testScenarioE_ParameterizedDestinationsMaintainIndependentScrollStatePerArgumentId() {
        // Class 6 vs Class 9-10 BookDetail chapters scroll positions
        viewModel.openBookDetail(BookId.CLASS_6)
        val keyClass6 = viewModel.getDestinationStateKey(Screen.BookDetail)
        viewModel.updateScrollPosition("book_detail:class_6:chapters", 7, 115)

        viewModel.openBookDetail(BookId.CLASS_9_10)
        val keyClass910 = viewModel.getDestinationStateKey(Screen.BookDetail)
        assertNotEquals("Different BookIds must produce distinct SaveableStateProvider keys", keyClass6, keyClass910)
        assertEquals(0, viewModel.getScrollPosition("book_detail:class_9_10:chapters").firstVisibleItemIndex)

        viewModel.updateScrollPosition("book_detail:class_9_10:chapters", 13, 210)
        assertEquals(7, viewModel.getScrollPosition("book_detail:class_6:chapters").firstVisibleItemIndex)
        assertEquals(115, viewModel.getScrollPosition("book_detail:class_6:chapters").firstVisibleItemScrollOffset)
        assertEquals(13, viewModel.getScrollPosition("book_detail:class_9_10:chapters").firstVisibleItemIndex)
        assertEquals(210, viewModel.getScrollPosition("book_detail:class_9_10:chapters").firstVisibleItemScrollOffset)

        // TableDetail(7) vs TableDetail(19)
        viewModel.openTable(7)
        val keyTable7 = viewModel.getDestinationStateKey(Screen.TableDetail)
        viewModel.updateScrollPosition("table_detail:7", 4, 55)

        viewModel.openTable(19)
        val keyTable19 = viewModel.getDestinationStateKey(Screen.TableDetail)
        assertNotEquals(keyTable7, keyTable19)
        assertEquals(0, viewModel.getScrollPosition("table_detail:19").firstVisibleItemIndex)
        assertEquals(4, viewModel.getScrollPosition("table_detail:7").firstVisibleItemIndex)
    }

    @Test
    fun testScenarioF_BottomNavigationTabsPreserveScrollAndSubTabStateAcrossSwitches() {
        // Scroll Home
        viewModel.updateScrollPosition("home:grid", 8, 120)

        // Switch to Tables tab and scroll
        viewModel.navigateToTab(Screen.Tables)
        assertEquals(Screen.Tables, viewModel.currentScreen)
        viewModel.updateScrollPosition("tables:grid", 11, 90)
        viewModel.updateUiContextState("tables:selectedRangeFilter", 1)

        // Switch to Dashboard tab and scroll
        viewModel.navigateToTab(Screen.Dashboard)
        assertEquals(Screen.Dashboard, viewModel.currentScreen)
        viewModel.updateScrollPosition("dashboard:main", 5, 140)
        viewModel.updateUiContextState("dashboard:selectedModuleFilter", "QUIZ")

        // Switch to Bookmarks tab and scroll
        viewModel.navigateToTab(Screen.Bookmarks)
        assertEquals(Screen.Bookmarks, viewModel.currentScreen)
        viewModel.updateScrollPosition("bookmarks:main", 3, 60)

        // Switch to Quizzes tab, select Mode 2 (Custom Builder), and scroll
        viewModel.navigateToTab(Screen.Quizzes)
        assertEquals(Screen.Quizzes, viewModel.currentScreen)
        viewModel.updateUiContextState("quizzes:selectedSetupMode", 2)
        viewModel.updateScrollPosition("quizzes:mode_2", 6, 105)

        // Switch back through each bottom-navigation tab and verify all states remain intact (BUG-39)
        viewModel.navigateToTab(Screen.Tables)
        assertEquals(11, viewModel.getScrollPosition("tables:grid").firstVisibleItemIndex)
        assertEquals(90, viewModel.getScrollPosition("tables:grid").firstVisibleItemScrollOffset)
        assertEquals(1, viewModel.getUiContextState("tables:selectedRangeFilter", 0))

        viewModel.navigateToTab(Screen.Dashboard)
        assertEquals(5, viewModel.getScrollPosition("dashboard:main").firstVisibleItemIndex)
        assertEquals(140, viewModel.getScrollPosition("dashboard:main").firstVisibleItemScrollOffset)
        assertEquals("QUIZ", viewModel.getUiContextState("dashboard:selectedModuleFilter", "ALL"))

        viewModel.navigateToTab(Screen.Bookmarks)
        assertEquals(3, viewModel.getScrollPosition("bookmarks:main").firstVisibleItemIndex)
        assertEquals(60, viewModel.getScrollPosition("bookmarks:main").firstVisibleItemScrollOffset)

        viewModel.navigateToTab(Screen.Quizzes)
        assertEquals(2, viewModel.getUiContextState("quizzes:selectedSetupMode", 0))
        assertEquals(6, viewModel.getScrollPosition("quizzes:mode_2").firstVisibleItemIndex)
        assertEquals(105, viewModel.getScrollPosition("quizzes:mode_2").firstVisibleItemScrollOffset)

        viewModel.navigateToHome()
        assertEquals(8, viewModel.getScrollPosition("home:grid").firstVisibleItemIndex)
        assertEquals(120, viewModel.getScrollPosition("home:grid").firstVisibleItemScrollOffset)
    }

    @Test
    fun testScenarioG_InternalModuleTabsPreserveIndependentScrollAndFilters() {
        // BCS Preparation: 5 internal tabs (0..4) scoped by exam profile
        viewModel.navigateTo(Screen.BcsPreparation)
        val profileId = viewModel.selectedBcsExamProfile.value.id

        viewModel.updateUiContextState("bcs_preparation:$profileId:selectedTab", 1)
        viewModel.updateScrollPosition("bcs_preparation:$profileId:tab_1", 4, 88)

        viewModel.updateUiContextState("bcs_preparation:$profileId:selectedTab", 3)
        viewModel.updateScrollPosition("bcs_preparation:$profileId:tab_3", 9, 175)

        // Open child screen (FormulaDetail) from BCS Tab 3 and return via Back
        val formulaId = "algebra_square_sum"
        viewModel.openFormula(formulaId)
        assertEquals(Screen.FormulaDetail, viewModel.currentScreen)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.BcsPreparation, viewModel.currentScreen)

        // Both Tab 3 (active) and Tab 1 (background) retain their independent scroll positions
        assertEquals(3, viewModel.getUiContextState("bcs_preparation:$profileId:selectedTab", 0))
        assertEquals(9, viewModel.getScrollPosition("bcs_preparation:$profileId:tab_3").firstVisibleItemIndex)
        assertEquals(175, viewModel.getScrollPosition("bcs_preparation:$profileId:tab_3").firstVisibleItemScrollOffset)
        assertEquals(4, viewModel.getScrollPosition("bcs_preparation:$profileId:tab_1").firstVisibleItemIndex)
        assertEquals(88, viewModel.getScrollPosition("bcs_preparation:$profileId:tab_1").firstVisibleItemScrollOffset)

        // VisualExplorer: Lab category selection & scroll preserved when opening ProblemSolver
        viewModel.navigateTo(Screen.VisualExplorer)
        viewModel.updateUiContextState("visual_explorer:selectedCategory", "GEOMETRY")
        viewModel.updateScrollPosition("visual_explorer:main", 5, 110)
        viewModel.navigateTo(Screen.ProblemSolver)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.VisualExplorer, viewModel.currentScreen)
        assertEquals(
            "GEOMETRY",
            viewModel.getUiContextState("visual_explorer:selectedCategory", "")
        )
        assertEquals(5, viewModel.getScrollPosition("visual_explorer:main").firstVisibleItemIndex)
        assertEquals(110, viewModel.getScrollPosition("visual_explorer:main").firstVisibleItemScrollOffset)
    }

    @Test
    fun testScenarioH_RepeatedNavigationCyclesAndBoundedLruCacheStability() {
        viewModel.navigateTo(Screen.BookHub)
        viewModel.updateScrollPosition("book_hub:main", 2, 65)

        // 5 repeated cycles into BookDetail and back
        repeat(5) { cycle ->
            viewModel.openBookDetail(BookId.CLASS_7)
            assertEquals(Screen.BookDetail, viewModel.currentScreen)
            viewModel.updateScrollPosition("book_detail:class_7:chapters", 5 + cycle, 40 + cycle)
            assertTrue(viewModel.navigateBack())
            assertEquals(Screen.BookHub, viewModel.currentScreen)
            assertEquals(2, viewModel.getScrollPosition("book_hub:main").firstVisibleItemIndex)
            assertEquals(65, viewModel.getScrollPosition("book_hub:main").firstVisibleItemScrollOffset)
        }

        // Verify LRU bound (300 entries max) does not grow unboundedly
        for (i in 1..350) {
            viewModel.updateScrollPosition("synthetic_key_$i", i, i * 2)
        }
        assertTrue(
            "Scroll position cache must be bounded to <= 300 entries",
            viewModel.scrollPositions.value.size <= 300
        )
    }

    @Test
    @Config(sdk = [36])
    fun testScenarioI_LiveActivityMultiLevelNavigationTabSwitchingAndRecreation() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val vm = ViewModelProvider(activity)[MathViewModel::class.java]
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // 1. Scroll Home Screen and open BookHub -> BookDetail -> BookChapterOverview -> Back chain
        vm.updateScrollPosition("home:grid", 9, 132)
        vm.openBookHub()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        vm.updateScrollPosition("book_hub:main", 3, 78)

        vm.openBookDetail(BookId.CLASS_6)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        vm.updateScrollPosition("book_detail:class_6:chapters", 5, 96)

        vm.openBookChapter(BookId.CLASS_6, 2)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.BookChapterOverview, vm.currentScreen)

        // Trigger study timer ticks and theme change while inside nested screen
        vm.toggleStudySessionRunning()
        vm.tickStudySessionTimer(15)
        vm.setThemeMode(AppThemeMode.DARK)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Navigate back step-by-step and verify every parent screen restores its exact scroll state
        assertTrue(vm.navigateBack())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.BookDetail, vm.currentScreen)
        assertEquals(5, vm.getScrollPosition("book_detail:class_6:chapters").firstVisibleItemIndex)
        assertEquals(96, vm.getScrollPosition("book_detail:class_6:chapters").firstVisibleItemScrollOffset)

        assertTrue(vm.navigateBack())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.BookHub, vm.currentScreen)
        assertEquals(3, vm.getScrollPosition("book_hub:main").firstVisibleItemIndex)
        assertEquals(78, vm.getScrollPosition("book_hub:main").firstVisibleItemScrollOffset)

        assertTrue(vm.navigateBack())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Home, vm.currentScreen)
        assertEquals(9, vm.getScrollPosition("home:grid").firstVisibleItemIndex)
        assertEquals(132, vm.getScrollPosition("home:grid").firstVisibleItemScrollOffset)

        // 2. Switch across Bottom Navigation tabs in live Compose tree
        vm.navigateToTab(Screen.Tables)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        vm.updateScrollPosition("tables:grid", 6, 84)

        vm.navigateToTab(Screen.Dashboard)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        vm.updateScrollPosition("dashboard:main", 4, 110)

        vm.navigateToTab(Screen.Tables)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(6, vm.getScrollPosition("tables:grid").firstVisibleItemIndex)
        assertEquals(84, vm.getScrollPosition("tables:grid").firstVisibleItemScrollOffset)

        // 3. Recreate Activity (configuration change simulation) and verify ViewModel state survives
        controller.recreate()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val recreatedVm = ViewModelProvider(controller.get())[MathViewModel::class.java]
        assertEquals(6, recreatedVm.getScrollPosition("tables:grid").firstVisibleItemIndex)
        assertEquals(84, recreatedVm.getScrollPosition("tables:grid").firstVisibleItemScrollOffset)
        assertEquals(9, recreatedVm.getScrollPosition("home:grid").firstVisibleItemIndex)
        assertEquals(132, recreatedVm.getScrollPosition("home:grid").firstVisibleItemScrollOffset)

        recreatedVm.setThemeMode(AppThemeMode.SYSTEM)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    @Config(sdk = [36])
    fun testScenarioJ_Full14ModulesAnd31DestinationsLiveMainActivityInteractiveAcceptance() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val vm = ViewModelProvider(activity)[MathViewModel::class.java]
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val visitedDestinations = mutableSetOf<String>()
        fun recordCurrentScreen() {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(50, java.util.concurrent.TimeUnit.MILLISECONDS)
            visitedDestinations.add(vm.currentScreen.route)
        }

        // Destination 1: Home
        recordCurrentScreen()
        vm.updateScrollPosition("home:grid", 8, 124)

        // Module 1: TABLES (Tables -> TableDetail -> PracticeTutor -> System Back -> Toolbar Back)
        vm.navigateTo(Screen.Tables)
        recordCurrentScreen()
        vm.updateScrollPosition("tables:grid", 7, 96)
        vm.updateUiContextState("tables:selectedRangeFilter", 1)
        vm.openTable(16)
        recordCurrentScreen()
        assertEquals("table_detail:16", vm.getDestinationStateKey(Screen.TableDetail))
        vm.updateScrollPosition("table_detail:16", 4, 60)
        vm.startTutor(16)
        recordCurrentScreen()
        assertEquals("practice_tutor:16", vm.getDestinationStateKey(Screen.PracticeTutor))
        vm.setTutorNumber(18)
        assertEquals("practice_tutor:18", vm.getDestinationStateKey(Screen.PracticeTutor))
        // Android System Back from PracticeTutor -> TableDetail(16)
        activity.onBackPressedDispatcher.onBackPressed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.TableDetail, vm.currentScreen)
        assertEquals(16, vm.selectedTableNumber.value)
        assertEquals(4, vm.getScrollPosition("table_detail:16").firstVisibleItemIndex)
        assertEquals(60, vm.getScrollPosition("table_detail:16").firstVisibleItemScrollOffset)
        // Toolbar Back from TableDetail -> Tables
        assertTrue(vm.navigateBack())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Tables, vm.currentScreen)
        assertEquals(7, vm.getScrollPosition("tables:grid").firstVisibleItemIndex)
        assertEquals(96, vm.getScrollPosition("tables:grid").firstVisibleItemScrollOffset)
        assertEquals(1, vm.getUiContextState("tables:selectedRangeFilter", 0))
        // System Back from Tables -> Home
        activity.onBackPressedDispatcher.onBackPressed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Home, vm.currentScreen)
        assertEquals(8, vm.getScrollPosition("home:grid").firstVisibleItemIndex)
        assertEquals(124, vm.getScrollPosition("home:grid").firstVisibleItemScrollOffset)

        // Module 2: PRIMES (Primes Tab 1 -> child navigation -> Back)
        vm.navigateTo(Screen.Primes)
        recordCurrentScreen()
        vm.updateUiContextState("primes:selectedTabIndex", 1)
        vm.updateScrollPosition("primes:tab_1", 5, 82)
        vm.navigateTo(Screen.ProblemSolver)
        recordCurrentScreen()
        activity.onBackPressedDispatcher.onBackPressed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Primes, vm.currentScreen)
        assertEquals(1, vm.getUiContextState("primes:selectedTabIndex", 0))
        assertEquals(5, vm.getScrollPosition("primes:tab_1").firstVisibleItemIndex)
        assertEquals(82, vm.getScrollPosition("primes:tab_1").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 3: SQUARES (SquaresCubes Tab 1 -> child navigation -> Back)
        vm.navigateTo(Screen.SquaresCubes)
        recordCurrentScreen()
        vm.updateUiContextState("squares_cubes:selectedTabIndex", 1)
        vm.updateScrollPosition("squares_cubes:tab_1", 9, 140)
        vm.navigateTo(Screen.ProblemSolver)
        assertTrue(vm.navigateBack())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.SquaresCubes, vm.currentScreen)
        assertEquals(1, vm.getUiContextState("squares_cubes:selectedTabIndex", 0))
        assertEquals(9, vm.getScrollPosition("squares_cubes:tab_1").firstVisibleItemIndex)
        assertEquals(140, vm.getScrollPosition("squares_cubes:tab_1").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 4: FORMULAS (Formulas -> FormulaDetail -> Back)
        vm.navigateTo(Screen.Formulas)
        recordCurrentScreen()
        vm.updateScrollPosition("formulas:main", 6, 108)
        vm.openFormula("algebra_square_sum")
        recordCurrentScreen()
        assertEquals("formula_detail:algebra_square_sum", vm.getDestinationStateKey(Screen.FormulaDetail))
        vm.updateScrollPosition("formula_detail:algebra_square_sum", 3, 52)
        activity.onBackPressedDispatcher.onBackPressed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Formulas, vm.currentScreen)
        assertEquals(6, vm.getScrollPosition("formulas:main").firstVisibleItemIndex)
        assertEquals(108, vm.getScrollPosition("formulas:main").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 5: FORMULA_LIBRARY (FormulaLibrary -> Subject Filter -> FormulaDetail -> Back)
        vm.navigateTo(Screen.FormulaLibrary)
        recordCurrentScreen()
        vm.updateUiContextState("formula_library:selectedSubject", com.example.math.FormulaSubject.GEOMETRY)
        vm.updateScrollPosition("formula_library:main", 11, 164)
        vm.openFormula("algebra_cube_sum")
        recordCurrentScreen()
        assertEquals("formula_detail:algebra_cube_sum", vm.getDestinationStateKey(Screen.FormulaDetail))
        assertTrue(vm.navigateBack())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.FormulaLibrary, vm.currentScreen)
        assertEquals(com.example.math.FormulaSubject.GEOMETRY, vm.getUiContextState<com.example.math.FormulaSubject?>("formula_library:selectedSubject", null))
        assertEquals(11, vm.getScrollPosition("formula_library:main").firstVisibleItemIndex)
        assertEquals(164, vm.getScrollPosition("formula_library:main").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 6: PRACTICE (PracticeLab -> PracticeLabPlay -> Back)
        vm.navigateTo(Screen.PracticeLab)
        recordCurrentScreen()
        vm.updateScrollPosition("practice_lab:setup", 4, 92)
        vm.startPracticeSession(PracticeCategory.entries.first(), PracticeDifficulty.MEDIUM)
        recordCurrentScreen()
        assertEquals(Screen.PracticeLabPlay, vm.currentScreen)
        vm.exitPracticeToCategories()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.PracticeLab, vm.currentScreen)
        assertEquals(4, vm.getScrollPosition("practice_lab:setup").firstVisibleItemIndex)
        assertEquals(92, vm.getScrollPosition("practice_lab:setup").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 7: MENTAL_MATH (MentalMath -> MentalMathPlay -> Back)
        vm.navigateTo(Screen.MentalMath)
        recordCurrentScreen()
        vm.updateScrollPosition("mental_math:setup", 5, 88)
        vm.startMentalMathChallenge(
            category = com.example.math.MentalMathCategory.entries.first(),
            difficulty = com.example.math.MentalMathDifficulty.EASY,
            mode = com.example.math.MentalMathMode.QUICK_CHALLENGE,
            durationOrCount = 60
        )
        recordCurrentScreen()
        assertEquals(Screen.MentalMathPlay, vm.currentScreen)
        vm.exitMentalMathToSetup()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.MentalMath, vm.currentScreen)
        assertEquals(5, vm.getScrollPosition("mental_math:setup").firstVisibleItemIndex)
        assertEquals(88, vm.getScrollPosition("mental_math:setup").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 8: NUMBER_SYSTEM (NumberSystem Tab 3 -> NumberSystemPractice -> Back)
        vm.navigateTo(Screen.NumberSystem)
        recordCurrentScreen()
        vm.updateUiContextState("number_system:selectedTabIndex", 3)
        vm.updateScrollPosition("number_system:tab_3", 6, 118)
        vm.startNumberSystemPractice()
        recordCurrentScreen()
        assertEquals(Screen.NumberSystemPractice, vm.currentScreen)
        activity.onBackPressedDispatcher.onBackPressed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.NumberSystem, vm.currentScreen)
        assertEquals(3, vm.getUiContextState("number_system:selectedTabIndex", 0))
        assertEquals(6, vm.getScrollPosition("number_system:tab_3").firstVisibleItemIndex)
        assertEquals(118, vm.getScrollPosition("number_system:tab_3").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 9: PROBLEM_SOLVER & Module 10: VISUAL_EXPLORER
        vm.navigateTo(Screen.VisualExplorer)
        recordCurrentScreen()
        vm.updateUiContextState("visual_explorer:selectedSection", com.example.ui.screens.VisualExplorerSection.ALGEBRA)
        vm.updateScrollPosition("visual_explorer:main", 4, 74)
        vm.navigateTo(Screen.ProblemSolver)
        recordCurrentScreen()
        vm.updateUiContextState("problem_solver:selectedModeIndex", 2)
        vm.updateScrollPosition("problem_solver:mode_2", 3, 66)
        activity.onBackPressedDispatcher.onBackPressed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.VisualExplorer, vm.currentScreen)
        assertEquals(com.example.ui.screens.VisualExplorerSection.ALGEBRA, vm.getUiContextState("visual_explorer:selectedSection", com.example.ui.screens.VisualExplorerSection.GEOMETRY))
        assertEquals(4, vm.getScrollPosition("visual_explorer:main").firstVisibleItemIndex)
        assertEquals(74, vm.getScrollPosition("visual_explorer:main").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 11: QUIZZES (Quizzes Mode 1 -> QuizPlay -> Back)
        vm.navigateTo(Screen.Quizzes)
        recordCurrentScreen()
        vm.updateUiContextState("quizzes:selectedSetupMode", 1)
        vm.updateScrollPosition("quizzes:mode_1", 5, 102)
        vm.startQuiz(
            topic = com.example.math.QuizTopic.MULTIPLICATION,
            mode = com.example.math.QuizMode.TOPIC,
            difficulty = PracticeDifficulty.EASY
        )
        vm.navigateTo(Screen.QuizPlay)
        recordCurrentScreen()
        assertEquals(Screen.QuizPlay, vm.currentScreen)
        assertTrue(vm.navigateBack())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Quizzes, vm.currentScreen)
        assertEquals(1, vm.getUiContextState("quizzes:selectedSetupMode", 0))
        assertEquals(5, vm.getScrollPosition("quizzes:mode_1").firstVisibleItemIndex)
        assertEquals(102, vm.getScrollPosition("quizzes:mode_1").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 12: DASHBOARD & Bookmarks & Customize
        vm.navigateTo(Screen.Dashboard)
        recordCurrentScreen()
        vm.updateUiContextState("dashboard:selectedModuleFilter", "BCS")
        vm.updateScrollPosition("dashboard:main", 7, 136)
        vm.navigateTo(Screen.Bookmarks)
        recordCurrentScreen()
        vm.updateScrollPosition("bookmarks:main", 3, 58)
        vm.navigateTo(Screen.Customize)
        recordCurrentScreen()
        vm.updateScrollPosition("customize:controls", 0, 145)
        assertTrue(vm.navigateBack()) // Customize -> Bookmarks
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Bookmarks, vm.currentScreen)
        assertEquals(3, vm.getScrollPosition("bookmarks:main").firstVisibleItemIndex)
        assertTrue(vm.navigateBack()) // Bookmarks -> Dashboard
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Dashboard, vm.currentScreen)
        assertEquals("BCS", vm.getUiContextState("dashboard:selectedModuleFilter", "ALL"))
        assertEquals(7, vm.getScrollPosition("dashboard:main").firstVisibleItemIndex)
        assertEquals(136, vm.getScrollPosition("dashboard:main").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack()) // Dashboard -> Home

        // Module 13: BCS_PREPARATION (Profile -> Mathematics/Guide Tab 1 -> FormulaDetail -> Back)
        vm.navigateTo(Screen.BcsPreparation)
        recordCurrentScreen()
        val bcsProfileId = vm.selectedBcsExamProfile.value.id
        vm.updateUiContextState("bcs_preparation:$bcsProfileId:selectedTab", 1)
        vm.updateScrollPosition("bcs_preparation:$bcsProfileId:tab_1", 6, 128)
        vm.openFormula("algebra_square_diff")
        recordCurrentScreen()
        activity.onBackPressedDispatcher.onBackPressed()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.BcsPreparation, vm.currentScreen)
        assertEquals(1, vm.getUiContextState("bcs_preparation:$bcsProfileId:selectedTab", 0))
        assertEquals(6, vm.getScrollPosition("bcs_preparation:$bcsProfileId:tab_1").firstVisibleItemIndex)
        assertEquals(128, vm.getScrollPosition("bcs_preparation:$bcsProfileId:tab_1").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack())

        // Module 14: BOOK (BookHub -> BookDetail -> BookChapterOverview -> BookLesson -> BookPdfReader [page turn 40->45] -> BookInteractiveLab -> BookPractice -> BookRevision -> Back chain)
        vm.openBookHub()
        recordCurrentScreen()
        vm.updateScrollPosition("book_hub:main", 2, 70)

        vm.openBookDetail(BookId.CLASS_7)
        recordCurrentScreen()
        vm.updateScrollPosition("book_detail:class_7:chapters", 5, 112)

        vm.openBookChapter(BookId.CLASS_7, 3)
        recordCurrentScreen()
        vm.updateUiContextState("book_chapter_overview:class_7:3:selectedTab", 1)
        vm.updateScrollPosition("book_chapter_overview:class_7:3:tab_1", 4, 86)

        vm.openBookLesson(BookId.CLASS_7, 3, "c7_ch3_l1")
        recordCurrentScreen()
        vm.updateScrollPosition("book_lesson:class_7:3:c7_ch3_l1", 3, 64)

        vm.openBookPdfReader(BookId.CLASS_7, printedPage = 40, chapterNumber = 3)
        recordCurrentScreen()
        assertEquals("book_pdf_reader:class_7:40", vm.getDestinationStateKey(Screen.BookPdfReader))
        // Turn page to 45 inside BookPdfReader and scroll page 45
        vm.updateBookReaderPage(BookId.CLASS_7, printedPage = 45, chapterNumber = 3)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals("book_pdf_reader:class_7:45", vm.getDestinationStateKey(Screen.BookPdfReader))
        vm.updateScrollPosition("book_pdf_reader:class_7:45", 2, 54)

        val labType = com.example.math.BookInteractiveLabType.NUMBER_LINE_LAB
        vm.openBookInteractiveLab(labType, BookId.CLASS_7, 3)
        recordCurrentScreen()
        vm.updateScrollPosition("book_interactive_lab:class_7:3:${labType.id}", 0, 95)

        vm.startBookPractice(BookId.CLASS_7, 3)
        recordCurrentScreen()

        vm.openBookRevision(BookId.CLASS_7)
        recordCurrentScreen()
        vm.updateScrollPosition("book_revision:class_7:tab_0", 2, 48)

        // Pop BookRevision -> BookPractice -> BookInteractiveLab -> BookPdfReader (must restore page 45 and offset 54!)
        assertTrue(vm.navigateBack()) // -> BookPractice
        assertEquals(Screen.BookPractice, vm.currentScreen)
        assertTrue(vm.navigateBack()) // -> BookInteractiveLab
        assertEquals(Screen.BookInteractiveLab, vm.currentScreen)
        assertEquals(95, vm.getScrollPosition("book_interactive_lab:class_7:3:${labType.id}").firstVisibleItemScrollOffset)
        assertTrue(vm.navigateBack()) // -> BookPdfReader
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.BookPdfReader, vm.currentScreen)
        assertEquals(BookId.CLASS_7, vm.selectedBookId.value)
        assertEquals(45, vm.selectedBookPrintedPage.value)
        assertEquals("book_pdf_reader:class_7:45", vm.getDestinationStateKey(Screen.BookPdfReader))
        assertEquals(2, vm.getScrollPosition("book_pdf_reader:class_7:45").firstVisibleItemIndex)
        assertEquals(54, vm.getScrollPosition("book_pdf_reader:class_7:45").firstVisibleItemScrollOffset)

        // Pop BookPdfReader -> BookLesson -> BookChapterOverview -> BookDetail -> BookHub -> Home
        assertTrue(vm.navigateBack()) // -> BookLesson
        assertEquals(Screen.BookLesson, vm.currentScreen)
        assertEquals(3, vm.getScrollPosition("book_lesson:class_7:3:c7_ch3_l1").firstVisibleItemIndex)
        assertTrue(vm.navigateBack()) // -> BookChapterOverview
        assertEquals(Screen.BookChapterOverview, vm.currentScreen)
        assertEquals(1, vm.getUiContextState("book_chapter_overview:class_7:3:selectedTab", 0))
        assertEquals(4, vm.getScrollPosition("book_chapter_overview:class_7:3:tab_1").firstVisibleItemIndex)
        assertTrue(vm.navigateBack()) // -> BookDetail
        assertEquals(Screen.BookDetail, vm.currentScreen)
        assertEquals(5, vm.getScrollPosition("book_detail:class_7:chapters").firstVisibleItemIndex)
        assertTrue(vm.navigateBack()) // -> BookHub
        assertEquals(Screen.BookHub, vm.currentScreen)
        assertEquals(2, vm.getScrollPosition("book_hub:main").firstVisibleItemIndex)
        assertTrue(vm.navigateBack()) // -> Home
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Screen.Home, vm.currentScreen)
        assertEquals(8, vm.getScrollPosition("home:grid").firstVisibleItemIndex)
        assertEquals(124, vm.getScrollPosition("home:grid").firstVisibleItemScrollOffset)

        // Confirm all 31 distinct production screen routes were mounted and verified in MainActivity
        assertEquals("All 31 production destinations must be mounted and tested", 31, visitedDestinations.size)
    }
}
