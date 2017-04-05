package com.example.satku.xfusion_platformapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import fragment.DashboardFragment;


/**
 * Created by satku on 2/9/2017.
 */

public class SPUtil {
    private SharedPreferences sp;

    // public static final String PASSWORD = "password";
    public static final String USERNAME = "username";

    public SPUtil(Context context) {
        if (context != null)
            sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return sp.getString(key, "");
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sp.edit(); //2
        editor.putString(key, value); //3
        editor.apply();
    }
}