package com.biketelemetry.data;

import com.biketelemetry.data.Telemetry;
import com.biketelemetry.data.TelemetryFile;
import com.biketelemetry.data.TelemetryFileListEntry;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.Collectors;

public class CsvParser {
    public List<TelemetryFileListEntry> parseFileList(String fileList){
        return Arrays.stream(fileList.split("|"))
                .map(entry -> new TelemetryFileListEntry(entry.split(",")[0], parseLong(entry.split(",")[1])))
                .collect(Collectors.toList());
    }

    public TelemetryFile parseFile(String file) {
        String[] data = file.split("|");
        return new TelemetryFile(data[0], data[1]);
    }

    public Telemetry parseTelemetry(String telemetryLine){
        String[] data = telemetryLine.split("|");
        return new Telemetry(parseDouble(data[1]), parseDouble(data[2]), parseDouble(data[3]),
                parseInt(data[4]), parseInt(data[5]),parseInt(data[6]),
                parseInt(data[7]), parseInt(data[8]), parseInt(data[9]), parseInt(data[10]),
                parseDouble(data[11]), parseDouble(data[12]), parseInt(data[13]),
                parseInt(data[14]), parseDouble(data[15]),
                parseDouble(data[16]), parseDouble(data[17]));
    }

    private int parseInt(String str) {
        return OptionalInt.of(Integer.parseInt(str)).orElse(0);
    }

    private double parseDouble(String str) {
        return OptionalDouble.of(Double.parseDouble(str)).orElse(0);
    }

    private long parseLong(String str) {
        return OptionalLong.of(Long.parseLong(str)).orElse(0);
    }
}
