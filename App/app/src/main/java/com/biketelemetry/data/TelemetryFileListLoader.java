package com.biketelemetry.data;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.biketelemetry.service.BluetoothService2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TelemetryFileListLoader extends Handler implements FileListLoader {
    private BluetoothService2 bluetoothService = new BluetoothService2();

    public List<TelemetryFileListEntry> getFiles(Context context) {
        String files = getFileList();
        return parseFiles(files);
    }

    private String getFileList() {
        try {
            return bluetoothService.send(BluetoothService2.COMMAND_GET_FILE_LIST, BluetoothService2.RESPONSE_TAG_GET_FILE_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {

    }

    private List<TelemetryFileListEntry> parseFiles(String files) {
        return Arrays.stream(files.split("\\|"))
            .skip(1)
            .map(TelemetryFileListLoader::createFile)
            .collect(Collectors.toList());
    }

    private static TelemetryFileListEntry createFile(String file) {
        String[] attributes = file.split(",");
        return new TelemetryFileListEntry(attributes[0], Long.parseLong(attributes[1]));
    }
}
