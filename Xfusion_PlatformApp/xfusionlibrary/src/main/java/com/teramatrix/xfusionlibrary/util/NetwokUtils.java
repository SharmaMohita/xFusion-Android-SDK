package com.teramatrix.xfusionlibrary.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by arun.singh on 3/1/2017.
 */

public class NetwokUtils {

    /*Get network connectivity status*/
    public static boolean getConnectivityStatus(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return true;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
