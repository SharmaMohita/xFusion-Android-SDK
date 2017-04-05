package com.example.satku.xfusion_platformapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satku on 4/4/2017.
 */

public class device {
    private String device_id;
    private String port;
    private String api_url;
    private String id;
    private String ip_address;
    private String is_added_to_metadata;
    private String gateway_id;
    private String connection_parameters;
    private String status;

    public String getDevice_id() {
        return device_id;
    }

    public String getPort() {
        return port;
    }

    public String getApi_url() {
        return api_url;
    }

    public String getId() {
        return id;
    }

    public String getIp_address() {
        return ip_address;
    }

    public String getIs_added_to_metadata() {
        return is_added_to_metadata;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public String getConnection_parameters() {
        return connection_parameters;
    }

    public String getStatus() {
        return status;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;

    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setApi_url(String api_url) {
        this.api_url = api_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public void setIs_added_to_metadata(String is_added_to_metadata) {
        this.is_added_to_metadata = is_added_to_metadata;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public void setConnection_parameters(String connection_parameters) {
        this.connection_parameters = connection_parameters;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
