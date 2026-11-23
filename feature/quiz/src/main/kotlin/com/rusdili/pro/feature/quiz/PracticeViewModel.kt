package com.rusdili.pro.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.RussianSpeech
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.feature.book.chapters
import com.rusdili.pro.feature.quiz.domain.PracticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PracticeQuestion(
    val chapterTitle: String,
    val exercise: com.rusdili.pro.feature.book.LessonExercise
)

data class PracticeData(
    val words: List<Word> = emptyList(),
    val questions: List<PracticeQuestion> = emptyList(),
    val mode: PracticeMode = PracticeMode.DAILY,
    val questionIndex: Int = 0,
    val correctAnswers: Int = 0,
    val answered: Boolean = false,
    val feedback: String = "",
    val cardIndex: Int = 0,
    val showingBack: Boolean = false,
    val cardsInitialized: Boolean = false,
    val questionsInitialized: Boolean = false,
    val completedChapterIds: Set<Int> = emptySet()
) {
    val currentQuestion: PracticeQuestion? get() = questions.getOrNull(questionIndex)
    val currentCard: Word? get() = words.getOrNull(cardIndex)
}

enum class PracticeMode { DAILY, FLASHCARDS }

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val repository: PracticeRepository,
    private val progressStore: UserProgressStore,
    private val speech: RussianSpeech
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<PracticeData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            var cards: List<Word>? = null
            combine(repository.observeWords(), repository.observeChapterScores()) { words, scores ->
                words to scores
            }.collect { (words, scores) ->
                if (cards == null) {
                    cards = repository.dueCards().ifEmpty {
                        words.filter { it.nextReviewDate <= System.currentTimeMillis() }.shuffled()
                    }
                }
                val completeIds = scores
                    .filter { it.scorePercent >= AppConstants.CHAPTER_PASS_PERCENT }
                    .map { it.chapterId }
                    .toSet()
                val current = (_uiState.value as? UiState.Success)?.data ?: PracticeData()
                val selectedCards = if (current.cardsInitialized) current.words else cards.orEmpty()
                val questions = if (!current.questionsInitialized || completeIds != current.completedChapterIds) {
                    chapters
                        .filter { it.id in completeIds }
                        .flatMap { chapter -> chapter.exercises.map { PracticeQuestion(chapter.title, it) } }
                        .shuffled()
                        .take(AppConstants.DAILY_PRACTICE_QUESTION_COUNT)
                } else {
                    current.questions
                }
                _uiState.value = UiState.Success(
                    current.copy(
                        words = selectedCards,
                        cardsInitialized = true,
                        questions = questions,
                        questionsInitialized = true,
                        completedChapterIds = completeIds,
                        cardIndex = current.cardIndex.coerceAtMost((selectedCards.size - 1).coerceAtLeast(0)),
                        questionIndex = current.questionIndex.coerceAtMost((questions.size - 1).coerceAtLeast(0))
                    )
                )
            }
        }
    }

    fun setMode(mode: PracticeMode) {
        speech.stop()
        updateData { it.copy(mode = mode) }
    }

    fun answerPractice(option: String) {
        updateData { data ->
            val exercise = data.currentQuestion?.exercise ?: return@updateData data
            if (data.answered) return@updateData data
            val correct = option.trim().equals(exercise.answer.trim(), ignoreCase = true)
            viewModelScope.launch {
                progressStore.recordQuestionCompleted(correct = correct)
            }
            data.copy(
                correctAnswers = data.correctAnswers + if (correct) 1 else 0,
                answered = true,
                feedback = if (correct) {
                    "Doğrudur! +${AppConstants.XP_PER_CORRECT_ANSWER} XP"
                } else {
                    "Bu cavab düzgün deyil. ${exercise.explanation} Düzgün cavab: ${exercise.answer}."
                }
            )
        }
    }

    fun nextQuestion() {
        speech.stop()
        updateData { data ->
            if (data.questionIndex >= data.questions.lastIndex) {
                data.copy(questionIndex = data.questions.size, answered = false, feedback = "")
            } else {
                data.copy(questionIndex = data.questionIndex + 1, answered = false, feedback = "")
            }
        }
    }

    fun restartPractice() {
        speech.stop()
        updateData { data ->
            data.copy(
                questions = data.questions.shuffled().take(AppConstants.DAILY_PRACTICE_QUESTION_COUNT),
                questionIndex = 0,
                correctAnswers = 0,
                answered = false,
                feedback = ""
            )
        }
    }

    fun flipCard() {
        updateData { it.copy(showingBack = !it.showingBack) }
    }

    fun rateCard(known: Boolean) {
        speech.stop()
        val data = (_uiState.value as? UiState.Success)?.data ?: return
        val word = data.currentCard ?: return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val stage = if (known) 0 else word.reviewStage.coerceIn(0, 3)
            val interval = if (known) DAY else REVIEW_INTERVALS[stage]
            repository.scheduleReview(word.russian, if (known) 0 else (stage + 1).coerceAtMost(3), now + interval)
            if (known) {
                progressStore.addXp(AppConstants.XP_PER_CORRECT_ANSWER)
            } else {
                progressStore.recordDailyActivity()
                delay(interval)
                val dueWords = repository.dueCards()
                updateData { current ->
                    current.copy(words = (current.words + dueWords).distinctBy(Word::russian))
                }
            }
        }
        updateData {
            it.copy(
                words = it.words.filterNot { card -> card.russian == word.russian },
                cardIndex = 0,
                showingBack = false
            )
        }
    }

    fun speak(text: String, slow: Boolean = false) {
        speech.speak(text, slow)
    }

    override fun onCleared() {
        speech.stop()
        super.onCleared()
    }

    private fun updateData(transform: (PracticeData) -> PracticeData) {
        _uiState.update { state ->
            if (state is UiState.Success) UiState.Success(transform(state.data)) else state
        }
    }

    private companion object {
        const val HOUR = 60 * 60 * 1000L
        const val DAY = 24 * HOUR
        val REVIEW_INTERVALS = longArrayOf(HOUR, DAY, 3 * DAY, 7 * DAY)
    }
}
