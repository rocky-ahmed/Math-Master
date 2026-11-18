package com.example.math

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Formal BODMAS + Level 2A/2B/2C/2D + 'এর' (Of) Atomic Step-by-Step Expression Engine.
 *
 * Precedence Hierarchy:
 * - Level 1: Grouping delimiters (), {}, [] (innermost first, strict bracket-pair matching)
 * - Level 2A (Tightest in Level 2): Postfix % and contiguous Unicode superscript integer exponents [⁰¹²³⁴⁵⁶⁷⁸⁹]+ (e.g., 2³² = 2^32 = 4294967296)
 * - Level 2B: Right-associative caret exponentiation ^ (e.g., 2^3^2 = 2^(3^2) = 512)
 * - Level 2C: Prefix square root √ (real-domain only; rejects √(-9) and ambiguous √9² / √9^2)
 * - Level 2D: Unary - / + (binds looser than superscript/caret/radical: -3² = -9, -3^2 = -9, -√9 = -3, whereas (-3)² = 9)
 * - Level 3: 'এর' (Of) operator (left-to-right, tighter than ÷ and ×; compact p/q adjacent to 'এর' binds as Rational(p, q))
 * - Level 4: Division (÷) and Multiplication (×) — equal priority, strictly left-to-right
 * - Level 5: Addition (+) and Subtraction (−) — equal priority, strictly left-to-right
 */
object SolverAtomicArithmeticEngine {

    class DomainValidationException(messageBn: String) : IllegalArgumentException(messageBn)

    private val usSymbols = DecimalFormatSymbols(Locale.US)
    private val dfLocal = ThreadLocal.withInitial { DecimalFormat("0.####", usSymbols) }
    private val AMBIGUOUS_RADICAL_POWER_REGEX = Regex("""√\s*[0-9]+(?:\.[0-9]+)?\s*(?:[⁰¹²³⁴⁵⁶⁷⁸⁹]+|\^)""")
    private val HAS_OF_KEYWORD_REGEX = Regex("""(?:এর|\bof\b)""", RegexOption.IGNORE_CASE)
    private val SPACED_SLASH_REGEX = Regex("""(?:\s+/\s*|\s*/\s+)""")
    private val CHAINED_SLASH_REGEX = Regex("""\d+/\d+/\d+""")

    private val superscriptDigitMap = mapOf(
        '⁰' to '0', '¹' to '1', '²' to '2', '³' to '3', '⁴' to '4',
        '⁵' to '5', '⁶' to '6', '⁷' to '7', '⁸' to '8', '⁹' to '9'
    )

    private val bnDigitMap = mapOf(
        '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
        '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9'
    )

    sealed class Token {
        data class Num(
            val rational: Rational?,
            val doubleVal: Double,
            val explicitDisplayBn: String? = null
        ) : Token() {
            val isNegative: Boolean
                get() = if (rational != null) {
                    rational.simplifiedNumerator < BigInteger.ZERO
                } else {
                    doubleVal < 0.0
                }

            fun negate(): Num {
                val negRat = rational?.let { Rational(it.simplifiedNumerator.negate(), it.simplifiedDenominator) }
                return Num(negRat, -doubleVal, null)
            }

            fun formatBn(wrapNegativeForPower: Boolean = false): String {
                val raw = when {
                    explicitDisplayBn != null -> explicitDisplayBn
                    rational != null -> {
                        if (rational.isInteger) {
                            BengaliNumberFormatter.toBn(rational.simplifiedNumerator.toString())
                        } else {
                            val exactDec = rational.toExactDecimalString()
                            if (exactDec != null) {
                                BengaliNumberFormatter.toBn(exactDec)
                            } else {
                                formatDoubleBn(doubleVal)
                            }
                        }
                    }
                    else -> formatDoubleBn(doubleVal)
                }
                return if (wrapNegativeForPower && isNegative && !raw.startsWith("(")) "($raw)" else raw
            }
        }

        data class Op(val kind: String) : Token() // "+", "-", "*", "/", "OF", "^", "SUP_POW", "PCT", "SQRT", "UMINUS", "UPLUS"
        data class OpenBracket(val ch: Char) : Token()
        data class CloseBracket(val ch: Char) : Token()
    }

    data class EvaluationTrace(
        val finalNum: Token.Num,
        val isExact: Boolean,
        val steps: List<SolutionStep>
    )

    fun formatDoubleBn(n: Double): String {
        if (abs(n) < 1e-12) return "০"
        val asLong = n.toLong()
        return if (n == asLong.toDouble() && abs(n) < 9e15) {
            BengaliNumberFormatter.toBn(asLong.toString())
        } else {
            val formatter = dfLocal.get() ?: DecimalFormat("0.####", usSymbols)
            BengaliNumberFormatter.toBn(formatter.format(n))
        }
    }

