package com.janady.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
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
    public int getItemLayoutId(int viewType) {
        return R.layout.jcamera_device_item;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, final int position, Camera item) {
        mCurrCamDev = item;

        holder.getTextView(R.id.item_name).setText("场景："+item.sceneName);
        holder.getTextView(R.id.item_sn).setText("SN："+item.name);

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
                intent.putExtra("FUN_DEVICE_ID", mCurrCamDev.sn);
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
                builder.setTitle("温馨提示");
                builder.setMessage("您确定要删除此摄像机吗?");
                //确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.liteOrm.delete(mCurrCamDev);
                        CameraDeviceAdapter.super.notifyItemRemoved(position);
                        mDevs = MyApplication.liteOrm.query(Camera.class);
                        CameraDeviceAdapter.super.setData(mDevs);
                        CameraDeviceAdapter.super.notifyDataSetChanged();
                        //确定删除的代码
                        Toast.makeText(context, "删除成功", 0).show();
                    }
                });
                //点取消按钮
                builder.setNegativeButton("取消", null);
                builder.show();
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
