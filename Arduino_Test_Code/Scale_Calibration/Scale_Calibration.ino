//Taken and modified from https://learn.sparkfun.com/tutorials/load-cell-amplifier-hx711-breakout-hookup-guide/all

#include <HX711.h>

#define DOUT  A0
#define CLK  A1

HX711 scale;

float calibration_factor = -396; //-7050 worked for my 440lb max scale setup
//-25000 first test calibration_factor

float weight = 0;

void setup() {
  Serial.begin(9600);
  Serial.println("HX711 calibration sketch");
  Serial.println("Remove all weight from scale");
  Serial.println("After readings begin, place known weight on scale");
  Serial.println("Press + or a to increase calibration factor");
  Serial.println("Press - or z to decrease calibration factor");

  scale.begin(DOUT, CLK);
  scale.set_scale(calibration_factor);
  scale.tare(); //Reset the scale to 0

  long zero_factor = scale.read_average(); //Get a baseline reading
  Serial.print("Zero factor: "); //This can be used to remove the need to tare the scale. Useful in permanent scale projects.
  Serial.println(zero_factor);
  scale.tare();
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
      Serial.print(weight,1);
      Serial.println();
      Bluetooth.print(weight,1);
    }
    else if (temp == 'r')
    {
      weight = scale.get_units(2)
      Serial.print(weight));
      Serial.println();
      Bluetooth.print(weight);
    }
    else if (temp == 't')
    {
      scale.tare();
      Serial.print("taring");
      Serial.println();
    }
  }
}
