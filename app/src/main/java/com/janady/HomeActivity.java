package com.janady;

import android.os.Bundle;

import com.TestFragment;
import com.lkd.smartlocker.R;
import com.janady.home.HomeFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;

@DefaultFirstFragment(value = TestFragment.class)
public class HomeActivity extends QMUIFragmentActivity {
    @Override
    protected int getContextViewId() {
        AppManager.getAppManager().addActivity(this);
        return R.id.home_page;
    }


}
