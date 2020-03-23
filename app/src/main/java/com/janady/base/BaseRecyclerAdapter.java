package com.janady.base;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.UIFactory;
import com.lkd.smartlocker.R;
import com.janady.RoundRect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cginechen
 * @date 2016-10-19
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<T> mData;
    private final Context mContext;
    private LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    private Bitmap mQrCodeBmp;

    public BaseRecyclerAdapter(Context ctx, List<T> list) {
        mData = (list != null) ? list : new ArrayList<T>();
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
    }

    public void setData(List<T> list) {
        mData = (list != null) ? list : new ArrayList<T>();
        notifyDataSetChanged();
    }
    protected Bitmap makeQRCode(String content){
        // 生成二维码
        Bitmap qrCodeBmp = UIFactory.createCode(
                content, 800, 0xff202020);
        if ( null != qrCodeBmp ) {
            if ( null !=  mQrCodeBmp ) {
                mQrCodeBmp.recycle();
            }
            mQrCodeBmp = qrCodeBmp;
            return qrCodeBmp;
        }else{
            return  null;
        }
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerViewHolder holder = new RecyclerViewHolder(mContext,
                mInflater.inflate(getItemLayoutId(viewType), parent, false));
        RoundRect roundRect = new RoundRect(50,50,100);
        Bitmap photo = roundRect.toRoundRect(mContext, R.drawable.ic_locker);


        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return true;
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        bindData(holder, position, mData.get(position));
    }

    public T getItem(int pos){
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(int pos, T item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void delete(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    @SuppressWarnings("SameReturnValue")
    abstract public int getItemLayoutId(int viewType);

    abstract public void bindData(RecyclerViewHolder holder, int position, T item);

    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int pos);
    }
}