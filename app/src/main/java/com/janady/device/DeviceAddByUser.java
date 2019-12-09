package com.janady.device;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.TestFragment;
import com.example.common.DialogInputPasswd;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.ListAdapterSimpleFunDevice;
import com.example.funsdkdemo.MyApplication;
import com.janady.AppManager;
import com.janady.HomeActivity;
import com.janady.utils.MqttUtil;
import com.janady.setup.FragmentUserLogin;
import com.lib.funsdk.support.config.ModifyPassword;
import com.lkd.smartlocker.R;
import com.google.zxing.activity.CaptureActivity;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.janady.AppConstants;
import com.janady.Dialogs;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.database.model.WifiRemoter;
import com.janady.lkd.BleLocker;
import com.janady.lkd.BleLockerStatus;
import com.janady.lkd.ClientManager;
import com.janady.lkd.WifiRemoterBoard;
import com.janady.view.PullRefreshListView;
import com.janady.view.PullToRefreshFrameLayout;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnAddSubDeviceResultListener;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunLoginType;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fogcloud.sdk.mdns.api.MDNS;
import io.fogcloud.sdk.mdns.helper.SearchDeviceCallBack;

import static com.lib.funsdk.support.models.FunDevType.EE_DEV_BLUETOOTH;
import static com.lib.funsdk.support.models.FunDevType.EE_DEV_BOUTIQUEROTOT;
import static com.lib.funsdk.support.models.FunDevType.EE_DEV_NORMAL_MONITOR;
import static com.lib.funsdk.support.models.FunDevType.EE_DEV_OW_REMOTER;


public class DeviceAddByUser extends ActivityDemo implements OnClickListener, OnFunDeviceListener, OnFunDeviceOptListener, OnItemSelectedListener, OnItemClickListener,  OnAddSubDeviceResultListener {

	private Context mcontext = this;

	private TextView mTextTitle = null;
	private ImageButton mBtnBack = null;
    private TextView mTextTip = null;
	
	private Spinner mSpinnerDevType = null;
	private EditText mEditDevSN;
	private EditText mEditSceneName;
	private EditText mEditPassword;
	private Button mBtnDevAdd = null;
	private ImageButton mBtnScanQrCode = null;
	
//	private ListView mListViewDev = null;
	private PullToRefreshFrameLayout mRefreshLayout;
	private PullRefreshListView mListViewDev = null;
	private ListAdapterSimpleFunDevice mAdapterDev = null;
	
	private FunDevice mFunDevice = null;
	private FunDevType mCurrDevType = null;

    private List<SearchResult> mBleDevices;
    private List<WifiRemoterBoard> mWifiRemoterBoards;

    private boolean isBleScanning = false;

    private Bluetooth mBluetooth;
    private Camera mCamera;
	private WifiRemoterBoard mWifiRemoterBoard;
    private WifiRemoter mWifiRemoter;

    private String camOldPsw;
    private String bleOldPsw;
	private String inputPwd = "";

    private BleLocker bleLocker = null;
    private boolean needCheckSTA = false;

    private boolean isModifing = false;

    DialogInputPasswd inputDialog = null;

    private CountDownTimer countDownTimer = null;

    private int step = 0;  //0-没事，1-密码输入

	//private final int MESSAGE_DELAY_FINISH = 0x100;
	private final int MESSAGE_REFRESH_DEVICE_STATUS = 0x100;
	private final int MESSAGE_EASYLINK_DEVICE_FOUND = 0x101;

	// 定义当前支持通过序列号登录的设备类型 
	// 如果是设备类型特定的话,固定一个就可以了
	private final FunDevType[] mSupportDevTypes = {
			FunDevType.EE_DEV_NORMAL_MONITOR,
/*			FunDevType.EE_DEV_INTELLIGENTSOCKET,
			FunDevType.EE_DEV_SCENELAMP,
			FunDevType.EE_DEV_LAMPHOLDER,
			FunDevType.EE_DEV_CARMATE,
			FunDevType.EE_DEV_BIGEYE,
			FunDevType.EE_DEV_SMALLEYE,
			FunDevType.EE_DEV_BOUTIQUEROTOT,
			FunDevType.EE_DEV_SPORTCAMERA,
			FunDevType.EE_DEV_SMALLRAINDROPS_FISHEYE,
			FunDevType.EE_DEV_LAMP_FISHEYE,
			FunDevType.EE_DEV_MINIONS,
			FunDevType.EE_DEV_MUSICBOX,
			FunDevType.EE_DEV_SPEAKER,
			FunDevType.EE_DEV_LINKCENTER,
			FunDevType.EE_DEV_DASH_CAMERA,
			FunDevType.EE_DEV_POWER_STRIP,
			FunDevType.EE_DEV_FISH_FUN,
			FunDevType.EE_DEV_UFO,
			FunDevType.EE_DEV_IDR,
			FunDevType.EE_DEV_BULLET,
			FunDevType.EE_DEV_DRUM,
			FunDevType.EE_DEV_CAMERA,
			FunDevType.EE_DEV_BOUTIQUEROTOT,*/
			FunDevType.EE_DEV_BLUETOOTH,
			FunDevType.EE_DEV_OW_REMOTER

	};


	private MDNS mdns = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppManager.getAppManager().addActivity(this);

		setContentView(R.layout.jdevice_add_by_user);

		setStatusBar();

		mWifiRemoterBoards = new ArrayList<WifiRemoterBoard>();

		mBleDevices = new ArrayList<SearchResult>();
		mBluetooth = new Bluetooth();
		mCamera = new Camera();


