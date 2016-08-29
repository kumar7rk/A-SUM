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

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

public class SelectRule extends AppCompatActivity {

    Button cancelB,nextB;
    RadioButton radioButton;
    RadioGroup radioGroup;
    String checkRadioButton;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_rule);
        cancelB = (Button)findViewById(R.id.cancel);
        nextB = (Button)findViewById(R.id.next);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String rule = "AA";
        rule = sharedPrefs.getString("ThisIsARule", rule);
        nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRB = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton)findViewById(selectedRB);
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                String rule = radioButton.getText().toString();
                editor.putString("ThisIsARule",rule);
                editor.commit();
                Main.showToast(getApplication(), rule + " Added");
                finish();
            }
        });
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.showToast(getApplicationContext(), "Cancelled");
                finish();
            }
        });
    }

    public void onRadioButtonClicked(View view){

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_rule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
}
