package com.gz.gzcar.settingfragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gz.gzcar.Database.CarWeiTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.settings.ParkingAddActivity;
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
 * 车位管理
 */
public class ParkingManagerFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.parking_add)
    Button mAdd;
    @Bind(R.id.recyclerview)
    RecyclerView rcy;


    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<CarWeiTable> allData;
    private MyAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parking_manager, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    private void initViews() {

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);

        myAdapter = new MyAdapter();
        rcy.setAdapter(myAdapter);


    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHoldder> {


        @Override
        public MyViewHoldder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_car_wei, parent, false);
            MyViewHoldder myViewHoldder = new MyViewHoldder(itemView);
            return myViewHoldder;
        }

        @Override
        public void onBindViewHolder(MyViewHoldder holder, final int position) {
            holder.mId.setText(position + 1 + "");
            holder.mInfo.setText(allData.get(position).getPrint_code() + allData.get(position).getId());

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    showDelete(allData.get(position).getId());
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return allData.size();
        }
    }

    class MyViewHoldder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_carwei_id)
        TextView mId;
        @Bind(R.id.item_carwei_info)
        TextView mInfo;

        public MyViewHoldder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void initData() {


        try {
            allData = db.selector(CarWeiTable.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getActivity(), "查询全部异常");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        initData();
        if (allData != null) {

            initViews();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.parking_add})
    public void onClick(View view) {

        startActivity(new Intent(getActivity(), ParkingAddActivity.class));

    }

    public void showDelete(final int id) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("确认删除该条信息?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            db.deleteById(CarWeiTable.class, id);
                            allData.clear();
                            allData.addAll(db.findAll(CarWeiTable.class));
                            myAdapter.notifyDataSetChanged();
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("取消", null);
        builder.show();
    }

}
