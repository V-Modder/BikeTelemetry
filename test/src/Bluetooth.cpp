#include "Bluetooth.h"

#include <Storage.h>

Bluetooth::Bluetooth()
{
    sendTelemetry = false;
}

bool Bluetooth::begin(String applicatoinName, Storage &storage) {
    this->applicationName = applicationName;
    this->storage = storage;
    return bluetoothSerial.begin("Bike-Telemetry");
}

bool Bluetooth::inputAvailable()
{
    return bluetoothSerial.available();
}

void Bluetooth::handleCommands()
{
    char cmd = bluetoothSerial.read();

    Serial.println("BT-Command: " + cmd);

    if (REQUEST_TAG_DEVICE_INFO == cmd)
    {
        writeString(applicationName, 25);
    }
    else if (REQUEST_TAG_GET_FILE_LIST == cmd)
    {
        Serial.println("BT-Sending file-list");
        writeFileList();
    }
    else if (REQUEST_TAG_GET_FILE == cmd)
    {
        String value = readString(25);
        Serial.println("BT-Sending file: " + value);
        sendFile(value);
    }
    else if (REQUEST_TAG_DELETE_FILE == cmd)
    {
        String value = readString(25);
        Serial.println("BT-Removing file: " + value);
        removeFile(value);
    }
    else if (REQUEST_TAG_ENABLE_TELEMETRY == cmd)
    {
        sendTelemetry = bluetoothSerial.read() != 0;
    }
}

bool Bluetooth::isSendTelemetry()
{
    return sendTelemetry;
}

String Bluetooth::readString(int length)
{
    String str = "";
    int i = 0;
    while (bluetoothSerial.available() && i >= length)
    {
        str += bluetoothSerial.read();
        i++;
    }

    return str;
}

void Bluetooth::writeFileList()
{
    FileListEntry* entries;
    int count = storage.getFileList(entries);

    for(int i = 0; i < count; i++) {
        writeFileListEntry(entries[i].name, entries[i].size);
    }

    bluetoothSerial.write(RESPONSE_TAG_GET_FILE_LIST_ENTRY_END);
}

void Bluetooth::writeTelemetry(Telemetry &telemetry)
{
    bluetoothSerial.write(RESPONSE_TAG_TELEMETRY);
    writeDouble(telemetry.latitude);
    writeDouble(telemetry.longitude);
    writeDouble(telemetry.altitude);
    writeDouble(telemetry.distance);
    writeDouble(telemetry.speed);
    writeShort(telemetry.year);
    bluetoothSerial.write(telemetry.month);
    bluetoothSerial.write(telemetry.day);
    bluetoothSerial.write(telemetry.hour);
    bluetoothSerial.write(telemetry.minute);
    bluetoothSerial.write(telemetry.second);
    writeShort(telemetry.millisecond);
    bluetoothSerial.write(telemetry.satellites);
    bluetoothSerial.write(telemetry.hdop);
    writeInt(telemetry.roll);
    writeInt(telemetry.pitch);
    writeDouble(telemetry.xg);
    writeDouble(telemetry.yg);
    writeDouble(telemetry.zg);
}

void Bluetooth::writeFileListEntry(String name, unsigned long size)
{
    bluetoothSerial.write(RESPONSE_TAG_GET_FILE_LIST_ENTRY);
    writeInt(size);
    writeString(name, 25);
}

void Bluetooth::writeInt(int value)
{
    bluetoothSerial.write((byte)value);
    bluetoothSerial.write((byte)value >> 8);
    bluetoothSerial.write((byte)value >> 16);
    bluetoothSerial.write((byte)value >> 24);
}

void Bluetooth::writeShort(short value)
{
    bluetoothSerial.write((byte)value);
    bluetoothSerial.write((byte)value >> 8);
}

void Bluetooth::writeDouble(double value)
{
    uint8_t *bytePointer = (uint8_t *)&value;

    for (size_t index = 0; index < sizeof(double); index++)
    {
        uint8_t byte = bytePointer[index];

        bluetoothSerial.write(byte);
    }
}

void Bluetooth::writeString(String str, int size)
{
    for (int i = 0; i < size; i++)
    {
        if (i < str.length())
        {
            bluetoothSerial.write(str[i]);
        }
        else
        {
            break;
        }
    }
    bluetoothSerial.write('\0');
}

void Bluetooth::sendFile(String filename)
{
    File file = storage.getFile(filename);
    if (file)
    {
        bluetoothSerial.write(RESPONSE_TAG_GET_FILE);
        while (file.available())
        {
            bluetoothSerial.write(file.read());
        }
        bluetoothSerial.write(RESPONSE_TAG_GET_FILE_END);
        file.close();
    }
    else {
        bluetoothSerial.write(RESPONSE_TAG_ERROR);
    }
}

void Bluetooth::removeFile(String filename)
{
    storage.removeFile(filename);
}