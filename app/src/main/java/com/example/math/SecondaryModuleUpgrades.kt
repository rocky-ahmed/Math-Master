package com.example.math

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

// =========================================================================
// 1. FORMULA LIBRARY UPGRADE: Interactive Variable Calculator & Mini-Quiz
// =========================================================================

data class FormulaVariableInputSpec(
    val symbol: String,
    val labelBn: String,
    val defaultValue: String,
    val mustBePositive: Boolean = false,
    val mustBeNonZero: Boolean = false
)

data class FormulaCalculationResult(
    val isSuccess: Boolean,
    val formulaTitleBn: String,
    val expressionBn: String,
    val variableDefinitionsBn: List<String>,
    val substitutionStepsBn: List<String>,
    val finalAnswerBn: String,
    val errorMessageBn: String? = null
)

data class FormulaMiniQuizItem(
    val id: String,
    val formulaId: String,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationBn: String
)

object FormulaInteractiveEngine {

    fun getVariableSpecsForFormula(entry: LibraryFormula): List<FormulaVariableInputSpec> {
        if (entry.variables.isNotEmpty()) {
            return entry.variables.take(3).mapIndexed { index, (sym, desc) ->
                val cleanSym = sym.trim().ifEmpty { "v${index + 1}" }
                val defaultVal = when (index) {
                    0 -> "6"
                    1 -> "4"
                    else -> "2"
                }
                val isDivisorOrPositive = desc.contains("হর") || desc.contains("ব্যাসার্ধ") ||
                    desc.contains("দৈর্ঘ্য") || desc.contains("প্রস্থ") || desc.contains("উচ্চতা") ||
                    desc.contains("ভূমি") || desc.contains("সময়") || desc.contains("মূলধন")
                FormulaVariableInputSpec(
                    symbol = cleanSym,
                    labelBn = "$cleanSym ($desc)",
                    defaultValue = defaultVal,
                    mustBePositive = isDivisorOrPositive,
                    mustBeNonZero = desc.contains("হর") || cleanSym.equals("q", ignoreCase = true) || cleanSym.equals("n", ignoreCase = true)
                )
            }
        }
        return when (entry.subject) {
            FormulaSubject.ALGEBRA -> listOf(
                FormulaVariableInputSpec("a", "প্রথম রাশি (a)", "5"),
                FormulaVariableInputSpec("b", "দ্বিতীয় রাশি (b)", "3")
            )
            FormulaSubject.ARITHMETIC, FormulaSubject.COMMERCIAL_MATH -> listOf(
                FormulaVariableInputSpec("P", "আসল বা মূল মান (P)", "1000", mustBePositive = true),
                FormulaVariableInputSpec("r", "শতকরা হার (r%)", "10", mustBePositive = true),
                FormulaVariableInputSpec("n", "সময় বা একক (n)", "2", mustBePositive = true)
            )
            FormulaSubject.GEOMETRY, FormulaSubject.MENSURATION -> listOf(
                FormulaVariableInputSpec("x", "দৈর্ঘ্য / ভূমি / ব্যাসার্ধ (x)", "6", mustBePositive = true),
                FormulaVariableInputSpec("y", "প্রস্থ / উচ্চতা (y)", "4", mustBePositive = true)
            )
            else -> listOf(
                FormulaVariableInputSpec("a", "প্রথম মান (a)", "6"),
                FormulaVariableInputSpec("b", "দ্বিতীয় মান (b)", "3", mustBeNonZero = true)
            )
        }
    }

    private fun evaluateModularCyclicityFormula(
        entry: LibraryFormula,
        specs: List<FormulaVariableInputSpec>,
        rawInputs: Map<String, String>
    ): FormulaCalculationResult {
        val parsedLongs = linkedMapOf<String, Long>()
        for (spec in specs) {
            val rawOriginal = rawInputs[spec.symbol]?.trim().orEmpty()
            val normalized = BengaliNumberFormatter.toEnglishDigits(rawOriginal)
                .replace("−", "-")
                .replace("–", "-")
                .trim()
            if (normalized.isEmpty() || normalized.toBigDecimalOrNull() == null) {
                return FormulaCalculationResult(
                    isSuccess = false,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = emptyList(),
                    substitutionStepsBn = emptyList(),
                    finalAnswerBn = "",
                    errorMessageBn = "'${spec.labelBn}' এর জন্য বৈধ পূর্ণসংখ্যা লিখুন।"
                )
            }
            if (normalized.contains('.') || !normalized.matches(Regex("^[+-]?\\d+$"))) {
                return FormulaCalculationResult(
                    isSuccess = false,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = emptyList(),
                    substitutionStepsBn = emptyList(),
                    finalAnswerBn = "",
                    errorMessageBn = "'${spec.labelBn}' পূর্ণসংখ্যা হতে হবে (দশমিক বা ভগ্নাংশ গ্রহণযোগ্য নয়)।"
                )
            }
            val parsedLong = normalized.toLongOrNull()
                ?: return FormulaCalculationResult(
                    isSuccess = false,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = emptyList(),
                    substitutionStepsBn = emptyList(),
                    finalAnswerBn = "",
                    errorMessageBn = "'${spec.labelBn}' এর মান অনুমোদিত পূর্ণসংখ্যার সীমার বাইরে।"
                )
            parsedLongs[spec.symbol] = parsedLong
        }

        val baseA = parsedLongs["a"] ?: parsedLongs.values.firstOrNull() ?: 7L
        val expB = parsedLongs["b"] ?: parsedLongs.values.drop(1).firstOrNull() ?: 1L
        val expSpecLabel = specs.find { it.symbol == "b" }?.labelBn
            ?: specs.getOrNull(1)?.labelBn
            ?: "ঘাত (b)"

        if (expB == 0L) {
            return FormulaCalculationResult(
                isSuccess = false,
                formulaTitleBn = entry.titleBn,
                expressionBn = entry.formulaExpression,
                variableDefinitionsBn = emptyList(),
                substitutionStepsBn = emptyList(),
                finalAnswerBn = "",
                errorMessageBn = "'$expSpecLabel' এর মান কমপক্ষে ১ (b ≥ ১ ধনাত্মক পূর্ণসংখ্যা) হওয়া আবশ্যক; b = ০ এই সূত্রের ডোমেইনের অন্তর্ভুক্ত নয়।"
            )
        }
        if (expB < 0L) {
            return FormulaCalculationResult(
                isSuccess = false,
                formulaTitleBn = entry.titleBn,
                expressionBn = entry.formulaExpression,
                variableDefinitionsBn = emptyList(),
                substitutionStepsBn = emptyList(),
                finalAnswerBn = "",
                errorMessageBn = "'$expSpecLabel' ঋণাত্মক হতে পারবে না; এই সূত্রের জন্য ধনাত্মক পূর্ণসংখ্যা (b ≥ ১) আবশ্যক।"
            )
        }

        val baseMod10 = ((baseA % 10L) + 10L) % 10L
        val reducedExp = (((expB - 1L) % 4L + 4L) % 4L) + 1L
        var unitDigit = 1L
        repeat(reducedExp.toInt()) {
            unitDigit = (unitDigit * baseMod10) % 10L
        }

        val defs = specs.map { spec ->
            val v = parsedLongs[spec.symbol] ?: 0L
            "${spec.symbol} = ${spec.labelBn} = ${BengaliNumberFormatter.toBn(v)}"
        }
        val steps = listOf(
            "ভিত্তির একক রিসিডিউ (Euclidean mod 10): ${BengaliNumberFormatter.toBn(baseA)} mod 10 = ${BengaliNumberFormatter.toBn(baseMod10)}",
            "সূচক সংকোচন: ((${BengaliNumberFormatter.toBn(expB)} − ১) mod 4) + ১ = ${BengaliNumberFormatter.toBn(reducedExp)}",
            "একক স্থানীয় অঙ্ক: ${BengaliNumberFormatter.toBn(baseMod10)}^${BengaliNumberFormatter.toBn(reducedExp)} mod 10 = ${BengaliNumberFormatter.toBn(unitDigit)}"
        )

        return FormulaCalculationResult(
            isSuccess = true,
            formulaTitleBn = entry.titleBn,
            expressionBn = entry.formulaExpression,
            variableDefinitionsBn = defs,
            substitutionStepsBn = steps,
            finalAnswerBn = BengaliNumberFormatter.toBn(unitDigit)
        )
    }

