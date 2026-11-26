package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDao
import com.example.data.db.MathDatabase
import com.example.data.model.BcsMistakeEntity
import com.example.data.model.BcsTopicProgressEntity
import com.example.data.model.BookMistakeEntity
import com.example.data.model.BookSpacedRevisionEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.TopicAnalyticsEntity
import com.example.data.model.UnifiedMistakeEntity
import com.example.data.repository.BcsTopicExamUpdate
import com.example.data.repository.ExamQuestionMistakeOutcome
import com.example.data.repository.MathRepository
import com.example.data.repository.TopicExamStat
import com.example.math.AnalyticsTimeFilter
import com.example.math.LearningAnalyticsEngine
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h900dp-xhdpi")
class Phase3DatabaseTransactionAndHistoryTest {

    private lateinit var app: Application
    private lateinit var db: MathDatabase
    private lateinit var dao: MathDao
    private lateinit var repository: MathRepository

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        db = Room.inMemoryDatabaseBuilder(app, MathDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.mathDao()
        repository = MathRepository(dao)
    }

    @After
    fun tearDown() {
        runCatching { db.openHelper.writableDatabase.execSQL("DROP TRIGGER IF EXISTS abort_after_partial_exam_writes") }
        runCatching { db.openHelper.writableDatabase.execSQL("DROP TRIGGER IF EXISTS abort_session_after_partial_writes") }
        runCatching { db.openHelper.writableDatabase.execSQL("DROP TRIGGER IF EXISTS abort_book_mistake_partial_writes") }
        runCatching { db.openHelper.writableDatabase.execSQL("DROP TRIGGER IF EXISTS abort_bcs_mistake_partial_writes") }
        runCatching { db.openHelper.writableDatabase.execSQL("DROP TRIGGER IF EXISTS abort_a05_after_quiz_score") }
        db.close()
    }

    private suspend fun seedExistingBaselineRows(nowMs: Long) {
        dao.upsertExamAttempt(
            ExamAttemptEntity(
                attemptId = "seed_exam_1",
                examTitleBn = "পূর্বের পরীক্ষা",
                examType = "TOPIC_TEST",
                selectedTopicsCsv = "algebra",
                difficulty = "easy",
                questionType = "mcq",
                totalQuestions = 5,
                durationLimitSeconds = 120,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.0,
                status = "COMPLETED",
                currentQuestionIndex = 4,
                elapsedSeconds = 40,
                remainingSeconds = 80,
                questionsJson = "[]",
                answersJson = "{}",
                markedForReviewCsv = "",
                visitedIndicesCsv = "0,1",
                questionTimeSecondsJson = "{}",
                correctCount = 4,
                wrongCount = 1,
                unansweredCount = 0,
                earnedMarks = 4.0,
                negativeMarksDeducted = 0.0,
                finalScore = 4.0,
                percentage = 80.0,
                accuracyPercent = 80.0,
                startedAt = nowMs - 100_000L,
                updatedAt = nowMs - 60_000L,
                completedAt = nowMs - 60_000L,
                examProfileId = "BCS_50TH_PRELIM_2025"
            )
        )
        dao.insertQuizScore(
            QuizScoreEntity(
                topic = "algebra",
                topicTitleBn = "পূর্বের স্কোর",
                score = 4,
                totalQuestions = 5,
                percentage = 80,
                timestamp = nowMs - 60_000L
            )
        )
        dao.upsertTopicAnalytics(
            TopicAnalyticsEntity(
                analyticsKey = "QUIZ_algebra_easy",
                module = "QUIZ",
                topicId = "algebra",
                topicTitleBn = "বীজগণিত",
                difficulty = "easy",
                totalAttempted = 5,
                totalCorrect = 4,
                totalWrong = 1,
                bestStreak = 3,
                totalTimeSeconds = 40,
                lastPracticedAt = nowMs - 60_000L
            )
        )
        dao.insertPracticeHistory(
            PracticeHistoryEntity(
                module = "PRACTICE_LAB",
                topicId = "algebra",
                topicTitleBn = "বীজগণিত",
                difficulty = "easy",
                mode = "fixed",
                score = 40,
                correctCount = 4,
                wrongCount = 1,
                totalQuestions = 5,
                accuracyPercent = 80,
                bestStreak = 3,
                durationSeconds = 40,
                timestamp = nowMs - 60_000L
            )
        )
        dao.upsertDailyStudyActivity(
            DailyStudyActivityEntity(
                dateKey = "2024-01-15",
                questionsAttempted = 5,
                correctCount = 4,
                wrongCount = 1,
                studyTimeSeconds = 40,
                examsCompleted = 1,
                practiceSessionsCompleted = 1,
                updatedAt = nowMs - 60_000L
            )
        )
        dao.upsertBcsTopicProgress(
            BcsTopicProgressEntity(
                topicId = "algebra",
                subjectTrack = "MATH_REASONING",
                isGuideCompleted = true,
                miniCheckPassed = false,
                questionsAttempted = 5,
                questionsCorrect = 4,
                totalTimeSeconds = 40,
                masteryPercent = 80,
                lastStudiedAt = nowMs - 60_000L,
                examProfileId = "BCS_50TH_PRELIM_2025"
            )
        )
        dao.upsertBcsMistake(
            BcsMistakeEntity(
                questionId = "seed_bcs_q1",
                subjectTrack = "MATH_REASONING",
                topicId = "algebra",
                questionPromptBn = "পূর্বের প্রশ্ন",
                wrongAnswerBn = "১",
                correctAnswerBn = "২",
                explanationBn = "ব্যাখ্যা",
                examProfileId = "BCS_50TH_PRELIM_2025",
                lastMistakeAt = nowMs - 60_000L
            )
        )
        dao.upsertBookMistake(
            BookMistakeEntity(
                questionId = "seed_book_q1",
                bookId = "class_9_10",
                chapterNumber = 1,
                questionPromptBn = "বই প্রশ্ন",
                previousWrongAnswerBn = "ক",
                correctAnswerBn = "খ",
                explanationBn = "ব্যাখ্যা",
                sourceProvenance = "ORIGINAL_TEXTBOOK",
                lastMistakeAt = nowMs - 60_000L
            )
        )
        dao.upsertBookSpacedRevision(
            BookSpacedRevisionEntity(
                itemId = "mistake_seed_book_q1",
                itemType = "QUESTION",
                bookId = "class_9_10",
                chapterNumber = 1,
                titleBn = "বই প্রশ্ন",
                subtitleBn = "খ",
                intervalStage = 0,
                nextReviewEpochMs = nowMs + 86_400_000L,
                lastReviewedAt = nowMs - 60_000L
            )
        )
        dao.upsertUnifiedMistake(
            UnifiedMistakeEntity(
                mistakeKey = "SEED_UNIFIED_1",
                sourceModule = "QUIZ",
                subjectId = "algebra",
                curriculumTrack = "NCTB",
                questionPromptBn = "ইউনিফাইড প্রশ্ন",
                userAnswerBn = "১",
                correctAnswerBn = "২",
                diagnosticExplanationBn = "ব্যাখ্যা",
                lastFailedAtMillis = nowMs - 60_000L
            )
        )
    }

