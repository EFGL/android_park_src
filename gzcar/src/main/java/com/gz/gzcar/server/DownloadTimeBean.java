package com.gz.gzcar.server;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


/**
 * 下行时间表，
 *@ClassName: DownloadTimeBean 
 */
@Table(name = "downloadtimebean")
public class DownloadTimeBean {

	/**
	 * 自增长id
	 */
	@Column(name = "id", isId = true)
	private int id;

	/**
	 * 不是自增长id,始终为0,拿这个作为查询条件
	 *
	 */
	@Column(name = "myid")
	private int myid;

	/**
	 * 下载通行记录的时间
	 */
	@Column(name="handler_in_out_record_download_time")
	private String handler_in_out_record_download_time;
	/**
	 * 下载临时车收费
	 */
	@Column(name="handler_down_tempfee_time")
	private String handler_down_tempfee_time;
	/**
	 * 下载车位表
	 */
	@Column(name="handler_down_info_stall_time")
	private String handler_down_info_stall_time;
	/**
	 * 下载固定车信息表
	 */
	@Column(name="handler_down_info_vehicle_time")
	private String handler_down_info_vehicle_time;
	/**
	 * 下载车辆绑定表
	 */
	@Column(name="handler_down_record_stall_vehicle_time")
	private String handler_down_record_stall_vehicle_time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHandler_in_out_record_download_time() {
		return handler_in_out_record_download_time;
	}
	public void setHandler_in_out_record_download_time(
			String handler_in_out_record_download_time) {
		this.handler_in_out_record_download_time = handler_in_out_record_download_time;
	}
	public String getHandler_down_tempfee_time() {
		return handler_down_tempfee_time;
	}
	public void setHandler_down_tempfee_time(String handler_down_tempfee_time) {
		this.handler_down_tempfee_time = handler_down_tempfee_time;
	}
	public String getHandler_down_info_stall_time() {
		return handler_down_info_stall_time;
	}
	public void setHandler_down_info_stall_time(String handler_down_info_stall_time) {
		this.handler_down_info_stall_time = handler_down_info_stall_time;
	}
	public String getHandler_down_info_vehicle_time() {
		return handler_down_info_vehicle_time;
	}
	public void setHandler_down_info_vehicle_time(
			String handler_down_info_vehicle_time) {
		this.handler_down_info_vehicle_time = handler_down_info_vehicle_time;
	}
	public String getHandler_down_record_stall_vehicle_time() {
		return handler_down_record_stall_vehicle_time;
	}
	public void setHandler_down_record_stall_vehicle_time(
			String handler_down_record_stall_vehicle_time) {
		this.handler_down_record_stall_vehicle_time = handler_down_record_stall_vehicle_time;
	}
	public int getMyid() {
		return myid;
	}

	public void setMyid(int myid) {
		this.myid = myid;
	}
}
