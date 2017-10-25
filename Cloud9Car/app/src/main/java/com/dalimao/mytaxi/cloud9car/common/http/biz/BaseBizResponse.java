package com.dalimao.mytaxi.cloud9car.common.http.biz;

/**
 * Created by zouxiaobang on 10/16/17.
 * 业务response
 */

public class BaseBizResponse {
    public static final int STATE_OK = 200;
    /**
     * 用户已经存在
     */
    public static final int STATE_USER_EXISTS = 100003;
    /**
     * 用户不存在
     */
    public static final int STATE_USER_NOT_EXISTS = 100002;
    /**
     * 密码输入错误
     */
    public static final int STATE_PW_ERR = 100005;
    /**
     * Token过期
     */
    public static final int STATE_TOKEN_INVALID = 100006;

    /**
     * 状态码
     */
    private int code;
    /**
     * 响应信息
     */
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
