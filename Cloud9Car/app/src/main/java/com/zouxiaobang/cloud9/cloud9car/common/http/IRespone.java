package com.zouxiaobang.cloud9.cloud9car.common.http;

/**
 * Created by zouxiaobang on 10/15/17.
 */

public interface IRespone {
    /**
     * 状态码
     * @return
     */
    int getCode();

    /**
     * 数据体
     * @return
     */
    String getData();
}
