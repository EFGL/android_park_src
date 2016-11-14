package com.gz.gzcar.server;

/**
 * 下传通行记录bean
 */
public class DownloadinandoutRecordBean {

	/**
	 * "pass_no": "string",
		      "card_type": "string",
		      "car_no": "string",
		      "in_time": "2016-07-18 09:10:43",
		      "in_operator": "string",
		      "in_image": "string",
		      "stall_code": "string",
		      "out_time": "2016-07-18 09:49:04",
		      "out_operator": "string",
		      "out_image": "string",
		      "status": "string",
		      "fee": "string",
		      "fact_fee": "string",
		      "created_at": "2016-07-18 09:48:43",
		      "updated_at": "2016-10-17 10:50:16",
		      "parked_time": 0

	 */
	private String pass_no;
	private String card_type;
	private String car_no;
	private String in_time;
	private String in_operator;
	private String in_image;
	private String stall_code;
	private String out_time;
	private String out_operator;
	private String out_image;
	private String status;
	private String fee;
	private String fact_fee;
	private String created_at;
	private String updated_controller_sn;
	private String updated_at;
	private long parked_time;
	public String getPass_no() {
		return pass_no;
	}
	public void setPass_no(String pass_no) {
		this.pass_no = pass_no;
	}
	public String getCard_type() {
		return card_type;
	}
	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public String getIn_time() {
		return in_time;
	}
	public void setIn_time(String in_time) {
		this.in_time = in_time;
	}
	public String getIn_operator() {
		return in_operator;
	}
	public void setIn_operator(String in_operator) {
		this.in_operator = in_operator;
	}
	public String getIn_image() {
		return in_image;
	}
	public void setIn_image(String in_image) {
		this.in_image = in_image;
	}
	public String getStall_code() {
		return stall_code;
	}
	public void setStall_code(String stall_code) {
		this.stall_code = stall_code;
	}
	public String getOut_time() {
		return out_time;
	}
	public void setOut_time(String out_time) {
		this.out_time = out_time;
	}
	public String getOut_operator() {
		return out_operator;
	}
	public void setOut_operator(String out_operator) {
		this.out_operator = out_operator;
	}
	public String getOut_image() {
		return out_image;
	}
	public void setOut_image(String out_image) {
		this.out_image = out_image;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getFact_fee() {
		return fact_fee;
	}
	public void setFact_fee(String fact_fee) {
		this.fact_fee = fact_fee;
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
	public long getParked_time() {	return parked_time;}
	public void setParked_time(long parked_time) {this.parked_time = parked_time;}

	public String getUpdated_controller_sn() {return updated_controller_sn;}
	public void setUpdated_controller_sn(String updated_controller_sn) {this.updated_controller_sn = updated_controller_sn;}
}