    fun evaluateFormula(
        entry: LibraryFormula,
        rawInputs: Map<String, String>
    ): FormulaCalculationResult {
        val specs = getVariableSpecsForFormula(entry)
        if (entry.id == "nt_bdmo_5") {
            return evaluateModularCyclicityFormula(entry, specs, rawInputs)
        }
        val parsed = linkedMapOf<String, Double>()

        for (spec in specs) {
            val raw = rawInputs[spec.symbol]?.trim().orEmpty()
            val normalized = BengaliNumberFormatter.toEnglishDigits(raw)
            val value = normalized.toDoubleOrNull()
                ?: return FormulaCalculationResult(
                    isSuccess = false,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = emptyList(),
                    substitutionStepsBn = emptyList(),
                    finalAnswerBn = "",
                    errorMessageBn = "'${spec.labelBn}' এর জন্য বৈধ সংখ্যা লিখুন।"
                )
            if (spec.mustBeNonZero && abs(value) < 1e-9) {
                return FormulaCalculationResult(
                    isSuccess = false,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = emptyList(),
                    substitutionStepsBn = emptyList(),
                    finalAnswerBn = "",
                    errorMessageBn = "'${spec.labelBn}' এর মান শূন্য (০) হতে পারবে না (শূন্য দ্বারা ভাগ অসংজ্ঞায়িত)।"
                )
            }
            if (spec.mustBePositive && value <= 0.0) {
                return FormulaCalculationResult(
                    isSuccess = false,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = emptyList(),
                    substitutionStepsBn = emptyList(),
                    finalAnswerBn = "",
                    errorMessageBn = "'${spec.labelBn}' এর মান ধনাত্মক (> ০) হওয়া আবশ্যক।"
                )
            }
            parsed[spec.symbol] = value
        }

        val defs = specs.map { "${it.symbol} = ${it.labelBn} = ${formatNumBn(parsed[it.symbol] ?: 0.0)}" }
        val valuesList = parsed.values.toList()
        val v1 = valuesList.getOrElse(0) { 5.0 }
        val v2 = valuesList.getOrElse(1) { 3.0 }
        val v3 = valuesList.getOrElse(2) { 2.0 }

        // Exact Formula-ID Dispatch first
        val exactById: Pair<List<String>, String>? = when (entry.id) {
            "nt_1" -> {
                val nLong = v1.toLong().coerceAtLeast(1L)
                val sum = nLong * (nLong + 1L) / 2L
                listOf(
                    "S = n(n + 1) / 2 = ${formatNumBn(nLong.toDouble())} × ${formatNumBn((nLong + 1).toDouble())} ÷ ২",
                    "= ${BengaliNumberFormatter.toBn(sum)}"
                ) to BengaliNumberFormatter.toBn(sum)
            }
            "nt_2" -> {
                val nLong = v1.toLong().coerceAtLeast(1L)
                val sumSq = nLong * (nLong + 1L) * (2L * nLong + 1L) / 6L
                listOf(
                    "S = n(n + 1)(2n + 1) / 6 = ${BengaliNumberFormatter.toBn(nLong)} × ${BengaliNumberFormatter.toBn(nLong + 1)} × ${BengaliNumberFormatter.toBn(2 * nLong + 1)} ÷ ৬",
                    "= ${BengaliNumberFormatter.toBn(sumSq)}"
                ) to BengaliNumberFormatter.toBn(sumSq)
            }
            "nt_3" -> {
                val nLong = v1.toLong().coerceAtLeast(1L)
                val base = nLong * (nLong + 1L) / 2L
                val sumCube = base * base
                listOf(
                    "S = [n(n + 1) / 2]² = [${BengaliNumberFormatter.toBn(base)}]²",
                    "= ${BengaliNumberFormatter.toBn(sumCube)}"
                ) to BengaliNumberFormatter.toBn(sumCube)
            }
            "nt_4" -> {
                val aL = v1.toLong().coerceAtLeast(1L)
                val bL = v2.toLong().coerceAtLeast(1L)
                val g = NumberSystemProvider.gcd(aL, bL)
                val l = NumberSystemProvider.lcm(aL, bL)
                listOf(
                    "গ.সা.গু(${BengaliNumberFormatter.toBn(aL)}, ${BengaliNumberFormatter.toBn(bL)}) = ${BengaliNumberFormatter.toBn(g)}, ল.সা.গু = ${BengaliNumberFormatter.toBn(l)}",
                    "গুণফল যাচাই: ${BengaliNumberFormatter.toBn(aL)} × ${BengaliNumberFormatter.toBn(bL)} = ${BengaliNumberFormatter.toBn(aL * bL)} = ${BengaliNumberFormatter.toBn(g)} × ${BengaliNumberFormatter.toBn(l)}"
                ) to "গ.সা.গু = ${BengaliNumberFormatter.toBn(g)}, ল.সা.গু = ${BengaliNumberFormatter.toBn(l)}"
            }
            "nt_bdmo_1", "nt_bdmo_2" -> {
                val analysis = SharedMathEngine.analyzeDivisorsAndTotient(v1.toLong().coerceAtLeast(2L))
                listOf(
                    "মৌলিক উৎপাদক: ${analysis.primePowerBn}",
                    "ভাজক সংখ্যা d(N) = ${BengaliNumberFormatter.toBn(analysis.divisorCount)}, ভাজক সমষ্টি σ(N) = ${BengaliNumberFormatter.toBn(analysis.divisorSum)}, টোশেন্ট φ(N) = ${BengaliNumberFormatter.toBn(analysis.eulerTotient)}"
                ) to "d(N) = ${BengaliNumberFormatter.toBn(analysis.divisorCount)}, σ(N) = ${BengaliNumberFormatter.toBn(analysis.divisorSum)}, φ(N) = ${BengaliNumberFormatter.toBn(analysis.eulerTotient)}"
            }
            "coord_1" -> {
                val dist = sqrt((v1 * v1) + (v2 * v2))
                listOf(
                    "Δx = ${formatNumBn(v1)}, Δy = ${formatNumBn(v2)}",
                    "দূরত্ব d = √(Δx² + Δy²) = √(${formatNumBn(v1 * v1)} + ${formatNumBn(v2 * v2)}) = ${formatNumBn(dist)}"
                ) to "${formatNumBn(dist)} একক"
            }
            "seq_1" -> {
                val term = v1 + (v2 - 1.0) * v3
                listOf(
                    "aₙ = a + (n − 1)d = ${formatNumBn(v1)} + (${formatNumBn(v2)} − ১) × ${formatNumBn(v3)}",
                    "= ${formatNumBn(term)}"
                ) to formatNumBn(term)
            }
            "seq_2" -> {
                val sum = (v1 / 2.0) * (2.0 * v2 + (v1 - 1.0) * v3)
                listOf(
                    "Sₙ = (n/2)[2a + (n − 1)d] = (${formatNumBn(v1)}/২) × [২(${formatNumBn(v2)}) + (${formatNumBn(v1)} − ১)×${formatNumBn(v3)}]",
                    "= ${formatNumBn(sum)}"
                ) to formatNumBn(sum)
            }
            "prob_1" -> {
                val denom = v2.coerceAtLeast(1.0)
                val p = (v1 / denom).coerceIn(0.0, 1.0)
                listOf(
                    "P(A) = n(A) / n(S) = ${formatNumBn(v1)} ÷ ${formatNumBn(denom)} = ${formatNumBn(p)}",
                    "শতকরা সম্ভাব্যতা = ${formatNumBn(p * 100.0)}%"
                ) to "${formatNumBn(p)} (${formatNumBn(p * 100.0)}%)"
            }
            else -> null
        }

        if (exactById != null) {
            return FormulaCalculationResult(
                isSuccess = true,
                formulaTitleBn = entry.titleBn,
                expressionBn = entry.formulaExpression,
                variableDefinitionsBn = defs,
                substitutionStepsBn = exactById.first,
                finalAnswerBn = exactById.second
            )
        }

        return when (entry.subject) {
            FormulaSubject.ALGEBRA -> {
                val expr = entry.formulaExpression
                val isDiffSq = expr.contains("a² - b²") || entry.titleBn.contains("বর্গান্তর")
                val isCube = expr.contains("³") || entry.titleBn.contains("ঘন")
                val isMinus = expr.contains("(a - b)")
                val resultVal: Double
                val step1: String
                val step2: String
                when {
                    isCube && isMinus -> {
                        resultVal = (v1 - v2).pow(3.0)
                        step1 = "(a - b)³ = (${formatNumBn(v1)} - ${formatNumBn(v2)})³"
                        step2 = "= (${formatNumBn(v1 - v2)})³ = ${formatNumBn(resultVal)}"
                    }
                    isCube -> {
                        resultVal = (v1 + v2).pow(3.0)
                        step1 = "(a + b)³ = (${formatNumBn(v1)} + ${formatNumBn(v2)})³"
                        step2 = "= (${formatNumBn(v1 + v2)})³ = ${formatNumBn(resultVal)}"
                    }
                    isDiffSq -> {
                        resultVal = (v1 + v2) * (v1 - v2)
                        step1 = "a² - b² = (${formatNumBn(v1)} + ${formatNumBn(v2)})(${formatNumBn(v1)} - ${formatNumBn(v2)})"
                        step2 = "= ${formatNumBn(v1 + v2)} × ${formatNumBn(v1 - v2)} = ${formatNumBn(resultVal)}"
                    }
                    isMinus -> {
                        resultVal = (v1 - v2).pow(2.0)
                        step1 = "(a - b)² = ${formatNumBn(v1)}² - ২(${formatNumBn(v1)})(${formatNumBn(v2)}) + ${formatNumBn(v2)}²"
                        step2 = "= ${formatNumBn(v1 * v1)} - ${formatNumBn(2 * v1 * v2)} + ${formatNumBn(v2 * v2)} = ${formatNumBn(resultVal)}"
                    }
                    else -> {
                        resultVal = (v1 + v2).pow(2.0)
                        step1 = "(a + b)² = ${formatNumBn(v1)}² + ২(${formatNumBn(v1)})(${formatNumBn(v2)}) + ${formatNumBn(v2)}²"
                        step2 = "= ${formatNumBn(v1 * v1)} + ${formatNumBn(2 * v1 * v2)} + ${formatNumBn(v2 * v2)} = ${formatNumBn(resultVal)}"
                    }
                }
                FormulaCalculationResult(
                    isSuccess = true,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = defs,
                    substitutionStepsBn = listOf("মান বসিয়ে পাই: $step1", "গণনা করে পাই: $step2"),
                    finalAnswerBn = formatNumBn(resultVal)
                )
            }
            FormulaSubject.ARITHMETIC, FormulaSubject.COMMERCIAL_MATH -> {
                val isCompound = entry.titleBn.contains("চক্রবৃদ্ধি")
                val resultVal = if (isCompound) {
                    v1 * (1.0 + v2 / 100.0).pow(v3)
                } else if (valuesList.size >= 3) {
                    (v1 * v2 * v3) / 100.0
                } else {
                    (v1 * v2) / 100.0
                }
                FormulaCalculationResult(
                    isSuccess = true,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = defs,
                    substitutionStepsBn = listOf(
                        "সূত্রে প্রদত্ত চলকের মান প্রতিস্থাপন করে পাই:",
                        "ফলাফল = ${formatNumBn(resultVal)}"
                    ),
                    finalAnswerBn = formatNumBn(resultVal)
                )
            }
            FormulaSubject.GEOMETRY, FormulaSubject.MENSURATION -> {
                val isTriangle = entry.titleBn.contains("ত্রিভুজ")
                val isCircle = entry.titleBn.contains("বৃত্ত") || entry.titleBn.contains("গোলক")
                val resultVal = when {
                    isCircle -> PI * v1 * v1
                    isTriangle -> 0.5 * v1 * v2
                    else -> v1 * v2
                }
                val step = when {
                    isCircle -> "π × r² = ৩.১৪১৬ × ${formatNumBn(v1)}² = ${formatNumBn(resultVal)}"
                    isTriangle -> "০.৫ × ${formatNumBn(v1)} × ${formatNumBn(v2)} = ${formatNumBn(resultVal)}"
                    else -> "${formatNumBn(v1)} × ${formatNumBn(v2)} = ${formatNumBn(resultVal)}"
                }
                FormulaCalculationResult(
                    isSuccess = true,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = defs,
                    substitutionStepsBn = listOf("মান বসিয়ে পাই: $step"),
                    finalAnswerBn = "${formatNumBn(resultVal)} একক"
                )
            }
            FormulaSubject.TRIGONOMETRY -> {
                val h = sqrt(v1 * v1 + v2 * v2)
                if (abs(h) < 1e-9) {
                    return FormulaCalculationResult(
                        isSuccess = false,
                        formulaTitleBn = entry.titleBn,
                        expressionBn = entry.formulaExpression,
                        variableDefinitionsBn = defs,
                        substitutionStepsBn = emptyList(),
                        finalAnswerBn = "",
                        errorMessageBn = "অতিভুজ শূন্য হতে পারে না।"
                    )
                }
                val sinVal = v1 / h
                val cosVal = v2 / h
                FormulaCalculationResult(
                    isSuccess = true,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = defs,
                    substitutionStepsBn = listOf(
                        "অতিভুজ h = √(${formatNumBn(v1)}² + ${formatNumBn(v2)}²) = ${formatNumBn(h)}",
                        "sinθ = ${formatNumBn(sinVal)}, cosθ = ${formatNumBn(cosVal)}"
                    ),
                    finalAnswerBn = "অতিভুজ = ${formatNumBn(h)}, sinθ = ${formatNumBn(sinVal)}"
                )
            }
            FormulaSubject.NUMBER_THEORY -> {
                val analysis = SharedMathEngine.analyzeDivisorsAndTotient(v1.toLong().coerceAtLeast(2L))
                FormulaCalculationResult(
                    isSuccess = true,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = defs,
                    substitutionStepsBn = analysis.stepsBn.map { "${it.first}: ${it.second}" },
                    finalAnswerBn = "d(N) = ${BengaliNumberFormatter.toBn(analysis.divisorCount)}, σ(N) = ${BengaliNumberFormatter.toBn(analysis.divisorSum)}"
                )
            }
            else -> {
                if (abs(v2) < 1e-9) {
                    return FormulaCalculationResult(
                        isSuccess = false,
                        formulaTitleBn = entry.titleBn,
                        expressionBn = entry.formulaExpression,
                        variableDefinitionsBn = defs,
                        substitutionStepsBn = emptyList(),
                        finalAnswerBn = "",
                        errorMessageBn = "শূন্য (০) দ্বারা ভাগ করা সম্ভব নয়।"
                    )
                }
                val sum = v1 + v2
                val prod = v1 * v2
                FormulaCalculationResult(
                    isSuccess = true,
                    formulaTitleBn = entry.titleBn,
                    expressionBn = entry.formulaExpression,
                    variableDefinitionsBn = defs,
                    substitutionStepsBn = listOf(
                        "চলকগুলোর মান বসিয়ে: প্রথম মান = ${formatNumBn(v1)}, দ্বিতীয় মান = ${formatNumBn(v2)}",
                        "সমষ্টি = ${formatNumBn(sum)}, গুণফল = ${formatNumBn(prod)}"
                    ),
                    finalAnswerBn = formatNumBn(prod)
                )
            }
        }
    }

