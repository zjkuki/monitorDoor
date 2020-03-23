package com.example.funsdkdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;

import androidx.multidex.MultiDexApplication;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.download.XDownloadFileManager;
import com.janady.services.LocationService;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.litesuits.orm.LiteOrm;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.sangbo.autoupdate.CheckVersion;

import java.util.Date;


//public class MyApplication extends Application {
public class MyApplication extends MultiDexApplication {

	public static boolean noToatsShow = false;
	public static LiteOrm liteOrm;
	public static LiteOrm cascadeliteOrm;
	//public static LiteOrm liteOrmCascade;
	public static Context context;
	public static boolean networkConnected;
	public static String mqttClientId;
	public static LocationService locationService;
	public static Vibrator mVibrator;

	@Override
	public void onCreate() {
		super.onCreate();
		CheckVersion.checkUrl = "http://121.37.25.137/files/appVersion.txt";

		QMUISwipeBackActivityManager.init(this);

		context = getApplicationContext();
		if (liteOrm == null) {
			liteOrm = LiteOrm.newSingleInstance(this, "liteorm.db");
		}

		if(cascadeliteOrm == null) {
			cascadeliteOrm = LiteOrm.newCascadeInstance(this, "liteorm.db");
		}

		liteOrm.setDebugged(false);
		liteOrm.cascade().setDebugged(false);
		/**
		 * 以下是FunSDK初始化
		 */
		FunSupport.getInstance().init(this);
		
		/**
		 * 以下是网络图片下载等的本地缓存初始化,可以加速图片显示,和节省用户流量
		 * 跟FunSDK无关,只跟com.example.download内容相关
		 */
		String cachePath = FunPath.getCapturePath();
		XDownloadFileManager.setFileManager(
				cachePath, 				// 缓存目录
				20 * 1024 * 1024		// 20M的本地缓存空间
				);

		mqttClientId = FunSupport.getInstance().getUserName()+"@"+new Date().getTime();

		/***
		 * 初始化定位sdk，建议在Application中创建
		 */
		locationService = new LocationService(getApplicationContext());
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		SDKInitializer.initialize(getApplicationContext());
		SDKInitializer.setCoordType(CoordType.BD09LL);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);

	}

	//创建一个静态的方法，以便获取context对象
	public static Context getContext(){
		return context;
	}

	@Override
	public void onTerminate(){
		super.onTerminate();
		unregisterReceiver(receiver);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo != null)
			{
				networkConnected = networkInfo.isAvailable();
			}
			else
			{
				networkConnected = false;
			}
		}
	};

	public void exit() {

		FunSupport.getInstance().term();
	}

	//这是一个重新方法
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		androidx.multidex.MultiDex.install(this);

	}

}
