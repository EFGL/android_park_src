package com.gz.gzcar.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.gz.gzcar.MyApplication;

/**
 * 下载的server
 * 分5个线程，
 *@ClassName: DownLoadServer
 *
 */
public class DownLoadServer extends Service{


	public  Boolean log=false;

	public static int handler_in_out_record_download= MyApplication.app_handler_in_out_record_download;//通行记录
	public static int handler_down_tempfee=MyApplication.app_handler_down_tempfee;//临时车收费
	public static int handler_down_info_stall=MyApplication.app_handler_down_info_stall;//下传车位表
	public static int handler_down_info_vehicle=MyApplication.app_handler_down_info_vehicle;//下传固定车信息表
	public static int handler_down_record_stall_vehicle=MyApplication.app_handler_down_record_stall_vehicle;//下传车位和车辆绑定表


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		start_down_server(handler_in_out_record_download);
		start_down_server(handler_down_tempfee);
		start_down_server(handler_down_info_stall);
		start_down_server(handler_down_info_vehicle);
		start_down_server(handler_down_record_stall_vehicle);
	}
	private void start_down_server(final int sendtime) {
		Runnable runnable=new Runnable() {
			@Override
			public void run() {
				do{
					Message message=new Message();
					message.arg1=sendtime;
					handler.sendMessage(message);
					try {
						Thread.sleep(sendtime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}while(true);
			}
		};
		Thread thread=new Thread(runnable);
		thread.start();
	}

	public void showlog(String msg){
		if(log)
			Log.i("chenghao", msg);
	}
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int getmsg_arg=msg.arg1;
			DownloadServerMessage message=new DownloadServerMessage();
			if(getmsg_arg==handler_in_out_record_download){
				message.getallmessage(1);
				showlog("执行下载通行记录");
			}else if(getmsg_arg==handler_down_tempfee){
				showlog("执行下载临时车收费");
				message.getallmessage(2);
			}
			else if(getmsg_arg==handler_down_info_stall){
				showlog("执行下载下传车位表");
				message.getallmessage(3);
			}
			else if(getmsg_arg==handler_down_info_vehicle){
				showlog("执行下载下传固定车信息表");
				message.getallmessage(4);
			}
			else if(getmsg_arg==handler_down_record_stall_vehicle){
				showlog("执行下载下传车位和车辆绑定表");
				message.getallmessage(5);
			}
		}};

}
