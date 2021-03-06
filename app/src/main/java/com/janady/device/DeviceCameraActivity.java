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
import androidx.core.graphics.ColorUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.DialogInputPasswd;
import com.example.common.UIFactory;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.MyApplication;
import com.example.funsdkdemo.adapter.GridCameraChannelsPreviewsAdapter;
import com.janady.AppManager;
import com.lkd.smartlocker.R;
import com.example.funsdkdemo.devices.ActivityDeviceFishEyeInfo;
import com.example.funsdkdemo.devices.ActivityGuideDevicePictureList;
import com.example.funsdkdemo.devices.ActivityGuideDeviceSportPicList;
import com.example.funsdkdemo.devices.monitor.ActivityGuideDevicePreview;
import com.example.funsdkdemo.devices.playback.ActivityGuideDeviceRecordList;
import com.example.funsdkdemo.devices.settings.ActivityGuideDeviceSetup;
import com.example.funsdkdemo.devices.tour.view.TourActivity;
import com.janady.HomeActivity;
import com.janady.database.model.Camera;
import com.janady.view.CustomCircle;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

//import static com.lib.funsdk.support.models.FunDevType.EE_DEV_SPORTCAMERA;

/**
 * Demo: 监控类设备播放控制等 
 * @author Administrator
 */
