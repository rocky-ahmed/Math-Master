package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsMistakeEntity
import com.example.data.model.BcsStudyPlanGoalEntity
import com.example.data.model.BcsTopicProgressEntity
import com.example.data.model.BcsUserProfileEntity
import com.example.data.model.BookLessonProgressEntity
import com.example.data.model.BookMistakeEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.BookReadingStateEntity
import com.example.data.model.BookSpacedRevisionEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.FormulaMasteryEntity
import com.example.data.model.RecentFormulaEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.model.StudyTimerFlushReceiptEntity
import com.example.data.model.TopicAnalyticsEntity
import com.example.data.model.UnifiedMistakeEntity
import com.example.data.repository.BcsTopicExamUpdate
import com.example.data.repository.ExamQuestionMistakeOutcome
import com.example.data.repository.MathRepository
import com.example.data.repository.TopicExamStat
import com.example.math.BcsGoalCreditLedgerTxnResult
import kotlinx.coroutines.flow.Flow
import kotlin.math.max
import kotlin.math.roundToInt

@Dao
interface MathDao {

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    suspend fun isBookmarked(id: String): Boolean

    // Quiz Scores
    @Query("SELECT * FROM quiz_scores ORDER BY timestamp DESC")
    fun getAllQuizScores(): Flow<List<QuizScoreEntity>>

    @Query("SELECT COUNT(*) FROM quiz_scores")
    fun getQuizScoresCount(): Flow<Int>

