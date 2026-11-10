package com.example.math

import kotlin.random.Random

/**
 * Explicit State Machine for Module 06 (Interactive Quiz).
 */
enum class QuizState {
    IDLE,
    LOADING,
    IN_PROGRESS,
    ANSWER_SUBMITTED,
    REVIEWING,
    COMPLETED,
    ERROR
}

/**
 * Supported Quiz Modes for Module 06.
 */
enum class QuizMode(val id: String, val titleBn: String, val subtitleBn: String) {
    TOPIC("topic", "টপিক কুইজ", "নির্দিষ্ট বিষয়ের ওপর কুইজ"),
    MIXED("mixed", "মিশ্র কুইজ", "সকল বিষয়ের সমন্বিত প্রশ্ন"),
    TIMED("timed", "টাইমড কুইজ", "নির্দিষ্ট সময়ের মধ্যে কুইজ সম্পন্ন"),
    PRACTICE("practice", "প্র্যাকটিস কুইজ", "তাৎক্ষণিক ব্যাখ্যাসহ অনুশীলন"),
    DIFFICULTY_BASED("difficulty", "লেভেল ভিত্তিক কুইজ", "সহজ, মাঝারি বা কঠিন স্তরের কুইজ"),
    REVIEW_INCORRECT("review_incorrect", "ভুল উত্তরের রিভিউ কুইজ", "পূর্বের ভুল করা প্রশ্নগুলোর পুনঃপরীক্ষা")
}

/**
 * Supported Question Formats in Interactive Quiz.
 */
enum class QuizQuestionType(val id: String, val titleBn: String) {
    MCQ("mcq", "বহুনির্বাচনী (MCQ)"),
    TRUE_FALSE("true_false", "সত্য / মিথ্যা"),
    NUMERICAL("numerical", "সংখ্যা ইনপুট (Keypad)"),
    MIXED("mixed", "মিশ্র ধরন")
}

data class QuizQuestion(
    val id: String,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val topicTitleBn: String,
    val questionType: QuizQuestionType = QuizQuestionType.MCQ,
    val correctAnswerText: String = optionsBn.getOrElse(correctIndex) { "" },
    val trueFalseStatementBn: String = "",
    val isStatementTrue: Boolean = true,
    val difficulty: PracticeDifficulty = PracticeDifficulty.EASY
)

enum class QuizTopic(val id: String, val titleBn: String) {
    MULTIPLICATION("MULTIPLICATION", "গুণ ও নামতা কুইজ (১-৪০)"),
    PRIMES("PRIMES", "মৌলিক সংখ্যা কুইজ (১-২০০)"),
    SQUARES_CUBES("SQUARES_CUBES", "বর্গ ও ঘন কুইজ (১-২০)"),
    ALGEBRA("ALGEBRA", "বীজগণিত সূত্রাবলি কুইজ"),
    ARITHMETIC_PERCENTAGE("ARITHMETIC_PERCENTAGE", "পাটিগণিত, শতকরা ও অনুপাত কুইজ"),
    NUMBER_SYSTEM_DIVISIBILITY("NUMBER_SYSTEM_DIVISIBILITY", "বাস্তব সংখ্যা ও বিভাজ্যতা কুইজ"),
    GEOMETRY_MENSURATION("GEOMETRY_MENSURATION", "জ্যামিতি ও পরিমিতি কুইজ"),
    MIXED_CHALLENGE("MIXED_CHALLENGE", "সমন্বিত মেগা কুইজ (সকল বিষয়)")
}

