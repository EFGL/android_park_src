package com.gz.gzcar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gz.gzcar.utils.L;

/**
 * Created by Endeavor on 2016/11/8 0008.
 */

public class MyReceiver extends BroadcastReceiver {
    private final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        L.showlogError("action=="+action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

            if (reason == null)
                return;

            // Home键
            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                Toast.makeText(context, "按了Home键", Toast.LENGTH_SHORT).show();
            }

            // 最近任务列表键
            if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                Toast.makeText(context, "按了最近任务列表", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
