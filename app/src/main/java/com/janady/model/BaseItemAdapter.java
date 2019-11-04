package com.janady.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.AppManager;
import com.janady.RoundRect;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.BluetoothListFragment;
import com.janady.device.BluetoothLockFragment;
import com.janady.device.BluetoothOperatorFragment;
import com.janady.device.CameraListFragment;
import com.janady.device.DeviceCameraFragment;
import com.janady.device.WifiRemoterBoardFragment;
import com.janady.lkd.BleLocker;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.models.FunDevStatus;
import com.lib.funsdk.support.models.FunDevice;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

public class BaseItemAdapter extends BaseRecyclerAdapter<ItemDescription> {
    private Context context;
    public BaseItemAdapter(Context ctx, List<ItemDescription> data) {
        super(ctx, data);
        this.context = ctx;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.jtest_item_layout;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, int position, final ItemDescription item) {
        if (item.getIconRes() != 0) holder.getImageView(R.id.img).setImageResource(item.getIconRes());
        holder.getTextView(R.id.name).setText(item.getName());

        RoundRect roundRect = new RoundRect(100,100,10);

        String stat = "未就绪";
        int textColor= Color.BLACK;
        Bitmap photo = null;
        //if(item.getDemoClass()== BluetoothOperatorFragment.class) {
        if(item.getDemoClass()== BluetoothLockFragment.class) {
            if(item.getEnable()) {
                stat = "在线";
                textColor = 0xFF00bfa5;
                photo = roundRect.toRoundRect(context, R.drawable.btlocker3);

            }else{
                stat = "离线";
                textColor = Color.RED;
                photo = roundRect.toRoundRect(context, R.drawable.btlocker3_disable);
            }

            //matchBleLockerOnline(item, holder);
        }

        if(item.getDemoClass()== DeviceCameraFragment.class){
            if(item.getEnable()) {
                textColor = 0xFF00bfa5;
                stat = "在线";
                photo = roundRect.toRoundRect(context, R.drawable.xmjp_camera);
            }else{
                stat = "离线";
                textColor = Color.RED;
                photo = roundRect.toRoundRect(context, R.drawable.xmjp_camera_disable);
            }

            if(item.getDemoClass()== WifiRemoterBoardFragment.class) {
                if (item.getEnable()) {
                    textColor = 0xFF00bfa5;
                    stat = "在线";
                    photo = roundRect.toRoundRect(context, R.drawable.wifiremote);
                } else {
                    stat = "离线";
                    textColor = Color.RED;
                    photo = roundRect.toRoundRect(context, R.drawable.wifiremote_disable);
                }
            }

            //checkFunDeviceStatus(item, holder);
        }

        if(photo!=null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(16);
            paint.setColor(textColor);

            Canvas cav = new Canvas(photo);
            cav.drawText(stat, 5, 23, paint);

            holder.getImageView(R.id.img).setEnabled(item.getEnable());
            holder.getImageView(R.id.img).setImageBitmap(photo);
        }
    }
    public Bitmap drawTextToBitmap(Context mContext,  int resourceId,  String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110, 110, 110));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 6;
            int y = (bitmap.getHeight() + bounds.height()) / 5;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception


            return null;
        }
    }

    /**
     * 查找蓝牙设备列表是否有这个设备
     *
     *
     */
    public void matchBleLockerOnline(final ItemDescription item, final RecyclerViewHolder holder) {

        final RoundRect roundRect = new RoundRect(100,100,10);

        final String[] stat = {"未就绪"};
        final int[] textColor = {Color.BLACK};
        final Bitmap[] photo = {null};

        if((Bluetooth)item.getItem()==null){return;}
        final Bluetooth ble = (Bluetooth) item.getItem();
        final BleLocker bleLocker = new BleLocker(ble,false,800, null);
        bleLocker.fastConnect(1, 2000,1, 2000, new BleLocker.OnCheckOnlineCallBack() {
            @Override
            public void onSuccess(Bluetooth bluetooth) {
                bleLocker.disconnect();

                ble.isOnline=true;

                MyApplication.liteOrm.save(ble);

                item.setEnable(true);

                return;
            }

            @Override
            public void onFail(Bluetooth bluetooth) {

                item.setEnable(false);

                ble.isOnline=false;
                MyApplication.liteOrm.save(ble);

                return;
            }
        });
        return;
    }

}
