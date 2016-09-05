package com.geeky7.rohit.location;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.geeky7.rohit.location.activity.ThreeTabsActivity;

import java.util.ArrayList;
import java.util.HashMap;
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
                Log.i("AppName " + Integer.toString(i), appName);
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
    public boolean showHomeScreen(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(startMain);
        return true;
    }
    //takes user to app's page in settings from which could be selected permission
    public void usageAccessSettingsPage(){
        if(!usageAccessPermission()){
            Main.showToast("Please grant this permission for app to function properly");
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            mContext.startActivity(intent);
        }
    }
    public ArrayAdapter<String> getInstalledApplicationsv2(){
        ArrayAdapter<String> adapter;
        ArrayList<String> list = new ArrayList<String>();
        HashMap<String,String> listHM = new HashMap<>();
        final PackageManager pm = MyApplication.getAppContext().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String packageName = packageInfo.packageName + "";
                String appName = pm.getApplicationLabel(packageInfo) + "";
                listHM.put(packageName, appName);
//                list.add(packageName);
//                list.add(appName);
                Log.i("AppName ", packageName);
                Log.i("AppNamess", appName);
            }
        }
        adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.select_dialog_multichoice, list);
        return adapter;
    }
    public boolean openLocationSettings(LocationManager service) {
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        }
        return enabled;
    }
    public boolean usageAccessPermission()
    {
        AppOpsManager appOps = (AppOpsManager) mContext
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), mContext.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }


}