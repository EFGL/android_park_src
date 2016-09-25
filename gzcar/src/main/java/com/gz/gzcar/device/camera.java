package com.gz.gzcar.device;

/**
 * Created by Administrator on 2016/9/18.
 * @0 0.wav           "0"
 @1 1.wav           "1"
 @2 2.wav           "2"
 @3 3.wav           "3"
 @4 4.wav           "4"
 @5 5.wav           "5"
 @6 6.wav           "6"
 @7 7.wav           "7"
 @8 8.wav           "8"
 @9 9.wav           "9"
 @10 10.wav         "十"
 @11 100.wav        "百"
 @12 1000.wav       "千"
 @13 year.wav       "年"
 @14 month.wav      "月"
 @15 date.wav       "日"
 @16 hour.wav       "小时"
 @17 minute.wav     "分钟"
 @18 second.wav     "秒"
 @19 yuan.wav       "元"
 @20 welcome.wav    "有卡请刷卡, 无卡请取卡"
 @21 welinr.wav     "请刷卡"
 @22 thsout.wav     "请刷卡，临时卡请交卡"
 @23 readcdf.wav    "有效期到"
 @24 cardbf.wav     "您的卡快到期，请及时充值"
 @25 cardaf.wav     "您的卡已过期，请充值"
 @26 cardend.wav    "您的卡被当作临时卡使用"
 @27 cardear.wav    "卡未开通"
 @28 readagain.wav  "重新读卡"
 @29 cardnuse.wav   "无效卡"
 @30 cardloss.wav   "挂失卡"
 @31 cardedat.wav   "此卡已过期"
 @32 entered.wav    "已进场"
 @33 exited.wav     "已出场"
 @34 park.wav       "停车"
 @35 consume.wav    "消费"
 @36 callin.wav     "call in"
 @37 callout.wav    "call out"
 @38 callbusy.wav   "对方忙"
 @39 noanswer.wav   "对方无应答"
 @40 Errtime.wav    "系统时间错误"
 @41 exitsub.wav    "未出子区"
 @42 entersub.wav   "未进主区"
 @43 exitmain.wav   "未出主区"
 @44 netfail.wav    "网络通讯失败"
 @45 thsin.wav      "欢迎光临"
 @46 thsouted.wav   "一路顺风"
 @47 lessmey.wav    "余额不足，卡上余额只有"
 @48 nordcard.wav   "本机禁止读卡"
 @49 notwork.wav    "本机处于禁止状态"
 @50 insertcard.wav "请插卡"
 @51 chargeagain.wav "补交收费"
 @52 takein.wav     "带卡进场"
 @53 takeout.wav    "带卡出场"
 @54 lestmey.wav    "卡上余额"
 @55 charge.wav     "收费"
 @56 readOK.wav	   "读卡成功"
 @57 Motorcycle.wav "摩托车"
 @58 SmallCar.wav   "小型车"
 @59 MiddleCar.wav  "中型车"
 @60 BigCar.wav     "大型车"
 @61 SpuCar.wav     "超大车"
 @62 PreDefCar.wav  "预留车"
 @63 NoAuthEnter.wav "无授权"
 @64 WatchCard.wav  "值班员请确认放行"
 @65 OperateCard.wav  "操作卡"
 @66 ChsCarType.wav "值班员请选择车型"
 @67 tian.wav "天"
 @68 takecard.wav "临时卡请交卡"
 @69 69.wav "请刷卡入场"
 @70 70.wav "未注册用户卡"
 @71 71.wav "当前时间段未授权"
 @72 72.wav "请取票入场"
 @73 73.wav "车位已满"
 @74 74.wav "非工作时间段"
 @75 75.wav "小型车，请交费"
 @76 76.wav ""
 @77 77.wav ""
 @78 78.wav ""
 @79 79.wav ""
 @80 80.wav ""
 */