object QuizGenerator {
    fun generateQuiz(
        topic: QuizTopic,
        questionCount: Int = 10,
        difficulty: PracticeDifficulty = PracticeDifficulty.EASY,
        questionType: QuizQuestionType = QuizQuestionType.MCQ
    ): List<QuizQuestion> {
        val rawQuestions = when (topic) {
            QuizTopic.MULTIPLICATION -> generateMultiplicationQuestions(questionCount, difficulty)
            QuizTopic.PRIMES -> generatePrimeQuestions(questionCount, difficulty, questionType)
            QuizTopic.SQUARES_CUBES -> generateSquareCubeQuestions(questionCount, difficulty)
            QuizTopic.ALGEBRA -> if (questionType == QuizQuestionType.NUMERICAL) {
                generateNumericalAlgebraQuestions(questionCount, difficulty)
            } else {
                generateAlgebraQuestions(questionCount)
            }
            QuizTopic.ARITHMETIC_PERCENTAGE -> generateArithmeticPercentageQuestions(questionCount, difficulty)
            QuizTopic.NUMBER_SYSTEM_DIVISIBILITY -> generateNumberSystemQuestions(questionCount, difficulty)
            QuizTopic.GEOMETRY_MENSURATION -> generateGeometryQuestions(questionCount, difficulty)
            QuizTopic.MIXED_CHALLENGE -> {
                val perPool = (questionCount / 4).coerceAtLeast(2)
                val algPool = if (questionType == QuizQuestionType.NUMERICAL) {
                    generateNumericalAlgebraQuestions(perPool, difficulty)
                } else {
                    generateAlgebraQuestions(perPool)
                }
                (generateMultiplicationQuestions(perPool, difficulty) +
                        generatePrimeQuestions(perPool, difficulty, questionType) +
                        generateSquareCubeQuestions(perPool, difficulty) +
                        algPool +
                        generateArithmeticPercentageQuestions(perPool, difficulty) +
                        generateGeometryQuestions(perPool, difficulty))
                    .shuffled()
                    .take(questionCount)
            }
        }

        val converted = rawQuestions.mapIndexed { index, q ->
            val rawAns = q.optionsBn.getOrElse(q.correctIndex) { q.correctAnswerText }
            val normalizedNumericAns = extractCleanNumericalAnswer(rawAns)
            val isNumericCapable = normalizedNumericAns != null

            val resolvedType = when (questionType) {
                QuizQuestionType.MIXED -> {
                    when (index % 3) {
                        0 -> QuizQuestionType.MCQ
                        1 -> QuizQuestionType.TRUE_FALSE
                        else -> if (isNumericCapable) QuizQuestionType.NUMERICAL else QuizQuestionType.MCQ
                    }
                }
                QuizQuestionType.NUMERICAL -> {
                    if (isNumericCapable) QuizQuestionType.NUMERICAL else QuizQuestionType.MCQ
                }
                else -> questionType
            }

            if (resolvedType == QuizQuestionType.TRUE_FALSE && q.optionsBn.size >= 2) {
                val correctOptText = q.optionsBn.getOrElse(q.correctIndex) { rawAns }
                val wrongOptions = q.optionsBn.filterIndexed { i, opt ->
                    i != q.correctIndex && !SharedMathEngine.isAnswerCorrect(opt, correctOptText)
                }
                val wantTrue = Random.nextBoolean() || wrongOptions.isEmpty()
                val shownOption = if (wantTrue) {
                    correctOptText
                } else {
                    wrongOptions.random()
                }
                val actualIsTrue = SharedMathEngine.isAnswerCorrect(shownOption, correctOptText)
                val tfStatement = formatTrueFalseStatementBn(q.questionBn, shownOption)
                val tfOptions = listOf("সত্য (True)", "মিথ্যা (False)")
                val tfCorrectIdx = if (actualIsTrue) 0 else 1
                q.copy(
                    questionType = QuizQuestionType.TRUE_FALSE,
                    optionsBn = tfOptions,
                    correctIndex = tfCorrectIdx,
                    correctAnswerText = tfOptions[tfCorrectIdx],
                    trueFalseStatementBn = tfStatement,
                    isStatementTrue = actualIsTrue,
                    difficulty = difficulty
                )
            } else if (resolvedType == QuizQuestionType.NUMERICAL) {
                val stemWithChoicesIfNeeded = if (
                    (q.questionBn.contains("নিচের কোন") || q.questionBn.contains("কোন সংখ্যাটি")) &&
                    !q.questionBn.contains("বিকল্পসমূহ:") &&
                    q.optionsBn.isNotEmpty()
                ) {
                    "${q.questionBn} (বিকল্পসমূহ: ${q.optionsBn.joinToString(", ")})"
                } else {
                    q.questionBn
                }
                q.copy(
                    questionBn = stemWithChoicesIfNeeded,
                    questionType = QuizQuestionType.NUMERICAL,
                    correctAnswerText = normalizedNumericAns ?: rawAns,
                    difficulty = difficulty
                )
            } else {
                q.copy(
                    questionType = resolvedType,
                    correctAnswerText = rawAns,
                    difficulty = difficulty
                )
            }
        }
        val seenIds = mutableSetOf<String>()
        return converted.mapIndexed { idx, q ->
            val uniqueId = if (seenIds.add(q.id)) q.id else "${q.id}_v${idx}"
            if (uniqueId == q.id) q else q.copy(id = uniqueId)
        }
    }

