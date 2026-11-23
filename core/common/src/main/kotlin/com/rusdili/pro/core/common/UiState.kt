package com.rusdili.pro.core.common

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class Word(
    val russian: String,
    val translation: String,
    val gender: String? = null,
    val example: String = "",
    val aspectPair: String? = null,
    val isFavorite: Boolean = false,
    val nextReviewDate: Long = 0L,
    val reviewStage: Int = 0
)

data class NewsItem(
    val id: String,
    val titleAz: String,
    val summaryAz: String,
    val original: String,
    val source: String,
    val publishedAt: String,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val unconfirmed: Boolean = false
)
