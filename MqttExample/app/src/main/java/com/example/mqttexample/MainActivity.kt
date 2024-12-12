import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mqttexample.R

class MainActivity : AppCompatActivity() {

    private lateinit var mqttHelper: MqttHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mqttHelper = MqttHelper(this)
        mqttHelper.subscribeToTopic("test/topic") // Subscribe to a topic

        // Publish a message
        mqttHelper.publishMessage("test/topic", "hey saurav here from goa!")

        // Display a toast to indicate publishing
        Toast.makeText(this, "Message published!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttHelper.disconnect() // Disconnect when the activity is destroyed
    }
}
