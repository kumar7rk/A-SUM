package com.geeky7.rohit.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by Rohit on 13/09/2016.
 */
public class ViolationDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Violation.db";
    SQLiteDatabase database = this.getWritableDatabase();
    SharedPreferences preferences;

    public ViolationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        /*database.execSQL("delete from Violation");
        Main.showToast("ViolationDbHelperContructor-->AllValuesDeletedPleaseRemoveMe");*/
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Main.showToast("ViolationDbHelperOnCreate");
        String s="create table Violation(AppName Text)";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList fetchAll() {
        String appName;
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor=database.rawQuery("select * from Violation", null);

        preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        if(cursor.moveToFirst()){
            do {
                appName = cursor.getString(0);
                arrayList.add(appName);
            } while (cursor.moveToNext());
        }
        editor.putInt(CONSTANTS.NUMBER_OF_VIOLATIONS,arrayList.size());
        editor.commit();
        return arrayList;
    }
}
