package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.geeky7.rohit.location.DataObject;
import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.activity.MainActivity;
import com.geeky7.rohit.location.adapter.AutomaticRecyclerViewAdapter;

import java.util.ArrayList;

public class AutomaticRuleFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> appList = new ArrayList<String>();
    ArrayList<String> listOfBlockedApps = new ArrayList<>();

    Drawable[] drawables = new Drawable[11];
    SharedPreferences preferences;
    LinearLayout layout;

    public AutomaticRuleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setbackArrow();
    }

    private void setbackArrow() {
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.automatic_recycler_view, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);
        layout = (LinearLayout) v.findViewById(R.id.linear);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AutomaticRecyclerViewAdapter(getDataSet());

        mRecyclerView.setAdapter(mAdapter);
        return v;

    }
    @Override
    public void onResume() {
        super.onResume();
        ((AutomaticRecyclerViewAdapter) mAdapter).setOnItemClickListener(new AutomaticRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (position == 2)
                    Main.showToast(getResources().getString(R.string.coming_soon));
            }
        });
    }
    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        String lastApplied = preferences.getString(CONSTANTS.AUTOMATIC_RULE_ADDED_TIME, "Never Applied");
        String timePeriod = "This rule is applied for ALL DAY. No quiet Hours";
        String apps = "someApps";
        addValues();
        getListOfBlockedApplications();
        addAppIcons();

        for (int index = 0; index < list.size(); index++) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(lastApplied);
            arrayList.add(timePeriod);
            arrayList.add(apps);

            DataObject obj = new DataObject(list.get(index),
                        arrayList.get(index));
            if (index == 2)
                obj = new DataObject(list.get(index));
            results.add(index, obj);
        }
        return results;
    }

    private void addAppIcons() {
        getAppIcon();
        for (int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(MyApplication.getAppContext());
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageDrawable(drawables[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120,120);
            layoutParams.setMargins(10,0,0,0);
            imageView.setLayoutParams(layoutParams);
            imageView.requestLayout();
            layout.addView(imageView);
        }
    }

    private void getAppIcon() {
        for (int i = 0; i < listOfBlockedApps.size(); i++) {
            try {
                drawables[i] = MyApplication.getAppContext().getPackageManager().
                        getApplicationIcon(listOfBlockedApps.get(i));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addValues(){
        list.add("Last Applied");
        list.add("Time Period");
        list.add("Blocked Applications");
    }
    public void getListOfBlockedApplications(){

        if(appInstalledOrNot("com.whatsapp")) listOfBlockedApps.add("com.whatsapp");
        if(appInstalledOrNot("com.facebook.katana")) listOfBlockedApps.add("com.facebook.katana");
        if(appInstalledOrNot("com.instagram.android")) listOfBlockedApps.add("com.instagram.android");
        if(appInstalledOrNot("com.snapchat.android")) listOfBlockedApps.add("com.snapchat.android");
    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = MyApplication.getAppContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
