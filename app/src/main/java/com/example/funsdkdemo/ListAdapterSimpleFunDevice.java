package com.example.funsdkdemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.database.model.WifiRemoter;
import com.janady.lkd.WifiRemoterBoard;
import com.lib.funsdk.support.models.FunDevStatus;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.lkd.smartlocker.R;

public class ListAdapterSimpleFunDevice extends BaseAdapter implements Comparator<SearchResult> {
	
	private Context mContext = null;
	private LayoutInflater mInflater;
	private List<FunDevice> mListDevs = new ArrayList<FunDevice>();
	private List<SearchResult> mListBleDevs = new ArrayList<SearchResult>();
	private List<WifiRemoterBoard> mListWifiRemoterBoardDevs = new ArrayList<WifiRemoterBoard>();

	public FunDevType getCurrentDevType() {
		return currentDevType;
	}

	public void setCurrentDevType(FunDevType currentDevType) {
		this.currentDevType = currentDevType;
	}

	private FunDevType currentDevType;
	private OnClickListener mOnClickListener;

	public void setOnClickListener(OnClickListener onClickListener) {
		this.mOnClickListener = onClickListener;
	}

	public ListAdapterSimpleFunDevice(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public ListAdapterSimpleFunDevice(Context context, FunDevType funDevType) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		currentDevType = funDevType;
	}


	public void updateWifiRemoterBoards(List<WifiRemoterBoard> devWifiRemoterBoardList) {
		mListWifiRemoterBoardDevs.clear();
		mListWifiRemoterBoardDevs.addAll(devWifiRemoterBoardList);
		this.notifyDataSetInvalidated();
	}

	public void updateBleDevice(List<SearchResult> devBleList) {
		mListBleDevs.clear();
		mListBleDevs.addAll(devBleList);
		Collections.sort(devBleList, this);
		this.notifyDataSetInvalidated();
	}

	public void updateDevice(List<FunDevice> devList) {
		mListDevs.clear();
		mListDevs.addAll(devList);
		this.notifyDataSetChanged();
		//this.notifyDataSetInvalidated();
	}
	
	public FunDevice getFunDevice(int position) {
		return (FunDevice)getItem(position);
	}

	public WifiRemoterBoard getWifiRemoterBoard(int position) {
		return (WifiRemoterBoard) getItem(position);
	}
	
