#include <HX711.h>
#include <SoftwareSerial.h>
#include <Wire.h>               //https://bitbucket.org/fmalpartida/new-liquidcrystal/downloads/
#include <LCD.h>
#include <LiquidCrystal_I2C.h>

SoftwareSerial Bluetooth(10, 9); // RX, TX

LiquidCrystal_I2C lcd(0x27,2,1,0,4,5,6,7);  //Set-up LCD screen

#define DOUT  3  //Set pins for scale amplifier
#define CLK  2

HX711 scale;  //Create scale object

float calibration_factor = -396; //-7050 worked for my 440lb max scale setup
//-25000 first test calibration_factor

float weight = 0;
float prevWeight = 0;

bool first;

void setup() {
  Serial.begin(9600);
  Bluetooth.begin(9600);       //Set up bluetooth serial communication
  Serial.println("Weight test sketch");
  Serial.println("Remove all weight from scale");
  Serial.println("Press q to print weight");
  Serial.println("Press t to tare scale");

  first = true;

  lcd.begin(16,2);                      //Set up the LCD screen
  //lcd.setBacklightPin(3,POSITIVE);
  lcd.setBacklight(HIGH);
  lcd.home();
  lcd.print("IoT Kitchen");  
  lcd.setCursor(0,1);
  lcd.print("Please Wait...");

  scale.begin(DOUT, CLK);               //Set up scale object, calibrate, tare, and begin
  scale.set_scale(calibration_factor);
  scale.tare(); //Reset the scale to 0

  scale.tare();
  lcd.setCursor(0,1);
  lcd.print("Ready for weight       ");
  Serial.println("Ready for weight");
}

void loop() {

  weight = scale.get_units(2);

  if (abs(weight-prevWeight) >= .2) {
    if (first) {
      lcd.setCursor (0,1);
      lcd.print("Weight: ");
      first = false;
    }
    lcd.setCursor (8,1);        // go to start of 2nd line
    lcd.print((int) round(weight));
    lcd.print(" g          ");
  }

  if(Serial.available())
  {
    char temp = Serial.read();
    if (temp == 'q')         //Print and send to bluetooth device weight rounded to the nearest gram
    {
      Serial.print((int) round(weight), 1);
      Serial.println();
      Bluetooth.print((int) round(weight));
      Bluetooth.print(" grams \r\n");
    }
    else if (temp == 'r')       //Print and send to bluetooth device weight rounded to the nearest 0.01 gram
    {
      Serial.print(weight);
      Serial.println();
      Bluetooth.print(weight);
      Bluetooth.print(" grams \r\n");
    }
    else if (temp == 't')     //Tare the scale
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
}
