package com.janady.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.example.common.DialogInputPasswd;
import com.example.common.DialogWaitting;
import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.janady.Dialogs;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.common.JDialogModifyPasswd;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.WifiRemoter;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;
import com.janady.lkd.WifiRemoterBoard;

import java.util.List;

public class WifiRemoterDeviceAdapter extends BaseRecyclerAdapter<WifiRemoter> {
    private Context context;
    private WifiRemoter mCurrWifiRemoter;
    private List<WifiRemoter> mDevs;
    private WifiRemoterBoard mCurrWifiRemoterBoard;
    private DialogWaitting mWaitDialog = null;


    private ImageButton ibChaPasswd;
    private TextView tvMac;
    private TextView tvRssi;
    private JDialogModifyPasswd inputDialog = null;
    private AlertDialog.Builder builder = null;
    private int step = 0; //0-正常   1-输入密码

    public WifiRemoterDeviceAdapter(Context ctx, List<WifiRemoter> data) {
        super(ctx, data);
        this.context = ctx;
        this.mDevs = data;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.jbluetooth_device_item;
    }


    @Override
    public void bindData(RecyclerViewHolder holder, final int position, WifiRemoter item) {
        mCurrWifiRemoter = item;

        holder.getTextView(R.id.item_ble_name).setText("场景："+mCurrWifiRemoter.sceneName);
        holder.getTextView(R.id.item_ble_sn).setText("SN："+mCurrWifiRemoter.name);
        holder.getTextView(R.id.item_ble_mac).setText("MAC："+mCurrWifiRemoter.mac);

        ImageView iv = holder.getImageView(R.id.bleCover);
        iv.setImageResource(R.drawable.ic_bluetooth_black_24dp);

        holder.getImageButton(R.id.ibBleDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);//内部类
                builder.setTitle("温馨提示");
                builder.setMessage("您确定要删除此WIFI设备吗?");
                //确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.liteOrm.delete(mCurrWifiRemoter);
                        WifiRemoterDeviceAdapter.super.notifyItemRemoved(position);
                        mDevs = MyApplication.liteOrm.query(WifiRemoter.class);
                        WifiRemoterDeviceAdapter.super.setData(mDevs);
                        WifiRemoterDeviceAdapter.super.notifyDataSetChanged();
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

        ibChaPasswd.setVisibility(View.GONE);
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

                //showInputPasswordDialog();
            }
        });

    }


    public void alertDialog(String text, DialogInterface.OnClickListener onClickListener, DialogInterface.OnCancelListener onCancelListener){
        if(builder!=null){return;}
        builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setMessage(text);
        builder.setPositiveButton("确定", onClickListener);
        builder.setOnCancelListener(onCancelListener);
        builder.show();
    }


}
