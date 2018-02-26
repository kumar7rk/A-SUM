package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(NotificationService.this, AutomaticService.class));
        stopService(new Intent(NotificationService.this, SemiAutomatic.class));
        stopService(new Intent(NotificationService.this, ManualService.class));

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
