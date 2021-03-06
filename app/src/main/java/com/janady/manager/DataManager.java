package com.janady.manager;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.funsdkdemo.MyApplication;
import com.janady.database.model.TestFragmentState;
import com.lkd.smartlocker.R;
import com.inuker.bluetooth.library.search.SearchResult;
import com.janady.database.model.Bluetooth;
import com.janady.database.model.Camera;
import com.janady.database.model.Door;
import com.janady.database.model.WifiRemoter;
import com.janady.device.AddDeviceFragment;
import com.janady.device.BluetoothEditFragment;
import com.janady.device.BluetoothListFragment;
import com.janady.device.BluetoothLockFragment;
import com.janady.device.CameraListFragment;
import com.janady.device.DeviceCameraFragment;
import com.janady.device.DoorEditFragment;
import com.janady.device.DoorListFragment;
import com.janady.device.RemoteEditFragment;
import com.janady.device.RemoteListFragment;
import com.janady.device.RemoteListFragment_old;
import com.janady.device.WifiRemoterBoardFragment;
import com.janady.model.CategoryItemDescription;
import com.janady.model.ItemDescription;
import com.janady.model.MainItemDescription;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public List<SearchResult> mBleDevices;
    public List<FunDevice> mFunDevices;
    public List<WifiRemoter> mWifiRemoters;

    public TestFragmentState testFragmentState;

    private static Context mcontext = MyApplication.getContext();;

    private static DataManager _sInstance;
    public static DataManager getInstance() {
        if (_sInstance == null) {
            _sInstance = new DataManager();
        }
        return _sInstance;
    }
    public static DataManager getInstance(Context context) {
        if (_sInstance == null) {
            _sInstance = new DataManager();
        }
        mcontext = context;
        return _sInstance;
    }
    public List<CategoryItemDescription> getCategoryDesciptions() {
        List<CategoryItemDescription> list = new ArrayList<>();

//        Door d = new Door();
//        d.name = "default";
//        MyApplication.liteOrm.save(d);
        // ArrayList<Camera> clists = MyApplication.liteOrm.query(Camera.class);
        List<FunDevice> funDevices = FunSupport.getInstance().getDeviceList();
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        ArrayList<WifiRemoter> rlists = MyApplication.liteOrm.cascade().query(WifiRemoter.class);
        ArrayList<Door> dlists = MyApplication.liteOrm.query(Door.class);
        CategoryItemDescription camera = new CategoryItemDescription(CameraListFragment.class, mcontext.getString(R.string.CAMERA), R.drawable.ic_camera, funDevices.size());
        //CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothListFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth, blists.size());
        //CategoryItemDescription remote = new CategoryItemDescription(RemoteListFragment_old.class, "远程控制", R.drawable.ic_remote, rlists.size());
        CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothListFragment.class, mcontext.getString(R.string.BLUETOOTH), R.drawable.ic_bluetooth_black_24dp, blists.size());
        CategoryItemDescription remote = new CategoryItemDescription(RemoteListFragment_old.class, mcontext.getString(R.string.WIFI_REMOTER), R.drawable.ic_remote_3, rlists.size());
        CategoryItemDescription room = new CategoryItemDescription(DoorListFragment.class, "场景", R.drawable.ic_room2, dlists.size());
        list.add(camera);
        list.add(bluetooth);
        list.add(remote);
        list.add(room);
        return list;
    }
    public List<CategoryItemDescription> showCategoryDesciptions() {
        List<CategoryItemDescription> list = new ArrayList<>();

        CategoryItemDescription camera = new CategoryItemDescription(AddDeviceFragment.class, mcontext.getString(R.string.CAMERA), R.drawable.ic_camera, 0);
        //CategoryItemDescription remoteConfig = new CategoryItemDescription(ELinkWifiConfigFragment.class, "WIFI板配网", R.drawable.ic_remote, 0);
        /*CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothEditFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth, 0);
        CategoryItemDescription remote = new CategoryItemDescription(RemoteEditFragment.class, "远程控制", R.drawable.ic_remote, 0);
        CategoryItemDescription room = new CategoryItemDescription(DoorEditFragment.class, "房间", R.drawable.ic_room, 0);*/
        CategoryItemDescription bluetooth = new CategoryItemDescription(BluetoothEditFragment.class, mcontext.getString(R.string.BLUETOOTH), R.drawable.ic_bluetooth_black_24dp, 0);
        CategoryItemDescription remote = new CategoryItemDescription(RemoteEditFragment.class, mcontext.getString(R.string.WIFI_REMOTER), R.drawable.ic_remote_3, 0);
        CategoryItemDescription room = new CategoryItemDescription(DoorEditFragment.class, "门禁位置", R.drawable.ic_room2, 0);
        list.add(camera);
        //list.add(remoteConfig);
        list.add(bluetooth);
        list.add(remote);
        list.add(room);
        return list;
    }
    public List<MainItemDescription> getDescriptions() {
        List<MainItemDescription> list = new ArrayList<>();
        /*List<FunDevice> funDevices = FunSupport.getInstance().getDeviceList();
        for (FunDevice device : funDevices) {
            MainItemDescription mainDescription = new MainItemDescription(DeviceCameraFragment.class, device.devName, R.drawable.ic_camera, MainItemDescription.DeviceType.CAM);
            mainDescription.setDevice(device);
            list.add(mainDescription);
        }*/
        MainItemDescription camDescription = new MainItemDescription(CameraListFragment.class, mcontext.getString(R.string.CAMERA), R.drawable.ic_camera, MainItemDescription.DeviceType.CAM);
        ArrayList<Camera> camlists = MyApplication.liteOrm.query(Camera.class);
        List<Object> camitems = new ArrayList<>();
        for (Camera camera : camlists) {
            ItemDescription itemDescription = new ItemDescription(DeviceCameraFragment.class, camera.sceneName, R.drawable.icon_check);

            //itemDescription.setEnable(matchFunDevOnline(camera.sn));
            //itemDescription.setEnable(FunSupport.getInstance().requestDeviceStatus(BuildFunDevice(camera)));
            //itemDescription.setEnable(FunSupport.getInstance().requestDeviceStatus(camera.type,camera.mac));
            itemDescription.setEnable(camera.isOnline);
            itemDescription.setItem(camera);

            camitems.add(itemDescription);
        }
        camDescription.setList(camitems);
        list.add(camDescription);


        //MainItemDescription bleDescription = new MainItemDescription(BluetoothLockFragment.class, "蓝牙门禁", R.drawable.ic_bluetooth, MainItemDescription.DeviceType.BLE);
        MainItemDescription bleDescription = new MainItemDescription(BluetoothListFragment.class, mcontext.getString(R.string.BLUETOOTH), R.drawable.ic_bluetooth_black_24dp, MainItemDescription.DeviceType.BLE);
        ArrayList<Bluetooth> blists = MyApplication.liteOrm.query(Bluetooth.class);
        List<Object> bitems = new ArrayList<>();
        for (Bluetooth bluetooth : blists) {
            //ItemDescription itemDescription = new ItemDescription(BluetoothOperatorFragment.class, bluetooth.sceneName, R.drawable.icon_check);
            ItemDescription itemDescription = new ItemDescription(BluetoothLockFragment.class, bluetooth.sceneName, R.drawable.icon_check);

            if(testFragmentState!=null){
                if(testFragmentState.bluetooths!=null && testFragmentState.bluetooths.size()>0) {
                    for (Bluetooth b:testFragmentState.bluetooths){
                        itemDescription.setEnable(matchBleDevOnline(b.mac));
                    }
                }
            }else {
                itemDescription.setEnable(matchBleDevOnline(bluetooth.mac));
            }

            //默认online为true，在BaseItemAdapter里进行连接检测后保存
            //itemDescription.setEnable(bluetooth.isOnline);
            itemDescription.setItem(bluetooth);
            //itemDescription.setItem(new BleLocker(bluetooth,false,800,null));

            bitems.add(itemDescription);
        }
        bleDescription.setList(bitems);
        list.add(bleDescription);

        MainItemDescription remoteDescription = new MainItemDescription(RemoteListFragment.class, mcontext.getString(R.string.WIFI_REMOTER), R.drawable.ic_remote_3, MainItemDescription.DeviceType.REMOTE);
        ArrayList<WifiRemoter> rlists = MyApplication.liteOrm.cascade().query(WifiRemoter.class);
        List<Object> ritems = new ArrayList<>();
        for (WifiRemoter remote : rlists) {
            //remoteDescription.setDevice(remote);
            ItemDescription itemDescription = new ItemDescription(WifiRemoterBoardFragment.class, remote.sceneName, R.drawable.icon_check);
            itemDescription.setEnable(remote.isOnline);
            itemDescription.setItem(remote);
/*            if (remote.doorList != null) {
                List<Object> ritems = new ArrayList<>();
                for (Door door : remote.doorList) {
                    ItemDescription itemDescription = new ItemDescription(RemoteEditFragment.class, door.name, R.drawable.icon_check);
                    itemDescription.setItem(door);
                    ritems.add(itemDescription);
                }*/
                ritems.add(itemDescription);
        }
        remoteDescription.setList(ritems);
        list.add(remoteDescription);

        return list;
    }


    /**
     * 查找蓝牙设备列表是否有这个设备
     *
     * @param bleMac 搜索字符
     */
    public boolean matchBleDevOnline(String bleMac) {
        if(mBleDevices==null){return false;}
        //如果为null，直接使用全部数据
        if (!bleMac.equals("") ||  mBleDevices.size()>0) {
            //否则，匹配相应的数据
            for (int i = 0; i < mBleDevices.size(); i++) {
                if (mBleDevices.get(i).getAddress().contains(bleMac)) {//这里可拓展自己想要的，甚至可以拆分搜索汉字来匹配
                   return true;
                }
            }
            return  false;
        }else{
            return false;
        }
    }


    /**
     * 查找摄像设备列表是否有这个设备
     *
     * @param FunDevSn 搜索字符
     */
    public boolean matchFunDevOnline(String FunDevSn) {
        if(mFunDevices==null){return false;}

        //如果为null，直接使用全部数据
        if (!FunDevSn.equals("") || mFunDevices.size()>0) {
            //否则，匹配相应的数据
            for (int i = 0; i < mFunDevices.size(); i++) {
                if (mFunDevices.get(i).getDevSn().contains(FunDevSn)) {//这里可拓展自己想要的，甚至可以拆分搜索汉字来匹配
                    return true;
                }
            }
            return  false;
        }else{
            return false;
        }
    }


    public JSONObject getAllDevices2FastJson(){
        JSONObject json = new JSONObject();
        ArrayList<Camera> camlists = MyApplication.liteOrm.cascade().query(Camera.class);
        json.put("cameras", camlists);
        Log.d("DataManager", "Cameras Json:\n"+json.toString());

        ArrayList<Bluetooth> bluetooths = MyApplication.liteOrm.cascade().query(Bluetooth.class);
        json.put("bluetooths", bluetooths);
        Log.d("DataManager", "Bluetooths Json:\n"+json.toString());

        ArrayList<WifiRemoter> wifiRemoters = MyApplication.liteOrm.cascade().query(WifiRemoter.class);
        json.put("wifiremoters", wifiRemoters);
        Log.d("DataManager", "WifiRemoters Json:\n"+json.toString());

        return json;
    }

}
