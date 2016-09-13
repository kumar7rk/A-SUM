package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geeky7.rohit.location.Main;

import java.util.ArrayList;

public class Automatic extends Service {
    Main m;
    ArrayList<String> listOfBlockedApps = new ArrayList<>();

    public Automatic() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(Automatic.this, SemiAutomatic.class));
        stopService(new Intent(Automatic.this, Manual.class));
        Main.showToast(getApplicationContext(), "Automatic Service started");

        m = new Main(getApplicationContext());
    }

    private void blockAppLaunched() {
        String currentApp = m.getForegroungApp();
        getListOfBlockedApplications();
        if (listOfBlockedApps.contains(currentApp))
            m.showHomeScreen();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        blockAppLaunched();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Main.showToast(getApplicationContext(), "AutomaticServiceDestroyed");
    }
    public void getListOfBlockedApplications(){
        listOfBlockedApps.add("com.desarrollodroide.repos");
        listOfBlockedApps.add("com.boopathy.raja.tutorial");
        listOfBlockedApps.add("com.fivasim.androsensor");
        listOfBlockedApps.add("com.touchboarder.android.api.demos");
        listOfBlockedApps.add("com.google.samples.apps.cardboarddemo");
        listOfBlockedApps.add("com.microsoft.skydrive");

    }
}