public class DeviceCameraActivity
				extends ActivityDemo
				implements OnClickListener,
							FunVideoView.GestureListner,
							OnFunDeviceOptListener,
							OnPreparedListener,
							OnErrorListener,
							OnInfoListener{


	private RelativeLayout mLayoutTop = null;
	private TextView mTextTitle = null;
	private ImageButton mBtnBack = null;
	private ImageButton mBtnSetup = null;

	private FunDevice mFunDevice = null;

	private RelativeLayout mLayoutVideoWnd = null;
	private FunVideoView mFunVideoView = null;
	private LinearLayout mVideoControlLayout = null;
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
    private Button mBtnDevPreview = null;

	private View mSplitView = null;
	private CheckBox mCbDoubleTalk = null;
	private RelativeLayout mLayoutRecording = null;

	//private RoundMenuView roundMenuView = null;
	private CustomCircle roundMenuView = null;

	private LinearLayout mLayoutControls = null;
	private LinearLayout mLayoutChannel = null;
	private RelativeLayout mBtnVoiceTalk = null;
	private RelativeLayout mBtnVoiceTalk_jcdp = null;
	private RelativeLayout mLayoutVideoScreen = null;

	private Button mBtnVoice = null;
    private ImageButton mBtnQuitVoice = null;
	private ImageButton mBtnDevCapture = null;
	private ImageButton mBtnDevRecord = null;
	private ImageButton mBtnVoic_jcdp= null;

	private ImageButton mBtnSkipNext = null;
    private ImageButton mBtnSkipPrevious = null;
	private ImageButton mBtnDevSound = null;

	private RelativeLayout mLayoutDirectionControl = null;
	private ImageButton mPtz_up = null;
	private ImageButton mPtz_down = null;
	private ImageButton mPtz_left = null;
	private ImageButton mPtz_right = null;

	private TextView mTextVideoStat = null;
	private AlertDialog alert = null;
	private AlertDialog.Builder builder = null;

	private String preset = null;
	private int mChannelCount;
	private boolean isGetSysFirst = true;

	private String newPsd = "";

	private boolean mIsLocked = false;

	private final int MESSAGE_PLAY_MEDIA = 0x100;
	private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x102;
	private final int MESSAGE_TOAST_SCREENSHOT_PREVIEW = 0x103;
	private final int MESSAGE_OPEN_VOICE = 0x104;
	private final int MESSAGE_PLAY_MEDIA_MULIT = 0x105;
	private final int MESSAGE_STOP_MEDIA_MULIT = 0x106;

	// 自动隐藏底部的操作控制按钮栏的时间
	private final int AUTO_HIDE_CONTROL_BAR_DURATION = 3000;

	private TalkManager mTalkManager = null;

	private boolean mCanToPlay = false;

	public String NativeLoginPsw; //本地密码
	private boolean mIsDoubleTalkPress;
	private TourActivity mTourFragment;
	//-----------------------------------------------
	//分屏预览专用变量
	private TextView textStart;
	private GridView gridview;
	private FunVideoView funVideoView;
	private GridCameraChannelsPreviewsAdapter cadapter;
	private List<TextView> textvlist = new ArrayList<TextView>();
	private List<FunVideoView> funvideovlist = new ArrayList<FunVideoView>();
	private int currFunDeviceIdx =0 ;
	private boolean mulitScreenNow = false;

	private Camera camera;
    List<Camera> cams = null;
    private int currIndex = 0;
	private boolean isMute = false;
	private Context mContext;

	private Camera getCamera(int index){
        if(cams.size()>0 && index>=0 && index<cams.size()){
            return cams.get(index);
        }else{
            return null;
        }
    }
	private void initCamera(int devId,String sn, String mac){
        //cams = MyApplication.liteOrm.query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_SN, sn));
		/*cams = MyApplication.liteOrm.query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_MAC, mac));
		if(cams.size()>0){
			camera = cams.get(0);
		}*/

        mulitScreenNow = false;

        mBtnSkipPrevious.setEnabled(true);
        mBtnSkipNext.setEnabled(true);

        cams = MyApplication.liteOrm.query(Camera.class);
        if(cams.size()>0){
            currIndex = 0;
            for(int i=0;i<cams.size();i++){
                if(cams.get(i).sn.equals(sn)) {
                    camera = cams.get(i);
                    currIndex = i;
                }else{
					if(cams.get(i).mac.equals(mac)) {
						camera = cams.get(i);
						currIndex = i;
					}
				}
            }
        }

		//mTextTitle.setText(mFunDevice.devName);
		if(!camera.isOnline){
			mTextTitle.setText(camera.sceneName+ getString(R.string.CAMERA_OFFLINE));
		}else{
			mTextTitle.setText(camera.sceneName+ getString(R.string.CAMERA_ONLINE));
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
        isMute = true;
		mBtnDevSound.setImageResource(R.drawable.icon_btn_mute_selector);

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
			mSplitView.setVisibility(VISIBLE);
			mLayoutDirectionControl.setVisibility(VISIBLE);
		}


		mFunVideoView.setGestureListner(this);
		mFunVideoView.setOnPreparedListener(this);
		mFunVideoView.setOnErrorListener(this);
		mFunVideoView.setOnInfoListener(this);
		mVideoControlLayout = (LinearLayout) findViewById(R.id.layoutVideoControl);

		// 允许横竖屏切换
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

		showVideoControlBar();
		//hideVideoControlBar();

		mFunVideoView.setMediaSound(true);			//关闭本地音频

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
			//loginDevice(camera.loginName, camera.loginPsw);
			loginDevice();
		} else {
			requestSystemInfo();
		}

		// 注册设备操作回调
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppManager.getAppManager().addActivity(this);

		setContentView(R.layout.device_camera_activity);

		mContext = this;

		mLayoutTop = (RelativeLayout) findViewById(R.id.layoutTop);
		mLayoutVideoScreen = (RelativeLayout) findViewById(R.id.rl_VideoScreen);


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
        mBtnDevPreview = (Button) findViewById(R.id.btnDevPreview);
		//mBtnLocker = (ImageButton) findViewById(R.id.btnLocker);
		mBtnDevSound = (ImageButton) findViewById(R.id.btnDevSound);

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
        mBtnDevPreview.setOnClickListener(this);
		mBtnSkipNext.setOnClickListener(this);
		mBtnSkipPrevious.setOnClickListener(this);
		mBtnDevSound.setOnClickListener(this);

		mTextVideoStat = (TextView) findViewById(R.id.textVideoStat);

		mBtnVoiceTalk_jcdp = (RelativeLayout) findViewById(R.id.btnVoiceTalk_jcdp);
		mBtnVoiceTalk_jcdp.setVisibility(VISIBLE);

		mBtnVoiceTalk = (RelativeLayout) findViewById(R.id.btnVoiceTalk);
		mBtnVoiceTalk.setVisibility(GONE);


		mBtnVoice = (Button) findViewById(R.id.Btn_Talk_Switch);
		mBtnVoice.setVisibility(GONE);

        mBtnQuitVoice = (ImageButton) findViewById(R.id.btn_quit_voice);
        mBtnQuitVoice.setVisibility(GONE);

		mBtnDevCapture = (ImageButton) findViewById(R.id.btnDevCapture);
		mBtnDevRecord = (ImageButton) findViewById(R.id.btnDevRecord);
		mBtnGetPreset = (Button) findViewById(R.id.btnGetPreset);
		mBtnSetPreset = (Button) findViewById(R.id.btnSetPreset);
		

		mSplitView = findViewById(R.id.splitView);
		mCbDoubleTalk = findViewById(R.id.cb_double_talk_switch);
		mCbDoubleTalk.setChecked(false); //false单向对讲，true双向对讲
		mCbDoubleTalk.setVisibility(GONE);


		mLayoutDirectionControl = (RelativeLayout) findViewById(R.id.layoutDirectionControl);
		/*mPtz_up = (ImageButton) findViewById(R.id.ptz_up);
		mPtz_down = (ImageButton) findViewById(R.id.ptz_down);
		mPtz_left = (ImageButton) findViewById(R.id.ptz_left);
		mPtz_right = (ImageButton) findViewById(R.id.ptz_right);*/

		mPtz_up = (ImageButton) findViewById(R.id.btnCamUp);
		mPtz_up.setVisibility(GONE);
		mPtz_down = (ImageButton) findViewById(R.id.btnCamDown);
		mPtz_down.setVisibility(GONE);
		mPtz_left = (ImageButton) findViewById(R.id.btnCamLeft);
		mPtz_left.setVisibility(GONE);
		mPtz_right = (ImageButton) findViewById(R.id.btnCamRight);
		mPtz_right.setVisibility(GONE);

		mBtnVoiceTalk.setOnClickListener(this);
		mBtnVoiceTalk.setOnTouchListener(mIntercomTouchLs);
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

		//mBtnLocker.setImageResource(R.drawable.icon_button_empty);
		//mBtnLocker.setOnClickListener(this);
		//mBtnLocker.setOnTouchListener(mLockerTouchLs);

		mLayoutControls = (LinearLayout) findViewById(R.id.layoutFunctionControl);
		mLayoutChannel = (LinearLayout) findViewById(R.id.layoutChannelBtn);


		mTextStreamType = (TextView) findViewById(R.id.textStreamStat);

		setNavagateRightButton(R.layout.imagebutton_settings);
		mBtnSetup = (ImageButton) findViewById(R.id.btnSettings);
		mBtnSetup.setOnClickListener(this);

		int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
		String sceneName = getIntent().getStringExtra("FUN_DEVICE_SCENE");
		String sn = getIntent().getStringExtra("FUN_DEVICE_SN");
		String mac = getIntent().getStringExtra("FUN_DEVICE_MAC");

		initCamera(devId, sn, mac);

		setStatusBar();
	}


	@Override
	protected void onDestroy() {

		stopMedia();
		stopMediaMuilt();

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


	@SuppressLint("SourceLockedOrientationActivity")
	@Override
	public void onBackPressed() {
		// 如果当前是横屏，返回时先回到竖屏
		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			return;
		}

		finish();

		Intent intent = new Intent();
		intent.putExtra("action","recreate");
		intent.setClass(mContext, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
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

	private void showMuiltPreview(int showNum){
		gridview = (GridView) findViewById(R.id.Frames_grid_view);
		mLayoutVideoWnd.setVisibility(GONE);
		gridview.setVisibility(VISIBLE);
		cadapter = new GridCameraChannelsPreviewsAdapter(this, showNum);
		gridview.setAdapter(cadapter);

		mulitScreenNow = true;
		mBtnSkipPrevious.setEnabled(false);
		mBtnSkipNext.setEnabled(false);

		Message message = new Message();
		message.what = MESSAGE_PLAY_MEDIA_MULIT;
		mHandler.sendMessageDelayed(message, 100);
	}

	private class OnItemViewTouchListener implements View.OnTouchListener {

		private int mChannel;

		public OnItemViewTouchListener(int channel){
			mChannel = channel;
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("TTT-->>> event = " + event.getAction());
			if (event.getAction() == MotionEvent.ACTION_UP) {
			    currFunDeviceIdx = mChannel;
				Message message = new Message();
				message.what = MESSAGE_STOP_MEDIA_MULIT;
				mHandler.sendMessageDelayed(message, 100);
				//选中后，显示打开独立的摄像头
				//this.setResult(mChannel, null);
				//ActivityGuideDevicePreview.this.finish();
			}

			return true;
			//巨坑！为false时只触发ACTION_DOWN
			//return false;
		}

	}

	public void playrealvideoMuilt(){
		int cam_count = cams.size();
		mFunVideoView.stopPlayback();
		mFunVideoView.stopRecordVideo();
		mFunVideoView.invalidate();
		mFunVideoView.clearVideo();
		FunDevice fundev= null;

        mTextTitle.setText("实时预览");

		for (int i = 0; i < cam_count; i++) {
			View v = gridview.findViewWithTag(i);
			if ( null != v ) {
				funVideoView = (FunVideoView)v.findViewById(R.id.funVideoView1);
				textStart = (TextView) v.findViewById(R.id.textVideoStat1);
			}
			funVideoView.clearVideo();
			funVideoView.setOnErrorListener(this);
			final int finalI = i;
			funVideoView.setOnInfoListener(new OnInfoListener() {
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
						textvlist.get(finalI).setText(R.string.media_player_buffering);
						textvlist.get(finalI).setVisibility(VISIBLE);
					} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
						textvlist.get(finalI).setVisibility(GONE);
					}

					return true;
				}
			});
			funVideoView.setOnTouchListener(new OnItemViewTouchListener(i));
            funVideoView.setGestureListner(this);
			funVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
			funvideovlist.add(funVideoView);

			// 显示状态: 正在打开视频...
			textStart.setText(R.string.media_player_opening);
			textStart.setVisibility(VISIBLE);
			textvlist.add(textStart);

			//cadapter.notifyDataSetInvalidated();
			if(cams.get(i).isOnline) {
				fundev = FunSupport.getInstance().findDeviceById(cams.get(i).devId);
				if (null == fundev) {
					fundev = FunSupport.getInstance().findLanDevice(cams.get(i).sn);
					if (fundev == null) {
						fundev = FunSupport.getInstance().findLanDevice(cams.get(i).name);
						if (fundev== null) {
							fundev = FunSupport.getInstance().findDeviceBySn(cams.get(i).sn);
							if (fundev == null) {
								fundev = FunSupport.getInstance().findTempDevice(cams.get(i).mac);
								if (fundev == null) {
									//finish();
									return;
								}
							}
						}
					}
				}

				if (fundev.isRemote) {
					funVideoView.setRealDevice(fundev.getDevSn(), fundev.CurrChannel);
				} else {
					String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
					funVideoView.setRealDevice(deviceIp, fundev.CurrChannel);
				}

				//gridview.setSelection(i);
			}
		}
	}

	private void stopMediaMuilt() {
		if(gridview!=null) {
			for (int i = 0; i < cams.size(); i++) {
				View v = gridview.findViewWithTag(i);
				if (null != v) {
					funVideoView = (FunVideoView) v.findViewById(R.id.funVideoView1);
					textStart = (TextView) v.findViewById(R.id.textVideoStat1);
				}
				funVideoView.stopRecordVideo();
				funVideoView.stopPlayback();
				funVideoView.clearVideo();
				funVideoView.setVisibility(GONE);
			}
		}

		if (funvideovlist != null) {
			for (int i = 0; i < funvideovlist.size(); i++) {
				if (null != funvideovlist.get(i)) {
					/*funvideovlist.get(i).stopPlayback();
					funvideovlist.get(i).stopRecordVideo();
					funvideovlist.get(i).clearVideo();
					funvideovlist.get(i).setVisibility(GONE);*/
					funvideovlist.remove(i);
					textvlist.remove(i);
				}
			}

			funvideovlist = null;
			textvlist = null;
		}

		if(funVideoView != null) {
			funVideoView.stopPlayback();
			funVideoView.stopRecordVideo();
			funVideoView.clearVideo();
			funVideoView.setVisibility(GONE);
			funVideoView.invalidate();
			funVideoView = null;
		}

		cadapter = null;
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
		case R.id.btnDevSound: // 声音播放
		{
			if(isMute){
				mBtnDevSound.setImageResource(R.drawable.icon_btn_sound_selector);
				//mBtnDevSound.setBackgroundResource(R.drawable.icon_btn_sound_normal);
				mFunVideoView.setMediaSound(true);
				isMute = false;
			}else{
				mBtnDevSound.setImageResource(R.drawable.icon_btn_mute_selector);
				mFunVideoView.setMediaSound(false);
				isMute = true;
			}
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
				//showFishEyeInfo();
				//startDevicesPreview();
				//showMuiltPreview(cams.size());
			}
			break;
        case R.id.btnDevPreview:
            {
                showMuiltPreview(cams.size());
            }
            break;
        case R.id.btnDevPre:
            {
                changePrevCamera();
            }
            break;
        case R.id.btnDevNext:
            {
            	changeNextCamera();
            }
            break;
        default:
            break;
		}
	}

	private void changePrevCamera(){
		currIndex --;
		if(currIndex<0){
			showToast(R.string.CAMERA_FIRST);
			currIndex=0;
			return;
		}

		if(getCamera(currIndex)!=null){
			camera=getCamera(currIndex);
			initCamera(camera.devId, camera.sn, camera.mac);
			if(camera.isOnline) {
				initCamera(camera.devId, camera.sn, camera.mac);
			}else{
				changePrevCamera();
			}
		}else{
			currIndex++;
			Log.d("---DCA----","切换摄像机失败");
		}
	}
	private void changeNextCamera(){
		currIndex ++;
		if(currIndex > cams.size()-1){
			showToast(R.string.CAMERA_LAST);
			currIndex =cams.size()-1;
			return;
		}

		if(getCamera(currIndex)!=null){
			camera=getCamera(currIndex);
			initCamera(camera.devId, camera.sn, camera.mac);
			if(camera.isOnline) {
				initCamera(camera.devId, camera.sn, camera.mac);
			}else{
				changeNextCamera();
			}
		}else{
			currIndex --;
			Log.d("---DCA----","切换摄像机失败");
		}
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
			mLayoutRecording.setVisibility(VISIBLE);
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
		if (mVideoControlLayout.getVisibility() != VISIBLE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 42), 0);
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(VISIBLE);
		}

		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏情况下,顶部标题栏也动画显示
			TranslateAnimation ani = new TranslateAnimation(0, 0, -UIFactory.dip2px(this, 48), 0);
			ani.setDuration(200);
			mLayoutTop.startAnimation(ani);
			mLayoutTop.setVisibility(VISIBLE);
		} else {
			mLayoutTop.setVisibility(VISIBLE);
		}

		// 显示后设置10秒后自动隐藏
		mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
		mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_CONTROL_BAR, AUTO_HIDE_CONTROL_BAR_DURATION);
	}

	private void hideVideoControlBar() {
		if (mVideoControlLayout.getVisibility() != GONE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 42));
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(GONE);
		}

		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏情况下,顶部标题栏也隐藏
			TranslateAnimation ani = new TranslateAnimation(0, 0, 0, -UIFactory.dip2px(this, 48));
			ani.setDuration(200);
			mLayoutTop.startAnimation(ani);
			mLayoutTop.setVisibility(GONE);
		}

		// 隐藏后清空自动隐藏的消息
		mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
	}

	private void showAsLandscape() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 隐藏底部的控制按钮区域
		mLayoutControls.setVisibility(GONE);

		// 视频窗口全屏显示
		RelativeLayout.LayoutParams lpVs = (RelativeLayout.LayoutParams) mLayoutVideoScreen.getLayoutParams();
		lpVs.height = LayoutParams.MATCH_PARENT;

		//RelativeLayout.LayoutParams lpGd = (RelativeLayout.LayoutParams) gridview.getLayoutParams();
		//lpGd.height = LayoutParams.MATCH_PARENT;

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
		mLayoutTop.setVisibility(VISIBLE);

		// 视频显示为小窗口
		RelativeLayout.LayoutParams lpVs = (RelativeLayout.LayoutParams) mLayoutVideoScreen.getLayoutParams();
		lpVs.height = UIFactory.dip2px(this, 280);

		//RelativeLayout.LayoutParams lpGd = (RelativeLayout.LayoutParams) gridview.getLayoutParams();
		//lpGd.height = UIFactory.dip2px(this, 280);

		RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
		lpWnd.height = UIFactory.dip2px(this, 240);
		lpWnd.topMargin = UIFactory.dip2px(this, 48);
		// lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
		mLayoutVideoWnd.setLayoutParams(lpWnd);

		// 显示底部的控制按钮区域
		mLayoutControls.setVisibility(VISIBLE);

		mBtnScreenRatio.setText(R.string.device_opt_fullscreen);
	}

	/**
	 * 切换视频全屏/小视频窗口(以切横竖屏切换替代)
	 */
	@SuppressLint("SourceLockedOrientationActivity")
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

	/**
	 * 打开设备配置
	 */
	private void startDeviceSetup() {
		Intent intent = new Intent();
		intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
		intent.setClass(this, ActivityGuideDeviceSetup.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/***
	 * 打开 多通道预览
	 */
	private void startDevicesPreview(){
		Intent intent = new Intent();
		intent.putExtra("FUNDEVICE_ID", mFunDevice.getId());
		intent.setClass(this, ActivityGuideDevicePreview.class);
		startActivityForResult(intent, 0);
	}

	private class OnVideoViewTouchListener implements OnTouchListener {

		//@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("TTT-->>> event = " + event.getAction());
			if (event.getAction() == MotionEvent.ACTION_UP) {
			//if (event.getAction() == MotionEvent.ACTION_DOWN) {

				// 显示或隐藏视频操作菜单
				if (mVideoControlLayout.getVisibility() == VISIBLE) {
					hideVideoControlBar();
				} else {
					showVideoControlBar();
				}
			}

			return true;
			//巨坑！！！false不触发action_up
			//return false;
		}

	}

	private void loginDevice() {
		showWaitDialog();

		FunSupport.getInstance().requestDeviceLogin(mFunDevice);
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
		mTextVideoStat.setVisibility(VISIBLE);

		if (mFunDevice.isRemote) {
			mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
		} else {
			String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
			mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
		}

		// 打开声音
		if(isMute) {
			mFunVideoView.setMediaSound(false);
		}else{
			mFunVideoView.setMediaSound(true);
		}

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
			case MESSAGE_STOP_MEDIA_MULIT:
			{
				stopMediaMuilt();

				gridview.setVisibility(GONE);
				gridview = null;
				mLayoutVideoWnd.setVisibility(VISIBLE);
				mLayoutVideoWnd.refreshDrawableState();
				mFunVideoView.clearVideo();
				mFunVideoView = null;
				mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_MEDIA, 1000);
				initCamera(cams.get(currFunDeviceIdx).devId, cams.get(currFunDeviceIdx).sn, cams.get(currFunDeviceIdx).mac);
			}
				break;
			case MESSAGE_PLAY_MEDIA_MULIT:
			{
				playrealvideoMuilt();
			}
					break;
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
				mFunVideoView.setMediaSound(true);
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
					openVoiceChannel();
					if (mCbDoubleTalk.isChecked()) {
						if (!mIsDoubleTalkPress) {
							startTalkByDoubleDirection();
							mBtnVoiceTalk.setBackgroundResource(R.drawable.icon_voice_talk_selected_60dp);
							mBtnVoiceTalk_jcdp.setBackgroundResource(R.drawable.icon_voice_talk_selected_60dp);
							mIsDoubleTalkPress = true;
						}else {
							stopTalkByDoubleDirection();
							mBtnVoiceTalk.setBackgroundResource(R.drawable.icon_voice_talk_normal_60dp);
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
			mBtnDevSound.setImageResource(R.drawable.icon_btn_mute_selector);
			mFunVideoView.setMediaSound(false);
			isMute = true;
			mTalkManager.startTalkByHalfDuplex();
		}
	}

	/**
	 * 关闭单向对讲
	 */
	private void stopTalkByHalfDuplex() {
		if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
			mTalkManager.stopTalkByHalfDuplex();
			mBtnDevSound.setImageResource(R.drawable.icon_btn_sound_selector);
			mFunVideoView.setMediaSound(false);
			isMute = false;
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
		mBtnVoiceTalk_jcdp.setBackgroundResource(R.drawable.icon_voice_talk);
	}

    private void openVoiceChannel(){

        if (mBtnVoice.getVisibility() == VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 100), 0);
            ani.setDuration(200);
            mBtnVoiceTalk.setAnimation(ani);
            mBtnVoiceTalk.setVisibility(VISIBLE);
			mBtnVoiceTalk_jcdp.setAnimation(ani);
			mBtnVoiceTalk_jcdp.setVisibility(VISIBLE);
            mBtnVoice.setVisibility(GONE);
            mFunVideoView.setMediaSound(false);			//关闭本地音频
        }
    }

    private void closeVoiceChannel(int delayTime){

        if (mBtnVoiceTalk.getVisibility() == VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 100));
            ani.setDuration(200);
            mBtnVoiceTalk.setAnimation(ani);
            mBtnVoiceTalk.setVisibility(GONE);
			mBtnVoiceTalk_jcdp.setAnimation(ani);
			mBtnVoiceTalk_jcdp.setVisibility(GONE);
            mBtnVoice.setVisibility(VISIBLE);
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
	 * 如果强制弹出密码输入框，需要把onDeviceSaveNativepws禁用
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
					NativeLoginPsw = editText;

					onDeviceSaveNativePws();

					// 重新登录
					//loginDevice(camera.loginName, newPsd);
					loginDevice();
				}
				return super.confirm(editText);
			}

			@Override
			public void cancel() {
				super.cancel();

				// 取消输入密码,直接退出
				//finish();
				onBackPressed();
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

				camera.loginPsw = newPsd;
				MyApplication.liteOrm.save(camera);
				sleep(300);

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
		//openVoiceChannel1();
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
		hideVideoControlBar();
	}

	@Override
	public void onGestureLeft() {
		onContrlPTZ1(EPTZCMD.PAN_LEFT, false);
		hideVideoControlBar();
	}

	@Override
	public void onGestureUp() {
		onContrlPTZ1(EPTZCMD.TILT_UP, false);
		hideVideoControlBar();
	}

	@Override
	public void onGestureDown() {
		onContrlPTZ1(EPTZCMD.TILT_DOWN, false);
		hideVideoControlBar();
	}
	@Override
	public void onGestureStop() {
		onContrlPTZ1(EPTZCMD.TILT_DOWN, true);
		showVideoControlBar();
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
			mTextVideoStat.setVisibility(VISIBLE);
		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			mTextVideoStat.setVisibility(GONE);
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

}
