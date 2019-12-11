package com.janady.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.example.funsdkdemo.MyApplication;
import com.janady.HomeActivity;
import com.janady.adapter.WifiRemoterDeviceAdapter;
import com.janady.database.model.WifiRemoter;
import com.lkd.smartlocker.R;
import com.janady.adapter.BluetoothDeviceAdapter;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.GridDividerItemDecoration;
import com.janady.database.model.Bluetooth;
import com.janady.lkd.BleLocker;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUICenterGravityRefreshOffsetCalculator;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.List;

public class RemoteListFragment extends JBaseFragment {
    QMUITopBarLayout mTopBar;
    RecyclerView mRecyclerView;
    QMUIPullRefreshLayout mPullRefreshLayout;
    //    private BaseRecyclerAdapter<FunDevice> mItemAdapter;
//    private List<FunDevice> mFunDevices;
    private BaseRecyclerAdapter<WifiRemoter> mItemAdapter;
    private List<WifiRemoter> mWifiRemoters;
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
        mTopBar.setTitle("我的远程控制设备");
        mTopBar.addRightImageButton(R.drawable.ic_topbar_add, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startFragment(new JTabSegmentFragment());
                Intent intent = new Intent();
                intent.putExtra("DeviceTypsSpinnerNo", 2);
                intent.setClass(getContext(), DeviceAddByUser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                popBackStack();
            }
        });

        //FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }

    private void initRecyclerView() {
        mWifiRemoters= MyApplication.liteOrm.cascade().query(WifiRemoter.class);
        mItemAdapter = new WifiRemoterDeviceAdapter(getContext(), mWifiRemoters);
        mItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                Intent intent = new Intent();
                intent.setClass(getContext(), WifiRemoterBoardActivity.class);
                intent.putExtra("WIFI_DEVICE_MAC", mWifiRemoters.get(pos).mac);
                intent.putExtra("WIFI_DEVICE_SCENE", mWifiRemoters.get(pos).sceneName);
                intent.putExtra("WIFI_DEVICE_SN", mWifiRemoters.get(pos).devName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //popBackStack();
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
                if("wifiremoter_list_refresh".equals(msg)){
                    initRecyclerView();
                }
            }
        };

        broadcastManager.registerReceiver(mDeviceAddByUserReceiver, intentFilter);*/

    }
}
