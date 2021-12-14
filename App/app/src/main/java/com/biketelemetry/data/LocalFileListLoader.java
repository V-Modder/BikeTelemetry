package com.biketelemetry.data;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import com.biketelemetry.service.BluetoothService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalFileListLoader {

    private Context context;

    public LocalFileListLoader(Context context) {
        this.context = context;
    }

    public List<TelemetryFileListEntry> loadFilelist() {
        return getFileList().stream()
            .map(LocalFileListLoader::createFile)
            .collect(Collectors.toList());
    }

    private List<File> getFileList() {
        return Arrays.stream(context.fileList())
                .map(File::new)
                .collect(Collectors.toList());
    }

    private static TelemetryFileListEntry createFile(File file) {
        return new TelemetryFileListEntry(file.getName(), file.length());
    }
}
