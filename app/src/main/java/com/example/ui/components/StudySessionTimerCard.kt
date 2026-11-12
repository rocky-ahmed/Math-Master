package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.BengaliNumberFormatter
import com.example.ui.StudySessionTimerState
import com.example.ui.StudyTimerDisplayStatus
import com.example.ui.theme.MathSpacing

private val CompactBengaliTextPlatformStyle = PlatformTextStyle(includeFontPadding = false)
private val CompactBengaliLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

/**
 * Formats seconds into Bengali digital clock string (MM:SS or HH:MM:SS).
 */
fun formatStudyTimerClockBn(totalSeconds: Int): String {
    val safeSec = totalSeconds.coerceAtLeast(0)
    val hours = safeSec / 3600
    val minutes = (safeSec % 3600) / 60
    val seconds = safeSec % 60
    val mm = minutes.toString().padStart(2, '0')
    val ss = seconds.toString().padStart(2, '0')
    return if (hours > 0) {
        val hh = hours.toString().padStart(2, '0')
        BengaliNumberFormatter.toBn("$hh:$mm:$ss")
    } else {
        BengaliNumberFormatter.toBn("$mm:$ss")
    }
}

/**
 * Formats total seconds into human-readable Bengali duration (e.g., "১২ মি. ৩০ সে.").
 */
fun formatStudyDurationSummaryBn(totalSeconds: Int): String {
    val safeSec = totalSeconds.coerceAtLeast(0)
    val hours = safeSec / 3600
    val minutes = (safeSec % 3600) / 60
    val seconds = safeSec % 60
    return when {
        hours > 0 -> "${BengaliNumberFormatter.toBn(hours)} ঘণ্টা ${BengaliNumberFormatter.toBn(minutes)} মি."
        minutes > 0 -> "${BengaliNumberFormatter.toBn(minutes)} মি. ${BengaliNumberFormatter.toBn(seconds)} সে."
        else -> "${BengaliNumberFormatter.toBn(seconds)} সে."
    }
}

/**
 * Study Timer 2.0 (BUG-78):
 * Default-OFF initialization + Compact OFF-State Home UI (~64-72dp height at 1.0x font scale)
 * with smooth expansion into the full interactive Study Timer panel when explicitly enabled.
 */
