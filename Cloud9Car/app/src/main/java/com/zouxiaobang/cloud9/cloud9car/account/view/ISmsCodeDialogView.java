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
     * 显示验证状态
     * @param b
     */
    void showSmsCodeCheckState(boolean b);

    /**
     * 显示用户是否存在
     * @param b
     */
    void showUserExists(boolean b);
}
