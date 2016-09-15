package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.ViolationDbHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Automatic extends Service {
    Main m;
    ArrayList<String> listOfBlockedApps = new ArrayList<>();
    private Handler mHandler;

    ViolationDbHelper violationDbHelper;
    SharedPreferences preferences;

    public Automatic() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(Automatic.this, SemiAutomatic.class));
        stopService(new Intent(Automatic.this, Manual.class));
        violationDbHelper = new ViolationDbHelper(getApplicationContext());
        mHandler = new Handler();
        m = new Main(getApplicationContext());
        startRepeatingTask();
        Main.showToast(getApplicationContext(), "Automatic Service started");
    }

    private void blockAppLaunched() {
        String currentApp = m.getForegroungApp();
        getListOfBlockedApplications();

        ViolationDbHelper violationDbHelper = new ViolationDbHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = violationDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        PackageManager packageManager= getApplicationContext().getPackageManager();
        String appName = "";

        try {
            appName = (String) packageManager.getApplicationLabel
                    (packageManager.getApplicationInfo(currentApp, PackageManager.GET_META_DATA));
            Drawable icon = packageManager.getApplicationIcon(packageManager.getApplicationInfo(currentApp, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (listOfBlockedApps.contains(currentApp)){
            m.showHomeScreen();
            contentValues.put("AppName", appName);
            sqLiteDatabase.insert("Violation", null, contentValues);
            preferences = PreferenceManager.getDefaultSharedPreferences(this) ;
            SharedPreferences.Editor editor = preferences.edit();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            editor.putString(CONSTANTS.VIOLATION_TIME,currentDateTimeString).commit();
        }
        sqLiteDatabase.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        blockAppLaunched();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopRepeatingTask();
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Main.showToast("AutomaticServiceDestroyed");
        stopRepeatingTask();
    }
    public void getListOfBlockedApplications(){
        listOfBlockedApps.add("com.touchboarder.android.api.demos");
        listOfBlockedApps.add("com.agup.gps");
        listOfBlockedApps.add("de.dfki.appdetox");
        listOfBlockedApps.add("com.desarrollodroide.repos");
        listOfBlockedApps.add("com.geeky7.rohit.listview");
        listOfBlockedApps.add("com.example.listview");
        listOfBlockedApps.add("com.example.rohit.myapplication");
        listOfBlockedApps.add("com.mycompany12.mytwelthapptopicsfinal");
        listOfBlockedApps.add("com.geeky7.rohit.locations");
        listOfBlockedApps.add("com.geeky7.rohit.lostphone");
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                blockAppLaunched();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mHandler.postDelayed(mStatusChecker, 500);
            }
        }
    };

    public void startRepeatingTask() {
        mStatusChecker.run();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
