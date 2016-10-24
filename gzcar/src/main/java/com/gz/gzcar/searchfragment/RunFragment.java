package com.gz.gzcar.searchfragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.BaseFragment;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Endeavor on 2016/8/8.
 * <p>
 * 通行记录查询
 */
public class RunFragment extends BaseFragment {


    @Bind(R.id.et_run_carnumber)
    EditText mCarNumber;
    @Bind(R.id.et_run_starttime)
    TextView mStartTime;
    @Bind(R.id.et_run_endtime)
    TextView mEndTime;
    @Bind(R.id.run_recyclerview)
    RecyclerView rcy;
    private View view;
    private DbManager db = null;
    private MyAdapter myAdapter;
    private List<TrafficInfoTable> allData;
    private MyPullText myPullText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (db == null)
            db = org.xutils.x.getDb(MyApplication.daoConfig);
        view = inflater.inflate(R.layout.fragment_search_run, container, false);
        ButterKnife.bind(this, view);
        initSpinner();
        return view;
    }

    private void initSpinner() {
        myPullText = (MyPullText) view.findViewById(R.id.mypulltext);
        ArrayList<String> popListItem = new ArrayList<String>();
        popListItem.add("所有车");
        popListItem.add("固定车");
        popListItem.add("临时车");
        popListItem.add("特殊车");
        myPullText.setPopList(popListItem);
        myPullText.setText(popListItem.get(0));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String start = DateUtils.getCurrentYear() + "-" + DateUtils.getCurrentMonth() + "-" + DateUtils.getCurrentDay() + " 00:00";
        String end = DateUtils.getCurrentDataDetailStr();
        mStartTime.setText(start);
        mEndTime.setText(end);
        initData();
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
                                .findAll();
                        if (allData != null&&all!=null) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                        }else {
                            T.showShort(getContext(),"未查到相关数据");
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
        }
    }

    private void initData() {
        String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
        Date date = DateUtils.string2DateDetail(today);
        try {
            allData = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", date)
                    .findAll();
        } catch (DbException e) {
            T.showShort(getActivity(), "查询异常");
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.et_run_starttime, R.id.et_run_endtime, R.id.btn_run_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_run_starttime:
                startTimeShow();
                break;
            case R.id.et_run_endtime:
                endTimeShow();
                break;
            case R.id.btn_run_search:
                search();
                break;
        }
    }

    private void search() {
        String type = myPullText.getText().toString().trim();
        String carNum = mCarNumber.getText().toString().trim();
        String start = mStartTime.getText().toString().trim();
        String end = mEndTime.getText().toString().trim();

        if (type.equals("所有车")) {
            if (TextUtils.isEmpty(carNum)) {
                searchAll(start, end);
        } else {
            searchWithType(start, end, type);
        }
        return;
    }
    /*按类型查找记录*/
    private void searchWithType(String start, String end, String type) {
        try {
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", DateUtils.string2DateDetail(start))
                    .and("update_time", "<", DateUtils.string2DateDetail(end))
                    .and("car_type", "=", type)
                    .findAll();
            if (allData != null&&all!=null&&all.size()>0) {
                allData.clear();
                allData.addAll(all);
                myAdapter.notifyDataSetChanged();
            }else {
                T.showShort(getContext(),"未查到相关数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(), "查询异常");
        }
    }

    private void searchWithTypeAndCarNum(String start, String end, String type, String carNum) {
        try {
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", DateUtils.string2DateDetail(start))
                    .and("update_time", "<", DateUtils.string2DateDetail(end))
                    .and("car_no", "=", carNum)
                    .findAll();
            if (all!=null) {
                allData.clear();
                allData.addAll(all);
                myAdapter.notifyDataSetChanged();
            }else {
                T.showShort(getContext(),"未查到相关数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(), "查询异常");
        }
    }
    /*查询所有车*/
    private void searchAll(String start, String end) {
        try {
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", DateUtils.string2DateDetail(start))
                    .and("update_time", "<", DateUtils.string2DateDetail(end))
                    .findAll();
            if (allData != null&&all!=null&&all.size()>0) {
                allData.clear();
                allData.addAll(all);
                myAdapter.notifyDataSetChanged();
            }else {
                T.showShort(getContext(),"未查到相关数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(), "查询异常");
        }
    }

    private void searchAllWithCarNum(String start, String end, String number) {
        try {
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", DateUtils.string2DateDetail(start))
                    .and("update_time", "<", DateUtils.string2DateDetail(end))
                    .and("car_no", "=", number)
                    .orderBy("id",true)
                    .findAll();
            if (allData != null&&all!=null&&all.size()>0) {
                allData.clear();
                allData.addAll(all);
                myAdapter.notifyDataSetChanged();
            }else {
                T.showShort(getContext(),"未查到相关数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(), "查询异常");
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_traffic, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            TrafficInfoTable traffic = allData.get(position);
            holder.Id.setText(position + 1 + "");
            holder.Carnum.setText(traffic.getCar_no());
            holder.Type.setText(traffic.getCar_type());
            if (traffic.getIn_time() != null) {
                holder.Starttime.setText(dateFormatDetail.format(traffic.getIn_time()));
            } else {
                holder.Starttime.setText("无入场记录");
            }
            if (traffic.getOut_time() != null) {
                holder.Endtime.setText(dateFormatDetail.format(traffic.getOut_time()));
                holder.Endtime.setTextColor(Color.BLACK);
            } else {
                    if (traffic.getStatus().equals("已出")) {
                        holder.Endtime.setText("异常出场");
                        holder.Endtime.setTextColor(Color.RED);
                    }
                    else{
                        holder.Endtime.setText("未出场");
                        holder.Endtime.setTextColor(Color.BLACK);
                    }
            }
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }

    }

    class MyHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_search_id)
        TextView Id;
        @Bind(R.id.item_search_carnum)
        TextView Carnum;
        @Bind(R.id.item_search_type)
        TextView Type;
        @Bind(R.id.item_search_starttime)
        TextView Starttime;
        @Bind(R.id.item_search_endtime)
        TextView Endtime;

        public MyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

}
