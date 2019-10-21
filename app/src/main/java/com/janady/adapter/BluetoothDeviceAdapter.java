package com.janady.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.DialogWaitting;
import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.devices.playback.ActivityGuideDeviceRecordList;
import com.janady.AppConstants;
import com.janady.BleLockerCallBack;
import com.janady.Dialogs;
import com.janady.Util;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.common.JDialogModifyPasswd;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.device.BluetoothLockFragment;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.models.FunDevice;

import java.io.File;
import java.util.List;

public class BluetoothDeviceAdapter extends BaseRecyclerAdapter<Bluetooth> {
    private Context context;
    private Bluetooth mCurrBleDev;
    private List<Bluetooth> mDevs;
    private BleLocker bleLocker;
    private DialogWaitting mWaitDialog = null;

    private ImageButton ibChaPasswd;
    private TextView tvMac;
    private TextView tvRssi;

    public BluetoothDeviceAdapter(Context ctx, List<Bluetooth> data) {
        super(ctx, data);
        this.context = ctx;
        this.mDevs = data;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.jbluetooth_device_item;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, final int position, Bluetooth item) {
        mCurrBleDev = item;

        holder.getTextView(R.id.item_ble_name).setText("场景："+item.sceneName);
        holder.getTextView(R.id.item_ble_sn).setText("SN："+item.name);
        holder.getTextView(R.id.item_ble_mac).setText("MAC："+item.mac);
        //tvMac=holder.getTextView(R.id.item_ble_mac);
        //tvMac.setText("MAC："+item.mac);
        //tvRssi=holder.getTextView(R.id.item_ble_rssi);

        ImageView iv = holder.getImageView(R.id.bleCover);
        iv.setImageResource(R.drawable.ic_bluetooth_black_24dp);

        holder.getImageButton(R.id.ibBleDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);//内部类
                builder.setTitle("温馨提示");
                builder.setMessage("您确定要删除此蓝牙设备吗?");
                //确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.liteOrm.delete(mCurrBleDev);
                        BluetoothDeviceAdapter.super.notifyItemRemoved(position);
                        mDevs = MyApplication.liteOrm.query(Bluetooth.class);
                        BluetoothDeviceAdapter.super.setData(mDevs);
                        BluetoothDeviceAdapter.super.notifyDataSetChanged();
                        //确定删除的代码
                        Toast.makeText(context, "删除成功", 0).show();
                    }
                });
                //点取消按钮
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        mWaitDialog = new DialogWaitting(context);

        ibChaPasswd=holder.getImageButton(R.id.ibBleChangePassword);
        ibChaPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleLocker = new BleLocker(mCurrBleDev.mac, false, AppConstants.bleService,
                        AppConstants.bleNotifitesCharacter, AppConstants.bleWriteCharacter , mCurrBleDev.password,800, iBleLockerListener);

                //bleLocker.disconnect();
                bleLocker.connect();
                bleLocker.setmNoRssi(true);
                mWaitDialog.show();
            }
        });

    }

    private BleLocker.IBleLockerListener iBleLockerListener = new BleLocker.IBleLockerListener() {
        @Override
        public void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status) {
            Dialogs.alertDialogBtn(context, "提示", "密码修改成功！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(bleLocker!=null){ bleLocker.disconnect();}
                }
            });
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
        public void onBleReadResponse(Bluetooth bluetooth, byte[] data, BleLockerStatus status) {

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
            //tvMac.setText("未连接");
        }

        @Override
        public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
            mWaitDialog.dismiss();
            //ibChaPasswd.setEnabled(true);
            showInputPasswordDialog();
        }

        @Override
        public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {

        }
        @Override
        public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
        }
        @Override
        public void onResetted(Bluetooth bluetooth, int Resetted ,BleLockerStatus status) {
        }
    };

    /**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog() {
        JDialogModifyPasswd inputDialog = new JDialogModifyPasswd(context,
                context.getResources().getString(R.string.device_login_input_password),
                "","","",
                R.string.common_confirm,
                R.string.common_cancel
        ){
            @Override
            public boolean confirm(String oldPasswd, String newPasswd, String confirmPasswd) {

                    if(confirmPasswd.equals(newPasswd)){

                        bleLocker.connect();

                        bleLocker.changePassword(newPasswd);

                        mCurrBleDev.password = newPasswd;

                        MyApplication.liteOrm.save(mCurrBleDev);
                    }else{
                        this.mMessages.setText("密码错误！蓝牙设备未连接成功！");
                        this.mMessages.setVisibility(View.VISIBLE);
                        return false;
                    }

                return super.confirm(oldPasswd,newPasswd,confirmPasswd);

            }

            @Override
            public void cancel() {
                super.cancel();

                // 取消输入密码,直接退出
            }

        };

        inputDialog.show();
    }

}