import android.os.Message;
import android.os.StrictMode;
import android.util.*;
import com.gz.gzcar.MainActivity;
import ice_ipcsdk.SDK;
public class camera {
    SDK sdk;
    plate_callback PlateCallback;
    mjpeg_callback MjpegCallback;
    PlateInfo info = new PlateInfo();

    //手动出场
    public void manualPassOutFunc(){
        Log.i("button:", "manualPassOutFunc");
    }

    public class PlateInfo{
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
    public  camera(String name, String ip){
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        sdk = new SDK();
        info.setName(name);
        info.setIp(ip);
        PlateCallback = new plate_callback();
        MjpegCallback = new mjpeg_callback();
        sdk.ICE_IPCSDK_Open(info.getIp(), null);// 1.连接相机
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
        SDK.TriggerResult result = sdk.ICE_IPCSDK_Trigger();
        if(result != null){
            try {
                info.setPlateNumber(new String(result.number.getBytes("GBK"), "UTF-8"));
                info.setPlateColor(new String(result.color.getBytes("GBK"), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            info.CarPicdata = result.picdata;
            Message msg = new Message();
            msg.obj = info;
            msg.what = 0;
            MainActivity.myHandler.sendMessage(msg);
        }
    }
    //手动获取图片
    public void CapturePic(){
        SDK.CaptureResult result = sdk.ICE_IPCSDK_Capture();
        if(result != null){
            info.setPlateNumber("手动起杆");
            info.setPlateColor("蓝色");
            info.CarPicdata = result.picdata;
            Message msg = new Message();
            msg.obj = info;
            msg.what = 0;
            MainActivity.myHandler.sendMessage(msg);
        }

    }
    //起杆
    public void openGate(){
        sdk.ICE_IPCSDK_OpenGate();
    }
    //报放语音
    public void playAudio(String audioStr){
        sdk.ICE_IPCSDK_BroadcastGroup(audioStr);
    }
    //透传显示屏接口
    public void ledDisplay(String[] info){
        for(char i=1;i<=info.length;i++){
            if(!info[i-1].isEmpty()) {
                byte[] buffer = LedModule.formatData(i, info[i - 1], "BX_5K1");
                sdk.ICE_IPCSDK_TransSerialPort(buffer);
            }
        }
    }
    // 车牌识别事件(车牌号和颜色为utf-8编码)
    class plate_callback implements SDK.IPlateCallback_Bytes {
        public void ICE_IPCSDK_Plate(String strIP, byte[] strNumber, byte[] strColor,
                                     byte[] bPicData, int nOffset, int nLen, int nOffsetCloseUp, int nLenCloseUp,
                                     int nPlatePosLeft, int nPlatePosTop, int nPlatePosRight, int nPlatePosBottom,
                                     float fPlateConfidence, int nVehicleColor, int nPlateType, int nVehicleDir,
                                     int nAlarmType, int nReserved1, int nReserved2, int nReserved3, int nReserved4) {

            try {
                info.setPlateNumber(new String(strNumber, "GBK"));
                info.setPlateColor(new String(strColor, "GBK"));
            } catch (Exception e) {
                e.printStackTrace();
            }
           info.CarPicdata = new byte[nLen];
            System.arraycopy(bPicData, nOffset,info.CarPicdata, 0, nLen);
            Message msg = new Message();
            msg.obj = info;
            msg.what = 2;
            MainActivity.myHandler.sendMessage(msg);
        }
    }
    // mjpeg码流回调
    class mjpeg_callback implements SDK.IMJpegCallback_Static {
        public void ICE_IPCSDK_MJpeg(String strIP, byte[] bData, int length) {// 参数：1.相机ip  2.mjpeg数据 3.jpg图片长度
            info.setCarPicdata(new byte[length]);
            System.arraycopy(bData, 0,info.CarPicdata, 0, length);
            Message msg = new Message();
            msg.obj = info;
            msg.what = 1;
            MainActivity.myHandler.sendMessage(msg);
        }
    }
}
