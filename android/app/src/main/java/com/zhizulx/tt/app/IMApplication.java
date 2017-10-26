package com.zhizulx.tt.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.utils.CrashHandler;
import com.zhizulx.tt.utils.ImageLoaderUtil;
import com.zhizulx.tt.utils.Logger;


public class IMApplication extends Application {

	private Logger logger = Logger.getLogger(IMApplication.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		logger.i("Application starts");
		CrashHandler handler = CrashHandler.getInstance();
		handler.init(getApplicationContext()); //在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器
		startIMService();
		ImageLoaderUtil.initImageLoaderConfig(getApplicationContext());
	}

	private void startIMService() {
		logger.i("start IMService");
		Intent intent = new Intent();
		intent.setClass(this, IMService.class);
		startService(intent);
	}

    public static boolean gifRunning = true;//gif是否运行
}
