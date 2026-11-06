package com.example.math

import com.example.data.model.ExamAttemptEntity
import com.example.data.repository.TopicExamStat
import kotlin.math.max
import kotlin.math.roundToInt

enum class ExamPresetType(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val defaultQuestions: Int,
    val defaultDurationMinutes: Int,
    val defaultMarksPerCorrect: Double = 1.0,
    val defaultNegativePerWrong: Double = 0.0,
    val defaultImmediateFeedback: Boolean = false
) {
    QUICK_TEST(
        id = "QUICK_TEST",
        titleBn = "দ্রুত পরীক্ষা (Quick Test)",
        subtitleBn = "১০টি প্রশ্ন • ৫ মিনিট • দ্রুত মূল্যায়ন",
        defaultQuestions = 10,
        defaultDurationMinutes = 5
    ),
    TOPIC_TEST(
        id = "TOPIC_TEST",
        titleBn = "টপিকভিত্তিক পরীক্ষা",
        subtitleBn = "নির্বাচিত টপিকের ওপর ১৫টি প্রশ্ন • ১০ মিনিট",
        defaultQuestions = 15,
        defaultDurationMinutes = 10
    ),
    MIXED_TEST(
        id = "MIXED_TEST",
        titleBn = "মিশ্র বিষয়ের পরীক্ষা",
        subtitleBn = "একাধিক অধ্যায়ের সমন্বয়ে ২০টি প্রশ্ন • ১৫ মিনিট",
        defaultQuestions = 20,
        defaultDurationMinutes = 15
    ),
    TIMED_MOCK(
        id = "TIMED_MOCK",
        titleBn = "টাইমড মক টেস্ট",
        subtitleBn = "২৫টি প্রশ্ন • ১৫ মিনিট • প্রতিযোগিতামূলক প্রস্তুতি",
        defaultQuestions = 25,
        defaultDurationMinutes = 15
    ),
    FULL_LENGTH_MOCK(
        id = "FULL_LENGTH_MOCK",
        titleBn = "পূর্ণাঙ্গ মক পরীক্ষা (Full Mock)",
        subtitleBn = "সকল অধ্যায় থেকে ৩০টি প্রশ্ন • ২৫ মিনিট",
        defaultQuestions = 30,
        defaultDurationMinutes = 25
    ),
    BCS_PRELIMINARY_MOCK(
        id = "BCS_PRELIMINARY_MOCK",
        titleBn = "বিসিএস প্রিলিমিনারি গাণিতিক যুক্তি মক",
        subtitleBn = "৫০তম বিসিএস পিএসসি সিলেবাস (২০ নম্বর) • ২০টি প্রশ্ন • অ্যাপ সেকশন মক প্রিসেট",
        defaultQuestions = 20,
        defaultDurationMinutes = 20,
        defaultMarksPerCorrect = 1.0,
        defaultNegativePerWrong = 0.0
    ),
    CUSTOM_EXAM(
        id = "CUSTOM_EXAM",
        titleBn = "কাস্টম পরীক্ষা (Custom Exam)",
        subtitleBn = "নিজের পছন্দমতো টপিক, সময়, প্রশ্ন ও নেগেটিভ মার্কিং নির্ধারণ",
        defaultQuestions = 15,
        defaultDurationMinutes = 12
    ),
    PRACTICE_EXAM(
        id = "PRACTICE_EXAM",
        titleBn = "অনুশীলন পরীক্ষা (Practice Mode)",
        subtitleBn = "সময়সীমাহীন • প্রতিটি প্রশ্নের সাথে তাৎক্ষণিক ব্যাখ্যা",
        defaultQuestions = 10,
        defaultDurationMinutes = 0,
        defaultImmediateFeedback = true
    ),
    REVIEW_INCORRECT(
        id = "REVIEW_INCORRECT",
        titleBn = "ভুল উত্তরের পুনঃপরীক্ষা",
        subtitleBn = "পূর্বে ভুল করা প্রশ্নগুলোর বিশেষ রিভিশন পরীক্ষা",
        defaultQuestions = 10,
        defaultDurationMinutes = 8
    )
}

