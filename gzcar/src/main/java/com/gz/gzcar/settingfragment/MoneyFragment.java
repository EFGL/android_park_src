package com.gz.gzcar.settingfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gz.gzcar.AppConstants;
import com.gz.gzcar.BaseFragment;
import com.gz.gzcar.Database.MoneyTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.SPUtils;
import com.gz.gzcar.weight.MyPullText;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;

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
public class MoneyFragment extends BaseFragment {
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
    private List<MoneyTable> allData = new ArrayList<>();
    private MyAdapter myAdapter;
    private  int id = -1;
    private SPUtils spUtils = MyApplication.settingInfo;;
    private boolean isFreeTemp = false;
    private boolean isHourAddTemp = false;

    private int clickItem = -1;
    private int pageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_money, container, false);
        ButterKnife.bind(this, v);

        ArrayList<String> templist = new ArrayList<>();
        templist.add("0");
        templist.add("30");
        templist.add("60");
        templist.add("120");
        templist.add("180");
        mTemp.setPopList(templist);
        mTemp.setText(templist.get(0));

        ArrayList<String> friendlist = new ArrayList<>();
        friendlist.add("0");
        friendlist.add("30");
        friendlist.add("60");
        friendlist.add("120");
        friendlist.add("180");
        mFriends.setPopList(friendlist);
        mFriends.setText(friendlist.get(1));

        return v;
    }

    private void saveConfig() {
        String temp = mTemp.getText();
        String friend = mFriends.getText();
        spUtils.putInt(AppConstants.TEMP_FREE,Integer.valueOf(temp));
        spUtils.putInt(AppConstants.FRIEND_FREE,Integer.valueOf(friend));
        spUtils.putBoolean(AppConstants.IS_FREE, isFreeTemp);
        spUtils.putBoolean(AppConstants.IS_HOURADD, isHourAddTemp);
        t.showShort(getActivity(), "保存成功");
    }
    private void readConfiig() {
        final int tempFree = spUtils.getInt(AppConstants.TEMP_FREE);// 临时车免费时长
        int friends = spUtils.getInt(AppConstants.FRIEND_FREE);
        final boolean isFree = spUtils.getBoolean(AppConstants.IS_FREE);// 核减免费
        final boolean isHourAdd = spUtils.getBoolean(AppConstants.IS_HOURADD);// 24h累加

        mTemp.setText(String.valueOf(tempFree));
        mFriends.setText(String.valueOf(friends));
        tbFree.setChecked(isFree);
        tbHourAdd.setChecked(isHourAdd);
        tbFree.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 否
                if (state.equals(State.LEFT)) {
                    isFreeTemp = false;
                }
                // 是
                if (state.equals(State.RIGHT)) {
                    isFreeTemp = true;
                }
            }
        });

        tbHourAdd.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 否
                if (state.equals(State.LEFT)) {
                    isHourAddTemp = false;
                }
                // 是
                if (state.equals(State.RIGHT)) {
                    isHourAddTemp = true;
                }
            }
        });
    }


    private void initViews() {

        final LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRcy.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        mRcy.setAdapter(myAdapter);

        mRcy.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                if (newState == 0 && lastVisibleItemPosition == allData.size() - 1) {
                    pageIndex += 1;
                    loadMore(pageIndex);
                }
            }
        });
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemview = LayoutInflater.from(getActivity()).inflate(R.layout.item_money, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(itemview);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            if (clickItem != -1) {
                if (clickItem == position) {
                    holder.mRoot.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    holder.mRoot.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }
            holder.mId.setText(position + 1 + "");
            holder.mType.setText(allData.get(position).getCar_type_name().toString());
            holder.mMoney.setText(allData.get(position).getMoney() + "元");
            holder.mTime_min.setText(String.format("%.1f小时", allData.get(position).getParked_min_time() / 60.0));
            holder.mTime_max.setText(String.format("%.1f小时", allData.get(position).getParked_max_time() / 60.0));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem = position;
                    myAdapter.notifyDataSetChanged();
                    id = allData.get(position).getId();
                    mNewMoney.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mNewMoney.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    String time = holder.mTime_min.getText().toString().trim() + "-" + holder.mTime_max.getText().toString().trim();
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
        @Bind(R.id.item_money_type)
        TextView mType;
        @Bind(R.id.item_money_time_min)
        TextView mTime_min;
        @Bind(R.id.item_money_time_max)
        TextView mTime_max;
        @Bind(R.id.item_money_free)
        TextView mMoney;
        @Bind(R.id.item_root)
        LinearLayout mRoot;

        public MyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        pageIndex = 0;
        allData.clear();

        readConfiig();
        initData();
        initViews();
    }


    private void initData() {

        loadMore(pageIndex);
    }

    private void loadMore(int pageIndex) {
        try {
            List<MoneyTable> all = db.selector(MoneyTable.class).orderBy("parked_min_time").limit(15).offset(15 * pageIndex).findAll();
            if (all.size() > 0) {

                allData.addAll(all);
                if (myAdapter != null)
                    myAdapter.notifyDataSetChanged();
            } else {
                t.showShort(getActivity(), "没有更多数据了");
            }
        } catch (DbException e) {
            e.printStackTrace();
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
                saveConfig();
                break;
        }
    }


    private void upMoneyData() {

        String newMoney = mNewMoney.getText().toString().trim();
        if (TextUtils.isEmpty(newMoney)) {
            t.showShort(getActivity(), "请输入新的金额");
            return;
        }
        double d = Double.parseDouble(newMoney);

        if (id != -1) {

            try {
                MoneyTable m = db.findById(MoneyTable.class, id);
                m.setMoney(d);
                db.update(m, "money");
                t.showShort(getActivity(), "更新成功");
                allData.clear();
                allData.addAll(db.findAll(MoneyTable.class));
                myAdapter.notifyDataSetChanged();
            } catch (DbException e) {
                t.showShort(getActivity(), "更新失败");
                e.printStackTrace();
            }
        }
    }
}
