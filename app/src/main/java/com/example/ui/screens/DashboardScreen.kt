package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.math.AnalyticsTimeFilter
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.LearningAnalyticsEngine
import com.example.math.TopicMasteryReport
import com.example.math.TopicMasteryStatus
import com.example.math.toBengaliDigits
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedScrollState
import com.example.ui.rememberManagedUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MathViewModel, modifier: Modifier = Modifier) {
    val progressList by viewModel.progressList.collectAsStateWithLifecycle()
    val quizScores by viewModel.quizScores.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val practiceHistory by viewModel.practiceHistory.collectAsStateWithLifecycle()
    val topicAnalytics by viewModel.topicAnalytics.collectAsStateWithLifecycle()
    val completedExams by viewModel.completedExamAttempts.collectAsStateWithLifecycle()
    val activeExam by viewModel.activeExamAttempt.collectAsStateWithLifecycle()
    val dailyActivities by viewModel.dailyStudyActivities.collectAsStateWithLifecycle()
    val selectedTimeFilter by viewModel.dashboardTimeFilter.collectAsStateWithLifecycle()
    val bookLessonProgress by viewModel.bookLessonProgress.collectAsStateWithLifecycle()
    val bcsTopicProgress by viewModel.bcsTopicProgress.collectAsStateWithLifecycle()
    val unifiedMistakes by viewModel.unifiedMistakes.collectAsStateWithLifecycle()
    val formulaMastery by viewModel.formulaMastery.collectAsStateWithLifecycle()

    var selectedModuleFilter by rememberManagedUiState(viewModel, "dashboard:selectedModuleFilter", "ALL")
    var showGoalConfigDialog by rememberManagedUiState(viewModel, "dashboard:showGoalConfigDialog", false)
    val mainListState = rememberManagedLazyListState(viewModel, "dashboard:main")
    val timeFilterScrollState = rememberManagedScrollState(viewModel, "dashboard:time_filter_row")
    val moduleFilterScrollState = rememberManagedScrollState(viewModel, "dashboard:module_filter_row")

    val analyticsData = remember(
        selectedTimeFilter,
        completedExams,
        activeExam,
        quizScores,
        practiceHistory,
        topicAnalytics,
        progressList,
        dailyActivities
    ) {
        LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = selectedTimeFilter,
            completedExams = completedExams,
            activeExam = activeExam,
            quizScores = quizScores,
            practiceHistory = practiceHistory,
            topicAnalytics = topicAnalytics,
            progressItems = progressList,
            dailyActivities = dailyActivities
        )
    }

    val filteredHistory = remember(practiceHistory, selectedModuleFilter) {
        if (selectedModuleFilter == "ALL") practiceHistory
        else practiceHistory.filter { it.module == selectedModuleFilter }
    }

    val dateFormatter = remember {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    }

    if (showGoalConfigDialog) {
        var qTarget by remember { mutableIntStateOf(analyticsData.goalProgress.dailyTargetQuestions) }
        var mTarget by remember { mutableIntStateOf(analyticsData.goalProgress.dailyTargetMinutes) }

        AlertDialog(
            onDismissRequest = { showGoalConfigDialog = false },
            title = { Text("দৈনিক অধ্যয়ন লক্ষ্যমাত্রা নির্ধারণ করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("দৈনিক প্রশ্নের লক্ষ্যমাত্রা:", fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 20, 30, 50, 75).forEach { q ->
                            FilterChip(
                                selected = qTarget == q,
                                onClick = { qTarget = q },
                                label = { Text("${q.toBengaliDigits()}টি") },
                                modifier = Modifier.testTag("goal_q_$q")
                            )
                        }
                    }

                    Text("দৈনিক অনুশীলনের সময় (মিনিট):", fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 15, 25, 30, 45).forEach { m ->
                            FilterChip(
                                selected = mTarget == m,
                                onClick = { mTarget = m },
                                label = { Text("${m.toBengaliDigits()} মি.") },
                                modifier = Modifier.testTag("goal_m_$m")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDailyStudyTargets(qTarget, mTarget)
                        showGoalConfigDialog = false
                    },
                    modifier = Modifier.testTag("btn_save_daily_goals")
                ) {
                    Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalConfigDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("অগ্রগতি ও লার্নিং অ্যানালিটিক্স", fontWeight = FontWeight.Bold)
                        Text(
                            "পরীক্ষা, অনুশীলন, টপিক দক্ষতা ও সুপারিশ কেন্দ্র",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            state = mainListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("dashboard_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Time-Based Analytics Filter Bar
            item {
                Column {
                    Text(
                        text = "সময়কাল ফিল্টার:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(timeFilterScrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnalyticsTimeFilter.values().forEach { tf ->
                            FilterChip(
                                selected = selectedTimeFilter == tf,
                                onClick = { viewModel.setDashboardTimeFilter(tf) },
                                label = { Text(tf.titleBn, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("dashboard_time_filter_${tf.id}")
                            )
                        }
                    }
                }
            }

            // 2. Daily Study Goal, Streak & Consistency Card
            item {
                val goal = analyticsData.goalProgress
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_daily_goal_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "দৈনিক লক্ষ্যমাত্রা ও ধারাবাহিকতা",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "বর্তমান স্ট্রিক: ${goal.currentStreakDays.toBengaliDigits()} দিন • সর্বোচ্চ: ${goal.bestStreakDays.toBengaliDigits()} দিন • সাপ্তাহিক ধারাবাহিকতা: ${goal.weeklyConsistencyPercent.toBengaliDigits()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showGoalConfigDialog = true },
                                modifier = Modifier.testTag("btn_configure_daily_goals")
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "লক্ষ্যমাত্রা পরিবর্তন",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Question Goal Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "আজকের প্রশ্ন অনুশীলন: ${goal.todayQuestionsAttempted.toBengaliDigits()} / ${goal.dailyTargetQuestions.toBengaliDigits()}টি",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(goal.questionsProgressFraction * 100).toInt().toBengaliDigits()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { goal.questionsProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Study Time Goal Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "আজকের অধ্যয়ন সময়: ${goal.todayStudyMinutes.toBengaliDigits()} / ${goal.dailyTargetMinutes.toBengaliDigits()} মিনিট",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(goal.timeProgressFraction * 100).toInt().toBengaliDigits()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { goal.timeProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        // 7-day Activity Calendar strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            goal.last7DaysActivity.forEach { dayPt ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when {
                                            dayPt.goalMet -> Color(0xFF2E7D32)
                                            dayPt.questionsAttempted > 0 -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = dayPt.shortLabelBn,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (dayPt.questionsAttempted > 0) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${dayPt.questionsAttempted.toBengaliDigits()}টি",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Separated Examination KPIs vs. Practice KPIs
            item {
                val kpi = analyticsData.kpiSummary
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "পরীক্ষা ও অনুশীলনের পৃথক মূল্যায়ন সূচক (KPI)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiMetricCard(
                            icon = Icons.Default.EmojiEvents,
                            titleBn = "গড় পরীক্ষার স্কোর",
                            valueBn = "${kpi.examAverageScorePercent.toBengaliDigits()}%",
                            subtitleBn = "মোট ${kpi.totalExamsCompleted.toBengaliDigits()}টি পরীক্ষা • সেরা ${kpi.examBestScorePercent.toBengaliDigits()}%",
                            modifier = Modifier
                                .weight(1f)
                                .testTag("kpi_exam_avg_score")
                        )
                        KpiMetricCard(
                            icon = Icons.Default.CheckCircle,
                            titleBn = "পরীক্ষার নির্ভুলতা",
                            valueBn = "${kpi.examOverallAccuracyPercent.toBengaliDigits()}%",
                            subtitleBn = "${kpi.examCorrectAnswers.toBengaliDigits()}/${kpi.examQuestionsAttempted.toBengaliDigits()}টি সঠিক উত্তর",
                            modifier = Modifier
                                .weight(1f)
                                .testTag("kpi_exam_accuracy")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiMetricCard(
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            titleBn = "অনুশীলন নির্ভুলতা",
                            valueBn = "${kpi.practiceAccuracyPercent.toBengaliDigits()}%",
                            subtitleBn = "${kpi.totalPracticeSessions.toBengaliDigits()}টি সেশন • ${kpi.practiceQuestionsAttempted.toBengaliDigits()}টি প্রশ্ন",
                            modifier = Modifier
                                .weight(1f)
                                .testTag("kpi_practice_accuracy")
                        )
                        KpiMetricCard(
                            icon = Icons.Default.Star,
                            titleBn = "সেরা স্ট্রিক ও পাঠ",
                            valueBn = "${kpi.practiceBestStreak.toBengaliDigits()}🔥",
                            subtitleBn = "শেখা আইটেম: ${kpi.learnedItemsCount.toBengaliDigits()}টি • বুকমার্ক: ${bookmarks.size.toBengaliDigits()}টি",
                            modifier = Modifier
                                .weight(1f)
                                .testTag("kpi_best_streak")
                        )
                    }
                }
            }

            // 3B. NCTB Textbook Curriculum Progress Card (4 Books, 47 Chapters)
            item(key = "dashboard_book_progress_card") {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openBookHub() }
                        .testTag("dashboard_book_progress_card"),
                    shape = RoundedCornerShape(20.dp)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "পাঠ্যবই শিক্ষা অগ্রগতি (৬ষ্ঠ – ৯ম-১০ম শ্রেণি)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "৪টি বোর্ড বই • ৪৭টি অধ্যায় • সম্পূর্ণ অফলাইন পাঠ্যক্রম",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            TextButton(onClick = { viewModel.openBookHub() }) {
                                Text("খুলুন", fontWeight = FontWeight.Bold)
                            }
                        }

                        BookId.entries.forEach { book ->
                            val totalLessons = BookCurriculumRepository.getTotalLessonsCount(book).coerceAtLeast(1)
                            val doneLessons = bookLessonProgress.count { it.bookId == book.id && it.isCompleted }
                            val pct = ((doneLessons * 100) / totalLessons).coerceIn(0, 100)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${book.titleBn} (${book.totalChapters.toBengaliDigits()} অধ্যায়)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${doneLessons.toBengaliDigits()}/${totalLessons.toBengaliDigits()} পাঠ (${pct.toBengaliDigits()}%)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { pct / 100f },
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

            // 3C. Cross-Module Ecosystem Completion & Unified Spaced Mistake Notebook Card (Room v7)
            item(key = "dashboard_ecosystem_mastery_card") {
                val bcsDone = bcsTopicProgress.filter { it.isGuideCompleted }.map { it.rawTopicId }.distinct().size
                val bcsTotal = com.example.math.BcsCurriculumAndGuideRepository.mathGuideLessons.size.coerceAtLeast(1)
                val unresolvedUnified = unifiedMistakes.filter { !it.resolved }
                val practicedFormulasCount = formulaMastery.count { it.practiceCount > 0 }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_ecosystem_mastery_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ইকোসিস্টেম সমন্বিত অগ্রগতি ও স্পেসড রিভিশন (Room v7)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• বিসিএস গাইড সম্পন্ন: ${bcsDone.toBengaliDigits()}/${bcsTotal.toBengaliDigits()}টি পাঠ\n" +
                                    "• চর্চাকৃত সূত্র (Formula Mastery): ${practicedFormulasCount.toBengaliDigits()}টি সূত্র\n" +
                                    "• অমীমাংসিত ভুল প্রশ্ন (Spaced Mistake Queue): ${unresolvedUnified.size.toBengaliDigits()}টি",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (unresolvedUnified.isNotEmpty()) {
                            unresolvedUnified.take(3).forEach { mistake ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
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
                                                text = mistake.questionPromptBn,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2
                                            )
                                            Text(
                                                text = "সঠিক উত্তর: ${mistake.correctAnswerBn} • ভুলের সংখ্যা: ${mistake.failureCount.toBengaliDigits()}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        TextButton(
                                            onClick = { viewModel.resolveUnifiedMistakeItem(mistake.mistakeKey) },
                                            modifier = Modifier.testTag("btn_resolve_mistake_${mistake.mistakeKey}")
                                        ) {
                                            Text("সমাধিত", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.BcsPreparation) },
                                modifier = Modifier.weight(1f).testTag("btn_dashboard_to_bcs")
                            ) {
                                Text("বিসিএস প্রস্তুতি", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.FormulaLibrary) },
                                modifier = Modifier.weight(1f).testTag("btn_dashboard_to_formula_lib")
                            ) {
                                Text("সূত্র লাইব্রেরি", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. Deterministic Learning Recommendations Engine
            if (analyticsData.recommendations.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_recommendations_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "স্মার্ট লার্নিং সুপারিশ (Rule-Based)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            analyticsData.recommendations.forEach { rec ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("recommendation_item_${rec.id}")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = rec.badgeBn,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                            TextButton(
                                                onClick = { viewModel.executeLearningRecommendation(rec) },
                                                modifier = Modifier.testTag("btn_rec_action_${rec.id}")
                                            ) {
                                                Text(rec.actionLabelBn, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        Text(
                                            text = rec.titleBn,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = rec.reasonBn,
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

            // 5. Confidence-Weighted Topic Mastery Section
            item {
                Text(
                    text = "অধ্যায়ভিত্তিক দক্ষতা ও আস্থা বিশ্লেষণ (Topic Mastery)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (analyticsData.topicMasteryList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_empty_mastery_card"),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Analytics,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "টপিকভিত্তিক দক্ষতার তথ্য এখনো তৈরি হয়নি",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "কোনো অধ্যায়ে দক্ষ (Mastered) স্বীকৃতি পেতে কমপক্ষে ১০টি প্রশ্নে ৮০%+ নির্ভুলতা প্রয়োজন।",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(analyticsData.topicMasteryList, key = { "${it.topicId}_${it.topicTitleBn}" }) { report ->
                    TopicMasteryAnalyticsCard(report = report)
                }
            }

            // 6. Module Activity History Filter & Recent Sessions
            item {
                Column {
                    Text(
                        text = "মডিউলভিত্তিক সাম্প্রতিক সেশনসমূহ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(moduleFilterScrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "ALL" to "সকল মডিউল",
                            "PRACTICE_LAB" to "প্র্যাকটিস ল্যাব",
                            "MENTAL_MATH" to "মেন্টাল ম্যাথ",
                            "QUIZ" to "কুইজ ও পরীক্ষা",
                            "NUMBER_SYSTEM" to "বাস্তব সংখ্যা",
                            "BOOK" to "পাঠ্যবই অনুশীলন"
                        ).forEach { (key, label) ->
                            FilterChip(
                                selected = selectedModuleFilter == key,
                                onClick = { selectedModuleFilter = key },
                                label = { Text(label) },
                                modifier = Modifier.testTag("dashboard_module_filter_$key")
                            )
                        }
                    }
                }
            }

            if (filteredHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "এই ফিল্টারে এখনো কোনো অনুশীলন সেশন নেই।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredHistory.take(15), key = { it.id }) { session ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
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
                                    text = session.topicTitleBn,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "সঠিক: ${session.correctCount.toBengaliDigits()}/${session.totalQuestions.toBengaliDigits()} • স্ট্রিক: ${session.bestStreak.toBengaliDigits()} • ${dateFormatter.format(Date(session.timestamp))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (session.accuracyPercent >= 80) {
                                    Color(0xFF2E7D32).copy(alpha = 0.15f)
                                } else {
                                    MaterialTheme.colorScheme.primaryContainer
                                }
                            ) {
                                Text(
                                    text = "${session.accuracyPercent.toBengaliDigits()}%",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (session.accuracyPercent >= 80) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onPrimaryContainer
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
private fun TopicMasteryAnalyticsCard(report: TopicMasteryReport) {
    val badgeColor = when (report.masteryStatus) {
        TopicMasteryStatus.MASTERED -> Color(0xFF2E7D32)
        TopicMasteryStatus.DEVELOPING -> MaterialTheme.colorScheme.primary
        TopicMasteryStatus.WEAK -> MaterialTheme.colorScheme.error
        TopicMasteryStatus.NEEDS_MORE_DATA -> Color(0xFFEF6C00)
        TopicMasteryStatus.UNTESTED -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("topic_mastery_card_${report.topicId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${report.topicTitleBn} (${report.trend.symbol} ${report.trend.titleBn})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${report.confidenceLevel.titleBn} • গড় সময়: ${report.avgTimePerQuestionSeconds.toInt().toBengaliDigits()} সে.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = report.masteryStatus.titleBn,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (report.accuracyPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = badgeColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = report.explanationBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun KpiMetricCard(
    icon: ImageVector,
    titleBn: String,
    valueBn: String,
    subtitleBn: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = valueBn,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = titleBn,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitleBn,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
