package com.zouxiaobang.cloud9.cloud9car.main.model;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public interface IMainManager {

    /**
     * 获取附近司机
     * @param latitude
     * @param longitude
     */
    void fetchNearDriver(double latitude, double longitude);
}
