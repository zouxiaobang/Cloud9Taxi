package com.zouxiaobang.cloud9.cloud9car.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zouxiaobang.cloud9.cloud9car.C9Application;
import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.Account;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.OkHttpClientImpl;
import com.zouxiaobang.cloud9.cloud9car.common.storage.SharedPreferenceDao;

import java.lang.ref.SoftReference;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class LoginDialog extends Dialog {
    private static final String TAG = "LoginDialog";
    private static final int LOGIN_SUCCESS = 1;
    private static final int SERVER_FAIL = 2;
    private static final int PASSWORD_ERROR = 3;
    private TextView mTvPhone;
    private EditText mEtPw;
    private Button mBtnConfirm;
    private ProgressBar mPbLoading;
    private TextView mTvTips;

    private String mPhoneStr;
    private IHttpClient mClient;
    private MyHander mHander;

    public LoginDialog(@NonNull Context context, String phone) {
        super(context);
        mPhoneStr = phone;
        mClient = new OkHttpClientImpl();
        mHander = new MyHander(this);
    }

    public LoginDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_login_input, null);
        setContentView(root);

        initView();
    }

    private void initView() {
        mTvPhone = (TextView) findViewById(R.id.phone);
        mEtPw = (EditText) findViewById(R.id.password);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mPbLoading = (ProgressBar) findViewById(R.id.loading);
        mTvTips = (TextView) findViewById(R.id.tips);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        mTvPhone.setText(mPhoneStr);
    }

    /**
     * 提交请求
     */
    private void submit() {
        final String password = mEtPw.getText().toString();
        //登录
        new Thread(){
            @Override
            public void run() {
                //获取url
                String url = API.Config.getDomain() + API.LOGIN;
                //创建Request
                IRequest request = new BaseRequest(url);
                request.setBody("phone", mPhoneStr);
                request.setBody("password", password);
                //执行请求
                IRespone respone = mClient.post(request, false);
                Log.d(TAG, "run: " + respone.getData());
                //处理Response
                if (respone.getCode() == BaseBizResponse.STATE_OK){
                    LoginResponse loginResponse
                            = new Gson().fromJson(respone.getData(), LoginResponse.class);

                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK){
                        Account account = loginResponse.getData();
                        SharedPreferenceDao dao
                                = new SharedPreferenceDao(C9Application.getInstance(),
                                SharedPreferenceDao.FILE_ACCOUNT);
                        dao.save(SharedPreferenceDao.KEY_ACCOUNT, account);
                        mHander.sendEmptyMessage(LOGIN_SUCCESS);
                    } else if (loginResponse.getCode() == BaseBizResponse.STATE_PW_ERR){
                        mHander.sendEmptyMessage(PASSWORD_ERROR);
                    } else {
                        mHander.sendEmptyMessage(SERVER_FAIL);
                    }
                } else {
                    mHander.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
    }

    private void showLoginSuccess() {
        dismiss();
        mPbLoading.setVisibility(View.GONE);
        mBtnConfirm.setVisibility(View.GONE);
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.login_suc));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        Toast.makeText(getContext(), getContext().getString(R.string.login_suc), Toast.LENGTH_SHORT).show();
    }

    private void showServerError() {
        mPbLoading.setVisibility(View.GONE);
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.error_server));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
    }

    private void showPwError(){
        mPbLoading.setVisibility(View.GONE);
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.pw_error));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    static class MyHander extends Handler{
        private SoftReference<LoginDialog> mLoginDialogSoftReference;
        public MyHander(LoginDialog dialog){
            mLoginDialogSoftReference = new SoftReference<LoginDialog>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginDialog dialog = mLoginDialogSoftReference.get();
            if (dialog == null){
                return;
            }

            switch (msg.what){
                case LOGIN_SUCCESS:
                    dialog.showLoginSuccess();
                    break;
                case SERVER_FAIL:
                    dialog.showServerError();
                    break;
                case PASSWORD_ERROR:
                    dialog.showPwError();
                    break;
            }
        }
    }
}
