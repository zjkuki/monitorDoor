package com.janady.device;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.funsdkdemo.R;
import com.janady.Dialogs;
import com.janady.setup.JBaseFragment;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.FunWifiPassword;
import com.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.DeviceWifiManager;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.funsdk.support.utils.StringUtils;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;

public class WifiConfigFragment extends JBaseFragment implements OnFunDeviceWiFiConfigListener {
    private QMUITopBarLayout mTopBar;
    private Button okBtn;
    private EditText mEditWifiSSID = null;
    private EditText mEditWifiPasswd = null;
    private RadioGroup rbgWifiDeviceGroup = null;
    private RadioButton rbWifiCam = null;
    private RadioButton rbWifiRemoter = null;
    private TextView tvWifiReadMe = null;
    private TextView tvTips = null;

    private int mWifiDevice = 0;  //0-摄像头 1-控制板
    private EasyLink el;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jconf_wifi, null);
        mTopBar = root.findViewById(R.id.topbar);
        mEditWifiSSID = root.findViewById(R.id.editWifiSSID);
        mEditWifiPasswd = root.findViewById(R.id.editWifiPasswd);
        rbgWifiDeviceGroup = root.findViewById(R.id.rbg_wifiDeviceGroup);

        rbWifiCam = root.findViewById(R.id.rbWifiCam);
        rbWifiCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiDevice = 0;
            }
        });

        rbWifiRemoter = root.findViewById(R.id.rbWifiRemoter);
        rbWifiRemoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiDevice = 1;
            }
        });

        tvWifiReadMe = root.findViewById(R.id.tvWifiReadMe);

        tvTips = root.findViewById(R.id.tvTips);
        tvTips.setVisibility(View.GONE);

        String currSSID = getConnectWifiSSID();
        mEditWifiSSID.setText(currSSID);
        mEditWifiPasswd.setText(FunWifiPassword.getInstance().getPassword(currSSID));

        tvWifiReadMe.setText("*请选择较强信号的WIFI，正确填写WIFI密码，可快速让设备联网；");

        okBtn = root.findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickSetting();
            }
        });
        initTopBar();
        FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(this);

        el = new EasyLink(getContext());

        return root;
    }

    @Override
    public void onDestroy() {
        stopQuickSetting();
        FunSupport.getInstance().removeOnFunDeviceWiFiConfigListener(this);

        super.onDestroy();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.guide_module_title_device_setwifi);
    }
    private String getConnectWifiSSID() {
        try {
            WifiManager wifimanage=(WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifimanage.getConnectionInfo().getSSID().replace("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // 开始快速配置
    private void startQuickSetting() {

        try {
            WifiManager wifiManage = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManage.getConnectionInfo();
            DhcpInfo wifiDhcp = wifiManage.getDhcpInfo();

            if ( null == wifiInfo ) {
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            String ssid = wifiInfo.getSSID().replace("\"", "");
            if ( StringUtils.isStringNULL(ssid) ) {
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            ScanResult scanResult = DeviceWifiManager.getInstance(getContext()).getCurScanResult(ssid);
            if ( null == scanResult ) {
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            int pwdType = MyUtils.getEncrypPasswordType(scanResult.capabilities);
            String wifiPwd = mEditWifiPasswd.getText().toString().trim();

            if ( pwdType != 0 && StringUtils.isStringNULL(wifiPwd) ) {
                // 需要密码
                showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            StringBuffer data = new StringBuffer();
            data.append("S:").append(ssid).append("P:").append(wifiPwd).append("T:").append(pwdType);

            String submask;
            if (wifiDhcp.netmask == 0) {
                submask = "255.255.255.0";
            } else {
                submask = MyUtils.formatIpAddress(wifiDhcp.netmask);
            }

            String mac = wifiInfo.getMacAddress();
            StringBuffer info = new StringBuffer();
            info.append("gateway:").append(MyUtils.formatIpAddress(wifiDhcp.gateway)).append(" ip:")
                    .append(MyUtils.formatIpAddress(wifiDhcp.ipAddress)).append(" submask:").append(submask)
                    .append(" dns1:").append(MyUtils.formatIpAddress(wifiDhcp.dns1)).append(" dns2:")
                    .append(MyUtils.formatIpAddress(wifiDhcp.dns2)).append(" mac:").append(mac)
                    .append(" ");

            showWaitDialog();

            if(mWifiDevice == 0) {
                FunSupport.getInstance().startWiFiQuickConfig(ssid,
                        data.toString(), info.toString(),
                        MyUtils.formatIpAddress(wifiDhcp.gateway),
                        pwdType, 0, mac, -1);

                FunWifiPassword.getInstance().saveWifiPassword(ssid, wifiPwd);
            }else {
                EasyLinkParams elp = new EasyLinkParams();
                elp.ssid = ssid;
                elp.password = wifiPwd;
                el.startEasyLink(elp, new EasyLinkCallBack() {
                    @Override
                    public void onSuccess(int code, String message) {
                        showToast(String.format(
                                getResources().getString(R.string.device_opt_set_wifi_success),
                                "wifi remoter ok!"));
                        Intent intent = new Intent();
                        intent.putExtra("DeviceTypsSpinnerNo",2);
                        intent.setClass(getContext(), DeviceAddByUser.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        popBackStack();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Dialogs.alertMessage(getContext(), "WIFI配网失败",message);
                    }
                });
            }
            new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    Log.i("WifiConfigFragment", "seconds remaining: " + millisUntilFinished / 1000);
                    setMsgText("正在进行WIFI配置，请稍候...."+String.valueOf(millisUntilFinished / 1000)+"秒");
                }

                public void onFinish() {
                    FunSupport.getInstance().stopWiFiQuickConfig();
                    hideWaitDialog();
                    Dialogs.alertMessage(getContext(),"WIFI配置失败","设备配置WIFI超时，请检查WIFI或设备是否正常开启，请根据使用说明进行操作，使设备进入WIFI配网模式后重试。");
                    Log.i("WifiConfigFragment", "done!");
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopQuickSetting() {
        if(mWifiDevice == 0) {
            FunSupport.getInstance().stopWiFiQuickConfig();
        }else{
            hideWaitDialog();
            el.stopEasyLink(new EasyLinkCallBack() {
                @Override
                public void onSuccess(int code, String message) {
                    //Dialogs.alertMessage(getContext(), "WIFI配网已停止",message);
                }

                @Override
                public void onFailure(int code, String message) {
                    //Dialogs.alertMessage(getContext(), "WIFI配网失败",message);
                }
            });
        }

    }

    @Override
    public void onDeviceWiFiConfigSetted(FunDevice funDevice) {
        hideWaitDialog();
        if ( null != funDevice ) {
            showToast(String.format(
                    getResources().getString(R.string.device_opt_set_wifi_success),
                    funDevice.getDevSn()));
            Intent intent = new Intent();
            intent.putExtra("DeviceTypsSpinnerNo",0);
            intent.setClass(getContext(), DeviceAddByUser.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            popBackStack();
        }
    }
}
