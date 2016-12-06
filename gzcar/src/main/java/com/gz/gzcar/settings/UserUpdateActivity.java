package com.gz.gzcar.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserUpdateActivity extends BaseActivity {
    @Bind(R.id.user_up_name)
    EditText mName;
    @Bind(R.id.user_update_type)
    MyPullText mType;
    @Bind(R.id.user_up_pwd)
    EditText mPwd;
    public DbManager db = x.getDb(MyApplication.daoConfig);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);
        ButterKnife.bind(this);
        ArrayList<String> typelist = new ArrayList<>();
        typelist.add("操作员");
        typelist.add("管理员");
        mType.setPopList(typelist);
        init();
    }

    private void init() {
        String userName = getIntent().getStringExtra("userName");
        String password = getIntent().getStringExtra("password");
        String type = getIntent().getStringExtra("type");
        mName.setText(userName);
        mPwd.setText(password);
        if(type.equals("system")){
            mType.setText("管理员");
        }
        else{
            mType.setText("操作员");
        }

    }

    @OnClick({R.id.user_up_cancle, R.id.user_up_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_up_cancle:
                finish();
                break;
            case R.id.user_up_update:
                upDate();

                break;
        }
    }

    private void upDate() {

        int id = getIntent().getIntExtra("id", -1);
        String newName = mName.getText().toString().trim();
        String newPwd = mPwd.getText().toString().trim();
        String newType = mType.getText().trim();

        if (id == -1) {
            return;
        }

        try {
            UserTable user = db.findById(UserTable.class, id);
            if(newType.equals("管理员")) {
                user.setType("system");
            }else{
                user.setType("common");
            }
            user.setUserName(newName);
            user.setPassword(newPwd);

            db.update(user, "userName", "password", "type");

            t.showShort(this, "保存成功");
            finish();
        } catch (DbException e) {
            t.showShort(this, "保存异常");
            e.printStackTrace();
        }
    }
}
