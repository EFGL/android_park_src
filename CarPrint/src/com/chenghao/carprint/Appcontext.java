package com.chenghao.carprint;

import com.gprinter.aidl.GpService;

import android.app.Application;

public class Appcontext extends Application{
	public static GpService mGpService;
	@Override
	public void onCreate() {
		super.onCreate();
	}
}
