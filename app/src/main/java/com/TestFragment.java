package com;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
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

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends JBaseFragment implements ExpandAdapter.OnClickListener, OnFunDeviceListener  {
    QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
    private ExpandAdapter mItemAdapter;
    private List<MainItemDescription> mainItems;

    private List<SearchResult> mBleDevices = new ArrayList<SearchResult>();

    private Handler mHandler = new Handler();

    private boolean isScanning = false;

    private CountDownTimer countDownTimer = null;

    @Override
    protected View onCreateView() {
        startBluetooth();
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.jbase_recycle_layout, null);

        mTopBar = rootView.findViewById(R.id.topbar);
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
                        mHandler.postDelayed(searchDevices, 0);
                        //refreshDataSet();
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });


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

        initTopBar();
        initRecyclerView();
        return rootView;
    }
    private void initTopBar() {
        mTopBar.setTitle("我的所有设备");
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientManager.getClient().stopSearch();
                mHandler.removeCallbacksAndMessages(null);
                startFragment(new JTabSegmentFragment());
            }
        });

        //FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }
    private void initRecyclerView() {
        mainItems = DataManager.getInstance().getDescriptions();
        mItemAdapter = new ExpandAdapter(getContext(), mainItems);
        mItemAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(), 1));
        mRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
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
        });
    }

    /*public List<MainItemDescription> createData() {
        ArrayList<Camera> camlists = MyApplication.liteOrm.query(Camera.class);
        List<MainItemDescription> res = new ArrayList<>();
        if(camlists.size()>0) {
            for (int i = 0; i < camlists.size(); i++) {
                MainItemDescription items = new MainItemDescription(CameraListFragment.class, "camera-" + i, R.drawable.ic_camera, MainItemDescription.DeviceType.CAM);
                res.add(items);
            }
        }

        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        List<Object> items = new ArrayList<>();
        if(blists.size()>0) {
            MainItemDescription bleDescription = new MainItemDescription(BluetoothListFragment.class, "蓝牙设备", R.drawable.ic_bluetooth_black_24dp, MainItemDescription.DeviceType.BLE);
            for (int i = 0; i < 5; i++) {
                ItemDescription itemDescription = new ItemDescription(BluetoothEditFragment.class, "ble-" + i, R.drawable.ic_bluetooth_black_24dp);
                items.add(itemDescription);
            }
            bleDescription.setList(items);
            res.add(bleDescription);
        }

        for (int i = 0; i < 2; i++) {
            MainItemDescription remoteDescription = new MainItemDescription(RemoteListFragment_old.class, "remote-"+i, R.drawable.ic_remote_3, MainItemDescription.DeviceType.REMOTE);
            List<Object> remote_items = new ArrayList<>();
            for (int j =0; j < 3; j++) {
                ItemDescription itemDescription = new ItemDescription(RemoteEditFragment.class, "remote-"+i, R.drawable.ic_remote_3);
                remote_items.add(itemDescription);
            }
            remoteDescription.setList(remote_items);
            res.add(remoteDescription);
        }

        for (int i = 0; i < 2; i++) {
            MainItemDescription remoteDescription = new MainItemDescription(DoorListFragment.class, "door-"+i, R.drawable.ic_room2, MainItemDescription.DeviceType.REMOTE);
            List<Object> remote_items = new ArrayList<>();
            for (int j =0; j < 3; j++) {
                ItemDescription itemDescription = new ItemDescription(RemoteEditFragment.class, "door-"+i, R.drawable.ic_room2);
                remote_items.add(itemDescription);
            }
            remoteDescription.setList(remote_items);
            res.add(remoteDescription);
        }
        return res;
    }*/

    private void startBluetooth(){
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
     * @param itemDescription
     */
    @Override
    public void onItemClick(ItemDescription itemDescription) {
        ClientManager.getClient().stopSearch();
        mHandler.removeCallbacksAndMessages(null);
        if(!itemDescription.getEnable()){
            return;
        }

        Toast.makeText(getContext(), itemDescription.getName() + "-clicked", Toast.LENGTH_LONG).show();
        try {
            JBaseFragment fragment = itemDescription.getDemoClass().newInstance();

            if (fragment instanceof DeviceCameraFragment && itemDescription.getItem() instanceof Camera) {
                Camera citem = (Camera)itemDescription.getItem();
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

                ((DeviceCameraFragment)fragment).setFunDevice(mFunDevice);

                Intent intent = new Intent();
                intent.setClass(getContext(), DeviceCameraActivity.class);
                intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
                intent.putExtra("FUN_DEVICE_SCENE", citem.sceneName);
                intent.putExtra("FUN_DEVICE_SN", citem.sn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            //if (fragment instanceof BluetoothOperatorFragment && itemDescription.getItem() instanceof Bluetooth) {
            if (fragment instanceof BluetoothLockFragment && itemDescription.getItem() instanceof Bluetooth) {
                Bluetooth citem = (Bluetooth)itemDescription.getItem();
                BleLocker bleLocker = new BleLocker(citem,false,800, new BleLockerCallBack(this.getContext(), false));
                bleLocker.setmNoRssi(true);


                //((BluetoothOperatorFragment)fragment).setBleLocker(bleLocker);
                ((BluetoothLockFragment)fragment).title = citem.sceneName;
                ((BluetoothLockFragment)fragment).bleLocker = bleLocker;
                ((BluetoothLockFragment)fragment).isDebugViewOpen = true;

                startFragment(fragment);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMainClick(MainItemDescription mainItemDescription) {
        ClientManager.getClient().stopSearch();
        mHandler.removeCallbacksAndMessages(null);
        JBaseFragment fragment = null;
        Toast.makeText(getContext(), mainItemDescription.getName() + "-clicked", Toast.LENGTH_LONG).show();try {
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
        mHandler.postDelayed(searchDevices, 0);//每n秒执行一次runnable.
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
            mHandler.postDelayed(searchDevices, 0);
            refreshDataSet();
        }else{

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
            ClientManager.getClient().stopSearch();
            mHandler.removeCallbacksAndMessages(null);
        }
    }
    private void  refreshDataSet() {
        /*if(FunSupport.getInstance().getDeviceList().size()>0){
            hideWaitDialog();
            DataManager.getInstance().mFunDevices = FunSupport.getInstance().getDeviceList();
            //countDownTimer.cancel();
        }*/

        DataManager.getInstance().mFunDevices = FunSupport.getInstance().getDeviceList();
        DataManager.getInstance().mBleDevices = mBleDevices;
        mainItems = DataManager.getInstance().getDescriptions();
        mItemAdapter.setData(mainItems);
        mItemAdapter.notifyDataSetChanged();
    }


    private void searchBleDevice() {
            startBluetooth();

            isScanning = true;

            //if(mBleDevices.size()>0){mBleDevices.clear();}
            SearchRequest request = new SearchRequest.Builder()
                  .searchBluetoothLeDevice(2000, 1).build();

            ClientManager.getClient().search(request, mSearchResponse);

            /*ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
            List<Object> bitems = new ArrayList<>();
            for (Bluetooth bluetooth : blists) {
                matchBleLockerOnline(bluetooth);
            }*/
        Log.i("DataManager","停止扫描设备....");
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

                    Log.i("TestFragment","DeviceAddByUser.Bluetooth founds count: " + mBleDevices.size());
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

            refreshDataSet();
            //}
            mHandler.postDelayed(searchDevices, 3000);//每n秒执行一次runnable.
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


}
