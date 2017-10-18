package com.zouxiaobang.cloud9.cloud9car.common.databus;

/**
 * Created by zouxiaobang on 10/18/17.
 * 数据订阅者，Presenter要实现这个接口来接收数据
 */

public interface DataBusSubscriber {
    void onEvent(Object object);
}
