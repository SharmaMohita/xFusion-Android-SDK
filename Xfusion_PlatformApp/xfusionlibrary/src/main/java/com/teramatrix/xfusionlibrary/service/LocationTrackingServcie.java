package com.teramatrix.xfusionlibrary.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.teramatrix.xfusionlibrary.IoTSDK;
import com.teramatrix.xfusionlibrary.R;
import com.teramatrix.xfusionlibrary.util.LogUtils;
import com.teramatrix.xfusionlibrary.util.SdkSPUtils;


/**
 * Created by arun.singh on 1/12/2017.
 */

public class LocationTrackingServcie extends Service implements LocationListener {

    /**
     * A flag to notify that service is a foreground service
     */
    public static final String START_FOREGROUND_SERVICE = "start";

    /**
     * Flag to remove notification and location listener
     */
    public static final String STOP_FOREGROUND_SERVICE = "stop";
    /**
     * Notification id for foreground service while tracking the current location of device
     */
    public static final int REQUEST_FOREGROUND = 105;

    /**
     * To get continues location updates
     */
    private FusedLocationProvider provider;
    /**
     * Because it is a foreground service so it is needed to have a notification on notification drawer always.
     * Following instance will build a notification on every location update with default style and settings.
     */
    private NotificationCompat.Builder builder;

    private int interval = 30;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.log("LocationTrackingServcie","onCreate");

        if (provider != null) {
            provider = null;
        }

        //Settting Location Update Interval
        interval = new SdkSPUtils(getApplicationContext()).getInt(SdkSPUtils.LOCATION_UPDATE_INTERVAL);

        // now initiating fused location provider and notification builder
        provider = new FusedLocationProvider(getApplicationContext())
                .setInterval(1000 * interval)
                .setFastestInterval(1000 * interval)
                .setPriority(interval < 10 ? FusedLocationProvider.PRIORITY_HIGH_ACCURACY : FusedLocationProvider.PRIORITY_BALANCED_POWER_ACCURACY)
                .setLocationListener(this);

        builder = new NotificationCompat.Builder(this)
                .setContentTitle("xFusionDataService is running")
                .setContentText("Monitoring device service data")
                .setSmallIcon(R.drawable.ic_stat_data_usage)
                .setShowWhen(true)
                .setOngoing(true);

        //Setting Exit Service to false
        new SdkSPUtils(getApplicationContext()).setValue(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING, true);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        LogUtils.log("LocationTrackingServcie","onStartCommand");
        switch (intent.getAction()) {
            // if we have to start service
            case START_FOREGROUND_SERVICE:
                LogUtils.log("LocationTrackingServcie","START_FOREGROUND_SERVICE");
                startForeground(REQUEST_FOREGROUND, builder.build());
                provider.start();
                break;
            // if we are going to stop everything
            case STOP_FOREGROUND_SERVICE:
                LogUtils.log("LocationTrackingServcie","STOP_FOREGROUND_SERVICE");
                stopForeground(true);
                provider.stop();
                stopSelf();
                break;
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LogUtils.log("LocationTrackingServcie","onDestroy");
            // stopping location provider
            if (provider != null)
                provider.stop();

            //stop this as forground
            stopForeground(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Service re start cases
        if (new SdkSPUtils(getApplicationContext()).getString(SdkSPUtils.SERVICE_EXECUTE_MODE).equalsIgnoreCase(IoTSDK.SERVICE_EXECUTE_MODE_ONLY_FORGROUND)) {
            //do nothing - Let service be stopped
            LogUtils.log("LocationTrackingServcie","onDestroy do nothing - Let service be stopped");
        } else if (!new SdkSPUtils(getApplicationContext()).getBoolean(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING)) {
            //do nothing - Let service be stopped
            LogUtils.log("LocationTrackingServcie","onDestroy do nothing - Let service be stopped");
        } else {
            //Re-start Service
            LogUtils.log("LocationTrackingServcie","onDestroy Re-start Service");
            startService(new Intent(this, LocationTrackingServcie.class).setAction(START_FOREGROUND_SERVICE));
        }
    }

    /*Callback method  to receive newly found location*/
    @Override
    public void onLocationChanged(Location location) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(getApplicationContext());

        //new lcoation, replace previos saved location with new location . this locatino will be sent to server
        //save new location in SP
        sdkSPUtils.setValue(SdkSPUtils.LOCATION_LATITUDE,(float) location.getLatitude());
        sdkSPUtils.setValue(SdkSPUtils.LOCATION_LONGITUDE,(float) location.getLongitude());
        sdkSPUtils.setValue(SdkSPUtils.LOCATION_ACCURACY, location.getAccuracy());
        sdkSPUtils.setValue(SdkSPUtils.LOCATION_PROVIDER, location.getProvider());
        sdkSPUtils.setValue(SdkSPUtils.LOCATION_LOG_TIME, location.getTime()+"");
        sdkSPUtils.setValue(SdkSPUtils.LOCATION_SPEED, location.getSpeed());

        Log.i("xFusionAndroidSdk","LocationTrackingServcie.onLocationChanged - new location:"+location.getLatitude()+","+location.getLongitude());


        //Send Broad cast to Third party app's Activity or Fragment (For showing location on UI).
        //If Third party app's activity or Fragment has registerd itself to receive location update broadcast.
        Intent locationUpdateBroadcast = new Intent("iot_sdk_mobile_android_location_data");
        locationUpdateBroadcast.putExtra("latitude",location.getLatitude()+"");
        locationUpdateBroadcast.putExtra("longitude",location.getLongitude()+"");
        locationUpdateBroadcast.putExtra("log_time",location.getTime());
        getApplicationContext().sendBroadcast(locationUpdateBroadcast);
    }

}
