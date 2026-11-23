package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDao
import com.example.data.db.MathDatabase
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsStudyPlanGoalEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.repository.MathRepository
import com.example.math.BcsGoalCreditApplyResult
import com.example.math.BcsGoalCreditDelta
import com.example.math.BcsGoalCreditFaultStage
import com.example.math.BcsGoalCreditLedgerTxnResult
import com.example.math.BcsGoalCreditReceiptStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class BcsGoalCreditRepositoryTest {

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var dao: MathDao
    private lateinit var repository: MathRepository
    private val testDbName = "bcs_gc02_stage3_repository_test.db"

    /**
     * Minimal Kotlin interface delegation wrapper over the real Room [MathDao] for inspecting
     * exact delegated arguments and deterministically simulating post-commit/verification faults
     * without introducing a mocking framework.
     */
    private class InspectingFaultDao(
        private val delegate: MathDao,
        var onApplyCredit: (suspend (String, Long) -> BcsGoalCreditLedgerTxnResult)? = null,
        var onReadLedgerByAttemptId: (suspend (String) -> BcsGoalCreditRecordEntity?)? = null
    ) : MathDao by delegate {
        val applyCalls = mutableListOf<Pair<String, Long>>()
        var readLedgerByIdCount: Int = 0
            private set
        var legacyGoalDeltaTxnCount: Int = 0
            private set

        override suspend fun applyBcsCompletionGoalCreditIdempotently(
            attemptId: String,
            creditedAtMs: Long
        ): BcsGoalCreditLedgerTxnResult {
            applyCalls.add(attemptId to creditedAtMs)
            val hook = onApplyCredit
            return if (hook != null) {
                hook(attemptId, creditedAtMs)
            } else {
                delegate.applyBcsCompletionGoalCreditIdempotently(attemptId, creditedAtMs)
            }
        }

        override suspend fun getBcsGoalCreditRecordByAttemptId(attemptId: String): BcsGoalCreditRecordEntity? {
            readLedgerByIdCount++
            val hook = onReadLedgerByAttemptId
            return if (hook != null) {
                hook(attemptId)
            } else {
                delegate.getBcsGoalCreditRecordByAttemptId(attemptId)
            }
        }

        override suspend fun applyBcsStudyGoalDeltaTransaction(
            goalKey: String,
            examProfileId: String,
            dateKey: String,
            lessonsDelta: Int,
            questionsDelta: Int,
            revisionsDelta: Int,
            studyTimeMinutesDelta: Int,
            mockExamsDelta: Int,
            nowMs: Long
        ): BcsStudyPlanGoalEntity {
            legacyGoalDeltaTxnCount++
            return delegate.applyBcsStudyGoalDeltaTransaction(
                goalKey = goalKey,
                examProfileId = examProfileId,
                dateKey = dateKey,
                lessonsDelta = lessonsDelta,
                questionsDelta = questionsDelta,
                revisionsDelta = revisionsDelta,
                studyTimeMinutesDelta = studyTimeMinutesDelta,
                mockExamsDelta = mockExamsDelta,
                nowMs = nowMs
            )
        }
    }

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.deleteDatabase(testDbName)
        db = Room.databaseBuilder(app, MathDatabase::class.java, testDbName)
            .addMigrations(
                MathDatabase.MIGRATION_1_2,
                MathDatabase.MIGRATION_2_3,
                MathDatabase.MIGRATION_3_4,
                MathDatabase.MIGRATION_4_5,
                MathDatabase.MIGRATION_5_6,
                MathDatabase.MIGRATION_6_7,
                MathDatabase.MIGRATION_7_8,
                MathDatabase.MIGRATION_8_9,
                MathDatabase.MIGRATION_9_10,
                MathDatabase.MIGRATION_10_11
            )
            .allowMainThreadQueries()
            .build()
        dao = db.mathDao()
        repository = MathRepository(dao)
    }

    @After
    fun tearDown() {
        repository.bcsGoalCreditFaultInjector = null
        db.close()
        app.deleteDatabase(testDbName)
    }

    private fun sampleCompletedAttempt(
        attemptId: String,
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        correctCount: Int = 7,
        wrongCount: Int = 3,
        elapsedSeconds: Int = 240,
        completedAt: Long = 1_763_800_000_000L
    ): ExamAttemptEntity = ExamAttemptEntity(
        attemptId = attemptId,
        examTitleBn = "৫০তম বিসিএস মক টেস্ট",
        examType = "BCS_PRELIMINARY_MOCK",
        selectedTopicsCsv = "arithmetic",
        difficulty = "medium",
        questionType = "mcq",
        totalQuestions = correctCount + wrongCount,
        durationLimitSeconds = 600,
        marksPerCorrect = 1.0,
        negativeMarksPerWrong = 0.5,
        status = "COMPLETED",
        currentQuestionIndex = 0,
        elapsedSeconds = elapsedSeconds,
        remainingSeconds = 0,
        questionsJson = "[]",
        answersJson = "{}",
        markedForReviewCsv = "",
        visitedIndicesCsv = "0",
        questionTimeSecondsJson = "{}",
        correctCount = correctCount,
        wrongCount = wrongCount,
        unansweredCount = 0,
        earnedMarks = correctCount.toDouble(),
        negativeMarksDeducted = wrongCount * 0.5,
        finalScore = correctCount - (wrongCount * 0.5),
        percentage = 70.0,
        accuracyPercent = 70.0,
        startedAt = completedAt - (elapsedSeconds * 1000L),
        updatedAt = completedAt,
        completedAt = completedAt,
        examProfileId = examProfileId
    )

    private fun samplePendingRecord(
        attemptId: String,
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        targetDateKey: String = "2026-11-22",
        questionsDelta: Int = 10,
        studyTimeMinutesDelta: Int = 4,
        mockExamsDelta: Int = 1,
        revisionsDelta: Int = 0,
        createdAtMs: Long = 1_763_800_000_000L
    ): BcsGoalCreditRecordEntity = BcsGoalCreditRecordEntity(
        attemptId = attemptId,
        examProfileId = examProfileId,
        targetDateKey = targetDateKey,
        questionsDelta = questionsDelta,
        studyTimeMinutesDelta = studyTimeMinutesDelta,
        mockExamsDelta = mockExamsDelta,
        revisionsDelta = revisionsDelta,
        status = BcsGoalCreditRecordEntity.STATUS_PENDING,
        createdAtMs = createdAtMs,
        creditedAtMs = null
    )

    // =========================================================================
    // TEST 1: finalizeExamAttempt() forwards optional PENDING ledger atomically
    // =========================================================================
    @Test
    fun test1_finalizeExamAttempt_forwardsPendingLedgerRecordAtomicallyWithoutTransformation() = runBlocking {
        val attemptId = "repo_t1_attempt_001"
        val pending = samplePendingRecord(
            attemptId = attemptId,
            examProfileId = "BCS_WRITTEN_008_MATH",
            targetDateKey = "2026-11-20",
            questionsDelta = 12,
            studyTimeMinutesDelta = 9,
            mockExamsDelta = 1,
            revisionsDelta = 2,
            createdAtMs = 1_763_700_123_456L
        )

        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(
                attemptId = attemptId,
                examProfileId = "BCS_WRITTEN_008_MATH"
            ),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = pending
        )

        val persistedAttempt = dao.getExamAttemptById(attemptId)
        assertNotNull(persistedAttempt)
        assertEquals("COMPLETED", persistedAttempt!!.status)

        val persistedLedger = dao.getBcsGoalCreditRecordByAttemptId(attemptId)
        assertEquals("Pending ledger row must pass through verbatim without transformation", pending, persistedLedger)
    }

    // =========================================================================
    // TEST 2: Existing callers omitting optional ledger record remain compatible
    // =========================================================================
    @Test
    fun test2_finalizeExamAttempt_omittingPendingLedgerPreservesExistingCallers() = runBlocking {
        val twoArgId = "repo_t2_two_arg"
        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = twoArgId),
            perTopicStats = emptyList()
        )
        assertNotNull(dao.getExamAttemptById(twoArgId))
        assertNull("2-arg finalizeExamAttempt must not insert a ledger row", dao.getBcsGoalCreditRecordByAttemptId(twoArgId))

        val fourArgId = "repo_t2_four_arg"
        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = fourArgId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList()
        )
        assertNotNull(dao.getExamAttemptById(fourArgId))
        assertNull("4-arg finalizeExamAttempt must not insert a ledger row", dao.getBcsGoalCreditRecordByAttemptId(fourArgId))
    }

    // =========================================================================
    // TEST 3: Repository credit API delegates using only attemptId & creditedAtMs
    // =========================================================================
    @Test
    fun test3_applyBcsCompletionGoalCreditFromLedger_delegatesOnlyAttemptIdAndCreditedAtMs() = runBlocking {
        val inspectingDao = InspectingFaultDao(dao)
        val inspectingRepo = MathRepository(inspectingDao)

        val attemptId = "repo_t3_delegate"
        inspectingRepo.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(attemptId = attemptId)
        )

        val creditedAtMs = 1_763_803_333_000L
        val result = inspectingRepo.applyBcsCompletionGoalCreditFromLedger(
            attemptId = attemptId,
            creditedAtMs = creditedAtMs
        )

        assertTrue("Expected Applied, got $result", result is BcsGoalCreditLedgerTxnResult.Applied)
        assertEquals("Must invoke DAO ledger transaction exactly once", 1, inspectingDao.applyCalls.size)
        assertEquals(attemptId to creditedAtMs, inspectingDao.applyCalls.single())
        assertEquals("Must never invoke legacy direct goal delta transaction", 0, inspectingDao.legacyGoalDeltaTxnCount)
    }

    // =========================================================================
    // TEST 4: Valid persisted PENDING record returns Applied using persisted values
    // =========================================================================
    @Test
    fun test4_validPendingRecord_returnsApplied_andUsesPersistedLedgerValues() = runBlocking {
        dao.upsertBcsStudyGoal(
            BcsStudyPlanGoalEntity(
                goalKey = "BCS_50TH_PRELIM_2025_2026-11-21",
                examProfileId = "BCS_50TH_PRELIM_2025",
                dateKey = "2026-11-21",
                lessonsCompleted = 4,
                questionsSolved = 6,
                revisionsCompleted = 1,
                studyTimeMinutes = 10,
                mockExamsCompleted = 0,
                updatedAt = 1_763_750_000_000L
            )
        )

        val attemptId = "repo_t4_applied"
        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attemptId,
                examProfileId = "BCS_50TH_PRELIM_2025",
                targetDateKey = "2026-11-21",
                questionsDelta = 9,
                studyTimeMinutesDelta = 5,
                mockExamsDelta = 1,
                revisionsDelta = 1
            )
        )

        val creditedAt = 1_763_804_500_000L
        val result = repository.applyBcsCompletionGoalCreditFromLedger(attemptId, creditedAt)
        assertTrue(result is BcsGoalCreditLedgerTxnResult.Applied)
        val applied = result as BcsGoalCreditLedgerTxnResult.Applied
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, applied.record.status)
        assertEquals(creditedAt, applied.record.creditedAtMs)

        // Inspect actual persisted Room rows
        val persistedLedger = dao.getBcsGoalCreditRecordByAttemptId(attemptId)!!
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, persistedLedger.status)
        assertEquals(creditedAt, persistedLedger.creditedAtMs)

        val persistedGoal = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-21")!!
        assertEquals("Existing lessonsCompleted must be preserved", 4, persistedGoal.lessonsCompleted)
        assertEquals(15, persistedGoal.questionsSolved)
        assertEquals(2, persistedGoal.revisionsCompleted)
        assertEquals(15, persistedGoal.studyTimeMinutes)
        assertEquals(1, persistedGoal.mockExamsCompleted)
        assertEquals(creditedAt, persistedGoal.updatedAt)
    }

    // =========================================================================
    // TEST 5: Repeated credit returns AlreadyCredited without duplicate increment
    // =========================================================================
    @Test
    fun test5_repeatedCredit_returnsAlreadyCredited_withoutDuplicateGoalIncrement() = runBlocking {
        val attemptId = "repo_t5_duplicate"
        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attemptId,
                questionsDelta = 8,
                studyTimeMinutesDelta = 3,
                mockExamsDelta = 1,
                revisionsDelta = 0
            )
        )

        val firstTs = 1_763_805_000_000L
        val first = repository.applyBcsCompletionGoalCreditFromLedger(attemptId, firstTs)
        assertTrue(first is BcsGoalCreditLedgerTxnResult.Applied)

        val second = repository.applyBcsCompletionGoalCreditFromLedger(attemptId, firstTs + 30_000L)
        assertTrue("Expected AlreadyCredited, got $second", second is BcsGoalCreditLedgerTxnResult.AlreadyCredited)
        assertEquals(firstTs, (second as BcsGoalCreditLedgerTxnResult.AlreadyCredited).record.creditedAtMs)

        val goal = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-22")!!
        assertEquals(8, goal.questionsSolved)
        assertEquals(3, goal.studyTimeMinutes)
        assertEquals(1, goal.mockExamsCompleted)
        assertEquals(0, goal.revisionsCompleted)
    }

    // =========================================================================
    // TEST 6: Missing ledger record returns NoPendingRecord without goal mutation
    // =========================================================================
    @Test
    fun test6_missingOrInvalidLedgerRecord_returnsExplicitNonSuccessWithoutGoalMutation() = runBlocking {
        val missing = repository.applyBcsCompletionGoalCreditFromLedger(
            attemptId = "repo_t6_missing",
            creditedAtMs = 1_763_806_000_000L
        )
        assertTrue("Expected NoPendingRecord, got $missing", missing is BcsGoalCreditLedgerTxnResult.NoPendingRecord)
        assertEquals("repo_t6_missing", (missing as BcsGoalCreditLedgerTxnResult.NoPendingRecord).attemptId)
        assertNull(dao.getBcsGoalCreditRecordByAttemptId("repo_t6_missing"))
        assertTrue(dao.getAllBcsStudyGoals().first().isEmpty())

        // Also confirm InvalidLedgerState propagates verbatim and is never converted to success
        db.openHelper.writableDatabase.execSQL(
            """
            INSERT INTO `bcs_goal_credit_records` (
                `attemptId`, `examProfileId`, `targetDateKey`,
                `questionsDelta`, `studyTimeMinutesDelta`, `mockExamsDelta`, `revisionsDelta`,
                `status`, `createdAtMs`, `creditedAtMs`
            ) VALUES (
                'repo_t6_invalid_status', 'BCS_50TH_PRELIM_2025', '2026-11-22',
                5, 2, 1, 0,
                'CORRUPT', 1763806000000, NULL
            )
            """.trimIndent()
        )
        val invalid = repository.applyBcsCompletionGoalCreditFromLedger(
            attemptId = "repo_t6_invalid_status",
            creditedAtMs = 1_763_806_100_000L
        )
        assertTrue("Expected InvalidLedgerState, got $invalid", invalid is BcsGoalCreditLedgerTxnResult.InvalidLedgerState)
        assertTrue(dao.getAllBcsStudyGoals().first().isEmpty())
    }

    // =========================================================================
    // TEST 7: Caller-supplied values in compatibility API cannot override or authorize
    // =========================================================================
    @Test
    fun test7_compatibilityApi_callerSuppliedValuesCannotOverridePersistedLedgerOrAuthorizeCredit() = runBlocking {
        val inspectingDao = InspectingFaultDao(dao)
        val inspectingRepo = MathRepository(inspectingDao)

        // 7a. Persisted PENDING row exists -> conflicting caller-supplied delta/profile/date must be ignored
        val persistedAttemptId = "repo_t7_persisted"
        inspectingRepo.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(
                attemptId = persistedAttemptId,
                examProfileId = "BCS_50TH_PRELIM_2025"
            ),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = persistedAttemptId,
                examProfileId = "BCS_50TH_PRELIM_2025",
                targetDateKey = "2026-11-19",
                questionsDelta = 4,
                studyTimeMinutesDelta = 2,
                mockExamsDelta = 1,
                revisionsDelta = 0
            )
        )

        val forgedDelta = BcsGoalCreditDelta(
            attemptId = persistedAttemptId,
            examProfileId = "BCS_WRITTEN_008_MATH",
            questionsDelta = 999,
            studyTimeMinutesDelta = 888,
            mockExamsDelta = 77,
            revisionsDelta = 66
        )
        val compatApplied = inspectingRepo.applyBcsCompletionGoalCredit(
            delta = forgedDelta,
            nowMs = 1_763_807_000_000L
        )
        assertTrue("Expected Committed via persisted ledger, got $compatApplied", compatApplied is BcsGoalCreditApplyResult.Committed)
        assertEquals("Legacy direct delta DAO method must never be called", 0, inspectingDao.legacyGoalDeltaTxnCount)

        val allGoals = dao.getAllBcsStudyGoals().first()
        assertEquals("Only the persisted ledger's profile+date goal row may exist", 1, allGoals.size)
        val persistedGoal = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-19")!!
        assertEquals(4, persistedGoal.questionsSolved)
        assertEquals(2, persistedGoal.studyTimeMinutes)
        assertEquals(1, persistedGoal.mockExamsCompleted)
        assertEquals(0, persistedGoal.revisionsCompleted)
        assertNull(dao.getBcsStudyGoalByKey("BCS_WRITTEN_008_MATH_2026-11-19"))

        // 7b. No PENDING row exists (even if legacy SharedPreferences markers exist) -> must fail without mutating goals
        val prefs = app.getSharedPreferences("bcs_gc02_stage3_legacy_compat_test", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        val legacyStore = BcsGoalCreditReceiptStore(prefs)
        val unpersistedAttemptId = "repo_t7_no_pending_row"
        legacyStore.markWp41AttemptLaunched(unpersistedAttemptId)

        val unpersistedResult = inspectingRepo.applyBcsCompletionGoalCredit(
            delta = BcsGoalCreditDelta(
                attemptId = unpersistedAttemptId,
                examProfileId = "BCS_50TH_PRELIM_2025",
                questionsDelta = 25,
                studyTimeMinutesDelta = 15,
                mockExamsDelta = 1,
                revisionsDelta = 1
            ),
            nowMs = 1_763_807_100_000L
        )
        assertTrue(
            "Missing PENDING ledger row must return explicit failure, got $unpersistedResult",
            unpersistedResult is BcsGoalCreditApplyResult.FailedBeforeMutation
        )
        assertNull(dao.getBcsGoalCreditRecordByAttemptId(unpersistedAttemptId))
        assertEquals("No new goal rows may be created without a PENDING ledger row", 1, dao.getAllBcsStudyGoals().first().size)
        assertEquals("Existing goal counters must remain unchanged", 4, dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-19")!!.questionsSolved)
        assertEquals(0, inspectingDao.legacyGoalDeltaTxnCount)
    }

    // =========================================================================
    // TEST 8: Post-fault verification handles CREDITED, PENDING, missing, and read failure
    // =========================================================================
    @Test
    fun test8_postFaultVerification_detectsCommittedCredited_andNeverAppliesSecondCredit() = runBlocking {
        // 8a. DAO commits PENDING -> CREDITED to real SQLite, then throws an ordinary exception
        val committedThenFaultDao = InspectingFaultDao(dao)
        val committedThenFaultRepo = MathRepository(committedThenFaultDao)
        val attempt8a = "repo_t8a_post_commit_dao_fault"
        committedThenFaultRepo.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attempt8a),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attempt8a,
                targetDateKey = "2026-11-18",
                questionsDelta = 6,
                studyTimeMinutesDelta = 3,
                mockExamsDelta = 1
            )
        )
        committedThenFaultDao.onApplyCredit = { id, ts ->
            val committed = dao.applyBcsCompletionGoalCreditIdempotently(id, ts)
            assertTrue(committed is BcsGoalCreditLedgerTxnResult.Applied)
            throw IllegalStateException("Simulated post-commit DAO transport failure")
        }

        val ts8a = 1_763_808_000_000L
        val verified8a = committedThenFaultRepo.applyBcsCompletionGoalCreditFromLedger(attempt8a, ts8a)
        assertTrue("Post-fault verification must resolve committed row as AlreadyCredited, got $verified8a", verified8a is BcsGoalCreditLedgerTxnResult.AlreadyCredited)
        assertEquals(ts8a, (verified8a as BcsGoalCreditLedgerTxnResult.AlreadyCredited).record.creditedAtMs)
        assertEquals("Repository must NEVER call DAO credit transaction a second time", 1, committedThenFaultDao.applyCalls.size)
        assertEquals("Post-fault verification must query Room ledger once", 1, committedThenFaultDao.readLedgerByIdCount)

        val goal8a = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-18")!!
        assertEquals(6, goal8a.questionsSolved)
        assertEquals(3, goal8a.studyTimeMinutes)
        assertEquals(1, goal8a.mockExamsCompleted)

        // 8b. Real MathRepository + AFTER_DB_COMMIT fault injector resolves as AlreadyCredited with 1x goal increment
        val attempt8b = "repo_t8b_after_commit_hook"
        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attempt8b),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attempt8b,
                targetDateKey = "2026-11-17",
                questionsDelta = 5,
                studyTimeMinutesDelta = 2,
                mockExamsDelta = 1
            )
        )
        repository.bcsGoalCreditFaultInjector = { stage ->
            if (stage == BcsGoalCreditFaultStage.AFTER_DB_COMMIT) {
                throw IllegalStateException("Injected post-commit repository fault")
            }
        }
        try {
            val verified8b = repository.applyBcsCompletionGoalCreditFromLedger(attempt8b, 1_763_808_100_000L)
            assertTrue(verified8b is BcsGoalCreditLedgerTxnResult.AlreadyCredited)
        } finally {
            repository.bcsGoalCreditFaultInjector = null
        }
        val goal8b = dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-17")!!
        assertEquals(5, goal8b.questionsSolved)
        assertEquals(2, goal8b.studyTimeMinutes)
        assertEquals(1, goal8b.mockExamsCompleted)

        // 8c. Failure while row remains PENDING rethrows original exception without touching goals
        val pendingFaultDao = InspectingFaultDao(dao)
        val pendingFaultRepo = MathRepository(pendingFaultDao)
        val attempt8c = "repo_t8c_still_pending"
        pendingFaultRepo.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attempt8c),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(
                attemptId = attempt8c,
                targetDateKey = "2026-11-16",
                questionsDelta = 7,
                studyTimeMinutesDelta = 3,
                mockExamsDelta = 1
            )
        )
        val pendingFailure = IllegalStateException("Transient failure before commit")
        pendingFaultDao.onApplyCredit = { _, _ -> throw pendingFailure }
        try {
            pendingFaultRepo.applyBcsCompletionGoalCreditFromLedger(attempt8c, 1_763_808_200_000L)
            fail("Original failure must be rethrown when ledger row remains PENDING")
        } catch (thrown: IllegalStateException) {
            assertSame(pendingFailure, thrown)
        }
        assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, dao.getBcsGoalCreditRecordByAttemptId(attempt8c)!!.status)
        assertNull(dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-16"))

        // 8d. Failure when no ledger row exists rethrows original exception without creating row or goal
        val missingFailure = IllegalStateException("Failure with missing ledger row")
        pendingFaultDao.onApplyCredit = { _, _ -> throw missingFailure }
        try {
            pendingFaultRepo.applyBcsCompletionGoalCreditFromLedger("repo_t8d_missing", 1_763_808_300_000L)
            fail("Original failure must be rethrown when no ledger row exists")
        } catch (thrown: IllegalStateException) {
            assertSame(missingFailure, thrown)
        }
        assertNull(dao.getBcsGoalCreditRecordByAttemptId("repo_t8d_missing"))

        // 8e. Failure during post-fault verification preserves original exception and attaches suppressed failure
        val verifyFailDao = InspectingFaultDao(dao)
        val verifyFailRepo = MathRepository(verifyFailDao)
        val primaryFailure = IllegalStateException("Primary credit execution failure")
        val verificationFailure = RuntimeException("Verification SQLite read failure")
        verifyFailDao.onApplyCredit = { _, _ -> throw primaryFailure }
        verifyFailDao.onReadLedgerByAttemptId = { _ -> throw verificationFailure }
        try {
            verifyFailRepo.applyBcsCompletionGoalCreditFromLedger(attempt8c, 1_763_808_400_000L)
            fail("Original failure must be rethrown when verification fails")
        } catch (thrown: IllegalStateException) {
            assertSame(primaryFailure, thrown)
            assertEquals(1, thrown.suppressed.size)
            assertSame(verificationFailure, thrown.suppressed[0])
        }
    }

    // =========================================================================
    // TEST 9: Ledger query helpers return exact persisted DAO records
    // =========================================================================
    @Test
    fun test9_queryHelpers_returnExactPersistedRecordsAndTrackTransitions() = runBlocking {
        val olderPending = samplePendingRecord(
            attemptId = "repo_t9_older",
            targetDateKey = "2026-11-20",
            questionsDelta = 4,
            createdAtMs = 1_763_800_001_000L
        )
        val newerPending = samplePendingRecord(
            attemptId = "repo_t9_newer",
            targetDateKey = "2026-11-21",
            questionsDelta = 9,
            createdAtMs = 1_763_800_002_000L
        )

        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = olderPending.attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = olderPending
        )
        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = newerPending.attemptId),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = newerPending
        )

        assertEquals(olderPending, repository.getBcsGoalCreditRecordByAttemptId(olderPending.attemptId))
        assertEquals(newerPending, repository.getBcsGoalCreditRecordByAttemptId(newerPending.attemptId))
        assertNull(repository.getBcsGoalCreditRecordByAttemptId("repo_t9_absent"))

        // Pending list is ordered by createdAtMs ASC
        val pendingList = repository.getPendingBcsGoalCreditRecords()
        assertEquals(listOf(olderPending, newerPending), pendingList)

        // Flow emits all records ordered by createdAtMs DESC
        val allFlowBefore = repository.allBcsGoalCreditRecords.first()
        assertEquals(listOf(newerPending, olderPending), allFlowBefore)

        // Credit the older row -> pending list now contains only newerPending; Flow shows both with updated status
        val creditedTs = 1_763_809_000_000L
        assertTrue(repository.applyBcsCompletionGoalCreditFromLedger(olderPending.attemptId, creditedTs) is BcsGoalCreditLedgerTxnResult.Applied)
        assertEquals(listOf(newerPending), repository.getPendingBcsGoalCreditRecords())

        val creditedRow = repository.getBcsGoalCreditRecordByAttemptId(olderPending.attemptId)!!
        assertEquals(BcsGoalCreditRecordEntity.STATUS_CREDITED, creditedRow.status)
        assertEquals(creditedTs, creditedRow.creditedAtMs)

        // Delete both attempts via repository.deleteExamAttempt -> removes PENDING row, preserves CREDITED row
        repository.deleteExamAttempt(newerPending.attemptId)
        repository.deleteExamAttempt(olderPending.attemptId)
        assertTrue(repository.getPendingBcsGoalCreditRecords().isEmpty())
        assertNull(repository.getBcsGoalCreditRecordByAttemptId(newerPending.attemptId))
        assertEquals(creditedRow, repository.getBcsGoalCreditRecordByAttemptId(olderPending.attemptId))
        assertEquals(listOf(creditedRow), repository.allBcsGoalCreditRecords.first())
    }

    // =========================================================================
    // TEST 10: CancellationException is never swallowed or converted to success
    // =========================================================================
    @Test
    fun test10_cancellationException_isNeverSwallowedOrConvertedToSuccess() = runBlocking {
        // 10a. CancellationException thrown AFTER real Room commit must still propagate
        val cancelDao = InspectingFaultDao(dao)
        val cancelRepo = MathRepository(cancelDao)
        val attempt10a = "repo_t10a_cancel_after_commit"
        cancelRepo.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attempt10a),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(attemptId = attempt10a)
        )
        cancelDao.onApplyCredit = { id, ts ->
            dao.applyBcsCompletionGoalCreditIdempotently(id, ts)
            throw CancellationException("Coroutine cancelled after commit")
        }
        try {
            cancelRepo.applyBcsCompletionGoalCreditFromLedger(attempt10a, 1_763_810_000_000L)
            fail("CancellationException must not be swallowed or converted to AlreadyCredited")
        } catch (ce: CancellationException) {
            assertEquals("Coroutine cancelled after commit", ce.message)
        }
        assertEquals("Verification query must not run on CancellationException", 0, cancelDao.readLedgerByIdCount)

        // 10b. CancellationException thrown BEFORE DB mutation propagates cleanly with row still PENDING
        val attempt10b = "repo_t10b_cancel_before"
        repository.finalizeExamAttempt(
            completedAttempt = sampleCompletedAttempt(attemptId = attempt10b),
            perTopicStats = emptyList(),
            bcsTopicUpdates = emptyList(),
            questionMistakeOutcomes = emptyList(),
            pendingBcsGoalCredit = samplePendingRecord(attemptId = attempt10b, targetDateKey = "2026-11-15")
        )
        repository.bcsGoalCreditFaultInjector = { stage ->
            if (stage == BcsGoalCreditFaultStage.BEFORE_DB_MUTATION) {
                throw CancellationException("Cancelled before DB mutation")
            }
        }
        try {
            try {
                repository.applyBcsCompletionGoalCreditFromLedger(attempt10b, 1_763_810_100_000L)
                fail("CancellationException must propagate")
            } catch (ce: CancellationException) {
                assertEquals("Cancelled before DB mutation", ce.message)
            }
            // 10d. Legacy compatibility adapter must also propagate CancellationException
            try {
                repository.applyBcsCompletionGoalCredit(
                    delta = BcsGoalCreditDelta(
                        attemptId = attempt10b,
                        examProfileId = "BCS_50TH_PRELIM_2025",
                        questionsDelta = 10,
                        studyTimeMinutesDelta = 4,
                        mockExamsDelta = 1
                    ),
                    nowMs = 1_763_810_150_000L
                )
                fail("Compatibility adapter must propagate CancellationException")
            } catch (ce: CancellationException) {
                assertEquals("Cancelled before DB mutation", ce.message)
            }
        } finally {
            repository.bcsGoalCreditFaultInjector = null
        }
        assertEquals(BcsGoalCreditRecordEntity.STATUS_PENDING, dao.getBcsGoalCreditRecordByAttemptId(attempt10b)!!.status)
        assertNull(dao.getBcsStudyGoalByKey("BCS_50TH_PRELIM_2025_2026-11-15"))

        // 10c. CancellationException thrown during post-fault verification propagates cleanly
        val verifyCancelDao = InspectingFaultDao(dao)
        val verifyCancelRepo = MathRepository(verifyCancelDao)
        verifyCancelDao.onApplyCredit = { _, _ -> throw IllegalStateException("Initial error") }
        verifyCancelDao.onReadLedgerByAttemptId = { _ -> throw CancellationException("Cancelled during verification") }
        try {
            verifyCancelRepo.applyBcsCompletionGoalCreditFromLedger(attempt10b, 1_763_810_200_000L)
            fail("CancellationException during post-fault verification must propagate")
        } catch (ce: CancellationException) {
            assertEquals("Cancelled during verification", ce.message)
        }
    }
}
