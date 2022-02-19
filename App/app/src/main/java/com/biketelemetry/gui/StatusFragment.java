package com.biketelemetry.gui;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biketelemetry.R;
import com.biketelemetry.service.BluetoothService;

public class StatusFragment extends Fragment {

    private Messenger bluetoothServiceInput;
    private Messenger bluetoothServiceReply;
    private boolean bluetoothServiceBound;
    private ServiceConnection bluetoothServiceConnection;

    private StatusFragment() {
        // Required empty public constructor
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
    protected void onStop() {
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