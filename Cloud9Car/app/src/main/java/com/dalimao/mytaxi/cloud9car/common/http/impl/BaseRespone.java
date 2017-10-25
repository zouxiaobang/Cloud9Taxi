package com.dalimao.mytaxi.cloud9car.common.http.impl;

import com.dalimao.mytaxi.cloud9car.common.http.IRespone;

/**
 * Created by zouxiaobang on 10/15/17.
 */

public class BaseRespone implements IRespone {
    public static final int STATE_UNKNOWN_ERROR = 1001;
    public static final int STATE_OK = 200;

    private int mCode;
    private String mData;

    @Override
    public int getCode() {
        return mCode;
    }

    @Override
    public String getData() {
        return mData;
    }

    public void setCode(int code){
        this.mCode = code;
    }

    public void setData(String data){
        this.mData = data;
    }
}
