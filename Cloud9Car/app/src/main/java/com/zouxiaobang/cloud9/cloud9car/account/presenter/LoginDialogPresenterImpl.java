package com.zouxiaobang.cloud9.cloud9car.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.account.view.ILoginDialogView;

import java.lang.ref.WeakReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class LoginDialogPresenterImpl implements ILoginDialogPresenter {
    private ILoginDialogView mView;
    private IAccountManager mAccountManager;

    private static class MyHandler extends Handler{
        private WeakReference<LoginDialogPresenterImpl> mReference ;

        public MyHandler(LoginDialogPresenterImpl presenter){
            mReference = new WeakReference<LoginDialogPresenterImpl>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginDialogPresenterImpl presenter = mReference.get();
            if (presenter == null)
                return;

            switch (msg.what){
                case IAccountManager.LOGIN_SUCCESS:
                    presenter.mView.showLoginSuccess();
                    break;
                case IAccountManager.PASSWORD_ERROR:
                    presenter.mView.showError(IAccountManager.PASSWORD_ERROR, "");
                    break;
                case IAccountManager.SMS_SERVER_FAIL:
                    presenter.mView.showError(IAccountManager.SMS_SERVER_FAIL, "");
                    break;
            }
        }
    }

    public LoginDialogPresenterImpl(ILoginDialogView view, IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
        mAccountManager.setHandler(new MyHandler(this));
    }


    @Override
    public void requestLogin(String phone, String password) {
        mAccountManager.login(phone, password);
    }
}
