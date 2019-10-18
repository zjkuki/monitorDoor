package com.janady;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;


public class RoundRect {

    private int width;
    private int height;
    private float cornerRadius;

    /**
     * 用于初始化圆角矩形基本参数
     *
     * @param width        图片宽度
     * @param height       图片高度
     * @param cornerRadius 圆角半径
     */
    public RoundRect(int width, int height, float cornerRadius) {
        this.width = width;
        this.height = height;
        this.cornerRadius = cornerRadius;
    }

    /**
     * 用于把普通图片转换为圆角矩形图像
     *
     * @param path 图片路径
     * @return output 转换后的圆角矩形图像
     */
    Bitmap toRoundRect(String path) {
        //创建位图对象
        Bitmap photo = lessenUriImage(path);
        return Transformation(photo);
    }

    /**
     * 用于把普通图片转换为圆角矩形图像
     *
     * @param imageID 图片资源ID
     * @param context 上下文对象
     * @return output 转换后的圆角矩形图像
     */
    public Bitmap toRoundRect(Context context, int imageID) {
        //创建位图对象
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), imageID);
        return Transformation(photo);
    }

    /**
     * 用于把Uri图片转换为Bitmap对象
     *
     * @param path 图片URI地址
     * @return 生成的Bitmap对象
     */
    public final static Bitmap lessenUriImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); //此时返回 bm 为空
        options.inJustDecodeBounds = false; //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = (int) (options.outHeight / (float) 320);
        if (be <= 0) be = 1;
        options.inSampleSize = be; //重新读入图片，注意此时已经把 options.inJustDecodeBounds 设回 false 了
        bitmap = BitmapFactory.decodeFile(path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + " " + h); //after zoom
        return bitmap;
    }

    /**
     * 用于把Bitmap图像转换为圆角图像
     *
     * @param photo 需要转换的Bitmap对象
     * @return 转换成圆角的Bitmap对象
     */
    private Bitmap Transformation(Bitmap photo) {

        //根据源文件新建一个darwable对象
        Drawable imageDrawable = new BitmapDrawable(photo);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, width, height);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(outerRect, cornerRadius, cornerRadius, paint);

        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, width, height);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        return output;
    }

    public Bitmap appendTextToPicture(final String picPath, final String msg){
        Bitmap bmp = BitmapFactory.decodeFile(picPath);
        return appendTextToPicture(bmp, msg);
    }

    public Bitmap appendTextToPicture(final Bitmap bmp, final String msg) {
//返回具有指定宽度和高度可变的位图,它的初始密度可以调用getDensity()
        final int TXT_SIZE = 10;
        //Bitmap bmp = BitmapFactory.decodeFile(picPath);
        final int y_offset = 0;
        int heigth = bmp.getHeight() + y_offset + TXT_SIZE;
        final int max_width = bmp.getWidth();
        List<String> buf = new ArrayList<String>();
        String lineStr = "";

        Paint p = new Paint();
        Typeface font = Typeface.create("宋体", Typeface.BOLD);
        p.setColor(Color.BLACK);
        p.setTypeface(font);
        p.setTextSize(TXT_SIZE);

        for (int i = 0; i < msg.length(); ) {

            if (Character.getType(msg.charAt(i)) == Character.OTHER_LETTER) {
                // 如果这个字符是一个汉字
                if ((i + 1) < msg.length()) {
                    lineStr += msg.substring(i, i + 2);
                }

                i = i + 2;
            } else {
                lineStr += msg.substring(i, i + 1);
                i++;
            }

            float[] ws = new float[lineStr.length()];
            int wid = p.getTextWidths(lineStr, ws);

            if (wid > max_width) {
                buf.add(lineStr);
                lineStr = "";
                heigth += (TXT_SIZE + y_offset);
            }

            if (i >= msg.length()) {
                heigth += (TXT_SIZE + y_offset);
                break;
            }
        }


        Bitmap canvasBmp = Bitmap.createBitmap(max_width, heigth + TXT_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBmp);
        canvas.drawColor(Color.BLACK);

        float y = y_offset + TXT_SIZE;
        for (String str : buf) {
            canvas.drawText(str, 0, y, p);
            y += (TXT_SIZE + y_offset);
        }

        canvas.drawBitmap(bmp, 0, y, p);
        return canvasBmp;
    }
}