    fun generateMiniQuizForFormula(entry: LibraryFormula): List<FormulaMiniQuizItem> {
        return listOf(
            FormulaMiniQuizItem(
                id = "${entry.id}_mq_1",
                formulaId = entry.id,
                questionBn = "'${entry.titleBn}' এর সঠিক গাণিতিক রূপ নিচের কোনটি?",
                optionsBn = listOf(
                    entry.formulaExpression,
                    "${entry.formulaExpression} + 2ab",
                    "2 × (${entry.formulaExpression})",
                    "১ / (${entry.formulaExpression})"
                ).distinct(),
                correctIndex = 0,
                explanationBn = "${entry.titleBn}-এর প্রমিত সূত্র হলো: ${entry.formulaExpression}। উদাহরণ: ${entry.exampleProblemBn} → ${entry.exampleSolutionBn}"
            ),
            FormulaMiniQuizItem(
                id = "${entry.id}_mq_2",
                formulaId = entry.id,
                questionBn = "'${entry.titleBn}' সূত্রের শর্ত ও প্রয়োগ সম্পর্কে কোনটি সঠিক?",
                optionsBn = listOf(
                    entry.conditionsBn.ifBlank { entry.explanationBn },
                    "শুধুমাত্র কাল্পনিক সংখ্যার ক্ষেত্রে প্রযোজ্য",
                    "হর শূন্য (০) হলেও সূত্রটি সর্বদা সংজ্ঞায়িত",
                    "কোনো গাণিতিক সমাধানে ব্যবহৃত হয় না"
                ),
                correctIndex = 0,
                explanationBn = "শর্ত ও ব্যাখ্যা: ${entry.explanationBn} (${entry.conditionsBn})"
            )
        )
    }

