package com.gz.gzcar.server;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 上传和下行服务
 *@ClassName: SendService  程浩
 *1，onCreate开始现成，while一直循环，在while里面发送handler
 *2，handler收到消息，执行查询数据库的代码
 *3，handler收到消息，如果数据库里有没有上传的，那么就开始上传第一条
 *4.上传成功，修改数据库
 *
 *也就是没一次只传一条数据上去
 */
public class SendService extends Service{
	/**
	 * DB
	 */
	private static DbManager db =x.getDb(MyApplication.daoConfig);

	private static String mycontroller_sn = MyApplication.devID;
	private static Boolean log=true;

	private final long sendtime=10;

	private static  String Myurl="http://221.204.11.69:3002/api/v1/in_out_record_upload"
;
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
					try {
						TrafficInfoTable table=db.selector(TrafficInfoTable.class).where("modife_flage", "=", false).orderBy("update_time").findFirst();
						if(table!=null){
							showlog("开始上传记录");
							String jsonstr=production_jsonstr(table);
							post_in_out_record_upload(mycontroller_sn, jsonstr, getpicname(table.getOut_image()), getpicname( table.getIn_image()), table.getOut_image(), table.getIn_image(),table);
							while(true){
								try {
									Thread.sleep(20);
									table=db.selector(TrafficInfoTable.class).where("id", "=", table.getId()).findFirst();
									if(table.isModifeFlage()){
										showlog("完成上传一条记录");
										break;
									}
									else
									{
										Thread.sleep(1000);
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}else {
							showlog("记录上传：没有需要上传的记录");
							try {
								Thread.sleep(sendtime*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} catch (DbException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread=new Thread(runnable);
		thread.start();
	}

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				TrafficInfoTable table=db.selector(TrafficInfoTable.class).where("modife_flage", "=", false).findFirst();
				if(table!=null){
					showlog("立即执行更新方法");
					String jsonstr=production_jsonstr(table);
					post_in_out_record_upload(mycontroller_sn, jsonstr, getpicname(table.getOut_image()), getpicname( table.getIn_image()), table.getOut_image(), table.getIn_image(),table);
				}else {
					showlog("没有需要更新的消息");
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}};


		/**
		 * 打印log
		 * @param msg
		 */
		public void showlog(String msg){
			if(log){
				Log.i("chenghaosend", msg);
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
			RequestParams params=new RequestParams(Myurl);
			params.addBodyParameter("controller_sn",controller_sn);
			showlog("我的设备号："+controller_sn);
			params.addBodyParameter("str",str);
			params.addBodyParameter("out_imagename",out_imagename);
			params.addBodyParameter("in_imagename",in_imagename);
			if(table.getStatus() != null && table.getStatus().equals("已入")){
				params.addBodyParameter("in_imagefile",getbase64msg(in_imagefile));
			}else if(table.getStatus() != null && table.getStatus().equals("已出")){
				params.addBodyParameter("out_imagefile",getbase64msg(out_imagefile));
			}
			showlog("str＝"+str);
			showlog("out_imagename＝"+out_imagename);
			showlog("in_imagename＝"+in_imagename);
			showlog("in_imagename＝"+in_imagename);
			showlog("开始上传记录");
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
						if(0==object.getInt("ref")){
							//上传成功，开始修改传入的bean
							updateBean(table);
							//修改完成后，继续查
							showlog("修改成功");
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
				//table.setModifeFlage(true);
				//db.update(table,"modife_flage");
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
			uploadBean.setIn_time(table.getIn_time()+"");
			uploadBean.setIn_operator(table.getIn_user());
			uploadBean.setIn_image(getpicname(table.getIn_image()));
			uploadBean.setStall_code(table.getStall());
			uploadBean.setOut_time(table.getOut_time()+"");
			uploadBean.setOut_operator(table.getOut_user());
			uploadBean.setOut_image(getpicname(table.getOut_image()));
			uploadBean.setStatus(table.getStatus());
			uploadBean.setFee(table.getReceivable()+"");
			uploadBean.setFact_fee(table.getActual_money()+"");
			uploadBean.setCreated_at(table.getUpdateTime()+"");
			uploadBean.setUpdated_controller_sn(table.getUpdated_controller_sn()+"");
			uploadBean.setParked_time(table.getStall_time());
			String str=JSON.toJSONString(uploadBean);
//			showlog("生产的json为＝"+str);
			return str;
		}



}
