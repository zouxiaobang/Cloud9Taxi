package com.zouxiaobang.cloud9.cloud9car.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.account.view.ICreatePasswordDialogView;

import java.lang.ref.WeakReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class CreatePasswordDialogPresenterImpl implements ICreatePasswordDialogPresenter {
    private ICreatePasswordDialogView mView;
    private IAccountManager mAccountManager;


    private static class MyHandler extends Handler {
        WeakReference<CreatePasswordDialogPresenterImpl> mReference;

        public MyHandler(CreatePasswordDialogPresenterImpl presenter) {
            mReference = new WeakReference<CreatePasswordDialogPresenterImpl>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            CreatePasswordDialogPresenterImpl presenter = mReference.get();
            if (presenter == null)
                return;

            switch (msg.what){
                case IAccountManager.REGISTER_SUCCESS:
                    presenter.mView.showRegisterSuccess();
                    break;
                case IAccountManager.LOGIN_SUCCESS:
                    presenter.mView.showLoginSuccess();
                    break;
                case IAccountManager.SMS_SERVER_FAIL:
                    presenter.mView.showError(IAccountManager.SMS_SERVER_FAIL, "");
                    break;
                case IAccountManager.PASSWORD_ERROR:
                    presenter.mView.showError(IAccountManager.PASSWORD_ERROR, "");
                    break;
            }
        }
    }
    public CreatePasswordDialogPresenterImpl(ICreatePasswordDialogView view,
                                             IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
        mAccountManager.setHandler(new MyHandler(this));
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
