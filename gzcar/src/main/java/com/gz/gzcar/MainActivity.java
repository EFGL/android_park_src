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
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.FreeInfoTable;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.device.camera;
import com.gz.gzcar.module.carInfoProcess;
import com.gz.gzcar.settings.SettingActivity;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.FileUtils;
import com.gz.gzcar.utils.SPUtils;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gz.gzcar.MyApplication.daoConfig;

public class MainActivity extends BaseActivity {

    public static FreeInfoTable chargeInfo = new FreeInfoTable();
    public static FileUtils picFileManage = new FileUtils();
    //摄像机IP
    static camera inCamera = new camera("in", MyApplication.settingInfo.getString("inCameraIp"));
    static camera outCamera = new camera("out", MyApplication.settingInfo.getString("outCameraIp"));
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

    private DbManager db = x.getDb(daoConfig);
    private SPUtils spUtils;
    private boolean first;
    private AlertDialog dialog;
    private static byte[] inPortPicBuffer;
    private static byte[] outPortPicBuffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLogin();
        context = MainActivity.this;
        //注册线程通讯
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        String type = getIntent().getStringExtra("type");
//        if (type.equals("管理员")) {
//            mainSetting.setVisibility(View.VISIBLE);
//        } else if (type.equals("操作员")) {
//            mainSetting.setVisibility(View.GONE);
//        }

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

