package com.geeky7.rohit.location.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.geeky7.rohit.location.Main;

public class MyAccessibilityService extends AccessibilityService {

    String eventText = "NothingYet";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        eventText = null;
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "Focused: ";
                break;
        }

        eventText = eventText + event.getContentDescription();
        Main.showToast(getApplicationContext(),eventText);
        Log.i("MyAccessibilityService",eventText);
    }

    public MyAccessibilityService() {
        Main.showToast(getApplicationContext(),eventText);
    }

    @Override
    public void onInterrupt() {

    }
}