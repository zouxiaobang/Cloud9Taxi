package com.zouxiaobang.cloud9.cloud9car.common.utils;

import android.app.Activity;
import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by zouxiaobang on 10/16/17.
 * 设备相关工具类
 */

public class DevUtil {
    /**
     * 获取UID
     * @param context
     * @return
     */
    public static String UUID(Context context){
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = manager.getDeviceId();
        return deviceId + System.currentTimeMillis();
    }

    public static void closeInputMethod(Activity context){
        InputMethodManager iim = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        iim.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
