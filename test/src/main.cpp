#include <BikeTelemetry.h>
#include <Bluetooth.h>
#include <Storage.h>
#include "include/version.h"

BikeTelemetry bikeTelemetry;
Bluetooth bluetooth(getApplicationName());
Storage storage;

void setup() {
  // GPS Inizialize
  Serial.begin(115200);
  Serial.println("Setup GPS START ...");
  bikeTelemetry.begin();
  delay(3000);
  Serial.println("Setup GPS End");

  bluetooth.begin();

  if (!storage.begin()) {
    while (true);
  }

  Serial.print(getApplicationName());
  Serial.println();
}

String getApplicationName() {
  return "BikeTelemetry V" + String(VERSION);
}

void loop() {
  if (bikeTelemetry.isAvailable()) {
        Telemetry telemetry = bikeTelemetry.getTelemetry();  
        writeToSd(telemetry);
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
  }
}
