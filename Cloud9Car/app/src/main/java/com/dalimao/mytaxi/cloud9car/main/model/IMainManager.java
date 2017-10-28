package com.dalimao.mytaxi.cloud9car.main.model;

import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public interface IMainManager {

    /**
     * 获取附近司机
     *
     * @param latitude
     * @param longitude
     */
    void fetchNearDriver(double latitude, double longitude);

    /**
     * 上报位置
     *
     * @param locationInfo
     */

    void updateLocationToServer(LocationInfo locationInfo);

    /**
     * 呼叫司机
     * @param pushKey
     * @param cost
     * @param startLocation
     * @param endLocation
     */
    void callDriver(String pushKey, float cost, LocationInfo startLocation, LocationInfo endLocation);

    /**
     * 取消订单
     * @param orderId
     */
    void cancelOrder(String orderId);

    /**
     * 完成支付功能
     * @param orderId
     */
    void pay(String orderId);
}
