package com.teramatrix.xfusionlibrary.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.teramatrix.xfusionlibrary.DeviceServcieDataProvider;
import com.teramatrix.xfusionlibrary.IoTSDK;
import com.teramatrix.xfusionlibrary.controller.RESTClient;
import com.teramatrix.xfusionlibrary.database.DatabaseHandler;
import com.teramatrix.xfusionlibrary.database.ServiceDataModel;
import com.teramatrix.xfusionlibrary.edgeanalytic.EdgeAnalyticFilter;
import com.teramatrix.xfusionlibrary.edgeanalytic.LocationFilter;
import com.teramatrix.xfusionlibrary.receivers.SdkWakefulBroadcastReceiver;
import com.teramatrix.xfusionlibrary.util.NetwokUtils;
import com.teramatrix.xfusionlibrary.util.SdkSPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;


/**
 * Created by arun.singh on 1/20/2017.
 * This Service will be invoked from SdkWakefulBroadcastReceiver . It collects all device service data + location data and send them to server.
 * It calls APIs synchronously and once its task is completed ,it gets finshed
 */

public class DataIntentService extends IntentService {

    public DataIntentService() {
        super("DataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("xFusionAndroidSdk", "DataIntentService Invoked");
        // Do the work that requires your app to keep the CPU running.
        //Send Devcie-App data to Server
        List<ServiceDataModel> serviceDataModelsList = new ArrayList<>();
        SdkSPUtils sdkSPUtils = new SdkSPUtils(getApplicationContext());

        String type = intent.getStringExtra("type");

        if (type != null && type.equalsIgnoreCase("send_offline_data")) {
            //This event is for sending all offline data to server.
            //Get all Offline data from DB for which 'status' is 'insert'
            serviceDataModelsList = DatabaseHandler.getAllServiceData("status","insert");
            Log.i("xFusionAndroidSdk", "DataIntentService Offline data Found");

        } else {
            //this event is for sending online data to server.

            long currentUtcTime_seconds = System.currentTimeMillis()/1000;

            //get saved devcie Id from SP
            String devcie_Id = sdkSPUtils.getString(SdkSPUtils.DEVCIE_ID);

            //get latest Location data details from SP
            float LocationLatitude = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_LATITUDE);
            float LocationLongitude = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_LONGITUDE);
            float LocationAccuracy = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_ACCURACY);
            float LocationSpeed = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_SPEED);
            String LocationProvider = sdkSPUtils.getString(SdkSPUtils.LOCATION_PROVIDER);

            //Create Latest Location Objext
            Location newLocation = new Location("newLocation");
            newLocation.setLatitude(LocationLatitude);
            newLocation.setLongitude(LocationLongitude);
            newLocation.setAccuracy(LocationAccuracy);
            newLocation.setSpeed(LocationSpeed);
            newLocation.setProvider(LocationProvider);

            //Compare new location with old location. Check if new Lcoation is better than old.
            //if new location is better tan old location , catch new location otherwise discard it.
            newLocation =compareNewLocationWithOldLocation(newLocation);

            //Add Location Detaisl in Service Data
            serviceDataModelsList.add(new ServiceDataModel("Latitude", "Location Parameters", currentUtcTime_seconds + "", currentUtcTime_seconds + "", newLocation.getLatitude() + "", devcie_Id));
            serviceDataModelsList.add(new ServiceDataModel("Longitude", "Location Parameters", currentUtcTime_seconds + "", currentUtcTime_seconds + "", newLocation.getLongitude() + "", devcie_Id));
            serviceDataModelsList.add(new ServiceDataModel("Location Accuracy", "Location Parameters", currentUtcTime_seconds + "", currentUtcTime_seconds + "", newLocation.getAccuracy() + "", devcie_Id));
            serviceDataModelsList.add(new ServiceDataModel("Location Provider", "Location Parameters", currentUtcTime_seconds + "", currentUtcTime_seconds + "", newLocation.getProvider() + "", devcie_Id));
            serviceDataModelsList.add(new ServiceDataModel("Location Speed", "Location Parameters", currentUtcTime_seconds + "", currentUtcTime_seconds + "", newLocation.getSpeed() + "", devcie_Id));

            //Add All Phone Related Parametrs in Service Data
            if (sdkSPUtils.getBoolean(IoTSDK.DEVICE_PARAMETERS_PHONE)) {
                serviceDataModelsList.addAll(DeviceServcieDataProvider.getPhoneData(this, currentUtcTime_seconds + "", devcie_Id));
            }
            //Add All Network Related Parametrs in Service Data
            if (sdkSPUtils.getBoolean(IoTSDK.DEVICE_PARAMETERS_NETWORKS)) {
                serviceDataModelsList.addAll(DeviceServcieDataProvider.getNetworkData(this, currentUtcTime_seconds + "", devcie_Id));
            }
            //Add All Trafic Related Parametrs in Service Data
            if (sdkSPUtils.getBoolean(IoTSDK.DEVICE_PARAMETERS_TRAFIC)) {
                serviceDataModelsList.addAll(DeviceServcieDataProvider.getTraficData(this, currentUtcTime_seconds + "", devcie_Id));
            }
            //Add Host Status in Service Data
            serviceDataModelsList.add(new ServiceDataModel("host_status", "host_status", currentUtcTime_seconds + "", currentUtcTime_seconds + "", "ON", devcie_Id));

            //add status(update|insert) for each data source value
            boolean isAnyDataSourceValueChanges = false;
            for (ServiceDataModel serviceDataModel : serviceDataModelsList) {
                String val = sdkSPUtils.getString(serviceDataModel.data_source);

                if (val == null || val.isEmpty()) {
                    serviceDataModel.status = "insert";
                    isAnyDataSourceValueChanges = true;
                } else {
                    if (serviceDataModel.current_value.equalsIgnoreCase(val) && !serviceDataModel.data_source.equalsIgnoreCase("host_status")) {
                        serviceDataModel.status = "update";
                    }
                    else if(serviceDataModel.data_source.equalsIgnoreCase("host_status") && !isAnyDataSourceValueChanges) {
                        serviceDataModel.status = "update";
                    }
                    else {
                        serviceDataModel.status = "insert";
                        isAnyDataSourceValueChanges = true;
                    }
                }
                sdkSPUtils.setValue(serviceDataModel.data_source, serviceDataModel.current_value);
            }

        }

        //Check if We have any data to send
        if(serviceDataModelsList.size()>0)
        {
            //Check network connectivity before sending data to server.
            if (NetwokUtils.getConnectivityStatus(getApplicationContext())) {
                //Network Available

                //Form Json String from all servcie data
                JSONArray jsonArray = new JSONArray();
                try {

                    for (ServiceDataModel serviceDataModel : serviceDataModelsList) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("data_source", serviceDataModel.data_source);
                        jsonObject.put("service_name", serviceDataModel.service_name);
                        jsonObject.put("check_timestamp", serviceDataModel.check_timestamp);
                        jsonObject.put("sys_timestamp", serviceDataModel.sys_timestamp);
                        jsonObject.put("current_value", serviceDataModel.current_value);
                        jsonObject.put("device_id", serviceDataModel.device_id);
                        jsonObject.put("status", serviceDataModel.status);
                        jsonArray.put(jsonObject);
                    }

                    Log.i("xFusionAndroidSdk", "DataIntentService - Collected device servcie parameters: " + jsonArray.toString());

                    //Send Broad cast to Third party app's Activity or Fragment (For showing Current service data on UI).
                    //If Third party app's activity or Fragment has registerd itself to receive service data updates broadcast.
                    if (type != null && type.equalsIgnoreCase("send_offline_data")) {
                        //do not broadcast data to UI if data is from Offline source(is picked from db)

                    } else {
                        //Send Broadcast To UI Elemet to show Current Servcie data to app user.
                        Intent serviceDataUpdateBroadcast = new Intent("iot_sdk_mobile_android_service_data");
                        serviceDataUpdateBroadcast.putExtra("service_data", jsonArray.toString());
                        getApplicationContext().sendBroadcast(serviceDataUpdateBroadcast);
                    }

                    //Calling Publish API to Upload data onto Server
                    String body = "data=" + jsonArray.toString();
                    Response response = RESTClient.updateDataSynchronusly(getApplicationContext(), sdkSPUtils.getString(SdkSPUtils.API_SERVICE), body);

                    //Getting Response OF Publish API
                    if (response != null) {
                        if (response.isSuccessful()) {
                            String res = null;
                            try {
                                res = response.body().string();
                                Log.i("xFusionAndroidSdk", "DataIntentService - Data send to server API response: " + res);

                                JSONObject jsonObject = new JSONObject(res);

                                //Check if data is ppublished on server or not
                                if (jsonObject.getBoolean("valid")) {
                                    //Data published successfully
                                    Log.i("xFusionAndroidSdk", "DataIntentService - Data published to server Successfully");
                                    //Check if sent data is from local db( Offline stored data when network was not avaialble)
                                    if (type != null && type.equalsIgnoreCase("send_offline_data")) {
                                        //All Offline ServiceData has been sent to sever successfully. Now Delete them from DB
                                        DatabaseHandler.deleteAllRecords();
                                        Log.i("xFusionAndroidSdk", "DataIntentService - Data published to server was offline data. Deleted from Lcoal DB");
                                    } else {
                                        //Check if Local DB has any recorded ServcieData
                                        if (DatabaseHandler.getRecordsCount() > 0) {
                                            // Start the service again to send un-synced data to server from local DB,
                                            Intent service = new Intent(getApplicationContext(), DataIntentService.class);
                                            service.putExtra("type", "send_offline_data");
                                            startService(service);

                                            Log.i("xFusionAndroidSdk", "DataIntentService - More offline data found. request to start service again");
                                        }
                                    }
                                } else {
                                    //Data failed to published.
                                    Log.i("xFusionAndroidSdk", "DataIntentService - Data failed to published");
                                    //Check if sent data is from local db( Offline stored data when network was not avaialble)
                                    if (type != null && type.equalsIgnoreCase("send_offline_data")) {
                                        // Do not store again in db if data was picked from offline source(DB)
                                        Log.i("xFusionAndroidSdk", "DataIntentService - Data was from offline source. Do not store it again in Local DB");
                                    } else {
                                        //Do store in db
                                        Log.i("xFusionAndroidSdk", "DataIntentService - Data was live. store it in Local DB for next sync");
                                        processOfflineData(type, serviceDataModelsList);
                                    }

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.i("xFusionAndroidSdk", "DataIntentService - Data send to server API response: Request failure");
                            //Data publish Un-successfull
                            //Process data for offline
                            processOfflineData(type, serviceDataModelsList);
                        }
                    } else {
                        Log.i("xFusionAndroidSdk", "DataIntentService - API Response Null");
                        //Data publish Un-successfull
                        //Process data for offline
                        processOfflineData(type, serviceDataModelsList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //Network Not Available
                //save all Service Data into LocalDB for sending them to Server when network becomes available.
                processOfflineData(type, serviceDataModelsList);
            }
        }

        // Release the wake lock provided by the SdkWakefulBroadcastReceiver.
        SdkWakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void processOfflineData(String type, List<ServiceDataModel> serviceDataModelsList) {
        if (type != null && type.equalsIgnoreCase("send_offline_data")) {
            //do not save data again in DB if data is from Offline source(is picked from db)
        } else {
            //Insert all current records in db.
            DatabaseHandler.saveRecordsInDB(serviceDataModelsList);
            Log.i("xFusionAndroidSdk", "data inserted into DB");
        }
    }


    private Location compareNewLocationWithOldLocation(Location newLocation) {
        //Get last location from SP
        SdkSPUtils sdkSPUtils = new SdkSPUtils(getApplicationContext());
        String Latitude = sdkSPUtils.getString("Latitude");
        if (Latitude == null || Latitude.isEmpty())
            return newLocation;
        else {
            //Get Last Valid Location which was sent to Server
            String Longitude = sdkSPUtils.getString("Longitude");
            String accuracy = sdkSPUtils.getString("Location Accuracy");
            String speed = sdkSPUtils.getString("Location Speed");
            String provider = sdkSPUtils.getString("Location Provider");

            Location lastLocation = new Location("lastLocation");
            lastLocation.setLatitude(Double.parseDouble(Latitude));
            lastLocation.setLongitude(Double.parseDouble(Longitude));
            lastLocation.setSpeed(Float.parseFloat(speed));
            lastLocation.setAccuracy(Float.parseFloat(accuracy));
            lastLocation.setProvider(provider);

            //compare locations
            boolean isBetterLocation = LocationFilter.isBetterLocation(newLocation, lastLocation);
            if (isBetterLocation)
                return newLocation;
            else
                return lastLocation;
        }
    }
}
