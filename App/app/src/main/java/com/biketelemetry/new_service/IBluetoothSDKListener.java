package com.biketelemetry.new_service;

import android.bluetooth.BluetoothDevice;

import com.biketelemetry.data.Telemetry;
import com.biketelemetry.data.TelemetryFileListEntry;

public interface IBluetoothSDKListener {
    void onDiscoveryStarted();
    void onDiscoveryStopped();
    void onDeviceDiscovered(BluetoothDevice device);
    void onDeviceConnected(BluetoothDevice device);
    void onDeviceInfoReceived(BluetoothDevice device, String deviceInfo);
    void onFileListEntryReceived(BluetoothDevice device, TelemetryFileListEntry telemetryFileListEntry);
    void onFileReceived(BluetoothDevice device, String filename);
    void onTelemetryReceived(BluetoothDevice device, Telemetry message);
    void onMessageSent(BluetoothDevice device);
    void onError(String message);
    void onDeviceDisconnected();
}
