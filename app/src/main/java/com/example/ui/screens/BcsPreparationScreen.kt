package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.math.BcsCurriculumAndGuideRepository
import com.example.math.BcsExamProfile
import com.example.math.BcsMathQuestion
import com.example.math.BcsMentalAbilityCategory
import com.example.math.BcsMentalAbilityQuestion
import com.example.math.BcsPreparationRepository
import com.example.math.BcsPreparationStage
import com.example.math.BcsQuestionSourceType
import com.example.math.BcsSyllabusUnit
import com.example.math.BcsSyllabusVersion
import com.example.math.BcsTopicGuideLesson
import com.example.math.BengaliNumberFormatter
import com.example.math.toBengaliDigits
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.BcsDiagramView
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedScrollState
import com.example.ui.rememberManagedUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BcsPreparationScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val topicAnalytics by viewModel.topicAnalytics.collectAsStateWithLifecycle()
    val selectedSyllabusVersion by viewModel.selectedBcsSyllabusVersion.collectAsStateWithLifecycle()
    val selectedExamProfile by viewModel.selectedBcsExamProfile.collectAsStateWithLifecycle()
    val userProfile by viewModel.bcsUserProfile.collectAsStateWithLifecycle()
    val topicProgressList by viewModel.bcsTopicProgress.collectAsStateWithLifecycle()
    val mistakesList by viewModel.bcsMistakes.collectAsStateWithLifecycle()
    val studyGoals by viewModel.bcsStudyGoals.collectAsStateWithLifecycle()

    val profileTopicProgress = remember(topicProgressList, selectedExamProfile) {
        topicProgressList.filter { it.examProfileId == selectedExamProfile.id }
    }
    val profileMistakes = remember(mistakesList, selectedExamProfile) {
        mistakesList.filter { it.examProfileId == selectedExamProfile.id }
    }

    val weakUnitAnalysis = remember(topicAnalytics, selectedSyllabusVersion) {
        BcsPreparationRepository.analyzeBcsWeakUnits(topicAnalytics, selectedSyllabusVersion)
    }
    val weakTopicDetails = remember(profileTopicProgress, profileMistakes, selectedExamProfile) {
        com.example.math.BcsWeakTopicIntelligenceEngine.analyzeWeakTopics(
            examProfileId = selectedExamProfile.id,
            topicProgressList = profileTopicProgress,
            mistakesList = profileMistakes
        )
    }

    var selectedTab by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:selectedTab",
        defaultValue = 0
    ) // 0: ড্যাশবোর্ড, 1: গাণিতিক যুক্তি, 2: মানসিক দক্ষতা, 3: বিগত প্রশ্ন, 4: মক টেস্ট ও রিভিশন
    var showProfileDialog by remember { mutableStateOf(false) }
    var selectedLessonForDetails by rememberManagedUiState<BcsTopicGuideLesson?>(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:selectedLessonForDetails",
        defaultValue = null
    )

    // First-launch Onboarding Trigger
    val isFirstLaunch = userProfile == null || !userProfile!!.onboardingCompleted

    // Unit, Domain & Source filters
    var selectedUnitFilter by rememberManagedUiState<BcsSyllabusUnit?>(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:selectedUnitFilter",
        defaultValue = null
    )
    var selectedMathDomainFilter by rememberManagedUiState<com.example.math.BcsMathDomain?>(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:selectedMathDomainFilter",
        defaultValue = null
    )
    var selectedSourceFilter by rememberManagedUiState<BcsQuestionSourceType?>(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:selectedSourceFilter",
        defaultValue = null
    )
    var selectedMentalCategory by rememberManagedUiState<BcsMentalAbilityCategory?>(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:selectedMentalCategory",
        defaultValue = null
    )
    var selectedPracticeMode by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:selectedPracticeMode",
        defaultValue = com.example.math.BcsPracticeMode.BCS_50TH_PRELIM_35_MOCK
    )

    // Mock Exam Configuration
    var mockQuestionCount by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:${selectedSyllabusVersion.name}:mockQuestionCount",
        defaultValue = selectedSyllabusVersion.totalMarks
    )
    var mockDurationMinutes by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:${selectedSyllabusVersion.name}:mockDurationMinutes",
        defaultValue = selectedSyllabusVersion.totalMarks
    )
    var mockNegativeMark by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:mockNegativeMark",
        defaultValue = 0.0
    )
    var mockTargetUnit by rememberManagedUiState<BcsSyllabusUnit?>(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${selectedExamProfile.id}:mockTargetUnit",
        defaultValue = null
    )

    // First-Launch or Manual Profile Onboarding Dialog
    if (isFirstLaunch || showProfileDialog) {
        BcsProfileOnboardingDialog(
            currentProfile = selectedExamProfile,
            currentStage = BcsPreparationStage.fromNumber(userProfile?.currentStageIndex ?: 1),
            dailyMinutes = userProfile?.dailyTargetMinutes ?: 30,
            dailyQuestions = userProfile?.dailyTargetQuestions ?: 20,
            onDismiss = { if (!isFirstLaunch) showProfileDialog = false },
            onSave = { profile, stage, minutes, questions ->
                viewModel.saveBcsOnboarding(profile, stage, minutes, questions)
                showProfileDialog = false
            }
        )
    }

    // Lesson Details Dialog with 16-Part Guide & Direct Links
    selectedLessonForDetails?.let { lesson ->
        BcsLessonDetailsDialog(
            lesson = lesson,
            isCompleted = profileTopicProgress.any { it.rawTopicId == lesson.topicId && it.isGuideCompleted },
            miniCheckPassed = profileTopicProgress.any { it.rawTopicId == lesson.topicId && it.miniCheckPassed },
            onDismiss = { selectedLessonForDetails = null },
            onCompleteGuide = {
                viewModel.completeBcsTopicGuide(lesson.topicId, "MATH")
            },
            onMiniCheckAnswer = { passed ->
                viewModel.recordBcsMiniCheckResult(lesson.topicId, "MATH", passed)
            },
            onOpenNctbBook = { bookId, chNum ->
                selectedLessonForDetails = null
                viewModel.openBookChapter(bookId, chNum)
            },
            onOpenFormula = { formulaId ->
                selectedLessonForDetails = null
                viewModel.openFormula(formulaId)
            },
            onOpenSolver = {
                selectedLessonForDetails = null
                viewModel.navigateTo(Screen.ProblemSolver)
            },
            onOpenLab = { labType ->
                selectedLessonForDetails = null
                lesson.linkedNctbBookId?.let { bId ->
                    lesson.linkedNctbChapterNumber?.let { cNum ->
                        viewModel.openBookInteractiveLab(labType, bId, cNum)
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("বিসিএস প্রস্তুতি কেন্দ্র", fontWeight = FontWeight.Bold)
                        Text(
                            "${selectedExamProfile.shortBadgeBn} • যাচাইকৃত অফিশিয়াল সিলেবাস",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("bcs_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showProfileDialog = true },
                        modifier = Modifier.testTag("bcs_profile_settings_btn")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "বিসিএস প্রোফাইল ও লক্ষ্য")
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
            // Top 5-pill Workspace Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.testTag("bcs_tab_dashboard"),
                    text = { Text("ড্যাশবোর্ড ও লক্ষ্য", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.testTag("bcs_tab_syllabus"),
                    text = { Text("গাণিতিক যুক্তি গাইড", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.testTag("bcs_tab_mental"),
                    text = { Text("মানসিক দক্ষতা", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    modifier = Modifier.testTag("bcs_tab_questions"),
                    text = { Text("বিগত প্রশ্ন ও ব্যাংক", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    modifier = Modifier.testTag("bcs_tab_mock"),
                    text = { Text("মক টেস্ট ও রিভিশন", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                // ==========================================
                // TAB 0: DASHBOARD & 8-STAGE JOURNEY
                // ==========================================
                0 -> {
                    BcsDashboardView(
                        viewModel = viewModel,
                        profile = selectedExamProfile,
                        currentStage = BcsPreparationStage.fromNumber(userProfile?.currentStageIndex ?: 1),
                        weakUnits = weakUnitAnalysis,
                        weakTopics = weakTopicDetails,
                        studyGoals = studyGoals,
                        dailyTargetMinutes = userProfile?.dailyTargetMinutes ?: 30,
                        dailyTargetQuestions = userProfile?.dailyTargetQuestions ?: 20,
                        mistakesCount = profileMistakes.count { !it.isResolved },
                        completedLessonsCount = profileTopicProgress
                            .filter { it.isGuideCompleted }
                            .map { it.rawTopicId }
                            .distinct()
                            .size,
                        totalLessons = BcsCurriculumAndGuideRepository.mathGuideLessons.size,
                        onOpenSyllabus = { selectedTab = 1 },
                        onOpenMentalAbility = { selectedTab = 2 },
                        onOpenPreviousQuestions = { selectedTab = 3 },
                        onOpenMockExam = { selectedTab = 4 },
                        onChangeProfile = { showProfileDialog = true }
                    )
                }

                // ==========================================
                // TAB 1: MATHEMATICAL REASONING GUIDE & UNITS
                // ==========================================
                1 -> {
                    val tab1ListState = rememberManagedLazyListState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_1"
                    )
                    val tab1VersionScrollState = rememberManagedScrollState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_1:version_row"
                    )
                    val tab1DomainScrollState = rememberManagedScrollState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_1:domain_row"
                    )
                    LazyColumn(
                        state = tab1ListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("bcs_tab_1_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bcs_syllabus_version_selector"),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "সিলেবাস সংস্করণ নির্বাচন:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(tab1VersionScrollState),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedSyllabusVersion == BcsSyllabusVersion.BCS_50TH_CURRENT,
                                        onClick = {
                                            viewModel.setBcsSyllabusVersion(BcsSyllabusVersion.BCS_50TH_CURRENT)
                                        },
                                        label = { Text("৫০তম বিসিএস (২০ নম্বর • ডিফল্ট)") },
                                        modifier = Modifier.testTag("bcs_version_50th_current")
                                    )
                                    FilterChip(
                                        selected = selectedSyllabusVersion == BcsSyllabusVersion.BCS_LEGACY_15,
                                        onClick = {
                                            viewModel.setBcsSyllabusVersion(BcsSyllabusVersion.BCS_LEGACY_15)
                                        },
                                        label = { Text("প্রাক্তন সিলেবাস (১৫ নম্বর • আর্কাইভড)") },
                                        modifier = Modifier.testTag("bcs_version_legacy_15")
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bcs_syllabus_header_card"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = selectedSyllabusVersion.titleBn,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "পিএসসি অফিশিয়াল সিলেবাস অনুযায়ী ৫টি ইউনিট ও ৯টি গাণিতিক ডোমেইনে বিন্যস্ত ১৯-ধাপের পূর্ণাঙ্গ গাইড, শর্টকাট প্রমাণ, লিখিত রুব্রিক ও পাঠ্যবই সংযোগ নিচে দেখুন।",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                                    )
                                }
                            }
                        }

                        // 9-Domain Filter Strip for Mathematical Reasoning
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "৯টি গাণিতিক ডোমেইন ফিল্টার (প্রিলিমিনারি ও লিখিত কোড ০০৮):",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(tab1DomainScrollState),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedMathDomainFilter == null,
                                        onClick = { selectedMathDomainFilter = null },
                                        label = { Text("সকল ডোমেইন (৯টি)") },
                                        modifier = Modifier.testTag("bcs_domain_filter_all")
                                    )
                                    com.example.math.BcsMathDomain.entries.forEach { domain ->
                                        FilterChip(
                                            selected = selectedMathDomainFilter == domain,
                                            onClick = { selectedMathDomainFilter = domain },
                                            label = { Text(domain.titleBn) },
                                            modifier = Modifier.testTag("bcs_domain_filter_${domain.id}")
                                        )
                                    }
                                }
                            }
                        }

                        val activeDomain = selectedMathDomainFilter
                        if (activeDomain != null) {
                            val domainLessons = BcsCurriculumAndGuideRepository.getLessonsForDomain(activeDomain)
                            items(domainLessons, key = { "dom_${it.topicId}" }) { lesson ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedLessonForDetails = lesson },
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "${lesson.mathDomain.titleBn} • ${lesson.authorityBadge.labelBn}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(lesson.topicTitleBn, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(lesson.simpleConceptExplanationBn.ifBlank { lesson.conceptIntroBn }, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        // 5 BPSC Mathematical Reasoning Units
                        items(weakUnitAnalysis, key = { it.unit.id }) { analysis ->
                            val unit = analysis.unit
                            val unitLessons = BcsCurriculumAndGuideRepository.getLessonsForUnit(unit)

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bcs_unit_card_${unit.id}"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = "${unit.unitNumberBn} • বরাদ্দ: ${analysis.allocatedMarksBn}",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (analysis.isWeak) MaterialTheme.colorScheme.errorContainer else Color(0xFF2E7D32).copy(alpha = 0.14f)
                                        ) {
                                            Text(
                                                text = analysis.statusBn,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (analysis.isWeak) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = unit.titleBn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "অফিশিয়াল বিষয়বস্তু: ${unit.officialTopicsBn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (unitLessons.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "১৬ ধাপের অধ্যায়ভিত্তিক পাঠ্য গাইড:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        unitLessons.forEach { lesson ->
                                            val isDone = profileTopicProgress.any { it.rawTopicId == lesson.topicId && it.isGuideCompleted }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                    .clickable { selectedLessonForDetails = lesson }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = lesson.topicTitleBn,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                if (isDone) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "সম্পন্ন", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                                } else {
                                                    Text("পাঠ পড়ুন ➜", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                selectedUnitFilter = unit
                                                selectedTab = 3
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_bcs_unit_questions_${unit.id}")
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("প্রশ্ন ও শর্টকাট")
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.startBcsMockExam(
                                                    unit = unit,
                                                    syllabusVersion = selectedSyllabusVersion,
                                                    questionCount = 10,
                                                    durationMinutes = 10,
                                                    negativeMarking = 0.0
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_bcs_unit_practice_${unit.id}")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("অনুশীলন টেস্ট")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.openBookHub() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_bcs_unit_nctb_${unit.id}")
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("পাঠ্যবই ফাউন্ডেশন (৬ষ্ঠ - ৯ম-১০ম এনসিটিবি অধ্যায়)")
                                    }
                                }
                            }
                        }

                        // Written Code 008 / 009 Step-by-Step Descriptive Rubric Card
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bcs_written_rubric_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "বিসিএস লিখিত (কোড ০০৮) বর্ণনামূলক উত্তর নির্দেশিকা",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "লিখিত পরীক্ষায় পূর্ণ নম্বর পেতে শর্টকাট বর্জন করে ৪টি ধাপ অনুসরণ করুন:\n" +
                                                "১. প্রদত্ত উপাত্ত ও ধরি (Given & Let)\n" +
                                                "২. প্রশ্নমতে সমীকরণ গঠন (Equation Setup)\n" +
                                                "৩. ধাপে ধাপে বীজগণিতীয় বা জ্যামিতিক সমাধান\n" +
                                                "৪. এককসহ নির্ণেয় চূড়ান্ত উত্তর (Final Answer with Unit)",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // TAB 2: MENTAL ABILITY TRACK
                // ==========================================
                2 -> {
                    BcsMentalAbilityTrackView(
                        viewModel = viewModel,
                        profileId = selectedExamProfile.id,
                        selectedCategory = selectedMentalCategory,
                        onSelectCategory = { selectedMentalCategory = it },
                        onRecordMistake = { q, userAns ->
                            viewModel.recordBcsMistake(
                                questionId = q.id,
                                subjectTrack = "MENTAL_ABILITY",
                                topicId = q.category.id,
                                promptBn = q.questionBn,
                                wrongAnsBn = userAns,
                                correctAnsBn = q.correctAnswerBn,
                                explanationBn = q.explanationBn,
                                shortcutTipBn = q.shortcutTipBn,
                                provenanceBadgeBn = q.provenanceTier.badgeBn
                            )
                        }
                    )
                }

                // ==========================================
                // TAB 3: VERIFIED PREVIOUS QUESTIONS & PRACTICE
                // ==========================================
                3 -> {
                    val questions = remember(selectedUnitFilter, selectedSourceFilter) {
                        BcsPreparationRepository.getQuestionsByUnit(selectedUnitFilter, selectedSourceFilter)
                    }
                    val verifiedCount = BcsPreparationRepository.verifiedPreviousBcsQuestions.size
                    val practiceCount = BcsPreparationRepository.syllabusAlignedPracticeQuestions.size
                    val tab3ListState = rememberManagedLazyListState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_3"
                    )
                    val tab3UnitScrollState = rememberManagedScrollState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_3:unit_row"
                    )
                    val tab3SourceScrollState = rememberManagedScrollState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_3:source_row"
                    )

                    LazyColumn(
                        state = tab3ListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("bcs_tab_3_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("অধ্যায় ফিল্টার:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(tab3UnitScrollState),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedUnitFilter == null,
                                        onClick = { selectedUnitFilter = null },
                                        label = { Text("সকল অধ্যায়") },
                                        modifier = Modifier.testTag("bcs_filter_unit_ALL")
                                    )
                                    BcsSyllabusUnit.values().forEach { u ->
                                        FilterChip(
                                            selected = selectedUnitFilter == u,
                                            onClick = { selectedUnitFilter = u },
                                            label = { Text(u.unitNumberBn) },
                                            modifier = Modifier.testTag("bcs_filter_unit_${u.id}")
                                        )
                                    }
                                }

                                Text("প্রশ্নের উৎস ফিল্টার (Strict Provenance):", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(tab3SourceScrollState),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedSourceFilter == null,
                                        onClick = { selectedSourceFilter = null },
                                        label = { Text("সকল প্রশ্ন (${BcsPreparationRepository.allBcsQuestions.size.toBengaliDigits()})") }
                                    )
                                    FilterChip(
                                        selected = selectedSourceFilter == BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
                                        onClick = { selectedSourceFilter = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS },
                                        label = { Text("বিগত বিসিএস (${verifiedCount.toBengaliDigits()})") },
                                        modifier = Modifier.testTag("bcs_filter_source_official")
                                    )
                                    FilterChip(
                                        selected = selectedSourceFilter == BcsQuestionSourceType.SYLLABUS_PRACTICE,
                                        onClick = { selectedSourceFilter = BcsQuestionSourceType.SYLLABUS_PRACTICE },
                                        label = { Text("সিলেবাস মডেল (${practiceCount.toBengaliDigits()})") },
                                        modifier = Modifier.testTag("bcs_filter_source_practice")
                                    )
                                }
                            }
                        }

                        items(questions, key = { it.id }) { q ->
                            BcsQuestionInteractiveCard(
                                question = q,
                                syllabusVersion = selectedSyllabusVersion,
                                onWrongAnswer = { userAns ->
                                    viewModel.recordBcsMistake(
                                        questionId = q.id,
                                        subjectTrack = "MATH",
                                        topicId = q.unit.id,
                                        promptBn = q.questionBn,
                                        wrongAnsBn = userAns,
                                        correctAnsBn = q.correctAnswerBn,
                                        explanationBn = q.shortcutTechniqueBn,
                                        shortcutTipBn = q.formulaUsedBn,
                                        provenanceBadgeBn = q.sourceType.badgeBn
                                    )
                                }
                            )
                        }
                    }
                }

                // ==========================================
                // TAB 4: MOCK TEST SIMULATOR & MISTAKE NOTEBOOK
                // ==========================================
                4 -> {
                    val tab4ListState = rememberManagedLazyListState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_4"
                    )
                    val tab4VersionScrollState = rememberManagedScrollState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_4:version_row"
                    )
                    val tab4NegScrollState = rememberManagedScrollState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_4:neg_row"
                    )
                    val tab4ModeScrollState = rememberManagedScrollState(
                        viewModel = viewModel,
                        stateKey = "bcs_preparation:${selectedExamProfile.id}:tab_4:mode_row"
                    )
                    LazyColumn(
                        state = tab4ListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("bcs_tab_4_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bcs_mock_config_card"),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "বিসিএস মক টেস্ট কনফিগারেশন",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "সক্রিয় সিলেবাস: ${selectedSyllabusVersion.titleBn}",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "৫০তম বিসিএস প্রিলিমিনারি মানদণ্ড: গাণিতিক যুক্তি ২০ নম্বর, নেগেটিভ মার্কিং -০.৫০ (বা কাস্টম প্রিসেট)।",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    Text("সিলেবাস সংস্করণ:", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(tab4VersionScrollState),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChip(
                                            selected = selectedSyllabusVersion == BcsSyllabusVersion.BCS_50TH_CURRENT,
                                            onClick = {
                                                viewModel.setBcsSyllabusVersion(BcsSyllabusVersion.BCS_50TH_CURRENT)
                                                mockQuestionCount = BcsSyllabusVersion.BCS_50TH_CURRENT.totalMarks
                                                mockDurationMinutes = BcsSyllabusVersion.BCS_50TH_CURRENT.totalMarks
                                            },
                                            label = { Text("৫০তম বিসিএস (২০ নম্বর • ডিফল্ট)") },
                                            modifier = Modifier.testTag("bcs_mock_version_50th")
                                        )
                                        FilterChip(
                                            selected = selectedSyllabusVersion == BcsSyllabusVersion.BCS_LEGACY_15,
                                            onClick = {
                                                viewModel.setBcsSyllabusVersion(BcsSyllabusVersion.BCS_LEGACY_15)
                                                mockQuestionCount = BcsSyllabusVersion.BCS_LEGACY_15.totalMarks
                                                mockDurationMinutes = BcsSyllabusVersion.BCS_LEGACY_15.totalMarks
                                            },
                                            label = { Text("প্রাক্তন সিলেবাস (১৫ নম্বর • আর্কাইভড)") },
                                            modifier = Modifier.testTag("bcs_mock_version_legacy")
                                        )
                                    }

                                    Text("প্রশ্ন সংখ্যা:", fontWeight = FontWeight.SemiBold)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(10, 15, 20, 30).forEach { cnt ->
                                            FilterChip(
                                                selected = mockQuestionCount == cnt,
                                                onClick = { mockQuestionCount = cnt },
                                                label = { Text("${cnt.toBengaliDigits()}টি") },
                                                modifier = Modifier.testTag("bcs_mock_qcount_$cnt")
                                            )
                                        }
                                    }

                                    Text("নেগেটিভ মার্কিং:", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(tab4NegScrollState),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        BcsPreparationRepository.negativeMarkingOptions.forEach { option ->
                                            FilterChip(
                                                selected = mockNegativeMark == option.penaltyPerWrong,
                                                onClick = { mockNegativeMark = option.penaltyPerWrong },
                                                label = { Text(option.labelBn) },
                                                modifier = Modifier.testTag("bcs_mock_neg_${option.penaltyPerWrong}")
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.startBcsMockExam(
                                                unit = mockTargetUnit,
                                                syllabusVersion = selectedSyllabusVersion,
                                                questionCount = mockQuestionCount,
                                                durationMinutes = mockDurationMinutes,
                                                negativeMarking = mockNegativeMark
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("btn_start_bcs_mock_exam"),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "মক টেস্ট শুরু করুন (${mockQuestionCount.toBengaliDigits()} নম্বর)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // 14-Mode Specialized BCS Practice & Mock Center
                        item {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bcs_14_modes_card"),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "১৪টি বিশেষায়িত বিসিএস প্র্যাকটিস ও মক মোড (${selectedExamProfile.shortBadgeBn})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(tab4ModeScrollState),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        com.example.math.BcsPracticeMode.entries.forEach { mode ->
                                            FilterChip(
                                                selected = selectedPracticeMode == mode,
                                                onClick = { selectedPracticeMode = mode },
                                                label = { Text(mode.titleBn) },
                                                modifier = Modifier.testTag("bcs_practice_mode_chip_${mode.id}")
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${selectedPracticeMode.subtitleBn} • প্রশ্ন: ${selectedPracticeMode.defaultQuestionCount.toBengaliDigits()}টি • সময়: ${selectedPracticeMode.durationMinutes.toBengaliDigits()} মিনিট",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = {
                                            viewModel.startBcsModeExam(
                                                mode = selectedPracticeMode,
                                                unit = mockTargetUnit
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_start_bcs_14_mode_exam"),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${selectedPracticeMode.titleBn} শুরু করুন", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // BCS Mistake Notebook Section
                        item {
                            Text(
                                text = "ভুল উত্তর নোটবুক (${profileMistakes.count { !it.isResolved }.toBengaliDigits()}টি অসমাধিত):",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (profileMistakes.isEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("এখনো কোনো ভুল উত্তর জমা হয়নি। নিয়মিত অনুশীলন ও মক টেস্ট দিলে ভুলগুলো এখানে স্বয়ংক্রিয়ভাবে স্পেসড রিভিশনের জন্য সংরক্ষিত হবে।", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        } else {
                            items(profileMistakes, key = { it.questionId }) { mistake ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = mistake.provenanceBadgeBn.ifBlank { "বিসিএস প্রশ্ন" },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (mistake.isResolved) Color(0xFF2E7D32).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                                            ) {
                                                Text(
                                                    text = if (mistake.isResolved) "সমাধান হয়েছে" else "রিভিশন বাকি",
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (mistake.isResolved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }

                                        Text(text = mistake.questionPromptBn, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text(text = "আপনার দেওয়া ভুল উত্তর: ${mistake.wrongAnswerBn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                        Text(text = "সঠিক উত্তর: ${mistake.correctAnswerBn}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)

                                        if (mistake.explanationBn.isNotBlank()) {
                                            Text(text = "ব্যাখ্যা: ${mistake.explanationBn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (!mistake.isResolved) {
                                                TextButton(onClick = { viewModel.markBcsMistakeResolved(mistake.questionId) }) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("সমাধান হিসেবে চিহ্নিত করুন")
                                                }
                                            }
                                            IconButton(onClick = { viewModel.deleteBcsMistake(mistake.questionId) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "মুছে ফেলুন", modifier = Modifier.size(18.dp))
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

/**
 * BCS Dashboard Component with 8-Stage Preparation Stepper & Weak Intelligence.
 */
@Composable
private fun BcsDashboardView(
    viewModel: MathViewModel,
    profile: BcsExamProfile,
    currentStage: BcsPreparationStage,
    weakUnits: List<com.example.math.BcsWeakUnitAnalysis>,
    weakTopics: List<com.example.math.BcsWeakTopicDiagnostic>,
    studyGoals: List<com.example.data.model.BcsStudyPlanGoalEntity>,
    dailyTargetMinutes: Int,
    dailyTargetQuestions: Int,
    mistakesCount: Int,
    completedLessonsCount: Int,
    totalLessons: Int,
    onOpenSyllabus: () -> Unit,
    onOpenMentalAbility: () -> Unit,
    onOpenPreviousQuestions: () -> Unit,
    onOpenMockExam: () -> Unit,
    onChangeProfile: () -> Unit
) {
    val progressPct = if (totalLessons > 0) (completedLessonsCount * 100) / totalLessons else 0
    val latestGoal = studyGoals.firstOrNull { it.examProfileId == profile.id }
    val tab0ListState = rememberManagedLazyListState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${profile.id}:tab_0"
    )
    val stageRowState = rememberManagedLazyListState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:${profile.id}:tab_0:stage_row"
    )

    LazyColumn(
        state = tab0ListState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("bcs_tab_0_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF065F46)
                        ) {
                            Text(
                                text = profile.stageTitleBn,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        OutlinedButton(
                            onClick = onChangeProfile,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("প্রোফাইল বদলান", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = profile.titleBn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "গাণিতিক যুক্তি: ${profile.mathMarks.toBengaliDigits()} নম্বর • মানসিক দক্ষতা: ${profile.mentalAbilityMarks.toBengaliDigits()} নম্বর (মোট ${profile.totalSubjectMarks.toBengaliDigits()} নম্বর)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("সিলেবাস গাইড অগ্রগতি:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("${completedLessonsCount.toBengaliDigits()}/${totalLessons.toBengaliDigits()} পাঠ (${progressPct.toBengaliDigits()}%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressPct / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF059669)
                    )
                }
            }
        }

        // 8-Stage Preparation Stepper
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "আপনার প্রস্তুতি পর্যায় (৮টি ধাপের অগ্রযাত্রা):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    LazyRow(
                        state = stageRowState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(BcsPreparationStage.entries) { stage ->
                            val isCurrent = stage == currentStage
                            val isPast = stage.stageNumber < currentStage.stageNumber
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isCurrent -> Color(0xFF047857)
                                    isPast -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.width(140.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = stage.badgeBn,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = stage.titleBn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stage.subtitleBn,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2,
                                        color = if (isCurrent) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Study Planner & Weak-Topic Intelligence Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bcs_study_planner_and_weak_intelligence_card"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "দৈনিক অধ্যয়ন পরিকল্পনাকারী ও দুর্বল টপিক ইন্টেলিজেন্স",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val completedQ = latestGoal?.questionsSolved ?: 0
                    val completedM = latestGoal?.studyTimeMinutes ?: 0
                    Text(
                        text = "আজকের লক্ষ্য: ${completedQ.toBengaliDigits()}/${dailyTargetQuestions.toBengaliDigits()}টি প্রশ্ন • ${completedM.toBengaliDigits()}/${dailyTargetMinutes.toBengaliDigits()} মিনিট • অমীমাংসিত ভুল: ${mistakesCount.toBengaliDigits()}টি",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    val topWeak = weakTopics.firstOrNull()
                    if (topWeak != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "পরবর্তী করণীয় (${topWeak.topicTitleBn}):",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = topWeak.recommendedActionBn,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Entry Cards
        item {
            Text(text = "বিসিএস শিখন ও অনুশীলন মডিউল:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenSyllabus() }
                        .padding(4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("গাণিতিক যুক্তি গাইড", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("১৬ ধাপের পূর্ণাঙ্গ পাঠ", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenMentalAbility() }
                        .padding(4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("মানসিক দক্ষতা", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("৬টি বিষয় ও চিত্র বিশ্লেষণ", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenPreviousQuestions() }
                        .padding(4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("বিগত বিসিএস প্রশ্ন", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("যাচাইকৃত মূল প্রশ্নপত্র", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenMockExam() }
                        .padding(4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("মক টেস্ট ও রিভিশন", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("ভুল সংশোধন ও টাইমার", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/**
 * Mental Ability Track View with Canvas Diagram Support.
 */
@Composable
private fun BcsMentalAbilityTrackView(
    viewModel: MathViewModel,
    profileId: String,
    selectedCategory: BcsMentalAbilityCategory?,
    onSelectCategory: (BcsMentalAbilityCategory?) -> Unit,
    onRecordMistake: (BcsMentalAbilityQuestion, String) -> Unit
) {
    val questions = remember(selectedCategory) {
        BcsCurriculumAndGuideRepository.getMentalAbilityQuestionsForCategory(selectedCategory)
    }
    val tab2ListState = rememberManagedLazyListState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:$profileId:tab_2"
    )
    val tab2CategoryScrollState = rememberManagedScrollState(
        viewModel = viewModel,
        stateKey = "bcs_preparation:$profileId:tab_2:category_row"
    )

    LazyColumn(
        state = tab2ListState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("bcs_tab_2_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "বিসিএস মানসিক দক্ষতা বিভাগ (১৫ নম্বর):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tab2CategoryScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text("সকল বিভাগ (৬টি)") }
                    )
                    BcsMentalAbilityCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onSelectCategory(cat) },
                            label = { Text(cat.titleBn) }
                        )
                    }
                }
            }
        }

        // Category Concept & Written Code 009 Guide Card
        item {
            val guideCategory = selectedCategory ?: BcsMentalAbilityCategory.VERBAL_REASONING
            val catGuide = BcsCurriculumAndGuideRepository.getMentalAbilityGuideForCategory(guideCategory)
            if (catGuide != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bcs_mental_category_guide_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${guideCategory.titleBn} — মূলনীতি ও সমাধান কৌশল (কোড ০০৯ ও প্রিলিমিনারি)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = catGuide.corePrinciplesBn.joinToString(" • "), style = MaterialTheme.typography.bodySmall)
                        catGuide.solvingRulesBn.forEachIndexed { idx, r ->
                            Text(text = "${(idx + 1).toBengaliDigits()}. $r", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = "⚠ সাধারণ ভুল: ${catGuide.commonTrapsBn.joinToString(" • ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "লিখিত কোড ০০৯ নোট: ${catGuide.writtenCode009NotesBn}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        items(questions, key = { it.id }) { q ->
            var selectedOpt by remember(q.id) { mutableStateOf<Int?>(null) }
            ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                            Text(
                                text = q.category.titleBn,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = q.examReferenceBn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }

                    if (q.diagramType != com.example.math.BcsDiagramType.NONE) {
                        BcsDiagramView(diagramType = q.diagramType)
                    }

                    Text(text = q.questionBn, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                    q.optionsBn.forEachIndexed { idx, opt ->
                        val isSelected = selectedOpt == idx
                        val isCorrect = idx == q.correctIndex
                        val revealed = selectedOpt != null

                        OutlinedCard(
                            onClick = {
                                if (selectedOpt == null) {
                                    selectedOpt = idx
                                    if (idx != q.correctIndex) {
                                        onRecordMistake(q, opt)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                width = if (isSelected || (revealed && isCorrect)) 2.dp else 1.dp,
                                color = when {
                                    revealed && isCorrect -> Color(0xFF2E7D32)
                                    revealed && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = opt, style = MaterialTheme.typography.bodyMedium)
                                if (revealed) {
                                    if (isCorrect) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                    } else if (isSelected) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }

                    if (selectedOpt != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "যৌক্তিক সমাধান:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Text(text = q.explanationBn, style = MaterialTheme.typography.bodySmall)
                                if (q.shortcutTipBn.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "💡 কৌশল: ${q.shortcutTipBn}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 16-Part Lesson Details Dialog with Deep Integration Links.
 */
@Composable
private fun BcsLessonDetailsDialog(
    lesson: BcsTopicGuideLesson,
    isCompleted: Boolean,
    miniCheckPassed: Boolean,
    onDismiss: () -> Unit,
    onCompleteGuide: () -> Unit,
    onMiniCheckAnswer: (Boolean) -> Unit,
    onOpenNctbBook: (com.example.math.BookId, Int) -> Unit,
    onOpenFormula: (String) -> Unit,
    onOpenSolver: () -> Unit,
    onOpenLab: (com.example.math.BookInteractiveLabType) -> Unit
) {
    var selectedMiniOpt by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(text = lesson.topicTitleBn, fontWeight = FontWeight.Bold)
                Text(text = lesson.domainTitleBn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "${lesson.mathDomain.titleBn} • ${lesson.authorityBadge.labelBn}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(text = lesson.conceptIntroBn, style = MaterialTheme.typography.bodyMedium)
                if (lesson.simpleConceptExplanationBn.isNotBlank()) {
                    Text(text = "সহজ ব্যাখ্যা: ${lesson.simpleConceptExplanationBn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }

                HorizontalDivider()

                Text(text = "১. মূল ধারণা ও নিয়মাবলী:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Text(text = lesson.detailedExplanationBn, style = MaterialTheme.typography.bodySmall)

                Text(text = "২. আদর্শ সমাধান পদ্ধতি (Standard Method):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Text(text = lesson.standardMethodBn, style = MaterialTheme.typography.bodySmall)

                Text(text = "৩. স্মার্ট শর্টকাট কৌশল ও গাণিতিক প্রমাণ:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = Color(0xFF047857))
                Text(text = lesson.smartShortcutMethodBn, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(text = "কেন শর্টকাট কাজ করে: ${lesson.shortcutProofAndWhyItWorksBn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (lesson.writtenCode008RubricStepsBn.isNotEmpty()) {
                    Text(text = "৪. বিসিএস লিখিত (কোড ০০৮) বর্ণনামূলক ধাপ:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    lesson.writtenCode008RubricStepsBn.forEachIndexed { idx, step ->
                        Text(text = "${(idx + 1).toBengaliDigits()}. $step", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (lesson.practicalApplicationBn.isNotBlank()) {
                    Text(text = "বাস্তব প্রয়োগ: ${lesson.practicalApplicationBn}", style = MaterialTheme.typography.bodySmall)
                }

                Text(text = "৫. পরীক্ষার ফাঁদ ও সাধারণ ভুল:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                Text(text = lesson.commonMistakesWarningBn, style = MaterialTheme.typography.bodySmall)

                HorizontalDivider()

                // Math Master Core Module Integrations
                Text(text = "ম্যাথ মাস্টার ইন্টিগ্রেশন লিঙ্ক:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    lesson.linkedNctbBookId?.let { bId ->
                        lesson.linkedNctbChapterNumber?.let { chNum ->
                            OutlinedButton(onClick = { onOpenNctbBook(bId, chNum) }) {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("বোর্ড বই অধ্যায়")
                            }
                        }
                    }
                    lesson.linkedFormulaId?.let { fId ->
                        OutlinedButton(onClick = { onOpenFormula(fId) }) {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ফর্মুলা লাইব্রেরি")
                        }
                    }
                    lesson.linkedSolverTopicId?.let {
                        OutlinedButton(onClick = onOpenSolver) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("প্রবলেম সলভার")
                        }
                    }
                    lesson.linkedLabType?.let { lab ->
                        OutlinedButton(onClick = { onOpenLab(lab) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ম্যাথ ল্যাব")
                        }
                    }
                }

                HorizontalDivider()

                // Mini Knowledge Check
                Text(text = "মিনি জ্ঞান যাচাই:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Text(text = lesson.miniCheckQuestionBn, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

                lesson.miniCheckOptionsBn.forEachIndexed { idx, opt ->
                    val isCorrect = idx == lesson.miniCheckCorrectIndex
                    OutlinedCard(
                        onClick = {
                            selectedMiniOpt = idx
                            onMiniCheckAnswer(isCorrect)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = opt, modifier = Modifier.weight(1f))
                            if (selectedMiniOpt != null) {
                                if (isCorrect) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                else if (selectedMiniOpt == idx) Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCompleteGuide()
                    onDismiss()
                }
            ) {
                Text(if (isCompleted) "পাঠ সম্পন্ন হয়েছে" else "পড়া সম্পন্ন চিহ্নিত করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}

/**
 * First-Launch Profile & Goal Onboarding Dialog.
 */
@Composable
private fun BcsProfileOnboardingDialog(
    currentProfile: BcsExamProfile,
    currentStage: BcsPreparationStage,
    dailyMinutes: Int,
    dailyQuestions: Int,
    onDismiss: () -> Unit,
    onSave: (BcsExamProfile, BcsPreparationStage, Int, Int) -> Unit
) {
    var selectedProf by remember(currentProfile) { mutableStateOf(currentProfile) }
    var selectedStg by remember(currentStage) { mutableStateOf(currentStage) }
    var selectedMins by remember(dailyMinutes) { mutableIntStateOf(dailyMinutes) }
    var selectedQCount by remember(dailyQuestions) { mutableIntStateOf(dailyQuestions) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("বিসিএস লক্ষ্য ও প্রোফাইল নির্ধারণ", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("আপনার টার্গেট বিসিএস পরীক্ষা নির্বাচন করুন:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                BcsExamProfile.entries.forEach { p ->
                    OutlinedCard(
                        onClick = { selectedProf = p },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(
                            width = if (selectedProf == p) 2.dp else 1.dp,
                            color = if (selectedProf == p) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = p.titleBn, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = p.descriptionBn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Text("প্রস্তুতির বর্তমান ধাপ নির্বাচন করুন:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BcsPreparationStage.entries.forEach { stg ->
                        FilterChip(
                            selected = selectedStg == stg,
                            onClick = { selectedStg = stg },
                            label = { Text("ধাপ ${stg.stageNumber.toBengaliDigits()}: ${stg.titleBn}") }
                        )
                    }
                }

                Text("দৈনিক অধ্যয়ন সময়সীমা:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 30, 45, 60).forEach { mins ->
                        FilterChip(
                            selected = selectedMins == mins,
                            onClick = { selectedMins = mins },
                            label = { Text("${mins.toBengaliDigits()} মিনিট") }
                        )
                    }
                }

                Text("দৈনিক প্রশ্ন সমাধানের লক্ষ্য:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(10, 20, 30, 50).forEach { qCnt ->
                        FilterChip(
                            selected = selectedQCount == qCnt,
                            onClick = { selectedQCount = qCnt },
                            label = { Text("${qCnt.toBengaliDigits()}টি") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedProf, selectedStg, selectedMins, selectedQCount)
                }
            ) {
                Text("সংরক্ষণ করুন ও প্রবেশ করুন")
            }
        }
    )
}

@Composable
private fun BcsQuestionInteractiveCard(
    question: BcsMathQuestion,
    syllabusVersion: BcsSyllabusVersion,
    onWrongAnswer: (String) -> Unit = {}
) {
    var selectedOption by remember(question.id) { mutableStateOf<Int?>(null) }
    var showShortcutTab by remember(question.id) { mutableStateOf(true) }

    val isOfficial = question.sourceType == BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bcs_question_card_${question.id}"),
        shape = RoundedCornerShape(18.dp)
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isOfficial) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = question.examReferenceBn,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOfficial) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = question.sourceType.badgeBn,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "উৎস: ${question.sourceTitleBn} • ${question.verificationStatus.badgeBn} (${question.unit.unitNumberBn})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (question.provenanceEvidenceBn.isNotBlank()) {
                Text(
                    text = "প্রমাণসূত্র: ${question.provenanceEvidenceBn}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = question.questionBn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            question.optionsBn.forEachIndexed { idx, opt ->
                val isSelected = selectedOption == idx
                val isCorrect = idx == question.correctIndex
                val revealed = selectedOption != null

                OutlinedCard(
                    onClick = {
                        if (selectedOption == null) {
                            selectedOption = idx
                            if (idx != question.correctIndex) {
                                onWrongAnswer(opt)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bcs_q_${question.id}_opt_$idx"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (isSelected || (revealed && isCorrect)) 2.dp else 1.dp,
                        color = when {
                            revealed && isCorrect -> Color(0xFF2E7D32)
                            revealed && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (revealed) {
                            if (isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                            } else if (isSelected) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = showShortcutTab,
                    onClick = { showShortcutTab = true },
                    label = { Text("⚡ শর্টকাট কৌশল", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("bcs_shortcut_toggle_${question.id}")
                )
                FilterChip(
                    selected = !showShortcutTab,
                    onClick = { showShortcutTab = false },
                    label = { Text("📝 ধাপে ধাপে সমাধান", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("bcs_steps_toggle_${question.id}")
                )
            }

            if (showShortcutTab) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "শর্টকাট নাম: ${question.shortcutTitleBn}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = "ব্যবহৃত সূত্র: ${question.formulaUsedBn}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text(text = "প্রয়োগ কৌশল: ${question.shortcutTechniqueBn}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "কখন প্রয়োগ করবেন: ${question.whenToUseShortcutBn}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "মানসম্মত লিখিত সমাধান:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        question.standardStepByStepBn.forEachIndexed { i, step ->
                            Text(text = "${(i + 1).toBengaliDigits()}. $step", style = MaterialTheme.typography.bodySmall)
                        }
                        if (question.commonMistakeWarningBn.isNotBlank()) {
                            Text(text = "⚠️ সতর্কবার্তা: ${question.commonMistakeWarningBn}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
