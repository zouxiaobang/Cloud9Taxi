package com.zouxiaobang.cloud9.cloud9car.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.account.view.ISmsCodeDialogView;

import java.lang.ref.WeakReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class SmsCodeDialogPresenterImpl implements ISmsCodeDialogPresenter {
    private ISmsCodeDialogView mView;
    private IAccountManager mAccountManager;
    private MyHandler mHandler;

    private static class MyHandler extends Handler{
        private WeakReference<SmsCodeDialogPresenterImpl> mPresenterWeakReference;

        public MyHandler(SmsCodeDialogPresenterImpl presenter){
            mPresenterWeakReference = new WeakReference<SmsCodeDialogPresenterImpl>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            SmsCodeDialogPresenterImpl presenter = mPresenterWeakReference.get();
            if (presenter == null){
                return;
            }

            switch (msg.what){
                case IAccountManager.SMS_SEND_SUCCESS:
                    presenter.mView.showCountDownTimer();
                    break;
                case IAccountManager.SMS_SEND_FAIL:
                    presenter.mView.showError(IAccountManager.SMS_SEND_FAIL, "");
                    break;
                case IAccountManager.SMS_CHECK_SUCCESS:
                    presenter.mView.showSmsCodeCheckState(true);
                    break;
                case IAccountManager.SMS_CHECK_FAIL:
                    presenter.mView.showError(IAccountManager.SMS_CHECK_FAIL, "");
                    break;
                case IAccountManager.USER_EXISTS:
                    presenter.mView.showUserExists(true);
                    break;
                case IAccountManager.USER_NOT_EXISTS:
                    presenter.mView.showUserExists(false);
                    break;
                case IAccountManager.SMS_SERVER_FAIL:
                    presenter.mView.showError(IAccountManager.SMS_SERVER_FAIL, "");
                    break;
            }
        }
    }

    public SmsCodeDialogPresenterImpl(ISmsCodeDialogView view, IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
        mHandler = new MyHandler(this);
        mAccountManager.setHandler(mHandler);
    }

    @Override
    public void requestSendSmsCode(String phone) {
        mAccountManager.fetchSMSCode(phone);
    }

    @Override
    public void requestCheckSmsCode(String phone, String smsCode) {
        mAccountManager.checkSmsCode(phone, smsCode);
    }

    @Override
    public void requestCheckUserExists(String phone) {
        mAccountManager.checkUserExists(phone);
    }
}
