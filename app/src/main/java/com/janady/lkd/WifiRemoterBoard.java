package com.janady.lkd;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.funsdkdemo.MyApplication;
import com.janady.utils.DateUtils;
import com.janady.utils.MqttUtil;
import com.janady.database.model.WifiRemoteLocker;
import com.janady.database.model.WifiRemoter;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fogcloud.fog_mqtt.helper.ListenDeviceCallBack;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class WifiRemoterBoard {
    private String TAG = "WifiRemoterBoard";
    private final int _EL_S = 1;
    private final int _EL_F = 2;

    @Getter @Setter private IWifiRemoterListener iWifiRemoterListener;
    @Getter @Setter public ListenDeviceCallBack setListenDeviceCallBack;

    @Getter @Setter private Context mcontext;

    @Getter @Setter private String mCurrentMsg;
    @Getter @Setter public TextView mLdpMsg;
    @Getter @Setter private WifiRemoter mWifiRemoter;

    @Getter @Setter private String mPublicTopic;
    @Getter @Setter private String mSubscribeTopic;

    @Getter @Setter private boolean mIsAutoConnect = true;

    @Getter private MqttUtil mqttUtil = null;

    private String MQSysTopic = "$SYS/brokers/+/clients/+/+";
    private String DevConnectedTopic = "";
    private String DevDisconnectedTopic = "";

    private String newpwd = "";

    private String[] subscribeTopics = new String[3];
    private int[] subscribeTopicsQos = new int[3];

    public WifiRemoterBoard(Context context){
        this.mcontext = context;
    }

    public WifiRemoterBoard(Context context, WifiRemoter wifiRemoter, boolean isAutoConnect){
        this.mcontext = context;
        this.mWifiRemoter = wifiRemoter;
        this.mIsAutoConnect = isAutoConnect;

        initMqttService(context, wifiRemoter.hostUrl, wifiRemoter.hostPort, wifiRemoter.hostUsername, wifiRemoter.hostPassword
                , wifiRemoter.publictopic, wifiRemoter.subscribetopic, wifiRemoter.clientid,false);

    }

    public void initMqttService(Context context, WifiRemoter wifiRemoter, boolean isAutoConnect){
        this.mcontext = context;
        this.mWifiRemoter = wifiRemoter;
        this.mIsAutoConnect = isAutoConnect;

        initMqttService(context, wifiRemoter.hostUrl, wifiRemoter.hostPort, wifiRemoter.hostUsername, wifiRemoter.hostPassword
                , wifiRemoter.publictopic, wifiRemoter.subscribetopic, wifiRemoter.clientid,false);
    }

    public void initMqttService(Context context, String host, String port, String username, String password, String PubTopic, String SubTopic, String clientid, boolean isencrypt){
        mcontext = context;
        mPublicTopic = PubTopic;
        mSubscribeTopic = SubTopic;

        this.subscribeTopics[0] = "$SYS/brokers/+/clients/"+this.mWifiRemoter.devClientid+"/connected";
        this.subscribeTopics[1] = "$SYS/brokers/+/clients/"+this.mWifiRemoter.devClientid+"/disconnected";
        this.subscribeTopics[2] = mSubscribeTopic;

        this.subscribeTopicsQos = new int[]{0, 0, 0};

        if(this.mIsAutoConnect) {
            //if(context==null){return;}
            //mqttUtil = new MqttUtil(context, host, username, password, clientid, 0, mPublicTopic, mSubscribeTopic, iMqttActionListener, mqttCallback);
            mqttUtil = new MqttUtil(context, host, username, password, clientid, 0, mPublicTopic, mSubscribeTopic, iMqttActionListener, mqttCallback);
        }
    }

    public void doMqttConnection(){
        if(mqttUtil == null && mWifiRemoter!=null) {
            //if(mcontext==null){return;}
            mqttUtil = new MqttUtil(this.mcontext, mWifiRemoter.hostUrl, mWifiRemoter.hostUsername,
                    mWifiRemoter.hostPassword, mWifiRemoter.clientid, 0, mPublicTopic, mSubscribeTopic, iMqttActionListener, mqttCallback);
        }else{
            mqttUtil.doClientConnection();
        }
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

    public void changePassword(String password, String newPassword) throws Exception {
        newpwd = newPassword;
        sendCommand("808", password, newPassword,0);
    }
    public void sendCommand(String funCode, String password, String extraValue, int doorNo) throws Exception{

        JSONObject json = new JSONObject();
        json.put("device_type", mWifiRemoter.devType);
        json.put("device_id", mWifiRemoter.mac.replace(":",""));
        json.put("funtion_code", Integer.parseInt(funCode));

        JSONObject data_json = new JSONObject();

        data_json.put("password", password);
        data_json.put("time", DateUtils.getNowTimeStamp());

        if(funCode!="802" &&  funCode!="806" && funCode!="807" && funCode!="808") {
            data_json.put("door_no", doorNo);
            data_json.put("button_value", extraValue);
        }else{
            if(funCode == "802") {
                data_json.put("door_no", doorNo);
            }else{
                if(funCode == "808") {
                    data_json.put("new_password", extraValue);
                }
            }
        }

        //data_json.put("time", new Date().getTime());
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
            if(iWifiRemoterListener!=null){iWifiRemoterListener.onConnected(mWifiRemoter, WifiRemoterStatus.SERVER_CONNECGED);}
            mqttUtil.isConnectSuccess = true;
            int[] qoss = {0,0,0};
            try {
                //mqttUtil.getMqttAndroidClient().subscribe(mSubscribeTopic, 0);//订阅主题，参数：主题、服务质量
                mqttUtil.getMqttAndroidClient().subscribe(subscribeTopics, qoss);
            } catch (MqttException e) {
                e.printStackTrace();
                if(iWifiRemoterListener!=null){iWifiRemoterListener.onDisconnected(mWifiRemoter, WifiRemoterStatus.SERVER_DISCONNECTED);}
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "onFailure 连接失败:" + arg1.getMessage());
            if(iWifiRemoterListener!=null){iWifiRemoterListener.onDisconnected(mWifiRemoter, WifiRemoterStatus.SERVER_DISCONNECTED);}
            mqttUtil.isConnectSuccess = false;
            mqttUtil.getHandler().sendEmptyMessageDelayed(mqttUtil.getHAND_RECONNECT(), mqttUtil.getRECONNECT_TIME_CONFIG());
        }
    };

    //订阅主题的回调
    @Getter @Setter private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： \ntopic："+topic+"\n payload：" + new String(message.getPayload()) + "\tToString:" + message.toString());
            //Dialogs.alertMessage(mcontext, "收到消息：",new String(message.getPayload()) + "\tToString:" + message.toString());
            //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
            //response("message arrived:"+message);
            try {
                com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(message.toString());
                com.alibaba.fastjson.JSONObject data = new com.alibaba.fastjson.JSONObject();
                if(topic.contains("disconnected") || topic.contains("connected")) {
                    String clientid = json.getString("clientid");
                    if (clientid.equals(mWifiRemoter.devClientid)) {
                        if (topic.contains("disconnected")) {
                            mWifiRemoter.isOnline = false;
                            if(iWifiRemoterListener!=null){iWifiRemoterListener.onDeviceOffline(mWifiRemoter, WifiRemoterStatus.DEVICE_OFFLINE);}
                            Log.i("WifiRemoterBoard", "设备client id【" + clientid + "】离线");
                        } else {
                            mWifiRemoter.isOnline = true;
                            if(iWifiRemoterListener!=null){iWifiRemoterListener.onDeviceOnline(mWifiRemoter, WifiRemoterStatus.DEVICE_ONLINE);}
                            Log.i("WifiRemoterBoard", "设备client id【" + clientid + "】在线");
                        }
                    }

                    MyApplication.liteOrm.cascade().save(mWifiRemoter);

                    return;
                }

                data = json.getJSONObject("data");
                String msg="";
                switch(data.getIntValue("operation_result")){
                    case 200:
                        msg="开门成功";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onOpened(mWifiRemoter, WifiRemoterStatus.SET_OPEN_SUCCESS);}
                        break;
                    case 201:
                        msg="开门失败";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onOpened(mWifiRemoter, WifiRemoterStatus.SET_OPEN_FAILE);}
                        break;
                    case 202:
                        msg="关门成功";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onClosed(mWifiRemoter, WifiRemoterStatus.SET_CLOSE_SUCCESS);}
                        break;
                    case 203:
                        msg="关门失败";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onClosed(mWifiRemoter, WifiRemoterStatus.SET_CLOSE_FAILE);}
                        break;
                    case 204:
                        msg="锁成功";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onLock(mWifiRemoter, WifiRemoterStatus.SET_LOCK_SUCCESS);}
                        break;
                    case 205:
                        msg="锁失败";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onLock(mWifiRemoter, WifiRemoterStatus.SET_CLOSE_FAILE);}
                        break;
                    case 206:
                        msg="停成功";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onStoped(mWifiRemoter, WifiRemoterStatus.SET_STOP_SUCCESS);}
                        break;
                    case 207:
                        msg="停失败";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onStoped(mWifiRemoter, WifiRemoterStatus.SET_STOP_FAILE);}
                        break;
                    case 208:
                        msg="更改默认门号为:"+data.getString("door_no")+"成功";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onDoorNoChanged(mWifiRemoter, WifiRemoterStatus.SET_DOORNO_CHANGED_SUCCESS);}
                    case 209:
                        msg="更改默认门号失败";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onDoorNoChanged(mWifiRemoter, WifiRemoterStatus.SET_DOORNO_CHANGED_FAILE);}
                        break;
                    case 210:
                        msg="密码正确";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onPasswdVerify(mWifiRemoter, WifiRemoterStatus.SET_PASSWORD_CHECK_SUCCESS);}
                        break;
                    case 211:
                        msg="密码错误";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onPasswdVerify(mWifiRemoter, WifiRemoterStatus.SET_PASSWORD_CHECK_FAILE);}
                        break;
                    case 212:
                        msg="密码设置成功";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onPasswordChanged(mWifiRemoter, newpwd, WifiRemoterStatus.SET_PASSWORD_SET_SUCCESS);}
                        break;
                    case 213:
                        msg="密码设置失败";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onPasswordChanged(mWifiRemoter, "", WifiRemoterStatus.SET_PASSWORD_SET_FAILE);}
                        break;
                    case 214:
                        msg="获取默认门号成功，当前默认门号为："+ data.getString("door_no");
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onDefaultDoorNo(mWifiRemoter, data.getString("door_no"), WifiRemoterStatus.SET_PASSWORD_SET_SUCCESS);}
                        break;
                    case 215:
                        msg="获取默认门号失败";
                        if(iWifiRemoterListener!=null){iWifiRemoterListener.onDefaultDoorNo(mWifiRemoter, "", WifiRemoterStatus.SET_PASSWORD_SET_FAILE);}
                        break;
                }

                mCurrentMsg = msg;
                if(mLdpMsg!=null){
                    mLdpMsg.setText(msg);
                }
                Log.i(TAG,msg);

               //Dialogs.alertMessage(mcontext, "操作结果",msg);

            }catch(Exception e){
                e.printStackTrace();
                mCurrentMsg = "";
                if(mLdpMsg!=null){
                    mLdpMsg.setText("");
                }
            }
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
        void onPasswordChanged(WifiRemoter wifiRemoter, String newPasswd, WifiRemoterStatus status);

        void onClosed(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onStoped(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onLock(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onOpened(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onConnected(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onDisconnected(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onPasswdVerify(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onDoorNoChanged(WifiRemoter wifiRemoter, WifiRemoterStatus status);

        void onDefaultDoorNo(WifiRemoter wifiRemoter, String doorNo, WifiRemoterStatus status);

        void onDeviceOnline(WifiRemoter wifiRemoter, WifiRemoterStatus status);
        void onDeviceOffline(WifiRemoter wifiRemoter, WifiRemoterStatus status);

    }
}
