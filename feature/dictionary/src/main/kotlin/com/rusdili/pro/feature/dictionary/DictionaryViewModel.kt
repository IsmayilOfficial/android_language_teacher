package com.rusdili.pro.feature.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.FlashcardDeck
import com.rusdili.pro.core.common.RussianSpeech
import com.rusdili.pro.core.common.SearchHistoryEntry
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.feature.dictionary.domain.DictionaryRepository
import com.rusdili.pro.feature.dictionary.domain.FlashcardDeckRepository
import com.rusdili.pro.feature.dictionary.domain.SearchWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DictionarySection { ALL, FAVORITES, FLASHCARDS, HISTORY }

data class DictionaryData(
    val query: String,
    val section: DictionarySection,
    val words: List<Word>,
    val allWords: List<Word>,
    val favoriteWords: List<Word>,
    val decks: List<FlashcardDeck>,
    val history: List<SearchHistoryEntry>
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val searchWords: SearchWordUseCase,
    private val repository: DictionaryRepository,
    private val deckRepository: FlashcardDeckRepository,
    private val progressStore: UserProgressStore,
    private val speech: RussianSpeech
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val section = MutableStateFlow(DictionarySection.ALL)

    private val filteredWordsFlow = combine(query, section) { text, activeSection -> text to activeSection }
        .flatMapLatest { (text, activeSection) ->
            when (activeSection) {
                DictionarySection.FAVORITES -> repository.favorites()
                else -> searchWords(text)
            }
        }

    private val wordCollectionsFlow = combine(
        searchWords(""),
        repository.favorites(),
        deckRepository.observeDecks(),
        repository.recentSearches()
    ) { allWords, favoriteWords, decks, history ->
        WordCollections(allWords, favoriteWords, decks, history)
    }

    val uiState = combine(
        query,
        section,
        filteredWordsFlow,
        wordCollectionsFlow
    ) { text, activeSection, displayedWords, collections ->
        val state: UiState<DictionaryData> = UiState.Success(
            DictionaryData(
                query = text,
                section = activeSection,
                words = displayedWords,
                allWords = collections.allWords,
                favoriteWords = collections.favoriteWords,
                decks = collections.decks,
                history = collections.history
            )
        )
        state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    private data class WordCollections(
        val allWords: List<Word>,
        val favoriteWords: List<Word>,
        val decks: List<FlashcardDeck>,
        val history: List<SearchHistoryEntry>
    )

    init {
        viewModelScope.launch {
            query.debounce(550).distinctUntilChanged().collect { value ->
                if (value.isNotBlank()) repository.recordSearch(value.trim())
            }
        }
    }

    fun search(value: String) {
        query.value = value
        if (section.value == DictionarySection.FLASHCARDS || section.value == DictionarySection.HISTORY) {
            section.value = DictionarySection.ALL
        }
    }

    fun selectSection(target: DictionarySection) {
        section.value = target
    }

    fun toggleFavorite(word: Word) {
        viewModelScope.launch {
            repository.setFavorite(word.russian, !word.isFavorite)
        }
    }

    fun recordWordLookup(word: Word) {
        viewModelScope.launch {
            repository.recordSearch(word.russian, matchedWord = word)
        }
    }

    fun selectHistoryEntry(entry: SearchHistoryEntry) {
        val searchTerm = entry.matchedRussian ?: entry.query
        query.value = searchTerm
        section.value = DictionarySection.ALL
        viewModelScope.launch {
            repository.recordSearch(searchTerm)
        }
    }

    fun removeHistoryEntry(queryKey: String) {
        viewModelScope.launch {
            repository.removeSearchHistoryEntry(queryKey)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    fun createDeck(name: String, description: String, initialWords: List<String>) {
        if (name.isBlank()) return
        viewModelScope.launch {
            deckRepository.createDeck(name, description, initialWords)
        }
    }

    fun deleteDeck(deckId: Long) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deckId)
        }
    }

    fun toggleWordInDeck(deckId: Long, wordRussian: String, currentlyInDeck: Boolean) {
        viewModelScope.launch {
            if (currentlyInDeck) {
                deckRepository.removeWordFromDeck(deckId, wordRussian)
            } else {
                deckRepository.addWordToDeck(deckId, wordRussian)
            }
        }
    }

    fun recordFlashcardReview(known: Boolean) {
        viewModelScope.launch {
            if (known) {
                progressStore.addXp(AppConstants.XP_PER_CORRECT_ANSWER / 2)
            } else {
                progressStore.recordDailyActivity()
            }
        }
    }

    fun speak(text: String, slow: Boolean = false) {
        speech.speak(text, slow)
    }

    fun stopSpeech() {
        speech.stop()
    }

    override fun onCleared() {
        speech.stop()
        super.onCleared()
    }
}
