package com.example.math

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

/**
 * Supported interactive question/answer formats across Practice Lab,
 * Mental Math Challenge, Interactive Quiz, and Real Numbers Practice.
 */
enum class QuestionInputFormat(
    val id: String,
    val titleBn: String,
    val titleEn: String
) {
    MCQ("mcq", "এমসিকিউ (MCQ)", "Multiple Choice"),
    KEYPAD("keypad", "সংখ্যা কিপ্যাড (Keypad)", "Numerical Keypad"),
    TRUE_FALSE("true_false", "সত্য / মিথ্যা (True/False)", "True / False")
}

/**
 * Filterable curriculum & competition tracks across Formula Library (Module 05),
 * Practice Lab (Module 06), and Real Numbers & Divisibility (Module 08).
 */
enum class CurriculumTrack(
    val id: String,
    val titleBn: String,
    val shortBadgeBn: String,
    val descriptionBn: String
) {
    ALL("all", "সকল ট্র্যাক", "সকল", "NCTB বোর্ড বই, গণিত অলিম্পিয়াড ও বিসিএস সমন্বিত সংগ্রহ"),
    NCTB("nctb", "NCTB পাঠ্যক্রম", "NCTB ৬ষ্ঠ–১০ম", "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (৬ষ্ঠ থেকে ৯ম-১০ম শ্রেণি)"),
    BDMO("bdmo", "BdMO অলিম্পিয়াড", "BdMO অলিম্পিয়াড", "বাংলাদেশ গণিত অলিম্পিয়াড (সংখ্যাতত্ত্ব, কম্বিনেটরিক্স, জ্যামিতি ও বীজগণিত)"),
    BCS("bcs", "বিসিএস প্রস্তুতি", "বিসিএস ও জব", "১০ম–৫০তম বিসিএস প্রিলিমিনারি ও লিখিত গাণিতিক যুক্তি");

    val badgeBn: String get() = shortBadgeBn
}

data class ModularExponentiationResult(
    val base: Long,
    val exponent: Long,
    val modulus: Long,
    val remainder: Long,
    val congruenceNotationBn: String,
    val stepsBn: List<Pair<String, String>>,
    val theoremNoteBn: String
)

data class DivisorAnalysisResult(
    val number: Long,
    val primePowerBn: String,
    val divisorCount: Long,
    val divisorSum: Long,
    val eulerTotient: Long,
    val divisorsPreviewBn: String,
    val stepsBn: List<Pair<String, String>>
)

data class SimultaneousLinearResult(
    val isUniqueSolution: Boolean,
    val isInfiniteSolutions: Boolean,
    val isNoSolution: Boolean,
    val determinant: Double,
    val xExactBn: String,
    val yExactBn: String,
    val statusBn: String,
    val stepsBn: List<Pair<String, String>>,
    val verificationBn: String
) {
    val hasUniqueSolution: Boolean get() = isUniqueSolution
}

/**
 * Exact arbitrary-precision Rational number (p / q) with automatic GCD reduction
 * and positive denominator normalization.
 */
data class Rational(
    val numerator: BigInteger,
    val denominator: BigInteger
) : Comparable<Rational> {

    init {
        if (denominator == BigInteger.ZERO) {
            throw ArithmeticException("ভগ্নাংশের হর শূন্য (০) হতে পারে না। (Division by zero)")
        }
    }

    val isInteger: Boolean
        get() = simplifiedDenominator == BigInteger.ONE

    val simplifiedNumerator: BigInteger
    val simplifiedDenominator: BigInteger

    init {
        val sign = if (denominator < BigInteger.ZERO) BigInteger.valueOf(-1) else BigInteger.ONE
        val normNum = numerator.multiply(sign)
        val normDen = denominator.abs()
        val g = normNum.abs().gcd(normDen)
        if (g == BigInteger.ZERO) {
            simplifiedNumerator = BigInteger.ZERO
            simplifiedDenominator = BigInteger.ONE
        } else {
            simplifiedNumerator = normNum.divide(g)
            simplifiedDenominator = normDen.divide(g)
        }
    }

    operator fun plus(other: Rational): Rational {
        val num = simplifiedNumerator.multiply(other.simplifiedDenominator)
            .add(other.simplifiedNumerator.multiply(simplifiedDenominator))
        val den = simplifiedDenominator.multiply(other.simplifiedDenominator)
        return Rational(num, den)
    }

    operator fun minus(other: Rational): Rational {
        val num = simplifiedNumerator.multiply(other.simplifiedDenominator)
            .subtract(other.simplifiedNumerator.multiply(simplifiedDenominator))
        val den = simplifiedDenominator.multiply(other.simplifiedDenominator)
        return Rational(num, den)
    }

    operator fun times(other: Rational): Rational {
        val num = simplifiedNumerator.multiply(other.simplifiedNumerator)
        val den = simplifiedDenominator.multiply(other.simplifiedDenominator)
        return Rational(num, den)
    }

    operator fun div(other: Rational): Rational {
        if (other.simplifiedNumerator == BigInteger.ZERO) {
            throw ArithmeticException("শূন্য (০) দিয়ে ভাগ করা অসংজ্ঞায়িত।")
        }
        val num = simplifiedNumerator.multiply(other.simplifiedDenominator)
        val den = simplifiedDenominator.multiply(other.simplifiedNumerator)
        return Rational(num, den)
    }

    fun pow(exponent: Int): Rational {
        if (exponent == 0) return ONE
        if (exponent > 0) {
            return Rational(simplifiedNumerator.pow(exponent), simplifiedDenominator.pow(exponent))
        }
        if (simplifiedNumerator == BigInteger.ZERO) {
            throw ArithmeticException("শূন্যের ঋণাত্মক ঘাত অসংজ্ঞায়িত।")
        }
        val posExp = abs(exponent)
        return Rational(simplifiedDenominator.pow(posExp), simplifiedNumerator.pow(posExp))
    }

    fun toBigDecimal(scale: Int = 6): BigDecimal {
        return BigDecimal(simplifiedNumerator)
            .divide(BigDecimal(simplifiedDenominator), scale, RoundingMode.HALF_UP)
            .stripTrailingZeros()
    }

    fun toDouble(): Double = toBigDecimal(10).toDouble()

    fun toPlainString(): String {
        return if (simplifiedDenominator == BigInteger.ONE) {
            simplifiedNumerator.toString()
        } else {
            "$simplifiedNumerator/$simplifiedDenominator"
        }
    }

    fun toBnString(): String = BengaliNumberFormatter.toBn(toPlainString())

    fun toMixedFractionBn(): String? {
        val absNum = simplifiedNumerator.abs()
        if (simplifiedDenominator == BigInteger.ONE || absNum <= simplifiedDenominator) return null
        val whole = absNum.divide(simplifiedDenominator)
        val rem = absNum.remainder(simplifiedDenominator)
        if (rem == BigInteger.ZERO) return null
        val prefix = if (simplifiedNumerator < BigInteger.ZERO) "-" else ""
        return "$prefix${BengaliNumberFormatter.toBn(whole.toString())} সমস্ত ${BengaliNumberFormatter.toBn(rem.toString())}/${BengaliNumberFormatter.toBn(simplifiedDenominator.toString())}"
    }

    override fun compareTo(other: Rational): Int {
        val lhs = simplifiedNumerator.multiply(other.simplifiedDenominator)
        val rhs = other.simplifiedNumerator.multiply(simplifiedDenominator)
        return lhs.compareTo(rhs)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Rational) return false
        return simplifiedNumerator == other.simplifiedNumerator &&
            simplifiedDenominator == other.simplifiedDenominator
    }

    override fun hashCode(): Int {
        return 31 * simplifiedNumerator.hashCode() + simplifiedDenominator.hashCode()
    }

    companion object {
        val ZERO = Rational(BigInteger.ZERO, BigInteger.ONE)
        val ONE = Rational(BigInteger.ONE, BigInteger.ONE)

        fun of(num: Long, den: Long = 1L): Rational =
            Rational(BigInteger.valueOf(num), BigInteger.valueOf(den))

        fun fromDouble(value: Double): Rational {
            val bd = BigDecimal.valueOf(value).stripTrailingZeros()
            val scale = bd.scale()
            return if (scale <= 0) {
                Rational(bd.toBigIntegerExact(), BigInteger.ONE)
            } else {
                val unscaled = bd.unscaledValue()
                val den = BigInteger.TEN.pow(scale)
                Rational(unscaled, den)
            }
        }
    }
}

data class IntegerDivisionResult(
    val dividend: Long,
    val divisor: Long,
    val quotient: Long,
    val remainder: Long,
    val isExactMultiple: Boolean,
    val formulaVerificationBn: String,
    val explanationBn: String
)

data class QuadraticAnalysisResult(
    val a: Double,
    val b: Double,
    val c: Double,
    val isValidQuadratic: Boolean,
    val discriminant: Double,
    val natureOfRootsBn: String,
    val natureOfRootsEn: String,
    val root1TextBn: String,
    val root2TextBn: String,
    val exactRootsTextBn: String,
    val vertexX: Double,
    val vertexY: Double,
    val stepsBn: List<Pair<String, String>>,
    val verificationBn: String,
    val errorMessageBn: String? = null
)

data class FactorizationResult(
    val isSuccess: Boolean,
    val originalInputBn: String,
    val factoredFormBn: String,
    val stepsBn: List<Pair<String, String>>,
    val verificationBn: String,
    val errorMessageBn: String? = null
)

