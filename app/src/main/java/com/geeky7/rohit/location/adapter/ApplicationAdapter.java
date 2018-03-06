package com.geeky7.rohit.location.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {

    private List<ApplicationInfo> appsList = null;
    private Context context;
    private PackageManager packageManager;
    private ArrayList<Boolean> checkList = new ArrayList<Boolean>();
    SharedPreferences preferences;
    CheckBox checkBox;
    ApplicationInfo data;

    ArrayList<String> packageNames = new ArrayList<>();
    Set<String> selectedAppsSet = new HashSet<String>();
    Set<String> selectedAppsSetIndex = new HashSet<String>();
    private SparseBooleanArray selectedApps =   new SparseBooleanArray();
    SharedPreferences.Editor editor;

    public ApplicationAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        Set<String> appsIndex = new HashSet<String>();
        ArrayList<String> appsIndexList = new ArrayList<>();

        appsIndex = preferences.getStringSet("someStringSetIndex", appsIndex);

        Main.showToast(getContext(),"ApplicationAdapter constructor");
        for (String s:appsIndex)
            Log.i("AppsIndex",s);

        for (int i = 0; i < appsList.size(); i++) {
            checkList.add(false);
        }
        for (String s: appsIndex){
            appsIndexList.add(s);
        }
        for (int i = 0;i<appsIndexList.size();i++) {
            int index = Integer.parseInt(appsIndexList.get(i));
            checkList.add(index, true);
            selectedApps.append(index,true);
        }
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.manual_listitems, null);
        }

        data = appsList.get(position);
        packageNames.add(data.packageName);

        if (null != data) {
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
            checkBox = (CheckBox) view.findViewById(R.id.cb_app);

            checkBox.setTag(position); // set the tag so we can identify the correct row in the listener
            checkBox.setChecked(selectedApps.get(position));
            checkBox.setText(data.loadLabel(packageManager));
            checkBox.setButtonDrawable(android.R.color.transparent);

            checkBox.setOnClickListener(new OnItemClickListener(position,checkBox.getText(),checkBox));
            iconview.setImageDrawable(data.loadIcon(packageManager));
        }
        return view;
    }
    public void commit(){
        editor.apply();
        Main.showToast(getContext(),"Commit");
    }

    // handles the onClickListener added above
    class OnItemClickListener implements View.OnClickListener {
        private int position;
        private CharSequence text;
        private CheckBox checkBox;
        OnItemClickListener(int position, CharSequence text,CheckBox checkBox){
            this.position = position;
            this.text = text;
            this.checkBox = checkBox;
        }
        @Override
        public void onClick(View arg0) {

            String packageName = packageNames.get((Integer) checkBox.getTag());

            boolean b = checkBox.isChecked();
            if (b){
                selectedApps.append(position, true);
                selectedAppsSetIndex.add(position+"");
                selectedAppsSet.add(packageName);
            }
            else{
                selectedApps.append(position, false);
                selectedAppsSetIndex.remove(position+"");
                selectedAppsSet.remove(packageName);
            }
            editor.putStringSet("someStringSet", selectedAppsSet);
            editor.putStringSet("someStringSetIndex", selectedAppsSetIndex);
            editor.apply();
        }
    }
}