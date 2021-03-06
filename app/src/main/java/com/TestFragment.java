package com;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.common.UIFactory;
import com.example.funsdkdemo.MyApplication;
import com.google.zxing.activity.CaptureActivity;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.janady.Dialogs;
import com.janady.database.model.TestFragmentState;
import com.janady.services.BaiduMapUtils;
import com.janady.services.LocationService;
import com.janady.utils.HttpUtils;
import com.janady.utils.MqttUtil;
import com.janady.common.JQrcodePopDialog;
import com.janady.setup.FragmentUserLogin;
import com.janady.view.popmenu.DropPopMenu;
import com.lkd.smartlocker.R;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.janady.database.model.WifiRemoter;
import com.janady.device.WifiRemoterBoardActivity;
import com.janady.device.WifiRemoterBoardFragment;
import com.janady.lkd.BleLockerCallBack;
import com.janady.base.GridDividerItemDecoration;
import com.janady.base.JTabSegmentFragment;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.device.BluetoothLockFragment;
import com.janady.device.DeviceCameraActivity;
import com.janady.device.DeviceCameraFragment;
import com.janady.lkd.BleLocker;
import com.janady.lkd.ClientManager;
import com.janady.manager.DataManager;
import com.janady.model.ExpandAdapter;
import com.janady.model.ItemDescription;
import com.janady.model.MainItemDescription;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.models.FunDevStatus;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUICenterGravityRefreshOffsetCalculator;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.sangbo.autoupdate.CheckVersion;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TestFragment extends JBaseFragment implements ExpandAdapter.OnClickListener, OnFunDeviceListener {
    QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
    private ExpandAdapter mItemAdapter;
    private List<MainItemDescription> mainItems;

    private List<SearchResult> mBleDevices = new ArrayList<SearchResult>();
    private List<WifiRemoter> mWifiRemoters = new ArrayList<WifiRemoter>();

    private List<JSONObject> mMqttDevices = new ArrayList<JSONObject>();

    private Handler mHandler = new Handler();

    private boolean isScanning = false;

    private CountDownTimer countDownTimer = null;
    private BottomNavigationViewEx bnve;
    private FloatingActionButton fabScanQrCode;
    private Bitmap mQrCodeBmp = null;

    private Thread thSearchDevice;

    private LocationService locationService;

    private MqttUtil mqttUtil = null;
    private boolean mqttAutoConnect = true;


    private static Context mcontext = MyApplication.getContext();

    private String fromMsgClientId = "";
    private String toMsgClientId = "";

    private String mqttaction = "";
    private String shareDevicePublishTopic = "lkd_app_sharedata/message";
    private String shareDeviceResponseTopic = "lkd_app_sharedata/response";

    private String clientShareDevicePublishTopic = "";
    private String clientShareDeviceResponseTopic = "";
    private String[] subscribeTopics = null;
    private int[] subscribeTopicsQos = null;

    private String mqttclientid = MqttUtil.getCLIENTID();

    private String shareCamMac = "";
    private String shareBleMac = "";
    private String shareRemoterMac = "";

    private View currPopMenuView = null;

    private DropPopMenu dropPopMenu = null;

    private TestFragmentState testFragmentState = null;

    @Override
    protected View onCreateView() {
        startBluetooth();
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.jbase_recycle_layout, null);

        thSearchDevice = new Thread(searchDevices);

        mTopBar = rootView.findViewById(R.id.topbar);
        bnve = rootView.findViewById(R.id.bnve);
        fabScanQrCode = rootView.findViewById(R.id.fabScanQRCode);
        mRecyclerView = rootView.findViewById(R.id.listview);
        mPullRefreshLayout = rootView.findViewById(R.id.pull_to_refresh);
        mPullRefreshLayout.setRefreshOffsetCalculator(new QMUICenterGravityRefreshOffsetCalculator());
        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //mHandler.postDelayed(searchDevices, 0);
                        //refreshDataSet();
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });

        initEvent();

        // 监听设备列表类事件
        FunSupport.getInstance().registerOnFunDeviceListener(this);

        //showWaitDialog();
        //setMsgText("正在检查设备在线状态，请稍等...");
        FunSupport.getInstance().requestLanDeviceList();

        /*countDownTimer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("TestFragment", "seconds remaining: " + millisUntilFinished / 1000);
                setMsgText("正在检查设备在线状态，请稍等..." + String.valueOf(millisUntilFinished / 1000) + "秒");

            }

            public void onFinish() {
                //FunSupport.getInstance().stopWiFiQuickConfig();
                hideWaitDialog();
                refreshDataSet();
                Log.i("TestFragment", "done!");
            }
        }.start();*/

        mqttSetup();

        initTopBar();
        initRecyclerView();

        CheckVersion.setDialogTheme(R.style.MyAlertDialogStyle);
        //CheckVersion.update(getContext());
        CheckVersion.update(mcontext);

        return rootView;
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1
                && resultCode == RESULT_OK) {
            // Demo, 扫描二维码的结果
            if (null != data) {
                //Dialogs.alertMessage(getContext(), getString(R.string.SCAN_DALG_TITLE), data.toString());
                Dialogs.alertMessage(mcontext, getString(R.string.SCAN_DALG_TITLE), data.toString());
            }
        }
    }

    private void initTopBar() {
        mTopBar.setTitle(R.string.main_title);
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testFragmentState = new TestFragmentState();
                testFragmentState.searchResults = mBleDevices;
                MyApplication.liteOrm.save(testFragmentState);
                sleep(300);

                mqttAutoConnect = false;
                mqttUtil.closeMqtt();
                mqttUtil = null;
                ClientManager.getClient().stopSearch();
                mHandler.removeCallbacksAndMessages(null);
                startFragment(new JTabSegmentFragment());
            }
        });

        //FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }

    private void initRecyclerView() {
        List<TestFragmentState> tfs = MyApplication.liteOrm.query(new QueryBuilder<TestFragmentState>(TestFragmentState.class).whereEquals(TestFragmentState.COL_USERNAME, FunSupport.getInstance().getUserName()));
        if(tfs.size()>0){
            testFragmentState = tfs.get(0);
        }else{
            testFragmentState = null;
        }

        if(testFragmentState!=null){
            DataManager.getInstance().testFragmentState = testFragmentState;
            MyApplication.liteOrm.delete(testFragmentState);
            testFragmentState = null;
        }
        mainItems = DataManager.getInstance().getDescriptions();
        //mItemAdapter = new ExpandAdapter(getContext(), mainItems);
        mItemAdapter = new ExpandAdapter(mcontext, mainItems);
        mItemAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mItemAdapter);
        //mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setLayoutManager(new GridLayoutManager(mcontext, 1));
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(mcontext, 1));
       /* mRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("操作");
                menu.add(0, 0, 0, "删除");
            }
        });
        mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mRecyclerView.showContextMenu();
                return true;
            }
        });*/
    }

    private void initEvent() {
        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);

        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.menu_main:
                        try {
                            Dialogs.alertDialog2Btn(mcontext, getString(R.string.DLG_SHARE_TIPS_TITLE), getString(R.string.DLG_SHARING_TIPS), new DialogInterface.OnClickListener() {
                            //Dialogs.alertDialog2Btn(getContext(), getString(R.string.DLG_SHARE_TIPS_TITLE), getString(R.string.DLG_SHARING_TIPS), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    JSONObject json = new com.alibaba.fastjson.JSONObject();

                                    //String msgid=FunSupport.getInstance().getUserName()+"@"+ctime;
                                    fromMsgClientId = mqttclientid;

                                    json.put("from", fromMsgClientId);
                                    json.put("action", "sharealldevices");


                                    Log.d("--TF--", json.toJSONString());

                                    //String str="看甲方时点击翻身肯\n";  //内容大小控制在240byte， >240 进行压缩·否则不压··
                                    /*Log.d("TF","原文大小："+str.getBytes().length+" \n压缩前："+str);

                                    String compress = GZIP.compress(str);
                                    Log.d("TF","解压大小："+compress.getBytes().length+" \n压缩后："+compress);

                                    String uncompress = GZIP.unCompress(compress);
                                    Log.d("TF","解压大小："+uncompress.getBytes().length+" \n解压缩："+uncompress);*/

                                    //json.put("SDPT", shareDevicePublishTopic);
                                    //json.put("SDRT", shareDevicePublishTopic);
                                    String content = Base64.encodeToString(json.toJSONString().getBytes(), Base64.DEFAULT);
                                    //Bitmap bitmap = xxxxx;// 这里是获取图片Bitmap，也可以传入其他参数到Dialog中
                                    //JQrcodePopDialog.Builder dialogBuild = new JQrcodePopDialog.Builder(getContext());
                                    JQrcodePopDialog.Builder dialogBuild = new JQrcodePopDialog.Builder(mcontext);
                                    mQrCodeBmp = makeQRCode(content);
                                    dialogBuild.setImage(mQrCodeBmp);
                                    JQrcodePopDialog jqdialog = dialogBuild.create();
                                    jqdialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
                                    jqdialog.show();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.menu_me:
                        Dialogs.alertDialog2Btn(mcontext, getString(R.string.DLG_LOGOUT_TITLE), getString(R.string.DLG_LOGOUT), new DialogInterface.OnClickListener() {
                        //Dialogs.alertDialog2Btn(getContext(), getString(R.string.DLG_LOGOUT_TITLE), getString(R.string.DLG_LOGOUT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (FunSupport.getInstance().logout()) {
                                    startFragmentAndDestroyCurrent(new FragmentUserLogin());
                                }
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });

                        break;
                    case R.id.menu_empty: {

                        position = 1;
                        //此处return false且在FloatingActionButton没有自定义点击事件时 会屏蔽点击事件
                        //return false;
                    }
                    default:
                        break;
                }
                return true;
            }
        });

        fabScanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientManager.getClient().stopSearch();
                thSearchDevice.interrupt();
                mHandler.removeCallbacksAndMessages(null);

                Intent intent = new Intent();
                //intent.setClass(getContext(), CaptureActivity.class);
                intent.setClass(mcontext, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void getMqttDevices() {
        String result = "";
        String api_key = "admin";
        String api_secret = "public";
        //认证信息
        String[] baseauth = {api_key, api_secret};
        //请求的URL的参数
        HashMap<String, Object> get_data = new HashMap<>();
        //get_data.put("page","1");       //page参数
        //get_data.put("name","test");  //name参数
        HttpUtils.okhttp_get("http://mqtt.lkd.365yiding.com/api/v3/nodes/emqx@127.0.0.1/connections", get_data, baseauth, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                List<WifiRemoter> wr = MyApplication.liteOrm.cascade().query(WifiRemoter.class);
                if (wr != null && wr.size() > 0) {
                    for (WifiRemoter w : wr) {
                        w.isOnline = false;
                    }
                }

                final String resp = response.body().string();
                if (resp != "") {
                    try {
                        JSONArray data = null;
                        String code = "";
                        JSONObject json = JSON.parseObject(resp);
                        if (json != null) {
                            code = json.getString("code");
                            data = json.getJSONArray("data");
                            if (data != null && data.size() > 0) {
                                mMqttDevices = new ArrayList<JSONObject>();
                                for (int i = 0; i < data.size(); i++) {
                                    mMqttDevices.add((JSONObject) data.get(i));
                                    if (wr != null && wr.size() > 0) {
                                        for (int w = 0; w < wr.size(); w++) {
                                            if (wr.get(w).devClientid.equals(((JSONObject) data.get(i)).getString("client_id"))) {
                                                wr.get(w).isOnline = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        MyApplication.liteOrm.cascade().save(wr);
                        //sleep(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mWifiRemoters != null && mWifiRemoters.size() > 0) {
                        for (WifiRemoter w : mWifiRemoters) {
                            w.isOnline = false;
                        }
                    }
                    MyApplication.liteOrm.cascade().save(mWifiRemoters);
                    //sleep(300);
                }
            }
        });

        return;
    }

    private void mqttSetup() {

        mWifiRemoters = MyApplication.liteOrm.cascade().query(WifiRemoter.class);

        shareDevicePublishTopic = "lkd_app/" + mqttclientid + "/message";
        shareDeviceResponseTopic = "lkd_app/" + mqttclientid + "/response";

        //if(mWifiRemoters!=null) {
        subscribeTopics = new String[mWifiRemoters.size() * 2 + 1];
        subscribeTopicsQos = new int[mWifiRemoters.size() * 2 + 1];
            /*subscribeTopics = new String[3];
            subscribeTopicsQos = new int[3];
            subscribeTopics[0] = shareDevicePublishTopic;
            subscribeTopicsQos[0] = 0;
            subscribeTopics[1] = "$SYS/brokers/+/clients/+/connected";
            subscribeTopicsQos[1] = 0;
            subscribeTopics[2] = "$SYS/brokers/+/clients/+/disconnected";
            subscribeTopicsQos[2] = 0;*/

        subscribeTopics[0] = shareDevicePublishTopic;
        subscribeTopicsQos[0] = 0;

        int i = 1;
        for (WifiRemoter wr : mWifiRemoters) {
            subscribeTopics[i] = "$SYS/brokers/+/clients/" + wr.devClientid + "/connected";
            subscribeTopicsQos[i] = 0;
            i++;
            subscribeTopics[i] = "$SYS/brokers/+/clients/" + wr.devClientid + "/disconnected";
            subscribeTopicsQos[i] = 0;
            i++;
        }
        //}

        if (mqttUtil == null) {
            //mqttUtil = new MqttUtil(getContext(), mqttclientid, 0, shareDevicePublishTopic, shareDeviceResponseTopic, iMqttActionListener, mqttCallback);
            mqttUtil = new MqttUtil(mcontext, mqttclientid, 0, shareDevicePublishTopic, shareDeviceResponseTopic, iMqttActionListener, mqttCallback);
        } else {
            if (mqttUtil.isConnectionLost) {
                mqttUtil.doClientConnection();
            }

        }

        getMqttDevices();
    }


    private void startBluetooth() {
        if (!ClientManager.getClient().isBluetoothOpened()) { // 蓝牙未开启，则开启蓝牙
            ClientManager.getClient().openBluetooth();
        }
        /*if (!ClientManager.getClient().isBluetoothOpened()) { // 蓝牙未开启，则开启蓝牙
            Dialogs.alertDialog2Btn(this.getContext(), "提示", "您的蓝牙未开启，需要现在打开蓝牙吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!ClientManager.getClient().openBluetooth()){
                        return;
                    }else{
                        return;
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

        }*/
    }

    /**
     * ExpandAdapter里的onItemClick重写，针对DataManager传入的ItemDescription响应点击事件
     *
     * @param itemDescription
     */
    @Override
    public void onItemClick(View item, ItemDescription itemDescription) {
        if (!itemDescription.getEnable()) {
            return;
        }

        try {
            testFragmentState = new TestFragmentState();
            testFragmentState.searchResults = mBleDevices;
            MyApplication.liteOrm.save(testFragmentState);
            sleep(300);

            mqttAutoConnect = false;
            mqttUtil.closeMqtt();
            mqttUtil = null;
            ClientManager.getClient().stopSearch();
            thSearchDevice.interrupt();
            mHandler.removeCallbacksAndMessages(null);

            //Toast.makeText(getContext(), itemDescription.getName() + "-clicked", Toast.LENGTH_LONG).show();
            Log.i("TF", "打开：" + itemDescription.getName() + "-cliecked");

            JBaseFragment fragment = itemDescription.getDemoClass().newInstance();

            if (fragment instanceof DeviceCameraFragment && itemDescription.getItem() instanceof Camera) {
                Camera citem = (Camera) itemDescription.getItem();
                FunDevice mFunDevice = null;

                // 虚拟一个设备, 只需要序列号和设备类型即可添加
                mFunDevice = new FunDevice();
                mFunDevice.devSn = citem.sn;
                mFunDevice.devName = citem.name;
                mFunDevice.devIp = citem.devIp;
                mFunDevice.devMac = citem.mac;
                mFunDevice.tcpPort = 34567;
                mFunDevice.devType = FunDevType.getType(citem.type);
                mFunDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
                mFunDevice.isRemote = true;
                mFunDevice.loginName = citem.loginName;
                mFunDevice.loginPsw = citem.loginPsw;
                // 添加设备之前都必须先登录一下,以防设备密码错误,也是校验其合法性
                //FunSupport.getInstance().requestDeviceLogin(citem.camDevice);

                ((DeviceCameraFragment) fragment).setFunDevice(mFunDevice);

                Intent intent = new Intent();
                //intent.setClass(getContext(), DeviceCameraActivity.class);
                intent.setClass(mcontext, DeviceCameraActivity.class);
                intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
                intent.putExtra("FUN_DEVICE_SCENE", citem.sceneName);
                intent.putExtra("FUN_DEVICE_SN", citem.sn);
                intent.putExtra("FUN_DEVICE_MAC", citem.mac);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            //if (fragment instanceof BluetoothOperatorFragment && itemDescription.getItem() instanceof Bluetooth) {
            if (fragment instanceof BluetoothLockFragment && itemDescription.getItem() instanceof Bluetooth) {
                Bluetooth citem = (Bluetooth) itemDescription.getItem();
                //BleLocker bleLocker = new BleLocker(citem, false, 800, new BleLockerCallBack(this.getContext(), false));
                BleLocker bleLocker = new BleLocker(citem, false, 800, new BleLockerCallBack(mcontext, false));
                bleLocker.setmNoRssi(true);


                //((BluetoothOperatorFragment)fragment).setBleLocker(bleLocker);
                ((BluetoothLockFragment) fragment).title = citem.sceneName;
                ((BluetoothLockFragment) fragment).bleLocker = bleLocker;
                ((BluetoothLockFragment) fragment).isDebugViewOpen = false;

                startFragment(fragment);
            }

            if (fragment instanceof WifiRemoterBoardFragment && itemDescription.getItem() instanceof WifiRemoter) {
                WifiRemoter citem = (WifiRemoter) itemDescription.getItem();

                Intent intent = new Intent();
                //intent.setClass(getContext(), WifiRemoterBoardActivity.class);
                intent.setClass(mcontext, WifiRemoterBoardActivity.class);
                intent.putExtra("WIFI_DEVICE_MAC", citem.mac);
                intent.putExtra("WIFI_DEVICE_SCENE", citem.sceneName);
                intent.putExtra("WIFI_DEVICE_SN", citem.devName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemLongClick(View itemView, final ItemDescription itemDescription) {
        //dropPopMenu.show(itemView);
        //showToast("test onItemLongClick,current item:"+itemDescription.getName());
        if(dropPopMenu!=null){dropPopMenu.dismiss();}
        //dropPopMenu = new DropPopMenu(getContext());
        dropPopMenu = new DropPopMenu(mcontext);
        dropPopMenu.setTriangleIndicatorViewColor(Color.WHITE);
        dropPopMenu.setBackgroundResource(R.drawable.bg_drop_pop_menu_white_shap);
        dropPopMenu.setItemTextColor(Color.BLACK);

        dropPopMenu.setOnItemClickListener(new DropPopMenu.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id, com.janady.view.popmenu.MenuItem menuItem, int currentDeviceItemsId) {
                //Toast.makeText(getContext(), "点击了 " + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

                final Camera cam;
                final Bluetooth ble;
                final WifiRemoter wr;

                String sceneName = "";

                if (itemDescription.getItem() instanceof Camera) {
                    cam = (Camera)itemDescription.getItem();
                    sceneName = cam.sceneName;
                }else{
                    cam=null;
                }

                if (itemDescription.getItem() instanceof Bluetooth) {
                    ble = (Bluetooth) itemDescription.getItem();
                    sceneName = ble.sceneName;
                }else{
                    ble=null;
                }

                if (itemDescription.getItem() instanceof WifiRemoter) {
                    wr = (WifiRemoter)itemDescription.getItem();
                    if(wr!=null) {
                        sceneName = wr.sceneName;
                    }
                }else{
                    wr=null;
                }

                switch (menuItem.getItemId()) {
                    case Menu.FIRST + 0:
                        //Toast.makeText(getContext(), itemDescription.getName(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(mcontext, itemDescription.getName(), Toast.LENGTH_SHORT).show();
                        //final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(getContext()).builder()
                        final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(mcontext).builder()
                                .setTitle(getString(R.string.DLG_RENAME))
                                .setEditText(sceneName);
                        myAlertInputDialog.setPositiveButton(getString(R.string.common_confirm), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("TF", myAlertInputDialog.getResult());
                                String s = myAlertInputDialog.getResult();
                                if (itemDescription.getItem() instanceof Camera) {
                                    if(cam!=null) {
                                        cam.sceneName = s;
                                        MyApplication.liteOrm.save(cam);
                                        sleep(300);
                                    }
                                }

                                if (itemDescription.getItem() instanceof Bluetooth) {
                                    if(ble!=null) {
                                        ble.sceneName = s;
                                        MyApplication.liteOrm.save(ble);
                                        sleep(300);
                                    }
                                }

                                if (itemDescription.getItem() instanceof WifiRemoter) {
                                    if(wr!=null) {
                                        wr.sceneName = s;
                                        MyApplication.liteOrm.cascade().save(wr);
                                        sleep(300);
                                    }
                                }

                                myAlertInputDialog.dismiss();
                            }
                        }).setNegativeButton(getString(R.string.common_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("TF", "取消");
                                myAlertInputDialog.dismiss();
                            }
                        });

                        myAlertInputDialog.show();
                        break;
                    case Menu.FIRST + 1:
                        //AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//内部类
                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);//内部类
                        builder.setTitle(getString(R.string.DLG_REMINDER_TITLE));
                        builder.setMessage(getString(R.string.DLG_REMINDER_DELETE));
                        //确定按钮
                        builder.setPositiveButton(getString(R.string.common_confirm), new DialogInterface.OnClickListener() {

                            @SuppressLint("WrongConstant")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (itemDescription.getItem() instanceof Camera) {
                                    if(cam!=null) {
                                        MyApplication.liteOrm.delete(cam);
                                    }
                                }

                                if (itemDescription.getItem() instanceof Bluetooth) {
                                    if(ble!=null) {
                                        MyApplication.liteOrm.delete(ble);
                                    }
                                }

                                if (itemDescription.getItem() instanceof WifiRemoter) {
                                    if(wr!=null ) {
                                        if(wr.cameras!=null && wr.cameras.size()>0) {
                                            wr.cameras.clear();//如果不清楚，则会把所有关联对象一起删除！
                                        }
                                        MyApplication.liteOrm.cascade().delete(wr);
                                    }
                                }

                                //确定删除的代码
                               // Toast.makeText(getContext(), "删除成功", 0).show();
                            }
                        });
                        //点取消按钮
                        builder.setNegativeButton(getString(R.string.common_cancel), null);
                        builder.show();

                        break;
                    default:
                        break;
                }
            }

        });
        dropPopMenu.setMenuList(getMenuList());

        dropPopMenu.show(itemView,0);
    }

    private List<com.janady.view.popmenu.MenuItem> getMenuList() {
        List<com.janady.view.popmenu.MenuItem> list = new ArrayList<>();
        list.add(new com.janady.view.popmenu.MenuItem(1, getString(R.string.DLG_RENAME_TITLE)));
        list.add(new com.janady.view.popmenu.MenuItem(2, getString(R.string.device_opt_remove_by_user)));
        return list;
    }

    @Override
    public void onMainClick(View itemView, MainItemDescription mainItemDescription) {
        testFragmentState = new TestFragmentState();
        testFragmentState.searchResults = mBleDevices;
        MyApplication.liteOrm.save(testFragmentState);
        sleep(300);

        mqttAutoConnect=false;
        mqttUtil.closeMqtt();
        mqttUtil = null;
        ClientManager.getClient().stopSearch();
        thSearchDevice.interrupt();
        mHandler.removeCallbacksAndMessages(null);
        JBaseFragment fragment = null;
        //Toast.makeText(getContext(), mainItemDescription.getName() + "-clicked", Toast.LENGTH_LONG).show();try {
        Toast.makeText(mcontext, mainItemDescription.getName() + "-clicked", Toast.LENGTH_LONG).show();try {
            fragment = mainItemDescription.getDemoClass().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        /*if (fragment instanceof DeviceCameraFragment && mainItemDescription.getDevice() instanceof FunDevice) {
            ((DeviceCameraFragment)fragment).setFunDevice((FunDevice) mainItemDescription.getDevice());
        }*/

        if (fragment != null) startFragment(fragment);
    }

    //***********************************************************
    // item class
    //***********************************************************

    @Override
    public void onResume() {
        //mHandler.postDelayed(searchDevices, 0);//每n秒执行一次runnable.
        //new Thread(searchDevices).start();
        mqttSetup();

        thSearchDevice = new Thread(searchDevices);
        thSearchDevice.start();

        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshDataSet();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
            //mHandler.postDelayed(searchDevices, 0);
            //new Thread(searchDevices).start();
            refreshDataSet();
        }else{

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == 1
                && resultCode == RESULT_OK ) {
            // Demo, 扫描二维码的结果
            if ( null != data ) {
                try {
                    String result=data.getStringExtra("result");
                    String uncompress = new String(Base64.decode(result.getBytes(),Base64.DEFAULT));
                    //Dialogs.alertMessage(getContext(), "扫描结果", uncompress);

                    JSONObject json = JSONObject.parseObject(uncompress);
                    long ctime = new Date().getTime();
                    String msgid=FunSupport.getInstance().getUserName()+"@"+ctime;

                    fromMsgClientId = json.getString("from");
                    toMsgClientId = mqttclientid;
                    mqttaction = json.getString("action");
                    if(mqttaction.equals("shareRemoterDevice") || mqttaction.equals("shareCameraDevice") || mqttaction.equals("shareBluetoothDevice")){
                        shareRemoterMac = json.getString("mac");
                    }

                    shareDevicePublishTopic = "lkd_app/"+fromMsgClientId+"/message";
                    shareDeviceResponseTopic = "lkd_app/"+fromMsgClientId+"/response";
                    //shareDevicePublishTopic = json.getString("SDPT");
                    //shareDeviceResponseTopic = json.getString("SDRT");

                    //mqttUtil =  new MqttUtil(getContext(), mqttclientid, 0, shareDevicePublishTopic, shareDeviceResponseTopic,iMqttActionListener,mqttCallback);
                    mqttUtil =  new MqttUtil(mcontext, mqttclientid, 0, shareDevicePublishTopic, shareDeviceResponseTopic,iMqttActionListener,mqttCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

/*    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String msg = intent.getStringExtra("data");
                if("tf_refresh".equals(msg)){

                }
            }
        };

    }*/

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if(hidden) {
            testFragmentState = new TestFragmentState();
            testFragmentState.searchResults = mBleDevices;
            MyApplication.liteOrm.save(testFragmentState);
            sleep(300);

            ClientManager.getClient().stopSearch();
            mHandler.removeCallbacksAndMessages(null);
            thSearchDevice.interrupt();
        }
    }
    private synchronized void  refreshDataSet() {
        /*if(FunSupport.getInstance().getDeviceList().size()>0){
            hideWaitDialog();
            DataManager.getInstance().mFunDevices = FunSupport.getInstance().getDeviceList();
            //countDownTimer.cancel();
        }*/
        mHandler.post(new Runnable(){
            public void run(){
                if(dropPopMenu!=null){dropPopMenu.dismiss();}

                DataManager.getInstance().mFunDevices = FunSupport.getInstance().getDeviceList();
                DataManager.getInstance().mBleDevices = mBleDevices;
                mWifiRemoters = MyApplication.liteOrm.cascade().query(WifiRemoter.class);
                DataManager.getInstance().mWifiRemoters = mWifiRemoters;

                mainItems = DataManager.getInstance().getDescriptions();
                mItemAdapter.setData(mainItems);
                mItemAdapter.notifyDataSetChanged();
            }
        });

    }


    private void searchBleDevice() {
            startBluetooth();

            isScanning = true;

            //if(mBleDevices.size()>0){mBleDevices.clear();}
            SearchRequest request = new SearchRequest.Builder()
                  .searchBluetoothLeDevice(6000, 1).build();

            ClientManager.getClient().search(request, mSearchResponse);

            /*ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
            List<Object> bitems = new ArrayList<>();
            for (Bluetooth bluetooth : blists) {
                matchBleLockerOnline(bluetooth);
            }*/
        //Log.i("DataManager","停止扫描设备....");
    }

    /**
     * 查找蓝牙设备列表是否有这个设备
     *
     *
     */
    public void matchBleLockerOnline(Bluetooth bluetooth) {
        if(bluetooth==null){return;}
        final BleLocker bleLocker = new BleLocker(bluetooth,false,800, null);
        bleLocker.connect(new BleLocker.OnCheckOnlineCallBack() {
            @Override
            public void onSuccess(Bluetooth bluetooth) {
                bleLocker.disconnect();

                return;
            }

            @Override
            public void onFail(Bluetooth bluetooth) {
                return;
            }
        });
        return;
    }
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.i("DataManager","开始扫描设备");
        }


        @Override
        public void onDeviceFounded(SearchResult device) {
            if (device.getName().contains("VB")) {
                if (!mBleDevices.contains(device)) {
                    mBleDevices.add(device);
                }

                if (mBleDevices.size() > 0) {
                    /*List<Bluetooth> blists = MyApplication.liteOrm.query(new QueryBuilder<Bluetooth>(Bluetooth.class).whereEquals(Bluetooth.COL_MAC, device.getAddress()));
                    if(blists.size()>0){
                        refreshDataSet();
                    }*/

                    Log.i("TestFragment","Bluetooth founds count: " + mBleDevices.size());
                    //refreshDataSet();
                }
            } else {
                Log.i("TestFragment","非本产品蓝牙设备");
            }
        }

        @Override
        public void onSearchStopped() {
            Log.i("TestFragment","扫描停止");
            if (mBleDevices.size() > 0) {
                refreshDataSet();
                isScanning = false;
            }
        }

        @Override
        public void onSearchCanceled() {
            Log.i("TestFragment","扫描取消");
            if (mBleDevices.size() > 0) {
                refreshDataSet();
                isScanning = false;
            }
        }
    };




    Runnable searchDevices=new Runnable() {
        @Override
        public void run() {
            searchBleDevice();
            //if(FunSupport.getInstance().requestLanDeviceList()){
            //FunSupport.getInstance().requestDeviceList();
            List<Camera> cams = MyApplication.liteOrm.query(Camera.class);
            for(Camera cam : cams) {
                //FunSupport.getInstance().requestDeviceStatus(BuildFunDevice(cam));
                FunSupport.getInstance().requestDeviceStatus(FunDevType.getType(cam.type), cam.sn);
            }


            if(mqttUtil!=null){
                if (mqttUtil.isConnectionLost) {
                    mqttUtil.doClientConnection();
                }else{
                    Log.i("TF", "QMTT服务已断开连接，正等待重连");
                }

                //getMqttDevices();
            }

            refreshDataSet();
            //}
            mHandler.postDelayed(searchDevices, 10000);//每n秒执行一次runnable.
        }
    };

    private FunDevice BuildFunDevice(Camera camera){
        //FunDevice funDevice = mFunDevices.get(pos);
        // 虚拟一个设备, 只需要序列号和设备类型即可添加
        FunDevice mFunDevice =new FunDevice();
        mFunDevice.devSn = camera.sn;
        mFunDevice.devName = camera.name;
        mFunDevice.devIp = camera.devIp;
        mFunDevice.devMac = camera.mac;
        mFunDevice.tcpPort = 34567;
        mFunDevice.devType = null;
        mFunDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
        mFunDevice.isRemote = true;
        mFunDevice.loginName = camera.loginName;
        mFunDevice.loginPsw = camera.loginPsw;

        return mFunDevice;
    }

    @Override
    public void onDeviceListChanged() {
        refreshDataSet();
    }

    @Override
    public void onDeviceStatusChanged(FunDevice funDevice) {
        String sn= funDevice.devSn;
        if(sn==null) {
            sn = funDevice.devName;
        }
        List<Camera> cams = MyApplication.liteOrm.query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_SN, sn));
        if (cams != null && cams.size() > 0) {
            if (funDevice.devStatus == FunDevStatus.STATUS_ONLINE) {
                cams.get(0).isOnline = true;
            }else {
                cams.get(0).isOnline = false;
            }

            MyApplication.liteOrm.save(cams);
            //sleep(300);

            if(mWifiRemoters!=null && mWifiRemoters.size()>0){
                for(WifiRemoter w:mWifiRemoters){
                    if(w.cameras!=null && w.cameras.size()>0) {
                        for (Camera c : w.cameras) {
                            if (c.sn.equals(sn)) {
                                if (funDevice.devStatus == FunDevStatus.STATUS_ONLINE) {
                                    c.isOnline = true;
                                } else {
                                    c.isOnline = false;
                                }
                            }
                        }
                    }
                }

                MyApplication.liteOrm.cascade().save(mWifiRemoters);
                //sleep(300);
            }

            refreshDataSet();
        }
    }

    @Override
    public void onDeviceAddedSuccess() {

    }

    @Override
    public void onDeviceAddedFailed(Integer errCode) {

    }

    @Override
    public void onDeviceRemovedSuccess() {

    }

    @Override
    public void onDeviceRemovedFailed(Integer errCode) {

    }

    @Override
    public void onAPDeviceListChanged() {
        refreshDataSet();
    }

    @Override
    public void onLanDeviceListChanged() {
        refreshDataSet();
    }

    private Bitmap makeQRCode(String content){
        // 生成二维码
        Bitmap qrCodeBmp = UIFactory.createCode(
                content, 800, 0xff202020);
        if ( null != qrCodeBmp ) {
            if ( null !=  mQrCodeBmp ) {
                mQrCodeBmp.recycle();
            }
            mQrCodeBmp = qrCodeBmp;
            return qrCodeBmp;
        }else{
            return  null;
        }
    }

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i("TF", "收到消息： \ntopic："+topic+"\n payload：" + new String(message.getPayload()) + "\tToString:" + message.toString());
            //Dialogs.alertMessage(getContext(), "mqtt reciver", message.toString());
            String clientid="";
            try{
                JSONObject json = JSONObject.parseObject(message.toString());
                JSONObject data = new JSONObject();

                if(topic.contains("disconnected") || topic.contains("connected")){
                    clientid = json.getString("clientid");
                    List<WifiRemoter> wr = MyApplication.liteOrm.cascade().query(new QueryBuilder<WifiRemoter>(WifiRemoter.class).whereEquals(WifiRemoter.COL_DEVCLIENTID,
                            clientid));
                    if(wr.size()>0){
                        if(topic.contains("disconnected")) {
                            wr.get(0).isOnline = false;
                            Log.i("TF", "设备client id【" + clientid + "】离线");
                        }else{
                            wr.get(0).isOnline = true;
                            Log.i("TF", "设备client id【" + clientid + "】在线");
                        }
                    }

                    MyApplication.liteOrm.cascade().save(wr);
                    sleep(300);

                    refreshDataSet();
                    return;
                }



                fromMsgClientId = json.getString("from");
                toMsgClientId = json.getString("to");
                mqttaction = json.getString("action");
                if(fromMsgClientId.equals(mqttclientid) && mqttaction.equals("requestDatas")){
                    data = DataManager.getInstance().getAllDevices2FastJson();
                    json.put("action","responsedDevicesDatas");
                    json.put("data", data);
                    mqttUtil.publish(json.toJSONString());
                    mqttaction="";
                }else{
                    if(fromMsgClientId.equals(mqttclientid) && mqttaction.equals("requestRemoterData")){
                        String mac = json.getString("mac");
                        List<WifiRemoter> wifiRemoters = MyApplication.liteOrm.cascade().query(new QueryBuilder<WifiRemoter>(WifiRemoter.class).whereEquals(WifiRemoter.COL_MAC, mac ));
                        if(wifiRemoters.size()>0) {
                            data = JSON.parseObject(wifiRemoters.get(0).toJson());
                            json.put("action","responsedRemoterDeviceData");
                            json.put("data", data);
                            mqttUtil.publish(json.toJSONString());
                            mqttaction="";
                        }
                    }else {
                        if(fromMsgClientId.equals(mqttclientid) && mqttaction.equals("requestCameraData")){
                            String mac = json.getString("mac");
                            List<Camera> cameras = MyApplication.liteOrm.cascade().query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_MAC, mac ));
                            if(cameras.size()>0) {
                                data = JSON.parseObject(cameras.get(0).toJson());
                                json.put("action","responsedCameraDeviceData");
                                json.put("data", data);
                                mqttUtil.publish(json.toJSONString());
                                mqttaction="";
                            }
                        }else {
                            if(fromMsgClientId.equals(mqttclientid) && mqttaction.equals("requestBluetoothData")){
                                String mac = json.getString("mac");
                                List<Bluetooth> bles = MyApplication.liteOrm.cascade().query(new QueryBuilder<Bluetooth>(Bluetooth.class).whereEquals(Bluetooth.COL_MAC, mac ));
                                if(bles.size()>0) {
                                    data = JSON.parseObject(bles.get(0).toJson());
                                    json.put("action","responsedBluetoothDeviceData");
                                    json.put("data", data);
                                    mqttUtil.publish(json.toJSONString());
                                    mqttaction="";
                                }
                            }else {
                                if (toMsgClientId.equals(mqttclientid) && mqttaction.equals("responsedDevicesDatas")) {
                                    data = json.getJSONObject("data");
                                    //Dialogs.alertMessage(getContext(), "Shared devices data:\n", data.toJSONString());
                                    if (data.toJSONString().contains("cameras")) {
                                        JSONArray cameras = data.getJSONArray("cameras");
                                        List<Camera> cams = JSON.parseArray(cameras.toJSONString(), Camera.class);
                                        MyApplication.liteOrm.cascade().save(cams);
                                        sleep(300);
                                    }

                                    if (data.toJSONString().contains("bluetooths")) {
                                        JSONArray bluetooths = data.getJSONArray("bluetooths");
                                        List<Bluetooth> bles = JSON.parseArray(bluetooths.toJSONString(), Bluetooth.class);
                                        MyApplication.liteOrm.cascade().save(bles);
                                        sleep(300);
                                    }

                                    if (data.toJSONString().contains("wifiremoters")) {
                                        JSONArray wifiremoters = data.getJSONArray("wifiremoters");
                                        List<WifiRemoter> wifiRemoters = JSON.parseArray(wifiremoters.toJSONString(), WifiRemoter.class);
                                        MyApplication.liteOrm.cascade().save(wifiRemoters);
                                        sleep(300);
                                    }

                                    //refreshDataSet();

                                } else {
                                    if (toMsgClientId.equals(mqttclientid) && mqttaction.equals("responsedRemoterDeviceData")) {
                                        data = json.getJSONObject("data");
                                        List<WifiRemoter> wr =  JSON.parseArray("["+data.toJSONString()+"]", WifiRemoter.class);
                                        if(wr.size()>0) {
                                            MyApplication.liteOrm.cascade().save(wr.get(0));
                                            sleep(300);
                                        }else{
                                            return;
                                        }
                                    } else {
                                        if (toMsgClientId.equals(mqttclientid) && mqttaction.equals("responsedCameraDeviceData")) {
                                            data = json.getJSONObject("data");
                                            Camera cam = (Camera) JSON.parseObject(data.toJSONString(), Camera.class);
                                            MyApplication.liteOrm.cascade().save(cam);
                                            sleep(300);
                                        } else {
                                            if (toMsgClientId.equals(mqttclientid) && mqttaction.equals("responsedBluetoothDeviceData")) {
                                                data = json.getJSONObject("data");
                                                Bluetooth ble = (Bluetooth) JSON.parseObject(data.toJSONString(), Bluetooth.class);
                                                MyApplication.liteOrm.cascade().save(ble);
                                                sleep(300);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                mqttaction = "";
            }catch (Exception e) {
                e.printStackTrace();
            }


            //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
            //response("message arrived:"+message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i("TF", "deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            try {
                Log.i("TF", "连接断开");
                if(mqttAutoConnect) {
                    mqttUtil.disconnect();//连接断开，重连
                    if (mqttclientid == null && shareDevicePublishTopic == null && shareDeviceResponseTopic == null) {
                        return;
                    }
                    //mqttUtil = new MqttUtil(getContext(), mqttclientid, 0, shareDevicePublishTopic, shareDeviceResponseTopic, iMqttActionListener, mqttCallback);
                    mqttUtil = new MqttUtil(mcontext, mqttclientid, 0, shareDevicePublishTopic, shareDeviceResponseTopic, iMqttActionListener, mqttCallback);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i("TF-MQTT----", "连接成功 ");
            mqttUtil.isConnectSuccess = true;
            try {
                if(mqttaction==null && toMsgClientId == null && fromMsgClientId==null){return;}
                //mqttUtil.getMqttAndroidClient().subscribe(mqttUtil.getPUBLISH_TOPIC(), 0);//订阅主题，参数：主题、服务质量
                mqttUtil.getMqttAndroidClient().subscribe(subscribeTopics, subscribeTopicsQos);//订阅主题，参数：主题、服务质量
                JSONObject json = new JSONObject();
                if (toMsgClientId.equals(mqttclientid) && mqttaction.equals("sharealldevices")){
                    json.put("from", fromMsgClientId);
                    json.put("to", toMsgClientId);
                    json.put("action","requestDatas");
                }else{
                    if (toMsgClientId.equals(mqttclientid) && (mqttaction.equals("shareRemoterDevice")  || mqttaction.equals("shareCameraDevice") || mqttaction.equals("shareBluetoothDevice"))) {
                        json.put("from", fromMsgClientId);
                        json.put("to", toMsgClientId);
                        json.put("mac", shareRemoterMac);
                        if(mqttaction.equals("shareRemoterDevice")){
                            mqttaction = "requestRemoterData";
                        }else{
                            if(mqttaction.equals("shareCameraDevice")){
                                mqttaction = "requestCameraData";
                            }else{
                                if(mqttaction.equals("shareBluetoothDevice")){
                                    mqttaction = "requestBluetoothData";
                                }else{
                                    mqttaction = "";
                                    return;
                                }
                            }
                        }
                        json.put("action", mqttaction);
                    }
                }
                mqttUtil.publish(mqttUtil.getPUBLISH_TOPIC(), 0, json.toJSONString());
                mqttaction = "";
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i("TF-MQTT----", "onFailure 连接失败:" + arg1.getMessage());
            mqttUtil.isConnectSuccess = false;
            mqttUtil.getHandler().sendEmptyMessageDelayed(mqttUtil.getHAND_RECONNECT(), mqttUtil.getRECONNECT_TIME_CONFIG());
        }
    };
}
