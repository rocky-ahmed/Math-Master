package com.example

import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.TopicAnalyticsEntity
import com.example.math.AnalyticsTimeFilter
import com.example.math.CameraPreset3D
import com.example.math.LearningAnalyticsEngine
import com.example.math.Math3DEngine
import com.example.math.OrbitCameraState
import com.example.math.PracticeDifficulty
import com.example.math.QuizGenerator
import com.example.math.QuizMode
import com.example.math.QuizQuestionType
import com.example.math.QuizTopic
import com.example.math.SharedMathEngine
import com.example.math.Solid3DShapeType
import com.example.math.VisualMathEngine
import com.example.ui.QuizSessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Unit tests for Visual Math Explorer (2D & 3D Solid Studio), Progress & Statistics
 * Analytics Engine fixes (BUG-41..45), and Interactive Quiz Engine fixes (BUG-47..52).
 */
class VisualMathExplorerUnitTest {

    // -------------------------------------------------------------
    // 1. GEOMETRIC CALCULATIONS & BOUNDARY CONDITIONS
    // -------------------------------------------------------------

    @Test
    fun testRectangleCalculation() {
        val res = VisualMathEngine.calculateRectangle(8.0, 5.0)
        assertTrue(res.isValid)
        assertEquals(40.0, res.primaryArea, 0.001)
        assertEquals(26.0, res.primaryPerimeterOrCircumference, 0.001)
        assertTrue(res.explanationStepsBn.isNotEmpty())
        assertTrue(res.formulaExpressionBn.contains("A = a · b"))
    }

    @Test
    fun testSquareCalculation() {
        val res = VisualMathEngine.calculateSquare(6.0)
        assertTrue(res.isValid)
        assertEquals(36.0, res.primaryArea, 0.001)
        assertEquals(24.0, res.primaryPerimeterOrCircumference, 0.001)
        assertTrue(res.explanationStepsBn.any { it.contains("কর্ণ") })
    }

    @Test
    fun testTriangleCalculationInsufficientInputs() {
        val res = VisualMathEngine.calculateTriangle(10.0, 7.0)
        assertTrue(res.isValid)
        assertEquals(35.0, res.primaryArea, 0.001)
        assertFalse(res.isPerimeterExact)
        assertEquals(0.0, res.primaryPerimeterOrCircumference, 0.001)
        assertNotNull(res.perimeterNoteBn)
        assertTrue(res.explanationStepsBn.any { it.contains("বাকি দুটি বাহুর দৈর্ঘ্য") })
    }

    @Test
    fun testTriangleCalculationWithValidSufficientSides() {
        val res = VisualMathEngine.calculateTriangle(base = 6.0, height = 4.0, sideA = 5.0, sideC = 5.0)
        assertTrue(res.isValid)
        assertTrue(res.isPerimeterExact)
        assertEquals(12.0, res.primaryArea, 0.001)
        assertEquals(16.0, res.primaryPerimeterOrCircumference, 0.001)
        assertTrue(res.explanationStepsBn.any { it.contains("পরিসীমা: তিন বাহুর সমষ্টি P = a + b + c") })
    }

    @Test
    fun testTriangleCalculationWithInvalidSides() {
        val invalidSides = VisualMathEngine.calculateTriangle(base = 10.0, height = 3.0, sideA = 2.0, sideC = 2.0)
        assertFalse(invalidSides.isValid)
        assertNotNull(invalidSides.errorMessageBn)

        val shortSide = VisualMathEngine.calculateTriangle(base = 6.0, height = 4.0, sideA = 2.0, sideC = 5.0)
        assertFalse(shortSide.isValid)
        assertNotNull(shortSide.errorMessageBn)
    }

    @Test
    fun testCircleCalculation() {
        val res = VisualMathEngine.calculateCircle(7.0)
        assertTrue(res.isValid)
        val expectedArea = PI * 49.0
        val expectedCircumference = 2 * PI * 7.0
        assertEquals(expectedArea, res.primaryArea, 0.01)
        assertEquals(expectedCircumference, res.primaryPerimeterOrCircumference, 0.01)
        assertTrue(res.explanationStepsBn.any { it.contains("ব্যাসার্ধ") })
    }

    @Test
    fun testGeometryInvalidInputsHandledSafely() {
        val zeroRect = VisualMathEngine.calculateRectangle(0.0, 5.0)
        assertFalse(zeroRect.isValid)
        assertNotNull(zeroRect.errorMessageBn)

        val negSquare = VisualMathEngine.calculateSquare(-4.0)
        assertFalse(negSquare.isValid)
        assertNotNull(negSquare.errorMessageBn)

        val negCircle = VisualMathEngine.calculateCircle(-1.0)
        assertFalse(negCircle.isValid)
        assertNotNull(negCircle.errorMessageBn)

        val zeroTriangle = VisualMathEngine.calculateTriangle(0.0, 0.0)
        assertFalse(zeroTriangle.isValid)
        assertNotNull(zeroTriangle.errorMessageBn)
    }

    // -------------------------------------------------------------
    // 2. VISUAL ALGEBRA (BALANCE-SCALE LINEAR EQUATIONS)
    // -------------------------------------------------------------

    @Test
    fun testVisualAlgebraStandardEquation() {
        val res = VisualMathEngine.solveVisualAlgebra(2.0, 4.0, 14.0)
        assertTrue(res.isSolvable)
        assertEquals(5.0, res.solutionX, 0.001)
        assertTrue(res.steps.size >= 2)
        assertTrue(res.verificationBn.contains("LHS") && res.verificationBn.contains("RHS"))
    }

    @Test
    fun testVisualAlgebraNegativeConstant() {
        val res = VisualMathEngine.solveVisualAlgebra(4.0, -2.0, 14.0)
        assertTrue(res.isSolvable)
        assertEquals(4.0, res.solutionX, 0.001)
    }

    @Test
    fun testVisualAlgebraZeroCoefficientError() {
        val res = VisualMathEngine.solveVisualAlgebra(0.0, 5.0, 10.0)
        assertFalse(res.isSolvable)
        assertNotNull(res.errorMessageBn)
    }

    // -------------------------------------------------------------
    // 3. NUMBER LINE & FRACTION ANALYSIS
    // -------------------------------------------------------------

    @Test
    fun testProperFractionAnalysis() {
        val frac = VisualMathEngine.analyzeFraction(3, 4)
        assertEquals(0.75, frac.decimalValue, 0.001)
        assertEquals(3, frac.simplifiedNumerator)
        assertEquals(4, frac.simplifiedDenominator)
        assertTrue(frac.fractionTypeBn.contains("প্রকৃত"))
        assertTrue(frac.comparisonExplanationBn.contains("০"))
    }

    @Test
    fun testImproperFractionSimplification() {
        val frac = VisualMathEngine.analyzeFraction(6, 4)
        assertEquals(1.5, frac.decimalValue, 0.001)
        assertEquals(3, frac.simplifiedNumerator)
        assertEquals(2, frac.simplifiedDenominator)
        assertTrue(frac.fractionTypeBn.contains("অপ্রকৃত"))
    }

    @Test
    fun testZeroDenominatorHandledSafely() {
        val frac = VisualMathEngine.analyzeFraction(5, 0)
        assertFalse(frac.isDefined)
        assertTrue(frac.decimalValue.isNaN())
        assertTrue(frac.comparisonExplanationBn.contains("অসংজ্ঞায়িত"))
    }

    // -------------------------------------------------------------
    // 4. FORMULA VISUALIZER PRESETS & CALCULATIONS
    // -------------------------------------------------------------

    @Test
    fun testPythagorasFormulaVisualization() {
        val pythagorasModel = VisualMathEngine.formulaVisualizerList.find { it.id == "vis_pythagoras" }
        assertNotNull(pythagorasModel)

        val result = pythagorasModel!!.calculate(3.0, 4.0, 0.0)
        assertEquals(5.0, result.outputValue, 0.001)
        assertTrue(result.calculationStepsBn.any { it.contains("c² = a² + b²") })
    }

