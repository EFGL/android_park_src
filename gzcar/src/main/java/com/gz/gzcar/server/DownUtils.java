package com.gz.gzcar.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownUtils {

	/**
	 * 字符串转换成日期的 没有时间的
	 * @param time
	 * @return
	 */
	public static Date getstringtoday(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(time!=null){
			try {
				Date date=sdf.parse(time);
				return date;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			return null;
		}
		return null;
	}

	/**
	 * 字符串转date
	 * @param time
	 * @return
	 */
	public static Date getstringtodate(String time){
		//2016-07-18 09:48:37  yyyy-MM-dd HH:mm:ss
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(time!=null){
			try {
				Date date=sdf.parse(time);
				return date;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			return null;
		}
		return null;

	}


	/**
	 * string转double
	 * @param mydouble
	 * @return
	 */
	public static Double getstringtodouble(String mydouble){
		if(mydouble==null){
			return null;
		}else {
			Double dos=Double.parseDouble(mydouble);
			return dos;
		}
	}

}
