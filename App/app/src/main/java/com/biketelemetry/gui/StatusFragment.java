package com.biketelemetry.gui;

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
import com.biketelemetry.service.BluetoothService;

public class StatusFragment extends Fragment {

    private Messenger bluetoothServiceInput;
    private Messenger bluetoothServiceReply;
    private boolean bluetoothServiceBound;
    private ServiceConnection bluetoothServiceConnection;

    private StatusFragment() {
        bluetoothServiceInput = null;
        bluetoothServiceReply = new Messenger(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == BluetoothService.RESPONSE_TAG_GET_FILE_LIST_ENTRY) {
                    fileList.add((TelemetryFileListEntry) msg.obj);
                    recyclerAdapter.notifyItemInserted(fileList.size());
                }
            }
        });
        bluetoothServiceBound = false;
        bluetoothServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                bluetoothServiceInput = new Messenger(service);
                bluetoothServiceBound = true;
                getFiles();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bluetoothServiceInput = null;
                bluetoothServiceBound = false;
            }
        };
    }

    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), BluetoothService.class);
        getContext().bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (bluetoothServiceBound) {
            getContext().unbindService(bluetoothServiceConnection);
            bluetoothServiceBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }
}