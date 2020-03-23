package com.janady.device;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import com.example.funsdkdemo.MyApplication;
import com.janady.HomeActivity;
import com.lkd.smartlocker.R;
import com.janady.adapter.CameraDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.GridDividerItemDecoration;
import com.janady.database.model.Camera;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.models.FunDevStatus;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUICenterGravityRefreshOffsetCalculator;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.List;

public class CameraListFragment extends JBaseFragment {
    QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
//    private BaseRecyclerAdapter<FunDevice> mItemAdapter;
//    private List<FunDevice> mFunDevices;
    private BaseRecyclerAdapter<Camera> mItemAdapter;
    private List<Camera> mFunDevices;
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
                        //onDataLoaded();
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });

        initTopBar();
        initRecyclerView();
        return rootView;
    }
    private void initTopBar() {
        mTopBar.setTitle(R.string.MY_CAMERA);
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startFragment(new JTabSegmentFragment());
                //startFragment(new JTabSegmentFragment());
                popBackStack();
                Intent intent = new Intent();
                intent.putExtra("DeviceTypsSpinnerNo", 0);
                intent.setClass(getContext(), DeviceAddByUser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        popBackStack();
        //FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }
    private void initRecyclerView() {
        //mFunDevices = FunSupport.getInstance().getDeviceList();
        //mItemAdapter = new FunDeviceAdapter(getContext(), mFunDevices);
        mFunDevices = MyApplication.liteOrm.query(Camera.class);
        mItemAdapter = new CameraDeviceAdapter(getContext(), mFunDevices);
        mItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                //FunDevice funDevice = mFunDevices.get(pos);
                // 虚拟一个设备, 只需要序列号和设备类型即可添加
                FunDevice mFunDevice =new FunDevice();
                mFunDevice.devSn = mFunDevices.get(pos).sn;
                mFunDevice.devName = mFunDevices.get(pos).name;
                mFunDevice.devIp = mFunDevices.get(pos).devIp;
                mFunDevice.devMac = mFunDevices.get(pos).mac;
                mFunDevice.tcpPort = 34567;
                mFunDevice.devType = FunDevType.getType(mFunDevices.get(pos).type);
                mFunDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
                mFunDevice.isRemote = true;
                mFunDevice.loginName = mFunDevices.get(pos).loginName;
                mFunDevice.loginPsw = mFunDevices.get(pos).loginPsw;
                DeviceCameraFragment deviceCameraFragment = new DeviceCameraFragment();
                //deviceCameraFragment.setFunDevice(funDevice);
                deviceCameraFragment.setFunDevice(mFunDevice);

                Intent intent = new Intent();
                intent.setClass(getContext(), DeviceCameraActivity.class);
                intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
                intent.putExtra("FUN_DEVICE_SCENE", mFunDevices.get(pos).sceneName);
                intent.putExtra("FUN_DEVICE_TYPE", mFunDevices.get(pos).type);
                intent.putExtra("FUN_DEVICE_SN", mFunDevices.get(pos).sn);
                intent.putExtra("FUN_DEVICE_MAC", mFunDevices.get(pos).mac);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //startFragment(deviceCameraFragment);
            }
        });
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(), 1));
    }

    @Override
    public void onBackPressed() {
        popBackStack();
        Intent intent = new Intent();
        intent.putExtra("action","recreate");
        intent.setClass(getContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mDeviceAddByUserReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String msg = intent.getStringExtra("data");
                if("cam_list_refresh".equals(msg)){
                    initRecyclerView();
                }
            }
        };

        broadcastManager.registerReceiver(mDeviceAddByUserReceiver, intentFilter);*/

    }
}
