package com.rusdili.pro.core.common

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val message: String) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()
}
