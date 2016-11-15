package com.chenghao.carprint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.gprinter.service.GpPrintService;

public class Mybracast extends BroadcastReceiver {
	
	

	@Override
	public void onReceive(Context context, Intent arg1) {
		int type = arg1.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
		if(type==3){
			Toast.makeText(context, "打印机连接成功", Toast.LENGTH_LONG).show();
		}else if(type==2){
			Toast.makeText(context, "正在连接打印机，请选择确定按钮", Toast.LENGTH_LONG).show();
		}
	}
	public void showlog(String log){
		Log.i("chenghao", log);
	}
}
