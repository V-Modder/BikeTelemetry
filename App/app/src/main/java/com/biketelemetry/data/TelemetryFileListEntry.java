package com.biketelemetry.data;

public class TelemetryFileListEntry {
    private String filename;
    private long size;

    public TelemetryFileListEntry() {}

    public TelemetryFileListEntry(String filename, long size) {
        this.filename = filename;
        setSize(size);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize(){
        return size;
    }

    public void setSize(long size) {
        if(size >= 0) {
            this.size = size;
        }
    }
}
