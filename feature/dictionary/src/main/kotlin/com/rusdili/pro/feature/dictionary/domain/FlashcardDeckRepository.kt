package com.rusdili.pro.feature.dictionary.domain

import com.rusdili.pro.core.common.FlashcardDeck
import kotlinx.coroutines.flow.Flow

interface FlashcardDeckRepository {
    fun observeDecks(): Flow<List<FlashcardDeck>>
    suspend fun createDeck(name: String, description: String, initialWords: List<String> = emptyList()): Long
    suspend fun deleteDeck(deckId: Long)
    suspend fun addWordToDeck(deckId: Long, wordRussian: String)
    suspend fun removeWordFromDeck(deckId: Long, wordRussian: String)
}
