package com.gz.gzcar.settings;

import android.os.Bundle;
import android.text.TextUtils;
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
    @Bind(R.id.editText)
    EditText editText;
    @Bind(R.id.lyt_count)
    LinearLayout lytCount;
    @Bind(R.id.lyt_time)
    LinearLayout lytTime;

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
        String current = DateUtils.date2String(new Date());
        mStarttiem.setText(current);
        String monthday = current.substring(5, 10);
        mEndtime.setText((DateUtils.getCurrentYear() + 1) + "-" + monthday);

        initMypull();
    }

    private void initMypull() {
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("固定车");
        typeList.add("特殊车");
        mCarTypeSelector.setPopList(typeList);
        mCarTypeSelector.setText(typeList.get(0));

        mCarTypeSelector.setOnTextChangedListener(new MyPullText.OnTextChangedListener() {
            @Override
            public void OnTextChanged() {
                String text = mCarTypeSelector.getText();
                if (text.equals("固定车")){
                    tvTypeDetail.setText("固定车类型:");
                    etTypeDetail.setText("月租车");
                }else {
                    tvTypeDetail.setText("收费类型:");
                    etTypeDetail.setText("探亲车");
                }
            }
        });
        rbCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lytCount.setVisibility(View.VISIBLE);
                    lytDate.setVisibility(View.GONE);
                    lytTime.setVisibility(View.GONE);
                }
            }
        });
        rbDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lytCount.setVisibility(View.GONE);
                    lytDate.setVisibility(View.VISIBLE);
                    lytTime.setVisibility(View.GONE);
                }
            }
        });
        rbTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lytCount.setVisibility(View.GONE);
                    lytDate.setVisibility(View.GONE);
                    lytTime.setVisibility(View.VISIBLE);
                }
            }
        });

        initCarWei(mCarwei);
        initCarWei(mCarwei2);
        initCarWei(mCarwei3);
        initCarWei(mCarwei4);
        initCarWei(mCarwei5);
        initCarWei(mCarwei6);

    }

    // 改为异步
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


    private void insertData() {

        getData();
    }

    public void getData() {
        String carNum = mCarnumber.getText().toString().trim().toUpperCase();
        String person = mPerson.getText().toString().trim();
        String carType = mCarTypeSelector.getText().toString().trim();
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
        if (TextUtils.isEmpty(person)) {
            T.showShort(this, "请输入联系人");
            return;
        }
//        if (TextUtils.isEmpty(startTime)) {
//            T.showShort(this, "请选择开始时间");
//            return;
//        }
//        if (TextUtils.isEmpty(endTime)) {
//            T.showShort(this, "请选择结束时间");
//            return;
//        }
        try {
            CarInfoTable mInfo = new CarInfoTable();
            mInfo.setCar_no(carNum);
            mInfo.setCar_type(carType);
            mInfo.setPerson_name(person + "");
            mInfo.setPerson_tel(phone + "");
            mInfo.setPerson_address(address + "");
            mInfo.setStart_date(DateUtils.string2Date(startTime));
            mInfo.setStop_date(DateUtils.string2Date(endTime));
            db.save(mInfo);
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
                insertData();
                break;
        }
    }
}
