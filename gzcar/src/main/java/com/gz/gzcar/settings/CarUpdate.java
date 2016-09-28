package com.gz.gzcar.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarUpdate extends BaseActivity {

    @Bind(R.id.up_et_carnum)
    EditText mCarnum;
    @Bind(R.id.car_update_type)
    MyPullText mType;
    @Bind(R.id.car_update_order)
    MyPullText mOrder;
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
    private int id;
    private DbManager db = x.getDb(MyApplication.daoConfig);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);


        ArrayList<String> typelist = new ArrayList<>();
        typelist.add("固定车");
        typelist.add("探亲车");
        mType.setPopList(typelist);
        mType.setText("固定车");

        ArrayList<String> orderlist = new ArrayList<>();
        orderlist.add("地库101");
        mOrder.setPopList(orderlist);
        mOrder.setText("地库101");

        initTime(mStart, mEnd);
        receiveData();
    }

    private void receiveData() {

        String carNumber = getIntent().getStringExtra("carNumber");
        String carType = getIntent().getStringExtra("carType");
        String carWei = getIntent().getStringExtra("carWei");
        String person = getIntent().getStringExtra("person");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        id = getIntent().getIntExtra("id", -1);

        mCarnum.setText(carNumber);
        mType.setText(carType);
        mOrder.setText(carWei);
        mPerson.setText(person);
        mPhone.setText(phone);
        mAddress.setText(address);
        mStart.setText(startTime);
        mEnd.setText(endTime);
    }


    private void upDate() {
        if (id != -1) {

            try {
                CarInfoTable car = db.findById(CarInfoTable.class, id);
                Log.i("ende",car.toString());
                car.setCar_no(mCarnum.getText().toString().trim());
                Log.i("ende","setCar_no");
                car.setCarWei(mOrder.getText().toString().trim());
                Log.i("ende","setCarWei");
                car.setCar_type(mType.getText().toString().trim());
                Log.i("ende","setCar_type");
                car.setPerson_name(mPerson.getText().toString().trim());
                Log.i("ende","setPerson_name");
                car.setPerson_tel(mPhone.getText().toString().trim());
                Log.i("ende","setPerson_tel");
                car.setPerson_address(mAddress.getText().toString().trim());
                Log.i("ende","setPerson_address");

                car.setStart_date(DateUtils.string2Date(mStart.getText().toString().trim()));
                Log.i("ende","setStart_date");
                car.setStop_date(DateUtils.string2Date(mEnd.getText().toString().trim()));
                Log.i("ende","setStop_date");

                db.update(car, "car_no", "car_wei", "car_type", "person_name", "person_tel", "person_address", "start_date", "stop_date");
                Log.i("ende","update");

                T.showShort(this,"修改成功");
                finish();
            } catch (DbException e) {
                T.showShort(this,"修改异常");
                e.printStackTrace();
            }
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
