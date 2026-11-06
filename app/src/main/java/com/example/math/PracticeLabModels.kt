package com.example.math

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.abs
import kotlin.random.Random

typealias PracticeQuestionGenerator = PracticeLabGenerator

/**
 * 12 Comprehensive Mathematics Practice Lab Topics (Directive Section 5).
 * Preserves existing enum identifiers for full backwards compatibility.
 */
enum class PracticeCategory(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val badgeBn: String
) {
    ARITHMETIC(
        id = "arithmetic",
        titleBn = "পাটিগণিত (Arithmetic)",
        titleEn = "Arithmetic",
        subtitleBn = "চার প্রক্রিয়া, গড়, ঐকিক নিয়ম ও লাভ-ক্ষতির মৌলিক হিসাব",
        badgeBn = "পাটিগণিত"
    ),
    FRACTIONS(
        id = "fractions",
        titleBn = "ভগ্নাংশ (Fractions)",
        titleEn = "Fractions",
        subtitleBn = "ভগ্নাংশের যোগ, বিয়োগ, গুণ, ভাগ ও লঘিষ্ঠ আকারে প্রকাশ",
        badgeBn = "হর ও লব"
    ),
    DECIMALS(
        id = "decimals",
        titleBn = "দশমিক (Decimals)",
        titleEn = "Decimals",
        subtitleBn = "দশমিক সংখ্যার হিসাব, স্থানীয় মান ও ভগ্নাংশে রূপান্তর",
        badgeBn = "দশমিক বিন্দু"
    ),
    PERCENTAGES(
        id = "percentages",
        titleBn = "শতকরা (Percentages)",
        titleEn = "Percentages",
        subtitleBn = "শতকরা হার, বৃদ্ধি-হ্রাস, ছাড় ও লাভ-ক্ষতির গাণিতিক প্রয়োগ",
        badgeBn = "% শতকরা হার"
    ),
    RATIOS(
        id = "ratios",
        titleBn = "অনুপাত ও সমানুপাত",
        titleEn = "Ratios & Proportions",
        subtitleBn = "লঘিষ্ঠ অনুপাত, সমানুপাতের অজানা পদ ও আনুপাতিক ভাগ",
        badgeBn = "a : b অনুপাত"
    ),
    BASIC_ALGEBRA(
        id = "basic_algebra",
        titleBn = "বীজগণিত (Algebra)",
        titleEn = "Algebra",
        subtitleBn = "এক চলকবিশিষ্ট সমীকরণ সমাধান, রাশির মান ও অভেদ প্রয়োগ",
        badgeBn = "চলক x ও y"
    ),
    GEOMETRY(
        id = "geometry",
        titleBn = "জ্যামিতি ও পরিমিতি",
        titleEn = "Geometry & Mensuration",
        subtitleBn = "ত্রিভুজ, চতুর্ভুজ, বৃত্ত ও ঘনবস্তুর ক্ষেত্রফল, পরিসীমা ও কোণ",
        badgeBn = "ক্ষেত্রফল ও কোণ"
    ),
    NUMBER_THEORY(
        id = "number_theory",
        titleBn = "সংখ্যাতত্ত্ব (Number Theory)",
        titleEn = "Number Theory",
        subtitleBn = "গ.সা.গু, ল.সা.গু, মৌলিক উৎপাদক, সহমৌলিক ও ভাজক সংখ্যা",
        badgeBn = "গ.সা.গু ও ল.সা.গু"
    ),
    DIVISIBILITY(
        id = "divisibility",
        titleBn = "বিভাজ্যতা (Divisibility)",
        titleEn = "Divisibility",
        subtitleBn = "২ থেকে ১২ পর্যন্ত বিভাজ্যতার শর্ত, লুপ্ত অঙ্ক ও ভাগশেষ নির্ণয়",
        badgeBn = "বিভাজ্যতার সূত্র"
    ),
    SQUARES_ROOTS(
        id = "squares_roots",
        titleBn = "সূচক ও বর্গ (Exponents)",
        titleEn = "Exponents & Squares",
        subtitleBn = "পূর্ণবর্গ সংখ্যা, সূচকের নিয়ম, ঘাতের গুণ-ভাগ ও সরলীকরণ",
        badgeBn = "xⁿ ও x²"
    ),
    CUBES_ROOTS(
        id = "cubes_roots",
        titleBn = "মূল ও করণী (Roots)",
        titleEn = "Roots & Radicals",
        subtitleBn = "বর্গমূল, ঘনমূল, পূর্ণঘন সংখ্যা ও করণী সরলীকরণ",
        badgeBn = "√x ও ∛x"
    ),
    NUMBER_OPERATIONS(
        id = "number_operations",
        titleBn = "মিশ্র প্রক্রিয়া ও BODMAS",
        titleEn = "Mixed Operations",
        subtitleBn = "বন্ধনী, ভাগ, গুণ, যোগ ও বিয়োগের ক্রম (BODMAS) ও সমন্বিত গণিত",
        badgeBn = "BODMAS ক্রম"
    );

    val primaryColor: Color
        get() = when (this) {
            ARITHMETIC -> Color(0xFF2563EB)
            FRACTIONS -> Color(0xFFD97706)
            DECIMALS -> Color(0xFF0891B2)
            PERCENTAGES -> Color(0xFF059669)
            RATIOS -> Color(0xFF4F46E5)
            BASIC_ALGEBRA -> Color(0xFF7C3AED)
            GEOMETRY -> Color(0xFF16A34A)
            NUMBER_THEORY -> Color(0xFF0D9488)
            DIVISIBILITY -> Color(0xFFDB2777)
            SQUARES_ROOTS -> Color(0xFFE11D48)
            CUBES_ROOTS -> Color(0xFF9333EA)
            NUMBER_OPERATIONS -> Color(0xFFEA580C)
        }

    fun getIcon(): ImageVector = when (this) {
        ARITHMETIC -> Icons.Default.Calculate
        FRACTIONS -> Icons.Default.PieChart
        DECIMALS -> Icons.Default.Exposure
        PERCENTAGES -> Icons.Default.Percent
        RATIOS -> Icons.Default.AutoAwesome
        BASIC_ALGEBRA -> Icons.Default.Functions
        GEOMETRY -> Icons.Default.Straighten
        NUMBER_THEORY -> Icons.Default.Numbers
        DIVISIBILITY -> Icons.Default.Numbers
        SQUARES_ROOTS -> Icons.Default.Straighten
        CUBES_ROOTS -> Icons.Default.ViewInAr
        NUMBER_OPERATIONS -> Icons.Default.Calculate
    }

    companion object {
        val ADDITION_SUBTRACTION: PracticeCategory get() = ARITHMETIC
    }
}

enum class PracticeDifficulty(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val pointsPerCorrect: Int,
    val badgeColor: Color
) {
    EASY("easy", "সহজ (Easy)", "Easy", 10, Color(0xFF10B981)),
    MEDIUM("medium", "মধ্যম (Medium)", "Medium", 20, Color(0xFFF59E0B)),
    HARD("hard", "কঠিন (Hard)", "Hard", 30, Color(0xFFEF4444)),
    ADAPTIVE("adaptive", "অ্যাডাপ্টিভ (Adaptive)", "Adaptive", 25, Color(0xFF7C3AED))
}

data class PracticeQuestion(
    val id: String,
    val category: PracticeCategory,
    val difficulty: PracticeDifficulty,
    val questionBn: String,
    val expression: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val correctAnswerBn: String,
    val stepByStepExplanationBn: List<String>,
    val tipBn: String,
    val hintBn: String = tipBn,
    val inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ,
    val trueFalseStatementBn: String? = null,
    val isTrueFalseCorrect: Boolean? = null
) {
    val questionTextBn: String get() = questionBn
    val correctOptionIndex: Int get() = correctIndex
    val isStatementTrue: Boolean get() = isTrueFalseCorrect ?: true
    val isTrueStatement: Boolean get() = isTrueFalseCorrect ?: true
}

data class PracticeSessionState(
    val category: PracticeCategory = PracticeCategory.FRACTIONS,
    val difficulty: PracticeDifficulty = PracticeDifficulty.EASY,
    val activeAdaptiveDifficulty: PracticeDifficulty = PracticeDifficulty.EASY,
    val inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ,
    val isUnlimitedMode: Boolean = false,
    val questions: List<PracticeQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val customInputText: String = "",
    val typedInput: String = "",
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val isHintRevealed: Boolean = false,
    val hintsUsedCount: Int = 0,
    val consecutiveCorrect: Int = 0,
    val consecutiveWrong: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val adaptiveTier: PracticeDifficulty = PracticeDifficulty.EASY,
    val startTimeMs: Long = 0L,
    val totalTimeSeconds: Long = 0L,
    val userAnswers: Map<Int, Int> = emptyMap(),
    val userTextAnswers: Map<Int, String> = emptyMap(),
    val questionOutcomes: Map<Int, Boolean> = emptyMap(),
    val isFinished: Boolean = false,
    val isReviewMode: Boolean = false
) {
    val incorrectQuestions: List<PracticeQuestion>
        get() = questions.filterIndexed { index, q ->
            val recordedOutcome = questionOutcomes[index]
            if (recordedOutcome != null) {
                !recordedOutcome
            } else {
                val ans = userAnswers[index]
                if (inputFormat == QuestionInputFormat.TRUE_FALSE) {
                    ans == null || ((ans == 0) != q.isStatementTrue)
                } else {
                    ans == null || ans != q.correctIndex
                }
            }
        }
}

