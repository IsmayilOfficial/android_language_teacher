package com.rusdili.pro.feature.dictionary.data

import com.rusdili.pro.core.common.FlashcardDeck
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.database.DeckWithWords
import com.rusdili.pro.core.database.FlashcardDeckDao
import com.rusdili.pro.core.database.FlashcardDeckEntity
import com.rusdili.pro.core.database.FlashcardDeckWordEntity
import com.rusdili.pro.core.database.WordEntity
import com.rusdili.pro.feature.dictionary.domain.FlashcardDeckRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFlashcardDeckRepository @Inject constructor(
    private val deckDao: FlashcardDeckDao
) : FlashcardDeckRepository {
    override fun observeDecks(): Flow<List<FlashcardDeck>> =
        deckDao.observeDecksWithWords().map { rows -> rows.map(DeckWithWords::toDomain) }

    override suspend fun createDeck(
        name: String,
        description: String,
        initialWords: List<String>
    ): Long {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty())
        val deckId = deckDao.insertDeck(
            FlashcardDeckEntity(
                name = trimmedName,
                description = description.trim()
            )
        )
        if (initialWords.isNotEmpty()) {
            deckDao.addWordsToDeck(
                initialWords.distinct().map { russian ->
                    FlashcardDeckWordEntity(deckId = deckId, wordRussian = russian)
                }
            )
        }
        return deckId
    }

    override suspend fun deleteDeck(deckId: Long) {
        deckDao.deleteDeck(deckId)
    }

    override suspend fun addWordToDeck(deckId: Long, wordRussian: String) {
        deckDao.addWordToDeck(FlashcardDeckWordEntity(deckId = deckId, wordRussian = wordRussian))
    }

    override suspend fun removeWordFromDeck(deckId: Long, wordRussian: String) {
        deckDao.removeWordFromDeck(deckId, wordRussian)
    }
}

private fun DeckWithWords.toDomain() = FlashcardDeck(
    id = deck.id,
    name = deck.name,
    description = deck.description,
    words = words.sortedBy { it.russian }.map(WordEntity::toDomain),
    createdAt = deck.createdAt
)

private fun WordEntity.toDomain() = Word(
    russian = russian,
    translation = translation,
    gender = gender,
    example = example,
    aspectPair = aspectPair,
    isFavorite = isFavorite,
    nextReviewDate = nextReviewDate,
    reviewStage = reviewStage
)

@Module
@InstallIn(SingletonComponent::class)
abstract class FlashcardDeckRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFlashcardDeckRepository(
        implementation: RoomFlashcardDeckRepository
    ): FlashcardDeckRepository
}
