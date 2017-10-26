
package com.zhizulx.tt.utils;

import com.zhizulx.tt.config.UrlConstant;

public class UploadLogUtil {
    private String strUrl = UrlConstant.TRACK_UPLOLAD_ADDRESS;
    private String strCode = "";
    private String strContent = "";
    private String strUserID = "";

    private Runnable runnable = new Runnable(){
        @Override
        public void run() {
            MoGuHttpClient moGuHttpClient = new MoGuHttpClient();
            moGuHttpClient.uploadApkLog(strUrl, strCode, strContent, strUserID);
        }
    };

    public void uploadApkLog(String strCode, String strContent, String strUserID) {
        this.strCode = strCode;
        this.strContent = strContent;
        this.strUserID = strUserID;
        new Thread(runnable).start();
    }
}
