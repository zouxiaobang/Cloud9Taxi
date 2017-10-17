package com.zouxiaobang.cloud9.cloud9car.account.presenter;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface ICreatePasswordDialogPresenter {

    /**
     * 检验密码输入的合法性
     * @param pw
     * @param pw1
     */
    void checkPw(String pw, String pw1);

    /**
     * 注册
     * @param phone
     * @param password
     */
    void register(String phone, String password);

    /**
     * 登录
     * @param phone
     * @param password
     */
    void login(String phone, String password);
}
