package com.janady;

import com.TestFragment;
import com.lkd.smartlocker.R;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;

@DefaultFirstFragment(value = TestFragment.class)
//@DefaultFirstFragment(value = HomeFragment.class)
public class HomeActivity extends QMUIFragmentActivity {
    @Override
    protected int getContextViewId() {
        AppManager.getAppManager().addActivity(this);
        return R.id.home_page;
    }


}
