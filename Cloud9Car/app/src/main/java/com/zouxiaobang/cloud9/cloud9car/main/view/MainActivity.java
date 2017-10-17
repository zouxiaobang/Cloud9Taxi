package com.zouxiaobang.cloud9.cloud9car.main.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zouxiaobang.cloud9.cloud9car.C9Application;
import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.account.model.AccountManagerImpl;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.Account;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.account.view.PhoneInputDialog;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.OkHttpClientImpl;
import com.zouxiaobang.cloud9.cloud9car.common.storage.SharedPreferenceDao;
import com.zouxiaobang.cloud9.cloud9car.main.presenter.IMainPresenter;
import com.zouxiaobang.cloud9.cloud9car.main.presenter.MainPresenterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 1、检查本地记录（登录态检查）
 * 2、若用户没登录则登录
 * 3、登录之前先校验手机号码
 * todo: 地图初始化
 */
public class MainActivity extends Activity implements IMainView {
    private static final int PERMISSION_READ_PHONE_STATE = 101;
    private static final int PERMISSION_REQUEST = 100;
    private static final String TAG = "MainActivity";

    private boolean isPermission = false;
    private IMainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenterImpl(this, new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferenceDao(C9Application.getInstance(),
                        SharedPreferenceDao.FILE_ACCOUNT)));


        //  10/16/17 申请权限 -- android.permission.READ_PHONE_STATE.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPermission = permission();
        } else {
            isPermission = true;
        }
        if (isPermission){
            mPresenter.requestLoginByToken();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean permission() {
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

        Log.i(TAG, "permission: ");
        return !request;
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
                        mPresenter.requestLoginByToken();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.rejectPermission), Toast.LENGTH_SHORT).show();
                        MainActivity.this.finish();
                    }
                }
                break;
        }
    }

    @Override
    public void showLoginSuccess() {
        Toast.makeText(MainActivity.this, getString(R.string.login_suc), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTokenInvalid() {
        showPhoneInputDialog();
        Toast.makeText(MainActivity.this, getString(R.string.token_invalid), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showServerError() {
        Toast.makeText(MainActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
    }
}
