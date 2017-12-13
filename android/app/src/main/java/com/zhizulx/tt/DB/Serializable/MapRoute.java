package com.zhizulx.tt.DB.Serializable;

import java.io.Serializable;

/**
 * Created by yuki on 2017/4/2.
 */

public class MapRoute implements Serializable {
    private String city;
    private double startLongitude;
    private double startLatitude;
    private double endLongitude;
    private double endLatitude;
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public double getStartLongitude() {
        return startLongitude;
    }
    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }
    public double getStartLatitude() {
        return startLatitude;
    }
    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }
    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }
    public double getEndLatitude() {
        return endLatitude;
    }
    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }
}
