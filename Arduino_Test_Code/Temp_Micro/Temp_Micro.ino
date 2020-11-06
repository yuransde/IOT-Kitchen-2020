#include <SoftwareSerial.h>
//LCD
#include <Wire.h>               //https://bitbucket.org/fmalpartida/new-liquidcrystal/downloads/
#include <LCD.h>
#include <LiquidCrystal_I2C.h>
//Temp sensor
#include <Adafruit_MAX31856.h>

//Initialize the bluetooth pins
SoftwareSerial Bluetooth(10, 9); // RX, TX

//Initializing the temperature sensor amplifier pins
// Use software SPI: CS, DI, DO, CLK
Adafruit_MAX31856 maxthermo = Adafruit_MAX31856(5,6,7,8);

LiquidCrystal_I2C lcd(0x27,2,1,0,4,5,6,7);  //Set-up LCD screen

int temp = 0;       //Initialize temp variables
int prevTemp = 0;

void setup() {
  Serial.begin(9600);
  Bluetooth.begin(9600);       //Set up bluetooth serial communication

  lcd.begin(16,2);                      //Set up the LCD screen
  lcd.setBacklightPin(3,POSITIVE);
  lcd.setBacklight(HIGH);
  lcd.home();
  lcd.print("IoT Kitchen         ");  
  lcd.setCursor(0,1);
  lcd.print("Please Wait...       ");

  maxthermo.begin();    //Start the temperature sensor
  maxthermo.setThermocoupleType(MAX31856_TCTYPE_K);   //Set the type of the temperature sensor

  lcd.setCursor (0,1);                
  lcd.print("Temp:              "); 
}

void loop() {
  temp = round(maxthermo.readThermocoupleTemperature());

  if (temp != prevTemp) {       //If temperature changes, update the LCD
    lcd.setCursor (6,0);
    lcd.print(temp);
    lcd.print(" C          ");

    if (temp > 30) {          //If the temperature is above 30 C start sending data
      Bluetooth.print(temp);
      Bluetooth.print(" C \r\n");
    }
  }

  delay(1000);    //Save power and do not need that many data points
}