    fun evaluate(rawExpression: String): EvaluationTrace {
        val trimmed = rawExpression.trim()
        if (trimmed.isEmpty()) {
            throw DomainValidationException("অনুগ্রহ করে একটি বৈধ গাণিতিক রাশি লিখুন। যেমন: (১৫ + ৫) × ৪ ÷ ২ − ৬")
        }

        val asciiDigitsStr = buildString {
            for (ch in trimmed) {
                append(bnDigitMap[ch] ?: ch)
            }
        }

        // 1. Validate bracket matching and non-empty brackets (), {}, []
        validateBrackets(asciiDigitsStr)

        // 2. Check Level 2C radical-exponent ambiguity: unparenthesized √9² or √9^2
        if (AMBIGUOUS_RADICAL_POWER_REGEX.containsMatchIn(asciiDigitsStr)) {
            throw DomainValidationException(
                "রাশিটি অস্পষ্ট: আপনি কি √(৯²) নাকি (√৯)² বোঝাতে চেয়েছেন? অনুগ্রহ করে বন্ধনী ব্যবহার করে স্পষ্ট করুন।"
            )
        }

        // 3. Check Level 3 ('এর' / 'of') with spaced or chained slash '/' ambiguity
        val hasOfKeyword = HAS_OF_KEYWORD_REGEX.containsMatchIn(asciiDigitsStr)
        if (hasOfKeyword) {
            if (SPACED_SLASH_REGEX.containsMatchIn(asciiDigitsStr) || CHAINED_SLASH_REGEX.containsMatchIn(asciiDigitsStr)) {
                throw DomainValidationException(
                    "ভগ্নাংশ ও 'এর' এর ক্রম স্পষ্ট নয়: ভগ্নাংশ হলে '১/২ এর ৮' (ফাঁকা ছাড়া) এবং ভাগ হলে '(১ ÷ ২) এর ৮' বা '১৬ ÷ ২ এর ৪' আকারে বন্ধনীসহ লিখুন।"
                )
            }
        }

        // 4. Tokenize expression
        val tokens = tokenize(asciiDigitsStr, hasOfKeyword).toMutableList()
        if (tokens.isEmpty()) {
            throw DomainValidationException("রাশিটি সঠিকভাবে বিশ্লেষণ করা যায়নি।")
        }

        val steps = mutableListOf<SolutionStep>()
        var exactSoFar = true

        // 5. Collapse trivial signed numbers inside brackets e.g. (-3) -> Num(-3) before main loop
        collapseTrivialBracketedNumbers(tokens)

        // 6. Main atomic reduction loop: innermost brackets first, then top-level
        var safety = 0
        while (safety < 250) {
            if (Thread.currentThread().isInterrupted) {
                throw kotlinx.coroutines.CancellationException("Solver calculation interrupted")
            }
            safety++
            collapseTrivialBracketedNumbers(tokens)

            val bracketSpan = findInnermostBracketSpan(tokens)
            if (bracketSpan != null) {
                val (openIdx, closeIdx) = bracketSpan
                val didReduce = performOneAtomicStepInSlice(
                    allTokens = tokens,
                    sliceStart = openIdx + 1,
                    sliceEndExclusive = closeIdx,
                    insideBracket = true,
                    steps = steps,
                    onApproximate = { exactSoFar = false }
                )
                collapseTrivialBracketedNumbers(tokens)
                if (!didReduce) {
                    // If slice is already a single Num, unwrap brackets
                    if (closeIdx == openIdx + 2 && tokens[openIdx + 1] is Token.Num) {
                        tokens.removeAt(closeIdx)
                        tokens.removeAt(openIdx)
                    } else {
                        throw DomainValidationException("বন্ধনীর ভেতরের রাশিটি বিশ্লেষণ করা যায়নি।")
                    }
                }
            } else {
                if (tokens.size == 1 && tokens[0] is Token.Num) break
                val didReduce = performOneAtomicStepInSlice(
                    allTokens = tokens,
                    sliceStart = 0,
                    sliceEndExclusive = tokens.size,
                    insideBracket = false,
                    steps = steps,
                    onApproximate = { exactSoFar = false }
                )
                if (!didReduce) break
            }
        }

        if (tokens.size != 1 || tokens[0] !is Token.Num) {
            throw DomainValidationException("রাশিটি সঠিকভাবে বিশ্লেষণ করা যায়নি। সঠিক চিহ্ন (+, −, ×, ÷) ব্যবহার করুন।")
        }

        val finalNum = tokens[0] as Token.Num
        if (steps.isEmpty()) {
            val exprBn = renderTokensBn(listOf(finalNum))
            steps.add(
                SolutionStep(
                    stepIndex = 1,
                    titleBn = "মান নির্ণয়",
                    expressionOrMathBn = "$exprBn = ${finalNum.formatBn()}",
                    explanationBn = "প্রদত্ত সংখ্যার মান অপরিবর্তিত রয়েছে।",
                    whyThisStepBn = "এখানে কোনো অতিরিক্ত গাণিতিক অপারেশন নেই।",
                    expressionBeforeBn = exprBn,
                    expressionAfterBn = finalNum.formatBn()
                )
            )
        }

        return EvaluationTrace(
            finalNum = finalNum,
            isExact = exactSoFar && (finalNum.rational != null),
            steps = steps
        )
    }

