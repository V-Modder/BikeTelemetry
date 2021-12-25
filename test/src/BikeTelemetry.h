
#ifndef BIKETELEMETRY_H_ /* Include guard */
#define BIKETELEMETRY_H_

#include <TinyGPSPlus.h>
#include <SoftwareSerial.h>
#include <Accelerometer.h>

struct Telemetry
{
    double latitude;
    double longitude;
    double altitude;
    double distance;
    double speed;
    short year;
    char month;
    char day;
    char hour;
    char minute;
    char second;
    short millisecond;
    char satellites;
    char hdop;
    int roll;
    int pitch;
    double xg;
    double yg;
    double zg;
};

class BikeTelemetry
{
public:
    BikeTelemetry();
    bool begin();
    bool isAvailable();
    Telemetry getTelemetry();

private:
    static const int GPS_RX_PIN = 16, GPS_TX_PIN = 17;
    static const int GPS_BAUD = 9600;
    TinyGPSPlus gps;
    double startLat;
    double startLng;
    bool startCalculateDistance;
    SoftwareSerial gpsSerial;
    Accelerometer accelerometer;
};

#endif // BIKETELEMETRY_H_