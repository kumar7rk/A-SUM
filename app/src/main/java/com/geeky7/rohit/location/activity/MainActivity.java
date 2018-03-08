package com.geeky7.rohit.location.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
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

import com.geeky7.rohit.location.BuildConfig;
import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.ViolationDbHelper;
import com.geeky7.rohit.location.fragment.MonitoringFragmentCardView;
import com.geeky7.rohit.location.fragment.ViolationsFragment;
import com.geeky7.rohit.location.fragment.RuleListFragment;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        m = new Main(this);

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

        /*mBottomBar = BottomBar.attach(this, savedInstanceState);
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
        mBottomBar.mapColorForTab(2, "#fff000");*/

        //m.openLocationSettings(manager);
    }

    public void updateBadge() {
        int redColor = Color.parseColor("#FF0000");
        int scenariosSelected = 0;
        int violations = 0;
        m = new Main(this);
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CONSTANTS.MAIN_SWITCH, aSwitch.isChecked());
        editor.apply();
    }

    private boolean checkPermissions() {
     //   m.calledMethodLog(TAG,"checkPermission");

        int locationPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int usageAccessPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.PACKAGE_USAGE_STATS);

        return locationPermission == PackageManager.PERMISSION_GRANTED &&
                usageAccessPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        m.calledMethodLog(TAG,"requestPermission");

        boolean shouldProvideRationaleLocation =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean shouldProvideRationaleUsageAccess =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.PACKAGE_USAGE_STATS);
        m = new Main(getApplicationContext());

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.

        // if either location or SMS or contacts permission is not granted; request
        if (shouldProvideRationaleLocation || shouldProvideRationaleUsageAccess) {
            m.updateLog(TAG, "Displaying permission rationale to provide additional context.");
            showAlertDialog(R.string.permission_rationale);
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
        m.calledMethodLog(TAG,"StartPermissionRequest");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.PACKAGE_USAGE_STATS},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }
    // this is the important bit which checks if the permission is granted or not
    // and therefore change your functionality accordingly
    // this method sets the values of the boolean variable for location, contact and sms and store them in sharedPreference
    // although it is not the right practice because if a user revokes the permission then these variables are not updated
    // and can therefore cause crash
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                m.updateLog(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            }
            if (grantResults[0] == PackageManager.PERMISSION_DENIED ||
                    grantResults[1] == PackageManager.PERMISSION_DENIED) {
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showAlertDialog(R.string.permission_denied_explanation);
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                );
            }
        }


        /*switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;
            }
        }*/
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==0){
            getFragmentManager().beginTransaction().replace(android.R.id.content,new MonitoringFragmentCardView())
                    .commit();
        }
    }
    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onPause();
    }

    // Show when a not granted permission is required
    // no button snackbar
    private void showSnackbar(final String text) {
        m.calledMethodLog(TAG,"showSnackbar");
        Snackbar.make(findViewById(android.R.id.content),text,
                Snackbar.LENGTH_LONG)
                .show();
    }
    // shows a Snackbar indefinitely (for permissions)
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        m.calledMethodLog(TAG,"showSnackbar");
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void showAlertDialog(final int text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required")
                .setMessage("Please grant Usage Access permission" + " " + getString(text))
                .setIcon(android.R.drawable.checkbox_on_background)
                .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        m.usageAccessSettingsPage();
                    }
                });
//        builder.show();
    }
}