    private fun validateBrackets(s: String) {
        if (s.contains("()") || s.contains("{}") || s.contains("[]")) {
            throw DomainValidationException("বন্ধনীর ভেতরে কোনো রাশি দেওয়া হয়নি।")
        }
        val stack = ArrayDeque<Char>()
        for (ch in s) {
            when (ch) {
                '(', '{', '[' -> stack.addLast(ch)
                ')', '}', ']' -> {
                    if (stack.isEmpty()) {
                        throw DomainValidationException("বন্ধনীর ক্রম সঠিক নয়। অতিরিক্ত সমাপ্তি বন্ধনী রয়েছে।")
                    }
                    val open = stack.removeLast()
                    val expectedClose = when (open) {
                        '(' -> ')'
                        '{' -> '}'
                        '[' -> ']'
                        else -> ')'
                    }
                    if (ch != expectedClose) {
                        throw DomainValidationException("বন্ধনীর ধরন মেলেনি ('$open' এর সাথে '$ch' ব্যবহার করা হয়েছে)। সঠিক বন্ধনী (), {}, [] ব্যবহার করুন।")
                    }
                }
            }
        }
        if (stack.isNotEmpty()) {
            throw DomainValidationException("বন্ধনীর সংখ্যা অসমান। শুরুর বন্ধনী বন্ধ করা হয়নি।")
        }
    }

    private fun tokenize(input: String, treatCompactSlashAsFraction: Boolean): List<Token> {
        val raw = mutableListOf<Token>()
        var i = 0
        val n = input.length

        while (i < n) {
            val ch = input[i]
            when {
                ch.isWhitespace() -> i++
                ch == '(' || ch == '{' || ch == '[' -> {
                    raw.add(Token.OpenBracket(ch))
                    i++
                }
                ch == ')' || ch == '}' || ch == ']' -> {
                    raw.add(Token.CloseBracket(ch))
                    i++
                }
                ch == '+' -> {
                    raw.add(Token.Op("+"))
                    i++
                }
                ch == '-' || ch == '−' || ch == '–' -> {
                    raw.add(Token.Op("-"))
                    i++
                }
                ch == '*' || ch == '×' -> {
                    raw.add(Token.Op("*"))
                    i++
                }
                ch == '÷' -> {
                    raw.add(Token.Op("/"))
                    i++
                }
                ch == '/' -> {
                    raw.add(Token.Op("/"))
                    i++
                }
                ch == '^' -> {
                    raw.add(Token.Op("^"))
                    i++
                }
                ch == '%' -> {
                    raw.add(Token.Op("PCT"))
                    i++
                }
                ch == '√' -> {
                    raw.add(Token.Op("SQRT"))
                    i++
                }
                superscriptDigitMap.containsKey(ch) -> {
                    val sb = StringBuilder()
                    while (i < n && superscriptDigitMap.containsKey(input[i])) {
                        sb.append(superscriptDigitMap[input[i]])
                        i++
                    }
                    val expBig = sb.toString().toBigIntegerOrNull()
                        ?: throw DomainValidationException("অবৈধ ঘাত (Exponent)।")
                    raw.add(Token.Op("SUP_POW"))
                    raw.add(Token.Num(Rational(expBig, BigInteger.ONE), expBig.toDouble()))
                }
                input.startsWith("এর", i) -> {
                    raw.add(Token.Op("OF"))
                    i += 2
                }
                input.regionMatches(i, "sqrt", 0, 4, ignoreCase = true) -> {
                    raw.add(Token.Op("SQRT"))
                    i += 4
                }
                input.regionMatches(i, "of", 0, 2, ignoreCase = true) &&
                    (i == 0 || !input[i - 1].isLetter()) &&
                    (i + 2 >= n || !input[i + 2].isLetter()) -> {
                    raw.add(Token.Op("OF"))
                    i += 2
                }
                ch.isDigit() || ch == '.' -> {
                    val start = i
                    var hasDot = false
                    while (i < n && (input[i].isDigit() || input[i] == '.')) {
                        if (input[i] == '.') {
                            if (hasDot) throw DomainValidationException("সংখ্যায় একাধিক দশমিক বিন্দু ব্যবহার করা হয়েছে।")
                            hasDot = true
                        }
                        i++
                    }
                    val numStr = input.substring(start, i)
                    // Check if this is a compact fraction p/q (no spaces around /, integer numerator & denominator)
                    if (!hasDot && treatCompactSlashAsFraction && i < n && input[i] == '/') {
                        val slashPos = i
                        val denStart = slashPos + 1
                        var denEnd = denStart
                        while (denEnd < n && input[denEnd].isDigit()) {
                            denEnd++
                        }
                        if (denEnd > denStart && (denEnd >= n || input[denEnd] != '.')) {
                            val numBig = numStr.toBigInteger()
                            val denBig = input.substring(denStart, denEnd).toBigInteger()
                            if (denBig == BigInteger.ZERO) {
                                throw ArithmeticException("ভগ্নাংশের হর শূন্য (০) হতে পারে না।")
                            }
                            val rat = Rational(numBig, denBig)
                            val dispBn = "${BengaliNumberFormatter.toBn(numBig.toString())}/${BengaliNumberFormatter.toBn(denBig.toString())}"
                            raw.add(Token.Num(rat, rat.toDouble(), dispBn))
                            i = denEnd
                            continue
                        }
                    }
                    val bd = numStr.toBigDecimalOrNull()
                        ?: throw DomainValidationException("অবৈধ সংখ্যা: $numStr")
                    val rat = bigDecimalToRational(bd)
                    raw.add(Token.Num(rat, bd.toDouble()))
                }
                else -> {
                    throw DomainValidationException("অসমর্থিত চিহ্ন বা অক্ষর: '$ch'")
                }
            }
        }

        // Insert implicit multiplication '*' where standard notation omits it:
        // e.g. 2(3+4), (2+3)(4+5), (2+3)4, 2√9, 50% (when followed by '(' or Num)
        val withImplicitMult = mutableListOf<Token>()
        for (idx in raw.indices) {
            val cur = raw[idx]
            withImplicitMult.add(cur)
            if (idx + 1 < raw.size) {
                val next = raw[idx + 1]
                val curIsValueEnd = cur is Token.Num || cur is Token.CloseBracket || (cur is Token.Op && cur.kind == "PCT")
                val nextIsValueStart = next is Token.OpenBracket || next is Token.Num || (next is Token.Op && next.kind == "SQRT")
                if (curIsValueEnd && nextIsValueStart) {
                    withImplicitMult.add(Token.Op("*"))
                }
            }
        }

        // Classify unary '+' and '-' into UPLUS / UMINUS
        val classified = mutableListOf<Token>()
        for (idx in withImplicitMult.indices) {
            val tok = withImplicitMult[idx]
            if (tok is Token.Op && (tok.kind == "+" || tok.kind == "-")) {
                val prev = classified.lastOrNull()
                if (prev is Token.Op && (prev.kind == "UPLUS" || prev.kind == "UMINUS")) {
                    throw DomainValidationException("একাধিক ইউনারি চিহ্ন পরপর ব্যবহার করা হয়েছে।")
                }
                val isUnary = prev == null ||
                    prev is Token.OpenBracket ||
                    (prev is Token.Op && prev.kind != "PCT")
                if (isUnary) {
                    classified.add(Token.Op(if (tok.kind == "-") "UMINUS" else "UPLUS"))
                } else {
                    classified.add(tok)
                }
            } else {
                classified.add(tok)
            }
        }

        return classified
    }

