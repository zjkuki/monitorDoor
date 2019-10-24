package com.janady.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
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

import com.example.common.DialogInputPasswd;
import com.example.common.DialogWaitting;
import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.devices.playback.ActivityGuideDeviceRecordList;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
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
import com.janady.lkd.ClientManager;
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
    private String oldPsw = "";

    private boolean needCheckSTA = false;

    private ImageButton ibChaPasswd;
    private TextView tvMac;
    private TextView tvRssi;
    private JDialogModifyPasswd inputDialog = null;
    private AlertDialog.Builder builder = null;
    private int step = 0; //0-正常   1-输入密码

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
                /*bleLocker = new BleLocker(mCurrBleDev,false, 800, iBleLockerListener);
                bleLocker.setmNoRssi(true);
                bleLocker.connect();

                oldPsw = mCurrBleDev.password;

                mWaitDialog.show();*/
                 //showInputModifyPasswordDialog();

                showInputPasswordDialog();
            }
        });

    }

    private BleLocker.IBleLockerListener iBleLockerListener = new BleLocker.IBleLockerListener() {
        @Override
        public void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status) {
            BluetoothLog.i(" 密码修改, onPasswordChanged：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
            if( step==2 ) {
                step=3;
                Dialogs.alertDialogBtn(context, "提示", "密码修改成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.liteOrm.save(mCurrBleDev);
                        if (bleLocker != null) {
                            bleLocker.disconnect();
                        }
                        return;
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mCurrBleDev.password = oldPsw;
                        if (bleLocker != null) {
                            bleLocker.disconnect();
                        }
                        return;
                    }
                });
            }
        }

        @Override
        public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onLock(Bluetooth bluetooth, BleLockerStatus status) {
            BluetoothLog.i(" 功能-锁 onLock：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");

        }

        @Override
        public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {
            BluetoothLog.i(" 功能-开 onOpened：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
        }

        @Override
        public void onBleReadResponse(Bluetooth bluetooth, byte[] data, BleLockerStatus status) {
            BluetoothLog.i(" 读取返回信息 onReadResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg()
                    +" bodycontent"+String.format("read: %s", ByteUtils.byteToString(data))+"\n");

        }

        @Override
        public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {
            BluetoothLog.i(" 发送数据 onWriteResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");

        }

        @Override
        public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {
            BluetoothLog.i(" 设备消息 onBleNotifyResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");

        }

        @Override
        public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {
            BluetoothLog.i(" 连接设备，onConnected：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
            if(status==BleLockerStatus.CONNECTED && needCheckSTA) {
                bleLocker.sta();
            }
        }

        @Override
        public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {
            BluetoothLog.i(" 断开连接，onDisconnected：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
            //tvMac.setText("未连接");
        }

        @Override
        public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
            BluetoothLog.i(" 设备已准备，onReday：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
            mWaitDialog.dismiss();
            if(step==1) {
                showInputModifyPasswordDialog();
            }
            //ibChaPasswd.setEnabled(true);
        }

        @Override
        public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {
            BluetoothLog.i(" RSSI，onGetRssi：rssi="+ Rssi +"   code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");

        }
        @Override
        public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
            mWaitDialog.dismiss();
            BluetoothLog.i(" 密码错误，onPasswdError：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
            /*alertDialog("原密码输入不正确，请重新输入！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showInputModifyPasswordDialog();

                    return;
                }
            }, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    return;
                }
            });*/
            showInputPasswordDialog();
        }
        @Override
        public void onResetted(Bluetooth bluetooth, int Resetted ,BleLockerStatus status) {
            needCheckSTA=false;
            if(Resetted ==1 ) {
                BluetoothLog.i(" 设备已重置，onResetted：code=" + status.getSatusId() + " message=" + status.getmStatusMsg() + "\n");
                Dialogs.alertDialogBtn(context, "提示", "设备已重置为出厂状态，输入新密码可重新激活本设备", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        oldPsw = "LKD.CN";
                        bleLocker = new BleLocker(bleLocker.getmBluetooth(), false, 800, iBleLockerListener);
                        bleLocker.setmPassword(oldPsw);
                        bleLocker.setmNoRssi(true);
                        bleLocker.connect();
                        showInputModifyPasswordDialog();
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                    }
                });
            }else{
                BluetoothLog.i(" 设备正常，onResetted：code=" + status.getSatusId() + " message=" + status.getmStatusMsg() + "\n");

            }
        }
    };

    public void alertDialog(String text, DialogInterface.OnClickListener onClickListener, DialogInterface.OnCancelListener onCancelListener){
        if(builder!=null){return;}
        builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setMessage(text);
        builder.setPositiveButton("确定", onClickListener);
        builder.setOnCancelListener(onCancelListener);
        builder.show();
    }

    /**
     * 显示输入设备密码对话框
     */
    private void showInputModifyPasswordDialog() {
        step = 0;
        if(inputDialog!=null){inputDialog.hide();}
        inputDialog = new JDialogModifyPasswd(context,
                context.getResources().getString(R.string.device_login_input_password),
                "","","",
                R.string.common_confirm,
                R.string.common_cancel,
                true
        ){
            @Override
            public boolean confirm(String oldPasswd, String newPasswd, String confirmPasswd) {
                step=2;
                if(confirmPasswd.equals(newPasswd)){
                    while (!bleLocker.getIsReday()) {
                    }
                    bleLocker.changePassword(newPasswd);
                    bleLocker.getmBluetooth().password = newPasswd;
                    MyApplication.liteOrm.save(bleLocker.getmBluetooth());

                    bleLocker = new BleLocker(bleLocker.getmBluetooth(), false, 800, iBleLockerListener);
                    bleLocker.setmPassword(newPasswd);
                    bleLocker.setmNoRssi(true);
                    bleLocker.connect();

                    MyApplication.liteOrm.save(bleLocker.getmBluetooth());

                    mWaitDialog.show();

                }else{
                    this.mMessages.setText("两次密码输入不正确！");
                    this.mMessages.setVisibility(View.VISIBLE);
                    return false;
                }

                return super.confirm(oldPasswd,newPasswd,confirmPasswd);

            }

            @Override
            public void cancel() {
                super.cancel();
                if(bleLocker!=null){bleLocker.disconnect();}
                hide();
                // 取消输入密码,直接退出
            }

        };

        inputDialog.show();
    }

    /**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog() {
        DialogInputPasswd inputDialog = new DialogInputPasswd(context,
                context.getResources().getString(R.string.device_login_input_password),
                "",
                R.string.common_confirm,
                R.string.common_cancel
        ){

            @Override
            public boolean confirm(String editText) {
                step = 1;
                //if(bleLocker.getmBluetooth()!=null){
                if(mCurrBleDev!=null){
                    //bleOldPsw = mBluetooth.password;
                    //if(editText.equals(bleOldPsw)) {
                    oldPsw = editText;
                    mCurrBleDev.password = editText;
                    mCurrBleDev.isFirst = false;

                    bleLocker = new BleLocker(mCurrBleDev, false, 800, iBleLockerListener);
                    bleLocker.setmNoRssi(true);
                    bleLocker.connect();

                    MyApplication.liteOrm.save(mCurrBleDev);

                    mWaitDialog.show();
                    super.hide();
                }
                return super.confirm(editText);
            }

            @Override
            public void cancel() {
                super.cancel();
                if(bleLocker!=null){bleLocker.disconnect();}
                hide();
                // 取消输入密码,直接退出
            }

        };

        inputDialog.show();
    }
}
