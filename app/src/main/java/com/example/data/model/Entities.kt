package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
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
    indices = [Index(value = ["timestamp"])]
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
        Index(value = ["examType"])
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
    val completedAt: Long? = null
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
