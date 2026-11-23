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
    val reminderMinute: Int = 0,
    val language: LearningLanguage = LearningLanguage.RUSSIAN
)

@Singleton
class UserProgressStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val selectedLanguageSelection: Flow<LearningLanguage?> = context.userPreferences.data.map { preferences ->
        preferences[SELECTED_LANGUAGE]?.let { LearningLanguage.fromCode(it) }
    }

    val selectedLanguage: Flow<LearningLanguage> = context.userPreferences.data.map { preferences ->
        LearningLanguage.fromCode(preferences[SELECTED_LANGUAGE])
    }

    val progress: Flow<DailyProgress> = context.userPreferences.data.map { preferences ->
        val language = LearningLanguage.fromCode(preferences[SELECTED_LANGUAGE])
        buildDailyProgress(preferences, language, Date())
    }

    fun progressFor(language: LearningLanguage): Flow<DailyProgress> =
        context.userPreferences.data.map { preferences ->
            buildDailyProgress(preferences, language, Date())
        }

    val isDarkTheme: Flow<Boolean?> = context.userPreferences.data.map { preferences ->
        preferences[DARK_THEME]
    }

    suspend fun setSelectedLanguage(language: LearningLanguage) {
        context.userPreferences.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language.code
        }
    }

    suspend fun addXp(points: Int, language: LearningLanguage? = null) {
        require(points >= 0)
        val now = Date()
        context.userPreferences.edit { preferences ->
            val lang = language ?: LearningLanguage.fromCode(preferences[SELECTED_LANGUAGE])
            applyXpInPreferences(preferences, lang, points, now)
            if (points > 0) {
                recordActivityInPreferences(preferences, lang, now)
            }
            evaluateAndPersistAchievements(preferences, lang, now)
        }
    }

    suspend fun recordQuestionCompleted(
        correct: Boolean,
        xpReward: Int = if (correct) AppConstants.XP_PER_CORRECT_ANSWER else 0,
        language: LearningLanguage? = null
    ) {
        require(xpReward >= 0)
        val now = Date()
        context.userPreferences.edit { preferences ->
            val lang = language ?: LearningLanguage.fromCode(preferences[SELECTED_LANGUAGE])
            val keys = LangKeys(lang)
            val prevQuestions = readInt(preferences, keys.totalQuestions, LEGACY_TOTAL_QUESTIONS_COMPLETED, lang)
            preferences[keys.totalQuestions] = prevQuestions + 1
            if (xpReward > 0) {
                applyXpInPreferences(preferences, lang, xpReward, now)
            }
            recordActivityInPreferences(preferences, lang, now)
            evaluateAndPersistAchievements(preferences, lang, now)
        }
    }

    suspend fun markLessonCompleted(language: LearningLanguage? = null) {
        val now = Date()
        context.userPreferences.edit { preferences ->
            val lang = language ?: LearningLanguage.fromCode(preferences[SELECTED_LANGUAGE])
            val keys = LangKeys(lang)
            val prevLessons = readInt(preferences, keys.totalLessons, LEGACY_TOTAL_LESSONS_COMPLETED, lang)
            preferences[keys.totalLessons] = prevLessons + 1
            recordActivityInPreferences(preferences, lang, now)
            evaluateAndPersistAchievements(preferences, lang, now)
        }
    }

    suspend fun recordDailyActivity(language: LearningLanguage? = null) {
        val now = Date()
        context.userPreferences.edit { preferences ->
            val lang = language ?: LearningLanguage.fromCode(preferences[SELECTED_LANGUAGE])
            recordActivityInPreferences(preferences, lang, now)
            evaluateAndPersistAchievements(preferences, lang, now)
        }
    }

    suspend fun syncStreak(language: LearningLanguage? = null) {
        val now = Date()
        context.userPreferences.edit { preferences ->
            val lang = language ?: LearningLanguage.fromCode(preferences[SELECTED_LANGUAGE])
            val keys = LangKeys(lang)
            val today = dateString(now)
            val yesterday = yesterdayString(now)
            val lastActive = readString(preferences, keys.lastActiveDate, LEGACY_LAST_ACTIVE_DATE, lang)
                ?: if (lang == LearningLanguage.RUSSIAN) preferences[LEGACY_LAST_LESSON_DATE] else null
            val storedStreak = readInt(preferences, keys.streak, LEGACY_STREAK, lang)

            if (lastActive != today && lastActive != yesterday && storedStreak != 0) {
                preferences[keys.streak] = 0
            }

            if (lastActive != null && preferences[keys.lastActiveDate] == null) {
                preferences[keys.lastActiveDate] = lastActive
            }
            val bestStreak = readInt(preferences, keys.bestStreak, LEGACY_BEST_STREAK, lang)
            if (bestStreak < storedStreak && (lastActive == today || lastActive == yesterday)) {
                preferences[keys.bestStreak] = storedStreak
            }
            evaluateAndPersistAchievements(preferences, lang, now)
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

    private fun buildDailyProgress(
        preferences: Preferences,
        lang: LearningLanguage,
        now: Date
    ): DailyProgress {
        val keys = LangKeys(lang)
        val today = dateString(now)
        val yesterday = yesterdayString(now)
        val lastActive = readString(preferences, keys.lastActiveDate, LEGACY_LAST_ACTIVE_DATE, lang)
            ?: if (lang == LearningLanguage.RUSSIAN) preferences[LEGACY_LAST_LESSON_DATE] else null
        val storedStreak = readInt(preferences, keys.streak, LEGACY_STREAK, lang)
        val xpDate = readString(preferences, keys.xpDate, LEGACY_XP_DATE, lang)
        val xpToday = if (xpDate == today) readInt(preferences, keys.xpToday, LEGACY_XP_TODAY, lang) else 0

        val isStreakAlive = lastActive == today || lastActive == yesterday
        val effectiveStreak = when {
            lastActive == today -> storedStreak.coerceAtLeast(1)
            lastActive == yesterday -> storedStreak
            else -> 0
        }

        val dailyXpMap = parseDailyXp(
            readString(preferences, keys.dailyXpHistory, LEGACY_DAILY_XP_HISTORY, lang)
        ).toMutableMap()
        if (xpToday > 0) {
            dailyXpMap[today] = maxOf(dailyXpMap[today] ?: 0, xpToday)
        }

        val storedDates = parseActiveDates(
            readString(preferences, keys.activeDates, LEGACY_ACTIVE_DATES, lang)
        )
        val activeDates = buildSet {
            addAll(storedDates)
            if (lastActive != null) add(lastActive)
            if (xpToday > 0) add(today)
        }

        val isTodayActive = lastActive == today || today in activeDates
        val storedBest = readInt(preferences, keys.bestStreak, LEGACY_BEST_STREAK, lang)
        val bestStreak = maxOf(storedBest, effectiveStreak)
        val storedTotalActive = readInt(preferences, keys.totalActiveDays, LEGACY_TOTAL_ACTIVE_DAYS, lang)
        val totalActiveDays = maxOf(
            storedTotalActive,
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

        val storedTotalXp = readInt(preferences, keys.totalXp, LEGACY_TOTAL_XP, lang)
        val totalXpEarned = maxOf(
            storedTotalXp,
            dailyXpMap.values.sum(),
            xpToday
        )
        val totalQuestionsCompleted = readInt(preferences, keys.totalQuestions, LEGACY_TOTAL_QUESTIONS_COMPLETED, lang)
        val totalLessonsCompleted = readInt(preferences, keys.totalLessons, LEGACY_TOTAL_LESSONS_COMPLETED, lang)
        val unlockedTimestamps = parseUnlockedAchievements(
            readString(preferences, keys.unlockedAchievements, LEGACY_UNLOCKED_ACHIEVEMENTS, lang)
        )
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
            reminderMinute = (preferences[REMINDER_MINUTE] ?: 0).coerceIn(0, 59),
            language = lang
        )
    }

    private fun applyXpInPreferences(
        preferences: MutablePreferences,
        lang: LearningLanguage,
        points: Int,
        now: Date
    ) {
        val keys = LangKeys(lang)
        val today = dateString(now)
        val xpDate = readString(preferences, keys.xpDate, LEGACY_XP_DATE, lang)
        val previousXp = if (xpDate == today) readInt(preferences, keys.xpToday, LEGACY_XP_TODAY, lang) else 0
        val newXp = previousXp + points
        preferences[keys.xpDate] = today
        preferences[keys.xpToday] = newXp
        if (points > 0) {
            val dailyXp = parseDailyXp(
                readString(preferences, keys.dailyXpHistory, LEGACY_DAILY_XP_HISTORY, lang)
            ).toMutableMap()
            val storedTotalXp = readInt(preferences, keys.totalXp, LEGACY_TOTAL_XP, lang)
            val historicalSum = maxOf(storedTotalXp, dailyXp.values.sum(), previousXp)
            dailyXp[today] = maxOf(dailyXp[today] ?: 0, newXp)
            preferences[keys.dailyXpHistory] = serializeDailyXp(dailyXp)
            preferences[keys.totalXp] = historicalSum + points
        }
    }

    private fun evaluateAndPersistAchievements(
        preferences: MutablePreferences,
        lang: LearningLanguage,
        now: Date
    ) {
        val keys = LangKeys(lang)
        val today = dateString(now)
        val yesterday = yesterdayString(now)
        val lastActive = readString(preferences, keys.lastActiveDate, LEGACY_LAST_ACTIVE_DATE, lang)
            ?: if (lang == LearningLanguage.RUSSIAN) preferences[LEGACY_LAST_LESSON_DATE] else null
        val storedStreak = readInt(preferences, keys.streak, LEGACY_STREAK, lang)
        val xpDate = readString(preferences, keys.xpDate, LEGACY_XP_DATE, lang)
        val xpToday = if (xpDate == today) readInt(preferences, keys.xpToday, LEGACY_XP_TODAY, lang) else 0
        val effectiveStreak = when {
            lastActive == today -> storedStreak.coerceAtLeast(1)
            lastActive == yesterday -> storedStreak
            else -> 0
        }
        val bestStreak = maxOf(readInt(preferences, keys.bestStreak, LEGACY_BEST_STREAK, lang), effectiveStreak)
        val activeDates = parseActiveDates(readString(preferences, keys.activeDates, LEGACY_ACTIVE_DATES, lang))
        val totalActiveDays = maxOf(
            readInt(preferences, keys.totalActiveDays, LEGACY_TOTAL_ACTIVE_DAYS, lang),
            activeDates.size,
            if (effectiveStreak > 0) 1 else 0
        )
        val dailyXpMap = parseDailyXp(readString(preferences, keys.dailyXpHistory, LEGACY_DAILY_XP_HISTORY, lang))
        val totalXpEarned = maxOf(
            readInt(preferences, keys.totalXp, LEGACY_TOTAL_XP, lang),
            dailyXpMap.values.sum(),
            xpToday
        )
        val snapshot = MilestoneSnapshot(
            bestStreak = bestStreak,
            totalActiveDays = totalActiveDays,
            totalQuestionsCompleted = readInt(preferences, keys.totalQuestions, LEGACY_TOTAL_QUESTIONS_COMPLETED, lang),
            totalLessonsCompleted = readInt(preferences, keys.totalLessons, LEGACY_TOTAL_LESSONS_COMPLETED, lang),
            totalXpEarned = totalXpEarned
        )

        val unlocked = parseUnlockedAchievements(
            readString(preferences, keys.unlockedAchievements, LEGACY_UNLOCKED_ACHIEVEMENTS, lang)
        ).toMutableMap()
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
            preferences[keys.unlockedAchievements] = serializeUnlockedAchievements(unlocked)
        }
    }

    private fun recordActivityInPreferences(
        preferences: MutablePreferences,
        lang: LearningLanguage,
        now: Date
    ) {
        val keys = LangKeys(lang)
        val today = dateString(now)
        val yesterday = yesterdayString(now)
        val lastActive = readString(preferences, keys.lastActiveDate, LEGACY_LAST_ACTIVE_DATE, lang)
            ?: if (lang == LearningLanguage.RUSSIAN) preferences[LEGACY_LAST_LESSON_DATE] else null
        val currentStreak = readInt(preferences, keys.streak, LEGACY_STREAK, lang)

        val newStreak = when (lastActive) {
            today -> currentStreak.coerceAtLeast(1)
            yesterday -> currentStreak + 1
            else -> 1
        }

        val existingDates = parseActiveDates(
            readString(preferences, keys.activeDates, LEGACY_ACTIVE_DATES, lang)
        ).toMutableSet()
        if (lastActive != null) existingDates.add(lastActive)
        val wasTodayRecorded = today in existingDates
        existingDates.add(today)

        val storedTotal = preferences[keys.totalActiveDays]
            ?: if (lang == LearningLanguage.RUSSIAN) preferences[LEGACY_TOTAL_ACTIVE_DAYS] else null
        val previousTotal = storedTotal ?: (existingDates.size - if (wasTodayRecorded) 0 else 1)
        val newTotal = if (wasTodayRecorded) maxOf(previousTotal, existingDates.size) else previousTotal + 1

        preferences[keys.streak] = newStreak
        preferences[keys.bestStreak] = maxOf(
            readInt(preferences, keys.bestStreak, LEGACY_BEST_STREAK, lang),
            newStreak
        )
        preferences[keys.lastActiveDate] = today
        if (lang == LearningLanguage.RUSSIAN) {
            preferences[LEGACY_LAST_LESSON_DATE] = today
        }
        preferences[keys.totalActiveDays] = newTotal
        preferences[keys.activeDates] = existingDates.sorted().takeLast(MAX_STORED_ACTIVE_DATES).joinToString("\n")
    }

    private fun readInt(
        preferences: Preferences,
        key: Preferences.Key<Int>,
        legacyRussianKey: Preferences.Key<Int>,
        lang: LearningLanguage
    ): Int = preferences[key]
        ?: if (lang == LearningLanguage.RUSSIAN) (preferences[legacyRussianKey] ?: 0) else 0

    private fun readString(
        preferences: Preferences,
        key: Preferences.Key<String>,
        legacyRussianKey: Preferences.Key<String>,
        lang: LearningLanguage
    ): String? = preferences[key]
        ?: if (lang == LearningLanguage.RUSSIAN) preferences[legacyRussianKey] else null

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

    private class LangKeys(lang: LearningLanguage) {
        private val p = lang.code
        val xpToday = intPreferencesKey("${p}_xp_today")
        val xpDate = stringPreferencesKey("${p}_xp_date")
        val totalXp = intPreferencesKey("${p}_total_xp")
        val streak = intPreferencesKey("${p}_streak")
        val bestStreak = intPreferencesKey("${p}_best_streak")
        val lastActiveDate = stringPreferencesKey("${p}_last_active_date")
        val activeDates = stringPreferencesKey("${p}_active_dates")
        val dailyXpHistory = stringPreferencesKey("${p}_daily_xp_history")
        val totalActiveDays = intPreferencesKey("${p}_total_active_days")
        val totalQuestions = intPreferencesKey("${p}_total_questions_completed")
        val totalLessons = intPreferencesKey("${p}_total_lessons_completed")
        val unlockedAchievements = stringPreferencesKey("${p}_unlocked_achievements")
    }

    private companion object {
        const val MAX_STORED_ACTIVE_DATES = 90
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val LEGACY_XP_TODAY = intPreferencesKey("xp_today")
        val LEGACY_XP_DATE = stringPreferencesKey("xp_date")
        val LEGACY_TOTAL_XP = intPreferencesKey("total_xp")
        val LEGACY_STREAK = intPreferencesKey("streak")
        val LEGACY_BEST_STREAK = intPreferencesKey("best_streak")
        val LEGACY_LAST_LESSON_DATE = stringPreferencesKey("last_lesson_date")
        val LEGACY_LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val LEGACY_ACTIVE_DATES = stringPreferencesKey("active_dates")
        val LEGACY_DAILY_XP_HISTORY = stringPreferencesKey("daily_xp_history")
        val LEGACY_TOTAL_ACTIVE_DAYS = intPreferencesKey("total_active_days")
        val LEGACY_TOTAL_QUESTIONS_COMPLETED = intPreferencesKey("total_questions_completed")
        val LEGACY_TOTAL_LESSONS_COMPLETED = intPreferencesKey("total_lessons_completed")
        val LEGACY_UNLOCKED_ACHIEVEMENTS = stringPreferencesKey("unlocked_achievements")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }
}
