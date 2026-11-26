package com.example

import com.example.math.InterestCalculationMode
import com.example.math.MathProblemSolverEngine
import com.example.math.NumberSystemProvider
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.ui.screens.StrictListParseResult
import com.example.ui.screens.evaluateTopicInputValidation
import com.example.ui.screens.parseBnToDouble
import com.example.ui.screens.parseBnToLong
import com.example.ui.screens.parseCompoundingFrequency
import com.example.ui.screens.parseStrictBnDoubleList
import com.example.ui.screens.parseStrictBnPositiveLongList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Phase 1 comprehensive validation, calculation integrity, and cross-layer parity tests
 * covering all 16 canonical SolverTopics and their subtypes.
 */
class Phase1ValidationAndIntegrityUnitTest {

    private fun validate(
        topic: SolverTopic,
        arithmeticExpr: String = "(২৫ + ১৫) × ২ - ৩০ ÷ ৩",
        fractionNum1: String = "৩",
        fractionDen1: String = "৪",
        fractionOp: String = "+",
        fractionNum2: String = "২",
        fractionDen2: String = "৫",
        percentageSubType: Int = 0,
        percentVal1: String = "১৫",
        percentVal2: String = "৫০০",
        ratioSubType: Int = 0,
        ratioTotal: String = "১২০০",
        ratioR1: String = "৩",
        ratioR2: String = "৫",
        ratioR3: String = "",
        profitLossSubType: Int = 0,
        plCp: String = "৫০০",
        plSp: String = "৫৫০",
        plPercent: String = "১০",
        plMp: String = "৬০০",
        averageInput: String = "১০, ২০, ৩০, ৪০, ৫০",
        interestMode: InterestCalculationMode = InterestCalculationMode.SIMPLE,
        compoundingFreq: Int = 1,
        siP: String = "৫০০০",
        siR: String = "১০",
        siT: String = "৩",
        siI: String = "",
        workSubType: Int = 0,
        workTimeA: String = "১০",
        workTimeB: String = "১৫",
        workTimeC: String = "১২",
        sdtSpeed: String = "",
        sdtDistance: String = "১০০",
        sdtTime: String = "২",
        divNumber: String = "৭৫২১",
        hcfLcmInput: String = "২৪, ৩৬",
        sqCubeNumber: String = "১৪৪",
        sqCubeOp: Int = 1,
        linearEqInput: String = "3x + 12 = 45",
        quadA: String = "১",
        quadB: String = "-৫",
        quadC: String = "৬",
        quadRelation: String = "=",
        factorInput: String = "x² - 5x + 6",
        simplifyInput: String = "√72"
    ) = evaluateTopicInputValidation(
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

    // -----------------------------------------------------------------
    // 1. ALL 16 TOPICS TABLE-DRIVEN CROSS-LAYER PARITY MATRIX
    // -----------------------------------------------------------------
    @Test
    fun allSixteenTopics_tableDrivenValidationMatchesEngineSuccessParity() {
        data class Case(
            val label: String,
            val expectedValid: Boolean,
            val runUiValidation: () -> Boolean,
            val runEngine: () -> Boolean
        )

        val matrix = listOf(
            // 1. BASIC_ARITHMETIC
            Case(
                "Arithmetic valid BODMAS",
                true,
                { validate(SolverTopic.BASIC_ARITHMETIC, arithmeticExpr = "(১৫ + ৫) × ৪ ÷ ২ - ৬").canSolve },
                { MathProblemSolverEngine.solveArithmetic("(১৫ + ৫) × ৪ ÷ ২ - ৬", SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Arithmetic invalid bracket",
                false,
                { validate(SolverTopic.BASIC_ARITHMETIC, arithmeticExpr = "(১৫ + ৫").canSolve },
                { MathProblemSolverEngine.solveArithmetic("(১৫ + ৫", SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Arithmetic division by zero",
                false,
                { validate(SolverTopic.BASIC_ARITHMETIC, arithmeticExpr = "10 ÷ 0").canSolve },
                { MathProblemSolverEngine.solveArithmetic("10 ÷ 0", SolverMode.QUICK).isSuccess }
            ),

            // 2. FRACTIONS_DECIMALS
            Case(
                "Fraction addition valid",
                true,
                { validate(SolverTopic.FRACTIONS_DECIMALS, fractionNum1 = "3", fractionDen1 = "4", fractionOp = "+", fractionNum2 = "2", fractionDen2 = "5").canSolve },
                { MathProblemSolverEngine.solveFraction(3, 4, "+", 2, 5, SolverMode.LEARNING).isSuccess }
            ),
            Case(
                "Fraction zero denominator",
                false,
                { validate(SolverTopic.FRACTIONS_DECIMALS, fractionNum1 = "3", fractionDen1 = "0", fractionOp = "+", fractionNum2 = "2", fractionDen2 = "5").canSolve },
                { MathProblemSolverEngine.solveFraction(3, 0, "+", 2, 5, SolverMode.LEARNING).isSuccess }
            ),
            Case(
                "Fraction division by zero fraction",
                false,
                { validate(SolverTopic.FRACTIONS_DECIMALS, fractionNum1 = "3", fractionDen1 = "4", fractionOp = "/", fractionNum2 = "0", fractionDen2 = "5").canSolve },
                { MathProblemSolverEngine.solveFraction(3, 4, "/", 0, 5, SolverMode.LEARNING).isSuccess }
            ),
            Case(
                "Fraction LCM overflow",
                false,
                { validate(SolverTopic.FRACTIONS_DECIMALS, fractionNum1 = "1", fractionDen1 = "4000000000", fractionOp = "+", fractionNum2 = "1", fractionDen2 = "4000000001").canSolve },
                { MathProblemSolverEngine.solveFraction(1, 4_000_000_000L, "+", 1, 4_000_000_001L, SolverMode.QUICK).isSuccess }
            ),

            // 3. PERCENTAGE
            Case(
                "Percentage sub 0 valid",
                true,
                { validate(SolverTopic.PERCENTAGE, percentageSubType = 0, percentVal1 = "20", percentVal2 = "500").canSolve },
                { MathProblemSolverEngine.solvePercentage(MathProblemSolverEngine.PercentageType.OF_NUMBER, 20.0, 500.0, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Percentage sub 1 total zero rejected",
                false,
                { validate(SolverTopic.PERCENTAGE, percentageSubType = 1, percentVal1 = "25", percentVal2 = "0").canSolve },
                { MathProblemSolverEngine.solvePercentage(MathProblemSolverEngine.PercentageType.WHAT_PERCENT_OF, 25.0, 0.0, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Percentage sub 2 oldVal zero rejected",
                false,
                { validate(SolverTopic.PERCENTAGE, percentageSubType = 2, percentVal1 = "0", percentVal2 = "100").canSolve },
                { MathProblemSolverEngine.solvePercentage(MathProblemSolverEngine.PercentageType.INCREASE_DECREASE, 0.0, 100.0, SolverMode.QUICK).isSuccess }
            ),

            // 4. RATIO_PROPORTION
            Case(
                "Ratio divide valid 3 terms",
                true,
                { validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 0, ratioTotal = "600", ratioR1 = "1", ratioR2 = "2", ratioR3 = "3").canSolve },
                { MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, 600.0, 1.0, 2.0, 3.0, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Ratio divide zero total accepted",
                true,
                { validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 0, ratioTotal = "0", ratioR1 = "3", ratioR2 = "5").canSolve },
                { MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, 0.0, 3.0, 5.0, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Ratio divide negative total rejected",
                false,
                { validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 0, ratioTotal = "-10", ratioR1 = "3", ratioR2 = "5").canSolve },
                { MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, -10.0, 3.0, 5.0, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Ratio fourth proportional A=0 rejected",
                false,
                { validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 1, ratioR1 = "0", ratioR2 = "8", ratioTotal = "12").canSolve },
                { MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.FOURTH_PROPORTIONAL, 12.0, 0.0, 8.0, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Ratio fourth proportional B=0 rejected",
                false,
                { validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 1, ratioR1 = "4", ratioR2 = "0", ratioTotal = "12").canSolve },
                { MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.FOURTH_PROPORTIONAL, 12.0, 4.0, 0.0, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Ratio fourth proportional C=0 rejected",
                false,
                { validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 1, ratioR1 = "4", ratioR2 = "8", ratioTotal = "0").canSolve },
                { MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.FOURTH_PROPORTIONAL, 0.0, 4.0, 8.0, null, SolverMode.QUICK).isSuccess }
            ),

            // 5. PROFIT_LOSS
            Case(
                "ProfitLoss sub 0 SP=0 (100% loss) accepted",
                true,
                { validate(SolverTopic.PROFIT_LOSS, profitLossSubType = 0, plCp = "500", plSp = "0").canSolve },
                { MathProblemSolverEngine.solveProfitLoss(MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT, 500.0, 0.0, null, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "ProfitLoss sub 0 SP<0 rejected",
                false,
                { validate(SolverTopic.PROFIT_LOSS, profitLossSubType = 0, plCp = "500", plSp = "-1").canSolve },
                { MathProblemSolverEngine.solveProfitLoss(MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT, 500.0, -1.0, null, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "ProfitLoss sub 3 MP missing rejected (no silent fallback)",
                false,
                { validate(SolverTopic.PROFIT_LOSS, profitLossSubType = 3, plMp = "", plPercent = "10").canSolve },
                { MathProblemSolverEngine.solveProfitLoss(MathProblemSolverEngine.ProfitLossType.DISCOUNT_MARKED_PRICE, null, null, 10.0, null, SolverMode.QUICK).isSuccess }
            ),

            // 6. AVERAGE
            Case(
                "Average valid dot decimal list",
                true,
                { validate(SolverTopic.AVERAGE, averageInput = "10.5, 20.5 30").canSolve },
                {
                    val parsed = parseStrictBnDoubleList("10.5, 20.5 30") as StrictListParseResult.Valid
                    MathProblemSolverEngine.solveAverage(parsed.values, SolverMode.QUICK).isSuccess
                }
            ),
            Case(
                "Average empty rejected",
                false,
                { validate(SolverTopic.AVERAGE, averageInput = "   ").canSolve },
                { MathProblemSolverEngine.solveAverage(emptyList(), SolverMode.QUICK).isSuccess }
            ),

            // 7. SIMPLE_INTEREST (Simple + Compound)
            Case(
                "Simple interest solve I valid",
                true,
                { validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.SIMPLE, siP = "5000", siR = "10", siT = "3", siI = "").canSolve },
                { MathProblemSolverEngine.solveSimpleInterest(5000.0, 10.0, 3.0, null, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Simple interest 4 values rejected",
                false,
                { validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.SIMPLE, siP = "5000", siR = "10", siT = "3", siI = "1500").canSolve },
                { MathProblemSolverEngine.solveSimpleInterest(5000.0, 10.0, 3.0, 1500.0, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Compound interest t=0 supported",
                true,
                { validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.COMPOUND, compoundingFreq = 4, siP = "5000", siR = "10", siT = "0").canSolve },
                { MathProblemSolverEngine.solveCompoundInterest(5000.0, 10.0, 0.0, 4, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Compound interest m=366 rejected",
                false,
                { validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.COMPOUND, compoundingFreq = 366, siP = "5000", siR = "10", siT = "2").canSolve },
                { MathProblemSolverEngine.solveCompoundInterest(5000.0, 10.0, 2.0, 366, SolverMode.QUICK).isSuccess }
            ),

            // 8. TIME_AND_WORK
            Case(
                "TimeAndWork 3 persons missing C rejected",
                false,
                { validate(SolverTopic.TIME_AND_WORK, workSubType = 1, workTimeA = "10", workTimeB = "15", workTimeC = "").canSolve },
                { MathProblemSolverEngine.solveTimeAndWork(MathProblemSolverEngine.WorkType.THREE_PERSONS, 10.0, 15.0, null, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "TimeAndWork pipe A>=B rejected",
                false,
                { validate(SolverTopic.TIME_AND_WORK, workSubType = 2, workTimeA = "15", workTimeB = "10").canSolve },
                { MathProblemSolverEngine.solveTimeAndWork(MathProblemSolverEngine.WorkType.PIPE_CISTERN, 15.0, 10.0, null, SolverMode.QUICK).isSuccess }
            ),

            // 9. SPEED_DISTANCE_TIME
            Case(
                "SDT D=0 T=5 -> S=0 supported",
                true,
                { validate(SolverTopic.SPEED_DISTANCE_TIME, sdtSpeed = "", sdtDistance = "0", sdtTime = "5").canSolve },
                { MathProblemSolverEngine.solveSpeedDistanceTime(null, 0.0, 5.0, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "SDT all 3 filled rejected",
                false,
                { validate(SolverTopic.SPEED_DISTANCE_TIME, sdtSpeed = "50", sdtDistance = "100", sdtTime = "2").canSolve },
                { MathProblemSolverEngine.solveSpeedDistanceTime(50.0, 100.0, 2.0, SolverMode.QUICK).isSuccess }
            ),

            // 10. NUMBER_SYSTEM_DIVISIBILITY
            Case(
                "Divisibility valid",
                true,
                { validate(SolverTopic.NUMBER_SYSTEM_DIVISIBILITY, divNumber = "7521").canSolve },
                { MathProblemSolverEngine.solveDivisibility(7521L, 3, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Divisibility blank rejected",
                false,
                { validate(SolverTopic.NUMBER_SYSTEM_DIVISIBILITY, divNumber = "").canSolve },
                { parseBnToLong("") != null }
            ),

            // 11. HCF_LCM
            Case(
                "HCF_LCM valid",
                true,
                { validate(SolverTopic.HCF_LCM, hcfLcmInput = "24, 36").canSolve },
                { MathProblemSolverEngine.solveHcfLcm(listOf(24L, 36L), SolverMode.QUICK).isSuccess }
            ),
            Case(
                "HCF_LCM overflow rejected",
                false,
                { validate(SolverTopic.HCF_LCM, hcfLcmInput = "4000000000, 4000000001").canSolve },
                { MathProblemSolverEngine.solveHcfLcm(listOf(4_000_000_000L, 4_000_000_001L), SolverMode.QUICK).isSuccess }
            ),

            // 12. SQUARES_CUBES_ROOTS
            Case(
                "SquareRoot positive valid",
                true,
                { validate(SolverTopic.SQUARES_CUBES_ROOTS, sqCubeNumber = "144", sqCubeOp = 1).canSolve },
                { MathProblemSolverEngine.solveSquareCube(144.0, MathProblemSolverEngine.SquareCubeOp.SQUARE_ROOT, SolverMode.QUICK).isSuccess }
            ),
            Case(
                "SquareRoot negative rejected",
                false,
                { validate(SolverTopic.SQUARES_CUBES_ROOTS, sqCubeNumber = "-9", sqCubeOp = 1).canSolve },
                { MathProblemSolverEngine.solveSquareCube(-9.0, MathProblemSolverEngine.SquareCubeOp.SQUARE_ROOT, SolverMode.QUICK).isSuccess }
            ),

            // 13. LINEAR_EQUATIONS
            Case(
                "Linear eq valid",
                true,
                { validate(SolverTopic.LINEAR_EQUATIONS, linearEqInput = "3x + 12 = 45").canSolve },
                { MathProblemSolverEngine.solveLinearEquation("3x + 12 = 45", SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Linear eq missing relation rejected",
                false,
                { validate(SolverTopic.LINEAR_EQUATIONS, linearEqInput = "3x + 12").canSolve },
                { MathProblemSolverEngine.solveLinearEquation("3x + 12", SolverMode.QUICK).isSuccess }
            ),

            // 14. QUADRATIC_EQUATIONS
            Case(
                "Quadratic eq valid",
                true,
                { validate(SolverTopic.QUADRATIC_EQUATIONS, quadA = "1", quadB = "-5", quadC = "6", quadRelation = "=").canSolve },
                { MathProblemSolverEngine.solveQuadraticEquation(1.0, -5.0, 6.0, SolverMode.QUICK, "=").isSuccess }
            ),
            Case(
                "Quadratic eq a=0 rejected",
                false,
                { validate(SolverTopic.QUADRATIC_EQUATIONS, quadA = "0", quadB = "2", quadC = "1", quadRelation = "=").canSolve },
                { MathProblemSolverEngine.solveQuadraticEquation(0.0, 2.0, 1.0, SolverMode.QUICK, "=").isSuccess }
            ),
            Case(
                "Quadratic inequality a=0 rejected",
                false,
                { validate(SolverTopic.QUADRATIC_EQUATIONS, quadA = "0", quadB = "2", quadC = "1", quadRelation = ">").canSolve },
                { MathProblemSolverEngine.solveQuadraticEquation(0.0, 2.0, 1.0, SolverMode.QUICK, ">").isSuccess }
            ),

            // 15. FACTORIZATION
            Case(
                "Factorization valid",
                true,
                { validate(SolverTopic.FACTORIZATION, factorInput = "x² - 5x + 6").canSolve },
                { MathProblemSolverEngine.solveFactorization("x² - 5x + 6", SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Factorization blank rejected",
                false,
                { validate(SolverTopic.FACTORIZATION, factorInput = "   ").canSolve },
                { MathProblemSolverEngine.solveFactorization("   ", SolverMode.QUICK).isSuccess }
            ),

            // 16. SIMPLIFICATION
            Case(
                "Simplification valid",
                true,
                { validate(SolverTopic.SIMPLIFICATION, simplifyInput = "√72").canSolve },
                { MathProblemSolverEngine.solveSimplification("√72", SolverMode.QUICK).isSuccess }
            ),
            Case(
                "Simplification blank rejected",
                false,
                { validate(SolverTopic.SIMPLIFICATION, simplifyInput = "").canSolve },
                { MathProblemSolverEngine.solveSimplification("", SolverMode.QUICK).isSuccess }
            )
        )

        matrix.forEach { case ->
            val uiCanSolve = case.runUiValidation()
            val engineOk = case.runEngine()
            assertEquals("UI canSolve mismatch for '${case.label}'", case.expectedValid, uiCanSolve)
            assertEquals("Engine isSuccess mismatch for '${case.label}'", case.expectedValid, engineOk)
        }
    }

    // -----------------------------------------------------------------
    // 2. STRICT LIST GRAMMAR & OVERFLOW HARDENING
    // -----------------------------------------------------------------
    @Test
    fun averageStrictListParser_enforcesCommaOrSpaceSeparatorsAndDotDecimals() {
        val valid = parseStrictBnDoubleList("১০, ২০.৫ ৩০.২৫")
        assertTrue(valid is StrictListParseResult.Valid)
        assertEquals(listOf(10.0, 20.5, 30.25), (valid as StrictListParseResult.Valid).values)

        val invalidToken = parseStrictBnDoubleList("10, abc, 30")
        assertTrue(invalidToken is StrictListParseResult.Invalid)
        assertTrue((invalidToken as StrictListParseResult.Invalid).messageBn.contains("abc"))

        val trailingComma = parseStrictBnDoubleList("10, 20,")
        assertTrue(trailingComma is StrictListParseResult.Invalid)

        val doubleComma = parseStrictBnDoubleList("10,,20")
        assertTrue(doubleComma is StrictListParseResult.Invalid)

        val leadingComma = parseStrictBnDoubleList(",10, 20")
        assertTrue(leadingComma is StrictListParseResult.Invalid)

        val multiDotToken = parseStrictBnDoubleList("10.2.3 20")
        assertTrue(multiDotToken is StrictListParseResult.Invalid)
        assertTrue((multiDotToken as StrictListParseResult.Invalid).messageBn.contains("10.2.3"))
    }

    @Test
    fun hcfLcmStrictListParserAndOverflow_rejectsZeroNegativeDecimalsAndLongOverflow() {
        val zeroRes = parseStrictBnPositiveLongList("24, 0")
        assertTrue(zeroRes is StrictListParseResult.Invalid)
        assertTrue((zeroRes as StrictListParseResult.Invalid).messageBn.contains("0"))

        val negRes = parseStrictBnPositiveLongList("24, -12")
        assertTrue(negRes is StrictListParseResult.Invalid)
        assertTrue((negRes as StrictListParseResult.Invalid).messageBn.contains("-12"))

        val decRes = parseStrictBnPositiveLongList("24, 3.5")
        assertTrue(decRes is StrictListParseResult.Invalid)
        assertTrue((decRes as StrictListParseResult.Invalid).messageBn.contains("3.5"))

        val singleRes = parseStrictBnPositiveLongList("24")
        assertTrue(singleRes is StrictListParseResult.Invalid)

        // Direct engine check also rejects zero/negative elements (no silent .filter { it > 0 })
        assertFalse(MathProblemSolverEngine.solveHcfLcm(listOf(24L, 36L, -5L), SolverMode.QUICK).isSuccess)
        assertFalse(MathProblemSolverEngine.solveHcfLcm(listOf(24L, 0L), SolverMode.QUICK).isSuccess)

        // Overflow safety: lcm(a, b) using BigInteger + Math.multiplyExact
        assertEquals(-1L, NumberSystemProvider.lcm(4_000_000_000L, 4_000_000_001L))
        assertEquals(-1L, NumberSystemProvider.lcm(Long.MAX_VALUE, 2L))
        assertFalse(MathProblemSolverEngine.solveHcfLcm(listOf(100_000_007L, 100_000_037L, 100_000_039L), SolverMode.QUICK).isSuccess)
    }

    // -----------------------------------------------------------------
    // 3. RATIO, PROFIT & LOSS, INTEREST & SDT BOUNDARY POLICIES
    // -----------------------------------------------------------------
    @Test
    fun ratioDomainPolicies_enforcedConsistently() {
        // total = 0 -> valid zero-valued shares
        val zeroShare = MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, 0.0, 3.0, 5.0, null, SolverMode.LEARNING)
        assertTrue(zeroShare.isSuccess)
        assertTrue(zeroShare.finalAnswerBn.contains("০"))

        // total < 0 -> rejected
        assertFalse(MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, -1.0, 3.0, 5.0, null, SolverMode.QUICK).isSuccess)
        assertFalse(validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 0, ratioTotal = "-1", ratioR1 = "3", ratioR2 = "5").canSolve)

        // r3 non-blank invalid/zero/negative rejected in UI validation & engine
        assertFalse(validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 0, ratioTotal = "100", ratioR1 = "2", ratioR2 = "3", ratioR3 = "0").canSolve)
        assertFalse(validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 0, ratioTotal = "100", ratioR1 = "2", ratioR2 = "3", ratioR3 = "-2").canSolve)
        assertFalse(validate(SolverTopic.RATIO_PROPORTION, ratioSubType = 0, ratioTotal = "100", ratioR1 = "2", ratioR2 = "3", ratioR3 = "abc").canSolve)
        assertFalse(MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, 100.0, 2.0, 3.0, 0.0, SolverMode.QUICK).isSuccess)
        assertFalse(MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, 100.0, 2.0, 3.0, -2.0, SolverMode.QUICK).isSuccess)

        // Non-finite ratio terms rejected
        assertFalse(MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, Double.NaN, 2.0, 3.0, null, SolverMode.QUICK).isSuccess)
        assertFalse(MathProblemSolverEngine.solveRatio(MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY, 100.0, Double.POSITIVE_INFINITY, 3.0, null, SolverMode.QUICK).isSuccess)
    }

    @Test
    fun profitLossDomainPolicies_spZeroAllowedSpNegativeRejected() {
        val spZeroRes = MathProblemSolverEngine.solveProfitLoss(
            MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT,
            cp = 250.0,
            sp = 0.0,
            percent = null,
            mp = null,
            mode = SolverMode.LEARNING
        )
        assertTrue(spZeroRes.isSuccess)
        assertTrue(spZeroRes.finalAnswerBn.contains("১০০%"))
        assertTrue(validate(SolverTopic.PROFIT_LOSS, profitLossSubType = 0, plCp = "250", plSp = "0").canSolve)

        assertFalse(MathProblemSolverEngine.solveProfitLoss(MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT, 250.0, -0.01, null, null, SolverMode.QUICK).isSuccess)
        assertFalse(validate(SolverTopic.PROFIT_LOSS, profitLossSubType = 0, plCp = "250", plSp = "-0.01").canSolve)
        assertFalse(validate(SolverTopic.PROFIT_LOSS, profitLossSubType = 0, plCp = "0", plSp = "100").canSolve)
    }

    @Test
    fun simpleAndCompoundInterestPolicies_consistentAcrossUiAndEngine() {
        // Simple interest rejects 4 simultaneous fields & malformed tokens
        assertFalse(validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.SIMPLE, siP = "5000", siR = "10", siT = "3", siI = "1500").canSolve)
        assertFalse(MathProblemSolverEngine.solveSimpleInterest(5000.0, 10.0, 3.0, 1500.0, null, SolverMode.QUICK).isSuccess)
        assertFalse(validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.SIMPLE, siP = "5000", siR = "10", siT = "abc", siI = "1500").canSolve)

        // Compound interest frequency validation (strict integer 1..365)
        assertEquals(1, parseCompoundingFrequency("1"))
        assertEquals(12, parseCompoundingFrequency("১২"))
        assertEquals(365, parseCompoundingFrequency("365"))
        assertNull(parseCompoundingFrequency("1.5"))
        assertNull(parseCompoundingFrequency("0"))
        assertNull(parseCompoundingFrequency("-4"))
        assertNull(parseCompoundingFrequency("366"))
        assertNull(parseCompoundingFrequency("NaN"))

        // t = 0 (N = 0) accepted identically in UI & engine: Amount = P, CI = 0
        assertTrue(validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.COMPOUND, compoundingFreq = 12, siP = "5000", siR = "8", siT = "0").canSolve)
        val tZero = MathProblemSolverEngine.solveCompoundInterest(5000.0, 8.0, 0.0, 12, SolverMode.LEARNING)
        assertTrue(tZero.isSuccess)
        assertTrue(tZero.finalAnswerBn.contains("৫০০০"))
        assertTrue(tZero.finalAnswerBn.contains("০ টাকা"))

        // m * t = 10_000 boundary accepted vs 10_001 rejected
        assertTrue(validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.COMPOUND, compoundingFreq = 100, siP = "100", siR = "1", siT = "100").canSolve)
        assertTrue(MathProblemSolverEngine.solveCompoundInterest(100.0, 1.0, 100.0, 100, SolverMode.QUICK).isSuccess)
        assertFalse(validate(SolverTopic.SIMPLE_INTEREST, interestMode = InterestCalculationMode.COMPOUND, compoundingFreq = 200, siP = "100", siR = "1", siT = "51").canSolve)
        assertFalse(MathProblemSolverEngine.solveCompoundInterest(100.0, 1.0, 51.0, 200, SolverMode.QUICK).isSuccess)
    }

    @Test
    fun speedDistanceTime_zeroDistanceSupportedAndDomainStrictlyEnforced() {
        // D = 0 solving S or T yields valid 0 result
        val sZero = MathProblemSolverEngine.solveSpeedDistanceTime(null, 0.0, 4.0, SolverMode.QUICK)
        assertTrue(sZero.isSuccess)
        assertTrue(sZero.finalAnswerBn.contains("০ km/h"))

        val tZero = MathProblemSolverEngine.solveSpeedDistanceTime(60.0, 0.0, null, SolverMode.QUICK)
        assertTrue(tZero.isSuccess)
        assertTrue(tZero.finalAnswerBn.contains("০ ঘন্টা"))

        // Malformed third field fails UI validation even when 2 fields are valid
        assertFalse(validate(SolverTopic.SPEED_DISTANCE_TIME, sdtSpeed = "abc", sdtDistance = "100", sdtTime = "2").canSolve)
        // S=0 or T=0 rejected in both UI and engine
        assertFalse(validate(SolverTopic.SPEED_DISTANCE_TIME, sdtSpeed = "0", sdtDistance = "100", sdtTime = "").canSolve)
        assertFalse(MathProblemSolverEngine.solveSpeedDistanceTime(0.0, 100.0, null, SolverMode.QUICK).isSuccess)
        assertFalse(validate(SolverTopic.SPEED_DISTANCE_TIME, sdtSpeed = "", sdtDistance = "100", sdtTime = "0").canSolve)
        assertFalse(MathProblemSolverEngine.solveSpeedDistanceTime(null, 100.0, 0.0, SolverMode.QUICK).isSuccess)
    }

    @Test
    fun parseBnToDoubleAndLong_rejectNonFiniteAndMalformedValues() {
        assertNull(parseBnToDouble("NaN"))
        assertNull(parseBnToDouble("Infinity"))
        assertNull(parseBnToDouble("-Infinity"))
        assertNull(parseBnToDouble("1e500"))
        assertNull(parseBnToLong("12.5"))
        assertNull(parseBnToLong("1e3"))
        assertNotNull(parseBnToDouble("২,৫"))
        assertNotNull(parseBnToLong("১২৩৪"))
    }

    // -----------------------------------------------------------------
    // 4. A-01: SHARED MATH ENGINE NUMERIC VS BOOLEAN ANSWER VALIDATION
    // -----------------------------------------------------------------
    @Test
    fun a01_sharedMathEngine_numericOneAndZeroRejectBooleanWordsAndAcceptNumericForms() {
        val trueWords = listOf("হ্যাঁ", "সত্য", "true", "True", "TRUE", "yes", "Yes", "ঠিক", "সত্য (True)")
        val falseWords = listOf("না", "মিথ্যা", "false", "False", "FALSE", "no", "No", "ভুল", "মিথ্যা (False)")

        // Numeric expected 1 ("1", "১", "1.0", "১.০", "1 একক") must reject all Boolean words
        val expectedOnes = listOf("1", "১", "1.0", "১.০", "1 সেমি")
        for (expOne in expectedOnes) {
            for (word in trueWords + falseWords) {
                assertFalse(
                    "Numeric expected '$expOne' must reject boolean word '$word'",
                    com.example.math.SharedMathEngine.isAnswerCorrect(word, expOne)
                )
            }
        }

        // Numeric expected 0 ("0", "০", "0.0", "০.০") must reject all Boolean words
        val expectedZeros = listOf("0", "০", "0.0", "০.০")
        for (expZero in expectedZeros) {
            for (word in falseWords + trueWords) {
                assertFalse(
                    "Numeric expected '$expZero' must reject boolean word '$word'",
                    com.example.math.SharedMathEngine.isAnswerCorrect(word, expZero)
                )
            }
        }

        // Numeric expected 1 and 0 must still accept valid numeric equivalents in Bengali & English
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("1", "1"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("১", "1"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("1", "১"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("১", "১"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("1.0", "১"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("১.০", "1"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("2/2", "১"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("  ১  ", "1"))

        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("0", "0"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("০", "0"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("0", "০"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("০", "০"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("0.0", "০"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("০.০", "0"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("0/5", "০"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("  ০  ", "0"))

        // Numeric 1 vs 0 must remain distinct
        assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("0", "1"))
        assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("১", "০"))
    }

    @Test
    fun a01_sharedMathEngine_booleanQuestionsAcceptValidAliasesAndRejectNumericOneZero() {
        val trueAliases = listOf("true", "True", "সত্য", "হ্যাঁ", "yes", "ঠিক", "সত্য (True)")
        val falseAliases = listOf("false", "False", "মিথ্যা", "না", "no", "ভুল", "মিথ্যা (False)")

        for (userTrue in trueAliases) {
            for (expectedTrue in trueAliases) {
                assertTrue(
                    "Boolean true alias '$userTrue' must match expected '$expectedTrue'",
                    com.example.math.SharedMathEngine.isAnswerCorrect(userTrue, expectedTrue)
                )
            }
            for (expectedFalse in falseAliases) {
                assertFalse(
                    "Boolean true alias '$userTrue' must NOT match expected false '$expectedFalse'",
                    com.example.math.SharedMathEngine.isAnswerCorrect(userTrue, expectedFalse)
                )
            }
            // Boolean questions must NOT accept raw numeric 1 or 0
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("1", userTrue))
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("১", userTrue))
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("0", userTrue))
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("০", userTrue))
        }

        for (userFalse in falseAliases) {
            for (expectedFalse in falseAliases) {
                assertTrue(
                    "Boolean false alias '$userFalse' must match expected '$expectedFalse'",
                    com.example.math.SharedMathEngine.isAnswerCorrect(userFalse, expectedFalse)
                )
            }
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("0", userFalse))
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("০", userFalse))
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("1", userFalse))
            assertFalse(com.example.math.SharedMathEngine.isAnswerCorrect("১", userFalse))
        }

        // Existing fraction, mixed fraction, ratio, unit-suffixed, and decimal answer formats remain valid
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("২/৪", "1/2"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("0.5", "১/২"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("১ সমস্ত ১/২", "1.5"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("২৪ সে.মি.", "24 cm"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("৩:৪", "6:8"))
    }

    // -----------------------------------------------------------------
    // 5. A-02: PROBLEM SOLVER ANSWER VALIDATION HARDENING
    // -----------------------------------------------------------------
    @Test
    fun a02_problemSolverStepValidation_rejectsPartialSubstringsAndAcceptsExactOrEquivalent125() {
        val expectedEn = "125"
        val expectedBn = "১২৫"

        // Required substring rejection checks for expected 125 (both English and Bengali numerals)
        for (expected in listOf(expectedEn, expectedBn)) {
            assertFalse(
                "Attempt '1' must be rejected for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("1", expected)
            )
            assertFalse(
                "Attempt '১' must be rejected for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("১", expected)
            )
            assertFalse(
                "Attempt '12' must be rejected for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("12", expected)
            )
            assertFalse(
                "Attempt '১২' must be rejected for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("১২", expected)
            )
            assertFalse(
                "Attempt '25' must be rejected for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("25", expected)
            )
            assertFalse(
                "Attempt '২৫' must be rejected for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("২৫", expected)
            )
            assertFalse(
                "Attempt '5' must be rejected for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("5", expected)
            )

            // Valid full value in English and Bengali numerals
            assertTrue(
                "Attempt '125' must be accepted for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("125", expected)
            )
            assertTrue(
                "Attempt '১২৫' must be accepted for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("১২৫", expected)
            )
            // Leading and trailing whitespace
            assertTrue(
                "Attempt '  125  ' with whitespace must be accepted for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("  125  ", expected)
            )
            assertTrue(
                "Attempt '  ১২৫  ' with whitespace must be accepted for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("  ১২৫  ", expected)
            )
            // Equivalent numeric forms (decimal & fraction)
            assertTrue(
                "Attempt '125.0' must be accepted for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("125.0", expected)
            )
            assertTrue(
                "Attempt '250/2' must be accepted for expected '$expected'",
                com.example.ui.screens.isSolverStepAttemptCorrect("250/2", expected)
            )
        }
    }

    @Test
    fun a02_problemSolverStepValidation_supportsFractionsDecimalsNegativesEquationsAndRejectsMalformedOrEmpty() {
        // 1. Valid fraction and decimal forms
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("3/4", "৩/৪"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("0.75", "৩/৪"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("০.৭৫", "3/4"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("6/8", "৩/৪"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("১ সমস্ত ১/২", "৩/২"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("1.5", "৩/২"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("3", "৩/৪"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("4", "৩/৪"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("/", "৩/৪"))

        // 2. Negative values
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("-125", "-১২৫"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("-১২৫", "-125"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("  -১২৫  ", "-125"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("-3/4", "-০.৭৫"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("125", "-125"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("১২৫", "-১২৫"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("-12", "-125"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("-", "-125"))

        // 3. Structured step expected values (equations, arrow transformations, labeled values, ratios)
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("125", "x = ১২৫"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("১২৫", "x = 125"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("x = 125", "x = ১২৫"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("12", "x = ১২৫"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("x =", "x = ১২৫"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("y = 125", "x = ১২৫"))

        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("৪০", "২৫ + ১৫ = ৪০"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("40", "২৫ + ১৫ = ৪০"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("২৫", "২৫ + ১৫ = ৪০"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("১৫", "২৫ + ১৫ = ৪০"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("৪", "২৫ + ১৫ = ৪০"))

        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("১২৫", "রূপান্তর ➜ ১২৫ টাকা"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("125", "উত্তর: ১২৫ টাকা"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("১২", "উত্তর: ১২৫ টাকা"))

        // Ratios must not treat the colon as a text label prefix
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("৩:৪", "3:4"))
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("6:8", "৩:৪"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("4", "3:4"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("৪", "৩:৪"))

        // 4. Ordinary algebraic/text step expressions require full normalized match (no partial factor/substring)
        assertTrue(com.example.ui.screens.isSolverStepAttemptCorrect("(x - 2)(x - 3)", "(x-2)(x-3)"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("(x - 2)", "(x - 2)(x - 3)"))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("x", "(x - 2)(x - 3)"))

        // 5. Empty and malformed inputs
        val malformedAndEmptyInputs = listOf("", "   ", "\t\n", "abc", "12..5", "12/0", "+++", "--125", "125/0", "NaN")
        for (badInput in malformedAndEmptyInputs) {
            assertFalse(
                "Malformed or empty input '$badInput' must be rejected for expected '125'",
                com.example.ui.screens.isSolverStepAttemptCorrect(badInput, "125")
            )
            assertFalse(
                "Malformed or empty input '$badInput' must be rejected for expected '১২৫'",
                com.example.ui.screens.isSolverStepAttemptCorrect(badInput, "১২৫")
            )
        }
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("125", ""))
        assertFalse(com.example.ui.screens.isSolverStepAttemptCorrect("125", "   "))
    }

    // -----------------------------------------------------------------
    // 6. A-03: BOOK QUESTION ENGINE NUMERICAL ANSWER VALIDATION
    // -----------------------------------------------------------------
    @Test
    fun a03_bookQuestionEngine_exactDecimalAndFractionEquivalenceRejectsNearIncorrectValues() {
        // Expected 0.50: 0.50, 0.5, ০.৫০, ০.৫, 1/2, ১/২, 2/4 are exact equivalents;
        // near-but-incorrect decimals within the old 0.01 window (0.509, 0.495, 0.501, 0.50001) MUST be rejected.
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("0.50", "0.50"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("0.5", "0.50"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("০.৫০", "0.50"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("০.৫", "0.50"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("1/2", "0.50"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("১/২", "0.50"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("2/4", "0.50"))

        val nearIncorrectForHalf = listOf("0.509", "০.৫০৯", "0.491", "0.499", "0.501", "0.505", "0.5001", "0.50001")
        for (nearWrong in nearIncorrectForHalf) {
            assertFalse(
                "Near-but-incorrect answer '$nearWrong' must NOT be accepted for expected '0.50'",
                com.example.math.BookQuestionEngine.isTextEquivalent(nearWrong, "0.50")
            )
        }

        // Exact integer answers (Bengali & English digits, unit suffixes, comma formatting)
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("36", "36"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("৩৬", "36"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("36", "৩৬"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("36.0", "36"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("৩৬টি", "36"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("36.005", "36"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("35.995", "36"))

        // Correct fraction/decimal/mixed-fraction/ratio equivalence & non-equivalence
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("১/২", "0.5"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("২/৪", "১/২"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("৩/৪", "0.75"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("১ সমস্ত ১/২", "1.5"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("৩ : ৪", "6 : 8"))
        // 1/3 is 0.3333..., NOT 0.33 unless rounding metadata explicitly permits 2 decimal places
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("0.33", "1/3"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("1/3", "0.333"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("১/৩", "১/২"))
        // Ratios must not match non-ratio fractions/decimals
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("3:4", "0.75"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("3:4", "3/4"))
    }

    @Test
    fun a03_bookQuestionEngine_negativesZeroSmallLargeExplicitRoundingAndInvalidInputs() {
        // Negative numerical answers
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("-15.5", "-১৫.৫"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("-১৫.৫", "-15.5"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("-31/2", "-১৫.৫"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("15.5", "-১৫.৫"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("-15.505", "-১৫.৫"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("-15.495", "-১৫.৫"))

        // Zero
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("0", "0"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("০", "0"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("0.0", "০"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("-0.0", "0"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("0/7", "০"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("0.001", "0"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("-0.001", "0"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("0.0001", "0"))

        // Very small decimal values (previously indistinguishable from 0 under < 0.01!)
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("0.0005", "0.0005"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("০.০০০৫", "0.0005"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("1/2000", "0.0005"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("0.0006", "0.0005"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("0", "0.0005"))

        // Large values (exact BigDecimal precision beyond Double mantissa limits)
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("৯৮৭৫৪৩০", "9875430"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("9,875,430", "৯৮৭৫৪৩০"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("999999999999999999", "999999999999999999"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("999999999999999998", "999999999999999999"))

        // Explicit rounding metadata on BookPracticeQuestion when permitted by the question
        val baseNumQuestion = com.example.math.BookPracticeQuestion(
            id = "a03_test_rounding",
            bookId = com.example.math.BookId.CLASS_9_10,
            chapterNumber = 17,
            lessonId = "c910_17_l1",
            format = com.example.math.BookQuestionFormat.NUMERICAL_INPUT,
            difficultyMode = com.example.math.BookPracticeMode.STANDARD,
            provenance = com.example.math.BookQuestionProvenance.ORIGINAL_TEXTBOOK,
            promptBn = "প্রচুরক নির্ণয় কর (২ দশমিক স্থান পর্যন্ত)",
            optionsBn = emptyList(),
            correctOptionIndices = emptySet(),
            correctTextAnswer = "143/3", // 47.6666... -> 47.67 at 2 decimal places
            hintBn = "",
            stepByStepSolutionBn = emptyList(),
            explanationBn = "",
            acceptedRoundingDecimalPlaces = null
        )
        // Without explicit rounding metadata: 47.67 is NOT accepted for exact 143/3, only exact 143/3 or 47 2/3 is accepted
        assertFalse(com.example.math.BookQuestionEngine.evaluateQuestion(baseNumQuestion, emptySet(), "47.67").first)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(baseNumQuestion, emptySet(), "143/3").first)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(baseNumQuestion, emptySet(), "৪৭ সমস্ত ২/৩").first)

        // With explicit acceptedRoundingDecimalPlaces = 2: 47.67 and ৪৭.৬৭ are accepted, while 47.66 and 47.68 are rejected
        val rounded2dpQuestion = baseNumQuestion.copy(acceptedRoundingDecimalPlaces = 2)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(rounded2dpQuestion, emptySet(), "47.67").first)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(rounded2dpQuestion, emptySet(), "৪৭.৬৭").first)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(rounded2dpQuestion, emptySet(), "47.666").first)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(rounded2dpQuestion, emptySet(), "143/3").first)
        assertFalse(com.example.math.BookQuestionEngine.evaluateQuestion(rounded2dpQuestion, emptySet(), "47.66").first)
        assertFalse(com.example.math.BookQuestionEngine.evaluateQuestion(rounded2dpQuestion, emptySet(), "47.68").first)
        assertFalse(com.example.math.BookQuestionEngine.evaluateQuestion(rounded2dpQuestion, emptySet(), "47.7").first)

        // Malformed, division-by-zero, NaN, and Infinity inputs (must be rejected against '0.5' AND against themselves)
        val malformedNumericAndNonFiniteInputs = listOf(
            "", "   ", "NaN", "nan", "-NaN", "Infinity", "+Infinity", "-Infinity", "inf", "∞",
            "1/0", "0/0", "12/0", "-5/0", "১/০", "3:0", "0:0", "1:2:3", "1/2/3", "12..5", "12.3.4", "--5", "++5"
        )
        for (invalid in malformedNumericAndNonFiniteInputs) {
            assertFalse(
                "Invalid input '$invalid' must be rejected against '0.5'",
                com.example.math.BookQuestionEngine.isTextEquivalent(invalid, "0.5")
            )
            assertFalse(
                "Invalid input '$invalid' must be rejected against itself",
                com.example.math.BookQuestionEngine.isTextEquivalent(invalid, invalid)
            )
        }

        // Ordinary text / algebraic formula answers must be rejected against numeric '0.5',
        // but preserved as valid text-to-text matches when compared against their expected text
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("abc", "0.5"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("abc", "abc"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("(x - 2) / (x + 3)", "(x - 2) / (x + 3)"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("সত্য (True)", "সত্য"))
    }
}



