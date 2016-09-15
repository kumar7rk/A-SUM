package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.DataObject;
import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.adapter.RecyclerViewAdapter;

import java.util.ArrayList;

public class MonitoringFragmentCardView extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "MonitoringFragment";
    ArrayList<String> list = new ArrayList<String>();

    SharedPreferences preferences;
    public MonitoringFragmentCardView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.monitoring_card_view, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        return v;

    }
    @Override
    public void onResume() {
        super.onResume();
        ((RecyclerViewAdapter) mAdapter).setOnItemClickListener(new RecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }
    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        /*for (int index = 0; index < 5; index++) {
            DataObject obj = new DataObject("Some Primary Text " + index,
                    "Secondary " + index);
            results.add(index, obj);
        }*/

        checkValues();
        for (int index = 0; index < list.size(); index++) {
            String violationTIme = "";
            violationTIme = preferences.getString(CONSTANTS.VIOLATION_TIME,violationTIme);

            DataObject obj = new DataObject(list.get(index)+"!",
                    "Recent Violation: "+ violationTIme);
            results.add(index, obj);
        }
        return results;
    }
    private void checkValues(){
        boolean restaurant = preferences.getBoolean(getResources().getString(R.string.restaurant), false);
        boolean religious_place = preferences.getBoolean(getResources().getString(R.string.religious_place), false);
        boolean movie_theatre = preferences.getBoolean(getResources().getString(R.string.movie_theatre), false);
        boolean bed_dark = preferences.getBoolean(getResources().getString(R.string.bed_dark), false);
        boolean walking = preferences.getBoolean(getResources().getString(R.string.walking), false);
        if (restaurant)
            list.add(getResources().getString(R.string.restaurant));
        if (religious_place)
            list.add(getResources().getString(R.string.religious_place));
        if (movie_theatre)
            list.add(getResources().getString(R.string.movie_theatre));
        if (bed_dark)
            list.add(getResources().getString(R.string.bed_dark));
        if (walking)
            list.add(getResources().getString(R.string.walking));

        if (!(restaurant || religious_place || movie_theatre || bed_dark || walking))
            list.add("No scenario to monitor. Click + symbol to add one");
    }
}
