package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geeky7.rohit.location.Main;

public class Automatic extends Service {
    public Automatic() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(Automatic.this, SemiAutomatic.class));
        stopService(new Intent(Automatic.this, Manual.class));
        Main.showToast(getApplicationContext(), "Automatic Service started");
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
}
