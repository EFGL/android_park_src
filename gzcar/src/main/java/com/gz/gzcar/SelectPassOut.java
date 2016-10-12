package com.gz.gzcar;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private List<TrafficInfoTable> allData;
    private int clickItem = -1;
    private MyAdapter myAdapter;
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
//            allData = db.findAll(TrafficInfoTable.class);
            allData = db.selector(TrafficInfoTable.class).where("out_time", "=", null).findAll();
            Log.e("ende", "allData==" + allData.toString());
            if (allData != null && allData.size() > 0) {
                initViews();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        rcy.setAdapter(myAdapter);

    }

    @OnClick({R.id.out_nocarnum, R.id.out_1hour, R.id.out_2hour, R.id.out_4hour, R.id.out_all_car, R.id.out_no_pass, R.id.out_cancel, R.id.out_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.out_nocarnum:

                try {
                    List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class).where("car_no", "=", null).findAll();
                    if (allData==null||all==null||all.size()<1){
                        T.showShort(this,"未查到相关数据");
                    }else {
                        allData.clear();
                        allData.addAll(all);
                        myAdapter.notifyDataSetChanged();
                    }
                } catch (DbException e) {
                    T.showShort(this, "查询异常");
                    e.printStackTrace();
                }

                break;
            case R.id.out_1hour:
                T.showShort(this, "1小时内");

                searchWithTime(1,0);
                break;
            case R.id.out_2hour:
                T.showShort(this, "1-2小时内");
                searchWithTime(2,1);
                break;
            case R.id.out_4hour:
                T.showShort(this, "2-4小时内");
                searchWithTime(4,2);
                break;
            case R.id.out_all_car:
                T.showShort(this, "今日所有车辆");

                break;
            case R.id.out_no_pass:
                T.showShort(this, "所有未出场车辆");

                break;
            case R.id.out_cancel:
                finish();
                break;
            case R.id.out_ok:
                T.showShort(this, "确认出场");
                break;
        }
    }

    private void searchWithTime(int start,int end) {
        Date befor = DateUtils.string2DateDetail(DateUtils.date2StringDetail(new Date(System.currentTimeMillis()-start*60*60*1000)));
        Date current = DateUtils.string2DateDetail(DateUtils.date2StringDetail(new Date(System.currentTimeMillis()-end*60*60*1000)));
        Log.e("ende","befor=="+befor+"：：current=="+current);

        try {
            List<TrafficInfoTable> all = db.selector(TrafficInfoTable.class)
                    .where("in_time", ">",befor)
                    .and("in_time", "<", current)
                    .findAll();
            if (allData==null||all==null||all.size()<1){
                T.showShort(this,"未查到相关数据");
            }else {
                allData.clear();
                allData.addAll(all);
                myAdapter.notifyDataSetChanged();
            }
        } catch (DbException e) {
            T.showShort(this,"查询异常");
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

            if(clickItem!=-1){
                if(clickItem==position){
                    holder.root.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    holder.root.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }

            holder.mCarNum.setText(allData.get(position).getCar_no());
            holder.mInTime.setText(DateUtils.date2StringDetail(allData.get(position).getIn_time()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem=position;
                    myAdapter.notifyDataSetChanged();
                    Glide.with(SelectPassOut.this).load(allData.get(position).getIn_image()).error(R.drawable.ic_img_car).into(outPhoto);
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
