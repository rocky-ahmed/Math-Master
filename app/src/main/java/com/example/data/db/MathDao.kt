package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyStudyActivityEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.PracticeHistoryEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity
import com.example.data.model.RecentFormulaEntity
import com.example.data.model.SolverHistoryEntity
import com.example.data.model.TopicAnalyticsEntity
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

    // User Progress
    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ProgressEntity)

    // Practice & Challenge History (Room v3)
    @Query("SELECT * FROM practice_history ORDER BY timestamp DESC LIMIT 200")
    fun getAllPracticeHistory(): Flow<List<PracticeHistoryEntity>>

    @Query("SELECT * FROM practice_history WHERE module = :module ORDER BY timestamp DESC LIMIT 50")
    fun getPracticeHistoryByModule(module: String): Flow<List<PracticeHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeHistory(history: PracticeHistoryEntity)

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

    // Daily Study Activity (Room v4)
    @Query("SELECT * FROM daily_study_activity ORDER BY dateKey DESC LIMIT 120")
    fun getAllDailyStudyActivity(): Flow<List<DailyStudyActivityEntity>>

    @Query("SELECT * FROM daily_study_activity WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getDailyStudyActivityByDate(dateKey: String): DailyStudyActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyStudyActivity(activity: DailyStudyActivityEntity)
}
