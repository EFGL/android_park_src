package com.gz.gzcar.module;

import android.net.rtp.AudioStream;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.security.keystore.KeyNotYetValidException;
import android.util.Log;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
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
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
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
    //查询有无空闲车位，有则返回车位组键，无则返回null,未分配车位则返回“null”
    private String findEmptyStall(String carNumber){
        String stall = null;
        try {
            //查找车位
            List<CarWeiBindTable> bindTable =  db.selector(CarWeiBindTable.class).where("car_no","=",carNumber).findAll();
            if(bindTable == null || bindTable.size()==0){
                stall =  "未分配";
            }else
            {
                for (CarWeiBindTable item:bindTable){
                    TrafficInfoTable log = db.selector(TrafficInfoTable.class).where("stall","=",item.getStall_code().toString()).and("car_no","!=",carNumber).and("status","=","已入").findFirst();
                    if (log == null){
                        return item.getStall_code().toString();
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return stall;
    }
    //查询车辆信息
    private CarInfoTable findCarInfo(String carNumber) throws DbException
    {
        CarInfoTable carInfo = null;
        //是否匹配汉字
        if(MyApplication.settingInfo.getBoolean("isUseChina"))
        {
            carInfo = db.selector(CarInfoTable.class).where("car_no", "=", carNumber).findFirst();
        }
        else
        {
            carInfo = db.selector(CarInfoTable.class).where("car_no", "like", carNumber.replace(carNumber.charAt(0),'%')).findFirst();
        }
        return carInfo;
    }
    //查询入口记录
    private  TrafficInfoTable findInPortLog(final String carNumber){
        TrafficInfoTable trafficInfo = null;
        //是否匹配汉字
        try{
            if(carNumber != null){
                if(MyApplication.settingInfo.getBoolean("isUseChina"))
                {
                    trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "=",carNumber).and("status", "=", "已入").findFirst();
                }
                else
                {
                    trafficInfo = db.selector(TrafficInfoTable.class).where("car_no", "like", carNumber.replace(carNumber.charAt(0),'%')).and("status", "=", "已入").findFirst();
                }
            }
        }catch (DbException e) {
            e.printStackTrace();
        }
        return trafficInfo;
    }
    //校验有效日期
    private long checkEffectiveDate(final camera myCamera, CarInfoTable carInfo){
        //判断有限期
        Date nowDate = new Date();
        long userDate = (carInfo.getStop_date().getTime() - nowDate.getTime())/(24*60*60*1000);
        long startDate = (nowDate.getTime() - carInfo.getStart_date().getTime())/(24*60*60*1000);
        if(userDate < 0)
        {
            //过期车
            //显示
            myCamera.ledDisplay(carInfo.getCar_type(),carInfo.getCar_no(), "有效期0天","请续费");
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    myCamera.playAudio(camera.AudioList.get("有效期到") + " " + camera.AudioList.get("请缴费"));
                }}, 5000);
            if(myCamera.getPortName().equals("out")){
                mainActivity.outPortLog.setReceivable(0.0);
                mainActivity.outPortLog.setCar_type(carInfo.getCar_type());
                mainActivity.outPortLog.setCar_no(carInfo.getCar_no());
                mainActivity.outPortLog.setStall_time(0);
            }
        }
        if (startDate >= 0 && userDate < 7 && userDate >=0) {
            //续费提示
            StringBuffer buffer = new StringBuffer();
            buffer.append(camera.AudioList.get("此卡可用日期") + " ");
            buffer.append(userDate + " ");
            buffer.append(camera.AudioList.get("天") );
            //固定车提示续费
            if(carInfo.getVehicle_type().equals("固定车")) {
                buffer.append(" "+camera.AudioList.get("请尽快延期"));
            }
            final String AudioString = buffer.toString();
            //显示
            myCamera.ledDisplay(carInfo.getCar_type(),carInfo.getCar_no(),String.format("有效期:%d天", userDate),"请尽快延期");
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    myCamera.playAudio(AudioString);
                    //起杆
                    myCamera.openGate();
                }}, 5000);
        } else if (startDate >= 0 && userDate >= 7) {

            if(myCamera.getPortName().equals("in")){
                //起杆
                myCamera.openGate();
                //显示
                myCamera.ledDisplay(carInfo.getCar_type(),carInfo.getCar_no(),"请通行","欢迎光临");
                //延时播放语音
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        myCamera.playAudio(camera.AudioList.get("请通行"));
                    }}, 5000);
            }else {
                //起杆
                myCamera.openGate();
                //显示
                myCamera.ledDisplay(carInfo.getCar_type(),carInfo.getCar_no(),"请通行","一路顺风");
                //延时播放语音
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        myCamera.playAudio(camera.AudioList.get("一路顺风"));
                    }}, 5000);
            }
        }
        return userDate;
    }
    //保存或更新入场记录
    private Boolean saveAndUpdateInLog(TrafficInfoTable trafficInfo,final byte[] picBuffer,final boolean upFlag)
    {
        try {
            FileUtils picFileManage = new FileUtils();
            String picPath = picFileManage.savePicture(picBuffer);  //保存图片
            if(upFlag){
                /*trafficInfo.setStatus("已出");
                trafficInfo.setUpdateTime(new Date());
                trafficInfo.setUpdated_controller_sn(MyApplication.devID);
                trafficInfo.setModifeFlage(false);
                trafficInfo.setReceivable(0.0);
                trafficInfo.setActual_money(0.0);*/
                db.update(TrafficInfoTable.class,
                        WhereBuilder.b("car_no","=",trafficInfo.getCar_no()),
                        new KeyValue("status","已出"),
                        new KeyValue("update_time", new Date()),
                        new KeyValue("updated_controller_sn",MyApplication.devID),
                        new KeyValue("modife_flage",false),
                        new KeyValue("receivable",0.0),
                        new KeyValue("stall_time",0),
                        new KeyValue("actual_money",0.0));
            }
            //
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            trafficInfo.setPass_no(MyApplication.devID + dateFormat.format(new Date()));
            trafficInfo.setIn_time(new Date());
            trafficInfo.setIn_image(picPath);
            trafficInfo.setOut_time(null);
            trafficInfo.setStall(null);
            trafficInfo.setIn_user(mainActivity.loginUserName);
            trafficInfo.setStatus("已入");
            trafficInfo.setUpdateTime(new Date());
            trafficInfo.setUpdated_controller_sn(MyApplication.devID);
            trafficInfo.setModifeFlage(false);
            db.save(trafficInfo);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    };
    //保存或更新出场记录
    private Boolean saveAndUpdateOutLog(TrafficInfoTable trafficInfo,byte[] picBuffer,boolean upFlag)
    {
        try {
            FileUtils picFileManage = new FileUtils();
            String picPath = picFileManage.savePicture(picBuffer);  //保存图片
            //更新车辆出厂状态
            trafficInfo.setOut_time(new Date());
            trafficInfo.setOut_image(picPath);
            trafficInfo.setOut_user(mainActivity.loginUserName);
            trafficInfo.setStatus("已出");
            trafficInfo.setUpdateTime(new Date());
            trafficInfo.setModifeFlage(false);
            trafficInfo.setUpdated_controller_sn(MyApplication.devID);
            if(upFlag) {
                db.update(trafficInfo, "out_time","out_image","receivable","actual_money","stall_time","out_user","update_time","updated_controller_sn","modife_flage","status");
            }
            else
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                trafficInfo.setPass_no(MyApplication.devID + dateFormat.format(new Date()));
                db.save(trafficInfo);
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    };
     /**
     * @param carNumber 车号
     * @return 执行状态
     */
    public  boolean processCarInFunc(String carNumber,byte[] picBuffer){
        Log.i("log","in process:"+carNumber);
        if(carNumber.length()<=4){
            //无牌车处理
            return false;
        }
        try {
            //查询车辆信息
            CarInfoTable carInfo = findCarInfo(carNumber);
            //检测有无车辆信息
            if(carInfo != null) {
                if(carInfo.getVehicle_type().equals("固定车"))
                {
                    processLeaseCarIn(carInfo,picBuffer);
                }else{
                    //特殊车
                    processOtherCarIn(carInfo,picBuffer);
                }
                return true;
            }else{
                //临时车
                processInTempCar(carNumber,picBuffer);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean processCarOutFunc(String carNumber,byte[] picBuffer,int delay) {
        Log.i("log","out process:"+carNumber);
        if(carNumber.length()<=4){
            //无牌车处理
            return false;
        }
        try {
            CarInfoTable carInfo = findCarInfo(carNumber);
            if(carInfo != null) {
                if (carInfo.getVehicle_type().equals("固定车")) {
                    return (processLeaseCarOut(carInfo, picBuffer));
                }
                else{
                    //特殊车
                    return (processOtherCarOut(carInfo,picBuffer,delay));
                }
            }else{
                //临时车出厂
                final int freeTime = MyApplication.settingInfo.getInt("tempFree");
                return(processOutChargeCar(carNumber,"临时车",freeTime,picBuffer,delay));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
    //处理租车位车辆出场
    private boolean processLeaseCarOut(final  CarInfoTable carInfo,byte[] picBuffer)
    {
        long timeLong = -1;
        long res =  checkEffectiveDate(outCamera,carInfo);
        if(res>=0) {
            TrafficInfoTable outLog = findInPortLog(carInfo.getCar_no());
            //保存数据
            if (outLog == null){
                outLog =  new TrafficInfoTable();
                outLog.setCar_type(carInfo.getCar_type());
                outLog.setCar_no(carInfo.getCar_no());
                outLog.setReceivable(0.0);
                outLog.setActual_money(0.0);
                outLog.setStall_time(-1);
                saveAndUpdateOutLog(outLog,picBuffer,false);
            }else{
                //计算停车时长
                if(outLog.getIn_time() != null ) {
                    timeLong = (new Date().getTime() - outLog.getIn_time().getTime()) / 60 / 1000;
                }
                outLog.setStall_time(timeLong);
                outLog.setReceivable(0.0);
                outLog.setActual_money(0.0);
                saveAndUpdateOutLog(outLog,picBuffer,true);
            }
            mainActivity.outPortLog.setReceivable(0.0);
            mainActivity.outPortLog.setCar_type(carInfo.getCar_type());
            mainActivity.outPortLog.setCar_no(carInfo.getCar_no());
            mainActivity.outPortLog.setStall_time(timeLong);
            return true;
        }
        return false;
    }
    //处理特殊车出场记录
    private boolean processOtherCarOut(CarInfoTable carInfo,byte[] picBuffer,int delay){
        long timeLong=  -1;
        //检测时间段是否为空，都不为空则按时间段校验
        if(carInfo.getStart_date() != null && carInfo.getStop_date() != null){
            //判断有限期
            long res =  checkEffectiveDate(inCamera,carInfo);
            if(res>=0) {
                TrafficInfoTable outLog = findInPortLog(carInfo.getCar_no());
                //保存数据
                if (outLog == null){
                    outLog =  new TrafficInfoTable();
                    outLog.setCar_type(carInfo.getFee_type());
                    outLog.setCar_no(carInfo.getCar_no());
                    outLog.setReceivable(0.0);
                    outLog.setActual_money(0.0);
                    outLog.setStall_time(-1);
                    saveAndUpdateOutLog(outLog,picBuffer,false);
                }else{
                    //计算停车时长
                    if(outLog.getIn_time() != null) {
                        timeLong = (new Date().getTime() - outLog.getIn_time().getTime()) / 60 / 1000;
                    }
                    outLog.setStall_time(timeLong);
                    outLog.setReceivable(0.0);
                    outLog.setActual_money(0.0);
                    saveAndUpdateOutLog(outLog,picBuffer,true);
                }
                //起杆
                outCamera.openGate();
                //显示
                outCamera.ledDisplay(carInfo.getFee_type(), carInfo.getCar_no(), "请通行", "一路顺风");
                //延时播放语音
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        outCamera.playAudio(camera.AudioList.get("一路顺风"));
                    }
                }, delay);
                mainActivity.outPortLog.setReceivable(0.0);
                mainActivity.outPortLog.setCar_type(carInfo.getCar_type());
                mainActivity.outPortLog.setCar_no(carInfo.getCar_no());
                mainActivity.outPortLog.setStall_time(timeLong);
                return true;
            }
        }
        //有效次数大于0，则核减有效次数
        else if(carInfo.getAllow_count() >0){
            //核减有效次数
            carInfo.setAllow_count(carInfo.getAllow_count()-1);
            try {
                db.update(carInfo,"allow_count");
            } catch (DbException e) {
                e.printStackTrace();
            }
            //起杆
            outCamera.openGate();
            //显示
            outCamera.ledDisplay(carInfo.getFee_type(), carInfo.getCar_no(), "请通行", "一路顺风");
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    outCamera.playAudio(camera.AudioList.get("一路顺风"));
                }
            }, delay);
            //保存数据
            TrafficInfoTable outLog = findInPortLog(carInfo.getCar_no());
            //保存数据
            if (outLog == null){
                outLog =  new TrafficInfoTable();
                outLog.setCar_type(carInfo.getFee_type());
                outLog.setCar_no(carInfo.getCar_no());
                outLog.setReceivable(0.0);
                outLog.setActual_money(0.0);
                outLog.setStall_time(-1);
                saveAndUpdateOutLog(outLog,picBuffer,false);
            }else{
                //计算停车时长
                if(outLog.getIn_time() != null) {
                     timeLong = (new Date().getTime() - outLog.getIn_time().getTime()) / 60 / 1000;
                }
                outLog.setStall_time(timeLong);
                outLog.setReceivable(0.0);
                outLog.setActual_money(0.0);
                saveAndUpdateOutLog(outLog,picBuffer,true);
                return true;
            }
            //设置显示界面内容
            mainActivity.outPortLog.setReceivable(0.0);
            mainActivity.outPortLog.setCar_type(carInfo.getCar_type());
            mainActivity.outPortLog.setCar_no(carInfo.getCar_no());
            mainActivity.outPortLog.setStall_time(timeLong);
        }
        else if(carInfo.getAllow_park_time() > 0){
            //特殊收费车辆按收费处理
            processOutChargeCar(carInfo.getCar_no(),carInfo.getFee_type(),carInfo.getAllow_park_time(),picBuffer,delay);
        }
        else {
            //有效次数为零或这期按临时车处理
            final int freeTime = MyApplication.settingInfo.getInt("tempFree");
            return (processOutChargeCar(carInfo.getCar_no(), "临时车", freeTime, picBuffer,delay));
        }
        return true;
    }
    //处理特殊车辆入场
    private boolean processOtherCarIn(CarInfoTable carInfo,byte[] picBuffer) throws DbException {
        //检测时间段是否为空，都不为空则按时间段校验
        if(carInfo.getStart_date() != null && carInfo.getStop_date() != null) {
            long res =  checkEffectiveDate(inCamera,carInfo);
            if(res>=0) {
                //保存记录
                TrafficInfoTable inLog = findInPortLog(carInfo.getCar_no());
                if (inLog == null){
                    inLog =  new TrafficInfoTable();
                    inLog.setCar_type(carInfo.getFee_type());
                    inLog.setCar_no(carInfo.getCar_no());
                    saveAndUpdateInLog(inLog,picBuffer,false);
                }else{
                    inLog.setCar_type(carInfo.getFee_type());
                    inLog.setCar_no(carInfo.getCar_no());
                    saveAndUpdateInLog(inLog,picBuffer,true);
                }
            }
        }
        else if (carInfo.getAllow_count()>0){
            //其它方式则保存入场记录
            //起杆
            inCamera.openGate();
            //显示
            inCamera.ledDisplay(carInfo.getFee_type(),carInfo.getCar_no(),"请通行","欢迎光临");
            //保存记录
            //保存记录
            TrafficInfoTable inLog = findInPortLog(carInfo.getCar_no());
            if (inLog == null){
                inLog =  new TrafficInfoTable();
                inLog.setCar_type(carInfo.getFee_type());
                inLog.setCar_no(carInfo.getCar_no());
                saveAndUpdateInLog(inLog,picBuffer,false);
            }else{
                inLog.setCar_type(carInfo.getFee_type());
                inLog.setCar_no(carInfo.getCar_no());
                saveAndUpdateInLog(inLog,picBuffer,true);
            }
        }
        else if (carInfo.getAllow_park_time()>0)
        {
            //其它方式则保存入场记录
            //起杆
            inCamera.openGate();
            //显示
            inCamera.ledDisplay(carInfo.getFee_type(),carInfo.getCar_no(),"请通行","欢迎光临");
            //保存记录
            //保存记录
            TrafficInfoTable inLog = findInPortLog(carInfo.getCar_no());
            if (inLog == null){
                inLog =  new TrafficInfoTable();
                inLog.setCar_type(carInfo.getFee_type());
                inLog.setCar_no(carInfo.getCar_no());
                saveAndUpdateInLog(inLog,picBuffer,false);
            }else{
                inLog.setCar_type(carInfo.getFee_type());
                inLog.setCar_no(carInfo.getCar_no());
                saveAndUpdateInLog(inLog,picBuffer,true);
            }
        }
        else
        {
            //按临时车入场处理
            processInTempCar(carInfo.getCar_no(),picBuffer);
        }
        return true;
    }
    //处理租车位车辆入场
    private boolean processLeaseCarIn(CarInfoTable carInfo,byte[] picBuffer) throws DbException {
        //检测有无空闲车位
        String emptyStall = findEmptyStall(carInfo.getCar_no());
        if (emptyStall ==  null)
        {
            //过期车
            inCamera.ledDisplay(carInfo.getCar_type(),carInfo.getCar_no(),"车位已满","需其它车辆出场后方可进入");
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    inCamera.playAudio(camera.AudioList.get("车位已满"));
                }}, 5000);
            return false;
        }
        //判断有限期
        long res =  checkEffectiveDate(inCamera,carInfo);
        if(res>=0) {
            //保存记录
            TrafficInfoTable inLog = findInPortLog(carInfo.getCar_no());
            if (inLog == null){
                inLog =  new TrafficInfoTable();
                inLog.setCar_type(carInfo.getCar_type());
                inLog.setCar_no(carInfo.getCar_no());
                inLog.setStall(emptyStall);
                saveAndUpdateInLog(inLog,picBuffer,false);
            }else{
                inLog.setCar_type(carInfo.getCar_type());
                inLog.setCar_no(carInfo.getCar_no());
                inLog.setStall(emptyStall);
                saveAndUpdateInLog(inLog,picBuffer,true);
            }
            return true;
        }
     return false;
    }
    //保存无牌车入场记录
    public boolean saveInNoPlateCar(final byte[] picBuffer) throws DbException {
        String AudioString;
        inCamera.openGate();
        //显示
        inCamera.ledDisplay("车牌识别","临时车","无牌入场","欢迎光临");
        AudioString = camera.AudioList.get("欢迎光临");
        //语音
        inCamera.playAudio(AudioString);
        //保存数据
        TrafficInfoTable inLog = new TrafficInfoTable();
        inLog.setCar_no("无牌");
        inLog.setCar_type("临时车");
        saveAndUpdateInLog(inLog,picBuffer,false);
        return true;
    }
    //保存手动起杆入场记录
    public boolean saveInTempCar(String carNumber,byte[] picBuffer) throws DbException {
        //入口起杆
        inCamera.openGate();
        //保存记录
        TrafficInfoTable inLog = findInPortLog(carNumber);
        if (inLog == null){
            inLog =  new TrafficInfoTable();
            inLog.setCar_type("临时车");
            inLog.setCar_no(carNumber);
            saveAndUpdateInLog(inLog,picBuffer,false);
        }else{
            inLog.setCar_type("临时车");
            inLog.setCar_no(carNumber);
            saveAndUpdateInLog(inLog,picBuffer,true);
        }
        return true;
    }
    /**
     * 入口临时车处理
     * @param carNumber 车号
     * @return
     */
    private boolean processInTempCar(String carNumber,byte[] picBuffer) throws DbException {
        inCamera.ledDisplay("临时车",carNumber,"欢迎光临","\\DH时\\DM分");
        boolean tempFlag = MyApplication.settingInfo.getBoolean("tempCarIn");
        if(!tempFlag) {
            saveInTempCar(carNumber, picBuffer);
            //延时播放语音
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    inCamera.playAudio(camera.AudioList.get("请通行"));
                }}, 5000);
            return true;
        }else
        {
            mainActivity.waitEnterCarNumber = carNumber;
        }
        return false;
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
       else
       {
           if(buffer.length()<4)
           {
               buffer.append(" " + camera.AudioList.get(NumberStr3));
           }
       }
       buffer.append(" " +  camera.AudioList.get("元"));
       if(!NumberStr4.equals("0")){
           buffer.append(" " + camera.AudioList.get(NumberStr4) + " " + camera.AudioList.get("角"));
       }
       return (buffer.toString());
   }
    //根据停车时长计算收费金额
    private double moneyCount(String type,double time)
    {
        double res = 0;
        int day = (int) time/24/60;
        int minute = (int)time % (24*60);
        try {
            MoneyTable maxTable = db.selector(MoneyTable.class).where("car_type_name","=",type).orderBy("money",true).findFirst();
            if(maxTable == null)
            {
                return res;
            }
            MoneyTable dayTable = db.selector(MoneyTable.class).where("car_type_name","=",type).and("parked_min_time","<=",minute).and("parked_max_time",">",minute).findFirst();
            if(dayTable == null)
            {
                return res;
            }
            //24小时累加开启则超出24小时返回最大值
            final boolean isHourAdd = MyApplication.settingInfo.getBoolean("isHourAdd");
            if(!isHourAdd  && day>0)
            {
                return  maxTable.getMoney();
            }
            res = day * maxTable.getMoney();
            res += dayTable.getMoney();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return   res;
    }
    /**
     * @param carNumber 车号
     * @return
     */
    private boolean processOutChargeCar(String carNumber,String carType,int freeTime, final byte[] picBuffer,int delay){
        TrafficInfoTable inLog = null;
            //保存记录
        inLog = findInPortLog(carNumber);
        if(inLog == null) {
            //显示
            inCamera.ledDisplay("车牌识别  一车一杆  减速慢行",carType,carNumber,"未入场");
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
            mainActivity.outPortLog.setCar_type(carType);
            mainActivity.outPortLog.setStall_time(-1);
            mainActivity.outPortLog.setReceivable(0.0);
            return true;
        }
        //更新出口信息
        long timeLong = (new Date().getTime() - inLog.getIn_time().getTime())/60/1000;
        mainActivity.outPortLog.setOut_time(new Date());
        mainActivity.outPortLog.setStall_time(timeLong);
        mainActivity.outPortLog.setUpdateTime(new Date());
        mainActivity.outPortLog.setModifeFlage(false);
        mainActivity.outPortLog.setCar_no(carNumber);
        mainActivity.outPortLog.setCar_type(carType);
        if(timeLong==0)
        {
            timeLong = 1;
        }else if (timeLong < 0 )
        {
            outCamera.playAudio(camera.AudioList.get("系统时间错误"));
            mainActivity.outPortLog.setStall_time(-2);
            mainActivity.outPortLog.setReceivable(0.0);
            return true;
        }
        Double money = 0.0;
        if (timeLong > freeTime)
        {
            //检测是否核减免费时长
            final boolean isFree = MyApplication.settingInfo.getBoolean("isFree");
            if(isFree || !carType.equals("临时车"))
            {
                money = moneyCount(carType,timeLong - freeTime);
            }else{
                money = moneyCount(carType,timeLong);
            }
        }
        mainActivity.outPortLog.setReceivable(money);
        mainActivity.outPortLog.setStall_time(timeLong);
        String timeFormat = String.format("%d时%d分",timeLong/60,timeLong%60);
        //显示
        inCamera.ledDisplay(carType,carNumber," 停车：" + timeFormat,String.format("请缴费: %.2f元",mainActivity.outPortLog.getReceivable()));
        boolean tempCarFree = MyApplication.settingInfo.getBoolean("tempCarFree");
        //判断收费为0时是否需要确认
        if(money == 0) {
            if (!tempCarFree) {
                saveOutTempCar(carNumber,picBuffer,0.0,0.0,timeLong);
                //延时播放语音
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        outCamera.playAudio(camera.AudioList.get("一路顺风"));
                    }
                }, delay);
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
        }, delay);
        return true;
    }
    //临时车手动出厂
    private boolean processManualOutTempCar(final TrafficInfoTable inLog,int freeTime, final byte[] picBuffer,int delay){
        //更新出口信息
        long timeLong = (new Date().getTime() - inLog.getIn_time().getTime())/60/1000;
        mainActivity.outPortLog.setOut_time(new Date());
        mainActivity.outPortLog.setStall_time(timeLong);
        mainActivity.outPortLog.setUpdateTime(new Date());
        mainActivity.outPortLog.setModifeFlage(false);
        mainActivity.outPortLog.setCar_no(inLog.getCar_no());
        mainActivity.outPortLog.setCar_type(inLog.getCar_type());
        if(timeLong==0)
        {
            timeLong = 1;
        }else if (timeLong < 0 )
        {
            outCamera.playAudio(camera.AudioList.get("系统时间错误"));
            mainActivity.outPortLog.setStall_time(-2);
            mainActivity.outPortLog.setReceivable(0.0);
            return true;
        }
        Double money = 0.0;
        if (timeLong > freeTime)
        {
            //检测是否核减免费时长
            final boolean isFree = MyApplication.settingInfo.getBoolean("isFree");
            if(isFree)
            {
                money = moneyCount(inLog.getCar_type(),timeLong - freeTime);
            }else{
                money = moneyCount(inLog.getCar_type(),timeLong);
            }
        }
        mainActivity.outPortLog.setReceivable(money);
        mainActivity.outPortLog.setStall_time(timeLong);
        String timeFormat = String.format("%d时%d分",timeLong/60,timeLong%60);
        //显示
        outCamera.ledDisplay(inLog.getCar_type(),inLog.getCar_no()," 停车：" + timeFormat,String.format("请缴费: %.2f元",mainActivity.outPortLog.getReceivable()));
        boolean tempCarFree = MyApplication.settingInfo.getBoolean("tempCarFree");
        //判断收费为0时是否需要确认
        if(money == 0) {
            if (!tempCarFree) {
                saveOutTempCar(inLog.getCar_no(),picBuffer,0.0,0.0,timeLong);
                //延时播放语音
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        outCamera.playAudio(camera.AudioList.get("一路顺风"));
                    }
                }, delay);
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
        }, delay);
        return true;
    }
    /**
     * @param id 通行记录id
     * @return
     */
    public boolean processManualSelectOut(int id,byte[] picBuffer){
        //保存记录
        try {
            mainActivity.outPortLog = db.selector(TrafficInfoTable.class).where("id", "=", id).findFirst();
            if(mainActivity.outPortLog == null) {
                return false;
            }
            if(mainActivity.outPortLog.getCar_type().equals("临时车")) {
                    final int freeTime = MyApplication.settingInfo.getInt("tempFree");
                    processManualOutTempCar(mainActivity.outPortLog,freeTime,picBuffer,1);
                }
                else{
                    CarInfoTable carInfo = db.selector(CarInfoTable.class).where("car_no","like",mainActivity.outPortLog.getCar_no()).findFirst();
                    if(carInfo != null) {
                        processOtherCarOut(carInfo, picBuffer,10);
                    }
                    else
                    {
                        return false;
                    }
                }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }
    //保存临时收费车辆记录
    public boolean saveOutTempCar(String carNumber,byte[] picBuffer,double receivable,double actualmoney,long stall_time){
        TrafficInfoTable outLog = null;
        outCamera.openGate();
        outLog = findInPortLog(carNumber);
        if(outLog == null){
            return false;
        }else{
            outLog.setReceivable(receivable);
            outLog.setActual_money(actualmoney);
            outLog.setStall_time(stall_time);
            outLog.setOut_user(MyApplication.settingInfo.getString("userName"));
            saveAndUpdateOutLog(outLog,picBuffer,true);
        }
        //更新收费信息
        if(mainActivity.outPortLog.getReceivable()>0) {
            Double money = Double.valueOf(MyApplication.settingInfo.getString("chargeMoney")) + mainActivity.outPortLog.getReceivable();
            MyApplication.settingInfo.putString("chargeMoney",String.format("%.2f",money));
            long chargeCarNum = MyApplication.settingInfo.getLong("chargeCarNumer") + 1;
            MyApplication.settingInfo.putLong("chargeCarNumer", chargeCarNum);
        }
        return true;
    }
    //保存免费出口车辆
    public boolean saveOutFreeCar(String carNumber,byte[] picBuffer){
        TrafficInfoTable inLog = null;
        outCamera.openGate();
        inLog =  new TrafficInfoTable();
        inLog.setCar_no(carNumber);
        inLog.setCar_type("临时车");
        inLog.setStall_time(-1);
        inLog.setIn_time(null);
        inLog.setIn_image(null);
        inLog.setReceivable(0.0);
        inLog.setActual_money(0.0);
        saveAndUpdateOutLog(inLog,picBuffer,false);
        return true;
    }
}
