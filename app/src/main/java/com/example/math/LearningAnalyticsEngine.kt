package com.example.math

import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.TopicAnalyticsEntity
import com.example.data.repository.MathRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

enum class AnalyticsTimeFilter(val id: String, val titleBn: String, val daysLookback: Int?) {
    TODAY("TODAY", "আজ", 1),
    LAST_7_DAYS("LAST_7_DAYS", "গত ৭ দিন", 7),
    LAST_30_DAYS("LAST_30_DAYS", "গত ৩০ দিন", 30),
    THIS_MONTH("THIS_MONTH", "এই মাস", null),
    LAST_90_DAYS("LAST_90_DAYS", "গত ৯০ দিন", 90),
    ALL_TIME("ALL_TIME", "সর্বমোট", null)
}

enum class SampleConfidenceLevel(val titleBn: String, val minQuestions: Int) {
    INSUFFICIENT_DATA("অপর্যাপ্ত তথ্য (<৫টি প্রশ্ন)", 0),
    LOW_CONFIDENCE("প্রাথমিক মূল্যায়ন (৫–৯টি প্রশ্ন)", 5),
    MODERATE_CONFIDENCE("মাঝারি আস্থা (১০–১৯টি প্রশ্ন)", 10),
    HIGH_CONFIDENCE("উচ্চ আস্থা (২০+ প্রশ্ন)", 20)
}

enum class TopicMasteryStatus(val titleBn: String) {
    UNTESTED("এখনো অনুশীলন হয়নি"),
    NEEDS_MORE_DATA("আরও অনুশীলন প্রয়োজন"),
    WEAK("দুর্বল (অগ্রাধিকার দিন)"),
    DEVELOPING("উন্নয়নশীল (মাঝারি)"),
    MASTERED("দক্ষ (Mastered)")
}

enum class TopicTrendDirection(val titleBn: String, val symbol: String) {
    IMPROVING("উন্নতি হচ্ছে", "↑"),
    DECLINING("কমছে", "↓"),
    STABLE("স্থিতিশীল", "→")
}

data class TopicMasteryReport(
    val topicId: String,
    val topicTitleBn: String,
    val modulesInvolved: Set<String>,
    val totalAttempted: Int,
    val totalCorrect: Int,
    val totalWrong: Int,
    val accuracyPercent: Int,
    val totalTimeSeconds: Int,
    val avgTimePerQuestionSeconds: Double,
    val masteryScore: Int, // 0..100 weighted by sample confidence
    val confidenceLevel: SampleConfidenceLevel,
    val masteryStatus: TopicMasteryStatus,
    val trend: TopicTrendDirection,
    val explanationBn: String,
    val lastPracticedAt: Long
)

data class SeparatedKpiSummary(
    // Examination KPIs (strictly from exam_attempts + legacy quiz_scores without duplication)
    val totalExamsCompleted: Int,
    val examQuestionsAttempted: Int,
    val examCorrectAnswers: Int,
    val examWrongAnswers: Int,
    val examAverageScorePercent: Int,
    val examBestScorePercent: Int,
    val examLowestScorePercent: Int,
    val examOverallAccuracyPercent: Int,
    val examTotalTimeSeconds: Int,

    // Practice & Challenge KPIs (strictly from non-QUIZ practice_history)
    val totalPracticeSessions: Int,
    val practiceQuestionsAttempted: Int,
    val practiceCorrectAnswers: Int,
    val practiceWrongAnswers: Int,
    val practiceAccuracyPercent: Int,
    val practiceBestStreak: Int,
    val practiceTotalTimeSeconds: Int,

    // Unified Learning Totals (Exam + Practice, strictly non-overlapping)
    val combinedQuestionsAttempted: Int,
    val combinedCorrectAnswers: Int,
    val combinedAccuracyPercent: Int,
    val combinedStudyTimeSeconds: Int,
    val learnedItemsCount: Int
)

data class DailyGoalProgressReport(
    val todayDateKey: String,
    val todayQuestionsAttempted: Int,
    val dailyTargetQuestions: Int,
    val questionsProgressFraction: Float,
    val todayStudyMinutes: Int,
    val dailyTargetMinutes: Int,
    val timeProgressFraction: Float,
    val isTodayGoalMet: Boolean,
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val activeDaysLast7: Int,
    val activeDaysLast30: Int,
    val weeklyConsistencyPercent: Int,
    val last7DaysActivity: List<DailyActivityPoint>
)

data class DailyActivityPoint(
    val dateKey: String,
    val shortLabelBn: String,
    val questionsAttempted: Int,
    val accuracyPercent: Int,
    val studyMinutes: Int,
    val goalMet: Boolean
)

enum class RecommendationActionType {
    OPEN_QUIZ_TOPIC,
    OPEN_EXAM_PRESET,
    OPEN_PRACTICE_LAB,
    OPEN_MENTAL_MATH,
    OPEN_FORMULA_LIBRARY,
    OPEN_PROBLEM_SOLVER,
    OPEN_BCS_PREPARATION,
    RESUME_ACTIVE_EXAM
}

data class LearningRecommendation(
    val id: String,
    val priorityRank: Int,
    val badgeBn: String,
    val titleBn: String,
    val reasonBn: String,
    val actionLabelBn: String,
    val actionType: RecommendationActionType,
    val targetTopicId: String = ""
)

data class ComprehensiveAnalyticsDashboardData(
    val selectedTimeFilter: AnalyticsTimeFilter,
    val kpiSummary: SeparatedKpiSummary,
    val goalProgress: DailyGoalProgressReport,
    val topicMasteryList: List<TopicMasteryReport>,
    val masteredTopics: List<TopicMasteryReport>,
    val developingTopics: List<TopicMasteryReport>,
    val weakTopics: List<TopicMasteryReport>,
    val lowSampleTopics: List<TopicMasteryReport>,
    val recommendations: List<LearningRecommendation>,
    val examTrendPoints: List<Pair<String, Int>>,
    val hasAnyActivity: Boolean
)

