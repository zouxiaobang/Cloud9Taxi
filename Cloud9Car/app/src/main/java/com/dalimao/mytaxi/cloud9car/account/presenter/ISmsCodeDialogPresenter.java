package com.dalimao.mytaxi.cloud9car.account.presenter;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface ISmsCodeDialogPresenter {
    /**
     * 下发验证码
     * @param phone
     */
    void requestSendSmsCode(String phone);

    /**
     * 校验验证码
     * @param phone
     * @param smsCode
     */
    void requestCheckSmsCode(String phone, String smsCode);

    /**
     * 检查用户是否存在
     * @param phone
     */
    void requestCheckUserExists(String phone);
}
