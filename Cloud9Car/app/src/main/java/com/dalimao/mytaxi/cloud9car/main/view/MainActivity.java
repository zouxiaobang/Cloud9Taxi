package com.dalimao.mytaxi.cloud9car.main.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;


import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.cloud9car.C9Application;
import com.dalimao.mytaxi.cloud9car.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.cloud9car.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.cloud9car.common.databus.RxBus;
import com.dalimao.mytaxi.cloud9car.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.cloud9car.common.lbs.GaodeLbsLayer;
import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;
import com.dalimao.mytaxi.cloud9car.common.utils.DevUtil;
import com.dalimao.mytaxi.cloud9car.main.model.MainManagerImpl;
import com.dalimao.mytaxi.cloud9car.main.presenter.MainPresenterImpl;
import com.dalimao.mytaxi.cloud9car.common.http.api.API;
import com.dalimao.mytaxi.cloud9car.common.lbs.ILbsLayer;
import com.dalimao.mytaxi.cloud9car.common.storage.SharedPreferenceDao;
import com.dalimao.mytaxi.cloud9car.main.presenter.IMainPresenter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * 1、检查本地记录（登录态检查）
 * 2、若用户没登录则登录
 * 3、登录之前先校验手机号码
 * 地图初始化
 * 1、地图接入
 * 2、设置小蓝点
 * 3、添加相机功能：让地图显示到的区域范围更小
 * 4、添加传感器，实时更新方向
 * 5、获取附近司机
 */
public class MainActivity extends Activity implements IMainView {
    private static final int PERMISSION_REQUEST = 100;
    private static final String TAG = "MainActivity";

    /**
     * LBS地图接口
     */
    private ILbsLayer mLbsLayer;
    /**
     * 起点和终点
     */
    private AutoCompleteTextView mStartEdit;
    private AutoCompleteTextView mEndEdit;
    private PoiAdapter mEndAdapter;
    /**
     * 标题栏显示当前城市
     */
    private TextView mCity;
    /**
     * 记录起点和终点的位置
     */
    private LocationInfo mStartLocation;
    private LocationInfo mEndLocation;

    private boolean isPermission = false;
    private IMainPresenter mPresenter;
    private boolean isFirstFix = true;
    private Bitmap mDriverBitmap;

    private String mPushKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenterImpl(this, new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferenceDao(C9Application.getInstance(),
                        SharedPreferenceDao.FILE_ACCOUNT)),
                new MainManagerImpl(new OkHttpClientImpl()));

        RxBus.getInstance().register(mPresenter);

        //  10/16/17 申请权限 -- android.permission.READ_PHONE_STATE.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPermission = permission();
        } else {
            isPermission = true;
        }
        if (isPermission) {
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
                mLbsLayer.addOrUpdateMarker(locationInfo,
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.navi_map_gps_locked));
                //设置起点
                mStartLocation = locationInfo;
                Log.d(TAG, "onLocation: city = " + mLbsLayer.getCity() + "  : name = " + locationInfo.getName() );
                //设置标题
                mCity.setText(mLbsLayer.getCity());
                //设置起点具体名称
                mStartEdit.setText(locationInfo.getName());


                //获取附近司机
                getNearDrivers(locationInfo.getLatitude(), locationInfo.getLongitude());
                // 上报当前位置
                updateLocationToServer(locationInfo);
            }
        });

        //添加地图视图
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.map_container);
        viewGroup.addView(mLbsLayer.getMapView());


        // 推送服务
        // 初始化BmobSDK
        Bmob.initialize(this, API.Config.getAppId());
        // 使用推送服务时的初始化操作
        BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);
        installation.save();
        mPushKey = installation.getInstallationId();
        // 启动推送服务
        BmobPush.startWork(this);

        initView();
    }

    /**
     * 初始化其他视图
     */
    private void initView() {
        mStartEdit = (AutoCompleteTextView) findViewById(R.id.start);
        mEndEdit = (AutoCompleteTextView) findViewById(R.id.end);
        mCity = (TextView) findViewById(R.id.city);
        mEndEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //关键搜索推荐地点
                mLbsLayer.poiSearch(editable.toString(), new ILbsLayer.OnSearchedListener(){

                    @Override
                    public void onSearched(List<LocationInfo> results) {
                        updatePoiList(results);
                    }

                    @Override
                    public void onError(int rCode) {

                    }
                });
            }
        });
    }

    /**
     * 处理联动搜索所得到的结果
     * @param results
     */
    private void updatePoiList(final List<LocationInfo> results) {
        List<String> listString = new ArrayList<>();
        for (int i = 0;i < results.size();i ++){
            listString.add(results.get(i).getName());
        }

        if (mEndAdapter == null){
            mEndAdapter = new PoiAdapter(getApplicationContext(), listString);
            mEndEdit.setAdapter(mEndAdapter);
        } else {
            mEndAdapter.setData(listString);
        }

        mEndEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DevUtil.closeInputMethod(MainActivity.this);
                //记录终点
                mEndLocation = results.get(position);
                // TODO: 10/24/17 绘制路径
                showRoute(mStartLocation, mEndLocation);
            }
        });
        mEndAdapter.notifyDataSetChanged();
    }

    /**
     * TODO: 绘制起点和终点路径
     * @param startLocation
     * @param endLocation
     */
    private void showRoute(LocationInfo startLocation, LocationInfo endLocation) {

    }

    /**
     * 上报当前位置
     *
     * @param locationInfo
     */
    private void updateLocationToServer(LocationInfo locationInfo) {
        locationInfo.setKey(mPushKey);
        mPresenter.updateLocationToServer(locationInfo);
    }

    /**
     * 获取附近司机
     *
     * @param latitude
     * @param longitude
     */
    private void getNearDrivers(double latitude, double longitude) {
        mPresenter.fetchNearDrivers(latitude, longitude);
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
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
            permissionStrs.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            request = true;
        }
        if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionStrs.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            request = true;
        }
        if (accessFindLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionStrs.add(Manifest.permission.ACCESS_FINE_LOCATION);
            request = true;
        }
        if (sensorsPermission != PackageManager.PERMISSION_GRANTED) {
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

        if (isPermission) {
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

    /**
     * 获取附近司机
     *
     * @param data
     */
    @Override
    public void showNears(List<LocationInfo> data) {
//        if (mDriverBitmap == null || mDriverBitmap.isRecycled()) {
//            mDriverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
//        }

        for (LocationInfo info : data) {
//            mLbsLayer.addOrUpdateMarker(info, mDriverBitmap);
            showLocationChange(info);
        }
    }

    @Override
    public void showLocationChange(LocationInfo locationInfo) {
        if (mDriverBitmap == null || mDriverBitmap.isRecycled()) {
            mDriverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        }
        mLbsLayer.addOrUpdateMarker(locationInfo, mDriverBitmap);
    }
}