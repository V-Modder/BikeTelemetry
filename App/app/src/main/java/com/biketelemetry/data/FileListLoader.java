package com.biketelemetry.data;

import android.content.Context;

import java.util.List;

public interface FileListLoader {

    List<TelemetryFileListEntry> getFiles(Context context);
}
