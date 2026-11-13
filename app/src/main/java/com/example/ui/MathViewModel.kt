package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MathDatabase
import com.example.data.model.BookLessonProgressEntity
import com.example.data.model.BookMistakeEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.BookReadingStateEntity
import com.example.data.model.BookSpacedRevisionEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.RecentFormulaEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.model.TopicAnalyticsEntity
import com.example.data.repository.MathRepository
import com.example.math.AnalyticsTimeFilter
import com.example.math.AnsweredMentalQuestion
import com.example.math.BcsPreparationRepository
import com.example.math.BcsSyllabusUnit
import com.example.math.BcsSyllabusVersion
import com.example.math.BengaliNumberFormatter
import com.example.math.BookChapterSpec
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookInteractiveLabType
import com.example.math.BookPracticeMode
import com.example.math.BookPracticeSessionState
import com.example.math.BookQuestionEngine
import com.example.math.BookQuestionFormat
import com.example.math.BookSearchFilterType
import com.example.math.DefaultMonotonicClock
import com.example.math.ExamConfig
import com.example.math.ExamDetailedResult
import com.example.math.ExamHistoryFilter
import com.example.math.ExamHistorySort
import com.example.math.ExamPresetType
import com.example.math.ExamQuestionBank
import com.example.math.ExamScoringEngine
import com.example.math.LearningRecommendation
import com.example.math.LibraryFormula
import com.example.math.MentalMathCategory
import com.example.math.MentalMathDifficulty
import com.example.math.MentalMathGenerator
import com.example.math.MentalMathMode
import com.example.math.MentalMathQuestion
import com.example.math.MentalMathSessionState
import com.example.math.MonotonicClock
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.math.PracticeLabGenerator
import com.example.math.PracticeQuestion
import com.example.math.PracticeQuestionGenerator
import com.example.math.PracticeSessionState
import com.example.math.QuestionInputFormat
import com.example.math.QuizGenerator
import com.example.math.QuizMode
import com.example.math.QuizQuestion
import com.example.math.QuizQuestionType
import com.example.math.QuizState
import com.example.math.QuizTopic
import com.example.math.RecommendationActionType
import com.example.math.SharedMathEngine
import com.example.math.SimilarProblemGenerator
import com.example.math.SolverResult
import com.example.math.SolverTopic
import com.example.math.TableRange
import com.example.ui.customization.AppCustomizationState
import com.example.ui.customization.CustomizationPreferences
import com.example.ui.customization.GlobalCustomConfig
import com.example.ui.customization.SectionCustomConfig
import com.example.ui.customization.SectionKey
import com.example.ui.theme.AppThemeMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Tables : Screen("tables")
    object TableDetail : Screen("table_detail")
    object Primes : Screen("primes")
    object SquaresCubes : Screen("squares_cubes")
    object Formulas : Screen("formulas")
    object FormulaDetail : Screen("formula_detail")
    object FormulaLibrary : Screen("formula_library")
    object PracticeLab : Screen("practice_lab")
    object PracticeLabPlay : Screen("practice_lab_play")
    object MentalMath : Screen("mental_math")
    object MentalMathPlay : Screen("mental_math_play")
    object NumberSystem : Screen("number_system")
    object NumberSystemPractice : Screen("number_system_practice")
    object Quizzes : Screen("quizzes")
    object QuizPlay : Screen("quiz_play")
    object Bookmarks : Screen("bookmarks")
    object PracticeTutor : Screen("practice_tutor")
    object Dashboard : Screen("dashboard")
    object Customize : Screen("customize")
    object ProblemSolver : Screen("problem_solver")
    object VisualExplorer : Screen("visual_explorer")
    object BcsPreparation : Screen("bcs_preparation")
    object BookHub : Screen("book_hub")
    object BookDetail : Screen("book_detail")
    object BookChapterOverview : Screen("book_chapter_overview")
    object BookLesson : Screen("book_lesson")
    object BookPractice : Screen("book_practice")
    object BookPdfReader : Screen("book_pdf_reader")
    object BookInteractiveLab : Screen("book_interactive_lab")
    object BookRevision : Screen("book_revision")
}

data class WeeklyDayStat(
    val dayNameBn: String,
    val dateStr: String,
    val count: Int,
    val isToday: Boolean,
    val isMet: Boolean
)

/**
 * In-memory Home Screen vertical scroll position (first visible item index and exact pixel offset)
 * preserved across module navigation and configuration changes within the active app session.
 */
data class HomeScrollPosition(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0
)

/**
 * Application-wide scroll position state (firstVisibleItemIndex and exact pixel offset)
 * keyed by destination / subsection / tab stateKey (BUG-37 / BUG-38 / BUG-39).
 */
data class ScrollPositionState(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0
)

/**
 * Snapshot of active parameterized arguments for a backstack entry so returning via
 * navigateBack() restores the parent screen's exact book/chapter/lesson/table/formula context.
 */
data class NavigationContextSnapshot(
    val screen: Screen,
    val tableNumber: Int,
    val formulaId: String,
    val tutorNumber: Int,
    val bookId: BookId,
    val bookChapterNumber: Int,
    val bookLessonId: String,
    val bookPrintedPage: Int,
    val bookLabType: BookInteractiveLabType
)

/**
 * Concise Bengali display statuses for Study Timer 2.0 (BUG-78).
 */
enum class StudyTimerDisplayStatus(val labelBn: String) {
    OFF("বন্ধ"),
    READY("প্রস্তুত"),
    RUNNING("চলমান"),
    PAUSED("বিরতি"),
    COMPLETED("সম্পন্ন")
}

/**
 * Persistent state for the Home Screen 'Study Session' learning timer component.
 * Initializes in the compact OFF state (isVisibleOnHome = false, isRunning = false) on every cold process launch (BUG-78).
 */
data class StudySessionTimerState(
    val isVisibleOnHome: Boolean = false,
    val isRunning: Boolean = false,
    val currentSessionSeconds: Int = 0,
    val todayTotalSeconds: Int = 0,
    val allTimeTotalSeconds: Int = 0,
    val dailyGoalMinutes: Int = 30,
    val completedSessionsToday: Int = 0,
    val unrecordedDbSeconds: Int = 0,
    val lastSessionCompleted: Boolean = false,
    val activeDateKey: String = ""
) {
    val dailyGoalSeconds: Int
        get() = dailyGoalMinutes * 60

    val dailyGoalProgressFraction: Float
        get() = if (dailyGoalSeconds <= 0) 0f else (todayTotalSeconds.toFloat() / dailyGoalSeconds.toFloat()).coerceIn(0f, 1f)

    val isDailyGoalReached: Boolean
        get() = todayTotalSeconds >= dailyGoalSeconds

    val displayStatus: StudyTimerDisplayStatus
        get() = when {
            !isVisibleOnHome -> StudyTimerDisplayStatus.OFF
            isRunning -> StudyTimerDisplayStatus.RUNNING
            currentSessionSeconds > 0 -> StudyTimerDisplayStatus.PAUSED
            lastSessionCompleted -> StudyTimerDisplayStatus.COMPLETED
            else -> StudyTimerDisplayStatus.READY
        }

    val statusLabelBn: String
        get() = displayStatus.labelBn
}

data class QuizSessionState(
    val attemptId: String = "",
    val examTitleBn: String = "",
    val examConfig: ExamConfig = ExamConfig(),
    val isFullExamMode: Boolean = false,
    val topic: QuizTopic = QuizTopic.MULTIPLICATION,
    val mode: QuizMode = QuizMode.TOPIC,
    val questionType: QuizQuestionType = QuizQuestionType.MCQ,
    val difficulty: PracticeDifficulty = PracticeDifficulty.EASY,
    val state: QuizState = QuizState.IDLE,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val typedAnswer: String = "",
    val isAnswerSubmitted: Boolean = false,
    val correctCount: Int = 0,
    val isFinished: Boolean = false,
    val userAnswers: Map<Int, Int> = emptyMap(),
    val userTypedAnswers: Map<Int, String> = emptyMap(),
    val pendingOptionSelections: Map<Int, Int> = emptyMap(),
    val pendingTypedAnswers: Map<Int, String> = emptyMap(),
    val markedForReviewIndices: Set<Int> = emptySet(),
    val visitedIndices: Set<Int> = setOf(0),
    val questionTimeSeconds: Map<Int, Int> = emptyMap(),
    val isReviewMode: Boolean = false,
    val timeRemainingSeconds: Int = 120,
    val configuredDurationSeconds: Int = 120,
    val elapsedSeconds: Int = 0,
    val startElapsedRealtimeMs: Long = 0L,
    val deadlineElapsedRealtimeMs: Long = 0L,
    val detailedResult: ExamDetailedResult? = null,
    val showSubmitConfirmDialog: Boolean = false,
    val examProfileId: String = ""
) {
    val incorrectQuestions: List<QuizQuestion>
        get() = questions.filterIndexed { index, q ->
            val ans = userAnswers[index]
            ExamScoringEngine.isOptionAnswered(ans) && ans != q.correctIndex
        }

    val effectiveAnswersMap: Map<Int, Int>
        get() {
            if (isFullExamMode && pendingOptionSelections.isEmpty() && pendingTypedAnswers.isEmpty() &&
                (selectedOptionIndex == null || userAnswers.containsKey(currentIndex)) &&
                (typedAnswer.isBlank() || userAnswers.containsKey(currentIndex))
            ) {
                return userAnswers
            }
            val merged = userAnswers.toMutableMap()
            pendingOptionSelections.forEach { (qIdx, optIdx) ->
                if (!userAnswers.containsKey(qIdx)) {
                    merged[qIdx] = optIdx
                }
            }
            pendingTypedAnswers.forEach { (qIdx, rawText) ->
                val trimmed = rawText.trim()
                if (trimmed.isNotEmpty() && !userAnswers.containsKey(qIdx)) {
                    val q = questions.getOrNull(qIdx)
                    val recOpt = if (q != null) {
                        val isCorrect = SharedMathEngine.isAnswerCorrect(trimmed, q.correctAnswerText)
                        val matchIdx = q.optionsBn.indexOfFirst { SharedMathEngine.isAnswerCorrect(trimmed, it) }
                        if (matchIdx != -1) matchIdx else if (isCorrect) q.correctIndex else -2
                    } else 0
                    merged[qIdx] = recOpt
                }
            }
            if (!userAnswers.containsKey(currentIndex)) {
                if (selectedOptionIndex != null) {
                    merged[currentIndex] = selectedOptionIndex
                } else if (typedAnswer.isNotBlank()) {
                    val trimmed = typedAnswer.trim()
                    val q = questions.getOrNull(currentIndex)
                    val recOpt = if (q != null) {
                        val isCorrect = SharedMathEngine.isAnswerCorrect(trimmed, q.correctAnswerText)
                        val matchIdx = q.optionsBn.indexOfFirst { SharedMathEngine.isAnswerCorrect(trimmed, it) }
                        if (matchIdx != -1) matchIdx else if (isCorrect) q.correctIndex else -2
                    } else 0
                    merged[currentIndex] = recOpt
                }
            }
            return merged
        }
}

