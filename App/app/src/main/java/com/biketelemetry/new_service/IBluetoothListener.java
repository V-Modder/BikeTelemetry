package com.biketelemetry.new_service;

import android.bluetooth.BluetoothDevice;

interface IBluetoothSDKListener {
    /**
     * from action BluetoothUtils.ACTION_DISCOVERY_STARTED
     */
    void onDiscoveryStarted();

    /**
     * from action BluetoothUtils.ACTION_DISCOVERY_STOPPED
     */
    void onDiscoveryStopped();

    /**
     * from action BluetoothUtils.ACTION_DEVICE_FOUND
     */
    void onDeviceDiscovered(BluetoothDevice device);

    /**
     * from action BluetoothUtils.ACTION_DEVICE_CONNECTED
     */
    void onDeviceConnected(BluetoothDevice device);

    /**
     * from action BluetoothUtils.ACTION_MESSAGE_RECEIVED
     */
    void onMessageReceived(BluetoothDevice device, String message);

    /**
     * from action BluetoothUtils.ACTION_MESSAGE_SENT
     */
    void onMessageSent(BluetoothDevice device);

    /**
     * from action BluetoothUtils.ACTION_CONNECTION_ERROR
     */
    void onError(String message);

    /**
     * from action BluetoothUtils.ACTION_DEVICE_DISCONNECTED
     */
    void onDeviceDisconnected();
}
