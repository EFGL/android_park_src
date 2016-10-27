package com.gz.gzcar.searchfragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    @Bind(R.id.tv_number)
    TextView mBottomCarNumber;

    private View view;
    private DbManager db = null;
    private MyAdapter myAdapter;
    private List<TrafficInfoTable> allData;
    private MyPullText myPullText;
    private RecyclerView.LayoutManager lm;
    private List<TrafficInfoTable> all;
    private String TAG = "chenghao";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search_run, container, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, view);
        initSpinner();
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        String start = DateUtils.getCurrentYear() + "-" + DateUtils.getCurrentMonth() + "-" + DateUtils.getCurrentDay() + " 00:00";
        String end = DateUtils.getCurrentDataDetailStr();
        mStartTime.setText(start);
        mEndTime.setText(end);
        lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        staartThreadgetmessage();
        myAdapter = new MyAdapter();
        rcy.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) lm;
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
                Log.e("ende", "lastVisibleItemPosition....." + lastVisibleItemPosition);
                if (newState == 0 && lastVisibleItemPosition == (allData.size() - 1)) {
                    setallmessage();
                }
            }
        });
    }


    public void staartThreadgetmessage() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.obj = timer;
                handler.sendMessage(message);
            }
        }, 0);
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //来收数据
            Log.e("chenghao", "shoudaoxiaoxi");
            Timer timer = (Timer) msg.obj;
            timer.cancel();
            initData();
            if (allData != null) {
                setallmessage();
                rcy.setAdapter(myAdapter);
            }
        }
    };


    public void setallmessage() {

        if (allData != null && all != null && all.size() != 0) {
            if (allData.size() == all.size()) {
                T.showShort(getContext(), "已经全部加载完");
                return;
            }
            int alldatesize = allData.size();
            int myallsize = all.size();
            Log.e("ende", "setallmessage:chufa alldatesize=" + alldatesize);
            if (alldatesize == 0) {
                if ((alldatesize + 100) > myallsize) {
                    int index = myallsize - alldatesize;
                    for (int i = alldatesize; i < alldatesize + index; i++) {
                        allData.add(all.get(i));
                    }
                } else {
                    for (int c = 0; c < 100; c++) {
                        allData.add(all.get(c));
                    }
                }
            } else {

                if ((alldatesize + 100) > myallsize) {
                    int index = myallsize - alldatesize;
                    for (int i = alldatesize; i < alldatesize + index; i++) {
                        allData.add(all.get(i));
                    }
                } else {
                    for (int i = alldatesize; i < alldatesize + 100; i++) {
                        allData.add(all.get(i));
                    }
                }
            }
            //走更新方法
            myAdapter.notifyDataSetChanged();

        }
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
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        allData = new ArrayList<TrafficInfoTable>();
        allData = new ArrayList<TrafficInfoTable>();
        if (db == null) {
            db = org.xutils.x.getDb(MyApplication.daoConfig);
        }
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
                        if (allData != null && all != null) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                            sumBottomCarNum(allData.size());
                        } else {
                            T.showShort(getContext(), "未查到相关数据");
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void initData() {
        String today = DateUtils.date2String(DateUtils.getCurrentData()) + " 00:00";
        Date date = DateUtils.string2DateDetail(today);
        try {
            all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", date)
                    .orderBy("update_time", true)
                    .findAll();
            if (all != null) {
                sumBottomCarNum(all.size());
            }

        } catch (DbException e) {
            T.showShort(getActivity(), "查询异常");
            e.printStackTrace();
        }
    }

    public void updaterecycltviewadapter() {
        Log.e("chenghao", "updaterecycltviewadapter: changdu=" + all.size());
        if (all.size() != 0) {
            allData.clear();
            setallmessage();
            sumBottomCarNum(all.size());
        } else {
            allData.clear();
            myAdapter.notifyDataSetChanged();
            sumBottomCarNum(0);
            T.showShort(getContext(), "未查到相关数据");
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
                searchAllWithCarNum(start, end, carNum);
            }
        } else {
            if (TextUtils.isEmpty(carNum)) {
                searchWithType(start, end, type);
            } else {
                searchAllWithCarNum(start, end, carNum);
            }
        }
        return;
    }

    /*按类型查找记录*/
    private void searchWithType(String start, String end, String type) {
        try {
            all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", DateUtils.string2DateDetail(start))
                    .and("update_time", "<", DateUtils.string2DateDetail(end))
                    .and("car_type", "=", type)
                    .orderBy("update_time", true)
                    .findAll();

            updaterecycltviewadapter();
//            if (all != null) {
//                allData.clear();
//                allData.addAll(all);
//                myAdapter.notifyDataSetChanged();
//            } else {
//                T.showShort(getContext(), "未查到相关数据");
//            }
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(), "查询异常");
        }
    }

    /*查询所有车*/
    private void searchAll(String start, String end) {
        try {
            all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", DateUtils.string2DateDetail(start))
                    .and("update_time", "<", DateUtils.string2DateDetail(end))
                    .orderBy("id", true)
                    .findAll();
            updaterecycltviewadapter();


//            if (allData != null&&all!=null&&all.size()>0) {
//                allData.clear();
//                allData.addAll(all);
//                myAdapter.notifyDataSetChanged();
//                sumBottomCarNum(allData.size());
//            }else {
//                T.showShort(getContext(),"未查到相关数据");
//            }
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(), "查询异常");
        }
    }

    private void searchAllWithCarNum(String start, String end, String number) {
        try {
            all = db.selector(TrafficInfoTable.class)
                    .where("update_time", ">", DateUtils.string2DateDetail(start))
                    .and("update_time", "<", DateUtils.string2DateDetail(end))
                    .and("car_no", "=", number)
                    .orderBy("id", true)
                    .findAll();

            updaterecycltviewadapter();

//            if (allData != null && all != null && all.size() > 0) {
//                allData.clear();
//                allData.addAll(all);
//                myAdapter.notifyDataSetChanged();
//                sumBottomCarNum(allData.size());
//            } else {
//                T.showShort(getContext(), "未查到相关数据");
//            }
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
//            Log.e("ende", "onBindViewHolder: "+position);
            final TrafficInfoTable traffic = allData.get(position);
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
                } else {
                    holder.Endtime.setText("未出场");
                    holder.Endtime.setTextColor(Color.BLACK);
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ImageDetailActivity.class);
                    intent.putExtra("in_image",traffic.getIn_image()+"");
                    intent.putExtra("out_image",traffic.getOut_image()+"");

                    intent.putExtra("carNumber", traffic.getCar_no()+"");
                    intent.putExtra("carType", traffic.getCar_type()+"");

                    intent.putExtra("stall", traffic.getStall()+"");//占用车位
                    intent.putExtra("receivable", traffic.getReceivable()+"");//应收费用
                    intent.putExtra("actual_money", traffic.getActual_money()+"");//实收费用
                    intent.putExtra("status", traffic.getStatus()+"");  //通行状态
                    intent.putExtra("out_user", traffic.getOut_user()+"");
                    intent.putExtra("in_user", traffic.getIn_user()+"");


                    intent.putExtra("in_time", DateUtils.date2StringDetail(traffic.getIn_time()));
                    intent.putExtra("out_time", DateUtils.date2StringDetail(traffic.getOut_time()));
                    intent.putExtra("stall_time", traffic.getStall_time()+"");//停车时长
                    intent.putExtra("update_time", DateUtils.date2StringDetail(traffic.getUpdateTime()));
                    startActivity(intent);
                }
            });
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

    private void sumBottomCarNum(int size) {
        mBottomCarNumber.setText("车辆总数:" + size + "辆");
    }
}
