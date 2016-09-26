package com.gz.gzcar;

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
import com.gz.gzcar.device.LedModule;
import com.gz.gzcar.device.camera;
import com.gz.gzcar.settings.SettingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    //摄像机IP
    String inCameraIp = "192.168.10.203";
    String outCameraIp = "192.168.10.202";
    camera inCamera = new camera("in", inCameraIp);
    camera outCamera = new camera("out", outCameraIp);
    static TextView plateTextIn;         //入口车牌
    static TextView plateTextOut;        //出口车牌
    static ImageView plateImageIn;       //入口图片
    static ImageView plateImageOut;      //出口图片
    static ImageView videoStreamIn;      //入口视频
    static ImageView videoStreamOut;     //出口视频
    static Button buttonManualPassIn;    //手动入场
    static Button buttonManualPassOut;   //手动出场
    static Button buttonAgainIdentIn;    //入口重新识别
    static Button buttonAgainIdentOut;   //出口重新识别
    static Button buttonManualInOpen;    //入口手动起杆
    static Button getButtonManualOutOpen;//出口手动起杆
    @Bind(R.id.main_setting)
    Button mainSetting;
    @Bind(R.id.main_query)
    Button mainQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        buttonManualPassIn = (Button) findViewById(R.id.button_manual_Pass_In);
        buttonManualPassIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualPassInFunc();
            }
        });
        buttonManualPassOut = (Button) findViewById(R.id.button_manual_Pass_Out);
        buttonManualPassOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inCamera.manualPassOutFunc();
            }
        });
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
                ;
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
    }

    //手动入场
    private void manualPassInFunc() {
        Log.i("button:", "manualPassInFunc" + LedModule.DeviceTypeMap.size());
    }

    //重新识别入场
    private void againIdentInFunc() {
        Log.i("button:", "againIdentInFunc");
        inCamera.againIdent();
    }

    //重新识别出场
    private void againIdentOutFunc() {
        Log.i("button:", "againIdentOutFunc");
        outCamera.againIdent();
    }

    //入口手动起杆
    private void manualInOpenFunc() {
        //拍照
        inCamera.CapturePic();
        //起杆
        inCamera.openGate();
        inCamera.playAudio("45");   //欢迎观临
        //显示
        String[] dispInfo = new String[]{"车牌识别  一车一杆  减速慢行", "\\DL月\\DD日", "\\DH时\\DM分", "智能停车场"};
        inCamera.ledDisplay(dispInfo);
    }

    //出口手动起杆
    private void manualOutOpenFunc() {
        //拍照
        outCamera.CapturePic();
        //起杆
        outCamera.openGate();
        outCamera.playAudio("46");   //一路顺风
        //显示
        String[] dispInfo = new String[]{"车牌识别  一车一杆  减速慢行", "\\DL月\\DD日", "\\DH时\\DM分", "智能停车场"};
        outCamera.ledDisplay(dispInfo);
    }

    public static Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            //得到车辆识别信息
            camera.PlateInfo info = (camera.PlateInfo) msg.obj;
            //解码车辆抓拍图片
            Bitmap bmp = BitmapFactory.decodeByteArray(info.getCarPicdata(), 0, info.getCarPicdata().length);
            switch (msg.what) {
                case 0:
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
                    }
                    break;
                case 1:
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
                case 2:
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
           /* if(!bmp.isRecycled()){
            bmp.recycle();
            }*/
            super.handleMessage(msg);
        }
    };

    public void setting(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }

    public void query(View view) {
        startActivity(new Intent(this, SrarchActivity.class));
    }

    public void exchange(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    public void onBackPressed() {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.content("是否确认退出程序?")//
//                .contentTextColor()
                .style(NormalDialog.STYLE_TWO)//
                .title("温馨提示")
//                .titleTextColor(Color.RED)
                .titleTextSize(23)//
                .btnText("继续看看", "残忍退出")//
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
}
