package com.gz.gzcar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Endeavor on 2016/11/8 0008.
 */

public class USBDiskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path)){
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(action)) {
                Log.d("usb",path);
//                 /storage/uhost1
//                /storage/uhost
            }
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                Log.d("usb",path);
            }
        }

    }
}
