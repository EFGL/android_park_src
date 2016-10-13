package com.gz.gzcar.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Endeavor on 2016/9/25 0025.
 * Detail：带时分秒
 */

public class DateUtils {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static Calendar calendar;

    public static String change(String date) {
        // +8:00 -> String
        try {

            String time = date.split("M")[0].replace("T", " ").substring(0, 11);
            return time;
        }catch (Exception e){
            e.toString();
            return date;
        }
    }

    public static String date2String(Date date) {
        // Date -> String
        return dateFormat.format(date);
    }
    public static String date2StringDetail(Date date) {
        // Date -> String
        return dateFormatDetail.format(date);
    }

    public static Date string2Date(String date) {
        // String -> Date
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date string2DateDetail(String date) {
        // String -> Date
        try {
            return dateFormatDetail.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取当前年份
    public static int getCurrentYear(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.YEAR);
    }
    // 获取当前月份
    public static int getCurrentMonth(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MONTH)+1;
    }
    // 获取当前日期
    public static int getCurrentDay(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    // 获取当前小时
    public static int getCurrentHours(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    // 获取当前分钟
    public static int getCurrentinute(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MINUTE);

    }
    // 获取当前时间字符串表现形式 yyyy-MM-dd
    public static String getCurrentDataStr(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year+"-"+month+"-"+day;
    }

    // 获取当前时间Date形式 yyyy-MM-dd
    public static Date getCurrentData(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return string2Date(year+"-"+month+"-"+day);
    }

    // 获取当前时间字符串表现形式 yyyy-MM-dd HH:mm
    public static String getCurrentDataDetailStr(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        Log.e("11111111111111111", "month==" + month);
        return year+"-"+month+"-"+day+" "+hours+":"+minute;
    }

    // 获取当前时间Date形式 yyyy-MM-dd HH:mm
    public static Date getCurrentDataDetail(){
        if (calendar==null){

            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return string2DateDetail(year+"-"+month+"-"+day+" "+hours+":"+minute);
    }
}
