package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

import java.util.ArrayList;

/**
 * Created by Rohit on 13/09/2016.
 */
public class NotificationFragment extends Fragment {
    ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification_detail, container, false);
        listView = (ListView)v.findViewById(R.id.list);
        loadItems();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Main.showToast(getResources().getString(R.string.coming_soon));
            }
        });
        return v;
    }
    public void loadItems() {
        list.add("Notification Sound");
        list.add("Notification Text");
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);
    }
}
