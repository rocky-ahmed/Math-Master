package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.math.SolverTopic
import com.example.math.VisualDiagramType
import com.example.math.VisualModelSpec
import com.example.ui.MathViewModel
import com.example.ui.Screen

@Composable
fun ProblemUnderstandingAndPrerequisiteCard(
    understanding: ProblemUnderstanding
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_problem_understanding_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "প্রশ্নটি বুঝি",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "প্রশ্নটি বুঝি ও প্রয়োজনীয় ধারণা (Beginner Guide)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (understanding.prerequisiteConceptBn.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "প্রয়োজনীয় পূর্বধারণা",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = understanding.prerequisiteConceptBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            if (understanding.summaryBn.isNotBlank()) {
                Text(
                    text = "• সারসংক্ষেপ: ${understanding.summaryBn}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (understanding.givenItemsBn.isNotEmpty()) {
                Text(
                    text = "• প্রদত্ত তথ্য: ${understanding.givenItemsBn.joinToString(" | ") { "${it.first}: ${it.second}" }}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (understanding.requiredTargetBn.isNotBlank()) {
                Text(
                    text = "• নির্ণেয় লক্ষ্য: ${understanding.requiredTargetBn}",
                    style = MaterialTheme.typography.bodySmall
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_formula_breakdown_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Functions,
                        contentDescription = "সূত্রের ব্যবচ্ছেদ",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "সূত্রের চলক পরিচিতি: ${breakdown.formulaTitleBn}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("btn_toggle_formula_breakdown")
                ) {
                    Text(if (expanded) "সংক্ষিপ্ত" else "চলক ও ব্যাখ্যা", fontSize = 11.sp)
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    breakdown.variables.forEach { v ->
                        val valSuffix = v.givenValueBn?.takeIf { it.isNotBlank() }?.let { " = $it" } ?: ""
                        Text(
                            text = "• ${v.symbolBn}: ${v.meaningBn}$valSuffix",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (breakdown.plainMeaningBn.isNotBlank()) {
                        Text(
                            text = "সহজ অর্থ: ${breakdown.plainMeaningBn}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (breakdown.whyApplicableBn.isNotBlank()) {
                        Text(
                            text = "কেন প্রযোজ্য: ${breakdown.whyApplicableBn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveLearningStepCard(
    step: SolutionStep,
    mode: SolverMode,
    forceExpandAll: Boolean?,
    onStepSelfCheckVerified: () -> Unit
) {
    var showSimple by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(forceExpandAll ?: false)
    }
    var showRuleAndWhy by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(forceExpandAll ?: (mode == SolverMode.LEARNING))
    }
    var showAnalogyAndTrap by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(forceExpandAll ?: false)
    }
    var showTryItYourself by remember(step.stepIndex, forceExpandAll) {
        mutableStateOf(false)
    }
    var hintLevel by remember(step.stepIndex) { mutableIntStateOf(0) }
    var userAttempt by remember(step.stepIndex) { mutableStateOf("") }
    var attemptFeedbackBn by remember(step.stepIndex) { mutableStateOf<String?>(null) }

    val beforeStr = step.expressionBeforeBn ?: ""
    val afterStr = step.expressionAfterBn ?: ""

    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_step_card_${step.stepIndex}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = step.expressionOrMathBn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (beforeStr.isNotBlank() && afterStr.isNotBlank() && beforeStr != afterStr) {
                        Text(
                            text = "রূপান্তর: $beforeStr ➜ $afterStr",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = step.explanationBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (mode == SolverMode.LEARNING) {
                Spacer(modifier = Modifier.height(8.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = showSimple,
                        onClick = { showSimple = !showSimple },
                        label = { Text("সহজ ব্যাখ্যা", fontSize = 10.sp) },
                        modifier = Modifier.testTag("step_${step.stepIndex}_chip_simple")
                    )
                    FilterChip(
                        selected = showRuleAndWhy,
                        onClick = { showRuleAndWhy = !showRuleAndWhy },
                        label = { Text("কেন এই ধাপ?", fontSize = 10.sp) },
                        modifier = Modifier.testTag("step_${step.stepIndex}_chip_why")
                    )
                    FilterChip(
                        selected = showAnalogyAndTrap,
                        onClick = { showAnalogyAndTrap = !showAnalogyAndTrap },
                        label = { Text("উদাহরণ ও ভুল", fontSize = 10.sp) },
                        modifier = Modifier.testTag("step_${step.stepIndex}_chip_analogy")
                    )
                    FilterChip(
                        selected = showTryItYourself,
                        onClick = { showTryItYourself = !showTryItYourself },
                        label = { Text("ইঙ্গিত/চেষ্টা", fontSize = 10.sp) },
                        modifier = Modifier.testTag("step_${step.stepIndex}_chip_try")
                    )
                }

                AnimatedVisibility(visible = showSimple && step.simpleExplanationBn.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                    ) {
                        Text(
                            text = "সহজ ভাষায় (Level 1): ${step.simpleExplanationBn}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = showRuleAndWhy) {
                    val whyText = step.whyThisStepBn ?: step.detailedRuleBn ?: ""
                    if (whyText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
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
                                    text = "কেন এই ধাপ: $whyText",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = showAnalogyAndTrap) {
                    val analogy = step.conceptualAnalogyBn ?: ""
                    val mistake = step.commonMistakeBn ?: ""
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (analogy.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "বাস্তব উদাহরণ (Level 3): $analogy",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        if (mistake.isNotBlank()) {
                            val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) Color(0xFF450A0A).copy(alpha = 0.7f) else Color(0xFFFEF2F2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "সাধারণ ভুল সতর্কতা: $mistake",
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFFFCA5A5) else Color(0xFF991B1B),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = showTryItYourself) {
                    val h1 = step.hint1Bn ?: ""
                    val h2 = step.hint2Bn ?: ""
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { hintLevel = 1 },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("ইঙ্গিত ১ (ধারণা)", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { hintLevel = 2 },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("ইঙ্গিত ২ (পরবর্তী সূত্র)", fontSize = 11.sp)
                                }
                            }
                            if (hintLevel >= 1 && h1.isNotBlank()) {
                                Text("ইঙ্গিত ১: $h1", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            if (hintLevel >= 2 && h2.isNotBlank()) {
                                Text("ইঙ্গিত ২: $h2", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = userAttempt,
                                    onValueChange = { userAttempt = it },
                                    label = { Text("নিজে ধাপটির মান লিখুন", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        val expected = (step.expectedStepValueBn ?: "").ifBlank { afterStr }
                                        val isMatch = SharedMathEngine.isAnswerCorrect(userAttempt, expected) ||
                                            (userAttempt.trim().isNotBlank() && expected.contains(userAttempt.trim()))
                                        if (isMatch) {
                                            attemptFeedbackBn = "সঠিক! আপনি এই ধাপটি নিজে সমাধান করেছেন।"
                                            onStepSelfCheckVerified()
                                        } else {
                                            attemptFeedbackBn = "আবার চেষ্টা করুন। প্রত্যাশিত ফলাফল: $expected"
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("যাচাই", fontSize = 11.sp)
                                }
                            }
                            attemptFeedbackBn?.let { fb ->
                                Text(
                                    text = fb,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (fb.startsWith("সঠিক")) Color(0xFF059669) else Color(0xFFD97706)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InlineSolverVisualCard(visualSpec: VisualModelSpec) {
    var expanded by remember { mutableStateOf(true) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_inline_visual_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("btn_toggle_inline_visual")
                ) {
                    Text(if (expanded) "চিত্র লুকান" else "চিত্র দেখুন", fontSize = 11.sp)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val a11yText = visualSpec.accessibilitySummaryBn.ifBlank { visualSpec.titleBn }
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(76.dp)
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
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    visualSpec.labelsBn.take(4).forEach { lbl ->
                                        Text(
                                            text = lbl,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
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

@Composable
fun ShortcutAndTrapCard(shortcutDetails: ShortcutDetails) {
    var showDerivation by remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val containerBg = if (isDark) Color(0xFF451A03).copy(alpha = 0.65f) else Color(0xFFFFFBEB)
    val titleColor = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
    val bodyColor = if (isDark) Color(0xFFFEF3C7) else Color(0xFF78350F)
    val positiveColor = if (isDark) Color(0xFF6EE7B7) else Color(0xFF065F46)
    val avoidColor = if (isDark) Color(0xFFFCA5A5) else Color(0xFF991B1B)
    val trapColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_shortcut_details_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "শর্টকাট কৌশল",
                        tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
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
                OutlinedButton(
                    onClick = { showDerivation = !showDerivation },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(if (showDerivation) "ব্যাখ্যা লুকান" else "কেন কাজ করে?", fontSize = 11.sp)
                }
            }
            Text(
                text = "দ্রুত সূত্র: ${shortcutDetails.shortcutFormulaOrRuleBn}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = bodyColor
            )
            AnimatedVisibility(visible = showDerivation) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "• কেন কাজ করে: ${shortcutDetails.whyShortcutWorksBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = bodyColor
                    )
                    Text(
                        text = "• কখন ব্যবহার করবেন: ${shortcutDetails.whenToUseBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = positiveColor
                    )
                    Text(
                        text = "• কখন ব্যবহার করবেন না: ${shortcutDetails.whenToAvoidBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = avoidColor
                    )
                    Text(
                        text = "• পরীক্ষার সাধারণ ফাঁদ: ${shortcutDetails.commonTrapBn}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = trapColor
                    )
                }
            }
        }
    }
}

@Composable
fun BcsSyllabusAndNavigatorCard(
    mapping: BcsSyllabusMapping,
    viewModel: MathViewModel? = null
) {
    var showCompanionModules by remember { mutableStateOf(false) }
    val companionModules = remember { SolverPedagogyEnricher.getBcsCompanionModules() }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_bcs_syllabus_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = mapping.provenanceLabelBn,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "• প্রিলিমিনারি: ${mapping.preliminarySectionBn} (${mapping.preliminaryMarksBn})",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "• লিখিত পরীক্ষা: ${mapping.writtenSectionBn} (${mapping.writtenMarksBn})",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "• নোট: ${mapping.examPatternNoteBn}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showCompanionModules = !showCompanionModules },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("btn_toggle_bcs_companion_navigator")
            ) {
                Text(
                    text = if (showCompanionModules) "অন্যান্য বিসিএস অধ্যায় লুকান" else "পূর্ণাঙ্গ বিসিএস সিলেবাসের অন্যান্য মডিউল দেখুন",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = showCompanionModules) {
                Column(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    companionModules.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
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
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedButton(
                                    onClick = {
                                        val screen = when (item.targetRouteType) {
                                            "FORMULA_LIBRARY" -> Screen.FormulaLibrary
                                            "VISUAL_EXPLORER" -> Screen.VisualExplorer
                                            "PRACTICE_LAB" -> Screen.PracticeLab
                                            "BCS_PREPARATION" -> Screen.BcsPreparation
                                            else -> Screen.Quizzes
                                        }
                                        viewModel?.navigateTo(screen)
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(item.targetModuleNameBn, fontSize = 10.sp)
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
fun LearnerFeedbackStatusRow(
    learningState: SolverLearningState,
    onStateChanged: (SolverLearningState) -> Unit,
    onShowSimpleExplanation: () -> Unit,
    onTrySimilar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solver_learner_feedback_card")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "শেখার অগ্রগতি ও প্রতিক্রিয়া:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = learningState.badgeBn,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onStateChanged(SolverLearningState.SELF_REPORTED_CONFIDENCE)
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text("বুঝেছি", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onShowSimpleExplanation,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text("আরও সহজ ব্যাখ্যা", fontSize = 11.sp)
                }
                Button(
                    onClick = {
                        onTrySimilar()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text("নিজে অনুশীলন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SimultaneousLinearSystemAndBridgeCard(
    selectedTopic: SolverTopic,
    viewModel: MathViewModel? = null
) {
    var expandedInOtherTopic by remember(selectedTopic) {
        mutableStateOf(selectedTopic == SolverTopic.LINEAR_EQUATIONS)
    }
    var systemDimension by remember { mutableIntStateOf(2) } // 2 for 2x2, 3 for 3x3
    var methodTab by remember { mutableIntStateOf(0) } // 0: Cramer, 1: Elimination, 2: Substitution

    var simA1 by remember { mutableStateOf("২") }
    var simB1 by remember { mutableStateOf("৩") }
    var simC1 by remember { mutableStateOf("১৩") }
    var simA2 by remember { mutableStateOf("৫") }
    var simB2 by remember { mutableStateOf("-২") }
    var simC2 by remember { mutableStateOf("৪") }

    // 3x3 additional inputs
    var sim3D1 by remember { mutableStateOf("৬") }
    var sim3D2 by remember { mutableStateOf("১৪") }
    var sim3A3 by remember { mutableStateOf("১") }
    var sim3B3 by remember { mutableStateOf("-১") }
    var sim3C3 by remember { mutableStateOf("২") }
    var sim3D3 by remember { mutableStateOf("৫") }

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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        modifier = Modifier.weight(1f).testTag("sim_solver_header_title")
                    )
                    if (selectedTopic != SolverTopic.LINEAR_EQUATIONS) {
                        OutlinedButton(
                            onClick = { expandedInOtherTopic = !expandedInOtherTopic },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("btn_toggle_simultaneous_solver")
                        ) {
                            Text(
                                text = if (expandedInOtherTopic) "সংক্ষিপ্ত করুন" else "সহসমীকরণ খুলুন",
                                fontSize = 11.sp
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = systemDimension == 2,
                        onClick = { systemDimension = 2 },
                        label = { Text("২ চলক (x, y)", fontSize = 11.sp) },
                        modifier = Modifier.testTag("sim_dim_2x2")
                    )
                    FilterChip(
                        selected = systemDimension == 3,
                        onClick = { systemDimension = 3 },
                        label = { Text("৩ চলক (x, y, z)", fontSize = 11.sp) },
                        modifier = Modifier.testTag("sim_dim_3x3")
                    )
                }

                if (systemDimension == 2) {
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Cramer's Rule", "অপনয়ন (Elimination)", "প্রতিস্থাপন (Substitution)").forEachIndexed { idx, title ->
                            FilterChip(
                                selected = methodTab == idx,
                                onClick = { methodTab = idx },
                                label = { Text(title, fontSize = 10.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = simA1,
                            onValueChange = { simA1 = it },
                            label = { Text("a₁") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = simB1,
                            onValueChange = { simB1 = it },
                            label = { Text("b₁") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = simC1,
                            onValueChange = { simC1 = it },
                            label = { Text("c₁") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
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
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = simB2,
                            onValueChange = { simB2 = it },
                            label = { Text("b₂") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = simC2,
                            onValueChange = { simC2 = it },
                            label = { Text("c₂") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val activeSteps = when (methodTab) {
                                1 -> simResult2x2.eliminationStepsBn.ifEmpty { simResult2x2.stepsBn }
                                2 -> simResult2x2.substitutionStepsBn.ifEmpty { simResult2x2.stepsBn }
                                else -> simResult2x2.stepsBn
                            }
                            activeSteps.forEach { (stepTitle, stepBody) ->
                                Text(
                                    text = "• $stepTitle: $stepBody",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (simResult2x2.isUniqueSolution) {
                                    "নির্ণেয় সমাধান: x = ${simResult2x2.xExactBn}, y = ${simResult2x2.yExactBn}"
                                } else {
                                    "সিদ্ধান্ত: ${simResult2x2.statusBn}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (simResult2x2.isUniqueSolution) Color(0xFF059669) else Color(0xFFD97706)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "৩ চলকবিশিষ্ট সমীকরণ জোট: aᵢx + bᵢy + cᵢz = dᵢ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(value = simA1, onValueChange = { simA1 = it }, label = { Text("a₁") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = simB1, onValueChange = { simB1 = it }, label = { Text("b₁") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = simC1, onValueChange = { simC1 = it }, label = { Text("c₁") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = sim3D1, onValueChange = { sim3D1 = it }, label = { Text("d₁") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(value = simA2, onValueChange = { simA2 = it }, label = { Text("a₂") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = simB2, onValueChange = { simB2 = it }, label = { Text("b₂") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = simC2, onValueChange = { simC2 = it }, label = { Text("c₂") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = sim3D2, onValueChange = { sim3D2 = it }, label = { Text("d₂") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(value = sim3A3, onValueChange = { sim3A3 = it }, label = { Text("a₃") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = sim3B3, onValueChange = { sim3B3 = it }, label = { Text("b₃") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = sim3C3, onValueChange = { sim3C3 = it }, label = { Text("c₃") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = sim3D3, onValueChange = { sim3D3 = it }, label = { Text("d₃") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            simResult3x3.stepsBn.forEach { (stepTitle, stepBody) ->
                                Text(text = "• $stepTitle: $stepBody", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (simResult3x3.isUniqueSolution) {
                                    "নির্ণেয় সমাধান: x = ${simResult3x3.xExactBn}, y = ${simResult3x3.yExactBn}, z = ${simResult3x3.zExactBn}"
                                } else {
                                    "সিদ্ধান্ত: ${simResult3x3.statusBn}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (simResult3x3.isUniqueSolution) Color(0xFF059669) else Color(0xFFD97706)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel?.navigateTo(Screen.FormulaLibrary) },
                    modifier = Modifier.weight(1f).testTag("btn_solver_to_formula_lib"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("সংশ্লিষ্ট সূত্র দেখুন", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { viewModel?.navigateTo(Screen.VisualExplorer) },
                    modifier = Modifier.weight(1f).testTag("btn_solver_to_visual_explorer"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("ভিজ্যুয়াল এক্সপ্লোরার", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
