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
public class RuleListFragment extends Fragment {

    ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_of_rule, container, false);
//        listView = (ListView)v.findViewById(android.R.id.appList);
        listView = (ListView)v.findViewById(R.id.list);
        loadItems();
        return v;
    }
    public void loadItems() {
        list.add("Automatic");
        list.add("SemiAutomatic");
        list.add("Manual");
        list.add("Notification");
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0)
                    getFragmentManager().beginTransaction().replace(android.R.id.content,new AutomaticRuleFragment()).commit();
                if (position==1)
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new SemiAutomaticRuleFragment()).commit();
                if (position==2)
                    getFragmentManager().beginTransaction().replace(android.R.id.content,new ManualRuleFragment()).commit();
                if (position==3)
                    getFragmentManager().beginTransaction().replace(android.R.id.content,new NotificationFragment()).commit();
            }
        });
    }
}
