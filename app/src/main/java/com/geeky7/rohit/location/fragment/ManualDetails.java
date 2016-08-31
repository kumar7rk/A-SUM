package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

import java.util.ArrayList;

/**
 * Created by Rohit on 30/08/2016.
 */
public class ManualDetails extends Fragment {
    ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();

    Main m;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppSelected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppSelected();
    }

    private void AppSelected() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Main.showToast("Yay" + list.get(position) + "has been selected to be blocked");
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule, container, false);
        m = new Main(getActivity());
        listView = (ListView)v.findViewById(R.id.listView);
        setHasOptionsMenu(true);

        adapter = m.getInstalledApplications();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.application_list_edit,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Main.showToast(getActivity(), "saved");
//                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                getActivity().getFragmentManager().popBackStack();

        }
                return super.onOptionsItemSelected(item);
    }
}
