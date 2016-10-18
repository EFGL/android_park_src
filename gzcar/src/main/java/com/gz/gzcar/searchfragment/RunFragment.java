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

import java.text.ParseException;
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
        MyPullText myPullText = (MyPullText) view.findViewById(R.id.mypulltext);
        ArrayList<String> popListItem = new ArrayList<String>();
        popListItem.add("所有车");
        popListItem.add("固定车");
        popListItem.add("临时车");
        popListItem.add("免费车");
        popListItem.add("其他");
        myPullText.setPopList(popListItem);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String start = DateUtils.getCurrentYear() + "-" + DateUtils.getCurrentMonth() + "-" + DateUtils.getCurrentDay() + " 00:00";
        String end = DateUtils.getCurrentDataDetailStr();
//        Log.e("ende", "start==" + start);
//        Log.e("ende", "end==" + end);
        mStartTime.setText(start);
        mEndTime.setText(end);
        // TODO: 2016/10/13 0013
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
//                Toast.makeText(getActivity(),"carNum111="+carNum,Toast.LENGTH_SHORT).show();
                if (carNum.length() == 0) {
                    try {
//                      List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).findAll();
                        String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
                        Date date = DateUtils.string2DateDetail(today);
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                                .where("update_time", ">", date)
                                .findAll();
                        if (allData != null) {

                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                        }
//                        Toast.makeText(getContext(),"all="+all.size()+";;allData="+allData.size(),Toast.LENGTH_SHORT).show();

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
//        DateUtils.string2DateDetail(DateUtils.getCurrentYear() + "-" + DateUtils.getCurrentMonth() + "-" + DateUtils.getCurrentDay() + " 00:00");
        String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
        Date date = DateUtils.string2DateDetail(today);
        try {
//            allData = db.selector(TrafficInfoTable.class).findAll();
            allData = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", date)
                    .findAll();
//            Log.e("my", "alldata==" + allData.toString());
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

                String carNum = mCarNumber.getText().toString().trim();
                String start = mStartTime.getText().toString().trim();
                String end = mEndTime.getText().toString().trim();

                if (TextUtils.isEmpty(carNum)) {
                    try {
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                                .where("in_time", ">", dateFormatDetail.parse(start))
                                .and("out_time", "<", dateFormatDetail.parse(end))
//                            .and("car_no","=",carNum)
                                .findAll();

//                        Log.e("mm", "all.size==" + allData.size());

                        if (all != null && all.size() > 0) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            if (allData != null) {

                                allData.clear();
                                String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
                                Date date = DateUtils.string2DateDetail(today);
                                allData = db.selector(TrafficInfoTable.class)
                                        .where("update_time", ">", date)
                                        .findAll();
                                myAdapter.notifyDataSetChanged();
                            }

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
                                .and("car_no", "=", carNum)
                                .findAll();

//                        Log.e("mm", "all.size==" + allData.size());

                        if (all != null && all.size() > 0) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            if (allData != null) {

                                allData.clear();
                                String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
//                                Log.e("ende", "today==" + today);
                                Date date = DateUtils.string2DateDetail(today);
                                allData = db.selector(TrafficInfoTable.class)
                                        .where("update_time", ">", date)
                                        .findAll();
                                myAdapter.notifyDataSetChanged();
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


    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if(pvTime2.isShowing()||pvTime.isShowing()){
//                pvTime2.dismiss();
//                pvTime.dismiss();
//                return true;
//            }
//            if(pvTime.isShowing()){
//                pvTime.dismiss();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = View.inflate(getActivity(), R.layout.search_car_info, false);
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_traffic, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            TrafficInfoTable traffic = allData.get(position);
            holder.Id.setText(position + 1 + "");
            holder.Carnum.setText(traffic.getCar_no());
            holder.Type.setText(traffic.getCard_type());
            Date in_time = traffic.getIn_time();
            if (traffic.getIn_time() == null) {
                Log.e("ende", "in_time==null");
            }
            if (traffic.getIn_time() != null) {
                Log.e("ende", "in_time!=null");
            }
            if (traffic.getIn_time() != null) {
                holder.Starttime.setText(dateFormatDetail.format(traffic.getIn_time()));
            } else {
                holder.Starttime.setText("未入场");
            }
            if (traffic.getOut_time() != null) {
                holder.Endtime.setText(dateFormatDetail.format(traffic.getOut_time()));
            } else {
                holder.Endtime.setText("未出场");
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
