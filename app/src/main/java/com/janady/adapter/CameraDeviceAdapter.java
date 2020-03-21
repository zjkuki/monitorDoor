package com.janady.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.funsdkdemo.MyApplication;
import com.janady.utils.MqttUtil;
import com.janady.common.JQrcodePopDialog;
import com.lkd.smartlocker.R;
import com.example.funsdkdemo.devices.playback.ActivityGuideDeviceRecordList;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.database.model.Camera;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.models.FunDevice;

import java.io.File;
import java.util.List;

public class CameraDeviceAdapter extends BaseRecyclerAdapter<Camera> {
    private Context context;
    private Camera mCurrCamDev;
    private List<Camera> mDevs;

    public CameraDeviceAdapter(Context ctx, List<Camera> data) {
        super(ctx, data);
        this.context = ctx;
        this.mDevs = data;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.jcamera_device_item;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, final int position, Camera item) {
        if (mDevs!=null) {
            mCurrCamDev = mDevs.get(position);
        }else{
            return;
        }
        holder.getTextView(R.id.item_name).setText(context.getString(R.string.SCENE)+item.sceneName);
        holder.getTextView(R.id.item_sn).setText("SN："+item.name);

        if(mCurrCamDev.isOnline){
            holder.getTextView(R.id.item_status).setText(context.getString(R.string.ONLINE));
            holder.getTextView(R.id.item_status).setTextColor(Color.BLUE);
        }else{
            holder.getTextView(R.id.item_status).setTextColor(Color.RED);
            holder.getTextView(R.id.item_status).setText(context.getString(R.string.OFFLINE));
        }
        ImageView iv = holder.getImageView(R.id.cover);
        String path = FunPath.getCoverPath(item.sn);

        File file = new File(path);
        if (file.exists()) {
            iv.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            iv.setImageBitmap(bitmap);
        } else {
            iv.setVisibility(View.GONE);
        }

        holder.getImageButton(R.id.ibCamLocalFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("FUN_DEVICE_ID", mDevs.get(position).sn);
                intent.putExtra("FILE_TYPE", "h264;mp4");
                intent.setClass(context, ActivityGuideDeviceRecordList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.getImageButton(R.id.ibCamDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);//内部类
                builder.setTitle(R.string.DLG_TIPS_TITLE);
                builder.setMessage(R.string.DLG_CAMERA_DEL);
                //确定按钮
                builder.setPositiveButton(context.getString(R.string.common_confirm), new DialogInterface.OnClickListener() {

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.liteOrm.delete(mDevs.get(position));
                        //CameraDeviceAdapter.super.notifyItemRemoved(position);
                        mDevs = MyApplication.liteOrm.query(Camera.class);
                        CameraDeviceAdapter.super.setData(mDevs);
                        CameraDeviceAdapter.super.notifyDataSetChanged();
                        //确定删除的代码
                        Toast.makeText(context, context.getString(R.string.delete_s), 0).show();
                    }
                });
                //点取消按钮
                builder.setNegativeButton(context.getString(R.string.common_cancel), null);
                builder.show();
            }
        });

        holder.getImageButton(R.id.ibcamshare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject json = new com.alibaba.fastjson.JSONObject();

                    json.put("from", MqttUtil.getCLIENTID());
                    json.put("action", "shareCameraDevice");
                    json.put("mac", mDevs.get(position).mac);

                    //json.put("SDPT", shareDevicePublishTopic);
                    //json.put("SDRT", shareDevicePublishTopic);
                    String content = Base64.encodeToString(json.toJSONString().getBytes(), Base64.DEFAULT);
                    //Bitmap bitmap = xxxxx;// 这里是获取图片Bitmap，也可以传入其他参数到Dialog中
                    JQrcodePopDialog.Builder dialogBuild = new JQrcodePopDialog.Builder(context);
                    Bitmap mQrCodeBmp=makeQRCode(content);
                    dialogBuild.setImage(mQrCodeBmp);
                    dialogBuild.setDialog_msg(context.getString(R.string.CAMERA_SHARE)+"-"+mDevs.get(position).sceneName);
                    JQrcodePopDialog dialog = dialogBuild.create();
                    dialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
                    dialog.show();
                    //Dialogs.alertMessage(context, "json", json.toJSONString());

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
//            FunVideoView funVideoView = (FunVideoView) holder.getView(R.id.funVideoView);
//            if (!item.hasLogin() || !item.hasConnected()) {
//                FunSupport.getInstance().requestDeviceLogin(item);
//            } else {
//                FunSupport.getInstance().requestDeviceConfig(item, SystemInfo.CONFIG_NAME);
//            }
        // playRealMedia(funVideoView, item);
//            if (item.getIconRes() != 0) {
//                holder.getImageView(R.id.item_icon).setImageResource(item.getIconRes());
//            }
    }

    public static boolean showCameraImage(FunDevice item, ImageView iv) {
        String path = FunPath.getCoverPath(item.getDevSn());

        File file = new File(path);
        if (file.exists()) {
            iv.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            iv.setImageBitmap(bitmap);
            return true;
        } else {
            return false;
        }
    }
}
