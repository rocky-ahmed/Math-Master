package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.math.BcsExamProfile
import com.example.math.BcsSyllabusVersion
import com.example.math.BookId
import com.example.math.BookInteractiveLabType
import com.example.ui.MathViewModel
import com.example.ui.NavigationContextSnapshot
import com.example.ui.NavigationFrame
import com.example.ui.Screen
import com.example.ui.ScreenCodec
import com.example.ui.UiContextEntry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class Phase3KeyIsolationAndNavigationTest {

    private lateinit var app: Application

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            MathDatabase.getInstance(app).clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        ScreenCodec.encodeStackObserverForTesting = null
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun keyScopedObservation_32Collectors_zeroUnrelatedWakeupsSynchronized() = runBlocking {
        val handle = SavedStateHandle()
        val viewModel = MathViewModel(app, savedStateHandle = handle)
        val initialLatches = Array(32) { CountDownLatch(1) }
        val wakeCounts = ConcurrentHashMap<String, AtomicInteger>()
        val perStepLatch = arrayOfNulls<CountDownLatch>(10)
        val jobs = ArrayList<Job>(32)

        viewModel.uiContextKeyEmissionObserverForTesting = { fullKey, _ ->
            wakeCounts.computeIfAbsent(fullKey) { AtomicInteger(0) }.incrementAndGet()
        }

        try {
            var currentStep = -1
            for (i in 0 until 32) {
                val key = "solver:field_$i"
                val job = launch(Dispatchers.Main.immediate, start = CoroutineStart.UNDISPATCHED) {
                    viewModel.observeUiContextKey(key).collect { entry ->
                        if (initialLatches[i].count > 0) {
                            initialLatches[i].countDown()
                        } else if (i == 0 && entry is UiContextEntry.Present && currentStep in 0..9) {
                            perStepLatch[currentStep]?.countDown()
                        }
                    }
                }
                jobs.add(job)
            }

            initialLatches.forEach { latch ->
                assertTrue("Initial emission settled for key", latch.await(5, TimeUnit.SECONDS))
            }
            assertEquals(32, viewModel.activeUiContextKeySlotCountForTesting())

            wakeCounts.clear()

            for (step in 0 until 10) {
                val latch = CountDownLatch(1)
                perStepLatch[step] = latch
                currentStep = step
                viewModel.updateUiContextState("solver:field_0", "step_value_$step")
                Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
                assertTrue("Step $step synchronized before next update", latch.await(5, TimeUnit.SECONDS))
            }

            assertEquals(10, wakeCounts["solver:field_0"]?.get() ?: 0)
            for (j in 1 until 32) {
                assertEquals(
                    "Unrelated key solver:field_$j must have 0 wakeups",
                    0,
                    wakeCounts["solver:field_$j"]?.get() ?: 0
                )
            }
        } finally {
            jobs.forEach { it.cancel() }
            jobs.forEach { it.join() }
            viewModel.uiContextKeyEmissionObserverForTesting = null
        }

        assertEquals(0, viewModel.activeUiContextKeySlotCountForTesting())
    }

    @Test
    fun uiContextEntry_distinguishesPresentNullFromAbsent_andEvictsLruAt256(): Unit = runBlocking {
        val handle = SavedStateHandle()
        val viewModel = MathViewModel(app, savedStateHandle = handle)
        val subjectKey = "formula_library:selectedSubject"
        val observed = mutableListOf<UiContextEntry>()
        val initialLatch = CountDownLatch(1)
        val presentNullLatch = CountDownLatch(1)
        val absentAfterResetLatch = CountDownLatch(1)

        val lruObserved = mutableListOf<UiContextEntry>()
        val lruInitialLatch = CountDownLatch(1)
        val lruEvictLatch = CountDownLatch(1)

        val job1 = launch(Dispatchers.Main.immediate, start = CoroutineStart.UNDISPATCHED) {
            viewModel.observeUiContextKey(subjectKey).collect { entry ->
                observed.add(entry)
                when {
                    initialLatch.count > 0 -> initialLatch.countDown()
                    entry is UiContextEntry.Present && entry.value == null -> presentNullLatch.countDown()
                    entry is UiContextEntry.Absent && observed.size >= 3 -> absentAfterResetLatch.countDown()
                }
            }
        }
        val job2 = launch(Dispatchers.Main.immediate, start = CoroutineStart.UNDISPATCHED) {
            viewModel.observeUiContextKey("lru:key_0").collect { entry ->
                lruObserved.add(entry)
                when {
                    lruInitialLatch.count > 0 -> lruInitialLatch.countDown()
                    entry is UiContextEntry.Absent && lruObserved.any { it is UiContextEntry.Present } ->
                        lruEvictLatch.countDown()
                }
            }
        }

        try {
            assertTrue(initialLatch.await(5, TimeUnit.SECONDS))
            assertTrue(lruInitialLatch.await(5, TimeUnit.SECONDS))
            assertEquals(UiContextEntry.Absent, observed.first())

            // Explicitly store null ("সবগুলো") and distinguish Present(null) from Absent.
            viewModel.updateUiContextState<String?>(subjectKey, null)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertTrue(presentNullLatch.await(5, TimeUnit.SECONDS))
            assertTrue(viewModel.hasUiContextKey(subjectKey))
            assertNull(viewModel.getUiContextState<String?>(subjectKey, "FALLBACK"))

            // Reset explicitly transitions Present(null) -> Absent.
            viewModel.resetUiContextState(subjectKey)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertTrue(absentAfterResetLatch.await(5, TimeUnit.SECONDS))
            assertFalse(viewModel.hasUiContextKey(subjectKey))
            assertEquals("FALLBACK", viewModel.getUiContextState<String?>(subjectKey, "FALLBACK"))

            // Push 257 keys so the eldest key ("lru:key_0") triggers LRU eviction -> Absent.
            for (idx in 0..256) {
                viewModel.updateUiContextState("lru:key_$idx", idx)
            }
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertTrue("Eldest LRU key must emit Absent on eviction", lruEvictLatch.await(5, TimeUnit.SECONDS))
            assertFalse(viewModel.hasUiContextKey("lru:key_0"))
            assertTrue(viewModel.hasUiContextKey("lru:key_256"))
        } finally {
            job1.cancel()
            job2.cancel()
            job1.join()
            job2.join()
        }

        assertEquals(0, viewModel.activeUiContextKeySlotCountForTesting())
    }

    @Test
    fun setBookSearchQuery_updatesSavedStateHandleWithoutCallingEncodeStack() {
        val handle = SavedStateHandle()
        val viewModel = MathViewModel(app, savedStateHandle = handle)
        val encodeCalls = AtomicInteger(0)
        ScreenCodec.encodeStackObserverForTesting = { encodeCalls.incrementAndGet() }

        try {
            encodeCalls.set(0)
            val keystrokes = listOf("স", "সম", "সমী", "সমীক", "সমীকর", "সমীকরণ", "বী", "বীজ", "বীজগ", "বীজগণিত")
            keystrokes.forEach { query ->
                viewModel.setBookSearchQuery(query)
            }
            assertEquals(0, encodeCalls.get())
            assertEquals("বীজগণিত", handle.get<String>("phase2_book_search_query"))
            assertEquals("বীজগণিত", viewModel.bookSearchQuery.value)
        } finally {
            ScreenCodec.encodeStackObserverForTesting = null
        }
    }

    @Test
    fun screenCodec_measuresSizesAcrossDepths1To200_andNavCompactionPreservesHomeAndTail() {
        val depths = listOf(1, 10, 50, 100, 200)
        val sampleScreens = listOf(
            Screen.Home,
            Screen.Tables,
            Screen.TableDetail,
            Screen.FormulaLibrary,
            Screen.ProblemSolver,
            Screen.BcsPreparation,
            Screen.BookHub,
            Screen.BookDetail,
            Screen.BookChapterOverview,
            Screen.BookLesson,
            Screen.BookPdfReader,
            Screen.BookInteractiveLab
        )

        val measuredBytes = linkedMapOf<Int, Int>()
        depths.forEach { depth ->
            val frames = (0 until depth).map { i ->
                val screen = if (i == 0) Screen.Home else sampleScreens[i % sampleScreens.size]
                val snap = NavigationContextSnapshot(
                    screen = screen,
                    tableNumber = (i % 20) + 1,
                    formulaId = "sq_${(i % 5) + 1}",
                    selectedLibraryFormulaId = if (screen == Screen.FormulaLibrary) "lib_trig_1" else null,
                    tutorNumber = (i % 12) + 2,
                    bcsExamProfile = BcsExamProfile.BCS_50TH_PRELIM,
                    bcsSyllabusVersion = BcsSyllabusVersion.BCS_50TH_CURRENT,
                    bookId = BookId.CLASS_9_10,
                    bookChapterNumber = (i % 8) + 1,
                    bookLessonId = "c910_ch1_l1",
                    bookPrintedPage = (i % 40) + 1,
                    bookLabType = BookInteractiveLabType.TRIG_UNIT_CIRCLE_LAB,
                    routeKey = "${screen.route}:$i",
                    screenInstanceId = "${screen.route}#$i"
                )
                NavigationFrame(screen = screen, snapshot = snap)
            }

            val encoded = ScreenCodec.encodeStack(frames, nextInstanceCounter = depth + 1)
            val byteSize = encoded.toByteArray(Charsets.UTF_8).size
            measuredBytes[depth] = byteSize

            val decoded = ScreenCodec.decodeStack(encoded, sanitizeTransientOnRestore = false)
            assertEquals(depth, decoded.frames.size)
            assertEquals(Screen.Home, decoded.frames.first().screen)
            assertEquals(frames.last().screen, decoded.frames.last().screen)
            assertEquals(frames.last().snapshot.routeKey, decoded.frames.last().snapshot.routeKey)
            val originalPdf = frames.lastOrNull { it.screen == Screen.BookPdfReader }
            val decodedPdf = decoded.frames.lastOrNull { it.screen == Screen.BookPdfReader }
            if (originalPdf != null && decodedPdf != null) {
                assertEquals(originalPdf.snapshot.bookPrintedPage, decodedPdf.snapshot.bookPrintedPage)
            }
        }

        // Verify payload size scales cleanly and stays well within Android Binder limits (< 100 KB at 200 frames).
        assertTrue((measuredBytes[1] ?: 0) < (measuredBytes[200] ?: 0))
        assertTrue((measuredBytes[200] ?: Int.MAX_VALUE) < 100_000)

        // Verify 256-frame compaction guard on MathViewModel preserves Screen.Home at index 0.
        val handle = SavedStateHandle()
        val viewModel = MathViewModel(app, savedStateHandle = handle)
        val cycleScreens = listOf(
            Screen.Tables,
            Screen.Primes,
            Screen.Formulas,
            Screen.FormulaLibrary,
            Screen.ProblemSolver,
            Screen.VisualExplorer,
            Screen.BookHub,
            Screen.BcsPreparation,
            Screen.Dashboard,
            Screen.Bookmarks
        )
        repeat(265) { step ->
            viewModel.navigateTo(cycleScreens[step % cycleScreens.size])
        }
        val finalStack = viewModel.screenStack.value
        assertEquals(256, finalStack.size)
        assertEquals(Screen.Home, finalStack.first())
        assertEquals(cycleScreens[264 % cycleScreens.size], finalStack.last())
        assertTrue(viewModel.navigateBack())
        assertEquals(255, viewModel.screenStack.value.size)
        assertEquals(Screen.Home, viewModel.screenStack.value.first())
    }
}
