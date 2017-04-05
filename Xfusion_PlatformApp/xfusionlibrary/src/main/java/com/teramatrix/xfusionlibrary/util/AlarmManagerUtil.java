package com.teramatrix.xfusionlibrary.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by arun.singh on 3/27/2017.
 */

public class AlarmManagerUtil {

    public static void setAlarm(Context context,int triggerAtSeconds,Class broadcastReceiverClass)
    {
        AlarmManager alarmMgr = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);
        Intent receiverIntent = new Intent(context.getApplicationContext(), broadcastReceiverClass);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, receiverIntent, 0);

        int interval = new SdkSPUtils(context).getInt(SdkSPUtils.DEVCIE_DATA_API_UPDATE_INTERVAL);
        if(interval == 0)
            interval = 120;

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+(1000 * interval),
                alarmIntent);

    }

}
