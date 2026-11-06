package com.example.data.repository

import com.example.data.db.MathDao
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.RecentFormulaEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.model.TopicAnalyticsEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

class MathRepository(private val dao: MathDao) {

    val allBookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val allQuizScores: Flow<List<QuizScoreEntity>> = dao.getAllQuizScores()
    val allProgress: Flow<List<ProgressEntity>> = dao.getAllProgress()
    val allPracticeHistory: Flow<List<PracticeHistoryEntity>> = dao.getAllPracticeHistory()
    val allTopicAnalytics: Flow<List<TopicAnalyticsEntity>> = dao.getAllTopicAnalytics()
    val recentFormulas: Flow<List<RecentFormulaEntity>> = dao.getRecentFormulas()
    val allExamAttempts: Flow<List<ExamAttemptEntity>> = dao.getAllExamAttempts()
    val completedExamAttempts: Flow<List<ExamAttemptEntity>> = dao.getCompletedExamAttempts()
    val activeExamAttempt: Flow<ExamAttemptEntity?> = dao.getActiveExamAttemptFlow()
    val allSolverHistory: Flow<List<SolverHistoryEntity>> = dao.getAllSolverHistory()
    val allDailyStudyActivity: Flow<List<DailyStudyActivityEntity>> = dao.getAllDailyStudyActivity()

    val bookmarks: Flow<List<BookmarkEntity>> get() = allBookmarks
    val quizScores: Flow<List<QuizScoreEntity>> get() = allQuizScores
    val progressList: Flow<List<ProgressEntity>> get() = allProgress

