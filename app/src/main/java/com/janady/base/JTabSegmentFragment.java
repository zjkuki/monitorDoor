package com.janady.base;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.funsdkdemo.R;
import com.janady.device.AddDeviceFragment;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.DeviceAddByUser;
import com.janady.device.DeviceCameraFragment;
import com.janady.device.DeviceLanFragment;
import com.janady.device.RemoteEditFragment;
import com.janady.device.WifiConfigFragment;
import com.janady.manager.DataManager;
import com.janady.model.CategoryItemDescription;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JTabSegmentFragment extends JBaseFragment {
    QMUITopBarLayout mTopBar;
    QMUITabSegment mTabSegment;
    ViewPager mContentViewPager;

    private Map<ContentPage, View> mPageMap = new HashMap<>();
    private ContentPage mDestPage = ContentPage.Item1;

    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return ContentPage.SIZE;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            ContentPage page = ContentPage.getPage(position);
            View view = getPageView(page);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    };

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle("添加设备");
    }
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.jfragment_tab_viewpager_layout, null);
        mTopBar = rootView.findViewById(R.id.topbar);
        mTabSegment = rootView.findViewById(R.id.tabSegment);
        mContentViewPager = rootView.findViewById(R.id.contentViewPager);
        initTopBar();
        initTabAndPager();

        return rootView;
    }

    private void initTabAndPager() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        mTabSegment.addTab(new QMUITabSegment.Tab("设备类型"));
        mTabSegment.addTab(new QMUITabSegment.Tab("WIFI配置"));
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(true);
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onDoubleTap(int index) {

            }
        });
    }

    private View getPageView(ContentPage page) {
        View view = mPageMap.get(page);
        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.jdevice_discover_layout, null);

            if (page == ContentPage.Item1) {
                QMUIGroupListView mGroupListView = view.findViewById(R.id.groupListView);
                int size = QMUIDisplayHelper.dp2px(getContext(), 20);
                QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                        //.setTitle("Section 1: 默认提供的样式")
                        //.setDescription("Section 1 的描述")
                        .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
                List<CategoryItemDescription> list = DataManager.getInstance().showCategoryDesciptions();
                for (final CategoryItemDescription item : list) {
                    QMUICommonListItemView normalItem = mGroupListView.createItemView(
                            ContextCompat.getDrawable(getContext(), item.getIconRes()),
                            item.getName(),
                            null,
                            QMUICommonListItemView.HORIZONTAL,
                            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
                    normalItem.setOrientation(QMUICommonListItemView.VERTICAL);
                    section.addItemView(normalItem, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int dts = 1;
                            Intent intent = new Intent();

                            if(item.getDemoClass() == DeviceCameraFragment.class) {
                                intent.putExtra("DeviceTypsSpinnerNo", 0);
                            }

                            if(item.getDemoClass() == BluetoothEditFragment.class) {
                                intent.putExtra("DeviceTypsSpinnerNo", 1);
                            }

                            if(item.getDemoClass() == RemoteEditFragment.class) {
                                intent.putExtra("DeviceTypsSpinnerNo", 2);
                            }

                            intent.setClass(getContext(), DeviceAddByUser.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            /*}else {
                                JBaseFragment fragment = null;
                                try {
                                    fragment = item.getDemoClass().newInstance();
                                    startFragment(fragment);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (java.lang.InstantiationException e) {
                                    e.printStackTrace();
                                }
                            }*/
                        }
                    });
                }

                section.addTo(mGroupListView);
            } else if (page == ContentPage.Item2) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.jadd_device_inpage, null);
                
                Button fastBtn = view.findViewById(R.id.fast_config_button);
                fastBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFragment(new WifiConfigFragment());
                    }
                });
                Button lanBtn = view.findViewById(R.id.lan_device);
                lanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFragment(new DeviceLanFragment());
                    }
                });
            }
            mPageMap.put(page, view);
        }
        return view;
    }
    public enum ContentPage {
        Item1(0),
        Item2(1);
        public static final int SIZE = 2;
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return Item1;
                case 1:
                    return Item2;
                default:
                    return Item1;
            }
        }

        public int getPosition() {
            return position;
        }
    }
}
