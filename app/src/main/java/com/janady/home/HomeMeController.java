package com.janady.home;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.janady.adapter.BaseItemAdapter;
import com.janady.base.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeMeController extends HomeController<String> {
    public HomeMeController(Context context) {
        super(context);
    }

    @Override
    protected void initTopBar() {
        mTopBar.setTitle("me");
    }

    @Override
    protected BaseRecyclerAdapter<String> getItemAdapter() {
        List<String> list = new ArrayList<>();
        return new BaseItemAdapter(getContext(), list);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getContext(), 1);
    }

    @Override
    protected void onItemClicked(int pos) {

    }
}
