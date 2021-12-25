#include "Accelerometer.h"
#include <Arduino.h>
#include <Wire.h>

bool Accelerometer::begin()
{
    Serial.print("Setup Accelerometer START...");
    Wire.begin();
    Wire.beginTransmission(MPU_addr);
    Wire.write(MPU6050_RA_PWR_MGMT_1);
    Wire.write(0);
    Wire.endTransmission(true);

    int calibrations = 1000;
    Angles cal = {0, 0, 0};
    for (int i = 0; i < calibrations; i++)
    {
        Angles tmp = getRawValues();
        cal.X += tmp.X;
        cal.Y += tmp.Y;
        cal.Z += tmp.Z;
    }

    cal.X /= calibrations;
    cal.Y /= calibrations;
    cal.Z /= calibrations;
    int xAng = map(cal.X, MIN_VAL, MAX_VAL, -90, 90);
    int yAng = map(cal.Y, MIN_VAL, MAX_VAL, -90, 90);
    int zAng = map(cal.Z, MIN_VAL, MAX_VAL, -90, 90);

    int x = RAD_TO_DEG * (atan2(-yAng, -zAng) + PI);
    int y = RAD_TO_DEG * (atan2(-xAng, -zAng) + PI);
    int z = RAD_TO_DEG * (atan2(-yAng, -xAng) + PI);

    calibration = {x, y, z};
    Serial.println("End");

    return true;
}

Angles Accelerometer::getAngles()
{
    Angles raw_angles = getRawValues();

    int xAng = map(raw_angles.X, MIN_VAL, MAX_VAL, -90, 90);
    int yAng = map(raw_angles.Y, MIN_VAL, MAX_VAL, -90, 90);
    int zAng = map(raw_angles.Z, MIN_VAL, MAX_VAL, -90, 90);

    int x = RAD_TO_DEG * (atan2(-yAng, -zAng) + PI);
    int y = RAD_TO_DEG * (atan2(-xAng, -zAng) + PI);
    int z = RAD_TO_DEG * (atan2(-yAng, -xAng) + PI);

    Angles angles = {x, y, z};
    applyCalibration(angles);

    return angles;
}

Angles Accelerometer::getRawValues() {
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true);
  
  int acX = Wire.read() << 8 | Wire.read();
  int acY = Wire.read() << 8 | Wire.read();
  int acZ = Wire.read() << 8 | Wire.read();

  return {acX, acY, acZ};
}

void Accelerometer::applyCalibration(Angles& angles) {
  angles.X += calibration.X;
  angles.Y += calibration.Y;
  angles.Z += calibration.Z; 
}