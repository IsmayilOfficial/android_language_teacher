package com.rusdili.pro.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.AchievementProgress
import com.rusdili.pro.core.common.DayActivity
import com.rusdili.pro.core.common.LearningLanguage
import com.rusdili.pro.core.common.RussianSpeech
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.feature.home.domain.GetHomeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeData(
    val language: LearningLanguage = LearningLanguage.RUSSIAN,
    val xpToday: Int,
    val streak: Int,
    val bestStreak: Int,
    val totalActiveDays: Int,
    val isTodayActive: Boolean,
    val weeklyActivity: List<DayActivity>,
    val weeklyTotalXp: Int,
    val totalXpEarned: Int,
    val totalQuestionsCompleted: Int,
    val totalLessonsCompleted: Int,
    val achievements: List<AchievementProgress>,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val wordRussian: String,
    val wordTranslation: String,
    val example: String,
    val latestNews: com.rusdili.pro.core.common.NewsItem?,
    val nextLesson: String
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeSummary: GetHomeSummaryUseCase,
    private val progressStore: UserProgressStore,
    private val reminderScheduler: DailyReminderScheduler,
    private val speech: RussianSpeech
) : ViewModel() {
    init {
        refreshStreak()
    }

    val uiState: StateFlow<UiState<HomeData>> = progressStore.selectedLanguage
        .distinctUntilChanged()
        .flatMapLatest { language ->
            combine(
                getHomeSummary(language),
                progressStore.progressFor(language)
            ) { summary, progress ->
                val result: UiState<HomeData> = UiState.Success(
                    HomeData(
                        language = language,
                        xpToday = progress.xpToday,
                        streak = progress.streak,
                        bestStreak = progress.bestStreak,
                        totalActiveDays = progress.totalActiveDays,
                        isTodayActive = progress.isTodayActive,
                        weeklyActivity = progress.weeklyActivity,
                        weeklyTotalXp = progress.weeklyTotalXp,
                        totalXpEarned = progress.totalXpEarned,
                        totalQuestionsCompleted = progress.totalQuestionsCompleted,
                        totalLessonsCompleted = progress.totalLessonsCompleted,
                        achievements = progress.achievements,
                        reminderEnabled = progress.reminderEnabled,
                        reminderHour = progress.reminderHour,
                        reminderMinute = progress.reminderMinute,
                        wordRussian = summary.wordRussian,
                        wordTranslation = summary.wordTranslation,
                        example = summary.example,
                        latestNews = summary.latestNews,
                        nextLesson = summary.nextLesson
                    )
                )
                result
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun recordDailyActivity() {
        val currentLanguage = (uiState.value as? UiState.Success)?.data?.language
        viewModelScope.launch {
            progressStore.recordDailyActivity(currentLanguage)
        }
    }

    fun refreshStreak() {
        val currentLanguage = (uiState.value as? UiState.Success)?.data?.language
        viewModelScope.launch {
            progressStore.syncStreak(currentLanguage)
        }
    }

    fun speakMascotGreeting() {
        val language = (uiState.value as? UiState.Success)?.data?.language ?: LearningLanguage.RUSSIAN
        speech.speak(language.greeting, language = language)
    }

    fun speakWordOfTheDay(text: String) {
        val language = (uiState.value as? UiState.Success)?.data?.language ?: LearningLanguage.RUSSIAN
        speech.speak(text, language = language)
    }

    fun setDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            progressStore.setReminderSettings(enabled, hour, minute)
            if (enabled) {
                reminderScheduler.scheduleDaily(hour, minute, replaceExisting = true)
            } else {
                reminderScheduler.cancelDaily()
            }
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        setDailyReminder(enabled = true, hour = hour, minute = minute)
    }

    fun sendTestReminderNotification() {
        reminderScheduler.sendTestNotificationNow()
    }

    override fun onCleared() {
        speech.stop()
        super.onCleared()
    }
}