    @Test
    fun testSimpleInterestFormulaVisualization() {
        val interestModel = VisualMathEngine.formulaVisualizerList.find { it.id == "vis_simple_interest" }
        assertNotNull(interestModel)

        val result = interestModel!!.calculate(1000.0, 2.0, 10.0)
        assertEquals(200.0, result.outputValue, 0.001)
        assertTrue(result.calculationStepsBn.any { it.contains("সুদ-আসল") })
    }

    @Test
    fun testCubeVolumeFormulaVisualization() {
        val cubeModel = VisualMathEngine.formulaVisualizerList.find { it.id == "vis_cube_vol" }
        assertNotNull(cubeModel)

        val result = cubeModel!!.calculate(4.0, 0.0, 0.0)
        assertEquals(64.0, result.outputValue, 0.001)
        assertTrue(result.calculationStepsBn.any { it.contains("ঘন একক") })
    }

    @Test
    fun testAnglesLinearGraphRatioPatternAndTransformations() {
        val angle = VisualMathEngine.calculateAngle(35.0)
        assertEquals(55.0, angle.complementaryDegrees!!, 0.001)
        assertEquals(145.0, angle.supplementaryDegrees!!, 0.001)

        val graph = VisualMathEngine.evaluateLinearGraph(2.0, -4.0, 3.0)
        assertEquals(2.0, graph.sampleY, 0.001)
        assertEquals(2.0, graph.xIntercept!!, 0.001)
        assertEquals(-4.0, graph.yIntercept, 0.001)

        val ratio = VisualMathEngine.analyzeRatioAndPercentage(6, 9, 40)
        assertEquals(2, ratio.simplifiedA)
        assertEquals(3, ratio.simplifiedB)
        assertEquals(40.0, ratio.shareAPercent, 0.001)

        val pattern = VisualMathEngine.generateVisualPattern("square", 5)
        assertEquals(listOf(1, 4, 9, 16, 25), pattern.terms)

        val transform = VisualMathEngine.calculate2DTransformation(3.0, 4.0, "reflect_x")
        assertEquals(3.0, transform.transformedX, 0.001)
        assertEquals(-4.0, transform.transformedY, 0.001)
    }

    // -------------------------------------------------------------
    // 5. 3D SOLID GEOMETRY ENGINE & NET UNFOLDING TESTS (Math3DEngine)
    // -------------------------------------------------------------

    @Test
    fun testMath3DEngineMensurationAllSixSolids() {
        // 1. Cube (a = 4) -> V = 64, TSA = 96, LSA = 64, d = 4*sqrt(3)
        val cube = Math3DEngine.calculateSolidMetrics(Solid3DShapeType.CUBE, p1 = 4.0)
        assertEquals(64.0, cube.volume, 0.001)
        assertEquals(96.0, cube.totalSurfaceArea, 0.001)
        assertEquals(64.0, cube.lateralSurfaceArea, 0.001)
        assertEquals(4.0 * sqrt(3.0), cube.secondaryMetricValue, 0.001)
        assertEquals(6, cube.facesCount)
        assertEquals(12, cube.edgesCount)
        assertEquals(8, cube.verticesCount)

        // 2. Cuboid (l=5, w=4, h=3) -> V = 60, TSA = 94, LSA = 54, d = sqrt(50)
        val cuboid = Math3DEngine.calculateSolidMetrics(
            Solid3DShapeType.CUBOID,
            p1 = 5.0,
            p2 = 4.0,
            p3 = 3.0
        )
        assertEquals(60.0, cuboid.volume, 0.001)
        assertEquals(94.0, cuboid.totalSurfaceArea, 0.001)
        assertEquals(54.0, cuboid.lateralSurfaceArea, 0.001)
        assertEquals(sqrt(50.0), cuboid.secondaryMetricValue, 0.001)

        // 3. Cylinder (r=3, h=5) -> V = 45*pi, LSA = 30*pi, TSA = 48*pi
        val cylinder = Math3DEngine.calculateSolidMetrics(
            Solid3DShapeType.CYLINDER,
            p1 = 3.0,
            p2 = 5.0
        )
        assertEquals(45.0 * PI, cylinder.volume, 0.01)
        assertEquals(30.0 * PI, cylinder.lateralSurfaceArea, 0.01)
        assertEquals(48.0 * PI, cylinder.totalSurfaceArea, 0.01)

        // 4. Cone (r=3, h=4) -> l = 5, V = 12*pi, LSA = 15*pi, TSA = 24*pi
        val cone = Math3DEngine.calculateSolidMetrics(
            Solid3DShapeType.CONE,
            p1 = 3.0,
            p2 = 4.0
        )
        assertEquals(5.0, cone.secondaryMetricValue, 0.001)
        assertEquals(12.0 * PI, cone.volume, 0.01)
        assertEquals(15.0 * PI, cone.lateralSurfaceArea, 0.01)
        assertEquals(24.0 * PI, cone.totalSurfaceArea, 0.01)

        // 5. Sphere (r=3) -> V = 36*pi, TSA = 36*pi
        val sphere = Math3DEngine.calculateSolidMetrics(
            Solid3DShapeType.SPHERE,
            p1 = 3.0
        )
        assertEquals(36.0 * PI, sphere.volume, 0.01)
        assertEquals(36.0 * PI, sphere.totalSurfaceArea, 0.01)

        // 6. Triangular Prism (b=6, h_t=4, L=10) -> BaseArea = 12, V = 120, LSA = 160, TSA = 184
        val prism = Math3DEngine.calculateSolidMetrics(
            Solid3DShapeType.TRIANGULAR_PRISM,
            p1 = 6.0,
            p2 = 4.0,
            p3 = 10.0
        )
        assertEquals(120.0, prism.volume, 0.001)
        assertEquals(160.0, prism.lateralSurfaceArea, 0.001)
        assertEquals(184.0, prism.totalSurfaceArea, 0.001)
    }

    @Test
    fun testMath3DEngineProjectionNetUnfoldAndVolumeSlice() {
        for (solid in Solid3DShapeType.entries) {
            val (facesClosed, linesClosed) = Math3DEngine.buildSolidMesh(
                shape = solid,
                p1 = 4.0,
                p2 = 3.0,
                p3 = 5.0,
                unfoldProgress = 0.0f,
                volumeFillFraction = 0.5f
            )
            val sceneClosed = Math3DEngine.projectScene(
                faces = facesClosed,
                internalLines = linesClosed,
                camera = OrbitCameraState(
                    pitchDeg = CameraPreset3D.ISOMETRIC.pitchDeg,
                    yawDeg = CameraPreset3D.ISOMETRIC.yawDeg,
                    zoom = 1.0f
                ),
                canvasWidth = 600f,
                canvasHeight = 400f
            )
            assertTrue("Faces should be non-empty for $solid", sceneClosed.facesBackToFront.isNotEmpty())
            assertTrue("Volume fill face should be present at 50% fill for $solid", sceneClosed.facesBackToFront.any { it.isVolumeFill })

            // Verify Painter's Algorithm sorting (averageDepthZ ascending from back to front)
            for (i in 0 until sceneClosed.facesBackToFront.size - 1) {
                assertTrue(
                    sceneClosed.facesBackToFront[i].averageDepthZ <= sceneClosed.facesBackToFront[i + 1].averageDepthZ + 1e-4f
                )
            }

            // Unfolded 2D net state (unfoldProgress = 1.0f)
            val (facesUnfolded, linesUnfolded) = Math3DEngine.buildSolidMesh(
                shape = solid,
                p1 = 4.0,
                p2 = 3.0,
                p3 = 5.0,
                unfoldProgress = 1.0f,
                volumeFillFraction = 0.0f
            )
            val sceneUnfolded = Math3DEngine.projectScene(
                faces = facesUnfolded,
                internalLines = linesUnfolded,
                camera = OrbitCameraState(
                    pitchDeg = CameraPreset3D.TOP.pitchDeg,
                    yawDeg = CameraPreset3D.TOP.yawDeg,
                    zoom = 1.0f
                ),
                canvasWidth = 600f,
                canvasHeight = 400f
            )
            assertTrue("Unfolded net faces should be non-empty for $solid", sceneUnfolded.facesBackToFront.isNotEmpty())
        }
    }

    // -------------------------------------------------------------
    // 6. PROGRESS & STATISTICS ANALYTICS ENGINE TESTS (BUG-41..45)
    // -------------------------------------------------------------

