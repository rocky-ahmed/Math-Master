package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.screens.BookChapterOverviewScreen
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.BookHubScreen
import com.example.ui.screens.BookInteractiveLabScreen
import com.example.ui.screens.BookLessonScreen
import com.example.ui.screens.BookPdfReaderScreen
import com.example.ui.screens.BookPracticeScreen
import com.example.ui.screens.BookRevisionScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CustomizeScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FormulaDetailScreen
import com.example.ui.screens.FormulaLibraryScreen
import com.example.ui.screens.FormulasScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MentalMathPlayScreen
import com.example.ui.screens.MentalMathScreen
import com.example.ui.screens.NumberSystemPracticePlayScreen
import com.example.ui.screens.NumberSystemScreen
import com.example.ui.screens.PracticeLabPlayScreen
import com.example.ui.screens.PracticeLabScreen
import com.example.ui.screens.PracticeTutorScreen
import com.example.ui.screens.PrimesScreen
import com.example.ui.screens.ProblemSolverScreen
import com.example.ui.screens.QuizPlayScreen
import com.example.ui.screens.QuizzesScreen
import com.example.ui.screens.SquaresCubesScreen
import com.example.ui.screens.TableDetailScreen
import com.example.ui.screens.TablesScreen
import com.example.ui.theme.MathMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MathViewModel = viewModel(factory = MathViewModel.Factory)
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val customState by viewModel.customizationState.collectAsStateWithLifecycle()
            val previewCustomState by viewModel.previewCustomizationState.collectAsStateWithLifecycle()
            val screenStack by viewModel.screenStack.collectAsStateWithLifecycle()
            val currentScreen = screenStack.lastOrNull() ?: Screen.Home

            val activeConfig = if (currentScreen is Screen.Customize) {
                previewCustomState.global
            } else {
                customState.global
            }

            MathMasterTheme(
                themeMode = themeMode,
                globalConfig = activeConfig
            ) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun rememberCurrentDestinationStateKey(
    viewModel: MathViewModel,
    currentScreen: Screen
): String {
    return when (currentScreen) {
        is Screen.Home -> Screen.Home.route
        is Screen.Tables -> Screen.Tables.route
        is Screen.TableDetail -> {
            val tableNum by viewModel.selectedTableNumber.collectAsStateWithLifecycle()
            "${Screen.TableDetail.route}:$tableNum"
        }
        is Screen.FormulaDetail -> {
            val formulaId by viewModel.selectedFormulaId.collectAsStateWithLifecycle()
            "${Screen.FormulaDetail.route}:$formulaId"
        }
        is Screen.PracticeTutor -> {
            val tutorNum by viewModel.tutorNumber.collectAsStateWithLifecycle()
            "${Screen.PracticeTutor.route}:$tutorNum"
        }
        is Screen.BcsPreparation -> {
            val profile by viewModel.selectedBcsExamProfile.collectAsStateWithLifecycle()
            "${Screen.BcsPreparation.route}:${profile.id}"
        }
        is Screen.BookDetail -> {
            val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
            "${Screen.BookDetail.route}:${bookId.id}"
        }
        is Screen.BookChapterOverview -> {
            val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
            val chapter by viewModel.selectedBookChapterNumber.collectAsStateWithLifecycle()
            "${Screen.BookChapterOverview.route}:${bookId.id}:$chapter"
        }
        is Screen.BookLesson -> {
            val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
            val chapter by viewModel.selectedBookChapterNumber.collectAsStateWithLifecycle()
            val lessonId by viewModel.selectedBookLessonId.collectAsStateWithLifecycle()
            "${Screen.BookLesson.route}:${bookId.id}:$chapter:$lessonId"
        }
        is Screen.BookPdfReader -> {
            val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
            val printedPage by viewModel.selectedBookPrintedPage.collectAsStateWithLifecycle()
            "${Screen.BookPdfReader.route}:${bookId.id}:$printedPage"
        }
        is Screen.BookInteractiveLab -> {
            val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
            val chapter by viewModel.selectedBookChapterNumber.collectAsStateWithLifecycle()
            val labType by viewModel.selectedBookLabType.collectAsStateWithLifecycle()
            "${Screen.BookInteractiveLab.route}:${bookId.id}:$chapter:${labType.id}"
        }
        is Screen.BookRevision -> {
            val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
            "${Screen.BookRevision.route}:${bookId.id}"
        }
        else -> currentScreen.route
    }
}

