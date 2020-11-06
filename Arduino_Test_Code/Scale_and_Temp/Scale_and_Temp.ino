#include <SoftwareSerial.h>
//Scale
#include <HX711.h>
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
Adafruit_MAX31856 maxthermo = Adafruit_MAX31856(5, 6, 7, 8);

LiquidCrystal_I2C lcd(0x27,2,1,0,4,5,6,7);  //Set-up LCD screen

#define DOUT  3  //Set pins for scale amplifier
#define CLK  2

HX711 scale;  //Create scale object

float calibration_factor = -396; //-7050 worked for my 440lb max scale setup
//-25000 first test calibration_factor

float weight = 0;     //Initialize weight variables
float prevWeight = 0;

int temp = 0;       //Initialize temp variables
int prevTemp = 0;

bool first;

void setup() {
  Serial.begin(9600);
  Bluetooth.begin(9600);       //Set up bluetooth serial communication
  Serial.println("Scale and Temperature sensor test sketch");
  Serial.println("Remove all weight from scale");
  Serial.println("Press q to print weight");
  Serial.println("Press t to tare scale");

  first = true;

  lcd.begin(16,2);                      //Set up the LCD screen
  lcd.setBacklightPin(3,POSITIVE);
  lcd.setBacklight(HIGH);
  lcd.home();
  lcd.print("IoT Kitchen      ");  
  lcd.setCursor(0,1);
  lcd.print("Please Wait...         ");

  maxthermo.begin();    //Start the temperature sensor

  maxthermo.setThermocoupleType(MAX31856_TCTYPE_K);   //Set the type of the temperature sensor

  scale.begin(DOUT, CLK);               //Set up scale object, calibrate, tare, and begin
  scale.set_scale(calibration_factor);
  scale.tare(); //Reset the scale to 0

  scale.tare();
  lcd.setCursor(0,0);
  lcd.print("Ready for weight       ");
  Serial.println("Ready for weight      ");
  lcd.setCursor (0,1);                
  lcd.print("Temp:              "); 
}

void loop() {

  weight = scale.get_units(2);      //Get the current weight
  temp = round(maxthermo.readThermocoupleTemperature());

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

  if (temp != prevTemp) {       //If temperature changes, update the LCD
    lcd.setCursor (6,0);
    lcd.print(temp);
    lcd.print(" C          ");
  }

  if(Serial.available())        //Bluetooth functionality and scale taring;    Not set up with temperature sensor
  {
    char inp = Serial.read();
    if (inp == 'q')         //Print and send to bluetooth device weight rounded to the nearest gram
    {
      Serial.print((int) round(weight), 1);
      Serial.println();
      Bluetooth.print((int) round(weight));
      Bluetooth.print(" grams \r\n");
    }
    else if (inp == 'r')       //Print and send to bluetooth device weight rounded to the nearest 0.01 gram
    {
      Serial.print(weight);

      Serial.println();
      Bluetooth.print(weight);
      Bluetooth.print(" grams \r\n");
    }
    else if (inp == 't')     //Tare the scale
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

  delay(10);    //Just to give it some downtime
}
