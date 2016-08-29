package com.geeky7.rohit.location.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

public class Configure extends AppCompatActivity implements ThreeTabsActivity.OnBackPressedListener {
    CheckBoxPreference pref;
    Button cancelB,nextB;
    AppCompatDelegate mDelegate;
    protected ThreeTabsActivity.OnBackPressedListener onBackPressedListener;

    @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_configure);
//            getWindow().setWindowAnimations(2);
            cancelB = (Button)findViewById(R.id.cancel);
            nextB = (Button)findViewById(R.id.next);
            getDelegate().installViewFactory();
            getDelegate().onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment())
                    .commit();
            nextB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Configure.this,SelectRule.class));
                    Main.showToast(getApplicationContext(),"Clicked");
                    /*getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentByTag("tag")).commit();
                    Main.showToast(getApplicationContext(), "Removed");
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content, new SelectRule.MyRuleFragment()).commit();
                    Main.showToast(getApplicationContext(),"replaced");*/

                }
            });
            cancelB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Main.showToast(getApplicationContext(),"Cancelled");
                    finish();
                }
            });
        }

    @Override
    public void doBack() {
//        new MonitoringFragment().loadItems();

    }
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.configure_preference);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configure, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setContentView(int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }
*/

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    public AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.ok:
//                getFragmentManager().beginTransaction().replace(android.R.id.content, new MonitoringFragment()).commit();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



/*    public interface OnBackPressedListener {
        void doBack();
    }*/

    public void setOnBackPressedListener(ThreeTabsActivity.OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new MonitoringFragment()).commit();
        finish();
       /* if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();*/
    }

    @Override
    protected void onDestroy() {
        onBackPressedListener = null;
        getDelegate().onDestroy();
        super.onDestroy();
    }

}