object PracticeLabGenerator {

    fun generateSessionQuestions(
        category: PracticeCategory,
        difficulty: PracticeDifficulty,
        count: Int = 10,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ
    ): List<PracticeQuestion> = generateSession(category, difficulty, count, inputFormat)

    fun generateSession(
        category: PracticeCategory,
        difficulty: PracticeDifficulty,
        count: Int = 10,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ
    ): List<PracticeQuestion> {
        val safeCount = count.coerceIn(1, 50)
        return (0 until safeCount).map { index ->
            val effectiveDiff = if (difficulty == PracticeDifficulty.ADAPTIVE) {
                when {
                    index < safeCount / 3 -> PracticeDifficulty.EASY
                    index < (safeCount * 2) / 3 -> PracticeDifficulty.MEDIUM
                    else -> PracticeDifficulty.HARD
                }
            } else {
                difficulty
            }
            val rawQ = generateSingleQuestion(category, effectiveDiff, index)
            val baseWithDiff = if (difficulty == PracticeDifficulty.ADAPTIVE) {
                rawQ.copy(difficulty = PracticeDifficulty.ADAPTIVE)
            } else {
                rawQ
            }
            adaptQuestionToFormat(baseWithDiff, inputFormat, index)
        }
    }

    fun generateNextAdaptiveQuestion(
        category: PracticeCategory,
        currentTier: PracticeDifficulty,
        index: Int,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ
    ): PracticeQuestion {
        val effectiveTier = if (currentTier == PracticeDifficulty.ADAPTIVE) PracticeDifficulty.MEDIUM else currentTier
        val raw = generateSingleQuestion(category, effectiveTier, index).copy(difficulty = PracticeDifficulty.ADAPTIVE)
        return adaptQuestionToFormat(raw, inputFormat, index)
    }

    fun generateNextAdaptiveQuestion(
        category: PracticeCategory,
        currentDifficulty: PracticeDifficulty,
        consecutiveCorrect: Int,
        consecutiveWrong: Int,
        questionIndex: Int,
        inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ
    ): PracticeQuestion {
        return generateNextAdaptiveQuestion(category, currentDifficulty, questionIndex, inputFormat)
    }

    private fun adaptQuestionToFormat(
        q: PracticeQuestion,
        inputFormat: QuestionInputFormat,
        index: Int
    ): PracticeQuestion {
        return when (inputFormat) {
            QuestionInputFormat.MCQ -> q.copy(inputFormat = QuestionInputFormat.MCQ)
            QuestionInputFormat.KEYPAD -> q.copy(inputFormat = QuestionInputFormat.KEYPAD)
            QuestionInputFormat.TRUE_FALSE -> {
                val showCorrect = (index % 2 == 0) || Random.nextBoolean()
                val displayedAns = if (showCorrect) {
                    q.correctAnswerBn
                } else {
                    q.optionsBn.firstOrNull { it != q.correctAnswerBn } ?: "${q.correctAnswerBn}১"
                }
                val tfOptions = listOf("সত্য (True)", "মিথ্যা (False)")
                val tfCorrectIdx = if (showCorrect) 0 else 1
                val statement = "${q.expression.replace("?", displayedAns)} — উক্তিটি কি সঠিক?"
                q.copy(
                    inputFormat = QuestionInputFormat.TRUE_FALSE,
                    questionBn = "${q.questionBn}\nপ্রস্তাবিত উত্তর: $displayedAns",
                    optionsBn = tfOptions,
                    correctIndex = tfCorrectIdx,
                    correctAnswerBn = tfOptions[tfCorrectIdx],
                    trueFalseStatementBn = statement,
                    isTrueFalseCorrect = showCorrect
                )
            }
        }
    }

    fun verifyQuestionIntegrity(q: PracticeQuestion): Boolean {
        if (q.questionBn.isBlank() || q.expression.isBlank()) return false
        if (q.optionsBn.isEmpty() || q.correctIndex !in q.optionsBn.indices) return false
        if (q.optionsBn.distinct().size != q.optionsBn.size) return false
        if (q.optionsBn[q.correctIndex] != q.correctAnswerBn) return false
        if (q.stepByStepExplanationBn.isEmpty()) return false
        return true
    }

    private fun generateSingleQuestion(
        category: PracticeCategory,
        difficulty: PracticeDifficulty,
        index: Int
    ): PracticeQuestion {
        val effDiff = if (difficulty == PracticeDifficulty.ADAPTIVE) PracticeDifficulty.MEDIUM else difficulty
        return when (category) {
            PracticeCategory.ARITHMETIC -> generateArithmeticQuestion(index, effDiff)
            PracticeCategory.FRACTIONS -> generateFractionQuestion(index, effDiff)
            PracticeCategory.DECIMALS -> generateDecimalQuestion(index, effDiff)
            PracticeCategory.PERCENTAGES -> generatePercentageQuestion(index, effDiff)
            PracticeCategory.RATIOS -> generateRatioQuestion(index, effDiff)
            PracticeCategory.BASIC_ALGEBRA -> generateAlgebraQuestion(index, effDiff)
            PracticeCategory.GEOMETRY -> generateGeometryQuestion(index, effDiff)
            PracticeCategory.NUMBER_THEORY -> generateNumberTheoryQuestion(index, effDiff)
            PracticeCategory.DIVISIBILITY -> generateDivisibilityQuestion(index, effDiff)
            PracticeCategory.SQUARES_ROOTS -> generateSquareQuestion(index, effDiff)
            PracticeCategory.CUBES_ROOTS -> generateCubeQuestion(index, effDiff)
            PracticeCategory.NUMBER_OPERATIONS -> generateBodmasQuestion(index, effDiff)
        }
    }

