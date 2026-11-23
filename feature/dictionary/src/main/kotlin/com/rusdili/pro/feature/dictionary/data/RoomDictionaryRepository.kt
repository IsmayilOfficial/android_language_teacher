package com.rusdili.pro.feature.dictionary.data

import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.database.WordDao
import com.rusdili.pro.core.database.WordEntity
import com.rusdili.pro.feature.dictionary.domain.DictionaryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDictionaryRepository @Inject constructor(
    private val dao: WordDao
) : DictionaryRepository {
    override fun search(query: String): Flow<List<Word>> =
        dao.search(query.trim()).map { rows -> rows.map(WordEntity::toDomain) }

    override fun favorites(): Flow<List<Word>> =
        dao.observeFavorites().map { rows -> rows.map(WordEntity::toDomain) }

    override suspend fun setFavorite(word: String, favorite: Boolean) {
        dao.setFavorite(word, favorite)
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
