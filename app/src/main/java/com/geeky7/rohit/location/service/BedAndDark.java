package com.geeky7.rohit.location.service;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geeky7.rohit.location.Main;

/**
 * Created by Rohit on 4/05/2016.
 */
/* the service could not be stopped explicitly as per SO answers,but nothing works;
    Next approach might include to call the required method from the service class;
    10p8
  */
public class BedAndDark extends Service implements SensorEventListener{

    SensorManager mSensorManager;
    Sensor mPhotometer,mAccelerometer,mMagnetometer;
    SensorEvent event;

    SharedPreferences preferences;
    int max = 0;

    KeyguardManager keyguardManager;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private AlertDialog dialog;

    @Override
    public void onCreate() {
//        Main.showToast("BedAndDark Service created");
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        keyguardManager = (KeyguardManager)getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mPhotometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer= mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mPhotometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        Main.showToast("BedAndDarkServiceDestroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Thread t= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        if (event.sensor==mPhotometer) {
            int intValue = (int) event.values[0];
            if (intValue==0)
                Main.showToast("Stop using phone in darkness");
            max = max < intValue ? intValue : max;
        }
        if (event.sensor==mAccelerometer){
            System.arraycopy(event.values, 0, mAccelerometerReading, 0, mAccelerometerReading.length);
        }
        else if (event.sensor==mMagnetometer) {
            System.arraycopy(event.values, 0, mMagnetometerReading,0,mMagnetometerReading.length);
        }
        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        String s = (int) Math.toDegrees(mOrientationAngles[0]) + " " + (int) Math.toDegrees(mOrientationAngles[1]) + " " + (int) Math.toDegrees(mOrientationAngles[2])+"";
        Log.i("Angles",s);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("jp.ne.hardyinfinity.bluelightfilter.free.service.FilterService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

