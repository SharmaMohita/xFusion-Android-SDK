package com.teramatrix.xfusionlibrary.receivers;

import android.content.Context;
import android.content.Intent;

import com.teramatrix.xfusionlibrary.database.DatabaseHandler;
import com.teramatrix.xfusionlibrary.service.DataIntentService;
import com.teramatrix.xfusionlibrary.util.NetwokUtils;

/**
 * Created by arun.singh on 3/3/2017.
 * This class will receive event when there is any change in network connectivity status.
 * IF network state becomes 'avaialble' send all offline Service Data from db to server if avaialble.
 */

public class NetworkChangeReceiver extends android.support.v4.content.WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Check is network is available
        if (NetwokUtils.getConnectivityStatus(context)) {
            //Network is available,now generate event to send all offline data to server.

            //Check if Local DB has any recorded ServcieData
            if (DatabaseHandler.getRecordsCount() > 0) {
                // Start the service, keeping the device awake while the service is
                // launching. This is the Intent to deliver to the service.
                Intent service = new Intent(context, DataIntentService.class);
                service.putExtra("type", "send_offline_data");
                startWakefulService(context, service);
            }
        }

    }
}