enum class QuestionPaletteState(val titleBn: String) {
    NOT_VISITED("অদেখা"),
    UNANSWERED("উত্তর দেওয়া হয়নি"),
    ANSWERED("উত্তর দেওয়া হয়েছে"),
    MARKED_FOR_REVIEW("রিভিউর জন্য চিহ্নিত"),
    ANSWERED_AND_MARKED("উত্তরসহ রিভিউ চিহ্নিত")
}

enum class ExamHistoryFilter(val titleBn: String) {
    ALL("সকল পরীক্ষা"),
    MOCK_ONLY("মক টেস্ট"),
    BCS_ONLY("বিসিএস মক"),
    TOPIC_ONLY("টপিক পরীক্ষা"),
    HIGH_SCORE("৮০%+ স্কোর")
}

enum class ExamHistorySort(val titleBn: String) {
    NEWEST_FIRST("সর্বশেষ আগে"),
    HIGHEST_SCORE("সর্বোচ্চ স্কোর"),
    HIGHEST_ACCURACY("সর্বোচ্চ নির্ভুলতা")
}

data class ExamConfig(
    val preset: ExamPresetType = ExamPresetType.QUICK_TEST,
    val selectedTopics: Set<QuizTopic> = setOf(QuizTopic.MIXED_CHALLENGE),
    val difficulty: PracticeDifficulty = PracticeDifficulty.MEDIUM,
    val questionType: QuizQuestionType = QuizQuestionType.MCQ,
    val questionCount: Int = 10,
    val durationMinutes: Int = 5,
    val marksPerCorrect: Double = 1.0,
    val negativeMarksPerWrong: Double = 0.0, // Off by default per user requirement
    val randomizeQuestions: Boolean = true,
    val randomizeOptions: Boolean = true,
    val immediateFeedback: Boolean = false
) {
    val durationSeconds: Int get() = max(0, durationMinutes * 60)

    fun toExamTitleBn(): String {
        return when {
            preset == ExamPresetType.BCS_PRELIMINARY_MOCK -> "বিসিএস গাণিতিক যুক্তি মক পরীক্ষা"
            selectedTopics.size == 1 -> "${selectedTopics.first().titleBn} (${preset.titleBn})"
            selectedTopics.size > 1 -> "বহু-বিষয়ক পরীক্ষা (${selectedTopics.size.toBengaliDigits()}টি টপিক)"
            else -> preset.titleBn
        }
    }
}

data class TopicScoreBreakdown(
    val topicId: String,
    val topicTitleBn: String,
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val accuracyPercent: Double,
    val timeSpentSeconds: Int
)

data class DifficultyScoreBreakdown(
    val difficulty: PracticeDifficulty,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val accuracyPercent: Double
)

data class ExamDetailedResult(
    val attemptId: String,
    val examTitleBn: String,
    val examType: String,
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val markedForReviewCount: Int,
    val marksPerCorrect: Double,
    val negativeMarksPerWrong: Double,
    val totalPossibleMarks: Double,
    val earnedPositiveMarks: Double,
    val negativeMarksDeducted: Double,
    val finalScore: Double,
    val percentage: Double,
    val accuracyPercent: Double,
    val timeSpentSeconds: Int,
    val durationLimitSeconds: Int,
    val avgTimePerQuestionSeconds: Double,
    val topicBreakdown: List<TopicScoreBreakdown>,
    val difficultyBreakdown: List<DifficultyScoreBreakdown>,
    val weakTopicTitlesBn: List<String>,
    val strongTopicTitlesBn: List<String>,
    val performanceGradeBn: String,
    val recommendationSummaryBn: String,
    val completedAt: Long
)

object ExamScoringEngine {

