package my.edu.tarc.speech

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        } catch (e: Exception) {
            //If error occurs, get error message
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    //Function that runs whenever sensor successfully receives voice input
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    //Get text from result
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    //Print user voice input to textView
                    val textViewResult: TextView = findViewById(R.id.textViewResult)
                    textViewResult.text = result!![0]

                    //Declaring variables
                    var lcdscr: String = ""        //Projector and screen
                    var lcdtxt: String = ""
                    var lcdbkR: String = ""        //Alarm system
                    var lcdbkG: String = ""
                    var lcdbkB: String = ""
                    var buzzer: String = ""
                    val string1 = result!![0]      //Declare strings for comparison
                    val string2 = "on"
                    val string3 = "off"
                    val string4 = "help"


                    //Defining database
                    val database1 = FirebaseDatabase.getInstance("https://bait2123-202010-03.firebaseio.com")
                    val database2 = FirebaseDatabase.getInstance("https://solenoid-lock-f65e8.firebaseio.com")

                    //Write to common resources firebase
                    val data1 = database1.getReference("PI_03_CONTROL")

                    //Write to personal firebase
                    val data2 = database2.getReference("PI_03_CONTROL")

                    //if-else statement to trigger action (switch on projector)
                    when {
                        string1.contains(string2) -> {     //Switch on
                            lcdscr = "1"
                            lcdtxt = "Projector is on!"
                            lcdbkR = "50"
                            lcdbkG = "50"
                            lcdbkB = "50"

                            //Update values to common resources firebase
                            data1.child("lcdscr").setValue(lcdscr)
                            data1.child("lcdtxt").setValue(lcdtxt)
                            data1.child("lcdbkR").setValue(lcdbkR)
                            data1.child("lcdbkG").setValue(lcdbkG)
                            data1.child("lcdbkB").setValue(lcdbkB)

                            //Update values to personal firebase
                            data2.child("lcdscr").setValue(lcdscr)
                            data2.child("lcdtxt").setValue(lcdtxt)
                            data2.child("lcdbkR").setValue(lcdbkR)
                            data2.child("lcdbkG").setValue(lcdbkG)
                            data2.child("lcdbkB").setValue(lcdbkB)
                        }
                        string1.contains(string3) -> {     //Switch off
                            lcdscr = "0"
                            lcdtxt = ""

                            //Update values to common resources firebase
                            data1.child("lcdscr").setValue(lcdscr)
                            data1.child("lcdtxt").setValue(lcdtxt)
                            data1.child("lcdbkR").setValue(lcdbkR)
                            data1.child("lcdbkG").setValue(lcdbkG)
                            data1.child("lcdbkB").setValue(lcdbkB)

                            //Update values to personal firebase
                            data2.child("lcdscr").setValue(lcdscr)
                            data2.child("lcdtxt").setValue(lcdtxt)
                            data2.child("lcdbkR").setValue(lcdbkR)
                            data2.child("lcdbkG").setValue(lcdbkG)
                            data2.child("lcdbkB").setValue(lcdbkB)
                        }
                        string1.contains(string4) -> {     //Trigger alarm
                            lcdscr = "1"
                            lcdbkR = "255"
                            lcdbkG = "0"
                            lcdbkB = "0"
                            //buzzer = "1"

                            //Update values to common resources firebase
                            data1.child("lcdscr").setValue(lcdscr)
                            data1.child("lcdtxt").setValue(lcdtxt)
                            data1.child("lcdbkR").setValue(lcdbkR)
                            data1.child("lcdbkG").setValue(lcdbkG)
                            data1.child("lcdbkB").setValue(lcdbkB)
                            data1.child("buzzer").setValue(buzzer)

                            //Update values to personal firebase
                            data2.child("lcdscr").setValue(lcdscr)
                            data2.child("lcdtxt").setValue(lcdtxt)
                            data2.child("lcdbkR").setValue(lcdbkR)
                            data2.child("lcdbkG").setValue(lcdbkG)
                            data2.child("lcdbkB").setValue(lcdbkB)
                            data2.child("buzzer").setValue(buzzer)
                        }
                    }
                }
            }
        }
    }
}
