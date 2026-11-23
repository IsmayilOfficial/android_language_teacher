package com.rusdili.pro.feature.book.domain

import com.rusdili.pro.core.common.Word

data class WordLookup(
    val token: String,
    val word: Word?,
    val grammaticalForm: String
)

interface BookWordRepository {
    suspend fun lookup(token: String): WordLookup
}
