package com.example.math

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.random.Random

/**
 * 10 Categories available for Mental Math Challenges (Module 03).
 */
enum class MentalMathCategory(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val primaryColor: Color,
    val containerColor: Color
) {
    ADDITION(
        id = "addition",
        titleBn = "যোগ",
        titleEn = "Addition",
        subtitleBn = "দ্রুত ও নির্ভুল দুই বা ততোধিক সংখ্যার যোগ",
        primaryColor = Color(0xFF2563EB), // Blue
        containerColor = Color(0xFFDBEAFE)
    ),
    SUBTRACTION(
        id = "subtraction",
        titleBn = "বিয়োগ",
        titleEn = "Subtraction",
        subtitleBn = "ধার নেওয়া ও দ্রুত মানসিক বিয়োগের কৌশল",
        primaryColor = Color(0xFF0D9488), // Teal
        containerColor = Color(0xFFCCFBF1)
    ),
    MULTIPLICATION(
        id = "multiplication",
        titleBn = "গুণ",
        titleEn = "Multiplication",
        subtitleBn = "নামতা ও দ্রুত গুণনের তীব্র মানসিক গতি",
        primaryColor = Color(0xFF7C3AED), // Purple
        containerColor = Color(0xFFEDE9FE)
    ),
    DIVISION(
        id = "division",
        titleBn = "ভাগ",
        titleEn = "Division",
        subtitleBn = "নিঃশেষে বিভাজ্য নিখুঁত ও দ্রুত ভাগফল",
        primaryColor = Color(0xFF0891B2), // Cyan
        containerColor = Color(0xFFCFFAFE)
    ),
    SQUARES(
        id = "squares",
        titleBn = "বর্গ সংখ্যা",
        titleEn = "Squares",
        subtitleBn = "১ থেকে ৩৫ পর্যন্ত সংখ্যার দ্রুত বর্গ নির্ণয়",
        primaryColor = Color(0xFFE11D48), // Rose
        containerColor = Color(0xFFFFE4E6)
    ),
    CUBES(
        id = "cubes",
        titleBn = "ঘন সংখ্যা",
        titleEn = "Cubes",
        subtitleBn = "১ থেকে ১৫ পর্যন্ত সংখ্যার দ্রুত ঘনফল ও ঘনমূল নির্ণয়",
        primaryColor = Color(0xFF9333EA), // Violet
        containerColor = Color(0xFFF3E8FF)
    ),
    PERCENTAGES(
        id = "percentages",
        titleBn = "শতকরা",
        titleEn = "Percentages",
        subtitleBn = "মৌখিক শতকরা হিসাব ও দ্রুত ছাড়/বৃদ্ধি গণনা",
        primaryColor = Color(0xFF059669), // Emerald
        containerColor = Color(0xFFD1FAE5)
    ),
    FRACTIONS(
        id = "fractions",
        titleBn = "ভগ্নাংশের মান",
        titleEn = "Fractions",
        subtitleBn = "সংখ্যার ভগ্নাংশ অংশ ও সমতুল্য মান দ্রুত নির্ণয়",
        primaryColor = Color(0xFFD97706), // Amber
        containerColor = Color(0xFFFEF3C7)
    ),
    NUMBER_PATTERNS(
        id = "patterns",
        titleBn = "সংখ্যার ধারা",
        titleEn = "Number Patterns",
        subtitleBn = "সমান্তর, গুণোত্তর ও বর্গ ধারার পরবর্তী সংখ্যা নির্ণয়",
        primaryColor = Color(0xFF4F46E5), // Indigo
        containerColor = Color(0xFFE0E7FF)
    ),
    MIXED_ARITHMETIC(
        id = "mixed",
        titleBn = "মিশ্র গণিত",
        titleEn = "Mixed Operations",
        subtitleBn = "যোগ, বিয়োগ, গুণ ও ভাগের সংমিশ্রণ চ্যালেঞ্জ",
        primaryColor = Color(0xFFEA580C), // Orange
        containerColor = Color(0xFFFFEDD5)
    );

    fun getIcon(): ImageVector = when (this) {
        ADDITION -> Icons.Default.Add
        SUBTRACTION -> Icons.Default.Remove
        MULTIPLICATION -> Icons.Default.Close
        DIVISION -> Icons.Default.Exposure
        SQUARES -> Icons.Default.Straighten
        CUBES -> Icons.Default.ViewInAr
        PERCENTAGES -> Icons.Default.Percent
        FRACTIONS -> Icons.Default.PieChart
        NUMBER_PATTERNS -> Icons.Default.AutoAwesome
        MIXED_ARITHMETIC -> Icons.Default.Calculate
    }
}

