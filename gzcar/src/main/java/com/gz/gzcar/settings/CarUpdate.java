package com.gz.gzcar.settings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.Database.CarWeiTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.L;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
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
    @Bind(R.id.tv_type_detail)
    TextView tvTypeDetail;
    @Bind(R.id.et_type_detail)
    EditText etTypeDetail;
    @Bind(R.id.rb_date)
    RadioButton rbDate;
    @Bind(R.id.rb_count)
    RadioButton rbCount;
    @Bind(R.id.rb_time)
    RadioButton rbTime;
    @Bind(R.id.lyt_date)
    LinearLayout lytDate;
    @Bind(R.id.et_free_count)
    EditText etFreeCount;
    @Bind(R.id.lyt_count)
    LinearLayout lytCount;
    @Bind(R.id.et_free_time)
    EditText etFreeTime;
    @Bind(R.id.lyt_time)
    LinearLayout lytTime;
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
        new PullTask().execute();
        initViews();
        receiveData();
    }

    private void initViews() {
        ArrayList<String> typelist = new ArrayList<>();
        typelist.add("固定车");
        typelist.add("特殊车");
        mType.setPopList(typelist);

        String current = DateUtils.date2String(new Date());
        mStart.setText(current);
        String monthday = current.substring(5, 10);
        mEnd.setText((DateUtils.getCurrentYear() + 1) + "/" + monthday);

        mType.setOnTextChangedListener(new MyPullText.OnTextChangedListener() {
            @Override
            public void OnTextChanged() {
                String text = mType.getText();
                if (text.equals("固定车")) {
                    tvTypeDetail.setText("固定车类型:");
                    etTypeDetail.setText("月租车");
                    rbDate.setVisibility(View.VISIBLE);
                    rbDate.setChecked(true);
                    rbCount.setVisibility(View.GONE);
                    rbTime.setVisibility(View.GONE);
                } else {
                    tvTypeDetail.setText("收费类型:");
                    etTypeDetail.setText("探亲车");
                    rbDate.setVisibility(View.VISIBLE);
                    rbCount.setVisibility(View.VISIBLE);
                    rbTime.setVisibility(View.VISIBLE);
                }
            }
        });
        rbCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lytCount.setVisibility(View.VISIBLE);
                    lytDate.setVisibility(View.GONE);
                    lytTime.setVisibility(View.GONE);
                }
            }
        });
        rbDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lytCount.setVisibility(View.GONE);
                    lytDate.setVisibility(View.VISIBLE);
                    lytTime.setVisibility(View.GONE);
                }
            }
        });
        rbTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lytCount.setVisibility(View.GONE);
                    lytDate.setVisibility(View.GONE);
                    lytTime.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void receiveData() {

        carNumber = getIntent().getStringExtra("carNumber");
//        String carType = getIntent().getStringExtra("carType");
//        String carWei = getIntent().getStringExtra("carWei");
        String person = getIntent().getStringExtra("person");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");

        id = getIntent().getIntExtra("id", -1);

        mCarnum.setText(carNumber);
//        mType.setText(carType);
//        mOrder.setText(carWei);
        mPerson.setText(person);
        mPhone.setText(phone);
        mAddress.setText(address);


        String vehicle_type = getIntent().getStringExtra("vehicle_type");// 车类型
        String carTypeDetail = getIntent().getStringExtra("carTypeDetail");// 固定车详情
        String fee_type = getIntent().getStringExtra("fee_type");// 特殊车详情
        int allow_count = getIntent().getIntExtra("allow_count", 0);
        int allow_park_time = getIntent().getIntExtra("allow_park_time", 0);

        mType.setText(vehicle_type);
        if (vehicle_type.equals("固定车")) {
            tvTypeDetail.setText("固定车类型:");
            etTypeDetail.setText(carTypeDetail);
            rbDate.setVisibility(View.VISIBLE);
            rbDate.setChecked(true);
            rbCount.setVisibility(View.GONE);
            rbTime.setVisibility(View.GONE);
            String startTime = getIntent().getStringExtra("startTime");
            String endTime = getIntent().getStringExtra("endTime");

            mStart.setText(startTime);
            mEnd.setText(endTime);
        } else {
            tvTypeDetail.setText("收费类型:");
            etTypeDetail.setText(fee_type);
            rbDate.setVisibility(View.VISIBLE);
            rbCount.setVisibility(View.VISIBLE);
            rbTime.setVisibility(View.VISIBLE);
        }
        if (allow_count != 0) {
            rbCount.setChecked(true);
            rbDate.setChecked(false);
            rbTime.setChecked(false);
            lytCount.setVisibility(View.VISIBLE);
            lytTime.setVisibility(View.GONE);
            lytDate.setVisibility(View.GONE);
            etFreeCount.setText(allow_count + "");
        }

        if (allow_park_time != 0) {
            rbCount.setChecked(false);
            rbDate.setChecked(false);
            rbTime.setChecked(true);
            lytTime.setVisibility(View.VISIBLE);
            lytDate.setVisibility(View.GONE);
            lytCount.setVisibility(View.GONE);
            etFreeTime.setText(allow_park_time + "");
        }

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

            String carNum = mCarnum.getText().toString().trim().toUpperCase();
            String person = mPerson.getText().toString().trim();
            String vehicle_type = mType.getText();// 车辆类型
            String phone = mPhone.getText().toString().trim();
            String address = mAddress.getText().toString().trim();
            String typeDetail = etTypeDetail.getText().toString().trim();
            if (TextUtils.isEmpty(typeDetail)) {
                t.showShort(this, "请输入详细类型");
                return;
            }


            String car_type = "";// 固定车类型详细
            String fee_type = "";// 特殊车类型详细
            String allow_park_time = "0";
            String allow_count = "0";

            if (TextUtils.isEmpty(carNum)) {
                t.showShort(this, "请输入车号");
                return;
            }

            if (TextUtils.isEmpty(person)) {
                t.showShort(this, "请输入联系人");
                return;
            }

            try {
                CarInfoTable mInfo = db.findById(CarInfoTable.class, id);

                if (vehicle_type.equals("固定车")) {

                    car_type = typeDetail;
                    String startTime = mStart.getText().toString().trim();
                    String endTime = mEnd.getText().toString().trim();
                    mInfo.setStart_date(DateUtils.string2Date(startTime));
                    mInfo.setStop_date(DateUtils.string2Date(endTime));

                    mInfo.setAllow_count(Integer.parseInt(allow_count));
                    mInfo.setAllow_park_time(Integer.parseInt(allow_park_time));
                } else {
                    fee_type = typeDetail;
                    if (rbDate.isChecked()) {
                        String startTime = mStart.getText().toString().trim();
                        String endTime = mEnd.getText().toString().trim();
                        mInfo.setStart_date(DateUtils.string2Date(startTime));
                        mInfo.setStop_date(DateUtils.string2Date(endTime));

                        mInfo.setAllow_count(Integer.parseInt(allow_count));
                        mInfo.setAllow_park_time(Integer.parseInt(allow_park_time));
                    } else if (rbCount.isChecked()) {
                        allow_count = etFreeCount.getText().toString().trim();
                        if (TextUtils.isEmpty(allow_count)) {
                            t.showShort(this, "请输入有效次数");
                            return;
                        }
                        mInfo.setAllow_count(Integer.parseInt(allow_count));
                        mInfo.setStart_date(null);
                        mInfo.setStop_date(null);
                        mInfo.setAllow_park_time(Integer.parseInt(allow_park_time));
                    } else if (rbTime.isChecked()) {
                        allow_park_time = etFreeTime.getText().toString().trim();
                        if (TextUtils.isEmpty(allow_park_time)) {
                            t.showShort(this, "请输入有效时长");
                            return;
                        }
                        mInfo.setAllow_park_time(Integer.parseInt(allow_park_time));

                        mInfo.setAllow_count(Integer.parseInt(allow_count));
                        mInfo.setStart_date(null);
                        mInfo.setStop_date(null);
                    }
                }
                mInfo.setVehicle_type(vehicle_type);// 车辆类型
                mInfo.setCar_type(car_type);
                mInfo.setFee_type(fee_type);

                mInfo.setCar_no(carNum);
                mInfo.setPerson_name(person);
                mInfo.setPerson_tel(phone);
                mInfo.setPerson_address(address);


                db.update(mInfo, "car_no", "vehicle_type", "car_type", "fee_type", "allow_count", "allow_park_time", "person_name", "person_tel", "person_address", "start_date", "stop_date");

                L.showlogError("当前车辆信息       " + mInfo.toString());
                String carWei1 = mCarwei1.getText();
                String carWei2 = mCarwei2.getText();
                String carWei3 = mCarwei3.getText();
                String carWei4 = mCarwei4.getText();
                String carWei5 = mCarwei5.getText();
                String carWei6 = mCarwei6.getText();

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

                t.showShort(this, "修改成功");
                finish();
            } catch (DbException e) {
                t.showShort(this, "修改异常");
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
            Log.e("ende", "id==" + id + "原来有 现在也有 更新");
        } else if (id != -1 && TextUtils.isEmpty(carWei)) {
            // 原来有 现在没有 删除
            db.deleteById(CarWeiBindTable.class, id);
            Log.e("ende", "id==" + id + "原来有 现在没有 删除");
        } else if (id == -1 && !TextUtils.isEmpty(carWei)) {
            // 原来没有 现在有 创建
            CarWeiBindTable carBind = new CarWeiBindTable();
            carBind.setCar_no(mCarnum.getText().toString().trim());
            carBind.setStall_code(carWei);
            db.save(carBind);
            Log.e("ende", "id==" + id + " 原来没有 现在有 创建");
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

    class PullTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            ArrayList<String> carweiList = new ArrayList<>();
            try {
                List<CarWeiTable> all = db.findAll(CarWeiTable.class);

                for (int i = 0; i < all.size(); i++) {
                    carweiList.add(all.get(i).getPrint_code() + all.get(i).getId());
                }
                carweiList.add(0, "");
                return carweiList;
            } catch (Exception e) {
                e.printStackTrace();
                return carweiList;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            mCarwei1.setPopList(strings);
            mCarwei2.setPopList(strings);
            mCarwei3.setPopList(strings);
            mCarwei4.setPopList(strings);
            mCarwei5.setPopList(strings);
            mCarwei6.setPopList(strings);
//            t.showShort(CarAdd.this,"车位检索完成,共"+strings.size()+"个");
        }
    }

//    private void initMypull() {
//
//
//        initCarWei(mCarwei1);
//        initCarWei(mCarwei2);
//        initCarWei(mCarwei3);
//        initCarWei(mCarwei4);
//        initCarWei(mCarwei5);
//        initCarWei(mCarwei6);
//    }
//
//    private void initCarWei(MyPullText myPullText) {
//        ArrayList<String> carweiList = new ArrayList<>();
//        try {
//            List<CarWeiTable> all = db.findAll(CarWeiTable.class);
//            if (all != null) {
//
//                for (int i = 0; i < all.size(); i++) {
//
//                    carweiList.add(all.get(i).getPrint_code() + all.get(i).getId());
//                }
//                carweiList.add(0, "");
//                myPullText.setPopList(carweiList);
//            }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
//    }

}