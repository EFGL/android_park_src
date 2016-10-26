package com.gz.gzcar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gz.gzcar.Database.MoneyTable;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.device.camera;
import com.gz.gzcar.module.carInfoProcess;
import com.gz.gzcar.server.DownLoadServer;
import com.gz.gzcar.server.SendService;
import com.gz.gzcar.settings.SettingActivity;
import com.gz.gzcar.utils.FileUtils;
import com.gz.gzcar.utils.SPUtils;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gz.gzcar.MyApplication.daoConfig;
import static com.gz.gzcar.MyApplication.settingInfo;

public class MainActivity extends BaseActivity {
    final  int ledDisplayDelay = 30*1000;
    DbManager db = x.getDb(daoConfig);
    public TrafficInfoTable outPortLog =  new TrafficInfoTable();
    public FileUtils picFileManage = new FileUtils();
    public String loginUserName;
    //摄像机IP
    camera inCamera = new camera(this,"in", settingInfo.getString("inCameraIp"));
    camera outCamera = new camera(this,"out", settingInfo.getString("outCameraIp"));
    //实始化车辆处理模块
    carInfoProcess carProcess = new carInfoProcess(db, inCamera, outCamera);
    TextView plateTextIn; //入口车牌
    TextView plateTextOut;        //出口车牌
    ImageView plateImageIn;       //入口图片
    ImageView plateImageOut;      //出口图片
    ImageView videoStreamIn;      //入口视频
    ImageView videoStreamOut;     //出口视频
    Button buttonManualPassIn;    //手动入场
    Button buttonAgainIdentIn;    //入口重新识别
    Button buttonAgainIdentOut;   //出口重新识别
    Button buttonManualInOpen;    //入口手动起杆
    Button ButtonManualOutOpen;//选车出场
    public  TextView chargeCarNumber;        //收费信息车号
    public  TextView chargeCarType;          //收费信息车类型
    TextView chargeParkTime;         //收费信息停车时长
    TextView chargeMoney;            //收费信息收费金额
    Button enterCharge;              //确认收费按钮
    Context context;

    //状态信息
    TextView textViewAllPlace;       //总车位
    TextView textViewEmptyPlace;     //空闲车位
    TextView textViewInCarCount;     //入场数量
    TextView textViewOutCarCount;    //出场数量
    //当班信息
    TextView textViewUserName;       //操作员
    TextView textViewLoginTime;      //登录长
    TextView textViewSumCar;         //当前班费车辆
    TextView textViewSumMoney;       //当班收费金额

    @Bind(R.id.main_setting)
    Button mainSetting;

    private AlertDialog dialog;
    private byte[] outPortPicBuffer;

