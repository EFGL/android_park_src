package com.gz.gzcar.settingfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.settings.UserAddActivity;
import com.gz.gzcar.settings.UserUpdateActivity;
import com.gz.gzcar.utils.T;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Endeavor on 2016/8/8.
 * <p/>
 * 用户管理
 */
public class UserFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.user_add)
    Button mBtnAdd;
    @Bind(R.id.user_update)
    Button mbtnUpdate;
    @Bind(R.id.recyclerview)
    RecyclerView rcy;
    @Bind(R.id.btn_delete)
    Button mDelete;

    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<UserTable> allData;
    private MyAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);


        ButterKnife.bind(this, view);
        return view;
    }


    private void initViews() {

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);


        if (allData != null) {

            myAdapter = new MyAdapter();
            rcy.setAdapter(myAdapter);
        }

    }

    private void initData() {
        // TODO: 2016/9/9
//        addData();

        try {
            allData = db.selector(UserTable.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(), "查询全部异常");
        }
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(itemView);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.mName.setText(allData.get(position).getUserName());
            holder.mType.setText(allData.get(position).getType());

        }

        @Override
        public int getItemCount() {
            return allData.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_user_check)
        CheckBox mCheck;
        @Bind(R.id.item_user_name)
        TextView mName;
        @Bind(R.id.item_user_type)
        TextView mType;

        public MyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    private void addData() {
        try {

            UserTable userTable = new UserTable();
            userTable.setUserName("张三");
            userTable.setPassword("111");
            userTable.setType("管理员");
            db.save(userTable);

            UserTable lisi = new UserTable();
            lisi.setUserName("李四");
            lisi.setPassword("111");
            lisi.setType("操作员");
            db.save(lisi);
        } catch (Exception e) {
            T.showShort(getContext(), "添加异常");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();

        if (allData != null && allData.size() > 0) {
            initViews();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.user_add, R.id.user_update, R.id.btn_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_add:

                startActivity(new Intent(getActivity(), UserAddActivity.class));
                break;
            case R.id.user_update:

                startActivity(new Intent(getActivity(), UserUpdateActivity.class));
                break;

            case R.id.user_deleted:

                break;
        }
    }


}
