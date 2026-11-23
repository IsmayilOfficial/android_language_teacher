package com.rusdili.pro.feature.book.domain

import kotlinx.coroutines.flow.Flow

data class ChapterScore(val chapterId: Int, val scorePercent: Int)

interface CourseRepository {
    fun observeScores(): Flow<List<ChapterScore>>
    suspend fun saveScore(chapterId: Int, scorePercent: Int)
}

class SaveChapterScoreUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(chapterId: Int, scorePercent: Int) {
        repository.saveScore(chapterId, scorePercent)
    }
}
