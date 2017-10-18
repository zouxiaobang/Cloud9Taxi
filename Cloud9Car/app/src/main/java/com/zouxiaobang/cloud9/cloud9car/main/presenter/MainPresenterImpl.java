package com.zouxiaobang.cloud9.cloud9car.main.presenter;

import android.os.Handler;
import android.os.Message;

import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.common.databus.RegisterBus;
import com.zouxiaobang.cloud9.cloud9car.main.view.IMainView;

import java.lang.ref.WeakReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class MainPresenterImpl implements IMainPresenter {
    private IMainView mView;
    private IAccountManager mAccountManager;


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

    public MainPresenterImpl(IMainView view, IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
    }


    @Override
    public void requestLoginByToken() {
        mAccountManager.loginByToken();
    }
}