    @Test
    fun finalizeExamAttempt_preSeededRollbackTriggerPreservesAllTables_thenCommitsAtomically(): Unit = runBlocking {
        val nowMs = System.currentTimeMillis()
        seedExistingBaselineRows(nowMs)

        val newExam = ExamAttemptEntity(
            attemptId = "new_exam_rollback_test",
            examTitleBn = "নতুন বিসিএস পরীক্ষা",
            examType = "BCS_PRELIMINARY_MOCK",
            selectedTopicsCsv = "algebra",
            difficulty = "easy",
            questionType = "mcq",
            totalQuestions = 10,
            durationLimitSeconds = 300,
            marksPerCorrect = 1.0,
            negativeMarksPerWrong = 0.25,
            status = "COMPLETED",
            currentQuestionIndex = 9,
            elapsedSeconds = 120,
            remainingSeconds = 180,
            questionsJson = "[]",
            answersJson = "{}",
            markedForReviewCsv = "",
            visitedIndicesCsv = "0,1",
            questionTimeSecondsJson = "{}",
            correctCount = 8,
            wrongCount = 2,
            unansweredCount = 0,
            earnedMarks = 8.0,
            negativeMarksDeducted = 0.5,
            finalScore = 7.5,
            percentage = 75.0,
            accuracyPercent = 80.0,
            startedAt = nowMs - 120_000L,
            updatedAt = nowMs,
            completedAt = nowMs,
            examProfileId = "BCS_50TH_PRELIM_2025"
        )
        val topicStats = listOf(
            TopicExamStat("algebra", "বীজগণিত", 10, 8, 2, 0, 120)
        )
        val bcsUpdates = listOf(
            BcsTopicExamUpdate(
                topicId = "algebra",
                miniCheckPassed = true,
                additionalTimeSeconds = 120,
                questionsAttemptedDelta = 10,
                questionsCorrectDelta = 8,
                examProfileId = "BCS_50TH_PRELIM_2025"
            )
        )
        val mistakes = listOf(
            ExamQuestionMistakeOutcome(
                unifiedMistakeKey = "QUIZ_NEW_MISTAKE_1",
                sourceModule = "BCS",
                subjectId = "algebra",
                curriculumTrack = "BCS",
                questionPromptBn = "নতুন ভুল প্রশ্ন",
                userAnswerBn = "৩",
                correctAnswerBn = "৪",
                diagnosticExplanationBn = "সঠিক সমাধান ৪",
                isCorrect = false,
                shouldRecordMistake = true,
                isBcsSession = true,
                bcsQuestionId = "new_bcs_q_abort",
                bcsTopicId = "algebra",
                examProfileId = "BCS_50TH_PRELIM_2025"
            )
        )

        val wdb = db.openHelper.writableDatabase
        wdb.execSQL(
            """
            CREATE TRIGGER abort_after_partial_exam_writes
            BEFORE INSERT ON unified_mistake_notebook
            BEGIN
                SELECT RAISE(ABORT, 'Simulated mid-transaction failure');
            END;
            """.trimIndent()
        )

        var failureThrown = false
        try {
            repository.finalizeExamAttempt(
                completedAttempt = newExam,
                perTopicStats = topicStats,
                bcsTopicUpdates = bcsUpdates,
                questionMistakeOutcomes = mistakes
            )
        } catch (ex: Exception) {
            failureThrown = true
            assertTrue(ex.message?.contains("Simulated mid-transaction failure") == true)
        } finally {
            wdb.execSQL("DROP TRIGGER IF EXISTS abort_after_partial_exam_writes")
        }

        assertTrue("Trigger must abort mid-transaction and raise exception", failureThrown)

        // Verify ALL pre-existing rows remain 100% intact and ZERO partial writes were committed.
        assertEquals(1, dao.getAllExamAttempts().first().size)
        assertEquals("seed_exam_1", dao.getAllExamAttempts().first().first().attemptId)
        assertEquals(1, dao.getAllQuizScores().first().size)
        assertEquals(4, dao.getTopicAnalyticsByKey("QUIZ_algebra_easy")?.totalCorrect)
        assertEquals(1, dao.getAllPracticeHistory().first().size)
        assertEquals(1, dao.getAllDailyStudyActivity().first().size)
        assertEquals(5, dao.getBcsTopicProgressById("algebra")?.questionsAttempted)
        assertEquals(1, dao.getAllBcsMistakes().first().size)
        assertEquals(1, dao.getAllBookMistakes().first().size)
        assertEquals(1, dao.getAllBookSpacedRevision().first().size)
        assertEquals(1, dao.getAllUnifiedMistakes().first().size)

        // Now drop trigger complete -> 4-argument overload commits atomically.
        repository.finalizeExamAttempt(
            completedAttempt = newExam,
            perTopicStats = topicStats,
            bcsTopicUpdates = bcsUpdates,
            questionMistakeOutcomes = mistakes
        )

        assertEquals(2, dao.getAllExamAttempts().first().size)
        assertNotNull(dao.getExamAttemptById("new_exam_rollback_test"))
        assertEquals(2, dao.getAllQuizScores().first().size)
        assertEquals(12, dao.getTopicAnalyticsByKey("QUIZ_algebra_easy")?.totalCorrect)
        assertEquals(2, dao.getAllPracticeHistory().first().size)
        assertEquals(2, dao.getAllDailyStudyActivity().first().size)
        assertEquals(15, dao.getBcsTopicProgressById("algebra")?.questionsAttempted)
        assertEquals(2, dao.getAllBcsMistakes().first().size)
        // WP4.1 F4-03: BCS exam outcome records strictly the canonical BCS profile-scoped unified key (1 seeded + 1 canonical BCS = 2) with zero generic QUIZ_ duplicate.
        assertEquals(2, dao.getAllUnifiedMistakes().first().size)
        assertNotNull(dao.getUnifiedMistakeByKey("BCS_BCS_50TH_PRELIM_2025_new_bcs_q_abort"))
        assertEquals(null, dao.getUnifiedMistakeByKey("QUIZ_NEW_MISTAKE_1"))

        // Verify preserved 2-argument JVM overload delegates atomically too.
        val twoArgExam = newExam.copy(attemptId = "two_arg_overload_exam", completedAt = nowMs + 5000L)
        repository.finalizeExamAttempt(twoArgExam, topicStats)
        assertNotNull(dao.getExamAttemptById("two_arg_overload_exam"))
        assertEquals(3, dao.getAllQuizScores().first().size)
    }

