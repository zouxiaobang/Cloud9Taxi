package com.dalimao.mytaxi.cloud9car.common.lbs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import java.util.List;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public interface ILbsLayer {
    /**
     * 获取地图视图
     */
    View getMapView();

    /**
     * 添加或更新标记点
     * 包括位置、角度
     * @param locationInfo
     */
    void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap);

    /**
     * 位置变化的监听
     * @param listener
     */
    void setLocationChangedListener(CommonLocationChangedListener listener);

    /**
     * 设置图标
     * @param res
     */
    void setLocationIcon(int res);

    void onCreate(Bundle state);
    void onResume();
    void onPause();
    void onDestroy();
    void onSaveInstanceState(Bundle outState);

    /**
     * 获取当前城市
     * @return
     */
    String getCity();

    /**
     * 联动搜索附近的位置
     * @param key
     * @param listener
     */
    void poiSearch(String key, OnSearchedListener listener);

    /**
     * 清除地图上所有的标记
     */
    void clearAllMarker();

    void driveRoute(LocationInfo start, LocationInfo end, int color, RouteCompletedListener listener);

    /**
     * 移动相机，以缩放地图
     * @param startLocation
     * @param endLocation
     */
    void moveCamera(LocationInfo startLocation, LocationInfo endLocation);

    /**
     * 设置相机视角
     * @param startLocation
     */
    void moveCameraToPoint(LocationInfo startLocation);


    interface RouteCompletedListener{
        void onCompleted(RouteInfo routeInfo);
    }

    interface CommonLocationChangedListener{
        /**
         * 位置发生变化时调用
         * @param locationInfo
         */
        void onLocationChanged(LocationInfo locationInfo);

        /**
         * 第一次定位时调用
         * @param locationInfo
         */
        void onLocation(LocationInfo locationInfo);
    }

    /**
     * POI 搜索结果监听器
     */
    interface OnSearchedListener{
        void onSearched(List<LocationInfo> results);

        void onError(int rCode);
    }
}
