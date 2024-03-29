
#ifndef BIKETELEMETRY_H_ /* Include guard */
#define BIKETELEMETRY_H_

#include <TinyGPSPlus.h>
#include <SoftwareSerial.h>
#include <BikeTelemetryStatus.h>
#include <Accelerometer.h>

struct __attribute__((packed)) Telemetry
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
    char roll;
    char pitch;
    char temperature;
};

class BikeTelemetry : public ITelemetryStatusReportable
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
    BikeTelemetryStatus status;
    Accelerometer accelerometer;
};

#endif // BIKETELEMETRY_H_