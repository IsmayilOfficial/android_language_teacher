package com.rusdili.pro.feature.book

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Locale

@Singleton
class RussianSpeech @Inject constructor(
    @ApplicationContext context: Context
) : TextToSpeech.OnInitListener {
    private val engine = TextToSpeech(context, this)
    private var ready = false
    private var pendingText: String? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val languageStatus = engine.setLanguage(Locale.forLanguageTag("ru-RU"))
            ready = languageStatus != TextToSpeech.LANG_MISSING_DATA &&
                languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
            if (ready) pendingText?.let(::speakNow)
        }
    }

    fun speak(text: String) {
        if (ready) speakNow(text) else pendingText = text
    }

    private fun speakNow(text: String) {
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "rusdili-${text.hashCode()}")
    }
}
