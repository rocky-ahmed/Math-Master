package com.example.data.repository

import com.example.data.db.MathDao
import com.example.data.model.BookLessonProgressEntity
import com.example.data.model.BookMistakeEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.BookReadingStateEntity
import com.example.data.model.BookSpacedRevisionEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.FormulaMasteryEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.RecentFormulaEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.model.TopicAnalyticsEntity
import com.example.data.model.UnifiedMistakeEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

class MathRepository(private val dao: MathDao) {

    val allBookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val allQuizScores: Flow<List<QuizScoreEntity>> = dao.getAllQuizScores()
    val allProgress: Flow<List<ProgressEntity>> = dao.getAllProgress()
    val allPracticeHistory: Flow<List<PracticeHistoryEntity>> = dao.getAllPracticeHistory()
    val allTopicAnalytics: Flow<List<TopicAnalyticsEntity>> = dao.getAllTopicAnalytics()
    val recentFormulas: Flow<List<RecentFormulaEntity>> = dao.getRecentFormulas()
    val allExamAttempts: Flow<List<ExamAttemptEntity>> = dao.getAllExamAttempts()
    val completedExamAttempts: Flow<List<ExamAttemptEntity>> = dao.getCompletedExamAttempts()
    val activeExamAttempt: Flow<ExamAttemptEntity?> = dao.getActiveExamAttemptFlow()
    val allSolverHistory: Flow<List<SolverHistoryEntity>> = dao.getAllSolverHistory()
    val allDailyStudyActivity: Flow<List<DailyStudyActivityEntity>> = dao.getAllDailyStudyActivity()

    // Book Module Flows (Room v5)
    val allBookLessonProgress: Flow<List<BookLessonProgressEntity>> = dao.getAllBookLessonProgress()
    val allBookReadingStates: Flow<List<BookReadingStateEntity>> = dao.getAllBookReadingStates()
    val allBookNotes: Flow<List<BookNoteEntity>> = dao.getAllBookNotes()
    val allBookMistakes: Flow<List<BookMistakeEntity>> = dao.getAllBookMistakes()
    val allBookSpacedRevision: Flow<List<BookSpacedRevisionEntity>> = dao.getAllBookSpacedRevision()
    val allBookSpacedRevisions: Flow<List<BookSpacedRevisionEntity>> get() = allBookSpacedRevision

    // BCS Module Flows (Room v6 / v8)
    val bcsUserProfile: Flow<com.example.data.model.BcsUserProfileEntity?> = dao.getBcsUserProfileFlow()
    val allBcsUserProfiles: Flow<List<com.example.data.model.BcsUserProfileEntity>> = dao.getAllBcsUserProfilesFlow()
    val allBcsTopicProgress: Flow<List<com.example.data.model.BcsTopicProgressEntity>> = dao.getAllBcsTopicProgress()
    val allBcsMistakes: Flow<List<com.example.data.model.BcsMistakeEntity>> = dao.getAllBcsMistakes()
    val allBcsStudyGoals: Flow<List<com.example.data.model.BcsStudyPlanGoalEntity>> = dao.getAllBcsStudyGoals()

    // Ecosystem Upgrade Flows (Room v7)
    val allUnifiedMistakes: Flow<List<UnifiedMistakeEntity>> = dao.getAllUnifiedMistakes()
    val allFormulaMastery: Flow<List<FormulaMasteryEntity>> = dao.getAllFormulaMastery()

    val bookmarks: Flow<List<BookmarkEntity>> get() = allBookmarks
    val quizScores: Flow<List<QuizScoreEntity>> get() = allQuizScores
    val progressList: Flow<List<ProgressEntity>> get() = allProgress

