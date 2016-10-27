package com.gz.gzcar.server;
/**
 * 下传固定车信息表返回的bean
 */
public class DownInfoVehicleBean {
	/**
	 *  "id": "string",                                            //主键
		      "car_no": "string",                                        //车号
		      "car_type": "string",                                      //车型
		      "person_name": "string",                                   //车辆所有人姓名
		      "person_tel": "string",                                    //车辆所有人电话
		      "person_address": "string",                                //车辆所有人住址
		      "start_date": "2016-08-29",                                //有效开始日期
		      "stop_date": "2016-08-31",                                 //有效截止日期
		      "status": "string",                                        //状态  正常 : 作废
		      "created_at": "2016-08-29 16:28:06",                       //创建时间
		      "updated_at": "2016-08-29 16:30:21"                        //时间戳
	 */

	private String id;
	private String car_no;
	private String car_type;
	private String vehicle_type;
	private String	fee_type;

	private String person_name;
	private String person_tel;
	private String person_address;
	private String start_date;
	private String stop_date;
	private String status;
	private String created_at;
	private String updated_at;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public String getCar_type() {
		return car_type;
	}
	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}

	public String getVehicle_type() {
		return vehicle_type;
	}

	public void setVehicle_type(String vehicle_type) {
		this.vehicle_type = vehicle_type;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getPerson_name() {
		return person_name;
	}
	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}
	public String getPerson_tel() {
		return person_tel;
	}
	public void setPerson_tel(String person_tel) {
		this.person_tel = person_tel;
	}
	public String getPerson_address() {
		return person_address;
	}
	public void setPerson_address(String person_address) {
		this.person_address = person_address;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getStop_date() {
		return stop_date;
	}
	public void setStop_date(String stop_date) {
		this.stop_date = stop_date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
