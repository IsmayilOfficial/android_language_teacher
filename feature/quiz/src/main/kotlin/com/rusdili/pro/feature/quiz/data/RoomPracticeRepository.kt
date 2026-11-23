package com.rusdili.pro.feature.quiz.data

import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.database.ChapterProgressDao
import com.rusdili.pro.core.database.WordDao
import com.rusdili.pro.core.database.WordEntity
import com.rusdili.pro.feature.book.domain.ChapterScore
import com.rusdili.pro.feature.quiz.domain.PracticeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPracticeRepository @Inject constructor(
    private val wordDao: WordDao,
    private val chapterProgressDao: ChapterProgressDao
) : PracticeRepository {
    override fun observeWords(): Flow<List<Word>> =
        wordDao.observeAll().map { rows -> rows.map(WordEntity::toDomain) }

    override fun observeChapterScores(): Flow<List<ChapterScore>> =
        chapterProgressDao.observeAll().map { rows ->
            rows.map { ChapterScore(it.chapterId, it.scorePercent) }
        }

    override suspend fun dueCards(): List<Word> =
        wordDao.dueForReview(System.currentTimeMillis(), 100).map(WordEntity::toDomain)

    override suspend fun scheduleReview(word: String, stage: Int, date: Long) {
        wordDao.scheduleReview(word, stage, date)
    }
}

private fun WordEntity.toDomain() = Word(
    russian, translation, gender, example, aspectPair, isFavorite, nextReviewDate, reviewStage
)

@Module
@InstallIn(SingletonComponent::class)
abstract class PracticeRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPracticeRepository(implementation: RoomPracticeRepository): PracticeRepository
}
