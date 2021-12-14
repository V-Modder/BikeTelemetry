package com.biketelemetry.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.biketelemetry.data.Telemetry;
import com.biketelemetry.data.TelemetryFileListEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BluetoothService extends Service {
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRYY = 1;
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRYY_END = 2;
    public static final byte RESPONSE_TAG_GET_FILE = 3;
    public static final byte RESPONSE_TAG_TELEMETRY = 4;
    public static final byte RESPONSE_TAG_ERROR = (byte) 255;

    private static final String TELEMETRY_DEVICE_NAME = "Bike-Telemetry";

    public static final byte REQUEST_TAG_DEVICE_INFO = 1;
    public static final byte REQUEST_TAG_GET_FILE_LIST = 2;
    public static final byte REQUEST_TAG_GET_FILE = 3;
    public static final byte REQUEST_TAG_ENABLE_TELEMETRY = 4;


    private BluetoothSocket socket;
    private boolean interrupted;
    private Messenger guiMessenger;

    class IncomingHandler extends Handler {
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_TAG_DEVICE_INFO:
                    Toast.makeText(applicationContext, "REQUEST_TAG_DEVICE_INFO!", Toast.LENGTH_SHORT).show();
                    BluetoothService.this.requestDeviceInfo();
                    break;
                case REQUEST_TAG_GET_FILE_LIST:
                    Toast.makeText(applicationContext, "REQUEST_TAG_GET_FILE_LIST!", Toast.LENGTH_SHORT).show();
                    BluetoothService.this.requestFileList();
                    break;
                case REQUEST_TAG_GET_FILE:
                    Toast.makeText(applicationContext, "REQUEST_TAG_GET_FILE!", Toast.LENGTH_SHORT).show();
                    BluetoothService.this.requestFile((String)msg.obj);
                    break;
                case REQUEST_TAG_ENABLE_TELEMETRY:
                    Toast.makeText(applicationContext, "REQUEST_TAG_ENABLE_TELEMETRY!", Toast.LENGTH_SHORT).show();
                    BluetoothService.this.requestEnableTelemetry(msg.arg1 == 1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void requestDeviceInfo() {
        executeRequest(REQUEST_TAG_DEVICE_INFO, null);
    }

    private void requestFileList() {
        executeRequest(REQUEST_TAG_GET_FILE_LIST, null);
    }

    private void requestFile(String filename) {
        byte[] param = filename.getBytes(StandardCharsets.UTF_8);
        executeRequest(REQUEST_TAG_GET_FILE, param);
    }

    private void requestEnableTelemetry(boolean enable) {
        byte[] param = new byte[] { (byte) (enable ? 1 : 0) };
        executeRequest(REQUEST_TAG_ENABLE_TELEMETRY, param);
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

            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(command);
            if(param != null) {
                outputStream.write(param);
            }

            receiveResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveResponse() {
        boolean received = false;
        while(!received && !interrupted) {
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
        File tmpFile = File.createTempFile("download", ".csv", getCacheDir());
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

        try {
            guiMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        interrupted = false;
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        guiMessenger = new Messenger(new IncomingHandler(this));
        return guiMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        interrupted = true;
        return super.onUnbind(intent);
    }
}
