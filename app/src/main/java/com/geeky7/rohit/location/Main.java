package com.geeky7.rohit.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.geeky7.rohit.location.activity.ThreeTabsActivity;

/**
 * Created by Rohit on 9/08/2016.
 */
public class Main {

    private final int permissionVariable = 0;

    public static String getMethodName(final int depth)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[1+depth].getMethodName();
    }
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(MyApplication.getAppContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(new ThreeTabsActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                permissionVariable);
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permissionVariable: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;
            }
        }
    }

}
