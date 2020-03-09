package com.omdd.dcapp.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.omdd.dcapp.API.MessageHandleCode
import com.omdd.dcapp.activity.BaseActivity
import com.omdd.dcapp.utils.PreferencesHelper
import com.omdd.dcapp.utils.extensionFunctions.component
import timber.log.Timber
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LocationService : Service() {

    @Inject lateinit var prefs:PreferencesHelper
    @Inject lateinit var context: Context

    private val binder = LocalBinder()
    var wakeLock:PowerManager.WakeLock ?= null

    lateinit var ls:LocationManager

    var provider = ""

    inner class LocalBinder : Binder() {
        // 声明一个方法，getService。（提供给客户端调用）
        internal// 返回当前对象LocalService,这样我们就可在客户端端调用Service的公共方法了
        val service: LocationService
            get() = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        acquireWakeLock(this)
        return binder
        //return object:ProcessConnection.Stub(){}
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()

        component().inject(this)



        try {
            var c: Criteria =  Criteria()
            c.accuracy = Criteria.ACCURACY_FINE//精度设置
            //c.powerRequirement=Criteria.POWER_MEDIUM//设置低耗电
            //c.accuracy = Criteria.ACCURACY_COARSE//精度设置
            c.powerRequirement=Criteria.POWER_HIGH

            c.isCostAllowed=false //只用免费的
            c.isBearingRequired=true//提供方向信息
            c.isAltitudeRequired=true//提供海拔信息
            c.bearingAccuracy=Criteria.ACCURACY_FINE//设置COARSE精度标准


            ls = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            provider = ls!!.getBestProvider(c, true)

            if (provider == null) {
                Timber.d("provider equals null")
                return
            }

            val listener = MyListener()
            ls.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, listener)

            lockScreenTest()

            acquireWakeLock(this)

            //saveLocation(location)



        }catch(e:Exception){
            Timber.d("GPS location error")
            return
        }



        acquireWakeLock(this)

        Timber.d("locationService")
    }

    override fun onDestroy() {
        releaseWakeLock()
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        //releaseWakeLock()
        return super.onUnbind(intent)
    }

    @SuppressLint("MissingPermission")
    fun saveLocation() {
        try {
            val location = ls.getLastKnownLocation(this.provider)
            if (location == null) {
                //prefs.gpsLongitude = 0.0
                //prefs.gpsLatitude = 0.0
                Timber.d("GPS位置获取失败！Longitude经度:0.0    Latitude纬度:0.0")
                Timber.d("location equals null")
                return
            }

            val longitude = location.getLongitude()
            val altitude = location.getLatitude()
            val accuracy = location.getAccuracy()

            val pos = ("Longitude经度:" + longitude + "    Latitude纬度:" + altitude
                    + "    Accuracy海拔:" + accuracy + "\n")
            //prefs.gpsLongitude = longitude
            //prefs.gpsLatitude = altitude

            Timber.d("GPS位置定位信息："+pos)
        }catch (e:Exception){
            //prefs.gpsLongitude = 0.0
            //prefs.gpsLatitude = 0.0
            Timber.d("GPS位置获取失败！Longitude经度:0.0    Latitude纬度:0.0")
            e.printStackTrace()
        }
    }
    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     */
    fun acquireWakeLock(context:Context) {
        if (null == wakeLock) {
            var pm =  getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, context.javaClass
                    .getCanonicalName())
            if (null != wakeLock) {
                Log.i(BaseActivity.TAG, "call acquireWakeLock")
                wakeLock!!.acquire()
            }
        }
    }

    // 释放设备电源锁
    fun releaseWakeLock() {
        if (null != wakeLock && wakeLock!!.isHeld()) {
            Log.i(BaseActivity.TAG, "call releaseWakeLock")
            wakeLock!!.release()
            wakeLock = null
        }
    }

    var i:Int=0

    val updateHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                MessageHandleCode.GET_GPS_LOCATION->{
                    i++
                    Timber.v("锁屏更新测试！！"+i)

                    saveLocation()
                }
            }
        }
    }
    open fun lockScreenTest(){
        val scheduled: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(2)
        scheduled.scheduleAtFixedRate(Runnable {
            updateHandler.sendEmptyMessage(MessageHandleCode.GET_GPS_LOCATION)
        }, 0, 6000, TimeUnit.MILLISECONDS)

    }
    private inner class MyListener : LocationListener {

        override fun onLocationChanged(location: Location) {
            // TODO Auto-generated method stub
            saveLocation()

            Timber.d("onLocationChanged")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // TODO Auto-generated method stub

        }

        override fun onProviderEnabled(provider: String) {
            // TODO Auto-generated method stub

        }

        override fun onProviderDisabled(provider: String) {
            // TODO Auto-generated method stub

        }


    }

    companion object {

        private val TAG = "LocationService"
    }

}