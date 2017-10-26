package com.zhizulx.tt.DB.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table TrafficInfo.
 */
public class TrafficEntity {

    private Long id;
    private int peerId;
    private int type;
    /** Not-null value. */
    private String no;
    /** Not-null value. */
    private String startCityCode;
    /** Not-null value. */
    private String startStation;
    /** Not-null value. */
    private String endCityCode;
    /** Not-null value. */
    private String endStation;
    /** Not-null value. */
    private String startTime;
    /** Not-null value. */
    private String endTime;
    /** Not-null value. */
    private String duration;
    private int price;
    /** Not-null value. */
    private String seatClass;
    private int select;
    private int version;
    private int status;
    private int created;
    private int updated;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public TrafficEntity() {
    }

    public TrafficEntity(Long id) {
        this.id = id;
    }

    public TrafficEntity(Long id, int peerId, int type, String no, String startCityCode, String startStation, String endCityCode, String endStation, String startTime, String endTime, String duration, int price, String seatClass, int select, int version, int status, int created, int updated) {
        this.id = id;
        this.peerId = peerId;
        this.type = type;
        this.no = no;
        this.startCityCode = startCityCode;
        this.startStation = startStation;
        this.endCityCode = endCityCode;
        this.endStation = endStation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.price = price;
        this.seatClass = seatClass;
        this.select = select;
        this.version = version;
        this.status = status;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPeerId() {
        return peerId;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /** Not-null value. */
    public String getNo() {
        return no;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setNo(String no) {
        this.no = no;
    }

    /** Not-null value. */
    public String getStartCityCode() {
        return startCityCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStartCityCode(String startCityCode) {
        this.startCityCode = startCityCode;
    }

    /** Not-null value. */
    public String getStartStation() {
        return startStation;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    /** Not-null value. */
    public String getEndCityCode() {
        return endCityCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEndCityCode(String endCityCode) {
        this.endCityCode = endCityCode;
    }

    /** Not-null value. */
    public String getEndStation() {
        return endStation;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    /** Not-null value. */
    public String getStartTime() {
        return startTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /** Not-null value. */
    public String getEndTime() {
        return endTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /** Not-null value. */
    public String getDuration() {
        return duration;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    /** Not-null value. */
    public String getSeatClass() {
        return seatClass;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
