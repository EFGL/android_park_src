package com.gz.gzcar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gz.gzcar.utils.L;
import com.gz.gzcar.utils.T;

/**
 * Created by Endeavor on 2016/11/8 0008.
 */

public class SdcardReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        L.showlogError("------SdcardReceiver收到开机广播 ---------");
        T.showShort(context,"SdcardReceiver收到开机广播 ");
//        Intent i = new Intent(context, MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
    }
}
