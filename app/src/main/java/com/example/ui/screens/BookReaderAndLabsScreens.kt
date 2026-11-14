package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.math.BengaliNumberFormatter
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookInteractiveLabType
import com.example.ui.MathViewModel
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedScrollState
import com.example.ui.rememberManagedUiState
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal object BookPdfRenderCache {
    val renderMutex = Mutex()
    val extractionLock = Any()
    private val bitmapCache = object : LruCache<String, Bitmap>(12) {}

    @Synchronized
    fun get(key: String): Bitmap? = bitmapCache.get(key)?.takeIf { !it.isRecycled }

    @Synchronized
    fun put(key: String, bitmap: Bitmap) {
        bitmapCache.put(key, bitmap)
    }

    @Synchronized
    fun evictAll() {
        bitmapCache.evictAll()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPdfReaderScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
    val printedPage by viewModel.selectedBookPrintedPage.collectAsStateWithLifecycle()
    val readingStates by viewModel.bookReadingStates.collectAsStateWithLifecycle()

    val currentReadingState = readingStates.firstOrNull { it.bookId == bookId.id }
    val bookmarkedPages = remember(currentReadingState) {
        currentReadingState?.bookmarkedPagesCsv
            ?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() }
            ?.toSet()
            ?: emptySet()
    }
    val isPageBookmarked = bookmarkedPages.contains(printedPage)

    var showPdfCanvasMode by remember(currentReadingState?.preferredViewMode) {
        mutableStateOf(currentReadingState?.preferredViewMode == "PDF_BITMAP")
    }
    var zoomScale by remember(currentReadingState?.lastZoomScale) {
        mutableFloatStateOf(currentReadingState?.lastZoomScale ?: 1.0f)
    }
    var pageJumpInput by remember(printedPage) {
        mutableStateOf(BengaliNumberFormatter.toBengaliDigits(printedPage))
    }
    var renderedBitmap by remember(bookId, printedPage) { mutableStateOf<Bitmap?>(null) }
    var isPdfLoading by remember(bookId, printedPage) { mutableStateOf(false) }
    var pdfErrorMessageBn by remember(bookId, printedPage) { mutableStateOf<String?>(null) }
    var pdfRetryToken by remember { mutableIntStateOf(0) }

    val structuredContent = remember(bookId, printedPage) {
        BookCurriculumRepository.getStructuredPageContent(bookId, printedPage)
    }
    val chapters = remember(bookId) { BookCurriculumRepository.getChapters(bookId) }

    LaunchedEffect(bookId, printedPage, showPdfCanvasMode, zoomScale, pdfRetryToken) {
        if (!showPdfCanvasMode) {
            isPdfLoading = false
            pdfErrorMessageBn = null
            return@LaunchedEffect
        }
        val scale = zoomScale.coerceIn(1.0f, 2.5f)
        val scaleBucket = (scale * 10f).roundToInt()
        val cacheKey = "${bookId.id}_p${printedPage}_z$scaleBucket"
        if (pdfRetryToken == 0) {
            BookPdfRenderCache.get(cacheKey)?.let { cached ->
                renderedBitmap = cached
                isPdfLoading = false
                pdfErrorMessageBn = null
                return@LaunchedEffect
            }
        }
        isPdfLoading = true
        pdfErrorMessageBn = null
        if (scale != 1.0f) {
            delay(80L)
        }
        val renderResult: Result<Bitmap> = withContext(Dispatchers.IO) {
            BookPdfRenderCache.renderMutex.withLock {
                if (pdfRetryToken == 0) {
                    BookPdfRenderCache.get(cacheKey)?.let { cached ->
                        return@withLock Result.success(cached)
                    }
                }
                renderPdfPageWithAutoRecovery(
                    context = context,
                    bookId = bookId,
                    printedPage = printedPage,
                    scale = scale,
                    cacheKey = cacheKey,
                    forceReExtract = pdfRetryToken > 0
                )
            }
        }
        renderResult.fold(
            onSuccess = { bmp ->
                renderedBitmap = bmp
                isPdfLoading = false
                pdfErrorMessageBn = null
            },
            onFailure = {
                renderedBitmap = null
                isPdfLoading = false
                pdfErrorMessageBn = "পিডিএফ পৃষ্ঠা রেন্ডার করা যায়নি। নিচের বাটনে ট্যাপ করে পুনরায় চেষ্টা করুন অথবা ডিজিটাল পাঠ্যবই ভিউ ব্যবহার করুন।"
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${bookId.titleBn} — পাঠ্যবই রিডার",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "পাঠ্যবই পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(printedPage)}/${BengaliNumberFormatter.toBengaliDigits(bookId.maxPrintedPage)} (PDF পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(structuredContent.pdfPage)})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_pdf_reader_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleBookReaderPageBookmark(bookId, printedPage) },
                        modifier = Modifier.testTag("book_pdf_bookmark_btn")
                    ) {
                        Icon(
                            imageVector = if (isPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "পৃষ্ঠা বুকমার্ক করুন",
                            tint = if (isPageBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val pdfReaderListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_pdf_reader:${bookId.id}:$printedPage"
        )
        val chapterStripScrollState = rememberManagedScrollState(
            viewModel = viewModel,
            stateKey = "book_pdf_reader:${bookId.id}:chapter_strip"
        )
        LazyColumn(
            state = pdfReaderListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_pdf_reader_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Page Navigation & Mode Switch Controls
            item(key = "reader_controls") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (printedPage > 1) {
                                        viewModel.updateBookReaderPage(bookId, printedPage - 1, zoomScale = zoomScale)
                                    }
                                },
                                enabled = printedPage > 1,
                                modifier = Modifier.testTag("pdf_prev_page_btn")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পূর্ববর্তী")
                            }

                            OutlinedTextField(
                                value = pageJumpInput,
                                onValueChange = { pageJumpInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pdf_page_jump_input"),
                                label = { Text("পৃষ্ঠা নং (১–${BengaliNumberFormatter.toBengaliDigits(bookId.maxPrintedPage)})") },
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    val target = BengaliNumberFormatter.toEnglishDigits(pageJumpInput)
                                        .trim()
                                        .toIntOrNull()
                                    if (target != null) {
                                        viewModel.updateBookReaderPage(
                                            bookId = bookId,
                                            printedPage = target.coerceIn(1, bookId.maxPrintedPage),
                                            zoomScale = zoomScale
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("pdf_page_jump_go_btn")
                            ) {
                                Text("যান")
                            }

                            OutlinedButton(
                                onClick = {
                                    if (printedPage < bookId.maxPrintedPage) {
                                        viewModel.updateBookReaderPage(bookId, printedPage + 1, zoomScale = zoomScale)
                                    }
                                },
                                enabled = printedPage < bookId.maxPrintedPage,
                                modifier = Modifier.testTag("pdf_next_page_btn")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "পরবর্তী")
                            }
                        }

                        // Chapter Quick Jump Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(chapterStripScrollState),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            chapters.forEach { ch ->
                                FilterChip(
                                    selected = structuredContent.chapterNumber == ch.chapterNumber,
                                    onClick = {
                                        viewModel.updateBookReaderPage(
                                            bookId = bookId,
                                            printedPage = ch.printedPageStart,
                                            chapterNumber = ch.chapterNumber,
                                            zoomScale = zoomScale
                                        )
                                    },
                                    label = {
                                        Text("অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(ch.chapterNumber)} (পৃ. ${BengaliNumberFormatter.toBengaliDigits(ch.printedPageStart)})")
                                    }
                                )
                            }
                        }

                        // View Mode Switch: Structured High-Contrast vs Original PDF Canvas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = !showPdfCanvasMode,
                                onClick = {
                                    showPdfCanvasMode = false
                                    viewModel.updateBookReaderPreferences(bookId, "STRUCTURED", zoomScale)
                                },
                                label = { Text("স্পষ্ট ডিজিটাল পাঠ্যবই ভিউ") },
                                modifier = Modifier.testTag("reader_mode_structured_btn")
                            )
                            FilterChip(
                                selected = showPdfCanvasMode,
                                onClick = {
                                    showPdfCanvasMode = true
                                    viewModel.updateBookReaderPreferences(bookId, "PDF_BITMAP", zoomScale)
                                },
                                label = { Text("মূল পিডিএফ পৃষ্ঠা (PDF)") },
                                leadingIcon = {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.testTag("reader_mode_pdf_btn")
                            )
                        }
                    }
                }
            }

            // 2. If PDF Canvas Mode is enabled, show Zoom Slider & Rendered PDF Page Bitmap
            if (showPdfCanvasMode) {
                item(key = "pdf_canvas_view") {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pdf_rendered_page_card"),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ZoomIn, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "জুম: ${BengaliNumberFormatter.toBengaliDigits((zoomScale * 100).roundToInt())}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Slider(
                                    value = zoomScale,
                                    onValueChange = { zoomScale = it },
                                    onValueChangeFinished = {
                                        viewModel.updateBookReaderPreferences(
                                            bookId = bookId,
                                            zoomScale = zoomScale
                                        )
                                    },
                                    valueRange = 1.0f..2.5f,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            val bmp = renderedBitmap
                            val errMessage = pdfErrorMessageBn
                            if (bmp != null) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "PDF Page $printedPage",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .testTag("pdf_rendered_bitmap_image"),
                                    contentScale = ContentScale.FillWidth
                                )
                            } else if (errMessage != null && !isPdfLoading) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                        .padding(16.dp)
                                        .testTag("pdf_render_error_box"),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = errMessage,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { pdfRetryToken++ },
                                            modifier = Modifier.testTag("btn_pdf_retry")
                                        ) {
                                            Text("পুনরায় চেষ্টা করুন")
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                showPdfCanvasMode = false
                                                viewModel.updateBookReaderPreferences(
                                                    bookId = bookId,
                                                    preferredViewMode = "STRUCTURED"
                                                )
                                            },
                                            modifier = Modifier.testTag("btn_pdf_fallback_structured")
                                        ) {
                                            Text("ডিজিটাল পাঠ্যবই ভিউ")
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                        .testTag("pdf_loading_box"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                        Text("পিডিএফ পৃষ্ঠা লোড হচ্ছে...")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Structured Textbook Page Content (Always visible below or as primary view for 100% clarity)
            item(key = "structured_page_content") {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("structured_textbook_page_card"),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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
                                    text = "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(structuredContent.chapterNumber)}: ${structuredContent.chapterTitleBn}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = "পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(structuredContent.printedPage)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = structuredContent.sectionHeadingBn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )

                        HorizontalDivider()

                        structuredContent.keyDefinitionsBn.forEach { def ->
                            Text(
                                text = "• $def",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (structuredContent.formulasOnPage.isNotEmpty()) {
                            Text(
                                text = "এই পৃষ্ঠার সংশ্লিষ্ট গাণিতিক সূত্র:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            structuredContent.formulasOnPage.forEach { f ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(f.titleBn, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                        Text(f.expressionBn, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
                                        Text(f.explanationBn, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        if (structuredContent.workedExamplesOnPage.isNotEmpty()) {
                            Text(
                                text = "সমাধানকৃত উদাহরণ:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            structuredContent.workedExamplesOnPage.forEach { ex ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(ex.titleBn, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        Text("প্রশ্ন: ${ex.problemStatementBn}", style = MaterialTheme.typography.bodySmall)
                                        ex.stepsBn.forEachIndexed { idx, s ->
                                            Text("ধাপ ${BengaliNumberFormatter.toBengaliDigits(idx + 1)}: $s", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text("উত্তর: ${ex.finalAnswerBn}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        if (structuredContent.rulesOnPageBn.isNotEmpty()) {
                            Text(
                                text = "সমাধানের নিয়ম ও কৌশল:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            structuredContent.rulesOnPageBn.forEachIndexed { idx, r ->
                                Text(
                                    text = "${BengaliNumberFormatter.toBengaliDigits(idx + 1)}. $r",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (structuredContent.theoremsOnPage.isNotEmpty()) {
                            Text(
                                text = "উপপাদ্য ও সম্পাদ্য:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            structuredContent.theoremsOnPage.forEach { th ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("${th.typeLabelBn}: ${th.titleBn}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                        Text(th.statementBn, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        if (structuredContent.exerciseSummaryBn.isNotEmpty()) {
                            HorizontalDivider()
                            structuredContent.exerciseSummaryBn.forEach { item ->
                                Text(
                                    text = "📌 $item",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (structuredContent.answerKeyOnPageBn.isNotEmpty()) {
                            Text(
                                text = "উত্তরমালা ও দ্রুত মিলকরণ:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            structuredContent.answerKeyOnPageBn.forEach { ans ->
                                Text(
                                    text = "✔ $ans",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.openBookChapter(bookId, structuredContent.chapterNumber)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("অধ্যায় গাইড খুলুন")
                            }
                            OutlinedButton(
                                onClick = {
                                    viewModel.startBookPractice(
                                        bookId = bookId,
                                        chapterNumber = structuredContent.chapterNumber
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("এই অধ্যায়ের অনুশীলন")
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun renderPdfPageWithAutoRecovery(
    context: android.content.Context,
    bookId: BookId,
    printedPage: Int,
    scale: Float,
    cacheKey: String,
    forceReExtract: Boolean = false
): Result<Bitmap> {
    for (attempt in 0..1) {
        val shouldForce = forceReExtract || attempt == 1
        val attemptResult = runCatching {
            val pdfFile = resolveOfflinePdfFile(context, bookId, forceRefresh = shouldForce)
                ?: throw IllegalStateException("PDF asset file unavailable for ${bookId.id}")
            if (!pdfFile.exists() || pdfFile.length() < 4_096L) {
                throw IllegalStateException("PDF file invalid or empty: ${pdfFile.absolutePath}")
            }
            ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    if (renderer.pageCount <= 0) {
                        throw IllegalStateException("PDF has 0 pages")
                    }
                    val pdfZeroIndex = (bookId.printedToPdfPage(printedPage) - 1)
                        .coerceIn(0, (renderer.pageCount - 1).coerceAtLeast(0))
                    renderer.openPage(pdfZeroIndex).use { page ->
                        val width = (page.width * scale).roundToInt().coerceIn(300, 1600)
                        val height = (page.height * scale).roundToInt().coerceIn(400, 2200)
                        val bmp = try {
                            Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                        } catch (_: OutOfMemoryError) {
                            BookPdfRenderCache.evictAll()
                            Bitmap.createBitmap(
                                (width / 2).coerceAtLeast(300),
                                (height / 2).coerceAtLeast(400),
                                Bitmap.Config.RGB_565
                            )
                        }
                        bmp.eraseColor(android.graphics.Color.WHITE)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        BookPdfRenderCache.put(cacheKey, bmp)
                        bmp
                    }
                }
            }
        }
        if (attemptResult.isSuccess) {
            return attemptResult
        }
    }
    return Result.failure(IllegalStateException("Failed to render PDF page after recovery attempt"))
}

internal fun isValidPdfFileHeader(file: File): Boolean {
    if (!file.exists() || file.length() < 4_096L) return false
    return runCatching {
        val header = ByteArray(5)
        file.inputStream().use { input ->
            val readCount = input.read(header)
            readCount == 5 && String(header, Charsets.US_ASCII) == "%PDF-"
        }
    }.getOrDefault(false)
}

internal fun resolveOfflinePdfFile(
    context: android.content.Context,
    bookId: BookId,
    forceRefresh: Boolean = false
): File? {
    // 1. Primary path: unpack the bundled NCTB PDF from APK assets (app/src/main/assets/books/*.pdf)
    val cachedAssetFile = synchronized(BookPdfRenderCache.extractionLock) {
        runCatching {
            val outFile = File(context.cacheDir, "cached_${bookId.id}.pdf")
            if (forceRefresh || !isValidPdfFileHeader(outFile)) {
                val tmpFile = File(context.cacheDir, "cached_${bookId.id}_${System.nanoTime()}.pdf.tmp")
                context.assets.open(bookId.assetFileName).use { input ->
                    tmpFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (isValidPdfFileHeader(tmpFile)) {
                    if (outFile.exists()) outFile.delete()
                    if (!tmpFile.renameTo(outFile)) {
                        tmpFile.copyTo(outFile, overwrite = true)
                        tmpFile.delete()
                    }
                } else if (tmpFile.exists()) {
                    tmpFile.delete()
                }
            }
            outFile.takeIf { isValidPdfFileHeader(it) }
        }.getOrNull()
    }
    if (cachedAssetFile != null) return cachedAssetFile

    // 2. Fallback for local non-packaged JVM test environments: check workspace assets
    val rawFileName = when (bookId) {
        BookId.CLASS_6 -> "Class_6_ffa30c8b.pdf"
        BookId.CLASS_7 -> "Class_7_8a4de8c4.pdf"
        BookId.CLASS_8 -> "Class_8_8e299d4a.pdf"
        BookId.CLASS_9_10 -> "Class_9-10_6131d29f.pdf"
    }
    val candidatePaths = listOf(
        File("app/src/main/assets/${bookId.assetFileName}"),
        File("/app/applet/app/src/main/assets/${bookId.assetFileName}"),
        File("public/assets/aistudio/$rawFileName"),
        File("/app/applet/public/assets/aistudio/$rawFileName")
    )
    return candidatePaths.firstOrNull { isValidPdfFileHeader(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookInteractiveLabScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val activeLab by viewModel.selectedBookLabType.collectAsStateWithLifecycle()
    val bookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
    val chapterNum by viewModel.selectedBookChapterNumber.collectAsStateWithLifecycle()

    var paramA by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_interactive_lab:${bookId.id}:$chapterNum:${activeLab.id}:paramA",
        defaultValue = 4f
    )
    var paramB by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_interactive_lab:${bookId.id}:$chapterNum:${activeLab.id}:paramB",
        defaultValue = 3f
    )
    var paramC by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_interactive_lab:${bookId.id}:$chapterNum:${activeLab.id}:paramC",
        defaultValue = 45f
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = activeLab.titleBn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${bookId.shortLabelBn} • অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapterNum)} • ইন্টারেক্টিভ ল্যাব",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_lab_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                }
            )
        }
    ) { innerPadding ->
        val labListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_interactive_lab:${bookId.id}:$chapterNum:${activeLab.id}"
        )
        val labStripScrollState = rememberManagedScrollState(
            viewModel = viewModel,
            stateKey = "book_interactive_lab:${bookId.id}:$chapterNum:lab_strip"
        )
        LazyColumn(
            state = labListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_interactive_lab_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Lab Switcher Strip (All 12 Labs)
            item(key = "lab_switcher_strip") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(labStripScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BookInteractiveLabType.entries.forEach { lab ->
                        FilterChip(
                            selected = activeLab == lab,
                            onClick = { viewModel.openBookInteractiveLab(lab, bookId, chapterNum) },
                            label = { Text(lab.titleBn) },
                            modifier = Modifier.testTag("switch_lab_${lab.id}")
                        )
                    }
                }
            }

            // Interactive Canvas & Real-time Calculation Card
            item(key = "lab_interactive_workspace") {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_lab_workspace_card"),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = activeLab.subtitleBn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Parameter Sliders
                        Column {
                            Text(
                                text = "মান ১ (a / মূলধন / ব্যাসার্ধ / প্রথম পদ): ${BengaliNumberFormatter.toBengaliDigits(paramA.roundToInt())}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = paramA,
                                onValueChange = { paramA = it },
                                valueRange = 1f..20f,
                                modifier = Modifier.testTag("lab_slider_a")
                            )

                            Text(
                                text = "মান ২ (b / হার বা সময় / উচ্চতা / অন্তর): ${BengaliNumberFormatter.toBengaliDigits(paramB.roundToInt())}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = paramB,
                                onValueChange = { paramB = it },
                                valueRange = 1f..20f,
                                modifier = Modifier.testTag("lab_slider_b")
                            )

                            if (activeLab in setOf(
                                    BookInteractiveLabType.TRIGONOMETRY_HEIGHT_DISTANCE_LAB,
                                    BookInteractiveLabType.PYTHAGORAS_THEOREM_VISUALIZER,
                                    BookInteractiveLabType.GEOMETRY_INTERACTIVE_LAB
                                )
                            ) {
                                Text(
                                    text = "কোণ θ (ডিগ্রি): ${BengaliNumberFormatter.toBengaliDigits(paramC.roundToInt())}°",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Slider(
                                    value = paramC,
                                    onValueChange = { paramC = it },
                                    valueRange = 15f..75f,
                                    modifier = Modifier.testTag("lab_slider_angle")
                                )
                            }
                        }

                        // Visual Canvas Diagram
                        val primaryColor = MaterialTheme.colorScheme.primary
                        val secondaryColor = MaterialTheme.colorScheme.tertiary
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(16.dp)
                        ) {
                            val w = size.width
                            val h = size.height
                            val cx = w / 2f
                            val cy = h / 2f

                            when (activeLab) {
                                BookInteractiveLabType.PYTHAGORAS_THEOREM_VISUALIZER,
                                BookInteractiveLabType.TRIGONOMETRY_HEIGHT_DISTANCE_LAB -> {
                                    val r = minOf(w, h) * 0.38f
                                    drawCircle(
                                        color = primaryColor,
                                        radius = r,
                                        center = Offset(cx, cy),
                                        style = Stroke(width = 5f)
                                    )
                                    val rad = paramC * (PI / 180.0)
                                    val px = cx + (r * cos(rad)).toFloat()
                                    val py = cy - (r * sin(rad)).toFloat()
                                    drawLine(primaryColor, Offset(cx, cy), Offset(px, py), strokeWidth = 5f)
                                    drawLine(secondaryColor, Offset(cx, cy), Offset(px, cy), strokeWidth = 4f)
                                    drawLine(secondaryColor, Offset(px, cy), Offset(px, py), strokeWidth = 4f)
                                }

                                BookInteractiveLabType.SET_AND_FUNCTION_VISUALIZER -> {
                                    val r = minOf(w, h) * 0.34f
                                    drawCircle(
                                        color = primaryColor.copy(alpha = 0.45f),
                                        radius = r,
                                        center = Offset(cx - r * 0.45f, cy)
                                    )
                                    drawCircle(
                                        color = secondaryColor.copy(alpha = 0.45f),
                                        radius = r,
                                        center = Offset(cx + r * 0.45f, cy)
                                    )
                                }

                                BookInteractiveLabType.DATA_AND_STATISTICS_LAB,
                                BookInteractiveLabType.PATTERN_AND_SEQUENCE_LAB,
                                BookInteractiveLabType.PROFIT_LOSS_PERCENTAGE_CALC -> {
                                    val bars = 6
                                    val barW = (w / (bars * 1.6f))
                                    for (i in 0 until bars) {
                                        val factor = ((i + 1) * paramA + paramB) / (6f * 20f + 20f)
                                        val barH = (h * factor.coerceIn(0.15f, 0.92f))
                                        val x = (i * barW * 1.4f) + 24f
                                        drawRect(
                                            color = if (i % 2 == 0) primaryColor else secondaryColor,
                                            topLeft = Offset(x, h - barH),
                                            size = Size(barW, barH)
                                        )
                                    }
                                }

                                else -> {
                                    // Coordinate axes + dynamic geometry/number-line representation
                                    drawLine(primaryColor, Offset(16f, cy), Offset(w - 16f, cy), strokeWidth = 4f)
                                    drawLine(primaryColor, Offset(cx, 16f), Offset(cx, h - 16f), strokeWidth = 4f)
                                    val boxW = (paramA / 20f) * (w * 0.4f)
                                    val boxH = (paramB / 20f) * (h * 0.4f)
                                    drawRect(
                                        color = secondaryColor.copy(alpha = 0.35f),
                                        topLeft = Offset(cx, cy - boxH),
                                        size = Size(boxW, boxH)
                                    )
                                }
                            }
                        }

                        // Real-Time Mathematical Output Summary for the Active Lab
                        val a = paramA.roundToInt()
                        val b = paramB.roundToInt()
                        val angle = paramC.roundToInt()
                        val resultLines = remember(activeLab, a, b, angle) {
                            computeLabExplanationLines(activeLab, a, b, angle)
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "গাণিতিক বিশ্লেষণ ও ফলাফল:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                resultLines.forEach { line ->
                                    Text(
                                        text = "• $line",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
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

internal fun computeLabExplanationLines(
    labType: BookInteractiveLabType,
    a: Int,
    b: Int,
    angleDeg: Int
): List<String> {
    fun gcd(x: Int, y: Int): Int = if (y == 0) x else gcd(y, x % y)
    return when (labType) {
        BookInteractiveLabType.NUMBER_SYSTEM_EXPLORER -> {
            val g = gcd(a, b)
            val l = (a * b) / g.coerceAtLeast(1)
            listOf(
                "সংখ্যারেখায় পূর্ণসংখ্যা যোগ-বিয়োগ: ($a) + (-$b) = ${a - b} এবং ($a) + ($b) = ${a + b}",
                "সংখ্যারেখায় মধ্যবর্তী দূরত্ব (পরমমান) |a - b| = |$a - $b| = ${kotlin.math.abs(a - b)}",
                "সংখ্যা দুটির গ.সা.গু. = $g এবং ল.সা.গু. = $l (যাচাই: $a × $b = ${a * b} = $g × $l)"
            )
        }
        BookInteractiveLabType.FRACTION_AND_RATIO_LAB -> {
            val g = gcd(a, b).coerceAtLeast(1)
            val simpA = a / g
            val simpB = b / g
            val pct = String.format("%.2f", (a.toDouble() / b.coerceAtLeast(1)) * 100.0)
            val compDiv = if (a != b) "${a + b}/${a - b}" else "অসংজ্ঞায়িত (a = b)"
            listOf(
                "ভগ্নাংশ $a/$b এর লঘিষ্ঠ রূপ = $simpA/$simpB (অনুপাত = $simpA : $simpB)",
                "দশমিক ও শতকরা রূপ = ${String.format("%.3f", a.toDouble() / b.coerceAtLeast(1))} = $pct%",
                "যোজন-বিয়োজন রূপ (a + b)/(a - b) = $compDiv এবং সমতুল ভগ্নাংশ = ${simpA * 2}/${simpB * 2}"
            )
        }
        BookInteractiveLabType.ALGEBRA_INTERACTIVE_LAB -> listOf(
            "বর্গের সূত্র: (a + b)² = ($a + $b)² = ${(a + b) * (a + b)} এবং a² + 2ab + b² = ${a * a} + ${2 * a * b} + ${b * b} = ${(a + b) * (a + b)}",
            "বর্গের অন্তর: a² - b² = (a + b)(a - b) = (${a + b}) × (${a - b}) = ${a * a - b * b}",
            "ঘনের সূত্র: (a + b)³ = ${(a + b).toDouble().pow(3.0).roundToInt()} এবং a³ + b³ = ${a * a * a + b * b * b}"
        )
        BookInteractiveLabType.EQUATION_SOLVER_VISUALIZER -> listOf(
            "এক চলকবিশিষ্ট সরল সমীকরণ: ${a}x + $b = ${a * 3 + b} ⇒ ${a}x = ${a * 3} ⇒ x = 3",
            "দুই চলকবিশিষ্ট সরল সহসমীকরণ: x + y = ${a + b} এবং x - y = ${a - b} ⇒ ছেদবিন্দু (x, y) = ($a, $b)",
            "লেখচিত্রের ঢাল ও ছেদক: y = ${a}x + $b রেখার ঢাল m = $a এবং Y-অক্ষের ছেদবিন্দু (0, $b)"
        )
        BookInteractiveLabType.GEOMETRY_INTERACTIVE_LAB -> {
            val hyp = sqrt((a * a + b * b).toDouble())
            listOf(
                "সমকোণী ত্রিভুজের ভূমি = $a, লম্ব = $b হলে অতিভুজ = √(${a}² + ${b}²) = ${String.format("%.2f", hyp)}",
                "ত্রিভুজটির ক্ষেত্রফল = ½ × $a × $b = ${String.format("%.1f", 0.5 * a * b)} বর্গ একক এবং পরিসীমা = ${String.format("%.2f", a + b + hyp)} একক",
                "প্রদত্ত কোণ $angleDeg° এর পূরক কোণ = ${90 - angleDeg}° এবং সম্পূরক কোণ = ${180 - angleDeg}°"
            )
        }
        BookInteractiveLabType.PROFIT_LOSS_PERCENTAGE_CALC -> {
            val p = a * 1000.0
            val r = b / 100.0
            val simple = p * r * 2
            val compound = p * (1 + r).pow(2.0) - p
            listOf(
                "মূলধন P = ${p.roundToInt()} টাকা, বার্ষিক মুনাফার হার r = $b%, সময় n = ২ বছর",
                "সরল মুনাফা I = Prn = ${simple.roundToInt()} টাকা এবং সবৃদ্ধিমূল A = ${(p + simple).roundToInt()} টাকা",
                "চক্রবৃদ্ধি মুনাফা = ${compound.roundToInt()} টাকা এবং ২ বছরে পার্থক্য = ${(compound - simple).roundToInt()} টাকা"
            )
        }
        BookInteractiveLabType.SET_AND_FUNCTION_VISUALIZER -> {
            val setA = (1..a.coerceAtMost(8)).toSet()
            val setB = (b.coerceAtMost(5)..(b.coerceAtMost(5) + 4)).toSet()
            val unionSet = setA union setB
            val interSet = setA intersect setB
            listOf(
                "A = $setA এবং B = $setB",
                "সংযোগ A ∪ B = $unionSet এবং ছেদ A ∩ B = $interSet",
                "ভেনচিত্রের সংখ্যার সূত্র যাচাই: n(A ∪ B) = ${unionSet.size} = n(A) + n(B) - n(A ∩ B) = ${setA.size} + ${setB.size} - ${interSet.size}"
            )
        }
        BookInteractiveLabType.PYTHAGORAS_THEOREM_VISUALIZER -> {
            val cSq = a * a + b * b
            val cVal = sqrt(cSq.toDouble())
            listOf(
                "সমকোণী ত্রিভুজের বাহুদ্বয়ের বর্গক্ষেত্র: a² + b² = ${a * a} + ${b * b} = $cSq = c² (অতিভুজ c = ${String.format("%.2f", cVal)})",
                "বৃত্তস্থ ও কেন্দ্রস্থ কোণ উপপাদ্য: বৃত্তস্থ ∠BAC = $angleDeg° হলে কেন্দ্রস্থ ∠BOC = 2 × $angleDeg° = ${2 * angleDeg}°",
                "অর্ধবৃত্তস্থ কোণ সর্বদা ১ সমকোণ (90°) এবং অতিভুজের মধ্যবিন্দু পরিবৃত্তের কেন্দ্র"
            )
        }
        BookInteractiveLabType.TRIGONOMETRY_HEIGHT_DISTANCE_LAB -> {
            val rad = angleDeg * PI / 180.0
            val height = a * tan(rad)
            val hyp = a / cos(rad).coerceAtLeast(0.001)
            listOf(
                "θ = $angleDeg° হলে sin θ = ${String.format("%.3f", sin(rad))}, cos θ = ${String.format("%.3f", cos(rad))}, tan θ = ${String.format("%.3f", tan(rad))}",
                "ত্রিকোণমিতিক অভেদ যাচাই: sin²($angleDeg°) + cos²($angleDeg°) = 1.000",
                "ভূমি দূরত্ব $a মিটার এবং উন্নতি কোণ $angleDeg° হলে উচ্চতা h = ${String.format("%.2f", height)} মিটার ও অতিভুজ = ${String.format("%.2f", hyp)} মিটার"
            )
        }
        BookInteractiveLabType.PATTERN_AND_SEQUENCE_LAB -> {
            val n = 10
            val nthTerm = a + (n - 1) * b
            val sum10 = (n / 2) * (2 * a + (n - 1) * b)
            listOf(
                "সমান্তর ধারার ১ম পদ a = $a, সাধারণ অন্তর d = $b হলে ১০ম পদ = a + 9d = $nthTerm",
                "প্রথম ১০টি পদের সমষ্টি S₁₀ = (10/2){2($a) + 9($b)} = $sum10",
                "ধারাটির প্রথম ৪টি পদ: $a, ${a + b}, ${a + 2 * b}, ${a + 3 * b}, ..."
            )
        }
        BookInteractiveLabType.MENSURATION_CALCULATOR -> listOf(
            "সমবৃত্তভূমিক বেলনের ব্যাসার্ধ r = $a সেমি ও উচ্চতা h = $b সেমি",
            "বক্রতলের ক্ষেত্রফল 2πrh = ${String.format("%.2f", 2 * 3.1416 * a * b)} বর্গ সেমি এবং সমগ্রতল 2πr(r+h) = ${String.format("%.2f", 2 * 3.1416 * a * (a + b))} বর্গ সেমি",
            "বেলনের আয়তন πr²h = ${String.format("%.2f", 3.1416 * a * a * b)} ঘন সেমি এবং $a সেমি ধারবিশিষ্ট ঘনকের আয়তন = ${a * a * a} ঘন সেমি"
        )
        BookInteractiveLabType.DATA_AND_STATISTICS_LAB -> listOf(
            "বিন্যস্ত উপাত্ত সেট (৫টি পদ): $a, ${a + b}, ${a + 2 * b}, ${a + 3 * b}, ${a + 4 * b}",
            "গাণিতিক গড় x̄ = ${a + 2 * b} এবং মধ্যক (৩য় পদ) = ${a + 2 * b}",
            "উপাত্তের পরিসর (Range) = (${a + 4 * b} - $a) + 1 = ${4 * b + 1}"
        )
    }
}
