package com.gz.gzcar.settingfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
    @Bind(R.id.et_server_port)
    EditText mServerPort;
    @Bind(R.id.et_server_mode)
    EditText mServerMode;
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
    private SPUtils spUtils;
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
        if (spUtils == null) {

            spUtils = new SPUtils(getContext(), "config");
        }
        String serverAddress = spUtils.getString("serverAddress", "");
        String serverPort = spUtils.getString("serverPort", "");
        String serverMode = spUtils.getString("serverMode", "");

        mServerAddress.setText(serverAddress);
        mServerPort.setText(serverPort);
        mServerMode.setText(serverMode);

        boolean tempCarIn = spUtils.getBoolean(TEMP_CAR_IN);
        boolean tempCarFree = spUtils.getBoolean(TEMP_CAR_FREE);
        boolean isPrint = spUtils.getBoolean(IS_PRINT_CARD);
        boolean isUseCard = spUtils.getBoolean(IS_USE_CARD_HELP);

        mTogglebutton1.setChecked(tempCarIn);
        mTogglebutton2.setChecked(tempCarFree);
        mTogglebutton3.setChecked(isPrint);
        mTogglebutton4.setChecked(isUseCard);
    }

    @Override
    public void onResume() {
        super.onResume();

//        mTogglebutton.setBackgroundColor(Color.WHITE);
//        mTogglebutton.setText("否", "是");
//        mTogglebutton.setTextColor(Color.BLACK);
//        mTogglebutton.setTextSize(20);
//        mTogglebutton.setTextMarginLeft(3);
//        mTogglebutton.setTextMarginRight(3);
//        mTogglebutton.setRightThumbColor(Color.GREEN);
//        mTogglebutton.setLeftThumbColor(Color.RED);
//        mTogglebutton.setChecked(true);

        initViews();
    }

    private void initViews() {
        mTogglebutton1.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    spUtils.putBoolean(TEMP_CAR_IN, false);
                }
                //开
                if (state.equals(State.RIGHT)) {
                    spUtils.putBoolean(TEMP_CAR_IN, true);

                }
            }
        });
        mTogglebutton2.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    spUtils.putBoolean(TEMP_CAR_FREE, false);
                }
                //开
                if (state.equals(State.RIGHT)) {
                    spUtils.putBoolean(TEMP_CAR_FREE, true);

                }
            }
        });
        mTogglebutton3.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    spUtils.putBoolean(IS_PRINT_CARD, false);
                }
                //开
                if (state.equals(State.RIGHT)) {
                    spUtils.putBoolean(IS_PRINT_CARD, true);

                }
            }
        });
        mTogglebutton4.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                // 关
                if (state.equals(State.LEFT)) {
                    spUtils.putBoolean(IS_USE_CARD_HELP, false);
                }
                //开
                if (state.equals(State.RIGHT)) {
                    spUtils.putBoolean(IS_USE_CARD_HELP, true);

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
        String serverAddress = mServerAddress.getText().toString().trim();
        String serverPort = mServerPort.getText().toString().trim();
        String serverMode = mServerMode.getText().toString().trim();
        if (spUtils == null) {

            spUtils = new SPUtils(getContext(), "config");
        }
        if (!TextUtils.isEmpty(serverAddress)) {
            spUtils.putString("serverAddress", serverAddress);
        }
        if (!TextUtils.isEmpty(serverPort)) {
            spUtils.putString("serverPort", serverPort);
        }
        if (!TextUtils.isEmpty(serverMode)) {
            spUtils.putString("serverMode", serverMode);
        }

        T.showShort(getContext(),"保存成功");
    }
}
