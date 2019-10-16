package com.janady.device;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.janady.BleLockerCallBack;
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
    private BleLocker bleLocker;
    private Button searchBtn;
    QMUITopBarLayout mTopBar;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jbluetooth_lock_layout, null);
        mTopBar = root.findViewById(R.id.topbar);
        initTopBar();
        bleLocker = new BleLocker(MAC, false, BleService,
                BleNotifitesCharacter, BleWriteCharacter, "123123",800, iBleLockerCallBack);
        root.findViewById(R.id.open).setOnClickListener(this);
        root.findViewById(R.id.close).setOnClickListener(this);
        root.findViewById(R.id.lock).setOnClickListener(this);
        root.findViewById(R.id.unlock).setOnClickListener(this);
        root.findViewById(R.id.connect).setOnClickListener(this);
        root.findViewById(R.id.disconnect).setOnClickListener(this);
        root.findViewById(R.id.password).setOnClickListener(this);
        searchBtn = root.findViewById(R.id.scan);
        searchBtn.setOnClickListener(this);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        bleLocker.connect();
    }

    @Override
    public void onStop() {
        bleLocker.disconnect();
        super.onStop();
    }

    private void initTopBar() {
        mTopBar.setTitle("我的摄像机");
        mTopBar.addRightImageButton(R.drawable.scan_capture, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startFragment(new JTabSegmentFragment());
            }
        });
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }


    BleLocker.IBleLockerListener iBleLockerCallBack = new BleLockerCallBack(requireContext(),true);

    private boolean isScanning = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
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
////                Beacon beacon = new Beacon(device.scanRecord);
////                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
//
////                BeaconItem beaconItem = null;
////                BeaconParser beaconParser = new BeaconParser(beaconItem);
////                int firstByte = beaconParser.readByte(); // 读取第1个字节
////                int secondByte = beaconParser.readByte(); // 读取第2个字节
////                int productId = beaconParser.readShort(); // 读取第3,4个字节
////                boolean bit1 = beaconParser.getBit(firstByte, 0); // 获取第1字节的第1bit
////                boolean bit2 = beaconParser.getBit(firstByte, 1); // 获取第1字节的第2bit
////                beaconParser.setPosition(0); // 将读取起点设置到第1字节处
//            }
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
}
