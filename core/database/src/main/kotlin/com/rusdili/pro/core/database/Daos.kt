package com.rusdili.pro.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE language = :language AND russian = :word LIMIT 1")
    suspend fun findByRussian(language: String, word: String): WordEntity?

    @Query(
        "SELECT * FROM words WHERE language = :language AND " +
            "(russian LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%') " +
            "ORDER BY russian"
    )
    fun search(language: String, query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE language = :language ORDER BY russian")
    fun observeAll(language: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE language = :language AND isFavorite = 1 ORDER BY russian")
    fun observeFavorites(language: String): Flow<List<WordEntity>>

    @Query(
        "SELECT * FROM words WHERE language = :language AND reviewStage > 0 AND nextReviewDate <= :now " +
            "ORDER BY nextReviewDate, russian LIMIT :limit"
    )
    suspend fun dueForReview(language: String, now: Long, limit: Int): List<WordEntity>

    @Query(
        "SELECT * FROM words WHERE reviewStage > 0 AND nextReviewDate <= :now " +
            "ORDER BY nextReviewDate, russian LIMIT :limit"
    )
    suspend fun dueForReviewAnyLanguage(now: Long, limit: Int): List<WordEntity>

    @Query("UPDATE words SET isFavorite = :favorite WHERE language = :language AND russian = :word")
    suspend fun setFavorite(language: String, word: String, favorite: Boolean)

    @Query(
        "UPDATE words SET nextReviewDate = :date, reviewStage = :stage " +
            "WHERE language = :language AND russian = :word"
    )
    suspend fun scheduleReview(language: String, word: String, stage: Int, date: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<WordEntity>)
}

@Dao
interface ChapterProgressDao {
    @Query("SELECT * FROM chapter_progress WHERE language = :language ORDER BY chapterId")
    fun observeByLanguage(language: String): Flow<List<ChapterProgressEntity>>

    @Query("SELECT * FROM chapter_progress WHERE language = :language AND chapterId = :chapterId LIMIT 1")
    suspend fun get(language: String, chapterId: Int): ChapterProgressEntity?

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
    @Query("SELECT * FROM flashcard_decks WHERE language = :language ORDER BY createdAt DESC")
    fun observeDecksWithWords(language: String): Flow<List<DeckWithWords>>

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

    @Query("DELETE FROM flashcard_deck_words WHERE deckId = :deckId AND wordId = :wordId")
    suspend fun removeWordFromDeck(deckId: Long, wordId: String)
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history WHERE language = :language ORDER BY searchedAt DESC LIMIT :limit")
    fun observeRecent(language: String, limit: Int): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE language = :language AND queryKey = :queryKey")
    suspend fun deleteByKey(language: String, queryKey: String)

    @Query("DELETE FROM search_history WHERE language = :language")
    suspend fun clearAll(language: String)

    @Query(
        "DELETE FROM search_history WHERE language = :language AND queryKey NOT IN " +
            "(SELECT queryKey FROM search_history WHERE language = :language ORDER BY searchedAt DESC LIMIT :limit)"
    )
    suspend fun trimToLimit(language: String, limit: Int)
}

@Dao
interface ReviewSessionDao {
    @Query("SELECT * FROM review_sessions WHERE language = :language ORDER BY completedAt DESC")
    fun observeByLanguage(language: String): Flow<List<ReviewSessionEntity>>

    @Query("SELECT * FROM review_sessions ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<ReviewSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: ReviewSessionEntity): Long
}
