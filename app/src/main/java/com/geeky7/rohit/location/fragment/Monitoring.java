package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.service.BackgroundService;


public class Monitoring extends Fragment implements View.OnClickListener{
    Button startServiceB,stopServiceB;
    boolean running;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkValues();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getView(inflater, container);
        return v;
    }
    private View getView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.monitoring, container, false);
        startServiceB = (Button)v.findViewById(R.id.startService);
        stopServiceB = (Button)v.findViewById(R.id.stopService);
        startServiceB.setOnClickListener(this);
        stopServiceB.setOnClickListener(this);
        startServiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running)
                    startService();
            }
        });
        stopServiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
                getActivity().stopService(serviceIntent);
                running = false;
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
    }
    private void startService() {
        Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
        getActivity().startService(serviceIntent);
    }
    private void checkValues()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean restaurant = sharedPrefs.getBoolean(getResources().getString(R.string.restaurant), false);
        boolean religious_place = sharedPrefs.getBoolean(getResources().getString(R.string.religious_place), false);
        boolean movie_theatre = sharedPrefs.getBoolean(getResources().getString(R.string.movie_theatre), false);
        boolean bed_dark = sharedPrefs.getBoolean(getResources().getString(R.string.bed_dark), false);
        boolean walking = sharedPrefs.getBoolean(getResources().getString(R.string.walking), false);
        if (restaurant)
            Main.showToast(getContext(), restaurant + "");
    }
}
