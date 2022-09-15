package com.biketelemetry.data;

import android.os.Parcel;
import android.os.Parcelable;

public class TelemetryFile implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(filename);
    }

    public static final Parcelable.Creator<TelemetryFile> CREATOR
            = new Parcelable.Creator<TelemetryFile>() {
        public TelemetryFile createFromParcel(Parcel in) {
            return new TelemetryFile(in);
        }

        public TelemetryFile[] newArray(int size) {
            return new TelemetryFile[size];
        }
    };

    private TelemetryFile(Parcel in) {
        action = in.readString();
        filename = in.readString();
    }
}
