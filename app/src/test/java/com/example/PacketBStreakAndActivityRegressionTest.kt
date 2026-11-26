package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.math.AnalyticsTimeFilter
import com.example.math.BcsExamProfile
import com.example.math.BcsPreparationStage
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.LearningAnalyticsEngine
import com.example.math.MonotonicClock
import com.example.math.PracticeCategory
import com.example.math.PracticeDifficulty
import com.example.math.QuestionInputFormat
import com.example.math.QuizMode
import com.example.math.QuizQuestionType
import com.example.math.QuizTopic
import com.example.ui.MathViewModel
import com.example.ui.Screen
import kotlinx.coroutines.Dispatchers
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
import java.util.TimeZone

/**
 * Packet B / Task B-01 regression suite:
 * Verifies Study Streak initialization, genuine-activity qualification,
 * same-day accumulation, consecutive-day increments, missed-day reset,
 * process/ViewModel recreation idempotency, and longest-streak preservation.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PacketBStreakAndActivityRegressionTest {

    private class FakeMonotonicClock(var nowMs: Long = 100_000L) : MonotonicClock {
        override fun elapsedRealtime(): Long = nowMs
    }

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var originalTimeZone: TimeZone
    private var fakeDateKey: String = "2026-11-26"

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Dhaka"))
        fakeDateKey = "2026-11-26"
        MathViewModel.defaultDateKeyProviderForTesting = { fakeDateKey }
        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = true
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        db = MathDatabase.getInstance(app)
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        MathViewModel.defaultDateKeyProviderForTesting = null
        MathViewModel.disableAutoBcsGoalCreditStartupSweepForTesting = false
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        TimeZone.setDefault(originalTimeZone)
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun createFreshViewModel(clock: FakeMonotonicClock = FakeMonotonicClock()): MathViewModel {
        val vm = MathViewModel(
            application = app,
            clock = clock,
            savedStateHandle = SavedStateHandle()
        )
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        return vm
    }

    private suspend fun awaitCondition(maxAttempts: Int = 80, predicate: suspend () -> Boolean) {
        repeat(maxAttempts) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (predicate()) return
            Thread.sleep(15)
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private suspend fun completeSinglePracticeSession(
        vm: MathViewModel,
        dateKey: String = fakeDateKey,
        answerCorrectly: Boolean = true
    ) {
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val beforeSessions = db.mathDao().getDailyStudyActivityByDate(dateKey)?.practiceSessionsCompleted ?: 0
        val beforeAct = prefs.getInt("act_$dateKey", 0)
        val expectedDelta = if (answerCorrectly) 5 else 4
        vm.startPracticeSession(
            category = PracticeCategory.ARITHMETIC,
            difficulty = PracticeDifficulty.EASY,
            count = 1,
            inputFormat = QuestionInputFormat.MCQ,
            isUnlimitedMode = false
        )
        val q = vm.practiceSession.value.questions.first()
        val chosenIdx = if (answerCorrectly) q.correctIndex else (q.correctIndex + 1) % q.optionsBn.size
        vm.selectPracticeOption(chosenIdx)
        vm.submitPracticeAnswer()
        vm.finishPracticeSessionNow()
        awaitCondition {
            val row = db.mathDao().getDailyStudyActivityByDate(dateKey)
            val act = prefs.getInt("act_$dateKey", 0)
            (row?.practiceSessionsCompleted ?: 0) >= beforeSessions + 1 &&
                act >= beforeAct + expectedDelta
        }
    }

    private fun completeSingleQuizSession(vm: MathViewModel) {
        vm.startQuiz(
            topic = QuizTopic.ALGEBRA,
            count = 1,
            mode = QuizMode.TOPIC,
            difficulty = PracticeDifficulty.EASY,
            questionType = QuizQuestionType.MCQ
        )
        val q = vm.quizSession.value.questions.first()
        vm.selectQuizOption(q.correctIndex)
        vm.submitQuizAnswer()
        vm.nextQuizQuestion()
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun b01_a_freshInstallationColdLaunch_keepsStreakAndTodayActivityAtZero() = runBlocking {
        fakeDateKey = "2026-11-26"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)

        val vm = createFreshViewModel()

        assertEquals("Fresh launch streak must be 0", 0, vm.streakDays.value)
        assertEquals("Fresh launch longest streak must be 0", 0, vm.longestStreak.value)
        assertFalse("Fresh launch must not fabricate act_today key", prefs.contains("act_2026-11-26"))
        assertEquals(0, prefs.getInt("act_2026-11-26", 0))
        assertNull("Fresh launch must not write streak_last_active_date", prefs.getString("streak_last_active_date", null))
        assertEquals(0, prefs.getInt("streak_current_days", 0))
        assertEquals(0, prefs.getInt("streak_longest_days", 0))

        val weekly = vm.weeklyActivities.value
        assertEquals(7, weekly.size)
        val todayStat = weekly.first { it.isToday }
        assertEquals("2026-11-26", todayStat.dateStr)
        assertEquals(0, todayStat.count)
        assertFalse(todayStat.isMet)

        val dashboard = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.ALL_TIME,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = emptyList(),
            practiceHistory = emptyList(),
            topicAnalytics = emptyList(),
            progressItems = emptyList(),
            dailyActivities = emptyList()
        )
        assertEquals(0, dashboard.goalProgress.currentStreakDays)
        assertEquals(0, dashboard.goalProgress.bestStreakDays)
    }

    @Test
    fun b01_b_appLaunchAndModuleNavigationWithoutStudying_doesNotFabricateActivity() = runBlocking {
        fakeDateKey = "2026-11-26"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val vm = createFreshViewModel()

        // Navigate across modules and open lesson/setup screens without completing a study action
        vm.navigateTo(Screen.FormulaLibrary)
        vm.navigateTo(Screen.PracticeLab)
        vm.navigateTo(Screen.Quizzes)
        vm.navigateTo(Screen.Dashboard)
        vm.navigateTo(Screen.BcsPreparation)
        vm.openBookHub()
        vm.openBookDetail(BookId.CLASS_8)
        vm.openBookChapter(BookId.CLASS_8, 1)
        val firstLesson = BookCurriculumRepository.getChapter(BookId.CLASS_8, 1)!!.lessons.first()
        vm.openBookLesson(BookId.CLASS_8, 1, firstLesson.id)
        vm.saveBcsOnboarding(
            profile = BcsExamProfile.BCS_50TH_PRELIM,
            stage = BcsPreparationStage.CONCEPT_MASTERY,
            dailyMinutes = 30,
            dailyQuestions = 20
        )
        // Starting sessions without answering/submitting must not fabricate study activity
        vm.startPracticeSession(PracticeCategory.ARITHMETIC, PracticeDifficulty.EASY, 5, QuestionInputFormat.MCQ)
        vm.startQuiz(QuizTopic.MULTIPLICATION, 5, QuizMode.TOPIC, PracticeDifficulty.EASY)
        vm.startTutor(7)
        vm.navigateBack()

        awaitCondition {
            db.mathDao().getBcsUserProfile("active_profile")?.onboardingCompleted == true
        }

        assertEquals(0, vm.streakDays.value)
        assertEquals(0, vm.longestStreak.value)
        assertFalse("Navigation must not fabricate act_today", prefs.contains("act_2026-11-26"))
        assertEquals(0, prefs.getInt("act_2026-11-26", 0))
        assertNull(prefs.getString("streak_last_active_date", null))
        assertTrue(vm.weeklyActivities.value.all { it.count == 0 })
    }

    @Test
    fun b01_c_firstGenuineCompletedStudyEvent_recordsActivityAndEstablishesStreakOne() = runBlocking {
        fakeDateKey = "2026-11-26"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val vm = createFreshViewModel()

        assertEquals(0, vm.streakDays.value)

        completeSinglePracticeSession(vm, dateKey = "2026-11-26", answerCorrectly = true)
        val sessionId = vm.practiceSession.value.sessionId
        assertTrue(sessionId.isNotBlank())

        assertEquals(1, vm.streakDays.value)
        assertEquals(1, vm.longestStreak.value)
        assertEquals("2026-11-26", prefs.getString("streak_last_active_date", null))
        assertEquals(1, prefs.getInt("streak_current_days", 0))
        assertEquals(1, prefs.getInt("streak_longest_days", 0))
        assertTrue(prefs.getInt("act_2026-11-26", 0) >= 4)
        assertEquals(prefs.getInt("act_2026-11-26", 0), vm.weeklyActivities.value.first { it.isToday }.count)
    }

    @Test
    fun b01_d_multipleStudySessionsOnSameDay_accumulateActivityButCountAsOneStreakDay() = runBlocking {
        fakeDateKey = "2026-11-26"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val vm = createFreshViewModel()

        // Session 1: Practice Lab
        completeSinglePracticeSession(vm, dateKey = "2026-11-26", answerCorrectly = true)
        val countAfterFirst = prefs.getInt("act_2026-11-26", 0)
        assertTrue(countAfterFirst > 0)
        assertEquals(1, vm.streakDays.value)

        // Session 2: Quiz completion on same day
        completeSingleQuizSession(vm)
        awaitCondition {
            (db.mathDao().getDailyStudyActivityByDate("2026-11-26")?.examsCompleted ?: 0) >= 1 &&
                prefs.getInt("act_2026-11-26", 0) > countAfterFirst
        }
        val countAfterSecond = prefs.getInt("act_2026-11-26", 0)
        assertTrue("Second study session on same day must accumulate activity count", countAfterSecond > countAfterFirst)
        assertEquals("Same-day sessions must remain streak = 1", 1, vm.streakDays.value)
        assertEquals(1, vm.longestStreak.value)

        // Session 3: Completing a textbook lesson on same day
        val firstLesson = BookCurriculumRepository.getChapter(BookId.CLASS_7, 1)!!.lessons.first()
        vm.toggleBookLessonCompleted(BookId.CLASS_7, 1, firstLesson.id)
        awaitCondition {
            db.mathDao().getBookLessonProgressByKey("${BookId.CLASS_7.id}:1:${firstLesson.id}")?.isCompleted == true &&
                prefs.getInt("act_2026-11-26", 0) > countAfterSecond
        }

        val countAfterThird = prefs.getInt("act_2026-11-26", 0)
        assertTrue(countAfterThird > countAfterSecond)
        assertEquals("Multiple study events on same day must count as 1 streak day", 1, vm.streakDays.value)
        assertEquals(1, vm.longestStreak.value)
        assertEquals(countAfterThird, vm.weeklyActivities.value.first { it.isToday }.count)
    }

    @Test
    fun b01_e_consecutiveActiveDays_incrementStreakAcrossDayRollover() = runBlocking {
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)

        // Day 1
        fakeDateKey = "2026-11-26"
        val vmDay1 = createFreshViewModel()
        completeSinglePracticeSession(vmDay1, dateKey = "2026-11-26", answerCorrectly = true)
        assertEquals(1, vmDay1.streakDays.value)
        assertEquals(1, vmDay1.longestStreak.value)

        // Day 2: Cold launch before studying keeps streak=1 and does not fabricate Day 2 activity
        fakeDateKey = "2026-11-27"
        val vmDay2 = createFreshViewModel()
        assertEquals(1, vmDay2.streakDays.value)
        assertEquals(0, prefs.getInt("act_2026-11-27", 0))
        assertFalse(prefs.contains("act_2026-11-27"))

        // Complete study on Day 2 -> streak increments to 2
        completeSinglePracticeSession(vmDay2, dateKey = "2026-11-27", answerCorrectly = true)
        assertEquals(2, vmDay2.streakDays.value)
        assertEquals(2, vmDay2.longestStreak.value)

        // Day 3: Complete study -> streak increments to 3
        fakeDateKey = "2026-11-28"
        val vmDay3 = createFreshViewModel()
        assertEquals(2, vmDay3.streakDays.value)
        assertFalse(prefs.contains("act_2026-11-28"))
        completeSinglePracticeSession(vmDay3, dateKey = "2026-11-28", answerCorrectly = true)
        assertEquals(3, vmDay3.streakDays.value)
        assertEquals(3, vmDay3.longestStreak.value)
    }

    @Test
    fun b01_f_missedDayGap_resetsCurrentStreakToZeroWhilePreservingHistory() = runBlocking {
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)

        // Build a 3-day consecutive streak on Nov 20, 21, 22
        fakeDateKey = "2026-11-20"
        val vm1 = createFreshViewModel()
        completeSinglePracticeSession(vm1, dateKey = "2026-11-20")

        fakeDateKey = "2026-11-21"
        val vm2 = createFreshViewModel()
        completeSinglePracticeSession(vm2, dateKey = "2026-11-21")

        fakeDateKey = "2026-11-22"
        val vm3 = createFreshViewModel()
        completeSinglePracticeSession(vm3, dateKey = "2026-11-22")
        assertEquals(3, vm3.streakDays.value)
        assertEquals(3, vm3.longestStreak.value)

        val act20 = prefs.getInt("act_2026-11-20", 0)
        val act21 = prefs.getInt("act_2026-11-21", 0)
        val act22 = prefs.getInt("act_2026-11-22", 0)

        // Skip Nov 23 and Nov 24, launch on Nov 25
        fakeDateKey = "2026-11-25"
        val vmAfterGap = createFreshViewModel()

        assertEquals("Missed-day gap must reset current streak to 0", 0, vmAfterGap.streakDays.value)
        assertEquals(0, prefs.getInt("streak_current_days", -1))
        assertEquals("Longest streak must remain preserved after gap", 3, vmAfterGap.longestStreak.value)
        assertFalse("Gap launch must not fabricate Nov 25 activity", prefs.contains("act_2026-11-25"))
        // Historical activity records untouched
        assertEquals(act20, prefs.getInt("act_2026-11-20", 0))
        assertEquals(act21, prefs.getInt("act_2026-11-21", 0))
        assertEquals(act22, prefs.getInt("act_2026-11-22", 0))

        // Studying on Nov 25 starts a fresh current streak of 1 while preserving longest=3
        completeSinglePracticeSession(vmAfterGap, dateKey = "2026-11-25")
        assertEquals(1, vmAfterGap.streakDays.value)
        assertEquals(3, vmAfterGap.longestStreak.value)
    }

    @Test
    fun b01_g_applicationRecreation_doesNotDuplicateDailyActivityOrMutateStreak() = runBlocking {
        fakeDateKey = "2026-11-26"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)

        val vm1 = createFreshViewModel()
        completeSinglePracticeSession(vm1, dateKey = "2026-11-26", answerCorrectly = true)

        val recordedCount = prefs.getInt("act_2026-11-26", 0)
        assertEquals(5, recordedCount)
        assertEquals(1, vm1.streakDays.value)
        assertEquals(1, vm1.longestStreak.value)

        // Recreate ViewModel multiple times on the same day
        val vmRecreated1 = createFreshViewModel()
        val vmRecreated2 = createFreshViewModel()

        assertEquals("App recreation must not duplicate today's activity", recordedCount, prefs.getInt("act_2026-11-26", 0))
        assertEquals(1, vmRecreated1.streakDays.value)
        assertEquals(1, vmRecreated2.streakDays.value)
        assertEquals(1, vmRecreated2.longestStreak.value)
        assertEquals(recordedCount, vmRecreated2.weeklyActivities.value.first { it.isToday }.count)

        // Recreate on the next day without studying -> yesterday preserved, today absent, streak intact at 1
        fakeDateKey = "2026-11-27"
        val vmNextDay1 = createFreshViewModel()
        val vmNextDay2 = createFreshViewModel()

        assertEquals(recordedCount, prefs.getInt("act_2026-11-26", 0))
        assertFalse(prefs.contains("act_2026-11-27"))
        assertEquals(1, vmNextDay1.streakDays.value)
        assertEquals(1, vmNextDay2.streakDays.value)
        assertEquals(0, vmNextDay2.weeklyActivities.value.first { it.isToday }.count)
        assertEquals(recordedCount, vmNextDay2.weeklyActivities.value.first { it.dateStr == "2026-11-26" }.count)
    }

    @Test
    fun b01_h_historicalLongestStreakAndLegacyActivityRecordsArePreserved() = runBlocking {
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        // Pre-seed legitimate historical longest streak (14 days) ending Oct 14
        prefs.edit()
            .putString("streak_last_active_date", "2026-10-14")
            .putInt("streak_current_days", 14)
            .putInt("streak_longest_days", 14)
            .putInt("act_2026-10-13", 4)
            .putInt("act_2026-10-14", 6)
            .commit()

        fakeDateKey = "2026-11-26"
        val vm = createFreshViewModel()

        // Current streak resets due to gap, while historical longest streak and activity stay intact
        assertEquals(0, vm.streakDays.value)
        assertEquals(14, vm.longestStreak.value)
        assertEquals(14, prefs.getInt("streak_longest_days", 0))
        assertEquals(4, prefs.getInt("act_2026-10-13", 0))
        assertEquals(6, prefs.getInt("act_2026-10-14", 0))
        assertFalse(prefs.contains("act_2026-11-26"))

        // New genuine activity starts current streak=1 without reducing longest=14
        completeSinglePracticeSession(vm, dateKey = "2026-11-26")
        assertEquals(1, vm.streakDays.value)
        assertEquals(14, vm.longestStreak.value)
        assertEquals(14, prefs.getInt("streak_longest_days", 0))
        assertEquals(6, prefs.getInt("act_2026-10-14", 0))
    }
}