    @Test
    fun testBug41AndBug43AndBug44_AnalyticsDeduplicationWindowingAndTopicNormalization() {
        val now = System.currentTimeMillis()
        val legacyQuizScores = listOf(
            QuizScoreEntity(topicId = "BOOK_CLASS_6_CH1", topicTitleBn = "অধ্যায় ১", score = 8, totalQuestions = 10, percentage = 80, timestamp = now - 10_000L),
            QuizScoreEntity(topicId = "VISUAL_GEOMETRY", topicTitleBn = "ভিজ্যুয়াল জ্যামিতি", score = 1, totalQuestions = 1, percentage = 100, timestamp = now - 5_000L),
            QuizScoreEntity(topicId = "PRIMES", topicTitleBn = "মৌলিক সংখ্যা", score = 9, totalQuestions = 10, percentage = 90, timestamp = now - 2_000L)
        )
        val practiceHistory = listOf(
            PracticeHistoryEntity(
                id = 1,
                module = "PRACTICE_LAB",
                topicId = "PRIMES",
                topicTitleBn = "প্র্যাকটিস: মৌলিক সংখ্যা (সহজ)",
                difficulty = "EASY",
                mode = "PRACTICE",
                score = 8,
                correctCount = 8,
                wrongCount = 2,
                totalQuestions = 10,
                accuracyPercent = 80,
                bestStreak = 5,
                durationSeconds = 50,
                timestamp = now - 3_000L
            ),
            PracticeHistoryEntity(
                id = 2,
                module = "PRACTICE_LAB",
                topicId = "PRIMES",
                topicTitleBn = "প্র্যাকটিস: মৌলিক সংখ্যা (কঠিন)",
                difficulty = "HARD",
                mode = "PRACTICE",
                score = 9,
                correctCount = 9,
                wrongCount = 1,
                totalQuestions = 10,
                accuracyPercent = 90,
                bestStreak = 7,
                durationSeconds = 70,
                timestamp = now - 1_000L
            )
        )

        val dashboardToday = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.TODAY,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = legacyQuizScores,
            practiceHistory = practiceHistory,
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = emptyList(),
            nowMs = now
        )

        // BUG-41: BOOK_ and VISUAL_ quiz scores are excluded from standalone legacy quizzes count
        assertEquals(1, dashboardToday.kpiSummary.totalExamsCompleted) // Only PRIMES counted as standalone legacy quiz
        assertEquals(2, dashboardToday.kpiSummary.totalPracticeSessions)

