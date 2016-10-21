package com.gz.gzcar.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
/**
 * 获取手机的id
 * @author jindanke
 *
 */
public class GetImei {
	public static String getphoneimei(Context context){
		TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei=telephonyManager.getDeviceId();
		return imei;
	}
}
