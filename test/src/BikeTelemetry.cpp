#include "BikeTelemetry.h"

#include <SoftwareSerial.h>

BikeTelemetry::BikeTelemetry() {
    gpsSerial.begin(GPS_BAUD, SWSERIAL_8N1, GPS_RX_PIN, GPS_TX_PIN, false);
    startLat = 0.0;
    startLng = 0.0;
    startCalculateDistance = false;
}