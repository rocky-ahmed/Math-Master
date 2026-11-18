package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.BengaliNumberFormatter
import com.example.math.InterestCalculationMode
import com.example.math.MathProblemSolverEngine
import com.example.math.NumberSystemProvider
import com.example.math.SharedMathEngine
import com.example.math.SolverAtomicArithmeticEngine
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.ui.theme.MathSpacing
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

data class TopicValidationState(
    val messageBn: String,
    val isError: Boolean = true
)

data class TopicValidationOutcome(
    val canSolve: Boolean,
    val banner: TopicValidationState? = null
)

/**
 * Evaluates explicit solve eligibility (`canSolve`) and live Bengali guidance across all 16 Solver topics.
 */
fun evaluateTopicInputValidation(
    topic: SolverTopic,
    arithmeticExpr: String,
    fractionNum1: String,
    fractionDen1: String,
    fractionOp: String = "+",
    fractionNum2: String,
    fractionDen2: String,
    percentageSubType: Int,
    percentVal1: String,
    percentVal2: String,
    ratioSubType: Int,
    ratioTotal: String,
    ratioR1: String,
    ratioR2: String,
    ratioR3: String = "",
    profitLossSubType: Int,
    plCp: String,
    plSp: String,
    plPercent: String,
    plMp: String,
    averageInput: String,
    interestMode: InterestCalculationMode,
    compoundingFreq: Int = 1,
    siP: String,
    siR: String,
    siT: String,
    siI: String,
    workSubType: Int = 0,
    workTimeA: String,
    workTimeB: String,
    workTimeC: String = "",
    sdtSpeed: String,
    sdtDistance: String,
    sdtTime: String,
    divNumber: String,
    hcfLcmInput: String,
    sqCubeNumber: String,
    sqCubeOp: Int,
    linearEqInput: String,
    quadA: String,
    quadB: String,
    quadC: String,
    quadRelation: String = "=",
    factorInput: String,
    simplifyInput: String
): TopicValidationOutcome {
    fun invalid(messageBn: String, isError: Boolean = true) =
        TopicValidationOutcome(canSolve = false, banner = TopicValidationState(messageBn, isError))

    val valid = TopicValidationOutcome(canSolve = true, banner = null)

    return when (topic) {
        SolverTopic.BASIC_ARITHMETIC -> {
            val trimmed = arithmeticExpr.trim()
            if (trimmed.isEmpty()) {
                invalid("অনুগ্রহ করে একটি গাণিতিক রাশি লিখুন (যেমন: (২৫ + ১৫) × ২)।")
            } else {
                var depth = 0
                var unbalancedParen = false
                for (ch in trimmed) {
                    if (ch == '(') depth++
                    else if (ch == ')') {
                        depth--
                        if (depth < 0) {
                            unbalancedParen = true
                            break
                        }
                    }
                }
                if (unbalancedParen || depth != 0) {
                    invalid("বন্ধনী () সঠিকভাবে মেলেনি। দয়া করে পরীক্ষা করুন।")
                } else {
                    try {
                        SolverAtomicArithmeticEngine.evaluate(trimmed)
                        valid
                    } catch (e: SolverAtomicArithmeticEngine.DomainValidationException) {
                        invalid(e.message ?: "রাশিটি বিশ্লেষণ করা যায়নি।")
                    } catch (e: ArithmeticException) {
                        invalid("গণিত ত্রুটি: ${e.message ?: "কোনো সংখ্যাকে শূন্য (০) দিয়ে ভাগ করা যায় না।"}")
                    } catch (_: Exception) {
                        invalid("রাশিটি সঠিকভাবে বিশ্লেষণ করা যায়নি। সঠিক চিহ্ন (+, -, ×, ÷) ব্যবহার করুন।")
                    }
                }
            }
        }
        SolverTopic.FRACTIONS_DECIMALS -> {
            val n1 = parseBnToLong(fractionNum1)
            val d1 = parseBnToLong(fractionDen1)
            val n2 = parseBnToLong(fractionNum2)
            val d2 = parseBnToLong(fractionDen2)
            when {
                n1 == null || d1 == null || n2 == null || d2 == null ->
                    invalid("সবগুলো লব ও হরের ঘরে বৈধ পূর্ণসংখ্যা লিখুন।")
                n1 == Long.MIN_VALUE || d1 == Long.MIN_VALUE || n2 == Long.MIN_VALUE || d2 == Long.MIN_VALUE ->
                    invalid("সংখ্যার মান সমর্থিত সীমার বাইরে (সংখ্যাধিক্য বা ওভারফ্লো)।")
                d1 == 0L || d2 == 0L ->
                    invalid("ভগ্নাংশের হর শূন্য (০) হতে পারে না।")
                (fractionOp == "/" || fractionOp == "÷") && n2 == 0L ->
                    invalid("শূন্য (০) দ্বারা ভাগ করা সংজ্ঞায়িত নয়।")
                else -> {
                    var sNum1 = n1
                    var sDen1 = d1
                    if (sDen1 < 0L) {
                        sNum1 = -sNum1
                        sDen1 = -sDen1
                    }
                    var sNum2 = n2
                    var sDen2 = d2
                    if (sDen2 < 0L) {
                        sNum2 = -sNum2
                        sDen2 = -sDen2
                    }
                    try {
                        val (resNum, resDen) = when (fractionOp) {
                            "+" -> {
                                val lcm = NumberSystemProvider.lcm(sDen1, sDen2)
                                if (lcm <= 0L) throw ArithmeticException("LCM overflow")
                                val t1 = Math.multiplyExact(sNum1, lcm / sDen1)
                                val t2 = Math.multiplyExact(sNum2, lcm / sDen2)
                                Math.addExact(t1, t2) to lcm
                            }
                            "-" -> {
                                val lcm = NumberSystemProvider.lcm(sDen1, sDen2)
                                if (lcm <= 0L) throw ArithmeticException("LCM overflow")
                                val t1 = Math.multiplyExact(sNum1, lcm / sDen1)
                                val t2 = Math.multiplyExact(sNum2, lcm / sDen2)
                                Math.subtractExact(t1, t2) to lcm
                            }
                            "*", "×" -> Math.multiplyExact(sNum1, sNum2) to Math.multiplyExact(sDen1, sDen2)
                            "/", "÷" -> Math.multiplyExact(sNum1, sDen2) to Math.multiplyExact(sDen1, sNum2)
                            else -> return invalid("অসমর্থিত অপারেটর: $fractionOp")
                        }
                        if (resNum == Long.MIN_VALUE || resDen == Long.MIN_VALUE) {
                            invalid("ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য বা ওভারফ্লো)।")
                        } else {
                            valid
                        }
                    } catch (_: ArithmeticException) {
                        invalid("গণনাটি সমর্থিত গাণিতিক সীমার বাইরে (সংখ্যাধিক্য বা ওভারফ্লো)।")
                    }
                }
            }
        }
        SolverTopic.PERCENTAGE -> {
            val v1 = parseBnToDouble(percentVal1)
            val v2 = parseBnToDouble(percentVal2)
            when {
                v1 == null || v2 == null ->
                    invalid("শতকরা হিসাবের জন্য উভয় ঘরে বৈধ সসীম সংখ্যা লিখুন।")
                percentageSubType == 0 && (!(v1 * v2).isFinite() || !((v1 * v2) / 100.0).isFinite()) ->
                    invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                percentageSubType == 1 && v2 == 0.0 ->
                    invalid("মোট সংখ্যা (Y) শূন্য (০) হতে পারে না।")
                percentageSubType == 1 && (!(v1 / v2).isFinite() || !((v1 / v2) * 100.0).isFinite()) ->
                    invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                percentageSubType == 2 && v1 == 0.0 ->
                    invalid("শতকরা পরিবর্তন বের করতে আদি মান শূন্য হতে পারে না।")
                percentageSubType == 2 && (!(v2 - v1).isFinite() || !((abs(v2 - v1) / v1) * 100.0).isFinite()) ->
                    invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                percentageSubType !in 0..2 && (!(v1 * v2).isFinite() || !(v1 + v2 + (v1 * v2) / 100.0).isFinite()) ->
                    invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                else -> valid
            }
        }
        SolverTopic.RATIO_PROPORTION -> {
            val r1 = parseBnToDouble(ratioR1)
            val r2 = parseBnToDouble(ratioR2)
            val tot = parseBnToDouble(ratioTotal)
            val r3 = parseBnToDouble(ratioR3)
            when (ratioSubType) {
                0 -> when {
                    tot == null || r1 == null || r2 == null ->
                        invalid("বণ্টনযোগ্য মোট পরিমাণ এবং ১ম ও ২য় অনুপাতে বৈধ সসীম সংখ্যা লিখুন।")
                    tot < 0.0 ->
                        invalid("বণ্টনযোগ্য মোট পরিমাণ ঋণাত্মক হতে পারবে না (অবশ্যই ≥ ০ হতে হবে)।")
                    r1 <= 0.0 || r2 <= 0.0 ->
                        invalid("বণ্টনের জন্য অনুপাতের প্রতিটি পদ অবশ্যই ধনাত্মক (> ০) হতে হবে।")
                    ratioR3.isNotBlank() && (r3 == null || r3 <= 0.0) ->
                        invalid("৩য় অনুপাত পদটি বৈধ ধনাত্মক (> ০) সংখ্যা হতে হবে বা ঘরটি ফাঁকা রাখুন।")
                    else -> {
                        val sumRatios = r1 + r2 + (if (ratioR3.isNotBlank()) r3!! else 0.0)
                        val s1 = tot * (r1 / sumRatios)
                        val s2 = tot * (r2 / sumRatios)
                        val s3 = if (ratioR3.isNotBlank()) tot * (r3!! / sumRatios) else 0.0
                        if (!sumRatios.isFinite() || sumRatios <= 0.0 || !s1.isFinite() || !s2.isFinite() || !s3.isFinite()) {
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        } else {
                            valid
                        }
                    }
                }
                1 -> when {
                    r1 == null || r2 == null || tot == null || r1 <= 0.0 || r2 <= 0.0 || tot <= 0.0 ->
                        invalid("চতুর্থ সমানুপাতী নির্ণয়ে A, B ও C অবশ্যই শূন্যের চেয়ে বড় (> ০) হতে হবে।")
                    !(r2 * tot).isFinite() || !((r2 * tot) / r1).isFinite() ->
                        invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                    else -> valid
                }
                else -> when {
                    r1 == null || r2 == null || r1 <= 0.0 || r2 <= 0.0 ->
                        invalid("অনুপাত সরলীকরণের জন্য উভয় পদ অবশ্যই ধনাত্মক (> ০) সংখ্যা হতে হবে।")
                    else -> {
                        var sr1 = r1
                        var sr2 = r2
                        while ((sr1 != kotlin.math.floor(sr1) || sr2 != kotlin.math.floor(sr2)) && sr1 < 1e9 && sr2 < 1e9) {
                            sr1 *= 10.0
                            sr2 *= 10.0
                        }
                        if (!sr1.isFinite() || !sr2.isFinite() || sr1 > 9e15 || sr2 > 9e15 ||
                            round(sr1).toLong() <= 0L || round(sr2).toLong() <= 0L
                        ) {
                            invalid("অনুপাত সরলীকরণের মান সমর্থিত সীমার বাইরে বা অতি ক্ষুদ্র।")
                        } else {
                            valid
                        }
                    }
                }
            }
        }
        SolverTopic.PROFIT_LOSS -> {
            when (profitLossSubType) {
                0 -> {
                    val cp = parseBnToDouble(plCp)
                    val sp = parseBnToDouble(plSp)
                    when {
                        cp == null || sp == null ->
                            invalid("ক্রয়মূল্য ও বিক্রয়মূল্যের বৈধ সসীম মান লিখুন।")
                        cp <= 0.0 || sp < 0.0 ->
                            invalid("ক্রয়মূল্য অবশ্যই ধনাত্মক (> ০) এবং বিক্রয়মূল্য অঋণাত্মক (≥ ০) হতে হবে।")
                        !(sp - cp).isFinite() || !((abs(sp - cp) / cp) * 100.0).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                }
                1 -> {
                    val cp = parseBnToDouble(plCp)
                    val pct = parseBnToDouble(plPercent)
                    when {
                        cp == null || pct == null ->
                            invalid("ক্রয়মূল্য ও লাভ/ক্ষতির হার লিখুন।")
                        cp <= 0.0 || pct < -100.0 ->
                            invalid("বৈধ ধনাত্মক ক্রয়মূল্য (CP > ০) এবং শতকরা হার (≥ -১০০%) প্রদান করুন।")
                        !((abs(pct) / 100.0) * cp).isFinite() || !(cp + (pct / 100.0) * cp).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                }
                2 -> {
                    val sp = parseBnToDouble(plSp)
                    val pct = parseBnToDouble(plPercent)
                    val factor = if (pct != null) 100.0 + pct else null
                    when {
                        sp == null || pct == null ->
                            invalid("বিক্রয়মূল্য ও লাভ/ক্ষতির হার লিখুন।")
                        sp <= 0.0 ->
                            invalid("বৈধ ধনাত্মক বিক্রয়মূল্য (SP > ০) প্রদান করুন।")
                        factor == null || factor <= 0.0 ->
                            invalid("১০০% বা তার বেশি ক্ষতি বাস্তবসম্মত নয়।")
                        !(sp * 100.0).isFinite() || !((sp * 100.0) / factor).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                }
                else -> {
                    val mp = parseBnToDouble(plMp)
                    val pct = parseBnToDouble(plPercent)
                    when {
                        mp == null || pct == null ->
                            invalid("ধার্যমূল্য ও ছাড়ের হার লিখুন।")
                        mp <= 0.0 || pct !in 0.0..100.0 ->
                            invalid("বৈধ ধনাত্মক ধার্যমূল্য (MP > ০) এবং ছাড়ের হার (০% থেকে ১০০%) প্রদান করুন।")
                        !(mp * (pct / 100.0)).isFinite() || !(mp - mp * (pct / 100.0)).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                }
            }
        }
        SolverTopic.AVERAGE -> {
            when (val parsed = parseStrictBnDoubleList(averageInput)) {
                is StrictListParseResult.Invalid -> invalid(parsed.messageBn)
                is StrictListParseResult.Valid -> {
                    val sum = parsed.values.sum()
                    val avg = sum / parsed.values.size
                    if (!sum.isFinite() || !avg.isFinite()) {
                        invalid("সংখ্যাগুলোর সমষ্টি বা গড় সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                    } else {
                        valid
                    }
                }
            }
        }
        SolverTopic.SIMPLE_INTEREST -> {
            val visibleMalformed = if (interestMode == InterestCalculationMode.COMPOUND) {
                listOf(siP, siR, siT).any { it.isNotBlank() && parseBnToDouble(it) == null }
            } else {
                listOf(siP, siR, siT, siI).any { it.isNotBlank() && parseBnToDouble(it) == null }
            }
            if (visibleMalformed) {
                return invalid("মুনাফার হিসাবের সব প্রদত্ত ঘরে বৈধ সসীম সংখ্যা লিখুন।")
            }
            val p = parseBnToDouble(siP)
            val r = parseBnToDouble(siR)
            val t = parseBnToDouble(siT)
            val i = parseBnToDouble(siI)
            if (interestMode == InterestCalculationMode.COMPOUND) {
                when {
                    p == null || r == null || t == null ->
                        invalid("চক্রবৃদ্ধি মুনাফার জন্য আসল (P), হার (r) ও সময় (t) দিন।")
                    p <= 0.0 || r < 0.0 || t < 0.0 ->
                        invalid("আসল (P > ০), মুনাফার হার (r ≥ ০) এবং সময় (t ≥ ০) সসীম বৈধ মান হতে হবে।")
                    compoundingFreq !in 1..365 ->
                        invalid("চক্রবৃদ্ধি পর্ব (m) অবশ্যই ১ থেকে ৩৬৫ এর মধ্যে পূর্ণসংখ্যা হতে হবে।")
                    else -> {
                        val totalPeriods = compoundingFreq.toDouble() * t
                        if (!totalPeriods.isFinite() || totalPeriods > 10_000.0) {
                            invalid("মোট চক্রবৃদ্ধি মেয়াদ (m × t) সমর্থিত সীমা (১০,০০০ পর্ব) অতিক্রম করেছে।")
                        } else {
                            val growth = (1.0 + r / (100.0 * compoundingFreq)).pow(totalPeriods)
                            val amt = p * growth
                            val ci = amt - p
                            val si = (p * r * t) / 100.0
                            if (!growth.isFinite() || !amt.isFinite() || !ci.isFinite() || !si.isFinite()) {
                                invalid("চক্রবৃদ্ধি মুনাফার ফলাফল সমর্থিত গাণিতিক সীমার বাইরে।")
                            } else {
                                valid
                            }
                        }
                    }
                }
            } else {
                val count = listOf(p, r, t, i).count { it != null }
                when {
                    count < 3 ->
                        invalid("সরল মুনাফায় P, r, t, I এর মধ্যে যেকোনো ৩টি মান দিন (৪র্থটি স্বয়ংক্রিয় বের হবে)।", isError = false)
                    count == 4 ->
                        invalid("সব ৪টি মান দেওয়া হয়েছে। গণনার জন্য P, r, t, I এর যেকোনো ১টি ঘর ফাঁকা রাখুন।", isError = true)
                    p != null && r != null && t != null && i == null -> when {
                        p <= 0.0 || r < 0.0 || t <= 0.0 ->
                            invalid("মুনাফা (I) নির্ণয়ে আসল (P > ০), সময় (t > ০) এবং হার (r ≥ ০) বৈধ হতে হবে।")
                        !(p * r * t).isFinite() || !((p * r * t) / 100.0).isFinite() || !(p + (p * r * t) / 100.0).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                    p == null && i != null && r != null && t != null -> when {
                        i <= 0.0 || r <= 0.0 || t <= 0.0 ->
                            invalid("আসল (P) নির্ণয়ে মুনাফা (I > ০), হার (r > ০) ও সময় (t > ০) ধনাত্মক হতে হবে।")
                        !(i * 100.0).isFinite() || !(r * t).isFinite() || !((i * 100.0) / (r * t)).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                    r == null && p != null && i != null && t != null -> when {
                        p <= 0.0 || i < 0.0 || t <= 0.0 ->
                            invalid("মুনাফার হার (r) নির্ণয়ে আসল (P > ০), সময় (t > ০) এবং মুনাফা (I ≥ ০) বৈধ হতে হবে।")
                        !(i * 100.0).isFinite() || !(p * t).isFinite() || !((i * 100.0) / (p * t)).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                    t == null && p != null && i != null && r != null -> when {
                        p <= 0.0 || i < 0.0 || r <= 0.0 ->
                            invalid("সময় (t) নির্ণয়ে আসল (P > ০), হার (r > ০) এবং মুনাফা (I ≥ ০) বৈধ হতে হবে।")
                        !(i * 100.0).isFinite() || !(p * r).isFinite() || !((i * 100.0) / (p * r)).isFinite() ->
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        else -> valid
                    }
                    else -> invalid("P, r, t, I এর মধ্যে ঠিক ৩টি মান দিন এবং ১টি ফাঁকা রাখুন।")
                }
            }
        }
        SolverTopic.TIME_AND_WORK -> {
            val a = parseBnToDouble(workTimeA)
            val b = parseBnToDouble(workTimeB)
            val c = parseBnToDouble(workTimeC)
            when {
                a == null || b == null ->
                    invalid("কাজ বা নলের সময়ের ঘরে বৈধ সসীম সংখ্যা লিখুন।")
                a <= 0.0 || b <= 0.0 ->
                    invalid("সময়ের মান ধনাত্মক (> ০) হতে হবে।")
                workSubType == 0 -> {
                    val res = (a * b) / (a + b)
                    if (!(a * b).isFinite() || !res.isFinite()) {
                        invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                    } else valid
                }
                workSubType == 1 -> when {
                    c == null || c <= 0.0 ->
                        invalid("৩ জনের কাজের জন্য C-এর সময় অবশ্যই সসীম ও ধনাত্মক (> ০) হতে হবে।")
                    else -> {
                        val rate = (1.0 / a) + (1.0 / b) + (1.0 / c)
                        val totalDays = if (rate > 0.0) 1.0 / rate else Double.NaN
                        if (!rate.isFinite() || rate <= 0.0 || !totalDays.isFinite()) {
                            invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                        } else valid
                    }
                }
                else -> when {
                    a >= b ->
                        invalid("খালি করার নলের সময় পূর্ণকারী নলের চেয়ে বড় হতে হবে (পূর্ণকারী < খালি করার নল)।")
                    !(a * b).isFinite() || !((a * b) / (b - a)).isFinite() ->
                        invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                    else -> valid
                }
            }
        }
        SolverTopic.SPEED_DISTANCE_TIME -> {
            if (listOf(sdtSpeed, sdtDistance, sdtTime).any { it.isNotBlank() && parseBnToDouble(it) == null }) {
                return invalid("গতিবেগ, দূরত্ব ও সময়ের প্রদত্ত মানগুলো অবশ্যই বৈধ সসীম সংখ্যা হতে হবে।")
            }
            val s = parseBnToDouble(sdtSpeed)
            val d = parseBnToDouble(sdtDistance)
            val t = parseBnToDouble(sdtTime)
            val provided = listOf(s, d, t).count { it != null }
            when {
                provided < 2 ->
                    invalid("গতিবেগ (S), দূরত্ব (D) ও সময় (T) এর মধ্যে ঠিক ২টি মান দিন (১টি ফাঁকা রাখুন)।", isError = false)
                provided > 2 ->
                    invalid("সব ৩টি মান দেওয়া হয়েছে। গণনার জন্য ঠিক ২টি মান দিন এবং ১টি ফাঁকা রাখুন।", isError = true)
                s != null && s <= 0.0 ->
                    invalid("গতিবেগ (S) অবশ্যই শূন্যের চেয়ে বেশি (> ০) হতে হবে।")
                t != null && t <= 0.0 ->
                    invalid("সময় (T) অবশ্যই শূন্যের চেয়ে বেশি (> ০) হতে হবে।")
                d != null && d < 0.0 ->
                    invalid("দূরত্ব (D) কখনো ঋণাত্মক হতে পারে না (অবশ্যই ≥ ০ হতে হবে)।")
                d != null && t != null && s == null -> {
                    val sKmh = d / t
                    val sMs = sKmh * (5.0 / 18.0)
                    if (!sKmh.isFinite() || !sMs.isFinite()) {
                        invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                    } else valid
                }
                s != null && t != null && d == null -> {
                    if (!(s * t).isFinite()) {
                        invalid("গণনার ফলাফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                    } else valid
                }
                d != null && s != null && t == null -> {
                    val calcT = d / s
                    val rawSec = calcT * 3600.0
                    if (!calcT.isFinite() || !rawSec.isFinite() || rawSec > (Long.MAX_VALUE / 2).toDouble()) {
                        invalid("সময়ের গণনা সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                    } else valid
                }
                else -> invalid("গতিবেগ (S), দূরত্ব (D) ও সময় (T) এর মধ্যে ঠিক ২টি মান দিন।")
            }
        }
        SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> {
            if (parseBnToLong(divNumber) == null) {
                invalid("বিভাজ্যতা যাচাইয়ের জন্য একটি বৈধ পূর্ণসংখ্যা লিখুন।")
            } else {
                valid
            }
        }
        SolverTopic.HCF_LCM -> {
            when (val parsed = parseStrictBnPositiveLongList(hcfLcmInput)) {
                is StrictListParseResult.Invalid -> invalid(parsed.messageBn)
                is StrictListParseResult.Valid -> {
                    val maxLongBig = BigInteger.valueOf(Long.MAX_VALUE)
                    var cumulativeBigLcm = BigInteger.valueOf(parsed.values[0])
                    var overflow = false
                    for (idx in 1 until parsed.values.size) {
                        val nextBig = BigInteger.valueOf(parsed.values[idx])
                        val gBig = cumulativeBigLcm.gcd(nextBig)
                        cumulativeBigLcm = cumulativeBigLcm.divide(gBig).multiply(nextBig)
                        if (cumulativeBigLcm > maxLongBig) {
                            overflow = true
                            break
                        }
                    }
                    if (overflow) {
                        invalid("প্রদত্ত সংখ্যাগুলোর ল.সা.গু গণনা সমর্থিত সীমার বাইরে (সংখ্যাধিক্য বা ওভারফ্লো)।")
                    } else {
                        valid
                    }
                }
            }
        }
        SolverTopic.SQUARES_CUBES_ROOTS -> {
            val n = parseBnToDouble(sqCubeNumber)
            when {
                n == null -> invalid("একটি বৈধ সসীম সংখ্যা লিখুন।")
                sqCubeOp == 0 && !(n * n).isFinite() ->
                    invalid("বর্গফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য বা ওভারফ্লো)।")
                sqCubeOp == 1 && n < 0.0 ->
                    invalid("বাস্তব বর্গমূলের জন্য ঋণাত্মক নয় এমন সংখ্যা (≥ ০) দিন।")
                sqCubeOp == 2 && !(n * n * n).isFinite() ->
                    invalid("ঘনফল সমর্থিত সীমার বাইরে (সংখ্যাধিক্য বা ওভারফ্লো)।")
                else -> valid
            }
        }
        SolverTopic.LINEAR_EQUATIONS -> {
            val eq = linearEqInput.trim()
            when {
                eq.isEmpty() -> invalid("একটি সরল সমীকরণ বা অসমতা লিখুন (যেমন: 3x + 12 = 45)।")
                !eq.any { it in listOf('=', '<', '>', '≤', '≥') } ->
                    invalid("সমীকরণ বা অসমতা চিহ্ন (=, <, >, ≤, ≥) যুক্ত করুন।")
                else -> {
                    val res = MathProblemSolverEngine.solveLinearEquation(eq, SolverMode.QUICK)
                    if (res.isSuccess) valid else invalid(res.errorMessageBn ?: "সমীকরণটি সমাধান করা যায়নি।")
                }
            }
        }
        SolverTopic.QUADRATIC_EQUATIONS -> {
            val a = parseBnToDouble(quadA)
            val b = parseBnToDouble(quadB)
            val c = parseBnToDouble(quadC)
            val disc = if (a != null && b != null && c != null) (b * b - 4.0 * a * c) else Double.NaN
            when {
                a == null || b == null || c == null ->
                    invalid("সহগ a, b, c-এর ঘরে বৈধ সসীম সংখ্যা লিখুন।")
                abs(a) < 1e-12 ->
                    invalid("দ্বিঘাত সমীকরণে x²-এর সহগ (a) শূন্য হতে পারে না।")
                quadRelation.trim() !in listOf("=", "<", "≤", "<=", ">", "≥", ">=") ->
                    invalid("অসমর্থিত সম্পর্ক চিহ্ন: $quadRelation")
                !disc.isFinite() ->
                    invalid("নিশ্চায়কের মান সমর্থিত সীমার বাইরে (সংখ্যাধিক্য)।")
                quadRelation.trim() == "=" && (!(-b / (2.0 * a)).isFinite() || !((4.0 * a * c - b * b) / (4.0 * a)).isFinite()) ->
                    invalid("শীর্ষবিন্দু বা গণনার ফলাফল সমর্থিত সীমার বাইরে।")
                else -> valid
            }
        }
        SolverTopic.FACTORIZATION -> {
            if (factorInput.isBlank()) {
                invalid("উৎপাদকে বিশ্লেষণের জন্য একটি সংখ্যা বা বীজগণিতীয় রাশি লিখুন।")
            } else {
                val res = SharedMathEngine.factorize(factorInput)
                if (res.isSuccess) valid else invalid(res.errorMessageBn ?: "প্রদত্ত রাশিটির উৎপাদকে বিশ্লেষণ সম্ভব হয়নি।")
            }
        }
        SolverTopic.SIMPLIFICATION -> {
            if (simplifyInput.isBlank()) {
                invalid("সরলীকরণের জন্য করণী, ভগ্নাংশ বা বীজগণিতীয় রাশি লিখুন।")
            } else {
                val res = SharedMathEngine.simplify(simplifyInput)
                if (res.isSuccess) valid else invalid(res.errorMessageBn ?: "রাশিটি সরলীকরণ করা সম্ভব হয়নি।")
            }
        }
    }
}

/**
 * Computes live, non-blocking topic-aware input validation guidance for the active topic.
 */
fun computeTopicInputValidation(
    topic: SolverTopic,
    arithmeticExpr: String,
    fractionNum1: String,
    fractionDen1: String,
    fractionOp: String = "+",
    fractionNum2: String,
    fractionDen2: String,
    percentageSubType: Int,
    percentVal1: String,
    percentVal2: String,
    ratioSubType: Int,
    ratioTotal: String,
    ratioR1: String,
    ratioR2: String,
    ratioR3: String = "",
    profitLossSubType: Int,
    plCp: String,
    plSp: String,
    plPercent: String,
    plMp: String,
    averageInput: String,
    interestMode: InterestCalculationMode,
    compoundingFreq: Int = 1,
    siP: String,
    siR: String,
    siT: String,
    siI: String,
    workSubType: Int = 0,
    workTimeA: String,
    workTimeB: String,
    workTimeC: String = "",
    sdtSpeed: String,
    sdtDistance: String,
    sdtTime: String,
    divNumber: String,
    hcfLcmInput: String,
    sqCubeNumber: String,
    sqCubeOp: Int,
    linearEqInput: String,
    quadA: String,
    quadB: String,
    quadC: String,
    quadRelation: String = "=",
    factorInput: String,
    simplifyInput: String
): TopicValidationState? = evaluateTopicInputValidation(
    topic = topic,
    arithmeticExpr = arithmeticExpr,
    fractionNum1 = fractionNum1,
    fractionDen1 = fractionDen1,
    fractionOp = fractionOp,
    fractionNum2 = fractionNum2,
    fractionDen2 = fractionDen2,
    percentageSubType = percentageSubType,
    percentVal1 = percentVal1,
    percentVal2 = percentVal2,
    ratioSubType = ratioSubType,
    ratioTotal = ratioTotal,
    ratioR1 = ratioR1,
    ratioR2 = ratioR2,
    ratioR3 = ratioR3,
    profitLossSubType = profitLossSubType,
    plCp = plCp,
    plSp = plSp,
    plPercent = plPercent,
    plMp = plMp,
    averageInput = averageInput,
    interestMode = interestMode,
    compoundingFreq = compoundingFreq,
    siP = siP,
    siR = siR,
    siT = siT,
    siI = siI,
    workSubType = workSubType,
    workTimeA = workTimeA,
    workTimeB = workTimeB,
    workTimeC = workTimeC,
    sdtSpeed = sdtSpeed,
    sdtDistance = sdtDistance,
    sdtTime = sdtTime,
    divNumber = divNumber,
    hcfLcmInput = hcfLcmInput,
    sqCubeNumber = sqCubeNumber,
    sqCubeOp = sqCubeOp,
    linearEqInput = linearEqInput,
    quadA = quadA,
    quadB = quadB,
    quadC = quadC,
    quadRelation = quadRelation,
    factorInput = factorInput,
    simplifyInput = simplifyInput
).banner

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicInputCard(
    topic: SolverTopic,
    // Arithmetic
    arithmeticExpr: String,
    onArithmeticChange: (String) -> Unit,
    // Fraction
    fractionNum1: String,
    fractionDen1: String,
    fractionOp: String,
    fractionNum2: String,
    fractionDen2: String,
    onFractionChange: (String, String, String, String, String) -> Unit,
    // Percentage
    percentageSubType: Int,
    percentVal1: String,
    percentVal2: String,
    onPercentageChange: (Int, String, String) -> Unit,
    // Ratio
    ratioSubType: Int,
    ratioTotal: String,
    ratioR1: String,
    ratioR2: String,
    ratioR3: String,
    onRatioChange: (Int, String, String, String, String) -> Unit,
    // Profit & Loss
    profitLossSubType: Int,
    plCp: String,
    plSp: String,
    plPercent: String,
    plMp: String,
    onProfitLossChange: (Int, String, String, String, String) -> Unit,
    // Average
    averageInput: String,
    onAverageChange: (String) -> Unit,
    // Simple & Compound Interest
    interestMode: InterestCalculationMode = InterestCalculationMode.SIMPLE,
    compoundingFreq: Int = 1,
    onInterestModeChange: (InterestCalculationMode, Int) -> Unit = { _, _ -> },
    siP: String,
    siR: String,
    siT: String,
    siI: String,
    onInterestChange: (String, String, String, String) -> Unit,
    // Time and Work
    workSubType: Int,
    workTimeA: String,
    workTimeB: String,
    workTimeC: String,
    onWorkChange: (Int, String, String, String) -> Unit,
    // Speed Distance Time
    sdtSpeed: String,
    sdtDistance: String,
    sdtTime: String,
    onSdtChange: (String, String, String) -> Unit,
    // Divisibility
    divNumber: String,
    divDivisor: Int,
    onDivChange: (String, Int) -> Unit,
    // HCF & LCM
    hcfLcmInput: String,
    onHcfLcmChange: (String) -> Unit,
    // Squares & Cubes
    sqCubeNumber: String,
    sqCubeOp: Int,
    onSqCubeChange: (String, Int) -> Unit,
    // Linear Equations
    linearEqInput: String,
    onLinearEqChange: (String) -> Unit,
    // Quadratic Equations
    quadA: String = "১",
    quadB: String = "-৫",
    quadC: String = "৬",
    quadRelation: String = "=",
    onQuadChange: (String, String, String) -> Unit = { _, _, _ -> },
    onQuadRelationChange: (String) -> Unit = {},
    // Factorization
    factorInput: String = "x² - 5x + 6",
    onFactorChange: (String) -> Unit = {},
    // Simplification
    simplifyInput: String = "√72",
    onSimplifyChange: (String) -> Unit = {},
    // Actions
    isSolveInFlight: Boolean = false,
    onResetClick: () -> Unit = {},
    onSolveClick: () -> Unit
) {
    val validationOutcome = evaluateTopicInputValidation(
        topic = topic,
        arithmeticExpr = arithmeticExpr,
        fractionNum1 = fractionNum1,
        fractionDen1 = fractionDen1,
        fractionOp = fractionOp,
        fractionNum2 = fractionNum2,
        fractionDen2 = fractionDen2,
        percentageSubType = percentageSubType,
        percentVal1 = percentVal1,
        percentVal2 = percentVal2,
        ratioSubType = ratioSubType,
        ratioTotal = ratioTotal,
        ratioR1 = ratioR1,
        ratioR2 = ratioR2,
        ratioR3 = ratioR3,
        profitLossSubType = profitLossSubType,
        plCp = plCp,
        plSp = plSp,
        plPercent = plPercent,
        plMp = plMp,
        averageInput = averageInput,
        interestMode = interestMode,
        compoundingFreq = compoundingFreq,
        siP = siP,
        siR = siR,
        siT = siT,
        siI = siI,
        workSubType = workSubType,
        workTimeA = workTimeA,
        workTimeB = workTimeB,
        workTimeC = workTimeC,
        sdtSpeed = sdtSpeed,
        sdtDistance = sdtDistance,
        sdtTime = sdtTime,
        divNumber = divNumber,
        hcfLcmInput = hcfLcmInput,
        sqCubeNumber = sqCubeNumber,
        sqCubeOp = sqCubeOp,
        linearEqInput = linearEqInput,
        quadA = quadA,
        quadB = quadB,
        quadC = quadC,
        quadRelation = quadRelation,
        factorInput = factorInput,
        simplifyInput = simplifyInput
    )
    val validationState = validationOutcome.banner

    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, topic.primaryColor.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_input_card")
    ) {
        Column(modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard)) {
            val fontScale = LocalDensity.current.fontScale
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val stackHeaderActions = maxWidth < 310.dp || (maxWidth < 340.dp && fontScale >= 1.3f)
                if (stackHeaderActions) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(topic.primaryColor.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = topic.getIcon(),
                                    contentDescription = null,
                                    tint = topic.primaryColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.titleBn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = topic.descriptionBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = onResetClick,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                                    .testTag("btn_reset_solver_input")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "ইনপুট মুছুন",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "মুছুন",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(topic.primaryColor.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = topic.getIcon(),
                                    contentDescription = null,
                                    tint = topic.primaryColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.titleBn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = topic.descriptionBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = onResetClick,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .heightIn(min = SolverDesignTokens.minTouchTarget)
                                .testTag("btn_reset_solver_input")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "ইনপুট মুছুন",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "মুছুন",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (topic) {
                SolverTopic.BASIC_ARITHMETIC -> {
                    OutlinedTextField(
                        value = arithmeticExpr,
                        onValueChange = onArithmeticChange,
                        label = { Text("গাণিতিক রাশি লিখুন (BODMAS)") },
                        placeholder = { Text("যেমন: (২৫ + ১৫) × ২ - ৩০ ÷ ৩") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("arithmetic_input_field"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "নমুনা রাশি (১-ট্যাপে ব্যবহার করুন):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "(১৫ + ৫) × ৪ ÷ ২ - ৬",
                            "৫০ - (১০ + ৫) × ২ + ১২",
                            "(৪০ ÷ ৮) × ৭ + (১৫ - ৯)",
                            "১০০ - ৪ × (১২ + ৩) + ৮"
                        ).forEach { sample ->
                            OutlinedButton(
                                onClick = { onArithmeticChange(sample) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            ) {
                                Text(sample, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                SolverTopic.FRACTIONS_DECIMALS -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = fractionNum1,
                                onValueChange = { onFractionChange(it, fractionDen1, fractionOp, fractionNum2, fractionDen2) },
                                label = { Text("১ম লব") },
                                modifier = Modifier.testTag("fraction_num1"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = fractionDen1,
                                onValueChange = { onFractionChange(fractionNum1, it, fractionOp, fractionNum2, fractionDen2) },
                                label = { Text("১ম হর") },
                                modifier = Modifier.testTag("fraction_den1"),
                                singleLine = true
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "অপারেটর",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            listOf(listOf("+", "-"), listOf("*", "/")).forEach { opRow ->
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    opRow.forEach { op ->
                                        val isSelected = fractionOp == op
                                        val opSymbol = when (op) {
                                            "*" -> "×"
                                            "/" -> "÷"
                                            else -> op
                                        }
                                        val opDesc = when (op) {
                                            "+" -> "যোগ অপারেটর"
                                            "-" -> "বিয়োগ অপারেটর"
                                            "*" -> "গুণ অপারেটর"
                                            else -> "ভাগ অপারেটর"
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) topic.primaryColor else MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.7f),
                                            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier
                                                .size(SolverDesignTokens.minTouchTarget)
                                                .selectable(
                                                    selected = isSelected,
                                                    role = Role.RadioButton,
                                                    onClick = {
                                                        onFractionChange(fractionNum1, fractionDen1, op, fractionNum2, fractionDen2)
                                                    }
                                                )
                                                .semantics {
                                                    contentDescription = opDesc
                                                    stateDescription = if (isSelected) "নির্বাচিত" else "নির্বাচিত নয়"
                                                }
                                                .testTag("fraction_op_$op")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = opSymbol,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = fractionNum2,
                                onValueChange = { onFractionChange(fractionNum1, fractionDen1, fractionOp, it, fractionDen2) },
                                label = { Text("২য় লব") },
                                modifier = Modifier.testTag("fraction_num2"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = fractionDen2,
                                onValueChange = { onFractionChange(fractionNum1, fractionDen1, fractionOp, fractionNum2, it) },
                                label = { Text("২য় হর") },
                                modifier = Modifier.testTag("fraction_den2"),
                                singleLine = true
                            )
                        }
                    }
                }
                SolverTopic.PERCENTAGE -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("X-এর P% কত?", "X, Y-এর কত %?", "শতকরা বৃদ্ধি/হ্রাস", "ক্রমাগত % পরিবর্তন").forEachIndexed { index, label ->
                            FilterChip(
                                selected = percentageSubType == index,
                                onClick = { onPercentageChange(index, percentVal1, percentVal2) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = percentVal1,
                            onValueChange = { onPercentageChange(percentageSubType, it, percentVal2) },
                            label = {
                                Text(
                                    when (percentageSubType) {
                                        0 -> "শতকরা হার (%)"
                                        1 -> "অংশ মান (X)"
                                        2 -> "আদি মান"
                                        else -> "১ম পরিবর্তন (%)"
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = percentVal2,
                            onValueChange = { onPercentageChange(percentageSubType, percentVal1, it) },
                            label = {
                                Text(
                                    when (percentageSubType) {
                                        0 -> "মূল সংখ্যা"
                                        1 -> "মোট সংখ্যা (Y)"
                                        2 -> "পরিবর্তিত মান"
                                        else -> "২য় পরিবর্তন (%)"
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                SolverTopic.RATIO_PROPORTION -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("পরিমাণ বণ্টন", "৪র্থ সমানুপাতী", "অনুপাত সরলীকরণ").forEachIndexed { index, label ->
                            FilterChip(
                                selected = ratioSubType == index,
                                onClick = { onRatioChange(index, ratioTotal, ratioR1, ratioR2, ratioR3) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                                    .testTag("ratio_subtype_$index")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (ratioSubType == 0) {
                        OutlinedTextField(
                            value = ratioTotal,
                            onValueChange = { onRatioChange(ratioSubType, it, ratioR1, ratioR2, ratioR3) },
                            label = { Text("বণ্টনযোগ্য মোট পরিমাণ") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ratio_input_total"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = ratioR1,
                                onValueChange = { onRatioChange(ratioSubType, ratioTotal, it, ratioR2, ratioR3) },
                                label = { Text("১ম অনুপাত") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_r1"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ratioR2,
                                onValueChange = { onRatioChange(ratioSubType, ratioTotal, ratioR1, it, ratioR3) },
                                label = { Text("২য় অনুপাত") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_r2"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ratioR3,
                                onValueChange = { onRatioChange(ratioSubType, ratioTotal, ratioR1, ratioR2, it) },
                                label = { Text("৩য় (ঐচ্ছিক)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_r3"),
                                singleLine = true
                            )
                        }
                    } else if (ratioSubType == 1) {
                        Text(
                            text = "A : B = C : ? (চতুর্থ রাশি নির্ণয়)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = ratioR1,
                                onValueChange = { onRatioChange(ratioSubType, ratioTotal, it, ratioR2, ratioR3) },
                                label = { Text("A") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_r1"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ratioR2,
                                onValueChange = { onRatioChange(ratioSubType, ratioTotal, ratioR1, it, ratioR3) },
                                label = { Text("B") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_r2"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ratioTotal,
                                onValueChange = { onRatioChange(ratioSubType, it, ratioR1, ratioR2, ratioR3) },
                                label = { Text("C") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_total"),
                                singleLine = true
                            )
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = ratioR1,
                                onValueChange = { onRatioChange(ratioSubType, ratioTotal, it, ratioR2, ratioR3) },
                                label = { Text("১ম পদ") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_r1"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ratioR2,
                                onValueChange = { onRatioChange(ratioSubType, ratioTotal, ratioR1, it, ratioR3) },
                                label = { Text("২য় পদ") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ratio_input_r2"),
                                singleLine = true
                            )
                        }
                    }
                }
                SolverTopic.PROFIT_LOSS -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("লাভ/ক্ষতি %", "বিক্রয়মূল্য নির্ণয়", "ক্রয়মূল্য নির্ণয়", "ধার্যমূল্য ও ছাড়").forEachIndexed { index, label ->
                            FilterChip(
                                selected = profitLossSubType == index,
                                onClick = { onProfitLossChange(index, plCp, plSp, plPercent, plMp) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                                    .testTag("pl_subtype_$index")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when (profitLossSubType) {
                        0 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = plCp,
                                onValueChange = { onProfitLossChange(profitLossSubType, it, plSp, plPercent, plMp) },
                                label = { Text("ক্রয়মূল্য (টাকা)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_cp"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = plSp,
                                onValueChange = { onProfitLossChange(profitLossSubType, plCp, it, plPercent, plMp) },
                                label = { Text("বিক্রয়মূল্য (টাকা)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_sp"),
                                singleLine = true
                            )
                        }
                        1 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = plCp,
                                onValueChange = { onProfitLossChange(profitLossSubType, it, plSp, plPercent, plMp) },
                                label = { Text("ক্রয়মূল্য") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_cp"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = plPercent,
                                onValueChange = { onProfitLossChange(profitLossSubType, plCp, plSp, it, plMp) },
                                label = { Text("লাভ/ক্ষতি % (- হলে ক্ষতি)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_percent"),
                                singleLine = true
                            )
                        }
                        2 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = plSp,
                                onValueChange = { onProfitLossChange(profitLossSubType, plCp, it, plPercent, plMp) },
                                label = { Text("বিক্রয়মূল্য") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_sp"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = plPercent,
                                onValueChange = { onProfitLossChange(profitLossSubType, plCp, plSp, it, plMp) },
                                label = { Text("লাভ/ক্ষতি % (- হলে ক্ষতি)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_percent"),
                                singleLine = true
                            )
                        }
                        else -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = plMp,
                                onValueChange = { onProfitLossChange(profitLossSubType, plCp, plSp, plPercent, it) },
                                label = { Text("ধার্যমূল্য (টাকা)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_mp"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = plPercent,
                                onValueChange = { onProfitLossChange(profitLossSubType, plCp, plSp, it, plMp) },
                                label = { Text("ছাড়ের হার (%)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pl_input_percent"),
                                singleLine = true
                            )
                        }
                    }
                }
                SolverTopic.AVERAGE -> {
                    OutlinedTextField(
                        value = averageInput,
                        onValueChange = onAverageChange,
                        label = { Text("সংখ্যার তালিকা (কমা দিয়ে লিখুন)") },
                        placeholder = { Text("যেমন: ১২, ১৮, ২৪, ৩০, ৩৬") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("average_input_field"),
                        singleLine = true
                    )
                }
                SolverTopic.SIMPLE_INTEREST -> {
                    CompactSegmentedSwitch(
                        options = InterestCalculationMode.entries,
                        selectedOption = interestMode,
                        onOptionSelected = { onInterestModeChange(it, compoundingFreq) },
                        labelProvider = { m -> if (m == InterestCalculationMode.SIMPLE) "সরল মুনাফা" else "চক্রবৃদ্ধি মুনাফা" },
                        subtitleProvider = { m -> if (m == InterestCalculationMode.SIMPLE) "I = Prn" else "A = P(1+r/m)^mt" },
                        testTagProvider = { m -> "interest_mode_${m.id}" },
                        accentColor = topic.primaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (interestMode == InterestCalculationMode.COMPOUND) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(1 to "বার্ষিক (m=১)", 2 to "অর্ধবার্ষিক (m=২)", 4 to "ত্রৈমাসিক (m=৪)").forEach { (freq, lbl) ->
                                FilterChip(
                                    selected = compoundingFreq == freq,
                                    onClick = { onInterestModeChange(interestMode, freq) },
                                    label = { Text(lbl, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    } else {
                        Text(
                            text = "P, r, t, I এর মধ্যে যেকোনো ৩টি রাশির মান দিন (বাকিটি স্বয়ংক্রিয় বের হবে):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = siP,
                            onValueChange = { onInterestChange(it, siR, siT, siI) },
                            label = { Text("আসল (P)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("si_input_p"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = siR,
                            onValueChange = { onInterestChange(siP, it, siT, siI) },
                            label = { Text("হার % (r)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("si_input_r"),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = siT,
                            onValueChange = { onInterestChange(siP, siR, it, siI) },
                            label = { Text("সময় বছর (t)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("si_input_t"),
                            singleLine = true
                        )
                        if (interestMode == InterestCalculationMode.SIMPLE) {
                            OutlinedTextField(
                                value = siI,
                                onValueChange = { onInterestChange(siP, siR, siT, it) },
                                label = { Text("মুনাফা (I)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("si_input_i"),
                                singleLine = true
                            )
                        }
                    }
                }
                SolverTopic.TIME_AND_WORK -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("A ও B এর কাজ", "A, B ও C এর কাজ", "চৌবাচ্চা ও পাইপ").forEachIndexed { index, label ->
                            FilterChip(
                                selected = workSubType == index,
                                onClick = { onWorkChange(index, workTimeA, workTimeB, workTimeC) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                                    .testTag("work_subtype_$index")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = workTimeA,
                            onValueChange = { onWorkChange(workSubType, it, workTimeB, workTimeC) },
                            label = { Text(if (workSubType == 2) "পূর্ণকারী নল (ঘন্টা)" else "A এর সময় (দিন)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("work_input_a"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = workTimeB,
                            onValueChange = { onWorkChange(workSubType, workTimeA, it, workTimeC) },
                            label = { Text(if (workSubType == 2) "খালি করার নল (ঘন্টা)" else "B এর সময় (দিন)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("work_input_b"),
                            singleLine = true
                        )
                        if (workSubType == 1) {
                            OutlinedTextField(
                                value = workTimeC,
                                onValueChange = { onWorkChange(workSubType, workTimeA, workTimeB, it) },
                                label = { Text("C এর সময় (দিন)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("work_input_c"),
                                singleLine = true
                            )
                        }
                    }
                }
                SolverTopic.SPEED_DISTANCE_TIME -> {
                    Text(
                        text = "গতিবেগ (S), দূরত্ব (D) ও সময় (T) এর যেকোনো ২টি রাশির মান দিন:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = sdtSpeed,
                            onValueChange = { onSdtChange(it, sdtDistance, sdtTime) },
                            label = { Text("গতিবেগ (km/h)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sdt_input_speed"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sdtDistance,
                            onValueChange = { onSdtChange(sdtSpeed, it, sdtTime) },
                            label = { Text("দূরত্ব (km)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sdt_input_distance"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sdtTime,
                            onValueChange = { onSdtChange(sdtSpeed, sdtDistance, it) },
                            label = { Text("সময় (ঘন্টা)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sdt_input_time"),
                            singleLine = true
                        )
                    }
                }
                SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> {
                    OutlinedTextField(
                        value = divNumber,
                        onValueChange = { onDivChange(it, divDivisor) },
                        label = { Text("পরীক্ষার সংখ্যা") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ভাজক নির্বাচন করুন:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).forEach { d ->
                            FilterChip(
                                selected = divDivisor == d,
                                onClick = { onDivChange(divNumber, d) },
                                label = {
                                    Text(
                                        text = BengaliNumberFormatter.toBn(d),
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            )
                        }
                    }
                }
                SolverTopic.HCF_LCM -> {
                    OutlinedTextField(
                        value = hcfLcmInput,
                        onValueChange = onHcfLcmChange,
                        label = { Text("সংখ্যাদ্বয় বা ত্রয়ী (কমা দিয়ে লিখুন)") },
                        placeholder = { Text("যেমন: ২৪, ৩৬ বা ১৮, ৩০, ৪২") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hcf_lcm_input_field"),
                        singleLine = true
                    )
                }
                SolverTopic.SQUARES_CUBES_ROOTS -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("বর্গ (N²)", "বর্গমূল (√N)", "ঘন (N³)", "ঘনমূল (³√N)").forEachIndexed { index, label ->
                            FilterChip(
                                selected = sqCubeOp == index,
                                onClick = { onSqCubeChange(sqCubeNumber, index) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sqCubeNumber,
                        onValueChange = { onSqCubeChange(it, sqCubeOp) },
                        label = { Text("সংখ্যা লিখুন") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                SolverTopic.LINEAR_EQUATIONS -> {
                    OutlinedTextField(
                        value = linearEqInput,
                        onValueChange = onLinearEqChange,
                        label = { Text("সরল সমীকরণ বা অসমতা (যেমন: 3x+12=45, |2x-4|≤6)") },
                        placeholder = { Text("যেমন: 3x + 12 = 45 বা |2x - 4| ≤ 6") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("linear_eq_input_field"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "নমুনা সমীকরণ ও অসমতা:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("3x + 12 = 45", "5x - 8 = 2x + 16", "2x - 3 < 7", "|2x - 4| ≤ 6").forEach { sample ->
                            OutlinedButton(
                                onClick = { onLinearEqChange(sample) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            ) {
                                Text(sample, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                SolverTopic.QUADRATIC_EQUATIONS -> {
                    Text(
                        text = "দ্বিঘাত সমীকরণ বা অসমতা ax² + bx + c $quadRelation 0 এর সহগ দিন:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("=", "<", "≤", ">", "≥").forEach { rel ->
                            FilterChip(
                                selected = quadRelation == rel,
                                onClick = { onQuadRelationChange(rel) },
                                label = { Text("ax²+bx+c $rel 0", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quadA,
                            onValueChange = { onQuadChange(it, quadB, quadC) },
                            label = { Text("a (x² সহগ)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quad_input_a"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = quadB,
                            onValueChange = { onQuadChange(quadA, it, quadC) },
                            label = { Text("b (x সহগ)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quad_input_b"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = quadC,
                            onValueChange = { onQuadChange(quadA, quadB, it) },
                            label = { Text("c (ধ্রুবক)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quad_input_c"),
                            singleLine = true
                        )
                    }
                }
                SolverTopic.FACTORIZATION -> {
                    OutlinedTextField(
                        value = factorInput,
                        onValueChange = onFactorChange,
                        label = { Text("রাশি বা সংখ্যা (যেমন: x² - 5x + 6 বা 360)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("factorization_input_field"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("x² - 5x + 6", "x² - 9", "2x² + 7x + 3", "360").forEach { sample ->
                            OutlinedButton(
                                onClick = { onFactorChange(sample) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            ) {
                                Text(sample, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                SolverTopic.SIMPLIFICATION -> {
                    OutlinedTextField(
                        value = simplifyInput,
                        onValueChange = onSimplifyChange,
                        label = { Text("রাশি, করণী বা ভগ্নাংশ (যেমন: √72, 24/36, 3x + 2y + 5x)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simplification_input_field"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("√72", "45/60", "3x + 5y + 4x - 2y", "(24 + 12) ÷ 6").forEach { sample ->
                            OutlinedButton(
                                onClick = { onSimplifyChange(sample) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            ) {
                                Text(sample, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            if (validationState != null) {
                Spacer(modifier = Modifier.height(10.dp))
                SolverInputValidationBanner(
                    messageBn = validationState.messageBn,
                    isError = validationState.isError
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSolveClick,
                enabled = validationOutcome.canSolve && !isSolveInFlight,
                colors = ButtonDefaults.buttonColors(containerColor = topic.primaryColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("solve_problem_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "সমাধান করুন",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "সমাধান করুন",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
