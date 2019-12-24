package com.janady.lkd;

public enum WifiRemoterStatus {
    SERVER_CONNECGED(100,"服务器连接成功"),
    SERVER_DISCONNECTED(101,"服务器连接断开"),
    DEVICE_ONLINE(102,"设备在线"),
    DEVICE_OFFLINE(103,"设备离线"),
    SET_OPEN_SUCCESS(200,"开门成功"),
    SET_OPEN_FAILE(201,"开门失败"),
    SET_CLOSE_SUCCESS(202,"关门成功"),
    SET_CLOSE_FAILE(203,"关门失败"),
    SET_LOCK_SUCCESS(204,"锁成功"),
    SET_LOCK_FAILE(205,"锁失败"),
    SET_STOP_SUCCESS(206,"停成功"),
    SET_STOP_FAILE(207,"停失败"),
    SET_DOORNO_CHANGED_SUCCESS(208,"更改默认门号成功"),
    SET_DOORNO_CHANGED_FAILE(209,"更改默认门号失败"),
    SET_PASSWORD_CHECK_SUCCESS(210,"密码校验通过"),
    SET_PASSWORD_CHECK_FAILE(211,"密码校验失败"),
    SET_PASSWORD_SET_SUCCESS(212,"密码设置成功"),
    SET_PASSWORD_SET_FAILE(213,"密码设置失败");



    private int mStatusId;
    private int mStatusResId;
    private String mStatusMsg;

    WifiRemoterStatus(int id, String message) {
        mStatusId = id;
        //mStatusResId = resId;
        mStatusMsg = message;
    }

    public static WifiRemoterStatus getStatus(int id) {
        for ( WifiRemoterStatus streamType : WifiRemoterStatus.values() ) {
            if ( streamType.getSatusId() == id ) {
                return streamType;
            }
        }
        return null;
    }

    public int getSatusId() {
        return mStatusId;
    }

    public int getStatusResId() {
        return mStatusResId;
    }

    public String getmStatusMsg() {
        return mStatusMsg;
    }
}
