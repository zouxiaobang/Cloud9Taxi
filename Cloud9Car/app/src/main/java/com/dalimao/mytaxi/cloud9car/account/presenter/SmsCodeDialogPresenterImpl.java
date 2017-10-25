package com.dalimao.mytaxi.cloud9car.account.presenter;

import android.util.Log;

import com.dalimao.mytaxi.cloud9car.account.model.IAccountManager;
import com.dalimao.mytaxi.cloud9car.account.model.response.SmsCodeResponse;
import com.dalimao.mytaxi.cloud9car.account.model.response.UserExistsResponse;
import com.dalimao.mytaxi.cloud9car.account.view.ISmsCodeDialogView;
import com.dalimao.mytaxi.cloud9car.common.databus.RegisterBus;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class SmsCodeDialogPresenterImpl implements ISmsCodeDialogPresenter {
    private static final String TAG = "SCDPI";
    private ISmsCodeDialogView mView;
    private IAccountManager mAccountManager;

    @RegisterBus
    public void onSendCode(SmsCodeResponse response){
        switch (response.getCode()){
            case IAccountManager.SMS_SEND_SUCCESS:
                mView.showCountDownTimer();
                break;
            case IAccountManager.SMS_SEND_FAIL:
                mView.showError(IAccountManager.SMS_SEND_FAIL, "");
                break;
            case IAccountManager.SMS_CHECK_SUCCESS:
                mView.showSmsCodeCheckState(true);
                break;
            case IAccountManager.SMS_CHECK_FAIL:
                mView.showError(IAccountManager.SMS_CHECK_FAIL, "");
                break;
            case IAccountManager.SMS_SERVER_FAIL:
                mView.showError(IAccountManager.SMS_SERVER_FAIL, "");
                break;
        }
    }

    @RegisterBus
    public void onUserExists(UserExistsResponse response){
        Log.d(TAG, "onUserExists: ");
        switch (response.getCode()){
            case IAccountManager.USER_EXISTS:
                mView.showUserExists(true);
                break;
            case IAccountManager.USER_NOT_EXISTS:
                mView.showUserExists(false);
                break;
            case IAccountManager.SMS_SERVER_FAIL:
                mView.showError(IAccountManager.SMS_SERVER_FAIL, "");
                break;
        }
    }

    public SmsCodeDialogPresenterImpl(ISmsCodeDialogView view, IAccountManager manager){
        this.mView = view;
        this.mAccountManager = manager;
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