    private fun formatNumBn(v: Double): String {
        val str = if (abs(v - v.roundToInt()) < 1e-6) {
            v.roundToInt().toString()
        } else {
            String.format("%.2f", v)
        }
        return str.map { ch ->
            if (ch in '0'..'9') ('০' + (ch - '0')) else ch
        }.joinToString("")
    }
}

// =========================================================================
// 2. PROBLEM SOLVER UPGRADE: Similar Problem Generator & History Serializer
// =========================================================================

data class GeneratedSimilarProblem(
    val topic: SolverTopic,
    val difficulty: PracticeDifficulty,
    val difficultyLabelBn: String,
    val problemStatementBn: String,
    val prefilledInputs: Map<String, String>,
    val solution: SolverResult
)

object SimilarProblemGenerator {

    fun generateSimilarProblems(topic: SolverTopic): List<GeneratedSimilarProblem> {
        return listOf(
            createForDifficulty(topic, PracticeDifficulty.EASY, "সহজ স্তর (Easy)"),
            createForDifficulty(topic, PracticeDifficulty.MEDIUM, "মাঝারি স্তর (Medium)"),
            createForDifficulty(topic, PracticeDifficulty.HARD, "কঠিন স্তর (Hard)")
        )
    }

    private fun createForDifficulty(
        topic: SolverTopic,
        difficulty: PracticeDifficulty,
        diffLabelBn: String
    ): GeneratedSimilarProblem {
        val solved: SolverResult
        val inputs: Map<String, String>

        when (topic) {
            SolverTopic.BASIC_ARITHMETIC -> {
                val expr = when (difficulty) {
                    PracticeDifficulty.EASY -> "(২০ + ১০) × ২ - ১৫ ÷ ৩"
                    PracticeDifficulty.MEDIUM -> "(৪৫ + ১৫) ÷ ৩ + ১২ × ৪ - ১০"
                    else -> "(১২০ ÷ ৬ + ১৫) × ৩ - ৪৫ ÷ ৯"
                }
                inputs = mapOf("expr" to expr)
                solved = MathProblemSolverEngine.solveArithmetic(expr, SolverMode.LEARNING)
            }
            SolverTopic.FRACTIONS_DECIMALS -> {
                val (n1, d1, n2, d2) = when (difficulty) {
                    PracticeDifficulty.EASY -> listOf(1L, 2L, 1L, 3L)
                    PracticeDifficulty.MEDIUM -> listOf(3L, 4L, 2L, 5L)
                    else -> listOf(5L, 6L, 7L, 8L)
                }
                inputs = mapOf("n1" to n1.toString(), "d1" to d1.toString(), "op" to "+", "n2" to n2.toString(), "d2" to d2.toString())
                solved = MathProblemSolverEngine.solveFraction(n1, d1, "+", n2, d2, SolverMode.LEARNING)
            }
            SolverTopic.PERCENTAGE -> {
                val (v1, v2) = when (difficulty) {
                    PracticeDifficulty.EASY -> 15.0 to 400.0
                    PracticeDifficulty.MEDIUM -> 25.0 to 1200.0
                    else -> 12.5 to 2400.0
                }
                inputs = mapOf("v1" to v1.toString(), "v2" to v2.toString())
                solved = MathProblemSolverEngine.solvePercentage(
                    MathProblemSolverEngine.PercentageType.OF_NUMBER, v1, v2, SolverMode.LEARNING
                )
            }
            SolverTopic.RATIO_PROPORTION -> {
                val (tot, r1, r2) = when (difficulty) {
                    PracticeDifficulty.EASY -> Triple(500.0, 2.0, 3.0)
                    PracticeDifficulty.MEDIUM -> Triple(1400.0, 3.0, 4.0)
                    else -> Triple(3600.0, 5.0, 7.0)
                }
                inputs = mapOf("total" to tot.toInt().toString(), "r1" to r1.toInt().toString(), "r2" to r2.toInt().toString())
                solved = MathProblemSolverEngine.solveRatio(
                    MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, tot, r1, r2, null, SolverMode.LEARNING
                )
            }
            SolverTopic.PROFIT_LOSS -> {
                val (cp, sp) = when (difficulty) {
                    PracticeDifficulty.EASY -> 400.0 to 460.0
                    PracticeDifficulty.MEDIUM -> 1200.0 to 1380.0
                    else -> 2500.0 to 2125.0
                }
                inputs = mapOf("cp" to cp.toInt().toString(), "sp" to sp.toInt().toString())
                solved = MathProblemSolverEngine.solveProfitLoss(
                    MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT, cp, sp, null, null, SolverMode.LEARNING
                )
            }
            SolverTopic.AVERAGE -> {
                val nums = when (difficulty) {
                    PracticeDifficulty.EASY -> listOf(10.0, 20.0, 30.0, 40.0)
                    PracticeDifficulty.MEDIUM -> listOf(15.0, 25.0, 35.0, 45.0, 55.0)
                    else -> listOf(12.0, 18.0, 24.0, 30.0, 36.0, 42.0)
                }
                inputs = mapOf("list" to nums.joinToString(", ") { it.toInt().toString() })
                solved = MathProblemSolverEngine.solveAverage(nums, SolverMode.LEARNING)
            }
            SolverTopic.SIMPLE_INTEREST -> {
                val (p, r, t) = when (difficulty) {
                    PracticeDifficulty.EASY -> Triple(1000.0, 10.0, 2.0)
                    PracticeDifficulty.MEDIUM -> Triple(5000.0, 8.0, 3.0)
                    else -> Triple(12000.0, 12.5, 4.0)
                }
                inputs = mapOf("p" to p.toInt().toString(), "r" to r.toString(), "t" to t.toInt().toString())
                solved = MathProblemSolverEngine.solveSimpleInterest(p, r, t, null, null, SolverMode.LEARNING)
            }
            SolverTopic.TIME_AND_WORK -> {
                val (a, b) = when (difficulty) {
                    PracticeDifficulty.EASY -> 6.0 to 12.0
                    PracticeDifficulty.MEDIUM -> 10.0 to 15.0
                    else -> 12.0 to 20.0
                }
                inputs = mapOf("a" to a.toInt().toString(), "b" to b.toInt().toString())
                solved = MathProblemSolverEngine.solveTimeAndWork(
                    MathProblemSolverEngine.WorkType.TWO_PERSONS, a, b, null, SolverMode.LEARNING
                )
            }
            SolverTopic.SPEED_DISTANCE_TIME -> {
                val (dist, time) = when (difficulty) {
                    PracticeDifficulty.EASY -> 120.0 to 3.0
                    PracticeDifficulty.MEDIUM -> 240.0 to 4.0
                    else -> 450.0 to 6.0
                }
                inputs = mapOf("distance" to dist.toInt().toString(), "time" to time.toInt().toString())
                solved = MathProblemSolverEngine.solveSpeedDistanceTime(null, dist, time, SolverMode.LEARNING)
            }
            SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> {
                val (num, div) = when (difficulty) {
                    PracticeDifficulty.EASY -> 246L to 3
                    PracticeDifficulty.MEDIUM -> 7524L to 9
                    else -> 14520L to 11
                }
                inputs = mapOf("num" to num.toString(), "div" to div.toString())
                solved = MathProblemSolverEngine.solveDivisibility(num, div, SolverMode.LEARNING)
            }
            SolverTopic.HCF_LCM -> {
                val list = when (difficulty) {
                    PracticeDifficulty.EASY -> listOf(12L, 18L)
                    PracticeDifficulty.MEDIUM -> listOf(24L, 36L, 60L)
                    else -> listOf(48L, 72L, 108L)
                }
                inputs = mapOf("list" to list.joinToString(", "))
                solved = MathProblemSolverEngine.solveHcfLcm(list, SolverMode.LEARNING)
            }
            SolverTopic.SQUARES_CUBES_ROOTS -> {
                val n = when (difficulty) {
                    PracticeDifficulty.EASY -> 144L
                    PracticeDifficulty.MEDIUM -> 324L
                    else -> 784L
                }
                inputs = mapOf("n" to n.toString(), "op" to "1")
                solved = MathProblemSolverEngine.solveSquareCube(
                    n.toDouble(),
                    MathProblemSolverEngine.SquareCubeOp.SQUARE_ROOT,
                    SolverMode.LEARNING
                )
            }
            SolverTopic.LINEAR_EQUATIONS -> {
                val eq = when (difficulty) {
                    PracticeDifficulty.EASY -> "2x + 4 = 14"
                    PracticeDifficulty.MEDIUM -> "5x - 7 = 18"
                    else -> "7x + 11 = 60"
                }
                inputs = mapOf("eq" to eq)
                solved = MathProblemSolverEngine.solveLinearEquation(eq, SolverMode.LEARNING)
            }
            SolverTopic.QUADRATIC_EQUATIONS -> {
                val (a, b, c) = when (difficulty) {
                    PracticeDifficulty.EASY -> Triple(1.0, -5.0, 6.0)
                    PracticeDifficulty.MEDIUM -> Triple(1.0, -9.0, 20.0)
                    else -> Triple(2.0, -7.0, 3.0)
                }
                inputs = mapOf("a" to a.toInt().toString(), "b" to b.toInt().toString(), "c" to c.toInt().toString())
                solved = MathProblemSolverEngine.solveQuadraticEquation(a, b, c, SolverMode.LEARNING)
            }
            SolverTopic.FACTORIZATION -> {
                val expr = when (difficulty) {
                    PracticeDifficulty.EASY -> "x² - 5x + 6"
                    PracticeDifficulty.MEDIUM -> "x² - 9x + 20"
                    else -> "x² - 16"
                }
                inputs = mapOf("expr" to expr)
                solved = MathProblemSolverEngine.solveFactorization(expr, SolverMode.LEARNING)
            }
            SolverTopic.SIMPLIFICATION -> {
                val expr = when (difficulty) {
                    PracticeDifficulty.EASY -> "√50"
                    PracticeDifficulty.MEDIUM -> "√72"
                    else -> "√128"
                }
                inputs = mapOf("expr" to expr)
                solved = MathProblemSolverEngine.solveSimplification(expr, SolverMode.LEARNING)
            }
        }

        return GeneratedSimilarProblem(
            topic = topic,
            difficulty = difficulty,
            difficultyLabelBn = diffLabelBn,
            problemStatementBn = solved.originalProblemBn,
            prefilledInputs = inputs,
            solution = solved
        )
    }