    fun evaluateExam(
        attemptId: String,
        examTitleBn: String,
        examType: String,
        questions: List<QuizQuestion>,
        selectedAnswers: Map<Int, Int>,
        markedForReview: Set<Int>,
        questionTimeSeconds: Map<Int, Int>,
        marksPerCorrect: Double,
        negativeMarksPerWrong: Double,
        timeSpentSeconds: Int,
        durationLimitSeconds: Int,
        completedAt: Long = System.currentTimeMillis()
    ): ExamDetailedResult {
        val totalQ = questions.size
        var correct = 0
        var wrong = 0
        var unanswered = 0

        val topicGroupMap = linkedMapOf<String, MutableList<Pair<Int, QuizQuestion>>>()
        val diffGroupMap = linkedMapOf<PracticeDifficulty, MutableList<Pair<Int, QuizQuestion>>>()

        questions.forEachIndexed { index, q ->
            val chosen = selectedAnswers[index]
            if (chosen == null || chosen < 0) {
                unanswered++
            } else if (chosen == q.correctIndex) {
                correct++
            } else {
                wrong++
            }
            topicGroupMap.getOrPut(q.topicTitleBn) { mutableListOf() }.add(index to q)
            diffGroupMap.getOrPut(q.difficulty) { mutableListOf() }.add(index to q)
        }

        val attempted = correct + wrong
        val totalPossibleMarks = totalQ * marksPerCorrect
        val earnedPositive = correct * marksPerCorrect
        val negativeDeducted = wrong * max(0.0, negativeMarksPerWrong)
        val rawFinalScore = earnedPositive - negativeDeducted
        val clampedForPct = max(0.0, rawFinalScore)
        val percentage = if (totalPossibleMarks > 0.0) {
            (clampedForPct / totalPossibleMarks) * 100.0
        } else 0.0
        val accuracyPercent = if (attempted > 0) {
            (correct.toDouble() / attempted.toDouble()) * 100.0
        } else 0.0

        val safeTimeSpent = max(1, timeSpentSeconds)
        val avgTime = if (totalQ > 0) safeTimeSpent.toDouble() / totalQ.toDouble() else 0.0

        val topicBreakdown = topicGroupMap.map { (topicTitle, indexedList) ->
            var tCorrect = 0
            var tWrong = 0
            var tUnanswered = 0
            var tTime = 0
            indexedList.forEach { (idx, q) ->
                val ans = selectedAnswers[idx]
                if (ans == null || ans < 0) tUnanswered++
                else if (ans == q.correctIndex) tCorrect++
                else tWrong++
                tTime += (questionTimeSeconds[idx] ?: (safeTimeSpent / max(1, totalQ)))
            }
            val tAttempted = tCorrect + tWrong
            val tAcc = if (tAttempted > 0) (tCorrect * 100.0) / tAttempted else 0.0
            val resolvedTopicId = QuizTopic.values().firstOrNull { it.titleBn == topicTitle }?.id ?: topicTitle
            TopicScoreBreakdown(
                topicId = resolvedTopicId,
                topicTitleBn = topicTitle,
                totalQuestions = indexedList.size,
                attemptedCount = tAttempted,
                correctCount = tCorrect,
                wrongCount = tWrong,
                unansweredCount = tUnanswered,
                accuracyPercent = tAcc,
                timeSpentSeconds = max(1, tTime)
            )
        }

        val difficultyBreakdown = diffGroupMap.map { (diff, indexedList) ->
            var dCorrect = 0
            var dWrong = 0
            var dUnanswered = 0
            indexedList.forEach { (idx, q) ->
                val ans = selectedAnswers[idx]
                if (ans == null || ans < 0) dUnanswered++
                else if (ans == q.correctIndex) dCorrect++
                else dWrong++
            }
            val dAttempted = dCorrect + dWrong
            val dAcc = if (dAttempted > 0) (dCorrect * 100.0) / dAttempted else 0.0
            DifficultyScoreBreakdown(
                difficulty = diff,
                totalQuestions = indexedList.size,
                correctCount = dCorrect,
                wrongCount = dWrong,
                unansweredCount = dUnanswered,
                accuracyPercent = dAcc
            )
        }

        val weakTopics = topicBreakdown
            .filter { it.accuracyPercent < 65.0 || (it.correctCount * 100.0 / max(1, it.totalQuestions)) < 60.0 }
            .map { it.topicTitleBn }

        val strongTopics = topicBreakdown
            .filter { it.attemptedCount > 0 && it.accuracyPercent >= 80.0 && (it.correctCount * 100.0 / max(1, it.totalQuestions)) >= 70.0 }
            .map { it.topicTitleBn }

        val gradeBn = when {
            percentage >= 85.0 -> "অসাধারণ (A+)"
            percentage >= 75.0 -> "খুব ভালো (A)"
            percentage >= 60.0 -> "সন্তোষজনক (B)"
            percentage >= 45.0 -> "উন্নতি প্রয়োজন (C)"
            else -> "পুনরায় অনুশীলন আবশ্যক (D)"
        }

        val recommendationBn = when {
            weakTopics.isNotEmpty() ->
                "আপনার দুর্বল ক্ষেত্র: ${weakTopics.joinToString(", ")}। এই অধ্যায়গুলোর সূত্র ও অনুশীলন ল্যাবে আরও অনুশীলন করার পরামর্শ দেওয়া হচ্ছে।"
            unanswered > totalQ / 3 ->
                "আপনি ${unanswered.toBengaliDigits()}টি প্রশ্নের উত্তর দেননি। সময় ব্যবস্থাপনা উন্নত করতে টাইমড মক টেস্ট অনুশীলন করুন।"
            negativeDeducted > 0.0 && wrong >= 3 ->
                "নেগেটিভ মার্কিংয়ের কারণে আপনার ${String.format("%.2f", negativeDeducted)} নম্বর কাটা গেছে। অনিশ্চিত প্রশ্নে অনুমান না করে নিশ্চিত প্রশ্নে মনোযোগ দিন।"
            else ->
                "চমৎকার পারফরম্যান্স! ধারাবাহিকতা বজায় রাখতে কঠিন স্তরের (Hard) পূর্ণাঙ্গ মক পরীক্ষায় অংশ নিন।"
        }

        return ExamDetailedResult(
            attemptId = attemptId,
            examTitleBn = examTitleBn,
            examType = examType,
            totalQuestions = totalQ,
            attemptedCount = attempted,
            correctCount = correct,
            wrongCount = wrong,
            unansweredCount = unanswered,
            markedForReviewCount = markedForReview.size,
            marksPerCorrect = marksPerCorrect,
            negativeMarksPerWrong = negativeMarksPerWrong,
            totalPossibleMarks = totalPossibleMarks,
            earnedPositiveMarks = earnedPositive,
            negativeMarksDeducted = negativeDeducted,
            finalScore = rawFinalScore,
            percentage = percentage,
            accuracyPercent = accuracyPercent,
            timeSpentSeconds = safeTimeSpent,
            durationLimitSeconds = durationLimitSeconds,
            avgTimePerQuestionSeconds = avgTime,
            topicBreakdown = topicBreakdown,
            difficultyBreakdown = difficultyBreakdown,
            weakTopicTitlesBn = weakTopics,
            strongTopicTitlesBn = strongTopics,
            performanceGradeBn = gradeBn,
            recommendationSummaryBn = recommendationBn,
            completedAt = completedAt
        )
    }

