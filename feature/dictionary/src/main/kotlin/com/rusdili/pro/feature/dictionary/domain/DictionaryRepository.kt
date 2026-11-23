package com.rusdili.pro.feature.dictionary.domain

import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.LearningLanguage
import com.rusdili.pro.core.common.SearchHistoryEntry
import com.rusdili.pro.core.common.Word
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    fun search(language: LearningLanguage, query: String): Flow<List<Word>>
    fun favorites(language: LearningLanguage): Flow<List<Word>>
    suspend fun setFavorite(language: LearningLanguage, word: String, favorite: Boolean)
    fun recentSearches(
        language: LearningLanguage,
        limit: Int = AppConstants.MAX_SEARCH_HISTORY
    ): Flow<List<SearchHistoryEntry>>
    suspend fun recordSearch(language: LearningLanguage, query: String, matchedWord: Word? = null)
    suspend fun removeSearchHistoryEntry(language: LearningLanguage, queryKey: String)
    suspend fun clearSearchHistory(language: LearningLanguage)
}

class SearchWordUseCase(private val repository: DictionaryRepository) {
    operator fun invoke(language: LearningLanguage, query: String): Flow<List<Word>> =
        repository.search(language, query)
}
