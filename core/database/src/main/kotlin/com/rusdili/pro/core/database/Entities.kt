package com.rusdili.pro.core.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "words",
    indices = [Index("language"), Index("russian")]
)
data class WordEntity(
    @PrimaryKey val id: String,
    val language: String = "ru",
    val russian: String,
    val translation: String,
    val gender: String?,
    val example: String,
    val aspectPair: String?,
    val isFavorite: Boolean = false,
    val nextReviewDate: Long = 0L,
    @ColumnInfo(defaultValue = "0") val reviewStage: Int = 0
)

@Entity(
    tableName = "chapter_progress",
    primaryKeys = ["language", "chapterId"],
    indices = [Index("language")]
)
data class ChapterProgressEntity(
    val language: String = "ru",
    val chapterId: Int,
    val scorePercent: Int,
    val completedAt: Long
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: String,
    val titleAz: String,
    val summaryAz: String,
    val original: String,
    val source: String,
    val publishedAt: String,
    val imageUrl: String?,
    val sourceUrl: String?,
    val unconfirmed: Boolean
)

@Entity(
    tableName = "flashcard_decks",
    indices = [Index("language")]
)
data class FlashcardDeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val language: String = "ru",
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "flashcard_deck_words",
    primaryKeys = ["deckId", "wordId"],
    foreignKeys = [
        ForeignKey(
            entity = FlashcardDeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId"), Index("wordId")]
)
data class FlashcardDeckWordEntity(
    val deckId: Long,
    val wordId: String,
    val addedAt: Long = System.currentTimeMillis()
)

data class DeckWithWords(
    @Embedded val deck: FlashcardDeckEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = FlashcardDeckWordEntity::class,
            parentColumn = "deckId",
            entityColumn = "wordId"
        )
    )
    val words: List<WordEntity>
)

@Entity(
    tableName = "search_history",
    primaryKeys = ["language", "queryKey"],
    indices = [Index("language")]
)
data class SearchHistoryEntity(
    val language: String = "ru",
    val queryKey: String,
    val displayQuery: String,
    val matchedRussian: String? = null,
    val matchedTranslation: String? = null,
    val searchedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "review_sessions",
    indices = [Index("language")]
)
data class ReviewSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val language: String = "ru",
    val sessionType: String,
    val totalItems: Int,
    val successfulItems: Int,
    val xpEarned: Int,
    val completedAt: Long = System.currentTimeMillis()
)
