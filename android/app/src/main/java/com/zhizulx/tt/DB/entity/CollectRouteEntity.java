package com.zhizulx.tt.DB.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import java.util.ArrayList;
import java.util.List;

/**
 * Entity mapped to table cityInfo.
 */
public class CollectRouteEntity {
    private int dbId;
    private RouteEntity routeEntity;
    private String startDate;
    private String endDate;
    private String startTrafficNo;
    private String endTrafficNo;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public CollectRouteEntity() {
    }

    public CollectRouteEntity(int dbId, RouteEntity routeEntity, String startDate,
                              String startTrafficNo, String endTrafficNo) {
        this.dbId = dbId;
        this.routeEntity = routeEntity;
        this.startDate = startDate;
        this.startTrafficNo = startTrafficNo;
        this.endTrafficNo = endTrafficNo;
    }

    public int getDbId() {
        return dbId;
    }
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public RouteEntity getRouteEntity() {
        return routeEntity;
    }
    public void setRouteEntity(RouteEntity routeEntity) {
        this.routeEntity = routeEntity;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTrafficNo() {
        return startTrafficNo;
    }

    public void setStartTrafficNo(String startTrafficNo) {
        this.startTrafficNo = startTrafficNo;
    }

    public String getEndTrafficNo() {
        return endTrafficNo;
    }

    public void setEndTrafficNo(String endTrafficNo) {
        this.endTrafficNo = endTrafficNo;
    }
}