package com.gz.gzcar.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.CarInfoTable;
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

        ArrayList<String> carweiList = new ArrayList<>();
        try {
            List<CarWeiTable> all = db.findAll(CarWeiTable.class);
            if (all != null) {

                for (int i = 0; i < all.size(); i++) {

                    carweiList.add(all.get(i).getInfo() + all.get(i).getId());
                }
                mCarwei.setPopList(carweiList);
                mCarwei.setText(carweiList.get(0));
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

        CarInfoTable mInfo = new CarInfoTable();
        mInfo.setCar_no(carNum);
        mInfo.setCar_type(carType);
        mInfo.setCarWei(carWei);
        mInfo.setPerson_name(person + "");
        mInfo.setPerson_tel(phone + "");
        mInfo.setPerson_address(address + "");
        mInfo.setStart_date(DateUtils.string2Date(startTime));
        mInfo.setStop_date(DateUtils.string2Date(endTime));

        try {
            x.getDb(daoConfig).save(mInfo);
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
