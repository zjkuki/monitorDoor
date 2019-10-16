package com.janady.device;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.adapter.CameraDeviceAdapter;
import com.janady.adapter.FunDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.GridDividerItemDecoration;
import com.janady.base.JTabSegmentFragment;
import com.janady.database.model.Camera;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevStatus;
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
        mTopBar.setTitle("我的摄像机");
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(new JTabSegmentFragment());
            }
        });

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
                mFunDevice.devType = mFunDevices.get(pos).type;
                mFunDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
                mFunDevice.isRemote = true;
                mFunDevice.loginName = mFunDevices.get(pos).loginName;
                mFunDevice.loginPsw = mFunDevices.get(pos).loginPsw;
                DeviceCameraFragment deviceCameraFragment = new DeviceCameraFragment();
                //deviceCameraFragment.setFunDevice(funDevice);
                deviceCameraFragment.setFunDevice(mFunDevice);
                startFragment(deviceCameraFragment);
            }
        });
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(), 1));
    }

}
