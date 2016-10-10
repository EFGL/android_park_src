package com.gz.gzcar.module;

import android.net.rtp.AudioStream;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.FreeInfoTable;
import com.gz.gzcar.Database.MoneyTable;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.MainActivity;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.device.camera;
import org.xutils.*;
import org.xutils.ex.DbException;
import java.util.*;

/**
 * Created by Administrator on 2016/9/30.
 */
public class carInfoProcess {
    public static DbManager db;
    private static camera inCamera;
    private static camera outCamera;

    /**
     * @param db    数据库
     * @param inCamera 入口像机
     * @param outCamera 出口像机
     */
    public  carInfoProcess(DbManager db,camera inCamera,camera outCamera){
        this.db = db;
        this.inCamera = inCamera;
        this.outCamera = outCamera;
    }
     /**
     * @param carNumber 车号
     * @return 执行状态
     */
    public static  boolean processCarInFunc(String carNumber,String picPath){
        Log.i("log","out process:"+carNumber);
        if(carNumber.length()<=4){
            //无牌车处理
            return false;
        }
        try {
            CarInfoTable carInfo = db.selector(CarInfoTable.class).where("car_no", "=", carNumber).findFirst();
            if(carInfo != null) {
                Log.i("log","find:"+carNumber + "type:" + carInfo.getCar_type());
                //固定车
                if (carInfo.getCar_type().equals("固定车")) {
                    processInRegistCar(carInfo);
                }
                return true;
            }else{
                //处理临时车
                processInTempCar(carNumber,picPath);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static  boolean processCarOutFunc(String carNumber) {
        Log.i("log","out process:"+carNumber);
        if(carNumber.length()<=4){
            //无牌车处理
            return false;
        }
        try {
            CarInfoTable carInfo = db.selector(CarInfoTable.class).where("car_no", "=", carNumber).findFirst();
            if(carInfo != null) {
                Log.i("log","find:"+carNumber + "type:" + carInfo.getCar_type());
                //固定车
                if (carInfo.getCar_type().equals("固定车")) {
                    return(processOutRegistCar(carInfo));
                }
                return false;
            }else{
                //处理临时车
                return(processOutTempCar(carNumber));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param portName 出入口方向
     * @param carNumber 车号
     * @param user 操作员
     * @return
     */
    public static boolean processManualOpenFunc(String portName,String carNumber,String user){
        //保存手动开门记录
        return true;
    }
    /**
     * @param carInfo 车辆信息
     * @return 执行状态
     */
    private static boolean processOutRegistCar(CarInfoTable carInfo){
        String AudioString = null;
        TrafficInfoTable trafficInfo = null;
        String[] dispInfo = new String[]{null, null,null,null};
        //判断有限期
        Date nowDate = new Date();
        long userDate = (carInfo.getStop_date().getTime() - nowDate.getTime())/(24*60*60*1000);
        long startDate = (nowDate.getTime() - carInfo.getStart_date().getTime())/(24*60*60*1000);
        Log.i("log", "date" + userDate + " " + startDate);
        if(userDate < 0)
        {
            //过期车
            AudioString =camera.AudioList.get("有效期到") + " " + camera.AudioList.get("请缴费");
            //初始化显示屏内容
            //车类型
            dispInfo[0] = carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] =  "有效期0天";
            //欢迎观临
            dispInfo[3] = " 请缴费";
            //显示
            outCamera.ledDisplay(dispInfo);
            //语音
            outCamera.playAudio(AudioString);
            return false;
        }
        if (startDate >= 0 && userDate < 10 && userDate >=0) {
            //续费提示
            StringBuffer buffer = new StringBuffer();
            buffer.append(camera.AudioList.get("此卡可用日期") + " ");
            buffer.append(userDate + " ");
            buffer.append(camera.AudioList.get("天") + " ");
            buffer.append(camera.AudioList.get("请尽快延期"));
            AudioString = buffer.toString();
            //初始化显示屏内容
            //车类型
            dispInfo[0] = " " + carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] = String.format("有效期:%d天", userDate);
            //欢迎观临
            dispInfo[3] = "\\DH时\\DM分";
            //显示
            outCamera.ledDisplay(dispInfo);
            //语音
            try {
                outCamera.playAudio(AudioString);
                Thread.sleep(3000);
                //起杆
                outCamera.openGate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (startDate >= 0 && userDate >= 10) {
            //初始化显示屏内容
            //车类型
            dispInfo[0] = carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] = " 请通行";
            //欢迎观临
            dispInfo[3] = "\\DH时\\DM分";
            //起杆
            outCamera.openGate();
            //显示
            outCamera.ledDisplay(dispInfo);
            //语音
            AudioString  = camera.AudioList.get("欢迎光临");
            outCamera.playAudio(AudioString);
        }
        //保存数据
        try {
            trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", carInfo.getCar_no()).and("out_time", "=", null).findFirst();
            if(trafficInfo == null) {
                trafficInfo = new TrafficInfoTable();
                trafficInfo.setCar_no(carInfo.getCar_no());
                trafficInfo.setCard_type(carInfo.getCar_type());
                trafficInfo.setOut_time(new Date());
                db.save(trafficInfo);
            }
            else{
                trafficInfo.setOut_time(new Date());
                db.update(trafficInfo,"out_time");
            }
            db.save(trafficInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        //写收费记录表
        MainActivity.chargeInfo.setCarNumber(trafficInfo.getCar_no());
        MainActivity. chargeInfo.setType(trafficInfo.getCard_type());
        MainActivity.chargeInfo.setInTime(trafficInfo.getIn_time());
        MainActivity.chargeInfo.setOutTime(trafficInfo.getOut_time());
        MainActivity.chargeInfo.setMoney(0);
        long timeLong = (trafficInfo.getOut_time().getTime() - trafficInfo.getIn_time().getTime())/60/1000;
        String timeFormat = String.format("%d时%d分",timeLong/60,timeLong%60);
        MainActivity.chargeInfo.setParkTime(timeFormat);
        return true;
    }
    private static boolean processInRegistCar(CarInfoTable carInfo) throws DbException {
        String AudioString = null;
        String[] dispInfo = new String[]{null, null,null,null};
        //判断有限期
        Date nowDate = new Date();
        long userDate = (carInfo.getStop_date().getTime() - nowDate.getTime())/(24*60*60*1000);
        long startDate = (nowDate.getTime() - carInfo.getStart_date().getTime())/(24*60*60*1000);
        if (startDate >= 0 && userDate < 10 && userDate >=0) {
            //续费提示
            StringBuffer buffer = new StringBuffer();
            buffer.append(camera.AudioList.get("此卡可用日期") + " ");
            buffer.append(userDate + " ");
            buffer.append(camera.AudioList.get("天") + " ");
            buffer.append(camera.AudioList.get("请尽快延期"));
            AudioString = buffer.toString();
            //初始化显示屏内容
            //车类型
            dispInfo[0] = " "+carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] = String.format("有效期:%d天", userDate);
            //欢迎观临
            dispInfo[3] = "欢迎光临";
            //显示
            inCamera.ledDisplay(dispInfo);
            //语音
            try {
                inCamera.playAudio(AudioString);
                Thread.sleep(3000);
                //起杆
                inCamera.openGate();
                //保存数据
                TrafficInfoTable trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", carInfo.getCar_no()).and("out_time", "=", null).findFirst();
                if(trafficInfo == null) {
                    trafficInfo = new TrafficInfoTable();
                    trafficInfo.setCar_no(carInfo.getCar_no());
                    trafficInfo.setCard_type(carInfo.getCar_type());
                    trafficInfo.setIn_time(new Date());
                    trafficInfo.setOut_time(null);
                    db.save(trafficInfo);
                }
                else
                {
                    trafficInfo.setIn_time(new Date());
                    db.update(trafficInfo,"in_time");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else if (startDate >= 0 && userDate >= 10) {
            //初始化显示屏内容
            //车类型
            dispInfo[0] = carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] = " 请通行";
            //欢迎观临
            dispInfo[3] = "欢迎光临";
            //起杆
            inCamera.openGate();
            //显示
            inCamera.ledDisplay(dispInfo);
            //语音
            AudioString  = camera.AudioList.get("欢迎光临");
            inCamera.playAudio(AudioString);
            //保存数据
            TrafficInfoTable trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", carInfo.getCar_no()).and("out_time", "=", null).findFirst();
            if(trafficInfo == null) {
                trafficInfo = new TrafficInfoTable();
                trafficInfo.setCar_no(carInfo.getCar_no());
                trafficInfo.setCard_type(carInfo.getCar_type());
                trafficInfo.setIn_time(new Date());
                trafficInfo.setOut_time(null);
                db.save(trafficInfo);
            }
            else
            {
                trafficInfo.setIn_time(new Date());
                db.update(trafficInfo,"in_time");
            }
        } else {
            //过期车
            AudioString =camera.AudioList.get("有效期到") + " " + camera.AudioList.get("请缴费");
            //初始化显示屏内容
            //车类型
            dispInfo[0] = carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] =  "有效期0天";
            //欢迎观临
            dispInfo[3] = "请缴费";
            //显示
            inCamera.ledDisplay(dispInfo);
            //语音
            inCamera.playAudio(AudioString);
        }
        return false;
    }
    //保存无牌车入场记录
    public static boolean saveInNoPlateCar(String picPath) throws DbException {
        String AudioString;
        String[] dispInfo = new String[]{null, null,null,null};
        //初始化显示屏内容
        //车类型
        dispInfo[0] = " 临时车";
        //车号
        dispInfo[1] = "无牌车";
        //有效日期
        dispInfo[2] =  "欢迎光临";
        //欢迎观临
        dispInfo[3] ="\\DH时\\DM分";
        //显示
        inCamera.ledDisplay(dispInfo);
        AudioString = camera.AudioList.get("欢迎光临");
        //语音
        inCamera.playAudio(AudioString);
        //保存数据
        TrafficInfoTable trafficInfo = new TrafficInfoTable();
        trafficInfo.setCar_no("无牌车");
        trafficInfo.setCard_type("临时车");
        trafficInfo.setIn_time(new Date());
        trafficInfo.setOut_time(null);
        trafficInfo.setIn_image(picPath);
        db.save(trafficInfo);
        return true;
    }
    //保存手动起杆入场记录
    public static boolean saveInTempCar(String carNumber,String picPath) throws DbException {
        //入口起杆
        inCamera.openGate();
        //语音
        inCamera.playAudio(camera.AudioList.get("欢迎光临"));
        //保存数据
        TrafficInfoTable trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=",carNumber).and("out_time", "=", null).findFirst();
        if(trafficInfo == null) {
            trafficInfo = new TrafficInfoTable();
            trafficInfo.setCar_no(carNumber);
            trafficInfo.setCard_type("临时车");
            trafficInfo.setIn_time(new Date());
            trafficInfo.setOut_time(null);
            trafficInfo.setIn_image(picPath);
            db.save(trafficInfo);
        }
        else
        {
            trafficInfo.setIn_time(new Date());
            trafficInfo.setIn_image(picPath);
            db.update(trafficInfo,"in_time","in_image");
        }
        return true;
    }
    /**
     * 入口临时车处理
     * @param carNumber 车号
     * @return
     */
    private static boolean processInTempCar(String carNumber,String picPath) throws DbException {
        String[] dispInfo = new String[]{null, null,null,null};
         //初始化显示屏内容
        //车类型
        dispInfo[0] = "临时车";
        //车号
        dispInfo[1] = carNumber;
        //有效日期
        dispInfo[2] =  "欢迎光临";
        //欢迎观临
        dispInfo[3] ="\\DH时\\DM分";
        //显示
        inCamera.ledDisplay(dispInfo);
        boolean tempFlag = MyApplication.settingInfo.getBoolean("tempCarIn");
        if(!tempFlag) {
            saveInTempCar(carNumber, picPath);
        }
        return true;
    }
    //格式化停车时长字符串
    private static String formatChargeStrTime(long minute) {
        StringBuffer buffer = new StringBuffer();
        long hour=minute/60;
        minute %= 60;
        buffer.append(camera.AudioList.get("停车") + " ");
        //最大小时
        if (hour > 999) {
            hour = 999;
        }
        //大于100小时
        if (hour / 100 > 0) {
            buffer.append(camera.AudioList.get(String.valueOf(hour / 100)) + " " + camera.AudioList.get("百") + " ");
            hour %= 100;
            buffer.append(camera.AudioList.get(String.valueOf(hour / 10)) + " ");
            if (hour / 10 > 0) {
                buffer.append(camera.AudioList.get("十") + " ");
            }
            hour %= 10;
            buffer.append(camera.AudioList.get(String.valueOf(hour)) + " ");
            buffer.append(camera.AudioList.get("小时"));
        }
        ///大于10小时
        else if (hour / 10 > 0) {
            buffer.append(camera.AudioList.get(String.valueOf(hour / 10)) + " " + camera.AudioList.get("十") + " ");
            hour %= 10;
            if (hour > 0) {
                buffer.append(camera.AudioList.get(String.valueOf(hour)) + " ");
            }
            buffer.append(camera.AudioList.get("小时"));
        }
        //10小时以内
        else if (hour > 0){
            buffer.append(camera.AudioList.get(String.valueOf(hour)) + " ");
            buffer.append(camera.AudioList.get("小时"));
        }
        buffer.append(" ");
        //大于10分种
        if (minute / 10 > 0) {
            buffer.append(camera.AudioList.get(String.valueOf(minute / 10)) + " " + camera.AudioList.get("十") + " ");
            minute %= 10;
            if (minute > 0) {
                buffer.append(camera.AudioList.get(String.valueOf(minute)) + " ");
            }
        }
        //10小时以内
        else {
            buffer.append(camera.AudioList.get(String.valueOf(minute)) + " ");
        }
        buffer.append(camera.AudioList.get("分钟"));
        return (buffer.toString());
    }
    //格式化收费语音字符串
   private static String formatChargeStrMoney(int money) {

       StringBuffer buffer = new StringBuffer();
       buffer.append(camera.AudioList.get("请缴费") + " ");
           //最大收费999
           if (money > 999) {
               money = 999;
           }
           //百元
           if (money / 100 > 0) {
               buffer.append(camera.AudioList.get(String.valueOf(money / 100)) + " " + camera.AudioList.get("百") + " ");
               money %= 100;
               buffer.append(camera.AudioList.get(String.valueOf(money / 10)) + " ");
               if (money / 10 > 0) {
                   buffer.append(camera.AudioList.get("十") + " ");
               }
               money %= 10;
               if (money > 0) {
                   buffer.append(camera.AudioList.get(String.valueOf(money)) + " ");
               }
               buffer.append(camera.AudioList.get("元"));
           }
           //十元
           else if (money / 10 > 0) {
               buffer.append(camera.AudioList.get(String.valueOf(money / 10)) + " " + camera.AudioList.get("十") + " ");
               money %= 10;
               if (money > 0) {
                   buffer.append(camera.AudioList.get(String.valueOf(money)) + " ");
               }
               buffer.append(camera.AudioList.get("元"));
           }
           //十元以内
           else if (money > 0) {
               buffer.append(camera.AudioList.get(String.valueOf(money)) + " ");
               buffer.append(camera.AudioList.get("元"));
           }
           return (buffer.toString());
   }
    //根据停车时长计算收费金额
    private static double moneyCount(double time)
    {
        int  reuslt=0;
        double  hours=time/60;
        double remainder=hours%24;
        int round=(int)hours/24;
        try {
            reuslt=round*db.selector(MoneyTable.class).where("part_time","=",24).findFirst().getMoney().intValue();
            MoneyTable mymonettable =db.selector(MoneyTable.class).where("part_time", ">=",remainder).and("part_time", "<", remainder+0.5).findFirst();
            if(mymonettable!=null)
                reuslt= reuslt+mymonettable.getMoney().intValue();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return   reuslt;
    }
    /**
     * @param carNumber 车号
     * @return
     */
    private static boolean processOutTempCar(String carNumber){
        String[] dispInfo = new String[]{null, null,null,null};
            //保存记录
            try {
                TrafficInfoTable trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", carNumber).and("out_time", "=", null).findFirst();
                if(trafficInfo == null) {
                    //初始化显示屏内容
                    dispInfo[0] = "车牌识别  一车一杆  减速慢行";
                    //车类型
                    dispInfo[1] = " 临时车";
                    //车号
                    dispInfo[2] = carNumber;
                    //有效日期
                    dispInfo[3] =  "无入场";
                    //显示
                    outCamera.ledDisplay(dispInfo);
                    //延时播放语音
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            outCamera.playAudio(camera.AudioList.get("未进主区"));
                        }
                    }, 5000);
                    return false;
                }
                //写收费记录表
                MainActivity.chargeInfo.setCarNumber(carNumber);
                MainActivity. chargeInfo.setType(trafficInfo.getCard_type());
                MainActivity.chargeInfo.setInTime(trafficInfo.getIn_time());
                MainActivity.chargeInfo.setOutTime(new Date());
                final long timeLong = (MainActivity.chargeInfo.getOutTime().getTime() - MainActivity.chargeInfo.getInTime().getTime())/60/1000;
                MainActivity.chargeInfo.setMoney(moneyCount(timeLong));
                String timeFormat = String.format("%d时%d分",timeLong/60,timeLong%60);
                MainActivity.chargeInfo.setParkTime(timeFormat);
                //初始化显示屏内容
                //车类型
                dispInfo[0] = "临时车";
                //车号
                dispInfo[1] = carNumber;
                //停车时长
                dispInfo[2] = " 停车：" + timeFormat;
                //缴费
                dispInfo[3] =  String.format("请缴费: %.1f元",MainActivity.chargeInfo.getMoney());
                //显示
                outCamera.ledDisplay(dispInfo);
                //延时播放语音
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        outCamera.playAudio(formatChargeStrTime(timeLong)+ " " + formatChargeStrMoney((int) MainActivity.chargeInfo.getMoney()));
                    }
                }, 5000);
            } catch (DbException e) {
                e.printStackTrace();
            }
        return true;
    }
}
