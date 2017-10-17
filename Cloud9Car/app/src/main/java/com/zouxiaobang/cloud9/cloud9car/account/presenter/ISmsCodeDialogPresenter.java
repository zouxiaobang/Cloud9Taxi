package com.zouxiaobang.cloud9.cloud9car.account.presenter;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public interface ISmsCodeDialogPresenter {
    /**
     * 下发验证码
     * @param phone
     */
    void fetchSMSCode(String phone);

    /**
     * 校验验证码
     * @param phone
     * @param smsCode
     */
    void checkSmsCode(String phone, String smsCode);

    /**
     * 检查用户是否存在
     * @param phone
     */
    void checkUserExists(String phone);
}
