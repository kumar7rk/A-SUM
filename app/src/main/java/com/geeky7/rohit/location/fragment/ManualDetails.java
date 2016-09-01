package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rohit on 30/08/2016.
 */
public class ManualDetails extends Fragment {
    ListView listView;
    private ArrayAdapter<String> adapter;
    SharedPreferences preferences;
    Main m;
    ArrayList<String> packageList = new ArrayList<String>();
    ArrayList<String> appList = new ArrayList<String>();

    Set<String> selectedAppsSet = new HashSet<String>();
    Set<String> selectedAppsSetIndex = new HashSet<String>();


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appSelected();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> apps = new HashSet<String>();
        Set<String> appsIndex = new HashSet<String>();
        apps = preferences.getStringSet("manualApps", apps);
        appsIndex = preferences.getStringSet("manualAppsIndex", appsIndex);
//        selectedAppsSetIndex = appsIndex;
        for (String s:appsIndex){
            Integer in = Integer.parseInt(s);
//            listView.setItemChecked(in,true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appSelected();
    }

    private void appSelected() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedAppS = listView.getItemAtPosition(position)+"";
//                String selectedAppS = adapter.getItem(position);
                String selectedAppS = packageList.get(position);
                selectedAppsSet.add(selectedAppS);
                selectedAppsSetIndex.add(position + "");
            }
        });
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule, container, false);
        m = new Main(getActivity());
        listView = (ListView)v.findViewById(R.id.listView);
        setHasOptionsMenu(true);

        adapter = getInstalledApplicationsv2();

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        appSelected();
        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.application_list_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet("manualApps", selectedAppsSet);
                editor.putStringSet("manualAppsIndex", selectedAppsSetIndex);
                editor.commit();

//                editor.apply();
//                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
//                getActivity().getFragmentManager().popBackStack();
                Main.showToast(getActivity(), "ManualAppBlockListUpdated");
                getActivity().getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new MonitoringFragment());
        }
                return super.onOptionsItemSelected(item);
    }
    public ArrayAdapter<String> getInstalledApplicationsv2(){
        ArrayAdapter<String> adapter;
        final PackageManager pm = MyApplication.getAppContext().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String packageName = packageInfo.packageName + "";
                String appName = pm.getApplicationLabel(packageInfo) + "";
                packageList.add(packageName);
                appList.add(appName);
                Log.i("AppName ", packageName);
                Log.i("AppNamess", appName);
            }
        }
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_multichoice, appList);
        return adapter;
    }
}