    @Test
    fun otherTransactionalWrites_rollbackCompletelyOnSqlTriggerFailure(): Unit = runBlocking {
        val nowMs = System.currentTimeMillis()
        seedExistingBaselineRows(nowMs)
        val wdb = db.openHelper.writableDatabase

        // 1) recordSessionAndAnalytics rollback
        wdb.execSQL(
            """
            CREATE TRIGGER abort_session_after_partial_writes
            BEFORE INSERT ON daily_study_activity
            BEGIN
                SELECT RAISE(ABORT, 'Session mid-transaction failure');
            END;
            """.trimIndent()
        )
        var sessionFailed = false
        try {
            repository.recordSessionAndAnalytics(
                module = "PRACTICE_LAB",
                topicId = "geometry",
                topicTitleBn = "জ্যামিতি",
                difficulty = "medium",
                mode = "fixed",
                score = 50,
                correctCount = 5,
                wrongCount = 0,
                totalQuestions = 5,
                bestStreak = 5,
                durationSeconds = 30
            )
        } catch (ex: Exception) {
            sessionFailed = ex.message?.contains("Session mid-transaction failure") == true
        } finally {
            wdb.execSQL("DROP TRIGGER IF EXISTS abort_session_after_partial_writes")
        }
        assertTrue(sessionFailed)
        assertEquals(1, dao.getAllPracticeHistory().first().size)
        assertEquals(null, dao.getTopicAnalyticsByKey("PRACTICE_LAB_geometry_medium"))

        // 2) recordBookQuestionMistake rollback
        wdb.execSQL(
            """
            CREATE TRIGGER abort_book_mistake_partial_writes
            BEFORE INSERT ON unified_mistake_notebook
            BEGIN
                SELECT RAISE(ABORT, 'Book mistake mid-transaction failure');
            END;
            """.trimIndent()
        )
        var bookFailed = false
        try {
            repository.recordBookQuestionMistake(
                questionId = "mistake_roll_book_99",
                bookId = "class_8",
                chapterNumber = 3,
                questionPromptBn = "ভুল প্রশ্ন",
                wrongAnswerBn = "১০",
                correctAnswerBn = "২০",
                explanationBn = "সঠিক ২০",
                sourceProvenance = "GENERATED",
                chapterTitleBn = "অধ্যায় ৩"
            )
        } catch (ex: Exception) {
            bookFailed = ex.message?.contains("Book mistake mid-transaction failure") == true
        } finally {
            wdb.execSQL("DROP TRIGGER IF EXISTS abort_book_mistake_partial_writes")
        }
        assertTrue(bookFailed)
        assertEquals(1, dao.getAllBookMistakes().first().size)
        assertEquals(1, dao.getAllBookSpacedRevision().first().size)
        assertEquals(1, dao.getAllUnifiedMistakes().first().size)

        // 3) recordBcsMistake rollback
        wdb.execSQL(
            """
            CREATE TRIGGER abort_bcs_mistake_partial_writes
            BEFORE INSERT ON unified_mistake_notebook
            BEGIN
                SELECT RAISE(ABORT, 'BCS mistake mid-transaction failure');
            END;
            """.trimIndent()
        )
        var bcsFailed = false
        try {
            repository.recordBcsMistake(
                questionId = "roll_bcs_77",
                subjectTrack = "MATH_REASONING",
                topicId = "probability",
                questionPromptBn = "সম্ভাবনা প্রশ্ন",
                wrongAnswerBn = "১/২",
                correctAnswerBn = "১/৪",
                explanationBn = "সঠিক ১/৪"
            )
        } catch (ex: Exception) {
            bcsFailed = ex.message?.contains("BCS mistake mid-transaction failure") == true
        } finally {
            wdb.execSQL("DROP TRIGGER IF EXISTS abort_bcs_mistake_partial_writes")
        }
        assertTrue(bcsFailed)
        assertEquals(1, dao.getAllBcsMistakes().first().size)
        assertEquals(1, dao.getAllUnifiedMistakes().first().size)
    }

