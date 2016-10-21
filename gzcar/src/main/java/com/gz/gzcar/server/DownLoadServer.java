package com.gz.gzcar.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * 下载的server
 *@ClassName: DownLoadServer 
 *每5分钟下载一次
 *
 */
public class DownLoadServer extends Service{

	public static int handlersendtime=1000*30;

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
				do{
					handler.sendEmptyMessage(2);
					try {
						Thread.sleep(handlersendtime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}while(true);
			}
		};
		Thread thread=new Thread(runnable);
		thread.start();
	}

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			DownloadServerMessage message=new DownloadServerMessage();
			message.getallmessage();
		}};

}
