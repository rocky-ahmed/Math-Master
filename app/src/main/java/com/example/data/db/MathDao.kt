package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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
import com.example.data.model.TopicAnalyticsEntity
import com.example.data.model.UnifiedMistakeEntity
import kotlinx.coroutines.flow.Flow

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizScore(score: QuizScoreEntity)

    @Query("DELETE FROM quiz_scores WHERE id = :id")
    suspend fun deleteQuizScoreById(id: Int)

    @Query("SELECT * FROM quiz_scores WHERE timestamp BETWEEN :minTimestamp AND :maxTimestamp ORDER BY ABS(timestamp - :targetTimestamp) ASC")
    suspend fun getQuizScoresInTimeWindow(minTimestamp: Long, maxTimestamp: Long, targetTimestamp: Long): List<QuizScoreEntity>

    @Query("DELETE FROM quiz_scores WHERE timestamp BETWEEN :minTimestamp AND :maxTimestamp")
    suspend fun deleteQuizScoresInTimeWindow(minTimestamp: Long, maxTimestamp: Long)

    // User Progress
    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ProgressEntity)

    // Practice & Challenge History (Room v3) — full historical completeness without arbitrary row truncation
    @Query("SELECT * FROM practice_history ORDER BY timestamp DESC")
    fun getAllPracticeHistory(): Flow<List<PracticeHistoryEntity>>

    @Query("SELECT * FROM practice_history WHERE module = :module ORDER BY timestamp DESC")
    fun getPracticeHistoryByModule(module: String): Flow<List<PracticeHistoryEntity>>

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopicAnalytics(analytics: TopicAnalyticsEntity)

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

    // Solver History (Room v4)
    @Query("SELECT * FROM solver_history ORDER BY timestamp DESC LIMIT 100")
    fun getAllSolverHistory(): Flow<List<SolverHistoryEntity>>

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsTopicProgress(progress: BcsTopicProgressEntity)

    // BCS Mistake Notebook (Room v6 / v8)
    @Query("SELECT * FROM bcs_mistake_notebook ORDER BY lastMistakeAt DESC")
    fun getAllBcsMistakes(): Flow<List<BcsMistakeEntity>>

    @Query("SELECT * FROM bcs_mistake_notebook WHERE examProfileId = :examProfileId ORDER BY lastMistakeAt DESC")
    fun getBcsMistakesByProfile(examProfileId: String): Flow<List<BcsMistakeEntity>>

    @Query("SELECT * FROM bcs_mistake_notebook WHERE questionId = :questionId LIMIT 1")
    suspend fun getBcsMistakeById(questionId: String): BcsMistakeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsMistake(mistake: BcsMistakeEntity)

    @Query("DELETE FROM bcs_mistake_notebook WHERE questionId = :questionId")
    suspend fun deleteBcsMistakeById(questionId: String)

    // BCS Study Plan Goals (Room v8)
    @Query("SELECT * FROM bcs_study_goals ORDER BY dateKey DESC, updatedAt DESC LIMIT 180")
    fun getAllBcsStudyGoals(): Flow<List<BcsStudyPlanGoalEntity>>

    @Query("SELECT * FROM bcs_study_goals WHERE goalKey = :goalKey LIMIT 1")
    suspend fun getBcsStudyGoalByKey(goalKey: String): BcsStudyPlanGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBcsStudyGoal(goal: BcsStudyPlanGoalEntity)

    // Unified Mistake Notebook (Room v7)
    @Query("SELECT * FROM unified_mistake_notebook ORDER BY lastFailedAtMillis DESC")
    fun getAllUnifiedMistakes(): Flow<List<UnifiedMistakeEntity>>

    @Query("SELECT * FROM unified_mistake_notebook WHERE mistakeKey = :key LIMIT 1")
    suspend fun getUnifiedMistakeByKey(key: String): UnifiedMistakeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUnifiedMistake(mistake: UnifiedMistakeEntity)

    @Query("DELETE FROM unified_mistake_notebook WHERE mistakeKey = :key")
    suspend fun deleteUnifiedMistakeByKey(key: String)

    // Formula Mastery State (Room v7)
    @Query("SELECT * FROM formula_mastery_state ORDER BY lastPracticedAtMillis DESC")
    fun getAllFormulaMastery(): Flow<List<FormulaMasteryEntity>>

    @Query("SELECT * FROM formula_mastery_state WHERE formulaId = :formulaId LIMIT 1")
    suspend fun getFormulaMasteryById(formulaId: String): FormulaMasteryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFormulaMastery(state: FormulaMasteryEntity)
}

