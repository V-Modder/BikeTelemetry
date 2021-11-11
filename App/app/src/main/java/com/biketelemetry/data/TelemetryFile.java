package com.biketelemetry.data;

public class TelemetryFile {
    private String action;
    private String filename;

    public TelemetryFile() {}

    public TelemetryFile(String action, String filename) {
        this.action = action;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAction(){
        return action;
    }

    public void setAction(String action) { this.action = action; }
}
