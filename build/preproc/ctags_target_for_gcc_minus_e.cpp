# 1 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino"
# 2 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 3 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 4 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 5 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 6 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2

# 8 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2




//https://github.com/espressif/arduino-esp32/issues/1745

static const char REQUEST_TAG_DEVICE_INFO = 1;
static const char REQUEST_TAG_GET_FILE_LIST = 2;
static const char REQUEST_TAG_GET_FILE = 3;
static const char REQUEST_TAG_DELETE_FILE = 4;
static const char REQUEST_TAG_ENABLE_TELEMETRY = 5;

static const char RESPONSE_TAG_DEVICE_INFO = 1;
static const char RESPONSE_TAG_GET_FILE_LIST_ENTRY = 2;
static const char RESPONSE_TAG_GET_FILE_LIST_ENTRY_END = 3;
static const char RESPONSE_TAG_GET_FILE = 4;
static const char RESPONSE_TAG_TELEMETRY = 5;
static const char RESPONSE_TAG_ERROR = 255;

static const int RXPin = 16, TXPin = 17;
static const int GPSBaud = 9600;
TinyGPSPlus gps;
double Start_LAT = 0.0;
double Start_LNG = 0.0;
bool Start_CalculateDistance = false;
SoftwareSerial ss(RXPin, TXPin);

BluetoothSerial SerialBT;
bool SendBTTelemetry = false;

const int CHIP_SELECT = 5;
String FILE_PATH = "/test";
File DATA_FILE;

struct Telemetry {
  double latitude;
  double longitude;
  double altitude;
  double distance;
  double speed;
  short year;
  char month;
  char day;
  char hour;
  char minute;
  char second;
  short millisecond;
  char satellites;
  char hdop;
  int roll;
  int pitch;
  double xg;
  double yg;
  double zg;
};

void setup() {
  // GPS Inizialize
  Serial.begin(115200);
  Serial.println("Setup GPS START ...");
  ss.begin(GPSBaud);
  //SerialGPS.begin(9600, SERIAL_8N1, RXPin, TXPin);
  delay(3000);
  Serial.println("Setup GPS End");

  SerialBT.begin("Bike-Telemetry");

  if (!SD.begin(CHIP_SELECT)) {
    Serial.println("initialization failed. Things to check:");
    Serial.println("1. is a card inserted?");
    Serial.println("2. is your wiring correct?");
    Serial.println("3. did you change the chipSelect pin to match your shield or module?");
    Serial.println("Note: press reset or reopen this serial monitor after fixing your issue!");
    while (true);
  }

  Serial.print("SD card size: ");
  Serial.print(SD.totalBytes() / 1024);
  Serial.println("mb");
  SD.mkdir(FILE_PATH);
  String newFile = getNewFileName();
  Serial.println(newFile);
  DATA_FILE = SD.open(newFile, "w");
  if(!DATA_FILE) {
    Serial.println("Cannot create file " + newFile);
    while(true);
  }
  Serial.print(getApplicationName());
  Serial.println(TinyGPSPlus::libraryVersion());
  Serial.println();
}

String getApplicationName() {
  return "MyTelemetry Project V" + String("v0.1.7 - 2021-12-19 19:26:35.226550");
}

String getNewFileName() {
  File dir = SD.open(FILE_PATH);
  int number = 0;
  File entry = dir.openNextFile();
  while (entry) {
    if (!entry.isDirectory()) {
      int i = split(entry.name(), '_', 0).toInt();
      if(i > number) {
        number = i;
      }
    }
    entry.close();
  }
  number += 1;
  return FILE_PATH + "/" + formatLeadingZero(number, 3) + "_trac.csv";
}

void loop() {
  while (ss.available() > 0) {
    if (gps.encode(ss.read())) {
      //displayInfo();
      if (gps.location.isValid()) {
        Telemetry telemetry = getTelemetry();
        writeToSd(telemetry);
        if(SendBTTelemetry) {
          writeTelemetry(telemetry);
        }
      }
      else {
        delay(5000);
        Serial.println("wait position");
      }
      if (SerialBT.available()) {
        readBTCommands();
      }
    }
  }

  if (millis() > 5000 && gps.charsProcessed() < 10) {
    Serial.println(((reinterpret_cast<const __FlashStringHelper *>(("No GPS detected: check wiring.")))));
    while (true);
  }
}

