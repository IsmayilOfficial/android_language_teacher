package com.rusdili.pro.core.common

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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

data class DayActivity(
    val date: String,
    val dayShortLabel: String,
    val isCompleted: Boolean,
    val isToday: Boolean,
    val xpEarned: Int = 0
)

data class DailyProgress(
    val xpToday: Int,
    val streak: Int,
    val bestStreak: Int = 0,
    val totalActiveDays: Int = 0,
    val isTodayActive: Boolean = false,
    val lastActiveDate: String? = null,
    val weeklyActivity: List<DayActivity> = emptyList(),
    val weeklyTotalXp: Int = 0,
    val totalXpEarned: Int = 0,
    val totalQuestionsCompleted: Int = 0,
    val totalLessonsCompleted: Int = 0,
    val achievements: List<AchievementProgress> = emptyList(),
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0
)

@Singleton
class UserProgressStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val progress: Flow<DailyProgress> = context.userPreferences.data.map { preferences ->
        buildDailyProgress(preferences, Date())
    }

    val isDarkTheme: Flow<Boolean?> = context.userPreferences.data.map { preferences ->
        preferences[DARK_THEME]
    }

    suspend fun addXp(points: Int) {
        require(points >= 0)
        val now = Date()
        context.userPreferences.edit { preferences ->
            applyXpInPreferences(preferences, points, now)
            if (points > 0) {
                recordActivityInPreferences(preferences, now)
            }
            evaluateAndPersistAchievements(preferences, now)
        }
    }

    suspend fun recordQuestionCompleted(
        correct: Boolean,
        xpReward: Int = if (correct) AppConstants.XP_PER_CORRECT_ANSWER else 0
    ) {
        require(xpReward >= 0)
        val now = Date()
        context.userPreferences.edit { preferences ->
            preferences[TOTAL_QUESTIONS_COMPLETED] = (preferences[TOTAL_QUESTIONS_COMPLETED] ?: 0) + 1
            if (xpReward > 0) {
                applyXpInPreferences(preferences, xpReward, now)
            }
            recordActivityInPreferences(preferences, now)
            evaluateAndPersistAchievements(preferences, now)
        }
    }

    suspend fun markLessonCompleted() {
        val now = Date()
        context.userPreferences.edit { preferences ->
            preferences[TOTAL_LESSONS_COMPLETED] = (preferences[TOTAL_LESSONS_COMPLETED] ?: 0) + 1
            recordActivityInPreferences(preferences, now)
            evaluateAndPersistAchievements(preferences, now)
        }
    }

    suspend fun recordDailyActivity() {
        val now = Date()
        context.userPreferences.edit { preferences ->
            recordActivityInPreferences(preferences, now)
            evaluateAndPersistAchievements(preferences, now)
        }
    }

    suspend fun syncStreak() {
        val now = Date()
        context.userPreferences.edit { preferences ->
            val today = dateString(now)
            val yesterday = yesterdayString(now)
            val lastActive = preferences[LAST_ACTIVE_DATE] ?: preferences[LAST_LESSON_DATE]
            val storedStreak = preferences[STREAK] ?: 0

            if (lastActive != today && lastActive != yesterday && storedStreak != 0) {
                preferences[STREAK] = 0
            }

            if (lastActive != null && preferences[LAST_ACTIVE_DATE] == null) {
                preferences[LAST_ACTIVE_DATE] = lastActive
            }
            if ((preferences[BEST_STREAK] ?: 0) < storedStreak && (lastActive == today || lastActive == yesterday)) {
                preferences[BEST_STREAK] = storedStreak
            }
            evaluateAndPersistAchievements(preferences, now)
        }
    }

    suspend fun setReminderSettings(enabled: Boolean, hour: Int, minute: Int) {
        context.userPreferences.edit { preferences ->
            preferences[REMINDER_ENABLED] = enabled
            preferences[REMINDER_HOUR] = hour.coerceIn(0, 23)
            preferences[REMINDER_MINUTE] = minute.coerceIn(0, 59)
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.userPreferences.edit { preferences ->
            preferences[DARK_THEME] = enabled
        }
    }

    private fun buildDailyProgress(preferences: Preferences, now: Date): DailyProgress {
        val today = dateString(now)
        val yesterday = yesterdayString(now)
        val lastActive = preferences[LAST_ACTIVE_DATE] ?: preferences[LAST_LESSON_DATE]
        val storedStreak = preferences[STREAK] ?: 0
        val xpToday = if (preferences[XP_DATE] == today) preferences[XP_TODAY] ?: 0 else 0

        val isStreakAlive = lastActive == today || lastActive == yesterday
        val effectiveStreak = when {
            lastActive == today -> storedStreak.coerceAtLeast(1)
            lastActive == yesterday -> storedStreak
            else -> 0
        }

        val dailyXpMap = parseDailyXp(preferences[DAILY_XP_HISTORY]).toMutableMap()
        if (xpToday > 0) {
            dailyXpMap[today] = maxOf(dailyXpMap[today] ?: 0, xpToday)
        }

        val storedDates = parseActiveDates(preferences[ACTIVE_DATES])
        val activeDates = buildSet {
            addAll(storedDates)
            if (lastActive != null) add(lastActive)
            if (xpToday > 0) add(today)
        }

        val isTodayActive = lastActive == today || today in activeDates
        val bestStreak = maxOf(preferences[BEST_STREAK] ?: 0, effectiveStreak)
        val totalActiveDays = maxOf(
            preferences[TOTAL_ACTIVE_DAYS] ?: 0,
            activeDates.size,
            if (isStreakAlive && effectiveStreak > 0) 1 else 0
        )

        val weekDates = currentWeekDates(now)
        val labels = listOf("B.e", "Ç.a", "Ç.", "C.a", "C.", "Ş.", "B.")
        val weeklyActivity = weekDates.mapIndexed { index, date ->
            DayActivity(
                date = date,
                dayShortLabel = labels[index],
                isCompleted = date in activeDates,
                isToday = date == today,
                xpEarned = dailyXpMap[date] ?: 0
            )
        }

        val totalXpEarned = maxOf(
            preferences[TOTAL_XP] ?: 0,
            dailyXpMap.values.sum(),
            xpToday
        )
        val totalQuestionsCompleted = preferences[TOTAL_QUESTIONS_COMPLETED] ?: 0
        val totalLessonsCompleted = preferences[TOTAL_LESSONS_COMPLETED] ?: 0
        val unlockedTimestamps = parseUnlockedAchievements(preferences[UNLOCKED_ACHIEVEMENTS])
        val achievements = AchievementCatalog.evaluate(
            snapshot = MilestoneSnapshot(
                bestStreak = bestStreak,
                totalActiveDays = totalActiveDays,
                totalQuestionsCompleted = totalQuestionsCompleted,
                totalLessonsCompleted = totalLessonsCompleted,
                totalXpEarned = totalXpEarned
            ),
            unlockedTimestamps = unlockedTimestamps
        )

        return DailyProgress(
            xpToday = xpToday,
            streak = effectiveStreak,
            bestStreak = bestStreak,
            totalActiveDays = totalActiveDays,
            isTodayActive = isTodayActive,
            lastActiveDate = lastActive,
            weeklyActivity = weeklyActivity,
            weeklyTotalXp = weeklyActivity.sumOf { it.xpEarned },
            totalXpEarned = totalXpEarned,
            totalQuestionsCompleted = totalQuestionsCompleted,
            totalLessonsCompleted = totalLessonsCompleted,
            achievements = achievements,
            reminderEnabled = preferences[REMINDER_ENABLED] ?: false,
            reminderHour = (preferences[REMINDER_HOUR] ?: 19).coerceIn(0, 23),
            reminderMinute = (preferences[REMINDER_MINUTE] ?: 0).coerceIn(0, 59)
        )
    }

    private fun applyXpInPreferences(preferences: MutablePreferences, points: Int, now: Date) {
        val today = dateString(now)
        val previousXp = if (preferences[XP_DATE] == today) preferences[XP_TODAY] ?: 0 else 0
        val newXp = previousXp + points
        preferences[XP_DATE] = today
        preferences[XP_TODAY] = newXp
        if (points > 0) {
            val dailyXp = parseDailyXp(preferences[DAILY_XP_HISTORY]).toMutableMap()
            val historicalSum = maxOf(preferences[TOTAL_XP] ?: 0, dailyXp.values.sum(), previousXp)
            dailyXp[today] = maxOf(dailyXp[today] ?: 0, newXp)
            preferences[DAILY_XP_HISTORY] = serializeDailyXp(dailyXp)
            preferences[TOTAL_XP] = historicalSum + points
        }
    }

    private fun evaluateAndPersistAchievements(preferences: MutablePreferences, now: Date) {
        val today = dateString(now)
        val yesterday = yesterdayString(now)
        val lastActive = preferences[LAST_ACTIVE_DATE] ?: preferences[LAST_LESSON_DATE]
        val storedStreak = preferences[STREAK] ?: 0
        val xpToday = if (preferences[XP_DATE] == today) preferences[XP_TODAY] ?: 0 else 0
        val effectiveStreak = when {
            lastActive == today -> storedStreak.coerceAtLeast(1)
            lastActive == yesterday -> storedStreak
            else -> 0
        }
        val bestStreak = maxOf(preferences[BEST_STREAK] ?: 0, effectiveStreak)
        val activeDates = parseActiveDates(preferences[ACTIVE_DATES])
        val totalActiveDays = maxOf(
            preferences[TOTAL_ACTIVE_DAYS] ?: 0,
            activeDates.size,
            if (effectiveStreak > 0) 1 else 0
        )
        val dailyXpMap = parseDailyXp(preferences[DAILY_XP_HISTORY])
        val totalXpEarned = maxOf(
            preferences[TOTAL_XP] ?: 0,
            dailyXpMap.values.sum(),
            xpToday
        )
        val snapshot = MilestoneSnapshot(
            bestStreak = bestStreak,
            totalActiveDays = totalActiveDays,
            totalQuestionsCompleted = preferences[TOTAL_QUESTIONS_COMPLETED] ?: 0,
            totalLessonsCompleted = preferences[TOTAL_LESSONS_COMPLETED] ?: 0,
            totalXpEarned = totalXpEarned
        )

        val unlocked = parseUnlockedAchievements(preferences[UNLOCKED_ACHIEVEMENTS]).toMutableMap()
        var changed = false
        AchievementCatalog.all.forEach { definition ->
            if (!unlocked.containsKey(definition.id) &&
                AchievementCatalog.metricValueFor(definition, snapshot) >= definition.targetValue
            ) {
                unlocked[definition.id] = now.time
                changed = true
            }
        }
        if (changed) {
            preferences[UNLOCKED_ACHIEVEMENTS] = serializeUnlockedAchievements(unlocked)
        }
    }

    private fun recordActivityInPreferences(preferences: MutablePreferences, now: Date) {
        val today = dateString(now)
        val yesterday = yesterdayString(now)
        val lastActive = preferences[LAST_ACTIVE_DATE] ?: preferences[LAST_LESSON_DATE]
        val currentStreak = preferences[STREAK] ?: 0

        val newStreak = when (lastActive) {
            today -> currentStreak.coerceAtLeast(1)
            yesterday -> currentStreak + 1
            else -> 1
        }

        val existingDates = parseActiveDates(preferences[ACTIVE_DATES]).toMutableSet()
        if (lastActive != null) existingDates.add(lastActive)
        val wasTodayRecorded = today in existingDates
        existingDates.add(today)

        val previousTotal = preferences[TOTAL_ACTIVE_DAYS] ?: (existingDates.size - if (wasTodayRecorded) 0 else 1)
        val newTotal = if (wasTodayRecorded) maxOf(previousTotal, existingDates.size) else previousTotal + 1

        preferences[STREAK] = newStreak
        preferences[BEST_STREAK] = maxOf(preferences[BEST_STREAK] ?: 0, newStreak)
        preferences[LAST_ACTIVE_DATE] = today
        preferences[LAST_LESSON_DATE] = today
        preferences[TOTAL_ACTIVE_DAYS] = newTotal
        preferences[ACTIVE_DATES] = existingDates.sorted().takeLast(MAX_STORED_ACTIVE_DATES).joinToString("\n")
    }

    private fun parseActiveDates(raw: String?): Set<String> =
        raw.orEmpty()
            .lineSequence()
            .map(String::trim)
            .filter(String::isNotBlank)
            .toSet()

    private fun parseDailyXp(raw: String?): Map<String, Int> =
        raw.orEmpty()
            .lineSequence()
            .mapNotNull { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    val date = parts[0].trim()
                    val xp = parts[1].trim().toIntOrNull()
                    if (date.isNotBlank() && xp != null && xp >= 0) date to xp else null
                } else {
                    null
                }
            }
            .toMap()

    private fun serializeDailyXp(map: Map<String, Int>): String =
        map.entries
            .sortedBy { it.key }
            .takeLast(MAX_STORED_ACTIVE_DATES)
            .joinToString("\n") { (date, xp) -> "$date=$xp" }

    private fun parseUnlockedAchievements(raw: String?): Map<String, Long> =
        raw.orEmpty()
            .lineSequence()
            .mapNotNull { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    val id = parts[0].trim()
                    val ts = parts[1].trim().toLongOrNull()
                    if (id.isNotBlank() && ts != null) id to ts else null
                } else {
                    null
                }
            }
            .toMap()

    private fun serializeUnlockedAchievements(map: Map<String, Long>): String =
        map.entries
            .sortedBy { it.value }
            .joinToString("\n") { (id, ts) -> "$id=$ts" }

    private fun dateString(date: Date = Date()): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)

    private fun yesterdayString(now: Date = Date()): String {
        val calendar = Calendar.getInstance().apply {
            time = now
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return dateString(calendar.time)
    }

    private fun currentWeekDates(now: Date): List<String> {
        val calendar = Calendar.getInstance().apply {
            time = now
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            val dayOfWeek = get(Calendar.DAY_OF_WEEK)
            val diffFromMonday = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek
            add(Calendar.DAY_OF_YEAR, diffFromMonday)
        }
        return List(7) {
            val formatted = dateString(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            formatted
        }
    }

    private companion object {
        const val MAX_STORED_ACTIVE_DATES = 90
        val XP_TODAY = intPreferencesKey("xp_today")
        val XP_DATE = stringPreferencesKey("xp_date")
        val TOTAL_XP = intPreferencesKey("total_xp")
        val STREAK = intPreferencesKey("streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val LAST_LESSON_DATE = stringPreferencesKey("last_lesson_date")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val ACTIVE_DATES = stringPreferencesKey("active_dates")
        val DAILY_XP_HISTORY = stringPreferencesKey("daily_xp_history")
        val TOTAL_ACTIVE_DAYS = intPreferencesKey("total_active_days")
        val TOTAL_QUESTIONS_COMPLETED = intPreferencesKey("total_questions_completed")
        val TOTAL_LESSONS_COMPLETED = intPreferencesKey("total_lessons_completed")
        val UNLOCKED_ACHIEVEMENTS = stringPreferencesKey("unlocked_achievements")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }
}
