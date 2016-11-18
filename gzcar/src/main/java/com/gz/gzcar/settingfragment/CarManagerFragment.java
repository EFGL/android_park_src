package com.gz.gzcar.settingfragment;


import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.settings.CarAdd;
import com.gz.gzcar.settings.CarUpdate;
import com.gz.gzcar.utils.CsvReader;
import com.gz.gzcar.utils.CsvWriter;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.L;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStorageDirectory;

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
    @Bind(R.id.exp_progress)
    ProgressBar progressBar;
    private int TAG = 0;
    private String type = "";
    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<CarInfoTable> allData = new ArrayList<>();
    private MyAdapter myAdapter;
    private AlertDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_car_manager, container, false);

        ButterKnife.bind(this, view);

        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("所有车");
        typeList.add("固定车");
        typeList.add("其它车");
        mCarType.setPopList(typeList);
        mCarType.setText(typeList.get(0));

        return view;
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
                L.showlogError("lastVisibleItemPosition===" + lastVisibleItemPosition);
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


    class ExportTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            CsvWriter cw = null;

            try {
                L.showlogError("--- 开始查询数据库 ---");
                List<CarInfoTable> all = db.findAll(CarInfoTable.class);

                if (all != null && all.size() > 0) {

                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                    String current = format.format(System.currentTimeMillis());
                    String fileName = "/" + current + ".csv";
                    String usbDir = "/storage/uhost";
                    String usbDir1 = "/storage/uhost1";

                    L.showlogError("fileName===" + fileName);


                    L.showlogError("path===" + getExternalStorageDirectory().getCanonicalPath() + "/" + current + ".csv");

                    String[] title = new String[]{"id", "组键", "车号", "固定车类型", "车辆类型", "收费类型", "联系人", "电话", "地址", "有效开始时间",
                            "有效结束时间", "有效次数", "免费时长（分钟）", "创建时间", "更新时间", "状态"};
                    try {
                        cw = new CsvWriter(usbDir1 + fileName, ',', Charset.forName("GBK"));
                        cw.writeRecord(title);
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            L.showlogError("--- 1号位未找到U盘,开始检索2号位 ---");
                            cw = new CsvWriter(usbDir + fileName, ',', Charset.forName("GBK"));
                            cw.writeRecord(title);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            L.showlogError("--- 未找到U盘,停止... ---");
                            return -2;
                        }
                    }

                    L.showlogError("--- 发现U盘 ---");


                    CarInfoTable carInfoTable;
                    for (int i = 0; i < all.size(); i++) {
                        carInfoTable = all.get(i);
                        int id = carInfoTable.getId();
                        String codeId = carInfoTable.getCodeId();
                        String car_no = carInfoTable.getCar_no();
                        String car_type = carInfoTable.getCar_type();
                        String vehicle_type = carInfoTable.getVehicle_type();
                        String fee_type = carInfoTable.getFee_type();
                        String person_name = carInfoTable.getPerson_name();
                        String person_tel = carInfoTable.getPerson_tel();
                        String person_address = carInfoTable.getPerson_address();
                        Date start_date = carInfoTable.getStart_date();
                        Date stop_date = carInfoTable.getStop_date();
                        int allow_count = carInfoTable.getAllow_count();
                        int allow_park_time = carInfoTable.getAllow_park_time();
                        Date created_at = carInfoTable.getCreated_at();
                        String updated_at = carInfoTable.getUpdated_at();
                        String status = carInfoTable.getStatus();

                        String[] carInfo = new String[]{id + "", codeId, car_no, car_type, vehicle_type, fee_type, person_name, person_tel, person_address, DateUtils.date2String(start_date),
                                DateUtils.date2String(stop_date), allow_count + "", allow_park_time + "", DateUtils.date2String(created_at), updated_at, status};

                        cw.writeRecord(carInfo);
                        L.showlogError("数据写入成功 数据:id==" + id);
                    }
                    return all.size();
                } else {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
                if (cw != null)
                    cw.close();
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            int i = integer.intValue();
            if (i == -1) {
                T.showShort(getContext(), "导出失败");
            } else if (i == -2) {
                T.showShort(getContext(), "请先插入U盘");

            } else if (i == 0) {
                T.showShort(getContext(), "暂无数据");
            } else {

                T.showShort(getContext(), "导出完成,共" + integer.toString() + "条");
            }

            progressBar.setVisibility(View.GONE);
        }
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

                    i.putExtra("vehicle_type", carInfo.getVehicle_type());// 固定车 特殊车
                    i.putExtra("carTypeDetail", carInfo.getCar_type()); //固定车详情
                    i.putExtra("fee_type", carInfo.getFee_type());//  特殊车详情
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


    @OnClick({R.id.car_add, R.id.car_export, R.id.car_import})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.car_add:
                startActivity(new Intent(getActivity(), CarAdd.class));
                break;
            case R.id.car_import:
                myImport();
                break;
            case R.id.car_export:
//                showExport();
                progressBar.setVisibility(View.VISIBLE);
                new ExportTask().execute();
//                File file = new File("/storage/uhost");
//                File file1 = new File("/storage/uhost1");

        }
    }

    /*导入*/
    private void myImport() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 1);
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
            String csvFilePath = getRealFilePath(getActivity(), uri);
            L.showlogError("csvFilePath==" + csvFilePath);
            String fileName = csvFilePath.substring(csvFilePath.lastIndexOf(".") + 1);
            if (!fileName.equalsIgnoreCase("csv") || csvFilePath == null) {
                T.showLong(getActivity(), "文件类型错误，导入失败！！！");
                return;
            }
            ArrayList<String[]> csvList = new ArrayList<String[]>(); // 保存数据
            CsvReader reader = null;
            try {
                reader = new CsvReader(csvFilePath, ',', Charset.forName("GBK"));
                reader.readHeaders(); // 跳过表头   如果需要表头的话，不要写这句。
                while (reader.readRecord()) { //逐行读入除表头的数据
                    csvList.add(reader.getValues());
                }
                for (int row = 0; row < csvList.size(); row++) {
                    String number = csvList.get(row)[2]; //取得第row行第2列的数据
                    L.showlogError("第"+row+"行元素个数=="+csvList.get(row).length+",车牌号==" + number);
                    CarInfoTable carInfoTable = new CarInfoTable(csvList.get(row));
                    db.save(carInfoTable);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null)
                    reader.close();
            }

        }
    }

    private String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
