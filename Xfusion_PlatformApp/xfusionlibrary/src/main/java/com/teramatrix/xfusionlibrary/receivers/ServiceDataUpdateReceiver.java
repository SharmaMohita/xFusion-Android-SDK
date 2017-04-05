package com.teramatrix.xfusionlibrary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by arun.singh on 1/27/2017.
 * This BroadcastReceiver will be used to transfer Device's all Service data(Json form string) from DataIntentService to any activity or fragment in Third Party App.
 *
 */

public class ServiceDataUpdateReceiver extends BroadcastReceiver {


    public interface INotifyServcieDataUpdates
    {
        public void onServcieDataChanged(String serviceDataJson);
    }
    INotifyServcieDataUpdates iNotifyServcieDataUpdates;
    public ServiceDataUpdateReceiver(INotifyServcieDataUpdates iNotifyServcieDataUpdates)
    {
        this.iNotifyServcieDataUpdates = iNotifyServcieDataUpdates;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String servcie_data = intent.getStringExtra("service_data");
        iNotifyServcieDataUpdates.onServcieDataChanged(servcie_data);
    }
}
