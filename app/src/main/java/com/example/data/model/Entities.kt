package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["category"])
    ]
)
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val category: String, // "TABLE", "PRIME", "SQUARE_CUBE", "FORMULA", "FORMULA_LIBRARY", "NUMBER_SYSTEM", "PROBLEM_SOLVER", "BCS_PREP"
    val titleBn: String,
    val subtitleBn: String,
    val detailBn: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "quiz_scores",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["topic"])
    ]
)
data class QuizScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val topicTitleBn: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor(
        topicId: String,
        topicTitleBn: String,
        score: Int,
        totalQuestions: Int,
        percentage: Int,
        timestamp: Long = System.currentTimeMillis()
    ) : this(
        id = 0,
        topic = topicId,
        topicTitleBn = topicTitleBn,
        score = score,
        totalQuestions = totalQuestions,
        percentage = percentage,
        timestamp = timestamp
    )

    val topicId: String get() = topic
}

@Entity(
    tableName = "user_progress",
    indices = [Index(value = ["isLearned"])]
)
data class ProgressEntity(
    @PrimaryKey val itemId: String, // e.g., "table_12", "formula_sq_1", "prime_1_100"
    val isLearned: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "practice_history",
    indices = [
        Index(value = ["module"]),
        Index(value = ["timestamp"])
    ]
)
data class PracticeHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val module: String, // "PRACTICE_LAB", "MENTAL_MATH", "QUIZ", "NUMBER_SYSTEM", "BCS_PREP"
    val topicId: String,
    val topicTitleBn: String,
    val difficulty: String,
    val mode: String,
    val score: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val totalQuestions: Int,
    val accuracyPercent: Int,
    val bestStreak: Int,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "topic_analytics",
    indices = [Index(value = ["module", "topicId"])]
)
data class TopicAnalyticsEntity(
    @PrimaryKey val analyticsKey: String, // "${module}_${topicId}_${difficulty}"
    val module: String,
    val topicId: String,
    val topicTitleBn: String,
    val difficulty: String,
    val totalAttempted: Int,
    val totalCorrect: Int,
    val totalWrong: Int,
    val bestStreak: Int,
    val totalTimeSeconds: Int,
    val lastPracticedAt: Long = System.currentTimeMillis()
) {
    val topicKey: String get() = topicId
}

@Entity(
    tableName = "recent_formulas",
    indices = [Index(value = ["viewedAt"])]
)
data class RecentFormulaEntity(
    @PrimaryKey val formulaId: String,
    val titleBn: String,
    val subjectId: String,
    val viewedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "exam_attempts",
    indices = [
        Index(value = ["status"]),
        Index(value = ["startedAt"]),
        Index(value = ["examType"]),
        Index(value = ["examProfileId"])
    ]
)
data class ExamAttemptEntity(
    @PrimaryKey val attemptId: String,
    val examTitleBn: String,
    val examType: String,
    val selectedTopicsCsv: String,
    val difficulty: String,
    val questionType: String,
    val totalQuestions: Int,
    val durationLimitSeconds: Int,
    val marksPerCorrect: Double,
    val negativeMarksPerWrong: Double,
    val status: String, // "IN_PROGRESS", "COMPLETED", "ABANDONED"
    val currentQuestionIndex: Int,
    val elapsedSeconds: Int,
    val remainingSeconds: Int,
    val questionsJson: String,
    val answersJson: String,
    val markedForReviewCsv: String,
    val visitedIndicesCsv: String,
    val questionTimeSecondsJson: String,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val earnedMarks: Double,
    val negativeMarksDeducted: Double,
    val finalScore: Double,
    val percentage: Double,
    val accuracyPercent: Double,
    val startedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val examProfileId: String = ""
)

