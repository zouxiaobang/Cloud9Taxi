package com.dalimao.mytaxi.cloud9car.main.presenter;

import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface IMainPresenter {
    /**
     * 请求使用Token进行登录
     */
    void requestLoginByToken();

    /**
     * 获取附近司机
     *
     * @param latitude
     * @param longitude
     */
    void fetchNearDrivers(double latitude, double longitude);

    /**
     * 上报当前位置
     *
     * @param locationInfo
     */
    void updateLocationToServer(LocationInfo locationInfo);

    /**
     * 请求呼叫司机
     * @param pushKey
     * @param cost
     * @param startLocation
     * @param endLocation
     */
    void requestCallDriver(String pushKey, float cost, LocationInfo startLocation, LocationInfo endLocation);

    /**
     * 请求取消订单
     */
    void requestCancel();
}
