package com.rusdili.pro.feature.book

import com.rusdili.pro.core.common.LearningLanguage

object CourseCatalog {
    fun chaptersFor(language: LearningLanguage): List<Chapter> = when (language) {
        LearningLanguage.RUSSIAN -> chapters
        LearningLanguage.SPANISH -> spanishChapters
        LearningLanguage.FRENCH -> frenchChapters
    }

    fun lessonTitlesFor(language: LearningLanguage): List<String> =
        chaptersFor(language).map { it.title }
}
