package com.gz.gzcar.settings;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import com.gz.gzcar.R;
import com.gz.gzcar.settingfragment.CarManagerFragment;
import com.gz.gzcar.settingfragment.MoneyFragment;
import com.gz.gzcar.settingfragment.ParkingManagerFragment;
import com.gz.gzcar.settingfragment.SettingsFragment;
import com.gz.gzcar.settingfragment.UserFragment;

public class SettingActivity extends FragmentActivity implements View.OnClickListener {

    private RadioButton back;
    private RadioButton r1;
    private RadioButton r2;
    private RadioButton r3;
    private RadioButton r4;
    private RadioButton r5;
    private CarManagerFragment fragment1;
    private ParkingManagerFragment fragment2;
    private MoneyFragment fragment3;
    private UserFragment fragment4;
    private SettingsFragment fragment5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        initViews();
        initEvents();
    }

    private void initEvents() {

        back.setOnClickListener(this);
        r1.setOnClickListener(this);
        r2.setOnClickListener(this);
        r3.setOnClickListener(this);
        r4.setOnClickListener(this);
        r5.setOnClickListener(this);

        setSelect(0);
        r1.setChecked(true);
    }

    private void initViews() {

        back = (RadioButton) findViewById(R.id.rb_back);
        r1 = (RadioButton) findViewById(R.id.rb_1);
        r2 = (RadioButton) findViewById(R.id.rb_2);
        r3 = (RadioButton) findViewById(R.id.rb_3);
        r4 = (RadioButton) findViewById(R.id.rb_4);
        r5 = (RadioButton) findViewById(R.id.rb_5);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_back:
                finish();
                break;
            case R.id.rb_1:
                setSelect(0);
                break;
            case R.id.rb_2:
                setSelect(1);
                break;
            case R.id.rb_3:
                setSelect(2);
                break;
            case R.id.rb_4:
                setSelect(3);
                break;
            case R.id.rb_5:
                setSelect(4);
                break;

        }
    }

    private void setSelect(int i) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        hideFragment(transaction);

        switch (i) {
            case 0:
                if (fragment1 == null) {
                    fragment1 = new CarManagerFragment();
                    transaction.add(R.id.fl, fragment1);
                } else {
                    transaction.show(fragment1);
                }
                break;
            case 1:
                if (fragment2 == null) {
                    fragment2 = new ParkingManagerFragment();
                    transaction.add(R.id.fl, fragment2);
                } else {
                    transaction.show(fragment2);

                }
                break;
            case 2:
                if (fragment3 == null) {
                    fragment3 = new MoneyFragment();
                    transaction.add(R.id.fl, fragment3);
                } else {
                    transaction.show(fragment3);
                }
                break;
            case 3:
                if (fragment4 == null) {
                    fragment4 = new UserFragment();
                    transaction.add(R.id.fl, fragment4);
                } else {
                    transaction.show(fragment4);
                }
                break;

            case 4:
                if (fragment5 == null) {
                    fragment5 = new SettingsFragment();
                    transaction.add(R.id.fl, fragment5);
                } else {
                    transaction.show(fragment5);
                }
                break;
        }

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (fragment1 != null) {
            transaction.hide(fragment1);
        }
        if (fragment2 != null) {
            transaction.hide(fragment2);
        }
        if (fragment3 != null) {
            transaction.hide(fragment3);
        }
        if (fragment4 != null) {
            transaction.hide(fragment4);
        }
        if (fragment5 != null) {
            transaction.hide(fragment5);
        }
    }
}
