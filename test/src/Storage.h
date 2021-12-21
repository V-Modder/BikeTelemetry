#include <Arduino.h>
#include <SD.h>
#include <BikeTelemetry.h>

struct FileListEntry {
    String name;
    long size;
};

class Storage {
    public:
        Storage();
        bool begin();
        void writeToSd(Telemetry &telemetry);
        void removeFile(String filename);
        int getFileList(FileListEntry*);
        File getFile(String name);

    private:
        const int CHIP_SELECT = 5;
        const String FILE_PATH = "/test";
        File currentDataFile;

        String getNewFileName();
        String split(String data, char separator, int index);
        String formatLeadingZero(int number, int digits);
};