    private fun bigDecimalToRational(bd: BigDecimal): Rational {
        val stripped = bd.stripTrailingZeros()
        val scale = stripped.scale()
        return if (scale <= 0) {
            Rational(stripped.toBigIntegerExact(), BigInteger.ONE)
        } else {
            Rational(stripped.unscaledValue(), BigInteger.TEN.pow(scale))
        }
    }

    /**
     * Collapses bracketed signed literals like (-3) or (+5) directly into Token.Num(-3)
     * without emitting a redundant calculation step, so (-3)² or 10 + (-3)^2 works cleanly.
     */
    private fun collapseTrivialBracketedNumbers(tokens: MutableList<Token>) {
        var changed = true
        while (changed) {
            changed = false
            for (i in 0 until tokens.size) {
                if (tokens[i] is Token.OpenBracket) {
                    // Case A: ( Num )
                    if (i + 2 < tokens.size && tokens[i + 1] is Token.Num && tokens[i + 2] is Token.CloseBracket) {
                        val num = tokens[i + 1] as Token.Num
                        tokens.subList(i, i + 3).clear()
                        tokens.add(i, num)
                        changed = true
                        break
                    }
                    // Case B: ( UMINUS/UPLUS Num )
                    if (i + 3 < tokens.size &&
                        tokens[i + 1] is Token.Op &&
                        ((tokens[i + 1] as Token.Op).kind == "UMINUS" || (tokens[i + 1] as Token.Op).kind == "UPLUS") &&
                        tokens[i + 2] is Token.Num &&
                        tokens[i + 3] is Token.CloseBracket
                    ) {
                        val opKind = (tokens[i + 1] as Token.Op).kind
                        val num = tokens[i + 2] as Token.Num
                        val signed = if (opKind == "UMINUS") num.negate() else num
                        tokens.subList(i, i + 4).clear()
                        tokens.add(i, signed)
                        changed = true
                        break
                    }
                }
            }
        }
    }

    private fun findInnermostBracketSpan(tokens: List<Token>): Pair<Int, Int>? {
        var lastOpen = -1
        for (i in tokens.indices) {
            val t = tokens[i]
            if (t is Token.OpenBracket) {
                lastOpen = i
            } else if (t is Token.CloseBracket && lastOpen != -1) {
                return Pair(lastOpen, i)
            }
        }
        return null
    }

