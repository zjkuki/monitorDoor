package com.janady.device;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.DialogInputPasswd;
import com.example.common.UIFactory;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.MyApplication;
import com.hb.dialog.myDialog.MyAlertDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.janady.Util;
import com.janady.database.model.Door;
import com.lkd.smartlocker.R;
import com.example.funsdkdemo.devices.ActivityDeviceFishEyeInfo;
import com.example.funsdkdemo.devices.ActivityGuideDevicePictureList;
import com.example.funsdkdemo.devices.ActivityGuideDeviceSportPicList;
import com.example.funsdkdemo.devices.monitor.ActivityGuideDevicePreview;
import com.example.funsdkdemo.devices.playback.ActivityGuideDeviceRecordList;
import com.example.funsdkdemo.devices.settings.ActivityGuideDeviceSetup;
import com.example.funsdkdemo.devices.tour.view.TourActivity;
import com.janady.Dialogs;
import com.janady.database.model.Camera;
import com.janady.database.model.WifiRemoter;
import com.janady.lkd.WifiRemoterBoard;
import com.janady.view.CustomCircle;
import com.janady.view.CycleSelector.SelectorView;
import com.lib.EPTZCMD;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunLog;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.OPPTZControl;
import com.lib.funsdk.support.config.OPPTZPreset;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunStreamType;
import com.lib.funsdk.support.utils.FileUtils;
import com.lib.funsdk.support.utils.TalkManager;
import com.lib.funsdk.support.widget.FunVideoView;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//import static com.lib.funsdk.support.models.FunDevType.EE_DEV_SPORTCAMERA;

/**
 * Demo: 监控类设备播放控制等 
 * @author Administrator
 */
