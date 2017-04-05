package com.teramatrix.xfusionlibrary;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.telephony.TelephonyManager;


import com.teramatrix.xfusionlibrary.controller.RESTClient;
import com.teramatrix.xfusionlibrary.database.DatabaseHandler;
import com.teramatrix.xfusionlibrary.exception.InvalidRequestParametersException;
import com.teramatrix.xfusionlibrary.exception.UnAuthorizedAccess;
import com.teramatrix.xfusionlibrary.receivers.LocationUpdateReceiver;
import com.teramatrix.xfusionlibrary.receivers.SdkWakefulBroadcastReceiver;
import com.teramatrix.xfusionlibrary.receivers.ServiceDataUpdateReceiver;
import com.teramatrix.xfusionlibrary.service.LocationTrackingServcie;
import com.teramatrix.xfusionlibrary.util.AlarmManagerUtil;
import com.teramatrix.xfusionlibrary.util.PermissionsUtils;
import com.teramatrix.xfusionlibrary.util.SdkSPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by arun.singh on 1/19/2017.
 * This is main Interface for user to access sdk services.
 */

public class IoTSDK {

    public static String SERVICE_EXECUTE_MODE_ALWAYS = "always_run";
    public static String SERVICE_EXECUTE_MODE_ONLY_FORGROUND = "run_only_in_forground";
    public static String DEVICE_PARAMETERS_PHONE = "device_parameters_phone";
    public static String DEVICE_PARAMETERS_NETWORKS = "device_parameters_network";
    public static String DEVICE_PARAMETERS_TRAFIC = "device_parameters_trafic";
    private static IoTSDK instance;

    //Testing
    private String api_end_point = "http://180.149.46.100:7878";
    //Development
    //private String api_end_point = "http://180.149.46.100:4444";

    private Context context;

    /*Location tracking Service variables*/
    private boolean is_location_tracking_enabled = false;
    private int locationUpdateInterval = 20;
    private String service_execute_mode;

    /* Device/App data API service variables*/
    private int device_data_api_update_interval = 120;

    private ArrayList<String> devcieParamsListToTrack = new ArrayList<>();
    private Call call;

    private IoTSDK(Context context) {
        this.context = context;
        this.service_execute_mode = SERVICE_EXECUTE_MODE_ALWAYS;

        devcieParamsListToTrack.add(DEVICE_PARAMETERS_PHONE);
        devcieParamsListToTrack.add(DEVICE_PARAMETERS_NETWORKS);
        devcieParamsListToTrack.add(DEVICE_PARAMETERS_TRAFIC);

        //API configuration
        SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
        sdkSPUtils.setValue(SdkSPUtils.API_END_POINT, api_end_point);
        sdkSPUtils.setValue(SdkSPUtils.GATEWAY_ID, "3");
        sdkSPUtils.setValue(SdkSPUtils.PROTOCOL, "https");

    }

    public static IoTSDK getInstance(Context context) {
        instance = new IoTSDK(context);
        return instance;
    }

    public int getLocationUpdateInterval() {
        return locationUpdateInterval;
    }

    public void setLocationUpdateInterval(int locationUpdateInterval) {
        this.locationUpdateInterval = locationUpdateInterval;
    }


    public void setAPI_END_POINT(String api_end_point) {
        this.api_end_point = api_end_point;
        new SdkSPUtils(context).setValue(SdkSPUtils.API_END_POINT, api_end_point);
    }


    /*Service Execute mode setter getter*/
    public void setLocationTrackingServiceExecuteMode(String execute_mode) {
        service_execute_mode = execute_mode;
    }

    public String getLocationTrackingServcieExecuteMode() {
        return service_execute_mode;
    }

    public boolean isLocationTrackingEnabled() {
        return is_location_tracking_enabled;
    }

    public void setLocationTrackingEnabled(boolean is_location_tracking_enabled) {
        this.is_location_tracking_enabled = is_location_tracking_enabled;
    }

    public int getDevceiDataAPIUpdateInterval() {
        int interval = new SdkSPUtils(context).getInt(SdkSPUtils.DEVCIE_DATA_API_UPDATE_INTERVAL);
        return interval == 0?device_data_api_update_interval:interval;
    }

    public void setDevceiDataAPIUpdateInterval(int device_data_update_interval_in_seconds) {
        new SdkSPUtils(context).setValue(SdkSPUtils.DEVCIE_DATA_API_UPDATE_INTERVAL, device_data_update_interval_in_seconds);
    }