/**
 * Three levels of difficulty for Mental Math.
 */
enum class MentalMathDifficulty(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val basePoints: Int,
    val badgeColor: Color
) {
    EASY(
        id = "easy",
        titleBn = "সহজ",
        titleEn = "Easy",
        basePoints = 10,
        badgeColor = Color(0xFF10B981)
    ),
    MEDIUM(
        id = "medium",
        titleBn = "মাঝারি",
        titleEn = "Medium",
        basePoints = 20,
        badgeColor = Color(0xFFF59E0B)
    ),
    HARD(
        id = "hard",
        titleBn = "কঠিন",
        titleEn = "Hard",
        basePoints = 30,
        badgeColor = Color(0xFFEF4444)
    )
}

/**
 * Challenge Modes for Mental Math Challenge (Module 03).
 * Preserves TIMED_SPRINT and FIXED_QUESTIONS while adding all 7 specialized challenge modes.
 */
enum class MentalMathMode(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val isTimed: Boolean = true,
    val presetDurationSeconds: Int = 60,
    val presetQuestionCount: Int = 10
) {
    TIMED_SPRINT("timed_sprint", "টাইমড স্প্রিন্ট", "নির্দিষ্ট সময়ে সর্বোচ্চ স্কোর অর্জন করুন", isTimed = true, presetDurationSeconds = 60, presetQuestionCount = 0),
    FIXED_QUESTIONS("fixed_questions", "নির্দিষ্ট প্রশ্ন", "১০/২০/৩০ প্রশ্নে নির্ভুলতা যাচাই করুন", isTimed = false, presetDurationSeconds = 0, presetQuestionCount = 10),
    QUICK_CHALLENGE("quick_challenge", "কুইক চ্যালেঞ্জ", "৩০ সেকেন্ডের ঝটপট মানসিক প্রস্তুতি", isTimed = true, presetDurationSeconds = 30, presetQuestionCount = 0),
    SPRINT_60S("sprint_60s", "৬০-সেকেন্ড চ্যালেঞ্জ", "১ মিনিটে সর্বোচ্চ সঠিক উত্তর দিন", isTimed = true, presetDurationSeconds = 60, presetQuestionCount = 0),
    SPRINT_2M("sprint_2m", "২-মিনিট চ্যালেঞ্জ", "১২০ সেকেন্ডের ধারাবাহিক গতি পরীক্ষা", isTimed = true, presetDurationSeconds = 120, presetQuestionCount = 0),
    SPRINT_5M("sprint_5m", "৫-মিনিট চ্যালেঞ্জ", "৩০০ সেকেন্ডের ম্যারাথন মেন্টাল ম্যাথ", isTimed = true, presetDurationSeconds = 300, presetQuestionCount = 0),
    ACCURACY_CHALLENGE("accuracy_challenge", "অ্যাকুরেসি চ্যালেঞ্জ", "২০টি প্রশ্নে ১০০% নির্ভুলতার চ্যালেঞ্জ", isTimed = false, presetDurationSeconds = 0, presetQuestionCount = 20),
    DAILY_CHALLENGE("daily_challenge", "ডেইলি চ্যালেঞ্জ", "প্রতিদিনের বিশেষ ১৫ প্রশ্নের মিশ্র চ্যালেঞ্জ", isTimed = true, presetDurationSeconds = 90, presetQuestionCount = 15),
    CUSTOM_CHALLENGE("custom_challenge", "কাস্টম চ্যালেঞ্জ", "নিজের পছন্দমতো সময় ও প্রশ্ন সংখ্যা নির্ধারণ", isTimed = true, presetDurationSeconds = 60, presetQuestionCount = 10);

    val defaultSeconds: Int get() = presetDurationSeconds
    val defaultQuestions: Int get() = presetQuestionCount
}

/**
 * An individual mental math challenge question.
 */
data class MentalMathQuestion(
    val id: String,
    val category: MentalMathCategory,
    val difficulty: MentalMathDifficulty,
    val expressionBn: String,
    val expressionPlain: String,
    val correctAnswer: Int,
    val correctAnswerBn: String,
    val options: List<Int>,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val trueFalseStatementBn: String = "",
    val isStatementTrue: Boolean = true
)

/**
 * Record of an answered question during a session.
 */
data class AnsweredMentalQuestion(
    val question: MentalMathQuestion,
    val chosenOption: Int,
    val isCorrect: Boolean,
    val timeTakenSeconds: Int
)

/**
 * State representing a mental math challenge session.
 */
