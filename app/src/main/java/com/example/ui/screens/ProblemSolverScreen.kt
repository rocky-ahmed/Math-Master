package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BookmarkEntity
import com.example.math.BengaliNumberFormatter
import com.example.math.InterestCalculationMode
import com.example.math.MathProblemSolverEngine
import com.example.math.SimilarProblemGenerator
import com.example.math.SolutionStep
import com.example.math.SolverLearningState
import com.example.math.SolverMode
import com.example.math.SolverResult
import com.example.math.SolverTopic
import com.example.ui.MathViewModel
import com.example.ui.components.MathTopAppBar
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedUiState
import com.example.ui.theme.MathSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProblemSolverScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val solverHistory by viewModel.solverHistory.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.navigateBack()
    }

    var selectedTopic by rememberManagedUiState(viewModel, "problem_solver:selectedTopic", SolverTopic.BASIC_ARITHMETIC)
    var selectedCategory by rememberManagedUiState(viewModel, "problem_solver:selectedCategory", SolverTopicCategory.ALL)
    var selectedMode by rememberManagedUiState(viewModel, "problem_solver:selectedMode", SolverMode.LEARNING)

    // Dynamic Topic Inputs
    var arithmeticExpr by rememberManagedUiState(viewModel, "problem_solver:arithmeticExpr", "(২৫ + ১৫) × ২ - ৩০ ÷ ৩")

    var fractionNum1 by rememberManagedUiState(viewModel, "problem_solver:fractionNum1", "৩")
    var fractionDen1 by rememberManagedUiState(viewModel, "problem_solver:fractionDen1", "৪")
    var fractionOp by rememberManagedUiState(viewModel, "problem_solver:fractionOp", "+")
    var fractionNum2 by rememberManagedUiState(viewModel, "problem_solver:fractionNum2", "২")
    var fractionDen2 by rememberManagedUiState(viewModel, "problem_solver:fractionDen2", "৫")

    var percentageSubType by rememberManagedUiState(viewModel, "problem_solver:percentageSubType", 0)
    var percentVal1 by rememberManagedUiState(viewModel, "problem_solver:percentVal1", "১৫")
    var percentVal2 by rememberManagedUiState(viewModel, "problem_solver:percentVal2", "৫০০")

    var ratioSubType by rememberManagedUiState(viewModel, "problem_solver:ratioSubType", 0)
    var ratioTotal by rememberManagedUiState(viewModel, "problem_solver:ratioTotal", "১২০০")
    var ratioR1 by rememberManagedUiState(viewModel, "problem_solver:ratioR1", "৩")
    var ratioR2 by rememberManagedUiState(viewModel, "problem_solver:ratioR2", "৫")
    var ratioR3 by rememberManagedUiState(viewModel, "problem_solver:ratioR3", "")

    var profitLossSubType by rememberManagedUiState(viewModel, "problem_solver:profitLossSubType", 0)
    var plCp by rememberManagedUiState(viewModel, "problem_solver:plCp", "৫০০")
    var plSp by rememberManagedUiState(viewModel, "problem_solver:plSp", "৫৫০")
    var plPercent by rememberManagedUiState(viewModel, "problem_solver:plPercent", "১০")
    var plMp by rememberManagedUiState(viewModel, "problem_solver:plMp", "৬০০")

    var averageInput by rememberManagedUiState(viewModel, "problem_solver:averageInput", "১০, ২০, ৩০, ৪০, ৫০")

    var interestMode by rememberManagedUiState(viewModel, "problem_solver:interestMode", InterestCalculationMode.SIMPLE)
    var compoundingFreq by rememberManagedUiState(viewModel, "problem_solver:compoundingFreq", 1)
    var siP by rememberManagedUiState(viewModel, "problem_solver:siP", "৫০০০")
    var siR by rememberManagedUiState(viewModel, "problem_solver:siR", "১০")
    var siT by rememberManagedUiState(viewModel, "problem_solver:siT", "৩")
    var siI by rememberManagedUiState(viewModel, "problem_solver:siI", "")

    var workSubType by rememberManagedUiState(viewModel, "problem_solver:workSubType", 0)
    var workTimeA by rememberManagedUiState(viewModel, "problem_solver:workTimeA", "১০")
    var workTimeB by rememberManagedUiState(viewModel, "problem_solver:workTimeB", "১৫")
    var workTimeC by rememberManagedUiState(viewModel, "problem_solver:workTimeC", "১২")

    var sdtSpeed by rememberManagedUiState(viewModel, "problem_solver:sdtSpeed", "")
    var sdtDistance by rememberManagedUiState(viewModel, "problem_solver:sdtDistance", "১০০")
    var sdtTime by rememberManagedUiState(viewModel, "problem_solver:sdtTime", "২")

    var divNumber by rememberManagedUiState(viewModel, "problem_solver:divNumber", "৭৫২১")
    var divDivisor by rememberManagedUiState(viewModel, "problem_solver:divDivisor", 3)

    var hcfLcmInput by rememberManagedUiState(viewModel, "problem_solver:hcfLcmInput", "২৪, ৩৬")

    var sqCubeNumber by rememberManagedUiState(viewModel, "problem_solver:sqCubeNumber", "১৪৪")
    var sqCubeOp by rememberManagedUiState(viewModel, "problem_solver:sqCubeOp", 1)

    var linearEqInput by rememberManagedUiState(viewModel, "problem_solver:linearEqInput", "৩x + ১২ = ৪৫")

    var quadA by rememberManagedUiState(viewModel, "problem_solver:quadA", "১")
    var quadB by rememberManagedUiState(viewModel, "problem_solver:quadB", "-৫")
    var quadC by rememberManagedUiState(viewModel, "problem_solver:quadC", "৬")
    var quadRelation by rememberManagedUiState(viewModel, "problem_solver:quadRelation", "=")

    var factorInput by rememberManagedUiState(viewModel, "problem_solver:factorInput", "x² - 5x + 6")
    var simplifyInput by rememberManagedUiState(viewModel, "problem_solver:simplifyInput", "√72")

    var solverResult by remember { mutableStateOf<SolverResult?>(null) }
    val learningSessionHolder = remember { SolverLearningSessionHolder() }
    var skipNextAutoSolve by remember { mutableStateOf(false) }

    fun resetCurrentTopicInputs() {
        when (selectedTopic) {
            SolverTopic.BASIC_ARITHMETIC -> arithmeticExpr = ""
            SolverTopic.FRACTIONS_DECIMALS -> {
                fractionNum1 = ""
                fractionDen1 = ""
                fractionNum2 = ""
                fractionDen2 = ""
            }
            SolverTopic.PERCENTAGE -> {
                percentVal1 = ""
                percentVal2 = ""
            }
            SolverTopic.RATIO_PROPORTION -> {
                ratioTotal = ""
                ratioR1 = ""
                ratioR2 = ""
                ratioR3 = ""
            }
            SolverTopic.PROFIT_LOSS -> {
                plCp = ""
                plSp = ""
                plPercent = ""
                plMp = ""
            }
            SolverTopic.AVERAGE -> averageInput = ""
            SolverTopic.SIMPLE_INTEREST -> {
                siP = ""
                siR = ""
                siT = ""
                siI = ""
            }
            SolverTopic.TIME_AND_WORK -> {
                workTimeA = ""
                workTimeB = ""
                workTimeC = ""
            }
            SolverTopic.SPEED_DISTANCE_TIME -> {
                sdtSpeed = ""
                sdtDistance = ""
                sdtTime = ""
            }
            SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> divNumber = ""
            SolverTopic.HCF_LCM -> hcfLcmInput = ""
            SolverTopic.SQUARES_CUBES_ROOTS -> sqCubeNumber = ""
            SolverTopic.LINEAR_EQUATIONS -> linearEqInput = ""
            SolverTopic.QUADRATIC_EQUATIONS -> {
                quadA = ""
                quadB = ""
                quadC = ""
            }
            SolverTopic.FACTORIZATION -> factorInput = ""
            SolverTopic.SIMPLIFICATION -> simplifyInput = ""
        }
        solverResult = null
        learningSessionHolder.clear()
    }

    fun solveCurrentTopic() {
        solverResult = try {
            when (selectedTopic) {
                SolverTopic.BASIC_ARITHMETIC -> {
                    MathProblemSolverEngine.solveArithmetic(arithmeticExpr, selectedMode)
                }
                SolverTopic.FRACTIONS_DECIMALS -> {
                    val n1 = parseBnToLong(fractionNum1) ?: 1L
                    val d1 = parseBnToLong(fractionDen1) ?: 1L
                    val n2 = parseBnToLong(fractionNum2) ?: 1L
                    val d2 = parseBnToLong(fractionDen2) ?: 1L
                    MathProblemSolverEngine.solveFraction(n1, d1, fractionOp, n2, d2, selectedMode)
                }
                SolverTopic.PERCENTAGE -> {
                    val v1 = parseBnToDouble(percentVal1) ?: 0.0
                    val v2 = parseBnToDouble(percentVal2) ?: 0.0
                    val type = when (percentageSubType) {
                        0 -> MathProblemSolverEngine.PercentageType.OF_NUMBER
                        1 -> MathProblemSolverEngine.PercentageType.WHAT_PERCENT_OF
                        2 -> MathProblemSolverEngine.PercentageType.INCREASE_DECREASE
                        else -> MathProblemSolverEngine.PercentageType.SUCCESSIVE
                    }
                    MathProblemSolverEngine.solvePercentage(type, v1, v2, selectedMode)
                }
                SolverTopic.RATIO_PROPORTION -> {
                    val tot = parseBnToDouble(ratioTotal) ?: 0.0
                    val r1 = parseBnToDouble(ratioR1) ?: 1.0
                    val r2 = parseBnToDouble(ratioR2) ?: 1.0
                    val r3 = parseBnToDouble(ratioR3)
                    val type = when (ratioSubType) {
                        0 -> MathProblemSolverEngine.RatioType.DIVIDE_QUANTITY
                        1 -> MathProblemSolverEngine.RatioType.FOURTH_PROPORTIONAL
                        else -> MathProblemSolverEngine.RatioType.SIMPLIFY
                    }
                    MathProblemSolverEngine.solveRatio(type, tot, r1, r2, r3, selectedMode)
                }
                SolverTopic.PROFIT_LOSS -> {
                    val cp = parseBnToDouble(plCp)
                    val sp = parseBnToDouble(plSp)
                    val pct = parseBnToDouble(plPercent)
                    val mp = parseBnToDouble(plMp)
                    val type = when (profitLossSubType) {
                        0 -> MathProblemSolverEngine.ProfitLossType.FIND_PROFIT_LOSS_PERCENT
                        1 -> MathProblemSolverEngine.ProfitLossType.FIND_SP_FROM_CP_AND_PERCENT
                        2 -> MathProblemSolverEngine.ProfitLossType.FIND_CP_FROM_SP_AND_PERCENT
                        else -> MathProblemSolverEngine.ProfitLossType.DISCOUNT_MARKED_PRICE
                    }
                    MathProblemSolverEngine.solveProfitLoss(type, cp, sp, pct, mp, selectedMode)
                }
                SolverTopic.AVERAGE -> {
                    val nums = averageInput.split(",", " ", "،")
                        .mapNotNull { parseBnToDouble(it.trim()) }
                    MathProblemSolverEngine.solveAverage(nums, selectedMode)
                }
                SolverTopic.SIMPLE_INTEREST -> {
                    val p = parseBnToDouble(siP)
                    val r = parseBnToDouble(siR)
                    val t = parseBnToDouble(siT)
                    val i = parseBnToDouble(siI)
                    if (interestMode == InterestCalculationMode.COMPOUND) {
                        MathProblemSolverEngine.solveCompoundInterest(
                            p = p ?: 1000.0,
                            r = r ?: 10.0,
                            t = t ?: 2.0,
                            m = compoundingFreq,
                            mode = selectedMode
                        )
                    } else {
                        MathProblemSolverEngine.solveSimpleInterest(p, r, t, i, null, selectedMode)
                    }
                }
                SolverTopic.TIME_AND_WORK -> {
                    val a = parseBnToDouble(workTimeA) ?: 10.0
                    val b = parseBnToDouble(workTimeB) ?: 15.0
                    val c = parseBnToDouble(workTimeC)
                    val type = when (workSubType) {
                        0 -> MathProblemSolverEngine.WorkType.TWO_PERSONS
                        1 -> MathProblemSolverEngine.WorkType.THREE_PERSONS
                        else -> MathProblemSolverEngine.WorkType.PIPE_CISTERN
                    }
                    MathProblemSolverEngine.solveTimeAndWork(type, a, b, c, selectedMode)
                }
                SolverTopic.SPEED_DISTANCE_TIME -> {
                    val s = parseBnToDouble(sdtSpeed)
                    val d = parseBnToDouble(sdtDistance)
                    val t = parseBnToDouble(sdtTime)
                    MathProblemSolverEngine.solveSpeedDistanceTime(s, d, t, selectedMode)
                }
                SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> {
                    val num = parseBnToLong(divNumber) ?: 1234L
                    MathProblemSolverEngine.solveDivisibility(num, divDivisor, selectedMode)
                }
                SolverTopic.HCF_LCM -> {
                    val nums = hcfLcmInput.split(",", " ", "،")
                        .mapNotNull { parseBnToLong(it.trim()) }
                    MathProblemSolverEngine.solveHcfLcm(nums, selectedMode)
                }
                SolverTopic.SQUARES_CUBES_ROOTS -> {
                    val n = parseBnToDouble(sqCubeNumber) ?: 16.0
                    val op = when (sqCubeOp) {
                        0 -> MathProblemSolverEngine.SquareCubeOp.SQUARE
                        1 -> MathProblemSolverEngine.SquareCubeOp.SQUARE_ROOT
                        2 -> MathProblemSolverEngine.SquareCubeOp.CUBE
                        else -> MathProblemSolverEngine.SquareCubeOp.CUBE_ROOT
                    }
                    MathProblemSolverEngine.solveSquareCube(n, op, selectedMode)
                }
                SolverTopic.LINEAR_EQUATIONS -> {
                    MathProblemSolverEngine.solveLinearEquation(linearEqInput, selectedMode)
                }
                SolverTopic.QUADRATIC_EQUATIONS -> {
                    val a = parseBnToDouble(quadA) ?: 1.0
                    val b = parseBnToDouble(quadB) ?: -5.0
                    val c = parseBnToDouble(quadC) ?: 6.0
                    MathProblemSolverEngine.solveQuadraticEquation(a, b, c, selectedMode, quadRelation)
                }
                SolverTopic.FACTORIZATION -> {
                    MathProblemSolverEngine.solveFactorization(factorInput, selectedMode)
                }
                SolverTopic.SIMPLIFICATION -> {
                    MathProblemSolverEngine.solveSimplification(simplifyInput, selectedMode)
                }
            }
        } catch (e: Exception) {
            SolverResult(
                topic = selectedTopic,
                mode = selectedMode,
                originalProblemBn = "সমস্যা বিশ্লেষণ",
                finalAnswerBn = "ত্রুটি",
                isSuccess = false,
                errorMessageBn = "ইনপুট মান সঠিক নয়। অনুগ্রহ করে সংখ্যা বা রাশি যাচাই করুন।"
            )
        }
        learningSessionHolder.syncForResult(solverResult)
    }

    LaunchedEffect(selectedTopic) {
        if (skipNextAutoSolve) {
            skipNextAutoSolve = false
        } else {
            solveCurrentTopic()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        MathTopAppBar(
            title = "গণিত সমাধানকারী",
            subtitle = "ধাপভিত্তিক বাংলা সমাধান • ১৬টি বিষয়",
            onBackClick = { viewModel.navigateBack() }
        )

        val solverListState = rememberManagedLazyListState(viewModel, "problem_solver:main:${selectedTopic.name}")
        val topicsRowState = rememberManagedLazyListState(viewModel, "problem_solver:topics_row")

        LazyColumn(
            state = solverListState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("solver_content_list"),
            contentPadding = MathSpacing.screenListContentPadding(),
            verticalArrangement = Arrangement.spacedBy(SolverDesignTokens.sectionSpacing)
        ) {
            // A. Modern Workspace Header
            item {
                SolverWorkspaceHeader()
            }

            // B. 3-Mode Segmented Control (Learning / Quick / BCS)
            item {
                LearningModeSelector(
                    currentMode = selectedMode,
                    onModeSelected = { mode ->
                        if (selectedMode != mode) {
                            selectedMode = mode
                            if (solverResult != null) {
                                solveCurrentTopic()
                            }
                        }
                    }
                )
            }

            // C. Categorized Topic Selection System across all 16 SolverTopics
            item {
                TopicSelectorCarousel(
                    currentTopic = selectedTopic,
                    currentCategory = selectedCategory,
                    listState = topicsRowState,
                    onCategorySelected = { category ->
                        selectedCategory = category
                    },
                    onTopicSelected = { topic ->
                        if (selectedTopic != topic) {
                            selectedTopic = topic
                        }
                    }
                )
            }

            // D. Topic-Specific Input Panel with Validation & Reset
            item {
                TopicInputCard(
                    topic = selectedTopic,
                    arithmeticExpr = arithmeticExpr,
                    onArithmeticChange = { arithmeticExpr = it },
                    fractionNum1 = fractionNum1,
                    fractionDen1 = fractionDen1,
                    fractionOp = fractionOp,
                    fractionNum2 = fractionNum2,
                    fractionDen2 = fractionDen2,
                    onFractionChange = { n1, d1, op, n2, d2 ->
                        fractionNum1 = n1
                        fractionDen1 = d1
                        fractionOp = op
                        fractionNum2 = n2
                        fractionDen2 = d2
                    },
                    percentageSubType = percentageSubType,
                    percentVal1 = percentVal1,
                    percentVal2 = percentVal2,
                    onPercentageChange = { sub, v1, v2 ->
                        percentageSubType = sub
                        percentVal1 = v1
                        percentVal2 = v2
                    },
                    ratioSubType = ratioSubType,
                    ratioTotal = ratioTotal,
                    ratioR1 = ratioR1,
                    ratioR2 = ratioR2,
                    ratioR3 = ratioR3,
                    onRatioChange = { sub, tot, r1, r2, r3 ->
                        ratioSubType = sub
                        ratioTotal = tot
                        ratioR1 = r1
                        ratioR2 = r2
                        ratioR3 = r3
                    },
                    profitLossSubType = profitLossSubType,
                    plCp = plCp,
                    plSp = plSp,
                    plPercent = plPercent,
                    plMp = plMp,
                    onProfitLossChange = { sub, cp, sp, pct, mp ->
                        profitLossSubType = sub
                        plCp = cp
                        plSp = sp
                        plPercent = pct
                        plMp = mp
                    },
                    averageInput = averageInput,
                    onAverageChange = { averageInput = it },
                    interestMode = interestMode,
                    compoundingFreq = compoundingFreq,
                    onInterestModeChange = { m, freq ->
                        interestMode = m
                        compoundingFreq = freq
                    },
                    siP = siP,
                    siR = siR,
                    siT = siT,
                    siI = siI,
                    onInterestChange = { p, r, t, i ->
                        siP = p
                        siR = r
                        siT = t
                        siI = i
                    },
                    workSubType = workSubType,
                    workTimeA = workTimeA,
                    workTimeB = workTimeB,
                    workTimeC = workTimeC,
                    onWorkChange = { sub, a, b, c ->
                        workSubType = sub
                        workTimeA = a
                        workTimeB = b
                        workTimeC = c
                    },
                    sdtSpeed = sdtSpeed,
                    sdtDistance = sdtDistance,
                    sdtTime = sdtTime,
                    onSdtChange = { s, d, t ->
                        sdtSpeed = s
                        sdtDistance = d
                        sdtTime = t
                    },
                    divNumber = divNumber,
                    divDivisor = divDivisor,
                    onDivChange = { n, d ->
                        divNumber = n
                        divDivisor = d
                    },
                    hcfLcmInput = hcfLcmInput,
                    onHcfLcmChange = { hcfLcmInput = it },
                    sqCubeNumber = sqCubeNumber,
                    sqCubeOp = sqCubeOp,
                    onSqCubeChange = { n, op ->
                        sqCubeNumber = n
                        sqCubeOp = op
                    },
                    linearEqInput = linearEqInput,
                    onLinearEqChange = { linearEqInput = it },
                    quadA = quadA,
                    quadB = quadB,
                    quadC = quadC,
                    quadRelation = quadRelation,
                    onQuadChange = { a, b, c ->
                        quadA = a
                        quadB = b
                        quadC = c
                    },
                    onQuadRelationChange = { quadRelation = it },
                    factorInput = factorInput,
                    onFactorChange = { factorInput = it },
                    simplifyInput = simplifyInput,
                    onSimplifyChange = { simplifyInput = it },
                    onResetClick = { resetCurrentTopicInputs() },
                    onSolveClick = {
                        solveCurrentTopic()
                        solverResult?.let { res ->
                            if (res.isSuccess) {
                                viewModel.saveSolverResultToHistory(
                                    topic = selectedTopic,
                                    inputs = mapOf("problem" to res.originalProblemBn),
                                    result = res
                                )
                            }
                        }
                    }
                )
            }

            // E. Solution Output Display (Mode-Adapted Workspace)
            val result = solverResult
            if (result != null) {
                item {
                    SolutionDisplayCard(
                        result = result,
                        isBookmarked = bookmarks.any { it.id == "solver_${result.topic.id}_${result.originalProblemBn.hashCode()}" },
                        sessionHolder = learningSessionHolder,
                        onToggleBookmark = {
                            val bookmarkId = "solver_${result.topic.id}_${result.originalProblemBn.hashCode()}"
                            val bookmark = BookmarkEntity(
                                id = bookmarkId,
                                category = "SOLVER",
                                titleBn = result.topic.titleBn,
                                subtitleBn = result.originalProblemBn,
                                detailBn = result.finalAnswerBn
                            )
                            viewModel.toggleBookmark(bookmark)
                            Toast.makeText(
                                context,
                                if (bookmarks.any { it.id == bookmarkId }) "বুকমার্ক মুছে ফেলা হয়েছে" else "বুকমার্কে সংরক্ষিত হয়েছে",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onCopySolution = {
                            val sb = StringBuilder()
                            sb.append("সমস্যা: ${result.originalProblemBn}\n")
                            result.formulaUsedBn?.let { sb.append("সূত্র: $it\n") }
                            sb.append("\nধাপসমূহ:\n")
                            result.steps.forEach { s ->
                                sb.append("ধাপ ${BengaliNumberFormatter.toBn(s.stepIndex)}: ${s.titleBn}\n")
                                sb.append("হিসাব: ${s.expressionOrMathBn}\n")
                                sb.append("ব্যাখ্যা: ${s.explanationBn}\n\n")
                            }
                            sb.append("চূড়ান্ত উত্তর: ${result.finalAnswerBn}\n")
                            result.verificationBn?.let { sb.append("$it\n") }
                            result.bcsShortcutBn?.let { sb.append("$it\n") }

                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Math Solution", sb.toString())
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "সম্পূর্ণ সমাধান ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        onTrySimilar = {
                            val inputs = MathProblemSolverEngine.generateSimilarInputs(selectedTopic)
                            when (selectedTopic) {
                                SolverTopic.BASIC_ARITHMETIC -> arithmeticExpr = inputs["expression"] ?: arithmeticExpr
                                SolverTopic.FRACTIONS_DECIMALS -> {
                                    fractionNum1 = BengaliNumberFormatter.toBn(inputs["num1"] ?: "2")
                                    fractionDen1 = BengaliNumberFormatter.toBn(inputs["den1"] ?: "3")
                                    fractionOp = inputs["op"] ?: "+"
                                    fractionNum2 = BengaliNumberFormatter.toBn(inputs["num2"] ?: "1")
                                    fractionDen2 = BengaliNumberFormatter.toBn(inputs["den2"] ?: "4")
                                }
                                SolverTopic.PERCENTAGE -> {
                                    percentVal1 = BengaliNumberFormatter.toBn(inputs["val1"] ?: "15")
                                    percentVal2 = BengaliNumberFormatter.toBn(inputs["val2"] ?: "500")
                                }
                                SolverTopic.RATIO_PROPORTION -> {
                                    ratioTotal = BengaliNumberFormatter.toBn(inputs["total"] ?: "1200")
                                    ratioR1 = BengaliNumberFormatter.toBn(inputs["r1"] ?: "2")
                                    ratioR2 = BengaliNumberFormatter.toBn(inputs["r2"] ?: "3")
                                }
                                SolverTopic.PROFIT_LOSS -> {
                                    plCp = BengaliNumberFormatter.toBn(inputs["cp"] ?: "400")
                                    plSp = BengaliNumberFormatter.toBn(inputs["sp"] ?: "480")
                                }
                                SolverTopic.AVERAGE -> averageInput = inputs["numbers"] ?: averageInput
                                SolverTopic.SIMPLE_INTEREST -> {
                                    siP = BengaliNumberFormatter.toBn(inputs["p"] ?: "5000")
                                    siR = BengaliNumberFormatter.toBn(inputs["r"] ?: "10")
                                    siT = BengaliNumberFormatter.toBn(inputs["t"] ?: "3")
                                    siI = ""
                                }
                                SolverTopic.TIME_AND_WORK -> {
                                    workTimeA = BengaliNumberFormatter.toBn(inputs["timeA"] ?: "10")
                                    workTimeB = BengaliNumberFormatter.toBn(inputs["timeB"] ?: "15")
                                }
                                SolverTopic.SPEED_DISTANCE_TIME -> {
                                    sdtDistance = BengaliNumberFormatter.toBn(inputs["distance"] ?: "120")
                                    sdtTime = BengaliNumberFormatter.toBn(inputs["time"] ?: "2")
                                    sdtSpeed = ""
                                }
                                SolverTopic.NUMBER_SYSTEM_DIVISIBILITY -> {
                                    divNumber = BengaliNumberFormatter.toBn(inputs["number"] ?: "1248")
                                    divDivisor = inputs["divisor"]?.toIntOrNull() ?: 4
                                }
                                SolverTopic.HCF_LCM -> hcfLcmInput = BengaliNumberFormatter.toBn(inputs["numbers"] ?: "24, 36")
                                SolverTopic.SQUARES_CUBES_ROOTS -> {
                                    sqCubeNumber = BengaliNumberFormatter.toBn(inputs["number"] ?: "144")
                                    sqCubeOp = 1
                                }
                                SolverTopic.LINEAR_EQUATIONS -> linearEqInput = BengaliNumberFormatter.toBn(inputs["equation"] ?: "3x + 9 = 27")
                                SolverTopic.QUADRATIC_EQUATIONS -> {
                                    quadA = BengaliNumberFormatter.toBn(inputs["a"] ?: "1")
                                    quadB = BengaliNumberFormatter.toBn(inputs["b"] ?: "-5")
                                    quadC = BengaliNumberFormatter.toBn(inputs["c"] ?: "6")
                                }
                                SolverTopic.FACTORIZATION -> factorInput = inputs["expression"] ?: "x² - 7x + 12"
                                SolverTopic.SIMPLIFICATION -> simplifyInput = inputs["expression"] ?: "√50"
                            }
                            solveCurrentTopic()
                            Toast.makeText(context, "অনুরূপ নতুন সমস্যা লোড হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        onSwitchToLearningMode = {
                            if (selectedMode != SolverMode.LEARNING) {
                                selectedMode = SolverMode.LEARNING
                                if (solverResult != null) {
                                    solveCurrentTopic()
                                }
                            }
                        },
                        viewModel = viewModel
                    )
                }

                // Simultaneous Linear System (2x2 & 3x3 — exclusively for LINEAR_EQUATIONS) & Cross-Module Bridge Card
                item {
                    SimultaneousLinearSystemAndBridgeCard(
                        selectedTopic = selectedTopic,
                        viewModel = viewModel
                    )
                }

                // Multi-Difficulty Similar Problems Generator
                item {
                    val similarProblems = remember(selectedTopic) {
                        SimilarProblemGenerator.generateSimilarProblems(selectedTopic)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("solver_similar_problems_card"),
                        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusHero),
                        colors = CardDefaults.cardColors(
                            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.secondaryContainer, 0.36f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "অনুরূপ অনুশীলন সমস্যা জেনারেটর (সহজ • মাঝারি • কঠিন)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            similarProblems.forEach { sim ->
                                var expandedSteps by remember(sim.problemStatementBn) { mutableStateOf(false) }
                                Surface(
                                    shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("similar_prob_${sim.difficulty.name}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                                            ) {
                                                Text(
                                                    text = sim.difficultyLabelBn,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            OutlinedButton(
                                                onClick = { expandedSteps = !expandedSteps },
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                                            ) {
                                                Text(
                                                    text = if (expandedSteps) "সমাধান লুকান" else "ধাপসহ উত্তর দেখুন",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = "সমস্যা: ${sim.problemStatementBn}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        AnimatedVisibility(
                                            visible = expandedSteps,
                                            enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                                            exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                sim.solution.steps.forEach { st ->
                                                    Text(
                                                        text = "${BengaliNumberFormatter.toBn(st.stepIndex)}. ${st.titleBn}: ${st.expressionOrMathBn} (${st.explanationBn})",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Text(
                                                    text = "চূড়ান্ত উত্তর: ${sim.solution.finalAnswerBn}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = SolverDesignTokens.successContentColor()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Saved Problem Solver History
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solver_history_card"),
                    shape = RoundedCornerShape(SolverDesignTokens.cardRadiusHero),
                    colors = CardDefaults.cardColors(
                        containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.35f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "সমাধান ইতিহাস",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সংরক্ষিত সমাধান ইতিহাস (${BengaliNumberFormatter.toBn(solverHistory.size)})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (solverHistory.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { viewModel.clearAllSolverHistory() },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                                        .testTag("btn_clear_solver_history")
                                ) {
                                    Text(
                                        text = "সব মুছুন",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (solverHistory.isEmpty()) {
                            Text(
                                text = "এখনো কোনো সমাধান ইতিহাসে সংরক্ষিত হয়নি। 'সমাধান করুন' বাটনে চাপলে স্বয়ংক্রিয়ভাবে এখানে সংরক্ষিত হবে।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            solverHistory.take(10).forEach { hist ->
                                Surface(
                                    shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val matchedTopic = SolverTopic.entries.firstOrNull { it.name == hist.topicId }
                                            if (matchedTopic != null) {
                                                if (matchedTopic != selectedTopic) {
                                                    skipNextAutoSolve = true
                                                    selectedTopic = matchedTopic
                                                }
                                                if (selectedCategory != SolverTopicCategory.ALL && matchedTopic !in selectedCategory.topics) {
                                                    selectedCategory = SolverTopicCategory.ALL
                                                }
                                            }
                                            val restoredSteps = SimilarProblemGenerator.deserializeSteps(hist.stepsJson)
                                            solverResult = SolverResult(
                                                topic = matchedTopic ?: selectedTopic,
                                                mode = selectedMode,
                                                originalProblemBn = hist.inputSummaryBn,
                                                formulaUsedBn = hist.formulaUsedBn.ifBlank { null },
                                                steps = restoredSteps,
                                                finalAnswerBn = hist.finalAnswerBn,
                                                verificationBn = hist.verificationBn.ifBlank { null },
                                                isSuccess = true
                                            )
                                            learningSessionHolder.syncForResult(solverResult)
                                        }
                                        .testTag("solver_history_item_${hist.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = hist.topicTitleBn,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = hist.inputSummaryBn,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "উত্তর: ${hist.finalAnswerBn}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SolverDesignTokens.successContentColor(),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteSolverHistoryItem(hist.id) },
                                            modifier = Modifier
                                                .size(SolverDesignTokens.minTouchTarget)
                                                .testTag("delete_solver_history_${hist.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "ইতিহাস থেকে মুছুন",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CATEGORIZED TOPIC SELECTION SYSTEM (ALL 16 TOPICS)
// -------------------------------------------------------------
@Composable
fun TopicSelectorCarousel(
    currentTopic: SolverTopic,
    currentCategory: SolverTopicCategory = SolverTopicCategory.ALL,
    listState: LazyListState = rememberLazyListState(),
    onCategorySelected: (SolverTopicCategory) -> Unit = {},
    onTopicSelected: (SolverTopic) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val visibleTopics = remember(currentCategory) { currentCategory.topics }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "গণিত বিষয় নির্বাচন (${BengaliNumberFormatter.toBn(SolverTopic.entries.size)}টি বিষয়)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.primaryContainer, 0.55f)
            ) {
                Text(
                    text = SolverTopicCategory.categoryOf(currentTopic).titleBn,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Category Filter Group (FlowRow wraps cleanly so no trailing chip is ever clipped)
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("solver_categories_row")
        ) {
            SolverTopicCategory.entries.forEach { category ->
                val isCatSelected = category == currentCategory
                FilterChip(
                    selected = isCatSelected,
                    onClick = {
                        onCategorySelected(category)
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    label = {
                        Text(
                            text = category.titleBn,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("solver_category_${category.id}")
                )
            }
        }

        // Scrollable Topic Chips Row (No trailing clipping, >= 48dp touch height)
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            modifier = Modifier.testTag("solver_topics_row")
        ) {
            items(visibleTopics, key = { it.id }) { topic ->
                val isSelected = topic == currentTopic
                val chipContainerColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        topic.primaryColor
                    } else {
                        MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.65f)
                    },
                    animationSpec = tween(SolverDesignTokens.EXPANSION_ANIM_MS),
                    label = "topic_chip_bg"
                )
                val chipContentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = chipContainerColor,
                    contentColor = chipContentColor,
                    border = if (isSelected) {
                        BorderStroke(1.5.dp, topic.primaryColor)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    },
                    tonalElevation = if (isSelected) 3.dp else 0.dp,
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .clip(RoundedCornerShape(12.dp))
                        .semantics { selected = isSelected }
                        .clickable { onTopicSelected(topic) }
                        .testTag("solver_topic_${topic.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = topic.getIcon(),
                            contentDescription = topic.titleBn,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = topic.titleBn,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// REFINED 3-MODE SEGMENTED SELECTOR
// -------------------------------------------------------------
@Composable
fun LearningModeSelector(
    currentMode: SolverMode,
    onModeSelected: (SolverMode) -> Unit
) {
    val activeMeta = solverModeUiMeta(currentMode)
    ElevatedCard(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.55f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("learning_mode_card")
    ) {
        Column(
            modifier = Modifier.padding(SolverDesignTokens.cardPaddingCompact),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "সমাধানের মোড নির্বাচন করুন",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SolverMode.entries.forEach { mode ->
                        val isSelected = mode == currentMode
                        val meta = solverModeUiMeta(mode)
                        val segmentBg by animateColorAsState(
                            targetValue = if (isSelected) {
                                SolverDesignTokens.modeAccentColor(mode)
                            } else {
                                Color.Transparent
                            },
                            animationSpec = tween(SolverDesignTokens.EXPANSION_ANIM_MS),
                            label = "mode_segment_bg"
                        )
                        val segmentFg by animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            animationSpec = tween(SolverDesignTokens.EXPANSION_ANIM_MS),
                            label = "mode_segment_fg"
                        )
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = segmentBg,
                            contentColor = segmentFg,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .heightIn(min = SolverDesignTokens.segmentMinHeight)
                                .clip(RoundedCornerShape(9.dp))
                                .semantics { selected = isSelected }
                                .clickable(
                                    role = Role.Tab,
                                    onClick = { onModeSelected(mode) }
                                )
                                .testTag("solver_mode_${mode.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = meta.icon,
                                    contentDescription = meta.labelBn,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = meta.labelBn,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = meta.subtitleBn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = segmentFg.copy(alpha = if (isSelected) 0.9f else 0.75f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = activeMeta.detailHintBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// -------------------------------------------------------------
// MODE-DIFFERENTIATED SOLUTION DISPLAY CARD
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SolutionDisplayCard(
    result: SolverResult,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onCopySolution: () -> Unit,
    onTrySimilar: () -> Unit,
    onSwitchToLearningMode: () -> Unit = {},
    viewModel: MathViewModel? = null,
    sessionHolder: SolverLearningSessionHolder? = null
) {
    val problemIdentity = if (result.isSuccess) SolverProblemIdentity.from(result) else null
    val standaloneSessionHolder = remember(problemIdentity) {
        SolverLearningSessionHolder(problemIdentity)
    }
    val activeSessionHolder = sessionHolder ?: standaloneSessionHolder

    if (!result.isSuccess) {
        Card(
            shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("solver_error_card")
        ) {
            Column(modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "ত্রুটি",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ইনপুট বা গাণিতিক ত্রুটি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.errorMessageBn ?: "ইনপুট মান সঠিক নয়।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (result.mode) {
            SolverMode.LEARNING -> {
                // 1. Problem Understanding & Prerequisites
                result.problemUnderstanding?.let { understanding ->
                    ProblemUnderstandingAndPrerequisiteCard(understanding = understanding)
                }

                // 2. Formula Card + Breakdown
                result.formulaUsedBn?.let { formula ->
                    SolutionFormulaCard(formula = formula, mode = result.mode)
                }
                result.formulaBreakdown?.let { breakdown ->
                    FormulaBreakdownDetailCard(breakdown = breakdown)
                }

                // 3. Interactive Step-by-Step Timeline
                SolutionStepsSection(
                    steps = result.steps,
                    mode = result.mode,
                    sessionHolder = activeSessionHolder,
                    onExpandAll = { activeSessionHolder.expandAll(result.steps) },
                    onCollapseAll = { activeSessionHolder.collapseAll(result.steps) },
                    onStepSelfCheckVerified = {
                        activeSessionHolder.learningState = SolverLearningState.INDEPENDENT_PRACTICE_VERIFIED
                    }
                )

                // 4. Inline Visual Diagram
                result.visualModelSpec?.let { spec ->
                    InlineSolverVisualCard(visualSpec = spec)
                }

                // 5. Final Answer Hero Banner + Verification
                SolutionAnswerHeroBanner(
                    result = result,
                    isBookmarked = isBookmarked,
                    onToggleBookmark = onToggleBookmark,
                    onCopySolution = onCopySolution,
                    onSwitchToLearningMode = onSwitchToLearningMode
                )
                result.verificationBn?.let { verification ->
                    SolutionVerificationCard(verification = verification)
                }

                // 6. Shortcuts & Common Traps
                result.bcsShortcutBn?.let { shortcut ->
                    SolutionBcsShortcutCard(shortcut = shortcut, examTip = result.bcsExamTipBn)
                }
                result.shortcutDetails?.let { sd ->
                    ShortcutAndTrapCard(shortcutDetails = sd)
                }

                // 7. Learner Confidence & Self-Check Progress
                LearnerFeedbackStatusRow(
                    learningState = activeSessionHolder.learningState,
                    onStateChanged = { activeSessionHolder.learningState = it },
                    onShowSimpleExplanation = { activeSessionHolder.expandAll(result.steps) },
                    onTrySimilar = onTrySimilar
                )

                // 8. Practice Similar Question
                SolutionTrySimilarCard(onTrySimilar = onTrySimilar)
            }

            SolverMode.QUICK -> {
                // 1. Result-First Hero Banner with Learning Mode Bridge
                SolutionAnswerHeroBanner(
                    result = result,
                    isBookmarked = isBookmarked,
                    onToggleBookmark = onToggleBookmark,
                    onCopySolution = onCopySolution,
                    onSwitchToLearningMode = onSwitchToLearningMode
                )

                // 2. Main Formula
                result.formulaUsedBn?.let { formula ->
                    SolutionFormulaCard(formula = formula, mode = result.mode)
                }

                // 3. Compact Essential Steps
                SolutionStepsSection(
                    steps = result.steps,
                    mode = result.mode,
                    sessionHolder = activeSessionHolder,
                    onExpandAll = {},
                    onCollapseAll = {},
                    onStepSelfCheckVerified = {}
                )

                // 4. Verification Summary
                result.verificationBn?.let { verification ->
                    SolutionVerificationCard(verification = verification)
                }

                // 5. Try Similar
                SolutionTrySimilarCard(onTrySimilar = onTrySimilar)
            }

            SolverMode.BCS -> {
                // 1. Hero Answer Banner (keeps copy & bookmark immediately accessible)
                SolutionAnswerHeroBanner(
                    result = result,
                    isBookmarked = isBookmarked,
                    onToggleBookmark = onToggleBookmark,
                    onCopySolution = onCopySolution,
                    onSwitchToLearningMode = onSwitchToLearningMode
                )

                // 2. Exam Formula
                result.formulaUsedBn?.let { formula ->
                    SolutionFormulaCard(formula = formula, mode = result.mode)
                }

                // 3. Exam Shortcut & Common Trap Cards
                result.bcsShortcutBn?.let { shortcut ->
                    SolutionBcsShortcutCard(shortcut = shortcut, examTip = result.bcsExamTipBn)
                }
                result.shortcutDetails?.let { sd ->
                    ShortcutAndTrapCard(shortcutDetails = sd)
                }

                // 4. BPSC Syllabus Mapping & Cross-Module Companion Navigator
                result.bcsSyllabusMapping?.let { bcsMap ->
                    BcsSyllabusAndNavigatorCard(mapping = bcsMap, viewModel = viewModel)
                }

                // 5. Compact Calculation Verification Steps
                SolutionStepsSection(
                    steps = result.steps,
                    mode = result.mode,
                    sessionHolder = activeSessionHolder,
                    onExpandAll = {},
                    onCollapseAll = {},
                    onStepSelfCheckVerified = {}
                )
                result.verificationBn?.let { verification ->
                    SolutionVerificationCard(verification = verification)
                }

                // 6. Practice Similar Question
                SolutionTrySimilarCard(onTrySimilar = onTrySimilar)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SolutionAnswerHeroBanner(
    result: SolverResult,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onCopySolution: () -> Unit,
    onSwitchToLearningMode: () -> Unit
) {
    val containerColor = MathSpacing.blendedContainerColor(
        SolverDesignTokens.modeContainerColor(result.mode),
        0.72f
    )
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, SolverDesignTokens.modeAccentColor(result.mode).copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_answer_banner")
    ) {
        Column(modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "চূড়ান্ত ফলাফল",
                        tint = SolverDesignTokens.modeAccentColor(result.mode),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "চূড়ান্ত ফলাফল",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SolverModeBadgePill(mode = result.mode)
                }

                Row {
                    IconButton(
                        onClick = onCopySolution,
                        modifier = Modifier
                            .size(SolverDesignTokens.minTouchTarget)
                            .testTag("solver_copy_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "সমাধান কপি করুন",
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(SolverDesignTokens.minTouchTarget)
                            .testTag("solver_bookmark_btn")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "বুকমার্ক সরান" else "বুকমার্ক করুন",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = result.originalProblemBn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = result.finalAnswerBn,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = SolverDesignTokens.modeAccentColor(result.mode)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SolverExactnessPill(isExactResult = result.isExactResult)
                        result.approximateResultBn?.let { approx ->
                            Text(
                                text = "দশমিক মান: $approx",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }

            if (result.mode == SolverMode.QUICK) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSwitchToLearningMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("btn_quick_to_learning_mode"),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "পূর্ণাঙ্গ লার্নিং ব্যাখ্যা",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "পূর্ণাঙ্গ লার্নিং ব্যাখ্যা দেখুন",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SolutionFormulaCard(
    formula: String,
    mode: SolverMode
) {
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStep),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.secondaryContainer, 0.55f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_formula_card")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Functions,
                        contentDescription = "সূত্র",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (mode == SolverMode.QUICK) "মূল সূত্র" else "ব্যবহৃত সূত্র ও নিয়মাবলী",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formula,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SolutionStepsSection(
    steps: List<SolutionStep>,
    mode: SolverMode,
    sessionHolder: SolverLearningSessionHolder,
    onExpandAll: () -> Unit,
    onCollapseAll: () -> Unit,
    onStepSelfCheckVerified: () -> Unit
) {
    if (steps.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    text = if (mode == SolverMode.QUICK) {
                        "সংক্ষিপ্ত হিসাবের ধাপ (${BengaliNumberFormatter.toBn(steps.size)}টি)"
                    } else {
                        "ধাপভিত্তিক সমাধান (${BengaliNumberFormatter.toBn(steps.size)}টি ধাপ)"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (mode == SolverMode.LEARNING) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onExpandAll,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .heightIn(min = SolverDesignTokens.minTouchTarget)
                            .testTag("btn_expand_all_steps")
                    ) {
                        Icon(
                            imageVector = Icons.Default.UnfoldMore,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সব খুলুন", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onCollapseAll,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .heightIn(min = SolverDesignTokens.minTouchTarget)
                            .testTag("btn_collapse_all_steps")
                    ) {
                        Icon(
                            imageVector = Icons.Default.UnfoldLess,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সব সংক্ষিপ্ত", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        steps.forEachIndexed { idx, step ->
            val hoistedState = if (mode == SolverMode.LEARNING) {
                sessionHolder.getStepState(step.stepIndex, mode)
            } else {
                null
            }
            InteractiveLearningStepCard(
                step = step,
                mode = mode,
                forceExpandAll = sessionHolder.forceExpandAllSteps,
                onStepSelfCheckVerified = onStepSelfCheckVerified,
                isLastStep = idx == steps.lastIndex,
                stepUiState = hoistedState,
                onStepUiStateChange = if (mode == SolverMode.LEARNING) {
                    { updated -> sessionHolder.updateStepState(step.stepIndex, mode) { updated } }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun SolutionVerificationCard(verification: String) {
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStep),
        colors = CardDefaults.cardColors(containerColor = SolverDesignTokens.successContainerColor()),
        border = BorderStroke(1.dp, SolverDesignTokens.successBorderColor()),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_verification_card")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "শুদ্ধি পরীক্ষা",
                tint = SolverDesignTokens.successContentColor(),
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "উত্তরের সত্যতা ও শুদ্ধি পরীক্ষা (Verification)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SolverDesignTokens.successContentColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = verification,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SolverDesignTokens.successBodyColor()
                )
            }
        }
    }
}

@Composable
private fun SolutionBcsShortcutCard(
    shortcut: String,
    examTip: String?
) {
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStep),
        colors = CardDefaults.cardColors(containerColor = SolverDesignTokens.warningContainerColor()),
        border = BorderStroke(1.dp, SolverDesignTokens.warningBorderColor()),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_bcs_card")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "বিসিএস শর্টকাট",
                tint = SolverDesignTokens.warningIconColor(),
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "বিসিএস ও চাকরির পরীক্ষার বিশেষ শর্টকাট টেকনিক",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SolverDesignTokens.warningTitleColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = shortcut,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SolverDesignTokens.warningBodyColor()
                )
                examTip?.let { tip ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "টিপস: $tip",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SolverDesignTokens.warningTitleColor()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SolutionTrySimilarCard(onTrySimilar: () -> Unit) {
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStep),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.55f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "অনুরূপ প্রশ্ন সমাধান করুন",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "এই নিয়মের আরেকটি প্রশ্ন অনুশীলন করে দক্ষতা যাচাই করুন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onTrySimilar,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                    .testTag("solver_try_similar_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "অনুরূপ প্রশ্ন",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "অনুরূপ প্রশ্ন অনুশীলন করুন",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// LEGACY-COMPATIBLE STEP CARD ITEM
// -------------------------------------------------------------
@Composable
fun StepCardItem(
    step: SolutionStep,
    mode: SolverMode
) {
    ElevatedCard(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStep),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = BengaliNumberFormatter.toBn(step.stepIndex),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = step.titleBn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = step.expressionOrMathBn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = step.explanationBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            step.whyThisStepBn?.let { why ->
                if (mode == SolverMode.LEARNING) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "কেন এই ধাপ?",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "কেন এই ধাপ: $why",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER PARSING FUNCTIONS
// -------------------------------------------------------------
fun parseBnToLong(s: String?): Long? {
    if (s.isNullOrBlank()) return null
    val ascii = toAscii(s.trim())
    return ascii.toLongOrNull()
}

/**
 * Normalizes and parses Bengali/ASCII decimal input.
 * Accepts '.' and unambiguous ',' as decimal separators (e.g. "২,৫" -> 2.5, "২.৫" -> 2.5, "-০,৫" -> -0.5).
 * Rejects ambiguous thousands separators such as "1,234" or "1,00,000".
 */
fun parseBnToDouble(s: String?): Double? {
    if (s.isNullOrBlank()) return null
    val raw = s.trim()

    val ascii = toAscii(raw).replace('،', ',').replace(" ", "")

    val sign = if (ascii.startsWith("-")) "-" else if (ascii.startsWith("+")) "+" else ""
    val body = if (sign.isNotEmpty()) ascii.substring(1) else ascii

    if (body.isEmpty()) return null

    if (body.contains(",") && body.contains(".")) {
        val thousandsDotRegex = Regex("""^\d{1,3}(,\d{3})+(\.\d+)?$""")
        if (thousandsDotRegex.matches(body)) {
            val normalized = body.replace(",", "")
            return "$sign$normalized".toDoubleOrNull()
        }
        return null
    }

    if (body.contains(",")) {
        val commaCount = body.count { it == ',' }
        if (commaCount > 1) {
            return null
        }

        val parts = body.split(",")
        val left = parts[0]
        val right = parts[1]

        if (right.isEmpty()) return null
        if ((left.isNotEmpty() && !left.all { it.isDigit() }) || !right.all { it.isDigit() }) {
            return null
        }

        if (left.isNotEmpty() && left != "0" && right.length == 3) {
            return null
        }

        val normalized = "${if (left.isEmpty()) "0" else left}.${right}"
        return "$sign$normalized".toDoubleOrNull()
    }

    return "$sign$body".toDoubleOrNull()
}

fun toAscii(s: String): String {
    val bnToAscii = mapOf(
        '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
        '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9'
    )
    val sb = StringBuilder()
    for (c in s) {
        sb.append(bnToAscii[c] ?: c)
    }
    return sb.toString()
}
