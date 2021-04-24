//Include libraries for SPI, Ethernet, and PubSub/MQTT connection
#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>

// Function prototypes
void subscribeReceive(char* topic, byte* payload, unsigned int length);
 
// Set MAC and IP address of Arduino/Client
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFF, 0xED };
IPAddress ip(192, 168, 192, 160);
 
// Sets the server to the public mqhive broker
const char* server = "broker.hivemq.com"; 

//declare some variables
int qos = 2;
String MQTTTopic = "MOBI3000/Final";
int led2Pin = 6;
int led1Pin = 3;
int led1State = 0;
int led2State = 0;


// Ethernet and MQTT related objects
EthernetClient ethClient;
PubSubClient mqttClient(ethClient);

void setup()
{
  Serial.begin(9600);
  
  // Start the ethernet connection
  Ethernet.begin(mac, ip);              
  
  // Give Ethernet some time to boot up
  delay(3000);                          
 
  // Set the MQTT server to the server variable from earlier
  mqttClient.setServer(server, 1883);   
 
  // Sets MQTT client name of the arduino
  if (mqttClient.connect("Arduino12390")) 
  {
    Serial.println("Connection has been established, well done"); //Prints a line in serial monitor to confirm connection was established
 
    // Establish subscribe event
    mqttClient.setCallback(subscribeReceive);
  } 
  else 
  {
    Serial.println("Looks like the server connection failed..."); // Serial print line if the client fails to connect to the broker
  }
}

void loop()
{
  // This is needed to keep the MQTT connection going
  mqttClient.loop();
 
  // Subscribe to the specific MQTT topic
  mqttClient.subscribe("MOBI3000/Final");
   
  // Set a delay to ensure nothing is happening too quickly.
  delay(1000);
}

//Function to make the embedded system "do something" i.e turn LEDs on
void subscribeReceive(char* topic, byte* payload, unsigned int length)
{  
  //Set pinmode on the Arduino to OUTPUT
  pinMode(6, OUTPUT); 
  pinMode(3, OUTPUT);

  //Code to handle printing topic string, and the message that is going through the broker's topic to monitor for bugs or lost messages/troubleshooting the embedded portion
  // Print the topic
  Serial.print("Topic: ");
  Serial.println(topic);
  //Declare message as a variable to hold the message from the mqtt broker
 String message;
  for(int i = 0; i < length; i ++)
  {
    message = message + (char)payload[i];
  }
  //Logic to turn LEDs ON/OFF depending on the message moving through the broker.
  if(message == "one" && led1State == 0){
Serial.println("LED 1 on");
digitalWrite(led1Pin, HIGH);
mqttClient.publish("MOBI3000/Final", "oneon");
led1State = 1;
}
else if(message == "one" && led1State == 1){
Serial.println("LED 1 off");
digitalWrite(led1Pin, LOW);
mqttClient.publish("MOBI3000/Final", "oneoff");
led1State = 0;
}
else if(message == "two" && led2State == 0){
Serial.println("LED 2 on");
digitalWrite(led2Pin, HIGH);
mqttClient.publish("MOBI3000/Final", "twoon");
led2State = 1;
}
else if(message == "two" && led2State == 1){
Serial.println("LED 2 off");
digitalWrite(led2Pin, LOW);
mqttClient.publish("MOBI3000/Final", "twooff");
led2State = 0;
}
  }
  // Print a new line in the serial monitor
//  Serial.println("");