data class MentalMathSessionState(
    val category: MentalMathCategory = MentalMathCategory.ADDITION,
    val difficulty: MentalMathDifficulty = MentalMathDifficulty.EASY,
    val mode: MentalMathMode = MentalMathMode.TIMED_SPRINT,
    val inputFormat: QuestionInputFormat = QuestionInputFormat.MCQ,
    val configuredDurationSeconds: Int = 60,
    val configuredQuestionCount: Int = 10,
    val questions: List<MentalMathQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val timeBonusPoints: Int = 0,
    val streak: Int = 0,
    val highestStreak: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val timeRemainingSeconds: Int = 60,
    val elapsedSeconds: Int = 0,
    val isAnswerSubmitted: Boolean = false,
    val selectedOptionIndex: Int? = null,
    val typedInput: String = "",
    val lastAnswerCorrect: Boolean? = null,
    val feedbackMessageBn: String? = null,
    val isFinished: Boolean = false,
    val history: List<AnsweredMentalQuestion> = emptyList(),
    val isReviewMode: Boolean = false,
    val startElapsedRealtimeMs: Long = 0L,
    val deadlineElapsedRealtimeMs: Long = 0L
)

/**
 * Monotonic clock abstraction for reliable, testable elapsed time measurement.
 */
interface MonotonicClock {
    fun elapsedRealtime(): Long
}

class DefaultMonotonicClock : MonotonicClock {
    override fun elapsedRealtime(): Long {
        return try {
            android.os.SystemClock.elapsedRealtime()
        } catch (e: Throwable) {
            System.nanoTime() / 1_000_000L
        }
    }
}

/**
 * Generator engine producing mathematically valid, educational mental math problems across all 10 categories.
 */
object MentalMathGenerator {

    /**
     * Generates a batch of distinct, valid mental math questions.
     */
    fun generateQuestions(
        category: MentalMathCategory,
        difficulty: MentalMathDifficulty,
        count: Int = 20
    ): List<MentalMathQuestion> {
        val list = mutableListOf<MentalMathQuestion>()
        val seenExpressions = mutableSetOf<String>()
        var attempts = 0

        while (list.size < count && attempts < count * 10) {
            attempts++
            val q = when (category) {
                MentalMathCategory.ADDITION -> generateAddition(list.size + 1, difficulty)
                MentalMathCategory.SUBTRACTION -> generateSubtraction(list.size + 1, difficulty)
                MentalMathCategory.MULTIPLICATION -> generateMultiplication(list.size + 1, difficulty)
                MentalMathCategory.DIVISION -> generateDivision(list.size + 1, difficulty)
                MentalMathCategory.SQUARES -> generateSquare(list.size + 1, difficulty)
                MentalMathCategory.CUBES -> generateCube(list.size + 1, difficulty)
                MentalMathCategory.PERCENTAGES -> generatePercentage(list.size + 1, difficulty)
                MentalMathCategory.FRACTIONS -> generateFractionOfNumber(list.size + 1, difficulty)
                MentalMathCategory.NUMBER_PATTERNS -> generateNumberPattern(list.size + 1, difficulty)
                MentalMathCategory.MIXED_ARITHMETIC -> generateMixed(list.size + 1, difficulty)
            }

            if (!seenExpressions.contains(q.expressionPlain)) {
                seenExpressions.add(q.expressionPlain)
                list.add(q)
            }
        }

        // Fill remaining if needed
        while (list.size < count) {
            val fallback = generateAddition(list.size + 1 + attempts++, difficulty)
            list.add(fallback)
        }

        return list
    }

    // 1. ADDITION
    private fun generateAddition(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val (a, b, c) = when (difficulty) {
            MentalMathDifficulty.EASY -> {
                val x = Random.nextInt(12, 85)
                val y = Random.nextInt(11, 75)
                Triple(x, y, 0)
            }
            MentalMathDifficulty.MEDIUM -> {
                val threeNumbers = Random.nextBoolean()
                if (threeNumbers) {
                    val x = Random.nextInt(15, 60)
                    val y = Random.nextInt(15, 55)
                    val z = Random.nextInt(10, 45)
                    Triple(x, y, z)
                } else {
                    val x = Random.nextInt(110, 490)
                    val y = Random.nextInt(95, 380)
                    Triple(x, y, 0)
                }
            }
            MentalMathDifficulty.HARD -> {
                val x = Random.nextInt(350, 1500)
                val y = Random.nextInt(280, 1200)
                Triple(x, y, 0)
            }
        }

        val ans = a + b + c
        val exprPlain = if (c > 0) "$a + $b + $c" else "$a + $b"
        val exprBn = if (c > 0) {
            "${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} + ${BengaliNumberFormatter.toBn(c)} = ?"
        } else {
            "${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} = ?"
        }

        val explanation = "যোগফল = $exprBn\nসঠিক উত্তর: ${BengaliNumberFormatter.toBn(ans)}"
        val options = generateDistractors(ans)

        return buildQuestion(
            id = "mm_add_${difficulty.id}_$index",
            category = MentalMathCategory.ADDITION,
            difficulty = difficulty,
            expressionBn = exprBn,
            expressionPlain = exprPlain,
            correctAnswer = ans,
            options = options,
            explanationBn = explanation
        )
    }

