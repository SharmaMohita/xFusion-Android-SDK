package com.example.satku.xfusion_platformapp;

import android.support.v4.app.Fragment;

import fragment.DashboardFragment;

/**
 * Created by satku on 2/14/2017.
 */

public class Util {

    public static void loadFragment(MainActivity mainActivity, Fragment fragment) {
        if (fragment instanceof DashboardFragment) {
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(MainActivity.STACK)
                    .commit();
        }
    }
}
