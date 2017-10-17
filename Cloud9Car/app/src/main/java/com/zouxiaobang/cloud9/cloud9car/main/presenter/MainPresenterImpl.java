package com.zouxiaobang.cloud9.cloud9car.main.presenter;

import android.os.Handler;
import android.os.Message;

import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.main.view.IMainView;

import java.lang.ref.WeakReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class MainPresenterImpl implements IMainPresenter {
    private IMainView mView;
    private IAccountManager mAccountManager;


    private static class MyHandler extends Handler{
        private WeakReference<MainPresenterImpl> mReference ;

        public MyHandler(MainPresenterImpl presenter){
            mReference = new WeakReference<MainPresenterImpl>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            MainPresenterImpl presenter = mReference.get();
            if (presenter == null)
                return;

            switch (msg.what){
                case IAccountManager.LOGIN_SUCCESS:
                    presenter.mView.showLoginSuccess();
                    break;
                case IAccountManager.TOKEN_INVALID:
                    presenter.mView.showTokenInvalid();
                    break;
                case IAccountManager.SMS_SERVER_FAIL:
                    presenter.mView.showServerError();
                    break;
            }
        }
    }

    public MainPresenterImpl(IMainView view, IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
        mAccountManager.setHandler(new MyHandler(this));
    }


    @Override
    public void requestLoginByToken() {
        mAccountManager.loginByToken();
    }
}
