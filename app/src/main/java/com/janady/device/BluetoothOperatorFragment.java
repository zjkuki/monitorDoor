package com.janady.device;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.janady.Util;
import com.janady.base.JBaseSegmentFragment;
import com.janady.database.model.Bluetooth;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BluetoothOperatorFragment extends JBaseSegmentFragment implements View.OnClickListener {
    private List<Bluetooth> mBluetooths;
    private Bluetooth mBluetooth;

    private List<BleLocker> mBleLockers;
    private BleLocker mBleLocker;

    private TextView tvresult;

    private boolean debugMsg;

    private boolean isLocked = false;

    private List<String> viewTitleList = new ArrayList<>();

    public void setBluetoothDevice(Bluetooth bluetooth){
        mBluetooth = bluetooth;
    }
    public Bluetooth getBluetoothDevice(){
        return mBluetooth;
    }

    public void setBleLocker(BleLocker bleLocker){
        mBleLocker = bleLocker;
    }
    public BleLocker getBleLocker(){
        return mBleLocker;
    }

    @Override
    protected String title() {
        return "蓝牙控制";
    }

    @Override
    protected List<View> createPageViews() {
        List<View> viewList = new ArrayList<>();

        //ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        List<Bluetooth> blists = MyApplication.liteOrm.query(new QueryBuilder<Bluetooth>(Bluetooth.class).whereEquals(Bluetooth.COL_MAC, mBleLocker.getmMac()));
        mBleLockers = new ArrayList<BleLocker>();
        for (Bluetooth bluetooth : blists) {

            mBleLocker = new BleLocker(bluetooth,false,800, iBleLockerListener);
            mBleLocker.setmNoRssi(true);

            mBleLockers.add(mBleLocker);

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.jbluetooth_operator_layout, null);

            tvresult = view.findViewById(R.id.result);

            view.findViewById(R.id.open).setOnClickListener(this);
            view.findViewById(R.id.close).setOnClickListener(this);
            view.findViewById(R.id.lock).setOnClickListener(this);
            view.findViewById(R.id.unlock).setOnClickListener(this);


            viewTitleList.add(bluetooth.sceneName);
            viewList.add(view);


            showWaitDialog();

            tabSelected(0);

            //mBleLocker.connect();
        }

        return viewList;
    }

    @Override
    protected  List<String> getPageTitles(){
        return viewTitleList;
    }

    @Override
    protected void tabSelected(int index) {
        mBleLockers.get(index).connect();
    }

    @Override
    protected void tabUnSelected(int index) {
        mBleLockers.get(index).disconnect();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.open: {
                mBleLocker.open();
            }
            break;
            case R.id.close:{
                mBleLocker.close();

            }
            break;
            case R.id.lock:{
                mBleLocker.lock();
            }
            break;
            case R.id.unlock:{
                mBleLocker.stop();
            }
            break;
        }

    }

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