object LearningAnalyticsEngine {

    fun filterStartTimestamp(
        filter: AnalyticsTimeFilter,
        nowMs: Long = System.currentTimeMillis()
    ): Long {
        if (filter == AnalyticsTimeFilter.ALL_TIME) return 0L
        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return when (filter) {
            AnalyticsTimeFilter.TODAY -> cal.timeInMillis
            AnalyticsTimeFilter.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.timeInMillis
            }
            else -> {
                val days = filter.daysLookback ?: 7
                cal.add(Calendar.DAY_OF_YEAR, -(days - 1))
                cal.timeInMillis
            }
        }
    }

    fun buildDashboardAnalytics(
        timeFilter: AnalyticsTimeFilter,
        completedExams: List<ExamAttemptEntity>,
        activeExam: ExamAttemptEntity?,
        quizScores: List<QuizScoreEntity>,
        practiceHistory: List<PracticeHistoryEntity>,
        topicAnalytics: List<TopicAnalyticsEntity>,
        progressItems: List<ProgressEntity>,
        dailyActivities: List<DailyStudyActivityEntity>,
        nowMs: Long = System.currentTimeMillis()
    ): ComprehensiveAnalyticsDashboardData {
        val cutoffMs = filterStartTimestamp(timeFilter, nowMs)

        val filteredExams = completedExams.filter { (it.completedAt ?: it.updatedAt) >= cutoffMs }
        val filteredQuizScores = quizScores.filter { it.timestamp >= cutoffMs }
        val filteredPractice = practiceHistory.filter { it.timestamp >= cutoffMs && it.module != "QUIZ" }
        val examTimestamps = filteredExams.map { it.completedAt ?: it.updatedAt }
        val standaloneLegacyQuizzes = filteredQuizScores.filter { legacy ->
            !legacy.topicId.startsWith("PRACTICE_") &&
                !legacy.topicId.startsWith("MENTAL_MATH_") &&
                !legacy.topicId.startsWith("BOOK_") &&
                !legacy.topicId.startsWith("VISUAL_") &&
                legacy.topicId != "NUMBER_SYSTEM" &&
                examTimestamps.none { kotlin.math.abs(it - legacy.timestamp) <= 2500L }
        }

        val kpi = computeSeparatedKpis(
            filteredExams = filteredExams,
            filteredLegacyQuizScores = filteredQuizScores,
            filteredPractice = filteredPractice,
            learnedItemsCount = progressItems.count { it.isLearned }
        )

        val windowedPracticeAndQuizHistory = practiceHistory.filter { it.timestamp >= cutoffMs }
        val effectiveTopicAnalytics = if (timeFilter == AnalyticsTimeFilter.ALL_TIME) {
            topicAnalytics
        } else {
            buildWindowedTopicAnalytics(
                windowedHistory = windowedPracticeAndQuizHistory,
                windowedExams = filteredExams,
                windowedLegacyQuizzes = standaloneLegacyQuizzes
            )
        }

        val masteryList = computeTopicMasteryReports(
            topicAnalytics = effectiveTopicAnalytics,
            practiceHistory = windowedPracticeAndQuizHistory,
            completedExams = filteredExams
        )

        val goalReport = computeDailyGoalsAndStreaks(
            dailyActivities = dailyActivities,
            practiceHistory = practiceHistory,
            completedExams = completedExams,
            quizScores = quizScores,
            nowMs = nowMs
        )

        val recommendations = generateDeterministicRecommendations(
            activeExam = activeExam,
            kpi = kpi,
            goalReport = goalReport,
            masteryList = masteryList,
            completedExams = completedExams
        )

        val examTrendPoints = run {
            data class RawTrendItem(val timestamp: Long, val percentage: Int)
            val combined = buildList {
                filteredExams.forEach { exam ->
                    add(
                        RawTrendItem(
                            timestamp = exam.completedAt ?: exam.updatedAt,
                            percentage = exam.percentage.roundToInt().coerceIn(0, 100)
                        )
                    )
                }
                standaloneLegacyQuizzes.forEach { score ->
                    add(
                        RawTrendItem(
                            timestamp = score.timestamp,
                            percentage = score.percentage.coerceIn(0, 100)
                        )
                    )
                }
            }
            val chronologicalLatest = combined
                .sortedByDescending { it.timestamp }
                .take(10)
                .reversed()
            val prefix = if (filteredExams.isEmpty() && standaloneLegacyQuizzes.isNotEmpty()) "কুইজ" else "পরীক্ষা"
            chronologicalLatest.mapIndexed { idx, item ->
                "$prefix ${(idx + 1).toBengaliDigits()}" to item.percentage
            }
        }

        val hasAnyActivity = kpi.totalExamsCompleted > 0 ||
            kpi.totalPracticeSessions > 0 ||
            masteryList.isNotEmpty() ||
            kpi.learnedItemsCount > 0

        return ComprehensiveAnalyticsDashboardData(
            selectedTimeFilter = timeFilter,
            kpiSummary = kpi,
            goalProgress = goalReport,
            topicMasteryList = masteryList,
            masteredTopics = masteryList.filter { it.masteryStatus == TopicMasteryStatus.MASTERED },
            developingTopics = masteryList.filter { it.masteryStatus == TopicMasteryStatus.DEVELOPING },
            weakTopics = masteryList.filter { it.masteryStatus == TopicMasteryStatus.WEAK },
            lowSampleTopics = masteryList.filter { it.masteryStatus == TopicMasteryStatus.NEEDS_MORE_DATA },
            recommendations = recommendations,
            examTrendPoints = examTrendPoints,
            hasAnyActivity = hasAnyActivity
        )
    }

    fun normalizeTopicTitleBn(rawTitle: String, fallbackTopicId: String = ""): String {
        val base = rawTitle.trim().ifBlank { fallbackTopicId.trim() }
        val cleaned = base
            .removePrefix("প্র্যাকটিস: ")
            .removePrefix("মেন্টাল ম্যাথ: ")
            .removePrefix("ভিজ্যুয়াল গণিত: ")
            .replace(Regex("""\s*\((সহজ|মাঝারি|কঠিন|মিশ্র|Easy|Medium|Hard|Mixed|STANDARD)\)\s*$"""), "")
            .replace(Regex("""\s+কুইজ(\s*\([০-৯0-9\-–]+\))?\s*$"""), "")
            .trim()
        return cleaned.ifBlank { base }
    }

    private fun buildWindowedTopicAnalytics(
        windowedHistory: List<PracticeHistoryEntity>,
        windowedExams: List<ExamAttemptEntity>,
        windowedLegacyQuizzes: List<QuizScoreEntity>
    ): List<TopicAnalyticsEntity> {
        if (windowedHistory.isEmpty() && windowedExams.isEmpty() && windowedLegacyQuizzes.isEmpty()) {
            return emptyList()
        }
        val map = mutableMapOf<String, TopicAnalyticsEntity>()
        val quizHistoryTimestamps = mutableListOf<Long>()
        windowedHistory.forEach { h ->
            if (h.module == "QUIZ") {
                quizHistoryTimestamps.add(h.timestamp)
            }
            val key = "${h.module}:${h.topicId}"
            val attempted = h.correctCount + h.wrongCount
            val existing = map[key]
            if (existing == null) {
                map[key] = TopicAnalyticsEntity(
                    analyticsKey = key,
                    module = h.module,
                    topicId = h.topicId,
                    topicTitleBn = normalizeTopicTitleBn(h.topicTitleBn, h.topicId),
                    difficulty = h.difficulty,
                    totalAttempted = attempted,
                    totalCorrect = h.correctCount,
                    totalWrong = h.wrongCount,
                    bestStreak = h.bestStreak,
                    totalTimeSeconds = max(0, h.durationSeconds),
                    lastPracticedAt = h.timestamp
                )
            } else {
                val newAttempted = existing.totalAttempted + attempted
                val newCorrect = existing.totalCorrect + h.correctCount
                val newWrong = existing.totalWrong + h.wrongCount
                map[key] = existing.copy(
                    totalAttempted = newAttempted,
                    totalCorrect = newCorrect,
                    totalWrong = newWrong,
                    bestStreak = max(existing.bestStreak, h.bestStreak),
                    totalTimeSeconds = existing.totalTimeSeconds + max(0, h.durationSeconds),
                    lastPracticedAt = max(existing.lastPracticedAt, h.timestamp)
                )
            }
        }
        windowedExams.forEach { exam ->
            val refTs = exam.completedAt ?: exam.updatedAt
            val alreadyInHistory = quizHistoryTimestamps.any { kotlin.math.abs(it - refTs) <= 2500L }
            if (!alreadyInHistory) {
                val topicId = exam.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" }
                val key = "QUIZ:$topicId"
                val attempted = exam.correctCount + exam.wrongCount
                if (attempted > 0) {
                    val existing = map[key]
                    if (existing == null) {
                        map[key] = TopicAnalyticsEntity(
                            analyticsKey = key,
                            module = "QUIZ",
                            topicId = topicId,
                            topicTitleBn = normalizeTopicTitleBn(exam.examTitleBn, topicId),
                            difficulty = exam.difficulty,
                            totalAttempted = attempted,
                            totalCorrect = exam.correctCount,
                            totalWrong = exam.wrongCount,
                            bestStreak = exam.correctCount,
                            totalTimeSeconds = max(0, exam.elapsedSeconds),
                            lastPracticedAt = refTs
                        )
                    } else {
                        map[key] = existing.copy(
                            totalAttempted = existing.totalAttempted + attempted,
                            totalCorrect = existing.totalCorrect + exam.correctCount,
                            totalWrong = existing.totalWrong + exam.wrongCount,
                            bestStreak = max(existing.bestStreak, exam.correctCount),
                            totalTimeSeconds = existing.totalTimeSeconds + max(0, exam.elapsedSeconds),
                            lastPracticedAt = max(existing.lastPracticedAt, refTs)
                        )
                    }
                }
            }
        }
        windowedLegacyQuizzes.forEach { q ->
            val key = "QUIZ:${q.topicId}"
            val correct = minOf(q.score, q.totalQuestions)
            val wrong = max(0, q.totalQuestions - correct)
            val existing = map[key]
            if (existing == null) {
                map[key] = TopicAnalyticsEntity(
                    analyticsKey = key,
                    module = "QUIZ",
                    topicId = q.topicId,
                    topicTitleBn = normalizeTopicTitleBn(q.topicTitleBn, q.topicId),
                    difficulty = "MEDIUM",
                    totalAttempted = q.totalQuestions,
                    totalCorrect = correct,
                    totalWrong = wrong,
                    bestStreak = correct,
                    totalTimeSeconds = 120,
                    lastPracticedAt = q.timestamp
                )
            } else if (quizHistoryTimestamps.none { kotlin.math.abs(it - q.timestamp) <= 2500L }) {
                map[key] = existing.copy(
                    totalAttempted = existing.totalAttempted + q.totalQuestions,
                    totalCorrect = existing.totalCorrect + correct,
                    totalWrong = existing.totalWrong + wrong,
                    bestStreak = max(existing.bestStreak, correct),
                    totalTimeSeconds = existing.totalTimeSeconds + 120,
                    lastPracticedAt = max(existing.lastPracticedAt, q.timestamp)
                )
            }
        }
        return map.values.toList()
    }

    fun computeSeparatedKpis(
        filteredExams: List<ExamAttemptEntity>,
        filteredLegacyQuizScores: List<QuizScoreEntity>,
        filteredPractice: List<PracticeHistoryEntity>,
        learnedItemsCount: Int
    ): SeparatedKpiSummary {
        // Dedup legacy quiz_scores that were created alongside ExamAttemptEntity (within 2.5 seconds)
        // and exclude non-exam practice/mental-math/book/visual entries from Exam KPIs
        val examTimestamps = filteredExams.map { it.completedAt ?: it.updatedAt }
        val standaloneLegacyQuizzes = filteredLegacyQuizScores.filter { legacy ->
            !legacy.topicId.startsWith("PRACTICE_") &&
                !legacy.topicId.startsWith("MENTAL_MATH_") &&
                !legacy.topicId.startsWith("BOOK_") &&
                !legacy.topicId.startsWith("VISUAL_") &&
                legacy.topicId != "NUMBER_SYSTEM" &&
                examTimestamps.none { kotlin.math.abs(it - legacy.timestamp) <= 2500L }
        }

        val totalExams = filteredExams.size + standaloneLegacyQuizzes.size
        val examCorrect = filteredExams.sumOf { it.correctCount } +
            standaloneLegacyQuizzes.sumOf { minOf(it.score, it.totalQuestions) }
        val examWrong = filteredExams.sumOf { it.wrongCount } +
            standaloneLegacyQuizzes.sumOf { max(0, it.totalQuestions - minOf(it.score, it.totalQuestions)) }
        val examAttempted = examCorrect + examWrong
        val examTimeSeconds = filteredExams.sumOf { it.elapsedSeconds } + (standaloneLegacyQuizzes.size * 120)

        val allExamPercentages = filteredExams.map { it.percentage.roundToInt().coerceIn(0, 100) } +
            standaloneLegacyQuizzes.map { it.percentage.coerceIn(0, 100) }

        val examAvgPct = if (allExamPercentages.isNotEmpty()) allExamPercentages.average().roundToInt() else 0
        val examBestPct = allExamPercentages.maxOrNull() ?: 0
        val examLowestPct = allExamPercentages.minOrNull() ?: 0
        val examAccuracyPct = if (examAttempted > 0) ((examCorrect * 100.0) / examAttempted).roundToInt() else 0

        val totalPracticeSessions = filteredPractice.size
        val practiceCorrect = filteredPractice.sumOf { it.correctCount }
        val practiceWrong = filteredPractice.sumOf { it.wrongCount }
        val practiceAttempted = practiceCorrect + practiceWrong
        val practiceAccuracyPct = if (practiceAttempted > 0) {
            ((practiceCorrect * 100.0) / practiceAttempted).roundToInt()
        } else 0
        val practiceBestStreak = filteredPractice.maxOfOrNull { it.bestStreak } ?: 0
        val practiceTimeSeconds = filteredPractice.sumOf { max(0, it.durationSeconds) }

        val combinedAttempted = examAttempted + practiceAttempted
        val combinedCorrect = examCorrect + practiceCorrect
        val combinedAccuracy = if (combinedAttempted > 0) {
            ((combinedCorrect * 100.0) / combinedAttempted).roundToInt()
        } else 0
        val combinedTimeSeconds = examTimeSeconds + practiceTimeSeconds

        return SeparatedKpiSummary(
            totalExamsCompleted = totalExams,
            examQuestionsAttempted = examAttempted,
            examCorrectAnswers = examCorrect,
            examWrongAnswers = examWrong,
            examAverageScorePercent = examAvgPct,
            examBestScorePercent = examBestPct,
            examLowestScorePercent = examLowestPct,
            examOverallAccuracyPercent = examAccuracyPct,
            examTotalTimeSeconds = examTimeSeconds,
            totalPracticeSessions = totalPracticeSessions,
            practiceQuestionsAttempted = practiceAttempted,
            practiceCorrectAnswers = practiceCorrect,
            practiceWrongAnswers = practiceWrong,
            practiceAccuracyPercent = practiceAccuracyPct,
            practiceBestStreak = practiceBestStreak,
            practiceTotalTimeSeconds = practiceTimeSeconds,
            combinedQuestionsAttempted = combinedAttempted,
            combinedCorrectAnswers = combinedCorrect,
            combinedAccuracyPercent = combinedAccuracy,
            combinedStudyTimeSeconds = combinedTimeSeconds,
            learnedItemsCount = learnedItemsCount
        )
    }

    /**
     * Confidence-weighted Topic Mastery evaluation.
     * Never classifies a topic as MASTERED from 1-4 questions!
     * Requires at least 5 attempted questions to classify as WEAK/DEVELOPING,
     * and at least 10 attempted questions with >= 80% accuracy to classify as MASTERED.
     */
    fun computeTopicMasteryReports(
        topicAnalytics: List<TopicAnalyticsEntity>,
        practiceHistory: List<PracticeHistoryEntity>,
        completedExams: List<ExamAttemptEntity>
    ): List<TopicMasteryReport> {
        if (topicAnalytics.isEmpty()) return emptyList()

        val grouped = topicAnalytics.groupBy { normalizeTopicTitleBn(it.topicTitleBn, it.topicId) }
        val recentHistoryByTopic = practiceHistory.groupBy { normalizeTopicTitleBn(it.topicTitleBn, it.topicId) }

        return grouped.map { (topicTitle, records) ->
            val primaryTopicId = records.first().topicId
            val modules = records.map { it.module }.toSet()
            val totalAttempted = records.sumOf { it.totalAttempted }
            val totalCorrect = records.sumOf { it.totalCorrect }
            val totalWrong = records.sumOf { it.totalWrong }
            val totalTime = records.sumOf { max(0, it.totalTimeSeconds) }
            val lastPracticedAt = records.maxOfOrNull { it.lastPracticedAt } ?: 0L

            val rawAccuracy = if (totalAttempted > 0) {
                ((totalCorrect * 100.0) / totalAttempted).roundToInt().coerceIn(0, 100)
            } else 0

            val avgTimePerQuestion = if (totalAttempted > 0) {
                totalTime.toDouble() / totalAttempted.toDouble()
            } else 0.0

            val confidence = when {
                totalAttempted >= 20 -> SampleConfidenceLevel.HIGH_CONFIDENCE
                totalAttempted >= 10 -> SampleConfidenceLevel.MODERATE_CONFIDENCE
                totalAttempted >= 5 -> SampleConfidenceLevel.LOW_CONFIDENCE
                else -> SampleConfidenceLevel.INSUFFICIENT_DATA
            }

            // Bayesian / sample-size damped mastery score so 1/1 != 100% mastery
            val confidenceMultiplier = (totalAttempted.toDouble() / 12.0).coerceIn(0.15, 1.0)
            val speedBonus = when {
                avgTimePerQuestion <= 0.0 -> 0.0
                avgTimePerQuestion <= 12.0 && rawAccuracy >= 75 -> 5.0
                avgTimePerQuestion > 40.0 -> -5.0
                else -> 0.0
            }
            val weightedMastery = ((rawAccuracy * confidenceMultiplier) + speedBonus)
                .roundToInt()
                .coerceIn(0, 100)

            val status = when {
                totalAttempted == 0 -> TopicMasteryStatus.UNTESTED
                totalAttempted < 5 -> TopicMasteryStatus.NEEDS_MORE_DATA
                rawAccuracy < 60 -> TopicMasteryStatus.WEAK
                totalAttempted >= 10 && rawAccuracy >= 80 -> TopicMasteryStatus.MASTERED
                else -> TopicMasteryStatus.DEVELOPING
            }

            // Compute trend from recent sessions for this topic
            val topicSessions = recentHistoryByTopic[topicTitle].orEmpty().sortedBy { it.timestamp }
            val trend = if (topicSessions.size >= 2) {
                val latestAcc = topicSessions.last().accuracyPercent
                val prevAcc = topicSessions.dropLast(1).map { it.accuracyPercent }.average()
                when {
                    latestAcc - prevAcc >= 6.0 -> TopicTrendDirection.IMPROVING
                    prevAcc - latestAcc >= 6.0 -> TopicTrendDirection.DECLINING
                    else -> TopicTrendDirection.STABLE
                }
            } else {
                TopicTrendDirection.STABLE
            }

            val explanationBn = when (status) {
                TopicMasteryStatus.UNTESTED ->
                    "এই টপিকে এখনো কোনো প্রশ্ন অনুশীলন করা হয়নি।"
                TopicMasteryStatus.NEEDS_MORE_DATA ->
                    "এখন পর্যন্ত মাত্র ${totalAttempted.toBengaliDigits()}টি প্রশ্ন সমাধান করা হয়েছে (নির্ভুলতা ${rawAccuracy.toBengaliDigits()}%)। নির্ভরযোগ্য মূল্যায়নের জন্য কমপক্ষে ৫–১০টি প্রশ্ন অনুশীলন করুন।"
                TopicMasteryStatus.WEAK ->
                    "${totalAttempted.toBengaliDigits()}টি প্রশ্নের মধ্যে নির্ভুলতা ${rawAccuracy.toBengaliDigits()}% (<৬০%)। মৌলিক ধারণা ও সূত্র রিভিশন দেওয়া জরুরি।"
                TopicMasteryStatus.DEVELOPING ->
                    "${totalAttempted.toBengaliDigits()}টি প্রশ্নে নির্ভুলতা ${rawAccuracy.toBengaliDigits()}%। দক্ষ (Mastered) পর্যায়ে পৌঁছাতে ১০+ প্রশ্নে ৮০%+ নির্ভুলতা অর্জন করুন।"
                TopicMasteryStatus.MASTERED ->
                    "${totalAttempted.toBengaliDigits()}টি প্রশ্নে ${rawAccuracy.toBengaliDigits()}% নির্ভুলতার সাথে আপনি এই অধ্যায়ে দক্ষতা অর্জন করেছেন (${confidence.titleBn})।"
            }

            TopicMasteryReport(
                topicId = primaryTopicId,
                topicTitleBn = topicTitle,
                modulesInvolved = modules,
                totalAttempted = totalAttempted,
                totalCorrect = totalCorrect,
                totalWrong = totalWrong,
                accuracyPercent = rawAccuracy,
                totalTimeSeconds = totalTime,
                avgTimePerQuestionSeconds = avgTimePerQuestion,
                masteryScore = weightedMastery,
                confidenceLevel = confidence,
                masteryStatus = status,
                trend = trend,
                explanationBn = explanationBn,
                lastPracticedAt = lastPracticedAt
            )
        }.sortedWith(
            compareBy<TopicMasteryReport> {
                when (it.masteryStatus) {
                    TopicMasteryStatus.WEAK -> 0
                    TopicMasteryStatus.DEVELOPING -> 1
                    TopicMasteryStatus.NEEDS_MORE_DATA -> 2
                    TopicMasteryStatus.MASTERED -> 3
                    TopicMasteryStatus.UNTESTED -> 4
                }
            }.thenBy { it.accuracyPercent }
        )
    }

    private fun parseDateKeyToEpochDay(dateKey: String): Long? {
        val parts = dateKey.split("-")
        if (parts.size != 3) return null
        val year = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val day = parts[2].toIntOrNull() ?: return null
        val utcCal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        utcCal.clear()
        utcCal.set(year, month - 1, day, 12, 0, 0)
        return utcCal.timeInMillis / 86_400_000L
    }

    fun computeDailyGoalsAndStreaks(
        dailyActivities: List<DailyStudyActivityEntity>,
        practiceHistory: List<PracticeHistoryEntity>,
        completedExams: List<ExamAttemptEntity>,
        quizScores: List<QuizScoreEntity>,
        nowMs: Long = System.currentTimeMillis()
    ): DailyGoalProgressReport {
        val todayKey = MathRepository.todayDateKey(nowMs)
        val explicitDateKeys = dailyActivities.map { it.dateKey }.toSet()
        val activityMap = dailyActivities.associateBy { it.dateKey }.toMutableMap()

        // Backfill and accumulate all sessions on dates that do not have an explicit daily_study_activity row
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val backfilledTimestampsByDate = mutableMapOf<String, MutableList<Long>>()

        practiceHistory.forEach { h ->
            val key = sdf.format(Date(h.timestamp))
            if (key !in explicitDateKeys) {
                val attempted = h.correctCount + h.wrongCount
                val dur = max(60, h.durationSeconds)
                val existing = activityMap[key]
                if (existing == null) {
                    activityMap[key] = DailyStudyActivityEntity(
                        dateKey = key,
                        questionsAttempted = attempted,
                        correctCount = h.correctCount,
                        wrongCount = h.wrongCount,
                        studyTimeSeconds = dur,
                        examsCompleted = if (h.module == "QUIZ") 1 else 0,
                        practiceSessionsCompleted = if (h.module != "QUIZ") 1 else 0
                    )
                } else {
                    activityMap[key] = existing.copy(
                        questionsAttempted = existing.questionsAttempted + attempted,
                        correctCount = existing.correctCount + h.correctCount,
                        wrongCount = existing.wrongCount + h.wrongCount,
                        studyTimeSeconds = existing.studyTimeSeconds + dur,
                        examsCompleted = existing.examsCompleted + if (h.module == "QUIZ") 1 else 0,
                        practiceSessionsCompleted = existing.practiceSessionsCompleted + if (h.module != "QUIZ") 1 else 0
                    )
                }
                backfilledTimestampsByDate.getOrPut(key) { mutableListOf() }.add(h.timestamp)
            }
        }
        completedExams.forEach { exam ->
            val refTs = exam.completedAt ?: exam.updatedAt
            val key = sdf.format(Date(refTs))
            if (key !in explicitDateKeys) {
                val alreadyCountedTs = backfilledTimestampsByDate[key].orEmpty()
                if (alreadyCountedTs.none { kotlin.math.abs(it - refTs) <= 2500L }) {
                    val attempted = exam.correctCount + exam.wrongCount
                    val dur = max(60, exam.elapsedSeconds)
                    val existing = activityMap[key]
                    if (existing == null) {
                        activityMap[key] = DailyStudyActivityEntity(
                            dateKey = key,
                            questionsAttempted = attempted,
                            correctCount = exam.correctCount,
                            wrongCount = exam.wrongCount,
                            studyTimeSeconds = dur,
                            examsCompleted = 1,
                            practiceSessionsCompleted = 0
                        )
                    } else {
                        activityMap[key] = existing.copy(
                            questionsAttempted = existing.questionsAttempted + attempted,
                            correctCount = existing.correctCount + exam.correctCount,
                            wrongCount = existing.wrongCount + exam.wrongCount,
                            studyTimeSeconds = existing.studyTimeSeconds + dur,
                            examsCompleted = existing.examsCompleted + 1
                        )
                    }
                    backfilledTimestampsByDate.getOrPut(key) { mutableListOf() }.add(refTs)
                }
            }
        }
        quizScores.forEach { q ->
            val key = sdf.format(Date(q.timestamp))
            if (key !in explicitDateKeys &&
                !q.topicId.startsWith("PRACTICE_") &&
                !q.topicId.startsWith("MENTAL_MATH_") &&
                !q.topicId.startsWith("BOOK_") &&
                !q.topicId.startsWith("VISUAL_") &&
                q.topicId != "NUMBER_SYSTEM"
            ) {
                val alreadyCountedTs = backfilledTimestampsByDate[key].orEmpty()
                if (alreadyCountedTs.none { kotlin.math.abs(it - q.timestamp) <= 2500L }) {
                    val correct = minOf(q.score, q.totalQuestions)
                    val wrong = max(0, q.totalQuestions - correct)
                    val existing = activityMap[key]
                    if (existing == null) {
                        activityMap[key] = DailyStudyActivityEntity(
                            dateKey = key,
                            questionsAttempted = q.totalQuestions,
                            correctCount = correct,
                            wrongCount = wrong,
                            studyTimeSeconds = 120,
                            examsCompleted = 1,
                            practiceSessionsCompleted = 0
                        )
                    } else {
                        activityMap[key] = existing.copy(
                            questionsAttempted = existing.questionsAttempted + q.totalQuestions,
                            correctCount = existing.correctCount + correct,
                            wrongCount = existing.wrongCount + wrong,
                            studyTimeSeconds = existing.studyTimeSeconds + 120,
                            examsCompleted = existing.examsCompleted + 1
                        )
                    }
                    backfilledTimestampsByDate.getOrPut(key) { mutableListOf() }.add(q.timestamp)
                }
            }
        }

        val todayEntity = activityMap[todayKey]
        val latestConfigured = dailyActivities.maxByOrNull { it.updatedAt }
        val targetQuestions = todayEntity?.dailyTargetQuestions ?: latestConfigured?.dailyTargetQuestions ?: 20
        val targetMinutes = todayEntity?.dailyTargetMinutes ?: latestConfigured?.dailyTargetMinutes ?: 15

        val todayQuestions = todayEntity?.questionsAttempted ?: 0
        val todayStudySec = todayEntity?.studyTimeSeconds ?: 0
        val todayMinutes = if (todayStudySec > 0) max(1, (todayStudySec / 60.0).roundToInt()) else 0

        val qProgress = (todayQuestions.toFloat() / max(1, targetQuestions).toFloat()).coerceIn(0f, 1f)
        val tProgress = (todayMinutes.toFloat() / max(1, targetMinutes).toFloat()).coerceIn(0f, 1f)
        val isTodayGoalMet = todayQuestions >= targetQuestions || todayMinutes >= targetMinutes

        // Compute streak across calendar days
        val cal = Calendar.getInstance()
        val last7Points = mutableListOf<DailyActivityPoint>()
        var activeLast7 = 0
        var activeLast30 = 0

        for (offset in 29 downTo 0) {
            cal.timeInMillis = nowMs
            cal.add(Calendar.DAY_OF_YEAR, -offset)
            val key = sdf.format(cal.time)
            val entity = activityMap[key]
            val qCount = entity?.questionsAttempted ?: 0
            val cCount = entity?.correctCount ?: 0
            val dayStudySec = entity?.studyTimeSeconds ?: 0
            val mins = if (dayStudySec > 0) max(1, (dayStudySec / 60.0).roundToInt()) else 0
            val isActive = qCount > 0 || dayStudySec > 0
            if (isActive) {
                activeLast30++
                if (offset < 7) activeLast7++
            }
            if (offset < 7) {
                val dayNumBn = cal.get(Calendar.DAY_OF_MONTH).toBengaliDigits()
                val acc = if (qCount > 0) ((cCount * 100.0) / qCount).roundToInt() else 0
                val dayGoalMet = qCount >= (entity?.dailyTargetQuestions ?: targetQuestions) ||
                    mins >= (entity?.dailyTargetMinutes ?: targetMinutes)
                last7Points.add(
                    DailyActivityPoint(
                        dateKey = key,
                        shortLabelBn = dayNumBn,
                        questionsAttempted = qCount,
                        accuracyPercent = acc,
                        studyMinutes = mins,
                        goalMet = dayGoalMet
                    )
                )
            }
        }

        // Unrestricted, DST-safe current & best consecutive active day streak across full history
        val activeEpochDays = activityMap.values
            .filter { it.questionsAttempted > 0 || it.studyTimeSeconds > 0 }
            .mapNotNull { parseDateKeyToEpochDay(it.dateKey) }
            .distinct()
            .sorted()
        val activeDaySet = activeEpochDays.toHashSet()
        val todayEpochDay = parseDateKeyToEpochDay(todayKey)

        var currentStreak = 0
        if (todayEpochDay != null) {
            var cursor: Long? = when {
                activeDaySet.contains(todayEpochDay) -> todayEpochDay
                activeDaySet.contains(todayEpochDay - 1L) -> todayEpochDay - 1L
                else -> null
            }
            while (cursor != null && activeDaySet.contains(cursor)) {
                currentStreak++
                cursor -= 1L
            }
        }

        var bestStreak = currentStreak
        var running = 0
        var prevDay: Long? = null
        for (day in activeEpochDays) {
            if (prevDay != null && day == prevDay + 1L) {
                running++
            } else {
                running = 1
            }
            if (running > bestStreak) {
                bestStreak = running
            }
            prevDay = day
        }

        val weeklyConsistency = ((activeLast7 / 7.0) * 100.0).roundToInt().coerceIn(0, 100)

        return DailyGoalProgressReport(
            todayDateKey = todayKey,
            todayQuestionsAttempted = todayQuestions,
            dailyTargetQuestions = targetQuestions,
            questionsProgressFraction = qProgress,
            todayStudyMinutes = todayMinutes,
            dailyTargetMinutes = targetMinutes,
            timeProgressFraction = tProgress,
            isTodayGoalMet = isTodayGoalMet,
            currentStreakDays = currentStreak,
            bestStreakDays = bestStreak,
            activeDaysLast7 = activeLast7,
            activeDaysLast30 = activeLast30,
            weeklyConsistencyPercent = weeklyConsistency,
            last7DaysActivity = last7Points
        )
    }

    fun generateDeterministicRecommendations(
        activeExam: ExamAttemptEntity?,
        kpi: SeparatedKpiSummary,
        goalReport: DailyGoalProgressReport,
        masteryList: List<TopicMasteryReport>,
        completedExams: List<ExamAttemptEntity>
    ): List<LearningRecommendation> {
        val recs = mutableListOf<LearningRecommendation>()

        // Rule 1: Unfinished Active Exam
        if (activeExam != null) {
            recs.add(
                LearningRecommendation(
                    id = "rec_resume_exam",
                    priorityRank = 1,
                    badgeBn = "চলমান পরীক্ষা",
                    titleBn = "অসমাপ্ত পরীক্ষাটি সম্পন্ন করুন: ${activeExam.examTitleBn}",
                    reasonBn = "আপনার একটি পরীক্ষা প্রশ্ন ${(activeExam.currentQuestionIndex + 1).toBengaliDigits()}/${activeExam.totalQuestions.toBengaliDigits()} অবস্থায় সংরক্ষিত আছে।",
                    actionLabelBn = "পরীক্ষা পুনরায় শুরু করুন",
                    actionType = RecommendationActionType.RESUME_ACTIVE_EXAM,
                    targetTopicId = activeExam.attemptId
                )
            )
        }

        // Rule 2: Weakest Topic Remediation
        val weakestTopic = masteryList.firstOrNull { it.masteryStatus == TopicMasteryStatus.WEAK }
        if (weakestTopic != null) {
            recs.add(
                LearningRecommendation(
                    id = "rec_weak_${weakestTopic.topicId}",
                    priorityRank = 2,
                    badgeBn = "দুর্বল অধ্যায় (অগ্রাধিকার)",
                    titleBn = "${weakestTopic.topicTitleBn} অধ্যায়ে নির্ভুলতা বাড়ান",
                    reasonBn = "এই অধ্যায়ে আপনার নির্ভুলতা মাত্র ${weakestTopic.accuracyPercent.toBengaliDigits()}% (${weakestTopic.totalAttempted.toBengaliDigits()}টি প্রশ্নে)। সূত্র ও অনুশীলন ল্যাবে রিভিশন দিন।",
                    actionLabelBn = "টপিক অনুশীলন শুরু করুন",
                    actionType = RecommendationActionType.OPEN_QUIZ_TOPIC,
                    targetTopicId = weakestTopic.topicId
                )
            )
        }

        // Rule 3: Low-Sample Confidence Completion
        val lowSample = masteryList.firstOrNull { it.masteryStatus == TopicMasteryStatus.NEEDS_MORE_DATA }
        if (lowSample != null) {
            val remainingForConfidence = max(1, 5 - lowSample.totalAttempted)
            recs.add(
                LearningRecommendation(
                    id = "rec_sample_${lowSample.topicId}",
                    priorityRank = 3,
                    badgeBn = "মূল্যায়ন অসম্পূর্ণ",
                    titleBn = "${lowSample.topicTitleBn}-এর দক্ষতা যাচাই সম্পূর্ণ করুন",
                    reasonBn = "এখন পর্যন্ত ${lowSample.totalAttempted.toBengaliDigits()}টি প্রশ্ন অনুশীলন করেছেন। আরও ${remainingForConfidence.toBengaliDigits()}টি প্রশ্ন সমাধান করলে নির্ভরযোগ্য দক্ষতা স্কোর পাওয়া যাবে।",
                    actionLabelBn = "দ্রুত কুইজ দিন",
                    actionType = RecommendationActionType.OPEN_QUIZ_TOPIC,
                    targetTopicId = lowSample.topicId
                )
            )
        }

        // Rule 4: Daily Study Goal Completion
        if (!goalReport.isTodayGoalMet) {
            val remainingQ = max(1, goalReport.dailyTargetQuestions - goalReport.todayQuestionsAttempted)
            recs.add(
                LearningRecommendation(
                    id = "rec_daily_goal",
                    priorityRank = 4,
                    badgeBn = "দৈনিক লক্ষ্যমাত্রা",
                    titleBn = "আজকের দৈনিক লক্ষ্য পূরণে আর ${remainingQ.toBengaliDigits()}টি প্রশ্ন বাকি",
                    reasonBn = "আজ আপনি ${goalReport.todayQuestionsAttempted.toBengaliDigits()}/${goalReport.dailyTargetQuestions.toBengaliDigits()}টি প্রশ্ন অনুশীলন করেছেন। ধারাবাহিক স্ট্রিক বজায় রাখতে একটি সংক্ষিপ্ত সেশন সম্পন্ন করুন।",
                    actionLabelBn = "প্র্যাকটিস ল্যাবে যান",
                    actionType = RecommendationActionType.OPEN_PRACTICE_LAB
                )
            )
        }

        // Rule 5: BCS Preliminary Preparation & Shortcut Mastery
        recs.add(
            LearningRecommendation(
                id = "rec_bcs_prep",
                priorityRank = 5,
                badgeBn = "বিসিএস প্রস্তুতি",
                titleBn = "বিসিএস গাণিতিক যুক্তি ও শর্টকাট কৌশল অনুশীলন করুন",
                reasonBn = "বিগত বিসিএস প্রশ্ন (৩৫তম–৪৫তম), শর্টকাট টেকনিক এবং পিএসসি সিলেবাসভিত্তিক মক টেস্ট দিয়ে প্রতিযোগিতামূলক প্রস্তুতি জোরদার করুন।",
                actionLabelBn = "বিসিএস প্রস্তুতি খুলুন",
                actionType = RecommendationActionType.OPEN_BCS_PREPARATION
            )
        )

        // Rule 6: First Full Mock Exam if user hasn't taken any exam yet
        if (kpi.totalExamsCompleted == 0) {
            recs.add(
                LearningRecommendation(
                    id = "rec_first_exam",
                    priorityRank = 2,
                    badgeBn = "প্রথম পরীক্ষা",
                    titleBn = "আপনার প্রথম পূর্ণাঙ্গ মূল্যায়ন পরীক্ষায় অংশ নিন",
                    reasonBn = "আপনার সামগ্রিক গাণিতিক ভিত্তি ও অধ্যায়ভিত্তিক শক্তিমত্তা নির্ণয়ের জন্য ১০ প্রশ্নের একটি দ্রুত পরীক্ষা (Quick Test) দিন।",
                    actionLabelBn = "পরীক্ষা কেন্দ্রে যান",
                    actionType = RecommendationActionType.OPEN_EXAM_PRESET,
                    targetTopicId = ExamPresetType.QUICK_TEST.id
                )
            )
        }

        return recs.sortedBy { it.priorityRank }.take(5)
    }
}
