package com.dalimao.mytaxi.cloud9car.account.presenter;

import com.dalimao.mytaxi.cloud9car.account.model.IAccountManager;
import com.dalimao.mytaxi.cloud9car.account.model.response.LoginResponse;
import com.dalimao.mytaxi.cloud9car.account.view.ICreatePasswordDialogView;
import com.dalimao.mytaxi.cloud9car.common.databus.RegisterBus;
import com.dalimao.mytaxi.cloud9car.account.model.response.RegisterResponse;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class CreatePasswordDialogPresenterImpl implements ICreatePasswordDialogPresenter {
    private ICreatePasswordDialogView mView;
    private IAccountManager mAccountManager;

    @RegisterBus
    public void onRegister(RegisterResponse response){
        switch (response.getCode()){
            case IAccountManager.REGISTER_SUCCESS:
                mView.showRegisterSuccess();
                break;
            case IAccountManager.SMS_SERVER_FAIL:
                mView.showError(IAccountManager.SMS_SERVER_FAIL, "");
                break;
        }
    }

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

    public CreatePasswordDialogPresenterImpl(ICreatePasswordDialogView view,
                                             IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
    }

    @Override
    public boolean requestCheckPw(String pw, String pw1) {
        if (pw == null || pw.equals("")) {
            mView.showPasswordNull();
            return false;
        }
        if (!pw.equals(pw1)) {
            mView.showPasswordNotEqual();
            return false;
        }
        return true;
    }

    @Override
    public void requestRegister(String phone, String password) {
        mAccountManager.register(phone, password);
    }

    @Override
    public void requestLogin(String phone, String password) {
        mAccountManager.login(phone, password);
    }
}
