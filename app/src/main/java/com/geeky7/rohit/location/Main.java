package com.geeky7.rohit.location;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Rohit on 9/08/2016.
 */
public class Main {

    Context mContext;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    SharedPreferences preferences;
    boolean restaurant,religious_place,movie_theatre,bedAndDark,walking;

    private Handler mHandler;
    public Main(Context mContext) {
        this.mContext = mContext;
    }

    public static String getMethodName(final int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[1 + depth].getMethodName();
    }

    // for testing requirements; omits need to comment individually
    public static void showToast(Context context, String text) {
//        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    // required in google play version
    public static void showToast(String text) {
        Toast.makeText(MyApplication.getAppContext(), text, Toast.LENGTH_SHORT).show();
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
            Main.showToast("Please grant Usage data access permission for app to function properly");
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
    public int countSelectedScenarioForBadge() {
        int counter = 0;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        boolean restaurant = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.restaurant), false);
        boolean religious_place = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.religious_place), false);
        boolean movie_theatre = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.movie_theatre), false);
        boolean bedAndDark= sharedPrefs.getBoolean(mContext.getResources().getString(R.string.bed_dark), false);
        boolean walking = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.walking), false);

        if (restaurant)
            counter++;
        if (religious_place)
            counter++;
        if (movie_theatre)
            counter++;
        if (bedAndDark)
            counter++;
        if (walking)
            counter++;

        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CONSTANTS.NUMBER_OF_SCENARIOS_SELECTED,counter);
        editor.apply();
        return counter;
    }

    public void initialValues(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());

        restaurant = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.restaurant), false);
        religious_place = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.religious_place), false);
        movie_theatre = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.movie_theatre), false);
        bedAndDark= sharedPrefs.getBoolean(mContext.getResources().getString(R.string.bed_dark), false);
        walking = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.walking), false);
    }
    public void cancelOperation(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(mContext.getResources().getString(R.string.restaurant),restaurant);
        editor.putBoolean(mContext.getResources().getString(R.string.religious_place),religious_place);
        editor.putBoolean(mContext.getResources().getString(R.string.movie_theatre),movie_theatre);
        editor.putBoolean(mContext.getResources().getString(R.string.bed_dark),bedAndDark);
        editor.putBoolean(mContext.getResources().getString(R.string.walking), walking);
        editor.commit();
    }

    //only start fetching place name regularly if atleast one scenario is selected
    public boolean isAnyScenarioSelected() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);


        initialValues();
        /*boolean restaurant = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.restaurant), false);
        boolean religious_place = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.religious_place), false);
        boolean movie_theatre = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.movie_theatre), false);*/

        if (restaurant||religious_place||movie_theatre)
            return true;

        return false;
    }

    // inner stuff for debugging
    public void updateLog(String className, String text){
        Log.i(className,text);
    }
    // for called method name
    public void calledMethodLog(String className, String text){
        Log.i(className,text);
    }
}