    private const val STEP_SEP = "\u001E"
    private const val FIELD_SEP = "\u001F"

    fun serializeSteps(steps: List<SolutionStep>): String {
        return steps.joinToString(STEP_SEP) { s ->
            listOf(
                s.stepIndex.toString(),
                s.titleBn.replace(FIELD_SEP, " "),
                s.expressionOrMathBn.replace(FIELD_SEP, " "),
                s.explanationBn.replace(FIELD_SEP, " ")
            ).joinToString(FIELD_SEP)
        }
    }

    fun deserializeSteps(serialized: String): List<SolutionStep> {
        if (serialized.isBlank()) return emptyList()
        return serialized.split(STEP_SEP).mapNotNull { rec ->
            val p = rec.split(FIELD_SEP)
            if (p.size < 4) return@mapNotNull null
            SolutionStep(
                stepIndex = p[0].toIntOrNull() ?: 1,
                titleBn = p[1],
                expressionOrMathBn = p[2],
                explanationBn = p[3]
            )
        }
    }

    fun serializeInputs(inputs: Map<String, String>): String {
        return inputs.entries.joinToString(STEP_SEP) { "${it.key}$FIELD_SEP${it.value}" }
    }

    fun deserializeInputs(serialized: String): Map<String, String> {
        if (serialized.isBlank()) return emptyMap()
        val map = linkedMapOf<String, String>()
        serialized.split(STEP_SEP).forEach { pair ->
            val kv = pair.split(FIELD_SEP)
            if (kv.size == 2) {
                map[kv[0]] = kv[1]
            }
        }
        return map
    }
}

// =========================================================================
// 3. VISUAL EXPLAINER UPGRADE: Mini Knowledge Checks & Deep-Link Mapping
// =========================================================================

data class VisualKnowledgeCheckQuestion(
    val id: String,
    val tabIndex: Int,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val relatedQuizTopic: QuizTopic,
    val relatedSolverTopic: SolverTopic
)

object VisualKnowledgeCheckProvider {

