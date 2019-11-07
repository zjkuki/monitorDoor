package com.janady.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lkd.smartlocker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/5/9.
 */
public class CustomCircle extends View {
    public static final String IR_DOWN = "down";
    public static final String IR_DEFINE = "define";
    public static final String IR_UP = "up";
    public static final String IR_LEFT = "left";
    public static final String IR_RIGHT = "right";

    //背景图片，以及覆盖图片
    private int mIdRoundBg = R.drawable.btlocker2;
    private int mIdUp = R.drawable.icon_arrow_up;
    private int mIdDown = R.drawable.icon_arrow_down;
    private int mIdLeft = R.drawable.icon_arrow_left;
    private int mIdRight = R.drawable.icon_arrow_right;
    private int mIdDefine = R.drawable.icon_back;

    private Paint mPaint;

    //以上资源文件生成对应的位图
    private Bitmap bitmap;
    private Bitmap upBitMap;
    private Bitmap downBitMap;
    private Bitmap leftBitMap;
    private Bitmap rightBitMap;
    private Bitmap defineBitMap;

    //确认键半径
    private int defineRadio;

    //背景图宽度
    private int mWidth;
    //半径
    private int mRadio;

    //按下时X，Y
    private float mDownY;
    private float mDownX;

    //监听
    private OnMyClickListener mListener;

    //覆盖图标识列表
    private List<String> studyList = new ArrayList<>();

    public CustomCircle(Context context) {
        super(context);
        init();
    }

    public CustomCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 设置监听事件
     * @param onMyClickListener
     */
    public void setOnClick(OnMyClickListener onMyClickListener){
        this.mListener = onMyClickListener;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(7);
        mPaint.setColor(getResources().getColor(R.color.red_primary_color));

        bitmap = BitmapFactory.decodeResource(this.getResources(), mIdRoundBg);
        upBitMap = BitmapFactory.decodeResource(this.getResources(), mIdUp);
        downBitMap = BitmapFactory.decodeResource(this.getResources(), mIdDown);
        leftBitMap = BitmapFactory.decodeResource(this.getResources(), mIdLeft);
        rightBitMap = BitmapFactory.decodeResource(this.getResources(), mIdRight);
        defineBitMap = BitmapFactory.decodeResource(this.getResources(), mIdDefine);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //mWidth = bitmap.getWidth();
        mWidth = 240;
        mRadio = mWidth / 2;
        //defineRadio = defineBitMap.getWidth() / 2;
        defineRadio = 120 / 2;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //设置宽高默认值，可以设置为warp_content借鉴网上的，也不是很懂
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(mWidth, widthSize);
        } else {
            width = mWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mWidth, heightSize);
        } else {
            height = mWidth;
        }

        setMeasuredDimension(width, height);
    }

    /**
     * 设置覆盖的某个按钮
     * @param code
     */
    public void setStudy(String code){
        if (!studyList.contains(code)){
            studyList.add(code);
        }
        invalidate();//重新绘制
    }

    /**
     * 移除某个按钮的覆盖
     * @param code
     */
    public void removeStudy(String code){
        if (studyList.contains(code)){
            studyList.remove(code);
        }
        invalidate();//重新绘制
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景图
        canvas.drawBitmap(bitmap,0,0,mPaint);

        //根据标识列表绘制覆盖图
        for (int i = 0 ; i < studyList.size() ; i ++){
            String code = studyList.get(i);
            switch (code){
                case IR_DEFINE:
                    canvas.drawBitmap(defineBitMap,mRadio - defineRadio,mRadio - defineRadio,mPaint);
                    break;
                case IR_LEFT:
                    canvas.drawBitmap(leftBitMap,0, mRadio - (float) Math.sqrt(mRadio * mRadio / 2),mPaint);
                    break;
                case IR_RIGHT:
                    canvas.drawBitmap(rightBitMap,2 * mRadio - (float) Math.sqrt(mRadio * mRadio / 2),mRadio - (float) Math.sqrt(mRadio * mRadio / 2),mPaint);
                    break;
                case IR_DOWN:
                    canvas.drawBitmap(downBitMap,mRadio - (float) Math.sqrt(mRadio * mRadio / 2),2 * mRadio - (float) Math.sqrt(mRadio * mRadio / 2),mPaint);
                    break;
                case IR_UP:
                    canvas.drawBitmap(upBitMap,mRadio - (float) Math.sqrt(mRadio * mRadio / 2),0,mPaint);
                    break;

            }
        }

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float mUpX = event.getX();
                float mUpY = event.getY();

                //获取移动距离
                float dUpY = Math.abs((mDownY - mUpY));
                float dUpX = Math.abs((mDownX - mUpX));

                //获取离开时离原点距离
                float dY = mUpY - mRadio;
                float dX = mUpX - mRadio;

                //当手指有滑动时不作处理
                if (dUpX > 10 || dUpY > 10){
                    return true;
                }

                //获取tan值，然后判断属于哪个模块被点击，需要复习小学数学
                double degrees = Math.toDegrees(Math.atan (dY / dX));
                if (Math.abs(dY) < defineRadio && Math.abs(dX) < defineRadio ){
                    //设置对应的监听
                    mListener.onClickListener(IR_DEFINE);
                } else if (dY < 0 && dX < 0) {
                    if (degrees > 0 && degrees < 45){
                        mListener.onClickListener(IR_LEFT);
                    } else if (degrees > 45 && degrees < 90) {
                        mListener.onClickListener(IR_UP);
                    }
                } else if (dY < 0 && dX >0) {
                    if (degrees > -90 && degrees < -45){
                        mListener.onClickListener(IR_UP);
                    } else if (degrees > -45 && degrees < 0) {
                        mListener.onClickListener(IR_RIGHT);
                    }
                } else if (dY > 0 && dX > 0) {
                    if (degrees > 0 && degrees < 45){
                        mListener.onClickListener(IR_RIGHT);
                    } else if (degrees > 45 && degrees < 90) {
                        mListener.onClickListener(IR_DOWN);
                    }
                } else if (dY > 0 && dX < 0) {
                    if (degrees > -90 && degrees < -45){
                        mListener.onClickListener(IR_DOWN);
                    } else if (degrees > -45 && degrees < 0) {
                        mListener.onClickListener(IR_LEFT);
                    }
                }
                break;
        }
        return true;
    }

    interface OnMyClickListener{
        void onClickListener(String code);
    }

}
