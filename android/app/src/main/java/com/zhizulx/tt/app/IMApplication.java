package com.zhizulx.tt.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;
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
		//CrashHandler handler = CrashHandler.getInstance();
		//handler.init(getApplicationContext()); //在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器
		startIMService();
		ImageLoaderUtil.initImageLoaderConfig(getApplicationContext());
		//开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
		Config.DEBUG = true;
		QueuedWork.isUseThreadPool = false;
		UMShareAPI.get(this);
	}

	private void startIMService() {
		logger.i("start IMService");
		Intent intent = new Intent();
		intent.setClass(this, IMService.class);
		startService(intent);
	}

    public static boolean gifRunning = true;//gif是否运行
	//各个平台的配置，建议放在全局Application或者程序入口
	{
		PlatformConfig.setWeixin("wxc2830ac92c59a8d7","a8833baad1122e313968aff77da54530");
		PlatformConfig.setQQZone("1106333242","RzuLJ0St1enZIXJq");
	}
}
