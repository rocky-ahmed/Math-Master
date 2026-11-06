package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MathDatabase
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
}

data class WeeklyDayStat(
    val dayNameBn: String,
    val dateStr: String,
    val count: Int,
    val isToday: Boolean,
    val isMet: Boolean
)

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
    val showSubmitConfirmDialog: Boolean = false
) {
    val incorrectQuestions: List<QuizQuestion>
        get() = questions.filterIndexed { index, q ->
            val ans = userAnswers[index]
            ans == null || ans != q.correctIndex
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
    }

    // App Navigation Stack (custom backstack for seamless BackHandler support)
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val screenStack: StateFlow<List<Screen>> = _screenStack.asStateFlow()

    val currentScreen: Screen
        get() = _screenStack.value.lastOrNull() ?: Screen.Home

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
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            return true
        } else if (current.firstOrNull() != Screen.Home) {
            _screenStack.value = listOf(Screen.Home)
            return true
        }
        return false
    }

    fun navigateToHome() {
        _screenStack.value = listOf(Screen.Home)
    }

    fun navigateToTab(screen: Screen) {
        if (screen == Screen.Home) {
            _screenStack.value = listOf(Screen.Home)
        } else {
            _screenStack.value = listOf(Screen.Home, screen)
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

    fun openFormula(formulaId: String) {
        _selectedFormulaId.value = formulaId
        navigateTo(Screen.FormulaDetail)
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
        val deadline = if (mode == QuizMode.TIMED) now + timedSeconds * 1000L else 0L
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
            durationMinutes = (timedSeconds / 60).coerceAtLeast(1),
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
            timeRemainingSeconds = if (mode == QuizMode.TIMED) timedSeconds else 0,
            configuredDurationSeconds = timedSeconds,
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
        val mappedMode = when (config.preset) {
            ExamPresetType.PRACTICE_EXAM -> QuizMode.PRACTICE
            ExamPresetType.REVIEW_INCORRECT -> QuizMode.REVIEW_INCORRECT
            ExamPresetType.MIXED_TEST, ExamPresetType.FULL_LENGTH_MOCK, ExamPresetType.BCS_PRELIMINARY_MOCK -> QuizMode.TIMED
            else -> if (config.durationSeconds > 0) QuizMode.TIMED else QuizMode.TOPIC
        }
        val durationSec = config.durationSeconds
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
            _quizSession.value = current.copy(selectedOptionIndex = index)
        }
    }

    fun setQuizTypedAnswer(text: String) {
        val current = _quizSession.value
        if (!current.isAnswerSubmitted && !current.isFinished) {
            _quizSession.value = current.copy(typedAnswer = text)
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
            correctCount = recomputedCorrect
        )
        _quizSession.value = nextState
        persistActiveExamStateAsync(nextState)
    }

    fun jumpToQuizQuestion(targetIndex: Int) {
        val current = _quizSession.value
        if (current.isFinished || targetIndex !in current.questions.indices) return
        val existingChoice = current.userAnswers[targetIndex]?.takeIf { it >= 0 }
        val existingTyped = current.userTypedAnswers[targetIndex] ?: ""
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
            recordedOption = if (matchIdx != -1) matchIdx else if (isCorrect) currentQ.correctIndex else -1
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
            selectedOptionIndex = if (recordedOption >= 0) recordedOption else null,
            correctCount = recomputedCorrect,
            userAnswers = updatedAnswers,
            userTypedAnswers = updatedTypedMap,
            visitedIndices = current.visitedIndices + current.currentIndex
        )
        _quizSession.value = nextState
        persistActiveExamStateAsync(nextState)

        if (isCorrect) {
            recordDailyActivity(1)
        }
    }

    fun previousQuizQuestion() {
        val current = _quizSession.value
        if (current.isFinished || current.currentIndex <= 0) return
        val prevIdx = current.currentIndex - 1
        val prevSelected = current.userAnswers[prevIdx]?.takeIf { it >= 0 }
        val prevTyped = current.userTypedAnswers[prevIdx] ?: ""
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
            val existingChoice = current.userAnswers[nextIndex]?.takeIf { it >= 0 }
            val existingTyped = current.userTypedAnswers[nextIndex] ?: ""
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

        if (current.mode != QuizMode.TIMED && current.configuredDurationSeconds <= 0) {
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
            _quizSession.value = current.copy(
                timeRemainingSeconds = rem,
                elapsedSeconds = elapsedSec,
                questionTimeSeconds = updatedQTimes
            )
        }
    }

    private fun finishQuizSession() {
        val current = _quizSession.value
        if (current.isFinished) return

        // If user selected an option on the last question without pressing submit yet, include it
        val finalAnswers = if (current.selectedOptionIndex != null && !current.userAnswers.containsKey(current.currentIndex)) {
            current.userAnswers + (current.currentIndex to current.selectedOptionIndex)
        } else {
            current.userAnswers
        }

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
            ans == null || ans != q.correctIndex
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
                answersJson = ExamQuestionBank.serializeIntMap(finalAnswers),
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
                completedAt = detailed.completedAt
            )
            repository.finalizeExamAttempt(
                completedAttempt = completedEntity,
                perTopicStats = ExamScoringEngine.toTopicExamStats(detailed.topicBreakdown)
            )
            recordDailyActivity(4)
        }
    }

    private fun persistActiveExamStateAsync(state: QuizSessionState) {
        if (state.isFinished || state.questions.isEmpty() || state.attemptId.isBlank()) return
        viewModelScope.launch {
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
                answersJson = ExamQuestionBank.serializeIntMap(state.userAnswers),
                markedForReviewCsv = ExamQuestionBank.serializeIntSet(state.markedForReviewIndices),
                visitedIndicesCsv = ExamQuestionBank.serializeIntSet(state.visitedIndices),
                questionTimeSecondsJson = ExamQuestionBank.serializeIntMap(state.questionTimeSeconds),
                correctCount = state.correctCount,
                wrongCount = (state.userAnswers.size - state.correctCount).coerceAtLeast(0),
                unansweredCount = (state.questions.size - state.userAnswers.size).coerceAtLeast(0),
                earnedMarks = state.correctCount * state.examConfig.marksPerCorrect,
                negativeMarksDeducted = 0.0,
                finalScore = state.correctCount * state.examConfig.marksPerCorrect,
                percentage = if (state.questions.isNotEmpty()) (state.correctCount * 100.0) / state.questions.size else 0.0,
                accuracyPercent = if (state.userAnswers.isNotEmpty()) (state.correctCount * 100.0) / state.userAnswers.size else 0.0,
                startedAt = System.currentTimeMillis() - (state.elapsedSeconds * 1000L),
                updatedAt = System.currentTimeMillis(),
                completedAt = null
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
            val marked = ExamQuestionBank.deserializeIntSet(entity.markedForReviewCsv)
            val visited = ExamQuestionBank.deserializeIntSet(entity.visitedIndicesCsv).ifEmpty { setOf(0) }
            val qTimes = ExamQuestionBank.deserializeIntMap(entity.questionTimeSecondsJson)
            val preset = runCatching { ExamPresetType.valueOf(entity.examType) }.getOrDefault(ExamPresetType.QUICK_TEST)
            val topics = entity.selectedTopicsCsv.split(",")
                .mapNotNull { id -> QuizTopic.values().firstOrNull { it.id == id.trim() } }
                .toSet()
                .ifEmpty { setOf(QuizTopic.MIXED_CHALLENGE) }
            val diff = PracticeDifficulty.values().firstOrNull { it.id == entity.difficulty } ?: PracticeDifficulty.MEDIUM
            val qType = QuizQuestionType.values().firstOrNull { it.id == entity.questionType } ?: QuizQuestionType.MCQ

            val remSec = entity.remainingSeconds.coerceAtLeast(30)
            val now = clock.elapsedRealtime()
            val deadline = if (entity.durationLimitSeconds > 0) now + remSec * 1000L else 0L
            val curIdx = entity.currentQuestionIndex.coerceIn(0, questions.lastIndex)

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
                    immediateFeedback = (preset == ExamPresetType.PRACTICE_EXAM)
                ),
                isFullExamMode = (preset != ExamPresetType.PRACTICE_EXAM),
                topic = topics.first(),
                mode = if (entity.durationLimitSeconds > 0) QuizMode.TIMED else QuizMode.TOPIC,
                questionType = qType,
                difficulty = diff,
                state = QuizState.IN_PROGRESS,
                questions = questions,
                currentIndex = curIdx,
                selectedOptionIndex = answers[curIdx],
                typedAnswer = "",
                isAnswerSubmitted = false,
                correctCount = answers.count { (i, a) -> questions.getOrNull(i)?.correctIndex == a },
                isFinished = false,
                userAnswers = answers,
                userTypedAnswers = emptyMap(),
                markedForReviewIndices = marked,
                visitedIndices = visited + curIdx,
                questionTimeSeconds = qTimes,
                isReviewMode = false,
                timeRemainingSeconds = remSec,
                configuredDurationSeconds = entity.durationLimitSeconds,
                elapsedSeconds = entity.elapsedSeconds,
                startElapsedRealtimeMs = now - (entity.elapsedSeconds * 1000L),
                deadlineElapsedRealtimeMs = deadline
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
        val marked = ExamQuestionBank.deserializeIntSet(entity.markedForReviewCsv)
        val visited = ExamQuestionBank.deserializeIntSet(entity.visitedIndicesCsv)
        val detailed = ExamQuestionBank.reconstructDetailedResult(entity)
        val preset = runCatching { ExamPresetType.valueOf(entity.examType) }.getOrDefault(ExamPresetType.QUICK_TEST)

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
            markedForReviewIndices = marked,
            visitedIndices = visited,
            isReviewMode = false,
            timeRemainingSeconds = 0,
            configuredDurationSeconds = entity.durationLimitSeconds,
            elapsedSeconds = entity.elapsedSeconds,
            detailedResult = detailed
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
            ans == null || ans != q.correctIndex
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
        } else {
            _screenStack.value = listOf(Screen.Home, Screen.Quizzes)
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
        } else {
            _screenStack.value = listOf(Screen.Home, Screen.PracticeLab)
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
        } else {
            _screenStack.value = listOf(Screen.Home, Screen.MentalMath)
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
                    repository.toggleLearned("table_${num}", currentlyLearned = false)
                    recordDailyActivity(3)
                }
            }
        } else {
            _tutorFeedback.value = "ভুল হয়েছে! সঠিক উত্তর: $num × $step = $expected"
        }
    }

    // --- Streak & Daily Activity Helpers ---
    private fun getTodayDateStr(): String {
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
        val questions = com.example.math.NumberSystemProvider.getPracticeQuestions(topic, difficulty)
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
            userAnswers = emptyMap()
        )
        navigateTo(Screen.NumberSystemPractice)
    }

    fun selectNumberSystemOption(index: Int) {
        val current = _numberSystemPracticeState.value
        if (!current.isSubmitted && !current.isCompleted) {
            _numberSystemPracticeState.value = current.copy(selectedOptionIndex = index)
        }
    }

    fun submitNumberSystemAnswer() {
        val current = _numberSystemPracticeState.value
        val selected = current.selectedOptionIndex ?: return
        if (current.isSubmitted || current.isCompleted) return

        val currentQ = current.questions.getOrNull(current.currentIndex) ?: return
        val isCorrect = selected == currentQ.correctIndex
        val newScore = if (isCorrect) current.score + 1 else current.score
        val updatedAnswers = current.userAnswers + (current.currentIndex to selected)

        _numberSystemPracticeState.value = current.copy(
            isSubmitted = true,
            score = newScore,
            userAnswers = updatedAnswers
        )

        if (isCorrect) {
            recordDailyActivity(1)
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
                    durationSeconds = 30L,
                    highestStreak = current.score
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
        viewModelScope.launch {
            repository.updateDailyStudyTargets(targetQuestions, targetMinutes)
        }
    }

    fun executeLearningRecommendation(rec: LearningRecommendation) {
        when (rec.actionType) {
            RecommendationActionType.RESUME_ACTIVE_EXAM -> resumeActiveExamAttempt()
            RecommendationActionType.OPEN_QUIZ_TOPIC -> {
                val topic = QuizTopic.values().firstOrNull { it.id == rec.targetTopicId } ?: QuizTopic.MIXED_CHALLENGE
                startQuiz(topic = topic, count = 10, mode = QuizMode.TOPIC)
            }
            RecommendationActionType.OPEN_EXAM_PRESET -> {
                val preset = runCatching { ExamPresetType.valueOf(rec.targetTopicId) }.getOrDefault(ExamPresetType.QUICK_TEST)
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

    fun setBcsSyllabusVersion(version: BcsSyllabusVersion) {
        _selectedBcsSyllabusVersion.value = version
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
            deadlineElapsedRealtimeMs = deadline
        )
        _quizSession.value = newState
        persistActiveExamStateAsync(newState)
        navigateTo(Screen.QuizPlay)
    }
}
