package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BcsMistakeEntity
import com.example.data.repository.MathRepository
import com.example.math.BookId
import com.example.math.MathProblemSolverEngine
import com.example.math.SharedMathEngine
import com.example.math.SolverMode
import com.example.ui.MathViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Principal Engineer Audit Regression Suite covering BUG-87 through BUG-92.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PrincipalAuditBug87To92RegressionTest {

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

    @Test
    fun bug87_parenthesizedNegativeBaseExponentAndImplicitMultiplication_evaluatesCorrectly() {
        // 1. 10 + (2 - 5)^2 = 10 + (-3)^2 = 10 + 9 = 19 (previously evaluated to 10 - 3^2 = 1)
        val r1 = MathProblemSolverEngine.solveArithmetic("10 + (2 - 5)^2", SolverMode.LEARNING)
        assertTrue(r1.isSuccess)
        assertEquals("১৯", r1.finalAnswerBn)

        // 2. 10 - (2 - 5)^2 = 10 - (-3)^2 = 10 - 9 = 1 (previously evaluated to 10 + 3^2 = 19)
        val r2 = MathProblemSolverEngine.solveArithmetic("10 - (2 - 5)^2", SolverMode.LEARNING)
        assertTrue(r2.isSuccess)
        assertEquals("১", r2.finalAnswerBn)

        // 3. (-3)^2 + (-2)^3 = 9 + (-8) = 1
        val r3 = MathProblemSolverEngine.solveArithmetic("(-3)^2 + (-2)^3", SolverMode.LEARNING)
        assertTrue(r3.isSuccess)
        assertEquals("১", r3.finalAnswerBn)

        // 4. -(-3)^2 = -9
        val r4 = MathProblemSolverEngine.solveArithmetic("-(-3)^2", SolverMode.LEARNING)
        assertTrue(r4.isSuccess)
        assertEquals("-৯", r4.finalAnswerBn)

        // 5. Implicit multiplication adjacent to parentheses: 2(3 + 4) = 14 and (2 + 3)(4 - 1)^2 = 45
        val r5 = MathProblemSolverEngine.solveArithmetic("2(3 + 4)", SolverMode.LEARNING)
        assertTrue(r5.isSuccess)
        assertEquals("১৪", r5.finalAnswerBn)

        val r6 = MathProblemSolverEngine.solveArithmetic("(2 + 3)(4 - 1)^2", SolverMode.LEARNING)
        assertTrue(r6.isSuccess)
        assertEquals("৪৫", r6.finalAnswerBn)
    }

    @Test
    fun bug88_radicalSimplificationLargeRadicandGuardOverflowAndNegativeCoeff() {
        // 1. Radicand above 10^12 is safely rejected immediately without hanging on trial division
        val huge = SharedMathEngine.simplify("√999999999999999989")
        assertFalse(huge.isSuccess)
        assertTrue(huge.errorMessageBn?.contains("১০¹²") == true)

        // 2. Large coefficient + radicand within bounds uses BigInteger in verificationBn without 64-bit Long overflow
        // outerCoeff = 100_000, radicand = 1_000_000_000_000 -> squaredMag = 10^10 * 10^12 = 10^22 > Long.MAX_VALUE
        val largeValid = SharedMathEngine.simplify("100000√1000000000000")
        assertTrue(largeValid.isSuccess)
        assertFalse("verificationBn must not contain negative overflow", largeValid.verificationBn.contains("-"))
        assertTrue(largeValid.verificationBn.contains("১০০০০০০০০০০০০০০০০০০০০০০"))

        // 3. Negative outer coefficients: -√72 = -6√2 and -2√50 = -10√2
        val neg1 = SharedMathEngine.simplify("-√72")
        assertTrue(neg1.isSuccess)
        assertTrue(neg1.simplifiedFormBn.startsWith("-৬√২"))

        val neg2 = SharedMathEngine.simplify("-2√50")
        assertTrue(neg2.isSuccess)
        assertTrue(neg2.simplifiedFormBn.startsWith("-১০√২"))
    }

    @Test
    fun bug89_factorizeDifferenceOfSquaresWithPrefixAndScaledMiddleTerms() {
        // 1. Difference of squares with common scalar factor: 2x^2 - 8 -> ২(x − ২)(x + ২) in both step and final answer
        val diffSq = SharedMathEngine.factorize("2x^2 - 8")
        assertTrue(diffSq.isSuccess)
        assertEquals("২(x − ২)(x + ২)", diffSq.factoredFormBn)
        assertTrue(
            "Step 1 must include common factor prefix ২",
            diffSq.stepsBn.first().second.contains("= ২(x − ২)(x + ২)")
        )

        // 2. Scaled quadratic middle-term breakup: 2x^2 + 10x + 12 -> m1 + m2 = 10 (6 and 4)
        val scaledQuad = SharedMathEngine.factorize("2x^2 + 10x + 12")
        assertTrue(scaledQuad.isSuccess)
        assertEquals("২(x + ২)(x + ৩)", scaledQuad.factoredFormBn)
        val step1Text = scaledQuad.stepsBn.first().second
        assertTrue(
            "Middle terms for 2x^2 + 10x + 12 must be ৬ and ৪ (summing to ১০)",
            step1Text.contains("৬") && step1Text.contains("৪")
        )

        // 3. Negative leading coefficient quadratic: -x^2 + 5x - 6 -> m1 + m2 = 5 (3 and 2)
        val negQuad = SharedMathEngine.factorize("-x^2 + 5x - 6")
        assertTrue(negQuad.isSuccess)
        assertTrue(negQuad.factoredFormBn.startsWith("-"))
        val negStep1Text = negQuad.stepsBn.first().second
        assertTrue(
            "Middle terms for -x^2 + 5x - 6 must be ২ and ৩ (summing to ৫)",
            negStep1Text.contains("২") && negStep1Text.contains("৩") && !negStep1Text.contains("-৩")
        )
    }

    @Test
    fun bug90_bookReaderPreservesZoomScaleAndAvoidsSamePageCountInflation() = runBlocking {
        val store = ViewModelStore()
        try {
            val vm = ViewModelProvider(
                store,
                ViewModelProvider.AndroidViewModelFactory(app)
            )[MathViewModel::class.java]

            // First open: page 10, totalPagesRead = 1
            repository.updateBookReadingState(
                bookId = BookId.CLASS_9_10.id,
                printedPage = 10,
                pdfPage = BookId.CLASS_9_10.printedToPdfPage(10),
                chapterNumber = 1,
                incrementPagesRead = true,
                lastZoomScale = 1.85f
            )
            val afterFirst = db.mathDao().getBookReadingStateByBookId(BookId.CLASS_9_10.id)
            assertNotNull(afterFirst)
            assertEquals(1, afterFirst!!.totalPagesRead)
            assertEquals(1.85f, afterFirst.lastZoomScale, 0.001f)

            // Reopen same page via ViewModel.updateBookReaderPage without explicit zoomScale
            vm.updateBookReaderPage(
                bookId = BookId.CLASS_9_10,
                printedPage = 10,
                chapterNumber = 1
            )
            repeat(30) {
                Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
                Thread.sleep(10)
            }

            val afterSamePageReopen = db.mathDao().getBookReadingStateByBookId(BookId.CLASS_9_10.id)
            assertNotNull(afterSamePageReopen)
            assertEquals("Zoom scale must be preserved on reopen", 1.85f, afterSamePageReopen!!.lastZoomScale, 0.001f)
            assertEquals("totalPagesRead must not inflate when reopening same page", 1, afterSamePageReopen.totalPagesRead)

            // Navigate to a new page (page 11): totalPagesRead increments to 2 and zoomScale remains 1.85f
            vm.updateBookReaderPage(
                bookId = BookId.CLASS_9_10,
                printedPage = 11,
                chapterNumber = 1
            )
            repeat(30) {
                Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
                Thread.sleep(10)
            }
            val afterNewPage = db.mathDao().getBookReadingStateByBookId(BookId.CLASS_9_10.id)
            assertNotNull(afterNewPage)
            assertEquals(1.85f, afterNewPage!!.lastZoomScale, 0.001f)
            assertEquals(2, afterNewPage.totalPagesRead)
        } finally {
            store.clear()
        }
    }

    @Test
    fun bug91_editingExistingBookNotePreservesCreatedAtTimestamp() = runBlocking {
        val t1 = 1_700_000_000_000L
        val t2 = 1_700_000_500_000L

        repository.saveBookNote(
            id = 0,
            bookId = BookId.CLASS_9_10.id,
            chapterNumber = 2,
            lessonId = "2.1",
            printedPage = 25,
            titleBn = "সেট ও ফাংশন নোট",
            contentBn = "প্রাথমিক নোট",
            nowMs = t1
        )

        val createdNotes = db.mathDao().getAllBookNotes().first()
        assertEquals(1, createdNotes.size)
        val savedNote = createdNotes.first()
        assertEquals(t1, savedNote.createdAt)
        assertEquals(t1, savedNote.updatedAt)

        // Edit existing note at t2 > t1
        repository.saveBookNote(
            id = savedNote.id,
            bookId = BookId.CLASS_9_10.id,
            chapterNumber = 2,
            lessonId = "2.1",
            printedPage = 25,
            titleBn = "সেট ও ফাংশন আপডেটেড নোট",
            contentBn = "সংশোধিত বিস্তারিত নোট",
            nowMs = t2
        )

        val updatedNote = db.mathDao().getBookNoteById(savedNote.id)
        assertNotNull(updatedNote)
        assertEquals("Original createdAt must be preserved on edit", t1, updatedNote!!.createdAt)
        assertEquals("updatedAt must advance to edit timestamp", t2, updatedNote.updatedAt)
        assertEquals("সেট ও ফাংশন আপডেটেড নোট", updatedNote.titleBn)
    }

    @Test
    fun bug92_deleteBcsMistakeRemovesBothProfileScopedAndLegacyRawId() = runBlocking {
        val rawQId = "bcs_q_real_numbers_01"
        val nonDefaultProfile = "BCS_46TH_PRELIM"
        val scopedId = "$nonDefaultProfile::$rawQId"

        // Insert both a legacy unprefixed row and a profile-scoped row
        db.mathDao().upsertBcsMistake(
            BcsMistakeEntity(
                questionId = rawQId,
                subjectTrack = "MATH",
                topicId = "REAL_NUMBERS",
                questionPromptBn = "মূলদ সংখ্যা কোনটি?",
                wrongAnswerBn = "√2",
                correctAnswerBn = "√4",
                explanationBn = "√4 = 2",
                shortcutTipBn = "",
                mistakeCount = 1,
                lastMistakeAt = 1000L,
                examProfileId = "BCS_50TH_PRELIM_2025"
            )
        )
        db.mathDao().upsertBcsMistake(
            BcsMistakeEntity(
                questionId = scopedId,
                subjectTrack = "MATH",
                topicId = "REAL_NUMBERS",
                questionPromptBn = "মূলদ সংখ্যা কোনটি?",
                wrongAnswerBn = "√2",
                correctAnswerBn = "√4",
                explanationBn = "√4 = 2",
                shortcutTipBn = "",
                mistakeCount = 1,
                lastMistakeAt = 2000L,
                examProfileId = nonDefaultProfile
            )
        )

        // Delete passing scopedId while nonDefaultProfile is active
        repository.deleteBcsMistake(questionId = scopedId, examProfileId = nonDefaultProfile)

        assertNull("Profile-scoped row must be deleted", db.mathDao().getBcsMistakeById(scopedId))
        assertNull("Legacy unprefixed rawQId row must also be deleted", db.mathDao().getBcsMistakeById(rawQId))
    }
}
