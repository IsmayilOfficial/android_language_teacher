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
    @Volatile private var activeText: String? = null
    @Volatile private var activeUtteranceId: String? = null
    private var utteranceCounter = 0L

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speakingText = MutableStateFlow<String?>(null)
    val speakingText: StateFlow<String?> = _speakingText.asStateFlow()

    private val _isAvailable = MutableStateFlow(true)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            ready = false
            _isAvailable.value = false
            return
        }
        engine.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    if (utteranceId == activeUtteranceId) {
                        _isSpeaking.value = true
                        _speakingText.value = activeText
                    }
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == activeUtteranceId) {
                        activeText = null
                        _isSpeaking.value = false
                        _speakingText.value = null
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (utteranceId == activeUtteranceId) {
                        activeText = null
                        _isSpeaking.value = false
                        _speakingText.value = null
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (utteranceId == activeUtteranceId) {
                        activeText = null
                        _isSpeaking.value = false
                        _speakingText.value = null
                    }
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
        _isAvailable.value = supported
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
        activeText = null
        activeUtteranceId = null
        if (ready) {
            engine.stop()
        }
        _isSpeaking.value = false
        _speakingText.value = null
    }

    private fun speakNow(text: String, slow: Boolean) {
        val utteranceId = "ll-${++utteranceCounter}"
        activeText = text
        activeUtteranceId = utteranceId
        engine.setSpeechRate(if (slow) 0.72f else 1.0f)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    private fun isSupported(languageStatus: Int): Boolean =
        languageStatus != TextToSpeech.LANG_MISSING_DATA &&
            languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
}
