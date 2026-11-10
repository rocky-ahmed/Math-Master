package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookmarkEntity
import com.example.math.BengaliNumberFormatter
import com.example.math.BookChapterSpec
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookPracticeMode
import com.example.math.BookSearchFilterType
import com.example.math.BookSearchResult
import com.example.ui.MathViewModel
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedScrollState
import com.example.ui.rememberManagedUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookHubScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val lessonProgress by viewModel.bookLessonProgress.collectAsStateWithLifecycle()
    val readingStates by viewModel.bookReadingStates.collectAsStateWithLifecycle()
    val mistakes by viewModel.bookMistakes.collectAsStateWithLifecycle()
    val notes by viewModel.bookNotes.collectAsStateWithLifecycle()
    val revisions by viewModel.bookSpacedRevisions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.bookSearchQuery.collectAsStateWithLifecycle()
    val searchFilter by viewModel.bookSearchFilter.collectAsStateWithLifecycle()

    val searchResults = remember(searchQuery, searchFilter) {
        if (searchQuery.isBlank() && searchFilter == BookSearchFilterType.ALL) {
            emptyList()
        } else {
            BookCurriculumRepository.searchAllBooks(
                query = searchQuery,
                filterType = searchFilter
            )
        }
    }

    val unresolvedMistakesCount = mistakes.count { !it.isResolved }
    val dueRevisionsCount = revisions.count { it.nextReviewDueAt <= System.currentTimeMillis() + 86_400_000L }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "পাঠ্যবই ও গাইড (BOOK)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "ষষ্ঠ, সপ্তম, অষ্টম এবং নবম-দশম শ্রেণির সম্পূর্ণ গণিত পাঠ্যবই",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_hub_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openBookRevision() },
                        modifier = Modifier.testTag("book_hub_revision_btn")
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = "রিভিশন ও নোটবুক")
                    }
                }
            )
        }
    ) { innerPadding ->
        val hubListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_hub:main"
        )
        val searchFilterScrollState = rememberManagedScrollState(
            viewModel = viewModel,
            stateKey = "book_hub:search_filter"
        )
        LazyColumn(
            state = hubListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_hub_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Offline Search Bar & Filter Chips across all 4 books
            item(key = "book_search_box") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setBookSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_hub_search_input"),
                            placeholder = {
                                Text("অধ্যায়, পাঠ, সূত্র, উপপাদ্য বা প্রশ্ন খুঁজুন...")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setBookSearchQuery("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "মুছুন")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(searchFilterScrollState),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BookSearchFilterType.entries.forEach { filter ->
                                FilterChip(
                                    selected = searchFilter == filter,
                                    onClick = { viewModel.setBookSearchFilter(filter) },
                                    label = { Text(filter.labelBn) },
                                    modifier = Modifier.testTag("book_search_filter_${filter.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            }

            // Show search results if active
            if (searchQuery.isNotBlank() || searchFilter != BookSearchFilterType.ALL) {
                item(key = "book_search_header") {
                    Text(
                        text = "অনুসন্ধান ফলাফল (${BengaliNumberFormatter.toBengaliDigits(searchResults.size)}টি)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(searchResults, key = { it.id }) { res ->
                    BookSearchResultCard(
                        result = res,
                        onClick = {
                            when (res.resultType) {
                                BookSearchFilterType.LESSON -> {
                                    val lessonId = res.lessonId
                                    if (lessonId != null) {
                                        viewModel.openBookLesson(res.bookId, res.chapterNumber, lessonId)
                                    } else {
                                        viewModel.openBookChapter(res.bookId, res.chapterNumber)
                                    }
                                }
                                BookSearchFilterType.QUESTION -> {
                                    viewModel.startBookPractice(
                                        bookId = res.bookId,
                                        chapterNumber = res.chapterNumber,
                                        mode = BookPracticeMode.STANDARD
                                    )
                                }
                                else -> viewModel.openBookChapter(res.bookId, res.chapterNumber)
                            }
                        },
                        onOpenPage = {
                            viewModel.openBookPdfReader(
                                bookId = res.bookId,
                                printedPage = res.printedPage,
                                chapterNumber = res.chapterNumber
                            )
                        }
                    )
                }
            }

            // 2. Spaced Revision & Mistake Notebook Summary Banner
            item(key = "book_revision_summary_banner") {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openBookRevision() }
                        .testTag("book_hub_revision_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "স্পেসড রিভিশন (১, ৩, ৭ ও ১৪ দিন) ও ভুল সংশোধনী খাতা",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "অমীমাংসিত ভুল: ${BengaliNumberFormatter.toBengaliDigits(unresolvedMistakesCount)}টি • রিভিশন তালিকা: ${BengaliNumberFormatter.toBengaliDigits(dueRevisionsCount)}টি • ব্যক্তিগত নোট: ${BengaliNumberFormatter.toBengaliDigits(notes.size)}টি",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // 3. The Four Primary NCTB Textbook Cards
            item(key = "books_section_title") {
                Text(
                    text = "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (NCTB) — ৪টি পাঠ্যবই",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(BookId.entries, key = { it.id }) { book ->
                val totalLessons = BookCurriculumRepository.getTotalLessonsCount(book).coerceAtLeast(1)
                val completedLessons = lessonProgress.count { it.bookId == book.id && it.isCompleted }
                val progressPct = ((completedLessons * 100) / totalLessons).coerceIn(0, 100)
                val readingState = readingStates.firstOrNull { it.bookId == book.id }

                BookCoverCard(
                    book = book,
                    completedLessons = completedLessons,
                    totalLessons = totalLessons,
                    progressPercent = progressPct,
                    lastReadPrintedPage = readingState?.lastReadPrintedPage,
                    lastReadChapter = readingState?.lastReadChapterNumber,
                    onOpenBook = { viewModel.openBookDetail(book) },
                    onOpenPdfReader = { viewModel.openBookPdfReader(bookId = book) },
                    onStartPractice = {
                        viewModel.startBookPractice(
                            bookId = book,
                            chapterNumber = 1,
                            mode = BookPracticeMode.STANDARD
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun BookCoverCard(
    book: BookId,
    completedLessons: Int,
    totalLessons: Int,
    progressPercent: Int,
    lastReadPrintedPage: Int?,
    lastReadChapter: Int?,
    onOpenBook: () -> Unit,
    onOpenPdfReader: () -> Unit,
    onStartPractice: () -> Unit
) {
    val gradientColors = when (book) {
        BookId.CLASS_6 -> listOf(Color(0xFF0284C7), Color(0xFF0369A1))
        BookId.CLASS_7 -> listOf(Color(0xFF0D9488), Color(0xFF0F766E))
        BookId.CLASS_8 -> listOf(Color(0xFF7C3AED), Color(0xFF5B21B6))
        BookId.CLASS_9_10 -> listOf(Color(0xFFE11D48), Color(0xFF9F1239))
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenBook() }
            .testTag("book_card_${book.id}"),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Gradient Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(gradientColors))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${book.shortLabelBn} • ${BengaliNumberFormatter.toBengaliDigits(book.totalChapters)}টি অধ্যায় • ${BengaliNumberFormatter.toBengaliDigits(book.totalPdfPages)} পৃষ্ঠা",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = book.titleBn,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = book.subtitleBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = book.titleBn,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            // Progress & Quick Actions Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "পাঠ অগ্রগতি: ${BengaliNumberFormatter.toBengaliDigits(completedLessons)}/${BengaliNumberFormatter.toBengaliDigits(totalLessons)} সম্পন্ন",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${BengaliNumberFormatter.toBengaliDigits(progressPercent)}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                if (lastReadPrintedPage != null && lastReadChapter != null) {
                    Text(
                        text = "সর্বশেষ পঠিত: অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(lastReadChapter)} • পাঠ্যবই পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(lastReadPrintedPage)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenBook,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_book_${book.id}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("অধ্যায় ও পাঠ", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onOpenPdfReader,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_pdf_book_${book.id}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("মূল বই")
                    }
                    OutlinedButton(
                        onClick = onStartPractice,
                        modifier = Modifier.testTag("btn_practice_book_${book.id}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = "অনুশীলন", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BookSearchResultCard(
    result: BookSearchResult,
    onClick: () -> Unit,
    onOpenPage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("book_search_result_${result.id}"),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = result.resultType.labelBn,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                AssistChip(
                    onClick = onOpenPage,
                    label = {
                        Text(
                            "পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(result.printedPage)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.titleBn,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = result.subtitleBn,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.snippetBn,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
    val lessonProgress by viewModel.bookLessonProgress.collectAsStateWithLifecycle()
    val readingStates by viewModel.bookReadingStates.collectAsStateWithLifecycle()

    val chapters = remember(bookId) { BookCurriculumRepository.getChapters(bookId) }
    val readingState = readingStates.firstOrNull { it.bookId == bookId.id }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = bookId.titleBn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${BengaliNumberFormatter.toBengaliDigits(chapters.size)}টি অধ্যায় • ${BengaliNumberFormatter.toBengaliDigits(bookId.totalPdfPages)} পিডিএফ পৃষ্ঠা",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_detail_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openBookPdfReader(bookId) },
                        modifier = Modifier.testTag("book_detail_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "মূল পাঠ্যবই")
                    }
                    IconButton(
                        onClick = { viewModel.openBookRevision(bookId) },
                        modifier = Modifier.testTag("book_detail_revision_btn")
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = "রিভিশন ও নোট")
                    }
                }
            )
        }
    ) { innerPadding ->
        val detailListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_detail:${bookId.id}:chapters"
        )
        LazyColumn(
            state = detailListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_detail_chapter_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quick Action Bar
            item(key = "book_detail_quick_actions") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (readingState != null) {
                                "সর্বশেষ পঠিত পৃষ্ঠা: ${BengaliNumberFormatter.toBengaliDigits(readingState.lastReadPrintedPage)} (অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(readingState.lastReadChapterNumber)})"
                            } else {
                                "অধ্যায়ভিত্তিক পাঠ, সূত্র, উদাহরণ, মূল পাঠ্যবই এবং ৬ ধরনের অনুশীলন মোড"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openBookPdfReader(bookId = bookId) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("book_detail_resume_pdf_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("পাঠ্যবই পড়ুন")
                            }
                            OutlinedButton(
                                onClick = {
                                    viewModel.startBookPractice(
                                        bookId = bookId,
                                        chapterNumber = null,
                                        mode = BookPracticeMode.MIXED_PRACTICE
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("book_detail_mixed_practice_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("মিশ্র অনুশীলন")
                            }
                        }
                    }
                }
            }

            // NCTB Official Edition & Source Fidelity Card
            item(key = "book_edition_fidelity_card") {
                val meta = bookId.editionMetadata
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_edition_metadata_card"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${meta.publisherBn} • ${meta.verificationBadgeBn}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${meta.academicYearBn} — ${meta.curriculumRegimeBn}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = meta.offlineModeNoteBn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Chapter Index Header
            item(key = "chapter_index_header") {
                Text(
                    text = "অধ্যায় সূচিপত্র (১ থেকে ${BengaliNumberFormatter.toBengaliDigits(chapters.size)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(chapters, key = { "${it.bookId.id}_ch_${it.chapterNumber}" }) { chapter ->
                val chCompletedLessons = chapter.lessons.count { lesson ->
                    lessonProgress.any { it.bookId == bookId.id && it.lessonId == lesson.id && it.isCompleted }
                }
                val chTotalLessons = chapter.lessons.size.coerceAtLeast(1)

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openBookChapter(bookId, chapter.chapterNumber) }
                        .testTag("chapter_card_${bookId.id}_${chapter.chapterNumber}"),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = chapter.titleBn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${chapter.titleEn} • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageStart)}–${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageEnd)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (chCompletedLessons == chTotalLessons) {
                                    Color(0xFF2E7D32).copy(alpha = 0.16f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = "${BengaliNumberFormatter.toBengaliDigits(chCompletedLessons)}/${BengaliNumberFormatter.toBengaliDigits(chTotalLessons)} পাঠ",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = chapter.summaryBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = { viewModel.openBookChapter(bookId, chapter.chapterNumber) },
                                label = { Text("পাঠ ও সূত্র (${BengaliNumberFormatter.toBengaliDigits(chapter.formulas.size)})") },
                                leadingIcon = {
                                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            AssistChip(
                                onClick = {
                                    viewModel.openBookPdfReader(
                                        bookId = bookId,
                                        printedPage = chapter.printedPageStart,
                                        chapterNumber = chapter.chapterNumber
                                    )
                                },
                                label = { Text("বই পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageStart)}") },
                                leadingIcon = {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            AssistChip(
                                onClick = {
                                    viewModel.startBookPractice(
                                        bookId = bookId,
                                        chapterNumber = chapter.chapterNumber,
                                        mode = BookPracticeMode.STANDARD
                                    )
                                },
                                label = { Text("অনুশীলন") },
                                leadingIcon = {
                                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookChapterOverviewScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
    val chapterNumber by viewModel.selectedBookChapterNumber.collectAsStateWithLifecycle()
    val lessonProgress by viewModel.bookLessonProgress.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsStateWithLifecycle()

    val chapter = remember(bookId, chapterNumber) {
        BookCurriculumRepository.getChapter(bookId, chapterNumber)
            ?: BookCurriculumRepository.getChapters(bookId).first()
    }

    var selectedTab by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_chapter_overview:${bookId.id}:${chapter.chapterNumber}:selectedTab",
        defaultValue = 0
    ) // 0: পাঠ ও শিখনফল, 1: সূত্র ও উদাহরণ, 2: অনুশীলন ও ল্যাব

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber)}: ${chapter.titleBn}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${bookId.shortLabelBn} • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageStart)}–${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageEnd)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("chapter_overview_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.openBookPdfReader(
                                bookId = bookId,
                                printedPage = chapter.printedPageStart,
                                chapterNumber = chapter.chapterNumber
                            )
                        },
                        modifier = Modifier.testTag("chapter_overview_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "মূল বইয়ে দেখুন")
                    }
                }
            )
        }
    ) { innerPadding ->
        val overviewListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_chapter_overview:${bookId.id}:${chapter.chapterNumber}:tab_$selectedTab"
        )
        val chapterTabScrollState = rememberManagedScrollState(
            viewModel = viewModel,
            stateKey = "book_chapter_overview:${bookId.id}:${chapter.chapterNumber}:tab_bar"
        )
        LazyColumn(
            state = overviewListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_chapter_overview_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Chapter Summary Card
            item(key = "chapter_summary_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "অধ্যায় সারসংক্ষেপ",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = chapter.summaryBn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (chapter.exerciseNamesBn.isNotEmpty()) {
                            Text(
                                text = "অনুশীলনী: ${chapter.exerciseNamesBn.joinToString(" • ")}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(com.example.ui.Screen.ProblemSolver) },
                                modifier = Modifier.weight(1f).testTag("btn_book_chapter_to_solver"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("সমস্যা সমাধানকারী", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(com.example.ui.Screen.BcsPreparation) },
                                modifier = Modifier.weight(1f).testTag("btn_book_chapter_to_bcs"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("বিসিএস সংযোগ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Cross-Module BCS & Ecosystem Bridge Card
            item(key = "chapter_cross_module_bridge") {
                val crossMappings = remember(bookId, chapter.chapterNumber) {
                    com.example.math.CrossModuleCurriculumBridge.getMappingsForBookChapter(bookId, chapter.chapterNumber)
                }
                if (crossMappings.isNotEmpty()) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chapter_cross_module_bridge_card"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "বিসিএস গাণিতিক যুক্তি ও ইকোসিস্টেম সংযোগ (Cross-Module Bridge)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            crossMappings.forEach { mapping ->
                                Text(
                                    text = "• ${mapping.bcsUnit.unitNumberBn} (${mapping.bcsMathDomain.titleBn}): ${mapping.bridgeNoteBn}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Section Selector Tabs
            item(key = "chapter_tabs") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(chapterTabScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        0 to "পাঠ ও শিখনফল (${BengaliNumberFormatter.toBengaliDigits(chapter.lessons.size)})",
                        1 to "সূত্র ও উদাহরণ (${BengaliNumberFormatter.toBengaliDigits(chapter.formulas.size + chapter.workedExamples.size)})",
                        2 to "অনুশীলন ও ল্যাব (${BengaliNumberFormatter.toBengaliDigits(BookPracticeMode.entries.size)} মোড)"
                    ).forEach { (idx, label) ->
                        FilterChip(
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            label = { Text(label, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("chapter_tab_$idx")
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    // Objectives
                    item(key = "chapter_objectives") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "এই অধ্যায় শেষে শিক্ষার্থীরা যা শিখবে (শিখনফল):",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                chapter.objectivesBn.forEach { obj ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(obj, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }

                    // Lessons List
                    items(chapter.lessons, key = { it.id }) { lesson ->
                        val isCompleted = lessonProgress.any { it.bookId == bookId.id && it.lessonId == lesson.id && it.isCompleted }
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openBookLesson(bookId, chapter.chapterNumber, lesson.id) }
                                .testTag("lesson_card_${lesson.id}"),
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "পাঠ ${lesson.code}: ${lesson.titleBn}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "পাঠ্যবই পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(lesson.printedPageStart)}–${BengaliNumberFormatter.toBengaliDigits(lesson.printedPageEnd)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = if (isCompleted) "সম্পন্ন" else "অসম্পন্ন",
                                        tint = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                                Text(
                                    text = lesson.conceptExplanationBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Button(
                                    onClick = { viewModel.openBookLesson(bookId, chapter.chapterNumber, lesson.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("বিস্তারিত পাঠ ও মিনি-চেক খুলুন")
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Formulas
                    if (chapter.formulas.isNotEmpty()) {
                        item(key = "formulas_heading") {
                            Text(
                                text = "প্রয়োজনীয় গাণিতিক সূত্র ও নিয়মাবলি",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(chapter.formulas, key = { it.id }) { formula ->
                            val bmId = "book_formula_${formula.id}"
                            val isBm = bookmarkedIds.contains(bmId)
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formula.titleBn,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.toggleBookmark(
                                                    BookmarkEntity(
                                                        id = bmId,
                                                        category = "FORMULA",
                                                        titleBn = "${bookId.shortLabelBn} • ${formula.titleBn}",
                                                        subtitleBn = formula.expressionBn,
                                                        detailBn = formula.explanationBn
                                                    )
                                                )
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isBm) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "বুকমার্ক"
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = formula.expressionBn,
                                            modifier = Modifier.padding(12.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    Text(
                                        text = formula.explanationBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    AssistChip(
                                        onClick = {
                                            viewModel.scheduleBookSpacedRevision(
                                                itemId = "rev_formula_${formula.id}",
                                                bookId = bookId,
                                                chapterNumber = chapter.chapterNumber,
                                                itemType = "FORMULA",
                                                titleBn = formula.titleBn,
                                                summaryBn = "${formula.expressionBn} (${formula.explanationBn})"
                                            )
                                        },
                                        label = { Text("১-৩-৭-১৪ দিনের রিভিশনে যুক্ত করুন") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Worked Examples
                    if (chapter.workedExamples.isNotEmpty()) {
                        item(key = "examples_heading") {
                            Text(
                                text = "পাঠ্যবইয়ের সমাধানকৃত উদাহরণ (Worked Examples)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(chapter.workedExamples, key = { it.id }) { ex ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${ex.titleBn} (পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(ex.printedPage)})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "প্রশ্ন: ${ex.problemStatementBn}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    ex.stepsBn.forEachIndexed { idx, step ->
                                        Text(
                                            text = "ধাপ ${BengaliNumberFormatter.toBengaliDigits(idx + 1)}: $step",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "নির্ণেয় উত্তর: ${ex.finalAnswerBn}",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Theorems or Constructions
                    if (chapter.theoremsOrConstructions.isNotEmpty()) {
                        item(key = "theorems_heading") {
                            Text(
                                text = "উপপাদ্য ও সম্পাদ্য (Theorems & Constructions)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(chapter.theoremsOrConstructions, key = { it.id }) { th ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${th.typeLabelBn}: ${th.titleBn} (পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(th.printedPage)})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = th.statementBn,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    th.stepsBn.forEachIndexed { idx, step ->
                                        Text(
                                            text = "${BengaliNumberFormatter.toBengaliDigits(idx + 1)}. $step",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Interactive Labs linked to this chapter
                    if (chapter.linkedLabTypes.isNotEmpty()) {
                        item(key = "chapter_labs_heading") {
                            Text(
                                text = "ইন্টারেক্টিভ লার্নিং ল্যাব (হাতে-কলমে গণিত অনুসন্ধান)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(chapter.linkedLabTypes, key = { it.id }) { lab ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.openBookInteractiveLab(
                                            labType = lab,
                                            bookId = bookId,
                                            chapterNumber = chapter.chapterNumber
                                        )
                                    }
                                    .testTag("chapter_lab_card_${lab.id}"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = lab.titleBn,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = lab.subtitleBn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Science,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // 9 Practice Modes for this Chapter
                    item(key = "practice_modes_heading") {
                        Text(
                            text = "অধ্যায়ভিত্তিক ${BengaliNumberFormatter.toBengaliDigits(BookPracticeMode.entries.size)}টি অনুশীলন ও পরীক্ষা মোড (১০ ধরনের প্রশ্ন ফরম্যাট)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(BookPracticeMode.entries, key = { it.id }) { mode ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.startBookPractice(
                                        bookId = bookId,
                                        chapterNumber = chapter.chapterNumber,
                                        mode = mode
                                    )
                                }
                                .testTag("chapter_practice_mode_${mode.id}"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mode.titleBn,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = mode.subtitleBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = {
                                        viewModel.startBookPractice(
                                            bookId = bookId,
                                            chapterNumber = chapter.chapterNumber,
                                            mode = mode
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("শুরু")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookLessonScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
    val chapterNumber by viewModel.selectedBookChapterNumber.collectAsStateWithLifecycle()
    val lessonId by viewModel.selectedBookLessonId.collectAsStateWithLifecycle()
    val lessonProgress by viewModel.bookLessonProgress.collectAsStateWithLifecycle()

    val chapter = remember(bookId, chapterNumber) {
        BookCurriculumRepository.getChapter(bookId, chapterNumber)
            ?: BookCurriculumRepository.getChapters(bookId).first()
    }
    val lesson = remember(chapter, lessonId) {
        chapter.lessons.firstOrNull { it.id == lessonId } ?: chapter.lessons.first()
    }
    val isCompleted = lessonProgress.any { it.bookId == bookId.id && it.lessonId == lesson.id && it.isCompleted }

    var selectedMiniOption by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_lesson:${bookId.id}:${chapter.chapterNumber}:${lesson.id}:selectedMiniOption",
        defaultValue = -1
    )
    var miniSubmitted by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_lesson:${bookId.id}:${chapter.chapterNumber}:${lesson.id}:miniSubmitted",
        defaultValue = false
    )
    var quickNoteText by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_lesson:${bookId.id}:${chapter.chapterNumber}:${lesson.id}:quickNoteText",
        defaultValue = ""
    )
    var noteSavedFeedback by remember(lesson.id) { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "পাঠ ${lesson.code}: ${lesson.titleBn}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${bookId.shortLabelBn} • অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber)} • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(lesson.printedPageStart)}–${BengaliNumberFormatter.toBengaliDigits(lesson.printedPageEnd)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_lesson_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.openBookPdfReader(
                                bookId = bookId,
                                printedPage = lesson.printedPageStart,
                                chapterNumber = chapter.chapterNumber
                            )
                        },
                        modifier = Modifier.testTag("book_lesson_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "মূল পৃষ্ঠা দেখুন")
                    }
                }
            )
        }
    ) { innerPadding ->
        val lessonListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_lesson:${bookId.id}:${chapter.chapterNumber}:${lesson.id}"
        )
        LazyColumn(
            state = lessonListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_lesson_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Concept Explanation Card
            item(key = "lesson_concept") {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "মূল ধারণা ও সহজ ব্যাখ্যা",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lesson.conceptExplanationBn,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "গুরুত্বপূর্ণ তথ্যসমূহ:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        lesson.keyPointsBn.forEach { pt ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("✔ ", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                Text(pt, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // 2. Step-by-Step Rules & Quick Memory Tips
            item(key = "lesson_rules_and_tips") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ধাপে ধাপে সমাধানের নিয়ম ও মনে রাখার কৌশল",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        lesson.stepByStepRulesBn.forEachIndexed { idx, rule ->
                            Text(
                                text = "${BengaliNumberFormatter.toBengaliDigits(idx + 1)}. $rule",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        lesson.quickMemoryTipsBn.forEach { tip ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "💡 কৌশল: $tip",
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // 3. Common Mistakes Warning Card
            if (lesson.commonMistakesBn.isNotEmpty()) {
                item(key = "lesson_mistakes") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "সাধারণ ভুল ও সতর্কতা",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            lesson.commonMistakesBn.forEach { mistake ->
                                Text(
                                    text = "⚠ $mistake",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // 4. Interactive Mini-Check Question
            item(key = "lesson_mini_check") {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lesson_mini_check_card"),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "তাৎক্ষণিক নিজেকে যাচাই করুন (Mini-Check)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lesson.miniCheckQuestionBn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        lesson.miniCheckOptionsBn.forEachIndexed { idx, opt ->
                            val isSelected = selectedMiniOption == idx
                            val isCorrectOpt = idx == lesson.miniCheckCorrectIndex
                            val bg = when {
                                miniSubmitted && isCorrectOpt -> Color(0xFF2E7D32).copy(alpha = 0.18f)
                                miniSubmitted && isSelected && !isCorrectOpt -> MaterialTheme.colorScheme.errorContainer
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = bg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !miniSubmitted) { selectedMiniOption = idx }
                                    .testTag("mini_check_opt_$idx")
                            ) {
                                Text(
                                    text = "${BengaliNumberFormatter.toBengaliDigits(idx + 1)}. $opt",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        if (!miniSubmitted) {
                            Button(
                                onClick = {
                                    if (selectedMiniOption >= 0) {
                                        miniSubmitted = true
                                        viewModel.recordLessonMiniCheckResult(
                                            bookId = bookId,
                                            chapterNumber = chapter.chapterNumber,
                                            lessonId = lesson.id,
                                            passed = (selectedMiniOption == lesson.miniCheckCorrectIndex)
                                        )
                                    }
                                },
                                enabled = selectedMiniOption >= 0,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mini_check_submit_btn")
                            ) {
                                Text("উত্তর যাচাই করুন")
                            }
                        } else {
                            val passed = selectedMiniOption == lesson.miniCheckCorrectIndex
                            Text(
                                text = if (passed) "✅ সঠিক উত্তর! ${lesson.miniCheckExplanationBn}"
                                else "❌ সঠিক উত্তর ছিল: ${lesson.miniCheckOptionsBn[lesson.miniCheckCorrectIndex]}। ব্যাখ্যা: ${lesson.miniCheckExplanationBn}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (passed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // 5. Quick Personal Note Card for this Lesson
            item(key = "lesson_personal_note") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "এই পাঠের ব্যক্তিগত নোট সংরক্ষণ করুন",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = quickNoteText,
                            onValueChange = {
                                quickNoteText = it
                                noteSavedFeedback = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lesson_note_input"),
                            placeholder = { Text("নিজের ভাষায় সূত্র বা শর্টকাট লিখে রাখুন...") },
                            minLines = 2
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (noteSavedFeedback) {
                                Text(
                                    text = "✔ নোট সংরক্ষিত হয়েছে!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            OutlinedButton(
                                onClick = {
                                    if (quickNoteText.isNotBlank()) {
                                        viewModel.saveBookPersonalNote(
                                            bookId = bookId,
                                            chapterNumber = chapter.chapterNumber,
                                            lessonId = lesson.id,
                                            noteTitleBn = "পাঠ ${lesson.code}: ${lesson.titleBn}",
                                            noteBodyBn = quickNoteText
                                        )
                                        quickNoteText = ""
                                        noteSavedFeedback = true
                                    }
                                },
                                modifier = Modifier.testTag("lesson_save_note_btn")
                            ) {
                                Text("নোট সেভ করুন")
                            }
                        }
                    }
                }
            }

            // 6. Lesson Completion & Practice Actions
            item(key = "lesson_bottom_actions") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.toggleBookLessonCompleted(bookId, chapter.chapterNumber, lesson.id)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lesson_complete_toggle_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isCompleted) "পাঠ সম্পন্ন হয়েছে" else "পাঠ সম্পন্ন চিহ্নিত করুন")
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.startBookPractice(
                                bookId = bookId,
                                chapterNumber = chapter.chapterNumber,
                                mode = BookPracticeMode.STANDARD
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lesson_start_practice_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("অধ্যায় অনুশীলন")
                    }
                }
            }
        }
    }
}
