package com.dalimao.mytaxi.cloud9car.main.model.response;

import com.dalimao.mytaxi.cloud9car.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.cloud9car.main.model.bean.Order;

/**
 * Created by zouxiaobang on 10/25/17.
 */

public class OptStateResponse extends BaseBizResponse {
    public static final int OPT_STATE_CREATED = 0;
    public static final int OPT_STATE_CANCEL = -1;
    public static final int OPT_STATE_ACCESS = 1;
    public static final int OPT_STATE_ARRIVE_START = 2;
    public static final int OPT_STATE_START_DRIVE = 3;
    public static final int OPT_STATE_ARRIVE_END = 4;


    private int state;
    private Order data;

    public Order getData() {
        return data;
    }

    public void setData(Order data) {
        this.data = data;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}