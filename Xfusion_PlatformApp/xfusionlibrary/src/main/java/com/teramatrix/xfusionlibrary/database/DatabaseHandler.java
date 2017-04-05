package com.teramatrix.xfusionlibrary.database;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by arun.singh on 3/1/2017.
 */

public class DatabaseHandler {


    //Save Servicedata Records in DB
    public static void saveRecordsInDB(List<ServiceDataModel> serviceDataModels) {
        ActiveAndroid.beginTransaction();
        try {
            for (ServiceDataModel serviceDataModel : serviceDataModels) {
                serviceDataModel.save();
            }
            Log.i("xFusionAndroidSdk", "DatabaseHandler ServcieData inserted in DB");
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    //Get all ServcieDataModel from DB
    public static List<ServiceDataModel> getAllServiceData() {
        return new Select()
                .from(ServiceDataModel.class)
                .execute();
    }

    //Get all ServcieDataModel from DB with Where clause
    public static List<ServiceDataModel> getAllServiceData(String column_name, String attribute_value) {
        return new Select()
                .from(ServiceDataModel.class)
                .where(column_name + " = ?", attribute_value)
                .execute();
    }

    //Get all ServcieDataModel from Db for whose given attribute have certain value.
    public static List<ServiceDataModel> findInDB(String column_name, String attribute_value) {
        try {
            return new Select()
                    .from(ServiceDataModel.class)
                    .where(column_name + " = ?", attribute_value)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Get all ServcieDataModel from Db for whose given attribute have certain value. ORDER wise
    public static List<ServiceDataModel> findInDB(String column_name, String attribute_value, String order) {
        try {
            return new Select()
                    .from(ServiceDataModel.class)
                    .where(column_name + " = ?", attribute_value)
                    .orderBy(order)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Get all ServcieDataModel from Db for whose given attribute have certain value. ORDER wise with limit
    public static List<ServiceDataModel> findInDB(String column_name, String attribute_value, String order, int limit) {
        try {
            return new Select()
                    .from(ServiceDataModel.class)
                    .where(column_name + " = ?", attribute_value)
                    .orderBy(order)
                    .limit(limit)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //get size of table
    public static int getRecordsCount() {
        return new Select().from(ServiceDataModel.class).execute().size();
    }

    //Delete all recods from ServiceDataModel table
    public static void deleteAllRecords() {
        new Delete().from(ServiceDataModel.class).execute();
    }

    //Delete all recods from ServiceDataModel table
    public static void deleteSelectedRecords(String where_column, String value) {
        new Delete().from(ServiceDataModel.class).where(where_column + " = ?", value).execute();
    }
}
