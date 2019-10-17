package com.janady.device;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.Util;
import com.janady.base.JBaseSegmentFragment;
import com.janady.database.model.Bluetooth;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;

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
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
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
            viewList.add(view);

            showWaitDialog();

            tabSelected(0);
            //mBleLocker.connect();
        }

        return viewList;
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
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onPasswordChanged：\n" + status.getmStatusMsg());
        }

        @Override
        public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onClose：\n" + status.getmStatusMsg());
        }

        @Override
        public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onStop：\n" + status.getmStatusMsg());
        }

        @Override
        public void onLock(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onLock：\n" + status.getmStatusMsg());
        }

        @Override
        public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onOpen：\n" + status.getmStatusMsg());
        }

        @Override
        public void onBleReadResponse(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onBleReadResponse：\n" + status.getmStatusMsg());
        }

        @Override
        public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onBleWriteResponse：\n" + status.getmStatusMsg());
        }

        @Override
        public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onNotifyResponse：\n" + status.getmStatusMsg()
                        +"Response Content:\n"+ NotifyValue);
        }

        @Override
        public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onConnected：\n" + status.getmStatusMsg());
        }

        @Override
        public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onDisconnected：\n" + status.getmStatusMsg());
        }

        @Override
        public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
            hideWaitDialog();
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onReday feed back：\n" + status.getmStatusMsg());

        }

        @Override
        public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onGetRssi feed back：\n" + status.getmStatusMsg());
        }
        @Override
        public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name.substring(0,5) + "... onPasswdError：\n" + status.getmStatusMsg());
        }
    };




}
