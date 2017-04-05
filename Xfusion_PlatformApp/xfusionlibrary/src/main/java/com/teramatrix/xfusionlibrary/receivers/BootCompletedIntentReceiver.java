package com.teramatrix.xfusionlibrary.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.teramatrix.xfusionlibrary.service.LocationTrackingServcie;
import com.teramatrix.xfusionlibrary.util.SdkSPUtils;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by arun.singh on 2/24/2017.
 * <p>
 * This class works as BroadCastReciver for reciving device re-boot event.
 * When device reboot completes , this code module will be notified by system. It will reinitiate all app services which have become inactive(Stopped) due to device shutdown.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

                //Start Location Tracking if it was running before device reboot.
                if (new SdkSPUtils(context).getBoolean(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING)) {
                    Intent locationServiceIntent = new Intent(context, LocationTrackingServcie.class);
                    locationServiceIntent.setAction(LocationTrackingServcie.START_FOREGROUND_SERVICE);
                    context.startService(locationServiceIntent);
                }
                //Re-Configure Alarm(It invokes device/app data api continuosly at each predefined interval)
                //If was configured before device reboot.
                SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
                if (sdkSPUtils.getBoolean(SdkSPUtils.IS_REPEATING_ALARM_SET)) {
                    AlarmManager alarmMgr = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);
                    Intent receiverIntent = new Intent(context.getApplicationContext(), SdkWakefulBroadcastReceiver.class);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, receiverIntent, 0);
                    alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            1000 * sdkSPUtils.getInt(SdkSPUtils.DEVCIE_DATA_API_UPDATE_INTERVAL),
                            1000 * sdkSPUtils.getInt(SdkSPUtils.DEVCIE_DATA_API_UPDATE_INTERVAL), alarmIntent);

                    sdkSPUtils.setValue(SdkSPUtils.IS_REPEATING_ALARM_SET, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
