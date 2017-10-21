package com.zouxiaobang.cloud9.cloud9car.main.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;


import com.zouxiaobang.cloud9.cloud9car.C9Application;
import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.account.model.AccountManagerImpl;
import com.zouxiaobang.cloud9.cloud9car.account.view.PhoneInputDialog;
import com.zouxiaobang.cloud9.cloud9car.common.databus.RxBus;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.OkHttpClientImpl;
import com.zouxiaobang.cloud9.cloud9car.common.lbs.GaodeLbsLayer;
import com.zouxiaobang.cloud9.cloud9car.common.lbs.ILbsLayer;
import com.zouxiaobang.cloud9.cloud9car.common.lbs.LocationInfo;
import com.zouxiaobang.cloud9.cloud9car.common.storage.SharedPreferenceDao;
import com.zouxiaobang.cloud9.cloud9car.common.utils.SensorEventHelper;
import com.zouxiaobang.cloud9.cloud9car.main.presenter.IMainPresenter;
import com.zouxiaobang.cloud9.cloud9car.main.presenter.MainPresenterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 1、检查本地记录（登录态检查）
 * 2、若用户没登录则登录
 * 3、登录之前先校验手机号码
 *  地图初始化
 * 1、地图接入
 * 2、设置小蓝点
 * 3、添加相机功能：让地图显示到的区域范围更小
 * 4、添加传感器，实时更新方向
 */
public class MainActivity extends Activity implements IMainView{
    private static final int PERMISSION_REQUEST = 100;
    private static final String TAG = "MainActivity";

    /**
     * LBS地图接口
     */
    private ILbsLayer mLbsLayer;

    private boolean isPermission = false;
    private IMainPresenter mPresenter;
    private boolean isFirstFix = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenterImpl(this, new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferenceDao(C9Application.getInstance(),
                        SharedPreferenceDao.FILE_ACCOUNT)));

        RxBus.getInstance().register(mPresenter);

        //  10/16/17 申请权限 -- android.permission.READ_PHONE_STATE.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPermission = permission();
        } else {
            isPermission = true;
        }
        if (isPermission){
            mPresenter.requestLoginByToken();
        }

        //初始化地图接口--高德地图
        mLbsLayer = new GaodeLbsLayer(this);

        // 此方法必须重写
        mLbsLayer.onCreate(savedInstanceState);
        mLbsLayer.setLocationChangedListener(new ILbsLayer.CommonLocationChangedListener() {
            @Override
            public void onLocationChanged(LocationInfo locationInfo) {

            }

            @Override
            public void onLocation(LocationInfo locationInfo) {
                // 首次定位，添加当前位置的标记
                mLbsLayer.addOrUpdateMarker(locationInfo, BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked));
            }
        });

        //添加地图视图
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.main_activity);
        viewGroup.addView(mLbsLayer.getMapView());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLbsLayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLbsLayer.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLbsLayer.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(mPresenter);

        mLbsLayer.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean permission() {
        List<String> permissionStrs = new ArrayList<>();
        boolean request = false;

        int phoneStatePermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        int writeExternalPermission
                = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accessCoarseLocationPermission
                = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFindLocationPermission
                = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        int sensorsPermission = checkSelfPermission(Manifest.permission.BODY_SENSORS);


        if (phoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            permissionStrs.add(Manifest.permission.READ_PHONE_STATE);
            request = true;
        }
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED){
            permissionStrs.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            request = true;
        }
        if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
            permissionStrs.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            request = true;
        }
        if (accessFindLocationPermission != PackageManager.PERMISSION_GRANTED){
            permissionStrs.add(Manifest.permission.ACCESS_FINE_LOCATION);
            request = true;
        }
        if (sensorsPermission != PackageManager.PERMISSION_GRANTED){
            permissionStrs.add(Manifest.permission.BODY_SENSORS);
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

        boolean isPermission = true;
        switch (requestCode) {
            case PERMISSION_REQUEST:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        isPermission = false;
                        Toast.makeText(MainActivity.this, getString(R.string.rejectPermission), Toast.LENGTH_SHORT).show();
                        MainActivity.this.finish();
                    }
                }
                break;
        }

        if (isPermission){
            // 拥有权限了
            mPresenter.requestLoginByToken();
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