    @Test
    fun queryCallback_measuresSqlStatementsAndSingleTopLevelTransactionBoundary(): Unit = runBlocking {
        val sqlStatements = ConcurrentLinkedQueue<String>()
        val queryExecutor = Executors.newSingleThreadExecutor()
        val instrumentedDb = Room.inMemoryDatabaseBuilder(app, MathDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryCallback(
                { sqlQuery, _ -> sqlStatements.add(sqlQuery.trim()) },
                queryExecutor
            )
            .build()

        try {
            val instrumentedRepo = MathRepository(instrumentedDb.mathDao())
            val now = System.currentTimeMillis()
            val stats = (1..10).map { idx ->
                TopicExamStat("topic_$idx", "টপিক $idx", 3, 2, 1, 0, 15)
            }
            val outcomes = (1..10).map { idx ->
                ExamQuestionMistakeOutcome(
                    unifiedMistakeKey = "Q_BOUNDARY_$idx",
                    sourceModule = "QUIZ",
                    subjectId = "topic_$idx",
                    curriculumTrack = "NCTB",
                    questionPromptBn = "প্রশ্ন $idx",
                    userAnswerBn = "ক",
                    correctAnswerBn = "খ",
                    diagnosticExplanationBn = "ব্যাখ্যা",
                    isCorrect = false,
                    shouldRecordMistake = true,
                    isBcsSession = false,
                    bcsQuestionId = "",
                    bcsTopicId = "topic_$idx",
                    examProfileId = ""
                )
            }
            val attempt = ExamAttemptEntity(
                attemptId = "boundary_measure_1",
                examTitleBn = "পরিমাপ পরীক্ষা",
                examType = "MIXED_TEST",
                selectedTopicsCsv = "topic_1",
                difficulty = "medium",
                questionType = "mcq",
                totalQuestions = 30,
                durationLimitSeconds = 600,
                marksPerCorrect = 1.0,
                negativeMarksPerWrong = 0.0,
                status = "COMPLETED",
                currentQuestionIndex = 29,
                elapsedSeconds = 150,
                remainingSeconds = 450,
                questionsJson = "[]",
                answersJson = "{}",
                markedForReviewCsv = "",
                visitedIndicesCsv = "",
                questionTimeSecondsJson = "{}",
                correctCount = 20,
                wrongCount = 10,
                unansweredCount = 0,
                earnedMarks = 20.0,
                negativeMarksDeducted = 0.0,
                finalScore = 20.0,
                percentage = 66.6,
                accuracyPercent = 66.6,
                startedAt = now - 150_000L,
                updatedAt = now,
                completedAt = now
            )

            // Warm up Room schema creation & InvalidationTracker before capturing finalizeExamAttempt SQL.
            instrumentedDb.openHelper.writableDatabase
            queryExecutor.submit { }.get()
            sqlStatements.clear()
            instrumentedRepo.finalizeExamAttempt(
                completedAttempt = attempt,
                perTopicStats = stats,
                bcsTopicUpdates = emptyList(),
                questionMistakeOutcomes = outcomes
            )
            // Drain single-thread callback executor.
            queryExecutor.submit { }.get()

            val snapshot = sqlStatements.toList()
            val topLevelTxBlocks = mutableListOf<List<String>>()
            var currentBlock: MutableList<String>? = null
            var txDepth = 0
            var nestedBeginCount = 0
            snapshot.forEach { sql ->
                when {
                    sql.startsWith("BEGIN", ignoreCase = true) -> {
                        if (txDepth == 0) {
                            currentBlock = mutableListOf(sql)
                        } else {
                            nestedBeginCount++
                            currentBlock?.add(sql)
                        }
                        txDepth++
                    }
                    sql.equals("COMMIT", ignoreCase = true) ||
                        sql.startsWith("END", ignoreCase = true) ||
                        sql.startsWith("ROLLBACK", ignoreCase = true) -> {
                        currentBlock?.add(sql)
                        if (txDepth > 0) txDepth--
                        if (txDepth == 0 && currentBlock != null) {
                            topLevelTxBlocks.add(currentBlock!!.toList())
                            currentBlock = null
                        }
                    }
                    else -> currentBlock?.add(sql)
                }
            }

            // Exclude Room's internal InvalidationTracker bookkeeping transactions (touching only room_table_modification_*).
            val appTxBlocks = topLevelTxBlocks.filter { block ->
                block.any { stmt ->
                    !stmt.startsWith("BEGIN", ignoreCase = true) &&
                        !stmt.startsWith("END", ignoreCase = true) &&
                        !stmt.equals("COMMIT", ignoreCase = true) &&
                        !stmt.equals("TRANSACTION SUCCESSFUL", ignoreCase = true) &&
                        !stmt.contains("room_table_modification_", ignoreCase = true)
                }
            }

            val appDmlOrSelectCount = snapshot.count {
                !it.contains("room_table_modification_", ignoreCase = true) && (
                    it.startsWith("SELECT", ignoreCase = true) ||
                        it.startsWith("INSERT", ignoreCase = true) ||
                        it.startsWith("UPDATE", ignoreCase = true) ||
                        it.startsWith("DELETE", ignoreCase = true)
                    )
            }

            assertTrue("Expected multiple application DML/SELECT SQL statements recorded, got $appDmlOrSelectCount", appDmlOrSelectCount >= 6)
            assertEquals(
                "All finalizeExamAttempt writes must execute inside 1 top-level SQLite transaction (nested=$nestedBeginCount, blocks=${appTxBlocks.size})",
                1,
                appTxBlocks.size
            )
            val singleTxText = appTxBlocks.first().joinToString("\n")
            assertTrue(singleTxText.contains("exam_attempts"))
            assertTrue(singleTxText.contains("quiz_scores"))
            assertTrue(singleTxText.contains("topic_analytics"))
            assertTrue(singleTxText.contains("practice_history"))
            assertTrue(singleTxText.contains("daily_study_activity"))
            assertTrue(singleTxText.contains("unified_mistake_notebook"))
            assertEquals("Top-level transaction depth must return to 0 on exit", 0, txDepth)
        } finally {
            queryExecutor.shutdownNow()
            instrumentedDb.close()
        }
    }

    @Test
    fun unboundedHistoryBeyond500AndEscapedLearnedTableCount(): Unit = runBlocking {
        val baseTime = System.currentTimeMillis() - 1_000_000L
        for (i in 1..650) {
            dao.insertPracticeHistory(
                PracticeHistoryEntity(
                    module = "PRACTICE_LAB",
                    topicId = "topic_${i % 5}",
                    topicTitleBn = "অনুশীলন $i",
                    difficulty = "easy",
                    mode = "fixed",
                    score = 100,
                    correctCount = 5,
                    wrongCount = 0,
                    totalQuestions = 5,
                    accuracyPercent = 100,
                    bestStreak = 5,
                    durationSeconds = 20,
                    timestamp = baseTime + i
                )
            )
            dao.insertQuizScore(
                QuizScoreEntity(
                    topic = "topic_${i % 5}",
                    topicTitleBn = "কুইজ $i",
                    score = 5,
                    totalQuestions = 5,
                    percentage = 100,
                    timestamp = baseTime + i
                )
            )
            dao.upsertUnifiedMistake(
                UnifiedMistakeEntity(
                    mistakeKey = "UNIFIED_650_$i",
                    sourceModule = "PRACTICE_LAB",
                    subjectId = "topic_${i % 5}",
                    curriculumTrack = "NCTB",
                    questionPromptBn = "প্রশ্ন $i",
                    userAnswerBn = "১",
                    correctAnswerBn = "২",
                    diagnosticExplanationBn = "ব্যাখ্যা $i",
                    lastFailedAtMillis = baseTime + i
                )
            )
        }

        // Insert 25 real learned tables ("table_1".."table_25") + non-table decoys including "tableX99" (LIKE wildcard trap!)
        for (t in 1..25) {
            dao.upsertProgress(ProgressEntity(itemId = "table_$t", isLearned = true))
        }
        dao.upsertProgress(ProgressEntity(itemId = "tableX99", isLearned = true))
        dao.upsertProgress(ProgressEntity(itemId = "formula_sq_1", isLearned = true))
        dao.upsertProgress(ProgressEntity(itemId = "table_unlearned", isLearned = false))

        val allPractice = repository.allPracticeHistory.first()
        val allQuizzes = repository.allQuizScores.first()
        val allMistakes = repository.allUnifiedMistakes.first()
        val quizCount = repository.quizScoresCount.first()
        val recentQuizzes = repository.getRecentQuizScores(5).first()
        val page1 = repository.getPracticeHistoryPage(limit = 50, offset = 600)
        val learnedTables = repository.learnedTablesCount.first()

        assertEquals(650, allPractice.size)
        assertEquals(650, allQuizzes.size)
        assertEquals(650, allMistakes.size)
        assertEquals(650, quizCount)
        assertEquals(5, recentQuizzes.size)
        assertEquals(50, page1.size)
        assertEquals("LIKE 'table\\_%' ESCAPE '\\' must exclude tableX99 and match exactly 25 rows", 25, learnedTables)

        val dashboardData = LearningAnalyticsEngine.buildDashboardAnalytics(
            timeFilter = AnalyticsTimeFilter.ALL_TIME,
            completedExams = emptyList(),
            activeExam = null,
            quizScores = allQuizzes,
            practiceHistory = allPractice,
            topicAnalytics = emptyList(),
            progressItems = dao.getAllProgress().first(),
            dailyActivities = emptyList(),
            nowMs = System.currentTimeMillis()
        )
        assertEquals(650, dashboardData.kpiSummary.totalPracticeSessions)
        assertEquals(650, dashboardData.kpiSummary.totalExamsCompleted)
        assertEquals(27, dashboardData.kpiSummary.learnedItemsCount)
    }

    // -----------------------------------------------------------------
    // A-04: BCS TOPIC PROGRESS CONCURRENCY & TRANSACTIONAL INTEGRITY
    // -----------------------------------------------------------------
    @Test
    fun a04_recordBcsTopicProgress_concurrentUpdatesSameTopicAndProfilePreserveAllAccumulations(): Unit = runBlocking {
        // Ensure Room schema is initialized before concurrent worker threads hit the transaction executor
        db.openHelper.writableDatabase

        val baseTimeMs = 1_710_000_000_000L
        val workerCount = 12
        val startLatch = java.util.concurrent.CountDownLatch(1)
        val readyLatch = java.util.concurrent.CountDownLatch(workerCount)
        val pool = Executors.newFixedThreadPool(workerCount)
        val dispatcher = pool.asCoroutineDispatcher()

        try {
            val deferreds = (1..workerCount).map { idx ->
                async(dispatcher) {
                    readyLatch.countDown()
                    assertTrue(
                        "Workers must reach start barrier within timeout",
                        startLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                    )
                    repository.recordBcsTopicProgress(
                        topicId = "bcs_math_real_numbers",
                        subjectTrack = "MATH_REASONING",
                        isGuideCompleted = idx == 5, // Only 1 worker marks guide completed
                        miniCheckPassed = idx == 9, // Only 1 worker marks mini-check passed
                        additionalTimeSeconds = 15,
                        questionsAttemptedDelta = 2,
                        questionsCorrectDelta = if (idx % 2 == 0) 2 else 1,
                        examProfileId = "BCS_50TH_PRELIM_2025",
                        nowMs = baseTimeMs + idx * 100L
                    )
                }
            }
            assertTrue(
                "All concurrent workers must be ready before release",
                readyLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
            )
            startLatch.countDown()
            deferreds.awaitAll()

            // Expected accumulated totals across 12 concurrent updates:
            // - attempted = 12 * 2 = 24
            // - correct = 6 * 2 + 6 * 1 = 18 (75% accuracy)
            // - totalTimeSeconds = 12 * 15 = 180
            // - isGuideCompleted = true (preserved once true even when subsequent workers pass false)
            // - miniCheckPassed = true (preserved once true even when subsequent workers pass false)
            // - masteryPercent = ((75 + 100) / 2).coerceIn(60, 100) = 87
            // - lastStudiedAt = max timestamp = baseTimeMs + 1200L
            val saved = dao.getBcsTopicProgressById("bcs_math_real_numbers")
            assertNotNull("Default profile row must exist under canonical topicId", saved)
            assertEquals(24, saved?.questionsAttempted)
            assertEquals(18, saved?.questionsCorrect)
            assertEquals(180, saved?.totalTimeSeconds)
            assertEquals(true, saved?.isGuideCompleted)
            assertEquals(true, saved?.miniCheckPassed)
            assertEquals(87, saved?.masteryPercent)
            assertEquals(baseTimeMs + 1200L, saved?.lastStudiedAt)
            assertEquals("BCS_50TH_PRELIM_2025", saved?.examProfileId)
        } finally {
            startLatch.countDown()
            runCatching { dispatcher.close() }
            pool.shutdownNow()
        }
    }

    @Test
    fun a04_recordBcsTopicProgress_concurrentMultiProfileMultiTopicIsolationAndDatabaseRecreationPersistence(): Unit = runBlocking {
        val dbFile = java.io.File(app.cacheDir, "a04_bcs_topic_concurrency_persistence.db")
        val dbWalFile = java.io.File(app.cacheDir, "a04_bcs_topic_concurrency_persistence.db-wal")
        val dbShmFile = java.io.File(app.cacheDir, "a04_bcs_topic_concurrency_persistence.db-shm")
        if (dbFile.exists()) dbFile.delete()
        if (dbWalFile.exists()) dbWalFile.delete()
        if (dbShmFile.exists()) dbShmFile.delete()

        val baseTimeMs = 1_720_000_000_000L
        val perGroupWorkers = 4
        val totalWorkers = perGroupWorkers * 3
        val pool = Executors.newFixedThreadPool(totalWorkers)
        val dispatcher = pool.asCoroutineDispatcher()

        val profile50 = "BCS_50TH_PRELIM_2025"
        val profile51 = "BCS_51ST_SPECIAL"
        val profileWritten = "BCS_WRITTEN_008_MATH"

        val topicReal = "bcs_math_real_numbers"
        val topicAlgebra = "bcs_math_algebra"

        try {
            val fileDb1 = Room.databaseBuilder(app, MathDatabase::class.java, dbFile.absolutePath)
                .allowMainThreadQueries()
                .build()
            try {
                // Warm up SQLite schema before releasing 12 concurrent worker threads
                fileDb1.openHelper.writableDatabase
                val repo1 = MathRepository(fileDb1.mathDao())
                val readyLatch = java.util.concurrent.CountDownLatch(totalWorkers)
                val startLatch = java.util.concurrent.CountDownLatch(1)

                val deferreds = mutableListOf<kotlinx.coroutines.Deferred<Unit>>()

                try {
                    // Group 1: Profile 50, topicReal (also passing scoped prefix in half the calls to test canonicalization)
                    for (i in 1..perGroupWorkers) {
                        deferreds += async(dispatcher) {
                            readyLatch.countDown()
                            assertTrue(
                                "Group 1 worker must reach start barrier within timeout",
                                startLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                            )
                            val inputTopic = if (i % 2 == 0) "$profile50::$topicReal" else topicReal
                            repo1.recordBcsTopicProgress(
                                topicId = inputTopic,
                                subjectTrack = "MATH_REASONING",
                                isGuideCompleted = i == 1,
                                miniCheckPassed = false,
                                additionalTimeSeconds = 20,
                                questionsAttemptedDelta = 3,
                                questionsCorrectDelta = 2,
                                examProfileId = profile50,
                                nowMs = baseTimeMs + i * 10L
                            )
                        }
                    }

                    // Group 2: Profile 51, SAME topicReal (must be isolated under BCS_51ST_SPECIAL::bcs_math_real_numbers)
                    for (i in 1..perGroupWorkers) {
                        deferreds += async(dispatcher) {
                            readyLatch.countDown()
                            assertTrue(
                                "Group 2 worker must reach start barrier within timeout",
                                startLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                            )
                            repo1.recordBcsTopicProgress(
                                topicId = topicReal,
                                subjectTrack = "MATH_REASONING",
                                isGuideCompleted = false,
                                miniCheckPassed = i == 2, // 1 worker passes mini-check with 0 explicit delta -> +1/+1
                                additionalTimeSeconds = 30,
                                questionsAttemptedDelta = if (i == 2) 0 else 4,
                                questionsCorrectDelta = if (i == 2) 0 else 3,
                                examProfileId = profile51,
                                nowMs = baseTimeMs + 1000L + i * 10L
                            )
                        }
                    }

                    // Group 3: Profile Written, different topicAlgebra
                    for (i in 1..perGroupWorkers) {
                        deferreds += async(dispatcher) {
                            readyLatch.countDown()
                            assertTrue(
                                "Group 3 worker must reach start barrier within timeout",
                                startLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                            )
                            repo1.recordBcsTopicProgress(
                                topicId = topicAlgebra,
                                subjectTrack = "MENTAL_ABILITY",
                                isGuideCompleted = i == 3,
                                miniCheckPassed = i == 4,
                                additionalTimeSeconds = 25,
                                questionsAttemptedDelta = 5,
                                questionsCorrectDelta = 5,
                                examProfileId = profileWritten,
                                nowMs = baseTimeMs + 2000L + i * 10L
                            )
                        }
                    }

                    assertTrue(
                        "All 12 workers across 3 profiles must be ready before release",
                        readyLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                    )
                    startLatch.countDown()
                    deferreds.awaitAll()
                } finally {
                    startLatch.countDown()
                }
            } finally {
                fileDb1.close()
            }

            // Re-open fresh Room database & repository instance from disk to verify durable persistence
            val fileDb2 = Room.databaseBuilder(app, MathDatabase::class.java, dbFile.absolutePath)
                .allowMainThreadQueries()
                .build()
            try {
                val dao2 = fileDb2.mathDao()
                val repo2 = MathRepository(dao2)

                val allRows = repo2.allBcsTopicProgress.first()
                assertEquals("Must persist exactly 3 distinct profile-scoped topic rows without duplicates", 3, allRows.size)

                // Verify Group 1 (Profile 50, topicReal): 4 workers * (+3 attempted, +2 correct, +20s)
                val row50Real = dao2.getBcsTopicProgressById(topicReal)
                assertNotNull(row50Real)
                assertEquals(12, row50Real?.questionsAttempted)
                assertEquals(8, row50Real?.questionsCorrect)
                assertEquals(80, row50Real?.totalTimeSeconds)
                assertEquals(true, row50Real?.isGuideCompleted)
                assertEquals(false, row50Real?.miniCheckPassed)
                assertEquals(75, row50Real?.masteryPercent) // guideDone=true, checkDone=false -> max(75, 66) = 75
                assertEquals(profile50, row50Real?.examProfileId)

                // Verify Group 2 (Profile 51, topicReal)
                val key51Real = BcsTopicProgressEntity.buildScopedKey(profile51, topicReal)
                val row51Real = dao2.getBcsTopicProgressById(key51Real)
                assertNotNull(row51Real)
                // 3 workers with +4/+3 and 1 worker with miniCheckPassed=true (+1/+1) -> 13 attempted, 10 correct
                assertEquals(13, row51Real?.questionsAttempted)
                assertEquals(10, row51Real?.questionsCorrect)
                assertEquals(120, row51Real?.totalTimeSeconds)
                assertEquals(false, row51Real?.isGuideCompleted)
                assertEquals(true, row51Real?.miniCheckPassed)
                assertEquals(profile51, row51Real?.examProfileId)

                // Verify Group 3 (Profile Written, topicAlgebra): 4 workers * (+5 attempted, +5 correct, +25s)
                val keyWrittenAlg = BcsTopicProgressEntity.buildScopedKey(profileWritten, topicAlgebra)
                val rowWrittenAlg = dao2.getBcsTopicProgressById(keyWrittenAlg)
                assertNotNull(rowWrittenAlg)
                assertEquals(20, rowWrittenAlg?.questionsAttempted)
                assertEquals(20, rowWrittenAlg?.questionsCorrect)
                assertEquals(100, rowWrittenAlg?.totalTimeSeconds)
                assertEquals(true, rowWrittenAlg?.isGuideCompleted)
                assertEquals(true, rowWrittenAlg?.miniCheckPassed)
                assertEquals(100, rowWrittenAlg?.masteryPercent)
                assertEquals("MENTAL_ABILITY", rowWrittenAlg?.subjectTrack)

                // Verify repeated guide-completion call on existing row is idempotent for completion flags
                // and preserves existing questionsAttempted, questionsCorrect, and subjectTrack
                repo2.recordBcsTopicProgress(
                    topicId = topicReal,
                    subjectTrack = "",
                    isGuideCompleted = false,
                    miniCheckPassed = false,
                    additionalTimeSeconds = 10,
                    questionsAttemptedDelta = 0,
                    questionsCorrectDelta = 0,
                    examProfileId = profile50,
                    nowMs = baseTimeMs + 20L // Older timestamp must not regress lastStudiedAt (max was baseTimeMs + 40L)
                )
                val afterRepeat = dao2.getBcsTopicProgressById(topicReal)
                assertEquals(12, afterRepeat?.questionsAttempted)
                assertEquals(8, afterRepeat?.questionsCorrect)
                assertEquals(90, afterRepeat?.totalTimeSeconds)
                assertEquals(true, afterRepeat?.isGuideCompleted)
                assertEquals("MATH_REASONING", afterRepeat?.subjectTrack)
                assertEquals(baseTimeMs + 40L, afterRepeat?.lastStudiedAt)
            } finally {
                fileDb2.close()
            }
        } finally {
            runCatching { dispatcher.close() }
            pool.shutdownNow()
            runCatching { dbFile.delete() }
            runCatching { dbWalFile.delete() }
            runCatching { dbShmFile.delete() }
        }
    }

    // -----------------------------------------------------------------
    // A-05: ATOMIC PRACTICE SESSION COMPLETION & IDEMPOTENCY (4 MODULES)
    // -----------------------------------------------------------------
    @Test
    fun a05_completePracticeSessionAtomically_allFourModulesPersistScoreHistoryAnalyticsAndRollbackCleanlyOnMidTransactionFailure(): Unit = runBlocking {
        val nowMs = 1_730_000_000_000L
        val dateKey = MathRepository.todayDateKey(nowMs)
        val wdb = db.openHelper.writableDatabase

        // 1. Install SQLite trigger on daily_study_activity (the final write in completePracticeSessionTransaction)
        // so that insertQuizScore, upsertProgress, insertPracticeHistory, and upsertTopicAnalytics execute first
        // and must ALL roll back when daily_study_activity aborts.
        wdb.execSQL(
            """
            CREATE TRIGGER abort_a05_after_quiz_score
            BEFORE INSERT ON daily_study_activity
            BEGIN
                SELECT RAISE(ABORT, 'A-05 mid-transaction failure after quiz_score and progress writes');
            END;
            """.trimIndent()
        )

        val moduleConfigs = listOf<suspend () -> Boolean>(
            // Practice Lab
            {
                repository.completePracticeSessionAtomically(
                    sessionId = "a05_pl_1",
                    quizScoreTopicId = "PRACTICE_fractions_easy",
                    quizScoreTitleBn = "প্র্যাকটিস: ভগ্নাংশ (সহজ)",
                    quizScoreValue = 8,
                    totalQuestions = 10,
                    percentage = 80,
                    learnedProgressItemId = "practice_fractions",
                    moduleType = "PRACTICE_LAB",
                    categoryId = "fractions",
                    categoryTitleBn = "ভগ্নাংশ",
                    difficulty = "easy",
                    mode = "fixed",
                    inputFormat = "mcq",
                    sessionScore = 80,
                    correctCount = 8,
                    wrongCount = 2,
                    durationSeconds = 45L,
                    highestStreak = 5,
                    nowMs = nowMs
                )
            },
            // Mental Math
            {
                repository.completePracticeSessionAtomically(
                    sessionId = "a05_mm_1",
                    quizScoreTopicId = "MENTAL_MATH_addition_medium",
                    quizScoreTitleBn = "মেন্টাল ম্যাথ: যোগ (মধ্যম)",
                    quizScoreValue = 145,
                    totalQuestions = 10,
                    percentage = 90,
                    learnedProgressItemId = "mental_math_addition",
                    moduleType = "MENTAL_MATH",
                    categoryId = "addition",
                    categoryTitleBn = "যোগ",
                    difficulty = "medium",
                    mode = "timed_sprint",
                    inputFormat = "keypad",
                    sessionScore = 145,
                    correctCount = 9,
                    wrongCount = 1,
                    durationSeconds = 60L,
                    highestStreak = 7,
                    nowMs = nowMs + 100L
                )
            },
            // Number System
            {
                repository.completePracticeSessionAtomically(
                    sessionId = "a05_ns_1",
                    quizScoreTopicId = "NUMBER_SYSTEM",
                    quizScoreTitleBn = "বাস্তব সংখ্যা ও বিভাজ্যতা (কঠিন)",
                    quizScoreValue = 7,
                    totalQuestions = 10,
                    percentage = 70,
                    learnedProgressItemId = null,
                    moduleType = "NUMBER_SYSTEM",
                    categoryId = "divisibility",
                    categoryTitleBn = "বাস্তব সংখ্যা ও বিভাজ্যতা (কঠিন)",
                    difficulty = "hard",
                    mode = "practice",
                    inputFormat = "mcq",
                    sessionScore = 70,
                    correctCount = 7,
                    wrongCount = 3,
                    durationSeconds = 50L,
                    highestStreak = 4,
                    nowMs = nowMs + 200L
                )
            },
            // Book Practice
            {
                repository.completePracticeSessionAtomically(
                    sessionId = "a05_book_1",
                    quizScoreTopicId = "BOOK_class_9_10_C1",
                    quizScoreTitleBn = "নবম-দশম শ্রেণি (অধ্যায় ১ • সাধারণ অনুশীলন)",
                    quizScoreValue = 9,
                    totalQuestions = 10,
                    percentage = 90,
                    learnedProgressItemId = null,
                    moduleType = "BOOK",
                    categoryId = "BOOK_class_9_10_C1",
                    categoryTitleBn = "নবম-দশম শ্রেণি (অধ্যায় ১ • সাধারণ অনুশীলন)",
                    difficulty = "standard",
                    mode = "standard",
                    inputFormat = "MULTI_FORMAT",
                    sessionScore = 90,
                    correctCount = 9,
                    wrongCount = 1,
                    durationSeconds = 60L,
                    highestStreak = 9,
                    nowMs = nowMs + 300L
                )
            }
        )

        try {
            for (attempt in moduleConfigs) {
                var aborted = false
                try {
                    attempt()
                } catch (e: Exception) {
                    aborted = e.message?.contains("A-05 mid-transaction failure") == true
                }
                assertTrue("Mid-transaction trigger must abort completion call", aborted)
            }
        } finally {
            wdb.execSQL("DROP TRIGGER IF EXISTS abort_a05_after_quiz_score")
        }

        // Verify ZERO partial state leaked into any table after rollback across all 4 modules
        assertEquals("No partial quiz_scores row may survive rollback", 0, dao.getAllQuizScores().first().size)
        assertEquals("No partial user_progress row may survive rollback", 0, dao.getAllProgress().first().size)
        assertEquals("No partial practice_history row may survive rollback", 0, dao.getAllPracticeHistory().first().size)
        assertEquals("No partial topic_analytics row may survive rollback", 0, dao.getAllTopicAnalytics().first().size)
        assertEquals("No partial daily_study_activity row may survive rollback", null, dao.getDailyStudyActivityByDate(dateKey))
        assertEquals("No rolled-back session receipt may block retry", null, dao.getProgressByItemId("__session_receipt__a05_pl_1"))

        // 2. Retry the exact same 4 session completions after trigger removal -> all 4 must succeed and commit atomically
        for (attempt in moduleConfigs) {
            assertTrue("Retry after clean rollback must commit", attempt())
        }

        val scores = dao.getAllQuizScores().first()
        val history = dao.getAllPracticeHistory().first()
        val analytics = dao.getAllTopicAnalytics().first()
        val progress = dao.getAllProgress().first()
        val daily = dao.getDailyStudyActivityByDate(dateKey)

        assertEquals(4, scores.size)
        assertEquals(4, history.size)
        assertEquals(4, analytics.size)
        // Only the 2 real learned items (Practice Lab & Mental Math) appear in getAllProgress(); session receipts are excluded
        assertEquals(2, progress.size)
        assertTrue(progress.any { it.itemId == "practice_fractions" && it.isLearned })
        assertTrue(progress.any { it.itemId == "mental_math_addition" && it.isLearned })

        assertNotNull(daily)
        assertEquals(4, daily?.practiceSessionsCompleted)
        assertEquals(0, daily?.examsCompleted)
        assertEquals(40, daily?.questionsAttempted)
        assertEquals(33, daily?.correctCount)
        assertEquals(7, daily?.wrongCount)
        assertEquals(215, daily?.studyTimeSeconds)
    }

    @Test
    fun a05_completePracticeSessionAtomically_duplicateAndConcurrentCompletionsAndDatabaseRecreationIdempotency(): Unit = runBlocking {
        val dbFile = java.io.File(app.cacheDir, "a05_session_completion_idempotency.db")
        val dbWalFile = java.io.File(app.cacheDir, "a05_session_completion_idempotency.db-wal")
        val dbShmFile = java.io.File(app.cacheDir, "a05_session_completion_idempotency.db-shm")
        if (dbFile.exists()) dbFile.delete()
        if (dbWalFile.exists()) dbWalFile.delete()
        if (dbShmFile.exists()) dbShmFile.delete()

        val nowMs = 1_735_000_000_000L
        val dateKey = MathRepository.todayDateKey(nowMs)
        val workerCount = 12
        val pool = Executors.newFixedThreadPool(workerCount)
        val dispatcher = pool.asCoroutineDispatcher()

        try {
            val fileDb1 = Room.databaseBuilder(app, MathDatabase::class.java, dbFile.absolutePath)
                .allowMainThreadQueries()
                .build()
            try {
                fileDb1.openHelper.writableDatabase
                val repo1 = MathRepository(fileDb1.mathDao())
                val readyLatch = java.util.concurrent.CountDownLatch(workerCount)
                val startLatch = java.util.concurrent.CountDownLatch(1)

                // 12 concurrent threads attempt to complete the EXACT SAME sessionId simultaneously
                val deferredResults = (1..workerCount).map { idx ->
                    async(dispatcher) {
                        readyLatch.countDown()
                        assertTrue(
                            "Worker must reach start barrier within timeout",
                            startLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                        )
                        repo1.completePracticeSessionAtomically(
                            sessionId = "shared_pl_session_99",
                            quizScoreTopicId = "PRACTICE_algebra_medium",
                            quizScoreTitleBn = "প্র্যাকটিস: বীজগণিত (মধ্যম)",
                            quizScoreValue = 9,
                            totalQuestions = 10,
                            percentage = 90,
                            learnedProgressItemId = "practice_algebra",
                            moduleType = "PRACTICE_LAB",
                            categoryId = "algebra",
                            categoryTitleBn = "বীজগণিত",
                            difficulty = "medium",
                            mode = "fixed",
                            inputFormat = "mcq",
                            sessionScore = 90,
                            correctCount = 9,
                            wrongCount = 1,
                            durationSeconds = 40L,
                            highestStreak = 6,
                            nowMs = nowMs + idx
                        )
                    }
                }
                assertTrue(
                    "All 12 concurrent workers must be ready before release",
                    readyLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)
                )
                startLatch.countDown()
                val outcomes = deferredResults.awaitAll()

                assertEquals("Strictly 1 concurrent completion for the same sessionId must commit", 1, outcomes.count { it })
                assertEquals("All remaining 11 duplicate concurrent completions must be no-ops", workerCount - 1, outcomes.count { !it })
            } finally {
                fileDb1.close()
            }

            // Re-open fresh Room database & repository instance from disk to verify persistence and durable idempotency
            val fileDb2 = Room.databaseBuilder(app, MathDatabase::class.java, dbFile.absolutePath)
                .allowMainThreadQueries()
                .build()
            try {
                val dao2 = fileDb2.mathDao()
                val repo2 = MathRepository(dao2)

                assertEquals(1, dao2.getAllQuizScores().first().size)
                assertEquals(1, dao2.getAllPracticeHistory().first().size)
                assertEquals(1, dao2.getAllProgress().first().size)
                val topicAfterConcurrent = dao2.getTopicAnalyticsByKey("PRACTICE_LAB_algebra_medium")
                assertNotNull(topicAfterConcurrent)
                assertEquals(10, topicAfterConcurrent?.totalAttempted)
                assertEquals(9, topicAfterConcurrent?.totalCorrect)
                assertEquals(1, topicAfterConcurrent?.totalWrong)

                val dailyAfterConcurrent = dao2.getDailyStudyActivityByDate(dateKey)
                assertNotNull(dailyAfterConcurrent)
                assertEquals(1, dailyAfterConcurrent?.practiceSessionsCompleted)
                assertEquals(10, dailyAfterConcurrent?.questionsAttempted)

                // Duplicate completion on recreated repository with same sessionId must still be rejected
                val dupAfterReopen = repo2.completePracticeSessionAtomically(
                    sessionId = "shared_pl_session_99",
                    quizScoreTopicId = "PRACTICE_algebra_medium",
                    quizScoreTitleBn = "প্র্যাকটিস: বীজগণিত (মধ্যম)",
                    quizScoreValue = 9,
                    totalQuestions = 10,
                    percentage = 90,
                    learnedProgressItemId = "practice_algebra",
                    moduleType = "PRACTICE_LAB",
                    categoryId = "algebra",
                    categoryTitleBn = "বীজগণিত",
                    difficulty = "medium",
                    mode = "fixed",
                    inputFormat = "mcq",
                    sessionScore = 90,
                    correctCount = 9,
                    wrongCount = 1,
                    durationSeconds = 40L,
                    highestStreak = 6,
                    nowMs = nowMs + 5000L
                )
                assertEquals(false, dupAfterReopen)
                assertEquals(1, dao2.getAllQuizScores().first().size)
                assertEquals(1, dao2.getAllPracticeHistory().first().size)
                assertEquals(10, dao2.getTopicAnalyticsByKey("PRACTICE_LAB_algebra_medium")?.totalAttempted)

                // Legitimate separate session with a new sessionId must commit and accumulate normally
                val distinctCommitted = repo2.completePracticeSessionAtomically(
                    sessionId = "distinct_pl_session_100",
                    quizScoreTopicId = "PRACTICE_algebra_medium",
                    quizScoreTitleBn = "প্র্যাকটিস: বীজগণিত (মধ্যম)",
                    quizScoreValue = 7,
                    totalQuestions = 10,
                    percentage = 70,
                    learnedProgressItemId = "practice_algebra",
                    moduleType = "PRACTICE_LAB",
                    categoryId = "algebra",
                    categoryTitleBn = "বীজগণিত",
                    difficulty = "medium",
                    mode = "fixed",
                    inputFormat = "mcq",
                    sessionScore = 70,
                    correctCount = 7,
                    wrongCount = 3,
                    durationSeconds = 35L,
                    highestStreak = 4,
                    nowMs = nowMs + 6000L
                )
                assertEquals(true, distinctCommitted)
                assertEquals(2, dao2.getAllQuizScores().first().size)
                assertEquals(2, dao2.getAllPracticeHistory().first().size)
                assertEquals(20, dao2.getTopicAnalyticsByKey("PRACTICE_LAB_algebra_medium")?.totalAttempted)
                assertEquals(16, dao2.getTopicAnalyticsByKey("PRACTICE_LAB_algebra_medium")?.totalCorrect)
                assertEquals(2, dao2.getDailyStudyActivityByDate(dateKey)?.practiceSessionsCompleted)
            } finally {
                fileDb2.close()
            }
        } finally {
            runCatching { dispatcher.close() }
            pool.shutdownNow()
            runCatching { dbFile.delete() }
            runCatching { dbWalFile.delete() }
            runCatching { dbShmFile.delete() }
        }
    }

