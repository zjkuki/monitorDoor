package com.janady;

import android.widget.Toast;

import com.example.funsdkdemo.MyApplication;
import com.lkd.smartlocker.R;


/**
 * Created by jwcui on 2016/6/25.
 */
public final class NetworkUtils {
    private NetworkUtils(){}

    public static boolean checkNetwork(){
        if(!MyApplication.networkConnected){
            Toast.makeText(MyApplication.context, R.string.network_unconnected, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
