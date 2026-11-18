package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.ui.theme.MathSpacing

/**
 * Reusable local design tokens for the Math Problem Solver workspace.
 * Built atop MaterialTheme.colorScheme and MathSpacing so Light & Dark themes
 * stay cohesive with the rest of Math Master.
 */
object SolverDesignTokens {
    val cardRadiusHero: Dp = 18.dp
    val cardRadiusStandard: Dp = 16.dp
    val cardRadiusStep: Dp = 14.dp
    val innerPanelRadius: Dp = 12.dp
    val chipRadius: Dp = 10.dp

    val sectionSpacing: Dp = 14.dp
    val itemSpacing: Dp = 10.dp
    val cardPaddingStandard: Dp = 16.dp
    val cardPaddingCompact: Dp = 12.dp

    val iconBadgeSize: Dp = 42.dp
    val stepBadgeSize: Dp = 28.dp
    val minTouchTarget: Dp = 48.dp
    val segmentMinHeight: Dp = 58.dp

    const val EXPANSION_ANIM_MS = 200

    @Composable
    fun isDarkTheme(): Boolean = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    @Composable
    fun modeAccentColor(mode: SolverMode): Color = when (mode) {
        SolverMode.LEARNING -> MaterialTheme.colorScheme.primary
        SolverMode.QUICK -> MaterialTheme.colorScheme.secondary
        SolverMode.BCS -> MaterialTheme.colorScheme.tertiary
    }

    @Composable
    fun modeContainerColor(mode: SolverMode): Color = when (mode) {
        SolverMode.LEARNING -> MaterialTheme.colorScheme.primaryContainer
        SolverMode.QUICK -> MaterialTheme.colorScheme.secondaryContainer
        SolverMode.BCS -> MaterialTheme.colorScheme.tertiaryContainer
    }

