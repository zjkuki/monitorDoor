package com.janady.device;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.R;
import com.janady.base.JBaseGroupedListFragment;
import com.janady.database.model.WifiRemoter;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;

public class RemoteListFragment_old extends JBaseGroupedListFragment {
    @Override
    protected String title() {
        return "远程控制";
    }

    @Override
    protected void initGroupListView() {
        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                .setTitle("已经添加的远程控制设备")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<WifiRemoter> blists = MyApplication.liteOrm.query(WifiRemoter.class);
        for (final WifiRemoter remote : blists) {
            QMUICommonListItemView itemView = mGroupListView.createItemView(
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_bluetooth),
                    remote.name,
                    "门数"+(remote.doorList != null ? remote.doorList.size() : 0),
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
            section.addItemView(itemView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RemoteEditFragment fragment = new RemoteEditFragment();
                    fragment.setRemote(remote);
                    startFragment(fragment);
                }
            });
        }
        if (blists.size() > 0) section.addTo(mGroupListView);

        QMUIGroupListView.Section action = QMUIGroupListView.newSection(getContext());
        QMUICommonListItemView itemView = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.drawable.socket_task_add_normal),
                "新增远程控制板",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        action.addItemView(itemView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteEditFragment fragment = new RemoteEditFragment();
                startFragment(fragment);
            }
        });
        action.addTo(mGroupListView);
    }
}
