package com.zouxiaobang.cloud9.cloud9car.main.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.zouxiaobang.cloud9.cloud9car.C9Application;
import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.account.model.AccountManagerImpl;
import com.zouxiaobang.cloud9.cloud9car.account.view.PhoneInputDialog;
import com.zouxiaobang.cloud9.cloud9car.common.databus.RxBus;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.OkHttpClientImpl;
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
public class MainActivity extends Activity implements IMainView, LocationSource, AMapLocationListener{
    private static final int PERMISSION_REQUEST = 100;
    private static final String TAG = "MainActivity";

    /**
     * 地图视图对象
     */
    private MapView mMapView;
    /**
     * 地图管理对象
     */
    private AMap mAMap;
    /**
     * 位置发生变化的监听器
     * 地图模块和小蓝点模块是相对独立的，
     * 两者是通过OnLocationChangedListener来产生联系的。
     *
     * 这里要强调一下AMapLocationListener和OnLocationChangedListener之间的区别
     * AMapLocationListener是定位发生变化时通知小蓝点在某个位置上重绘的
     * OnLocationChangedListener是定位发生变化时通知地图位置改变的。
     */
    private LocationSource.OnLocationChangedListener mLocationChangedListener;
    /**
     * 地图位置定位管理
     */
    private AMapLocationClient mLocationClient;
    /**
     * 地图定位参数对象
     */
    private AMapLocationClientOption mLocationOption;
    /**
     * 位置的标记
     */
    private Marker mLocMarker;
    /**
     * 传感器帮助类
     */
    private SensorEventHelper mSensorEventHelper;

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

        //初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        // 此方法必须重写
        mMapView.onCreate(savedInstanceState);
        initMap();

        mSensorEventHelper = new SensorEventHelper(this);
        mSensorEventHelper.registerSensorListener();
    }

    private void initMap(){
        if (mAMap == null){
            mAMap = mMapView.getMap();
            setupMap();
        }
    }

    /**
     *  设置小蓝点的样式
     */
    private void setupMap() {
        //小蓝点样式
        MyLocationStyle style = new MyLocationStyle();
        //设置小蓝点的图标
        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        //设置定位范围的外圈大小
        style.strokeWidth(1.0f);
        //设置定位范围的圆的外圈颜色
        style.strokeColor(Color.argb(100, 0, 0, 180));
        //设置定位范围的圆的颜色
        style.radiusFillColor(Color.argb(100, 0, 0, 180));

        //为地图设置小蓝点样式
        mAMap.setMyLocationStyle(style);
        //监听定位是否激活
        mAMap.setLocationSource(this);
        //显示定位按钮
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        //显示定位层可触发定位
        mAMap.setMyLocationEnabled(true);

    }


    /**
     * 添加Marker标记
     * @param latLng
     */
    private void addMarker(LatLng latLng){
        if (mLocMarker != null){
            return;
        }

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

        MarkerOptions options = new MarkerOptions();
        //设置marker的图片
        options.icon(des );
        //设置marker的锚点
        options.anchor(0.5f, 0.5f);
        //设置marker的位置
        options.position(latLng);
        mLocMarker = mAMap.addMarker(options);

        mSensorEventHelper.setCurrentMarker(mLocMarker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorEventHelper.unRegisterSensorListener();

        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(mPresenter);

        mMapView.onDestroy();
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

    /**
     * 位置发生变化时调用
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //  10/21/17 显示小蓝点
        if (mLocationChangedListener != null
                && aMapLocation != null){
            if (aMapLocation.getErrorCode() == 0){
                //位置发生变化
                mLocationChangedListener.onLocationChanged(aMapLocation);

                //是否第一次定位
                if (isFirstFix){
                    isFirstFix = false;

                    //设置Camera，让显示范围变小
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(
                            new CameraPosition(latLng, 18, 30, 30));
                    mAMap.moveCamera(update);
                    addMarker(latLng);
                }
            } else {
                String errText = "定位失败," +
                        aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    /**
     * 定位激活
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        //  10/21/17 设置定位管理对象并开启定位
        //设置通知地图的位置变化监听
        mLocationChangedListener = onLocationChangedListener;

        if (mLocationClient == null){
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();

            //设置为高精度
            mLocationOption.setLocationMode(
                    AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置位置参数
            mLocationClient.setLocationOption(mLocationOption);
            //设置通知小蓝点位置变化的监听
            mLocationClient.setLocationListener(this);

            //开始定位
            mLocationClient.startLocation();
        }
    }

    /**
     * 定位关闭
     */
    @Override
    public void deactivate() {
        //  10/21/17 关闭定位功能并将定位管理对象置为空
        if (mLocationClient != null){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }

        mLocationClient = null;
    }
}
