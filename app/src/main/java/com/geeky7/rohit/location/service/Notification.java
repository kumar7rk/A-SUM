package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class Notification extends Service {
    public Notification() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(Notification.this, Automatic.class));
        stopService(new Intent(Notification.this, SemiAutomatic.class));
        stopService(new Intent(Notification.this, Manual.class));

        Main.showToast(getApplicationContext(), "Notification Service started");
        pugNotification("A reminder","Hey! You've been detected in a social place",
                "Let's just not use the phone and enjoy the moment");
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Main.showToast(getApplicationContext(), "NotificationServiceDestroyed");

    }
    public void pugNotification(String title ,String message, String bigText){
    PugNotification.with(getApplicationContext())
            .load()
    .title(title)
    .message(message)
    .smallIcon(R.drawable.pugnotification_ic_launcher)
    .bigTextStyle(message+" "+bigText)
    .largeIcon(R.drawable.pugnotification_ic_launcher)
    .flags(android.app.Notification.DEFAULT_ALL)
    .simple()
    .build();
    }
}
