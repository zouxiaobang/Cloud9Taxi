package com.zouxiaobang.cloud9.cloud9car.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.google.gson.Gson;
import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.OkHttpClientImpl;

import java.lang.ref.SoftReference;
import java.net.URL;

/**
 * Created by zouxiaobang on 10/16/17.
 */

public class SmsCodeDialog extends Dialog {
    private static final String TAG = "SmsCodeDialog";
    private static final int SMS_SEND_SUCCESS = 1;
    private static final int SMS_SEND_FAIL = -1;
    private static final int SMS_CHECK_SUCCESS = 2;
    private static final int SMS_CHECK_FAIL = -2;
    private static final int USER_EXISTS = 3;
    private static final int USER_NOT_EXISTS = -3;
    private static final int SMS_SERVICE_FAIL = 100;
    private TextView mTvPhone;
    private Button mBtnResend;
    private VerificationCodeInput mVerificationCodeInput;
    private ProgressBar mPbLoading;
    private TextView mTvError;

    private String mPhone;
    private IHttpClient mClient;
    private MyHandler mHandler;

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
        mClient = new OkHttpClientImpl();
        mHandler = new MyHandler(this);
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

        // 请求下发验证码
        requestSendSmsCode();
    }

    /**
     * 请求下发验证码
     *
     */
    private void requestSendSmsCode() {
        new Thread(){
            @Override
            public void run() {
                //创建url
                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", mPhone);
                IRespone respone = mClient.get(request, false);
                Log.d(TAG, "run: " + respone.getCode() + " : " + respone.getData());
                /** {"code":200,"msg":"code has send"} **/

                if (respone.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(), BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(SMS_SEND_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCountDownTimer.cancel();
    }

    public void dismiss(){
        super.dismiss();
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

    private void commit(final String code) {
        showLoading();
        //  10/16/17 网络请求校验验证码
        new Thread(){
            @Override
            public void run() {
                //创建url
                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", mPhone);
                request.setBody("code", code);
                IRespone respone = mClient.get(request, false);
                Log.d(TAG, "run: " + respone.getCode() + " : " + respone.getData());
                /** {"code":200,"data":{},"msg":"succ SMS code"} **/

                if (respone.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(), BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(SMS_CHECK_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                }
            }
        }.start();
    }

    private void resend() {
        String template = "正在向%s发送短信验证码";
        mTvPhone.setText(String.format(template, mPhone));
    }

    private void showLoading() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    public void showVerifyState(boolean suc){
        if (!suc){
            //提示验证码错误
            mTvError.setVisibility(View.VISIBLE);
            mVerificationCodeInput.setEnabled(true);
            mPbLoading.setVisibility(View.GONE);
        } else {
            mTvError.setVisibility(View.GONE);
            mPbLoading.setVisibility(View.VISIBLE);
            //  10/16/17 检查用户是否存在
            new Thread(){
                @Override
                public void run() {
                    //创建url
                    String url = API.Config.getDomain() + API.CHECK_USER_EXISTS;
                    //创建Request对象
                    IRequest request = new BaseRequest(url);
                    request.setBody("phone", mPhone);
                    IRespone respone = mClient.get(request, false);
                    Log.d(TAG, "run: " + respone.getCode() + " : " + respone.getData());
                    /** {"code":200,"data":{},"msg":"succ SMS code"} **/

                    if (respone.getCode() == BaseBizResponse.STATE_OK){
                        BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(), BaseBizResponse.class);
                        if (bizResponse.getCode() == BaseBizResponse.STATE_USER_EXISTS){
                            mHandler.sendEmptyMessage(USER_EXISTS);
                        } else if (bizResponse.getCode() == BaseBizResponse.STATE_USER_NOT_EXISTS){
                            mHandler.sendEmptyMessage(USER_NOT_EXISTS);
                        }
                    } else {
                        mHandler.sendEmptyMessage(SMS_SERVICE_FAIL);
                    }
                }
            }.start();
        }
    }

    public void showUserExists(boolean exists){
        mPbLoading.setVisibility(View.GONE);
        mTvError.setVisibility(View.GONE);
        dismiss();
        if (!exists){
            //  10/16/17 用户不存在，进入注册对话框
            dismiss();
            CreatePasswordDialog dialog = new CreatePasswordDialog(getContext(), mPhone);
            dialog.show();
        } else {
            // TODO: 10/16/17 用户存在，进入登录
        }
    }

    /**
     * 使用静态类，防止内存泄漏
     */
    static class MyHandler extends Handler{
        //软引用：随时被回收 -- 当界面不存在的时候
        SoftReference<SmsCodeDialog> mSmsCodeDialogSoftReference;
        public MyHandler(SmsCodeDialog codeDialog){
            mSmsCodeDialogSoftReference = new SoftReference<SmsCodeDialog>(codeDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            SmsCodeDialog dialog = mSmsCodeDialogSoftReference.get();
            if (dialog == null){
                return;
            }

            // UI响应处理
            switch (msg.what){
                case SMS_SEND_SUCCESS:
                    //打开倒计时
                    dialog.mCountDownTimer.start();
                    break;
                case SMS_SEND_FAIL:
                    Toast.makeText(dialog.getContext(),
                            dialog.getContext().getString(R.string.sms_send_fail),
                            Toast.LENGTH_SHORT).show();
                    break;
                case SMS_CHECK_SUCCESS:
                    // 10/16/17  验证码校验成功
                    dialog.showVerifyState(true);
                    break;
                case SMS_CHECK_FAIL:
                    dialog.showVerifyState(false);
                    break;
                case USER_EXISTS:
                    //  10/16/17 用户存在
                    dialog.showUserExists(true);
                    break;
                case USER_NOT_EXISTS:
                    //  10/16/17 用户不存在
                    dialog.showUserExists(false);
                    break;
                case SMS_SERVICE_FAIL:
                    Toast.makeText(dialog.getContext(),
                            dialog.getContext().getString(R.string.error_server),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
