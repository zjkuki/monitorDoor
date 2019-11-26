package com.janady;

public class AppConstants {
	/** 下拉刷新列表的各个状态 */
    public static final int LIST = 0;
    public static final int EMPTY = 1;
    public static final int ERROR = 2;
    public static final int LOADING = 3;
    public static final int ALLOW_PULL_IN_EMPTY_PAGE = 4; // 没有内容，但是允许下拉刷新

    public static final String bleService = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String bleNotifitesCharacter = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String bleWriteCharacter = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    public static final String IOT_API_HOST = "http://kukipc.frp.365yiding.cn:3277";


}
