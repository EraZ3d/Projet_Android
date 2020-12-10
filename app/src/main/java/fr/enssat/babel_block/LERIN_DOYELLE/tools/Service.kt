package fr.enssat.babel_block.LERIN_DOYELLE.tools

import android.content.Context
import fr.enssat.babel_block.LERIN_DOYELLE.tools.impl.SpeechRecognizerHandler
import fr.enssat.babel_block.LERIN_DOYELLE.tools.impl.TextToSpeechHandler
import fr.enssat.babel_block.LERIN_DOYELLE.tools.impl.TranslatorHandler
import java.util.Locale

interface TextToSpeechTool {
    fun speak(text: String)
    fun stop()
    fun close()
}

interface TranslationTool {
    fun translate(text: String, callback: (String) -> Unit)
    fun close()
}

interface SpeechToTextTool {
    interface Listener {
        fun onResult(text: String, isFinal: Boolean)
    }
    fun start(listener: Listener)
    fun stop()
    fun close()
}

class BlockService(val context: Context) {

    fun textToSpeech():TextToSpeechTool {
        val locale = Locale.getDefault()
        return TextToSpeechHandler(context.applicationContext, locale)
    }

    fun translator(from: Locale, to: Locale): TranslationTool =
        TranslatorHandler(context.applicationContext, from, to)

    fun speechToText(from: Locale = Locale.getDefault()): SpeechToTextTool =
        SpeechRecognizerHandler(context.applicationContext, from)
}
