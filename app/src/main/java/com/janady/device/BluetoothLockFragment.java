package com.janady.device;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.common.DialogInputPasswd;
import com.example.funsdkdemo.MyApplication;
import com.janady.HomeActivity;
import com.lkd.smartlocker.R;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.janady.Dialogs;
import com.janady.utils.Util;
import com.janady.common.JDialogModifyPasswd;
import com.janady.database.model.Bluetooth;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;
import com.janady.lkd.ClientManager;
import com.janady.setup.JBaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class BluetoothLockFragment extends JBaseFragment implements View.OnClickListener {
    private static final String MAC = "D0:D3:86:72:4D:42";
    private static final String BleService = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String BleNotifitesCharacter = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String BleWriteCharacter = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public String title = getString(R.string.MY_BT);
    public BleLocker bleLocker;
    public boolean isDebugViewOpen = false;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton searchBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton passwordBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton connectBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton disconnectBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton openBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton closeBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton lockBtn;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton stopBtn;
    private ImageButton mBtnBack;

    private ImageView imgLockStat;
    private TextView tvLockStat;

    private TextView mTextTitle;
    private TextView tvresult;

    private boolean isLocked = false;

    private String bleOldPsw = "LKD.CN";
    private boolean needCheckSTA = false;

    private int step = 0; //0-正常   1-输入密码

    private CountDownTimer countDownTimer;

    QMUITopBarLayout mTopBar;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jbluetooth_lock_layout, null);
        mTopBar = root.findViewById(R.id.topbar);
        initTopBar();
//        bleLocker = new BleLocker(MAC, false, BleService,
//                BleNotifitesCharacter, BleWriteCharacter, "123123",800, iBleLockerCallBack);

        bleLocker.setBleLockerCallBack(iBleLockerListener);

        openBtn = root.findViewById(R.id.open);
        openBtn.setOnClickListener(this);

        closeBtn = root.findViewById(R.id.close);
        closeBtn.setOnClickListener(this);

        lockBtn = root.findViewById(R.id.lock);
        lockBtn.setOnClickListener(this);

        stopBtn = root.findViewById(R.id.unlock);
        stopBtn.setOnClickListener(this);

        imgLockStat = root.findViewById(R.id.imgLockStat);
        tvLockStat = root.findViewById(R.id.tvLockStat);

        tvresult = root.findViewById(R.id.result);
        tvresult.setMovementMethod(ScrollingMovementMethod.getInstance());

        if(isDebugViewOpen){
            tvresult.setVisibility(View.VISIBLE);
        }else{
            tvresult.setVisibility(View.GONE);
        }

        connectBtn=root.findViewById(R.id.connect);
        connectBtn.setOnClickListener(this);
        connectBtn.setVisibility(View.GONE);

        disconnectBtn = root.findViewById(R.id.disconnect);
        disconnectBtn.setOnClickListener(this);
        disconnectBtn.setVisibility(View.GONE);

        searchBtn = root.findViewById(R.id.scan);
        searchBtn.setOnClickListener(this);
        searchBtn.setVisibility(View.GONE);

        passwordBtn = root.findViewById(R.id.password);
        passwordBtn.setOnClickListener(this);
        passwordBtn.setVisibility(View.GONE);

        mBtnBack = root.findViewById(R.id.backBtnInTopLayout);
        mBtnBack.setOnClickListener(this);

        mTextTitle = root.findViewById(R.id.textViewInTopLayout);
        mTextTitle.setText(title + " - "+getString(R.string.WAITING_CONN));

        mTopBar.setTitle(title  + " - "+getString(R.string.WAITING_CONN));

        showWaitDialog();
        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("BleLockFragment", "seconds remaining: " + millisUntilFinished / 1000);
                //setMsgText("正在连接蓝牙设备，请稍等..." + String.valueOf(millisUntilFinished / 1000) + "秒");

            }

            public void onFinish() {
                Dialogs.alertDialog2Btn(getContext(), getString(R.string.BT_DEV_CONN_FAILED), getString(R.string.DLG_DEVICE_RECONNECT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();
                        if (bleLocker != null) {
                            needCheckSTA = true;
                            bleLocker.connect();
                        }
                        countDownTimer.start();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideWaitDialog();
                        onBackPressed();
                    }
                });

                Log.i("BleLockFragment", "连接失败");
            }
        }.start();

        return root;
    }

    @Override
    public void onDestroyView() {
        if(bleLocker!=null){bleLocker.disconnect();}
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(bleLocker != null) {
            needCheckSTA = true;
            bleLocker.connect();
        }
    }

    @Override
    public void onStop() {
        if(bleLocker != null) {bleLocker.disconnect();}
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        popBackStack();

        Intent intent = new Intent();
        intent.putExtra("action","recreate");
        intent.setClass(getContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initTopBar() {
        mTopBar.setTitle(title);
        /*mTopBar.addRightImageButton(R.drawable.scan_capture, R.id.topbar_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startFragment(new JTabSegmentFragment());
            }
        });*/
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bleLocker!=null){
                    bleLocker.disconnect();
                }

                onBackPressed();
            }
        });
    }

    private boolean isScanning = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtnInTopLayout:
            {
                // 返回/退出
                getBaseFragmentActivity().popBackStack();
            }
            break;
            case R.id.open:
