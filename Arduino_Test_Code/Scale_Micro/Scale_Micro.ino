#include <SoftwareSerial.h>
//Scale
#include <HX711.h>
//LCD
#include <Wire.h>               //https://bitbucket.org/fmalpartida/new-liquidcrystal/downloads/
#include <LCD.h>
#include <LiquidCrystal_I2C.h>

//Initialize the bluetooth pins
SoftwareSerial Bluetooth(10, 9); // RX, TX

LiquidCrystal_I2C lcd(0x27,2,1,0,4,5,6,7);  //Set-up LCD screen

#define DOUT  3  //Set pins for scale amplifier
#define CLK  2

// Button set-up
int TarePin = 6;     //Button for taring
int SendPin = 5;      //Button for sending to bluetooth
int TareVal;
int SendVal;
int prevTare = HIGH;    //High means button not pressed
int prevSend = HIGH;

//Scale set-up
HX711 scale;  //Create scale object
float calibration_factor = -396;    //Set calibration factor (the m in y=mx+b [slope])   -25000 first test calibration_factor
float weight = 0;     //Initialize weight variables
float prevWeight = 0;

bool first;

void setup() {
   // Define pin for Send and Tare as inputs and activate the internal pull-up resistors
   pinMode(TarePin, INPUT_PULLUP);
   pinMode(SendPin, INPUT_PULLUP);

  
  Serial.begin(9600);
  Bluetooth.begin(9600);       //Set up bluetooth serial communication
  Serial.println("Weight test sketch");
  Serial.println("Remove all weight from scale");

  first = true;

  lcd.begin(16,2);                      //Set up the LCD screen
  lcd.setBacklightPin(3,POSITIVE);
  lcd.setBacklight(HIGH);
  lcd.home();
  lcd.print("IoT Kitchen       ");  
  lcd.setCursor(0,1);
  lcd.print("Please Wait...      ");

  scale.begin(DOUT, CLK);               //Set up scale object, calibrate, tare, and begin
  scale.set_scale(calibration_factor);
  scale.tare(); //Reset the scale to 0

  scale.tare();
  lcd.setCursor(0,1);
  lcd.print("Ready for weight       ");
  Serial.println("Ready for weight     ");
}

void loop() {
  weight = scale.get_units(2);      //Get the current weight
  
  if (abs(weight-prevWeight) >= .2) {     //If weight has changed, update the LCD     (Meant to prevent values switching back and forth)
    if (first) {                          //First time weight is added, print Weight on the LCD screen
      lcd.setCursor (0,0);                //Go to start of 1st line
      lcd.print("Weight: ");   
      first = false;
    }
    lcd.setCursor (8,0);        // go to 1st line after Weight
    lcd.print((int) round(weight));
    lcd.print(" g          ");
  }

     // Define pin #12 as input and activate the internal pull-up resistor
  TareVal = digitalRead(TarePin);
  SendVal = digitalRead(SendPin);
  if(Serial.available())        //Bluetooth functionality and scale taring
  {
    if (SendVal == LOW && prevSend == HIGH)         //Send to bluetooth device weight rounded to the nearest gram when button is initially pressed down
    {
      Serial.print((int) round(weight), 1);
      Serial.println();
      Bluetooth.print("w ");
      Bluetooth.print((int) round(weight));
      Bluetooth.print(" grams \r\n");
    }
    else if (TareVal == LOW && prevTare == HIGH)     //Tare the scale when button is initially pressed down
    {
      lcd.setCursor(0,1);
      lcd.print("Taring                 ");
      first = true;
      scale.tare();
      lcd.setCursor (0,1);
      lcd.print("Weight: ");
      lcd.print("0 g           ");
      Serial.print("taring");
      Serial.println();
    }
  }
  prevWeight = weight;
  prevTare = TareVal;
  prevSend = SendVal;

  delay(10);    //Just to give it some downtime
}
