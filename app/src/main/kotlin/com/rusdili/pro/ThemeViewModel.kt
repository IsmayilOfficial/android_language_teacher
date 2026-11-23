package com.rusdili.pro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.UserProgressStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val progressStore: UserProgressStore
) : ViewModel() {
    val darkThemePreference: StateFlow<Boolean?> = progressStore.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun toggleTheme(currentlyDark: Boolean) {
        viewModelScope.launch {
            progressStore.setDarkTheme(!currentlyDark)
        }
    }
}
