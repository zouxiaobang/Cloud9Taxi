package com.dalimao.mytaxi.cloud9car.main.model.response;

import com.dalimao.mytaxi.cloud9car.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;

import java.util.List;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public class NearDriversResponse extends BaseBizResponse {
    List<LocationInfo> data;

    public List<LocationInfo> getData() {
        return data;
    }

    public void setData(List<LocationInfo> data) {
        this.data = data;
    }
}