    fun computePaletteState(
        questionIndex: Int,
        visitedIndices: Set<Int>,
        selectedAnswers: Map<Int, Int>,
        markedForReview: Set<Int>
    ): QuestionPaletteState {
        val isVisited = visitedIndices.contains(questionIndex)
        val hasAnswer = (selectedAnswers[questionIndex] ?: -1) >= 0
        val isMarked = markedForReview.contains(questionIndex)

        return when {
            hasAnswer && isMarked -> QuestionPaletteState.ANSWERED_AND_MARKED
            isMarked -> QuestionPaletteState.MARKED_FOR_REVIEW
            hasAnswer -> QuestionPaletteState.ANSWERED
            isVisited -> QuestionPaletteState.UNANSWERED
            else -> QuestionPaletteState.NOT_VISITED
        }
    }

    fun toTopicExamStats(breakdown: List<TopicScoreBreakdown>): List<TopicExamStat> {
        return breakdown.map {
            TopicExamStat(
                topicId = it.topicId,
                topicTitleBn = it.topicTitleBn,
                totalQuestions = it.totalQuestions,
                correctCount = it.correctCount,
                wrongCount = it.wrongCount,
                unansweredCount = it.unansweredCount,
                timeSpentSeconds = it.timeSpentSeconds
            )
        }
    }
}

