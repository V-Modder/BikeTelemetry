package com.biketelemetry.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.biketelemetry.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_status, StatusFragment.newInstance()).commit();
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