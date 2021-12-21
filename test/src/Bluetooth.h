#ifndef BLUETOOTH_H_   /* Include guard */
#define BLUETOOTH_H_

/*#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif*/

#include "BikeTelemetry.h"

#include <Storage.h>
#include <BluetoothSerial.h>

class Bluetooth {
    public:
        Bluetooth();
        bool begin(String applicationName, Storage storage);
        bool inputAvailable();
        void handleCommands();
        bool isSendTelemetry();
        void writeTelemetry(Telemetry &telemetry);

    private:
        static const char REQUEST_TAG_DEVICE_INFO = 1;
        static const char REQUEST_TAG_GET_FILE_LIST = 2;
        static const char REQUEST_TAG_GET_FILE = 3;
        static const char REQUEST_TAG_DELETE_FILE = 4;
        static const char REQUEST_TAG_ENABLE_TELEMETRY = 5;

        static const char RESPONSE_TAG_DEVICE_INFO = 1;
        static const char RESPONSE_TAG_GET_FILE_LIST_ENTRY = 2;
        static const char RESPONSE_TAG_GET_FILE_LIST_ENTRY_END = 3;
        static const char RESPONSE_TAG_GET_FILE = 4;
        static const char RESPONSE_TAG_GET_FILE_END = 5;
        static const char RESPONSE_TAG_TELEMETRY = 6;
        static const char RESPONSE_TAG_ERROR = 255;

        bool sendTelemetry;
        BluetoothSerial bluetoothSerial;
        String applicationName;
        Storage storage;

        String readString(int length);
        void writeFileList();
        void writeFileListEntry(String name, unsigned long size);
        void writeInt(int value);
        void writeShort(short value);
        void writeDouble(double value);
        void writeString(String str, int size);
        void sendFile(String filename);
        void removeFile(String filename);
};

#endif // BLUETOOTH_H_