    fun getKnowledgeChecksForTab(tabIndex: Int): List<VisualKnowledgeCheckQuestion> {
        return when (tabIndex) {
            0 -> listOf(
                VisualKnowledgeCheckQuestion(
                    id = "vkc_geom_1",
                    tabIndex = 0,
                    questionBn = "চিন্তা করে বলুন: একটি বৃত্তের ব্যাসার্ধ দ্বিগুণ করলে তার ক্ষেত্রফল (πr²) কত গুণ বৃদ্ধি পাবে?",
                    optionsBn = listOf("২ গুণ", "৪ গুণ", "৮ গুণ", "অপরিবর্তিত থাকবে"),
                    correctIndex = 1,
                    explanationBn = "বৃত্তের ক্ষেত্রফল ব্যাসার্ধের বর্গের সমানুপাতিক (πr²)। ব্যাসার্ধ ২ গুণ হলে ক্ষেত্রফল ২² = ৪ গুণ হয়।",
                    relatedQuizTopic = QuizTopic.GEOMETRY_MENSURATION,
                    relatedSolverTopic = SolverTopic.SQUARES_CUBES_ROOTS
                ),
                VisualKnowledgeCheckQuestion(
                    id = "vkc_geom_2",
                    tabIndex = 0,
                    questionBn = "যদি একটি কোণের মান ৬০° হয়, তবে তার পূরক কোণ (Complementary Angle) কত ডিগ্রি?",
                    optionsBn = listOf("৩০°", "১২০°", "৯০°", "৪৫°"),
                    correctIndex = 0,
                    explanationBn = "দুটি কোণের সমষ্টি ৯০° হলে তারা পরস্পরের পূরক কোণ। তাই ৯০° - ৬০° = ৩০°।",
                    relatedQuizTopic = QuizTopic.GEOMETRY_MENSURATION,
                    relatedSolverTopic = SolverTopic.BASIC_ARITHMETIC
                )
            )
            1 -> listOf(
                VisualKnowledgeCheckQuestion(
                    id = "vkc_alg_1",
                    tabIndex = 1,
                    questionBn = "সমীকরণ ভিজ্যুয়ালাইজারে 2x + 4 = 10 এর উভয় পক্ষ থেকে ৪ বিয়োগ করলে বাম ও ডান পক্ষে কী থাকে?",
                    optionsBn = listOf("2x = 6", "2x = 14", "x = 6", "2x = 4"),
                    correctIndex = 0,
                    explanationBn = "সমীকরণের ভারসাম্য বজায় রাখতে উভয় পক্ষ থেকে ৪ বিয়োগ করলে 2x = 10 - 4 = 6 পাওয়া যায়, откуда x = 3।",
                    relatedQuizTopic = QuizTopic.ALGEBRA,
                    relatedSolverTopic = SolverTopic.LINEAR_EQUATIONS
                ),
                VisualKnowledgeCheckQuestion(
                    id = "vkc_alg_2",
                    tabIndex = 1,
                    questionBn = "(a + b)² এর জ্যামিতিক চিত্রে দুটি বর্গক্ষেত্র (a² ও b²) ছাড়াও আর কী তৈরি হয়?",
                    optionsBn = listOf("সমান ক্ষেত্রফলের ২টি আয়তক্ষেত্র (ab + ab)", "১টি ত্রিভুজ", "৪টি বৃত্ত", "শুধুমাত্র ১টি আয়তক্ষেত্র"),
                    correctIndex = 0,
                    explanationBn = "(a + b)² = a² + 2ab + b²; এখানে 2ab অংশটি দুটি সমান আয়তক্ষেত্র (a × b) দ্বারা গঠিত।",
                    relatedQuizTopic = QuizTopic.ALGEBRA,
                    relatedSolverTopic = SolverTopic.FACTORIZATION
                )
            )
            2 -> listOf(
                VisualKnowledgeCheckQuestion(
                    id = "vkc_frac_1",
                    tabIndex = 2,
                    questionBn = "একটি বৃত্ত বা গ্রিডের ৪ ভাগের ৩ ভাগ (৩/৪) রঙিন করলে শতকরা কত ভাগ রঙিন হবে?",
                    optionsBn = listOf("২৫%", "৫০%", "৭৫%", "৮০%"),
                    correctIndex = 2,
                    explanationBn = "৩/৪ = ০.৭৫ = ৭৫% (১০০ ঘরের গ্রিডে ৭৫টি ঘর পূর্ণ)।",
                    relatedQuizTopic = QuizTopic.ARITHMETIC_PERCENTAGE,
                    relatedSolverTopic = SolverTopic.FRACTIONS_DECIMALS
                )
            )
            else -> listOf(
                VisualKnowledgeCheckQuestion(
                    id = "vkc_pythagoras_1",
                    tabIndex = tabIndex,
                    questionBn = "সমকোণী ত্রিভুজের লম্ব a = ৮ এবং ভূমি b = ৫ হলে অতিভুজের বর্গ (c² = a² + b²) কত হবে?",
                    optionsBn = listOf("৮৯ (৬৪ + ২৫)", "৪১ (১৬ + ২৫)", "৩৯ (৬৪ − ২৫)", "১৩ (৮ + ৫)"),
                    correctIndex = 0,
                    explanationBn = "পিথাগোরাসের উপপাদ্য অনুযায়ী c² = a² + b² = ৮² + ৫² = ৬৪ + ২৫ = ৮৯ এবং অতিভুজ c = √৮৯ ≈ ৯.৪৩ একক।",
                    relatedQuizTopic = QuizTopic.GEOMETRY_MENSURATION,
                    relatedSolverTopic = SolverTopic.SQUARES_CUBES_ROOTS
                ),
                VisualKnowledgeCheckQuestion(
                    id = "vkc_graph_1",
                    tabIndex = tabIndex,
                    questionBn = "y = mx + c সরলরেখার সমীকরণে ঢাল (m) ধনাত্মক হলে রেখাটি কোন দিকে অগ্রসর হয়?",
                    optionsBn = listOf("বাম থেকে ডানে উপরের দিকে উঠে", "বাম থেকে ডানে নিচের দিকে নামে", "আনুভূমিক থাকে", "উল্লম্ব থাকে"),
                    correctIndex = 0,
                    explanationBn = "ঢাল m > 0 হলে x বৃদ্ধির সাথে সাথে y বৃদ্ধি পায়, তাই লেখচিত্রটি বাম থেকে ডানে উপরের দিকে উঠে।",
                    relatedQuizTopic = QuizTopic.ALGEBRA,
                    relatedSolverTopic = SolverTopic.LINEAR_EQUATIONS
                )
            )
        }
    }

    fun getKnowledgeChecksForContext(
        tabIndex: Int,
        selectedFormulaId: String? = null
    ): List<VisualKnowledgeCheckQuestion> {
        val baseList = getKnowledgeChecksForTab(tabIndex)
        if (tabIndex != 3 || selectedFormulaId.isNullOrBlank()) return baseList
        return when (selectedFormulaId) {
            "vis_pythagoras" -> baseList
            "vis_alg_sq_plus", "vis_rect_area" -> getKnowledgeChecksForTab(1).reversed() + baseList
            "vis_circle_area", "vis_circle_prop" -> getKnowledgeChecksForTab(0) + baseList
            else -> baseList
        }
    }

