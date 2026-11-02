package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BookmarkEntity
import com.example.data.model.ProgressEntity
import com.example.data.model.QuizScoreEntity

@Database(
    entities = [BookmarkEntity::class, QuizScoreEntity::class, ProgressEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MathDatabase : RoomDatabase() {
    abstract fun mathDao(): MathDao

    companion object {
        @Volatile
        private var INSTANCE: MathDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_bookmarks_timestamp ON bookmarks(timestamp)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_quiz_scores_topicId ON quiz_scores(topicId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_quiz_scores_timestamp ON quiz_scores(timestamp)")
            }
        }

        fun getInstance(context: Context): MathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MathDatabase::class.java,
                    "math_master.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
