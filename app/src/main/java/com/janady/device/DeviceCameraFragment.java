package com.janady.device;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.common.DialogInputPasswd;
import com.example.common.UIFactory;
import com.example.funsdkdemo.R;
import com.janady.setup.JBaseFragment;
import com.janady.view.TestView;
import com.lib.EPTZCMD;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceCaptureListener;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.OPPTZPreset;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunStreamType;
import com.lib.funsdk.support.utils.TalkManager;
import com.lib.funsdk.support.widget.FunVideoView;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragmentDelegate;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class DeviceCameraFragment extends JBaseFragment
        implements OnFunDeviceOptListener,
        OnFunDeviceCaptureListener,
        FunVideoView.GestureListner,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, View.OnClickListener {

    private static final String TAG = "DeviceCameraFragment";
    private static final String ARG_MENU = "arg_menu";

    private QMUITopBarLayout mTopBar;
    private QMUITopBarLayout mLayoutTop;

    private FunDevice mFunDevice;
    private FunVideoView mFunVideoView;

    private TextView mTextVideoStat = null;

    private Button mBtnScreenRatio = null;
    private Button mBtnVoice = null;
    private ImageButton mBtnQuitVoice = null;
    private ImageButton mBtnDevCapture = null;
    private ImageButton mBtnDevRecord = null;

    private LinearLayout mVideoControlLayout;
    private LinearLayout mLayoutControls;

    private RelativeLayout mBtnVoiceTalk = null;
    private RelativeLayout mLayoutVideoWnd;

    private final int MESSAGE_PLAY_MEDIA = 0x100;
    private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x102;
    private final int MESSAGE_TOAST_SCREENSHOT_PREVIEW = 0x103;
    private final int MESSAGE_OPEN_VOICE = 0x104;

    // 自动隐藏底部的操作控制按钮栏的时间
    private final int AUTO_HIDE_CONTROL_BAR_DURATION = 10000;

    private TalkManager mTalkManager = null;
    private boolean mCanToPlay = false;

    private boolean mIsDoubleTalkPress;

    private String mMenu;

    // region 重力感应

    protected boolean isLandscape = false;      // 默认是竖屏

    protected SensorManager sm;
    protected OrientationSensorListener listener; // 重力感应监听
    protected Sensor sensor;

    protected ContentObserver mSettingsContentObserver;    // 监控方向锁定

    // endregion

    public static DeviceCameraFragment newInstance(String menu) {

        Bundle args = new Bundle();
        args.putString(ARG_MENU, menu);

        DeviceCameraFragment fragment = new DeviceCameraFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public void setFunDevice(FunDevice mFunDevice) {
        this.mFunDevice = mFunDevice;
    }

    private float gestureX = 0;
    private float gestureY = 0;
    private int gestureMoveLen = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mMenu = args.getString(ARG_MENU);
        }
    }

    @Override
    protected void onBackPressed() {
        //super.onBackPressed();
        // 如果当前是横屏，返回时先回到竖屏
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }

        popBackStack();
    }


    @Override
    protected View onCreateView() {
        Log.d("zyk", " >> onCreateView!");
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.jdevice_camera_layout, null);
        mTopBar = root.findViewById(R.id.topbar);
        mFunVideoView = root.findViewById(R.id.funVideoView);
        mFunVideoView.setGestureListner(this);
        mFunVideoView.setOnPreparedListener(this);
        mFunVideoView.setOnErrorListener(this);
        mFunVideoView.setOnInfoListener(this);
        mTextVideoStat = (TextView) root.findViewById(R.id.textVideoStat);

        mBtnScreenRatio = (Button) root.findViewById(R.id.btnScreenRatio);
        mBtnScreenRatio.setOnClickListener(this);

        mLayoutVideoWnd = (RelativeLayout) root.findViewById(R.id.layoutPlayWnd);
        mLayoutTop = root.findViewById(R.id.topbar);
        mLayoutControls = (LinearLayout) root.findViewById(R.id.layoutFunctionControl);
        mVideoControlLayout = (LinearLayout) root.findViewById(R.id.layoutVideoControl);
        initTopBar();
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);
        initVideoView();
        initSensor();

        showVideoControlBar();

        mTalkManager = new TalkManager(mFunDevice.getDevSn(), new TalkManager.OnTalkButtonListener() {
            @Override
            public boolean isPressed() {
                return false;
            }

            @Override
            public void onUpdateUI() {

            }

            @Override
            public void OnCreateLinkResult(int Result) {

            }

            @Override
            public void OnCloseTalkResult(int Result) {

            }

            @Override
            public void OnVoiceOperateResult(int Type, int result) {

            }
        });

        return root;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);

        // 关闭重力感应
        sm.unregisterListener(listener);
        // 关闭监控手机方向锁定
        getActivity().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    private void initTopBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        mTopBar.removeCenterViewAndTitleView();
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(mFunDevice == null ? "" : mFunDevice.getDevName());
    }


    private void initSensor(){

        // region 重力感应

        // 初始化重力感应器
        sm = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        listener = new OrientationSensorListener(mHandler, getActivity());

        // 设置事件
        mSettingsContentObserver = new ContentObserver(new Handler()) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                // 启动重力感应
                if (Settings.System.getInt(getContext().getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                    // 1为自动旋转模式，0为锁定竖屏模式
                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                } else {
                    sm.unregisterListener(listener);
                }
            }

        };
        getActivity().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true,
                mSettingsContentObserver);

        // endregion
    }

    private void initVideoView() {
        if (mFunDevice == null) return;
        if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
            FunSupport.getInstance().requestDeviceLogin(mFunDevice);
        } else {
            FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);
        }
    }

    private void requestSystemInfo(FunDevice funDevice) {
        FunSupport.getInstance().requestDeviceConfig(funDevice, SystemInfo.CONFIG_NAME);
    }

    public void onDeviceSaveNativePws(FunDevice funDevice, String password) {
        FunDevicePassword.getInstance().saveDevicePassword(funDevice.getDevSn(),
                password);
        // 库函数方式本地保存密码
        if (FunSupport.getInstance().getSaveNativePassword()) {
            FunSDK.DevSetLocalPwd(funDevice.getDevSn(), "admin", password);
            // 如果设置了使用本地保存密码，则将密码保存到本地文件
        }
    }
    /**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog(final FunDevice funDevice) {
        DialogInputPasswd inputDialog = new DialogInputPasswd(getContext(),
                getResources().getString(R.string.device_login_input_password), "", R.string.common_confirm,
                R.string.common_cancel) {

            @Override
            public boolean confirm(String editText) {
                // 重新以新的密码登录
                if (null != funDevice) {
                    onDeviceSaveNativePws(funDevice, editText);

                    // 重新登录
                    FunSupport.getInstance().requestDeviceLogin(funDevice);
                }
                return super.confirm(editText);
            }

            @Override
            public void cancel() {
                super.cancel();

                // 取消输入密码,直接退出

            }

        };

        inputDialog.show();
    }

    private void playRealMedia() {
        Log.d("zyk", " >> playRealMedia");
        mTextVideoStat.setText(R.string.media_player_opening);
        mTextVideoStat.setVisibility(View.VISIBLE);
        if (mFunDevice.isRemote) {
            mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
        } else {
            String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
            mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
        }

        // 打开声音
        mFunVideoView.setMediaSound(true);
    }

    private void onContrlPTZ1(int nPTZCommand, boolean bStop) {
        Log.d("zyk", " >> onContrlPTZ1-"+nPTZCommand);
        FunSupport.getInstance().requestDevicePTZControl(mFunDevice,
                nPTZCommand, bStop, mFunDevice.CurrChannel);
    }
    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {
        if (funDevice!=null && mFunDevice!=null && funDevice.getId()==mFunDevice.getId()) requestSystemInfo(mFunDevice);
    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
        Log.d("zyk", funDevice.getDevName() + " >> login failed:" + errCode);
        if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
            showInputPasswordDialog(funDevice);
        }
    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
        Log.d("zyk", funDevice.getDevName() + " >> Config success!");
        if (SystemInfo.CONFIG_NAME.equals(configName)) {
            if (funDevice.channel == null) {
                FunSupport.getInstance().requestGetDevChnName(funDevice);
                requestSystemInfo(funDevice);
                return;
            }
            if (mFunVideoView != null && funDevice != null && funDevice.getId() == mFunDevice.getId()) playRealMedia();
            if (funDevice.isSupportPTZ()) {
                //requestPTZPreset();
            }
        }
    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

        if (errCode == -11406) {
            funDevice.invalidConfig(OPPTZPreset.CONFIG_NAME);
        }
    }

    @Override
    public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

    }

    @Override
    public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {

    }

    @Override
    public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

    }

    @Override
    public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

    }

    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {

    }

    @Override
    public void onGestureRight() {
        onContrlPTZ1(EPTZCMD.PAN_RIGHT, false);
    }

    @Override
    public void onGestureLeft() {
        onContrlPTZ1(EPTZCMD.PAN_LEFT, false);
    }

    @Override
    public void onGestureUp() {
        onContrlPTZ1(EPTZCMD.TILT_UP, false);
    }

    @Override
    public void onGestureDown() {
        onContrlPTZ1(EPTZCMD.TILT_DOWN, false);
    }@Override
    public void onGestureStop() {
        onContrlPTZ1(EPTZCMD.TILT_DOWN, true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        showToast(getResources().getString(R.string.media_play_error)
                + " : "
                + FunError.getErrorStr(extra));

        if ( FunError.EE_TPS_NOT_SUP_MAIN == extra
                || FunError.EE_DSS_NOT_SUP_MAIN == extra ) {
            // 不支持高清码流,设置为标清码流重新播放
            if (null != mFunVideoView) {
                mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
                playRealMedia();
            }
        }
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mTextVideoStat.setText(R.string.media_player_buffering);
            mTextVideoStat.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            mTextVideoStat.setVisibility(View.GONE);
            tryToCapture();
        }
        return true;
    }
    /**
     * 视频截图,并延时一会提示截图对话框
     */
    private void tryToCapture() {
        Log.d("zyk", "tryToCapture: "+mFunVideoView.isPlaying());
        if (!mFunVideoView.isPlaying()) {
            showToast(R.string.media_capture_failure_need_playing);
            return;
        }

        final String path = mFunVideoView.captureCover(mFunDevice.getDevSn());
    }
    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCaptureSuccess(String picStr) {
        Log.d("zyk", "onCaptureSuccess:" + picStr);
    }

    @Override
    public void onCaptureFailed(int ErrorCode) {
        Log.d("zyk", "onCaptureFailed");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 检测屏幕的方向：纵向或横向
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏， 在此处添加额外的处理代码
            showAsLandscape();
        }
        else if(getResources().getConfiguration().orientation
                ==Configuration.ORIENTATION_PORTRAIT) {
            // 当前为竖屏， 在此处添加额外的处理代码
            showAsPortrait();
        }

        super.onConfigurationChanged(newConfig);
    }


    // region 重力感应相关方法

    /**
     * 接收重力感应监听的结果，来改变屏幕朝向
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

            if (msg.what == 888) {
                int orientation = msg.arg1;

                /**
                 * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
                 */
                if (orientation > 45 && orientation < 135) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    isLandscape = true;
                    showAsLandscape();
                } else if (orientation > 135 && orientation < 225) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    showAsPortrait();
                    isLandscape = false;
                } else if (orientation > 225 && orientation < 315) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    showAsLandscape();
                    isLandscape = true;
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    showAsPortrait();
                    isLandscape = false;
                }
            }
        }
    };


    private void showVideoControlBar() {
        /*if (mVideoControlLayout.getVisibility() != View.VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 42), 0);
            ani.setDuration(200);
            mVideoControlLayout.startAnimation(ani);
            mVideoControlLayout.setVisibility(View.VISIBLE);
        }*/

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏情况下,顶部标题栏也动画显示
            //TranslateAnimation ani = new TranslateAnimation(0, 0, -UIFactory.dip2px(getContext(), 48), 0);
            //ani.setDuration(200);
            //mLayoutTop.startAnimation(ani);
           // mLayoutTop.setVisibility(View.VISIBLE);
        } else {
            //mLayoutTop.setVisibility(View.VISIBLE);
        }

        // 显示后设置10秒后自动隐藏
        //mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
        //mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_CONTROL_BAR, AUTO_HIDE_CONTROL_BAR_DURATION);
    }

    private void hideVideoControlBar() {
        if (mVideoControlLayout.getVisibility() != View.GONE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(getContext(), 42));
            ani.setDuration(200);
            mVideoControlLayout.startAnimation(ani);
            mVideoControlLayout.setVisibility(View.GONE);
        }

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏情况下,顶部标题栏也隐藏
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, -UIFactory.dip2px(getContext(), 48));
            ani.setDuration(200);
            mLayoutTop.startAnimation(ani);
            mLayoutTop.setVisibility(View.GONE);
        }

        // 隐藏后清空自动隐藏的消息
        //mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
    }

    private void showAsLandscape() {
        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 隐藏底部的控制按钮区域
        mLayoutControls.setVisibility(View.GONE);

        // 视频窗口全屏显示
        //RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
        //lpWnd.height = ViewGroup.LayoutParams.MATCH_PARENT;

        //DisplayMetrics dm = new DisplayMetrics();
        //lpWnd.height = dm.heightPixels;
        //lpWnd.width = dm.widthPixels;
        // lpWnd.removeRule(RelativeLayout.BELOW);

        //lpWnd.topMargin = 0;
        //mLayoutVideoWnd.setLayoutParams(lpWnd);

        // 上面标题半透明背景
        mLayoutTop.setBackgroundColor(0x40000000);
        //mLayoutTop.setVisibility(View.GONE);

        mBtnScreenRatio.setText(R.string.device_opt_smallscreen);
    }

    private void showAsPortrait() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 还原上面标题栏背景
        mLayoutTop.setBackgroundColor(getResources().getColor(R.color.theme_color));
        mLayoutTop.setVisibility(View.VISIBLE);

        // 视频显示为小窗口
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
        lpWnd.height = UIFactory.dip2px(getContext(), 240);
        lpWnd.topMargin = UIFactory.dip2px(getContext(), 48);
        // lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
        mLayoutVideoWnd.setLayoutParams(lpWnd);

        // 显示底部的控制按钮区域
        mLayoutControls.setVisibility(View.VISIBLE);

        mBtnScreenRatio.setText(R.string.device_opt_fullscreen);
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {

        private boolean isClickFullScreen;        // 记录全屏按钮的状态，默认false
        private boolean isEffetSysSetting = false;   // 手机系统的重力感应设置是否生效，默认无效，想要生效改成true就好了
        private boolean isOpenSensor = true;      // 是否打开传输，默认打开
        private boolean isLandscape = false;      // 默认是竖屏
        private boolean isChangeOrientation = true;  // 记录点击全屏后屏幕朝向是否改变，默认会自动切换

        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private Handler rotateHandler;
        private Activity activity;

        public OrientationSensorListener(Handler handler, Activity activity) {
            rotateHandler = handler;
            this.activity = activity;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }


            /**
             * 获取手机系统的重力感应开关设置，这段代码看需求，不要就删除
             * screenchange = 1 表示开启，screenchange = 0 表示禁用
             * 要是禁用了就直接返回
             */
            if (isEffetSysSetting) {
                try {
                    int isRotate = Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);

                    // 如果用户禁用掉了重力感应就直接return
                    if (isRotate == 0) return;
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }


            // 只有点了按钮时才需要根据当前的状态来更新状态
            if (isClickFullScreen) {
                if (isLandscape && screenIsPortrait(orientation)) {           // 之前是横屏，并且当前是竖屏的状态
                    updateState(false, false, true, true);
                } else if (!isLandscape && screenIsLandscape(orientation)) {  // 之前是竖屏，并且当前是横屏的状态
                    updateState(true, false, true, true);
                } else if (isLandscape && screenIsLandscape(orientation)) {    // 之前是横屏，现在还是横屏的状态
                    isChangeOrientation = false;
                } else if (!isLandscape && screenIsPortrait(orientation)) {  // 之前是竖屏，现在还是竖屏的状态
                    isChangeOrientation = false;
                }
            }

            // 判断是否要进行中断信息传递
            if (!isOpenSensor) {
                return;
            }

            if (rotateHandler != null) {
                rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
            }
        }

        /**
         * 当前屏幕朝向是否竖屏
         *
         * @param orientation
         * @return
         */
        private boolean screenIsPortrait(int orientation) {
            return (((orientation > 315 && orientation <= 360) || (orientation >= 0 && orientation <= 45))
                    || (orientation > 135 && orientation <= 225));
        }

        /**
         * 当前屏幕朝向是否横屏
         *
         * @param orientation
         * @return
         */
        private boolean screenIsLandscape(int orientation) {
            return ((orientation > 45 && orientation <= 135) || (orientation > 225 && orientation <= 315));
        }


        /**
         * 更新状态
         *
         * @param isLandscape         横屏
         * @param isClickFullScreen   全屏点击
         * @param isOpenSensor        打开传输
         * @param isChangeOrientation 朝向改变
         */
        private void updateState(boolean isLandscape, boolean isClickFullScreen, boolean isOpenSensor, boolean isChangeOrientation) {
            this.isLandscape = isLandscape;
            this.isClickFullScreen = isClickFullScreen;
            this.isOpenSensor = isOpenSensor;
            this.isChangeOrientation = isChangeOrientation;
        }

    }

    // endregion


    /**
     * 开启单向对讲
     */
    private void startTalkByHalfDuplex() {
        if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
            mTalkManager.startTalkByHalfDuplex();
        }
    }

    /**
     * 关闭单向对讲
     */
    private void stopTalkByHalfDuplex() {
        if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
            mTalkManager.stopTalkByHalfDuplex();
        }
    }

    /**
     * 开启双向对讲
     */
    private void startTalkByDoubleDirection() {
        if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
            mTalkManager.startTalkByDoubleDirection();
        }
    }

    /**
     * 关闭双向对讲
     */
    private void stopTalkByDoubleDirection() {
        if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
            mTalkManager.stopTalkByDoubleDirection();
        }
    }

    private void destroyTalk() {
        if (mTalkManager != null) {
            mTalkManager.stopTalkThread();
            mTalkManager.sendStopTalkCommand();
        }
        mIsDoubleTalkPress = false;
        mBtnVoiceTalk.setBackgroundResource(R.drawable.icon_voice_talk);
    }

}
