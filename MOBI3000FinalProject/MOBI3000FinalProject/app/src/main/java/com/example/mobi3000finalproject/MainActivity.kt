package com.example.mobi3000finalproject

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage


class MainActivity : AppCompatActivity() {

    private lateinit var mqttAppintro: TextView
    private lateinit var mqttAppIntroInstructions: TextView
    private lateinit var mqttButtonInstructions: TextView
    private lateinit var led1StatusText: TextView
    private lateinit var controlButton1: Button
    private lateinit var led2StatusText: TextView
    private lateinit var controlButton2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)

        mqttAppintro = findViewById(R.id.mqtt_app_intro)
        mqttAppIntroInstructions = findViewById(R.id.mqtt_app_intructions)
        mqttButtonInstructions = findViewById(R.id.mqtt_button_intructions)
        controlButton1 = findViewById(R.id.control_button1)
        led1StatusText = findViewById(R.id.led1_status_text)
        controlButton2 = findViewById(R.id.control_button2)
        led2StatusText = findViewById(R.id.led2_status_text)

        // Call function to setup and configure MQTT client
        configureMQTTClient()
    }

    // Configure MQTT Client with Hive MQTT. If client is not public, username
    // and password must be used. They can be updated in the strings.xml file.
    private fun configureMQTTClient() {
        val clientId = MqttClient.generateClientId()
        val client = MqttAndroidClient(
            this.applicationContext, getString(R.string.mqtt_server),
            clientId
        )
        try {
            val options = MqttConnectOptions()
            options.userName = getString(R.string.user_name)
            options.password = getString(R.string.password).toCharArray()
            val token = client.connect(options)
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Log successful connectivity message
                    Log.d("TAG", getString(R.string.connect_success_message))
                    // Call functions to subscribe to MQTT topic, initialize the status listener,
                    // and setup control buttons.
                    subscribe(getString(R.string.mqtt_topic), client)
                    initStatusListener(getString(R.string.mqtt_topic), client)
                    configureControlButton1(client)
                    configureControlButton2(client)
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Log connectivity failure message
                    Log.d("TAG", getString(R.string.connect_failure_message))
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
            // Log exception error
            Log.d("TAG", getString(R.string.mqtt_exception_message))
        }
    }

    private fun subscribe(topic: String, client: MqttAndroidClient) {
        // Set MQTT quality of service for subscriptions
        val qos = 2
        try {
            client.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Log successful subscription
                    Log.d("TAG", getString(R.string.subscription_success_message))
                }override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    // Log failed subscription
                    Log.d("TAG", getString(R.string.subscription_failure_message))
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            // Log exception error
            Log.d("TAG", getString(R.string.mqtt_exception_message))
        }
    }

    // Function to initialize subscription status listener. When data is received the messageArrived
    // function is invoked and then the function to update the LED states.
    private fun initStatusListener(topic: String, client: MqttAndroidClient){
        // Set MQTT quality of service for incoming messages
        val qos = 2
        try {
            client.subscribe(topic, qos, object : IMqttMessageListener {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val receivedMessage = message.toString()
                    setLEDState(receivedMessage)
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            // Log exception error
            Log.d("TAG", getString(R.string.mqtt_exception_message))
        }
    }

    // Function to configure control button 1 with onClickListener that publishes LED state
    // to MQTT broker.
    private fun configureControlButton1(client: MqttAndroidClient){
        controlButton1.setOnClickListener {
            val topic = getString(R.string.mqtt_topic)
            val payload = getString(R.string.mqtt_led1_message)
            val encodedPayload: ByteArray
            try {
                encodedPayload = payload.toByteArray(charset(getString(R.string.char_set)))
                val message = MqttMessage(encodedPayload)
                client.publish(topic, message)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: MqttException) {
                e.printStackTrace()
                // Log exception error
                Log.d("TAG", getString(R.string.mqtt_exception_message))
            }
        }
    }

    // Function to configure control button 2 with onClickListener that publishes LED state
    // to MQTT broker.
    private fun configureControlButton2(client: MqttAndroidClient){
        controlButton2.setOnClickListener {
            val topic = getString(R.string.mqtt_topic)
            val payload = getString(R.string.mqtt_led2_message)
            val encodedPayload: ByteArray
            try {
                encodedPayload = payload.toByteArray(charset(getString(R.string.char_set)))
                val message = MqttMessage(encodedPayload)
                client.publish(topic, message)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                // Log encoding error
                Log.d("TAG", getString(R.string.mqtt_encoding_exception_message))
            } catch (e: MqttException) {
                // Log exception error
                Log.d("TAG", getString(R.string.mqtt_exception_message))
                e.printStackTrace()
            }
        }
    }

    // Function to update the states of the LEDs according to the received messages
    // being subscribed to. If statements check version as getColor required minimum API version 23
    private fun setLEDState(message: String){
        // Log message
        Log.d("TAG", message)
        when (message) {
            "oneon" -> {
                val newText = getString(R.string.led1_state_on)
                led1StatusText.text = newText
                // Check against minimum APi version
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    controlButton1.setBackgroundColor(getColor(R.color.led_on_color))
                }
            }
            "oneoff" -> {
                led1StatusText.text = getString(R.string.led1_state_off)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    controlButton1.setBackgroundColor(getColor(R.color.led_off_color))
                }
            }
            "twoon" -> {
                led2StatusText.text = getString(R.string.led2_state_on)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    controlButton2.setBackgroundColor(getColor(R.color.led_on_color))
                }
            }
            "twooff" -> {
                led2StatusText.text = getString(R.string.led2_state_off)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    controlButton2.setBackgroundColor(getColor(R.color.led_off_color))
                }
            }
        }
    }
}