    @Query("SELECT * FROM quiz_scores ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentQuizScores(limit: Int): Flow<List<QuizScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizScore(score: QuizScoreEntity)

    @Query("DELETE FROM quiz_scores WHERE id = :id")
    suspend fun deleteQuizScoreById(id: Int)

    @Query("SELECT * FROM quiz_scores WHERE timestamp BETWEEN :minTimestamp AND :maxTimestamp ORDER BY ABS(timestamp - :targetTimestamp) ASC")
    suspend fun getQuizScoresInTimeWindow(minTimestamp: Long, maxTimestamp: Long, targetTimestamp: Long): List<QuizScoreEntity>

    @Query("DELETE FROM quiz_scores WHERE timestamp BETWEEN :minTimestamp AND :maxTimestamp")
    suspend fun deleteQuizScoresInTimeWindow(minTimestamp: Long, maxTimestamp: Long)

    // User Progress
    @Query("SELECT * FROM user_progress WHERE itemId NOT LIKE '__session_receipt__%'")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Query("SELECT COUNT(*) FROM user_progress WHERE isLearned = 1 AND itemId LIKE 'table\\_%' ESCAPE '\\'")
    fun getLearnedTablesCount(): Flow<Int>

    @Query("SELECT * FROM user_progress WHERE itemId = :itemId LIMIT 1")
    suspend fun getProgressByItemId(itemId: String): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgressIfAbsent(progress: ProgressEntity): Long

    // Practice & Challenge History (Room v3) — full historical completeness without arbitrary row truncation
    @Query("SELECT * FROM practice_history ORDER BY timestamp DESC")
    fun getAllPracticeHistory(): Flow<List<PracticeHistoryEntity>>

    @Query("SELECT * FROM practice_history WHERE module = :module ORDER BY timestamp DESC")
    fun getPracticeHistoryByModule(module: String): Flow<List<PracticeHistoryEntity>>

    @Query("SELECT * FROM practice_history ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getPracticeHistoryPage(limit: Int, offset: Int): List<PracticeHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeHistory(history: PracticeHistoryEntity)

    @Query("DELETE FROM practice_history WHERE id = :id")
    suspend fun deletePracticeHistoryById(id: Int)

    @Query("SELECT * FROM practice_history WHERE module = 'QUIZ' AND timestamp BETWEEN :minTimestamp AND :maxTimestamp ORDER BY ABS(timestamp - :targetTimestamp) ASC")
    suspend fun getQuizPracticeHistoryInTimeWindow(minTimestamp: Long, maxTimestamp: Long, targetTimestamp: Long): List<PracticeHistoryEntity>

    @Query("DELETE FROM practice_history WHERE module = 'QUIZ' AND timestamp BETWEEN :minTimestamp AND :maxTimestamp")
    suspend fun deleteQuizPracticeHistoryInTimeWindow(minTimestamp: Long, maxTimestamp: Long)

    // Topic Analytics (Room v3)
    @Query("SELECT * FROM topic_analytics ORDER BY lastPracticedAt DESC")
    fun getAllTopicAnalytics(): Flow<List<TopicAnalyticsEntity>>

    @Query("SELECT * FROM topic_analytics WHERE analyticsKey = :key LIMIT 1")
    suspend fun getTopicAnalyticsByKey(key: String): TopicAnalyticsEntity?

    @Query("SELECT * FROM topic_analytics WHERE analyticsKey IN (:keys)")
    suspend fun getTopicAnalyticsByKeys(keys: List<String>): List<TopicAnalyticsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopicAnalytics(analytics: TopicAnalyticsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopicAnalyticsList(items: List<TopicAnalyticsEntity>)

    // Recently Viewed Formulas (Room v3)
    @Query("SELECT * FROM recent_formulas ORDER BY viewedAt DESC LIMIT 20")
    fun getRecentFormulas(): Flow<List<RecentFormulaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecentFormula(recent: RecentFormulaEntity)

    // Exam Attempts (Room v4)
    @Query("SELECT * FROM exam_attempts ORDER BY startedAt DESC")
    fun getAllExamAttempts(): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts WHERE status = 'COMPLETED' ORDER BY completedAt DESC, startedAt DESC")
    fun getCompletedExamAttempts(): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts WHERE status = 'COMPLETED' AND attemptId != :excludeAttemptId AND COALESCE(completedAt, updatedAt) BETWEEN :minTimestamp AND :maxTimestamp")
    suspend fun getOtherCompletedExamsInTimeWindow(excludeAttemptId: String, minTimestamp: Long, maxTimestamp: Long): List<ExamAttemptEntity>

    @Query("SELECT * FROM exam_attempts WHERE status = 'IN_PROGRESS' ORDER BY updatedAt DESC LIMIT 1")
    fun getActiveExamAttemptFlow(): Flow<ExamAttemptEntity?>

    @Query("SELECT * FROM exam_attempts WHERE status = 'IN_PROGRESS' ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getActiveExamAttempt(): ExamAttemptEntity?

    @Query("SELECT * FROM exam_attempts WHERE attemptId = :attemptId LIMIT 1")
    suspend fun getExamAttemptById(attemptId: String): ExamAttemptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExamAttempt(attempt: ExamAttemptEntity)

    @Query("DELETE FROM exam_attempts WHERE attemptId = :attemptId")
    suspend fun deleteExamAttemptById(attemptId: String)

    @Query("UPDATE exam_attempts SET status = 'ABANDONED', updatedAt = :timestamp WHERE status = 'IN_PROGRESS'")
    suspend fun abandonActiveExamAttempts(timestamp: Long = System.currentTimeMillis())

    // Solver History (Room v4) — full historical completeness; use getRecentSolverHistory(limit) for bounded previews
    @Query("SELECT * FROM solver_history ORDER BY timestamp DESC")
    fun getAllSolverHistory(): Flow<List<SolverHistoryEntity>>

    @Query("SELECT * FROM solver_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSolverHistory(limit: Int): Flow<List<SolverHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolverHistory(history: SolverHistoryEntity)

    @Query("DELETE FROM solver_history WHERE id = :id")
    suspend fun deleteSolverHistoryById(id: Int)

    @Query("DELETE FROM solver_history")
    suspend fun clearAllSolverHistory()

    // Daily Study Activity (Room v4) — full historical completeness for multi-year streak calculation
    @Query("SELECT * FROM daily_study_activity ORDER BY dateKey DESC")
    fun getAllDailyStudyActivity(): Flow<List<DailyStudyActivityEntity>>

    @Query("SELECT * FROM daily_study_activity WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getDailyStudyActivityByDate(dateKey: String): DailyStudyActivityEntity?

    @Query("SELECT * FROM daily_study_activity ORDER BY updatedAt DESC, dateKey DESC LIMIT 1")
    suspend fun getLatestDailyStudyActivity(): DailyStudyActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyStudyActivity(activity: DailyStudyActivityEntity)

    @Transaction
    suspend fun deleteExamAttemptSafely(attemptId: String, dateKeyForRefTs: String) {
        val existing = getExamAttemptById(attemptId) ?: return
        deleteExamAttemptById(attemptId)
        // BCS-GC-02: Remove only a PENDING ledger row inside the same transaction; CREDITED rows are preserved.
        deletePendingBcsGoalCreditRecordByAttemptId(attemptId)

        // Only remove paired completion records and adjust daily counters if the attempt was actually COMPLETED
        if (existing.status != "COMPLETED") return

        val refTs = existing.completedAt ?: existing.updatedAt
        if (refTs <= 0L) return

        val expectedTopicId = existing.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" }
        val otherExams = getOtherCompletedExamsInTimeWindow(attemptId, refTs - 2500L, refTs + 2500L)

        // 1. Delete at most 1 paired quiz_scores record with full signature verification & sibling protection
        val otherSameQuizSig = otherExams.filter { other ->
            val otherTopic = other.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" }
            other.totalQuestions == existing.totalQuestions &&
                other.correctCount == existing.correctCount &&
                (otherTopic == expectedTopicId || other.examTitleBn == existing.examTitleBn)
        }
        val candidateScores = getQuizScoresInTimeWindow(refTs - 2500L, refTs + 2500L, refTs)
        val matchingScores = candidateScores.filter { qs ->
            qs.totalQuestions == existing.totalQuestions &&
                qs.score == existing.correctCount &&
                (qs.topic == expectedTopicId || qs.topicTitleBn == existing.examTitleBn)
        }
        if (matchingScores.size > otherSameQuizSig.size) {
            val targetScore = matchingScores.firstOrNull { qs ->
                val distToDeleted = kotlin.math.abs(qs.timestamp - refTs)
                otherSameQuizSig.none { sib ->
                    val sibTs = sib.completedAt ?: sib.updatedAt
                    kotlin.math.abs(qs.timestamp - sibTs) < distToDeleted
                }
            } ?: matchingScores.first()
            deleteQuizScoreById(targetScore.id)
        }

        // 2. Delete at most 1 paired practice_history record with full signature verification & sibling protection
        val otherSameHistorySig = otherExams.filter { other ->
            val otherTopic = other.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" }
            other.totalQuestions == existing.totalQuestions &&
                other.correctCount == existing.correctCount &&
                other.wrongCount == existing.wrongCount &&
                (other.examType == existing.examType || otherTopic == expectedTopicId || other.examTitleBn == existing.examTitleBn)
        }
        val candidateHistory = getQuizPracticeHistoryInTimeWindow(refTs - 2500L, refTs + 2500L, refTs)
        val matchingHistory = candidateHistory.filter { ph ->
            ph.totalQuestions == existing.totalQuestions &&
                ph.correctCount == existing.correctCount &&
                ph.wrongCount == existing.wrongCount &&
                (ph.mode == existing.examType || ph.topicId == expectedTopicId || ph.topicTitleBn == existing.examTitleBn)
        }
        if (matchingHistory.size > otherSameHistorySig.size) {
            val targetHistory = matchingHistory.firstOrNull { ph ->
                val distToDeleted = kotlin.math.abs(ph.timestamp - refTs)
                otherSameHistorySig.none { sib ->
                    val sibTs = sib.completedAt ?: sib.updatedAt
                    kotlin.math.abs(ph.timestamp - sibTs) < distToDeleted
                }
            } ?: matchingHistory.first()
            deletePracticeHistoryById(targetHistory.id)
        }

        // 3. Adjust daily_study_activity atomically within the same transaction
        if (dateKeyForRefTs.isNotBlank()) {
            val daily = getDailyStudyActivityByDate(dateKeyForRefTs)
            if (daily != null) {
                val attemptedDelta = existing.correctCount + existing.wrongCount
                upsertDailyStudyActivity(
                    daily.copy(
                        questionsAttempted = kotlin.math.max(0, daily.questionsAttempted - attemptedDelta),
                        correctCount = kotlin.math.max(0, daily.correctCount - existing.correctCount),
                        wrongCount = kotlin.math.max(0, daily.wrongCount - existing.wrongCount),
                        studyTimeSeconds = kotlin.math.max(0, daily.studyTimeSeconds - kotlin.math.max(1, existing.elapsedSeconds)),
                        examsCompleted = kotlin.math.max(0, daily.examsCompleted - 1),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // Book Lesson Progress (Room v5)
    @Query("SELECT * FROM book_lesson_progress ORDER BY lastVisitedAt DESC")
    fun getAllBookLessonProgress(): Flow<List<BookLessonProgressEntity>>

    @Query("SELECT * FROM book_lesson_progress WHERE lessonKey = :lessonKey LIMIT 1")
    suspend fun getBookLessonProgressByKey(lessonKey: String): BookLessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookLessonProgress(progress: BookLessonProgressEntity)

    // Book Reading State (Room v5)
    @Query("SELECT * FROM book_reading_state ORDER BY updatedAt DESC")
    fun getAllBookReadingStates(): Flow<List<BookReadingStateEntity>>

    @Query("SELECT * FROM book_reading_state WHERE bookId = :bookId LIMIT 1")
    suspend fun getBookReadingStateByBookId(bookId: String): BookReadingStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookReadingState(state: BookReadingStateEntity)

    // Book Notes (Room v5)
    @Query("SELECT * FROM book_notes ORDER BY updatedAt DESC")
    fun getAllBookNotes(): Flow<List<BookNoteEntity>>

    @Query("SELECT * FROM book_notes WHERE id = :id LIMIT 1")
    suspend fun getBookNoteById(id: Int): BookNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookNote(note: BookNoteEntity)

    @Query("DELETE FROM book_notes WHERE id = :id")
    suspend fun deleteBookNoteById(id: Int)

    // Book Mistakes (Room v5)
    @Query("SELECT * FROM book_mistakes ORDER BY lastMistakeAt DESC")
    fun getAllBookMistakes(): Flow<List<BookMistakeEntity>>

    @Query("SELECT * FROM book_mistakes WHERE questionId = :questionId LIMIT 1")
    suspend fun getBookMistakeById(questionId: String): BookMistakeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookMistake(mistake: BookMistakeEntity)

    @Query("DELETE FROM book_mistakes WHERE questionId = :questionId")
    suspend fun deleteBookMistakeById(questionId: String)

    // Book Spaced Revision (Room v5)
    @Query("SELECT * FROM book_spaced_revision ORDER BY nextReviewEpochMs ASC")
    fun getAllBookSpacedRevision(): Flow<List<BookSpacedRevisionEntity>>

    @Query("SELECT * FROM book_spaced_revision WHERE itemId = :itemId LIMIT 1")
    suspend fun getBookSpacedRevisionById(itemId: String): BookSpacedRevisionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookSpacedRevision(item: BookSpacedRevisionEntity)

    @Query("DELETE FROM book_spaced_revision WHERE itemId = :itemId")
    suspend fun deleteBookSpacedRevisionById(itemId: String)

    // BCS User Profile (Room v6)
    @Query("SELECT * FROM bcs_user_profile WHERE profileId = :profileId LIMIT 1")
    fun getBcsUserProfileFlow(profileId: String = "active_profile"): Flow<BcsUserProfileEntity?>

    @Query("SELECT * FROM bcs_user_profile WHERE profileId = :profileId LIMIT 1")
    suspend fun getBcsUserProfile(profileId: String = "active_profile"): BcsUserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsUserProfile(profile: BcsUserProfileEntity)

    @Query("SELECT * FROM bcs_user_profile ORDER BY updatedAt DESC")
    fun getAllBcsUserProfilesFlow(): Flow<List<BcsUserProfileEntity>>

    // BCS Topic Progress (Room v6 / v8)
    @Query("SELECT * FROM bcs_topic_progress ORDER BY lastStudiedAt DESC")
    fun getAllBcsTopicProgress(): Flow<List<BcsTopicProgressEntity>>

    @Query("SELECT * FROM bcs_topic_progress WHERE examProfileId = :examProfileId ORDER BY lastStudiedAt DESC")
    fun getBcsTopicProgressByProfile(examProfileId: String): Flow<List<BcsTopicProgressEntity>>

    @Query("SELECT * FROM bcs_topic_progress WHERE topicId = :topicId LIMIT 1")
    suspend fun getBcsTopicProgressById(topicId: String): BcsTopicProgressEntity?

    @Query("SELECT * FROM bcs_topic_progress WHERE topicId IN (:topicIds)")
    suspend fun getBcsTopicProgressByIds(topicIds: List<String>): List<BcsTopicProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsTopicProgress(progress: BcsTopicProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsTopicProgressList(items: List<BcsTopicProgressEntity>)

    // BCS Mistake Notebook (Room v6 / v8)
    @Query("SELECT * FROM bcs_mistake_notebook ORDER BY lastMistakeAt DESC")
    fun getAllBcsMistakes(): Flow<List<BcsMistakeEntity>>

    @Query("SELECT * FROM bcs_mistake_notebook WHERE examProfileId = :examProfileId ORDER BY lastMistakeAt DESC")
    fun getBcsMistakesByProfile(examProfileId: String): Flow<List<BcsMistakeEntity>>

    @Query("SELECT * FROM bcs_mistake_notebook WHERE questionId = :questionId LIMIT 1")
    suspend fun getBcsMistakeById(questionId: String): BcsMistakeEntity?

    @Query("SELECT * FROM bcs_mistake_notebook WHERE questionId IN (:questionIds)")
    suspend fun getBcsMistakesByIds(questionIds: List<String>): List<BcsMistakeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsMistake(mistake: BcsMistakeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsMistakes(items: List<BcsMistakeEntity>)

    @Query("DELETE FROM bcs_mistake_notebook WHERE questionId = :questionId")
    suspend fun deleteBcsMistakeById(questionId: String)

    // BCS Study Plan Goals (Room v8)
    @Query("SELECT * FROM bcs_study_goals ORDER BY dateKey DESC, updatedAt DESC LIMIT 180")
    fun getAllBcsStudyGoals(): Flow<List<BcsStudyPlanGoalEntity>>

    @Query("SELECT * FROM bcs_study_goals WHERE goalKey = :goalKey LIMIT 1")
    suspend fun getBcsStudyGoalByKey(goalKey: String): BcsStudyPlanGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsStudyGoal(goal: BcsStudyPlanGoalEntity)

    // BCS Goal Credit Ledger (Room v11 - BCS-GC-02)
    @Query("SELECT * FROM bcs_goal_credit_records WHERE attemptId = :attemptId LIMIT 1")
    suspend fun getBcsGoalCreditRecordByAttemptId(attemptId: String): BcsGoalCreditRecordEntity?

    @Query("SELECT * FROM bcs_goal_credit_records WHERE status = 'PENDING' ORDER BY createdAtMs ASC")
    suspend fun getPendingBcsGoalCreditRecords(): List<BcsGoalCreditRecordEntity>

    @Query("SELECT * FROM bcs_goal_credit_records ORDER BY createdAtMs DESC")
    fun getAllBcsGoalCreditRecords(): Flow<List<BcsGoalCreditRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBcsGoalCreditRecordStrict(record: BcsGoalCreditRecordEntity): Long

    @Query(
        "UPDATE bcs_goal_credit_records " +
            "SET status = 'CREDITED', creditedAtMs = :creditedAtMs " +
            "WHERE attemptId = :attemptId AND status = 'PENDING'"
    )
    suspend fun transitionBcsGoalCreditPendingToCredited(
        attemptId: String,
        creditedAtMs: Long
    ): Int

    @Query("DELETE FROM bcs_goal_credit_records WHERE attemptId = :attemptId AND status = 'PENDING'")
    suspend fun deletePendingBcsGoalCreditRecordByAttemptId(attemptId: String): Int

    // Unified Mistake Notebook (Room v7)
    @Query("SELECT * FROM unified_mistake_notebook ORDER BY lastFailedAtMillis DESC")
    fun getAllUnifiedMistakes(): Flow<List<UnifiedMistakeEntity>>

    @Query("SELECT * FROM unified_mistake_notebook WHERE mistakeKey = :key LIMIT 1")
    suspend fun getUnifiedMistakeByKey(key: String): UnifiedMistakeEntity?

    @Query("SELECT * FROM unified_mistake_notebook WHERE mistakeKey IN (:keys)")
    suspend fun getUnifiedMistakesByKeys(keys: List<String>): List<UnifiedMistakeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUnifiedMistake(mistake: UnifiedMistakeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUnifiedMistakes(items: List<UnifiedMistakeEntity>)

    @Query("DELETE FROM unified_mistake_notebook WHERE mistakeKey = :key")
    suspend fun deleteUnifiedMistakeByKey(key: String)

    // Formula Mastery State (Room v7)
    @Query("SELECT * FROM formula_mastery_state ORDER BY lastPracticedAtMillis DESC")
    fun getAllFormulaMastery(): Flow<List<FormulaMasteryEntity>>

    @Query("SELECT * FROM formula_mastery_state WHERE formulaId = :formulaId LIMIT 1")
    suspend fun getFormulaMasteryById(formulaId: String): FormulaMasteryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFormulaMastery(state: FormulaMasteryEntity)

    // Study Timer Flush Receipts (Room v10 - BUG-86)
    @Query("SELECT * FROM study_timer_flush_receipts WHERE operationId = :operationId LIMIT 1")
    suspend fun getStudyTimerFlushReceiptById(operationId: String): StudyTimerFlushReceiptEntity?

    @Query("SELECT * FROM study_timer_flush_receipts ORDER BY appliedAtEpochMs DESC")
    suspend fun getAllStudyTimerFlushReceipts(): List<StudyTimerFlushReceiptEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudyTimerFlushReceipt(receipt: StudyTimerFlushReceiptEntity): Long

    @Transaction
    suspend fun applyStudyTimerFlushIdempotently(
        receipt: StudyTimerFlushReceiptEntity,
        defaultTargetQuestions: Int = 20,
        defaultTargetMinutes: Int = 15,
        simulateTransactionFailureAfterUpsert: Boolean = false
    ): Boolean {
        val cleanOpId = receipt.operationId.trim()
        require(cleanOpId.isNotEmpty()) { "operationId must not be blank" }
        if (getStudyTimerFlushReceiptById(cleanOpId) != null) {
            return false
        }
        val cleanDateKey = receipt.studyDateKey.trim()
        require(cleanDateKey.isNotEmpty()) { "studyDateKey must not be blank" }

        val deltaSeconds = kotlin.math.max(0, receipt.pendingSeconds)
        val deltaSessions = kotlin.math.max(0, receipt.completedSessionsDelta)
        val existingDay = getDailyStudyActivityByDate(cleanDateKey)
        val updatedDay = if (existingDay == null) {
            val latestConfigured = getLatestDailyStudyActivity()
            DailyStudyActivityEntity(
                dateKey = cleanDateKey,
                questionsAttempted = 0,
                correctCount = 0,
                wrongCount = 0,
                studyTimeSeconds = deltaSeconds,
                examsCompleted = 0,
                practiceSessionsCompleted = deltaSessions,
                dailyTargetQuestions = latestConfigured?.dailyTargetQuestions ?: defaultTargetQuestions,
                dailyTargetMinutes = latestConfigured?.dailyTargetMinutes ?: defaultTargetMinutes,
                updatedAt = receipt.appliedAtEpochMs
            )
        } else {
            existingDay.copy(
                studyTimeSeconds = existingDay.studyTimeSeconds + deltaSeconds,
                practiceSessionsCompleted = existingDay.practiceSessionsCompleted + deltaSessions,
                updatedAt = receipt.appliedAtEpochMs
            )
        }
        upsertDailyStudyActivity(updatedDay)
        if (simulateTransactionFailureAfterUpsert) {
            throw IllegalStateException("Injected Room failure after daily_study_activity upsert before receipt commit")
        }
        val rowId = insertStudyTimerFlushReceipt(receipt.copy(operationId = cleanOpId, studyDateKey = cleanDateKey))
        if (rowId == -1L) {
            throw IllegalStateException("Duplicate Study Timer flush receipt for operationId=$cleanOpId")
        }
        return true
    }

    // =========================================================================
    // PHASE 3 TRANSACTIONAL MULTI-TABLE WRITES (P3-DB-001, P3-DB-002)
    // Lock ordering rule: dailyStudyWriteMutex is ALWAYS acquired in MathRepository
    // BEFORE entering any @Transaction method that writes daily_study_activity.
    // =========================================================================

    @Transaction
    suspend fun recordSessionAndAnalyticsTransaction(
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
        durationSeconds: Int,
        nowMs: Long,
        dateKey: String
    ) {
        val accuracy = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
        insertPracticeHistory(
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
                timestamp = nowMs
            )
        )

        val analyticsKey = "${module}_${topicId}_${difficulty}"
        val existingTopic = getTopicAnalyticsByKey(analyticsKey)
        val attemptedDelta = correctCount + wrongCount
        val updatedTopic = if (existingTopic == null) {
            TopicAnalyticsEntity(
                analyticsKey = analyticsKey,
                module = module,
                topicId = topicId,
                topicTitleBn = topicTitleBn,
                difficulty = difficulty,
                totalAttempted = max(0, attemptedDelta),
                totalCorrect = max(0, correctCount),
                totalWrong = max(0, wrongCount),
                bestStreak = max(0, bestStreak),
                totalTimeSeconds = max(0, durationSeconds),
                lastPracticedAt = nowMs
            )
        } else {
            existingTopic.copy(
                totalAttempted = existingTopic.totalAttempted + max(0, attemptedDelta),
                totalCorrect = existingTopic.totalCorrect + max(0, correctCount),
                totalWrong = existingTopic.totalWrong + max(0, wrongCount),
                bestStreak = max(existingTopic.bestStreak, bestStreak),
                totalTimeSeconds = existingTopic.totalTimeSeconds + max(0, durationSeconds),
                lastPracticedAt = nowMs
            )
        }
        upsertTopicAnalytics(updatedTopic)

        applyDailyStudyDeltaInsideTransaction(
            dateKey = dateKey,
            questionsAttemptedDelta = attemptedDelta,
            correctDelta = correctCount,
            wrongDelta = wrongCount,
            studyTimeSecondsDelta = max(1, durationSeconds),
            examsCompletedDelta = if (module == "QUIZ") 1 else 0,
            practiceSessionsCompletedDelta = if (module != "QUIZ") 1 else 0,
            timestampMs = nowMs
        )
    }

    /**
     * A-05 / F4-05C: Atomically persists QuizScoreEntity, optional learned ProgressEntity,
     * PracticeHistoryEntity, cumulative TopicAnalyticsEntity, and DailyStudyActivityEntity
     * inside a single Room SQLite transaction, guarded by an optional idempotent [sessionId]
     * completion receipt so duplicate or retried completion calls never double-count scores,
     * history, or analytics.
     */
    @Transaction
    suspend fun completePracticeSessionTransaction(
        sessionId: String = "",
        quizScoreTopicId: String,
        quizScoreTitleBn: String,
        quizScoreValue: Int,
        totalQuestions: Int,
        percentage: Int,
        learnedProgressItemId: String? = null,
        module: String,
        topicId: String,
        topicTitleBn: String,
        difficulty: String,
        mode: String,
        score: Int,
        correctCount: Int,
        wrongCount: Int,
        bestStreak: Int,
        durationSeconds: Int,
        nowMs: Long,
        dateKey: String
    ): Boolean {
        val cleanSessionId = sessionId.trim()
        if (cleanSessionId.isNotEmpty()) {
            val receiptRowId = insertProgressIfAbsent(
                ProgressEntity(
                    itemId = "__session_receipt__$cleanSessionId",
                    isLearned = false,
                    updatedAt = nowMs
                )
            )
            if (receiptRowId == -1L) {
                return false
            }
        }

        insertQuizScore(
            QuizScoreEntity(
                topicId = quizScoreTopicId,
                topicTitleBn = quizScoreTitleBn,
                score = quizScoreValue,
                totalQuestions = totalQuestions,
                percentage = percentage,
                timestamp = nowMs
            )
        )

        val cleanLearnedId = learnedProgressItemId?.trim().orEmpty()
        if (cleanLearnedId.isNotEmpty()) {
            upsertProgress(
                ProgressEntity(
                    itemId = cleanLearnedId,
                    isLearned = true,
                    updatedAt = nowMs
                )
            )
        }

        recordSessionAndAnalyticsTransaction(
            module = module,
            topicId = topicId,
            topicTitleBn = topicTitleBn,
            difficulty = difficulty,
            mode = mode,
            score = score,
            correctCount = correctCount,
            wrongCount = wrongCount,
            totalQuestions = totalQuestions,
            bestStreak = bestStreak,
            durationSeconds = durationSeconds,
            nowMs = nowMs,
            dateKey = dateKey
        )
        return true
    }

    @Transaction
    suspend fun recordBookQuestionMistakeTransaction(
        questionId: String,
        bookId: String,
        chapterNumber: Int,
        questionPromptBn: String,
        wrongAnswerBn: String,
        correctAnswerBn: String,
        explanationBn: String,
        sourceProvenance: String,
        chapterTitleBn: String,
        nowMs: Long
    ) {
        val rawId = questionId.removePrefix("mistake_").removePrefix("mistake_")
        val revItemId = "mistake_$rawId"
        val existing = getBookMistakeById(questionId)
            ?: getBookMistakeById(revItemId)
            ?: getBookMistakeById(rawId)
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
        upsertBookMistake(updatedMistake)

        upsertBookSpacedRevision(
            BookSpacedRevisionEntity(
                itemId = revItemId,
                itemType = "QUESTION",
                bookId = bookId,
                chapterNumber = chapterNumber,
                titleBn = questionPromptBn,
                subtitleBn = "$chapterTitleBn • সঠিক উত্তর: $correctAnswerBn",
                intervalStage = 0,
                nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(0, nowMs),
                lastReviewedAt = nowMs
            )
        )

        upsertUnifiedMistakeInsideTransaction(
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

    @Transaction
    suspend fun markBookMistakeResolvedTransaction(
        questionId: String,
        resolved: Boolean,
        nowMs: Long
    ) {
        val rawId = questionId.removePrefix("mistake_").removePrefix("mistake_")
        val revItemId = "mistake_$rawId"
        val unifiedKey = "BOOK_$rawId"
        val existing = getBookMistakeById(questionId)
            ?: getBookMistakeById(revItemId)
            ?: getBookMistakeById(rawId)
        val existingUnified = getUnifiedMistakeByKey(unifiedKey)

        if (resolved) {
            val bookWasUnresolved = existing != null && !existing.isResolved
            val unifiedWasUnresolved = existingUnified != null && !existingUnified.resolved

            // Idempotent no-op if already resolved or no mistake record exists
            if (!bookWasUnresolved && !unifiedWasUnresolved) {
                return
            }

            if (bookWasUnresolved && existing != null) {
                upsertBookMistake(
                    existing.copy(
                        isResolved = true
                    )
                )
            }

            val existingRev = getBookSpacedRevisionById(revItemId)
            if (existingRev != null) {
                val nextStage = (existingRev.intervalStage + 1).coerceAtMost(4)
                upsertBookSpacedRevision(
                    existingRev.copy(
                        intervalStage = nextStage,
                        nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(nextStage, nowMs),
                        lastReviewedAt = nowMs
                    )
                )
            }

            if (unifiedWasUnresolved && existingUnified != null) {
                upsertUnifiedMistake(buildResolvedUnifiedMistake(existingUnified, nowMs))
            }
        } else {
            if (existing != null && existing.isResolved) {
                upsertBookMistake(
                    existing.copy(
                        isResolved = false
                    )
                )
            }
            if (existingUnified != null && existingUnified.resolved) {
                upsertUnifiedMistake(
                    existingUnified.copy(
                        resolved = false
                    )
                )
            }
        }
    }

    @Transaction
    suspend fun deleteBookMistakeTransaction(questionId: String) {
        val rawId = questionId.removePrefix("mistake_").removePrefix("mistake_")
        val revItemId = "mistake_$rawId"
        deleteBookMistakeById(questionId)
        if (revItemId != questionId) deleteBookMistakeById(revItemId)
        if (rawId != questionId) deleteBookMistakeById(rawId)
        deleteBookSpacedRevisionById(revItemId)
        deleteBookSpacedRevisionById("mistake_$questionId")
        deleteUnifiedMistakeByKey("BOOK_$rawId")
    }

    @Transaction
    suspend fun recordBcsMistakeTransaction(
        questionId: String,
        subjectTrack: String,
        topicId: String,
        questionPromptBn: String,
        wrongAnswerBn: String,
        correctAnswerBn: String,
        explanationBn: String,
        shortcutTipBn: String,
        provenanceBadgeBn: String,
        examProfileId: String,
        nowMs: Long
    ) {
        val rawQId = MathRepository.extractCanonicalBcsQuestionId(questionId)
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        val resolvedKey = if (safeProfile == "BCS_50TH_PRELIM_2025") {
            rawQId
        } else {
            "$safeProfile::$rawQId"
        }
        val existing = getBcsMistakeById(resolvedKey)
        val updated = if (existing == null) {
            BcsMistakeEntity(
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
                nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(0, nowMs),
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
                nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(0, nowMs),
                intervalStage = 0,
                lastMistakeAt = nowMs,
                examProfileId = safeProfile
            )
        }
        upsertBcsMistake(updated)
        upsertUnifiedMistakeInsideTransaction(
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

    @Transaction
    suspend fun markBcsMistakeResolvedTransaction(
        questionId: String,
        nowMs: Long,
        examProfileId: String
    ) {
        val rawQId = MathRepository.extractCanonicalBcsQuestionId(questionId)
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        val isDefaultProfile = safeProfile == "BCS_50TH_PRELIM_2025"
        val scopedKey = if (!isDefaultProfile) {
            "$safeProfile::$rawQId"
        } else {
            rawQId
        }
        val existing = getBcsMistakeById(scopedKey)
            ?: getBcsMistakeById(questionId)?.takeIf { it.belongsToProfile(safeProfile) }
            ?: (if (isDefaultProfile) getBcsMistakeById(rawQId) else null)
        if (existing != null && !existing.isResolved) {
            val nextStage = (existing.intervalStage + 1).coerceAtMost(4)
            upsertBcsMistake(
                existing.copy(
                    isResolved = true,
                    retrySuccessCount = existing.retrySuccessCount + 1,
                    intervalStage = nextStage,
                    nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(nextStage, nowMs)
                )
            )
        }
        resolveUnifiedMistakeInsideTransaction("BCS_${safeProfile}_$rawQId", nowMs)
        if (isDefaultProfile) {
            resolveUnifiedMistakeInsideTransaction("BCS_$rawQId", nowMs)
        }
    }

    @Transaction
    suspend fun applyBcsStudyGoalDeltaTransaction(
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
        val existing = getBcsStudyGoalByKey(goalKey)
        val updated = if (existing == null) {
            BcsStudyPlanGoalEntity(
                goalKey = goalKey,
                examProfileId = examProfileId,
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
        upsertBcsStudyGoal(updated)
        return updated
    }

    /**
     * A-04 / F4-05A: Atomically reads, merges, and upserts a single [BcsTopicProgressEntity] row
     * inside a Room SQLite transaction to eliminate lost-update races across concurrent updates.
     */
    @Transaction
    suspend fun recordBcsTopicProgressTransaction(
        topicId: String,
        subjectTrack: String,
        isGuideCompleted: Boolean,
        miniCheckPassed: Boolean,
        additionalTimeSeconds: Int,
        questionsAttemptedDelta: Int,
        questionsCorrectDelta: Int,
        examProfileId: String,
        nowMs: Long
    ): BcsTopicProgressEntity {
        val rawTopicId = topicId.substringAfter("::", topicId)
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        val resolvedKey = if (safeProfile == "BCS_50TH_PRELIM_2025") {
            rawTopicId
        } else {
            BcsTopicProgressEntity.buildScopedKey(safeProfile, rawTopicId)
        }
        val existing = getBcsTopicProgressById(resolvedKey)
        val attemptedDelta = if (questionsAttemptedDelta > 0) questionsAttemptedDelta else if (miniCheckPassed) 1 else 0
        val correctDelta = if (questionsCorrectDelta > 0) questionsCorrectDelta else if (miniCheckPassed) 1 else 0
        val safeTimeDelta = max(0, additionalTimeSeconds)
        val updated = if (existing == null) {
            val baseMastery = when {
                miniCheckPassed && isGuideCompleted -> 100
                isGuideCompleted -> 70
                attemptedDelta > 0 -> ((correctDelta * 100) / attemptedDelta.coerceAtLeast(1)).coerceIn(20, 95)
                else -> 40
            }
            BcsTopicProgressEntity(
                topicId = resolvedKey,
                subjectTrack = subjectTrack,
                isGuideCompleted = isGuideCompleted,
                miniCheckPassed = miniCheckPassed,
                questionsAttempted = attemptedDelta,
                questionsCorrect = correctDelta,
                totalTimeSeconds = safeTimeDelta,
                masteryPercent = baseMastery,
                lastStudiedAt = nowMs,
                examProfileId = safeProfile
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
                subjectTrack = subjectTrack.ifBlank { existing.subjectTrack },
                isGuideCompleted = guideDone,
                miniCheckPassed = checkDone,
                questionsAttempted = nextAttempted,
                questionsCorrect = nextCorrect,
                totalTimeSeconds = existing.totalTimeSeconds + safeTimeDelta,
                masteryPercent = computedMastery,
                lastStudiedAt = max(existing.lastStudiedAt, nowMs),
                examProfileId = examProfileId.ifBlank { existing.examProfileId }
            )
        }
        upsertBcsTopicProgress(updated)
        return updated
    }

    /**
     * BCS-GC-02: Atomically transitions a persisted PENDING ledger record to CREDITED and increments
     * the corresponding bcs_study_goals row strictly from the persisted ledger payload.
     */
    @Transaction
    suspend fun applyBcsCompletionGoalCreditIdempotently(
        attemptId: String,
        creditedAtMs: Long
    ): BcsGoalCreditLedgerTxnResult {
        val cleanAttemptId = attemptId.trim()
        if (cleanAttemptId.isEmpty()) {
            return BcsGoalCreditLedgerTxnResult.NoPendingRecord(attemptId = attemptId)
        }

        val existing = getBcsGoalCreditRecordByAttemptId(cleanAttemptId)
            ?: return BcsGoalCreditLedgerTxnResult.NoPendingRecord(attemptId = cleanAttemptId)

        return when (existing.status) {
            BcsGoalCreditRecordEntity.STATUS_CREDITED -> {
                BcsGoalCreditLedgerTxnResult.AlreadyCredited(record = existing)
            }
            BcsGoalCreditRecordEntity.STATUS_PENDING -> {
                val safeProfileId = existing.examProfileId.trim()
                val safeDateKey = existing.targetDateKey.trim()
                if (safeProfileId.isEmpty()) {
                    return BcsGoalCreditLedgerTxnResult.InvalidLedgerState(
                        attemptId = cleanAttemptId,
                        status = existing.status,
                        reason = "Blank examProfileId in PENDING ledger record"
                    )
                }
                if (safeDateKey.isEmpty()) {
                    return BcsGoalCreditLedgerTxnResult.InvalidLedgerState(
                        attemptId = cleanAttemptId,
                        status = existing.status,
                        reason = "Blank targetDateKey in PENDING ledger record"
                    )
                }
                if (existing.questionsDelta < 0 ||
                    existing.studyTimeMinutesDelta < 0 ||
                    existing.mockExamsDelta < 0 ||
                    existing.revisionsDelta < 0
                ) {
                    return BcsGoalCreditLedgerTxnResult.InvalidLedgerState(
                        attemptId = cleanAttemptId,
                        status = existing.status,
                        reason = "Negative delta in PENDING ledger record"
                    )
                }
                val hasAnyCredit = existing.questionsDelta > 0 ||
                    existing.studyTimeMinutesDelta > 0 ||
                    existing.mockExamsDelta > 0 ||
                    existing.revisionsDelta > 0
                if (!hasAnyCredit) {
                    return BcsGoalCreditLedgerTxnResult.InvalidLedgerState(
                        attemptId = cleanAttemptId,
                        status = existing.status,
                        reason = "Zero total delta in PENDING ledger record"
                    )
                }

                val updatedRows = transitionBcsGoalCreditPendingToCredited(
                    attemptId = cleanAttemptId,
                    creditedAtMs = creditedAtMs
                )
                if (updatedRows != 1) {
                    val latest = getBcsGoalCreditRecordByAttemptId(cleanAttemptId)
                    return when {
                        latest == null -> BcsGoalCreditLedgerTxnResult.NoPendingRecord(attemptId = cleanAttemptId)
                        latest.status == BcsGoalCreditRecordEntity.STATUS_CREDITED ->
                            BcsGoalCreditLedgerTxnResult.AlreadyCredited(record = latest)
                        else -> BcsGoalCreditLedgerTxnResult.InvalidLedgerState(
                            attemptId = cleanAttemptId,
                            status = latest.status,
                            reason = "Conditional PENDING -> CREDITED transition affected $updatedRows rows"
                        )
                    }
                }

                val goalKey = "${safeProfileId}_$safeDateKey"
                val existingGoal = getBcsStudyGoalByKey(goalKey)
                val updatedGoal = if (existingGoal == null) {
                    BcsStudyPlanGoalEntity(
                        goalKey = goalKey,
                        examProfileId = safeProfileId,
                        dateKey = safeDateKey,
                        lessonsCompleted = 0,
                        questionsSolved = existing.questionsDelta,
                        revisionsCompleted = existing.revisionsDelta,
                        studyTimeMinutes = existing.studyTimeMinutesDelta,
                        mockExamsCompleted = existing.mockExamsDelta,
                        updatedAt = creditedAtMs
                    )
                } else {
                    existingGoal.copy(
                        questionsSolved = existingGoal.questionsSolved + existing.questionsDelta,
                        revisionsCompleted = existingGoal.revisionsCompleted + existing.revisionsDelta,
                        studyTimeMinutes = existingGoal.studyTimeMinutes + existing.studyTimeMinutesDelta,
                        mockExamsCompleted = existingGoal.mockExamsCompleted + existing.mockExamsDelta,
                        updatedAt = creditedAtMs
                    )
                }
                upsertBcsStudyGoal(updatedGoal)

                val creditedRecord = existing.copy(
                    status = BcsGoalCreditRecordEntity.STATUS_CREDITED,
                    creditedAtMs = creditedAtMs
                )
                BcsGoalCreditLedgerTxnResult.Applied(
                    record = creditedRecord,
                    updatedGoal = updatedGoal
                )
            }
            else -> {
                BcsGoalCreditLedgerTxnResult.InvalidLedgerState(
                    attemptId = cleanAttemptId,
                    status = existing.status,
                    reason = "Unexpected ledger status '${existing.status}'"
                )
            }
        }
    }

    /**
     * BCS-GC-02 (Stage 4B1): Transitions an existing PENDING ledger record to CREDITED without
     * mutating bcs_study_goals. Intended strictly for reconciling a caller-verified legacy
     * SharedPreferences credit receipt for the same attemptId.
     */
    @Transaction
    suspend fun markPendingBcsGoalCreditCreditedWithoutGoalMutation(
        attemptId: String,
        creditedAtMs: Long
    ): Boolean {
        val cleanAttemptId = attemptId.trim()
        if (cleanAttemptId.isEmpty()) {
            return false
        }

        val existing = getBcsGoalCreditRecordByAttemptId(cleanAttemptId) ?: return false
        if (existing.attemptId != cleanAttemptId) {
            return false
        }

        return when (existing.status) {
            BcsGoalCreditRecordEntity.STATUS_CREDITED -> true
            BcsGoalCreditRecordEntity.STATUS_PENDING -> {
                val updatedRows = transitionBcsGoalCreditPendingToCredited(
                    attemptId = cleanAttemptId,
                    creditedAtMs = creditedAtMs
                )
                if (updatedRows == 1) {
                    true
                } else {
                    val latest = getBcsGoalCreditRecordByAttemptId(cleanAttemptId)
                    latest != null && latest.status == BcsGoalCreditRecordEntity.STATUS_CREDITED
                }
            }
            else -> false
        }
    }

    @Transaction
    suspend fun resolveUnifiedMistakeTransaction(
        mistakeKey: String,
        nowMs: Long
    ) {
        if (mistakeKey.startsWith("BOOK_")) {
            val rawId = mistakeKey.removePrefix("BOOK_")
            markBookMistakeResolvedTransaction(
                questionId = "mistake_$rawId",
                resolved = true,
                nowMs = nowMs
            )
            return
        }
        if (mistakeKey.startsWith("BCS_")) {
            val remainder = mistakeKey.removePrefix("BCS_")
            val matchedProfile = com.example.math.BcsExamProfile.entries
                .sortedByDescending { it.id.length }
                .firstOrNull { remainder.startsWith("${it.id}_") }
            if (matchedProfile != null) {
                val rawQId = remainder.removePrefix("${matchedProfile.id}_")
                if (rawQId.isNotBlank()) {
                    markBcsMistakeResolvedTransaction(
                        questionId = rawQId,
                        nowMs = nowMs,
                        examProfileId = matchedProfile.id
                    )
                    return
                }
            } else if (remainder.isNotBlank()) {
                markBcsMistakeResolvedTransaction(
                    questionId = remainder,
                    nowMs = nowMs,
                    examProfileId = "BCS_50TH_PRELIM_2025"
                )
                return
            }
        }
        resolveUnifiedMistakeInsideTransaction(mistakeKey, nowMs)
    }

    @Transaction
    suspend fun unresolveUnifiedMistakeTransaction(
        mistakeKey: String,
        nowMs: Long
    ) {
        if (mistakeKey.startsWith("BOOK_")) {
            val rawId = mistakeKey.removePrefix("BOOK_")
            markBookMistakeResolvedTransaction(
                questionId = "mistake_$rawId",
                resolved = false,
                nowMs = nowMs
            )
            return
        }
        if (mistakeKey.startsWith("BCS_")) {
            val remainder = mistakeKey.removePrefix("BCS_")
            val matchedProfile = com.example.math.BcsExamProfile.entries
                .sortedByDescending { it.id.length }
                .firstOrNull { remainder.startsWith("${it.id}_") }
            val safeProfile = matchedProfile?.id ?: "BCS_50TH_PRELIM_2025"
            val rawQId = if (matchedProfile != null) {
                remainder.removePrefix("${matchedProfile.id}_")
            } else {
                remainder
            }
            if (rawQId.isNotBlank()) {
                val canonicalQId = MathRepository.extractCanonicalBcsQuestionId(rawQId)
                val isDefaultProfile = safeProfile == "BCS_50TH_PRELIM_2025"
                val scopedKey = if (!isDefaultProfile) "$safeProfile::$canonicalQId" else canonicalQId
                val existingBcs = getBcsMistakeById(scopedKey)
                    ?: getBcsMistakeById(rawQId)?.takeIf { it.belongsToProfile(safeProfile) }
                    ?: (if (isDefaultProfile) getBcsMistakeById(canonicalQId) else null)
                if (existingBcs != null && existingBcs.isResolved) {
                    upsertBcsMistake(
                        existingBcs.copy(
                            isResolved = false,
                            nextReviewEpochMs = nowMs
                        )
                    )
                }
                val keysToUnresolve = buildList {
                    add(mistakeKey)
                    add("BCS_${safeProfile}_$canonicalQId")
                    if (isDefaultProfile) add("BCS_$canonicalQId")
                }.distinct()
                for (key in keysToUnresolve) {
                    val unifiedRow = getUnifiedMistakeByKey(key)
                    if (unifiedRow != null && unifiedRow.resolved) {
                        upsertUnifiedMistake(unifiedRow.copy(resolved = false))
                    }
                }
                return
            }
        }
        val existing = getUnifiedMistakeByKey(mistakeKey) ?: return
        if (existing.resolved) {
            upsertUnifiedMistake(
                existing.copy(
                    resolved = false
                )
            )
        }
    }

    @Transaction
    suspend fun deleteBcsMistakeTransaction(questionId: String, examProfileId: String) {
        val rawQId = MathRepository.extractCanonicalBcsQuestionId(questionId)
        val safeProfile = examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
        deleteBcsMistakeById(questionId)
        if (examProfileId.isNotBlank() && examProfileId != "BCS_50TH_PRELIM_2025") {
            deleteBcsMistakeById("$examProfileId::$rawQId")
        }
        if (rawQId != questionId) {
            deleteBcsMistakeById(rawQId)
        }
        deleteUnifiedMistakeByKey("BCS_${safeProfile}_$rawQId")
        deleteUnifiedMistakeByKey("BCS_$rawQId")
    }

    @Transaction
    suspend fun finalizeExamAttemptTransaction(
        completedAttempt: ExamAttemptEntity,
        perTopicStats: List<TopicExamStat>,
        bcsTopicUpdates: List<BcsTopicExamUpdate>,
        questionMistakeOutcomes: List<ExamQuestionMistakeOutcome>,
        pendingBcsGoalCredit: BcsGoalCreditRecordEntity? = null
    ) {
        val now = completedAttempt.completedAt ?: System.currentTimeMillis()
        val finalAttempt = if (completedAttempt.completedAt != null) {
            completedAttempt
        } else {
            completedAttempt.copy(completedAt = now)
        }
        if (pendingBcsGoalCredit != null) {
            require(finalAttempt.attemptId.isNotBlank() && pendingBcsGoalCredit.attemptId == finalAttempt.attemptId) {
                "Pending BCS goal credit attemptId '${pendingBcsGoalCredit.attemptId}' does not match finalized attempt '${finalAttempt.attemptId}'"
            }
            require(pendingBcsGoalCredit.examProfileId.isNotBlank()) {
                "Pending BCS goal credit examProfileId must not be blank"
            }
            if (finalAttempt.examProfileId.isNotBlank()) {
                require(pendingBcsGoalCredit.examProfileId == finalAttempt.examProfileId) {
                    "Pending BCS goal credit examProfileId '${pendingBcsGoalCredit.examProfileId}' does not match finalized attempt profile '${finalAttempt.examProfileId}'"
                }
            }
            require(pendingBcsGoalCredit.targetDateKey.isNotBlank()) {
                "Pending BCS goal credit targetDateKey must not be blank"
            }
            require(pendingBcsGoalCredit.status == BcsGoalCreditRecordEntity.STATUS_PENDING) {
                "Pending BCS goal credit record must have status PENDING, got '${pendingBcsGoalCredit.status}'"
            }
            require(pendingBcsGoalCredit.creditedAtMs == null) {
                "Pending BCS goal credit record must not specify creditedAtMs"
            }
            require(
                pendingBcsGoalCredit.questionsDelta >= 0 &&
                    pendingBcsGoalCredit.studyTimeMinutesDelta >= 0 &&
                    pendingBcsGoalCredit.mockExamsDelta >= 0 &&
                    pendingBcsGoalCredit.revisionsDelta >= 0
            ) {
                "Pending BCS goal credit deltas must be non-negative"
            }
            val hasAnyCredit = pendingBcsGoalCredit.questionsDelta > 0 ||
                pendingBcsGoalCredit.studyTimeMinutesDelta > 0 ||
                pendingBcsGoalCredit.mockExamsDelta > 0 ||
                pendingBcsGoalCredit.revisionsDelta > 0
            require(hasAnyCredit) {
                "Pending BCS goal credit record must contain positive credit"
            }
        }
        upsertExamAttempt(finalAttempt)
        if (pendingBcsGoalCredit != null) {
            insertBcsGoalCreditRecordStrict(pendingBcsGoalCredit)
        }

        val roundedPct = finalAttempt.percentage.roundToInt().coerceIn(0, 100)
        insertQuizScore(
            QuizScoreEntity(
                topic = finalAttempt.selectedTopicsCsv.substringBefore(",").ifBlank { "MIXED" },
                topicTitleBn = finalAttempt.examTitleBn,
                score = finalAttempt.correctCount,
                totalQuestions = finalAttempt.totalQuestions,
                percentage = roundedPct,
                timestamp = now
            )
        )

        // Batch TopicAnalytics reads & upserts
        val effectiveTopicStats = if (perTopicStats.isNotEmpty()) {
            perTopicStats
        } else {
            listOf(
                TopicExamStat(
                    topicId = completedAttempt.selectedTopicsCsv.ifBlank { "MIXED" },
                    topicTitleBn = completedAttempt.examTitleBn,
                    totalQuestions = completedAttempt.totalQuestions,
                    correctCount = completedAttempt.correctCount,
                    wrongCount = completedAttempt.wrongCount,
                    unansweredCount = completedAttempt.unansweredCount,
                    timeSpentSeconds = completedAttempt.elapsedSeconds
                )
            )
        }
        val topicKeys = effectiveTopicStats.map { "QUIZ_${it.topicId}_${completedAttempt.difficulty}" }.distinct()
        val existingTopicMap = HashMap<String, TopicAnalyticsEntity>(topicKeys.size)
        topicKeys.chunked(200).forEach { chunk ->
            getTopicAnalyticsByKeys(chunk).forEach { existingTopicMap[it.analyticsKey] = it }
        }
        val topicsToUpsert = LinkedHashMap<String, TopicAnalyticsEntity>(effectiveTopicStats.size)
        effectiveTopicStats.forEach { stat ->
            val key = "QUIZ_${stat.topicId}_${completedAttempt.difficulty}"
            val current = topicsToUpsert[key] ?: existingTopicMap[key]
            val attemptedDelta = stat.correctCount + stat.wrongCount
            topicsToUpsert[key] = if (current == null) {
                TopicAnalyticsEntity(
                    analyticsKey = key,
                    module = "QUIZ",
                    topicId = stat.topicId,
                    topicTitleBn = stat.topicTitleBn,
                    difficulty = completedAttempt.difficulty,
                    totalAttempted = max(0, attemptedDelta),
                    totalCorrect = max(0, stat.correctCount),
                    totalWrong = max(0, stat.wrongCount),
                    bestStreak = max(0, stat.correctCount),
                    totalTimeSeconds = max(0, stat.timeSpentSeconds),
                    lastPracticedAt = now
                )
            } else {
                current.copy(
                    totalAttempted = current.totalAttempted + max(0, attemptedDelta),
                    totalCorrect = current.totalCorrect + max(0, stat.correctCount),
                    totalWrong = current.totalWrong + max(0, stat.wrongCount),
                    bestStreak = max(current.bestStreak, stat.correctCount),
                    totalTimeSeconds = current.totalTimeSeconds + max(0, stat.timeSpentSeconds),
                    lastPracticedAt = now
                )
            }
        }
        if (topicsToUpsert.isNotEmpty()) {
            upsertTopicAnalyticsList(topicsToUpsert.values.toList())
        }

        // Single QUIZ practice_history row
        insertPracticeHistory(
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

        // Daily study delta
        applyDailyStudyDeltaInsideTransaction(
            dateKey = MathRepository.todayDateKey(now),
            questionsAttemptedDelta = completedAttempt.correctCount + completedAttempt.wrongCount,
            correctDelta = completedAttempt.correctCount,
            wrongDelta = completedAttempt.wrongCount,
            studyTimeSecondsDelta = max(1, completedAttempt.elapsedSeconds),
            examsCompletedDelta = 1,
            practiceSessionsCompletedDelta = 0,
            timestampMs = now
        )

        // Batch BCS Topic Progress reads & upserts
        if (bcsTopicUpdates.isNotEmpty()) {
            val resolvedKeys = bcsTopicUpdates.map { upd ->
                val rawTopicId = upd.topicId.substringAfter("::", upd.topicId)
                if (upd.examProfileId == "BCS_50TH_PRELIM_2025" || upd.examProfileId.isBlank()) {
                    rawTopicId
                } else {
                    BcsTopicProgressEntity.buildScopedKey(upd.examProfileId, rawTopicId)
                }
            }.distinct()
            val existingBcsTopics = HashMap<String, BcsTopicProgressEntity>(resolvedKeys.size)
            resolvedKeys.chunked(200).forEach { chunk ->
                getBcsTopicProgressByIds(chunk).forEach { existingBcsTopics[it.topicId] = it }
            }
            val bcsTopicsToUpsert = LinkedHashMap<String, BcsTopicProgressEntity>(bcsTopicUpdates.size)
            bcsTopicUpdates.forEach { upd ->
                val rawTopicId = upd.topicId.substringAfter("::", upd.topicId)
                val resolvedKey = if (upd.examProfileId == "BCS_50TH_PRELIM_2025" || upd.examProfileId.isBlank()) {
                    rawTopicId
                } else {
                    BcsTopicProgressEntity.buildScopedKey(upd.examProfileId, rawTopicId)
                }
                val existing = bcsTopicsToUpsert[resolvedKey] ?: existingBcsTopics[resolvedKey]
                val attemptedDelta = if (upd.questionsAttemptedDelta > 0) upd.questionsAttemptedDelta else if (upd.miniCheckPassed) 1 else 0
                val correctDelta = if (upd.questionsCorrectDelta > 0) upd.questionsCorrectDelta else if (upd.miniCheckPassed) 1 else 0
                bcsTopicsToUpsert[resolvedKey] = if (existing == null) {
                    val baseMastery = when {
                        upd.miniCheckPassed && upd.isGuideCompleted -> 100
                        upd.isGuideCompleted -> 70
                        attemptedDelta > 0 -> ((correctDelta * 100) / attemptedDelta.coerceAtLeast(1)).coerceIn(20, 95)
                        else -> 40
                    }
                    BcsTopicProgressEntity(
                        topicId = resolvedKey,
                        subjectTrack = upd.subjectTrack,
                        isGuideCompleted = upd.isGuideCompleted,
                        miniCheckPassed = upd.miniCheckPassed,
                        questionsAttempted = attemptedDelta,
                        questionsCorrect = correctDelta,
                        totalTimeSeconds = upd.additionalTimeSeconds,
                        masteryPercent = baseMastery,
                        lastStudiedAt = now,
                        examProfileId = upd.examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
                    )
                } else {
                    val guideDone = existing.isGuideCompleted || upd.isGuideCompleted
                    val checkDone = existing.miniCheckPassed || upd.miniCheckPassed
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
                        totalTimeSeconds = existing.totalTimeSeconds + upd.additionalTimeSeconds,
                        masteryPercent = computedMastery,
                        lastStudiedAt = now,
                        examProfileId = upd.examProfileId.ifBlank { existing.examProfileId }
                    )
                }
            }
            if (bcsTopicsToUpsert.isNotEmpty()) {
                upsertBcsTopicProgressList(bcsTopicsToUpsert.values.toList())
            }
        }

        // Batch Unified & BCS Mistake updates (unified_mistake_notebook MUST execute after prior tables)
        if (questionMistakeOutcomes.isNotEmpty()) {
            val unifiedKeysToQuery = LinkedHashSet<String>(questionMistakeOutcomes.size * 2)
            val bcsKeysToQuery = LinkedHashSet<String>(questionMistakeOutcomes.size * 2)
            questionMistakeOutcomes.forEach { o ->
                if (o.isBcsSession) {
                    val qId = o.bcsQuestionId.ifBlank { "bcs_q_${kotlin.math.abs(o.questionPromptBn.hashCode())}" }
                    val rawQId = MathRepository.extractCanonicalBcsQuestionId(qId)
                    val safeProfile = o.examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
                    val isDefaultProfile = safeProfile == "BCS_50TH_PRELIM_2025"
                    val resolvedBcsKey = if (isDefaultProfile) rawQId else "$safeProfile::$rawQId"
                    bcsKeysToQuery.add(resolvedBcsKey)
                    unifiedKeysToQuery.add("BCS_${safeProfile}_$rawQId")
                    if (o.isCorrect) {
                        if (o.bcsQuestionId.isNotBlank()) {
                            bcsKeysToQuery.add(o.bcsQuestionId)
                        }
                        if (isDefaultProfile) {
                            bcsKeysToQuery.add(rawQId)
                            unifiedKeysToQuery.add("BCS_$rawQId")
                        }
                    }
                } else {
                    unifiedKeysToQuery.add(o.unifiedMistakeKey)
                }
            }

            val existingUnified = HashMap<String, UnifiedMistakeEntity>(unifiedKeysToQuery.size)
            unifiedKeysToQuery.toList().chunked(200).forEach { chunk ->
                getUnifiedMistakesByKeys(chunk).forEach { existingUnified[it.mistakeKey] = it }
            }
            val existingBcs = HashMap<String, BcsMistakeEntity>(bcsKeysToQuery.size)
            if (bcsKeysToQuery.isNotEmpty()) {
                bcsKeysToQuery.toList().chunked(200).forEach { chunk ->
                    getBcsMistakesByIds(chunk).forEach { existingBcs[it.questionId] = it }
                }
            }

            val unifiedToUpsert = LinkedHashMap<String, UnifiedMistakeEntity>()
            val bcsToUpsert = LinkedHashMap<String, BcsMistakeEntity>()

            questionMistakeOutcomes.forEach { o ->
                if (!o.isCorrect && o.shouldRecordMistake) {
                    if (o.isBcsSession) {
                        val qId = o.bcsQuestionId.ifBlank { "bcs_q_${kotlin.math.abs(o.questionPromptBn.hashCode())}" }
                        val rawQId = MathRepository.extractCanonicalBcsQuestionId(qId)
                        val safeProfile = o.examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
                        val resolvedBcsKey = if (safeProfile == "BCS_50TH_PRELIM_2025") rawQId else "$safeProfile::$rawQId"
                        val prevBcs = bcsToUpsert[resolvedBcsKey] ?: existingBcs[resolvedBcsKey]
                        bcsToUpsert[resolvedBcsKey] = if (prevBcs == null) {
                            BcsMistakeEntity(
                                questionId = resolvedBcsKey,
                                subjectTrack = "MATH_REASONING",
                                topicId = o.bcsTopicId,
                                questionPromptBn = o.questionPromptBn,
                                wrongAnswerBn = o.userAnswerBn,
                                correctAnswerBn = o.correctAnswerBn,
                                explanationBn = o.diagnosticExplanationBn,
                                shortcutTipBn = "",
                                provenanceBadgeBn = "",
                                mistakeCount = 1,
                                retrySuccessCount = 0,
                                isResolved = false,
                                nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(0, now),
                                intervalStage = 0,
                                lastMistakeAt = now,
                                examProfileId = safeProfile
                            )
                        } else {
                            prevBcs.copy(
                                wrongAnswerBn = o.userAnswerBn,
                                correctAnswerBn = o.correctAnswerBn,
                                explanationBn = o.diagnosticExplanationBn,
                                mistakeCount = prevBcs.mistakeCount + 1,
                                isResolved = false,
                                nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(0, now),
                                intervalStage = 0,
                                lastMistakeAt = now,
                                examProfileId = safeProfile
                            )
                        }
                        val bcsUnifiedKey = "BCS_${safeProfile}_$rawQId"
                        val prevBcsUnified = unifiedToUpsert[bcsUnifiedKey] ?: existingUnified[bcsUnifiedKey]
                        unifiedToUpsert[bcsUnifiedKey] = buildRecordedUnifiedMistake(
                            existing = prevBcsUnified,
                            mistakeKey = bcsUnifiedKey,
                            sourceModule = "BCS",
                            subjectId = o.bcsTopicId.ifBlank { "MATH_REASONING" },
                            curriculumTrack = "BCS",
                            questionPromptBn = o.questionPromptBn,
                            userAnswerBn = o.userAnswerBn,
                            correctAnswerBn = o.correctAnswerBn,
                            diagnosticExplanationBn = o.diagnosticExplanationBn,
                            nowMs = now
                        )
                    } else {
                        val prevUnified = unifiedToUpsert[o.unifiedMistakeKey] ?: existingUnified[o.unifiedMistakeKey]
                        unifiedToUpsert[o.unifiedMistakeKey] = buildRecordedUnifiedMistake(
                            existing = prevUnified,
                            mistakeKey = o.unifiedMistakeKey,
                            sourceModule = o.sourceModule,
                            subjectId = o.subjectId,
                            curriculumTrack = o.curriculumTrack,
                            questionPromptBn = o.questionPromptBn,
                            userAnswerBn = o.userAnswerBn,
                            correctAnswerBn = o.correctAnswerBn,
                            diagnosticExplanationBn = o.diagnosticExplanationBn,
                            nowMs = now
                        )
                    }
                } else if (o.isCorrect) {
                    if (o.isBcsSession) {
                        val qId = o.bcsQuestionId.ifBlank { "bcs_q_${kotlin.math.abs(o.questionPromptBn.hashCode())}" }
                        val rawQId = MathRepository.extractCanonicalBcsQuestionId(qId)
                        val safeProfile = o.examProfileId.ifBlank { "BCS_50TH_PRELIM_2025" }
                        val isDefaultProfile = safeProfile == "BCS_50TH_PRELIM_2025"
                        val scopedKey = if (!isDefaultProfile) {
                            "$safeProfile::$rawQId"
                        } else {
                            rawQId
                        }
                        val prevBcs = bcsToUpsert[scopedKey] ?: existingBcs[scopedKey]
                            ?: (bcsToUpsert[o.bcsQuestionId] ?: existingBcs[o.bcsQuestionId])?.takeIf { it.belongsToProfile(safeProfile) }
                            ?: if (isDefaultProfile) {
                                bcsToUpsert[rawQId] ?: existingBcs[rawQId]
                            } else {
                                null
                            }
                        if (prevBcs != null && !prevBcs.isResolved) {
                            val nextStage = (prevBcs.intervalStage + 1).coerceAtMost(4)
                            bcsToUpsert[prevBcs.questionId] = prevBcs.copy(
                                isResolved = true,
                                retrySuccessCount = prevBcs.retrySuccessCount + 1,
                                intervalStage = nextStage,
                                nextReviewEpochMs = MathRepository.nextSpacedReviewEpochMs(nextStage, now)
                            )
                        }
                        val u1 = "BCS_${safeProfile}_$rawQId"
                        (unifiedToUpsert[u1] ?: existingUnified[u1])?.takeIf { !it.resolved }?.let {
                            unifiedToUpsert[u1] = buildResolvedUnifiedMistake(it, now)
                        }
                        if (isDefaultProfile) {
                            val u2 = "BCS_$rawQId"
                            (unifiedToUpsert[u2] ?: existingUnified[u2])?.takeIf { !it.resolved }?.let {
                                unifiedToUpsert[u2] = buildResolvedUnifiedMistake(it, now)
                            }
                        }
                    } else {
                        val prevUnified = unifiedToUpsert[o.unifiedMistakeKey] ?: existingUnified[o.unifiedMistakeKey]
                        if (prevUnified != null && !prevUnified.resolved) {
                            unifiedToUpsert[o.unifiedMistakeKey] = buildResolvedUnifiedMistake(prevUnified, now)
                        }
                    }
                }
            }

            if (unifiedToUpsert.isNotEmpty()) {
                upsertUnifiedMistakes(unifiedToUpsert.values.toList())
            }
            if (bcsToUpsert.isNotEmpty()) {
                upsertBcsMistakes(bcsToUpsert.values.toList())
            }
        }
    }

    private suspend fun applyDailyStudyDeltaInsideTransaction(
        dateKey: String,
        questionsAttemptedDelta: Int,
        correctDelta: Int,
        wrongDelta: Int,
        studyTimeSecondsDelta: Int,
        examsCompletedDelta: Int,
        practiceSessionsCompletedDelta: Int,
        timestampMs: Long
    ) {
        val existing = getDailyStudyActivityByDate(dateKey)
        val updated = if (existing == null) {
            val latestConfigured = getLatestDailyStudyActivity()
            DailyStudyActivityEntity(
                dateKey = dateKey,
                questionsAttempted = max(0, questionsAttemptedDelta),
                correctCount = max(0, correctDelta),
                wrongCount = max(0, wrongDelta),
                studyTimeSeconds = max(0, studyTimeSecondsDelta),
                examsCompleted = max(0, examsCompletedDelta),
                practiceSessionsCompleted = max(0, practiceSessionsCompletedDelta),
                dailyTargetQuestions = latestConfigured?.dailyTargetQuestions ?: 20,
                dailyTargetMinutes = latestConfigured?.dailyTargetMinutes ?: 15,
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
        upsertDailyStudyActivity(updated)
    }

    private suspend fun upsertUnifiedMistakeInsideTransaction(
        mistakeKey: String,
        sourceModule: String,
        subjectId: String,
        curriculumTrack: String,
        questionPromptBn: String,
        userAnswerBn: String,
        correctAnswerBn: String,
        diagnosticExplanationBn: String,
        nowMs: Long
    ) {
        val existing = getUnifiedMistakeByKey(mistakeKey)
        upsertUnifiedMistake(
            buildRecordedUnifiedMistake(
                existing = existing,
                mistakeKey = mistakeKey,
                sourceModule = sourceModule,
                subjectId = subjectId,
                curriculumTrack = curriculumTrack,
                questionPromptBn = questionPromptBn,
                userAnswerBn = userAnswerBn,
                correctAnswerBn = correctAnswerBn,
                diagnosticExplanationBn = diagnosticExplanationBn,
                nowMs = nowMs
            )
        )
    }

    private suspend fun resolveUnifiedMistakeInsideTransaction(mistakeKey: String, nowMs: Long) {
        val existing = getUnifiedMistakeByKey(mistakeKey) ?: return
        if (existing.resolved) return
        upsertUnifiedMistake(buildResolvedUnifiedMistake(existing, nowMs))
    }

    private fun buildRecordedUnifiedMistake(
        existing: UnifiedMistakeEntity?,
        mistakeKey: String,
        sourceModule: String,
        subjectId: String,
        curriculumTrack: String,
        questionPromptBn: String,
        userAnswerBn: String,
        correctAnswerBn: String,
        diagnosticExplanationBn: String,
        nowMs: Long
    ): UnifiedMistakeEntity {
        return if (existing == null) {
            UnifiedMistakeEntity(
                mistakeKey = mistakeKey,
                sourceModule = sourceModule,
                subjectId = subjectId,
                curriculumTrack = curriculumTrack,
                questionPromptBn = questionPromptBn,
                userAnswerBn = userAnswerBn,
                correctAnswerBn = correctAnswerBn,
                diagnosticExplanationBn = diagnosticExplanationBn,
                errorType = "CONCEPTUAL",
                failureCount = 1,
                reviewIntervalDays = 1,
                nextReviewAtMillis = MathRepository.nextSpacedReviewEpochMs(0, nowMs),
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
                nextReviewAtMillis = MathRepository.nextSpacedReviewEpochMs(0, nowMs),
                resolved = false,
                lastFailedAtMillis = nowMs
            )
        }
    }

    private fun buildResolvedUnifiedMistake(existing: UnifiedMistakeEntity, nowMs: Long): UnifiedMistakeEntity {
        if (existing.resolved) {
            return existing
        }
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
        return existing.copy(
            resolved = true,
            reviewIntervalDays = nextIntervalDays,
            nextReviewAtMillis = MathRepository.nextSpacedReviewEpochMs(nextStageIndex, nowMs),
            lastFailedAtMillis = existing.lastFailedAtMillis
        )
    }
}



