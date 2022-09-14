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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class BluetoothSDKService extends Service {

    // Service Binder
    private LocalBinder binder;

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

    class LocalBinder extends Binder {
        /**
         * Enable the discovery, registering a broadcastreceiver {@link discoveryBroadcastReceiver}
         * The discovery filter by LABELER_SERVER_TOKEN_NAME
         */
        public void startDiscovery(Context context) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(discoveryBroadcastReceiver, filter);
            bluetoothAdapter.startDiscovery();
            pushBroadcastMessage(BluetoothUtils.ACTION_DISCOVERY_STARTED, null, null);
        }


        /**
         * stop discovery
         */
        public void stopDiscovery() {
            bluetoothAdapter.cancelDiscovery();
            pushBroadcastMessage(BluetoothUtils.ACTION_DISCOVERY_STOPPED, null, null);
        }
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
                     numBytes = mmInStream.read(mmBuffer);
                } catch (IOException e) {
                    pushBroadcastMessage(
                            BluetoothUtils.ACTION_CONNECTION_ERROR,
                            null,
                            "Input stream was disconnected"
                    );
                    break;
                }

                String message = new String(mmBuffer, 0, numBytes);

                // Send to broadcast the message
                pushBroadcastMessage(
                        BluetoothUtils.ACTION_MESSAGE_RECEIVED,
                        mmSocket.getRemoteDevice(),
                        message
                );
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Send to broadcast the message
                pushBroadcastMessage(
                        BluetoothUtils.ACTION_MESSAGE_SENT,
                        mmSocket.getRemoteDevice(),
                        null
                );
            } catch (IOException e) {
                pushBroadcastMessage(
                        BluetoothUtils.ACTION_CONNECTION_ERROR,
                        null,
                        "Error occurred when sending data"
                );
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                pushBroadcastMessage(
                        BluetoothUtils.ACTION_CONNECTION_ERROR,
                        null,
                        "Could not close the connect socket"
                );
            }
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

    private void pushBroadcastMessage(String action, BluetoothDevice device, String message) {
        Intent intent = new Intent(action);
        if (device != null) {
            intent.putExtra(BluetoothUtils.EXTRA_DEVICE, device);
        }
        if (message != null) {
            intent.putExtra(BluetoothUtils.EXTRA_MESSAGE, message);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}