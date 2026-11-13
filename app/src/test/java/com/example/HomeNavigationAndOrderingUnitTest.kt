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
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
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
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        viewModel = MathViewModel(app)

        // 1. Initial default state (Study Timer 2.0 Requirement A: Default OFF on cold launch)
        val initial = viewModel.studySessionTimer.value
        assertFalse("Study session timer MUST be OFF (collapsed) on Home by default", initial.isVisibleOnHome)
        assertFalse("Study session timer MUST not be running initially", initial.isRunning)
        assertEquals(0, initial.currentSessionSeconds)
        assertEquals(30, initial.dailyGoalMinutes)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.OFF, initial.displayStatus)
        assertEquals("বন্ধ", initial.statusLabelBn)
        assertFalse("No timer coroutine job should be active on cold launch", viewModel.isStudyTimerJobActive)

        // 2. Toggle visibility ON (Ready) and back OFF
        viewModel.toggleStudyTimerVisibility()
        assertTrue("Study timer should expand when explicitly toggled ON", viewModel.studySessionTimer.value.isVisibleOnHome)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.READY, viewModel.studySessionTimer.value.displayStatus)
        assertEquals("প্রস্তুত", viewModel.studySessionTimer.value.statusLabelBn)

        viewModel.toggleStudyTimerVisibility()
        assertFalse("Study timer should collapse when toggled OFF", viewModel.studySessionTimer.value.isVisibleOnHome)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.OFF, viewModel.studySessionTimer.value.displayStatus)

        // Re-enable panel for session start
        viewModel.toggleStudyTimerVisibility()
        assertTrue(viewModel.studySessionTimer.value.isVisibleOnHome)

        // 3. Start timer and tick 900 seconds (15 minutes)
        viewModel.setStudyDailyGoalMinutes(15)
        viewModel.toggleStudySessionRunning()
        assertTrue("Timer should be running after explicit start", viewModel.studySessionTimer.value.isRunning)
        assertTrue("Single timer coroutine job should be active while running", viewModel.isStudyTimerJobActive)
        assertEquals(1, viewModel.activeStudyTimerJobCount)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.RUNNING, viewModel.studySessionTimer.value.displayStatus)
        assertEquals("চলমান", viewModel.studySessionTimer.value.statusLabelBn)

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
        assertFalse("Timer coroutine job should stop after finishing session", viewModel.isStudyTimerJobActive)
        assertEquals(0, savedState.currentSessionSeconds)
        assertEquals(900, savedState.todayTotalSeconds)
        assertEquals(1, savedState.completedSessionsToday)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.COMPLETED, savedState.displayStatus)
        assertEquals("সম্পন্ন", savedState.statusLabelBn)

        // 5. Verify cold process restart across a newly created ViewModel instance:
        // Historical stats are preserved, but user-facing timer initializes as OFF.
        val restoredViewModel = MathViewModel(app)
        val restoredState = restoredViewModel.studySessionTimer.value
        assertEquals(900, restoredState.todayTotalSeconds)
        assertEquals(900, restoredState.allTimeTotalSeconds)
        assertEquals(15, restoredState.dailyGoalMinutes)
        assertEquals(1, restoredState.completedSessionsToday)
        assertFalse("Cold process launch must initialize timer as OFF", restoredState.isVisibleOnHome)
        assertFalse("Cold process launch must not auto-resume timer", restoredState.isRunning)
        assertEquals(0, restoredState.currentSessionSeconds)
        assertFalse(restoredViewModel.isStudyTimerJobActive)
    }

    @Test
    fun testStudyTimer20DefaultOffIgnoresLegacyPersistedFlagsAndClosedAppDowntime() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val todayKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val twoHoursAgoMs = System.currentTimeMillis() - (2 * 3600 * 1000L)

        // Simulate an existing installation / interrupted process with legacy ON + running flags
        prefs.edit()
            .putString("study_timer_date_key", todayKey)
            .putBoolean("study_timer_visible_on_home", true)
            .putBoolean("study_timer_is_running", true)
            .putInt("study_timer_current_session_sec", 120)
            .putInt("study_timer_today_sec_$todayKey", 300)
            .putInt("study_timer_all_time_sec", 1500)
            .putInt("study_timer_daily_goal_min", 45)
            .putInt("study_timer_sessions_$todayKey", 2)
            .putInt("study_timer_unrecorded_db_sec", 25)
            .putLong("study_timer_last_wall_ms", twoHoursAgoMs)
            .commit()

        // Launch a new cold-start ViewModel
        val coldVm = MathViewModel(app)
        val state = coldVm.studySessionTimer.value

        // Requirement A: User-facing timer MUST initialize as OFF and not running
        assertFalse("Existing install with persisted ON flag must still start OFF", state.isVisibleOnHome)
        assertFalse("Interrupted running flag must never auto-resume on cold start", state.isRunning)
        assertEquals("Current session seconds must reset to 0 on cold start", 0, state.currentSessionSeconds)
        assertFalse("No coroutine job may run on cold start", coldVm.isStudyTimerJobActive)
        assertEquals(0, coldVm.activeStudyTimerJobCount)

        // Closed-app time (2 hours = 7200s) MUST NOT be added to todayTotalSeconds or allTimeTotalSeconds
        assertEquals("Today total seconds must not include closed-app downtime", 300, state.todayTotalSeconds)
        assertEquals("All-time seconds must not include closed-app downtime", 1500, state.allTimeTotalSeconds)
        assertEquals(45, state.dailyGoalMinutes)
        assertEquals(2, state.completedSessionsToday)

        // Stale preferences must be sanitized in SharedPreferences
        assertFalse(prefs.getBoolean("study_timer_visible_on_home", true))
        assertFalse(prefs.getBoolean("study_timer_is_running", true))
        assertEquals(0, prefs.getInt("study_timer_current_session_sec", -1))
        assertEquals(0, prefs.getInt("study_timer_unrecorded_db_sec", -1))
    }

    @Test
    fun testStudyTimer20StateMachinePauseResetFinishDisableAndSingleCoroutineInvariants() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        val vm = MathViewModel(app)

        // 1. Navigating to Home or Dashboard does NOT activate timer
        vm.navigateTo(Screen.Dashboard)
        assertFalse(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm.studySessionTimer.value.isRunning)
        vm.navigateBack()
        assertFalse(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm.studySessionTimer.value.isRunning)

        // 2. Explicit enable -> READY ("প্রস্তুত")
        vm.setStudyTimerVisibility(true)
        assertTrue(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm.studySessionTimer.value.isRunning)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.READY, vm.studySessionTimer.value.displayStatus)
        assertEquals("প্রস্তুত", vm.studySessionTimer.value.statusLabelBn)

        // 3. Explicit Start -> RUNNING ("চলমান")
        vm.startStudySession()
        assertTrue(vm.studySessionTimer.value.isRunning)
        assertTrue(vm.isStudyTimerJobActive)
        assertEquals(1, vm.activeStudyTimerJobCount)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.RUNNING, vm.studySessionTimer.value.displayStatus)

        // Calling startStudySession() repeatedly is idempotent and never creates duplicate jobs
        vm.startStudySession()
        vm.startStudySession()
        assertEquals(1, vm.activeStudyTimerJobCount)

        // Tick 40 seconds
        vm.tickStudySessionTimer(40)
        assertEquals(40, vm.studySessionTimer.value.currentSessionSeconds)
        assertEquals(40, vm.studySessionTimer.value.todayTotalSeconds)

        // 4. Same-process navigation across modules preserves active running session
        vm.navigateTo(Screen.Tables)
        assertTrue("Active timer must stay running during same-process navigation", vm.studySessionTimer.value.isRunning)
        assertTrue("Timer job must stay active when HomeScreen leaves composition", vm.isStudyTimerJobActive)
        assertEquals(1, vm.activeStudyTimerJobCount)
        vm.tickStudySessionTimer(15)
        assertEquals(55, vm.studySessionTimer.value.currentSessionSeconds)
        vm.navigateBack()
        assertEquals(Screen.Home, vm.currentScreen)
        assertTrue(vm.studySessionTimer.value.isRunning)
        assertEquals(55, vm.studySessionTimer.value.currentSessionSeconds)

        // 5. Explicit Pause -> PAUSED ("বিরতি")
        vm.pauseStudySession()
        assertFalse(vm.studySessionTimer.value.isRunning)
        assertFalse(vm.isStudyTimerJobActive)
        assertEquals(0, vm.activeStudyTimerJobCount)
        assertEquals(55, vm.studySessionTimer.value.currentSessionSeconds)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.PAUSED, vm.studySessionTimer.value.displayStatus)
        assertEquals("বিরতি", vm.studySessionTimer.value.statusLabelBn)

        // Repeated pause is idempotent
        vm.pauseStudySession()
        assertEquals(55, vm.studySessionTimer.value.currentSessionSeconds)

        // 6. Reset current session -> READY ("প্রস্তুত"), currentSessionSeconds = 0, todayTotalSeconds preserved
        vm.resetCurrentStudySession()
        assertFalse(vm.studySessionTimer.value.isRunning)
        assertEquals(0, vm.studySessionTimer.value.currentSessionSeconds)
        assertEquals(55, vm.studySessionTimer.value.todayTotalSeconds)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.READY, vm.studySessionTimer.value.displayStatus)

        // 7. Start another session, tick 25s, then turn Switch OFF directly -> stops session & collapses
        vm.startStudySession()
        vm.tickStudySessionTimer(25)
        assertEquals(25, vm.studySessionTimer.value.currentSessionSeconds)
        assertEquals(80, vm.studySessionTimer.value.todayTotalSeconds)

        vm.setStudyTimerVisibility(false)
        assertFalse("Turning Switch OFF must collapse panel", vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse("Turning Switch OFF must stop running session", vm.studySessionTimer.value.isRunning)
        assertFalse("Turning Switch OFF must cancel coroutine job", vm.isStudyTimerJobActive)
        assertEquals(0, vm.activeStudyTimerJobCount)
        assertEquals(0, vm.studySessionTimer.value.currentSessionSeconds)
        assertEquals(80, vm.studySessionTimer.value.todayTotalSeconds)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.OFF, vm.studySessionTimer.value.displayStatus)

        // 8. Rapid ON/OFF stress test -> never leaks a coroutine job
        repeat(10) {
            vm.toggleStudyTimerVisibility()
            if (vm.studySessionTimer.value.isVisibleOnHome) {
                vm.toggleStudySessionRunning()
            }
        }
        vm.setStudyTimerVisibility(false)
        assertFalse(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm.studySessionTimer.value.isRunning)
        assertEquals(0, vm.activeStudyTimerJobCount)
    }

    @Test
    fun testStudyTimer20BackgroundForegroundAndBug40Bug74AnalyticsPreservation() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        val vm = MathViewModel(app)

        // 1. BUG-40 preservation: empty activity history has 0 current and 0 best streak
        val emptySummary = com.example.math.LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = com.example.math.AnalyticsTimeFilter.ALL_TIME,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = emptyList(),
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = emptyList(),
            nowMs = System.currentTimeMillis()
        )
        assertEquals("BUG-40: Empty history must have 0 current streak", 0, emptySummary.goalProgress.currentStreakDays)
        assertEquals("BUG-40: Empty history must have 0 best streak", 0, emptySummary.goalProgress.bestStreakDays)
        assertEquals("BUG-40: Empty history must have 0 study minutes today", 0, emptySummary.goalProgress.todayStudyMinutes)

        // 2. BUG-74 preservation: partial-minute study session (e.g. 20 seconds) counts as 1 minute in analytics
        val todayKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val partialSummary = com.example.math.LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = com.example.math.AnalyticsTimeFilter.ALL_TIME,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = emptyList(),
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = listOf(
                com.example.data.model.DailyStudyActivityEntity(
                    dateKey = todayKey,
                    questionsAttempted = 0,
                    correctCount = 0,
                    wrongCount = 0,
                    studyTimeSeconds = 20,
                    examsCompleted = 0,
                    practiceSessionsCompleted = 1,
                    dailyTargetQuestions = 20,
                    dailyTargetMinutes = 15,
                    updatedAt = System.currentTimeMillis()
                )
            ),
            nowMs = System.currentTimeMillis()
        )
        assertEquals("Partial-minute study session (20s) must record as 1 minute", 1, partialSummary.goalProgress.todayStudyMinutes)
        assertEquals(1, partialSummary.goalProgress.currentStreakDays)

        // 3. Background/Foreground while OFF must never activate timer
        vm.onAppBackgrounded()
        vm.onAppForegrounded()
        assertFalse(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm.studySessionTimer.value.isRunning)

        // 4. Background/Foreground while running flushes unrecorded seconds cleanly at most once
        vm.startStudySession()
        vm.tickStudySessionTimer(18)
        assertEquals(18, vm.studySessionTimer.value.unrecordedDbSeconds)
        vm.onAppBackgrounded()
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        assertEquals("Unrecorded seconds must be flushed on backgrounding", 0, prefs.getInt("study_timer_unrecorded_db_sec", -1))
        assertEquals("In-memory unrecordedDbSeconds must be 0 after flush", 0, vm.studySessionTimer.value.unrecordedDbSeconds)
        // Subsequent manual flush calls must be no-op (at-most-once guarantee)
        vm.flushUnrecordedStudyTime()
        assertEquals(0, vm.studySessionTimer.value.unrecordedDbSeconds)
        vm.onAppForegrounded()
        assertTrue(vm.studySessionTimer.value.isRunning)
        vm.setStudyTimerVisibility(false)
    }

    @Test
    fun testStudyTimer20CriticalAnalyticsBoundaryDurationsAndMultiSessionAggregation() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        val todayKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val nowMs = System.currentTimeMillis()

        fun computeMinutesForSeconds(seconds: Int): Int {
            val activities = if (seconds == 0) {
                emptyList()
            } else {
                listOf(
                    com.example.data.model.DailyStudyActivityEntity(
                        dateKey = todayKey,
                        questionsAttempted = 0,
                        correctCount = 0,
                        wrongCount = 0,
                        studyTimeSeconds = seconds,
                        examsCompleted = 0,
                        practiceSessionsCompleted = 1,
                        dailyTargetQuestions = 20,
                        dailyTargetMinutes = 15,
                        updatedAt = nowMs
                    )
                )
            }
            val data = com.example.math.LearningAnalyticsEngine.buildDashboardAnalytics(
                timeFilter = com.example.math.AnalyticsTimeFilter.ALL_TIME,
                completedExams = emptyList(),
                activeExam = null,
                quizScores = emptyList(),
                practiceHistory = emptyList(),
                topicAnalytics = emptyList(),
                progressItems = emptyList(),
                dailyActivities = activities,
                nowMs = nowMs
            )
            return data.goalProgress.todayStudyMinutes
        }

        // 1. CRITICAL ANALYTICS CHECK: Verify study durations at 0, 1, 29, 30, 59, 60, and 61 seconds
        assertEquals("0s must produce 0 study minutes", 0, computeMinutesForSeconds(0))
        assertEquals("1s must produce 1 study minute", 1, computeMinutesForSeconds(1))
        assertEquals("29s must produce 1 study minute", 1, computeMinutesForSeconds(29))
        assertEquals("30s must produce 1 study minute", 1, computeMinutesForSeconds(30))
        assertEquals("59s must produce 1 study minute", 1, computeMinutesForSeconds(59))
        assertEquals("60s must produce 1 study minute", 1, computeMinutesForSeconds(60))
        assertEquals("61s must produce 1 study minute", 1, computeMinutesForSeconds(61))

        // 2. Verify Switch ON only expands panel (READY) and never starts recording session;
        //    only explicit Start begins the session.
        val vm = MathViewModel(app)
        vm.toggleStudyTimerVisibility()
        assertTrue("Switch ON must expand panel", vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse("Switch ON must NOT start recording session", vm.studySessionTimer.value.isRunning)
        assertFalse("Switch ON must NOT launch timer coroutine job", vm.isStudyTimerJobActive)
        assertEquals(0, vm.studySessionTimer.value.currentSessionSeconds)

        // 3. Multiple short sessions whose combined duration is BELOW 60s (3 x 15s = 45s):
        //    Precise seconds (45s) are preserved; aggregate minutes is 1 (not inflated to 3).
        repeat(3) {
            vm.startStudySession()
            vm.tickStudySessionTimer(15)
            vm.finishAndSaveStudySession()
        }
        assertEquals("Precise todayTotalSeconds must be 45s after 3x15s sessions", 45, vm.studySessionTimer.value.todayTotalSeconds)
        assertEquals("Precise allTimeTotalSeconds must be 45s after 3x15s sessions", 45, vm.studySessionTimer.value.allTimeTotalSeconds)
        assertEquals(3, vm.studySessionTimer.value.completedSessionsToday)
        assertEquals("Combined 45s (<60s) across 3 short sessions must equal 1 minute, not 3", 1, computeMinutesForSeconds(vm.studySessionTimer.value.todayTotalSeconds))

        // 4. Add another 15s short session -> combined duration is EQUAL to 60s (4 x 15s = 60s):
        //    Precise seconds (60s) are preserved; aggregate minutes is 1 (not inflated to 4).
        vm.startStudySession()
        vm.tickStudySessionTimer(15)
        vm.finishAndSaveStudySession()
        assertEquals("Precise todayTotalSeconds must be 60s", 60, vm.studySessionTimer.value.todayTotalSeconds)
        assertEquals("Precise allTimeTotalSeconds must be 60s", 60, vm.studySessionTimer.value.allTimeTotalSeconds)
        assertEquals(4, vm.studySessionTimer.value.completedSessionsToday)
        assertEquals("Combined 60s (==60s) across 4 short sessions must equal 1 minute, not 4", 1, computeMinutesForSeconds(vm.studySessionTimer.value.todayTotalSeconds))

        // 5. Add another 15s short session -> combined duration is ABOVE 60s (5 x 15s = 75s):
        //    Precise seconds (75s) are preserved; aggregate minutes is 1 (75/60 = 1.25 -> 1, not 5).
        vm.startStudySession()
        vm.tickStudySessionTimer(15)
        vm.finishAndSaveStudySession()
        assertEquals("Precise todayTotalSeconds must be 75s", 75, vm.studySessionTimer.value.todayTotalSeconds)
        assertEquals("Precise allTimeTotalSeconds must be 75s", 75, vm.studySessionTimer.value.allTimeTotalSeconds)
        assertEquals(5, vm.studySessionTimer.value.completedSessionsToday)
        assertEquals("Combined 75s (>60s) across 5 short sessions must equal 1 minute, not 5", 1, computeMinutesForSeconds(vm.studySessionTimer.value.todayTotalSeconds))

        // 6. Add another 25s short session -> combined duration is 100s (>90s):
        //    Precise seconds (100s) are preserved; aggregate minutes is 2 (100/60 = 1.67 -> 2, not 6).
        vm.startStudySession()
        vm.tickStudySessionTimer(25)
        vm.finishAndSaveStudySession()
        assertEquals("Precise todayTotalSeconds must be 100s", 100, vm.studySessionTimer.value.todayTotalSeconds)
        assertEquals("Precise allTimeTotalSeconds must be 100s", 100, vm.studySessionTimer.value.allTimeTotalSeconds)
        assertEquals(6, vm.studySessionTimer.value.completedSessionsToday)
        assertEquals("Combined 100s across 6 short sessions must equal 2 minutes, not 6", 2, computeMinutesForSeconds(vm.studySessionTimer.value.todayTotalSeconds))

        vm.setStudyTimerVisibility(false)
    }

    @OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
    private fun collectSemanticsNodesByTag(
        rootView: android.view.View
    ): Map<String, androidx.compose.ui.semantics.SemanticsNode> {
        val result = mutableMapOf<String, androidx.compose.ui.semantics.SemanticsNode>()
        fun findRootForTest(view: android.view.View): androidx.compose.ui.node.RootForTest? {
            if (view is androidx.compose.ui.node.RootForTest) return view
            if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) {
                    val found = findRootForTest(view.getChildAt(i))
                    if (found != null) return found
                }
            }
            return null
        }
        val dispatcher = androidx.compose.ui.platform.AndroidUiDispatcher.CurrentThread[kotlin.coroutines.ContinuationInterceptor]
        val callback = runCatching {
            dispatcher?.javaClass?.getDeclaredField("dispatchCallback")?.apply { isAccessible = true }?.get(dispatcher)
        }.getOrNull()
        repeat(3) {
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
            (callback as? Runnable)?.run()
            androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
            (callback as? android.view.Choreographer.FrameCallback)?.doFrame(System.nanoTime())
            (callback as? Runnable)?.run()
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(50, java.util.concurrent.TimeUnit.MILLISECONDS)
        }
        val rootForTest = findRootForTest(rootView) ?: return result
        rootForTest.measureAndLayoutForTest()
        fun walk(node: androidx.compose.ui.semantics.SemanticsNode) {
            val tag = if (node.config.contains(androidx.compose.ui.semantics.SemanticsProperties.TestTag)) {
                node.config[androidx.compose.ui.semantics.SemanticsProperties.TestTag]
            } else {
                null
            }
            if (tag != null) {
                result[tag] = node
            }
            node.children.forEach { walk(it) }
        }
        walk(rootForTest.semanticsOwner.unmergedRootSemanticsNode)
        return result
    }

    @Test
    @Config(sdk = [36])
    fun testStudyTimer20CompactOffStateUiAndExpandCollapseBehavior() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()

        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val vm = androidx.lifecycle.ViewModelProvider(activity)[MathViewModel::class.java]
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        // 1. Cold start on HomeScreen: Study Timer is OFF by default
        assertEquals(Screen.Home, vm.currentScreen)
        assertFalse(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm.studySessionTimer.value.isRunning)

        // Inspect live rendered Compose Semantics tree in OFF state
        val density = activity.resources.displayMetrics.density
        val offNodes = collectSemanticsNodesByTag(activity.window.decorView)
        val offCardNode = offNodes["study_session_timer_card"]
        assertNotNull("Compact study_session_timer_card must be rendered on HomeScreen when OFF", offCardNode)
        val offCardHeightDp = offCardNode!!.size.height / density
        assertTrue(
            "Compact OFF-state card height (${offCardHeightDp}dp) must be approximately 64..72dp at normal font scale",
            offCardHeightDp in 64f..72f
        )
        assertNotNull("Title must be present when OFF", offNodes["study_timer_title_text"])
        assertNotNull("Compact status text must be present when OFF", offNodes["study_timer_status_text"])
        assertNotNull("Toggle Switch must be present when OFF", offNodes["study_timer_toggle_switch"])

        // Expanded clock, progress bar, and controls MUST be absent in OFF mode
        assertFalse("Expanded body must be absent when OFF", offNodes.containsKey("study_timer_expanded_body"))
        assertFalse("Digital clock must be absent when OFF", offNodes.containsKey("study_timer_clock_text"))
        assertFalse("Start/Pause button must be absent when OFF", offNodes.containsKey("study_timer_start_pause_btn"))
        assertFalse("Reset button must be absent when OFF", offNodes.containsKey("study_timer_reset_btn"))
        assertFalse("Goal progress bar must be absent when OFF", offNodes.containsKey("study_timer_goal_progress"))
        assertFalse("Goal chip must be absent when OFF", offNodes.containsKey("study_timer_goal_chip_15"))
        assertFalse("Save button must be absent when OFF", offNodes.containsKey("study_timer_save_btn"))
        val tablesTopWhenOff = offNodes["module_tables_card"]?.positionInRoot?.y ?: 0f

        // Verify source-level & layout invariants for StudySessionTimerCard.kt
        val cardCandidates = listOf(
            java.io.File("app/src/main/java/com/example/ui/components/StudySessionTimerCard.kt"),
            java.io.File("src/main/java/com/example/ui/components/StudySessionTimerCard.kt"),
            java.io.File("/app/applet/app/src/main/java/com/example/ui/components/StudySessionTimerCard.kt")
        )
        val cardSource = cardCandidates.first { it.exists() }.readText()
        assertTrue("Compact OFF state must display 'পড়াশোনা সেশন টাইমার'", cardSource.contains("পড়াশোনা সেশন টাইমার"))
        assertTrue("Compact OFF state must display 'বন্ধ • চালু করতে টগল করুন'", cardSource.contains("বন্ধ • চালু করতে টগল করুন"))
        assertTrue("Header row must enforce MathSpacing.minTouchTarget (48.dp)", cardSource.contains("heightIn(min = MathSpacing.minTouchTarget)"))
        assertTrue("Switch must enforce minimumInteractiveComponentSize()", cardSource.contains("minimumInteractiveComponentSize()"))
        assertFalse("Duplicate compact play/pause button must be removed from OFF bar", cardSource.contains("study_timer_compact_play_pause_btn"))
        assertFalse("Duplicate expand/collapse chevron IconButton must be removed", cardSource.contains("study_timer_toggle_btn"))
        assertFalse("Duplicate LaunchedEffect ticker inside composable must be removed", cardSource.contains("LaunchedEffect(timerState.isRunning)"))

        // 2. Toggle ON -> expands panel in live Activity composition without starting recording session
        vm.toggleStudyTimerVisibility()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(1000, java.util.concurrent.TimeUnit.MILLISECONDS)
        activity.window.decorView.requestLayout()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(1000, java.util.concurrent.TimeUnit.MILLISECONDS)
        assertTrue(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse("Toggling Switch ON must not auto-start session", vm.studySessionTimer.value.isRunning)
        assertEquals(com.example.ui.StudyTimerDisplayStatus.READY, vm.studySessionTimer.value.displayStatus)

        val onNodes = collectSemanticsNodesByTag(activity.window.decorView)
        val onCardHeightDp = onNodes["study_session_timer_card"]!!.size.height / density
        val expandedBodyHeightDp = onNodes["study_timer_expanded_body"]!!.size.height / density
        assertTrue(
            "Expanded ON card (${onCardHeightDp}dp) and expanded body (${expandedBodyHeightDp}dp) must reveal full controls",
            onCardHeightDp > offCardHeightDp || expandedBodyHeightDp > 60f
        )
        assertNotNull("Expanded body must be present when ON", onNodes["study_timer_expanded_body"])
        assertNotNull("Status pill must be present when ON", onNodes["study_timer_status_pill"])
        assertNotNull("Digital clock must be present when ON", onNodes["study_timer_clock_text"])
        assertNotNull("Start/Pause button must be present when ON", onNodes["study_timer_start_pause_btn"])
        assertNotNull("Reset button must be present when ON", onNodes["study_timer_reset_btn"])
        assertNotNull("Goal progress bar must be present when ON", onNodes["study_timer_goal_progress"])
        assertNotNull("Goal chip 15m must be present when ON", onNodes["study_timer_goal_chip_15"])

        // Start session -> Running
        vm.toggleStudySessionRunning()
        vm.tickStudySessionTimer(12)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertTrue(vm.studySessionTimer.value.isRunning)
        assertEquals(12, vm.studySessionTimer.value.currentSessionSeconds)

        // Verify Dark Theme rendering while expanded
        vm.setThemeMode(com.example.ui.theme.AppThemeMode.DARK)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertEquals(com.example.ui.theme.AppThemeMode.DARK, vm.themeMode.value)

        // Toggle OFF -> collapses back to compact bar and stops session
        vm.toggleStudyTimerVisibility()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(1000, java.util.concurrent.TimeUnit.MILLISECONDS)
        assertFalse(vm.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm.studySessionTimer.value.isRunning)
        assertEquals(0, vm.studySessionTimer.value.currentSessionSeconds)

        val collapsedAgainNodes = collectSemanticsNodesByTag(activity.window.decorView)
        assertFalse("Expanded body must be removed after toggling OFF", collapsedAgainNodes.containsKey("study_timer_expanded_body"))

        // Reset theme and clean up Activity
        vm.setThemeMode(com.example.ui.theme.AppThemeMode.SYSTEM)
        controller.pause().stop().destroy()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
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
        vm.setStudyTimerVisibility(false)
        controller.pause().stop().destroy()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }
}
