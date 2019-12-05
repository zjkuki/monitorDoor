package com.example.funsdkdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.common.DialogWaitting;
import com.example.common.UIFactory;
import com.janady.setup.JBaseFragment;
import com.lib.FunSDK;
import com.lkd.smartlocker.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.xm.ui.widget.SpinnerSelectItem;

import java.util.Arrays;
import java.util.List;

public class ActivityDemo extends FragmentActivity {
	private DialogWaitting mWaitDialog = null;
	private Toast mToast = null;
	
	private View mNavRightView = null;

	public void setText(String text){
		if ( null != mWaitDialog ) {
			mWaitDialog.setText(text);
		}
	}

	public void showWaitDialog() {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show();
	}
	
	public void showWaitDialog(int resid) {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show(resid);
	}
	
	public void showWaitDialog(String text) {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show(text);
	}
	
	public void hideWaitDialog() {
		if ( null != mWaitDialog ) {
			mWaitDialog.dismiss();
		}
	}
	
	public void showToast(String text){
		if(MyApplication.noToatsShow){return;}
		if ( null != text ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}
	
	public void showToast(int resid){
		if(MyApplication.noToatsShow){return;}
		if ( resid > 0 ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, resid, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	protected void setStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			this.getWindow().setStatusBarColor(getResources().getColor(R.color.theme_color));//设置状态栏颜色
			//this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
		}
	}

	public void alertDialog(String text, DialogInterface.OnClickListener onClickListener, DialogInterface.OnCancelListener onCancelListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(text);
		builder.setPositiveButton("确定", onClickListener);
		builder.setOnCancelListener(onCancelListener);
		builder.show();
	}
	
	/**
	  *  判断某个字符串是否存在于数组中
	  *  用来判断该配置是否通道相关
	  *  @param stringArray 原数组
	  *  @param source 查找的字符串
	  *  @return 是否找到
	  */
	 public static boolean contains(String[] stringArray, String source) {
	  // 转换为list
	  List<String> tempList = Arrays.asList(stringArray);
	  
	  // 利用list的包含方法,进行判断
		 return tempList.contains(source);
	 }
	
	// 只有布局中有指定的标题栏的Activity才允许设置
	protected View setNavagateRightButton(int resource) {
		return setNavagateRightButton(resource, 48, LayoutParams.MATCH_PARENT);
	}
	
	protected View setNavagateRightButton(int resource, int witdhOfDP, int heightOfDP) {
		if ( null != findViewById(R.id.layoutTop) ) {
			RelativeLayout navLayout = (RelativeLayout)findViewById(R.id.layoutTop);
			if ( null != mNavRightView ) {
				navLayout.removeView(mNavRightView);
			}
			
			mNavRightView = LayoutInflater.from(this).inflate(resource, null);
			RelativeLayout.LayoutParams lp = null;
			
			int lpWidth = 0;
			int lpHeight = 0;
			if ( witdhOfDP > 0 ) {
				lpWidth = UIFactory.dip2px(this, witdhOfDP);
			} else {
				lpWidth = witdhOfDP;
			}
			
			if ( heightOfDP > 0 ) {
				lpHeight = UIFactory.dip2px(this, heightOfDP);
			} else {
				lpHeight = heightOfDP;
			}
			
			lp = new RelativeLayout.LayoutParams(
					lpWidth, lpHeight);
			
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp.addRule(RelativeLayout.CENTER_VERTICAL);
			
			lp.rightMargin = UIFactory.dip2px(this, 5);
			
			navLayout.addView(mNavRightView, lp);
			
			return mNavRightView;
		}
		return null;
	}

	protected int getIntValue(View layout,int id) {
		if (layout == null) {
			return 0;
		}
		View v = layout.findViewById(id);
		return getIntValue(v);
	}

	protected int getIntValue(View v) {
		if (v == null) {
			return 0;
		}
		if (v instanceof EditText) {
			EditText v0 = (EditText) v;
			return Integer.valueOf(v0.getText().toString());
		} else if (v instanceof CheckBox) {
			CheckBox v0 = (CheckBox) v;
			return v0.isChecked() ? 1 : 0;
		} else if (v instanceof SeekBar) {
			SeekBar v0 = (SeekBar) v;
			return v0.getProgress();
		} else if (v instanceof Spinner) {
			Spinner sp = (Spinner) v;
			Object iv = v.getTag();
			if (iv != null && iv instanceof int[]) {
				int[] values = (int[]) iv;
				int i = sp.getSelectedItemPosition();
				if (i >= 0 && i < values.length) {
					return values[i];
				}
				return 0;
			}
		} else {
//            System.err.println("GetIntValue:" + id);
		}
		return 0;
	}

	protected int initSpinnerText(Spinner sp, String[] texts, int[] values) {
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item,
				texts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		if (values == null) {
			values = new int[texts.length];
			for (int i = 0; i < texts.length; ++i) {
				values[i] = i;
			}
		}
		sp.setTag(values);
		return 0;
	}

	public int setValue(View v, int value) {
		if (v instanceof SpinnerSelectItem) {
			v = ((SpinnerSelectItem) v).getSpinner();
		}
		if (v instanceof EditText) {
			EditText v0 = (EditText) v;
			v0.setText(String.valueOf(value));
		} else if (v instanceof CheckBox) {
			CheckBox v0 = (CheckBox) v;
			v0.setChecked(value != 1);
		} else if (v instanceof SeekBar) {
			SeekBar v0 = (SeekBar) v;
			v0.setProgress(value);
		} else if (v instanceof Spinner) {
			Spinner sp = (Spinner) v;
			Object iv = v.getTag();
			if (iv != null && iv instanceof int[]) {
				int values[] = (int[]) iv;
				for (int i = 0; i < values.length; ++i) {
					if (value == values[i]) {
						sp.setSelection(i);
						break;
					}
				}
			}
		}
		return 0;
	}

	public void replaceFragment(int resId, Fragment fragment) {
// 1.获取FragmentManager，在活动中可以直接通过调用getFragmentManager()方法得到
		FragmentManager fragmentManager =getSupportFragmentManager();
// 2.开启一个事务，通过调用beginTransaction()方法开启
		FragmentTransaction transaction = fragmentManager.beginTransaction();
// 3.向容器内添加或替换碎片，一般使用replace()方法实现，需要传入容器的id和待添加的碎片实例
		transaction.replace(resId, fragment);  //fr_container不能为fragment布局，可使用线性布局相对布局等。
// 4.使用addToBackStack()方法，将事务添加到返回栈中，填入的是用于描述返回栈的一个名字
		transaction.addToBackStack(null);
// 5.提交事物,调用commit()方法来完成
		transaction.commit();
	}


}
