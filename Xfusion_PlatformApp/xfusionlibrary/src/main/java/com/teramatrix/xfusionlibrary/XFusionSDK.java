package com.teramatrix.xfusionlibrary;

import com.facebook.stetho.Stetho;

/**
 * Created by arun.singh on 3/1/2017.
 */

public class XFusionSDK extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //For dubugging local databse - see data in chrome browser. remove it in release build.
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );
        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();
        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);

    }
}