object ExamQuestionBank {

    fun generateExamQuestions(config: ExamConfig): List<QuizQuestion> {
        val targetCount = config.questionCount.coerceIn(5, 60)

        if (config.preset == ExamPresetType.BCS_PRELIMINARY_MOCK) {
            return BcsPreparationRepository.generateBcsExamQuestions(
                count = targetCount,
                difficulty = config.difficulty,
                includePreviousBcs = true
            )
        }

        val topics = if (config.selectedTopics.isEmpty() || config.selectedTopics.contains(QuizTopic.MIXED_CHALLENGE)) {
            QuizTopic.values().filter { it != QuizTopic.MIXED_CHALLENGE }
        } else {
            config.selectedTopics.toList()
        }

        val perTopicCount = max(2, (targetCount / max(1, topics.size)) + 2)
        val combined = mutableListOf<QuizQuestion>()

        topics.forEach { topic ->
            val generated = QuizGenerator.generateQuiz(
                topic = topic,
                questionCount = perTopicCount,
                difficulty = config.difficulty,
                questionType = config.questionType
            )
            combined.addAll(generated)
        }

        val deduplicated = combined
            .distinctBy { it.questionBn }
            .map { ensureUniqueOptions(it) }

        val finalPool = if (config.randomizeQuestions) deduplicated.shuffled() else deduplicated
        val selected = finalPool.take(targetCount).toMutableList()

        // Top up if deduplication left fewer than targetCount
        if (selected.size < targetCount) {
            val extra = QuizGenerator.generateQuiz(
                topic = QuizTopic.MIXED_CHALLENGE,
                questionCount = targetCount - selected.size + 4,
                difficulty = config.difficulty,
                questionType = config.questionType
            ).map { ensureUniqueOptions(it) }
            for (q in extra) {
                if (selected.size >= targetCount) break
                if (selected.none { it.questionBn == q.questionBn }) {
                    selected.add(q)
                }
            }
        }

        return selected.take(targetCount)
    }

    fun ensureUniqueOptions(question: QuizQuestion): QuizQuestion {
        if (question.optionsBn.size <= 1) return question
        val correctText = question.optionsBn.getOrElse(question.correctIndex) { question.correctAnswerText }
        val distinctOpts = question.optionsBn.distinct().toMutableList()
        var fillerSeed = 1
        while (distinctOpts.size < question.optionsBn.size) {
            val candidate = "${(fillerSeed * 11 + 3).toBengaliDigits()}"
            if (!distinctOpts.contains(candidate)) {
                distinctOpts.add(candidate)
            }
            fillerSeed++
        }
        val newCorrectIdx = distinctOpts.indexOf(correctText).coerceAtLeast(0)
        return question.copy(
            optionsBn = distinctOpts,
            correctIndex = newCorrectIdx,
            correctAnswerText = distinctOpts[newCorrectIdx]
        )
    }

    // Pure-Kotlin delimiter serializer so Room persistence works identically in Android and local JVM tests
    private const val RECORD_SEP = "\u001E"
    private const val FIELD_SEP = "\u001F"
    private const val LIST_SEP = "\u001D"

