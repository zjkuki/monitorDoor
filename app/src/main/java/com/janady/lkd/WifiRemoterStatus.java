package com.janady.lkd;

public enum WifiRemoterStatus {

    ERROR_PASS(0, "密码错误\n"),
    ERROR_COMM(1, "密码正确，命令错误\n"),
    SET_ERROR(2, "密码正确，命令正确，操作码不正确\n"),
    CHA_OK(3, "更改密码成功\n"),
    SET_OPEN(4,"控制开\n"),
    SET_CLOSE(5,"控制关\n"),
    SET_LOCK(6,"控制锁\n"),
    SET_STOP(7,"控制停\n"),
    DISCONNECTED(7,"已断开\n"),
    CONNECTED(8,"已连接\n"),
    LOCKED(17,"已锁\n"),
    UNLOCKED(18,"无锁\n"),
    RESETTED(20,"设备已重置为出厂状态\n"),
    NORESETTED(21,"正常配置状态\n"),
    SET_OPEN_SUCCESS(200,"开门成功"),
    SET_OPEN_FAILE(201,"开门失败"),
    SET_CLOSE_SUCCESS(202,"关门成功"),
    SET_CLOSE_FAILE(203,"关门失败"),
    SET_LOCK_SUCCESS(204,"锁成功"),
    SET_LOCK_FAILE(205,"锁失败"),
    SET_STOP_SUCCESS(206,"停成功"),
    SET_STOP_FAILE(207,"停失败");



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
