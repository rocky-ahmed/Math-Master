package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.math.BcsMathQuestion
import com.example.math.BcsPreparationRepository
import com.example.math.BcsQuestionSourceType
import com.example.math.BcsSyllabusUnit
import com.example.math.BcsSyllabusVersion
import com.example.math.toBengaliDigits
import com.example.ui.MathViewModel

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

    val weakUnitAnalysis = remember(topicAnalytics, selectedSyllabusVersion) {
        BcsPreparationRepository.analyzeBcsWeakUnits(topicAnalytics, selectedSyllabusVersion)
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = অফিশিয়াল সিলেবাস ও দুর্বল অধ্যায়, 1 = বিগত বিসিএস প্রশ্ন ও শর্টকাট ব্যাংক, 2 = বিসিএস প্র্যাকটিস ও মক টেস্ট
    var selectedUnitFilter by remember { mutableStateOf<BcsSyllabusUnit?>(null) }
    var selectedSourceFilter by remember { mutableStateOf<BcsQuestionSourceType?>(null) }

    // Mock Test Configuration (defaults to 20 questions / 20 minutes for 50th BCS 20-mark syllabus)
    var mockQuestionCount by remember(selectedSyllabusVersion) {
        mutableIntStateOf(selectedSyllabusVersion.totalMarks)
    }
    var mockDurationMinutes by remember(selectedSyllabusVersion) {
        mutableIntStateOf(selectedSyllabusVersion.totalMarks)
    }
    var mockNegativeMark by remember { mutableStateOf(0.0) }
    var mockTargetUnit by remember { mutableStateOf<BcsSyllabusUnit?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("বিসিএস গাণিতিক যুক্তি প্রস্তুতি কেন্দ্র", fontWeight = FontWeight.Bold)
                        Text(
                            if (selectedSyllabusVersion == BcsSyllabusVersion.BCS_50TH_CURRENT) {
                                "৫০তম বিসিএস পিএসসি সিলেবাস (২০ নম্বর • ৯ ডিসেম্বর ২০২৫), যাচাইকৃত প্রশ্ন ও শর্টকাট"
                            } else {
                                "প্রাক্তন বিসিএস সিলেবাস (১৫ নম্বর • আর্কাইভড), যাচাইকৃত প্রশ্ন ও শর্টকাট"
                            },
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.testTag("bcs_tab_syllabus"),
                    text = { Text("পিএসসি সিলেবাস", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.testTag("bcs_tab_questions"),
                    text = { Text("প্রশ্ন ও শর্টকাট", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.testTag("bcs_tab_mock"),
                    text = { Text("মক টেস্ট", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedSyllabusVersion == BcsSyllabusVersion.BCS_50TH_CURRENT,
                                        onClick = {
                                            viewModel.setBcsSyllabusVersion(BcsSyllabusVersion.BCS_50TH_CURRENT)
                                        },
                                        label = { Text("৫০তম বিসিএস সিলেবাস (২০ নম্বর • ডিফল্ট)") },
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
                                        text = if (selectedSyllabusVersion == BcsSyllabusVersion.BCS_50TH_CURRENT) {
                                            "প্রকাশের তারিখ: ৯ ডিসেম্বর ২০২৫ (BPSC)। পূর্ণমান: ২০ নম্বর (৫টি ইউনিটে প্রতিটিতে ৪ নম্বর করে বণ্টিত)। নিচে আপনার পারফরম্যান্স অনুযায়ী ইউনিটভিত্তিক প্রস্তুতির অবস্থা দেখানো হয়েছে।"
                                        } else {
                                            "আর্কাইভড ৩৫তম–৪৯তম বিসিএস প্রিলিমিনারি সিলেবাস। পূর্ণমান: ১৫ নম্বর (৫টি ইউনিটে প্রতিটিতে ৩ নম্বর করে বণ্টিত)। বর্তমান ৫০তম বিসিএস প্রস্তুতির জন্য ২০ নম্বরের ডিফল্ট সিলেবাস ব্যবহার করুন।"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "অফিশিয়াল সূত্র: ${selectedSyllabusVersion.authorityBn} • ${selectedSyllabusVersion.officialSourceUrl}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        items(weakUnitAnalysis, key = { it.unit.id }) { analysis ->
                            val unit = analysis.unit
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
                                            color = if (analysis.isWeak) {
                                                MaterialTheme.colorScheme.errorContainer
                                            } else {
                                                Color(0xFF2E7D32).copy(alpha = 0.14f)
                                            }
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
                                        text = "অফিশিয়াল সিলেবাস বিষয়বস্তু: ${unit.officialTopicsBn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "পরামর্শ: ${analysis.recommendationBn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                selectedUnitFilter = unit
                                                selectedTab = 1
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_bcs_unit_questions_${unit.id}")
                                        ) {
                                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
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
                                }
                            }
                        }
                    }
                }

                1 -> {
                    val questions = remember(selectedUnitFilter, selectedSourceFilter) {
                        BcsPreparationRepository.getQuestionsByUnit(selectedUnitFilter, selectedSourceFilter)
                    }
                    val verifiedCount = BcsPreparationRepository.verifiedPreviousBcsQuestions.size
                    val practiceCount = BcsPreparationRepository.syllabusAlignedPracticeQuestions.size

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("অধ্যায় ফিল্টার:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
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

                                Text("প্রশ্নের উৎস ফিল্টার (Provenance Verified):", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
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
                                        label = { Text("বিগত বিসিএস প্রশ্ন (${verifiedCount.toBengaliDigits()})") },
                                        modifier = Modifier.testTag("bcs_filter_source_official")
                                    )
                                    FilterChip(
                                        selected = selectedSourceFilter == BcsQuestionSourceType.SYLLABUS_PRACTICE,
                                        onClick = { selectedSourceFilter = BcsQuestionSourceType.SYLLABUS_PRACTICE },
                                        label = { Text("সিলেবাস অনুশীলন মডেল (${practiceCount.toBengaliDigits()})") },
                                        modifier = Modifier.testTag("bcs_filter_source_practice")
                                    )
                                }
                            }
                        }

                        items(questions, key = { it.id }) { q ->
                            BcsQuestionInteractiveCard(
                                question = q,
                                syllabusVersion = selectedSyllabusVersion
                            )
                        }
                    }
                }

                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
                                            text = "বিসিএস গাণিতিক যুক্তি মক টেস্ট কনফিগারেশন",
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
                                                text = if (selectedSyllabusVersion == BcsSyllabusVersion.BCS_50TH_CURRENT) {
                                                    "৫০তম বিসিএস প্রিলিমিনারি মানদণ্ড: মোট ২০টি প্রশ্ন • ২০ নম্বর (প্রতি প্রশ্নে ১ নম্বর, ৫টি ইউনিটে ৪টি করে প্রশ্ন)। সময়সীমা অ্যাপ অনুশীলন প্রিসেট অনুযায়ী পরিবর্তনযোগ্য।"
                                                } else {
                                                    "প্রাক্তন (আর্কাইভড) সিলেবাস মানদণ্ড: মোট ১৫টি প্রশ্ন • ১৫ নম্বর (প্রতি প্রশ্নে ১ নম্বর, ৫টি ইউনিটে ৩টি করে প্রশ্ন)।"
                                                },
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    Text("সিলেবাস সংস্করণ:", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
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

                                    Text("অধ্যায় নির্বাচন:", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChip(
                                            selected = mockTargetUnit == null,
                                            onClick = { mockTargetUnit = null },
                                            label = { Text("পূর্ণাঙ্গ সিলেবাস (সকল অধ্যায়)") }
                                        )
                                        BcsSyllabusUnit.values().forEach { u ->
                                            FilterChip(
                                                selected = mockTargetUnit == u,
                                                onClick = { mockTargetUnit = u },
                                                label = { Text(u.titleBn) }
                                            )
                                        }
                                    }

                                    Text("প্রশ্ন সংখ্যা (পূর্ণ মক ডিফল্ট: ${selectedSyllabusVersion.totalMarks.toBengaliDigits()}টি):", fontWeight = FontWeight.SemiBold)
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

                                    Text("অনুশীলন সময়সীমা (মিনিট):", fontWeight = FontWeight.SemiBold)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(10, 15, 20, 25).forEach { mins ->
                                            FilterChip(
                                                selected = mockDurationMinutes == mins,
                                                onClick = { mockDurationMinutes = mins },
                                                label = { Text("${mins.toBengaliDigits()} মিনিট") }
                                            )
                                        }
                                    }

                                    Text("নেগেটিভ মার্কিং (কাস্টম অনুশীলন ও ঐতিহাসিক রেফারেন্স):", fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
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
                                    val selectedNegOption = BcsPreparationRepository.negativeMarkingOptions.firstOrNull {
                                        it.penaltyPerWrong == mockNegativeMark
                                    }
                                    if (selectedNegOption != null) {
                                        Text(
                                            text = selectedNegOption.category.descriptionBn,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
                                            text = "বিসিএস মক টেস্ট শুরু করুন (${mockQuestionCount.toBengaliDigits()} নম্বর)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
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
private fun BcsQuestionInteractiveCard(
    question: BcsMathQuestion,
    syllabusVersion: BcsSyllabusVersion
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
                text = "উৎস ও যাচাইকরণ: ${question.sourceTitleBn} • ${question.verificationStatus.badgeBn} (${question.unit.unitNumberBn}: ${question.unit.allocatedMarksBn(syllabusVersion)})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (question.provenanceEvidenceBn.isNotBlank()) {
                Text(
                    text = "প্রমাণসূত্র (ID: ${question.id}): ${question.provenanceEvidenceBn}",
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
                    onClick = { selectedOption = idx },
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
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = question.shortcutTitleBn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "সূত্র: ${question.formulaUsedBn}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = question.shortcutTechniqueBn,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "কখন প্রযোজ্য: ${question.whenToUseShortcutBn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        question.standardStepByStepBn.forEachIndexed { i, step ->
                            Text(
                                text = "ধাপ ${(i + 1).toBengaliDigits()}: $step",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সতর্কতা: ${question.commonMistakeWarningBn}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
