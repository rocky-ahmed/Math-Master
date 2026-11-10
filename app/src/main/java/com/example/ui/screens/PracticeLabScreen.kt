package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.math.BengaliNumberFormatter
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.math.QuestionInputFormat
import com.example.ui.MathViewModel
import com.example.ui.components.MathSectionHeader
import com.example.ui.components.MathTopAppBar

@Composable
fun PracticeLabScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val quizScores by viewModel.quizScores.collectAsStateWithLifecycle()
    val topicAnalytics by viewModel.topicAnalytics.collectAsStateWithLifecycle()
    val practiceHistory = remember(quizScores) {
        quizScores.filter { it.topicId.startsWith("PRACTICE_") }
    }

    var selectedCategory by com.example.ui.rememberManagedUiState(viewModel, "practice_lab:selectedCategory", PracticeCategory.ADDITION_SUBTRACTION)
    var selectedDifficulty by com.example.ui.rememberManagedUiState(viewModel, "practice_lab:selectedDifficulty", PracticeDifficulty.EASY)
    var questionCount by com.example.ui.rememberManagedUiState(viewModel, "practice_lab:questionCount", 10)
    var isUnlimitedMode by com.example.ui.rememberManagedUiState(viewModel, "practice_lab:isUnlimitedMode", false)
    var selectedInputFormat by com.example.ui.rememberManagedUiState(viewModel, "practice_lab:selectedInputFormat", QuestionInputFormat.MCQ)
    var selectedTrack by com.example.ui.rememberManagedUiState(viewModel, "practice_lab:selectedTrack", com.example.math.CurriculumTrack.ALL)

    val filteredCategories = remember(selectedTrack) {
        PracticeCategory.values().filter {
            selectedTrack == com.example.math.CurriculumTrack.ALL || it.curriculumTracks.contains(selectedTrack)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_practice_lab")
    ) {
        MathTopAppBar(
            title = "গণিত প্র্যাকটিস ল্যাব",
            subtitle = "১২টি বিষয়ে ধাপে ধাপে অনুশীলন ও দক্ষতা যাচাই",
            onBackClick = { viewModel.navigateBack() }
        )

        val mainListState = com.example.ui.rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "practice_lab:main:${selectedTrack.name}"
        )

        LazyColumn(
            state = mainListState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("practice_lab_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Curriculum Track Filter Chips (NCTB, BdMO, BCS)
            item {
                val trackRowState = com.example.ui.rememberManagedLazyListState(viewModel, "practice_lab:track_row")
                LazyRow(
                    state = trackRowState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("practice_track_chip_row"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(com.example.math.CurriculumTrack.entries) { track ->
                        FilterChip(
                            selected = selectedTrack == track,
                            onClick = { selectedTrack = track },
                            label = {
                                Text(
                                    text = "${track.badgeBn} (${track.titleBn})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selectedTrack == track) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("practice_track_chip_${track.id}")
                        )
                    }
                }
            }

            // Hero Configuration Banner
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = selectedCategory.primaryColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = selectedCategory.getIcon(),
                                        contentDescription = null,
                                        tint = selectedCategory.primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "নির্বাচিত: ${selectedCategory.titleBn}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = selectedCategory.subtitleBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Difficulty Level Selector (Easy, Medium, Hard, Adaptive)
                        Text(
                            text = "কঠিনতার স্তর নির্বাচন করুন:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(PracticeDifficulty.values()) { diff ->
                                val isSelected = selectedDifficulty == diff
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedDifficulty = diff },
                                    label = {
                                        Text(
                                            text = diff.titleBn,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(diff.badgeColor, CircleShape)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = diff.badgeColor.copy(alpha = 0.2f),
                                        selectedLabelColor = diff.badgeColor
                                    ),
                                    modifier = Modifier.testTag("chip_diff_${diff.id}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Question Count & Unlimited Mode Selector
                        Text(
                            text = "প্রশ্নের সংখ্যা ও মোড:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listOf(5, 10, 15)) { count ->
                                val isSelected = !isUnlimitedMode && questionCount == count
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        isUnlimitedMode = false
                                        questionCount = count
                                    },
                                    label = {
                                        Text(
                                            text = "${BengaliNumberFormatter.toBn(count)} টি প্রশ্ন",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.testTag("chip_count_$count")
                                )
                            }
                            item {
                                FilterChip(
                                    selected = isUnlimitedMode,
                                    onClick = { isUnlimitedMode = true },
                                    label = {
                                        Text(
                                            text = "আনলিমিটেড",
                                            fontWeight = if (isUnlimitedMode) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.AllInclusive,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    modifier = Modifier.testTag("chip_unlimited_mode")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Question Format Selector (MCQ, Keypad, True/False)
                        Text(
                            text = "উত্তরের ধরন নির্বাচন করুন:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                                        .testTag("chip_format_${fmt.name}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Start Button
                        Button(
                            onClick = {
                                viewModel.startPracticeSession(
                                    category = selectedCategory,
                                    difficulty = selectedDifficulty,
                                    count = questionCount,
                                    isUnlimitedMode = isUnlimitedMode,
                                    inputFormat = selectedInputFormat
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_start_practice_session"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = selectedCategory.primaryColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অনুশীলন শুরু করুন",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Daily Interleaved Challenge Card (Rohrer & Taylor Interleaving Science)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_interleaved_challenge"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "দৈনিক ইন্টারলিভড চ্যালেঞ্জ (মিশ্র অনুশীলন)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "একাধিক গণিত অধ্যায়ের প্রশ্ন একত্রে অনুশীলন করলে কোন অঙ্কে কোন সূত্র প্রয়োগ করতে হবে তা দ্রুত চেনার দক্ষতা (Strategy Discrimination) বৃদ্ধি পায়।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.startInterleavedPracticeChallenge(
                                    track = selectedTrack,
                                    inputFormat = selectedInputFormat,
                                    count = questionCount
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_start_interleaved_challenge"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ইন্টারলিভড চ্যালেঞ্জ শুরু করুন (${selectedTrack.badgeBn})",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Categories Section (Filtered by Track)
            item {
                MathSectionHeader(
                    title = "অনুশীলনের বিষয়সমূহ (${BengaliNumberFormatter.toBn(filteredCategories.size)}টি)",
                    subtitle = "নিচ থেকে যেকোনো বিষয়ে ট্যাপ করে অনুশীলন করুন"
                )
            }

            items(filteredCategories) { category ->
                val isSelected = selectedCategory == category
                val topicStat = topicAnalytics.firstOrNull {
                    it.topicKey == "PRACTICE_LAB_${category.id}"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedCategory = category }
                        .border(
                            width = if (isSelected) 2.dp else 0.5.dp,
                            color = if (isSelected) category.primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .testTag("practice_category_card_${category.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) category.primaryColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = category.primaryColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = category.getIcon(),
                                        contentDescription = null,
                                        tint = category.primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = category.titleBn,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = category.titleEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = category.subtitleBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    maxLines = 1
                                )
                                if (topicStat != null && topicStat.totalAttempted > 0) {
                                    val acc = (topicStat.totalCorrect * 100) / topicStat.totalAttempted
                                    Text(
                                        text = "দক্ষতা: ${BengaliNumberFormatter.toBn(acc)}% (${BengaliNumberFormatter.toBn(topicStat.totalCorrect)}/${BengaliNumberFormatter.toBn(topicStat.totalAttempted)})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = category.primaryColor
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = if (isSelected) category.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Recent Practice Stats
            if (practiceHistory.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    MathSectionHeader(
                        title = "সর্বশেষ অনুশীলন অগ্রগতি",
                        subtitle = "অফলাইন মেমরিতে সংরক্ষিত পারফরম্যান্স"
                    )
                }

                item {
                    val historyRowState = com.example.ui.rememberManagedLazyListState(viewModel, "practice_lab:history_row")
                    LazyRow(
                        state = historyRowState,
                        contentPadding = PaddingValues(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(practiceHistory.take(5)) { score ->
                            Card(
                                modifier = Modifier.width(180.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = score.topicTitleBn,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${BengaliNumberFormatter.toBn(score.score)} / ${BengaliNumberFormatter.toBn(score.totalQuestions)}",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "সঠিকতা: ${BengaliNumberFormatter.toBn(score.percentage)}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
