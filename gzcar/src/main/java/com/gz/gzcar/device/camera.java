package com.gz.gzcar.device;

/**
 * Created by Administrator on 2016/9/18.



 */
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.gz.gzcar.MainActivity;
import com.gz.gzcar.MyApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import ice_ipcsdk.SDK;

public class camera {
    SDK sdk;
    public MainActivity mainActivity = null;
    plate_callback PlateCallback;
    mjpeg_callback MjpegCallback;
    String portName;
    String cameraIp;
    PlateInfo streamInfo = new PlateInfo();
    PlateInfo plateinfo = new PlateInfo();
    //定义语音
    public static final HashMap<String,String> AudioList=  new HashMap<String,String>(){ {
        put("0","0");
        put("1","1");
        put("2","2");
        put("3","3");
        put("4","4");
        put("5","5");
        put("6","6");
        put("7","7");
        put("8","8");
        put("9","9");
        put("十","10");
        put("百","11");
        put("千","12");
        put("年","13");
        put("月","14");
        put("日","15");
        put("小时","16");
        put("分钟","17");
        put("秒","18");
        put("元","19");
        put("有卡请刷卡,无卡请取卡","20");
        put("请刷卡","21");
        put("请刷卡,临时卡请交卡","22");
        put("有效期到","23");
        put("您的卡快到期,请及时充值","24");
        put("您的卡已过期,请充值","25");
        put("您的卡被当作临时卡使用","26");
        put("卡未开通","27");
        put("重新读卡","28");
        put("无效卡","29");
        put("此卡已过期","31");
        put("已进场","32");
        put("已出场","33");
        put("停车","34");
        put("消费","35");
        put("call in","36");
        put("call out","37");
        put("对方忙","38");
        put("对方无应答","39");
        put("系统时间错误","40");
        put("未出子区","41");
        put("未进主区","42");
        put("未出主区","43");
        put("网络通讯失败","44");
        put("欢迎光临","45");
        put("一路顺风","46");
        put("余额不足，卡上余额只有","47");
        put("本机禁止读卡","48");
        put("本机处于禁止状态","49");
        put("请插卡","50");
        put("补交收费","51");
        put("带卡进场","52");
        put("带卡出场","53");
        put("卡上余额","54");
        put("收费","55");
        put("读卡成功","56");
        put("摩托车","57");
        put("小型车","58");
        put("中型车","59");
        put("大型车","60");
        put("超大车","61");
        put("预留车","62");
        put("无授权","63");
        put("值班员请确认放行","64");
        put("操作卡","65");
        put("值班员请选择车型","66");
        put("天","67");
        put("临时卡请交卡","68");
        put("请刷卡入场","69");
        put("未注册用户卡","70");
        put("当前时间段未授权","71");
        put("请取票入场","72");
        put("车位已满","73");
        put("非工作时间段","74");
        put("小型车，请交费","75");
        put("A","76");
        put("B","77");
        put("C","78");
        put("D","79");
        put("E","80");
        put("F","81");
        put("G","82");
        put("H","83");
        put("J","84");
        put("K","85");
        put("L","86");
        put("M","87");
        put("N","88");
        put("O","89");
        put("P","90");
        put("Q","91");
        put("R","92");
        put("S","93");
        put("T","94");
        put("U","95");
        put("V","96");
        put("W","97");
        put("X","98");
        put("Y","99");
        put("Z","100");
        put("澳","101");
        put("川","102");
        put("鄂","103");
        put("甘","104");
        put("赣","105");
        put("港","106");
        put("挂","107");
        put("贵","108");
        put("黑","109");
        put("沪","110");
        put("吉","111");
        put("冀","112");
        put("津","113");
        put("晋","114");
        put("京","115");
        put("警","116");
        put("辽","117");
        put("领","118");
        put("鲁","119");
        put("蒙","120");
        put("闽","121");
        put("宁","122");
        put("青","123");
        put("琼","124");
        put("陕","125");
        put("使","126");
        put("苏","127");
        put("皖","128");
        put("湘","129");
        put("新","130");
        put("学","131");
        put("渝","132");
        put("豫","133");
        put("粤","134");
        put("云","135");
        put("藏","136");
        put("浙","137");
        put("月租车","138");
        put("免费车","139");
        put("临时车","140");
        put("储值车","141");
        put("角","142");
        put("请缴费","143");
        put("请尽快延期","144");
        put("此卡可用日期","145");
    }};
    public enum msgType{PLATE,PIC,STREAM};

    //手动出场
    public void manualPassOutFunc(){
        Log.i("button:", "manualPassOutFunc");
    }
    public class PlateInfo{
        public msgType msgType;
        String name;
        String ip;
        String PlateNumber;
        String PlateColor;
        byte[] CarPicdata;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPlateNumber() {
            return PlateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            PlateNumber = plateNumber;
        }

        public String getPlateColor() {
            return PlateColor;
        }

        public void setPlateColor(String plateColor) {
            PlateColor = plateColor;
        }

        public byte[] getCarPicdata() {
            return CarPicdata;
        }