@SuppressLint("ClickableViewAccessibility")
public class WifiRemoterBoardActivity
				extends ActivityDemo
				implements OnClickListener,
							FunVideoView.GestureListner,
							OnFunDeviceOptListener,
							OnPreparedListener,
							OnErrorListener,
							OnInfoListener{

	private final String TAG="----WRBActivity-------";
	private RelativeLayout mLayoutTop = null;
	private TextView mTextTitle = null;
	private ImageButton mBtnBack = null;
	private ImageButton mBtnSetup = null;

	private FunDevice mFunDevice = null;

	private RelativeLayout mLayoutVideoWnd = null;
	private RelativeLayout mLayoutFunctionButtons = null;
	private FunVideoView mFunVideoView = null;

	private LinearLayout mVideoControlLayout = null;
	private LinearLayout mLayoutCameraButtons = null;
	private LinearLayout mLayoutLockerButtons = null;

	private TextView mTextStreamType = null;

	private Button mBtnPlay = null;
	private Button mBtnStop = null;
	private Button mBtnStream = null;
	private Button mBtnCapture = null;
	private Button mBtnRecord = null;
	private Button mBtnScreenRatio = null;
	private Button mBtnFishEyeInfo = null;
	private Button mBtnGetPreset = null;
	private Button mBtnSetPreset = null;

	private View mSplitView = null;
	private CheckBox mCbDoubleTalk = null;
	private RelativeLayout mLayoutRecording = null;

	//private RoundMenuView roundMenuView = null;
	private CustomCircle roundMenuView = null;

	private LinearLayout mLayoutControls = null;
	private LinearLayout mLayoutChannel = null;
	private RelativeLayout mBtnVoiceTalk = null;
	private RelativeLayout mBtnVoiceTalk_jcdp = null;

	private TabLayout mTabDoors = null;

	private Button mBtnVoice = null;
    private ImageButton mBtnQuitVoice = null;
	private ImageButton mBtnDevCapture = null;
	private ImageButton mBtnDevRecord = null;
	private ImageButton mBtnLocker = null;

	private ImageButton mBtnSkipNext = null;
    private ImageButton mBtnSkipPrevious = null;


	private RelativeLayout mLayoutDirectionControl = null;
	private ImageButton mPtz_up = null;
	private ImageButton mPtz_down = null;
	private ImageButton mPtz_left = null;
	private ImageButton mPtz_right = null;

	private ImageButton mBtnAddDoor = null;
	private ImageButton mBtnRemoveDoor = null;
	private ImageView mBtnSelectLeft = null;
	private ImageView mBtnSelectRight = null;

	private ImageButton mBtnLockOpen = null;
	private ImageButton mBtnLockClose = null;
	private ImageButton mBtnLockStop = null;
	private ImageButton mBtnLockLocker = null;
	private ImageButton mBtnOpenCamera = null;



	private TextView mTextVideoStat = null;
	private AlertDialog alert = null;
	private AlertDialog.Builder builder = null;

	private String preset = null;
	private String newPsd = "";

	private int mChannelCount;
	private boolean isGetSysFirst = true;

	private boolean mIsLocked = false;

	private final int MESSAGE_PLAY_MEDIA = 0x100;
	private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x102;
	private final int MESSAGE_TOAST_SCREENSHOT_PREVIEW = 0x103;
	private final int MESSAGE_OPEN_VOICE = 0x104;

	// 自动隐藏底部的操作控制按钮栏的时间
	private final int AUTO_HIDE_CONTROL_BAR_DURATION = 10000;

	private TalkManager mTalkManager = null;

	private boolean mCanToPlay = false;

	public String NativeLoginPsw; //本地密码
	private boolean mIsDoubleTalkPress;
	private TourActivity mTourFragment;

	private Camera camera;
    List<Camera> cams = null;
    private int currIndex = 0;
    private boolean mIsCameraOpend = false;



    private WifiRemoterBoard wifiRemoterBoard = null;
    private WifiRemoter mWifiRemoter = null;
    private int currWifiRemoterDoorIndex = 0;
    private int maxDoorNo = 0;


    private SelectorView selectorView;

    private TextView tvSelectedCamera;
    private CheckBox cbSelectedDoor;
	private ImageView imgLockLockerStat;
	private List<String> doorNo;
	private Context mContext;

	private Camera getCamera(int index){
        if(cams.size()>0 && index>=0 && index<cams.size()){
            return cams.get(index);
        }else{
            return null;
        }
    }
	private void initCamera(int devId, String sn){
        //cams = MyApplication.liteOrm.query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_SN, sn));
        cams = MyApplication.liteOrm.query(Camera.class);
        if(cams.size()>0){
            currIndex = 0;
            for(int i=0;i<cams.size();i++){
                if(cams.get(i).sn.equals(sn)) {
                    camera = cams.get(i);
                    currIndex = i;
                }
            }
        }
		//mTextTitle.setText(mFunDevice.devName);
		if(!camera.isOnline){
			tvSelectedCamera.setText("摄像机："+camera.sceneName+"(离线)");
		}else{
			tvSelectedCamera.setText("摄像机："+camera.sceneName);
		}

        mFunDevice = FunSupport.getInstance().findDeviceById(devId);
        if (null == mFunDevice) {
            mFunDevice = FunSupport.getInstance().findLanDevice(camera.sn);
            if(mFunDevice == null) {
                mFunDevice = FunSupport.getInstance().findLanDevice(camera.name);
                if(mFunDevice == null) {
                    mFunDevice = FunSupport.getInstance().findDeviceBySn(camera.sn);
                    if(mFunDevice == null) {
                        mFunDevice = FunSupport.getInstance().findTempDevice(camera.mac);
                        if(mFunDevice == null) {
                            finish();
                            return;
                        }
                    }
                }
            }
        }

		mCanToPlay = false;
        isGetSysFirst = true;

		mFunVideoView = (FunVideoView) findViewById(R.id.funVideoView);
        mFunVideoView.clearVideo();
		mFunVideoView.stopPlayback();
		//playRealMedia();

		//if (mFunDevice.devType == FunDevType.EE_DEV_LAMP_FISHEYE) {
		if (mFunDevice.devType == FunDevType.EE_DEV_CAMERA) {
			// 鱼眼灯泡,设置鱼眼效果
			mFunVideoView.setFishEye(true);
		}

		// 如果支持云台控制，显示方向键和预置点按钮
		if (mFunDevice.isSupportPTZ()) {
			mSplitView.setVisibility(View.VISIBLE);
			mLayoutDirectionControl.setVisibility(View.VISIBLE);
		}

		//mFunVideoView.setOnTouchListener(new OnVideoViewTouchListener());
		mFunVideoView.setGestureListner(this);
		mFunVideoView.setOnPreparedListener(this);
		mFunVideoView.setOnErrorListener(this);
		mFunVideoView.setOnInfoListener(this);
		mVideoControlLayout = (LinearLayout) findViewById(R.id.layoutVideoControl);


		// 允许横竖屏切换
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

		//showVideoControlBar();
		hideVideoControlBar();

		mFunVideoView.setMediaSound(false);			//关闭本地音频

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

		mCanToPlay = false;

		// 如果设备未登录,先登录设备
		if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
			loginDevice(camera.loginName, camera.loginPsw);
		} else {
			requestSystemInfo();
		}

		// 注册设备操作回调
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }


	SelectorView.SeletcorAdapter doors_adapter = new SelectorView.SeletcorAdapter(){
		@Override
		public int getItemCount() {
			return doorNo.size();
		}

		@Override
		public void setView(View view, int position) {
			((TextView)view).setText(doorNo.get(position));
		}


	};
	public void left(View view){
		selectorView.left();
	}

	public void right(View view){
		selectorView.right();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wifiremoterboard_lock_activity);
		cbSelectedDoor = (CheckBox) findViewById(R.id.cb_selected_door);
		cbSelectedDoor.setText("默认");
		cbSelectedDoor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cbSelectedDoor.isChecked()){
					mWifiRemoter.defaultDoorId = mTabDoors.getSelectedTabPosition();
					MyApplication.liteOrm.cascade().save(mWifiRemoter);
				}else{

				}
			}
		});
		tvSelectedCamera = (TextView) findViewById(R.id.tv_selected_camera);
		imgLockLockerStat = (ImageView) findViewById(R.id.imgLockLockerStat);
		imgLockLockerStat.setVisibility(View.GONE);

		mTabDoors = (TabLayout) findViewById(R.id.tabDoors);
		mTabDoors.setTabGravity(TabLayout.GRAVITY_CENTER);
		mTabDoors.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				if(tab.getPosition()==mWifiRemoter.defaultDoorId){
					cbSelectedDoor.setChecked(true);
				}else{
					cbSelectedDoor.setChecked(false);
				}

				Toast.makeText(mContext, "选中的"+tab.getPosition()+"   doorNo:"+mWifiRemoter.doorList.get(tab.getPosition()).no+"   text:"+tab.getText(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

				Toast.makeText(mContext, "未选中的"+tab.getText(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

				Toast.makeText(mContext, "复选的"+tab.getText(), Toast.LENGTH_SHORT).show();

			}
		});

		mContext = this;

		mLayoutTop = (RelativeLayout) findViewById(R.id.layoutTop);

		mTextTitle = (TextView) findViewById(R.id.textViewInTopLayout);

		mBtnBack = (ImageButton) findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);

		mLayoutVideoWnd = (RelativeLayout) findViewById(R.id.layoutPlayWnd);

		mBtnPlay = (Button) findViewById(R.id.btnPlay);
		mBtnStop = (Button) findViewById(R.id.btnStop);
		mBtnStream = (Button) findViewById(R.id.btnStream);
		mBtnCapture = (Button) findViewById(R.id.btnCapture);
		mBtnRecord = (Button) findViewById(R.id.btnRecord);
		mBtnScreenRatio = (Button) findViewById(R.id.btnScreenRatio);
		mBtnFishEyeInfo = (Button) findViewById(R.id.btnFishEyeInfo);
        mBtnSkipNext = (ImageButton) findViewById(R.id.btnDevNext);
        mBtnSkipPrevious = (ImageButton) findViewById(R.id.btnDevPre);

		mLayoutRecording = (RelativeLayout) findViewById(R.id.layout_recording);
		mBtnPlay.setOnClickListener(this);
		mBtnStop.setOnClickListener(this);
		mBtnStream.setOnClickListener(this);
		mBtnCapture.setOnClickListener(this);
		mBtnRecord.setOnClickListener(this);
		mBtnScreenRatio.setOnClickListener(this);
		mBtnFishEyeInfo.setOnClickListener(this);
		mBtnSkipNext.setOnClickListener(this);
		mBtnSkipPrevious.setOnClickListener(this);

		mTextVideoStat = (TextView) findViewById(R.id.textVideoStat);

		mBtnVoiceTalk_jcdp = (RelativeLayout) findViewById(R.id.btnVoiceTalk_jcdp);
		mBtnVoiceTalk_jcdp.setVisibility(View.VISIBLE);

		mBtnVoiceTalk = (RelativeLayout) findViewById(R.id.btnVoiceTalk);
		mBtnVoiceTalk.setVisibility(View.VISIBLE);

		//mBtnVoice = (Button) findViewById(R.id.Btn_Talk_Switch);
		mBtnVoice = (Button) findViewById(R.id.Btn_Talk_Switch_jcdp);
		mBtnVoice.setVisibility(View.GONE);

        mBtnQuitVoice = (ImageButton) findViewById(R.id.btn_quit_voice);
        mBtnQuitVoice.setVisibility(View.GONE);

		mBtnDevCapture = (ImageButton) findViewById(R.id.btnDevCapture);
		mBtnDevRecord = (ImageButton) findViewById(R.id.btnDevRecord);
		mBtnGetPreset = (Button) findViewById(R.id.btnGetPreset);
		mBtnSetPreset = (Button) findViewById(R.id.btnSetPreset);
		

		mSplitView = findViewById(R.id.splitView);
		mCbDoubleTalk = findViewById(R.id.cb_double_talk_switch);
		mCbDoubleTalk.setChecked(true);
		mCbDoubleTalk.setVisibility(View.GONE);


		mLayoutFunctionButtons = (RelativeLayout) findViewById(R.id.layoutFunctionButtons);
		mLayoutFunctionButtons.setVisibility(View.GONE);

		mLayoutDirectionControl = (RelativeLayout) findViewById(R.id.layoutDirectionControl);

		mPtz_up = (ImageButton) findViewById(R.id.btnCamUp);
		mPtz_down = (ImageButton) findViewById(R.id.btnCamDown);
		mPtz_left = (ImageButton) findViewById(R.id.btnCamLeft);
		mPtz_right = (ImageButton) findViewById(R.id.btnCamRight);

		mBtnAddDoor = (ImageButton) findViewById(R.id.btnAddDoor);
		mBtnRemoveDoor = (ImageButton) findViewById(R.id.btnRemoveDoor);
		mBtnSelectLeft = (ImageView) findViewById(R.id.select_left);
		mBtnSelectRight = (ImageView) findViewById(R.id.select_right);

		mBtnLockOpen = (ImageButton) findViewById(R.id.btnOpenDoor);
		mBtnLockClose = (ImageButton) findViewById(R.id.btnCloseDoor);
		mBtnLockStop = (ImageButton) findViewById(R.id.btnLockStop);
		mBtnLockLocker = (ImageButton) findViewById(R.id.btnLockLocker);
		mBtnOpenCamera = (ImageButton) findViewById(R.id.btnOpenCamear);

		mBtnAddDoor.setOnClickListener(this);
		mBtnRemoveDoor.setOnClickListener(this);

		mBtnLockOpen.setOnClickListener(this);
		mBtnLockClose.setOnClickListener(this);
		mBtnLockStop.setOnClickListener(this);

		mBtnLockLocker.setOnClickListener(this);
		mBtnLockLocker.setOnTouchListener(mLockerLockTouchLs);

		mBtnOpenCamera.setOnClickListener(this);
		mBtnOpenCamera.setOnTouchListener(null);

		mBtnVoiceTalk_jcdp.setOnClickListener(this);
		mBtnVoiceTalk_jcdp.setOnTouchListener(mIntercomTouchLs);
		mBtnVoice.setOnClickListener(this);
        mBtnQuitVoice.setOnClickListener(this);
		mBtnDevCapture.setOnClickListener(this);
		mBtnDevRecord.setOnClickListener(this);
		//mBtnGetPreset.setOnClickListener(this);
		//mBtnSetPreset.setOnClickListener(this);

		mCbDoubleTalk.setOnClickListener(this);
		/*mPtz_up.setOnTouchListener(onPtz_up);
		mPtz_down.setOnTouchListener(onPtz_down);
		mPtz_left.setOnTouchListener(onPtz_left);
		mPtz_right.setOnTouchListener(onPtz_right);*/

		//mBtnLocker.setOnClickListener(this);
		//mBtnLocker.setOnTouchListener(mLockerTouchLs);

		mLayoutControls = (LinearLayout) findViewById(R.id.layoutFunctionControl);
		mLayoutChannel = (LinearLayout) findViewById(R.id.layoutChannelBtn);

		mLayoutCameraButtons = (LinearLayout) findViewById(R.id.layoutCameraButtons);
		mLayoutLockerButtons = (LinearLayout) findViewById(R.id.layoutLockerButtons);


		mTextStreamType = (TextView) findViewById(R.id.textStreamStat);

		setNavagateRightButton(R.layout.imagebutton_settings);
		mBtnSetup = (ImageButton) findViewById(R.id.btnSettings);
		mBtnSetup.setImageResource(R.drawable.ic_menu_white_24dp);
		mBtnSetup.setOnClickListener(this);

		String wifiMac = getIntent().getStringExtra("WIFI_DEVICE_MAC");
		String wifiSceneName = getIntent().getStringExtra("WIFI_DEVICE_SCENE");
		String wifiSn = getIntent().getStringExtra("WIFI_DEVICE_SN");

		initWifiDevice(wifiMac, wifiSceneName, wifiSn);

		setStatusBar();
	}

	private void initWifiDevice(String mac, String sceneName, String sn){
		List<WifiRemoter> wifiRemoters = MyApplication.liteOrm.cascade().query(new QueryBuilder<WifiRemoter>(WifiRemoter.class).whereEquals(WifiRemoter.COL_MAC, mac));
		if(wifiRemoters.size()>0) {
			mWifiRemoter = wifiRemoters.get(0);
			wifiRemoterBoard = new WifiRemoterBoard(mContext, mWifiRemoter, null);
			if(mWifiRemoter.camera!=null) {
				showCamera(mWifiRemoter.camera.devId, mWifiRemoter.camera.sn);
			}else{
				showCamera(0,"");
			}
		}else{
			mWifiRemoter = null;
			wifiRemoterBoard = null;
			showCamera(0, "");
		}

		mTextTitle.setText(sceneName);

		if(mWifiRemoter.doorList!=null && mWifiRemoter.doorList.size()>0) {
			//mTabDoors.setTabGravity();
			/*doorNo = new ArrayList<>();
			for (int i = 0; i < 1; i++) {
				doorNo.add(i + "号门");
				mTabDoors.addTab(mTabDoors.newTab().setText(i + "号门"));
			}*/
			if(mTabDoors.getTabCount()<5){
				mTabDoors.setTabMode(TabLayout.MODE_FIXED);
				mTabDoors.setTabGravity(TabLayout.GRAVITY_FILL);
			}else{
				mTabDoors.setTabMode(TabLayout.MODE_SCROLLABLE);
				mTabDoors.setTabGravity(TabLayout.GRAVITY_CENTER);
			}

			for (int i = 0; i < mWifiRemoter.doorList.size(); i++) {
				TabLayout.Tab tab = mTabDoors.newTab();
				tab.setText(mWifiRemoter.doorList.get(i).name);
				mTabDoors.addTab(tab);
				currWifiRemoterDoorIndex = i;
			}
			if(mTabDoors.getTabCount()>0) {
				mTabDoors.getTabAt(mWifiRemoter.defaultDoorId).select();
			}
			//cbSelectedDoor.setText(mWifiRemoter.doorList.get(currWifiRemoterDoorIndex).name);
		}else{
			Dialogs.alertDialog2Btn(mContext, "提示", "您还没有添加门锁，马上添加门锁吗？", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AddDoor();
				}
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
		}


		/*selectorView = (SelectorView) findViewById(R.id.selector);

		selectorView.setAdapter(doors_adapter);

		selectorView.setOnItemCheckListener(new SelectorView.OnItemCheckListener() {
			@Override
			public void onItemChecked(int position) {
				Log.i("-----WRBA----", "onItemChecked: "+position);
				tvSelectedDoor.setText(doorNo.get(position));
			}

			@Override
			public void onScrolled(int position) {
				Log.i("-----WRBA-----", "onScrolled: "+position);
			}
		});*/

	}

	private void showCamera(int devId, String sn){
		RelativeLayout.LayoutParams lp =  (RelativeLayout.LayoutParams) mLayoutControls.getLayoutParams();

		if(devId!=0) {
			mLayoutVideoWnd.setVisibility(View.VISIBLE);
			//mLayoutFunctionButtons.setVisibility(View.VISIBLE);
			mLayoutCameraButtons.setVisibility(View.VISIBLE);
			lp.addRule(RelativeLayout.BELOW, R.id.layoutPlayWnd);
			lp.topMargin=0;
			mLayoutControls.setLayoutParams(lp);
			initCamera(devId, sn);
			mBtnOpenCamera.setImageResource(R.drawable.icon_btn_camera_selected);
			mIsCameraOpend = true;
		}else{
			mLayoutVideoWnd.setVisibility(View.GONE);
			//mLayoutFunctionButtons.setVisibility(View.GONE);
			mLayoutCameraButtons.setVisibility(View.GONE);
			lp.addRule(RelativeLayout.BELOW, R.id.layoutTop);
			lp.topMargin=0;
			mLayoutControls.setLayoutParams(lp);
			mBtnOpenCamera.setImageResource(R.drawable.icon_btn_camera_normal);
			mIsCameraOpend = false;
		}

	}

	private void selectCamera(){
		final int[] index = new int[1];
		final List<Camera> cams = MyApplication.liteOrm.query(Camera.class);
		if(cams.size()>0) {
			String[] s=new String[cams.size()+1];
			s[0] = "隐藏摄像机";
			for(int i=0;i<cams.size();i++){
				s[i+1]=cams.get(i).sceneName;
				if(cams.get(i).isOnline) {
					s[i + 1] = s[i + 1] + "（在线）";
				}else{
					s[i + 1] = s[i + 1] + "（离线）";
				}
			}

			Dialogs.alertDialogSingleSelect(mContext, "请选择一个摄像机",s , R.drawable.xmjp_camera, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which==0) {
						index[0] = -1;
					}else{
						index[0] = which ;
					}
				}
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(index[0]==0) {
						showCamera(0,"");
					}else{
						showCamera(cams.get(index[0]-1).devId, cams.get(index[0]-1).sn);

						mWifiRemoter.camera = cams.get(index[0]-1);
						wifiRemoterBoard.setWifiRemoter(mWifiRemoter);
						MyApplication.liteOrm.cascade().save(mWifiRemoter);
						//MyApplication.liteOrm.save(mWifiRemoter);

					}
				}
			});
		}else{
			Dialogs.alertMessage(mContext,"失败", "您还没添加摄像机。");
		}
	}

	@Override
	protected void onDestroy() {

		stopMedia();

		FunSupport.getInstance().removeOnFunDeviceOptListener(this);

//		 if ( null != mFunDevice ) {
//		 FunSupport.getInstance().requestDeviceLogout(mFunDevice);
//		 }

		if (null != mHandler) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}

		super.onDestroy();
	}


	@Override
	protected void onResume() {

		if (mCanToPlay) {
			playRealMedia();
		}
//			 resumeMedia();

		super.onResume();
	}


	@Override
	protected void onPause() {
		destroyTalk();
		//closeVoiceChannel(0);
		closeVoiceChannel1(0);
		stopMedia();
		super.onPause();
	}


	@Override
	public void onBackPressed() {
		// 如果当前是横屏，返回时先回到竖屏
		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			return;
		}

		finish();
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

	@SuppressLint("ResourceType")
	@Override
	public void onClick(View v) {
		if (v.getId() >= 1000 && v.getId() < 1000 + mChannelCount) {
			mFunDevice.CurrChannel = v.getId() - 1000;
			mFunVideoView.stopPlayback();
			playRealMedia();
		}
		switch (v.getId()) {
		case 1101: {
			startDevicesPreview();
		}
			break;
		case R.id.backBtnInTopLayout: {
			// 返回/退出
			onBackPressed();
		}
			break;
		case R.id.btnPlay: // 开始播放
		{
			mFunVideoView.stopPlayback();
			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_MEDIA, 1000);
//			playRealMedia();
		}
			break;
		case R.id.btnStop: // 停止播放
		{
			stopMedia();
		}
			break;
		case R.id.btnStream: // 切换码流
		{
			switchMediaStream();
		}
			break;
		case R.id.btnCapture: // 截图
		{
			tryToCapture();
//			FunSupport.getInstance().requestDeviceCapture(mFunDevice);  //device capture
		}
			break;
		case R.id.btnRecord: // 录像
		{
			tryToRecord();
		}
			break;
            case R.id.Btn_Talk_Switch:
            {
                openVoiceChannel();
            }
            break;
			case R.id.cb_double_talk_switch://双向对讲开关
            case R.id.btn_quit_voice://退出对讲开关
            {
				closeVoiceChannel(500);
            }
            break;
		case R.id.btnDevCapture: // 远程设备图像列表
		{
			startPictureList();
		}
			break;
		case R.id.btnDevRecord: // 远程设备录像列表（回放）
		{
			startRecordList();
		}
			break;
		case R.id.btnScreenRatio: // 横竖屏切换
		{
			switchOrientation();
		}
			break;
		case R.id.btnSettings: // 系统设置/系统信息
		{
			startDeviceSetup();
		}
			break;
		case R.id.btnSetPreset:
			{
			final EditText editText = new EditText(this);
			int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
			editText.setInputType(inputType);
				new AlertDialog.Builder(this).setTitle(R.string.user_input_preset_number)
					.setView(editText)
					.setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							int i = 0;
							String preset = editText.getText().toString();
							if (TextUtils.isEmpty(preset)) {
								i = 1;
							}
							 else {
								i = Integer.parseInt(preset);
							}
							if (i > 200) {
								 Toast.makeText(getApplicationContext(),R.string.user_input_preset_number_warn, Toast.LENGTH_SHORT).show();
							} else {
								// 注意：如果是IPC/摇头机,channel = 0, 否则channel=-1，以实际使用设备为准，如果需要兼容，可以两条命令同时发送
								OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_SET_PRESET, 0, i);
								FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);

								// for Demo, 为了兼容设备，cmd2和cmd一起发送，两条命令的差别是channel值不同
								OPPTZControl cmd2 = new OPPTZControl(OPPTZControl.CMD_SET_PRESET, -1, i);
								FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd2);
							}
						}

					})
					.setNegativeButton(R.string.common_cancel, null).show();
		}
			break;
		case R.id.btnGetPreset:
			{
			OPPTZPreset oPPTZPreset = (OPPTZPreset) mFunDevice.getConfig(OPPTZPreset.CONFIG_NAME);
			if (null != oPPTZPreset) {
				int[] ids = oPPTZPreset.getIds();
				int index = 0;
                preset = null;
				Arrays.sort(ids);
				if (ids != null && ids.length > 0) {
					final String[] idStrs = new String[ids.length];
					for (int i = 0; i < ids.length; i++) {
						idStrs[i] = (Integer.toString(ids[i]));
					}
					alert = null;
					builder = new AlertDialog.Builder(this);
			            alert = builder
			                    .setTitle(R.string.user_select_preset)
							.setSingleChoiceItems(idStrs, index, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									preset = idStrs[which];
								}
			                    })
			                    .setPositiveButton(R.string.common_skip, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (TextUtils.isEmpty(preset)) {
										preset = idStrs[0];
									}
									which = Integer.parseInt(preset);
									OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_GO_TO_PRESET, 0, which);
									FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);
								}
								})
								.setNegativeButton(R.string.common_delete, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (TextUtils.isEmpty(preset)) {
										preset = idStrs[0];
									}
									which = Integer.parseInt(preset);
									OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_CLEAR_PRESET, 0, which);
									FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);
								}
							}).setNeutralButton(R.string.common_correct, new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
                                        OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_CORRECT, 0, 0);
                                        FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);
									}
								}).create();
					alert.show();
				}
			}
		}
			break;
		case R.id.btnFishEyeInfo:
			{
				// 显示鱼眼信息
				showFishEyeInfo();
			}
			break;
        case R.id.btnDevPre:
            {
                currIndex --;
                if(currIndex<0){
					showToast("已是第一个摄像机！");
					currIndex=0;
					return;
				}

                if(getCamera(currIndex)!=null){
                    camera=getCamera(currIndex);
                    initCamera(camera.devId, camera.sn);
                }else{
                	currIndex++;
                	Log.d("---DCA----","切换摄像机失败");
				}
            }
            break;
        case R.id.btnDevNext:
            {
                currIndex ++;
				if(currIndex > cams.size()-1){
					showToast("已是最后一个摄像机！");
					currIndex =cams.size()-1;
					return;
				}
                if(getCamera(currIndex)!=null){
                    camera=getCamera(currIndex);
                    initCamera(camera.devId, camera.sn);
                }else{
					currIndex --;
					Log.d("---DCA----","切换摄像机失败");
                }
            }
            break;
			case R.id.btnOpenCamear:
			{

				if(wifiRemoterBoard!=null && wifiRemoterBoard.getWifiRemoter()!=null){
					if(wifiRemoterBoard.getWifiRemoter().camera!=null){
						if(!mIsCameraOpend) {
							showCamera(wifiRemoterBoard.getWifiRemoter().camera.devId, wifiRemoterBoard.getWifiRemoter().camera.sn);
						}else{
							showCamera(0,"");
						}
					}else{
						Dialogs.alertDialogBtn(mContext, "打开摄像机失败", "此设备还未绑定摄像机，马上进行摄像机绑定吗？", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								selectCamera();
							}
						}, new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {

							}
						});
					}
				}
			}
			break;
			case R.id.btnAddDoor:
			{
				AddDoor();
			}
			break;
			case R.id.btnRemoveDoor:
			{
				RemoveDoor();
			}
			break;
        default:
            break;
		}
	}

	private void AddDoor(){
		final String[] doorName = {""};

		final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(mContext).builder()
				.setTitle("请输入门锁的名称")
				.setEditText("");
		myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, myAlertInputDialog.getResult());
        		doorName[0] = myAlertInputDialog.getResult();
				if(mWifiRemoter!=null){
					Door door = new Door();

					if(mWifiRemoter.doorList==null){
						mWifiRemoter.doorList = new ArrayList<>();
					}

					if(mWifiRemoter.doorList.size()>0){
						maxDoorNo = MaxDoorNo(mWifiRemoter.doorList);
						door.no = maxDoorNo+1;

						currWifiRemoterDoorIndex++;
					}else {
						door.no = 0;
						mWifiRemoter.defaultDoorId = 0;
						currWifiRemoterDoorIndex = 0;
					}
					door.name = doorName[0];
					door.remote=mWifiRemoter;
					mWifiRemoter.doorList.add(door);
					MyApplication.liteOrm.cascade().save(mWifiRemoter);

					if(mTabDoors.getTabCount()<5){
						mTabDoors.setTabMode(TabLayout.MODE_FIXED);
						mTabDoors.setTabGravity(TabLayout.GRAVITY_FILL);
					}else{
						mTabDoors.setTabMode(TabLayout.MODE_SCROLLABLE);
						mTabDoors.setTabGravity(TabLayout.GRAVITY_CENTER);
					}
					//cbSelectedDoor.setText(mWifiRemoter.doorList.get(mWifiRemoter.defaultDoorId).name);
					mTabDoors.addTab(mTabDoors.newTab().setText(mWifiRemoter.doorList.get(currWifiRemoterDoorIndex).name));
				}
                myAlertInputDialog.dismiss();
            }
        }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "取消");
                myAlertInputDialog.dismiss();
            }
        });
		myAlertInputDialog.show();
	}

	private void RemoveDoor(){
		final Door door = mWifiRemoter.doorList.get(mTabDoors.getSelectedTabPosition());

		final MyAlertDialog myAlertDialog = new MyAlertDialog(mContext).builder()
				.setTitle("删除门锁")
				.setMsg("确定删除【"+door.name+"】吗？");
		myAlertDialog.setPositiveButton("确认", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currWifiRemoterDoorIndex --;
				mWifiRemoter.doorList.remove(mTabDoors.getSelectedTabPosition());
				mTabDoors.removeTabAt(mTabDoors.getSelectedTabPosition());
				MyApplication.liteOrm.cascade().save(mWifiRemoter);
			}
		}).setNegativeButton("取消", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "取消");
			}
		});
		myAlertDialog.show();
	}

	private void tryToRecord() {

		if (!mFunVideoView.isPlaying() || mFunVideoView.isPaused()) {
			showToast(R.string.media_record_failure_need_playing);
			return;
		}


		if (mFunVideoView.bRecord) {
			mFunVideoView.stopRecordVideo();
			mLayoutRecording.setVisibility(View.INVISIBLE);
			toastRecordSucess(mFunVideoView.getFilePath());
		} else {
			mFunVideoView.startRecordVideo(null);
			mLayoutRecording.setVisibility(View.VISIBLE);
			showToast(R.string.media_record_start);
		}

	}

	/**
	 * 视频截图,并延时一会提示截图对话框
	 */
	private void tryToCapture() {
		if (!mFunVideoView.isPlaying()) {
			showToast(R.string.media_capture_failure_need_playing);
			return;
		}

		final String path = mFunVideoView.captureImage(null);	//图片异步保存
		if (!TextUtils.isEmpty(path)) {
			Message message = new Message();
			message.what = MESSAGE_TOAST_SCREENSHOT_PREVIEW;
			message.obj = path;
			mHandler.sendMessageDelayed(message, 200);			//此处延时一定时间等待图片保存完成后显示，也可以在回调成功后显示
		}
	}



	/**
	 * 显示截图成功对话框
	 * @param path
	 */
	private void toastScreenShotPreview(final String path) {
		View view = getLayoutInflater().inflate(R.layout.screenshot_preview, null, false);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_screenshot_preview);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inDither = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		iv.setImageBitmap(bitmap);
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_socket_capture_preview)
                .setView(view)
                .setPositiveButton(R.string.device_socket_capture_save,
                        new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File file = new File(path);
                                File imgPath = new File(FunPath.PATH_PHOTO + File.separator
                                        + file.getName());
						if (imgPath.exists()) {
							showToast(R.string.device_socket_capture_exist);
						} else {
                                    FileUtils.copyFile(path, FunPath.PATH_PHOTO + File.separator
                                            + file.getName());
							showToast(R.string.device_socket_capture_save_success);
						}
					}
                        })
                .setNegativeButton(R.string.device_socket_capture_delete,
                        new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FunPath.deleteFile(path);
						showToast(R.string.device_socket_capture_delete_success);
					}
                        })
                .show();
	}

	/**
	 * 显示录像成功对话框
	 * @param path
	 */
	private void toastRecordSucess(final String path) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_sport_camera_record_success)
				.setMessage(getString(R.string.media_record_stop) + path)
				.setPositiveButton(R.string.device_sport_camera_record_success_open,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent("android.intent.action.VIEW");
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								String type = "video/*";
								Uri uri = Uri.fromFile(new File(path));
								intent.setDataAndType(uri, type);
								startActivity(intent);
								FunLog.e("test", "------------startActivity------" + uri.toString());
							}
						})
				.setNegativeButton(R.string.device_sport_camera_record_success_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
				.show();
	}

	private void showVideoControlBar() {
		if (mVideoControlLayout.getVisibility() != View.VISIBLE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 42), 0);
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(View.VISIBLE);
		}

		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏情况下,顶部标题栏也动画显示
			TranslateAnimation ani = new TranslateAnimation(0, 0, -UIFactory.dip2px(this, 48), 0);
			ani.setDuration(200);
			mLayoutTop.startAnimation(ani);
			mLayoutTop.setVisibility(View.VISIBLE);
		} else {
			mLayoutTop.setVisibility(View.VISIBLE);
		}

		// 显示后设置10秒后自动隐藏
		mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
		mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_CONTROL_BAR, AUTO_HIDE_CONTROL_BAR_DURATION);
	}

	private void hideVideoControlBar() {
		if (mVideoControlLayout.getVisibility() != View.GONE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 42));
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(View.GONE);
		}

		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏情况下,顶部标题栏也隐藏
			TranslateAnimation ani = new TranslateAnimation(0, 0, 0, -UIFactory.dip2px(this, 48));
			ani.setDuration(200);
			mLayoutTop.startAnimation(ani);
			mLayoutTop.setVisibility(View.GONE);
		}

		// 隐藏后清空自动隐藏的消息
		mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
	}

	private void showAsLandscape() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 隐藏底部的控制按钮区域
		mLayoutControls.setVisibility(View.GONE);

		// 视频窗口全屏显示
		RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
		lpWnd.height = LayoutParams.MATCH_PARENT;
		// lpWnd.removeRule(RelativeLayout.BELOW);
		lpWnd.topMargin = 0;
		mLayoutVideoWnd.setLayoutParams(lpWnd);

		// 上面标题半透明背景
		mLayoutTop.setBackgroundColor(0x40000000);

		mBtnScreenRatio.setText(R.string.device_opt_smallscreen);
	}

	private void showAsPortrait() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 还原上面标题栏背景
		mLayoutTop.setBackgroundColor(getResources().getColor(R.color.theme_color));
		mLayoutTop.setVisibility(View.VISIBLE);

		// 视频显示为小窗口
		RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
		lpWnd.height = UIFactory.dip2px(this, 240);
		lpWnd.topMargin = UIFactory.dip2px(this, 48);
		// lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
		mLayoutVideoWnd.setLayoutParams(lpWnd);

		// 显示底部的控制按钮区域
		mLayoutControls.setVisibility(View.VISIBLE);

		mBtnScreenRatio.setText(R.string.device_opt_fullscreen);
	}

	/**
	 * 切换视频全屏/小视频窗口(以切横竖屏切换替代)
	 */
	private void switchOrientation() {
		// 横竖屏切换
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				&& getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}


	private void showPopupMenu(View view) {
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(this, view);
		// menu布局
		popupMenu.getMenuInflater().inflate(R.menu.menu_wifiremoter_activity, popupMenu.getMenu());
		// menu的item点击事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
				switch(item.getItemId()) {
					case R.id.action_bindCam: {
						selectCamera();
					}
					break;
					case R.id.setup: {
						Intent intent = new Intent();
						intent.putExtra("FUNDEVICE_ID", mFunDevice.getId());
						intent.setClass(mContext, ActivityGuideDevicePreview.class);
						startActivityForResult(intent, 0);
					}
					break;
				}
				return false;
			}
		});
		// PopupMenu关闭事件
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
			}
		});

		popupMenu.show();
	}

	/**
	 * 打开设备配置
	 */
	private void startDeviceSetup() {
		showPopupMenu(mBtnSetup);
	}

	/***
	 * 打开 多通道预览
	 */
	private void startDevicesPreview(){

	}

	private class OnVideoViewTouchListener implements OnTouchListener {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("TTT-->>> event = " + event.getAction());
			if (event.getAction() == MotionEvent.ACTION_UP) {

				// 显示或隐藏视频操作菜单
				if (mVideoControlLayout.getVisibility() == View.VISIBLE) {
					hideVideoControlBar();
				} else {
					showVideoControlBar();
				}
			}

			return false;
		}

	}

	private void loginDevice(String loginName, String pwd) {
		showWaitDialog();

		FunSupport.getInstance().requestDeviceLogin1(mFunDevice,loginName,pwd);
	}


	private void requestSystemInfo() {
		showWaitDialog();

		FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);
	}

	// 获取设备预置点列表
	private void requestPTZPreset() {
		//FunSupport.getInstance().requestDeviceConfig(mFunDevice, OPPTZPreset.CONFIG_NAME, 0);
		FunSupport.getInstance().requestDeviceConfig(mFunDevice, "Uart.PTZPreset", 0);
	}

	private void startPictureList() {
		Intent intent = new Intent();
		intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
		intent.putExtra("FILE_TYPE", "jpg");
		//if (mFunDevice.devType == EE_DEV_SPORTCAMERA) {
		if (mFunDevice.devType == FunDevType.EE_DEV_CAMERA) {
			intent.setClass(this, ActivityGuideDeviceSportPicList.class);
		} else {
			intent.setClass(this, ActivityGuideDevicePictureList.class);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void startRecordList() {
		Intent intent = new Intent();
		intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
		intent.putExtra("FILE_TYPE", "h264;mp4");
		intent.setClass(this, ActivityGuideDeviceRecordList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void playRealMedia() {

		// 显示状态: 正在打开视频...
		mTextVideoStat.setText(R.string.media_player_opening);
		mTextVideoStat.setVisibility(View.VISIBLE);

		if (mFunDevice.isRemote) {
			mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
		} else {
			String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
			mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
		}

		// 打开声音
		//mFunVideoView.setMediaSound(true);

		// 设置当前播放的码流类型
		if (FunStreamType.STREAM_SECONDARY == mFunVideoView.getStreamType()) {
			mTextStreamType.setText(R.string.media_stream_secondary);
		} else {
			mTextStreamType.setText(R.string.media_stream_main);
		}
	}
	// 添加通道选择按钮
	@SuppressWarnings("ResourceType")
	private void addChannelBtn(int channelCount) {

		int m = UIFactory.dip2px(this, 5);
		int p = UIFactory.dip2px(this, 3);
		TextView textView = new TextView(this);
		LinearLayout.LayoutParams layoutParamsT = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParamsT.setMargins(m, m, m, m);
		textView.setLayoutParams(layoutParamsT);
		textView.setText(R.string.device_opt_channel);
		textView.setTextSize(UIFactory.dip2px(this, 10));
		textView.setTextColor(getResources().getColor(R.color.theme_color));
		mLayoutChannel.addView(textView);

		Button bt = new Button(this);
		bt.setId(1101);
		bt.setTextColor(getResources().getColor(R.color.theme_color));
		bt.setPadding(p, p, p, p);
		bt.setLayoutParams(layoutParamsT);
		bt.setText(R.string.device_camera_channels_preview_title);
		bt.setOnClickListener(this);
		mLayoutChannel.addView(bt);

		for (int i = 0; i < channelCount; i++) {
			Button btn = new Button(this);
			btn.setId(1000 + i);
			btn.setTextColor(getResources().getColor(R.color.theme_color));
			btn.setPadding(p, p, p, p);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(UIFactory.dip2px(this, 40),
					UIFactory.dip2px(this, 40));
			layoutParams.setMargins(m, m, m, m);
			btn.setLayoutParams(layoutParams);
			btn.setText(String.valueOf(i));
			btn.setOnClickListener(this);
			mLayoutChannel.addView(btn);
		}

	}

	private void stopMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.stopPlayback();
			mFunVideoView.stopRecordVideo();
		}
	}

	private void pauseMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.pause();
		}
	}

	private void resumeMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.resume();
		}
	}

	private void switchMediaStream() {
		if (null != mFunVideoView) {
			if (FunStreamType.STREAM_MAIN == mFunVideoView.getStreamType()) {
				mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
			} else {
				mFunVideoView.setStreamType(FunStreamType.STREAM_MAIN);
			}

			// 重新播放
			mFunVideoView.stopPlayback();
			playRealMedia();
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_PLAY_MEDIA:
				{
				playRealMedia();
			}
				break;
			case MESSAGE_AUTO_HIDE_CONTROL_BAR:
				{
				hideVideoControlBar();
			}
				break;
            case MESSAGE_TOAST_SCREENSHOT_PREVIEW:
                {
				String path = (String) msg.obj;
				toastScreenShotPreview(path);
			}
				break;
            case MESSAGE_OPEN_VOICE:
                {
                	if(mFunVideoView!=null) {
						mFunVideoView.setMediaSound(true);
					}
			}
            default:
                break;
			}
		}
	};

	private OnTouchListener mIntercomTouchLs = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			try {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					if (mCbDoubleTalk.isChecked()) {
						if (!mIsDoubleTalkPress) {
							startTalkByDoubleDirection();
							mBtnVoiceTalk_jcdp.setBackgroundResource(R.drawable.icon_voice_talk_selected_60dp);
							mIsDoubleTalkPress = true;
						}else {
							stopTalkByDoubleDirection();
							mBtnVoiceTalk_jcdp.setBackgroundResource(R.drawable.icon_voice_talk_normal_60dp);
							mIsDoubleTalkPress = false;
						}
					}else {
						startTalkByHalfDuplex();
					}
				} else if (!mCbDoubleTalk.isChecked()
							&& arg1.getAction() == MotionEvent.ACTION_UP) {
					stopTalkByHalfDuplex();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};

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
		mBtnVoiceTalk_jcdp.setBackgroundResource(R.drawable.icon_voice_talk);
	}

    private void openVoiceChannel(){

        if (mBtnVoice.getVisibility() == View.VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 100), 0);
            ani.setDuration(200);
            mBtnVoiceTalk_jcdp.setAnimation(ani);
            mBtnVoiceTalk_jcdp.setVisibility(View.VISIBLE);
            mBtnVoice.setVisibility(View.GONE);
            mFunVideoView.setMediaSound(false);			//关闭本地音频
        }
    }

    private void closeVoiceChannel(int delayTime){

        if (mBtnVoiceTalk_jcdp.getVisibility() == View.VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 100));
            ani.setDuration(200);
            mBtnVoiceTalk_jcdp.setAnimation(ani);
            mBtnVoiceTalk_jcdp.setVisibility(View.GONE);
            mBtnVoice.setVisibility(View.VISIBLE);
			destroyTalk();
            mHandler.sendEmptyMessageDelayed(MESSAGE_OPEN_VOICE, delayTime);
        }
    }

	private void openVoiceChannel1(){
			mFunVideoView.setMediaSound(false);			//关闭本地音频
	}

	private void closeVoiceChannel1(int delayTime){
			destroyTalk();
			mHandler.sendEmptyMessageDelayed(MESSAGE_OPEN_VOICE, delayTime);
	}

	/**
	 * 显示输入设备密码对话框
	 */
	private void showInputPasswordDialog() {
		DialogInputPasswd inputDialog = new DialogInputPasswd(this,
				getResources().getString(R.string.device_login_input_password), "", R.string.common_confirm,
				R.string.common_cancel) {

			@Override
			public boolean confirm(String editText) {
				// 重新以新的密码登录
				if (null != mFunDevice) {
					newPsd = editText;
					//NativeLoginPsw = editText;

					//onDeviceSaveNativePws();

					// 重新登录
					loginDevice(camera.loginName, newPsd);
				}
				return super.confirm(editText);
			}

			@Override
			public void cancel() {
				super.cancel();

				// 取消输入密码,直接退出
				finish();
			}

		};

		inputDialog.show();
	}

	private void showFishEyeInfo() {
		if ( null != mFunVideoView ) {
			String fishEyeInfo = mFunVideoView.getFishEyeFrameJSONString();
			Intent intent = new Intent();
			intent.setClass(this, ActivityDeviceFishEyeInfo.class);
			intent.putExtra("FISH_EYE_INFO", fishEyeInfo);
			intent.putExtra("DEVICE_SN", mFunDevice.getDevSn());
			this.startActivity(intent);
		}
	}
	
	public void onDeviceSaveNativePws() {
		FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(),
				NativeLoginPsw);
		// 库函数方式本地保存密码
		if (FunSupport.getInstance().getSaveNativePassword()) {
			FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", NativeLoginPsw);
			// 如果设置了使用本地保存密码，则将密码保存到本地文件
		}
	}

	@Override
	public void onDeviceLoginSuccess(final FunDevice funDevice) {
		hideWaitDialog();
		System.out.println("TTT---->>>> loginsuccess");
		
		if (null != mFunDevice && null != funDevice) {
			if (mFunDevice.getId() == funDevice.getId()) {
				
				// 登录成功后立刻获取SystemInfo
				// 如果不需要获取SystemInfo,在这里播放视频也可以:playRealMedia();
				requestSystemInfo();
			}
		}
	}

	@Override
	public void onDeviceLoginFailed(final FunDevice funDevice, final Integer errCode) {
		// 设备登录失败
		hideWaitDialog();
		showToast(FunError.getErrorStr(errCode));

		// 如果账号密码不正确,那么需要提示用户,输入密码重新登录
		if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
			showInputPasswordDialog();
		}
	}

	@Override
	public void onDeviceGetConfigSuccess(final FunDevice funDevice, final String configName, final int nSeq) {
		int channelCount = 0;
		if (SystemInfo.CONFIG_NAME.equals(configName)) {
			
			if (!isGetSysFirst) {
				return;
			}
			
			// 更新UI
			//此处为示例如何取通道信息，可能会增加打开视频的时间，可根据需求自行修改代码逻辑
			if (funDevice.channel == null) {
				FunSupport.getInstance().requestGetDevChnName(funDevice);
				requestSystemInfo();
				return;
			}
			channelCount = funDevice.channel.nChnCount;
			// if (channelCount >= 5) {
			// channelCount = 5;
			// }
			if (channelCount > 1) {
				mChannelCount = channelCount;

				addChannelBtn(channelCount);
			}

			hideWaitDialog();

			// 设置允许播放标志
			mCanToPlay = true;
			
			isGetSysFirst = false;
			
			showToast(getType(funDevice.getNetConnectType()));
			
			// 获取信息成功后,如果WiFi连接了就自动播放
			// 此处逻辑客户自定义
//			if (MyUtils.detectWifiNetwork(this)) {
				playRealMedia();
//			} else {
//				showToast(R.string.meida_not_auto_play_because_no_wifi);
//			}

			// 如果支持云台控制,在获取到SystemInfo之后,获取预置点信息,如果不需要云台控制/预置点功能功能,可忽略之
			if (mFunDevice.isSupportPTZ()) {
				requestPTZPreset();
			}
		} else if (OPPTZPreset.CONFIG_NAME.equals(configName)) {

		} else if (OPPTZControl.CONFIG_NAME.equals(configName)) {
			Toast.makeText(getApplicationContext(), R.string.user_set_preset_succeed, Toast.LENGTH_SHORT).show();

			// 重新获取预置点列表
//			requestPTZPreset();
		}

		//打开双向对讲
		openVoiceChannel1();
	}

	private String getType(int i){
		switch (i) {
		case 0:
			return "P2P";
		case 1:
			return "Forward";
		case 2:
			return "IP";
		case 5:
			return "RPS";
		default:
			return "";
		}
	}

	@Override
	public void onDeviceGetConfigFailed(final FunDevice funDevice, final Integer errCode) {
		showToast(FunError.getErrorStr(errCode));
		if (errCode == -11406) {
			funDevice.invalidConfig(OPPTZPreset.CONFIG_NAME);
		}
	}


	@Override
	public void onDeviceSetConfigSuccess(final FunDevice funDevice,
			final String configName) {
			Log.d("-DCA-",configName);
	}


	@Override
	public void onDeviceSetConfigFailed(final FunDevice funDevice, 
			final String configName, final Integer errCode) {
		if (OPPTZControl.CONFIG_NAME.equals(configName)) {
			Toast.makeText(getApplicationContext(), R.string.user_set_preset_fail, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDeviceChangeInfoSuccess(final FunDevice funDevice) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeviceChangeInfoFailed(final FunDevice funDevice, final Integer errCode) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeviceOptionSuccess(final FunDevice funDevice, final String option) {
		// TODO Auto-generated method stub
		Log.d("-DCA-",option);
	}

	@Override
	public void onDeviceOptionFailed(final FunDevice funDevice, final String option, final Integer errCode) {
		// TODO Auto-generated method stub
		Log.d("-DCA-",option);
	}

	@Override
	public void onDeviceFileListChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub

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
	}
	@Override
	public void onGestureStop() {
		onContrlPTZ1(EPTZCMD.TILT_DOWN, true);
	}

	@Override
	public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

	}


	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// 播放失败
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
	public boolean onInfo(MediaPlayer arg0, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			mTextVideoStat.setText(R.string.media_player_buffering);
			mTextVideoStat.setVisibility(View.VISIBLE);
		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			mTextVideoStat.setVisibility(View.GONE);
		}
		return true;
	}

	private OnTouchListener onPtz_up = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				Log.i("test", "onPtz_up -- KeyEvent.ACTION_DOWN");
				bstop = false;
				nPTZCommand = EPTZCMD.TILT_UP;
				break;
			case KeyEvent.ACTION_UP:
				Log.i("test", "onPtz_up -- KeyEvent.ACTION_UP");
				nPTZCommand = EPTZCMD.TILT_UP;
				bstop = true;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.TILT_UP;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_down = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.TILT_DOWN;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.TILT_DOWN;
				onContrlPTZ1(nPTZCommand, bstop);
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.TILT_DOWN;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_left = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.PAN_LEFT;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.PAN_LEFT;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.PAN_LEFT;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_right = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};


	private void onContrlPTZ1(int nPTZCommand, boolean bStop) {
		FunSupport.getInstance().requestDevicePTZControl(mFunDevice,
	    		nPTZCommand, bStop, mFunDevice.CurrChannel);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		mFunDevice.CurrChannel = arg1;
		System.out.println("TTTT----"+mFunDevice.CurrChannel);
		if (mCanToPlay) {
			playRealMedia();
		}
	}


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

	private OnTouchListener mLockerLockTouchLs = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			try {
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
						if (!mIsLocked) {
							mBtnLockLocker.setImageResource(R.drawable.icon_unlock_btn_normal);
							mIsLocked = true;
						} else {
							mBtnLockLocker.setImageResource(R.drawable.icon_lock_btn_normal);
							mIsLocked = false;
						}
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};

	//获取doorId最大值
	private int MaxDoorNo(ArrayList<Door> sampleList)
	{
		try
		{
			int maxDevation = 0;
			int totalCount = sampleList.size();
			if (totalCount >= 1)
			{
				int max =sampleList.get(0).no;
				for (int i = 0; i < totalCount; i++)
				{
					int temp = sampleList.get(i).no;
					if (temp > max)
					{
						max = temp;
					}
				} maxDevation = max;
			}
			return maxDevation;
		}
		catch (Exception ex)
		{
			throw ex;
		}

	}
}