		mTextTitle = (TextView)findViewById(R.id.textViewInTopLayout);
        mTextTip = (TextView)findViewById(R.id.textTip);
        mTextTip.setText("搜索设备：");

        mTextTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBleScanning) {
                    searchDevice();
                }else{
                    isBleScanning=false;
                    ClientManager.getClient().stopSearch();
                }
            }
        });

		mBtnBack = (ImageButton)findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);

        Intent intent = getIntent();
        int spinnerArrNo = intent.getIntExtra("DeviceTypsSpinnerNo",0);

		// 初始化设备类型选择器
		mSpinnerDevType = (Spinner)findViewById(R.id.spinnerDeviceType);
		String[] spinnerStrs = new String[mSupportDevTypes.length];
		for ( int i = 0; i < mSupportDevTypes.length; i ++ ) {
			spinnerStrs[i] = getResources().getString(mSupportDevTypes[i].getTypeStrId());
		}
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerStrs);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerDevType.setAdapter(adapter);
		//mSpinnerDevType.setSelection(0);
		//mCurrDevType = mSupportDevTypes[0];
        mSpinnerDevType.setSelection(spinnerArrNo);

        mCurrDevType = mSupportDevTypes[spinnerArrNo];
		mSpinnerDevType.setOnItemSelectedListener(this);
		
		mEditDevSN = (EditText)findViewById(R.id.editDeviceSN);
		mEditDevSN.setEnabled(false);

		mEditSceneName = (EditText)findViewById(R.id.editSceneName);
		mEditPassword = (EditText)findViewById(R.id.editDeviceLoginPassword);
		mEditPassword.setVisibility(View.VISIBLE);

		mBtnDevAdd = (Button)findViewById(R.id.devAddBtn);
		mBtnDevAdd.setOnClickListener(this);
		
		mBtnScanQrCode = (ImageButton)findViewById(R.id.btnScanCode);
		mBtnScanQrCode.setOnClickListener(this);

		mRefreshLayout = (PullToRefreshFrameLayout) findViewById(R.id.listOtherDevices);

		mListViewDev = mRefreshLayout.getPullToRefreshListView();
		mAdapterDev = new ListAdapterSimpleFunDevice(this, mCurrDevType);

		mAdapterDev.setOnClickListener(new ListAdapterSimpleFunDevice.OnClickListener() {
			@Override
			public void OnClickedBle(SearchResult searchResult) {
					needCheckSTA = true;
					List<Bluetooth> bles = MyApplication.liteOrm.query(new QueryBuilder<Bluetooth>(Bluetooth.class).whereEquals(Bluetooth.COL_MAC, searchResult.getAddress()));
					if (bles != null && bles.size() > 0) {
						/*bleOldPsw = bles.get(0).password;
						mEditDevSN.setText(bles.get(0).name);
						mEditPassword.setText(bles.get(0).password);
						mEditSceneName.setText(bles.get(0).sceneName);
						mBluetooth.isFirst = false;*/

						mTextTitle.setText("修改设备");
						mBtnDevAdd.setText("修改");
						isModifing = true;

						bleOldPsw = bles.get(0).password;
						mBluetooth = bles.get(0);
						mEditDevSN.setText(searchResult.getName());
						mEditSceneName.setText(bles.get(0).sceneName);

						mBluetooth.name = searchResult.getName();
						mBluetooth.mac = searchResult.getAddress();
						mBluetooth.writeUuid = AppConstants.bleWriteCharacter;
						mBluetooth.notifyUuid = AppConstants.bleNotifitesCharacter;
						mBluetooth.serviceUuid = AppConstants.bleService;
						//showInputPasswordDialog(EE_DEV_BLUETOOTH);
						//return;
					} else {
						mTextTitle.setText("添加设备");
						mBtnDevAdd.setText("添加");
						isModifing = false;

						bleOldPsw = "LKD.CN";
						mEditSceneName.setText("");
						mEditDevSN.setText(searchResult.getName());
						mEditPassword.setText("");
						mBluetooth.isFirst = true;
					}

					mBluetooth.name = searchResult.getName();
					mBluetooth.mac = searchResult.getAddress();
					mBluetooth.writeUuid = AppConstants.bleWriteCharacter;
					mBluetooth.notifyUuid = AppConstants.bleNotifitesCharacter;
					mBluetooth.serviceUuid = AppConstants.bleService;
					mBluetooth.sceneName = mEditSceneName.getText().toString();
					mBluetooth.password = bleOldPsw;

					bleLocker = new BleLocker(mBluetooth, false, 800, iBleLockerCallBack);
					bleLocker.setmNoRssi(true);
					bleLocker.connect();
					//showInputPasswordDialog(EE_DEV_BLUETOOTH);
					showWaitDialog();
			}

			@Override
			public void OnClickedFun(FunDevice funDevice) {
				mFunDevice = funDevice;
				mEditDevSN.setText(funDevice.devSn);

				//mEditSceneName.setText();
				List<Camera> cams = MyApplication.liteOrm.query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_SN, mEditDevSN.getText().toString()));
				if (cams != null && cams.size() > 0) {
					mCamera = cams.get(0);
				  	mEditSceneName.setText(cams.get(0).sceneName);
					mEditPassword.setText("");
					//密码留空，调用FUNSDK进行密码校验
					//mFunDevice.loginPsw = cams.get(0).loginPsw;
					mTextTitle.setText("修改设备");
					mBtnDevAdd.setText("修改");
					isModifing = true;

					if(!cams.get(0).loginPsw.equals("")){
						showInputPasswordDialog(EE_DEV_NORMAL_MONITOR);
					}
				}else{
					mTextTitle.setText("添加设备");
					mBtnDevAdd.setText("添加");
					isModifing = false;
					mEditSceneName.setText("");
					mFunDevice.loginPsw ="";
					camOldPsw = "";
					mCamera.devId = mFunDevice.getId();
					mCamera.devIp = mFunDevice.getDevIP();
					mCamera.mac = mFunDevice.getDevMac();
					mCamera.name = mFunDevice.getDevName();
					mCamera.sn = mFunDevice.getDevSn();
					mCamera.serialNo = mFunDevice.getSerialNo();
					mCamera.type = mCurrDevType.getDevIndex();
					mCamera.loginName = "admin";
					mCamera.loginPsw = mFunDevice.loginPsw;
				}

				Log.d("DeviceAddByUser", "OnClickedFun: \ndevLoginName:"+funDevice.loginName
						+"\ndevLoginPsw:"+funDevice.loginPsw
						+"\nID:"+funDevice.getId()
						+"\nSerialNo:"+funDevice.getSerialNo()
						+"\nSN:"+funDevice.getDevSn()
						+"\nName:"+funDevice.getDevName()
						+"\nIP:"+funDevice.getDevIP()
						+"\nMac:"+funDevice.getDevMac()
						+"\nDevType:"+funDevice.getDevType());
			}

			@Override
			public void OnClickedWifiRmoter(WifiRemoterBoard wifiRemoterBoard) {
				//mWifiRemoterBoard = wifiRemoterBoard;
				mWifiRemoter = wifiRemoterBoard.getMWifiRemoter();
				List<WifiRemoter> wr = MyApplication.liteOrm.cascade().query(new QueryBuilder<WifiRemoter>(WifiRemoter.class).whereEquals(WifiRemoter.COL_MAC,
						mWifiRemoter.mac));
				if (wr != null && wr.size() > 0) {
					mTextTitle.setText("修改设备");
					mBtnDevAdd.setText("修改");
					isModifing = true;

					mEditDevSN.setText(wr.get(0).name);
					mEditSceneName.setText(wr.get(0).sceneName);
				}else{
					mTextTitle.setText("添加设备");
					mBtnDevAdd.setText("添加");
					isModifing = false;
					mEditSceneName.setText("");
					mEditDevSN.setText(mWifiRemoter.name);
				}

				Log.d("DeviceAddByUser", "OnClickedWifiRemoterBoard: \nName:"+wifiRemoterBoard.getMWifiRemoter().name
						+"\ndevName:"+wifiRemoterBoard.getMWifiRemoter().devName
						+"\nHost Url:"+wifiRemoterBoard.getMWifiRemoter().hostUrl
						+"\nPublic Topics:"+wifiRemoterBoard.getMWifiRemoter().publictopic
						+"\nSubscribe Topics:"+wifiRemoterBoard.getMWifiRemoter().subscribetopic
						+"\nClient Id:"+wifiRemoterBoard.getMWifiRemoter().clientid
						+"\nDevice Local IP:"+wifiRemoterBoard.getMWifiRemoter().devIpAddr
						+"\nDevice Local Port:"+ wifiRemoterBoard.getMWifiRemoter().devPort
						+"\nMac:"+wifiRemoterBoard.getMWifiRemoter().mac);
			}
		});

		mListViewDev.setAdapter(mAdapterDev);
		mListViewDev.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				searchDevice();
			}

		});

		mdns = new MDNS(this);

		searchDevice();

		//mListViewDev.setOnItemClickListener(this);
		
		mTextTitle.setText(R.string.guide_module_title_device_add);


		/*if (mCurrDevType == FunDevType.EE_DEV_BLUETOOTH) {
			mEditPassword.setVisibility(View.VISIBLE);
		}else{
			mEditPassword.setVisibility(View.GONE);
		}*/


		//if(mCurrDevType!=EE_DEV_BOUTIQUEROTOT) {
			// 设置登录方式为互联网方式
		//	FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);
		//}else {
				FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);
		//}
		
		// 监听设备类事件
		FunSupport.getInstance().registerOnFunDeviceListener(this);
		
		// 设备操作类事件(登录是否成功需要)
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);
		
		if ( !FunSupport.getInstance().hasLogin() ) {
			// 用户还未登录,需要先登录
			startLogin();
		}

		requestToGetLanDeviceList();


	}


	@Override
	protected void onDestroy() {
		//super.onDestroy();
		// 注销设备事件监听
		FunSupport.getInstance().removeOnFunDeviceListener(this);
		
		FunSupport.getInstance().removeOnFunDeviceOptListener(this);


		// 切换回网络访问
		FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);


		if ( null != mHandler ) {
			mHandler.removeCallbacksAndMessages(null);
		}

		if(bleLocker!=null){bleLocker.disconnect();}

		super.onDestroy();
	}


	@Override
	protected void onPause() {
		if(bleLocker!=null){
			bleLocker.disconnect();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		if(bleLocker!=null){
			showWaitDialog();
			bleLocker.connect();
		}
		super.onResume();
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.backBtnInTopLayout:
			{
				// 返回/退出
				//finish();
				onBackPressed();
			}
			break;
		case R.id.devAddBtn:
			{
				if(mEditDevSN.getText().toString().equals("") || mEditSceneName.getText().toString().equals("")){
					alertDialog("序列号或场景名不能为空！", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					}, new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							return;
						}
					});
				}else {
					if (mCurrDevType == FunDevType.EE_DEV_BLUETOOTH) {
						if(mEditPassword.getText().toString().equals("")) {
							Dialogs.alertMessage(mcontext, "错误", "密码不能为空，请输入正确密码", new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									return;
								}
							});
						}

						if(mEditPassword.getText().length() > 6 || mEditPassword.getText().length()< 6) {
							Dialogs.alertMessage(mcontext, "错误", "请输入六位密码", new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									return;
								}
							});
						}else {

							//Bluetooth bluetooth = null;
							mBluetooth.name = mEditDevSN.getText().toString();
							mBluetooth.sceneName = mEditSceneName.getText().toString();
							mBluetooth.password = mEditPassword.getText().toString();
							mBluetooth.isFirst = false;
						/*List<Bluetooth> bles = MyApplication.liteOrm.query(new QueryBuilder<Bluetooth>(Bluetooth.class).whereEquals("mac", mBluetooth.mac));
						if (bles != null && bles.size() > 0) {
							bluetooth = bles.get(0);
							bluetooth.name = mBluetooth.name;
							bluetooth.password = mBluetooth.password;
							bluetooth.isFirst = mBluetooth.isFirst;
						} else {
							bluetooth = mBluetooth;
						}*/

							if (bleLocker.getIsReday()) {
								bleLocker.changePassword(mBluetooth.password);
							} else {
								alertDialog("蓝牙设备未连接成功！", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										return;
									}
								}, new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										return;
									}
								});
							}

							MyApplication.liteOrm.save(mBluetooth);

							Intent intent = new Intent("android.intent.action.CART_BROADCAST");
							intent.putExtra("data", "ble_list_refresh");
							LocalBroadcastManager.getInstance(MyApplication.context).sendBroadcast(intent);
							sendBroadcast(intent);
						}
					} else {
						if (mCurrDevType == FunDevType.EE_DEV_NORMAL_MONITOR) {
							List<Camera> cams = MyApplication.liteOrm.query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_SN, mEditDevSN.getText().toString()));
							if (cams != null && cams.size() > 0) {
								mCamera = cams.get(0);
								mCamera.sceneName = mEditSceneName.getText().toString();
								mCamera.loginPsw = mEditPassword.getText().toString();
							} else {
								mCamera.devId = mFunDevice.getId();
								mCamera.devIp = mFunDevice.getDevIP();
								mCamera.mac = mFunDevice.getDevMac();
								mCamera.name = mFunDevice.getDevName();
								mCamera.sn = mFunDevice.getDevSn();
								mCamera.serialNo = mFunDevice.getSerialNo();
								mCamera.type = mCurrDevType.getDevIndex();
								mCamera.sceneName = mEditSceneName.getText().toString();
								mCamera.loginName = "admin";
								mCamera.loginPsw = mEditPassword.getText().toString();
							}

							MyApplication.liteOrm.save(mCamera);

							// 修改密码,设置ModifyPassword参数
							// 注意,如果是直接调用FunSDK.DevSetConfigByJson()接口,需要对密码做MD5加密,参考ModifyPassword.java的处理
							ModifyPassword modifyPasswd = new ModifyPassword();
							modifyPasswd.PassWord = camOldPsw;
							modifyPasswd.NewPassWord = mEditPassword.getText().toString();

							FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, modifyPasswd);

							Intent intent = new Intent("android.intent.action.CART_BROADCAST");
							intent.putExtra("data", "cam_list_refresh");
							LocalBroadcastManager.getInstance(MyApplication.context).sendBroadcast(intent);
							sendBroadcast(intent);
						}else {
							List<WifiRemoter> wr = MyApplication.liteOrm.cascade().query(new QueryBuilder<WifiRemoter>(WifiRemoter.class).whereEquals(WifiRemoter.COL_MAC,
									mWifiRemoter.mac));
							if (wr != null && wr.size() > 0) {
								wr.get(0).devClientid = mWifiRemoter.devClientid;
								wr.get(0).clientid = mWifiRemoter.clientid;
								mWifiRemoter = wr.get(0);
								mWifiRemoter.loginPsw = mEditPassword.getText().toString();
								mWifiRemoter.sceneName = mEditSceneName.getText().toString();
							} else {
								mWifiRemoter.sceneName = mEditSceneName.getText().toString();
								mWifiRemoter.loginPsw = mEditPassword.getText().toString();
							}
							MyApplication.liteOrm.cascade().save(mWifiRemoter);

							/*Intent intent = new Intent("android.intent.action.CART_BROADCAST");
							intent.putExtra("data", "remoter_list_refresh");
							LocalBroadcastManager.getInstance(MyApplication.context).sendBroadcast(intent);
							sendBroadcast(intent);*/
						}
					}

					//replaceFragment(android.R.id.content, new TestFragment());
					//finish();
					onBackPressed();
				}
			}
			break;
		case R.id.btnScanCode:
			{
				startScanQrCode();
			}
			break;
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case MESSAGE_REFRESH_DEVICE_STATUS: {
					FunSupport.getInstance().requestAllLanDeviceStatus();
					//FunSupport.getInstance().requestAllDeviceStatus();
				}
				break;
			/*case MESSAGE_DELAY_FINISH:
				{
					hideWaitDialog();
					
					// 启动/打开设备操作界面
					if ( null != mFunDevice ) {
						DeviceActivitys.startDeviceActivity(DeviceAddByUser.this, mFunDevice);
					}
					
					mFunDevice = null;
					finish();
				}
				break;*/

				case MESSAGE_EASYLINK_DEVICE_FOUND:
				{
					JSONArray jsonArray = (JSONArray)msg.obj;
					for (int i = 0; i < jsonArray.length(); i++) {
						try {
							JSONObject tmp = (JSONObject) jsonArray.get(i);
							if (tmp.getString("Name").contains("LKD-OWSL")) {

								WifiRemoter wr = new WifiRemoter();

								String s=tmp.getString("Name").substring(tmp.getString("Name").lastIndexOf("#")+1);
								if(tmp.getString("Name").contains("-19#")){
									wr.name = "WIFI主控器（单向）@ "+ s ;
									wr.devType = "One_Way_Smart_Lock";
									wr.devTypeId = EE_DEV_OW_REMOTER.getDevIndex();
								}else{
									wr.name = "WIFI主控器（双向）@ "+s;
                                    wr.devType = "One_Way_Smart_Lock";
                                    wr.devTypeId = EE_DEV_OW_REMOTER.getDevIndex();
								}

								/*正式版应该从服务器获取设备MQTT设置*/
								wr.hostUrl = MqttUtil.getHOST();
								wr.hostPort = "1883";
								wr.clientid = MqttUtil.getCLIENTID();
								wr.devClientid = wr.devType+":"+tmp.getString("MAC").replace(":","");
								wr.publictopic = "hardware/from/server/" + tmp.getString("MAC").replace(":","");
								wr.subscribetopic = "hardware/from/client/" + tmp.getString("MAC").replace(":","");
								wr.hostUsername = MqttUtil.getUSERNAME();
								wr.hostPassword = MqttUtil.getPASSWORD();

								wr.devName = tmp.getString("Name");
								wr.devIpAddr = tmp.getString("IP");
								wr.devPort = tmp.getString("Port");
								wr.mac = tmp.getString("MAC");

								WifiRemoterBoard wrb = new WifiRemoterBoard(mcontext, wr, false);

								if(matchWifiRemoterInList(wr.mac)){
									Log.d("DeviceAddByUser", "Found Device in List");
								}else{
									mWifiRemoterBoards.add(wrb);
									mAdapterDev.updateWifiRemoterBoards(mWifiRemoterBoards);
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					mAdapterDev.updateWifiRemoterBoards(mWifiRemoterBoards);
				}
					break;
			}
		}
		
	};

	private boolean matchWifiRemoterInList(String Mac) {
		if(mWifiRemoterBoards==null){return false;}
		//如果为null，直接使用全部数据
		if (!Mac.equals("") ||  mWifiRemoterBoards.size()>0) {
			//否则，匹配相应的数据
			for (int i = 0; i < mWifiRemoterBoards.size(); i++) {
				if (mWifiRemoterBoards.get(i).getMWifiRemoter().mac.contains(Mac)) {//这里可拓展自己想要的，甚至可以拆分搜索汉字来匹配
					return true;
				}
			}
			return  false;
		}else{
			return false;
		}
	}

	private int getSpinnerIndexByDeviceType(FunDevType type) {
		for ( int i = 0; i < mSupportDevTypes.length; i ++ ) {
			if ( type == mSupportDevTypes[i] ) {
				return i;
			}
		}
		return 0;
	}
	
	private void startLogin() {
		//Intent intent = new Intent();
		//intent.setClass(this, ActivityGuideUserLogin.class);
		//replaceFragment(android.R.id.content, new FragmentUserLogin());
		replaceFragment(R.id.dab_container, new FragmentUserLogin());

	}
	
	// 扫描二维码
	private void startScanQrCode() {
		Intent intent = new Intent();
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}
	
	// 设备登录
	private void requestDeviceLogin(String loginName, String pwd) {
		String devSN = mEditDevSN.getText().toString().trim();

		if ( devSN.length() == 0 ) {
			showToast(R.string.device_login_error_sn);
			return;
		}
		
		showWaitDialog();

		// 添加设备之前都必须先登录一下,以防设备密码错误,也是校验其合法性
		FunSupport.getInstance().requestDeviceLogin1(mFunDevice,loginName, pwd);
	}
	
	private void requestReloginByPasswd() {
		if ( null != mFunDevice ) {
			
//			mFunDevice.loginPsw = passwd;
			
			showWaitDialog();
			
			FunSupport.getInstance().requestDeviceLogin1(mFunDevice,"admin","");
		}
	}

	private void requestDeviceAdd() {
		if ( null != mFunDevice ) {
			FunSupport.getInstance().requestDeviceAdd(mFunDevice);
		}
	}

	private void requestToGetLanDeviceList() {
		//if(mCurrDevType==EE_DEV_BOUTIQUEROTOT){
			FunSupport.getInstance().requestLanDeviceList();
		//}else{
		//	FunSupport.getInstance().requestDeviceList();
		//}

/*		if (!FunSupport.getInstance().requestLanDeviceList()) {
			showToast(R.string.guide_message_error_call);
		} else {
			//showWaitDialog();
			//mRefreshLayout.showState(AppConstants.LOADING);
		}*/
	}


	/**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog(final FunDevType devType) {
    	if(inputDialog!=null){return;}
    	inputDialog = new DialogInputPasswd(this,
    			getResources().getString(R.string.device_login_input_password),
    			"",
    			R.string.common_confirm,
    			R.string.common_cancel
    			){

					@Override
					public boolean confirm(String editText) {
						if (devType == FunDevType.EE_DEV_BLUETOOTH) {
							step = 1;
							needCheckSTA = false;
							if (mBluetooth != null) {
								inputPwd = editText;
								//bleOldPsw = mBluetooth.password;
								//if(editText.equals(bleOldPsw)) {
								bleOldPsw = editText;
								mBluetooth.password = editText;
								mEditDevSN.setText(mBluetooth.name);
								mEditPassword.setText(mBluetooth.password);
								mEditSceneName.setText(mBluetooth.sceneName);
								mBluetooth.isFirst = false;

								bleLocker = new BleLocker(mBluetooth, false, 800, iBleLockerCallBack);
								bleLocker.setmNoRssi(true);
								bleLocker.connect();

								showWaitDialog();

								super.hide();
							}
						} else {
							if(devType == EE_DEV_NORMAL_MONITOR || devType ==EE_DEV_BOUTIQUEROTOT) {
									camOldPsw = editText;
									requestDeviceLogin("admin", editText);
									super.hide();
								/*}else{
									Dialogs.alertDialogBtn(mcontext, "密码错误", "请输入正确的密码", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											mEditDevSN.setText("");
											mEditPassword.setText("");
											mEditSceneName.setText("");
										}
									}, new DialogInterface.OnCancelListener() {
										@Override
										public void onCancel(DialogInterface dialog) {
										}
									});

								}*/

							}
						}


						inputDialog = null;
						return super.confirm(editText);
					}

					@Override
					public void cancel() {
						super.cancel();
						
						// 取消输入密码,直接退出
						//finish();

						inputDialog = null;
					}
    		
    	};
    	
    	inputDialog.show();
    }

	@Override
	public void onDeviceListChanged() {
		// TODO Auto-generated method stub
		refreshLanDeviceList();
	}

	@Override
	public void onDeviceStatusChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub
		if (null != mAdapterDev) {
			mAdapterDev.notifyDataSetChanged();
		}
	}


	@Override
	public void onDeviceAddedSuccess() {
		hideWaitDialog();
		mListViewDev.onRefreshComplete(true);
		mRefreshLayout.showState(AppConstants.LIST);
		showToast(R.string.device_opt_add_success);
		
		// 如果需要,在添加设备成功之后,可以更新一次设备列表
		FunSupport.getInstance().requestDeviceList();
	}


	@Override
	public void onDeviceAddedFailed(Integer errCode) {
		hideWaitDialog();
		mListViewDev.onRefreshComplete(true);
		mRefreshLayout.showState(AppConstants.LIST);
		//showToast(FunError.getErrorStr(errCode));
	}


	@Override
	public void onDeviceRemovedSuccess() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceRemovedFailed(Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAPDeviceListChanged() {
		// TODO Auto-generated method stub
		refreshLanDeviceList();
	}

	@Override
	public void onLanDeviceListChanged() {
		// TODO Auto-generated method stub
		refreshLanDeviceList();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if ( position >= 0 && position < mSupportDevTypes.length ) {
			mCurrDevType = mSupportDevTypes[position];
			mAdapterDev.setCurrentDevType(mCurrDevType);

			//if(mCurrDevType!=EE_DEV_BOUTIQUEROTOT) {
				// 设置登录方式为互联网方式
			//	FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);
			//}else {
				FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);
			//}

			/*if (mCurrDevType == FunDevType.EE_DEV_BLUETOOTH) {
				mEditPassword.setVisibility(View.VISIBLE);
			}else{
				mEditPassword.setVisibility(View.GONE);
			}*/

			searchDevice();
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if ( null != mAdapterDev ) {
			FunDevice funDevice = mAdapterDev.getFunDevice(position);
			if ( null != funDevice ) {
				// 当前选择的设备
				mFunDevice = funDevice;
				mCurrDevType = funDevice.devType;
				// 在Sipnner中设置当前选择设备的类型
				mSpinnerDevType.setSelection(getSpinnerIndexByDeviceType(mCurrDevType));
				// 在EditText中设置当前选择设备的序列号
				mEditDevSN.setText(mFunDevice.getDevSn());
				mAdapterDev.setCurrentDevType(mCurrDevType);
			}
		}
		
	}


	@Override
	public void onDeviceLoginSuccess(FunDevice funDevice) {
		mEditPassword.setText(camOldPsw);
		if ( null != mFunDevice
				&& null != funDevice
				&& mFunDevice.getId() == funDevice.getId() ) {
			requestDeviceAdd();
		}
	}


	@Override
	public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
		hideWaitDialog();
		
		showToast(FunError.getErrorStr(errCode));
		
		// 如果账号密码不正确,那么需要提示用户,输入密码重新登录
		if ( errCode == FunError.EE_DVR_PASSWORD_NOT_VALID ) {
			showInputPasswordDialog(EE_DEV_BOUTIQUEROTOT);
		}
	}


	@Override
	public void onDeviceGetConfigSuccess(FunDevice funDevice,
			String configName, int nSeq) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
		if ( ModifyPassword.CONFIG_NAME.equals(configName) ) {
			// 修改密码成功,保存新密码,下次登录使用
			if ( null != mFunDevice && null != mEditPassword.getText() ) {
				FunDevicePassword.getInstance().saveDevicePassword(
						mFunDevice.getDevSn(),
						mEditPassword.getText().toString());
			}
			// 库函数方式本地保存密码
			if (FunSupport.getInstance().getSaveNativePassword()) {
				FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", mEditPassword.getText().toString());
				// 如果设置了使用本地保存密码，则将密码保存到本地文件
			}
			// 隐藏等待框
			hideWaitDialog();
			showToast(R.string.user_forget_pwd_reset_passw_success);

			onBackPressed();
		}
		
	}

	@Override
	public void onBackPressed() {
		//AppManager.getAppManager().getActivity(HomeActivity.class);
		/*Intent intent = new Intent();
		intent.setClass(mcontext, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);*/
		finish();
	}

	@Override
	public void onDeviceSetConfigFailed(FunDevice funDevice, String configName,
			Integer errCode) {
			hideWaitDialog();
			showToast(FunError.getErrorStr(errCode));
		
	}


	@Override
	public void onDeviceChangeInfoSuccess(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceOptionSuccess(FunDevice funDevice, String option) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceOptionFailed(FunDevice funDevice, String option,
			Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceFileListChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceFileListChanged(FunDevice funDevice,
			H264_DVR_FILE_DATA[] datas) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data) {
		if ( requestCode == 1 
				&& responseCode == RESULT_OK ) {
			// Demo, 扫描二维码的结果
			if ( null != data ) {
				String deviceSn = data.getStringExtra("result");
				if ( null != deviceSn && null != mEditDevSN ) {
					mEditDevSN.setText(deviceSn);
				}
			}
		}
		
	}


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

	private void refreshLanDeviceList() {
		hideWaitDialog();
		mTextTip.setText("扫描设备");
		//if(mCurrDevType==EE_DEV_BOUTIQUEROTOT) {
			mAdapterDev.updateDevice(FunSupport.getInstance().getLanDeviceList());
		//}else {
		//	mAdapterDev.updateDevice(FunSupport.getInstance().getDeviceList());
		//}
		mListViewDev.onRefreshComplete(true);
		mRefreshLayout.showState(AppConstants.LIST);

		// 延时100毫秒更新设备消息
		mHandler.removeMessages(MESSAGE_REFRESH_DEVICE_STATUS);
		//if (FunSupport.getInstance().getLanDeviceList().size() > 0) {
		//if (FunSupport.getInstance().getDeviceList().size() > 0) {
		if (mAdapterDev.getCount() > 0) {
			mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH_DEVICE_STATUS, 100);
		}
	}

    /**
     * -----------------
     * 搜索设备
     */
    private void searchDevice() {
    	if(mCurrDevType==FunDevType.EE_DEV_BLUETOOTH) {
    		if(mBleDevices.size()>0){mBleDevices.clear();}
			SearchRequest request = new SearchRequest.Builder()
					.searchBluetoothLeDevice(5000, 2).build();

			ClientManager.getClient().search(request, mSearchResponse);
		}else{
    		if(mCurrDevType == EE_DEV_BLUETOOTH) {
				//requestToGetLanDeviceList();
				// 以局域网内搜索过的设备,显示在下方作为测试设备添加
				if (null != mAdapterDev && (mCurrDevType == FunDevType.EE_DEV_NORMAL_MONITOR || mCurrDevType == EE_DEV_BOUTIQUEROTOT)) {
					//mAdapterDev.updateDevice(FunSupport.getInstance().getLanDeviceList());
					requestToGetLanDeviceList();
				}
			}else{
				if(mCurrDevType == EE_DEV_OW_REMOTER) {
					searchEasyLinkDevices();
				}else{
					return;
				}
			}
		}

		mTextTip.setText("停止扫描设备....");
		mRefreshLayout.showState(AppConstants.LOADING);

    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            mTextTip.setText("停止扫描设备....");
            isBleScanning = true;
            mRefreshLayout.showState(AppConstants.LIST);
            BluetoothLog.w("正在搜索设备");
            //toolbar.setTitle(R.string.string_refreshing);
        }


        @Override
        public void onDeviceFounded(SearchResult device) {
//            BluetoothLog.w("MainActivity.onDeviceFounded " + device.device.getAddress());
			if(device.getName().contains("VB")) {
				if (!mBleDevices.contains(device)) {
					mBleDevices.add(device);
					mAdapterDev.updateBleDevice(mBleDevices);
				}

				if (mBleDevices.size() > 0) {
					BluetoothLog.w("DeviceAddByUser.Bluetooth founds count: " + mBleDevices.size());
					mListViewDev.onRefreshComplete(true);
					mRefreshLayout.showState(AppConstants.LIST);
				}
			}else{
				BluetoothLog.w("Dorp device!");
			}
        }

        @Override
        public void onSearchStopped() {
            isBleScanning = false;
            mTextTip.setText("扫描设备");
            BluetoothLog.w("DeviceAddByUser.onSearchStopped");
            mRefreshLayout.showState(AppConstants.LIST);
            //mTextTip.setText("扫描设备");
            //toolbar.setTitle(R.string.devices);
        }

        @Override
        public void onSearchCanceled() {
            isBleScanning = false;
			mListViewDev.onRefreshComplete(true);
			mRefreshLayout.showState(AppConstants.LIST);
            BluetoothLog.w("DeviceAddByUser.onSearchCanceled");

            mTextTip.setText("扫描设备");
            //toolbar.setTitle(R.string.devices);
        }
    };

	@Override
	public void onAddSubDeviceFailed(FunDevice funDevice, MsgContent msgContent) {

	}

	@Override
	public void onAddSubDeviceSuccess(FunDevice funDevice, MsgContent msgContent) {

	}

	BleLocker.IBleLockerListener iBleLockerCallBack = new BleLocker.IBleLockerListener() {
		@Override
		public void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onLock(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onBleReadResponse(Bluetooth bluetooth, byte[] data, BleLockerStatus status) {
			BluetoothLog.i(" 读取返回信息 onReadResponse：code="+ status.getSatusId() +" message=" + status.getmStatusMsg()
					+" bodycontent"+String.format("read: %s", ByteUtils.byteToString(data))+"\n");
		}

		@Override
		public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {
			BluetoothLog.i(" 设备消息 onBleNotifyResponse：code="+ status.getSatusId() +"\n message=" + NotifyValue + "\n  status:" + status.getmStatusMsg() +"\n");
		}

		@Override
		public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {
			BluetoothLog.i(" 连接设备，onConnected：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
			if(status==BleLockerStatus.CONNECTED && needCheckSTA) {
				bleLocker.sta();
			}
		}

		@Override
		public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

		}

		@Override
		public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
			BluetoothLog.i(" 设备已准备，onReday：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
			hideWaitDialog();

		}

		@Override
		public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {

		}
		@Override
		public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
			BluetoothLog.i(" 密码错误，onPasswdError：code="+ status.getSatusId() +" message=" + status.getmStatusMsg() +"\n");
			hideWaitDialog();

			if (step == 1) {
				alertDialog("密码错误！蓝牙设备连接失败！", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mEditDevSN.setText("");
						mEditPassword.setText("");
						mEditSceneName.setText("");
						return;
					}
				}, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						mEditDevSN.setText("");
						mEditPassword.setText("");
						mEditSceneName.setText("");
						return;
					}
				});

				step=0;
			}
			/*alertDialog("设备连接密码不正确，如果您忘记密码，可重置出厂设置后再添加此设备！", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}, new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					return;
				}
			});*/
			//bleLocker.sta();
		}
		@Override
		public void onResetted(Bluetooth bluetooth, int Resetted, BleLockerStatus status) {
			needCheckSTA=false;
			if(Resetted ==1 ) {
				BluetoothLog.i(" 设备已重置，onResetted：code=" + status.getSatusId() + " message=" + status.getmStatusMsg() + "\n");
				Dialogs.alertMessage(mcontext, "提示","设备已重置为出厂状态，输入新密码可重新激活本设备");
				mTextTitle.setText("添加设备");
				mBtnDevAdd.setText("添加");
				bleOldPsw = "LKD.CN";
				mBluetooth.password = "";
				mBluetooth.isFirst = true;
				bleLocker = new BleLocker(mBluetooth, false, 800, iBleLockerCallBack);
				bleLocker.setmPassword(bleOldPsw);
				bleLocker.setmNoRssi(true);
				bleLocker.connect();
			}else{
				BluetoothLog.i(" 设备正常，onResetted：code=" + status.getSatusId() + " message=" + status.getmStatusMsg() + "\n");
				mTextTitle.setText("修改设备");
				mBtnDevAdd.setText("修改");
				showInputPasswordDialog(EE_DEV_BLUETOOTH);
			}
		}
	};

	private void searchEasyLinkDevices(){
		mdns.startSearchDevices("_easylink._tcp.local.", new SearchDeviceCallBack() {
			@Override
			public void onSuccess(int code, String message) {
				super.onSuccess(code, message);
				Log.d("---mdns---", "\ncode="+code+"\nmessage=\n"+message);
			}

			@Override
			public void onFailure(int code, String message) {
				super.onFailure(code, message);
				Log.d("---mdns---", "\ncode="+code+"\nmessage=\n"+message);
			}

			@Override
			public void onDevicesFind(int code, JSONArray deviceStatus) {
				super.onDevicesFind(code, deviceStatus);
				if (!deviceStatus.equals("")) {
					send2handlerObj(MESSAGE_EASYLINK_DEVICE_FOUND, deviceStatus);
					Log.d("---mdns---","\ncode="+code+"\ndeviceInfo=\n"+deviceStatus.toString());
				}else{
					Log.d("---mdns---", "\ncode="+code+"\ndeviceInfo=\n"+deviceStatus.toString());
				}
			}
		});

      countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.i("SearchEasyLinkDevice", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                hideWaitDialog();
                mdns.stopSearchDevices(new SearchDeviceCallBack() {
					@Override
					public void onSuccess(int code, String message) {
						super.onSuccess(code, message);
						mListViewDev.onRefreshComplete(true);
						mRefreshLayout.showState(AppConstants.LIST);
						Log.i("SearchEasyLinkDevice","CountDownTimer-onFinish");

						mTextTip.setText("扫描设备");
						//toolbar.setTitle(R.string.devices);
						Log.i("SearchEasyLinkDevice", "down");
					}

					@Override
					public void onFailure(int code, String message) {
						super.onFailure(code, message);
					}

					@Override
					public void onDevicesFind(int code, JSONArray deviceStatus) {
						super.onDevicesFind(code, deviceStatus);
						if (!deviceStatus.equals("")) {
							send2handlerObj(MESSAGE_EASYLINK_DEVICE_FOUND, deviceStatus);
							Log.d("---mdns---","\ncode="+code+"\ndeviceInfo=\n"+deviceStatus.toString());
						}else{
							Log.d("---mdns---", "\ncode="+code+"\ndeviceInfo=\n"+deviceStatus.toString());
						}
					}
				});
            }
        }.start();
	}

	private void send2handler(int code, String message) {
		send2handlerObj(code, message);
	}

	private void send2handlerObj(int code, Object message) {
		Message msg = new Message();
		msg.what = code;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}
}
