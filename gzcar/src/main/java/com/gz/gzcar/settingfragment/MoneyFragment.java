package com.gz.gzcar.settingfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.Database.MoneyTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Endeavor on 2016/8/8.
 * <p>
 * 收费规则
 */
public class MoneyFragment extends Fragment {
    @Bind(R.id.moneyrcy_rcy)
    RecyclerView mRcy;
    @Bind(R.id.money_tv_time)
    TextView mTime;
    @Bind(R.id.money_new_money)
    EditText mNewMoney;
    @Bind(R.id.money_temp)
    MyPullText mTemp;
    @Bind(R.id.money_friends)
    MyPullText mFriends;
    @Bind(R.id.tb_free)
    JellyToggleButton tbFree;
    @Bind(R.id.tb_hour_add)
    JellyToggleButton tbHourAdd;
    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<MoneyTable> allData;
    private MyAdapter myAdapter;
    private static int id = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_money, container, false);
        ButterKnife.bind(this, v);

        ArrayList<String> templist = new ArrayList<>();
        templist.add("30");
        templist.add("60");
        templist.add("120");
        templist.add("180");
        mTemp.setPopList(templist);
        mTemp.setText(templist.get(0));

        ArrayList<String> friendlist = new ArrayList<>();
        friendlist.add("30");
        friendlist.add("60");
        friendlist.add("120");
        friendlist.add("180");
        mFriends.setPopList(friendlist);
        mFriends.setText(friendlist.get(1));

        return v;
    }

    private void initViews() {

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRcy.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        mRcy.setAdapter(myAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemview = LayoutInflater.from(getContext()).inflate(R.layout.item_money, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(itemview);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.mId.setText(position + 1 + "");
            holder.mMoney.setText(allData.get(position).getMoney() + "元");
            double start = allData.get(position).getPartTime() - 0.5;
            double time = allData.get(position).getPartTime();
            holder.mTime.setText(start + " - " + time + " 小时");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    id = allData.get(position).getId();

                    mNewMoney.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mNewMoney.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

                    String time = holder.mTime.getText().toString().trim();
                    mTime.setText(time + "");

                }
            });
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_money_id)
        TextView mId;
        @Bind(R.id.item_money_time)
        TextView mTime;
        @Bind(R.id.item_money_free)
        TextView mMoney;

        public MyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();
    }

    private void initData() {

        try {
            allData = db.findAll(MoneyTable.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (allData == null || allData.size() < 1) {
            addBaseData();
            // TODO: 2016/9/27 0027
//            initViews();
        } else {
            initViews();
        }
    }


    private void addBaseData() {
        double baseMoney = 0.00;
        double baseTime = 0.00;
        MoneyTable m;

        for (int i = 0; i < 48; i++) {

            baseTime += 0.5;
            baseMoney += 5;


            Log.i("ende", "baseTime==" + baseTime);
            Log.i("ende", "baseMoney==" + baseMoney);
            Log.e("ende", "----------------------------");

            m = new MoneyTable();
            m.setPartTime(baseTime);
            m.setMoney(baseMoney);
            try {
                db.save(m);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        try {
            allData = db.findAll(MoneyTable.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (allData != null) {
            initViews();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.save_new_money, R.id.money_save_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_new_money:

                upMoneyData();
                break;
            case R.id.money_save_update:
                break;
        }
    }

    private void upMoneyData() {

        String newMoney = mNewMoney.getText().toString().trim();
        if (TextUtils.isEmpty(newMoney)) {
            T.showShort(getContext(), "请输入新的金额");
            return;
        }
        double d = Double.parseDouble(newMoney);

        if (id != -1) {

            try {
                MoneyTable m = db.findById(MoneyTable.class, id);
                m.setMoney(d);
                db.update(m, "money");
                T.showShort(getContext(), "更新成功");
                allData.clear();
                allData.addAll(db.findAll(MoneyTable.class));
                myAdapter.notifyDataSetChanged();
            } catch (DbException e) {
                T.showShort(getContext(), "更新失败");
                e.printStackTrace();
            }
        }
    }
}
