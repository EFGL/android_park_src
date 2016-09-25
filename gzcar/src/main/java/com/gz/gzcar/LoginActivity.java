package com.gz.gzcar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {


    // UI references.
    private EditText mUser;
    private EditText mPasswordView;
    private RelativeLayout rt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rt = (RelativeLayout) findViewById(R.id.root);
        // Set up the login form.
        mUser = (EditText) findViewById(R.id.user);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        Button mCencleButton = (Button) findViewById(R.id.cencle_button);
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

   /* private void initDB() {
        // File 目录下创建数据同名文件
        File files = getFilesDir();
        File file = new File(files, "tenement_passing_manager.db");
        if (file.exists()){
            return;
        }
        //复制文件
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            // 读取assets资产目录下的数据库文件
            inputStream = getAssets().open("tenement_passing_manager.db");
            // 将读取到的写到指定file
            fos = new FileOutputStream(file);
            byte [] bs = new byte[1024];
            int temp;
            while ((temp = inputStream.read(bs))>0){
                fos.write(bs,0,temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null&&fos!=null){
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
*/
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
        if (password.length() < 2) {
            mPasswordView.setError("请检查密码长度");
            return;
        }
        //进入主页面
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