    companion object {
        // Default 1, 3, 7, and 14 days spaced revision intervals in milliseconds
        val SPACED_REVISION_INTERVAL_DAYS = listOf(1, 3, 7, 14)
        private const val DAY_MS = 24L * 60L * 60L * 1000L

        fun todayDateKey(timestampMs: Long = System.currentTimeMillis()): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date(timestampMs))
        }

        fun nextSpacedReviewEpochMs(intervalStage: Int, nowMs: Long = System.currentTimeMillis()): Long {
            val days = SPACED_REVISION_INTERVAL_DAYS.getOrElse(intervalStage.coerceIn(0, SPACED_REVISION_INTERVAL_DAYS.lastIndex)) { 14 }
            return nowMs + days * DAY_MS
        }
    }

    suspend fun toggleBookmark(bookmark: BookmarkEntity) {
        val exists = dao.isBookmarked(bookmark.id)
        if (exists) {
            dao.deleteBookmarkById(bookmark.id)
        } else {
            dao.insertBookmark(bookmark)
        }
    }

    suspend fun toggleBookmark(bookmark: BookmarkEntity, currentlyBookmarked: Boolean) {
        if (currentlyBookmarked) {
            dao.deleteBookmarkById(bookmark.id)
        } else {
            dao.insertBookmark(bookmark)
        }
    }

    suspend fun removeBookmark(id: String) {
        dao.deleteBookmarkById(id)
    }

    suspend fun recordQuizScore(score: QuizScoreEntity) {
        dao.insertQuizScore(score)
    }

    suspend fun saveQuizScore(
        topic: String,
        topicTitleBn: String,
        score: Int,
        totalQuestions: Int
    ) {
        val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        dao.insertQuizScore(
            QuizScoreEntity(
                topic = topic,
                topicTitleBn = topicTitleBn,
                score = score,
                totalQuestions = totalQuestions,
                percentage = percentage
            )
        )
    }

    suspend fun deleteQuizScore(id: Int) {
        dao.deleteQuizScoreById(id)
    }

    suspend fun setItemLearned(itemId: String, isLearned: Boolean) {
        dao.upsertProgress(ProgressEntity(itemId = itemId, isLearned = isLearned))
    }

    suspend fun toggleLearned(itemId: String, currentlyLearned: Boolean) {
        setItemLearned(itemId, !currentlyLearned)
    }

    suspend fun recordPractice(itemId: String) {
        setItemLearned(itemId, true)
    }

    suspend fun recordSessionAndAnalytics(
        module: String,
        topicId: String,
        topicTitleBn: String,
        difficulty: String,
        mode: String,
        score: Int,
        correctCount: Int,
        wrongCount: Int,
        totalQuestions: Int,
        bestStreak: Int,
        durationSeconds: Int
    ) {
        val accuracy = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
        val now = System.currentTimeMillis()
        dao.insertPracticeHistory(
            PracticeHistoryEntity(
                module = module,
                topicId = topicId,
                topicTitleBn = topicTitleBn,
                difficulty = difficulty,
                mode = mode,
                score = score,
                correctCount = correctCount,
                wrongCount = wrongCount,
                totalQuestions = totalQuestions,
                accuracyPercent = accuracy,
                bestStreak = bestStreak,
                durationSeconds = durationSeconds,
                timestamp = now
            )
        )

        upsertTopicMasteryDelta(
            module = module,
            topicId = topicId,
            topicTitleBn = topicTitleBn,
            difficulty = difficulty,
            attemptedDelta = correctCount + wrongCount,
            correctDelta = correctCount,
            wrongDelta = wrongCount,
            bestStreak = bestStreak,
            durationSeconds = durationSeconds,
            now = now
        )

        recordDailyStudyDelta(
            questionsAttemptedDelta = correctCount + wrongCount,
            correctDelta = correctCount,
            wrongDelta = wrongCount,
            studyTimeSecondsDelta = max(1, durationSeconds),
            examsCompletedDelta = if (module == "QUIZ") 1 else 0,
            practiceSessionsCompletedDelta = if (module != "QUIZ") 1 else 0,
            timestampMs = now
        )
    }

    suspend fun upsertTopicMasteryDelta(
        module: String,
        topicId: String,
        topicTitleBn: String,
        difficulty: String,
        attemptedDelta: Int,
        correctDelta: Int,
        wrongDelta: Int,
        bestStreak: Int,
        durationSeconds: Int,
        now: Long = System.currentTimeMillis()
    ) {
        val key = "${module}_${topicId}_${difficulty}"
        val existing = dao.getTopicAnalyticsByKey(key)
        val updated = if (existing == null) {
            TopicAnalyticsEntity(
                analyticsKey = key,
                module = module,
                topicId = topicId,
                topicTitleBn = topicTitleBn,
                difficulty = difficulty,
                totalAttempted = max(0, attemptedDelta),
                totalCorrect = max(0, correctDelta),
                totalWrong = max(0, wrongDelta),
                bestStreak = max(0, bestStreak),
                totalTimeSeconds = max(0, durationSeconds),
                lastPracticedAt = now
            )
        } else {
            existing.copy(
                totalAttempted = existing.totalAttempted + max(0, attemptedDelta),
                totalCorrect = existing.totalCorrect + max(0, correctDelta),
                totalWrong = existing.totalWrong + max(0, wrongDelta),
                bestStreak = max(existing.bestStreak, bestStreak),
                totalTimeSeconds = existing.totalTimeSeconds + max(0, durationSeconds),
                lastPracticedAt = now
            )
        }
        dao.upsertTopicAnalytics(updated)
    }

    suspend fun recordSessionAndAnalytics(
        moduleType: String,
        categoryId: String,
        categoryTitleBn: String,
        difficulty: String,
        mode: String,
        inputFormat: String = "mcq",
        score: Int,
        correctCount: Int,
        wrongCount: Int,
        totalQuestions: Int,
        accuracyPercent: Int = 0,
        durationSeconds: Long,
        highestStreak: Int
    ) {
        recordSessionAndAnalytics(
            module = moduleType,
            topicId = categoryId,
            topicTitleBn = categoryTitleBn,
            difficulty = difficulty,
            mode = "$mode:$inputFormat",
            score = score,
            correctCount = correctCount,
            wrongCount = wrongCount,
            totalQuestions = totalQuestions,
            bestStreak = highestStreak,
            durationSeconds = durationSeconds.toInt().coerceAtLeast(1)
        )
    }

    suspend fun recordFormulaViewed(
        formulaId: String,
        titleBn: String,
        subjectId: String = "",
        subjectName: String = ""
    ) {
        val resolvedSubject = subjectId.ifEmpty { subjectName }
        dao.upsertRecentFormula(
            RecentFormulaEntity(
                formulaId = formulaId,
                titleBn = titleBn,
                subjectId = resolvedSubject,
                viewedAt = System.currentTimeMillis()
            )
        )
    }

    // Exam Attempt Persistence (Room v4)
    suspend fun saveExamAttemptState(attempt: ExamAttemptEntity) {
        dao.upsertExamAttempt(attempt)
    }

    suspend fun getActiveExamAttemptSnapshot(): ExamAttemptEntity? {
        return dao.getActiveExamAttempt()
    }

    suspend fun getExamAttemptById(attemptId: String): ExamAttemptEntity? {
        return dao.getExamAttemptById(attemptId)
    }

    suspend fun abandonActiveExams() {
        dao.abandonActiveExamAttempts(System.currentTimeMillis())
    }

    suspend fun deleteExamAttempt(attemptId: String) {
        val existing = dao.getExamAttemptById(attemptId) ?: return
        val refTs = existing.completedAt ?: existing.updatedAt
        val dateKey = if (refTs > 0L) todayDateKey(refTs) else ""
        dao.deleteExamAttemptSafely(attemptId, dateKey)
    }

    suspend fun finalizeExamAttempt(
        completedAttempt: ExamAttemptEntity,
        perTopicStats: List<TopicExamStat>
    ) {
        val now = completedAttempt.completedAt ?: System.currentTimeMillis()
        val finalAttempt = if (completedAttempt.completedAt != null) {
            completedAttempt
        } else {
            completedAttempt.copy(completedAt = now)
        }
        dao.upsertExamAttempt(finalAttempt)

        // Also record into quiz_scores for backward compatibility with legacy consumers
        val roundedPct = finalAttempt.percentage.roundToInt().coerceIn(0, 100)
        dao.insertQuizScore(
            QuizScoreEntity(
                topic = finalAttempt.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" },
                topicTitleBn = finalAttempt.examTitleBn,
                score = finalAttempt.correctCount,
                totalQuestions = finalAttempt.totalQuestions,
                percentage = roundedPct,
                timestamp = now
            )
        )

        // Update per-topic analytics without inflating session count per topic
        if (perTopicStats.isNotEmpty()) {
            perTopicStats.forEach { stat ->
                upsertTopicMasteryDelta(
                    module = "QUIZ",
                    topicId = stat.topicId,
                    topicTitleBn = stat.topicTitleBn,
                    difficulty = completedAttempt.difficulty,
                    attemptedDelta = stat.correctCount + stat.wrongCount,
                    correctDelta = stat.correctCount,
                    wrongDelta = stat.wrongCount,
                    bestStreak = stat.correctCount,
                    durationSeconds = stat.timeSpentSeconds,
                    now = now
                )
            }
        } else {
            upsertTopicMasteryDelta(
                module = "QUIZ",
                topicId = completedAttempt.selectedTopicsCsv.ifBlank { "MIXED" },
                topicTitleBn = completedAttempt.examTitleBn,
                difficulty = completedAttempt.difficulty,
                attemptedDelta = completedAttempt.correctCount + completedAttempt.wrongCount,
                correctDelta = completedAttempt.correctCount,
                wrongDelta = completedAttempt.wrongCount,
                bestStreak = completedAttempt.correctCount,
                durationSeconds = completedAttempt.elapsedSeconds,
                now = now
            )
        }

        // Insert a single QUIZ practice_history entry for the exam attempt
        dao.insertPracticeHistory(
            PracticeHistoryEntity(
                module = "QUIZ",
                topicId = completedAttempt.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" },
                topicTitleBn = completedAttempt.examTitleBn,
                difficulty = completedAttempt.difficulty,
                mode = completedAttempt.examType,
                score = completedAttempt.finalScore.roundToInt(),
                correctCount = completedAttempt.correctCount,
                wrongCount = completedAttempt.wrongCount,
                totalQuestions = completedAttempt.totalQuestions,
                accuracyPercent = completedAttempt.accuracyPercent.roundToInt(),
                bestStreak = completedAttempt.correctCount,
                durationSeconds = completedAttempt.elapsedSeconds,
                timestamp = now
            )
        )

        recordDailyStudyDelta(
            questionsAttemptedDelta = completedAttempt.correctCount + completedAttempt.wrongCount,
            correctDelta = completedAttempt.correctCount,
            wrongDelta = completedAttempt.wrongCount,
            studyTimeSecondsDelta = max(1, completedAttempt.elapsedSeconds),
            examsCompletedDelta = 1,
            practiceSessionsCompletedDelta = 0,
            timestampMs = now
        )
    }

    // Solver History Persistence (Room v4)
    suspend fun saveSolvedProblemHistory(history: SolverHistoryEntity) {
        dao.insertSolverHistory(history)
    }

    suspend fun deleteSolvedProblemHistory(id: Int) {
        dao.deleteSolverHistoryById(id)
    }

    suspend fun clearSolvedProblemHistory() {
        dao.clearAllSolverHistory()
    }

    // Daily Study Goals & Activity (Room v4)
    suspend fun recordDailyStudyDelta(
        questionsAttemptedDelta: Int,
        correctDelta: Int,
        wrongDelta: Int,
        studyTimeSecondsDelta: Int,
        examsCompletedDelta: Int,
        practiceSessionsCompletedDelta: Int,
        timestampMs: Long = System.currentTimeMillis()
    ) {
        val dateKey = todayDateKey(timestampMs)
        val existing = dao.getDailyStudyActivityByDate(dateKey)
        val updated = if (existing == null) {
            DailyStudyActivityEntity(
                dateKey = dateKey,
                questionsAttempted = max(0, questionsAttemptedDelta),
                correctCount = max(0, correctDelta),
                wrongCount = max(0, wrongDelta),
                studyTimeSeconds = max(0, studyTimeSecondsDelta),
                examsCompleted = max(0, examsCompletedDelta),
                practiceSessionsCompleted = max(0, practiceSessionsCompletedDelta),
                dailyTargetQuestions = 20,
                dailyTargetMinutes = 15,
                updatedAt = timestampMs
            )
        } else {
            existing.copy(
                questionsAttempted = existing.questionsAttempted + max(0, questionsAttemptedDelta),
                correctCount = existing.correctCount + max(0, correctDelta),
                wrongCount = existing.wrongCount + max(0, wrongDelta),
                studyTimeSeconds = existing.studyTimeSeconds + max(0, studyTimeSecondsDelta),
                examsCompleted = existing.examsCompleted + max(0, examsCompletedDelta),
                practiceSessionsCompleted = existing.practiceSessionsCompleted + max(0, practiceSessionsCompletedDelta),
                updatedAt = timestampMs
            )
        }
        dao.upsertDailyStudyActivity(updated)
    }

    suspend fun updateDailyStudyTargets(
        targetQuestions: Int,
        targetMinutes: Int,
        timestampMs: Long = System.currentTimeMillis()
    ) {
        val safeQuestions = targetQuestions.coerceIn(5, 200)
        val safeMinutes = targetMinutes.coerceIn(5, 240)
        val dateKey = todayDateKey(timestampMs)
        val existing = dao.getDailyStudyActivityByDate(dateKey)
        val updated = if (existing == null) {
            DailyStudyActivityEntity(
                dateKey = dateKey,
                questionsAttempted = 0,
                correctCount = 0,
                wrongCount = 0,
                studyTimeSeconds = 0,
                examsCompleted = 0,
                practiceSessionsCompleted = 0,
                dailyTargetQuestions = safeQuestions,
                dailyTargetMinutes = safeMinutes,
                updatedAt = timestampMs
            )
        } else {
            existing.copy(
                dailyTargetQuestions = safeQuestions,
                dailyTargetMinutes = safeMinutes,
                updatedAt = timestampMs
            )
        }
        dao.upsertDailyStudyActivity(updated)
    }

    // ==========================================
    // BOOK MODULE PERSISTENCE (Room v5)
    // ==========================================
    suspend fun upsertBookLessonProgress(
        bookId: String,
        chapterNumber: Int,
        lessonId: String,
        isCompleted: Boolean,
        miniCheckPassed: Boolean = false,
        timeSpentDeltaSeconds: Int = 15,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val lessonKey = "$bookId:$chapterNumber:$lessonId"
        val existing = dao.getBookLessonProgressByKey(lessonKey)
        val updated = if (existing == null) {
            BookLessonProgressEntity(
                lessonKey = lessonKey,
                bookId = bookId,
                chapterNumber = chapterNumber,
                lessonId = lessonId,
                isCompleted = isCompleted,
                miniCheckPassed = miniCheckPassed,
                timeSpentSeconds = max(0, timeSpentDeltaSeconds),
                lastVisitedAt = nowMs
            )
        } else {
            existing.copy(
                isCompleted = isCompleted || existing.isCompleted,
                miniCheckPassed = miniCheckPassed || existing.miniCheckPassed,
                timeSpentSeconds = existing.timeSpentSeconds + max(0, timeSpentDeltaSeconds),
                lastVisitedAt = nowMs
            )
        }
        dao.upsertBookLessonProgress(updated)
        setItemLearned("book_lesson_$lessonKey", updated.isCompleted)
    }

    suspend fun toggleBookLessonCompleted(
        bookId: String,
        chapterNumber: Int,
        lessonId: String,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val lessonKey = "$bookId:$chapterNumber:$lessonId"
        val existing = dao.getBookLessonProgressByKey(lessonKey)
        val nextCompleted = !(existing?.isCompleted ?: false)
        val updated = BookLessonProgressEntity(
            lessonKey = lessonKey,
            bookId = bookId,
            chapterNumber = chapterNumber,
            lessonId = lessonId,
            isCompleted = nextCompleted,
            miniCheckPassed = existing?.miniCheckPassed ?: false,
            timeSpentSeconds = (existing?.timeSpentSeconds ?: 0) + 10,
            lastVisitedAt = nowMs
        )
        dao.upsertBookLessonProgress(updated)
        setItemLearned("book_lesson_$lessonKey", nextCompleted)
    }

    suspend fun updateBookReadingState(
        bookId: String,
        chapterNumber: Int? = null,
        lessonId: String? = null,
        practiceMode: String? = null,
        printedPage: Int? = null,
        pdfPage: Int? = null,
        incrementPagesRead: Boolean = false,
        preferredViewMode: String? = null,
        lastZoomScale: Float? = null,
        activeEditionId: String? = null,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val existing = dao.getBookReadingStateByBookId(bookId)
        val resolvedPrinted = printedPage ?: existing?.lastPrintedPage ?: 1
        val resolvedPdf = pdfPage ?: existing?.lastPdfPage ?: (resolvedPrinted + 5)
        val updated = if (existing == null) {
            BookReadingStateEntity(
                bookId = bookId,
                lastChapterNumber = chapterNumber ?: 1,
                lastLessonId = lessonId ?: "1.1",
                lastPracticeMode = practiceMode ?: "BASIC",
                lastPrintedPage = resolvedPrinted,
                lastPdfPage = resolvedPdf,
                totalPagesRead = if (incrementPagesRead) 1 else 0,
                bookmarkedPagesCsv = "",
                updatedAt = nowMs,
                preferredViewMode = preferredViewMode ?: "STRUCTURED",
                lastZoomScale = (lastZoomScale ?: 1.0f).coerceIn(1.0f, 3.0f),
                activeEditionId = activeEditionId ?: "NCTB_2026_STANDARD"
            )
        } else {
            existing.copy(
                lastChapterNumber = chapterNumber ?: existing.lastChapterNumber,
                lastLessonId = lessonId ?: existing.lastLessonId,
                lastPracticeMode = practiceMode ?: existing.lastPracticeMode,
                lastPrintedPage = resolvedPrinted,
                lastPdfPage = resolvedPdf,
                totalPagesRead = existing.totalPagesRead + (if (incrementPagesRead) 1 else 0),
                updatedAt = nowMs,
                preferredViewMode = preferredViewMode ?: existing.preferredViewMode,
                lastZoomScale = (lastZoomScale ?: existing.lastZoomScale).coerceIn(1.0f, 3.0f),
                activeEditionId = activeEditionId ?: existing.activeEditionId
            )
        }
        dao.upsertBookReadingState(updated)
    }

    suspend fun toggleBookPageBookmark(
        bookId: String,
        printedPage: Int,
        bookTitleBn: String,
        chapterTitleBn: String,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val existing = dao.getBookReadingStateByBookId(bookId)
        val currentPages = existing?.bookmarkedPagesCsv
            ?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() }
            ?.toMutableSet() ?: mutableSetOf()
        val wasBookmarked = currentPages.contains(printedPage)
        if (wasBookmarked) {
            currentPages.remove(printedPage)
        } else {
            currentPages.add(printedPage)
        }
        val csv = currentPages.sorted().joinToString(",")
        val nextState = (existing ?: BookReadingStateEntity(
            bookId = bookId,
            lastChapterNumber = 1,
            lastLessonId = "1.1",
            lastPracticeMode = "BASIC",
            lastPrintedPage = printedPage,
            lastPdfPage = printedPage + 5,
            totalPagesRead = 1
        )).copy(
            bookmarkedPagesCsv = csv,
            updatedAt = nowMs
        )
        dao.upsertBookReadingState(nextState)

        val bookmarkId = "book_page_${bookId}_$printedPage"
        if (wasBookmarked) {
            dao.deleteBookmarkById(bookmarkId)
        } else {
            dao.insertBookmark(
                BookmarkEntity(
                    id = bookmarkId,
                    category = "BOOK_PAGE",
                    titleBn = "$bookTitleBn • পৃষ্ঠা $printedPage",
                    subtitleBn = chapterTitleBn,
                    detailBn = "মূল পাঠ্যবইয়ের পৃষ্ঠা $printedPage (PDF পৃষ্ঠা ${printedPage + 5})",
                    timestamp = nowMs
                )
            )
        }
    }

    suspend fun saveBookNote(
        id: Int = 0,
        bookId: String,
        chapterNumber: Int,
        lessonId: String,
        printedPage: Int,
        titleBn: String,
        contentBn: String,
        nowMs: Long = System.currentTimeMillis()
    ) {
        dao.upsertBookNote(
            BookNoteEntity(
                id = id,
                bookId = bookId,
                chapterNumber = chapterNumber,
                lessonId = lessonId,
                printedPage = printedPage,
                titleBn = titleBn,
                contentBn = contentBn,
                createdAt = nowMs,
                updatedAt = nowMs
            )
        )
    }

    suspend fun deleteBookNote(id: Int) {
        dao.deleteBookNoteById(id)
    }

    suspend fun recordBookQuestionMistake(
        questionId: String,
        bookId: String,
        chapterNumber: Int,
        questionPromptBn: String,
        wrongAnswerBn: String,
        correctAnswerBn: String,
        explanationBn: String,
        sourceProvenance: String,
        chapterTitleBn: String,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val rawId = questionId.removePrefix("mistake_").removePrefix("mistake_")
        val revItemId = "mistake_$rawId"
        val existing = dao.getBookMistakeById(questionId)
            ?: dao.getBookMistakeById(revItemId)
            ?: dao.getBookMistakeById(rawId)
        val targetMistakeId = existing?.questionId ?: questionId
        val updatedMistake = if (existing == null) {
            BookMistakeEntity(
                questionId = targetMistakeId,
                bookId = bookId,
                chapterNumber = chapterNumber,
                questionPromptBn = questionPromptBn,
                previousWrongAnswerBn = wrongAnswerBn,
                correctAnswerBn = correctAnswerBn,
                explanationBn = explanationBn,
                sourceProvenance = sourceProvenance,
                mistakeCount = 1,
                isResolved = false,
                lastMistakeAt = nowMs
            )
        } else {
            existing.copy(
                previousWrongAnswerBn = wrongAnswerBn,
                correctAnswerBn = correctAnswerBn,
                explanationBn = explanationBn,
                mistakeCount = existing.mistakeCount + 1,
                isResolved = false,
                lastMistakeAt = nowMs
            )
        }
        dao.upsertBookMistake(updatedMistake)

        // Schedule in 1/3/7/14-day spaced revision starting at stage 0 (1 day)
        scheduleSpacedRevisionItem(
            itemId = revItemId,
            itemType = "QUESTION",
            bookId = bookId,
            chapterNumber = chapterNumber,
            titleBn = questionPromptBn,
            subtitleBn = "$chapterTitleBn • সঠিক উত্তর: $correctAnswerBn",
            resetToFirstStage = true,
            nowMs = nowMs
        )

        // Keep unified_mistake_notebook synchronized with book_mistakes
        recordUnifiedMistake(
            mistakeKey = "BOOK_$rawId",
            sourceModule = "BOOK",
            subjectId = "${bookId}_ch_${chapterNumber}",
            curriculumTrack = "NCTB",
            questionPromptBn = questionPromptBn,
            userAnswerBn = wrongAnswerBn,
            correctAnswerBn = correctAnswerBn,
            diagnosticExplanationBn = explanationBn,
            nowMs = nowMs
        )
    }

    suspend fun markBookMistakeResolved(
        questionId: String,
        resolved: Boolean = true,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val rawId = questionId.removePrefix("mistake_").removePrefix("mistake_")
        val revItemId = "mistake_$rawId"
        val existing = dao.getBookMistakeById(questionId)
            ?: dao.getBookMistakeById(revItemId)
            ?: dao.getBookMistakeById(rawId)
        if (existing != null) {
            dao.upsertBookMistake(
                existing.copy(
                    isResolved = resolved,
                    lastMistakeAt = nowMs
                )
            )
        }
        if (resolved) {
            advanceSpacedRevisionItem(revItemId, answeredCorrectly = true, nowMs = nowMs)
            resolveUnifiedMistake("BOOK_$rawId", nowMs = nowMs)
        }
    }

    suspend fun deleteBookMistake(questionId: String) {
        val rawId = questionId.removePrefix("mistake_").removePrefix("mistake_")
        val revItemId = "mistake_$rawId"
        dao.deleteBookMistakeById(questionId)
        if (revItemId != questionId) dao.deleteBookMistakeById(revItemId)
        if (rawId != questionId) dao.deleteBookMistakeById(rawId)
        dao.deleteBookSpacedRevisionById(revItemId)
        dao.deleteBookSpacedRevisionById("mistake_$questionId")
        dao.deleteUnifiedMistakeByKey("BOOK_$rawId")
    }

    suspend fun scheduleSpacedRevisionItem(
        itemId: String,
        itemType: String,
        bookId: String,
        chapterNumber: Int,
        titleBn: String,
        subtitleBn: String,
        resetToFirstStage: Boolean = false,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val existing = dao.getBookSpacedRevisionById(itemId)
        val stage = if (resetToFirstStage || existing == null) 0 else existing.intervalStage
        val nextEpoch = nextSpacedReviewEpochMs(stage, nowMs)
        dao.upsertBookSpacedRevision(
            BookSpacedRevisionEntity(
                itemId = itemId,
                itemType = itemType,
                bookId = bookId,
                chapterNumber = chapterNumber,
                titleBn = titleBn,
                subtitleBn = subtitleBn,
                intervalStage = stage,
                nextReviewEpochMs = nextEpoch,
                lastReviewedAt = nowMs
            )
        )
    }

    suspend fun advanceSpacedRevisionItem(
        itemId: String,
        answeredCorrectly: Boolean,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val existing = dao.getBookSpacedRevisionById(itemId) ?: return
        val nextStage = if (answeredCorrectly) {
            (existing.intervalStage + 1).coerceAtMost(4)
        } else {
            0
        }
        val nextEpoch = nextSpacedReviewEpochMs(nextStage, nowMs)
        dao.upsertBookSpacedRevision(
            existing.copy(
                intervalStage = nextStage,
                nextReviewEpochMs = nextEpoch,
                lastReviewedAt = nowMs
            )
        )
    }

    // ==========================================
    // BCS PREPARATION PERSISTENCE (Room v6 / v8)
    // ==========================================
    suspend fun getBcsUserProfile(profileId: String = "active_profile"): com.example.data.model.BcsUserProfileEntity? {
        return dao.getBcsUserProfile(profileId)
    }

    suspend fun saveBcsUserProfile(profile: com.example.data.model.BcsUserProfileEntity) {
        dao.upsertBcsUserProfile(profile)
        // Also persist a profile-scoped snapshot row so switching profiles restores each profile's stage/targets
        val scopedProfileId = "profile_${profile.selectedExamProfileId}"
        if (profile.profileId != scopedProfileId) {
            dao.upsertBcsUserProfile(profile.copy(profileId = scopedProfileId))
        }
    }

    suspend fun recordBcsTopicProgress(
        topicId: String,
        subjectTrack: String,
        isGuideCompleted: Boolean,
        miniCheckPassed: Boolean,
        additionalTimeSeconds: Int = 30,
        questionsAttemptedDelta: Int = 0,
        questionsCorrectDelta: Int = 0,
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        nowMs: Long = System.currentTimeMillis()
    ) {
        val rawTopicId = topicId.substringAfter("::", topicId)
        val resolvedKey = if (examProfileId == "BCS_50TH_PRELIM_2025" || examProfileId.isBlank()) {
            rawTopicId
        } else {
            com.example.data.model.BcsTopicProgressEntity.buildScopedKey(examProfileId, rawTopicId)
        }
        val existing = dao.getBcsTopicProgressById(resolvedKey)
        val attemptedDelta = if (questionsAttemptedDelta > 0) questionsAttemptedDelta else if (miniCheckPassed) 1 else 0
        val correctDelta = if (questionsCorrectDelta > 0) questionsCorrectDelta else if (miniCheckPassed) 1 else 0
        val updated = if (existing == null) {
            val baseMastery = when {
                miniCheckPassed && isGuideCompleted -> 100
                isGuideCompleted -> 70
                attemptedDelta > 0 -> ((correctDelta * 100) / attemptedDelta.coerceAtLeast(1)).coerceIn(20, 95)
                else -> 40
            }
            com.example.data.model.BcsTopicProgressEntity(
                topicId = resolvedKey,
                subjectTrack = subjectTrack,
                isGuideCompleted = isGuideCompleted,
                miniCheckPassed = miniCheckPassed,
                questionsAttempted = attemptedDelta,
                questionsCorrect = correctDelta,
                totalTimeSeconds = additionalTimeSeconds,
                masteryPercent = baseMastery,
                lastStudiedAt = nowMs,
                examProfileId = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
            )
        } else {
            val guideDone = existing.isGuideCompleted || isGuideCompleted
            val checkDone = existing.miniCheckPassed || miniCheckPassed
            val nextAttempted = existing.questionsAttempted + attemptedDelta
            val nextCorrect = existing.questionsCorrect + correctDelta
            val accuracyRatio = if (nextAttempted > 0) (nextCorrect * 100) / nextAttempted else existing.masteryPercent
            val computedMastery = when {
                guideDone && checkDone && nextAttempted >= 3 -> ((accuracyRatio + 100) / 2).coerceIn(60, 100)
                guideDone && checkDone -> 100
                guideDone -> max(75, accuracyRatio)
                nextAttempted > 0 -> accuracyRatio.coerceIn(15, 95)
                else -> existing.masteryPercent
            }
            existing.copy(
                isGuideCompleted = guideDone,
                miniCheckPassed = checkDone,
                questionsAttempted = nextAttempted,
                questionsCorrect = nextCorrect,
                totalTimeSeconds = existing.totalTimeSeconds + additionalTimeSeconds,
                masteryPercent = computedMastery,
                lastStudiedAt = nowMs,
                examProfileId = examProfileId.ifBlank { existing.examProfileId }
            )
        }
        dao.upsertBcsTopicProgress(updated)
    }

    internal fun extractCanonicalBcsQuestionId(questionId: String): String {
        val withoutProfile = questionId.substringAfter("::", questionId)
        return withoutProfile
            .replace(Regex("""^(?:bcs_ma_exam|bcs_mistake_retry|bcs_exam|bcs_prev)_\d+_"""), "")
            .replace(Regex("""_rep_\d+$"""), "")
            .ifBlank { withoutProfile }
    }

    suspend fun recordBcsMistake(
        questionId: String,
        subjectTrack: String,
        topicId: String,
        questionPromptBn: String,
        wrongAnswerBn: String,
        correctAnswerBn: String,
        explanationBn: String,
        shortcutTipBn: String = "",
        provenanceBadgeBn: String = "",
        examProfileId: String = "BCS_50TH_PRELIM_2025",
        nowMs: Long = System.currentTimeMillis()
    ) {
        val rawQId = extractCanonicalBcsQuestionId(questionId)
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        val resolvedKey = if (safeProfile == "BCS_50TH_PRELIM_2025") {
            rawQId
        } else {
            "$safeProfile::$rawQId"
        }
        val existing = dao.getBcsMistakeById(resolvedKey)
        val updated = if (existing == null) {
            com.example.data.model.BcsMistakeEntity(
                questionId = resolvedKey,
                subjectTrack = subjectTrack,
                topicId = topicId,
                questionPromptBn = questionPromptBn,
                wrongAnswerBn = wrongAnswerBn,
                correctAnswerBn = correctAnswerBn,
                explanationBn = explanationBn,
                shortcutTipBn = shortcutTipBn,
                provenanceBadgeBn = provenanceBadgeBn,
                mistakeCount = 1,
                retrySuccessCount = 0,
                isResolved = false,
                nextReviewEpochMs = nextSpacedReviewEpochMs(0, nowMs),
                intervalStage = 0,
                lastMistakeAt = nowMs,
                examProfileId = safeProfile
            )
        } else {
            existing.copy(
                wrongAnswerBn = wrongAnswerBn,
                correctAnswerBn = correctAnswerBn,
                explanationBn = explanationBn,
                mistakeCount = existing.mistakeCount + 1,
                isResolved = false,
                nextReviewEpochMs = nextSpacedReviewEpochMs(0, nowMs),
                intervalStage = 0,
                lastMistakeAt = nowMs,
                examProfileId = safeProfile
            )
        }
        dao.upsertBcsMistake(updated)
        recordUnifiedMistake(
            mistakeKey = "BCS_${safeProfile}_$rawQId",
            sourceModule = "BCS",
            subjectId = topicId.ifBlank { subjectTrack },
            curriculumTrack = "BCS",
            questionPromptBn = questionPromptBn,
            userAnswerBn = wrongAnswerBn,
            correctAnswerBn = correctAnswerBn,
            diagnosticExplanationBn = explanationBn,
            nowMs = nowMs
        )
    }

    suspend fun markBcsMistakeResolved(
        questionId: String,
        nowMs: Long = System.currentTimeMillis(),
        examProfileId: String = ""
    ) {
        val rawQId = extractCanonicalBcsQuestionId(questionId)
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        val scopedKey = if (examProfileId.isNotBlank() && examProfileId != "BCS_50TH_PRELIM_2025") {
            "$examProfileId::$rawQId"
        } else {
            rawQId
        }
        val existing = dao.getBcsMistakeById(questionId)
            ?: dao.getBcsMistakeById(scopedKey)
            ?: (if (examProfileId.isBlank() || examProfileId == "BCS_50TH_PRELIM_2025") dao.getBcsMistakeById(rawQId) else null)
        if (existing != null) {
            val nextStage = (existing.intervalStage + 1).coerceAtMost(4)
            dao.upsertBcsMistake(
                existing.copy(
                    isResolved = true,
                    retrySuccessCount = existing.retrySuccessCount + 1,
                    intervalStage = nextStage,
                    nextReviewEpochMs = nextSpacedReviewEpochMs(nextStage, nowMs),
                    lastMistakeAt = nowMs
                )
            )
        }
        resolveUnifiedMistake("BCS_${safeProfile}_$rawQId", nowMs = nowMs)
        resolveUnifiedMistake("BCS_$rawQId", nowMs = nowMs)
    }

    suspend fun deleteBcsMistake(questionId: String, examProfileId: String = "") {
        val rawQId = extractCanonicalBcsQuestionId(questionId)
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        dao.deleteBcsMistakeById(questionId)
        if (examProfileId.isNotBlank() && examProfileId != "BCS_50TH_PRELIM_2025") {
            dao.deleteBcsMistakeById("$examProfileId::$rawQId")
        } else if (rawQId != questionId) {
            dao.deleteBcsMistakeById(rawQId)
        }
        dao.deleteUnifiedMistakeByKey("BCS_${safeProfile}_$rawQId")
        dao.deleteUnifiedMistakeByKey("BCS_$rawQId")
    }

    suspend fun recordBcsStudyGoalDelta(
        examProfileId: String,
        lessonsDelta: Int = 0,
        questionsDelta: Int = 0,
        revisionsDelta: Int = 0,
        studyTimeMinutesDelta: Int = 0,
        mockExamsDelta: Int = 0,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        val dateKey = todayDateKey(nowMs)
        val goalKey = "${safeProfile}_$dateKey"
        val existing = dao.getBcsStudyGoalByKey(goalKey)
        val updated = if (existing == null) {
            com.example.data.model.BcsStudyPlanGoalEntity(
                goalKey = goalKey,
                examProfileId = safeProfile,
                dateKey = dateKey,
                lessonsCompleted = max(0, lessonsDelta),
                questionsSolved = max(0, questionsDelta),
                revisionsCompleted = max(0, revisionsDelta),
                studyTimeMinutes = max(0, studyTimeMinutesDelta),
                mockExamsCompleted = max(0, mockExamsDelta),
                updatedAt = nowMs
            )
        } else {
            existing.copy(
                lessonsCompleted = existing.lessonsCompleted + max(0, lessonsDelta),
                questionsSolved = existing.questionsSolved + max(0, questionsDelta),
                revisionsCompleted = existing.revisionsCompleted + max(0, revisionsDelta),
                studyTimeMinutes = existing.studyTimeMinutes + max(0, studyTimeMinutesDelta),
                mockExamsCompleted = existing.mockExamsCompleted + max(0, mockExamsDelta),
                updatedAt = nowMs
            )
        }
        dao.upsertBcsStudyGoal(updated)
    }

    // ==========================================
    // ECOSYSTEM UPGRADE PERSISTENCE (Room v7)
    // ==========================================
    suspend fun recordUnifiedMistake(
        mistakeKey: String,
        sourceModule: String,
        subjectId: String,
        curriculumTrack: String = "NCTB",
        questionPromptBn: String,
        userAnswerBn: String,
        correctAnswerBn: String,
        diagnosticExplanationBn: String,
        errorType: String = "CONCEPTUAL",
        nowMs: Long = System.currentTimeMillis()
    ) {
        val existing = dao.getUnifiedMistakeByKey(mistakeKey)
        val updated = if (existing == null) {
            UnifiedMistakeEntity(
                mistakeKey = mistakeKey,
                sourceModule = sourceModule,
                subjectId = subjectId,
                curriculumTrack = curriculumTrack,
                questionPromptBn = questionPromptBn,
                userAnswerBn = userAnswerBn,
                correctAnswerBn = correctAnswerBn,
                diagnosticExplanationBn = diagnosticExplanationBn,
                errorType = errorType,
                failureCount = 1,
                reviewIntervalDays = 1,
                nextReviewAtMillis = nextSpacedReviewEpochMs(0, nowMs),
                resolved = false,
                lastFailedAtMillis = nowMs
            )
        } else {
            existing.copy(
                userAnswerBn = userAnswerBn,
                correctAnswerBn = correctAnswerBn,
                diagnosticExplanationBn = diagnosticExplanationBn,
                failureCount = existing.failureCount + 1,
                reviewIntervalDays = 1,
                nextReviewAtMillis = nextSpacedReviewEpochMs(0, nowMs),
                resolved = false,
                lastFailedAtMillis = nowMs
            )
        }
        dao.upsertUnifiedMistake(updated)
    }

    suspend fun resolveUnifiedMistake(
        mistakeKey: String,
        nowMs: Long = System.currentTimeMillis()
    ) {
        val existing = dao.getUnifiedMistakeByKey(mistakeKey) ?: return
        val nextIntervalDays = when (existing.reviewIntervalDays) {
            1 -> 3
            3 -> 7
            7 -> 14
            else -> 30
        }
        val nextStageIndex = when (nextIntervalDays) {
            3 -> 1
            7 -> 2
            else -> 3
        }
        dao.upsertUnifiedMistake(
            existing.copy(
                resolved = true,
                reviewIntervalDays = nextIntervalDays,
                nextReviewAtMillis = nextSpacedReviewEpochMs(nextStageIndex, nowMs),
                lastFailedAtMillis = nowMs
            )
        )
    }

    suspend fun deleteUnifiedMistake(mistakeKey: String) {
        dao.deleteUnifiedMistakeByKey(mistakeKey)
    }

    suspend fun recordFormulaMastery(
        formulaId: String,
        subjectId: String,
        masteryLevel: String = "MASTERED",
        nowMs: Long = System.currentTimeMillis()
    ) {
        val existing = dao.getFormulaMasteryById(formulaId)
        val updated = if (existing == null) {
            FormulaMasteryEntity(
                formulaId = formulaId,
                subjectId = subjectId,
                masteryLevel = masteryLevel,
                practiceCount = 1,
                lastPracticedAtMillis = nowMs
            )
        } else {
            existing.copy(
                masteryLevel = masteryLevel,
                practiceCount = existing.practiceCount + 1,
                lastPracticedAtMillis = nowMs
            )
        }
        dao.upsertFormulaMastery(updated)
        setItemLearned("formula_lib_$formulaId", masteryLevel == "MASTERED")
    }
}

data class TopicExamStat(
    val topicId: String,
    val topicTitleBn: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val timeSpentSeconds: Int
)

