package com.zouxiaobang.cloud9.cloud9car.account.model;

/**
 * Created by zouxiaobang on 10/17/17.
 * Model层接口
 */

public interface IAccountManager {
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

    /**
     * 根据Token进行登录
     */
    void loginByToken();
}
