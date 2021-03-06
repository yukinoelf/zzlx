package com.zhizulx.tt.DB.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table SightInfo.
 */
public class SightEntity {

    private Long id;
    private int peerId;
    /** Not-null value. */
    private String cityCode;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String pic;
    private int star;
    /** Not-null value. */
    private String tag;
    private int mustGo;
    /** Not-null value. */
    private String openTime;
    private int playTime;
    private int price;
    private double longitude;
    private double latitude;
    /** Not-null value. */
    private String address;
    /** Not-null value. */
    private String introduction;
    /** Not-null value. */
    private String introductionDetail;
    /** Not-null value. */
    private String startTime;
    /** Not-null value. */
    private String endTime;
    private int select;
    private int version;
    private int status;
    private int created;
    private int updated;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public SightEntity() {
    }

    public SightEntity(Long id) {
        this.id = id;
    }

    public SightEntity(Long id, int peerId, String cityCode, String name, String pic, int star, String tag, int mustGo, String openTime, int playTime, int price, double longitude, double latitude, String address, String introduction, String introductionDetail, String startTime, String endTime, int select, int version, int status, int created, int updated) {
        this.id = id;
        this.peerId = peerId;
        this.cityCode = cityCode;
        this.name = name;
        this.pic = pic;
        this.star = star;
        this.tag = tag;
        this.mustGo = mustGo;
        this.openTime = openTime;
        this.playTime = playTime;
        this.price = price;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.introduction = introduction;
        this.introductionDetail = introductionDetail;
        this.startTime = startTime;
        this.endTime = endTime;
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

    /** Not-null value. */
    public String getCityCode() {
        return cityCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getPic() {
        return pic;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    /** Not-null value. */
    public String getTag() {
        return tag;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getMustGo() {
        return mustGo;
    }

    public void setMustGo(int mustGo) {
        this.mustGo = mustGo;
    }

    /** Not-null value. */
    public String getOpenTime() {
        return openTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /** Not-null value. */
    public String getAddress() {
        return address;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setAddress(String address) {
        this.address = address;
    }

    /** Not-null value. */
    public String getIntroduction() {
        return introduction;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /** Not-null value. */
    public String getIntroductionDetail() {
        return introductionDetail;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIntroductionDetail(String introductionDetail) {
        this.introductionDetail = introductionDetail;
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
