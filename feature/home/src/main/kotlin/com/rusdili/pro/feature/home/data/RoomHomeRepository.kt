package com.rusdili.pro.feature.home.data

import com.rusdili.pro.core.common.NewsItem
import com.rusdili.pro.core.database.ChapterProgressDao
import com.rusdili.pro.core.database.NewsDao
import com.rusdili.pro.core.database.WordDao
import com.rusdili.pro.feature.home.domain.GetHomeSummaryUseCase
import com.rusdili.pro.feature.home.domain.HomeRepository
import com.rusdili.pro.feature.home.domain.HomeSummary
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RoomHomeRepository @Inject constructor(
    private val wordDao: WordDao,
    private val newsDao: NewsDao,
    private val chapterProgressDao: ChapterProgressDao
) : HomeRepository {
    override fun observeSummary(): Flow<HomeSummary> = combine(
        wordDao.observeAll(), newsDao.observeAll(), chapterProgressDao.observeAll()
    ) { words, news, progress ->
        val word = words.firstOrNull { it.russian == "книга" }
        val latestNews = news.firstOrNull()?.let {
            NewsItem(
                it.id, it.titleAz, it.summaryAz, it.original, it.source,
                it.publishedAt, it.imageUrl, it.sourceUrl, it.unconfirmed
            )
        }
        val completed = progress.associate { it.chapterId to it.scorePercent }
        val lessonTitles = listOf(
            "Əlifba", "Vurğu və reduksiya", "İsim və Cins", "6 Hal", "Sifət",
            "Əvəzliklər", "Saylar", "Feilin Növü", "Feil Zamanları", "Hərəkət Feilləri",
            "Qayıdış Feilləri", "Əmr və Şərt", "Feili Sifət və Feili Zərf", "Sintaksis", "Orfoqrafiya"
        )
        val nextId = (1..15).firstOrNull { (completed[it] ?: 0) < 80 } ?: 15
        HomeSummary(
            wordRussian = word?.russian ?: "книга",
            wordTranslation = word?.translation ?: "kitab",
            example = word?.example ?: "Я читаю книгу.",
            latestNews = latestNews,
            nextLesson = "$nextId-ci fəsil: ${lessonTitles[nextId - 1]}"
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHomeRepository(implementation: RoomHomeRepository): HomeRepository
}

@Module
@InstallIn(SingletonComponent::class)
object HomeUseCaseModule {
    @Provides
    fun provideGetHomeSummaryUseCase(repository: HomeRepository) = GetHomeSummaryUseCase(repository)
}