    /**
     * Executes exactly ONE atomic operation inside [sliceStart, sliceEndExclusive) according to
     * our strict precedence hierarchy (2A -> 2B -> 2C -> 2D -> 3 -> 4 -> 5), records a SolutionStep,
     * and unwraps the surrounding bracket if the bracketed slice has now simplified to a single Num.
     */
    private fun performOneAtomicStepInSlice(
        allTokens: MutableList<Token>,
        sliceStart: Int,
        sliceEndExclusive: Int,
        insideBracket: Boolean,
        steps: MutableList<SolutionStep>,
        onApproximate: () -> Unit
    ): Boolean {
        val beforeExprBn = renderTokensBn(allTokens)

        // First collapse any UPLUS silently
        for (i in sliceStart until sliceEndExclusive) {
            val t = allTokens[i]
            if (t is Token.Op && t.kind == "UPLUS") {
                allTokens.removeAt(i)
                return true
            }
        }

        // Also if a UMINUS immediately follows a binary operator (*, /, +, -, OF, ^, SUP_POW, SQRT)
        // and precedes a Num that is NOT followed by ^ or SUP_POW, fold UMINUS into that Num silently.
        for (i in sliceStart until sliceEndExclusive - 1) {
            val t = allTokens[i]
            val next = allTokens[i + 1]
            if (t is Token.Op && t.kind == "UMINUS" && next is Token.Num) {
                val afterNext = if (i + 2 < sliceEndExclusive) allTokens[i + 2] else null
                val nextHasTighterPower = afterNext is Token.Op && (afterNext.kind == "SUP_POW" || afterNext.kind == "^")
                if (!nextHasTighterPower) {
                    allTokens[i + 1] = next.negate()
                    allTokens.removeAt(i)
                    collapseTrivialBracketedNumbers(allTokens)
                    return true
                }
            }
        }

        // Level 2A: Postfix '%' and Superscript Power 'SUP_POW' (left-to-right)
        for (i in sliceStart until sliceEndExclusive) {
            val t = allTokens[i]
            if (t is Token.Op && t.kind == "PCT") {
                if (i - 1 < sliceStart || allTokens[i - 1] !is Token.Num) {
                    throw DomainValidationException("শতকরা (%) চিহ্নের পূর্বে সংখ্যা নেই।")
                }
                val base = allTokens[i - 1] as Token.Num
                val res = applyPercent(base)
                val subBefore = "${base.formatBn()}%"
                allTokens.subList(i - 1, i + 1).clear()
                allTokens.add(i - 1, res)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                recordStep(
                    steps = steps,
                    titleBn = "শতকরা (%) কে মানে রূপান্তর",
                    subMathBn = "$subBefore = ${res.formatBn()}",
                    explanationBn = "শতকরা চিহ্নের অর্থ ১০০ দ্বারা ভাগ (${base.formatBn()} ÷ ১০০ = ${res.formatBn()})।",
                    whyBn = "BODMAS নিয়মে সূচক ও শতকরার কাজ গুণ-ভাগের পূর্বে সম্পন্ন করতে হয়।",
                    beforeFullBn = beforeExprBn,
                    afterFullBn = renderTokensBn(allTokens)
                )
                return true
            }
            if (t is Token.Op && t.kind == "SUP_POW") {
                if (i - 1 < sliceStart || i + 1 >= sliceEndExclusive ||
                    allTokens[i - 1] !is Token.Num || allTokens[i + 1] !is Token.Num
                ) {
                    throw DomainValidationException("ঘাতের (Exponent) ভিত্তি বা সূচক সঠিক নয়।")
                }
                val base = allTokens[i - 1] as Token.Num
                val exp = allTokens[i + 1] as Token.Num
                val (res, exact) = applyPower(base, exp)
                if (!exact) onApproximate()
                val subMath = "${base.formatBn(wrapNegativeForPower = true)}^${exp.formatBn()} = ${res.formatBn()}"
                allTokens.subList(i - 1, i + 2).clear()
                allTokens.add(i - 1, res)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                recordStep(
                    steps = steps,
                    titleBn = if (insideBracket) "বন্ধনীর ভেতরের ঘাত (Power) নির্ণয়" else "ঘাত (Power / সূচক) নির্ণয়",
                    subMathBn = subMath,
                    explanationBn = "${base.formatBn(wrapNegativeForPower = true)} কে ${exp.formatBn()} ঘাতে উন্নীত করে ${res.formatBn()} পাওয়া গেল।",
                    whyBn = "সূচক বা ঘাতের (Orders/Powers) অগ্রাধিকার গুণ, ভাগ, যোগ ও বিয়োগের চেয়ে বেশি।",
                    beforeFullBn = beforeExprBn,
                    afterFullBn = renderTokensBn(allTokens)
                )
                return true
            }
        }

        // Level 2B: Right-Associative Caret Exponentiation '^' (scan right-to-left!)
        for (i in sliceEndExclusive - 1 downTo sliceStart) {
            val t = allTokens[i]
            if (t is Token.Op && t.kind == "^") {
                if (i - 1 < sliceStart || i + 1 >= sliceEndExclusive ||
                    allTokens[i - 1] !is Token.Num || allTokens[i + 1] !is Token.Num
                ) {
                    throw DomainValidationException("সূচক (^) চিহ্নের আগে ও পরে বৈধ সংখ্যা থাকতে হবে।")
                }
                val base = allTokens[i - 1] as Token.Num
                val exp = allTokens[i + 1] as Token.Num
                val (res, exact) = applyPower(base, exp)
                if (!exact) onApproximate()
                val subMath = "${base.formatBn(wrapNegativeForPower = true)}^${exp.formatBn()} = ${res.formatBn()}"
                allTokens.subList(i - 1, i + 2).clear()
                allTokens.add(i - 1, res)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                recordStep(
                    steps = steps,
                    titleBn = if (insideBracket) "বন্ধনীর ভেতরের সূচক (Power) নির্ণয়" else "বন্ধনীর ঘাত (Power) নির্ণয়",
                    subMathBn = subMath,
                    explanationBn = "ডান-সংযোজনশীল (Right-associative) সূচকের নিয়ম অনুযায়ী ${base.formatBn(wrapNegativeForPower = true)}^${exp.formatBn()} = ${res.formatBn()} নির্ণয় করা হলো।",
                    whyBn = "একাধিক সূচক থাকলে ডানদিক থেকে বামদিকে ঘাত হিসাব করতে হয় (যেমন 2^3^2 = 2^(3^2))।",
                    beforeFullBn = beforeExprBn,
                    afterFullBn = renderTokensBn(allTokens)
                )
                return true
            }
        }

        // Level 2C: Prefix Square Root 'SQRT' (scan right-to-left)
        for (i in sliceEndExclusive - 1 downTo sliceStart) {
            val t = allTokens[i]
            if (t is Token.Op && t.kind == "SQRT") {
                if (i + 1 >= sliceEndExclusive || allTokens[i + 1] !is Token.Num) {
                    throw DomainValidationException("বর্গমূল (√) চিহ্নের পরে বৈধ সংখ্যা দিন।")
                }
                val operand = allTokens[i + 1] as Token.Num
                val (res, exact) = applySqrt(operand)
                if (!exact) onApproximate()
                val subMath = "√${operand.formatBn(wrapNegativeForPower = true)} = ${res.formatBn()}"
                allTokens.subList(i, i + 2).clear()
                allTokens.add(i, res)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                recordStep(
                    steps = steps,
                    titleBn = "বর্গমূল (Square Root) নির্ণয়",
                    subMathBn = subMath,
                    explanationBn = "${operand.formatBn()} এর ধনাত্মক বর্গমূল নির্ণয় করে ${res.formatBn()} পাওয়া গেল।",
                    whyBn = "বর্গমূল (Orders/Roots) এর কাজ গুণ ও ভাগের পূর্বে সম্পন্ন করতে হয়।",
                    beforeFullBn = beforeExprBn,
                    afterFullBn = renderTokensBn(allTokens)
                )
                return true
            }
        }

        // Level 2D: Remaining Unary Minus 'UMINUS' (after powers/roots have evaluated, e.g. -3² -> -9)
        for (i in sliceEndExclusive - 1 downTo sliceStart) {
            val t = allTokens[i]
            if (t is Token.Op && t.kind == "UMINUS") {
                if (i + 1 >= sliceEndExclusive || allTokens[i + 1] !is Token.Num) {
                    throw DomainValidationException("ঋণাত্মক চিহ্নের পর কোনো সংখ্যা পাওয়া যায়নি।")
                }
                val operand = allTokens[i + 1] as Token.Num
                val neg = operand.negate()
                allTokens.subList(i, i + 2).clear()
                allTokens.add(i, neg)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                return true
            }
        }

        // Level 3: 'এর' ('OF') — strictly left-to-right, tighter than ÷ and ×
        for (i in sliceStart until sliceEndExclusive) {
            val t = allTokens[i]
            if (t is Token.Op && t.kind == "OF") {
                if (i - 1 < sliceStart || i + 1 >= sliceEndExclusive ||
                    allTokens[i - 1] !is Token.Num || allTokens[i + 1] !is Token.Num
                ) {
                    throw DomainValidationException("'এর' (Of) এর আগে ও পরে বৈধ সংখ্যা থাকতে হবে।")
                }
                val left = allTokens[i - 1] as Token.Num
                val right = allTokens[i + 1] as Token.Num
                val res = applyMultiply(left, right)
                val subMath = "${left.formatBn()} এর ${right.formatBn()} = ${res.formatBn()}"
                allTokens.subList(i - 1, i + 2).clear()
                allTokens.add(i - 1, res)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                recordStep(
                    steps = steps,
                    titleBn = "'এর' (Of) এর কাজ সম্পন্ন করণ",
                    subMathBn = subMath,
                    explanationBn = "BODMAS নিয়মে ভাগ (÷) ও গুণ (×) এর পূর্বে 'এর' (Of) এর গুণ সম্পন্ন করতে হয়।",
                    whyBn = "'এর' মানে ভগ্নাংশীয় বা সংযুক্ত গুণ, যার অগ্রাধিকার সাধারণ ভাগ ও গুণের চেয়ে বেশি।",
                    beforeFullBn = beforeExprBn,
                    afterFullBn = renderTokensBn(allTokens)
                )
                return true
            }
        }

        // Level 4: Division (÷) and Multiplication (×) — equal priority, strictly left-to-right
        for (i in sliceStart until sliceEndExclusive) {
            val t = allTokens[i]
            if (t is Token.Op && (t.kind == "*" || t.kind == "/")) {
                if (i - 1 < sliceStart || i + 1 >= sliceEndExclusive ||
                    allTokens[i - 1] !is Token.Num || allTokens[i + 1] !is Token.Num
                ) {
                    throw DomainValidationException("চিহ্নের আগে বা পরে সংখ্যা অনুপস্থিত।")
                }
                val left = allTokens[i - 1] as Token.Num
                val right = allTokens[i + 1] as Token.Num
                val isMult = (t.kind == "*")
                val res = if (isMult) applyMultiply(left, right) else applyDivide(left, right)
                val opDisp = if (isMult) "×" else "÷"
                val subMath = "${left.formatBn()} $opDisp ${right.formatBn()} = ${res.formatBn()}"
                allTokens.subList(i - 1, i + 2).clear()
                allTokens.add(i - 1, res)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                val stepTitle = when {
                    insideBracket -> "বন্ধনীর ভেতরের সরল"
                    isMult -> "গুণ সম্পন্ন করণ"
                    else -> "ভাগ সম্পন্ন করণ"
                }
                val stepExp = when {
                    insideBracket -> "BODMAS নিয়ম অনুযায়ী বন্ধনীর ভেতরের কাজ ($subMath) সম্পন্ন করা হলো।"
                    isMult -> "বাম থেকে ডান ক্রমে গুণ ($subMath) সম্পন্ন করা হলো।"
                    else -> "বাম থেকে ডান ক্রমে ভাগ ($subMath) সম্পন্ন করা হলো।"
                }
                recordStep(
                    steps = steps,
                    titleBn = stepTitle,
                    subMathBn = subMath,
                    explanationBn = stepExp,
                    whyBn = "ভাগ ও গুণের অগ্রাধিকার সমান; তাই বাম থেকে ডানে যেটি আগে আসে সেটি আগে করতে হয়।",
                    beforeFullBn = beforeExprBn,
                    afterFullBn = renderTokensBn(allTokens)
                )
                return true
            }
        }

        // Level 5: Addition (+) and Subtraction (−) — equal priority, strictly left-to-right
        for (i in sliceStart until sliceEndExclusive) {
            val t = allTokens[i]
            if (t is Token.Op && (t.kind == "+" || t.kind == "-")) {
                if (i - 1 < sliceStart || i + 1 >= sliceEndExclusive ||
                    allTokens[i - 1] !is Token.Num || allTokens[i + 1] !is Token.Num
                ) {
                    throw DomainValidationException("যোগ বা বিয়োগ চিহ্নের আগে বা পরে সংখ্যা অনুপস্থিত।")
                }
                val left = allTokens[i - 1] as Token.Num
                val right = allTokens[i + 1] as Token.Num
                val isAdd = (t.kind == "+")
                val res = if (isAdd) applyAdd(left, right) else applySubtract(left, right)
                val opDisp = if (isAdd) "+" else "−"
                val subMath = "${left.formatBn()} $opDisp ${right.formatBn()} = ${res.formatBn()}"
                allTokens.subList(i - 1, i + 2).clear()
                allTokens.add(i - 1, res)
                unwrapBracketIfSingleNum(allTokens, sliceStart, insideBracket)
                val isFinalStep = (allTokens.size == 1 && allTokens[0] is Token.Num)
                val stepTitle = when {
                    insideBracket -> "বন্ধনীর ভেতরের সরল"
                    isFinalStep && isAdd -> "যোগ ও চূড়ান্ত ফলাফল"
                    isFinalStep && !isAdd -> "বিয়োগ ও চূড়ান্ত ফলাফল"
                    isAdd -> "যোগ সম্পন্ন করণ"
                    else -> "বিয়োগ সম্পন্ন করণ"
                }
                val stepExp = when {
                    insideBracket -> "BODMAS নিয়ম অনুযায়ী বন্ধনীর ভেতরের কাজ ($subMath) সর্বাগ্রে সম্পন্ন করা হলো।"
                    isAdd -> "বাম থেকে ডান ক্রমে যোগ ($subMath) সম্পন্ন করা হলো।"
                    else -> "বাম থেকে ডান ক্রমে বিয়োগ ($subMath) সম্পন্ন করা হলো।"
                }
                recordStep(
                    steps = steps,
                    titleBn = stepTitle,
                    subMathBn = subMath,
                    explanationBn = stepExp,
                    whyBn = if (insideBracket) {
                        "বন্ধনী হিসাবের অগ্রাধিকার নির্ধারণ করে, তাই বন্ধনীর ভেতরের সমাধান আগে করতে হয়।"
                    } else {
                        "যোগ ও বিয়োগের অগ্রাধিকার সমান; তাই বাম থেকে ডান ক্রমে হিসাব সম্পন্ন করা হয়।"
                    },
                    beforeFullBn = beforeExprBn,
                    afterFullBn = renderTokensBn(allTokens)
                )
                return true
            }
        }

        return false
    }

