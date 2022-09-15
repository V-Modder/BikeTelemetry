package com.biketelemetry.new_service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.biketelemetry.data.Telemetry;
import com.biketelemetry.data.TelemetryBuilder;
import com.biketelemetry.data.TelemetryFileListEntry;
import com.biketelemetry.service.StreamHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class BluetoothSDKService extends Service {

    // Service Binder
    private BluetoothSDKBinder binder;

    // Bluetooth stuff
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice connectedDevice;
    private String MY_UUID = "...";
    private int RESULT_INTENT = 15;

    // Bluetooth connections
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private AcceptThread mAcceptThread;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /**
     * Broadcast Receiver for catching ACTION_FOUND aka new device discovered
     */
    private BroadcastReceiver discoveryBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*
              Our broadcast receiver for manage Bluetooth actions
            */
        }
    };

    private class AcceptThread extends Thread {
    }

    private class ConnectThread extends Thread {
        private BluetoothDevice device;
    }

    private synchronized void startConnectedThread(BluetoothSocket bluetoothSocket) {
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
    }

    private class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket bluetoothSocket) throws IOException {
            this.mmSocket = bluetoothSocket;
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
            mmBuffer = new byte[1024];
        }

        @Override
         public void run() {
            int numBytes;

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                 try {
                     numBytes = mmInStream.read();
                     readData(mmInStream, mmSocket.getRemoteDevice());
                 } catch (IOException e) {
                     pushBroadcastMessage(BluetoothUtils.ACTION_CONNECTION_ERROR,null,null,"Input stream was disconnected");
                     break;
                 }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                pushBroadcastMessage(BluetoothUtils.ACTION_MESSAGE_SENT, mmSocket.getRemoteDevice(), null,null);
            } catch (IOException e) {
                pushBroadcastMessage(BluetoothUtils.ACTION_CONNECTION_ERROR,null, null,"Error occurred when sending data");
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                pushBroadcastMessage(BluetoothUtils.ACTION_CONNECTION_ERROR,null, null, "Could not close the connect socket");
            }
        }

        private void readData(InputStream inputStream, BluetoothDevice device) throws IOException {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (inputStream.available() > 0) {
                int messageType = inputStream.read();
                switch (messageType) {
                    case BluetoothUtils.RESPONSE_TAG_DEVICE_INFO:
                        pushBroadcastMessage(BluetoothUtils.ACTION_DEVICE_INFO_RECEIVED, device,null, receiveDeviceInfo(inputStream));
                        break;
                    case BluetoothUtils.RESPONSE_TAG_GET_FILE_LIST_ENTRY:
                        pushBroadcastMessage(BluetoothUtils.ACTION_FILE_LIST_ENTRY_RECEIVED, device, receiveFileListEntry(inputStream), null);
                        break;
                    //case BluetoothUtils.RESPONSE_TAG_GET_FILE_LIST_ENTRY_END:
                    //    pushBroadcastMessage(BluetoothUtils., device, null, null);
                    //    break;
                    case BluetoothUtils.RESPONSE_TAG_GET_FILE:
                        pushBroadcastMessage(BluetoothUtils.ACTION_FILE_RECEIVED, device,null, receiveFile(inputStream));
                        break;
                    case BluetoothUtils.RESPONSE_TAG_TELEMETRY:
                        pushBroadcastMessage(BluetoothUtils.ACTION_TELEMETRY_RECEIVED, device, receiveTelemetry(inputStream), null);
                        break;
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
        private String receiveFile(InputStream inputStream) throws IOException {
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

            return tmpFile.getAbsolutePath();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(discoveryBroadcastReceiver);
        } catch (Exception e) {
            // already unregistered
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void pushBroadcastMessage(String action, BluetoothDevice device, Parcelable parcelable, String str) {
        Intent intent = new Intent(action);
        if (device != null) {
            intent.putExtra(BluetoothUtils.EXTRA_DEVICE, device);
        }
        if (parcelable != null) {
            intent.putExtra(BluetoothUtils.EXTRA_PARCEBLE, parcelable);
        }
        if(str != null) {
            intent.putExtra(BluetoothUtils.EXTRA_STRING, str);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public class BluetoothSDKBinder extends Binder {
        public void startDiscovery(Context context) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(discoveryBroadcastReceiver, filter);
            bluetoothAdapter.startDiscovery();
            pushBroadcastMessage(BluetoothUtils.ACTION_DISCOVERY_STARTED, null,null, null);
        }

        public void stopDiscovery() {
            bluetoothAdapter.cancelDiscovery();
            pushBroadcastMessage(BluetoothUtils.ACTION_DISCOVERY_STOPPED, null,  null,null);
        }

        public BluetoothSDKService getService() {
            return BluetoothSDKService.this;
        }
    }

    public void requestDeviceInfo() {
        connectedThread.write(new byte[] {BluetoothUtils.REQUEST_TAG_DEVICE_INFO});
    }

    public void requestFileList() {
        connectedThread.write(new byte[] {BluetoothUtils.REQUEST_TAG_GET_FILE_LIST});
    }

    public void requestFile(String filename) {
        byte[] param = filename.getBytes(StandardCharsets.UTF_8);
        connectedThread.write(new byte[] {BluetoothUtils.REQUEST_TAG_GET_FILE});
        connectedThread.write(param);
    }

    public void requestDeleteFile(String filename) {
        byte[] param = filename.getBytes(StandardCharsets.UTF_8);
        connectedThread.write(new byte[] {BluetoothUtils.REQUEST_TAG_DELETE_FILE});
        connectedThread.write(param);
    }

    public void requestEnableTelemetry(boolean enable) {
        byte[] param = new byte[] { (byte) (enable ? 1 : 0) };
        connectedThread.write(new byte[] {BluetoothUtils.REQUEST_TAG_ENABLE_TELEMETRY});
        connectedThread.write(param);
    }
}