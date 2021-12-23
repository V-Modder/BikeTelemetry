package com.biketelemetry.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.MenuItem;
import android.widget.Toast;

import com.biketelemetry.R;
import com.biketelemetry.data.TelemetryFileListEntry;
import com.biketelemetry.service.BluetoothService;

import java.util.ArrayList;
import java.util.List;

public class RemoteFileActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 12;

    private Messenger bluetoothServiceInput;
    private Messenger bluetoothServiceReply;
    private boolean bluetoothServiceBound;
    private ServiceConnection bluetoothServiceConnection;
    private List<TelemetryFileListEntry> fileList;
    private RecyclerAdapter recyclerAdapter;

    public RemoteFileActivity() {
        bluetoothServiceInput = null;
        bluetoothServiceReply = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_file);

        fileList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(fileList);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
        registerForContextMenu(recyclerView);
        //getFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fileList.clear();
        //getFiles();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (bluetoothServiceBound) {
            unbindService(bluetoothServiceConnection);
            bluetoothServiceBound = false;
        }
    }

    private void getFiles() {
        Message msg = new Message();
        msg.what = BluetoothService.REQUEST_TAG_GET_FILE_LIST;
        msg.replyTo = bluetoothServiceReply;
        try {
            bluetoothServiceInput.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((RecyclerAdapter)getAdapter()).getSelectedIndex();
        } catch (Exception e) {
            //Log.d(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.ctx_copy:
                copyTelemetryFile(fileList.get(position));
                break;
            case R.id.ctx_delete:
                deleteTelemetryFile(fileList.get(position));
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void copyTelemetryFile(TelemetryFileListEntry telemetryFileListEntry) {
        Message msg = new Message();
        msg.what = BluetoothService.REQUEST_TAG_GET_FILE;
        msg.obj = telemetryFileListEntry.getFilename();
        msg.replyTo = bluetoothServiceReply;
        try {
            bluetoothServiceInput.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void deleteTelemetryFile(TelemetryFileListEntry telemetryFileListEntry) {
        Message msg = new Message();
        msg.what = BluetoothService.REQUEST_TAG_DELETE_FILE;
        msg.replyTo = bluetoothServiceReply;
        try {
            bluetoothServiceInput.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private RecyclerView.Adapter getAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        return recyclerView.getAdapter();
    }

    private String getLocalPath() {
        return "";
    }
}