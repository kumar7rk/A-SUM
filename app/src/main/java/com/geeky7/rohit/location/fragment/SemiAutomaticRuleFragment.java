package com.geeky7.rohit.location.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.DataObject;
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.adapter.SemiAutomaticRecyclerViewAdapter;

import java.util.ArrayList;

public class SemiAutomaticRuleFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    TextView textView;

    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> appList = new ArrayList<String>();
    ArrayList<String> listOfBlockedApps = new ArrayList<>();

    AlertDialog dialog;

    Drawable[] drawables = new Drawable[10];

    SharedPreferences preferences;

    LinearLayout layout, layout1, layout2; // layout - icon, layout1,2 - off-time text and seekbar

    public SemiAutomaticRuleFragment() {
    }

    //customising seekbar view
    //setting the chosen off-time from the sharedPreferences
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        textView = new TextView(MyApplication.getAppContext());
        String off_time = preferences.getString(CONSTANTS.SEMIAUTOMATIC_SEEKBAR_PROGRESS_OFF_TIME,"0");
        textView.setText(off_time+" mins");
        textView.setTextColor(Color.GREEN);
        textView.setTextSize(20);
    }

    //setting the adapter
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.semiautomatic_recycler_view, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);
        layout = (LinearLayout) v.findViewById(R.id.linear);
        layout1 = (LinearLayout) v.findViewById(R.id.linear1);
        layout2 = (LinearLayout) v.findViewById(R.id.linear2);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SemiAutomaticRecyclerViewAdapter(getDataSet());

        mRecyclerView.setAdapter(mAdapter);
        return v;

    }
    //setting a dialog for seek bar view - not sure why
    //maybe for conveying the meaning of off-time
    //after all it's a developer rule ;)
    @Override
    public void onResume() {
        super.onResume();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Set New Off-Time")
                .setTitle("Off-time");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.create();
        ((SemiAutomaticRecyclerViewAdapter) mAdapter).setOnItemClickListener(new SemiAutomaticRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                if (position == 2) {
//                    dialog.show();
                }
                if (position == 3)
                    Main.showToast(getResources().getString(R.string.coming_soon));
            }
        });
    }
    //Dealing with data sets and setting values for the titles
    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        ArrayList<String> arrayList = new ArrayList<>();

        String lastApplied = preferences.getString(CONSTANTS.SEMIAUTOMATIC_RULE_ADDED_TIME, "Never");
        String timePeriod = "This rule is applied for ALL DAY. No quiet Hours";

        addValues();
        getListOfBlockedApplications();
        addAppIcons();

        arrayList.add(lastApplied);
        arrayList.add(timePeriod);
        arrayList.add("");
        arrayList.add("");

        for (int index = 0; index < list.size(); index++) {

            DataObject obj = new DataObject(list.get(index),
                        arrayList.get(index));
            if (index==2)
                addSeekBar();
            if (index == 3)
                obj = new DataObject(list.get(index));
            results.add(index, obj);
        }
        return results;
    }

    //adding seekbar to choose an off-tine between 5 and 60 mins in increments of 5 mins
   public void addSeekBar(){
       SeekBar seekBar = new SeekBar(MyApplication.getAppContext());
       seekBar.setMax(12);
       seekBar.incrementProgressBy(5);

       seekBar.setVisibility(View.VISIBLE);
       String off_time = preferences.getString(CONSTANTS.SEMIAUTOMATIC_SEEKBAR_PROGRESS_OFF_TIME,"0");
       int offTime = Integer.parseInt(off_time);
       seekBar.setProgress(offTime/5);

       /*int match_parent = ViewGroup.LayoutParams.MATCH_PARENT;
       match_parent -= 20;*/
       LayoutParams lp = new LayoutParams(1300, 100);
       seekBar.setLayoutParams(lp);

       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

           public void onStopTrackingTouch(SeekBar arg0) {
           }

           public void onStartTrackingTouch(SeekBar arg0) {
           }

           public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
               int step = 5;
               int progress = arg1 * step;
               SharedPreferences.Editor editor = preferences.edit();
               editor.putString(CONSTANTS.SEMIAUTOMATIC_SEEKBAR_PROGRESS_OFF_TIME, progress + "").commit();
               textView.setText(progress + " mins");

           }
       });
       layout2.addView(textView);
       layout1.addView(seekBar);
    }
    // formatting and adding the blocked apps icons in the layout
    // the for loop is running for like 10 apps
    // which could be fine because it's developer rule
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

    //fetching app icons for the blocked applications
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
    //adding the card views titles
    private void addValues(){
        list.add("Last Applied");
        list.add("Time Period");
        list.add("Off-Time");
        list.add("Blocked Applications");

    }
    //manually added list of the applications that I think (;)) should be blocked
    // it's actually developer rule not automatic rule :D
    public void getListOfBlockedApplications(){

        if(appInstalledOrNot("com.whatsapp")) listOfBlockedApps.add("com.whatsapp");
        if(appInstalledOrNot("com.facebook.katana")) listOfBlockedApps.add("com.facebook.katana");
        if(appInstalledOrNot("com.instagram.android")) listOfBlockedApps.add("com.instagram.android");
        if(appInstalledOrNot("com.snapchat.android")) listOfBlockedApps.add("com.snapchat.android");
    }
    //check if the manually inserted apps are installed or not for a user
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
