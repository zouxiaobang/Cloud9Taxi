package com.dalimao.mytaxi.cloud9car.common.lbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.dalimao.mytaxi.cloud9car.common.utils.SensorEventHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.rotation;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public class GaodeLbsLayer implements ILbsLayer, AMapLocationListener, LocationSource {

    private static final String MY_LOCATION_ID = "1000";
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
    private Map<String, Marker> mMarkerMap = new HashMap<>();
    /**
     * 是否第一次定位
     */
    private boolean isFirstFix = true;
    /**
     * 当前城市
     */
    private String mCity;
    /**
     * 路径搜索类
     */
    private RouteSearch mRouteSearch;

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
//        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
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
        Marker storedMarker = mMarkerMap.get(locationInfo.getKey());
        LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());

        if (storedMarker != null){
            //如果已经存在，则更新位置、角度
            storedMarker.setPosition(latLng);
            storedMarker.setRotateAngle(locationInfo.getRotation());
        } else {
            //如果不存在，则创建
//            Bitmap bMap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.navi_map_gps_locked);
//            BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

            MarkerOptions options = new MarkerOptions();
            //设置marker的图片
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            //设置marker的锚点
            options.anchor(0.5f, 0.5f);
            //设置marker的位置
            options.position(latLng);
            Marker marker = mAMap.addMarker(options);

            marker.setRotateAngle(rotation);
            mMarkerMap.put(locationInfo.getKey(), marker);

            //如果是当前位置
            if (locationInfo.getKey().equals(MY_LOCATION_ID)){
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
    public String getCity() {
        //  10/24/17 获取城市接口
        return mCity;
    }

    /**
     * 高德地图的POI搜索接口
     * @param key
     * @param listener
     */
    @Override
    public void poiSearch(String key, final OnSearchedListener listener) {
        if (!TextUtils.isEmpty(key)){
            //组装关键字
            InputtipsQuery inputtipsQuery = new InputtipsQuery(key, "");
            Inputtips inputtips = new Inputtips(mContext, inputtipsQuery);
            //开始异步搜索
            inputtips.requestInputtipsAsyn();
            inputtips.setInputtipsListener(new Inputtips.InputtipsListener() {
                @Override
                public void onGetInputtips(List<Tip> list, int code) {
                    if (code == AMapException.CODE_AMAP_SUCCESS){
                        //对结果进行解析
                        List<LocationInfo> locationInfos = new ArrayList<LocationInfo>();
                        for (Tip tip: list){
                            LocationInfo locationInfo = new LocationInfo(tip.getPoint().getLatitude(),
                                    tip.getPoint().getLongitude());
                            locationInfo.setName(tip.getName());
                            locationInfos.add(locationInfo);
                        }
                        listener.onSearched(locationInfos);
                    }else {
                        listener.onError(code);
                    }
                }
            });
        }
    }

    @Override
    public void clearAllMarker() {
        if (mAMap != null)
            mAMap.clear();
        if (mMarkerMap != null)
            mMarkerMap.clear();
    }

    @Override
    public void driveRoute(final LocationInfo start, LocationInfo end, final int color, final RouteCompletedListener listener) {
        //封装起终点的位置信息
        final LatLonPoint startPoint = new LatLonPoint(start.getLatitude(), start.getLongitude());
        LatLonPoint endPoint = new LatLonPoint(end.getLatitude(), end.getLongitude());

        //路径搜索类
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        //搜索参数
        RouteSearch.DriveRouteQuery option = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DrivingDefault,
                null,
                null,
                "");
        if (mRouteSearch == null){
            mRouteSearch = new RouteSearch(mContext);
        }
        //执行搜索
        mRouteSearch.calculateDriveRouteAsyn(option);
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                //获取第一条路径
                DrivePath path = driveRouteResult.getPaths().get(0);
                //获取这条路径上的所有点
                LatLonPoint startPos = driveRouteResult.getStartPos();
                LatLonPoint endPos = driveRouteResult.getTargetPos();
                List<DriveStep> steps = path.getSteps();

                //使用Polyline来绘制路径
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(color);
                //添加起点
                polylineOptions.add(new LatLng(startPos.getLatitude(), startPos.getLongitude()));
                //添加中间点
                for (DriveStep step: steps){
                    List<LatLonPoint> polyline = step.getPolyline();
                    for (LatLonPoint point: polyline){
                        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                        polylineOptions.add(latLng);
                    }
                }
                //添加终点
                polylineOptions.add(new LatLng(endPos.getLatitude(), endPos.getLongitude()));

                //执行绘制
                mAMap.addPolyline(polylineOptions);

                //处理回调
                if (listener != null){
                    RouteInfo info = new RouteInfo();
                    info.setTaxiCost(driveRouteResult.getTaxiCost());
                    info.setDuration(10 + new Long(path.getDuration() / 1000 * 60).intValue());
                    info.setDistance(path.getDistance() / 1000);
                    listener.onCompleted(info);
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
    }

    @Override
    public void moveCamera(LocationInfo startLocation, LocationInfo endLocation) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()));
        builder.include(new LatLng(endLocation.getLatitude(), endLocation.getLongitude()));
        LatLngBounds bounds = builder.build();
        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void moveCameraToPoint(LocationInfo startLocation) {
        LatLng latLng = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(latLng, 18, 30, 30));
        mAMap.moveCamera(update);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//  10/21/17 显示小蓝点
        if (mLocationChangedListener != null
                && aMapLocation != null){
            //获取当前城市
            mCity = aMapLocation.getCity();
            if (aMapLocation.getErrorCode() == 0){
                //位置发生变化
                mLocationChangedListener.onLocationChanged(aMapLocation);

                LocationInfo locationInfo
                        = new LocationInfo(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                locationInfo.setKey(MY_LOCATION_ID);
                locationInfo.setName(aMapLocation.getPoiName());

                //是否第一次定位
                if (isFirstFix){
                    isFirstFix = false;

                    //设置Camera，让显示范围变小
                    moveCameraToPoint(locationInfo);

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
