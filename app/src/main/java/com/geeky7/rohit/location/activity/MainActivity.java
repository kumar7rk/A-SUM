package com.geeky7.rohit.location.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.ViolationDbHelper;
import com.geeky7.rohit.location.fragment.MonitoringFragmentCardView;
import com.geeky7.rohit.location.fragment.RuleListFragment;
import com.geeky7.rohit.location.fragment.ViolationsFragment;
import com.geeky7.rohit.location.service.AutomaticService;
import com.geeky7.rohit.location.service.BackgroundService;
import com.geeky7.rohit.location.service.ManualService;
import com.geeky7.rohit.location.service.NotificationService;
import com.geeky7.rohit.location.service.SemiAutomaticService;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;


public class MainActivity extends AppCompatActivity {

    private BottomBar mBottomBar;

    private static final String TAG = CONSTANTS.MAIN_ACTIVITY;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    boolean running,mainSwitch = true;

    Main m;
    SharedPreferences preferences;

    public static View view;
    Switch aSwitch;
    MenuItem toggleService;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        m = new Main(this);
        m.calledMethodLog(TAG,m.getMethodName(2));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        // set onClickListener for floating action button
        // which opens a new activity with a list of scenarios
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, SelectScenarioActivity.class),1);
            }
        });
        // checking if the service is on or off
        mainSwitch =  preferences.getBoolean(CONSTANTS.MAIN_SWITCH, mainSwitch);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // check location permission
        if (!checkPermissions()) requestPermissions();


        if (!running && gps && mainSwitch) startService();

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.main);
        updateBadge();
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(int menuItemId) {
                Fragment fragment = new RuleListFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                switch (menuItemId) {
                    case R.id.bb_menu_monitoring:
                        fragment = new MonitoringFragmentCardView();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;
                    case R.id.bb_menu_rules:
                        fragment = new RuleListFragment();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;
                    case R.id.bb_menu_violation:
                        fragment = new ViolationsFragment();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;
                    default:
                        fragmentTransaction.remove(fragment);
                }
            }
            @Override
            public void onMenuTabReSelected(int menuItemId) {
                Fragment fragment = new RuleListFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                switch (menuItemId) {
                    case R.id.bb_menu_monitoring:
                        fragment = new MonitoringFragmentCardView();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;

                    case R.id.bb_menu_rules:
                        fragment = new RuleListFragment();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;

                    case R.id.bb_menu_violation:
                        fragment = new ViolationsFragment();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;

                    default:
                        fragmentTransaction.remove(fragment);
                }
            }
        });
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, "#fff000");
        mBottomBar.mapColorForTab(2, "#fff000");

        //m.openLocationSettings(manager);
    }

    public void updateBadge() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        int redColor = Color.parseColor("#FF0000");
        int scenariosSelected = 0;
        int violations = 0;
        ViolationDbHelper violationDbHelper = new ViolationDbHelper(getApplicationContext());
        m.countSelectedScenarioForBadge();

        scenariosSelected = preferences.getInt(CONSTANTS.NUMBER_OF_SCENARIOS_SELECTED, scenariosSelected);

        BottomBarBadge monitoringBadge = mBottomBar.makeBadgeForTabAt(0, redColor, scenariosSelected);
        monitoringBadge.setAutoShowAfterUnSelection(true);

        violations = preferences.getInt(CONSTANTS.NUMBER_OF_VIOLATIONS,violations);
        violationDbHelper.fetchAll();
        BottomBarBadge violationBadge = mBottomBar.makeBadgeForTabAt(2, redColor, violations);
        violationBadge.setAutoShowAfterUnSelection(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        m.calledMethodLog(TAG,m.getMethodName(2));
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        m.calledMethodLog(TAG,m.getMethodName(2));
        getMenuInflater().inflate(R.menu.main_menu, menu);
        toggleService = menu.findItem(R.id.main_switch);
        View view = MenuItemCompat.getActionView(toggleService);
        aSwitch = (Switch) view.findViewById(R.id.a_switch);

        boolean m_switch =  preferences.getBoolean(CONSTANTS.MAIN_SWITCH, false);

        aSwitch.setChecked(m_switch);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editorMainSwitch();
                if (isChecked) {
                    if (!running) {
                        startService();
                        running = true;
                        aSwitch.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        aSwitch.getTrackDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    }
                } else if (!isChecked) {
                    Intent serviceIntent = new Intent(MainActivity.this, BackgroundService.class);
                    if (running) {
                        aSwitch.getThumbDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                        aSwitch.getTrackDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

                        stopService(new Intent(MainActivity.this, AutomaticService.class));
                        stopService(new Intent(MainActivity.this, SemiAutomaticService.class));
                        stopService(new Intent(MainActivity.this, ManualService.class));
                        stopService(new Intent(MainActivity.this, NotificationService.class));

                        stopService(serviceIntent);

                        running = false;
                    }
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void editorMainSwitch() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CONSTANTS.MAIN_SWITCH, aSwitch.isChecked());
        editor.apply();
    }

    private boolean checkPermissions() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        int locationPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return locationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        boolean shouldProvideRationaleLocation =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        m = new Main(getApplicationContext());

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.

        // if either location or SMS or contacts permission is not granted; request
        if (shouldProvideRationaleLocation) {
            m.updateLog(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startPermissionRequest();
                        }
                    });
        } else {
            m.updateLog(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startPermissionRequest();
        }
    }
    // This method is called the first time the app is installed
    // requests all the permissions stated here
    private void startPermissionRequest() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }
    // this is the important bit which checks if the permission is granted or not
    // and therefore change your functionality accordingly
    // this method sets the values of the boolean variable for location, contact and sms and store them in sharedPreference
    // although it is not the right practice because if a user revokes the permission then these variables are not updated
    // and can therefore cause crash
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        m.calledMethodLog(TAG,m.getMethodName(2));
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                m.updateLog(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    /*&& grantResults[1] == PackageManager.PERMISSION_GRANTED*/) {
                // Permission granted.
            }
            if (grantResults[0] == PackageManager.PERMISSION_DENIED /*||
                    grantResults[1] == PackageManager.PERMISSION_DENIED*/) {
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

                showSnackbar(R.string.permission_rationale, android.R.string.ok,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
                                startPermissionRequest();
                            }
                        });
            }
            boolean firstTime = preferences.getBoolean(CONSTANTS.APP_OPENED_FIRST_TIME,true);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final SharedPreferences.Editor editor = preferences.edit();
            if (firstTime){
                m.showUsageDataAccessDialog(this);
                editor.putBoolean(CONSTANTS.APP_OPENED_FIRST_TIME,false).apply();
            }
        }
    }
    private void startService() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        Intent serviceIntent = new Intent(getApplicationContext(), BackgroundService.class);
        startService(serviceIntent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        m.calledMethodLog(TAG,m.getMethodName(2));
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        super.onResume();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        m.calledMethodLog(TAG,m.getMethodName(2));
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==0){
            getFragmentManager().beginTransaction().replace(android.R.id.content,new MonitoringFragmentCardView())
                    .commit();
        }
    }
    @Override
    protected void onPause() {
        m.calledMethodLog(TAG,m.getMethodName(2));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onPause();
    }
    // shows a Snackbar indefinitely (for permissions)
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        m.calledMethodLog(TAG,m.getMethodName(2));
        Snackbar.make(findViewById(R.id.coordinator),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}