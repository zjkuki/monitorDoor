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

import com.example.funsdkdemo.R;
import com.janady.RoundRect;
import com.janady.base.BaseRecyclerAdapter;
import com.janady.base.RecyclerViewHolder;
import com.janady.database.model.Bluetooth;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.BluetoothListFragment;
import com.janady.device.BluetoothLockFragment;
import com.janady.device.BluetoothOperatorFragment;
import com.janady.device.CameraListFragment;
import com.janady.device.DeviceCameraFragment;
import com.janady.lkd.BleLocker;

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

            matchBleLockerOnline(item, holder);
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
        bleLocker.connect(new BleLocker.OnCheckOnlineCallBack() {
            @Override
            public void onSuccess(Bluetooth bluetooth) {
                bleLocker.disconnect();

                stat[0] = "在线";
                textColor[0] = 0xFF00bfa5;
                photo[0] = roundRect.toRoundRect(context, R.drawable.btlocker3);

                item.setEnable(true);

                if(photo!=null) {
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setTextSize(16);
                    paint.setColor(textColor[0]);

                    Canvas cav = new Canvas(photo[0]);
                    cav.drawText(stat[0], 5, 23, paint);

                    holder.getImageView(R.id.img).setEnabled(item.getEnable());
                    holder.getImageView(R.id.img).setImageBitmap(photo[0]);
                }
                return;
            }

            @Override
            public void onFail(Bluetooth bluetooth) {
                stat[0] = "离线";
                textColor[0] = Color.RED;
                photo[0] = roundRect.toRoundRect(context, R.drawable.btlocker3_disable);
                item.setEnable(false);

                if(photo!=null) {
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setTextSize(16);
                    paint.setColor(textColor[0]);

                    Canvas cav = new Canvas(photo[0]);
                    cav.drawText(stat[0], 5, 23, paint);

                    holder.getImageView(R.id.img).setEnabled(item.getEnable());
                    holder.getImageView(R.id.img).setImageBitmap(photo[0]);
                }
                return;
            }
        });
        return;
    }
}