    // 2. SUBTRACTION (Guaranteed positive result)
    private fun generateSubtraction(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val (a, b) = when (difficulty) {
            MentalMathDifficulty.EASY -> {
                val x = Random.nextInt(35, 99)
                val y = Random.nextInt(11, x - 5)
                Pair(x, y)
            }
            MentalMathDifficulty.MEDIUM -> {
                val x = Random.nextInt(120, 550)
                val y = Random.nextInt(35, x - 15)
                Pair(x, y)
            }
            MentalMathDifficulty.HARD -> {
                val x = Random.nextInt(600, 2200)
                val y = Random.nextInt(180, x - 50)
                Pair(x, y)
            }
        }

        val ans = a - b
        val exprPlain = "$a - $b"
        val exprBn = "${BengaliNumberFormatter.toBn(a)} − ${BengaliNumberFormatter.toBn(b)} = ?"
        val explanation = "${BengaliNumberFormatter.toBn(a)} থেকে ${BengaliNumberFormatter.toBn(b)} বিয়োগ করলে থাকে ${BengaliNumberFormatter.toBn(ans)}"
        val options = generateDistractors(ans)

        return buildQuestion(
            id = "mm_sub_${difficulty.id}_$index",
            category = MentalMathCategory.SUBTRACTION,
            difficulty = difficulty,
            expressionBn = exprBn,
            expressionPlain = exprPlain,
            correctAnswer = ans,
            options = options,
            explanationBn = explanation
        )
    }

    // 3. MULTIPLICATION
    private fun generateMultiplication(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val (a, b) = when (difficulty) {
            MentalMathDifficulty.EASY -> {
                val x = Random.nextInt(3, 13)
                val y = Random.nextInt(3, 13)
                Pair(x, y)
            }
            MentalMathDifficulty.MEDIUM -> {
                val x = Random.nextInt(12, 28)
                val y = Random.nextInt(4, 15)
                Pair(x, y)
            }
            MentalMathDifficulty.HARD -> {
                val x = Random.nextInt(18, 48)
                val y = Random.nextInt(14, 35)
                Pair(x, y)
            }
        }

        val ans = a * b
        val exprPlain = "$a * $b"
        val exprBn = "${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)} = ?"
        val explanation = "${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)} এর গুণফল = ${BengaliNumberFormatter.toBn(ans)}"
        val options = generateDistractors(ans)

        return buildQuestion(
            id = "mm_mul_${difficulty.id}_$index",
            category = MentalMathCategory.MULTIPLICATION,
            difficulty = difficulty,
            expressionBn = exprBn,
            expressionPlain = exprPlain,
            correctAnswer = ans,
            options = options,
            explanationBn = explanation
        )
    }

    // 4. DIVISION (Divisor is never zero, dividend evenly divisible)
    private fun generateDivision(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val (divisor, quotient) = when (difficulty) {
            MentalMathDifficulty.EASY -> {
                val d = Random.nextInt(2, 11)
                val q = Random.nextInt(3, 15)
                Pair(d, q)
            }
            MentalMathDifficulty.MEDIUM -> {
                val d = Random.nextInt(4, 16)
                val q = Random.nextInt(11, 32)
                Pair(d, q)
            }
            MentalMathDifficulty.HARD -> {
                val d = Random.nextInt(11, 26)
                val q = Random.nextInt(14, 45)
                Pair(d, q)
            }
        }

        val dividend = divisor * quotient
        val exprPlain = "$dividend / $divisor"
        val exprBn = "${BengaliNumberFormatter.toBn(dividend)} ÷ ${BengaliNumberFormatter.toBn(divisor)} = ?"
        val explanation = "${BengaliNumberFormatter.toBn(dividend)} কে ${BengaliNumberFormatter.toBn(divisor)} দিয়ে ভাগ করলে ভাগফল হয় ${BengaliNumberFormatter.toBn(quotient)}"
        val options = generateDistractors(quotient)

        return buildQuestion(
            id = "mm_div_${difficulty.id}_$index",
            category = MentalMathCategory.DIVISION,
            difficulty = difficulty,
            expressionBn = exprBn,
            expressionPlain = exprPlain,
            correctAnswer = quotient,
            options = options,
            explanationBn = explanation
        )
    }

