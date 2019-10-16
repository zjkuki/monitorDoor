package com.janady.device;

import android.view.LayoutInflater;
import android.view.View;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.base.JBaseSegmentFragment;
import com.janady.database.model.Bluetooth;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;

import java.util.ArrayList;
import java.util.List;

public class BluetoothOperatorFragment extends JBaseSegmentFragment implements View.OnClickListener {
    private Bluetooth mBluetooth;
    private BleLocker mBleLocker;

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
        for (Bluetooth bluetooth : blists) {

            mBleLocker = new BleLocker(bluetooth,false,800, iBleLockerListener);

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.jbluetooth_operator_layout, null);
            view.findViewById(R.id.open).setOnClickListener(this);
            view.findViewById(R.id.close).setOnClickListener(this);
            view.findViewById(R.id.lock).setOnClickListener(this);
            view.findViewById(R.id.unlock).setOnClickListener(this);
            viewList.add(view);

            showWaitDialog();
            mBleLocker.connect();
        }

        return viewList;
    }

    @Override
    protected void tabSelected(int index) {

    }

    @Override
    protected void tabUnSelected(int index) {

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

        }

        @Override
        public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onLock(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {
        }

        @Override
        public void onBleReadResponse(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {

        }

        @Override
        public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {
        }

        @Override
        public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
            hideWaitDialog();

        }

        @Override
        public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {

        }
        @Override
        public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
        }
    };
}
