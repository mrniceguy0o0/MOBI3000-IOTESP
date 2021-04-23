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
  mqttClient.subscribe("testtopic/testopic12");
   
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
  if(message == "one")
  {
    //Turns LED ONE on, prints that it is on in the serial monitor, and publishes a message back to the broker that says "First LED is ON"
    //HIGH means that the pin is sending power, and LOW means it is not, OR in simpler terms, the first LED connected to that pin is ON if the pin is HIGH, and it is OFF if the pin is LOW
  Serial.println("LIGHT ONE IS ON");
    digitalWrite(6, HIGH);
    digitalWrite(3, LOW);
    boolean rc = mqttClient.publish("testtopic/testopic12", "First LED is ON");
    delay(100);
  }
  if(message == "four")
  {
   Serial.println("LIGHT FOUR IS ON");
    digitalWrite(6, LOW);
    digitalWrite(3, HIGH);
    boolean rc = mqttClient.publish("testtopic/testopic12", "Second LED is ON");
    delay(100);
  }
  // Print a new line in the serial monitor
  Serial.println("");
}