    private fun unwrapBracketIfSingleNum(
        allTokens: MutableList<Token>,
        sliceStart: Int,
        insideBracket: Boolean
    ) {
        if (!insideBracket) return
        val openIdx = sliceStart - 1
        val closeIdx = sliceStart + 1
        if (openIdx >= 0 && closeIdx < allTokens.size &&
            allTokens[openIdx] is Token.OpenBracket &&
            allTokens[sliceStart] is Token.Num &&
            allTokens[closeIdx] is Token.CloseBracket
        ) {
            allTokens.removeAt(closeIdx)
            allTokens.removeAt(openIdx)
        }
    }

    private fun recordStep(
        steps: MutableList<SolutionStep>,
        titleBn: String,
        subMathBn: String,
        explanationBn: String,
        whyBn: String,
        beforeFullBn: String,
        afterFullBn: String
    ) {
        val nextIdx = steps.size + 1
        val fullTransitionBn = if (beforeFullBn != afterFullBn) {
            "$subMathBn  [রাশি: $beforeFullBn ➜ $afterFullBn]"
        } else {
            subMathBn
        }
        steps.add(
            SolutionStep(
                stepIndex = nextIdx,
                titleBn = titleBn,
                expressionOrMathBn = fullTransitionBn,
                explanationBn = explanationBn,
                whyThisStepBn = whyBn,
                expressionBeforeBn = beforeFullBn,
                expressionAfterBn = afterFullBn,
                simpleExplanationBn = explanationBn,
                detailedRuleBn = whyBn,
                commonMistakeBn = "সতর্কতা: BODMAS ক্রম না মেনে বাম থেকে ডানে সব অপারেশন একসাথে করলে ভুল উত্তর আসে।",
                hint1Bn = "এই ধাপে কোন প্রক্রিয়াটির অগ্রাধিকার সবচেয়ে বেশি তা চিহ্নিত করুন।",
                hint2Bn = "গণনা করুন: $subMathBn",
                expectedStepValueBn = afterFullBn
            )
        )
    }