    fun getDiscoveryInvariantBn(
        tabIndex: Int,
        selectedFormulaId: String? = null
    ): String {
        return when (tabIndex) {
            0 -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): যেকোনো সদৃশ জ্যামিতিক আকৃতির বাহু k গুণ বৃদ্ধি পেলে পরিসীমা k গুণ, ক্ষেত্রফল k² গুণ এবং ৩ডি আয়তন k³ গুণ বৃদ্ধি পায়।"
            1 -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): সমীকরণের উভয় পক্ষে একই সংখ্যা যোগ, বিয়োগ বা অশূন্য সংখ্যা দ্বারা গুণ-ভাগ করলে সমতার ভারসাম্য অপরিবর্তিত থাকে।"
            2 -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): সংখ্যারেখায় যেকোনো দুটি মূলদ সংখ্যার মধ্যবিন্দু (a+b)/2 নিজেও একটি মূলদ সংখ্যা (অসীম ঘনত্ব ধর্ম)।"
            3 -> when (selectedFormulaId) {
                "vis_pythagoras" -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): সমকোণী ত্রিভুজের অতিভুজের ওপর অঙ্কিত বর্গক্ষেত্রের ক্ষেত্রফল (c²) অপর দুই বাহুর ওপর অঙ্কিত বর্গক্ষেত্রদ্বয়ের ক্ষেত্রফলের সমষ্টির (a² + b²) সমান।"
                "vis_circle_area", "vis_circle_prop" -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): বৃত্তের ব্যাসার্ধ r গুণ বৃদ্ধি পেলে পরিধি r গুণ এবং ক্ষেত্রফল (πr²) বর্গের অনুপাতে বৃদ্ধি পায়।"
                "vis_cube_vol" -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): ঘনকের ধার a হলে এর ৬টি বর্গাকার তলের মোট ক্ষেত্রফল 6a² এবং ত্রিমাত্রিক আয়তন V = a³ ঘন একক।"
                "vis_simple_interest" -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): সরল মুনাফায় (I = Prn/100) মূলধন ও সুদের হার স্থির থাকলে মুনাফা সময়ের (n) সমানুপাতিক।"
                else -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): (a+b)² বর্গক্ষেত্রটি চারটি অংশে বিভক্ত: একটি a² বর্গ, একটি b² বর্গ এবং দুটি ab আয়তক্ষেত্র।"
            }
            else -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): অয়লারের বহুতলক সূত্র অনুযায়ী যেকোনো উত্তল ঘনবস্তুর তল (F), শীর্ষবিন্দু (V) ও ধারের (E) সম্পর্ক সর্বদা F + V − E = ২।"
        }
    }
}

// =========================================================================
// 4. REAL NUMBERS UPGRADE: Guided Step-by-Step Explorer & Classification Quiz
// =========================================================================

data class GuidedNumberSetStep(
    val stepIndex: Int,
    val symbol: String,
    val titleBn: String,
    val definitionBn: String,
    val subsetRelationshipBn: String,
    val keyPropertiesBn: List<String>,
    val numberLinePoints: List<Pair<Double, String>>,
    val examplesBn: List<String>,
    val nonExamplesBn: List<String>
)

data class NumberClassificationExercise(
    val id: String,
    val numberDisplayBn: String,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val stepByStepReasonBn: String
)

object RealNumberGuidedExplorer {

    val guidedSteps: List<GuidedNumberSetStep> = listOf(
        GuidedNumberSetStep(
            stepIndex = 1,
            symbol = "ℕ",
            titleBn = "স্বাভাবিক সংখ্যা (Natural Numbers)",
            definitionBn = "গণনার কাজে ব্যবহৃত ধনাত্মক পূর্ণসংখ্যাগুলোকে স্বাভাবিক সংখ্যা (ℕ) বলা হয়: ℕ = {১, ২, ৩, ৪, ...}।",
            subsetRelationshipBn = "ℕ ⊂ W ⊂ ℤ ⊂ ℚ ⊂ ℝ (সবচেয়ে ভেতরের মৌলিক সেট)",
            keyPropertiesBn = listOf(
                "ক্ষুদ্রতম স্বাভাবিক সংখ্যা হলো ১ (শূন্য স্বাভাবিক সংখ্যা নয়)।",
                "স্বাভাবিক সংখ্যার সেটে যোগ ও গুণ প্রক্রিয়া আবদ্ধ (Closed)।",
                "কোনো বৃহত্তম স্বাভাবিক সংখ্যা নেই; এটি অসীম।"
            ),
            numberLinePoints = listOf(1.0 to "১", 2.0 to "২", 3.0 to "৩", 4.0 to "৪", 5.0 to "৫"),
            examplesBn = listOf("১", "৭", "২৩", "১০০"),
            nonExamplesBn = listOf("০ (পূর্ণ সংখ্যা W কিন্তু ℕ নয়)", "-৫ (ঋণাত্মক)", "৩/৪ (ভগ্নাংশ)")
        ),
        GuidedNumberSetStep(
            stepIndex = 2,
            symbol = "W",
            titleBn = "অঋণাত্মক পূর্ণসংখ্যা (Whole Numbers)",
            definitionBn = "শূন্য (০) সহ সকল স্বাভাবিক সংখ্যার সেটকে অঋণাত্মক পূর্ণসংখ্যা (W) বলে: W = {০, ১, ২, ৩, ...}।",
            subsetRelationshipBn = "W = {০} ∪ ℕ এবং ℕ ⊂ W ⊂ ℤ",
            keyPropertiesBn = listOf(
                "ক্ষুদ্রতম অঋণাত্মক পূর্ণসংখ্যা হলো ০।",
                "সকল স্বাভাবিক সংখ্যাই W-এর অন্তর্ভুক্ত, কিন্তু ০ স্বাভাবিক সংখ্যা নয়।"
            ),
            numberLinePoints = listOf(0.0 to "০", 1.0 to "১", 2.0 to "২", 3.0 to "৩", 4.0 to "৪"),
            examplesBn = listOf("০", "১", "১৫", "৫০০"),
            nonExamplesBn = listOf("-১ (ঋণাত্মক)", "২.৫ (দশমিক)")
        ),
        GuidedNumberSetStep(
            stepIndex = 3,
            symbol = "ℤ",
            titleBn = "পূর্ণসংখ্যা (Integers)",
            definitionBn = "শূন্যসহ সকল ধনাত্মক ও ঋণাত্মক অখণ্ড সংখ্যার সেটকে পূর্ণসংখ্যা (ℤ) বলে: ℤ = {..., -৩, -২, -১, ০, ১, ২, ৩, ...}।",
            subsetRelationshipBn = "ℕ ⊂ W ⊂ ℤ ⊂ ℚ",
            keyPropertiesBn = listOf(
                "পূর্ণসংখ্যার সেটে যোগ, বিয়োগ ও গুণ প্রক্রিয়া সর্বদা আবদ্ধ।",
                "০ ধনাত্মকও নয়, ঋণাত্মকও নয়—এটি নিরপেক্ষ পূর্ণসংখ্যা।"
            ),
            numberLinePoints = listOf(-3.0 to "-৩", -2.0 to "-২", -1.0 to "-১", 0.0 to "০", 1.0 to "১", 2.0 to "২", 3.0 to "৩"),
            examplesBn = listOf("-২৫", "-১", "০", "৪২"),
            nonExamplesBn = listOf("১/২", "√৩", "৩.১৪")
        ),
        GuidedNumberSetStep(
            stepIndex = 4,
            symbol = "ℚ",
            titleBn = "মূলদ সংখ্যা (Rational Numbers)",
            definitionBn = "যে সংখ্যাকে p/q আকারে প্রকাশ করা যায় (যেখানে p, q ∈ ℤ এবং q ≠ 0) তাকে মূলদ সংখ্যা (ℚ) বলে।",
            subsetRelationshipBn = "ℕ ⊂ W ⊂ ℤ ⊂ ℚ ⊂ ℝ",
            keyPropertiesBn = listOf(
                "সকল পূর্ণসংখ্যা, সসীম দশমিক এবং আবৃত দশমিক সংখ্যা মূলদ সংখ্যা।",
                "পূর্ণবর্গ সংখ্যার বর্গমূল (যেমন √৯ = ৩, √১৬ = ৪) এবং পূর্ণঘন সংখ্যার ঘনমূল (∛৮ = ২) সর্বদা মূলদ।"
            ),
            numberLinePoints = listOf(-1.5 to "-৩/২", -0.5 to "-১/২", 0.0 to "০", 0.5 to "১/২", 1.25 to "৫/৪", 2.0 to "২"),
            examplesBn = listOf("৩/৪", "-৭/৫", "০.৩৩৩... (১/৩)", "√২৫ = ৫"),
            nonExamplesBn = listOf("√২", "√৫", "π")
        ),
        GuidedNumberSetStep(
            stepIndex = 5,
            symbol = "ℚ'",
            titleBn = "অমূলদ সংখ্যা (Irrational Numbers)",
            definitionBn = "যে বাস্তব সংখ্যাকে দুটি পূর্ণসংখ্যার অনুপাত p/q আকারে প্রকাশ করা যায় না তাকে অমূলদ সংখ্যা (ℚ') বলে।",
            subsetRelationshipBn = "ℚ ∩ ℚ' = ∅ (মূলদ ও অমূলদ পরস্পর নিশ্ছেদ সেট)",
            keyPropertiesBn = listOf(
                "অমূলদ সংখ্যার দশমিক প্রকাশ অসীম ও অনাবৃত (Non-terminating, non-repeating)।",
                "পূর্ণবর্গ নয় এমন যেকোনো ধনাত্মক পূর্ণসংখ্যার বর্গমূল (√২, √৩, √৭) অমূলদ।"
            ),
            numberLinePoints = listOf(-1.414 to "-√২", 0.0 to "০", 1.414 to "√২", 1.732 to "√৩", 2.236 to "√৫", 3.1416 to "π"),
            examplesBn = listOf("√২ ≈ ১.৪১৪২...", "√৩ ≈ ১.৭৩২০...", "π ≈ ৩.১৪১৫৯...", "e ≈ ২.৭১৮২৮..."),
            nonExamplesBn = listOf("২২/৭ (মূলদ আসন্ন মান)", "√৪৯ = ৭", "০.২৫")
        ),
        GuidedNumberSetStep(
            stepIndex = 6,
            symbol = "ℝ",
            titleBn = "বাস্তব সংখ্যা (Real Numbers)",
            definitionBn = "সকল মূলদ (ℚ) এবং অমূলদ (ℚ') সংখ্যার সংযোগে গঠিত সেটকে বাস্তব সংখ্যা (ℝ) বলা হয়: ℝ = ℚ ∪ ℚ'।",
            subsetRelationshipBn = "ℕ ⊂ W ⊂ ℤ ⊂ ℚ ⊂ ℝ এবং ℚ' ⊂ ℝ",
            keyPropertiesBn = listOf(
                "সংখ্যা রেখার প্রতিটি বিন্দু একটি অনন্য বাস্তব সংখ্যা নির্দেশ করে।",
                "যেকোনো বাস্তব সংখ্যার বর্গ সর্বদা অঋণাত্মক (x² ≥ 0)।"
            ),
            numberLinePoints = listOf(-2.0 to "-২", -0.75 to "-৩/৪", 0.0 to "০", 1.0 to "১", 1.414 to "√২", 3.1416 to "π"),
            examplesBn = listOf("-৭", "০", "৫/৮", "√২", "π"),
            nonExamplesBn = listOf("√(-১) = i (কাল্পনিক বা জটিল সংখ্যা)")
        )
    )

