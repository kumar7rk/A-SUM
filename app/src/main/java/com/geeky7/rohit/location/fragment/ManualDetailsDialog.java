package com.geeky7.rohit.location.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.MyApplication;
import com.geeky7.rohit.location.R;
import com.geeky7.rohit.location.adapter.ApplicationAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rohit on 30/08/2016.
 */
public class ManualDetailsDialog extends DialogFragment {
    ListView listView;
    private ArrayAdapter<String> adapter;
    SharedPreferences preferences;
    Main m;
    ArrayList<String> packageList = new ArrayList<String>();
    ArrayList<String> appList = new ArrayList<String>();

    Set<String> selectedAppsSet = new HashSet<String>();
    Set<String> selectedAppsSetIndex = new HashSet<String>();

    AlertDialog.Builder alertDialog;

    private PackageManager packageManager = null;
    private ApplicationAdapter listadapter = null;
    private List<ApplicationInfo> applist = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        packageManager = MyApplication.getAppContext().getPackageManager();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Set<String> apps = new HashSet<String>();
        Set<String> appsIndex = new HashSet<String>();

//        apps = preferences.getStringSet("manualApps", apps);
        appsIndex = preferences.getStringSet("manualAppsIndex", appsIndex);
//        selectedAppsSetIndex = appsIndex;
        for (String s:appsIndex){
            Integer in = Integer.parseInt(s);
//            listView.setItemChecked(in,true);
        }
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        appSelected();
    }

    private void appSelected() {
//        Main.showToast("appSelected");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationInfo applicationInfo = (ApplicationInfo) parent.getItemAtPosition(position);
//                Main.showToast("itemClicked");
                view.getFocusables(position);
                view.setSelected(true);

//                Main.showToast(applicationInfo.loadLabel(packageManager) + "");
                String selectedAppS = packageList.get(position);
                selectedAppsSet.add(selectedAppS);
                selectedAppsSetIndex.add(position + "");
            }
        });
    }
    public ManualDetailsDialog(){

    }
    public static ManualDetailsDialog newInstance(int title) {
        ManualDetailsDialog dialog = new ManualDetailsDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        dialog.setArguments(args);
        return dialog;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom, null);
        listView = (ListView) convertView.findViewById(R.id.listView1);

        alertDialog = new AlertDialog.Builder(getActivity());

        loadItems();

        alertDialog.setView(convertView);
        alertDialog.setTitle("Select Application(s)").setIcon(R.drawable.abc_list_selector_holo_dark);
        addButtons();
        return alertDialog.create();
    }

    private void addButtons() {
        alertDialog.setPositiveButton(
                "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ApplicationAdapter a = new ApplicationAdapter(MyApplication.getAppContext(),0);
                        ApplicationAdapter a = new ApplicationAdapter(MyApplication.getAppContext(),0,applist);
                        a.commit();

                        getFragmentManager().beginTransaction().replace(android.R.id.content,new ManualDetailsCardView())
                                .commit();
                        Main.showToast(getContext(),"Saved");
                    }
                });

        alertDialog.setNegativeButton(
                "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Main.showToast(getContext(),"Cancelled");
                        getFragmentManager().beginTransaction().replace(android.R.id.content,new ManualDetailsCardView())
                                .commit();
                    }
                });
    }

    public void loadItems(){
        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

        Collections.sort(applist, new ApplicationInfo.DisplayNameComparator(packageManager));

        listadapter = new ApplicationAdapter(MyApplication.getAppContext(),
                R.layout.manual_listitems, applist);
        listView.setAdapter(listadapter);
    }
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            String packageName = info.packageName;
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                    packageList.add(packageName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }
}
