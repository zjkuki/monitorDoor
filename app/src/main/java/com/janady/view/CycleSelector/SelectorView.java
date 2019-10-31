package com.janady.view.CycleSelector;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Checkable;
import android.widget.Scroller;

import com.example.funsdkdemo.R;


/**
 * Created by lifengwei on 2017/7/4.
 * 可左右滑动的选择控件
 */

public class SelectorView extends ViewGroup {

    private static final String TAG = "SelectorView";

    private int columnSpace;
    private int childWidth;

    private int touchSlop;

    private CycleStruct<Integer> cycleStruct;

    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private ValueAnimator mValueAnimator;

    private int miniVelocity;
    private SeletcorAdapter adapter;
    private OnItemCheckListener mOnItemCheckListener;

    private Checkable lastCheckedItem;
    private int lastCheckedPosition = 0;

    private int x_scroll = 0;
    private float touchDownX;
    private int x_down;
    private int lastFillingX;
    private boolean mScrolling;

    public SelectorView(Context context) {
        super(context);
    }

    public SelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.SelectorView);
        columnSpace = t.getDimensionPixelSize(R.styleable.SelectorView_space,0);
        t.recycle();

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        miniVelocity = viewConfiguration.getScaledMinimumFlingVelocity();

        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new Scroller(context);

        mValueAnimator = new ValueAnimator();
        mValueAnimator.setInterpolator(new AccelerateInterpolator());
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);

        cycleStruct = new CycleStruct<>();
        cycleStruct.addData(0);
        cycleStruct.addData(1);
        cycleStruct.addData(2);
        cycleStruct.addData(3);

    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag()==null){
                return;
            }
            Checkable checkable = (Checkable) v;
            if(!checkable.isChecked()){
                lastCheckedPosition = (int) v.getTag();
                checkable.setChecked(true);
                if(lastCheckedItem!=null&&lastCheckedItem!=v){
                    lastCheckedItem.setChecked(false);
                }
                lastCheckedItem = checkable;
            }
            if(mOnItemCheckListener!=null){
                mOnItemCheckListener.onItemChecked(lastCheckedPosition);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < 4; i++) {
            getChildAt(i).setOnClickListener(clickListener);
        }
    }

    private int getMaxScrollRange(){
        if(adapter!=null&&adapter.getItemCount()>3){
            return (adapter.getItemCount()-3)*(childWidth+columnSpace);
        }
        return 0;
    }

    private int getItemWidth(){
        return childWidth+columnSpace;
    }
    private int lastStartIndex = -1;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measureHeight = getChildAt(0).getMeasuredHeight();
        int measureWidth = getChildAt(0).getMeasuredWidth();
        childWidth = measureWidth;
        //edge check
        if(x_scroll> getMaxScrollRange()){
            x_scroll = getMaxScrollRange();
        }else if(x_scroll<0){
            x_scroll = 0;
        }

        int startIndex = x_scroll/getItemWidth();
        cycleStruct.start(startIndex);
        int offset = x_scroll%getItemWidth();
        int itemStartPos = getItemWidth()*2+offset;
        int childOrder = 0;
        while (cycleStruct.canNext()){
            int i = cycleStruct.get();
            View child = getChildAt(i);
            int itemIndex = startIndex+childOrder;
            //scrolled by an item,order has changed,set value again
            if(adapter!=null&&itemIndex<adapter.getItemCount()&&(lastStartIndex!=startIndex)){
                adapter.setView(child,itemIndex);
                child.setTag(itemIndex);
                Checkable checkable = (Checkable) child;
                checkable.setChecked(lastCheckedPosition==itemIndex);
                if(lastCheckedPosition==itemIndex){
                    //set lastCheckedItem when init
                    lastCheckedItem = checkable;
                }
                //notify scrolled
                if(mOnItemCheckListener!=null&&childOrder==0){
                    mOnItemCheckListener.onScrolled(itemIndex);
                }
            }
            child.layout(itemStartPos+columnSpace/2,0,itemStartPos+columnSpace/2+measureWidth,measureHeight);
            itemStartPos -= getItemWidth();
            childOrder ++;
        }
        lastStartIndex = startIndex;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        View child = getChildAt(0);
        LayoutParams layoutParam = child.getLayoutParams();
        int childWidth = (width - 3* columnSpace)/3;
        int childHeightSpec;
        if(layoutParam == null||layoutParam.height== LayoutParams.WRAP_CONTENT){
            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
        }else if(layoutParam.height== LayoutParams.MATCH_PARENT){
            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }else{//指定高度
            childHeightSpec = MeasureSpec.makeMeasureSpec(layoutParam.height, MeasureSpec.EXACTLY);
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),childHeightSpec);
        }
        int childHeight = child.getMeasuredHeight();
        //the view height equals it's child height
        setMeasuredDimension(width,childHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelAnimation();
                touchDownX = event.getX();
                x_down = x_scroll;
                mScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(touchDownX - event.getX()) >=touchSlop) {
                    mScrolling = true;
                } else {
                    mScrolling = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mScrolling = false;
                break;
        }
        return mScrolling;
    }

    int startFilingX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = event.getX()- touchDownX;
                if(mScrolling|| Math.abs(offsetX)>touchSlop){
                    mScrolling= true;
                    x_scroll = (int) (x_down+offsetX);
                    requestLayout();
                }
                break;
            case MotionEvent.ACTION_UP:
                mScrolling = false;
                mVelocityTracker.computeCurrentVelocity(1000);
                if(Math.abs(mVelocityTracker.getXVelocity())>miniVelocity){
                    startFilingX = x_scroll;
                    mScroller.fling(0,0, (int) mVelocityTracker.getXVelocity(),0, Integer.MIN_VALUE, Integer.MAX_VALUE,0,0);
                    invalidate();
                    Log.i(TAG, "onTouchEvent: filing");
                }else{
                    Log.i(TAG, "onTouchEvent: not filing");
                    regressPos();
                }

                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()&&mScroller.getCurrX()!=lastFillingX&&mScroller.getCurrX()!=0){
            x_scroll = startFilingX+mScroller.getCurrX();
            lastFillingX = mScroller.getCurrX();
            Log.i(TAG, "computeScroll: filling:"+mScroller.getCurrX());
            requestLayout();
        }else if((!mScrolling)&&(!mValueAnimator.isStarted())&&(x_scroll%getItemWidth()!=0)){
            regressPos();
            Log.i(TAG, "computeScroll: regress");
        }else{
            Log.i(TAG, "computeScroll: none");
        }

    }

    private void cancelAnimation(){
        mValueAnimator.cancel();
        mScroller.abortAnimation();
    }

    /**
     * 弹回到只显示三个的位置
     */
    private void regressPos(){
        int residue = x_scroll%getItemWidth();
        int animStep;
        if(residue>getItemWidth()/2){
            animStep = getItemWidth()-residue;
        }else{
            animStep = -residue;
        }
        if(mValueAnimator.isRunning()){
            mValueAnimator.cancel();
        }
        Log.i(TAG, "regressPos: x_scroll:"+x_scroll+",to:"+(x_scroll+animStep));
        mValueAnimator.setIntValues(x_scroll,x_scroll+animStep);
        mValueAnimator.setDuration(Math.abs(animStep)/3);
        mValueAnimator.start();
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener(){
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            x_scroll = (int) animation.getAnimatedValue();
            Log.i(TAG, "onAnimationUpdate: x_scroll:"+x_scroll);
            requestLayout();
        }
    };

    public void setAdapter(SeletcorAdapter adapter){
        this.adapter = adapter;
        requestLayout();
    }

    public void setOnItemCheckListener(OnItemCheckListener l){
        mOnItemCheckListener = l;
    }

    public abstract static class SeletcorAdapter{
        public abstract int getItemCount();
        public abstract void setView(View view, int position);
    }

    public interface OnItemCheckListener{
        void onItemChecked(int position);
        void onScrolled(int position);
    }

    public void left(){
        mValueAnimator.cancel();
        Log.i(TAG, "left: x_scroll:"+x_scroll+",max:"+ getMaxScrollRange());
        if(x_scroll< getMaxScrollRange()){
            int to = x_scroll+getItemWidth();
            if(to > getMaxScrollRange()){
                to = getMaxScrollRange();
            }
            Log.i(TAG, "left: from:"+x_scroll+",to:"+to);
            mValueAnimator.setIntValues(x_scroll,to);
            mValueAnimator.setDuration(Math.abs(getItemWidth())/3);
            mValueAnimator.start();
        }
    }

    public void right(){
        mValueAnimator.cancel();
        if(x_scroll>0){
            int to = x_scroll - getItemWidth();
            if(to<0){
                to = 0;
            }
            mValueAnimator.setIntValues(x_scroll,to);
            mValueAnimator.setDuration(Math.abs(getItemWidth())/3);
            mValueAnimator.start();
        }
    }
}