        // BUG-44: "প্র্যাকটিস: মৌলিক সংখ্যা (সহজ)" normalizes to "মৌলিক সংখ্যা"
        val normalized = LearningAnalyticsEngine.normalizeTopicTitleBn("প্র্যাকটিস: মৌলিক সংখ্যা (সহজ)")
        assertEquals("মৌলিক সংখ্যা", normalized)
        val primeMastery = dashboardToday.topicMasteryList.find { it.topicTitleBn == "মৌলিক সংখ্যা" }
        assertNotNull("Normalized topic mastery for মৌলিক সংখ্যা should exist", primeMastery)
    }

    @Test
    fun testBug45_MultiSessionSameDayBackfillAccumulation() {
        val now = System.currentTimeMillis()
        val practiceHistory = listOf(
            PracticeHistoryEntity(
                id = 1,
                module = "PRACTICE_LAB",
                topicId = "ADD_SUB",
                topicTitleBn = "যোগ ও বিয়োগ",
                difficulty = "EASY",
                mode = "PRACTICE",
                score = 8,
                correctCount = 8,
                wrongCount = 2,
                totalQuestions = 10,
                accuracyPercent = 80,
                bestStreak = 5,
                durationSeconds = 300,
                timestamp = now - 2_000L
            ),
            PracticeHistoryEntity(
                id = 2,
                module = "MENTAL_MATH",
                topicId = "SPEED",
                topicTitleBn = "দ্রুত হিসাব",
                difficulty = "MEDIUM",
                mode = "SPEED",
                score = 10,
                correctCount = 10,
                wrongCount = 0,
                totalQuestions = 10,
                accuracyPercent = 100,
                bestStreak = 10,
                durationSeconds = 420,
                timestamp = now - 1_000L
            )
        )

        val dashboard = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.TODAY,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = practiceHistory,
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = emptyList(),
            nowMs = now
        )

        // 300s + 420s = 720s (12 minutes) accumulated on the same day
        assertEquals(12, dashboard.goalProgress.todayStudyMinutes)
        assertEquals(20, dashboard.goalProgress.todayQuestionsAttempted)
    }

    // -------------------------------------------------------------
    // 7. INTERACTIVE QUIZ FIXES TESTS (BUG-47, BUG-50, BUG-51)
    // -------------------------------------------------------------

    @Test
    fun testBug47_EffectiveAnswersMapIncludesPendingUncheckedSelections() {
        val questions = QuizGenerator.generateQuiz(
            topic = QuizTopic.MULTIPLICATION,
            questionCount = 5
        )
        val session = QuizSessionState(
            topic = QuizTopic.MULTIPLICATION,
            mode = QuizMode.TOPIC,
            questions = questions,
            currentIndex = 1,
            selectedOptionIndex = 2,
            userAnswers = mapOf(0 to 1),
            pendingOptionSelections = mapOf(3 to 0)
        )

        val effective = session.effectiveAnswersMap
        assertEquals(3, effective.size)
        assertEquals(1, effective[0])
        assertEquals(2, effective[1])
        assertEquals(0, effective[3])
    }

    @Test
    fun testBug50AndBug51_TrueFalseStatementFormattingAndUniqueQuestionIds() {
        val statement = QuizGenerator.formatTrueFalseStatementBn("৭ × ৮ = কত?", "৫৬")
        assertFalse("Should not contain '= কত?' alongside '= ৫৬'", statement.contains("কত?"))
        assertTrue(statement.contains("৫৬"))

        val qList = QuizGenerator.generateQuiz(
            topic = QuizTopic.PRIMES,
            questionCount = 10
        )
        val ids = qList.map { it.id }
        assertEquals(qList.size, ids.distinct().size)
    }

    // -------------------------------------------------------------
    // 8. ADVERSARIAL QA & REGRESSION TESTS (BUG-40 THROUGH BUG-58)
    // -------------------------------------------------------------

    @Test
    fun testBug42AndBug43_ExamTrendChronologicalOrderAndWindowedTopicMasteryIsolation() {
        val now = 1_762_000_000_000L // Fixed reference epoch
        val dayMs = 86_400_000L

        // Cumulative all-time table has 100 historical questions from 60 days ago
        val cumulativeTopicAnalytics = listOf(
            TopicAnalyticsEntity(
                analyticsKey = "PRACTICE_LAB_PRIMES_EASY",
                module = "PRACTICE_LAB",
                topicId = "PRIMES",
                topicTitleBn = "মৌলিক সংখ্যা",
                difficulty = "EASY",
                totalAttempted = 100,
                totalCorrect = 95,
                totalWrong = 5,
                bestStreak = 25,
                totalTimeSeconds = 600,
                lastPracticedAt = now - 60 * dayMs
            )
        )

        // Windowed history: 6 questions today on PRIMES, and 8 questions 10 days ago on ALGEBRA
        val history = listOf(
            PracticeHistoryEntity(
                id = 10,
                module = "PRACTICE_LAB",
                topicId = "PRIMES",
                topicTitleBn = "প্র্যাকটিস: মৌলিক সংখ্যা (সহজ)",
                difficulty = "EASY",
                mode = "PRACTICE",
                score = 3,
                correctCount = 3,
                wrongCount = 3,
                totalQuestions = 6,
                accuracyPercent = 50,
                bestStreak = 2,
                durationSeconds = 60,
                timestamp = now - 3_600_000L
            ),
            PracticeHistoryEntity(
                id = 11,
                module = "PRACTICE_LAB",
                topicId = "ALGEBRA",
                topicTitleBn = "বীজগণিত সূত্র কুইজ",
                difficulty = "MEDIUM",
                mode = "PRACTICE",
                score = 7,
                correctCount = 7,
                wrongCount = 1,
                totalQuestions = 8,
                accuracyPercent = 88,
                bestStreak = 6,
                durationSeconds = 90,
                timestamp = now - 10 * dayMs
            )
        )

        val exams = listOf(
            ExamAttemptEntity(
                attemptId = "ex_old",
                examTitleBn = "কুইজ ১",
                examType = "TOPIC",
                selectedTopicsCsv = "MULTIPLICATION",
                difficulty = "EASY",
                questionType = "MCQ",
                totalQuestions = 10,
                durationLimitSeconds = 300,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.0,
                status = "COMPLETED",
                currentQuestionIndex = 9,
                elapsedSeconds = 120,
                remainingSeconds = 180,
                questionsJson = "[]",
                answersJson = "{}",
                markedForReviewCsv = "",
                visitedIndicesCsv = "0,1,2,3,4,5,6,7,8,9",
                questionTimeSecondsJson = "{}",
                correctCount = 6,
                wrongCount = 4,
                unansweredCount = 0,
                earnedMarks = 6.0,
                negativeMarksDeducted = 0.0,
                finalScore = 6.0,
                percentage = 60.0,
                accuracyPercent = 60.0,
                startedAt = now - 5_000_000L,
                updatedAt = now - 4_000_000L,
                completedAt = now - 4_000_000L
            ),
            ExamAttemptEntity(
                attemptId = "ex_new",
                examTitleBn = "কুইজ ৩",
                examType = "TOPIC",
                selectedTopicsCsv = "MULTIPLICATION",
                difficulty = "EASY",
                questionType = "MCQ",
                totalQuestions = 10,
                durationLimitSeconds = 300,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.0,
                status = "COMPLETED",
                currentQuestionIndex = 9,
                elapsedSeconds = 110,
                remainingSeconds = 190,
                questionsJson = "[]",
                answersJson = "{}",
                markedForReviewCsv = "",
                visitedIndicesCsv = "0,1,2,3,4,5,6,7,8,9",
                questionTimeSecondsJson = "{}",
                correctCount = 9,
                wrongCount = 1,
                unansweredCount = 0,
                earnedMarks = 9.0,
                negativeMarksDeducted = 0.0,
                finalScore = 9.0,
                percentage = 90.0,
                accuracyPercent = 90.0,
                startedAt = now - 1_500_000L,
                updatedAt = now - 1_000_000L,
                completedAt = now - 1_000_000L
            )
        )
        // Standalone legacy quiz occurred BETWEEN ex_old and ex_new
        val legacyQuizzes = listOf(
            QuizScoreEntity(
                id = 55,
                topic = "PRIMES",
                topicTitleBn = "মৌলিক সংখ্যা কুইজ (১-২০০)",
                score = 7,
                totalQuestions = 10,
                percentage = 75,
                timestamp = now - 2_500_000L
            )
        )

        val todayAnalytics = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.TODAY,
            completedExams = exams,
            activeExam = null,
            quizScores = legacyQuizzes,
            practiceHistory = history,
            topicAnalytics = cumulativeTopicAnalytics,
            progressItems = emptyList(),
            dailyActivities = emptyList(),
            nowMs = now
        )

        // BUG-42: Exam trend points must merge exams and standalone legacy quizzes in true chronological order (60 -> 75 -> 90)
        assertEquals(listOf(60, 75, 90), todayAnalytics.examTrendPoints.map { it.second })

        // BUG-43: TODAY filter must NOT leak the 100 all-time questions from 60 days ago or the ALGEBRA session from 10 days ago
        assertTrue("ALGEBRA from 10 days ago must not appear in TODAY topic mastery", todayAnalytics.topicMasteryList.none { it.topicId == "ALGEBRA" })
        val todayPrime = todayAnalytics.topicMasteryList.find { it.topicTitleBn == "মৌলিক সংখ্যা" }
        assertNotNull(todayPrime)
        // 6 questions from today's practice + 10 questions from today's legacy quiz = 16 questions (NOT 100+16=116)
        assertEquals(16, todayPrime!!.totalAttempted)

        // All 6 time-window filters execute cleanly and monotonically widen history
        val allFilters = AnalyticsTimeFilter.entries.map { filter ->
            filter to LearningAnalyticsEngine.buildDashboardAnalytics(
                timeFilter = filter,
                completedExams = exams,
                activeExam = null,
                quizScores = legacyQuizzes,
                practiceHistory = history,
                topicAnalytics = cumulativeTopicAnalytics,
                progressItems = emptyList(),
                dailyActivities = emptyList(),
                nowMs = now
            )
        }.toMap()
        assertEquals(1, allFilters[AnalyticsTimeFilter.TODAY]!!.kpiSummary.totalPracticeSessions)
        assertEquals(2, allFilters[AnalyticsTimeFilter.LAST_30_DAYS]!!.kpiSummary.totalPracticeSessions)
        val allTimePrime = allFilters[AnalyticsTimeFilter.ALL_TIME]!!.topicMasteryList.find { it.topicTitleBn == "মৌলিক সংখ্যা" }
        assertNotNull(allTimePrime)
        assertTrue("ALL_TIME must include cumulative topicAnalytics", allTimePrime!!.totalAttempted >= 100)
    }

    @Test
    fun testBug45_BestStreakBeyond365DaysAndHistoricalStreakWithoutTruncation() {
        val baseDate = LocalDate.of(2026, 11, 10)
        val nowMs = baseDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() + 43_200_000L

        // Build a 410-day consecutive streak ending today (crosses leap year / DST / year boundaries)
        val longStreakActivities = (0 until 410).map { offset ->
            val dateStr = baseDate.minusDays(offset.toLong()).toString()
            DailyStudyActivityEntity(
                dateKey = dateStr,
                questionsAttempted = 15,
                correctCount = 12,
                wrongCount = 3,
                studyTimeSeconds = 600,
                examsCompleted = 0,
                practiceSessionsCompleted = 1
            )
        }

        val dashboard410 = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.ALL_TIME,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = emptyList(),
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = longStreakActivities,
            nowMs = nowMs
        )
        assertEquals("Current streak must not truncate at 365 days", 410, dashboard410.goalProgress.currentStreakDays)
        assertEquals("Best streak must not truncate at 365 days", 410, dashboard410.goalProgress.bestStreakDays)

        // Historical streak of 25 days occurring 500 days ago (outside a 365-day window), with 0 current streak
        val historicalActivities = (500 until 525).map { offset ->
            val dateStr = baseDate.minusDays(offset.toLong()).toString()
            DailyStudyActivityEntity(
                dateKey = dateStr,
                questionsAttempted = 10,
                correctCount = 9,
                wrongCount = 1,
                studyTimeSeconds = 300,
                examsCompleted = 0,
                practiceSessionsCompleted = 1
            )
        }
        val dashboardHistorical = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.ALL_TIME,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = emptyList(),
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = historicalActivities,
            nowMs = nowMs
        )
        assertEquals(0, dashboardHistorical.goalProgress.currentStreakDays)
        assertEquals("Historical streak > 365 days ago must be preserved", 25, dashboardHistorical.goalProgress.bestStreakDays)

        // Empty history -> 0 current streak, 0 best streak
        val emptyDashboard = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.ALL_TIME,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = emptyList(),
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = emptyList(),
            nowMs = nowMs
        )
        assertEquals(0, emptyDashboard.goalProgress.currentStreakDays)
        assertEquals(0, emptyDashboard.goalProgress.bestStreakDays)
    }

    @Test
    fun testBug47_EffectiveAnswersMapActiveSelectionOverridesStalePendingAndEvaluatesTypedAnswers() {
        val questions = QuizGenerator.generateQuiz(
            topic = QuizTopic.MULTIPLICATION,
            questionCount = 4,
            questionType = QuizQuestionType.NUMERICAL
        )
        val q2CorrectText = questions[2].correctAnswerText
        val session = QuizSessionState(
            topic = QuizTopic.MULTIPLICATION,
            mode = QuizMode.TOPIC,
            questions = questions,
            currentIndex = 1,
            // Active question 1 has newly selected option 3, whereas pendingOptionSelections had stale option 0
            selectedOptionIndex = 3,
            pendingOptionSelections = mapOf(1 to 0),
            // Question 2 has a pending typed numerical answer matching the correct answer
            pendingTypedAnswers = mapOf(2 to q2CorrectText),
            // Question 3 has an invalid pending typed answer
            typedAnswer = "৯৯৯৯৯৯"
        )

        val effective = session.effectiveAnswersMap
        assertEquals("Active selection on currentIndex must override stale pendingOptionSelections", 3, effective[1])
        assertEquals("Pending typed correct answer must map to correctIndex", questions[2].correctIndex, effective[2])

        val sessionOnQ3 = session.copy(currentIndex = 3, selectedOptionIndex = null)
        assertEquals("Wrong uncommitted typed answer on currentIndex must map to -2", -2, sessionOnQ3.effectiveAnswersMap[3])
    }

    @Test
    fun testBug50AndBug51_AllTopicsTrueFalseDeclarativeAndPolarityPreserved() {
        for (topic in QuizTopic.entries) {
            val questions = QuizGenerator.generateQuiz(
                topic = topic,
                questionCount = 12,
                difficulty = PracticeDifficulty.MEDIUM,
                questionType = QuizQuestionType.TRUE_FALSE
            )
            val ids = questions.map { it.id }
            assertEquals("All question IDs in $topic must be unique", questions.size, ids.distinct().size)
            for (q in questions) {
                assertTrue("Question ID ${q.id} must be non-blank and content-prefixed", q.id.contains("_"))
                assertFalse("Question ID ${q.id} must not be a bare positional index", q.id in setOf("q_0", "mul_0", "quiz_arith_0", "quiz_ns_0", "quiz_geom_0"))
                if (q.questionType == QuizQuestionType.TRUE_FALSE) {
                    val stmt = q.trueFalseStatementBn ?: ""
                    assertTrue("True/False statement must not be blank for $topic", stmt.isNotBlank())
                    assertFalse("True/False statement must not contain '?' in: $stmt", stmt.contains("?"))
                    assertFalse("True/False statement must not contain interrogative 'কত' in: $stmt", stmt.contains(" কত"))
                    assertFalse("True/False statement must not contain interrogative 'কোনটি' in: $stmt", stmt.contains("কোনটি"))
                    val expectedIdx = if (q.isStatementTrue) 0 else 1
                    assertEquals("Polarity must match correctIndex for $stmt", expectedIdx, q.correctIndex)
                }
            }
        }
    }

    @Test
    fun testMath3DEngine_ExactVolumeFillMathematicsForAllSixSolids() {
        // 1. Cube, Cuboid, Cylinder: linear with height (at p = 0.5, volume fraction = 0.5)
        for (linearShape in listOf(Solid3DShapeType.CUBE, Solid3DShapeType.CUBOID, Solid3DShapeType.CYLINDER)) {
            val fill50 = Math3DEngine.calculateVolumeFill(linearShape, 4.0, 6.0, 5.0, 0.5f)
            assertTrue(fill50.isLinearWithHeight)
            assertEquals(0.5, fill50.volumeFillFraction, 1e-6)
            assertEquals(fill50.totalVolume * 0.5, fill50.filledVolume, 1e-5)
        }

        // 2. Cone (r=3, h=4, V = 12*pi):
        // At p = 0.5: base-down frustum volume fraction = 1 - (1 - 0.5)^3 = 0.875 (87.5%)
        // Apex-down inverted cone volume fraction = 0.5^3 = 0.125 (12.5%)
        val coneFill50 = Math3DEngine.calculateVolumeFill(Solid3DShapeType.CONE, 3.0, 4.0, 4.0, 0.5f)
        assertFalse(coneFill50.isLinearWithHeight)
        assertEquals(0.875, coneFill50.volumeFillFraction, 1e-6)
        assertEquals(0.125, coneFill50.invertedApexDownVolumeFraction!!, 1e-6)
        assertEquals(12.0 * PI * 0.875, coneFill50.filledVolume, 1e-4)
        assertEquals(12.0 * PI * 0.125, coneFill50.invertedApexDownVolume!!, 1e-4)

        // 3. Sphere (r=3, V = 36*pi):
        // Spherical cap formula: V_fill / V = p^2 * (3 - 2p)
        // At p = 0.25 -> 0.0625 * 2.5 = 0.15625 (15.625%)
        // At p = 0.50 -> 0.25 * 2.0 = 0.50 (50.0%)
        // At p = 0.75 -> 0.5625 * 1.5 = 0.84375 (84.375%)
        val sphere25 = Math3DEngine.calculateVolumeFill(Solid3DShapeType.SPHERE, 3.0, heightFillFraction = 0.25f)
        val sphere50 = Math3DEngine.calculateVolumeFill(Solid3DShapeType.SPHERE, 3.0, heightFillFraction = 0.50f)
        val sphere75 = Math3DEngine.calculateVolumeFill(Solid3DShapeType.SPHERE, 3.0, heightFillFraction = 0.75f)
        assertEquals(0.15625, sphere25.volumeFillFraction, 1e-6)
        assertEquals(0.50, sphere50.volumeFillFraction, 1e-6)
        assertEquals(0.84375, sphere75.volumeFillFraction, 1e-6)

        // 4. Triangular Prism (b=6, h=4, L=10, V = 120):
        // Resting on base b×L with apex ridge at top: V_fill / V = 2p - p^2
        // At p = 0.5 -> 2(0.5) - 0.25 = 0.75 (75.0%, filledVolume = 90.0)
        val prism50 = Math3DEngine.calculateVolumeFill(Solid3DShapeType.TRIANGULAR_PRISM, 6.0, 4.0, 10.0, 0.5f)
        assertFalse(prism50.isLinearWithHeight)
        assertEquals(0.75, prism50.volumeFillFraction, 1e-6)
        assertEquals(90.0, prism50.filledVolume, 1e-5)
    }

    @Test
    fun testMath3DEngine_RigidHingeNetUnfoldingAreaInvarianceAndDegenerateInputSafety() {
        fun polygonArea3D(verts: List<com.example.math.Vec3>): Double {
            if (verts.size < 3) return 0.0
            val p0 = verts[0]
            var sum = 0.0
            for (i in 1 until verts.size - 1) {
                val e1 = verts[i] - p0
                val e2 = verts[i + 1] - p0
                sum += 0.5 * e1.cross(e2).length().toDouble()
            }
            return sum
        }

        // Verify rigid hinge unfolding preserves total mesh surface area across 0% -> 25% -> 50% -> 75% -> 100% -> 0%
        for (shape in listOf(Solid3DShapeType.CUBE, Solid3DShapeType.CUBOID, Solid3DShapeType.TRIANGULAR_PRISM)) {
            val (closedFaces, _) = Math3DEngine.buildSolidMesh(shape, 4.0, 3.0, 5.0, unfoldProgress = 0f)
            val closedArea = closedFaces.filter { !it.isVolumeFill }.sumOf { polygonArea3D(it.vertices) }

            for (progress in listOf(0.25f, 0.5f, 0.75f, 1.0f, 0.0f)) {
                val (facesAtP, _) = Math3DEngine.buildSolidMesh(shape, 4.0, 3.0, 5.0, unfoldProgress = progress)
                val areaAtP = facesAtP.filter { !it.isVolumeFill }.sumOf { polygonArea3D(it.vertices) }
                assertEquals(
                    "Rigid hinge rotation for $shape at unfold=$progress must preserve exact surface area (no lerp shrinkage)",
                    closedArea,
                    areaAtP,
                    1e-3
                )
            }
        }

        // Verify Cone base unfolds into the z = r net plane at unfold = 1.0f
        val (coneUnfolded, _) = Math3DEngine.buildSolidMesh(Solid3DShapeType.CONE, 3.0, 4.0, unfoldProgress = 1.0f)
        val coneBase = coneUnfolded.first { it.id == "cone_base" }
        val zSpread = coneBase.vertices.maxOf { it.z } - coneBase.vertices.minOf { it.z }
        assertTrue("Cone base must unfold flat into the net plane at unfold=1.0", zSpread < 1e-4f)

        // Verify degenerate / extreme inputs (NaN, Infinity, 0, negative) never produce NaN coordinates
        for (shape in Solid3DShapeType.entries) {
            val metrics = Math3DEngine.calculateSolidMetrics(shape, Double.NaN, -5.0, 0.0)
            assertFalse(metrics.volume.isNaN() || metrics.volume.isInfinite())
            assertTrue(metrics.volume > 0.0)
            val (faces, lines) = Math3DEngine.buildSolidMesh(shape, Double.NaN, -10.0, Double.POSITIVE_INFINITY, Float.NaN, Float.NEGATIVE_INFINITY)
            val scene = Math3DEngine.projectScene(
                faces = faces,
                internalLines = lines,
                camera = OrbitCameraState(pitchDeg = Float.NaN, yawDeg = Float.POSITIVE_INFINITY, zoom = -3f),
                canvasWidth = 0f,
                canvasHeight = Float.NaN
            )
            assertTrue(scene.facesBackToFront.all { f -> f.points.all { !it.x.isNaN() && !it.y.isNaN() } })
        }
    }

    // -------------------------------------------------------------
    // 9. VISUAL MATH EXPLORER 2.0 STUDIO ENGINE & 10-SOLID TESTS
    // -------------------------------------------------------------

    @Test
    fun testVisualMath20_NewFourSolidsMensurationAndNonLinearVolumeFill() {
        // 7. Hemisphere (r = 3) -> V = (2/3)*pi*27 = 18*pi, CSA = 2*pi*9 = 18*pi, TSA = 3*pi*9 = 27*pi
        val hemi = Math3DEngine.calculateSolidMetrics(Solid3DShapeType.HEMISPHERE, p1 = 3.0)
        assertEquals(18.0 * PI, hemi.volume, 0.01)
        assertEquals(18.0 * PI, hemi.lateralSurfaceArea, 0.01)
        assertEquals(27.0 * PI, hemi.totalSurfaceArea, 0.01)

        // Hemisphere non-linear volume fill at p = 0.5 -> V_fill / V = p*(3 - p^2)/2 = 0.5 * 2.75 / 2 = 0.6875 (68.75%)
        val hemiFill50 = Math3DEngine.calculateVolumeFill(Solid3DShapeType.HEMISPHERE, 3.0, heightFillFraction = 0.5f)
        assertFalse(hemiFill50.isLinearWithHeight)
        assertEquals(0.6875, hemiFill50.volumeFillFraction, 1e-5)

        // 8. Square Pyramid (a = 6, h = 4) -> slant l = sqrt(3^2 + 4^2) = 5, Base = 36, LSA = 2*6*5 = 60, TSA = 96, V = 48
        val pyramid = Math3DEngine.calculateSolidMetrics(Solid3DShapeType.SQUARE_PYRAMID, p1 = 6.0, p2 = 4.0)
        assertEquals(48.0, pyramid.volume, 0.001)
        assertEquals(60.0, pyramid.lateralSurfaceArea, 0.001)
        assertEquals(96.0, pyramid.totalSurfaceArea, 0.001)
        assertEquals(5.0, pyramid.secondaryMetricValue, 0.001)

        val pyrFill50 = Math3DEngine.calculateVolumeFill(Solid3DShapeType.SQUARE_PYRAMID, 6.0, 4.0, heightFillFraction = 0.5f)
        assertFalse(pyrFill50.isLinearWithHeight)
        assertEquals(0.875, pyrFill50.volumeFillFraction, 1e-6)

        // 9. Hexagonal Prism (a = 4, h = 5) -> Base = (3*sqrt(3)/2)*16 = 24*sqrt(3), LSA = 6*4*5 = 120, V = 120*sqrt(3)
        val hexPrism = Math3DEngine.calculateSolidMetrics(Solid3DShapeType.HEXAGONAL_PRISM, p1 = 4.0, p2 = 5.0)
        val expectedHexBase = 24.0 * sqrt(3.0)
        assertEquals(expectedHexBase * 5.0, hexPrism.volume, 0.01)
        assertEquals(120.0, hexPrism.lateralSurfaceArea, 0.01)
        assertEquals(2.0 * expectedHexBase + 120.0, hexPrism.totalSurfaceArea, 0.01)

        // 10. Conical Frustum (R = 6, r = 3, h = 4) -> l = sqrt((6-3)^2 + 4^2) = 5
        // LSA = pi*(6+3)*5 = 45*pi, TSA = 45*pi + 36*pi + 9*pi = 90*pi, V = (pi*4/3)*(36 + 18 + 9) = 84*pi
        val frustum = Math3DEngine.calculateSolidMetrics(Solid3DShapeType.FRUSTUM, p1 = 6.0, p2 = 3.0, p3 = 4.0)
        assertEquals(5.0, frustum.secondaryMetricValue, 0.001)
        assertEquals(45.0 * PI, frustum.lateralSurfaceArea, 0.01)
        assertEquals(90.0 * PI, frustum.totalSurfaceArea, 0.01)
        assertEquals(84.0 * PI, frustum.volume, 0.01)
    }

    @Test
    fun testVisualMath20_CrossSectionSlicingAndNineStageGuidesForAllTenSolids() {
        for (solid in Solid3DShapeType.entries) {
            // 1. Verify 9-stage guide completeness
            val guide = com.example.math.VisualMathStudioEngine.buildNineStageGuideForSolid(
                shape = solid,
                p1 = solid.defaultP1,
                p2 = solid.defaultP2,
                p3 = solid.defaultP3
            )
            assertEquals("Each solid must have exactly 9 pedagogical stages", 9, guide.stages.size)
            assertTrue(guide.practiceChallenge.expectedValue > 0.0)
            assertEquals(4, guide.knowledgeCheckQuestion.optionsBn.size)

            // 2. Verify Horizontal, Vertical, and Angled cross-sections
            for (mode in listOf(
                com.example.math.CrossSectionSliceMode.HORIZONTAL,
                com.example.math.CrossSectionSliceMode.VERTICAL,
                com.example.math.CrossSectionSliceMode.ANGLED
            )) {
                val slice = Math3DEngine.calculateCrossSection(
                    shape = solid,
                    p1 = solid.defaultP1,
                    p2 = solid.defaultP2,
                    p3 = solid.defaultP3,
                    sliceMode = mode,
                    slicePosition = 0.5f
                )
                assertTrue("Cross-section area must be > 0 for $solid ($mode)", slice.sectionArea > 0.0)
                assertTrue("Cross-section perimeter must be > 0 for $solid ($mode)", slice.sectionPerimeter > 0.0)
                assertTrue("2D contour must have >= 3 vertices for $solid ($mode)", slice.contour2D.size >= 3)

                val (facesWithSlice, _) = Math3DEngine.buildSolidMesh(
                    shape = solid,
                    p1 = solid.defaultP1,
                    p2 = solid.defaultP2,
                    p3 = solid.defaultP3,
                    unfoldProgress = 0f,
                    volumeFillFraction = 0f,
                    sliceMode = mode,
                    slicePosition = 0.5f
                )
                assertTrue("Mesh must contain isCrossSection face for $solid ($mode)", facesWithSlice.any { it.isCrossSection })
            }
        }
    }

    @Test
    fun testVisualMath20_TrigonometryUnitCircle3DCoordinatesSurfaceAndAlgebraTiles() {
        // 1. Unit Circle & Elevation
        val deg30 = com.example.math.VisualMathStudioEngine.analyzeUnitCircle(30.0)
        assertEquals(0.5, deg30.sinValue, 1e-4)
        assertEquals(sqrt(3.0) / 2.0, deg30.cosValue, 1e-4)
        assertEquals(1.0, deg30.pythagoreanIdentitySum, 1e-6)
        assertEquals(1, deg30.quadrantNumber)

        val deg90 = com.example.math.VisualMathStudioEngine.analyzeUnitCircle(90.0)
        assertEquals(1.0, deg90.sinValue, 1e-5)
        assertEquals(0.0, deg90.cosValue, 1e-5)
        assertEquals(null, deg90.tanValue)

        val elev = com.example.math.VisualMathStudioEngine.analyzeTrigElevation(45.0, 25.0)
        assertEquals(25.0, elev.oppositeHeight, 1e-3)

        // 2. 3D Coordinates & Vector Studio
        val coord = com.example.math.VisualMathStudioEngine.analyze3DCoordinates(
            com.example.math.Point3DCoord(0.0, 0.0, 0.0),
            com.example.math.Point3DCoord(3.0, 4.0, 12.0)
        )
        // Clamped to [-10, 10] -> B(3, 4, 10) -> d = sqrt(9 + 16 + 100) = sqrt(125)
        assertEquals(sqrt(125.0), coord.distance3D, 1e-4)
        val (coordFaces, coordLines) = com.example.math.VisualMathStudioEngine.build3DCoordinateSceneMesh(coord)
        assertTrue(coordFaces.isNotEmpty())
        assertTrue(coordLines.size >= 4)

        // 3. Quadratic 2D & 3D Surface z = f(x,y)
        val quad = com.example.math.VisualMathStudioEngine.analyzeQuadratic2D(1.0, -5.0, 6.0)
        assertEquals(2.5, quad.vertexX, 1e-5)
        assertEquals(-0.25, quad.vertexY, 1e-5)
        assertEquals(listOf(2.0, 3.0), quad.realRoots)

        for (preset in com.example.math.Surface3DPreset.entries) {
            val surf = com.example.math.VisualMathStudioEngine.build3DSurfaceMesh(preset, 1.2, 1.5, 8)
            assertEquals(64, surf.faces.size)
            assertTrue(surf.lines.isNotEmpty())
        }

        // 4. Algebra Tiles & Fraction Operations
        val tiles = com.example.math.VisualMathStudioEngine.analyzeAlgebraTiles(
            xSquaredCountInput = 1,
            xPositiveInput = 6,
            xNegativeInput = 1,
            unitPositiveInput = 6,
            unitNegativeInput = 0,
            xEvalValueInput = 2
        )
        assertEquals(1, tiles.zeroPairsX)
        assertEquals(5, tiles.netXCount)
        assertNotNull("x^2 + 5x + 6 should factorize", tiles.factorizedFormBn)
        // At x = 2: 2^2 + 5(2) + 6 = 20
        assertEquals(20.0, tiles.evaluatedValueAtX, 1e-5)

        val fracAdd = com.example.math.VisualMathStudioEngine.analyzeFractionOperation(
            1, 2, 1, 3, com.example.math.FractionOpType.ADD
        )
        assertEquals(5, fracAdd.simplifiedNum)
        assertEquals(6, fracAdd.simplifiedDen)

        val fracDiv = com.example.math.VisualMathStudioEngine.analyzeFractionOperation(
            3, 4, 9, 8, com.example.math.FractionOpType.DIVIDE
        )
        assertEquals(2, fracDiv.simplifiedNum)
        assertEquals(3, fracDiv.simplifiedDen)
    }

    @Test
    fun testAdversarialAudit_GlMvpOverlayProjectionSyncAndShaderIntegrity() {
        val (faces, lines) = Math3DEngine.buildSolidMesh(
            shape = Solid3DShapeType.HEXAGONAL_PRISM,
            p1 = 4.0,
            p2 = 6.0,
            unfoldProgress = 0.0f,
            volumeFillFraction = 0.4f
        )
        val cameras = listOf(
            OrbitCameraState(pitchDeg = 25f, yawDeg = -35f, zoom = 1.0f, panX = 0f, panY = 0f, isOrthographic = false),
            OrbitCameraState(pitchDeg = -40f, yawDeg = 120f, zoom = 1.45f, panX = 24f, panY = -16f, isOrthographic = false),
            OrbitCameraState(pitchDeg = 18f, yawDeg = 45f, zoom = 0.85f, panX = -12f, panY = 20f, isOrthographic = true)
        )
        val width = 800
        val height = 600

        for (cam in cameras) {
            val canvasScene = Math3DEngine.projectScene(
                faces = faces,
                internalLines = lines,
                camera = cam,
                canvasWidth = width.toFloat(),
                canvasHeight = height.toFloat()
            )
            val testFace = faces.first()
            val projectedFace = canvasScene.facesBackToFront.first { it.id == testFace.id }
            for (vIdx in testFace.vertices.indices) {
                val worldPt = testFace.vertices[vIdx]
                val canvasPt = projectedFace.points[vIdx]
                val glPt = com.example.ui.components.Math3DGlRenderer.projectPointThroughGlPipeline(
                    point = worldPt,
                    camera = cam,
                    width = width,
                    height = height
                )
                assertEquals("GL X must match Canvas X within 0.05px for cam=$cam", canvasPt.x, glPt.x, 0.05f)
                assertEquals("GL Y must match Canvas Y within 0.05px for cam=$cam", canvasPt.y, glPt.y, 0.05f)
            }
        }

        assertTrue(com.example.ui.components.Math3DGlRenderer.SURFACE_VERTEX_SHADER.contains("uMVPMatrix"))
        assertTrue(com.example.ui.components.Math3DGlRenderer.SURFACE_FRAGMENT_SHADER.contains("gl_FragColor"))
        assertTrue(com.example.ui.components.Math3DGlRenderer.LINE_VERTEX_SHADER.contains("uMVPMatrix"))
    }

    @Test
    fun testAdversarialAudit_NetUnfoldRigidIsometryCyclesAndFrustumCoplanarity() {
        val polyhedralSolids = listOf(
            Solid3DShapeType.CUBE,
            Solid3DShapeType.CUBOID,
            Solid3DShapeType.TRIANGULAR_PRISM,
            Solid3DShapeType.SQUARE_PYRAMID,
            Solid3DShapeType.HEXAGONAL_PRISM
        )
        val fractions = listOf(0.0f, 0.25f, 0.50f, 0.75f, 1.00f)

        fun polygonArea3D(verts: List<com.example.math.Vec3>): Double {
            if (verts.size < 3) return 0.0
            val v0 = verts[0]
            var sum = 0.0
            for (i in 1 until verts.size - 1) {
                val u = verts[i] - v0
                val v = verts[i + 1] - v0
                val c = u.cross(v)
                sum += 0.5 * sqrt((c.x * c.x + c.y * c.y + c.z * c.z).toDouble())
            }
            return sum
        }

        for (solid in polyhedralSolids) {
            val (closedFaces, _) = Math3DEngine.buildSolidMesh(solid, 4.0, 3.0, 5.0, unfoldProgress = 0.0f)
            val closedTotalArea = closedFaces.sumOf { polygonArea3D(it.vertices) }

            for (f in fractions) {
                val (stepFaces, _) = Math3DEngine.buildSolidMesh(solid, 4.0, 3.0, 5.0, unfoldProgress = f)
                val stepTotalArea = stepFaces.sumOf { polygonArea3D(it.vertices) }
                assertEquals(
                    "Rigid hinge net for $solid must preserve exact 3D surface area at unfold=$f",
                    closedTotalArea,
                    stepTotalArea,
                    1e-4
                )
            }

            // Reverse fold 1.0 -> 0.0 must restore identical vertex coordinates
            val (refoldedFaces, _) = Math3DEngine.buildSolidMesh(solid, 4.0, 3.0, 5.0, unfoldProgress = 0.0f)
            for (i in closedFaces.indices) {
                for (vIdx in closedFaces[i].vertices.indices) {
                    val vA = closedFaces[i].vertices[vIdx]
                    val vB = refoldedFaces[i].vertices[vIdx]
                    assertEquals(vA.x, vB.x, 1e-6f)
                    assertEquals(vA.y, vB.y, 1e-6f)
                    assertEquals(vA.z, vB.z, 1e-6f)
                }
            }
        }

        // Frustum at unfold = 1.0f must place frustum_top, frustum_bot, and lateral quads coplanar in Z
        val (frustumUnfolded, _) = Math3DEngine.buildSolidMesh(
            shape = Solid3DShapeType.FRUSTUM,
            p1 = 6.0,
            p2 = 3.0,
            p3 = 4.0,
            unfoldProgress = 1.0f
        )
        val refZ = frustumUnfolded.first { it.id == "frustum_bot" }.vertices.first().z
        for (face in frustumUnfolded) {
            for (v in face.vertices) {
                assertEquals("Frustum face ${face.id} must lie in net plane z=$refZ at 100% unfold", refZ, v.z, 1e-4f)
            }
        }
    }

    @Test
    fun testAdversarialAudit_ExactCrossSectionAnalyticalFormulasAndBoundaryTangents() {
        // 1. Hemisphere (r = 4): Horizontal at s = 0.0 (base Great Circle), s = 0.5 (r_c = r*sqrt(0.75)), s = 1.0 (pole tangent = 0)
        val hemiBase = Math3DEngine.calculateCrossSection(Solid3DShapeType.HEMISPHERE, 4.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 0.0f)
        assertEquals(16.0 * PI, hemiBase.sectionArea, 1e-4)

        val hemiMid = Math3DEngine.calculateCrossSection(Solid3DShapeType.HEMISPHERE, 4.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 0.5f)
        assertEquals(12.0 * PI, hemiMid.sectionArea, 1e-4) // pi * 4^2 * (1 - 0.25) = 12*pi

        val hemiPole = Math3DEngine.calculateCrossSection(Solid3DShapeType.HEMISPHERE, 4.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 1.0f)
        assertEquals(0.0, hemiPole.sectionArea, 1e-6)

        // Hemisphere Vertical at s = 0.5 -> Semicircle of radius 4: Area = 0.5 * pi * 16 = 8*pi, Perimeter = (pi + 2)*4
        val hemiVert = Math3DEngine.calculateCrossSection(Solid3DShapeType.HEMISPHERE, 4.0, sliceMode = com.example.math.CrossSectionSliceMode.VERTICAL, slicePosition = 0.5f)
        assertEquals(8.0 * PI, hemiVert.sectionArea, 1e-4)
        assertEquals((PI + 2.0) * 4.0, hemiVert.sectionPerimeter, 1e-4)

        // 2. Sphere (r = 5): s = 0.0 (tangent = 0), s = 0.25 (d = 2.5 -> r_c^2 = 18.75), s = 0.5 (Great Circle = 25*pi)
        val sphTangent = Math3DEngine.calculateCrossSection(Solid3DShapeType.SPHERE, 5.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 0.0f)
        assertEquals(0.0, sphTangent.sectionArea, 1e-6)

        val sphQuarter = Math3DEngine.calculateCrossSection(Solid3DShapeType.SPHERE, 5.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 0.25f)
        assertEquals(18.75 * PI, sphQuarter.sectionArea, 1e-4)

        val sphCenter = Math3DEngine.calculateCrossSection(Solid3DShapeType.SPHERE, 5.0, sliceMode = com.example.math.CrossSectionSliceMode.VERTICAL, slicePosition = 0.5f)
        assertEquals(25.0 * PI, sphCenter.sectionArea, 1e-4)

        // 3. Cone (r = 6, h = 8): Horizontal at s = 0.5 -> r_c = 3 -> Area = 9*pi; at s = 1.0 -> apex tangent = 0
        val coneMid = Math3DEngine.calculateCrossSection(Solid3DShapeType.CONE, 6.0, 8.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 0.5f)
        assertEquals(9.0 * PI, coneMid.sectionArea, 1e-4)
        val coneApex = Math3DEngine.calculateCrossSection(Solid3DShapeType.CONE, 6.0, 8.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 1.0f)
        assertEquals(0.0, coneApex.sectionArea, 1e-6)

        // 4. Hexagonal Prism (a = 4, h = 6): Angled 30 deg -> Area = A_hex * sec(30) = 3 * a^2 = 48.0
        val hexAngled = Math3DEngine.calculateCrossSection(Solid3DShapeType.HEXAGONAL_PRISM, 4.0, 6.0, sliceMode = com.example.math.CrossSectionSliceMode.ANGLED, slicePosition = 0.5f)
        assertEquals(48.0, hexAngled.sectionArea, 1e-4)

        // 5. Frustum (R = 6, r = 2, h = 5): Horizontal vs Angled at s = 0.5 (r_slice = 4)
        val frustHoriz = Math3DEngine.calculateCrossSection(Solid3DShapeType.FRUSTUM, 6.0, 2.0, 5.0, sliceMode = com.example.math.CrossSectionSliceMode.HORIZONTAL, slicePosition = 0.5f)
        val frustAngled = Math3DEngine.calculateCrossSection(Solid3DShapeType.FRUSTUM, 6.0, 2.0, 5.0, sliceMode = com.example.math.CrossSectionSliceMode.ANGLED, slicePosition = 0.5f)
        assertEquals(16.0 * PI, frustHoriz.sectionArea, 1e-4)
        assertEquals((16.0 * PI) / kotlin.math.cos(PI / 6.0), frustAngled.sectionArea, 1e-4)
    }

    @Test
    fun testAdversarialAudit_VolumeFillInvertedFrustumNegativeFractionsAndAlgebraDistribution() {
        // 1. Frustum base-down vs inverted volume fill at p = 0.5 (R = 6, r = 2, h = 4)
        // Base-down: r(0.5) = 4 -> numer = 0.5*(36 + 24 + 16) = 38, denom = 36 + 12 + 4 = 52 -> 38/52 = 19/26
        // Inverted: r_inv(0.5) = 4 -> numer = 0.5*(4 + 8 + 16) = 14, denom = 52 -> 14/52 = 7/26
        // Notice: baseDown(0.5) + inverted(0.5) = 19/26 + 7/26 = 26/26 = 1.0!
        val frustFill = Math3DEngine.calculateVolumeFill(Solid3DShapeType.FRUSTUM, 6.0, 2.0, 4.0, heightFillFraction = 0.5f)
        assertEquals(19.0 / 26.0, frustFill.volumeFillFraction, 1e-5)
        assertEquals(7.0 / 26.0, frustFill.invertedApexDownVolumeFraction ?: 0.0, 1e-5)
        assertEquals(1.0, frustFill.volumeFillFraction + (frustFill.invertedApexDownVolumeFraction ?: 0.0), 1e-6)

        // 2. Negative & Mixed Fractions
        val negFrac = com.example.math.VisualMathStudioEngine.analyzeFractionOperation(
            -1, 2, 3, 4, com.example.math.FractionOpType.ADD
        )
        assertEquals(1, negFrac.simplifiedNum)
        assertEquals(4, negFrac.simplifiedDen)

        val improperFrac = com.example.math.VisualMathStudioEngine.analyzeFractionOperation(
            5, 4, 1, 2, com.example.math.FractionOpType.ADD
        )
        assertEquals(7, improperFrac.simplifiedNum)
        assertEquals(4, improperFrac.simplifiedDen)
        assertEquals(1, improperFrac.mixedWhole)
        assertEquals(3, improperFrac.mixedRemNum)
        assertTrue(improperFrac.stepsBn.last().contains("মিশ্র ভগ্নাংশ"))

        // 3. Algebra Tiles: Non-monic quadratic (2x^2 + 5x + 2 = (x + 2)(2x + 1)) and linear distribution (3x + 6 = 3(x + 2))
        val nonMonic = com.example.math.VisualMathStudioEngine.analyzeAlgebraTiles(
            xSquaredCountInput = 2,
            xPositiveInput = 5,
            xNegativeInput = 0,
            unitPositiveInput = 2,
            unitNegativeInput = 0
        )
        assertNotNull("2x^2 + 5x + 2 must factorize", nonMonic.factorizedFormBn)

        val distributive = com.example.math.VisualMathStudioEngine.analyzeAlgebraTiles(
            xSquaredCountInput = 0,
            xPositiveInput = 3,
            xNegativeInput = 0,
            unitPositiveInput = 6,
            unitNegativeInput = 0
        )
        assertEquals("৩(x + ২)", distributive.factorizedFormBn)

        // 4. 3D Coordinates: Direction cosines unit norm (cos^2 alpha + cos^2 beta + cos^2 gamma = 1)
        val coord = com.example.math.VisualMathStudioEngine.analyze3DCoordinates(
            com.example.math.Point3DCoord(1.0, 2.0, -1.0),
            com.example.math.Point3DCoord(4.0, 6.0, 4.0)
        )
        val (ca, cb, cc) = coord.directionCosines
        assertEquals(1.0, ca * ca + cb * cb + cc * cc, 1e-6)
        assertEquals(5.0, coord.xyProjectionDistance, 1e-6)
        assertTrue(coord.stepsBn.any { it.contains("Direction Cosines") })
    }
}


