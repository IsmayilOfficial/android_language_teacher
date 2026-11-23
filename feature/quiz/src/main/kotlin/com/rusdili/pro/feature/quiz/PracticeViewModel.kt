package com.rusdili.pro.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.ReviewSession
import com.rusdili.pro.core.common.ReviewSessionType
import com.rusdili.pro.core.common.RussianSpeech
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.feature.book.chapters
import com.rusdili.pro.feature.quiz.domain.PracticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class PracticeQuestion(
    val chapterTitle: String,
    val exercise: com.rusdili.pro.feature.book.LessonExercise
)

enum class PracticeMode { DAILY, FLASHCARDS, PROGRESS }

enum class ProgressRange(val days: Int?, val label: String) {
    SEVEN_DAYS(7, "7 gün"),
    FOURTEEN_DAYS(14, "14 gün"),
    ALL_TIME(null, "Hamısı")
}

enum class ProgressMetric(val label: String) {
    SUCCESSFUL_REVIEWS("Uğurlu təkrarlar"),
    ACCURACY("Dəqiqlik %")
}

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
    val completedChapterIds: Set<Int> = emptySet(),
    val reviewSessions: List<ReviewSession> = emptyList(),
    val allWordsCount: Int = 0,
    val reviewedWordsCount: Int = 0,
    val currentStreak: Int = 0,
    val progressRange: ProgressRange = ProgressRange.SEVEN_DAYS,
    val progressMetric: ProgressMetric = ProgressMetric.SUCCESSFUL_REVIEWS
) {
    val currentQuestion: PracticeQuestion? get() = questions.getOrNull(questionIndex)
    val currentCard: Word? get() = words.getOrNull(cardIndex)
}

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val repository: PracticeRepository,
    private val progressStore: UserProgressStore,
    private val speech: RussianSpeech
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<PracticeData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val sessionMutex = Mutex()
    private var activeDailySessionId: Long = 0L
    private var dailyTotalAnswered: Int = 0
    private var dailyCorrectAnswered: Int = 0

    private var activeFlashcardSessionId: Long = 0L
    private var flashcardTotalRated: Int = 0
    private var flashcardKnownRated: Int = 0

    init {
        viewModelScope.launch {
            var cards: List<Word>? = null
            combine(
                repository.observeWords(),
                repository.observeChapterScores(),
                repository.observeReviewSessions(),
                progressStore.progress
            ) { words, scores, sessions, progress ->
                PracticeInputs(words, scores, sessions, progress.streak)
            }.collect { inputs ->
                if (cards == null) {
                    cards = repository.dueCards().ifEmpty {
                        inputs.words.filter { it.nextReviewDate <= System.currentTimeMillis() }.shuffled()
                    }
                }
                val completeIds = inputs.scores
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
                val reviewedWordsCount = inputs.words.count { it.nextReviewDate > 0L }
                _uiState.value = UiState.Success(
                    current.copy(
                        words = selectedCards,
                        cardsInitialized = true,
                        questions = questions,
                        questionsInitialized = true,
                        completedChapterIds = completeIds,
                        reviewSessions = inputs.sessions,
                        allWordsCount = inputs.words.size,
                        reviewedWordsCount = reviewedWordsCount,
                        currentStreak = inputs.streak,
                        cardIndex = current.cardIndex.coerceAtMost((selectedCards.size - 1).coerceAtLeast(0)),
                        questionIndex = current.questionIndex.coerceAtMost((questions.size - 1).coerceAtLeast(0))
                    )
                )
            }
        }
    }

    private data class PracticeInputs(
        val words: List<Word>,
        val scores: List<com.rusdili.pro.feature.book.domain.ChapterScore>,
        val sessions: List<ReviewSession>,
        val streak: Int
    )

    fun setMode(mode: PracticeMode) {
        speech.stop()
        updateData { it.copy(mode = mode) }
    }

    fun setProgressRange(range: ProgressRange) {
        updateData { it.copy(progressRange = range) }
    }

    fun setProgressMetric(metric: ProgressMetric) {
        updateData { it.copy(progressMetric = metric) }
    }

    fun answerPractice(option: String) {
        var didAnswer = false
        var wasCorrect = false
        updateData { data ->
            val exercise = data.currentQuestion?.exercise ?: return@updateData data
            if (data.answered) return@updateData data
            val correct = option.trim().equals(exercise.answer.trim(), ignoreCase = true)
            didAnswer = true
            wasCorrect = correct
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
        if (didAnswer) {
            viewModelScope.launch {
                progressStore.recordQuestionCompleted(correct = wasCorrect)
                sessionMutex.withLock {
                    dailyTotalAnswered += 1
                    if (wasCorrect) dailyCorrectAnswered += 1
                    val savedId = repository.saveReviewSession(
                        ReviewSession(
                            id = activeDailySessionId,
                            sessionType = ReviewSessionType.DAILY_QUIZ,
                            totalItems = dailyTotalAnswered,
                            successfulItems = dailyCorrectAnswered,
                            xpEarned = dailyCorrectAnswered * AppConstants.XP_PER_CORRECT_ANSWER,
                            completedAt = System.currentTimeMillis()
                        )
                    )
                    if (activeDailySessionId == 0L) activeDailySessionId = savedId
                }
            }
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
        viewModelScope.launch {
            sessionMutex.withLock {
                activeDailySessionId = 0L
                dailyTotalAnswered = 0
                dailyCorrectAnswered = 0
            }
        }
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
            }
            sessionMutex.withLock {
                flashcardTotalRated += 1
                if (known) flashcardKnownRated += 1
                val savedId = repository.saveReviewSession(
                    ReviewSession(
                        id = activeFlashcardSessionId,
                        sessionType = ReviewSessionType.FLASHCARD_REVIEW,
                        totalItems = flashcardTotalRated,
                        successfulItems = flashcardKnownRated,
                        xpEarned = flashcardKnownRated * AppConstants.XP_PER_CORRECT_ANSWER,
                        completedAt = now
                    )
                )
                if (activeFlashcardSessionId == 0L) activeFlashcardSessionId = savedId
            }
            if (!known) {
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
