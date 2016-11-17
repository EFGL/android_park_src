package com.gz.gzcar.settingfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gz.gzcar.AppConstants;
import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.CarWeiBindTable;
import com.gz.gzcar.Database.CarWeiTable;
import com.gz.gzcar.Database.FreeInfoTable;
import com.gz.gzcar.Database.MoneyTable;
import com.gz.gzcar.Database.TrafficInfoTable;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MainActivity;
import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.server.DownloadTimeBean;
import com.gz.gzcar.utils.InitUtils;
import com.gz.gzcar.utils.T;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Endeavor on 2016/8/8.
 * <p/>
 * 系统设置
 */
public class SettingsFragment extends Fragment {

    @Bind(R.id.et_server_address)
    EditText mServerAddress;
    @Bind(R.id.et_in_ip)
    EditText mInIp;
    @Bind(R.id.et_out_ip)
    EditText mOutIp;
    @Bind(R.id.editText_stallNumber)
    EditText stallNumber;
    @Bind(R.id.editText_enter_delay)
    EditText editText_enterDelay;
    @Bind(R.id.tb_togglebutton1)
    JellyToggleButton mTogglebutton1;
    @Bind(R.id.tb_togglebutton2)
    JellyToggleButton mTogglebutton2;
    @Bind(R.id.tb_togglebutton3)
    JellyToggleButton mTogglebutton3;
    @Bind(R.id.tb_togglebutton4)
    JellyToggleButton mTogglebutton4;
    @Bind(R.id.tb_togglebutton_carNumber)
    JellyToggleButton getmTogglebuttonCarNumber;
    @Bind(R.id.btn_save_update)
    Button mSave;
    @Bind(R.id.textViewDevId)
    TextView textViewDevId;
    @Bind(R.id.company)
    EditText mCompany;

    private boolean isTempCarIn;
    private boolean isFree;
    private boolean isPrint;
    private boolean isCardRead;
    private boolean isChina;
    private int enterDelay;