    val classificationExercises: List<NumberClassificationExercise> = listOf(
        NumberClassificationExercise(
            id = "nce_1",
            numberDisplayBn = "০ (শূন্য)",
            questionBn = "০ (শূন্য) নিচের কোন সেটের অন্তর্ভুক্ত নয়?",
            optionsBn = listOf("স্বাভাবিক সংখ্যা (ℕ)", "অঋণাত্মক পূর্ণসংখ্যা (W)", "পূর্ণসংখ্যা (ℤ)", "মূলদ সংখ্যা (ℚ)"),
            correctIndex = 0,
            stepByStepReasonBn = "স্বাভাবিক সংখ্যা ১ থেকে শুরু হয় (ℕ = {১, ২, ৩...})। তাই ০ স্বাভাবিক সংখ্যা নয়, কিন্তু এটি W, ℤ, ℚ এবং ℝ-এর সদস্য।"
        ),
        NumberClassificationExercise(
            id = "nce_2",
            numberDisplayBn = "√১৬",
            questionBn = "√১৬ সংখ্যাটির সঠিক শ্রেণিবিন্যাস কোনটি?",
            optionsBn = listOf("মূলদ ও স্বাভাবিক সংখ্যা", "অমূলদ সংখ্যা", "ঋণাত্মক পূর্ণসংখ্যা", "কাল্পনিক সংখ্যা"),
            correctIndex = 0,
            stepByStepReasonBn = "১৬ একটি পূর্ণবর্গ সংখ্যা (৪² = ১৬)। সুতরাং √১৬ = ৪ = ৪/১, যা একই সাথে স্বাভাবিক (ℕ), পূর্ণ (ℤ) ও মূলদ (ℚ) সংখ্যা।"
        ),
        NumberClassificationExercise(
            id = "nce_3",
            numberDisplayBn = "π (পাই) বনাম ২২/৭",
            questionBn = "π এবং ২২/৭ সম্পর্কে নিচের কোন তথ্যটি সঠিক?",
            optionsBn = listOf(
                "π অমূলদ কিন্তু ২২/৭ মূলদ সংখ্যা",
                "উভয়ই মূলদ সংখ্যা",
                "উভয়ই অমূলদ সংখ্যা",
                "π মূলদ কিন্তু ২২/৭ অমূলদ"
            ),
            correctIndex = 0,
            stepByStepReasonBn = "π একটি অসীম অনাবৃত দশমিক (৩.১৪১৫৯২৬৫...), তাই অমূলদ। কিন্তু ২২/৭ দুটি পূর্ণসংখ্যার অনুপাত (p/q) হওয়ায় এটি মূলদ।"
        ),
        NumberClassificationExercise(
            id = "nce_4",
            numberDisplayBn = "০.৩৩৩৩...",
            questionBn = "অসীম আবৃত দশমিক সংখ্যা ০.৩৩৩৩... (০.৩̇) কোন ধরনের সংখ্যা?",
            optionsBn = listOf("মূলদ সংখ্যা (ℚ)", "অমূলদ সংখ্যা (ℚ')", "পূর্ণসংখ্যা (ℤ)", "স্বাভাবিক সংখ্যা (ℕ)"),
            correctIndex = 0,
            stepByStepReasonBn = "যেকোনো অসীম আবৃত দশমিক সংখ্যাকে ভগ্নাংশে প্রকাশ করা যায়: ০.৩৩৩... = ৩/৯ = ১/৩। তাই এটি মূলদ সংখ্যা।"
        ),
        NumberClassificationExercise(
            id = "nce_5",
            numberDisplayBn = "√৭",
            questionBn = "√৭ কোন সেটের অন্তর্ভুক্ত?",
            optionsBn = listOf("অমূলদ ও বাস্তব সংখ্যা (ℚ' ⊂ ℝ)", "মূলদ সংখ্যা (ℚ)", "পূর্ণসংখ্যা (ℤ)", "স্বাভাবিক সংখ্যা (ℕ)"),
            correctIndex = 0,
            stepByStepReasonBn = "৭ একটি মৌলিক সংখ্যা যা পূর্ণবর্গ নয়। তাই √৭-কে p/q আকারে প্রকাশ করা যায় না; এটি অমূলদ ও বাস্তব সংখ্যা।"
        )
    )
}
