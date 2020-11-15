package my.edu.tarc.speech

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageViewMic: ImageView = findViewById(R.id.imageViewMic)
        //Button to show SpeechToText dialog
        imageViewMic.setOnClickListener {
            speak()
        }
    }

    //Function to trigger dialog
    private fun speak() {
        //Intent to show SpeechToText dialog
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi say something")

        try {
            //If there is no error, show the dialog
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e:Exception) {
            //If error occurs, get error message
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    //Function that runs whenever sensor successfully receives voice input
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    //Get text from result
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    //Print user voice input to textView
                    val textViewResult: TextView = findViewById(R.id.textViewResult)
                    textViewResult.text = result!![0]

                    //Declaring variables
                    var lcdscr: String = "0"        //Projector
                    var lcdtxt: String = ""
                    val string1 = result!![0]
                    val string2 = "on"
                    val string3 = "off"

                    //if-else statement to trigger actions (switch on projector)
                    if (string1.contains(string2)) {            //Switch on
                        lcdscr = "1"
                        lcdtxt = "****Welcome!****"
                    } else if (string1.contains(string3)) {     //Switch off
                        lcdscr = "0"
                        lcdtxt = ""
                    }

                    //Defining database
                    val database = FirebaseDatabase.getInstance("https://bait2123-202010-03.firebaseio.com")

                    //Write to common resources firebase
                    val data1 = database.getReference("PI_03_CONTROL/lcdscr")
                    data1.setValue(lcdscr)
                    val data2 = database.getReference("PI_03_CONTROL/lcdtxt")
                    data2.setValue(lcdtxt)
                }
            }
        }
    }
}