    companion object {
        fun todayDateKey(timestampMs: Long = System.currentTimeMillis()): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date(timestampMs))
        }
    }

    suspend fun toggleBookmark(bookmark: BookmarkEntity) {
        val exists = dao.isBookmarked(bookmark.id)
        if (exists) {
            dao.deleteBookmarkById(bookmark.id)
        } else {
            dao.insertBookmark(bookmark)
        }
    }

    suspend fun toggleBookmark(bookmark: BookmarkEntity, currentlyBookmarked: Boolean) {
        if (currentlyBookmarked) {
            dao.deleteBookmarkById(bookmark.id)
        } else {
            dao.insertBookmark(bookmark)
        }
    }

    suspend fun removeBookmark(id: String) {
        dao.deleteBookmarkById(id)
    }

    suspend fun recordQuizScore(score: QuizScoreEntity) {
        dao.insertQuizScore(score)
    }

    suspend fun saveQuizScore(
        topic: String,
        topicTitleBn: String,
        score: Int,
        totalQuestions: Int
    ) {
        val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        dao.insertQuizScore(
            QuizScoreEntity(
                topic = topic,
                topicTitleBn = topicTitleBn,
                score = score,
                totalQuestions = totalQuestions,
                percentage = percentage
            )
        )
    }

    suspend fun deleteQuizScore(id: Int) {
        dao.deleteQuizScoreById(id)
    }

    suspend fun setItemLearned(itemId: String, isLearned: Boolean) {
        dao.upsertProgress(ProgressEntity(itemId = itemId, isLearned = isLearned))
    }

    suspend fun toggleLearned(itemId: String, currentlyLearned: Boolean) {
        setItemLearned(itemId, !currentlyLearned)
    }

    suspend fun recordPractice(itemId: String) {
        setItemLearned(itemId, true)
    }

    suspend fun recordSessionAndAnalytics(
        module: String,
        topicId: String,
        topicTitleBn: String,
        difficulty: String,
        mode: String,
        score: Int,
        correctCount: Int,
        wrongCount: Int,
        totalQuestions: Int,
        bestStreak: Int,
        durationSeconds: Int
    ) {
        val accuracy = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
        val now = System.currentTimeMillis()
        dao.insertPracticeHistory(
            PracticeHistoryEntity(
                module = module,
                topicId = topicId,
                topicTitleBn = topicTitleBn,
                difficulty = difficulty,
                mode = mode,
                score = score,
                correctCount = correctCount,
                wrongCount = wrongCount,
                totalQuestions = totalQuestions,
                accuracyPercent = accuracy,
                bestStreak = bestStreak,
                durationSeconds = durationSeconds,
                timestamp = now
            )
        )

        upsertTopicMasteryDelta(
            module = module,
            topicId = topicId,
            topicTitleBn = topicTitleBn,
            difficulty = difficulty,
            attemptedDelta = correctCount + wrongCount,
            correctDelta = correctCount,
            wrongDelta = wrongCount,
            bestStreak = bestStreak,
            durationSeconds = durationSeconds,
            now = now
        )

        recordDailyStudyDelta(
            questionsAttemptedDelta = correctCount + wrongCount,
            correctDelta = correctCount,
            wrongDelta = wrongCount,
            studyTimeSecondsDelta = max(1, durationSeconds),
            examsCompletedDelta = if (module == "QUIZ") 1 else 0,
            practiceSessionsCompletedDelta = if (module != "QUIZ") 1 else 0,
            timestampMs = now
        )
    }

    suspend fun upsertTopicMasteryDelta(
        module: String,
        topicId: String,
        topicTitleBn: String,
        difficulty: String,
        attemptedDelta: Int,
        correctDelta: Int,
        wrongDelta: Int,
        bestStreak: Int,
        durationSeconds: Int,
        now: Long = System.currentTimeMillis()
    ) {
        val key = "${module}_${topicId}_${difficulty}"
        val existing = dao.getTopicAnalyticsByKey(key)
        val updated = if (existing == null) {
            TopicAnalyticsEntity(
                analyticsKey = key,
                module = module,
                topicId = topicId,
                topicTitleBn = topicTitleBn,
                difficulty = difficulty,
                totalAttempted = max(0, attemptedDelta),
                totalCorrect = max(0, correctDelta),
                totalWrong = max(0, wrongDelta),
                bestStreak = max(0, bestStreak),
                totalTimeSeconds = max(0, durationSeconds),
                lastPracticedAt = now
            )
        } else {
            existing.copy(
                totalAttempted = existing.totalAttempted + max(0, attemptedDelta),
                totalCorrect = existing.totalCorrect + max(0, correctDelta),
                totalWrong = existing.totalWrong + max(0, wrongDelta),
                bestStreak = max(existing.bestStreak, bestStreak),
                totalTimeSeconds = existing.totalTimeSeconds + max(0, durationSeconds),
                lastPracticedAt = now
            )
        }
        dao.upsertTopicAnalytics(updated)
    }

    suspend fun recordSessionAndAnalytics(
        moduleType: String,
        categoryId: String,
        categoryTitleBn: String,
        difficulty: String,
        mode: String,
        inputFormat: String = "mcq",
        score: Int,
        correctCount: Int,
        wrongCount: Int,
        totalQuestions: Int,
        accuracyPercent: Int = 0,
        durationSeconds: Long,
        highestStreak: Int
    ) {
        recordSessionAndAnalytics(
            module = moduleType,
            topicId = categoryId,
            topicTitleBn = categoryTitleBn,
            difficulty = difficulty,
            mode = "$mode:$inputFormat",
            score = score,
            correctCount = correctCount,
            wrongCount = wrongCount,
            totalQuestions = totalQuestions,
            bestStreak = highestStreak,
            durationSeconds = durationSeconds.toInt().coerceAtLeast(1)
        )
    }

    suspend fun recordFormulaViewed(
        formulaId: String,
        titleBn: String,
        subjectId: String = "",
        subjectName: String = ""
    ) {
        val resolvedSubject = subjectId.ifEmpty { subjectName }
        dao.upsertRecentFormula(
            RecentFormulaEntity(
                formulaId = formulaId,
                titleBn = titleBn,
                subjectId = resolvedSubject,
                viewedAt = System.currentTimeMillis()
            )
        )
    }

    // Exam Attempt Persistence (Room v4)
    suspend fun saveExamAttemptState(attempt: ExamAttemptEntity) {
        dao.upsertExamAttempt(attempt)
    }

    suspend fun getActiveExamAttemptSnapshot(): ExamAttemptEntity? {
        return dao.getActiveExamAttempt()
    }

    suspend fun getExamAttemptById(attemptId: String): ExamAttemptEntity? {
        return dao.getExamAttemptById(attemptId)
    }

    suspend fun abandonActiveExams() {
        dao.abandonActiveExamAttempts(System.currentTimeMillis())
    }

    suspend fun deleteExamAttempt(attemptId: String) {
        dao.deleteExamAttemptById(attemptId)
    }

    suspend fun finalizeExamAttempt(
        completedAttempt: ExamAttemptEntity,
        perTopicStats: List<TopicExamStat>
    ) {
        val now = completedAttempt.completedAt ?: System.currentTimeMillis()
        dao.upsertExamAttempt(completedAttempt)

        // Also record into quiz_scores for backward compatibility with legacy consumers
        val roundedPct = completedAttempt.percentage.roundToInt().coerceIn(0, 100)
        dao.insertQuizScore(
            QuizScoreEntity(
                topic = completedAttempt.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" },
                topicTitleBn = completedAttempt.examTitleBn,
                score = completedAttempt.correctCount,
                totalQuestions = completedAttempt.totalQuestions,
                percentage = roundedPct,
                timestamp = now
            )
        )

        // Update per-topic analytics without inflating session count per topic
        if (perTopicStats.isNotEmpty()) {
            perTopicStats.forEach { stat ->
                upsertTopicMasteryDelta(
                    module = "QUIZ",
                    topicId = stat.topicId,
                    topicTitleBn = stat.topicTitleBn,
                    difficulty = completedAttempt.difficulty,
                    attemptedDelta = stat.correctCount + stat.wrongCount,
                    correctDelta = stat.correctCount,
                    wrongDelta = stat.wrongCount,
                    bestStreak = stat.correctCount,
                    durationSeconds = stat.timeSpentSeconds,
                    now = now
                )
            }
        } else {
            upsertTopicMasteryDelta(
                module = "QUIZ",
                topicId = completedAttempt.selectedTopicsCsv.ifBlank { "MIXED" },
                topicTitleBn = completedAttempt.examTitleBn,
                difficulty = completedAttempt.difficulty,
                attemptedDelta = completedAttempt.correctCount + completedAttempt.wrongCount,
                correctDelta = completedAttempt.correctCount,
                wrongDelta = completedAttempt.wrongCount,
                bestStreak = completedAttempt.correctCount,
                durationSeconds = completedAttempt.elapsedSeconds,
                now = now
            )
        }

        // Insert a single QUIZ practice_history entry for the exam attempt
        dao.insertPracticeHistory(
            PracticeHistoryEntity(
                module = "QUIZ",
                topicId = completedAttempt.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" },
                topicTitleBn = completedAttempt.examTitleBn,
                difficulty = completedAttempt.difficulty,
                mode = completedAttempt.examType,
                score = completedAttempt.finalScore.roundToInt(),
                correctCount = completedAttempt.correctCount,
                wrongCount = completedAttempt.wrongCount,
                totalQuestions = completedAttempt.totalQuestions,
                accuracyPercent = completedAttempt.accuracyPercent.roundToInt(),
                bestStreak = completedAttempt.correctCount,
                durationSeconds = completedAttempt.elapsedSeconds,
                timestamp = now
            )
        )

        recordDailyStudyDelta(
            questionsAttemptedDelta = completedAttempt.correctCount + completedAttempt.wrongCount,
            correctDelta = completedAttempt.correctCount,
            wrongDelta = completedAttempt.wrongCount,
            studyTimeSecondsDelta = max(1, completedAttempt.elapsedSeconds),
            examsCompletedDelta = 1,
            practiceSessionsCompletedDelta = 0,
            timestampMs = now
        )
    }

    // Solver History Persistence (Room v4)
    suspend fun saveSolvedProblemHistory(history: SolverHistoryEntity) {
        dao.insertSolverHistory(history)
    }

    suspend fun deleteSolvedProblemHistory(id: Int) {
        dao.deleteSolverHistoryById(id)
    }

    suspend fun clearSolvedProblemHistory() {
        dao.clearAllSolverHistory()
    }

    // Daily Study Goals & Activity (Room v4)
    suspend fun recordDailyStudyDelta(
        questionsAttemptedDelta: Int,
        correctDelta: Int,
        wrongDelta: Int,
        studyTimeSecondsDelta: Int,
        examsCompletedDelta: Int,
        practiceSessionsCompletedDelta: Int,
        timestampMs: Long = System.currentTimeMillis()
    ) {
        val dateKey = todayDateKey(timestampMs)
        val existing = dao.getDailyStudyActivityByDate(dateKey)
        val updated = if (existing == null) {
            DailyStudyActivityEntity(
                dateKey = dateKey,
                questionsAttempted = max(0, questionsAttemptedDelta),
                correctCount = max(0, correctDelta),
                wrongCount = max(0, wrongDelta),
                studyTimeSeconds = max(0, studyTimeSecondsDelta),
                examsCompleted = max(0, examsCompletedDelta),
                practiceSessionsCompleted = max(0, practiceSessionsCompletedDelta),
                dailyTargetQuestions = 20,
                dailyTargetMinutes = 15,
                updatedAt = timestampMs
            )
        } else {
            existing.copy(
                questionsAttempted = existing.questionsAttempted + max(0, questionsAttemptedDelta),
                correctCount = existing.correctCount + max(0, correctDelta),
                wrongCount = existing.wrongCount + max(0, wrongDelta),
                studyTimeSeconds = existing.studyTimeSeconds + max(0, studyTimeSecondsDelta),
                examsCompleted = existing.examsCompleted + max(0, examsCompletedDelta),
                practiceSessionsCompleted = existing.practiceSessionsCompleted + max(0, practiceSessionsCompletedDelta),
                updatedAt = timestampMs
            )
        }
        dao.upsertDailyStudyActivity(updated)
    }

    suspend fun updateDailyStudyTargets(
        targetQuestions: Int,
        targetMinutes: Int,
        timestampMs: Long = System.currentTimeMillis()
    ) {
        val safeQuestions = targetQuestions.coerceIn(5, 200)
        val safeMinutes = targetMinutes.coerceIn(5, 240)
        val dateKey = todayDateKey(timestampMs)
        val existing = dao.getDailyStudyActivityByDate(dateKey)
        val updated = if (existing == null) {
            DailyStudyActivityEntity(
                dateKey = dateKey,
                questionsAttempted = 0,
                correctCount = 0,
                wrongCount = 0,
                studyTimeSeconds = 0,
                examsCompleted = 0,
                practiceSessionsCompleted = 0,
                dailyTargetQuestions = safeQuestions,
                dailyTargetMinutes = safeMinutes,
                updatedAt = timestampMs
            )
        } else {
            existing.copy(
                dailyTargetQuestions = safeQuestions,
                dailyTargetMinutes = safeMinutes,
                updatedAt = timestampMs
            )
        }
        dao.upsertDailyStudyActivity(updated)
    }
}

data class TopicExamStat(
    val topicId: String,
    val topicTitleBn: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val timeSpentSeconds: Int
)
