package com.gz.gzcar.searchfragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.BaseFragment;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Endeavor on 2016/8/8.
 * <p/>
 * 收费记录
 */
public class PrizeFragment extends BaseFragment {


    @Bind(R.id.et_money_carnumber)
    EditText mCarNumber;
    @Bind(R.id.et_money_starttime)
    TextView mStartTime;
    @Bind(R.id.et_money_endtime)
    TextView mEndTime;
    @Bind(R.id.btn_money_search)
    Button mSearch;
    @Bind(R.id.money_recyclerview)
    RecyclerView rcy;
    @Bind(R.id.tv_money)
    TextView mMoney;
    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<TrafficInfoTable> allData = new ArrayList<>();
    private MyAdapter myAdapter;
//c7edcc
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_money, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String start = DateUtils.getCurrentYear() + "-" + DateUtils.getCurrentMonth() + "-" + DateUtils.getCurrentDay() + " 00:00";
        String end = DateUtils.getCurrentDataDetailStr();
        Log.e("ende", "start==" + start);
        Log.e("ende", "end==" + end);
        mStartTime.setText(start);
        mEndTime.setText(end);
        initdata();
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews() {

        //时间选择器
        initDetailTime(getContext(), mStartTime, mEndTime);

        mCarNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String carNum = mCarNumber.getText().toString().trim();
                if (carNum.length() == 0) {
                    try {
                        String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
                        Date date = DateUtils.string2DateDetail(today);
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                                .where("update_time", ">", date)
                                .and("status", "=", "已出")
                                .and("receivable", ">", 0)
                                .and("car_type", "!=", "固定车")
                                .orderBy("update_time", true)// true 倒序
                                .findAll();
//                        .OrderBy("update_time",true);
                        if (all != null) {
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                            sumMoney();
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        rcy.setAdapter(myAdapter);
        sumMoney();
    }

    private void initdata() {
        try {
            String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
            Date date = DateUtils.string2DateDetail(today);
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", date)
                    .and("status", "=", "已出")
                    .and("receivable", ">", 0)
                    .and("car_type", "!=", "固定车")
                    .findAll();
            if (all != null) {
                allData.addAll(all);
            }
        } catch (DbException e) {
            T.showShort(getContext(), "全部查询异常");
            e.printStackTrace();
        }

    }


    @OnClick({R.id.et_money_starttime, R.id.et_money_endtime, R.id.btn_money_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_money_starttime:
                startTimeShow();
                break;
            case R.id.et_money_endtime:
                endTimeShow();
                break;

            case R.id.btn_money_search:

                String carNum = mCarNumber.getText().toString().trim();
                String start = mStartTime.getText().toString().trim();
                String end = mEndTime.getText().toString().trim();
                allData.clear();
                if (TextUtils.isEmpty(carNum)) {
                    try {
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                                .where("in_time", ">", dateFormatDetail.parse(start))
                                .and("out_time", "<", dateFormatDetail.parse(end))
                                .and("status", "=", "已出")
                                .and("receivable", ">", 0)
                                .and("car_type", "!=", "固定车")
                                .findAll();
                        if (all != null) {
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                            sumMoney();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            sumMoney();
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                                .where("in_time", ">", dateFormatDetail.parse(start))
                                .and("out_time", "<", dateFormatDetail.parse(end))
                                .and("car_number", "=", carNum)
                                .and("receivable", ">", 0)
                                .and("status", "=", "已出")
                                .and("car_type", "!=", "固定车")
                                .findAll();
                        if (all != null) {
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                            sumMoney();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            sumMoney();
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                break;
        }
    }

    private void sumMoney() {
        double toteMoney = 0;
        for (int i = 0; i < allData.size(); i++) {

            double money = allData.get(i).getActual_money();
            toteMoney += money;
        }
        mMoney.setText("合计金额:" + toteMoney);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_free, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            TrafficInfoTable free = allData.get(position);
            holder.mId.setText(position + 1 + "");
            holder.mCarNum.setText(free.getCar_no());
            holder.mMoney.setText(free.getActual_money() + "");
            holder.mParkingtime.setText(free.getStall_time());
            holder.mType.setText(free.getCar_type());
            Date inTime = free.getIn_time();
            if (inTime != null) {
                holder.mInTime.setText(dateFormatDetail.format(inTime));
            }
            if (free.getOut_time() != null) {

                holder.mOuttime.setText(dateFormatDetail.format(free.getOut_time()));
            }

        }

        @Override
        public int getItemCount() {
            return allData.size();
        }

    }


    class MyHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_free_id)
        TextView mId;
        @Bind(R.id.item_free_num)
        TextView mCarNum;
        @Bind(R.id.item_free_type)
        TextView mType;
        @Bind(R.id.item_free_intime)
        TextView mInTime;
        @Bind(R.id.item_free_outtime)
        TextView mOuttime;
        @Bind(R.id.item_free_parkingtime)
        TextView mParkingtime;
        @Bind(R.id.item_free_money)
        TextView mMoney;

        public MyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }


    public String getTime(Date date) {

        return dateFormatDetail.format(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
