package com.dalimao.mytaxi.cloud9car.account.presenter;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface ILoginDialogPresenter {

    /**
     * 用户登录
     */
    void requestLogin(String phone, String password);
}
