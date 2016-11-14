package com.gz.gzcar;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.L;
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

// 选车出场
public class SelectPassOut extends BaseActivity {

    @Bind(R.id.out_carnum)
    EditText outCarnum;
    @Bind(R.id.select_out_car_rcy)
    RecyclerView rcy;
    @Bind(R.id.out_photo)
    ImageView outPhoto;
    @Bind(R.id.out_ph_carnum)
    TextView outPhCarnum;
    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<TrafficInfoTable> allData = new ArrayList<>();
    private int clickItem = -1;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pass_out);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        try {
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).where("car_no", "=", "无牌").and("status", "=", "已入").orderBy("id", true).findAll();

            if (all != null) {
                allData.addAll(all);
            }
            initViews();

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        rcy.setAdapter(myAdapter);
        outCarnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String carNum = outCarnum.getText().toString().trim();
                if (TextUtils.isEmpty(carNum)) {
                    try {
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).where("car_no", "like", "%"+carNum+"%").and("status", "=", "已入").findAll();
                        if (all != null) {
                            allData.addAll(all);
                        }
                        myAdapter.notifyDataSetChanged();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).where("car_no", "=", "%"+carNum+"%").and("status", "=", "已入").findAll();
                        if (all != null) {
                            allData.addAll(all);
                        }
                        myAdapter.notifyDataSetChanged();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @OnClick({R.id.out_nocarnum, R.id.out_1hour, R.id.out_2hour, R.id.out_4hour, R.id.out_all_car, R.id.out_no_pass, R.id.out_cancel, R.id.out_ok})
    public void onClick(View view) {
        Intent intent = new Intent();
        int id;
        switch (view.getId()) {
            case R.id.out_nocarnum:
                try {
                    allData.clear();
                    List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).where("car_no", "=", "无牌").and("status", "=", "已入").orderBy("in_time", true).findAll();
                    if (all == null) {
                        T.showShort(this, "未查到相关数据");
                    } else {
                        T.showShort(this, "找到" + all.size() + "条相关数据");
                        allData.addAll(all);
                    }
                    myAdapter.notifyDataSetChanged();
                } catch (DbException e) {
                    T.showShort(this, "查询异常");
                    e.printStackTrace();
                }
                break;
            case R.id.out_1hour:
                searchWithTime(1, 0);
                break;
            case R.id.out_2hour:
                searchWithTime(2, 1);
                break;
            case R.id.out_4hour:
                searchWithTime(4, 2);
                break;
            case R.id.out_all_car:
                searchWithTime(24, 0);
                break;
            case R.id.out_no_pass:
                try {
                    allData.clear();
                    List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).where("status", "=", "已入").and("car_type", "=", "临时车").orderBy("in_time", true).findAll();
                    if (all != null) {
                        T.showShort(this, "找到" + all.size() + "条相关数据");
                        allData.addAll(all);
                    } else {
                        T.showShort(this, "未查到相关数据");
                    }
                    myAdapter.notifyDataSetChanged();
                } catch (DbException e) {
                    T.showShort(this, "查询异常");
                    e.printStackTrace();
                }
                break;
            case R.id.out_cancel:
                finish();//结束当前的activity的生命周期
                break;
            case R.id.out_ok:
                if (clickItem == -1) {
                    T.showShort(this, "未选择车辆");
                    return;
                } else {
                    id = allData.get(clickItem).getId();
                    intent = new Intent();
                    intent.putExtra("id", id);
                    //通过Intent对象返回结果，调用setResult方法
                    setResult(1, intent);
                    finish();//结束当前的activity的生命周期
                }
                break;
        }
    }

    private void searchWithTime(int start, int end) {
        Date befor = DateUtils.string2DateDetail(DateUtils.date2StringDetail(new Date(System.currentTimeMillis() - start * 60 * 60 * 1000)));
        Date current = DateUtils.string2DateDetail(DateUtils.date2StringDetail(new Date(System.currentTimeMillis() - end * 60 * 60 * 1000 + 60 * 1000)));
        Log.e("ende", "befor==" + befor + "：：current==" + current);
        try {
            allData.clear();
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                    .where("in_time", ">", befor)
                    .and("in_time", "<", current)
                    .and("car_type", "=", "临时车")
                    .and("status", "=", "已入")
                    .orderBy("in_time", true)
                    .findAll();
            if (all != null) {
                allData.addAll(all);
                T.showShort(this, "找到" + allData.size() + "条相关数据");
            } else {
                T.showShort(this, "未查到相关数据");
            }
            myAdapter.notifyDataSetChanged();
        } catch (DbException e) {
            T.showShort(this, "查询异常");
            e.printStackTrace();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(SelectPassOut.this).inflate(R.layout.item_pass_out, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            if (clickItem != -1) {
                if (clickItem == position) {
                    holder.root.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    holder.root.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }
            holder.mCarNum.setText(allData.get(position).getCar_no());
            holder.mInTime.setText(DateUtils.date2StringDetail(allData.get(position).getIn_time()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem = position;
                    myAdapter.notifyDataSetChanged();
                    String picPath = allData.get(position).getIn_image() + "";
                    if (!TextUtils.isEmpty(picPath)) {

                        if (picPath.length() < 30&&picPath.length()>10) {
                            // 网络图片
                            String path = picPath.substring(0, 10);
                            String serverPath = MyApplication.settingInfo.getString("serverIp", "") + "car_images/" + path + "/" + picPath;
                            L.showlogError("serverPath==" + serverPath);
                            Glide.with(SelectPassOut.this).load(serverPath).error(R.drawable.ic_img_car).into(outPhoto);

                        } else {
                            // 本地图片
                            Glide.with(SelectPassOut.this).load(picPath).error(R.drawable.ic_img_car).into(outPhoto);
                        }
                    }
                    outPhCarnum.setText(allData.get(position).getCar_no() + "");
                }
            });
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_pass_out_carnum)
        TextView mCarNum;
        @Bind(R.id.item_pass_out_intime)
        TextView mInTime;
        @Bind(R.id.root)
        LinearLayout root;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
