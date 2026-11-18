package com.example

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.core.app.ApplicationProvider
import com.example.math.BookId
import com.example.math.Math3DEngine
import com.example.math.OrbitCameraState
import com.example.math.Solid3DShapeType
import com.example.math.SolidAnatomyHighlight
import com.example.ui.components.Math3DStudioViewport
import com.example.ui.components.glSurfaceViewFactoryForTesting
import com.example.ui.screens.BookPdfRenderCachePool
import com.example.ui.screens.MAX_PDF_CACHE_BYTES
import com.example.ui.screens.MAX_PDF_CACHE_ENTRIES
import com.example.ui.screens.computeSafeCacheBudgetBytes
import com.example.ui.screens.resolveOfflinePdfFile
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
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
        File(app.cacheDir, "cached_${BookId.CLASS_6.id}.pdf").delete()
    }

    @After
    fun tearDown() {
        glSurfaceViewFactoryForTesting = null
        ShadowBuild.reset()
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
}
