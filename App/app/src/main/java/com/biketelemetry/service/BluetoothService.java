package com.biketelemetry.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.annotation.IntDef;

import com.biketelemetry.data.TelemetryFileListEntry;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BluetoothService {
    public static final byte RESPONSE_TAG_GET_FILE_LIST = 1;
    public static final byte RESPONSE_TAG_GET_FILE = 2;
    public static final byte RESPONSE_TAG_TELEMETRY = 3;

    @IntDef(value = {
            RESPONSE_TAG_GET_FILE_LIST,
            RESPONSE_TAG_GET_FILE,
            RESPONSE_TAG_TELEMETRY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResponseTag {}


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

        if(inputStream.available() > 0) {
            int messageType = inputStream.read();
            switch (messageType) {
                case RESPONSE_TAG_GET_FILE_LIST:
                    byte[] dummy =new byte[4];
                    inputStream.read(dummy);
                    int size = ByteBuffer.wrap(dummy).getInt();
                    StringBuilder sb = new StringBuilder();
                    int ch = 0;
                    while ((ch = inputStream.read()) != -1) {
                        sb.append((char) ch);
                    }

                    String s = sb.toString();
                    new TelemetryFileListEntry(s, size);
                    break;
                case RESPONSE_TAG_GET_FILE:
                    byte[] dummy = new byte[11];
                    String str = "FILE_START|";
                    while((inputStream.read(dummy)))

                    StringBuilder sb = new StringBuilder();
                    int ch = 0;
                    while ((ch = inputStream.read()) != -1 && (char)ch == '\n') {
                        sb.append((char) ch);
                    }

                    break;
                case RESPONSE_TAG_TELEMETRY:
                    break;
            }
        }
    }

    private Optional<BluetoothDevice> getTelemetryDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.getBondedDevices()
                .stream()
                .filter(dev -> TELEMETRY_DEVICE_NAME.equals(dev.getName()))
                .findFirst();
    }

}
