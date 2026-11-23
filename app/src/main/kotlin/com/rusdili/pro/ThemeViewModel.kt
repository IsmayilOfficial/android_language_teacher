package com.rusdili.pro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.LearningLanguage
import com.rusdili.pro.core.common.RussianSpeech
import com.rusdili.pro.core.common.UserProgressStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface LanguagePreferenceState {
    data object Loading : LanguagePreferenceState
    data class Ready(val selectedLanguage: LearningLanguage?) : LanguagePreferenceState
}

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val progressStore: UserProgressStore,
    private val speech: RussianSpeech
) : ViewModel() {
    val darkThemePreference: StateFlow<Boolean?> = progressStore.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val languagePreferenceState: StateFlow<LanguagePreferenceState> = progressStore.selectedLanguageSelection
        .map<LearningLanguage?, LanguagePreferenceState> { LanguagePreferenceState.Ready(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LanguagePreferenceState.Loading)

    fun toggleTheme(currentlyDark: Boolean) {
        viewModelScope.launch {
            progressStore.setDarkTheme(!currentlyDark)
        }
    }

    fun selectLanguage(language: LearningLanguage) {
        speech.stop()
        viewModelScope.launch {
            progressStore.setSelectedLanguage(language)
        }
    }

    fun previewGreeting(language: LearningLanguage) {
        speech.speak(language.greeting, language = language)
    }

    override fun onCleared() {
        speech.stop()
        super.onCleared()
    }
}
