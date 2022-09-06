#ifndef ACCELEROMETER_H_ /* Include guard */
#define ACCELEROMETER_H_

#include <SparkFunLSM6DS3.h>

struct Angles
{
    int Pitch;
    int Roll;
    int Temp;
};

class Accelerometer
{
public:
    bool begin();
    Angles getAngles();

private:    
    LSM6DS3 myIMU;
};

#endif