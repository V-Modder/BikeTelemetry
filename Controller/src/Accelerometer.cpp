#include "Accelerometer.h"
#include <Arduino.h>

bool Accelerometer::begin()
{
    Serial.print("  Setup Accelerometer START...");
    if(!Wire.begin()) {
        Serial.println("\n  Error initialize Wire");
        return false;
    }
    if(!myIMU.begin()) {
        Serial.println("\n  Error initialize IMU");
        return false;
    }
    
    Serial.println("End");
    return true;
}

Angles Accelerometer::getAngles()
{
    float x1, y1, inclinacion, roll;
    float x = myIMU.readFloatAccelX();
    float y = myIMU.readFloatAccelY();
    float z = myIMU.readFloatAccelZ();
    float temp = myIMU.readRawTemp();

    temp = (temp / 530.0) + 25;

    x1 = RAD_TO_DEG * (atan2(-y, -z) + PI);

    if (x1 > 180)
    {
        inclinacion = x1 - 360;
    }
    else
    {
        inclinacion = x1;
    }

    y1 = RAD_TO_DEG * (atan2(-x, -z) + PI);

    if (y1 > 180)
    {
        roll = y1 - 360;
    }
    else
    {
        roll = y1;
    }

    return {(int)roll, (int)inclinacion, (int)temp};
}
