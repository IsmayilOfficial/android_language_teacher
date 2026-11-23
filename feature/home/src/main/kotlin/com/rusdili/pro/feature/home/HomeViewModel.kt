package com.rusdili.pro.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusdili.pro.core.common.AchievementProgress
import com.rusdili.pro.core.common.DayActivity
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.feature.home.domain.GetHomeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeData(
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHomeSummary: GetHomeSummaryUseCase,
    private val progressStore: UserProgressStore,
    private val reminderScheduler: DailyReminderScheduler
) : ViewModel() {
    init {
        refreshStreak()
    }

    val uiState: StateFlow<UiState<HomeData>> = combine(
        getHomeSummary(),
        progressStore.progress
    ) { summary, progress ->
        val result: UiState<HomeData> = UiState.Success(
            HomeData(
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun recordDailyActivity() {
        viewModelScope.launch {
            progressStore.recordDailyActivity()
        }
    }

    fun refreshStreak() {
        viewModelScope.launch {
            progressStore.syncStreak()
        }
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
}
