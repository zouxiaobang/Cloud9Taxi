package com.dalimao.mytaxi.cloud9car.main.presenter;

import android.util.Log;

import com.dalimao.mytaxi.cloud9car.common.databus.RegisterBus;
import com.dalimao.mytaxi.cloud9car.account.model.IAccountManager;
import com.dalimao.mytaxi.cloud9car.account.model.response.LoginResponse;
import com.dalimao.mytaxi.cloud9car.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;
import com.dalimao.mytaxi.cloud9car.main.model.IMainManager;
import com.dalimao.mytaxi.cloud9car.main.model.bean.Order;
import com.dalimao.mytaxi.cloud9car.main.model.response.NearDriversResponse;
import com.dalimao.mytaxi.cloud9car.main.model.response.OptStateResponse;
import com.dalimao.mytaxi.cloud9car.main.view.IMainView;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class MainPresenterImpl implements IMainPresenter {
    private static final String TAG = "MainPresenterImpl";

    private IMainView mView;
    private IAccountManager mAccountManager;
    private IMainManager mMainManager;
    private Order mCurrentOrder;

    @RegisterBus
    public void onLoginByToken(LoginResponse response) {
        switch (response.getCode()) {
            case IAccountManager.LOGIN_SUCCESS:
                mView.showLoginSuccess();
                break;
            case IAccountManager.TOKEN_INVALID:
                mView.showTokenInvalid();
                break;
            case IAccountManager.SMS_SERVER_FAIL:
                mView.showServerError();
                break;
        }
    }

    @RegisterBus
    public void onNearDriverResponse(NearDriversResponse response) {
        if (response.getCode() == NearDriversResponse.STATE_OK) {
            mView.showNears(response.getData());
        }
    }

    public MainPresenterImpl(IMainView view, IAccountManager manager, IMainManager mainManager) {
        this.mView = view;
        this.mAccountManager = manager;
        this.mMainManager = mainManager;
    }



    @Override
    public void requestLoginByToken() {
        mAccountManager.loginByToken();
    }

    @Override
    public void fetchNearDrivers(double latitude, double longitude) {
        mMainManager.fetchNearDriver(latitude, longitude);
    }

    /**
     *
     * 更新司机的位置
     * @param locationInfo
     */
    @RegisterBus
    public void onLocationInfo(LocationInfo locationInfo) {

        mView.showLocationChange(locationInfo);
    }

    @Override
    public void updateLocationToServer(LocationInfo locationInfo) {
        mMainManager.updateLocationToServer(locationInfo);
    }

    @Override
    public void requestCallDriver(String pushKey, float cost, LocationInfo startLocation, LocationInfo endLocation) {
        mMainManager.callDriver(pushKey, cost, startLocation, endLocation);
    }

    @Override
    public void requestCancel() {
        //  10/25/17 取消订单
        if (mCurrentOrder != null){
            //通知Model层去执行逻辑
            mMainManager.cancelOrder(mCurrentOrder.getOrderId());
            Log.d(TAG, "requestCancel: orderid = " + mCurrentOrder.getOrderId());
        } else {
            mView.showCancelSuc();
        }
    }

    @RegisterBus
    public void onCallDriverCompleted(OptStateResponse response){
        if (response.getState() == OptStateResponse.OPT_STATE_CREATED){
            if (response.getCode() == BaseBizResponse.STATE_OK){
                mView.showCallDriverTip(true);
                mCurrentOrder = response.getData();
                Log.d(TAG, "onCallDriverCompleted: orderid = " + mCurrentOrder.getOrderId());
            } else {
                mView.showCallDriverTip(false);
            }
        } else if (response.getState() == OptStateResponse.OPT_STATE_CANCEL){
            if (response.getCode() == BaseBizResponse.STATE_OK){
                mView.showCancelSuc();
            } else {
                mView.showCancelFail();
            }
        }
    }
}
