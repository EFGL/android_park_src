package com.gz.gzcar.searchfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.DateUtils;
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

/**
 * Created by Endeavor on 2016/8/8.
 * <p>
 * 车辆信息查询
 */
public class CarInfoFragment extends Fragment {

    @Bind(R.id.et_search_car)
    EditText mCarNumber;
    @Bind(R.id.btn_search_car)
    Button mSearchButton;
    @Bind(R.id.tv_number)
    TextView mBottomCarNumber;
    private DbManager db = x.getDb(MyApplication.daoConfig);
    private RecyclerView rcy;
    private View view;
    private List<CarInfoTable> allData = new ArrayList<>();
    private MyAdapter myAdapter;
    private int pageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_car, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_search_car)
    public void onClick() {
        String carNum = mCarNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(carNum) && carNum.length() > 0) {
            try {
                List<CarInfoTable> carNumList = db.selector(CarInfoTable.class).where("car_no", "like", "%"+carNum+"%").orderBy("id", true).findAll();
                if (allData != null) {
                    allData.clear();
                    allData.addAll(carNumList);
                    myAdapter.notifyDataSetChanged();
                    sumBottomCarNum(allData.size());
                }
            } catch (DbException e) {
                T.showShort(getActivity(), "查询异常");
                e.printStackTrace();
            }
        } else {
            T.showShort(getActivity(), "请输入正确的车牌号码");
        }
    }


    private void initData() {
        loadMore(pageIndex);
    }

    private void loadMore(int pageIndex) {
        try {
            List<CarInfoTable> more = db.selector(CarInfoTable.class).limit(15).offset(pageIndex * 15).orderBy("id", true).findAll();
            if (more!=null&&more.size() > 0) {
                allData.addAll(more);
                if (myAdapter != null)
                    myAdapter.notifyDataSetChanged();
            } else {
                T.showShort(getContext(), "没有更多数据了");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        pageIndex = 0;
        allData.clear();
        if (myAdapter != null)
            myAdapter.notifyDataSetChanged();
        initData();
        initViews();
    }

    private void initViews() {

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
                        List<CarInfoTable> all = db.selector(CarInfoTable.class).orderBy("id", true).findAll();
                        if (all != null) {
                            allData.clear();
                            allData.addAll(all);
                            myAdapter.notifyDataSetChanged();
                            sumBottomCarNum(allData.size());
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        rcy = (RecyclerView) view.findViewById(R.id.search_car_info_recyclerview);
        final LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        rcy.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                if (lastVisibleItemPosition == (allData.size() - 1) && newState == 0) {
                    pageIndex += 1;
                    loadMore(pageIndex);
                }

            }
        });
        if (allData != null) {

            myAdapter = new MyAdapter();
            rcy.setAdapter(myAdapter);
            sumBottomCarNum(allData.size());
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_car_info, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
//            if(clickItem!=-1){
//                if(clickItem==position){
//                    holder.mRoot.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                }else{
//                    holder.mRoot.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//                }
//            }
            final CarInfoTable carInfo = allData.get(position);
            holder.id.setText(position + 1 + "");
            holder.carNum.setText(carInfo.getCar_no());
            if( carInfo.getVehicle_type().equals("固定车")) {
                holder.cartype.setText(carInfo.getCar_type());
            }
            else{
                holder.cartype.setText(carInfo.getFee_type());
            }
            try {
                List<CarWeiBindTable> all = db.selector(CarWeiBindTable.class).where("car_no ", "=", carInfo.getCar_no()).orderBy("id", true).findAll();
                if (all != null) {
                    holder.carwei.setText(all.size() + "个");
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            holder.person.setText(carInfo.getPerson_name());
            holder.phone.setText(carInfo.getPerson_tel());
            Date start_date = carInfo.getStart_date();
            Date stop_date = carInfo.getStop_date();
//            Date created_at = carInfo.getCreated_at();
//            Date updated_at = carInfo.getUpdated_at();

//            final Intent intent = new Intent(getContext(), ImageDetailActivity.class);
//            intent.putExtra("carNumber", carInfo.getCar_no());
//            intent.putExtra("carType", carInfo.getCar_type());
//            intent.putExtra("personName", carInfo.getPerson_name());

//            if (created_at != null)
//                intent.putExtra("createTime", DateUtils.date2String(created_at));// 创建时间
//            else
//                intent.putExtra("createTime", "");
//            if (updated_at != null)
//                intent.putExtra("updateTime", DateUtils.date2String(updated_at));// 更新时间
//            else
//                intent.putExtra("createTime", "");


            if (start_date != null) {

                holder.startTime.setText(DateUtils.date2String(start_date));
//                intent.putExtra("startTime", DateUtils.date2String(start_date));
            }

            if (stop_date != null) {

                holder.endTime.setText(DateUtils.date2String(stop_date));
//                intent.putExtra("endTime", DateUtils.date2String(stop_date));
            }
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    clickItem=position;
////                    myAdapter.notifyDataSetChanged();
//
//                    startActivity(intent);
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return allData.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView carNum;
        private TextView cartype;
        private TextView carwei;
        private TextView person;
        private TextView phone;
        private TextView startTime;
        private TextView endTime;
        private TextView id;
        private LinearLayout mRoot;

        public MyHolder(View itemView) {
            super(itemView);

            mRoot = (LinearLayout) itemView.findViewById(R.id.root);
            carNum = (TextView) itemView.findViewById(R.id.search_carinfo_carnum);
            cartype = (TextView) itemView.findViewById(R.id.search_carinfo_cartype);
            carwei = (TextView) itemView.findViewById(R.id.search_carinfo_carwei);
            person = (TextView) itemView.findViewById(R.id.search_carinfo_person);
            phone = (TextView) itemView.findViewById(R.id.search_carinfo_phone);
            startTime = (TextView) itemView.findViewById(R.id.search_carinfo_starttime);
            endTime = (TextView) itemView.findViewById(R.id.search_carinfo_endtime);
            id = (TextView) itemView.findViewById(R.id.search_carinfo_id);
        }
    }


    private void sumBottomCarNum(int size) {
        mBottomCarNumber.setText("车辆总数:" + size + "辆");
    }
}
