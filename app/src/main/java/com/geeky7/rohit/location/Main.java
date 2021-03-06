package com.geeky7.rohit.location;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
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
    public static final String TAG = CONSTANTS.MAIN;

    Context mContext;

    //this is the interval for running the background service code- to check if a user is in a social scenario
    private int mInterval = 5000; // 5 seconds by default, can be changed later

    boolean restaurant,religious_place,movie_theatre,bedAndDark,walking;

    private Handler mHandler;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Main(Context mContext) {
        this.mContext = mContext;
    }

    public String getMethodName(final int depth) {
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

    //get the foreground app - I think this is the code on SO
    public String getForegroundApp() {
        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
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

    //get the list of installed applications and returns in an arrayAdapter
    public ArrayAdapter<String> getInstalledApplications() {
        ArrayAdapter<String> adapter;
        ArrayList<String> list = new ArrayList<String>();
        List<PackageInfo> packList = mContext.getPackageManager().getInstalledPackages(0);

        editor = preferences.edit();

        editor.putInt(CONSTANTS.NUMBER_OF_INSTALLED_APPS,packList.size()).apply();


        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                list.add(appName);
                Drawable appIcon = mContext.getPackageManager().getApplicationIcon(packInfo.applicationInfo);
                appIcon.setBounds(0, 0, 40, 40);

                //holder.apkName.setCompoundDrawables(appIcon, null, null, null);
            }
        }
        adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.select_dialog_multichoice, list);
        return adapter;
    }
    //Hey, I'm your cool runnable which runs every 5 seconds but only when required
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
    // I start the above runnable
    public void startRepeatingTask() {
        mStatusChecker.run();
    }

    //I stop the runnable
    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    //I block the app as they say it in English ;)
    //how to do this bit was a mystery for some time
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
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            mContext.startActivity(intent);
        }
    }
    //this is a second method to get a list of installed applications and return an arrayAdapter
    //I'm ~40k ft above in the air so next time you see this comment get rid of one of them
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
            }
        }
        adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.select_dialog_multichoice, list);
        return adapter;
    }
    //if the gps is not turned on, this method opens the location settings page
    public boolean openLocationSettings(LocationManager service) {
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
        return enabled;
    }
    // checks usageAccessPermissions and return a boolean
    public boolean usageAccessPermission(){
        AppOpsManager appOps = (AppOpsManager) mContext
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), mContext.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    //check how many scenarios are selected to show a badge on the bottombar monitoring fragment
    public void countSelectedScenarioForBadge() {
        int counter = 0;
        //not sure why I'm using two differnet SP. Test this the next time you see this comment
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        boolean restaurant = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.restaurant), false);
        boolean religious_place = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.religious_place), false);
        boolean movie_theatre = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.movie_theatre), false);
        boolean bedAndDark= sharedPrefs.getBoolean(mContext.getResources().getString(R.string.bed_dark), false);
        boolean walking = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.walking), false);

        if (restaurant) counter++;
        if (religious_place) counter++;
        if (movie_theatre) counter++;
        if (bedAndDark) counter++;
        if (walking) counter++;

        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CONSTANTS.NUMBER_OF_SCENARIOS_SELECTED,counter);
        editor.apply();
    }

    //this method get the values of scenarios - whether selected or not
    // used in the method below to check if any scenario is selected before running backgroundService code
    public void initialValues(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());

        restaurant = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.restaurant), false);
        religious_place = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.religious_place), false);
        movie_theatre = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.movie_theatre), false);
        bedAndDark= sharedPrefs.getBoolean(mContext.getResources().getString(R.string.bed_dark), false);
        walking = sharedPrefs.getBoolean(mContext.getResources().getString(R.string.walking), false);
    }
    //when the cancel button is clicked on select scenario activity
    //I think it's here to rollback when cancel button is clicked on the select scenario activity
    public void cancelOperation(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(mContext.getResources().getString(R.string.restaurant),restaurant);
        editor.putBoolean(mContext.getResources().getString(R.string.religious_place),religious_place);
        editor.putBoolean(mContext.getResources().getString(R.string.movie_theatre),movie_theatre);
        editor.putBoolean(mContext.getResources().getString(R.string.bed_dark),bedAndDark);
        editor.putBoolean(mContext.getResources().getString(R.string.walking), walking);
        editor.apply();
    }

    //only start fetching place name regularly if at least one scenario is selected
    public boolean isAnyScenarioSelected() {
        initialValues();
        if (restaurant||religious_place||movie_theatre) return true;
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

    //show user a dialog if there's a scenario selected but the usageAccessPermission is not granted
    // this only runs when the app runs from the icon and doesn't run on press back button :thumbsup
    public void showUsageDataAccessDialog(Activity activity){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle("Permission required")
                .setMessage(R.string.usage_data_access)
                .setIcon(R.drawable.permission_warning)
                .setPositiveButton(MyApplication.getAppContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        usageAccessSettingsPage();
                    }
                })
                .setNegativeButton(MyApplication.getAppContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    //this method checks if your service is running
    // I think it's usage was removed (bed and dark) as the code runs delighfully even without this piece of code
    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}