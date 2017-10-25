package com.dalimao.mytaxi.cloud9car.main.model.response;

import com.dalimao.mytaxi.cloud9car.common.http.biz.BaseBizResponse;

/**
 * Created by zouxiaobang on 10/25/17.
 */

public class OptStateResponse extends BaseBizResponse {
    public static final int OPT_STATE_CREATED = 0;

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
