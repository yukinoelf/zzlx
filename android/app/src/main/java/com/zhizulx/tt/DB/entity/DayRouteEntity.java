package com.zhizulx.tt.DB.entity;

import java.util.ArrayList;
import java.util.List;

public class DayRouteEntity {
    private List<Integer> sightIDList = new ArrayList<>();
    private List<Integer> hotelIDList = new ArrayList<>();

    public DayRouteEntity() {
    }

    public DayRouteEntity(List<Integer> sightIDList, List<Integer> hotelIDList) {
        this.sightIDList.addAll(sightIDList);
        this.hotelIDList.addAll(hotelIDList);
    }

    public void setSightIDList(List<Integer> sightIDList) {
        this.sightIDList.clear();
        this.sightIDList.addAll(sightIDList);
    }

    public List<Integer> getSightIDList() {
        return sightIDList;
    }

    public void setHotelIDList(List<Integer> hotelIDList) {
        this.hotelIDList.clear();
        this.hotelIDList.addAll(hotelIDList);
    }

    public List<Integer> getHotelIDList() {
        return hotelIDList;
    }
}
