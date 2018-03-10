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
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.MonitoringDataObject;
import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.activity.MainActivity;
import com.geeky7.rohit.location.adapter.MonitoringRecyclerViewAdapter;

import java.util.ArrayList;

public class MonitoringFragmentCardView extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "MonitoringFragment";
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> detectedTimeList = new ArrayList<String>();
    String detected_restaurant,detected_religiousPlace,
            detected_movieThestre,detected_bedAndDark,detected_walking;

    SharedPreferences preferences;

    Main m;
    public MonitoringFragmentCardView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        m = new Main(MyApplication.getAppContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.monitoring_recycler_view, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MonitoringRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        return v;

    }
    @Override
    public void onResume() {
        super.onResume();
        ((MonitoringRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MonitoringRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }
    private ArrayList<MonitoringDataObject> getDataSet() {
        ArrayList results = new ArrayList<MonitoringDataObject>();
        checkValues();
        if (list.size()>0){
            for (int index = 0; index < list.size(); index++) {
                String detectionTime;
                detectionTime = detectedTimeList.get(index);

                if (detectionTime == null)  detectionTime = "No Detection Yet!";

                MonitoringDataObject obj = new MonitoringDataObject(list.get(index)+"",
                        "Recent Detection: "+ detectionTime);
                results.add(index, obj);
            }
            if (!m.usageAccessPermission()) m.showUsageDataAccessDialog(getActivity());
        }
        else{
            MonitoringDataObject obj = new MonitoringDataObject("No scenario monitored",
                    "Click + symbol to add one");
            results.add(0,obj);
        }
        return results;
    }
    private void checkValues(){
        boolean restaurant = preferences.getBoolean(getResources().getString(R.string.restaurant), false);
        boolean religious_place = preferences.getBoolean(getResources().getString(R.string.religious_place), false);
        boolean movie_theatre = preferences.getBoolean(getResources().getString(R.string.movie_theatre), false);
        boolean bed_dark = preferences.getBoolean(getResources().getString(R.string.bed_dark), false);
        boolean walking = preferences.getBoolean(getResources().getString(R.string.walking), false);
        if (restaurant){
            list.add(getResources().getString(R.string.restaurant));
            detected_restaurant = preferences.getString(CONSTANTS.DETECTED_RESTAURNT_TIME, detected_restaurant);
            detectedTimeList.add(detected_restaurant);
        }
        if (religious_place){
            list.add(getResources().getString(R.string.religious_place));
            detected_religiousPlace = preferences.getString(CONSTANTS.DETECTED_RELIGIOUSPLACE_TIME,detected_religiousPlace);
            detectedTimeList.add(detected_religiousPlace);
        }
        if (movie_theatre){
            list.add(getResources().getString(R.string.movie_theatre));
            detected_movieThestre = preferences.getString(CONSTANTS.DETECTED_MOVIETHEATRE_TIME,detected_movieThestre);
            detectedTimeList.add(detected_movieThestre);
        }
        if (bed_dark){
            list.add(getResources().getString(R.string.bed_dark));
            detected_bedAndDark = preferences.getString(CONSTANTS.DETECTED_BEDANDDARK_TIME,detected_bedAndDark);
            detectedTimeList.add(detected_bedAndDark);
        }
        if (walking){
            list.add(getResources().getString(R.string.walking));
            detected_walking = preferences.getString(CONSTANTS.DETECTED_WALKING_TIME,detected_walking);
            detectedTimeList.add(detected_walking);
        }

        /*if (!(restaurant || religious_place || movie_theatre || bed_dark || walking))
            list.add("No scenario to monitor. Click + symbol to add one");*/
    }
}
