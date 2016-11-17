package com.gz.gzcar.utils;

import com.gz.gzcar.AppConstants;

import static com.gz.gzcar.MyApplication.settingInfo;

/**
 * Created by Endeavor on 2016/11/15 0015.
 */

public class InitUtils {
    public static void init() {

        SPUtils spUtils = settingInfo;
        boolean first = spUtils.getBoolean(AppConstants.IS_FIRST);
        if (!first) {
            L.showlogError("---第一次启动程序,开始初始化收费配置---");
            spUtils.putInt(AppConstants.TEMP_FREE, 30);
            spUtils.putInt(AppConstants.FRIEND_FREE, 180);
            spUtils.putBoolean(AppConstants.IS_FREE, true);
            spUtils.putBoolean(AppConstants.IS_HOURADD, true);

            L.showlogError("---收费配置初始化完成,开始初始化系统设置---");
            spUtils.putString(AppConstants.SERVER_IP, "http://221.204.11.69:3002/");// 服务器地址url
            spUtils.putString(AppConstants.IN_CAMERA_IP, "192.168.10.203");// 入口相机地址
            spUtils.putString(AppConstants.OUT_CAMERA_IP, "192.168.10.202");// 出口相机地址
            spUtils.putLong(AppConstants.ALL_CAR_PLACE,500);
            spUtils.putInt(AppConstants.ENTER_DELAY,1);
            spUtils.putInt(AppConstants.TEMP_FREE,30);

            spUtils.putString(AppConstants.USER_NAME, " ");
            spUtils.putLong("inCarCount", 0);
            spUtils.putLong("outCarCount", 0);
            spUtils.putLong("chargeCarNumber", 0);
            spUtils.putString("chargeMoney", "0.00");
            spUtils.putBoolean("loginStatus", false);

            spUtils.putBoolean(AppConstants.TEMP_CAR_IN, true);
            spUtils.putBoolean(AppConstants.TEMP_CAR_FREE, false);
            spUtils.putBoolean(AppConstants.IS_PRINT_CARD, false);
            spUtils.putBoolean(AppConstants.IS_USE_CARD_HELP, false);
            spUtils.putBoolean(AppConstants.IS_USE_CHINA, false);
            spUtils.putString(AppConstants.COMPANY_NAME, "车牌识别停车场");


            spUtils.putBoolean(AppConstants.IS_FIRST, true);
            L.showlogError("---初始化完成---");
        }

    }
}