        public void setCarPicdata(byte[] carPicdata) {
            CarPicdata = carPicdata;
        }
    }
    public  camera(MainActivity myActive,String name, String ip){
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        Log.i("log","connect camera " + name +  " " + ip);
        mainActivity = myActive;
        sdk = new SDK();
        portName = name;
        cameraIp = ip;
        //设置视频流缓存
        streamInfo.setName(portName);
        streamInfo.msgType = msgType.STREAM;
        PlateCallback = new plate_callback();
        MjpegCallback = new mjpeg_callback();
        sdk.ICE_IPCSDK_Open(cameraIp, null);// 1.连接相机
        sdk.ICE_ICPSDK_SetPlateCallback(PlateCallback);// 35.设置断网续传事件
        sdk.ICE_IPCSDK_SetMJpegallback_Static(MjpegCallback);// 37.设置mjpeg码流回调
        //同步显示屏时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] ledClockBuffer = LedModule.FormatClock();
        sdk.ICE_IPCSDK_TransSerialPort(ledClockBuffer);
    }
    //重新识别
    public void againIdent(){
       // PlateInfo info = new PlateInfo();
        sdk.ICE_IPCSDK_Trigger();
        /*Log.i("log","againIdent");
       SDK.TriggerResult result =  sdk.ICE_IPCSDK_Trigger();
       if(result != null){
            try {
                Log.i("log","againIdent:" + result.number);
                //info.setPlateNumber(new String(result.number.getBytes("GBK"), "UTF-8"));
                //info.setPlateColor(new String(result.color.getBytes("GBK"), "UTF-8"));
                info.setPlateNumber(result.number);
                info.setPlateColor(result.color);
                info.CarPicdata = result.picdata;
                info.msgType = msgType.PLATE;
                EventBus.getDefault().post(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
    //手动获取图片
    public byte[] CapturePic(){
        SDK.CaptureResult result = sdk.ICE_IPCSDK_Capture();
        PlateInfo info = new PlateInfo();
        info.setName(portName);
        if(result != null){
            info.setPlateNumber("待通行");
            info.setPlateColor("蓝色");
            info.CarPicdata = result.picdata;
            info.msgType = msgType.PIC;
            Message msg = new Message();
            msg.obj = info;
            mainActivity.myHandler.sendMessage(msg);
          //  MainActivity.myHandler.sendMessage(msg);
            return info.CarPicdata;
        }
        return null;
    }
    //起杆
    public void openGate(){
        sdk.ICE_IPCSDK_OpenGate();
        if (portName.equals("in")) {
            MyApplication.settingInfo.putLong("inCarCount", MyApplication.settingInfo.getLong("inCarCount") + 1);
        }else{
            MyApplication.settingInfo.putLong("outCarCount", MyApplication.settingInfo.getLong("outCarCount") + 1);
        }
    }
    //报放语音
    public void playAudio(String audioStr){
        Log.i("log", "audio:" +audioStr);
        sdk.ICE_IPCSDK_BroadcastGroup(audioStr);
    }
    //透传显示屏接口
    public void ledDisplay(String line1,String line2,String line3,String line4) {
        if (!line1.isEmpty()){
            byte[] buffer = LedModule.formatData((char) 1, line1, "BX_5K1");
            sdk.ICE_IPCSDK_TransSerialPort(buffer);
            Log.i("log", "ledDisplay:" + line1);
        }
        if (!line2.isEmpty()){
            byte[] buffer = LedModule.formatData((char) 2, line2, "BX_5K1");
            sdk.ICE_IPCSDK_TransSerialPort(buffer);
            Log.i("log", "ledDisplay:" + line2);
        }
        if (!line3.isEmpty()){
            byte[] buffer = LedModule.formatData((char) 3, line3, "BX_5K1");
            sdk.ICE_IPCSDK_TransSerialPort(buffer);
            Log.i("log", "ledDisplay:" + line3);
        }
        if (!line4.isEmpty()){
            byte[] buffer = LedModule.formatData((char) 4, line4, "BX_5K1");
            sdk.ICE_IPCSDK_TransSerialPort(buffer);
            Log.i("log", "ledDisplay:" + line4);
        }
    }
    // 车牌识别事件(车牌号和颜色为utf-8编码)
    class plate_callback implements SDK.IPlateCallback_Bytes {
        public void ICE_IPCSDK_Plate(String strIP, byte[] strNumber, byte[] strColor,
                                     byte[] bPicData, int nOffset, int nLen, int nOffsetCloseUp, int nLenCloseUp,
                                     int nPlatePosLeft, int nPlatePosTop, int nPlatePosRight, int nPlatePosBottom,
                                     float fPlateConfidence, int nVehicleColor, int nPlateType, int nVehicleDir,
                                     int nAlarmType, int nReserved1, int nReserved2, int nReserved3, int nReserved4) {
            plateinfo.setName(portName);
           try {
               plateinfo.setPlateNumber(new String(strNumber, "GBK"));
               plateinfo.setPlateColor(new String(strColor, "GBK"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            plateinfo.CarPicdata = new byte[nLen];
            System.arraycopy(bPicData, nOffset,plateinfo.CarPicdata, 0, nLen);
            plateinfo.msgType = msgType.PLATE;
            Message msg = new Message();
            msg.obj = plateinfo;
           // MainActivity.myHandler.sendMessage(msg);
            mainActivity.myHandler.sendMessage(msg);
        }
    }
    // mjpeg码流回调
    class mjpeg_callback implements SDK.IMJpegCallback_Static {
        public void ICE_IPCSDK_MJpeg(String strIP, byte[] bData, int length) {// 参数：1.相机ip  2.mjpeg数据 3.jpg图片长度
            streamInfo.setCarPicdata(new byte[length]);
            System.arraycopy(bData, 0,streamInfo.CarPicdata, 0, length);
            EventBus.getDefault().post(streamInfo);
        }
    }
}
