package com.geeky7.rohit.location;

import android.app.Notification;
import android.content.Context;

/**
 * Created by Rohit on 15/09/2016.
 */
public class CommonCode {
    public void createNotification(String contentTitle, String contentText,Context context) {

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText);
//        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        mNotificationManager.notify(NOTIFICATION_ID++, builder.build());
    }
}