    private fun applyPercent(base: Token.Num): Token.Num {
        val rat = base.rational?.let { it / Rational.of(100) }
        return Token.Num(rat, base.doubleVal / 100.0)
    }

    private fun applyPower(base: Token.Num, exp: Token.Num): Pair<Token.Num, Boolean> {
        val expRat = exp.rational
        if (base.rational != null && expRat != null && expRat.isInteger) {
            val expInt = expRat.simplifiedNumerator
            if (expInt.abs() <= BigInteger.valueOf(1000L)) {
                val e = expInt.toInt()
                if (e < 0 && base.rational == Rational.ZERO) {
                    throw ArithmeticException("শূন্যের ঋণাত্মক ঘাত অসংজ্ঞায়িত।")
                }
                val powRat = base.rational.pow(e)
                return Pair(Token.Num(powRat, powRat.toDouble()), true)
            }
        }
        if (base.doubleVal < 0.0 && floor(exp.doubleVal) != exp.doubleVal) {
            throw DomainValidationException("বাস্তব সংখ্যার ডোমেনে ঋণাত্মক সংখ্যার ভগ্নাংশ ঘাত বাস্তব সংখ্যা নয়।")
        }
        val d = base.doubleVal.pow(exp.doubleVal)
        if (d.isNaN() || d.isInfinite()) {
            throw ArithmeticException("ঘাতের মান সমর্থিত সীমার বাইরে।")
        }
        return Pair(Token.Num(null, d), false)
    }

