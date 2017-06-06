package com.geeky7.rohit.location.service;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.geeky7.rohit.location.AdminReceiver;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.activity.ThreeTabsActivity;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Walking extends IntentService {

    private String TAG = "ActivityDetected";
    private DevicePolicyManager mgr=null;
    private ComponentName cn=null;
    public int NOTIFICATION_ID;
    public boolean walking;
    public Walking() {
        super("Walking");
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Main.showToast("Walking IntentService created");
        cn=new ComponentName(this, AdminReceiver.class);
        mgr=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        walking = sharedPrefs.getBoolean(getResources().getString(R.string.walking), false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Main.showToast("WalkingServiceDestroyed");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleActivity(result.getProbableActivities());
        }

    }

    private void handleActivity(List<DetectedActivity> probableActivities) {
        for (DetectedActivity detectedActivity : probableActivities){
            switch (detectedActivity.getType()){
                case DetectedActivity.WALKING:
                    Log.i(TAG, "Walking " + detectedActivity.getConfidence());
                    //Main.showToast("Walking " + detectedActivity.getConfidence());
                    //createNotification("you were walking", detectedActivity.getConfidence() + "", getApplicationContext());
                    if (detectedActivity.getConfidence()>55 && walking){
                        createNotification("Warning! You were walking", "For safety reason we locked you phone", getApplicationContext());
                        lockMeNow(ThreeTabsActivity.view);

                    }
                    break;
                case DetectedActivity.STILL:
                    Log.i(TAG, "Standing " + detectedActivity.getConfidence());
//                    createNotification("You are still", detectedActivity.getConfidence() + "", getApplicationContext());
//                    unlockMeNow(MainActivity.view);
                    break;
            }
        }
    }
    public void createNotification(String contentTitle, String contentText,Context context) {

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText);
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID++, builder.build());
    }
    public void lockMeNow(View v) {
        if (mgr.isAdminActive(cn)) {
            mgr.lockNow();
        }
        else {
            Intent intent=
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_explanation));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    public void unlockMeNow(View v){
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock kl = km .newKeyguardLock("MyKeyguardLock");
        kl.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
        wakeLock.acquire();
    }
}
