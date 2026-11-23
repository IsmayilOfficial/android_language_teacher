package com.rusdili.pro.feature.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.feature.dictionary.domain.DictionaryRepository
import com.rusdili.pro.feature.dictionary.domain.SearchWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DictionaryData(
    val query: String,
    val favoritesOnly: Boolean,
    val words: List<Word>,
    val history: List<String>
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val searchWords: SearchWordUseCase,
    private val repository: DictionaryRepository,
    private val progressStore: UserProgressStore
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val favoritesOnly = MutableStateFlow(false)

    val uiState = combine(query, favoritesOnly) { text, favorites -> text to favorites }
        .flatMapLatest { (text, favorites) ->
            val wordsFlow = if (favorites) repository.favorites() else searchWords(text)
            wordsFlow.map { words -> Triple(text, favorites, words) }
        }
        .combine(progressStore.searchHistory) { result, history ->
            val (text, favorites, words) = result
            val state: UiState<DictionaryData> = UiState.Success(
                DictionaryData(text, favorites, words, history)
            )
            state
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    init {
        viewModelScope.launch {
            query.debounce(400).distinctUntilChanged().collect { value ->
                if (value.isNotBlank()) progressStore.recordSearch(value.trim())
            }
        }
    }

    fun search(value: String) {
        query.value = value
        favoritesOnly.value = false
    }

    fun showFavorites(enabled: Boolean) {
        favoritesOnly.value = enabled
    }

    fun toggleFavorite(word: Word) {
        viewModelScope.launch {
            repository.setFavorite(word.russian, !word.isFavorite)
        }
    }
}
