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

	@Column(name="time")
	private String time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
