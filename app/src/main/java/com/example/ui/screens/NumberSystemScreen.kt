package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookmarkEntity
import com.example.math.BengaliNumberFormatter
import com.example.math.DivisibilityRule
import com.example.math.GcdLcmResult
import com.example.math.NumberSystemCategory
import com.example.math.NumberSystemDifficulty
import com.example.math.NumberSystemProvider
import com.example.math.NumberSystemTopic
import com.example.math.RealNumberGuidedExplorer
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.MathSectionHeader
import com.example.ui.components.MathTopAppBar
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberSystemScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    // Tabs: 0 -> বাস্তব সংখ্যা শ্রেণিবিভাগ, 1 -> বিভাজ্যতার নিয়ম (২-১১), 2 -> গ.সা.গু ও ল.সা.গু ক্যালকুলেটর, 3 -> অনুশীলন ল্যাব
    var selectedTab by rememberManagedUiState(viewModel, "number_system:selectedTab", 0)
    val tabTitles = listOf("সংখ্যার শ্রেণিবিভাগ", "বিভাজ্যতার নিয়ম", "গ.সা.গু ও ল.সা.গু", "বিসিএস প্র্যাকটিস")

    Column(modifier = modifier.fillMaxSize()) {
        MathTopAppBar(
            title = "সংখ্যা পদ্ধতি ও বিভাজ্যতা",
            subtitle = "বাস্তব সংখ্যা, উৎপাদক, ল.সা.গু, গ.সা.গু ও BCS প্রস্তুতি",
            onBackClick = { viewModel.navigateBack() },
            actions = {
                // Shortcut to existing Prime Numbers module
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Primes) },
                    modifier = Modifier.testTag("ns_shortcut_primes_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.LooksOne,
                        contentDescription = "মৌলিক সংখ্যা মডিউল"
                    )
                }
            }
        )

        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("number_system_tab_row")
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                    },
                    modifier = Modifier.testTag("ns_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> NumberClassificationTab(viewModel = viewModel, bookmarks = bookmarks)
            1 -> DivisibilityRulesTab(viewModel = viewModel)
            2 -> GcdLcmCalculatorTab(viewModel = viewModel)
            3 -> NumberSystemPracticeHubTab(viewModel = viewModel)
        }
    }
}

/**
 * Tab 1: Real Number Classification (Natural, Whole, Integer, Rational, Irrational, Primes, Factors)
 */
