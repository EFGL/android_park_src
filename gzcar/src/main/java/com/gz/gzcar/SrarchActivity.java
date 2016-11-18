package com.gz.gzcar;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;

import com.gz.gzcar.searchfragment.CarInfoFragment;
import com.gz.gzcar.searchfragment.PrizeFragment;
import com.gz.gzcar.searchfragment.RunFragment;

public class SrarchActivity extends BaseActivity implements View.OnClickListener {

    private RadioButton back;
    private RadioButton r1;
    private RadioButton r2;
    private RadioButton r3;

    private CarInfoFragment fragment1;
    private RunFragment fragment2;
    private PrizeFragment fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initEvents();
    }

    private void initEvents() {

        back.setOnClickListener(this);
        r1.setOnClickListener(this);
        r2.setOnClickListener(this);
        r3.setOnClickListener(this);


        setSelect(0);
        r1.setChecked(true);
    }

    private void initViews() {

        back = (RadioButton) findViewById(R.id.rb_search_back);
        r1 = (RadioButton) findViewById(R.id.rb_search_1);
        r2 = (RadioButton) findViewById(R.id.rb_search_2);
        r3 = (RadioButton) findViewById(R.id.rb_search_3);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_search_back:
                finish();
                break;
            case R.id.rb_search_1:
                setSelect(0);
                break;
            case R.id.rb_search_2:
                setSelect(1);
                break;
            case R.id.rb_search_3:
                setSelect(2);
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
                    fragment1 = new CarInfoFragment();
                    transaction.add(R.id.fl_search, fragment1);
                } else {
                    transaction.show(fragment1);
                }
                break;
            case 1:
                if (fragment2 == null) {
                    fragment2 = new RunFragment();
                    transaction.add(R.id.fl_search, fragment2);
                } else {
                    transaction.show(fragment2);

                }
                break;
            case 2:
                if (fragment3 == null) {
                    fragment3 = new PrizeFragment();
                    transaction.add(R.id.fl_search, fragment3);
                } else {
                    transaction.show(fragment3);
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

    }
}
