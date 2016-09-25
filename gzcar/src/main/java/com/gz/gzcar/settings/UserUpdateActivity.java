package com.gz.gzcar.settings;

import android.os.Bundle;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.R;
import com.gz.gzcar.weight.MyPullText;

import java.util.ArrayList;

public class UserUpdateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);
        MyPullText type = (MyPullText) findViewById(R.id.user_update_type);// 顺序
        ArrayList<String> typelist = new ArrayList<>();
        typelist.add("操作员");
        typelist.add("管理员");
        type.setPopList(typelist);
        type.setText("操作员");
    }
}
