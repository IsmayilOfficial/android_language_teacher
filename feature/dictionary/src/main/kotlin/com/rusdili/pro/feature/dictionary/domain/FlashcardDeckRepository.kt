package com.rusdili.pro.feature.dictionary.domain

import com.rusdili.pro.core.common.FlashcardDeck
import com.rusdili.pro.core.common.LearningLanguage
import kotlinx.coroutines.flow.Flow

interface FlashcardDeckRepository {
    fun observeDecks(language: LearningLanguage): Flow<List<FlashcardDeck>>
    suspend fun createDeck(
        language: LearningLanguage,
        name: String,
        description: String,
        initialWords: List<String> = emptyList()
    ): Long
    suspend fun deleteDeck(deckId: Long)
    suspend fun addWordToDeck(language: LearningLanguage, deckId: Long, wordRussian: String)
    suspend fun removeWordFromDeck(language: LearningLanguage, deckId: Long, wordRussian: String)
}
