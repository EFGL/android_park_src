package com.gz.gzcar;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.gz.gzcar.utils.DateUtils;

import java.util.Date;

public class BaseActivity extends AppCompatActivity {
    protected Context mContext = this;
    protected BaseAnimatorSet mBasIn;
    protected BaseAnimatorSet mBasOut;
    protected TimePickerView pvTime;
    protected TimePickerView pvTime2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_beas);

        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();


    }

    protected void startTimeShow(){
        pvTime.show();
    }

    protected void endTimeShow(){
        pvTime2.show();
    }

    protected void initTime(final TextView star, final TextView end){
        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                star.setText(DateUtils.date2String(date));
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
                end.setText(DateUtils.date2String(date));
            }
        });
    }

    public void setBasIn(BaseAnimatorSet bas_in) {
        this.mBasIn = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.mBasOut = bas_out;
    }
}
