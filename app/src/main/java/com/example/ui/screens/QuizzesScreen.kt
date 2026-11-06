package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExamAttemptEntity
import com.example.math.ExamConfig
import com.example.math.ExamDetailedResult
import com.example.math.ExamHistoryFilter
import com.example.math.ExamHistorySort
import com.example.math.ExamPresetType
import com.example.math.ExamScoringEngine
import com.example.math.PracticeDifficulty
import com.example.math.QuestionPaletteState
import com.example.math.QuizMode
import com.example.math.QuizQuestion
import com.example.math.QuizQuestionType
import com.example.math.QuizTopic
import com.example.math.SharedMathEngine
import com.example.math.toBengaliDigits
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.BanglaNumericKeypad
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizzesScreen(viewModel: MathViewModel, modifier: Modifier = Modifier) {
    val scores by viewModel.quizScores.collectAsStateWithLifecycle()
    val incorrectPool by viewModel.incorrectQuizQuestionPool.collectAsStateWithLifecycle()
    val completedExams by viewModel.completedExamAttempts.collectAsStateWithLifecycle()
    val activeExam by viewModel.activeExamAttempt.collectAsStateWithLifecycle()
    val historyFilter by viewModel.examHistoryFilter.collectAsStateWithLifecycle()
    val historySort by viewModel.examHistorySort.collectAsStateWithLifecycle()
    val comparisonIds by viewModel.comparisonAttemptIds.collectAsStateWithLifecycle()

    var activeCenterTab by remember { mutableIntStateOf(0) } // 0 = কুইজ ও পরীক্ষা, 1 = কাস্টম পরীক্ষা কেন্দ্র, 2 = পরীক্ষার ইতিহাস

    // Legacy quick-quiz state (preserved for existing testTags and quick topic launch)
    var selectedQuestionCount by remember { mutableIntStateOf(10) }
    var selectedMode by remember { mutableStateOf(QuizMode.TOPIC) }
    var selectedQuestionType by remember { mutableStateOf(QuizQuestionType.MCQ) }
    var selectedDifficulty by remember { mutableStateOf(PracticeDifficulty.EASY) }
    var selectedTimedSeconds by remember { mutableIntStateOf(120) }

    // Custom / Preset Exam Builder state
    var selectedPreset by remember { mutableStateOf(ExamPresetType.QUICK_TEST) }
    var selectedExamTopics by remember { mutableStateOf(setOf(QuizTopic.MIXED_CHALLENGE)) }
    var examQuestionCount by remember { mutableIntStateOf(10) }
    var examDurationMinutes by remember { mutableIntStateOf(5) }
    var examDifficulty by remember { mutableStateOf(PracticeDifficulty.MEDIUM) }
    var examQuestionType by remember { mutableStateOf(QuizQuestionType.MCQ) }
    var examNegativeMarking by remember { mutableStateOf(0.0) }
    var examImmediateFeedback by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ইন্টারেক্টিভ কুইজ ও পরীক্ষা কেন্দ্র", fontWeight = FontWeight.Bold)
                        Text(
                            "অফলাইন পরীক্ষা, নেগেটিভ মার্কিং, প্রশ্ন প্যালেট ও বিশ্লেষণ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.BcsPreparation) },
                        modifier = Modifier.testTag("btn_open_bcs_from_exam")
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("বিসিএস প্রস্তুতি", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Active Unfinished Exam Recovery Banner
            AnimatedVisibility(visible = activeExam != null) {
                activeExam?.let { unfinished ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("active_exam_recovery_banner"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "অসমাপ্ত পরীক্ষা সংরক্ষিত আছে",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = "${unfinished.examTitleBn} • প্রশ্ন ${(unfinished.currentQuestionIndex + 1).toBengaliDigits()}/${unfinished.totalQuestions.toBengaliDigits()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { viewModel.discardActiveExamAttempt() },
                                    modifier = Modifier.testTag("btn_discard_active_exam")
                                ) {
                                    Text("বাতিল করুন")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.resumeActiveExamAttempt(unfinished) },
                                    modifier = Modifier.testTag("btn_resume_active_exam")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("পুনরায় শুরু করুন", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            TabRow(selectedTabIndex = activeCenterTab) {
                Tab(
                    selected = activeCenterTab == 0,
                    onClick = { activeCenterTab = 0 },
                    modifier = Modifier.testTag("exam_tab_quick_quiz"),
                    text = { Text("টপিক কুইজ", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeCenterTab == 1,
                    onClick = { activeCenterTab = 1 },
                    modifier = Modifier.testTag("exam_tab_custom_builder"),
                    text = { Text("মক ও কাস্টম পরীক্ষা", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeCenterTab == 2,
                    onClick = { activeCenterTab = 2 },
                    modifier = Modifier.testTag("exam_tab_history"),
                    text = { Text("ইতিহাস (${completedExams.size.toBengaliDigits()})", fontWeight = FontWeight.Bold) }
                )
            }

            when (activeCenterTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "কুইজ মোড নির্বাচন করুন",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        QuizMode.values().forEach { mode ->
                                            FilterChip(
                                                selected = selectedMode == mode,
                                                onClick = { selectedMode = mode },
                                                label = { Text(mode.titleBn, fontWeight = FontWeight.SemiBold) },
                                                modifier = Modifier.testTag("quiz_mode_${mode.id}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = selectedMode.subtitleBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "প্রশ্নের ধরন:",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        QuizQuestionType.values().forEach { qType ->
                                            FilterChip(
                                                selected = selectedQuestionType == qType,
                                                onClick = { selectedQuestionType = qType },
                                                label = { Text(qType.titleBn) },
                                                modifier = Modifier.testTag("quiz_qtype_${qType.id}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "কঠিনতার স্তর:",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(
                                            PracticeDifficulty.EASY,
                                            PracticeDifficulty.MEDIUM,
                                            PracticeDifficulty.HARD
                                        ).forEach { diff ->
                                            FilterChip(
                                                selected = selectedDifficulty == diff,
                                                onClick = { selectedDifficulty = diff },
                                                label = { Text(diff.titleBn, fontWeight = FontWeight.Bold) },
                                                modifier = Modifier.testTag("quiz_diff_${diff.id}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "প্রশ্ন সংখ্যা:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        listOf(5, 10, 15, 20).forEach { count ->
                                            FilterChip(
                                                selected = selectedQuestionCount == count,
                                                onClick = { selectedQuestionCount = count },
                                                label = {
                                                    Text(
                                                        "${count.toBengaliDigits()}টি",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                },
                                                modifier = Modifier.testTag("quiz_count_$count")
                                            )
                                        }
                                    }

                                    if (selectedMode == QuizMode.TIMED) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "সময়সীমা:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            listOf(60 to "১ মি.", 120 to "২ মি.", 300 to "৫ মি.").forEach { (sec, label) ->
                                                FilterChip(
                                                    selected = selectedTimedSeconds == sec,
                                                    onClick = { selectedTimedSeconds = sec },
                                                    label = { Text(label, fontWeight = FontWeight.Bold) },
                                                    modifier = Modifier.testTag("quiz_timer_$sec")
                                                )
                                            }
                                        }
                                    }

                                    if (incorrectPool.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { viewModel.startReviewIncorrectQuiz() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("btn_review_incorrect_pool"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text(
                                                "পূর্বে ভুল করা ${incorrectPool.size.toBengaliDigits()}টি প্রশ্নের রিভিউ কুইজ দিন",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        items(QuizTopic.values().toList(), key = { it.id }) { topic ->
                            val bestScore = scores.filter { it.topic == topic.id }.maxByOrNull { it.percentage }
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.startQuiz(
                                            topic = topic,
                                            count = selectedQuestionCount,
                                            mode = selectedMode,
                                            difficulty = selectedDifficulty,
                                            questionType = selectedQuestionType,
                                            timedSeconds = selectedTimedSeconds
                                        )
                                    }
                                    .testTag("quiz_topic_${topic.id}"),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = topic.titleBn,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        if (bestScore != null) {
                                            Text(
                                                text = "সর্বোচ্চ স্কোর: ${bestScore.percentage.toBengaliDigits()}% (${bestScore.score.toBengaliDigits()}/${bestScore.totalQuestions.toBengaliDigits()})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        } else {
                                            Text(
                                                text = "এখনো অংশগ্রহণ করা হয়নি • ${selectedQuestionCount.toBengaliDigits()}টি প্রশ্ন",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = {
                                            viewModel.startQuiz(
                                                topic = topic,
                                                count = selectedQuestionCount,
                                                mode = selectedMode,
                                                difficulty = selectedDifficulty,
                                                questionType = selectedQuestionType,
                                                timedSeconds = selectedTimedSeconds
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("start_quiz_${topic.id}")
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("শুরু")
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Tab 1: Professional Exam Center & Custom Builder
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "১. পরীক্ষার ধরন (Preset) নির্বাচন করুন",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(ExamPresetType.values().toList(), key = { it.id }) { preset ->
                            val isSelected = selectedPreset == preset
                            OutlinedCard(
                                onClick = {
                                    selectedPreset = preset
                                    examQuestionCount = preset.defaultQuestions
                                    examDurationMinutes = preset.defaultDurationMinutes
                                    examNegativeMarking = preset.defaultNegativePerWrong
                                    examImmediateFeedback = preset.defaultImmediateFeedback
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("exam_preset_${preset.id}"),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = preset.titleBn,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = preset.subtitleBn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "২. পরীক্ষার কনফিগারেশন ও নেগেটিভ মার্কিং",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text("একাধিক অধ্যায় নির্বাচন (Multi-Topic Selection):", fontWeight = FontWeight.SemiBold)
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        QuizTopic.values().forEach { t ->
                                            val chosen = selectedExamTopics.contains(t)
                                            FilterChip(
                                                selected = chosen,
                                                onClick = {
                                                    selectedExamTopics = if (chosen && selectedExamTopics.size > 1) {
                                                        selectedExamTopics - t
                                                    } else {
                                                        selectedExamTopics + t
                                                    }
                                                },
                                                label = { Text(t.titleBn.substringBefore(" (")) },
                                                modifier = Modifier.testTag("exam_multi_topic_${t.id}")
                                            )
                                        }
                                    }

                                    Text("প্রশ্ন সংখ্যা:", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(10, 15, 20, 25, 30, 40).forEach { qCount ->
                                            FilterChip(
                                                selected = examQuestionCount == qCount,
                                                onClick = { examQuestionCount = qCount },
                                                label = { Text("${qCount.toBengaliDigits()}টি") },
                                                modifier = Modifier.testTag("exam_qcount_$qCount")
                                            )
                                        }
                                    }

                                    Text("সময়সীমা (মিনিট):", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(0 to "সময়সীমাহীন", 5 to "৫ মিনিট", 10 to "১০ মিনিট", 15 to "১৫ মিনিট", 25 to "২৫ মিনিট", 30 to "৩০ মিনিট").forEach { (min, label) ->
                                            FilterChip(
                                                selected = examDurationMinutes == min,
                                                onClick = { examDurationMinutes = min },
                                                label = { Text(label) },
                                                modifier = Modifier.testTag("exam_duration_$min")
                                            )
                                        }
                                    }

                                    Text("নেগেটিভ মার্কিং (কাস্টম অনুশীলন ও ঐতিহাসিক রেফারেন্স):", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            0.0 to "বন্ধ (০.০০ • ডিফল্ট)",
                                            0.25 to "-০.২৫ (কাস্টম অনুশীলন)",
                                            0.50 to "-০.৫০ (ঐতিহাসিক বিসিএস / কাস্টম)"
                                        ).forEach { (neg, label) ->
                                            FilterChip(
                                                selected = examNegativeMarking == neg,
                                                onClick = { examNegativeMarking = neg },
                                                label = { Text(label, fontWeight = FontWeight.Bold) },
                                                modifier = Modifier.testTag("exam_neg_$neg")
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("তাৎক্ষণিক ব্যাখ্যা (Practice Feedback)", fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "চালু থাকলে প্রতিটি প্রশ্নের পর উত্তর ও ব্যাখ্যা দেখাবে; বন্ধ থাকলে পূর্ণাঙ্গ পরীক্ষার শেষে ফলাফল দেখাবে",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Switch(
                                            checked = examImmediateFeedback,
                                            onCheckedChange = { examImmediateFeedback = it },
                                            modifier = Modifier.testTag("switch_immediate_feedback")
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.startConfiguredExam(
                                                ExamConfig(
                                                    preset = selectedPreset,
                                                    selectedTopics = selectedExamTopics,
                                                    difficulty = examDifficulty,
                                                    questionType = examQuestionType,
                                                    questionCount = examQuestionCount,
                                                    durationMinutes = examDurationMinutes,
                                                    marksPerCorrect = 1.0,
                                                    negativeMarksPerWrong = examNegativeMarking,
                                                    immediateFeedback = examImmediateFeedback
                                                )
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("btn_launch_configured_exam"),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "পরীক্ষা শুরু করুন (${examQuestionCount.toBengaliDigits()}টি প্রশ্ন)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Tab 2: Filterable, Sortable Exam History & Comparison
                    val filteredAttempts = remember(completedExams, historyFilter, historySort) {
                        val filtered = completedExams.filter { exam ->
                            when (historyFilter) {
                                ExamHistoryFilter.ALL -> true
                                ExamHistoryFilter.MOCK_ONLY -> exam.examType.contains("MOCK")
                                ExamHistoryFilter.BCS_ONLY -> exam.examType == ExamPresetType.BCS_PRELIMINARY_MOCK.id
                                ExamHistoryFilter.TOPIC_ONLY -> exam.examType == ExamPresetType.TOPIC_TEST.id || exam.examType == ExamPresetType.QUICK_TEST.id
                                ExamHistoryFilter.HIGH_SCORE -> exam.percentage >= 80.0
                            }
                        }
                        when (historySort) {
                            ExamHistorySort.NEWEST_FIRST -> filtered.sortedByDescending { it.completedAt ?: it.updatedAt }
                            ExamHistorySort.HIGHEST_SCORE -> filtered.sortedByDescending { it.percentage }
                            ExamHistorySort.HIGHEST_ACCURACY -> filtered.sortedByDescending { it.accuracyPercent }
                        }
                    }

                    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExamHistoryFilter.values().forEach { filter ->
                                    FilterChip(
                                        selected = historyFilter == filter,
                                        onClick = { viewModel.setExamHistoryFilter(filter) },
                                        label = { Text(filter.titleBn) },
                                        modifier = Modifier.testTag("exam_hist_filter_${filter.name}")
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExamHistorySort.values().forEach { sort ->
                                    FilterChip(
                                        selected = historySort == sort,
                                        onClick = { viewModel.setExamHistorySort(sort) },
                                        label = { Text(sort.titleBn) },
                                        modifier = Modifier.testTag("exam_hist_sort_${sort.name}")
                                    )
                                }
                            }
                        }

                        // Comparison Card when 2 attempts are selected
                        if (comparisonIds.size == 2) {
                            val pair = completedExams.filter { comparisonIds.contains(it.attemptId) }
                            if (pair.size == 2) {
                                item {
                                    ExamAttemptComparisonCard(first = pair[1], second = pair[0])
                                }
                            }
                        }

                        if (filteredAttempts.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.History,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "এখনো কোনো সম্পন্ন পরীক্ষার ইতিহাস নেই",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "যেকোনো টপিক কুইজ বা মক পরীক্ষায় অংশ নিলে বিস্তারিত ফলাফল এখানে সংরক্ষিত থাকবে।",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredAttempts, key = { it.attemptId }) { attempt ->
                                val isCompared = comparisonIds.contains(attempt.attemptId)
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openCompletedExamResult(attempt) }
                                        .testTag("exam_history_item_${attempt.attemptId}"),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = attempt.examTitleBn,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = dateFormat.format(Date(attempt.completedAt ?: attempt.startedAt)),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "${attempt.percentage.roundToInt().toBengaliDigits()}%",
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "সঠিক: ${attempt.correctCount.toBengaliDigits()} • ভুল: ${attempt.wrongCount.toBengaliDigits()} • উত্তরহীন: ${attempt.unansweredCount.toBengaliDigits()} • চূড়ান্ত নম্বর: ${String.format("%.2f", attempt.finalScore)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            FilterChip(
                                                selected = isCompared,
                                                onClick = { viewModel.toggleExamComparisonSelection(attempt.attemptId) },
                                                label = { Text(if (isCompared) "তুলনায় যুক্ত" else "তুলনা করুন") }
                                            )
                                            Row {
                                                TextButton(onClick = { viewModel.openCompletedExamResult(attempt) }) {
                                                    Text("বিশ্লেষণ দেখুন", fontWeight = FontWeight.Bold)
                                                }
                                                IconButton(
                                                    onClick = { viewModel.deleteExamAttemptFromHistory(attempt.attemptId) },
                                                    modifier = Modifier.testTag("delete_exam_${attempt.attemptId}")
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "মুছে ফেলুন",
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
    }
}

@Composable
private fun ExamAttemptComparisonCard(first: ExamAttemptEntity, second: ExamAttemptEntity) {
    val scoreDiff = (second.percentage - first.percentage).roundToInt()
    val accDiff = (second.accuracyPercent - first.accuracyPercent).roundToInt()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_comparison_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "দুইটি পরীক্ষার তুলনামূলক বিশ্লেষণ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "পূর্বের পরীক্ষা: ${first.percentage.roundToInt().toBengaliDigits()}%  →  পরবর্তী পরীক্ষা: ${second.percentage.roundToInt().toBengaliDigits()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "স্কোর পরিবর্তন: ${if (scoreDiff >= 0) "+${scoreDiff.toBengaliDigits()}" else scoreDiff.toBengaliDigits()}% • নির্ভুলতা পরিবর্তন: ${if (accDiff >= 0) "+${accDiff.toBengaliDigits()}" else accDiff.toBengaliDigits()}%",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizPlayScreen(viewModel: MathViewModel, modifier: Modifier = Modifier) {
    val session by viewModel.quizSession.collectAsStateWithLifecycle()
    var showPaletteSheet by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateBack()
    }

    // Active timer loop
    LaunchedEffect(session.isFinished, session.mode, session.configuredDurationSeconds) {
        if (!session.isFinished) {
            while (true) {
                delay(1000L)
                viewModel.tickQuizTimer()
            }
        }
    }

    // Pre-Submit Confirmation Modal
    if (session.showSubmitConfirmDialog && !session.isFinished) {
        val totalQ = session.questions.size
        val answeredCount = session.userAnswers.count { it.value >= 0 }
        val unansweredCount = (totalQ - answeredCount).coerceAtLeast(0)
        val markedCount = session.markedForReviewIndices.size

        AlertDialog(
            onDismissRequest = { viewModel.dismissExamSubmitConfirmation() },
            title = { Text("পরীক্ষা চূড়ান্তভাবে জমা দেবেন?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("মোট প্রশ্ন: ${totalQ.toBengaliDigits()}টি")
                    Text("উত্তর দেওয়া হয়েছে: ${answeredCount.toBengaliDigits()}টি", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                    Text("উত্তর দেওয়া হয়নি: ${unansweredCount.toBengaliDigits()}টি", color = if (unansweredCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    Text("রিভিউর জন্য চিহ্নিত: ${markedCount.toBengaliDigits()}টি")
                    if (session.examConfig.negativeMarksPerWrong > 0.0) {
                        Text(
                            "সতর্কতা: প্রতিটি ভুল উত্তরের জন্য ${String.format("%.2f", session.examConfig.negativeMarksPerWrong)} নম্বর কর্তন হবে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitFullExamNow() },
                    modifier = Modifier.testTag("confirm_submit_exam_btn")
                ) {
                    Text("হ্যাঁ, জমা দিন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissExamSubmitConfirmation() }) {
                    Text("পরীক্ষায় ফিরে যান")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = session.examTitleBn.ifBlank { session.topic.titleBn },
                            maxLines = 1,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${session.mode.titleBn} • ${session.difficulty.titleBn}" +
                                if (session.examConfig.negativeMarksPerWrong > 0.0) " • নেগেটিভ -${session.examConfig.negativeMarksPerWrong}" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    if (!session.isFinished && (session.mode == QuizMode.TIMED || session.configuredDurationSeconds > 0)) {
                        val min = session.timeRemainingSeconds / 60
                        val sec = session.timeRemainingSeconds % 60
                        val isLow = session.timeRemainingSeconds <= 30
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLow) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("quiz_timer_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${min.toBengaliDigits()}:${sec.toString().padStart(2, '0').map { if (it.isDigit()) '০' + (it - '0') else it }.joinToString("")}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    if (!session.isFinished && session.questions.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.requestExamSubmitConfirmation() },
                            modifier = Modifier.testTag("top_submit_exam_btn")
                        ) {
                            Text("জমা দিন", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (session.isFinished) {
            QuizCompletedContent(
                session = session,
                onRetake = {
                    if (session.isFullExamMode) {
                        viewModel.startConfiguredExam(session.examConfig)
                    } else {
                        viewModel.startQuiz(
                            topic = session.topic,
                            count = session.questions.size.coerceAtLeast(5),
                            mode = session.mode,
                            difficulty = session.difficulty,
                            questionType = session.questionType,
                            timedSeconds = session.configuredDurationSeconds
                        )
                    }
                },
                onReviewWrong = { viewModel.startReviewIncorrectQuiz() },
                onPracticeWeakTopics = { weakTitles -> viewModel.practiceWeakExamTopics(weakTitles) },
                onToggleReview = { viewModel.toggleQuizReviewMode() },
                onNextTopic = { viewModel.startNextQuizTopic() },
                onExitToQuizzes = { viewModel.exitQuizToTopicList() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            val currentQ = session.questions.getOrNull(session.currentIndex)
            if (currentQ == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("প্রশ্ন লোড হচ্ছে...")
                }
                return@Scaffold
            }

            val total = session.questions.size
            val progress = (session.currentIndex + 1).toFloat() / total.coerceAtLeast(1)
            val isMarkedForReview = session.markedForReviewIndices.contains(session.currentIndex)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Progress & Palette Toggle Bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "প্রশ্ন ${(session.currentIndex + 1).toBengaliDigits()} / ${total.toBengaliDigits()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = { showPaletteSheet = !showPaletteSheet },
                                modifier = Modifier.testTag("btn_toggle_question_palette")
                            ) {
                                Text(
                                    text = if (showPaletteSheet) "প্যালেট লুকান" else "প্রশ্ন প্যালেট (${session.userAnswers.size.toBengaliDigits()}/${total.toBengaliDigits()})",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                // Expandable 5-State Question Palette
                if (showPaletteSheet) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("question_palette_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "প্রশ্ন নেভিগেশন প্যালেট (যে কোনো প্রশ্নে যেতে ট্যাপ করুন)",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    session.questions.indices.forEach { qIdx ->
                                        val pState = ExamScoringEngine.computePaletteState(
                                            questionIndex = qIdx,
                                            visitedIndices = session.visitedIndices,
                                            selectedAnswers = session.userAnswers,
                                            markedForReview = session.markedForReviewIndices
                                        )
                                        val bgColor = when (pState) {
                                            QuestionPaletteState.ANSWERED -> Color(0xFF2E7D32)
                                            QuestionPaletteState.ANSWERED_AND_MARKED -> Color(0xFF5E35B1)
                                            QuestionPaletteState.MARKED_FOR_REVIEW -> Color(0xFF8E24AA)
                                            QuestionPaletteState.UNANSWERED -> Color(0xFFEF6C00)
                                            QuestionPaletteState.NOT_VISITED -> MaterialTheme.colorScheme.surface
                                        }
                                        val txtColor = if (pState == QuestionPaletteState.NOT_VISITED) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else Color.White

                                        Surface(
                                            onClick = { viewModel.jumpToQuizQuestion(qIdx) },
                                            shape = CircleShape,
                                            color = bgColor,
                                            border = if (qIdx == session.currentIndex) {
                                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                            } else null,
                                            modifier = Modifier
                                                .size(38.dp)
                                                .testTag("palette_q_$qIdx")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = (qIdx + 1).toBengaliDigits(),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = txtColor
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "সবুজ: উত্তরকৃত • কমলা: উত্তরহীন • বেগুনি: রিভিউ চিহ্নিত",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Question Card + Mark for Review + Clear Answer controls
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = currentQ.topicTitleBn,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(
                                        onClick = { viewModel.toggleMarkCurrentQuestionForReview() },
                                        modifier = Modifier.testTag("btn_mark_for_review")
                                    ) {
                                        Icon(
                                            imageVector = if (isMarkedForReview) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = null,
                                            tint = if (isMarkedForReview) Color(0xFF8E24AA) else MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isMarkedForReview) "রিভিউ চিহ্নিত" else "রিভিউ মার্ক",
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isMarkedForReview) Color(0xFF8E24AA) else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    if (session.selectedOptionIndex != null || session.userAnswers.containsKey(session.currentIndex)) {
                                        if (session.isFullExamMode || !session.isAnswerSubmitted) {
                                            TextButton(
                                                onClick = { viewModel.clearCurrentQuizAnswer() },
                                                modifier = Modifier.testTag("btn_clear_answer")
                                            ) {
                                                Text("উত্তর মুছুন", color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentQ.questionBn,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Options or Numerical Keypad
                item {
                    if (currentQ.questionType == QuizQuestionType.NUMERICAL) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = session.typedAnswer,
                                onValueChange = { viewModel.setQuizTypedAnswer(it) },
                                enabled = !session.isAnswerSubmitted,
                                label = { Text("আপনার উত্তর লিখুন") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quiz_numerical_input"),
                                shape = RoundedCornerShape(14.dp)
                            )
                            if (!session.isAnswerSubmitted) {
                                BanglaNumericKeypad(
                                    currentValue = session.typedAnswer,
                                    onValueChange = { viewModel.setQuizTypedAnswer(it) },
                                    onClear = { viewModel.setQuizTypedAnswer("") }
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            currentQ.optionsBn.forEachIndexed { index, optionText ->
                                val isSelected = session.selectedOptionIndex == index
                                val isCorrectOption = index == currentQ.correctIndex
                                val showImmediate = !session.isFullExamMode && session.isAnswerSubmitted

                                val borderColor = when {
                                    showImmediate && isCorrectOption -> Color(0xFF2E7D32)
                                    showImmediate && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }

                                val containerColor = when {
                                    showImmediate && isCorrectOption -> Color(0xFF2E7D32).copy(alpha = 0.14f)
                                    showImmediate && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    else -> MaterialTheme.colorScheme.surface
                                }

                                OutlinedCard(
                                    onClick = { viewModel.selectQuizOption(index) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("quiz_option_$index"),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(if (isSelected || (showImmediate && isCorrectOption)) 2.dp else 1.dp, borderColor),
                                    colors = CardDefaults.outlinedCardColors(containerColor = containerColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (showImmediate) {
                                            if (isCorrectOption) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "সঠিক",
                                                    tint = Color(0xFF2E7D32)
                                                )
                                            } else if (isSelected) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "ভুল",
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

                // Immediate Feedback Card (when not in full exam mode and answer is submitted)
                if (!session.isFullExamMode && session.isAnswerSubmitted) {
                    item {
                        val wasCorrect = session.userAnswers[session.currentIndex] == currentQ.correctIndex
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (wasCorrect) "সঠিক উত্তর!" else "ভুল উত্তর! সঠিক উত্তর: ${currentQ.correctAnswerText}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (wasCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ব্যাখ্যা: ${currentQ.explanationBn}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Bottom Navigation Controls
                item {
                    val canSubmit = session.selectedOptionIndex != null ||
                        (currentQ.questionType == QuizQuestionType.NUMERICAL && session.typedAnswer.isNotBlank())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (session.currentIndex > 0) {
                            OutlinedButton(
                                onClick = { viewModel.previousQuizQuestion() },
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("btn_prev_question"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("পূর্ববর্তী", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (session.isFullExamMode) {
                            Button(
                                onClick = {
                                    if (session.currentIndex + 1 < total) {
                                        viewModel.nextQuizQuestion()
                                    } else {
                                        viewModel.requestExamSubmitConfirmation()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("next_question_btn"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = if (session.currentIndex + 1 < total) "পরবর্তী প্রশ্ন" else "পরীক্ষা জমা দিন",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (!session.isAnswerSubmitted) {
                                        viewModel.submitQuizAnswer()
                                    } else {
                                        viewModel.nextQuizQuestion()
                                    }
                                },
                                enabled = if (!session.isAnswerSubmitted) canSubmit else true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag(if (!session.isAnswerSubmitted) "submit_answer_btn" else "next_question_btn"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = when {
                                        !session.isAnswerSubmitted -> "উত্তর যাচাই করুন"
                                        session.currentIndex + 1 < total -> "পরবর্তী প্রশ্ন"
                                        else -> "ফলাফল দেখুন"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (session.isAnswerSubmitted) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
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
private fun QuizCompletedContent(
    session: com.example.ui.QuizSessionState,
    onRetake: () -> Unit,
    onReviewWrong: () -> Unit,
    onPracticeWeakTopics: (List<String>) -> Unit,
    onToggleReview: () -> Unit,
    onNextTopic: () -> Unit,
    onExitToQuizzes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = session.questions.size
    val detailed: ExamDetailedResult? = session.detailedResult
    val percentage = detailed?.percentage?.roundToInt()
        ?: if (total > 0) (session.correctCount * 100) / total else 0
    val wrongCount = detailed?.wrongCount ?: session.incorrectQuestions.size
    val unansweredCount = detailed?.unansweredCount ?: 0

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_result_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detailed?.performanceGradeBn ?: "পরীক্ষা সম্পন্ন!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${percentage.toBengaliDigits()}%",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "সঠিক: ${session.correctCount.toBengaliDigits()} • ভুল: ${wrongCount.toBengaliDigits()} • উত্তরহীন: ${unansweredCount.toBengaliDigits()} (মোট ${total.toBengaliDigits()}টি)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    if (detailed != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("অর্জিত নম্বর", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    String.format("%.2f / %.0f", detailed.finalScore, detailed.totalPossibleMarks),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("নেগেটিভ কর্তন", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "-${String.format("%.2f", detailed.negativeMarksDeducted)}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (detailed.negativeMarksDeducted > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("নির্ভুলতা", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${detailed.accuracyPercent.roundToInt().toBengaliDigits()}%",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("প্রশ্নপ্রতি সময়", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${detailed.avgTimePerQuestionSeconds.roundToInt().toBengaliDigits()} সে.",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = detailed.recommendationSummaryBn,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Result Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onToggleReview,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_review_answers")
                        ) {
                            Text(
                                if (session.isReviewMode) "উত্তর লুকান" else "সকল উত্তর দেখুন",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = onRetake,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("retake_quiz_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("আবার দিন", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (wrongCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onReviewWrong,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_retry_wrong_quiz"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "ভুল হওয়া ${wrongCount.toBengaliDigits()}টি প্রশ্ন পুনরায় অনুশীলন করুন",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (detailed != null && detailed.weakTopicTitlesBn.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onPracticeWeakTopics(detailed.weakTopicTitlesBn) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_practice_weak_topics")
                        ) {
                            Text("দুর্বল অধ্যায়গুলো অনুশীলন করুন", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onExitToQuizzes,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_exit_to_quiz_list")
                        ) {
                            Text("পরীক্ষা তালিকায় ফিরুন", fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = onNextTopic,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_next_quiz_topic"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("পরবর্তী টপিক", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Topic-wise Breakdown Card
        if (detailed != null && detailed.topicBreakdown.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exam_topic_breakdown_card"),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "অধ্যায়ভিত্তিক ফলাফল বিশ্লেষণ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        detailed.topicBreakdown.forEach { tb ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = tb.topicTitleBn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${tb.correctCount.toBengaliDigits()}/${tb.totalQuestions.toBengaliDigits()} (${tb.accuracyPercent.roundToInt().toBengaliDigits()}%)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tb.accuracyPercent >= 75.0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { (tb.accuracyPercent.toFloat() / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }
        }

        if (session.isReviewMode) {
            item {
                Text(
                    text = "প্রশ্নওয়ারী উত্তর ও ব্যাখ্যা:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            itemsIndexed(session.questions) { index, q ->
                val userChoice = session.userAnswers[index]
                val isCorrect = userChoice == q.correctIndex
                QuizAnswerReviewCard(
                    index = index,
                    question = q,
                    userChoice = userChoice,
                    isCorrect = isCorrect
                )
            }
        }
    }
}

@Composable
private fun QuizAnswerReviewCard(
    index: Int,
    question: QuizQuestion,
    userChoice: Int?,
    isCorrect: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) {
                Color(0xFF2E7D32).copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "প্রশ্ন ${(index + 1).toBengaliDigits()}: ${question.questionBn}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            val userAnsText = if (userChoice != null && userChoice in question.optionsBn.indices) {
                question.optionsBn[userChoice]
            } else {
                "উত্তর দেওয়া হয়নি"
            }
            val correctAnsText = question.optionsBn.getOrElse(question.correctIndex) { question.correctAnswerText }
            Text(
                text = "আপনার উত্তর: $userAnsText",
                style = MaterialTheme.typography.bodySmall,
                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
            if (!isCorrect) {
                Text(
                    text = "সঠিক উত্তর: $correctAnsText",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ব্যাখ্যা: ${question.explanationBn}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
