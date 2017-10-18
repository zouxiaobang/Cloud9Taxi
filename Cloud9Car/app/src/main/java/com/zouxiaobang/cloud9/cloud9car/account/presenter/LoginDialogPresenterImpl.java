package com.zouxiaobang.cloud9.cloud9car.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.account.view.ILoginDialogView;
import com.zouxiaobang.cloud9.cloud9car.common.databus.RegisterBus;

import java.lang.ref.WeakReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class LoginDialogPresenterImpl implements ILoginDialogPresenter {
    private ILoginDialogView mView;
    private IAccountManager mAccountManager;

    @RegisterBus
    public void onLogin(LoginResponse response){
        switch (response.getCode()){
            case IAccountManager.LOGIN_SUCCESS:
                mView.showLoginSuccess();
                break;
            case IAccountManager.PASSWORD_ERROR:
                mView.showError(IAccountManager.PASSWORD_ERROR, "");
                break;
            case IAccountManager.SMS_SERVER_FAIL:
                mView.showError(IAccountManager.SMS_SERVER_FAIL, "");
                break;
        }
    }

    public LoginDialogPresenterImpl(ILoginDialogView view, IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
    }


    @Override
    public void requestLogin(String phone, String password) {
        mAccountManager.login(phone, password);
    }
}
