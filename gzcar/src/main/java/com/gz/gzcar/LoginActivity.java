package com.gz.gzcar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.SPUtils;
import com.gz.gzcar.utils.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import static com.gz.gzcar.MyApplication.daoConfig;
import static com.gz.gzcar.R.id.user;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {


    // UI references.
    private EditText mUser;
    private EditText mPasswordView;
    private RelativeLayout rt;
    private DbManager db = x.getDb(daoConfig);
    private SPUtils spUtils;
    private boolean first;

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

        if (spUtils == null) {

            spUtils = new SPUtils(this, "config");
        }
        first = spUtils.getBoolean("first");

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


    }

    @Override
    protected void onResume() {
        super.onResume();
//
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
                            T.showShort(LoginActivity.this, "stop_data==" + DateUtils.change(stop_date));

                            db.save(mInfo);
                            T.showShort(LoginActivity.this, "增加成功");

                        } catch (DbException e) {
                            T.showShort(LoginActivity.this, "新增异常");
                            Log.e("ende","DbException-----"+e.toString());
                        }

                        //保存完后，继续请求数据，递归
                        get_info_vehicles(controller_sn, id, "info_vehicles");
                    } catch (JSONException e) {
                        Log.e("ende","JSONException-----"+e.toString());
                    }
                } else if (result.length() == 2) {
                    Log.e("ende", "info 22222");
                    Toast.makeText(LoginActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                intent.putExtra("type", type);
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

