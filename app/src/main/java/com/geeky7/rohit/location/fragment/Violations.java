package com.geeky7.rohit.location.fragment;

import android.app.ListFragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.ViolationDbHelper;
import com.geeky7.rohit.location.adapter.ApplicationAdapter;

import java.util.ArrayList;
import java.util.List;


public class Violations extends ListFragment {

    private PackageManager packageManager = null;
    ViolationDbHelper violationDbHelper;
//    ListView listView;
    private ApplicationAdapter listadapter = null;
    private List<ApplicationInfo> applist = null;
    Drawable[] drawables;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        packageManager = MyApplication.getAppContext().getPackageManager();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_violations, container, false);
        violationDbHelper = new ViolationDbHelper(MyApplication.getAppContext());
//        listView = (ListView)v.findViewById(R.id.listView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadItems();
    }

    public void loadItems() {
        ArrayList<String> appNames = violationDbHelper.fetchAll();
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, appNames);
        for (int i = 0; i < appNames.size(); i++) {
            try {
                drawables[i] = MyApplication.getAppContext().getPackageManager().
                        getApplicationIcon(appNames.get(i));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        setListAdapter(adapter);
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
}
