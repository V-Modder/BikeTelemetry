#include <Storage.h>

const String Storage::FILE_PATH = "/test";
const String Storage::FILE_EXTENSION = ".bin";

bool Storage::begin()
{
    if (!SD.begin(CHIP_SELECT))
    {
        Serial.println("initialization failed. Things to check:");
        Serial.println("1. is a card inserted?");
        Serial.println("2. is your wiring correct?");
        Serial.println("3. did you change the chipSelect pin to match your shield or module?");
        Serial.println("Note: press reset or reopen this serial monitor after fixing your issue!");
        return false;
    }
    
    Serial.print("\n    SD card size: ");
    Serial.print(SD.totalBytes() / (1024 * 1024));
    Serial.println(" mb");

    SD.mkdir(FILE_PATH);
    String newFile = getNewFileName();
    Serial.print("\t");
    Serial.println(newFile);
    currentDataFile = SD.open(newFile, FILE_WRITE);
    if (!currentDataFile)
    {
        Serial.println("Cannot create file " + newFile);
        return false;
    }

    return true;
}

String Storage::getNewFileName()
{
    File dir = SD.open(FILE_PATH);
    int number = 0;
    File entry = dir.openNextFile();
    while (entry)
    {
        if (!entry.isDirectory())
        {
            int i = split(entry.name(), '_', 0).toInt();
            if (i > number)
            {
                number = i;
            }
        }
        entry.close();
    }
    number += 1;
    return FILE_PATH + "/" + formatLeadingZero(number, 3) + "_trac" + FILE_EXTENSION;
}

String Storage::split(String data, char separator, int index)
{
    int found = 0;
    int strIndex[] = {0, -1};
    int maxIndex = data.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++)
    {
        if (data.charAt(i) == separator || i == maxIndex)
        {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i + 1 : i;
        }
    }

    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

String Storage::formatLeadingZero(int number, int digits)
{
    String prefix = "";
    for (int i = digits; i > 0; i--)
    {
        int comp = pow(10, i - 1);
        if (number < comp)
        {
            prefix += "0";
        }
    }

    return prefix + String(number);
}

void Storage::writeToSd(Telemetry &telemetry)
{
    currentDataFile.write((const uint8_t *)&telemetry, sizeof(telemetry));
    currentDataFile.flush();
}

void Storage::removeFile(String filename)
{
    String filePath = FILE_PATH + "/" + filename;
    if (SD.exists(filePath) && filename != currentDataFile.name())
    {
        SD.remove(filePath);
    }
}

FileList Storage::getFileList()
{
    File dir = SD.open(FILE_PATH, FILE_READ);
    FileList list;
    list.size = getFileCount(dir);
    list.entries = (FileListEntry *)malloc(sizeof(FileListEntry) * list.size);
    File entry = dir.openNextFile(FILE_READ);
    int i = 0;
    while (entry)
    {
        if (!entry.isDirectory())
        {
            String name = String(entry.name());
            if (name.endsWith(".csv") && name != currentDataFile.name())
            {
                list.entries[i].name = (char *)name.substring(name.lastIndexOf('/') + 1).c_str();
                list.entries[i].size = entry.size();
                i++;
            }
        }
        entry.close();
        entry = dir.openNextFile(FILE_READ);
    }
    dir.close();

    return list;
}

int Storage::getFileCount(File &dire)
{
    File dir = SD.open(FILE_PATH, FILE_READ);
    File entry = dir.openNextFile(FILE_READ);

    int fileCount = 0;
    while (entry)
    {
        if (!entry.isDirectory())
        {
            String name = String(entry.name());
            if (name.endsWith(FILE_EXTENSION) && name != currentDataFile.name())
            {
                fileCount++;
            }
        }
        entry.close();
        entry = dir.openNextFile(FILE_READ);
    }
    dir.close();

    return fileCount;
}

File Storage::getFile(String name)
{
    String filePath = FILE_PATH + "/" + name;
    if (name != currentDataFile.name() && SD.exists(filePath))
    {
        return SD.open(filePath);
    }
    else
    {
        return File();
    }
}