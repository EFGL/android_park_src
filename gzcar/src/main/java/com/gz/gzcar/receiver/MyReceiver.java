package com.gz.gzcar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gz.gzcar.MainActivity;
import com.gz.gzcar.utils.L;
import com.gz.gzcar.utils.T;

/**
 * Created by Endeavor on 2016/11/8 0008.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        L.showlogError("------停车场收到开机广播 ---------");
        T.showShort(context,"停车场收到开机广播 ");
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
