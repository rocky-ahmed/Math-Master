package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.BengaliNumberFormatter
import com.example.math.MentalMathCategory
import com.example.math.MentalMathDifficulty
import com.example.math.MentalMathMode
import com.example.math.QuestionInputFormat
import com.example.ui.MathViewModel
import com.example.ui.components.MathSectionHeader
import com.example.ui.components.MathTopAppBar

@Composable
fun MentalMathScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val quizScores by viewModel.quizScores.collectAsStateWithLifecycle()
    val mentalMathHistory = remember(quizScores) {
        quizScores.filter { it.topicId.startsWith("MENTAL_MATH_") }
    }

    var selectedCategory by com.example.ui.rememberManagedUiState(viewModel, "mental_math:selectedCategory", MentalMathCategory.ADDITION)
    var selectedDifficulty by com.example.ui.rememberManagedUiState(viewModel, "mental_math:selectedDifficulty", MentalMathDifficulty.EASY)
    var selectedMode by com.example.ui.rememberManagedUiState(viewModel, "mental_math:selectedMode", MentalMathMode.TIMED_SPRINT)
    var sprintDurationSec by com.example.ui.rememberManagedUiState(viewModel, "mental_math:sprintDurationSec", 60)
    var fixedQuestionCount by com.example.ui.rememberManagedUiState(viewModel, "mental_math:fixedQuestionCount", 10)
    var selectedInputFormat by com.example.ui.rememberManagedUiState(viewModel, "mental_math:selectedInputFormat", QuestionInputFormat.MCQ)

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_mental_math")
    ) {
        MathTopAppBar(
            title = "মেন্টাল ম্যাথ চ্যালেঞ্জ",
            subtitle = "১০টি বিভাগ ও ৭টি চ্যালেঞ্জ মোডে দ্রুত মানসিক গণনা",
            onBackClick = { viewModel.navigateBack() }
        )

        val mainListState = com.example.ui.rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "mental_math:main"
        )

        LazyColumn(
            state = mainListState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("mental_math_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "স্পিড রান ও মানসিক তীব্রতা",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "মেন্টাল ম্যাথ চ্যালেঞ্জ",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "কাগজ-কলম ছাড়াই মনের ভেতর দ্রুত হিসাব করে সঠিক উত্তর নির্বাচন করুন।",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        if (mentalMathHistory.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            val bestScore = mentalMathHistory.maxOfOrNull { it.score } ?: 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "আপনার সেরা স্কোর:",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "${BengaliNumberFormatter.toBn(bestScore)} পয়েন্ট",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Challenge Mode Selector (All modes in a scrollable row, preserving mode_${mode.id} tags)
            item {
                Column {
                    Text(
                        text = "১. চ্যালেঞ্জের ধরন নির্বাচন করুন",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val modeRowState = com.example.ui.rememberManagedLazyListState(viewModel, "mental_math:mode_row")
                    LazyRow(
                        state = modeRowState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(MentalMathMode.values()) { mode ->
                            val isSelected = selectedMode == mode
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .width(175.dp)
                                    .clickable {
                                        selectedMode = mode
                                        if (mode.isTimed) {
                                            sprintDurationSec = mode.defaultSeconds
                                        } else {
                                            fixedQuestionCount = mode.defaultQuestions
                                        }
                                    }
                                    .testTag("mode_${mode.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = mode.titleBn,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = mode.subtitleBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Configurable Timer or Question Count
            item {
                Column {
                    Text(
                        text = if (selectedMode.isTimed) "২. সময়সীমা নির্ধারণ (সেকেন্ড)" else "২. প্রশ্ন সংখ্যা নির্ধারণ",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val timerRowState = com.example.ui.rememberManagedLazyListState(viewModel, "mental_math:timer_row")
                    LazyRow(
                        state = timerRowState,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (selectedMode.isTimed) {
                            items(listOf(30, 60, 120, 300)) { sec ->
                                FilterChip(
                                    selected = sprintDurationSec == sec,
                                    onClick = { sprintDurationSec = sec },
                                    label = {
                                        Text("${BengaliNumberFormatter.toBn(sec)} সেকেন্ড", fontWeight = FontWeight.Bold)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    modifier = Modifier.testTag("timer_chip_$sec")
                                )
                            }
                        } else {
                            items(listOf(10, 15, 20, 30)) { count ->
                                FilterChip(
                                    selected = fixedQuestionCount == count,
                                    onClick = { fixedQuestionCount = count },
                                    label = {
                                        Text("${BengaliNumberFormatter.toBn(count)} টি প্রশ্ন", fontWeight = FontWeight.Bold)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    modifier = Modifier.testTag("count_chip_$count")
                                )
                            }
                        }
                    }
                }
            }

            // Difficulty Level Selector
            item {
                Column {
                    Text(
                        text = "৩. কাঠিন্যের স্তর ও উত্তরের ধরন",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MentalMathDifficulty.values().forEach { diff ->
                            val isSelected = selectedDifficulty == diff
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDifficulty = diff },
                                label = {
                                    Text(
                                        text = "${diff.titleBn} (+${BengaliNumberFormatter.toBn(diff.basePoints)} পয়েন্ট)",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = diff.badgeColor.copy(alpha = 0.2f),
                                    selectedLabelColor = diff.badgeColor
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("diff_${diff.id}")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuestionInputFormat.values().forEach { fmt ->
                            val isSelected = selectedInputFormat == fmt
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedInputFormat = fmt },
                                label = {
                                    Text(
                                        text = fmt.titleBn,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mm_format_${fmt.name}")
                            )
                        }
                    }
                }
            }

            // Mental Strategy Coach Card for Selected Category
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mm_strategy_coach_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = selectedCategory.containerColor.copy(alpha = 0.45f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "মানসিক গণনা কৌশল কোচ (${selectedCategory.titleBn})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = selectedCategory.primaryColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedCategory.strategyCoachBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Category Selector (10 Mental Math Categories)
            item {
                Text(
                    text = "৪. গণিত বিষয় নির্বাচন করুন (১০টি)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(MentalMathCategory.values()) { cat ->
                val isSelected = selectedCategory == cat
                ElevatedCard(
                    onClick = { selectedCategory = cat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cat_${cat.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isSelected) cat.containerColor.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = if (isSelected) 4.dp else 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(cat.primaryColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cat.getIcon(),
                                contentDescription = cat.titleBn,
                                tint = cat.primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.titleBn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cat.subtitleBn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "নির্বাচিত",
                                tint = cat.primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Big Start Challenge Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val durationOrCount = if (selectedMode.isTimed) {
                            sprintDurationSec
                        } else {
                            fixedQuestionCount
                        }
                        viewModel.startMentalMathChallenge(
                            category = selectedCategory,
                            difficulty = selectedDifficulty,
                            mode = selectedMode,
                            durationOrCount = durationOrCount,
                            inputFormat = selectedInputFormat
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_mental_math_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedCategory.primaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "চ্যালেঞ্জ শুরু করুন (${selectedCategory.titleBn})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // History Section
            if (mentalMathHistory.isNotEmpty()) {
                item {
                    MathSectionHeader(
                        title = "পূর্ববর্তী চ্যালেঞ্জ স্কোর",
                        subtitle = "অফলাইন ডাটাবেসে সংরক্ষিত ইতিহাস"
                    )
                }

                items(mentalMathHistory.take(5)) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.topicTitleBn,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "সঠিক: ${BengaliNumberFormatter.toBn(item.totalQuestions)} টি প্রশ্ন • ${BengaliNumberFormatter.toBn(item.score)} পয়েন্ট • নির্ভুলতা ${BengaliNumberFormatter.toBn(item.percentage)}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "${BengaliNumberFormatter.toBn(item.score)} pts",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