    internal fun formatTrueFalseStatementBn(questionBn: String, shownOption: String): String {
        val trimmed = questionBn.trim()
        val rawDeclarative = when {
            trimmed == "নিচের কোন সংখ্যাটি মৌলিক সংখ্যা?" || trimmed.contains("কোন সংখ্যাটি মৌলিক") ->
                "বিবৃতি: $shownOption একটি মৌলিক সংখ্যা।"
            trimmed.contains("মৌলিক সংখ্যা নয়") ->
                "বিবৃতি: $shownOption একটি যৌগিক সংখ্যা (মৌলিক সংখ্যা নয়)।"
            trimmed.endsWith("কি মৌলিক সংখ্যা?") -> {
                val subj = trimmed.removeSuffix("কি মৌলিক সংখ্যা?").trim()
                when {
                    shownOption.startsWith("হ্যাঁ") -> "বিবৃতি: $subj একটি মৌলিক সংখ্যা।"
                    shownOption.startsWith("না") -> "বিবৃতি: $subj একটি যৌগিক সংখ্যা (মৌলিক নয়)।"
                    else -> "বিবৃতি: $subj সম্পর্কিত প্রদত্ত সিদ্ধান্ত হলো $shownOption।"
                }
            }
            trimmed.endsWith("= কত?") ->
                "বিবৃতি: ${trimmed.removeSuffix("= কত?").trim()} = $shownOption"
            trimmed.endsWith("এর মান কত?") ->
                "বিবৃতি: ${trimmed.removeSuffix("এর মান কত?").trim()} এর মান $shownOption।"
            trimmed.endsWith("সমান কোনটি?") ->
                "বিবৃতি: ${trimmed.removeSuffix("সমান কোনটি?").trim()} = $shownOption।"
            trimmed.endsWith("কোনটি?") ->
                "বিবৃতি: ${trimmed.removeSuffix("কোনটি?").trim()} হলো $shownOption।"
            trimmed.endsWith("কত?") ->
                "বিবৃতি: ${trimmed.removeSuffix("কত?").trim()} হলো $shownOption।"
            else -> {
                val deinterrogated = trimmed
                    .removeSuffix("?")
                    .replace("= কত", "= $shownOption")
                    .replace("কত ডিগ্রি", "$shownOption ডিগ্রি")
                    .replace("কতটি", "${shownOption}টি")
                    .replace("কয়টি", "${shownOption}টি")
                    .replace("কোনটি", shownOption)
                    .replace("কত", shownOption)
                    .replace("কী", shownOption)
                    .replace("কি", "")
                    .replace(Regex("""\s+"""), " ")
                    .trim()
                if (deinterrogated.contains(shownOption)) {
                    "বিবৃতি: $deinterrogated।"
                } else {
                    "বিবৃতি: $deinterrogated — প্রদত্ত মান $shownOption।"
                }
            }
        }
        return rawDeclarative.replace("?", "").trim()
    }

    internal fun extractCleanNumericalAnswer(rawAnswer: String): String? {
        val cleaned = rawAnswer
            .replace("টি", "")
            .replace("জন", "")
            .replace("টাকা", "")
            .replace("দিন", "")
            .replace("ঘণ্টা", "")
            .replace("ঘন্টা", "")
            .replace("মিটার", "")
            .replace("সে.মি.", "")
            .replace("বর্গ একক", "")
            .replace("একক", "")
            .replace("%", "")
            .replace("°", "")
            .trim()
        if (cleaned.isEmpty()) return null
        if (cleaned.any { it in 'a'..'z' || it in 'A'..'Z' || it == '²' || it == '³' || it == '√' || it == 'π' }) {
            return null
        }
        return if (SharedMathEngine.parseRationalOrNull(SharedMathEngine.normalizeAnswerToken(cleaned)) != null) cleaned else null
    }

