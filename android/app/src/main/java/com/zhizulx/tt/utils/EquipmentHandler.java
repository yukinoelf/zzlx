package com.zhizulx.tt.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.zhizulx.tt.utils.FileUtil.AppFileDir;
import static com.zhizulx.tt.utils.FileUtil.SDCardRoot;

/**
 * Created by yuki on 2017/2/23.
 */


public class EquipmentHandler {
    private static EquipmentHandler INSTANCE = new EquipmentHandler();// CrashHandler实例
    private Context mContext;// 程序的Context对象
    private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
    private SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分

    /** 保证只有一个CrashHandler实例 */
    private EquipmentHandler() {

    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static EquipmentHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
    }

    public void collection() {
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveInfo2File();
    }

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();// 获得包管理器
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        String carrier= android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        info.put("manufacturer", carrier);
        info.put("model", model);

        WindowManager wmManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wmManager.getDefaultDisplay().getWidth();
        int height = wmManager.getDefaultDisplay().getHeight();
        info.put("resolution", height+"x"+width);
    }

    private String saveInfo2File() {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\r\n");
        }

        // 保存文件
        String fileName = "equipment.log";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(SDCardRoot + AppFileDir + File.separator + "log" + File.separator);
                if (!dir.exists())
                    dir.mkdir();
                FileOutputStream fos = new FileOutputStream(new File(dir,
                        fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean hasEquipmentInfo() {
        String fileName = "equipment.log";
        File dir = new File(SDCardRoot + AppFileDir + File.separator + "log" + File.separator);
        return dir.exists();
    }
}
