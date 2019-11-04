package com.janady.lkd;

import android.content.Context;

import com.janady.database.model.WifiRemoteLocker;
import com.janady.database.model.WifiRemoter;

import java.util.ArrayList;

import io.fogcloud.sdk.mqtt.api.MQTT;
import io.fogcloud.sdk.mqtt.helper.ListenDeviceCallBack;
import io.fogcloud.sdk.mqtt.helper.ListenDeviceParams;

public class WifiRemoterBoard {
    private String TAG = "WifiRemoterBoard";
    private final int _EL_S = 1;
    private final int _EL_F = 2;


    public ListenDeviceCallBack setListenDeviceCallBack;

    private Context mcontext;
    private MQTT mqtt;


    public WifiRemoter getWifiRemoter() {
        return mWifiRemoter;
    }

    public void setWifiRemoter(WifiRemoter wifiRemoter) {
        this.mWifiRemoter = wifiRemoter;
    }

    private WifiRemoter mWifiRemoter;


    public String getPublicTopic() {
        return mPublicTopic;
    }

    public void setPublicTopic(String mPublicTopic) {
        this.mPublicTopic = mPublicTopic;
    }

    public String getSubscribeTopic() {
        return mSubscribeTopic;
    }

    public void setSubscribeTopic(String mSubscribeTopic) {
        this.mSubscribeTopic = mSubscribeTopic;
    }

    private String mPublicTopic;
    private String mSubscribeTopic;

    private ListenDeviceParams mqttDeviceParams;


    public WifiRemoterBoard(Context context){
        this.mcontext = context;

    }

    public WifiRemoterBoard(Context context, WifiRemoter wifiRemoter, ListenDeviceCallBack listenDeviceCallBack){
        this.mcontext = context;
        this.mWifiRemoter = wifiRemoter;
        WifiRemoterBoard(context, wifiRemoter.hostUrl, wifiRemoter.hostPort, wifiRemoter.hostUsername, wifiRemoter.hostPassword
                , wifiRemoter.publictopic, wifiRemoter.subscribetopic, wifiRemoter.clientid,false,listenDeviceCallBack);

    }

    public void WifiRemoterBoard(Context context, String host, String port, String username, String password, String PubTopic, String SubTopic, String clientid, boolean isencrypt, ListenDeviceCallBack listenDeviceCallBack){
        mcontext = context;
        mPublicTopic = PubTopic;
        mSubscribeTopic = SubTopic;

        mqttDeviceParams = new ListenDeviceParams();
        mqttDeviceParams.host = host;
        mqttDeviceParams.port = port;
        mqttDeviceParams.userName = username;
        mqttDeviceParams.passWord = password;
        mqttDeviceParams.topic = SubTopic;
        mqttDeviceParams.clientID = clientid;
        mqttDeviceParams.isencrypt = isencrypt;

        setListenDeviceCallBack = listenDeviceCallBack;

        mqtt = new MQTT(mcontext);

    }

    public void connect() {

        mqtt.startMqtt(mqttDeviceParams,new WiFiRemoterCallBack());
    }

    public void disconnect() {
        mqtt.stopMqtt(new WiFiRemoterCallBack());
    }

    public void setTag(String command){
        //String topic = "d64f517c/c8934691813c/in/write/0012";
        //String command = "{\"4\":true}";

        mqtt.publish(mPublicTopic, command, 0, false, new WiFiRemoterCallBack());
/*        mqtt.publish(topic, command, 0, false, new ListenDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                Log.d(TAG, code + " " + message);
                send2handler(_EL_S, message);
            }

            @Override
            public void onFailure(int code, String message) {
                Log.d(TAG, code + " " + message);
                send2handler(_EL_F, message);
            }
        });*/
    }

    public void subscribe(){
        //String addtopic = "d64f517c/c8934691813c/in/write";
        mqtt.subscribe(mSubscribeTopic, 0, new WiFiRemoterCallBack());
    }

    public void unSubscribe(){
        mqtt.unsubscribe(mSubscribeTopic,new WiFiRemoterCallBack());
    }

    public boolean addWifiRemoteLocker(WifiRemoteLocker wifiRemoteLocker){
        if(mWifiRemoter.Lockers == null){
            mWifiRemoter.Lockers = new ArrayList<WifiRemoteLocker>();
        }
        return mWifiRemoter.Lockers.add(wifiRemoteLocker);
    }

    public boolean removeWifiRemoteLocker(WifiRemoteLocker wifiRemoteLocker){
        if(mWifiRemoter.Lockers!=null && mWifiRemoter.Lockers.size()>0){
            return mWifiRemoter.Lockers.remove(wifiRemoteLocker);
        }else{
            return false;
        }
    }

    public int getWifiRemoteLockersCount() {
        return getWifiRemoter().Lockers.size();
    }



    public interface IWifiRemoterListener {
        void onPasswordChanged(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onClosed(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onStoped(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onLock(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onOpened(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onConnected(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onDisconnected(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onReday(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onPasswdError(WifiRemoter bluetooth, WifiRemoterStatus status);

        void onResetted(WifiRemoter bluetooth, WifiRemoterStatus status);
    }
}