	@Override
	public Object getItem(int position) {
		if(currentDevType == FunDevType.EE_DEV_BLUETOOTH ) {
			if (position >= 0 && position < mListBleDevs.size()) {
				return mListBleDevs.get(position);
			}

			if (position >= 0 && position < mListBleDevs.size()) {
				return mListBleDevs.get(position);
			}
		}else{
			if(currentDevType == FunDevType.EE_DEV_NORMAL_MONITOR) {
				if (position >= 0 && position < mListDevs.size()) {
					return mListDevs.get(position);
				}

				if (position >= 0 && position < mListDevs.size()) {
					return mListDevs.get(position);
				}
			}else{
				if (position >= 0 && position < mListWifiRemoterBoardDevs.size()) {
					return mListWifiRemoterBoardDevs.get(position);
				}

				if (position >= 0 && position < mListWifiRemoterBoardDevs.size()) {
					return mListWifiRemoterBoardDevs.get(position);
				}
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		if(currentDevType == FunDevType.EE_DEV_BLUETOOTH ) {
			return mListBleDevs.size();
		}else{
			if(currentDevType == FunDevType.EE_DEV_NORMAL_MONITOR) {
				return mListDevs.size();
			}else{
				return mListWifiRemoterBoardDevs.size();
			}
		}


	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int groupPosition,
			View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_device_list_item,
					null);

			holder = new ViewHolder();
			holder.imgDevIcon = (ImageView) convertView
					.findViewById(R.id.imgDevIcon);
			holder.txtDevSceneName = (TextView) convertView
					.findViewById(R.id.txtDevSceneName);
			holder.txtDevName = (TextView) convertView
					.findViewById(R.id.txtDevName);
			holder.txtDevStatus = (TextView) convertView
					.findViewById(R.id.txtDevStatus);
			holder.imgArrowIcon = (ImageView) convertView
					.findViewById(R.id.imgArrowIcon);
			holder.imgArrowIcon.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(currentDevType == FunDevType.EE_DEV_BLUETOOTH) {
			//SearchResult bleDevice = mListBleDevs.get(groupPosition);
			final SearchResult result = (SearchResult) getItem(groupPosition);

			List<Bluetooth> ble = MyApplication.liteOrm.query(new QueryBuilder<Bluetooth>(Bluetooth.class).whereEquals(Bluetooth.COL_MAC, result.getAddress()));
			if(ble.size()>0){
				holder.txtDevSceneName.setText(ble.get(0).sceneName+" 【已绑定】");
				holder.txtDevSceneName.setTextColor(mContext.getResources().getColorStateList(R.color.theme_color));
				holder.txtDevName.setTextColor(mContext.getResources().getColorStateList(R.color.theme_color));
				holder.txtDevStatus.setTextColor(mContext.getResources().getColorStateList(R.color.theme_color));
			}else{
				holder.txtDevSceneName.setText("-未绑定-");
				holder.txtDevSceneName.setTextColor(Color.BLACK);
				holder.txtDevName.setTextColor(Color.BLACK);
				holder.txtDevStatus.setTextColor(Color.BLACK);
			}

			holder.imgDevIcon.setImageResource(currentDevType.getDrawableResId());
			holder.txtDevName.setText(result.getName());

			//holder.txtDevName.setTextColor(mContext.getResources().getColorStateList(R.drawable.common_title_color));

			holder.txtDevStatus.setText(String.format("Rssi: %d", result.rssi));
			//holder.txtDevStatus.setTextColor(0xff177fca);
			//holder.txtDevStatus.setTextColor(mContext.getResources().getColor(R.color.demo_desc));

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnClickListener != null) mOnClickListener.OnClickedBle(result);
				}
			});
		} else {
			if(currentDevType == FunDevType.EE_DEV_NORMAL_MONITOR) {
				final FunDevice funDevice = mListDevs.get(groupPosition);

				List<Camera> dev = MyApplication.liteOrm.query(new QueryBuilder<Camera>(Camera.class).whereEquals(Camera.COL_MAC, funDevice.getDevMac()));
				if(dev.size()>0){
					holder.txtDevSceneName.setText(dev.get(0).sceneName+" 【已绑定】");
					holder.txtDevSceneName.setTextColor(mContext.getResources().getColorStateList(R.color.theme_color));
					holder.txtDevName.setTextColor(mContext.getResources().getColorStateList(R.color.theme_color));
				}else{
					holder.txtDevSceneName.setText("-未绑定-");
					holder.txtDevSceneName.setTextColor(Color.BLACK);
					holder.txtDevName.setTextColor(Color.BLACK);
				}

				holder.imgDevIcon.setImageResource(funDevice.devType.getDrawableResId());
				holder.txtDevName.setText(funDevice.getDevName());

				//holder.txtDevName.setTextColor(mContext.getResources().getColorStateList(R.drawable.common_title_color));

				holder.txtDevStatus.setText(funDevice.devStatus.getStatusResId());
				if (funDevice.devStatus == FunDevStatus.STATUS_ONLINE) {
					holder.txtDevStatus.setTextColor(0xff177fca);
				} else if (funDevice.devStatus == FunDevStatus.STATUS_OFFLINE) {
					holder.txtDevStatus.setTextColor(0xffda202e);
				} else {
					holder.txtDevStatus.setTextColor(mContext.getResources().getColor(R.color.demo_desc));
				}
				convertView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOnClickListener != null) mOnClickListener.OnClickedFun(funDevice);
					}
				});
			}else{
				final WifiRemoterBoard result = (WifiRemoterBoard) getItem(groupPosition);
				List<WifiRemoter> dev = MyApplication.liteOrm.query(new QueryBuilder<WifiRemoter>(WifiRemoter.class).whereEquals(WifiRemoter.COL_MAC, result.getMWifiRemoter().mac));
				if(dev.size()>0){
					holder.txtDevSceneName.setText(dev.get(0).sceneName+" 【已绑定】");
					holder.txtDevSceneName.setTextColor(mContext.getResources().getColorStateList(R.color.theme_color));
					holder.txtDevName.setTextColor(mContext.getResources().getColorStateList(R.color.theme_color));

					//if (dev.get(0).isOnline) {
						holder.txtDevStatus.setText("在线");
						holder.txtDevStatus.setTextColor(0xff177fca);
					//} else  {
						//holder.txtDevStatus.setText("离线");
						//holder.txtDevStatus.setTextColor(mContext.getResources().getColor(R.color.demo_desc));
						//holder.txtDevStatus.setTextColor(0xffda202e);
					//}
				}else{
					holder.txtDevSceneName.setText("-未绑定-");
					holder.txtDevSceneName.setTextColor(Color.BLACK);
					holder.txtDevName.setTextColor(Color.BLACK);
					holder.txtDevStatus.setText("离线");
					holder.txtDevStatus.setTextColor(mContext.getResources().getColor(R.color.demo_desc));
				}


				holder.imgDevIcon.setImageResource(currentDevType.getDrawableResId());
				holder.txtDevName.setText(result.getMWifiRemoter().name);

				holder.txtDevName.setTextColor(mContext.getResources().getColor(R.color.title_text_bg_gray));

				//holder.txtDevStatus.setVisibility(View.GONE);
				//holder.txtDevStatus.setText(String.format("Rssi: %d", result.rssi));
				//holder.txtDevStatus.setTextColor(0xff177fca);
				//holder.txtDevStatus.setTextColor(mContext.getResources().getColor(R.color.demo_desc));

				convertView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOnClickListener != null) mOnClickListener.OnClickedWifiRmoter(result);
					}
				});
			}
		}
		return convertView;
	}

	@Override
	public int compare(SearchResult lhs, SearchResult rhs)  {
		return rhs.rssi - lhs.rssi;
	}

	private class ViewHolder {
		ImageView imgDevIcon;
		TextView txtDevSceneName;
		TextView txtDevName;
		TextView txtDevStatus;
		ImageView imgArrowIcon;
	}

	public interface OnClickListener {
		public void OnClickedBle(SearchResult searchResult);
		public void OnClickedFun(FunDevice funDevice);
		public void OnClickedWifiRmoter(WifiRemoterBoard wifiRemoterBoard);
	}
}
