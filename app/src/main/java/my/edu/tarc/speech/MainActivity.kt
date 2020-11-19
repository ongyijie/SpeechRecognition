package my.edu.tarc.speech

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SPEECH_INPUT = 100

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

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
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.mychannel)
            val descriptionText = getString(R.string.channeldesc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1234", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
                    var oledsc: String?         //Projector and screen
                    var lcdscr: String?         //Notify librarian that assistance is needed
                    var lcdtxt: String?
                    var lcdbkR: String?
                    var lcdbkG: String?
                    var lcdbkB: String?
                    var buzzer: String?
                    val string1 = result!![0]   //Declare strings to be used for comparison
                    val string2 = "switch on"
                    val string3 = "switch off"
                    val string4 = "help"
                    val string5 = "dismiss"

                    //Defining database
                    val database1 =
                        FirebaseDatabase.getInstance("https://bait2123-202010-03.firebaseio.com")
                    val database2 =
                        FirebaseDatabase.getInstance("https://solenoid-lock-f65e8.firebaseio.com")

                    //Write to common resources firebase
                    val data1 = database1.getReference("PI_03_CONTROL")

                    //Write to personal firebase
                    val data2 = database2.getReference("PI_03_CONTROL")

                    //Use when expression to look for matching condition and trigger action (switch on projector)
                    when (string1) {
                        string2 -> {     //Switch on
                            oledsc = "1"

                            //Update values to common resources firebase
                            data1.child("oledsc").setValue(oledsc)

                            //Update values to personal firebase
                            data2.child("oledsc").setValue(oledsc)
                        }
                        string3 -> {     //Switch off
                            oledsc = "0"

                            //Update values to common resources firebase
                            data1.child("oledsc").setValue(oledsc)

                            //Update values to personal firebase
                            data2.child("oledsc").setValue(oledsc)
                        }
                        string4 -> {     //Notify staff that assistance is needed
                            lcdscr = "1"
                            lcdtxt = "Room 1"
                            lcdbkR = "0"
                            lcdbkG = "100"
                            lcdbkB = "0"
                            //buzzer = "1"

                            //Update values to common resources firebase
                            data1.child("lcdscr").setValue(lcdscr)
                            data1.child("lcdtxt").setValue(lcdtxt)
                            data1.child("lcdbkR").setValue(lcdbkR)
                            data1.child("lcdbkG").setValue(lcdbkG)
                            data1.child("lcdbkB").setValue(lcdbkB)
                            //data1.child("buzzer").setValue(buzzer)

                            //Update values to personal firebase
                            data2.child("lcdscr").setValue(lcdscr)
                            data2.child("lcdtxt").setValue(lcdtxt)
                            data2.child("lcdbkR").setValue(lcdbkR)
                            data2.child("lcdbkG").setValue(lcdbkG)
                            data2.child("lcdbkB").setValue(lcdbkB)
                            //data2.child("buzzer").setValue(buzzer)

                            val builder = NotificationCompat.Builder(this, "1234")
                                .setSmallIcon(R.drawable.ic_baseline_help_center_24)
                                .setContentTitle("Smart Assistant")
                                .setContentText("Your request has been sent.")
                                .setStyle(NotificationCompat.BigTextStyle()
                                    .bigText("Your request has been sent, our librarian will approach you soon."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                            val notificationId = 5678
                            with(NotificationManagerCompat.from(this)) {
                                // notificationId is a unique int for each notification that you must define
                                notify(notificationId, builder.build())
                            }

                        }
                        string5 -> {     //Dismiss the notification
                            lcdscr = "1"
                            lcdtxt = "=APP IS RUNNING="
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
                    }
                }
            }
        }
    }
}
