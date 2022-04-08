package com.biketelemetry.gui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biketelemetry.R;

public class StatusFragment extends Fragment {

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
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bund le savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }
}