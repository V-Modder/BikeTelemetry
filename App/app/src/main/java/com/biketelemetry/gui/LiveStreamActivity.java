package com.biketelemetry.gui;

import android.app.Activity;
import android.os.Bundle;

import com.biketelemetry.R;

public class LiveStreamActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);
    }
}
