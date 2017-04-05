package com.teramatrix.xfusionlibrary.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.teramatrix.xfusionlibrary.service.DataIntentService;
import com.teramatrix.xfusionlibrary.util.AlarmManagerUtil;
import com.teramatrix.xfusionlibrary.util.SdkSPUtils;


/**
 * Created by arun.singh on 1/20/2017.
 * This BroadcastReceiver will be invoked periodicall from Alarmmanager. It wll launch DataIntentService service to uploading data to server.
 */

public class SdkWakefulBroadcastReceiver extends android.support.v4.content.WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SdkWakefulBR", "Invoked");
        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        Intent service = new Intent(context, DataIntentService.class);
        startWakefulService(context, service);

        int interval = new SdkSPUtils(context).getInt(SdkSPUtils.DEVCIE_DATA_API_UPDATE_INTERVAL);
if (interval == 0)
        interval = 120;
        AlarmManagerUtil.setAlarm(context.getApplicationContext(), 1000 * interval, SdkWakefulBroadcastReceiver.class);

        }
        }
