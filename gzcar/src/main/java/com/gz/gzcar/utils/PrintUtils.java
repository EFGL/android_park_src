package com.gz.gzcar.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Endeavor on 2016/10/24 0024.
 *
 * 打印方法
 */

public class PrintUtils {


    /**
     * intent.putExtra("msg", jsonStrng);
     * intent.putExtra("operatename", "张三");
     * intent.putExtra("company", "石景山首钢小区停车场");
     * <p>
     * msg的格式
     * <p>
     * {"carNumber":"京Q66478","inTime":"2016-10-1 19:00","money":999.9,"outTime":"2016-10-9 8:00","parkTime":"20小时58分钟","type":"探亲车"}
     */
    public static void print(Context context, String msg, String operatename, String company) {
        Intent intent = new Intent("cartprint");
        intent.putExtra("msg", msg);
        intent.putExtra("type","1");
        intent.putExtra("operatename", operatename);
        intent.putExtra("company", company);
        context.sendBroadcast(intent);
    }

    public static void printAll(Context context, String msg){
        Intent intent = new Intent("cartprint");
        intent.putExtra("msg", msg);
        intent.putExtra("type","2");
        context.sendBroadcast(intent);
    }
}
