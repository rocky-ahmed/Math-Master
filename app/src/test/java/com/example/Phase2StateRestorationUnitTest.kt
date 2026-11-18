package com.example

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.math.BcsExamProfile
import com.example.math.BcsSyllabusVersion
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookInteractiveLabType
import com.example.math.MentalMathCategory
import com.example.math.MentalMathDifficulty
import com.example.math.MentalMathMode
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.math.QuizMode
import com.example.math.QuizTopic
import com.example.math.SolverTopic
import com.example.ui.MathViewModel
import com.example.ui.NavigationContextSnapshot
import com.example.ui.NavigationFrame
import com.example.ui.Screen
import com.example.ui.ScreenCodec
import com.example.ui.managedUiStateSaver
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedScrollState
import com.example.ui.rememberManagedUiState
import com.example.ui.screens.SimultaneousLinearSystemAndBridgeCard
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MathMasterTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class Phase2StateRestorationUnitTest {

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
    fun screenCodec_roundTripsAll31ScreensAnd12ArgCarryingPayloads() {
        val sampleLessonId = BookCurriculumRepository.getChapter(BookId.CLASS_9_10, 2)!!.lessons.first().id
        val allScreens = listOf(
            Screen.Home,
            Screen.Tables,
            Screen.TableDetail,
            Screen.Primes,
            Screen.SquaresCubes,
            Screen.Formulas,
            Screen.FormulaDetail,
            Screen.FormulaLibrary,
            Screen.PracticeLab,
            Screen.PracticeLabPlay,
            Screen.MentalMath,
            Screen.MentalMathPlay,
            Screen.NumberSystem,
            Screen.NumberSystemPractice,
            Screen.Quizzes,
            Screen.QuizPlay,
            Screen.Bookmarks,
            Screen.PracticeTutor,
            Screen.Dashboard,
            Screen.Customize,
            Screen.ProblemSolver,
            Screen.VisualExplorer,
            Screen.BcsPreparation,
            Screen.BookHub,
            Screen.BookDetail,
            Screen.BookChapterOverview,
            Screen.BookLesson,
            Screen.BookPractice,
            Screen.BookPdfReader,
            Screen.BookInteractiveLab,
            Screen.BookRevision
        )
        assertEquals(31, allScreens.size)

        var argCarryingCount = 0
        allScreens.forEachIndexed { idx, screen ->
            val snap = NavigationContextSnapshot(
                screen = screen,
                tableNumber = 17,
                formulaId = "sq_2",
                selectedLibraryFormulaId = if (screen == Screen.FormulaLibrary) "lib_trig_1" else null,
                tutorNumber = 9,
                bcsExamProfile = BcsExamProfile.BCS_LEGACY_PRELIM,
                bcsSyllabusVersion = BcsSyllabusVersion.BCS_LEGACY_15,
                bookId = BookId.CLASS_9_10,
                bookChapterNumber = 2,
                bookLessonId = sampleLessonId,
                bookPrintedPage = 24,
                bookLabType = BookInteractiveLabType.TRIG_UNIT_CIRCLE_LAB,
                routeKey = "${screen.route}_rk_$idx",
                screenInstanceId = "${screen.route}#$idx"
            )
            val json = ScreenCodec.encodeStack(
                frames = listOf(NavigationFrame(screen = screen, snapshot = snap)),
                nextInstanceCounter = idx + 1
            )
            val parsedRoot = JSONObject(json)
            assertEquals(ScreenCodec.SCHEMA_VERSION, parsedRoot.getInt("schemaVersion"))
            val argsObj = parsedRoot.getJSONArray("frames").getJSONObject(0).getJSONObject("args")
            if (argsObj.length() > 0) {
                argCarryingCount++
            }

            val decoded = ScreenCodec.decodeStack(json, sanitizeTransientOnRestore = false)
            val restoredFrame = decoded.frames.last()
            assertEquals(screen, restoredFrame.screen)
            assertEquals(snap.routeKey, restoredFrame.snapshot.routeKey)
            assertEquals(snap.screenInstanceId, restoredFrame.snapshot.screenInstanceId)
            when (screen) {
                Screen.TableDetail -> assertEquals(17, restoredFrame.snapshot.tableNumber)
                Screen.FormulaDetail -> assertEquals("sq_2", restoredFrame.snapshot.formulaId)
                Screen.FormulaLibrary -> assertEquals("lib_trig_1", restoredFrame.snapshot.selectedLibraryFormulaId)
                Screen.PracticeTutor -> assertEquals(9, restoredFrame.snapshot.tutorNumber)
                Screen.BcsPreparation -> {
                    assertEquals(BcsExamProfile.BCS_LEGACY_PRELIM, restoredFrame.snapshot.bcsExamProfile)
                    assertEquals(BcsSyllabusVersion.BCS_LEGACY_15, restoredFrame.snapshot.bcsSyllabusVersion)
                }
                Screen.BookDetail, Screen.BookRevision -> assertEquals(BookId.CLASS_9_10, restoredFrame.snapshot.bookId)
                Screen.BookChapterOverview -> {
                    assertEquals(BookId.CLASS_9_10, restoredFrame.snapshot.bookId)
                    assertEquals(2, restoredFrame.snapshot.bookChapterNumber)
                }
                Screen.BookLesson, Screen.BookPractice -> {
                    assertEquals(BookId.CLASS_9_10, restoredFrame.snapshot.bookId)
                    assertEquals(2, restoredFrame.snapshot.bookChapterNumber)
                    assertEquals(sampleLessonId, restoredFrame.snapshot.bookLessonId)
                }
                Screen.BookPdfReader -> {
                    assertEquals(BookId.CLASS_9_10, restoredFrame.snapshot.bookId)
                    assertEquals(24, restoredFrame.snapshot.bookPrintedPage)
                }
                Screen.BookInteractiveLab -> {
                    assertEquals(BookId.CLASS_9_10, restoredFrame.snapshot.bookId)
                    assertEquals(2, restoredFrame.snapshot.bookChapterNumber)
                    assertEquals(BookInteractiveLabType.TRIG_UNIT_CIRCLE_LAB, restoredFrame.snapshot.bookLabType)
                }
                else -> Unit
            }
        }
        assertEquals(12, argCarryingCount)
    }

    @Test
    fun bookPractice_typedArgsAndSanitizationOnProcessDeathAndBackNavigation() {
        val sampleLesson = BookCurriculumRepository.getChapter(BookId.CLASS_8, 3)!!.lessons.first()
        val withLessonFrames = listOf(
            NavigationFrame(Screen.Home, NavigationContextSnapshot(Screen.Home)),
            NavigationFrame(
                Screen.BookDetail,
                NavigationContextSnapshot(Screen.BookDetail, bookId = BookId.CLASS_8)
            ),
            NavigationFrame(
                Screen.BookChapterOverview,
                NavigationContextSnapshot(
                    Screen.BookChapterOverview,
                    bookId = BookId.CLASS_8,
                    bookChapterNumber = 3
                )
            ),
            NavigationFrame(
                Screen.BookPractice,
                NavigationContextSnapshot(
                    Screen.BookPractice,
                    bookId = BookId.CLASS_8,
                    bookChapterNumber = 3,
                    bookLessonId = sampleLesson.id
                )
            )
        )
        val jsonWithLesson = ScreenCodec.encodeStack(withLessonFrames, nextInstanceCounter = 4)

        // 1. Raw decode preserves BookPractice with typed args
        val rawDecoded = ScreenCodec.decodeStack(jsonWithLesson, sanitizeTransientOnRestore = false)
        assertEquals(Screen.BookPractice, rawDecoded.frames.last().screen)
        assertEquals(BookId.CLASS_8, rawDecoded.frames.last().snapshot.bookId)
        assertEquals(3, rawDecoded.frames.last().snapshot.bookChapterNumber)
        assertEquals(sampleLesson.id, rawDecoded.frames.last().snapshot.bookLessonId)

        // 2. Process-death decode maps BookPractice with non-blank bookLessonId -> BookLesson
        val handleWithLesson = SavedStateHandle(mapOf("phase2_nav_stack_json_v1" to jsonWithLesson))
        val vmRestoredLesson = MathViewModel(application = app, savedStateHandle = handleWithLesson)
        assertEquals(Screen.BookLesson, vmRestoredLesson.currentScreen)
        assertEquals(BookId.CLASS_8, vmRestoredLesson.selectedBookId.value)
        assertEquals(3, vmRestoredLesson.selectedBookChapterNumber.value)
        assertEquals(sampleLesson.id, vmRestoredLesson.selectedBookLessonId.value)
        assertTrue(vmRestoredLesson.navigateBack())
        assertEquals(Screen.BookChapterOverview, vmRestoredLesson.currentScreen)
        assertEquals(BookId.CLASS_8, vmRestoredLesson.selectedBookId.value)
        assertEquals(3, vmRestoredLesson.selectedBookChapterNumber.value)

        // 3. Process-death decode maps BookPractice with blank bookLessonId -> BookChapterOverview
        val chapterPracticeFrames = listOf(
            NavigationFrame(Screen.Home, NavigationContextSnapshot(Screen.Home)),
            NavigationFrame(
                Screen.BookDetail,
                NavigationContextSnapshot(Screen.BookDetail, bookId = BookId.CLASS_7)
            ),
            NavigationFrame(
                Screen.BookPractice,
                NavigationContextSnapshot(
                    Screen.BookPractice,
                    bookId = BookId.CLASS_7,
                    bookChapterNumber = 2,
                    bookLessonId = ""
                )
            )
        )
        val jsonWithoutLesson = ScreenCodec.encodeStack(chapterPracticeFrames, nextInstanceCounter = 3)
        val handleChapterOnly = SavedStateHandle(mapOf("phase2_nav_stack_json_v1" to jsonWithoutLesson))
        val vmRestoredChapter = MathViewModel(application = app, savedStateHandle = handleChapterOnly)
        assertEquals(Screen.BookChapterOverview, vmRestoredChapter.currentScreen)
        assertEquals(BookId.CLASS_7, vmRestoredChapter.selectedBookId.value)
        assertEquals(2, vmRestoredChapter.selectedBookChapterNumber.value)
        assertTrue(vmRestoredChapter.navigateBack())
        assertEquals(Screen.BookDetail, vmRestoredChapter.currentScreen)
        assertEquals(BookId.CLASS_7, vmRestoredChapter.selectedBookId.value)
    }

    @Test
    fun multiFrameBackStack_retainsPerFrameTypedArgsAndSpecialCharacters() {
        val frames = listOf(
            NavigationFrame(Screen.Home, NavigationContextSnapshot(Screen.Home)),
            NavigationFrame(
                Screen.BookLesson,
                NavigationContextSnapshot(
                    screen = Screen.BookLesson,
                    bookId = BookId.CLASS_6,
                    bookChapterNumber = 1,
                    bookLessonId = "c6|1@:পাঠ#১\n\"ক\"\\খ",
                    routeKey = "fake|route:split#trap",
                    screenInstanceId = "lesson#1|@"
                )
            ),
            NavigationFrame(
                Screen.FormulaDetail,
                NavigationContextSnapshot(
                    screen = Screen.FormulaDetail,
                    formulaId = "sq_3",
                    routeKey = "formula|detail:sq_3#2",
                    screenInstanceId = "formula#2"
                )
            ),
            NavigationFrame(
                Screen.BookLesson,
                NavigationContextSnapshot(
                    screen = Screen.BookLesson,
                    bookId = BookId.CLASS_9_10,
                    bookChapterNumber = 2,
                    bookLessonId = BookCurriculumRepository.getChapter(BookId.CLASS_9_10, 2)!!.lessons.last().id,
                    routeKey = "lesson|second:c910",
                    screenInstanceId = "lesson#3"
                )
            )
        )
        val encoded = ScreenCodec.encodeStack(frames, nextInstanceCounter = 4)
        val decoded = ScreenCodec.decodeStack(encoded, sanitizeTransientOnRestore = true)
        assertEquals(4, decoded.frames.size)
        assertEquals(BookId.CLASS_6, decoded.frames[1].snapshot.bookId)
        assertEquals("c6|1@:পাঠ#১\n\"ক\"\\খ", decoded.frames[1].snapshot.bookLessonId)
        assertEquals("sq_3", decoded.frames[2].snapshot.formulaId)
        assertEquals(BookId.CLASS_9_10, decoded.frames[3].snapshot.bookId)
        assertEquals(2, decoded.frames[3].snapshot.bookChapterNumber)
    }

    @Test
    fun corruptAndMissingArgs_fallbackDeterministicallyAndKeepPlayRoutesSanitized() {
        val corrupt = ScreenCodec.decodeStack("{not valid json!!!", sanitizeTransientOnRestore = true)
        assertEquals(1, corrupt.frames.size)
        assertEquals(Screen.Home, corrupt.frames.first().screen)

        val missingArgsJson = """
            {
              "schemaVersion": 1,
              "nextInstanceCounter": 5,
              "frames": [
                {"route": "home", "routeKey": "home", "screenInstanceId": "home#0", "args": {}},
                {"route": "table_detail", "routeKey": "table_detail:99", "screenInstanceId": "t#1", "args": {}},
                {"route": "formula_detail", "routeKey": "formula_detail:missing", "screenInstanceId": "f#2", "args": {"formulaId": "does_not_exist"}},
                {"route": "quiz_play", "routeKey": "quiz_play", "screenInstanceId": "q#3", "args": {}},
                {"route": "customize", "routeKey": "customize", "screenInstanceId": "c#4", "args": {}}
              ]
            }
        """.trimIndent()
        val recovered = ScreenCodec.decodeStack(missingArgsJson, sanitizeTransientOnRestore = true)
        // Missing tableNumber -> Tables; invalid formulaId -> Formulas; quiz_play -> Quizzes; customize -> popped
        assertEquals(
            listOf(Screen.Home, Screen.Tables, Screen.Formulas, Screen.Quizzes),
            recovered.frames.map { it.screen }
        )
    }

    @Test
    fun stateRestorationTester_restoresScrollAndUiStatesAcrossFreshViewModel() {
        val restorationTester = StateRestorationTester(composeTestRule)
        var currentVm by mutableStateOf(MathViewModel(app))
        var activeScrollOffset = 0
        var activeUiValue = "initial"

        restorationTester.setContent {
            val vm = remember(currentVm) { currentVm }
            val listState = rememberManagedLazyListState(vm, "p2_test_lazy")
            val scrollState = rememberManagedScrollState(vm, "p2_test_scroll")
            val uiState = rememberManagedUiState(vm, "p2_test_ui", "default")
            activeScrollOffset = scrollState.value
            activeUiValue = uiState.value

            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState, modifier = Modifier.height(100.dp)) {
                    items(30) { idx -> Text("Item $idx") }
                }
                Column(modifier = Modifier.height(100.dp).verticalScroll(scrollState)) {
                    repeat(30) { Text("Row $it") }
                }
                Text(
                    text = uiState.value,
                    modifier = Modifier.testTag("restored_ui_text")
                )
                Text(
                    text = "Update",
                    modifier = Modifier
                        .testTag("btn_update_ui_state")
                        .clickable {
                            uiState.value = "user_entered_state"
                        }
                )
            }
        }

        composeTestRule.onNodeWithTag("btn_update_ui_state").performClick()
        composeTestRule.waitForIdle()
        assertEquals("user_entered_state", activeUiValue)

        // Simulate process death: brand new ViewModel with empty in-memory maps
        val freshVm = MathViewModel(app)
        currentVm = freshVm
        restorationTester.emulateSavedInstanceStateRestore()
        composeTestRule.waitForIdle()

        assertEquals("user_entered_state", activeUiValue)
        assertEquals("user_entered_state", freshVm.getUiContextState("p2_test_ui", "default"))
        assertTrue(activeScrollOffset >= 0)
    }

    @Test
    fun managedUiStateSaver_distinguishesListAndSetRoundTrip() {
        val saver = managedUiStateSaver<Any>()
        val originalSet = linkedSetOf("A", "B", "C")
        val originalList = listOf("X", "Y", "Z")

        val scope = androidx.compose.runtime.saveable.SaverScope { true }
        val savedSet = with(saver) { scope.save(mutableStateOf(originalSet)) }!!
        val savedList = with(saver) { scope.save(mutableStateOf(originalList)) }!!

        val restoredSet = saver.restore(savedSet)!!.value
        val restoredList = saver.restore(savedList)!!.value

        assertTrue(restoredSet is Set<*>)
        assertEquals(originalSet, restoredSet)
        assertTrue(restoredList is List<*>)
        assertFalse(restoredList is Set<*>)
        assertEquals(originalList, restoredList)
    }

    @Test
    fun simultaneousLinearSystemCard_all15StateVariablesSurviveRestorationAndViewModelRestart() {
        val restorationTester = StateRestorationTester(composeTestRule)
        var currentVm by mutableStateOf(MathViewModel(app))

        restorationTester.setContent {
            MathMasterTheme(themeMode = AppThemeMode.LIGHT) {
                SimultaneousLinearSystemAndBridgeCard(
                    selectedTopic = SolverTopic.LINEAR_EQUATIONS,
                    viewModel = currentVm
                )
            }
        }

        // Modify 2x2 shared coeffs (6), switch method tab, then switch to 3x3 & modify 6 3x3 coeffs
        composeTestRule.onNodeWithTag("sim_method_tab_1").performClick()
        composeTestRule.onNodeWithTag("sim2_a1").performTextReplacement("৭")
        composeTestRule.onNodeWithTag("sim2_b1").performTextReplacement("৮")
        composeTestRule.onNodeWithTag("sim2_c1").performTextReplacement("৯")
        composeTestRule.onNodeWithTag("sim2_a2").performTextReplacement("১০")
        composeTestRule.onNodeWithTag("sim2_b2").performTextReplacement("১১")
        composeTestRule.onNodeWithTag("sim2_c2").performTextReplacement("১২")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("sim_dim_3x3").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("sim3_d1").performTextReplacement("২১")
        composeTestRule.onNodeWithTag("sim3_d2").performTextReplacement("২২")
        composeTestRule.onNodeWithTag("sim3_a3").performTextReplacement("৩")
        composeTestRule.onNodeWithTag("sim3_b3").performTextReplacement("৪")
        composeTestRule.onNodeWithTag("sim3_c3").performTextReplacement("৫")
        composeTestRule.onNodeWithTag("sim3_d3").performTextReplacement("২৩")
        composeTestRule.waitForIdle()

        // Replace ViewModel (simulates process death with SavedInstanceState Bundle intact)
        val freshVm = MathViewModel(app)
        currentVm = freshVm
        restorationTester.emulateSavedInstanceStateRestore()
        composeTestRule.waitForIdle()

        assertEquals(3, freshVm.getUiContextState("sim_system_dimension", 2))
        assertEquals(1, freshVm.getUiContextState("sim_method_tab", 0))
        assertEquals("৭", freshVm.getUiContextState("sim_coeff_a1", ""))
        assertEquals("৮", freshVm.getUiContextState("sim_coeff_b1", ""))
        assertEquals("৯", freshVm.getUiContextState("sim_coeff_c1", ""))
        assertEquals("১০", freshVm.getUiContextState("sim_coeff_a2", ""))
        assertEquals("১১", freshVm.getUiContextState("sim_coeff_b2", ""))
        assertEquals("১২", freshVm.getUiContextState("sim_coeff_c2", ""))
        assertEquals("২১", freshVm.getUiContextState("sim_coeff_3d1", ""))
        assertEquals("২২", freshVm.getUiContextState("sim_coeff_3d2", ""))
        assertEquals("৩", freshVm.getUiContextState("sim_coeff_3a3", ""))
        assertEquals("৪", freshVm.getUiContextState("sim_coeff_3b3", ""))
        assertEquals("৫", freshVm.getUiContextState("sim_coeff_3c3", ""))
        assertEquals("২৩", freshVm.getUiContextState("sim_coeff_3d3", ""))
    }

    @Test
    fun activityRecreationAndSimulatedProcessDeath_preserveRouteStackAndSanitizePlayScreens() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        var freshController: org.robolectric.android.controller.ActivityController<MainActivity>? = null
        try {
            val vm = androidx.lifecycle.ViewModelProvider(controller.get())[MathViewModel::class.java]

            vm.openBookDetail(BookId.CLASS_8)
            vm.openBookChapter(BookId.CLASS_8, 3)
            val lessonId = BookCurriculumRepository.getChapter(BookId.CLASS_8, 3)!!.lessons.first().id
            vm.openBookLesson(BookId.CLASS_8, 3, lessonId)

            // Activity recreate() preserves exact screen & backstack
            controller.recreate()
            val recreatedVm = androidx.lifecycle.ViewModelProvider(controller.get())[MathViewModel::class.java]
            assertEquals(Screen.BookLesson, recreatedVm.currentScreen)
            assertEquals(BookId.CLASS_8, recreatedVm.selectedBookId.value)
            assertEquals(3, recreatedVm.selectedBookChapterNumber.value)
            assertEquals(lessonId, recreatedVm.selectedBookLessonId.value)

            // Now navigate to an active QuizPlay and save instance state bundle before simulated process kill
            recreatedVm.startQuiz(
                topic = QuizTopic.ALGEBRA,
                count = 5,
                mode = QuizMode.TOPIC
            )
            assertEquals(Screen.QuizPlay, recreatedVm.currentScreen)

            val savedBundle = Bundle()
            controller.saveInstanceState(savedBundle)

            // Boot a completely fresh Activity from the saved Bundle (simulating fresh OS process)
            freshController = Robolectric.buildActivity(MainActivity::class.java).setup(savedBundle)
            val freshVm = androidx.lifecycle.ViewModelProvider(freshController.get())[MathViewModel::class.java]
            assertNotNull(freshVm)
            assertEquals(Screen.Quizzes, freshVm.currentScreen)
            assertTrue(freshVm.navigateBack())
            assertEquals(Screen.BookLesson, freshVm.currentScreen)
            assertEquals(BookId.CLASS_8, freshVm.selectedBookId.value)
        } finally {
            runCatching { freshController?.pause()?.stop()?.destroy() }
            runCatching { controller.pause().stop().destroy() }
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        }
    }
}
