package com.gz.gzcar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Endeavor on 2016/9/25 0025.
 * Detail：带时分秒
 */

public class DateUtils {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
}
