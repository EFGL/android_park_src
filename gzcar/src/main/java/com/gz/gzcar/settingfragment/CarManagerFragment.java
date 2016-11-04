package com.gz.gzcar.settingfragment;


import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.settings.CarAdd;
import com.gz.gzcar.settings.CarUpdate;
import com.gz.gzcar.utils.CarInfoDbutils;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.L;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Endeavor on 2016/8/8.
 * <p/>
 * 车辆管理
 */
public class CarManagerFragment extends Fragment implements View.OnClickListener {


    @Bind(R.id.carnumber)
    EditText mCarNumber;
    @Bind(R.id.cartype)
    MyPullText mCarType;
    @Bind(R.id.setting_carmanager_recyclerview)
    RecyclerView rcy;
    @Bind(R.id.tv_bottom)
    TextView mBottomCarNumber;
    @Bind(R.id.car_import)
    Button buttonCarImport;
    private int TAG = 0;
    private String type = "";


    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<CarInfoTable> allData = new ArrayList<>();
    private MyAdapter myAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_car_manager, container, false);

        ButterKnife.bind(this, view);

        /*导入*/
        buttonCarImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 1);

                    /*Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*.csv");
                startActivityForResult(intent, 1);*/
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("所有车");
        typeList.add("固定车");
        typeList.add("其它车");
        mCarType.setPopList(typeList);
        mCarType.setText(typeList.get(0));
        pageIndex = 0;
        allData.clear();
        if (myAdapter != null)
            myAdapter.notifyDataSetChanged();
        initData();
        initViews();
        new SumTask().execute();
    }

    private void initViews() {
        // 根据车号查询
        mCarNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TAG = 0;
                String carNum = mCarNumber.getText().toString().trim();
                if (TextUtils.isEmpty(carNum)) {
                    pageIndex = 0;
                    allData.clear();
                    initData();

                    new SumTask().execute();
                } else {
                    try {

                        List<CarInfoTable> all = db.selector(CarInfoTable.class).where("car_no", "like", "%" + carNum + "%").orderBy("id", true).findAll();
                        if (all != null) {
                            allData.clear();
                            allData.addAll(all);
                            if (myAdapter != null)
                                myAdapter.notifyDataSetChanged();
                            mBottomCarNumber.setText("车辆总数:" + all.size() + " 辆");
                        } else {
                            allData.clear();
                            if (myAdapter != null)
                                myAdapter.notifyDataSetChanged();
                            mBottomCarNumber.setText("车辆总数:0 辆");
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
//晋A8888    临时车
            }
        });

        // 根据类型查询
        mCarType.setOnTextChangedListener
                (new MyPullText.OnTextChangedListener() {

                     @Override
                     public void OnTextChanged() {
                         mCarNumber.setText("");
                         type = mCarType.getText().toString().trim();
                         allData.clear();
                         pageIndex = 0;
                         if (type == "所有车") {
                             TAG = 0;
                             initData();
                             new SumTask().execute();
                         } else {
                             TAG = 1;
                             initData();
                             new SumTask().execute();
                         }

                     }
                 }

                );

        final LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        myAdapter = new MyAdapter();

        rcy.setAdapter(myAdapter);

        rcy.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                L.showlogError("lastVisibleItemPosition==="+lastVisibleItemPosition);
                if (lastVisibleItemPosition == (allData.size() - 1) && newState == 0) {
                    pageIndex += 1;
                    loadMore(pageIndex);
                }

            }
        });

    }

    private void initData() {

        loadMore(pageIndex);
    }

    private int pageIndex = 0;

    private void loadMore(int pageIndex) {

        if (TAG == 0) {
            try {
                List<CarInfoTable> more = db.selector(CarInfoTable.class).orderBy("id", true).limit(15).offset(pageIndex * 15).findAll();
                if (more != null && more.size() > 0) {
                    allData.addAll(more);
                    if (myAdapter != null)
                        myAdapter.notifyDataSetChanged();
                } else {
                    T.showShort(getContext(), "没有更多数据了");
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<CarInfoTable> more = db.selector(CarInfoTable.class)
                        .where("car_type", "=", type)
                        .limit(30)
                        .offset(pageIndex * 30).orderBy("id", true).findAll();
                if (more != null && more.size() > 0) {

                    allData.addAll(more);
                    if (myAdapter != null)
                        myAdapter.notifyDataSetChanged();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }


    class SumTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            if (TAG == 0) {
                try {
                    List<CarInfoTable> all = db.findAll(CarInfoTable.class);
                    return all.size() + "";
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                try {
                    List<CarInfoTable> all = db.selector(CarInfoTable.class)
                            .where("car_type", "=", type).findAll();
                    return all.size() + "";
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!TextUtils.isEmpty(s))
                mBottomCarNumber.setText("车辆总数:" + s + " 辆");
        }
    }

    @OnClick({R.id.car_add})
    public void onClick(View view) {
        startActivity(new Intent(getActivity(), CarAdd.class));
    }


    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        private String start = "";
        private String end = "";

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = View.inflate(getActivity(), R.layout.search_car_info, false);
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_setting_carmanager, parent, false);
            MyHolder myHolder = new MyHolder(itemView);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {

            final CarInfoTable carInfo = allData.get(position);
            holder.mId.setText(position + 1 + "");
            holder.mCarNumber.setText(carInfo.getCar_no());
            try {
                List<CarWeiBindTable> all = db.selector(CarWeiBindTable.class).where("car_no", "=", carInfo.getCar_no()).findAll();
                if (all != null) {
                    holder.mCarWei.setText(all.size() + "个");
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (carInfo.getVehicle_type().equals("固定车")) {
                holder.mType.setText(carInfo.getCar_type());
            } else {
                holder.mType.setText(carInfo.getFee_type());
            }
            holder.mPerson.setText(carInfo.getPerson_name());
            holder.mPhone.setText(carInfo.getPerson_tel());

            final Date start_date = allData.get(position).getStart_date();
            Date stop_date = allData.get(position).getStop_date();
            if (start_date != null) {
                start = DateUtils.date2String(start_date);
                holder.mStartDate.setText(start);
            }
            if (stop_date != null) {
                end = DateUtils.date2String(stop_date);
                holder.mEndDate.setText(end);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), CarUpdate.class);
                    i.putExtra("carNumber", carInfo.getCar_no());

                    i.putExtra("vehicle_type",carInfo.getVehicle_type());// 固定车 特殊车
                    i.putExtra("carTypeDetail", carInfo.getCar_type()); //固定车详情
                    i.putExtra("fee_type",carInfo.getFee_type());//  特殊车详情
                    i.putExtra("allow_count", carInfo.getAllow_count());// 免费次数
                    i.putExtra("allow_park_time", carInfo.getAllow_park_time());// 免费时长


                    i.putExtra("person", carInfo.getPerson_name());
                    i.putExtra("phone", carInfo.getPerson_tel());
                    i.putExtra("address", carInfo.getPerson_address());
                    i.putExtra("startTime", DateUtils.date2String(allData.get(position).getStart_date()));
                    i.putExtra("endTime", DateUtils.date2String(allData.get(position).getStop_date()));
//                    L.showlogError("startTime=="+s);
//                    L.showlogError("endTime=="+end);
                    i.putExtra("id", carInfo.getId());

                    startActivity(i);

                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDelete(carInfo.getId());
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return allData.size();
        }
    }


    class MyHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_setting_carmanager_id)
        TextView mId;
        @Bind(R.id.item_setting_carmanager_carnumber)
        TextView mCarNumber;
        @Bind(R.id.item_setting_carmanager_type)
        TextView mType;
        @Bind(R.id.item_setting_carmanager_carwei)
        TextView mCarWei;
        @Bind(R.id.item_setting_carmanager_person)
        TextView mPerson;
        @Bind(R.id.item_setting_carmanager_phone)
        TextView mPhone;
        @Bind(R.id.item_setting_carmanager_startdate)
        TextView mStartDate;
        @Bind(R.id.item_setting_carmanager_enddate)
        TextView mEndDate;


        public MyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void showDelete(final int id) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("确认删除该条信息?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            db.deleteById(CarInfoTable.class, id);
                            allData.clear();
                            myAdapter.notifyDataSetChanged();
//                            allData.addAll(db.selector(CarInfoTable.class).orderBy("id", true).findAll());
//                            myAdapter.notifyDataSetChanged();

                            TAG = 0;
                            pageIndex = 0;
                            initData();
                            new SumTask().execute();

                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("取消", null);
        builder.show();
    }

    /*
    功能：
    1、重载onActivityResult方法
    2、从文件管理界面中得到文件路径
    3、通过流打开文件，读取文件，拆分，插入车辆信息表中
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String fileName = uri.toString();
            T.showLong(getActivity(), fileName);
            L.showlogError("fileName===" + fileName);
            fileName = fileName.toString().substring(fileName.lastIndexOf(".") + 1);
            if (!fileName.equalsIgnoreCase("csv")) {
                T.showLong(getActivity(), "文件类型错误，导入失败！！！");
                return;
            }
             ContentResolver cr = getActivity().getContentResolver();

                BufferedReader reader=null;
                try {
                    reader= new BufferedReader(new InputStreamReader(cr.openInputStream(uri), Charset.forName("GBK")));
                    ArrayList<CarInfoTable> list=new   ArrayList<CarInfoTable>();
                    String  line= reader.readLine();
                    L.showlogInfo(line);
                    while ((line = reader.readLine())!= null)
                    {
                        String[] members=line.split(",");
                        L.showlogInfo(members.toString());
                        L.showlogError("line==="+line);
                        try {
                            CarInfoTable carInfoTable = new CarInfoTable(members);
                            list.add(carInfoTable);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            continue;
                        }
                    }
                    T.showLong(getActivity(),"已成功导入"+ CarInfoDbutils.save_carinfo(list)+"条数据！！");
                } catch (Exception e) {
                    T.showLong(getActivity(),"导入失败！！");
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
