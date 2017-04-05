package com.example.satku.xfusion_platformapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fragment.Tab1;
import fragment.Tab2;
import fragment.Tab3;

/**
 * Created by satku on 3/27/2017.
 */

public class pager extends FragmentPagerAdapter {
    int tabCount;

    public pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Tab1 tab1 = new Tab1();
                return tab1;
            case 1:
                Tab2 tab2 = new Tab2();
                return tab2;
            case 2:
                Tab3 tab3=new Tab3();
                return tab3;
            default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
