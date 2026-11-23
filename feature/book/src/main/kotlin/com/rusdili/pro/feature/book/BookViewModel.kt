package com.rusdili.pro.feature.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.LearningLanguage
import com.rusdili.pro.core.common.RussianSpeech
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.feature.book.domain.BookWordRepository
import com.rusdili.pro.feature.book.domain.CourseRepository
import com.rusdili.pro.feature.book.domain.SaveChapterScoreUseCase
import com.rusdili.pro.feature.book.domain.WordLookup
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChapterRow(val chapter: Chapter, val score: Int, val locked: Boolean)

data class BookData(
    val language: LearningLanguage = LearningLanguage.RUSSIAN,
    val chapters: List<ChapterRow> = emptyList(),
    val selectedChapterId: Int? = null,
    val selectedLessonIndex: Int? = null,
    val exerciseIndex: Int = 0,
    val correctAnswers: Int = 0,
    val earnedXp: Int = 0,
    val answered: Boolean = false,
    val feedback: String = "",
    val finished: Boolean = false,
    val lookupToken: String? = null,
    val wordLookup: WordLookup? = null,
    val lookupLoading: Boolean = false
) {
    val selectedChapter: Chapter? get() = chapters.firstOrNull { it.chapter.id == selectedChapterId }?.chapter
    val currentExercise: LessonExercise?
        get() = selectedChapter?.exercises?.getOrNull(exerciseIndex)
    val lessonScore: Int get() = correctAnswers * 20
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookViewModel @Inject constructor(
    repository: CourseRepository,
    private val saveScore: SaveChapterScoreUseCase,
    private val wordRepository: BookWordRepository,
    private val speech: RussianSpeech,
    private val progressStore: UserProgressStore
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<BookData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            progressStore.selectedLanguage
                .flatMapLatest { language ->
                    repository.observeScores(language.code).map { scores -> language to scores }
                }
                .collect { (language, scores) ->
                    val scoreById = scores.associate { it.chapterId to it.scorePercent }
                    val rows = CourseCatalog.chaptersFor(language).map { chapter ->
                        val score = scoreById[chapter.id] ?: 0
                        ChapterRow(
                            chapter = chapter,
                            score = score,
                            locked = chapter.id > 1 &&
                                (scoreById[chapter.id - 1] ?: 0) < AppConstants.CHAPTER_PASS_PERCENT
                        )
                    }
                    val current = (_uiState.value as? UiState.Success)?.data
                    val languageChanged = current != null && current.language != language
                    _uiState.value = if (current == null || languageChanged) {
                        UiState.Success(BookData(language = language, chapters = rows))
                    } else {
                        UiState.Success(current.copy(language = language, chapters = rows))
                    }
                }
        }
    }

    fun openChapter(chapterId: Int) {
        updateData { data ->
            if (data.chapters.firstOrNull { it.chapter.id == chapterId }?.locked == true) data
            else data.copy(
                selectedChapterId = chapterId,
                selectedLessonIndex = null,
                exerciseIndex = 0,
                correctAnswers = 0,
                earnedXp = 0,
                answered = false,
                feedback = "",
                finished = false
            )
        }
    }

    fun startLesson(chapterId: Int, lessonIndex: Int) {
        openChapter(chapterId)
        updateData { it.copy(selectedLessonIndex = lessonIndex, finished = false) }
    }

    fun answer(option: String) {
        updateData { data ->
            val exercise = data.currentExercise ?: return@updateData data
            if (data.answered) return@updateData data
            val correct = option.trim().equals(exercise.answer.trim(), ignoreCase = true)
            viewModelScope.launch {
                progressStore.recordQuestionCompleted(correct = correct)
            }
            data.copy(
                correctAnswers = data.correctAnswers + if (correct) 1 else 0,
                earnedXp = data.earnedXp + if (correct) AppConstants.XP_PER_CORRECT_ANSWER else 0,
                answered = true,
                feedback = if (correct) {
                    "Doğrudur! +${AppConstants.XP_PER_CORRECT_ANSWER} XP"
                } else {
                    "Bu cavab düzgün deyil. ${exercise.explanation} Düzgün cavab: ${exercise.answer}."
                }
            )
        }
    }

    fun continueLesson() {
        val data = (_uiState.value as? UiState.Success)?.data ?: return
        if (!data.answered) return
        if (data.exerciseIndex == data.selectedChapter?.exercises?.lastIndex) {
            updateData { it.copy(finished = true, answered = false) }
            viewModelScope.launch { progressStore.markLessonCompleted() }
            if (data.selectedLessonIndex == 3 && data.selectedChapterId != null) {
                viewModelScope.launch {
                    saveScore(data.language.code, data.selectedChapterId, data.lessonScore)
                }
            }
        } else {
            updateData {
                it.copy(exerciseIndex = it.exerciseIndex + 1, answered = false, feedback = "")
            }
        }
    }

    fun nextLesson() {
        updateData { data ->
            val current = data.selectedLessonIndex ?: 0
            data.copy(
                selectedLessonIndex = null,
                exerciseIndex = 0,
                correctAnswers = 0,
                earnedXp = 0,
                answered = false,
                feedback = "",
                finished = false,
                selectedChapterId = if (current >= 3) null else data.selectedChapterId
            )
        }
    }

    fun speak(text: String) {
        val language = (_uiState.value as? UiState.Success)?.data?.language
        speech.speak(text, language = language)
    }

    fun lookupWord(token: String) {
        val language = (_uiState.value as? UiState.Success)?.data?.language ?: LearningLanguage.RUSSIAN
        updateData { it.copy(lookupToken = token, wordLookup = null, lookupLoading = true) }
        viewModelScope.launch {
            val result = wordRepository.lookup(token, language)
            updateData { data ->
                if (data.lookupToken == token) data.copy(wordLookup = result, lookupLoading = false)
                else data
            }
        }
    }

    fun clearLookup() {
        updateData { it.copy(lookupToken = null, wordLookup = null, lookupLoading = false) }
    }

    fun closeChapter() {
        updateData { it.copy(selectedChapterId = null, selectedLessonIndex = null, finished = false) }
    }

    fun backToChapter() {
        updateData {
            it.copy(
                selectedLessonIndex = null,
                exerciseIndex = 0,
                answered = false,
                feedback = "",
                finished = false
            )
        }
    }

    private fun updateData(transform: (BookData) -> BookData) {
        _uiState.update { state ->
            if (state is UiState.Success) UiState.Success(transform(state.data)) else state
        }
    }
}
