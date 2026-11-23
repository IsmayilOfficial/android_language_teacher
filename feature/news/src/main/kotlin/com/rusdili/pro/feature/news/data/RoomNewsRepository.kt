package com.rusdili.pro.feature.news.data

import com.rusdili.pro.core.common.NewsItem
import com.rusdili.pro.core.database.NewsDao
import com.rusdili.pro.core.database.NewsEntity
import com.rusdili.pro.core.network.NewsApiService
import com.rusdili.pro.feature.news.domain.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNewsRepository @Inject constructor(
    private val dao: NewsDao,
    private val api: NewsApiService
) : NewsRepository {
    override fun observeNews(): Flow<List<NewsItem>> =
        dao.observeAll().map { rows ->
            rows.map {
                NewsItem(
                    id = it.id,
                    titleAz = it.titleAz,
                    summaryAz = it.summaryAz,
                    original = it.original,
                    source = it.source,
                    publishedAt = it.publishedAt,
                    imageUrl = it.imageUrl,
                    sourceUrl = it.sourceUrl,
                    unconfirmed = it.unconfirmed
                )
            }
        }

    override suspend fun refreshWorldNews() {
        val response = api.searchArticles(query = "world news")
        dao.upsertAll(
            response.articles.mapNotNull { article ->
                val title = article.title?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                val url = article.url?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                NewsEntity(
                    id = "gdelt-${url.hashCode()}",
                    titleAz = "Dünya xəbəri",
                    summaryAz = "Bu başlıq GDELT xəbər indeksində dərc edilmiş məqalədən götürülüb. " +
                        "Məqalənin tam məzmunu və konteksti üçün orijinal mənbəyə baxın.",
                    original = title,
                    source = article.domain ?: "GDELT",
                    publishedAt = article.seendate ?: "Tarix göstərilməyib",
                    imageUrl = article.socialimage,
                    sourceUrl = url,
                    unconfirmed = true
                )
            }
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(implementation: RoomNewsRepository): NewsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NewsApiModule {
    @Provides
    @Singleton
    fun provideNewsApi(): NewsApiService = com.rusdili.pro.core.network.NewsNetwork.api
}
