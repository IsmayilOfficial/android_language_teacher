package com.rusdili.pro.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

@Dao
interface FlashcardDeckDao {
    @Transaction
    @Query("SELECT * FROM flashcard_decks ORDER BY createdAt DESC")
    fun observeDecksWithWords(): Flow<List<DeckWithWords>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: FlashcardDeckEntity): Long

    @Query("UPDATE flashcard_decks SET name = :name, description = :description WHERE id = :deckId")
    suspend fun updateDeck(deckId: Long, name: String, description: String)

    @Query("DELETE FROM flashcard_decks WHERE id = :deckId")
    suspend fun deleteDeck(deckId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWordToDeck(crossRef: FlashcardDeckWordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWordsToDeck(crossRefs: List<FlashcardDeckWordEntity>)

    @Query("DELETE FROM flashcard_deck_words WHERE deckId = :deckId AND wordRussian = :wordRussian")
    suspend fun removeWordFromDeck(deckId: Long, wordRussian: String)
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE queryKey = :queryKey")
    suspend fun deleteByKey(queryKey: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    @Query(
        "DELETE FROM search_history WHERE queryKey NOT IN " +
            "(SELECT queryKey FROM search_history ORDER BY searchedAt DESC LIMIT :limit)"
    )
    suspend fun trimToLimit(limit: Int)
}
