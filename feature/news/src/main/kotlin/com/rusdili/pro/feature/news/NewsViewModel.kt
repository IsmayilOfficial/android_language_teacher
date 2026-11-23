package com.rusdili.pro.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.NewsItem
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.feature.news.domain.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class NewsTab { TRUMP, WORLD }

data class NewsData(
    val tab: NewsTab,
    val items: List<NewsItem>,
    val refreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    private val tab = MutableStateFlow(NewsTab.TRUMP)
    private val refreshing = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private var worldRequestAttempted = false

    val uiState: StateFlow<UiState<NewsData>> = combine(
        repository.observeNews(), tab, refreshing, error
    ) { news, selectedTab, loading, message ->
        val visible = when (selectedTab) {
            NewsTab.TRUMP -> news.filter { it.source.contains("Truth Social", ignoreCase = true) }
            NewsTab.WORLD -> news.filterNot { it.source.contains("Truth Social", ignoreCase = true) }
        }
        val state: UiState<NewsData> = UiState.Success(NewsData(selectedTab, visible, loading, message))
        state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun selectTab(selected: NewsTab) {
        tab.value = selected
        error.value = null
        if (selected == NewsTab.WORLD && !worldRequestAttempted) refreshWorld()
    }

    fun refreshWorld() {
        if (refreshing.value) return
        worldRequestAttempted = true
        viewModelScope.launch {
            refreshing.value = true
            error.value = null
            try {
                repository.refreshWorldNews()
            } catch (exception: IOException) {
                error.value = "Xəbərləri yükləmək mümkün olmadı: internet bağlantısını yoxla."
            } catch (exception: HttpException) {
                error.value = "Xəbər xidməti xətası: HTTP ${exception.code()}."
            } finally {
                refreshing.value = false
            }
        }
    }
}
