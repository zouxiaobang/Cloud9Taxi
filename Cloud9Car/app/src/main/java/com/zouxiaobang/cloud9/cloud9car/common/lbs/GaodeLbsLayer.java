package com.zouxiaobang.cloud9.cloud9car.common.lbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.common.utils.SensorEventHelper;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.rotation;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public class GaodeLbsLayer implements ILbsLayer, AMapLocationListener, LocationSource {

    private static final int MY_LOCATION_ID = 1000;
    private Context mContext;
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

    /**
     * 管理Marker的集合
     */
    private Map<Integer, Marker> mMarkerMap = new HashMap<>();
    /**
     * 是否第一次定位
     */
    private boolean isFirstFix = true;

    private CommonLocationChangedListener mCommonLocationChangedListener;


    public GaodeLbsLayer(Context context){
        mContext = context;
        //初始化地图
        mMapView = new MapView(context);

        initMap();

        mSensorEventHelper = new SensorEventHelper(context);
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

    @Override
    public View getMapView() {
        return mMapView;
    }

    @Override
    public void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap) {
        Marker storedMarker = mMarkerMap.get(locationInfo.getId());
        LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());

        if (storedMarker != null){
            //如果已经存在，则更新位置、角度
            storedMarker.setPosition(latLng);
            storedMarker.setRotateAngle(locationInfo.getRotation());
        } else {
            //如果不存在，则创建
            Bitmap bMap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.navi_map_gps_locked);
            BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

            MarkerOptions options = new MarkerOptions();
            //设置marker的图片
            options.icon(des );
            //设置marker的锚点
            options.anchor(0.5f, 0.5f);
            //设置marker的位置
            options.position(latLng);
            Marker marker = mAMap.addMarker(options);

            marker.setRotateAngle(rotation);
            mMarkerMap.put(locationInfo.getId(), marker);

            //如果是当前位置
            if (locationInfo.getId() == MY_LOCATION_ID){
                mSensorEventHelper.setCurrentMarker(marker);
            }

        }
    }

    @Override
    public void setLocationChangedListener(CommonLocationChangedListener listener) {
        mCommonLocationChangedListener = listener;
    }

    @Override
    public void setLocationIcon(int res) {
        // TODO: 10/21/17
    }

    @Override
    public void onCreate(Bundle state) {
        mMapView.onCreate(state);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        mSensorEventHelper.registerSensorListener();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        mSensorEventHelper.unRegisterSensorListener();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }



    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//  10/21/17 显示小蓝点
        if (mLocationChangedListener != null
                && aMapLocation != null){
            if (aMapLocation.getErrorCode() == 0){
                //位置发生变化
                mLocationChangedListener.onLocationChanged(aMapLocation);

                LocationInfo locationInfo
                        = new LocationInfo(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                locationInfo.setId(MY_LOCATION_ID);
                locationInfo.setName(aMapLocation.getPoiName());

                //是否第一次定位
                if (isFirstFix){
                    isFirstFix = false;

                    //设置Camera，让显示范围变小
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(
                            new CameraPosition(latLng, 18, 30, 30));
                    mAMap.moveCamera(update);

                    if (mCommonLocationChangedListener != null){
                        mCommonLocationChangedListener.onLocation(locationInfo);
                    }
                }

                if (mCommonLocationChangedListener != null){
                    mCommonLocationChangedListener.onLocationChanged(locationInfo);
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
            mLocationClient = new AMapLocationClient(mContext);
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
