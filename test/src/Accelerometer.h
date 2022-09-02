#ifndef ACCELEROMETER_H_ /* Include guard */
#define ACCELEROMETER_H_

struct Angles
{
    int Pitch;
    int Roll;
};

class Accelerometer
{
public:
    bool begin();
    Angles getAngles();

private:
    static const int MPU_addr = 0x68;
    static const int MIN_VAL = 265;
    static const int MAX_VAL = 402;
    static const int MPU6050_RA_PWR_MGMT_1 = 0x6B; // Reset
    
    Angles calibration;

    Angles getRawValues();
    void applyCalibration(Angles& angles);
};

#endif