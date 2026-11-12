package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.math.BengaliNumberFormatter
import com.example.math.CrossSectionAnalysis
import com.example.math.CrossSectionSliceMode
import com.example.math.FractionOpType
import com.example.math.LearningStageType
import com.example.math.OrbitCameraState
import com.example.math.Point3DCoord
import com.example.math.SolidAnatomyHighlight
import com.example.math.Surface3DPreset
import com.example.math.TopicNineStageGuide
import com.example.math.VisualMathEngine
import com.example.math.VisualMathStudioEngine
import com.example.ui.MathViewModel
import com.example.ui.components.Math3DStudioViewport
import com.example.ui.rememberManagedUiState
import com.example.ui.theme.MathSpacing
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// ============================================================================
// 1. NINE-STAGE BENGALI GUIDED LEARNING STEPPER CARD (INTRODUCE -> RECAP)
// ============================================================================

@Composable
fun VisualNineStageStepperCard(
    guide: TopicNineStageGuide,
    viewModel: MathViewModel,
    statePrefix: String,
    onApplyRecommendedPreset: ((LearningStageType) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var activeStageOrdinal by rememberManagedUiState(viewModel, "$statePrefix:stage_idx", 0)
    var practiceInputText by rememberManagedUiState(viewModel, "$statePrefix:practice_in:${guide.topicId}", "")
    var practiceChecked by rememberManagedUiState(viewModel, "$statePrefix:practice_checked:${guide.topicId}", false)
    var practiceIsCorrect by rememberManagedUiState(viewModel, "$statePrefix:practice_ok:${guide.topicId}", false)
    var showPracticeSolution by rememberManagedUiState(viewModel, "$statePrefix:practice_sol:${guide.topicId}", false)
    var selectedKcOption by remember(guide.topicId) { mutableStateOf<Int?>(null) }

    val safeIdx = activeStageOrdinal.coerceIn(0, (guide.stages.size - 1).coerceAtLeast(0))
    val stageContent = guide.stages[safeIdx]

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MathSpacing.blendedContainerColor(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                fraction = 0.35f
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("visual_nine_stage_stepper_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "৯-ধাপের নির্দেশিত শিক্ষা প্রবাহ (9-Stage Guided Studio)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${guide.topicTitleBn} — ধাপ ${BengaliNumberFormatter.toBengaliDigits((safeIdx + 1).toString())}/৯: ${stageContent.stage.subtitleBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Stage Selector Chips (1..9)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("nine_stage_chips_row"),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(guide.stages.size) { index ->
                    val st = guide.stages[index].stage
                    FilterChip(
                        selected = safeIdx == index,
                        onClick = { activeStageOrdinal = index },
                        label = {
                            Text(
                                text = st.titleBn,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (safeIdx == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = if (safeIdx == index) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        modifier = Modifier.testTag("nine_stage_chip_${index + 1}")
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
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
                            text = stageContent.headlineBn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        if (stageContent.recommendedPresetLabelBn != null && onApplyRecommendedPreset != null) {
                            OutlinedButton(
                                onClick = { onApplyRecommendedPreset(stageContent.stage) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("nine_stage_preset_action_btn")
                            ) {
                                Text(
                                    text = stageContent.recommendedPresetLabelBn,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = stageContent.bodyBn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    stageContent.keyPointsBn.forEachIndexed { idx, point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = BengaliNumberFormatter.toBengaliDigits((idx + 1).toString()),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Stage 7: Live Interactive Practice Input
                    if (stageContent.stage == LearningStageType.PRACTICE) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Text(
                            text = guide.practiceChallenge.promptBn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = practiceInputText,
                                onValueChange = {
                                    practiceInputText = it
                                    practiceChecked = false
                                },
                                label = { Text("উত্তর (${guide.practiceChallenge.unitBn})") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("nine_stage_practice_input")
                            )
                            Button(
                                onClick = {
                                    val normalized = BengaliNumberFormatter.toEnglishDigits(practiceInputText.trim())
                                    val parsed = normalized.toDoubleOrNull()
                                    practiceChecked = true
                                    practiceIsCorrect = parsed != null &&
                                        abs(parsed - guide.practiceChallenge.expectedValue) <= guide.practiceChallenge.tolerance
                                    viewModel.recordVisualMathCheckpoint(
                                        topicId = "${guide.topicId}_practice",
                                        topicTitleBn = "${guide.topicTitleBn} অনুশীলন",
                                        isCorrect = practiceIsCorrect
                                    )
                                },
                                modifier = Modifier.testTag("nine_stage_practice_verify_btn")
                            ) {
                                Text("উত্তর যাচাই", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (practiceChecked) {
                            val expectedBn = VisualMathEngine.formatNumberBn(guide.practiceChallenge.expectedValue, 2)
                            Text(
                                text = if (practiceIsCorrect) {
                                    "চমৎকার! আপনার উত্তর সঠিক ($expectedBn ${guide.practiceChallenge.unitBn})।"
                                } else {
                                    "আবার চেষ্টা করুন! ইঙ্গিত: ${guide.practiceChallenge.hintBn} (সঠিক মান ≈ $expectedBn ${guide.practiceChallenge.unitBn})"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (practiceIsCorrect) Color(0xFF15803D) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("nine_stage_practice_feedback")
                            )
                        }

                        OutlinedButton(
                            onClick = { showPracticeSolution = !showPracticeSolution },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (showPracticeSolution) "সমাধান লুকান" else "সম্পূর্ণ সমাধান দেখুন",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        AnimatedVisibility(visible = showPracticeSolution) {
                            Text(
                                text = guide.practiceChallenge.solutionExplanationBn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Stage 8: Interactive Knowledge Check
                    if (stageContent.stage == LearningStageType.KNOWLEDGE_CHECK) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        val q = guide.knowledgeCheckQuestion
                        Text(
                            text = q.questionBn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(q.optionsBn.size) { optIdx ->
                                FilterChip(
                                    selected = selectedKcOption == optIdx,
                                    onClick = {
                                        selectedKcOption = optIdx
                                        viewModel.recordVisualMathCheckpoint(
                                            topicId = guide.topicId,
                                            topicTitleBn = guide.topicTitleBn,
                                            isCorrect = (optIdx == q.correctIndex)
                                        )
                                    },
                                    label = { Text(q.optionsBn[optIdx], style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("nine_stage_kc_opt_$optIdx")
                                )
                            }
                        }
                        if (selectedKcOption != null) {
                            val ok = selectedKcOption == q.correctIndex
                            Text(
                                text = if (ok) "সঠিক! ${q.explanationBn}" else "সঠিক উত্তর: ${q.optionsBn[q.correctIndex]}। ${q.explanationBn}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (ok) Color(0xFF15803D) else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Stage 9: Recap Takeaways
                    if (stageContent.stage == LearningStageType.RECAP) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        guide.recapTakeawaysBn.forEach { takeaway ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = takeaway,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "নির্দেশনা: ${stageContent.interactiveActionHintBn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Previous / Next Stage Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { if (safeIdx > 0) activeStageOrdinal = safeIdx - 1 },
                    enabled = safeIdx > 0,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("nine_stage_prev_btn")
                ) {
                    Text("পূর্ববর্তী ধাপ", style = MaterialTheme.typography.labelMedium)
                }

                Text(
                    text = "${BengaliNumberFormatter.toBengaliDigits((safeIdx + 1).toString())} / ৯",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { if (safeIdx < guide.stages.size - 1) activeStageOrdinal = safeIdx + 1 },
                    enabled = safeIdx < guide.stages.size - 1,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("nine_stage_next_btn")
                ) {
                    Text("পরবর্তী ধাপ", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// ============================================================================
// 2. 3D PLANE CROSS-SECTION SLICER & 2D CONTOUR INSPECTOR
// ============================================================================

@Composable
fun CrossSectionSlicerControlsAndInspector(
    sliceMode: CrossSectionSliceMode,
    onSliceModeChange: (CrossSectionSliceMode) -> Unit,
    slicePosition: Float,
    onSlicePositionChange: (Float) -> Unit,
    analysis: CrossSectionAnalysis,
    modifier: Modifier = Modifier
) {
    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MathSpacing.blendedContainerColor(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                fraction = 0.35f
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("solid_3d_cross_section_panel")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "৩ডি প্রস্থচ্ছেদ স্লাইসার (Interactive 3D Cross-Section Slicer)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("solid_3d_slice_mode_row"),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(CrossSectionSliceMode.entries) { mode ->
                    FilterChip(
                        selected = sliceMode == mode,
                        onClick = { onSliceModeChange(mode) },
                        label = { Text(mode.titleBn, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("solid_3d_slice_mode_${mode.id}")
                    )
                }
            }

            if (sliceMode != CrossSectionSliceMode.NONE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ছেদতলের অবস্থান (Slice Position): ${BengaliNumberFormatter.toBengaliDigits((slicePosition * 100).toInt().toString())}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = analysis.sectionShapeNameBn,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFDC2626)
                    )
                }

                Slider(
                    value = slicePosition,
                    onValueChange = { onSlicePositionChange(it.coerceIn(0.1f, 0.9f)) },
                    valueRange = 0.1f..0.9f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solid_3d_slice_position_slider")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 2D Cross-Section Contour Preview Box
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.5.dp, Color(0xFFDC2626).copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                            .testTag("cross_section_2d_inspector_canvas"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            val pts = analysis.contour2D
                            if (pts.size >= 3) {
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val rScale = min(size.width, size.height) * 0.42f
                                val path = Path().apply {
                                    moveTo(cx + pts[0].first * rScale, cy - pts[0].second * rScale)
                                    for (i in 1 until pts.size) {
                                        lineTo(cx + pts[i].first * rScale, cy - pts[i].second * rScale)
                                    }
                                    close()
                                }
                                drawPath(path = path, color = Color(0xFFEF4444).copy(alpha = 0.28f), style = Fill)
                                drawPath(path = path, color = Color(0xFFDC2626), style = Stroke(width = 3.2f))
                            }
                            drawContext.canvas.nativeCanvas.apply {
                                val paint = android.graphics.Paint().apply {
                                    color = onSurfaceArgb
                                    textSize = 18f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    isAntiAlias = true
                                }
                                drawText("২ডি ছেদতল", size.width / 2f, size.height - 2f, paint)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "আকৃতি: ${analysis.sectionShapeNameBn}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ছেদতলের ক্ষেত্রফল: ${VisualMathEngine.formatNumberBn(analysis.sectionArea, 2)} বর্গ একক",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "ছেদতলের পরিসীমা: ${VisualMathEngine.formatNumberBn(analysis.sectionPerimeter, 2)} একক",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "সূত্র: ${analysis.formulaBn}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = analysis.explanationBn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 3. TRIGONOMETRY UNIT CIRCLE & HEIGHT-DISTANCE ELEVATION STUDIO CARD
// ============================================================================

@Composable
fun TrigonometryUnitCircleLabCard(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    var angleDeg by rememberManagedUiState(viewModel, "visual_explorer:tab_0:trig_angle", 45.0)
    var elevBaseDist by rememberManagedUiState(viewModel, "visual_explorer:tab_0:trig_base", 20.0)
    var showElevationMode by rememberManagedUiState(viewModel, "visual_explorer:tab_0:trig_elev_mode", false)

    val unitState = remember(angleDeg) { VisualMathStudioEngine.analyzeUnitCircle(angleDeg) }
    val elevState = remember(angleDeg, elevBaseDist) {
        VisualMathStudioEngine.analyzeTrigElevation(angleDeg, elevBaseDist)
    }

    val standardAngles = listOf(0.0, 30.0, 45.0, 60.0, 90.0, 120.0, 180.0, 270.0)
    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("trigonometry_unit_circle_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ত্রিকোণমিতি একক বৃত্ত ও উচ্চতা-দূরত্ব ল্যাব (Trigonometry Studio)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "sinθ, cosθ, tanθ তরঙ্গ, চতুর্ভাগ চিহ্ন এবং উন্নতি কোণের বাস্তব প্রয়োগ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilterChip(
                    selected = showElevationMode,
                    onClick = { showElevationMode = !showElevationMode },
                    label = {
                        Text(
                            text = if (showElevationMode) "একক বৃত্ত ভিউ" else "উচ্চতা ও দূরত্ব",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.testTag("trig_toggle_elevation_btn")
                )
            }

            // Standard Angle Presets
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(standardAngles) { presetDeg ->
                    FilterChip(
                        selected = abs(angleDeg - presetDeg) < 0.5,
                        onClick = { angleDeg = presetDeg },
                        label = {
                            Text(
                                "${BengaliNumberFormatter.toBengaliDigits(presetDeg.toInt().toString())}°",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("trig_angle_preset_${presetDeg.toInt()}")
                    )
                }
            }

            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryColor = MaterialTheme.colorScheme.secondary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val axisColor = MaterialTheme.colorScheme.outline

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    .testTag("trig_unit_circle_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    if (!showElevationMode) {
                        // Left: Unit Circle | Right: Synchronized Sine & Cosine Wave
                        val circleCx = size.width * 0.28f
                        val circleCy = size.height * 0.52f
                        val rPx = min(size.width * 0.23f, size.height * 0.38f)

                        drawLine(axisColor, Offset(circleCx - rPx * 1.25f, circleCy), Offset(circleCx + rPx * 1.25f, circleCy), 2f)
                        drawLine(axisColor, Offset(circleCx, circleCy - rPx * 1.25f), Offset(circleCx, circleCy + rPx * 1.25f), 2f)
                        drawCircle(primaryColor, radius = rPx, center = Offset(circleCx, circleCy), style = Stroke(width = 3f))

                        val rad = unitState.angleRadians.toFloat()
                        val px = circleCx + rPx * cos(rad)
                        val py = circleCy - rPx * sin(rad)

                        // cos(theta) horizontal segment (blue/primary) & sin(theta) vertical segment (red)
                        drawLine(Color(0xFF2563EB), Offset(circleCx, circleCy), Offset(px, circleCy), strokeWidth = 5f)
                        drawLine(Color(0xFFDC2626), Offset(px, circleCy), Offset(px, py), strokeWidth = 5f)
                        drawLine(tertiaryColor, Offset(circleCx, circleCy), Offset(px, py), strokeWidth = 4f)
                        drawCircle(Color(0xFFDC2626), radius = 7f, center = Offset(px, py))

                        // Right half: Sine wave trace from 0 to 360 deg
                        val waveLeft = size.width * 0.56f
                        val waveRight = size.width * 0.96f
                        val waveW = (waveRight - waveLeft).coerceAtLeast(20f)
                        drawLine(axisColor, Offset(waveLeft, circleCy), Offset(waveRight, circleCy), 2f)

                        val sinePath = Path()
                        val steps = 48
                        for (i in 0..steps) {
                            val t = i.toFloat() / steps
                            val wx = waveLeft + t * waveW
                            val wy = circleCy - rPx * sin(t * 2f * PI.toFloat())
                            if (i == 0) sinePath.moveTo(wx, wy) else sinePath.lineTo(wx, wy)
                        }
                        drawPath(sinePath, color = Color(0xFFDC2626), style = Stroke(width = 3.2f))

                        val markerX = waveLeft + (unitState.angleDegrees.toFloat() / 360f) * waveW
                        drawCircle(Color(0xFFDC2626), radius = 6.5f, center = Offset(markerX, py))
                        drawLine(
                            color = Color(0xFFDC2626).copy(alpha = 0.45f),
                            start = Offset(px, py),
                            end = Offset(markerX, py),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    } else {
                        // Right-Triangle Height & Distance Elevation View
                        val startX = size.width * 0.14f
                        val baseY = size.height * 0.82f
                        val baseW = size.width * 0.62f
                        val towerH = (baseW * kotlin.math.tan(Math.toRadians(elevState.angleDegrees)).toFloat()).coerceIn(30f, size.height * 0.65f)
                        val topPt = Offset(startX + baseW, baseY - towerH)

                        val path = Path().apply {
                            moveTo(startX, baseY)
                            lineTo(startX + baseW, baseY)
                            lineTo(topPt.x, topPt.y)
                            close()
                        }
                        drawPath(path, primaryColor.copy(alpha = 0.18f), style = Fill)
                        drawPath(path, primaryColor, style = Stroke(width = 4f))

                        val paint = android.graphics.Paint().apply {
                            color = onSurfaceArgb
                            textSize = 22f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        drawContext.canvas.nativeCanvas.apply {
                            drawText("ভূমি d = ${VisualMathEngine.formatNumberBn(elevState.baseDistance, 1)}", startX + baseW / 2f, baseY + 26f, paint)
                            drawText("উচ্চতা h = ${VisualMathEngine.formatNumberBn(elevState.oppositeHeight, 2)}", startX + baseW + 8f, baseY - towerH / 2f, paint)
                            drawText("θ = ${VisualMathEngine.formatNumberBn(elevState.angleDegrees, 1)}°", startX + 54f, baseY - 12f, paint)
                        }
                    }
                }
            }

            // Angle Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "কোণ θ: ${VisualMathEngine.formatNumberBn(unitState.angleDegrees, 1)}° (${VisualMathEngine.formatNumberBn(unitState.angleRadians, 2)} rad)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = unitState.quadrantNameBn,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Slider(
                value = angleDeg.toFloat(),
                onValueChange = { angleDeg = it.toInt().toDouble() },
                valueRange = 0f..360f,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trig_angle_slider")
            )

            if (showElevationMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ভূমির দূরত্ব d: ${VisualMathEngine.formatNumberBn(elevBaseDist, 1)} একক",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "নির্ণেয় উচ্চতা h = ${VisualMathEngine.formatNumberBn(elevState.oppositeHeight, 2)} একক",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = elevBaseDist.toFloat(),
                    onValueChange = { elevBaseDist = it.toInt().toDouble() },
                    valueRange = 5f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = elevState.explanationBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                unitState.stepsBn.forEach { step ->
                    Text(
                        text = "• $step",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ============================================================================
// 4. 3D COORDINATE & VECTOR STUDIO CARD (X, Y, Z)
// ============================================================================

@Composable
fun Coordinate3DVectorStudioCard(
    viewModel: MathViewModel,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    var bx by rememberManagedUiState(viewModel, "visual_explorer:tab_0:coord3d_bx", 3.0)
    var by by rememberManagedUiState(viewModel, "visual_explorer:tab_0:coord3d_by", 4.0)
    var bz by rememberManagedUiState(viewModel, "visual_explorer:tab_0:coord3d_bz", 5.0)
    var pitchDeg by rememberManagedUiState(viewModel, "visual_explorer:tab_0:coord3d_pitch", 24f)
    var yawDeg by rememberManagedUiState(viewModel, "visual_explorer:tab_0:coord3d_yaw", -36f)

    val analysis = remember(bx, by, bz) {
        VisualMathStudioEngine.analyze3DCoordinates(
            a = Point3DCoord(0.0, 0.0, 0.0),
            b = Point3DCoord(bx, by, bz)
        )
    }
    val (faces, lines) = remember(analysis) {
        VisualMathStudioEngine.build3DCoordinateSceneMesh(analysis)
    }

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("coord_3d_vector_studio_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "ত্রিমাত্রিক স্থানাঙ্ক ও ভেক্টর স্টুডিও (3D Coordinate & Vector Space)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "মূলবিন্দু O(0,0,0) থেকে P(x,y,z) বিন্দুর ৩ডি দূরত্ব d = √(x² + y² + z²) এবং মধ্যবিন্দু M নির্ণয়",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            yawDeg = ((yawDeg + dragAmount.x * 0.45f) % 360f + 360f) % 360f
                            pitchDeg = (pitchDeg - dragAmount.y * 0.45f).coerceIn(-80f, 80f)
                        }
                    }
                    .testTag("coord_3d_viewport_box")
            ) {
                Math3DStudioViewport(
                    faces = faces,
                    internalLines = lines,
                    camera = OrbitCameraState(pitchDeg = pitchDeg, yawDeg = yawDeg, zoom = 1.0f),
                    highlightMode = SolidAnatomyHighlight.ALL,
                    primaryColor = MaterialTheme.colorScheme.primary,
                    secondaryColor = MaterialTheme.colorScheme.secondary,
                    tertiaryColor = MaterialTheme.colorScheme.tertiary,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("X = ${VisualMathEngine.formatNumberBn(bx, 0)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Slider(value = bx.toFloat(), onValueChange = { bx = it.toInt().toDouble() }, valueRange = -6f..6f)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Y = ${VisualMathEngine.formatNumberBn(by, 0)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Slider(value = by.toFloat(), onValueChange = { by = it.toInt().toDouble() }, valueRange = -6f..6f)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Z = ${VisualMathEngine.formatNumberBn(bz, 0)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Slider(value = bz.toFloat(), onValueChange = { bz = it.toInt().toDouble() }, valueRange = -6f..6f)
                }
            }

            analysis.stepsBn.forEach { step ->
                Text(text = "• $step", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ============================================================================
// 5. ALGEBRA TILES & QUADRATIC / 3D SURFACE (z = f(x,y)) LAB CARDS
// ============================================================================

@Composable
fun AlgebraTilesLabCard(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    var x2Count by rememberManagedUiState(viewModel, "visual_explorer:tab_1:tiles_x2", 1)
    var xpCount by rememberManagedUiState(viewModel, "visual_explorer:tab_1:tiles_xp", 5)
    var xnCount by rememberManagedUiState(viewModel, "visual_explorer:tab_1:tiles_xn", 0)
    var upCount by rememberManagedUiState(viewModel, "visual_explorer:tab_1:tiles_up", 6)
    var unCount by rememberManagedUiState(viewModel, "visual_explorer:tab_1:tiles_un", 0)

    val analysis = remember(x2Count, xpCount, xnCount, upCount, unCount) {
        VisualMathStudioEngine.analyzeAlgebraTiles(
            xSquaredCountInput = x2Count,
            xPositiveInput = xpCount,
            xNegativeInput = xnCount,
            unitPositiveInput = upCount,
            unitNegativeInput = unCount
        )
    }

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("algebra_tiles_lab_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "বীজগণিত টাইলস ও উৎপাদক বিশ্লেষণ ল্যাব (Algebra Tiles & Factoring)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "রাশি: ${analysis.simplifiedExpressionBn}${analysis.factorizedFormBn?.let { " = $it" } ?: ""}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .testTag("algebra_tiles_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    var cursorX = 12f
                    val yTop = 18f
                    // Draw x^2 tiles
                    for (i in 0 until analysis.xSquaredCount) {
                        drawRect(Color(0xFF2563EB).copy(alpha = 0.8f), Offset(cursorX, yTop), Size(54f, 54f))
                        cursorX += 60f
                    }
                    // Draw +x tiles (green) and -x tiles (red)
                    for (i in 0 until analysis.xPositiveCount.coerceAtMost(5)) {
                        drawRect(Color(0xFF16A34A).copy(alpha = 0.85f), Offset(cursorX, yTop), Size(22f, 54f))
                        cursorX += 26f
                    }
                    for (i in 0 until analysis.xNegativeCount.coerceAtMost(4)) {
                        drawRect(Color(0xFFDC2626).copy(alpha = 0.85f), Offset(cursorX, yTop), Size(22f, 54f))
                        cursorX += 26f
                    }
                    // Draw +1 / -1 unit tiles on second row
                    var unitX = 12f
                    val unitY = 84f
                    for (i in 0 until analysis.unitPositiveCount.coerceAtMost(10)) {
                        drawRect(Color(0xFFD97706).copy(alpha = 0.85f), Offset(unitX, unitY), Size(22f, 22f))
                        unitX += 26f
                    }
                    for (i in 0 until analysis.unitNegativeCount.coerceAtMost(8)) {
                        drawRect(Color(0xFF9333EA).copy(alpha = 0.85f), Offset(unitX, unitY), Size(22f, 22f))
                        unitX += 26f
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("x² টাইলস: ${BengaliNumberFormatter.toBengaliDigits(x2Count.toString())}", style = MaterialTheme.typography.labelSmall)
                    Slider(value = x2Count.toFloat(), onValueChange = { x2Count = it.toInt() }, valueRange = 0f..3f)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("+x টাইলস: ${BengaliNumberFormatter.toBengaliDigits(xpCount.toString())}", style = MaterialTheme.typography.labelSmall)
                    Slider(value = xpCount.toFloat(), onValueChange = { xpCount = it.toInt() }, valueRange = 0f..6f)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("−x টাইলস: ${BengaliNumberFormatter.toBengaliDigits(xnCount.toString())}", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = xnCount.toFloat(),
                        onValueChange = { xnCount = it.toInt() },
                        valueRange = 0f..6f,
                        modifier = Modifier.testTag("slider_tile_xn")
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("+1 একক: ${BengaliNumberFormatter.toBengaliDigits(upCount.toString())}", style = MaterialTheme.typography.labelSmall)
                    Slider(value = upCount.toFloat(), onValueChange = { upCount = it.toInt() }, valueRange = 0f..9f)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("−1 একক: ${BengaliNumberFormatter.toBengaliDigits(unCount.toString())}", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = unCount.toFloat(),
                        onValueChange = { unCount = it.toInt() },
                        valueRange = 0f..9f,
                        modifier = Modifier.testTag("slider_tile_un")
                    )
                }
            }

            analysis.stepsBn.forEach { step ->
                Text(text = "• $step", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun QuadraticAndSurface3DStudioCard(
    viewModel: MathViewModel,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    var quadA by rememberManagedUiState(viewModel, "visual_explorer:tab_1:quad_a", 1.0)
    var quadB by rememberManagedUiState(viewModel, "visual_explorer:tab_1:quad_b", -2.0)
    var quadC by rememberManagedUiState(viewModel, "visual_explorer:tab_1:quad_c", -3.0)
    var show3DSurface by rememberManagedUiState(viewModel, "visual_explorer:tab_1:show_3d_surface", true)
    var surfacePreset by rememberManagedUiState(viewModel, "visual_explorer:tab_1:surf_preset", Surface3DPreset.PARABOLOID)
    var pitchDeg by rememberManagedUiState(viewModel, "visual_explorer:tab_1:surf_pitch", 28f)
    var yawDeg by rememberManagedUiState(viewModel, "visual_explorer:tab_1:surf_yaw", -35f)

    val quadAnalysis = remember(quadA, quadB, quadC) {
        VisualMathStudioEngine.analyzeQuadratic2D(quadA, quadB, quadC)
    }
    val surfaceAnalysis = remember(surfacePreset, quadA, quadB) {
        VisualMathStudioEngine.build3DSurfaceMesh(
            preset = surfacePreset,
            paramAInput = quadA,
            paramBInput = max(0.5, abs(quadB).coerceAtLeast(1.0)),
            gridResolution = 10
        )
    }

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("function_and_3d_surface_studio_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ফাংশন গ্রাফিং ও ৩ডি সারফেস স্টুডিও (2D Parabola & z = f(x,y) 3D Surface)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (show3DSurface) surfacePreset.formulaDisplay else quadAnalysis.formulaTextBn,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                FilterChip(
                    selected = show3DSurface,
                    onClick = { show3DSurface = !show3DSurface },
                    label = {
                        Text(
                            text = if (show3DSurface) "২ডি প্যারাবোলা" else "৩ডি সারফেস z=f(x,y)",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.testTag("toggle_3d_surface_mode_btn")
                )
            }

            if (show3DSurface) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(Surface3DPreset.entries) { preset ->
                        FilterChip(
                            selected = surfacePreset == preset,
                            onClick = { surfacePreset = preset },
                            label = { Text(preset.titleBn, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("surface_3d_preset_${preset.id}")
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(235.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                        .pointerInput(surfacePreset) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                yawDeg = ((yawDeg + dragAmount.x * 0.45f) % 360f + 360f) % 360f
                                pitchDeg = (pitchDeg - dragAmount.y * 0.45f).coerceIn(-80f, 80f)
                            }
                        }
                        .testTag("surface_3d_viewport_box")
                ) {
                    Math3DStudioViewport(
                        faces = surfaceAnalysis.faces,
                        internalLines = surfaceAnalysis.lines,
                        camera = OrbitCameraState(pitchDeg = pitchDeg, yawDeg = yawDeg, zoom = 1.0f),
                        highlightMode = SolidAnatomyHighlight.ALL,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        secondaryColor = MaterialTheme.colorScheme.secondary,
                        tertiaryColor = MaterialTheme.colorScheme.tertiary,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = surfaceAnalysis.explanationBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val primaryColor = MaterialTheme.colorScheme.primary
                val axisColor = MaterialTheme.colorScheme.outline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                        .testTag("quadratic_2d_canvas")
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val unitPx = min(size.width, size.height) / 14f

                        drawLine(axisColor, Offset(0f, cy), Offset(size.width, cy), 2f)
                        drawLine(axisColor, Offset(cx, 0f), Offset(cx, size.height), 2f)

                        val path = Path()
                        val samples = 60
                        for (i in 0..samples) {
                            val x = -6.0 + (12.0 * i) / samples
                            val y = quadAnalysis.a * x * x + quadAnalysis.b * x + quadAnalysis.c
                            val sx = cx + x.toFloat() * unitPx
                            val sy = (cy - y.toFloat() * unitPx).coerceIn(-40f, size.height + 40f)
                            if (i == 0) path.moveTo(sx, sy) else path.lineTo(sx, sy)
                        }
                        drawPath(path, color = primaryColor, style = Stroke(width = 3.8f))

                        // Vertex marker
                        val vxScreen = Offset(
                            (cx + quadAnalysis.vertexX.toFloat() * unitPx).coerceIn(8f, size.width - 8f),
                            (cy - quadAnalysis.vertexY.toFloat() * unitPx).coerceIn(8f, size.height - 8f)
                        )
                        drawCircle(Color(0xFFDC2626), radius = 7f, center = vxScreen)
                    }
                }

                quadAnalysis.stepsBn.forEach { step ->
                    Text(text = "• $step", style = MaterialTheme.typography.bodySmall)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("প্যারামিটার a = ${VisualMathEngine.formatNumberBn(quadA, 1)}", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = quadA.toFloat(),
                        onValueChange = {
                            val v = (Math.round(it * 2.0) / 2.0)
                            quadA = if (abs(v) < 0.2) 0.5 else v
                        },
                        valueRange = -2.5f..2.5f
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("প্যারামিটার b = ${VisualMathEngine.formatNumberBn(quadB, 1)}", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = quadB.toFloat(),
                        onValueChange = { quadB = it.toInt().toDouble() },
                        valueRange = -5f..5f
                    )
                }
                if (!show3DSurface) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ধ্রুবক c = ${VisualMathEngine.formatNumberBn(quadC, 1)}", style = MaterialTheme.typography.labelSmall)
                        Slider(
                            value = quadC.toFloat(),
                            onValueChange = { quadC = it.toInt().toDouble() },
                            valueRange = -5f..5f
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 6. FRACTION OPERATIONS LAB CARD (+, -, ×, ÷)
// ============================================================================

@Composable
fun FractionOperationsLabCard(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    var num1 by rememberManagedUiState(viewModel, "visual_explorer:tab_2:fop_n1", 1)
    var den1 by rememberManagedUiState(viewModel, "visual_explorer:tab_2:fop_d1", 2)
    var num2 by rememberManagedUiState(viewModel, "visual_explorer:tab_2:fop_n2", 1)
    var den2 by rememberManagedUiState(viewModel, "visual_explorer:tab_2:fop_d2", 3)
    var opType by rememberManagedUiState(viewModel, "visual_explorer:tab_2:fop_op", FractionOpType.ADD)

    val analysis = remember(num1, den1, num2, den2, opType) {
        VisualMathStudioEngine.analyzeFractionOperation(num1, den1, num2, den2, opType)
    }

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("fraction_operations_lab_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "ভগ্নাংশের প্রক্রিয়া ল্যাব: যোগ, বিয়োগ, গুণ ও ভাগ (Fraction Operations)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FractionOpType.entries) { op ->
                    FilterChip(
                        selected = opType == op,
                        onClick = { opType = op },
                        label = { Text(op.titleBn, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("fraction_op_chip_${op.name.lowercase()}")
                    )
                }
            }

            val n1Bn = BengaliNumberFormatter.toBengaliDigits(analysis.num1.toString())
            val d1Bn = BengaliNumberFormatter.toBengaliDigits(analysis.den1.toString())
            val n2Bn = BengaliNumberFormatter.toBengaliDigits(analysis.num2.toString())
            val d2Bn = BengaliNumberFormatter.toBengaliDigits(analysis.den2.toString())
            val resNBn = BengaliNumberFormatter.toBengaliDigits(analysis.simplifiedNum.toString())
            val resDBn = BengaliNumberFormatter.toBengaliDigits(analysis.simplifiedDen.toString())

            Text(
                text = "($n1Bn/$d1Bn) ${opType.symbol} ($n2Bn/$d2Bn) = $resNBn/$resDBn (≈ ${VisualMathEngine.formatNumberBn(analysis.decimalValue, 2)})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("১ম ভগ্নাংশ ($n1Bn/$d1Bn)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Slider(value = num1.toFloat(), onValueChange = { num1 = it.toInt().coerceIn(1, 8) }, valueRange = 1f..8f)
                    Slider(value = den1.toFloat(), onValueChange = { den1 = it.toInt().coerceIn(1, 8) }, valueRange = 1f..8f)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("২য় ভগ্নাংশ ($n2Bn/$d2Bn)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Slider(value = num2.toFloat(), onValueChange = { num2 = it.toInt().coerceIn(1, 8) }, valueRange = 1f..8f)
                    Slider(value = den2.toFloat(), onValueChange = { den2 = it.toInt().coerceIn(1, 8) }, valueRange = 1f..8f)
                }
            }

            analysis.stepsBn.forEach { step ->
                Text(text = "• $step", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
