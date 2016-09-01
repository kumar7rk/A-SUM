package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.R;

import java.util.ArrayList;

/**
 * Created by Rohit on 30/08/2016.
 */
public class list_of_rule extends Fragment {

    ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_of_rule, container, false);
//        listView = (ListView)v.findViewById(android.R.id.list);
        listView = (ListView)v.findViewById(R.id.list);
        loadItems();
        return v;
    }
    public void loadItems() {
        list.add("Automatic");
        list.add("SemiAutomatic");
        list.add("Manual");
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Main.showToast(getActivity(), list.get(position)+"");

                if (position==0)
                    getFragmentManager().beginTransaction().replace(android.R.id.content,new AutomaticDetails()).commit();
                if (position==1)
                    getFragmentManager().beginTransaction().replace(android.R.id.content,new SemiAutomaticDetails()).commit();
                if (position==2)
                    getFragmentManager().beginTransaction().replace(android.R.id.content,new ManualDetails()).commit();
            }
        });
    }
}
