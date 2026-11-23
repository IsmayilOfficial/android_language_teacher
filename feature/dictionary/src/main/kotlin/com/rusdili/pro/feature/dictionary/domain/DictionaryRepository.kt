package com.rusdili.pro.feature.dictionary.domain

import com.rusdili.pro.core.common.Word
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    fun search(query: String): Flow<List<Word>>
    fun favorites(): Flow<List<Word>>
    suspend fun setFavorite(word: String, favorite: Boolean)
}

class SearchWordUseCase(private val repository: DictionaryRepository) {
    operator fun invoke(query: String): Flow<List<Word>> = repository.search(query)
}
