package com.rusdili.pro.feature.news.domain

import com.rusdili.pro.core.common.NewsItem
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun observeNews(): Flow<List<NewsItem>>
    suspend fun refreshWorldNews()
}
