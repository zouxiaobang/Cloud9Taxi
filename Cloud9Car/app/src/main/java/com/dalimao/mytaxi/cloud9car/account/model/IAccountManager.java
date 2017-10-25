package com.dalimao.mytaxi.cloud9car.account.model;

/**
 * Created by zouxiaobang on 10/17/17.
 * Model层接口
 */

public interface IAccountManager {

    public static final int SMS_SERVER_FAIL = 100;
    public static final int SMS_SEND_SUCCESS = 1;
    public static final int SMS_SEND_FAIL = -1;
    public static final int SMS_CHECK_SUCCESS = 2;
    public static final int SMS_CHECK_FAIL = -2;
    public static final int USER_EXISTS = 3;
    public static final int USER_NOT_EXISTS = -3;
    public static final int REGISTER_SUCCESS = 4;
    public static final int LOGIN_SUCCESS = 5;
    public static final int TOKEN_INVALID = -6;
    public static final int PASSWORD_ERROR = -7;


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
