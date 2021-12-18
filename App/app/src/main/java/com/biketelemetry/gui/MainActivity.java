package com.biketelemetry.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.biketelemetry.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openLocalFiles(View view) {
        Intent intent = new Intent(this, LocalFileActivity.class);
        startActivity(intent);
    }

    public void openTelemetryFiles(View view) {
        Intent intent = new Intent(this, RemoteFileActivity.class);
        startActivity(intent);
    }

    public void openLiveStream(View view) {
        Intent intent = new Intent(this, RemoteFileActivity.class);
        startActivity(intent);
    }
}