data class SimplificationResult(
    val isSuccess: Boolean,
    val originalInputBn: String,
    val simplifiedFormBn: String,
    val isExact: Boolean,
    val approximateValueBn: String? = null,
    val stepsBn: List<Pair<String, String>>,
    val verificationBn: String,
    val errorMessageBn: String? = null
)

/**
 * Shared Mathematical Engine providing validated number theory, exact rationals,
 * quadratic & algebraic solvers, radical simplification, and answer verification.
 */
object SharedMathEngine {

    private val df = DecimalFormat("#.####", DecimalFormatSymbols(Locale.US))

    fun formatDouble(d: Double): String {
        if (d.isNaN() || d.isInfinite()) return "অসংজ্ঞায়িত"
        val clean = if (abs(d) < 1e-12) 0.0 else d
        return if (clean == floor(clean) && abs(clean) < 1e15) {
            clean.toLong().toString()
        } else {
            df.format(clean)
        }
    }

    fun formatDoubleBn(d: Double): String = BengaliNumberFormatter.toBn(formatDouble(d))

    /**
     * Normalizes Bengali/English numerals, signs, and whitespace for deterministic answer verification.
     */
    fun normalizeAnswerToken(raw: String): String {
        val bnToEn = mapOf(
            '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
            '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9',
            '−' to '-', '–' to '-', '×' to '*', '÷' to '/'
        )
        val asciiSpacePreserved = buildString {
            for (ch in raw.trim()) {
                append(bnToEn[ch] ?: ch)
            }
        }.replace(Regex("""\s+"""), " ").trim()

        val unitStripped = stripCommonUnitSuffixes(asciiSpacePreserved)

        // Convert mixed fractions ("1 1/2", "1 সমস্ত 1/2", "1 পূর্ণ 1/2", "-2 3/4") to improper fractions
        val mixedMatch = Regex("""^([+-]?\d+)\s+(?:সমস্ত\s+|পূর্ণ\s+)?(\d+)\s*/\s*(\d+)$""").find(unitStripped)
        if (mixedMatch != null) {
            val whole = mixedMatch.groupValues[1].toLongOrNull()
            val num = mixedMatch.groupValues[2].toLongOrNull()
            val den = mixedMatch.groupValues[3].toLongOrNull()
            if (whole != null && num != null && den != null && den != 0L) {
                val sign = if (whole < 0 || mixedMatch.groupValues[1].startsWith("-")) -1L else 1L
                val impNum = sign * (abs(whole) * den + num)
                return "$impNum/$den"
            }
        }

        val str = unitStripped.replace(" ", "")
        // Normalize trailing .0
        val asDouble = str.toDoubleOrNull()
        if (asDouble != null && asDouble == floor(asDouble) && abs(asDouble) < 1e12) {
            return asDouble.toLong().toString()
        }
        return str.lowercase(Locale.ROOT)
    }

    /**
     * Independent mathematical equivalence check between user's submitted answer and expected answer.
     */
    fun isAnswerCorrect(userAnswer: String, expectedAnswer: String): Boolean {
        if (userAnswer.isBlank()) return false
        val normUser = normalizeAnswerToken(userAnswer)
        val normExpected = normalizeAnswerToken(expectedAnswer)
        if (normUser == normExpected) return true

        // True / False bilingual equivalence
        val trueTokens = setOf("true", "সত্য", "হ্যাঁ", "yes", "1", "ঠিক")
        val falseTokens = setOf("false", "মিথ্যা", "না", "no", "0", "ভুল")
        if (normUser in trueTokens && normExpected in trueTokens) return true
        if (normUser in falseTokens && normExpected in falseTokens) return true

        val strippedUser = stripCommonUnitSuffixes(normUser)
        val strippedExpected = stripCommonUnitSuffixes(normExpected)
        if (strippedUser.isNotBlank() && strippedUser == strippedExpected) return true

        // Rational / Fraction / Ratio equivalence (e.g., "2/4" == "1/2" or "0.5", "3:4" == "6:8")
        val rUser = parseRationalOrNull(strippedUser)
        val rExpected = parseRationalOrNull(strippedExpected)
        if (rUser != null && rExpected != null && rUser == rExpected) {
            return true
        }

        // Decimal tolerance check
        val dUser = strippedUser.removeSuffix("%").toDoubleOrNull()
        val dExpected = strippedExpected.removeSuffix("%").toDoubleOrNull()
        if (dUser != null && dExpected != null) {
            return abs(dUser - dExpected) < 1e-4
        }

        return false
    }

    private fun stripCommonUnitSuffixes(token: String): String {
        var result = token.trim()
        val suffixes = listOf(
            "বর্গকিলোমিটার", "ঘনকিলোমিটার", "কিলোমিটার",
            "বর্গসেন্টিমিটার", "ঘনসেন্টিমিটার", "সেন্টিমিটার", "মিলিমিটার",
            "বর্গসে.মি.", "ঘনসে.মি.", "বর্গকি.মি.", "ঘনকি.মি.",
            "বর্গমিটার", "ঘনমিটার", "বর্গসেমি", "ঘনসেমি",
            "বর্গএকক", "ঘনএকক", "বর্গ একক", "ঘন একক",
            "বর্গ মিটার", "ঘন মিটার", "বর্গ সেমি", "ঘন সেমি",
            "সে.মি.", "কি.মি.", "ব.মি.", "ঘ.মি.",
            "সেমি", "কিমি", "মিটার", "লিটার", "ডিগ্রি",
            "মি.", "একক", "টাকা", "জন", "টি", "দিন",
            "ঘণ্টা", "ঘন্টা", "মিনিট", "সেকেন্ড", "বছর",
            "কেজি", "গ্রাম", "মাইল", "বার", "°"
        )
        var changed = true
        while (changed) {
            changed = false
            for (suffix in suffixes) {
                if (result.endsWith(suffix) && result.length > suffix.length) {
                    result = result.removeSuffix(suffix).trim()
                    changed = true
                    break
                }
            }
        }
        val enUnitMatch = Regex(
            """^([+-]?\d+(?:\.\d+)?(?:\s+\d+\s*/\s*\d+|\s*/\s*\d+)?)\s*(sq\.?\s*cm|sq\.?\s*m|cu\.?\s*cm|cu\.?\s*m|cm\^2|cm\^3|m\^2|m\^3|km|cm|mm|kg|gm|sec|min|hrs|hr|deg|degrees|degree|meters|meter|liters|liter|units|unit|taka|tk|m|g|s)$""",
            RegexOption.IGNORE_CASE
        ).find(result)
        if (enUnitMatch != null) {
            val numberPart = enUnitMatch.groupValues[1].trim()
            val unitPart = enUnitMatch.groupValues[2].trim()
            val hasSpaceBeforeUnit = result.substring(numberPart.length).startsWith(" ")
            if (unitPart.length > 1 || hasSpaceBeforeUnit) {
                result = numberPart
            }
        }
        return result
    }

