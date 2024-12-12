import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttHelper(context: Context) {

    private val client: MqttAndroidClient
    private val brokerUrl = "tcp://localhost:1883" // Replace with your broker IP
    private val clientId = "AndroidClient"

    init {
        client = MqttAndroidClient(context.applicationContext, brokerUrl, clientId)

        try {
            val token: IMqttToken = client.connect()
            token.setActionCallback(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // Connection succeeded
                    subscribeToTopic("your/subscribe/topic") // Subscribe to a topic after connecting
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    // Connection failed
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // Subscribe to a topic
    fun subscribeToTopic(topic: String) {
        try {
            client.subscribe(topic, 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // Subscription succeeded
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    // Subscription failed
                }
            })

            client.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    // Connection lost
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // Handle incoming messages
                    val payload = message?.payload?.let { String(it) }
                    // Do something with the payload, like updating the UI
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Delivery complete
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // Publish a message
    fun publishMessage(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage()
            mqttMessage.payload = message.toByteArray()
            client.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // Disconnect from MQTT broker
    fun disconnect() {
        try {
            if (client.isConnected) {
                client.disconnect()
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}
