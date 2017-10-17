package com.zouxiaobang.cloud9.cloud9car.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
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
import com.zouxiaobang.cloud9.cloud9car.account.model.AccountManagerImpl;
import com.zouxiaobang.cloud9.cloud9car.account.model.IAccountManager;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.Account;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.account.presenter.CreatePasswordDialogPresenterImpl;
import com.zouxiaobang.cloud9.cloud9car.account.presenter.ICreatePasswordDialogPresenter;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.OkHttpClientImpl;
import com.zouxiaobang.cloud9.cloud9car.common.storage.SharedPreferenceDao;
import com.zouxiaobang.cloud9.cloud9car.common.utils.DevUtil;

import java.lang.ref.SoftReference;

/**
 * Created by zouxiaobang on 10/16/17.
 * 创建密码和修改密码的对话框
 */

public class CreatePasswordDialog extends Dialog implements ICreatePasswordDialogView {

    private static final String TAG = "CreatePasswordDialog";
    private static final int REGISTER_SUCCESS = 1;
    private static final int SERVER_FAIL = 100;
    private static final int LOGIN_SUCCESS = 2;
    private TextView mTvPhone;
    private EditText mEtPw;
    private EditText mEtPw1;
    private Button mBtnConfirm;
    private ProgressBar mPbLoading;
    private TextView mTvTips;

    private String mPhoneStr;
    private ICreatePasswordDialogPresenter mPresenter;

    public CreatePasswordDialog(@NonNull Context context, String phone) {
        super(context);

        this.mPhoneStr = phone;

        IAccountManager manager = new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferenceDao(C9Application.getInstance(),
                        SharedPreferenceDao.FILE_ACCOUNT));
        mPresenter = new CreatePasswordDialogPresenterImpl(this, manager);
    }

    public CreatePasswordDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_create_pw, null);
        setContentView(root);

        mTvPhone = (TextView) findViewById(R.id.phone);
        mEtPw = (EditText) findViewById(R.id.pw);
        mEtPw1 = (EditText) findViewById(R.id.pw1);
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
     * 提交
     */
    private void submit() {
        final String pw = mEtPw.getText().toString();
        String pw1 = mEtPw1.getText().toString();
        final String phone = mPhoneStr;

        if (mPresenter.requestCheckPw(pw, pw1)){
            // 注册
            mPresenter.requestRegister(phone, pw);
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void showLoading() {
        showOrHideLoading(true);
    }

    @Override
    public void showPasswordNull() {
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.pw_is_null));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
    }

    @Override
    public void showPasswordNotEqual() {
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.pw_is_not_equals));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
    }


    @Override
    public void showError(int code, String msg) {
        showOrHideLoading(false);
        if (code == IAccountManager.SMS_SERVER_FAIL){
            mTvTips.setVisibility(View.VISIBLE);
            mTvTips.setText(getContext().getString(R.string.error_server));
            mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        } else if (code == IAccountManager.PASSWORD_ERROR){
            showOrHideLoading(false);
            mTvTips.setVisibility(View.VISIBLE);
            mTvTips.setText(getContext().getString(R.string.login_error));
            mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        }
    }

    @Override
    public void showLoginSuccess() {
        dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.login_suc), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRegisterSuccess() {
        showOrHideLoading(true);
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.register_suc_and_login));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));

        //  10/16/17 请求网络，完成自动登录
        mPresenter.requestLogin(mPhoneStr, mEtPw.getText().toString());
    }

    private void showOrHideLoading(boolean show){
        if (show){
            mPbLoading.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.GONE);
        } else {
            mPbLoading.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.VISIBLE);
        }
    }
}
