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

public class AutomaticService extends Service {
    Main m;
    ArrayList<String> listOfBlockedApps = new ArrayList<>();
    private Handler mHandler;

    ViolationDbHelper violationDbHelper;
    SharedPreferences preferences;

    public AutomaticService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(AutomaticService.this, SemiAutomaticService.class));
        stopService(new Intent(AutomaticService.this, ManualService.class));
        stopService(new Intent(AutomaticService.this, NotificationService.class));
        violationDbHelper = new ViolationDbHelper(getApplicationContext());
        mHandler = new Handler();
        m = new Main(getApplicationContext());
        startRepeatingTask();
        Main.showToast(getApplicationContext(),"Automatic Service started");
    }

    private void blockAppLaunched() {
        String currentApp = m.getForegroundApp();
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
            Main.showToast(appName +" is blocked.");
            sqLiteDatabase.insert("Violation", null, contentValues);
            preferences = PreferenceManager.getDefaultSharedPreferences(this) ;
            SharedPreferences.Editor editor = preferences.edit();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            editor.putString(CONSTANTS.VIOLATION_TIME,currentDateTimeString).apply();
        }
        sqLiteDatabase.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        Main.showToast(getApplicationContext(),"AutomaticServiceDestroyed");
        stopRepeatingTask();
    }
    public void getListOfBlockedApplications(){
        listOfBlockedApps.add("com.whatsapp");
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