Telemetry getTelemetry() {
  Telemetry telemetry;

  if (gps.location.isValid()) {
    telemetry.latitude = gps.location.lat();
    telemetry.longitude = gps.location.lng();

    if (Start_LAT == 0.000000 && Start_LNG == 0.000000) {
      Start_CalculateDistance = true;
      Start_LAT = telemetry.latitude;
      Start_LNG = telemetry.longitude;
    }
    if (Start_CalculateDistance) {
      telemetry.distance = gps.distanceBetween(telemetry.latitude, telemetry.longitude, Start_LAT, Start_LNG);
      Start_LAT = telemetry.latitude;
      Start_LNG = telemetry.longitude;
    }
  }

  if (gps.date.isValid()) {
    telemetry.year = gps.date.year();
    telemetry.month = gps.date.month();
    telemetry.day = gps.date.day();
    telemetry.hour = gps.time.hour();
    telemetry.minute = gps.time.minute();
    telemetry.second = gps.time.second();
    telemetry.millisecond = gps.time.centisecond() * 100;
  }

  telemetry.speed = gps.speed.kmph();
  telemetry.altitude = gps.altitude.meters();
  telemetry.satellites = gps.satellites.value();
  telemetry.hdop = gps.hdop.value();

  telemetry.roll = 0;
  telemetry.pitch = 0;
  telemetry.xg = 0.0;
  telemetry.yg = 0.0;
  telemetry.zg = 0.0;

  return telemetry;
}

void readBTCommands() {
  char cmd = SerialBT.read();

  Serial.println("BT-Command: " + cmd);

  if(REQUEST_TAG_DEVICE_INFO == cmd) {
    writeString(getApplicationName(), 25);
  }
  else if(REQUEST_TAG_GET_FILE_LIST == cmd) {
    Serial.println("BT-Sending file-list");
    writeFileList();
  }
  else if(REQUEST_TAG_GET_FILE == cmd) {
    String value = readString(25);
    Serial.println("BT-Sending file: " + value);
    sendFile(value);
  }
  else if(REQUEST_TAG_DELETE_FILE == cmd) {
    String value = readString(25);
    Serial.println("BT-Removing file: " + value);
    removeFile(value);
  }
  else if(REQUEST_TAG_ENABLE_TELEMETRY == cmd) {
    SendBTTelemetry = SerialBT.read() != 0;
  }
}

String readString(int length) {
  String str = "";
  int i = 0;
  while(SerialBT.available() && i >= length) {
    str += SerialBT.read();
    i++;
  }

  return str;
}

