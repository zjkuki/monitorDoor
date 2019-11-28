package com.janady.lkd;

import android.content.Context;
import android.util.Log;

import com.DateUtils;
import com.janady.Dialogs;
import com.janady.MqttUtil;
import com.janady.database.model.WifiRemoteLocker;
import com.janady.database.model.WifiRemoter;
import com.litesuits.orm.db.utils.DataUtil;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;

import io.fogcloud.fog_mqtt.helper.ListenDeviceCallBack;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class WifiRemoterBoard {
    private String TAG = "WifiRemoterBoard";
    private final int _EL_S = 1;
    private final int _EL_F = 2;


    @Getter @Setter public ListenDeviceCallBack setListenDeviceCallBack;

    @Getter @Setter private Context mcontext;

    @Getter @Setter private WifiRemoter mWifiRemoter;

    @Getter @Setter private String mPublicTopic;
    @Getter @Setter private String mSubscribeTopic;

    @Getter private MqttUtil mqttUtil = null;

    public WifiRemoterBoard(Context context){
        this.mcontext = context;

    }

    public WifiRemoterBoard(Context context, WifiRemoter wifiRemoter){
        this.mcontext = context;
        this.mWifiRemoter = wifiRemoter;
        initMqttService(context, wifiRemoter.hostUrl, wifiRemoter.hostPort, wifiRemoter.hostUsername, wifiRemoter.hostPassword
                , wifiRemoter.publictopic, wifiRemoter.subscribetopic, wifiRemoter.clientid,false);

    }

    public void initMqttService(Context context, String host, String port, String username, String password, String PubTopic, String SubTopic, String clientid, boolean isencrypt){
        mcontext = context;
        mPublicTopic = PubTopic;
        mSubscribeTopic = SubTopic;

        mqttUtil = new MqttUtil(context,host,username,password,clientid,0,mPublicTopic,mPublicTopic,iMqttActionListener,mqttCallback);
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

    public void sendCommand(String funCode, String buttonValue, int doorNo) throws Exception{

        JSONObject json = new JSONObject();
        json.put("device_type", mWifiRemoter.devType);
        json.put("device_id", mWifiRemoter.mac.replace(":",""));
        json.put("function_code", funCode);

        JSONObject data_json = new JSONObject();
        data_json.put("door_no", doorNo);
        if(funCode!="802") {
            data_json.put("button_value", buttonValue);
        }
        data_json.put("time", DateUtils.getNowTimeStamp());
        data_json.put("version", "1.0");
        json.put("data",data_json);

        mqttUtil.publish(json.toString());
        Log.d(TAG, "publish function operations:\n"+json.toString());
    }

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            mqttUtil.isConnectSuccess = true;
            try {
                mqttUtil.getMqttAndroidClient().subscribe(mSubscribeTopic, 0);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "onFailure 连接失败:" + arg1.getMessage());
            mqttUtil.isConnectSuccess = false;
            mqttUtil.getHandler().sendEmptyMessageDelayed(mqttUtil.getHAND_RECONNECT(), mqttUtil.getRECONNECT_TIME_CONFIG());
        }
    };

    //订阅主题的回调
    @Getter @Setter private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： " + new String(message.getPayload()) + "\tToString:" + message.toString());
            Dialogs.alertMessage(mcontext, "收到消息：",new String(message.getPayload()) + "\tToString:" + message.toString());
            //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
            //response("message arrived:"+message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

            Log.i(TAG, "deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开");
            Log.i(TAG, "onFailure 连接失败:" + arg0.getMessage());
            mqttUtil.isConnectSuccess = false;
            mqttUtil.getHandler().sendEmptyMessageDelayed(mqttUtil.getHAND_RECONNECT(), mqttUtil.getRECONNECT_TIME_CONFIG());
        }
    };


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