    private fun generateMultiplicationQuestions(count: Int, difficulty: PracticeDifficulty = PracticeDifficulty.EASY): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        val usedPairs = mutableSetOf<Pair<Int, Int>>()
        repeat(count) {
            var a = 2
            var b = 2
            var attempts = 0
            do {
                a = when (difficulty) {
                    PracticeDifficulty.EASY -> Random.nextInt(2, 21)
                    PracticeDifficulty.MEDIUM -> Random.nextInt(12, 35)
                    else -> Random.nextInt(18, 41)
                }
                b = when (difficulty) {
                    PracticeDifficulty.EASY -> Random.nextInt(2, 11)
                    PracticeDifficulty.MEDIUM -> Random.nextInt(6, 16)
                    else -> Random.nextInt(11, 25)
                }
                attempts++
            } while (!usedPairs.add(a to b) && attempts < 50)
            val correctAns = a * b
            val questionBn = "${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)} এর মান কত?"

            val distractors = mutableSetOf<Int>()
            while (distractors.size < 3) {
                val delta = Random.nextInt(-12, 13)
                val fake = correctAns + delta
                if (fake > 0 && fake != correctAns) {
                    distractors.add(fake)
                }
            }
            val allOptions = (distractors.toList() + correctAns).shuffled()
            val correctIdx = allOptions.indexOf(correctAns)
            val optsBn = allOptions.map { BengaliNumberFormatter.toBn(it) }

            list.add(
                QuizQuestion(
                    id = "mul_${a}x${b}",
                    questionBn = questionBn,
                    optionsBn = optsBn,
                    correctIndex = correctIdx,
                    explanationBn = "${BengaliNumberFormatter.toBn(a)} কে ${BengaliNumberFormatter.toBn(b)} দিয়ে গুণ করলে গুণফল হয় ${BengaliNumberFormatter.toBn(correctAns)}।",
                    topicTitleBn = "নামতা কুইজ",
                    correctAnswerText = BengaliNumberFormatter.toBn(correctAns)
                )
            )
        }
        return list
    }

    private fun generatePrimeQuestions(
        count: Int,
        difficulty: PracticeDifficulty = PracticeDifficulty.EASY,
        questionType: QuizQuestionType = QuizQuestionType.MCQ
    ): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        repeat(count) { idx ->
            val type = if (questionType == QuizQuestionType.NUMERICAL) idx % 2 else idx % 3
            when (type) {
                0 -> {
                    val primeNum = if (difficulty == PracticeDifficulty.EASY || Random.nextBoolean()) {
                        PrimeNumberProvider.primes1To100.random()
                    } else {
                        PrimeNumberProvider.primes101To200.random()
                    }
                    val composites = mutableSetOf<Int>()
                    while (composites.size < 3) {
                        val num = Random.nextInt(4, 200)
                        if (!PrimeNumberProvider.isPrime(num)) composites.add(num)
                    }
                    val allOptions = (composites.toList() + primeNum).shuffled()
                    val correctIdx = allOptions.indexOf(primeNum)

                    list.add(
                        QuizQuestion(
                            id = "prime_q_${primeNum}_${composites.sorted().joinToString("_")}",
                            questionBn = "নিচের কোন সংখ্যাটি মৌলিক সংখ্যা?",
                            optionsBn = allOptions.map { BengaliNumberFormatter.toBn(it) },
                            correctIndex = correctIdx,
                            explanationBn = "${BengaliNumberFormatter.toBn(primeNum)} একটি মৌলিক সংখ্যা কারণ এর ১ ও এটি ছাড়া অন্য উৎপাদক নেই।",
                            topicTitleBn = "মৌলিক সংখ্যা"
                        )
                    )
                }
                1 -> {
                    val compositeNum = run {
                        var c = Random.nextInt(4, 200)
                        while (PrimeNumberProvider.isPrime(c)) c = Random.nextInt(4, 200)
                        c
                    }
                    val primes = mutableSetOf<Int>()
                    while (primes.size < 3) {
                        val p = if (Random.nextBoolean()) PrimeNumberProvider.primes1To100.random() else PrimeNumberProvider.primes101To200.random()
                        primes.add(p)
                    }
                    val allOptions = (primes.toList() + compositeNum).shuffled()
                    val correctIdx = allOptions.indexOf(compositeNum)

                    list.add(
                        QuizQuestion(
                            id = "prime_comp_${compositeNum}_${primes.sorted().joinToString("_")}",
                            questionBn = "নিচের কোনটি মৌলিক সংখ্যা নয় (যৌগিক সংখ্যা)?",
                            optionsBn = allOptions.map { BengaliNumberFormatter.toBn(it) },
                            correctIndex = correctIdx,
                            explanationBn = "${BengaliNumberFormatter.toBn(compositeNum)} যৌগিক সংখ্যা। এর গুণনীয়ক ১ এবং নিজেকে ছাড়া আরও রয়েছে।",
                            topicTitleBn = "মৌলিক সংখ্যা"
                        )
                    )
                }
                else -> {
                    val num = Random.nextInt(2, 199)
                    val isP = PrimeNumberProvider.isPrime(num)
                    val q = "${BengaliNumberFormatter.toBn(num)} কি মৌলিক সংখ্যা?"
                    val options = listOf("হ্যাঁ, এটি মৌলিক", "না, এটি যৌগিক")
                    val correctIdx = if (isP) 0 else 1
                    val explanation = PrimeNumberProvider.getPrimeInfo(num).explanationBn

                    list.add(
                        QuizQuestion(
                            id = "prime_check_${num}",
                            questionBn = q,
                            optionsBn = options,
                            correctIndex = correctIdx,
                            explanationBn = explanation,
                            topicTitleBn = "মৌলিক সংখ্যা"
                        )
                    )
                }
            }
        }
        return list
    }

    private fun generateSquareCubeQuestions(count: Int, difficulty: PracticeDifficulty = PracticeDifficulty.EASY): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        val usedSpecs = mutableSetOf<String>()
        repeat(count) {
            val maxN = when (difficulty) {
                PracticeDifficulty.EASY -> 15
                PracticeDifficulty.MEDIUM -> 22
                else -> 30
            }
            var n = Random.nextInt(2, maxN + 1)
            var isSquare = Random.nextBoolean()
            var isRoot = Random.nextBoolean()
            var tries = 0
            while (!usedSpecs.add("${n}_${isSquare}_${isRoot}") && tries < 40) {
                n = Random.nextInt(2, maxN + 1)
                isSquare = Random.nextBoolean()
                isRoot = Random.nextBoolean()
                tries++
            }

            if (isSquare) {
                if (isRoot) {
                    val sq = n * n
                    val questionBn = "√${BengaliNumberFormatter.toBn(sq)} এর মান কত?"
                    val correctAns = n
                    val distractors = mutableSetOf<Int>()
                    while (distractors.size < 3) {
                        val fake = Random.nextInt(1, maxN + 3)
                        if (fake != correctAns) distractors.add(fake)
                    }
                    val allOptions = (distractors.toList() + correctAns).shuffled()
                    list.add(
                        QuizQuestion(
                            id = "sq_q_${sq}",
                            questionBn = questionBn,
                            optionsBn = allOptions.map { BengaliNumberFormatter.toBn(it) },
                            correctIndex = allOptions.indexOf(correctAns),
                            explanationBn = "${BengaliNumberFormatter.toBn(n)}² = ${BengaliNumberFormatter.toBn(sq)}, সুতরাং √${BengaliNumberFormatter.toBn(sq)} = ${BengaliNumberFormatter.toBn(n)}",
                            topicTitleBn = "বর্গ ও বর্গমূল"
                        )
                    )
                } else {
                    val sq = n * n
                    val questionBn = "${BengaliNumberFormatter.toBn(n)}² (বর্গ) এর মান কত?"
                    val correctAns = sq
                    val distractors = mutableSetOf<Int>()
                    while (distractors.size < 3) {
                        val delta = Random.nextInt(-15, 16)
                        val fake = correctAns + delta
                        if (fake > 0 && fake != correctAns) distractors.add(fake)
                    }
                    val allOptions = (distractors.toList() + correctAns).shuffled()
                    list.add(
                        QuizQuestion(
                            id = "sq_val_${n}",
                            questionBn = questionBn,
                            optionsBn = allOptions.map { BengaliNumberFormatter.toBn(it) },
                            correctIndex = allOptions.indexOf(correctAns),
                            explanationBn = "${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)} = ${BengaliNumberFormatter.toBn(sq)}",
                            topicTitleBn = "বর্গ ও বর্গমূল"
                        )
                    )
                }
            } else {
                val cbN = n.coerceAtMost(15)
                val cb = cbN * cbN * cbN
                if (isRoot) {
                    val questionBn = "∛${BengaliNumberFormatter.toBn(cb)} এর মান কত?"
                    val correctAns = cbN
                    val distractors = mutableSetOf<Int>()
                    while (distractors.size < 3) {
                        val fake = Random.nextInt(1, 18)
                        if (fake != correctAns) distractors.add(fake)
                    }
                    val allOptions = (distractors.toList() + correctAns).shuffled()
                    list.add(
                        QuizQuestion(
                            id = "cb_root_${cb}",
                            questionBn = questionBn,
                            optionsBn = allOptions.map { BengaliNumberFormatter.toBn(it) },
                            correctIndex = allOptions.indexOf(correctAns),
                            explanationBn = "${BengaliNumberFormatter.toBn(cbN)}³ = ${BengaliNumberFormatter.toBn(cb)}, সুতরাং ∛${BengaliNumberFormatter.toBn(cb)} = ${BengaliNumberFormatter.toBn(cbN)}",
                            topicTitleBn = "ঘন ও ঘনমূল"
                        )
                    )
                } else {
                    val questionBn = "${BengaliNumberFormatter.toBn(cbN)}³ (ঘন) এর মান কত?"
                    val correctAns = cb
                    val distractors = mutableSetOf<Int>()
                    while (distractors.size < 3) {
                        val delta = Random.nextInt(-30, 31)
                        val fake = correctAns + delta
                        if (fake > 0 && fake != correctAns) distractors.add(fake)
                    }
                    val allOptions = (distractors.toList() + correctAns).shuffled()
                    list.add(
                        QuizQuestion(
                            id = "cb_val_${cbN}",
                            questionBn = questionBn,
                            optionsBn = allOptions.map { BengaliNumberFormatter.toBn(it) },
                            correctIndex = allOptions.indexOf(correctAns),
                            explanationBn = "${BengaliNumberFormatter.toBn(cbN)} × ${BengaliNumberFormatter.toBn(cbN)} × ${BengaliNumberFormatter.toBn(cbN)} = ${BengaliNumberFormatter.toBn(cb)}",
                            topicTitleBn = "ঘন ও ঘনমূল"
                        )
                    )
                }
            }
        }
        return list
    }

    private fun generateAlgebraQuestions(count: Int): List<QuizQuestion> {
        val pool = listOf(
            QuizQuestion(
                id = "alg_1",
                questionBn = "(a + b)² এর সঠিক বিস্তার কোনটি?",
                optionsBn = listOf("a² + 2ab + b²", "a² - 2ab + b²", "a² + b²", "a² + ab + b²"),
                correctIndex = 0,
                explanationBn = "(a + b)² = a² + 2ab + b²",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_2",
                questionBn = "a² - b² সমান কোনটি?",
                optionsBn = listOf("(a + b)(a - b)", "(a - b)²", "(a + b)²", "a² - 2ab + b²"),
                correctIndex = 0,
                explanationBn = "বর্গান্তরের সূত্র: a² - b² = (a + b)(a - b)",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_3",
                questionBn = "(a - b)² এর সূত্র কোনটি?",
                optionsBn = listOf("a² - 2ab + b²", "a² + 2ab + b²", "a² - b²", "a² - ab + b²"),
                correctIndex = 0,
                explanationBn = "(a - b)² = a² - 2ab + b²",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_4",
                questionBn = "4ab সমান নিচের কোনটি?",
                optionsBn = listOf("(a + b)² - (a - b)²", "(a + b)² + (a - b)²", "2(a² + b²)", "(a² + b²)/2"),
                correctIndex = 0,
                explanationBn = "4ab = (a + b)² - (a - b)²",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_5",
                questionBn = "2(a² + b²) এর অনুসিদ্ধান্ত কোনটি?",
                optionsBn = listOf("(a + b)² + (a - b)²", "(a + b)² - (a - b)²", "4ab", "(a + b)²"),
                correctIndex = 0,
                explanationBn = "2(a² + b²) = (a + b)² + (a - b)²",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_6",
                questionBn = "(a + b)³ এর সঠিক সূত্র কোনটি?",
                optionsBn = listOf("a³ + 3a²b + 3ab² + b³", "a³ - 3a²b + 3ab² - b³", "a³ + b³ + ab", "a³ + b³"),
                correctIndex = 0,
                explanationBn = "(a + b)³ = a³ + 3a²b + 3ab² + b³",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_7",
                questionBn = "a³ + b³ এর উৎপাদক সূত্র কোনটি?",
                optionsBn = listOf("(a + b)(a² - ab + b²)", "(a - b)(a² + ab + b²)", "(a + b)³ - 3ab", "(a + b)(a² + b²)"),
                correctIndex = 0,
                explanationBn = "a³ + b³ = (a + b)(a² - ab + b²)",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_8",
                questionBn = "a³ - b³ এর উৎপাদক সূত্র কোনটি?",
                optionsBn = listOf("(a - b)(a² + ab + b²)", "(a + b)(a² - ab + b²)", "(a - b)³ + 3ab", "(a - b)(a² - b²)"),
                correctIndex = 0,
                explanationBn = "a³ - b³ = (a - b)(a² + ab + b²)",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_9",
                questionBn = "(a - b)³ এর সঠিক সূত্র কোনটি?",
                optionsBn = listOf("a³ - 3a²b + 3ab² - b³", "a³ + 3a²b - 3ab² + b³", "a³ - b³ - 3ab", "a³ - b³"),
                correctIndex = 0,
                explanationBn = "(a - b)³ = a³ - 3a²b + 3ab² - b³ = a³ - b³ - 3ab(a - b)",
                topicTitleBn = "বীজগণিত সূত্র"
            ),
            QuizQuestion(
                id = "alg_10",
                questionBn = "(a + b + c)² সমান নিচের কোনটি?",
                optionsBn = listOf(
                    "a² + b² + c² + 2(ab + bc + ca)",
                    "a² + b² + c² + ab + bc + ca",
                    "(a + b)² + c²",
                    "a² + b² + c² - 2(ab + bc + ca)"
                ),
                correctIndex = 0,
                explanationBn = "(a + b + c)² = a² + b² + c² + 2(ab + bc + ca)",
                topicTitleBn = "বীজগণিত সূত্র"
            )
        )
        val result = mutableListOf<QuizQuestion>()
        var pass = 0
        while (result.size < count) {
            for (q in pool.shuffled()) {
                if (result.size >= count) break
                val correctOpt = q.optionsBn[q.correctIndex]
                val shuffledOpts = q.optionsBn.shuffled()
                val qId = if (pass == 0) q.id else "${q.id}_p$pass"
                result.add(
                    q.copy(
                        id = qId,
                        optionsBn = shuffledOpts,
                        correctIndex = shuffledOpts.indexOf(correctOpt)
                    )
                )
            }
            pass++
        }
        return result
    }

    private fun generateNumericalAlgebraQuestions(
        count: Int,
        difficulty: PracticeDifficulty = PracticeDifficulty.EASY
    ): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        val usedTriples = mutableSetOf<Triple<Int, Int, Int>>()
        repeat(count) { idx ->
            val pattern = idx % 4
            var a = 4
            var b = 2
            var tries = 0
            do {
                a = when (difficulty) {
                    PracticeDifficulty.EASY -> Random.nextInt(3, 8)
                    PracticeDifficulty.MEDIUM -> Random.nextInt(5, 11)
                    else -> Random.nextInt(7, 15)
                }
                b = Random.nextInt(1, a)
                tries++
            } while (!usedTriples.add(Triple(pattern, a, b)) && tries < 40)
            val (questionBn, correctAns, explanationBn) = when (pattern) {
                0 -> {
                    val ans = (a + b) * (a + b)
                    val aBn = BengaliNumberFormatter.toBn(a)
                    val bBn = BengaliNumberFormatter.toBn(b)
                    val ansBn = BengaliNumberFormatter.toBn(ans)
                    Triple(
                        "যদি a = $aBn এবং b = $bBn হয়, তবে (a + b)² এর মান কত?",
                        ans,
                        "(a + b)² = ($aBn + $bBn)² = ${BengaliNumberFormatter.toBn(a + b)}² = $ansBn"
                    )
                }
                1 -> {
                    val ans = (a * a) - (b * b)
                    val aBn = BengaliNumberFormatter.toBn(a)
                    val bBn = BengaliNumberFormatter.toBn(b)
                    val ansBn = BengaliNumberFormatter.toBn(ans)
                    Triple(
                        "যদি a = $aBn এবং b = $bBn হয়, তবে a² - b² এর মান কত?",
                        ans,
                        "a² - b² = (a + b)(a - b) = ${BengaliNumberFormatter.toBn(a + b)} × ${BengaliNumberFormatter.toBn(a - b)} = $ansBn"
                    )
                }
                2 -> {
                    val ans = 4 * a * b
                    val aBn = BengaliNumberFormatter.toBn(a)
                    val bBn = BengaliNumberFormatter.toBn(b)
                    val ansBn = BengaliNumberFormatter.toBn(ans)
                    Triple(
                        "যদি a = $aBn এবং b = $bBn হয়, তবে (a + b)² - (a - b)² এর মান কত?",
                        ans,
                        "(a + b)² - (a - b)² = 4ab = ৪ × $aBn × $bBn = $ansBn"
                    )
                }
                else -> {
                    val ans = 2 * ((a * a) + (b * b))
                    val aBn = BengaliNumberFormatter.toBn(a)
                    val bBn = BengaliNumberFormatter.toBn(b)
                    val ansBn = BengaliNumberFormatter.toBn(ans)
                    Triple(
                        "যদি a = $aBn এবং b = $bBn হয়, তবে (a + b)² + (a - b)² এর মান কত?",
                        ans,
                        "(a + b)² + (a - b)² = 2(a² + b²) = ২ × (${BengaliNumberFormatter.toBn(a * a)} + ${BengaliNumberFormatter.toBn(b * b)}) = $ansBn"
                    )
                }
            }
            val distractors = listOf(correctAns + 2, (correctAns - 2).coerceAtLeast(1), correctAns + 4)
            val allOpts = (distractors + correctAns).distinct().shuffled()
            val optsBn = allOpts.map { BengaliNumberFormatter.toBn(it) }
            val correctIdx = allOpts.indexOf(correctAns).coerceAtLeast(0)
            list.add(
                QuizQuestion(
                    id = "alg_num_p${pattern}_${a}_${b}",
                    questionBn = questionBn,
                    optionsBn = optsBn,
                    correctIndex = correctIdx,
                    explanationBn = explanationBn,
                    topicTitleBn = "বীজগণিত সূত্র",
                    questionType = QuizQuestionType.NUMERICAL,
                    correctAnswerText = BengaliNumberFormatter.toBn(correctAns),
                    difficulty = difficulty
                )
            )
        }
        return list
    }

    private fun generateArithmeticPercentageQuestions(
        count: Int,
        difficulty: PracticeDifficulty
    ): List<QuizQuestion> {
        val practiceQs = PracticeQuestionGenerator.generateSessionQuestions(
            category = PracticeCategory.PERCENTAGES,
            difficulty = difficulty,
            count = count
        )
        return practiceQs.map { pq ->
            val contentKey = pq.questionTextBn.hashCode().toUInt().toString(16)
            QuizQuestion(
                id = "quiz_arith_$contentKey",
                questionBn = pq.questionTextBn,
                optionsBn = pq.optionsBn,
                correctIndex = pq.correctOptionIndex,
                explanationBn = pq.stepByStepExplanationBn.joinToString("\n"),
                topicTitleBn = "পাটিগণিত ও শতকরা",
                correctAnswerText = pq.correctAnswerBn
            )
        }
    }

    private fun generateNumberSystemQuestions(
        count: Int,
        difficulty: PracticeDifficulty
    ): List<QuizQuestion> {
        val practiceQs = PracticeQuestionGenerator.generateSessionQuestions(
            category = PracticeCategory.DIVISIBILITY,
            difficulty = difficulty,
            count = count
        )
        return practiceQs.map { pq ->
            val contentKey = pq.questionTextBn.hashCode().toUInt().toString(16)
            QuizQuestion(
                id = "quiz_ns_$contentKey",
                questionBn = pq.questionTextBn,
                optionsBn = pq.optionsBn,
                correctIndex = pq.correctOptionIndex,
                explanationBn = pq.stepByStepExplanationBn.joinToString("\n"),
                topicTitleBn = "বাস্তব সংখ্যা ও বিভাজ্যতা",
                correctAnswerText = pq.correctAnswerBn
            )
        }
    }

    private fun generateGeometryQuestions(
        count: Int,
        difficulty: PracticeDifficulty
    ): List<QuizQuestion> {
        val practiceQs = PracticeQuestionGenerator.generateSessionQuestions(
            category = PracticeCategory.GEOMETRY,
            difficulty = difficulty,
            count = count
        )
        return practiceQs.map { pq ->
            val contentKey = pq.questionTextBn.hashCode().toUInt().toString(16)
            QuizQuestion(
                id = "quiz_geom_$contentKey",
                questionBn = pq.questionTextBn,
                optionsBn = pq.optionsBn,
                correctIndex = pq.correctOptionIndex,
                explanationBn = pq.stepByStepExplanationBn.joinToString("\n"),
                topicTitleBn = "জ্যামিতি ও পরিমিতি",
                correctAnswerText = pq.correctAnswerBn
            )
        }
    }
}
