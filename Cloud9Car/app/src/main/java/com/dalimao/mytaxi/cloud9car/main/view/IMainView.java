package com.dalimao.mytaxi.cloud9car.main.view;

import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;
import com.dalimao.mytaxi.cloud9car.main.model.bean.Order;

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

    /* 显示司机位置变化
      * @param locationInfo
      */
    void showLocationChange(LocationInfo locationInfo);


    /**
     * 呼叫司机成功或失败
     * @param show
     */
    void showCallDriverTip(boolean show);

    /**
     * 显示取消订单成功
     */
    void showCancelSuc();

    /**
     * 显示取消订单失败
     */
    void showCancelFail();

    /**
     * 显示司机接单
     * @param order
     */
    void showDriverAcceptOrder(Order order);

    /**
     * 显示司机接单后到用户附近的路径
     * @param locationInfo
     * @param currentOrder
     */
    void updateDriver2StartRoute(LocationInfo locationInfo, Order currentOrder);

    /**
     * 司机已到达上车点
     * @param currentOrder
     */
    void showArriveStart(Order currentOrder);

    /**
     * 接到用户，开始行程
     * @param currentOrder
     */
    void showStartDrive(Order currentOrder);

    /**
     * 绘制开始行程的路径
     * @param locationInfo
     * @param currentOrder
     */
    void updateDriver2EndRoute(LocationInfo locationInfo, Order currentOrder);

    /**
     * 已到达终点
     * @param currentOrder
     */
    void showArriveEnd(Order currentOrder);

    /**
     * 显示是否支付成功
     * @param b
     */
    void showPayResult(boolean b);
}