    public void setDeviceParametersToTrack(String... params) {

        devcieParamsListToTrack = new ArrayList<>();
        for (String param : params) {
            devcieParamsListToTrack.add(param);
        }
    }

    public void setGatewayID(String gateway_id) {
        new SdkSPUtils(context).setValue(SdkSPUtils.GATEWAY_ID, gateway_id);
    }

    public String getGatewayId() {
        return new SdkSPUtils(context).getString(SdkSPUtils.GATEWAY_ID);
    }

    public void setProtocol(String protocol) {
        new SdkSPUtils(context).setValue(SdkSPUtils.PROTOCOL, protocol);
    }

    public String protocol() {
        return new SdkSPUtils(context).getString(SdkSPUtils.PROTOCOL);
    }

    public void requestPermission(Activity activity) {
        new PermissionsUtils().requestForPermission(activity);
    }

    /*Get user's session credentials, these methods returns valid vales only after user login process */
    public String getToken() {
        return new SdkSPUtils(context).getString(SdkSPUtils.TOKEN);
    }

    public String getUserKey() {
        return new SdkSPUtils(context).getString(SdkSPUtils.USER_KEY);
    }

    public String getUserID() {
        return new SdkSPUtils(context).getString(SdkSPUtils.USER_ID);
    }

    public String getAccessKey() {
        return new SdkSPUtils(context).getString(SdkSPUtils.ACCESS_KEY);
    }

    public String getDeviceName() {
        return new SdkSPUtils(context).getString(SdkSPUtils.DEVCIE_NAME);
    }


