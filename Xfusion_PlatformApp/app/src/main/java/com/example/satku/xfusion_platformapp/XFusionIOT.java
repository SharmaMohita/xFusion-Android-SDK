package com.example.satku.xfusion_platformapp;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.facebook.stetho.Stetho;

/**
 * Created by satku on 3/2/2017.
 */

public class XFusionIOT extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "Montserrat-Regular.ttf");
        ActiveAndroid.initialize(this);
        Stetho.initializeWithDefaults(this);


    }
}