    fun serializeQuestions(questions: List<QuizQuestion>): String {
        return questions.joinToString(RECORD_SEP) { q ->
            val opts = q.optionsBn.joinToString(LIST_SEP) { it.replace(LIST_SEP, " ") }
            listOf(
                q.id.replace(FIELD_SEP, " "),
                q.questionBn.replace(FIELD_SEP, " "),
                opts,
                q.correctIndex.toString(),
                q.explanationBn.replace(FIELD_SEP, " "),
                q.topicTitleBn.replace(FIELD_SEP, " "),
                q.questionType.name,
                q.correctAnswerText.replace(FIELD_SEP, " "),
                q.trueFalseStatementBn.replace(FIELD_SEP, " "),
                q.isStatementTrue.toString(),
                q.difficulty.name
            ).joinToString(FIELD_SEP)
        }
    }

    fun deserializeQuestions(serialized: String): List<QuizQuestion> {
        if (serialized.isBlank()) return emptyList()
        return serialized.split(RECORD_SEP).mapNotNull { record ->
            val parts = record.split(FIELD_SEP)
            if (parts.size < 11) return@mapNotNull null
            val options = if (parts[2].isEmpty()) emptyList() else parts[2].split(LIST_SEP)
            val qType = runCatching { QuizQuestionType.valueOf(parts[6]) }.getOrDefault(QuizQuestionType.MCQ)
            val diff = runCatching { PracticeDifficulty.valueOf(parts[10]) }.getOrDefault(PracticeDifficulty.EASY)
            QuizQuestion(
                id = parts[0],
                questionBn = parts[1],
                optionsBn = options,
                correctIndex = parts[3].toIntOrNull() ?: 0,
                explanationBn = parts[4],
                topicTitleBn = parts[5],
                questionType = qType,
                correctAnswerText = parts[7],
                trueFalseStatementBn = parts[8],
                isStatementTrue = parts[9].toBooleanStrictOrNull() ?: true,
                difficulty = diff
            )
        }
    }

    fun serializeIntMap(map: Map<Int, Int>): String {
        if (map.isEmpty()) return ""
        return map.entries
            .sortedBy { it.key }
            .joinToString(",") { "${it.key}:${it.value}" }
    }

    fun deserializeIntMap(serialized: String): Map<Int, Int> {
        if (serialized.isBlank()) return emptyMap()
        val result = linkedMapOf<Int, Int>()
        serialized.split(",").forEach { pair ->
            val kv = pair.split(":")
            if (kv.size == 2) {
                val k = kv[0].toIntOrNull()
                val v = kv[1].toIntOrNull()
                if (k != null && v != null) {
                    result[k] = v
                }
            }
        }
        return result
    }

    fun serializeIntSet(set: Set<Int>): String {
        if (set.isEmpty()) return ""
        return set.sorted().joinToString(",")
    }

    fun deserializeIntSet(csv: String): Set<Int> {
        if (csv.isBlank()) return emptySet()
        return csv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    fun reconstructDetailedResult(entity: ExamAttemptEntity): ExamDetailedResult {
        val questions = deserializeQuestions(entity.questionsJson)
        val answers = deserializeIntMap(entity.answersJson)
        val marked = deserializeIntSet(entity.markedForReviewCsv)
        val questionTimes = deserializeIntMap(entity.questionTimeSecondsJson)
        return ExamScoringEngine.evaluateExam(
            attemptId = entity.attemptId,
            examTitleBn = entity.examTitleBn,
            examType = entity.examType,
            questions = questions,
            selectedAnswers = answers,
            markedForReview = marked,
            questionTimeSeconds = questionTimes,
            marksPerCorrect = entity.marksPerCorrect,
            negativeMarksPerWrong = entity.negativeMarksPerWrong,
            timeSpentSeconds = entity.elapsedSeconds,
            durationLimitSeconds = entity.durationLimitSeconds,
            completedAt = entity.completedAt ?: entity.updatedAt
        )
    }
}
