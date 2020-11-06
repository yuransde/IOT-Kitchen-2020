#include <Wire.h>
#include <LCD.h>
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27,2,1,0,4,5,6,7);

unsigned long minutes,seconds;

void setup() {
  // put your setup code here, to run once:
  lcd.begin(16,2);

  lcd.setBacklightPin(3,POSITIVE);
  lcd.setBacklight(HIGH);
  lcd.home();  
  Serial.begin(9600);
  
}

void loop() {
  // put your main code here, to run repeatedly:

  seconds = (millis()/1000);
  //Serial.print("TEMPRATURE = ");
  Serial.print("test");
  //Serial.print("*C");
  Serial.print("  ");
  //Serial.print("TIME = ");
  //Serial.print(minutes);
  //Serial.print(".");
  Serial.print(seconds);
  //Serial.print("min");
  Serial.println();
  
  /*
  if (digitalRead(button_pin) == LOW) {
    tim = millis();
    lcd.clear();
  }*/

  lcd.home (); // set cursor to 0,0
  lcd.print("Test: ");
  lcd.print("C"); 
  lcd.setCursor (0,1);        // go to start of 2nd line
  lcd.print("Time: ");
  lcd.print((millis())/60000);
  lcd.print("min");
  
  delay(1000);
}
