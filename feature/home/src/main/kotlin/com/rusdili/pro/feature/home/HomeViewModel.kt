package com.rusdili.pro.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.feature.home.domain.GetHomeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeData(
    val xpToday: Int,
    val streak: Int,
    val wordRussian: String,
    val wordTranslation: String,
    val example: String,
    val latestNews: com.rusdili.pro.core.common.NewsItem?,
    val nextLesson: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHomeSummary: GetHomeSummaryUseCase,
    progressStore: UserProgressStore
) : ViewModel() {
    val uiState: StateFlow<UiState<HomeData>> = combine(
        getHomeSummary(),
        progressStore.progress
    ) { summary, progress ->
        val result: UiState<HomeData> = UiState.Success(
            HomeData(
                xpToday = progress.xpToday,
                streak = progress.streak,
                wordRussian = summary.wordRussian,
                wordTranslation = summary.wordTranslation,
                example = summary.example,
                latestNews = summary.latestNews,
                nextLesson = summary.nextLesson
            )
        )
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)
}
