#include <SoftwareSerial.h>
SoftwareSerial Bluetooth(10, 9); // RX, TX

const int button_pin=7; //Sets port 10 as RX and port 9 as TX

int button_val;     //Current button value and the previous one (either HIGH or LOW)

int pushed = 0;

int count = 0;

void setup() {
  pinMode(button_pin, INPUT_PULLUP);  //Set the button as an INPUT
  Bluetooth.begin(9600);       //Set up bluetooth serial communication
  Serial.begin(9600);          //Set up local serial communication (mainly used for testing)
  Serial.println("Waiting for command...");  //Testing to make sure the Arduino is running properly
}

void loop() {
  button_val = digitalRead(button_pin);  //Reads whether the button is pressed or not
  while (button_val == LOW) 
  {
    count = count + 1;
    if ((count > 50) && (pushed == 0)) 
    {  //Checks if the button has just been pressed and does not allow for the button to be held down
      pushed = 1;
      Serial.println("GO!");  //Prints on the local serial monitor for testing purposes
      Bluetooth.print(1);  //Sends a message (currently just the number 1)
      //!!Using Bluetooth.println causes issues!!
    }
    if (pushed == 1)
    {
      count = 0;
      pushed = 0;
    }
    button_val = digitalRead(button_pin);
    delay(1);
  }
  count = 0;
  pushed = 0;
  delay(1);
}
