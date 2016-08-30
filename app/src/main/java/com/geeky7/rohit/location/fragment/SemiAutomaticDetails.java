package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.R;

import java.util.ArrayList;

/**
 * Created by Rohit on 30/08/2016.
 */
public class SemiAutomaticDetails extends Fragment {
    ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_semiautomatic_detail, container, false);
//        listView = (ListView)v.findViewById(android.R.id.list);
        listView = (ListView)v.findViewById(R.id.list);
        loadItems();
        return v;
    }
    public void loadItems() {
        list.add("Blocked app goes here");
        list.add("Something like allowed for x minutes;");
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }
}
