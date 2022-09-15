package com.biketelemetry.new_service;

import android.content.Context;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BluetoothSDKListenerHelper {
    private static List<BluetoothSDKBroadcastReceiver> receivers;

    public static void registerBluetoothSDKListener(Context context, IBluetoothSDKListener listener) {
        if (receivers == null) {
            receivers = new ArrayList<BluetoothSDKBroadcastReceiver>();
        }
        receiverRegistered(listener)
            .orElse(createReceiver(context, listener));
    }

    public static void unregisterBluetoothSDKListener(Context context, IBluetoothSDKListener listener) {
        if (receivers != null) {
            receiverRegistered(listener).ifPresent(r -> {
               if(r.removeBluetoothSDKListener(listener)) {
                   LocalBroadcastManager.getInstance(context)
                           .unregisterReceiver(r);
               }
            });
        }
    }

    private static Optional<BluetoothSDKBroadcastReceiver> receiverRegistered(IBluetoothSDKListener listener) {
        return receivers.stream()
            .filter(r -> r.getBluetoothSDKListener() == listener)
            .findFirst();
    }

    private static BluetoothSDKBroadcastReceiver createReceiver(Context context, IBluetoothSDKListener listener) {
        BluetoothSDKBroadcastReceiver receiver = new BluetoothSDKBroadcastReceiver();
        receiver.setBluetoothSDKListener(listener);

        IntentFilter intentFilter = new IntentFilter();
        listener.getFilter().forEach(filter -> intentFilter.addAction(filter));

        LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, intentFilter);

        return receiver;
    }
}
