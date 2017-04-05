package com.teramatrix.xfusionlibrary.edgeanalytic;

import android.location.Location;
import android.util.Log;

import com.teramatrix.xfusionlibrary.database.DatabaseHandler;
import com.teramatrix.xfusionlibrary.database.ServiceDataModel;

import java.util.List;



/**
 * Created by arun.singh on 3/2/2017.
 */

public class EdgeAnalyticFilter {


    public static void saveServiceDataInDB(List<ServiceDataModel> serviceDataModelsList, String selected_servcie_name_filter) {


        //Extract Location Object from list of new records
        Location newLocation = extractLocationServiceData(serviceDataModelsList);

        //get offline records from local db
        List<ServiceDataModel> serviceDataModels_time = DatabaseHandler.findInDB("service_name", selected_servcie_name_filter, "sys_timestamp DESC", 1);

        //Check if there is any offline record in local db.
        if (serviceDataModels_time != null && serviceDataModels_time.size() > 0) {
            //Offline record found, compare new records with last records in local db

            String sys_timestamp = serviceDataModels_time.get(0).sys_timestamp;
            serviceDataModels_time = DatabaseHandler.findInDB("sys_timestamp", sys_timestamp);

            //Extract Location Object from list of offline records
            Location oldLocation = extractLocationServiceData(serviceDataModels_time);

            boolean isBetterLocation  = LocationFilter.isBetterLocation(oldLocation, newLocation);
            if(!isBetterLocation)
            {
                //Replace Old Location with new location
                DatabaseHandler.deleteSelectedRecords("sys_timestamp", sys_timestamp);
                Log.i("xFusionAndroidSdk", "EdgeAnalyticFilter Old ServcieData deleted from DB");
            }
            //Insert all current records in db.
            DatabaseHandler.saveRecordsInDB(serviceDataModelsList);
            Log.i("xFusionAndroidSdk", "EdgeAnalyticFilter data inserted into DB");

        } else {
            //No offline record found
            //Insert all current records in db.
            DatabaseHandler.saveRecordsInDB(serviceDataModelsList);
        }

    }

    private static Location extractLocationServiceData(List<ServiceDataModel> serviceDataModelsList) {

        Location location = new Location("");
        for (ServiceDataModel serviceDataModel : serviceDataModelsList) {
            if (serviceDataModel.service_name.equalsIgnoreCase("Location Parameters")) {
                if (serviceDataModel.data_source.equalsIgnoreCase("Latitude")) {
                    location.setLatitude(Double.parseDouble(serviceDataModel.current_value));
                } else if (serviceDataModel.data_source.equalsIgnoreCase("Longitude")) {
                    location.setLongitude(Double.parseDouble(serviceDataModel.current_value));
                } else if (serviceDataModel.data_source.equalsIgnoreCase("Location Accuracy")) {
                    location.setAccuracy(Float.parseFloat(serviceDataModel.current_value));
                } else if (serviceDataModel.data_source.equalsIgnoreCase("Location Provider")) {
                    location.setProvider(serviceDataModel.current_value);
                } else if (serviceDataModel.data_source.equalsIgnoreCase("Location Logtime")) {
                    location.setTime(Long.parseLong(serviceDataModel.current_value));
                }
            }
        }
        return location;
    }


}
