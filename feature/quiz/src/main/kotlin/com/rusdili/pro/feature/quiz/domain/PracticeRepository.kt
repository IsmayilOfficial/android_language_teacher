package com.rusdili.pro.feature.quiz.domain

import com.rusdili.pro.core.common.LearningLanguage
import com.rusdili.pro.core.common.ReviewSession
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.feature.book.domain.ChapterScore
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    fun observeWords(language: LearningLanguage): Flow<List<Word>>
    fun observeChapterScores(language: LearningLanguage): Flow<List<ChapterScore>>
    fun observeReviewSessions(language: LearningLanguage): Flow<List<ReviewSession>>
    suspend fun dueCards(language: LearningLanguage): List<Word>
    suspend fun scheduleReview(language: LearningLanguage, word: String, stage: Int, date: Long)
    suspend fun saveReviewSession(session: ReviewSession): Long
}
