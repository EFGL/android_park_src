package com.gz.gzcar.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.MyApplication;

/**
 * 上传和下行服务
 *@ClassName: SendService 
 *1，onCreate开始现成，while一直循环，在while里面判断 startsend是否发送handler，true发送，false不发送
 *2，handler收到消息，执行全部下载的代码
 *3，handler收到消息，执行startupload（）；
 *4.查询TrafficInfoTable表中的上传是否为false，如果有false，那么就上传，上传完后，修改数据库，把false改完true
 *5，一条执行完在查询，始终调用）—4—.直到全为true了，那么就停止
 *6，进入sleep的状态
 */
public class SendService extends Service{
	/**
	 * DB
	 */
	public static DbManager db =x.getDb(MyApplication.daoConfig);

	/**
	 * 是否发送handler
	 */
	public static Boolean startsend=true;

	public static String mycontroller_sn="001";

	public static int handlersendtime=5000;

	public static Boolean log=true;

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
				while(true){
					if(startsend){
						showlog("发送");
						handler.sendEmptyMessage(1);
						startsend=false;
					}
				}
			}
		};
		Thread thread=new Thread(runnable);
		thread.start();
		showlog(thread.getName());
	}

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//调用下载全部，目的，下载全部所需要的值
			DownloadServerMessage message=new DownloadServerMessage();
			message.getallmessage();
			startupload();
		}};

		/**
		 * 打印log
		 * @param msg
		 */
		public void showlog(String msg){
			if(log){
				Log.i("chenghao", msg);
			}
		}

		/**
		 * 开始进行上传记录
		 * 1，进入就先查询下，如果有值，那么就开始上传
		 */
		public void startupload() {
			try {
				TrafficInfoTable table=db.selector(TrafficInfoTable.class).where("modife_flage", "=", false).findFirst();
				if(table!=null){
					String jsonstr=production_jsonstr(table);
					post_in_out_record_upload(mycontroller_sn, jsonstr, getpicname(table.getOut_image()), getpicname( table.getOut_image()), table.getOut_image(), table.getIn_image(),table);
				}else {
					showlog("方法执行完成");
					try {
						Thread.sleep(handlersendtime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					showlog("唤醒发送");
					startsend=true;
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 把图片转换成base64的字符串
		 * 如果传空串，直接返回空串
		 * @param filepath
		 * @return
		 */
		public String getbase64msg(String filepath){
			try {
				if(filepath!=null){
					InputStream inputStream_out = new FileInputStream(new File(filepath));
					byte[] bytes = readInputStream(inputStream_out);
					String mypicstr = Base64.encodeToString(bytes, Base64.DEFAULT);
					return mypicstr;
				}else {
					return "";
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 根据图片路径返回对应的图片名称
		 * @param path
		 * @return
		 */
		public String getpicname(String path){
			if(path==null){
				return "";
			}else {
				String [] mypath=path.split("/");
				return mypath[mypath.length-1];
			}
		}

		/**
		 * 上传通行记录
		 * @param controller_sn  (设备号）
		 * @param str (数据json串)
		 * @param out_imagename （出场图片名称带后缀）（入场记录不用填写）
		 * @param in_imagename      (入场图片名称带后缀）（出场记录不用填写）
		 * @param out_imagefile 出场图片文件路径
		 * @param in_imagefile 入场图片文件路径
		 * @param table 上传成功后，修改这个指
		 * @throws IOException
		 */
		public void post_in_out_record_upload(String controller_sn,String str,String out_imagename,String in_imagename,String out_imagefile,String in_imagefile,final TrafficInfoTable table) {
			RequestParams params=new RequestParams("http://221.204.11.69:3002/api/v1/in_out_record_upload");
			params.addBodyParameter("controller_sn",controller_sn);
			params.addBodyParameter("str",str);
			params.addBodyParameter("out_imagename",out_imagename);
			params.addBodyParameter("in_imagename",in_imagename);
			params.addBodyParameter("out_imagefile",getbase64msg(out_imagefile));
			params.addBodyParameter("out_imagefile",getbase64msg(in_imagefile));
			showlog("开始上传记录，params为＝"+params.toString());
			x.http().post(params, new CommonCallback<String>() {
				@Override
				public void onCancelled(CancelledException arg0) {
				}
				@Override
				public void onError(Throwable arg0, boolean arg1) {
				}
				@Override
				public void onFinished() {
				}
				@Override
				public void onSuccess(String arg0) {
					showlog("成功返回："+arg0);
					try {
						JSONObject object=new JSONObject(arg0);
						if(1==object.getInt("ref")){
							//上传成功，开始修改传入的bean
							updateBean(table);
							//修改完成后，继续查
							showlog("继续查询");
							try {
								TrafficInfoTable table=db.selector(TrafficInfoTable.class).where("modife_flage", "=", false).findFirst();
								if(table!=null){
									String jsonstr=production_jsonstr(table);
									post_in_out_record_upload(mycontroller_sn, jsonstr, getpicname(table.getOut_image()), getpicname( table.getOut_image()), table.getOut_image(), table.getIn_image(),table);
								}else {
									showlog("方法执行完成");
									try {
										Thread.sleep(handlersendtime);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									showlog("唤醒");
									startsend=true;
								}
							} catch (DbException e) {
								e.printStackTrace();
							}
						};
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
		/**
		 * 修改传入的数据，把false改完true，根据自增长的id修改
		 * @param table
		 */
		public void updateBean(TrafficInfoTable table){
			try {
				db.update(TrafficInfoTable.class, WhereBuilder.b("id", "=", table.getId()),new KeyValue("modife_flage",true));
			} catch (DbException e) {
				e.printStackTrace();
			}
			showlog("修改数据库完成");
		}
		private static byte[] readInputStream(InputStream inputStream)throws IOException {
			byte[] buffer = new byte[4096];
			int len = 0;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.close();
			return bos.toByteArray();
		}

		/**
		 * 生成json字符串
		 * @param table
		 * @return
		 */
		public String production_jsonstr(TrafficInfoTable table){
			//根据传入的table，生成对应的jsonstr
			UploadBean uploadBean=new UploadBean();
			uploadBean.setPass_no(table.getPass_no()+"");
			uploadBean.setCard_type(table.getCar_type());
			uploadBean.setCar_no(table.getCar_no());
			uploadBean.setIn_time(table.getIn_time().toString());
			uploadBean.setIn_operator(table.getIn_user());
			uploadBean.setIn_image(table.getIn_image());
			uploadBean.setStall_code(table.getStall());
			uploadBean.setOut_time(table.getOut_time().toString());
			uploadBean.setOut_operator(table.getOut_user());
			uploadBean.setOut_image(table.getOut_image());
			uploadBean.setStatus(table.getStatus());
			uploadBean.setFee(table.getReceivable()+"");
			uploadBean.setFact_fee(table.getActual_money()+"");
			uploadBean.setCreated_at(table.getUpdateTime().toString());
			uploadBean.setParked_time(table.getStall_time());
			String str=JSON.toJSONString(uploadBean);
			showlog("生产的json为＝"+str);
			return str;
		}

}
