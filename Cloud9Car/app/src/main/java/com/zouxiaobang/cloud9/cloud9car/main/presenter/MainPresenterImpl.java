package com.zouxiaobang.cloud9.cloud9car.main.presenter;

import android.os.Handler;
import android.os.Message;

import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.common.databus.RegisterBus;
import com.zouxiaobang.cloud9.cloud9car.main.model.IMainManager;
import com.zouxiaobang.cloud9.cloud9car.main.model.response.NearDriversResponse;
import com.zouxiaobang.cloud9.cloud9car.main.view.IMainView;

import java.lang.ref.WeakReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class MainPresenterImpl implements IMainPresenter {
    private IMainView mView;
    private IAccountManager mAccountManager;
    private IMainManager mMainManager;

    @RegisterBus
    public void onLoginByToken(LoginResponse response){
        switch (response.getCode()){
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
    public void onNearDriverResponse(NearDriversResponse response){
        if (response.getCode() == NearDriversResponse.STATE_OK){
            mView.showNears(response.getData());
        }
    }

    public MainPresenterImpl(IMainView view, IAccountManager manager, IMainManager mainManager){
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
}