    /*Method to initialize sdk services*/
    public void initService() throws UnAuthorizedAccess {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(context);

        if (sdkSPUtils.getString(SdkSPUtils.TOKEN).isEmpty() || sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY).isEmpty())
            throw new UnAuthorizedAccess("Access Token or Access Key is not found. Login to get these credential");


        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            /*IF Device gps is off ,alert user to turn it on*/
            buildAlertMessageNoGps((Activity) context);
        } else {

            //save module configuration set by user
            sdkSPUtils.setValue(SdkSPUtils.LOCATION_UPDATE_INTERVAL, locationUpdateInterval);

            sdkSPUtils.setValue(SdkSPUtils.SERVICE_EXECUTE_MODE, service_execute_mode);

            sdkSPUtils.setValue(IoTSDK.DEVICE_PARAMETERS_PHONE, false);
            sdkSPUtils.setValue(IoTSDK.DEVICE_PARAMETERS_NETWORKS, false);
            sdkSPUtils.setValue(IoTSDK.DEVICE_PARAMETERS_TRAFIC, false);
            for (String param : devcieParamsListToTrack) {
                sdkSPUtils.setValue(param, true);
            }


            //Do not start Location Tracking service if user has set it disabled. by default it is enabled
            if (is_location_tracking_enabled) {

                //Start Location Tracking if only it is not running already
                if (!new SdkSPUtils(context).getBoolean(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING)) {
                    Intent locationServiceIntent = new Intent(context, LocationTrackingServcie.class);
                    locationServiceIntent.setAction(LocationTrackingServcie.START_FOREGROUND_SERVICE);
                    context.startService(locationServiceIntent);
                }
            }

            //Configure Alarm(It invokes device/app data api continuosly at each predefined interval)
            //If already configured,skip it.
            if (!sdkSPUtils.getBoolean(SdkSPUtils.IS_REPEATING_ALARM_SET)) {

                /*AlarmManager alarmMgr = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);
                Intent receiverIntent = new Intent(context.getApplicationContext(), SdkWakefulBroadcastReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, receiverIntent, 0);

                alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        1000 * getDevceiDataAPIUpdateInterval(),
                        1000 * getDevceiDataAPIUpdateInterval(), alarmIntent);

                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        1000 * getDevceiDataAPIUpdateInterval(),
                        alarmIntent);
*/
                AlarmManagerUtil.setAlarm(context.getApplicationContext(),1000 * getDevceiDataAPIUpdateInterval(),SdkWakefulBroadcastReceiver.class);




                sdkSPUtils.setValue(SdkSPUtils.IS_REPEATING_ALARM_SET, true);
            }

        }
    }

    /*Alert message if device GPS is OFF*/
    private void buildAlertMessageNoGps(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("Location Service Disabled");
        builder.setMessage("Please enable location services.")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*User can stop sdk servcie by calling this method*/
    public void stopIoTServcies() {

        //Set servcies running status flag
        new SdkSPUtils(context).setValue(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING, false);
        new SdkSPUtils(context).setValue(SdkSPUtils.IS_REPEATING_ALARM_SET, false);

        //Stop Location Traking Service
        Intent locationServiceIntent = new Intent(context, LocationTrackingServcie.class);
        context.stopService(locationServiceIntent);

        //Cancel Registerd Alarm so no more Data API invokation occur.
        cancelRepeatingAlarm();
    }

    /*For canceling alarm mangaer*/
    private void cancelRepeatingAlarm() {
        Intent receiverIntent = new Intent(context, SdkWakefulBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
        SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
        sdkSPUtils.setValue(SdkSPUtils.IS_REPEATING_ALARM_SET, false);
    }

    /*Logout - Client App has to call this method while log-out from app*/
    public void logout() {
        //Stop All background services
        stopIoTServcies();

        //clear cofiguration data from SharedPreference
        new SdkSPUtils(context).clearPreferences();
        instance = null;

        //Delete Offline database
        DatabaseHandler.deleteAllRecords();
    }


    /*Login method */
    public void login(final Activity activity, String username, String password, final IUserAuthorizationCallback iUserAuthorizationCallback) throws InvalidRequestParametersException {

        if (activity == null)
            throw new InvalidRequestParametersException("Activity instance can not be null");
        else if (username == null || username.isEmpty())
            throw new InvalidRequestParametersException("Username can not be empty");
        else if (password == null || password.isEmpty())
            throw new InvalidRequestParametersException("Password can not be empty");
        else if (iUserAuthorizationCallback == null)
            throw new InvalidRequestParametersException("IUserAuthorizationCallback can not be null");

        //call auth Api async

        String body = "username=" + username +
                "&password=" + password +
                "&applicationid=" + RESTClient.APP_ID;
        call = RESTClient.Login(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                System.out.println("Auth onFailure");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iUserAuthorizationCallback.onFailure("Auth onFailure");
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        System.out.println("Auth Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {

                                JSONObject jsonObject1 = jsonObject.getJSONObject("object");
                                String access_token = jsonObject1.getString("access_token");
                                String message = jsonObject1.getString("message");
                                String userKey = jsonObject1.getString("userKey");
                                String user_id = jsonObject1.getString("user_id");
                                String access_key = jsonObject1.getString("access_key");
                                String roles_name = jsonObject1.getString("roles_name");
                                String roles_id = jsonObject1.getString("roles_id");

                                SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
                                sdkSPUtils.setValue(SdkSPUtils.TOKEN, access_token);
                                sdkSPUtils.setValue(SdkSPUtils.USER_KEY, userKey);
                                sdkSPUtils.setValue(SdkSPUtils.USER_ID, user_id);
                                sdkSPUtils.setValue(SdkSPUtils.ACCESS_KEY, access_key);
                                sdkSPUtils.setValue(SdkSPUtils.ROLES_NAME, roles_name);
                                sdkSPUtils.setValue(SdkSPUtils.ROLES_ID, roles_id);
                                sdkSPUtils.setValue(SdkSPUtils.DEVCIE_NAME, ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId() + "_PLATFORM_SDK_" + user_id + "2123");

                                registerDeviceOnPlatform(activity, iUserAuthorizationCallback);
                            } else {
                                iUserAuthorizationCallback.onFailure("Invalid request");
                            }
                        } else {
                            iUserAuthorizationCallback.onFailure("Response format is not correct");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iUserAuthorizationCallback.onFailure("Error in parsing response");
                            }
                        });
                    }
                } else {
                    System.out.println("Auth Fail ");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iUserAuthorizationCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }


    private void registerDeviceOnPlatform(final Activity activity, final IUserAuthorizationCallback iUserAuthorizationCallback) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(activity);
        String body = "token=" + sdkSPUtils.getString(SdkSPUtils.TOKEN) +
                "&userKey=" + sdkSPUtils.getString(SdkSPUtils.USER_KEY) +
                "&user_id=" + sdkSPUtils.getString(SdkSPUtils.USER_ID) +
                "&device_id=" + sdkSPUtils.getString(SdkSPUtils.DEVCIE_NAME) +
                "&gatewayid=" + sdkSPUtils.getString(SdkSPUtils.GATEWAY_ID) +
                "&protocol=" + sdkSPUtils.getString(SdkSPUtils.PROTOCOL) +
                "&access_key=" + sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY);

        call = RESTClient.registerDevice(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        iUserAuthorizationCallback.onFailure("Registration onFailure");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String res = response.body().string();
                        System.out.println("Devcie Register Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("object");
                                if (jsonArray.length() > 0) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                    String port = jsonObject1.getString("port");
                                    String api_url = jsonObject1.getString("api_url");
                                    String id = jsonObject1.getString("id");

                                    SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
                                    sdkSPUtils.setValue(SdkSPUtils.API_SERVICE, api_url);
                                    sdkSPUtils.setValue(sdkSPUtils.DEVCIE_ID, id);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                iUserAuthorizationCallback.onSuccess(res);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iUserAuthorizationCallback.onFailure("Request not valid");
                                    }
                                });
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iUserAuthorizationCallback.onFailure("Response format is incorrect");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iUserAuthorizationCallback.onFailure("Error in parsing response");
                            }
                        });

                    } finally {
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iUserAuthorizationCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }

    //Register Location update Receiver from Third party app's activity/Fragemnt to get Location data updates
    public static LocationUpdateReceiver registerLocationUpdateReeiver(LocationUpdateReceiver.INotifyLocationUpdates activity) {
        LocationUpdateReceiver locationUpdateReceiver = new LocationUpdateReceiver(activity);
        ((Activity) activity).registerReceiver(locationUpdateReceiver, new IntentFilter("iot_sdk_mobile_android_location_data"));
        return locationUpdateReceiver;
    }

    //Un-register Location update Receiver from Third party app's activity/Fragemnt to not receive Location data updates
    public static void unregisterLocationUpdateReeiver(Activity activity, LocationUpdateReceiver locationUpdateReceiver) {
        activity.unregisterReceiver(locationUpdateReceiver);
    }


    //Register Device's service data update receiver from Third party app's activity/Fragemnt to get service data updates
    public static ServiceDataUpdateReceiver registerServiceDataUpdateReeiver(ServiceDataUpdateReceiver.INotifyServcieDataUpdates activity) {
        ServiceDataUpdateReceiver serviceDataUpdateReceiver = new ServiceDataUpdateReceiver(activity);
        ((Activity) activity).registerReceiver(serviceDataUpdateReceiver, new IntentFilter("iot_sdk_mobile_android_service_data"));
        return serviceDataUpdateReceiver;
    }

    //Un-register Device's service data update receiver from Third party app's activity/Fragemnt to not receive service data updates
    public static void unregisterServiceDataUpdateReeiver(Activity activity, ServiceDataUpdateReceiver serviceDataUpdateReceiver) {
        activity.unregisterReceiver(serviceDataUpdateReceiver);
    }


    //get list of all Country
    public void getCountryList(final Activity activity, final ICountryListCallback iCountryListCallback) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(activity);
        String body = "token=" + sdkSPUtils.getString(SdkSPUtils.TOKEN) +
                "&access_key=" + sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY);


        call = RESTClient.getCountryLocationList(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        iCountryListCallback.onFailure("Failed to fetch data");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        System.out.println("Devcie Register Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {
                                iCountryListCallback.onSuccess(res);
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iCountryListCallback.onFailure("Request not valid");
                                    }
                                });
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iCountryListCallback.onFailure("Response format is incorrect");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iCountryListCallback.onFailure("Error in parsing response");
                            }
                        });

                    } finally {
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iCountryListCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }

    //get list of all states assocoated with a contry id
    public void getStateList(final Activity activity, final IStateListCallback iStateListCallback, String country_id) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(activity);
        String body = "token=" + sdkSPUtils.getString(SdkSPUtils.TOKEN) +
                "&access_key=" + sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY) +
                "&country_id=" + country_id;

        call = RESTClient.getStateLocationList(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        iStateListCallback.onFailure("Failed to fetch data");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        System.out.println("Devcie Register Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {
                                iStateListCallback.onSuccess(res);
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iStateListCallback.onFailure("Request not valid");
                                    }
                                });
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iStateListCallback.onFailure("Response format is incorrect");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iStateListCallback.onFailure("Error in parsing response");
                            }
                        });

                    } finally {
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iStateListCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }

    //get list of all city assocoated with a state id
    public void getCityList(final Activity activity, final ICityListCallback iCityListCallback, String state_id) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(activity);
        String body = "token=" + sdkSPUtils.getString(SdkSPUtils.TOKEN) +
                "&access_key=" + sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY) +
                "&state_id=" + state_id;

        call = RESTClient.getCityLocationList(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iCityListCallback.onFailure("Failed to fetch data");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        System.out.println("Devcie Register Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {
                                iCityListCallback.onSuccess(res);
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iCityListCallback.onFailure("Request not valid");
                                    }
                                });
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iCityListCallback.onFailure("Response format is incorrect");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iCityListCallback.onFailure("Error in parsing response");
                            }
                        });

                    } finally {
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iCityListCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }

    //get list of all organizaiton
    public void getOrganizationList(final Activity activity, final IOrganizationListCallback iOrganizationListCallback) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(activity);
        String body = "token=" + sdkSPUtils.getString(SdkSPUtils.TOKEN) +
                "&access_key=" + sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY) +
                "&userKey=" + sdkSPUtils.getString(SdkSPUtils.USER_KEY) +
                "&user_id=" + sdkSPUtils.getString(SdkSPUtils.USER_ID);


        call = RESTClient.getOrganizationList(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iOrganizationListCallback.onFailure("Failed to fetch data");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        System.out.println("Devcie Register Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {
                                iOrganizationListCallback.onSuccess(res);
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iOrganizationListCallback.onFailure("Request not valid");
                                    }
                                });
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iOrganizationListCallback.onFailure("Response format is incorrect");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iOrganizationListCallback.onFailure("Error in parsing response");
                            }
                        });

                    } finally {
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iOrganizationListCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }

    //add registed device to an organization
    public void addDeviceToOrganization(final Activity activity, final ICreateDeviceCallback iCreateDeviceCallback, String device_alias, String organization_ids, String device_country, String device_state, String device_city) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(activity);

        String body = "token=" + sdkSPUtils.getString(SdkSPUtils.TOKEN) +
                "&userKey=" + sdkSPUtils.getString(SdkSPUtils.USER_KEY) +
                "&user_id=" + sdkSPUtils.getString(SdkSPUtils.USER_ID) +
                "&name=" + sdkSPUtils.getString(SdkSPUtils.DEVCIE_NAME) +
                "&alias=" + device_alias +
                "&ipaddress=" + "" +
                "&lattitude=" + "0.0" +
                "&longitutde=" + "0.0" +
                "&mac_address=" + "" +
                "&description=" + "" +
                "&elevation=" + "" +
                "&hardware_version=" + "" +
                "&software_version=" + "" +
                "&device_id=" + sdkSPUtils.getString(SdkSPUtils.DEVCIE_ID) +
                "&device_type=" + "8" +
                "&device_model=" + "23" +
                "&device_technology=" + "9" +
                "&device_vendor=" + "11" +
                "&device_country=" + device_country +
                "&device_state=" + device_state +
                "&device_city=" + device_city +
                "&parentDevice=" + "" +
                "&organization_ids=" + organization_ids +
                "&properties_ids=" + "" +
                "&properties_names=" + "" +
                "&properties_values=" + "" +
                "&is_configurable=" + "0" +
                "&tags=" + "" +
                "&access_key=" + sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY);


        call = RESTClient.createDevice(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        iCreateDeviceCallback.onFailure("Device creation failed");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String res = response.body().string();
                        System.out.println("Devcie Register Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iCreateDeviceCallback.onSuccess(res);
                                    }
                                });

                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iCreateDeviceCallback.onFailure("Request not valid");
                                    }
                                });
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iCreateDeviceCallback.onFailure("Response format is incorrect");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iCreateDeviceCallback.onFailure("Error in parsing response");
                            }
                        });

                    } finally {
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iCreateDeviceCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }


    //Interface for delevering Login API response
    public interface IUserAuthorizationCallback {
        void onSuccess(String response_messg) throws JSONException;

        void onFailure(String response_messg);
    }

    //Interface for delevering Coutnry API response
    public interface ICountryListCallback {
        void onSuccess(String response_messg);

        void onFailure(String response_messg);
    }

    //Interface for delevering State API response
    public interface IStateListCallback {
        void onSuccess(String response_messg);

        void onFailure(String response_messg);
    }

    //Interface for delevering City API response
    public interface ICityListCallback {
        void onSuccess(String response_messg);

        void onFailure(String response_messg);
    }

    //Interface for delevering Organization API response
    public interface IOrganizationListCallback {
        void onSuccess(String response_messg);

        void onFailure(String response_messg);
    }

    //Interface for delevering Organization API response
    public interface ICreateDeviceCallback {
        void onSuccess(String response_messg);

        void onFailure(String response_messg);
    }



}
