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
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.BaseFragment;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.ParseException;
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
    private DbManager db = x.getDb(MyApplication.daoConfig);
    private MyAdapter myAdapter;
    private List<TrafficInfoTable> allData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_run, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    @Override
    public void onResume() {
        super.onResume();

        initViews();
    }

    private void initViews() {

        //时间选择器
        initTime(getContext(),mStartTime, mEndTime);

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
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).findAll();
                        if (allData!=null){

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


        try {
            allData = db.selector(TrafficInfoTable.class).findAll();
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
                                .where("in_time", ">", dateFormat.parse(start))
                                .and("out_time", "<", dateFormat.parse(end))
//                            .and("car_no","=",carNum)
                                .findAll();

//                        Log.e("mm", "all.size==" + allData.size());

                        if (all!=null&&all.size() > 0) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            if (allData != null) {

                                allData.clear();
                                allData = db.selector(TrafficInfoTable.class).findAll();
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
                                .where("in_time", ">", dateFormat.parse(start))
                                .and("out_time", "<", dateFormat.parse(end))
                                .and("car_no", "=", carNum)
                                .findAll();

//                        Log.e("mm", "all.size==" + allData.size());

                        if (all!=null&&all.size() > 0) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                            if (allData!=null){

                                allData.clear();
                                allData = db.selector(TrafficInfoTable.class).findAll();
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
            if (traffic.getIn_time() != null) {
                holder.Starttime.setText(dateFormat.format(traffic.getIn_time()));
            }            else{
                holder.Starttime.setText("无入场记录");
            }
            if (traffic.getOut_time() != null) {
                holder.Endtime.setText(dateFormat.format(traffic.getOut_time()));
            }else{
                holder.Endtime.setText("无出场记录");
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
