package com.geeky7.rohit.location.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.TabMessage;
import com.geeky7.rohit.location.ViolationDbHelper;
import com.geeky7.rohit.location.fragment.MonitoringFragment;
import com.geeky7.rohit.location.fragment.Rules;
import com.geeky7.rohit.location.fragment.Violations;
import com.geeky7.rohit.location.fragment.list_of_rule;
import com.geeky7.rohit.location.service.Automatic;
import com.geeky7.rohit.location.service.BackgroundService;
import com.geeky7.rohit.location.service.Manual;
import com.geeky7.rohit.location.service.SemiAutomatic;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;


public class ThreeTabsActivity extends AppCompatActivity {

    private BottomBar mBottomBar;
    private final int permissionVariable = 0;
    boolean running,a,mainSwitch = true;
    Main m;
    public static View view;
    SharedPreferences preferences;
    Switch aSwitch;
    MenuItem toggleService;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        m = new Main(this);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThreeTabsActivity.this,Configure.class));
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mainSwitch =  preferences.getBoolean(CONSTANTS.MAIN_SWITCH, mainSwitch);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        m.openLocationSettings(manager);
        m.usageAccessSettingsPage();
        checkPermission();

        if (!running && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mainSwitch)
            startService();

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.main);

        updateBadge();
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {

            @Override
            public void onMenuTabSelected(int menuItemId) {
                Fragment fragment = new Rules();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                switch (menuItemId) {
                    case R.id.bb_menu_monitoring:
                        fragment = new MonitoringFragment();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;
                    case R.id.bb_menu_rules:
                        fragment = new list_of_rule();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;
                    case R.id.bb_menu_violation:
                        fragment = new Violations();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;
                    default:
                        fragmentTransaction.remove(fragment);
                }
            }

            @Override
            public void onMenuTabReSelected(int menuItemId) {
                Toast.makeText(getApplicationContext(), TabMessage.get(menuItemId, true), Toast.LENGTH_SHORT).show();

                Fragment fragment = new Rules();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                switch (menuItemId) {

                    case R.id.bb_menu_monitoring:
                        fragment = new MonitoringFragment();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;

                    case R.id.bb_menu_rules:
                        fragment = new list_of_rule();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;

                    case R.id.bb_menu_violation:
                        fragment = new Violations();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;

                    default:
                        fragmentTransaction.remove(fragment);
                }
            }
        });
    }

    public void updateBadge() {
        int redColor = Color.parseColor("#FF0000");
        int scenariosSelected = Integer.MAX_VALUE;
        int violations = Integer.MAX_VALUE;
        m = new Main(this);
        ViolationDbHelper violationDbHelper = new ViolationDbHelper(getApplicationContext());
        m.countSelectedScenarioForBadge();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.main_switch:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        editorMainSwitch();
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        toggleService = menu.findItem(R.id.main_switch);
        View view = MenuItemCompat.getActionView(toggleService);
        aSwitch = (Switch) view.findViewById(R.id.a_switch);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mainSwitch =  preferences.getBoolean(CONSTANTS.MAIN_SWITCH, mainSwitch);

        aSwitch.setChecked(mainSwitch);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editorMainSwitch();
                if (isChecked) {
                    if (!running) {
                        startService();
                        running = true;
                    }
//                    Main.showToast("isChecked");
                }
                if (!isChecked){
                    Intent serviceIntent = new Intent(ThreeTabsActivity.this, BackgroundService.class);
                    if (running){
                        stopService(serviceIntent);
                        stopService(new Intent(ThreeTabsActivity.this, Automatic.class));
                        stopService(new Intent(ThreeTabsActivity.this, SemiAutomatic.class));
                        stopService(new Intent(ThreeTabsActivity.this, Manual.class));
                        running = false;
                    }
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void editorMainSwitch() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CONSTANTS.MAIN_SWITCH, aSwitch.isChecked());
        editor.commit();
    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.PACKAGE_USAGE_STATS},
                permissionVariable);

    }
    @Override
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
    private void startService() {
        Intent serviceIntent = new Intent(getApplicationContext(), BackgroundService.class);
        startService(serviceIntent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Fragment fragment;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragment = new MonitoringFragment();
        fragmentTransaction.replace(android.R.id.content, fragment).commit();

    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onPause();
    }
}