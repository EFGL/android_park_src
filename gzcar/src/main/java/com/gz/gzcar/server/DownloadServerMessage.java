package com.gz.gzcar.server;

import android.util.Log;

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

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 下载一切需要的记录
 */
public class DownloadServerMessage {
	/**
	 * DB
	 */
	public static DbManager db =x.getDb(MyApplication.daoConfig);

	public static String mycontroller_sn="1";

	public static String url="http://221.204.11.69:3002/api/v1";

	public static Boolean log=true;

	/**
	 * 开始下载全部数据
	 */
	public void getallmessage(){
		try {
			DownloadTimeBean bean=db.selector(DownloadTimeBean.class).findFirst();
			if(bean!=null){
				startdown(bean);
			}else {
				//初始化新至进去
				DownloadTimeBean timeBean=new DownloadTimeBean();
				timeBean.setTime("1970-1-1 01:00:00");
				db.save(timeBean);
				startdown(timeBean);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	public void startdown(DownloadTimeBean bean){
		//开始下载
		try {
			get_in_out_record_upload(url, bean.getTime(), mycontroller_sn);
			get_down_tempfee(url, bean.getTime(), mycontroller_sn);
			get_down_info_stall(url, bean.getTime(), mycontroller_sn);
			get_down_info_vehicle(url, bean.getTime(), mycontroller_sn);
			get_down_record_stall_vehicle(url, bean.getTime(), mycontroller_sn);
			//修改时间
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			db.update(DownloadTimeBean.class, WhereBuilder.b("time", "=", bean.getTime()),new KeyValue("time",dateFormat.format(new Date())));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载通行记录
	 * @param time   上次记录时间
	 * @param controller_sn  设备号
	 * @param baseurl  base路径
	 */
	private void get_in_out_record_upload(String baseurl,String time,String controller_sn){
		RequestParams params=new RequestParams(baseurl+"/in_out_record_upload");
		params.addBodyParameter("controller_sn",controller_sn);
		params.addBodyParameter("max_updated_at",time);
		x.http().get(params, new CommonCallback<String>() {
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
				showlog("下载通行记录返回:"+arg0);
				try {
					JSONObject object=new JSONObject(arg0);
					String ref=object.getString("ref");
					if("1".equals(ref)){
						showlog("下载通行记录返回成功。result长度为＝"+object.getString("result").length());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * 下传临时车收费明细
	 * @param time   上次记录时间
	 * @param controller_sn  设备号
	 * @param baseurl  base路径
	 */
	private void get_down_tempfee(String baseurl,String time,String controller_sn){
		RequestParams params=new RequestParams(baseurl+"/down_tempfee");
		params.addBodyParameter("controller_sn",controller_sn);
		params.addBodyParameter("max_updated_at",time);
		x.http().get(params, new CommonCallback<String>() {
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
				showlog("下传临时车收费明细返回:"+arg0);
				try {
					JSONObject object=new JSONObject(arg0);
					String ref=object.getString("ref");
					if("0".equals(ref)){
						showlog("下传临时车收费明细返回成功。result长度为＝"+object.getString("result").length());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}



	/**
	 * 下传车位表
	 * @param time   上次记录时间
	 * @param controller_sn  设备号
	 * @param baseurl  base路径
	 */
	private void get_down_info_stall(String baseurl,String time,String controller_sn){
		RequestParams params=new RequestParams(baseurl+"/down_info_stall");
		params.addBodyParameter("controller_sn",controller_sn);
		params.addBodyParameter("max_updated_at",time);
		x.http().get(params, new CommonCallback<String>() {
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
				showlog("下传车位表返回:"+arg0);
				try {
					JSONObject object=new JSONObject(arg0);
					String ref=object.getString("ref");
					if("0".equals(ref)){
						showlog("下传车位表返回成功。result长度为＝"+object.getString("result").length());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 下传固定车信息表
	 * @param time   上次记录时间
	 * @param controller_sn  设备号
	 * @param baseurl  base路径
	 */
	private void get_down_info_vehicle(String baseurl,String time,String controller_sn){
		RequestParams params=new RequestParams(baseurl+"/down_info_vehicle");
		params.addBodyParameter("controller_sn",controller_sn);
		params.addBodyParameter("max_updated_at",time);
		x.http().get(params, new CommonCallback<String>() {
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
				showlog("下传固定车信息表返回:"+arg0);
				try {
					JSONObject object=new JSONObject(arg0);
					String ref=object.getString("ref");
					if("0".equals(ref)){
						showlog("下传固定车信息表返回成功。result长度为＝"+object.getString("result").length());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 下传车位和车辆绑定表
	 * @param time   上次记录时间
	 * @param controller_sn  设备号
	 * @param baseurl  base路径
	 */
	private void get_down_record_stall_vehicle(String baseurl,String time,String controller_sn){
		RequestParams params=new RequestParams(baseurl+"/down_record_stall_vehicle");
		params.addBodyParameter("controller_sn",controller_sn);
		params.addBodyParameter("max_updated_at",time);
		x.http().get(params, new CommonCallback<String>() {
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
				showlog("下传车位和车辆绑定表返回:"+arg0);
				try {
					JSONObject object=new JSONObject(arg0);
					String ref=object.getString("ref");
					if("0".equals(ref)){
						showlog("下传车位和车辆绑定表返回成功。result长度为＝"+object.getString("result").length());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 打印log
	 * @param msg
	 */
	public void showlog(String msg){
		if(log){
			Log.i("chenghao", msg);
		}
	}
}
