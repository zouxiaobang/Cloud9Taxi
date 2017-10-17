package com.zouxiaobang.cloud9.cloud9car.account.view;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface ICreatePasswordDialogView extends IView {

    /**
     * 显示注册成功
     */
    void showRegisterSuccess();

    /**
     * 显示登录成功
     */
    void showLoginSuccess();

    /**
     * 提示密码不能为空
     */
    void showPasswordNull();

    /**
     * 提示两次输入密码不同
     */
    void showPasswordNotEqual();
}
