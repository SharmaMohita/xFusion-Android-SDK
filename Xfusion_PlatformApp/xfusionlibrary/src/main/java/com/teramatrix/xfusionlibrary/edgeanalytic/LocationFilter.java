package com.teramatrix.xfusionlibrary.edgeanalytic;

import android.location.Location;
import android.util.Log;


/**
 * Created by arun.singh on 3/6/2017.
 * Filter for identifyning Bad Geo data .
 */

public class LocationFilter {


    //Accuracy in meters
    private static int accuracyLimit = 30;
    //Speeed Limit in km/hour
    private static int speedLimit = 120;
    //DistanceLimit in meters
    private static int distanceLimit = 500;


    /*This method compares new Location data with old Location data on various parmeters*/
    public static boolean isBetterLocation(Location location,Location lastLocation)
    {
        if (location == null
                && lastLocation != null) {
            //location = lastLocation;
            log("Null Location");
            return false;
        } else if (location != null
                && lastLocation != null
                && (location.getLatitude() == 0
                || location.getLongitude() == 0)) {
            log("Zero Latitude And Longitude");
            return false;
        } else if (location != null
                && lastLocation != null
                && location.getAccuracy() > accuracyLimit) {
            log("Inaccurate Location");
            return false;
        } else if (location != null
                && lastLocation != null
                && (location.getSpeed() * 3.6) > speedLimit) {
            log("Over speed location");
            return false;
        } else if (location != null
                && lastLocation != null
                && location.getAccuracy() > location.distanceTo(lastLocation)) {
            // if accuracy is more than distance between previous and current location
            log("Inside Accuracy Radius");
            return false;
        } else if (location != null
                && lastLocation != null
                && location.distanceTo(lastLocation) < distanceLimit) {
            // if distance between last location and current location is less than distanceLimit meters
            log("Very Short Distance");
            return false;
        }

        return true;
    }
    public static void log(String msg) {
        try {
            Log.e("LocationFilter", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
