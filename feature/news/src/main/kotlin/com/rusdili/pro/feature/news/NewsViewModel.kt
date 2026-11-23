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

data class NewsData(
    val items: List<NewsItem>,
    val refreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    private val refreshing = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    init {
        refreshWorld()
    }

    val uiState: StateFlow<UiState<NewsData>> = combine(
        repository.observeNews(), refreshing, error
    ) { news, loading, message ->
        val visible = news.filterNot {
            it.id.startsWith("seed-trump-") || it.source.contains("Truth Social", ignoreCase = true)
        }
        val state: UiState<NewsData> = UiState.Success(NewsData(visible, loading, message))
        state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun refreshWorld() {
        if (refreshing.value) return
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