    @Test
    fun a05_viewModelFourModules_practiceLabMentalMathNumberSystemAndBookPracticeCommitAtomicallyOnce() {
        val singletonDb = MathDatabase.getInstance(app)
        runBlocking(Dispatchers.IO) {
            singletonDb.clearAllTables()
        }
        val singletonDao = singletonDb.mathDao()
        val vm = com.example.ui.MathViewModel(app, savedStateHandle = androidx.lifecycle.SavedStateHandle())

        fun waitUntilDbCondition(timeoutMs: Long = 10_000L, condition: suspend () -> Boolean) {
            val deadline = System.nanoTime() + java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(timeoutMs)
            while (true) {
                org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
                if (runBlocking(Dispatchers.IO) { condition() }) break
                if (System.nanoTime() > deadline) {
                    throw AssertionError("Timed out after ${timeoutMs}ms waiting for DB condition")
                }
                Thread.sleep(15)
            }
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        }

        try {
            // 1. Practice Lab completion (with repeated finishPracticeSessionNow calls)
            vm.startPracticeSession(
                category = com.example.math.PracticeCategory.FRACTIONS,
                difficulty = com.example.math.PracticeDifficulty.EASY,
                count = 5,
                inputFormat = com.example.math.QuestionInputFormat.MCQ,
                isUnlimitedMode = false
            )
            val plQ0 = vm.practiceSession.value.questions.first()
            vm.selectPracticeOption(plQ0.correctIndex)
            vm.submitPracticeAnswer()
            repeat(4) { vm.finishPracticeSessionNow() }

            waitUntilDbCondition {
                singletonDao.getAllPracticeHistory().first().any { it.module == "PRACTICE_LAB" }
            }

            // 2. Mental Math completion (with repeated finishMentalMathSession calls)
            vm.startMentalMathChallenge(
                category = com.example.math.MentalMathCategory.ADDITION,
                difficulty = com.example.math.MentalMathDifficulty.EASY,
                mode = com.example.math.MentalMathMode.FIXED_QUESTIONS,
                durationOrCount = 5,
                inputFormat = com.example.math.QuestionInputFormat.MCQ
            )
            val mmQ0 = vm.mentalMathSession.value.questions.first()
            vm.submitMentalMathAnswer(mmQ0.correctAnswer)
            repeat(4) { vm.finishMentalMathSession() }

            waitUntilDbCondition {
                singletonDao.getAllPracticeHistory().first().any { it.module == "MENTAL_MATH" }
            }

            // 3. Number System completion (complete all questions + repeated nextNumberSystemQuestion calls)
            vm.startNumberSystemPractice(
                topic = com.example.math.NumberSystemTopic.DIVISIBILITY,
                difficulty = com.example.math.NumberSystemDifficulty.EASY,
                inputFormat = com.example.math.QuestionInputFormat.MCQ
            )
            val nsTotal = vm.numberSystemPracticeState.value.questions.size
            for (idx in 0 until nsTotal) {
                val q = vm.numberSystemPracticeState.value.questions[idx]
                vm.selectNumberSystemOption(q.correctIndex)
                vm.submitNumberSystemAnswer()
                if (idx < nsTotal - 1) {
                    vm.nextNumberSystemQuestion()
                }
            }
            repeat(4) { vm.nextNumberSystemQuestion() }

            waitUntilDbCondition {
                singletonDao.getAllPracticeHistory().first().any { it.module == "NUMBER_SYSTEM" }
            }

            // 4. Book Practice completion (with repeated finishBookPracticeSession calls)
            vm.startBookPractice(
                bookId = com.example.math.BookId.CLASS_6,
                chapterNumber = 1,
                mode = com.example.math.BookPracticeMode.STANDARD,
                questionCount = 5
            )
            val bookQ0 = vm.bookPracticeSession.value.questions.first()
            if (bookQ0.correctOptionIndices.isNotEmpty()) {
                bookQ0.correctOptionIndices.forEach { vm.toggleBookPracticeOption(it) }
            } else {
                vm.setBookPracticeTextInput(bookQ0.correctTextAnswer)
            }
            vm.submitBookPracticeAnswer()
            repeat(4) { vm.finishBookPracticeSession() }

            waitUntilDbCondition {
                singletonDao.getAllPracticeHistory().first().any { it.module == "BOOK" }
            }

            runBlocking(Dispatchers.IO) {
                val allScores = singletonDao.getAllQuizScores().first()
                val allHistory = singletonDao.getAllPracticeHistory().first()
                val allAnalytics = singletonDao.getAllTopicAnalytics().first()
                val allProgress = singletonDao.getAllProgress().first()

                assertEquals("Strictly 4 QuizScoreEntity rows (1 per module)", 4, allScores.size)
                assertEquals("Strictly 4 PracticeHistoryEntity rows (1 per module)", 4, allHistory.size)
                assertEquals(
                    setOf("PRACTICE_LAB", "MENTAL_MATH", "NUMBER_SYSTEM", "BOOK"),
                    allHistory.map { it.module }.toSet()
                )
                assertEquals("Strictly 4 TopicAnalyticsEntity rows (1 per module)", 4, allAnalytics.size)
                assertTrue(allProgress.any { it.itemId == "practice_fractions" && it.isLearned })
                assertTrue(allProgress.any { it.itemId == "mental_math_addition" && it.isLearned })
            }
        } finally {
            runBlocking(Dispatchers.IO) {
                singletonDb.clearAllTables()
            }
        }
    }
}

