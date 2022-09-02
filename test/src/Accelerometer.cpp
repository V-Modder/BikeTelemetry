#include "Accelerometer.h"
#include <Arduino.h>
#include <Wire.h>
#include <LSM6DS3.h>

bool Accelerometer::begin()
{
  Serial.print("Setup Accelerometer START...");
  //  Wire.begin();
  //  Wire.beginTransmission(MPU_addr);
  //  Wire.write(MPU6050_RA_PWR_MGMT_1);
  //  Wire.write(0);
  //  Wire.endTransmission(true);

  //  int calibrations = 1000;
  //  Angles cal = {0, 0, 0};
  //  for (int i = 0; i < calibrations; i++)
  //  {
  //      Angles tmp = getRawValues();
  //      cal.X += tmp.X;
  //      cal.Y += tmp.Y;
  //      cal.Z += tmp.Z;
  //  }

  //  cal.X /= calibrations;
  //  cal.Y /= calibrations;
  //  cal.Z /= calibrations;
  //  int xAng = map(cal.X, MIN_VAL, MAX_VAL, -90, 90);
  //  int yAng = map(cal.Y, MIN_VAL, MAX_VAL, -90, 90);
  //  int zAng = map(cal.Z, MIN_VAL, MAX_VAL, -90, 90);

  //  int x = RAD_TO_DEG * (atan2(-yAng, -zAng) + PI);
  //  int y = RAD_TO_DEG * (atan2(-xAng, -zAng) + PI);
  //  int z = RAD_TO_DEG * (atan2(-yAng, -xAng) + PI);

  //  calibration = {x, y, z};

  bool imuBegin = IMU.begin();
  Serial.println("End");
  return imuBegin;
}

Angles Accelerometer::getAngles()
{
  // Angles raw_angles = getRawValues();

  // int xAng = map(raw_angles.X, MIN_VAL, MAX_VAL, -90, 90);
  // int yAng = map(raw_angles.Y, MIN_VAL, MAX_VAL, -90, 90);
  // int zAng = map(raw_angles.Z, MIN_VAL, MAX_VAL, -90, 90);

  // int x = RAD_TO_DEG * (atan2(-yAng, -zAng) + PI);
  // int y = RAD_TO_DEG * (atan2(-xAng, -zAng) + PI);
  // int z = RAD_TO_DEG * (atan2(-yAng, -xAng) + PI);

  // Angles angles = {x, y, z};
  // applyCalibration(angles);

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

Angles Accelerometer::getRawValues()
{
  // Wire.beginTransmission(MPU_addr);
  // Wire.write(0x3B);
  // Wire.endTransmission(false);
  // Wire.requestFrom(MPU_addr, 14, 1);

  // int acX = Wire.read() << 8 | Wire.read();
  // int acY = Wire.read() << 8 | Wire.read();
  // int acZ = Wire.read() << 8 | Wire.read();
}

void Accelerometer::applyCalibration(Angles &angles)
{
  // angles.X += calibration.X;
  // angles.Y += calibration.Y;
  // angles.Z += calibration.Z;
}