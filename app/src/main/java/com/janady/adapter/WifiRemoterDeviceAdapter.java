package com.janady.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.common.DialogWaitting;
import com.example.funsdkdemo.MyApplication;
import com.janady.utils.MqttUtil;
import com.janady.common.JQrcodePopDialog;
import com.lkd.smartlocker.R;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.common.JDialogModifyPasswd;
import com.janady.database.model.WifiRemoter;
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
        return R.layout.jwifiremoter_device_item;
    }


    @Override
    public void bindData(RecyclerViewHolder holder, final int position, WifiRemoter item) {
        mCurrWifiRemoter = item;

        holder.getTextView(R.id.item_wr_name).setText(context.getString(R.string.SCENE)+mCurrWifiRemoter.sceneName);
        holder.getTextView(R.id.item_wr_sn).setText("SN："+mCurrWifiRemoter.name);
        holder.getTextView(R.id.item_wr_mac).setText("MAC："+mCurrWifiRemoter.mac);

        ImageView iv = holder.getImageView(R.id.wrCover);
        iv.setImageResource(R.drawable.ic_remote_3);

        holder.getImageButton(R.id.ibwrDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);//内部类
                builder.setTitle(R.string.DLG_TIPS_TITLE);
                builder.setMessage(R.string.WIFI_REMOTER_DEL);
                //确定按钮
                builder.setPositiveButton(context.getString(R.string.common_confirm), new DialogInterface.OnClickListener() {

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.liteOrm.cascade().delete(mCurrWifiRemoter);
                        WifiRemoterDeviceAdapter.super.notifyItemRemoved(position);
                        mDevs = MyApplication.liteOrm.query(WifiRemoter.class);
                        WifiRemoterDeviceAdapter.super.setData(mDevs);
                        WifiRemoterDeviceAdapter.super.notifyDataSetChanged();
                        //确定删除的代码
                        Toast.makeText(context, context.getString(R.string.delete_s), 0).show();
                    }
                });
                //点取消按钮
                builder.setNegativeButton(context.getString(R.string.common_cancel), null);
                builder.show();
            }
        });

        holder.getImageButton(R.id.ibwrshare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject json = new com.alibaba.fastjson.JSONObject();

                    json.put("from", MqttUtil.getCLIENTID());
                    json.put("action", "shareRemoterDevice");
                    json.put("mac", mCurrWifiRemoter.mac);

                    //json.put("SDPT", shareDevicePublishTopic);
                    //json.put("SDRT", shareDevicePublishTopic);
                    String content = Base64.encodeToString(json.toJSONString().getBytes(), Base64.DEFAULT);
                    //Bitmap bitmap = xxxxx;// 这里是获取图片Bitmap，也可以传入其他参数到Dialog中
                    JQrcodePopDialog.Builder dialogBuild = new JQrcodePopDialog.Builder(context);
                    Bitmap mQrCodeBmp=makeQRCode(content);
                    dialogBuild.setImage(mQrCodeBmp);
                    dialogBuild.setDialog_msg(context.getString(R.string.WIFI_REMOTER_SHARING)+"-"+mCurrWifiRemoter.sceneName);
                    JQrcodePopDialog dialog = dialogBuild.create();
                    dialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
                    dialog.show();
                    //Dialogs.alertMessage(context, "json", json.toJSONString());

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        mWaitDialog = new DialogWaitting(context);

        ibChaPasswd=holder.getImageButton(R.id.ibwrChangePassword);
        ibChaPasswd.setVisibility(View.GONE);
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
        builder.setPositiveButton(context.getString(R.string.common_confirm), onClickListener);
        builder.setOnCancelListener(onCancelListener);
        builder.show();
    }


}
