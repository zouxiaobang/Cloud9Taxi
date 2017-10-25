package com.dalimao.mytaxi.cloud9car.account.model.response;

import com.dalimao.mytaxi.cloud9car.common.http.biz.BaseBizResponse;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class LoginResponse extends BaseBizResponse{
    Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
