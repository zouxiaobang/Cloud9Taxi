package com.zouxiaobang.cloud9.cloud9car.main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.account.view.PhoneInputDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 1、检查本地记录（登录态检查）
 * 2、若用户没登录则登录
 * 3、登录之前先校验手机号码
 * todo: 地图初始化
 */
public class MainActivity extends Activity {
    private static final int PERMISSION_READ_PHONE_STATE = 101;
    private static final int PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  10/16/17 申请权限 -- android.permission.READ_PHONE_STATE.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();
        }
        checkLoginState();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permission() {
        List<String> permissionStrs = new ArrayList<>();
        boolean request = false;

        int phoneStatePermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);

        if (phoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            permissionStrs.add(Manifest.permission.READ_PHONE_STATE);
            request = true;
        }

        if (request) {
            String[] permissionArr = new String[permissionStrs.size()];
            permissionStrs.toArray(permissionArr);
            requestPermissions(permissionArr, PERMISSION_REQUEST);
        }


    }

    /**
     * 检查用户是否登录
     */
    private void checkLoginState() {
        //todo：获取本地登录信息

        //登录是否过期
        boolean tokenValid = false;
        //todo：检查token是否过期

        if (!tokenValid) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        // 拥有权限了
                        checkLoginState();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.rejectPermission), Toast.LENGTH_SHORT).show();
                        MainActivity.this.finish();
                    }
                }
                break;
        }
    }
}
