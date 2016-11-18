package com.gz.gzcar.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gz.gzcar.BaseActivity;
import com.gz.gzcar.Database.CarWeiTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class ParkingAddActivity extends BaseActivity {
    @Bind(R.id.et_id)
    EditText mInfo;
    @Bind(R.id.et_start)
    EditText mStart;
    @Bind(R.id.count)
    EditText mCount;
    private DbManager db = x.getDb(MyApplication.daoConfig);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_add);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.parking_cancle, R.id.parking_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.parking_cancle:
                ParkingAddActivity.this.finish();
                break;
            case R.id.parking_add:
                insert();
                break;
        }
    }

    private void insert() {

        String info = mInfo.getText().toString().trim();
        String count = mCount.getText().toString().trim();
        String start = mStart.getText().toString().trim();
        if (TextUtils.isEmpty(info) || TextUtils.isEmpty(count) || TextUtils.isEmpty(start)) {
            T.showShort(this, "信息不完整");
            return;
        }

        for (int i = 0; i < Integer.parseInt(count); i++) {
            try {
                CarWeiTable cw = new CarWeiTable();
                cw.setPrint_code(info);
                db.save(cw);
                T.showShort(this, "增加成功");
                finish();
            } catch (DbException e) {
                T.showShort(this, "增加异常");
                e.printStackTrace();
            }
        }


    }
}