        showLogin();

    }

    private void initLogin() {

        makeUser();

        new Thread(){
            @Override
            public void run() {
                super.run();
                if (!first) {
                    get_info_vehicles("1", "NULL", "first_down_car");
                } else {
                    get_info_vehicles("1", "NULL", "info_vehicles");
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (spUtils == null) {
                    spUtils = new SPUtils(MainActivity.this, "config");
                }
                first = spUtils.getBoolean("first");
                spUtils.putString("serverIp", "http://221.204.11.69:3002/");// 服务器地址url
                spUtils.putString("inCameraIp", "192.168.10.203");// 入口相机地址
                spUtils.putString("outCameraIp", "192.168.10.202");// 出口相机地址
            }
        }.start();
    }

    // 生成管理员基本帐号
    private void makeUser() {
        try {
            List<UserTable> all = db.findAll(UserTable.class);
            if (all == null || all.size() < 1) {

                UserTable user = new UserTable();
                user.setUserName("123");
                user.setPassword("123");
                user.setType("管理员");
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
//        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);

        final MyPullText mUser = (MyPullText) view.findViewById(R.id.login_user);
        final EditText mPasswordView = (EditText) view.findViewById(R.id.login_password);
        Button cencle = (Button) view.findViewById(R.id.login_cencle_button);
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

        cencle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = mUser.getText().toString();
                String password = mPasswordView.getText().toString();
                if (TextUtils.isEmpty(user)) {
                    T.showShort(MainActivity.this, "请选择用户名");
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
                T.showShort(this, "count==" + all.size());
                String type = all.get(0).getType();

//                进入主页面
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

    /**
     * 硬件终端获取固定车信息
     *
     * @param controller_sn
     * @param id
     */
    public void get_info_vehicles(final String controller_sn, String id, final String url) {
//        RequestParams params=new RequestParams(MyApplication.Baseurl+"first_down_car");
//        RequestParams params=new RequestParams(MyApplication.Baseurl+"info_vehicles");
        RequestParams params = new RequestParams(MyApplication.Baseurl + url);
        params.addBodyParameter("controller_sn", controller_sn);
        params.addBodyParameter("id", id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String result) {
                if (url.equals("first_down_car")) {
                    try {
                        JSONObject ret = new JSONObject(result);
                        int ref = ret.getInt("ref");
                        if (ref == 0) {
                            Log.e("ende", "first onSuccess：result==" + result);
                            // 保存sp
                            spUtils.putBoolean("first", true);
                            Log.e("ende", "sp保存成功");
                            get_info_vehicles("1", "NULL", "info_vehicles");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                /**
                 * 成功：[{"id":"P0401030001600001129","car_no":"晋AGL202","car_type":"长期固定车","card_no":null,"label_no":null,"person_name":"浦院","person_sex":null,"person_tel":null,"person_address":null,"person_idcard":null,"car_color":null,"stop_date":"3036-01-01T00:00:00.000+08:00","start_date":"2016-01-01T00:00:00.000+08:00","carimage":null,"fee_flag":"1","created_at":"2016-05-18T11:28:04.000+08:00","updated_at":"2016-09-22T15:06:59.000+08:00","status":"作废","door1":"0","door2":"0","door3":"0","door4":"0","door5":"0","door6":"0","door7":"0","door8":"0","garage_code":"040103000","park_code":"04010300","note":null}]
                 */
                else if (result.length() != 2 && url.equals("info_vehicles")) {
                    try {
                        Log.e("ende", "info onSuccess：result==" + result);
                        JSONArray array = new JSONArray(result);
                        JSONObject object = new JSONObject(array.get(0).toString());
                        String id = object.getString("id");
                        String car_no = object.getString("car_no");
                        String car_type = object.getString("car_type");
                        String card_no = object.getString("card_no");
                        String label_no = object.getString("label_no");
                        String person_name = object.getString("person_name");
                        String person_sex = object.getString("person_sex");
                        String person_tel = object.getString("person_tel");
                        String person_address = object.getString("person_address");
                        String person_idcard = object.getString("person_idcard");
                        String car_color = object.getString("car_color");
                        String stop_date = object.getString("stop_date");
                        String start_date = object.getString("start_date");
                        String carimage = object.getString("carimage");
                        String fee_flag = object.getString("fee_flag");
                        String created_at = object.getString("created_at");
                        String updated_at = object.getString("updated_at");
                        String status = object.getString("status");
                        String garage_code = object.getString("garage_code");
                        String park_code = object.getString("park_code");
                        String note = object.getString("note");
                        //开始保存数据
                        try {
                            CarInfoTable mInfo = new CarInfoTable();
                            mInfo.setCarId(id);
                            mInfo.setCar_no(car_no);
                            mInfo.setCar_type(car_type);
                            mInfo.setCard_no(card_no);
                            mInfo.setLabel_no(label_no);
                            mInfo.setPerson_name(person_name);
                            mInfo.setPerson_sex(person_sex);
                            mInfo.setPerson_tel(person_tel);
                            mInfo.setPerson_address(person_address);
                            mInfo.setPerson_idcard(person_idcard);
                            mInfo.setCar_color(car_color);
                            mInfo.setStop_date(DateUtils.string2Date(DateUtils.change(stop_date)));
                            mInfo.setStart_date(DateUtils.string2Date(DateUtils.change(start_date)));
                            mInfo.setCar_image(carimage);
                            mInfo.setFee_flag(fee_flag);
                            mInfo.setCreated_at(DateUtils.string2Date(DateUtils.change(created_at)));
                            mInfo.setUpdated_at(updated_at);
                            mInfo.setStatus(status);
                            mInfo.setGarage_code(garage_code);
                            mInfo.setPark_code(park_code);
                            mInfo.setNote(note);
                            T.showShort(MainActivity.this, "stop_data==" + DateUtils.change(stop_date));

                            db.save(mInfo);
                            T.showShort(MainActivity.this, "增加成功");

                        } catch (DbException e) {
                            T.showShort(MainActivity.this, "新增异常");
                            Log.e("ende","DbException-----"+e.toString());
                        }

                        //保存完后，继续请求数据，递归
                        get_info_vehicles(controller_sn, id, "info_vehicles");
                    } catch (JSONException e) {
                        Log.e("ende","JSONException-----"+e.toString());
                    }
                } else if (result.length() == 2) {
                    Log.e("ende", "info 22222");
                    Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //确认收费
    private void enterChangeFunc() {

        if (chargeCarNumber.getText().length() == 0) {
            T.showShort(context, "无可收费车辆");
            return;
        }
        if(carInfoProcess.saveOutTempCar(outPortPicBuffer))
        {
            T.showShort(context, "收费完成");
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
        byte[] picBuffer = inCamera.CapturePic();
        if (picBuffer == null) {
            T.showShort(context, "拍照失败，请重新操作");
        } else {
            try {
                carInfoProcess.saveInTempCar(plateTextIn.getText().toString(),picBuffer);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        T.showShort(context, "已完成确认通行");
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
                        //缓存入口图片
                        inPortPicBuffer = info.getCarPicdata();
                        //入口处理
                        carProcess.processCarInFunc(info.getPlateNumber(), info.getCarPicdata());
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
                        //出口处理
                        if (carProcess.processCarOutFunc(info.getPlateNumber(),info.getCarPicdata())) {
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
                startActivity(new Intent(this, SelectPassOut.class));
                break;
            case R.id.main_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.main_search:
                startActivity(new Intent(this, SrarchActivity.class));
                break;
            case R.id.main_change:
                showLogin();
//                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
