package com.rusdili.pro.core.common

enum class LearningLanguage(
    val code: String,
    val displayNameAz: String,
    val nativeName: String,
    val ttsTag: String,
    val fallbackTtsTag: String,
    val greeting: String,
    val themeColorHex: Long
) {
    RUSSIAN(
        code = "ru",
        displayNameAz = "Rus dili",
        nativeName = "Русский",
        ttsTag = "ru-RU",
        fallbackTtsTag = "ru",
        greeting = "Привет!",
        themeColorHex = 0xFF2196F3
    ),
    SPANISH(
        code = "es",
        displayNameAz = "İspan dili",
        nativeName = "Español",
        ttsTag = "es-ES",
        fallbackTtsTag = "es",
        greeting = "¡Hola!",
        themeColorHex = 0xFFE53935
    ),
    FRENCH(
        code = "fr",
        displayNameAz = "Fransız dili",
        nativeName = "Français",
        ttsTag = "fr-FR",
        fallbackTtsTag = "fr",
        greeting = "Bonjour!",
        themeColorHex = 0xFF7E57C2
    );

    companion object {
        fun fromCode(code: String?): LearningLanguage =
            entries.firstOrNull { it.code == code } ?: RUSSIAN
    }
}
