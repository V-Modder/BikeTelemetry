package com.biketelemetry.new_service;

import android.bluetooth.BluetoothDevice;

import com.biketelemetry.data.Telemetry;
import com.biketelemetry.data.TelemetryFileListEntry;

import java.util.List;

public interface IBluetoothSDKListener {
    List<String> getFilter();
    default void onDeviceConnected(BluetoothDevice device) {}
    default void onDeviceDisconnected() {}
    default void onDeviceDiscovered(BluetoothDevice device) {}
    default void onDeviceInfoReceived(BluetoothDevice device, String deviceInfo) {}
    default void onDiscoveryStarted() {}
    default void onDiscoveryStopped() {}
    default void onError(String message) {}
    default void onFileListEntryReceived(BluetoothDevice device, TelemetryFileListEntry telemetryFileListEntry) {}
    default void onFileReceived(BluetoothDevice device, String filename) {}
    default void onMessageSent(BluetoothDevice device) {}
    default void onTelemetryReceived(BluetoothDevice device, Telemetry message) {}
}
