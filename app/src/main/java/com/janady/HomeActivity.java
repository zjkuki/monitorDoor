package com.janady;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.TestFragment;
import com.janady.base.JBaseEditFragment;
import com.janady.setup.JBaseFragment;
import com.lkd.smartlocker.R;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import java.util.List;

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
        if (AppManager.getAppManager() != null) {
            if (AppManager.getAppManager().getActivitys() == null) {
                AppManager.getAppManager().addActivity(this);
            } else {
                AppManager.getAppManager().getActivity(HomeActivity.class);
            }
        }

        List<Fragment> fms = getSupportFragmentManager().getFragments();
        if (fms != null) {
            if (fms.size() == 0) {
                //getSupportFragmentManager().beginTransaction().add(R.id.home_page, new TestFragment(), "testFragment").commit();
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).add(R.id.home_page, new TestFragment(), "testFragment").commit();
            } else {
                /*for (Fragment fragment : fms) {
                    if ((JBaseFragment) fragment instanceof TestFragment) {
                        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        trans.show(fragment);
                        trans.addToBackStack(null);
                        trans.commit();
                    }
                }*/
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).show(getSupportFragmentManager().findFragmentByTag("testFragment")).commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).add(R.id.home_page, new TestFragment(), "testFragment").commit();
        }
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
