package com.biketelemetry.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.biketelemetry.R;
import com.biketelemetry.data.LocalFileListLoader;
import com.biketelemetry.data.TelemetryFileListEntry;

import java.util.List;

public class LocalFileActivity extends AppCompatActivity {

    private List<TelemetryFileListEntry> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file);

        fileList = new LocalFileListLoader(getApplicationContext()).loadFilelist();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapter(fileList));
        registerForContextMenu(recyclerView);
    }
}