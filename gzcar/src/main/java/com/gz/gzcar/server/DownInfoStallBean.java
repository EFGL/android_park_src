package com.gz.gzcar.server;

/**
 * 下传车位表返回的bean
 */
public class DownInfoStallBean {
	/**
	 *   "stall_code": "string",                        //主键
	      "area_code": "string",                         //区域表主键
	      "print_code": "string",                        //车位编号
	      "created_at": "2016-08-29 16:29:22",           //创建时间
	      "updated_at": "2016-08-29 16:29:22"            //时间戳
	      status
	 */

	private String stall_code;
	private String area_code;
	private String print_code;
	private String created_at;
	private String updated_at;
	private String status;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStall_code() {
		return stall_code;
	}
	public void setStall_code(String stall_code) {
		this.stall_code = stall_code;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getPrint_code() {
		return print_code;
	}
	public void setPrint_code(String print_code) {
		this.print_code = print_code;
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
