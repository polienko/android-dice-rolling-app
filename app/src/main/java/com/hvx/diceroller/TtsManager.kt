package com.hvx.diceroller

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale
import java.util.UUID

class TtsManager(context: Context) {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
    }

    fun speakResult(voiceEnabled: Boolean, result1: Int, result2: Int?, isTwoDice: Boolean) {
        if (!voiceEnabled || tts == null) return

        val textToSpeak = if (isTwoDice) {
            (result1 + (result2 ?: 0)).toString()
        } else {
            result1.toString()
        }

        val utteranceId = UUID.randomUUID().toString()
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun cleanup() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}