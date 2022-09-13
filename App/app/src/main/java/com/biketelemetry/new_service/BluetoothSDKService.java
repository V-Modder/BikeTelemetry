package com.biketelemetry.new_service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
        /*
        Function that can be called from Activity or Fragment
        */
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

        public  ConnectedThread(BluetoothSocket bluetoothSocket) {
            this.mmSocket = bluetoothSocket;
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