@Entity(
    tableName = "solver_history",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["topicId"])
    ]
)
data class SolverHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicId: String,
    val topicTitleBn: String,
    val categoryTitleBn: String,
    val inputSummaryBn: String,
    val inputsJson: String,
    val finalAnswerBn: String,
    val stepsJson: String,
    val formulaUsedBn: String,
    val verificationBn: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_study_activity"
)
data class DailyStudyActivityEntity(
    @PrimaryKey val dateKey: String, // YYYY-MM-DD in local timezone
    val questionsAttempted: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val studyTimeSeconds: Int,
    val examsCompleted: Int,
    val practiceSessionsCompleted: Int,
    val dailyTargetQuestions: Int = 20,
    val dailyTargetMinutes: Int = 15,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "book_lesson_progress",
    indices = [
        Index(value = ["bookId", "chapterNumber"]),
        Index(value = ["isCompleted"])
    ]
)
data class BookLessonProgressEntity(
    @PrimaryKey val lessonKey: String, // "${bookId}:${chapterNumber}:${lessonId}"
    val bookId: String,
    val chapterNumber: Int,
    val lessonId: String,
    val isCompleted: Boolean,
    val miniCheckPassed: Boolean,
    val timeSpentSeconds: Int,
    val lastVisitedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "book_reading_state"
)
data class BookReadingStateEntity(
    @PrimaryKey val bookId: String,
    val lastChapterNumber: Int,
    val lastLessonId: String,
    val lastPracticeMode: String,
    val lastPrintedPage: Int,
    val lastPdfPage: Int,
    val totalPagesRead: Int,
    val bookmarkedPagesCsv: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val preferredViewMode: String = "STRUCTURED",
    val lastZoomScale: Float = 1.0f,
    val activeEditionId: String = "NCTB_2026_STANDARD"
) {
    val lastReadPrintedPage: Int get() = lastPrintedPage
    val lastReadChapterNumber: Int get() = lastChapterNumber
    val zoomScale: Float get() = lastZoomScale
}

@Entity(
    tableName = "book_notes",
    indices = [
        Index(value = ["bookId", "chapterNumber"]),
        Index(value = ["updatedAt"])
    ]
)
data class BookNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: String,
    val chapterNumber: Int,
    val lessonId: String,
    val printedPage: Int,
    val titleBn: String,
    val contentBn: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val noteTitleBn: String get() = titleBn
    val noteBodyBn: String get() = contentBn
}

@Entity(
    tableName = "book_mistakes",
    indices = [
        Index(value = ["bookId", "chapterNumber"]),
        Index(value = ["isResolved"]),
        Index(value = ["lastMistakeAt"])
    ]
)
data class BookMistakeEntity(
    @PrimaryKey val questionId: String,
    val bookId: String,
    val chapterNumber: Int,
    val questionPromptBn: String,
    val previousWrongAnswerBn: String,
    val correctAnswerBn: String,
    val explanationBn: String,
    val sourceProvenance: String, // "ORIGINAL_TEXTBOOK", "CONCEPT_DERIVED", "GENERATED"
    val mistakeCount: Int = 1,
    val isResolved: Boolean = false,
    val lastMistakeAt: Long = System.currentTimeMillis()
) {
    val mistakeId: String get() = questionId
    val userAnswerBn: String get() = previousWrongAnswerBn
}

@Entity(
    tableName = "book_spaced_revision",
    indices = [
        Index(value = ["nextReviewEpochMs"]),
        Index(value = ["bookId", "chapterNumber"])
    ]
)
data class BookSpacedRevisionEntity(
    @PrimaryKey val itemId: String,
    val itemType: String, // "QUESTION", "LESSON", "FORMULA"
    val bookId: String,
    val chapterNumber: Int,
    val titleBn: String,
    val subtitleBn: String,
    val intervalStage: Int, // 0 -> 1d, 1 -> 3d, 2 -> 7d, 3 -> 14d, 4 -> Mastered
    val nextReviewEpochMs: Long,
    val lastReviewedAt: Long = System.currentTimeMillis()
) {
    val nextReviewDueAt: Long get() = nextReviewEpochMs
    val summaryBn: String get() = subtitleBn
    val intervalStepIndex: Int get() = intervalStage.coerceIn(0, 3)
    val intervalDays: Int
        get() = when (intervalStage) {
            0 -> 1
            1 -> 3
            2 -> 7
            else -> 14
        }
    val completedReviewsCount: Int get() = intervalStage
}

@Entity(tableName = "bcs_user_profile")
data class BcsUserProfileEntity(
    @PrimaryKey val profileId: String = "active_profile",
    val selectedExamProfileId: String = "BCS_50TH_PRELIM_2025",
    val onboardingCompleted: Boolean = false,
    val currentStageIndex: Int = 1,
    val dailyTargetMinutes: Int = 30,
    val dailyTargetQuestions: Int = 20,
    val targetCompletionEpochMs: Long? = null,
    val officialExamDateEpochMs: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val preparationLevel: String = "BEGINNER",
    val weeklyTargetTopics: Int = 3,
    val weeklyTargetMocks: Int = 1,
    val targetAccuracyPercent: Int = 75
)