    private Timer inLedTimer;
    private Timer outLedTimer;
    //入口更新显示定时器超时任务
    TimerTask inLedTimerTask = new TimerTask() {
        @Override
        public void run() {
            String[] dispInfo = new String[]{null, null,null,null};
            //设定总车位
            long value = MyApplication.settingInfo.getLong("allCarPlace");
            //设定空闲车位
            try {
                value = value - db.selector(TrafficInfoTable.class).where("status", "=", "已入").count();
            } catch (DbException e) {
                e.printStackTrace();
            }
            //初始化显示屏内容
            dispInfo[0] = "空位:" + value;
            dispInfo[1] = "欢迎光临";
            dispInfo[2] =  "\\DH时\\DM分";
            dispInfo[3] = "车牌识别 一车一杆 减速慢行";
            //显示
            inCamera.ledDisplay(dispInfo);
        }
    };
    //出口更新显示定时器超时任务
    TimerTask outLedTimerTask = new TimerTask() {
        @Override
        public void run() {
            String[] dispInfo = new String[]{null, null,null,null};
            //设定总车位
            long value = MyApplication.settingInfo.getLong("allCarPlace");
            //设定空闲车位
            try {
                value = value - db.selector(TrafficInfoTable.class).where("status", "=", "已入").count();
            } catch (DbException e) {
                e.printStackTrace();
            }
            //初始化显示屏内容
            dispInfo[0] = "空位:" + value;
            dispInfo[1] = "欢迎光临";
            dispInfo[2] =  "\\DH时\\DM分";
            dispInfo[3] = "车牌识别 一车一杆 减速慢行";
            //显示
            outCamera.ledDisplay(dispInfo);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inLedTimer = new Timer();
        inLedTimer.schedule(inLedTimerTask,1000);
        outLedTimer = new Timer();
        outLedTimer.schedule(outLedTimerTask,1000);
        setContentView(R.layout.activity_main);
        initLogin();
        context = MainActivity.this;
        //注册线程通讯
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        String type = getIntent().getStringExtra("type");
        plateTextIn = (TextView) findViewById(R.id.textView_PlateIn);
        plateTextOut = (TextView) findViewById(R.id.textView_PlateOut);
        plateImageIn = (ImageView) findViewById(R.id.imageView_PicPlateIn);
        plateImageOut = (ImageView) findViewById(R.id.imageView_PicPlateOut);
        videoStreamIn = (ImageView) findViewById(R.id.imageView_videoPlateIn);
        videoStreamOut = (ImageView) findViewById(R.id.imageView_videoPlateOut);
        chargeCarNumber = (TextView) findViewById(R.id.chargeCarNumber);
        chargeCarType = (TextView) findViewById(R.id.chargeCarType);
        chargeParkTime = (TextView) findViewById(R.id.chargeParkTime);
        chargeMoney = (TextView) findViewById(R.id.chargeMoney);

        buttonManualPassIn = (Button) findViewById(R.id.button_manual_Pass_In);
        buttonManualPassIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualPassInFunc();
            }
        });
        buttonAgainIdentIn = (Button) findViewById(R.id.button_againIdent_In);
        buttonAgainIdentIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                againIdentInFunc();
            }
        });
        buttonManualInOpen = (Button) findViewById(R.id.button_manual_Open_In);
        buttonManualInOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualInOpenFunc();
            }
        });

        buttonAgainIdentOut = (Button) findViewById(R.id.button_againIdent_Out);
        buttonAgainIdentOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                againIdentOutFunc();
            }
        });
        ButtonManualOutOpen = (Button) findViewById(R.id.button_manual_Open_Out);
        ButtonManualOutOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualOutOpenFunc();
            }
        });

        //状态信息
         textViewAllPlace = (TextView) findViewById(R.id.textViewAllPlace);       //总车位
         textViewEmptyPlace = (TextView) findViewById(R.id.textViewEmptyPlace);     //空闲车位
         textViewInCarCount = (TextView) findViewById(R.id.textViewInCarCount);     //入场数量
         textViewOutCarCount = (TextView) findViewById(R.id.textViewOutCarCount);    //出场数量
        //当班信息
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);       //操作员
        textViewLoginTime = (TextView) findViewById(R.id.textViewloginTime);      //登录长
        textViewSumCar = (TextView) findViewById(R.id.textViewSumCar);         //当前班费车辆
        textViewSumMoney = (TextView) findViewById(R.id.textViewSumMoney);       //当班收费金额

        enterCharge = (Button) findViewById(R.id.enterCharge);
        enterCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterChangeFunc();
            }
        });

        outPortLog.setReceivable(0.0);
        outPortLog.setCar_no("");
        outPortLog.setCar_type("");
        outPortLog.setStall_time("待通行");
        showLogin();
        startmyserver();
    }

    /**
     * 启动我的服务
     */
    public void startmyserver(){
       // Intent intent=new Intent(MainActivity.this,SendService.class);
       // startService(intent);
        Intent intentDon=new Intent(MainActivity.this,DownLoadServer.class);
        startService(intentDon);
    }

    private void initLogin() {
        makeUser();
        if (MyApplication.settingInfo == null) {
            MyApplication.settingInfo = new SPUtils(MainActivity.this, "config");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<MoneyTable> all = null;
        try {
            all = db.findAll(MoneyTable.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (all == null || all.size() < 1)
            addMoneyBaseData();
    }

    // 生成收费表
    private void addMoneyBaseData() {
        MoneyTable m;
        for (int i = 0; i < 48; i++) {
            m = new MoneyTable();
            m.setFee_code(String.valueOf(i+1));
            m.setFee_detail_code(null);
            m.setMoney(i/2 +1);
            m.setFee_name(String.format("固定车",i*0.5,(double)(i*0.5)+0.5));
            m.setParked_min_time(i*30);
            m.setParked_max_time((i+1)*30);
            try {
                db.save(m);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    // 生成管理员基本帐号
    private void makeUser() {
        try {
            List<UserTable> all = db.findAll(UserTable.class);
            if (all == null || all.size() < 1) {
                UserTable user = new UserTable();
                user.setUserName("管理员");
                user.setPassword("123456p");
                user.setType("管理员");
                db.save(user);
                user.setUserName("操作员");
                user.setPassword("123");
                user.setType("操作员");
                db.save(user);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void showLogin() {
        View view = LayoutInflater.from(this).inflate(R.layout.login_diglog, null);
        dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 500;
        params.height = 400;
        dialog.getWindow().setAttributes(params);
        final MyPullText mUser = (MyPullText) view.findViewById(R.id.login_user);
        final EditText mPasswordView = (EditText) view.findViewById(R.id.login_password);
        Button login = (Button) view.findViewById(R.id.login_sign_in_button);
        try {
            ArrayList<String> userName = new ArrayList<>();
            List<UserTable> all = db.findAll(UserTable.class);
            Log.e("ende", "all.size==" + all.size());
            if (all != null) {
                for (int i = 0; i < all.size(); i++) {
                    userName.add(all.get(i).getUserName());
                }
                mUser.setPopList(userName);
                Log.e("ende", "userName.size==" + userName.size());
                if (userName.size() > 0) {
                    mUser.setText(userName.get(0));
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = mUser.getText().toString();
                String password = mPasswordView.getText().toString();
                if (TextUtils.isEmpty(user)) {
                    T.showShort(MainActivity.this, "请选择用户名");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    T.showShort(MainActivity.this, "密码不能为空");
                    return;
                }
                match(user, password);
            }
        });
    }

    private void match(String userName, String password) {
        try {
            List<UserTable> all = db.selector(UserTable.class).where("userName", "=", userName).and("password", "=", password).findAll();
            if (all.size() > 0) {
                String type = all.get(0).getType();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = dateFormat.format(new Date());
                try {
                    Date nowStartDate = dateFormat.parse(dateStr);
                    long count = db.selector(TrafficInfoTable.class).where("in_time", ">=", nowStartDate).count();
                    MyApplication.settingInfo.putLong("inCarCount", count);
                    count = db.selector(TrafficInfoTable.class).where("out_time", ">=", nowStartDate).count();
                    MyApplication.settingInfo.putLong("inCarCount", count);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                loginUserName = userName;
                //上次非本用户或用户退出,则清空数据
                userName = MyApplication.settingInfo.getString("userName");
                MyApplication.settingInfo.putString("userName", userName);
                if (!MyApplication.settingInfo.getBoolean("loginStatus")   || !userName.equals(loginUserName)) {
                        MyApplication.settingInfo.putString("userName",loginUserName);
                        MyApplication.settingInfo.putBoolean("loginStatus", true);
                        MyApplication.settingInfo.putLong("inCarCount", 0);
                        MyApplication.settingInfo.putLong("outCarCount", 0);
                        MyApplication.settingInfo.putLong("chargeCarNumer", 0);
                        MyApplication.settingInfo.putString("chargeMoney", "0.00");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        MyApplication.settingInfo.putString("loginTime", format.format(new Date()));
                }
                this.upStatusInfoDisp();
                //进入主页面
                if (type.equals("管理员")) {
                    mainSetting.setVisibility(View.VISIBLE);
                } else if (type.equals("操作员")) {
                    mainSetting.setVisibility(View.GONE);
                }
                if (dialog != null) {

                    dialog.dismiss();
                }
            } else {
                T.showShort(this, "用户名或密码错误");
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    //更新状态信息
    private void upStatusInfoDisp() {
        //设定总车位
        long value = MyApplication.settingInfo.getLong("allCarPlace");
        textViewAllPlace.setText(String.format("总车位：%d个", value));
        //设定空闲车位
        try {
            value = value - db.selector(TrafficInfoTable.class).where("status", "=", "已入").count();
        } catch (DbException e) {
            e.printStackTrace();
        }
        textViewEmptyPlace.setText(String.format("空闲车位：%d个", value));
        value = MyApplication.settingInfo.getLong("inCarCount");
        textViewInCarCount.setText(String.format("当班入场：%d车次", value));
        value = MyApplication.settingInfo.getLong("outCarCount");
        textViewOutCarCount.setText(String.format("当班出场：%d车次", value));
        String stringValue = MyApplication.settingInfo.getString("userName");
        textViewUserName.setText("操作员：" + stringValue);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date loginTime = format.parse(MyApplication.settingInfo.getString("loginTime"));
            long loginTimeMinute = (new Date().getTime() - loginTime.getTime()) / 60 / 1000;
            stringValue = String.format("登陆：%d天%d小时%d分钟", loginTimeMinute / (24 * 60), (loginTimeMinute % 24) / 60, loginTimeMinute % 60);
            textViewLoginTime.setText(stringValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long chargeNum = MyApplication.settingInfo.getLong("chargeCarNumer");
        stringValue = String.format("收费车辆：%d辆",chargeNum);
        textViewSumCar.setText(stringValue);
        stringValue = String.format("收费金额：" + MyApplication.settingInfo.getString("chargeMoney") + "元");
        textViewSumMoney.setText(stringValue);
    }

    //确认收费
    private void enterChangeFunc() {
        String ParkTime = chargeParkTime.getText().toString();
        if (ParkTime.indexOf("无入场记录") > 0 ) {
            T.showShort(context, "无可收费车辆");
            //更新出口收费信息
            chargeCarNumber.setText("");
            chargeCarType.setText("");
            chargeParkTime.setText("");
            chargeMoney.setText("待通行");
            return;
        }
        //如开启0元收费自动放行，则点本按钮无效
        if(outPortLog.getReceivable() == 0) {
            boolean tempCarFree = MyApplication.settingInfo.getBoolean("tempCarFree");
            if (!tempCarFree) {
               T.showShort(context,"该车无需收费，已放行！");
            }
            else{
                T.showShort(context, "无可收费车辆");
            }
            //更新出口收费信息
            chargeCarNumber.setText("");
            chargeCarType.setText("");
            chargeParkTime.setText("");
            chargeMoney.setText("待通行");
            upStatusInfoDisp();
            return ;
        }
        if (carProcess.saveOutTempCar(outPortPicBuffer)) {
            outCamera.playAudio(camera.AudioList.get("一路顺风"));
        }
        T.showShort(context, "收费完成");
        //更新出口收费信息
        chargeCarNumber.setText("");
        chargeCarType.setText("");
        chargeParkTime.setText("");
        chargeMoney.setText("待通行");
        upStatusInfoDisp();
    }

    //无牌入场
    private void manualPassInFunc() {
        T.showShort(context, "已完成无牌入场");
        byte[] picBuffer = inCamera.CapturePic();
        if (picBuffer == null) {
            T.showShort(context, "拍照失败，请重新操作");
        } else {
            String picPath = picFileManage.savePicture(picBuffer);
            try {
                carProcess.saveInNoPlateCar(picPath);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        inLedTimer.cancel();
        inLedTimer = new Timer();
        inLedTimer.schedule(inLedTimerTask,10000);
        upStatusInfoDisp();
    }

    //重新识别入场
    private void againIdentInFunc() {
        T.showShort(context, "入口重新识别中......");
        inCamera.againIdent();

    }

    //重新识别出场
    private void againIdentOutFunc() {
        T.showShort(context, "出口重新识别中......");
        outCamera.againIdent();

    }

    //入口确认起杆
    private void manualInOpenFunc() {
        T.showShort(context, "已完成确认通行");
        byte[] picBuffer = inCamera.CapturePic();
        if (picBuffer == null) {
            T.showShort(context, "拍照失败，请重新操作");
        } else {
            try {
                if (plateTextIn.getText().toString().contentEquals("待通行")) {
                    T.showShort(context, "无可确认通行车辆");
                    return;
                }
                inCamera.playAudio(camera.AudioList.get("欢迎光临"));
                carProcess.saveInTempCar(plateTextIn.getText().toString(), picBuffer);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        plateTextIn.setText("待通行");
        T.showShort(context, "已完成确认通行");
        upStatusInfoDisp();
    }

    //出口手免费通行
    private void manualOutOpenFunc() {
        T.showShort(context, "免费通行");
        String ParkTime = chargeParkTime.getText().toString().toString();
        if (ParkTime.indexOf("无入场记录") > 0 || chargeCarNumber.getText().length() == 0) {
            //拍照
            byte[] picBuffer = outCamera.CapturePic();
            carProcess.saveOutFreeCar(picBuffer);
            outCamera.playAudio(camera.AudioList.get("一路顺风"));
        } else {
            outPortLog.setReceivable(0.0);
            outPortLog.setCar_type("免费车");
            carProcess.saveOutTempCar(outPortPicBuffer);
            outCamera.playAudio(camera.AudioList.get("一路顺风"));
            T.showShort(context, "已放行");
        }
        //更新出口收费信息
        chargeCarNumber.setText("");
        chargeCarType.setText("");
        chargeParkTime.setText("");
        chargeMoney.setText("待通行");
        upStatusInfoDisp();
    }
    @Override
    public void onBackPressed() {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.content("确认退出?")//
//                .contentTextColor()
                .style(NormalDialog.STYLE_TWO)//
                .title("提示")
//                .titleTextColor(Color.RED)
                .titleTextSize(23)//
                .btnText("取消", "确认")//
                .btnTextColor(Color.parseColor("#383838"), Color.parseColor("#D4D4D4"))//
                .btnTextSize(16f, 16f)//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
//                .widthScale(0.5f)
                .heightScale(0.5f)
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                        finish();
                    }
                });
    }

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            camera.PlateInfo info = (camera.PlateInfo) msg.obj;
            Bitmap bmp = BitmapFactory.decodeByteArray(info.getCarPicdata(), 0, info.getCarPicdata().length);
            switch (info.msgType) {
                case PLATE:
                    Log.i("log", "event:" + info.msgType + info.getPlateNumber());
                    //设置显示入口车号和图片
                    if (info.getName().equals("in")) {
                        plateTextIn.setText(info.getPlateNumber());
                        if (info.getPlateColor().equals("黄色")) {
                            plateTextIn.setBackgroundColor(Color.YELLOW);
                        } else {
                            plateTextIn.setBackgroundColor(Color.BLUE);
                        }
                        plateImageIn.setImageBitmap(bmp);
                        plateImageIn.invalidate();
                    }
                    //设置显示出口车号和图片
                    if (info.getName().equals("out")) {
                        plateTextOut.setText(info.getPlateNumber());
                        if (info.getPlateColor().equals("黄色")) {
                            plateTextIn.setBackgroundColor(Color.YELLOW);
                        } else {
                            plateTextIn.setBackgroundColor(Color.BLUE);
                        }
                        plateImageOut.setImageBitmap(bmp);
                        plateImageOut.invalidate();
                        //缓存出口图片
                        outPortPicBuffer = info.getCarPicdata();
                    }
                    //查询最近通行记录，如果通行时间小于设定时间则禁止通行，防止重复识别
                    try {
                        TrafficInfoTable log = db.selector(TrafficInfoTable.class).where("car_no", "=", info.getPlateNumber()).orderBy("update_time",true).findFirst();
                        if(log != null ) {
                            long delay = new Date().getTime() - log.getUpdateTime().getTime();
                            if(delay < MyApplication.settingInfo.getInt("enterDelay")*60*1000) {
                                if(delay>5*1000){
                                T.showShort(context, "该车出频繁，请稍后通行");}
                                return;
                            }
                        }
                        if (info.getName().equals("in")) {
                            //入口处理
                            carProcess.processCarInFunc(info.getPlateNumber(), info.getCarPicdata());
                        }else if (info.getName().equals("out")) {
                            //出口处理
                            if (carProcess.processCarOutFunc(info.getPlateNumber(), info.getCarPicdata())) {
                                //更新出口收费信息
                                chargeCarNumber.setText(outPortLog.getCar_no());
                                chargeCarType.setText(outPortLog.getCar_type());
                                chargeParkTime.setText("停车：" + outPortLog.getStall_time());
                                chargeMoney.setText(String.format("收费：%.1f元", outPortLog.getReceivable()));
                            }
                        }
                        upStatusInfoDisp();
                        return;
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                case PIC:
                    Log.i("log", info.getPlateNumber());
                    //手动起杆捕捉图片
                    if (info.getName().equals("in")) {
                        plateTextIn.setText(info.getPlateNumber());
                        plateTextIn.setBackgroundColor(Color.BLUE);
                        plateImageIn.setImageBitmap(bmp);
                        plateImageIn.invalidate();
                    }
                    //设置显示出口车号和图片
                    if (info.getName().equals("out")) {
                        plateTextOut.setText(info.getPlateNumber());
                        plateTextIn.setBackgroundColor(Color.BLUE);
                        plateImageOut.setImageBitmap(bmp);
                        plateImageOut.invalidate();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(camera.PlateInfo info) {
        //解码车辆抓拍图片
        Bitmap bmp = BitmapFactory.decodeByteArray(info.getCarPicdata(), 0, info.getCarPicdata().length);
        switch (info.msgType) {
            case STREAM:
                //设置显示入口视频
                if (info.getName().equals("in")) {
                    videoStreamIn.setImageBitmap(bmp);
                    videoStreamIn.invalidate();
                }
                //设置显示出口视频
                if (info.getName().equals("out")) {
                    videoStreamOut.setImageBitmap(bmp);
                    videoStreamOut.invalidate();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.button_manual_Pass_Out, R.id.main_setting, R.id.main_search, R.id.main_change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_manual_Pass_Out://选车出场
                Intent intent = new Intent(this, SelectPassOut.class);
                startActivityForResult(intent, 101);
                outCamera.againIdent();
                break;
            case R.id.main_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.main_search:
                startActivity(new Intent(this, SrarchActivity.class));
                break;
            case R.id.main_change:
                ask();
                break;
        }
    }

    private void ask() {
        View view = LayoutInflater.from(this).inflate(R.layout.ask_diglog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCancelable(true);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 500;
        params.height = 400;
        dialog.getWindow().setAttributes(params);
        Button cencle = (Button) view.findViewById(R.id.ask_cencle);
        Button ok = (Button) view.findViewById(R.id.ask_ok);

        cencle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //退出
                settingInfo.putBoolean("loginStatus", false);
                showLogin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("log", "requestCode:" + requestCode + "   resultCode:" + resultCode);
        switch (resultCode) {
            case 1:
                int id = data.getIntExtra("id", -1);
                if (id >= 0) {
                    //byte[] picBuffer = outCamera.CapturePic();
                    carProcess.processManualSelectOut(id, outPortPicBuffer);
                    //更新出口收费信息
                    chargeCarNumber.setText(outPortLog.getCar_no());
                    chargeCarType.setText(outPortLog.getCar_type());
                    chargeParkTime.setText("停车：" + outPortLog.getStall_time());
                    chargeMoney.setText(String.format("收费：%.1f元", outPortLog.getReceivable()));
                }
                break;
            default:
                break;
        }
    }
}