class MathViewModel @JvmOverloads constructor(
    application: Application,
    var clock: MonotonicClock = DefaultMonotonicClock()
) : AndroidViewModel(application) {
    private val repository: MathRepository
    private val prefs = application.getSharedPreferences("math_master_prefs", android.content.Context.MODE_PRIVATE)

    // Study Streaks & Daily Activity
    private val _streakDays = MutableStateFlow(1)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _longestStreak = MutableStateFlow(1)
    val longestStreak: StateFlow<Int> = _longestStreak.asStateFlow()

    private val _weeklyActivities = MutableStateFlow<List<WeeklyDayStat>>(emptyList())
    val weeklyActivities: StateFlow<List<WeeklyDayStat>> = _weeklyActivities.asStateFlow()

    // Persistent Study Session Timer State
    private val _studySessionTimer = MutableStateFlow(StudySessionTimerState())
    val studySessionTimer: StateFlow<StudySessionTimerState> = _studySessionTimer.asStateFlow()

    // Customization System State
    private val customPrefs = CustomizationPreferences(application)
    private val initialCustomState = customPrefs.loadCustomizationState()
    private val _customizationState = MutableStateFlow(initialCustomState)
    val customizationState: StateFlow<AppCustomizationState> = _customizationState.asStateFlow()

    // Live Preview Customization State
    private val _previewCustomizationState = MutableStateFlow(initialCustomState)
    val previewCustomizationState: StateFlow<AppCustomizationState> = _previewCustomizationState.asStateFlow()

    init {
        val db = MathDatabase.getInstance(application)
        repository = MathRepository(db.mathDao())
        initStreakAndActivity()
        initStudySessionTimer()
        viewModelScope.launch {
            repository.bcsUserProfile.collect { profile ->
                if (profile != null) {
                    val examProfile = com.example.math.BcsExamProfile.fromIdOrDefault(profile.selectedExamProfileId)
                    _selectedBcsExamProfile.value = examProfile
                    _selectedBcsSyllabusVersion.value = if (examProfile == com.example.math.BcsExamProfile.BCS_LEGACY_PRELIM) {
                        BcsSyllabusVersion.BCS_LEGACY_15
                    } else {
                        BcsSyllabusVersion.BCS_50TH_CURRENT
                    }
                }
            }
        }
    }

    // App Navigation Stack (custom backstack for seamless BackHandler support)
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val screenStack: StateFlow<List<Screen>> = _screenStack.asStateFlow()

    val currentScreen: Screen
        get() = _screenStack.value.lastOrNull() ?: Screen.Home

    // Home Screen Scroll Position State (preserves firstVisibleItemIndex and exact pixel offset across module navigation)
    private val _homeScrollPosition = MutableStateFlow(HomeScrollPosition())
    val homeScrollPosition: StateFlow<HomeScrollPosition> = _homeScrollPosition.asStateFlow()

    // Application-Wide Scroll Position & UI Context Registries (BUG-37 / BUG-38 / BUG-39)
    private val _scrollPositions = MutableStateFlow<Map<String, ScrollPositionState>>(emptyMap())
    val scrollPositions: StateFlow<Map<String, ScrollPositionState>> = _scrollPositions.asStateFlow()

    private val _uiContextStates = MutableStateFlow<Map<String, Any?>>(emptyMap())
    val uiContextStates: StateFlow<Map<String, Any?>> = _uiContextStates.asStateFlow()

    private val _navigationContextStack = mutableListOf<NavigationContextSnapshot>()

    companion object {
        private const val MAX_SAVED_STATE_ENTRIES = 256
    }

    private fun isHomeScrollKey(stateKey: String): Boolean {
        return stateKey == Screen.Home.route || stateKey == "${Screen.Home.route}:grid"
    }

    fun updateHomeScrollPosition(firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) {
        val safeIndex = firstVisibleItemIndex.coerceAtLeast(0)
        val safeOffset = firstVisibleItemScrollOffset.coerceAtLeast(0)
        val current = _homeScrollPosition.value
        if (current.firstVisibleItemIndex != safeIndex || current.firstVisibleItemScrollOffset != safeOffset) {
            _homeScrollPosition.value = HomeScrollPosition(
                firstVisibleItemIndex = safeIndex,
                firstVisibleItemScrollOffset = safeOffset
            )
        }
        val existing = _scrollPositions.value[Screen.Home.route]
        if (existing?.firstVisibleItemIndex != safeIndex || existing.firstVisibleItemScrollOffset != safeOffset) {
            val nextMap = LinkedHashMap(_scrollPositions.value)
            val pos = ScrollPositionState(safeIndex, safeOffset)
            nextMap[Screen.Home.route] = pos
            nextMap["${Screen.Home.route}:grid"] = pos
            _scrollPositions.value = nextMap
        }
    }

    fun resetHomeScrollPosition() {
        _homeScrollPosition.value = HomeScrollPosition(0, 0)
        val nextMap = LinkedHashMap(_scrollPositions.value)
        val zero = ScrollPositionState(0, 0)
        nextMap[Screen.Home.route] = zero
        nextMap["${Screen.Home.route}:grid"] = zero
        _scrollPositions.value = nextMap
    }

    fun getScrollPosition(stateKey: String): ScrollPositionState {
        if (isHomeScrollKey(stateKey)) {
            val home = _homeScrollPosition.value
            return ScrollPositionState(home.firstVisibleItemIndex, home.firstVisibleItemScrollOffset)
        }
        return _scrollPositions.value[stateKey] ?: ScrollPositionState()
    }

    fun updateScrollPosition(
        stateKey: String,
        firstVisibleItemIndex: Int,
        firstVisibleItemScrollOffset: Int
    ) {
        val safeIndex = firstVisibleItemIndex.coerceAtLeast(0)
        val safeOffset = firstVisibleItemScrollOffset.coerceAtLeast(0)
        if (isHomeScrollKey(stateKey)) {
            updateHomeScrollPosition(safeIndex, safeOffset)
            return
        }
        val currentMap = _scrollPositions.value
        val existing = currentMap[stateKey]
        if (existing?.firstVisibleItemIndex == safeIndex && existing.firstVisibleItemScrollOffset == safeOffset) {
            return
        }
        val nextMap = LinkedHashMap<String, ScrollPositionState>(currentMap.size + 1)
        nextMap.putAll(currentMap)
        nextMap.remove(stateKey)
        nextMap[stateKey] = ScrollPositionState(safeIndex, safeOffset)
        while (nextMap.size > MAX_SAVED_STATE_ENTRIES && nextMap.isNotEmpty()) {
            val eldestKey = nextMap.keys.firstOrNull { !isHomeScrollKey(it) } ?: break
            nextMap.remove(eldestKey)
        }
        _scrollPositions.value = nextMap
    }

    fun resetScrollPosition(stateKey: String) {
        if (isHomeScrollKey(stateKey)) {
            resetHomeScrollPosition()
            return
        }
        val nextMap = LinkedHashMap(_scrollPositions.value)
        nextMap[stateKey] = ScrollPositionState(0, 0)
        _scrollPositions.value = nextMap
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getUiContextState(stateKey: String, defaultValue: T): T {
        if (!_uiContextStates.value.containsKey(stateKey)) {
            return defaultValue
        }
        val raw = _uiContextStates.value[stateKey]
        return try {
            raw as T
        } catch (_: ClassCastException) {
            defaultValue
        }
    }

    fun <T> updateUiContextState(stateKey: String, value: T) {
        val currentMap = _uiContextStates.value
        if (currentMap.containsKey(stateKey) && currentMap[stateKey] == value) {
            return
        }
        val nextMap = LinkedHashMap<String, Any?>(currentMap.size + 1)
        nextMap.putAll(currentMap)
        nextMap.remove(stateKey)
        nextMap[stateKey] = value
        while (nextMap.size > MAX_SAVED_STATE_ENTRIES && nextMap.isNotEmpty()) {
            val eldestKey = nextMap.keys.firstOrNull() ?: break
            nextMap.remove(eldestKey)
        }
        _uiContextStates.value = nextMap
    }

    fun resetUiContextState(stateKey: String) {
        val nextMap = LinkedHashMap(_uiContextStates.value)
        nextMap.remove(stateKey)
        _uiContextStates.value = nextMap
    }

    fun getDestinationStateKey(screen: Screen = currentScreen): String {
        return when (screen) {
            is Screen.Home -> Screen.Home.route
            is Screen.Tables -> Screen.Tables.route
            is Screen.TableDetail -> "${Screen.TableDetail.route}:${_selectedTableNumber.value}"
            is Screen.FormulaDetail -> "${Screen.FormulaDetail.route}:${_selectedFormulaId.value}"
            is Screen.PracticeTutor -> "${Screen.PracticeTutor.route}:${_tutorNumber.value}"
            is Screen.BcsPreparation -> "${Screen.BcsPreparation.route}:${_selectedBcsExamProfile.value.id}"
            is Screen.BookDetail -> "${Screen.BookDetail.route}:${_selectedBookId.value.id}"
            is Screen.BookChapterOverview -> "${Screen.BookChapterOverview.route}:${_selectedBookId.value.id}:${_selectedBookChapterNumber.value}"
            is Screen.BookLesson -> "${Screen.BookLesson.route}:${_selectedBookId.value.id}:${_selectedBookChapterNumber.value}:${_selectedBookLessonId.value}"
            is Screen.BookPdfReader -> "${Screen.BookPdfReader.route}:${_selectedBookId.value.id}:${_selectedBookPrintedPage.value}"
            is Screen.BookInteractiveLab -> "${Screen.BookInteractiveLab.route}:${_selectedBookId.value.id}:${_selectedBookChapterNumber.value}:${_selectedBookLabType.value.id}"
            is Screen.BookRevision -> "${Screen.BookRevision.route}:${_selectedBookId.value.id}"
            else -> screen.route
        }
    }

    private fun captureCurrentNavigationSnapshot(screen: Screen): NavigationContextSnapshot {
        return NavigationContextSnapshot(
            screen = screen,
            tableNumber = _selectedTableNumber.value,
            formulaId = _selectedFormulaId.value,
            tutorNumber = _tutorNumber.value,
            bookId = _selectedBookId.value,
            bookChapterNumber = _selectedBookChapterNumber.value,
            bookLessonId = _selectedBookLessonId.value,
            bookPrintedPage = _selectedBookPrintedPage.value,
            bookLabType = _selectedBookLabType.value
        )
    }

    private fun syncTopNavigationSnapshot() {
        val currentTop = currentScreen
        val snapshot = captureCurrentNavigationSnapshot(currentTop)
        if (_navigationContextStack.isEmpty()) {
            _navigationContextStack.add(snapshot)
        } else {
            _navigationContextStack[_navigationContextStack.lastIndex] = snapshot
        }
    }

    private fun restoreNavigationSnapshot(snapshot: NavigationContextSnapshot) {
        when (snapshot.screen) {
            is Screen.TableDetail -> _selectedTableNumber.value = snapshot.tableNumber
            is Screen.FormulaDetail -> _selectedFormulaId.value = snapshot.formulaId
            is Screen.PracticeTutor -> _tutorNumber.value = snapshot.tutorNumber
            is Screen.BookDetail -> _selectedBookId.value = snapshot.bookId
            is Screen.BookChapterOverview -> {
                _selectedBookId.value = snapshot.bookId
                _selectedBookChapterNumber.value = snapshot.bookChapterNumber
            }
            is Screen.BookLesson -> {
                _selectedBookId.value = snapshot.bookId
                _selectedBookChapterNumber.value = snapshot.bookChapterNumber
                _selectedBookLessonId.value = snapshot.bookLessonId
            }
            is Screen.BookPdfReader -> {
                _selectedBookId.value = snapshot.bookId
                _selectedBookPrintedPage.value = snapshot.bookPrintedPage
            }
            is Screen.BookInteractiveLab -> {
                _selectedBookId.value = snapshot.bookId
                _selectedBookChapterNumber.value = snapshot.bookChapterNumber
                _selectedBookLabType.value = snapshot.bookLabType
            }
            is Screen.BookRevision -> {
                _selectedBookId.value = snapshot.bookId
            }
            else -> {}
        }
    }

    // Active Selection State
    private val _selectedTableNumber = MutableStateFlow(1)
    val selectedTableNumber: StateFlow<Int> = _selectedTableNumber.asStateFlow()

    private val _selectedFormulaId = MutableStateFlow("sq_1")
    val selectedFormulaId: StateFlow<String> = _selectedFormulaId.asStateFlow()

    private val _selectedTableRange = MutableStateFlow(TableRange.RANGE_1_10)
    val selectedTableRange: StateFlow<TableRange> = _selectedTableRange.asStateFlow()

    // Theme Mode - Loaded from persistent SharedPreferences
    private val initialTheme: AppThemeMode = try {
        val savedName = prefs.getString("selected_theme_mode", AppThemeMode.SYSTEM.name)
        AppThemeMode.valueOf(savedName ?: AppThemeMode.SYSTEM.name)
    } catch (_: Exception) {
        AppThemeMode.SYSTEM
    }

    private val _themeMode = MutableStateFlow(initialTheme)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Persistent Database State
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedIds: StateFlow<Set<String>> = repository.bookmarks
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val quizScores: StateFlow<List<QuizScoreEntity>> = repository.quizScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val progressList: StateFlow<List<ProgressEntity>> = repository.progressList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room v3 Analytics & History Flows
    val practiceHistory: StateFlow<List<PracticeHistoryEntity>> = repository.allPracticeHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topicAnalytics: StateFlow<List<TopicAnalyticsEntity>> = repository.allTopicAnalytics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentFormulas: StateFlow<List<RecentFormulaEntity>> = repository.recentFormulas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room v4 Examination, Solver History & Daily Study Flows
    val completedExamAttempts: StateFlow<List<ExamAttemptEntity>> = repository.completedExamAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeExamAttempt: StateFlow<ExamAttemptEntity?> = repository.activeExamAttempt
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val solverHistory: StateFlow<List<SolverHistoryEntity>> = repository.allSolverHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyStudyActivities: StateFlow<List<DailyStudyActivityEntity>> = repository.allDailyStudyActivity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room v5 Offline Book Module Flows
    val bookLessonProgress: StateFlow<List<BookLessonProgressEntity>> = repository.allBookLessonProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookReadingStates: StateFlow<List<BookReadingStateEntity>> = repository.allBookReadingStates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookNotes: StateFlow<List<BookNoteEntity>> = repository.allBookNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookMistakes: StateFlow<List<BookMistakeEntity>> = repository.allBookMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookSpacedRevisions: StateFlow<List<BookSpacedRevisionEntity>> = repository.allBookSpacedRevisions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room v6 / v8 BCS Preparation Flows
    val bcsUserProfile: StateFlow<com.example.data.model.BcsUserProfileEntity?> = repository.bcsUserProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allBcsUserProfiles: StateFlow<List<com.example.data.model.BcsUserProfileEntity>> = repository.allBcsUserProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bcsTopicProgress: StateFlow<List<com.example.data.model.BcsTopicProgressEntity>> = repository.allBcsTopicProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bcsMistakes: StateFlow<List<com.example.data.model.BcsMistakeEntity>> = repository.allBcsMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bcsStudyGoals: StateFlow<List<com.example.data.model.BcsStudyPlanGoalEntity>> = repository.allBcsStudyGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room v7 Cross-Module Unified Mistake Notebook & Formula Mastery Flows
    val unifiedMistakes: StateFlow<List<com.example.data.model.UnifiedMistakeEntity>> = repository.allUnifiedMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val formulaMastery: StateFlow<List<com.example.data.model.FormulaMasteryEntity>> = repository.allFormulaMastery
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun resolveUnifiedMistakeItem(mistakeId: String) {
        viewModelScope.launch {
            repository.resolveUnifiedMistake(mistakeId)
        }
    }

    fun recordFormulaMasteryInteraction(formulaId: String, categoryId: String, isPracticed: Boolean = false) {
        viewModelScope.launch {
            repository.recordFormulaMastery(
                formulaId = formulaId,
                subjectId = categoryId,
                masteryLevel = if (isPracticed) "MASTERED" else "LEARNING"
            )
        }
    }

    // Analytics & Exam History Filter State
    private val _dashboardTimeFilter = MutableStateFlow(AnalyticsTimeFilter.ALL_TIME)
    val dashboardTimeFilter: StateFlow<AnalyticsTimeFilter> = _dashboardTimeFilter.asStateFlow()

    private val _examHistoryFilter = MutableStateFlow(ExamHistoryFilter.ALL)
    val examHistoryFilter: StateFlow<ExamHistoryFilter> = _examHistoryFilter.asStateFlow()

    private val _examHistorySort = MutableStateFlow(ExamHistorySort.NEWEST_FIRST)
    val examHistorySort: StateFlow<ExamHistorySort> = _examHistorySort.asStateFlow()

    private val _comparisonAttemptIds = MutableStateFlow<Set<String>>(emptySet())
    val comparisonAttemptIds: StateFlow<Set<String>> = _comparisonAttemptIds.asStateFlow()

    // Quiz Session
    private val _quizSession = MutableStateFlow(QuizSessionState())
    val quizSession: StateFlow<QuizSessionState> = _quizSession.asStateFlow()

    // Last incorrect quiz questions for Review Incorrect Quiz mode
    private val _incorrectQuizQuestionPool = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val incorrectQuizQuestionPool: StateFlow<List<QuizQuestion>> = _incorrectQuizQuestionPool.asStateFlow()

    // Math Practice Lab State
    private val _practiceSession = MutableStateFlow(PracticeSessionState())
    val practiceSession: StateFlow<PracticeSessionState> = _practiceSession.asStateFlow()

    // Mental Math Challenge State
    private val _mentalMathSession = MutableStateFlow(MentalMathSessionState())
    val mentalMathSession: StateFlow<MentalMathSessionState> = _mentalMathSession.asStateFlow()

    // Step-by-Step Interactive Practice Tutor State
    private val _tutorNumber = MutableStateFlow(7)
    val tutorNumber: StateFlow<Int> = _tutorNumber.asStateFlow()
    private val _tutorStep = MutableStateFlow(1) // 1 to 10
    val tutorStep: StateFlow<Int> = _tutorStep.asStateFlow()
    private val _tutorUserAnswer = MutableStateFlow("")
    val tutorUserAnswer: StateFlow<String> = _tutorUserAnswer.asStateFlow()
    private val _tutorFeedback = MutableStateFlow<String?>(null)
    val tutorFeedback: StateFlow<String?> = _tutorFeedback.asStateFlow()
    private val _tutorCompleted = MutableStateFlow(false)
    val tutorCompleted: StateFlow<Boolean> = _tutorCompleted.asStateFlow()

    fun navigateTo(screen: Screen) {
        val current = _screenStack.value
        if (_navigationContextStack.size != current.size) {
            _navigationContextStack.clear()
            current.forEach { s -> _navigationContextStack.add(captureCurrentNavigationSnapshot(s)) }
        }
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
            _navigationContextStack.add(captureCurrentNavigationSnapshot(screen))
        } else {
            syncTopNavigationSnapshot()
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        if (current.size > 1) {
            val nextStack = current.dropLast(1)
            if (_navigationContextStack.size == current.size && _navigationContextStack.isNotEmpty()) {
                _navigationContextStack.removeAt(_navigationContextStack.lastIndex)
                _navigationContextStack.lastOrNull()?.let { restoreNavigationSnapshot(it) }
            }
            _screenStack.value = nextStack
            return true
        } else if (current.firstOrNull() != Screen.Home) {
            _screenStack.value = listOf(Screen.Home)
            _navigationContextStack.clear()
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Home))
            return true
        }
        return false
    }

    fun navigateToHome() {
        _screenStack.value = listOf(Screen.Home)
        _navigationContextStack.clear()
        _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Home))
    }

    fun navigateToTab(screen: Screen) {
        if (screen == Screen.Home) {
            _screenStack.value = listOf(Screen.Home)
            _navigationContextStack.clear()
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Home))
        } else {
            _screenStack.value = listOf(Screen.Home, screen)
            _navigationContextStack.clear()
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Home))
            _navigationContextStack.add(captureCurrentNavigationSnapshot(screen))
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("selected_theme_mode", mode.name).apply()
    }

    fun selectRange(range: TableRange) {
        _selectedTableRange.value = range
    }

    fun openTable(number: Int) {
        _selectedTableNumber.value = number
        navigateTo(Screen.TableDetail)
    }

    private val _selectedLibraryFormulaId = MutableStateFlow<String?>(null)
    val selectedLibraryFormulaId: StateFlow<String?> = _selectedLibraryFormulaId.asStateFlow()

    fun openFormula(formulaId: String) {
        _selectedFormulaId.value = formulaId
        navigateTo(Screen.FormulaDetail)
    }

    fun openLibraryFormulaFromBookmark(formulaId: String) {
        _selectedLibraryFormulaId.value = formulaId
        navigateTo(Screen.FormulaLibrary)
    }

    fun clearSelectedLibraryFormulaId() {
        _selectedLibraryFormulaId.value = null
    }

    fun recordFormulaView(formula: LibraryFormula) {
        viewModelScope.launch {
            repository.recordFormulaViewed(
                formulaId = formula.id,
                titleBn = formula.titleBn,
                subjectName = formula.subject.name
            )
            recordDailyActivity(1)
        }
    }

    fun isBookmarked(id: String): Boolean {
        return bookmarkedIds.value.contains(id)
    }

    fun toggleBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            val currently = isBookmarked(bookmark.id)
            repository.toggleBookmark(bookmark, currently)
        }
    }

    fun removeBookmark(id: String) {
        viewModelScope.launch {
            repository.removeBookmark(id)
        }
    }

    fun isLearned(itemId: String): Boolean {
        return progressList.value.find { it.itemId == itemId }?.isLearned ?: false
    }

    fun toggleLearned(itemId: String) {
        viewModelScope.launch {
            val currently = isLearned(itemId)
            repository.toggleLearned(itemId, currently)
            if (!currently) {
                recordDailyActivity(2)
            }
        }
    }

    // ==========================================
    // MODULE 06: INTERACTIVE QUIZ OPERATIONS
    // ==========================================
    fun startQuiz(
        topic: QuizTopic,
        count: Int,
        mode: QuizMode,
        questionType: QuizQuestionType,
        difficulty: PracticeDifficulty
    ) {
        startQuiz(
            topic = topic,
            count = count,
            mode = mode,
            difficulty = difficulty,
            questionType = questionType,
            timedSeconds = 120
        )
    }

    @JvmOverloads
    fun startQuiz(
        topic: QuizTopic,
        count: Int = 10,
        mode: QuizMode = QuizMode.TOPIC,
        difficulty: PracticeDifficulty = PracticeDifficulty.EASY,
        questionType: QuizQuestionType = QuizQuestionType.MCQ,
        timedSeconds: Int = 120
    ) {
        _quizSession.value = _quizSession.value.copy(state = QuizState.LOADING)
        val effectiveTopic = if (mode == QuizMode.MIXED) QuizTopic.MIXED_CHALLENGE else topic
        val questions = if (mode == QuizMode.REVIEW_INCORRECT && _incorrectQuizQuestionPool.value.isNotEmpty()) {
            _incorrectQuizQuestionPool.value.shuffled().take(count)
        } else {
            QuizGenerator.generateQuiz(
                topic = effectiveTopic,
                questionCount = count,
                difficulty = difficulty,
                questionType = questionType
            )
        }

        val now = clock.elapsedRealtime()
        val effectiveTimedSeconds = if (mode == QuizMode.TIMED) timedSeconds.coerceAtLeast(1) else 0
        val deadline = if (effectiveTimedSeconds > 0) now + effectiveTimedSeconds * 1000L else 0L
        val attemptId = "exam_${System.currentTimeMillis()}_${effectiveTopic.id}"
        val preset = when (mode) {
            QuizMode.TIMED -> ExamPresetType.TIMED_MOCK
            QuizMode.MIXED -> ExamPresetType.MIXED_TEST
            QuizMode.PRACTICE -> ExamPresetType.PRACTICE_EXAM
            QuizMode.REVIEW_INCORRECT -> ExamPresetType.REVIEW_INCORRECT
            else -> ExamPresetType.TOPIC_TEST
        }
        val config = ExamConfig(
            preset = preset,
            selectedTopics = setOf(effectiveTopic),
            difficulty = difficulty,
            questionType = questionType,
            questionCount = questions.size.coerceAtLeast(1),
            durationMinutes = if (effectiveTimedSeconds > 0) (effectiveTimedSeconds / 60).coerceAtLeast(1) else 0,
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = 0.0,
            immediateFeedback = (mode == QuizMode.PRACTICE)
        )

        val newState = QuizSessionState(
            attemptId = attemptId,
            examTitleBn = effectiveTopic.titleBn,
            examConfig = config,
            isFullExamMode = false,
            topic = effectiveTopic,
            mode = mode,
            questionType = questionType,
            difficulty = difficulty,
            state = if (questions.isNotEmpty()) QuizState.IN_PROGRESS else QuizState.ERROR,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            typedAnswer = "",
            isAnswerSubmitted = false,
            correctCount = 0,
            isFinished = false,
            userAnswers = emptyMap(),
            userTypedAnswers = emptyMap(),
            markedForReviewIndices = emptySet(),
            visitedIndices = setOf(0),
            questionTimeSeconds = emptyMap(),
            isReviewMode = false,
            timeRemainingSeconds = effectiveTimedSeconds,
            configuredDurationSeconds = effectiveTimedSeconds,
            elapsedSeconds = 0,
            startElapsedRealtimeMs = now,
            deadlineElapsedRealtimeMs = deadline,
            detailedResult = null,
            showSubmitConfirmDialog = false
        )
        _quizSession.value = newState
        persistActiveExamStateAsync(newState)
        navigateTo(Screen.QuizPlay)
    }

    fun startConfiguredExam(config: ExamConfig) {
        _quizSession.value = _quizSession.value.copy(state = QuizState.LOADING)
        val questions = if (config.preset == ExamPresetType.REVIEW_INCORRECT && _incorrectQuizQuestionPool.value.isNotEmpty()) {
            _incorrectQuizQuestionPool.value.shuffled().take(config.questionCount)
        } else {
            ExamQuestionBank.generateExamQuestions(config)
        }
        val primaryTopic = config.selectedTopics.firstOrNull() ?: QuizTopic.MIXED_CHALLENGE
        val isUntimedPractice = config.immediateFeedback || config.preset == ExamPresetType.PRACTICE_EXAM
        val durationSec = if (isUntimedPractice) 0 else config.durationSeconds
        val mappedMode = when (config.preset) {
            ExamPresetType.PRACTICE_EXAM -> QuizMode.PRACTICE
            ExamPresetType.REVIEW_INCORRECT -> QuizMode.REVIEW_INCORRECT
            ExamPresetType.MIXED_TEST, ExamPresetType.FULL_LENGTH_MOCK, ExamPresetType.BCS_PRELIMINARY_MOCK -> {
                if (durationSec > 0) QuizMode.TIMED else QuizMode.PRACTICE
            }
            else -> if (durationSec > 0) QuizMode.TIMED else if (config.immediateFeedback) QuizMode.PRACTICE else QuizMode.TOPIC
        }
        val now = clock.elapsedRealtime()
        val deadline = if (durationSec > 0) now + durationSec * 1000L else 0L
        val attemptId = "exam_${System.currentTimeMillis()}_${config.preset.id}"

        val newState = QuizSessionState(
            attemptId = attemptId,
            examTitleBn = config.toExamTitleBn(),
            examConfig = config,
            isFullExamMode = !config.immediateFeedback,
            topic = primaryTopic,
            mode = mappedMode,
            questionType = config.questionType,
            difficulty = config.difficulty,
            state = if (questions.isNotEmpty()) QuizState.IN_PROGRESS else QuizState.ERROR,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            typedAnswer = "",
            isAnswerSubmitted = false,
            correctCount = 0,
            isFinished = false,
            userAnswers = emptyMap(),
            userTypedAnswers = emptyMap(),
            markedForReviewIndices = emptySet(),
            visitedIndices = setOf(0),
            questionTimeSeconds = emptyMap(),
            isReviewMode = false,
            timeRemainingSeconds = durationSec,
            configuredDurationSeconds = durationSec,
            elapsedSeconds = 0,
            startElapsedRealtimeMs = now,
            deadlineElapsedRealtimeMs = deadline,
            detailedResult = null,
            showSubmitConfirmDialog = false
        )
        _quizSession.value = newState
        persistActiveExamStateAsync(newState)
        navigateTo(Screen.QuizPlay)
    }

    fun selectQuizOption(index: Int) {
        val current = _quizSession.value
        if (current.isFinished) return
        if (current.isFullExamMode) {
            val updatedAnswers = current.userAnswers + (current.currentIndex to index)
            val recomputedCorrect = updatedAnswers.count { (qIdx, ansIdx) ->
                current.questions.getOrNull(qIdx)?.correctIndex == ansIdx
            }
            val nextState = current.copy(
                selectedOptionIndex = index,
                userAnswers = updatedAnswers,
                correctCount = recomputedCorrect
            )
            _quizSession.value = nextState
            persistActiveExamStateAsync(nextState)
        } else if (!current.isAnswerSubmitted) {
            _quizSession.value = current.copy(
                selectedOptionIndex = index,
                pendingOptionSelections = current.pendingOptionSelections + (current.currentIndex to index)
            )
        }
    }

    fun setQuizTypedAnswer(text: String) {
        val current = _quizSession.value
        if (current.isFinished) return
        val currentQ = current.questions.getOrNull(current.currentIndex)
        if (current.isFullExamMode && currentQ != null && (currentQ.questionType == QuizQuestionType.NUMERICAL || current.questionType == QuizQuestionType.NUMERICAL)) {
            val trimmed = text.trim()
            val updatedTyped = if (trimmed.isEmpty()) {
                current.userTypedAnswers - current.currentIndex
            } else {
                current.userTypedAnswers + (current.currentIndex to trimmed)
            }
            val updatedAnswers = if (trimmed.isEmpty()) {
                current.userAnswers - current.currentIndex
            } else {
                val isCorrect = SharedMathEngine.isAnswerCorrect(trimmed, currentQ.correctAnswerText)
                val matchIdx = currentQ.optionsBn.indexOfFirst { SharedMathEngine.isAnswerCorrect(trimmed, it) }
                val recordedOption = if (matchIdx != -1) matchIdx else if (isCorrect) currentQ.correctIndex else -2
                current.userAnswers + (current.currentIndex to recordedOption)
            }
            val recomputedCorrect = updatedAnswers.count { (qIdx, ansIdx) ->
                current.questions.getOrNull(qIdx)?.correctIndex == ansIdx
            }
            val nextState = current.copy(
                typedAnswer = text,
                userTypedAnswers = updatedTyped,
                userAnswers = updatedAnswers,
                correctCount = recomputedCorrect
            )
            _quizSession.value = nextState
            persistActiveExamStateAsync(nextState)
        } else if (!current.isAnswerSubmitted) {
            val updatedPendingTyped = if (text.isEmpty()) {
                current.pendingTypedAnswers - current.currentIndex
            } else {
                current.pendingTypedAnswers + (current.currentIndex to text)
            }
            _quizSession.value = current.copy(
                typedAnswer = text,
                pendingTypedAnswers = updatedPendingTyped
            )
        }
    }

    fun toggleMarkCurrentQuestionForReview() {
        val current = _quizSession.value
        if (current.isFinished) return
        val idx = current.currentIndex
        val updatedMarked = if (current.markedForReviewIndices.contains(idx)) {
            current.markedForReviewIndices - idx
        } else {
            current.markedForReviewIndices + idx
        }
        val nextState = current.copy(markedForReviewIndices = updatedMarked)
        _quizSession.value = nextState
        persistActiveExamStateAsync(nextState)
    }

    fun clearCurrentQuizAnswer() {
        val current = _quizSession.value
        if (current.isFinished) return
        if (!current.isFullExamMode && current.isAnswerSubmitted) return
        val idx = current.currentIndex
        val updatedAnswers = current.userAnswers - idx
        val updatedTyped = current.userTypedAnswers - idx
        val updatedPendingOptions = current.pendingOptionSelections - idx
        val updatedPendingTyped = current.pendingTypedAnswers - idx
        val recomputedCorrect = updatedAnswers.count { (qIdx, ansIdx) ->
            current.questions.getOrNull(qIdx)?.correctIndex == ansIdx
        }
        val nextState = current.copy(
            selectedOptionIndex = null,
            typedAnswer = "",
            isAnswerSubmitted = false,
            state = QuizState.IN_PROGRESS,
            userAnswers = updatedAnswers,
            userTypedAnswers = updatedTyped,
            pendingOptionSelections = updatedPendingOptions,
            pendingTypedAnswers = updatedPendingTyped,
            correctCount = recomputedCorrect
        )
        _quizSession.value = nextState
        persistActiveExamStateAsync(nextState)
    }

    fun jumpToQuizQuestion(targetIndex: Int) {
        val current = _quizSession.value
        if (current.isFinished || targetIndex !in current.questions.indices) return
        val targetQ = current.questions.getOrNull(targetIndex)
        val existingChoice = if (targetQ?.questionType == QuizQuestionType.NUMERICAL) {
            null
        } else {
            current.userAnswers[targetIndex]?.takeIf { it >= 0 }
                ?: current.pendingOptionSelections[targetIndex]
        }
        val existingTyped = current.userTypedAnswers[targetIndex]
            ?: current.pendingTypedAnswers[targetIndex]
            ?: ""
        val alreadyAnswered = if (current.isFullExamMode) false else current.userAnswers.containsKey(targetIndex)
        val nextState = current.copy(
            currentIndex = targetIndex,
            selectedOptionIndex = existingChoice,
            typedAnswer = existingTyped,
            isAnswerSubmitted = alreadyAnswered,
            visitedIndices = current.visitedIndices + targetIndex,
            state = if (alreadyAnswered) QuizState.ANSWER_SUBMITTED else QuizState.IN_PROGRESS
        )
        _quizSession.value = nextState
        persistActiveExamStateAsync(nextState)
    }

    fun submitQuizAnswer() {
        val current = _quizSession.value
        if (current.isAnswerSubmitted || current.isFinished) return
        val currentQ = current.questions.getOrNull(current.currentIndex) ?: return

        val selectedOpt = current.selectedOptionIndex
        val isCorrect: Boolean
        val recordedOption: Int
        val updatedTypedMap = current.userTypedAnswers.toMutableMap()

        if (currentQ.questionType == QuizQuestionType.NUMERICAL && selectedOpt == null) {
            val typed = current.typedAnswer.trim()
            if (typed.isEmpty()) return
            isCorrect = SharedMathEngine.isAnswerCorrect(typed, currentQ.correctAnswerText)
            val matchIdx = currentQ.optionsBn.indexOfFirst { SharedMathEngine.isAnswerCorrect(typed, it) }
            recordedOption = if (matchIdx != -1) matchIdx else if (isCorrect) currentQ.correctIndex else -2
            updatedTypedMap[current.currentIndex] = typed
        } else {
            if (selectedOpt == null) return
            recordedOption = selectedOpt
            isCorrect = selectedOpt == currentQ.correctIndex
        }

        val updatedAnswers = current.userAnswers + (current.currentIndex to recordedOption)
        val recomputedCorrect = updatedAnswers.count { (qIdx, ansIdx) ->
            current.questions.getOrNull(qIdx)?.correctIndex == ansIdx
        }

        if (!isCorrect) {
            val pool = _incorrectQuizQuestionPool.value
            if (pool.none { it.questionBn == currentQ.questionBn }) {
                _incorrectQuizQuestionPool.value = (pool + currentQ).takeLast(30)
            }
        }

        val nextState = current.copy(
            state = QuizState.ANSWER_SUBMITTED,
            isAnswerSubmitted = true,
            selectedOptionIndex = if (currentQ.questionType == QuizQuestionType.NUMERICAL) null else if (recordedOption >= 0) recordedOption else null,
            correctCount = recomputedCorrect,
            userAnswers = updatedAnswers,
            userTypedAnswers = updatedTypedMap,
            pendingOptionSelections = current.pendingOptionSelections - current.currentIndex,
            pendingTypedAnswers = current.pendingTypedAnswers - current.currentIndex,
            visitedIndices = current.visitedIndices + current.currentIndex
        )
        _quizSession.value = nextState
        persistActiveExamStateAsync(nextState)

        val isBcsSession = current.examProfileId.isNotBlank() || current.examConfig.preset == ExamPresetType.BCS_PRELIMINARY_MOCK
        val activeBcsProfileId = current.examProfileId.ifBlank { _selectedBcsExamProfile.value.id }
        val quizUnifiedKey = "QUIZ_${currentQ.topicTitleBn}_${kotlin.math.abs(currentQ.questionBn.hashCode())}"
        val userSummaryBn = updatedTypedMap[current.currentIndex]?.takeIf { it.isNotBlank() }
            ?: currentQ.optionsBn.getOrNull(recordedOption)
            ?: "ভুল সংখ্যা ইনপুট"
        val correctAnsBn = currentQ.optionsBn.getOrElse(currentQ.correctIndex) { currentQ.correctAnswerText }

        viewModelScope.launch {
            if (!isCorrect) {
                repository.recordUnifiedMistake(
                    mistakeKey = quizUnifiedKey,
                    sourceModule = if (isBcsSession) "BCS" else "QUIZ",
                    subjectId = currentQ.topicTitleBn,
                    curriculumTrack = if (isBcsSession) "BCS" else "NCTB",
                    questionPromptBn = currentQ.questionBn,
                    userAnswerBn = userSummaryBn,
                    correctAnswerBn = correctAnsBn,
                    diagnosticExplanationBn = currentQ.explanationBn
                )
                if (isBcsSession) {
                    repository.recordBcsMistake(
                        questionId = currentQ.id.ifBlank { "bcs_q_${kotlin.math.abs(currentQ.questionBn.hashCode())}" },
                        subjectTrack = "MATH_REASONING",
                        topicId = QuizTopic.values().firstOrNull { it.titleBn == currentQ.topicTitleBn }?.id ?: currentQ.topicTitleBn,
                        questionPromptBn = currentQ.questionBn,
                        wrongAnswerBn = userSummaryBn,
                        correctAnswerBn = correctAnsBn,
                        explanationBn = currentQ.explanationBn,
                        examProfileId = activeBcsProfileId
                    )
                }
            } else {
                repository.resolveUnifiedMistake(quizUnifiedKey)
                if (isBcsSession && currentQ.id.isNotBlank()) {
                    repository.markBcsMistakeResolved(
                        questionId = currentQ.id,
                        examProfileId = activeBcsProfileId
                    )
                }
            }
        }

        if (isCorrect) {
            recordDailyActivity(1)
        }
    }

    fun previousQuizQuestion() {
        val current = _quizSession.value
        if (current.isFinished || current.currentIndex <= 0) return
        val prevIdx = current.currentIndex - 1
        val prevQ = current.questions.getOrNull(prevIdx)
        val prevSelected = if (prevQ?.questionType == QuizQuestionType.NUMERICAL) {
            null
        } else {
            current.userAnswers[prevIdx]?.takeIf { it >= 0 }
                ?: current.pendingOptionSelections[prevIdx]
        }
        val prevTyped = current.userTypedAnswers[prevIdx]
            ?: current.pendingTypedAnswers[prevIdx]
            ?: ""
        val alreadySubmitted = if (current.isFullExamMode) false else current.userAnswers.containsKey(prevIdx)
        val nextState = current.copy(
            currentIndex = prevIdx,
            selectedOptionIndex = prevSelected,
            typedAnswer = prevTyped,
            isAnswerSubmitted = alreadySubmitted,
            visitedIndices = current.visitedIndices + prevIdx,
            state = if (alreadySubmitted) QuizState.ANSWER_SUBMITTED else QuizState.IN_PROGRESS
        )
        _quizSession.value = nextState
        persistActiveExamStateAsync(nextState)
    }

    fun nextQuizQuestion() {
        val current = _quizSession.value
        if (!current.isFullExamMode && !current.isAnswerSubmitted) return

        val nextIndex = current.currentIndex + 1
        if (nextIndex < current.questions.size) {
            val nextQ = current.questions.getOrNull(nextIndex)
            val existingChoice = if (nextQ?.questionType == QuizQuestionType.NUMERICAL) {
                null
            } else {
                current.userAnswers[nextIndex]?.takeIf { it >= 0 }
                    ?: current.pendingOptionSelections[nextIndex]
            }
            val existingTyped = current.userTypedAnswers[nextIndex]
                ?: current.pendingTypedAnswers[nextIndex]
                ?: ""
            val alreadyAnswered = if (current.isFullExamMode) false else current.userAnswers.containsKey(nextIndex)
            val nextState = current.copy(
                currentIndex = nextIndex,
                selectedOptionIndex = existingChoice,
                typedAnswer = existingTyped,
                isAnswerSubmitted = alreadyAnswered,
                visitedIndices = current.visitedIndices + nextIndex,
                state = if (alreadyAnswered) QuizState.ANSWER_SUBMITTED else QuizState.IN_PROGRESS
            )
            _quizSession.value = nextState
            persistActiveExamStateAsync(nextState)
        } else {
            finishQuizSession()
        }
    }

    fun requestExamSubmitConfirmation() {
        val current = _quizSession.value
        if (!current.isFinished) {
            _quizSession.value = current.copy(showSubmitConfirmDialog = true)
        }
    }

    fun dismissExamSubmitConfirmation() {
        val current = _quizSession.value
        _quizSession.value = current.copy(showSubmitConfirmDialog = false)
    }

    fun submitFullExamNow() {
        _quizSession.value = _quizSession.value.copy(showSubmitConfirmDialog = false)
        finishQuizSession()
    }

    fun tickQuizTimer() {
        val current = _quizSession.value
        if (current.isFinished) return
        val now = clock.elapsedRealtime()
        val elapsedSec = if (current.startElapsedRealtimeMs > 0L) {
            ((now - current.startElapsedRealtimeMs) / 1000L).toInt().coerceAtLeast(0)
        } else {
            current.elapsedSeconds + 1
        }
        val curQTime = (current.questionTimeSeconds[current.currentIndex] ?: 0) + 1
        val updatedQTimes = current.questionTimeSeconds + (current.currentIndex to curQTime)

        val isUntimedPractice = current.examConfig.immediateFeedback ||
            current.mode == QuizMode.PRACTICE ||
            current.configuredDurationSeconds <= 0
        if (isUntimedPractice) {
            _quizSession.value = current.copy(
                elapsedSeconds = elapsedSec,
                questionTimeSeconds = updatedQTimes
            )
            return
        }

        val deadline = current.deadlineElapsedRealtimeMs
        val rem = if (deadline > 0L) {
            val diff = deadline - now
            if (diff <= 0L) 0 else ((diff + 999L) / 1000L).toInt()
        } else {
            (current.timeRemainingSeconds - 1).coerceAtLeast(0)
        }
        if (rem <= 0) {
            finishQuizSession()
        } else if (rem != current.timeRemainingSeconds || elapsedSec != current.elapsedSeconds) {
            val nextState = current.copy(
                timeRemainingSeconds = rem,
                elapsedSeconds = elapsedSec,
                questionTimeSeconds = updatedQTimes
            )
            _quizSession.value = nextState
            if (elapsedSec % 5 == 0 || rem <= 30) {
                persistActiveExamStateAsync(nextState)
            }
        }
    }

    private fun finishQuizSession() {
        val current = _quizSession.value
        if (current.isFinished) return

        // Merge any unsubmitted draft selections or typed numerical inputs across visited questions
        val mergedAnswers = current.userAnswers.toMutableMap()
        val mergedTypedAnswers = current.userTypedAnswers.toMutableMap()

        current.pendingOptionSelections.forEach { (qIdx, optIdx) ->
            if (!current.userAnswers.containsKey(qIdx)) {
                mergedAnswers[qIdx] = optIdx
            }
        }
        current.pendingTypedAnswers.forEach { (qIdx, rawText) ->
            val trimmed = rawText.trim()
            val q = current.questions.getOrNull(qIdx)
            if (trimmed.isNotEmpty() && q != null && !current.userAnswers.containsKey(qIdx)) {
                val isCorrect = SharedMathEngine.isAnswerCorrect(trimmed, q.correctAnswerText)
                val matchIdx = q.optionsBn.indexOfFirst { SharedMathEngine.isAnswerCorrect(trimmed, it) }
                val recOpt = if (matchIdx != -1) matchIdx else if (isCorrect) q.correctIndex else -2
                mergedTypedAnswers[qIdx] = trimmed
                mergedAnswers[qIdx] = recOpt
            }
        }

        val activeQ = current.questions.getOrNull(current.currentIndex)
        if (!current.userAnswers.containsKey(current.currentIndex)) {
            if (current.selectedOptionIndex != null) {
                mergedAnswers[current.currentIndex] = current.selectedOptionIndex
            } else if (
                activeQ?.questionType == QuizQuestionType.NUMERICAL &&
                current.typedAnswer.trim().isNotEmpty()
            ) {
                val typed = current.typedAnswer.trim()
                val isCorrect = SharedMathEngine.isAnswerCorrect(typed, activeQ.correctAnswerText)
                val matchIdx = activeQ.optionsBn.indexOfFirst { SharedMathEngine.isAnswerCorrect(typed, it) }
                val recOpt = if (matchIdx != -1) matchIdx else if (isCorrect) activeQ.correctIndex else -2
                mergedTypedAnswers[current.currentIndex] = typed
                mergedAnswers[current.currentIndex] = recOpt
            }
        }

        val finalAnswers = mergedAnswers.toMap()
        val finalTypedAnswers = mergedTypedAnswers.toMap()

        val elapsedSec = if (current.startElapsedRealtimeMs > 0L) {
            ((clock.elapsedRealtime() - current.startElapsedRealtimeMs) / 1000L).toInt().coerceAtLeast(1)
        } else {
            current.elapsedSeconds.coerceAtLeast(10)
        }

        val resolvedAttemptId = current.attemptId.ifBlank { "exam_${System.currentTimeMillis()}" }
        val resolvedTitle = current.examTitleBn.ifBlank { current.topic.titleBn }

        val detailed = ExamScoringEngine.evaluateExam(
            attemptId = resolvedAttemptId,
            examTitleBn = resolvedTitle,
            examType = current.examConfig.preset.id,
            questions = current.questions,
            selectedAnswers = finalAnswers,
            markedForReview = current.markedForReviewIndices,
            questionTimeSeconds = current.questionTimeSeconds,
            marksPerCorrect = current.examConfig.marksPerCorrect,
            negativeMarksPerWrong = current.examConfig.negativeMarksPerWrong,
            timeSpentSeconds = elapsedSec,
            durationLimitSeconds = current.configuredDurationSeconds,
            completedAt = System.currentTimeMillis()
        )

        // Track wrong questions in incorrectQuizQuestionPool for Review Incorrect mode
        val wrongQs = current.questions.filterIndexed { index, q ->
            val ans = finalAnswers[index]
            ExamScoringEngine.isOptionAnswered(ans) && ans != q.correctIndex
        }
        if (wrongQs.isNotEmpty()) {
            _incorrectQuizQuestionPool.value = (_incorrectQuizQuestionPool.value + wrongQs)
                .distinctBy { it.questionBn }
                .takeLast(30)
        }

        _quizSession.value = current.copy(
            attemptId = resolvedAttemptId,
            examTitleBn = resolvedTitle,
            userAnswers = finalAnswers,
            userTypedAnswers = finalTypedAnswers,
            pendingOptionSelections = emptyMap(),
            pendingTypedAnswers = emptyMap(),
            correctCount = detailed.correctCount,
            isFinished = true,
            state = QuizState.COMPLETED,
            timeRemainingSeconds = 0,
            elapsedSeconds = elapsedSec,
            detailedResult = detailed,
            showSubmitConfirmDialog = false
        )

        viewModelScope.launch {
            val completedEntity = ExamAttemptEntity(
                attemptId = resolvedAttemptId,
                examTitleBn = resolvedTitle,
                examType = current.examConfig.preset.id,
                selectedTopicsCsv = current.examConfig.selectedTopics.joinToString(",") { it.id }.ifBlank { current.topic.id },
                difficulty = current.difficulty.id,
                questionType = current.questionType.id,
                totalQuestions = detailed.totalQuestions,
                durationLimitSeconds = current.configuredDurationSeconds,
                marksPerCorrect = detailed.marksPerCorrect,
                negativeMarksPerWrong = detailed.negativeMarksPerWrong,
                status = "COMPLETED",
                currentQuestionIndex = current.currentIndex,
                elapsedSeconds = elapsedSec,
                remainingSeconds = 0,
                questionsJson = ExamQuestionBank.serializeQuestions(current.questions),
                answersJson = ExamQuestionBank.serializeAnswersWithTyped(finalAnswers, finalTypedAnswers),
                markedForReviewCsv = ExamQuestionBank.serializeIntSet(current.markedForReviewIndices),
                visitedIndicesCsv = ExamQuestionBank.serializeIntSet(current.visitedIndices),
                questionTimeSecondsJson = ExamQuestionBank.serializeIntMap(current.questionTimeSeconds),
                correctCount = detailed.correctCount,
                wrongCount = detailed.wrongCount,
                unansweredCount = detailed.unansweredCount,
                earnedMarks = detailed.earnedPositiveMarks,
                negativeMarksDeducted = detailed.negativeMarksDeducted,
                finalScore = detailed.finalScore,
                percentage = detailed.percentage,
                accuracyPercent = detailed.accuracyPercent,
                startedAt = System.currentTimeMillis() - (elapsedSec * 1000L),
                updatedAt = System.currentTimeMillis(),
                completedAt = detailed.completedAt,
                examProfileId = current.examProfileId
            )
            repository.finalizeExamAttempt(
                completedAttempt = completedEntity,
                perTopicStats = ExamScoringEngine.toTopicExamStats(detailed.topicBreakdown)
            )
            val isBcsSession = current.examProfileId.isNotBlank() || current.examConfig.preset == ExamPresetType.BCS_PRELIMINARY_MOCK
            val activeBcsProfileId = current.examProfileId.ifBlank { _selectedBcsExamProfile.value.id }
            if (isBcsSession) {
                detailed.topicBreakdown.forEach { tb ->
                    if (tb.attemptedCount > 0) {
                        repository.recordBcsTopicProgress(
                            topicId = tb.topicId,
                            subjectTrack = "MATH_REASONING",
                            isGuideCompleted = false,
                            miniCheckPassed = tb.accuracyPercent >= 75.0,
                            additionalTimeSeconds = tb.timeSpentSeconds,
                            questionsAttemptedDelta = tb.attemptedCount,
                            questionsCorrectDelta = tb.correctCount,
                            examProfileId = activeBcsProfileId
                        )
                    }
                }
            }
            // Sync mistakes & resolutions for questions not already persisted during immediate-feedback submitQuizAnswer()
            current.questions.forEachIndexed { index, q ->
                val ans = finalAnswers[index]
                if (!ExamScoringEngine.isOptionAnswered(ans)) return@forEachIndexed
                val quizUnifiedKey = "QUIZ_${q.topicTitleBn}_${kotlin.math.abs(q.questionBn.hashCode())}"
                val correctAnsBn = q.optionsBn.getOrElse(q.correctIndex) { q.correctAnswerText }
                if (ans != q.correctIndex) {
                    val wrongAnsBn = finalTypedAnswers[index]?.takeIf { it.isNotBlank() }
                        ?: q.optionsBn.getOrNull(ans ?: -1)
                        ?: "ভুল সংখ্যা ইনপুট"
                    if (current.isFullExamMode || !current.userAnswers.containsKey(index)) {
                        repository.recordUnifiedMistake(
                            mistakeKey = quizUnifiedKey,
                            sourceModule = if (isBcsSession) "BCS" else "QUIZ",
                            subjectId = q.topicTitleBn,
                            curriculumTrack = if (isBcsSession) "BCS" else "NCTB",
                            questionPromptBn = q.questionBn,
                            userAnswerBn = wrongAnsBn,
                            correctAnswerBn = correctAnsBn,
                            diagnosticExplanationBn = q.explanationBn
                        )
                        if (isBcsSession) {
                            repository.recordBcsMistake(
                                questionId = q.id.ifBlank { "bcs_q_${kotlin.math.abs(q.questionBn.hashCode())}" },
                                subjectTrack = "MATH_REASONING",
                                topicId = QuizTopic.values().firstOrNull { it.titleBn == q.topicTitleBn }?.id ?: q.topicTitleBn,
                                questionPromptBn = q.questionBn,
                                wrongAnswerBn = wrongAnsBn,
                                correctAnswerBn = correctAnsBn,
                                explanationBn = q.explanationBn,
                                examProfileId = activeBcsProfileId
                            )
                        }
                    }
                } else {
                    repository.resolveUnifiedMistake(quizUnifiedKey)
                    if (isBcsSession && q.id.isNotBlank()) {
                        repository.markBcsMistakeResolved(
                            questionId = q.id,
                            examProfileId = activeBcsProfileId
                        )
                    }
                }
            }
            recordDailyActivity(4)
        }
    }

    private fun persistActiveExamStateAsync(state: QuizSessionState) {
        if (state.isFinished || state.questions.isEmpty() || state.attemptId.isBlank()) return
        viewModelScope.launch {
            val answeredCount = state.userAnswers.count { ExamScoringEngine.isOptionAnswered(it.value) }
            val entity = ExamAttemptEntity(
                attemptId = state.attemptId,
                examTitleBn = state.examTitleBn.ifBlank { state.topic.titleBn },
                examType = state.examConfig.preset.id,
                selectedTopicsCsv = state.examConfig.selectedTopics.joinToString(",") { it.id }.ifBlank { state.topic.id },
                difficulty = state.difficulty.id,
                questionType = state.questionType.id,
                totalQuestions = state.questions.size,
                durationLimitSeconds = state.configuredDurationSeconds,
                marksPerCorrect = state.examConfig.marksPerCorrect,
                negativeMarksPerWrong = state.examConfig.negativeMarksPerWrong,
                status = "IN_PROGRESS",
                currentQuestionIndex = state.currentIndex,
                elapsedSeconds = state.elapsedSeconds,
                remainingSeconds = state.timeRemainingSeconds,
                questionsJson = ExamQuestionBank.serializeQuestions(state.questions),
                answersJson = ExamQuestionBank.serializeAnswersWithTyped(state.userAnswers, state.userTypedAnswers),
                markedForReviewCsv = ExamQuestionBank.serializeIntSet(state.markedForReviewIndices),
                visitedIndicesCsv = ExamQuestionBank.serializeIntSet(state.visitedIndices),
                questionTimeSecondsJson = ExamQuestionBank.serializeIntMap(state.questionTimeSeconds),
                correctCount = state.correctCount,
                wrongCount = (answeredCount - state.correctCount).coerceAtLeast(0),
                unansweredCount = (state.questions.size - answeredCount).coerceAtLeast(0),
                earnedMarks = state.correctCount * state.examConfig.marksPerCorrect,
                negativeMarksDeducted = 0.0,
                finalScore = state.correctCount * state.examConfig.marksPerCorrect,
                percentage = if (state.questions.isNotEmpty()) (state.correctCount * 100.0) / state.questions.size else 0.0,
                accuracyPercent = if (answeredCount > 0) (state.correctCount * 100.0) / answeredCount else 0.0,
                startedAt = System.currentTimeMillis() - (state.elapsedSeconds * 1000L),
                updatedAt = System.currentTimeMillis(),
                completedAt = null,
                examProfileId = state.examProfileId
            )
            repository.saveExamAttemptState(entity)
        }
    }

    fun resumeActiveExamAttempt(attemptEntity: ExamAttemptEntity? = null) {
        viewModelScope.launch {
            val entity = attemptEntity ?: repository.getActiveExamAttemptSnapshot() ?: return@launch
            val questions = ExamQuestionBank.deserializeQuestions(entity.questionsJson)
            if (questions.isEmpty()) return@launch
            val answers = ExamQuestionBank.deserializeIntMap(entity.answersJson)
            val typedAnswers = ExamQuestionBank.deserializeTypedMap(entity.answersJson, questions, answers)
            val marked = ExamQuestionBank.deserializeIntSet(entity.markedForReviewCsv)
            val visited = ExamQuestionBank.deserializeIntSet(entity.visitedIndicesCsv).ifEmpty { setOf(0) }
            val qTimes = ExamQuestionBank.deserializeIntMap(entity.questionTimeSecondsJson)
            val preset = ExamPresetType.fromIdOrDefault(entity.examType)
            val topics = entity.selectedTopicsCsv.split(",")
                .mapNotNull { id -> QuizTopic.values().firstOrNull { it.id == id.trim() } }
                .toSet()
                .ifEmpty { setOf(QuizTopic.MIXED_CHALLENGE) }
            val diff = PracticeDifficulty.values().firstOrNull { it.id == entity.difficulty } ?: PracticeDifficulty.MEDIUM
            val qType = QuizQuestionType.values().firstOrNull { it.id == entity.questionType } ?: QuizQuestionType.MCQ

            val remSec = if (entity.durationLimitSeconds > 0) {
                val computedRem = entity.durationLimitSeconds - entity.elapsedSeconds
                if (entity.remainingSeconds > 0) entity.remainingSeconds else computedRem.coerceAtLeast(1)
            } else {
                0
            }
            val now = clock.elapsedRealtime()
            val deadline = if (entity.durationLimitSeconds > 0) now + remSec * 1000L else 0L
            val curIdx = entity.currentQuestionIndex.coerceIn(0, questions.lastIndex)
            val curQ = questions.getOrNull(curIdx)
            val isImmediatePractice = preset == ExamPresetType.PRACTICE_EXAM ||
                entity.attemptId.startsWith("bcs_mode_topic_practice") ||
                entity.attemptId.startsWith("bcs_mode_mistake_practice")

            val restoredState = QuizSessionState(
                attemptId = entity.attemptId,
                examTitleBn = entity.examTitleBn,
                examConfig = ExamConfig(
                    preset = preset,
                    selectedTopics = topics,
                    difficulty = diff,
                    questionType = qType,
                    questionCount = questions.size,
                    durationMinutes = (entity.durationLimitSeconds / 60).coerceAtLeast(0),
                    marksPerCorrect = entity.marksPerCorrect,
                    negativeMarksPerWrong = entity.negativeMarksPerWrong,
                    immediateFeedback = isImmediatePractice
                ),
                isFullExamMode = !isImmediatePractice,
                topic = topics.first(),
                mode = if (isImmediatePractice) QuizMode.PRACTICE else if (entity.durationLimitSeconds > 0) QuizMode.TIMED else QuizMode.TOPIC,
                questionType = qType,
                difficulty = diff,
                state = QuizState.IN_PROGRESS,
                questions = questions,
                currentIndex = curIdx,
                selectedOptionIndex = if (curQ?.questionType == QuizQuestionType.NUMERICAL) null else answers[curIdx]?.takeIf { it >= 0 },
                typedAnswer = typedAnswers[curIdx] ?: "",
                isAnswerSubmitted = if (!isImmediatePractice) false else answers.containsKey(curIdx),
                correctCount = answers.count { (i, a) -> questions.getOrNull(i)?.correctIndex == a },
                isFinished = false,
                userAnswers = answers,
                userTypedAnswers = typedAnswers,
                markedForReviewIndices = marked,
                visitedIndices = visited + curIdx,
                questionTimeSeconds = qTimes,
                isReviewMode = false,
                timeRemainingSeconds = remSec,
                configuredDurationSeconds = entity.durationLimitSeconds,
                elapsedSeconds = entity.elapsedSeconds,
                startElapsedRealtimeMs = now - (entity.elapsedSeconds * 1000L),
                deadlineElapsedRealtimeMs = deadline,
                examProfileId = entity.examProfileId
            )
            _quizSession.value = restoredState
            navigateTo(Screen.QuizPlay)
        }
    }

    fun discardActiveExamAttempt() {
        viewModelScope.launch {
            repository.abandonActiveExams()
        }
    }

    fun openCompletedExamResult(entity: ExamAttemptEntity) {
        val questions = ExamQuestionBank.deserializeQuestions(entity.questionsJson)
        val answers = ExamQuestionBank.deserializeIntMap(entity.answersJson)
        val typedAnswers = ExamQuestionBank.deserializeTypedMap(entity.answersJson, questions, answers)
        val marked = ExamQuestionBank.deserializeIntSet(entity.markedForReviewCsv)
        val visited = ExamQuestionBank.deserializeIntSet(entity.visitedIndicesCsv)
        val detailed = ExamQuestionBank.reconstructDetailedResult(entity)
        val preset = ExamPresetType.fromIdOrDefault(entity.examType)

        _quizSession.value = QuizSessionState(
            attemptId = entity.attemptId,
            examTitleBn = entity.examTitleBn,
            examConfig = ExamConfig(
                preset = preset,
                questionCount = entity.totalQuestions,
                durationMinutes = entity.durationLimitSeconds / 60,
                marksPerCorrect = entity.marksPerCorrect,
                negativeMarksPerWrong = entity.negativeMarksPerWrong
            ),
            isFullExamMode = true,
            state = QuizState.COMPLETED,
            questions = questions,
            currentIndex = 0,
            correctCount = entity.correctCount,
            isFinished = true,
            userAnswers = answers,
            userTypedAnswers = typedAnswers,
            markedForReviewIndices = marked,
            visitedIndices = visited,
            isReviewMode = false,
            timeRemainingSeconds = 0,
            configuredDurationSeconds = entity.durationLimitSeconds,
            elapsedSeconds = entity.elapsedSeconds,
            detailedResult = detailed,
            examProfileId = entity.examProfileId
        )
        navigateTo(Screen.QuizPlay)
    }

    fun deleteExamAttemptFromHistory(attemptId: String) {
        viewModelScope.launch {
            repository.deleteExamAttempt(attemptId)
            _comparisonAttemptIds.value = _comparisonAttemptIds.value - attemptId
        }
    }

    fun toggleExamComparisonSelection(attemptId: String) {
        val cur = _comparisonAttemptIds.value
        _comparisonAttemptIds.value = if (cur.contains(attemptId)) {
            cur - attemptId
        } else if (cur.size < 2) {
            cur + attemptId
        } else {
            setOf(cur.last(), attemptId)
        }
    }

    fun setExamHistoryFilter(filter: ExamHistoryFilter) {
        _examHistoryFilter.value = filter
    }

    fun setExamHistorySort(sort: ExamHistorySort) {
        _examHistorySort.value = sort
    }

    fun practiceWeakExamTopics(weakTopicTitles: List<String>) {
        val matchedTopics = QuizTopic.values().filter { t ->
            weakTopicTitles.any { it.contains(t.titleBn.substringBefore(" ")) }
        }.toSet().ifEmpty { setOf(QuizTopic.MIXED_CHALLENGE) }

        startConfiguredExam(
            ExamConfig(
                preset = ExamPresetType.PRACTICE_EXAM,
                selectedTopics = matchedTopics,
                difficulty = PracticeDifficulty.MEDIUM,
                questionCount = 10,
                durationMinutes = 0,
                immediateFeedback = true
            )
        )
    }

    fun startReviewIncorrectQuiz() {
        val current = _quizSession.value
        val wrongFromCurrent = current.questions.filterIndexed { index, q ->
            val ans = current.userAnswers[index]
            ExamScoringEngine.isOptionAnswered(ans) && ans != q.correctIndex
        }
        val pool = if (wrongFromCurrent.isNotEmpty()) wrongFromCurrent else _incorrectQuizQuestionPool.value
        if (pool.isEmpty()) {
            startQuiz(current.topic, 10)
            return
        }
        _incorrectQuizQuestionPool.value = pool
        startQuiz(
            topic = current.topic,
            count = pool.size,
            mode = QuizMode.REVIEW_INCORRECT,
            difficulty = current.difficulty,
            questionType = current.questionType
        )
    }

    fun exitQuizToTopicList() {
        val current = _screenStack.value
        val quizzesIndex = current.indexOfLast { it == Screen.Quizzes }
        if (quizzesIndex != -1) {
            _screenStack.value = current.take(quizzesIndex + 1)
            while (_navigationContextStack.size > quizzesIndex + 1) {
                _navigationContextStack.removeAt(_navigationContextStack.lastIndex)
            }
            _navigationContextStack.lastOrNull()?.let { restoreNavigationSnapshot(it) }
        } else {
            _screenStack.value = listOf(Screen.Home, Screen.Quizzes)
            _navigationContextStack.clear()
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Home))
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Quizzes))
        }
        _quizSession.value = QuizSessionState()
    }

    fun startNextQuizTopic() {
        val currentTopic = _quizSession.value.topic
        val allTopics = QuizTopic.values()
        val currentIndex = allTopics.indexOf(currentTopic)
        val nextTopic = if (currentIndex != -1 && currentIndex + 1 < allTopics.size) {
            allTopics[currentIndex + 1]
        } else {
            allTopics.first()
        }
        startQuiz(nextTopic, 10)
    }

    fun toggleQuizReviewMode() {
        val current = _quizSession.value
        val nextReview = !current.isReviewMode
        _quizSession.value = current.copy(
            isReviewMode = nextReview,
            state = if (nextReview) QuizState.REVIEWING else QuizState.COMPLETED
        )
    }

    // ==========================================
    // MODULE 02: MATH PRACTICE LAB OPERATIONS
    // ==========================================
    @JvmOverloads
    fun startPracticeSession(
        category: PracticeCategory,
        difficulty: PracticeDifficulty,
        count: Int = 10,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ,
        isUnlimitedMode: Boolean = false
    ) {
        val initialCount = if (isUnlimitedMode) 15 else count
        val questions = PracticeLabGenerator.generateSession(category, difficulty, initialCount, inputFormat)
        _practiceSession.value = PracticeSessionState(
            category = category,
            difficulty = difficulty,
            activeAdaptiveDifficulty = if (difficulty == PracticeDifficulty.ADAPTIVE) PracticeDifficulty.EASY else difficulty,
            inputFormat = inputFormat,
            isUnlimitedMode = isUnlimitedMode,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            customInputText = "",
            isAnswerSubmitted = false,
            isCorrect = false,
            isHintRevealed = false,
            hintsUsedCount = 0,
            consecutiveCorrect = 0,
            consecutiveWrong = 0,
            correctCount = 0,
            wrongCount = 0,
            startTimeMs = System.currentTimeMillis(),
            totalTimeSeconds = 0L,
            isFinished = false,
            userAnswers = emptyMap(),
            isReviewMode = false
        )
        navigateTo(Screen.PracticeLabPlay)
    }

    @JvmOverloads
    fun startInterleavedPracticeChallenge(
        track: com.example.math.CurriculumTrack = com.example.math.CurriculumTrack.ALL,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ,
        count: Int = 10
    ) {
        val questions = PracticeLabGenerator.generateInterleavedChallenge(
            count = count,
            track = track,
            inputFormat = inputFormat
        )
        val firstCat = questions.firstOrNull()?.category ?: PracticeCategory.ARITHMETIC
        _practiceSession.value = PracticeSessionState(
            category = firstCat,
            difficulty = PracticeDifficulty.ADAPTIVE,
            activeAdaptiveDifficulty = PracticeDifficulty.MEDIUM,
            inputFormat = inputFormat,
            isUnlimitedMode = false,
            isInterleavedChallenge = true,
            selectedTrack = track,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            customInputText = "",
            isAnswerSubmitted = false,
            isCorrect = false,
            isHintRevealed = false,
            hintsUsedCount = 0,
            consecutiveCorrect = 0,
            consecutiveWrong = 0,
            correctCount = 0,
            wrongCount = 0,
            startTimeMs = System.currentTimeMillis(),
            totalTimeSeconds = 0L,
            isFinished = false,
            userAnswers = emptyMap(),
            isReviewMode = false
        )
        navigateTo(Screen.PracticeLabPlay)
    }

    fun selectPracticeOption(index: Int) {
        val current = _practiceSession.value
        if (!current.isAnswerSubmitted && !current.isFinished) {
            _practiceSession.value = current.copy(selectedOptionIndex = index)
        }
    }

    fun setPracticeCustomInput(text: String) {
        val current = _practiceSession.value
        if (!current.isAnswerSubmitted && !current.isFinished) {
            _practiceSession.value = current.copy(customInputText = text)
        }
    }

    fun revealPracticeHint() {
        val current = _practiceSession.value
        if (!current.isHintRevealed && !current.isAnswerSubmitted && !current.isFinished) {
            _practiceSession.value = current.copy(
                isHintRevealed = true,
                hintsUsedCount = current.hintsUsedCount + 1
            )
        }
    }

    fun submitPracticeAnswer() {
        val current = _practiceSession.value
        if (current.isAnswerSubmitted || current.isFinished) return
        val currentQ = current.questions.getOrNull(current.currentIndex) ?: return

        val selectedOpt = current.selectedOptionIndex
        val isCorrect: Boolean
        val effectiveSelectedOpt: Int

        when (current.inputFormat) {
            QuestionInputFormat.TRUE_FALSE -> {
                if (selectedOpt == null) return
                effectiveSelectedOpt = selectedOpt
                // Option 0 = সত্য (True), Option 1 = মিথ্যা (False)
                val chosenTrue = (selectedOpt == 0)
                isCorrect = (chosenTrue == currentQ.isStatementTrue)
            }
            QuestionInputFormat.KEYPAD -> {
                if (selectedOpt != null) {
                    effectiveSelectedOpt = selectedOpt
                    isCorrect = selectedOpt == currentQ.correctIndex
                } else if (current.customInputText.trim().isNotEmpty()) {
                    val userText = current.customInputText.trim()
                    val correctAns = currentQ.correctAnswerBn
                    val matchedIndex = currentQ.optionsBn.indexOfFirst {
                        SharedMathEngine.isAnswerCorrect(userText, it)
                    }
                    effectiveSelectedOpt = if (matchedIndex != -1) matchedIndex else -1
                    isCorrect = SharedMathEngine.isAnswerCorrect(userText, correctAns) ||
                            SharedMathEngine.isAnswerCorrect(userText, currentQ.optionsBn.getOrNull(currentQ.correctIndex) ?: "")
                } else {
                    return
                }
            }
            QuestionInputFormat.MCQ -> {
                if (selectedOpt != null) {
                    effectiveSelectedOpt = selectedOpt
                    isCorrect = selectedOpt == currentQ.correctIndex
                } else if (current.customInputText.trim().isNotEmpty()) {
                    val userText = current.customInputText.trim()
                    val correctAns = currentQ.correctAnswerBn
                    val matchedIndex = currentQ.optionsBn.indexOfFirst {
                        SharedMathEngine.isAnswerCorrect(userText, it)
                    }
                    effectiveSelectedOpt = if (matchedIndex != -1) matchedIndex else -1
                    isCorrect = SharedMathEngine.isAnswerCorrect(userText, correctAns) ||
                            SharedMathEngine.isAnswerCorrect(userText, currentQ.optionsBn.getOrNull(currentQ.correctIndex) ?: "")
                } else {
                    return
                }
            }
        }

        val updatedCorrect = if (isCorrect) current.correctCount + 1 else current.correctCount
        val updatedWrong = if (isCorrect) current.wrongCount else current.wrongCount + 1
        val newConsecCorrect = if (isCorrect) current.consecutiveCorrect + 1 else 0
        val newConsecWrong = if (!isCorrect) current.consecutiveWrong + 1 else 0
        val newBestStreak = maxOf(current.bestStreak, newConsecCorrect)
        val updatedAnswers = current.userAnswers + (current.currentIndex to effectiveSelectedOpt)
        val updatedOutcomes = current.questionOutcomes + (current.currentIndex to isCorrect)
        val updatedTextAnswers = if (current.customInputText.isNotBlank()) {
            current.userTextAnswers + (current.currentIndex to current.customInputText.trim())
        } else {
            current.userTextAnswers
        }

        // Dynamic adaptive difficulty adjustment
        val nextAdaptiveDiff = if (current.difficulty == PracticeDifficulty.ADAPTIVE) {
            when {
                newConsecCorrect >= 2 && current.activeAdaptiveDifficulty == PracticeDifficulty.EASY -> PracticeDifficulty.MEDIUM
                newConsecCorrect >= 2 && current.activeAdaptiveDifficulty == PracticeDifficulty.MEDIUM -> PracticeDifficulty.HARD
                newConsecWrong >= 2 && current.activeAdaptiveDifficulty == PracticeDifficulty.HARD -> PracticeDifficulty.MEDIUM
                newConsecWrong >= 2 && current.activeAdaptiveDifficulty == PracticeDifficulty.MEDIUM -> PracticeDifficulty.EASY
                else -> current.activeAdaptiveDifficulty
            }
        } else {
            current.activeAdaptiveDifficulty
        }

        _practiceSession.value = current.copy(
            isAnswerSubmitted = true,
            isCorrect = isCorrect,
            selectedOptionIndex = if (effectiveSelectedOpt >= 0) effectiveSelectedOpt else null,
            correctCount = updatedCorrect,
            wrongCount = updatedWrong,
            consecutiveCorrect = newConsecCorrect,
            consecutiveWrong = newConsecWrong,
            currentStreak = newConsecCorrect,
            bestStreak = newBestStreak,
            activeAdaptiveDifficulty = nextAdaptiveDiff,
            adaptiveTier = nextAdaptiveDiff,
            userAnswers = updatedAnswers,
            userTextAnswers = updatedTextAnswers,
            questionOutcomes = updatedOutcomes
        )

        if (isCorrect) {
            recordDailyActivity(1)
            viewModelScope.launch {
                repository.resolveUnifiedMistake("PRACTICE_${currentQ.id}")
            }
        } else {
            val chosenAnsText = if (current.customInputText.isNotBlank()) {
                current.customInputText.trim()
            } else {
                currentQ.optionsBn.getOrNull(effectiveSelectedOpt) ?: "ভুল উত্তর"
            }
            viewModelScope.launch {
                repository.recordUnifiedMistake(
                    mistakeKey = "PRACTICE_${currentQ.id}",
                    sourceModule = "PRACTICE_LAB",
                    subjectId = currentQ.category.id,
                    curriculumTrack = currentQ.curriculumTrack.name,
                    questionPromptBn = "${currentQ.questionBn} (${currentQ.expression})",
                    userAnswerBn = chosenAnsText,
                    correctAnswerBn = currentQ.correctAnswerBn,
                    diagnosticExplanationBn = currentQ.diagnosticFeedbackBn.ifBlank {
                        currentQ.stepByStepExplanationBn.joinToString(" ")
                    }
                )
            }
        }
    }

    fun nextPracticeQuestion() {
        val current = _practiceSession.value
        if (!current.isAnswerSubmitted) return

        val nextIndex = current.currentIndex + 1
        val updatedQuestions = if (current.isUnlimitedMode && nextIndex >= current.questions.size - 2) {
            val newQ = PracticeQuestionGenerator.generateNextAdaptiveQuestion(
                category = current.category,
                currentDifficulty = current.activeAdaptiveDifficulty,
                consecutiveCorrect = current.consecutiveCorrect,
                consecutiveWrong = current.consecutiveWrong,
                questionIndex = current.questions.size + 1,
                inputFormat = current.inputFormat
            )
            current.questions + newQ
        } else {
            current.questions
        }

        if (nextIndex < updatedQuestions.size) {
            _practiceSession.value = current.copy(
                questions = updatedQuestions,
                currentIndex = nextIndex,
                selectedOptionIndex = null,
                customInputText = "",
                isAnswerSubmitted = false,
                isCorrect = false,
                isHintRevealed = false
            )
        } else {
            finishPracticeSessionNow()
        }
    }

    fun finishPracticeSessionNow() {
        val current = _practiceSession.value
        if (current.isFinished) return
        val elapsedSec = ((System.currentTimeMillis() - current.startTimeMs) / 1000).coerceAtLeast(1)
        val totalAttempted = (current.correctCount + current.wrongCount).coerceAtLeast(1)
        val total = if (current.isUnlimitedMode) totalAttempted else current.questions.size.coerceAtLeast(1)
        val percentage = (current.correctCount * 100) / total

        _practiceSession.value = current.copy(
            isFinished = true,
            totalTimeSeconds = elapsedSec
        )

        viewModelScope.launch {
            val topicId = "PRACTICE_${current.category.id}_${current.difficulty.id}"
            val titleBn = "প্র্যাকটিস: ${current.category.titleBn} (${current.difficulty.titleBn})"
            repository.recordQuizScore(
                QuizScoreEntity(
                    topicId = topicId,
                    topicTitleBn = titleBn,
                    score = current.correctCount,
                    totalQuestions = total,
                    percentage = percentage
                )
            )
            repository.recordPractice("practice_${current.category.id}")
            repository.recordSessionAndAnalytics(
                moduleType = "PRACTICE_LAB",
                categoryId = current.category.id,
                categoryTitleBn = current.category.titleBn,
                difficulty = current.difficulty.id,
                mode = if (current.isUnlimitedMode) "unlimited" else "fixed",
                inputFormat = current.inputFormat.id,
                score = current.correctCount * 10,
                correctCount = current.correctCount,
                wrongCount = current.wrongCount,
                totalQuestions = total,
                accuracyPercent = percentage,
                durationSeconds = elapsedSec,
                highestStreak = maxOf(current.bestStreak, current.consecutiveCorrect)
            )
            recordDailyActivity(4)
        }
    }

    fun retryPracticeSession() {
        val current = _practiceSession.value
        startPracticeSession(
            category = current.category,
            difficulty = current.difficulty,
            count = current.questions.size.coerceAtLeast(5),
            inputFormat = current.inputFormat,
            isUnlimitedMode = current.isUnlimitedMode
        )
    }

    fun retryIncorrectPracticeQuestions() {
        val current = _practiceSession.value
        val wrongQuestions = current.incorrectQuestions
        if (wrongQuestions.isEmpty()) {
            retryPracticeSession()
            return
        }
        _practiceSession.value = PracticeSessionState(
            category = current.category,
            difficulty = current.difficulty,
            activeAdaptiveDifficulty = current.activeAdaptiveDifficulty,
            inputFormat = current.inputFormat,
            isUnlimitedMode = false,
            questions = wrongQuestions,
            currentIndex = 0,
            selectedOptionIndex = null,
            customInputText = "",
            isAnswerSubmitted = false,
            isCorrect = false,
            isHintRevealed = false,
            hintsUsedCount = 0,
            consecutiveCorrect = 0,
            consecutiveWrong = 0,
            correctCount = 0,
            wrongCount = 0,
            startTimeMs = System.currentTimeMillis(),
            totalTimeSeconds = 0L,
            isFinished = false,
            userAnswers = emptyMap(),
            isReviewMode = false
        )
    }

    fun togglePracticeReviewMode() {
        val current = _practiceSession.value
        _practiceSession.value = current.copy(isReviewMode = !current.isReviewMode)
    }

    fun exitPracticeToCategories() {
        val current = _screenStack.value
        val practiceIndex = current.indexOfLast { it == Screen.PracticeLab }
        if (practiceIndex != -1) {
            _screenStack.value = current.take(practiceIndex + 1)
            while (_navigationContextStack.size > practiceIndex + 1) {
                _navigationContextStack.removeAt(_navigationContextStack.lastIndex)
            }
            _navigationContextStack.lastOrNull()?.let { restoreNavigationSnapshot(it) }
        } else {
            _screenStack.value = listOf(Screen.Home, Screen.PracticeLab)
            _navigationContextStack.clear()
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Home))
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.PracticeLab))
        }
        _practiceSession.value = PracticeSessionState()
    }

    // ==========================================
    // MODULE 03: MENTAL MATH CHALLENGE ENGINE
    // ==========================================
    @JvmOverloads
    fun startMentalMathChallenge(
        category: MentalMathCategory,
        difficulty: MentalMathDifficulty,
        mode: MentalMathMode,
        durationOrCount: Int,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ
    ) {
        val isTimedMode = mode.isTimed
        val resolvedDuration = when (mode) {
            MentalMathMode.TIMED_SPRINT, MentalMathMode.CUSTOM_CHALLENGE -> durationOrCount.coerceAtLeast(1)
            MentalMathMode.QUICK_CHALLENGE, MentalMathMode.SPRINT_60S,
            MentalMathMode.SPRINT_2M, MentalMathMode.SPRINT_5M,
            MentalMathMode.DAILY_CHALLENGE -> if (durationOrCount > 0) durationOrCount else mode.presetDurationSeconds
            else -> 0
        }
        val resolvedQuestionCount = when (mode) {
            MentalMathMode.FIXED_QUESTIONS -> durationOrCount.coerceAtLeast(1)
            MentalMathMode.ACCURACY_CHALLENGE -> if (durationOrCount > 0) durationOrCount else mode.presetQuestionCount
            MentalMathMode.DAILY_CHALLENGE -> mode.presetQuestionCount
            else -> 0
        }
        val countToGenerate = if (resolvedQuestionCount > 0) resolvedQuestionCount else 35
        val initialQuestions = MentalMathGenerator.generateQuestions(category, difficulty, countToGenerate)
        val now = clock.elapsedRealtime()
        val deadline = if (isTimedMode) now + resolvedDuration * 1000L else 0L

        _mentalMathSession.value = MentalMathSessionState(
            category = category,
            difficulty = difficulty,
            mode = mode,
            inputFormat = inputFormat,
            configuredDurationSeconds = resolvedDuration,
            configuredQuestionCount = resolvedQuestionCount,
            questions = initialQuestions,
            currentIndex = 0,
            score = 0,
            timeBonusPoints = 0,
            streak = 0,
            highestStreak = 0,
            correctCount = 0,
            wrongCount = 0,
            timeRemainingSeconds = resolvedDuration,
            elapsedSeconds = 0,
            isAnswerSubmitted = false,
            selectedOptionIndex = null,
            typedInput = "",
            lastAnswerCorrect = null,
            feedbackMessageBn = null,
            isFinished = false,
            history = emptyList(),
            isReviewMode = false,
            startElapsedRealtimeMs = now,
            deadlineElapsedRealtimeMs = deadline
        )
        navigateTo(Screen.MentalMathPlay)
    }

    fun setMentalMathTypedInput(text: String) {
        val current = _mentalMathSession.value
        if (!current.isAnswerSubmitted && !current.isFinished) {
            _mentalMathSession.value = current.copy(typedInput = text)
        }
    }

    fun submitMentalMathTypedAnswer() {
        val current = _mentalMathSession.value
        if (current.isAnswerSubmitted || current.isFinished) return
        val typed = current.typedInput.trim()
        if (typed.isEmpty()) return
        val asciiDigits = BengaliNumberFormatter.toEnglishDigits(typed)
        val parsed = asciiDigits.toIntOrNull() ?: -999999
        submitMentalMathAnswer(parsed)
    }

    fun submitMentalMathTrueFalse(chosenTrue: Boolean) = submitMentalMathTrueFalseAnswer(chosenTrue)

    fun submitMentalMathTrueFalseAnswer(chosenTrue: Boolean) {
        val current = _mentalMathSession.value
        if (current.isAnswerSubmitted || current.isFinished) return
        val currentQ = current.questions.getOrNull(current.currentIndex) ?: return
        val valueToPass = if (chosenTrue == currentQ.isStatementTrue) {
            currentQ.correctAnswer
        } else {
            currentQ.options.firstOrNull { it != currentQ.correctAnswer } ?: -1
        }
        submitMentalMathAnswer(valueToPass)
    }

    fun submitMentalMathAnswer(optionValue: Int) {
        val current = _mentalMathSession.value
        if (current.isAnswerSubmitted || current.isFinished) return
        val currentQ = current.questions.getOrNull(current.currentIndex) ?: return

        val isCorrect = optionValue == currentQ.correctAnswer
        val optionIndex = currentQ.options.indexOf(optionValue)

        val newStreak = if (isCorrect) current.streak + 1 else 0
        val newHighestStreak = maxOf(current.highestStreak, newStreak)

        // Combo bonus: 1x, 1.5x (2+ streak), 2x (4+ streak), 2.5x (6+ streak)
        val comboMultiplier = when {
            newStreak >= 6 -> 2.5f
            newStreak >= 4 -> 2.0f
            newStreak >= 2 -> 1.5f
            else -> 1.0f
        }
        val baseEarned = if (isCorrect) (current.difficulty.basePoints * comboMultiplier).toInt() else 0
        // Speed / time bonus when in a timed mode
        val speedBonus = if (isCorrect && current.mode.isTimed && current.timeRemainingSeconds > 0) {
            (current.timeRemainingSeconds / 15).coerceIn(1, 5)
        } else 0
        val pointsEarned = baseEarned + speedBonus

        val feedbackMsg = if (isCorrect) {
            if (newStreak >= 3) "চমৎকার! ${BengaliNumberFormatter.toBn(newStreak)}টি টানা সঠিক (+${BengaliNumberFormatter.toBn(pointsEarned)})"
            else "সঠিক উত্তর! (+${BengaliNumberFormatter.toBn(pointsEarned)})"
        } else {
            "ভুল উত্তর! সঠিক: ${currentQ.correctAnswerBn}"
        }

        val answeredRecord = AnsweredMentalQuestion(
            question = currentQ,
            chosenOption = optionValue,
            isCorrect = isCorrect,
            timeTakenSeconds = 1
        )

        _mentalMathSession.value = current.copy(
            isAnswerSubmitted = true,
            selectedOptionIndex = optionIndex,
            lastAnswerCorrect = isCorrect,
            score = current.score + pointsEarned,
            timeBonusPoints = current.timeBonusPoints + speedBonus,
            streak = newStreak,
            highestStreak = newHighestStreak,
            correctCount = if (isCorrect) current.correctCount + 1 else current.correctCount,
            wrongCount = if (isCorrect) current.wrongCount else current.wrongCount + 1,
            feedbackMessageBn = feedbackMsg,
            history = current.history + answeredRecord
        )

        if (isCorrect) {
            recordDailyActivity(1)
            viewModelScope.launch {
                repository.resolveUnifiedMistake("MENTAL_MATH_${currentQ.id}")
            }
        } else {
            viewModelScope.launch {
                repository.recordUnifiedMistake(
                    mistakeKey = "MENTAL_MATH_${currentQ.id}",
                    sourceModule = "MENTAL_MATH",
                    subjectId = current.category.id,
                    curriculumTrack = "NCTB",
                    questionPromptBn = currentQ.expressionBn,
                    userAnswerBn = BengaliNumberFormatter.toBn(optionValue),
                    correctAnswerBn = currentQ.correctAnswerBn,
                    diagnosticExplanationBn = currentQ.explanationBn.ifBlank { currentQ.mentalStrategyTipBn }
                )
            }
        }
    }

    fun nextMentalMathQuestion() {
        val current = _mentalMathSession.value
        if (!current.isAnswerSubmitted || current.isFinished) return

        val nextIndex = current.currentIndex + 1

        // Check if finished in fixed question count modes
        if (current.configuredQuestionCount > 0 && nextIndex >= current.configuredQuestionCount) {
            finishMentalMathSession()
            return
        }

        // Dynamic difficulty progression on high streaks
        val dynamicDifficulty = when {
            current.streak >= 8 -> MentalMathDifficulty.HARD
            current.streak >= 4 && current.difficulty == MentalMathDifficulty.EASY -> MentalMathDifficulty.MEDIUM
            else -> current.difficulty
        }

        val currentQuestions = current.questions
        val updatedQuestions = if (nextIndex >= currentQuestions.size - 3) {
            currentQuestions + MentalMathGenerator.generateQuestions(current.category, dynamicDifficulty, 20)
        } else {
            currentQuestions
        }

        _mentalMathSession.value = current.copy(
            questions = updatedQuestions,
            currentIndex = nextIndex,
            selectedOptionIndex = null,
            typedInput = "",
            lastAnswerCorrect = null,
            feedbackMessageBn = null,
            isAnswerSubmitted = false
        )
    }

    fun tickMentalMathTimer() {
        val current = _mentalMathSession.value
        if (current.isFinished) return

        val now = clock.elapsedRealtime()
        val startMs = current.startElapsedRealtimeMs
        val actualElapsed = if (startMs > 0L) {
            ((now - startMs) / 1000L).toInt().coerceAtLeast(0)
        } else {
            current.elapsedSeconds + 1
        }

        if (current.mode.isTimed) {
            val deadlineMs = current.deadlineElapsedRealtimeMs
            val remainingSec = if (deadlineMs > 0L) {
                val remMs = deadlineMs - now
                if (remMs <= 0L) 0 else ((remMs + 999L) / 1000L).toInt()
            } else {
                (current.timeRemainingSeconds - 1).coerceAtLeast(0)
            }

            if (remainingSec <= 0) {
                finishMentalMathSession()
            } else {
                if (remainingSec != current.timeRemainingSeconds || actualElapsed != current.elapsedSeconds) {
                    _mentalMathSession.value = current.copy(
                        timeRemainingSeconds = remainingSec,
                        elapsedSeconds = actualElapsed
                    )
                }
            }
        } else {
            if (actualElapsed != current.elapsedSeconds) {
                _mentalMathSession.value = current.copy(elapsedSeconds = actualElapsed)
            }
        }
    }

    fun finishMentalMathSession() {
        val current = _mentalMathSession.value
        if (current.isFinished) return

        val totalAttempted = current.correctCount + current.wrongCount
        val percentage = if (totalAttempted > 0) (current.correctCount * 100) / totalAttempted else 0
        val finalElapsed = if (current.mode.isTimed) {
            maxOf(current.configuredDurationSeconds, current.elapsedSeconds)
        } else {
            current.elapsedSeconds.coerceAtLeast(1)
        }

        _mentalMathSession.value = current.copy(
            isFinished = true,
            isAnswerSubmitted = true,
            timeRemainingSeconds = 0,
            elapsedSeconds = finalElapsed
        )

        viewModelScope.launch {
            val topicId = "MENTAL_MATH_${current.category.id}_${current.difficulty.id}"
            val titleBn = "মেন্টাল ম্যাথ: ${current.category.titleBn} (${current.difficulty.titleBn})"
            repository.recordQuizScore(
                QuizScoreEntity(
                    topicId = topicId,
                    topicTitleBn = titleBn,
                    score = current.score,
                    totalQuestions = totalAttempted,
                    percentage = percentage
                )
            )
            repository.recordPractice("mental_math_${current.category.id}")
            repository.recordSessionAndAnalytics(
                moduleType = "MENTAL_MATH",
                categoryId = current.category.id,
                categoryTitleBn = current.category.titleBn,
                difficulty = current.difficulty.id,
                mode = current.mode.id,
                inputFormat = current.inputFormat.id,
                score = current.score,
                correctCount = current.correctCount,
                wrongCount = current.wrongCount,
                totalQuestions = totalAttempted,
                accuracyPercent = percentage,
                durationSeconds = finalElapsed.toLong(),
                highestStreak = current.highestStreak
            )
            recordDailyActivity(4)
        }
    }

    fun retryMentalMathChallenge() {
        val current = _mentalMathSession.value
        val durationOrCount = if (current.mode.isTimed) {
            current.configuredDurationSeconds
        } else {
            current.configuredQuestionCount
        }
        startMentalMathChallenge(
            category = current.category,
            difficulty = current.difficulty,
            mode = current.mode,
            durationOrCount = durationOrCount,
            inputFormat = current.inputFormat
        )
    }

    fun toggleMentalMathReviewMode() {
        val current = _mentalMathSession.value
        _mentalMathSession.value = current.copy(isReviewMode = !current.isReviewMode)
    }

    fun exitMentalMathToSetup() {
        val current = _screenStack.value
        val setupIndex = current.indexOfLast { it == Screen.MentalMath }
        if (setupIndex != -1) {
            _screenStack.value = current.take(setupIndex + 1)
            while (_navigationContextStack.size > setupIndex + 1) {
                _navigationContextStack.removeAt(_navigationContextStack.lastIndex)
            }
            _navigationContextStack.lastOrNull()?.let { restoreNavigationSnapshot(it) }
        } else {
            _screenStack.value = listOf(Screen.Home, Screen.MentalMath)
            _navigationContextStack.clear()
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.Home))
            _navigationContextStack.add(captureCurrentNavigationSnapshot(Screen.MentalMath))
        }
        _mentalMathSession.value = MentalMathSessionState()
    }

    // Step-by-Step Practice Tutor
    fun startTutor(number: Int = 5) {
        _tutorNumber.value = number
        _tutorStep.value = 1
        _tutorUserAnswer.value = ""
        _tutorFeedback.value = null
        _tutorCompleted.value = false
        navigateTo(Screen.PracticeTutor)
    }

    fun setTutorNumber(number: Int) {
        _tutorNumber.value = number
        _tutorStep.value = 1
        _tutorUserAnswer.value = ""
        _tutorFeedback.value = null
        _tutorCompleted.value = false
        syncTopNavigationSnapshot()
    }

    fun setTutorAnswerInput(input: String) {
        _tutorUserAnswer.value = BengaliNumberFormatter.toEnglishDigits(input).filter { it.isDigit() }
    }

    fun checkTutorStep() {
        if (_tutorCompleted.value) return
        val num = _tutorNumber.value
        val step = _tutorStep.value
        val expected = num * step
        val userAnsInt = BengaliNumberFormatter.toEnglishDigits(_tutorUserAnswer.value).toIntOrNull()

        if (userAnsInt == null) {
            _tutorFeedback.value = "অনুগ্রহ করে উত্তর লিখুন"
            return
        }

        if (userAnsInt == expected) {
            recordDailyActivity(1)
            if (step < 10) {
                _tutorFeedback.value = "সঠিক উত্তর! পরবর্তী ধাপে যাচ্ছি..."
                _tutorStep.value = step + 1
                _tutorUserAnswer.value = ""
            } else {
                _tutorCompleted.value = true
                _tutorFeedback.value = "অভিনন্দন! আপনি সফলভাবে ${num} এর নামতা সম্পন্ন করেছেন!"
                viewModelScope.launch {
                    repository.recordPractice("table_${num}")
                    recordDailyActivity(3)
                }
            }
        } else {
            _tutorFeedback.value = "ভুল হয়েছে! সঠিক উত্তর: $num × $step = $expected"
        }
    }

    // --- Streak & Daily Activity Helpers ---
    internal var currentDateKeyProvider: (() -> String)? = null

    private fun getTodayDateStr(): String {
        currentDateKeyProvider?.invoke()?.takeIf { it.isNotBlank() }?.let { return it }
        val cal = Calendar.getInstance()
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    private fun getYesterdayDateStr(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    private fun getDayOfWeekBn(calendar: Calendar): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "শনি"
            Calendar.SUNDAY -> "রবি"
            Calendar.MONDAY -> "সোম"
            Calendar.TUESDAY -> "মঙ্গল"
            Calendar.WEDNESDAY -> "বুধ"
            Calendar.THURSDAY -> "বৃহঃ"
            Calendar.FRIDAY -> "শুক্র"
            else -> ""
        }
    }

    private fun initStreakAndActivity() {
        val today = getTodayDateStr()
        val yesterday = getYesterdayDateStr()
        val lastDate = prefs.getString("streak_last_active_date", null)
        var currentStreak = prefs.getInt("streak_current_days", 0)
        var longestStreak = prefs.getInt("streak_longest_days", 0)

        if (lastDate == null) {
            currentStreak = 1
            longestStreak = 1
            prefs.edit()
                .putString("streak_last_active_date", today)
                .putInt("streak_current_days", 1)
                .putInt("streak_longest_days", 1)
                .putInt("act_$today", maxOf(prefs.getInt("act_$today", 0), 1))
                .apply()
        } else if (lastDate == yesterday) {
            if (currentStreak < 1) currentStreak = 1
        } else if (lastDate != today) {
            currentStreak = 1
            prefs.edit().putInt("streak_current_days", 1).apply()
        }

        if (longestStreak < currentStreak) {
            longestStreak = currentStreak
            prefs.edit().putInt("streak_longest_days", longestStreak).apply()
        }

        _streakDays.value = currentStreak
        _longestStreak.value = longestStreak
        refreshWeeklyStats()
    }

    fun recordDailyActivity(count: Int = 1) {
        val today = getTodayDateStr()
        val yesterday = getYesterdayDateStr()
        val currentTodayCount = prefs.getInt("act_$today", 0)
        prefs.edit().putInt("act_$today", currentTodayCount + count).apply()

        val lastDate = prefs.getString("streak_last_active_date", null)
        var currentStreak = prefs.getInt("streak_current_days", 1)
        var longestStreak = prefs.getInt("streak_longest_days", 1)

        if (lastDate == yesterday) {
            currentStreak += 1
        } else if (lastDate != null && lastDate != today) {
            currentStreak = 1
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak
        }

        prefs.edit()
            .putString("streak_last_active_date", today)
            .putInt("streak_current_days", currentStreak)
            .putInt("streak_longest_days", longestStreak)
            .apply()

        _streakDays.value = currentStreak
        _longestStreak.value = longestStreak
        refreshWeeklyStats()
    }

    fun refreshWeeklyStats() {
        val list = mutableListOf<WeeklyDayStat>()
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = sdf.format(cal.time)

        for (i in 6 downTo 0) {
            val dayCal = Calendar.getInstance()
            dayCal.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = sdf.format(dayCal.time)
            val dayName = getDayOfWeekBn(dayCal)
            val count = prefs.getInt("act_$dateStr", 0)
            list.add(
                WeeklyDayStat(
                    dayNameBn = dayName,
                    dateStr = dateStr,
                    count = count,
                    isToday = dateStr == todayStr,
                    isMet = count >= 2
                )
            )
        }
        _weeklyActivities.value = list
    }

    // --- Customization Management ---
    fun updatePreviewGlobal(updater: (GlobalCustomConfig) -> GlobalCustomConfig) {
        val current = _previewCustomizationState.value
        _previewCustomizationState.value = current.copy(global = updater(current.global))
    }

    fun updatePreviewSection(key: SectionKey, updater: (SectionCustomConfig) -> SectionCustomConfig) {
        val current = _previewCustomizationState.value
        val sectionCurrent = current.sections[key.id] ?: SectionCustomConfig()
        val updated = updater(sectionCurrent)
        val newMap = current.sections.toMutableMap()
        newMap[key.id] = updated
        _previewCustomizationState.value = current.copy(sections = newMap)
    }

    fun resetPreviewSection(key: SectionKey) {
        val current = _previewCustomizationState.value
        val newMap = current.sections.toMutableMap()
        newMap.remove(key.id)
        _previewCustomizationState.value = current.copy(sections = newMap)
    }

    fun resetPreviewCategory(category: String) {
        val current = _previewCustomizationState.value
        when (category) {
            "LAYOUT" -> {
                _previewCustomizationState.value = current.copy(
                    global = current.global.copy(
                        layoutType = com.example.ui.customization.CustomLayoutType.GRID,
                        gridColumns = 2,
                        horizontalGapDp = 14,
                        verticalGapDp = 14,
                        cornerRadiusDp = 20,
                        showBorder = false,
                        borderThicknessDp = 0,
                        contentAlignmentCenter = false
                    )
                )
            }
            "TYPOGRAPHY" -> {
                _previewCustomizationState.value = current.copy(
                    global = current.global.copy(
                        fontWeight = com.example.ui.customization.CustomFontWeight.MEDIUM,
                        titleFontSizeSp = 16,
                        subtitleFontSizeSp = 12,
                        interfaceScalePercent = 100
                    )
                )
            }
            "GLOBAL_THEME" -> {
                _previewCustomizationState.value = current.copy(
                    global = current.global.copy(
                        customAccentHex = null,
                        customPrimaryActionHex = null,
                        customLightBackgroundHex = null,
                        customLightCardSurfaceHex = null,
                        customLightTextColorHex = null,
                        customDarkBackgroundHex = null,
                        customDarkCardSurfaceHex = null,
                        customDarkTextColorHex = null
                    )
                )
            }
            "SECTIONS" -> {
                _previewCustomizationState.value = current.copy(sections = emptyMap())
            }
        }
    }

    fun saveCustomization() {
        val toSave = _previewCustomizationState.value
        customPrefs.saveCustomizationState(toSave)
        _customizationState.value = toSave
    }

    fun discardCustomizationPreview() {
        _previewCustomizationState.value = _customizationState.value
    }

    fun resetAllCustomizations() {
        customPrefs.clearAll()
        val clean = AppCustomizationState()
        _customizationState.value = clean
        _previewCustomizationState.value = clean
    }

    // ==========================================
    // MODULE 07: NUMBER SYSTEM & DIVISIBILITY PRACTICE
    // ==========================================
    private val _numberSystemPracticeState = MutableStateFlow(com.example.math.NumberSystemPracticeState())
    val numberSystemPracticeState: StateFlow<com.example.math.NumberSystemPracticeState> = _numberSystemPracticeState.asStateFlow()

    @JvmOverloads
    fun startNumberSystemPractice(
        topic: com.example.math.NumberSystemTopic = com.example.math.NumberSystemTopic.ALL,
        difficulty: com.example.math.NumberSystemDifficulty = com.example.math.NumberSystemDifficulty.EASY,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ
    ) {
        val questions = com.example.math.NumberSystemProvider.getPracticeQuestions(topic, difficulty, inputFormat)
        _numberSystemPracticeState.value = com.example.math.NumberSystemPracticeState(
            topic = topic,
            difficulty = difficulty,
            inputFormat = inputFormat,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            typedInput = "",
            isSubmitted = false,
            score = 0,
            isCompleted = false,
            userAnswers = emptyMap(),
            currentStreak = 0,
            highestStreak = 0,
            sessionStartEpochMs = System.currentTimeMillis()
        )
        navigateTo(Screen.NumberSystemPractice)
    }

    fun selectNumberSystemOption(index: Int) {
        val current = _numberSystemPracticeState.value
        if (!current.isSubmitted && !current.isCompleted) {
            _numberSystemPracticeState.value = current.copy(selectedOptionIndex = index)
        }
    }

    fun setNumberSystemTypedInput(input: String) {
        val current = _numberSystemPracticeState.value
        if (!current.isSubmitted && !current.isCompleted) {
            _numberSystemPracticeState.value = current.copy(typedInput = input)
        }
    }

    fun appendNumberSystemKeypadDigit(digit: String) {
        val current = _numberSystemPracticeState.value
        if (!current.isSubmitted && !current.isCompleted && current.typedInput.length < 15) {
            _numberSystemPracticeState.value = current.copy(typedInput = current.typedInput + digit)
        }
    }

    fun backspaceNumberSystemKeypad() {
        val current = _numberSystemPracticeState.value
        if (!current.isSubmitted && !current.isCompleted && current.typedInput.isNotEmpty()) {
            _numberSystemPracticeState.value = current.copy(typedInput = current.typedInput.dropLast(1))
        }
    }

    fun clearNumberSystemKeypad() {
        val current = _numberSystemPracticeState.value
        if (!current.isSubmitted && !current.isCompleted) {
            _numberSystemPracticeState.value = current.copy(typedInput = "")
        }
    }

    fun submitNumberSystemAnswer() {
        val current = _numberSystemPracticeState.value
        if (current.isSubmitted || current.isCompleted) return
        val currentQ = current.questions.getOrNull(current.currentIndex) ?: return

        val isCorrect: Boolean
        val recordedChoice: Int

        if (current.inputFormat == QuestionInputFormat.KEYPAD && current.selectedOptionIndex == null) {
            val typed = current.typedInput.trim()
            if (typed.isEmpty()) return
            isCorrect = SharedMathEngine.isAnswerCorrect(typed, currentQ.correctAnswerBn) ||
                SharedMathEngine.isAnswerCorrect(
                    typed,
                    currentQ.optionsBn.getOrElse(currentQ.correctIndex) { "" }.removeSuffix("টি").trim()
                )
            recordedChoice = if (isCorrect) currentQ.correctIndex else -2
        } else {
            val selected = current.selectedOptionIndex ?: return
            isCorrect = selected == currentQ.correctIndex
            recordedChoice = selected
        }

        val newScore = if (isCorrect) current.score + 1 else current.score
        val nextStreak = if (isCorrect) current.currentStreak + 1 else 0
        val nextHighestStreak = maxOf(current.highestStreak, nextStreak)
        val updatedAnswers = current.userAnswers + (current.currentIndex to recordedChoice)

        _numberSystemPracticeState.value = current.copy(
            isSubmitted = true,
            selectedOptionIndex = recordedChoice,
            score = newScore,
            userAnswers = updatedAnswers,
            currentStreak = nextStreak,
            highestStreak = nextHighestStreak
        )

        if (isCorrect) {
            recordDailyActivity(1)
            viewModelScope.launch {
                repository.resolveUnifiedMistake("NUMBER_SYSTEM_${currentQ.id}")
            }
        } else {
            val userAnsText = if (current.inputFormat == QuestionInputFormat.KEYPAD && current.typedInput.isNotBlank()) {
                current.typedInput.trim()
            } else {
                currentQ.optionsBn.getOrElse(recordedChoice) { "ভুল উত্তর" }
            }
            val expectedAnsText = currentQ.optionsBn.getOrElse(currentQ.correctIndex) { currentQ.correctAnswerBn }
            viewModelScope.launch {
                repository.recordUnifiedMistake(
                    mistakeKey = "NUMBER_SYSTEM_${currentQ.id}",
                    sourceModule = "NUMBER_SYSTEM",
                    subjectId = currentQ.topic.id,
                    curriculumTrack = if (currentQ.curriculumTracks.contains(com.example.math.CurriculumTrack.BCS)) "BCS" else "NCTB",
                    questionPromptBn = currentQ.questionBn,
                    userAnswerBn = userAnsText,
                    correctAnswerBn = expectedAnsText,
                    diagnosticExplanationBn = currentQ.explanationBn
                )
            }
        }
    }

    fun nextNumberSystemQuestion() {
        val current = _numberSystemPracticeState.value
        if (current.currentIndex + 1 < current.questions.size) {
            _numberSystemPracticeState.value = current.copy(
                currentIndex = current.currentIndex + 1,
                selectedOptionIndex = null,
                typedInput = "",
                isSubmitted = false
            )
        } else {
            val total = current.questions.size
            val percentage = if (total > 0) (current.score * 100) / total else 0
            val topicTitle = "বাস্তব সংখ্যা ও বিভাজ্যতা (${current.difficulty.titleBn})"
            val elapsedSec = ((System.currentTimeMillis() - current.sessionStartEpochMs) / 1000L).coerceAtLeast(1L)
            val bestStreak = current.highestStreak.coerceAtLeast(if (current.score > 0) 1 else 0)

            viewModelScope.launch {
                repository.recordQuizScore(
                    QuizScoreEntity(
                        topicId = "NUMBER_SYSTEM",
                        topicTitleBn = topicTitle,
                        score = current.score,
                        totalQuestions = total,
                        percentage = percentage
                    )
                )
                repository.recordSessionAndAnalytics(
                    moduleType = "NUMBER_SYSTEM",
                    categoryId = current.topic.id,
                    categoryTitleBn = topicTitle,
                    difficulty = current.difficulty.id,
                    mode = "practice",
                    inputFormat = current.inputFormat.id,
                    score = current.score * 10,
                    correctCount = current.score,
                    wrongCount = (total - current.score).coerceAtLeast(0),
                    totalQuestions = total,
                    accuracyPercent = percentage,
                    durationSeconds = elapsedSec,
                    highestStreak = bestStreak
                )
                recordDailyActivity(3)
            }

            _numberSystemPracticeState.value = current.copy(
                isCompleted = true
            )
        }
    }

    fun restartNumberSystemPractice() {
        val current = _numberSystemPracticeState.value
        startNumberSystemPractice(current.topic, current.difficulty, current.inputFormat)
    }

    // ==========================================
    // PRIORITY 2 & 3: DASHBOARD, SOLVER HISTORY & BCS PREPARATION ACTIONS
    // ==========================================
    fun setDashboardTimeFilter(filter: AnalyticsTimeFilter) {
        _dashboardTimeFilter.value = filter
    }

    fun updateDailyStudyTargets(targetQuestions: Int, targetMinutes: Int) {
        val clampedMinutes = targetMinutes.coerceIn(5, 240)
        prefs.edit().putInt("study_timer_daily_goal_min", clampedMinutes).apply()
        _studySessionTimer.value = _studySessionTimer.value.copy(dailyGoalMinutes = clampedMinutes)
        viewModelScope.launch {
            repository.updateDailyStudyTargets(targetQuestions, clampedMinutes)
        }
    }

    fun executeLearningRecommendation(rec: LearningRecommendation) {
        when (rec.actionType) {
            RecommendationActionType.RESUME_ACTIVE_EXAM -> resumeActiveExamAttempt()
            RecommendationActionType.OPEN_QUIZ_TOPIC -> {
                if (rec.targetTopicId.startsWith("BOOK_", ignoreCase = true)) {
                    openBookHub()
                    return
                }
                if (rec.targetTopicId.startsWith("VISUAL_", ignoreCase = true)) {
                    navigateTo(Screen.VisualExplorer)
                    return
                }
                if (rec.targetTopicId.startsWith("BCS", ignoreCase = true)) {
                    navigateTo(Screen.BcsPreparation)
                    return
                }
                val directQuizTopic = QuizTopic.values().firstOrNull { it.id.equals(rec.targetTopicId, ignoreCase = true) }
                if (directQuizTopic != null) {
                    startQuiz(topic = directQuizTopic, count = 10, mode = QuizMode.TOPIC)
                    return
                }
                val practiceCategory = PracticeCategory.values().firstOrNull {
                    it.id.equals(rec.targetTopicId, ignoreCase = true) ||
                        rec.targetTopicId.removePrefix("PRACTICE_").startsWith(it.id, ignoreCase = true)
                }
                if (practiceCategory != null) {
                    startPracticeSession(
                        category = practiceCategory,
                        difficulty = PracticeDifficulty.MEDIUM,
                        count = 10
                    )
                    return
                }
                val numberSystemTopic = com.example.math.NumberSystemTopic.values().firstOrNull {
                    it.id.equals(rec.targetTopicId, ignoreCase = true)
                }
                if (numberSystemTopic != null ||
                    rec.targetTopicId.equals("NUMBER_SYSTEM", ignoreCase = true) ||
                    rec.targetTopicId.contains("num", ignoreCase = true) ||
                    rec.targetTopicId.contains("div", ignoreCase = true)
                ) {
                    startNumberSystemPractice(
                        topic = numberSystemTopic ?: com.example.math.NumberSystemTopic.ALL,
                        difficulty = com.example.math.NumberSystemDifficulty.MEDIUM
                    )
                    return
                }
                val mappedTopic = when {
                    rec.targetTopicId.contains("mul", ignoreCase = true) || rec.targetTopicId.contains("table", ignoreCase = true) -> QuizTopic.MULTIPLICATION
                    rec.targetTopicId.contains("prime", ignoreCase = true) -> QuizTopic.PRIMES
                    rec.targetTopicId.contains("square", ignoreCase = true) || rec.targetTopicId.contains("cube", ignoreCase = true) -> QuizTopic.SQUARES_CUBES
                    rec.targetTopicId.contains("alg", ignoreCase = true) -> QuizTopic.ALGEBRA
                    rec.targetTopicId.contains("geom", ignoreCase = true) -> QuizTopic.GEOMETRY_MENSURATION
                    rec.targetTopicId.contains("arith", ignoreCase = true) || rec.targetTopicId.contains("percent", ignoreCase = true) || rec.targetTopicId.contains("ratio", ignoreCase = true) -> QuizTopic.ARITHMETIC_PERCENTAGE
                    else -> QuizTopic.MIXED_CHALLENGE
                }
                startQuiz(topic = mappedTopic, count = 10, mode = QuizMode.TOPIC)
            }
            RecommendationActionType.OPEN_EXAM_PRESET -> {
                val preset = ExamPresetType.fromIdOrDefault(rec.targetTopicId)
                startConfiguredExam(ExamConfig(preset = preset))
            }
            RecommendationActionType.OPEN_PRACTICE_LAB -> navigateTo(Screen.PracticeLab)
            RecommendationActionType.OPEN_MENTAL_MATH -> navigateTo(Screen.MentalMath)
            RecommendationActionType.OPEN_FORMULA_LIBRARY -> navigateTo(Screen.FormulaLibrary)
            RecommendationActionType.OPEN_PROBLEM_SOLVER -> navigateTo(Screen.ProblemSolver)
            RecommendationActionType.OPEN_BCS_PREPARATION -> navigateTo(Screen.BcsPreparation)
        }
    }

    fun saveSolverResultToHistory(
        topic: SolverTopic,
        inputs: Map<String, String>,
        result: SolverResult
    ) {
        if (!result.isSuccess) return
        viewModelScope.launch {
            val summary = inputs.entries.joinToString(", ") { "${it.key} = ${it.value}" }.ifBlank { result.originalProblemBn }
            val entity = SolverHistoryEntity(
                topicId = topic.name,
                topicTitleBn = topic.titleBn,
                categoryTitleBn = topic.titleEn,
                inputSummaryBn = summary,
                inputsJson = SimilarProblemGenerator.serializeInputs(inputs),
                finalAnswerBn = result.finalAnswerBn,
                stepsJson = SimilarProblemGenerator.serializeSteps(result.steps),
                formulaUsedBn = result.formulaUsedBn ?: "",
                verificationBn = result.verificationBn ?: "",
                timestamp = System.currentTimeMillis()
            )
            repository.saveSolvedProblemHistory(entity)
            repository.recordDailyStudyDelta(
                questionsAttemptedDelta = 1,
                correctDelta = 1,
                wrongDelta = 0,
                studyTimeSecondsDelta = 30,
                examsCompletedDelta = 0,
                practiceSessionsCompletedDelta = 1
            )
            recordDailyActivity(1)
        }
    }

    fun deleteSolverHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteSolvedProblemHistory(id)
        }
    }

    fun clearAllSolverHistory() {
        viewModelScope.launch {
            repository.clearSolvedProblemHistory()
        }
    }

    private val _selectedBcsSyllabusVersion = MutableStateFlow(BcsSyllabusVersion.BCS_50TH_CURRENT)
    val selectedBcsSyllabusVersion: StateFlow<BcsSyllabusVersion> = _selectedBcsSyllabusVersion.asStateFlow()

    private val _selectedBcsExamProfile = MutableStateFlow(com.example.math.BcsExamProfile.BCS_50TH_PRELIM)
    val selectedBcsExamProfile: StateFlow<com.example.math.BcsExamProfile> = _selectedBcsExamProfile.asStateFlow()

    fun setBcsSyllabusVersion(version: BcsSyllabusVersion) {
        _selectedBcsSyllabusVersion.value = version
        if (version == BcsSyllabusVersion.BCS_LEGACY_15) {
            _selectedBcsExamProfile.value = com.example.math.BcsExamProfile.BCS_LEGACY_PRELIM
        } else if (_selectedBcsExamProfile.value == com.example.math.BcsExamProfile.BCS_LEGACY_PRELIM) {
            _selectedBcsExamProfile.value = com.example.math.BcsExamProfile.BCS_50TH_PRELIM
        }
    }

    fun setBcsExamProfile(profile: com.example.math.BcsExamProfile) {
        _selectedBcsExamProfile.value = profile
        if (profile == com.example.math.BcsExamProfile.BCS_LEGACY_PRELIM) {
            _selectedBcsSyllabusVersion.value = BcsSyllabusVersion.BCS_LEGACY_15
        } else {
            _selectedBcsSyllabusVersion.value = BcsSyllabusVersion.BCS_50TH_CURRENT
        }
        viewModelScope.launch {
            val savedScoped = repository.getBcsUserProfile("profile_${profile.id}")
            val currentProfile = repository.getBcsUserProfile("active_profile")
            val nextActive = if (savedScoped != null) {
                savedScoped.copy(
                    profileId = "active_profile",
                    selectedExamProfileId = profile.id,
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                (currentProfile ?: com.example.data.model.BcsUserProfileEntity()).copy(
                    profileId = "active_profile",
                    selectedExamProfileId = profile.id,
                    updatedAt = System.currentTimeMillis()
                )
            }
            repository.saveBcsUserProfile(nextActive)
        }
    }

    fun saveBcsOnboarding(
        profile: com.example.math.BcsExamProfile,
        stage: com.example.math.BcsPreparationStage,
        dailyMinutes: Int,
        dailyQuestions: Int,
        targetCompletionEpochMs: Long? = null,
        officialExamDateEpochMs: Long? = null,
        preparationLevel: String = "BEGINNER",
        weeklyTargetTopics: Int = 3,
        weeklyTargetMocks: Int = 1
    ) {
        _selectedBcsExamProfile.value = profile
        if (profile == com.example.math.BcsExamProfile.BCS_LEGACY_PRELIM) {
            _selectedBcsSyllabusVersion.value = BcsSyllabusVersion.BCS_LEGACY_15
        } else {
            _selectedBcsSyllabusVersion.value = BcsSyllabusVersion.BCS_50TH_CURRENT
        }
        viewModelScope.launch {
            val existing = repository.getBcsUserProfile()
            repository.saveBcsUserProfile(
                com.example.data.model.BcsUserProfileEntity(
                    profileId = "active_profile",
                    selectedExamProfileId = profile.id,
                    onboardingCompleted = true,
                    currentStageIndex = stage.stageNumber,
                    dailyTargetMinutes = dailyMinutes,
                    dailyTargetQuestions = dailyQuestions,
                    targetCompletionEpochMs = targetCompletionEpochMs ?: existing?.targetCompletionEpochMs,
                    officialExamDateEpochMs = officialExamDateEpochMs ?: existing?.officialExamDateEpochMs,
                    updatedAt = System.currentTimeMillis(),
                    preparationLevel = preparationLevel,
                    weeklyTargetTopics = weeklyTargetTopics,
                    weeklyTargetMocks = weeklyTargetMocks,
                    targetAccuracyPercent = existing?.targetAccuracyPercent ?: 75
                )
            )
            recordDailyActivity(2)
        }
    }

    fun completeBcsTopicGuide(topicId: String, subjectTrack: String) {
        val activeProfileId = _selectedBcsExamProfile.value.id
        viewModelScope.launch {
            repository.recordBcsTopicProgress(
                topicId = topicId,
                subjectTrack = subjectTrack,
                isGuideCompleted = true,
                miniCheckPassed = false,
                additionalTimeSeconds = 45,
                examProfileId = activeProfileId
            )
            repository.recordBcsStudyGoalDelta(
                examProfileId = activeProfileId,
                lessonsDelta = 1,
                studyTimeMinutesDelta = 5
            )
            recordDailyActivity(1)
        }
    }

    fun recordBcsMiniCheckResult(topicId: String, subjectTrack: String, passed: Boolean) {
        val activeProfileId = _selectedBcsExamProfile.value.id
        viewModelScope.launch {
            repository.recordBcsTopicProgress(
                topicId = topicId,
                subjectTrack = subjectTrack,
                isGuideCompleted = true,
                miniCheckPassed = passed,
                additionalTimeSeconds = 30,
                questionsAttemptedDelta = 1,
                questionsCorrectDelta = if (passed) 1 else 0,
                examProfileId = activeProfileId
            )
            repository.recordBcsStudyGoalDelta(
                examProfileId = activeProfileId,
                questionsDelta = 1,
                studyTimeMinutesDelta = 2
            )
            if (passed) {
                recordDailyActivity(2)
            }
        }
    }

    fun recordBcsMistake(
        questionId: String,
        subjectTrack: String,
        topicId: String,
        promptBn: String,
        wrongAnsBn: String,
        correctAnsBn: String,
        explanationBn: String,
        shortcutTipBn: String = "",
        provenanceBadgeBn: String = ""
    ) {
        val activeProfileId = _selectedBcsExamProfile.value.id
        viewModelScope.launch {
            repository.recordBcsMistake(
                questionId = questionId,
                subjectTrack = subjectTrack,
                topicId = topicId,
                questionPromptBn = promptBn,
                wrongAnswerBn = wrongAnsBn,
                correctAnswerBn = correctAnsBn,
                explanationBn = explanationBn,
                shortcutTipBn = shortcutTipBn,
                provenanceBadgeBn = provenanceBadgeBn,
                examProfileId = activeProfileId
            )
            repository.recordBcsTopicProgress(
                topicId = topicId,
                subjectTrack = subjectTrack,
                isGuideCompleted = false,
                miniCheckPassed = false,
                additionalTimeSeconds = 25,
                questionsAttemptedDelta = 1,
                questionsCorrectDelta = 0,
                examProfileId = activeProfileId
            )
            repository.recordBcsStudyGoalDelta(
                examProfileId = activeProfileId,
                questionsDelta = 1,
                studyTimeMinutesDelta = 1
            )
        }
    }

    fun markBcsMistakeResolved(questionId: String) {
        val activeProfileId = _selectedBcsExamProfile.value.id
        viewModelScope.launch {
            repository.markBcsMistakeResolved(questionId, examProfileId = activeProfileId)
            repository.recordBcsStudyGoalDelta(
                examProfileId = activeProfileId,
                revisionsDelta = 1,
                questionsDelta = 1,
                studyTimeMinutesDelta = 2
            )
            recordDailyActivity(1)
        }
    }

    fun deleteBcsMistake(questionId: String) {
        val activeProfileId = _selectedBcsExamProfile.value.id
        viewModelScope.launch {
            repository.deleteBcsMistake(questionId, examProfileId = activeProfileId)
        }
    }

    fun startBcsModeExam(
        mode: com.example.math.BcsPracticeMode,
        unit: BcsSyllabusUnit? = null,
        difficulty: PracticeDifficulty = PracticeDifficulty.MEDIUM,
        customNegativeMarking: Double? = null
    ) {
        val activeProfile = _selectedBcsExamProfile.value
        val unresolvedIds = bcsMistakes.value
            .filter { !it.isResolved && it.belongsToProfile(activeProfile.id) }
            .map { it.rawQuestionId }
            .toSet()
        val weakUnits = BcsPreparationRepository.analyzeBcsWeakUnits(
            topicStats = topicAnalytics.value,
            syllabusVersion = _selectedBcsSyllabusVersion.value
        ).filter { it.isWeak }.map { it.unit }.toSet()

        val questions = BcsPreparationRepository.generateBcsModeExamQuestions(
            mode = mode,
            examProfile = activeProfile,
            unit = unit,
            difficulty = difficulty,
            unresolvedMistakeIds = unresolvedIds,
            weakUnits = weakUnits
        )
        val isUntimedPractice = (mode == com.example.math.BcsPracticeMode.TOPIC_PRACTICE ||
            mode == com.example.math.BcsPracticeMode.MISTAKE_PRACTICE)
        val durationMinutes = if (isUntimedPractice) 0 else mode.durationMinutes.coerceAtLeast(5)
        val durationSec = durationMinutes * 60
        val negPenalty = customNegativeMarking ?: mode.negativeMarking
        val now = clock.elapsedRealtime()
        val deadline = if (durationSec > 0) now + durationSec * 1000L else 0L
        val attemptId = "bcs_mode_${mode.id.lowercase()}_${System.currentTimeMillis()}"
        val titleBn = "${mode.titleBn} (${activeProfile.shortBadgeBn})"

        val config = ExamConfig(
            preset = ExamPresetType.BCS_PRELIMINARY_MOCK,
            selectedTopics = setOf(unit?.mappedQuizTopic ?: QuizTopic.MIXED_CHALLENGE),
            difficulty = difficulty,
            questionType = QuizQuestionType.MCQ,
            questionCount = questions.size,
            durationMinutes = durationMinutes,
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = negPenalty,
            immediateFeedback = isUntimedPractice
        )

        val newState = QuizSessionState(
            attemptId = attemptId,
            examTitleBn = titleBn,
            examConfig = config,
            isFullExamMode = !config.immediateFeedback,
            topic = unit?.mappedQuizTopic ?: QuizTopic.MIXED_CHALLENGE,
            mode = if (config.immediateFeedback) QuizMode.PRACTICE else QuizMode.TIMED,
            questionType = QuizQuestionType.MCQ,
            difficulty = difficulty,
            state = if (questions.isNotEmpty()) QuizState.IN_PROGRESS else QuizState.ERROR,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            typedAnswer = "",
            isAnswerSubmitted = false,
            correctCount = 0,
            isFinished = false,
            userAnswers = emptyMap(),
            userTypedAnswers = emptyMap(),
            markedForReviewIndices = emptySet(),
            visitedIndices = setOf(0),
            questionTimeSeconds = emptyMap(),
            isReviewMode = false,
            timeRemainingSeconds = durationSec,
            configuredDurationSeconds = durationSec,
            elapsedSeconds = 0,
            startElapsedRealtimeMs = now,
            deadlineElapsedRealtimeMs = deadline,
            examProfileId = activeProfile.id
        )
        _quizSession.value = newState
        persistActiveExamStateAsync(newState)
        viewModelScope.launch {
            repository.recordBcsStudyGoalDelta(
                examProfileId = activeProfile.id,
                mockExamsDelta = 1,
                questionsDelta = questions.size,
                studyTimeMinutesDelta = (mode.durationMinutes / 2).coerceAtLeast(5)
            )
        }
        navigateTo(Screen.QuizPlay)
    }

    fun startBcsMockExam(
        unit: BcsSyllabusUnit? = null,
        syllabusVersion: BcsSyllabusVersion = _selectedBcsSyllabusVersion.value,
        questionCount: Int = syllabusVersion.totalMarks,
        durationMinutes: Int = syllabusVersion.totalMarks,
        negativeMarking: Double = 0.0
    ) {
        val questions = BcsPreparationRepository.generateBcsExamQuestions(
            count = questionCount,
            difficulty = PracticeDifficulty.MEDIUM,
            unit = unit,
            includePreviousBcs = true,
            syllabusVersion = syllabusVersion
        )
        val durationSec = (durationMinutes * 60).coerceAtLeast(60)
        val now = clock.elapsedRealtime()
        val deadline = now + durationSec * 1000L
        val attemptId = "bcs_mock_${System.currentTimeMillis()}"
        val titleBn = when {
            unit != null -> "বিসিএস অনুশীলন (${syllabusVersion.shortBadgeBn}): ${unit.titleBn}"
            syllabusVersion == BcsSyllabusVersion.BCS_50TH_CURRENT -> "৫০তম বিসিএস প্রিলিমিনারি গাণিতিক যুক্তি মক টেস্ট (২০ নম্বর)"
            else -> "প্রাক্তন বিসিএস গাণিতিক যুক্তি মক টেস্ট (১৫ নম্বর • আর্কাইভড)"
        }
        val config = ExamConfig(
            preset = ExamPresetType.BCS_PRELIMINARY_MOCK,
            selectedTopics = setOf(unit?.mappedQuizTopic ?: QuizTopic.MIXED_CHALLENGE),
            difficulty = PracticeDifficulty.MEDIUM,
            questionType = QuizQuestionType.MCQ,
            questionCount = questions.size,
            durationMinutes = durationMinutes,
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = negativeMarking,
            immediateFeedback = false
        )

        val newState = QuizSessionState(
            attemptId = attemptId,
            examTitleBn = titleBn,
            examConfig = config,
            isFullExamMode = true,
            topic = unit?.mappedQuizTopic ?: QuizTopic.MIXED_CHALLENGE,
            mode = QuizMode.TIMED,
            questionType = QuizQuestionType.MCQ,
            difficulty = PracticeDifficulty.MEDIUM,
            state = if (questions.isNotEmpty()) QuizState.IN_PROGRESS else QuizState.ERROR,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            typedAnswer = "",
            isAnswerSubmitted = false,
            correctCount = 0,
            isFinished = false,
            userAnswers = emptyMap(),
            userTypedAnswers = emptyMap(),
            markedForReviewIndices = emptySet(),
            visitedIndices = setOf(0),
            questionTimeSeconds = emptyMap(),
            isReviewMode = false,
            timeRemainingSeconds = durationSec,
            configuredDurationSeconds = durationSec,
            elapsedSeconds = 0,
            startElapsedRealtimeMs = now,
            deadlineElapsedRealtimeMs = deadline,
            examProfileId = _selectedBcsExamProfile.value.id
        )
        _quizSession.value = newState
        persistActiveExamStateAsync(newState)
        viewModelScope.launch {
            repository.recordBcsStudyGoalDelta(
                examProfileId = _selectedBcsExamProfile.value.id,
                mockExamsDelta = 1,
                questionsDelta = questions.size,
                studyTimeMinutesDelta = (durationMinutes / 2).coerceAtLeast(5)
            )
        }
        navigateTo(Screen.QuizPlay)
    }

    // ==========================================
    // MODULE: BOOK (CLASS 6, 7, 8, 9-10 OFFLINE TEXTBOOK LEARNING SYSTEM)
    // ==========================================
    private val _selectedBookId = MutableStateFlow(BookId.CLASS_6)
    val selectedBookId: StateFlow<BookId> = _selectedBookId.asStateFlow()

    private val _selectedBookChapterNumber = MutableStateFlow(1)
    val selectedBookChapterNumber: StateFlow<Int> = _selectedBookChapterNumber.asStateFlow()

    private val _selectedBookLessonId = MutableStateFlow("c6_1_l1")
    val selectedBookLessonId: StateFlow<String> = _selectedBookLessonId.asStateFlow()

    private val _selectedBookPrintedPage = MutableStateFlow(1)
    val selectedBookPrintedPage: StateFlow<Int> = _selectedBookPrintedPage.asStateFlow()

    private val _selectedBookLabType = MutableStateFlow(BookInteractiveLabType.NUMBER_LINE_LAB)
    val selectedBookLabType: StateFlow<BookInteractiveLabType> = _selectedBookLabType.asStateFlow()

    private val _bookPracticeSession = MutableStateFlow(BookPracticeSessionState())
    val bookPracticeSession: StateFlow<BookPracticeSessionState> = _bookPracticeSession.asStateFlow()

    private val _bookSearchQuery = MutableStateFlow("")
    val bookSearchQuery: StateFlow<String> = _bookSearchQuery.asStateFlow()

    private val _bookSearchFilter = MutableStateFlow(BookSearchFilterType.ALL)
    val bookSearchFilter: StateFlow<BookSearchFilterType> = _bookSearchFilter.asStateFlow()

    fun setBookSearchQuery(query: String) {
        _bookSearchQuery.value = query
    }

    fun setBookSearchFilter(filter: BookSearchFilterType) {
        _bookSearchFilter.value = filter
    }

    fun openBookHub() {
        val current = _screenStack.value
        val existingIdx = current.indexOf(Screen.BookHub)
        if (existingIdx != -1) {
            _screenStack.value = current.take(existingIdx + 1)
            while (_navigationContextStack.size > existingIdx + 1) {
                _navigationContextStack.removeAt(_navigationContextStack.lastIndex)
            }
        } else {
            navigateTo(Screen.BookHub)
        }
    }

    fun selectBook(bookId: BookId) {
        _selectedBookId.value = bookId
        syncTopNavigationSnapshot()
    }

    fun openBookDetail(bookId: BookId) {
        _selectedBookId.value = bookId
        navigateTo(Screen.BookDetail)
    }

    fun openBookChapter(bookId: BookId, chapterNumber: Int) {
        _selectedBookId.value = bookId
        _selectedBookChapterNumber.value = chapterNumber
        val chapter = BookCurriculumRepository.getChapter(bookId, chapterNumber)
        chapter?.lessons?.firstOrNull()?.let { _selectedBookLessonId.value = it.id }
        navigateTo(Screen.BookChapterOverview)
    }

    fun openBookLesson(bookId: BookId, chapterNumber: Int, lessonId: String) {
        _selectedBookId.value = bookId
        _selectedBookChapterNumber.value = chapterNumber
        _selectedBookLessonId.value = lessonId
        navigateTo(Screen.BookLesson)
        recordDailyActivity(1)
    }

    @JvmOverloads
    fun openBookPdfReader(
        bookId: BookId = _selectedBookId.value,
        printedPage: Int? = null,
        chapterNumber: Int? = null
    ) {
        _selectedBookId.value = bookId
        val savedReadingState = bookReadingStates.value.firstOrNull { it.bookId == bookId.id }
        val targetPrintedPage = when {
            printedPage != null -> printedPage.coerceIn(1, bookId.maxPrintedPage)
            chapterNumber != null -> {
                val ch = BookCurriculumRepository.getChapter(bookId, chapterNumber)
                ch?.printedPageStart ?: 1
            }
            savedReadingState != null -> savedReadingState.lastReadPrintedPage.coerceIn(1, bookId.maxPrintedPage)
            else -> 1
        }
        val inferredChapter = chapterNumber
            ?: BookCurriculumRepository.getChapterForPrintedPage(bookId, targetPrintedPage).chapterNumber
        _selectedBookPrintedPage.value = targetPrintedPage
        _selectedBookChapterNumber.value = inferredChapter
        updateBookReaderPage(bookId, targetPrintedPage, inferredChapter)
        navigateTo(Screen.BookPdfReader)
    }

    @JvmOverloads
    fun openBookInteractiveLab(
        labType: BookInteractiveLabType,
        bookId: BookId = _selectedBookId.value,
        chapterNumber: Int = _selectedBookChapterNumber.value
    ) {
        _selectedBookId.value = bookId
        _selectedBookChapterNumber.value = chapterNumber
        _selectedBookLabType.value = labType
        navigateTo(Screen.BookInteractiveLab)
    }

    @JvmOverloads
    fun openBookRevision(bookId: BookId = _selectedBookId.value) {
        _selectedBookId.value = bookId
        navigateTo(Screen.BookRevision)
    }

    fun updateBookReaderPage(
        bookId: BookId,
        printedPage: Int,
        chapterNumber: Int? = null,
        zoomScale: Float = 1.0f,
        preferredViewMode: String? = null
    ) {
        val clampedPrinted = printedPage.coerceIn(1, bookId.maxPrintedPage)
        val pdfPage = bookId.printedToPdfPage(clampedPrinted)
        val chNum = chapterNumber
            ?: BookCurriculumRepository.getChapterForPrintedPage(bookId, clampedPrinted).chapterNumber
        _selectedBookPrintedPage.value = clampedPrinted
        _selectedBookChapterNumber.value = chNum
        syncTopNavigationSnapshot()
        viewModelScope.launch {
            repository.updateBookReadingState(
                bookId = bookId.id,
                printedPage = clampedPrinted,
                pdfPage = pdfPage,
                chapterNumber = chNum,
                incrementPagesRead = true,
                preferredViewMode = preferredViewMode,
                lastZoomScale = zoomScale,
                activeEditionId = bookId.editionMetadata.editionId
            )
        }
    }

    fun updateBookReaderPreferences(
        bookId: BookId,
        preferredViewMode: String? = null,
        zoomScale: Float? = null
    ) {
        viewModelScope.launch {
            repository.updateBookReadingState(
                bookId = bookId.id,
                preferredViewMode = preferredViewMode,
                lastZoomScale = zoomScale,
                activeEditionId = bookId.editionMetadata.editionId
            )
        }
    }

    fun toggleBookReaderPageBookmark(bookId: BookId, printedPage: Int) {
        viewModelScope.launch {
            val chapter = BookCurriculumRepository.getChapterForPrintedPage(bookId, printedPage)
            repository.toggleBookPageBookmark(
                bookId = bookId.id,
                printedPage = printedPage,
                bookTitleBn = bookId.titleBn,
                chapterTitleBn = "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber)}: ${chapter.titleBn}"
            )
        }
    }

    fun isBookLessonCompleted(lessonId: String, bookId: BookId = _selectedBookId.value): Boolean {
        return bookLessonProgress.value.firstOrNull { it.bookId == bookId.id && it.lessonId == lessonId }?.isCompleted == true
    }

    fun toggleBookLessonCompleted(
        bookId: BookId,
        chapterNumber: Int,
        lessonId: String
    ) {
        viewModelScope.launch {
            val existing = bookLessonProgress.value.firstOrNull { it.bookId == bookId.id && it.lessonId == lessonId }
            val nextCompleted = !(existing?.isCompleted ?: false)
            repository.toggleBookLessonCompleted(
                bookId = bookId.id,
                chapterNumber = chapterNumber,
                lessonId = lessonId
            )
            if (nextCompleted) {
                recordDailyActivity(2)
            }
        }
    }

    fun recordLessonMiniCheckResult(
        bookId: BookId,
        chapterNumber: Int,
        lessonId: String,
        passed: Boolean
    ) {
        viewModelScope.launch {
            val existing = bookLessonProgress.value.firstOrNull { it.bookId == bookId.id && it.lessonId == lessonId }
            repository.upsertBookLessonProgress(
                bookId = bookId.id,
                chapterNumber = chapterNumber,
                lessonId = lessonId,
                isCompleted = existing?.isCompleted == true || passed,
                miniCheckPassed = passed,
                timeSpentDeltaSeconds = 45
            )
            if (!passed) {
                val lesson = BookCurriculumRepository.getLesson(bookId, chapterNumber, lessonId)
                if (lesson != null) {
                    repository.scheduleSpacedRevisionItem(
                        itemId = "rev_lesson_$lessonId",
                        bookId = bookId.id,
                        chapterNumber = chapterNumber,
                        itemType = "WEAK_LESSON",
                        titleBn = "পাঠ ${lesson.code}: ${lesson.titleBn}",
                        subtitleBn = lesson.miniCheckExplanationBn
                    )
                }
            } else {
                recordDailyActivity(1)
            }
        }
    }

    fun saveBookPersonalNote(
        id: Int = 0,
        bookId: BookId = _selectedBookId.value,
        chapterNumber: Int = _selectedBookChapterNumber.value,
        lessonId: String? = null,
        noteTitleBn: String,
        noteBodyBn: String,
        tagBn: String = "সাধারণ নোট"
    ) {
        if (noteTitleBn.isBlank() && noteBodyBn.isBlank()) return
        viewModelScope.launch {
            val chapter = BookCurriculumRepository.getChapter(bookId, chapterNumber)
            repository.saveBookNote(
                id = id,
                bookId = bookId.id,
                chapterNumber = chapterNumber,
                lessonId = lessonId ?: "${chapterNumber}.1",
                printedPage = chapter?.printedPageStart ?: 1,
                titleBn = noteTitleBn.ifBlank { "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapterNumber)} নোট ($tagBn)" },
                contentBn = noteBodyBn.trim()
            )
            recordDailyActivity(1)
        }
    }

    fun deleteBookPersonalNote(id: Int) {
        viewModelScope.launch {
            repository.deleteBookNote(id)
        }
    }

    fun markBookMistakeResolved(mistakeId: String, resolved: Boolean = true) {
        viewModelScope.launch {
            repository.markBookMistakeResolved(mistakeId, resolved)
        }
    }

    fun deleteBookMistake(mistakeId: String) {
        viewModelScope.launch {
            repository.deleteBookMistake(mistakeId)
        }
    }

    fun scheduleBookSpacedRevision(
        itemId: String,
        bookId: BookId,
        chapterNumber: Int,
        itemType: String,
        titleBn: String,
        summaryBn: String
    ) {
        viewModelScope.launch {
            repository.scheduleSpacedRevisionItem(
                itemId = itemId,
                bookId = bookId.id,
                chapterNumber = chapterNumber,
                itemType = itemType,
                titleBn = titleBn,
                subtitleBn = summaryBn
            )
        }
    }

    fun completeSpacedRevisionReview(itemId: String, rememberedWell: Boolean) {
        viewModelScope.launch {
            repository.advanceSpacedRevisionItem(itemId, rememberedWell)
            recordDailyActivity(1)
        }
    }

    // --- Multi-Format Book Practice Engine Operations ---
    @JvmOverloads
    fun startBookPractice(
        bookId: BookId = _selectedBookId.value,
        chapterNumber: Int? = _selectedBookChapterNumber.value,
        mode: BookPracticeMode = BookPracticeMode.STANDARD,
        questionCount: Int = 10
    ) {
        _selectedBookId.value = bookId
        if (chapterNumber != null) {
            _selectedBookChapterNumber.value = chapterNumber
        }
        val weakQIds = bookMistakes.value
            .filter { it.bookId == bookId.id && !it.isResolved }
            .map { it.questionId.removePrefix("mistake_") }
            .toSet()
        val questions = BookQuestionEngine.buildSessionQuestions(
            bookId = bookId,
            chapterNumber = chapterNumber,
            mode = mode,
            maxCount = questionCount,
            weakQuestionIds = weakQIds
        )
        _bookPracticeSession.value = BookPracticeSessionState(
            bookId = bookId,
            chapterNumber = chapterNumber,
            mode = mode,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndices = emptySet(),
            userTextInput = "",
            isAnswerSubmitted = false,
            isCurrentCorrect = null,
            showHint = false,
            correctCount = 0,
            wrongCount = 0,
            isCompleted = false,
            elapsedSeconds = 0,
            answeredResults = emptyMap(),
            selectedOptionsByIndex = emptyMap(),
            userTextInputsByIndex = emptyMap(),
            sessionStartEpochMs = System.currentTimeMillis()
        )
        navigateTo(Screen.BookPractice)
    }

    fun toggleBookPracticeOption(index: Int) {
        val current = _bookPracticeSession.value
        if (current.isAnswerSubmitted || current.isCompleted) return
        val currentQ = current.currentQuestion ?: return
        val nextSet = if (currentQ.format == BookQuestionFormat.MULTIPLE_SELECT) {
            if (current.selectedOptionIndices.contains(index)) {
                current.selectedOptionIndices - index
            } else {
                current.selectedOptionIndices + index
            }
        } else {
            setOf(index)
        }
        _bookPracticeSession.value = current.copy(
            selectedOptionIndices = nextSet,
            selectedOptionsByIndex = current.selectedOptionsByIndex + (current.currentIndex to nextSet)
        )
    }

    fun setBookPracticeTextInput(text: String) {
        val current = _bookPracticeSession.value
        if (!current.isAnswerSubmitted && !current.isCompleted) {
            _bookPracticeSession.value = current.copy(
                userTextInput = text,
                userTextInputsByIndex = current.userTextInputsByIndex + (current.currentIndex to text)
            )
        }
    }

    fun toggleBookPracticeHint() {
        val current = _bookPracticeSession.value
        _bookPracticeSession.value = current.copy(showHint = !current.showHint)
    }

    fun submitBookPracticeAnswer() {
        val current = _bookPracticeSession.value
        if (current.isAnswerSubmitted || current.isCompleted || current.answeredResults.containsKey(current.currentIndex)) return
        if (current.selectedOptionIndices.isEmpty() && current.userTextInput.trim().isEmpty()) return
        val q = current.currentQuestion ?: return

        val (isCorrect, userSummary) = BookQuestionEngine.evaluateQuestion(
            question = q,
            selectedOptionIndices = current.selectedOptionIndices,
            userTextInput = current.userTextInput
        )

        val updatedResults = current.answeredResults + (current.currentIndex to isCorrect)
        val updatedSelections = current.selectedOptionsByIndex + (current.currentIndex to current.selectedOptionIndices)
        val updatedTextInputs = current.userTextInputsByIndex + (current.currentIndex to current.userTextInput)
        val newCorrect = updatedResults.values.count { it }
        val newWrong = updatedResults.values.count { !it }

        _bookPracticeSession.value = current.copy(
            isAnswerSubmitted = true,
            isCurrentCorrect = isCorrect,
            correctCount = newCorrect,
            wrongCount = newWrong,
            answeredResults = updatedResults,
            selectedOptionsByIndex = updatedSelections,
            userTextInputsByIndex = updatedTextInputs
        )

        viewModelScope.launch {
            val chapter = BookCurriculumRepository.getChapter(q.bookId, q.chapterNumber)
            val targetLessonId = q.lessonId ?: chapter?.lessons?.firstOrNull()?.id
            if (targetLessonId != null) {
                val existing = bookLessonProgress.value.firstOrNull { it.bookId == q.bookId.id && it.lessonId == targetLessonId }
                repository.upsertBookLessonProgress(
                    bookId = q.bookId.id,
                    chapterNumber = q.chapterNumber,
                    lessonId = targetLessonId,
                    isCompleted = existing?.isCompleted == true || isCorrect,
                    miniCheckPassed = isCorrect,
                    timeSpentDeltaSeconds = 30
                )
            }

            if (!isCorrect) {
                // Automatically record in Mistake Notebook, Spaced Revision (1-3-7-14 day), and Unified Mistake Notebook
                repository.recordBookQuestionMistake(
                    questionId = "mistake_${q.id}",
                    bookId = q.bookId.id,
                    chapterNumber = q.chapterNumber,
                    questionPromptBn = q.promptBn,
                    wrongAnswerBn = userSummary,
                    correctAnswerBn = q.correctTextAnswer,
                    explanationBn = q.explanationBn,
                    sourceProvenance = q.provenance.name,
                    chapterTitleBn = chapter?.titleBn ?: "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(q.chapterNumber)}"
                )
            } else {
                recordDailyActivity(1)
                repository.markBookMistakeResolved("mistake_${q.id}", resolved = true)
                repository.resolveUnifiedMistake("BOOK_${q.id}")
            }
        }
    }

    fun nextBookPracticeQuestion() {
        val current = _bookPracticeSession.value
        if (!current.isAnswerSubmitted) return
        val nextIdx = current.currentIndex + 1
        if (nextIdx < current.questions.size) {
            val alreadyAnswered = current.answeredResults.containsKey(nextIdx)
            _bookPracticeSession.value = current.copy(
                currentIndex = nextIdx,
                selectedOptionIndices = current.selectedOptionsByIndex[nextIdx] ?: emptySet(),
                userTextInput = current.userTextInputsByIndex[nextIdx] ?: "",
                isAnswerSubmitted = alreadyAnswered,
                isCurrentCorrect = current.answeredResults[nextIdx],
                showHint = false
            )
        } else {
            finishBookPracticeSession()
        }
    }

    fun previousBookPracticeQuestion() {
        val current = _bookPracticeSession.value
        if (current.currentIndex <= 0 || current.isCompleted) return
        val prevIdx = current.currentIndex - 1
        _bookPracticeSession.value = current.copy(
            currentIndex = prevIdx,
            selectedOptionIndices = current.selectedOptionsByIndex[prevIdx] ?: emptySet(),
            userTextInput = current.userTextInputsByIndex[prevIdx] ?: "",
            isAnswerSubmitted = current.answeredResults.containsKey(prevIdx),
            isCurrentCorrect = current.answeredResults[prevIdx],
            showHint = false
        )
    }

    fun finishBookPracticeSession() {
        val current = _bookPracticeSession.value
        if (current.isCompleted) return
        val total = current.questions.size.coerceAtLeast(1)
        val pct = (current.correctCount * 100) / total
        val elapsedSec = if (current.elapsedSeconds > 0) {
            current.elapsedSeconds.toLong()
        } else {
            ((System.currentTimeMillis() - current.sessionStartEpochMs) / 1000L).coerceAtLeast(1L)
        }

        _bookPracticeSession.value = current.copy(
            isCompleted = true
        )

        viewModelScope.launch {
            val chLabel = current.chapterNumber?.let { "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(it)}" } ?: "মিশ্র অনুশীলন"
            val titleBn = "${current.bookId.titleBn} ($chLabel • ${current.mode.titleBn})"
            repository.recordQuizScore(
                QuizScoreEntity(
                    topicId = "BOOK_${current.bookId.id}_C${current.chapterNumber ?: 0}",
                    topicTitleBn = titleBn,
                    score = current.correctCount,
                    totalQuestions = total,
                    percentage = pct
                )
            )
            repository.recordSessionAndAnalytics(
                moduleType = "BOOK",
                categoryId = "BOOK_${current.bookId.id}_C${current.chapterNumber ?: 0}",
                categoryTitleBn = titleBn,
                difficulty = current.mode.id,
                mode = current.mode.id,
                inputFormat = "MULTI_FORMAT",
                score = current.correctCount * 10,
                correctCount = current.correctCount,
                wrongCount = current.wrongCount,
                totalQuestions = total,
                accuracyPercent = pct,
                durationSeconds = elapsedSec,
                highestStreak = current.correctCount
            )
            recordDailyActivity(3)
        }
    }

    // =========================================================================
    // PERSISTENT 'STUDY SESSION' TIMER 2.0 COMPONENT (BUG-78)
    // =========================================================================

    private var studyTimerJob: kotlinx.coroutines.Job? = null
    private var lastTickElapsedRealtimeMs: Long = 0L

    val isStudyTimerJobActive: Boolean
        get() = studyTimerJob?.isActive == true

    var activeStudyTimerJobCount: Int = 0
        private set

    private fun startStudyTimerJobIfNeeded() {
        if (studyTimerJob?.isActive == true) return
        studyTimerJob?.cancel()
        lastTickElapsedRealtimeMs = clock.elapsedRealtime()
        activeStudyTimerJobCount = 1
        studyTimerJob = viewModelScope.launch {
            try {
                while (_studySessionTimer.value.isRunning) {
                    kotlinx.coroutines.delay(1000L)
                    if (!_studySessionTimer.value.isRunning) break
                    val nowRealtime = clock.elapsedRealtime()
                    val elapsedDeltaSec = if (
                        lastTickElapsedRealtimeMs > 0L &&
                        nowRealtime >= lastTickElapsedRealtimeMs + 1000L
                    ) {
                        ((nowRealtime - lastTickElapsedRealtimeMs) / 1000L).toInt().coerceIn(1, 14400)
                    } else {
                        1
                    }
                    tickStudySessionTimer(elapsedDeltaSec)
                }
            } finally {
                activeStudyTimerJobCount = 0
            }
        }
    }

    private fun stopStudyTimerJob() {
        studyTimerJob?.cancel()
        studyTimerJob = null
        activeStudyTimerJobCount = 0
        lastTickElapsedRealtimeMs = 0L
    }

    /**
     * Cold-process initialization for Study Timer 2.0 (BUG-78):
     * - Always initializes in the compact OFF state (isVisibleOnHome = false, isRunning = false).
     * - Ignores and sanitizes any stale persisted enabled/running flags from previous sessions.
     * - Never counts closed-app downtime.
     * - Safely flushes any persisted unrecorded elapsed study seconds from an interrupted session at most once.
     */
    private fun initStudySessionTimer() {
        stopStudyTimerJob()
        val today = getTodayDateStr()
        val persistedDateKey = prefs.getString("study_timer_active_date_key", null)?.takeIf { it.isNotBlank() } ?: today
        val savedTodaySec = prefs.getInt("study_timer_today_sec_$today", 0).coerceAtLeast(0)
        val savedAllTimeSec = prefs.getInt("study_timer_all_time_sec", 0).coerceAtLeast(0)
        val savedGoalMin = prefs.getInt("study_timer_daily_goal_min", 30).coerceIn(5, 240)
        val savedSessionsCount = prefs.getInt("study_timer_sessions_$today", 0).coerceAtLeast(0)
        val persistedUnrecordedSec = prefs.getInt("study_timer_unrecorded_db_sec", 0).coerceIn(0, 3600)

        // Reset process-scoped activation flags in SharedPreferences so old ON/running flags never persist
        prefs.edit()
            .putBoolean("study_timer_visible_on_home", false)
            .putBoolean("study_timer_is_running", false)
            .putInt("study_timer_current_session_sec", 0)
            .putInt("study_timer_unrecorded_db_sec", 0)
            .putLong("study_timer_last_wall_ms", 0L)
            .putString("study_timer_active_date_key", today)
            .apply()

        _studySessionTimer.value = StudySessionTimerState(
            isVisibleOnHome = false,
            isRunning = false,
            currentSessionSeconds = 0,
            todayTotalSeconds = savedTodaySec,
            allTimeTotalSeconds = savedAllTimeSec,
            dailyGoalMinutes = savedGoalMin,
            completedSessionsToday = savedSessionsCount,
            unrecordedDbSeconds = 0,
            lastSessionCompleted = false,
            activeDateKey = today
        )

        if (persistedUnrecordedSec > 0) {
            viewModelScope.launch {
                repository.recordDailyStudyDelta(
                    questionsAttemptedDelta = 0,
                    correctDelta = 0,
                    wrongDelta = 0,
                    studyTimeSecondsDelta = persistedUnrecordedSec,
                    examsCompletedDelta = 0,
                    practiceSessionsCompletedDelta = 0,
                    dateKeyOverride = persistedDateKey
                )
            }
        }
    }

    internal fun ensureStudyTimerDateCurrent(today: String = getTodayDateStr()): StudySessionTimerState {
        val current = _studySessionTimer.value
        if (current.activeDateKey == today) return current
        if (current.activeDateKey.isEmpty()) {
            prefs.edit().putString("study_timer_active_date_key", today).apply()
            val stamped = current.copy(activeDateKey = today)
            _studySessionTimer.value = stamped
            return stamped
        }
        val previousDateKey = current.activeDateKey
        val pendingPrevDaySec = current.unrecordedDbSeconds
        val todayBaselineSec = prefs.getInt("study_timer_today_sec_$today", 0).coerceAtLeast(0)
        val todayBaselineSessions = prefs.getInt("study_timer_sessions_$today", 0).coerceAtLeast(0)

        prefs.edit()
            .putInt("study_timer_unrecorded_db_sec", 0)
            .putString("study_timer_active_date_key", today)
            .apply()

        val rolledOver = current.copy(
            todayTotalSeconds = todayBaselineSec,
            completedSessionsToday = todayBaselineSessions,
            unrecordedDbSeconds = 0,
            activeDateKey = today
        )
        _studySessionTimer.value = rolledOver

        if (pendingPrevDaySec > 0) {
            viewModelScope.launch {
                repository.recordDailyStudyDelta(
                    questionsAttemptedDelta = 0,
                    correctDelta = 0,
                    wrongDelta = 0,
                    studyTimeSecondsDelta = pendingPrevDaySec,
                    examsCompletedDelta = 0,
                    practiceSessionsCompletedDelta = 0,
                    dateKeyOverride = previousDateKey
                )
            }
        }
        return rolledOver
    }

    internal fun setStudyTimerDateKeyForTesting(dateKey: String) {
        prefs.edit().putString("study_timer_active_date_key", dateKey).apply()
        _studySessionTimer.value = _studySessionTimer.value.copy(activeDateKey = dateKey)
    }

    fun toggleStudyTimerVisibility() {
        setStudyTimerVisibility(!_studySessionTimer.value.isVisibleOnHome)
    }

    fun setStudyTimerVisibility(visible: Boolean) {
        val today = getTodayDateStr()
        val current = ensureStudyTimerDateCurrent(today)
        if (visible) {
            if (current.isVisibleOnHome) return
            prefs.edit()
                .putBoolean("study_timer_visible_on_home", true)
                .putString("study_timer_active_date_key", today)
                .apply()
            _studySessionTimer.value = current.copy(
                isVisibleOnHome = true,
                isRunning = false,
                lastSessionCompleted = false,
                activeDateKey = today
            )
        } else {
            if (!current.isVisibleOnHome && !current.isRunning && current.unrecordedDbSeconds == 0 && current.currentSessionSeconds == 0) {
                return
            }
            stopStudyTimerJob()
            val unrecorded = current.unrecordedDbSeconds
            val hadActiveSession = current.isRunning || current.currentSessionSeconds > 0
            val nextCompleted = if (hadActiveSession && current.currentSessionSeconds > 0) {
                current.completedSessionsToday + 1
            } else {
                current.completedSessionsToday
            }

            prefs.edit()
                .putBoolean("study_timer_visible_on_home", false)
                .putBoolean("study_timer_is_running", false)
                .putInt("study_timer_current_session_sec", 0)
                .putInt("study_timer_unrecorded_db_sec", 0)
                .putInt("study_timer_sessions_$today", nextCompleted)
                .putLong("study_timer_last_wall_ms", 0L)
                .putString("study_timer_active_date_key", today)
                .apply()

            _studySessionTimer.value = current.copy(
                isVisibleOnHome = false,
                isRunning = false,
                currentSessionSeconds = 0,
                completedSessionsToday = nextCompleted,
                unrecordedDbSeconds = 0,
                lastSessionCompleted = false,
                activeDateKey = today
            )

            if (unrecorded > 0 || (hadActiveSession && current.currentSessionSeconds > 0)) {
                if (hadActiveSession && current.currentSessionSeconds > 0) {
                    recordDailyActivity(1)
                }
                viewModelScope.launch {
                    repository.recordDailyStudyDelta(
                        questionsAttemptedDelta = 0,
                        correctDelta = 0,
                        wrongDelta = 0,
                        studyTimeSecondsDelta = unrecorded,
                        examsCompletedDelta = 0,
                        practiceSessionsCompletedDelta = if (hadActiveSession && current.currentSessionSeconds > 0) 1 else 0,
                        dateKeyOverride = today
                    )
                }
            }
        }
    }

    fun startStudySession() {
        val today = getTodayDateStr()
        val current = ensureStudyTimerDateCurrent(today)
        if (current.isRunning) {
            startStudyTimerJobIfNeeded()
            return
        }
        val nowWallMs = System.currentTimeMillis()
        prefs.edit()
            .putBoolean("study_timer_visible_on_home", true)
            .putBoolean("study_timer_is_running", true)
            .putLong("study_timer_last_wall_ms", nowWallMs)
            .putString("study_timer_active_date_key", today)
            .apply()

        _studySessionTimer.value = current.copy(
            isVisibleOnHome = true,
            isRunning = true,
            lastSessionCompleted = false,
            activeDateKey = today
        )
        startStudyTimerJobIfNeeded()
    }

    fun pauseStudySession() {
        val today = getTodayDateStr()
        val current = ensureStudyTimerDateCurrent(today)
        if (!current.isRunning && current.unrecordedDbSeconds == 0) {
            stopStudyTimerJob()
            return
        }
        stopStudyTimerJob()
        val unrecorded = current.unrecordedDbSeconds
        prefs.edit()
            .putBoolean("study_timer_is_running", false)
            .putInt("study_timer_unrecorded_db_sec", 0)
            .putLong("study_timer_last_wall_ms", 0L)
            .putString("study_timer_active_date_key", today)
            .apply()

        _studySessionTimer.value = current.copy(
            isRunning = false,
            unrecordedDbSeconds = 0,
            activeDateKey = today
        )

        if (unrecorded > 0) {
            viewModelScope.launch {
                repository.recordDailyStudyDelta(
                    questionsAttemptedDelta = 0,
                    correctDelta = 0,
                    wrongDelta = 0,
                    studyTimeSecondsDelta = unrecorded,
                    examsCompletedDelta = 0,
                    practiceSessionsCompletedDelta = 0,
                    dateKeyOverride = today
                )
            }
        }
    }

    fun toggleStudySessionRunning() {
        if (_studySessionTimer.value.isRunning) {
            pauseStudySession()
        } else {
            startStudySession()
        }
    }

    fun tickStudySessionTimer(deltaSeconds: Int = 1) {
        if (deltaSeconds <= 0) return
        val today = getTodayDateStr()
        val current = ensureStudyTimerDateCurrent(today)
        if (!current.isRunning) return

        lastTickElapsedRealtimeMs = clock.elapsedRealtime()
        val nextSessionSec = current.currentSessionSeconds + deltaSeconds
        val nextTodaySec = current.todayTotalSeconds + deltaSeconds
        val nextAllTimeSec = current.allTimeTotalSeconds + deltaSeconds
        val nextUnrecorded = current.unrecordedDbSeconds + deltaSeconds

        // Flush to Room DB every 30 seconds of accumulated study time (BUG-74)
        val shouldFlushToDb = nextUnrecorded >= 30
        val remainingUnrecorded = if (shouldFlushToDb) 0 else nextUnrecorded

        prefs.edit()
            .putInt("study_timer_current_session_sec", nextSessionSec)
            .putInt("study_timer_today_sec_$today", nextTodaySec)
            .putInt("study_timer_all_time_sec", nextAllTimeSec)
            .putInt("study_timer_unrecorded_db_sec", remainingUnrecorded)
            .putLong("study_timer_last_wall_ms", System.currentTimeMillis())
            .putString("study_timer_active_date_key", today)
            .apply()

        _studySessionTimer.value = current.copy(
            currentSessionSeconds = nextSessionSec,
            todayTotalSeconds = nextTodaySec,
            allTimeTotalSeconds = nextAllTimeSec,
            unrecordedDbSeconds = remainingUnrecorded,
            activeDateKey = today
        )

        if (shouldFlushToDb) {
            viewModelScope.launch {
                repository.recordDailyStudyDelta(
                    questionsAttemptedDelta = 0,
                    correctDelta = 0,
                    wrongDelta = 0,
                    studyTimeSecondsDelta = nextUnrecorded,
                    examsCompletedDelta = 0,
                    practiceSessionsCompletedDelta = 0,
                    dateKeyOverride = today
                )
            }
        }
    }

    fun finishAndSaveStudySession() {
        val today = getTodayDateStr()
        val current = ensureStudyTimerDateCurrent(today)
        if (current.currentSessionSeconds <= 0 && !current.isRunning) return

        stopStudyTimerJob()
        val nextCompleted = current.completedSessionsToday + 1
        val unrecorded = current.unrecordedDbSeconds

        prefs.edit()
            .putBoolean("study_timer_is_running", false)
            .putInt("study_timer_current_session_sec", 0)
            .putInt("study_timer_unrecorded_db_sec", 0)
            .putInt("study_timer_sessions_$today", nextCompleted)
            .putLong("study_timer_last_wall_ms", 0L)
            .putString("study_timer_active_date_key", today)
            .apply()

        _studySessionTimer.value = current.copy(
            isRunning = false,
            currentSessionSeconds = 0,
            completedSessionsToday = nextCompleted,
            unrecordedDbSeconds = 0,
            lastSessionCompleted = true,
            activeDateKey = today
        )

        recordDailyActivity(1)
        viewModelScope.launch {
            repository.recordDailyStudyDelta(
                questionsAttemptedDelta = 0,
                correctDelta = 0,
                wrongDelta = 0,
                studyTimeSecondsDelta = unrecorded,
                examsCompletedDelta = 0,
                practiceSessionsCompletedDelta = 1,
                dateKeyOverride = today
            )
        }
    }

    fun resetCurrentStudySession() {
        val today = getTodayDateStr()
        val current = ensureStudyTimerDateCurrent(today)
        if (current.currentSessionSeconds <= 0 && !current.isRunning && current.unrecordedDbSeconds <= 0) {
            stopStudyTimerJob()
            return
        }
        stopStudyTimerJob()
        val unrecorded = current.unrecordedDbSeconds
        prefs.edit()
            .putBoolean("study_timer_is_running", false)
            .putInt("study_timer_current_session_sec", 0)
            .putInt("study_timer_unrecorded_db_sec", 0)
            .putLong("study_timer_last_wall_ms", 0L)
            .putString("study_timer_active_date_key", today)
            .apply()

        _studySessionTimer.value = current.copy(
            isRunning = false,
            currentSessionSeconds = 0,
            unrecordedDbSeconds = 0,
            lastSessionCompleted = false,
            activeDateKey = today
        )

        if (unrecorded > 0) {
            viewModelScope.launch {
                repository.recordDailyStudyDelta(
                    questionsAttemptedDelta = 0,
                    correctDelta = 0,
                    wrongDelta = 0,
                    studyTimeSecondsDelta = unrecorded,
                    examsCompletedDelta = 0,
                    practiceSessionsCompletedDelta = 0,
                    dateKeyOverride = today
                )
            }
        }
    }

    fun setStudyDailyGoalMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(5, 240)
        val current = _studySessionTimer.value
        prefs.edit().putInt("study_timer_daily_goal_min", clamped).apply()
        _studySessionTimer.value = current.copy(dailyGoalMinutes = clamped)
        viewModelScope.launch {
            repository.updateDailyStudyTargetMinutesOnly(clamped)
        }
    }

    fun flushUnrecordedStudyTime() {
        val current = _studySessionTimer.value
        val unrecorded = current.unrecordedDbSeconds
        if (unrecorded <= 0) return
        val targetDateKey = current.activeDateKey.takeIf { it.isNotBlank() } ?: getTodayDateStr()
        prefs.edit().putInt("study_timer_unrecorded_db_sec", 0).apply()
        _studySessionTimer.value = current.copy(unrecordedDbSeconds = 0)
        viewModelScope.launch {
            repository.recordDailyStudyDelta(
                questionsAttemptedDelta = 0,
                correctDelta = 0,
                wrongDelta = 0,
                studyTimeSecondsDelta = unrecorded,
                examsCompletedDelta = 0,
                practiceSessionsCompletedDelta = 0,
                dateKeyOverride = targetDateKey
            )
        }
    }

    fun onAppBackgrounded() {
        if (!_studySessionTimer.value.isRunning) return
        lastTickElapsedRealtimeMs = clock.elapsedRealtime()
        flushUnrecordedStudyTime()
    }

    fun onAppForegrounded() {
        val current = ensureStudyTimerDateCurrent()
        if (!current.isRunning || !current.isVisibleOnHome) return
        val nowRealtime = clock.elapsedRealtime()
        if (lastTickElapsedRealtimeMs > 0L && nowRealtime >= lastTickElapsedRealtimeMs + 1000L) {
            val deltaSec = ((nowRealtime - lastTickElapsedRealtimeMs) / 1000L).toInt().coerceIn(1, 14400)
            tickStudySessionTimer(deltaSec)
        }
        startStudyTimerJobIfNeeded()
    }

    override fun onCleared() {
        stopStudyTimerJob()
        super.onCleared()
    }

    private val _completedVisualCheckpointIds = mutableSetOf<String>()
    val completedVisualCheckpointIds: Set<String>
        get() = synchronized(_completedVisualCheckpointIds) { _completedVisualCheckpointIds.toSet() }

    fun recordVisualMathCheckpoint(
        topicId: String,
        topicTitleBn: String,
        isCorrect: Boolean
    ): Boolean {
        val cleanId = topicId.trim()
        if (cleanId.isEmpty()) return false
        if (isCorrect) {
            val isNewCompletion = synchronized(_completedVisualCheckpointIds) {
                _completedVisualCheckpointIds.add(cleanId)
            }
            if (!isNewCompletion) {
                // Prevent duplicate checkpoint inflation when the same stage/question is re-submitted
                return false
            }
        }
        recordDailyActivity(1)
        viewModelScope.launch {
            repository.recordSessionAndAnalytics(
                moduleType = "VISUAL_MATH",
                categoryId = cleanId,
                categoryTitleBn = topicTitleBn,
                difficulty = "MEDIUM",
                mode = "interactive_lab",
                inputFormat = "MCQ",
                score = if (isCorrect) 10 else 0,
                correctCount = if (isCorrect) 1 else 0,
                wrongCount = if (isCorrect) 0 else 1,
                totalQuestions = 1,
                accuracyPercent = if (isCorrect) 100 else 0,
                durationSeconds = 30L,
                highestStreak = if (isCorrect) 1 else 0
            )
        }
        return true
    }
}
