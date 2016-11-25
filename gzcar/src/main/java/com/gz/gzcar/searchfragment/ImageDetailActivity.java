package com.gz.gzcar.searchfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.L;
import com.gz.gzcar.utils.PrintBean;
import com.gz.gzcar.utils.PrintUtils;
import com.gz.gzcar.utils.T;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageDetailActivity extends BaseActivity {

    @Bind(R.id.in_image)
    ImageView inImage;
    @Bind(R.id.out_image)
    ImageView outImage;
    @Bind(R.id.car_num)
    TextView carNum;
    @Bind(R.id.car_type)
    TextView mCarType;
    @Bind(R.id.in_user)
    TextView mInUser;
    @Bind(R.id.out_user)
    TextView mOutUser;
    @Bind(R.id.use_carwei)
    TextView mUseCarwei;
    @Bind(R.id.state)
    TextView mState;
    @Bind(R.id.y_money)
    TextView myMoney;
    @Bind(R.id.s_money)
    TextView msMoney;
    @Bind(R.id.in_time)
    TextView mInTime;
    @Bind(R.id.out_time)
    TextView mOutTime;
    @Bind(R.id.park_time)
    TextView mParkTime;
    @Bind(R.id.update_time)
    TextView mUpdateTime;
    @Bind(R.id.relative_print)
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", -1);
        if (tag > 0) {
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
        String in_image = intent.getStringExtra("in_image");
        String out_image = intent.getStringExtra("out_image");

        final String carNumber = intent.getStringExtra("carNumber");
        final String carType = intent.getStringExtra("carType");
        String stall = intent.getStringExtra("stall");//占用车位
        String status = intent.getStringExtra("status");
        final String out_user = intent.getStringExtra("out_user");
        String in_user = intent.getStringExtra("in_user");
        String receivable = intent.getStringExtra("receivable");//应收费用
        final String actual_money = intent.getStringExtra("actual_money");
        final String in_time = intent.getStringExtra("in_time");
        final String out_time = intent.getStringExtra("out_time");
        final String stall_time = intent.getStringExtra("stall_time");//停车时长
        String update_time = intent.getStringExtra("update_time");


        loadImage(in_image, out_image);
        carNum.setText("车牌号:" + carNumber);
        mCarType.setText("车类型:" + carType);
        mUseCarwei.setText("占用车位:" + stall);
        mState.setText("通行状态:" + status);
        mOutUser.setText("出场操作员:" + out_user);
        mInUser.setText("入场操作员:" + in_user);
        myMoney.setText("应收费用:" + receivable);
        msMoney.setText("实收费用:" + actual_money);
        mInTime.setText("入场时间:" + in_time);
        mOutTime.setText("出场时间:" + out_time);
        mParkTime.setText("停车时长:" + stall_time);
        mUpdateTime.setText("更新时间:" + update_time);

        Button btnPrint = (Button) findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPrint = MyApplication.settingInfo.getBoolean("isPrintCard");
                L.showlogError("是否打印:" + isPrint);
                if (isPrint) {
                    Gson gson = new Gson();
                    PrintBean printBean = new PrintBean();
                    printBean.carNumber = carNumber;
                    printBean.inTime = in_time;
                    printBean.outTime = out_time;
                    printBean.type = carType;

                    if (actual_money.equals("未出场"))
                        printBean.money = 0.00;
                    else
                        printBean.money = Double.parseDouble(actual_money);

                    if (stall_time.equals("未知"))
                        printBean.parkTime = "0";
                    else
                        printBean.parkTime = stall_time;

                    String json = gson.toJson(printBean);
                    L.showlogError("Json==" + json);

                    PrintUtils.print(ImageDetailActivity.this, json, out_user, MyApplication.settingInfo.getString("companyName"));

                } else {
                    T.showShort(ImageDetailActivity.this, "请到系统设置中打开打印权限");
                }

            }
        });

    }

    private void loadImage(String in_image, String out_image) {
        L.showlogError("loadImage 1==" + in_image);
        L.showlogError("loadImage 2==" + out_image);

        if (in_image.length() < 30 && in_image.length() > 10) {
            // 网络图片
            String path = in_image.substring(0, 10);
            String serverPath = MyApplication.settingInfo.getString("serverIp", "") + "/car_images/" + path + "/" + in_image;
            L.showlogError("in_image serverPath==" + serverPath);
            Glide.with(ImageDetailActivity.this).load(serverPath).error(R.drawable.ic_img_car).into(inImage);

        } else {
            // 本地图片
            in_image = checkPath(in_image);
            Glide.with(ImageDetailActivity.this).load(in_image).error(R.drawable.ic_img_car).into(inImage);
        }

        if (out_image.length() < 30 && out_image.length() > 10) {
            // 网络图片
            String path = out_image.substring(0, 10);
            String serverPath = MyApplication.settingInfo.getString("serverIp", "") + "/car_images/" + path + "/" + out_image;
            L.showlogError("out_image serverPath==" + serverPath);
            Glide.with(ImageDetailActivity.this).load(serverPath).error(R.drawable.ic_img_car).into(outImage);

        } else {
            // 本地图片
            out_image = checkPath(out_image);
            Glide.with(ImageDetailActivity.this).load(out_image).error(R.drawable.ic_img_car).into(outImage);
        }
    }

    private String checkPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            String[] captures = path.split("capture");
            L.showlogError("captures[0]===" + captures[0] + ",captures[1]==" + captures[1]);
            String serverIp = MyApplication.settingInfo.getString("serverIp", "") + "/car_images" + captures[1];
            L.showlogError("checkPath  serverIp=="+serverIp);
            return serverIp;
        } else {
            return path;
        }
    }
}
