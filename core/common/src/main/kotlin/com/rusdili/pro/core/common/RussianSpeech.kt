package com.rusdili.pro.core.common

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class RussianSpeech @Inject constructor(
    @ApplicationContext context: Context,
    progressStore: UserProgressStore
) : TextToSpeech.OnInitListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val engine = TextToSpeech(context, this)
    private var ready = false
    private var pendingRequest: Triple<String, Boolean, LearningLanguage?>? = null
    @Volatile private var activeLanguage: LearningLanguage = LearningLanguage.RUSSIAN
    @Volatile private var appliedLanguage: LearningLanguage? = null
    @Volatile private var activeText: String? = null
    @Volatile private var activeUtteranceId: String? = null
    private var utteranceCounter = 0L

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speakingText = MutableStateFlow<String?>(null)
    val speakingText: StateFlow<String?> = _speakingText.asStateFlow()

    private val _isAvailable = MutableStateFlow(true)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    init {
        scope.launch {
            progressStore.selectedLanguage.collect { language ->
                activeLanguage = language
                if (ready) {
                    applyLocale(language)
                }
            }
        }
    }

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
        ready = true
        applyLocale(activeLanguage)
        pendingRequest?.let { (text, slow, language) ->
            pendingRequest = null
            speakNow(text, slow, language ?: activeLanguage)
        }
    }

    fun speak(text: String, slow: Boolean = false, language: LearningLanguage? = null) {
        val normalized = text.trim()
        if (normalized.isBlank()) return
        if (ready) {
            speakNow(normalized, slow, language ?: activeLanguage)
        } else {
            pendingRequest = Triple(normalized, slow, language)
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

    private fun speakNow(text: String, slow: Boolean, language: LearningLanguage) {
        if (appliedLanguage != language) {
            applyLocale(language)
        }
        val utteranceId = "ll-${++utteranceCounter}"
        activeText = text
        activeUtteranceId = utteranceId
        engine.setSpeechRate(if (slow) 0.72f else 1.0f)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    private fun applyLocale(language: LearningLanguage) {
        val primaryStatus = engine.setLanguage(Locale.forLanguageTag(language.ttsTag))
        val supported = if (isSupported(primaryStatus)) {
            true
        } else {
            isSupported(engine.setLanguage(Locale.forLanguageTag(language.fallbackTtsTag)))
        }
        appliedLanguage = language
        _isAvailable.value = supported
    }

    private fun isSupported(languageStatus: Int): Boolean =
        languageStatus != TextToSpeech.LANG_MISSING_DATA &&
            languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
}
