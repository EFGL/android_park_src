package com.gz.gzcar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bigkoo.pickerview.view.WheelTime.dateFormat;

/**
 * Created by Endeavor on 2016/9/25 0025.
 */

public class DateUtils {
    public static SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String date2String(Date date) {
        // Date -> String
        return dateFormat.format(date);
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
}
