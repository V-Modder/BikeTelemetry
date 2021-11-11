# 1 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino"
# 2 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 3 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 4 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 5 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2
# 6 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino" 2





static const int RXPin = 16, TXPin = 17;
static const int GPSBaud = 9600;
TinyGPSPlus gps;
double Start_LAT = 0.0; // Per calcolo Distanza percorsa
double Start_LNG = 0.0; // Per calcolo Distanza percorsa
bool Start_CalculateDistance = false; // Per calcolo Distanza percorsa
SoftwareSerial ss(RXPin, TXPin);

BluetoothSerial SerialBT;
String ENABLE_BT_TELEMENTRY = "enable_bt_telemetry";
String GET_FILE_LIST = "get_file_list";
String GET_FILE = "get_file";
String REMOVE_FILE = "remove_file";
bool SendBTTelemetry = false;

const int CHIP_SELECT = 5;
String FILE_PATH = "/test";
File DATA_FILE;

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
  Serial.println(getFileList());
  String newFile = getNewFileName();
  Serial.println(newFile);
  DATA_FILE = SD.open(newFile, "w");
  if(!DATA_FILE) {
    Serial.println("Cannot create file " + newFile);
    while(true);
  }
  Serial.print(((reinterpret_cast<const __FlashStringHelper *>(("MyTelemetry Project V 1.03 ")))));
  Serial.println(TinyGPSPlus::libraryVersion());
  Serial.println();
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
        String strMessage = getDisplayInfo();
        // comment the line below before deploy
        Serial.println(strMessage);
        writeToSd(strMessage);
        if(SendBTTelemetry) {
          writeToBt(strMessage);
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

String getDisplayInfo() {
  double distanceMeters = 0.0;

  /* ARRAY DEFINITION:

     01 - Latitude

     02 - N (Nord)

     03 - Longitude

     04 - E (East)

     05 - distance

     06 - month

     07 - day

     08 - year

     09 - hour

     10 - minute

     11 - second

     12 - millisecond

     13 - speed (Km/h)

     14 - altitude (m)

     15 - satellites (number of satellites)

     16 - hdop (number of satellites in use)

     17 - roll

     18 - pitch

     19 - Xg

     20 - Yg

     21 - Zg

  */
# 140 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino"
  String strMessage = "TELEMETRY|";

  if (gps.location.isValid()) {
    double latitude = gps.location.lat();
    double longitude = gps.location.lng();

    if (Start_LAT == 0.000000 && Start_LNG == 0.000000) {
      Start_CalculateDistance = true;
      Start_LAT = latitude;
      Start_LNG = longitude;
    }
    if (Start_CalculateDistance) {
      distanceMeters = gps.distanceBetween(latitude, longitude, Start_LAT, Start_LNG);
      Start_LAT = latitude;
      Start_LNG = longitude;
    }

    strMessage += String(latitude, 6); // 1
    strMessage += "|N|"; // 2
    strMessage += String(longitude, 6); // 3
    strMessage += "|E|"; // 4
    strMessage += String(distanceMeters, 1); // 5
  }
  else
  {
    strMessage = "INVALID"; // 1
    strMessage += "|N"; // 2
    strMessage += "|INVALID"; // 3
    strMessage += "|E"; // 4
    strMessage += "|INVALID"; // 5
  }

  /*

    Serial.print("Sentences that failed checksum=");

    Serial.println(gps.failedChecksum());

    // Testing overflow in SoftwareSerial is sometimes useful too.

    Serial.print("Soft Serial device overflowed? ");

    Serial.println(ss.overflow() ? "YES!" : "No");

    Serial.print("charsProcessed ");

    Serial.println(gps.charsProcessed());

    Serial.print("sentencesWithFix ");

    Serial.println(gps.sentencesWithFix());

    Serial.print("passedChecksum ");

    Serial.println(gps.passedChecksum());

  */
# 187 "d:\\workspace\\BikeTelemetry\\Controller\\bike_telemetry.ino"
  // Data
  if (gps.date.isValid()) {
    strMessage += gps.date.month(); // 6
    strMessage += "|";
    strMessage += gps.date.day(); // 7
    strMessage += "|";
    strMessage += gps.date.year(); // 8
    strMessage += "|";
    strMessage += gps.time.hour(); // 9
    strMessage += "|";
    strMessage += gps.time.minute(); // 10
    strMessage += "|";
    strMessage += gps.time.second(); // 11
    strMessage += "|";
    strMessage += gps.time.centisecond() * 100; // 12
    strMessage += "|";
  }
  else {
    strMessage += "INVALID"; // 6
    strMessage += "|";
    strMessage += "INVALID"; // 7
    strMessage += "|";
    strMessage += "INVALID"; // 8
    strMessage += "|";
    strMessage += "INVALID"; // 9
    strMessage += "|";
    strMessage += "INVALID"; // 10
    strMessage += "|";
    strMessage += "INVALID"; // 11
    strMessage += "|";
    strMessage += "INVALID"; // 12
    strMessage += "|";
  }

  // Speed
  strMessage += gps.speed.kmph(); // 13
  strMessage += "|";
  // Altitude
  double alt = gps.altitude.meters(); // 14
  strMessage += String(alt, 6);
  strMessage += "|";

  // Number of satellites in use (u32)
  strMessage += String(gps.satellites.value()); // 15
  strMessage += "|";
  // Number of satellites in use (u32)
  strMessage += String(gps.hdop.value()); // 16

  return strMessage;
}

void readBTCommands() {
  String line = "";
  while(SerialBT.available()) {
    char c = SerialBT.read();
    if(c != '\n' && c != '\r') {
      line += c;
    }
  }

  Serial.println("BT-Line: " + line);
  String cmd = split(line, '|', 0);
  String value = split(line, '|', 1);

  if(cmd == "") {
    cmd = line;
  }
  Serial.println("BT-Command: " + cmd);

  if(ENABLE_BT_TELEMENTRY == cmd) {
    SendBTTelemetry = value == "1";
  }
  else if(GET_FILE_LIST.equals(cmd)) {
    Serial.println("BT-Sending file-list");
    writeToBt(getFileList());
  }
  else if(GET_FILE.equals(cmd)) {
    Serial.println("BT-Sending file: " + value);
    sendFile(value);
  }
  else if(REMOVE_FILE.equals(cmd)) {
    Serial.println("BT-Removing file: " + value);
    removeFile(value);
  }
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

void writeToSd(String msg) {
  DATA_FILE.println(msg);
  DATA_FILE.flush();
}

void writeToBt(String msg) {
  SerialBT.println(msg);
}

String getFileList() {
  File dir = SD.open(FILE_PATH);
  String files = "FILE_LIST";
  File entry = dir.openNextFile();
  while(entry) {
    if (!entry.isDirectory()) {
      String name = String(entry.name());
      if(name.endsWith(".csv") && name != DATA_FILE.name()) {
        files += "|";
        files += name;
        files += ",";
        files += entry.size();
      }
    }
    entry.close();
    entry = dir.openNextFile();
  }

  return files;
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
