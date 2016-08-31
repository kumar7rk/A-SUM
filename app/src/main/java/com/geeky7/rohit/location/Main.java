package com.geeky7.rohit.location;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.geeky7.rohit.location.activity.ThreeTabsActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Rohit on 9/08/2016.
 */
public class Main {

    private final int permissionVariable = 0;
    Context mContext;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    public Main(Context mContext) {
        this.mContext = mContext;
    }

    public static String getMethodName(final int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[1 + depth].getMethodName();
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showToast(String text) {
        Toast.makeText(MyApplication.getAppContext(), text, Toast.LENGTH_LONG).show();
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

    public String getForegroungApp() {
        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                Log.i("Size", appList.size() + "");
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        return currentApp;
    }

    public ArrayAdapter<String> getInstalledApplications() {
        ArrayAdapter<String> adapter;
        ArrayList<String> list = new ArrayList<String>();
        List<PackageInfo> packList = mContext.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                list.add(appName);
                Drawable appIcon = mContext.getPackageManager().getApplicationIcon(packInfo.applicationInfo);
                appIcon.setBounds(0, 0, 40, 40);

                //holder.apkName.setCompoundDrawables(appIcon, null, null, null);
                Log.e("App ? " + Integer.toString(i), appName);
            }
        }
        adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.select_dialog_multichoice, list);
        return adapter;
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                runThisCode();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };
    public void startRepeatingTask() {
        mStatusChecker.run();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
    public void runThisCode(){

    }
    public boolean showHomeScreen(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(startMain);
        return true;
    }
}