package com.gz.gzcar.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class CarUpdate extends BaseActivity {

    @Bind(R.id.up_et_carnum)
    EditText mCarnum;
    @Bind(R.id.car_update_type)
    MyPullText mType;
    @Bind(R.id.up_et_person)
    EditText mPerson;
    @Bind(R.id.up_et_phone)
    EditText mPhone;
    @Bind(R.id.up_et_address)
    EditText mAddress;
    @Bind(R.id.up_et_start)
    TextView mStart;
    @Bind(R.id.up_et_end)
    TextView mEnd;
    @Bind(R.id.add_carwei)
    MyPullText mCarwei1;
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
    private int id;
    private DbManager db = x.getDb(MyApplication.daoConfig);
    private String carNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        initTime(mStart, mEnd);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initMypull();
        receiveData();
    }

    private void initMypull() {
        ArrayList<String> typelist = new ArrayList<>();
        typelist.add("固定车");
        typelist.add("探亲车");
        mType.setPopList(typelist);
        mType.setText("固定车");

        initCarWei(mCarwei1);
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

                    carweiList.add(all.get(i).getPrint_code() + all.get(i).getId());
                }
                carweiList.add(0, "");
                myPullText.setPopList(carweiList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void receiveData() {

        carNumber = getIntent().getStringExtra("carNumber");
        String carType = getIntent().getStringExtra("carType");
//        String carWei = getIntent().getStringExtra("carWei");
        String person = getIntent().getStringExtra("person");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        id = getIntent().getIntExtra("id", -1);

        mCarnum.setText(carNumber);
        mType.setText(carType);
//        mOrder.setText(carWei);
        mPerson.setText(person);
        mPhone.setText(phone);
        mAddress.setText(address);
        mStart.setText(startTime);
        mEnd.setText(endTime);

        setCarBindData(carNumber);
    }

    int id1 = -1;
    int id2 = -1;
    int id3 = -1;
    int id4 = -1;
    int id5 = -1;
    int id6 = -1;


    private void setCarBindData(String carNumber) {

        if (TextUtils.isEmpty(carNumber))
            return;

        try {
            List<CarWeiBindTable> all = db.selector(CarWeiBindTable.class).where("car_no", "=", carNumber).findAll();
            if (all != null && all.size() > 0) {
                for (int i = 0; i < all.size(); i++) {
                    String carWei = all.get(i).getStall_code();
                    if (i == 0) {
                        mCarwei1.setText(carWei);
                        id1 = all.get(i).getId();
                    }
                    if (i == 1) {
                        mCarwei2.setText(carWei);
                        id2 = all.get(i).getId();
                    }
                    if (i == 2) {
                        mCarwei3.setText(carWei);
                        id3 = all.get(i).getId();
                    }
                    if (i == 3) {
                        mCarwei4.setText(carWei);
                        id4 = all.get(i).getId();
                    }
                    if (i == 4) {
                        mCarwei5.setText(carWei);
                        id5 = all.get(i).getId();
                    }
                    if (i == 5) {
                        mCarwei6.setText(carWei);
                        id6 = all.get(i).getId();
                    }
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }


    private void upDate() {
        if (id != -1) {

            try {
                CarInfoTable car = db.findById(CarInfoTable.class, id);
                car.setCar_no(mCarnum.getText().toString().trim());
//                car.setCarWei(mOrder.getText().toString().trim());
                car.setCar_type(mType.getText().toString().trim());
                car.setPerson_name(mPerson.getText().toString().trim());
                car.setPerson_tel(mPhone.getText().toString().trim());
                car.setPerson_address(mAddress.getText().toString().trim());

                car.setStart_date(DateUtils.string2Date(mStart.getText().toString().trim()));
                car.setStop_date(DateUtils.string2Date(mEnd.getText().toString().trim()));

                db.update(car, "car_no", "car_type", "person_name", "person_tel", "person_address", "start_date", "stop_date");

                // TODO: 2016/10/17 0017
                String carWei1 = mCarwei1.getText().toString().trim();
                String carWei2 = mCarwei2.getText().toString().trim();
                String carWei3 = mCarwei3.getText().toString().trim();
                String carWei4 = mCarwei4.getText().toString().trim();
                String carWei5 = mCarwei5.getText().toString().trim();
                String carWei6 = mCarwei6.getText().toString().trim();

                for (int i = 0; i < 6; i++) {
                    if (i == 0)
                        upCarBindInfo(id1, carWei1);
                    if (i == 1)
                        upCarBindInfo(id2, carWei2);
                    if (i == 2)
                        upCarBindInfo(id3, carWei3);
                    if (i == 3)
                        upCarBindInfo(id4, carWei4);
                    if (i == 4)
                        upCarBindInfo(id5, carWei5);
                    if (i == 5)
                        upCarBindInfo(id6, carWei6);
                }


                Log.i("ende", "update");

                T.showShort(this, "修改成功");
                finish();
            } catch (DbException e) {
                T.showShort(this, "修改异常");
                e.printStackTrace();
            }
        }
    }

    private void upCarBindInfo(int id, String carWei) throws DbException {
        if (id != -1 && !TextUtils.isEmpty(carWei)) {
            // 原来有 现在也有 更新
            CarWeiBindTable carBind = db.findById(CarWeiBindTable.class, id);
            carBind.setStall_code(carWei);
            db.update(carBind, "car_wei");
            Log.e("ende","id=="+id+"原来有 现在也有 更新");
        } else if (id != -1 && TextUtils.isEmpty(carWei)) {
            // 原来有 现在没有 删除
            db.deleteById(CarWeiBindTable.class,id);
            Log.e("ende","id=="+id+"原来有 现在没有 删除");
        } else if (id == -1 && !TextUtils.isEmpty(carWei)) {
            // 原来没有 现在有 创建
            CarWeiBindTable carBind = new CarWeiBindTable();
            carBind.setCar_no(mCarnum.getText().toString().trim());
            carBind.setStall_code(carWei);
            db.save(carBind);
            Log.e("ende","id=="+id+" 原来没有 现在有 创建");
        }
    }

    @OnClick({R.id.up_et_start, R.id.up_et_end, R.id.up_cancle, R.id.up_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.up_et_start:
                startTimeShow();
                break;
            case R.id.up_et_end:
                endTimeShow();
                break;
            case R.id.up_cancle:
                finish();
                break;
            case R.id.up_update:
                upDate();
                break;
        }
    }

}