/*                if(isLocked){
                    Dialogs.alertMessage(this.getContext(),"提示", "操作失败，请先开锁！");
                }*/
                if(bleLocker!=null && bleLocker.getIsReday()) {
                    bleLocker.open();
                }
                break;
            case R.id.close:
                if(bleLocker!=null && bleLocker.getIsReday()) {
                    bleLocker.close();
                }
                break;
            case R.id.lock:
                if(bleLocker!=null && bleLocker.getIsReday()) {
                    bleLocker.lock();
                }
                break;
            case R.id.unlock:
                if(bleLocker!=null && bleLocker.getIsReday()) {
                    bleLocker.stop();
                }
                break;
            case R.id.scan:
                if(!isScanning) {
                    searchDevice();
                }else{
                    ClientManager.getClient().stopSearch();
                }
                break;
            case R.id.password:
                bleLocker.changePassword("123456");
                break;
            case R.id.connect:
                bleLocker.connect();
                break;
            case R.id.disconnect:
                bleLocker.disconnect();
                break;
        }
    }

    private void setLockedBtns(boolean isLocked){
        openBtn.setEnabled(!isLocked);
        closeBtn.setEnabled(!isLocked);
        lockBtn.setEnabled(!isLocked);
        if(isLocked) {
            openBtn.setTextColor(Color.GRAY);
            closeBtn.setTextColor(Color.GRAY);
            lockBtn.setTextColor(Color.GRAY);
            stopBtn.setText(R.string.BTN_UNLOCK);
        }else{
            openBtn.setTextColor(stopBtn.getTextColors());
            closeBtn.setTextColor(stopBtn.getTextColors());
            lockBtn.setTextColor(stopBtn.getTextColors());
            stopBtn.setText(R.string.BTN_STOP);
        }

    }

    /**
     * -----------------
     * 搜索蓝牙设备
     */
    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 2).build();

        ClientManager.getClient().search(request, mSearchResponse);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {

            searchBtn.setText(R.string.STOP_SCAN_DEVICE);
            isScanning = true;

       }

        @Override
        public void onDeviceFounded(SearchResult device) {
//            if (!mDevices.contains(device)) {
//                mDevices.add(device);
//                mAdapter.setDataList(mDevices);
//
//            if (mDevices.size() > 0) {
//                mRefreshLayout.showState(AppConstants.LIST);
//            }
        }

        @Override
        public void onSearchStopped() {

            isScanning = true;
            searchBtn.setText(R.string.SCAN_DEVICE);
            //toolbar.setTitle(R.string.devices);
        }

        @Override
        public void onSearchCanceled() {

            searchBtn.setText(R.string.SCAN_DEVICE);
            //toolbar.setTitle(R.string.devices);
        }
    };

    private BleLocker.IBleLockerListener iBleLockerListener = new BleLocker.IBleLockerListener() {
        @Override
        public void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onPasswordChanged：" + status.getmStatusMsg());}
        }

        @Override
        public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onClose：" + status.getmStatusMsg());}
            mTextTitle.setText(title + " - "+getString(R.string.CLOSE_OK));
            mTopBar.setTitle(title + " - "+getString(R.string.CLOSE_OK));

        }

        @Override
        public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onStop：" + status.getmStatusMsg());}
            mTextTitle.setText(title + " - "+getString(R.string.STOP_OK));
            mTopBar.setTitle(title + " - "+getString(R.string.STOP_OK));
        }

        @Override
        public void onLock(Bluetooth bluetooth, BleLockerStatus status) {
            if(status==BleLockerStatus.LOCKED){
                isLocked = true;
                imgLockStat.setImageResource(R.drawable.ic_locked);
                tvLockStat.setText(R.string.LOCKED);
                mTextTitle.setText(title + " - "+getString(R.string.LOCKED));
                mTopBar.setTitle(title + " - "+getString(R.string.LOCKED));
            }else{
                imgLockStat.setImageResource(R.drawable.ic_unlocked);
                tvLockStat.setText(R.string.UNLOCKED);
                isLocked = false;
            }

            setLockedBtns(isLocked);
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onLock：" + status.getmStatusMsg());}
        }

        @Override
        public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "..\n   onOpen：" + status.getmStatusMsg());}
            mTextTitle.setText(title + " - "+getString(R.string.OPEN_OK));
            mTopBar.setTitle(title + " - "+getString(R.string.OPEN_OK));
        }

        @Override
        public void onBleReadResponse(Bluetooth bluetooth,  byte[] data, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onBleReadResponse：" + String.format("read: %s", ByteUtils.byteToString(data))+"\n"
                    +"\n Status:"+ status.getmStatusMsg());}
        }

        @Override
        public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onBleWriteResponse：" + status.getmStatusMsg());}
        }

        @Override
        public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onNotifyResponse：" +  NotifyValue
                    +"\n  Status: " + status.getmStatusMsg());}

            if(status == BleLockerStatus.REDAY){
                hideWaitDialog();
                mTextTitle.setText(title + " - "+getString(R.string.CONNECTED));
                mTopBar.setTitle(title + " - "+getString(R.string.CONNECTED));

                if(countDownTimer!=null){countDownTimer.cancel();}
            }
        }

        @Override
        public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {
            mTextTitle.setText(title+" - "+getString(R.string.CONNECTIONNING));
            mTopBar.setTitle(title + " - "+getString(R.string.CONNECTIONNING));
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onConnected：" + status.getmStatusMsg());}
            if(status==BleLockerStatus.CONNECTED && needCheckSTA) {
                bleLocker.sta();
            }
        }

        @Override
        public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onDisconnected：" + status.getmStatusMsg());}
            popBackStack();
        }

        @Override
        public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

        }

        @Override
        public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
            hideWaitDialog();
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onReday：" + status.getmStatusMsg());}

        }

        @Override
        public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onGetRssi：" + status.getmStatusMsg());}
        }
        @Override
        public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
            hideWaitDialog();
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onPasswdError：" + status.getmStatusMsg());}
            if (step == 1) {
                showInputPasswordDialog();
            }
            step = 0;
        }

        private void alertDialog(String text, DialogInterface.OnClickListener onClickListener, DialogInterface.OnCancelListener onCancelListener){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(text);
            builder.setPositiveButton(getString(R.string.common_confirm), onClickListener);
            builder.setOnCancelListener(onCancelListener);
            builder.show();
        }
        @Override
        public void onResetted(Bluetooth bluetooth, int Resetted, BleLockerStatus status) {
            if(isDebugViewOpen){Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onResetted：" + Resetted
                    +"\n  status:" + status.getmStatusMsg());}
            needCheckSTA=false;
            if(Resetted ==1 ) {
                BluetoothLog.i(" 设备已重置，onResetted：code=" + status.getSatusId() + " message=" + status.getmStatusMsg() + "\n");
                Dialogs.alertDialogBtn(getContext(), getString(R.string.DLG_TIPS_TITLE), getString(R.string.BT_DEVICE_RESETED), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bleOldPsw = "LKD.CN";
                        bleLocker = new BleLocker(bleLocker.getmBluetooth(), false, 800, iBleLockerListener);
                        bleLocker.setmPassword(bleOldPsw);
                        bleLocker.setmNoRssi(true);
                        bleLocker.connect();
                        showInputNewPasswordDialog();
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //popBackStack();
                    }
                });
            }else{
                step=0;
                BluetoothLog.i(" 设备正常，onResetted：code=" + status.getSatusId() + " message=" + status.getmStatusMsg() + "\n");
            }
        }
    };

    /**
     * 显示输入设备密码对话框
     */
    private void showInputNewPasswordDialog() {
        JDialogModifyPasswd inputDialog = new JDialogModifyPasswd(getContext(),
                getContext().getResources().getString(R.string.device_login_input_password),
                "","","",
                R.string.common_confirm,
                R.string.common_cancel,
                true
        ){
            @Override
            public boolean confirm(String oldPasswd, String newPasswd, String confirmPasswd) {

                if(confirmPasswd.equals(newPasswd)){
                    while (!bleLocker.getIsReday()) {
                    }
                    bleLocker.changePassword(newPasswd);
                    bleLocker.getmBluetooth().password = newPasswd;
                    MyApplication.liteOrm.save(bleLocker.getmBluetooth());

                    tvresult.setText("");
                    bleLocker = new BleLocker(bleLocker.getmBluetooth(), false, 800, iBleLockerListener);
                    bleLocker.setmPassword(newPasswd);
                    bleLocker.setmNoRssi(true);
                    bleLocker.connect();

                    MyApplication.liteOrm.save(bleLocker.getmBluetooth());

                    showWaitDialog();

                }else{
                    this.mMessages.setText(R.string.DLG_PASSWD_ERR_2);
                    this.mMessages.setVisibility(View.VISIBLE);
                    return false;
                }

                return super.confirm(oldPasswd,newPasswd,confirmPasswd);

            }

            @Override
            public void cancel() {
                super.cancel();
                if(bleLocker!=null){bleLocker.disconnect();}
                popBackStack();
                // 取消输入密码,直接退出
            }

        };

        inputDialog.show();
    }

    /**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog() {
        DialogInputPasswd inputDialog = new DialogInputPasswd(getContext(),
                getResources().getString(R.string.device_login_input_password),
                "",
                R.string.common_confirm,
                R.string.common_cancel
        ){

            @Override
            public boolean confirm(String editText) {
                    step = 1;
                    needCheckSTA = false;
                    if(bleLocker.getmBluetooth()!=null){
                        //bleOldPsw = mBluetooth.password;
                        //if(editText.equals(bleOldPsw)) {
                        bleOldPsw = editText;
                        bleLocker.getmBluetooth().password = editText;
                        bleLocker.getmBluetooth().isFirst = false;

                        bleLocker = new BleLocker(bleLocker.getmBluetooth(), false, 800, iBleLockerListener);
                        bleLocker.setmNoRssi(true);
                        bleLocker.connect();

                        MyApplication.liteOrm.save(bleLocker.getmBluetooth());

                        showWaitDialog();
                        super.hide();
                    }
                return super.confirm(editText);
            }

            @Override
            public void cancel() {
                super.cancel();

                // 取消输入密码,直接退出
                popBackStack();
            }

        };

        inputDialog.show();
    }
}
