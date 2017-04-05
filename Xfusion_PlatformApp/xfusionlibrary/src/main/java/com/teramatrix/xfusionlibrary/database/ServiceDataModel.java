package com.teramatrix.xfusionlibrary.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by arun.singh on 3/1/2017.
 */
@Table(name = "ServiceDataModel")
public class ServiceDataModel extends Model {

    @Column(name = "data_source")
    public String data_source;
    @Column(name = "service_name")
    public String service_name;
    @Column(name = "check_timestamp")
    public String check_timestamp;
    @Column(name = "sys_timestamp")
    public String sys_timestamp;
    @Column(name = "current_value")
    public String current_value;
    @Column(name = "device_id")
    public String device_id;
    @Column(name = "status")
    public String status;



    public ServiceDataModel() {
        super();
    }

    public ServiceDataModel(String data_source, String service_name, String check_timestamp, String sys_timestamp, String current_value, String device_id) {
        super();
        this.data_source = data_source;
        this.service_name = service_name;
        this.check_timestamp = check_timestamp;
        this.sys_timestamp = sys_timestamp;
        this.current_value = current_value;
        this.device_id = device_id;
    }
}
