package com.gz.gzcar.module;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.device.camera;

import org.xutils.ex.DbException;

/**
 * Created by Administrator on 2016/11/3 0003.
 */

public class delayTask extends Service {
    int emptyValue = 0;
    int maxCount = 10;
    int countIn =0;
    int countOut = 0;
    boolean runFlagIn = false;
    boolean runFlagOut = false;
    camera cameraIn;
    camera cameraOut;
    String inDispStr1,inDispStr2,inDispStr3,inDispStr4;
    String outDispStr1,outDispStr2,outDispStr3,outDispStr4;
    @Nullable
    @Override
     public IBinder onBind(Intent intent) {
        return new ServicesBinder();
    }
    public class ServicesBinder extends Binder {

        public delayTask getService() {
            return delayTask.this;
        }
    }
    //初始化像机及延时
    public void initCamera(camera inCamera,camera outCamera,int delay){
        cameraIn = inCamera;
        cameraOut = outCamera;
        maxCount = delay;
    }
    public void display(String direction, String str1,String str2,String str3,String str4,int delay){
         {
            if(direction.equals("in")) {
                if(delay >0) {
                    inDispStr1 = str1;
                    inDispStr2 = str2;
                    inDispStr3 = str3;
                    inDispStr4 = str4;
                    countIn = 0;
                    runFlagIn = true;
                }else{
                    cameraIn.ledDisplay(str1,str2,str3,str4);
                    cameraIn.ledDisplay(1,str1 + "车牌识别 一车一杆 减速慢行");
                }
            }
            else{
                if(delay >0) {
                    outDispStr1 = str1;
                    outDispStr2 = str2;
                    outDispStr3 = str3;
                    outDispStr4 = str4;
                    countOut = 0;
                    runFlagOut = true;
                }else{
                    cameraOut.ledDisplay(str1,str2,str3,str4);
                    cameraOut.ledDisplay(1,str1+ "车牌识别 一车一杆 减速慢行");
                }
            }
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //延时
                    try {
                        Thread.sleep(1000);
                        if(runFlagIn){ countIn++;}
                        if(runFlagOut){ countOut++;}
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (countIn > maxCount) {
                        countIn = 0;
                        runFlagIn = false;
                        cameraIn.ledDisplay(inDispStr1,inDispStr2,inDispStr3,inDispStr4);
                    }
                    if (countOut > maxCount) {
                        countOut = 0;
                        runFlagOut = false;
                        cameraOut.ledDisplay(outDispStr1,outDispStr2,outDispStr3,outDispStr4);
                    }
                }
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }
}
