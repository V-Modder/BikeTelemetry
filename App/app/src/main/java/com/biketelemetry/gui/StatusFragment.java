package com.biketelemetry.gui;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biketelemetry.R;
import com.biketelemetry.data.TelemetryFileListEntry;
import com.biketelemetry.new_service.BluetoothSDKListenerHelper;
import com.biketelemetry.new_service.BluetoothSDKService;
import com.biketelemetry.new_service.BluetoothUtils;
import com.biketelemetry.new_service.IBluetoothSDKListener;
import com.biketelemetry.service.BluetoothService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

public class StatusFragment extends BottomSheetDialogFragment implements IBluetoothSDKListener {

//    private Messenger bluetoothServiceInput;
//    private Messenger bluetoothServiceReply;
//    private boolean bluetoothServiceBound;
    private BluetoothSDKService mService;
    //private FragmentPopupDiscoveredLabelerDeviceBinding binding;

    private StatusFragment() {
//        bluetoothServiceInput = null;
//        bluetoothServiceReply = new Messenger(new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                if(msg.what == BluetoothService.RESPONSE_TAG_GET_FILE_LIST_ENTRY) {
//                    fileList.add((TelemetryFileListEntry) msg.obj);
//                    recyclerAdapter.notifyItemInserted(fileList.size());
//                }
//            }
//        });
//        bluetoothServiceBound = false;
//        bluetoothServiceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName className,
//                                           IBinder service) {
//                bluetoothServiceInput = new Messenger(service);
//                bluetoothServiceBound = true;
//                getFiles();
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName arg0) {
//                bluetoothServiceInput = null;
//                bluetoothServiceBound = false;
//            }
//        };
    }

    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        Intent intent = new Intent(getContext(), BluetoothService.class);
//        getContext().bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
//
//        if (bluetoothServiceBound) {
//            getContext().unbindService(bluetoothServiceConnection);
//            bluetoothServiceBound = false;
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothSDKListenerHelper.unregisterBluetoothSDKListener(requireContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        //binding = FragmentPopupDiscoveredLabelerDeviceBinding.bind(view);

        bindBluetoothService();

        // Register Listener
        BluetoothSDKListenerHelper.registerBluetoothSDKListener(requireContext(), this);

        return view;
    }

    private void bindBluetoothService() {
        // Bind to LocalService
        Intent intent = new Intent(requireActivity().getApplicationContext(), BluetoothSDKService.class);
        requireActivity().getApplicationContext()
                .bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothSDKService.BluetoothSDKBinder binder = (BluetoothSDKService.BluetoothSDKBinder)service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    public List<String> getFilter() {
        return Arrays.asList(BluetoothUtils.ACTION_DISCOVERY_STARTED, BluetoothUtils.ACTION_DISCOVERY_STOPPED,
                BluetoothUtils.ACTION_DEVICE_FOUND, BluetoothUtils.ACTION_DEVICE_CONNECTED,
                BluetoothUtils.ACTION_DEVICE_DISCONNECTED, BluetoothUtils.ACTION_CONNECTION_ERROR,
                BluetoothUtils.ACTION_DEVICE_INFO_RECEIVED, BluetoothUtils.ACTION_FILE_LIST_ENTRY_RECEIVED,
                BluetoothUtils.ACTION_FILE_RECEIVED, BluetoothUtils.ACTION_TELEMETRY_RECEIVED);
    }

    @Override
    public void onDiscoveryStarted() {

    }

    @Override
    public void onDiscoveryStopped() {

    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {

    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {

    }

    @Override
    public void onMessageSent(BluetoothDevice device) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onDeviceDisconnected() {

    }
}