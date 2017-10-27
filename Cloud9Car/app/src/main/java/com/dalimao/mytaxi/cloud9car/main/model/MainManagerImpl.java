package com.dalimao.mytaxi.cloud9car.main.model;

import android.util.Log;

import com.dalimao.mytaxi.cloud9car.C9Application;
import com.dalimao.mytaxi.cloud9car.account.model.response.Account;
import com.dalimao.mytaxi.cloud9car.common.databus.RxBus;
import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;
import com.dalimao.mytaxi.cloud9car.common.storage.SharedPreferenceDao;
import com.dalimao.mytaxi.cloud9car.main.model.bean.Order;
import com.dalimao.mytaxi.cloud9car.main.model.response.OptStateResponse;
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

    @Override
    public void callDriver(final String pushKey, final float cost, final LocationInfo startLocation, final LocationInfo endLocation) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                //获取uid和phone
                SharedPreferenceDao dao = new SharedPreferenceDao(C9Application.getInstance(),
                        SharedPreferenceDao.FILE_ACCOUNT);
                Account account = (Account) dao.get(SharedPreferenceDao.KEY_ACCOUNT, Account.class);
                String uid = account.getUid();
                String phone = account.getAccount();

                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.CALL_DRIVER);
                request.setBody("key", pushKey);
                request.setBody("uid",uid);
                request.setBody("phone", phone);
                request.setBody("startLatitude",
                        new Double(startLocation.getLatitude()).toString() );
                request.setBody("startLongitude",
                        new Double(startLocation.getLongitude()).toString() );
                request.setBody("endLatitude",
                        new Double(endLocation.getLatitude()).toString() );
                request.setBody("endLongitude",
                        new Double(endLocation.getLongitude()).toString() );
                request.setBody("cost", new Float(cost).toString());

                Log.d(TAG, "call: key = " + pushKey + " : userid = " + uid + " : phone = " + phone);

                Log.d(TAG, "call: " + startLocation.getLatitude() + " : " + endLocation.getLatitude());

                IRespone response = mClient.post(request, false);
                OptStateResponse optStateResponse = new OptStateResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    optStateResponse = new Gson().fromJson(response.getData(),
                            OptStateResponse.class);
                    Order data = optStateResponse.getData();
                    Log.d(TAG, "call: orderid = " + response.getData());
                    Log.d(TAG, "call: orderid = " + data.getOrderId());
                }

                optStateResponse.setCode(response.getCode());
                optStateResponse.setState(OptStateResponse.OPT_STATE_CREATED);
                return optStateResponse;
            }
        });
    }

    @Override
    public void cancelOrder(final String orderId) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.Config.getDomain()
                        + API.CANCEL_ORDER);
                request.setBody("id", orderId);
                Log.d(TAG, "call: orderid = " + orderId);

                IRespone response = mClient.post(request, false);
                OptStateResponse optStateResponse = new OptStateResponse();
                optStateResponse.setCode(response.getCode());
                optStateResponse.setState(OptStateResponse.OPT_STATE_CANCEL);
                return optStateResponse;
            }
        });
    }
}
