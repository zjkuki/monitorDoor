package com.janady.services;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;

import static com.example.funsdkdemo.MyApplication.locationService;

public class BaiduMapUtils {
	public final static String CoorType_GCJ02 = "gcj02";
	public final static String CoorType_BD09LL= "bd09ll";
	public final static String CoorType_BD09MC= "bd09";

	/***
	 *61 ： GPS定位结果，GPS定位成功。
	 *62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
	 *63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
	 *65 ： 定位缓存的结果。
	 *66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
	 *67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
	 *68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
	 *161： 网络定位结果，网络定位定位成功。
	 *162： 请求串密文解析失败。
	 *167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
	 *502： key参数错误，请按照说明文档重新申请KEY。
	 *505： key不存在或者非法，请按照说明文档重新申请KEY。
	 *601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
	 *602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
	 *501～700：key验证失败，请按照说明文档重新申请KEY。
	 */

	public static float[] EARTH_WEIGHT = {0.1f,0.2f,0.4f,0.6f,0.8f}; // 推算计算权重_地球
	//public static float[] MOON_WEIGHT = {0.0167f,0.033f,0.067f,0.1f,0.133f}; 
	//public static float[] MARS_WEIGHT = {0.034f,0.068f,0.152f,0.228f,0.304f};
	public final static int RECEIVE_TAG = 1;
	public final static int DIAGNOSTIC_TAG = 2;

	public static StringBuffer geneLocDiagnosticInfo(int locType, int diagnosticType, String diagnosticMessage){
		StringBuffer sb = new StringBuffer(256);
		sb.append("诊断结果: ");
		if (locType == BDLocation.TypeNetWorkLocation) {
			if (diagnosticType == 1) {
				sb.append("网络定位成功，没有开启GPS，建议打开GPS会更好");
				sb.append("\n" + diagnosticMessage);
			} else if (diagnosticType == 2) {
				sb.append("网络定位成功，没有开启Wi-Fi，建议打开Wi-Fi会更好");
				sb.append("\n" + diagnosticMessage);
			}
		} else if (locType == BDLocation.TypeOffLineLocationFail) {
			if (diagnosticType == 3) {
				sb.append("定位失败，请您检查您的网络状态");
				sb.append("\n" + diagnosticMessage);
			}
		} else if (locType == BDLocation.TypeCriteriaException) {
			if (diagnosticType == 4) {
				sb.append("定位失败，无法获取任何有效定位依据");
				sb.append("\n" + diagnosticMessage);
			} else if (diagnosticType == 5) {
				sb.append("定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位");
				sb.append(diagnosticMessage);
			} else if (diagnosticType == 6) {
				sb.append("定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试");
				sb.append("\n" + diagnosticMessage);
			} else if (diagnosticType == 7) {
				sb.append("定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试");
				sb.append("\n" + diagnosticMessage);
			} else if (diagnosticType == 9) {
				sb.append("定位失败，无法获取任何有效定位依据");
				sb.append("\n" + diagnosticMessage);
			}
		} else if (locType == BDLocation.TypeServerError) {
			if (diagnosticType == 8) {
				sb.append("定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限");
				sb.append("\n" + diagnosticMessage);
			}
		}
		return sb;
	}
	public static StringBuffer geneLocInfo(BDLocation location){
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer(256);
		if (null != location && location.getLocType() != BDLocation.TypeServerError) {
			sb.append("time : ");
			/**
			 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
			 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
			 */
			sb.append(location.getTime());
			sb.append("\nlocType : ");// 定位类型
			sb.append(location.getLocType());
			sb.append("\nlocType description : ");// *****对应的定位类型说明*****
			sb.append(location.getLocTypeDescription());
			sb.append("\nlatitude : ");// 纬度
			sb.append(location.getLatitude());
			sb.append("\nlongtitude : ");// 经度
			sb.append(location.getLongitude());
			sb.append("\nradius : ");// 半径
			sb.append(location.getRadius());
			sb.append("\nCountryCode : ");// 国家码
			sb.append(location.getCountryCode());
			sb.append("\nProvince : ");// 获取省份
			sb.append(location.getProvince());
			sb.append("\nCountry : ");// 国家名称
			sb.append(location.getCountry());
			sb.append("\ncitycode : ");// 城市编码
			sb.append(location.getCityCode());
			sb.append("\ncity : ");// 城市
			sb.append(location.getCity());
			sb.append("\nDistrict : ");// 区
			sb.append(location.getDistrict());
			sb.append("\nTown : ");// 获取镇信息
			sb.append(location.getTown());
			sb.append("\nStreet : ");// 街道
			sb.append(location.getStreet());
			sb.append("\naddr : ");// 地址信息
			sb.append(location.getAddrStr());
			sb.append("\nStreetNumber : ");// 获取街道号码
			sb.append(location.getStreetNumber());
			sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
			sb.append(location.getUserIndoorState());
			sb.append("\nDirection(not all devices have value): ");
			sb.append(location.getDirection());// 方向
			sb.append("\nlocationdescribe: ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			sb.append("\nPoi: ");// POI信息
			if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
				for (int i = 0; i < location.getPoiList().size(); i++) {
					Poi poi = (Poi) location.getPoiList().get(i);
					sb.append("poiName:");
					sb.append(poi.getName() + ", ");
					sb.append("poiTag:");
					sb.append(poi.getTags() + "\n");
				}
			}
			if (location.getPoiRegion() != null) {
				sb.append("PoiRegion: ");// 返回定位位置相对poi的位置关系，仅在开发者设置需要POI信息时才会返回，在网络不通或无法获取时有可能返回null
				PoiRegion poiRegion = location.getPoiRegion();
				sb.append("DerectionDesc:"); // 获取POIREGION的位置关系，ex:"内"
				sb.append(poiRegion.getDerectionDesc() + "; ");
				sb.append("Name:"); // 获取POIREGION的名字字符串
				sb.append(poiRegion.getName() + "; ");
				sb.append("Tags:"); // 获取POIREGION的类型
				sb.append(poiRegion.getTags() + "; ");
				sb.append("\nSDK版本: ");
			}
			sb.append(locationService.getSDKVersion()); // 获取SDK版本
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 速度 单位：km/h
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());// 卫星数目
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 海拔高度 单位：米
				sb.append("\ngps status : ");
				sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				// 运营商信息
				if (location.hasAltitude()) {// *****如果有海拔高度*****
					sb.append("\nheight : ");
					sb.append(location.getAltitude());// 单位：米
				}
				sb.append("\noperationers : ");// 运营商信息
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
		}else{
			sb.append("");
		}

		return sb;
	}
}
