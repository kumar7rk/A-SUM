/*
* Let's you update the keyword; save it in sharePreferences; share with your family friends via SMS
*
* */
package com.geeky7.rohit.location.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geeky7.rohit.location.CONSTANTS;
import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.Main;

public class NotificationTextFragment extends DialogFragment {
    SharedPreferences preferences;
    EditText notification_text_et;
    AlertDialog.Builder alertDialog;
    Main m;

    public static final String TAG = CONSTANTS.NOTIFICATION_TEXT_FRAGMENT;
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

        notification_text_et = (EditText) view.findViewById(R.id.notification_text_et);

        //putting the caret on the last of the keyword for convenience
        notification_text_et.setSelection(notification_text_et.getText().length());

//        String text = notification_text_et.getText().toString();

        // creating dialog with buttons
        //on click save button save the keyword in the sharedPreferences
        alertDialog = new AlertDialog.Builder(getActivity())
            .setTitle("Customise Notification Message")
            .setView(inflater.inflate(R.layout.activity_notification_text, null))
            //.setPositiveButton(R.string.save,null)
            //.setNegativeButton(R.string.close,null)
            .setNeutralButton(R.string.reset,null)
         // save button, onClick updates the keyword in sharedPreference
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String notificationTextS = notification_text_et.getText().toString().trim();
                        editor.putString(CONSTANTS.NOTIFICATION_TEXT,notificationTextS);
                        editor.apply();
                    }
                })
                // close button to close the dialog
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                //reset the text to default
            .setNeutralButton(R.string.reset, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    notification_text_et.setText(CONSTANTS.NOTIFICATION_TEXT_DEFAULT);
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


        // handling onclick reset button
        // show a dialog to confirm if user want to reset the text.
        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog!=null){
            Button resetB = dialog.getButton(Dialog.BUTTON_NEUTRAL);
            resetB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        builder.setTitle("Reset notification message to Default")
                                .setIcon(R.drawable.permission_warning)
                                .setPositiveButton(MyApplication.getAppContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        notification_text_et.setText(CONSTANTS.NOTIFICATION_TEXT_DEFAULT);
                                        notification_text_et.setSelection(notification_text_et.getText().length());
                                    }
                                })
                                .setNegativeButton(MyApplication.getAppContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                            builder.show();
                        m.updateLog(TAG,"Resetting everything in 3..2...1. Just kidding. Just resetting the notification text to default");
                    }
            });
        }
        notification_text_et = (EditText) getDialog().findViewById(R.id.notification_text_et);
        // get the stored notification text
        notification_text_et.setText(preferences.getString(CONSTANTS.NOTIFICATION_TEXT,CONSTANTS.NOTIFICATION_TEXT_DEFAULT));
        // move the caret to the last of the text; front by default
        notification_text_et.setSelection(notification_text_et.getText().length());
    }
}