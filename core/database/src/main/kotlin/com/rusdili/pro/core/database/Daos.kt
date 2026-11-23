package com.rusdili.pro.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE russian = :word LIMIT 1")
    suspend fun findByRussian(word: String): WordEntity?

    @Query(
        "SELECT * FROM words WHERE russian LIKE '%' || :query || '%' " +
            "OR translation LIKE '%' || :query || '%' ORDER BY russian"
    )
    fun search(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY russian")
    fun observeAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY russian")
    fun observeFavorites(): Flow<List<WordEntity>>

    @Query(
        "SELECT * FROM words WHERE reviewStage > 0 AND nextReviewDate <= :now " +
            "ORDER BY nextReviewDate, russian LIMIT :limit"
    )
    suspend fun dueForReview(now: Long, limit: Int): List<WordEntity>

    @Query("UPDATE words SET isFavorite = :favorite WHERE russian = :word")
    suspend fun setFavorite(word: String, favorite: Boolean)

    @Query("UPDATE words SET nextReviewDate = :date, reviewStage = :stage WHERE russian = :word")
    suspend fun scheduleReview(word: String, stage: Int, date: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<WordEntity>)
}

@Dao
interface ChapterProgressDao {
    @Query("SELECT * FROM chapter_progress ORDER BY chapterId")
    fun observeAll(): Flow<List<ChapterProgressEntity>>

    @Query("SELECT * FROM chapter_progress WHERE chapterId = :chapterId LIMIT 1")
    suspend fun get(chapterId: Int): ChapterProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(progress: ChapterProgressEntity)
}

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun observeAll(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<NewsEntity>)
}
