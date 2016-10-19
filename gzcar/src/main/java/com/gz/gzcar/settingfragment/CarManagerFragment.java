package com.gz.gzcar.settingfragment;


import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.settings.CarAdd;
import com.gz.gzcar.settings.CarUpdate;
import com.gz.gzcar.utils.DateUtils;
import com.gz.gzcar.utils.T;
import com.gz.gzcar.weight.MyPullText;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    private DbManager db = x.getDb(MyApplication.daoConfig);
    private List<CarInfoTable> allData;
    private MyAdapter myAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_car_manager, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("所有车");
        typeList.add("固定车");
        typeList.add("探亲车");
        mCarType.setPopList(typeList);
        mCarType.setText(typeList.get(0));
        initData();
        if (allData != null) {

            initViews();
        }
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
                String carNum = mCarNumber.getText().toString().trim();
                String type = mCarType.getText().toString().trim();
// TODO: 2016/10/12 0012  
                if (type.equals("所有车")) {
                    // TODO: 2016/10/12 0012
                    if (TextUtils.isEmpty(carNum)) {
                        try {
                            List<CarInfoTable> all = db.findAll(CarInfoTable.class);
                            if (allData != null && all.size() > 0) {

                                allData.clear();
                                allData.addAll(all);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                T.showShort(getContext(), "未查到相关数据");
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            List<CarInfoTable> all = db.selector(CarInfoTable.class)
                                    .where("car_no", "=", carNum)
                                    .findAll();
                            if (allData != null && all.size() > 0) {

                                allData.clear();
                                allData.addAll(all);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                T.showShort(getContext(), "未查到相关数据");
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    if (!TextUtils.isEmpty(carNum)) {
                        try {
                            List<CarInfoTable> all = db.selector(CarInfoTable.class)
                                    .where("car_no", "=", carNum)
                                    .and("car_type", "=", type)
                                    .findAll();
                            if (allData != null && all.size() > 0) {

                                allData.clear();
                                allData.addAll(all);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                T.showShort(getContext(), "未查到相关数据");
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            List<CarInfoTable> all = db.selector(CarInfoTable.class)
                                    .where("car_type", "=", type)
                                    .findAll();
                            if (allData != null && all.size() > 0) {

                                allData.clear();
                                allData.addAll(all);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                T.showShort(getContext(), "未查到相关数据");
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
//晋A8888    临时车
            }
        });

        // 根据类型查询
        mCarType.setOnTextChangedListener(new MyPullText.OnTextChangedListener() {
                                              @Override
                                              public void OnTextChanged() {
                                                  String carNum = mCarNumber.getText().toString().trim();
                                                  String type = mCarType.getText().toString().trim();

                                                  if (type.equals("所有车")) {
                                                      // TODO: 2016/10/12 0012
                                                      if (TextUtils.isEmpty(carNum)) {
                                                          try {
                                                              List<CarInfoTable> all = db.findAll(CarInfoTable.class);
                                                              if (allData != null && all.size() > 0) {

                                                                  allData.clear();
                                                                  allData.addAll(all);
                                                                  myAdapter.notifyDataSetChanged();
                                                              } else {
                                                                  T.showShort(getContext(), "未查到相关数据");
                                                              }
                                                          } catch (DbException e) {
                                                              e.printStackTrace();
                                                          }
                                                      } else {
                                                          try {
                                                              List<CarInfoTable> all = db.selector(CarInfoTable.class)
                                                                      .where("car_no", "=", carNum)
                                                                      .findAll();
                                                              if (allData != null && all.size() > 0) {

                                                                  allData.clear();
                                                                  allData.addAll(all);
                                                                  myAdapter.notifyDataSetChanged();
                                                              } else {
                                                                  T.showShort(getContext(), "未查到相关数据");
                                                              }
                                                          } catch (DbException e) {
                                                              e.printStackTrace();
                                                          }
                                                      }
                                                  } else {

                                                      if (!TextUtils.isEmpty(carNum)) {
                                                          try {
                                                              List<CarInfoTable> all = db.selector(CarInfoTable.class)
                                                                      .where("car_no", "=", carNum)
                                                                      .and("car_type", "=", type)
                                                                      .findAll();
                                                              if (allData != null && all.size() > 0) {

                                                                  allData.clear();
                                                                  allData.addAll(all);
                                                                  myAdapter.notifyDataSetChanged();
                                                              } else {
                                                                  T.showShort(getContext(), "未查到相关数据");
                                                              }
                                                          } catch (DbException e) {
                                                              e.printStackTrace();
                                                          }
                                                      } else {

                                                          try {
                                                              List<CarInfoTable> all = db.selector(CarInfoTable.class)
                                                                      .where("car_type", "=", type)
                                                                      .findAll();
                                                              if (allData != null && all.size() > 0) {

                                                                  allData.clear();
                                                                  allData.addAll(all);
                                                                  myAdapter.notifyDataSetChanged();
                                                              } else {
                                                                  T.showShort(getContext(), "未查到相关数据");
                                                              }
                                                          } catch (DbException e) {
                                                              e.printStackTrace();
                                                          }
                                                      }
                                                  }
                                              }
                                          }

        );
        // 根据类型查询
//        mCarType.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String carNum = mCarNumber.getText().toString().trim();
//                String type = mCarType.getText().toString().trim();
//
//                if (!TextUtils.isEmpty(type)) {
//                    if (!TextUtils.isEmpty(carNum)) {
//                        try {
//                            List<CarInfoTable> all = db.selector(CarInfoTable.class)
//                                    .where("car_no", "=", carNum)
//                                    .and("car_type", "=", type)
//                                    .findAll();
//                            if (allData != null) {
//
//                                allData.clear();
//                                allData.addAll(all);
//                                myAdapter.notifyDataSetChanged();
//                            }
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//
//                        try {
//                            List<CarInfoTable> all = db.selector(CarInfoTable.class)
//                                    .where("car_type", "=", type)
//                                    .findAll();
//                            if (allData != null) {
//
//                                allData.clear();
//                                allData.addAll(all);
//                            }
////                        Toast.makeText(getContext(),"all="+all.size()+";;allData="+allData.size(),Toast.LENGTH_SHORT).show();
//
//                            myAdapter.notifyDataSetChanged();
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else if (!TextUtils.isEmpty(carNum)) {
//                    try {
//                        List<CarInfoTable> all = db.selector(CarInfoTable.class)
//                                .where("car_no", "=", carNum)
//                                .findAll();
//                        allData.clear();
//                        allData.addAll(all);
////                        Toast.makeText(getContext(),"all="+all.size()+";;allData="+allData.size(),Toast.LENGTH_SHORT).show();
//
//                        myAdapter.notifyDataSetChanged();
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        List<CarInfoTable> all = db.selector(CarInfoTable.class).findAll();
//                        allData.clear();
//                        allData.addAll(all);
//                        myAdapter.notifyDataSetChanged();
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            }
//        });

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcy.setLayoutManager(lm);
        myAdapter = new

                MyAdapter();

        rcy.setAdapter(myAdapter);

    }

    private void initData() {

        try {
            allData = db.selector(CarInfoTable.class).findAll();
               //Collections.sort(allData);
        } catch (DbException e) {
            e.printStackTrace();
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
        public void onBindViewHolder(MyHolder holder, int position) {

            final CarInfoTable carInfo = allData.get(position);
            holder.mId.setText(position + 1 + "");
            holder.mCarNumber.setText(carInfo.getCar_no());
            try {
                List<CarWeiBindTable> all = db.selector(CarWeiBindTable.class).where("car_number", "=", carInfo.getCar_no()).findAll();
                if (all != null) {
                    holder.mCarWei.setText(all.size() + "个");
                }
            } catch (DbException e) {
                e.printStackTrace();
            }

            holder.mType.setText(carInfo.getCar_type());
//            holder.mCarWei.setText(carInfo.getCarWei());
            holder.mPerson.setText(carInfo.getPerson_name());
            holder.mPhone.setText(carInfo.getPerson_tel());
//            holder.mAddress.setText(carInfo.getPerson_address());

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
                    i.putExtra("carType", carInfo.getCar_type());
//                    i.putExtra("carWei", carInfo.getCarWei());
                    i.putExtra("person", carInfo.getPerson_name());
                    i.putExtra("phone", carInfo.getPerson_tel());
                    i.putExtra("address", carInfo.getPerson_address());
                    i.putExtra("startTime", start);
                    i.putExtra("endTime", end);
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
                            allData.addAll(db.findAll(CarInfoTable.class));
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
