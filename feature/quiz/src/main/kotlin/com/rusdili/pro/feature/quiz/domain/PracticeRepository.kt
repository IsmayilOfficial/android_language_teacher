package com.rusdili.pro.feature.quiz.domain

import com.rusdili.pro.core.common.Word
import com.rusdili.pro.feature.book.domain.ChapterScore
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    fun observeWords(): Flow<List<Word>>
    fun observeChapterScores(): Flow<List<ChapterScore>>
    suspend fun dueCards(): List<Word>
    suspend fun scheduleReview(word: String, stage: Int, date: Long)
}