    // 5. SQUARES
    private fun generateSquare(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val n = when (difficulty) {
            MentalMathDifficulty.EASY -> Random.nextInt(2, 13)
            MentalMathDifficulty.MEDIUM -> Random.nextInt(13, 26)
            MentalMathDifficulty.HARD -> Random.nextInt(26, 36)
        }

        val ans = n * n
        val exprPlain = "$n^2"
        val exprBn = "${BengaliNumberFormatter.toBn(n)}² = ?"
        val explanation = "${BengaliNumberFormatter.toBn(n)} এর বর্গ (${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)}) = ${BengaliNumberFormatter.toBn(ans)}"
        val options = generateDistractors(ans)

        return buildQuestion(
            id = "mm_sq_${difficulty.id}_$index",
            category = MentalMathCategory.SQUARES,
            difficulty = difficulty,
            expressionBn = exprBn,
            expressionPlain = exprPlain,
            correctAnswer = ans,
            options = options,
            explanationBn = explanation
        )
    }

    // 6. CUBES
    private fun generateCube(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val n = when (difficulty) {
            MentalMathDifficulty.EASY -> Random.nextInt(2, 7)     // 2..6
            MentalMathDifficulty.MEDIUM -> Random.nextInt(5, 11)  // 5..10
            MentalMathDifficulty.HARD -> Random.nextInt(9, 16)    // 9..15
        }
        val isRoot = difficulty != MentalMathDifficulty.EASY && Random.nextBoolean()
        val cube = n * n * n
        return if (isRoot) {
            val exprPlain = "cbrt($cube)_$index"
            val exprBn = "∛${BengaliNumberFormatter.toBn(cube)} = ?"
            val explanation = "${BengaliNumberFormatter.toBn(n)}³ = ${BengaliNumberFormatter.toBn(cube)}, সুতরাং ∛${BengaliNumberFormatter.toBn(cube)} = ${BengaliNumberFormatter.toBn(n)}"
            buildQuestion(
                id = "mm_cb_${difficulty.id}_$index",
                category = MentalMathCategory.CUBES,
                difficulty = difficulty,
                expressionBn = exprBn,
                expressionPlain = exprPlain,
                correctAnswer = n,
                options = generateDistractors(n),
                explanationBn = explanation
            )
        } else {
            val exprPlain = "$n^3_$index"
            val exprBn = "${BengaliNumberFormatter.toBn(n)}³ = ?"
            val explanation = "${BengaliNumberFormatter.toBn(n)} এর ঘন (${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)} × ${BengaliNumberFormatter.toBn(n)}) = ${BengaliNumberFormatter.toBn(cube)}"
            buildQuestion(
                id = "mm_cb_${difficulty.id}_$index",
                category = MentalMathCategory.CUBES,
                difficulty = difficulty,
                expressionBn = exprBn,
                expressionPlain = exprPlain,
                correctAnswer = cube,
                options = generateDistractors(cube),
                explanationBn = explanation
            )
        }
    }

    // 7. PERCENTAGES
    private fun generatePercentage(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val pairs = when (difficulty) {
            MentalMathDifficulty.EASY -> listOf(
                10 to Random.nextInt(2, 15) * 10,
                20 to Random.nextInt(2, 12) * 5,
                25 to Random.nextInt(2, 12) * 4,
                50 to Random.nextInt(2, 25) * 2
            )
            MentalMathDifficulty.MEDIUM -> listOf(
                15 to Random.nextInt(2, 12) * 20,
                30 to Random.nextInt(2, 15) * 10,
                40 to Random.nextInt(2, 15) * 5,
                75 to Random.nextInt(2, 12) * 4
            )
            MentalMathDifficulty.HARD -> listOf(
                12 to Random.nextInt(2, 10) * 25,
                35 to Random.nextInt(2, 10) * 20,
                60 to Random.nextInt(3, 15) * 5,
                80 to Random.nextInt(3, 15) * 5
            )
        }
        val (pct, base) = pairs.random()
        val ans = (pct * base) / 100
        val exprPlain = "$pct% of $base"
        val exprBn = "${BengaliNumberFormatter.toBn(base)} এর ${BengaliNumberFormatter.toBn(pct)}% = ?"
        val explanation = "(${BengaliNumberFormatter.toBn(base)} × ${BengaliNumberFormatter.toBn(pct)}) ÷ ১০০ = ${BengaliNumberFormatter.toBn(ans)}"

        return buildQuestion(
            id = "mm_pct_${difficulty.id}_$index",
            category = MentalMathCategory.PERCENTAGES,
            difficulty = difficulty,
            expressionBn = exprBn,
            expressionPlain = exprPlain,
            correctAnswer = ans,
            options = generateDistractors(ans),
            explanationBn = explanation
        )
    }

