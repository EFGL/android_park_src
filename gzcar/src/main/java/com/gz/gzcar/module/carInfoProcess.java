package com.gz.gzcar.module;

import android.net.rtp.AudioStream;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.util.Log;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.FreeInfoTable;
import com.gz.gzcar.Database.MoneyTable;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.MainActivity;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.device.camera;
import com.gz.gzcar.utils.FileUtils;
import com.gz.gzcar.utils.T;

import org.xutils.*;
import org.xutils.ex.DbException;
import java.util.*;

/**
 * Created by Administrator on 2016/9/30.
 */
public class carInfoProcess {
    private DbManager db;
    private camera inCamera;
    private camera outCamera;
    private MainActivity mainActivity;
    /**
     * @param db    数据库
     * @param inCamera 入口像机
     * @param outCamera 出口像机
     */
    public  carInfoProcess(DbManager db,camera inCamera,camera outCamera){
        this.db = db;
        this.inCamera = inCamera;
        this.outCamera = outCamera;
        mainActivity = inCamera.mainActivity;
    }
     /**
     * @param carNumber 车号
     * @return 执行状态
     */
    public  boolean processCarInFunc(String carNumber,byte[] picBuffer){
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
                    processInRegistCar(carInfo,picBuffer);
                }
                return true;
            }else{
                //处理临时车
                processInTempCar(carNumber,picBuffer);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean processCarOutFunc(String carNumber,byte[] picBuffer) {
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
                    return(processOutRegistCar(carInfo,picBuffer));
                }
                return false;
            }else{
                //处理临时车
                return(processOutTempCar(carNumber,picBuffer));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
        /**
     * @param carInfo 车辆信息
     * @return 执行状态
     */
    private boolean processOutRegistCar(CarInfoTable carInfo,byte[] picBuffer){
        TrafficInfoTable trafficInfo = null;
        String[] dispInfo = new String[]{null, null,null,null};
        //判断有限期
        Date nowDate = new Date();
        long userDate = (carInfo.getStop_date().getTime() - nowDate.getTime())/(24*60*60*1000);
        long startDate = (nowDate.getTime() - carInfo.getStart_date().getTime())/(24*60*60*1000);
        Log.i("log", "date" + userDate + " " + startDate);
        if(userDate <= 0)
        {
            //过期车
            //初始化显示屏内容
            //车类型
            dispInfo[0] = " " + carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] =  "有效期0天";
            //欢迎观临
            dispInfo[3] = " 请续费";
            //显示
            outCamera.ledDisplay(dispInfo);
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    outCamera.playAudio(camera.AudioList.get("有效期到") + " " + camera.AudioList.get("请缴费"));
                }}, 5000);
            return false;
        }
        if (startDate >= 0 && userDate < 10 && userDate >0) {
            //续费提示
            StringBuffer buffer = new StringBuffer();
            buffer.append(camera.AudioList.get("此卡可用日期") + " ");
            buffer.append(userDate + " ");
            buffer.append(camera.AudioList.get("天") + " ");
            buffer.append(camera.AudioList.get("请尽快延期"));
            final String AudioString = buffer.toString();
            //初始化显示屏内容
            //车类型
            dispInfo[0] = " " + carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] = String.format("有效期:%d天", userDate);
            //欢迎观临
            dispInfo[3] = "请尽快延期";
            //显示
            outCamera.ledDisplay(dispInfo);
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    outCamera.playAudio(AudioString);
                    //起杆
                    outCamera.openGate();
                }}, 5000);
        } else if (startDate >= 0 && userDate >= 10) {
            //初始化显示屏内容
            //车类型
            dispInfo[0] = " " + carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] = " 请通行";
            //欢迎观临
            dispInfo[3] = "欢迎光临";
            //起杆
            outCamera.openGate();
            //显示
            outCamera.ledDisplay(dispInfo);
        }
        //保存数据
        try {
            FileUtils picFileManage = new FileUtils();
            String picPath = picFileManage.savePicture(picBuffer);  //保存图片
            trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", carInfo.getCar_no()).and("status", "=", "已入").findFirst();
            if(trafficInfo == null) {
                trafficInfo = new TrafficInfoTable();
                trafficInfo.setCar_no(carInfo.getCar_no());
                trafficInfo.setCar_type(carInfo.getCar_type());
                trafficInfo.setIn_time(null);
                trafficInfo.setIn_image(null);
                trafficInfo.setOut_time(new Date());
                trafficInfo.setOut_image(picPath);
                trafficInfo.setOut_user(mainActivity.loginUserName);
                trafficInfo.setStatus("已入");
                trafficInfo.setUpdateTime(new Date());
                trafficInfo.setModifeFlage(false);
                db.save(trafficInfo);
            }
            else{
                trafficInfo.setOut_time(new Date());
                trafficInfo.setOut_image(picPath);
                trafficInfo.setOut_user(mainActivity.loginUserName);
                trafficInfo.setUpdateTime(new Date());
                trafficInfo.setModifeFlage(false);
                db.update(trafficInfo, "out_time","out_image","out_user","update_time","modifeFlage");
            }
            db.save(mainActivity.outPortLog);
        } catch (DbException e) {
            e.printStackTrace();
        }
        String timeFormat;
        if(mainActivity.outPortLog.getIn_time() != null) {
            final long timeLong = (mainActivity.outPortLog.getOut_time().getTime() - mainActivity.outPortLog.getIn_time().getTime()) / 60 / 1000;
            timeFormat = String.format("%d时%d分",timeLong/60,timeLong%60);
        }else{
            timeFormat = "无入场记录";
        }
        mainActivity.outPortLog.setReceivable(0.0);
        mainActivity.outPortLog.setCar_type("固定车");
        mainActivity.outPortLog.setCar_no(trafficInfo.getCar_no());
        mainActivity.outPortLog.setStall(timeFormat);
        return true;
    }
    //处理入口固定车
    private boolean processInRegistCar(CarInfoTable carInfo,byte[] picBuffer) throws DbException {
        String[] dispInfo = new String[]{null, null,null,null};
        //判断有限期
        Date nowDate = new Date();
        long userDate = (carInfo.getStop_date().getTime() - nowDate.getTime())/(24*60*60*1000);
        long startDate = (nowDate.getTime() - carInfo.getStart_date().getTime())/(24*60*60*1000);
        if(userDate <= 0)
        {
            //过期车
            //初始化显示屏内容
            //车类型
            dispInfo[0] = " " + carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] =  "有效期0天";
            //欢迎观临
            dispInfo[3] = " 请续费";
            //显示
            inCamera.ledDisplay(dispInfo);
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    inCamera.playAudio(camera.AudioList.get("有效期到") + " " + camera.AudioList.get("请缴费"));
                }}, 5000);
            return false;
        }
        if (startDate >= 0 && userDate < 10 && userDate >0) {
            //续费提示
            StringBuffer buffer = new StringBuffer();
            buffer.append(camera.AudioList.get("此卡可用日期") + " ");
            buffer.append(userDate + " ");
            buffer.append(camera.AudioList.get("天") + " ");
            buffer.append(camera.AudioList.get("请尽快延期"));
            final String AudioString = buffer.toString();
            //初始化显示屏内容
            //车类型
            dispInfo[0] = " "+carInfo.getCar_type();
            //车号
            dispInfo[1] = carInfo.getCar_no();
            //有效日期
            dispInfo[2] = String.format("有效期:%d天", userDate);
            //欢迎观临
            dispInfo[3] = "请尽快延期";
            //显示
            inCamera.ledDisplay(dispInfo);
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    inCamera.playAudio(AudioString);
                    //起杆
                    inCamera.openGate();
                }}, 5000);
        }
        else if (startDate >= 0 && userDate >= 10)  {
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
        }
        //保存数据
        FileUtils picFileManage = new FileUtils();
        String picPath = picFileManage.savePicture(picBuffer);  //保存图片
        TrafficInfoTable trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", carInfo.getCar_no()).and("status", "=", "已入").findFirst();
        if(trafficInfo == null) {
            trafficInfo = new TrafficInfoTable();
            trafficInfo.setCar_no(carInfo.getCar_no());
            trafficInfo.setCar_type(carInfo.getCar_type());
            trafficInfo.setIn_time(new Date());
            trafficInfo.setIn_image(picPath);
            trafficInfo.setOut_time(null);
            trafficInfo.setStall(null);
            trafficInfo.setIn_user(mainActivity.loginUserName);
            trafficInfo.setStatus("已入");
            trafficInfo.setUpdateTime(new Date());
            trafficInfo.setModifeFlage(false);
            db.save(trafficInfo);
        }else{
            trafficInfo.setIn_time(new Date());
            trafficInfo.setIn_image(picPath);
            trafficInfo.setIn_user(mainActivity.loginUserName);
            trafficInfo.setUpdateTime(new Date());
            trafficInfo.setModifeFlage(false);
            db.update(trafficInfo, "in_time","in_image","in_user","update_time","modifeFlage");
        }
        return true;
    }
    //保存无牌车入场记录
    public boolean saveInNoPlateCar(String picPath) throws DbException {
        String AudioString;
        String[] dispInfo = new String[]{null, null,null,null};
        inCamera.openGate();
        //初始化显示屏内容
        //车类型
        dispInfo[0] = " 临时车";
        //车号
        dispInfo[1] = "无牌车";
        //有效日期
        dispInfo[2] =  "无牌入场";
        //欢迎观临
        dispInfo[3] ="欢迎光临";
        //显示
        inCamera.ledDisplay(dispInfo);
        AudioString = camera.AudioList.get("欢迎光临");
        //语音
        inCamera.playAudio(AudioString);
        //保存数据
        TrafficInfoTable trafficInfo = new TrafficInfoTable();
        trafficInfo.setCar_no("无牌车");
        trafficInfo.setCar_type("临时车");
        trafficInfo.setIn_time(new Date());
        trafficInfo.setIn_image(picPath);
        trafficInfo.setStall(null);
        trafficInfo.setStatus("已入");
        trafficInfo.setIn_user(mainActivity.loginUserName);
        trafficInfo.setUpdateTime(new Date());
        trafficInfo.setModifeFlage(false);
        db.save(trafficInfo);
        return true;
    }
    //保存手动起杆入场记录
    public boolean saveInTempCar(String carNumber,byte[] picBuffer) throws DbException {
        //入口起杆
        inCamera.openGate();
        //语音
        inCamera.playAudio(camera.AudioList.get("欢迎光临"));
        //保存图片
        FileUtils picFileManage = new FileUtils();
        String picPath = picFileManage.savePicture(picBuffer);
        //保存数据
        TrafficInfoTable trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=",carNumber).and("status", "=", "已入").findFirst();
        if(trafficInfo == null) {
            trafficInfo = new TrafficInfoTable();
            trafficInfo.setCar_no(carNumber);
            trafficInfo.setCar_type("临时车");
            trafficInfo.setIn_time(new Date());
            trafficInfo.setOut_time(null);
            trafficInfo.setIn_image(picPath);
            trafficInfo.setStall(null);
            trafficInfo.setIn_user(mainActivity.loginUserName);
            trafficInfo.setStatus("已入");
            trafficInfo.setUpdateTime(new Date());
            trafficInfo.setModifeFlage(false);
            db.save(trafficInfo);
        }
        else
        {
            trafficInfo.setIn_time(new Date());
            trafficInfo.setIn_image(picPath);
            trafficInfo.setIn_user(mainActivity.loginUserName);
            trafficInfo.setStatus("已入");
            trafficInfo.setUpdateTime(new Date());
            trafficInfo.setModifeFlage(false);
            db.update(trafficInfo, "in_time","in_image","in_user","update_time","modifeFlage");
        }
        return true;
    }
    /**
     * 入口临时车处理
     * @param carNumber 车号
     * @return
     */
    private boolean processInTempCar(String carNumber,byte[] picBuffer) throws DbException {
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
            saveInTempCar(carNumber, picBuffer);
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
   private static String formatChargeStrMoney(double money) {
       if (money > 999) {
           money = 999;
       }
       String NumberStr1 = String.valueOf((int)(money/100));
       money %= 100;
       String NumberStr2 = String.valueOf((int)(money/10));
       money %= 10;
       String NumberStr3 = String.valueOf((int)(money/1));
       money %= 1;
       String NumberStr4 = String.valueOf((int)(money/0.1));
       StringBuffer buffer = new StringBuffer();
       buffer.append(camera.AudioList.get("请缴费"));
       if(!NumberStr1.equals("0")){
           buffer.append(" " + camera.AudioList.get(NumberStr1) + " " + camera.AudioList.get("百"));
       }
       if(!NumberStr2.equals("0")){
           buffer.append(" " + camera.AudioList.get(NumberStr2) + " " + camera.AudioList.get("十"));
       }
       if(!NumberStr3.equals("0")){
           buffer.append(" " + camera.AudioList.get(NumberStr3));
       }
       buffer.append(" " +  camera.AudioList.get("元"));
       if(!NumberStr4.equals("0")){
           buffer.append(" " + camera.AudioList.get(NumberStr4) + " " + camera.AudioList.get("角"));
       }
       return (buffer.toString());
   }
    //根据停车时长计算收费金额
    private double moneyCount(double time)
    {
        double  reuslt=0;
        double  hours=time/60;
        double remainder=hours%24;
        int round=(int)hours/24;
        try {
            reuslt=round*db.selector(MoneyTable.class).where("part_time","=",24).findFirst().getMoney();
            MoneyTable mymonettable =db.selector(MoneyTable.class).where("part_time", ">=",remainder).and("part_time", "<", remainder+0.5).findFirst();
            if(mymonettable!=null)
                reuslt= reuslt+mymonettable.getMoney();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return   reuslt;
    }
    /**
     * @param carNumber 车号
     * @return
     */
    private boolean processOutTempCar(String carNumber, final byte[] picBuffer){
        TrafficInfoTable trafficInfo = null;
        String[] dispInfo = new String[]{null, null,null,null};
            //保存记录
            try {
                trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", carNumber).and("status", "=", "已入").findFirst();
                if(trafficInfo == null) {
                    //初始化显示屏内容
                    dispInfo[0] = "车牌识别  一车一杆  减速慢行";
                    //车类型
                    dispInfo[1] = " 临时车";
                    //车号
                    dispInfo[2] = carNumber;
                    //有效日期
                    dispInfo[3] =  "未入场";
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
                    //设置显示
                    mainActivity.outPortLog.setCar_no(carNumber);
                    mainActivity.outPortLog.setCar_type("临时车");
                    mainActivity.outPortLog.setStall_time("无入场记录");
                    mainActivity.outPortLog.setReceivable(0.0);
                    return true;
                }
                //更新出口信息
                mainActivity.outPortLog.setOut_time(new Date());
                mainActivity.outPortLog.setStall_time(mainActivity.loginUserName);
                mainActivity.outPortLog.setUpdateTime(new Date());
                mainActivity.outPortLog.setModifeFlage(false);
                long timeLong = (mainActivity.outPortLog.getOut_time().getTime() - trafficInfo.getIn_time().getTime())/60/1000;
                if(timeLong<=0)
                {
                    timeLong = 1;
                }
                Double money = moneyCount(timeLong);
                mainActivity.outPortLog.setReceivable(money);
                String timeFormat = String.format("%d时%d分",timeLong/60,timeLong%60);
                mainActivity.outPortLog.setStall_time(timeFormat);
                //初始化显示屏内容
                //车类型
                dispInfo[0] = "临时车";
                //车号
                dispInfo[1] = carNumber;
                //停车时长
                dispInfo[2] = " 停车：" + timeFormat;
                //缴费
                dispInfo[3] =  String.format("请缴费: %.2f元",mainActivity.outPortLog.getReceivable());
                //显示
                outCamera.ledDisplay(dispInfo);
                boolean tempCarFree = MyApplication.settingInfo.getBoolean("tempCarFree");
                //判断收费为0时是否需要确认
                if(money == 0) {
                    if (!tempCarFree) {
                        saveOutTempCar(picBuffer);
                        //延时播放语音
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                outCamera.playAudio(camera.AudioList.get("一路顺风"));
                            }
                        }, 5000);
                        return true;
                    }
                }
                //延时播放语音
                final String audioString = formatChargeStrTime(timeLong)+ " " + formatChargeStrMoney(mainActivity.outPortLog.getReceivable());
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        outCamera.playAudio(audioString);
                    }
                }, 5000);
            } catch (DbException e) {
                e.printStackTrace();
            }
        return true;
    }
    /**
     * @param id 通行记录id
     * @return
     */
    public boolean processManualSelectOut(int id,byte[] picBuffer){
        String[] dispInfo = new String[]{null, null,null,null};
        //保存记录
        try {
            mainActivity.outPortLog = db.selector(TrafficInfoTable.class).where("id", "=", id).findFirst();
            if(mainActivity.outPortLog == null) {
                return false;
            }
            //更新出口信息
            mainActivity.outPortLog.setOut_time(new Date());
            mainActivity.outPortLog.setOut_user(mainActivity.loginUserName);
            mainActivity.outPortLog.setUpdateTime(new Date());
            mainActivity.outPortLog.setModifeFlage(false);
            long timeLong = ( mainActivity.outPortLog.getOut_time().getTime() -  mainActivity.outPortLog.getIn_time().getTime())/60/1000;
            if(timeLong<=0)
            {
                timeLong = 1;
            }
            Double money = moneyCount(timeLong);
            mainActivity.outPortLog.setReceivable(money);
            String timeFormat = String.format("%d时%d分",timeLong/60,timeLong%60);
            mainActivity.outPortLog.setStall_time(timeFormat);
            //初始化显示屏内容
            //车类型
            dispInfo[0] = mainActivity.outPortLog.getCar_type();
            //车号
            dispInfo[1] = mainActivity.outPortLog.getCar_no();
            //停车时长
            dispInfo[2] = " 停车：" + timeFormat;
            //缴费
            dispInfo[3] =  String.format("请缴费: %.2f元",mainActivity.outPortLog.getReceivable());
            //显示
            outCamera.ledDisplay(dispInfo);
            boolean tempCarFree = MyApplication.settingInfo.getBoolean("tempCarFree");
            //判断收费为0时是否需要确认
            if(money == 0) {
                if (!tempCarFree) {
                    saveOutTempCar(picBuffer);
                    outCamera.playAudio(camera.AudioList.get("一路顺风"));
                    return true;
                }
            }
            //延时播放语音
            final String audioString = formatChargeStrTime(timeLong)+ " " + formatChargeStrMoney( mainActivity.outPortLog.getReceivable());
            outCamera.playAudio(audioString);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }
    //保存临时收费车辆记录
    public boolean saveOutTempCar(byte[] picBuffer){
        TrafficInfoTable trafficInfo = null;
        outCamera.openGate();
        try {
            //更新通行记录
            FileUtils picFileManage = new FileUtils();
            String picPath = picFileManage.savePicture(picBuffer); //保存图片
            trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", mainActivity.outPortLog.getCar_no()).and("status", "=", "已入").findFirst();
            if (trafficInfo == null) {
                return false;
            } else {
                trafficInfo.setOut_time(mainActivity.outPortLog.getOut_time());
                trafficInfo.setOut_image(picPath);
                trafficInfo.setOut_user(mainActivity.loginUserName);
                trafficInfo.setReceivable(mainActivity.outPortLog.getReceivable());
                trafficInfo.setActual_money(mainActivity.outPortLog.getActual_money());
                trafficInfo.setStall_time(mainActivity.outPortLog.getStall_time());
                trafficInfo.setStatus("已出");
                trafficInfo.setUpdateTime(new Date());
                trafficInfo.setModifeFlage(false);
                db.update(trafficInfo, "out_time","out_image","out_user","update_time","status","stall","stall_time","receivable","actual_money","modifeFlage");
            }
            //更收费信息
            if(mainActivity.outPortLog.getReceivable()>0) {
                Double money = Double.valueOf(MyApplication.settingInfo.getString("chargeMoney")) + mainActivity.outPortLog.getReceivable();
                MyApplication.settingInfo.putString("chargeMoney",String.format("%.2f",money));
                MyApplication.settingInfo.putLong("chargeCarNumer", MyApplication.settingInfo.getLong("chargeCarNumer")+1);
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
    //保存免费出口车辆
    public boolean saveOutFreeCar(byte[] picBuffer){
        TrafficInfoTable trafficInfo = null;
        outCamera.openGate();
        try {
            //更新通行记录
            FileUtils picFileManage = new FileUtils();
            String picPath = picFileManage.savePicture(picBuffer); //保存图片
            trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=", mainActivity.outPortLog.getCar_no()).and("status", "=", "已入").findFirst();
            if (trafficInfo == null) {
                trafficInfo = new TrafficInfoTable();
                trafficInfo.setIn_time(null);
                trafficInfo.setIn_image(null);
                if(mainActivity.chargeCarNumber.getText().toString().isEmpty()) {
                    trafficInfo.setCar_no("无牌");
                }else{
                    trafficInfo.setCar_no(mainActivity.outPortLog.getCar_no());
                }
                trafficInfo.setCar_type("免费车");
                trafficInfo.setOut_time(new Date());
                trafficInfo.setOut_image(picPath);
                trafficInfo.setReceivable(mainActivity.outPortLog.getReceivable());
                trafficInfo.setActual_money(mainActivity.outPortLog.getReceivable());
                trafficInfo.setActual_money(0.0);
                trafficInfo.setStall_time(mainActivity.outPortLog.getStall_time());
                trafficInfo.setOut_user(mainActivity.loginUserName);
                trafficInfo.setStatus("已出");
                trafficInfo.setUpdateTime(new Date());
                trafficInfo.setModifeFlage(false);
                db.save(trafficInfo);
            } else {
                trafficInfo.setOut_time(mainActivity.outPortLog.getOut_time());
                trafficInfo.setOut_image(picPath);
                trafficInfo.setReceivable(mainActivity.outPortLog.getReceivable());
                trafficInfo.setActual_money(mainActivity.outPortLog.getReceivable());
                trafficInfo.setActual_money(0.0);
                trafficInfo.setStall_time(mainActivity.outPortLog.getStall_time());
                trafficInfo.setOut_user(mainActivity.loginUserName);
                trafficInfo.setStatus("已出");
                trafficInfo.setUpdateTime(new Date());
                trafficInfo.setModifeFlage(false);
                db.update(trafficInfo, "out_time","out_image","out_user","update_time","status","stall","stall_time","receivable","actual_money","modifeFlage");
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
}
