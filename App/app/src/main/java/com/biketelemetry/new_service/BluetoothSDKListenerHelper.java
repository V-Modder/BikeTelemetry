package com.biketelemetry.new_service;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BluetoothSDKListenerHelper {
    private static BluetoothSDKBroadcastReceiver mBluetoothSDKBroadcastReceiver;

    public static void registerBluetoothSDKListener(Context context, IBluetoothSDKListener listener) {
        if (mBluetoothSDKBroadcastReceiver == null) {
            mBluetoothSDKBroadcastReceiver = new BluetoothSDKBroadcastReceiver();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothUtils.ACTION_DEVICE_FOUND);
            intentFilter.addAction(BluetoothUtils.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothUtils.ACTION_DISCOVERY_STOPPED);
            intentFilter.addAction(BluetoothUtils.ACTION_DEVICE_CONNECTED);
            intentFilter.addAction(BluetoothUtils.ACTION_DEVICE_INFO_RECEIVED);
            intentFilter.addAction(BluetoothUtils.ACTION_FILE_LIST_ENTRY_RECEIVED);
            intentFilter.addAction(BluetoothUtils.ACTION_FILE_RECEIVED);
            intentFilter.addAction(BluetoothUtils.ACTION_TELEMETRY_RECEIVED);
            intentFilter.addAction(BluetoothUtils.ACTION_MESSAGE_SENT);
            intentFilter.addAction(BluetoothUtils.ACTION_CONNECTION_ERROR);
            intentFilter.addAction(BluetoothUtils.ACTION_DEVICE_DISCONNECTED);


            LocalBroadcastManager.getInstance(context)
                    .registerReceiver(mBluetoothSDKBroadcastReceiver, intentFilter);
        }

        mBluetoothSDKBroadcastReceiver.setBluetoothSDKListener(listener);
    }

    public static void unregisterBluetoothSDKListener(Context context, IBluetoothSDKListener listener) {
        if (mBluetoothSDKBroadcastReceiver != null) {
            if (mBluetoothSDKBroadcastReceiver.removeBluetoothSDKListener(listener)) {
                LocalBroadcastManager.getInstance(context)
                        .unregisterReceiver(mBluetoothSDKBroadcastReceiver);
                mBluetoothSDKBroadcastReceiver = null;
            }
        }
    }
}
