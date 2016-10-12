package com.gz.gzcar.searchfragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.BaseFragment;
import com.gz.gzcar.Database.FreeInfoTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.ParseException;
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
    private List<FreeInfoTable> allData;
    private MyAdapter myAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_money, container, false);

        ButterKnife.bind(this, view);
        return view;
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
//                Toast.makeText(getActivity(),"carNum111="+carNum,Toast.LENGTH_SHORT).show();
                if (carNum.length() == 0) {
                    try {
                        List<FreeInfoTable> all = db.selector(FreeInfoTable.class).findAll();
                        if (allData != null) {

                            allData.clear();
                            allData.addAll(all);
//                        Toast.makeText(getContext(),"all="+all.size()+";;allData="+allData.size(),Toast.LENGTH_SHORT).show();

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
        if (allData != null) {

            myAdapter = new MyAdapter();
            rcy.setAdapter(myAdapter);
            sumMoney();
        }
    }

    private void initdata() {
//        addData();
        try {
            allData = db.selector(FreeInfoTable.class).findAll();
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

                if (TextUtils.isEmpty(carNum)) {
                    try {
                        List<FreeInfoTable> all = db.selector(FreeInfoTable.class)
                                .where("in_time", ">", dateFormatDetail.parse(start))
                                .and("out_time", "<", dateFormatDetail.parse(end))
//                            .and("car_no","=",carNum)
                                .findAll();

//                        Log.e("mm", "all.size==" + allData.size());

                        if (allData != null && all.size() > 0) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                            sumMoney();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            if (allData != null) {

                                allData.clear();
                                allData = db.selector(FreeInfoTable.class).findAll();
                                myAdapter.notifyDataSetChanged();
                                sumMoney();
                            }
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        List<FreeInfoTable> all = db.selector(FreeInfoTable.class)
                                .where("in_time", ">", dateFormatDetail.parse(start))
                                .and("out_time", "<", dateFormatDetail.parse(end))
                                .and("car_number", "=", carNum)
                                .findAll();

//                        Log.e("mm", "all.size==" + allData.size());

                        if (allData != null && all.size() > 0) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                            sumMoney();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            if (allData != null) {

                                allData.clear();
                                allData = db.selector(FreeInfoTable.class).findAll();
                                myAdapter.notifyDataSetChanged();
                                sumMoney();
                            }
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

            double money = allData.get(i).getMoney();
            toteMoney += money;
        }
        mMoney.setText("合计金额:" + toteMoney);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = View.inflate(getActivity(), R.layout.search_car_info, false);
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_free, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            FreeInfoTable free = allData.get(position);
            holder.mId.setText(position + 1 + "");
            holder.mCarNum.setText(free.getCarNumber());
            holder.mMoney.setText(free.getMoney() + "");
            holder.mParkingtime.setText(free.getParkTime());
            holder.mType.setText(free.getType());
            Date inTime = free.getInTime();
            if (inTime != null) {

                holder.mInTime.setText(dateFormatDetail.format(inTime));
            }
            if (free.getOutTime() != null) {

                holder.mOuttime.setText(dateFormatDetail.format(free.getOutTime()));
            }

            free = null;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initdata();
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
