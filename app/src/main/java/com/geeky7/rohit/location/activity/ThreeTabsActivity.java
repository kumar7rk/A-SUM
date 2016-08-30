package com.geeky7.rohit.location.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.TabMessage;
import com.geeky7.rohit.location.fragment.MonitoringFragment;
import com.geeky7.rohit.location.fragment.Rules;
import com.geeky7.rohit.location.fragment.Violations;
import com.geeky7.rohit.location.fragment.list_of_rule;
import com.geeky7.rohit.location.service.BackgroundService;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

/**
 * Created by iiro on 7.6.2016.
 */
public class ThreeTabsActivity extends AppCompatActivity {

    private BottomBar mBottomBar;
    private final int permissionVariable = 0;
    protected OnBackPressedListener onBackPressedListener;
    boolean running,pausedFromActionBar,a;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
//        getWindow().setWindowAnimations(1);

        checkPermission();
        if (!running)
            startService();
//        checkValues();
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.main);

//        Fragment fragment = new Monitoring();
//        Fragment fragment = new Rules();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//         fragmentTransaction.replace(android.R.id.content, fragment).commit();


        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {

            @Override
            public void onMenuTabSelected(int menuItemId) {
                Fragment fragment = new Rules();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                switch (menuItemId) {
                    case R.id.bb_menu_monitoring:
//                        fragment = new Monitoring();
                        fragment = new MonitoringFragment();
                        fragmentTransaction.replace(android.R.id.content, fragment).commit();
                        break;
                    case R.id.bb_menu_rules:
//                        fragment = new Rules();
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
//                        fragment = new Monitoring();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_configure:
                startActivity(new Intent(this,Configure.class));
            case R.id.stop_service:
                Intent serviceIntent = new Intent(ThreeTabsActivity.this, BackgroundService.class);
                /*stopService(new Intent(ThreeTabsActivity.this, Automatic.class));
                stopService(new Intent(ThreeTabsActivity.this,SemiAutomatic.class));
                stopService(new Intent(ThreeTabsActivity.this,Manual.class));*/
                if (running){
                    stopService(serviceIntent);
//                    getActionBar().hide();
//                    getActionBar().isShowing()
                    running = false;
                }
            case R.id.start_service:
                if (!running){
                    startService();
//                    getActionBar().hide();
                    running = true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }


    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        finish();
        /*if (onBackPressedListener != null){
            onBackPressedListener.doBack();
        }
        else
            super.onBackPressed();*/
    }

    @Override
    protected void onDestroy() {
        onBackPressedListener = null;
        super.onDestroy();
    }


}