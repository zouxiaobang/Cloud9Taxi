package com.dalimao.mytaxi.cloud9car.main.model;

import android.util.Log;

import com.dalimao.mytaxi.cloud9car.common.databus.RxBus;
import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;
import com.google.gson.Gson;
import com.dalimao.mytaxi.cloud9car.common.http.IHttpClient;
import com.dalimao.mytaxi.cloud9car.common.http.IRequest;
import com.dalimao.mytaxi.cloud9car.common.http.IRespone;
import com.dalimao.mytaxi.cloud9car.common.http.api.API;
import com.dalimao.mytaxi.cloud9car.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.cloud9car.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.cloud9car.main.model.response.NearDriversResponse;

import rx.functions.Func1;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public class MainManagerImpl implements IMainManager {
    private static final String TAG = "MainManagerImpl";

    IHttpClient mClient;

    public MainManagerImpl(IHttpClient client) {
        mClient = client;
    }

    @Override
    public void fetchNearDriver(final double latitude, final double longitude) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.GET_NEAR_DRIVERS);
                request.setBody("latitude", String.valueOf(latitude));
                request.setBody("longitude", String.valueOf(longitude));
                IRespone respone = mClient.get(request, false);

                if (respone.getCode() == BaseBizResponse.STATE_OK) {
                    try {
                        NearDriversResponse nearDriversResponse
                                = new Gson().fromJson(respone.getData(), NearDriversResponse.class);
                        return nearDriversResponse;
                    } catch (Exception e) {
                        return null;
                    }
                }

                return null;
            }
        });
    }

    @Override
    public void updateLocationToServer(final LocationInfo locationInfo) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.UPLOAD_LOCATION);
                request.setBody("latitude",
                        new Double(locationInfo.getLatitude()).toString());
                request.setBody("longitude",
                        new Double(locationInfo.getLongitude()).toString());
                request.setBody("key", locationInfo.getKey());
                request.setBody("rotation",
                        new Float(locationInfo.getRotation()).toString());
                IRespone response = mClient.post(request, false);
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    Log.d(TAG, "位置上报成功");
                } else {
                    Log.d(TAG, "位置上报失败");
                }
                return null;
            }
        });
    }
}