    // 1. ARITHMETIC
    private fun generateArithmeticQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val a = Random.nextInt(15, 65)
                val b = Random.nextInt(12, 45)
                val ans = a + b
                buildStandardIntQuestion(
                    id = "arith_easy_$index",
                    category = PracticeCategory.ARITHMETIC,
                    difficulty = difficulty,
                    questionBn = "সংখ্যা দুটির যোগফল নির্ণয় করো:",
                    expression = "${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "একক ও দশক স্থানীয় অঙ্ক যোগ করে পাই:",
                        "${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "প্রথমে দশকের অংশ যোগ করে তারপর এককের অঙ্ক যোগ করলে দ্রুত হিসাব হয়।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val n1 = Random.nextInt(10, 25) * 2
                val n2 = n1 + 4
                val n3 = n1 + 8
                val avg = (n1 + n2 + n3) / 3
                buildStandardIntQuestion(
                    id = "arith_med_$index",
                    category = PracticeCategory.ARITHMETIC,
                    difficulty = difficulty,
                    questionBn = "সংখ্যা তিনটির গাণিতিক গড় কত?",
                    expression = "${BengaliNumberFormatter.toBn(n1)}, ${BengaliNumberFormatter.toBn(n2)}, ${BengaliNumberFormatter.toBn(n3)} এর গড় = ?",
                    correctAns = avg,
                    explanation = listOf(
                        "ধাপ ১: সংখ্যাগুলোর সমষ্টি = ${BengaliNumberFormatter.toBn(n1)} + ${BengaliNumberFormatter.toBn(n2)} + ${BengaliNumberFormatter.toBn(n3)} = ${BengaliNumberFormatter.toBn(n1 + n2 + n3)}",
                        "ধাপ ২: গড় = সমষ্টি ÷ ৩ = ${BengaliNumberFormatter.toBn(n1 + n2 + n3)} ÷ ৩ = ${BengaliNumberFormatter.toBn(avg)}"
                    ),
                    tip = "সমপার্থক্যবিশিষ্ট বিজোড় সংখ্যক পদের গড় হলো ঠিক মাঝের সংখ্যাটি (${BengaliNumberFormatter.toBn(n2)})।"
                )
            }
            else -> {
                val unitPrice = Random.nextInt(12, 35)
                val qty1 = Random.nextInt(4, 9)
                val qty2 = Random.nextInt(10, 18)
                val cost1 = unitPrice * qty1
                val ans = unitPrice * qty2
                buildStandardIntQuestion(
                    id = "arith_hard_$index",
                    category = PracticeCategory.ARITHMETIC,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(qty1)}টি কলমের দাম ${BengaliNumberFormatter.toBn(cost1)} টাকা হলে ${BengaliNumberFormatter.toBn(qty2)}টি কলমের দাম কত?",
                    expression = "(${BengaliNumberFormatter.toBn(cost1)} ÷ ${BengaliNumberFormatter.toBn(qty1)}) × ${BengaliNumberFormatter.toBn(qty2)} = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "ধাপ ১ (ঐকিক নিয়ম): ১টি কলমের দাম = ${BengaliNumberFormatter.toBn(cost1)} ÷ ${BengaliNumberFormatter.toBn(qty1)} = ${BengaliNumberFormatter.toBn(unitPrice)} টাকা",
                        "ধাপ ২: ${BengaliNumberFormatter.toBn(qty2)}টি কলমের দাম = ${BengaliNumberFormatter.toBn(unitPrice)} × ${BengaliNumberFormatter.toBn(qty2)} = ${BengaliNumberFormatter.toBn(ans)} টাকা"
                    ),
                    tip = "ঐকিক নিয়মে প্রথমে ১টির মান বের করে তারপর কাঙ্ক্ষিত সংখ্যা দিয়ে গুণ করুন।"
                )
            }
        }
    }

    // 2. FRACTIONS
    private fun generateFractionQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val den = listOf(5, 7, 8, 9, 11, 12).random()
                val a = Random.nextInt(1, den - 1)
                val b = Random.nextInt(1, den - a)
                val sumNum = a + b
                val g = gcd(sumNum, den)
                val simpNum = sumNum / g
                val simpDen = den / g

                val correctBn = if (simpDen == 1) {
                    BengaliNumberFormatter.toBn(simpNum)
                } else {
                    "${BengaliNumberFormatter.toBn(simpNum)}/${BengaliNumberFormatter.toBn(simpDen)}"
                }

                val expr = "${BengaliNumberFormatter.toBn(a)}/${BengaliNumberFormatter.toBn(den)} + ${BengaliNumberFormatter.toBn(b)}/${BengaliNumberFormatter.toBn(den)} = ?"
                val options = generateFractionOptions(simpNum, simpDen)
                val shuffled = options.shuffled()
                val correctIdx = shuffled.indexOf(correctBn)

                PracticeQuestion(
                    id = "frac_easy_$index",
                    category = PracticeCategory.FRACTIONS,
                    difficulty = difficulty,
                    questionBn = "সমহর বিশিষ্ট ভগ্নাংশ দুটির যোগফল নির্ণয় করো:",
                    expression = expr,
                    optionsBn = shuffled,
                    correctIndex = correctIdx,
                    correctAnswerBn = correctBn,
                    stepByStepExplanationBn = listOf(
                        "ধাপ ১: যেহেতু উভয় ভগ্নাংশের হর সমান (${BengaliNumberFormatter.toBn(den)}), তাই লব দুটি সরাসরি যোগ হবে।",
                        "ধাপ ২: লবের যোগফল = ${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(sumNum)}",
                        if (g > 1) "ধাপ ৩: ${BengaliNumberFormatter.toBn(g)} দিয়ে লব ও হরকে ভাগ করে লঘিষ্ঠ আকার = $correctBn"
                        else "ধাপ ৩: নির্ণেয় ফলাফল = $correctBn"
                    ),
                    tipBn = "সমহর ভগ্নাংশের যোগে কখনোই হরগুলোর যোগ করবেন না; হর অপরিবর্তিত থাকে।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val pairs = listOf(Pair(2, 3), Pair(3, 4), Pair(2, 5), Pair(3, 5), Pair(4, 5))
                val (d1, d2) = pairs.random()
                val n1 = 1
                val n2 = 1
                val commonDen = d1 * d2
                val sumNum = (n1 * d2) + (n2 * d1)
                val g = gcd(sumNum, commonDen)
                val simpNum = sumNum / g
                val simpDen = commonDen / g

                val correctBn = "${BengaliNumberFormatter.toBn(simpNum)}/${BengaliNumberFormatter.toBn(simpDen)}"
                val expr = "${BengaliNumberFormatter.toBn(n1)}/${BengaliNumberFormatter.toBn(d1)} + ${BengaliNumberFormatter.toBn(n2)}/${BengaliNumberFormatter.toBn(d2)} = ?"
                val options = generateFractionOptions(simpNum, simpDen)
                val shuffled = options.shuffled()

                PracticeQuestion(
                    id = "frac_med_$index",
                    category = PracticeCategory.FRACTIONS,
                    difficulty = difficulty,
                    questionBn = "ভিন্ন হর বিশিষ্ট ভগ্নাংশ দুটির যোগফল নির্ণয় করো:",
                    expression = expr,
                    optionsBn = shuffled,
                    correctIndex = shuffled.indexOf(correctBn),
                    correctAnswerBn = correctBn,
                    stepByStepExplanationBn = listOf(
                        "ধাপ ১: হর ${BengaliNumberFormatter.toBn(d1)} এবং ${BengaliNumberFormatter.toBn(d2)}-এর ল.সা.গু = ${BengaliNumberFormatter.toBn(commonDen)}",
                        "ধাপ ২: সমহর করে পাই: (${BengaliNumberFormatter.toBn(n1)}×${BengaliNumberFormatter.toBn(d2)} + ${BengaliNumberFormatter.toBn(n2)}×${BengaliNumberFormatter.toBn(d1)}) / ${BengaliNumberFormatter.toBn(commonDen)}",
                        "ধাপ ৩: যোগফল = (${BengaliNumberFormatter.toBn(d2)} + ${BengaliNumberFormatter.toBn(d1)}) / ${BengaliNumberFormatter.toBn(commonDen)} = $correctBn"
                    ),
                    tipBn = "১/a + ১/b আকারের ভগ্নাংশের দ্রুত যোগফল = (a + b) / (a × b)।"
                )
            }
            else -> {
                val n1 = Random.nextInt(2, 6)
                val d1 = Random.nextInt(3, 8)
                val n2 = Random.nextInt(2, 6)
                val d2 = Random.nextInt(3, 8)
                val prodNum = n1 * n2
                val prodDen = d1 * d2
                val g = gcd(prodNum, prodDen)
                val simpNum = prodNum / g
                val simpDen = prodDen / g

                val correctBn = if (simpDen == 1) {
                    BengaliNumberFormatter.toBn(simpNum)
                } else {
                    "${BengaliNumberFormatter.toBn(simpNum)}/${BengaliNumberFormatter.toBn(simpDen)}"
                }
                val expr = "${BengaliNumberFormatter.toBn(n1)}/${BengaliNumberFormatter.toBn(d1)} × ${BengaliNumberFormatter.toBn(n2)}/${BengaliNumberFormatter.toBn(d2)} = ?"
                val options = generateFractionOptions(simpNum, simpDen)
                val shuffled = options.shuffled()

                PracticeQuestion(
                    id = "frac_hard_$index",
                    category = PracticeCategory.FRACTIONS,
                    difficulty = difficulty,
                    questionBn = "ভগ্নাংশ দুটির গুণফল লঘিষ্ঠ আকারে নির্ণয় করো:",
                    expression = expr,
                    optionsBn = shuffled,
                    correctIndex = shuffled.indexOf(correctBn),
                    correctAnswerBn = correctBn,
                    stepByStepExplanationBn = listOf(
                        "ধাপ ১: লবগুলোর গুণফল = ${BengaliNumberFormatter.toBn(n1)} × ${BengaliNumberFormatter.toBn(n2)} = ${BengaliNumberFormatter.toBn(prodNum)}",
                        "ধাপ ২: হরগুলোর গুণফল = ${BengaliNumberFormatter.toBn(d1)} × ${BengaliNumberFormatter.toBn(d2)} = ${BengaliNumberFormatter.toBn(prodDen)}",
                        "ধাপ ৩: গ.সা.গু (${BengaliNumberFormatter.toBn(g)}) দ্বারা ভাগ করে লঘিষ্ঠ রূপ = $correctBn"
                    ),
                    tipBn = "গুণ করার আগেই কোণাকুণি সাধারণ উৎপাদক দিয়ে কাটাকাটি করে নিলে হিসাব অনেক সহজ হয়।"
                )
            }
        }
    }

    // 3. DECIMALS
    private fun generateDecimalQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val aTenths = Random.nextInt(12, 65)
                val bTenths = Random.nextInt(11, 55)
                val sumTenths = aTenths + bTenths
                val aStr = formatTenthsBn(aTenths)
                val bStr = formatTenthsBn(bTenths)
                val correctBn = formatTenthsBn(sumTenths)

                val distractors = listOf(
                    formatTenthsBn(sumTenths + 10),
                    formatTenthsBn((sumTenths - 5).coerceAtLeast(5)),
                    formatTenthsBn(sumTenths + 2),
                    formatTenthsBn(sumTenths + 4),
                    formatTenthsBn(sumTenths + 7)
                ).distinct().filter { it != correctBn }.take(3)
                val allOpts = (distractors + correctBn).shuffled()

                PracticeQuestion(
                    id = "dec_easy_$index",
                    category = PracticeCategory.DECIMALS,
                    difficulty = difficulty,
                    questionBn = "দশমিক সংখ্যা দুটির যোগফল কত?",
                    expression = "$aStr + $bStr = ?",
                    optionsBn = allOpts,
                    correctIndex = allOpts.indexOf(correctBn),
                    correctAnswerBn = correctBn,
                    stepByStepExplanationBn = listOf(
                        "ধাপ ১: দশমিক বিন্দু বরাবর সংখ্যা দুটি নিচে নিচে সাজান।",
                        "ধাপ ২: ডানদিক (দশমাংশ) থেকে যোগ শুরু করে পূর্ণসংখ্যায় হাতে রাখুন।",
                        "ধাপ ৩: $aStr + $bStr = $correctBn"
                    ),
                    tipBn = "দশমিকের যোগ ও বিয়োগে দশমিক বিন্দুগুলোকে সবসময় একই সোজা কলামে রাখবেন।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val aTenths = Random.nextInt(4, 35)
                val mult = listOf(10, 100).random()
                val resultVal = if (mult == 10) {
                    BengaliNumberFormatter.toBn(aTenths)
                } else {
                    BengaliNumberFormatter.toBn(aTenths * 10)
                }
                val aStr = formatTenthsBn(aTenths)
                val multBn = BengaliNumberFormatter.toBn(mult)

                val distractors = listOf(
                    formatTenthsBn(aTenths),
                    BengaliNumberFormatter.toBn(aTenths * 100),
                    BengaliNumberFormatter.toBn(aTenths + 10),
                    BengaliNumberFormatter.toBn(aTenths * 10 + 5),
                    formatTenthsBn(aTenths + 10)
                ).distinct().filter { it != resultVal }.take(3)
                val allOpts = (distractors + resultVal).shuffled()

                PracticeQuestion(
                    id = "dec_med_$index",
                    category = PracticeCategory.DECIMALS,
                    difficulty = difficulty,
                    questionBn = "দশমিক সংখ্যাটির গুণফল নির্ণয় করো:",
                    expression = "$aStr × $multBn = ?",
                    optionsBn = allOpts,
                    correctIndex = allOpts.indexOf(resultVal),
                    correctAnswerBn = resultVal,
                    stepByStepExplanationBn = listOf(
                        "ধাপ ১: ১০ বা ১০০ দিয়ে গুণ করলে দশমিক বিন্দু শূন্যের সমান সংখ্যক ঘর ডানে সরে যায়।",
                        "ধাপ ২: $multBn-এ ${if (mult == 10) "১টি" else "২টি"} শূন্য আছে, তাই বিন্দুটি ডানে সরিয়ে পাই = $resultVal"
                    ),
                    tipBn = "১০ⁿ দিয়ে গুণ করলে দশমিক বিন্দু n ঘর ডানে সরে, আর ভাগ করলে n ঘর বামে সরে।"
                )
            }
            else -> {
                val a = Random.nextInt(2, 9)
                val b = Random.nextInt(2, 9)
                val prodHundredths = a * b
                val aBn = "০.${BengaliNumberFormatter.toBn(a)}"
                val bBn = "০.${BengaliNumberFormatter.toBn(b)}"
                val correctBn = formatHundredthsBn(prodHundredths)

                val distractors = listOf(
                    formatTenthsBn(prodHundredths),
                    "০.০০${BengaliNumberFormatter.toBn(prodHundredths)}",
                    formatHundredthsBn(prodHundredths + a),
                    formatHundredthsBn(prodHundredths + b + 1),
                    BengaliNumberFormatter.toBn(prodHundredths)
                ).distinct().filter { it != correctBn }.take(3)
                val allOpts = (distractors + correctBn).shuffled()

                PracticeQuestion(
                    id = "dec_hard_$index",
                    category = PracticeCategory.DECIMALS,
                    difficulty = difficulty,
                    questionBn = "দশমিকের গুণফল নির্ণয় করো:",
                    expression = "$aBn × $bBn = ?",
                    optionsBn = allOpts,
                    correctIndex = allOpts.indexOf(correctBn),
                    correctAnswerBn = correctBn,
                    stepByStepExplanationBn = listOf(
                        "ধাপ ১: দশমিক ছাড়া পূর্ণসংখ্যা গুণ করুন: ${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(prodHundredths)}",
                        "ধাপ ২: উভয় সংখ্যায় দশমিকের পরে মোট ঘর সংখ্যা = ১ + ১ = ২ ঘর।",
                        "ধাপ ৩: ডানদিক থেকে ২ ঘর বামে দশমিক বসিয়ে পাই = $correctBn"
                    ),
                    tipBn = "বিসিএস পরীক্ষায় ০.১ × ০.১ = ০.০১ ধরণের প্রশ্ন প্রায়ই আসে—দশমিকের পরের মোট অঙ্ক সংখ্যা যোগ করতে ভুলবেন না!"
                )
            }
        }
    }

    // 4. PERCENTAGES
    private fun generatePercentageQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val pct = listOf(10, 20, 25, 50).random()
                val base = listOf(40, 60, 80, 100, 120, 200).random()
                val ans = (base * pct) / 100
                buildStandardIntQuestion(
                    id = "pct_easy_$index",
                    category = PracticeCategory.PERCENTAGES,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(base)} এর ${BengaliNumberFormatter.toBn(pct)}% কত?",
                    expression = "${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(pct)}% = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "ধাপ ১: ${BengaliNumberFormatter.toBn(pct)}% অর্থ হলো ${BengaliNumberFormatter.toBn(pct)} / ১০০",
                        "ধাপ ২: (${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(pct)}) ÷ ১০০ = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "২৫% মানে চার ভাগের এক ভাগ (÷ ৪) এবং ৫০% মানে অর্ধেক (÷ ২)।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val pct = listOf(15, 30, 40, 75).random()
                val base = listOf(80, 120, 160, 240, 300).random()
                val ans = (base * pct) / 100
                buildStandardIntQuestion(
                    id = "pct_med_$index",
                    category = PracticeCategory.PERCENTAGES,
                    difficulty = difficulty,
                    questionBn = "মান নির্ণয় করো: ${BengaliNumberFormatter.toBn(base)}-এর ${BengaliNumberFormatter.toBn(pct)}%",
                    expression = "(${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(pct)}) ÷ ১০০ = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "ধাপ ১: প্রথমে ১০% বের করি = ${BengaliNumberFormatter.toBn(base / 10)}",
                        "ধাপ ২: ${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(pct)} / ১০০ = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "যেকোনো সংখ্যার ১০% বের করতে শেষ থেকে এক ঘর আগে দশমিক বসান, তারপর প্রয়োজনমতো গুণ করুন।"
                )
            }
            else -> {
                val base = listOf(200, 300, 400, 500, 600).random()
                val pct = listOf(10, 15, 20, 25).random()
                val isIncrease = Random.nextBoolean()
                val delta = (base * pct) / 100
                val ans = if (isIncrease) base + delta else base - delta
                val actionWord = if (isIncrease) "বৃদ্ধি" else "হ্রাস (ছাড়)"
                val opSign = if (isIncrease) "+" else "−"

                buildStandardIntQuestion(
                    id = "pct_hard_$index",
                    category = PracticeCategory.PERCENTAGES,
                    difficulty = difficulty,
                    questionBn = "একটি পণ্যের মূল্য ${BengaliNumberFormatter.toBn(base)} টাকা। মূল্য ${BengaliNumberFormatter.toBn(pct)}% $actionWord পেলে নতুন মূল্য কত?",
                    expression = "${BengaliNumberFormatter.toBn(base)} $opSign (${BengaliNumberFormatter.toBn(base)} এর ${BengaliNumberFormatter.toBn(pct)}%) = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "ধাপ ১: ${BengaliNumberFormatter.toBn(base)} টাকার ${BengaliNumberFormatter.toBn(pct)}% = (${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(pct)}) ÷ ১০০ = ${BengaliNumberFormatter.toBn(delta)} টাকা",
                        "ধাপ ২: নতুন মূল্য = ${BengaliNumberFormatter.toBn(base)} $opSign ${BengaliNumberFormatter.toBn(delta)} = ${BengaliNumberFormatter.toBn(ans)} টাকা"
                    ),
                    tip = "${BengaliNumberFormatter.toBn(pct)}% $actionWord মানে সরাসরি মূল সংখ্যার ${BengaliNumberFormatter.toBn(if (isIncrease) 100 + pct else 100 - pct)}% বের করা।"
                )
            }
        }
    }

    // 5. RATIOS
    private fun generateRatioQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(a + 1, 9)
                if (gcd(a, b) != 1) return generateRatioQuestion(index, difficulty)
                val k = Random.nextInt(2, 7)
                val leftA = a * k
                val leftB = b * k
                val correctBn = "${BengaliNumberFormatter.toBn(a)} : ${BengaliNumberFormatter.toBn(b)}"

                val distractors = listOf(
                    "${BengaliNumberFormatter.toBn(b)} : ${BengaliNumberFormatter.toBn(a)}",
                    "${BengaliNumberFormatter.toBn(a + 1)} : ${BengaliNumberFormatter.toBn(b)}",
                    "${BengaliNumberFormatter.toBn(a)} : ${BengaliNumberFormatter.toBn(b + 1)}"
                ).distinct().filter { it != correctBn }.take(3)
                val allOpts = (distractors + correctBn).shuffled()

                PracticeQuestion(
                    id = "ratio_easy_$index",
                    category = PracticeCategory.RATIOS,
                    difficulty = difficulty,
                    questionBn = "অনুপাতটিকে লঘিষ্ঠ (সরলতম) আকারে প্রকাশ করো:",
                    expression = "${BengaliNumberFormatter.toBn(leftA)} : ${BengaliNumberFormatter.toBn(leftB)} = ?",
                    optionsBn = allOpts,
                    correctIndex = allOpts.indexOf(correctBn),
                    correctAnswerBn = correctBn,
                    stepByStepExplanationBn = listOf(
                        "ধাপ ১: ${BengaliNumberFormatter.toBn(leftA)} এবং ${BengaliNumberFormatter.toBn(leftB)} এর গ.সা.গু = ${BengaliNumberFormatter.toBn(k)}",
                        "ধাপ ২: উভয় পদকে ${BengaliNumberFormatter.toBn(k)} দ্বারা ভাগ করে পাই = $correctBn"
                    ),
                    tipBn = "অনুপাতের উভয় পদকে তাদের গরিষ্ঠ সাধারণ গুণনীয়ক (গ.সা.গু) দিয়ে ভাগ করলে সরলতম অনুপাত পাওয়া যায়।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(3, 8)
                val mult = Random.nextInt(3, 7)
                val c = a * mult
                val x = b * mult
                buildStandardIntQuestion(
                    id = "ratio_med_$index",
                    category = PracticeCategory.RATIOS,
                    difficulty = difficulty,
                    questionBn = "সমানুপাত হতে x এর মান নির্ণয় করো:",
                    expression = "${BengaliNumberFormatter.toBn(a)} : ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(c)} : x",
                    correctAns = x,
                    explanation = listOf(
                        "ধাপ ১: ১ম পদ ${BengaliNumberFormatter.toBn(a)} কে ${BengaliNumberFormatter.toBn(mult)} দিয়ে গুণ করে ${BengaliNumberFormatter.toBn(c)} পাওয়া গেছে।",
                        "ধাপ ২: তাই ২য় পদ ${BengaliNumberFormatter.toBn(b)} কেও ${BengaliNumberFormatter.toBn(mult)} দিয়ে গুণ করতে হবে।",
                        "ধাপ ৩: x = ${BengaliNumberFormatter.toBn(b)} × ${BengaliNumberFormatter.toBn(mult)} = ${BengaliNumberFormatter.toBn(x)}"
                    ),
                    tip = "সমানুপাতের নিয়ম: ১ম × ৪র্থ পদ = ২য় × ৩য় পদ।"
                )
            }
            else -> {
                val r1 = Random.nextInt(2, 5)
                val r2 = Random.nextInt(3, 7)
                val sumRatio = r1 + r2
                val unitVal = Random.nextInt(5, 15)
                val total = sumRatio * unitVal
                val largerShare = maxOf(r1, r2) * unitVal
                buildStandardIntQuestion(
                    id = "ratio_hard_$index",
                    category = PracticeCategory.RATIOS,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(total)} টাকা ${BengaliNumberFormatter.toBn(r1)} : ${BengaliNumberFormatter.toBn(r2)} অনুপাতে ভাগ করলে বৃহত্তম অংশ কত টাকা হবে?",
                    expression = "${BengaliNumberFormatter.toBn(total)} × (${BengaliNumberFormatter.toBn(maxOf(r1, r2))} / ${BengaliNumberFormatter.toBn(sumRatio)}) = ?",
                    correctAns = largerShare,
                    explanation = listOf(
                        "ধাপ ১: অনুপাতের রাশিদ্বয়ের যোগফল = ${BengaliNumberFormatter.toBn(r1)} + ${BengaliNumberFormatter.toBn(r2)} = ${BengaliNumberFormatter.toBn(sumRatio)}",
                        "ধাপ ২: ১ ভাগের মান = ${BengaliNumberFormatter.toBn(total)} ÷ ${BengaliNumberFormatter.toBn(sumRatio)} = ${BengaliNumberFormatter.toBn(unitVal)} টাকা",
                        "ধাপ ৩: বৃহত্তম অংশ = ${BengaliNumberFormatter.toBn(maxOf(r1, r2))} × ${BengaliNumberFormatter.toBn(unitVal)} = ${BengaliNumberFormatter.toBn(largerShare)} টাকা"
                    ),
                    tip = "আনুপাতিক ভাগ = মোট পরিমাণ × (নিজের অনুপাত ÷ অনুপাতগুলোর যোগফল)।"
                )
            }
        }
    }

    // 6. BASIC ALGEBRA
    private fun generateAlgebraQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val isAdd = Random.nextBoolean()
                if (isAdd) {
                    val x = Random.nextInt(4, 25)
                    val a = Random.nextInt(5, 30)
                    val b = x + a
                    buildStandardIntQuestion(
                        id = "alg_easy_$index",
                        category = PracticeCategory.BASIC_ALGEBRA,
                        difficulty = difficulty,
                        questionBn = "সমীকরণটি সমাধান করে x এর মান নির্ণয় করো:",
                        expression = "x + ${BengaliNumberFormatter.toBn(a)} = ${BengaliNumberFormatter.toBn(b)}  ⇒  x = ?",
                        correctAns = x,
                        explanation = listOf(
                            "x + ${BengaliNumberFormatter.toBn(a)} = ${BengaliNumberFormatter.toBn(b)}",
                            "পক্ষান্তর করে: x = ${BengaliNumberFormatter.toBn(b)} − ${BengaliNumberFormatter.toBn(a)}",
                            "x = ${BengaliNumberFormatter.toBn(x)}"
                        ),
                        tip = "বাম পাশের যোগ রাশি ডান পাশে গেলে বিয়োগ চিহ্নে রূপান্তরিত হয়।"
                    )
                } else {
                    val x = Random.nextInt(3, 15)
                    val a = Random.nextInt(2, 8)
                    val b = a * x
                    buildStandardIntQuestion(
                        id = "alg_easy_$index",
                        category = PracticeCategory.BASIC_ALGEBRA,
                        difficulty = difficulty,
                        questionBn = "x এর মান কত?",
                        expression = "${BengaliNumberFormatter.toBn(a)}x = ${BengaliNumberFormatter.toBn(b)}  ⇒  x = ?",
                        correctAns = x,
                        explanation = listOf(
                            "${BengaliNumberFormatter.toBn(a)}x = ${BengaliNumberFormatter.toBn(b)}",
                            "উভয় পক্ষকে ${BengaliNumberFormatter.toBn(a)} দিয়ে ভাগ করে:",
                            "x = ${BengaliNumberFormatter.toBn(b)} ÷ ${BengaliNumberFormatter.toBn(a)} = ${BengaliNumberFormatter.toBn(x)}"
                        ),
                        tip = "চলকের সাথে গুণিতক থাকলে পক্ষান্তর করার পর তা ভাগে পরিণত হয়।"
                    )
                }
            }
            PracticeDifficulty.MEDIUM -> {
                val x = Random.nextInt(3, 12)
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(3, 20)
                val c = (a * x) + b
                buildStandardIntQuestion(
                    id = "alg_med_$index",
                    category = PracticeCategory.BASIC_ALGEBRA,
                    difficulty = difficulty,
                    questionBn = "সমীকরণটির মূল (x এর মান) নির্ণয় করো:",
                    expression = "${BengaliNumberFormatter.toBn(a)}x + ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(c)}  ⇒  x = ?",
                    correctAns = x,
                    explanation = listOf(
                        "ধাপ ১: ${BengaliNumberFormatter.toBn(a)}x = ${BengaliNumberFormatter.toBn(c)} − ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(c - b)}",
                        "ধাপ ২: x = ${BengaliNumberFormatter.toBn(c - b)} ÷ ${BengaliNumberFormatter.toBn(a)}",
                        "সুতরাং x = ${BengaliNumberFormatter.toBn(x)}"
                    ),
                    tip = "প্রথমে ধ্রুবক পদ পক্ষান্তর করুন, তারপর চলকের সহগ দিয়ে ভাগ করুন।"
                )
            }
            else -> {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                val ans = (a * a) + (2 * a * b) + (b * b)
                buildStandardIntQuestion(
                    id = "alg_hard_$index",
                    category = PracticeCategory.BASIC_ALGEBRA,
                    difficulty = difficulty,
                    questionBn = "যদি a = ${BengaliNumberFormatter.toBn(a)} এবং b = ${BengaliNumberFormatter.toBn(b)} হয়, তবে a² + 2ab + b² এর মান কত?",
                    expression = "a = ${BengaliNumberFormatter.toBn(a)}, b = ${BengaliNumberFormatter.toBn(b)}  ⇒  (a + b)² = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "সূত্র: a² + 2ab + b² = (a + b)²",
                        "= (${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)})² = ${BengaliNumberFormatter.toBn(a + b)}²",
                        "= ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "আলাদা মান বসানোর বদলে অভেদের সূত্র (a + b)² প্রয়োগ করলে হিসাব নিমেষেই শেষ হয়।"
                )
            }
        }
    }

    // 7. GEOMETRY & MENSURATION
    private fun generateGeometryQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val l = Random.nextInt(6, 16)
                val w = Random.nextInt(4, 12)
                val area = l * w
                buildStandardIntQuestion(
                    id = "geo_easy_$index",
                    category = PracticeCategory.GEOMETRY,
                    difficulty = difficulty,
                    questionBn = "একটি আয়তক্ষেত্রের দৈর্ঘ্য ${BengaliNumberFormatter.toBn(l)} সেমি ও প্রস্থ ${BengaliNumberFormatter.toBn(w)} সেমি হলে এর ক্ষেত্রফল কত বর্গ সেমি?",
                    expression = "ক্ষেত্রফল = ${BengaliNumberFormatter.toBn(l)} × ${BengaliNumberFormatter.toBn(w)} = ?",
                    correctAns = area,
                    explanation = listOf(
                        "সূত্র: আয়তক্ষেত্রের ক্ষেত্রফল = দৈর্ঘ্য × প্রস্থ",
                        "= ${BengaliNumberFormatter.toBn(l)} × ${BengaliNumberFormatter.toBn(w)} = ${BengaliNumberFormatter.toBn(area)} বর্গ সেমি"
                    ),
                    tip = "আয়তক্ষেত্রের ক্ষেত্রফল = দৈর্ঘ্য × প্রস্থ এবং পরিসীমা = ২(দৈর্ঘ্য + প্রস্থ)।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val base = Random.nextInt(4, 12) * 2
                val height = Random.nextInt(5, 15)
                val area = (base * height) / 2
                buildStandardIntQuestion(
                    id = "geo_med_$index",
                    category = PracticeCategory.GEOMETRY,
                    difficulty = difficulty,
                    questionBn = "একটি ত্রিভুজের ভূমি ${BengaliNumberFormatter.toBn(base)} সেমি এবং উচ্চতা ${BengaliNumberFormatter.toBn(height)} সেমি হলে ক্ষেত্রফল কত?",
                    expression = "½ × ${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(height)} = ?",
                    correctAns = area,
                    explanation = listOf(
                        "সূত্র: ত্রিভুজের ক্ষেত্রফল = ½ × ভূমি × উচ্চতা",
                        "= ½ × ${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(height)} = ${BengaliNumberFormatter.toBn(area)} বর্গ সেমি"
                    ),
                    tip = "ভূমি বা উচ্চতার যেকোনো একটি জোড় সংখ্যাকে আগে ২ দিয়ে ভাগ করে নিলে গুণ সহজ হয়।"
                )
            }
            else -> {
                val triplets = listOf(Triple(3, 4, 5), Triple(5, 12, 13), Triple(6, 8, 10), Triple(8, 15, 17), Triple(9, 12, 15))
                val (a, b, c) = triplets.random()
                buildStandardIntQuestion(
                    id = "geo_hard_$index",
                    category = PracticeCategory.GEOMETRY,
                    difficulty = difficulty,
                    questionBn = "সমকোণী ত্রিভুজের লম্ব ${BengaliNumberFormatter.toBn(a)} একক এবং ভূমি ${BengaliNumberFormatter.toBn(b)} একক হলে অতিভুজের দৈর্ঘ্য কত?",
                    expression = "√(${BengaliNumberFormatter.toBn(a)}² + ${BengaliNumberFormatter.toBn(b)}²) = ?",
                    correctAns = c,
                    explanation = listOf(
                        "পিথাগোরাসের উপপাদ্য: অতিভুজ² = লম্ব² + ভূমি²",
                        "= ${BengaliNumberFormatter.toBn(a)}² + ${BengaliNumberFormatter.toBn(b)}² = ${BengaliNumberFormatter.toBn(a * a)} + ${BengaliNumberFormatter.toBn(b * b)} = ${BengaliNumberFormatter.toBn(c * c)}",
                        "অতিভুজ = √${BengaliNumberFormatter.toBn(c * c)} = ${BengaliNumberFormatter.toBn(c)}"
                    ),
                    tip = "পিথাগোরাসীয় ত্রয়ী (3,4,5), (5,12,13), (8,15,17) মনে রাখলে হিসাব ছাড়াই উত্তর দেওয়া যায়।"
                )
            }
        }
    }

    // 8. NUMBER THEORY
    private fun generateNumberTheoryQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val g = Random.nextInt(3, 9)
                val a = g * 2
                val b = g * 3
                buildStandardIntQuestion(
                    id = "nt_easy_$index",
                    category = PracticeCategory.NUMBER_THEORY,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(a)} এবং ${BengaliNumberFormatter.toBn(b)} এর গ.সা.গু (HCF) কত?",
                    expression = "GCD(${BengaliNumberFormatter.toBn(a)}, ${BengaliNumberFormatter.toBn(b)}) = ?",
                    correctAns = g,
                    explanation = listOf(
                        "${BengaliNumberFormatter.toBn(a)} = ${BengaliNumberFormatter.toBn(g)} × ২",
                        "${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(g)} × ৩",
                        "সাধারণ গরিষ্ঠ উৎপাদক (গ.সা.গু) = ${BengaliNumberFormatter.toBn(g)}"
                    ),
                    tip = "দুটি সংখ্যার বিয়োগফল দ্বারা উভয় সংখ্যা বিভাজ্য হলে বিয়োগফলই তাদের গ.সা.গু।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val g = Random.nextInt(2, 6)
                val a = g * 3
                val b = g * 4
                val lcmVal = g * 12
                buildStandardIntQuestion(
                    id = "nt_med_$index",
                    category = PracticeCategory.NUMBER_THEORY,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(a)} এবং ${BengaliNumberFormatter.toBn(b)} এর ল.সা.গু (LCM) কত?",
                    expression = "LCM(${BengaliNumberFormatter.toBn(a)}, ${BengaliNumberFormatter.toBn(b)}) = ?",
                    correctAns = lcmVal,
                    explanation = listOf(
                        "সংখ্যা দুটির গ.সা.গু = ${BengaliNumberFormatter.toBn(g)}",
                        "ল.সা.গু = (${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)}) ÷ ${BengaliNumberFormatter.toBn(g)} = ${BengaliNumberFormatter.toBn(lcmVal)}"
                    ),
                    tip = "ল.সা.গু = (সংখ্যা দুটির গুণফল) ÷ গ.সা.গু।"
                )
            }
            else -> {
                val nums = listOf(12, 18, 24, 36, 48, 60)
                val n = nums.random()
                val divCount = NumberSystemProvider.getFactors(n.toLong()).size
                buildStandardIntQuestion(
                    id = "nt_hard_$index",
                    category = PracticeCategory.NUMBER_THEORY,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(n)} সংখ্যাটির মোট গুণনীয়ক (ভাজক) কয়টি?",
                    expression = "d(${BengaliNumberFormatter.toBn(n)}) = ?",
                    correctAns = divCount,
                    explanation = listOf(
                        "${BengaliNumberFormatter.toBn(n)} এর মৌলিক উৎপাদক ঘাত বিশ্লেষণ: ${SharedMathEngine.formatPrimePowerBn(n.toLong())}",
                        "ভাজকগুলো: ${NumberSystemProvider.getFactors(n.toLong()).joinToString(", ") { BengaliNumberFormatter.toBn(it) }}",
                        "মোট ভাজক সংখ্যা = ${BengaliNumberFormatter.toBn(divCount)}টি"
                    ),
                    tip = "মৌলিক উৎপাদকের ঘাতগুলোর সাথে ১ যোগ করে গুণ করলে মোট ভাজক সংখ্যা পাওয়া যায়।"
                )
            }
        }
    }

    // 9. DIVISIBILITY
    private fun generateDivisibilityQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val q = Random.nextInt(12, 45)
                val r = Random.nextInt(1, 5)
                val n = q * 5 + r
                buildStandardIntQuestion(
                    id = "div_easy_$index",
                    category = PracticeCategory.DIVISIBILITY,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(n)} কে ৫ দ্বারা ভাগ করলে ভাগশেষ কত থাকবে?",
                    expression = "${BengaliNumberFormatter.toBn(n)} mod ৫ = ?",
                    correctAns = r,
                    explanation = listOf(
                        "${BengaliNumberFormatter.toBn(n)} = ৫ × ${BengaliNumberFormatter.toBn(q)} + ${BengaliNumberFormatter.toBn(r)}",
                        "সুতরাং ভাগশেষ = ${BengaliNumberFormatter.toBn(r)}"
                    ),
                    tip = "কোনো সংখ্যাকে ৫ দ্বারা ভাগ করলে তার একক অঙ্ককে ৫ দ্বারা ভাগ করলেই ভাগশেষ পাওয়া যায়।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val d1 = Random.nextInt(2, 8)
                val d2 = Random.nextInt(1, 7)
                val partialSum = d1 + d2
                val nextMult9 = ((partialSum / 9) + 1) * 9
                val missingDigit = nextMult9 - partialSum
                buildStandardIntQuestion(
                    id = "div_med_$index",
                    category = PracticeCategory.DIVISIBILITY,
                    difficulty = difficulty,
                    questionBn = "${BengaliNumberFormatter.toBn(d1)}${BengaliNumberFormatter.toBn(d2)}* সংখ্যাটি ৯ দ্বারা নিঃশেষে বিভাজ্য হলে * চিহ্নিত স্থানে কোন অঙ্ক বসবে?",
                    expression = "(${BengaliNumberFormatter.toBn(d1)} + ${BengaliNumberFormatter.toBn(d2)} + *) ÷ ৯ নিঃশেষে বিভাজ্য ⇒ * = ?",
                    correctAns = missingDigit,
                    explanation = listOf(
                        "৯ দ্বারা বিভাজ্যতার শর্ত: অঙ্কগুলোর যোগফল ৯ দ্বারা বিভাজ্য হতে হবে।",
                        "প্রদত্ত অঙ্কদ্বয়ের যোগফল = ${BengaliNumberFormatter.toBn(d1)} + ${BengaliNumberFormatter.toBn(d2)} = ${BengaliNumberFormatter.toBn(partialSum)}",
                        "৯ এর পরবর্তী গুণিতক ${BengaliNumberFormatter.toBn(nextMult9)} পেতে * = ${BengaliNumberFormatter.toBn(nextMult9)} − ${BengaliNumberFormatter.toBn(partialSum)} = ${BengaliNumberFormatter.toBn(missingDigit)}"
                    ),
                    tip = "৯ দ্বারা বিভাজ্য হতে হলে সকল অঙ্কের সমষ্টি ৯, ১৮, ২৭... হতে হবে।"
                )
            }
            else -> {
                // Least number divisible by 4, 6 with remainder
                val rem = Random.nextInt(1, 4)
                val lcmVal = 12 * Random.nextInt(2, 5)
                val ans = lcmVal + rem
                buildStandardIntQuestion(
                    id = "div_hard_$index",
                    category = PracticeCategory.DIVISIBILITY,
                    difficulty = difficulty,
                    questionBn = "কোন সংখ্যার ল.সা.গু অংশ ${BengaliNumberFormatter.toBn(lcmVal)} এবং প্রতি ক্ষেত্রে ${BengaliNumberFormatter.toBn(rem)} অবশিষ্ট থাকলে সংখ্যাটি কত?",
                    expression = "ল.সা.গু (${BengaliNumberFormatter.toBn(lcmVal)}) + অবশিষ্ট (${BengaliNumberFormatter.toBn(rem)}) = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "ধাপ ১: নিঃশেষে বিভাজ্য সংখ্যাটি = ${BengaliNumberFormatter.toBn(lcmVal)}",
                        "ধাপ ২: প্রতি ক্ষেত্রে ${BengaliNumberFormatter.toBn(rem)} অবশিষ্ট থাকায় নির্ণেয় সংখ্যা = ${BengaliNumberFormatter.toBn(lcmVal)} + ${BengaliNumberFormatter.toBn(rem)} = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "প্রতি ক্ষেত্রে r অবশিষ্ট থাকলে নির্ণেয় ক্ষুদ্রতম সংখ্যা = ল.সা.গু + r।"
                )
            }
        }
    }

    // 10. SQUARES & EXPONENTS
    private fun generateSquareQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val n = Random.nextInt(2, 13)
                val sq = n * n
                val isRoot = Random.nextBoolean()
                if (isRoot) {
                    buildStandardIntQuestion(
                        id = "sq_easy_$index",
                        category = PracticeCategory.SQUARES_ROOTS,
                        difficulty = difficulty,
                        questionBn = "বর্গমূল নির্ণয় করো:",
                        expression = "√${BengaliNumberFormatter.toBn(sq)} = ?",
                        correctAns = n,
                        explanation = listOf(
                            "আমরা জানি, ${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)} = ${BengaliNumberFormatter.toBn(sq)}",
                            "সুতরাং √${BengaliNumberFormatter.toBn(sq)} = ${BengaliNumberFormatter.toBn(n)}"
                        ),
                        tip = "১ থেকে ২০ পর্যন্ত সংখ্যার বর্গ মুখস্থ থাকলে বর্গমূল ১ সেকেন্ডেই বের করা যায়।"
                    )
                } else {
                    buildStandardIntQuestion(
                        id = "sq_easy_$index",
                        category = PracticeCategory.SQUARES_ROOTS,
                        difficulty = difficulty,
                        questionBn = "সংখ্যাটির বর্গ কত?",
                        expression = "${BengaliNumberFormatter.toBn(n)}² = ?",
                        correctAns = sq,
                        explanation = listOf(
                            "${BengaliNumberFormatter.toBn(n)}² = ${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)}",
                            "= ${BengaliNumberFormatter.toBn(sq)}"
                        ),
                        tip = "কোনো সংখ্যার বর্গ মানে সংখ্যাটিকে ওই সংখ্যা দিয়েই গুণ করা।"
                    )
                }
            }
            PracticeDifficulty.MEDIUM -> {
                val n = Random.nextInt(13, 26)
                val sq = n * n
                buildStandardIntQuestion(
                    id = "sq_med_$index",
                    category = PracticeCategory.SQUARES_ROOTS,
                    difficulty = difficulty,
                    questionBn = "বর্গফল নির্ণয় করো:",
                    expression = "${BengaliNumberFormatter.toBn(n)}² = ?",
                    correctAns = sq,
                    explanation = listOf(
                        "সূত্র (a + b)² ব্যবহার করে বা সরাসরি গুণ করে:",
                        "${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)} = ${BengaliNumberFormatter.toBn(sq)}"
                    ),
                    tip = "এককের ঘরে ৫ থাকলে বর্গ করার শর্টকাট: দশকের অঙ্ক n হলে ফলাফল হবে n(n+1) এবং শেষে ২৫।"
                )
            }
            else -> {
                val triplets = listOf(Triple(3, 4, 5), Triple(5, 12, 13), Triple(6, 8, 10), Triple(8, 15, 17))
                val (a, b, c) = triplets.random()
                buildStandardIntQuestion(
                    id = "sq_hard_$index",
                    category = PracticeCategory.SQUARES_ROOTS,
                    difficulty = difficulty,
                    questionBn = "সরল মান নির্ণয় করো:",
                    expression = "√(${BengaliNumberFormatter.toBn(a)}² + ${BengaliNumberFormatter.toBn(b)}²) = ?",
                    correctAns = c,
                    explanation = listOf(
                        "ধাপ ১: ${BengaliNumberFormatter.toBn(a)}² = ${BengaliNumberFormatter.toBn(a * a)} এবং ${BengaliNumberFormatter.toBn(b)}² = ${BengaliNumberFormatter.toBn(b * b)}",
                        "ধাপ ২: যোগফল = ${BengaliNumberFormatter.toBn(a * a)} + ${BengaliNumberFormatter.toBn(b * b)} = ${BengaliNumberFormatter.toBn(c * c)}",
                        "ধাপ ৩: √${BengaliNumberFormatter.toBn(c * c)} = ${BengaliNumberFormatter.toBn(c)}"
                    ),
                    tip = "এটি পিথাগোরাসের ত্রয়ী (${BengaliNumberFormatter.toBn(a)}, ${BengaliNumberFormatter.toBn(b)}, ${BengaliNumberFormatter.toBn(c)})।"
                )
            }
        }
    }

    // 11. CUBES & ROOTS
    private fun generateCubeQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val n = Random.nextInt(2, 8)
                val cube = n * n * n
                buildStandardIntQuestion(
                    id = "cube_easy_$index",
                    category = PracticeCategory.CUBES_ROOTS,
                    difficulty = difficulty,
                    questionBn = "সংখ্যাটির ঘন (Cube) কত?",
                    expression = "${BengaliNumberFormatter.toBn(n)}³ = ?",
                    correctAns = cube,
                    explanation = listOf(
                        "${BengaliNumberFormatter.toBn(n)}³ = ${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)}",
                        "= ${BengaliNumberFormatter.toBn(n * n)} × ${BengaliNumberFormatter.toBn(n)} = ${BengaliNumberFormatter.toBn(cube)}"
                    ),
                    tip = "১ থেকে ১০ পর্যন্ত ঘন সংখ্যাগুলো মুখস্থ রাখলে ঘনমূল নিমেষেই চেনা যায়।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val n = Random.nextInt(6, 13)
                val cube = n * n * n
                val isRoot = Random.nextBoolean()
                if (isRoot) {
                    buildStandardIntQuestion(
                        id = "cube_med_$index",
                        category = PracticeCategory.CUBES_ROOTS,
                        difficulty = difficulty,
                        questionBn = "ঘনমূল কত?",
                        expression = "∛${BengaliNumberFormatter.toBn(cube)} = ?",
                        correctAns = n,
                        explanation = listOf(
                            "${BengaliNumberFormatter.toBn(n)}³ = ${BengaliNumberFormatter.toBn(cube)}",
                            "সুতরাং এর ঘনমূল = ${BengaliNumberFormatter.toBn(n)}"
                        ),
                        tip = "১০³ = ১০০০ এবং এর কাছাকাছি ঘন সংখ্যাগুলোর তালিকা মনে রাখুন।"
                    )
                } else {
                    buildStandardIntQuestion(
                        id = "cube_med_$index",
                        category = PracticeCategory.CUBES_ROOTS,
                        difficulty = difficulty,
                        questionBn = "ঘনফল কত?",
                        expression = "${BengaliNumberFormatter.toBn(n)}³ = ?",
                        correctAns = cube,
                        explanation = listOf(
                            "${BengaliNumberFormatter.toBn(n)}² = ${BengaliNumberFormatter.toBn(n * n)}",
                            "${BengaliNumberFormatter.toBn(n * n)} × ${BengaliNumberFormatter.toBn(n)} = ${BengaliNumberFormatter.toBn(cube)}"
                        ),
                        tip = "১১³ = ১৩৩১ এবং ১২³ = ১৭২৮ খুব সাধারণ ও প্রয়োজনীয় মান।"
                    )
                }
            }
            else -> {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 5)
                val cubeA = a * a * a
                val cubeB = b * b * b
                val ans = cubeA + cubeB
                buildStandardIntQuestion(
                    id = "cube_hard_$index",
                    category = PracticeCategory.CUBES_ROOTS,
                    difficulty = difficulty,
                    questionBn = "ঘনের সমষ্টি কত?",
                    expression = "${BengaliNumberFormatter.toBn(a)}³ + ${BengaliNumberFormatter.toBn(b)}³ = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "${BengaliNumberFormatter.toBn(a)}³ = ${BengaliNumberFormatter.toBn(cubeA)}",
                        "${BengaliNumberFormatter.toBn(b)}³ = ${BengaliNumberFormatter.toBn(cubeB)}",
                        "সমষ্টি = ${BengaliNumberFormatter.toBn(cubeA)} + ${BengaliNumberFormatter.toBn(cubeB)} = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "প্রতিটি পদের ঘনফল পৃথকভাবে বের করে যোগ করুন।"
                )
            }
        }
    }

    // 12. MIXED OPERATIONS & BODMAS
    private fun generateBodmasQuestion(index: Int, difficulty: PracticeDifficulty): PracticeQuestion {
        return when (difficulty) {
            PracticeDifficulty.EASY -> {
                val b = Random.nextInt(3, 9)
                val c = Random.nextInt(2, 7)
                val a = Random.nextInt(10, 35)
                val ans = a + (b * c)
                buildStandardIntQuestion(
                    id = "bodmas_easy_$index",
                    category = PracticeCategory.NUMBER_OPERATIONS,
                    difficulty = difficulty,
                    questionBn = "BODMAS নিয়ম অনুসারে সরলফল নির্ণয় করো:",
                    expression = "${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} × ${BengaliNumberFormatter.toBn(c)} = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "BODMAS অনুসারে যোগের আগে গুণের কাজ আগে করতে হবে।",
                        "ধাপ ১ (গুণ): ${BengaliNumberFormatter.toBn(b)} × ${BengaliNumberFormatter.toBn(c)} = ${BengaliNumberFormatter.toBn(b * c)}",
                        "ধাপ ২ (যোগ): ${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b * c)} = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "সবসময় গুণের কাজ আগে, যোগের কাজ পরে। ভুলেও আগে যোগ করবেন না।"
                )
            }
            PracticeDifficulty.MEDIUM -> {
                val a = Random.nextInt(4, 15)
                val b = Random.nextInt(3, 12)
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(5, 20)
                val ans = ((a + b) * c) - d
                buildStandardIntQuestion(
                    id = "bodmas_med_$index",
                    category = PracticeCategory.NUMBER_OPERATIONS,
                    difficulty = difficulty,
                    questionBn = "বন্ধনীর সরলফল নির্ণয় করো:",
                    expression = "(${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)}) × ${BengaliNumberFormatter.toBn(c)} − ${BengaliNumberFormatter.toBn(d)} = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "ধাপ ১ (বন্ধনী): (${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)}) = ${BengaliNumberFormatter.toBn(a + b)}",
                        "ধাপ ২ (গুণ): ${BengaliNumberFormatter.toBn(a + b)} × ${BengaliNumberFormatter.toBn(c)} = ${BengaliNumberFormatter.toBn((a + b) * c)}",
                        "ধাপ ৩ (বিয়োগ): ${BengaliNumberFormatter.toBn((a + b) * c)} − ${BengaliNumberFormatter.toBn(d)} = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "বন্ধনীর (Brackets) ভেতরের কাজ সর্বদা সবার আগে সম্পন্ন করতে হয়।"
                )
            }
            else -> {
                val e = Random.nextInt(2, 5)
                val d = e * Random.nextInt(2, 6)
                val c = (d / e) + Random.nextInt(3, 8)
                val b = Random.nextInt(3, 6)
                val a = Random.nextInt(10, 40)
                val insideBracket = c - (d / e)
                val ans = a + (b * insideBracket)
                buildStandardIntQuestion(
                    id = "bodmas_hard_$index",
                    category = PracticeCategory.NUMBER_OPERATIONS,
                    difficulty = difficulty,
                    questionBn = "জটিল BODMAS রাশির সরল মান নির্ণয় করো:",
                    expression = "${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} × (${BengaliNumberFormatter.toBn(c)} − ${BengaliNumberFormatter.toBn(d)} ÷ ${BengaliNumberFormatter.toBn(e)}) = ?",
                    correctAns = ans,
                    explanation = listOf(
                        "ধাপ ১ (বন্ধনীর ভেতরের ভাগ): ${BengaliNumberFormatter.toBn(d)} ÷ ${BengaliNumberFormatter.toBn(e)} = ${BengaliNumberFormatter.toBn(d / e)}",
                        "ধাপ ২ (বন্ধনীর ভেতরের বিয়োগ): ${BengaliNumberFormatter.toBn(c)} − ${BengaliNumberFormatter.toBn(d / e)} = ${BengaliNumberFormatter.toBn(insideBracket)}",
                        "ধাপ ৩ (গুণ): ${BengaliNumberFormatter.toBn(b)} × ${BengaliNumberFormatter.toBn(insideBracket)} = ${BengaliNumberFormatter.toBn(b * insideBracket)}",
                        "ধাপ ৪ (যোগ): ${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b * insideBracket)} = ${BengaliNumberFormatter.toBn(ans)}"
                    ),
                    tip = "ক্রম: প্রথম বন্ধনী → ভাগ (Division) → গুণ (Multiplication) → যোগ (Addition) → বিয়োগ (Subtraction)।"
                )
            }
        }
    }

    private fun formatTenthsBn(tenths: Int): String {
        val whole = tenths / 10
        val rem = abs(tenths % 10)
        return if (rem == 0) {
            BengaliNumberFormatter.toBn(whole)
        } else {
            "${BengaliNumberFormatter.toBn(whole)}.${BengaliNumberFormatter.toBn(rem)}"
        }
    }

    private fun formatHundredthsBn(hundredths: Int): String {
        val whole = hundredths / 100
        val rem = abs(hundredths % 100)
        val remStr = if (rem < 10) "০${BengaliNumberFormatter.toBn(rem)}" else BengaliNumberFormatter.toBn(rem)
        return "${BengaliNumberFormatter.toBn(whole)}.$remStr"
    }

    private fun buildStandardIntQuestion(
        id: String,
        category: PracticeCategory,
        difficulty: PracticeDifficulty,
        questionBn: String,
        expression: String,
        correctAns: Int,
        explanation: List<String>,
        tip: String
    ): PracticeQuestion {
        val distractors = mutableSetOf<Int>()
        var attempts = 0
        while (distractors.size < 3 && attempts < 40) {
            attempts++
            val delta = when (attempts % 4) {
                0 -> 1
                1 -> -1
                2 -> 10
                else -> Random.nextInt(-15, 16)
            }
            val fake = correctAns + delta
            if (fake > 0 && fake != correctAns) {
                distractors.add(fake)
            }
        }
        var fallback = 1
        while (distractors.size < 3) {
            val candidate = correctAns + fallback
            if (candidate > 0 && candidate != correctAns) {
                distractors.add(candidate)
            }
            fallback += 2
        }

        val all = (distractors.toList() + correctAns).shuffled()
        val correctIdx = all.indexOf(correctAns)
        val optsBn = all.map { BengaliNumberFormatter.toBn(it) }

        return PracticeQuestion(
            id = id,
            category = category,
            difficulty = difficulty,
            questionBn = questionBn,
            expression = expression,
            optionsBn = optsBn,
            correctIndex = correctIdx,
            correctAnswerBn = BengaliNumberFormatter.toBn(correctAns),
            stepByStepExplanationBn = explanation,
            tipBn = tip,
            hintBn = explanation.firstOrNull() ?: tip
        )
    }

    private fun generateFractionOptions(num: Int, den: Int): List<String> {
        val correct = if (den == 1) {
            BengaliNumberFormatter.toBn(num)
        } else {
            "${BengaliNumberFormatter.toBn(num)}/${BengaliNumberFormatter.toBn(den)}"
        }
        val set = mutableSetOf(correct)
        var delta = 1
        while (set.size < 4) {
            val fakeNum = (num + delta).coerceAtLeast(1)
            val fake = if (den == 1) {
                BengaliNumberFormatter.toBn(fakeNum + 1)
            } else {
                "${BengaliNumberFormatter.toBn(fakeNum)}/${BengaliNumberFormatter.toBn(den)}"
            }
            set.add(fake)
            delta++
        }
        return set.toList()
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}
