package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.geeky7.rohit.location.Main;

import java.util.HashSet;
import java.util.Set;

public class Manual extends Service {
    Set<String> listOfBlockedApps = new HashSet<String>();
    Main m;
    SharedPreferences preferences;

    public Manual() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(Manual.this, Automatic.class));
        stopService(new Intent(Manual.this, SemiAutomatic.class));
        Main.showToast(getApplicationContext(), "Manual Service started");
        m = new Main(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        blockAppLaunched();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Main.showToast(getApplicationContext(), "ManualServiceDestroyed");
    }
    private void blockAppLaunched() {
        String currentApp = m.getForegroungApp();
        getListOfBlockedApplications();
        if (listOfBlockedApps.contains(currentApp))
            m.showHomeScreen();
    }
    public void getListOfBlockedApplications(){
        listOfBlockedApps = preferences.getStringSet("someStringSet", listOfBlockedApps);
    }
}
