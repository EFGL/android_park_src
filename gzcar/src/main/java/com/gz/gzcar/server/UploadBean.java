package com.gz.gzcar.server;

/**
 * 上传记录的json格式
 *@ClassName: UploadBean 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author A18ccms a18ccms_gmail_com
 * @date 2016-10-18 上午11:25:23 
 * @author jindanke
 *
 */
public class UploadBean {

	/**
	 *  "pass_no": "P040103001600001112",   （数据主键，保证各设备不重复）
			    "card_type": "临时车",              （车辆类型：临时车 : 固定车）
			    "car_no": "晋AXB999",               （车号）
			    "in_time": "2016-07-18 09:38:05",   （入场时间）
			    "in_operator": "string",						（入场操作员）
			    "in_image": "123.jpg",              （入场图片名称）
			    "stall_code": "string",             （占用车位号，车位表主键）
			    "out_time": "2016-07-18 09:48:37",  （出场时间）
			    "out_operator": "string",           （出场操作员）
			    "out_image": "string",              （出场图片名称）
			    "status": "已出",                   （状态）
			    "fee": "0.0",                       （应缴费用）
			    "fact_fee": "0.0",                  （实缴费用）
			    "created_at": "2016-07-18 09:48:05",（记录创建时间）
			    "parked_time": 10                   （停车时长）
	 */


	private String  pass_no;
	private String  card_type;
	private String car_no ;
	private String in_time ;
	private String  in_operator;
	private String in_image ;
	private String  stall_code;
	private String out_time ;
	private String  out_operator;
	private String out_image ;
	private String status ;
	private String  fee;
	private String  fact_fee;
	private String  created_at;
	private String parked_time ;
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
	public String getParked_time() {
		return parked_time;
	}
	public void setParked_time(String parked_time) {
		this.parked_time = parked_time;
	}


}
