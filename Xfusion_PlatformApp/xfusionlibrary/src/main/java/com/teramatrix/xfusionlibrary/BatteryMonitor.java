package com.teramatrix.xfusionlibrary;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by arun.singh on 2/28/2017.
 */

public class BatteryMonitor {

    public static HashMap<String, String> getBatteryState(Context context) {

        //Register Receiver for receiving battery change sticky Intent broadcasted by BatteryManager.
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        //Determine battery percentage
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//        float batteryPct = level / (float) scale;


        //Determine charging status

        // 1. Is device charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // 2. How device is charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        //Record Battery parameters
        HashMap<String, String> batteryState = new HashMap<>();
        batteryState.put("Device Battery Percentage", "" + level);
        batteryState.put("Device Charging Status", "" + isCharging);

        if (isCharging) {
            if (usbCharge)
                batteryState.put("Device Charging Source", "USB");
            else if (acCharge)
                batteryState.put("Device Charging Source", "AC");
            else
                batteryState.put("Device Charging Source", "Not Available");
        } else
            batteryState.put("Device Charging Source", "Not Available");

        return  batteryState;

    }
}
