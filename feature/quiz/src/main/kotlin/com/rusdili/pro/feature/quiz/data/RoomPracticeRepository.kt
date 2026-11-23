package com.rusdili.pro.feature.quiz.data

import com.rusdili.pro.core.common.LearningLanguage
import com.rusdili.pro.core.common.ReviewSession
import com.rusdili.pro.core.common.ReviewSessionType
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.database.ChapterProgressDao
import com.rusdili.pro.core.database.ReviewSessionDao
import com.rusdili.pro.core.database.ReviewSessionEntity
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
    private val chapterProgressDao: ChapterProgressDao,
    private val reviewSessionDao: ReviewSessionDao
) : PracticeRepository {
    override fun observeWords(language: LearningLanguage): Flow<List<Word>> =
        wordDao.observeAll(language.code).map { rows -> rows.map(WordEntity::toDomain) }

    override fun observeChapterScores(language: LearningLanguage): Flow<List<ChapterScore>> =
        chapterProgressDao.observeByLanguage(language.code).map { rows ->
            rows.map { ChapterScore(it.chapterId, it.scorePercent) }
        }

    override fun observeReviewSessions(language: LearningLanguage): Flow<List<ReviewSession>> =
        reviewSessionDao.observeByLanguage(language.code).map { rows -> rows.map(ReviewSessionEntity::toDomain) }

    override suspend fun dueCards(language: LearningLanguage): List<Word> =
        wordDao.dueForReview(language.code, System.currentTimeMillis(), 100).map(WordEntity::toDomain)

    override suspend fun scheduleReview(
        language: LearningLanguage,
        word: String,
        stage: Int,
        date: Long
    ) {
        wordDao.scheduleReview(language.code, word, stage, date)
    }

    override suspend fun saveReviewSession(session: ReviewSession): Long =
        reviewSessionDao.upsert(session.toEntity())
}

private fun WordEntity.toDomain() = Word(
    russian = russian,
    translation = translation,
    gender = gender,
    example = example,
    aspectPair = aspectPair,
    isFavorite = isFavorite,
    nextReviewDate = nextReviewDate,
    reviewStage = reviewStage,
    language = language
)

private fun ReviewSessionEntity.toDomain() = ReviewSession(
    id = id,
    sessionType = if (sessionType == ReviewSessionType.FLASHCARD_REVIEW.name) {
        ReviewSessionType.FLASHCARD_REVIEW
    } else {
        ReviewSessionType.DAILY_QUIZ
    },
    totalItems = totalItems,
    successfulItems = successfulItems,
    xpEarned = xpEarned,
    completedAt = completedAt,
    language = language
)

private fun ReviewSession.toEntity() = ReviewSessionEntity(
    id = id,
    language = language,
    sessionType = sessionType.name,
    totalItems = totalItems,
    successfulItems = successfulItems,
    xpEarned = xpEarned,
    completedAt = completedAt
)

@Module
@InstallIn(SingletonComponent::class)
abstract class PracticeRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPracticeRepository(implementation: RoomPracticeRepository): PracticeRepository
}
