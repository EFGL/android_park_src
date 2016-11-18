package com.gz.gzcar.server;

/**
 * 下传临时车收费明细返回的javabean
 */
public class DownTemFeeBean {
	/**
	 *     "fee_detail_code": "W0401040001600000000",
            "fee_code": "1",
            "fee_name": "临时车收费",
            "car_type_code": "9",
            "car_type_name": "公车固定车",
            "parked_min_time": 1,
            "parked_max_time": 30,
            "money": "1.0",
            "created_at": "2016-08-29 16:24:16",
            "updated_at": "2016-08-29 16:24:16"
	 */

	private String fee_detail_code;
	private String fee_code;
	private String fee_name;
	private String car_type_code;
	private String car_type_name;
	private int parked_min_time;
	private int parked_max_time;
	private String money;
	private String created_at;
	private String updated_at;
	public String getFee_detail_code() {
		return fee_detail_code;
	}
	public void setFee_detail_code(String fee_detail_code) {
		this.fee_detail_code = fee_detail_code;
	}
	public String getFee_code() {
		return fee_code;
	}
	public void setFee_code(String fee_code) {
		this.fee_code = fee_code;
	}
	public String getFee_name() {
		return fee_name;
	}
	public void setFee_name(String fee_name) {
		this.fee_name = fee_name;
	}
	public String getCar_type_code() {
		return car_type_code;
	}
	public void setCar_type_code(String car_type_code) {
		this.car_type_code = car_type_code;
	}
	public String getCar_type_name() {
		return car_type_name;
	}
	public void setCar_type_name(String car_type_name) {
		this.car_type_name = car_type_name;
	}
	public int getParked_min_time() {
		return parked_min_time;
	}
	public void setParked_min_time(int parked_min_time) {
		this.parked_min_time = parked_min_time;
	}
	public int getParked_max_time() {
		return parked_max_time;
	}
	public void setParked_max_time(int parked_max_time) {
		this.parked_max_time = parked_max_time;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
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
