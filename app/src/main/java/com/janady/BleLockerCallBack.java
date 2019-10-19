package com.janady;

import android.content.Context;
import android.widget.Toast;

import com.example.funsdkdemo.R;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.janady.database.model.Bluetooth;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

public class BleLockerCallBack implements BleLocker.IBleLockerListener {
    private boolean mIsTaost = false;
    private Context context;

    public BleLockerCallBack(Context context, boolean isTaostShow){

        this.mIsTaost = isTaostShow;
        this.context = context;
    }

    @Override
    public void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 密码修改, onPasswordChanged：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 密码修改, onPasswordChanged：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 功能-开 onOpened：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 功能-开 onOpened：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }


    @Override
    public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 功能-关, onClosed：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 功能-关, onClosed：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 功能-停 onStoped：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 功能-停 onStoped：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }


    @Override
    public void onLock(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 功能-锁 onLock：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 功能-锁 onLock：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onBleReadResponse(Bluetooth bluetooth, byte[] data, BleLockerStatus status) {
        BluetoothLog.i(" 读取返回信息 onReadResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg()
                +" bodycontent"+String.format("read: %s", ByteUtils.byteToString(data))+"\n");

        AppendText(" 读取返回信息 onReadResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg()
                +" bodycontent"+String.format("read: %s", ByteUtils.byteToString(data))+"\n");
    }

    @Override
    public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 发送数据 onWriteResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 发送数据 onWriteResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        //BluetoothLog.v(String.format("%s onWriteResponse", this.getClass().getSimpleName()));
    }

    @Override
    public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {
        BluetoothLog.i(" 设备消息 onBleNotifyResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 设备消息 onBleNotifyResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 连接设备，onConnected：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 连接设备，onConnected：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 断开连接，onDisconnected：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 断开连接，onDisconnected：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 发送心跳，onHeartBeatting：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 发送心跳，onHeartBeatting：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 设备已准备，onReday：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 设备已准备，onReday：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }

    @Override
    public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {
        BluetoothLog.i(" RSSI，onGetRssi：rssi="+ Rssi +"   code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" RSSI，onRssi：rssi="+ Rssi +"   code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }
    @Override
    public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
        BluetoothLog.i(" 密码错误，onPasswdError：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        AppendText(" 密码错误，onPasswdError：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
    }
    private void AppendText(String text) {
        if(mIsTaost){
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }
    private String getTime() {
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

}
