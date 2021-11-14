package com.biketelemetry.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.biketelemetry.data.Telemetry;
import com.biketelemetry.data.TelemetryFileListEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BluetoothService {
    private static final String TELEMETRY_DEVICE_NAME = "Bike-Telemetry";

    public static final byte RESPONSE_TAG_GET_FILE_LIST = 1;
    public static final byte RESPONSE_TAG_GET_FILE = 2;
    public static final byte RESPONSE_TAG_TELEMETRY = 3;

    private File tmpDir;
    private Map<Byte, List<Handler>> handlers;

    public BluetoothService(File tmpDir) {
        this.tmpDir = tmpDir;
        this.handlers = new HashMap<>();
        handlers.put(RESPONSE_TAG_GET_FILE_LIST, new ArrayList<>());
        handlers.put(RESPONSE_TAG_GET_FILE, new ArrayList<>());
        handlers.put(RESPONSE_TAG_TELEMETRY, new ArrayList<>());
    }

    public void addHandler(byte responseTag, Handler handler) {
        handlers.get(responseTag).add(responseTag, handler);
    }

    public void removeHandler(Handler handler) {
        handlers.entrySet()
                .stream()
                .forEach(entry -> entry.getValue().remove(handler));
    }

    public void test(String command) throws Exception {
        Optional<BluetoothDevice> telemetryDevice = getTelemetryDevice();

        if(!telemetryDevice.isPresent()) {
            throw new Exception("Please connect Bluetooth Telemetry");
        }

        BluetoothSocket socket = telemetryDevice.get().createRfcommSocketToServiceRecord(telemetryDevice.get().getUuids()[0].getUuid());
        if(!socket.isConnected()) {
            socket.connect();
        }

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        outputStream.write(command.getBytes(StandardCharsets.UTF_8));
//https://developer.android.com/guide/topics/connectivity/bluetooth/transfer-data
        if(inputStream.available() > 0) {
            int messageType = inputStream.read();
            Object response = null;
            switch (messageType) {
                case RESPONSE_TAG_GET_FILE_LIST:
                    response = receiveFileList(inputStream);
                    break;
                case RESPONSE_TAG_GET_FILE:
                    response = receiveFile(inputStream);
                    break;
                case RESPONSE_TAG_TELEMETRY:
                    response = receiveTelemetry();
                    break;
            }

            if(response != null) {
                handleData(messageType, response);
            }
        }
    }

    @NonNull
    private TelemetryFileListEntry receiveFileList(InputStream inputStream) throws IOException {
        byte[] dummy =new byte[4];
        inputStream.read(dummy);
        int size = ByteBuffer.wrap(dummy).getInt();
        StringBuilder sb = new StringBuilder();
        int ch = 0;
        while ((ch = inputStream.read()) != -1) {
            sb.append((char) ch);
        }

        return new TelemetryFileListEntry(sb.toString(), size);
    }

    @NonNull
    private File receiveFile(InputStream inputStream) throws IOException {
        File tmpFile = File.createTempFile("download", ".csv", tmpDir);
        try(FileOutputStream fileOutputStream = new FileOutputStream(tmpFile)) {
            byte[] dummy = new byte[500];
            int ch = 0;
            while ((ch = inputStream.read(dummy)) != -1) {
                fileOutputStream.write(dummy, 0, ch);
            }
        }

        return tmpFile;
    }

    @NonNull
    private Telemetry receiveTelemetry() {
        return new Telemetry();
    }

    private Optional<BluetoothDevice> getTelemetryDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.getBondedDevices()
                .stream()
                .filter(dev -> TELEMETRY_DEVICE_NAME.equals(dev.getName()))
                .findFirst();
    }

    private void handleData(int what, Object data) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = data;
        handlers.get(what).forEach(handle -> handle.handleMessage(msg));
    }
}
