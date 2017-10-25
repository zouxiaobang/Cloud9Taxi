package com.dalimao.mytaxi.cloud9car.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.cloud9car.C9Application;
import com.dalimao.mytaxi.cloud9car.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.cloud9car.account.model.IAccountManager;
import com.dalimao.mytaxi.cloud9car.account.presenter.ISmsCodeDialogPresenter;
import com.dalimao.mytaxi.cloud9car.account.presenter.SmsCodeDialogPresenterImpl;
import com.dalimao.mytaxi.cloud9car.common.databus.RxBus;
import com.dalimao.mytaxi.cloud9car.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.cloud9car.common.storage.SharedPreferenceDao;
import com.dalimao.corelibrary.VerificationCodeInput;
import com.dalimao.mytaxi.cloud9car.common.http.IHttpClient;

/**
 * Created by zouxiaobang on 10/16/17.
 */

public class SmsCodeDialog extends Dialog implements ISmsCodeDialogView {
    private static final String TAG = "SmsCodeDialog";
    private TextView mTvPhone;
    private Button mBtnResend;
    private VerificationCodeInput mVerificationCodeInput;
    private ProgressBar mPbLoading;
    private TextView mTvError;

    private String mPhone;
    private ISmsCodeDialogPresenter mPresenter;

    private CountDownTimer mCountDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mBtnResend.setEnabled(false);
            mBtnResend.setText(String.format(getContext().getString(R.string.after_time_resend),
                            millisUntilFinished/1000));
        }

        @Override
        public void onFinish() {
            mBtnResend.setEnabled(true);
            mBtnResend.setText("重新发送");
            cancel();
        }
    };

    public SmsCodeDialog(@NonNull Context context, String phone) {
        super(context);
        this.mPhone = phone;

        IHttpClient httpClient = new OkHttpClientImpl();
        SharedPreferenceDao dao = new SharedPreferenceDao(C9Application.getInstance(),
                SharedPreferenceDao.FILE_ACCOUNT);
        IAccountManager manager = new AccountManagerImpl(httpClient, dao);
        mPresenter = new SmsCodeDialogPresenterImpl(this, manager);
    }

    public SmsCodeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_smscode_input, null);
        setContentView(root);

        mTvPhone = (TextView) findViewById(R.id.phone);
        String template = getContext().getString(R.string.sending);
        mTvPhone.setText(String.format(template, mPhone));
        mBtnResend = (Button) findViewById(R.id.btn_resend);
        mVerificationCodeInput = (VerificationCodeInput) findViewById(R.id.verificationCodeInput);
        mPbLoading = (ProgressBar) findViewById(R.id.loading);
        mTvError = (TextView) findViewById(R.id.error);
        mTvError.setVisibility(View.GONE);
        initListener();

        RxBus.getInstance().register(mPresenter);

        // 请求下发验证码
        mPresenter.requestSendSmsCode(mPhone);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCountDownTimer.cancel();
    }

    public void dismiss(){
        super.dismiss();

        RxBus.getInstance().unregister(mPresenter);
    }

    private void initListener() {
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mBtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend();
            }
        });

        mVerificationCodeInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String code) {
                commit(code);
            }
        });
    }

    private void commit(String code) {
        showLoading();
        // 10/16/17 网络请求校验验证码
        mPresenter.requestCheckSmsCode(mPhone, code);
    }

    private void resend() {
        String template = "正在向%s发送短信验证码";
        mTvPhone.setText(String.format(template, mPhone));
        mPresenter.requestSendSmsCode(mPhone);
    }

    /**
     * 检测用户名是否存在
     * @param exists
     */
    @Override
    public void showUserExists(boolean exists){
        mPbLoading.setVisibility(View.GONE);
        mTvError.setVisibility(View.GONE);
        dismiss();
        if (!exists){
            //  10/16/17 用户不存在，进入注册对话框
            CreatePasswordDialog dialog = new CreatePasswordDialog(getContext(), mPhone);
            dialog.show();
        } else {
            //  10/16/17 用户存在，进入登录
            LoginDialog dialog = new LoginDialog(getContext(), mPhone);
            dialog.show();
        }
    }

    @Override
    public void showLoading() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 错误处理
     * @param code
     * @param msg
     */
    @Override
    public void showError(int code, String msg) {
        mPbLoading.setVisibility(View.GONE);

        switch (code){
            case IAccountManager.SMS_CHECK_FAIL:
                mTvError.setVisibility(View.VISIBLE);
                mVerificationCodeInput.setEnabled(true);
                break;
            case IAccountManager.SMS_SERVER_FAIL:
                Toast.makeText(getContext(), getContext().getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                break;
            case IAccountManager.SMS_SEND_FAIL:
                Toast.makeText(getContext(), getContext().getString(R.string.sms_check_fail), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 验证码发送成功回调
     */
    @Override
    public void showCountDownTimer() {
        mTvPhone.setText(String.format(getContext()
                .getString(R.string.sms_code_send_phone), mPhone));
        mCountDownTimer.start();
        mBtnResend.setEnabled(false);
    }

    /**
     * 校验验证码是否成功
     * @param b
     */
    @Override
    public void showSmsCodeCheckState(boolean b) {
        if (!b){
            //提示验证码错误
            mTvError.setVisibility(View.VISIBLE);
            mVerificationCodeInput.setEnabled(true);
            mPbLoading.setVisibility(View.GONE);
        } else {
            mTvError.setVisibility(View.GONE);
            mPbLoading.setVisibility(View.VISIBLE);
            // todo 10/16/17 检查用户是否存在
            mPresenter.requestCheckUserExists(mPhone);
        }
    }
}
