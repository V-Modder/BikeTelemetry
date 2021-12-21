#ifndef STORAGE_H_   /* Include guard */
#define STORAGE_H_

#include <Arduino.h>
#include <SD.h>
#include <BikeTelemetry.h>

struct FileListEntry {
    String name;
    long size;
};

struct FileList {
    int size;
    FileListEntry* entries;
};

class Storage {
    public:
        bool begin();
        void writeToSd(Telemetry &telemetry);
        void removeFile(String filename);
        FileList getFileList();
        File getFile(String name);

    private:
        static const int CHIP_SELECT = 5;
        static const String FILE_PATH;
        File currentDataFile;

        String getNewFileName();
        String split(String data, char separator, int index);
        String formatLeadingZero(int number, int digits);
};

#endif