@Composable
fun MainAppContent(viewModel: MathViewModel) {
    val screenStack by viewModel.screenStack.collectAsStateWithLifecycle()
    val currentScreen = screenStack.lastOrNull() ?: Screen.Home
    val saveableStateHolder = rememberSaveableStateHolder()
    val currentDestinationKey = rememberCurrentDestinationStateKey(viewModel, currentScreen)

    val transientPlayRoutes = remember {
        setOf(
            Screen.PracticeLabPlay.route,
            Screen.MentalMathPlay.route,
            Screen.NumberSystemPractice.route,
            Screen.QuizPlay.route,
            Screen.BookPractice.route,
            Screen.Customize.route
        )
    }
    val activeRoutes = remember(screenStack) { screenStack.map { it.route }.toSet() }
    val previousKeys = remember { mutableSetOf<String>() }

    DisposableEffect(activeRoutes, currentDestinationKey) {
        val removedKeys = previousKeys.filter { key ->
            val baseRoute = key.substringBefore(':')
            baseRoute !in activeRoutes && baseRoute in transientPlayRoutes
        }
        removedKeys.forEach { key ->
            saveableStateHolder.removeState(key)
            previousKeys.remove(key)
        }
        previousKeys.add(currentDestinationKey)
        onDispose { }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_STOP -> viewModel.onAppBackgrounded()
                androidx.lifecycle.Lifecycle.Event.ON_START -> viewModel.onAppForegrounded()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = screenStack.size > 1) {
        viewModel.navigateBack()
    }

    val showBottomNav = currentScreen in listOf(
        Screen.Home,
        Screen.Tables,
        Screen.Dashboard,
        Screen.Bookmarks,
        Screen.Quizzes
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Show bottom navigation bar when on primary sections
            if (showBottomNav) {
                NavigationBar(modifier = Modifier.testTag("app_bottom_nav")) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Home,
                        onClick = { viewModel.navigateToHome() },
                        icon = { Icon(Icons.Default.Home, contentDescription = "হোম") },
                        label = { Text("হোম") },
                        modifier = Modifier.testTag("nav_item_home")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Tables,
                        onClick = { viewModel.navigateToTab(Screen.Tables) },
                        icon = { Icon(Icons.Default.Functions, contentDescription = "নামতা") },
                        label = { Text("নামতা") },
                        modifier = Modifier.testTag("nav_item_tables")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Dashboard,
                        onClick = { viewModel.navigateToTab(Screen.Dashboard) },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "অগ্রগতি") },
                        label = { Text("অগ্রগতি") },
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Bookmarks,
                        onClick = { viewModel.navigateToTab(Screen.Bookmarks) },
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = "বুকমার্ক") },
                        label = { Text("বুকমার্ক") },
                        modifier = Modifier.testTag("nav_item_bookmarks")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Quizzes,
                        onClick = { viewModel.navigateToTab(Screen.Quizzes) },
                        icon = { Icon(Icons.Default.Quiz, contentDescription = "কুইজ") },
                        label = { Text("কুইজ") },
                        modifier = Modifier.testTag("nav_item_quizzes")
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = if (showBottomNav) {
            Modifier.padding(innerPadding)
        } else {
            Modifier
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.navigationBars)
        }
        saveableStateHolder.SaveableStateProvider(key = currentDestinationKey) {
            when (currentScreen) {
                is Screen.Home -> HomeScreen(viewModel = viewModel, modifier = modifier)
                is Screen.Tables -> TablesScreen(viewModel = viewModel, modifier = modifier)
                is Screen.TableDetail -> TableDetailScreen(viewModel = viewModel, modifier = modifier)
                is Screen.Primes -> PrimesScreen(viewModel = viewModel, modifier = modifier)
                is Screen.SquaresCubes -> SquaresCubesScreen(viewModel = viewModel, modifier = modifier)
                is Screen.Formulas -> FormulasScreen(viewModel = viewModel, modifier = modifier)
                is Screen.FormulaDetail -> FormulaDetailScreen(viewModel = viewModel, modifier = modifier)
                is Screen.FormulaLibrary -> FormulaLibraryScreen(viewModel = viewModel, modifier = modifier)
                is Screen.PracticeLab -> PracticeLabScreen(viewModel = viewModel, modifier = modifier)
                is Screen.PracticeLabPlay -> PracticeLabPlayScreen(viewModel = viewModel, modifier = modifier)
                is Screen.MentalMath -> MentalMathScreen(viewModel = viewModel, modifier = modifier)
                is Screen.MentalMathPlay -> MentalMathPlayScreen(viewModel = viewModel, modifier = modifier)
                is Screen.NumberSystem -> NumberSystemScreen(viewModel = viewModel, modifier = modifier)
                is Screen.NumberSystemPractice -> NumberSystemPracticePlayScreen(viewModel = viewModel, modifier = modifier)
                is Screen.Quizzes -> QuizzesScreen(viewModel = viewModel, modifier = modifier)
                is Screen.QuizPlay -> QuizPlayScreen(viewModel = viewModel, modifier = modifier)
                is Screen.Bookmarks -> BookmarksScreen(viewModel = viewModel, modifier = modifier)
                is Screen.PracticeTutor -> PracticeTutorScreen(viewModel = viewModel, modifier = modifier)
                is Screen.Dashboard -> DashboardScreen(viewModel = viewModel, modifier = modifier)
                is Screen.Customize -> CustomizeScreen(viewModel = viewModel, modifier = modifier)
                is Screen.ProblemSolver -> ProblemSolverScreen(viewModel = viewModel, modifier = modifier)
                is Screen.VisualExplorer -> com.example.ui.screens.VisualExplorerScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BcsPreparation -> com.example.ui.screens.BcsPreparationScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookHub -> BookHubScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookDetail -> BookDetailScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookChapterOverview -> BookChapterOverviewScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookLesson -> BookLessonScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookPractice -> BookPracticeScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookPdfReader -> BookPdfReaderScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookInteractiveLab -> BookInteractiveLabScreen(viewModel = viewModel, modifier = modifier)
                is Screen.BookRevision -> BookRevisionScreen(viewModel = viewModel, modifier = modifier)
            }
        }
    }
}
