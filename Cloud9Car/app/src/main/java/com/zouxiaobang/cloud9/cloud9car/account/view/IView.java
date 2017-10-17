package com.zouxiaobang.cloud9.cloud9car.account.view;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface IView {
    /**
     * 显示Loading
     */
    void showLoading();

    /**
     * 显示错误信息
     */
    void showError(int code, String msg);
}
