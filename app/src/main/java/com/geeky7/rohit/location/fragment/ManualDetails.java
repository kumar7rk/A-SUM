package com.geeky7.rohit.location.fragment;

import android.app.ListFragment;
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

import com.geeky7.rohit.location.adapter.ApplicationAdapter;
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
public class ManualDetails extends ListFragment {
    ListView listView;
    private ArrayAdapter<String> adapter;
    SharedPreferences preferences;
    Main m;

    ArrayList<String> packageList = new ArrayList<String>();
    ArrayList<String> appList = new ArrayList<String>();

    Set<String> selectedAppsSet = new HashSet<String>();
    Set<String> selectedAppsSetIndex = new HashSet<String>();


    private PackageManager packageManager = null;
    private ApplicationAdapter listadapter = null;
    private List<ApplicationInfo> applist = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        packageManager = MyApplication.getAppContext().getPackageManager();
        super.onCreate(savedInstanceState);
    }

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
        loadItems();
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
                ApplicationInfo applicationInfo = (ApplicationInfo) parent.getItemAtPosition(position);
                Main.showToast(applicationInfo.loadLabel(packageManager) + "");
                String selectedAppS = packageList.get(position);
                selectedAppsSet.add(selectedAppS);
                selectedAppsSetIndex.add(position + "");
            }
        });
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule, container, false);
        listView = (ListView)v.findViewById(android.R.id.list);

        m = new Main(getActivity());

//        adapter = getInstalledApplicationsv2();
//        setListAdapter(adapter);
//        adapter.notifyDataSetChanged();

        appSelected();
        setHasOptionsMenu(true);
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
            case R.id.action_save:
                SharedPreferences.Editor editor = preferences.edit();

                editor.putStringSet("manualApps", selectedAppsSet);
                editor.putStringSet("manualAppsIndex", selectedAppsSetIndex);

                editor.commit();

                Main.showToast(getActivity(), "ManualAppBlockListUpdated");

                getActivity().getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new MonitoringFragmentCardView()).commit();
        }
                return super.onOptionsItemSelected(item);
    }
    public void loadItems(){
        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        listadapter = new ApplicationAdapter(MyApplication.getAppContext(),
                R.layout.manual_listitems, applist);
        setListAdapter(listadapter);
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
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
                android.R.layout.simple_list_item_checked, appList);
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_multichoice, appList);
        return adapter;
    }
}
