package com.example.mobi3000finalproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException


class MainActivity : AppCompatActivity() {

    private lateinit var mqttAppintro: TextView
    private lateinit var mqttAppIntroInstructions: TextView
    private lateinit var mqttButtonInstructions: TextView
    private lateinit var led1StatusOnTextView: TextView
    private lateinit var led1StatusOffTextView: TextView
    private lateinit var controlButton1: Button
    private lateinit var led2StatusOnTextView: TextView
    private lateinit var led2StatusOffTextView: TextView
    private lateinit var controlButton2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)

        mqttAppintro = findViewById(R.id.mqtt_app_intro)
        mqttAppIntroInstructions = findViewById(R.id.mqtt_app_intructions)
        mqttButtonInstructions = findViewById(R.id.mqtt_button_intructions)
        led1StatusOnTextView = findViewById(R.id.led1_status_on_textview)
        led1StatusOffTextView = findViewById(R.id.led1_status_off_textview)
        controlButton1 = findViewById(R.id.control_button1)
        led2StatusOnTextView = findViewById(R.id.led2_status_on_textview)
        led2StatusOffTextView = findViewById(R.id.led2_status_off_textview)
        controlButton2 = findViewById(R.id.control_button2)

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
            // Uncomment two line below and update username and password in strings.xml
            // if using private broker.
            //options.userName = getString(R.string.user_name)
            //options.password = getString(R.string.password).toCharArray()
            options.isAutomaticReconnect = true
            options.keepAliveInterval = 60
            val token = client.connect(options)
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Log successful connectivity message
                    Log.d("TAG", getString(R.string.connect_success_message))
                    // Call functions to subscribe to MQTT topic, initialize the status listener,
                    // and setup control buttons.
                    subscribe(getString(R.string.mqtt_topic), client)
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
        val qos = 0
        try {
            client.subscribe(topic, qos, null, object : IMqttActionListener {
                //}, object : IMqttMessageListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Log successful subscription
                    Log.d("TAG", getString(R.string.subscription_success_message))
                    initStatusListener(client)
                }
                override fun onFailure(
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

    // Function to initialize status listener. When data is received the messageArrived
    // function is invoked and then the function to update the LED states.
    private fun initStatusListener(client: MqttAndroidClient){
        try {
            client.setCallback(object: MqttCallbackExtended {
                override fun connectComplete(b:Boolean, s:String) {
                    // Log connection
                    Log.d("TAG", getString(R.string.mqtt_connection_complete_message))
                }
                override fun connectionLost(throwable:Throwable) {
                    // Log connection lost
                    Log.d("TAG", getString(R.string.mqtt_connection_lost_message))
                }
                override fun messageArrived(topic:String, mqttMessage: MqttMessage) {
                    setLEDState(mqttMessage.toString())
                }
                override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                    // Log delivery complete
                    Log.d("TAG", getString(R.string.mqtt_delivery_complete_message))
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
    // being subscribed to.
    private fun setLEDState(message: String){
        // Log message
        Log.d("TAG", message)
        when (message) {
            "oneon" -> {
                led1StatusOffTextView.visibility = View.INVISIBLE
                led1StatusOnTextView.visibility = View.VISIBLE
            }
            "oneoff" -> {
                led1StatusOnTextView.visibility = View.INVISIBLE
                led1StatusOffTextView.visibility = View.VISIBLE

            }
            "twoon" -> {
                led2StatusOffTextView.visibility = View.INVISIBLE
                led2StatusOnTextView.visibility = View.VISIBLE
            }
            "twooff" -> {
                led2StatusOnTextView.visibility = View.INVISIBLE
                led2StatusOffTextView.visibility = View.VISIBLE
            }
        }
    }
}





