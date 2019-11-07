package com.janady.common;



import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.common.UIFactory;
import com.lkd.smartlocker.R;

public abstract class JDialogModifyPasswd {
	Context context;
	AlertDialog tip;
	public TextView mTextTitle;
	public TextView mMessages;
	TextView mTextConfirm;
	TextView mTextCancel;
	RelativeLayout mLayoutTipShow;
	LinearLayout llOldPasswd;
	LinearLayout mLayoutConfirm;
	LinearLayout mLayoutCancel;
	EditText edtOldPasswd;
	EditText edtNewPasswd;
	EditText edtComfirmPasswd;
	public boolean isOnlyNewPasswd = false;

	private final int MESSAGE_SHOW_IM = 0x102;


	public JDialogModifyPasswd(Context context, String titleRes, String oldPasswd, String newPasswd, String comfirmPasswd, int confirmRes,
                               int cancelRes, boolean OnlyNewPasswd) {

		this.isOnlyNewPasswd = OnlyNewPasswd;
		this.context = context;

		tip = new AlertDialog.Builder(context).create();
		tip.requestWindowFeature(Window.FEATURE_NO_TITLE);
		tip.setCanceledOnTouchOutside(true);
		tip.show();

		View view = LayoutInflater.from(context).inflate(R.layout.tip_modify_passwd, null);

		mTextTitle = (TextView) view.findViewById(R.id.title);
		mTextConfirm = (TextView) view.findViewById(R.id.confirm);
		mTextCancel = (TextView) view.findViewById(R.id.cancel);
		mLayoutTipShow = (RelativeLayout) view.findViewById(R.id.tipShow);
		mLayoutConfirm = (LinearLayout) view.findViewById(R.id.confirmLayout);
		mLayoutCancel = (LinearLayout) view.findViewById(R.id.cancelLayout);
		llOldPasswd = (LinearLayout) view.findViewById(R.id.ll_oldPasswd);
		if(isOnlyNewPasswd){
			llOldPasswd.setVisibility(View.GONE);
		}else {
			llOldPasswd.setVisibility(View.VISIBLE);
		}

		edtOldPasswd = (EditText) view.findViewById(R.id.editOldPasswd);
		edtNewPasswd = (EditText) view.findViewById(R.id.editNewPasswd);
		edtComfirmPasswd = (EditText) view.findViewById(R.id.editComfirmPasswd);
		mMessages = (TextView) view.findViewById(R.id.tvMessage);
//		mEditText.setKeyListener(DialerKeyListener.getInstance());
		
		mTextTitle.setText(titleRes);
		mTextConfirm.setText(confirmRes);
		mTextCancel.setText(cancelRes);

		RelativeLayout.LayoutParams pointLp = (RelativeLayout.LayoutParams) mLayoutTipShow
				.getLayoutParams();
		pointLp.width = UIFactory.dip2px(context, 320);
		pointLp.height = UIFactory.dip2px(context, 270);
		mLayoutTipShow.setLayoutParams(pointLp);

		mLayoutConfirm.setOnClickListener(confirmListener);
		mLayoutCancel.setOnClickListener(cancelListener);

		Window window = tip.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.setContentView(view);
		
		tip.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); 
		tip.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		if(edtOldPasswd.getVisibility()==View.VISIBLE){
			edtOldPasswd.setFocusableInTouchMode(true);
			edtOldPasswd.setFocusable(true);
			edtOldPasswd.requestFocus();
		}else{
			edtNewPasswd.setFocusableInTouchMode(true);
			edtNewPasswd.setFocusable(true);
			edtNewPasswd.requestFocus();
		}
	}
	


	OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			tryConfirm();
		}
	};
	
	private void tryConfirm() {
		String oldPasswdStr = edtOldPasswd.getText().toString();
		String newPasswdStr = edtNewPasswd.getText().toString();
		String comfirmPasswdStr = edtComfirmPasswd.getText().toString();

		if(!confirm(oldPasswdStr,newPasswdStr,comfirmPasswdStr)){
			edtOldPasswd.setText("");
			edtNewPasswd.setText("");
			edtComfirmPasswd.setText("");
		}

	}

	OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			cancel();
		}
	};

	public void show() {
		tip.show();
		
		mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_IM, 100);
	}

	public void hide() {
		tip.dismiss();

	}

	public boolean confirm(String oldPasswd, String newPasswd, String confirmPasswd) {
		hide();
		return true;
	}

	public void cancel() {
		hide();
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_SHOW_IM:
				{
					edtOldPasswd.requestFocus();
					InputMethodManager imm = (InputMethodManager) JDialogModifyPasswd.this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(edtOldPasswd, InputMethodManager.SHOW_IMPLICIT);
				}
				break;
			}
		}
		
	};
}
