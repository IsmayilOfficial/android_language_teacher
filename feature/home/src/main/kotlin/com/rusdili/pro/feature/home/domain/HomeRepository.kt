package com.rusdili.pro.feature.home.domain

import com.rusdili.pro.core.common.NewsItem
import kotlinx.coroutines.flow.Flow

data class HomeSummary(
    val wordRussian: String,
    val wordTranslation: String,
    val example: String,
    val latestNews: NewsItem?,
    val nextLesson: String
)

interface HomeRepository {
    fun observeSummary(): Flow<HomeSummary>
}

class GetHomeSummaryUseCase(private val repository: HomeRepository) {
    operator fun invoke(): Flow<HomeSummary> = repository.observeSummary()
}
