package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import com.example.ui.customization.MultiplicationTableVector
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.math.BengaliNumberFormatter
import com.example.math.BcsCurriculumAndGuideRepository
import com.example.math.BcsPreparationStage
import com.example.math.toBengaliDigits
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.MathSectionHeader
import com.example.ui.components.MathTopAppBar
import com.example.ui.components.StudySessionTimerSection
import com.example.ui.customization.ComfortableModuleCard
import com.example.ui.customization.CompactModuleCard
import com.example.ui.customization.CustomLayoutType
import com.example.ui.customization.HomeModuleItem
import com.example.ui.customization.MathModuleGridCard
import com.example.ui.customization.SectionKey
import com.example.ui.customization.TableModuleCard
import com.example.ui.theme.AppThemeMode

@Composable
fun HomeScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val progressList by viewModel.progressList.collectAsStateWithLifecycle()
    val quizScores by viewModel.quizScores.collectAsStateWithLifecycle()
    val customState by viewModel.customizationState.collectAsStateWithLifecycle()
    val studyTimerState by viewModel.studySessionTimer.collectAsStateWithLifecycle()

    var showThemeMenu by remember { mutableStateOf(false) }

    val learnedTablesCount = progressList.count { it.itemId.startsWith("table_") && it.isLearned }
    val learnedPercentage = (learnedTablesCount * 100) / 40

    val homeModules = remember {
        HomeScreenDefaults.createHomeModules(
            onOpenBookHub = { viewModel.openBookHub() },
            onNavigate = { viewModel.navigateTo(it) }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        MathTopAppBar(
            title = "Math Master",
            subtitle = "গণিত শিক্ষা ও অনুশীলনের সহযোগী",
            actions = {
                // Progress Dashboard shortcut
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                    modifier = Modifier.testTag("home_dashboard_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "অগ্রগতি ড্যাশবোর্ড"
                    )
                }

                // Theme picker menu
                Box {
                    IconButton(
                        onClick = { showThemeMenu = true },
                        modifier = Modifier.testTag("theme_mode_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "থিম পরিবর্তন"
                        )
                    }
                    DropdownMenu(
                        expanded = showThemeMenu,
                        onDismissRequest = { showThemeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("সিস্টেম ডিফল্ট") },
                            onClick = {
                                viewModel.setThemeMode(AppThemeMode.SYSTEM)
                                showThemeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("লাইট মোড") },
                            onClick = {
                                viewModel.setThemeMode(AppThemeMode.LIGHT)
                                showThemeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("ডার্ক মোড") },
                            onClick = {
                                viewModel.setThemeMode(AppThemeMode.DARK)
                                showThemeMenu = false
                            }
                        )
                    }
                }

                // Customize screen shortcut
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Customize) },
                    modifier = Modifier.testTag("home_customize_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "অ্যাপ কাস্টমাইজ করুন"
                    )
                }

                // Bookmark shortcut
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Bookmarks) },
                    modifier = Modifier.testTag("home_bookmarks_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "বুকমার্ক তালিকা"
                    )
                }
            }
        )

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val screenWidthDp = configuration.screenWidthDp
        val columns = if (customState.global.layoutType == CustomLayoutType.GRID) {
            if (isLandscape || screenWidthDp >= 600) {
                maxOf(customState.global.gridColumns, if (screenWidthDp >= 840) 4 else 3)
            } else {
                customState.global.gridColumns.coerceIn(1, 4)
            }
        } else {
            1
        }
        val gridState = rememberLazyGridState()

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(customState.global.verticalGapDp.dp),
            horizontalArrangement = Arrangement.spacedBy(customState.global.horizontalGapDp.dp)
        ) {
            // Hero Welcome Banner
            item(span = { GridItemSpan(maxLineSpan) }, key = "home_hero_banner") {
                HomeHeroBanner(
                    learnedCount = learnedTablesCount,
                    totalTables = 40,
                    percentage = learnedPercentage,
                    quizCount = quizScores.size,
                    onStartPractice = { viewModel.startTutor(5) },
                    onOpenDashboard = { viewModel.navigateTo(Screen.Dashboard) }
                )
            }

            // Persistent 'Study Session' Timer Component (Toggleable on Home Screen)
            item(span = { GridItemSpan(maxLineSpan) }, key = "home_study_session_timer") {
                StudySessionTimerSection(
                    timerState = studyTimerState,
                    onToggleVisibility = { viewModel.toggleStudyTimerVisibility() },
                    onToggleRunning = { viewModel.toggleStudySessionRunning() },
                    onTick = { deltaSec -> viewModel.tickStudySessionTimer(deltaSec) },
                    onFinishSession = { viewModel.finishAndSaveStudySession() },
                    onResetSession = { viewModel.resetCurrentStudySession() },
                    onSelectGoalMinutes = { mins -> viewModel.setStudyDailyGoalMinutes(mins) }
                )
            }

            // Quick Modules Section Heading
            item(span = { GridItemSpan(maxLineSpan) }, key = "home_section_header") {
                MathSectionHeader(
                    title = "গণিত মডিউলসমূহ",
                    subtitle = "নিচে থেকে আপনার পছন্দের বিষয় নির্বাচন করুন"
                )
            }

            // 14 Mathematics Modules
            items(
                items = homeModules,
                key = { it.sectionKey.id },
                span = {
                    if (customState.global.layoutType == CustomLayoutType.GRID) {
                        GridItemSpan(1)
                    } else {
                        GridItemSpan(maxLineSpan)
                    }
                }
            ) { item ->
                val sectionConfig = customState.getSectionConfig(item.sectionKey)
                when (customState.global.layoutType) {
                    CustomLayoutType.GRID -> {
                        MathModuleGridCard(
                            item = item,
                            global = customState.global,
                            sectionConfig = sectionConfig
                        )
                    }
                    CustomLayoutType.COMPACT_LIST -> {
                        CompactModuleCard(
                            item = item,
                            global = customState.global,
                            sectionConfig = sectionConfig
                        )
                    }
                    CustomLayoutType.TABLE_LIST -> {
                        TableModuleCard(
                            item = item,
                            global = customState.global,
                            sectionConfig = sectionConfig
                        )
                    }
                    CustomLayoutType.COMFORTABLE_LIST -> {
                        ComfortableModuleCard(
                            item = item,
                            global = customState.global,
                            sectionConfig = sectionConfig
                        )
                    }
                }
            }


            // Recent Quiz Results
            if (quizScores.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }, key = "home_quiz_header") {
                    Spacer(modifier = Modifier.height(10.dp))
                    MathSectionHeader(
                        title = "সর্বশেষ কুইজ ফলাফল",
                        subtitle = "অফলাইন ডাটাবেসে সংরক্ষিত অগ্রগতি"
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }, key = "home_quiz_list") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(quizScores.take(5)) { score ->
                            ElevatedCard(
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(14.dp),
                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = 3.dp,
                                    pressedElevation = 5.dp
                                ),
                                modifier = Modifier
                                    .width(180.dp)
                                    .shadow(
                                        elevation = 3.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        ambientColor = Color.Black.copy(alpha = 0.06f),
                                        spotColor = Color.Black.copy(alpha = 0.10f)
                                    )
                                    .border(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                        RoundedCornerShape(14.dp)
                                    )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = score.topicTitleBn,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "স্কোর: ${BengaliNumberFormatter.toBn(score.score)} / ${BengaliNumberFormatter.toBn(score.totalQuestions)}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${BengaliNumberFormatter.toBn(score.percentage)}% নির্ভুল",
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

@Composable
fun HomeHeroBanner(
    learnedCount: Int,
    totalTables: Int,
    percentage: Int,
    quizCount: Int,
    onStartPractice: () -> Unit,
    onOpenDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "স্বাগতম Math Master এ!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "সহজে গণিত শিখুন, মুখস্থ করুন এবং নিজেকে যাচাই করুন।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "নামতা শিক্ষা অগ্রগতি:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${BengaliNumberFormatter.toBn(learnedCount)}/${BengaliNumberFormatter.toBn(totalTables)} সম্পন্ন (${BengaliNumberFormatter.toBn(percentage)}%)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStartPractice() }
                        .testTag("tutor_practice_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "অনুশীলন টিউটর",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenDashboard() }
                        .testTag("hero_dashboard_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "অগ্রগতি চার্ট",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Canonical default Home Screen module collection.
 * Contains exactly 14 modules in verified order, with BOOK placed at the final position (item 14).
 */
object HomeScreenDefaults {
    fun createHomeModules(
        onOpenBookHub: () -> Unit,
        onNavigate: (Screen) -> Unit
    ): List<HomeModuleItem> = listOf(
        HomeModuleItem(
            sectionKey = SectionKey.TABLES,
            titleBn = "গুণ ও নামতা\n(১ থেকে ৪০)",
            subtitleBn = "১-৪০ পর্যন্ত নামতার সহজ চার্ট ও অনুশীলন",
            badgeBn = "৪০ টি নামতা",
            defaultIcon = SectionKey.TABLES.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
            testTag = "module_tables_card",
            onClick = { onNavigate(Screen.Tables) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.PRIMES,
            titleBn = "মৌলিক সংখ্যা\n(১ থেকে ২০০)",
            subtitleBn = "৪৬টি মৌলিক সংখ্যার তালিকা ও বিশদ বিশ্লেষণ",
            badgeBn = "৪৬ টি মৌলিক",
            defaultIcon = SectionKey.PRIMES.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF0D9488), Color(0xFF0F766E)),
            testTag = "module_primes_card",
            onClick = { onNavigate(Screen.Primes) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.SQUARES,
            titleBn = "বর্গ ও ঘন\n(১ থেকে ২০)",
            subtitleBn = "বর্গ, বর্গমূল, ঘন ও ঘনমূলের মান ও চার্ট",
            badgeBn = "বর্গমূল ও ঘনমূল",
            defaultIcon = SectionKey.SQUARES.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFFD97706), Color(0xFFB45309)),
            testTag = "module_squares_card",
            onClick = { onNavigate(Screen.SquaresCubes) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.FORMULAS,
            titleBn = "বীজগণিতীয়\nসূত্রাবলী",
            subtitleBn = "বর্গ ও ঘনের সূত্র, প্রতিপাদন ও অনুসিদ্ধান্ত",
            badgeBn = "ব্যাখ্যা ও সমাধান",
            defaultIcon = SectionKey.FORMULAS.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
            testTag = "module_formulas_card",
            onClick = { onNavigate(Screen.Formulas) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.FORMULA_LIBRARY,
            titleBn = "ফর্মুলা লাইব্রেরি\n(পূর্ণাঙ্গ সংগ্রহ)",
            subtitleBn = "বীজগণিত, জ্যামিতি ও ক্যালকুলাসের সূত্র",
            badgeBn = "পূর্ণাঙ্গ সংগ্রহ",
            defaultIcon = SectionKey.FORMULA_LIBRARY.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF4F46E5), Color(0xFF4338CA)),
            testTag = "module_formula_library_card",
            onClick = { onNavigate(Screen.FormulaLibrary) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.PRACTICE_LAB,
            titleBn = "গণিত প্র্যাকটিস\nল্যাব",
            subtitleBn = "ধাপে ধাপে অনুশীলন ও স্বয়ংক্রিয় টিউটর",
            badgeBn = "ইন্টারেক্টিভ ল্যাব",
            defaultIcon = SectionKey.PRACTICE_LAB.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF0D9488), Color(0xFF0F766E)),
            testTag = "module_practice_lab_card",
            onClick = { onNavigate(Screen.PracticeLab) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.MENTAL_MATH,
            titleBn = "মেন্টাল ম্যাথ\nচ্যালেঞ্জ",
            subtitleBn = "দ্রুত হিসাব করার তীব্র মানসিক প্রতিযোগিতা",
            badgeBn = "টাইমার মোড",
            defaultIcon = SectionKey.MENTAL_MATH.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFFEA580C), Color(0xFFC2410C)),
            testTag = "module_mental_math_card",
            onClick = { onNavigate(Screen.MentalMath) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.NUMBER_SYSTEM,
            titleBn = "বাস্তব সংখ্যা ও\nবিভাজ্যতা",
            subtitleBn = "সংখ্যা পদ্ধতি ও বিভাজ্যতার সহজ নিয়ম",
            badgeBn = "৪টি পদ্ধতি",
            defaultIcon = SectionKey.NUMBER_SYSTEM.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
            testTag = "module_number_system_card",
            onClick = { onNavigate(Screen.NumberSystem) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.PROBLEM_SOLVER,
            titleBn = "সমস্যা সমাধানকারী\n(Problem Solver)",
            subtitleBn = "পাটিগণিত ও বীজগণিতের ব্যাখ্যাসহ সমাধান",
            badgeBn = "১৬টি বিষয়",
            defaultIcon = SectionKey.PROBLEM_SOLVER.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
            testTag = "module_problem_solver_card",
            onClick = { onNavigate(Screen.ProblemSolver) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.VISUAL_EXPLORER,
            titleBn = "ভিজ্যুয়াল ম্যাথ\nএক্সপ্লোরার",
            subtitleBn = "জ্যামিতি, দাঁড়িপাল্লা ও সূত্র ভিজ্যুয়ালাইজার",
            badgeBn = "১০টি বিভাগ",
            defaultIcon = SectionKey.VISUAL_EXPLORER.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF059669), Color(0xFF047857)),
            testTag = "module_visual_explorer_card",
            onClick = { onNavigate(Screen.VisualExplorer) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.QUIZZES,
            titleBn = "ইন্টারেক্টিভ কুইজ\nপরীক্ষা",
            subtitleBn = "নামতা, মৌলিক ও সূত্রের এমসিকিউ পরীক্ষা",
            badgeBn = "স্কোরবোর্ড যুক্ত",
            defaultIcon = SectionKey.QUIZZES.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFFE11D48), Color(0xFFBE123C)),
            testTag = "module_quizzes_card",
            onClick = { onNavigate(Screen.Quizzes) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.DASHBOARD,
            titleBn = "অগ্রগতি ও পরিসংখ্যান\nড্যাশবোর্ড",
            subtitleBn = "দৈনিক স্ট্রিক ও চার্টভিত্তিক অ্যানালিটিক্স",
            badgeBn = "চার্ট ও বিশ্লেষণ",
            defaultIcon = SectionKey.DASHBOARD.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
            testTag = "module_dashboard_card",
            onClick = { onNavigate(Screen.Dashboard) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.BCS_MATH,
            titleBn = "বিসিএস গাণিতিক\nযুক্তি",
            subtitleBn = "গাণিতিক যুক্তি ও মানসিক দক্ষতা প্রস্তুতি",
            badgeBn = "৫০তম বিসিএস • প্রস্তুতি",
            defaultIcon = SectionKey.BCS_MATH.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF059669), Color(0xFF047857)),
            testTag = "module_bcs_math_card",
            onClick = { onNavigate(Screen.BcsPreparation) }
        ),
        HomeModuleItem(
            sectionKey = SectionKey.BOOK,
            titleBn = "পাঠ্যবই ও গাইড\n(৬ষ্ঠ - ৯ম-১০ম)",
            subtitleBn = "৪টি বোর্ড বই, ৪৭টি অধ্যায়, পিডিএফ ও অনুশীলন",
            badgeBn = "৪টি পাঠ্যবই • অফলাইন",
            defaultIcon = SectionKey.BOOK.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF0284C7), Color(0xFF1D4ED8)),
            testTag = "module_book_card",
            onClick = onOpenBookHub
        )
    )
}