    private fun applySqrt(operand: Token.Num): Pair<Token.Num, Boolean> {
        if (operand.isNegative || operand.doubleVal < 0.0) {
            throw DomainValidationException(
                "বাস্তব সংখ্যার ডোমেনে ঋণাত্মক সংখ্যার বর্গমূল (যেমন √(${operand.formatBn()})) বাস্তব সংখ্যা নয়।"
            )
        }
        val rat = operand.rational
        if (rat != null) {
            val numSqrt = rat.simplifiedNumerator.sqrt()
            val denSqrt = rat.simplifiedDenominator.sqrt()
            if (numSqrt.multiply(numSqrt) == rat.simplifiedNumerator &&
                denSqrt.multiply(denSqrt) == rat.simplifiedDenominator
            ) {
                val exactRat = Rational(numSqrt, denSqrt)
                return Pair(Token.Num(exactRat, exactRat.toDouble()), true)
            }
        }
        val d = sqrt(operand.doubleVal)
        return Pair(Token.Num(null, d), false)
    }

    private fun applyMultiply(left: Token.Num, right: Token.Num): Token.Num {
        val rat = if (left.rational != null && right.rational != null) {
            left.rational * right.rational
        } else null
        return Token.Num(rat, left.doubleVal * right.doubleVal)
    }

    private fun applyDivide(left: Token.Num, right: Token.Num): Token.Num {
        val isZero = (right.rational != null && right.rational == Rational.ZERO) || abs(right.doubleVal) < 1e-15
        if (isZero) {
            throw ArithmeticException("কোনো সংখ্যাকে শূন্য (০) দিয়ে ভাগ করা যায় না।")
        }
        val rat = if (left.rational != null && right.rational != null) {
            left.rational / right.rational
        } else null
        return Token.Num(rat, left.doubleVal / right.doubleVal)
    }

    private fun applyAdd(left: Token.Num, right: Token.Num): Token.Num {
        val rat = if (left.rational != null && right.rational != null) {
            left.rational + right.rational
        } else null
        return Token.Num(rat, left.doubleVal + right.doubleVal)
    }

    private fun applySubtract(left: Token.Num, right: Token.Num): Token.Num {
        val rat = if (left.rational != null && right.rational != null) {
            left.rational - right.rational
        } else null
        return Token.Num(rat, left.doubleVal - right.doubleVal)
    }

    fun renderTokensBn(tokens: List<Token>): String {
        val sb = StringBuilder()
        for (i in tokens.indices) {
            val t = tokens[i]
            val next = tokens.getOrNull(i + 1)
            val nextIsPower = next is Token.Op && (next.kind == "^" || next.kind == "SUP_POW")
            when (t) {
                is Token.Num -> sb.append(t.formatBn(wrapNegativeForPower = nextIsPower))
                is Token.OpenBracket -> sb.append(t.ch)
                is Token.CloseBracket -> sb.append(t.ch)
                is Token.Op -> when (t.kind) {
                    "+" -> sb.append(" + ")
                    "-" -> sb.append(" − ")
                    "*" -> sb.append(" × ")
                    "/" -> sb.append(" ÷ ")
                    "OF" -> sb.append(" এর ")
                    "^", "SUP_POW" -> sb.append("^")
                    "PCT" -> sb.append("%")
                    "SQRT" -> sb.append("√")
                    "UMINUS" -> sb.append("-")
                    "UPLUS" -> sb.append("+")
                }
            }
        }
        return sb.toString().trim()
    }
}
