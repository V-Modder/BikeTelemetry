package com.biketelemetry.service;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.biketelemetry.data.Telemetry;
import com.biketelemetry.data.TelemetryBuilder;
import com.biketelemetry.data.TelemetryFileListEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BluetoothService extends Service {
    //https://www.c-sharpcorner.com/article/bound-service-using-messenger-in-android-part-3/
    public static final byte RESPONSE_TAG_DEVICE_CONNECTED = 1;
    public static final byte RESPONSE_TAG_DEVICE_INFO = 2;
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRY = 3;
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRY_END = 4;
    public static final byte RESPONSE_TAG_GET_FILE = 5;
    public static final byte RESPONSE_TAG_GET_FILE_END = 6;
    public static final byte RESPONSE_TAG_TELEMETRY = 7;
    public static final byte RESPONSE_TAG_ERROR = (byte) 255;

    private static final String TELEMETRY_DEVICE_NAME = "Bike-Telemetry";

    public static final byte IS_DEVICE_CONNECTED = 1;
    public static final byte REQUEST_TAG_DEVICE_INFO = 2;
    public static final byte REQUEST_TAG_GET_FILE_LIST = 3;
    public static final byte REQUEST_TAG_GET_FILE = 4;
    public static final byte REQUEST_TAG_DELETE_FILE = 5;
    public static final byte REQUEST_TAG_ENABLE_TELEMETRY = 6;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 54141;

    private BluetoothSocket socket;
    private boolean interrupted;
    private Messenger replyMessenger;
    private Messenger inputMessenger;

    class IncomingHandler extends Handler {
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IS_DEVICE_CONNECTED:
                    replyMessenger = msg.replyTo;
                    BluetoothService.this.isDeviceConnected();
                    break;
                case REQUEST_TAG_DEVICE_INFO:
                    Toast.makeText(applicationContext, "REQUEST_TAG_DEVICE_INFO!", Toast.LENGTH_SHORT).show();
                    replyMessenger = msg.replyTo;
                    BluetoothService.this.requestDeviceInfo();
                    break;
                case REQUEST_TAG_GET_FILE_LIST:
                    //Toast.makeText(applicationContext, "REQUEST_TAG_GET_FILE_LIST!", Toast.LENGTH_SHORT).show();
                    replyMessenger = msg.replyTo;
                    BluetoothService.this.requestFileList();
                    break;
                case REQUEST_TAG_GET_FILE:
                    Toast.makeText(applicationContext, "REQUEST_TAG_GET_FILE!", Toast.LENGTH_SHORT).show();
                    replyMessenger = msg.replyTo;
                    BluetoothService.this.requestFile((String)msg.obj);
                    break;
                case REQUEST_TAG_DELETE_FILE:
                    Toast.makeText(applicationContext, "REQUEST_TAG_DELETE_FILE!", Toast.LENGTH_SHORT).show();
                    BluetoothService.this.requestDeleteFile((String)msg.obj);
                    break;
                case REQUEST_TAG_ENABLE_TELEMETRY:
                    Toast.makeText(applicationContext, "REQUEST_TAG_ENABLE_TELEMETRY!", Toast.LENGTH_SHORT).show();
                    replyMessenger = msg.replyTo;
                    BluetoothService.this.requestEnableTelemetry(msg.arg1 == 1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void isDeviceConnected() {
        Optional<BluetoothDevice> telemetryDevice = getTelemetryDevice();
        boolean isConnected = false;
        if(telemetryDevice.isPresent()) {
            try {
                Method isConnectedMethod = telemetryDevice.get().getClass().getMethod("isConnected");
                if((boolean) isConnectedMethod.invoke(telemetryDevice.get())) {
                    isConnected = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Message msg = new Message();
        msg.what = RESPONSE_TAG_DEVICE_CONNECTED;
        msg.obj = isConnected;
        try {
            replyMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
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

    private void requestDeleteFile(String filename) {
        byte[] param = filename.getBytes(StandardCharsets.UTF_8);
        executeRequest(REQUEST_TAG_DELETE_FILE, param);
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
            if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                socket = telemetryDevice.get().createRfcommSocketToServiceRecord(telemetryDevice.get().getUuids()[0].getUuid());
            }

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
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (inputStream.available() > 0) {
                    int messageType = inputStream.read();
                    switch (messageType) {
                        case RESPONSE_TAG_DEVICE_INFO:
                            handleData(messageType, receiveDeviceInfo(inputStream));
                            break;
                        case RESPONSE_TAG_GET_FILE_LIST_ENTRY:
                            handleData(messageType, receiveFileListEntry(inputStream));
                            break;
                        case RESPONSE_TAG_GET_FILE_LIST_ENTRY_END:
                            handleData(messageType, null);
                            received = true;
                            break;
                        case RESPONSE_TAG_GET_FILE:
                            handleData(messageType, receiveFile(inputStream));
                            received = true;
                            break;
                        case RESPONSE_TAG_TELEMETRY:
                            handleData(messageType, receiveTelemetry(inputStream));
                            break;
                    }
                }
            }
            catch (IOException e) {
                handleData(RESPONSE_TAG_ERROR, e.getMessage());
            }
        }
    }

    private String receiveDeviceInfo(InputStream inputStream) throws IOException {
        return StreamHelper.readString(inputStream);
    }

    @NonNull
    private TelemetryFileListEntry receiveFileListEntry(InputStream inputStream) throws IOException {
        long size = StreamHelper.readInt(inputStream);
        String filename = StreamHelper.readString(inputStream);
        return new TelemetryFileListEntry(filename, size);
    }

    @NonNull
    private File receiveFile(InputStream inputStream) throws IOException {
        File tmpFile = File.createTempFile("download", ".csv", getCacheDir());
        try(FileOutputStream fileOutputStream = new FileOutputStream(tmpFile)) {
            int remainingBytes = StreamHelper.readInt(inputStream);
            while (remainingBytes > 0) {
                int bytesToRead = remainingBytes < 500 ? remainingBytes : 500;
                byte[] dummy = new byte[bytesToRead];
                inputStream.read(dummy, 0, dummy.length);
                fileOutputStream.write(dummy);
                remainingBytes -= bytesToRead;
            }
        }

        return tmpFile;
    }

    @NonNull
    private Telemetry receiveTelemetry(InputStream inputStream) throws IOException {
        return new TelemetryBuilder()
            .withLatitude(StreamHelper.readDouble(inputStream))
            .withLongitude(StreamHelper.readDouble(inputStream))
            .withAltitude(StreamHelper.readDouble(inputStream))
            .withDistance(StreamHelper.readDouble(inputStream))
            .withSpeed(StreamHelper.readDouble(inputStream))
            .withYear(StreamHelper.readShort(inputStream))
            .withMonth(inputStream.read())
            .withDay(inputStream.read())
            .withHour(inputStream.read())
            .withMinute(inputStream.read())
            .withSecond(inputStream.read())
            .withMillisecond(StreamHelper.readShort(inputStream))
            .withSatellites(inputStream.read())
            .withHdop(inputStream.read())
            .withRoll(inputStream.read())
            .withPitch(inputStream.read())
            .withTemperature(inputStream.read())
            .createTelemetry();
    }

    private Optional<BluetoothDevice> getTelemetryDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

//        if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
//                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//        }

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
            replyMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        interrupted = false;
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        inputMessenger = new Messenger(new IncomingHandler(this));
        return inputMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        interrupted = true;
        return super.onUnbind(intent);
    }
}
