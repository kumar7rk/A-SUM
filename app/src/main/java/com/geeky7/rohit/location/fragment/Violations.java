package com.geeky7.rohit.location.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geeky7.rohit.location.R;


public class Violations extends Fragment {

    TextView violationTV;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_violations,container,false);
        violationTV = (TextView)v.findViewById(R.id.violations);
        violationTV.setText(setEmptyText());
        return v;
    }
    public String setEmptyText(){
        return "No rule break;";
    }

}
