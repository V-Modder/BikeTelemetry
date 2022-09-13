package com.biketelemetry.new_service;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BluetoothSDKListenerHelper {
    private BluetoothSDKBroadcastReceiver mBluetoothSDKBroadcastReceiver;

    class BluetoothSDKBroadcastReceiver extends BroadcastReceiver {
        private IBluetoothSDKListener mGlobalListener;

        public void setBluetoothSDKListener(IBluetoothSDKListener listener) {
            mGlobalListener = listener;
        }

        public boolean removeBluetoothSDKListener(IBluetoothSDKListener listener) {
            if (mGlobalListener == listener) {
                mGlobalListener = null;
            }

            return mGlobalListener == null;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothUtils.EXTRA_DEVICE);
            String message = intent.getStringExtra(BluetoothUtils.EXTRA_MESSAGE);

            switch (intent.getAction()) {
                case BluetoothUtils.ACTION_DEVICE_FOUND:
                    mGlobalListener.onDeviceDiscovered(device);
                    break;
                case BluetoothUtils.ACTION_DISCOVERY_STARTED:
                    mGlobalListener.onDiscoveryStarted();
                    break;
                case BluetoothUtils.ACTION_DISCOVERY_STOPPED:
                    mGlobalListener.onDiscoveryStopped();
                    break;
                case BluetoothUtils.ACTION_DEVICE_CONNECTED:
                    mGlobalListener.onDeviceConnected(device);
                    break;
                case BluetoothUtils.ACTION_MESSAGE_RECEIVED:
                    mGlobalListener.onMessageReceived(device, message);
                    break;
                case BluetoothUtils.ACTION_MESSAGE_SENT:
                    mGlobalListener.onMessageSent(device);
                    break;
                case BluetoothUtils.ACTION_CONNECTION_ERROR:
                    mGlobalListener.onError(message);
                    break;
                case BluetoothUtils.ACTION_DEVICE_DISCONNECTED:
                    mGlobalListener.onDeviceDisconnected();
                    break;
            }
        }
    }

    public void registerBluetoothSDKListener(Context context, IBluetoothSDKListener listener) {
        if (mBluetoothSDKBroadcastReceiver == null) {
            mBluetoothSDKBroadcastReceiver = new BluetoothSDKBroadcastReceiver();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothUtils.ACTION_DEVICE_FOUND);
            intentFilter.addAction(BluetoothUtils.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothUtils.ACTION_DISCOVERY_STOPPED);
            intentFilter.addAction(BluetoothUtils.ACTION_DEVICE_CONNECTED);
            intentFilter.addAction(BluetoothUtils.ACTION_MESSAGE_RECEIVED);
            intentFilter.addAction(BluetoothUtils.ACTION_MESSAGE_SENT);
            intentFilter.addAction(BluetoothUtils.ACTION_CONNECTION_ERROR);
            intentFilter.addAction(BluetoothUtils.ACTION_DEVICE_DISCONNECTED);


            LocalBroadcastManager.getInstance(context)
                    .registerReceiver(mBluetoothSDKBroadcastReceiver, intentFilter);
        }

        mBluetoothSDKBroadcastReceiver.setBluetoothSDKListener(listener);
    }

    public void unregisterBluetoothSDKListener(Context context, IBluetoothSDKListener listener) {
        if (mBluetoothSDKBroadcastReceiver != null) {
            boolean empty = mBluetoothSDKBroadcastReceiver.removeBluetoothSDKListener(listener);

            if (empty) {
                LocalBroadcastManager.getInstance(context)
                        .unregisterReceiver(mBluetoothSDKBroadcastReceiver);
                mBluetoothSDKBroadcastReceiver = null;
            }
        }
    }
}
