package com.gz.gzcar;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gz.gzcar.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Endeavor on 2016/10/9 0009.
 */

public class BaseFragment extends Fragment {
    //    protected  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected SimpleDateFormat dateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    protected TimePickerView pvTime;
    protected TimePickerView pvTime2;

    protected void startTimeShow() {
        pvTime.show();
    }

    protected void endTimeShow() {
        pvTime2.show();
    }

    protected void initTime(Context context, final TextView star, final TextView end) {
        //时间选择器
        pvTime = new TimePickerView(context, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTime(new Date(System.currentTimeMillis()));
        pvTime.setCyclic(true);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                star.setText(DateUtils.date2String(date));
            }
        });

        pvTime2 = new TimePickerView(context, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime2.setTime(new Date());
        pvTime2.setCyclic(true);
        pvTime2.setCancelable(true);
        //时间选择后回调
        pvTime2.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                end.setText(DateUtils.date2String(date));
            }
        });
    }

    // yyyy-MM-dd HH:mm
    protected void initDetailTime(Context context, final TextView star, final TextView end) {
        //时间选择器
        pvTime = new TimePickerView(context, TimePickerView.Type.ALL);
        pvTime.setTime(new Date(new Date().getTime()-24*60*60*1000));
        pvTime.setCyclic(true);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                star.setText(DateUtils.date2StringDetail(date));
            }
        });

        pvTime2 = new TimePickerView(context, TimePickerView.Type.ALL);
        pvTime2.setTime(new Date());
        pvTime2.setCyclic(true);
        pvTime2.setCancelable(true);
        //时间选择后回调
        pvTime2.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                end.setText(DateUtils.date2StringDetail(date));
            }
        });
    }
}
