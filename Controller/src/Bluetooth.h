#ifndef BLUETOOTH_H_ /* Include guard */
#define BLUETOOTH_H_
#include <Arduino.h>

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#include "BikeTelemetry.h"

#include <Storage.h>
#include <BluetoothSerial.h>

class Bluetooth
{
public:
    Bluetooth();
    bool begin(String applicationName, Storage &storage, ITelemetryStatusReportable &bikeTelemetry);
    bool inputAvailable();
    void handleCommands();
    bool isSendTelemetry();
    void writeTelemetry(Telemetry &telemetry);

private:
    static const char REQUEST_TAG_DEVICE_INFO = 2;
    static const char REQUEST_TAG_GET_FILE_LIST = 3;
    static const char REQUEST_TAG_GET_FILE = 4;
    static const char REQUEST_TAG_DELETE_FILE = 5;
    static const char REQUEST_TAG_ENABLE_TELEMETRY = 6;
    static const char REQUEST_TAG_GET_STATUS = 6;

    static const char RESPONSE_TAG_DEVICE_INFO = 2;
    static const char RESPONSE_TAG_GET_FILE_LIST_ENTRY = 3;
    static const char RESPONSE_TAG_GET_FILE_LIST_ENTRY_END = 4;
    static const char RESPONSE_TAG_GET_FILE = 5;
    static const char RESPONSE_TAG_GET_FILE_END = 6;
    static const char RESPONSE_TAG_TELEMETRY = 7;
    static const char RESPONSE_TAG_GET_STATUS = 8;
    static const char RESPONSE_TAG_ERROR = 255;

    bool sendTelemetry;
    BluetoothSerial bluetoothSerial;
    String applicationName;
    Storage storage;
    ITelemetryStatusReportable bikeTelemetry;

    String readString(int length);
    void writeFileList();
    void writeFileListEntry(FileListEntry &entry);
    void writeInt(int value);
    void writeShort(short value);
    void writeDouble(double value);
    void writeString(String str);
    void sendFile(String filename);
    void removeFile(String filename);
    void sendStatus();
};

#endif // BLUETOOTH_H_