package com.rusdili.pro.feature.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.FlashcardDeck
import com.rusdili.pro.core.common.LearningLanguage
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
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DictionarySection { ALL, FAVORITES, FLASHCARDS, HISTORY }

data class DictionaryData(
    val language: LearningLanguage = LearningLanguage.RUSSIAN,
    val query: String,
    val section: DictionarySection,
    val words: List<Word>,
    val allWords: List<Word>,
    val favoriteWords: List<Word>,
    val decks: List<FlashcardDeck>,
    val history: List<SearchHistoryEntry>,
    val speakingText: String? = null,
    val isSpeechAvailable: Boolean = true
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
    private val selectedLanguage = progressStore.selectedLanguage
        .stateIn(viewModelScope, SharingStarted.Eagerly, LearningLanguage.RUSSIAN)

    private val filteredWordsFlow = combine(
        progressStore.selectedLanguage.distinctUntilChanged(),
        query,
        section
    ) { language, text, activeSection -> Triple(language, text, activeSection) }
        .flatMapLatest { (language, text, activeSection) ->
            when (activeSection) {
                DictionarySection.FAVORITES -> repository.favorites(language)
                else -> searchWords(language, text)
            }
        }

    private val wordCollectionsFlow = progressStore.selectedLanguage
        .distinctUntilChanged()
        .flatMapLatest { language ->
            combine(
                searchWords(language, ""),
                repository.favorites(language),
                deckRepository.observeDecks(language),
                repository.recentSearches(language)
            ) { allWords, favoriteWords, decks, history ->
                WordCollections(language, allWords, favoriteWords, decks, history)
            }
        }

    private val speechStateFlow = combine(
        speech.speakingText,
        speech.isAvailable
    ) { speaking, available -> speaking to available }

    val uiState = combine(
        query,
        section,
        filteredWordsFlow,
        wordCollectionsFlow,
        speechStateFlow
    ) { text, activeSection, displayedWords, collections, (speaking, available) ->
        val state: UiState<DictionaryData> = UiState.Success(
            DictionaryData(
                language = collections.language,
                query = text,
                section = activeSection,
                words = displayedWords,
                allWords = collections.allWords,
                favoriteWords = collections.favoriteWords,
                decks = collections.decks,
                history = collections.history,
                speakingText = speaking,
                isSpeechAvailable = available
            )
        )
        state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    private data class WordCollections(
        val language: LearningLanguage,
        val allWords: List<Word>,
        val favoriteWords: List<Word>,
        val decks: List<FlashcardDeck>,
        val history: List<SearchHistoryEntry>
    )

    init {
        viewModelScope.launch {
            progressStore.selectedLanguage
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    query.value = ""
                    section.value = DictionarySection.ALL
                    speech.stop()
                }
        }
        viewModelScope.launch {
            query.debounce(550).distinctUntilChanged().collect { value ->
                if (value.isNotBlank()) {
                    repository.recordSearch(selectedLanguage.value, value.trim())
                }
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
        val lang = LearningLanguage.fromCode(word.language)
        viewModelScope.launch {
            repository.setFavorite(lang, word.russian, !word.isFavorite)
        }
    }

    fun recordWordLookup(word: Word) {
        val lang = LearningLanguage.fromCode(word.language)
        viewModelScope.launch {
            repository.recordSearch(lang, word.russian, matchedWord = word)
        }
    }

    fun selectHistoryEntry(entry: SearchHistoryEntry) {
        val lang = LearningLanguage.fromCode(entry.language)
        val searchTerm = entry.matchedRussian ?: entry.query
        query.value = searchTerm
        section.value = DictionarySection.ALL
        viewModelScope.launch {
            repository.recordSearch(lang, searchTerm)
        }
    }

    fun removeHistoryEntry(queryKey: String) {
        val lang = selectedLanguage.value
        viewModelScope.launch {
            repository.removeSearchHistoryEntry(lang, queryKey)
        }
    }

    fun clearHistory() {
        val lang = selectedLanguage.value
        viewModelScope.launch {
            repository.clearSearchHistory(lang)
        }
    }

    fun createDeck(name: String, description: String, initialWords: List<String>) {
        if (name.isBlank()) return
        val lang = selectedLanguage.value
        viewModelScope.launch {
            deckRepository.createDeck(lang, name, description, initialWords)
        }
    }

    fun deleteDeck(deckId: Long) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deckId)
        }
    }

    fun toggleWordInDeck(deckId: Long, wordRussian: String, currentlyInDeck: Boolean) {
        val lang = selectedLanguage.value
        viewModelScope.launch {
            if (currentlyInDeck) {
                deckRepository.removeWordFromDeck(lang, deckId, wordRussian)
            } else {
                deckRepository.addWordToDeck(lang, deckId, wordRussian)
            }
        }
    }

    fun recordFlashcardReview(known: Boolean) {
        val lang = selectedLanguage.value
        viewModelScope.launch {
            if (known) {
                progressStore.addXp(AppConstants.XP_PER_CORRECT_ANSWER / 2, lang)
            } else {
                progressStore.recordDailyActivity(lang)
            }
        }
    }

    fun speak(text: String, slow: Boolean = false) {
        speech.speak(text, slow, language = selectedLanguage.value)
    }

    fun stopSpeech() {
        speech.stop()
    }

    override fun onCleared() {
        speech.stop()
        super.onCleared()
    }
}
