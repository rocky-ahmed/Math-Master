package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.FormulaInteractiveEngine
import com.example.data.model.BookmarkEntity
import com.example.math.BengaliNumberFormatter
import com.example.math.CurriculumTrack
import com.example.math.FormulaDifficulty
import com.example.math.FormulaLibraryProvider
import com.example.math.FormulaSubject
import com.example.math.LibraryFormula
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.MathTopAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormulaLibraryScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val context = LocalContext.current
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val recentFormulas by viewModel.recentFormulas.collectAsStateWithLifecycle()
    val selectedLibraryFormulaId by viewModel.selectedLibraryFormulaId.collectAsStateWithLifecycle()

    var searchQuery by com.example.ui.rememberManagedUiState(viewModel, "formula_library:searchQuery", "")
    var selectedSubject by com.example.ui.rememberManagedUiState<FormulaSubject?>(viewModel, "formula_library:selectedSubject", null) // null = All
    var selectedSubCategory by com.example.ui.rememberManagedUiState<String?>(viewModel, "formula_library:selectedSubCategory", null)
    var selectedDifficulty by com.example.ui.rememberManagedUiState<FormulaDifficulty?>(viewModel, "formula_library:selectedDifficulty", null)
    var selectedTrack by com.example.ui.rememberManagedUiState(viewModel, "formula_library:selectedTrack", CurriculumTrack.ALL)
    var showOnlyBookmarked by com.example.ui.rememberManagedUiState(viewModel, "formula_library:showOnlyBookmarked", false)
    var showOnlyRecent by com.example.ui.rememberManagedUiState(viewModel, "formula_library:showOnlyRecent", false)
    var showCheatSheetDialog by remember { mutableStateOf(false) }
    // 0 = Unicode, 1 = LaTeX, 2 = Plain Text
    var notationMode by com.example.ui.rememberManagedUiState(viewModel, "formula_library:notationMode", 0)

    // Expanded formula cards set
    var expandedFormulaIds by com.example.ui.rememberManagedUiState(viewModel, "formula_library:expandedFormulaIds", setOf<String>())

    LaunchedEffect(selectedLibraryFormulaId) {
        val targetId = selectedLibraryFormulaId
        if (!targetId.isNullOrBlank()) {
            val targetFormula = FormulaLibraryProvider.getFormulaById(targetId)
            if (targetFormula != null) {
                selectedSubject = null
                selectedSubCategory = null
                selectedDifficulty = null
                selectedTrack = CurriculumTrack.ALL
                showOnlyBookmarked = false
                showOnlyRecent = false
                searchQuery = targetFormula.titleBn
                expandedFormulaIds = expandedFormulaIds + targetId
                viewModel.recordFormulaView(targetFormula)
            }
            viewModel.clearSelectedLibraryFormulaId()
        }
    }

    val availableSubCategories = remember(selectedSubject) {
        FormulaLibraryProvider.getSubCategories(selectedSubject)
    }

    val filteredFormulas = remember(
        searchQuery,
        selectedSubject,
        selectedSubCategory,
        selectedDifficulty,
        selectedTrack,
        showOnlyBookmarked,
        showOnlyRecent,
        bookmarks,
        recentFormulas
    ) {
        val bookmarkedIds = bookmarks.map { it.id }.toSet()
        val recentIds = recentFormulas.map { it.formulaId }.toSet()
        FormulaLibraryProvider.searchFormulas(
            query = searchQuery,
            subject = selectedSubject,
            subCategory = selectedSubCategory,
            difficulty = selectedDifficulty,
            onlyBookmarkedIds = if (showOnlyBookmarked) bookmarkedIds else null,
            onlyRecentIds = if (showOnlyRecent) recentIds else null,
            curriculumTrack = selectedTrack
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_formula_library")
    ) {
        // App Bar with Cheat Sheet, Recent, and Bookmark filters
        MathTopAppBar(
            title = "ফর্মুলা লাইব্রেরি",
            subtitle = "১২টি বিভাগের পূর্ণাঙ্গ গাণিতিক সূত্র ভাণ্ডার",
            onBackClick = { viewModel.navigateBack() },
            actions = {
                // Toggle recently viewed
                IconButton(
                    onClick = { showOnlyRecent = !showOnlyRecent },
                    modifier = Modifier.testTag("btn_filter_recent")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "সাম্প্রতিক পঠিত সূত্রসমূহ",
                        tint = if (showOnlyRecent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Toggle bookmarked-only view
                IconButton(
                    onClick = { showOnlyBookmarked = !showOnlyBookmarked },
                    modifier = Modifier.testTag("btn_filter_bookmarked")
                ) {
                    Icon(
                        imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "বুকমার্ক করা সূত্রসমূহ",
                        tint = if (showOnlyBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quick Exam Cheat-Sheet Sheet
                IconButton(
                    onClick = { showCheatSheetDialog = true },
                    modifier = Modifier.testTag("btn_open_cheatsheet")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "সংক্ষিপ্ত রিভিশন শিট",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Search Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("formula_search_input"),
                placeholder = {
                    Text(
                        text = "সূত্র বা বিষয়ের নাম দিয়ে খুঁজুন... (যেমন: a², বৃত্ত, sin, গ.সা.গু)",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "খুঁজুন",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.testTag("btn_clear_search")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "মুছুন")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
        }

        // Primary Subject Tabs (kept for core navigation & test compatibility: সবগুলো, বীজগণিত, জ্যামিতি, ক্যালকুলাস)
        val tabs = listOf(
            null to "সবগুলো (${BengaliNumberFormatter.toBn(FormulaLibraryProvider.allFormulas.size)})",
            FormulaSubject.ALGEBRA to "বীজগণিত (${BengaliNumberFormatter.toBn(FormulaLibraryProvider.algebraFormulas.size)})",
            FormulaSubject.GEOMETRY to "জ্যামিতি (${BengaliNumberFormatter.toBn(FormulaLibraryProvider.geometryFormulas.size)})",
            FormulaSubject.CALCULUS to "ক্যালকুলাস (${BengaliNumberFormatter.toBn(FormulaLibraryProvider.calculusFormulas.size)})"
        )
        val selectedTabIndex = tabs.indexOfFirst { it.first == selectedSubject }.coerceAtLeast(0)

        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("subject_tab_row"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, pair ->
                Tab(
                    selected = selectedSubject == pair.first,
                    onClick = {
                        selectedSubject = pair.first
                        selectedSubCategory = null
                    },
                    text = {
                        Text(
                            text = pair.second,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedSubject == pair.first) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.testTag("subject_tab_$index")
                )
            }
        }

        // Curriculum Track Filter Chips (NCTB, BdMO Olympiad, BCS)
        val trackRowState = com.example.ui.rememberManagedLazyListState(viewModel, "formula_library:track_row")
        LazyRow(
            state = trackRowState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .testTag("formula_track_chip_row"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(CurriculumTrack.values()) { track ->
                FilterChip(
                    selected = selectedTrack == track,
                    onClick = { selectedTrack = track },
                    label = { Text(track.titleBn, fontWeight = if (selectedTrack == track) FontWeight.Bold else FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier.testTag("track_chip_${track.id}")
                )
            }
        }

        // Full 12-Category Horizontal Filter Row
        val subjectRowState = com.example.ui.rememberManagedLazyListState(viewModel, "formula_library:subject_row")
        LazyRow(
            state = subjectRowState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .testTag("all_subjects_chip_row"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = selectedSubject == null,
                    onClick = {
                        selectedSubject = null
                        selectedSubCategory = null
                    },
                    label = { Text("সকল ১২ বিভাগ") },
                    modifier = Modifier.testTag("chip_subject_ALL")
                )
            }
            items(FormulaSubject.values()) { subj ->
                val count = remember(subj) { FormulaLibraryProvider.getFormulasBySubject(subj).size }
                FilterChip(
                    selected = selectedSubject == subj,
                    onClick = {
                        selectedSubject = if (selectedSubject == subj) null else subj
                        selectedSubCategory = null
                    },
                    label = {
                        Text("${subj.titleBn} (${BengaliNumberFormatter.toBn(count)})")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = subj.primaryColor.copy(alpha = 0.18f),
                        selectedLabelColor = subj.primaryColor
                    ),
                    modifier = Modifier.testTag("chip_subject_${subj.name}")
                )
            }
        }

        // Subcategory + Difficulty + Notation Mode Filter Chips Row
        val subCatRowState = com.example.ui.rememberManagedLazyListState(viewModel, "formula_library:subcat_row:${selectedSubject?.name ?: "ALL"}")
        LazyRow(
            state = subCatRowState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("subcategory_filter_row"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = selectedSubCategory == null,
                    onClick = { selectedSubCategory = null },
                    label = { Text("সব উপবিভাগ") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            items(availableSubCategories) { subCat ->
                FilterChip(
                    selected = selectedSubCategory == subCat,
                    onClick = {
                        selectedSubCategory = if (selectedSubCategory == subCat) null else subCat
                    },
                    label = { Text(subCat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }

            items(FormulaDifficulty.values()) { diff ->
                FilterChip(
                    selected = selectedDifficulty == diff,
                    onClick = {
                        selectedDifficulty = if (selectedDifficulty == diff) null else diff
                    },
                    label = { Text(diff.titleBn) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = diff.badgeColor.copy(alpha = 0.2f),
                        selectedLabelColor = diff.badgeColor
                    ),
                    modifier = Modifier.testTag("chip_formula_diff_${diff.name}")
                )
            }

            item {
                val notationLabel = when (notationMode) {
                    1 -> "LaTeX"
                    2 -> "Plain"
                    else -> "Unicode"
                }
                FilterChip(
                    selected = notationMode != 0,
                    onClick = { notationMode = (notationMode + 1) % 3 },
                    label = { Text("নোটেশন: $notationLabel") },
                    modifier = Modifier.testTag("chip_notation_mode")
                )
            }
        }

        // Active Filter Indicators / Formula Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "মোট ${BengaliNumberFormatter.toBn(filteredFormulas.size)} টি সূত্র পাওয়া গেছে",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            if (searchQuery.isNotEmpty() || selectedSubCategory != null || selectedDifficulty != null || showOnlyBookmarked || showOnlyRecent) {
                TextButton(
                    onClick = {
                        searchQuery = ""
                        selectedSubCategory = null
                        selectedDifficulty = null
                        showOnlyBookmarked = false
                        showOnlyRecent = false
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ফিল্টার মুছুন",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Formula List
        if (filteredFormulas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Functions,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Text(
                        text = when {
                            showOnlyBookmarked -> "কোনো বুকমার্ক করা সূত্র পাওয়া যায়নি"
                            showOnlyRecent -> "সাম্প্রতিক পঠিত কোনো সূত্র নেই"
                            else -> "কোনো সূত্র পাওয়া যায়নি"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (showOnlyBookmarked) "পছন্দের সূত্রে বুকমার্ক আইকনে ক্লিক করে সংরক্ষণ করুন।" else "অনুসন্ধানের শব্দ পরিবর্তন করে বা অন্য ক্যাটাগরি বেছে নিয়ে পুনরায় চেষ্টা করুন।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedSubject = null
                            selectedSubCategory = null
                            selectedDifficulty = null
                            showOnlyBookmarked = false
                            showOnlyRecent = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("সকল সূত্র দেখুন")
                    }
                }
            }
        } else {
            val formulaListState = com.example.ui.rememberManagedLazyListState(
                viewModel = viewModel,
                stateKey = "formula_library:list:${selectedSubject?.name ?: "ALL"}"
            )
            LazyColumn(
                state = formulaListState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("formula_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredFormulas, key = { it.id }) { formula ->
                    val isBookmarked = bookmarks.any { it.id == formula.id }
                    val isExpanded = expandedFormulaIds.contains(formula.id)

                    FormulaCard(
                        formula = formula,
                        notationMode = notationMode,
                        isBookmarked = isBookmarked,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            if (!isExpanded) {
                                viewModel.recordFormulaView(formula)
                            }
                            expandedFormulaIds = if (isExpanded) {
                                expandedFormulaIds - formula.id
                            } else {
                                expandedFormulaIds + formula.id
                            }
                        },
                        onSelectRelatedFormula = { related ->
                            selectedSubject = related.subject
                            selectedSubCategory = null
                            searchQuery = related.titleBn
                            expandedFormulaIds = expandedFormulaIds + related.id
                            viewModel.recordFormulaView(related)
                        },
                        onToggleBookmark = {
                            val entity = BookmarkEntity(
                                id = formula.id,
                                category = "FORMULA_LIBRARY",
                                titleBn = formula.titleBn,
                                subtitleBn = "${formula.subject.titleBn} • ${formula.subCategory}",
                                detailBn = formula.formulaExpression
                            )
                            viewModel.toggleBookmark(entity)
                            val message = if (isBookmarked) "বুকমার্ক থেকে সরানো হয়েছে" else "বুকমার্কে যোগ করা হয়েছে"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                        onCopyFormula = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val exprToCopy = when (notationMode) {
                                1 -> formula.latexExpression.ifBlank { formula.formulaExpression }
                                2 -> formula.plainTextExpression.ifBlank { formula.formulaExpression }
                                else -> formula.formulaExpression
                            }
                            val clip = ClipData.newPlainText(formula.titleBn, exprToCopy)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "সূত্র ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        onPracticeInLab = {
                            val mappedCat = when (formula.subject) {
                                FormulaSubject.ALGEBRA, FormulaSubject.SEQUENCES_SERIES, FormulaSubject.CALCULUS -> PracticeCategory.BASIC_ALGEBRA
                                FormulaSubject.GEOMETRY, FormulaSubject.MENSURATION, FormulaSubject.TRIGONOMETRY, FormulaSubject.COORDINATE_GEOMETRY -> PracticeCategory.GEOMETRY
                                FormulaSubject.NUMBER_THEORY -> PracticeCategory.NUMBER_THEORY
                                FormulaSubject.COMMERCIAL_MATH -> PracticeCategory.PERCENTAGES
                                else -> PracticeCategory.ARITHMETIC
                            }
                            viewModel.recordFormulaMasteryInteraction(formula.id, formula.subject.id, isPracticed = true)
                            viewModel.startPracticeSession(mappedCat, PracticeDifficulty.MEDIUM)
                        },
                        onOpenProblemSolver = {
                            viewModel.navigateTo(Screen.ProblemSolver)
                        }
                    )
                }
            }
        }
    }

    // Quick Exam Cheat-Sheet Dialog
    if (showCheatSheetDialog) {
        QuickCheatSheetDialog(
            formulas = filteredFormulas,
            onDismiss = { showCheatSheetDialog = false },
            onCopyFormula = { formulaText ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Math Formula", formulaText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "সূত্র কপি হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FormulaCard(
    formula: LibraryFormula,
    notationMode: Int,
    isBookmarked: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectRelatedFormula: (LibraryFormula) -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyFormula: () -> Unit,
    onPracticeInLab: () -> Unit = {},
    onOpenProblemSolver: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val displayedExpression = when (notationMode) {
        1 -> formula.latexExpression.ifBlank { formula.formulaExpression }
        2 -> formula.plainTextExpression.ifBlank { formula.formulaExpression }
        else -> formula.formulaExpression
    }
    val relatedFormulas = remember(formula.id) {
        FormulaLibraryProvider.getRelatedFormulas(formula.id)
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring())
            .testTag("formula_card_${formula.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Subject Badge + Subcategory Pill + Difficulty Badge + Bookmark Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Subject Chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = formula.subject.primaryColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(formula.subject.primaryColor, CircleShape)
                            )
                            Text(
                                text = formula.subject.titleBn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = formula.subject.primaryColor
                                )
                            )
                        }
                    }

                    // Sub-category Chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = formula.subCategory,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Difficulty Chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = formula.difficultyLevel.badgeColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = formula.difficultyLevel.titleBn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = formula.difficultyLevel.badgeColor
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Bookmark Icon Button
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_bookmark_${formula.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "বুকমার্ক সরান" else "বুকমার্ক করুন",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Formula Titles
            Text(
                text = formula.titleBn,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formula.titleEn,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Formula Math Display Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayedExpression,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onCopyFormula,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_copy_${formula.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "সূত্র কপি করুন",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Concise Concept Summary
            Text(
                text = formula.explanationBn,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expand / Collapse Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "সংক্ষিপ্ত করুন" else "প্রতীক পরিচিতি, শর্ত, প্রমাণ ও সমাধান দেখুন",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expanded Student Reference Details
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Domain conditions / restrictions
                    if (formula.conditionsBn.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "প্রযোজ্য শর্ত: ${formula.conditionsBn}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Variables Guide
                    if (formula.variables.isNotEmpty()) {
                        Text(
                            text = "প্রতীক পরিচিতি:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            formula.variables.forEach { (symbol, meaning) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$symbol: ",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                        Text(
                                            text = meaning,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Derivation Steps
                    if (formula.derivationStepsBn.isNotEmpty()) {
                        Text(
                            text = "সূত্রের উৎপত্তি ও ধাপসমূহ:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        formula.derivationStepsBn.forEachIndexed { idx, step ->
                            Text(
                                text = "${BengaliNumberFormatter.toBn(idx + 1)}. $step",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Worked Out Example
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "বাস্তব উদাহরণ ও সমাধান:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = formula.exampleProblemBn,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formula.exampleSolutionBn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Exam Tips Callout
                    if (formula.tipsBn.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7).copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "পরীক্ষার টিপস",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "পরীক্ষার টিপস ও সতর্কতা",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = formula.tipsBn,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }
                        }
                    }

                    // Related Formula Suggestions
                    if (relatedFormulas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "সম্পর্কিত অন্যান্য সূত্র:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            relatedFormulas.forEach { rel ->
                                FilterChip(
                                    selected = false,
                                    onClick = { onSelectRelatedFormula(rel) },
                                    label = {
                                        Text(
                                            text = rel.titleBn,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.testTag("related_formula_${rel.id}")
                                )
                            }
                        }
                    }

                    // P1 UPGRADE: Interactive Variable Calculator & Formula Mini-Quiz
                    Spacer(modifier = Modifier.height(12.dp))
                    FormulaInteractiveToolsSection(
                        formula = formula,
                        onPracticeInLab = onPracticeInLab,
                        onOpenProblemSolver = onOpenProblemSolver
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FormulaInteractiveToolsSection(
    formula: LibraryFormula,
    onPracticeInLab: () -> Unit = {},
    onOpenProblemSolver: () -> Unit = {}
) {
    val specs = remember(formula.id) { FormulaInteractiveEngine.getVariableSpecsForFormula(formula) }
    val miniQuizzes = remember(formula.id) { FormulaInteractiveEngine.generateMiniQuizForFormula(formula) }

    var showCalculator by remember { mutableStateOf(false) }
    var showMiniQuiz by remember { mutableStateOf(false) }

    var inputValues by remember(formula.id) {
        mutableStateOf(specs.associate { it.symbol to it.defaultValue })
    }
    var calcResult by remember(formula.id, inputValues) {
        mutableStateOf(FormulaInteractiveEngine.evaluateFormula(formula, inputValues))
    }

    var quizIndex by remember(formula.id) { mutableIntStateOf(0) }
    var selectedQuizOpt by remember(formula.id, quizIndex) { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = showCalculator,
                onClick = { showCalculator = !showCalculator },
                label = { Text("চলক ক্যালকুলেটর", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("btn_formula_calc_${formula.id}")
            )
            FilterChip(
                selected = showMiniQuiz,
                onClick = { showMiniQuiz = !showMiniQuiz },
                label = { Text("মিনি-কুইজ অনুশীলন", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("btn_formula_quiz_${formula.id}")
            )
            FilterChip(
                selected = false,
                onClick = onPracticeInLab,
                label = { Text("প্র্যাকটিস ল্যাবে অনুশীলন") },
                modifier = Modifier.testTag("btn_formula_to_practice_${formula.id}")
            )
            FilterChip(
                selected = false,
                onClick = onOpenProblemSolver,
                label = { Text("সমস্যা সমাধানকারী") },
                modifier = Modifier.testTag("btn_formula_to_solver_${formula.id}")
            )
        }

        if (showCalculator) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("formula_calculator_panel_${formula.id}")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "চলক ক্যালকুলেটর: ${formula.formulaExpression}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    specs.forEach { spec ->
                        OutlinedTextField(
                            value = inputValues[spec.symbol] ?: "",
                            onValueChange = { newVal ->
                                val nextMap = inputValues.toMutableMap()
                                nextMap[spec.symbol] = newVal
                                inputValues = nextMap
                                calcResult = FormulaInteractiveEngine.evaluateFormula(formula, nextMap)
                            },
                            label = { Text(spec.labelBn) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("formula_var_input_${formula.id}_${spec.symbol}")
                        )
                    }

                    if (!calcResult.isSuccess) {
                        Text(
                            text = calcResult.errorMessageBn ?: "ত্রুটি",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        calcResult.substitutionStepsBn.forEach { step ->
                            Text(
                                text = "• $step",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "চূড়ান্ত ফলাফল: ${calcResult.finalAnswerBn}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("formula_calc_result_${formula.id}")
                        )
                    }
                }
            }
        }

        if (showMiniQuiz && miniQuizzes.isNotEmpty()) {
            val currentMq = miniQuizzes[quizIndex.coerceIn(0, miniQuizzes.lastIndex)]
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("formula_miniquiz_panel_${formula.id}")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "মিনি-কুইজ (${quizIndex + 1}/${miniQuizzes.size}): ${currentMq.questionBn}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    currentMq.optionsBn.forEachIndexed { idx, opt ->
                        val isChosen = selectedQuizOpt == idx
                        FilterChip(
                            selected = isChosen,
                            onClick = { selectedQuizOpt = idx },
                            label = { Text(opt) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("formula_mq_opt_${formula.id}_$idx")
                        )
                    }
                    if (selectedQuizOpt != null) {
                        val isRight = selectedQuizOpt == currentMq.correctIndex
                        Text(
                            text = if (isRight) "সঠিক উত্তর! ${currentMq.explanationBn}" else "ভুল উত্তর। সঠিক: ${currentMq.optionsBn[currentMq.correctIndex]}। ${currentMq.explanationBn}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isRight) Color(0xFF15803D) else MaterialTheme.colorScheme.error
                        )
                        if (miniQuizzes.size > 1) {
                            TextButton(
                                onClick = {
                                    quizIndex = (quizIndex + 1) % miniQuizzes.size
                                    selectedQuizOpt = null
                                }
                            ) {
                                Text("পরবর্তী মিনি-কুইজ প্রশ্ন")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickCheatSheetDialog(
    formulas: List<LibraryFormula>,
    onDismiss: () -> Unit,
    onCopyFormula: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = "সংক্ষিপ্ত রিভিশন শিট",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "পরীক্ষার আগের দ্রুত রিভিশন তালিকা",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(formulas) { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
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
                                    text = item.titleBn,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.formulaExpression,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            IconButton(
                                onClick = { onCopyFormula(item.formulaExpression) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "কপি",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}
