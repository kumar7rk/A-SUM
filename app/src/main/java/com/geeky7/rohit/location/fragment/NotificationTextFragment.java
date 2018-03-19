/*
* Let's you update the keyword; save it in sharePreferences; share with your family friends via SMS
*
* */
package com.geeky7.rohit.location.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.Main;

public class NotificationTextFragment extends DialogFragment {
    SharedPreferences preferences;
    EditText text_et;
    AlertDialog.Builder alertDialog;
    Main m;

    public static final String TAG = CONSTANTS.NOTIFICATION_TEXT;
    public NotificationTextFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        m = new Main(getActivity());
        m.calledMethodLog(TAG,"onCreate");
        super.onCreate(savedInstanceState);
    }

    //sets up the dialog; pretty much does everything this class is expected too
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        m.calledMethodLog(TAG,"onCreateDialog");
        //setting up Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = preferences.edit();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_notification_text, null);

        text_et= (EditText) view.findViewById(R.id.notification_text_et);

        //putting the caret on the last of the keyword for convenience
        text_et.setSelection(text_et.getText().length());

        // creating dialog with buttons
        //on click save button save the keyword in the sharedPreferences
        alertDialog = new AlertDialog.Builder(getActivity())
        .setTitle("Customise Notification Text")
        .setView(inflater.inflate(R.layout.activity_notification_text, null))
                // save button, onClick updates the keyword in sharedPreference
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String keywordS = keyword.getText().toString().trim();
                        editor.putString(CONSTANTS.NOTIFICATION_TEXT,keywordS);
                        editor.apply();
                    }
                })
                // close button to close the dialog
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
        //share button to share the keyword via sms
        // open the default messaging app with pre added text with keyword
        .setNeutralButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return alertDialog.create();
    }

    //gets the saved keyword from the sharedPreferences and also magically puts the caret at the end of the text
    // yeah it's not like that by default
    @Override
    public void onResume() {
        m.calledMethodLog(TAG,"onResume");

        super.onResume();
        text_et = (EditText) getDialog().findViewById(R.id.notification_text_et);
        // get the stored keyword
        text_et.setText(preferences.getString(CONSTANTS.NOTIFICATION_TEXT,""));
        // move the caret to the last of the text; front by default
        text_et.setSelection(text_et.getText().length());
    }
}