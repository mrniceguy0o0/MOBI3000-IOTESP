import time
import paho.mqtt.client as mqtt
# import paho.mqtt.publish as publish
import RPi.GPIO as GPIO

Broker = "broker.hivemq.com"

sub_topic = "testtopic/testopic12"

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(8, GPIO.OUT, initial=GPIO.LOW)
GPIO.setup(10, GPIO.OUT, initial=GPIO.LOW)

def redON():
    GPIO.output(8,GPIO.HIGH)
    
def redOFF():
    GPIO.output(8,GPIO.LOW)
    
def greenON():
    GPIO.output(10,GPIO.HIGH)
    
def greenOFF():
    GPIO.output(10,GPIO.LOW)
# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))
    
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe(sub_topic)

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    topic = str(msg.topic)
    message = str(msg.payload.decode("utf-8"))
    print(topic+" "+message)
    
# def on_publish(mosq, obj, mid):
#     print("mid: "+str())
    
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

val = on_message

client.connect(Broker, 1883, 60)

client.loop_start()

# while True:
if val == 0:
    redOFF()
    greenON()
else:
    redON()
    greenOFF()
        
#     sensor_data = [read_temp(), read_humidity(), read_pressure()]
#     client.publish("monto/solar/sensors", str(sensor_data))
    time.sleep(20)