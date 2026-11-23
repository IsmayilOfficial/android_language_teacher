package com.rusdili.pro.core.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferences by preferencesDataStore(name = "user_preferences")

data class DailyProgress(val xpToday: Int, val streak: Int)

@Singleton
class UserProgressStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val progress: Flow<DailyProgress> = context.userPreferences.data.map { preferences ->
        val today = dateString()
        DailyProgress(
            xpToday = if (preferences[XP_DATE] == today) preferences[XP_TODAY] ?: 0 else 0,
            streak = preferences[STREAK] ?: 0
        )
    }

    val searchHistory: Flow<List<String>> = context.userPreferences.data.map { preferences ->
        preferences[SEARCH_HISTORY].orEmpty().lineSequence().filter(String::isNotBlank).toList()
    }

    suspend fun addXp(points: Int) {
        require(points >= 0)
        context.userPreferences.edit { preferences ->
            val today = dateString()
            val previousXp = if (preferences[XP_DATE] == today) preferences[XP_TODAY] ?: 0 else 0
            preferences[XP_DATE] = today
            preferences[XP_TODAY] = previousXp + points
        }
    }

    suspend fun markLessonCompleted() {
        context.userPreferences.edit { preferences ->
            val today = dateString()
            if (preferences[LAST_LESSON_DATE] != today) {
                val streak = preferences[STREAK] ?: 0
                preferences[STREAK] = if (preferences[LAST_LESSON_DATE] == yesterdayString()) streak + 1 else 1
                preferences[LAST_LESSON_DATE] = today
            }
        }
    }

    suspend fun recordSearch(word: String) {
        if (word.isBlank()) return
        context.userPreferences.edit { preferences ->
            val history = preferences[SEARCH_HISTORY].orEmpty().lineSequence()
                .filter(String::isNotBlank)
                .filterNot { it.equals(word, ignoreCase = true) }
                .toList()
            preferences[SEARCH_HISTORY] = (listOf(word) + history)
                .take(AppConstants.MAX_SEARCH_HISTORY)
                .joinToString("\n")
        }
    }

    private fun dateString(date: Date = Date()): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)

    private fun yesterdayString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return dateString(calendar.time)
    }

    private companion object {
        val XP_TODAY = intPreferencesKey("xp_today")
        val XP_DATE = stringPreferencesKey("xp_date")
        val STREAK = intPreferencesKey("streak")
        val LAST_LESSON_DATE = stringPreferencesKey("last_lesson_date")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
    }
}
