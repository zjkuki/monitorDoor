package com.example.funsdkdemo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.download.XDownloadFileManager;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.litesuits.orm.LiteOrm;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;

import java.util.Date;


public class MyApplication extends Application {

	public static boolean noToatsShow = false;
	public static LiteOrm liteOrm;
	//public static LiteOrm liteOrmCascade;
	public static Context context;
	public static boolean networkConnected;
	public static String mqttClientId;

	@Override
	public void onCreate() {
		super.onCreate();
		QMUISwipeBackActivityManager.init(this);

		context = getApplicationContext();
		if (liteOrm == null) {
			liteOrm = LiteOrm.newSingleInstance(this, "liteorm.db");
		}
		liteOrm.setDebugged(false);
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


		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);

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
	
}
