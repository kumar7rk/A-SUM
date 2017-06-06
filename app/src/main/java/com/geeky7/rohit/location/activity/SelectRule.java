package com.geeky7.rohit.location.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

import java.text.DateFormat;
import java.util.Date;

public class SelectRule extends AppCompatActivity {

    Button backB, okB;
    RadioButton radioButton;
    RadioGroup radioGroup;
    SharedPreferences preferences;
    Main m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_rule);
        backB = (Button)findViewById(R.id.back);
        okB = (Button)findViewById(R.id.ok);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        m = new Main(getApplicationContext());
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*String rule = "AA";
        rule = sharedPrefs.getString(CONSTANTS.SELECTED_RULE, rule);*/
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRB = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedRB);
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                String rule = radioButton.getText().toString();
                editor.putString(CONSTANTS.SELECTED_RULE, rule);
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                if (rule.equalsIgnoreCase("Automatic")){
                    editor.putString(CONSTANTS.AUTOMATIC_RULE_ADDED_TIME,currentDateTimeString);
                }
                if (rule.equalsIgnoreCase("SemiAutomatic")){
                    editor.putString(CONSTANTS.SEMIAUTOMATIC_RULE_ADDED_TIME,currentDateTimeString);
                }
                if (rule.equalsIgnoreCase("Manual")){
                    editor.putString(CONSTANTS.MANUAL_RULE_ADDED_TIME,currentDateTimeString);
                }
                if (rule.equalsIgnoreCase("Notification")){
                    editor.putString(CONSTANTS.NOTIFICATION_RULE_ADDED_TIME,currentDateTimeString);
                }
                editor.commit();
                Main.showToast(getApplicationContext(), rule + " Added");
                setResult(0);
                finish();

                m.usageAccessSettingsPage();
                //startActivity(new Intent(SelectRule.this,ThreeTabsActivity.class)); using this opens selectRuleActivity
                // instead of configure;
            }
        });
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_select_rule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(1);
                finish();
                return true;

            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyRuleFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.rule_preference);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        finish();
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    @Override
    protected void onResume() {
        super.onPostResume();
        getDelegate().onPostResume();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
