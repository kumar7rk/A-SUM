package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit on 30/08/2016.
 */
public class ManualDetails extends Fragment {
    ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<String>();



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule, container, false);
        listView = (ListView)v.findViewById(R.id.listView);
        getInstalledApplications();
        return v;
    }
    public void getInstalledApplications()
    {
        List<PackageInfo> packList = getActivity().getPackageManager().getInstalledPackages(0);

        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            if (  (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String appName = packInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
                list.add(appName);
                Drawable appIcon = getActivity().getPackageManager().getApplicationIcon(packInfo.applicationInfo);
                appIcon.setBounds(0, 0, 40, 40);

                //holder.apkName.setCompoundDrawables(appIcon, null, null, null);
                Log.e("App ? " + Integer.toString(i), appName);
            }
        }
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_multichoice, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.application_list_edit,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Main.showToast(getActivity(), "saved");
//                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                getActivity().getFragmentManager().popBackStack();

        }
                return super.onOptionsItemSelected(item);
    }
}
