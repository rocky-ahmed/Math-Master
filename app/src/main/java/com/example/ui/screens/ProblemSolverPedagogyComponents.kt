package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.BcsSyllabusMapping
import com.example.math.BengaliNumberFormatter
import com.example.math.FormulaBreakdown
import com.example.math.ProblemUnderstanding
import com.example.math.SharedMathEngine
import com.example.math.ShortcutDetails
import com.example.math.SolutionStep
import com.example.math.SolverLearningState
import com.example.math.SolverMode
import com.example.math.SolverPedagogyEnricher
import com.example.math.SolverResult
import com.example.math.SolverTopic
import com.example.math.VisualDiagramType
import com.example.math.VisualModelSpec
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.rememberManagedUiState
import com.example.ui.theme.MathSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProblemUnderstandingAndPrerequisiteCard(
    understanding: ProblemUnderstanding
) {
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_problem_understanding_card")
    ) {
        Column(
            modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "প্রশ্নটি বুঝি",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "প্রশ্নটি বুঝি ও প্রয়োজনীয় ধারণা",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "সমাধান শুরুর আগে মূল তথ্য ও পূর্বধারণা",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (understanding.prerequisiteConceptBn.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                    color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.primaryContainer, 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "প্রয়োজনীয় পূর্বধারণা",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = understanding.prerequisiteConceptBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (understanding.summaryBn.isNotBlank()) {
                Text(
                    text = "• সারসংক্ষেপ: ${understanding.summaryBn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (understanding.givenItemsBn.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    understanding.givenItemsBn.forEach { (key, value) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = "$key: $value",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            if (understanding.requiredTargetBn.isNotBlank()) {
                Text(
                    text = "• নির্ণেয় লক্ষ্য: ${understanding.requiredTargetBn}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            understanding.realLifeContextBn?.takeIf { it.isNotBlank() }?.let { ctx ->
                Text(
                    text = "• বাস্তব উদাহরণ: $ctx",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FormulaBreakdownDetailCard(breakdown: FormulaBreakdown) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.secondaryContainer, 0.42f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_formula_breakdown_card")
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
                        imageVector = Icons.Default.Functions,
                        contentDescription = "সূত্রের ব্যবচ্ছেদ",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সূত্রের চলক পরিচিতি: ${breakdown.formulaTitleBn}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("btn_toggle_formula_breakdown")
                ) {
                    Text(
                        text = if (expanded) "সংক্ষিপ্ত" else "চলক ও ব্যাখ্যা",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    breakdown.variables.forEach { v ->
                        val valSuffix = v.givenValueBn?.takeIf { it.isNotBlank() }?.let { " = $it" } ?: ""
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = v.symbolBn,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${v.meaningBn}$valSuffix",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    if (breakdown.plainMeaningBn.isNotBlank()) {
                        Text(
                            text = "সহজ অর্থ: ${breakdown.plainMeaningBn}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (breakdown.whyApplicableBn.isNotBlank()) {
                        Text(
                            text = "কেন প্রযোজ্য: ${breakdown.whyApplicableBn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Immutable
data class SolverProblemIdentity(
    val topic: SolverTopic,
    val originalProblemBn: String,
    val finalAnswerBn: String
) {
    companion object {
        fun from(result: SolverResult): SolverProblemIdentity = SolverProblemIdentity(
            topic = result.topic,
            originalProblemBn = result.originalProblemBn,
            finalAnswerBn = result.finalAnswerBn
        )
    }
}

@Immutable
data class StepLearningUiState(
    val showSimple: Boolean = false,
    val showRuleAndWhy: Boolean = false,
    val showAnalogyAndTrap: Boolean = false,
    val showTryItYourself: Boolean = false,
    val hintLevel: Int = 0,
    val userAttempt: String = "",
    val attemptFeedbackBn: String? = null
) {
    companion object {
        fun defaultFor(
            stepIndex: Int,
            mode: SolverMode,
            forceExpandAll: Boolean?
        ): StepLearningUiState = StepLearningUiState(
            showSimple = forceExpandAll ?: false,
            showRuleAndWhy = forceExpandAll ?: (mode == SolverMode.LEARNING && stepIndex == 1),
            showAnalogyAndTrap = forceExpandAll ?: false,
            showTryItYourself = false,
            hintLevel = 0,
            userAttempt = "",
            attemptFeedbackBn = null
        )
    }
}

@Stable
class SolverLearningSessionHolder(
    initialIdentity: SolverProblemIdentity? = null
) {
    var activeIdentity by mutableStateOf(initialIdentity)
        private set

    var forceExpandAllSteps by mutableStateOf<Boolean?>(null)
    var learningState by mutableStateOf(SolverLearningState.WORKED_EXAMPLE_STUDIED)
    private val _stepStates = mutableStateMapOf<Int, StepLearningUiState>()

    fun syncForResult(result: SolverResult?) {
        if (result == null || !result.isSuccess) {
            clear()
            return
        }
        val nextIdentity = SolverProblemIdentity.from(result)
        if (activeIdentity != nextIdentity) {
            activeIdentity = nextIdentity
            forceExpandAllSteps = null
            learningState = SolverLearningState.WORKED_EXAMPLE_STUDIED
            _stepStates.clear()
        }
    }

    fun clear() {
        activeIdentity = null
        forceExpandAllSteps = null
        learningState = SolverLearningState.WORKED_EXAMPLE_STUDIED
        _stepStates.clear()
    }

    fun getStepState(stepIndex: Int, mode: SolverMode): StepLearningUiState {
        return _stepStates[stepIndex] ?: StepLearningUiState.defaultFor(
            stepIndex = stepIndex,
            mode = mode,
            forceExpandAll = forceExpandAllSteps
        )
    }

    fun updateStepState(
        stepIndex: Int,
        mode: SolverMode,
        transform: (StepLearningUiState) -> StepLearningUiState
    ) {
        val current = getStepState(stepIndex, mode)
        _stepStates[stepIndex] = transform(current)
    }

    fun expandAll(steps: List<SolutionStep>) {
        forceExpandAllSteps = true
        steps.forEach { step ->
            val current = _stepStates[step.stepIndex] ?: StepLearningUiState.defaultFor(
                stepIndex = step.stepIndex,
                mode = SolverMode.LEARNING,
                forceExpandAll = true
            )
            _stepStates[step.stepIndex] = current.copy(
                showSimple = true,
                showRuleAndWhy = true,
                showAnalogyAndTrap = true
            )
        }
    }

    fun collapseAll(steps: List<SolutionStep>) {
        forceExpandAllSteps = false
        steps.forEach { step ->
            val current = _stepStates[step.stepIndex] ?: StepLearningUiState.defaultFor(
                stepIndex = step.stepIndex,
                mode = SolverMode.LEARNING,
                forceExpandAll = false
            )
            _stepStates[step.stepIndex] = current.copy(
                showSimple = false,
                showRuleAndWhy = false,
                showAnalogyAndTrap = false,
                showTryItYourself = false
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveLearningStepCard(
    step: SolutionStep,
    mode: SolverMode,
    forceExpandAll: Boolean?,
    onStepSelfCheckVerified: () -> Unit,
    isLastStep: Boolean = false,
    stepUiState: StepLearningUiState? = null,
    onStepUiStateChange: ((StepLearningUiState) -> Unit)? = null
) {
    // Preserve Step 1 default "why" selection on initial Learning Mode render for guided discovery
    // while keeping subsequent steps compact unless the user expands them.
    var localShowSimple by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(forceExpandAll ?: false)
    }
    var localShowRuleAndWhy by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(forceExpandAll ?: (mode == SolverMode.LEARNING && step.stepIndex == 1))
    }
    var localShowAnalogyAndTrap by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(forceExpandAll ?: false)
    }
    var localShowTryItYourself by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(false)
    }
    var localHintLevel by remember(step.stepIndex) { mutableIntStateOf(0) }
    var localUserAttempt by remember(step.stepIndex) { mutableStateOf("") }
    var localAttemptFeedbackBn by remember(step.stepIndex) { mutableStateOf<String?>(null) }

    val useHoisted = stepUiState != null && onStepUiStateChange != null
    val activeState = stepUiState ?: StepLearningUiState(
        showSimple = localShowSimple,
        showRuleAndWhy = localShowRuleAndWhy,
        showAnalogyAndTrap = localShowAnalogyAndTrap,
        showTryItYourself = localShowTryItYourself,
        hintLevel = localHintLevel,
        userAttempt = localUserAttempt,
        attemptFeedbackBn = localAttemptFeedbackBn
    )
    fun updateUiState(transform: (StepLearningUiState) -> StepLearningUiState) {
        val next = transform(activeState)
        if (useHoisted) {
            onStepUiStateChange?.invoke(next)
        } else {
            localShowSimple = next.showSimple
            localShowRuleAndWhy = next.showRuleAndWhy
            localShowAnalogyAndTrap = next.showAnalogyAndTrap
            localShowTryItYourself = next.showTryItYourself
            localHintLevel = next.hintLevel
            localUserAttempt = next.userAttempt
            localAttemptFeedbackBn = next.attemptFeedbackBn
        }
    }

    val showSimple = activeState.showSimple
    val showRuleAndWhy = activeState.showRuleAndWhy
    val showAnalogyAndTrap = activeState.showAnalogyAndTrap
    val showTryItYourself = activeState.showTryItYourself
    val hintLevel = activeState.hintLevel
    val userAttempt = activeState.userAttempt
    val attemptFeedbackBn = activeState.attemptFeedbackBn

    val beforeStr = step.expressionBeforeBn ?: ""
    val afterStr = step.expressionAfterBn ?: ""
    val anyDetailOpen = showSimple || showRuleAndWhy || showAnalogyAndTrap || showTryItYourself

    if (mode != SolverMode.LEARNING) {
        // Streamlined compact summary step card for QUICK and BCS modes
        Surface(
            shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStep),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("solver_step_card_${step.stepIndex}")
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SolverDesignTokens.modeAccentColor(mode),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = BengaliNumberFormatter.toBn(step.stepIndex),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = step.titleBn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.55f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = step.expressionOrMathBn,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
                Text(
                    text = step.explanationBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    // LEARNING MODE: Guided Step Card with Timeline Header, Independent Chips & Self-Check Workspace
    ElevatedCard(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStep),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_step_card_${step.stepIndex}")
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
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(SolverDesignTokens.stepBadgeSize)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = BengaliNumberFormatter.toBn(step.stepIndex),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ধাপ ${BengaliNumberFormatter.toBn(step.stepIndex)}${if (isLastStep) " (চূড়ান্ত ধাপ)" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = step.titleBn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                IconButton(
                    onClick = {
                        val target = !anyDetailOpen
                        updateUiState {
                            it.copy(
                                showSimple = target,
                                showRuleAndWhy = target,
                                showAnalogyAndTrap = target,
                                showTryItYourself = if (!target) false else it.showTryItYourself
                            )
                        }
                    },
                    modifier = Modifier.size(SolverDesignTokens.minTouchTarget)
                ) {
                    Icon(
                        imageVector = if (anyDetailOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (anyDetailOpen) "ধাপের বিস্তারিত সংক্ষিপ্ত করুন" else "ধাপের বিস্তারিত দেখুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.55f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = step.expressionOrMathBn,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (beforeStr.isNotBlank() && afterStr.isNotBlank() && beforeStr != afterStr) {
                        Text(
                            text = "রূপান্তর: $beforeStr ➜ $afterStr",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = step.explanationBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = showSimple,
                    onClick = { updateUiState { it.copy(showSimple = !it.showSimple) } },
                    label = {
                        Text(
                            text = "সহজ ব্যাখ্যা",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (showSimple) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("step_${step.stepIndex}_chip_simple")
                )
                FilterChip(
                    selected = showRuleAndWhy,
                    onClick = { updateUiState { it.copy(showRuleAndWhy = !it.showRuleAndWhy) } },
                    label = {
                        Text(
                            text = "কেন এই ধাপ?",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (showRuleAndWhy) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("step_${step.stepIndex}_chip_why")
                )
                FilterChip(
                    selected = showAnalogyAndTrap,
                    onClick = { updateUiState { it.copy(showAnalogyAndTrap = !it.showAnalogyAndTrap) } },
                    label = {
                        Text(
                            text = "উদাহরণ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (showAnalogyAndTrap) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("step_${step.stepIndex}_chip_analogy")
                )
                FilterChip(
                    selected = showTryItYourself,
                    onClick = { updateUiState { it.copy(showTryItYourself = !it.showTryItYourself) } },
                    label = {
                        Text(
                            text = "নিজে চেষ্টা করুন",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (showTryItYourself) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("step_${step.stepIndex}_chip_try")
                )
            }

            AnimatedVisibility(
                visible = showSimple && step.simpleExplanationBn.isNotBlank(),
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.primaryContainer, 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "সহজ ভাষায়: ${step.simpleExplanationBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = showRuleAndWhy,
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                val whyText = step.whyThisStepBn ?: step.detailedRuleBn ?: ""
                if (whyText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.secondaryContainer, 0.48f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "কেন এই ধাপ?",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "কেন এই ধাপ: $whyText",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showAnalogyAndTrap,
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                val analogy = step.conceptualAnalogyBn ?: ""
                val mistake = step.commonMistakeBn ?: ""
                if (analogy.isNotBlank() || mistake.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (analogy.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.tertiaryContainer, 0.45f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "বাস্তব উদাহরণ: $analogy",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        if (mistake.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SolverDesignTokens.mistakeContainerColor(),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.28f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = "সাধারণ ভুল সতর্কতা",
                                        tint = SolverDesignTokens.mistakeContentColor(),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "সাধারণ ভুল সতর্কতা: $mistake",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SolverDesignTokens.mistakeContentColor()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showTryItYourself,
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                val h1 = step.hint1Bn ?: ""
                val h2 = step.hint2Bn ?: ""
                Surface(
                    shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                    color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "ধাপটি নিজে চেষ্টা করুন",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "প্রগতিশীল ইঙ্গিত ও অনুশীলন",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    updateUiState {
                                        it.copy(hintLevel = if (it.hintLevel == 1) 0 else 1)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                                    .testTag("step_${step.stepIndex}_hint_1_btn")
                            ) {
                                Text(
                                    text = "ইঙ্গিত ১ (ধারণা)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    updateUiState { it.copy(hintLevel = 2) }
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                                    .testTag("step_${step.stepIndex}_hint_2_btn")
                            ) {
                                Text(
                                    text = "ইঙ্গিত ২ (পরবর্তী সূত্র)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (hintLevel >= 1 && h1.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "ইঙ্গিত ১: $h1",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        if (hintLevel >= 2 && h2.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "ইঙ্গিত ২: $h2",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = userAttempt,
                                onValueChange = { typed ->
                                    updateUiState { it.copy(userAttempt = typed) }
                                },
                                label = { Text("নিজে ধাপটির মান লিখুন", style = MaterialTheme.typography.labelSmall) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    val expected = (step.expectedStepValueBn ?: "").ifBlank { afterStr }
                                    val isMatch = isSolverStepAttemptCorrect(userAttempt, expected)
                                    if (isMatch) {
                                        updateUiState {
                                            it.copy(attemptFeedbackBn = "সঠিক! আপনি এই ধাপটি নিজে সমাধান করেছেন।")
                                        }
                                        onStepSelfCheckVerified()
                                    } else {
                                        updateUiState {
                                            it.copy(attemptFeedbackBn = "আবার চেষ্টা করুন। প্রত্যাশিত ফলাফল: $expected")
                                        }
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                            ) {
                                Text(
                                    text = "যাচাই",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        attemptFeedbackBn?.let { fb ->
                            val isCorrectFb = fb.startsWith("সঠিক")
                            Text(
                                text = fb,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrectFb) SolverDesignTokens.successContentColor() else SolverDesignTokens.warningTitleColor(),
                                modifier = Modifier
                                    .semantics { liveRegion = LiveRegionMode.Polite }
                                    .testTag("solver_step_attempt_feedback")
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Validates a learner's self-check step attempt against the expected step value.
 *
 * Strictly forbids arbitrary substring matching (`expected.contains(userAttempt)`), which previously
 * caused partial prefixes/substrings (e.g., "1", "12", "25" for "125") to be falsely accepted.
 *
 * Supported formats:
 * - Exact normalized numeric, fraction, decimal, ratio, percentage, or text equality via [SharedMathEngine.isAnswerCorrect].
 * - Bengali and English numeral equivalence and whitespace/formatting normalization.
 * - Complete RHS value matching when [expectedStepValue] is a labeled or assignment expression
 *   (e.g., "x = 125", "২৫ + ১৫ = ৪০", "রূপান্তর ➜ ১২৫", "উত্তর: ১২৫ টাকা") — compares the user attempt
 *   against the full extracted right-hand side via [SharedMathEngine.isAnswerCorrect].
 */
internal fun isSolverStepAttemptCorrect(
    userAttempt: String,
    expectedStepValue: String
): Boolean {
    val trimmedAttempt = userAttempt.trim()
    val trimmedExpected = expectedStepValue.trim()
    if (trimmedAttempt.isEmpty() || trimmedExpected.isEmpty()) return false

    // 1. Direct normalized comparison and mathematical equivalence via SharedMathEngine
    if (SharedMathEngine.isAnswerCorrect(trimmedAttempt, trimmedExpected)) {
        return true
    }

    // 2. If the user also typed an assignment/arrow/labeled expression (e.g. "x = 125"),
    // compare normalized compact algebraic forms or extracted RHS values only when both sides
    // represent the complete target value.
    val expectedRhsCandidates = extractCompleteStepRhsCandidates(trimmedExpected)
    for (candidate in expectedRhsCandidates) {
        if (SharedMathEngine.isAnswerCorrect(trimmedAttempt, candidate)) {
            return true
        }
    }

    // 3. If both user attempt and expected value are assignment equations (e.g., "x = 125" vs "x=১২৫"),
    // verify both LHS variable/label and RHS mathematical equivalence.
    if (trimmedAttempt.contains("=") && trimmedExpected.contains("=")) {
        val userLhs = trimmedAttempt.substringBefore("=").trim()
        val userRhs = trimmedAttempt.substringAfterLast("=").trim()
        val expLhs = trimmedExpected.substringBefore("=").trim()
        val expRhs = trimmedExpected.substringAfterLast("=").trim()
        if (userLhs.isNotEmpty() && userRhs.isNotEmpty() &&
            SharedMathEngine.normalizeAnswerToken(userLhs) == SharedMathEngine.normalizeAnswerToken(expLhs) &&
            SharedMathEngine.isAnswerCorrect(userRhs, expRhs)
        ) {
            return true
        }
    }

    return false
}

private fun extractCompleteStepRhsCandidates(expectedStepValue: String): List<String> {
    val candidates = mutableListOf<String>()
    if (expectedStepValue.contains("➜")) {
        val afterArrow = expectedStepValue.substringAfterLast("➜").trim()
        if (afterArrow.isNotEmpty()) {
            candidates.add(afterArrow)
        }
    }
    if (expectedStepValue.contains("=")) {
        val afterEquals = expectedStepValue.substringAfterLast("=").trim()
        if (afterEquals.isNotEmpty()) {
            candidates.add(afterEquals)
        }
    }
    // Support colon-prefixed labels (e.g. "উত্তর: ১২৫", "ফল: ১২৫") but NOT mathematical ratios (e.g. "৩:৪" or "3 : 4")
    if (expectedStepValue.contains(":")) {
        val beforeColon = expectedStepValue.substringBeforeLast(":").trim()
        val afterColon = expectedStepValue.substringAfterLast(":").trim()
        val beforeIsNumeric = BengaliNumberFormatter.toEnglishDigits(beforeColon).toDoubleOrNull() != null
        if (!beforeIsNumeric && afterColon.isNotEmpty()) {
            candidates.add(afterColon)
        }
    }
    return candidates.distinct()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InlineSolverVisualCard(visualSpec: VisualModelSpec) {
    var expanded by remember { mutableStateOf(true) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_inline_visual_card")
    ) {
        Column(modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ভিজ্যুয়াল ডায়াগ্রাম: ${visualSpec.titleBn}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    if (visualSpec.subtitleBn.isNotBlank()) {
                        Text(
                            text = visualSpec.subtitleBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("btn_toggle_inline_visual")
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (expanded) "চিত্র লুকান" else "চিত্র দেখুন",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val a11yText = visualSpec.accessibilitySummaryBn.ifBlank { visualSpec.titleBn }
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(78.dp)
                                    .semantics {
                                        contentDescription = a11yText
                                    }
                            ) {
                                val w = size.width
                                val h = size.height
                                when (visualSpec.diagramType) {
                                    VisualDiagramType.NUMBER_LINE,
                                    VisualDiagramType.INEQUALITY_NUMBER_LINE,
                                    VisualDiagramType.ALGEBRA_BALANCE -> {
                                        val midY = h * 0.55f
                                        drawLine(
                                            color = primaryColor,
                                            start = Offset(w * 0.08f, midY),
                                            end = Offset(w * 0.92f, midY),
                                            strokeWidth = 6f
                                        )
                                        for (i in 0..4) {
                                            val x = w * (0.12f + i * 0.19f)
                                            drawCircle(
                                                color = if (i == 2) secondaryColor else primaryColor,
                                                radius = if (i == 2) 10f else 6f,
                                                center = Offset(x, midY)
                                            )
                                        }
                                    }
                                    VisualDiagramType.PARABOLA_PLOT -> {
                                        val axisY = h * 0.65f
                                        drawLine(
                                            color = Color.Gray,
                                            start = Offset(w * 0.1f, axisY),
                                            end = Offset(w * 0.9f, axisY),
                                            strokeWidth = 3f
                                        )
                                        val path = Path().apply {
                                            moveTo(w * 0.2f, h * 0.15f)
                                            quadraticTo(w * 0.5f, h * 0.95f, w * 0.8f, h * 0.15f)
                                        }
                                        drawPath(path = path, color = primaryColor, style = Stroke(width = 5f))
                                    }
                                    else -> {
                                        val rawVals: List<Double> = visualSpec.primaryValues.ifEmpty { listOf(45.0, 55.0) }
                                        val floatVals: List<Float> = rawVals.map { it.toFloat().coerceAtLeast(1f) }
                                        val total: Float = floatVals.sum().coerceAtLeast(1f)
                                        var startX = w * 0.05f
                                        val barW = w * 0.9f
                                        val colors = listOf(primaryColor, secondaryColor, tertiaryColor)
                                        floatVals.forEachIndexed { idx: Int, v: Float ->
                                            val segW = (v / total) * barW
                                            drawRoundRect(
                                                color = colors[idx % colors.size],
                                                topLeft = Offset(startX, h * 0.25f),
                                                size = Size((segW - 4f).coerceAtLeast(8f), h * 0.5f),
                                                cornerRadius = CornerRadius(8f, 8f)
                                            )
                                            startX += segW
                                        }
                                    }
                                }
                            }
                            if (visualSpec.labelsBn.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    visualSpec.labelsBn.take(4).forEach { lbl ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                        ) {
                                            Text(
                                                text = lbl,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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

@Composable
fun ShortcutAndTrapCard(shortcutDetails: ShortcutDetails) {
    var showDerivation by remember { mutableStateOf(false) }
    val containerBg = SolverDesignTokens.warningContainerColor()
    val titleColor = SolverDesignTokens.warningTitleColor()
    val bodyColor = SolverDesignTokens.warningBodyColor()
    val positiveColor = SolverDesignTokens.successContentColor()
    val avoidColor = SolverDesignTokens.mistakeContentColor()
    val trapColor = SolverDesignTokens.warningIconColor()

    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(1.dp, SolverDesignTokens.warningBorderColor()),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_shortcut_details_card")
    ) {
        Column(
            modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "শর্টকাট কৌশল",
                        tint = trapColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = shortcutDetails.shortcutTitleBn,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { showDerivation = !showDerivation },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                ) {
                    Text(
                        text = if (showDerivation) "ব্যাখ্যা লুকান" else "কেন কাজ করে?",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "দ্রুত সূত্র: ${shortcutDetails.shortcutFormulaOrRuleBn}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }
            AnimatedVisibility(
                visible = showDerivation,
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "• কেন কাজ করে: ${shortcutDetails.whyShortcutWorksBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = bodyColor
                    )
                    Text(
                        text = "• কখন ব্যবহার করবেন: ${shortcutDetails.whenToUseBn}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = positiveColor
                    )
                    Text(
                        text = "• কখন ব্যবহার করবেন না: ${shortcutDetails.whenToAvoidBn}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = avoidColor
                    )
                    Text(
                        text = "• পরীক্ষার সাধারণ ফাঁদ: ${shortcutDetails.commonTrapBn}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BcsSyllabusAndNavigatorCard(
    mapping: BcsSyllabusMapping,
    viewModel: MathViewModel? = null
) {
    var showCompanionModules by remember { mutableStateOf(false) }
    val companionModules = remember { SolverPedagogyEnricher.getBcsCompanionModules() }

    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.tertiaryContainer, 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_bcs_syllabus_card")
    ) {
        Column(
            modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "বিসিএস সিলেবাস ম্যাপিং",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BPSC সিলেবাস ও উৎস যাচাইকরণ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = mapping.provenanceLabelBn,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "• প্রিলিমিনারি: ${mapping.preliminarySectionBn} (${mapping.preliminaryMarksBn})",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "• লিখিত পরীক্ষা: ${mapping.writtenSectionBn} (${mapping.writtenMarksBn})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "• নোট: ${mapping.examPatternNoteBn}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showCompanionModules = !showCompanionModules },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                    .testTag("btn_toggle_bcs_companion_navigator")
            ) {
                Text(
                    text = if (showCompanionModules) "অন্যান্য বিসিএস অধ্যায় লুকান" else "পূর্ণাঙ্গ বিসিএস সিলেবাসের অন্যান্য মডিউল দেখুন",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            AnimatedVisibility(
                visible = showCompanionModules,
                enter = expandVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeIn(tween(SolverDesignTokens.EXPANSION_ANIM_MS)),
                exit = shrinkVertically(tween(SolverDesignTokens.EXPANSION_ANIM_MS)) + fadeOut(tween(SolverDesignTokens.EXPANSION_ANIM_MS))
            ) {
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    companionModules.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.syllabusAreaBn} (${item.prelimAndWrittenWeightBn})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = item.topicsCoveredBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        val screen = when (item.targetRouteType.uppercase(java.util.Locale.ROOT)) {
                                            "FORMULA_LIBRARY" -> Screen.FormulaLibrary
                                            "VISUAL_EXPLORER" -> Screen.VisualExplorer
                                            "PRACTICE_LAB" -> Screen.PracticeLab
                                            "BCS_PREPARATION" -> Screen.BcsPreparation
                                            else -> Screen.Quizzes
                                        }
                                        viewModel?.navigateTo(screen)
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget)
                                ) {
                                    Text(
                                        text = item.targetModuleNameBn,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LearnerFeedbackStatusRow(
    learningState: SolverLearningState,
    onStateChanged: (SolverLearningState) -> Unit,
    onShowSimpleExplanation: () -> Unit,
    onTrySimilar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_learner_feedback_card")
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
                Text(
                    text = "শেখার অগ্রগতি ও প্রতিক্রিয়া:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = learningState.badgeBn,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onStateChanged(SolverLearningState.SELF_REPORTED_CONFIDENCE)
                    },
                    modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "বুঝেছি",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = onShowSimpleExplanation,
                    modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "আরও সহজ ব্যাখ্যা",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Button(
                    onClick = onTrySimilar,
                    modifier = Modifier.heightIn(min = SolverDesignTokens.minTouchTarget),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "নিজে অনুশীলন",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> rememberSimUiState(
    viewModel: MathViewModel?,
    stateKey: String,
    defaultValue: T
): MutableState<T> {
    return if (viewModel != null) {
        rememberManagedUiState(viewModel, stateKey, defaultValue)
    } else {
        rememberSaveable(stateKey, key = stateKey) { mutableStateOf(defaultValue) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimultaneousLinearSystemAndBridgeCard(
    selectedTopic: SolverTopic,
    viewModel: MathViewModel? = null
) {
    var expandedInOtherTopic by rememberSimUiState(
        viewModel = viewModel,
        stateKey = "sim_expanded_${selectedTopic.name}",
        defaultValue = selectedTopic == SolverTopic.LINEAR_EQUATIONS
    )
    var systemDimension by rememberSimUiState(viewModel, "sim_system_dimension", 2) // 2 for 2x2, 3 for 3x3
    var methodTab by rememberSimUiState(viewModel, "sim_method_tab", 0) // 0: Cramer, 1: Elimination, 2: Substitution

    var simA1 by rememberSimUiState(viewModel, "sim_coeff_a1", "২")
    var simB1 by rememberSimUiState(viewModel, "sim_coeff_b1", "৩")
    var simC1 by rememberSimUiState(viewModel, "sim_coeff_c1", "১৩")
    var simA2 by rememberSimUiState(viewModel, "sim_coeff_a2", "৫")
    var simB2 by rememberSimUiState(viewModel, "sim_coeff_b2", "-২")
    var simC2 by rememberSimUiState(viewModel, "sim_coeff_c2", "৪")

    // 3x3 additional inputs
    var sim3D1 by rememberSimUiState(viewModel, "sim_coeff_3d1", "৬")
    var sim3D2 by rememberSimUiState(viewModel, "sim_coeff_3d2", "১৪")
    var sim3A3 by rememberSimUiState(viewModel, "sim_coeff_3a3", "১")
    var sim3B3 by rememberSimUiState(viewModel, "sim_coeff_3b3", "-১")
    var sim3C3 by rememberSimUiState(viewModel, "sim_coeff_3c3", "২")
    var sim3D3 by rememberSimUiState(viewModel, "sim_coeff_3d3", "৫")

    val simResult2x2 = remember(simA1, simB1, simC1, simA2, simB2, simC2) {
        val a1 = BengaliNumberFormatter.toEnglishDigits(simA1).toDoubleOrNull() ?: 2.0
        val b1 = BengaliNumberFormatter.toEnglishDigits(simB1).toDoubleOrNull() ?: 3.0
        val c1 = BengaliNumberFormatter.toEnglishDigits(simC1).toDoubleOrNull() ?: 13.0
        val a2 = BengaliNumberFormatter.toEnglishDigits(simA2).toDoubleOrNull() ?: 5.0
        val b2 = BengaliNumberFormatter.toEnglishDigits(simB2).toDoubleOrNull() ?: -2.0
        val c2 = BengaliNumberFormatter.toEnglishDigits(simC2).toDoubleOrNull() ?: 4.0
        SharedMathEngine.solveSimultaneousLinear(a1, b1, c1, a2, b2, c2)
    }

    val simResult3x3 = remember(simA1, simB1, simC1, sim3D1, simA2, simB2, simC2, sim3D2, sim3A3, sim3B3, sim3C3, sim3D3) {
        val a1 = BengaliNumberFormatter.toEnglishDigits(simA1).toDoubleOrNull() ?: 1.0
        val b1 = BengaliNumberFormatter.toEnglishDigits(simB1).toDoubleOrNull() ?: 1.0
        val c1 = BengaliNumberFormatter.toEnglishDigits(simC1).toDoubleOrNull() ?: 1.0
        val d1 = BengaliNumberFormatter.toEnglishDigits(sim3D1).toDoubleOrNull() ?: 6.0
        val a2 = BengaliNumberFormatter.toEnglishDigits(simA2).toDoubleOrNull() ?: 1.0
        val b2 = BengaliNumberFormatter.toEnglishDigits(simB2).toDoubleOrNull() ?: 2.0
        val c2 = BengaliNumberFormatter.toEnglishDigits(simC2).toDoubleOrNull() ?: 3.0
        val d2 = BengaliNumberFormatter.toEnglishDigits(sim3D2).toDoubleOrNull() ?: 14.0
        val a3 = BengaliNumberFormatter.toEnglishDigits(sim3A3).toDoubleOrNull() ?: 1.0
        val b3 = BengaliNumberFormatter.toEnglishDigits(sim3B3).toDoubleOrNull() ?: -1.0
        val c3 = BengaliNumberFormatter.toEnglishDigits(sim3C3).toDoubleOrNull() ?: 2.0
        val d3 = BengaliNumberFormatter.toEnglishDigits(sim3D3).toDoubleOrNull() ?: 5.0
        SharedMathEngine.solveSimultaneousLinear3x3(a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3)
    }

    val isEquationTopic = selectedTopic == SolverTopic.LINEAR_EQUATIONS

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_simultaneous_and_bridge_card"),
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusHero),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.tertiaryContainer, 0.38f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.24f))
    ) {
        Column(
            modifier = Modifier.padding(SolverDesignTokens.cardPaddingStandard),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isEquationTopic) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val simHeaderTitle = if (systemDimension == 3) {
                        "তিন চলকবিশিষ্ট সহসমীকরণ সমাধান (৩×৩ ব্যবস্থা: aᵢx+bᵢy+cᵢz=dᵢ)"
                    } else {
                        "দুই চলকবিশিষ্ট সহসমীকরণ সমাধান (Cramer's Rule: a₁x+b₁y=c₁, a₂x+b₂y=c₂)"
                    }
                    Text(
                        text = simHeaderTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sim_solver_header_title")
                    )
                    if (selectedTopic != SolverTopic.LINEAR_EQUATIONS) {
                        OutlinedButton(
                            onClick = { expandedInOtherTopic = !expandedInOtherTopic },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .heightIn(min = SolverDesignTokens.minTouchTarget)
                                .testTag("btn_toggle_simultaneous_solver")
                        ) {
                            Text(
                                text = if (expandedInOtherTopic) "সংক্ষিপ্ত করুন" else "সহসমীকরণ খুলুন",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "সংশ্লিষ্ট সূত্র ও ভিজ্যুয়াল ল্যাব",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("sim_solver_bridge_only_title")
                )
            }

            if (isEquationTopic && (expandedInOtherTopic || selectedTopic == SolverTopic.LINEAR_EQUATIONS)) {
                CompactSegmentedSwitch(
                    options = listOf(2, 3),
                    selectedOption = systemDimension,
                    onOptionSelected = { systemDimension = it },
                    labelProvider = { dim -> if (dim == 2) "২ চলক (x, y)" else "৩ চলক (x, y, z)" },
                    subtitleProvider = { dim -> if (dim == 2) "২×২ ব্যবস্থা" else "৩×৩ ব্যবস্থা" },
                    testTagProvider = { dim -> if (dim == 2) "sim_dim_2x2" else "sim_dim_3x3" }
                )

                if (systemDimension == 2) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Cramer's Rule", "অপনয়ন (Elimination)", "প্রতিস্থাপন (Substitution)").forEachIndexed { idx, title ->
                            FilterChip(
                                selected = methodTab == idx,
                                onClick = { methodTab = idx },
                                label = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (methodTab == idx) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier
                                    .heightIn(min = SolverDesignTokens.minTouchTarget)
                                    .testTag("sim_method_tab_$idx")
                            )
                        }
                    }
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val fontScale = LocalDensity.current.fontScale
                        val useCompact2x2 = ((maxWidth - 12.dp) / 3f) < 96.dp || fontScale >= 1.2f
                        if (useCompact2x2) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "সমীকরণ ১ (a₁x + b₁y = c₁)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = simA1,
                                        onValueChange = { simA1 = it },
                                        label = { Text("a₁") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_a1")
                                    )
                                    OutlinedTextField(
                                        value = simB1,
                                        onValueChange = { simB1 = it },
                                        label = { Text("b₁") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_b1")
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = simC1,
                                        onValueChange = { simC1 = it },
                                        label = { Text("c₁") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_c1")
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                Text(
                                    text = "সমীকরণ ২ (a₂x + b₂y = c₂)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = simA2,
                                        onValueChange = { simA2 = it },
                                        label = { Text("a₂") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_a2")
                                    )
                                    OutlinedTextField(
                                        value = simB2,
                                        onValueChange = { simB2 = it },
                                        label = { Text("b₂") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_b2")
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = simC2,
                                        onValueChange = { simC2 = it },
                                        label = { Text("c₂") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_c2")
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = simA1,
                                        onValueChange = { simA1 = it },
                                        label = { Text("a₁") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_a1")
                                    )
                                    OutlinedTextField(
                                        value = simB1,
                                        onValueChange = { simB1 = it },
                                        label = { Text("b₁") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_b1")
                                    )
                                    OutlinedTextField(
                                        value = simC1,
                                        onValueChange = { simC1 = it },
                                        label = { Text("c₁") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_c1")
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = simA2,
                                        onValueChange = { simA2 = it },
                                        label = { Text("a₂") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_a2")
                                    )
                                    OutlinedTextField(
                                        value = simB2,
                                        onValueChange = { simB2 = it },
                                        label = { Text("b₂") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_b2")
                                    )
                                    OutlinedTextField(
                                        value = simC2,
                                        onValueChange = { simC2 = it },
                                        label = { Text("c₂") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("sim2_c2")
                                    )
                                }
                            }
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val activeSteps = when (methodTab) {
                                1 -> simResult2x2.eliminationStepsBn.ifEmpty { simResult2x2.stepsBn }
                                2 -> simResult2x2.substitutionStepsBn.ifEmpty { simResult2x2.stepsBn }
                                else -> simResult2x2.stepsBn
                            }
                            activeSteps.forEach { (stepTitle, stepBody) ->
                                Text(
                                    text = "• $stepTitle: $stepBody",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (simResult2x2.isUniqueSolution) {
                                    "নির্ণেয় সমাধান: x = ${simResult2x2.xExactBn}, y = ${simResult2x2.yExactBn}"
                                } else {
                                    "সিদ্ধান্ত: ${simResult2x2.statusBn}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (simResult2x2.isUniqueSolution) {
                                    SolverDesignTokens.successContentColor()
                                } else {
                                    SolverDesignTokens.warningTitleColor()
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "৩ চলকবিশিষ্ট সমীকরণ জোট: aᵢx + bᵢy + cᵢz = dᵢ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val fontScale = LocalDensity.current.fontScale
                        val useCompact3x3 = ((maxWidth - 18.dp) / 4f) < 96.dp || fontScale >= 1.2f
                        if (useCompact3x3) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "সমীকরণ ১",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(value = simA1, onValueChange = { simA1 = it }, label = { Text("a₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_a1"))
                                    OutlinedTextField(value = simB1, onValueChange = { simB1 = it }, label = { Text("b₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_b1"))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(value = simC1, onValueChange = { simC1 = it }, label = { Text("c₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_c1"))
                                    OutlinedTextField(value = sim3D1, onValueChange = { sim3D1 = it }, label = { Text("d₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_d1"))
                                }
                                Text(
                                    text = "সমীকরণ ২",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(value = simA2, onValueChange = { simA2 = it }, label = { Text("a₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_a2"))
                                    OutlinedTextField(value = simB2, onValueChange = { simB2 = it }, label = { Text("b₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_b2"))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(value = simC2, onValueChange = { simC2 = it }, label = { Text("c₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_c2"))
                                    OutlinedTextField(value = sim3D2, onValueChange = { sim3D2 = it }, label = { Text("d₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_d2"))
                                }
                                Text(
                                    text = "সমীকরণ ৩",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(value = sim3A3, onValueChange = { sim3A3 = it }, label = { Text("a₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_a3"))
                                    OutlinedTextField(value = sim3B3, onValueChange = { sim3B3 = it }, label = { Text("b₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_b3"))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(value = sim3C3, onValueChange = { sim3C3 = it }, label = { Text("c₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_c3"))
                                    OutlinedTextField(value = sim3D3, onValueChange = { sim3D3 = it }, label = { Text("d₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_d3"))
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    OutlinedTextField(value = simA1, onValueChange = { simA1 = it }, label = { Text("a₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_a1"))
                                    OutlinedTextField(value = simB1, onValueChange = { simB1 = it }, label = { Text("b₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_b1"))
                                    OutlinedTextField(value = simC1, onValueChange = { simC1 = it }, label = { Text("c₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_c1"))
                                    OutlinedTextField(value = sim3D1, onValueChange = { sim3D1 = it }, label = { Text("d₁") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_d1"))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    OutlinedTextField(value = simA2, onValueChange = { simA2 = it }, label = { Text("a₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_a2"))
                                    OutlinedTextField(value = simB2, onValueChange = { simB2 = it }, label = { Text("b₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_b2"))
                                    OutlinedTextField(value = simC2, onValueChange = { simC2 = it }, label = { Text("c₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_c2"))
                                    OutlinedTextField(value = sim3D2, onValueChange = { sim3D2 = it }, label = { Text("d₂") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_d2"))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    OutlinedTextField(value = sim3A3, onValueChange = { sim3A3 = it }, label = { Text("a₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_a3"))
                                    OutlinedTextField(value = sim3B3, onValueChange = { sim3B3 = it }, label = { Text("b₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_b3"))
                                    OutlinedTextField(value = sim3C3, onValueChange = { sim3C3 = it }, label = { Text("c₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_c3"))
                                    OutlinedTextField(value = sim3D3, onValueChange = { sim3D3 = it }, label = { Text("d₃") }, singleLine = true, modifier = Modifier.weight(1f).testTag("sim3_d3"))
                                }
                            }
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(SolverDesignTokens.innerPanelRadius),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            simResult3x3.stepsBn.forEach { (stepTitle, stepBody) ->
                                Text(
                                    text = "• $stepTitle: $stepBody",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (simResult3x3.isUniqueSolution) {
                                    "নির্ণেয় সমাধান: x = ${simResult3x3.xExactBn}, y = ${simResult3x3.yExactBn}, z = ${simResult3x3.zExactBn}"
                                } else {
                                    "সিদ্ধান্ত: ${simResult3x3.statusBn}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (simResult3x3.isUniqueSolution) {
                                    SolverDesignTokens.successContentColor()
                                } else {
                                    SolverDesignTokens.warningTitleColor()
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel?.navigateTo(Screen.FormulaLibrary) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("btn_solver_to_formula_lib"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Functions,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "সংশ্লিষ্ট সূত্র দেখুন",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                OutlinedButton(
                    onClick = { viewModel?.navigateTo(Screen.VisualExplorer) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .testTag("btn_solver_to_visual_explorer"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ভিজ্যুয়াল এক্সপ্লোরার",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
