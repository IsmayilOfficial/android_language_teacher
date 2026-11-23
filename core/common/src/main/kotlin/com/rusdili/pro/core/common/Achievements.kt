package com.rusdili.pro.core.common

enum class AchievementCategory(val labelAz: String) {
    STREAK("Seriya"),
    QUIZ("Test"),
    LESSONS("Dərs"),
    XP("XP")
}

data class AchievementDefinition(
    val id: String,
    val titleAz: String,
    val descriptionAz: String,
    val category: AchievementCategory,
    val targetValue: Int,
    val badgeCode: String
)

data class AchievementProgress(
    val definition: AchievementDefinition,
    val currentValue: Int,
    val isUnlocked: Boolean,
    val unlockedAtEpochMs: Long? = null
) {
    val progressFraction: Float
        get() = if (definition.targetValue <= 0) {
            1f
        } else {
            (currentValue.toFloat() / definition.targetValue.toFloat()).coerceIn(0f, 1f)
        }

    val clampedCurrent: Int
        get() = currentValue.coerceAtMost(definition.targetValue)
}

data class MilestoneSnapshot(
    val bestStreak: Int,
    val totalActiveDays: Int,
    val totalQuestionsCompleted: Int,
    val totalLessonsCompleted: Int,
    val totalXpEarned: Int
)

object AchievementCatalog {
    val all: List<AchievementDefinition> = listOf(
        AchievementDefinition(
            id = "first_step",
            titleAz = "İlk addım",
            descriptionAz = "İlk günlük öyrənmə fəaliyyətini qeyd et",
            category = AchievementCategory.STREAK,
            targetValue = 1,
            badgeCode = "1G"
        ),
        AchievementDefinition(
            id = "streak_3",
            titleAz = "3 günlük alov",
            descriptionAz = "Ardıcıl 3 gün öyrənmə seriyasına çat",
            category = AchievementCategory.STREAK,
            targetValue = 3,
            badgeCode = "3G"
        ),
        AchievementDefinition(
            id = "streak_7",
            titleAz = "Həftəlik seriya",
            descriptionAz = "Ardıcıl 7 gün fasiləsiz davam et",
            category = AchievementCategory.STREAK,
            targetValue = 7,
            badgeCode = "7G"
        ),
        AchievementDefinition(
            id = "streak_30",
            titleAz = "Ayın çempionu",
            descriptionAz = "Ardıcıl 30 günlük seriyaya çat",
            category = AchievementCategory.STREAK,
            targetValue = 30,
            badgeCode = "30G"
        ),
        AchievementDefinition(
            id = "quiz_10",
            titleAz = "Məşq başlanğıcı",
            descriptionAz = "10 test sualını cavabla",
            category = AchievementCategory.QUIZ,
            targetValue = 10,
            badgeCode = "10S"
        ),
        AchievementDefinition(
            id = "quiz_50",
            titleAz = "Test ustası",
            descriptionAz = "50 test sualını tamamla",
            category = AchievementCategory.QUIZ,
            targetValue = 50,
            badgeCode = "50S"
        ),
        AchievementDefinition(
            id = "quiz_100",
            titleAz = "Sual qrossmeysteri",
            descriptionAz = "100 test sualını tamamla",
            category = AchievementCategory.QUIZ,
            targetValue = 100,
            badgeCode = "100S"
        ),
        AchievementDefinition(
            id = "lesson_5",
            titleAz = "Kitab sevər",
            descriptionAz = "5 interaktiv dərsi tamamla",
            category = AchievementCategory.LESSONS,
            targetValue = 5,
            badgeCode = "5D"
        ),
        AchievementDefinition(
            id = "xp_250",
            titleAz = "XP toplayan",
            descriptionAz = "Cəmi 250 XP topla",
            category = AchievementCategory.XP,
            targetValue = 250,
            badgeCode = "250XP"
        ),
        AchievementDefinition(
            id = "xp_1000",
            titleAz = "Rus dili bilicisi",
            descriptionAz = "Cəmi 1000 XP topla",
            category = AchievementCategory.XP,
            targetValue = 1000,
            badgeCode = "1000XP"
        )
    )

    fun metricValueFor(definition: AchievementDefinition, snapshot: MilestoneSnapshot): Int =
        when (definition.id) {
            "first_step" -> snapshot.totalActiveDays
            "streak_3", "streak_7", "streak_30" -> snapshot.bestStreak
            "quiz_10", "quiz_50", "quiz_100" -> snapshot.totalQuestionsCompleted
            "lesson_5" -> snapshot.totalLessonsCompleted
            "xp_250", "xp_1000" -> snapshot.totalXpEarned
            else -> 0
        }

    fun evaluate(
        snapshot: MilestoneSnapshot,
        unlockedTimestamps: Map<String, Long>
    ): List<AchievementProgress> = all.map { definition ->
        val current = metricValueFor(definition, snapshot)
        val unlockedAt = unlockedTimestamps[definition.id]
        val isUnlocked = unlockedAt != null || current >= definition.targetValue
        AchievementProgress(
            definition = definition,
            currentValue = if (isUnlocked) maxOf(current, definition.targetValue) else current,
            isUnlocked = isUnlocked,
            unlockedAtEpochMs = unlockedAt
        )
    }
}
