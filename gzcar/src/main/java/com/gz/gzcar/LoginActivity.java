package com.gz.gzcar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

import static com.gz.gzcar.R.id.user;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {


    // UI references.
    private EditText mUser;
    private EditText mPasswordView;
    private RelativeLayout rt;
    private DbManager db = x.getDb(MyApplication.daoConfig);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rt = (RelativeLayout) findViewById(R.id.root);
        // Set up the login form.
        mUser = (EditText) findViewById(user);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        Button mCencleButton = (Button) findViewById(R.id.cencle_button);
        makeUser();


        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        //取消
        mCencleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //initDB();
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

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String user = mUser.getText().toString();
        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(user)) {
            mUser.setError("用户名不能为空");
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("密码不能为空");
            return;
        }
        match(user, password);

    }

    private void match(String userName, String password) {

        try {
            List<UserTable> all = db.selector(UserTable.class).where("userName", "=", userName).and("password", "=", password).findAll();

            if (all.size() > 0) {
                T.showShort(this, "count==" + all.size());
                String type = all.get(0).getType();
                //进入主页面
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("type",type);
                startActivity(intent);
                finish();
            } else {
                T.showShort(this, "用户名或密码错误");
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}

