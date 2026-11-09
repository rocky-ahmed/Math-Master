package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.BengaliNumberFormatter
import com.example.ui.StudySessionTimerState
import kotlinx.coroutines.delay

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
 * Persistent Study Session Timer component on the Home Screen that users can toggle on/off
 * to track how much time they spend learning.
 */
@Composable
fun StudySessionTimerSection(
    timerState: StudySessionTimerState,
    onToggleVisibility: () -> Unit,
    onToggleRunning: () -> Unit,
    onTick: (Int) -> Unit,
    onFinishSession: () -> Unit,
    onResetSession: () -> Unit,
    onSelectGoalMinutes: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1-second coroutine ticker while the Study Session timer is running
    LaunchedEffect(timerState.isRunning) {
        while (timerState.isRunning) {
            delay(1000L)
            onTick(1)
        }
    }

    val goalOptions = listOf(15, 30, 45, 60)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
            .border(
                width = 1.dp,
                color = if (timerState.isRunning) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                },
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("study_session_timer_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Top Header Row: Title, Running Status Pill, and Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                if (timerState.isRunning) {
                                    listOf(Color(0xFF059669), Color(0xFF047857))
                                } else {
                                    listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "স্টাডি সেশন টাইমার আইকন",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "স্টাডি সেশন টাইমার",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (timerState.isRunning) {
                                Color(0xFF059669).copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (timerState.isRunning) Color(0xFF059669)
                                            else MaterialTheme.colorScheme.outline
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (timerState.isRunning) "চলমান" else "বিরতি",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (timerState.isRunning) {
                                        Color(0xFF059669)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (timerState.isVisibleOnHome) {
                            "আজকের অধ্যয়ন: ${formatStudyDurationSummaryBn(timerState.todayTotalSeconds)} • লক্ষ্য: ${BengaliNumberFormatter.toBn(timerState.dailyGoalMinutes)} মি."
                        } else {
                            "চলমান: ${formatStudyTimerClockBn(timerState.currentSessionSeconds)} • আজকের মোট: ${formatStudyDurationSummaryBn(timerState.todayTotalSeconds)}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quick Play/Pause when collapsed so users can control timer in both states
                if (!timerState.isVisibleOnHome) {
                    IconButton(
                        onClick = onToggleRunning,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("study_timer_compact_play_pause_btn")
                    ) {
                        Icon(
                            imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (timerState.isRunning) "টাইমার বিরতি দিন" else "টাইমার শুরু করুন",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Toggle Switch to expand / collapse the persistent Study Session timer on Home Screen
                Switch(
                    checked = timerState.isVisibleOnHome,
                    onCheckedChange = { onToggleVisibility() },
                    modifier = Modifier
                        .semantics { contentDescription = "স্টাডি সেশন টাইমার টগল" }
                        .testTag("study_timer_toggle_switch")
                )

                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("study_timer_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (timerState.isVisibleOnHome) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (timerState.isVisibleOnHome) "টাইমার সংকুচিত করুন" else "টাইমার প্রসারিত করুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expandable Full Study Session Timer Body
            AnimatedVisibility(
                visible = timerState.isVisibleOnHome,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    // Main Digital Clock Display & Primary Controls Row
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "বর্তমান অধ্যয়ন সেশন",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Start / Pause Button
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (timerState.isRunning) {
                                        Color(0xFFD97706)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier
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

                                // Reset Session IconButton
                                IconButton(
                                    onClick = onResetSession,
                                    enabled = timerState.currentSessionSeconds > 0 || timerState.isRunning,
                                    modifier = Modifier
                                        .size(48.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

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
                                }
                            )

                            Text(
                                text = "আজ ${BengaliNumberFormatter.toBn(timerState.completedSessionsToday)}টি সেশন • মোট ${formatStudyDurationSummaryBn(timerState.allTimeTotalSeconds)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

                    Spacer(modifier = Modifier.height(10.dp))

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
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = onFinishSession,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("study_timer_save_btn")
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
