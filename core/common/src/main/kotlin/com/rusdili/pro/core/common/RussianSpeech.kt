package com.rusdili.pro.core.common

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class RussianSpeech @Inject constructor(
    @ApplicationContext context: Context
) : TextToSpeech.OnInitListener {
    private val engine = TextToSpeech(context, this)
    private var ready = false
    private var pendingRequest: Pair<String, Boolean>? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            ready = false
            return
        }
        engine.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            }
        )
        val primaryStatus = engine.setLanguage(Locale.forLanguageTag("ru-RU"))
        val supported = if (isSupported(primaryStatus)) {
            true
        } else {
            isSupported(engine.setLanguage(Locale.forLanguageTag("ru")))
        }
        ready = supported
        if (ready) {
            pendingRequest?.let { (text, slow) ->
                pendingRequest = null
                speakNow(text, slow)
            }
        }
    }

    fun speak(text: String, slow: Boolean = false) {
        val normalized = text.trim()
        if (normalized.isBlank()) return
        if (ready) {
            speakNow(normalized, slow)
        } else {
            pendingRequest = normalized to slow
        }
    }

    fun stop() {
        pendingRequest = null
        if (ready) {
            engine.stop()
        }
        _isSpeaking.value = false
    }

    private fun speakNow(text: String, slow: Boolean) {
        engine.setSpeechRate(if (slow) 0.72f else 1.0f)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ll-${text.hashCode()}")
    }

    private fun isSupported(languageStatus: Int): Boolean =
        languageStatus != TextToSpeech.LANG_MISSING_DATA &&
            languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
}
