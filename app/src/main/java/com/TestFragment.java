package com;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.janady.Dialogs;
import com.janady.RoundRect;
import com.janady.adapter.FunDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.GridDividerItemDecoration;
import com.janady.base.JTabSegmentFragment;
import com.janady.base.RecyclerViewHolder;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.BluetoothListFragment;
import com.janady.device.CameraListFragment;
import com.janady.device.DeviceCameraFragment;
import com.janady.device.DoorEditFragment;
import com.janady.device.DoorListFragment;
import com.janady.device.RemoteEditFragment;
import com.janady.device.RemoteListFragment;
import com.janady.lkd.ClientManager;
import com.janady.manager.DataManager;
import com.janady.model.CategoryItemDescription;
import com.janady.model.ExpandAdapter;
import com.janady.model.ItemDescription;
import com.janady.model.MainItemDescription;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevStatus;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUICenterGravityRefreshOffsetCalculator;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.lib.funsdk.support.models.FunDevType.EE_DEV_BOUTIQUEROTOT;
import static com.lib.funsdk.support.models.FunDevType.EE_DEV_UNKNOWN;

public class TestFragment extends JBaseFragment implements ExpandAdapter.OnClickListener {
    QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
    private ExpandAdapter mItemAdapter;
    private List<MainItemDescription> mainItems;

    private List<SearchResult> mBleDevices = new ArrayList<SearchResult>();

    private Handler mHandler = new Handler();

    @Override
    protected View onCreateView() {
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
                        refreshDataSet();
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });

        mHandler.postDelayed(searchDevices, 0);//每n秒执行一次runnable.

        initTopBar();
        initRecyclerView();
        return rootView;
    }
    private void initTopBar() {
        mTopBar.setTitle("我的所有设备");
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            MainItemDescription remoteDescription = new MainItemDescription(RemoteListFragment.class, "remote-"+i, R.drawable.ic_remote_3, MainItemDescription.DeviceType.REMOTE);
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

        }
    }

    @Override
    public void onItemClick(ItemDescription itemDescription) {

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
                mFunDevice.devType = citem.type;
                mFunDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
                mFunDevice.isRemote = true;
                mFunDevice.loginName = citem.loginName;
                mFunDevice.loginPsw = citem.loginPsw;
                // 添加设备之前都必须先登录一下,以防设备密码错误,也是校验其合法性
                //FunSupport.getInstance().requestDeviceLogin(citem.camDevice);

                ((DeviceCameraFragment)fragment).setFunDevice(mFunDevice);
            }

            startFragment(fragment);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMainClick(MainItemDescription mainItemDescription) {
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
    public void onStart() {
        super.onStart();
        refreshDataSet();
    }

    private void  refreshDataSet() {
        startBluetooth();
        DataManager.getInstance().mBleDevices = mBleDevices;
        DataManager.getInstance().mFunDevices = FunSupport.getInstance().getLanDeviceList();
        mainItems = DataManager.getInstance().getDescriptions();
        mItemAdapter.setData(mainItems);
        mItemAdapter.notifyDataSetChanged();
    }


    private void searchBleDevice() {
            if(mBleDevices.size()>0){mBleDevices.clear();}
            SearchRequest request = new SearchRequest.Builder()
                    .searchBluetoothLeDevice(5000, 2).build();

            ClientManager.getClient().search(request, mSearchResponse);
        Log.i("DataManager","停止扫描设备....");
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
                    Log.i("DataManager","DeviceAddByUser.Bluetooth founds count: " + mBleDevices.size());
                    refreshDataSet();
                }
            } else {
                Log.i("DataManager","非本产品蓝牙设备");
            }
        }

        @Override
        public void onSearchStopped() {
            Log.i("DataManager","扫描停止");
            if (mBleDevices.size() > 0) {
                refreshDataSet();
            }
        }

        @Override
        public void onSearchCanceled() {
            Log.i("DataManager","扫描取消");
            if (mBleDevices.size() > 0) {
                refreshDataSet();
            }
        }
    };

    Runnable searchDevices=new Runnable() {
        @Override
        public void run() {
            searchBleDevice();
            if(FunSupport.getInstance().requestLanDeviceList()){
                refreshDataSet();
            }
            mHandler.postDelayed(searchDevices, 30000);//每n秒执行一次runnable.
        }
    };
}
