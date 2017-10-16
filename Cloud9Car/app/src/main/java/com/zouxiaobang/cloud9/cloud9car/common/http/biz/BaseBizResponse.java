package com.zouxiaobang.cloud9.cloud9car.common.http.biz;

/**
 * Created by zouxiaobang on 10/16/17.
 * 业务response
 */

public class BaseBizResponse {
    public static final int STATE_OK = 200;

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
