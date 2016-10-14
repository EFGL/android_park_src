package com.gz.gzcar.settingfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gz.gzcar.MyApplication;
import com.gz.gzcar.R;
import com.gz.gzcar.utils.SPUtils;
import com.gz.gzcar.utils.T;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;

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
    @Bind(R.id.tb_togglebutton1)
    JellyToggleButton mTogglebutton1;
    @Bind(R.id.tb_togglebutton2)
    JellyToggleButton mTogglebutton2;
    @Bind(R.id.tb_togglebutton3)
    JellyToggleButton mTogglebutton3;
    @Bind(R.id.tb_togglebutton4)
    JellyToggleButton mTogglebutton4;
    @Bind(R.id.btn_save_update)
    Button mSave;
    private final String TEMP_CAR_IN = "tempCarIn";// 临时车入场是否确认
    private final String TEMP_CAR_FREE = "tempCarFree";// 零收费车是否确认
    private final String IS_PRINT_CARD = "isPrintCard";// 是否打印小票
    private final String IS_USE_CARD_HELP = "isUseCardHelp";// 是否使用读卡器


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        String serverIp = MyApplication.settingInfo.getString("serverIp", "");// 服务器地址url
        String inCameraIp = MyApplication.settingInfo.getString("inCameraIp", "");// 入口相机地址
        String outCameraIp = MyApplication.settingInfo.getString("outCameraIp", "");// 出口相机地址

        mServerAddress.setText(serverIp);
        mInIp.setText(inCameraIp);
        mOutIp.setText(outCameraIp);

        boolean tempCarIn = MyApplication.settingInfo.getBoolean(TEMP_CAR_IN);
        boolean tempCarFree = MyApplication.settingInfo.getBoolean(TEMP_CAR_FREE);
        boolean isPrint = MyApplication.settingInfo.getBoolean(IS_PRINT_CARD);
        boolean isUseCard = MyApplication.settingInfo.getBoolean(IS_USE_CARD_HELP);

        mTogglebutton1.setChecked(tempCarIn);
        mTogglebutton2.setChecked(tempCarFree);
        mTogglebutton3.setChecked(isPrint);
        mTogglebutton4.setChecked(isUseCard);
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    private boolean isTempCarIn;
    private boolean isFree;
    private boolean isPrint;
    private boolean isCardRead;

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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_save_update)
    public void onClick() {

        MyApplication.settingInfo.putBoolean(TEMP_CAR_IN, isTempCarIn);
        MyApplication.settingInfo.putBoolean(TEMP_CAR_FREE, isFree);
        MyApplication.settingInfo.putBoolean(IS_PRINT_CARD, isPrint);
        MyApplication.settingInfo.putBoolean(IS_USE_CARD_HELP, isCardRead);

        String serverAddress = mServerAddress.getText().toString().trim();
        String inCameraIp = mInIp.getText().toString().trim();
        String outCameraIp = mOutIp.getText().toString().trim();
        if (!TextUtils.isEmpty(serverAddress)) {
            MyApplication.settingInfo.putString("serverIp", serverAddress);
        }
        if (!TextUtils.isEmpty(inCameraIp)) {
            MyApplication.settingInfo.putString("inCameraIp", inCameraIp);
        }
        if (!TextUtils.isEmpty(outCameraIp)) {
            MyApplication.settingInfo.putString("outCameraIp", outCameraIp);
        }

        T.showShort(getContext(), "保存成功");
    }
}
