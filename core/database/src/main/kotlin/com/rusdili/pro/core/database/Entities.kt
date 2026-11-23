package com.rusdili.pro.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val russian: String,
    val translation: String,
    val gender: String?,
    val example: String,
    val aspectPair: String?,
    val isFavorite: Boolean = false,
    val nextReviewDate: Long = 0L,
    @ColumnInfo(defaultValue = "0") val reviewStage: Int = 0
)

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterId: Int,
    val scorePercent: Int,
    val completedAt: Long
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: String,
    val titleAz: String,
    val summaryAz: String,
    val original: String,
    val source: String,
    val publishedAt: String,
    val imageUrl: String?,
    val sourceUrl: String?,
    val unconfirmed: Boolean
)
