package com.zouxiaobang.cloud9.cloud9car.main;

import android.app.Activity;
import android.os.Bundle;

import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.account.view.PhoneInputDialog;

/**
 * 1、检查本地记录（登录态检查）
 * 2、若用户没登录则登录
 * 3、登录之前先校验手机号码
 * todo: 地图初始化
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLoginState();
    }

    /**
     * 检查用户是否登录
     */
    private void checkLoginState() {
        //todo：获取本地登录信息

        //登录是否过期
        boolean tokenValid = false;
        //todo：检查token是否过期

        if (!tokenValid){
            showPhoneInputDialog();
        } else {
            //todo: 请求网络，完成自动登录
        }
    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        PhoneInputDialog dialog = new PhoneInputDialog(this);
        dialog.show();
    }
}
