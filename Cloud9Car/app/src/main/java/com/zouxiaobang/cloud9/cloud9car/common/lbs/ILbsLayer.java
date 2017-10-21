package com.zouxiaobang.cloud9.cloud9car.common.lbs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

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
}
