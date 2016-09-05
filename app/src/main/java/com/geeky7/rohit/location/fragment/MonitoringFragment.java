package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.R;

import java.util.ArrayList;

public class MonitoringFragment extends Fragment{

    ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_monitoring, container, false);
        listView = (ListView)v.findViewById(R.id.listView);

        return v;
    }

    public void loadItems() {
        checkValues();
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadItems();
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
            list.add(getResources().getString(R.string.restaurant));
        if (religious_place)
            list.add(getResources().getString(R.string.religious_place));
        if (movie_theatre)
            list.add(getResources().getString(R.string.movie_theatre));
        if (bed_dark)
            list.add(getResources().getString(R.string.bed_dark));
        if (walking)
            list.add(getResources().getString(R.string.walking));
    }

}
