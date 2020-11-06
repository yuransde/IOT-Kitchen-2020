#include <SoftwareSerial.h>
SoftwareSerial Bluetooth(10, 9); // RX, TX

const int button_pin=7; //Sets port 10 as RX and port 9 as TX

void setup() {
  pinMode(button_pin, INPUT_PULLUP);  //Set the button as an INPUT
  Bluetooth.begin(9600);       //Set up bluetooth serial communication
  Serial.begin(9600);          //Set up local serial communication (mainly used for testing)
  Serial.println("Waiting for command...");  //Testing to make sure the Arduino is running properly
}

void loop() {

  if(Serial.available())
  {
    char temp = Serial.read();
    if(temp == '+' || temp == 'a')
      Bluetooth.print("1");
  }
  delay(1);
}
