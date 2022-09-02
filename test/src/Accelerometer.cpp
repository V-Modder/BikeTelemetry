#include "Accelerometer.h"
#include <Arduino.h>
#include <LSM6DS3.h>

bool Accelerometer::begin()
{
    Serial.print("Setup Accelerometer START...");
    bool imuBegin = IMU.begin();
    Serial.println("End");
    return imuBegin;
}

Angles Accelerometer::getAngles()
{
    float x, y, z, x1, y1;
    int roll, inclinacion;
    IMU.readAcceleration(x, y, z);
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

    return {roll, inclinacion};
}

void Accelerometer::applyCalibration(Angles &angles)
{
    // angles.X += calibration.X;
    // angles.Y += calibration.Y;
    // angles.Z += calibration.Z;
}