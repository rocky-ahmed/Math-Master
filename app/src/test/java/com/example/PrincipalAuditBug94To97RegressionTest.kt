package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.repository.MathRepository
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookSearchFilterType
import com.example.math.FormulaInteractiveEngine
import com.example.math.FormulaLibraryProvider
import com.example.math.VisualKnowledgeCheckProvider
import com.example.math.VisualMathEngine
import com.example.ui.MathViewModel
import com.example.ui.screens.FormulaLibraryScreen
import com.example.ui.screens.VisualExplorerScreen
import com.example.ui.screens.isValidPdfFileHeader
import com.example.ui.screens.resolveOfflinePdfFile
import com.example.ui.theme.MathMasterTheme
import java.io.File
import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Principal Engineer Audit Regression Suite covering BUG-94 through BUG-97:
 * - BUG-94: Visual Math Explorer Pythagorean calculation, formatting precision, and diagram geometry
 * - BUG-95: Guided Discovery & Mini Knowledge Check responsive layout and formula context sync
 * - BUG-96: Formula Library card header FlowRow, horizontal difficulty badge, and nt_bdmo_5 cyclicity notation
 * - BUG-97: Class 6 PDF Reader lesson heading normalization ("পাঠ পাঠ" fix), page offset, and PDF cache recovery
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PrincipalAuditBug94To97RegressionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var repository: MathRepository

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        db = MathDatabase.getInstance(app)
        repository = MathRepository(db.mathDao())
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun createViewModel(): MathViewModel {
        val store = ViewModelStore()
        return ViewModelProvider(
            store,
            ViewModelProvider.AndroidViewModelFactory(app)
        )[MathViewModel::class.java]
    }

    @Test
    fun bug94_pythagorasCalculation_verifiesAllRequiredCasesAndDynamicUpdates() {
        val pythagoras = VisualMathEngine.formulaVisualizerList.first { it.id == "vis_pythagoras" }

        // Test 1: a = 8, b = 5 -> Expected c² = 64 + 25 = 89, c = √89 ≈ 9.433981 (৯.৪৩)
        val r8_5 = pythagoras.calculate(8.0, 5.0, 0.0)
        assertEquals(sqrt(89.0), r8_5.outputValue, 1e-6)
        assertEquals("৯.৪৩", VisualMathEngine.formatNumberBn(r8_5.outputValue, decimals = 2, keepTrailingZeros = true))
        assertTrue(r8_5.calculationStepsBn[0].contains("a = ৮") && r8_5.calculationStepsBn[0].contains("b = ৫"))
        assertTrue(r8_5.calculationStepsBn[1].contains("৮² + ৫²"))
        assertTrue(r8_5.calculationStepsBn[2].contains("৬৪ + ২৫ = ৮৯"))
        assertTrue(r8_5.calculationStepsBn[3].contains("√(৮৯) ≈ ৯.৪৩ একক"))
        assertFalse("Must never mix a=4 into a=8 calculation", r8_5.calculationStepsBn.any { it.contains("১৬") || it.contains("৪১") })
        assertTrue(r8_5.diagramNotesBn.contains("a = ৮, b = ৫, c ≈ ৯.৪৩"))

        // Test 2: a = 3, b = 4 -> Expected c = 5 (৫)
        val r3_4 = pythagoras.calculate(3.0, 4.0, 0.0)
        assertEquals(5.0, r3_4.outputValue, 1e-6)
        assertEquals("৫", VisualMathEngine.formatNumberBn(r3_4.outputValue, decimals = 2, keepTrailingZeros = false))
        assertTrue(r3_4.calculationStepsBn[2].contains("৯ + ১৬ = ২৫"))
        assertTrue(r3_4.calculationStepsBn[3].contains("√(২৫) = ৫ একক"))
        assertTrue(r3_4.diagramNotesBn.contains("a = ৩, b = ৪, c = ৫"))

        // Test 3: a = 5, b = 12 -> Expected c = 13 (১৩)
        val r5_12 = pythagoras.calculate(5.0, 12.0, 0.0)
        assertEquals(13.0, r5_12.outputValue, 1e-6)
        assertEquals("১৩", VisualMathEngine.formatNumberBn(r5_12.outputValue, decimals = 2, keepTrailingZeros = false))
        assertTrue(r5_12.calculationStepsBn[2].contains("২৫ + ১৪৪ = ১৬৯"))
        assertTrue(r5_12.calculationStepsBn[3].contains("√(১৬৯) = ১৩ একক"))
        assertTrue(r5_12.diagramNotesBn.contains("a = ৫, b = ১২, c = ১৩"))

        // Test 4: Repeated dynamic updates across the full 1..20 slider domain
        val testPairs = listOf(
            4.0 to 5.0,
            8.0 to 5.0,
            3.0 to 4.0,
            5.0 to 12.0,
            6.0 to 8.0,
            1.0 to 1.0,
            20.0 to 20.0
        )
        for ((a, b) in testPairs) {
            val res = pythagoras.calculate(a, b, 0.0)
            val expectedCSq = a * a + b * b
            val expectedC = sqrt(expectedCSq)
            assertEquals(expectedC, res.outputValue, 1e-6)
            val aBn = VisualMathEngine.formatNumberBn(a)
            val bBn = VisualMathEngine.formatNumberBn(b)
            val cSqBn = VisualMathEngine.formatNumberBn(expectedCSq)
            assertTrue(res.calculationStepsBn[0].contains("a = $aBn") && res.calculationStepsBn[0].contains("b = $bBn"))
            assertTrue(res.calculationStepsBn[2].contains("= $cSqBn"))
        }
    }

    @Test
    fun bug95_guidedDiscoveryAndMiniKnowledgeCheck_syncsWithFormulaAndPreventsVerticalButtonWrapping() {
        // Verify context-specific Guided Discovery invariant and question for Pythagoras on Tab 3
        val pythagorasInvariant = VisualKnowledgeCheckProvider.getDiscoveryInvariantBn(3, "vis_pythagoras")
        assertTrue(pythagorasInvariant.contains("সমকোণী ত্রিভুজের অতিভুজের"))
        assertTrue(pythagorasInvariant.contains("a² + b²"))

        val pythagorasChecks = VisualKnowledgeCheckProvider.getKnowledgeChecksForContext(3, "vis_pythagoras")
        assertTrue(pythagorasChecks.isNotEmpty())
        assertEquals("vkc_pythagoras_1", pythagorasChecks.first().id)
        assertTrue(pythagorasChecks.first().questionBn.contains("a = ৮ এবং ভূমি b = ৫"))
        assertEquals("৮৯ (৬৪ + ২৫)", pythagorasChecks.first().optionsBn[pythagorasChecks.first().correctIndex])

        // Verify UI layout in VisualExplorerScreen: toggle button is horizontal (width > height), not a vertical column
        val vm = createViewModel()
        composeTestRule.setContent {
            MathMasterTheme {
                VisualExplorerScreen(viewModel = vm)
            }
        }
        composeTestRule.waitForIdle()

        // Navigate to Tab 3 (Formula Visualizer)
        composeTestRule.onNodeWithTag("tab_formulas").performClick()
        composeTestRule.waitForIdle()

        val toggleBtnCollapsedBounds = composeTestRule.onNodeWithTag("btn_toggle_visual_kc")
            .assertIsDisplayed()
            .fetchSemanticsNode()
            .boundsInRoot
        assertTrue(
            "Collapsed toggle button must render horizontally (width > height), was ${toggleBtnCollapsedBounds.width}x${toggleBtnCollapsedBounds.height}",
            toggleBtnCollapsedBounds.width > toggleBtnCollapsedBounds.height
        )

        // Expand Mini Knowledge Check panel
        composeTestRule.onNodeWithTag("btn_toggle_visual_kc").performClick()
        composeTestRule.waitForIdle()

        val toggleBtnExpandedBounds = composeTestRule.onNodeWithTag("btn_toggle_visual_kc")
            .assertIsDisplayed()
            .fetchSemanticsNode()
            .boundsInRoot
        assertTrue(
            "Expanded toggle button must render horizontally (width > height), was ${toggleBtnExpandedBounds.width}x${toggleBtnExpandedBounds.height}",
            toggleBtnExpandedBounds.width > toggleBtnExpandedBounds.height
        )

        // All 4 options and 3 bottom action links are displayed without horizontal off-screen clipping
        for (idx in 0..3) {
            composeTestRule.onNodeWithTag("visual_kc_opt_$idx").assertIsDisplayed()
        }
        composeTestRule.onNodeWithTag("btn_visual_link_quiz").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_visual_link_formula").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_visual_link_solver").assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w360dp-h800dp-xxhdpi")
    fun bug96_formulaLibrary_difficultyBadgeRendersHorizontallyAndCyclicityFormulaIsUnambiguous() {
        val cyclicity = FormulaLibraryProvider.getFormulaById("nt_bdmo_5")
        assertNotNull(cyclicity)
        assertEquals(
            "a^b (mod 10) = (a mod 10)^(((b − 1) mod 4) + 1) mod 10",
            cyclicity!!.formulaExpression
        )

        // Verify interactive calculator evaluation for nt_bdmo_5
        val r7_222 = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to "7", "b" to "222"))
        assertTrue(r7_222.isSuccess)
        assertEquals("৯", r7_222.finalAnswerBn)

        val r7_2025 = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to "7", "b" to "2025"))
        assertTrue(r7_2025.isSuccess)
        assertEquals("৭", r7_2025.finalAnswerBn)

        val r2_4 = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to "2", "b" to "4"))
        assertTrue(r2_4.isSuccess)
        assertEquals("৬", r2_4.finalAnswerBn)

        // Verify UI rendering on a compact 360dp screen: difficulty badge in FormulaCard is a single horizontal line (lineCount == 1)
        val vm = createViewModel()
        composeTestRule.setContent {
            MathMasterTheme {
                FormulaLibraryScreen(viewModel = vm)
            }
        }
        composeTestRule.waitForIdle()

        val badgeNode = composeTestRule.onNodeWithTag("formula_difficulty_badge_lib_arith_01", useUnmergedTree = true)
            .assertIsDisplayed()
            .fetchSemanticsNode()
        val textLayouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        badgeNode.config[androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult].action?.invoke(textLayouts)
        assertTrue("TextLayoutResult must be available for difficulty badge", textLayouts.isNotEmpty())
        assertEquals(
            "Difficulty badge must render on a single horizontal line (never wrapping vertically)",
            1,
            textLayouts.first().lineCount
        )
        val badgeHeightDp = badgeNode.boundsInRoot.height / app.resources.displayMetrics.density
        assertTrue(
            "Difficulty badge height must be compact single-line (<= 36dp), was ${badgeHeightDp}dp",
            badgeHeightDp <= 36f
        )
    }

    @Test
    fun bug96_modularCyclicityInteractiveEngine_enforcesStrictDomainAndEuclideanModulo() {
        val cyclicity = FormulaLibraryProvider.getFormulaById("nt_bdmo_5")!!

        // 1. Existing positive integer cases — must continue passing
        val positiveCases = listOf(
            Triple("7", "1", "৭"),
            Triple("7", "2", "৯"),
            Triple("7", "3", "৩"),
            Triple("7", "4", "১"),
            Triple("7", "5", "৭"),
            Triple("7", "8", "১"),
            Triple("7", "222", "৯"),
            Triple("7", "2025", "৭"),
            Triple("2", "4", "৬"),
            Triple("৭", "২২২", "৯") // Bengali digits
        )
        for ((a, b, expectedBn) in positiveCases) {
            val res = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to a, "b" to b))
            assertTrue("Expected success for a=$a, b=$b", res.isSuccess)
            assertEquals("Mismatch for a=$a, b=$b", expectedBn, res.finalAnswerBn)
        }

        // 2. Zero base with positive exponent — must succeed and return ০
        val zeroBaseCases = listOf("1", "3")
        for (b in zeroBaseCases) {
            val res = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to "0", "b" to b))
            assertTrue("Expected success for a=0, b=$b", res.isSuccess)
            assertEquals("০", res.finalAnswerBn)
        }

        // 3. Negative integer bases with positive exponent — Euclidean modulo ((a % 10) + 10) % 10
        val negativeBaseCases = listOf(
            Triple("-3", "1", "৭"),
            Triple("-3", "2", "৯"),
            Triple("-2", "3", "২"),
            Triple("−৩", "১", "৭") // Unicode minus + Bengali digits
        )
        for ((a, b, expectedBn) in negativeBaseCases) {
            val res = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to a, "b" to b))
            assertTrue("Expected success for negative integer base a=$a, b=$b", res.isSuccess)
            assertEquals("Mismatch for negative base a=$a, b=$b", expectedBn, res.finalAnswerBn)
        }

        // 4. Invalid exponent cases — must fail (isSuccess = false) with non-blank Bengali message
        val invalidExponents = listOf("0", "০", "-2", "-১", "2.5", "২.৫", "", "abc")
        for (badB in invalidExponents) {
            val res = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to "7", "b" to badB))
            assertFalse("Exponent b='$badB' must be rejected", res.isSuccess)
            assertEquals("", res.finalAnswerBn)
            assertTrue("Exponent b='$badB' must provide Bengali error message", !res.errorMessageBn.isNullOrBlank())
        }

        // 5. Invalid base cases (fractional, blank, non-numeric) — must fail (isSuccess = false)
        val invalidBases = listOf("2.5", "-3.5", "২.৫", "", "xyz")
        for (badA in invalidBases) {
            val res = FormulaInteractiveEngine.evaluateFormula(cyclicity, mapOf("a" to badA, "b" to "3"))
            assertFalse("Base a='$badA' must be rejected", res.isSuccess)
            assertEquals("", res.finalAnswerBn)
            assertTrue("Base a='$badA' must provide Bengali error message", !res.errorMessageBn.isNullOrBlank())
        }

        // 6. Out-of-range / 64-bit Long overflow inputs — must fail safely without silent truncation
        val overflowResA = FormulaInteractiveEngine.evaluateFormula(
            cyclicity,
            mapOf("a" to "999999999999999999999999999", "b" to "3")
        )
        assertFalse("Overflow base must be rejected safely", overflowResA.isSuccess)
        assertTrue(!overflowResA.errorMessageBn.isNullOrBlank())

        val overflowResB = FormulaInteractiveEngine.evaluateFormula(
            cyclicity,
            mapOf("a" to "7", "b" to "999999999999999999999999999")
        )
        assertFalse("Overflow exponent must be rejected safely", overflowResB.isSuccess)
        assertTrue(!overflowResB.errorMessageBn.isNullOrBlank())
    }

    @Test
    fun bug97_bookReader_eliminatesDuplicatePathPrefixAndRecoversCorruptedPdfCache() {
        // 1. Verify Class 6 Page 1 structured heading has single "পাঠ" prefix, never "পাঠ পাঠ"
        val class6Page1 = BookCurriculumRepository.getStructuredPageContent(BookId.CLASS_6, 1)
        assertEquals(
            "পাঠ ১.১–১.২: অঙ্কপাতন এবং দেশীয় ও আন্তর্জাতিক গণনা রীতি",
            class6Page1.sectionHeadingBn
        )
        assertFalse(class6Page1.sectionHeadingBn.contains("পাঠ পাঠ"))

        // 2. Verify across all 4 textbooks and all lessons/search results that "পাঠ পাঠ" never occurs
        for (book in BookId.entries) {
            val chapters = BookCurriculumRepository.getChapters(book)
            for (ch in chapters) {
                val pageContent = BookCurriculumRepository.getStructuredPageContent(book, ch.printedPageStart)
                assertFalse(
                    "Duplicate 'পাঠ পাঠ' found in ${book.id} page ${ch.printedPageStart}: ${pageContent.sectionHeadingBn}",
                    pageContent.sectionHeadingBn.contains("পাঠ পাঠ")
                )
            }
            val searchResults = BookCurriculumRepository.searchAllBooks("", BookSearchFilterType.LESSON, book)
            for (res in searchResults) {
                assertFalse(
                    "Duplicate 'পাঠ পাঠ' found in search result ${res.id}: ${res.titleBn}",
                    res.titleBn.contains("পাঠ পাঠ")
                )
            }
        }

        // 3. Verify intentional Class 6 printed page 1 -> PDF page 6 (+5 front-matter offset) mapping
        assertEquals(6, BookId.CLASS_6.printedToPdfPage(1))
        assertEquals(1, BookId.CLASS_6.pdfToPrintedPage(6))

        // 4. Verify resolveOfflinePdfFile extracts a valid %PDF- file and auto-recovers if cache file was corrupted
        val corruptedCacheFile = File(app.cacheDir, "cached_${BookId.CLASS_6.id}.pdf")
        corruptedCacheFile.writeBytes(ByteArray(8192) { 0x41 }) // 8KB non-PDF garbage
        assertFalse("Garbage cache file must fail PDF header validation", isValidPdfFileHeader(corruptedCacheFile))

        val resolvedPdf = resolveOfflinePdfFile(app, BookId.CLASS_6, forceRefresh = false)
        assertNotNull("resolveOfflinePdfFile must recover and return valid PDF file", resolvedPdf)
        assertTrue("Recovered file must have valid %PDF- header", isValidPdfFileHeader(resolvedPdf!!))
        assertTrue("Recovered Class 6 PDF must be > 1MB", resolvedPdf.length() > 1_000_000L)
    }
}
