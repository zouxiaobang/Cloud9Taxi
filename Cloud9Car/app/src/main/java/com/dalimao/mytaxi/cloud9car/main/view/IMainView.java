package com.dalimao.mytaxi.cloud9car.main.view;

import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;

import java.util.List;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface IMainView {

    /**
     * 显示登录成功
     */
    void showLoginSuccess();

    /**
     * 显示Token失效
     */
    void showTokenInvalid();

    /**
     * 显示服务端错误
     */
    void showServerError();

    /**
     * 附近司机
     *
     * @param data
     */
    void showNears(List<LocationInfo> data);

    /* 显示位置变化
      * @param locationInfo
      */
    void showLocationChange(LocationInfo locationInfo);
}