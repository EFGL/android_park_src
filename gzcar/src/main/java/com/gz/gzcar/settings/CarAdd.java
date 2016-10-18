package com.gz.gzcar.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.Database.CarWeiTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.DateUtils;
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

import static com.gz.gzcar.MyApplication.daoConfig;

/*
 * 新增
 */
public class CarAdd extends BaseActivity {

    @Bind(R.id.add_carnumber)
    EditText mCarnumber;
    @Bind(R.id.add_cartype)
    MyPullText mCartype;
    @Bind(R.id.add_carwei)
    MyPullText mCarwei;
    @Bind(R.id.add_person)
    EditText mPerson;
    @Bind(R.id.add_phone)
    EditText mPhone;
    @Bind(R.id.add_address)
    EditText mAddress;
    @Bind(R.id.add_starttiem)
    TextView mStarttiem;
    @Bind(R.id.add_endtime)
    TextView mEndtime;
    @Bind(R.id.add_carwei2)
    MyPullText mCarwei2;
    @Bind(R.id.add_carwei3)
    MyPullText mCarwei3;
    @Bind(R.id.add_carwei4)
    MyPullText mCarwei4;
    @Bind(R.id.add_carwei5)
    MyPullText mCarwei5;
    @Bind(R.id.add_carwei6)
    MyPullText mCarwei6;

    private DbManager db = x.getDb(MyApplication.daoConfig);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        initTime(mStarttiem, mEndtime);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initMypull();
    }

    private void initMypull() {
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("固定车");
        typeList.add("探亲车");
        mCartype.setPopList(typeList);
        mCartype.setText(typeList.get(0));

        initCarWei(mCarwei);
        initCarWei(mCarwei2);
        initCarWei(mCarwei3);
        initCarWei(mCarwei4);
        initCarWei(mCarwei5);
        initCarWei(mCarwei6);

    }

    private void initCarWei(MyPullText myPullText) {
        ArrayList<String> carweiList = new ArrayList<>();
        try {
            List<CarWeiTable> all = db.findAll(CarWeiTable.class);
            if (all != null) {

                for (int i = 0; i < all.size(); i++) {

                    carweiList.add(all.get(i).getInfo() + all.get(i).getId());
                }
                carweiList.add(0,"");
                myPullText.setPopList(carweiList);
//                myPullText.setText(carweiList.get(0));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void insertData() {

        getData();
    }

    public void getData() {
        String carNum = mCarnumber.getText().toString().trim();
        String person = mPerson.getText().toString().trim();
        String carType = mCartype.getText().toString().trim();
        String address = mAddress.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String startTime = mStarttiem.getText().toString().trim();
        String endTime = mEndtime.getText().toString().trim();

        if (TextUtils.isEmpty(carNum)) {
            T.showShort(this, "请输入车号");
            return;
        }
        if (TextUtils.isEmpty(carType)) {
            T.showShort(this, "请输入类型");
            return;
        }
//        if (TextUtils.isEmpty(carWei)) {
//            T.showShort(this, "请输入车位");
//            return;
//        }
        if (TextUtils.isEmpty(person)) {
            T.showShort(this, "请输入联系人");
            return;
        }
//        if (TextUtils.isEmpty(address)) {
//            T.showShort(this, "请输入地址");
//            return;
//        }

//        if (TextUtils.isEmpty(phone)) {
//            T.showShort(this, "请输入电话号码");
//            return;
//        }
        if (TextUtils.isEmpty(startTime)) {
            T.showShort(this, "请选择开始时间");
            return;
        }
        if (TextUtils.isEmpty(endTime)) {
            T.showShort(this, "请选择结束时间");
            return;
        }
        try {
            CarInfoTable mInfo = new CarInfoTable();
            mInfo.setCar_no(carNum);
            mInfo.setCar_type(carType);
//        mInfo.setCarWei(carWei);
            mInfo.setPerson_name(person + "");
            mInfo.setPerson_tel(phone + "");
            mInfo.setPerson_address(address + "");
            mInfo.setStart_date(DateUtils.string2Date(startTime));
            mInfo.setStop_date(DateUtils.string2Date(endTime));
            x.getDb(daoConfig).save(mInfo);

            String carWei = mCarwei.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei)){

                CarWeiBindTable carBind = new CarWeiBindTable();
                carBind.setCarWei(carWei);
                carBind.setCarNumber(carNum);
                db.save(carBind);
                Toast.makeText(this,"第1条保存成功",Toast.LENGTH_SHORT).show();
            }
            String carWei2 = mCarwei2.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei2)){

                CarWeiBindTable carBind = new CarWeiBindTable();
                carBind.setCarWei(carWei2);
                carBind.setCarNumber(carNum);
                db.save(carBind);
                Toast.makeText(this,"第2条保存成功",Toast.LENGTH_SHORT).show();
            }
            String carWei3 = mCarwei3.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei3)){

                CarWeiBindTable carBind = new CarWeiBindTable();
                carBind.setCarWei(carWei3);
                carBind.setCarNumber(carNum);
                db.save(carBind);
                Toast.makeText(this,"第3条保存成功",Toast.LENGTH_SHORT).show();
            }
            String carWei4 = mCarwei4.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei4)){

                CarWeiBindTable carBind = new CarWeiBindTable();
                carBind.setCarWei(carWei4);
                carBind.setCarNumber(carNum);
                db.save(carBind);
                Toast.makeText(this,"第4条保存成功",Toast.LENGTH_SHORT).show();
            }
            String carWei6 = mCarwei6.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei6)){

                CarWeiBindTable carBind = new CarWeiBindTable();
                carBind.setCarWei(carWei6);
                carBind.setCarNumber(carNum);
                db.save(carBind);
                Toast.makeText(this,"第5条保存成功",Toast.LENGTH_SHORT).show();
            }
            String carWei5 = mCarwei5.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei5)){

                CarWeiBindTable carBind = new CarWeiBindTable();
                carBind.setCarWei(carWei5);
                carBind.setCarNumber(carNum);
                db.save(carBind);
                Toast.makeText(this,"第6条保存成功",Toast.LENGTH_SHORT).show();
            }

            T.showShort(this, "增加成功");
            finish();
        } catch (DbException e) {
            T.showShort(this, "新增异常");
            e.printStackTrace();
        }
    }

    @OnClick({R.id.add_starttiem, R.id.add_endtime, R.id.add_btn_cancle, R.id.add_btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_starttiem:
                startTimeShow();
                break;
            case R.id.add_endtime:
                endTimeShow();
//                Log.e("ende","当前时间："+year+"-"+month+"-"+day+" "+hours+":"+minute);
                break;
            case R.id.add_btn_cancle:
                finish();
                break;
            case R.id.add_btn_ok:
                insertData();
                break;
        }
    }
}
