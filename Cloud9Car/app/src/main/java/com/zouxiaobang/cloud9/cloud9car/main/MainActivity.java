package com.zouxiaobang.cloud9.cloud9car.main;

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
    private static final String TAG = "MainActivity";
    private IHttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = new OkHttpClientImpl();

        //  10/16/17 申请权限 -- android.permission.READ_PHONE_STATE.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();
        } else {
            checkLoginState();
        }

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
        //获取本地登录信息
        SharedPreferenceDao dao
                = new SharedPreferenceDao(C9Application.getInstance(),
                SharedPreferenceDao.FILE_ACCOUNT);
        final Account account = (Account) dao.get(SharedPreferenceDao.KEY_ACCOUNT, Account.class);

        //登录是否过期
        boolean tokenValid = false;
        //检查token是否过期
        if (account != null){
            if (account.getExpired() > System.currentTimeMillis()){
                //token有效
                tokenValid = true;
            }
        }

        Log.i(TAG, "checkLoginState: tokenvalid = " + tokenValid);
        if (!tokenValid) {
            showPhoneInputDialog();
        } else {
            // 请求网络，完成自动登录
            new Thread(){
                @Override
                public void run() {
                    //获取url
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    //创建Request
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.getToken());
                    //执行请求
                    IRespone respone = mClient.post(request, false);
                    Log.d(TAG, "run: " + respone.getData());
                    //处理Response
                    if (respone.getCode() == BaseBizResponse.STATE_OK){
                        LoginResponse loginResponse
                                = new Gson().fromJson(respone.getData(), LoginResponse.class);

                        if (loginResponse.getCode() == BaseBizResponse.STATE_OK){
                            Account account = loginResponse.getData();
                            SharedPreferenceDao dao
                                    = new SharedPreferenceDao(C9Application.getInstance(),
                                    SharedPreferenceDao.FILE_ACCOUNT);
                            dao.save(SharedPreferenceDao.KEY_ACCOUNT, account);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, getString(R.string.login_suc), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (loginResponse.getCode() == BaseBizResponse.STATE_TOKEN_INVALID){
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showPhoneInputDialog();
                                }
                            });
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }.start();
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
