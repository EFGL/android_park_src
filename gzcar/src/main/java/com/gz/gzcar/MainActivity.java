package com.gz.gzcar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gz.gzcar.Database.FreeInfoTable;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.device.camera;
import com.gz.gzcar.module.carInfoProcess;
import com.gz.gzcar.settings.SettingActivity;
import com.gz.gzcar.utils.FileUtils;
import com.gz.gzcar.utils.T;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.ex.DbException;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    public static FreeInfoTable chargeInfo = new FreeInfoTable();
    public static FileUtils picFileManage = new FileUtils();
    //摄像机IP
    static String inCameraIp = "192.168.10.202";
    static String outCameraIp = "192.168.10.203";
    //static camera inCamera = new camera("in", settingInfo.getString("inCameraIp"));
    // static camera outCamera = new camera("out", settingInfo.getString("outCameraIp"));
    static camera inCamera = new camera("in", inCameraIp);
    static camera outCamera = new camera("out", outCameraIp);
    //实始化车辆处理模块
    static carInfoProcess carProcess = new carInfoProcess(x.getDb(MyApplication.daoConfig), inCamera, outCamera);
    static TextView plateTextIn; //入口车牌
    static TextView plateTextOut;        //出口车牌
    static ImageView plateImageIn;       //入口图片
    static ImageView plateImageOut;      //出口图片
    static ImageView videoStreamIn;      //入口视频
    static ImageView videoStreamOut;     //出口视频
    static Button buttonManualPassIn;    //手动入场
//    static Button buttonManualPassOut;   //手动出场
    static Button buttonAgainIdentIn;    //入口重新识别
    static Button buttonAgainIdentOut;   //出口重新识别
    static Button buttonManualInOpen;    //入口手动起杆
    static Button getButtonManualOutOpen;//出口手动起杆
    static TextView chargeCarNumber;        //收费信息车号
    static TextView chargeCarType;          //收费信息车类型
    static TextView chargeParkTime;         //收费信息停车时长
    static TextView chargeMoney;            //收费信息收费金额
    static Button enterCharge;              //确认收费按钮
    static Context context;

    @Bind(R.id.main_setting)
    Button mainSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        //注册线程通讯
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        String type = getIntent().getStringExtra("type");
        if (type.equals("管理员")) {
            mainSetting.setVisibility(View.VISIBLE);
        } else if (type.equals("操作员")) {
            mainSetting.setVisibility(View.GONE);
        }

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
//        buttonManualPassOut = (Button) findViewById(R.id.button_manual_Pass_Out);
//        buttonManualPassOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                inCamera.manualPassOutFunc();
//            }
//        });
        buttonAgainIdentIn = (Button) findViewById(R.id.button_againIdent_In);
        buttonAgainIdentIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                againIdentInFunc();
            }
        });
        buttonAgainIdentOut = (Button) findViewById(R.id.button_againIdent_Out);
        buttonAgainIdentOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                againIdentOutFunc();
            }
        });
        buttonManualInOpen = (Button) findViewById(R.id.button_manual_Open_In);
        buttonManualInOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualInOpenFunc();
            }
        });
        getButtonManualOutOpen = (Button) findViewById(R.id.button_manual_Open_Out);
        getButtonManualOutOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualOutOpenFunc();
            }
        });

        enterCharge = (Button) findViewById(R.id.enterCharge);
        enterCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterChangeFunc();
            }
        });


    }

    //确认收费
    private void enterChangeFunc() {
        TrafficInfoTable trafficInfo = null;
        if (chargeCarNumber.getText().length() == 0) {
            T.showShort(context, "无可收费车辆");
            return;
        }
        outCamera.openGate();
        outCamera.playAudio(camera.AudioList.get("一路顺风"));
        try {
            //更新通行记录
            trafficInfo = MyApplication.db.selector(TrafficInfoTable.class).where("car_no", "=", chargeInfo.getCarNumber()).and("out_time", "=", null).findFirst();
            if (trafficInfo == null) {
                T.showShort(context, "该车无入场记录");
                return;
            } else {
                trafficInfo.setOut_time(chargeInfo.getOutTime());
                MyApplication.db.update(trafficInfo, "out_time");
            }
            MyApplication.db.update(trafficInfo, "out_time");
            //保存收费信息
            MyApplication.db.save(chargeInfo);
            T.showShort(context, "收费完成");
        } catch (DbException e) {
            e.printStackTrace();
        }
        //更新出口收费信息
        chargeCarNumber.setText("");
        chargeCarType.setText("");
        chargeParkTime.setText("");
        chargeMoney.setText("待通行");
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
                carInfoProcess.saveInNoPlateCar(picPath);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
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

    //入口手动起杆
    private void manualInOpenFunc() {
        T.showShort(context, "已完成确认通行");
        byte[] picBuffer = inCamera.CapturePic();
        if (picBuffer == null) {
            T.showShort(context, "拍照失败，请重新操作");
        } else {
            String picPath = picFileManage.savePicture(picBuffer);
            try {
                carInfoProcess.saveInTempCar(plateTextIn.getText().toString(), picPath);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        //拍照
        inCamera.CapturePic();
        //起杆
        inCamera.openGate();
        inCamera.playAudio("45");   //欢迎观临
        //显示
        // String[] dispInfo = new String[]{"车牌识别  一车一杆  减速慢行", "\\DL月\\DD日", "\\DH时\\DM分", "智能停车场"};
        String[] dispInfo = new String[]{"车牌识别  一车一杆  减速慢行", "手动通行", "欢迎观临", "智能停车场"};
        inCamera.ledDisplay(dispInfo);
        T.showShort(context, "入口手动起杆");
    }

    //出口手动起杆
    private void manualOutOpenFunc() {
        //拍照
        outCamera.CapturePic();
        //起杆
        outCamera.openGate();
        outCamera.playAudio("46");   //一路顺风
        //显示
        String[] dispInfo = new String[]{"车牌识别  一车一杆  减速慢行", "手动通行", "一路顺风", "智能停车场"};
        outCamera.ledDisplay(dispInfo);
        T.showShort(context, "出口手动起杆");
    }

//    public void setting(View view) {
//        startActivity(new Intent(this, SettingActivity.class));
//    }
//
//    public void query(View view) {
//        startActivity(new Intent(this, SrarchActivity.class));
//    }
//
//    public void exchange(View view) {
//        startActivity(new Intent(this, LoginActivity.class));
//    }


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

    public static Handler myHandler = new Handler() {
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
                        //入口处理
                        carProcess.processCarInFunc(info.getPlateNumber(), null);
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
                        //出口处理
                        if (carProcess.processCarOutFunc(info.getPlateNumber())) {
                            //更新出口收费信息
                            chargeCarNumber.setText(chargeInfo.getCarNumber());
                            chargeCarType.setText(chargeInfo.getType());
                            chargeParkTime.setText("停车：" + chargeInfo.getParkTime());
                            chargeMoney.setText(String.format("收费：%.1f元", chargeInfo.getMoney()));
                        }
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
                inCamera.manualPassOutFunc();
                startActivity(new Intent(this,SelectPassOut.class));
                break;
            case R.id.main_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.main_search:
                startActivity(new Intent(this, SrarchActivity.class));
                break;
            case R.id.main_change:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