    private DbManager db = x.getDb(MyApplication.daoConfig);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readConfig();
    }


    private void readConfig() {

        String serverIp = MyApplication.settingInfo.getString(AppConstants.SERVER_IP, "");// 服务器地址url
        String inCameraIp = MyApplication.settingInfo.getString(AppConstants.IN_CAMERA_IP, "");// 入口相机地址
        String outCameraIp = MyApplication.settingInfo.getString(AppConstants.OUT_CAMERA_IP, "");// 出口相机地址
        long stallNum = MyApplication.settingInfo.getLong(AppConstants.ALL_CAR_PLACE);
        enterDelay = MyApplication.settingInfo.getInt(AppConstants.ENTER_DELAY);
        String companyName = MyApplication.settingInfo.getString(AppConstants.COMPANY_NAME, "");// 单位名称
        mServerAddress.setText(serverIp);
        mInIp.setText(inCameraIp);
        mOutIp.setText(outCameraIp);
        stallNumber.setText(String.valueOf(stallNum));
        editText_enterDelay.setText(String.valueOf(enterDelay));
        mCompany.setText(companyName);
        textViewDevId.setText(MyApplication.devID);


        isTempCarIn = MyApplication.settingInfo.getBoolean(AppConstants.TEMP_CAR_IN);
        isFree = MyApplication.settingInfo.getBoolean(AppConstants.TEMP_CAR_FREE);
        isPrint = MyApplication.settingInfo.getBoolean(AppConstants.IS_PRINT_CARD);
        isCardRead = MyApplication.settingInfo.getBoolean(AppConstants.IS_USE_CARD_HELP);
        isChina = MyApplication.settingInfo.getBoolean(AppConstants.IS_USE_CHINA);

        mTogglebutton1.setChecked(isTempCarIn);
        mTogglebutton2.setChecked(isFree);
        mTogglebutton3.setChecked(isPrint);
        mTogglebutton4.setChecked(isCardRead);
        getmTogglebuttonCarNumber.setChecked(isChina);

    }


    private void saveConfig() {
        MyApplication.settingInfo.putBoolean(AppConstants.TEMP_CAR_IN, isTempCarIn);
        MyApplication.settingInfo.putBoolean(AppConstants.TEMP_CAR_FREE, isFree);
        MyApplication.settingInfo.putBoolean(AppConstants.IS_PRINT_CARD, isPrint);
        MyApplication.settingInfo.putBoolean(AppConstants.IS_USE_CARD_HELP, isCardRead);
        MyApplication.settingInfo.putBoolean(AppConstants.IS_USE_CHINA, isChina);
        String serverAddress = mServerAddress.getText().toString().trim();
        String inCameraIp = mInIp.getText().toString().trim();
        String outCameraIp = mOutIp.getText().toString().trim();
        String companyName = mCompany.getText().toString().trim();
        Long stallNum = Long.valueOf(stallNumber.getText().toString().trim());
        enterDelay = Integer.valueOf(editText_enterDelay.getText().toString().trim());
        if (enterDelay < 1) {
            enterDelay = 1;
        }
        MyApplication.settingInfo.putInt(AppConstants.ENTER_DELAY, enterDelay);
        MyApplication.settingInfo.putString(AppConstants.SERVER_IP, serverAddress);
        MyApplication.settingInfo.putString(AppConstants.IN_CAMERA_IP, inCameraIp);
        MyApplication.settingInfo.putString(AppConstants.OUT_CAMERA_IP, outCameraIp);
        MyApplication.settingInfo.putLong(AppConstants.ALL_CAR_PLACE, stallNum);
        MyApplication.settingInfo.putString(AppConstants.COMPANY_NAME, companyName);
        T.showShort(getContext(), "保存成功");
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews() {
        mTogglebutton1.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    isTempCarIn = false;
                }
                //开
                if (state.equals(State.RIGHT)) {
                    isTempCarIn = true;

                }
            }
        });
        mTogglebutton2.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    isFree = false;
                }
                //开
                if (state.equals(State.RIGHT)) {
                    isFree = true;

                }
            }
        });
        mTogglebutton3.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    isPrint = false;
                }
                //开
                if (state.equals(State.RIGHT)) {
                    isPrint = true;
                }
            }
        });
        mTogglebutton4.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    isCardRead = false;
                }
                //开
                if (state.equals(State.RIGHT)) {
                    isCardRead = true;
                }
            }
        });
        getmTogglebuttonCarNumber.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    isChina = false;
                }
                //开
                if (state.equals(State.RIGHT)) {
                    isChina = true;
                }
            }
        });
    }


    @OnClick({R.id.btn_save_update, R.id.btn_clear_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_update:
                saveConfig();

                break;
            case R.id.btn_clear_all:
                request();
                break;
        }
    }

    private void clear(String userName) {
        try {
            db.delete(CarInfoTable.class);
            db.delete(CarWeiTable.class);
            db.delete(CarWeiBindTable.class);
            db.delete(FreeInfoTable.class);
            db.delete(MoneyTable.class);
            db.delete(TrafficInfoTable.class);
//            db.delete(DownloadTimeBean.class);
//            db.delete(UserTable.class);

            db.update(DownloadTimeBean.class, WhereBuilder.b("myid","=",0),new KeyValue("handler_in_out_record_download_time","1970-1-1 01:00:00")
                    ,new KeyValue("handler_down_tempfee_time","1970-1-1 01:00:00")
                    ,new KeyValue("handler_down_info_stall_time","1970-1-1 01:00:00")
                    ,new KeyValue("handler_down_info_vehicle_time","1970-1-1 01:00:00")
                    ,new KeyValue("handler_down_record_stall_vehicle_time","1970-1-1 01:00:00"));
            MyApplication.settingInfo.clear();
            InitUtils.init();
            MyApplication.settingInfo.putString(AppConstants.USER_NAME,userName);
            T.showShort(getContext(),"初始化成功,数据已全部删除");
            startActivity(new Intent(getContext(),MainActivity.class));


        } catch (DbException e) {
            e.printStackTrace();
            T.showShort(getContext(),"初始化失败");
        }
    }

    private void request() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.clear_all_diglog, null);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCancelable(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 500;
        params.height = 400;
        dialog.getWindow().setAttributes(params);
        dialog.show();

        final EditText pwd = (EditText) view.findViewById(R.id.clear_password);
        TextView name = (TextView) view.findViewById(R.id.clear_name);
        Button cancle = (Button) view.findViewById(R.id.clear_cancle);
        final Button clear = (Button) view.findViewById(R.id.clear_clear);

        final String username = MyApplication.settingInfo.getString(AppConstants.USER_NAME, "");
        name.setText(username);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pwd.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    T.showShort(getContext(), "请输入密码");
                    return;
                }

                try {
                    List<UserTable> all = db.selector(UserTable.class).where("userName", "=", username).and("password", "=", password).findAll();
                    if (all!=null&&all.size()>0){
                        dialog.dismiss();
                        clear(username);
                    }
                    else{

                        T.showShort(getContext(),"您输入的密码有误!");
                    }

                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
