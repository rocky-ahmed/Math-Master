package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.math.AlgebraFormulaProvider
import com.example.math.BengaliNumberFormatter
import com.example.math.FormulaLibraryProvider
import com.example.math.FormulaSubject
import com.example.math.MentalMathCategory
import com.example.math.MentalMathDifficulty
import com.example.math.MentalMathGenerator
import com.example.math.MentalMathMode
import com.example.math.MultiplicationProvider
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.math.PracticeLabGenerator
import com.example.math.PrimeNumberProvider
import com.example.math.QuizGenerator
import com.example.math.QuizTopic
import com.example.math.SquareCubeProvider
import com.example.math.TableRange
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Math Master", appName)
    }

    @Test
    fun `verify MainActivity launches without crash`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
    }

    @Test
    fun `verify multiplication tables 1 to 40 in 4 ranges`() {
        assertEquals(4, TableRange.values().size)

        val range1 = MultiplicationProvider.getTablesForRange(TableRange.RANGE_1_10)
        val range2 = MultiplicationProvider.getTablesForRange(TableRange.RANGE_11_20)
        val range3 = MultiplicationProvider.getTablesForRange(TableRange.RANGE_21_30)
        val range4 = MultiplicationProvider.getTablesForRange(TableRange.RANGE_31_40)

        assertEquals(10, range1.size)
        assertEquals(10, range2.size)
        assertEquals(10, range3.size)
        assertEquals(10, range4.size)

        // Verify table 7
        val table7 = MultiplicationProvider.getTable(7)
        assertEquals(10, table7.rows.size)
        assertEquals(7, table7.rows[0].product) // 7 * 1
        assertEquals(70, table7.rows[9].product) // 7 * 10
    }

    @Test
    fun `verify prime numbers count in ranges`() {
        val primes1To100 = PrimeNumberProvider.primes1To100
        val primes101To200 = PrimeNumberProvider.primes101To200

        assertEquals(25, primes1To100.size)
        assertEquals(21, primes101To200.size)

        assertTrue(PrimeNumberProvider.isPrime(2))
        assertTrue(PrimeNumberProvider.isPrime(97))
        assertTrue(PrimeNumberProvider.isPrime(101))
        assertTrue(PrimeNumberProvider.isPrime(199))

        assertFalse(PrimeNumberProvider.isPrime(1))
        assertFalse(PrimeNumberProvider.isPrime(4))
        assertFalse(PrimeNumberProvider.isPrime(91)) // 7 * 13
        assertFalse(PrimeNumberProvider.isPrime(119)) // 7 * 17
    }

    @Test
    fun `verify squares and cubes from 1 to 20`() {
        val squareList = SquareCubeProvider.squareTableList
        val cubeList = SquareCubeProvider.cubeTableList

        assertEquals(20, squareList.size) // 1 to 20
        assertEquals(20, cubeList.size)   // 1 to 20

        val item1 = squareList.first { it.n == 1 }
        assertEquals("1² = 1", item1.squareText)
        assertEquals("√1 = 1", item1.squareRootText)

        val item12 = squareList.first { it.n == 12 }
        assertEquals("12² = 144", item12.squareText)
        assertEquals("√144 = 12", item12.squareRootText)

        val item20 = squareList.first { it.n == 20 }
        assertEquals("20² = 400", item20.squareText)
        assertEquals("√400 = 20", item20.squareRootText)

        val cube1 = cubeList.first { it.n == 1 }
        assertEquals("1³ = 1", cube1.cubeText)
        assertEquals("∛1 = 1", cube1.cubeRootText)

        val cube10 = cubeList.first { it.n == 10 }
        assertEquals("10³ = 1000", cube10.cubeText)
        assertEquals("∛1000 = 10", cube10.cubeRootText)

        val cube20 = cubeList.first { it.n == 20 }
        assertEquals("20³ = 8000", cube20.cubeText)
        assertEquals("∛8000 = 20", cube20.cubeRootText)
    }

    @Test
    fun `verify algebraic square and cube formulas`() {
        val squares = AlgebraFormulaProvider.squareFormulas
        val cubes = AlgebraFormulaProvider.cubeFormulas

        assertEquals(14, squares.size) // Exact 14 square formulas
        assertEquals(8, cubes.size)     // Exact 8 cube formulas

        assertTrue(squares.any { it.formulaLatex.contains("(a + b)² = a² + 2ab + b²") })
        assertTrue(squares.any { it.formulaLatex.contains("(a − b − c)²") })
        assertTrue(cubes.any { it.formulaLatex.contains("(a + b)³ = a³ + 3a²b + 3ab² + b³") })
        assertTrue(cubes.any { it.formulaLatex.contains("a³ − b³ = (a − b)(a² + ab + b²)") })
    }

    @Test
    fun `verify quiz question generator`() {
        val questions = QuizGenerator.generateQuiz(QuizTopic.MULTIPLICATION, 5)
        assertEquals(5, questions.size)
        questions.forEach { q ->
            assertEquals(4, q.optionsBn.size)
            assertTrue(q.correctIndex in 0..3)
        }
    }

    @Test
    fun `verify bengali numeral converter`() {
        assertEquals("০", BengaliNumberFormatter.toBn(0))
        assertEquals("১২৩৪৫", BengaliNumberFormatter.toBn(12345))
        assertEquals("১০০", BengaliNumberFormatter.toBn(100))
    }

    @Test
    fun `verify math viewmodel creation and streak initialization`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)
        assertTrue(viewModel.streakDays.value >= 1)
        assertTrue(viewModel.longestStreak.value >= 1)
        assertEquals(7, viewModel.weeklyActivities.value.size)
        assertEquals(com.example.ui.Screen.Home, viewModel.currentScreen)
    }

    @Test
    fun `verify prime numbers magic code for 1 to 100 and 101 to 200`() {
        val magic100 = PrimeNumberProvider.magicCode1To100
        assertEquals("4422322321", magic100.codeString)
        assertEquals(25, magic100.totalPrimes)
        assertEquals(10, magic100.decadeGroups.size)
        assertEquals(25, magic100.decadeGroups.sumOf { it.magicDigit })
        assertEquals(listOf(4, 4, 2, 2, 3, 2, 2, 3, 2, 1), magic100.decadeGroups.map { it.magicDigit })

        val magic200 = PrimeNumberProvider.magicCode101To200
        assertEquals("4113122214", magic200.codeString)
        assertEquals(21, magic200.totalPrimes)
        assertEquals(10, magic200.decadeGroups.size)
        assertEquals(21, magic200.decadeGroups.sumOf { it.magicDigit })
        assertEquals(listOf(4, 1, 1, 3, 1, 2, 2, 2, 1, 4), magic200.decadeGroups.map { it.magicDigit })

        // Verify first decade of 1-100 has 4 primes: 2, 3, 5, 7
        assertEquals(listOf(2, 3, 5, 7), magic100.decadeGroups[0].primes)
        // Verify last decade of 1-100 has 1 prime: 97
        assertEquals(listOf(97), magic100.decadeGroups[9].primes)

        // Verify 101-110 has 4 primes: 101, 103, 107, 109
        assertEquals(listOf(101, 103, 107, 109), magic200.decadeGroups[0].primes)
        // Verify 191-200 has 4 primes: 191, 193, 197, 199
        assertEquals(listOf(191, 193, 197, 199), magic200.decadeGroups[9].primes)
    }

    @Test
    fun `verify customization state and persistence`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // Initial default state
        assertEquals(com.example.ui.customization.CustomLayoutType.GRID, viewModel.customizationState.value.global.layoutType)
        assertTrue(viewModel.customizationState.value.sections.isEmpty())

        // Update preview layout to GRID and configure section
        viewModel.updatePreviewGlobal { it.copy(layoutType = com.example.ui.customization.CustomLayoutType.GRID, gridColumns = 3) }
        viewModel.updatePreviewSection(com.example.ui.customization.SectionKey.TABLES) {
            it.copy(iconId = com.example.ui.customization.AvailableIcon.CALCULATE.id, iconSizeDp = 32, cornerRadiusDp = 20)
        }

        assertEquals(com.example.ui.customization.CustomLayoutType.GRID, viewModel.previewCustomizationState.value.global.layoutType)
        assertEquals(3, viewModel.previewCustomizationState.value.global.gridColumns)
        val secCfg = viewModel.previewCustomizationState.value.getSectionConfig(com.example.ui.customization.SectionKey.TABLES)
        assertEquals("calculate", secCfg.iconId)
        assertEquals(32, secCfg.iconSizeDp)
        assertEquals(20, secCfg.cornerRadiusDp)

        // Save customization
        viewModel.saveCustomization()
        assertEquals(com.example.ui.customization.CustomLayoutType.GRID, viewModel.customizationState.value.global.layoutType)

        // Discard preview returns to saved
        viewModel.updatePreviewGlobal { it.copy(layoutType = com.example.ui.customization.CustomLayoutType.COMPACT_LIST) }
        viewModel.discardCustomizationPreview()
        assertEquals(com.example.ui.customization.CustomLayoutType.GRID, viewModel.previewCustomizationState.value.global.layoutType)

        // Verify independent section reset
        viewModel.resetPreviewSection(com.example.ui.customization.SectionKey.TABLES)
        assertNull(viewModel.previewCustomizationState.value.sections[com.example.ui.customization.SectionKey.TABLES.id])

        // Verify reset all customizations restores defaults
        viewModel.resetAllCustomizations()
        assertEquals(com.example.ui.customization.CustomLayoutType.GRID, viewModel.customizationState.value.global.layoutType)
        assertTrue(viewModel.customizationState.value.sections.isEmpty())
    }

    @Test
    fun `verify color contrast and hex validation`() {
        assertTrue(com.example.ui.customization.ColorUtils.isValidHex("#1E3A8A"))
        assertTrue(com.example.ui.customization.ColorUtils.isValidHex("1E3A8A"))
        assertFalse(com.example.ui.customization.ColorUtils.isValidHex("invalid_hex"))

        val darkBlue = com.example.ui.customization.ColorUtils.parseHexColor("#1E3A8A")
        assertNotNull(darkBlue)
        assertTrue(com.example.ui.customization.ColorUtils.hasAdequateContrast(androidx.compose.ui.graphics.Color.White, darkBlue!!))
    }

    @Test
    fun `verify theme mode persistence across ViewModel instances`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel1 = com.example.ui.MathViewModel(app)

        // Set Dark theme
        viewModel1.setThemeMode(com.example.ui.theme.AppThemeMode.DARK)
        assertEquals(com.example.ui.theme.AppThemeMode.DARK, viewModel1.themeMode.value)

        // New ViewModel instance should restore DARK from SharedPreferences
        val viewModel2 = com.example.ui.MathViewModel(app)
        assertEquals(com.example.ui.theme.AppThemeMode.DARK, viewModel2.themeMode.value)

        // Set Light theme
        viewModel2.setThemeMode(com.example.ui.theme.AppThemeMode.LIGHT)
        assertEquals(com.example.ui.theme.AppThemeMode.LIGHT, viewModel2.themeMode.value)

        val viewModel3 = com.example.ui.MathViewModel(app)
        assertEquals(com.example.ui.theme.AppThemeMode.LIGHT, viewModel3.themeMode.value)

        // Set System default
        viewModel3.setThemeMode(com.example.ui.theme.AppThemeMode.SYSTEM)
        assertEquals(com.example.ui.theme.AppThemeMode.SYSTEM, viewModel3.themeMode.value)
    }

    @Test
    fun `verify full quiz 10 of 10 completion, final score, next quiz, retry, review, and return to quiz list`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // Start Multiplication Quiz
        viewModel.startQuiz(QuizTopic.MULTIPLICATION, 10)
        var session = viewModel.quizSession.value
        assertEquals(10, session.questions.size)
        assertEquals(0, session.currentIndex)
        assertFalse(session.isFinished)
        assertEquals(com.example.ui.Screen.QuizPlay, viewModel.currentScreen)

        // Answer all 10 questions correctly
        for (i in 0 until 10) {
            val q = viewModel.quizSession.value.questions[i]
            // Select correct option
            viewModel.selectQuizOption(q.correctIndex)
            assertEquals(q.correctIndex, viewModel.quizSession.value.selectedOptionIndex)

            // Submit answer
            viewModel.submitQuizAnswer()
            assertTrue(viewModel.quizSession.value.isAnswerSubmitted)
            assertEquals(i + 1, viewModel.quizSession.value.correctCount)

            // Advance to next question (or finish on the 10th question)
            viewModel.nextQuizQuestion()
        }

        // Quiz must be finished!
        session = viewModel.quizSession.value
        assertTrue("Quiz must be finished after question 10", session.isFinished)
        assertEquals(10, session.correctCount)
        assertEquals(10, session.questions.size)
        val percentage = (session.correctCount * 100) / session.questions.size
        assertEquals(100, percentage)

        // Verify Review Answers toggle
        assertFalse(session.isReviewMode)
        viewModel.toggleQuizReviewMode()
        assertTrue(viewModel.quizSession.value.isReviewMode)
        viewModel.toggleQuizReviewMode()
        assertFalse(viewModel.quizSession.value.isReviewMode)

        // Verify Next Quiz Topic button starts next topic cleanly
        viewModel.startNextQuizTopic()
        val nextSession = viewModel.quizSession.value
        assertFalse(nextSession.isFinished)
        assertEquals(0, nextSession.currentIndex)
        assertEquals(10, nextSession.questions.size)
        assertEquals(QuizTopic.PRIMES, nextSession.topic)

        // Verify Retry Quiz button restarts current topic
        viewModel.startQuiz(QuizTopic.PRIMES, 10)
        val retrySession = viewModel.quizSession.value
        assertFalse(retrySession.isFinished)
        assertEquals(0, retrySession.currentIndex)
        assertEquals(QuizTopic.PRIMES, retrySession.topic)

        // Verify Return to Quiz List exits cleanly
        viewModel.exitQuizToTopicList()
        assertEquals(com.example.ui.Screen.Quizzes, viewModel.currentScreen)
        assertFalse(viewModel.quizSession.value.isFinished)
        assertTrue(viewModel.quizSession.value.questions.isEmpty())
    }

    @Test
    fun `verify quiz session state with partial score 7 of 10`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        viewModel.startQuiz(QuizTopic.SQUARES_CUBES, 10)
        for (i in 0 until 10) {
            val q = viewModel.quizSession.value.questions[i]
            // Answer first 7 correctly, last 3 incorrectly
            val chosenOpt = if (i < 7) q.correctIndex else (q.correctIndex + 1) % 4
            viewModel.selectQuizOption(chosenOpt)
            viewModel.submitQuizAnswer()
            viewModel.nextQuizQuestion()
        }

        val session = viewModel.quizSession.value
        assertTrue(session.isFinished)
        assertEquals(7, session.correctCount)
        assertEquals(10, session.questions.size)
        val percentage = (session.correctCount * 100) / session.questions.size
        assertEquals(70, percentage)
    }

    @Test
    fun `verify all customization options and categories reset`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // Test all Layout types
        com.example.ui.customization.CustomLayoutType.values().forEach { layoutType ->
            viewModel.updatePreviewGlobal { it.copy(layoutType = layoutType) }
            assertEquals(layoutType, viewModel.previewCustomizationState.value.global.layoutType)
        }

        // Test all Font weights
        com.example.ui.customization.CustomFontWeight.values().forEach { weight ->
            viewModel.updatePreviewGlobal { it.copy(fontWeight = weight) }
            assertEquals(weight, viewModel.previewCustomizationState.value.global.fontWeight)
        }

        // Test numerical parameters
        viewModel.updatePreviewGlobal {
            it.copy(
                gridColumns = 4,
                cornerRadiusDp = 24,
                horizontalGapDp = 18,
                verticalGapDp = 18,
                titleFontSizeSp = 20,
                subtitleFontSizeSp = 14,
                contentAlignmentCenter = true,
                showBorder = true,
                borderThicknessDp = 2,
                customPrimaryActionHex = "#1E3A8A",
                customLightBackgroundHex = "#F1F5F9",
                customDarkBackgroundHex = "#0B0F19"
            )
        }

        val g = viewModel.previewCustomizationState.value.global
        assertEquals(4, g.gridColumns)
        assertEquals(24, g.cornerRadiusDp)
        assertEquals(20, g.titleFontSizeSp)
        assertEquals(14, g.subtitleFontSizeSp)
        assertTrue(g.contentAlignmentCenter)
        assertTrue(g.showBorder)
        assertEquals(2, g.borderThicknessDp)
        assertEquals("#1E3A8A", g.customPrimaryActionHex)
        assertEquals("#F1F5F9", g.customLightBackgroundHex)
        assertEquals("#0B0F19", g.customDarkBackgroundHex)

        // Test reset category: LAYOUT
        viewModel.resetPreviewCategory("LAYOUT")
        assertEquals(com.example.ui.customization.CustomLayoutType.GRID, viewModel.previewCustomizationState.value.global.layoutType)
        assertEquals(2, viewModel.previewCustomizationState.value.global.gridColumns)
        assertFalse(viewModel.previewCustomizationState.value.global.contentAlignmentCenter)
        // Typography and colors should remain untouched
        assertEquals(20, viewModel.previewCustomizationState.value.global.titleFontSizeSp)
        assertEquals("#1E3A8A", viewModel.previewCustomizationState.value.global.customPrimaryActionHex)

        // Test reset category: TYPOGRAPHY
        viewModel.resetPreviewCategory("TYPOGRAPHY")
        assertEquals(16, viewModel.previewCustomizationState.value.global.titleFontSizeSp)
        assertEquals(12, viewModel.previewCustomizationState.value.global.subtitleFontSizeSp)
        assertEquals(com.example.ui.customization.CustomFontWeight.MEDIUM, viewModel.previewCustomizationState.value.global.fontWeight)

        // Test reset category: GLOBAL_THEME
        viewModel.resetPreviewCategory("GLOBAL_THEME")
        assertNull(viewModel.previewCustomizationState.value.global.customPrimaryActionHex)
        assertNull(viewModel.previewCustomizationState.value.global.customLightBackgroundHex)
        assertNull(viewModel.previewCustomizationState.value.global.customDarkBackgroundHex)
    }

    @Test
    fun `verify formula library categorizes math formulas in algebra geometry and calculus in bengali`() {
        val all = FormulaLibraryProvider.allFormulas
        val algebra = FormulaLibraryProvider.getFormulasBySubject(FormulaSubject.ALGEBRA)
        val geometry = FormulaLibraryProvider.getFormulasBySubject(FormulaSubject.GEOMETRY)
        val calculus = FormulaLibraryProvider.getFormulasBySubject(FormulaSubject.CALCULUS)

        assertTrue("All formulas must be at least 40 items", all.size >= 40)
        assertTrue("Algebra formulas must have at least 15 items", algebra.size >= 15)
        assertTrue("Geometry formulas must have at least 15 items", geometry.size >= 15)
        assertTrue("Calculus formulas must have at least 14 items", calculus.size >= 14)

        // Verify Bengali categorization and subject names
        assertEquals("বীজগণিত", FormulaSubject.ALGEBRA.titleBn)
        assertEquals("জ্যামিতি", FormulaSubject.GEOMETRY.titleBn)
        assertEquals("ক্যালকুলাস", FormulaSubject.CALCULUS.titleBn)

        // Verify each formula contains necessary student reference fields
        all.forEach { f ->
            assertTrue("Formula ID must not be empty", f.id.isNotEmpty())
            assertTrue("Formula titleBn must not be empty", f.titleBn.isNotEmpty())
            assertTrue("Formula expression must not be empty", f.formulaExpression.isNotEmpty())
            assertTrue("Formula explanationBn must not be empty", f.explanationBn.isNotEmpty())
            assertTrue("Formula example problem must not be empty", f.exampleProblemBn.isNotEmpty())
            assertTrue("Formula example solution must not be empty", f.exampleSolutionBn.isNotEmpty())
            assertTrue("Formula tags must not be empty", f.tags.isNotEmpty())
        }

        // Verify key Algebra formulas
        assertTrue(algebra.any { it.formulaExpression.contains("(a + b)² = a² + 2ab + b²") })
        assertTrue(algebra.any { it.formulaExpression.contains("a² − b² = (a + b)(a − b)") })
        assertTrue(algebra.any { it.formulaExpression.contains("aᵐ · aⁿ = aᵐ⁺ⁿ") })
        assertTrue(algebra.any { it.formulaExpression.contains("Tₙ = a + (n − 1)d") })
        assertTrue(algebra.any { it.formulaExpression.contains("x = [−b ± √(b² − 4ac)] / (2a)") })

        // Verify key Geometry formulas
        assertTrue(geometry.any { it.formulaExpression.contains("½ × ভূমি × উচ্চতা") || it.formulaExpression.contains("½ · b · h") })
        assertTrue(geometry.any { it.formulaExpression.contains("c² = a² + b²") })
        assertTrue(geometry.any { it.formulaExpression.contains("2πr") && it.formulaExpression.contains("πr²") })
        assertTrue(geometry.any { it.formulaExpression.contains("4πr²") && it.formulaExpression.contains("(4/3)πr³") })
        assertTrue(geometry.any { it.formulaExpression.contains("√[(x₂ − x₁)² + (y₂ − y₁)²]") })

        // Verify key Calculus formulas
        assertTrue(calculus.any { it.formulaExpression.contains("d/dx (xⁿ) = n · xⁿ⁻¹") })
        assertTrue(calculus.any { it.formulaExpression.contains("u(dv/dx) + v(du/dx)") })
        assertTrue(calculus.any { it.formulaExpression.contains("d/dx(sin x) = cos x") })
        assertTrue(calculus.any { it.formulaExpression.contains("∫ xⁿ dx = (xⁿ⁺¹ / (n + 1)) + C") })
        assertTrue(calculus.any { it.formulaExpression.contains("lim(x→0) [sin x / x] = 1") })
    }

    @Test
    fun `verify formula library bookmark integration and screen navigation`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // Test navigation to FormulaLibrary
        viewModel.navigateTo(com.example.ui.Screen.FormulaLibrary)
        assertEquals(com.example.ui.Screen.FormulaLibrary, viewModel.currentScreen)

        // Test bookmarking a library formula
        val formula = FormulaLibraryProvider.allFormulas.first()
        val bookmark = com.example.data.model.BookmarkEntity(
            id = formula.id,
            category = "FORMULA_LIBRARY",
            titleBn = formula.titleBn,
            subtitleBn = "${formula.subject.titleBn} • ${formula.subCategory}",
            detailBn = formula.formulaExpression
        )

        assertFalse(viewModel.isBookmarked(formula.id))
        viewModel.toggleBookmark(bookmark)

        // Verify navigateBack
        assertTrue(viewModel.navigateBack())
        assertEquals(com.example.ui.Screen.Home, viewModel.currentScreen)
    }

    @Test
    fun `verify practice lab generates mathematically valid questions for all 8 categories across all 3 difficulty levels`() {
        assertEquals(12, PracticeCategory.values().size)
        assertEquals(4, PracticeDifficulty.values().size)
        assertEquals(3, com.example.math.QuestionInputFormat.values().size)

        PracticeCategory.values().forEach { cat ->
            PracticeDifficulty.values().forEach { diff ->
                com.example.math.QuestionInputFormat.values().forEach { format ->
                    val questions = PracticeLabGenerator.generateSession(cat, diff, 4, format)
                    assertEquals("Each category ($cat, $diff, $format) must produce 4 questions", 4, questions.size)

                    questions.forEach { q ->
                        assertEquals(cat, q.category)
                        assertEquals(diff, q.difficulty)
                        assertEquals(format, q.inputFormat)
                        assertTrue("Question must pass structural integrity check", PracticeLabGenerator.verifyQuestionIntegrity(q))
                        assertTrue("Correct answer must validate via SharedMathEngine", com.example.math.SharedMathEngine.isAnswerCorrect(q.correctAnswerBn, q.optionsBn[q.correctIndex]))
                        if (format == com.example.math.QuestionInputFormat.TRUE_FALSE) {
                            assertEquals(2, q.optionsBn.size)
                            assertNotNull(q.trueFalseStatementBn)
                            assertNotNull(q.isTrueFalseCorrect)
                        } else {
                            assertEquals(4, q.optionsBn.size)
                            assertTrue(q.correctIndex in 0..3)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `verify practice lab session execution answer checking duplicate submit protection and completion stats`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // Start session: Fractions, Medium, 5 questions
        viewModel.startPracticeSession(PracticeCategory.FRACTIONS, PracticeDifficulty.MEDIUM, 5)
        var session = viewModel.practiceSession.value
        assertEquals(5, session.questions.size)
        assertEquals(0, session.currentIndex)
        assertFalse(session.isAnswerSubmitted)
        assertFalse(session.isFinished)
        assertEquals(com.example.ui.Screen.PracticeLabPlay, viewModel.currentScreen)

        // Answer question 1 correctly
        val q0 = session.questions[0]
        viewModel.selectPracticeOption(q0.correctIndex)
        viewModel.submitPracticeAnswer()
        session = viewModel.practiceSession.value
        assertTrue(session.isAnswerSubmitted)
        assertTrue(session.isCorrect)
        assertEquals(1, session.correctCount)
        assertEquals(0, session.wrongCount)

        // Duplicate submit attempt should NOT increment score again
        viewModel.submitPracticeAnswer()
        session = viewModel.practiceSession.value
        assertEquals(1, session.correctCount)

        // Advance to Q2
        viewModel.nextPracticeQuestion()
        session = viewModel.practiceSession.value
        assertEquals(1, session.currentIndex)
        assertFalse(session.isAnswerSubmitted)

        // Answer Q2 incorrectly
        val q1 = session.questions[1]
        val wrongOpt = (q1.correctIndex + 1) % 4
        viewModel.selectPracticeOption(wrongOpt)
        viewModel.submitPracticeAnswer()
        session = viewModel.practiceSession.value
        assertTrue(session.isAnswerSubmitted)
        assertFalse(session.isCorrect)
        assertEquals(1, session.correctCount)
        assertEquals(1, session.wrongCount)

        // Answer remaining 3 questions correctly
        for (i in 2 until 5) {
            viewModel.nextPracticeQuestion()
            val q = viewModel.practiceSession.value.questions[i]
            viewModel.selectPracticeOption(q.correctIndex)
            viewModel.submitPracticeAnswer()
        }

        // Finish session on last question
        viewModel.nextPracticeQuestion()
        session = viewModel.practiceSession.value
        assertTrue("Session must be finished after question 5", session.isFinished)
        assertEquals(4, session.correctCount)
        assertEquals(1, session.wrongCount)
        assertEquals(5, session.questions.size)
    }

    @Test
    fun `verify practice lab retry review mode and category exit navigation`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        viewModel.startPracticeSession(PracticeCategory.PERCENTAGES, PracticeDifficulty.EASY, 5)
        var session = viewModel.practiceSession.value
        assertEquals(PracticeCategory.PERCENTAGES, session.category)

        // Toggle review mode
        assertFalse(session.isReviewMode)
        viewModel.togglePracticeReviewMode()
        assertTrue(viewModel.practiceSession.value.isReviewMode)
        viewModel.togglePracticeReviewMode()
        assertFalse(viewModel.practiceSession.value.isReviewMode)

        // Retry session
        viewModel.retryPracticeSession()
        session = viewModel.practiceSession.value
        assertEquals(0, session.currentIndex)
        assertFalse(session.isFinished)
        assertEquals(PracticeCategory.PERCENTAGES, session.category)

        // Exit practice lab
        viewModel.exitPracticeToCategories()
        assertFalse(viewModel.practiceSession.value.isFinished)
        assertTrue(viewModel.practiceSession.value.questions.isEmpty())
        assertEquals(com.example.ui.Screen.PracticeLab, viewModel.currentScreen)
    }

    @Test
    fun `verify mental math arithmetic correctness across all 6 categories and 3 difficulties`() {
        assertEquals(10, MentalMathCategory.values().size)
        assertEquals(3, MentalMathDifficulty.values().size)

        MentalMathCategory.values().forEach { cat ->
            MentalMathDifficulty.values().forEach { diff ->
                val questions = MentalMathGenerator.generateQuestions(cat, diff, 5)
                assertEquals("Should generate 5 questions for $cat ($diff)", 5, questions.size)

                questions.forEach { q ->
                    assertEquals(cat, q.category)
                    assertEquals(diff, q.difficulty)
                    assertTrue("Expression must not be empty", q.expressionBn.isNotEmpty())
                    assertEquals("Must have 4 distinct options", 4, q.options.toSet().size)
                    assertEquals("Must have 4 Bengali options", 4, q.optionsBn.size)
                    assertTrue("Correct index must be in 0..3", q.correctIndex in 0..3)
                    assertEquals("Option at correctIndex must equal correctAnswer", q.correctAnswer, q.options[q.correctIndex])
                    assertEquals("CorrectAnswerBn must match Bengali formatter", BengaliNumberFormatter.toBn(q.correctAnswer), q.correctAnswerBn)
                    assertTrue("Correct answer must be positive", q.correctAnswer > 0)
                }
            }
        }
    }

    @Test
    fun `verify division edge cases in mental math - no division by zero, exact integer quotients, positive dividends`() {
        listOf(MentalMathDifficulty.EASY, MentalMathDifficulty.MEDIUM, MentalMathDifficulty.HARD).forEach { diff ->
            val divQuestions = MentalMathGenerator.generateQuestions(MentalMathCategory.DIVISION, diff, 20)
            assertEquals(20, divQuestions.size)

            divQuestions.forEach { q ->
                // Plain expression is formatted as "$dividend / $divisor"
                val parts = q.expressionPlain.split("/")
                assertEquals(2, parts.size)
                val dividend = parts[0].trim().toInt()
                val divisor = parts[1].trim().toInt()

                assertTrue("Divisor must never be zero", divisor > 0)
                assertTrue("Dividend must be greater than divisor", dividend >= divisor)
                assertEquals("Dividend must be evenly divisible by divisor", 0, dividend % divisor)
                assertEquals("Quotient must match correctAnswer", dividend / divisor, q.correctAnswer)
                assertTrue("All options must be positive integers", q.options.all { it > 0 })
            }
        }
    }

    @Test
    fun `verify mental math scoring, combo multiplier, streak, and duplicate answer prevention`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // Start Fixed Questions challenge: Addition, Medium (+20 base points), 5 questions
        viewModel.startMentalMathChallenge(
            category = MentalMathCategory.ADDITION,
            difficulty = MentalMathDifficulty.MEDIUM,
            mode = MentalMathMode.FIXED_QUESTIONS,
            durationOrCount = 5
        )

        var session = viewModel.mentalMathSession.value
        assertEquals(5, session.questions.size)
        assertEquals(0, session.currentIndex)
        assertEquals(0, session.score)
        assertEquals(0, session.streak)
        assertFalse(session.isFinished)
        assertEquals(com.example.ui.Screen.MentalMathPlay, viewModel.currentScreen)

        // Question 1: Answer correctly (streak 1, 1.0x -> +20 pts)
        val q0 = session.questions[0]
        viewModel.submitMentalMathAnswer(q0.correctAnswer)
        session = viewModel.mentalMathSession.value
        assertTrue(session.isAnswerSubmitted)
        assertEquals(true, session.lastAnswerCorrect)
        assertEquals(20, session.score)
        assertEquals(1, session.streak)
        assertEquals(1, session.correctCount)

        // Duplicate answer tap protection: must NOT increase score or streak again
        viewModel.submitMentalMathAnswer(q0.correctAnswer)
        session = viewModel.mentalMathSession.value
        assertEquals(20, session.score)
        assertEquals(1, session.correctCount)

        // Next to Question 2
        viewModel.nextMentalMathQuestion()
        val q1 = viewModel.mentalMathSession.value.questions[1]
        // Question 2: Answer correctly (streak 2, 1.5x -> +30 pts, total 50)
        viewModel.submitMentalMathAnswer(q1.correctAnswer)
        session = viewModel.mentalMathSession.value
        assertEquals(50, session.score)
        assertEquals(2, session.streak)
        assertEquals(2, session.correctCount)

        // Next to Question 3
        viewModel.nextMentalMathQuestion()
        val q2 = viewModel.mentalMathSession.value.questions[2]
        // Question 3: Answer incorrectly
        val wrongAns = q2.options.first { it != q2.correctAnswer }
        viewModel.submitMentalMathAnswer(wrongAns)
        session = viewModel.mentalMathSession.value
        assertEquals(50, session.score) // Score unchanged
        assertEquals(0, session.streak) // Streak reset to 0
        assertEquals(2, session.highestStreak) // Highest streak retained
        assertEquals(2, session.correctCount)
        assertEquals(1, session.wrongCount)

        // Next to Question 4
        viewModel.nextMentalMathQuestion()
        val q3 = viewModel.mentalMathSession.value.questions[3]
        viewModel.submitMentalMathAnswer(q3.correctAnswer) // +20 (streak 1) -> 70
        assertEquals(70, viewModel.mentalMathSession.value.score)

        // Next to Question 5
        viewModel.nextMentalMathQuestion()
        val q4 = viewModel.mentalMathSession.value.questions[4]
        viewModel.submitMentalMathAnswer(q4.correctAnswer) // +30 (streak 2, 1.5x) -> 100
        assertEquals(100, viewModel.mentalMathSession.value.score)

        // Advance after last question -> Finished!
        viewModel.nextMentalMathQuestion()
        session = viewModel.mentalMathSession.value
        assertTrue("Session must be finished after question 5", session.isFinished)
        assertEquals(4, session.correctCount)
        assertEquals(1, session.wrongCount)
        assertEquals(100, session.score)
    }

    @Test
    fun `verify mental math timer countdown and completion when time reaches zero`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)
        val testClock = object : com.example.math.MonotonicClock {
            var timeMs = 100000L
            override fun elapsedRealtime() = timeMs
        }
        viewModel.clock = testClock

        // Start Timed Sprint with 3 seconds
        viewModel.startMentalMathChallenge(
            category = MentalMathCategory.MULTIPLICATION,
            difficulty = MentalMathDifficulty.EASY,
            mode = MentalMathMode.TIMED_SPRINT,
            durationOrCount = 3
        )

        var session = viewModel.mentalMathSession.value
        assertEquals(3, session.timeRemainingSeconds)
        assertEquals(0, session.elapsedSeconds)
        assertFalse(session.isFinished)

        // Tick 1 (1 second passes)
        testClock.timeMs += 1000L
        viewModel.tickMentalMathTimer()
        session = viewModel.mentalMathSession.value
        assertEquals(2, session.timeRemainingSeconds)
        assertEquals(1, session.elapsedSeconds)
        assertFalse(session.isFinished)

        // Tick 2 (1 second passes)
        testClock.timeMs += 1000L
        viewModel.tickMentalMathTimer()
        session = viewModel.mentalMathSession.value
        assertEquals(1, session.timeRemainingSeconds)
        assertEquals(2, session.elapsedSeconds)
        assertFalse(session.isFinished)

        // Tick 3: Reaches 0 -> Finished!
        testClock.timeMs += 1000L
        viewModel.tickMentalMathTimer()
        session = viewModel.mentalMathSession.value
        assertEquals(0, session.timeRemainingSeconds)
        assertEquals(3, session.elapsedSeconds)
        assertTrue("Challenge must be finished when time reaches 0", session.isFinished)
    }

    @Test
    fun `verify delayed timer updates do not extend session and remaining time never becomes negative`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)
        val testClock = object : com.example.math.MonotonicClock {
            var timeMs = 100000L
            override fun elapsedRealtime() = timeMs
        }
        viewModel.clock = testClock

        // Start 60-second sprint
        viewModel.startMentalMathChallenge(
            category = MentalMathCategory.ADDITION,
            difficulty = MentalMathDifficulty.EASY,
            mode = MentalMathMode.TIMED_SPRINT,
            durationOrCount = 60
        )

        assertEquals(60, viewModel.mentalMathSession.value.timeRemainingSeconds)

        // Simulate 20 seconds delay (e.g. app backgrounded or coroutine delayed)
        testClock.timeMs += 20000L
        viewModel.tickMentalMathTimer()

        // Remaining time must jump directly to 40 seconds (not 59)
        val sessionAfterDelay = viewModel.mentalMathSession.value
        assertEquals(40, sessionAfterDelay.timeRemainingSeconds)
        assertEquals(20, sessionAfterDelay.elapsedSeconds)
        assertFalse(sessionAfterDelay.isFinished)

        // Simulate massive delay past the deadline (e.g. 50 seconds more, total 70s > 60s)
        testClock.timeMs += 50000L
        viewModel.tickMentalMathTimer()

        val finishedSession = viewModel.mentalMathSession.value
        assertEquals(0, finishedSession.timeRemainingSeconds)
        assertTrue("Remaining time must never become negative", finishedSession.timeRemainingSeconds >= 0)
        assertTrue("Session must be finished on timeout", finishedSession.isFinished)

        // Multiple subsequent ticks must not duplicate completion or make time negative
        viewModel.tickMentalMathTimer()
        viewModel.tickMentalMathTimer()
        assertEquals(0, viewModel.mentalMathSession.value.timeRemainingSeconds)
        assertTrue(viewModel.mentalMathSession.value.isFinished)

        // Starting a new session resets the timer correctly
        viewModel.startMentalMathChallenge(
            category = MentalMathCategory.SUBTRACTION,
            difficulty = MentalMathDifficulty.MEDIUM,
            mode = MentalMathMode.TIMED_SPRINT,
            durationOrCount = 30
        )
        val newSession = viewModel.mentalMathSession.value
        assertEquals(30, newSession.timeRemainingSeconds)
        assertEquals(0, newSession.elapsedSeconds)
        assertFalse(newSession.isFinished)
    }

    @Test
    fun `verify mental math retry, review mode, and setup exit navigation`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        viewModel.startMentalMathChallenge(
            category = MentalMathCategory.SQUARES,
            difficulty = MentalMathDifficulty.HARD,
            mode = MentalMathMode.FIXED_QUESTIONS,
            durationOrCount = 10
        )

        // Test review mode toggle
        assertFalse(viewModel.mentalMathSession.value.isReviewMode)
        viewModel.toggleMentalMathReviewMode()
        assertTrue(viewModel.mentalMathSession.value.isReviewMode)
        viewModel.toggleMentalMathReviewMode()
        assertFalse(viewModel.mentalMathSession.value.isReviewMode)

        // Test retry
        viewModel.retryMentalMathChallenge()
        val retriedSession = viewModel.mentalMathSession.value
        assertEquals(0, retriedSession.currentIndex)
        assertFalse(retriedSession.isFinished)
        assertEquals(MentalMathCategory.SQUARES, retriedSession.category)
        assertEquals(MentalMathDifficulty.HARD, retriedSession.difficulty)

        // Test exit to setup
        viewModel.exitMentalMathToSetup()
        assertEquals(com.example.ui.Screen.MentalMath, viewModel.currentScreen)
        assertFalse(viewModel.mentalMathSession.value.isFinished)
    }

    @Test
    fun `verify all 12 home modules navigation and stack back-navigation`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        assertEquals(com.example.ui.Screen.Home, viewModel.currentScreen)
        assertEquals(listOf(com.example.ui.Screen.Home), viewModel.screenStack.value)

        val targetScreens = listOf(
            com.example.ui.Screen.Tables,
            com.example.ui.Screen.Primes,
            com.example.ui.Screen.SquaresCubes,
            com.example.ui.Screen.Formulas,
            com.example.ui.Screen.FormulaLibrary,
            com.example.ui.Screen.PracticeLab,
            com.example.ui.Screen.MentalMath,
            com.example.ui.Screen.NumberSystem,
            com.example.ui.Screen.ProblemSolver,
            com.example.ui.Screen.VisualExplorer,
            com.example.ui.Screen.Quizzes,
            com.example.ui.Screen.Dashboard
        )

        targetScreens.forEach { screen ->
            viewModel.navigateTo(screen)
            assertEquals(screen, viewModel.currentScreen)
            assertTrue(viewModel.screenStack.value.size >= 2)
            viewModel.navigateBack()
            assertEquals(com.example.ui.Screen.Home, viewModel.currentScreen)
            assertEquals(listOf(com.example.ui.Screen.Home), viewModel.screenStack.value)
        }
    }

    @Test
    fun `verify home dashboard grid layout default configuration and migration`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        val customState = viewModel.customizationState.value
        assertEquals(com.example.ui.customization.CustomLayoutType.GRID, customState.global.layoutType)
        assertEquals(2, customState.global.gridColumns)
        assertEquals(20, customState.global.cornerRadiusDp)
    }

    @Test
    fun `verify upgraded formula library 12 categories validation search and related formulas`() {
        assertEquals(12, FormulaSubject.values().size)
        assertTrue("All formulas must pass structural and mathematical integrity check", FormulaLibraryProvider.validateAllFormulas())

        FormulaSubject.values().forEach { subject ->
            val list = FormulaLibraryProvider.getFormulasBySubject(subject)
            assertTrue("Subject $subject must have formulas", list.isNotEmpty())
        }

        val searchQuad = FormulaLibraryProvider.searchFormulas(query = "দ্বিঘাত")
        assertTrue(searchQuad.isNotEmpty())

        val firstFormula = FormulaLibraryProvider.allFormulas.first()
        val related = FormulaLibraryProvider.getRelatedFormulas(firstFormula)
        assertTrue(related.isNotEmpty())
    }

    @Test
    fun `verify practice lab hint system unlimited mode adaptive difficulty and keypad input`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        viewModel.startPracticeSession(
            category = PracticeCategory.ARITHMETIC,
            difficulty = PracticeDifficulty.ADAPTIVE,
            count = 5,
            inputFormat = com.example.math.QuestionInputFormat.KEYPAD,
            isUnlimitedMode = true
        )

        var session = viewModel.practiceSession.value
        assertTrue(session.isUnlimitedMode)
        assertEquals(com.example.math.QuestionInputFormat.KEYPAD, session.inputFormat)
        assertFalse(session.isHintRevealed)

        // Reveal hint
        viewModel.revealPracticeHint()
        session = viewModel.practiceSession.value
        assertTrue(session.isHintRevealed)
        assertEquals(1, session.hintsUsedCount)

        // Submit correct answer via keypad text
        val q0 = session.questions[0]
        viewModel.setPracticeCustomInput(q0.correctAnswerBn)
        viewModel.submitPracticeAnswer()
        session = viewModel.practiceSession.value
        assertTrue(session.isAnswerSubmitted)
        assertTrue(session.isCorrect)
        assertEquals(1, session.correctCount)

        // Finish unlimited session explicitly
        viewModel.finishPracticeSessionNow()
        assertTrue(viewModel.practiceSession.value.isFinished)
    }

    @Test
    fun `verify interactive quiz modes previous navigation numerical answer and review incorrect`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        assertEquals(8, QuizTopic.values().size)

        viewModel.startQuiz(
            topic = QuizTopic.ARITHMETIC_PERCENTAGE,
            count = 5,
            mode = com.example.math.QuizMode.PRACTICE,
            difficulty = PracticeDifficulty.MEDIUM,
            questionType = com.example.math.QuizQuestionType.MCQ
        )

        var session = viewModel.quizSession.value
        assertEquals(com.example.math.QuizState.IN_PROGRESS, session.state)

        // Answer Q1 wrong
        val q0 = session.questions[0]
        val wrongIdx = (q0.correctIndex + 1) % q0.optionsBn.size
        viewModel.selectQuizOption(wrongIdx)
        viewModel.submitQuizAnswer()
        assertEquals(com.example.math.QuizState.ANSWER_SUBMITTED, viewModel.quizSession.value.state)

        // Move to Q2, then test previousQuizQuestion back to Q1
        viewModel.nextQuizQuestion()
        assertEquals(1, viewModel.quizSession.value.currentIndex)
        viewModel.previousQuizQuestion()
        assertEquals(0, viewModel.quizSession.value.currentIndex)
    }

    @Test
    fun `verify Room v2 to v3 migration preserves populated v2 bookmarks quiz_scores user_progress and supports v3 tables`() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val dbName = "migration_v2_to_v3_audit.db"
        val dbFile = app.getDatabasePath(dbName)
        if (dbFile.exists()) dbFile.delete()
        dbFile.parentFile?.mkdirs()

        // 1. Create a realistic populated Room v2 SQLite database fixture on disk
        val sqliteDb = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        try {
            sqliteDb.execSQL(
                "CREATE TABLE IF NOT EXISTS `bookmarks` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `titleBn` TEXT NOT NULL, `subtitleBn` TEXT NOT NULL, `detailBn` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))"
            )
            sqliteDb.execSQL(
                "CREATE TABLE IF NOT EXISTS `quiz_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `topic` TEXT NOT NULL, `topicTitleBn` TEXT NOT NULL, `score` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `percentage` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)"
            )
            sqliteDb.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_quiz_scores_timestamp` ON `quiz_scores` (`timestamp`)"
            )
            sqliteDb.execSQL(
                "CREATE TABLE IF NOT EXISTS `user_progress` (`itemId` TEXT NOT NULL, `isLearned` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`itemId`))"
            )
            sqliteDb.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_user_progress_isLearned` ON `user_progress` (`isLearned`)"
            )

            // Populate v2 bookmarks
            sqliteDb.execSQL(
                "INSERT INTO `bookmarks` (`id`, `category`, `titleBn`, `subtitleBn`, `detailBn`, `timestamp`) VALUES ('table_12', 'TABLE', '১২ এর নামতা', 'গুণ ও নামতা', '১২ × ১ = ১২', 1700000100000)"
            )
            sqliteDb.execSQL(
                "INSERT INTO `bookmarks` (`id`, `category`, `titleBn`, `subtitleBn`, `detailBn`, `timestamp`) VALUES ('prime_97', 'PRIME', 'মৌলিক সংখ্যা ৯৭', '১-১০০ মৌলিক', '২৫তম মৌলিক সংখ্যা', 1700000200000)"
            )
            sqliteDb.execSQL(
                "INSERT INTO `bookmarks` (`id`, `category`, `titleBn`, `subtitleBn`, `detailBn`, `timestamp`) VALUES ('alg_sq_1', 'FORMULA', '(a+b)² সূত্র', 'বীজগণিত', 'a² + 2ab + b²', 1700000300000)"
            )

            // Populate v2 quiz_scores
            sqliteDb.execSQL(
                "INSERT INTO `quiz_scores` (`id`, `topic`, `topicTitleBn`, `score`, `totalQuestions`, `percentage`, `timestamp`) VALUES (1, 'TABLES', 'নামতা কুইজ', 9, 10, 90, 1700000400000)"
            )
            sqliteDb.execSQL(
                "INSERT INTO `quiz_scores` (`id`, `topic`, `topicTitleBn`, `score`, `totalQuestions`, `percentage`, `timestamp`) VALUES (2, 'ALGEBRA', 'বীজগণিত কুইজ', 10, 10, 100, 1700000500000)"
            )

            // Populate v2 user_progress
            sqliteDb.execSQL(
                "INSERT INTO `user_progress` (`itemId`, `isLearned`, `updatedAt`) VALUES ('table_12', 1, 1700000600000)"
            )
            sqliteDb.execSQL(
                "INSERT INTO `user_progress` (`itemId`, `isLearned`, `updatedAt`) VALUES ('prime_97', 1, 1700000700000)"
            )
            sqliteDb.execSQL(
                "INSERT INTO `user_progress` (`itemId`, `isLearned`, `updatedAt`) VALUES ('sq_15', 0, 1700000800000)"
            )

            // Mark database version = 2
            sqliteDb.version = 2
        } finally {
            sqliteDb.close()
        }

        // 2. Open the v2 database with Room v4 and execute real MIGRATION_2_3 + MIGRATION_3_4
        val migratedDb = androidx.room.Room.databaseBuilder(
            app,
            com.example.data.db.MathDatabase::class.java,
            dbName
        )
            .addMigrations(
                com.example.data.db.MathDatabase.MIGRATION_1_2,
                com.example.data.db.MathDatabase.MIGRATION_2_3,
                com.example.data.db.MathDatabase.MIGRATION_3_4
            )
            .allowMainThreadQueries()
            .build()

        try {
            val dao = migratedDb.mathDao()
            val repo = com.example.data.repository.MathRepository(dao)

            // 3. Verify all pre-existing v2 bookmarks survived migration intact
            val bookmarks = dao.getAllBookmarks().first()
            assertEquals(3, bookmarks.size)
            assertTrue(bookmarks.any { it.id == "table_12" && it.titleBn == "১২ এর নামতা" })
            assertTrue(bookmarks.any { it.id == "prime_97" && it.category == "PRIME" })
            assertTrue(bookmarks.any { it.id == "alg_sq_1" && it.detailBn == "a² + 2ab + b²" })

            // 4. Verify all pre-existing v2 quiz scores survived migration intact
            val quizScores = dao.getAllQuizScores().first()
            assertEquals(2, quizScores.size)
            assertEquals("ALGEBRA", quizScores[0].topicId)
            assertEquals(100, quizScores[0].percentage)
            assertEquals("TABLES", quizScores[1].topicId)
            assertEquals(90, quizScores[1].percentage)

            // 5. Verify all pre-existing v2 user progress records survived migration intact
            val progress = dao.getAllProgress().first()
            assertEquals(3, progress.size)
            val table12Prog = progress.first { it.itemId == "table_12" }
            assertTrue(table12Prog.isLearned)
            assertEquals(1700000600000L, table12Prog.updatedAt)

            // 6. Verify new v3 tables (practice_history, topic_analytics, recent_formulas) work & prevent duplicates
            repo.recordSessionAndAnalytics(
                moduleType = "PRACTICE_LAB",
                categoryId = "fractions",
                categoryTitleBn = "ভগ্নাংশ",
                difficulty = "medium",
                mode = "fixed",
                inputFormat = "mcq",
                score = 80,
                correctCount = 8,
                wrongCount = 2,
                totalQuestions = 10,
                accuracyPercent = 80,
                durationSeconds = 45L,
                highestStreak = 5
            )
            repo.recordSessionAndAnalytics(
                moduleType = "PRACTICE_LAB",
                categoryId = "fractions",
                categoryTitleBn = "ভগ্নাংশ",
                difficulty = "medium",
                mode = "fixed",
                inputFormat = "mcq",
                score = 90,
                correctCount = 9,
                wrongCount = 1,
                totalQuestions = 10,
                accuracyPercent = 90,
                durationSeconds = 35L,
                highestStreak = 7
            )

            val historyList = dao.getAllPracticeHistory().first()
            assertEquals(2, historyList.size)
            assertEquals("fractions", historyList[0].topicId)

            // Two sessions on same module/topic/difficulty must aggregate into 1 TopicAnalyticsEntity row
            val analyticsList = dao.getAllTopicAnalytics().first()
            assertEquals(1, analyticsList.size)
            val fracAnalytics = analyticsList.first()
            assertEquals(20, fracAnalytics.totalAttempted)
            assertEquals(17, fracAnalytics.totalCorrect)
            assertEquals(3, fracAnalytics.totalWrong)
            assertEquals(7, fracAnalytics.bestStreak)
            assertEquals(80, fracAnalytics.totalTimeSeconds)

            // Record recent formula twice -> must upsert without duplicate rows
            repo.recordFormulaViewed("lib_alg_1", "(a + b)²", "ALGEBRA")
            repo.recordFormulaViewed("lib_alg_1", "(a + b)²", "ALGEBRA")
            val recentFormulas = dao.getRecentFormulas().first()
            assertEquals(1, recentFormulas.size)
            assertEquals("lib_alg_1", recentFormulas[0].formulaId)
        } finally {
            migratedDb.close()
            if (dbFile.exists()) dbFile.delete()
        }
    }

    @Test
    fun `verify practice lab true false format keypad retry incorrect and tutor bengali input`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // 1. Start Practice Lab in TRUE_FALSE format
        viewModel.startPracticeSession(
            category = PracticeCategory.DIVISIBILITY,
            difficulty = PracticeDifficulty.EASY,
            count = 5,
            inputFormat = com.example.math.QuestionInputFormat.TRUE_FALSE,
            isUnlimitedMode = false
        )
        val tfSession = viewModel.practiceSession.value
        assertEquals(com.example.math.QuestionInputFormat.TRUE_FALSE, tfSession.inputFormat)
        assertEquals(2, tfSession.questions.first().optionsBn.size)
        assertNotNull(tfSession.questions.first().trueFalseStatementBn)

        // 2. Start Practice Lab in KEYPAD format, submit 1 wrong and 1 right, then retry incorrect
        viewModel.startPracticeSession(
            category = PracticeCategory.ARITHMETIC,
            difficulty = PracticeDifficulty.EASY,
            count = 2,
            inputFormat = com.example.math.QuestionInputFormat.KEYPAD,
            isUnlimitedMode = false
        )
        viewModel.setPracticeCustomInput("৯৯৯৯৯৯") // wrong answer
        viewModel.submitPracticeAnswer()
        assertFalse(viewModel.practiceSession.value.isCorrect)
        viewModel.nextPracticeQuestion()

        val q1 = viewModel.practiceSession.value.questions[1]
        viewModel.setPracticeCustomInput(q1.correctAnswerBn)
        viewModel.submitPracticeAnswer()
        assertTrue(viewModel.practiceSession.value.isCorrect)
        assertEquals(1, viewModel.practiceSession.value.incorrectQuestions.size)

        viewModel.retryIncorrectPracticeQuestions()
        assertEquals(1, viewModel.practiceSession.value.questions.size)

        // 3. Verify Tutor accepts Bengali digits
        viewModel.startTutor(5)
        viewModel.setTutorAnswerInput("৫")
        viewModel.checkTutorStep()
        assertEquals(2, viewModel.tutorStep.value)
    }

    @Test
    fun `verify all 9 mental math challenge modes and all 6 interactive quiz modes execute end to end`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // 1. Verify all 9 MentalMathMode entries exist and initialize valid sessions
        val allMentalModes = MentalMathMode.values()
        assertEquals(9, allMentalModes.size)
        allMentalModes.forEach { mode ->
            viewModel.startMentalMathChallenge(
                category = MentalMathCategory.ADDITION,
                difficulty = MentalMathDifficulty.EASY,
                mode = mode,
                durationOrCount = if (mode.isTimed) mode.presetDurationSeconds else mode.presetQuestionCount
            )
            val session = viewModel.mentalMathSession.value
            assertEquals(mode, session.mode)
            assertTrue("Mental math mode $mode must generate questions", session.questions.isNotEmpty())
            assertFalse(session.isFinished)
        }

        // 2. Verify all 6 QuizMode entries exist and initialize valid sessions
        val allQuizModes = com.example.math.QuizMode.values()
        assertEquals(6, allQuizModes.size)
        allQuizModes.forEach { quizMode ->
            viewModel.startQuiz(
                topic = QuizTopic.MULTIPLICATION,
                count = 5,
                mode = quizMode,
                difficulty = PracticeDifficulty.MEDIUM,
                questionType = com.example.math.QuizQuestionType.MCQ
            )
            val quizSession = viewModel.quizSession.value
            assertEquals(quizMode, quizSession.mode)
            assertTrue("Quiz mode $quizMode must generate questions", quizSession.questions.isNotEmpty())
            if (quizMode == com.example.math.QuizMode.MIXED) {
                assertEquals(QuizTopic.MIXED_CHALLENGE, quizSession.topic)
            }
        }
    }

    @Test
    fun `verify full end to end persistence across application restart for bookmark quiz practice formula and customization`() {
        kotlinx.coroutines.runBlocking {
            val app = ApplicationProvider.getApplicationContext<android.app.Application>()
            val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
            val viewModel1 = com.example.ui.MathViewModel(app)

            // 1. Create a bookmark and open a formula
            val formula = FormulaLibraryProvider.allFormulas.first()
            viewModel1.recordFormulaView(formula)
            val bm = com.example.data.model.BookmarkEntity(
                id = formula.id,
                category = "FORMULA_LIBRARY",
                titleBn = formula.titleBn,
                subtitleBn = formula.subject.titleBn,
                detailBn = formula.formulaExpression
            )
            viewModel1.toggleBookmark(bm)

            // 2. Complete one Interactive Quiz (5 questions)
            viewModel1.startQuiz(
                topic = QuizTopic.ALGEBRA,
                count = 5,
                mode = com.example.math.QuizMode.TOPIC,
                difficulty = PracticeDifficulty.EASY,
                questionType = com.example.math.QuizQuestionType.MCQ
            )
            for (i in 0 until 5) {
                val q = viewModel1.quizSession.value.questions[i]
                viewModel1.selectQuizOption(q.correctIndex)
                viewModel1.submitQuizAnswer()
                viewModel1.nextQuizQuestion()
            }
            assertTrue(viewModel1.quizSession.value.isFinished)

            // 3. Complete one Practice Lab session (5 questions)
            viewModel1.startPracticeSession(
                category = PracticeCategory.RATIOS,
                difficulty = PracticeDifficulty.MEDIUM,
                count = 5,
                inputFormat = com.example.math.QuestionInputFormat.MCQ,
                isUnlimitedMode = false
            )
            for (i in 0 until 5) {
                val q = viewModel1.practiceSession.value.questions[i]
                viewModel1.selectPracticeOption(q.correctIndex)
                viewModel1.submitPracticeAnswer()
                viewModel1.nextPracticeQuestion()
            }
            assertTrue(viewModel1.practiceSession.value.isFinished)

            // 4. Change customization preference and theme mode
            viewModel1.setThemeMode(com.example.ui.theme.AppThemeMode.DARK)
            viewModel1.updatePreviewGlobal { it.copy(layoutType = com.example.ui.customization.CustomLayoutType.COMFORTABLE_LIST) }
            viewModel1.saveCustomization()

            // Allow background coroutines to flush to Room SQLite database
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            kotlinx.coroutines.delay(150)

            // 5. Simulate Activity restart and new ViewModel instance
            controller.recreate()
            val viewModel2 = com.example.ui.MathViewModel(app)
            val dao = com.example.data.db.MathDatabase.getDatabase(app).mathDao()

            val savedBookmarks = dao.getAllBookmarks().first()
            assertTrue("Bookmark must persist across restart", savedBookmarks.any { it.id == formula.id })

            val savedQuizScores = dao.getAllQuizScores().first()
            assertTrue("Quiz score must persist across restart", savedQuizScores.any { it.topicId == QuizTopic.ALGEBRA.id && it.percentage == 100 })

            val savedPracticeHistory = dao.getAllPracticeHistory().first()
            assertTrue("Practice session history must persist across restart", savedPracticeHistory.any { it.topicId == PracticeCategory.RATIOS.id })

            val savedAnalytics = dao.getAllTopicAnalytics().first()
            assertTrue("Topic analytics must persist across restart", savedAnalytics.any { it.topicId == PracticeCategory.RATIOS.id })

            val savedRecentFormulas = dao.getRecentFormulas().first()
            assertTrue("Recently viewed formula must persist across restart", savedRecentFormulas.any { it.formulaId == formula.id })

            assertEquals(com.example.ui.theme.AppThemeMode.DARK, viewModel2.themeMode.value)
            assertEquals(
                com.example.ui.customization.CustomLayoutType.COMFORTABLE_LIST,
                viewModel2.customizationState.value.global.layoutType
            )
            controller.pause().stop().destroy()
        }
    }

    @Test
    @Config(sdk = [36], qualifiers = "land")
    fun `verify landscape and all screen destinations compose without error`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        val viewModel = com.example.ui.MathViewModel(app)

        val allScreens = listOf(
            com.example.ui.Screen.Home,
            com.example.ui.Screen.Tables,
            com.example.ui.Screen.TableDetail,
            com.example.ui.Screen.Primes,
            com.example.ui.Screen.SquaresCubes,
            com.example.ui.Screen.Formulas,
            com.example.ui.Screen.FormulaDetail,
            com.example.ui.Screen.FormulaLibrary,
            com.example.ui.Screen.PracticeLab,
            com.example.ui.Screen.PracticeLabPlay,
            com.example.ui.Screen.MentalMath,
            com.example.ui.Screen.MentalMathPlay,
            com.example.ui.Screen.NumberSystem,
            com.example.ui.Screen.NumberSystemPractice,
            com.example.ui.Screen.Quizzes,
            com.example.ui.Screen.QuizPlay,
            com.example.ui.Screen.Bookmarks,
            com.example.ui.Screen.PracticeTutor,
            com.example.ui.Screen.Dashboard,
            com.example.ui.Screen.Customize,
            com.example.ui.Screen.ProblemSolver,
            com.example.ui.Screen.VisualExplorer,
            com.example.ui.Screen.BcsPreparation
        )

        allScreens.forEach { screen ->
            viewModel.navigateTo(screen)
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            assertEquals(screen, viewModel.currentScreen)
        }
        controller.pause().stop().destroy()
    }

    @Test
    fun `verify room migration 3 to 4 preserves v3 data and initializes v4 exam solver and daily goal tables`() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val dbName = "math_master_migration_v3_v4_test.db"
        val dbFile = app.getDatabasePath(dbName)
        if (dbFile.exists()) dbFile.delete()
        dbFile.parentFile?.mkdirs()

        val sqliteDb = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        try {
            sqliteDb.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `titleBn` TEXT NOT NULL, `subtitleBn` TEXT NOT NULL, `detailBn` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            sqliteDb.execSQL("CREATE TABLE IF NOT EXISTS `quiz_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `topic` TEXT NOT NULL, `topicTitleBn` TEXT NOT NULL, `score` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `percentage` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)")
            sqliteDb.execSQL("CREATE INDEX IF NOT EXISTS `index_quiz_scores_timestamp` ON `quiz_scores` (`timestamp`)")
            sqliteDb.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (`itemId` TEXT NOT NULL, `isLearned` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`itemId`))")
            sqliteDb.execSQL("CREATE INDEX IF NOT EXISTS `index_user_progress_isLearned` ON `user_progress` (`isLearned`)")
            sqliteDb.execSQL("CREATE TABLE IF NOT EXISTS `practice_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `module` TEXT NOT NULL, `topicId` TEXT NOT NULL, `topicTitleBn` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `mode` TEXT NOT NULL, `score` INTEGER NOT NULL, `correctCount` INTEGER NOT NULL, `wrongCount` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `accuracyPercent` INTEGER NOT NULL, `bestStreak` INTEGER NOT NULL, `durationSeconds` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)")
            sqliteDb.execSQL("CREATE INDEX IF NOT EXISTS `index_practice_history_module` ON `practice_history` (`module`)")
            sqliteDb.execSQL("CREATE INDEX IF NOT EXISTS `index_practice_history_timestamp` ON `practice_history` (`timestamp`)")
            sqliteDb.execSQL("CREATE TABLE IF NOT EXISTS `topic_analytics` (`analyticsKey` TEXT NOT NULL, `module` TEXT NOT NULL, `topicId` TEXT NOT NULL, `topicTitleBn` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `totalAttempted` INTEGER NOT NULL, `totalCorrect` INTEGER NOT NULL, `totalWrong` INTEGER NOT NULL, `bestStreak` INTEGER NOT NULL, `totalTimeSeconds` INTEGER NOT NULL, `lastPracticedAt` INTEGER NOT NULL, PRIMARY KEY(`analyticsKey`))")
            sqliteDb.execSQL("CREATE INDEX IF NOT EXISTS `index_topic_analytics_module_topicId` ON `topic_analytics` (`module`, `topicId`)")
            sqliteDb.execSQL("CREATE TABLE IF NOT EXISTS `recent_formulas` (`formulaId` TEXT NOT NULL, `titleBn` TEXT NOT NULL, `subjectId` TEXT NOT NULL, `viewedAt` INTEGER NOT NULL, PRIMARY KEY(`formulaId`))")
            sqliteDb.execSQL("CREATE INDEX IF NOT EXISTS `index_recent_formulas_viewedAt` ON `recent_formulas` (`viewedAt`)")

            sqliteDb.execSQL("INSERT INTO `practice_history` (`id`, `module`, `topicId`, `topicTitleBn`, `difficulty`, `mode`, `score`, `correctCount`, `wrongCount`, `totalQuestions`, `accuracyPercent`, `bestStreak`, `durationSeconds`, `timestamp`) VALUES (1, 'PRACTICE_LAB', 'algebra', 'বীজগণিত', 'medium', 'fixed:mcq', 90, 9, 1, 10, 90, 6, 50, 1700001000000)")
            sqliteDb.version = 3
        } finally {
            sqliteDb.close()
        }

        val migratedDb = androidx.room.Room.databaseBuilder(
            app,
            com.example.data.db.MathDatabase::class.java,
            dbName
        )
            .addMigrations(com.example.data.db.MathDatabase.MIGRATION_3_4)
            .allowMainThreadQueries()
            .build()

        try {
            val dao = migratedDb.mathDao()
            val repo = com.example.data.repository.MathRepository(dao)

            // Verify v3 practice_history survived
            val history = dao.getAllPracticeHistory().first()
            assertEquals(1, history.size)
            assertEquals("algebra", history.first().topicId)

            // Verify v4 exam_attempts CRUD
            val attempt = com.example.data.model.ExamAttemptEntity(
                attemptId = "exam_v4_test_1",
                examTitleBn = "বিসিএস প্রিলিমিনারি মক",
                examType = "BCS_PRELIMINARY_MOCK",
                selectedTopicsCsv = "REAL_NUMBERS,ALGEBRA",
                difficulty = "medium",
                questionType = "MCQ",
                totalQuestions = 15,
                durationLimitSeconds = 1200,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.5,
                status = "COMPLETED",
                currentQuestionIndex = 14,
                elapsedSeconds = 540,
                remainingSeconds = 660,
                questionsJson = "",
                answersJson = "",
                markedForReviewCsv = "2",
                visitedIndicesCsv = "0,1,2",
                questionTimeSecondsJson = "",
                correctCount = 10,
                wrongCount = 2,
                unansweredCount = 3,
                earnedMarks = 10.0,
                negativeMarksDeducted = 1.0,
                finalScore = 9.0,
                percentage = 60.0,
                accuracyPercent = 83.33,
                startedAt = 1700002000000L,
                updatedAt = 1700002540000L,
                completedAt = 1700002540000L
            )
            repo.saveExamAttemptState(attempt)
            val savedAttempts = dao.getCompletedExamAttempts().first()
            assertEquals(1, savedAttempts.size)
            assertEquals("BCS_PRELIMINARY_MOCK", savedAttempts.first().examType)
            assertEquals(9.0, savedAttempts.first().finalScore, 0.001)

            // Verify v4 solver_history CRUD
            repo.saveSolvedProblemHistory(
                com.example.data.model.SolverHistoryEntity(
                    topicId = "linear_equations",
                    topicTitleBn = "সরল সমীকরণ ও বীজগণিত",
                    categoryTitleBn = "সমীকরণ সমাধান",
                    inputSummaryBn = "2x + 5 = 15",
                    inputsJson = "eq=2x+5=15",
                    finalAnswerBn = "x = ৫",
                    stepsJson = "১. 2x = 10||২. x = 5",
                    formulaUsedBn = "ax + b = c",
                    verificationBn = "2(5)+5 = 15"
                )
            )
            val solverItems = dao.getAllSolverHistory().first()
            assertEquals(1, solverItems.size)
            assertEquals("x = ৫", solverItems.first().finalAnswerBn)

            // Verify v4 daily_study_activity CRUD
            repo.recordDailyStudyDelta(
                questionsAttemptedDelta = 15,
                correctDelta = 12,
                wrongDelta = 3,
                studyTimeSecondsDelta = 300,
                examsCompletedDelta = 1,
                practiceSessionsCompletedDelta = 0
            )
            val dailyList = dao.getAllDailyStudyActivity().first()
            assertEquals(1, dailyList.size)
            assertEquals(15, dailyList.first().questionsAttempted)
        } finally {
            migratedDb.close()
            if (dbFile.exists()) dbFile.delete()
        }
    }

    @Test
    fun `verify exam scoring engine positive negative marking unanswered clamping and 5-state palette`() {
        val questions = com.example.math.ExamQuestionBank.generateExamQuestions(
            config = com.example.math.ExamConfig(
                preset = com.example.math.ExamPresetType.BCS_PRELIMINARY_MOCK,
                selectedTopics = setOf(QuizTopic.ALGEBRA, QuizTopic.ARITHMETIC_PERCENTAGE),
                questionCount = 5,
                negativeMarksPerWrong = 0.25
            )
        )
        assertEquals(5, questions.size)

        // Simulate: Q0 correct, Q1 wrong, Q2 wrong + marked, Q3 correct + marked, Q4 unanswered
        val wrongIdx1 = (questions[1].correctIndex + 1) % questions[1].optionsBn.size
        val wrongIdx2 = (questions[2].correctIndex + 1) % questions[2].optionsBn.size

        val selectedAnswers = mapOf(
            0 to questions[0].correctIndex,
            1 to wrongIdx1,
            2 to wrongIdx2,
            3 to questions[3].correctIndex
        )
        val markedIndices = setOf(2, 3)
        val visitedIndices = setOf(0, 1, 2, 3)

        // Verify 5-state QuestionPaletteState
        val state0 = com.example.math.ExamScoringEngine.computePaletteState(0, visitedIndices, selectedAnswers, markedIndices)
        val state1 = com.example.math.ExamScoringEngine.computePaletteState(1, visitedIndices, selectedAnswers, markedIndices)
        val state2 = com.example.math.ExamScoringEngine.computePaletteState(2, visitedIndices, selectedAnswers, markedIndices)
        val state4 = com.example.math.ExamScoringEngine.computePaletteState(4, visitedIndices, selectedAnswers, markedIndices)
        assertEquals(com.example.math.QuestionPaletteState.ANSWERED, state0)
        assertEquals(com.example.math.QuestionPaletteState.ANSWERED, state1)
        assertEquals(com.example.math.QuestionPaletteState.ANSWERED_AND_MARKED, state2)
        assertEquals(com.example.math.QuestionPaletteState.NOT_VISITED, state4)

        val result = com.example.math.ExamScoringEngine.evaluateExam(
            attemptId = "test_attempt_1",
            examTitleBn = "বিসিএস মক",
            examType = "BCS_PRELIMINARY_MOCK",
            questions = questions,
            selectedAnswers = selectedAnswers,
            markedForReview = markedIndices,
            questionTimeSeconds = mapOf(0 to 12, 1 to 15, 2 to 20, 3 to 10),
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = 0.25,
            timeSpentSeconds = 57,
            durationLimitSeconds = 600
        )

        assertEquals(5, result.totalQuestions)
        assertEquals(4, result.attemptedCount)
        assertEquals(2, result.correctCount)
        assertEquals(2, result.wrongCount)
        assertEquals(1, result.unansweredCount)
        assertEquals(2, result.markedForReviewCount)
        // Raw marks: 2 * 1.0 - 2 * 0.25 = 1.50
        assertEquals(1.50, result.finalScore, 0.001)
        assertEquals(5.0, result.totalPossibleMarks, 0.001)
        assertEquals(30.0, result.percentage, 0.01) // 1.5 / 5.0 = 30%
        assertEquals(50.0, result.accuracyPercent, 0.01) // 2 correct out of 4 answered = 50%

        // Verify serialization round-trip
        val serializedQ = com.example.math.ExamQuestionBank.serializeQuestions(questions)
        val restoredQ = com.example.math.ExamQuestionBank.deserializeQuestions(serializedQ)
        assertEquals(5, restoredQ.size)
        assertEquals(questions[0].questionBn, restoredQ[0].questionBn)
    }

    @Test
    fun `verify examination center full lifecycle palette navigation review marking clear answer and confirmation`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        viewModel.startConfiguredExam(
            com.example.math.ExamConfig(
                preset = com.example.math.ExamPresetType.TIMED_MOCK,
                selectedTopics = setOf(QuizTopic.ALGEBRA),
                questionCount = 5,
                durationMinutes = 10,
                negativeMarksPerWrong = 0.25
            )
        )

        val s0 = viewModel.quizSession.value
        assertEquals(5, s0.questions.size)
        assertEquals(0.25, s0.examConfig.negativeMarksPerWrong, 0.001)
        assertTrue(s0.visitedIndices.contains(0))

        // Select answer on Q0, mark for review, then clear answer
        viewModel.selectQuizOption(s0.questions[0].correctIndex)
        assertTrue(viewModel.quizSession.value.userAnswers.containsKey(0))
        viewModel.toggleMarkCurrentQuestionForReview()
        assertTrue(viewModel.quizSession.value.markedForReviewIndices.contains(0))
        viewModel.clearCurrentQuizAnswer()
        assertFalse(viewModel.quizSession.value.userAnswers.containsKey(0))

        // Re-select correct answer on Q0 and jump to Q3 via palette
        viewModel.selectQuizOption(s0.questions[0].correctIndex)
        viewModel.jumpToQuizQuestion(3)
        assertEquals(3, viewModel.quizSession.value.currentIndex)
        assertTrue(viewModel.quizSession.value.visitedIndices.contains(3))

        // Request pre-submit confirmation dialog and finalize exam
        viewModel.requestExamSubmitConfirmation()
        assertTrue(viewModel.quizSession.value.showSubmitConfirmDialog)
        viewModel.submitFullExamNow()

        val finished = viewModel.quizSession.value
        assertTrue(finished.isFinished)
        assertNotNull(finished.detailedResult)
        assertEquals(1, finished.detailedResult!!.correctCount)
        assertEquals(4, finished.detailedResult!!.unansweredCount)
    }

    @Test
    fun `verify learning analytics engine separated kpis confidence weighted topic mastery and daily goals`() {
        val now = 1750000000000L

        val examAttempts = listOf(
            com.example.data.model.ExamAttemptEntity(
                attemptId = "ex1",
                examTitleBn = "টাইমড মক",
                examType = "TIMED_MOCK",
                selectedTopicsCsv = "ALGEBRA",
                difficulty = "medium",
                questionType = "MCQ",
                totalQuestions = 10,
                durationLimitSeconds = 600,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.25,
                status = "COMPLETED",
                currentQuestionIndex = 9,
                elapsedSeconds = 300,
                remainingSeconds = 300,
                questionsJson = "",
                answersJson = "",
                markedForReviewCsv = "",
                visitedIndicesCsv = "",
                questionTimeSecondsJson = "",
                correctCount = 8,
                wrongCount = 2,
                unansweredCount = 0,
                earnedMarks = 8.0,
                negativeMarksDeducted = 0.5,
                finalScore = 7.5,
                percentage = 75.0,
                accuracyPercent = 80.0,
                startedAt = now - 600_000L,
                updatedAt = now - 300_000L,
                completedAt = now - 300_000L
            )
        )

        val practiceHistory = listOf(
            // One QUIZ record in practiceHistory (must NOT be double-counted into practice KPIs)
            com.example.data.model.PracticeHistoryEntity(
                id = 1,
                module = "QUIZ",
                topicId = "ALGEBRA",
                topicTitleBn = "বীজগণিত",
                difficulty = "medium",
                mode = "TIMED_MOCK",
                score = 75,
                correctCount = 8,
                wrongCount = 2,
                totalQuestions = 10,
                accuracyPercent = 80,
                bestStreak = 5,
                durationSeconds = 300,
                timestamp = now - 300_000L
            ),
            // One non-exam PRACTICE_LAB record
            com.example.data.model.PracticeHistoryEntity(
                id = 2,
                module = "PRACTICE_LAB",
                topicId = "fractions",
                topicTitleBn = "ভগ্নাংশ",
                difficulty = "medium",
                mode = "fixed",
                score = 90,
                correctCount = 9,
                wrongCount = 1,
                totalQuestions = 10,
                accuracyPercent = 90,
                bestStreak = 7,
                durationSeconds = 120,
                timestamp = now - 100_000L
            )
        )

        val topicAnalytics = listOf(
            // Topic with 3 questions at 100% accuracy -> MUST be NEEDS_MORE_DATA (never MASTERED)
            com.example.data.model.TopicAnalyticsEntity(
                analyticsKey = "PRACTICE_LAB_geometry_easy",
                module = "PRACTICE_LAB",
                topicId = "geometry",
                topicTitleBn = "জ্যামিতি",
                difficulty = "easy",
                totalAttempted = 3,
                totalCorrect = 3,
                totalWrong = 0,
                bestStreak = 3,
                totalTimeSeconds = 30,
                lastPracticedAt = now
            ),
            // Topic with 20 questions at 90% accuracy -> MASTERED
            com.example.data.model.TopicAnalyticsEntity(
                analyticsKey = "PRACTICE_LAB_algebra_medium",
                module = "PRACTICE_LAB",
                topicId = "algebra",
                topicTitleBn = "বীজগণিত",
                difficulty = "medium",
                totalAttempted = 20,
                totalCorrect = 18,
                totalWrong = 2,
                bestStreak = 10,
                totalTimeSeconds = 200,
                lastPracticedAt = now
            )
        )

        val todayKey = com.example.data.repository.MathRepository.todayDateKey(now)
        val yesterdayKey = com.example.data.repository.MathRepository.todayDateKey(now - 86_400_000L)
        val dailyActivities = listOf(
            com.example.data.model.DailyStudyActivityEntity(
                dateKey = todayKey,
                questionsAttempted = 25,
                correctCount = 20,
                wrongCount = 5,
                studyTimeSeconds = 950,
                examsCompleted = 1,
                practiceSessionsCompleted = 1,
                dailyTargetQuestions = 20,
                dailyTargetMinutes = 15,
                updatedAt = now
            ),
            com.example.data.model.DailyStudyActivityEntity(
                dateKey = yesterdayKey,
                questionsAttempted = 22,
                correctCount = 18,
                wrongCount = 4,
                studyTimeSeconds = 600,
                examsCompleted = 0,
                practiceSessionsCompleted = 2,
                dailyTargetQuestions = 20,
                dailyTargetMinutes = 15,
                updatedAt = now - 86_400_000L
            )
        )

        val dashboardData = com.example.math.LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = com.example.math.AnalyticsTimeFilter.ALL_TIME,
            completedExams = examAttempts,
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = practiceHistory,
            topicAnalytics = topicAnalytics,
            progressItems = emptyList(),
            dailyActivities = dailyActivities,
            nowMs = now
        )

        // 1. Verify Separated KPIs (no double counting between Exam and Practice)
        assertEquals(1, dashboardData.kpiSummary.totalExamsCompleted)
        assertEquals(10, dashboardData.kpiSummary.examQuestionsAttempted)
        assertEquals(1, dashboardData.kpiSummary.totalPracticeSessions)
        assertEquals(10, dashboardData.kpiSummary.practiceQuestionsAttempted)
        assertEquals(20, dashboardData.kpiSummary.combinedQuestionsAttempted)

        // 2. Verify Confidence-Weighted Topic Mastery NEVER classifies 1..4 questions as MASTERED
        val geometryAssessment = dashboardData.topicMasteryList.first { it.topicId == "geometry" }
        assertEquals(com.example.math.TopicMasteryStatus.NEEDS_MORE_DATA, geometryAssessment.masteryStatus)
        assertEquals(com.example.math.SampleConfidenceLevel.INSUFFICIENT_DATA, geometryAssessment.confidenceLevel)

        val algebraAssessment = dashboardData.topicMasteryList.first { it.topicId == "algebra" }
        assertEquals(com.example.math.TopicMasteryStatus.MASTERED, algebraAssessment.masteryStatus)
        assertEquals(com.example.math.SampleConfidenceLevel.HIGH_CONFIDENCE, algebraAssessment.confidenceLevel)

        // 3. Verify Daily Goal Progress & Streak calculation
        assertTrue(dashboardData.goalProgress.isTodayGoalMet)
        assertEquals(2, dashboardData.goalProgress.currentStreakDays)
        assertEquals(7, dashboardData.goalProgress.last7DaysActivity.size)
    }

    @Test
    fun `verify all 5 priority 3 secondary module upgrades formula calculator solver history visual checks real numbers and bcs mode`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(app)

        // 1. Formula Library Interactive Variable Calculator & Mini-Quiz
        val firstFormula = FormulaLibraryProvider.allFormulas.first() // (a + b)^2
        val specs = com.example.math.FormulaInteractiveEngine.getVariableSpecsForFormula(firstFormula)
        val calcInputs = specs.associate { it.symbol to it.defaultValue }
        val calcResult = com.example.math.FormulaInteractiveEngine.evaluateFormula(firstFormula, calcInputs)
        assertTrue(calcResult.isSuccess)
        assertTrue(calcResult.finalAnswerBn.isNotBlank())

        val miniQuizList = com.example.math.FormulaInteractiveEngine.generateMiniQuizForFormula(firstFormula)
        assertTrue(miniQuizList.isNotEmpty())
        assertTrue(miniQuizList.first().optionsBn.size >= 2)
        assertTrue(miniQuizList.first().correctIndex in miniQuizList.first().optionsBn.indices)

        // 2. Problem Solver History Persistence & Similar Problem Generator (Easy/Medium/Hard)
        val similarProblems = com.example.math.SimilarProblemGenerator.generateSimilarProblems(
            com.example.math.SolverTopic.LINEAR_EQUATIONS
        )
        assertEquals(3, similarProblems.size)
        similarProblems.forEach { sim ->
            assertTrue(sim.problemStatementBn.isNotBlank())
            assertTrue(sim.solution.finalAnswerBn.isNotBlank())
            assertTrue(sim.solution.steps.isNotEmpty())
        }

        // 3. Visual Explainer Mini Knowledge Checks across tabs
        for (tabIdx in 0..3) {
            val checks = com.example.math.VisualKnowledgeCheckProvider.getKnowledgeChecksForTab(tabIdx)
            assertTrue(checks.isNotEmpty())
            assertEquals(4, checks.first().optionsBn.size)
            assertTrue(checks.first().correctIndex in 0..3)
        }

        // 4. Real Numbers Guided Step-by-Step Number Explorer & Classification Exercises
        assertEquals(6, com.example.math.RealNumberGuidedExplorer.guidedSteps.size)
        assertTrue(com.example.math.RealNumberGuidedExplorer.classificationExercises.isNotEmpty())

        // 5. BCS Mathematics Preparation Mode (Official 50th BCS 20-mark syllabus, 5 units x 4 marks, Verified Provenance, Mock Exam)
        val bcsUnits = com.example.math.BcsSyllabusUnit.values()
        assertEquals(5, bcsUnits.size)
        assertEquals(20, com.example.math.BcsPreparationRepository.totalOfficialMarks)
        assertEquals(
            com.example.math.BcsSyllabusVersion.BCS_50TH_CURRENT,
            com.example.math.BcsPreparationRepository.defaultSyllabusVersion
        )
        assertTrue(bcsUnits.all {
            it.isOfficialBpscSyllabus &&
                it.officialMarks == 4 &&
                it.allocatedMarksBn.contains("৪") &&
                it.marksForVersion(com.example.math.BcsSyllabusVersion.BCS_50TH_CURRENT) == 4 &&
                it.marksForVersion(com.example.math.BcsSyllabusVersion.BCS_LEGACY_15) == 3
        })

        val allBcsQuestions = com.example.math.BcsPreparationRepository.allBcsQuestions
        assertTrue(allBcsQuestions.any { it.sourceType == com.example.math.BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS })
        assertTrue(allBcsQuestions.any { it.sourceType == com.example.math.BcsQuestionSourceType.SYLLABUS_PRACTICE })
        allBcsQuestions.forEach { q ->
            assertTrue(q.shortcutTechniqueBn.isNotBlank())
            assertTrue(q.standardStepByStepBn.isNotEmpty())
            assertEquals(4, q.optionsBn.size)
            assertEquals(4, q.optionsBn.distinct().size)
            assertTrue(q.correctIndex in 0..3)
        }

        // Default 50th BCS Mock Exam: 20 questions / 20 marks
        viewModel.startBcsMockExam(negativeMarking = 0.50)
        assertEquals(com.example.ui.Screen.QuizPlay, viewModel.currentScreen)
        assertEquals(20, viewModel.quizSession.value.questions.size)
        assertEquals(0.50, viewModel.quizSession.value.examConfig.negativeMarksPerWrong, 0.001)
    }

    @Test
    fun test50thBcsSyllabusVersionSwitchingAndProvenanceAudit() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MathViewModel(context)

        // Verify default syllabus version is 50th BCS (20 marks, 4 marks per unit)
        assertEquals(
            com.example.math.BcsSyllabusVersion.BCS_50TH_CURRENT,
            viewModel.selectedBcsSyllabusVersion.value
        )
        assertEquals(20, viewModel.selectedBcsSyllabusVersion.value.totalMarks)
        assertEquals(4, viewModel.selectedBcsSyllabusVersion.value.marksPerUnit)
        assertEquals(20, com.example.math.ExamPresetType.BCS_PRELIMINARY_MOCK.defaultQuestions)

        val currentAnalysis = com.example.math.BcsPreparationRepository.analyzeBcsWeakUnits(
            emptyList(),
            com.example.math.BcsSyllabusVersion.BCS_50TH_CURRENT
        )
        assertEquals(5, currentAnalysis.size)
        assertEquals(20, currentAnalysis.sumOf { it.officialMarks })
        assertTrue(currentAnalysis.all { it.officialMarks == 4 && it.allocatedMarksBn == "৪ নম্বর" })

        // Switch to Legacy 15-mark syllabus (archived 35th-49th BCS)
        viewModel.setBcsSyllabusVersion(com.example.math.BcsSyllabusVersion.BCS_LEGACY_15)
        assertEquals(
            com.example.math.BcsSyllabusVersion.BCS_LEGACY_15,
            viewModel.selectedBcsSyllabusVersion.value
        )
        assertEquals(15, viewModel.selectedBcsSyllabusVersion.value.totalMarks)
        assertEquals(3, viewModel.selectedBcsSyllabusVersion.value.marksPerUnit)

        val legacyAnalysis = com.example.math.BcsPreparationRepository.analyzeBcsWeakUnits(
            emptyList(),
            com.example.math.BcsSyllabusVersion.BCS_LEGACY_15
        )
        assertEquals(15, legacyAnalysis.sumOf { it.officialMarks })
        assertTrue(legacyAnalysis.all { it.officialMarks == 3 && it.allocatedMarksBn == "৩ নম্বর" })

        viewModel.startBcsMockExam()
        assertEquals(15, viewModel.quizSession.value.questions.size)

        // Restore default 50th BCS syllabus
        viewModel.setBcsSyllabusVersion(com.example.math.BcsSyllabusVersion.BCS_50TH_CURRENT)
        viewModel.startBcsMockExam()
        assertEquals(20, viewModel.quizSession.value.questions.size)

        // Verify Provenance Audit Log, Unique Question IDs & Question Bank Authenticity
        val verifiedQuestions = com.example.math.BcsPreparationRepository.verifiedPreviousBcsQuestions
        val practiceQuestions = com.example.math.BcsPreparationRepository.syllabusAlignedPracticeQuestions
        val excludedQuestions = com.example.math.BcsPreparationRepository.excludedUnverifiedQuestions
        val allBcsQuestions = com.example.math.BcsPreparationRepository.allBcsQuestions

        // Reconciled Question Counts: 22 Verified Historical + 18 Syllabus Practice + 0 Excluded = 40 Total
        assertEquals(22, verifiedQuestions.size)
        assertEquals(18, practiceQuestions.size)
        assertEquals(0, excludedQuestions.size)
        assertEquals(40, allBcsQuestions.size)
        assertEquals(
            allBcsQuestions.size,
            verifiedQuestions.size + practiceQuestions.size + excludedQuestions.size
        )

        // Global Question ID Uniqueness & Non-Collision Check
        val allIds = allBcsQuestions.map { it.id }
        assertEquals("Every active BCS question ID must be globally unique", allIds.size, allIds.distinct().size)

        val verifiedIdSet = verifiedQuestions.map { it.id }.toSet()
        val practiceIdSet = practiceQuestions.map { it.id }.toSet()
        assertTrue(
            "Verified historical IDs and syllabus practice IDs must have zero intersection",
            verifiedIdSet.intersect(practiceIdSet).isEmpty()
        )

        // Verify canonical ID naming & full multi-source provenance metadata on all 22 historical questions
        verifiedQuestions.forEach { q ->
            assertEquals(com.example.math.BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS, q.sourceType)
            assertEquals(com.example.math.BcsVerificationStatus.VERIFIED_HISTORICAL, q.verificationStatus)
            assertTrue("bcsExamNumber must be present for ${q.id}", q.bcsExamNumber != null && q.bcsExamNumber!! in 10..46)
            assertTrue("examYear must be present for ${q.id}", q.examYear != null && q.examYear!! in 1989..2025)
            assertTrue(
                "Historical question ID (${q.id}) must canonically match its bcsExamNumber (${q.bcsExamNumber})",
                q.id.startsWith("bcs_hist_${q.bcsExamNumber}_")
            )
            assertTrue("sourceTitleBn must be present for ${q.id}", q.sourceTitleBn.isNotBlank())
            assertTrue("questionPaperRefBn must be present for ${q.id}", q.questionPaperRefBn.isNotBlank())
            assertTrue("provenanceEvidenceBn must be present for ${q.id}", q.provenanceEvidenceBn.isNotBlank())
        }

        // Verify all 18 syllabus practice questions have canonical bcs_prac_u* IDs and no fabricated BCS numbers
        practiceQuestions.forEach { q ->
            assertEquals(com.example.math.BcsQuestionSourceType.SYLLABUS_PRACTICE, q.sourceType)
            assertEquals(com.example.math.BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL, q.verificationStatus)
            assertTrue(
                "Practice question ID (${q.id}) must start with bcs_prac_u${q.unit.ordinal + 1}_",
                q.id.startsWith("bcs_prac_u${q.unit.ordinal + 1}_")
            )
            assertEquals(null, q.bcsExamNumber)
            assertEquals(null, q.examYear)
        }

        // Verify Provenance Audit Log Integrity (20 audited records, 1-to-1 mapping, legacy alias resolution)
        val auditLog = com.example.math.BcsPreparationRepository.provenanceAuditLog
        assertEquals(20, auditLog.size)
        assertEquals(1, auditLog.count { it.auditAction == "VERIFIED_RETAINED" })
        assertEquals(6, auditLog.count { it.auditAction == "PROVENANCE_CORRECTED" })
        assertEquals(13, auditLog.count { it.auditAction == "RECLASSIFIED_TO_PRACTICE" })

        val auditOriginalIds = auditLog.map { it.originalQuestionId }
        val auditResolvedIds = auditLog.map { it.resolvedQuestionId }
        assertEquals("Audit log originalQuestionIds must be unique", auditOriginalIds.size, auditOriginalIds.distinct().size)
        assertEquals("Audit log resolvedQuestionIds must be unique", auditResolvedIds.size, auditResolvedIds.distinct().size)

        // Specifically verify bcs_prev_44_arith_1 and bcs_prev_44_alg_1 do not collide
        val arithAudit = auditLog.first { it.originalQuestionId == "bcs_prev_44_arith_1" }
        val algAudit = auditLog.first { it.originalQuestionId == "bcs_prev_44_alg_1" }
        assertEquals("bcs_prac_u1_simple_interest_double", arithAudit.resolvedQuestionId)
        assertEquals(com.example.math.BcsQuestionSourceType.SYLLABUS_PRACTICE, arithAudit.finalSourceType)
        assertEquals("bcs_hist_12_alg_factor_2x2", algAudit.resolvedQuestionId)
        assertEquals(com.example.math.BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS, algAudit.finalSourceType)

        auditLog.forEach { entry ->
            val targetQuestion = allBcsQuestions.firstOrNull { it.id == entry.resolvedQuestionId }
            assertTrue("Resolved ID ${entry.resolvedQuestionId} must exist in allBcsQuestions", targetQuestion != null)
            assertEquals(entry.finalSourceType, targetQuestion!!.sourceType)
            assertEquals(entry.finalVerificationStatus, targetQuestion.verificationStatus)

            val resolvedByAlias = com.example.math.BcsPreparationRepository.findQuestionByIdOrAlias(entry.originalQuestionId)
            assertEquals(
                "Legacy ID ${entry.originalQuestionId} must resolve to ${entry.resolvedQuestionId}",
                entry.resolvedQuestionId,
                resolvedByAlias?.id
            )
        }

        // Verify zero historical leakage when includePreviousBcs = false (Practice-Only Pool Isolation)
        val practiceOnlyExamQuestions = com.example.math.BcsPreparationRepository.generateBcsExamQuestions(
            unit = null,
            count = 15,
            includePreviousBcs = false,
            syllabusVersion = com.example.math.BcsSyllabusVersion.BCS_50TH_CURRENT
        )
        assertEquals(15, practiceOnlyExamQuestions.size)
        assertTrue(
            "Practice-only pool must not contain any historical BCS questions",
            practiceOnlyExamQuestions.none { q ->
                q.explanationBn.contains(com.example.math.BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS.badgeBn)
            }
        )

        // Verify Negative Marking Configuration Labels & Categories
        val negOptions = com.example.math.BcsPreparationRepository.negativeMarkingOptions
        assertEquals(3, negOptions.size)
        assertTrue("No negative marking option may claim to be an official 50th BCS syllabus rule", negOptions.none { it.isOfficial50thSyllabusRule })
        val zeroOpt = negOptions.first { it.penaltyPerWrong == 0.0 }
        val quarterOpt = negOptions.first { it.penaltyPerWrong == 0.25 }
        val halfOpt = negOptions.first { it.penaltyPerWrong == 0.50 }
        assertEquals(com.example.math.BcsNegativeMarkingCategory.CUSTOM_PRACTICE_DEFAULT, zeroOpt.category)
        assertEquals(com.example.math.BcsNegativeMarkingCategory.CUSTOM_PRACTICE_PENALTY, quarterOpt.category)
        assertEquals(com.example.math.BcsNegativeMarkingCategory.HISTORICAL_BCS_REFERENCE, halfOpt.category)
        assertTrue(quarterOpt.labelBn.contains("কাস্টম"))
        assertTrue(halfOpt.labelBn.contains("ঐতিহাসিক বিসিএস রেফারেন্স"))
    }
}

