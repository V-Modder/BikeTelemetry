#include <BikeTelemetry.h>
#include <Bluetooth.h>
#include <Storage.h>
#include "version.h"

BikeTelemetry bikeTelemetry;
Bluetooth bluetooth;
Storage storage;

String getApplicationName()
{
    return "BikeTelemetry V" + String(VERSION);
}

void setup()
{
    Serial.begin(115200);
    delay(2000);
    while(!Serial) {};
    Serial.println();

    if (!bikeTelemetry.begin())
    {
        while (true);
    }
    delay(3000);

    Serial.print("Setup Storage START...");
    if (!storage.begin())
    {
        while (true);
    }
    Serial.println("END");

    Serial.print("Setup Bluetooth START...");
    if (!bluetooth.begin(getApplicationName(), storage, bikeTelemetry))
    {
        while (true);
    }
    Serial.println("END");

    Serial.print(getApplicationName());
    Serial.println();
}

void loop()
{
    if (bikeTelemetry.isAvailable())
    {
        Telemetry telemetry = bikeTelemetry.getTelemetry();
        storage.writeToSd(telemetry);
        if (bluetooth.isSendTelemetry())
        {
            bluetooth.writeTelemetry(telemetry);
        }
    }
    else
    {
        delay(5000);
        Serial.println("wait position");
        Telemetry telemetry = bikeTelemetry.getTelemetry();
        Serial.print("Pitch: ");
        Serial.print(telemetry.pitch);
        Serial.print(", Roll: ");
        Serial.println(telemetry.roll);
    }

    if (bluetooth.inputAvailable())
    {
        bluetooth.handleCommands();
    }
}
