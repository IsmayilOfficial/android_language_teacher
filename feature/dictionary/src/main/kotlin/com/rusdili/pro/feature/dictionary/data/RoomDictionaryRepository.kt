package com.rusdili.pro.feature.dictionary.data

import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.SearchHistoryEntry
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.database.SearchHistoryDao
import com.rusdili.pro.core.database.SearchHistoryEntity
import com.rusdili.pro.core.database.WordDao
import com.rusdili.pro.core.database.WordEntity
import com.rusdili.pro.feature.dictionary.domain.DictionaryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDictionaryRepository @Inject constructor(
    private val dao: WordDao,
    private val searchHistoryDao: SearchHistoryDao
) : DictionaryRepository {
    override fun search(query: String): Flow<List<Word>> =
        dao.search(query.trim()).map { rows -> rows.map(WordEntity::toDomain) }

    override fun favorites(): Flow<List<Word>> =
        dao.observeFavorites().map { rows -> rows.map(WordEntity::toDomain) }

    override suspend fun setFavorite(word: String, favorite: Boolean) {
        dao.setFavorite(word, favorite)
    }

    override fun recentSearches(limit: Int): Flow<List<SearchHistoryEntry>> =
        searchHistoryDao.observeRecent(limit).map { rows -> rows.map(SearchHistoryEntity::toDomain) }

    override suspend fun recordSearch(query: String, matchedWord: Word?) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val normalizedKey = (matchedWord?.russian ?: trimmed).lowercase(Locale.ROOT)
        val display = matchedWord?.russian ?: trimmed
        val exactWord = matchedWord ?: dao.findByRussian(trimmed)?.toDomain()
        searchHistoryDao.upsert(
            SearchHistoryEntity(
                queryKey = normalizedKey,
                displayQuery = display,
                matchedRussian = exactWord?.russian,
                matchedTranslation = exactWord?.translation,
                searchedAt = System.currentTimeMillis()
            )
        )
        searchHistoryDao.trimToLimit(AppConstants.MAX_SEARCH_HISTORY)
    }

    override suspend fun removeSearchHistoryEntry(queryKey: String) {
        searchHistoryDao.deleteByKey(queryKey)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.clearAll()
    }
}

private fun WordEntity.toDomain() = Word(
    russian = russian,
    translation = translation,
    gender = gender,
    example = example,
    aspectPair = aspectPair,
    isFavorite = isFavorite,
    nextReviewDate = nextReviewDate,
    reviewStage = reviewStage
)

private fun SearchHistoryEntity.toDomain() = SearchHistoryEntry(
    queryKey = queryKey,
    query = displayQuery,
    matchedRussian = matchedRussian,
    matchedTranslation = matchedTranslation,
    searchedAt = searchedAt
)

@Module
@InstallIn(SingletonComponent::class)
abstract class DictionaryRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDictionaryRepository(implementation: RoomDictionaryRepository): DictionaryRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DictionaryUseCaseModule {
    @Provides
    fun provideSearchWordUseCase(repository: DictionaryRepository) =
        com.rusdili.pro.feature.dictionary.domain.SearchWordUseCase(repository)
}
