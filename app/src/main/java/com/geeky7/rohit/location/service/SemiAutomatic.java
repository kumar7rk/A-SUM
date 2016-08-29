package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geeky7.rohit.location.Main;

public class SemiAutomatic extends Service {
    public SemiAutomatic() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(SemiAutomatic.this, Automatic.class));
        stopService(new Intent(SemiAutomatic.this, Manual.class));
        Main.showToast(getApplicationContext(), "SemiAutomatic Service started");
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Main.showToast(getApplicationContext(), "SemiAutomaticServiceDestroyed");
    }
}
