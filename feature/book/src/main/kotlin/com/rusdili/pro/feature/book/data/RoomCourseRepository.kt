package com.rusdili.pro.feature.book.data

import com.rusdili.pro.core.database.ChapterProgressDao
import com.rusdili.pro.core.database.ChapterProgressEntity
import com.rusdili.pro.feature.book.domain.ChapterScore
import com.rusdili.pro.feature.book.domain.CourseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomCourseRepository @Inject constructor(
    private val dao: ChapterProgressDao
) : CourseRepository {
    override fun observeScores(): Flow<List<ChapterScore>> =
        dao.observeAll().map { rows -> rows.map { ChapterScore(it.chapterId, it.scorePercent) } }

    override suspend fun saveScore(chapterId: Int, scorePercent: Int) {
        dao.save(ChapterProgressEntity(chapterId, scorePercent, System.currentTimeMillis()))
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CourseRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCourseRepository(implementation: RoomCourseRepository): CourseRepository
}

@Module
@InstallIn(SingletonComponent::class)
object CourseUseCaseModule {
    @Provides
    fun provideSaveChapterScoreUseCase(repository: CourseRepository) =
        com.rusdili.pro.feature.book.domain.SaveChapterScoreUseCase(repository)
}
