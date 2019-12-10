package com.janady.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.lib.funsdk.support.FunSupport;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * CreateTime 2019/8/8 16:11
 * Author LiuShiHua
 * Description：
 */
@Data
public class MqttUtil {
    private final String TAG = "------------->mqtt";
    private static MqttUtil mqttUtil;
    @Getter @Setter private Context context;

    @Getter private MqttAndroidClient mqttAndroidClient;

    @Getter @Setter private MqttConnectOptions mqttConnectOptions;

    @Getter @Setter public boolean isConnectSuccess = false;
    @Getter @Setter public boolean isConnectionLost = true;

    //MQTT相关配置
    private final static long cid = System.currentTimeMillis();
    @Getter @Setter public static String CLIENTID = FunSupport.getInstance().getUserName()+"@"+cid;

    //@Getter @Setter public static String HOST = "tcp://mqtt.xuanma.tech:1883";//服务器地址（协议+地址+端口号）
    @Getter @Setter public static String HOST = "tcp://mqtt.lkd.365yiding.com:1883";//服务器地址（协议+地址+端口号）
    @Getter @Setter public static String USERNAME = "lkd";//用户名
    @Getter @Setter public static String PASSWORD = "lkd123!@#";//密码
    @Getter @Setter public static String PUBLISH_TOPIC = "lkd_smart_locker_app/message";//发布主题
    @Getter @Setter public static String RESPONSE_TOPIC = "lkd_smart_locker_app/message";//订阅主题
    @Getter @Setter public static String[] PUBLISH_TOPICS = {""};//发布主题
    @Getter @Setter public static String[] RESPONSE_TOPICS = {""};//订阅主题
    @Getter @Setter public static int[] QUALITY_OF_SERVICES = {0};//订阅主题

    /**
     * QUALITY_OF_SERVICE
     * 至多一次，消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。这一级别可用于如下情况，环境传感器数据，丢失一次读记录无所谓，因为不久后还会有第二次发送。
     * 至少一次，确保消息到达，但消息重复可能会发生。
     * 只有一次，确保消息到达一次。这一级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果
     */
    @Getter @Setter public static int QUALITY_OF_SERVICE = 0;//服务质量,0最多一次，1最少一次，2只一次


