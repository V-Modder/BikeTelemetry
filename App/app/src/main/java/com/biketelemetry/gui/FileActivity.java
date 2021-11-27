package com.biketelemetry.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.biketelemetry.R;
import com.biketelemetry.data.FileListLoader;
import com.biketelemetry.data.LocalFileListLoader;
import com.biketelemetry.data.TelemetryFileListEntry;
import com.biketelemetry.data.TelemetryFileListLoader;

import java.util.ArrayList;
import java.util.List;

public class FileActivity extends AppCompatActivity {
    public static final String USE_LOCAL_FILE_LOADER = "local_file_loader";
    private static final int REQUEST_ENABLE_BT = 12;

    private List<TelemetryFileListEntry> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        fileList = new ArrayList<>();//= getFiles();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapter(fileList));
        if(!getIntent().getBooleanExtra(USE_LOCAL_FILE_LOADER, true)) {
            registerForContextMenu(recyclerView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        fileList.clear();
        fileList.addAll(getFiles());
    }

    private List<TelemetryFileListEntry> getFiles() {
        FileListLoader loader;
        if(getIntent().getBooleanExtra(USE_LOCAL_FILE_LOADER, true)) {
            loader = new LocalFileListLoader();
        }
        else {
            loader = new TelemetryFileListLoader();
        }
        return loader.getFiles(getApplicationContext());
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
    }

    private void deleteTelemetryFile(TelemetryFileListEntry telemetryFileListEntry) {
        BluetoothService2 serv = new BluetoothService2();
        serv.send(BluetoothService2.COMMAND_GET_FILE, rey)
    }

    private RecyclerView.Adapter getAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        return recyclerView.getAdapter();
    }

    private String getLocalPath() {
        return "";
    }
}