package com.janady;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.TestFragment;
import com.janady.setup.JBaseFragment;
import com.lkd.smartlocker.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;

//@DefaultFirstFragment(value = TestFragment.class)
//@DefaultFirstFragment(value = HomeFragment.class)
public class HomeActivity extends QMUIFragmentActivity {



    @Override
    protected int getContextViewId() {
        return R.id.home_page;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        getSupportFragmentManager().beginTransaction().add(R.id.home_page,new TestFragment(),"testFragment").commit();
        //Log.i("HOME", getCurrentFragment().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.getIntent()!=null) {
            String s=this.getIntent().getStringExtra("action");
            if (s!=null && s.equals("recreate")) {
                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("testFragment")).commit();
            }
        }
        //Log.i("HOME", getCurrentFragment().toString());
    }
}
