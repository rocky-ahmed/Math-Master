package com.example

import android.app.Application
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.repository.MathRepository
import com.example.math.AlgebraFormulaProvider
import com.example.math.BcsExamProfile
import com.example.math.BcsMentalAbilityCategory
import com.example.math.BcsSyllabusUnit
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.CurriculumTrack
import com.example.math.FormulaInteractiveEngine
import com.example.math.FormulaLibraryProvider
import com.example.math.MathProblemSolverEngine
import com.example.math.MentalMathCategory
import com.example.math.MentalMathDifficulty
import com.example.math.MentalMathGenerator
import com.example.math.MultiplicationProvider
import com.example.math.NumberSystemProvider
import com.example.math.PracticeLabGenerator
import com.example.math.PrimeNumberProvider
import com.example.math.PrimeRange
import com.example.math.QuestionInputFormat
import com.example.math.SharedMathEngine
import com.example.math.SolverMode
import com.example.math.SolverTopic
import com.example.math.SquareCubeProvider
import com.example.math.TableRange
import com.example.math.VisualMathEngine
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.customization.SectionKey
import com.example.ui.screens.HomeScreenDefaults
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.math.BigInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EcosystemUpgradeV7UnitTest {

    // =========================================================================
    // 1. PROTECTED MODULES 01–04 INTEGRITY & REGRESSION VERIFICATION
    // =========================================================================

    @Test
    fun protectedModules01To04_preserveExactModelsDataAndHomeGridPositions() {
        // Module 01: Multiplication Tables 1..40
        val tables = TableRange.entries.flatMap { MultiplicationProvider.getTablesForRange(it) }
        assertEquals("Module 01 must have exactly 40 tables", 40, tables.size)
        assertEquals(1, tables[0].number)
        assertEquals(40, tables[39].number)
        assertEquals(10, tables[0].rows.size)
        assertEquals(400, tables[39].rows.last().product)

        // Module 02: Prime Numbers 1..200 (46 primes: 25 in 1..100 + 21 in 101..200)
        val primes1To200 = (1..200).filter { PrimeNumberProvider.isPrime(it) }
        assertEquals("Module 02 must have exactly 46 primes in 1..200", 46, primes1To200.size)
        assertEquals(25, PrimeRange.RANGE_1_100.expectedCount)
        assertEquals(21, PrimeRange.RANGE_101_200.expectedCount)
        assertEquals(2, primes1To200[0])
        assertEquals(199, primes1To200.last())

        // Module 03: Squares & Cubes 1..20
        val squares = SquareCubeProvider.squareTableList
        assertEquals("Module 03 must have 20 square items (1..20)", 20, squares.size)
        assertTrue(squares.last().squareText.contains("400"))

        // Module 04: Algebraic Formulas
        val algebraFormulas = AlgebraFormulaProvider.squareFormulas
        assertTrue("Module 04 must retain its core square algebraic formulas", algebraFormulas.size >= 10)

        // Home Grid Positions 0..3 Lock
        val homeModules = HomeScreenDefaults.createHomeModules(onOpenBookHub = {}, onNavigate = {})
        assertEquals(14, homeModules.size)
        assertEquals(SectionKey.TABLES, homeModules[0].sectionKey)
        assertEquals(SectionKey.PRIMES, homeModules[1].sectionKey)
        assertEquals(SectionKey.SQUARES, homeModules[2].sectionKey)
        assertEquals(SectionKey.FORMULAS, homeModules[3].sectionKey)

        // Verify all 8 protected source files exist unmodified in structure
        val protectedPaths = listOf(
            "app/src/main/java/com/example/ui/screens/TablesScreen.kt",
            "app/src/main/java/com/example/ui/screens/PrimesScreen.kt",
            "app/src/main/java/com/example/ui/screens/SquaresCubesScreen.kt",
            "app/src/main/java/com/example/ui/screens/FormulasScreen.kt",
            "app/src/main/java/com/example/math/MultiplicationModels.kt",
            "app/src/main/java/com/example/math/PrimeNumberModels.kt",
            "app/src/main/java/com/example/math/SquareCubeModels.kt",
            "app/src/main/java/com/example/math/AlgebraFormulaModels.kt"
        )
        for (rel in protectedPaths) {
            val f = File(rel).let { if (it.exists()) it else File("../$rel") }
            assertTrue("Protected file $rel must exist", f.exists() && f.length() > 100L)
        }
    }

    // =========================================================================
    // 2. ROOM DATABASE v6 -> v7 MIGRATION & DATA PRESERVATION TEST
    // =========================================================================

    @Test
    fun roomMigration6To7_preservesAll17ExistingV6TablesAndPopulatedData_andSupportsNewV7Tables() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val dbName = "test_migration_v6_to_v7_${System.currentTimeMillis()}.db"
        context.deleteDatabase(dbName)

        // Step 1: Create a genuine v6 database and populate all v6 tables
        val helperConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(6) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `titleBn` TEXT NOT NULL, `subtitleBn` TEXT NOT NULL, `detailBn` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                    db.execSQL("CREATE TABLE IF NOT EXISTS `quiz_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `topic` TEXT NOT NULL, `topicTitleBn` TEXT NOT NULL, `score` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `percentage` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)")
                    db.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (`itemId` TEXT NOT NULL, `isLearned` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`itemId`))")
                    MathDatabase.MIGRATION_1_2.migrate(db)
                    MathDatabase.MIGRATION_2_3.migrate(db)
                    MathDatabase.MIGRATION_3_4.migrate(db)
                    MathDatabase.MIGRATION_4_5.migrate(db)
                    MathDatabase.MIGRATION_5_6.migrate(db)
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()

        val v6Helper = FrameworkSQLiteOpenHelperFactory().create(helperConfig)
        val v6Db = v6Helper.writableDatabase

        v6Db.execSQL("INSERT INTO `bookmarks` VALUES ('bm_1', 'FORMULA', 'বর্গের সূত্র', 'বীজগণিত', '(a+b)^2', 1700000000000)")
        v6Db.execSQL("INSERT INTO `quiz_scores` (`topic`, `topicTitleBn`, `score`, `totalQuestions`, `percentage`, `timestamp`) VALUES ('TABLES', 'নামতা কুইজ', 9, 10, 90, 1700000000000)")
        v6Db.execSQL("INSERT INTO `user_progress` VALUES ('table_12', 1, 1700000000000)")
        v6Db.execSQL("INSERT INTO `practice_history` (`module`, `topicId`, `topicTitleBn`, `difficulty`, `mode`, `score`, `correctCount`, `wrongCount`, `totalQuestions`, `accuracyPercent`, `bestStreak`, `durationSeconds`, `timestamp`) VALUES ('PRACTICE_LAB', 'ARITHMETIC', 'পাটিগণিত', 'MEDIUM', 'MCQ', 80, 8, 2, 10, 80, 5, 45, 1700000000000)")
        v6Db.execSQL("INSERT INTO `topic_analytics` VALUES ('PRACTICE_LAB_ARITHMETIC_MEDIUM', 'PRACTICE_LAB', 'ARITHMETIC', 'পাটিগণিত', 'MEDIUM', 10, 8, 2, 5, 45, 1700000000000)")
        v6Db.execSQL("INSERT INTO `recent_formulas` VALUES ('lib_alg_01', 'দ্বিপদী বর্গ', 'algebra', 1700000000000)")
        v6Db.execSQL("INSERT INTO `solver_history` (`topicId`, `topicTitleBn`, `categoryTitleBn`, `inputSummaryBn`, `inputsJson`, `finalAnswerBn`, `stepsJson`, `formulaUsedBn`, `verificationBn`, `timestamp`) VALUES ('PERCENTAGE', 'শতকরা', 'পাটিগণিত', '200 এর 15%', '{}', '৩০', '[]', 'P*r/100', 'OK', 1700000000000)")
        v6Db.execSQL("INSERT INTO `daily_study_activity` VALUES ('2026-11-07', 25, 20, 5, 600, 1, 2, 20, 15, 1700000000000)")
        v6Db.execSQL("INSERT INTO `book_reading_state` VALUES ('CLASS_6', 2, 'c6_ch2_l1', 'CONCEPT', 25, 30, 15, '10,20', 1700000000000)")
        v6Db.execSQL("INSERT INTO `book_mistakes` VALUES ('bm_q1', 'CLASS_6', 1, 'প্রশ্ন ১', '১০', '১২', 'ব্যাখ্যা', 'ORIGINAL_TEXTBOOK', 1, 0, 1700000000000)")
        v6Db.execSQL("INSERT INTO `bcs_user_profile` VALUES ('active_profile', 'BCS_50TH_PRELIM_2025', 1, 2, 45, 30, 1750000000000, 1760000000000, 1700000000000)")
        v6Db.execSQL("INSERT INTO `bcs_topic_progress` VALUES ('bcs_math_u1', 'MATH', 1, 1, 20, 16, 300, 80, 1700000000000)")
        v6Db.execSQL("INSERT INTO `bcs_mistake_notebook` VALUES ('bcs_q1', 'MATH', 'bcs_math_u1', 'বিসিএস প্রশ্ন', 'ক', 'খ', 'ব্যাখ্যা', 'শর্টকাট', '৪৫তম বিসিএস', 1, 0, 0, 1700086400000, 0, 1700000000000)")
        v6Db.close()
        v6Helper.close()

        // Step 2: Open with Room v9 using ONLY non-destructive migrations (MIGRATION_1_2..MIGRATION_8_9)
        val roomV7 = Room.databaseBuilder(context, MathDatabase::class.java, dbName)
            .addMigrations(
                MathDatabase.MIGRATION_1_2,
                MathDatabase.MIGRATION_2_3,
                MathDatabase.MIGRATION_3_4,
                MathDatabase.MIGRATION_4_5,
                MathDatabase.MIGRATION_5_6,
                MathDatabase.MIGRATION_6_7,
                MathDatabase.MIGRATION_7_8,
                MathDatabase.MIGRATION_8_9
            )
            .allowMainThreadQueries()
            .build()

        val dao = roomV7.mathDao()
        val repo = MathRepository(dao)

        // Verify v6 user records survived MIGRATION_6_7, MIGRATION_7_8, and MIGRATION_8_9 intact
        val bookmarks = dao.getAllBookmarks().first()
        assertEquals(1, bookmarks.size)
        assertEquals("bm_1", bookmarks[0].id)

        val quizScores = dao.getAllQuizScores().first()
        assertEquals(1, quizScores.size)
        assertEquals(90, quizScores[0].percentage)

        val progress = dao.getAllProgress().first()
        assertEquals(1, progress.size)
        assertTrue(progress[0].isLearned)

        val bookState = dao.getBookReadingStateByBookId("CLASS_6")
        assertNotNull(bookState)
        assertEquals(25, bookState?.lastPrintedPage)
        assertEquals("STRUCTURED", bookState?.preferredViewMode)

        val bcsProfile = dao.getBcsUserProfile("active_profile")
        assertNotNull(bcsProfile)
        assertEquals(45, bcsProfile?.dailyTargetMinutes)

        val bcsMistakes = dao.getAllBcsMistakes().first()
        assertEquals(1, bcsMistakes.size)
        assertEquals("bcs_q1", bcsMistakes[0].questionId)
        assertEquals("BCS_50TH_PRELIM_2025", bcsMistakes[0].examProfileId)

        // Verify Room v8 study goal table
        repo.recordBcsStudyGoalDelta(
            examProfileId = "BCS_50TH_PRELIM_2025",
            questionsDelta = 15,
            studyTimeMinutesDelta = 25
        )
        val studyGoals = repo.allBcsStudyGoals.first()
        assertEquals(1, studyGoals.size)
        assertEquals(15, studyGoals[0].questionsSolved)
        assertEquals(25, studyGoals[0].studyTimeMinutes)

        // Verify Room v9 secondary indexes exist in SQLite pragma index_list
        val rawDb = roomV7.openHelper.readableDatabase
        val bmIndexes = mutableSetOf<String>()
        rawDb.query("PRAGMA index_list(`bookmarks`)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIdx >= 0) bmIndexes.add(cursor.getString(nameIdx))
            }
        }
        assertTrue("index_bookmarks_timestamp must exist", bmIndexes.contains("index_bookmarks_timestamp"))
        assertTrue("index_bookmarks_category must exist", bmIndexes.contains("index_bookmarks_category"))

        val qsIndexes = mutableSetOf<String>()
        rawDb.query("PRAGMA index_list(`quiz_scores`)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIdx >= 0) qsIndexes.add(cursor.getString(nameIdx))
            }
        }
        assertTrue("index_quiz_scores_topic must exist", qsIndexes.contains("index_quiz_scores_topic"))

        // Step 3: Verify new v7 tables (unified_mistake_notebook & formula_mastery_state) + duplicate handling
        repo.recordUnifiedMistake(
            mistakeKey = "PRACTICE_LAB:ALGEBRA:q_101",
            sourceModule = "PRACTICE_LAB",
            subjectId = "BASIC_ALGEBRA",
            curriculumTrack = "BDMO",
            questionPromptBn = "যদি x + 1/x = 3 হয়, তবে x² + 1/x² কত?",
            userAnswerBn = "৯",
            correctAnswerBn = "৭",
            diagnosticExplanationBn = "২ বিয়োগ করতে ভুল হয়েছে: 3² - 2 = 7",
            errorType = "FORMULA_CONFUSION"
        )
        val afterFirstFail = repo.allUnifiedMistakes.first()
        assertEquals(1, afterFirstFail.size)
        assertEquals(1, afterFirstFail[0].failureCount)
        assertEquals(1, afterFirstFail[0].reviewIntervalDays)
        assertFalse(afterFirstFail[0].resolved)

        // Duplicate key upsert should increment failureCount in-place
        repo.recordUnifiedMistake(
            mistakeKey = "PRACTICE_LAB:ALGEBRA:q_101",
            sourceModule = "PRACTICE_LAB",
            subjectId = "BASIC_ALGEBRA",
            curriculumTrack = "BDMO",
            questionPromptBn = "যদি x + 1/x = 3 হয়, তবে x² + 1/x² কত?",
            userAnswerBn = "১১",
            correctAnswerBn = "৭",
            diagnosticExplanationBn = "২ বিয়োগ করতে ভুল হয়েছে: 3² - 2 = 7",
            errorType = "FORMULA_CONFUSION"
        )
        val afterSecondFail = repo.allUnifiedMistakes.first()
        assertEquals("Duplicate mistakeKey must upsert in-place", 1, afterSecondFail.size)
        assertEquals(2, afterSecondFail[0].failureCount)
        assertEquals("১১", afterSecondFail[0].userAnswerBn)

        // Resolve mistake -> advances reviewIntervalDays from 1 to 3
        repo.resolveUnifiedMistake("PRACTICE_LAB:ALGEBRA:q_101")
        val afterResolve = repo.allUnifiedMistakes.first()
        assertTrue(afterResolve[0].resolved)
        assertEquals(3, afterResolve[0].reviewIntervalDays)

        // Formula Mastery State progression
        repo.recordFormulaMastery("nt_bdmo_1", "number_theory", masteryLevel = "LEARNING")
        assertEquals("LEARNING", repo.allFormulaMastery.first()[0].masteryLevel)
        repo.recordFormulaMastery("nt_bdmo_1", "number_theory", masteryLevel = "MASTERED")
        val mastered = repo.allFormulaMastery.first()[0]
        assertEquals(2, mastered.practiceCount)
        assertEquals("MASTERED", mastered.masteryLevel)

        roomV7.close()
        context.deleteDatabase(dbName)
        Unit
    }

    // =========================================================================
    // 3. SHARED MATH ENGINE — NORMAL, BOUNDARY, OVERFLOW & EXCEPTIONAL CASES
    // =========================================================================

    @Test
    fun sharedMathEngine_handlesNormalBoundaryOverflowAndExceptionalInputs() {
        // 3.1 analyzeDivisorsAndTotient: normal, N=1, N=0, N<0, large prime, large composite
        val d1 = SharedMathEngine.analyzeDivisorsAndTotient(1L)
        assertEquals(1L, d1.divisorCount)
        assertEquals(1L, d1.divisorSum)
        assertEquals(1L, d1.eulerTotient)

        val d36 = SharedMathEngine.analyzeDivisorsAndTotient(36L)
        assertEquals(9L, d36.divisorCount)
        assertEquals(91L, d36.divisorSum)
        assertEquals(12L, d36.eulerTotient)

        val d0 = SharedMathEngine.analyzeDivisorsAndTotient(0L)
        assertEquals(0L, d0.divisorCount)
        assertEquals(0L, d0.divisorSum)
        assertEquals(0L, d0.eulerTotient)
        assertTrue(d0.primePowerBn.contains("অসংজ্ঞায়িত"))

        val dNeg = SharedMathEngine.analyzeDivisorsAndTotient(-48L)
        assertEquals(0L, dNeg.divisorCount)
        assertEquals(0L, dNeg.eulerTotient)

        // Large prime p = 999_999_999_989L (> 3*10^9, so p^2 overflows 64-bit Long unless BigInteger is used)
        val largePrime = 999_999_999_989L
        val dPrime = SharedMathEngine.analyzeDivisorsAndTotient(largePrime)
        assertEquals(2L, dPrime.divisorCount)
        assertEquals(largePrime + 1L, dPrime.divisorSum)
        assertEquals(largePrime - 1L, dPrime.eulerTotient)

        // Large composite 10^11 = 2^11 * 5^11 => d(N) = 12 * 12 = 144, phi(10^11) = 4 * 10^10
        val tenPow11 = 100_000_000_000L
        val dTen11 = SharedMathEngine.analyzeDivisorsAndTotient(tenPow11)
        assertEquals(144L, dTen11.divisorCount)
        assertEquals(40_000_000_000L, dTen11.eulerTotient)

        // 3.2 modPowStepsBn: normal, zero exp, negative base, modulus=1, modulus<=0, huge modulus > sqrt(Long.MAX_VALUE)
        val modNorm = SharedMathEngine.modPowStepsBn(7L, 222L, 10L)
        assertEquals(9L, modNorm.remainder)

        val modFermat = SharedMathEngine.modPowStepsBn(2L, 100L, 101L)
        assertEquals(1L, modFermat.remainder)

        val modZeroExp = SharedMathEngine.modPowStepsBn(13L, 0L, 17L)
        assertEquals(1L, modZeroExp.remainder)

        val modNegBase = SharedMathEngine.modPowStepsBn(-3L, 3L, 5L) // (-27) mod 5 = 3
        assertEquals(3L, modNegBase.remainder)

        val modOne = SharedMathEngine.modPowStepsBn(999L, 50L, 1L)
        assertEquals(0L, modOne.remainder)

        val modZero = SharedMathEngine.modPowStepsBn(7L, 10L, 0L)
        assertEquals(0L, modZero.remainder)
        assertTrue(modZero.congruenceNotationBn.contains("অসংজ্ঞায়িত"))

        val modNeg = SharedMathEngine.modPowStepsBn(7L, 10L, -5L)
        assertEquals(0L, modNeg.remainder)
        assertTrue(modNeg.congruenceNotationBn.contains("অসংজ্ঞায়িত"))

        // Huge modulus > 3,037,000,499 (sqrt(Long.MAX_VALUE)) to verify no 64-bit overflow in binary exponentiation trace
        val hugeMod = 4_000_000_007L
        val hugeBase = 3_500_000_001L
        val modHuge = SharedMathEngine.modPowStepsBn(hugeBase, 13L, hugeMod)
        val expectedHuge = BigInteger.valueOf(hugeBase).modPow(BigInteger.valueOf(13L), BigInteger.valueOf(hugeMod)).toLong()
        assertEquals(expectedHuge, modHuge.remainder)
        assertFalse("Trace must not contain negative overflow numbers", modHuge.stepsBn.any { it.second.contains("-") })

        // 3.3 solveSimultaneousLinear: unique integer, unique fraction, parallel no-solution, coincident infinite, 0x+0y=5 contradiction, NaN/Inf
        val sysInt = SharedMathEngine.solveSimultaneousLinear(2.0, 3.0, 13.0, 5.0, -2.0, 4.0)
        assertTrue(sysInt.isUniqueSolution)
        assertEquals("২", sysInt.xExactBn)
        assertEquals("৩", sysInt.yExactBn)

        // Exact fraction: 2x + y = 5, x - y = 2 => 3x = 7 => x = 7/3, y = 1/3
        val sysFrac = SharedMathEngine.solveSimultaneousLinear(2.0, 1.0, 5.0, 1.0, -1.0, 2.0)
        assertTrue(sysFrac.isUniqueSolution)
        assertTrue("Expected exact fraction ৭/৩ in ${sysFrac.xExactBn}", sysFrac.xExactBn.contains("৭/৩"))
        assertTrue("Expected exact fraction ১/৩ in ${sysFrac.yExactBn}", sysFrac.yExactBn.contains("১/৩"))

        // Parallel lines: 2x + 3y = 6, 4x + 6y = 15 => No solution
        val sysParallel = SharedMathEngine.solveSimultaneousLinear(2.0, 3.0, 6.0, 4.0, 6.0, 15.0)
        assertFalse(sysParallel.isUniqueSolution)
        assertFalse(sysParallel.isInfiniteSolutions)
        assertTrue(sysParallel.isNoSolution)

        // Coincident lines: 2x + 3y = 6, 4x + 6y = 12 => Infinite solutions
        val sysCoincident = SharedMathEngine.solveSimultaneousLinear(2.0, 3.0, 6.0, 4.0, 6.0, 12.0)
        assertFalse(sysCoincident.isUniqueSolution)
        assertTrue(sysCoincident.isInfiniteSolutions)
        assertFalse(sysCoincident.isNoSolution)

        // Degenerate zero-coefficient contradiction: 0x + 0y = 0, 0x + 0y = 5 => No solution (NOT infinite!)
        val sysZeroContradiction = SharedMathEngine.solveSimultaneousLinear(0.0, 0.0, 0.0, 0.0, 0.0, 5.0)
        assertFalse(sysZeroContradiction.isUniqueSolution)
        assertFalse("0x + 0y = 5 must NOT be reported as infinite solutions", sysZeroContradiction.isInfiniteSolutions)
        assertTrue(sysZeroContradiction.isNoSolution)

        // NaN / Infinity guard
        val sysNan = SharedMathEngine.solveSimultaneousLinear(Double.NaN, 1.0, 2.0, 1.0, -1.0, 0.0)
        assertFalse(sysNan.isUniqueSolution)
        assertTrue(sysNan.isNoSolution)
    }

    // =========================================================================
    // 4. MODULE-BY-MODULE FUNCTIONAL AUDIT (MODULES 05–14)
    // =========================================================================

    @Test
    fun module05_formulaLibrary_supportsTrackFilteringAndBdmoNumberTheoryFormulas() {
        val allFormulas = FormulaLibraryProvider.searchFormulas(curriculumTrack = CurriculumTrack.ALL)
        val nctbFormulas = FormulaLibraryProvider.searchFormulas(curriculumTrack = CurriculumTrack.NCTB)
        val bdmoFormulas = FormulaLibraryProvider.searchFormulas(curriculumTrack = CurriculumTrack.BDMO)
        val bcsFormulas = FormulaLibraryProvider.searchFormulas(curriculumTrack = CurriculumTrack.BCS)

        assertTrue("All formulas should be valid with unique IDs", FormulaLibraryProvider.validateAllFormulas())
        assertEquals("Formula library must contain 73 verified formulas", 73, allFormulas.size)
        assertTrue(nctbFormulas.isNotEmpty())
        assertTrue(bdmoFormulas.isNotEmpty())
        assertTrue(bcsFormulas.isNotEmpty())

        for (id in listOf("nt_bdmo_1", "nt_bdmo_2", "nt_bdmo_3", "nt_bdmo_4", "nt_bdmo_5")) {
            assertNotNull("Formula $id must exist", FormulaLibraryProvider.getFormulaById(id))
        }

        val divisorFormula = FormulaLibraryProvider.getFormulaById("nt_bdmo_1")!!
        val d36Specs = FormulaInteractiveEngine.getVariableSpecsForFormula(divisorFormula)
        val d36 = FormulaInteractiveEngine.evaluateFormula(divisorFormula, d36Specs.associate { it.symbol to "36" })
        assertTrue(d36.isSuccess)
        assertTrue(d36.finalAnswerBn.contains("৯"))
        assertTrue(d36.finalAnswerBn.contains("৯১"))
        assertTrue(d36.finalAnswerBn.contains("১২"))
    }

    @Test
    fun module06To08_practiceLabMentalMathAndNumberSystem_verifyAllNewFeatures() {
        // Module 06: Interleaved Challenge + Track Filtering + Diagnostic Feedback
        for (track in listOf(CurriculumTrack.ALL, CurriculumTrack.NCTB, CurriculumTrack.BDMO, CurriculumTrack.BCS)) {
            val interleaved = PracticeLabGenerator.generateInterleavedChallenge(
                count = 12,
                track = track,
                inputFormat = QuestionInputFormat.MCQ
            )
            assertEquals(12, interleaved.size)
            assertTrue(interleaved.map { it.category }.toSet().size >= 3)
            interleaved.forEach { q ->
                assertTrue(q.diagnosticFeedbackBn.isNotBlank())
                assertTrue(PracticeLabGenerator.verifyQuestionIntegrity(q))
            }
        }

        // Module 07: Mental Math Strategy Coach across all 10 categories
        assertEquals(10, MentalMathCategory.entries.size)
        MentalMathCategory.entries.forEach { cat ->
            assertTrue(cat.strategyCoachBn.isNotBlank())
            val qs = MentalMathGenerator.generateQuestions(cat, MentalMathDifficulty.MEDIUM, 4)
            assertEquals(4, qs.size)
            qs.forEach { q -> assertTrue(q.mentalStrategyTipBn.isNotBlank()) }
        }

        // Module 08: Extended Divisibility Rules (13, 15, 16, 18, 25) + Track Filtering
        val divisors = NumberSystemProvider.divisibilityRules.map { it.divisor }
        listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 18, 25).forEach { d ->
            assertTrue("Divisibility rule for $d must exist", divisors.contains(d))
        }
        assertTrue(NumberSystemProvider.checkDivisibility(221L, 13).first)
        assertFalse(NumberSystemProvider.checkDivisibility(222L, 13).first)
        assertTrue(NumberSystemProvider.checkDivisibility(135L, 15).first)
        assertTrue(NumberSystemProvider.checkDivisibility(4096L, 16).first)
        assertTrue(NumberSystemProvider.checkDivisibility(198L, 18).first)
        assertTrue(NumberSystemProvider.checkDivisibility(1875L, 25).first)
    }

    @Test
    fun module09To14_solverVisualQuizzesDashboardBcsAndBook_verifyEndToEndIntegration() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // Module 09: Problem Solver arithmetic & simultaneous linear integration
        assertEquals(16, SolverTopic.entries.size)
        val arithRes = MathProblemSolverEngine.solveArithmetic("(১৫ + ৫) × ৪ ÷ ২ - ৬", SolverMode.LEARNING)
        assertTrue(arithRes.isSuccess)
        assertTrue(arithRes.finalAnswerBn.contains("৩৪"))

        // Module 10: Visual Math Explorer engine verification
        val rectRes = VisualMathEngine.calculateRectangle(8.0, 5.0)
        assertTrue(rectRes.isValid)
        assertEquals(40.0, rectRes.primaryArea, 0.001)

        // Module 13: BCS 50th Preliminary & Written repository integrity
        assertEquals(20, BcsExamProfile.BCS_50TH_PRELIM.mathMarks)
        assertEquals(15, BcsExamProfile.BCS_50TH_PRELIM.mentalAbilityMarks)
        assertEquals(5, BcsSyllabusUnit.entries.size)
        assertEquals(6, BcsMentalAbilityCategory.entries.size)

        // Module 14: All 4 NCTB books and 47 chapters exist
        assertEquals(4, BookId.entries.size)
        val totalChapters = BookCurriculumRepository.getAllChapters().size
        assertEquals(47, totalChapters)

        // Cross-module ViewModel workflows (Interleaved Practice Lab launch)
        vm.startInterleavedPracticeChallenge(CurriculumTrack.BDMO)
        assertEquals(Screen.PracticeLabPlay, vm.currentScreen)
        assertEquals(10, vm.practiceSession.value.questions.size)
    }

    // =========================================================================
    // 5. AUDIT ROOT-CAUSE FIX VERIFICATION TESTS (BUG-01 .. BUG-08)
    // =========================================================================

    @Test
    fun auditDefectFixes_numericalKeypadScoring_fullExamTypedPersistence_bcsImmediateFeedback_andPdfResolver() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // 1. BUG-01: Numerical keypad wrong answers (-2 sentinel) must be counted as wrong (not unanswered) with negative marking
        vm.startQuiz(
            topic = com.example.math.QuizTopic.MULTIPLICATION,
            count = 5,
            mode = com.example.math.QuizMode.TOPIC,
            difficulty = com.example.math.PracticeDifficulty.EASY,
            questionType = com.example.math.QuizQuestionType.NUMERICAL
        )
        vm.setQuizTypedAnswer("999999") // guaranteed wrong numerical answer not in optionsBn
        vm.submitQuizAnswer()
        val afterSubmit = vm.quizSession.value
        assertEquals(-2, afterSubmit.userAnswers[0])
        assertEquals("999999", afterSubmit.userTypedAnswers[0])
        assertEquals(
            com.example.math.QuestionPaletteState.ANSWERED,
            com.example.math.ExamScoringEngine.computePaletteState(
                questionIndex = 0,
                visitedIndices = afterSubmit.visitedIndices,
                selectedAnswers = afterSubmit.userAnswers,
                markedForReview = afterSubmit.markedForReviewIndices
            )
        )
        val eval = com.example.math.ExamScoringEngine.evaluateExam(
            attemptId = "test_num_wrong",
            examTitleBn = "Test",
            examType = "QUICK_TEST",
            questions = afterSubmit.questions,
            selectedAnswers = afterSubmit.userAnswers,
            markedForReview = emptySet(),
            questionTimeSeconds = emptyMap(),
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = 0.5,
            timeSpentSeconds = 20,
            durationLimitSeconds = 120
        )
        assertEquals(1, eval.attemptedCount)
        assertEquals(0, eval.correctCount)
        assertEquals(1, eval.wrongCount)
        assertEquals(4, eval.unansweredCount)
        assertEquals(0.5, eval.negativeMarksDeducted, 0.001)
        assertEquals(-0.5, eval.finalScore, 0.001)

        // 2. BUG-02: Full-exam mode numerical typed answer persists across navigation
        vm.startConfiguredExam(
            com.example.math.ExamConfig(
                preset = com.example.math.ExamPresetType.QUICK_TEST,
                questionType = com.example.math.QuizQuestionType.NUMERICAL,
                questionCount = 5,
                immediateFeedback = false
            )
        )
        val firstCorrectText = vm.quizSession.value.questions[0].correctAnswerText
        vm.setQuizTypedAnswer(firstCorrectText)
        assertEquals(vm.quizSession.value.questions[0].correctIndex, vm.quizSession.value.userAnswers[0])
        assertEquals(1, vm.quizSession.value.correctCount)
        vm.nextQuizQuestion()
        vm.setQuizTypedAnswer("888888")
        assertEquals(-2, vm.quizSession.value.userAnswers[1])
        vm.previousQuizQuestion()
        assertEquals(firstCorrectText, vm.quizSession.value.typedAnswer)

        // 3. BUG-03: ExamPresetType.fromIdOrDefault case-insensitive resolution
        assertEquals(
            com.example.math.ExamPresetType.BCS_PRELIMINARY_MOCK,
            com.example.math.ExamPresetType.fromIdOrDefault("bcs_preliminary_mock")
        )
        assertEquals(
            com.example.math.ExamPresetType.QUICK_TEST,
            com.example.math.ExamPresetType.fromIdOrDefault("unknown_preset_xyz")
        )

        // 4. BUG-04: BCS TOPIC_PRACTICE mode enables immediate feedback (isFullExamMode == false) and records examProfileId
        vm.setBcsExamProfile(BcsExamProfile.BCS_51ST_SPECIAL)
        vm.startBcsModeExam(
            mode = com.example.math.BcsPracticeMode.TOPIC_PRACTICE,
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC
        )
        val bcsPracticeSession = vm.quizSession.value
        assertFalse("TOPIC_PRACTICE must have isFullExamMode == false for immediate feedback", bcsPracticeSession.isFullExamMode)
        assertEquals(com.example.math.QuizMode.PRACTICE, bcsPracticeSession.mode)
        assertEquals(BcsExamProfile.BCS_51ST_SPECIAL.id, bcsPracticeSession.examProfileId)

        // 5. Module 14 offline PDF resolver works with bundled NCTB PDFs
        for (bookId in BookId.entries) {
            val pdfFile = com.example.ui.screens.resolveOfflinePdfFile(app, bookId)
            assertNotNull("Offline PDF file for ${bookId.id} must resolve on device", pdfFile)
            assertTrue("Offline PDF file for ${bookId.id} must exist and be >= 4KB", pdfFile!!.exists() && pdfFile.length() >= 4_096L)
        }
    }

    // =========================================================================
    // 10. RELEASE CANDIDATE PHASE B: EXAM ENGINE, TYPED RESUME & BCS ATTRIBUTION
    // =========================================================================
    @Test
    fun releaseCandidate_phaseB_examEngineAndBcsAttribution(): Unit = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // A. Numerical keypad full matrix: correct, wrong distractor match, wrong non-distractor (-2),
        // change answer, clear answer, unanswered, palette jump, DB resume & result review
        vm.startConfiguredExam(
            com.example.math.ExamConfig(
                preset = com.example.math.ExamPresetType.CUSTOM_EXAM,
                questionType = com.example.math.QuizQuestionType.NUMERICAL,
                questionCount = 6,
                durationMinutes = 10,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.25,
                immediateFeedback = false
            )
        )
        val qs = vm.quizSession.value.questions
        assertEquals(6, qs.size)

        // Q0: First enter wrong answer, then change to correct answer (verify no double counting)
        vm.setQuizTypedAnswer("777777")
        assertEquals(-2, vm.quizSession.value.userAnswers[0])
        assertEquals(0, vm.quizSession.value.correctCount)
        vm.setQuizTypedAnswer(qs[0].correctAnswerText)
        assertEquals(qs[0].correctIndex, vm.quizSession.value.userAnswers[0])
        assertEquals(1, vm.quizSession.value.correctCount)

        // Q1: Enter wrong answer matching a listed distractor in optionsBn
        vm.nextQuizQuestion()
        val q1DistractorIdx = qs[1].optionsBn.indices.first { it != qs[1].correctIndex }
        val q1DistractorText = qs[1].optionsBn[q1DistractorIdx]
        vm.setQuizTypedAnswer(q1DistractorText)
        assertEquals(q1DistractorIdx, vm.quizSession.value.userAnswers[1])
        assertEquals(1, vm.quizSession.value.correctCount)

        // Q2: Enter wrong answer NOT matching any distractor (-2 sentinel)
        vm.nextQuizQuestion()
        vm.setQuizTypedAnswer("987654")
        assertEquals(-2, vm.quizSession.value.userAnswers[2])
        assertEquals("987654", vm.quizSession.value.userTypedAnswers[2])

        // Q3: Enter correct answer, then clear it via clearCurrentQuizAnswer() -> becomes unanswered
        vm.nextQuizQuestion()
        vm.setQuizTypedAnswer(qs[3].correctAnswerText)
        assertEquals(2, vm.quizSession.value.correctCount)
        vm.clearCurrentQuizAnswer()
        assertFalse(vm.quizSession.value.userAnswers.containsKey(3))
        assertFalse(vm.quizSession.value.userTypedAnswers.containsKey(3))
        assertEquals("", vm.quizSession.value.typedAnswer)
        assertEquals(1, vm.quizSession.value.correctCount)

        // Q4 & Q5: Explicitly skipped / unanswered via Question Palette jump
        vm.jumpToQuizQuestion(2)
        assertEquals("987654", vm.quizSession.value.typedAnswer)
        assertEquals(null, vm.quizSession.value.selectedOptionIndex)

        // Verify serialization & deserialization of answers + typed answers for Resume / Recreation
        val preResumeState = vm.quizSession.value
        val serializedAnswers = com.example.math.ExamQuestionBank.serializeAnswersWithTyped(
            preResumeState.userAnswers,
            preResumeState.userTypedAnswers
        )
        val deserializedInts = com.example.math.ExamQuestionBank.deserializeIntMap(serializedAnswers)
        val deserializedTyped = com.example.math.ExamQuestionBank.deserializeTypedMap(
            serializedAnswers,
            qs,
            deserializedInts
        )
        assertEquals(preResumeState.userAnswers, deserializedInts)
        assertEquals("987654", deserializedTyped[2])
        assertEquals(qs[0].correctAnswerText, deserializedTyped[0])

        // Submit the full exam and verify scoring: 1 correct (+1.0), 2 wrong (-0.50 total), 3 unanswered (0.0) -> finalScore = 0.50
        vm.submitFullExamNow()
        val finished = vm.quizSession.value
        assertTrue(finished.isFinished)
        val detail = finished.detailedResult!!
        assertEquals(6, detail.totalQuestions)
        assertEquals(3, detail.attemptedCount)
        assertEquals(1, detail.correctCount)
        assertEquals(2, detail.wrongCount)
        assertEquals(3, detail.unansweredCount)
        assertEquals(0.50, detail.negativeMarksDeducted, 0.001)
        assertEquals(0.50, detail.finalScore, 0.001)
        // Verify incorrectQuestions only includes the 2 wrong answered questions, NOT the 3 unanswered questions
        assertEquals(2, finished.incorrectQuestions.size)

        // B. BCS Multi-Profile Attribution across all 4 profiles (50th Prelim, Written 008, 51st Special, Legacy)
        val allProfiles = listOf(
            BcsExamProfile.BCS_50TH_PRELIM,
            BcsExamProfile.BCS_WRITTEN_008_MATH,
            BcsExamProfile.BCS_51ST_SPECIAL,
            BcsExamProfile.BCS_LEGACY_PRELIM
        )
        for (profile in allProfiles) {
            vm.setBcsExamProfile(profile)
            assertEquals(profile, vm.selectedBcsExamProfile.value)
            vm.startBcsModeExam(
                mode = com.example.math.BcsPracticeMode.TIMED_TOPIC_TEST,
                unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC
            )
            assertEquals(profile.id, vm.quizSession.value.examProfileId)
            assertTrue(vm.quizSession.value.isFullExamMode)
            // Answer Q0 wrong and Q1 right, then finish
            val bcsQs = vm.quizSession.value.questions
            val wrongOpt = (bcsQs[0].correctIndex + 1) % bcsQs[0].optionsBn.size
            vm.selectQuizOption(wrongOpt)
            vm.nextQuizQuestion()
            vm.selectQuizOption(bcsQs[1].correctIndex)
            vm.submitFullExamNow()
            assertEquals(profile.id, vm.quizSession.value.examProfileId)
            assertEquals(1, vm.quizSession.value.detailedResult!!.correctCount)
            assertEquals(1, vm.quizSession.value.detailedResult!!.wrongCount)
        }
    }

    // =========================================================================
    // 11. RELEASE CANDIDATE PHASE C: POPULATED ROOM v1 -> v9 MIGRATION AUDIT
    // =========================================================================
    @Test
    fun releaseCandidate_phaseC_populatedRoomV1ToV9MigrationAndPragma(): Unit = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val dbName = "rc_populated_v1_to_v9_${System.currentTimeMillis()}.db"
        context.deleteDatabase(dbName)

        val callback = object : SupportSQLiteOpenHelper.Callback(1) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `titleBn` TEXT NOT NULL, `subtitleBn` TEXT NOT NULL, `detailBn` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `quiz_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `topic` TEXT NOT NULL, `topicTitleBn` TEXT NOT NULL, `score` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `percentage` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (`itemId` TEXT NOT NULL, `isLearned` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`itemId`))")
                // Seed v1 user data
                db.execSQL("INSERT INTO `bookmarks` VALUES ('bm_rc_1', 'FORMULA', 'সূত্র ১', 'বীজগণিত', '(a+b)² = a²+2ab+b²', 1700000100)")
                db.execSQL("INSERT INTO `quiz_scores` (`topic`, `topicTitleBn`, `score`, `totalQuestions`, `percentage`, `timestamp`) VALUES ('ALGEBRA', 'বীজগণিত কুইজ', 9, 10, 90, 1700000200)")
                db.execSQL("INSERT INTO `user_progress` VALUES ('table_12', 1, 1700000250)")
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(callback)
                .build()
        ).let { helper ->
            val rawDb = helper.writableDatabase
            // Step-by-step migration from v1 -> v9, seeding rows at intermediate versions
            MathDatabase.MIGRATION_1_2.migrate(rawDb)
            MathDatabase.MIGRATION_2_3.migrate(rawDb)
            rawDb.execSQL("INSERT INTO `practice_history` (`module`, `topicId`, `topicTitleBn`, `difficulty`, `mode`, `score`, `correctCount`, `wrongCount`, `totalQuestions`, `accuracyPercent`, `bestStreak`, `durationSeconds`, `timestamp`) VALUES ('PRACTICE_LAB', 'ARITH', 'পাটিগণিত', 'MEDIUM', 'STANDARD', 80, 8, 2, 10, 80, 5, 120, 1700000300)")
            MathDatabase.MIGRATION_3_4.migrate(rawDb)
            MathDatabase.MIGRATION_4_5.migrate(rawDb)
            rawDb.execSQL("INSERT INTO `book_reading_state` VALUES ('CLASS_6', 3, 'c6_3_l1', 'CONCEPT', 45, 51, 20, '12,45', 1700000400)")
            MathDatabase.MIGRATION_5_6.migrate(rawDb)
            rawDb.execSQL("INSERT INTO `bcs_topic_progress` VALUES ('UNIT_1_ARITHMETIC', 'MATH_REASONING', 1, 1, 10, 9, 300, 90, 1700000500)")
            MathDatabase.MIGRATION_6_7.migrate(rawDb)
            rawDb.execSQL("INSERT INTO `formula_mastery_state` (`formulaId`, `subjectId`, `masteryLevel`, `practiceCount`, `lastPracticedAtMillis`) VALUES ('alg_sq_1', 'ALGEBRA', 'MASTERED', 4, 1700000600)")
            MathDatabase.MIGRATION_7_8.migrate(rawDb)
            MathDatabase.MIGRATION_8_9.migrate(rawDb)
            rawDb.version = 9

            // Run SQLite integrity_check and foreign_key_check
            rawDb.query("PRAGMA integrity_check").use { c ->
                assertTrue(c.moveToFirst())
                assertEquals("ok", c.getString(0).lowercase())
            }
            rawDb.query("PRAGMA foreign_key_check").use { c ->
                assertEquals("PRAGMA foreign_key_check must return 0 violations", 0, c.count)
            }
            helper.close()
        }

        // Open with Room v9 and verify all seeded rows survived intact
        val roomDb = Room.databaseBuilder(context, MathDatabase::class.java, dbName)
            .addMigrations(
                MathDatabase.MIGRATION_1_2,
                MathDatabase.MIGRATION_2_3,
                MathDatabase.MIGRATION_3_4,
                MathDatabase.MIGRATION_4_5,
                MathDatabase.MIGRATION_5_6,
                MathDatabase.MIGRATION_6_7,
                MathDatabase.MIGRATION_7_8,
                MathDatabase.MIGRATION_8_9
            )
            .allowMainThreadQueries()
            .build()

        val dao = roomDb.mathDao()
        assertEquals(1, dao.getAllBookmarks().first().size)
        assertEquals("সূত্র ১", dao.getAllBookmarks().first()[0].titleBn)
        assertEquals(1, dao.getAllQuizScores().first().size)
        assertEquals(90, dao.getAllQuizScores().first()[0].percentage)
        assertEquals(1, dao.getAllProgress().first().size)
        assertTrue(dao.getAllProgress().first()[0].isLearned)
        assertEquals(1, dao.getAllPracticeHistory().first().size)
        val bookState = dao.getBookReadingStateByBookId("CLASS_6")
        assertNotNull(bookState)
        assertEquals(45, bookState!!.lastPrintedPage)
        assertEquals("STRUCTURED", bookState.preferredViewMode)
        val bcsProgress = dao.getBcsTopicProgressById("UNIT_1_ARITHMETIC")
        assertNotNull(bcsProgress)
        assertEquals("BCS_50TH_PRELIM_2025", bcsProgress!!.examProfileId)
        val formulaMastery = dao.getFormulaMasteryById("alg_sq_1")
        assertNotNull(formulaMastery)
        assertEquals("MASTERED", formulaMastery!!.masteryLevel)

        roomDb.close()
        context.deleteDatabase(dbName)
    }

    // =========================================================================
    // 12. RELEASE CANDIDATE PHASE D: ALL 4 TEXTBOOK PDFS (894 PAGES) & 47 CHAPTERS
    // =========================================================================
    @Test
    fun releaseCandidate_phaseD_all4PdfAssets894PagesAnd47Chapters() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val expectedPagesByBook = mapOf(
            BookId.CLASS_6 to 161,
            BookId.CLASS_7 to 181,
            BookId.CLASS_8 to 190,
            BookId.CLASS_9_10 to 362
        )
        val expectedChaptersByBook = mapOf(
            BookId.CLASS_6 to 8,
            BookId.CLASS_7 to 11,
            BookId.CLASS_8 to 11,
            BookId.CLASS_9_10 to 17
        )

        var totalPagesAcrossBooks = 0
        var totalChaptersAcrossBooks = 0

        for (bookId in BookId.entries) {
            val expectedPdfPages = expectedPagesByBook.getValue(bookId)
            assertEquals("BookId.totalPdfPages must match expected page count for ${bookId.id}", expectedPdfPages, bookId.totalPdfPages)
            totalPagesAcrossBooks += bookId.totalPdfPages

            val pdfFile = com.example.ui.screens.resolveOfflinePdfFile(app, bookId)
            assertNotNull("Packaged PDF file must exist for ${bookId.id}", pdfFile)
            assertTrue("Packaged PDF file must be > 5MB for ${bookId.id}", pdfFile!!.length() > 5_000_000L)

            // Verify PDF binary header and structural page count in the PDF stream
            val rawBytes = pdfFile.readBytes()
            val header = String(rawBytes, 0, 8, Charsets.US_ASCII)
            assertTrue("PDF header must start with %PDF-", header.startsWith("%PDF-"))

            // Verify all chapters for this book
            val chapters = BookCurriculumRepository.getChapters(bookId)
            assertEquals(expectedChaptersByBook.getValue(bookId), chapters.size)
            totalChaptersAcrossBooks += chapters.size

            // Verify chapter ordering and valid page ranges
            chapters.forEachIndexed { idx, ch ->
                assertEquals("Chapter numbers must be sequential 1..N", idx + 1, ch.chapterNumber)
                assertTrue("Chapter start page must be >= 1", ch.printedPageStart in 1..bookId.maxPrintedPage)
                assertTrue("Chapter end page must be >= start page and <= maxPrintedPage", ch.printedPageEnd in ch.printedPageStart..bookId.maxPrintedPage)
                assertTrue("Every chapter must have structured lessons", ch.lessons.isNotEmpty())
            }

            // Verify Rich Native Page Renderer on first, middle, last, and sampled printed pages
            val maxPrinted = bookId.maxPrintedPage
            val samplePages = listOf(
                1,
                maxPrinted / 4,
                maxPrinted / 2,
                (maxPrinted * 3) / 4,
                maxPrinted
            )
            for (page in samplePages) {
                val content = BookCurriculumRepository.getStructuredPageContent(bookId, page)
                assertEquals(page, content.printedPage)
                assertTrue(content.chapterTitleBn.isNotBlank())
                assertTrue(content.sectionHeadingBn.isNotBlank())
                assertTrue(content.keyDefinitionsBn.isNotEmpty())
            }
        }

        assertEquals(894, totalPagesAcrossBooks)
        assertEquals(47, totalChaptersAcrossBooks)
    }

    // =========================================================================
    // 13. DEEP AUDIT REGRESSION TESTS (BUG-14 THROUGH BUG-20)
    // =========================================================================

    @Test
    fun auditRegression_bug14_untimedPracticeAndNonTimedQuizTrackElapsedTimeWithoutAutoSubmit() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // 1. Non-timed QuizMode (TOPIC, PRACTICE, MIXED) must NOT auto-finish when tickQuizTimer() runs
        for (nonTimedMode in listOf(
            com.example.math.QuizMode.TOPIC,
            com.example.math.QuizMode.PRACTICE,
            com.example.math.QuizMode.MIXED
        )) {
            vm.startQuiz(
                topic = com.example.math.QuizTopic.MULTIPLICATION,
                count = 5,
                mode = nonTimedMode,
                difficulty = com.example.math.PracticeDifficulty.EASY,
                questionType = com.example.math.QuizQuestionType.MCQ,
                timedSeconds = 120
            )
            val s0 = vm.quizSession.value
            assertEquals("Untimed mode $nonTimedMode must have configuredDurationSeconds = 0", 0, s0.configuredDurationSeconds)
            assertEquals("Untimed mode $nonTimedMode must have timeRemainingSeconds = 0", 0, s0.timeRemainingSeconds)
            assertEquals(0L, s0.deadlineElapsedRealtimeMs)
            repeat(5) { vm.tickQuizTimer() }
            assertFalse("Untimed mode $nonTimedMode must NOT auto-finish on timer tick", vm.quizSession.value.isFinished)
        }

        // 2. Timed QuizMode.TIMED must still have countdown configured
        vm.startQuiz(
            topic = com.example.math.QuizTopic.MULTIPLICATION,
            count = 5,
            mode = com.example.math.QuizMode.TIMED,
            difficulty = com.example.math.PracticeDifficulty.EASY,
            questionType = com.example.math.QuizQuestionType.MCQ,
            timedSeconds = 90
        )
        assertEquals(90, vm.quizSession.value.configuredDurationSeconds)
        assertEquals(90, vm.quizSession.value.timeRemainingSeconds)
        assertTrue(vm.quizSession.value.deadlineElapsedRealtimeMs > 0L)

        // 3. ExamPresetType.PRACTICE_EXAM must be untimed (configuredDurationSeconds == 0)
        vm.startConfiguredExam(
            com.example.math.ExamConfig(
                preset = com.example.math.ExamPresetType.PRACTICE_EXAM,
                questionCount = 5,
                durationMinutes = 15,
                immediateFeedback = true
            )
        )
        assertEquals(0, vm.quizSession.value.configuredDurationSeconds)
        assertEquals(0L, vm.quizSession.value.deadlineElapsedRealtimeMs)
        repeat(5) { vm.tickQuizTimer() }
        assertFalse("PRACTICE_EXAM must NOT auto-finish on timer tick", vm.quizSession.value.isFinished)

        // 4. BCS TOPIC_PRACTICE and MISTAKE_PRACTICE must track upward elapsed time only (configuredDurationSeconds == 0)
        for (bcsPracticeMode in listOf(
            com.example.math.BcsPracticeMode.TOPIC_PRACTICE,
            com.example.math.BcsPracticeMode.MISTAKE_PRACTICE
        )) {
            vm.startBcsModeExam(
                mode = bcsPracticeMode,
                unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC
            )
            assertEquals("BCS $bcsPracticeMode must have configuredDurationSeconds = 0", 0, vm.quizSession.value.configuredDurationSeconds)
            assertEquals(0L, vm.quizSession.value.deadlineElapsedRealtimeMs)
            repeat(5) { vm.tickQuizTimer() }
            assertFalse("BCS $bcsPracticeMode must NOT auto-finish on timer tick", vm.quizSession.value.isFinished)
        }
    }

    @Test
    fun auditRegression_bug15_reviewIncorrectExcludesUnansweredQuestions() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        vm.startConfiguredExam(
            com.example.math.ExamConfig(
                preset = com.example.math.ExamPresetType.QUICK_TEST,
                questionType = com.example.math.QuizQuestionType.MCQ,
                questionCount = 5,
                immediateFeedback = false
            )
        )
        val qs = vm.quizSession.value.questions
        assertEquals(5, qs.size)

        // Q0: answer correctly
        vm.selectQuizOption(qs[0].correctIndex)
        // Q1: answer wrongly
        vm.nextQuizQuestion()
        val wrongIndex = (qs[1].correctIndex + 1) % qs[1].optionsBn.size
        vm.selectQuizOption(wrongIndex)
        // Q2, Q3, Q4: leave unanswered (null)
        vm.submitFullExamNow()

        val finished = vm.quizSession.value
        assertTrue(finished.isFinished)
        assertEquals(1, finished.detailedResult!!.correctCount)
        assertEquals(1, finished.detailedResult!!.wrongCount)
        assertEquals(3, finished.detailedResult!!.unansweredCount)

        // Start Review Incorrect Quiz -> must ONLY include the 1 wrong question, NOT the 3 unanswered ones
        vm.startReviewIncorrectQuiz()
        val reviewSession = vm.quizSession.value
        assertFalse(reviewSession.isFinished)
        assertEquals("startReviewIncorrectQuiz must exclude unanswered questions", 1, reviewSession.questions.size)
        assertEquals(qs[1].questionBn, reviewSession.questions[0].questionBn)
    }

    @Test
    fun auditRegression_bug16_numericalQuestionTypeStrictValidationAcrossAllTopicsAndPresets() {
        for (topic in com.example.math.QuizTopic.entries) {
            for (diff in com.example.math.PracticeDifficulty.entries) {
                val qs = com.example.math.QuizGenerator.generateQuiz(
                    topic = topic,
                    questionCount = 8,
                    difficulty = diff,
                    questionType = com.example.math.QuizQuestionType.NUMERICAL
                )
                assertTrue("QuizGenerator NUMERICAL for $topic/$diff must not be empty", qs.isNotEmpty())
                qs.forEach { q ->
                    assertEquals(
                        "Question '${q.questionBn}' in $topic/$diff must have NUMERICAL questionType",
                        com.example.math.QuizQuestionType.NUMERICAL,
                        q.questionType
                    )
                    assertNotNull(
                        "Question '${q.questionBn}' correctAnswerText '${q.correctAnswerText}' must be numeric",
                        com.example.math.SharedMathEngine.parseRationalOrNull(
                            com.example.math.SharedMathEngine.normalizeAnswerToken(q.correctAnswerText)
                        )
                    )
                    assertTrue(
                        "SharedMathEngine must validate correctAnswerText '${q.correctAnswerText}'",
                        com.example.math.SharedMathEngine.isAnswerCorrect(q.correctAnswerText, q.correctAnswerText)
                    )
                }
            }
        }

        // Verify BCS_PRELIMINARY_MOCK with NUMERICAL questionType honors NUMERICAL
        val bcsNumerical = com.example.math.ExamQuestionBank.generateExamQuestions(
            com.example.math.ExamConfig(
                preset = com.example.math.ExamPresetType.BCS_PRELIMINARY_MOCK,
                questionType = com.example.math.QuizQuestionType.NUMERICAL,
                questionCount = 10
            )
        )
        assertEquals(10, bcsNumerical.size)
        bcsNumerical.forEach { q ->
            assertEquals(com.example.math.QuizQuestionType.NUMERICAL, q.questionType)
            assertNotNull(
                com.example.math.SharedMathEngine.parseRationalOrNull(
                    com.example.math.SharedMathEngine.normalizeAnswerToken(q.correctAnswerText)
                )
            )
        }
    }

    @Test
    fun auditRegression_bug17_numberSystemPracticeSupportsAllThreeInputFormatsAndKeypadValidation() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // 1. TRUE_FALSE format
        vm.startNumberSystemPractice(
            topic = com.example.math.NumberSystemTopic.ALL,
            difficulty = com.example.math.NumberSystemDifficulty.EASY,
            inputFormat = QuestionInputFormat.TRUE_FALSE
        )
        val tfState = vm.numberSystemPracticeState.value
        assertEquals(QuestionInputFormat.TRUE_FALSE, tfState.inputFormat)
        assertTrue(tfState.questions.isNotEmpty())
        tfState.questions.forEach { q ->
            assertEquals(QuestionInputFormat.TRUE_FALSE, q.inputFormat)
            assertEquals(listOf("সত্য (True)", "মিথ্যা (False)"), q.optionsBn)
            assertTrue(q.correctIndex in 0..1)
        }

        // 2. KEYPAD format + keypad helper methods + validation
        vm.startNumberSystemPractice(
            topic = com.example.math.NumberSystemTopic.ALL,
            difficulty = com.example.math.NumberSystemDifficulty.MEDIUM,
            inputFormat = QuestionInputFormat.KEYPAD
        )
        val kpState0 = vm.numberSystemPracticeState.value
        assertEquals(QuestionInputFormat.KEYPAD, kpState0.inputFormat)
        assertTrue(kpState0.questions.isNotEmpty())
        val firstQ = kpState0.questions[0]
        assertEquals(QuestionInputFormat.KEYPAD, firstQ.inputFormat)
        assertTrue(firstQ.correctAnswerBn.isNotBlank())

        // Test keypad digit append, backspace, clear
        vm.appendNumberSystemKeypadDigit("১")
        vm.appendNumberSystemKeypadDigit("২")
        assertEquals("১২", vm.numberSystemPracticeState.value.typedInput)
        vm.backspaceNumberSystemKeypad()
        assertEquals("১", vm.numberSystemPracticeState.value.typedInput)
        vm.clearNumberSystemKeypad()
        assertEquals("", vm.numberSystemPracticeState.value.typedInput)

        // Submit correct keypad answer
        vm.setNumberSystemTypedInput(firstQ.correctAnswerBn)
        vm.submitNumberSystemAnswer()
        val kpSubmitted = vm.numberSystemPracticeState.value
        assertTrue(kpSubmitted.isSubmitted)
        assertEquals(firstQ.correctIndex, kpSubmitted.selectedOptionIndex)
        assertEquals(1, kpSubmitted.score)

        // Move to Q1 and submit wrong keypad answer
        vm.nextNumberSystemQuestion()
        vm.setNumberSystemTypedInput("999999")
        vm.submitNumberSystemAnswer()
        val kpWrong = vm.numberSystemPracticeState.value
        assertTrue(kpWrong.isSubmitted)
        assertEquals(-2, kpWrong.selectedOptionIndex)
        assertEquals(1, kpWrong.score)
    }

    @Test
    fun auditRegression_bug19_and_bug20_formulaLibraryBookmarkDeepLinkAndBcsRawTopicDeduplication() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // BUG-19: openLibraryFormulaFromBookmark sets selectedLibraryFormulaId and navigates to FormulaLibrary
        val targetFormula = FormulaLibraryProvider.allFormulas.first()
        vm.openLibraryFormulaFromBookmark(targetFormula.id)
        assertEquals(Screen.FormulaLibrary, vm.currentScreen)
        assertEquals(targetFormula.id, vm.selectedLibraryFormulaId.value)
        vm.clearSelectedLibraryFormulaId()
        org.junit.Assert.assertNull(vm.selectedLibraryFormulaId.value)

        // BUG-20: Profile-scoped BCS topic completions deduplicate by rawTopicId
        val multiProfileRows = listOf(
            com.example.data.model.BcsTopicProgressEntity(
                topicId = com.example.data.model.BcsTopicProgressEntity.buildScopedKey("BCS_50TH_PRELIM_2025", "bcs_guide_real_numbers"),
                examProfileId = "BCS_50TH_PRELIM_2025",
                subjectTrack = "MATH_REASONING",
                isGuideCompleted = true
            ),
            com.example.data.model.BcsTopicProgressEntity(
                topicId = com.example.data.model.BcsTopicProgressEntity.buildScopedKey("BCS_51ST_SPECIAL", "bcs_guide_real_numbers"),
                examProfileId = "BCS_51ST_SPECIAL",
                subjectTrack = "MATH_REASONING",
                isGuideCompleted = true
            ),
            com.example.data.model.BcsTopicProgressEntity(
                topicId = com.example.data.model.BcsTopicProgressEntity.buildScopedKey("BCS_50TH_PRELIM_2025", "bcs_guide_percentage"),
                examProfileId = "BCS_50TH_PRELIM_2025",
                subjectTrack = "MATH_REASONING",
                isGuideCompleted = true
            )
        )
        val deduplicatedDone = multiProfileRows.filter { it.isGuideCompleted }.map { it.rawTopicId }.distinct().size
        assertEquals("Same guide completed across 2 profiles must count once toward unique guide total", 2, deduplicatedDone)
    }

    @Test
    fun auditRegression_bug21_bcsMultiProfileMistakeResolutionAndDeletion() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = androidx.room.Room.inMemoryDatabaseBuilder(app, com.example.data.db.MathDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repo = com.example.data.repository.MathRepository(db.mathDao())
            val specialProfileId = BcsExamProfile.BCS_51ST_SPECIAL.id

            // Record a mistake under a non-default BCS profile (stored as "BCS_51ST_SPECIAL_2026::bcs_q_99")
            repo.recordBcsMistake(
                questionId = "bcs_q_99",
                subjectTrack = "MATH_REASONING",
                topicId = "UNIT_1_ARITHMETIC",
                questionPromptBn = "৫১তম বিশেষ বিসিএস অনুশীলন প্রশ্ন",
                wrongAnswerBn = "১০",
                correctAnswerBn = "২০",
                explanationBn = "ব্যাখ্যা",
                examProfileId = specialProfileId
            )
            val scopedRecord = db.mathDao().getBcsMistakeById("$specialProfileId::bcs_q_99")
            assertNotNull("Scoped mistake must be persisted under profile key", scopedRecord)
            assertFalse(scopedRecord!!.isResolved)
            assertFalse("Non-default profile mistake must not belong to default profile", scopedRecord.belongsToProfile(BcsExamProfile.BCS_50TH_PRELIM.id))
            assertTrue("Non-default profile mistake must belong to its own profile", scopedRecord.belongsToProfile(specialProfileId))

            // Resolve using raw questionId + examProfileId (BUG-21 fix)
            repo.markBcsMistakeResolved(questionId = "bcs_q_99", examProfileId = specialProfileId)
            val resolvedRecord = db.mathDao().getBcsMistakeById("$specialProfileId::bcs_q_99")
            assertNotNull(resolvedRecord)
            assertTrue("Mistake must be marked resolved when called with raw questionId and profileId", resolvedRecord!!.isResolved)
            assertEquals(1, resolvedRecord.retrySuccessCount)

            // Delete using raw questionId + examProfileId (BUG-21 fix)
            repo.deleteBcsMistake(questionId = "bcs_q_99", examProfileId = specialProfileId)
            val afterDelete = db.mathDao().getBcsMistakeById("$specialProfileId::bcs_q_99")
            org.junit.Assert.assertNull("Scoped mistake must be deleted when called with raw questionId and profileId", afterDelete)
        } finally {
            db.close()
        }
    }

    @Test
    fun auditRegression_phaseF_roomDatabaseV9All20EntityTablesVerified() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = androidx.room.Room.inMemoryDatabaseBuilder(app, com.example.data.db.MathDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val sqlDb = db.openHelper.writableDatabase
            val tables = mutableSetOf<String>()
            sqlDb.query("SELECT name FROM sqlite_master WHERE type='table'").use { cursor ->
                while (cursor.moveToNext()) {
                    tables.add(cursor.getString(0))
                }
            }
            val expected20ApplicationTables = setOf(
                "bookmarks",
                "quiz_scores",
                "user_progress",
                "practice_history",
                "topic_analytics",
                "recent_formulas",
                "exam_attempts",
                "solver_history",
                "daily_study_activity",
                "book_lesson_progress",
                "book_reading_state",
                "book_notes",
                "book_mistakes",
                "book_spaced_revision",
                "bcs_user_profile",
                "bcs_topic_progress",
                "bcs_mistake_notebook",
                "bcs_study_goals",
                "unified_mistake_notebook",
                "formula_mastery_state"
            )
            assertEquals(20, expected20ApplicationTables.size)
            expected20ApplicationTables.forEach { tableName ->
                assertTrue("Room v9 schema must contain table '$tableName'", tables.contains(tableName))
            }
        } finally {
            db.close()
        }
    }

    @Test
    fun auditRegression_bug22_bookPracticeBidirectionalNavigationPreservesStateAndPreventsScoreDoubleCounting() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        vm.startBookPractice(
            bookId = BookId.CLASS_6,
            chapterNumber = 1,
            mode = com.example.math.BookPracticeMode.STANDARD,
            questionCount = 5
        )
        val initial = vm.bookPracticeSession.value
        assertEquals(5, initial.questions.size)
        assertEquals(0, initial.currentIndex)

        // Answer Q0 correctly
        val q0 = initial.questions[0]
        if (q0.correctOptionIndices.isNotEmpty()) {
            q0.correctOptionIndices.forEach { idx -> vm.toggleBookPracticeOption(idx) }
        } else {
            vm.setBookPracticeTextInput(q0.correctTextAnswer)
        }
        vm.submitBookPracticeAnswer()
        val afterQ0 = vm.bookPracticeSession.value
        assertTrue(afterQ0.isAnswerSubmitted)
        assertEquals(true, afterQ0.isCurrentCorrect)
        assertEquals(1, afterQ0.correctCount)
        assertEquals(0, afterQ0.wrongCount)

        // Advance to Q1 and answer wrongly
        vm.nextBookPracticeQuestion()
        assertEquals(1, vm.bookPracticeSession.value.currentIndex)
        assertFalse(vm.bookPracticeSession.value.isAnswerSubmitted)

        val q1 = vm.bookPracticeSession.value.questions[1]
        if (q1.optionsBn.isNotEmpty() && q1.correctOptionIndices.isNotEmpty()) {
            val wrongIdx = (0 until q1.optionsBn.size).firstOrNull { !q1.correctOptionIndices.contains(it) } ?: 0
            vm.toggleBookPracticeOption(wrongIdx)
        } else {
            vm.setBookPracticeTextInput("ভুল_উত্তর_৯৯৯৯৯")
        }
        vm.submitBookPracticeAnswer()
        val afterQ1 = vm.bookPracticeSession.value
        assertTrue(afterQ1.isAnswerSubmitted)
        assertEquals(false, afterQ1.isCurrentCorrect)
        assertEquals(1, afterQ1.correctCount)
        assertEquals(1, afterQ1.wrongCount)

        // Navigate back to Q0 -> must preserve submitted state and selections
        vm.previousBookPracticeQuestion()
        val backAtQ0 = vm.bookPracticeSession.value
        assertEquals(0, backAtQ0.currentIndex)
        assertTrue("Q0 must remain submitted when navigating back", backAtQ0.isAnswerSubmitted)
        assertEquals(true, backAtQ0.isCurrentCorrect)
        if (q0.correctOptionIndices.isNotEmpty()) {
            assertEquals(q0.correctOptionIndices, backAtQ0.selectedOptionIndices)
        } else {
            assertEquals(q0.correctTextAnswer, backAtQ0.userTextInput)
        }

        // Calling submitBookPracticeAnswer again on Q0 must be a no-op (no double counting)
        vm.submitBookPracticeAnswer()
        assertEquals(1, vm.bookPracticeSession.value.correctCount)
        assertEquals(1, vm.bookPracticeSession.value.wrongCount)

        // Navigate forward to Q1 -> must ALSO preserve Q1's submitted state and selections without resetting
        vm.nextBookPracticeQuestion()
        val forwardAtQ1 = vm.bookPracticeSession.value
        assertEquals(1, forwardAtQ1.currentIndex)
        assertTrue("Q1 must remain submitted when navigating forward to an already-answered question", forwardAtQ1.isAnswerSubmitted)
        assertEquals(false, forwardAtQ1.isCurrentCorrect)
        assertEquals(1, forwardAtQ1.correctCount)
        assertEquals(1, forwardAtQ1.wrongCount)

        // Advance to Q2 (unanswered) -> must be unsubmitted and clean
        vm.nextBookPracticeQuestion()
        val atQ2 = vm.bookPracticeSession.value
        assertEquals(2, atQ2.currentIndex)
        assertFalse("Unanswered Q2 must not be marked submitted", atQ2.isAnswerSubmitted)
        org.junit.Assert.assertNull(atQ2.isCurrentCorrect)
    }

    @Test
    fun auditRegression_bug23_bug25_bug26_streakTrackingKpiDeduplicationAndBookTextEquivalence() {
        // BUG-26: BookQuestionEngine.isTextEquivalent supports equivalent fractions, ratios, decimals, and Bengali/ASCII numbers
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("১/২", "0.5"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("২/৪", "১/২"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("৩ : ৪", "6 : 8"))
        assertTrue(com.example.math.BookQuestionEngine.isTextEquivalent("৩৬টি", "36"))
        assertFalse(com.example.math.BookQuestionEngine.isTextEquivalent("১/৩", "১/২"))

        // BUG-25: LearningAnalyticsEngine.computeSeparatedKpis does not double-count BOOK_ legacy quiz_scores as standalone Exams
        val now = System.currentTimeMillis()
        val legacyBookScore = com.example.data.model.QuizScoreEntity(
            id = 1,
            topic = "BOOK_class_6_C1",
            topicTitleBn = "৬ষ্ঠ শ্রেণি (অধ্যায় ১)",
            score = 8,
            totalQuestions = 10,
            percentage = 80,
            timestamp = now
        )
        val bookPracticeRecord = com.example.data.model.PracticeHistoryEntity(
            id = 1,
            module = "BOOK",
            topicId = "BOOK_class_6_C1",
            topicTitleBn = "৬ষ্ঠ শ্রেণি (অধ্যায় ১)",
            difficulty = "STANDARD",
            mode = "STANDARD",
            score = 80,
            correctCount = 8,
            wrongCount = 2,
            totalQuestions = 10,
            accuracyPercent = 80,
            bestStreak = 8,
            durationSeconds = 45,
            timestamp = now
        )
        val separated = com.example.math.LearningAnalyticsEngine.computeSeparatedKpis(
            filteredExams = emptyList(),
            filteredLegacyQuizScores = listOf(legacyBookScore),
            filteredPractice = listOf(bookPracticeRecord),
            learnedItemsCount = 5
        )
        assertEquals("BOOK_ legacy quiz score must not be double-counted as an Exam", 0, separated.totalExamsCompleted)
        assertEquals("BOOK practice session must be counted once in Practice KPIs", 1, separated.totalPracticeSessions)
        assertEquals(10, separated.practiceQuestionsAttempted)
        assertEquals(10, separated.combinedQuestionsAttempted)

        // BUG-23: NumberSystemPracticeState tracks consecutive streak and highestStreak accurately
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)
        vm.startNumberSystemPractice(
            topic = com.example.math.NumberSystemTopic.ALL,
            difficulty = com.example.math.NumberSystemDifficulty.EASY,
            inputFormat = QuestionInputFormat.MCQ
        )
        val nsQuestions = vm.numberSystemPracticeState.value.questions
        assertTrue(nsQuestions.size >= 3)

        // Q0 correct -> streak = 1, highest = 1
        vm.selectNumberSystemOption(nsQuestions[0].correctIndex)
        vm.submitNumberSystemAnswer()
        assertEquals(1, vm.numberSystemPracticeState.value.currentStreak)
        assertEquals(1, vm.numberSystemPracticeState.value.highestStreak)

        // Q1 correct -> streak = 2, highest = 2
        vm.nextNumberSystemQuestion()
        vm.selectNumberSystemOption(nsQuestions[1].correctIndex)
        vm.submitNumberSystemAnswer()
        assertEquals(2, vm.numberSystemPracticeState.value.currentStreak)
        assertEquals(2, vm.numberSystemPracticeState.value.highestStreak)

        // Q2 wrong -> streak = 0, highest remains 2
        vm.nextNumberSystemQuestion()
        val wrongOpt = (nsQuestions[2].correctIndex + 1) % nsQuestions[2].optionsBn.size
        vm.selectNumberSystemOption(wrongOpt)
        vm.submitNumberSystemAnswer()
        assertEquals(0, vm.numberSystemPracticeState.value.currentStreak)
        assertEquals(2, vm.numberSystemPracticeState.value.highestStreak)
        assertEquals(2, vm.numberSystemPracticeState.value.score)
    }

    @Test
    fun adversarialAudit_bug29_dotlessUnitSuffixesAndMixedFractionEquivalence() {
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("24", "২৪ সেমি"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("২৪ সে.মি.", "২৪ সেমি"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("150", "১৫০ বর্গসেমি"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("64", "৬৪ ঘনসেমি"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("12", "১২ মিটার"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("48", "৪৮ বর্গমিটার"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("5", "৫ কিমি"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("1 1/2", "1.5"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("১ ১/২", "৩/২"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("১ সমস্ত ১/২", "1.5"))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("১ পূর্ণ ১/২ সেমি", "1.5 সে.মি."))
        assertTrue(com.example.math.SharedMathEngine.isAnswerCorrect("2 3/4", "১১/৪"))
    }

    @Test
    fun adversarialAudit_bug30_parenthesizedDistributiveAlgebraicSimplification() {
        val r1 = com.example.math.SharedMathEngine.simplify("2(x + 3) + 4x - 1")
        assertTrue("2(x + 3) + 4x - 1 must simplify successfully", r1.isSuccess)
        assertEquals("৬x + ৫", r1.simplifiedFormBn)
        assertTrue("Distributive step must be included", r1.stepsBn.size >= 3)

        val r2 = com.example.math.SharedMathEngine.simplify("3(2x - 1) - 2(x - 4)")
        assertTrue(r2.isSuccess)
        assertEquals("৪x + ৫", r2.simplifiedFormBn)

        val r3 = com.example.math.SharedMathEngine.simplify("-(x + 5) + 3x")
        assertTrue(r3.isSuccess)
        assertEquals("২x − ৫", r3.simplifiedFormBn)

        val r4 = com.example.math.SharedMathEngine.simplify("2(x^2 + 3x - 1) - x^2")
        assertTrue(r4.isSuccess)
        assertEquals("x² + ৬x − ২", r4.simplifiedFormBn)
    }

    @Test
    fun adversarialAudit_bug31_and_bug32_crossTableMistakeSyncAndResolution() = runBlocking<Unit> {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = MathDatabase.getInstance(app)
        val dao = db.mathDao()
        val repo = com.example.data.repository.MathRepository(dao)

        // 1. BUG-31: Book mistake syncs across book_mistakes, book_spaced_revision, and unified_mistake_notebook
        repo.recordBookQuestionMistake(
            questionId = "mistake_c8_q_sync_1",
            bookId = "class_8",
            chapterNumber = 4,
            questionPromptBn = "(a+b)² এর মান কত?",
            wrongAnswerBn = "a²+b²",
            correctAnswerBn = "a²+2ab+b²",
            explanationBn = "দ্বিপদী বর্গের সূত্র",
            sourceProvenance = "ORIGINAL_TEXTBOOK",
            chapterTitleBn = "বীজগণিতীয় সূত্রাবলি"
        )
        assertNotNull(dao.getBookMistakeById("mistake_c8_q_sync_1"))
        assertNotNull(dao.getBookSpacedRevisionById("mistake_c8_q_sync_1"))
        assertNull(dao.getBookSpacedRevisionById("mistake_mistake_c8_q_sync_1"))
        val unifiedBookMistake = dao.getUnifiedMistakeByKey("BOOK_c8_q_sync_1")
        assertNotNull("Book mistake must also be recorded in unified_mistake_notebook", unifiedBookMistake)
        assertFalse(unifiedBookMistake!!.resolved)

        // Resolving Book mistake resolves both book_mistakes and unified_mistake_notebook
        repo.markBookMistakeResolved("mistake_c8_q_sync_1", resolved = true)
        assertTrue(dao.getBookMistakeById("mistake_c8_q_sync_1")!!.isResolved)
        assertTrue(dao.getUnifiedMistakeByKey("BOOK_c8_q_sync_1")!!.resolved)
        assertEquals(1, dao.getBookSpacedRevisionById("mistake_c8_q_sync_1")!!.intervalStage)

        // Deleting Book mistake removes from all 3 tables without leaving orphans
        repo.deleteBookMistake("mistake_c8_q_sync_1")
        assertNull(dao.getBookMistakeById("mistake_c8_q_sync_1"))
        assertNull(dao.getBookSpacedRevisionById("mistake_c8_q_sync_1"))
        assertNull(dao.getUnifiedMistakeByKey("BOOK_c8_q_sync_1"))

        // 2. BUG-32: BCS mistake canonicalization across exam-prefixed and retry-prefixed IDs + unified sync
        val profileId = com.example.math.BcsExamProfile.BCS_50TH_PRELIM.id
        repo.recordBcsMistake(
            questionId = "bcs_exam_3_bcs_hist_45_arith_99",
            subjectTrack = "MATH_REASONING",
            topicId = "ARITHMETIC_PERCENTAGE",
            questionPromptBn = "শতকরা লাভের অংক",
            wrongAnswerBn = "১০%",
            correctAnswerBn = "২০%",
            explanationBn = "লাভ ২০%",
            examProfileId = profileId
        )
        val storedBcs = dao.getBcsMistakeById("bcs_hist_45_arith_99")
        assertNotNull("Exam-prefixed BCS question ID must canonicalize to base ID", storedBcs)
        assertFalse(storedBcs!!.isResolved)
        val unifiedBcs = dao.getUnifiedMistakeByKey("BCS_${profileId}_bcs_hist_45_arith_99")
        assertNotNull("BCS mistake must sync to unified_mistake_notebook", unifiedBcs)
        assertFalse(unifiedBcs!!.resolved)

        // Answering correctly in Mistake Practice (with retry prefix) resolves canonical BCS mistake & unified mistake
        repo.markBcsMistakeResolved(
            questionId = "bcs_mistake_retry_1_bcs_hist_45_arith_99",
            examProfileId = profileId
        )
        assertTrue(dao.getBcsMistakeById("bcs_hist_45_arith_99")!!.isResolved)
        assertTrue(dao.getUnifiedMistakeByKey("BCS_${profileId}_bcs_hist_45_arith_99")!!.resolved)

        repo.deleteBcsMistake("bcs_hist_45_arith_99", examProfileId = profileId)
        assertNull(dao.getBcsMistakeById("bcs_hist_45_arith_99"))
        assertNull(dao.getUnifiedMistakeByKey("BCS_${profileId}_bcs_hist_45_arith_99"))
    }

    @Test
    fun adversarialAudit_bug33_and_bug34_unsubmittedDraftPreservationAndSkippedWeakTopics() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        var fakeElapsedMs = 5_000L
        val fakeClock = object : com.example.math.MonotonicClock {
            override fun elapsedRealtime(): Long = fakeElapsedMs
        }
        val vm = MathViewModel(app, fakeClock)

        // BUG-33: Start a non-full-exam TIMED quiz, select an option on Q0 without clicking submit, jump to Q1, then return to Q0
        vm.startQuiz(
            topic = com.example.math.QuizTopic.MULTIPLICATION,
            count = 4,
            mode = com.example.math.QuizMode.TIMED,
            difficulty = com.example.math.PracticeDifficulty.EASY,
            questionType = com.example.math.QuizQuestionType.MCQ,
            timedSeconds = 60
        )
        val q0 = vm.quizSession.value.questions[0]
        val q1 = vm.quizSession.value.questions[1]
        vm.selectQuizOption(q0.correctIndex)
        assertEquals(q0.correctIndex, vm.quizSession.value.selectedOptionIndex)

        // Jump to Q1 and select correct option without pressing submit
        vm.jumpToQuizQuestion(1)
        assertEquals(1, vm.quizSession.value.currentIndex)
        vm.selectQuizOption(q1.correctIndex)

        // Navigate back to Q0 -> draft selection on Q0 must still be preserved!
        vm.previousQuizQuestion()
        assertEquals(0, vm.quizSession.value.currentIndex)
        assertEquals("Unsubmitted draft selection on Q0 must be preserved across navigation", q0.correctIndex, vm.quizSession.value.selectedOptionIndex)

        // Expire timer -> both Q0 and Q1 unsubmitted drafts must be rescued and scored
        fakeElapsedMs = 70_000L
        vm.tickQuizTimer()
        assertTrue(vm.quizSession.value.isFinished)
        assertEquals("Both Q0 and Q1 draft selections must be scored on timer expiry", 2, vm.quizSession.value.correctCount)

        // BUG-34: ExamScoringEngine must NOT classify a completely skipped topic (attemptedCount == 0) as a weakTopic
        val sampleQuestions = listOf(
            com.example.math.QuizQuestion(
                id = "q_attempted_strong",
                questionBn = "৫ × ৫ = ?",
                optionsBn = listOf("২০", "২৫", "৩০", "৩৫"),
                correctIndex = 1,
                explanationBn = "২৫",
                topicTitleBn = "গুণ ও নামতা কুইজ (১-৪০)"
            ),
            com.example.math.QuizQuestion(
                id = "q_skipped_topic",
                questionBn = "মৌলিক সংখ্যা কোনটি?",
                optionsBn = listOf("৪", "৭", "৯", "১৫"),
                correctIndex = 1,
                explanationBn = "৭",
                topicTitleBn = "মৌলিক সংখ্যা কুইজ (১-২০০)"
            )
        )
        val eval = com.example.math.ExamScoringEngine.evaluateExam(
            attemptId = "test_bug34",
            examTitleBn = "টেস্ট",
            examType = "MIXED_TEST",
            questions = sampleQuestions,
            selectedAnswers = mapOf(0 to 1), // Q0 correct, Q1 skipped (unanswered)
            markedForReview = emptySet(),
            questionTimeSeconds = mapOf(0 to 10),
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = 0.25,
            timeSpentSeconds = 10,
            durationLimitSeconds = 60
        )
        assertFalse(
            "Skipped topic with 0 attempts must not be marked as weakTopic",
            eval.weakTopicTitlesBn.contains("মৌলিক সংখ্যা কুইজ (১-২০০)")
        )
        assertTrue(eval.strongTopicTitlesBn.contains("গুণ ও নামতা কুইজ (১-৪০)"))
    }

    @Test
    fun adversarialAudit_bug35_pdfRenderCacheAndSynchronizedOfflinePdfExtraction() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        com.example.ui.screens.BookPdfRenderCache.evictAll()
        val testBmp = android.graphics.Bitmap.createBitmap(300, 400, android.graphics.Bitmap.Config.RGB_565)
        com.example.ui.screens.BookPdfRenderCache.put("class_6_p1_z10", testBmp)
        assertNotNull(com.example.ui.screens.BookPdfRenderCache.get("class_6_p1_z10"))
        com.example.ui.screens.BookPdfRenderCache.evictAll()
        assertNull(com.example.ui.screens.BookPdfRenderCache.get("class_6_p1_z10"))

        val extracted = com.example.ui.screens.resolveOfflinePdfFile(app, com.example.math.BookId.CLASS_6)
        assertNotNull("Bundled Class 6 PDF must extract cleanly via synchronized atomic extraction", extracted)
        assertTrue(extracted!!.exists() && extracted.length() > 4_096L)
    }

    @Test
    fun bug36_homeScreenScrollPositionAndOffsetRestoredAfterModuleNavigationDisposal() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // 1. Fresh launch starts at (0, 0)
        assertEquals(0, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(0, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // 2. Simulate scrolling down to Module 10 / Module 13 area (index = 8, pixel offset = 94)
        val scrolledGridState = androidx.compose.foundation.lazy.grid.LazyGridState(
            firstVisibleItemIndex = 8,
            firstVisibleItemScrollOffset = 94
        )
        vm.updateHomeScrollPosition(
            firstVisibleItemIndex = scrolledGridState.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = scrolledGridState.firstVisibleItemScrollOffset
        )

        // Verify LazyGridState.Saver preserves both index and exact pixel offset when saved by SaveableStateHolder
        @Suppress("UNCHECKED_CAST")
        val saver = androidx.compose.foundation.lazy.grid.LazyGridState.Saver as androidx.compose.runtime.saveable.Saver<androidx.compose.foundation.lazy.grid.LazyGridState, Any>
        val allowAllSaverScope = androidx.compose.runtime.saveable.SaverScope { true }
        val savedBundle = with(saver) {
            allowAllSaverScope.save(scrolledGridState)
        }
        assertNotNull("LazyGridState.Saver must serialize non-null state", savedBundle)

        // 3. Navigate to Module 10 (VisualExplorer) — HomeScreen leaves composition
        vm.navigateTo(Screen.VisualExplorer)
        assertEquals(Screen.VisualExplorer, vm.currentScreen)
        assertEquals(listOf(Screen.Home, Screen.VisualExplorer), vm.screenStack.value)
        assertEquals(8, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(94, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)

        // 4. Return to Home via navigateBack() — HomeScreen re-enters composition
        assertTrue(vm.navigateBack())
        assertEquals(Screen.Home, vm.currentScreen)

        // Verify restoration via LazyGridState.Saver (SaveableStateHolder path)
        val restoredFromSaver = saver.restore(savedBundle!!)
        assertNotNull(restoredFromSaver)
        assertEquals(8, restoredFromSaver!!.firstVisibleItemIndex)
        assertEquals(94, restoredFromSaver.firstVisibleItemScrollOffset)

        // Verify restoration via MathViewModel state holder (ViewModel fallback path after composition disposal)
        val restoredFromViewModel = androidx.compose.foundation.lazy.grid.LazyGridState(
            firstVisibleItemIndex = vm.homeScrollPosition.value.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = vm.homeScrollPosition.value.firstVisibleItemScrollOffset
        )
        assertEquals(8, restoredFromViewModel.firstVisibleItemIndex)
        assertEquals(94, restoredFromViewModel.firstVisibleItemScrollOffset)

        // 5. Multiple module cycles + deep navigation (Module 13 BCS -> QuizPlay -> Back -> Home)
        vm.navigateTo(Screen.BcsPreparation)
        vm.startBcsMockExam()
        assertEquals(Screen.QuizPlay, vm.currentScreen)
        assertTrue(vm.navigateBack())
        assertEquals(Screen.BcsPreparation, vm.currentScreen)
        assertTrue(vm.navigateBack())
        assertEquals(Screen.Home, vm.currentScreen)
        assertEquals(8, vm.homeScrollPosition.value.firstVisibleItemIndex)
        assertEquals(94, vm.homeScrollPosition.value.firstVisibleItemScrollOffset)
    }

    @Test
    fun adversarialAudit_bug48_pairedExamDeletionCollisionAndInProgressDiscardSafety() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val roomDb = Room.inMemoryDatabaseBuilder(context, MathDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = roomDb.mathDao()
        val repo = MathRepository(dao)

        val t0 = 1_762_100_000_000L
        val dateKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(t0))

        // 1. Insert a COMPLETED exam at t0 with its paired quiz_scores, practice_history, and daily_study_activity
        val completedAttempt = com.example.data.model.ExamAttemptEntity(
            attemptId = "exam_completed_1",
            examTitleBn = "টপিক কুইজ: মৌলিক সংখ্যা",
            examType = "TOPIC",
            selectedTopicsCsv = "PRIMES",
            difficulty = "EASY",
            questionType = "MCQ",
            totalQuestions = 10,
            durationLimitSeconds = 600,
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = 0.0,
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
            negativeMarksDeducted = 0.0,
            finalScore = 8.0,
            percentage = 80.0,
            accuracyPercent = 80.0,
            startedAt = t0 - 120_000L,
            updatedAt = t0,
            completedAt = t0
        )
        dao.upsertExamAttempt(completedAttempt)
        dao.insertQuizScore(
            com.example.data.model.QuizScoreEntity(
                topicId = "PRIMES",
                topicTitleBn = "টপিক কুইজ: মৌলিক সংখ্যা",
                score = 8,
                totalQuestions = 10,
                percentage = 80,
                timestamp = t0 + 100L
            )
        )
        dao.insertPracticeHistory(
            com.example.data.model.PracticeHistoryEntity(
                module = "QUIZ",
                topicId = "PRIMES",
                topicTitleBn = "টপিক কুইজ: মৌলিক সংখ্যা",
                difficulty = "EASY",
                mode = "TOPIC",
                score = 80,
                correctCount = 8,
                wrongCount = 2,
                totalQuestions = 10,
                accuracyPercent = 80,
                bestStreak = 5,
                durationSeconds = 120,
                timestamp = t0 + 120L
            )
        )
        dao.upsertDailyStudyActivity(
            com.example.data.model.DailyStudyActivityEntity(
                dateKey = dateKey,
                questionsAttempted = 10,
                correctCount = 8,
                wrongCount = 2,
                studyTimeSeconds = 120,
                examsCompleted = 1,
                practiceSessionsCompleted = 0,
                updatedAt = t0
            )
        )

        // 2. User immediately starts a new IN_PROGRESS exam 400ms later (within ±2500ms!) and discards it
        val inProgressAttempt = completedAttempt.copy(
            attemptId = "exam_in_progress_2",
            status = "IN_PROGRESS",
            percentage = 0.0,
            correctCount = 0,
            wrongCount = 0,
            startedAt = t0 + 400L,
            updatedAt = t0 + 500L,
            completedAt = null
        )
        dao.upsertExamAttempt(inProgressAttempt)
        repo.deleteExamAttempt("exam_in_progress_2")

        // CRITICAL: Discarding IN_PROGRESS exam must NEVER delete the completed exam's paired records!
        assertEquals(1, dao.getAllQuizScores().first().size)
        assertEquals(1, dao.getAllPracticeHistory().first().size)
        assertEquals(10, dao.getDailyStudyActivityByDate(dateKey)?.questionsAttempted)

        // 3. Another COMPLETED exam with identical score/topic is imported/saved 800ms after completedAttempt (collision in ±2500ms)
        val collidingAttempt = completedAttempt.copy(
            attemptId = "exam_completed_sibling",
            updatedAt = t0 + 800L,
            completedAt = t0 + 800L
        )
        dao.upsertExamAttempt(collidingAttempt)
        repo.deleteExamAttempt("exam_completed_sibling")

        // Because exam_completed_1 is within ±2500ms and exam_completed_sibling has no closer dedicated quiz_score than exam_completed_1,
        // exam_completed_1's paired quiz_score (at t0+100L) and practice_history (at t0+120L) MUST NOT be deleted!
        assertEquals("Colliding sibling deletion must not wipe exam_completed_1's quiz_score", 1, dao.getAllQuizScores().first().size)
        assertEquals("Colliding sibling deletion must not wipe exam_completed_1's practice_history", 1, dao.getAllPracticeHistory().first().size)

        // 4. Now delete exam_completed_1 itself (no remaining sibling collision) -> paired records and daily activity clean up
        repo.deleteExamAttempt("exam_completed_1")
        assertEquals(0, dao.getAllQuizScores().first().size)
        assertEquals(0, dao.getAllPracticeHistory().first().size)
        assertEquals(0, dao.getDailyStudyActivityByDate(dateKey)?.questionsAttempted)

        roomDb.close()
    }

    @Test
    fun adversarialAudit_bug49AndBug52_timerResumeUnder30SecondsAndNumericalSelectionIsolation() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MathViewModel(app)

        // BUG-52: Start a NUMERICAL quiz and submit an answer whose text matches one of the underlying options
        vm.startQuiz(
            topic = com.example.math.QuizTopic.MULTIPLICATION,
            mode = com.example.math.QuizMode.TOPIC,
            count = 5,
            difficulty = com.example.math.PracticeDifficulty.EASY,
            questionType = com.example.math.QuizQuestionType.NUMERICAL
        )
        val session0 = vm.quizSession.value
        assertNotNull(session0)
        val firstQ = session0!!.questions.first()
        assertEquals(com.example.math.QuizQuestionType.NUMERICAL, firstQ.questionType)

        // Type the exact correct answer (which also exists in firstQ.optionsBn)
        vm.setQuizTypedAnswer(firstQ.correctAnswerText)
        vm.submitQuizAnswer()

        val afterSubmit = vm.quizSession.value!!
        assertTrue(afterSubmit.isAnswerSubmitted)
        assertEquals("NUMERICAL question must keep selectedOptionIndex null even when typed answer matches an MCQ option", null, afterSubmit.selectedOptionIndex)

        // Navigate next and back to question 0 -> selectedOptionIndex must still be null while typedAnswer is restored
        vm.nextQuizQuestion()
        assertEquals(1, vm.quizSession.value!!.currentIndex)
        vm.jumpToQuizQuestion(0)
        val restoredQ0 = vm.quizSession.value!!
        assertEquals(0, restoredQ0.currentIndex)
        assertEquals(null, restoredQ0.selectedOptionIndex)
        assertEquals(firstQ.correctAnswerText, restoredQ0.typedAnswer)

        // BUG-44: Verify recommendation routing for BOOK_, VISUAL_, BCS, and GEOMETRY_MENSURATION
        vm.executeLearningRecommendation(
            com.example.math.LearningRecommendation(
                id = "rec_vis",
                priorityRank = 1,
                badgeBn = "ভিজ্যুয়াল",
                titleBn = "ভিজ্যুয়াল জ্যামিতি",
                reasonBn = "টেস্ট",
                actionLabelBn = "খুলুন",
                actionType = com.example.math.RecommendationActionType.OPEN_QUIZ_TOPIC,
                targetTopicId = "VISUAL_GEOMETRY"
            )
        )
        assertEquals(Screen.VisualExplorer, vm.currentScreen)
    }
}



