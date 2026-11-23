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
import kotlinx.coroutines.flow.first
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
}
