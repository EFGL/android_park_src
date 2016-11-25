package com.gz.gzcar.server;

import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewOutlineProvider;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.Database.CarWeiTable;
import com.gz.gzcar.Database.MoneyTable;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 下载一切需要的记录
 * 功能：
 * 有2个循环，大循环是控制是否执行方法。小循环是判断是否方法执行完成，如果完成那么跳出小循环，继续执行大循环记好
 */
public class DownloadServerMessage {

	private FileUtils fileUtils = new FileUtils();
	/**
	 * DB
	 */
	public static final DbManager db = x.getDb(MyApplication.daoConfig);
	/**
	 * 设备号
	 */
	public static final String mycontroller_sn = MyApplication.devID;
	/**
	 * url
	 */
	public static final String url =  MyApplication.settingInfo.getString("serverIp") + "/api/v1";
	/**
	 * 是否打印log
	 */
	public static Boolean log = true;
	/**
	 * 判断方法是否执行完成，（内循环）
	 */
	public Boolean ifinteriorok = true;

	/**
	 * 检查完所以，开始休息一次
	 */
	public long sleeptime = 6;

	/**
	 * 执行次数
	 */
	public int count = 6;
	/**
	 * 开始下载全部数据
	 */
	public void getallmessage() {
		showlog("进入download");
		try {
			DownloadTimeBean bean = db.selector(DownloadTimeBean.class).findFirst();
			if (bean != null) {
				startdown();
			} else {
				//初始化新至进去
				DownloadTimeBean timeBean = new DownloadTimeBean();
				timeBean.setHandler_down_info_stall_time("1970-1-1 01:00:00");
				timeBean.setHandler_down_info_vehicle_time("1970-1-1 01:00:00");
				timeBean.setHandler_down_record_stall_vehicle_time("1970-1-1 01:00:00");
				timeBean.setHandler_down_tempfee_time("1970-1-1 01:00:00");
				timeBean.setHandler_in_out_record_download_time("1970-1-1 01:00:00");
				db.save(timeBean);
				startdown();
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 只运行一次
	 * @param bean
	 */
	public void sendonfirst(DownloadTimeBean bean ){
		/*try {
			bean = db.selector(DownloadTimeBean.class).findFirst();
		} catch (DbException e2) {
			e2.printStackTrace();
		}
		//开机下载一次收费记录表,并下载所有记录
		String mysendtime = bean.getHandler_down_tempfee_time();*/
		String mysendtime = "1970-1-1 01:00:00";
		showlog("下载临时车收费 传入时间:" + mysendtime);
		get_down_tempfee(url, mysendtime, mycontroller_sn);
		while(ifinteriorok == false) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//开机下载一次操作员表,并下载所有记录
		get_userList(url,mysendtime,mycontroller_sn);
		while(ifinteriorok == false) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 1.先有个大循环，一直在跑
	 * 2，定义一个标识位，ifexecutetogetmessage true 和false
	 * 3.定义int 变量，方便用switch 执行判断执行哪个方法
	 * 4,定义bool变量，判断内部数据是否执行完成 ifinteriorok
	 *
	 * @param
	 */
	public void startdown() {
		String sendtime;
		DownloadTimeBean bean = null;
		sendonfirst(bean);
		showlog("起动下载" +"mycontroller_sn: "  + mycontroller_sn + ",URL:"+ url);
		while (true) {
			//取所有信息最后一次下载时间;
			try {
				bean = db.selector(DownloadTimeBean.class).findFirst();
			} catch (DbException e2) {
				e2.printStackTrace();
			}
			try {
				sendtime = bean.getHandler_in_out_record_download_time();
				showlog("下载通行记录 传入时间:" + sendtime);
				get_in_out_record_download(url, sendtime, mycontroller_sn);
				//待待接收完成
				while(ifinteriorok == false) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				count++;
				if(count>=sleeptime)
				{
					count = 0;
					for(int i=0;i<3;i++) {
						//起动对应下载任务
						switch (i) {
							case 0:
								sendtime = bean.getHandler_down_info_stall_time();
								showlog("下载车位表 传入时间:" + sendtime);
								get_down_info_stall(url, sendtime, mycontroller_sn);
								break;
							case 1:
								sendtime = bean.getHandler_down_info_vehicle_time();
								showlog("下载固定车 传入时间:" + sendtime);
								get_down_info_vehicle(url, sendtime, mycontroller_sn);
								break;
							case 2:
								sendtime = bean.getHandler_down_record_stall_vehicle_time();
								showlog("下载车辆和车位绑定 传入时间:" + sendtime);
								get_down_record_stall_vehicle(url, sendtime, mycontroller_sn);
								break;
						}//end switch
						//待待接收完成
						while(ifinteriorok == false) {
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}//end for
				}
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}//end while
	}
	class processDownRecordDatas extends AsyncTask<Void,Void,Integer>{
		private String arg0;
		public processDownRecordDatas(String arg0){
			this.arg0 = arg0;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				JSONObject object = new JSONObject(arg0);
				String ref = object.getString("ref");
				if ("0".equals(ref)) {
					String result = object.getString("result");
					if (result.length() != 2) {
						List<DownloadinandoutRecordBean> list = new ArrayList<DownloadinandoutRecordBean>();
						list = com.alibaba.fastjson.JSONObject.parseArray(result, DownloadinandoutRecordBean.class);
						//下一步根据list来存数据库
						for (int c = 0; c < list.size(); c++) {
							try {
								TrafficInfoTable table = db.selector(TrafficInfoTable.class).where("pass_no","=",list.get(c).getPass_no()).findFirst();
								if (table == null) {
									table = new TrafficInfoTable();
									//开始存数据
									table.setIn_user(list.get(c).getIn_operator());
									table.setPass_no(list.get(c).getPass_no());
									table.setCar_type(list.get(c).getCard_type());
									table.setCar_no(list.get(c).getCar_no());
									table.setIn_time(DownUtils.getstringtodate(list.get(c).getIn_time()));
									table.setIn_image(list.get(c).getIn_image());
									table.setOut_time(DownUtils.getstringtodate(list.get(c).getOut_time()));
									table.setOut_image(list.get(c).getOut_image());
									table.setOut_user(list.get(c).getOut_operator());
									table.setStall(list.get(c).getStall_code());
									table.setReceivable(DownUtils.getstringtodouble(list.get(c).getFee()));
									table.setActual_money(DownUtils.getstringtodouble(list.get(c).getFact_fee()));
									table.setStall_time(list.get(c).getParked_time());
									table.setUpdateTime(DownUtils.getstringtodate(list.get(c).getUpdated_at()));
									table.setStatus(list.get(c).getStatus());
									table.setModifeFlage(true);
									db.save(table);
								}
								else {
									//开始存数据
									table.setIn_user(list.get(c).getIn_operator());
									table.setPass_no(list.get(c).getPass_no());
									table.setCar_type(list.get(c).getCard_type());
									table.setCar_no(list.get(c).getCar_no());
									table.setIn_time(DownUtils.getstringtodate(list.get(c).getIn_time()));
									table.setIn_image(list.get(c).getIn_image());
									table.setOut_time(DownUtils.getstringtodate(list.get(c).getOut_time()));
									table.setOut_image(list.get(c).getOut_image());
									table.setOut_user(list.get(c).getOut_operator());
									table.setStall(list.get(c).getStall_code());
									table.setReceivable(DownUtils.getstringtodouble(list.get(c).getFee()));
									table.setActual_money(DownUtils.getstringtodouble(list.get(c).getFact_fee()));
									table.setStall_time(list.get(c).getParked_time());
									table.setUpdateTime(DownUtils.getstringtodate(list.get(c).getUpdated_at()));
									table.setStatus(list.get(c).getStatus());
									table.setModifeFlage(true);
									db.update(table);
								}
							} catch (DbException e) {
								e.printStackTrace();
							}
						}
						//得到最后一条的更新时间，进行修改数据库
						String updatetiem = list.get(list.size() - 1).getUpdated_at();
						try {
							db.update(DownloadTimeBean.class, WhereBuilder.b("id", "=", 1), new KeyValue("handler_in_out_record_download_time", updatetiem));
						} catch (DbException e) {
							e.printStackTrace();
						}
						showlog("下载通行记录："+list.size()+"条");
					} else {
						showlog("下载通行记录无更新");
					}
				}
				ifinteriorok = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 下载通行记录
	 *
	 * @param time          上次记录时间
	 * @param controller_sn 设备号
	 * @param baseurl       base路径
	 */
	private void get_in_out_record_download(String baseurl, String time, String controller_sn) {
		ifinteriorok = false;
		RequestParams params = new RequestParams(baseurl + "/in_out_record_download");
		params.addBodyParameter("controller_sn", controller_sn);
		params.addBodyParameter("max_updated_at", time);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				showlog(arg0.toString());
				ifinteriorok = true;
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(String arg0) {
				new processDownRecordDatas(arg0).execute();
			}
		});
	}

	//处理收费规则表下载数据
	class processTempFeeDatas extends AsyncTask<Void,Void,Integer>{
		private String arg0;
		public processTempFeeDatas(String arg0){
			this.arg0 = arg0;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				JSONObject object = new JSONObject(arg0);
				String ref = object.getString("ref");
				if ("0".equals(ref)) {
					String result = object.getString("result");
					if (result.length() != 2) {
						//清空原有记录
						try {
							db.delete(MoneyTable.class);
						} catch (DbException e) {
							e.printStackTrace();
						}
						List<DownTemFeeBean> list = new ArrayList<DownTemFeeBean>();
						if (list != null) {
							list = com.alibaba.fastjson.JSONObject.parseArray(result, DownTemFeeBean.class);
						}
						//下一步根据list来存数据库
						for (int c = 0; c < list.size(); c++) {
							try {
								//新增记录
								MoneyTable table = new MoneyTable();
								table.setFee_detail_code(list.get(c).getFee_detail_code());
								table.setFee_code(list.get(c).getFee_code());
								table.setFee_name(list.get(c).getFee_name());
								table.setParked_min_time(list.get(c).getParked_min_time());
								table.setParked_max_time(list.get(c).getParked_max_time());
								table.setMoney(DownUtils.getstringtodouble(list.get(c).getMoney()));
								table.setCreated_at(DownUtils.getstringtodate(list.get(c).getCreated_at()));
								table.setUpdated_at(DownUtils.getstringtodate(list.get(c).getUpdated_at()));
								table.setCar_type_code(list.get(c).getCar_type_code());
								table.setCar_type_name(list.get(c).getCar_type_name());
								db.save(table);
							} catch (DbException e) {
								e.printStackTrace();
							}
						}
						showlog("下传临时车收费明细："+list.size()+"条");
					} else {
						showlog("下传临时车收费明细无更新");
					}
				}
				ifinteriorok = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 下传临时车收费明细
	 *
	 * @param time          上次记录时间
	 * @param controller_sn 设备号
	 * @param baseurl       base路径
	 */
	private void get_down_tempfee(String baseurl, String time, String controller_sn) {
		ifinteriorok = false;
		RequestParams params = new RequestParams(baseurl + "/down_tempfee");
		params.addBodyParameter("controller_sn", controller_sn);
		params.addBodyParameter("max_updated_at", time);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				ifinteriorok = true;
			}

			@Override
			public void onFinished() {
			}
			@Override
			public void onSuccess(String arg0) {
				new processTempFeeDatas(arg0).execute();
			}
		});
	}
	//处理用户下载数据
	class processDownUserDatas extends AsyncTask<Void,Void,Integer>
	{
		private  String arg0;
		public processDownUserDatas(String arg0){
			this.arg0 = arg0;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				JSONObject object = new JSONObject(arg0);
				String ref = object.getString("ref");
				if ("0".equals(ref)) {
					String result = object.getString("result");
					if (result.length() != 2) {
						//清空原有记录
						try {
							db.delete(UserTable.class);
						} catch (DbException e) {
							e.printStackTrace();
						}
						List<DownInfoUser> list = new ArrayList<>();
						if (list != null) {
							list = com.alibaba.fastjson.JSONObject.parseArray(result, DownInfoUser.class);
						}
						//下一步根据list来存数据库
						for (int c = 0; c < list.size(); c++) {
							try {
								//新增记录
								UserTable table = new UserTable();
								table.setType(list.get(c).getUser_type());
								table.setUserName(list.get(c).getLogin());
								table.setPassword(list.get(c).getPassword());
								db.save(table);
							} catch (DbException e) {
								e.printStackTrace();
							}
						}
						showlog("下传临时车收费明细："+list.size()+"条");
					} else {
						showlog("下传临时车收费明细无更新");
					}
				}
				ifinteriorok = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 下传操作员表
	 *
	 * @param time          上次记录时间
	 * @param controller_sn 设备号
	 * @param baseurl       base路径
	 */
	private void get_userList(String baseurl, String time, String controller_sn) {
		ifinteriorok = false;
		RequestParams params = new RequestParams(baseurl + "/down_front_user");
		params.addBodyParameter("controller_sn", controller_sn);
		params.addBodyParameter("max_updated_at", time);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}
			@Override
			public void onError(Throwable arg0, boolean arg1) {
				ifinteriorok = true;
			}

			@Override
			public void onFinished() {
			}
			@Override
			public void onSuccess(String arg0) {
				new processDownUserDatas(arg0).execute();
			}
		});
	}
	class processDownStallDatas extends AsyncTask<Void,Void,Integer>{
		private String arg0;
		public processDownStallDatas(String arg0){
			this.arg0 = arg0;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				JSONObject object = new JSONObject(arg0);
				String ref = object.getString("ref");
				if ("0".equals(ref)) {
					String result = object.getString("result");
					if (result.length() != 2) {
						//开始进行解析
						List<DownInfoStallBean> list = new ArrayList<DownInfoStallBean>();
						list = com.alibaba.fastjson.JSONObject.parseArray(result, DownInfoStallBean.class);
						//下一步根据list来存数据库
						for (int c = 0; c < list.size(); c++) {
							try {
								if("正常".equals(list.get(c).getStatus())){
									CarWeiTable mytable=db.selector(CarWeiTable.class).where("stall_code", "=", list.get(c).getStall_code()).findFirst();
									if(mytable!=null){
										//修改
										db.update(CarWeiTable.class, WhereBuilder.b("stall_code", "=", list.get(c).getStall_code()),
												new KeyValue("stall_code",list.get(c).getStall_code()),
												new KeyValue("area_code", list.get(c).getArea_code()),
												new KeyValue("print_code", list.get(c).getPrint_code()),
												new KeyValue("created_at", DownUtils.getstringtodate(list.get(c).getCreated_at())),
												new KeyValue("updated_at", DownUtils.getstringtodate(list.get(c).getUpdated_at()))
										);
									}else {
										//新增
										CarWeiTable table = new CarWeiTable();
										table.setStall_code(list.get(c).getStall_code());
										table.setArea_code(list.get(c).getArea_code());
										table.setPrint_code(list.get(c).getPrint_code());
										table.setCreated_at(DownUtils.getstringtodate(list.get(c).getCreated_at()));
										table.setUpdated_at(DownUtils.getstringtodate(list.get(c).getUpdated_at()));
										try {
											db.save(table);
										} catch (DbException e) {
											e.printStackTrace();
										}
									}
								}else {
									//删除数据
									db.delete(CarWeiTable.class, WhereBuilder.b("stall_code", "=", list.get(c).getStall_code()));
								}
							} catch (DbException e1) {
								e1.printStackTrace();
							}
						}
						//得到最后一条的更新时间，进行修改数据库
						String updatetiem = list.get(list.size() - 1).getUpdated_at();
						try {
							db.update(DownloadTimeBean.class, WhereBuilder.b("id", "=", 1), new KeyValue("handler_down_info_stall_time", updatetiem));
						} catch (DbException e) {
							e.printStackTrace();
						}
						showlog("下传车位表："+list.size()+"条");
					} else {
						showlog("下传车位表无更新");
					}
				}
				ifinteriorok = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 下传车位表
	 *
	 * @param time          上次记录时间
	 * @param controller_sn 设备号
	 * @param baseurl       base路径
	 */
	private void get_down_info_stall(String baseurl, String time, String controller_sn) {
		ifinteriorok = false;
		RequestParams params = new RequestParams(baseurl + "/down_info_stall");
		params.addBodyParameter("controller_sn", controller_sn);
		params.addBodyParameter("max_updated_at", time);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				ifinteriorok = true;
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(String arg0) {
				new processDownStallDatas(arg0).execute();
			}
		});
	}
	//处理下载车辆信息表
	class processDownCarInfoDatas extends AsyncTask<Void,Void,Integer>{
		private String arg0;
		public processDownCarInfoDatas(String arg0){
			this.arg0 = arg0;
		}
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				JSONObject object = new JSONObject(arg0);
				String ref = object.getString("ref");
				if ("0".equals(ref)) {
					String result = object.getString("result");
					if (result.length() != 2) {
						//开始进行解析
						List<DownInfoVehicleBean> list = new ArrayList<DownInfoVehicleBean>();
						list = com.alibaba.fastjson.JSONObject.parseArray(result, DownInfoVehicleBean.class);
						//下一步根据list来存数据库
						for (int c = 0; c < list.size(); c++) {
							//对固定车信息表进行新增，删除，修改
							try {
								if("正常".equals(list.get(c).getStatus())){
									//查询这个车是否存在，根据主键id查询
									CarInfoTable mytable=db.selector(CarInfoTable.class).where("codeId", "=", list.get(c).getId()).findFirst();
									if(mytable!=null){
										//修改
										db.update(CarInfoTable.class, WhereBuilder.b("codeId", "=", list.get(c).getId()),
												new KeyValue("codeId",list.get(c).getId()),
												new KeyValue("car_no", list.get(c).getCar_no()),
												new KeyValue("car_type", list.get(c).getCar_type()),
												new KeyValue("vehicle_type", list.get(c).getVehicle_type()),
												new KeyValue("fee_type", list.get(c).getFee_type()),
												new KeyValue("person_name", list.get(c).getPerson_name()),
												new KeyValue("person_tel", list.get(c).getPerson_tel()),
												new KeyValue("person_address", list.get(c).getPerson_address()),
												new KeyValue("start_date", DownUtils.getstringtoday(list.get(c).getStart_date())),
												new KeyValue("stop_date", DownUtils.getstringtoday(list.get(c).getStop_date())),
												new KeyValue("allow_count",list.get(c).getAllow_count()),
												new KeyValue("allow_park_time",list.get(c).getAllow_park_time()),
												new KeyValue("created_at", DownUtils.getstringtodate(list.get(c).getCreated_at())),
												new KeyValue("updated_at", list.get(c).getUpdated_at()),
												new KeyValue("status", list.get(c).getStatus())
										);
									}else {
										//新增
										CarInfoTable table = new CarInfoTable();
										table.setCodeId(list.get(c).getId());
										table.setCar_no(list.get(c).getCar_no());
										table.setCar_type(list.get(c).getCar_type());
										table.setVehicle_type(list.get(c).getVehicle_type());
										table.setFee_type(list.get(c).getFee_type());
										table.setPerson_name(list.get(c).getPerson_name());
										table.setPerson_tel(list.get(c).getPerson_tel());
										table.setPerson_address(list.get(c).getPerson_address());
										table.setStart_date(DownUtils.getstringtoday(list.get(c).getStart_date()));
										table.setStop_date(DownUtils.getstringtoday(list.get(c).getStop_date()));
										table.setAllow_count(list.get(c).getAllow_count());
										table.setAllow_park_time(list.get(c).getAllow_park_time());
										table.setCreated_at(DownUtils.getstringtodate(list.get(c).getCreated_at()));
										table.setUpdated_at(list.get(c).getUpdated_at());
										table.setStatus(list.get(c).getStatus());
										try {
											db.save(table);
										} catch (DbException e) {
											e.printStackTrace();
										}
									}
								}else {
									//删除这条数据
									db.delete(CarInfoTable.class, WhereBuilder.b("codeId", "=", list.get(c).getId()));
								}
							} catch (DbException e1) {
								e1.printStackTrace();
							}
						}
						//得到最后一条的更新时间，进行修改数据库
						String updatetiem = list.get(list.size() - 1).getUpdated_at();
						try {
							db.update(DownloadTimeBean.class, WhereBuilder.b("id", "=", 1), new KeyValue("handler_down_info_vehicle_time", updatetiem));
						} catch (DbException e) {
							e.printStackTrace();
						}
						showlog("下传固定车信息："+list.size()+"条");
					} else {
						showlog("下传固定车信息表无更新");
					}
				}
				ifinteriorok = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}


	}
	/**
	 * 下传固定车信息表
	 *
	 * @param time          上次记录时间
	 * @param controller_sn 设备号
	 * @param baseurl       base路径
	 */
	private void get_down_info_vehicle(String baseurl, String time, String controller_sn) {
		ifinteriorok = false;
		RequestParams params = new RequestParams(baseurl + "/down_info_vehicle");
		params.addBodyParameter("controller_sn", controller_sn);
		params.addBodyParameter("max_updated_at", time);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				ifinteriorok = true;
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(String arg0) {
				new processDownCarInfoDatas(arg0).execute();
			}
		});
	}
	//处理车位绑定下载数据
	class processDownStallVehicle extends AsyncTask<Void,Void,Integer>{
		private String arg0;
		public processDownStallVehicle(String arg0){
			this.arg0 = arg0;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				JSONObject object = new JSONObject(arg0);
				String ref = object.getString("ref");
				if ("0".equals(ref)) {
					String result = object.getString("result");
					if (result.length() != 2) {
						//开始进行解析
						List<DownRecordStallVehicleBean> list = new ArrayList<DownRecordStallVehicleBean>();
						list = com.alibaba.fastjson.JSONObject.parseArray(result, DownRecordStallVehicleBean.class);
						//下一步根据list来存数据库
						for (int c = 0; c < list.size(); c++) {
							try {
								if("Y".equals(list.get(c).getStatus())){
									//查询这个车是否存在，根据主键id查询
									CarWeiBindTable mytable=db.selector(CarWeiBindTable.class).where("code", "=", list.get(c).getCode()).findFirst();
									if(mytable!=null){
										//修改
										db.update(CarWeiBindTable.class, WhereBuilder.b("code", "=", list.get(c).getCode()),
												new KeyValue("code",list.get(c).getCode()),
												new KeyValue("car_no", list.get(c).getCar_no()),
												new KeyValue("stall_code", list.get(c).getStall_code()),
												new KeyValue("begin_date", DownUtils.getstringtoday(list.get(c).getBegin_date())),
												new KeyValue("end_date", DownUtils.getstringtoday(list.get(c).getEnd_date())),
												new KeyValue("created_at", DownUtils.getstringtodate(list.get(c).getCreated_at())),
												new KeyValue("updated_at",DownUtils.getstringtodate(list.get(c).getUpdated_at())),
												new KeyValue("status", DownUtils.getstringtoday(list.get(c).getStatus()))
										);

									}else {
										//新增
										CarWeiBindTable table = new CarWeiBindTable();
										table.setCode(list.get(c).getCode());
										table.setCar_no(list.get(c).getCar_no());
										table.setStall_code(list.get(c).getStall_code());
										table.setBegin_date(DownUtils.getstringtoday(list.get(c).getBegin_date()));
										table.setEnd_date(DownUtils.getstringtoday(list.get(c).getEnd_date()));
										table.setCreated_at(DownUtils.getstringtodate(list.get(c).getCreated_at()));
										table.setUpdated_at(DownUtils.getstringtodate(list.get(c).getUpdated_at()));
										table.setStatus(list.get(c).getStatus());
										try {
											db.save(table);
										} catch (DbException e) {
											e.printStackTrace();
										}
									}
								}else {
									//删除
									db.delete(CarWeiBindTable.class, WhereBuilder.b("code", "=", list.get(c).getCode()));
								}
							} catch (DbException e1) {
								e1.printStackTrace();
							}
						}
						//得到最后一条的更新时间，进行修改数据库
						String updatetiem = list.get(list.size() - 1).getUpdated_at();
						try {
							db.update(DownloadTimeBean.class, WhereBuilder.b("id", "=", 1), new KeyValue("handler_down_record_stall_vehicle_time", updatetiem));
						} catch (DbException e) {
							e.printStackTrace();
						}
						showlog("下传车位和车辆绑定表："+list.size()+"条");
					} else {
						showlog("下传车位和车辆绑定表无更新");
					}
				}
				ifinteriorok = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 下传车位和车辆绑定表
	 *
	 * @param time          上次记录时间
	 * @param controller_sn 设备号
	 * @param baseurl       base路径
	 */
	private void get_down_record_stall_vehicle(String baseurl, String time, String controller_sn) {
		ifinteriorok = false;
		RequestParams params = new RequestParams(baseurl + "/down_record_stall_vehicle");
		params.addBodyParameter("controller_sn", controller_sn);
		params.addBodyParameter("max_updated_at", time);
		x.http().get(params, new CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				ifinteriorok = true;
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(String arg0) {
				new processDownStallVehicle(arg0).execute();
			}
		});
	}

	class execFileWrite extends AsyncTask<Void,Void,String>{
		String msg;
		private execFileWrite(String msg){
			this.msg = msg;
		}
		@Override
		protected String doInBackground(Void... params) {
			fileUtils.witefile(msg, DateUtils.date2String(new Date()));
			return null;
		}
	}
	/**
	 * 打印log
	 *
	 * @param msg
	 */
	public void showlog(String msg) {
		if (log) {
			Log.i("chenghao", msg);
			new execFileWrite(msg).execute();
		}
	}
}
