#include <BikeTelemetry.h>
#include <Bluetooth.h>
#include <Storage.h>
#include "version.h"

BikeTelemetry bikeTelemetry;
Bluetooth bluetooth;
Storage storage;

String getApplicationName() {
  return "BikeTelemetry V" + String(VERSION);
}

void setup() {
  // GPS Inizialize
  Serial.begin(115200);
  Serial.println("Setup GPS START ...");
  bikeTelemetry.begin();
  delay(3000);
  Serial.println("Setup GPS End");

  bluetooth.begin(getApplicationName(), storage);

  if (!storage.begin()) {
    while (true);
  }

  Serial.print(getApplicationName());
  Serial.println();
}

void loop() {
  if (bikeTelemetry.isAvailable()) {
    Telemetry telemetry = bikeTelemetry.getTelemetry();  
    storage.writeToSd(telemetry);
    if(bluetooth.isSendTelemetry()) {
      bluetooth.writeTelemetry(telemetry);
    }
  }
  else {
    delay(5000);
    Serial.println("wait position");
  }
  
  if (bluetooth.inputAvailable()) {
    bluetooth.handleCommands();
  }
}
