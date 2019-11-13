package com;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    /**
     * java时间戳是13位的，php后台是10位的，所以要截断
     */
    private static long timestampConvent(long stamp) {
        String temp = stamp + "";
        return Long.parseLong(temp.substring(0, 10));
    }


    /**
     * 获取今天的时间戳
     */
    public static long getNowTimeStamp() {
        return timestampConvent(new Date().getTime());
    }

    /**
     * 获取明天的时间戳
     */
    public static long getTomorrowTimeStamp() {
        Calendar tomrrow = Calendar.getInstance();
        tomrrow.add(Calendar.DAY_OF_MONTH, 1);
        return timestampConvent(tomrrow.getTime().getTime());
    }

    /**
     * 获取指定时间的时间戳
     */
    public static long getTimeStamp(String time, String format) {
        Date date;
        //注意format的格式要与日期String的格式相匹配
        DateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return timestampConvent(date.getTime());
    }


    /**
     * 把时间戳转为阅读友好的字符串
     *
     * @param time 10位时间戳，因为服务器端传过来的是10位，所以在android里面使用时需要*1000增加到13位
     */
    public static String getFormatTime(long time) {
        Calendar target = Calendar.getInstance();
        target.setTime(new Date(time * 1000));

        String format = "yyyy年MM月dd日 HH:mm:ss";
        Calendar today = Calendar.getInstance();

        if (today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == target.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)) {
            //今天
            if (target.get(Calendar.HOUR_OF_DAY) < 3) {
                format = "凌晨 HH:mm:ss";
            } else if (target.get(Calendar.HOUR_OF_DAY) < 12) {
                format = "上午 HH:mm:ss";
            } else if (target.get(Calendar.HOUR_OF_DAY) < 18) {
                format = "下午 HH:mm:ss";
            } else {
                format = "晚上 HH:mm:ss";
            }
        } else {
            target.add(Calendar.DAY_OF_MONTH, 1);//加1，如果时间为昨天的话，加1之后的时间就是今天了

            if (today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                    today.get(Calendar.MONTH) == target.get(Calendar.MONTH) &&
                    today.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)) {
                //昨天
                format = "昨天 HH:mm:ss";
            } else {
                target.setTime(new Date(time * 1000));

                if (target.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                    format = "MM月dd日 HH:mm:ss";
                }
            }
        }
        return timestampToString(time + "", format);
    }

    /**
     * 10位时间戳转换为指定格式的时间字符串
     */
    public static String timestampToString(String time, String format) {
        if (time.length() > 3) {
            long temp = Long.parseLong(time) * 1000;
            Timestamp ts = new Timestamp(temp);
            String tsStr = "";
            DateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
            try {
                tsStr = dateFormat.format(ts);
                System.out.println(tsStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tsStr;
        } else {
            return "";
        }
    }
}