    @Getter private final int HAND_RECONNECT = 1;//重连hand
    @Getter private final int RECONNECT_TIME_CONFIG = 10 * 1000;//重连时间间隔为10秒
    @Getter private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAND_RECONNECT:
                    if (!isConnectSuccess)
                        doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
                    break;
            }
        }
    };


    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            isConnectSuccess = true;
            try {
                mqttAndroidClient.subscribe(PUBLISH_TOPIC, QUALITY_OF_SERVICE);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "onFailure 连接失败:" + arg1.getMessage());
            isConnectSuccess = false;
            handler.sendEmptyMessageDelayed(HAND_RECONNECT, RECONNECT_TIME_CONFIG);
        }
    };

    //订阅主题的回调
    @Getter @Setter private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： " + new String(message.getPayload()) + "\tToString:" + message.toString());
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
            doClientConnection();//连接断开，重连
        }
    };

    //单例模式
    public static MqttUtil getInstance(Context context) {
        if (mqttUtil == null) {
            mqttUtil = new MqttUtil(context);
        }
        return mqttUtil;
    }

    public MqttUtil(Context context, String host, String username, String password, String clientid, int Qos, String publishTopic, String reponseTopic, IMqttActionListener iMqttActionListener,MqttCallback mqttCallback){
        this.context = context;
        this.HOST = host;
        this.USERNAME = username;
        this.PASSWORD = password;
        this.CLIENTID = clientid;
        this.PUBLISH_TOPIC = publishTopic;
        this.RESPONSE_TOPIC = reponseTopic;
        this.QUALITY_OF_SERVICE = Qos;

        this.setIMqttActionListener(iMqttActionListener);
        this.setMqttCallback(mqttCallback);

        initMqtt();

    }


    public MqttUtil(Context context, String clientid, int Qos, String publishTopic, String reponseTopic, IMqttActionListener iMqttActionListener, MqttCallback mqttCallback){
        this.context = context;
        this.CLIENTID = clientid;
        this.PUBLISH_TOPIC = publishTopic;
        this.RESPONSE_TOPIC = reponseTopic;
        this.QUALITY_OF_SERVICE = Qos;

        this.setIMqttActionListener(iMqttActionListener);
        this.setMqttCallback(mqttCallback);

        initMqtt();

    }

    public MqttUtil(Context context, int[] Qos, String[] publishTopic, String[] reponseTopic, IMqttActionListener iMqttActionListener, MqttCallback mqttCallback){
        this.context = context;
        this.PUBLISH_TOPICS = publishTopic;
        this.RESPONSE_TOPICS = reponseTopic;
        this.QUALITY_OF_SERVICES = Qos;

        this.setIMqttActionListener(iMqttActionListener);
        this.setMqttCallback(mqttCallback);

        initMqtt();

    }

    public MqttUtil(Context context, IMqttActionListener iMqttActionListener,MqttCallback mqttCallback){
        this.context = context;
        this.setIMqttActionListener(iMqttActionListener);
        this.setMqttCallback(mqttCallback);

        initMqtt();

    }

    protected MqttUtil(Context context) {
        this.context = this.context;
        initMqtt();
    }

    private void initMqtt() {
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        mqttAndroidClient = new MqttAndroidClient(context, serverURI, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置订阅消息的回调
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mqttConnectOptions.setKeepAliveInterval(20); //设置心跳包发送间隔，单位：秒
        mqttConnectOptions.setUserName(USERNAME); //设置用户名
        mqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
        Integer qos = QUALITY_OF_SERVICE;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            try {
                mqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "setWill Exception Occured:" + e.getMessage(), e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            BluetoothLog.d("mMqttConnectOptions.setWill Success");
            doClientConnection();
        }
    }


    /**
     * 连接MQTT服务器
     */
    public void doClientConnection() {
        BluetoothLog.d("是否链接成功：" + mqttAndroidClient.isConnected());
        //if (!mqttAndroidClient.isConnected() && Tools.isInternetConnect(context)) {
        if (!mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                BluetoothLog.d("doClientConnection:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 发布消息(自定义主题)
     *
     * @param message 消息
     */
    public void publish(String topic, Integer qos, String message) {
        //String topic = PUBLISH_TOPIC;
        //Integer qos = QUALITY_OF_SERVICE;
        Boolean retained = false;
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
                mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } else {
                BluetoothLog.d("mqttAndroidClient is Null");
            }
        } catch (MqttException e) {
            BluetoothLog.d("publish MqttException:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发布消息
     *
     * @param message 消息
     */
    public void publish(String message) {
        String topic = PUBLISH_TOPIC;
        Integer qos = QUALITY_OF_SERVICE;
        Boolean retained = false;
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
                mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } else {
                BluetoothLog.d("mqttAndroidClient is Null");
            }
        } catch (MqttException e) {
            BluetoothLog.d("publish MqttException:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void response(String message) {
        String topic = RESPONSE_TOPIC;
        Integer qos = QUALITY_OF_SERVICE;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            BluetoothLog.d("publish:" + e.getMessage());
            e.printStackTrace();
        }
    }

    //断开链接
    public void disconnect() {
        try {
            if (mqttAndroidClient != null)
                mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void pullDevicesListFromMQTTServer(){
                Map<String,String> map = new HashMap<>();
                map.put("appkey", FunSupport.APP_KEY);
                map.put("uname", "123123");
                map.put("password", "123123");

                OkHttpClient client=new OkHttpClient();

                FormBody.Builder Body = new FormBody.Builder();
                for(Map.Entry<String,String> entry:map.entrySet()){
                    Body.add(entry.getKey(),entry.getValue());
                }

                RequestBody requestBody = Body.build();

                Request request = new Request.Builder()
                        .url("http://mqtt.lkd.365yiding.com/api/v3nodes/emqx@127.0.0.1/connections/")//请求的url
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        Log.v("Main.ID=", res);
                    }

                });
    }

}
