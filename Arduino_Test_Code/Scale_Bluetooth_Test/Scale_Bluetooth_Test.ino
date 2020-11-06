//Taken and modified from https://learn.sparkfun.com/tutorials/load-cell-amplifier-hx711-breakout-hookup-guide/all

#include <HX711.h>
#include <SoftwareSerial.h>

SoftwareSerial Bluetooth(10, 9); // RX, TX

#define DOUT  3
#define CLK  2

HX711 scale;

float calibration_factor = -396; //-7050 worked for my 440lb max scale setup
//-25000 first test calibration_factor

float weight = 0;

void setup() {
  Serial.begin(9600);
  Bluetooth.begin(9600);       //Set up bluetooth serial communication
  Serial.println("Weight test sketch");
  Serial.println("Remove all weight from scale");
  Serial.println("Press q to print weight");
  Serial.println("Press t to tare scale");

  scale.begin(DOUT, CLK);
  scale.set_scale(calibration_factor);
  scale.tare(); //Reset the scale to 0

  scale.tare();
  Serial.println("Ready for weight");
}

void loop() {

  //scale.set_scale(calibration_factor); //Adjust to this calibration factor

  /*Serial.print("Reading: ");
  Serial.print(scale.get_units(), 3);
  Serial.print(" kg"); //Change this to kg and re-adjust the calibration factor if you follow SI units like a sane person
  Serial.print(" calibration_factor: ");
  Serial.print(calibration_factor);
  Serial.println();*/

  if(Serial.available())
  {
    char temp = Serial.read();
    if(temp == '+' || temp == 'a')
      calibration_factor += 10;
    else if(temp == '-' || temp == 'z')
      calibration_factor -= 10;
    else if (temp == 'q')
    {
      weight = (int) round(scale.get_units(2));
      Serial.print((int) weight, 1);
      Serial.println();
      Bluetooth.print((int) weight);
      Bluetooth.print(" grams \r\n");
    }
    else if (temp == 'r')
    {
      weight = scale.get_units(2);
      Serial.print(weight);
      Serial.println();
      Bluetooth.print(weight);
      Bluetooth.print(" grams \r\n");
    }
    else if (temp == 't')
    {
      scale.tare();
      Serial.print("taring");
      Serial.println();
    }
  }
}
