package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BookmarkEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.repository.MathRepository
import com.example.math.BcsExamProfile
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookInteractiveLabType
import com.example.math.BookQuestionEngine
import com.example.math.FormulaInteractiveEngine
import com.example.math.FormulaLibraryProvider
import com.example.math.MathProblemSolverEngine
import com.example.math.MentalMathCategory
import com.example.math.MentalMathDifficulty
import com.example.math.MentalMathMode
import com.example.math.NumberSystemProvider
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.math.PracticeLabGenerator
import com.example.math.PrimeNumberProvider
import com.example.math.QuestionInputFormat
import com.example.math.QuizTopic
import com.example.math.SimilarProblemGenerator
import com.example.math.SolutionStep
import com.example.math.SolverAdvancedAlgebraAndInterestEngine
import com.example.math.SolverMode
import com.example.math.VisualKnowledgeCheckProvider
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.customization.AppCustomizationState
import com.example.ui.customization.CustomLayoutType
import com.example.ui.customization.CustomizationPreferences
import com.example.ui.customization.GlobalCustomConfig
import com.example.ui.customization.SectionCustomConfig
import com.example.ui.screens.PracticeLabPlayScreen
import com.example.ui.screens.computeLabExplanationLines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
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
import java.util.Locale
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Comprehensive regression test suite verifying all defects discovered and fixed
 * during the full codebase bug audit (BUG-A01 through BUG-A10).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ComprehensiveAuditBugFixRegressionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var repository: MathRepository

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
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
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun awaitCondition(timeoutMs: Long = 3000L, predicate: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (predicate()) return
            Thread.sleep(25)
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertTrue("Condition not met within ${timeoutMs}ms", predicate())
    }

    // -------------------------------------------------------------------------
    // BUG-A01: CustomizationPreferences grid migration must preserve sections
    // -------------------------------------------------------------------------
    @Test
    fun bugA01_gridMigration_preservesSavedPerSectionCustomizations() {
        val prefsManager = CustomizationPreferences(app)
        val savedSection = SectionCustomConfig(
            iconId = "quiz",
            iconColorHex = "#FF2563EB",
            cornerRadiusDp = 24,
            borderThicknessDp = 2
        )
        val initialState = AppCustomizationState(
            global = GlobalCustomConfig(layoutType = CustomLayoutType.COMFORTABLE_LIST),
            sections = mapOf("section_quizzes" to savedSection)
        )
        prefsManager.saveCustomizationState(initialState)
        // Simulate pre-migration state where migration flag has not yet run
        app.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("has_migrated_default_grid_v2", false)
            .commit()

        val migrated = prefsManager.loadCustomizationState()
        assertEquals(CustomLayoutType.GRID, migrated.global.layoutType)
        assertTrue(
            "Per-section customizations must be preserved during grid migration",
            migrated.sections.containsKey("section_quizzes")
        )
        assertEquals("quiz", migrated.sections["section_quizzes"]?.iconId)
        assertEquals(24, migrated.sections["section_quizzes"]?.cornerRadiusDp)

        // Reload a second time to verify persisted prefs still contain the section map
        val reloaded = prefsManager.loadCustomizationState()
        assertTrue(reloaded.sections.containsKey("section_quizzes"))
    }

    // -------------------------------------------------------------------------
    // BUG-A02: deleteExamAttempt serialized under dailyStudyWriteMutex
    // -------------------------------------------------------------------------
    @Test
    fun bugA02_concurrentDeleteExamAttemptAndDailyStudy_noLostUpdates() = runBlocking(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val dateKey = MathRepository.todayDateKey(now)
        val attempts = (1..8).map { i ->
            ExamAttemptEntity(
                attemptId = "exam_conc_$i",
                examTitleBn = "পরীক্ষা $i",
                examType = "TOPIC_EXAM",
                selectedTopicsCsv = "ARITHMETIC",
                difficulty = "MEDIUM",
                questionType = "MCQ",
                totalQuestions = 10,
                durationLimitSeconds = 600,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.25,
                status = "COMPLETED",
                currentQuestionIndex = 9,
                elapsedSeconds = 120,
                remainingSeconds = 480,
                questionsJson = "[]",
                answersJson = "{}",
                markedForReviewCsv = "",
                visitedIndicesCsv = "0,1,2,3,4,5,6,7,8,9",
                questionTimeSecondsJson = "{}",
                correctCount = 8,
                wrongCount = 2,
                unansweredCount = 0,
                earnedMarks = 8.0,
                negativeMarksDeducted = 0.5,
                finalScore = 7.5,
                percentage = 75.0,
                accuracyPercent = 80.0,
                startedAt = now - 2000L,
                updatedAt = now,
                completedAt = now
            )
        }
        attempts.forEach { repository.finalizeExamAttempt(it, emptyList()) }
        val beforeRow = db.mathDao().getDailyStudyActivityByDate(dateKey)
        assertNotNull(beforeRow)
        val initialExamsCompleted = beforeRow!!.examsCompleted
        val initialStudySeconds = beforeRow.studyTimeSeconds

        // Concurrently delete 4 exam attempts and record 6 daily activity increments
        val jobs = (1..4).map { i ->
            async(Dispatchers.IO) { repository.deleteExamAttempt("exam_conc_$i") }
        } + (1..6).map {
            async(Dispatchers.IO) {
                repository.recordDailyStudyDelta(
                    questionsAttemptedDelta = 3,
                    correctDelta = 2,
                    wrongDelta = 1,
                    studyTimeSecondsDelta = 30,
                    examsCompletedDelta = 0,
                    practiceSessionsCompletedDelta = 1,
                    timestampMs = now
                )
            }
        }
        jobs.awaitAll()

        val afterRow = db.mathDao().getDailyStudyActivityByDate(dateKey)
        assertNotNull(afterRow)
        assertEquals(
            "examsCompleted must equal initial - 4 deleted with zero lost updates",
            initialExamsCompleted - 4,
            afterRow!!.examsCompleted
        )
        assertEquals(
            "practiceSessionsCompleted must equal 6 with zero lost updates",
            6,
            afterRow.practiceSessionsCompleted
        )
        assertEquals(
            "studyTimeSeconds must equal initial - 4*120 + 6*30 with zero lost updates",
            initialStudySeconds - (4 * 120) + (6 * 30),
            afterRow.studyTimeSeconds
        )
        assertEquals(4, db.mathDao().getCompletedExamAttempts().first().size)
    }

    // -------------------------------------------------------------------------
    // BUG-A03: unresolveUnifiedMistake syncs bcs_mistake_notebook symmetrically
    // -------------------------------------------------------------------------
    @Test
    fun bugA03_unresolveUnifiedMistake_syncsBcsMistakeNotebookForDefaultAndCustomProfiles() = runBlocking(Dispatchers.IO) {
        val defaultProfile = BcsExamProfile.BCS_50TH_PRELIM.id
        val customProfile = BcsExamProfile.BCS_WRITTEN_REGULAR.id
        val qId1 = "bcs_q_sync_1"
        val qId2 = "bcs_q_sync_2"

        repository.recordBcsMistake(
            questionId = qId1,
            subjectTrack = "MATH",
            topicId = "algebra",
            questionPromptBn = "প্রশ্ন ১",
            wrongAnswerBn = "৪",
            correctAnswerBn = "৫",
            explanationBn = "ব্যাখ্যা ১",
            examProfileId = defaultProfile
        )
        repository.recordBcsMistake(
            questionId = qId2,
            subjectTrack = "MATH",
            topicId = "geometry",
            questionPromptBn = "প্রশ্ন ২",
            wrongAnswerBn = "১০",
            correctAnswerBn = "১২",
            explanationBn = "ব্যাখ্যা ২",
            examProfileId = customProfile
        )

        val unifiedKey1 = "BCS_${defaultProfile}_$qId1"
        val unifiedKey2 = "BCS_${customProfile}_$qId2"

        repository.resolveUnifiedMistake(unifiedKey1)
        repository.resolveUnifiedMistake(unifiedKey2)
        assertTrue(db.mathDao().getBcsMistakeById(qId1)!!.isResolved)
        assertTrue(db.mathDao().getBcsMistakeById("$customProfile::$qId2")!!.isResolved)
        assertTrue(db.mathDao().getUnifiedMistakeByKey(unifiedKey1)!!.resolved)
        assertTrue(db.mathDao().getUnifiedMistakeByKey(unifiedKey2)!!.resolved)

        // Now unresolve via unified mistake center
        repository.unresolveUnifiedMistake(unifiedKey1)
        repository.unresolveUnifiedMistake(unifiedKey2)

        assertFalse(
            "Default-profile BCS mistake must be unresolved in bcs_mistake_notebook",
            db.mathDao().getBcsMistakeById(qId1)!!.isResolved
        )
        assertFalse(
            "Custom-profile BCS mistake must be unresolved in bcs_mistake_notebook",
            db.mathDao().getBcsMistakeById("$customProfile::$qId2")!!.isResolved
        )
        assertFalse(db.mathDao().getUnifiedMistakeByKey(unifiedKey1)!!.resolved)
        assertFalse(db.mathDao().getUnifiedMistakeByKey(unifiedKey2)!!.resolved)
    }

    // -------------------------------------------------------------------------
    // BUG-A04: Solver percentage sign, Long.MIN_VALUE divisibility, square/cube root
    // -------------------------------------------------------------------------
    @Test
    fun bugA04_solverPercentageNegativeOldVal_divisibilityMinValue_squareAndCubeRoot() {
        // 1. Percentage increase from -100 to -50 must be +50% বৃদ্ধি, not -50% বৃদ্ধি
        val pctRes = MathProblemSolverEngine.solvePercentage(
            MathProblemSolverEngine.PercentageType.INCREASE_DECREASE,
            -100.0,
            -50.0,
            SolverMode.LEARNING
        )
        assertTrue(pctRes.isSuccess)
        assertFalse(
            "Percentage change must not be negative: ${pctRes.finalAnswerBn}",
            pctRes.finalAnswerBn.contains("-")
        )
        assertTrue(pctRes.finalAnswerBn.contains("৫০%") && pctRes.finalAnswerBn.contains("বৃদ্ধি"))

        // 2. Long.MIN_VALUE divisibility by 2 must explain last digit 8 (even/divisible), not 7 (odd)
        val divRes = MathProblemSolverEngine.solveDivisibility(Long.MIN_VALUE, 2, SolverMode.LEARNING)
        assertTrue(divRes.isSuccess)
        assertTrue(divRes.finalAnswerBn.contains("বিভাজ্য"))
        val ruleStep = divRes.steps.joinToString("\n") { it.expressionOrMathBn + " " + it.explanationBn }
        assertTrue("Explanation for Long.MIN_VALUE mod 2 must reference last digit 8: $ruleStep", ruleStep.contains("8"))
        assertFalse("Explanation must not say odd/not divisible for Long.MIN_VALUE: $ruleStep", ruleStep.contains("বিজোড়"))

        // 3. Square of -5 must not claim √২৫ = -৫
        val sqRes = MathProblemSolverEngine.solveSquareCube(-5.0, MathProblemSolverEngine.SquareCubeOp.SQUARE, SolverMode.LEARNING)
        assertTrue(sqRes.isSuccess)
        assertEquals("২৫", sqRes.finalAnswerBn)
        assertFalse(
            "Square verification must not claim principal square root is negative: ${sqRes.verificationBn}",
            sqRes.verificationBn?.contains("√২৫ = -৫") == true
        )

        // 4. Irrational cube root must set isExactResult = false; perfect cube sets true
        val cbrtIrrational = MathProblemSolverEngine.solveSquareCube(2.0, MathProblemSolverEngine.SquareCubeOp.CUBE_ROOT, SolverMode.LEARNING)
        assertTrue(cbrtIrrational.isSuccess)
        assertFalse("³√2 is irrational so isExactResult must be false", cbrtIrrational.isExactResult)

        val cbrtExact = MathProblemSolverEngine.solveSquareCube(27.0, MathProblemSolverEngine.SquareCubeOp.CUBE_ROOT, SolverMode.LEARNING)
        assertTrue(cbrtExact.isSuccess)
        assertTrue("³√27 is exact so isExactResult must be true", cbrtExact.isExactResult)
        assertEquals("৩", cbrtExact.finalAnswerBn)
    }

    // -------------------------------------------------------------------------
    // BUG-A05: Concurrent ThreadLocal DecimalFormat in SolverAdvancedAlgebraAndInterestEngine
    // -------------------------------------------------------------------------
    @Test
    fun bugA05_concurrentAdvancedAlgebraAndInterestFormatting_isThreadSafe() {
        val pool = Executors.newFixedThreadPool(8)
        try {
            val tasks = (1..80).map { i ->
                Callable {
                    val p = 1000.0 + (i % 7) * 123.45
                    val r = 7.25 + (i % 5) * 0.35
                    val t = 2.5
                    val res = SolverAdvancedAlgebraAndInterestEngine.solveCompoundInterest(p, r, t, 2, SolverMode.LEARNING)
                    assertTrue(res.isSuccess)
                    assertTrue(res.finalAnswerBn.isNotBlank())
                    res.finalAnswerBn
                }
            }
            val results = pool.invokeAll(tasks).map { it.get() }
            assertEquals(results[0], results[35])
            assertEquals(results[1], results[36])
        } finally {
            pool.shutdownNow()
        }
    }

    // -------------------------------------------------------------------------
    // BUG-A06: PracticeLabPlayScreen must not leak correct answer in placeholder
    // -------------------------------------------------------------------------
    @Test
    fun bugA06_practiceLabPlayScreen_doesNotLeakCorrectAnswerInPlaceholder() {
        val vm = MathViewModel(app)
        vm.startPracticeSession(
            category = PracticeCategory.PERCENTAGES,
            difficulty = PracticeDifficulty.EASY,
            count = 10,
            inputFormat = QuestionInputFormat.KEYPAD
        )
        val currentQ = vm.practiceSession.value.questions.first()

        composeRule.setContent {
            PracticeLabPlayScreen(viewModel = vm)
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("input_practice_custom_answer").assertExists()
        composeRule.onNodeWithText("যেমন: ${currentQ.correctAnswerBn}", useUnmergedTree = true).assertDoesNotExist()
    }

    // -------------------------------------------------------------------------
    // BUG-A07: FormulaInteractiveEngine Unicode minus, lib_* IDs, Locale.US, serializer, typo
    // -------------------------------------------------------------------------
    @Test
    fun bugA07_formulaInteractiveEngine_localeSafety_serializerAndTypoFixes() {
        val origLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.FRANCE)

            // 1. lib_alg_02: (a − b)² with a=7, b=3 => 16 (previously computed (7+3)^2 = 100!)
            val alg02 = FormulaLibraryProvider.allFormulas.first { it.id == "lib_alg_02" }
            val rAlg02 = FormulaInteractiveEngine.evaluateFormula(alg02, mapOf("a" to "7", "b" to "3"))
            assertTrue(rAlg02.isSuccess)
            assertEquals("১৬", rAlg02.finalAnswerBn)

            // 2. lib_alg_03: a² − b² with a²=7, b²=3 => 40 (previously computed (7+3)^2 = 100!)
            val alg03 = FormulaLibraryProvider.allFormulas.first { it.id == "lib_alg_03" }
            val rAlg03 = FormulaInteractiveEngine.evaluateFormula(alg03, mapOf("a²" to "7", "b²" to "3"))
            assertTrue(rAlg03.isSuccess)
            assertEquals("৪০", rAlg03.finalAnswerBn)

            // 3. lib_nt_01: GCD and LCM
            val nt01 = FormulaLibraryProvider.allFormulas.first { it.id == "lib_nt_01" }
            val specsNt = FormulaInteractiveEngine.getVariableSpecsForFormula(nt01)
            val rNt01 = FormulaInteractiveEngine.evaluateFormula(
                nt01,
                mapOf(specsNt[0].symbol to "12", specsNt[1].symbol to "18", specsNt[2].symbol to "6")
            )
            assertTrue(rNt01.isSuccess)
            assertTrue(rNt01.finalAnswerBn.contains("গ.সা.গু = ৬") && rNt01.finalAnswerBn.contains("ল.সা.গু = ৩৬"))

            // 4. lib_prob_01: Probability with Locale.FRANCE must use dot decimal (০.৩৩), no comma
            val prob01 = FormulaLibraryProvider.allFormulas.first { it.id == "lib_prob_01" }
            val specsProb = FormulaInteractiveEngine.getVariableSpecsForFormula(prob01)
            val rProb01 = FormulaInteractiveEngine.evaluateFormula(
                prob01,
                mapOf(specsProb[0].symbol to "1", specsProb[1].symbol to "3", specsProb[2].symbol to "2")
            )
            assertTrue(rProb01.isSuccess)
            assertFalse("Must not emit comma decimal under Locale.FRANCE: ${rProb01.finalAnswerBn}", rProb01.finalAnswerBn.contains(","))
            assertTrue(rProb01.finalAnswerBn.contains("০.৩৩"))

            // 5. lib_seq_01: AM & GM (a=4, b=16 => AM=10, GM=8)
            val seq01 = FormulaLibraryProvider.allFormulas.first { it.id == "lib_seq_01" }
            val specsSeq1 = FormulaInteractiveEngine.getVariableSpecsForFormula(seq01)
            val rSeq01 = FormulaInteractiveEngine.evaluateFormula(
                seq01,
                mapOf(specsSeq1[0].symbol to "4", specsSeq1[1].symbol to "16")
            )
            assertTrue(rSeq01.isSuccess)
            assertEquals("AM = ১০, GM = ৮", rSeq01.finalAnswerBn)

            // 6. lib_seq_02: Infinite GP S∞ = a/(1-r) with |r|<1 validation
            val seq02 = FormulaLibraryProvider.allFormulas.first { it.id == "lib_seq_02" }
            val rSeq02Valid = FormulaInteractiveEngine.evaluateFormula(seq02, mapOf("a" to "1", "r" to "0.5"))
            assertTrue(rSeq02Valid.isSuccess)
            assertEquals("২", rSeq02Valid.finalAnswerBn)

            val rSeq02Invalid = FormulaInteractiveEngine.evaluateFormula(seq02, mapOf("a" to "1", "r" to "1.5"))
            assertFalse("Infinite GP must reject |r| >= 1", rSeq02Invalid.isSuccess)
        } finally {
            Locale.setDefault(origLocale)
        }

        // 7. Serializer must safely round-trip steps/inputs even if raw strings contain STEP_SEP / FIELD_SEP
        val hostileStep = SolutionStep(
            stepIndex = 1,
            titleBn = "ধাপ\u001E১\u001Fপরীক্ষা",
            expressionOrMathBn = "a\u001Eb",
            explanationBn = "ব্যাখ্যা\u001F\u001E"
        )
        val roundTrippedSteps = SimilarProblemGenerator.deserializeSteps(
            SimilarProblemGenerator.serializeSteps(listOf(hostileStep))
        )
        assertEquals(1, roundTrippedSteps.size)
        assertEquals(1, roundTrippedSteps[0].stepIndex)

        val roundTrippedInputs = SimilarProblemGenerator.deserializeInputs(
            SimilarProblemGenerator.serializeInputs(mapOf("k\u001E1" to "v\u001F\u001E1", "k2" to "v2"))
        )
        assertEquals(2, roundTrippedInputs.size)

        // 8. VisualKnowledgeCheckProvider must contain no stray Russian word "откуда"
        val allChecks = (0..4).flatMap { VisualKnowledgeCheckProvider.getKnowledgeChecksForTab(it) }
        assertTrue(allChecks.none { it.explanationBn.contains("откуда") })
    }

    // -------------------------------------------------------------------------
    // BUG-A08: Prime Magic Code 1-100 must have 10 digits summing to 25
    // -------------------------------------------------------------------------
    @Test
    fun bugA08_primeMagicCode1To100_displayMatches10DigitsSummingTo25() {
        val info = PrimeNumberProvider.magicCode1To100
        val displayDigits = info.codeDisplayEn.filter { it.isDigit() }
        assertEquals("4422322321", displayDigits)
        assertEquals(10, displayDigits.length)
        assertEquals(25, displayDigits.sumOf { it.digitToInt() })
        assertFalse(info.formattedCodeBn.contains("২২২"))
        assertFalse(info.tipBn.contains("222"))
    }

    // -------------------------------------------------------------------------
    // BUG-A09: MathViewModel authoritative toggles, PDF page restore, mental math input, exit persistence
    // -------------------------------------------------------------------------
    @Test
    fun bugA09_viewModelAuthoritativeMutations_typedMentalMathValidation_andExitNavPersistence() {
        val handle = SavedStateHandle()
        val vm = MathViewModel(app, savedStateHandle = handle)

        // 1. toggleBookmark works cleanly twice without active bookmarkedIds collector
        val bm = BookmarkEntity(
            id = "bm_cold_1",
            category = "FORMULA",
            titleBn = "সূত্র",
            subtitleBn = "উপশিরোনাম",
            detailBn = "বিস্তারিত"
        )
        vm.toggleBookmark(bm)
        awaitCondition { runBlocking(Dispatchers.IO) { db.mathDao().isBookmarked("bm_cold_1") } }
        vm.toggleBookmark(bm)
        awaitCondition { runBlocking(Dispatchers.IO) { !db.mathDao().isBookmarked("bm_cold_1") } }

        // 2. toggleLearned toggles authoritatively without active progressList collector
        vm.toggleLearned("table_7")
        awaitCondition { runBlocking(Dispatchers.IO) { db.mathDao().getProgressByItemId("table_7")?.isLearned == true } }
        vm.toggleLearned("table_7")
        awaitCondition { runBlocking(Dispatchers.IO) { db.mathDao().getProgressByItemId("table_7")?.isLearned == false } }

        // 3. openBookPdfReader without collector restores persisted page rather than clobbering it to page 1
        runBlocking(Dispatchers.IO) {
            repository.updateBookReadingState(
                bookId = BookId.CLASS_6.id,
                printedPage = 42,
                pdfPage = BookId.CLASS_6.printedToPdfPage(42),
                chapterNumber = 3
            )
        }
        vm.openBookPdfReader(bookId = BookId.CLASS_6)
        awaitCondition { vm.selectedBookPrintedPage.value == 42 }
        val storedPage = runBlocking(Dispatchers.IO) {
            db.mathDao().getBookReadingStateByBookId(BookId.CLASS_6.id)?.lastPrintedPage
        }
        assertEquals(42, storedPage)

        // 4. submitMentalMathTypedAnswer rejects "-" without recording -999999 mistake
        vm.startMentalMathChallenge(
            category = MentalMathCategory.ADDITION,
            difficulty = MentalMathDifficulty.EASY,
            mode = MentalMathMode.FIXED_QUESTIONS,
            durationOrCount = 5,
            inputFormat = QuestionInputFormat.KEYPAD
        )
        vm.setMentalMathTypedInput("-")
        vm.submitMentalMathTypedAnswer()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        assertFalse("Invalid input '-' must not submit the question", vm.mentalMathSession.value.isAnswerSubmitted)
        val mistakes = runBlocking(Dispatchers.IO) { db.mathDao().getAllUnifiedMistakes().first() }
        assertTrue(
            "Invalid typed input must never persist sentinel -999999 into mistake notebook",
            mistakes.none { it.userAnswerBn.contains("৯৯৯৯৯৯") }
        )

        // 5. exitMentalMathToSetup, exitQuizToTopicList, exitPracticeToCategories persist popped stack to SavedStateHandle
        vm.exitMentalMathToSetup()
        val restoredAfterMentalExit = MathViewModel(app, savedStateHandle = handle)
        assertEquals(Screen.MentalMath, restoredAfterMentalExit.currentScreen)

        vm.navigateTo(Screen.Quizzes)
        vm.startQuiz(QuizTopic.MULTIPLICATION, 5)
        vm.exitQuizToTopicList()
        val restoredAfterQuizExit = MathViewModel(app, savedStateHandle = handle)
        assertEquals(Screen.Quizzes, restoredAfterQuizExit.currentScreen)

        vm.navigateTo(Screen.PracticeLab)
        vm.startPracticeSession(PracticeCategory.ARITHMETIC, PracticeDifficulty.EASY)
        vm.exitPracticeToCategories()
        val restoredAfterPracticeExit = MathViewModel(app, savedStateHandle = handle)
        assertEquals(Screen.PracticeLab, restoredAfterPracticeExit.currentScreen)
    }

    // -------------------------------------------------------------------------
    // BUG-A10: gcd(Long.MIN_VALUE, 0), distinct book options, unique div-by-9, Locale.US lab lines
    // -------------------------------------------------------------------------
    @Test
    fun bugA10_mathAndGeneratorEdgeCases_gcdMinValue_distinctBookOptions_uniqueDiv9_andLabLocale() {
        // 1. gcd with Long.MIN_VALUE and 0 must never return negative Long.MIN_VALUE
        assertEquals(Long.MAX_VALUE, NumberSystemProvider.gcd(Long.MIN_VALUE, 0L))
        assertEquals(Long.MAX_VALUE, NumberSystemProvider.gcd(0L, Long.MIN_VALUE))
        assertEquals(Long.MAX_VALUE, NumberSystemProvider.gcd(Long.MIN_VALUE, Long.MIN_VALUE))
        assertEquals(4L, NumberSystemProvider.gcd(Long.MIN_VALUE, 12L))

        // 2. Every BookQuestionEngine chapter has 4 strictly distinct MCQ options (including Class 9-10 Ch 12 where answer=2)
        for (bookId in BookId.entries) {
            val chapters = BookCurriculumRepository.getChapters(bookId)
            for (ch in chapters) {
                val qs = BookQuestionEngine.getQuestionsForChapter(
                    bookId = bookId,
                    chapterNumber = ch.chapterNumber
                )
                for (q in qs) {
                    if (q.optionsBn.size >= 4) {
                        assertEquals(
                            "Duplicate MCQ options in ${bookId.id} ch${ch.chapterNumber} q=${q.id}: ${q.optionsBn}",
                            q.optionsBn.size,
                            q.optionsBn.distinct().size
                        )
                    }
                }
            }
        }

        // 3. PracticeLab divisibility-by-9 MEDIUM questions always have a unique missing digit in 1..8
        val divMedQuestions = PracticeLabGenerator.generateSession(
            category = PracticeCategory.DIVISIBILITY,
            difficulty = PracticeDifficulty.MEDIUM,
            count = 40
        )
        for (q in divMedQuestions) {
            val ans = com.example.math.BengaliNumberFormatter.toEnglishDigits(q.correctAnswerBn).trim().toInt()
            assertTrue(
                "Divisibility-by-9 missing digit must be uniquely in 1..8 (not ambiguous 0/9), got $ans",
                ans in 1..8
            )
        }

        // 4. computeLabExplanationLines must format decimals with dot under Locale.FRANCE
        val origLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.FRANCE)
            val labTypes = listOf(
                BookInteractiveLabType.FRACTION_AND_RATIO_LAB,
                BookInteractiveLabType.GEOMETRY_INTERACTIVE_LAB,
                BookInteractiveLabType.PYTHAGORAS_THEOREM_VISUALIZER,
                BookInteractiveLabType.TRIGONOMETRY_HEIGHT_DISTANCE_LAB,
                BookInteractiveLabType.MENSURATION_CALCULATOR
            )
            for (lab in labTypes) {
                val lines = computeLabExplanationLines(lab, 3, 4, 30)
                val joined = lines.joinToString("\n")
                assertFalse(
                    "Locale.FRANCE comma decimal leaked in $lab: $joined",
                    Regex("\\d,\\d").containsMatchIn(joined)
                )
            }
        } finally {
            Locale.setDefault(origLocale)
        }
    }
}
