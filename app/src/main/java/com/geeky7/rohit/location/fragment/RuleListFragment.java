package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.activity.MainActivity;

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
        listView = (ListView)v.findViewById(R.id.list);
        loadItems();
        setActionBarTitle("Rules");
        return v;
    }
    public void loadItems() {
        list.add(getString(R.string.automatic));
        list.add(getString(R.string.semi_automatic));
        list.add(getString(R.string.manual));
        list.add(getString(R.string.notification));
        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    addFragment(new AutomaticRuleFragment());
                    setActionBarTitleForRules("Automatic Rule");
                }
                if (position==1){
                    addFragment(new SemiAutomaticRuleFragment());
                    setActionBarTitleForRules("SemiAutomatic Rule");
                }
                if (position==2){
                    addFragment(new ManualRuleFragment());
                    setActionBarTitleForRules("Manual Rule");
                }
                if (position==3){
                    addFragment(new NotificationFragment());
                    setActionBarTitleForRules("Notification Rule");
                }
            }
        });
    }

    private void addFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(android.R.id.content,fragment)/*.addToBackStack(null)*/.commit();
    }

    private void setActionBarTitle(String title){
        ((MainActivity) getActivity()).setActionBarTitle(title);
    }

    public void setActionBarTitleForRules(String title){
        ((MainActivity) getActivity()).setActionBarTitleForRules(title);
    }
}