String split(String data, char separator, int index) {
 int found = 0;
 int strIndex[] = {0, -1};
 int maxIndex = data.length() - 1;

 for (int i = 0; i <= maxIndex && found <= index; i++) {
  if (data.charAt(i) == separator || i == maxIndex) {
   found++;
   strIndex[0] = strIndex[1] + 1;
   strIndex[1] = (i == maxIndex) ? i + 1 : i;
  }
 }

 return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

int stringSplit(String sInput, char cDelim, String sParams[], int iMaxParams) {
    int iParamCount = 0;
    int iPosDelim, iPosStart = 0;

    do {
      iPosDelim = sInput.indexOf(cDelim,iPosStart);
      if (iPosDelim > (iPosStart+1)) {
        sParams[iParamCount] = sInput.substring(iPosStart,iPosDelim-1);
        iParamCount++;
        if (iParamCount >= iMaxParams) {
          return (iParamCount);
        }
        iPosStart = iPosDelim + 1;
      }
    } while (iPosDelim >= 0);

    if (iParamCount < iMaxParams) {
      sParams[iParamCount] = sInput.substring(iPosStart);
      iParamCount++;
    }

    return iParamCount;
}

String formatLeadingZero(int number, int digits) {
  String prefix = "";
  for(int i = digits; i > 0; i--) {
    int comp = pow(10, i - 1);
    if(number < comp) {
      prefix += "0";
    }
  }

  return prefix + String(number);
}

void writeToSd(Telemetry &telemetry) {
  String line = String(telemetry.latitude, 6);
  line += "|" + String(telemetry.longitude, 6);
  line += "|" + String(telemetry.altitude, 6);
  line += "|" + String(telemetry.distance, 1);
  line += "|" + String(telemetry.speed);
  line += "|" + String(telemetry.month);
  line += "|" + String(telemetry.day);
  line += "|" + String(telemetry.year);
  line += "|" + String(telemetry.hour);
  line += "|" + String(telemetry.minute);
  line += "|" + String(telemetry.second);
  line += "|" + String(telemetry.millisecond);
  line += "|" + String(telemetry.satellites);
  line += "|" + String(telemetry.hdop);

  DATA_FILE.println(line);
  DATA_FILE.flush();
}

void writeToBt(String msg) {
  SerialBT.println(msg);
}

void writeFileList() {
  File dir = SD.open(FILE_PATH);
  File entry = dir.openNextFile();

  while(entry) {
    if (!entry.isDirectory()) {
      String name = String(entry.name());
      if(name.endsWith(".csv") && name != DATA_FILE.name()) {
        writeFileListEntry(name, entry.size());
      }
    }
    entry.close();
    entry = dir.openNextFile();
  }

  SerialBT.write(RESPONSE_TAG_GET_FILE_LIST_ENTRY_END);
}

void writeTelemetry(Telemetry &telemetry) {
  SerialBT.write(RESPONSE_TAG_TELEMETRY);
  writeDouble(telemetry.latitude);
  writeDouble(telemetry.longitude);
  writeDouble(telemetry.altitude);
  writeDouble(telemetry.distance);
  writeDouble(telemetry.speed);
  writeShort(telemetry.year);
  SerialBT.write(telemetry.month);
  SerialBT.write(telemetry.day);
  SerialBT.write(telemetry.hour);
  SerialBT.write(telemetry.minute);
  SerialBT.write(telemetry.second);
  writeShort(telemetry.millisecond);
  SerialBT.write(telemetry.satellites);
  SerialBT.write(telemetry.hdop);
  writeInt(telemetry.roll);
  writeInt(telemetry.pitch);
  writeDouble(telemetry.xg);
  writeDouble(telemetry.yg);
  writeDouble(telemetry.zg);
}

void writeFileListEntry(String name, unsigned long size) {
  SerialBT.write(RESPONSE_TAG_GET_FILE_LIST_ENTRY);
  writeInt(size);
  writeString(name, 25);
}

void writeInt(int value) {
  SerialBT.write((byte) value);
  SerialBT.write((byte) value >> 8);
  SerialBT.write((byte) value >> 16);
  SerialBT.write((byte) value >> 24);
}

void writeShort(short value) {
  SerialBT.write((byte) value);
  SerialBT.write((byte) value >> 8);
}

void writeDouble(double value) {
  uint8_t *bytePointer = (uint8_t *)&value;

  for(size_t index = 0; index < sizeof(double); index++) {
    uint8_t byte = bytePointer[index];

    SerialBT.write(byte);
  }
}

void writeString(String str, int size) {
  for(int i = 0; i < size; i++) {
    if(i < str.length()) {
      SerialBT.write(str[i]);
    }
    else {
      break;
    }
  }
  SerialBT.write('\0');
}

void sendFile(String filename) {
  if(filename != DATA_FILE.name() && SD.exists(FILE_PATH + "/" + filename)) {
    String filePath = FILE_PATH + "/" + filename;
    File file = SD.open(filePath);
    writeToBt("FILE_START|" + filename);
    while(file.available()) {
      String line = file.readStringUntil('\n');
      writeToBt(line);
    }
    writeToBt("FILE_END|" + filename);
    file.close();
  }
}

void removeFile(String filename) {
  String filePath = FILE_PATH + "/" + filename;
  if(SD.exists(filePath) && filename != DATA_FILE.name()) {
    SD.remove(filePath);
  }
}
