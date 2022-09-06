#include "BikeTelemetry.h"

#include <SoftwareSerial.h>
#include <BikeTelemetryStatus.h>

BikeTelemetry::BikeTelemetry()
{
    startLat = 0.0;
    startLng = 0.0;
    startCalculateDistance = false;
    status = NOT_STARTED;
}

bool BikeTelemetry::begin()
{
    Serial.println("Setup BikeTelemetry...");
    Serial.print("  Setup GPS START...");
    gpsSerial.begin(GPS_BAUD, SWSERIAL_8N1, GPS_RX_PIN, GPS_TX_PIN, false);
    Serial.println("  END");

    bool result = accelerometer.begin();
    if(result) {
        Serial.println("End");
    }
    return result;
}

bool BikeTelemetry::isAvailable()
{
    return gpsSerial.available() > 0 && gps.encode(gpsSerial.read());
}

Telemetry BikeTelemetry::getTelemetry()
{
    Telemetry telemetry;

    if (gps.location.isValid())
    {
        telemetry.latitude = gps.location.lat();
        telemetry.longitude = gps.location.lng();

        if (startLat == 0.000000 && startLng == 0.000000)
        {
            startCalculateDistance = true;
            startLat = telemetry.latitude;
            startLng = telemetry.longitude;
        }
        if (startCalculateDistance)
        {
            telemetry.distance = gps.distanceBetween(telemetry.latitude, telemetry.longitude, startLat, startLng);
            startLat = telemetry.latitude;
            startLng = telemetry.longitude;
        }
        status = RUNNING;
    }
    else {
        status = NO_SIGNAL;
    }

    if (gps.date.isValid())
    {
        telemetry.year = gps.date.year();
        telemetry.month = gps.date.month();
        telemetry.day = gps.date.day();
        telemetry.hour = gps.time.hour();
        telemetry.minute = gps.time.minute();
        telemetry.second = gps.time.second();
        telemetry.millisecond = gps.time.centisecond() * 100;
    }

    telemetry.speed = gps.speed.kmph();
    telemetry.altitude = gps.altitude.meters();
    telemetry.satellites = gps.satellites.value();
    telemetry.hdop = gps.hdop.value();

    Angles angles = accelerometer.getAngles();

    telemetry.roll = angles.Roll;
    telemetry.pitch = angles.Pitch;

    return telemetry;
}