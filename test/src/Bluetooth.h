#ifndef BLUETOOTH_H_   /* Include guard */
#define BLUETOOTH_H_

#include "telemetry.h"

class Bluetooth {
    public:
        Bluetooth();
        bool inputAvailable();
        void readBTCommands();
        bool isSendTelemetry();

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

        String readString(int length)
        void writeFileList();
        void writeTelemetry(Telemetry &telemetry);
        void writeFileListEntry(String name, unsigned long size);
        void writeInt(int value);
        void writeShort(short value);
        void writeDouble(double value);
        void writeString(String str, int size);
        void sendFile(String filename);
        void removeFile(String filename);
};

#endif // BLUETOOTH_H_