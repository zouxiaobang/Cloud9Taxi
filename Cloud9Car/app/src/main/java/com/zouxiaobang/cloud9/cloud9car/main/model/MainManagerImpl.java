package com.zouxiaobang.cloud9.cloud9car.main.model;

import com.google.gson.Gson;
import com.zouxiaobang.cloud9.cloud9car.common.databus.RxBus;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.main.model.response.NearDriversResponse;

import rx.functions.Func1;

/**
 * Created by zouxiaobang on 10/21/17.
 */

public class MainManagerImpl implements IMainManager {
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

                if (respone.getCode() == BaseBizResponse.STATE_OK){
                    try {
                        NearDriversResponse nearDriversResponse
                                = new Gson().fromJson(respone.getData(), NearDriversResponse.class);
                        return nearDriversResponse;
                    } catch (Exception e){
                        return null;
                    }
                }

                return null;
            }
        });
    }
}
