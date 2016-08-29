package com.geeky7.rohit.location;

/**
 * Created by iiro on 7.6.2016.
 */
public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.bb_menu_monitoring:
                message += "monitoring";
                break;
            case R.id.bb_menu_rules:
                message += "rules";
                break;
            case R.id.bb_menu_violation:
                message += "violations";
                break;
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
