import time
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import RPi.GPIO as GPIO

Broker = "192.168.1.252"

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

def on_connect(client, userdata, fkags, rc):
    print("Connected with result code "+str(rc))
    client.subscribe(sub_topic)
    
def on_message(client, userdata, msg):
    message = str(msg.payload)
    print(msg.topic+" "+message)
    display_sensehat(message)
    
def on_publish(mosq, obj, mid):
    print("mid: "+str())
    
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(Broker, 8333, 60)
client.loop_start()

while True:
    sensor_data = [read_temp(), read_humidity(), read_pressure()]
    client.publish("monto/solar/sensors", str(sensor_data))
    time.sleep(1*60)