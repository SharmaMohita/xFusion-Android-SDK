package com.teramatrix.xfusionlibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import com.teramatrix.xfusionlibrary.database.ServiceDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by arun.singh on 1/20/2017.
 * <p>
 * This class is used for collecting device related data known as service data.
 * It collects Network ,Phone and Network Traffic data.
 */

public class DeviceServcieDataProvider {


    /*Phone Specific data*/
    public static ArrayList<ServiceDataModel> getPhoneData(Context context,String current_timestamp,String devcie_id) {

        ArrayList<ServiceDataModel> serviceDataModelsList = new ArrayList<ServiceDataModel>();

        try {
            TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
            String phone_type = "";
            switch (tm.getPhoneType()) {
                case (TelephonyManager.PHONE_TYPE_CDMA):
                    phone_type = "CDMA";
                    break;
                case (TelephonyManager.PHONE_TYPE_GSM):
                    phone_type = "GSM";
                    ;
                    break;
                case (TelephonyManager.PHONE_TYPE_NONE):
                    phone_type = "NONE";
                    break;
                default:
                    phone_type = "UNKNOWN";
            }
            String sim_state = "";
            switch (tm.getSimState()) {
                case (TelephonyManager.SIM_STATE_ABSENT):
                    sim_state = "ABSENT";
                    break;
                case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):
                    sim_state = "NETWORK_LOCKED";
                    ;
                    break;
                case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
                    sim_state = "PIN_REQUESTED";
                    break;
                case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
                    sim_state = "PUK_REQUESTED";
                    break;
                case (TelephonyManager.SIM_STATE_READY):
                    sim_state = "READY";
                    break;
                case (TelephonyManager.SIM_STATE_UNKNOWN):
                    sim_state = "UNKNOWN";
                    break;
                default:
                    sim_state = "-";
            }
            serviceDataModelsList.add(new ServiceDataModel("Phone Type","Devcie Parameters_invent",current_timestamp,current_timestamp, phone_type,devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("IMEI Number","Devcie Parameters_invent",current_timestamp,current_timestamp, tm.getDeviceId(),devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Manufacturer","Devcie Parameters_invent",current_timestamp,current_timestamp, android.os.Build.MANUFACTURER,devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Model","Devcie Parameters_invent",current_timestamp,current_timestamp, android.os.Build.MODEL,devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Software Version","Devcie Parameters_invent",current_timestamp,current_timestamp, tm.getDeviceSoftwareVersion(), devcie_id));

            serviceDataModelsList.add(new ServiceDataModel("Sim Serial Number","Phone Parameters",current_timestamp,current_timestamp, tm.getSimSerialNumber(), devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("SIM Country ISO","Phone Parameters",current_timestamp,current_timestamp, tm.getSimCountryIso(),devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("SIM State","Phone Parameters",current_timestamp,current_timestamp, sim_state, devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("SubscriberID","Phone Parameters",current_timestamp,current_timestamp, tm.getSubscriberId(),devcie_id));

            //Record Device Battery State
            HashMap<String, String> batteryState = BatteryMonitor.getBatteryState(context);
            for(String key:batteryState.keySet())
            {
                serviceDataModelsList.add(new ServiceDataModel(key,"Phone Parameters",current_timestamp,current_timestamp,batteryState.get(key),devcie_id));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceDataModelsList;

    }

    /*Network specific data*/
    public static ArrayList<ServiceDataModel> getNetworkData(Context context,String current_timestamp,String devcie_id) {

        ArrayList<ServiceDataModel> serviceDataModelsList = new ArrayList<ServiceDataModel>();


        try {
            //Get the instance of TelephonyManager
            TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
//        Get the phone type
            String strphoneType = "";
            switch (tm.getPhoneType()) {
                case (TelephonyManager.PHONE_TYPE_CDMA):
                    strphoneType = "CDMA";
                    break;
                case (TelephonyManager.PHONE_TYPE_GSM):
                    strphoneType = "GSM";
                    break;
                case (TelephonyManager.PHONE_TYPE_NONE):
                    strphoneType = "NONE";
                    break;
            }
            //Call State
            String call_state = "";
            switch (tm.getCallState()) {
                case (TelephonyManager.CALL_STATE_IDLE):
                    call_state = "IDLE";
                    break;
                case (TelephonyManager.CALL_STATE_OFFHOOK):
                    call_state = "OFF HOOK";
                    ;
                    break;
                case (TelephonyManager.CALL_STATE_RINGING):
                    call_state = "RINGING";
                    break;
            }
            String network_technology = "";
            switch (tm.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    network_technology = "2G GPRS";
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    network_technology = "2G EDGE";
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    network_technology = "2G CDMA";
                    break;
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    network_technology = "2G 1xRTT";
                    break;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    network_technology = "2G IDEN";
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    network_technology = "3G UMTS";
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    network_technology = "3G EVDO_0";
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    network_technology = "3G EVDO_A";
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    network_technology = "3G HSDPA";
                    break;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    network_technology = "3G HSUPA";
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    network_technology = "3G HSPA";
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    network_technology = "3G EVDO_B";
                    break;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    network_technology = "3G EHRPD";
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    network_technology = "3G HSPAP";
                    break;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    network_technology = "4G LTE";
                    break;
                default:
                    network_technology = "UNKNOWN";
            }
            //Network Parameters
            serviceDataModelsList.add(new ServiceDataModel("Network Operator","Network Parameters",current_timestamp,current_timestamp, tm.getNetworkOperatorName(),devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Network State","Network Parameters",current_timestamp,current_timestamp, getConnectivityStatus(context),devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Technology","Network Parameters",current_timestamp,current_timestamp, network_technology,devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Network Country ISO","Network Parameters",current_timestamp,current_timestamp, tm.getNetworkCountryIso(),devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Phone Network Type","Network Parameters",current_timestamp,current_timestamp, strphoneType,devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("In Roaming","Network Parameters",current_timestamp,current_timestamp, tm.isNetworkRoaming() + "", devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Call State","Network Parameters",current_timestamp,current_timestamp, call_state,devcie_id));

            ArrayList<String> property_strings = new ArrayList<String>();
            List<CellInfo> cellInfos = tm.getAllCellInfo();
            if (cellInfos != null) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i).isRegistered()) {
                        if (cellInfos.get(i) instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) tm.getAllCellInfo().get(0);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

                            cellSignalStrengthWcdma.getAsuLevel();


                            property_strings.add("Signal Strength(dbm)," + String.valueOf(cellSignalStrengthWcdma.getDbm()));
                            property_strings.add("Signal Level," + cellSignalStrengthWcdma.getLevel());
                            property_strings.add("CID," + cellInfoWcdma.getCellIdentity().getCid() + "");
                            property_strings.add("LAC," + cellInfoWcdma.getCellIdentity().getLac());
                            property_strings.add("MCC," + cellInfoWcdma.getCellIdentity().getMcc());
                            property_strings.add("MNC," + cellInfoWcdma.getCellIdentity().getMnc());
                            property_strings.add("PSC," + cellInfoWcdma.getCellIdentity().getPsc());
//                        property_strings.add("WCDMA UARFCN,"+cellInfoWcdma.getCellIdentity().getUarfcn());

                        } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                            CellInfoGsm cellInfogsm = (CellInfoGsm) tm.getAllCellInfo().get(0);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();


                            property_strings.add("Signal Strength(dbm)," + String.valueOf(cellSignalStrengthGsm.getDbm()));
                            property_strings.add("Signal Level," + cellSignalStrengthGsm.getLevel());
                            property_strings.add("CID," + cellInfogsm.getCellIdentity().getCid());
                            property_strings.add("LAC," + cellInfogsm.getCellIdentity().getLac());
                            property_strings.add("MCC," + cellInfogsm.getCellIdentity().getMcc());
                            property_strings.add("MNC," + cellInfogsm.getCellIdentity().getMnc());
                            property_strings.add("PSC," + cellInfogsm.getCellIdentity().getPsc());
//                        property_strings.add("GSM ARFCN,"+cellInfogsm.getCellIdentity().getArfcn());


                        } else if (cellInfos.get(i) instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) tm.getAllCellInfo().get(0);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();

                            property_strings.add("Signal Strength(dbm)," + String.valueOf(cellSignalStrengthLte.getDbm()));
                            property_strings.add("Signal Level," + cellSignalStrengthLte.getLevel());
                            property_strings.add("CI," + cellInfoLte.getCellIdentity().getCi());
                            property_strings.add("TAC," + cellInfoLte.getCellIdentity().getTac());
                            property_strings.add("MCC," + cellInfoLte.getCellIdentity().getMcc());
                            property_strings.add("MNC," + cellInfoLte.getCellIdentity().getMnc());
                            property_strings.add("PCI," + cellInfoLte.getCellIdentity().getPci());

                        }

                    }
                }
                for (int i = 0; i < property_strings.size(); i++) {
                    serviceDataModelsList.add(new ServiceDataModel(property_strings.get(i).split(",")[0],"Network Parameters",current_timestamp,current_timestamp, property_strings.get(i).split(",")[1], devcie_id));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serviceDataModelsList;
    }

    /*Devcie Network Traffic specific data*/
    public static ArrayList<ServiceDataModel> getTraficData(Context context,String current_timestamp,String devcie_id) {

        ArrayList<ServiceDataModel> serviceDataModelsList = new ArrayList<ServiceDataModel>();

        try {
            //Get the instance of TelephonyManager
            TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));

            String data_state = "";
            switch (tm.getDataState()) {
                case (TelephonyManager.DATA_CONNECTED):
                    data_state = "CONNECTED";
                    break;
                case (TelephonyManager.DATA_CONNECTING):
                    data_state = "CONNECTING";
                    ;
                    break;
                case (TelephonyManager.DATA_DISCONNECTED):
                    data_state = "DISCONNECTED";
                    break;
                case (TelephonyManager.DATA_SUSPENDED):
                    data_state = "SUSPENDED";
                    break;
                default:
                    data_state = "-";
            }

            String data_activity = "";
            switch (tm.getDataActivity()) {
                case (TelephonyManager.DATA_ACTIVITY_INOUT):
                    data_activity = "INOUT";
                    break;
                case (TelephonyManager.DATA_ACTIVITY_IN):
                    data_activity = "IN";
                    ;
                    break;
                case (TelephonyManager.DATA_ACTIVITY_DORMANT):
                    data_activity = "DORMANT";
                    break;
                case (TelephonyManager.DATA_ACTIVITY_NONE):
                    data_activity = "NONE";
                    break;
                case (TelephonyManager.DATA_ACTIVITY_OUT):
                    data_activity = "OUT";
                    break;
                default:
                    data_activity = "-";
            }


            serviceDataModelsList.add(new ServiceDataModel("Data Activity","Trafic Parameters",current_timestamp,current_timestamp, data_activity,devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Data State","Trafic Parameters",current_timestamp,current_timestamp, data_state, devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Mobile Rx bytes","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getMobileRxBytes() + "", devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Mobile Tx bytes","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getMobileTxBytes() + "", devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Total Rx bytes","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getTotalRxBytes() + "", devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Total Tx bytes","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getTotalTxBytes() + "", devcie_id));

            serviceDataModelsList.add(new ServiceDataModel("Mobile Rx packets","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getMobileRxPackets() + "", devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Mobile Tx packets","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getMobileTxPackets() + "", devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Total Rx packets","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getTotalRxPackets() + "", devcie_id));
            serviceDataModelsList.add(new ServiceDataModel("Total Tx packets","Trafic Parameters",current_timestamp,current_timestamp, TrafficStats.getTotalTxPackets() + "", devcie_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceDataModelsList;

    }

    /*Get network connectivity status*/
    private static String getConnectivityStatus(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return "Wifi Connected";

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return "Mobile Data Connected";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Not Connected";
    }
}
