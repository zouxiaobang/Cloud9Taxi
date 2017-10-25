package com.dalimao.mytaxi.cloud9car.account.presenter;

import com.dalimao.mytaxi.cloud9car.account.model.IAccountManager;
import com.dalimao.mytaxi.cloud9car.account.model.response.LoginResponse;
import com.dalimao.mytaxi.cloud9car.account.view.ILoginDialogView;
import com.dalimao.mytaxi.cloud9car.common.databus.RegisterBus;

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
