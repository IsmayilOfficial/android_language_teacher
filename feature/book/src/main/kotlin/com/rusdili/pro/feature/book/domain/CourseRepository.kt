package com.rusdili.pro.feature.book.domain

import kotlinx.coroutines.flow.Flow

data class ChapterScore(val chapterId: Int, val scorePercent: Int)

interface CourseRepository {
    fun observeScores(language: String): Flow<List<ChapterScore>>
    suspend fun saveScore(language: String, chapterId: Int, scorePercent: Int)
}

class SaveChapterScoreUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(language: String, chapterId: Int, scorePercent: Int) {
        repository.saveScore(language, chapterId, scorePercent)
    }
}
