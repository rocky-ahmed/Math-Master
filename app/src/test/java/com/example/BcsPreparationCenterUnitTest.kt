package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.BcsMistakeEntity
import com.example.data.model.BcsTopicProgressEntity
import com.example.data.model.BcsUserProfileEntity
import com.example.math.BcsCurriculumAndGuideRepository
import com.example.math.BcsDiagramType
import com.example.math.BcsExamProfile
import com.example.math.BcsMentalAbilityCategory
import com.example.math.BcsPreparationRepository
import com.example.math.BcsPreparationStage
import com.example.math.BcsProvenanceTier
import com.example.math.BcsQuestionSourceType
import com.example.math.BcsSyllabusUnit
import com.example.math.BcsSyllabusVersion
import com.example.math.BcsVerificationStatus
import com.example.ui.MathViewModel
import com.example.ui.Screen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BcsPreparationCenterUnitTest {

    @Test
    fun testBcsExamProfilesAndOfficialBpscMarksDistribution() {
        // 1. 50th BCS Preliminary (Current Default)
        val p50th = BcsExamProfile.BCS_50TH_PRELIM
        assertEquals(20, p50th.mathMarks)
        assertEquals(15, p50th.mentalAbilityMarks)
        assertEquals(35, p50th.totalSubjectMarks)
        assertEquals(0.50, p50th.officialNegativeMarkingPenalty, 0.001)
        assertEquals("৯ ডিসেম্বর ২০২৫", p50th.publishedDateBn)
        assertTrue(p50th.officialSourceUrl.contains("bpsc.gov.bd"))

        // 2. Regular BCS Written (Code 008 & 009)
        val pWritten = BcsExamProfile.BCS_WRITTEN_REGULAR
        assertEquals(50, pWritten.mathMarks)
        assertEquals(50, pWritten.mentalAbilityMarks)
        assertEquals(100, pWritten.totalSubjectMarks)
        assertEquals(0.0, pWritten.officialNegativeMarkingPenalty, 0.001)
        assertEquals(120, pWritten.durationMinutes)

        // 3. 51st Special BCS 2026 (Health Cadre Screening)
        val p51st = BcsExamProfile.BCS_51ST_SPECIAL
        assertEquals(10, p51st.mathMarks)
        assertEquals(10, p51st.mentalAbilityMarks)
        assertEquals(20, p51st.totalSubjectMarks)
        assertEquals(0.50, p51st.officialNegativeMarkingPenalty, 0.001)

        // 4. Legacy 35th–49th BCS Preliminary (Archived)
        val pLegacy = BcsExamProfile.BCS_LEGACY_PRELIM
        assertEquals(15, pLegacy.mathMarks)
        assertEquals(15, pLegacy.mentalAbilityMarks)
        assertEquals(30, pLegacy.totalSubjectMarks)

        // Verify fallback resolution
        assertEquals(BcsExamProfile.BCS_50TH_PRELIM, BcsExamProfile.fromIdOrDefault("INVALID_ID"))
        assertEquals(BcsExamProfile.BCS_51ST_SPECIAL, BcsExamProfile.fromIdOrDefault("BCS_51ST_SPECIAL_2026"))
    }

    @Test
    fun testBcsMentalAbilityCurriculumAndDiagrams() {
        // Verify all 6 BPSC categories exist
        assertEquals(6, BcsMentalAbilityCategory.entries.size)
        assertTrue(BcsMentalAbilityCategory.entries.any { it.id == "MECHANICAL_REASONING" })
        assertTrue(BcsMentalAbilityCategory.entries.any { it.id == "SPACE_RELATION" })
        assertTrue(BcsMentalAbilityCategory.entries.any { it.id == "VERBAL_REASONING" })
        assertTrue(BcsMentalAbilityCategory.entries.any { it.id == "PROBLEM_SOLVING" })
        assertTrue(BcsMentalAbilityCategory.entries.any { it.id == "SPELLING_LANGUAGE" })
        assertTrue(BcsMentalAbilityCategory.entries.any { it.id == "NUMERICAL_ABILITY" })

        val questions = BcsCurriculumAndGuideRepository.mentalAbilityQuestions
        assertTrue("Mental Ability bank must have questions", questions.isNotEmpty())

        questions.forEach { q ->
            assertTrue("Question ${q.id} prompt must not be blank", q.questionBn.isNotBlank())
            assertEquals("Question ${q.id} must have 4 options", 4, q.optionsBn.size)
            assertTrue("Question ${q.id} correctIndex in range", q.correctIndex in 0..3)
            assertTrue("Question ${q.id} explanation must not be blank", q.explanationBn.isNotBlank())
            assertTrue("Question ${q.id} must have valid correctAnswerBn", q.correctAnswerBn.isNotBlank())
        }

        // Verify diagram types present in question bank
        val gearQ = questions.firstOrNull { it.diagramType == BcsDiagramType.GEAR_ROTATION }
        assertNotNull("Gear rotation question must exist", gearQ)
        assertEquals("GEARS:3:CW", gearQ?.diagramData)

        val pulleyQ = questions.firstOrNull { it.diagramType == BcsDiagramType.PULLEY_SYSTEM }
        assertNotNull("Pulley system question must exist", pulleyQ)

        val compassQ = questions.firstOrNull { it.diagramType == BcsDiagramType.COMPASS_DIRECTION }
        assertNotNull("Compass direction question must exist", compassQ)

        val triangleQ = questions.firstOrNull { it.diagramType == BcsDiagramType.TRIANGLE_COUNT }
        assertNotNull("Triangle counting question must exist", triangleQ)
    }

    @Test
    fun testBcsMathGuide16PartLessonsAndIntegrations() {
        val lessons = BcsCurriculumAndGuideRepository.mathGuideLessons
        assertTrue("Must have structured lessons across 7 domains", lessons.size >= 5)

        lessons.forEach { lesson ->
            assertTrue("Lesson ${lesson.topicId} title must not be blank", lesson.topicTitleBn.isNotBlank())
            assertTrue("Lesson ${lesson.topicId} intro must not be blank", lesson.conceptIntroBn.isNotBlank())
            assertTrue("Lesson ${lesson.topicId} must have learning objectives", lesson.learningObjectivesBn.isNotEmpty())
            assertTrue("Lesson ${lesson.topicId} standard method must not be blank", lesson.standardMethodBn.isNotBlank())
            assertTrue("Lesson ${lesson.topicId} smart shortcut must not be blank", lesson.smartShortcutMethodBn.isNotBlank())
            assertTrue("Lesson ${lesson.topicId} shortcut proof must not be blank", lesson.shortcutProofAndWhyItWorksBn.isNotBlank())
            assertTrue("Lesson ${lesson.topicId} common mistakes must not be blank", lesson.commonMistakesWarningBn.isNotBlank())
            assertEquals("Lesson ${lesson.topicId} mini-check must have 4 options", 4, lesson.miniCheckOptionsBn.size)
            assertTrue("Lesson ${lesson.topicId} mini-check index in range", lesson.miniCheckCorrectIndex in 0..3)

            // Verify integration links
            if (lesson.linkedNctbBookId != null) {
                assertNotNull("NCTB link must have chapter number", lesson.linkedNctbChapterNumber)
                assertTrue("Linked chapter number must be >= 1", lesson.linkedNctbChapterNumber!! >= 1)
            }
        }

        // Unit-specific lesson queries
        val u1Lessons = BcsCurriculumAndGuideRepository.getLessonsForUnit(BcsSyllabusUnit.UNIT_1_ARITHMETIC)
        assertTrue("Unit 1 must have arithmetic lessons", u1Lessons.isNotEmpty())
        assertTrue(u1Lessons.any { it.topicId == "bcs_math_real_numbers" })
        assertTrue(u1Lessons.any { it.topicId == "bcs_math_percentage_ratio" })
    }

    @Test
    fun testBcsRoomV6MigrationAndPersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = MathDatabase.getInstance(context)
        val dao = db.mathDao()

        // 1. BCS User Profile persistence
        val testProfile = BcsUserProfileEntity(
            profileId = "test_user_profile",
            selectedExamProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
            onboardingCompleted = true,
            currentStageIndex = 2,
            dailyTargetMinutes = 45,
            dailyTargetQuestions = 25
        )
        dao.upsertBcsUserProfile(testProfile)
        val retrievedProfile = dao.getBcsUserProfile("test_user_profile")
        assertNotNull(retrievedProfile)
        assertEquals(BcsExamProfile.BCS_50TH_PRELIM.id, retrievedProfile?.selectedExamProfileId)
        assertEquals(45, retrievedProfile?.dailyTargetMinutes)
        assertEquals(2, retrievedProfile?.currentStageIndex)
        assertTrue(retrievedProfile?.onboardingCompleted == true)

        // 2. BCS Topic Progress persistence
        val testProgress = BcsTopicProgressEntity(
            topicId = "bcs_math_real_numbers",
            subjectTrack = "MATH",
            isGuideCompleted = true,
            miniCheckPassed = true,
            questionsAttempted = 10,
            questionsCorrect = 9,
            totalTimeSeconds = 300,
            masteryPercent = 90
        )
        dao.upsertBcsTopicProgress(testProgress)
        val retrievedProgress = dao.getBcsTopicProgressById("bcs_math_real_numbers")
        assertNotNull(retrievedProgress)
        assertTrue(retrievedProgress?.isGuideCompleted == true)
        assertTrue(retrievedProgress?.miniCheckPassed == true)
        assertEquals(90, retrievedProgress?.masteryPercent)

        // 3. BCS Mistake Notebook persistence and resolution
        val testMistake = BcsMistakeEntity(
            questionId = "test_q_mistake_1",
            subjectTrack = "MATH",
            topicId = "UNIT_1_ARITHMETIC",
            questionPromptBn = "চিনির মূল্য ২৫% বৃদ্ধিতে ব্যবহার কত কমাতে হবে?",
            wrongAnswerBn = "২৫%",
            correctAnswerBn = "২০%",
            explanationBn = "[২৫ / ১২৫] × ১০০% = ২০%",
            shortcutTipBn = "r / (১০০ + r) %",
            provenanceBadgeBn = "বিগত বিসিএস",
            mistakeCount = 1,
            isResolved = false
        )
        dao.upsertBcsMistake(testMistake)
        val retrievedMistake = dao.getBcsMistakeById("test_q_mistake_1")
        assertNotNull(retrievedMistake)
        assertFalse(retrievedMistake?.isResolved == true)
        assertEquals("২৫%", retrievedMistake?.wrongAnswerBn)
        assertEquals("২০%", retrievedMistake?.correctAnswerBn)

        // Mark resolved
        dao.upsertBcsMistake(retrievedMistake!!.copy(isResolved = true, retrySuccessCount = 1))
        val resolvedMistake = dao.getBcsMistakeById("test_q_mistake_1")
        assertTrue(resolvedMistake?.isResolved == true)
        assertEquals(1, resolvedMistake?.retrySuccessCount)

        // Clean up
        dao.deleteBcsMistakeById("test_q_mistake_1")
        val deletedMistake = dao.getBcsMistakeById("test_q_mistake_1")
        assertTrue(deletedMistake == null)
    }

    @Test
    fun testBcsPreparationStages() {
        assertEquals(8, BcsPreparationStage.entries.size)
        assertEquals(BcsPreparationStage.FOUNDATION_BUILDING, BcsPreparationStage.fromNumber(1))
        assertEquals(BcsPreparationStage.CONCEPT_MASTERY, BcsPreparationStage.fromNumber(2))
        assertEquals(BcsPreparationStage.STANDARD_PRACTICE, BcsPreparationStage.fromNumber(3))
        assertEquals(BcsPreparationStage.ADVANCED_PRACTICE, BcsPreparationStage.fromNumber(4))
        assertEquals(BcsPreparationStage.SPEED_ACCURACY, BcsPreparationStage.fromNumber(5))
        assertEquals(BcsPreparationStage.PREVIOUS_QUESTION_REVISION, BcsPreparationStage.fromNumber(6))
        assertEquals(BcsPreparationStage.MOCK_EXAMINATION, BcsPreparationStage.fromNumber(7))
        assertEquals(BcsPreparationStage.FINAL_REVISION, BcsPreparationStage.fromNumber(8))
    }

    @Test
    fun testViewModelBcsIntegration() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = MathViewModel(app)

        // Default exam profile
        assertEquals(BcsExamProfile.BCS_50TH_PRELIM, viewModel.selectedBcsExamProfile.value)
        assertEquals(BcsSyllabusVersion.BCS_50TH_CURRENT, viewModel.selectedBcsSyllabusVersion.value)

        // Switch to legacy
        viewModel.setBcsExamProfile(BcsExamProfile.BCS_LEGACY_PRELIM)
        assertEquals(BcsExamProfile.BCS_LEGACY_PRELIM, viewModel.selectedBcsExamProfile.value)
        assertEquals(BcsSyllabusVersion.BCS_LEGACY_15, viewModel.selectedBcsSyllabusVersion.value)

        // Switch back to 50th
        viewModel.setBcsExamProfile(BcsExamProfile.BCS_50TH_PRELIM)
        assertEquals(BcsExamProfile.BCS_50TH_PRELIM, viewModel.selectedBcsExamProfile.value)
        assertEquals(BcsSyllabusVersion.BCS_50TH_CURRENT, viewModel.selectedBcsSyllabusVersion.value)

        // Save onboarding
        viewModel.saveBcsOnboarding(
            profile = BcsExamProfile.BCS_50TH_PRELIM,
            stage = BcsPreparationStage.CONCEPT_MASTERY,
            dailyMinutes = 45,
            dailyQuestions = 25
        )

        // Verify navigation to BCS screen
        viewModel.navigateTo(Screen.BcsPreparation)
        assertEquals(Screen.BcsPreparation, viewModel.currentScreen)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen)
    }

    @Test
    fun testHomeNavigationAndBcsMathematicalReasoningCard() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = MathViewModel(app)

        // 1. Initial screen is Home
        assertEquals(Screen.Home, viewModel.currentScreen)

        // 2. Open BCS Mathematical Reasoning from Home Screen featured card
        viewModel.navigateTo(Screen.BcsPreparation)
        assertEquals(Screen.BcsPreparation, viewModel.currentScreen)

        // 3. Verify Back navigation safely returns to Home
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen)

        // 4. Verify Book module featured card navigation remains intact
        viewModel.openBookHub()
        assertEquals(Screen.BookHub, viewModel.currentScreen)
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen)

        // 5. Verify the 5 core Bottom Navigation destinations navigate cleanly
        viewModel.navigateToTab(Screen.Tables)
        assertEquals(Screen.Tables, viewModel.currentScreen)

        viewModel.navigateToTab(Screen.Dashboard)
        assertEquals(Screen.Dashboard, viewModel.currentScreen)

        viewModel.navigateToTab(Screen.Bookmarks)
        assertEquals(Screen.Bookmarks, viewModel.currentScreen)

        viewModel.navigateToTab(Screen.Quizzes)
        assertEquals(Screen.Quizzes, viewModel.currentScreen)

        viewModel.navigateToHome()
        assertEquals(Screen.Home, viewModel.currentScreen)
    }

    @Test
    fun testBcsMathematicalReasoning14thGridModuleIntegrity() {
        // 1. Verify SectionKey has BCS_MATH
        val bcsKey = com.example.ui.customization.SectionKey.BCS_MATH
        assertNotNull(bcsKey)
        assertEquals("section_bcs_math", bcsKey.id)
        assertTrue(bcsKey.titleBn.contains("বিসিএস গাণিতিক যুক্তি"))
        assertEquals(com.example.ui.customization.AvailableIcon.SCHOOL, bcsKey.defaultIcon)

        // 2. Total unique section keys is now 14
        assertEquals(14, com.example.ui.customization.SectionKey.entries.size)
    }

    @Test
    fun testModule13And14TransformationUpgrade_9Domains_14Modes_CrossModuleBridge_and_9BookModes() {
        // 1. Verify all 6 BCS Exam Profiles (including separate Code 008 and Code 009)
        assertEquals(6, BcsExamProfile.entries.size)
        assertEquals(50, BcsExamProfile.BCS_WRITTEN_008_MATH.mathMarks)
        assertEquals(0, BcsExamProfile.BCS_WRITTEN_008_MATH.mentalAbilityMarks)
        assertEquals(0, BcsExamProfile.BCS_WRITTEN_009_MENTAL.mathMarks)
        assertEquals(50, BcsExamProfile.BCS_WRITTEN_009_MENTAL.mentalAbilityMarks)

        // 2. Verify all 9 Mathematical Reasoning Domains & 11 19-part Guide Lessons
        assertEquals(9, com.example.math.BcsMathDomain.entries.size)
        val allLessons = BcsCurriculumAndGuideRepository.mathGuideLessons
        assertEquals(11, allLessons.size)
        com.example.math.BcsMathDomain.entries.forEach { domain ->
            val domainLessons = BcsCurriculumAndGuideRepository.getLessonsForDomain(domain)
            assertTrue("Domain ${domain.id} must have at least 1 guide lesson", domainLessons.isNotEmpty())
        }
        allLessons.forEach { lesson ->
            assertTrue(lesson.simpleConceptExplanationBn.isNotBlank())
            assertTrue(lesson.writtenCode008RubricStepsBn.isNotEmpty())
            assertTrue(lesson.practicalApplicationBn.isNotBlank())
        }

        // 3. Verify all 6 Mental Ability Category Guides & 12 Mental Ability Questions
        assertEquals(6, BcsCurriculumAndGuideRepository.mentalAbilityCategoryGuides.size)
        BcsMentalAbilityCategory.entries.forEach { cat ->
            val guide = BcsCurriculumAndGuideRepository.getMentalAbilityGuideForCategory(cat)
            assertNotNull("Category ${cat.id} must have a conceptual guide", guide)
            assertTrue(guide!!.solvingRulesBn.isNotEmpty())
        }
        assertEquals(12, BcsCurriculumAndGuideRepository.mentalAbilityQuestions.size)

        // 4. Verify all 14 BCS Practice & Mock Modes generate valid questions
        assertEquals(14, com.example.math.BcsPracticeMode.entries.size)
        com.example.math.BcsPracticeMode.entries.forEach { mode ->
            val generated = BcsPreparationRepository.generateBcsModeExamQuestions(
                mode = mode,
                examProfile = BcsExamProfile.BCS_50TH_PRELIM
            )
            assertTrue("Mode ${mode.id} must generate non-empty exam questions", generated.isNotEmpty())
        }

        // 5. Verify Cross-Module Curriculum Bridge between Module 13 and Module 14
        val allMappings = com.example.math.CrossModuleCurriculumBridge.mappings
        assertTrue(allMappings.size >= 12)
        val c9TrigoMappings = com.example.math.CrossModuleCurriculumBridge.getMappingsForBookChapter(
            com.example.math.BookId.CLASS_9_10,
            9
        )
        assertTrue(c9TrigoMappings.isNotEmpty())

        // 6. Verify Module 14 9 BookPracticeModes, Edition Metadata & Enriched Page Content
        assertEquals(9, com.example.math.BookPracticeMode.entries.size)
        com.example.math.BookId.entries.forEach { book ->
            assertTrue(book.editionMetadata.academicYearBn.isNotBlank())
            val pageContent = com.example.math.BookCurriculumRepository.getStructuredPageContent(book, 1)
            assertTrue(pageContent.rulesOnPageBn.isNotEmpty())
            assertTrue(pageContent.answerKeyOnPageBn.isNotEmpty())
        }
        com.example.math.BookPracticeMode.entries.forEach { mode ->
            val qs = com.example.math.BookQuestionEngine.buildSessionQuestions(
                bookId = com.example.math.BookId.CLASS_9_10,
                chapterNumber = 1,
                mode = mode
            )
            assertTrue("Book practice mode ${mode.id} must return questions", qs.isNotEmpty())
        }
    }

    @Test
    fun testFinalBpscProfileAccuracy_51stSpecial_Written008_009_LegacyScoping_and_DomainMapping() {
        // A. 51st Special BCS 2026: 10 Math + 10 Mental Ability = 20 Marks, strictly Health Cadre
        val p51 = BcsExamProfile.BCS_51ST_SPECIAL
        assertEquals(10, p51.mathMarks)
        assertEquals(10, p51.mentalAbilityMarks)
        assertEquals(20, p51.totalSubjectMarks)
        assertTrue(p51.descriptionBn.contains("স্বাস্থ্য ক্যাডার"))
        assertTrue(p51.descriptionBn.contains("১০ নম্বর"))
        assertTrue(p51.officialPdfUrl.contains("2026-03-04"))
        val mock51Questions = BcsPreparationRepository.generateBcsModeExamQuestions(
            mode = com.example.math.BcsPracticeMode.BCS_51ST_SPECIAL_20_MOCK,
            examProfile = p51
        )
        assertEquals(20, mock51Questions.size)

        // B. Written Code 008: 50 Marks official syllabus, configurable practice count = 10
        val p008 = BcsExamProfile.BCS_WRITTEN_008_MATH
        assertEquals(50, p008.mathMarks)
        assertEquals(0, p008.mentalAbilityMarks)
        assertEquals(50, p008.totalSubjectMarks)
        assertEquals(10, p008.defaultQuestionCount)
        assertEquals(
            com.example.math.BcsPracticeMode.BCS_WRITTEN_008_PRACTICE.defaultQuestionCount,
            p008.defaultQuestionCount
        )
        assertTrue(p008.officialPdfUrl.contains("WRITTEN"))
        assertTrue(p008.descriptionBn.contains("কাস্টমাইজযোগ্য"))

        // C. Written Code 009: 50 Marks official syllabus across 6 cognitive areas
        val p009 = BcsExamProfile.BCS_WRITTEN_009_MENTAL
        assertEquals(0, p009.mathMarks)
        assertEquals(50, p009.mentalAbilityMarks)
        assertEquals(50, p009.totalSubjectMarks)
        assertEquals(50, p009.defaultQuestionCount)
        assertTrue(p009.officialPdfUrl.contains("WRITTEN"))
        assertTrue(p009.descriptionBn.contains("বিমূর্ত যুক্তি"))

        // D. Legacy BCS Preliminary: Strictly scoped to 35th–49th BCS (15 Math + 15 Mental = 30 Marks)
        val pLegacy = BcsExamProfile.BCS_LEGACY_PRELIM
        assertEquals("BCS_LEGACY_35_49", pLegacy.id)
        assertEquals(15, pLegacy.mathMarks)
        assertEquals(15, pLegacy.mentalAbilityMarks)
        assertEquals(30, pLegacy.totalSubjectMarks)
        assertTrue(pLegacy.descriptionBn.contains("৩৫তম থেকে ৪৯তম"))
        assertTrue(pLegacy.descriptionBn.contains("১০ম–৩৪তম"))

        // E. Verify all 9 BcsMathDomain entries explicitly document their official BPSC syllabus mapping
        com.example.math.BcsMathDomain.entries.forEach { domain ->
            assertTrue(domain.isAppPedagogicalGrouping)
            assertTrue(
                "Domain ${domain.id} must have officialSyllabusMappingBn",
                domain.officialSyllabusMappingBn.isNotBlank()
            )
        }
    }

    @Test
    fun testBookOfflinePackagedNctbPdfs_RealMultiMegabyteAssets_and_ContentCompleteness47Chapters() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Verify all 4 packaged offline PDFs in app/src/main/assets/books/*.pdf are real multi-megabyte NCTB PDFs
        val expectedAssetSizes = mapOf(
            com.example.math.BookId.CLASS_6 to 5_372_426L,
            com.example.math.BookId.CLASS_7 to 6_391_697L,
            com.example.math.BookId.CLASS_8 to 6_459_157L,
            com.example.math.BookId.CLASS_9_10 to 12_969_807L
        )
        com.example.math.BookId.entries.forEach { bookId ->
            val assetFile = java.io.File("app/src/main/assets/${bookId.assetFileName}")
                .takeIf { it.exists() }
                ?: java.io.File("../app/src/main/assets/${bookId.assetFileName}")
            assertTrue("Packaged asset PDF for ${bookId.id} must exist", assetFile.exists())
            assertTrue(
                "Packaged asset PDF for ${bookId.id} must be >= 5 MB optimized NCTB PDF (not a placeholder)",
                assetFile.length() >= 5_000_000L
            )
            assertEquals(expectedAssetSizes[bookId], assetFile.length())

            // Verify resolveOfflinePdfFile unpacks the genuine asset into cacheDir
            val resolvedFile = com.example.ui.screens.resolveOfflinePdfFile(context, bookId)
            assertNotNull("resolveOfflinePdfFile must return non-null PDF for ${bookId.id}", resolvedFile)
            assertTrue(
                "Resolved offline PDF for ${bookId.id} must be >= 5 MB",
                resolvedFile!!.length() >= 5_000_000L
            )
            val header = resolvedFile.inputStream().use { input ->
                val buf = ByteArray(5)
                input.read(buf)
                String(buf)
            }
            assertEquals("%PDF-", header)
        }

        // 2. Verify exact 47-chapter content completeness & 499 multi-format practice questions
        val allChapters = com.example.math.BookCurriculumRepository.getAllChapters()
        assertEquals(47, allChapters.size)
        assertEquals(64, allChapters.sumOf { it.subsections.size })
        assertEquals(95, allChapters.sumOf { it.formulas.size })
        assertEquals(59, allChapters.sumOf { it.workedExamples.size })
        assertEquals(19, allChapters.sumOf { it.theoremsAndConstructions.size })
        assertEquals(109, allChapters.sumOf { it.exercisesListBn.size })

        val allQuestions = allChapters.flatMap { ch ->
            com.example.math.BookQuestionEngine.getQuestionsForChapter(ch.bookId, ch.chapterNumber)
        }
        assertTrue("Total practice questions across 47 chapters must be >= 470", allQuestions.size >= 470)
        assertTrue(
            allQuestions.count { it.provenance == com.example.math.BookQuestionProvenance.ORIGINAL_TEXTBOOK } >= 170
        )
        assertTrue(
            allQuestions.count { it.provenance == com.example.math.BookQuestionProvenance.CONCEPT_DERIVED } >= 180
        )
        assertTrue(
            allQuestions.count { it.provenance == com.example.math.BookQuestionProvenance.GENERATED } >= 47
        )
    }

    @Test
    fun testRoomV8ProfileIsolation_and_MathEdgeCaseEvaluation() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = MathDatabase.getInstance(context)
        val dao = db.mathDao()

        // 1. Verify profile-isolated BCS Topic Progress across 50th Prelim, Written 008, Written 009, and 51st Special
        val p50Progress = BcsTopicProgressEntity(
            topicId = BcsTopicProgressEntity.buildScopedKey(BcsExamProfile.BCS_50TH_PRELIM.id, "bcs_math_real_numbers"),
            examProfileId = BcsExamProfile.BCS_50TH_PRELIM.id,
            subjectTrack = "MATH",
            isGuideCompleted = true,
            miniCheckPassed = true,
            questionsAttempted = 20,
            questionsCorrect = 18,
            totalTimeSeconds = 600,
            masteryPercent = 90
        )
        val p008Progress = BcsTopicProgressEntity(
            topicId = BcsTopicProgressEntity.buildScopedKey(BcsExamProfile.BCS_WRITTEN_008_MATH.id, "bcs_math_real_numbers"),
            examProfileId = BcsExamProfile.BCS_WRITTEN_008_MATH.id,
            subjectTrack = "MATH",
            isGuideCompleted = false,
            miniCheckPassed = false,
            questionsAttempted = 5,
            questionsCorrect = 3,
            totalTimeSeconds = 900,
            masteryPercent = 60
        )
        dao.upsertBcsTopicProgress(p50Progress)
        dao.upsertBcsTopicProgress(p008Progress)

        val fetched50 = dao.getBcsTopicProgressById(p50Progress.topicId)
        val fetched008 = dao.getBcsTopicProgressById(p008Progress.topicId)
        assertNotNull(fetched50)
        assertNotNull(fetched008)
        assertEquals(90, fetched50?.masteryPercent)
        assertEquals(60, fetched008?.masteryPercent)
        assertEquals("bcs_math_real_numbers", fetched50?.rawTopicId)
        assertEquals("bcs_math_real_numbers", fetched008?.rawTopicId)

        // 2. Verify mathematical answer evaluation across Bengali/English digits, negatives, decimals, and large numbers
        val numericSample = com.example.math.BookPracticeQuestion(
            id = "test_num_edge",
            bookId = com.example.math.BookId.CLASS_6,
            chapterNumber = 3,
            lessonId = "c6_ch3_l1",
            format = com.example.math.BookQuestionFormat.NUMERICAL_INPUT,
            difficultyMode = com.example.math.BookPracticeMode.STANDARD,
            provenance = com.example.math.BookQuestionProvenance.GENERATED,
            promptBn = "মান নির্ণয় কর",
            optionsBn = emptyList(),
            correctOptionIndices = emptySet(),
            correctTextAnswer = "-১৫.৫",
            hintBn = "",
            stepByStepSolutionBn = emptyList(),
            explanationBn = "",
            sourcePrintedPage = 60
        )
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(numericSample, emptySet<Int>(), "-১৫.৫").first)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(numericSample, emptySet<Int>(), "-15.5").first)
        assertFalse(com.example.math.BookQuestionEngine.evaluateQuestion(numericSample, emptySet<Int>(), "১৫.৫").first)

        val largeNumSample = numericSample.copy(
            correctTextAnswer = "৯৮৭৫৪৩০"
        )
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(largeNumSample, emptySet<Int>(), "৯৮৭৫৪৩০").first)
        assertTrue(com.example.math.BookQuestionEngine.evaluateQuestion(largeNumSample, emptySet<Int>(), "9875430").first)
    }
}
