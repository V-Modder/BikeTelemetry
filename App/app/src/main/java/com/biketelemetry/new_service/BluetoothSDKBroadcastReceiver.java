package com.biketelemetry.new_service;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothSDKBroadcastReceiver extends BroadcastReceiver {
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
