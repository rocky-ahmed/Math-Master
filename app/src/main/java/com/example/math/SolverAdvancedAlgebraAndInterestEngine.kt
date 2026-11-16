package com.example.math

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Advanced deterministic solvers for:
 * 1. Exact Integer-Period Compound Interest (A = P(1 + r/(100m))^(mt), CI = A - P)
 * 2. Linear Inequalities (ax + b ⋈ cx + d) & Complete 24+8 Case Absolute-Value Linear Inequalities (|ax + b| ⋈ c)
 * 3. Complete 24-Case Quadratic Inequalities (ax² + bx + c ⋈ 0 across a > 0 / a < 0 and Δ > 0 / Δ = 0 / Δ < 0)
 */
object SolverAdvancedAlgebraAndInterestEngine {

    private val usSymbols = DecimalFormatSymbols(Locale.US)
    private val df = DecimalFormat("0.####", usSymbols)

    private fun formatNum(n: Double): String {
        if (abs(n) < 1e-12) return "০"
        val asLong = n.toLong()
        return if (n == asLong.toDouble() && abs(n) < 9e15) {
            BengaliNumberFormatter.toBn(asLong.toString())
        } else {
            BengaliNumberFormatter.toBn(df.format(n))
        }
    }

    private fun formatRationalOrDoubleBn(rat: Rational?, d: Double): String {
        if (rat != null) {
            val exactDec = rat.toExactDecimalString()
            if (exactDec != null) {
                return BengaliNumberFormatter.toBn(exactDec)
            }
            return "${rat.toBnString()} (≈ ${formatNum(d)})"
        }
        return formatNum(d)
    }