@Composable
fun NumberClassificationTab(
    viewModel: MathViewModel,
    bookmarks: List<BookmarkEntity>
) {
    var testNumberInput by rememberManagedUiState(viewModel, "number_system:tab_0:testNumberInput", "")
    var classificationResult by remember(testNumberInput) {
        val n = BengaliNumberFormatter.toEnglishDigits(testNumberInput.trim()).toLongOrNull()
        mutableStateOf(if (n != null && testNumberInput.isNotBlank()) NumberSystemProvider.classifyNumber(n) else null)
    }
    var expandedCategory by rememberManagedUiState<String?>(viewModel, "number_system:tab_0:expandedCategory", null)
    var activeGuidedStepIdx by rememberManagedUiState(viewModel, "number_system:tab_0:activeGuidedStepIdx", 0)
    var currentExerciseIdx by rememberManagedUiState(viewModel, "number_system:tab_0:currentExerciseIdx", 0)
    var selectedExerciseOption by remember(currentExerciseIdx) { mutableStateOf<Int?>(null) }
    val listState = rememberManagedLazyListState(viewModel, "number_system:tab_0")
    val guidedRowState = rememberManagedLazyListState(viewModel, "number_system:tab_0:guided_row")

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("number_system_tab_0_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // P1 UPGRADE: Guided Step-by-Step Real Numbers Explorer (ℕ ⊂ W ⊂ ℤ ⊂ ℚ ⊂ ℝ & ℚ')
        item {
            val steps = RealNumberGuidedExplorer.guidedSteps
            val currentStep = steps[activeGuidedStepIdx.coerceIn(0, steps.lastIndex)]
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("real_numbers_guided_explorer_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ধাপে ধাপে বাস্তব সংখ্যার গাইডেড এক্সপ্লোরার (ℕ ⊂ W ⊂ ℤ ⊂ ℚ ⊂ ℝ)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyRow(
                        state = guidedRowState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(steps.size) { idx ->
                            val st = steps[idx]
                            FilterChip(
                                selected = activeGuidedStepIdx == idx,
                                onClick = { activeGuidedStepIdx = idx },
                                label = { Text("ধাপ ${BengaliNumberFormatter.toBn(st.stepIndex)}: ${st.symbol}", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("guided_step_chip_${st.stepIndex}")
                            )
                        }
                    }

                    Text(
                        text = "${currentStep.symbol} — ${currentStep.titleBn}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = currentStep.definitionBn,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "সেট সম্পর্ক: ${currentStep.subsetRelationshipBn}",
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Interactive Number Line Representation
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guided_number_line_box")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "সংখ্যা রেখায় অবস্থান (Number Line):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                currentStep.numberLinePoints.forEach { (_, labelBn) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = labelBn,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    currentStep.keyPropertiesBn.forEach { prop ->
                        Text(
                            text = "• $prop",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "উদাহরণ: ${currentStep.examplesBn.joinToString(", ")}  |  অন্তর্ভুক্ত নয়: ${currentStep.nonExamplesBn.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // P1 UPGRADE: Interactive Set Classification Exercise Card
        item {
            val exercises = RealNumberGuidedExplorer.classificationExercises
            val ex = exercises[currentExerciseIdx.coerceIn(0, exercises.lastIndex)]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("set_classification_exercise_card"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "শ্রেণিবিন্যাস অনুশীলন (${BengaliNumberFormatter.toBn(currentExerciseIdx + 1)}/${BengaliNumberFormatter.toBn(exercises.size)})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedButton(
                            onClick = {
                                currentExerciseIdx = (currentExerciseIdx + 1) % exercises.size
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_next_classification_ex")
                        ) {
                            Text("পরবর্তী প্রশ্ন", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Text(
                        text = ex.questionBn,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ex.optionsBn.forEachIndexed { idx, optText ->
                        FilterChip(
                            selected = selectedExerciseOption == idx,
                            onClick = { selectedExerciseOption = idx },
                            label = { Text(optText) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("classification_opt_$idx")
                        )
                    }

                    if (selectedExerciseOption != null) {
                        val isRight = selectedExerciseOption == ex.correctIndex
                        Text(
                            text = if (isRight) "সঠিক উত্তর! ${ex.stepByStepReasonBn}" else "ভুল উত্তর। সঠিক: ${ex.optionsBn[ex.correctIndex]}। ${ex.stepByStepReasonBn}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isRight) Color(0xFF15803D) else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Interactive Live Classifier Box
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interactive_classifier_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ইন্টারেক্টিভ সংখ্যা পরীক্ষক ও বিশ্লেষক",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "যেকোনো পূর্ণসংখ্যা লিখে জানুন তা কোন কোন শ্রেণির অন্তর্ভুক্ত (স্বাভাবিক, অখণ্ড, জোড়/বিজোড়, মৌলিক/যৌগিক)।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = testNumberInput,
                            onValueChange = {
                                if (it.all { ch -> ch.isDigit() || ch == '-' }) {
                                    testNumberInput = it
                                }
                            },
                            label = { Text("একটি সংখ্যা লিখুন (যেমন: ৪২, -৭, ০)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val n = BengaliNumberFormatter.toEnglishDigits(testNumberInput.trim()).toLongOrNull()
                                    if (n != null) {
                                        classificationResult = NumberSystemProvider.classifyNumber(n)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("classifier_input_field")
                        )

                        Button(
                            onClick = {
                                val n = BengaliNumberFormatter.toEnglishDigits(testNumberInput.trim()).toLongOrNull()
                                if (n != null) {
                                    classificationResult = NumberSystemProvider.classifyNumber(n)
                                }
                            },
                            modifier = Modifier.testTag("classifier_check_btn")
                        ) {
                            Text("যাচাই")
                        }
                    }

                    if (classificationResult != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = classificationResult!!,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            MathSectionHeader(
                title = "বাস্তব সংখ্যার বৃক্ষ ও সংজ্ঞাসমূহ",
                subtitle = "বিসিএস প্রিলিমিনারি ও সকল প্রতিযোগিতামূলক গণিত পরীক্ষার জন্য"
            )
        }

        // List of Real Number categories
        items(NumberSystemCategory.entries) { cat ->
            val isBookmarked = bookmarks.any { it.id == "ns_cat_${cat.id}" }
            val isExpanded = expandedCategory == cat.id

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("number_cat_card_${cat.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.titleBn,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = cat.titleEn,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.toggleBookmark(
                                    BookmarkEntity(
                                        id = "ns_cat_${cat.id}",
                                        category = "NUMBER_SYSTEM",
                                        titleBn = cat.titleBn,
                                        subtitleBn = cat.titleEn,
                                        detailBn = cat.definitionBn
                                    )
                                )
                            },
                            modifier = Modifier.testTag("bookmark_ns_cat_${cat.id}")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "বুকমার্ক",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }

                        IconButton(
                            onClick = {
                                expandedCategory = if (isExpanded) null else cat.id
                            }
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "সংকোচন" else "প্রসারণ"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = cat.definitionBn,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "উদাহরণ: ${cat.examplesBn}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cat.bcsNotesBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            // If this is Prime & Composite, offer a direct button to deep-dive into the existing Primes screen
                            if (cat == NumberSystemCategory.PRIME_COMPOSITE) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(Screen.Primes) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("open_primes_module_btn")
                                ) {
                                    Icon(Icons.Default.LooksOne, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("১-২০০ মৌলিক সংখ্যার পূর্ণাঙ্গ চার্ট ও ম্যাজিক কোড দেখুন")
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
 * Tab 2: Comprehensive Divisibility Rules (2, 3, 4, 5, 6, 8, 9, 10, 11) with Live Tester
 */
@Composable
fun DivisibilityRulesTab(
    viewModel: MathViewModel
) {
    val rules = NumberSystemProvider.divisibilityRules
    var selectedDivisor by rememberManagedUiState(viewModel, "number_system:tab_1:selectedDivisor", 2)
    var userTestNumber by rememberManagedUiState(viewModel, "number_system:tab_1:userTestNumber", "4872")
    val listState = rememberManagedLazyListState(viewModel, "number_system:tab_1")
    val divisorRowState = rememberManagedLazyListState(viewModel, "number_system:tab_1:divisor_row")

    val activeRule = rules.find { it.divisor == selectedDivisor } ?: rules.first()
    val testNumberLong = BengaliNumberFormatter.toEnglishDigits(userTestNumber.trim()).toLongOrNull() ?: activeRule.testExampleNumber
    val (isDivisible, dynamicExplanation) = NumberSystemProvider.checkDivisibility(testNumberLong, activeRule.divisor)

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("number_system_tab_1_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Selector Chips for Divisors 2, 3, 4, 5, 6, 8, 9, 10, 11
        item {
            Text(
                text = "ভাজক নির্বাচন করুন:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                state = divisorRowState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(rules) { rule ->
                    FilterChip(
                        selected = selectedDivisor == rule.divisor,
                        onClick = {
                            selectedDivisor = rule.divisor
                            userTestNumber = rule.testExampleNumber.toString()
                        },
                        label = {
                            Text("${BengaliNumberFormatter.toBn(rule.divisor)} এর নিয়ম")
                        },
                        modifier = Modifier.testTag("rule_chip_${rule.divisor}")
                    )
                }
            }
        }

        // Active Rule Card
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_divisibility_rule_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "÷ ${BengaliNumberFormatter.toBn(activeRule.divisor)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = activeRule.ruleNameBn,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = activeRule.summaryBn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = activeRule.detailedRuleBn,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeRule.bcsShortcutBn,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Live Interactive Divisibility Tester
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("divisibility_tester_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "লাইভ পরীক্ষা ও ব্যাখ্যা যাচাই",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = userTestNumber,
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() }) userTestNumber = it
                        },
                        label = { Text("যেকোনো সংখ্যা লিখুন (যেমন: $testNumberLong)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("divisibility_input_field")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Verdict Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDivisible) Color(0xFFDCFCE7) else Color(0xFFFFE4E6),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDivisible) Color(0xFF22C55E) else Color(0xFFEF4444)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDivisible) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isDivisible) Color(0xFF16A34A) else Color(0xFFDC2626),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isDivisible) "নিঃশেষে বিভাজ্য ✓" else "নিঃশেষে বিভাজ্য নয় ✗",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDivisible) Color(0xFF15803D) else Color(0xFFB91C1C)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dynamicExplanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 18.sp,
                                    color = Color.Black.copy(alpha = 0.8f)
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
 * Tab 3: Step-by-Step HCF (GCD) and LCM Explanations & Solver
 */
@Composable
fun GcdLcmCalculatorTab(
    viewModel: MathViewModel
) {
    var inputA by rememberManagedUiState(viewModel, "number_system:tab_2:inputA", "24")
    var inputB by rememberManagedUiState(viewModel, "number_system:tab_2:inputB", "36")
    var calculatedResult by remember(inputA, inputB) {
        val a = BengaliNumberFormatter.toEnglishDigits(inputA.trim()).toLongOrNull() ?: 24L
        val b = BengaliNumberFormatter.toEnglishDigits(inputB.trim()).toLongOrNull() ?: 36L
        mutableStateOf<GcdLcmResult?>(NumberSystemProvider.calculateGcdLcm(a, b))
    }
    val listState = rememberManagedLazyListState(viewModel, "number_system:tab_2")

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("number_system_tab_2_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Input Controls
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gcd_lcm_input_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "গ.সা.গু ও ল.সা.গু সমাধানক ও ধাপভিত্তিক ব্যাখ্যা",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "দুটি সংখ্যা ইনপুট দিন; সম্পূর্ণ গুণনীয়ক বিশ্লেষণ, ইউক্লিডীয় পদ্ধতি এবং ল.সা.গু এর ধাপসমূহ দেখুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = inputA,
                            onValueChange = { if (it.all { ch -> ch.isDigit() }) inputA = it },
                            label = { Text("১ম সংখ্যা (A)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gcd_input_a")
                        )
                        OutlinedTextField(
                            value = inputB,
                            onValueChange = { if (it.all { ch -> ch.isDigit() }) inputB = it },
                            label = { Text("২য় সংখ্যা (B)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gcd_input_b")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val a = BengaliNumberFormatter.toEnglishDigits(inputA.trim()).toLongOrNull() ?: 1L
                            val b = BengaliNumberFormatter.toEnglishDigits(inputB.trim()).toLongOrNull() ?: 1L
                            calculatedResult = NumberSystemProvider.calculateGcdLcm(a, b)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculate_gcd_lcm_btn")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ধাপভিত্তিক হিসাব ও ব্যাখ্যা দেখুন")
                    }
                }
            }
        }

        // Summary Badges
        calculatedResult?.let { res ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_gcd_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "গ.সা.গু (HCF / GCD)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = BengaliNumberFormatter.toBn(res.gcd),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_lcm_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ল.সা.গু (LCM)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (res.lcm <= 0L) "সীমাতিরিক্ত" else BengaliNumberFormatter.toBn(res.lcm),
                                style = if (res.lcm <= 0L) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            // GCD Steps
            item {
                MathSectionHeader(
                    title = "গরিষ্ঠ সাধারণ গুণনীয়ক (গ.সা.গু) নির্ণয়ের ধাপ",
                    subtitle = "উৎপাদক ও সাধারণ গুণনীয়ক পদ্ধতি"
                )
            }

            items(res.gcdSteps) { step ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ধাপ ${BengaliNumberFormatter.toBn(step.stepNumber)}: ${step.titleBn}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.detailBn,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // LCM Steps
            item {
                MathSectionHeader(
                    title = "লঘিষ্ঠ সাধারণ গুণিতক (ল.সা.গু) নির্ণয়ের ধাপ",
                    subtitle = "মৌলিক উৎপাদক ও সম্পর্কের সূত্র"
                )
            }

            items(res.lcmSteps) { step ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ধাপ ${BengaliNumberFormatter.toBn(step.stepNumber)}: ${step.titleBn}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.detailBn,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Cross-verification formula
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "গাণিতিক সম্পর্ক ও যাচাইকরণ",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = res.formulaCheckBn,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // Coprime & Euclidean Integer Division Analyzer Card
            item {
                val coprimeInfo = remember(res.a, res.b) {
                    NumberSystemProvider.analyzeCoprimeAndDivision(res.a, res.b)
                }
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("coprime_division_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "সহমৌলিকতা ও ইউক্লিডীয় ভাগশেষ বিশ্লেষণ",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = coprimeInfo.coprimeExplanationBn,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = coprimeInfo.divisionEquationBn,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // BdMO & BCS Upgrade: Divisor Count/Sum d(N), σ(N), φ(N) & Modular Exponentiation a^b mod m
            item {
                val divAnalysis = remember(res.a) {
                    com.example.math.SharedMathEngine.analyzeDivisorsAndTotient(res.a.coerceAtLeast(1L))
                }
                val modResult = remember(res.a, res.b) {
                    com.example.math.SharedMathEngine.modPowStepsBn(
                        base = res.a.coerceAtLeast(2L),
                        exponent = (res.b % 20L).coerceAtLeast(2L),
                        modulus = res.b.coerceAtLeast(3L)
                    )
                }
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bdmo_divisor_modular_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "অলিম্পিয়াড ও বিসিএস সংখ্যাতত্ত্ব ল্যাব (d(N), σ(N), φ(N) ও Mod)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "১ম সংখ্যা N = ${BengaliNumberFormatter.toBn(divAnalysis.number)} = ${divAnalysis.primePowerBn}\n" +
                                "• মোট ভাজক সংখ্যা d(N) = ${BengaliNumberFormatter.toBn(divAnalysis.divisorCount)}টি\n" +
                                "• ভাজকগুলোর সমষ্টি σ(N) = ${BengaliNumberFormatter.toBn(divAnalysis.divisorSum)}\n" +
                                "• অয়লার টোশেন্ট φ(N) = ${BengaliNumberFormatter.toBn(divAnalysis.eulerTotient)}",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 19.sp
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "মডুলার সূচক (Modular Remainder): ${modResult.congruenceNotationBn}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = modResult.theoremNoteBn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab 4: Practice Hub with Difficulty Selection (Easy, Medium, Hard), Curriculum Tracks, and Subtopics
 */
@Composable
fun NumberSystemPracticeHubTab(
    viewModel: MathViewModel
) {
    var selectedDifficulty by rememberManagedUiState(viewModel, "number_system:tab_3:selectedDifficulty", NumberSystemDifficulty.EASY)
    var selectedTopic by rememberManagedUiState(viewModel, "number_system:tab_3:selectedTopic", NumberSystemTopic.ALL)
    var selectedTrack by rememberManagedUiState(viewModel, "number_system:tab_3:selectedTrack", com.example.math.CurriculumTrack.ALL)
    var selectedInputFormat by rememberManagedUiState(viewModel, "number_system:tab_3:selectedInputFormat", com.example.math.QuestionInputFormat.MCQ)
    val listState = rememberManagedLazyListState(viewModel, "number_system:tab_3")
    val trackRowState = rememberManagedLazyListState(viewModel, "number_system:tab_3:track_row")
    val formatRowState = rememberManagedLazyListState(viewModel, "number_system:tab_3:format_row")
    val topicRowState = rememberManagedLazyListState(viewModel, "number_system:tab_3:topic_row")

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("number_system_tab_3_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "বাস্তব সংখ্যা ও বিভাজ্যতা অনুশীলন ল্যাব",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "বিসিএস প্রিলিমিনারি, শিক্ষক নিয়োগ ও ব্যাংক পরীক্ষার স্ট্যান্ডার্ড অনুযায়ী ৩ স্তরের প্রশ্ন অনুশীলন করুন। তাৎক্ষণিক ফলাফল ও ব্যাখ্যা সংরক্ষিত হবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Curriculum Track Selection (NCTB, BdMO Olympiad, BCS)
        item {
            Text(
                text = "পাঠ্যক্রম ও প্রতিযোগিতা ট্র্যাক:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                state = trackRowState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("ns_track_chip_row")
            ) {
                items(com.example.math.CurriculumTrack.entries) { track ->
                    FilterChip(
                        selected = selectedTrack == track,
                        onClick = {
                            selectedTrack = track
                            if (track == com.example.math.CurriculumTrack.BDMO || track == com.example.math.CurriculumTrack.BCS) {
                                selectedDifficulty = NumberSystemDifficulty.HARD
                            }
                        },
                        label = { Text(track.titleBn) },
                        modifier = Modifier.testTag("ns_track_chip_${track.id}")
                    )
                }
            }
        }

        // Difficulty Selection
        item {
            Text(
                text = "কঠিন্যের স্তর নির্বাচন করুন:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberSystemDifficulty.entries.forEach { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = { selectedDifficulty = diff },
                        label = { Text(diff.titleBn) },
                        modifier = Modifier.testTag("practice_diff_${diff.id}")
                    )
                }
            }
        }

        // Input Format Selection
        item {
            Text(
                text = "প্রশ্নের ধরন নির্বাচন করুন:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                state = formatRowState,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(com.example.math.QuestionInputFormat.entries) { format ->
                    FilterChip(
                        selected = selectedInputFormat == format,
                        onClick = { selectedInputFormat = format },
                        label = { Text(format.titleBn) },
                        modifier = Modifier.testTag("practice_format_${format.id}")
                    )
                }
            }
        }

        // Subtopic Selection
        item {
            Text(
                text = "বিষয় নির্বাচন করুন:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                state = topicRowState,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(NumberSystemTopic.entries) { topic ->
                    FilterChip(
                        selected = selectedTopic == topic,
                        onClick = { selectedTopic = topic },
                        label = { Text(topic.titleBn) },
                        modifier = Modifier.testTag("practice_topic_${topic.id}")
                    )
                }
            }
        }

        // Launch Action
        item {
            Button(
                onClick = {
                    viewModel.startNumberSystemPractice(selectedTopic, selectedDifficulty, selectedInputFormat)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_ns_practice_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "অনুশীলন শুরু করুন (${selectedDifficulty.titleBn})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // BCS Prep Strategy Tips Card
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BCS প্রিলিমিনারি গণিত টিপস",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• বিসিএস প্রিলিমিনারিতে ১-২টি প্রশ্ন সর্বদা বাস্তব সংখ্যা (মূলদ/অমূলদ) ও বিভাজ্যতা থেকে আসে।\n" +
                                "• দুটি ক্রমিক জোড় সংখ্যার গুণফল সর্বদা ৮ দ্বারা বিভাজ্য।\n" +
                                "• π অমূলদ কিন্তু ২২/৭ মূলদ সংখ্যা—এটি বারবার আসা একটি প্রিয় প্রশ্ন।\n" +
                                "• ১১ এর বিভাজ্যতা নির্ণয়ে বিজোড় ও জোড় অবস্থানের অঙ্কগুলোর সমষ্টির অন্তর মনে রাখবেন।",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
