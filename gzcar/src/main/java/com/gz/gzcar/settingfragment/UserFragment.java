package com.gz.gzcar.settingfragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gz.gzcar.BaseFragment;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.settings.UserAddActivity;
import com.gz.gzcar.settings.UserUpdateActivity;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Endeavor on 2016/8/8.
 * 用户管理
 */
public class UserFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.user_add)
    Button mBtnAdd;
    @Bind(R.id.recyclerview)
    RecyclerView rcy;
    @Bind(R.id.tv_bottom)
    TextView mBottom;


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

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);


        if (allData != null) {

            myAdapter = new MyAdapter();
            rcy.setAdapter(myAdapter);
        }

    }

    private void initData() {

        try {
            allData = db.selector(UserTable.class).orderBy("id", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            t.showShort(getActivity(), "查询全部异常");
        }
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_user, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(itemView);

            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.mName.setText(allData.get(position).getUserName());
            if(allData.get(position).getType().equals("system")) {
                holder.mType.setText("管理员");
            }else{
                holder.mType.setText("操作员");
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    showDelete(allData.get(position).getId());
                    return true;

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserUpdateActivity.class);
                    intent.putExtra("userName", allData.get(position).getUserName());
                    intent.putExtra("type", allData.get(position).getType());
                    intent.putExtra("password", allData.get(position).getPassword());
                    intent.putExtra("id", allData.get(position).getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_user_name)
        TextView mName;
        @Bind(R.id.item_user_type)
        TextView mType;

        public MyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();

        if (allData != null && allData.size() > 0) {
            initViews();
        }
        MySumTask mySumTask = new MySumTask();
        mySumTask.execute();
    }

    class MySumTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            try {
                List<UserTable> all = db.findAll(UserTable.class);
                return all.size() + "";
            } catch (DbException e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!TextUtils.isEmpty(s))
                mBottom.setText("用户:"+s+" 个");
        }
    }

    @Override

    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.user_add})
    public void onClick(View view) {
        startActivity(new Intent(getActivity(), UserAddActivity.class));
    }

    public void showDelete(final int id) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("确认删除该条信息?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            db.deleteById(UserTable.class, id);
                            allData.clear();
                            allData.addAll(db.findAll(UserTable.class));
                            myAdapter.notifyDataSetChanged();
                            new MySumTask().execute();
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("取消", null);
        builder.show();
    }

}
