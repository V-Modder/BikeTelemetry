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
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRYY = 1;
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRYY_END = 2;
    public static final byte RESPONSE_TAG_GET_FILE = 3;
    public static final byte RESPONSE_TAG_TELEMETRY = 4;
    public static final byte RESPONSE_TAG_ERROR = (byte) 255;

    private static final String TELEMETRY_DEVICE_NAME = "Bike-Telemetry";

    private static final byte REQUEST_TAG_GET_FILE_LIST = 1;
    private static final byte REQUEST_TAG_GET_FILE = 2;
    private static final byte REQUEST_TAG_ENABLE_TELEMETRY = 3;

    private File tmpDir;
    private Map<Byte, List<Handler>> handlers;
    private BluetoothSocket socket;
    private Thread worker;

    public BluetoothService(File tmpDir) {
        this.tmpDir = tmpDir;
        this.handlers = new HashMap<>();
        handlers.put(RESPONSE_TAG_GET_FILE_LIST_ENTRYY, new ArrayList<>());
        handlers.put(RESPONSE_TAG_GET_FILE, new ArrayList<>());
        handlers.put(RESPONSE_TAG_TELEMETRY, new ArrayList<>());
        worker = new Thread(() -> receiveResponse());
    }

    public void addHandler(byte responseTag, Handler handler) {
        handlers.get(responseTag).add(responseTag, handler);
    }

    public void removeHandler(Handler handler) {
        handlers.entrySet()
                .stream()
                .forEach(entry -> entry.getValue().remove(handler));
    }

    public void requestFileList() {
        new Thread(() -> executeRequest(REQUEST_TAG_GET_FILE_LIST, null)).start();
    }

    public void rquestFile(String filename) {
        byte[] param = filename.getBytes(StandardCharsets.UTF_8);
        new Thread(() -> executeRequest(REQUEST_TAG_GET_FILE, param)).start();
    }

    public void requestEnableTelemetry() {
        requestEnableTelemetry(true);
    }

    public void requestDisableTelemetry() {
        requestEnableTelemetry(false);
    }

    private void requestEnableTelemetry(boolean enable) {
        byte[] param = new byte[] { (byte) (enable ? 1 : 0) };
        new Thread(() -> executeRequest(REQUEST_TAG_ENABLE_TELEMETRY, param)).start();
    }

    private void executeRequest(byte command, byte[] param) {
        Optional<BluetoothDevice> telemetryDevice = getTelemetryDevice();

        if(!telemetryDevice.isPresent()) {
            return;
            //throw new Exception("Please connect Bluetooth Telemetry");
        }

        try {
            socket = telemetryDevice.get().createRfcommSocketToServiceRecord(telemetryDevice.get().getUuids()[0].getUuid());

            if(!socket.isConnected()) {
                socket.connect();
            }

            if(!worker.isAlive()) {
                worker.start();
            }

            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(command);
            if(param != null) {
                outputStream.write(param);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveResponse() {
        boolean received = false;
        while(!received && !worker.isInterrupted()) {
            try {
                InputStream inputStream = socket.getInputStream();
                while (inputStream.available() > 0) {
                    int messageType = inputStream.read();
                    switch (messageType) {
                        case RESPONSE_TAG_GET_FILE_LIST_ENTRYY:
                            handleData(messageType, receiveFileListEntry(inputStream));
                            break;
                        case RESPONSE_TAG_GET_FILE_LIST_ENTRYY_END:
                            handleData(messageType, null);
                            received = true;
                            break;
                        case RESPONSE_TAG_GET_FILE:
                            handleData(messageType, receiveFile(inputStream));
                            received = true;
                            break;
                        case RESPONSE_TAG_TELEMETRY:
                            handleData(messageType, receiveTelemetry());
                            break;
                    }
                }
            }
            catch (IOException e) {
                handleData(RESPONSE_TAG_ERROR, e.getMessage());
            }
        }
    }

    @NonNull
    private TelemetryFileListEntry receiveFileListEntry(InputStream inputStream) throws IOException {
        byte[] dummy = new byte[4];
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
        if(data != null) {
            msg.obj = data;
        }
        handlers.get(what).forEach(handle -> handle.handleMessage(msg));
    }
}
