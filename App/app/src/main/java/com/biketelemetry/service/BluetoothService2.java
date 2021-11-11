package com.biketelemetry.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import androidx.annotation.StringDef;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BluetoothService2 {
    public static final String COMMAND_GET_FILE_LIST = "get_file_list";
    public static final String COMMAND_GET_FILE = "get_file";
    public static final String COMMAND_REMOVE_FILE = "remove_file";
    public static final String RESPONSE_TAG_GET_FILE_LIST = "FILE_LIST";
    public static final String RESPONSE_TAG_GET_FILE = "get_file";
    public static final String RESPONSE_TAG_REMOVE_FILE = "remove_file";
    private static final String RESPONSE_TAG_NONE = "none";

    @StringDef(value = {
            COMMAND_GET_FILE_LIST,
            COMMAND_GET_FILE,
            COMMAND_REMOVE_FILE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Command {}

    @StringDef(value = {
            RESPONSE_TAG_GET_FILE_LIST,
            RESPONSE_TAG_GET_FILE,
            RESPONSE_TAG_REMOVE_FILE,
            RESPONSE_TAG_NONE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResponseTag {}

    private static final String TELEMETRY_DEVICE_NAME = "Bike-Telemetry";

    public boolean bluetoothEnabled() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        return bluetooth != null && bluetooth.isEnabled();
    }

    public Optional<BluetoothDevice> getTelemetryDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.getBondedDevices()
                .stream()
                .filter(dev -> TELEMETRY_DEVICE_NAME.equals(dev.getName()))
                .findFirst();
    }

    public String send(@Command String command, @ResponseTag String responseTag) throws Exception {
        return send(command, Optional.empty(), responseTag, Optional.empty(), responseTag);
    }

    public String send(@Command String command, Optional<String> inputData, @ResponseTag String responseTag, Optional<String> responseTagExtension, @ResponseTag String dataTag) throws Exception {
        /*if(!bluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 12);
        }*/
        if(responseTagExtension.isPresent()) {
            responseTag += responseTagExtension;
        }
        if(inputData.isPresent()) {
            command += inputData;
        }
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder data = new StringBuilder();
        String line;

        do {
             line = reader.readLine();
             if(line.startsWith(dataTag)) {
                 data.append(line);
             }
        }while (!line.startsWith(responseTag + "|"));

        return data.toString();
    }
}
