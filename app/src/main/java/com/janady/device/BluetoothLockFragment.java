package com.janady.device;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.janady.BleLockerCallBack;
import com.janady.Dialogs;
import com.janady.Util;
import com.janady.base.JTabSegmentFragment;
import com.janady.database.model.Bluetooth;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;
import com.janady.lkd.ClientManager;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BluetoothLockFragment extends JBaseFragment implements View.OnClickListener {
    private static final String MAC = "D0:D3:86:72:4D:42";
    private static final String BleService = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String BleNotifitesCharacter = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String BleWriteCharacter = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public String title = "我的蓝牙设备";
    public BleLocker bleLocker;
    public boolean isDebugViewOpen = false;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton searchBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton passwordBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton connectBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton disconnectBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton openBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton closeBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton lockBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton stopBtn;
    private ImageButton mBtnBack;
    private TextView mTextTitle;
    private TextView tvresult;

    private boolean isLocked = false;

    QMUITopBarLayout mTopBar;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jbluetooth_lock_layout, null);
        //mTopBar = root.findViewById(R.id.topbar);
        //initTopBar();
//        bleLocker = new BleLocker(MAC, false, BleService,
//                BleNotifitesCharacter, BleWriteCharacter, "123123",800, iBleLockerCallBack);

        bleLocker.setBleLockerCallBack(iBleLockerListener);

        openBtn = root.findViewById(R.id.open);
        openBtn.setOnClickListener(this);

        closeBtn = root.findViewById(R.id.close);
        closeBtn.setOnClickListener(this);

        lockBtn = root.findViewById(R.id.lock);
        lockBtn.setOnClickListener(this);

        stopBtn = root.findViewById(R.id.unlock);
        stopBtn.setOnClickListener(this);

        tvresult = root.findViewById(R.id.result);
        if(isDebugViewOpen){
            tvresult.setVisibility(View.VISIBLE);
        }else{
            tvresult.setVisibility(View.GONE);
        }

        connectBtn=root.findViewById(R.id.connect);
        connectBtn.setOnClickListener(this);
        connectBtn.setVisibility(View.GONE);

        disconnectBtn = root.findViewById(R.id.disconnect);
        disconnectBtn.setOnClickListener(this);
        disconnectBtn.setVisibility(View.GONE);

        searchBtn = root.findViewById(R.id.scan);
        searchBtn.setOnClickListener(this);
        searchBtn.setVisibility(View.GONE);

        passwordBtn = root.findViewById(R.id.password);
        passwordBtn.setOnClickListener(this);
        passwordBtn.setVisibility(View.GONE);

        mBtnBack = root.findViewById(R.id.backBtnInTopLayout);
        mBtnBack.setOnClickListener(this);

        mTextTitle = root.findViewById(R.id.textViewInTopLayout);
        mTextTitle.setText(title);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(bleLocker != null) {bleLocker.connect();}
    }

    @Override
    public void onStop() {
        if(bleLocker != null) {bleLocker.disconnect();}
        super.onStop();
    }

    private void initTopBar() {
        mTopBar.setTitle(title);
        /*mTopBar.addRightImageButton(R.drawable.scan_capture, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startFragment(new JTabSegmentFragment());
            }
        });*/
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bleLocker!=null){
                    bleLocker.disconnect();
                }
                popBackStack();
            }
        });
    }


    private boolean isScanning = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtnInTopLayout:
            {
                // 返回/退出
                getBaseFragmentActivity().popBackStack();
            }
            break;
            case R.id.open:
                if(isLocked){
                    Dialogs.alertMessage(this.getContext(),"提示", "操作失败，请先开锁！");
                }
                bleLocker.open();
                break;
            case R.id.close:
                bleLocker.close();
                break;
            case R.id.lock:
                bleLocker.lock();
                break;
            case R.id.unlock:
                bleLocker.stop();
                break;
            case R.id.scan:
                if(!isScanning) {
                    searchDevice();
                }else{
                    ClientManager.getClient().stopSearch();
                }
                break;
            case R.id.password:
                bleLocker.changePassword("123456");
                break;
            case R.id.connect:
                bleLocker.connect();
                break;
            case R.id.disconnect:
                bleLocker.disconnect();
                break;
        }
    }

    /**
     * -----------------
     * 搜索蓝牙设备
     */
    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 2).build();

        ClientManager.getClient().search(request, mSearchResponse);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {

            searchBtn.setText("停止扫描");
            isScanning = true;

       }

        @Override
        public void onDeviceFounded(SearchResult device) {
//            if (!mDevices.contains(device)) {
//                mDevices.add(device);
//                mAdapter.setDataList(mDevices);
//
//            if (mDevices.size() > 0) {
//                mRefreshLayout.showState(AppConstants.LIST);
//            }
        }

        @Override
        public void onSearchStopped() {

            isScanning = true;
            searchBtn.setText("扫描设备");
            //toolbar.setTitle(R.string.devices);
        }

        @Override
        public void onSearchCanceled() {

            searchBtn.setText("扫描设备");
            //toolbar.setTitle(R.string.devices);
        }
    };

    private BleLocker.IBleLockerListener iBleLockerListener = new BleLocker.IBleLockerListener() {
        @Override
        public void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onPasswordChanged：" + status.getmStatusMsg());
        }

        @Override
        public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onClose：" + status.getmStatusMsg());
        }

        @Override
        public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onStop：" + status.getmStatusMsg());
        }

        @Override
        public void onLock(Bluetooth bluetooth, BleLockerStatus status) {
            if(status==BleLockerStatus.LOCKED){
                isLocked = true;
            }else{
                isLocked = false;
            }
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onLock：" + status.getmStatusMsg());
        }

        @Override
        public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "..\n   onOpen：" + status.getmStatusMsg());
        }

        @Override
        public void onBleReadResponse(Bluetooth bluetooth,  byte[] data, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onBleReadResponse：" + String.format("read: %s", ByteUtils.byteToString(data))+"\n"
                    +"\n   onBleReadResponse Status:"+ status.getmStatusMsg());
        }

        @Override
        public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onBleWriteResponse：" + status.getmStatusMsg()
            );
        }

        @Override
        public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onNotifyResponse：" +  NotifyValue
                    +"\n    on NotifyResponse Status: " + status.getmStatusMsg());
        }

        @Override
        public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onConnected：" + status.getmStatusMsg());
        }

        @Override
        public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onDisconnected：" + status.getmStatusMsg());
        }

        @Override
        public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
            hideWaitDialog();
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onReday feed back：" + status.getmStatusMsg());

        }

        @Override
        public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onGetRssi feed back：" + status.getmStatusMsg());
        }
        @Override
        public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "...\n   onPasswdError：" + status.getmStatusMsg());
        }
    };

}
