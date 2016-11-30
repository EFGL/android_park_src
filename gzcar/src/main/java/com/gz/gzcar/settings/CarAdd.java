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
import com.gz.gzcar.utils.T;
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

/*
 * 新增
 */
public class CarAdd extends BaseActivity {

    @Bind(R.id.add_carnumber)
    EditText mCarnumber;
    @Bind(R.id.pt_cartype)
    MyPullText mCarTypeSelector;
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
    @Bind(R.id.lyt_count)
    LinearLayout lytCount;
    @Bind(R.id.lyt_time)
    LinearLayout lytTime;
    @Bind(R.id.et_free_count)
    EditText etFreeCount;
    @Bind(R.id.et_free_time)
    EditText etFreeTime;

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

        init();
    }

    // 初始化
    private void init() {
        initViews();

        new PullTask().execute();
    }

    private void initViews() {
        String current = DateUtils.date2String(new Date());
        mStarttiem.setText(current);
        String monthday = current.substring(5, 10);
        mEndtime.setText((DateUtils.getCurrentYear() + 1) + "/" + monthday);


        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("固定车");
        typeList.add("特殊车");
        mCarTypeSelector.setPopList(typeList);
        mCarTypeSelector.setText(typeList.get(0));

        mCarTypeSelector.setOnTextChangedListener(new MyPullText.OnTextChangedListener() {
            @Override
            public void OnTextChanged() {
                String text = mCarTypeSelector.getText();
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
            mCarwei.setPopList(strings);
            mCarwei2.setPopList(strings);
            mCarwei3.setPopList(strings);
            mCarwei4.setPopList(strings);
            mCarwei5.setPopList(strings);
            mCarwei6.setPopList(strings);
//            T.showShort(CarAdd.this,"车位检索完成,共"+strings.size()+"个");
        }
    }

    private void saveData() {
        String carNum = mCarnumber.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(carNum)) {
            T.showShort(this, "请输入车号");
            return;
        }

        try {
            List<CarInfoTable> all = db.selector(CarInfoTable.class).where("car_no", "=", carNum).findAll();
            if (all != null && all.size() > 0) {
                T.showShort(this, "该车牌已存在,请重启输入");
                return;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        String person = mPerson.getText().toString().trim();
        String vehicle_type = mCarTypeSelector.getText().toString().trim();// 车辆类型
        String address = mAddress.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();

        String typeDetail = etTypeDetail.getText().toString().trim();
        if (TextUtils.isEmpty(typeDetail)) {
            T.showShort(this, "请输入详细类型");
            return;
        }

        String car_type = "";// 固定车类型详细
        String fee_type = "";// 特殊车类型详细


        if (TextUtils.isEmpty(person)) {
            T.showShort(this, "请输入联系人");
            return;
        }

        try {
            CarInfoTable mInfo = new CarInfoTable();


            if (vehicle_type.equals("固定车")) {

                car_type = typeDetail;
                String startTime = mStarttiem.getText().toString().trim();
                String endTime = mEndtime.getText().toString().trim();
                mInfo.setStart_date(DateUtils.string2Date(startTime));
                mInfo.setStop_date(DateUtils.string2Date(endTime));
            } else {
                fee_type = typeDetail;
                if (rbDate.isChecked()) {
                    String startTime = mStarttiem.getText().toString().trim();
                    String endTime = mEndtime.getText().toString().trim();
                    mInfo.setStart_date(DateUtils.string2Date(startTime));
                    mInfo.setStop_date(DateUtils.string2Date(endTime));
                } else if (rbCount.isChecked()) {
                    String allow_count = etFreeCount.getText().toString().trim();
                    if (TextUtils.isEmpty(allow_count)) {
                        T.showShort(this, "请输入有效次数");
                        return;
                    }
                    mInfo.setAllow_count(Integer.parseInt(allow_count));
                } else if (rbTime.isChecked()) {
                    String freeTime = etFreeTime.getText().toString().trim();
                    if (TextUtils.isEmpty(freeTime)) {
                        T.showShort(this, "请输入有效时长");
                        return;
                    }
                    mInfo.setAllow_park_time(Integer.parseInt(freeTime));
                }
            }
            mInfo.setCar_no(carNum);
            mInfo.setPerson_name(person + "");
            mInfo.setPerson_tel(phone + "");
            mInfo.setPerson_address(address + "");
            mInfo.setVehicle_type(vehicle_type);// 车辆类型
            mInfo.setCar_type(car_type);
            mInfo.setFee_type(fee_type);
            db.save(mInfo);
            Log.e("ende", "该车信息:" + mInfo.toString());

            //保存车位绑定信息
            CarWeiBindTable carBind = new CarWeiBindTable();
            carBind.setCar_no(carNum);
            String carWei = mCarwei.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei)) {
                carBind.setStall_code(carWei);
                db.save(carBind);
            }
            carWei = mCarwei2.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei)) {
                carBind.setStall_code(carWei);
                db.save(carBind);
            }
            carWei = mCarwei3.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei)) {
                carBind.setStall_code(carWei);
                db.save(carBind);
            }
            carWei = mCarwei4.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei)) {
                carBind.setStall_code(carWei);
                db.save(carBind);
            }
            carWei = mCarwei5.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei)) {
                carBind.setStall_code(carWei);
                db.save(carBind);
            }
            carWei = mCarwei6.getText().toString().trim();
            if (!TextUtils.isEmpty(carWei)) {
                carBind.setStall_code(carWei);
                db.save(carBind);
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
                break;
            case R.id.add_btn_cancle:
                finish();
                break;
            case R.id.add_btn_ok:
                saveData();
                break;
        }
    }
}
