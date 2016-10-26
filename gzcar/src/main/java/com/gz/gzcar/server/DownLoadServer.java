package com.gz.gzcar.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 下载的server
 *@ClassName: DownLoadServer 
 *启动一个线程，跑DownloadServerMessage这个方法
 *
 */
public class DownLoadServer extends Service{
	public  Boolean log=false;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Runnable runnable=new Runnable() {
			@Override
			public void run() {
				DownloadServerMessage message=new DownloadServerMessage();
				message.getallmessage();
			}
		};
		Thread thread=new Thread(runnable);
		thread.start();
	}
	public void showlog(String msg){
		if(log)
			Log.i("chenghao", msg);
	}
}
