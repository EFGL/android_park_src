package com.gz.gzcar.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserAddActivity extends BaseActivity {

    @Bind(R.id.user_name)
    EditText userName;
    @Bind(R.id.user_type)
    MyPullText userType;
    @Bind(R.id.password)
    EditText password;
 // en
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);
        ButterKnife.bind(this);

        MyPullText type = (MyPullText) findViewById(R.id.user_type);// 顺序

        ArrayList<String> typelist = new ArrayList<>();
        typelist.add("操作员");
        typelist.add("管理员");
        type.setPopList(typelist);
        type.setText("操作员");


    }

    @OnClick({R.id.user_cancle, R.id.user_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_cancle:
                finish();
                break;
            case R.id.user_add:
                insert();
                break;
        }
    }

    private void insert() {

        String name = userName.getText().toString().trim();
        String type = userType.getText();
        String pwd = password.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(type) || TextUtils.isEmpty(pwd)) {
            T.showShort(this, "信息不完整");
            return;
        }
        DbManager db = x.getDb(MyApplication.daoConfig);
        try {
            List<UserTable> user = db.selector(UserTable.class).where("userName", "=", name).findAll();
            if (user.size()>0){
                T.showShort(this,"用户名已存在,请重新输入");
                return;
            }

            UserTable userTable = new UserTable();
            userTable.setUserName(name);
            userTable.setPassword(pwd);
            userTable.setType(type);
            db.save(userTable);
            T.showShort(this, "增加成功");
            finish();
        } catch (DbException e) {
            T.showShort(this, "增加异常");
            e.printStackTrace();
        }
    }
}