@Entity(
    tableName = "bcs_topic_progress",
    indices = [
        Index(value = ["subjectTrack"]),
        Index(value = ["lastStudiedAt"]),
        Index(value = ["examProfileId"])
    ]
)
data class BcsTopicProgressEntity(
    @PrimaryKey val topicId: String,
    val subjectTrack: String, // "MATH" or "MENTAL_ABILITY"
    val isGuideCompleted: Boolean = false,
    val miniCheckPassed: Boolean = false,
    val questionsAttempted: Int = 0,
    val questionsCorrect: Int = 0,
    val totalTimeSeconds: Int = 0,
    val masteryPercent: Int = 0,
    val lastStudiedAt: Long = System.currentTimeMillis(),
    val examProfileId: String = "BCS_50TH_PRELIM_2025"
) {
    val rawTopicId: String
        get() = topicId.substringAfter("::", topicId)

    fun belongsToProfile(profileId: String): Boolean {
        if (examProfileId == profileId) return true
        return profileId == "BCS_50TH_PRELIM_2025" && !topicId.contains("::") && examProfileId.isBlank()
    }

    companion object {
        fun buildScopedKey(examProfileId: String, rawTopicId: String): String {
            val cleanTopic = rawTopicId.substringAfter("::", rawTopicId)
            return "$examProfileId::$cleanTopic"
        }
    }
}

@Entity(
    tableName = "bcs_mistake_notebook",
    indices = [
        Index(value = ["subjectTrack"]),
        Index(value = ["isResolved"]),
        Index(value = ["nextReviewEpochMs"]),
        Index(value = ["examProfileId"])
    ]
)
data class BcsMistakeEntity(
    @PrimaryKey val questionId: String,
    val subjectTrack: String, // "MATH" or "MENTAL_ABILITY"
    val topicId: String,
    val questionPromptBn: String,
    val wrongAnswerBn: String,
    val correctAnswerBn: String,
    val explanationBn: String,
    val shortcutTipBn: String = "",
    val provenanceBadgeBn: String = "",
    val mistakeCount: Int = 1,
    val retrySuccessCount: Int = 0,
    val isResolved: Boolean = false,
    val nextReviewEpochMs: Long = System.currentTimeMillis() + 86400000L,
    val intervalStage: Int = 0,
    val lastMistakeAt: Long = System.currentTimeMillis(),
    val examProfileId: String = "BCS_50TH_PRELIM_2025"
) {
    val rawQuestionId: String
        get() = questionId.substringAfter("::", questionId)

    fun belongsToProfile(profileId: String): Boolean {
        if (examProfileId == profileId) return true
        return profileId == "BCS_50TH_PRELIM_2025" && !questionId.contains("::") && examProfileId.isBlank()
    }
}

@Entity(
    tableName = "bcs_study_goals",
    indices = [
        Index(value = ["examProfileId", "dateKey"]),
        Index(value = ["updatedAt"])
    ]
)
data class BcsStudyPlanGoalEntity(
    @PrimaryKey val goalKey: String, // "${examProfileId}_${dateKey}"
    val examProfileId: String,
    val dateKey: String,
    val lessonsCompleted: Int = 0,
    val questionsSolved: Int = 0,
    val revisionsCompleted: Int = 0,
    val studyTimeMinutes: Int = 0,
    val mockExamsCompleted: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "unified_mistake_notebook",
    indices = [
        Index(value = ["sourceModule", "resolved"]),
        Index(value = ["nextReviewAtMillis"])
    ]
)
data class UnifiedMistakeEntity(
    @PrimaryKey val mistakeKey: String,
    val sourceModule: String, // "PRACTICE_LAB", "MENTAL_MATH", "NUMBER_SYSTEM", "QUIZ", "BCS", "BOOK"
    val subjectId: String,
    val curriculumTrack: String = "NCTB", // "NCTB", "BDMO", "BCS"
    val questionPromptBn: String,
    val userAnswerBn: String,
    val correctAnswerBn: String,
    val diagnosticExplanationBn: String,
    val errorType: String = "CONCEPTUAL",
    val failureCount: Int = 1,
    val reviewIntervalDays: Int = 1,
    val nextReviewAtMillis: Long = System.currentTimeMillis() + 86400000L,
    val resolved: Boolean = false,
    val lastFailedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "formula_mastery_state",
    indices = [
        Index(value = ["subjectId"]),
        Index(value = ["masteryLevel"])
    ]
)
data class FormulaMasteryEntity(
    @PrimaryKey val formulaId: String,
    val subjectId: String,
    val masteryLevel: String = "LEARNING", // "UNSEEN", "LEARNING", "MASTERED"
    val practiceCount: Int = 1,
    val lastPracticedAtMillis: Long = System.currentTimeMillis()
)




