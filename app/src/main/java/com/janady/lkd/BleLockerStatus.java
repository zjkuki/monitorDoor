package com.janady.lkd;

public enum BleLockerStatus {

    ERROR_PASS(0, "密码错误"),
    ERROR_COMM(1, "密码正确，命令错误"),
    SET_ERROR(2, "密码正确，命令正确，操作码不正确"),
    CHA_OK(3, "更改密码成功"),
    SET_OPEN(4,"控制开"),
    SET_CLOSE(5,"控制关"),
    SET_LOCK(6,"控制锁"),
    SET_STOP(7,"控制停"),
    DISCONNECTED(7,"已断开"),
    CONNECTED(8,"已连接"),
    NOTIFY_SUCCESS(9,"消息通知"),
    WRITE_SUCCESS(10,"发送成功"),
    WRITE_FAIL(11,"发送失败"),
    HEARTBEAT_SUCCESS(12,"心跳包"),
    READ_RESPONSE_SUCCESS(13,"读取回复成功"),
    READ_RESPONSE_FAIL(14,"读取回复失败"),
    DEVICE_REDAY(15,"已准备"),
    DEVICE_RSSI_GETTING(16,"获取Rssi");


    private int mStatusId;
    private int mStatusResId;
    private String mStatusMsg;

    BleLockerStatus(int id, String message) {
        mStatusId = id;
        //mStatusResId = resId;
        mStatusMsg = message;
    }

    public static BleLockerStatus getStatus(int id) {
        for ( BleLockerStatus streamType : BleLockerStatus.values() ) {
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