    fun parseRationalOrNull(token: String): Rational? {
        return try {
            if (token.contains("/") || token.contains(":")) {
                val delimiter = if (token.contains("/")) "/" else ":"
                val parts = token.split(delimiter)
                if (parts.size != 2) return null
                val n = BigInteger(parts[0].trim())
                val d = BigInteger(parts[1].trim())
                if (d == BigInteger.ZERO) return null
                Rational(n, d)
            } else {
                val d = token.toDoubleOrNull() ?: return null
                if (d.isNaN() || d.isInfinite()) return null
                Rational.fromDouble(d)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun isBigIntPerfectSquare(n: BigInteger): Boolean {
        if (n < BigInteger.ZERO) return false
        if (n == BigInteger.ZERO || n == BigInteger.ONE) return true
        val root = n.sqrt()
        return root.multiply(root) == n
    }

    // -------------------------------------------------------------
    // NUMBER THEORY UTILITIES
    // -------------------------------------------------------------

    fun areCoprime(a: Long, b: Long): Boolean {
        if (a == 0L && b == 0L) return false
        return NumberSystemProvider.gcd(a, b) == 1L
    }

    fun integerDivision(dividend: Long, divisor: Long): IntegerDivisionResult {
        if (divisor == 0L) {
            throw ArithmeticException("ভাজক শূন্য (০) হতে পারে না।")
        }
        val aBig = BigInteger.valueOf(dividend)
        val bBig = BigInteger.valueOf(divisor)
        val absB = bBig.abs()
        var qBig = aBig.divide(bBig)
        var rBig = aBig.remainder(bBig)
        if (rBig < BigInteger.ZERO) {
            if (bBig > BigInteger.ZERO) {
                qBig = qBig.subtract(BigInteger.ONE)
                rBig = rBig.add(bBig)
            } else {
                qBig = qBig.add(BigInteger.ONE)
                rBig = rBig.subtract(bBig)
            }
        }
        if (qBig > BigInteger.valueOf(Long.MAX_VALUE) || qBig < BigInteger.valueOf(Long.MIN_VALUE) ||
            rBig > BigInteger.valueOf(Long.MAX_VALUE)
        ) {
            throw ArithmeticException("সংখ্যার মান সমর্থিত সীমার বাইরে (সংখ্যাধিক্য বা ওভারফ্লো)।")
        }
        val q = qBig.toLong()
        val r = rBig.toLong()
        val isExact = r == 0L
        val dBn = BengaliNumberFormatter.toBn(aBig.toString())
        val divBn = BengaliNumberFormatter.toBn(bBig.toString())
        val qBn = BengaliNumberFormatter.toBn(qBig.toString())
        val rBn = BengaliNumberFormatter.toBn(rBig.toString())

        val verify = "$dBn = ($divBn × $qBn) + $rBn"
        val expl = if (isExact) {
            "$dBn কে $divBn দ্বারা ভাগ করলে ভাগফল $qBn এবং ভাগশেষ ০। অর্থাৎ $dBn সংখ্যাটি $divBn দ্বারা নিঃশেষে বিভাজ্য।"
        } else {
            "$dBn কে $divBn দ্বারা ভাগ করলে ইউক্লিডীয় ভাগফল $qBn এবং অঋণাত্মক ভাগশেষ $rBn (০ ≤ $rBn < |${BengaliNumberFormatter.toBn(absB.toString())}|) থাকে।"
        }
        return IntegerDivisionResult(
            dividend = dividend,
            divisor = divisor,
            quotient = q,
            remainder = r,
            isExactMultiple = isExact,
            formulaVerificationBn = verify,
            explanationBn = expl
        )
    }

    fun primeFactorizationExponents(n: Long): Map<Long, Int> {
        val factors = NumberSystemProvider.getPrimeFactors(n)
        val map = linkedMapOf<Long, Int>()
        for (p in factors) {
            map[p] = (map[p] ?: 0) + 1
        }
        return map
    }

    fun formatPrimePowerBn(n: Long): String {
        val map = primeFactorizationExponents(n)
        if (map.isEmpty()) return BengaliNumberFormatter.toBn(n)
        val superscripts = mapOf(
            '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
            '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹'
        )
        return map.entries.joinToString(" × ") { (prime, exp) ->
            val pBn = BengaliNumberFormatter.toBn(prime)
            if (exp == 1) pBn
            else {
                val expSup = exp.toString().map { superscripts[it] ?: it }.joinToString("")
                "$pBn$expSup"
            }
        }
    }

    // -------------------------------------------------------------
    // QUADRATIC EQUATION SOLVER: ax² + bx + c = 0
    // -------------------------------------------------------------

    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticAnalysisResult {
        if (a.isNaN() || b.isNaN() || c.isNaN() || a.isInfinite() || b.isInfinite() || c.isInfinite()) {
            return QuadraticAnalysisResult(
                a = a, b = b, c = c,
                isValidQuadratic = false,
                discriminant = Double.NaN,
                natureOfRootsBn = "অবৈধ ইনপুট",
                natureOfRootsEn = "Invalid Input",
                root1TextBn = "",
                root2TextBn = "",
                exactRootsTextBn = "",
                vertexX = 0.0,
                vertexY = 0.0,
                stepsBn = emptyList(),
                verificationBn = "",
                errorMessageBn = "সহগগুলোর মান অবশ্যই সসীম বাস্তব সংখ্যা হতে হবে।"
            )
        }

        if (abs(a) < 1e-12) {
            return QuadraticAnalysisResult(
                a = a, b = b, c = c,
                isValidQuadratic = false,
                discriminant = Double.NaN,
                natureOfRootsBn = "দ্বিঘাত সমীকরণ নয় (a = 0)",
                natureOfRootsEn = "Not a quadratic equation (a = 0)",
                root1TextBn = "",
                root2TextBn = "",
                exactRootsTextBn = "",
                vertexX = 0.0,
                vertexY = 0.0,
                stepsBn = emptyList(),
                verificationBn = "",
                errorMessageBn = "দ্বিঘাত সমীকরণের x² এর সহগ (a) কখনো শূন্য (০) হতে পারে না। a = 0 হলে এটি একঘাত সরল সমীকরণে পরিণত হয়।"
            )
        }

        val discriminant = b * b - 4.0 * a * c
        val vertexX = -b / (2.0 * a)
        val vertexY = a * vertexX * vertexX + b * vertexX + c

        val aBn = formatDoubleBn(a)
        val bBn = formatDoubleBn(b)
        val cBn = formatDoubleBn(c)
        val discBn = formatDoubleBn(discriminant)

        val steps = mutableListOf<Pair<String, String>>()
        steps.add(
            "ধাপ ১: আদর্শ দ্বিঘাত সমীকরণের সাথে তুলনা" to
                "প্রদত্ত সমীকরণকে ax² + bx + c = 0 এর সাথে তুলনা করে পাই: a = $aBn, b = $bBn, c = $cBn"
        )
        steps.add(
            "ধাপ ২: নিশ্চায়ক (Discriminant, Δ = b² − 4ac) নির্ণয়" to
                "Δ = ($bBn)² − ৪ × ($aBn) × ($cBn) = ${formatDoubleBn(b * b)} − (${formatDoubleBn(4.0 * a * c)}) = $discBn"
        )

        return when {
            discriminant > 1e-9 -> {
                val sqrtD = sqrt(discriminant)
                val x1 = (-b + sqrtD) / (2.0 * a)
                val x2 = (-b - sqrtD) / (2.0 * a)
                val isRationalDisc = try {
                    val rA = Rational.fromDouble(a)
                    val rB = Rational.fromDouble(b)
                    val rC = Rational.fromDouble(c)
                    val rDisc = (rB * rB) - (Rational.of(4) * rA * rC)
                    isBigIntPerfectSquare(rDisc.simplifiedNumerator) && isBigIntPerfectSquare(rDisc.simplifiedDenominator)
                } catch (_: Exception) {
                    abs(sqrtD - kotlin.math.round(sqrtD)) < 1e-9 &&
                        abs(discriminant - kotlin.math.round(discriminant)) < 1e-9
                }

                val natureBn = if (isRationalDisc) {
                    "মূলদ্বয় বাস্তব, অসমান এবং মূলদ (Real, Distinct & Rational)"
                } else {
                    "মূলদ্বয় বাস্তব, অসমান এবং অমূলদ (Real, Distinct & Irrational)"
                }

                val exactText = if (isRationalDisc) {
                    "x₁ = ${formatDoubleBn(x1)},  x₂ = ${formatDoubleBn(x2)}"
                } else {
                    "x = (${formatDoubleBn(-b)} ± √$discBn) / ${formatDoubleBn(2.0 * a)}  ≈  ${formatDoubleBn(x1)}, ${formatDoubleBn(x2)}"
                }

                steps.add(
                    "ধাপ ৩: মূলের প্রকৃতি নির্ধারণ" to
                        "যেহেতু নিশ্চায়ক Δ = $discBn > ০, তাই সমীকরণটির দুটি ভিন্ন বাস্তব মূল রয়েছে ($natureBn)।"
                )
                steps.add(
                    "ধাপ ৪: দ্বিঘাত সূত্র x = [−b ± √Δ] / (2a) প্রয়োগ" to
                        "x₁ = (${formatDoubleBn(-b)} + ${formatDoubleBn(sqrtD)}) ÷ ${formatDoubleBn(2.0 * a)} = ${formatDoubleBn(x1)}\n" +
                        "x₂ = (${formatDoubleBn(-b)} − ${formatDoubleBn(sqrtD)}) ÷ ${formatDoubleBn(2.0 * a)} = ${formatDoubleBn(x2)}"
                )

                val sumRoots = x1 + x2
                val prodRoots = x1 * x2
                val verify = "মূল ও সহগের সম্পর্ক যাচাই:\n" +
                    "• মূলদ্বয়ের যোগফল (x₁ + x₂) = ${formatDoubleBn(sumRoots)} = −b/a = ${formatDoubleBn(-b / a)}\n" +
                    "• মূলদ্বয়ের গুণফল (x₁ · x₂) = ${formatDoubleBn(prodRoots)} = c/a = ${formatDoubleBn(c / a)}"

                QuadraticAnalysisResult(
                    a = a, b = b, c = c,
                    isValidQuadratic = true,
                    discriminant = discriminant,
                    natureOfRootsBn = natureBn,
                    natureOfRootsEn = "Two distinct real roots",
                    root1TextBn = formatDoubleBn(x1),
                    root2TextBn = formatDoubleBn(x2),
                    exactRootsTextBn = exactText,
                    vertexX = vertexX,
                    vertexY = vertexY,
                    stepsBn = steps,
                    verificationBn = verify
                )
            }
            abs(discriminant) <= 1e-9 -> {
                val x = -b / (2.0 * a)
                val xBn = formatDoubleBn(x)
                steps.add(
                    "ধাপ ৩: মূলের প্রকৃতি নির্ধারণ" to
                        "যেহেতু নিশ্চায়ক Δ = ০, তাই সমীকরণটির মূলদ্বয় বাস্তব ও পরস্পর সমান (Real & Equal Roots)।"
                )
                steps.add(
                    "ধাপ ৪: মূল নির্ণয় x = −b / (2a)" to
                        "x₁ = x₂ = ${formatDoubleBn(-b)} ÷ ${formatDoubleBn(2.0 * a)} = $xBn"
                )
                val verify = "শুদ্ধি পরীক্ষা: x = $xBn সমীকরণে বসিয়ে পাই: ${formatDoubleBn(a * x * x + b * x + c)} = ০"
                QuadraticAnalysisResult(
                    a = a, b = b, c = c,
                    isValidQuadratic = true,
                    discriminant = 0.0,
                    natureOfRootsBn = "মূলদ্বয় বাস্তব এবং পরস্পর সমান (Real & Equal)",
                    natureOfRootsEn = "Real and equal roots",
                    root1TextBn = xBn,
                    root2TextBn = xBn,
                    exactRootsTextBn = "x₁ = x₂ = $xBn",
                    vertexX = vertexX,
                    vertexY = vertexY,
                    stepsBn = steps,
                    verificationBn = verify
                )
            }
            else -> {
                val absD = abs(discriminant)
                val realPart = -b / (2.0 * a)
                val imagPart = sqrt(absD) / abs(2.0 * a)
                val rBn = formatDoubleBn(realPart)
                val iBn = formatDoubleBn(imagPart)
                val root1 = "$rBn + ${iBn}i"
                val root2 = "$rBn − ${iBn}i"

                steps.add(
                    "ধাপ ৩: মূলের প্রকৃতি নির্ধারণ" to
                        "যেহেতু নিশ্চায়ক Δ = $discBn < ০ (ঋণাত্মক), তাই সমীকরণটির কোনো বাস্তব মূল নেই; মূলদ্বয় জটিল বা কাল্পনিক অনুবন্ধী (Complex Conjugate Roots)।"
                )
                steps.add(
                    "ধাপ ৪: জটিল মূল প্রকাশ (√(-1) = i)" to
                        "x = (${formatDoubleBn(-b)} ± i√${formatDoubleBn(absD)}) / ${formatDoubleBn(2.0 * a)}\n" +
                        "x₁ = $root1,  x₂ = $root2"
                )

                QuadraticAnalysisResult(
                    a = a, b = b, c = c,
                    isValidQuadratic = true,
                    discriminant = discriminant,
                    natureOfRootsBn = "বাস্তব মূল নেই — অনুবন্ধী জটিল মূল (Complex Conjugate)",
                    natureOfRootsEn = "Complex conjugate roots (No real roots)",
                    root1TextBn = root1,
                    root2TextBn = root2,
                    exactRootsTextBn = "x = $rBn ± ${iBn}i (বাস্তব সংখ্যার ডোমেনে সমাধান নেই)",
                    vertexX = vertexX,
                    vertexY = vertexY,
                    stepsBn = steps,
                    verificationBn = "নিশ্চায়ক Δ < ০ হওয়ায় লেখচিত্রটি x-অক্ষকে ছেদ করে না। শীর্ষবিন্দু: (${formatDoubleBn(vertexX)}, ${formatDoubleBn(vertexY)})।"
                )
            }
        }
    }

    // -------------------------------------------------------------
    // FACTORIZATION ENGINE (Integer Prime Power & Algebraic Quadratics)
    // -------------------------------------------------------------

    fun factorize(input: String): FactorizationResult {
        val clean = normalizeInputAscii(input)
        if (clean.isBlank()) {
            return FactorizationResult(
                isSuccess = false,
                originalInputBn = input,
                factoredFormBn = "",
                stepsBn = emptyList(),
                verificationBn = "",
                errorMessageBn = "উৎপাদকে বিশ্লেষণের জন্য একটি ধনাত্মক পূর্ণসংখ্যা (যেমন: 360) অথবা বীজগণিতীয় রাশি (যেমন: x^2 + 5x + 6 বা x^2 - 9) লিখুন।"
            )
        }

        // Case 1: Pure integer factorization
        val asLong = clean.toLongOrNull()
        if (asLong != null) {
            if (asLong <= 1L) {
                return FactorizationResult(
                    isSuccess = false,
                    originalInputBn = BengaliNumberFormatter.toBn(asLong),
                    factoredFormBn = "",
                    stepsBn = emptyList(),
                    verificationBn = "",
                    errorMessageBn = "মৌলিক উৎপাদকে বিশ্লেষণের জন্য ১ অপেক্ষা বড় ধনাত্মক পূর্ণসংখ্যা (≥ ২) দিন।"
                )
            }
            if (asLong > 1_000_000_000_000L) {
                return FactorizationResult(
                    isSuccess = false,
                    originalInputBn = BengaliNumberFormatter.toBn(asLong),
                    factoredFormBn = "",
                    stepsBn = emptyList(),
                    verificationBn = "",
                    errorMessageBn = "সংখ্যাটি সমর্থিত সীমার বাইরে। ১০¹² এর নিচের সংখ্যা দিন।"
                )
            }
            val factors = NumberSystemProvider.getPrimeFactors(asLong)
            val expForm = formatPrimePowerBn(asLong)
            val expandedForm = factors.joinToString(" × ") { BengaliNumberFormatter.toBn(it) }
            val allDivisors = NumberSystemProvider.getFactors(asLong)
            val nBn = BengaliNumberFormatter.toBn(asLong)

            val steps = listOf(
                "ধাপ ১: ক্ষুদ্রতম মৌলিক সংখ্যা দ্বারা ক্রমান্বয়ে ভাগ" to
                    "$nBn = $expandedForm",
                "ধাপ ২: সূচকীয় (Prime Power) আকারে প্রকাশ" to
                    "$nBn = $expForm",
                "ধাপ ৩: মোট গুণনীয়ক (উৎপাদক) তালিকা" to
                    "মোট গুণনীয়ক ${BengaliNumberFormatter.toBn(allDivisors.size)}টি: ${allDivisors.take(20).joinToString(", ") { BengaliNumberFormatter.toBn(it) }}"
            )
            return FactorizationResult(
                isSuccess = true,
                originalInputBn = nBn,
                factoredFormBn = if (expForm != expandedForm) "$expandedForm  (বা $expForm)" else expandedForm,
                stepsBn = steps,
                verificationBn = "গুণফল যাচাই: $expandedForm = $nBn"
            )
        }

        // Case 2: Algebraic Quadratic Trinomial ax^2 + bx + c or Difference of Squares x^2 - k
        val quadCoeffs = parseQuadraticExpression(clean)
        if (quadCoeffs != null) {
            val (a, b, c) = quadCoeffs
            if (a == 0L) {
                // Linear expression bx + c -> factor out GCD
                val g = NumberSystemProvider.gcd(b, c)
                if (g <= 1L) {
                    return FactorizationResult(
                        isSuccess = true,
                        originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                        factoredFormBn = BengaliNumberFormatter.toBn(clean),
                        stepsBn = listOf("ধাপ ১: সাধারণ উৎপাদক পরীক্ষা" to "পদগুলোর মধ্যে ১ ব্যতীত কোনো সাধারণ উৎপাদক নেই (মৌলিক রাশি)।"),
                        verificationBn = "রাশিটি ইতোমধ্যে সরলতম উৎপাদক আকারে রয়েছে।"
                    )
                }
                val bRem = b / g
                val cRem = c / g
                val sign = if (cRem >= 0) "+" else "-"
                val factored = "${BengaliNumberFormatter.toBn(g)}(${formatCoeffVarBn(bRem, "x")} $sign ${BengaliNumberFormatter.toBn(abs(cRem))})"
                return FactorizationResult(
                    isSuccess = true,
                    originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                    factoredFormBn = factored,
                    stepsBn = listOf(
                        "ধাপ ১: গ.সা.গু (সাধারণ উৎপাদক) কমন নেওয়া" to
                            "${BengaliNumberFormatter.toBn(b)} এবং ${BengaliNumberFormatter.toBn(c)} এর গ.সা.গু = ${BengaliNumberFormatter.toBn(g)}",
                        "ধাপ ২: উৎপাদক আকারে প্রকাশ" to factored
                    ),
                    verificationBn = "গুণ করে যাচাই: $factored = ${BengaliNumberFormatter.toBn(clean)}"
                )
            }

            // Quadratic ax^2 + bx + c using BigInteger to prevent overflow
            val bigA = BigInteger.valueOf(a)
            val bigB = BigInteger.valueOf(b)
            val bigC = BigInteger.valueOf(c)
            val bigDisc = bigB.multiply(bigB).subtract(BigInteger.valueOf(4L).multiply(bigA).multiply(bigC))
            if (bigDisc < BigInteger.ZERO) {
                return FactorizationResult(
                    isSuccess = false,
                    originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                    factoredFormBn = "",
                    stepsBn = emptyList(),
                    verificationBn = "",
                    errorMessageBn = "নিশ্চায়ক Δ = b² − 4ac = ${BengaliNumberFormatter.toBn(bigDisc.toString())} < ০ হওয়ায় বাস্তব মূলদ সংখ্যায় রাশিটির রৈখিক উৎপাদক সম্ভব নয়।"
                )
            }
            val bigSqrtD = bigDisc.sqrt()
            if (bigSqrtD.multiply(bigSqrtD) != bigDisc) {
                return FactorizationResult(
                    isSuccess = false,
                    originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                    factoredFormBn = "",
                    stepsBn = emptyList(),
                    verificationBn = "",
                    errorMessageBn = "নিশ্চায়ক Δ = ${BengaliNumberFormatter.toBn(bigDisc.toString())} পূর্ণবর্গ সংখ্যা না হওয়ায় পূর্ণসংখ্যায় মধ্যপদ বিশ্লেষণ (Middle-term breakup) সম্ভব নয়।"
                )
            }

            // Roots r1 = (-b + sqrtD)/(2a), r2 = (-b - sqrtD)/(2a)
            val twoA = BigInteger.valueOf(2L).multiply(bigA)
            val r1 = Rational(bigB.negate().add(bigSqrtD), twoA)
            val r2 = Rational(bigB.negate().subtract(bigSqrtD), twoA)

            // a * (x - r1) * (x - r2)
            // Write (x - p/q) as (qx - p)
            val q1 = r1.simplifiedDenominator.toLong()
            val p1 = r1.simplifiedNumerator.toLong()
            val q2 = r2.simplifiedDenominator.toLong()
            val p2 = r2.simplifiedNumerator.toLong()

            val outerScale = a / (q1 * q2)
            val factor1 = formatLinearFactorBn(q1, -p1)
            val factor2 = formatLinearFactorBn(q2, -p2)
            val prefix = when (outerScale) {
                1L -> ""
                -1L -> "-"
                else -> BengaliNumberFormatter.toBn(outerScale)
            }
            val finalFactored = if (factor1 == factor2) "$prefix$factor1²" else "$prefix$factor1$factor2"

            // BUG-89 FIX: Both middle-term components must be scaled by outerScale so m1 + m2 = b and m1 * m2 = a * c
            val m1 = (-p1) * q2 * outerScale
            val m2 = (-p2) * q1 * outerScale

            val steps = mutableListOf<Pair<String, String>>()
            if (b == 0L && c < 0L) {
                steps.add(
                    "ধাপ ১: বর্গের অন্তর সূত্র (a² − b² = (a + b)(a − b)) প্রয়োগ" to
                        "${BengaliNumberFormatter.toBn(input.trim())} = $finalFactored"
                )
            } else {
                steps.add(
                    "ধাপ ১: মধ্যপদ বিশ্লেষণ (Middle-Term Breakup)" to
                        "প্রান্তীয় পদদ্বয়ের গুণফল = ${BengaliNumberFormatter.toBn(a * c)} এবং মধ্যপদ = ${BengaliNumberFormatter.toBn(b)}।\n" +
                        "সংখ্যা দুটি হলো ${BengaliNumberFormatter.toBn(m1)} এবং ${BengaliNumberFormatter.toBn(m2)}।"
                )
                steps.add(
                    "ধাপ ২: জোড়ায় জোড়ায় সাধারণ উৎপাদক কমন নেওয়া" to
                        "উৎপাদক বিশ্লেষিত রূপ = $finalFactored"
                )
            }

            return FactorizationResult(
                isSuccess = true,
                originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                factoredFormBn = finalFactored,
                stepsBn = steps,
                verificationBn = "উৎপাদকদ্বয় গুণ করে পুনরায় মূল রাশি ${BengaliNumberFormatter.toBn(input.trim())} পাওয়া যায়।"
            )
        }

        return FactorizationResult(
            isSuccess = false,
            originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
            factoredFormBn = "",
            stepsBn = emptyList(),
            verificationBn = "",
            errorMessageBn = "সমর্থিত ফরম্যাট: ধনাত্মক পূর্ণসংখ্যা (যেমন: 120) অথবা দ্বিঘাত বীজগণিতীয় রাশি (যেমন: x^2 + 5x + 6, x^2 - 16, 2x^2 + 7x + 3)।"
        )
    }

    private fun expandAlgebraicParentheses(expr: String): String {
        var s = expr.replace(" ", "").replace("²", "^2").replace("X", "x")
        val parenRegex = Regex("""([+-]?\d*)\*?\(([^()]+)\)""")
        var safety = 0
        while (parenRegex.containsMatchIn(s) && safety < 20) {
            safety++
            val next = parenRegex.replace(s) { m ->
                val rawMult = m.groupValues[1]
                val mult = when (rawMult) {
                    "", "+" -> 1L
                    "-" -> -1L
                    else -> rawMult.toLongOrNull() ?: return@replace m.value
                }
                val inside = m.groupValues[2].replace("-", "+-")
                val innerTokens = inside.split("+").filter { it.isNotEmpty() }
                if (innerTokens.isEmpty()) return@replace m.value
                val expandedTerms = mutableListOf<String>()
                for (tok in innerTokens) {
                    when {
                        tok.contains("x^2") -> {
                            val cStr = tok.replace("x^2", "").replace("*", "")
                            val c = when (cStr) {
                                "", "+" -> 1L
                                "-" -> -1L
                                else -> cStr.toLongOrNull() ?: return@replace m.value
                            }
                            val prod = mult * c
                            expandedTerms.add(if (prod >= 0) "+${prod}x^2" else "${prod}x^2")
                        }
                        tok.contains("x") -> {
                            val cStr = tok.replace("x", "").replace("*", "")
                            val c = when (cStr) {
                                "", "+" -> 1L
                                "-" -> -1L
                                else -> cStr.toLongOrNull() ?: return@replace m.value
                            }
                            val prod = mult * c
                            expandedTerms.add(if (prod >= 0) "+${prod}x" else "${prod}x")
                        }
                        else -> {
                            val c = tok.toLongOrNull() ?: return@replace m.value
                            val prod = mult * c
                            expandedTerms.add(if (prod >= 0) "+$prod" else "$prod")
                        }
                    }
                }
                expandedTerms.joinToString("")
            }
            if (next == s) break
            s = next
        }
        return s.removePrefix("+")
    }

    private fun parseQuadraticExpression(clean: String): Triple<Long, Long, Long>? {
        return try {
            val s = expandAlgebraicParentheses(clean)
            if (s.contains("(") || s.contains(")")) return null
            // Reject unsupported characters
            if (s.any { it.isLetter() && it != 'x' && it != 'X' }) return null
            val normalized = s.replace("X", "x").replace("-", "+-")
            val tokens = normalized.split("+").filter { it.isNotEmpty() }
            if (tokens.isEmpty()) return null
            var a = 0L
            var b = 0L
            var c = 0L
            for (tok in tokens) {
                when {
                    tok.contains("x^2") -> {
                        val coeffStr = tok.replace("x^2", "").replace("*", "")
                        val coeff = when (coeffStr) {
                            "", "+" -> 1L
                            "-" -> -1L
                            else -> coeffStr.toLongOrNull() ?: return null
                        }
                        a += coeff
                    }
                    tok.contains("x") -> {
                        val coeffStr = tok.replace("x", "").replace("*", "")
                        val coeff = when (coeffStr) {
                            "", "+" -> 1L
                            "-" -> -1L
                            else -> coeffStr.toLongOrNull() ?: return null
                        }
                        b += coeff
                    }
                    else -> {
                        val constVal = tok.toLongOrNull() ?: return null
                        c += constVal
                    }
                }
            }
            Triple(a, b, c)
        } catch (_: Exception) {
            null
        }
    }

    private fun formatCoeffVarBn(coeff: Long, variable: String): String {
        return when (coeff) {
            1L -> variable
            -1L -> "-$variable"
            else -> "${BengaliNumberFormatter.toBn(coeff)}$variable"
        }
    }

    private fun formatLinearFactorBn(q: Long, constTerm: Long): String {
        val xPart = formatCoeffVarBn(q, "x")
        return when {
            constTerm == 0L -> "($xPart)"
            constTerm > 0L -> "($xPart + ${BengaliNumberFormatter.toBn(constTerm)})"
            else -> "($xPart − ${BengaliNumberFormatter.toBn(abs(constTerm))})"
        }
    }

    // -------------------------------------------------------------
    // SIMPLIFICATION ENGINE (Fractions, Radicals √n, Linear Algebraic Terms)
    // -------------------------------------------------------------

    fun simplify(input: String): SimplificationResult {
        val clean = normalizeInputAscii(input)
        if (clean.isBlank()) {
            return SimplificationResult(
                isSuccess = false,
                originalInputBn = input,
                simplifiedFormBn = "",
                isExact = true,
                stepsBn = emptyList(),
                verificationBn = "",
                errorMessageBn = "সরলীকরণের জন্য ভগ্নাংশ (যেমন: 48/72), করণী (যেমন: √72 বা sqrt(50)), অথবা বীজগণিতীয় রাশি (যেমন: 3x + 5 - x + 7) লিখুন।"
            )
        }

        // Case 1: Radical simplification: √n or sqrt(n) or [+-]k√n (BUG-88 FIX)
        val radicalMatch = Regex("""^([+-])?\s*(\d+)?\s*(?:√|sqrt\()\s*([+-]?\d+)\s*\)?$""", RegexOption.IGNORE_CASE).find(clean)
        if (radicalMatch != null) {
            val sign = if (radicalMatch.groupValues[1] == "-") -1L else 1L
            val rawOuterStr = radicalMatch.groupValues[2]
            val rawOuter = if (rawOuterStr.isEmpty()) 1L else rawOuterStr.toLongOrNull()
            val radicandStr = radicalMatch.groupValues[3]
            if (radicandStr.startsWith("-")) {
                return SimplificationResult(
                    isSuccess = false,
                    originalInputBn = BengaliNumberFormatter.toBn(input),
                    simplifiedFormBn = "",
                    isExact = false,
                    stepsBn = emptyList(),
                    verificationBn = "",
                    errorMessageBn = "বর্গমূলের ভেতরের সংখ্যাটি অঋণাত্মক পূর্ণসংখ্যা হতে হবে।"
                )
            }
            val radicand = radicandStr.toLongOrNull()
            if (radicand == null || rawOuter == null || radicand > 1_000_000_000_000L || rawOuter > 1_000_000L) {
                return SimplificationResult(
                    isSuccess = false,
                    originalInputBn = BengaliNumberFormatter.toBn(input),
                    simplifiedFormBn = "",
                    isExact = false,
                    stepsBn = emptyList(),
                    verificationBn = "",
                    errorMessageBn = "বর্গমূলের ভেতরের সংখ্যাটি ১,০০০,০০০,০০০,০০০ (১০¹²) এবং বাইরের সহগ ১০,০০,০০০ (১০⁶)-এর মধ্যে হতে হবে।"
                )
            }
            val outerCoeff = sign * rawOuter
            if (radicand == 0L || outerCoeff == 0L) {
                return SimplificationResult(
                    isSuccess = true,
                    originalInputBn = BengaliNumberFormatter.toBn(input),
                    simplifiedFormBn = "০",
                    isExact = true,
                    stepsBn = listOf("ধাপ ১: শূন্যের বর্গমূল" to "√০ = ০"),
                    verificationBn = "০² = ০"
                )
            }

            // Find largest square factor k^2 dividing radicand (bounded by d <= 10^6)
            var largestSquareRoot = 1L
            var d = 2L
            var temp = radicand
            while (d * d <= temp) {
                while (temp % (d * d) == 0L) {
                    largestSquareRoot *= d
                    temp /= (d * d)
                }
                d++
            }
            val totalOuter = outerCoeff * largestSquareRoot
            val remainingInside = temp
            val approx = outerCoeff * sqrt(radicand.toDouble())

            val exactStr = when {
                remainingInside == 1L -> BengaliNumberFormatter.toBn(totalOuter)
                totalOuter == 1L -> "√${BengaliNumberFormatter.toBn(remainingInside)}"
                totalOuter == -1L -> "-√${BengaliNumberFormatter.toBn(remainingInside)}"
                else -> "${BengaliNumberFormatter.toBn(totalOuter)}√${BengaliNumberFormatter.toBn(remainingInside)}"
            }

            val isPerfect = remainingInside == 1L
            val steps = listOf(
                "ধাপ ১: পূর্ণবর্গ গুণনীয়ক পৃথকীকরণ" to
                    "${BengaliNumberFormatter.toBn(radicand)} = ${BengaliNumberFormatter.toBn(largestSquareRoot * largestSquareRoot)} × ${BengaliNumberFormatter.toBn(remainingInside)} = (${BengaliNumberFormatter.toBn(largestSquareRoot)})² × ${BengaliNumberFormatter.toBn(remainingInside)}",
                "ধাপ ২: বর্গমূল চিহ্নের বাইরে পূর্ণবর্গ মূল আনয়ন" to
                    "সঠিক মান (Exact Form) = $exactStr" + if (!isPerfect) "\nআসন্ন দশমিক মান (Approximate) ≈ ${formatDoubleBn(approx)}" else ""
            )

            val squaredMag = BigInteger.valueOf(outerCoeff)
                .multiply(BigInteger.valueOf(outerCoeff))
                .multiply(BigInteger.valueOf(radicand))

            return SimplificationResult(
                isSuccess = true,
                originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                simplifiedFormBn = if (isPerfect) "$exactStr (পূর্ণবর্গ — Exact)" else "$exactStr  (আসন্ন মান ≈ ${formatDoubleBn(approx)})",
                isExact = isPerfect,
                approximateValueBn = formatDoubleBn(approx),
                stepsBn = steps,
                verificationBn = "বর্গ করে যাচাই: ($exactStr)² = ${BengaliNumberFormatter.toBn(squaredMag.toString())}"
            )
        }

        // Case 2: Exact Fraction a/b (arbitrary precision via BigInteger)
        if (clean.matches(Regex("""^[+-]?\d+\s*/\s*[+-]?\d+$"""))) {
            val parts = clean.split("/")
            val nBig = parts[0].trim().toBigIntegerOrNull() ?: BigInteger.ZERO
            val dBig = parts[1].trim().toBigIntegerOrNull() ?: BigInteger.ZERO
            if (dBig == BigInteger.ZERO) {
                return SimplificationResult(
                    isSuccess = false,
                    originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                    simplifiedFormBn = "",
                    isExact = false,
                    stepsBn = emptyList(),
                    verificationBn = "",
                    errorMessageBn = "ভগ্নাংশের হর শূন্য (০) হতে পারে না (শূন্য দ্বারা ভাগ অসংজ্ঞায়িত)।"
                )
            }
            val rat = Rational(nBig, dBig)
            val gBig = nBig.abs().gcd(dBig.abs())
            val mixed = rat.toMixedFractionBn()
            val decStr = BengaliNumberFormatter.toBn(rat.toBigDecimal(6).toPlainString())
            val finalStr = buildString {
                append(rat.toBnString())
                if (mixed != null) append(" (মিশ্র: $mixed)")
                append(" [দশমিক: $decStr]")
            }
            val steps = listOf(
                "ধাপ ১: লব ও হরের গ.সা.গু (GCD) নির্ণয়" to
                    "|${BengaliNumberFormatter.toBn(nBig.toString())}| এবং |${BengaliNumberFormatter.toBn(dBig.toString())}| এর গ.সা.গু = ${BengaliNumberFormatter.toBn(gBig.toString())}",
                "ধাপ ২: লব ও হরকে গ.সা.গু দ্বারা ভাগ করে লঘিষ্ঠ আকারে প্রকাশ" to
                    "সরলতম ভগ্নাংশ = ${rat.toBnString()}"
            )
            return SimplificationResult(
                isSuccess = true,
                originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                simplifiedFormBn = finalStr,
                isExact = true,
                approximateValueBn = decStr,
                stepsBn = steps,
                verificationBn = "তুল্য ভগ্নাংশ যাচাই: ${rat.toBnString()} = ${BengaliNumberFormatter.toBn(nBig.toString())}/${BengaliNumberFormatter.toBn(dBig.toString())}"
            )
        }

        // Case 3: Algebraic like-terms collection (e.g. 3x + 5 - x + 2 or 2(x + 3) + 4x - 1)
        if (clean.contains("x", ignoreCase = true)) {
            val quad = parseQuadraticExpression(clean)
            if (quad != null) {
                val (a, b, c) = quad
                val parts = mutableListOf<String>()
                if (a != 0L) parts.add(formatCoeffVarBn(a, "x²"))
                if (b != 0L) {
                    val bStr = formatCoeffVarBn(abs(b), "x")
                    if (parts.isEmpty()) parts.add(if (b < 0) "-$bStr" else bStr)
                    else parts.add(if (b < 0) "− $bStr" else "+ $bStr")
                }
                if (c != 0L || parts.isEmpty()) {
                    val cStr = BengaliNumberFormatter.toBn(abs(c))
                    if (parts.isEmpty()) parts.add(if (c < 0) "-$cStr" else cStr)
                    else parts.add(if (c < 0) "− $cStr" else "+ $cStr")
                }
                val simplified = parts.joinToString(" ")
                val steps = buildList {
                    if (clean.contains("(")) {
                        val expandedAscii = expandAlgebraicParentheses(clean)
                        add(
                            "ধাপ ১: বণ্টন বিধি অনুসারে বন্ধনী সম্প্রসারণ (Distributive Expansion)" to
                                "${BengaliNumberFormatter.toBn(input.trim())} = ${BengaliNumberFormatter.toBn(expandedAscii)}"
                        )
                        add(
                            "ধাপ ২: সদৃশ পদগুলোর (Like Terms) সহগ একত্রীকরণ" to
                                "x² এর সহগ সমষ্টি = ${BengaliNumberFormatter.toBn(a)}, x এর সহগ সমষ্টি = ${BengaliNumberFormatter.toBn(b)}, ধ্রুবক সমষ্টি = ${BengaliNumberFormatter.toBn(c)}"
                        )
                        add("ধাপ ৩: সরলীকৃত বীজগণিতীয় রাশি" to simplified)
                    } else {
                        add(
                            "ধাপ ১: সদৃশ পদগুলোর (Like Terms) সহগ একত্রীকরণ" to
                                "x² এর সহগ সমষ্টি = ${BengaliNumberFormatter.toBn(a)}, x এর সহগ সমষ্টি = ${BengaliNumberFormatter.toBn(b)}, ধ্রুবক সমষ্টি = ${BengaliNumberFormatter.toBn(c)}"
                        )
                        add("ধাপ ২: সরলীকৃত বীজগণিতীয় রাশি" to simplified)
                    }
                }
                return SimplificationResult(
                    isSuccess = true,
                    originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                    simplifiedFormBn = simplified,
                    isExact = true,
                    stepsBn = steps,
                    verificationBn = "সদৃশ পদগুলো যোগ-বিয়োগ করে আদর্শ বহুপদী আকারে সাজানো হয়েছে।"
                )
            }
        }

        // Case 4: General arithmetic expression with powers/roots
        val arithRes = MathProblemSolverEngine.solveArithmetic(input, SolverMode.LEARNING)
        return if (arithRes.isSuccess) {
            SimplificationResult(
                isSuccess = true,
                originalInputBn = arithRes.originalProblemBn,
                simplifiedFormBn = arithRes.finalAnswerBn,
                isExact = !arithRes.finalAnswerBn.contains("≈"),
                stepsBn = arithRes.steps.map { "ধাপ ${BengaliNumberFormatter.toBn(it.stepIndex)}: ${it.titleBn}" to "${it.expressionOrMathBn}\n${it.explanationBn}" },
                verificationBn = arithRes.verificationBn ?: "BODMAS অনুসারে সরলীকৃত।"
            )
        } else {
            SimplificationResult(
                isSuccess = false,
                originalInputBn = BengaliNumberFormatter.toBn(input.trim()),
                simplifiedFormBn = "",
                isExact = false,
                stepsBn = emptyList(),
                verificationBn = "",
                errorMessageBn = arithRes.errorMessageBn
            )
        }
    }

    fun normalizeInputAscii(raw: String): String {
        val map = mapOf(
            '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
            '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9',
            '−' to '-', '–' to '-', '×' to '*', '÷' to '/'
        )
        val sb = StringBuilder()
        for (c in raw.trim()) {
            sb.append(map[c] ?: c)
        }
        return sb.toString()
    }

    // -------------------------------------------------------------
    // MODULAR ARITHMETIC, DIVISOR ANALYZER & SIMULTANEOUS EQUATIONS
    // -------------------------------------------------------------

    fun modPowStepsBn(base: Long, exponent: Long, modulus: Long): ModularExponentiationResult {
        val baseBn = BengaliNumberFormatter.toBn(base)
        val rawExpBn = BengaliNumberFormatter.toBn(exponent)
        val rawModBn = BengaliNumberFormatter.toBn(modulus)

        if (modulus <= 0L) {
            return ModularExponentiationResult(
                base = base,
                exponent = exponent,
                modulus = modulus,
                remainder = 0L,
                congruenceNotationBn = "অসংজ্ঞায়িত (Modulus ≤ ০)",
                stepsBn = listOf(
                    "ধাপ ১: ভাজক (Modulus) যাচাই" to
                        "প্রদত্ত ভাজক m = $rawModBn ≤ ০। মডুলার পাটিগণিতে ভাজক অবশ্যই ধনাত্মক পূর্ণসংখ্যা (m ≥ ১) হতে হবে।"
                ),
                theoremNoteBn = "শূন্য বা ঋণাত্মক ভাজক দ্বারা মডুলার ভাগশেষ সংজ্ঞায়িত নয়।"
            )
        }

        if (modulus == 1L) {
            val safeExp = exponent.coerceAtLeast(0L)
            val expBn = BengaliNumberFormatter.toBn(safeExp)
            return ModularExponentiationResult(
                base = base,
                exponent = safeExp,
                modulus = 1L,
                remainder = 0L,
                congruenceNotationBn = "$baseBn^$expBn ≡ ০ (mod ১)",
                stepsBn = listOf(
                    "ধাপ ১: একক ভাজক (Modulus = ১) বিধি" to
                        "যেকোনো পূর্ণসংখ্যাকে ১ দ্বারা ভাগ করলে ভাগশেষ সর্বদা ০ হয়। অতএব $baseBn^$expBn ≡ ০ (mod ১)।"
                ),
                theoremNoteBn = "সকল পূর্ণসংখ্যা ১ দ্বারা নিঃশেষে বিভাজ্য, তাই mod ১ এ ভাগশেষ সর্বদা ০।"
            )
        }

        val safeMod = modulus
        val safeExp = exponent.coerceAtLeast(0L)
        val bigBase = BigInteger.valueOf(base)
        val bigExp = BigInteger.valueOf(safeExp)
        val bigMod = BigInteger.valueOf(safeMod)
        val bigReducedBase = bigBase.mod(bigMod)
        val reducedBase = bigReducedBase.toLong()
        val rem = bigBase.modPow(bigExp, bigMod).toLong()

        val expBn = BengaliNumberFormatter.toBn(safeExp)
        val modBn = BengaliNumberFormatter.toBn(safeMod)
        val redBaseBn = BengaliNumberFormatter.toBn(reducedBase)
        val remBn = BengaliNumberFormatter.toBn(rem)

        val steps = mutableListOf<Pair<String, String>>()
        if (exponent < 0L) {
            steps.add(
                "ধাপ ০: ঋণাত্মক ঘাত সতর্কবার্তা" to
                    "প্রদত্ত ঘাত ($rawExpBn) ঋণাত্মক হওয়ায় অঋণাত্মক সূচক ০ ধরে হিসাব করা হয়েছে।"
            )
        }
        steps.add(
            "ধাপ ১: ভিত্তিকে ভাজক (Modulus) দ্বারা লঘুকরণ" to
                "$baseBn ≡ $redBaseBn (mod $modBn)"
        )

        var curBaseBig = bigReducedBase
        var curExp = safeExp
        var accBig = BigInteger.ONE.mod(bigMod)
        val traceLines = mutableListOf<String>()
        var bitStep = 1
        while (curExp > 0L && bitStep <= 24) {
            if (curExp % 2L == 1L) {
                val nextAccBig = accBig.multiply(curBaseBig).mod(bigMod)
                traceLines.add(
                    "ঘাত বিজোড় (${BengaliNumberFormatter.toBn(curExp)}): গুণফল = (${BengaliNumberFormatter.toBn(accBig.toLong())} × ${BengaliNumberFormatter.toBn(curBaseBig.toLong())}) mod $modBn = ${BengaliNumberFormatter.toBn(nextAccBig.toLong())}"
                )
                accBig = nextAccBig
            }
            curExp /= 2L
            if (curExp > 0L) {
                val nextBaseBig = curBaseBig.multiply(curBaseBig).mod(bigMod)
                traceLines.add(
                    "বর্গকরণ: ${BengaliNumberFormatter.toBn(curBaseBig.toLong())}² mod $modBn = ${BengaliNumberFormatter.toBn(nextBaseBig.toLong())} (বাকি ঘাত: ${BengaliNumberFormatter.toBn(curExp)})"
                )
                curBaseBig = nextBaseBig
            }
            bitStep++
        }
        if (traceLines.isEmpty()) {
            traceLines.add("ঘাত ০ হওয়ায় যেকোনো সংখ্যার ঘাত ০ = ১ ≡ ${BengaliNumberFormatter.toBn(1L % safeMod)} (mod $modBn)")
        }
        steps.add(
            "ধাপ ২: বাইনারি সূচকীকরণ (Binary Exponentiation) ধাপসমূহ" to
                traceLines.joinToString("\n")
        )
        steps.add(
            "ধাপ ৩: চূড়ান্ত ভাগশেষ (Modular Remainder)" to
                "$baseBn^$expBn কে $modBn দ্বারা ভাগ করলে ভাগশেষ হবে $remBn।"
        )

        val isModPrime = safeMod <= 1_000_000_000_000L && NumberSystemProvider.isPrime(safeMod)
        val theoremNote = if (isModPrime && reducedBase != 0L) {
            "ফার্মার ক্ষুদ্র উপপাদ্য (Fermat's Little Theorem): যেহেতু $modBn মৌলিক এবং gcd($baseBn, $modBn) = ১, তাই $baseBn^${BengaliNumberFormatter.toBn(safeMod - 1)} ≡ ১ (mod $modBn)।"
        } else {
            "অয়লারের টোশেন্ট উপপাদ্য (Euler's Totient Theorem): gcd(a, m) = ১ হলে a^φ(m) ≡ ১ (mod m)।"
        }

        return ModularExponentiationResult(
            base = base,
            exponent = safeExp,
            modulus = safeMod,
            remainder = rem,
            congruenceNotationBn = "$baseBn^$expBn ≡ $remBn (mod $modBn)",
            stepsBn = steps,
            theoremNoteBn = theoremNote
        )
    }

    fun analyzeDivisorsAndTotient(n: Long): DivisorAnalysisResult {
        if (n <= 0L) {
            val rawBn = BengaliNumberFormatter.toBn(n)
            return DivisorAnalysisResult(
                number = n,
                primePowerBn = "অসংজ্ঞায়িত (N ≤ ০)",
                divisorCount = 0L,
                divisorSum = 0L,
                eulerTotient = 0L,
                divisorsPreviewBn = "প্রযোজ্য নয়",
                stepsBn = listOf(
                    "ধাপ ১: ইনপুট ডোমেইন যাচাই" to
                        "প্রদত্ত সংখ্যা N = $rawBn ≤ ০। ধনাত্মক ভাজক সংখ্যা d(N), ভাজক সমষ্টি σ(N) এবং অয়লারের টোশেন্ট ফাংশন φ(N) শুধুমাত্র ধনাত্মক পূর্ণসংখ্যা (N ≥ ১) এর জন্য সংজ্ঞায়িত।"
                )
            )
        }

        val safeN = n.coerceIn(1L, 1_000_000_000_000L)
        val nBn = BengaliNumberFormatter.toBn(safeN)
        if (safeN == 1L) {
            return DivisorAnalysisResult(
                number = 1L,
                primePowerBn = "১",
                divisorCount = 1L,
                divisorSum = 1L,
                eulerTotient = 1L,
                divisorsPreviewBn = "১",
                stepsBn = listOf(
                    "ধাপ ১: মৌলিক উৎপাদক বিশ্লেষণ" to "১ এর একমাত্র ধনাত্মক ভাজক ১। d(১) = ১, σ(১) = ১, φ(১) = ১।"
                )
            )
        }

        val expMap = primeFactorizationExponents(safeN)
        val primePowerStr = formatPrimePowerBn(safeN)
        var divCount = 1L
        var divSumBig = BigInteger.ONE
        var totient = safeN

        val countTerms = mutableListOf<String>()
        val sumTerms = mutableListOf<String>()

        for ((p, a) in expMap) {
            divCount *= (a + 1L)
            countTerms.add("(${BengaliNumberFormatter.toBn(a)} + ১)")

            // Overflow-safe geometric series 1 + p + p^2 + ... + p^a using BigInteger
            val pBig = BigInteger.valueOf(p)
            val geomFactorBig = pBig.pow(a + 1).subtract(BigInteger.ONE).divide(pBig.subtract(BigInteger.ONE))
            divSumBig = divSumBig.multiply(geomFactorBig)
            sumTerms.add("(${BengaliNumberFormatter.toBn(p)}^${BengaliNumberFormatter.toBn(a + 1)} − ১)/(${BengaliNumberFormatter.toBn(p)} − ১)")

            totient = (totient / p) * (p - 1L)
        }

        val divSum = divSumBig.coerceIn(BigInteger.ONE, BigInteger.valueOf(Long.MAX_VALUE)).toLong()
        val factors = if (safeN <= 100_000L) NumberSystemProvider.getFactors(safeN) else emptyList()
        val preview = if (factors.isNotEmpty()) {
            factors.take(16).joinToString(", ") { BengaliNumberFormatter.toBn(it) } +
                if (factors.size > 16) " ..." else ""
        } else {
            "১, ..., $nBn"
        }

        val steps = listOf(
            "ধাপ ১: মৌলিক ঘাত আকারে প্রকাশ (Prime Power Factorization)" to
                "$nBn = $primePowerStr",
            "ধাপ ২: মোট ভাজক সংখ্যা d(N) = Π(aᵢ + 1)" to
                "d($nBn) = ${countTerms.joinToString(" × ")} = ${BengaliNumberFormatter.toBn(divCount)}টি",
            "ধাপ ৩: সকল ভাজকের সমষ্টি σ(N) = Π[(pᵢ^(aᵢ+1) − 1)/(pᵢ − 1)]" to
                "σ($nBn) = ${sumTerms.joinToString(" × ")} = ${BengaliNumberFormatter.toBn(divSumBig.toString())}",
            "ধাপ ৪: অয়লারের টোশেন্ট ফাংশন φ(N) [১ থেকে $nBn পর্যন্ত সহমৌলিক সংখ্যা]" to
                "φ($nBn) = ${BengaliNumberFormatter.toBn(totient)}"
        )

        return DivisorAnalysisResult(
            number = safeN,
            primePowerBn = primePowerStr,
            divisorCount = divCount,
            divisorSum = divSum,
            eulerTotient = totient,
            divisorsPreviewBn = preview,
            stepsBn = steps
        )
    }

    fun solveSimultaneousLinear(
        a1: Double,
        b1: Double,
        c1: Double,
        a2: Double,
        b2: Double,
        c2: Double
    ): SimultaneousLinearResult {
        if (listOf(a1, b1, c1, a2, b2, c2).any { it.isNaN() || it.isInfinite() }) {
            return SimultaneousLinearResult(
                isUniqueSolution = false,
                isInfiniteSolutions = false,
                isNoSolution = true,
                determinant = Double.NaN,
                xExactBn = "অসংজ্ঞায়িত",
                yExactBn = "অসংজ্ঞায়িত",
                statusBn = "অবৈধ ইনপুট: সহগগুলোর মান অবশ্যই সসীম বাস্তব সংখ্যা হতে হবে।",
                stepsBn = listOf(
                    "ধাপ ১: ইনপুট যাচাই" to "সহগগুলোর মধ্যে অসংজ্ঞায়িত (NaN) বা অসীম (Infinity) মান পাওয়া গেছে।"
                ),
                verificationBn = "সসীম বাস্তব সংখ্যা দিয়ে পুনরায় চেষ্টা করুন।"
            )
        }

        val det = a1 * b2 - a2 * b1
        val detX = c1 * b2 - c2 * b1
        val detY = a1 * c2 - a2 * c1

        val eq1Bn = "${formatDoubleBn(a1)}x + ${formatDoubleBn(b1)}y = ${formatDoubleBn(c1)}"
        val eq2Bn = "${formatDoubleBn(a2)}x + ${formatDoubleBn(b2)}y = ${formatDoubleBn(c2)}"

        if (abs(det) < 1e-10) {
            val eq1ZeroCoeffs = abs(a1) < 1e-10 && abs(b1) < 1e-10
            val eq2ZeroCoeffs = abs(a2) < 1e-10 && abs(b2) < 1e-10
            val eq1Contradiction = eq1ZeroCoeffs && abs(c1) >= 1e-10
            val eq2Contradiction = eq2ZeroCoeffs && abs(c2) >= 1e-10

            // Also check proportional ratio consistency when neither equation is trivial 0x+0y=0
            val isConsistentWhenDetZero = if (eq1Contradiction || eq2Contradiction) {
                false
            } else {
                abs(detX) < 1e-10 && abs(detY) < 1e-10
            }

            val isInfinite = isConsistentWhenDetZero
            val status = if (isInfinite) {
                "অসংখ্য সমাধান বিদ্যমান (সমীকরণদ্বয় সমাপতিত / নির্ভরশীল)"
            } else {
                "কোনো সমাধান নেই (সমীকরণদ্বয় অসামঞ্জস্যপূর্ণ / সমান্তরাল রেখা)"
            }
            return SimultaneousLinearResult(
                isUniqueSolution = false,
                isInfiniteSolutions = isInfinite,
                isNoSolution = !isInfinite,
                determinant = 0.0,
                xExactBn = "অসংজ্ঞায়িত",
                yExactBn = "অসংজ্ঞায়িত",
                statusBn = status,
                stepsBn = listOf(
                    "ধাপ ১: সহগ নির্ণায়ক (Determinant D = a₁b₂ − a₂b₁) পরীক্ষা" to
                        "D = (${formatDoubleBn(a1)} × ${formatDoubleBn(b2)}) − (${formatDoubleBn(a2)} × ${formatDoubleBn(b1)}) = ০",
                    "ধাপ ২: সমাধানের প্রকৃতি" to status
                ),
                verificationBn = "সমীকরণ জোট: (১) $eq1Bn এবং (২) $eq2Bn"
            )
        }

        val xVal = detX / det
        val yVal = detY / det
        val xRat = runCatching { Rational.fromDouble(detX) / Rational.fromDouble(det) }.getOrNull()
        val yRat = runCatching { Rational.fromDouble(detY) / Rational.fromDouble(det) }.getOrNull()

        val xText = if (xRat != null && !xRat.isInteger) {
            "${xRat.toBnString()} (বা ${formatDoubleBn(xVal)})"
        } else {
            formatDoubleBn(xVal)
        }
        val yText = if (yRat != null && !yRat.isInteger) {
            "${yRat.toBnString()} (বা ${formatDoubleBn(yVal)})"
        } else {
            formatDoubleBn(yVal)
        }

        val steps = listOf(
            "ধাপ ১: প্রদত্ত সরল সহসমীকরণ জোট" to
                "(১) $eq1Bn\n(২) $eq2Bn",
            "ধাপ ২: ক্রেমারের নিয়ম (Cramer's Rule / আরগুণন পদ্ধতি) অনুসারে নির্ণায়ক গণনা" to
                "D = a₁b₂ − a₂b₁ = (${formatDoubleBn(a1)} × ${formatDoubleBn(b2)}) − (${formatDoubleBn(a2)} × ${formatDoubleBn(b1)}) = ${formatDoubleBn(det)}\n" +
                "Dx = c₁b₂ − c₂b₁ = (${formatDoubleBn(c1)} × ${formatDoubleBn(b2)}) − (${formatDoubleBn(c2)} × ${formatDoubleBn(b1)}) = ${formatDoubleBn(detX)}\n" +
                "Dy = a₁c₂ − a₂c₁ = (${formatDoubleBn(a1)} × ${formatDoubleBn(c2)}) − (${formatDoubleBn(a2)} × ${formatDoubleBn(c1)}) = ${formatDoubleBn(detY)}",
            "ধাপ ৩: চলকের মান নির্ণয় (x = Dx/D, y = Dy/D)" to
                "x = ${formatDoubleBn(detX)} ÷ ${formatDoubleBn(det)} = $xText\n" +
                "y = ${formatDoubleBn(detY)} ÷ ${formatDoubleBn(det)} = $yText"
        )

        val lhs1 = a1 * xVal + b1 * yVal
        val lhs2 = a2 * xVal + b2 * yVal
        val verify = "শুদ্ধি পরীক্ষা: ১ম সমীকরণের বামপক্ষ = ${formatDoubleBn(lhs1)} = ডানপক্ষ (${formatDoubleBn(c1)}); ২য় সমীকরণের বামপক্ষ = ${formatDoubleBn(lhs2)} = ডানপক্ষ (${formatDoubleBn(c2)})।"

        return SimultaneousLinearResult(
            isUniqueSolution = true,
            isInfiniteSolutions = false,
            isNoSolution = false,
            determinant = det,
            xExactBn = xText,
            yExactBn = yText,
            statusBn = "একক ও অনন্য সমাধান বিদ্যমান (Unique Solution)",
            stepsBn = steps,
            verificationBn = verify
        )
    }
}
