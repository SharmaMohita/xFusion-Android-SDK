package com.example.satku.xfusion_platformapp;

/**
 * Created by satku on 3/6/2017.
 */
import android.content.Context;
import android.content.Intent;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by satku on 1/25/2017.
 */
@Table(name = "MyLocation")
public class MyLocation extends Model {

    @Column(name = "latitude")
    public double latitude;

    @Column(name = "longitude")
    public double longitude;

    @Column(name = "logTime")
    public long logTime;


   /* public MyLocation() {
        super();
    }*/

    public MyLocation(double latitude, double longitude, long logTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.logTime = logTime;

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }
}