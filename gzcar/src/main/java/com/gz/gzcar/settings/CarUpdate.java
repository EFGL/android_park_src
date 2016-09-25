package com.gz.gzcar.settings;

import android.os.Bundle;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.R;
import com.gz.gzcar.weight.MyPullText;

import java.util.ArrayList;

public class CarUpdate extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        MyPullText type = (MyPullText) findViewById(R.id.car_update_type);// 类型
        MyPullText order = (MyPullText) findViewById(R.id.car_update_order);// 顺序

        ArrayList<String> typelist = new ArrayList<>();
        typelist.add("固定车");
        typelist.add("探亲车");
        type.setPopList(typelist);
        type.setText("固定车");

        ArrayList<String> orderlist = new ArrayList<>();
        orderlist.add("地库101");
        order.setPopList(orderlist);
        order.setText("地库101");
    }
}