    // 8. FRACTIONS (Integer result of a/b of N for fast mental calculation)
    private fun generateFractionOfNumber(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        val (num, den, mult) = when (difficulty) {
            MentalMathDifficulty.EASY -> {
                val d = listOf(2, 3, 4, 5).random()
                val n = Random.nextInt(1, d)
                val m = Random.nextInt(3, 12)
                Triple(n, d, m)
            }
            MentalMathDifficulty.MEDIUM -> {
                val d = listOf(4, 5, 6, 8, 10).random()
                val n = Random.nextInt(2, d)
                val m = Random.nextInt(5, 16)
                Triple(n, d, m)
            }
            MentalMathDifficulty.HARD -> {
                val d = listOf(7, 8, 9, 12, 15).random()
                val n = Random.nextInt(2, d)
                val m = Random.nextInt(6, 20)
                Triple(n, d, m)
            }
        }
        val total = den * mult
        val ans = num * mult
        val exprPlain = "($num/$den) * $total"
        val exprBn = "${BengaliNumberFormatter.toBn(total)} এর ${BengaliNumberFormatter.toBn(num)}/${BengaliNumberFormatter.toBn(den)} অংশ = ?"
        val explanation = "${BengaliNumberFormatter.toBn(total)} ÷ ${BengaliNumberFormatter.toBn(den)} = ${BengaliNumberFormatter.toBn(mult)}; ${BengaliNumberFormatter.toBn(mult)} × ${BengaliNumberFormatter.toBn(num)} = ${BengaliNumberFormatter.toBn(ans)}"

        return buildQuestion(
            id = "mm_frac_${difficulty.id}_$index",
            category = MentalMathCategory.FRACTIONS,
            difficulty = difficulty,
            expressionBn = exprBn,
            expressionPlain = exprPlain,
            correctAnswer = ans,
            options = generateDistractors(ans),
            explanationBn = explanation
        )
    }

    // 9. NUMBER PATTERNS
    private fun generateNumberPattern(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        return when (difficulty) {
            MentalMathDifficulty.EASY -> {
                val start = Random.nextInt(2, 20)
                val step = Random.nextInt(3, 12)
                val t1 = start
                val t2 = start + step
                val t3 = start + 2 * step
                val t4 = start + 3 * step
                val ans = start + 4 * step
                val exprPlain = "$t1, $t2, $t3, $t4, ?"
                val exprBn = "${BengaliNumberFormatter.toBn(t1)}, ${BengaliNumberFormatter.toBn(t2)}, ${BengaliNumberFormatter.toBn(t3)}, ${BengaliNumberFormatter.toBn(t4)}, ? (পরবর্তী পদ)"
                val explanation = "প্রতি পদে +${BengaliNumberFormatter.toBn(step)} করে বৃদ্ধি পেয়েছে: ${BengaliNumberFormatter.toBn(t4)} + ${BengaliNumberFormatter.toBn(step)} = ${BengaliNumberFormatter.toBn(ans)}"
                buildQuestion(
                    id = "mm_pat_easy_$index",
                    category = MentalMathCategory.NUMBER_PATTERNS,
                    difficulty = difficulty,
                    expressionBn = exprBn,
                    expressionPlain = exprPlain,
                    correctAnswer = ans,
                    options = generateDistractors(ans),
                    explanationBn = explanation
                )
            }
            MentalMathDifficulty.MEDIUM -> {
                val base = Random.nextInt(1, 8)
                val t1 = base * base
                val t2 = (base + 1) * (base + 1)
                val t3 = (base + 2) * (base + 2)
                val t4 = (base + 3) * (base + 3)
                val ans = (base + 4) * (base + 4)
                val exprPlain = "sq:$t1,$t2,$t3,$t4,?"
                val exprBn = "${BengaliNumberFormatter.toBn(t1)}, ${BengaliNumberFormatter.toBn(t2)}, ${BengaliNumberFormatter.toBn(t3)}, ${BengaliNumberFormatter.toBn(t4)}, ? (বর্গ ধারা)"
                val explanation = "ক্রমিক পূর্ণবর্গ সংখ্যা: (${BengaliNumberFormatter.toBn(base + 4)})² = ${BengaliNumberFormatter.toBn(ans)}"
                buildQuestion(
                    id = "mm_pat_med_$index",
                    category = MentalMathCategory.NUMBER_PATTERNS,
                    difficulty = difficulty,
                    expressionBn = exprBn,
                    expressionPlain = exprPlain,
                    correctAnswer = ans,
                    options = generateDistractors(ans),
                    explanationBn = explanation
                )
            }
            MentalMathDifficulty.HARD -> {
                val start = Random.nextInt(2, 7)
                val d1 = Random.nextInt(3, 8)
                val t1 = start
                val t2 = t1 + d1
                val t3 = t2 + d1 * 2
                val t4 = t3 + d1 * 3
                val ans = t4 + d1 * 4
                val exprPlain = "inc:$t1,$t2,$t3,$t4,?"
                val exprBn = "${BengaliNumberFormatter.toBn(t1)}, ${BengaliNumberFormatter.toBn(t2)}, ${BengaliNumberFormatter.toBn(t3)}, ${BengaliNumberFormatter.toBn(t4)}, ?"
                val explanation = "পার্থক্য যথাক্রমে ${BengaliNumberFormatter.toBn(d1)}, ${BengaliNumberFormatter.toBn(d1 * 2)}, ${BengaliNumberFormatter.toBn(d1 * 3)}, ${BengaliNumberFormatter.toBn(d1 * 4)}। তাই পরবর্তী পদ = ${BengaliNumberFormatter.toBn(t4)} + ${BengaliNumberFormatter.toBn(d1 * 4)} = ${BengaliNumberFormatter.toBn(ans)}"
                buildQuestion(
                    id = "mm_pat_hard_$index",
                    category = MentalMathCategory.NUMBER_PATTERNS,
                    difficulty = difficulty,
                    expressionBn = exprBn,
                    expressionPlain = exprPlain,
                    correctAnswer = ans,
                    options = generateDistractors(ans),
                    explanationBn = explanation
                )
            }
        }
    }

