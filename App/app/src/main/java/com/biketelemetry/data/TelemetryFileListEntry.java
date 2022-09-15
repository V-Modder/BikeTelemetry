package com.biketelemetry.data;

import android.os.Parcel;
import android.os.Parcelable;

public class TelemetryFileListEntry implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filename);
        dest.writeLong(size);
    }

    public static final Parcelable.Creator<TelemetryFileListEntry> CREATOR
            = new Parcelable.Creator<TelemetryFileListEntry>() {
        public TelemetryFileListEntry createFromParcel(Parcel in) {
            return new TelemetryFileListEntry(in);
        }

        public TelemetryFileListEntry[] newArray(int size) {
            return new TelemetryFileListEntry[size];
        }
    };

    private TelemetryFileListEntry(Parcel in) {
        filename = in.readString();
        size = in.readLong();
    }
}
