package com.zhizulx.tt.config;

/**
 * @author : yingmu on 15-3-16.
 * @email : yingmu@mogujie.com.
 *
 */
public class UrlConstant {

    // 头像路径前缀
    public final static String AVATAR_URL_PREFIX = "";

    // 图片路径前缀
    public final static String PIC_URL_PREFIX = "http://192.168.1.9:8080/";

    // access 地址
    //public final static String BASE_ADDRESS = "http://119.23.248.206";
    public final static String BASE_ADDRESS = "http://39.108.92.92";
    public final static String ACCESS_MSG_ADDRESS = BASE_ADDRESS + ":8080/msg_server";
    //public final static String ACCESS_MSG_ADDRESS = "http://39.108.92.92:8080/msg_server";
    public final static String AVATAR_UPLOLAD_ADDRESS = BASE_ADDRESS + ":1122/pic/save.do";
    public final static String FEEDBACK_UPLOLAD_ADDRESS = BASE_ADDRESS + ":1122/feedback/save.do";
    public final static String TRACK_UPLOLAD_ADDRESS = BASE_ADDRESS + ":1122/track/save.do";
    public final static String AVATAR_DOWNLOAD_ADDRESS = BASE_ADDRESS + ":8521/";
    //public final static String ACCESS_MSG_ADDRESS = "http://192.168.1.12:8080/msg_server";

}