    // -------------------------------------------------------------
    // 1. COMPOUND INTEREST SOLVER (Exact Rational/BigDecimal when N = m*t is integer)
    // -------------------------------------------------------------
    fun solveCompoundInterest(
        p: Double,
        r: Double,
        t: Double,
        m: Int = 1,
        mode: SolverMode
    ): SolverResult {
        if (p <= 0.0 || r < 0.0 || t < 0.0 || m <= 0) {
            return SolverResult(
                topic = SolverTopic.SIMPLE_INTEREST,
                mode = mode,
                originalProblemBn = "P=$p, r=$r%, t=$t, m=$m",
                finalAnswerBn = "সমাধান সম্ভব নয়",
                isSuccess = false,
                errorMessageBn = "চক্রবৃদ্ধি মুনাফার জন্য আসল (P > ০), হার (r ≥ ০), সময় (t ≥ ০) এবং পর্ব সংখ্যা (m ≥ ১) বৈধ হতে হবে।"
            )
        }

        val totalPeriodsDouble = m * t
        val isIntegerPeriods = abs(totalPeriodsDouble - totalPeriodsDouble.roundToInt()) < 1e-9 &&
            totalPeriodsDouble.roundToInt() in 0..240
        val nPeriods = totalPeriodsDouble.roundToInt()

        val steps = mutableListOf<SolutionStep>()
        var stepIdx = 1

        val freqLabelBn = when (m) {
            1 -> "বার্ষিক (m = ১)"
            2 -> "অর্ধবার্ষিক (m = ২)"
            4 -> "ত্রৈমাসিক (m = ৪)"
            12 -> "মাসিক (m = ১২)"
            else -> "বছরে ${BengaliNumberFormatter.toBn(m)} বার (m = ${BengaliNumberFormatter.toBn(m)})"
        }

        var exactA: Rational? = null
        var exactCI: Rational? = null
        var isExactDecimal = false

        if (isIntegerPeriods) {
            runCatching {
                val rP = Rational.fromDouble(p)
                val rR = Rational.fromDouble(r)
                val rM = Rational.of(m.toLong())
                val factorPerPeriod = Rational.ONE + (rR / (Rational.of(100L) * rM))
                val growthFactor = factorPerPeriod.pow(nPeriods)
                val amountRat = rP * growthFactor
                val ciRat = amountRat - rP
                exactA = amountRat
                exactCI = ciRat
                isExactDecimal = amountRat.isTerminatingDecimal && ciRat.isTerminatingDecimal
            }
        }

        val amountDouble = exactA?.toDouble() ?: (p * (1.0 + (r / (100.0 * m))).pow(totalPeriodsDouble))
        val ciDouble = exactCI?.toDouble() ?: (amountDouble - p)
        val siDouble = (p * r * t) / 100.0
        val diffCiSi = ciDouble - siDouble

        val amountDispBn = if (exactA != null && exactA!!.isTerminatingDecimal) {
            BengaliNumberFormatter.toBn(exactA!!.toExactDecimalString()!!)
        } else if (exactA != null) {
            "${formatNum(amountDouble)} [প্রকৃত ভগ্নাংশ: ${exactA!!.toBnString()}]"
        } else {
            formatNum(amountDouble)
        }

        val ciDispBn = if (exactCI != null && exactCI!!.isTerminatingDecimal) {
            BengaliNumberFormatter.toBn(exactCI!!.toExactDecimalString()!!)
        } else if (exactCI != null) {
            "${formatNum(ciDouble)} [প্রকৃত ভগ্নাংশ: ${exactCI!!.toBnString()}]"
        } else {
            formatNum(ciDouble)
        }

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "চক্রবৃদ্ধি পর্ব ও প্রতি পর্বের হার নির্ধারণ",
                expressionOrMathBn = "পর্ব সংখ্যা N = m × t = ${BengaliNumberFormatter.toBn(m)} × ${formatNum(t)} = ${formatNum(totalPeriodsDouble)}টি পর্ব ($freqLabelBn)",
                explanationBn = "বার্ষিক হার ${formatNum(r)}% এবং বছরে ${BengaliNumberFormatter.toBn(m)} বার পর্ব হিসাব করলে মোট পর্ব সংখ্যা N = ${formatNum(totalPeriodsDouble)}।",
                whyThisStepBn = "চক্রবৃদ্ধি মুনাফায় প্রতি পর্ব শেষে মুনাফা আসলের সাথে যুক্ত হয়, তাই মোট পর্ব সংখ্যা জানা আবশ্যক।"
            )
        )

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "সবৃদ্ধিমূল বা চক্রবৃদ্ধি আসল (Compound Amount, A) গণনা",
                expressionOrMathBn = "A = P × (১ + r / (১০০ × m))^(m×t) = ${formatNum(p)} × (১ + ${formatNum(r)} / ${BengaliNumberFormatter.toBn(100 * m)})^${formatNum(totalPeriodsDouble)} = $amountDispBn টাকা",
                explanationBn = "চক্রবৃদ্ধি সবৃদ্ধিমূলের সূত্রে আসল, হার ও পর্ব বসিয়ে মোট সবৃদ্ধিমূল (A) নির্ণয় করা হলো।",
                whyThisStepBn = "চক্রবৃদ্ধির সূত্র সরাসরি মোট সবৃদ্ধিমূল (আসল + মুনাফা) প্রদান করে।"
            )
        )

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "চক্রবৃদ্ধি মুনাফা (Compound Interest, CI = A − P) পৃথকীকরণ",
                expressionOrMathBn = "CI = A − P = ${formatNum(amountDouble)} − ${formatNum(p)} = $ciDispBn টাকা",
                explanationBn = "সবৃদ্ধিমূল (A) থেকে প্রারম্ভিক আসল (P) বিয়োগ করে শুধুমাত্র অর্জিত চক্রবৃদ্ধি মুনাফা (CI) বের করা হলো।",
                whyThisStepBn = "পরীক্ষায় প্রায়ই সবৃদ্ধিমূল (A) এবং চক্রবৃদ্ধি মুনাফা (CI) গুলিয়ে ফেলা হয়; তাই A থেকে P বিয়োগ করা অপরিহার্য।"
            )
        )

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "সরল মুনাফা ও চক্রবৃদ্ধি মুনাফার পার্থক্য (CI − SI)",
                expressionOrMathBn = "সরল মুনাফা (SI) = ${formatNum(siDouble)} টাকা;  পার্থক্য (CI − SI) = ${formatNum(ciDouble)} − ${formatNum(siDouble)} = ${formatNum(diffCiSi)} টাকা",
                explanationBn = "একই আসল, হার ও সময়ে সরল মুনাফার চেয়ে চক্রবৃদ্ধি মুনাফা ${formatNum(diffCiSi)} টাকা বেশি।"
            )
        )

        val isExactOutput = isIntegerPeriods && isExactDecimal
        val exactnessMode = if (isExactOutput) CalculationExactness.EXACT else CalculationExactness.APPROXIMATE

        val bcsShortcut = if (m == 1 && abs(t - 2.0) < 1e-9) {
            "বিসিএস শর্টকাট (২ বছরের পার্থক্য): ২ বছরে চক্রবৃদ্ধি ও সরল মুনাফার পার্থক্য (CI − SI) = P × (r/১০০)² = ${formatNum(p)} × (${formatNum(r)}/১০০)² = ${formatNum(diffCiSi)} টাকা।"
        } else {
            "বিসিএস শর্টকাট: ১০% হারে ২ বছরের কার্যকর চক্রবৃদ্ধি হার = ১০ + ১০ + (১০×১০)/১০০ = ২১%; ৩ বছরের কার্যকর হার = ৩৩.১%।"
        }

        val res = SolverResult(
            topic = SolverTopic.SIMPLE_INTEREST,
            mode = mode,
            originalProblemBn = "আসল ${formatNum(p)} টাকা, বার্ষিক ${formatNum(r)}% হারে ${formatNum(t)} বছরের ($freqLabelBn) চক্রবৃদ্ধি মুনাফা ও সবৃদ্ধিমূল কত?",
            formulaUsedBn = "সবৃদ্ধিমূল A = P(১ + r/(১০০m))^(mt) এবং চক্রবৃদ্ধি মুনাফা CI = A − P",
            steps = steps,
            finalAnswerBn = "চক্রবৃদ্ধি মুনাফা (CI): $ciDispBn টাকা, সবৃদ্ধিমূল (A): $amountDispBn টাকা",
            verificationBn = "শুদ্ধি পরীক্ষা: আসল P (${formatNum(p)}) + চক্রবৃদ্ধি মুনাফা CI (${formatNum(ciDouble)}) = সবৃদ্ধিমূল A (${formatNum(amountDouble)}) টাকা। সরল মুনাফার (${formatNum(siDouble)} টাকা) সাথে পার্থক্য = ${formatNum(diffCiSi)} টাকা।",
            bcsShortcutBn = bcsShortcut,
            bcsExamTipBn = "প্রশ্নে 'সবৃদ্ধিমূল (A)' নাকি 'চক্রবৃদ্ধি মুনাফা (CI)' চাওয়া হয়েছে তা ভালোভাবে লক্ষ্য করুন—CI চাইলে অবশ্যই শেষে আসল (P) বিয়োগ করবেন।",
            similarProblemPromptBn = "১০০০ টাকা ১০% হারে ২ বছরের চক্রবৃদ্ধি মুনাফা কত?",
            isExactResult = isExactOutput,
            approximateResultBn = if (!isExactOutput) "আসন্ন দশমিক মান: CI ≈ ${formatNum(ciDouble)} টাকা, A ≈ ${formatNum(amountDouble)} টাকা" else null,
            exactness = exactnessMode,
            visualModelSpec = VisualModelSpec(
                diagramType = VisualDiagramType.INTEREST_GROWTH_BAR,
                titleBn = "আসল, সরল মুনাফা ও চক্রবৃদ্ধি সবৃদ্ধিমূল তুলনা",
                subtitleBn = "আসল P = ${formatNum(p)} | SI = ${formatNum(siDouble)} | CI = ${formatNum(ciDouble)}",
                primaryValues = listOf(p, p + siDouble, amountDouble),
                labelsBn = listOf("আসল (P)", "সরল সবৃদ্ধিমূল", "চক্রবৃদ্ধি সবৃদ্ধিমূল (A)"),
                accessibilitySummaryBn = "চক্রবৃদ্ধি বৃদ্ধির চিত্র: আসল ${formatNum(p)} টাকা, সরল মুনাফাসহ ${formatNum(p + siDouble)} টাকা এবং চক্রবৃদ্ধি সবৃদ্ধিমূল ${formatNum(amountDouble)} টাকা।"
            )
        )
        return SolverPedagogyEnricher.enrich(res)
    }

    // -------------------------------------------------------------
    // 2. LINEAR & ABSOLUTE-VALUE INEQUALITY SOLVER
    // -------------------------------------------------------------
    fun containsInequalityOrAbsValue(input: String): Boolean {
        val s = input.trim()
        return s.contains("<=") || s.contains(">=") || s.contains("≤") || s.contains("≥") ||
            s.contains("<") || s.contains(">") || s.contains("|")
    }

    private fun normalizeRelationSymbol(rawRel: String): String = when (rawRel.trim()) {
        "<=", "≤" -> "≤"
        ">=", "≥" -> "≥"
        "<" -> "<"
        ">" -> ">"
        else -> "="
    }

    private fun reverseRelationSymbol(rel: String): String = when (rel) {
        "<" -> ">"
        "≤" -> "≥"
        ">" -> "<"
        "≥" -> "≤"
        else -> rel
    }

    fun solveLinearOrAbsInequality(
        rawInput: String,
        mode: SolverMode,
        parseLinearSideFn: (String) -> Pair<Double, Double>
    ): SolverResult {
        val trimmed = rawInput.trim()
        val ascii = SharedMathEngine.normalizeInputAscii(trimmed)
            .replace("≤", "<=")
            .replace("≥", ">=")

        // Find relation operator (<=, >=, <, >, =)
        val relMatch = Regex("""(<=|>=|<|>|=)""").find(ascii)
            ?: return SolverResult(
                topic = SolverTopic.LINEAR_EQUATIONS,
                mode = mode,
                originalProblemBn = BengaliNumberFormatter.toBn(trimmed),
                finalAnswerBn = "সমাধান সম্ভব নয়",
                isSuccess = false,
                errorMessageBn = "সমীকরণ বা অসমতায় একটি বৈধ সম্পর্ক চিহ্ন (=, <, ≤, >, ≥) থাকতে হবে।"
            )

        val rawOp = relMatch.value
        val op = normalizeRelationSymbol(rawOp)
        val lhsRaw = ascii.substring(0, relMatch.range.first).trim()
        val rhsRaw = ascii.substring(relMatch.range.last + 1).trim()

        if (lhsRaw.isEmpty() || rhsRaw.isEmpty()) {
            return SolverResult(
                topic = SolverTopic.LINEAR_EQUATIONS,
                mode = mode,
                originalProblemBn = BengaliNumberFormatter.toBn(trimmed),
                finalAnswerBn = "সমাধান সম্ভব নয়",
                isSuccess = false,
                errorMessageBn = "অসমতার বামপক্ষ এবং ডানপক্ষ উভয়ই থাকতে হবে।"
            )
        }

        // Case A: Absolute-Value Equation or Inequality |ax + b| ⋈ c
        if (lhsRaw.startsWith("|") && lhsRaw.endsWith("|") && lhsRaw.length >= 3) {
            val insideStr = lhsRaw.substring(1, lhsRaw.length - 1).trim()
            if (insideStr.contains("|") || rhsRaw.contains("|") || rhsRaw.contains("x", ignoreCase = true)) {
                return SolverResult(
                    topic = SolverTopic.LINEAR_EQUATIONS,
                    mode = mode,
                    originalProblemBn = BengaliNumberFormatter.toBn(trimmed),
                    finalAnswerBn = "সমাধান সম্ভব নয়",
                    isSuccess = false,
                    errorMessageBn = "পরমমান অসমতা |ax + b| ⋈ c আকারে লিখুন (যেমন: |2x - 3| ≤ 5)।"
                )
            }
            return try {
                val (a, b) = parseLinearSideFn(insideStr.replace(" ", ""))
                val c = rhsRaw.replace(" ", "").toDoubleOrNull()
                    ?: throw IllegalArgumentException("ডানপক্ষে একটি বৈধ বাস্তব ধ্রুবক সংখ্যা (c) দিন।")
                solveAbsoluteValueRelation(trimmed, a, b, op, c, mode)
            } catch (e: Exception) {
                SolverResult(
                    topic = SolverTopic.LINEAR_EQUATIONS,
                    mode = mode,
                    originalProblemBn = BengaliNumberFormatter.toBn(trimmed),
                    finalAnswerBn = "সমাধান সম্ভব নয়",
                    isSuccess = false,
                    errorMessageBn = e.message ?: "পরমমান রাশিটি বিশ্লেষণ করা যায়নি। যেমন: |2x - 3| ≤ 5 আকারে লিখুন।"
                )
            }
        }

        // Case B: Standard Linear Inequality ax + b ⋈ cx + d
        return try {
            val (a, b) = parseLinearSideFn(lhsRaw.replace(" ", ""))
            val (c, d) = parseLinearSideFn(rhsRaw.replace(" ", ""))
            solveStandardLinearInequality(trimmed, a, b, op, c, d, mode)
        } catch (e: Exception) {
            SolverResult(
                topic = SolverTopic.LINEAR_EQUATIONS,
                mode = mode,
                originalProblemBn = BengaliNumberFormatter.toBn(trimmed),
                finalAnswerBn = "সমাধান সম্ভব নয়",
                isSuccess = false,
                errorMessageBn = e.message ?: "অসমতাটি বিশ্লেষণে ত্রুটি। যেমন: 3x - 5 < 10 আকারে লিখুন।"
            )
        }
    }

    /**
     * Solves |ax + b| ⋈ c across all 4 inequality operators (<, ≤, >, ≥) + '=',
     * all 3 signs of c (c > 0, c = 0, c < 0), and all signs of a (a > 0, a < 0, a = 0).
     */
    fun solveAbsoluteValueRelation(
        originalText: String,
        a: Double,
        b: Double,
        op: String,
        c: Double,
        mode: SolverMode
    ): SolverResult {
        val steps = mutableListOf<SolutionStep>()
        var stepIdx = 1
        val insideBn = formatLinearExprBn(a, b)
        val probBn = "|$insideBn| $op ${formatNum(c)}"

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "পরমমান রাশি ও ডানপক্ষের ধ্রুবক c এর চিহ্ন পরীক্ষা",
                expressionOrMathBn = probBn,
                explanationBn = "পরমমানের সংজ্ঞা অনুযায়ী যেকোনো বাস্তব সংখ্যার পরমমান সর্বদা অঋণাত্মক (|$insideBn| ≥ ০)।"
            )
        )

        // Degenerate constant case: a == 0 -> |b| ⋈ c
        if (abs(a) < 1e-12) {
            val absB = abs(b)
            val holds = when (op) {
                "<" -> absB < c - 1e-12
                "≤" -> absB <= c + 1e-12
                ">" -> absB > c + 1e-12
                "≥" -> absB >= c - 1e-12
                "=" -> abs(absB - c) <= 1e-12
                else -> false
            }
            val ansBn = if (holds) {
                "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
            } else {
                "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
            }
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "ধ্রুবক পরমমান সত্যতা যাচাই (a = ০)",
                    expressionOrMathBn = "|${formatNum(b)}| = ${formatNum(absB)} $op ${formatNum(c)} ⟹ ${if (holds) "সর্বদা সত্য" else "অসম্ভব (মিথ্যা)"}",
                    explanationBn = if (holds) {
                        "চলক x না থাকায় এবং শর্তটি সর্বদা সত্য হওয়ায় x এর যেকোনো বাস্তব মানের জন্য এটি সিদ্ধ।"
                    } else {
                        "চলক x না থাকায় এবং শর্তটি মিথ্যা হওয়ায় কোনো বাস্তব সমাধান নেই।"
                    }
                )
            )
            return SolverPedagogyEnricher.enrich(
                SolverResult(
                    topic = SolverTopic.LINEAR_EQUATIONS,
                    mode = mode,
                    originalProblemBn = "সমাধান করুন: ${BengaliNumberFormatter.toBn(originalText)}",
                    formulaUsedBn = "|b| $op c ধ্রুবক অসমতা যাচাই",
                    steps = steps,
                    finalAnswerBn = ansBn,
                    verificationBn = "ধ্রুবক যাচাই: |${formatNum(b)}| = ${formatNum(absB)}",
                    bcsShortcutBn = "বিসিএস টিপস: পরমমান সর্বদা ≥ ০ হয়; চলকহীন অসমতায় সত্য হলে সমাধান ℝ এবং মিথ্যা হলে ∅।"
                )
            )
        }

        // Case 1: c < 0
        if (c < -1e-12) {
            val isAllReals = (op == ">" || op == "≥")
            val ansBn = if (isAllReals) {
                "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
            } else {
                "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
            }
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "ঋণাত্মক ধ্রুবক (c < ০) এর সাথে পরমমানের তুলনা",
                    expressionOrMathBn = "|$insideBn| ≥ ০ এবং c = ${formatNum(c)} < ০ ⟹ $ansBn",
                    explanationBn = if (isAllReals) {
                        "যেকোনো বাস্তব x এর জন্য পরমমান সর্বদা ০ বা ধনাত্মক, যা ঋণাত্মক সংখ্যা ${formatNum(c)} অপেক্ষা সর্বদা বড়। তাই সকল বাস্তব সংখ্যাই সমাধান।"
                    } else {
                        "পরমমান কখনো ঋণাত্মক হতে পারে না, তাই |$insideBn| এর মান ${formatNum(c)} এর সমান বা ছোট হওয়া অসম্ভব।"
                    },
                    whyThisStepBn = "c < ০ হলে কোনো দীর্ঘ হিসাব ছাড়াই পরমমানের মৌলিক ধর্ম (|u| ≥ ০) থেকে সরাসরি সমাধান নির্ণয় করা যায়।"
                )
            )
            return SolverPedagogyEnricher.enrich(
                SolverResult(
                    topic = SolverTopic.LINEAR_EQUATIONS,
                    mode = mode,
                    originalProblemBn = "সমাধান করুন: ${BengaliNumberFormatter.toBn(originalText)}",
                    formulaUsedBn = "যেকোনো বাস্তব রাশির জন্য |u| ≥ ০",
                    steps = steps,
                    finalAnswerBn = ansBn,
                    verificationBn = "যেহেতু c = ${formatNum(c)} < ০ এবং পরমমান সর্বদা অঋণাত্মক, তাই সিদ্ধান্তটি শতভাগ নিশ্চিত।",
                    bcsShortcutBn = "বিসিএস ১-সেকেন্ড শর্টকাট: ডানপাশে ঋণাত্মক সংখ্যা থাকলে |u| < ঋণাত্মক বা |u| ≤ ঋণাত্মক এর উত্তর সর্বদা 'ফাঁকা সেট (∅)', এবং |u| > ঋণাত্মক বা |u| ≥ ঋণাত্মক এর উত্তর 'সকল বাস্তব সংখ্যা (ℝ)'!"
                )
            )
        }

        // Case 2: c == 0
        if (abs(c) <= 1e-12) {
            val x0 = -b / a
            val x0Bn = formatNum(x0)
            val ansBn = when (op) {
                "<" -> "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
                "≤", "=" -> "একক বিন্দু সমাধান: x = $x0Bn"
                ">" -> "x ≠ $x0Bn  [ব্যবধি: (-∞, $x0Bn) ∪ ($x0Bn, ∞)]"
                "≥" -> "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
                else -> "x = $x0Bn"
            }
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "শূন্য সীমানা (c = ০) বিশ্লেষণ",
                    expressionOrMathBn = "$insideBn = ০ ⟹ x = $x0Bn  ⇒  $ansBn",
                    explanationBn = when (op) {
                        "<" -> "পরমমান কখনো ০ অপেক্ষা ছোট হতে পারে না, তাই কোনো সমাধান নেই (x ∈ ∅)।"
                        "≤", "=" -> "পরমমান কেবল তখনই ≤ ০ হয় যখন ভেতরের রাশিটি ঠিক ০ এর সমান হয় ($insideBn = ০ ⟹ x = $x0Bn)।"
                        ">" -> "পরমমান ০ অপেক্ষা বড় হওয়ার অর্থ ভেতরের রাশিটি ০ হতে পারবে না ($insideBn ≠ ০ ⟹ x ≠ $x0Bn)।"
                        else -> "যেকোনো বাস্তব সংখ্যার পরমমান সর্বদা ≥ ০, তাই সকল বাস্তব সংখ্যা (x ∈ ℝ) এর জন্য এটি সত্য।"
                    }
                )
            )
            return SolverPedagogyEnricher.enrich(
                SolverResult(
                    topic = SolverTopic.LINEAR_EQUATIONS,
                    mode = mode,
                    originalProblemBn = "সমাধান করুন: ${BengaliNumberFormatter.toBn(originalText)}",
                    formulaUsedBn = "|u| ⋈ ০ এর সীমানা বিধি (u = ০ বিন্দু: x = $x0Bn)",
                    steps = steps,
                    finalAnswerBn = ansBn,
                    verificationBn = "x = $x0Bn বসালে |$insideBn| = ০ পাওয়া যায়।"
                )
            )
        }

        // Case 3: c > 0 and a != 0
        val e1 = (-c - b) / a
        val e2 = (c - b) / a
        val low = min(e1, e2)
        val high = max(e1, e2)
        val lowBn = formatNum(low)
        val highBn = formatNum(high)

        if (op == "=") {
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "পরমমান সমীকরণকে দুটি শাখায় বিভক্তকরণ",
                    expressionOrMathBn = "$insideBn = -${formatNum(c)}  অথবা  $insideBn = ${formatNum(c)}",
                    explanationBn = "|u| = c (যেখানে c > ০) হলে u = -c অথবা u = +c।"
                )
            )
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "উভয় শাখা থেকে x এর মান নির্ণয়",
                    expressionOrMathBn = "x = $lowBn  অথবা  x = $highBn",
                    explanationBn = "পক্ষান্তর ও সহগ দ্বারা ভাগ করে দুটি সমাধান পাওয়া গেল।"
                )
            )
            return SolverPedagogyEnricher.enrich(
                SolverResult(
                    topic = SolverTopic.LINEAR_EQUATIONS,
                    mode = mode,
                    originalProblemBn = "সমাধান করুন: ${BengaliNumberFormatter.toBn(originalText)}",
                    formulaUsedBn = "|ax + b| = c ⟹ ax + b = ±c",
                    steps = steps,
                    finalAnswerBn = "x = $lowBn, $highBn",
                    verificationBn = "উভয় মান বসালে পরমমান ${formatNum(c)} পাওয়া যায়।"
                )
            )
        }

        val isConjunctive = (op == "<" || op == "≤")
        val splitRuleBn = when (op) {
            "<" -> "-${formatNum(c)} < $insideBn < ${formatNum(c)}"
            "≤" -> "-${formatNum(c)} ≤ $insideBn ≤ ${formatNum(c)}"
            ">" -> "$insideBn < -${formatNum(c)}  অথবা  $insideBn > ${formatNum(c)}"
            else -> "$insideBn ≤ -${formatNum(c)}  অথবা  $insideBn ≥ ${formatNum(c)}"
        }

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "পরমমান চিহ্ন অপসারণ ও যৌগিক অসমতায় রূপান্তর",
                expressionOrMathBn = splitRuleBn,
                explanationBn = if (isConjunctive) {
                    "|u| $op c হলে রাশিটি -c এবং +c এর মধ্যবর্তী ব্যবধিতে থাকে।"
                } else {
                    "|u| $op c হলে রাশিটি -c এর বামে অথবা +c এর ডানে দুইটি পৃথক শাখায় অবস্থান করে।"
                }
            )
        )

        val subConstLeft = -c - b
        val subConstRight = c - b
        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "ধ্রুবক পদ (${formatNum(b)}) বিয়োগ/পক্ষান্তর করণ",
                expressionOrMathBn = if (isConjunctive) {
                    "${formatNum(subConstLeft)} $op ${formatNum(a)}x $op ${formatNum(subConstRight)}"
                } else {
                    "${formatNum(a)}x ${if (op == ">") "<" else "≤"} ${formatNum(subConstLeft)}  অথবা  ${formatNum(a)}x $op ${formatNum(subConstRight)}"
                },
                explanationBn = "চলক পদ ${formatNum(a)}x কে মাঝখানে বা এক পাশে রেখে ধ্রুবক পদ সরলীকরণ করা হলো।"
            )
        )

        if (a < 0.0) {
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "ঋণাত্মক সহগ (${formatNum(a)}) দ্বারা ভাগ ও অসমতার চিহ্ন পরিবর্তন",
                    expressionOrMathBn = "যেহেতু a = ${formatNum(a)} < ০, তাই ভাগ করার পর অসমতার দিক উল্টে গিয়ে সীমানা ক্রম হলো: $lowBn এবং $highBn",
                    explanationBn = "অসমতার উভয় পক্ষকে ঋণাত্মক সংখ্যা দ্বারা ভাগ করলে অসমতার চিহ্ন বিপরীত হয়ে যায় (< থেকে > এবং ≤ থেকে ≥)।",
                    whyThisStepBn = "সংখ্যারেখায় ঋণাত্মক সংখ্যা দিয়ে ভাগ করলে ক্রম উল্টে যায়—এটিই অসমতার সবচেয়ে গুরুত্বপূর্ণ নিয়ম।"
                )
            )
        }

        val finalAnsBn = when (op) {
            "<" -> "$lowBn < x < $highBn  [ব্যবধি: ($lowBn, $highBn)]"
            "≤" -> "$lowBn ≤ x ≤ $highBn  [ব্যবধি: [$lowBn, $highBn]]"
            ">" -> "x < $lowBn অথবা x > $highBn  [ব্যবধি: (-∞, $lowBn) ∪ ($highBn, ∞)]"
            else -> "x ≤ $lowBn অথবা x ≥ $highBn  [ব্যবধি: (-∞, $lowBn] ∪ [$highBn, ∞)]"
        }

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "চূড়ান্ত সমাধান ব্যবধি (Interval Notation)",
                expressionOrMathBn = finalAnsBn,
                explanationBn = "চলক x এর সহগ দ্বারা ভাগ করে প্রমিত ছোট-থেকে-বড় ক্রমে সমাধান ব্যবধি সাজানো হলো।"
            )
        )

        return SolverPedagogyEnricher.enrich(
            SolverResult(
                topic = SolverTopic.LINEAR_EQUATIONS,
                mode = mode,
                originalProblemBn = "সমাধান করুন: ${BengaliNumberFormatter.toBn(originalText)}",
                formulaUsedBn = if (isConjunctive) "|u| $op c ⟹ -c $op u $op c" else "|u| $op c ⟹ u < -c অথবা u > c",
                steps = steps,
                finalAnswerBn = finalAnsBn,
                verificationBn = "সীমানা বিন্দু x = $lowBn এবং x = $highBn বসালে |$insideBn| = ${formatNum(c)} পাওয়া যায়।",
                bcsShortcutBn = "বিসিএস টিপস: |ax + b| ≤ c হলে উত্তর সর্বদা একটি আবদ্ধ ব্যবধি [low, high], আর |ax + b| ≥ c হলে দুই প্রান্তের সংযোগ (-∞, low] ∪ [high, ∞)।",
                visualModelSpec = VisualModelSpec(
                    diagramType = VisualDiagramType.INEQUALITY_NUMBER_LINE,
                    titleBn = "সংখ্যারেখায় পরমমান অসমতার সমাধান ব্যবধি",
                    subtitleBn = finalAnsBn,
                    primaryValues = listOf(low, high),
                    labelsBn = listOf(lowBn, highBn, op),
                    accessibilitySummaryBn = "সংখ্যারেখায় সমাধান ব্যবধি: $finalAnsBn"
                )
            )
        )
    }

    private fun solveStandardLinearInequality(
        originalText: String,
        a: Double,
        b: Double,
        op: String,
        c: Double,
        d: Double,
        mode: SolverMode
    ): SolverResult {
        val steps = mutableListOf<SolutionStep>()
        var stepIdx = 1

        val netA = a - c
        val netConst = d - b

        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "চলক পদ বামপক্ষে ও ধ্রুবক পদ ডানপক্ষে পক্ষান্তর",
                expressionOrMathBn = "${formatNum(netA)}x $op ${formatNum(netConst)}",
                explanationBn = "পক্ষান্তর বিধি অনুযায়ী চলকগুলো বামে (${formatNum(a)}x − ${formatNum(c)}x) এবং ধ্রুবকগুলো ডানে (${formatNum(d)} − ${formatNum(b)}) নেওয়া হলো।"
            )
        )

        if (abs(netA) < 1e-12) {
            val holds = when (op) {
                "<" -> 0.0 < netConst - 1e-12
                "≤" -> 0.0 <= netConst + 1e-12
                ">" -> 0.0 > netConst + 1e-12
                "≥" -> 0.0 >= netConst - 1e-12
                else -> abs(netConst) <= 1e-12
            }
            val ansBn = if (holds) {
                "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
            } else {
                "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
            }
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "শূন্য সহগ বিশ্লেষণ (0x $op ${formatNum(netConst)})",
                    expressionOrMathBn = "০ $op ${formatNum(netConst)} ⟹ $ansBn",
                    explanationBn = if (holds) "অসমতাটি x এর সকল মানের জন্য সর্বদা সত্য।" else "অসমতাটি অসম্ভব, তাই কোনো বাস্তব সমাধান নেই।"
                )
            )
            return SolverPedagogyEnricher.enrich(
                SolverResult(
                    topic = SolverTopic.LINEAR_EQUATIONS,
                    mode = mode,
                    originalProblemBn = "সমাধান করুন: ${BengaliNumberFormatter.toBn(originalText)}",
                    formulaUsedBn = "রৈখিক অসমতার পক্ষান্তর বিধি",
                    steps = steps,
                    finalAnswerBn = ansBn,
                    verificationBn = "চলক পদ বিলুপ্ত হওয়ায় ধ্রুবক তুলনা দ্বারা যাচাইকৃত।"
                )
            )
        }

        val finalOp = if (netA < 0.0) reverseRelationSymbol(op) else op
        val bound = netConst / netA
        val boundBn = formatNum(bound)

        if (netA < 0.0) {
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "ঋণাত্মক সহগ (${formatNum(netA)}) দ্বারা ভাগ ও অসমতার চিহ্ন পরিবর্তন",
                    expressionOrMathBn = "x $finalOp ${formatNum(netConst)} ÷ (${formatNum(netA)}) ⟹ x $finalOp $boundBn",
                    explanationBn = "উভয় পক্ষকে ঋণাত্মক সংখ্যা (${formatNum(netA)}) দ্বারা ভাগ করায় অসমতার চিহ্ন '$op' পরিবর্তিত হয়ে '$finalOp' হয়েছে।",
                    whyThisStepBn = "ঋণাত্মক সংখ্যা দ্বারা গুণ বা ভাগ করলে সংখ্যারেখায় সংখ্যার অবস্থান উল্টে যায়, তাই অসমতার চিহ্ন উল্টে দেওয়া বাধ্যতামূলক।"
                )
            )
        } else {
            steps.add(
                SolutionStep(
                    stepIndex = stepIdx++,
                    titleBn = "ধনাত্মক সহগ (${formatNum(netA)}) দ্বারা ভাগ",
                    expressionOrMathBn = "x $finalOp ${formatNum(netConst)} ÷ ${formatNum(netA)} ⟹ x $finalOp $boundBn",
                    explanationBn = "ধনাত্মক সংখ্যা দ্বারা ভাগ করায় অসমতার চিহ্ন অপরিবর্তিত রয়েছে।"
                )
            )
        }

        val intervalBn = when (finalOp) {
            "<" -> "(-∞, $boundBn)"
            "≤" -> "(-∞, $boundBn]"
            ">" -> "($boundBn, ∞)"
            "≥" -> "[$boundBn, ∞)"
            else -> "{$boundBn}"
        }
        val finalAnsBn = "x $finalOp $boundBn  [ব্যবধি: $intervalBn]"

        return SolverPedagogyEnricher.enrich(
            SolverResult(
                topic = SolverTopic.LINEAR_EQUATIONS,
                mode = mode,
                originalProblemBn = "সমাধান করুন: ${BengaliNumberFormatter.toBn(originalText)}",
                formulaUsedBn = "ax + b $op cx + d (ঋণাত্মক সংখ্যা দিয়ে ভাগে অসমতার চিহ্ন পরিবর্তন)",
                steps = steps,
                finalAnswerBn = finalAnsBn,
                verificationBn = "সীমানা বিন্দু x = $boundBn বসালে উভয় পক্ষ সমান হয় এবং ব্যবধির যেকোনো বিন্দু অসমতাটিকে সিদ্ধ করে।",
                bcsShortcutBn = "বিসিএস সতর্কতা: অসমতার অংকে ৯০% ভুল হয় ঋণাত্মক সহগ (-a) দিয়ে ভাগ করার সময় অসমতার চিহ্ন উল্টাতে ভুলে যাওয়ার কারণে!",
                visualModelSpec = VisualModelSpec(
                    diagramType = VisualDiagramType.INEQUALITY_NUMBER_LINE,
                    titleBn = "সংখ্যারেখায় রৈখিক অসমতার সমাধান",
                    subtitleBn = finalAnsBn,
                    primaryValues = listOf(bound),
                    labelsBn = listOf(boundBn, finalOp),
                    accessibilitySummaryBn = "সংখ্যারেখায় রৈখিক অসমতার সমাধান: $finalAnsBn"
                )
            )
        )
    }

    // -------------------------------------------------------------
    // 3. COMPLETE 24-CASE QUADRATIC INEQUALITY SOLVER (ax² + bx + c ⋈ 0)
    // -------------------------------------------------------------
    fun solveQuadraticInequality(
        a: Double,
        b: Double,
        c: Double,
        rawRelation: String,
        mode: SolverMode
    ): SolverResult {
        val op = normalizeRelationSymbol(rawRelation)
        val eqBn = "${formatNum(a)}x² ${if (b >= 0) "+ ${formatNum(b)}x" else "− ${formatNum(abs(b))}x"} ${if (c >= 0) "+ ${formatNum(c)}" else "− ${formatNum(abs(c))}"} $op ০"

        if (abs(a) < 1e-12 || a.isNaN() || b.isNaN() || c.isNaN()) {
            return SolverResult(
                topic = SolverTopic.QUADRATIC_EQUATIONS,
                mode = mode,
                originalProblemBn = eqBn,
                finalAnswerBn = "সমাধান সম্ভব নয়",
                isSuccess = false,
                errorMessageBn = "দ্বিঘাত অসমতায় x² এর সহগ (a) শূন্য হতে পারে না।"
            )
        }

        val disc = b * b - 4.0 * a * c
        val discBn = formatNum(disc)
        val steps = mutableListOf<SolutionStep>()
        var stepIdx = 1

        val parabolaDirBn = if (a > 0) "ঊর্ধ্বমুখী (Upward Opening, a > ০)" else "নিম্নমুখী (Downward Opening, a < ০)"
        steps.add(
            SolutionStep(
                stepIndex = stepIdx++,
                titleBn = "প্রধান সহগ (a) এর চিহ্ন ও নিশ্চায়ক (Δ = b² − 4ac) নির্ণয়",
                expressionOrMathBn = "a = ${formatNum(a)} ($parabolaDirBn),  Δ = (${formatNum(b)})² − ৪(${formatNum(a)})(${formatNum(c)}) = $discBn",
                explanationBn = "দ্বিঘাত অসমতার সমাধান নির্ভর করে a এর চিহ্ন এবং নিশ্চায়ক Δ এর মানের ওপর।"
            )
        )

        val finalAnsBn: String
        val primaryPlotVals: List<Double>

        when {
            disc > 1e-9 -> {
                val sqrtD = sqrt(disc)
                val r1 = (-b - sqrtD) / (2.0 * a)
                val r2 = (-b + sqrtD) / (2.0 * a)
                val alpha = min(r1, r2)
                val beta = max(r1, r2)
                val alphaBn = formatNum(alpha)
                val betaBn = formatNum(beta)
                primaryPlotVals = listOf(alpha, beta)

                steps.add(
                    SolutionStep(
                        stepIndex = stepIdx++,
                        titleBn = "সমীকরণের বাস্তব মূলদ্বয় (ক্রমানুসারে α < β) নির্ণয়",
                        expressionOrMathBn = "ক্ষুদ্রতর মূল α = $alphaBn,  বৃহত্তর মূল β = $betaBn",
                        explanationBn = "সংখ্যারেখাকে α ও β বিন্দু দুটি তিনটি অঞ্চলে ভাগ করে: (-∞, $alphaBn), ($alphaBn, $betaBn) এবং ($betaBn, ∞)।"
                    )
                )

                finalAnsBn = if (a > 0) {
                    when (op) {
                        "<" -> "$alphaBn < x < $betaBn  [ব্যবধি: ($alphaBn, $betaBn)]"
                        "≤" -> "$alphaBn ≤ x ≤ $betaBn  [ব্যবধি: [$alphaBn, $betaBn]]"
                        ">" -> "x < $alphaBn অথবা x > $betaBn  [ব্যবধি: (-∞, $alphaBn) ∪ ($betaBn, ∞)]"
                        else -> "x ≤ $alphaBn অথবা x ≥ $betaBn  [ব্যবধি: (-∞, $alphaBn] ∪ [$betaBn, ∞)]"
                    }
                } else {
                    // a < 0: parabola is negative outside (alpha, beta) and positive inside (alpha, beta)
                    when (op) {
                        "<" -> "x < $alphaBn অথবা x > $betaBn  [ব্যবধি: (-∞, $alphaBn) ∪ ($betaBn, ∞)]"
                        "≤" -> "x ≤ $alphaBn অথবা x ≥ $betaBn  [ব্যবধি: (-∞, $alphaBn] ∪ [$betaBn, ∞)]"
                        ">" -> "$alphaBn < x < $betaBn  [ব্যবধি: ($alphaBn, $betaBn)]"
                        else -> "$alphaBn ≤ x ≤ $betaBn  [ব্যবধি: [$alphaBn, $betaBn]]"
                    }
                }

                steps.add(
                    SolutionStep(
                        stepIndex = stepIdx++,
                        titleBn = "চিহ্ন তালিকা (Sign Chart) ও সমাধান ব্যবধি নির্ধারণ",
                        expressionOrMathBn = finalAnsBn,
                        explanationBn = if (a > 0) {
                            "যেহেতু a > ০, তাই রাশিটি মূলদ্বয়ের মধ্যবর্তী অঞ্চলে ($alphaBn, $betaBn) ঋণাত্মক এবং বাইরে ধনাত্মক।"
                        } else {
                            "যেহেতু a < ০, তাই রাশিটি মূলদ্বয়ের মধ্যবর্তী অঞ্চলে ($alphaBn, $betaBn) ধনাত্মক এবং দুই প্রান্তের বাইরে ঋণাত্মক।"
                        }
                    )
                )
            }
            abs(disc) <= 1e-9 -> {
                val r0 = -b / (2.0 * a)
                val r0Bn = formatNum(r0)
                primaryPlotVals = listOf(r0)

                steps.add(
                    SolutionStep(
                        stepIndex = stepIdx++,
                        titleBn = "পূর্ণবর্গ আকার ও স্পর্শবিন্দু (Δ = ০) বিশ্লেষণ",
                        expressionOrMathBn = "${formatNum(a)}(x − $r0Bn)² $op ০  (যেখানে দ্বিত্ব মূল x₀ = $r0Bn)",
                        explanationBn = "নিশ্চায়ক শূন্য হওয়ায় রাশিটি x = $r0Bn বিন্দুতে ০ হয় এবং অন্য সকল বিন্দুতে a এর সমচিহ্নবিশিষ্ট থাকে।"
                    )
                )

                finalAnsBn = if (a > 0) {
                    when (op) {
                        "<" -> "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
                        "≤" -> "একক বিন্দু সমাধান: x = $r0Bn"
                        ">" -> "x ≠ $r0Bn  [ব্যবধি: (-∞, $r0Bn) ∪ ($r0Bn, ∞)]"
                        else -> "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
                    }
                } else {
                    // a < 0: a(x - r0)^2 <= 0 for all real x, and == 0 at x = r0
                    when (op) {
                        "<" -> "x ≠ $r0Bn  [ব্যবধি: (-∞, $r0Bn) ∪ ($r0Bn, ∞)]"
                        "≤" -> "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
                        ">" -> "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
                        else -> "একক বিন্দু সমাধান: x = $r0Bn"
                    }
                }

                steps.add(
                    SolutionStep(
                        stepIndex = stepIdx++,
                        titleBn = "চূড়ান্ত সমাধান সেট",
                        expressionOrMathBn = finalAnsBn,
                        explanationBn = "পূর্ণবর্গ রাশির অঋণাত্মক ধর্ম (x − $r0Bn)² ≥ ০ এবং a এর চিহ্ন বিবেচনা করে সমাধান নির্ধারিত হলো।"
                    )
                )
            }
            else -> {
                // disc < 0: no real roots; expression has the same sign as 'a' for all real x
                val vx = -b / (2.0 * a)
                primaryPlotVals = listOf(vx)

                finalAnsBn = if (a > 0) {
                    when (op) {
                        "<", "≤" -> "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
                        else -> "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
                    }
                } else {
                    when (op) {
                        "<", "≤" -> "সকল বাস্তব সংখ্যা: x ∈ ℝ  [ব্যবধি: (-∞, ∞)]"
                        else -> "কোনো বাস্তব সমাধান নেই: x ∈ ∅"
                    }
                }

                steps.add(
                    SolutionStep(
                        stepIndex = stepIdx++,
                        titleBn = "বাস্তব মূলহীন প্যারাবোলার চিহ্ন বিশ্লেষণ (Δ < ০)",
                        expressionOrMathBn = "Δ = $discBn < ০ এবং a = ${formatNum(a)} ⟹ $finalAnsBn",
                        explanationBn = if (a > 0) {
                            "Δ < ০ এবং a > ০ হওয়ায় প্যারাবোলাটি সম্পূর্ণভাবে x-অক্ষের ওপরে অবস্থিত; অর্থাৎ সকল বাস্তব x এর জন্য রাশিটি সর্বদা ধনাত্মক (> ০)।"
                        } else {
                            "Δ < ০ এবং a < ০ হওয়ায় প্যারাবোলাটি সম্পূর্ণভাবে x-অক্ষের নিচে অবস্থিত; অর্থাৎ সকল বাস্তব x এর জন্য রাশিটি সর্বদা ঋণাত্মক (< ০)।"
                        }
                    )
                )
            }
        }

        return SolverPedagogyEnricher.enrich(
            SolverResult(
                topic = SolverTopic.QUADRATIC_EQUATIONS,
                mode = mode,
                originalProblemBn = "দ্বিঘাত অসমতা সমাধান করুন: $eqBn",
                formulaUsedBn = "দ্বিঘাত অসমতার চিহ্ন তালিকা (Sign Chart): a(x − α)(x − β) $op ০",
                steps = steps,
                finalAnswerBn = finalAnsBn,
                verificationBn = "প্যারাবোলার শীর্ষবিন্দু ও সংখ্যারেখার চিহ্ন পরীক্ষা দ্বারা যাচাইকৃত।",
                bcsShortcutBn = "বিসিএস শর্টকাট: a > ০ এবং মূলদ্বয় α < β হলে (x − α)(x − β) < ০ এর উত্তর সর্বদা মাঝের ব্যবধি (α, β), আর > ০ হলে দুই পাশের ব্যবধি (-∞, α) ∪ (β, ∞)!",
                visualModelSpec = VisualModelSpec(
                    diagramType = VisualDiagramType.PARABOLA_PLOT,
                    titleBn = "দ্বিঘাত প্যারাবোলা ও চিহ্ন তালিকা (Sign Chart)",
                    subtitleBn = finalAnsBn,
                    primaryValues = primaryPlotVals,
                    secondaryValues = listOf(a, b, c, disc),
                    labelsBn = listOf(eqBn, finalAnsBn),
                    accessibilitySummaryBn = "দ্বিঘাত অসমতার লেখচিত্র ও সমাধান: $finalAnsBn"
                )
            )
        )
    }

    private fun formatLinearExprBn(a: Double, b: Double): String {
        val aPart = when {
            abs(a - 1.0) < 1e-12 -> "x"
            abs(a + 1.0) < 1e-12 -> "-x"
            else -> "${formatNum(a)}x"
        }
        return when {
            abs(b) < 1e-12 -> aPart
            b > 0 -> "$aPart + ${formatNum(b)}"
            else -> "$aPart − ${formatNum(abs(b))}"
        }
    }
}
