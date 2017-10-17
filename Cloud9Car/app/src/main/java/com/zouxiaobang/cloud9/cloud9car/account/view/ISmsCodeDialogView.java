package com.zouxiaobang.cloud9.cloud9car.account.view;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface ISmsCodeDialogView extends IView {

    /**
     * 显示倒计时
     */
    void showCountDownTimer();

    /**
     * 关闭视图
     */
    void close();
}