    // 10. MIXED ARITHMETIC
    private fun generateMixed(index: Int, difficulty: MentalMathDifficulty): MentalMathQuestion {
        return when (difficulty) {
            MentalMathDifficulty.EASY -> {
                val a = Random.nextInt(10, 40)
                val b = Random.nextInt(5, 30)
                val c = Random.nextInt(2, (a + b) - 5)
                val ans = a + b - c
                val exprPlain = "$a + $b - $c"
                val exprBn = "${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} − ${BengaliNumberFormatter.toBn(c)} = ?"
                val explanation = "ধাপ ১: ${BengaliNumberFormatter.toBn(a)} + ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(a + b)}\nধাপ ২: ${BengaliNumberFormatter.toBn(a + b)} − ${BengaliNumberFormatter.toBn(c)} = ${BengaliNumberFormatter.toBn(ans)}"
                buildQuestion(
                    id = "mm_mix_easy_$index",
                    category = MentalMathCategory.MIXED_ARITHMETIC,
                    difficulty = difficulty,
                    expressionBn = exprBn,
                    expressionPlain = exprPlain,
                    correctAnswer = ans,
                    options = generateDistractors(ans),
                    explanationBn = explanation
                )
            }
            MentalMathDifficulty.MEDIUM -> {
                val isMulAdd = Random.nextBoolean()
                if (isMulAdd) {
                    val a = Random.nextInt(4, 12)
                    val b = Random.nextInt(3, 11)
                    val c = Random.nextInt(10, 50)
                    val ans = (a * b) + c
                    val exprPlain = "($a * $b) + $c"
                    val exprBn = "(${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)}) + ${BengaliNumberFormatter.toBn(c)} = ?"
                    val explanation = "ধাপ ১: ${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)} = ${BengaliNumberFormatter.toBn(a * b)}\nধাপ ২: ${BengaliNumberFormatter.toBn(a * b)} + ${BengaliNumberFormatter.toBn(c)} = ${BengaliNumberFormatter.toBn(ans)}"
                    buildQuestion(
                        id = "mm_mix_med_$index",
                        category = MentalMathCategory.MIXED_ARITHMETIC,
                        difficulty = difficulty,
                        expressionBn = exprBn,
                        expressionPlain = exprPlain,
                        correctAnswer = ans,
                        options = generateDistractors(ans),
                        explanationBn = explanation
                    )
                } else {
                    val divisor = Random.nextInt(2, 9)
                    val quotient = Random.nextInt(5, 18)
                    val dividend = divisor * quotient
                    val c = Random.nextInt(5, 30)
                    val ans = quotient + c
                    val exprPlain = "($dividend / $divisor) + $c"
                    val exprBn = "(${BengaliNumberFormatter.toBn(dividend)} ÷ ${BengaliNumberFormatter.toBn(divisor)}) + ${BengaliNumberFormatter.toBn(c)} = ?"
                    val explanation = "ধাপ ১: ${BengaliNumberFormatter.toBn(dividend)} ÷ ${BengaliNumberFormatter.toBn(divisor)} = ${BengaliNumberFormatter.toBn(quotient)}\nধাপ ২: ${BengaliNumberFormatter.toBn(quotient)} + ${BengaliNumberFormatter.toBn(c)} = ${BengaliNumberFormatter.toBn(ans)}"
                    buildQuestion(
                        id = "mm_mix_med_$index",
                        category = MentalMathCategory.MIXED_ARITHMETIC,
                        difficulty = difficulty,
                        expressionBn = exprBn,
                        expressionPlain = exprPlain,
                        correctAnswer = ans,
                        options = generateDistractors(ans),
                        explanationBn = explanation
                    )
                }
            }
            MentalMathDifficulty.HARD -> {
                var a = Random.nextInt(6, 15)
                var b = Random.nextInt(5, 12)
                var c = Random.nextInt(3, 9)
                var d = Random.nextInt(2, 8)
                var prod1 = a * b
                var prod2 = c * d
                val isAdd = Random.nextBoolean()
                if (!isAdd) {
                    if (prod1 < prod2) {
                        val tempProd = prod1; prod1 = prod2; prod2 = tempProd
                        val tempA = a; a = c; c = tempA
                        val tempB = b; b = d; d = tempB
                    } else if (prod1 == prod2) {
                        a += 2
                        prod1 = a * b
                    }
                }
                val ans = if (isAdd) prod1 + prod2 else (prod1 - prod2)
                val opSymbol = if (isAdd) "+" else "−"
                val exprPlain = "($a * $b) $opSymbol ($c * $d)"
                val exprBn = "(${BengaliNumberFormatter.toBn(a)} × ${BengaliNumberFormatter.toBn(b)}) $opSymbol (${BengaliNumberFormatter.toBn(c)} × ${BengaliNumberFormatter.toBn(d)}) = ?"
                val explanation = "(${BengaliNumberFormatter.toBn(prod1)}) $opSymbol (${BengaliNumberFormatter.toBn(prod2)}) = ${BengaliNumberFormatter.toBn(ans)}"
                buildQuestion(
                    id = "mm_mix_hard_$index",
                    category = MentalMathCategory.MIXED_ARITHMETIC,
                    difficulty = difficulty,
                    expressionBn = exprBn,
                    expressionPlain = exprPlain,
                    correctAnswer = ans,
                    options = generateDistractors(ans),
                    explanationBn = explanation
                )
            }
        }
    }

