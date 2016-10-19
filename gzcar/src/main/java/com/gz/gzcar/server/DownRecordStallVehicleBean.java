package com.gz.gzcar.server;
/**
 * 、下传车位和车辆绑定表返回的bean
 *
 */
public class DownRecordStallVehicleBean {

	/**
	 *   "code": "string",                                            //主键
		      "car_no": "string",																					 //车号
		      "stall_code": "string",                                      //车位表主键
		      "status": "string",                                          // Y : N 值
      		"begin_date": "2016-08-29",                                  //有效开始日期
      		"end_date": "2016-09-07",                                    //有效截止日期
		      "created_at": "2016-08-29 16:29:46",                         //记录创建时间
		      "updated_at": "2016-08-29 16:29:49"                          //时间戳
	 */
	
	private String code;
	private String car_no;
	private String stall_code;
	private String status;
	private String begin_date;
	private String end_date;
	private String created_at;
	private String updated_at;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public String getStall_code() {
		return stall_code;
	}
	public void setStall_code(String stall_code) {
		this.stall_code = stall_code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBegin_date() {
		return begin_date;
	}
	public void setBegin_date(String begin_date) {
		this.begin_date = begin_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	
}
