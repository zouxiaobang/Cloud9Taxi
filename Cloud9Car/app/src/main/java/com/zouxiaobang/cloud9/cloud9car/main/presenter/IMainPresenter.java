package com.zouxiaobang.cloud9.cloud9car.main.presenter;

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
     * @param latitude
     * @param longitude
     */
    void fetchNearDrivers(double latitude, double longitude);
}