    private fun buildQuestion(
        id: String,
        category: MentalMathCategory,
        difficulty: MentalMathDifficulty,
        expressionBn: String,
        expressionPlain: String,
        correctAnswer: Int,
        options: List<Int>,
        explanationBn: String
    ): MentalMathQuestion {
        val optionsBn = options.map { BengaliNumberFormatter.toBn(it) }
        val correctIndex = options.indexOf(correctAnswer)
        val isTrue = Random.nextBoolean()
        val shownVal = if (isTrue) {
            correctAnswer
        } else {
            options.firstOrNull { it != correctAnswer } ?: (correctAnswer + 2)
        }
        val cleanPrompt = expressionBn.replace("= ?", "").trim()
        val tfStatement = "$cleanPrompt = ${BengaliNumberFormatter.toBn(shownVal)}"

        return MentalMathQuestion(
            id = id,
            category = category,
            difficulty = difficulty,
            expressionBn = expressionBn,
            expressionPlain = expressionPlain,
            correctAnswer = correctAnswer,
            correctAnswerBn = BengaliNumberFormatter.toBn(correctAnswer),
            options = options,
            optionsBn = optionsBn,
            correctIndex = correctIndex,
            explanationBn = explanationBn,
            trueFalseStatementBn = tfStatement,
            isStatementTrue = isTrue
        )
    }

    /**
     * Generates 4 strictly distinct integer options containing the correct answer.
     */
    fun generateDistractors(correctAnswer: Int): List<Int> {
        val optionsSet = mutableSetOf(correctAnswer)
        val deltas = mutableListOf(-10, 10, -1, 1, -2, 2, -5, 5, -20, 20, -15, 15, -3, 3).shuffled()

        for (d in deltas) {
            val candidate = correctAnswer + d
            if (candidate > 0 && candidate != correctAnswer) {
                optionsSet.add(candidate)
            }
            if (optionsSet.size == 4) break
        }

        var counter = 1
        while (optionsSet.size < 4) {
            val cand1 = correctAnswer + (counter * 7)
            val cand2 = (correctAnswer - (counter * 7)).coerceAtLeast(1)
            if (cand1 != correctAnswer) optionsSet.add(cand1)
            if (optionsSet.size < 4 && cand2 != correctAnswer) optionsSet.add(cand2)
            counter++
        }

        return optionsSet.toList().shuffled()
    }
}
