package fr.enssat.babel_block.LERIN_DOYELLE

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.enssat.babel_block.LERIN_DOYELLE.tools.BlockService
import fr.enssat.babel_block.LERIN_DOYELLE.tools.SpeechToTextTool
import fr.enssat.babel_block.LERIN_DOYELLE.tools.TextToSpeechTool
import fr.enssat.babel_block.LERIN_DOYELLE.tools.TranslationTool
import java.util.*


class MainActivity : AppCompatActivity() {

    private val RecordAudioRequestCode = 1

    // Instantiation des handlers
    lateinit var speechToText: SpeechToTextTool
    lateinit var translator : TranslationTool
    lateinit var speaker: TextToSpeechTool

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity) // Mise en place du layout

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        val service = BlockService(this)


        // Instanciation des handlers
        speechToText = service.speechToText()
        translator = service.translator(Locale.FRENCH, Locale.ENGLISH)
        speaker = service.textToSpeech()

        // Récupération des boutons et des champs textuels du layout
        val recording = findViewById<Button>(R.id.record_button)
        val text_to_translate = findViewById<TextView>(R.id.word)
        val translated_text = findViewById<TextView>(R.id.translation)
        val translate = findViewById<Button>(R.id.translate_button)
        val ecoute = findViewById<Button>(R.id.listen_button)


        // Mise en place des actions à effectuer à l'appui sur le bouton "Recording" grâce à un listener
        recording.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d("Reco UI", "Button pressed")
                v.performClick()
                speechToText.start(object : SpeechToTextTool.Listener {
                    override fun onResult(text: String, isFinal: Boolean) {
                        if (isFinal) { text_to_translate.text = text}
                    }
                })
            } else if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Reco UI", "Button releases")
                speechToText.stop()
            }
            false
        }


        // Mise en place des actions à effectuer à l'appui sur le bouton "Translate" grâce à un listener
        translate.setOnClickListener {
            translator.translate(text_to_translate.text.toString()) { enText ->
                translated_text.text = enText
            }
        }


        // Mise en place des actions à effectuer à l'appui sur le bouton "Listening" grâce à un listener
        ecoute.setOnClickListener {
            val text = translated_text.text.toString()
            speaker.speak(text)
        }


    }

    override fun onDestroy() {
        translator.close()
        speechToText.close()
        super.onDestroy()
    }

    private fun checkPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }


}