package com.gz.gzcar.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.T;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.Date;

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
    EditText mCartype;
    @Bind(R.id.add_carwei)
    EditText mCarwei;
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
    private TimePickerView pvTime;
    private TimePickerView pvTime2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        initViews();

    }



    private void insertData() {


        getData();
    }

    public void getData() {
        String carNum = mCarnumber.getText().toString().trim();
        String person = mPerson.getText().toString().trim();
        String carType = mCartype.getText().toString().trim();
        String carWei = mCarwei.getText().toString().trim();
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
        if (TextUtils.isEmpty(carWei)) {
            T.showShort(this, "请输入车位");
            return;
        }
        if (TextUtils.isEmpty(person)) {
            T.showShort(this, "请输入联系人");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            T.showShort(this, "请输入地址");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            T.showShort(this, "请输入电话号码");
            return;
        }
        if (TextUtils.isEmpty(startTime)) {
            T.showShort(this, "请选择开始时间");
            return;
        }
        if (TextUtils.isEmpty(endTime)) {
            T.showShort(this, "请选择结束时间");
            return;
        }

        CarInfoTable mInfo = new CarInfoTable();
        mInfo.setCar_no(carNum);
        mInfo.setCar_type(carType);
        mInfo.setPerson_address(carWei);
        mInfo.setPerson_name(person);
        mInfo.setPerson_tel(phone);
        mInfo.setPerson_address(address);
        mInfo.setStart_date(DateUtils.string2Date(startTime));
        mInfo.setStop_date(DateUtils.string2Date(endTime));

        try {
            x.getDb(daoConfig).save(mInfo);
            T.showShort(this, "增加成功");
        } catch (DbException e) {
            T.showShort(this, "新增异常");
            e.printStackTrace();
        }
    }

    @OnClick({R.id.add_starttiem, R.id.add_endtime,R.id.add_btn_cancle, R.id.add_btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_starttiem:
                pvTime.show();
                break;
            case R.id.add_endtime:
                pvTime2.show();
                break;
            case R.id.add_btn_cancle:
                finish();
                break;
            case R.id.add_btn_ok:
                insertData();
                break;
        }
    }

    public void initViews(){
        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                mStarttiem.setText(DateUtils.date2String(date));
            }
        });

        pvTime2 = new TimePickerView(this, TimePickerView.Type.ALL);
        pvTime2.setTime(new Date());
        pvTime2.setCyclic(false);
        pvTime2.setCancelable(true);
        //时间选择后回调
        pvTime2.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                mEndtime.setText(DateUtils.date2String(date));
            }
        });
    }
}
