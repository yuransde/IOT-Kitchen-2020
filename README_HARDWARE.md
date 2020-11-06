# Hardware Report

## Scale
#### Bill of Materials
The costs shown below are what we found, but may be different as time has passed and these parts may be found in many different stores.  Already the price has changed on several of the sites.

The below table also does not take into account wires, breadboard, buttons, or other minor components.  Only one of each component in the table is required.

| Item | Cost | Vendor |
| ---- | ---- | ------ |
| Arduino Uno | $18.50 | [Arduino](https://store.arduino.cc/usa/arduino-uno-rev3) |
| HC-05 Bluetooth Module | $4.99 | [Newegg](https://www.newegg.com/p/2A7-00D0-00029?item=9SIAAZM4D78003&source=region&nm_mc=knc-googlemkp-pc&cm_mmc=knc-googlemkp-pc-_-pla-axe-tech-_-ec+-+test+%26+measurement-_-9SIAAZM4D78003&gclid=CjwKCAjw4pT1BRBUEiwAm5QuR1sU7ZQ3juAJBh7DWdgi9QQxvOhapDBGI-okFYsE2MlAPqds6tzFqhoCdBkQAvD_BwE&gclsrc=aw.ds) |
| 5kg Strain Gauge Load Cell & HX711 Load Cell Amplifier | $14.95 | [Amazon](https://www.amazon.com/Degraw-Load-Cell-HX711-Combo/dp/B075317R45/ref=sr_1_8?dchild=1&keywords=5kg+load+cell&qid=1587930005&sr=8-8) |
| 16x2 I2C LCD Display | $6.22 | [Amazon](https://www.amazon.com/ILS-Backlight-Display-Screen-Arduino/dp/B07PXV4YHL/ref=sr_1_30?crid=1AUB126ST113F&dchild=1&keywords=16x2+lcd+i2c&qid=1587930056&sprefix=16x2+LCD+%2Caps%2C149&sr=8-30) |
| Total: | $44.66 | -------- |


#### Circuit Schematic and Assembled Circuit
The circuit schematic can be found in pdf format here: [Scale Circuit Schematic](Scale_Circuit_Schematic.pdf)

 * Image of the scale from a top down view
![](./Images/Scale_top.jpg)
 * Image of the scale from a side view to see the load cell
![](./Images/Scale_side.jpg)


#### Code/How to run
All Arduino code was written and compiled using:

    Arduino 1.8.12

There are numerous Arduino scale codes in the folder Arduion_Test_Code, but only two are of importance and those are:

* Scale_Calibration - Calibration script needed to find the proper calibration factor for the scale.  The process is as follows:

    1. Setup the scale, run the Scale_Calibration code with the calibration_factor set to 1 on the Arduino and with 0 weight on the scale. Open the Serial monitor for the Arduino.
    2. Record the measurement with 0 weight on the scale (should be displayed in the serial monitor and should be 0)
    3. Place a known weight on the scale and record the measurement.
    4. As the load cell used works in a linear fashion and the y-intercept is set to 0, use the equation y=mx to solve for m, the calibration factor. y is the known weight and x is the weight value read.
    5. Finally replace the calibration factor in the all future scale code with this value.  You can also test and fine tune further using the Scale_Calibration code and using the + and - keys to modify the calibration factor.

* Final_scale_demo - Main script for the Scale with no buttons.  The scale must currently be attached to a laptop with Arduino installed and the Serial monitor open.  Then to tare the scale input the key "t" into the serial monitor and press enter.  Similarly, to send data to the app input a "q" into the serial monitor.  If the script gives an error saying that it is missing libraries follow the following steps:

    1. In the Arduino environment go to: tools -> Manage Libraries
    2. Search for HX711
    3. Install the library titled "HX711 Arduino Library"
    4. Go to the github linked in all of the scripts and linked to below for the Lcd library.  There are instructions on how to add it there.

#### Useful resources
* The library used for the LCD can be found [here](https://github.com/johnrickman/LiquidCrystal_I2C)
* HX711 Datasheet can be found [here](https://cdn.sparkfun.com/datasheets/Sensors/ForceFlex/hx711_english.pdf)
* Useful page with information on hooking up the load cell and HX711 to the Arduino, and information on different types of load cells can be found [here](https://learn.sparkfun.com/tutorials/load-cell-amplifier-hx711-breakout-hookup-guide?_ga=2.131138380.232106450.1587934292-1930200389.1587934292)

#### Other
Due to having no buttons the current scale setup is reliant on being attached to a computer for taring and sending data functionality.  However, if this were not the case, the Arduino from the scale could be plugged directly into a wall outlet as long as only 7-12V are applied.



## Temperature Sensor
#### Bill of Materials
The costs shown below are what we found, but may be different as time has passed and these parts may be found in many different stores.  Already the price has changed on several of the sites.

The below table also does not take into account wires, breadboard, or other minor components.  Only one of each component in the table is required.

| Item | Cost | Vendor |
| ---- | ---- | ------ |
| Arduino Micro | $20.70 | [Arduino](https://store.arduino.cc/usa/arduino-micro) |
| HC-05 Bluetooth Module | $4.99 | [Newegg](https://www.newegg.com/p/2A7-00D0-00029?item=9SIAAZM4D78003&source=region&nm_mc=knc-googlemkp-pc&cm_mmc=knc-googlemkp-pc-_-pla-axe-tech-_-ec+-+test+%26+measurement-_-9SIAAZM4D78003&gclid=CjwKCAjw4pT1BRBUEiwAm5QuR1sU7ZQ3juAJBh7DWdgi9QQxvOhapDBGI-okFYsE2MlAPqds6tzFqhoCdBkQAvD_BwE&gclsrc=aw.ds) |
| Thermocouple Type-K Glass Braid Insulated Stainless Steel Tip | $9.95 | [Adafruit](https://www.adafruit.com/product/3245) |
| MAX31856 Universal Thermocouple Amplifier | $17.50 | [Adafruit](https://www.adafruit.com/product/3263) |
| 16x2 I2C LCD Display | $6.22 | [Amazon](https://www.amazon.com/ILS-Backlight-Display-Screen-Arduino/dp/B07PXV4YHL/ref=sr_1_30?crid=1AUB126ST113F&dchild=1&keywords=16x2+lcd+i2c&qid=1587930056&sprefix=16x2+LCD+%2Caps%2C149&sr=8-30) |
| Total: | $59.36 | -------- |


#### Circuit Schematic and Assembled Circuit
The circuit schematic can be found in pdf format here: [Temperature Sensor Circuit Schematic](./Images/Temp_Circuit_Schematic.pdf)

 * Image of the temperature sensor from a top down view
![](./Images/Temperature_sensor.jpg)


#### Code/How to run
All Arduino code was written and compiled using:

    Arduino 1.8.12
    
There are a couple Arduino temperature sensor codes in the folder Arduion_Test_Code, but only one of them is of importance:

* Temp_Micro - Main script for the temperature sensor.  The arduino micro must have the code on it and then the device can be plugged directly into a wall outlet.  Once plugged in the LCD will display the currently measured temperature at the tip of the stainless steel thermocouple cap.  Currently, the script has it so that data is transmitted at a rate of 1 Hz and only once the temperature measured increases above 20 Celsius.  This can be changed by modifying the delay at the end of the script or the line "if (temp > 20).  For first use some libraries are required and can be installed by:

    1. In the Arduino environment go to: tools -> Manage Libraries
    2. Search for MAX31856
    3. Install the library titled "Adafruit MAX31856 library"
    4. Go to the github linked in all of the scripts and linked to below for the Lcd library.  There are instructions on how to add it there.

#### Useful resources
* The library used for the LCD can be found [here](https://github.com/johnrickman/LiquidCrystal_I2C)
* MAX31856 Datasheet can be found [here](https://datasheets.maximintegrated.com/en/ds/MAX31856.pdf)
* In depth setup and use of the MAX31856 with a thermocouple can be found [here](https://learn.sparkfun.com/tutorials/load-cell-amplifier-hx711-breakout-hookup-guide?_ga=2.131138380.232106450.1587934292-1930200389.1587934292)

#### Other
Once the relevant code is on the Arduino Micro the temperature sensor can be plugged directly into a wall outlet that provides 7-12V.

The hardware of the temperature sensor is sensitive to both heat and liquid and so should NOT be put into the oven.  Instead, place the hardware next to the oven and snake the thermocouple wire into the oven and potentially into the food item.