    @Composable
    fun modeOnContainerColor(mode: SolverMode): Color = when (mode) {
        SolverMode.LEARNING -> MaterialTheme.colorScheme.onPrimaryContainer
        SolverMode.QUICK -> MaterialTheme.colorScheme.onSecondaryContainer
        SolverMode.BCS -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    @Composable
    fun successContainerColor(): Color = if (isDarkTheme()) {
        Color(0xFF064E3B).copy(alpha = 0.7f)
    } else {
        Color(0xFFECFDF5)
    }

    @Composable
    fun successBorderColor(): Color = Color(0xFF10B981).copy(alpha = 0.45f)

    @Composable
    fun successContentColor(): Color = if (isDarkTheme()) {
        Color(0xFF6EE7B7)
    } else {
        Color(0xFF065F46)
    }

    @Composable
    fun successBodyColor(): Color = if (isDarkTheme()) {
        Color(0xFFD1FAE5)
    } else {
        Color(0xFF064E3B)
    }

    @Composable
    fun warningContainerColor(): Color = if (isDarkTheme()) {
        Color(0xFF451A03).copy(alpha = 0.7f)
    } else {
        Color(0xFFFFFBEB)
    }

    @Composable
    fun warningBorderColor(): Color = Color(0xFFF59E0B).copy(alpha = 0.45f)

    @Composable
    fun warningTitleColor(): Color = if (isDarkTheme()) {
        Color(0xFFFDE68A)
    } else {
        Color(0xFF92400E)
    }

    @Composable
    fun warningBodyColor(): Color = if (isDarkTheme()) {
        Color(0xFFFEF3C7)
    } else {
        Color(0xFF78350F)
    }

    @Composable
    fun warningIconColor(): Color = if (isDarkTheme()) {
        Color(0xFFFBBF24)
    } else {
        Color(0xFFD97706)
    }

    @Composable
    fun mistakeContainerColor(): Color = if (isDarkTheme()) {
        Color(0xFF450A0A).copy(alpha = 0.72f)
    } else {
        Color(0xFFFEF2F2)
    }

    @Composable
    fun mistakeContentColor(): Color = if (isDarkTheme()) {
        Color(0xFFFCA5A5)
    } else {
        Color(0xFF991B1B)
    }
}

/**
 * UI-level metadata for the three Solver modes matching the redesigned labels & subtitles
 * while preserving the underlying SolverMode enum unchanged.
 */
@Immutable
data class SolverModeUiMeta(
    val mode: SolverMode,
    val labelBn: String,
    val subtitleBn: String,
    val detailHintBn: String,
    val icon: ImageVector
)

fun solverModeUiMeta(mode: SolverMode): SolverModeUiMeta = when (mode) {
    SolverMode.LEARNING -> SolverModeUiMeta(
        mode = mode,
        labelBn = "শেখার মোড",
        subtitleBn = "ধাপে ধাপে বুঝুন",
        detailHintBn = "প্রশ্ন বিশ্লেষণ, সূত্রের চলক, প্রতিটি ধাপের কারণ, ইঙ্গিত ও অনুশীলনসহ গাইডেড শেখা",
        icon = Icons.Default.School
    )
    SolverMode.QUICK -> SolverModeUiMeta(
        mode = mode,
        labelBn = "দ্রুত সমাধান",
        subtitleBn = "দ্রুত ফলাফল দেখুন",
        detailHintBn = "চূড়ান্ত উত্তর, মূল সূত্র, সংক্ষিপ্ত হিসাবের ধাপ ও শুদ্ধি পরীক্ষা এক নজরে",
        icon = Icons.Default.Speed
    )
    SolverMode.BCS -> SolverModeUiMeta(
        mode = mode,
        labelBn = "BCS প্রস্তুতি",
        subtitleBn = "শর্টকাট ও পরীক্ষার কৌশল",
        detailHintBn = "বৈধ শর্টকাট সূত্র, প্রয়োগের শর্ত, পরীক্ষার সাধারণ ফাঁদ ও BPSC সিলেবাস ম্যাপিং",
        icon = Icons.Default.AutoAwesome
    )
}

/**
 * Categorized topic groups for rapid filtering across all 16 canonical SolverTopic entries.
 * Default is ALL so all 16 topics remain immediately discoverable without extra navigation depth.
 */
enum class SolverTopicCategory(
    val id: String,
    val titleBn: String,
    val topics: List<SolverTopic>
) {
    ALL(
        id = "all",
        titleBn = "সব (১৬)",
        topics = SolverTopic.entries
    ),
    ARITHMETIC(
        id = "arithmetic",
        titleBn = "পাটিগণিত",
        topics = listOf(
            SolverTopic.BASIC_ARITHMETIC,
            SolverTopic.FRACTIONS_DECIMALS,
            SolverTopic.AVERAGE,
            SolverTopic.TIME_AND_WORK,
            SolverTopic.SPEED_DISTANCE_TIME
        )
    ),
    COMMERCIAL(
        id = "commercial",
        titleBn = "শতকরা ও অর্থ",
        topics = listOf(
            SolverTopic.PERCENTAGE,
            SolverTopic.RATIO_PROPORTION,
            SolverTopic.PROFIT_LOSS,
            SolverTopic.SIMPLE_INTEREST
        )
    ),
    NUMBER_THEORY(
        id = "number_theory",
        titleBn = "সংখ্যা ও মূল",
        topics = listOf(
            SolverTopic.NUMBER_SYSTEM_DIVISIBILITY,
            SolverTopic.HCF_LCM,
            SolverTopic.SQUARES_CUBES_ROOTS
        )
    ),
    ALGEBRA(
        id = "algebra",
        titleBn = "বীজগণিত",
        topics = listOf(
            SolverTopic.LINEAR_EQUATIONS,
            SolverTopic.QUADRATIC_EQUATIONS,
            SolverTopic.FACTORIZATION,
            SolverTopic.SIMPLIFICATION
        )
    );

    companion object {
        fun categoryOf(topic: SolverTopic): SolverTopicCategory =
            entries.firstOrNull { it != ALL && topic in it.topics } ?: ALL
    }
}

/**
 * Compact, inviting workspace header exposing controls immediately without consuming excess vertical space.
 */
@Composable
fun SolverWorkspaceHeader(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(SolverDesignTokens.cardRadiusStandard),
        color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.primaryContainer, 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("solver_workspace_header")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(SolverDesignTokens.iconBadgeSize)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "গণিত সমাধান স্টুডিও",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "গণিত বুঝে সমাধান করুন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ধাপে ধাপে শিখুন, অনুশীলন করুন এবং পরীক্ষার জন্য প্রস্তুত হোন।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Reusable compact segmented control for mutually exclusive binary/ternary options
 * (e.g., 2x2 vs 3x3 simultaneous system, Simple vs Compound interest).
 */
@Composable
fun <T> CompactSegmentedSwitch(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    subtitleProvider: ((T) -> String)? = null,
    testTagProvider: (T) -> String,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MathSpacing.blendedContainerColor(MaterialTheme.colorScheme.surfaceVariant, 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) accentColor else Color.Transparent,
                    animationSpec = tween(durationMillis = SolverDesignTokens.EXPANSION_ANIM_MS),
                    label = "compact_segment_bg"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = SolverDesignTokens.EXPANSION_ANIM_MS),
                    label = "compact_segment_fg"
                )
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = containerColor,
                    contentColor = contentColor,
                    shadowElevation = if (isSelected) 1.dp else 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .heightIn(min = SolverDesignTokens.minTouchTarget)
                        .clip(RoundedCornerShape(9.dp))
                        .semantics {
                            selected = isSelected
                        }
                        .clickable(
                            role = Role.Tab,
                            onClick = { onOptionSelected(option) }
                        )
                        .testTag(testTagProvider(option))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = labelProvider(option),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        subtitleProvider?.let { sub ->
                            val subText = sub(option)
                            if (subText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = subText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = contentColor.copy(alpha = if (isSelected) 0.9f else 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visible topic-aware input validation banner shown inside TopicInputCard when user input
 * has an actionable issue (empty fields, zero divisor/denominator, missing '=' in equation, etc.).
 */
@Composable
fun SolverInputValidationBanner(
    messageBn: String?,
    isError: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (messageBn.isNullOrBlank()) return
    val bgColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
    } else {
        SolverDesignTokens.warningContainerColor()
    }
    val fgColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        SolverDesignTokens.warningBodyColor()
    }
    val iconTint = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        SolverDesignTokens.warningIconColor()
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(
            1.dp,
            if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.4f) else SolverDesignTokens.warningBorderColor()
        ),
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite }
            .testTag("solver_input_validation_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.WarningAmber else Icons.Default.Info,
                contentDescription = if (isError) "ইনপুট ত্রুটি" else "ইনপুট নির্দেশনা",
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = messageBn,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = fgColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Compact Mode Badge pill used in result cards to reinforce mode identity without relying on color alone.
 */
@Composable
fun SolverModeBadgePill(
    mode: SolverMode,
    modifier: Modifier = Modifier
) {
    val meta = solverModeUiMeta(mode)
    val bg = SolverDesignTokens.modeContainerColor(mode)
    val fg = SolverDesignTokens.modeOnContainerColor(mode)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = meta.icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = meta.labelBn,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = fg
            )
        }
    }
}

/**
 * Compact badge indicating verified answer status in Quick & Learning Mode.
 */
@Composable
fun SolverExactnessPill(
    isExactResult: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isExactResult) {
        SolverDesignTokens.successContainerColor()
    } else {
        SolverDesignTokens.warningContainerColor()
    }
    val fg = if (isExactResult) {
        SolverDesignTokens.successContentColor()
    } else {
        SolverDesignTokens.warningTitleColor()
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExactResult) Icons.Default.CheckCircle else Icons.Default.Functions,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isExactResult) "নির্ভুল মান (Exact)" else "আসন্ন মান (Approximate)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = fg
            )
        }
    }
}