@Composable
fun StudySessionTimerSection(
    timerState: StudySessionTimerState,
    onToggleVisibility: () -> Unit,
    onToggleRunning: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onTick: (Int) -> Unit = {},
    onFinishSession: () -> Unit,
    onResetSession: () -> Unit,
    onSelectGoalMinutes: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val goalOptions = listOf(15, 30, 45, 60)
    val isPanelEnabled = timerState.isVisibleOnHome
    val displayStatus = timerState.displayStatus

    val cardRadius = MathSpacing.cornerRadiusStandard
    val borderColor = when {
        timerState.isRunning -> MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
        isPanelEnabled -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isPanelEnabled) 4.dp else 2.dp,
                shape = RoundedCornerShape(cardRadius),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cardRadius)
            )
            .testTag("study_session_timer_card"),
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isPanelEnabled) 2.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MathSpacing.cardPaddingStandard,
                    vertical = if (isPanelEnabled) MathSpacing.cardPaddingCompact else MathSpacing.xs
                )
        ) {
            // Compact Horizontal Header Row (68.0dp total card height when OFF at 1.0x font scale)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = MathSpacing.minTouchTarget),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                when {
                                    timerState.isRunning -> listOf(Color(0xFF059669), Color(0xFF047857))
                                    isPanelEnabled -> listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                    else -> listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                        MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "পড়াশোনা সেশন টাইমার আইকন",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(MathSpacing.sm))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy((-10).dp, Alignment.CenterVertically)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MathSpacing.xs)
                    ) {
                        Text(
                            text = "পড়াশোনা সেশন টাইমার",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 18.sp,
                                platformStyle = CompactBengaliTextPlatformStyle,
                                lineHeightStyle = CompactBengaliLineHeightStyle
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .testTag("study_timer_title_text")
                        )

                        if (isPanelEnabled) {
                            val pillColor = when (displayStatus) {
                                StudyTimerDisplayStatus.RUNNING -> Color(0xFF059669)
                                StudyTimerDisplayStatus.PAUSED -> Color(0xFFD97706)
                                StudyTimerDisplayStatus.COMPLETED -> Color(0xFF059669)
                                StudyTimerDisplayStatus.READY -> MaterialTheme.colorScheme.primary
                                StudyTimerDisplayStatus.OFF -> MaterialTheme.colorScheme.outline
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MathSpacing.blendedContainerColor(
                                    containerColor = pillColor,
                                    fraction = 0.14f
                                ),
                                modifier = Modifier.testTag("study_timer_status_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(pillColor)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = timerState.statusLabelBn,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            platformStyle = CompactBengaliTextPlatformStyle
                                        ),
                                        color = pillColor,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = if (isPanelEnabled) {
                            "আজকের অধ্যয়ন: ${formatStudyDurationSummaryBn(timerState.todayTotalSeconds)} • লক্ষ্য: ${BengaliNumberFormatter.toBn(timerState.dailyGoalMinutes)} মি."
                        } else {
                            "বন্ধ • চালু করতে টগল করুন"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 15.sp,
                            platformStyle = CompactBengaliTextPlatformStyle,
                            lineHeightStyle = CompactBengaliLineHeightStyle
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("study_timer_status_text")
                    )
                }

                Spacer(modifier = Modifier.width(MathSpacing.xs))

                // Accessible ON/OFF Switch with guaranteed >= 48dp interactive target
                Switch(
                    checked = isPanelEnabled,
                    onCheckedChange = { onToggleVisibility() },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .semantics { contentDescription = "পড়াশোনা সেশন টাইমার টগল" }
                        .testTag("study_timer_toggle_switch")
                )
            }

            // Expandable Full Study Session Timer Body (strictly hidden when OFF)
            if (isPanelEnabled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MathSpacing.sm)
                        .testTag("study_timer_expanded_body")
                ) {
                    val clockContainerColor = MathSpacing.blendedContainerColor(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        fraction = 0.45f
                    )
                    // Main Digital Clock Display & Primary Controls Row
                    Surface(
                        shape = RoundedCornerShape(MathSpacing.cornerRadiusCompact),
                        color = clockContainerColor,
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = MathSpacing.cardPaddingStandard,
                                    vertical = MathSpacing.sm
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "বর্তমান অধ্যয়ন সেশন",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatStudyTimerClockBn(timerState.currentSessionSeconds),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.2.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.testTag("study_timer_clock_text")
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MathSpacing.xs)
                            ) {
                                // Start / Pause Button (minimum 48dp touch height)
                                Surface(
                                    shape = RoundedCornerShape(MathSpacing.cornerRadiusCompact),
                                    color = if (timerState.isRunning) {
                                        Color(0xFFD97706)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier
                                        .heightIn(min = MathSpacing.minTouchTarget)
                                        .clip(RoundedCornerShape(MathSpacing.cornerRadiusCompact))
                                        .clickable { onToggleRunning() }
                                        .testTag("study_timer_start_pause_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (timerState.isRunning) "বিরতি দিন" else "শুরু করুন",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (timerState.isRunning) "বিরতি দিন" else "শুরু করুন",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }

                                // Reset Session IconButton (48dp touch target)
                                IconButton(
                                    onClick = onResetSession,
                                    enabled = timerState.currentSessionSeconds > 0 || timerState.isRunning,
                                    modifier = Modifier
                                        .size(MathSpacing.minTouchTarget)
                                        .testTag("study_timer_reset_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "সেশন রিসেট করুন",
                                        tint = if (timerState.currentSessionSeconds > 0 || timerState.isRunning) {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        } else {
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(MathSpacing.sm))

                    // Daily Study Goal Progress Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (timerState.isDailyGoalReached) {
                                    "দৈনিক লক্ষ্যমাত্রা অর্জিত! 🎉"
                                } else {
                                    "দৈনিক অধ্যয়ন অগ্রগতি (${BengaliNumberFormatter.toBn((timerState.dailyGoalProgressFraction * 100).toInt())}%)"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (timerState.isDailyGoalReached) {
                                    Color(0xFF059669)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            Spacer(modifier = Modifier.width(MathSpacing.xs))

                            Text(
                                text = "আজ ${BengaliNumberFormatter.toBn(timerState.completedSessionsToday)}টি সেশন • মোট ${formatStudyDurationSummaryBn(timerState.allTimeTotalSeconds)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { timerState.dailyGoalProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("study_timer_goal_progress"),
                            color = if (timerState.isDailyGoalReached) {
                                Color(0xFF059669)
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(MathSpacing.xs))

                    // Daily Goal Chips + Complete & Save Session Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            goalOptions.forEach { goalMin ->
                                val isSelected = timerState.dailyGoalMinutes == goalMin
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectGoalMinutes(goalMin) },
                                    label = {
                                        Text(
                                            text = "${BengaliNumberFormatter.toBn(goalMin)} মি.",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.testTag("study_timer_goal_chip_$goalMin")
                                )
                            }
                        }

                        if (timerState.currentSessionSeconds > 0) {
                            Spacer(modifier = Modifier.width(MathSpacing.xs))
                            OutlinedButton(
                                onClick = onFinishSession,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .heightIn(min = MathSpacing.minTouchTarget)
                                    .testTag("study_timer_save_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "সেশন সংরক্ষণ করুন",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "সম্পন্ন",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
