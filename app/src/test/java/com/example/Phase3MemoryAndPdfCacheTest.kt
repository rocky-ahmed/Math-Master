package com.example

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookInteractiveLabType
import com.example.math.BookPracticeMode
import com.example.math.BookSearchFilterType
import com.example.math.Math3DEngine
import com.example.math.OrbitCameraState
import com.example.math.Solid3DShapeType
import com.example.math.SolidAnatomyHighlight
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.Math3DStudioViewport
import com.example.ui.components.glSurfaceViewFactoryForTesting
import com.example.ui.screens.BookChapterOverviewScreen
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.BookHubScreen
import com.example.ui.screens.BookLessonScreen
import com.example.ui.screens.BookPdfReaderScreen
import com.example.ui.screens.BookPdfRenderCachePool
import com.example.ui.screens.BookPracticeScreen
import com.example.ui.screens.BookRevisionScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.MAX_PDF_CACHE_BYTES
import com.example.ui.screens.MAX_PDF_CACHE_ENTRIES
import com.example.ui.screens.computeSafeCacheBudgetBytes
import com.example.ui.screens.getBookPdfAvailabilityStatus
import com.example.ui.screens.isOfflinePdfAssetAvailable
import com.example.ui.screens.isValidPdfFileHeader
import com.example.ui.screens.renderPdfPageWithAutoRecovery
import com.example.ui.screens.resolveOfflinePdfFile
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBuild

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class Phase3MemoryAndPdfCacheTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application

    private class TestLifecycleOwner(initialState: Lifecycle.State = Lifecycle.State.RESUMED) : LifecycleOwner {
        private val registry = LifecycleRegistry(this).apply {
            currentState = initialState
        }

        override val lifecycle: Lifecycle
            get() = registry

        fun moveTo(state: Lifecycle.State) {
            if (registry.currentState != Lifecycle.State.DESTROYED) {
                registry.currentState = state
            }
        }
    }

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            MathDatabase.getInstance(app).clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        File(app.cacheDir, "cached_${BookId.CLASS_6.id}.pdf").delete()
    }

    @After
    fun tearDown() {
        glSurfaceViewFactoryForTesting = null
        ShadowBuild.reset()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            MathDatabase.getInstance(app).clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        File(app.cacheDir, "cached_${BookId.CLASS_6.id}.pdf").delete()
    }

    @Test
    fun computeSafeCacheBudgetBytes_verifiesAll4FormulaBranchesAndStrictCeiling() {
        val mb = 1024L * 1024L
        val kb = 1024L

        // Branch 1: vmMaxHeapBytes / 16 active (64 MB heap, 4 MB used -> free 60 MB, min(4 MB, 7.5 MB, 16 MB) = 4 MB)
        val b1 = computeSafeCacheBudgetBytes(vmMaxHeapBytes = 64L * mb, currentUsedBytes = 4L * mb)
        assertEquals((4L * mb).toInt(), b1)
        assertTrue(b1 <= (64L * mb / 16L).toInt())
        assertTrue(b1 <= MAX_PDF_CACHE_BYTES)

        // Branch 2: freeHeadroomBytes / 8 active (64 MB heap, 48 MB used -> free 16 MB, min(4 MB, 2 MB, 16 MB) = 2 MB)
        val b2 = computeSafeCacheBudgetBytes(vmMaxHeapBytes = 64L * mb, currentUsedBytes = 48L * mb)
        assertEquals((2L * mb).toInt(), b2)

        // Branch 3: 16 MB hard ceiling active (512 MB heap, 64 MB used -> free 448 MB, min(32 MB, 56 MB, 16 MB) = 16 MB)
        val b3 = computeSafeCacheBudgetBytes(vmMaxHeapBytes = 512L * mb, currentUsedBytes = 64L * mb)
        assertEquals(MAX_PDF_CACHE_BYTES, b3)

        // Branch 4a: 0-byte bypass when vmMaxHeapBytes / 16 < 512 KB (4 MB heap -> 256 KB < 512 KB -> 0)
        val b4a = computeSafeCacheBudgetBytes(vmMaxHeapBytes = 4L * mb, currentUsedBytes = 1L * mb)
        assertEquals(0, b4a)

        // Branch 4b: 0-byte bypass when freeHeadroomBytes / 8 < 512 KB (64 MB heap, 61 MB used -> free 3 MB -> 384 KB < 512 KB -> 0)
        val b4b = computeSafeCacheBudgetBytes(vmMaxHeapBytes = 64L * mb, currentUsedBytes = 61L * mb)
        assertEquals(0, b4b)

        // Branch 4c: Extreme zero / negative heap boundary
        assertEquals(0, computeSafeCacheBudgetBytes(vmMaxHeapBytes = 0L, currentUsedBytes = 0L))
        assertEquals(0, computeSafeCacheBudgetBytes(vmMaxHeapBytes = 512L * kb, currentUsedBytes = 0L))

        // Pool constructed with 0 budget must bypass caching completely.
        val zeroPool = BookPdfRenderCachePool(maxBudgetBytes = b4a, maxEntries = MAX_PDF_CACHE_ENTRIES)
        val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
        try {
            zeroPool.put("p1", bmp)
            assertEquals(0, zeroPool.currentSizeBytes())
            assertEquals(0, zeroPool.entryCount())
            assertNull(zeroPool.get("p1"))
        } finally {
            bmp.recycle()
        }
    }

    @Test
    fun bookPdfRenderCachePool_enforcesByteBudgetMax6EntriesAndOnTrimMemory() {
        val allocatedBitmaps = mutableListOf<Bitmap>()
        try {
            // 1) Byte eviction test: 800x1100 RGB_565 = 1,760,000 bytes (~1.68 MB each).
            // In a 4 MB pool, at most 2 pages fit (~3.36 MB); inserting a 3rd evicts the eldest.
            val pool4Mb = BookPdfRenderCachePool(maxBudgetBytes = 4 * 1024 * 1024, maxEntries = MAX_PDF_CACHE_ENTRIES)
            val b1 = Bitmap.createBitmap(800, 1100, Bitmap.Config.RGB_565).also { allocatedBitmaps.add(it) }
            val b2 = Bitmap.createBitmap(800, 1100, Bitmap.Config.RGB_565).also { allocatedBitmaps.add(it) }
            val b3 = Bitmap.createBitmap(800, 1100, Bitmap.Config.RGB_565).also { allocatedBitmaps.add(it) }

            pool4Mb.put("p1", b1)
            pool4Mb.put("p2", b2)
            assertEquals(2, pool4Mb.entryCount())
            assertTrue(pool4Mb.currentSizeBytes() <= 4 * 1024 * 1024)

            pool4Mb.put("p3", b3)
            assertTrue(pool4Mb.currentSizeBytes() <= 4 * 1024 * 1024)
            assertNull("Eldest large bitmap must be evicted under byte cap", pool4Mb.get("p1"))
            assertNotNull(pool4Mb.get("p2"))
            assertNotNull(pool4Mb.get("p3"))

            // 2) Entry cap (<= 6) test inside a spacious 16 MB pool.
            val pool16Mb = BookPdfRenderCachePool(maxBudgetBytes = MAX_PDF_CACHE_BYTES, maxEntries = MAX_PDF_CACHE_ENTRIES)
            for (idx in 1..10) {
                val small = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565).also { allocatedBitmaps.add(it) }
                pool16Mb.put("small_$idx", small)
                assertTrue("Entry count must never exceed 6", pool16Mb.entryCount() <= MAX_PDF_CACHE_ENTRIES)
            }
            assertEquals(6, pool16Mb.entryCount())
            val sizeBeforeTrim = pool16Mb.currentSizeBytes()
            assertTrue(sizeBeforeTrim > 0)

            // 3) TRIM_MEMORY_RUNNING_MODERATE must evict >= 50% of cached bytes.
            @Suppress("DEPRECATION")
            pool16Mb.onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE)
            val sizeAfterModerate = pool16Mb.currentSizeBytes()
            assertTrue(
                "Moderate memory trim must shrink cached bytes by >= 50% (before=$sizeBeforeTrim, after=$sizeAfterModerate)",
                sizeAfterModerate <= sizeBeforeTrim / 2
            )

            // 4) TRIM_MEMORY_RUNNING_LOW clears cache completely.
            @Suppress("DEPRECATION")
            pool16Mb.onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW)
            assertEquals(0, pool16Mb.currentSizeBytes())
            assertEquals(0, pool16Mb.entryCount())

            // Re-populate and verify onLowMemory() also clears cache to 0.
            val bRe = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565).also { allocatedBitmaps.add(it) }
            pool16Mb.put("small_re", bRe)
            assertTrue(pool16Mb.currentSizeBytes() > 0)
            pool16Mb.onLowMemory()
            assertEquals(0, pool16Mb.currentSizeBytes())
        } finally {
            allocatedBitmaps.forEach { if (!it.isRecycled) it.recycle() }
        }
    }

    @Test
    fun resolveOfflinePdfFile_8ConcurrentCoroutines_mutexSerializesExtraction() = runBlocking {
        val targetCache = File(app.cacheDir, "cached_${BookId.CLASS_6.id}.pdf")
        targetCache.delete()

        val results = (1..8).map {
            async(Dispatchers.IO) {
                resolveOfflinePdfFile(app, BookId.CLASS_6, forceRefresh = false)
            }
        }.awaitAll()

        results.forEach { resolved ->
            assertNotNull("All concurrent callers must resolve a valid offline PDF", resolved)
            assertTrue(resolved!!.exists() && resolved.length() > 100L)
        }
        val distinctPaths = results.filterNotNull().map { it.canonicalPath }.distinct()
        assertEquals("Deterministic single-file resolution across 8 concurrent callers", 1, distinctPaths.size)
    }

    @Test
    fun math3dViewport_lifecycleTransitions_driveGlSurfaceViewOnPauseAndOnResume() {
        val pauseCalls = AtomicInteger(0)
        val resumeCalls = AtomicInteger(0)

        ShadowBuild.setFingerprint("google/panther/panther:14/AP1A.240305.019.A1/test:user/release-keys")
        glSurfaceViewFactoryForTesting = { ctx ->
            object : GLSurfaceView(ctx) {
                override fun setRenderer(renderer: Renderer?) {
                    // Skip starting native EGL thread under JVM Robolectric while keeping real GLSurfaceView instance.
                }

                override fun setRenderMode(renderMode: Int) = Unit

                override fun onPause() {
                    pauseCalls.incrementAndGet()
                }

                override fun onResume() {
                    resumeCalls.incrementAndGet()
                }

                override fun requestRender() = Unit
            }
        }

        val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        val (faces, lines) = Math3DEngine.buildSolidMesh(Solid3DShapeType.CUBE, 4.0)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                Math3DStudioViewport(
                    faces = faces,
                    internalLines = lines,
                    camera = OrbitCameraState(),
                    highlightMode = SolidAnatomyHighlight.ALL,
                    primaryColor = Color(0xFF2563EB),
                    secondaryColor = Color(0xFF10B981),
                    tertiaryColor = Color(0xFFF59E0B),
                    backgroundColor = Color.White,
                    gridColor = Color.LightGray,
                    isDarkTheme = false,
                    useHardwareGl = true
                )
            }
        }
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        val resumeAfterCompose = resumeCalls.get()
        assertTrue("Initial RESUMED composition must resume GLSurfaceView", resumeAfterCompose >= 1)

        val pauseBeforeTransition = pauseCalls.get()
        composeTestRule.runOnUiThread {
            lifecycleOwner.moveTo(Lifecycle.State.STARTED)
        }
        composeTestRule.waitForIdle()
        assertTrue("RESUMED -> STARTED (ON_PAUSE) must pause GLSurfaceView", pauseCalls.get() > pauseBeforeTransition)

        val resumeBeforeReturn = resumeCalls.get()
        composeTestRule.runOnUiThread {
            lifecycleOwner.moveTo(Lifecycle.State.RESUMED)
        }
        composeTestRule.waitForIdle()
        assertTrue("STARTED -> RESUMED (ON_RESUME) must resume GLSurfaceView", resumeCalls.get() > resumeBeforeReturn)

        val pauseBeforeDestroy = pauseCalls.get()
        composeTestRule.runOnUiThread {
            lifecycleOwner.moveTo(Lifecycle.State.DESTROYED)
        }
        composeTestRule.waitForIdle()
        assertTrue("DESTROYED transition must pause GLSurfaceView cleanly", pauseCalls.get() >= pauseBeforeDestroy)
    }

    // =========================================================================
    // A-07: CLASS 6–8 PDF RESOLUTION, CLASS 9–10 MISSING PDF DETECTION & SAFE FALLBACK
    // =========================================================================

    @Test
    fun a07_pdfResolverAndCurriculumIntegrity_class6To8Available_class910MissingDetected_all17ChaptersIntact() {
        val expectedAvailableAssetSizes = mapOf(
            BookId.CLASS_6 to 5_372_426L,
            BookId.CLASS_7 to 6_391_697L,
            BookId.CLASS_8 to 6_459_157L
        )
        val expectedChaptersCount = mapOf(
            BookId.CLASS_6 to 8,
            BookId.CLASS_7 to 11,
            BookId.CLASS_8 to 11,
            BookId.CLASS_9_10 to 17
        )

        // 1, 2, 3. Verify Class 6, Class 7, and Class 8 offline PDFs resolve with exact sizes and valid %PDF- headers.
        listOf(BookId.CLASS_6, BookId.CLASS_7, BookId.CLASS_8).forEach { bookId ->
            File(app.cacheDir, "cached_${bookId.id}.pdf").delete()
            assertTrue("Offline PDF must be detected as available for ${bookId.id}", isOfflinePdfAssetAvailable(app, bookId))
            val resolvedFile = resolveOfflinePdfFile(app, bookId, forceRefresh = true)
            assertNotNull("resolveOfflinePdfFile must return non-null File for ${bookId.id}", resolvedFile)
            assertTrue("Resolved file for ${bookId.id} must have valid %PDF- header", isValidPdfFileHeader(resolvedFile!!))
            assertEquals(
                "Resolved PDF byte size for ${bookId.id} must match verified NCTB asset",
                expectedAvailableAssetSizes.getValue(bookId),
                resolvedFile.length()
            )

            val status = getBookPdfAvailabilityStatus(app, bookId)
            assertTrue(status.isStructuredContentAvailable)
            assertTrue(status.isOfflinePdfAvailable)
            assertEquals(expectedChaptersCount.getValue(bookId), status.totalChaptersCount)
            assertTrue(status.structuredStatusBn.contains("উপলব্ধ"))
            assertTrue(status.pdfStatusBn.contains("উপলব্ধ"))
            assertNull(status.fallbackExplanationBn)
        }

        // 4 & 9. Verify Class 9–10 missing PDF is accurately detected and renderPdfPageWithAutoRecovery does not crash.
        File(app.cacheDir, "cached_${BookId.CLASS_9_10.id}.pdf").delete()
        val class910AssetFile = File("app/src/main/assets/${BookId.CLASS_9_10.assetFileName}")
        val class910WorkspaceFile = File("/app/applet/app/src/main/assets/${BookId.CLASS_9_10.assetFileName}")
        val isClass910PresentOnDisk = isValidPdfFileHeader(class910AssetFile) || isValidPdfFileHeader(class910WorkspaceFile)
        assertFalse("Class 9-10 PDF asset is currently absent from assets/books/", isClass910PresentOnDisk)

        assertNull("resolveOfflinePdfFile must return null when Class 9-10 PDF asset is absent", resolveOfflinePdfFile(app, BookId.CLASS_9_10))
        assertFalse("isOfflinePdfAssetAvailable must return false for Class 9-10 when asset is absent", isOfflinePdfAssetAvailable(app, BookId.CLASS_9_10))

        val class910Status = getBookPdfAvailabilityStatus(app, BookId.CLASS_9_10)
        assertTrue("Structured digital content for Class 9-10 must be reported available", class910Status.isStructuredContentAvailable)
        assertFalse("Offline PDF for Class 9-10 must be reported unavailable", class910Status.isOfflinePdfAvailable)
        assertEquals(17, class910Status.totalChaptersCount)
        assertTrue(class910Status.structuredStatusBn.contains("ডিজিটাল পাঠ্যবই কনটেন্ট (১৭টি অধ্যায়): উপলব্ধ"))
        assertTrue(class910Status.pdfStatusBn.contains("বর্তমানে অনুপলব্ধ"))
        assertNotNull(class910Status.fallbackExplanationBn)
        assertTrue(class910Status.fallbackExplanationBn!!.contains("১৭টি অধ্যায়ের সকল পাঠ"))

        val renderAttempt = renderPdfPageWithAutoRecovery(
            context = app,
            bookId = BookId.CLASS_9_10,
            printedPage = 1,
            scale = 1.0f,
            cacheKey = "class_9_10_p1_z10",
            forceReExtract = true
        )
        assertTrue("renderPdfPageWithAutoRecovery must return clean failure Result without crashing when PDF is absent", renderAttempt.isFailure)

        // 8. Verify all 17 Class 9–10 chapters, lessons, formulas, worked examples, theorems, and structured page content remain intact.
        val class910Chapters = BookCurriculumRepository.getChapters(BookId.CLASS_9_10)
        assertEquals("Class 9-10 must preserve all 17 chapters", 17, class910Chapters.size)
        class910Chapters.forEachIndexed { index, ch ->
            assertEquals(index + 1, ch.chapterNumber)
            assertTrue(ch.titleBn.isNotBlank())
            assertTrue("Chapter ${ch.chapterNumber} must have lessons", ch.lessons.isNotEmpty())
            assertTrue("Chapter ${ch.chapterNumber} must have formulas", ch.formulas.isNotEmpty())
            if (ch.chapterNumber in setOf(7, 15)) {
                assertTrue(
                    "Geometry chapter ${ch.chapterNumber} must have theorems or constructions",
                    ch.theoremsAndConstructions.isNotEmpty()
                )
            } else {
                assertTrue("Chapter ${ch.chapterNumber} must have worked examples", ch.workedExamples.isNotEmpty())
            }
            assertTrue(ch.printedPageStart in 1..BookId.CLASS_9_10.maxPrintedPage)
            assertTrue(ch.printedPageEnd in ch.printedPageStart..BookId.CLASS_9_10.maxPrintedPage)

            val startPageContent = BookCurriculumRepository.getStructuredPageContent(BookId.CLASS_9_10, ch.printedPageStart)
            assertEquals(ch.chapterNumber, startPageContent.chapterNumber)
            assertEquals(ch.printedPageStart, startPageContent.printedPage)
            assertTrue(startPageContent.sectionHeadingBn.isNotBlank())
            assertTrue(startPageContent.keyDefinitionsBn.isNotEmpty())
        }
        assertTrue(
            "Class 9-10 geometry chapters must preserve theorems and constructions",
            class910Chapters.sumOf { it.theoremsAndConstructions.size } > 0
        )
    }

    @Test
    fun a07_class910BookDetailAndReader_gracefulBengaliFallback_pdfActionsDisabled_structuredNavigationIntact() {
        val vm = MathViewModel(app, savedStateHandle = SavedStateHandle())

        // Pre-seed preferredViewMode = "PDF_BITMAP" for Class 9-10 to ensure even a previously saved PDF canvas preference
        // is safely intercepted and forced into structured digital textbook mode without showing a broken PDF canvas.
        vm.updateBookReaderPreferences(BookId.CLASS_9_10, preferredViewMode = "PDF_BITMAP", zoomScale = 1.2f)
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        // 5, 6, 10. Open Class 9-10 BookDetailScreen -> verify structured vs PDF status distinction, disabled top-bar PDF icon,
        // Bengali fallback banner, and intact 17-chapter navigation.
        vm.openBookDetail(BookId.CLASS_9_10)
        assertEquals(Screen.BookDetail, vm.currentScreen)
        assertEquals(BookId.CLASS_9_10, vm.selectedBookId.value)

        composeTestRule.setContent {
            val activeScreen by vm.screenStack.collectAsStateWithLifecycle()
            when (activeScreen.last()) {
                is Screen.BookDetail -> BookDetailScreen(viewModel = vm)
                is Screen.BookPdfReader -> BookPdfReaderScreen(viewModel = vm)
                else -> BookDetailScreen(viewModel = vm)
            }
        }
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        // Top-bar raw PDF button is disabled for Class 9-10 when PDF asset is absent.
        composeTestRule.onNodeWithTag("book_detail_pdf_btn").assertIsNotEnabled()
        // Explicit distinction between Structured content (Available) and Offline PDF (Unavailable) is displayed.
        composeTestRule.onNodeWithTag("book_detail_structured_status_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("book_detail_pdf_status_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("book_detail_pdf_unavailable_banner").assertIsDisplayed()

        // Clicking the primary reader button ("ডিজিটাল পাঠ্যবই পড়ুন") opens BookPdfReaderScreen in structured digital view.
        composeTestRule.onNodeWithTag("book_detail_resume_pdf_btn").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        assertEquals(Screen.BookPdfReader, vm.currentScreen)

        // 5, 6, 7, 9. Inside BookPdfReaderScreen for Class 9-10:
        // - Structured mode chip is selected; PDF mode chip is disabled.
        // - Bengali fallback notice banner is displayed.
        // - No blank, loading, or error PDF canvas card is rendered.
        // - Structured textbook page content card is displayed and navigable across chapters/pages.
        composeTestRule.onNodeWithTag("reader_mode_structured_btn").assertIsSelected()
        composeTestRule.onNodeWithTag("reader_mode_pdf_btn").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("reader_mode_pdf_btn").assertIsNotSelected()
        composeTestRule.onNodeWithTag("book_pdf_unavailable_fallback_banner").assertIsDisplayed()
        composeTestRule.onNodeWithTag("book_reader_structured_status_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("book_reader_pdf_status_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("book_reader_pdf_fallback_message_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("structured_textbook_page_card").assertIsDisplayed()

        assertEquals(
            "Broken or blank PDF canvas card must NEVER be displayed when PDF asset is absent",
            0,
            composeTestRule.onAllNodesWithTag("pdf_rendered_page_card").fetchSemanticsNodes().size
        )
        assertEquals(
            0,
            composeTestRule.onAllNodesWithTag("pdf_render_error_box").fetchSemanticsNodes().size
        )
        assertEquals(
            0,
            composeTestRule.onAllNodesWithTag("pdf_loading_box").fetchSemanticsNodes().size
        )

        // 7 & 10. Navigate pages and chapters within Class 9-10 structured reader (Page 1 -> Page 2 -> Chapter 17 Page 325).
        assertEquals(1, vm.selectedBookPrintedPage.value)
        composeTestRule.onNodeWithTag("pdf_next_page_btn").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()
        assertEquals(2, vm.selectedBookPrintedPage.value)

        val chapter17 = BookCurriculumRepository.getChapter(BookId.CLASS_9_10, 17)
        assertNotNull(chapter17)
        composeTestRule.runOnUiThread {
            vm.updateBookReaderPage(BookId.CLASS_9_10, printedPage = chapter17!!.printedPageStart, chapterNumber = 17)
        }
        composeTestRule.waitForIdle()
        assertEquals(chapter17!!.printedPageStart, vm.selectedBookPrintedPage.value)
        assertEquals(17, vm.selectedBookChapterNumber.value)
        composeTestRule.onNodeWithTag("structured_textbook_page_card").assertIsDisplayed()

        // Verify Class 9-10 chapter overview, lesson, and interactive lab navigation remain intact.
        composeTestRule.runOnUiThread {
            vm.openBookChapter(BookId.CLASS_9_10, 9) // Trigonometry
        }
        assertEquals(Screen.BookChapterOverview, vm.currentScreen)
        assertEquals(9, vm.selectedBookChapterNumber.value)

        val ch9FirstLesson = BookCurriculumRepository.getChapter(BookId.CLASS_9_10, 9)!!.lessons.first()
        composeTestRule.runOnUiThread {
            vm.openBookLesson(BookId.CLASS_9_10, 9, ch9FirstLesson.id)
        }
        assertEquals(Screen.BookLesson, vm.currentScreen)
        assertEquals(ch9FirstLesson.id, vm.selectedBookLessonId.value)

        composeTestRule.runOnUiThread {
            vm.openBookInteractiveLab(BookInteractiveLabType.TRIGONOMETRY_HEIGHT_DISTANCE_LAB, BookId.CLASS_9_10, 9)
        }
        assertEquals(Screen.BookInteractiveLab, vm.currentScreen)

        // 1, 2, 3, 10. Verify Class 6 (whose PDF asset IS available) has enabled PDF buttons on BookDetailScreen and BookPdfReaderScreen.
        composeTestRule.runOnUiThread {
            vm.openBookDetail(BookId.CLASS_6)
        }
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        composeTestRule.onNodeWithTag("book_detail_pdf_btn").assertIsEnabled()
        assertEquals(
            "Fallback unavailable banner must not appear for Class 6 whose PDF is available",
            0,
            composeTestRule.onAllNodesWithTag("book_detail_pdf_unavailable_banner").fetchSemanticsNodes().size
        )

        composeTestRule.runOnUiThread {
            vm.openBookPdfReader(BookId.CLASS_6, printedPage = 10, chapterNumber = 1)
        }
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        composeTestRule.onNodeWithTag("reader_mode_pdf_btn").assertIsEnabled()
        assertEquals(
            0,
            composeTestRule.onAllNodesWithTag("book_pdf_unavailable_fallback_banner").fetchSemanticsNodes().size
        )
    }

    private fun assertStructuredReaderWithoutPdfCanvas() {
        composeTestRule.onNodeWithTag("reader_mode_structured_btn").assertIsSelected()
        composeTestRule.onNodeWithTag("reader_mode_pdf_btn").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("reader_mode_pdf_btn").assertIsNotSelected()
        composeTestRule.onNodeWithTag("book_pdf_unavailable_fallback_banner").assertIsDisplayed()
        composeTestRule.onNodeWithTag("structured_textbook_page_card").assertIsDisplayed()
        assertEquals(
            "Broken or blank PDF canvas card must NEVER be displayed when PDF asset is absent",
            0,
            composeTestRule.onAllNodesWithTag("pdf_rendered_page_card").fetchSemanticsNodes().size
        )
        assertEquals(
            0,
            composeTestRule.onAllNodesWithTag("pdf_loading_box").fetchSemanticsNodes().size
        )
        assertEquals(
            0,
            composeTestRule.onAllNodesWithTag("pdf_render_error_box").fetchSemanticsNodes().size
        )
    }

    @Test
    fun a07_chapterOverviewAndLessonPageActions_missingPdf_openStructuredReaderAndKeepPdfDisabled() {
        val vm = MathViewModel(app, savedStateHandle = SavedStateHandle())
        vm.updateBookReaderPreferences(BookId.CLASS_9_10, preferredViewMode = "PDF_BITMAP", zoomScale = 1.15f)
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        val chapter9 = BookCurriculumRepository.getChapter(BookId.CLASS_9_10, 9)!!
        val firstLesson = chapter9.lessons.first()

        composeTestRule.setContent {
            val activeScreen by vm.screenStack.collectAsStateWithLifecycle()
            when (activeScreen.last()) {
                is Screen.BookChapterOverview -> BookChapterOverviewScreen(viewModel = vm)
                is Screen.BookLesson -> BookLessonScreen(viewModel = vm)
                is Screen.BookPdfReader -> BookPdfReaderScreen(viewModel = vm)
                else -> BookChapterOverviewScreen(viewModel = vm)
            }
        }

        // 1. Chapter Overview page action with missing Class 9-10 PDF
        composeTestRule.runOnUiThread {
            vm.openBookChapter(BookId.CLASS_9_10, 9)
        }
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        val chapterActionNode = composeTestRule.onNodeWithTag("chapter_overview_pdf_btn")
            .assertIsDisplayed()
            .assertIsEnabled()
        val chapterContentDesc = chapterActionNode.fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription]
            .joinToString(" ")
        assertTrue(
            "Chapter Overview action must truthfully indicate digital structured content when PDF is unavailable, was '$chapterContentDesc'",
            chapterContentDesc.contains("ডিজিটাল পাঠ্যবই") && chapterContentDesc.contains("অনুপলব্ধ")
        )

        chapterActionNode.performClick()
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        assertEquals(Screen.BookPdfReader, vm.currentScreen)
        assertEquals(BookId.CLASS_9_10, vm.selectedBookId.value)
        assertEquals(chapter9.printedPageStart, vm.selectedBookPrintedPage.value)
        assertStructuredReaderWithoutPdfCanvas()

        // 2. Lesson page action with missing Class 9-10 PDF
        composeTestRule.runOnUiThread {
            vm.openBookLesson(BookId.CLASS_9_10, 9, firstLesson.id)
        }
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        val lessonActionNode = composeTestRule.onNodeWithTag("book_lesson_pdf_btn")
            .assertIsDisplayed()
            .assertIsEnabled()
        val lessonContentDesc = lessonActionNode.fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription]
            .joinToString(" ")
        assertTrue(
            "Lesson page action must truthfully indicate digital structured content when PDF is unavailable, was '$lessonContentDesc'",
            lessonContentDesc.contains("ডিজিটাল পাঠ্যবই") && lessonContentDesc.contains("অনুপলব্ধ")
        )

        lessonActionNode.performClick()
        composeTestRule.waitForIdle()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        assertEquals(Screen.BookPdfReader, vm.currentScreen)
        assertEquals(BookId.CLASS_9_10, vm.selectedBookId.value)
        assertEquals(firstLesson.printedPageStart, vm.selectedBookPrintedPage.value)
        assertStructuredReaderWithoutPdfCanvas()
    }

    private fun assertDigitalBookSemantics(nodeTag: String, expectedTextSubstring: String? = null) {
        val node = composeTestRule.onNodeWithTag(nodeTag)
            .assertIsDisplayed()
            .fetchSemanticsNode()
        val contentDesc = node.config.getOrElse(SemanticsProperties.ContentDescription) { emptyList() }
            .joinToString(" ")
        assertTrue(
            "Node '$nodeTag' must have truthful digital-book Bengali contentDescription, was '$contentDesc'",
            contentDesc.contains("ডিজিটাল পাঠ্যবই") && contentDesc.contains("অনুপলব্ধ")
        )
        if (expectedTextSubstring != null) {
            val text = node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
                .joinToString(" ") { it.text }
            assertTrue(
                "Node '$nodeTag' must display '$expectedTextSubstring', was '$text'",
                text.contains(expectedTextSubstring)
            )
        }
    }

    @Test
    fun a07_searchBookmarkAndPracticeRevisionPageRoutes_missingPdf_resolveSafelyThroughStructuredReader() {
        val db = MathDatabase.getInstance(app)
        val vm = MathViewModel(app, savedStateHandle = SavedStateHandle())
        vm.updateBookReaderPreferences(BookId.CLASS_9_10, preferredViewMode = "PDF_BITMAP", zoomScale = 1.1f)
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        val class910SearchResults = BookCurriculumRepository.searchAllBooks(
            query = "ত্রিকোণমিতি",
            filterType = BookSearchFilterType.CHAPTER,
            bookFilter = BookId.CLASS_9_10
        )
        assertTrue("Expected Class 9-10 trigonometry search result", class910SearchResults.isNotEmpty())
        val searchResult = class910SearchResults.first { it.bookId == BookId.CLASS_9_10 }
        val targetBookmarkPage = 180

        try {
            composeTestRule.setContent {
                val activeScreen by vm.screenStack.collectAsStateWithLifecycle()
                when (activeScreen.last()) {
                    is Screen.BookHub -> BookHubScreen(viewModel = vm)
                    is Screen.BookDetail -> BookDetailScreen(viewModel = vm)
                    is Screen.Bookmarks -> BookmarksScreen(viewModel = vm)
                    is Screen.BookPractice -> BookPracticeScreen(viewModel = vm)
                    is Screen.BookRevision -> BookRevisionScreen(viewModel = vm)
                    is Screen.BookPdfReader -> BookPdfReaderScreen(viewModel = vm)
                    else -> BookHubScreen(viewModel = vm)
                }
            }

            // 1. Search result page route in BookHubScreen
            composeTestRule.runOnUiThread {
                vm.openBookHub()
                vm.setBookSearchFilter(BookSearchFilterType.CHAPTER)
                vm.setBookSearchQuery("ত্রিকোণমিতি")
            }
            composeTestRule.waitUntil(timeoutMillis = 5_000L) {
                composeTestRule.onAllNodesWithTag("book_search_result_page_${searchResult.id}")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("book_search_result_page_${searchResult.id}")
                .performScrollTo()
                .assertIsDisplayed()
                .assertIsEnabled()
            assertDigitalBookSemantics("book_search_result_page_${searchResult.id}", "ডিজিটাল পৃষ্ঠা")
            composeTestRule.onNodeWithTag("book_search_result_page_${searchResult.id}").performClick()
            composeTestRule.waitForIdle()
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

            assertEquals(Screen.BookPdfReader, vm.currentScreen)
            assertEquals(BookId.CLASS_9_10, vm.selectedBookId.value)
            assertEquals(searchResult.printedPage, vm.selectedBookPrintedPage.value)
            assertStructuredReaderWithoutPdfCanvas()

            // 2. Bookmark page route (BookmarksScreen)
            composeTestRule.runOnUiThread {
                vm.toggleBookReaderPageBookmark(BookId.CLASS_9_10, targetBookmarkPage)
                vm.navigateTo(Screen.Bookmarks)
            }
            composeTestRule.waitUntil(timeoutMillis = 5_000L) {
                composeTestRule.onAllNodesWithTag("bookmark_item_book_page_class_9_10_$targetBookmarkPage")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("bookmark_item_book_page_class_9_10_$targetBookmarkPage")
                .performScrollTo()
                .assertIsDisplayed()
            assertDigitalBookSemantics("bookmark_item_book_page_class_9_10_$targetBookmarkPage", "ডিজিটাল পাঠ্যবই পৃষ্ঠা")
            composeTestRule.onNodeWithTag("bookmark_item_book_page_class_9_10_$targetBookmarkPage").performClick()
            composeTestRule.waitForIdle()
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

            assertEquals(Screen.BookPdfReader, vm.currentScreen)
            assertEquals(BookId.CLASS_9_10, vm.selectedBookId.value)
            assertEquals(targetBookmarkPage, vm.selectedBookPrintedPage.value)
            assertStructuredReaderWithoutPdfCanvas()

            // 3. Revision & Practice page routes (BookRevisionScreen Tab 3 + BookPracticeScreen + BookDetailScreen chapter chip)
            composeTestRule.runOnUiThread {
                vm.openBookRevision(BookId.CLASS_9_10)
            }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("revision_tab_3").performScrollTo().performClick()
            composeTestRule.waitUntil(timeoutMillis = 5_000L) {
                composeTestRule.onAllNodesWithTag("revision_bookmarked_page_class_9_10_$targetBookmarkPage")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("revision_bookmarked_page_class_9_10_$targetBookmarkPage")
                .performScrollTo()
                .assertIsDisplayed()
            assertDigitalBookSemantics("revision_bookmarked_page_class_9_10_$targetBookmarkPage", "ডিজিটাল পাঠ্যবই কনটেন্ট")
            composeTestRule.onNodeWithTag("revision_bookmarked_page_class_9_10_$targetBookmarkPage").performClick()
            composeTestRule.waitForIdle()
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

            assertEquals(Screen.BookPdfReader, vm.currentScreen)
            assertEquals(BookId.CLASS_9_10, vm.selectedBookId.value)
            assertEquals(targetBookmarkPage, vm.selectedBookPrintedPage.value)
            assertStructuredReaderWithoutPdfCanvas()

            // Verify BookPracticeScreen for Class 9-10 renders safely and BookDetailScreen chapter page chip routes safely
            composeTestRule.runOnUiThread {
                vm.startBookPractice(BookId.CLASS_9_10, chapterNumber = 9, mode = BookPracticeMode.STANDARD)
            }
            composeTestRule.waitForIdle()
            assertEquals(Screen.BookPractice, vm.currentScreen)
            composeTestRule.onNodeWithTag("book_practice_question_card").assertIsDisplayed()

            composeTestRule.runOnUiThread {
                vm.openBookDetail(BookId.CLASS_9_10)
            }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("book_detail_chapter_list")
                .performScrollToNode(hasTestTag("chapter_page_chip_class_9_10_1"))
            assertDigitalBookSemantics("chapter_page_chip_class_9_10_1", "ডিজিটাল পৃষ্ঠা")
            composeTestRule.onNodeWithTag("chapter_page_chip_class_9_10_1")
                .assertIsDisplayed()
                .assertIsEnabled()
                .performClick()
            composeTestRule.waitForIdle()
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

            assertEquals(Screen.BookPdfReader, vm.currentScreen)
            assertEquals(BookId.CLASS_9_10, vm.selectedBookId.value)
            assertStructuredReaderWithoutPdfCanvas()
        } finally {
            runBlocking(Dispatchers.IO) {
                db.mathDao().deleteBookmarkById("book_page_class_9_10_$targetBookmarkPage")
            }
            vm.setBookSearchQuery("")
            vm.setBookSearchFilter(BookSearchFilterType.ALL)
        }
    }
}


