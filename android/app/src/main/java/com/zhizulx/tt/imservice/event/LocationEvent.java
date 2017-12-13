package com.zhizulx.tt.imservice.event;


import com.zhizulx.tt.DB.entity.GroupEntity;

import java.util.List;

/**
 * @author : yingmu on 14-12-30.
 * @email : yingmu@mogujie.com.
 */
public class LocationEvent {
    private Event event;

    private double dLongitude = 0.0;
    private double dLatitude = 0.0;
    private String cityName;
    private String weather;

    public LocationEvent(Event event){
        this.event = event;
    }

    public LocationEvent(Event event, GroupEntity groupEntity){
        this.event = event;
    }

    public enum Event{
        FRESH_EVENT,
        GET_EVENT,
        GET_WEATHER,
        SEND_WEATHER
    }

    public double getLongitude() {
        return dLongitude;
    }

    public void setLongitude(double dLongitude) {
        this.dLongitude = dLongitude;
    }

    public double getLatitude() {
        return dLatitude;
    }

    public void setLatitude(double dLatitude) {
        this.dLatitude = dLatitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }
}
