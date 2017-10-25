package com.dalimao.mytaxi.cloud9car.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.cloud9car.C9Application;
import com.dalimao.mytaxi.cloud9car.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.cloud9car.account.model.IAccountManager;
import com.dalimao.mytaxi.cloud9car.account.presenter.ILoginDialogPresenter;
import com.dalimao.mytaxi.cloud9car.common.databus.RxBus;
import com.dalimao.mytaxi.cloud9car.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.cloud9car.common.storage.SharedPreferenceDao;
import com.dalimao.mytaxi.cloud9car.account.presenter.LoginDialogPresenterImpl;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class LoginDialog extends Dialog implements ILoginDialogView {
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
    private ILoginDialogPresenter mPresenter;

    public LoginDialog(@NonNull Context context, String phone) {
        super(context);
        mPhoneStr = phone;

        IAccountManager manager = new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferenceDao(C9Application.getInstance(),
                        SharedPreferenceDao.FILE_ACCOUNT));
        mPresenter = new LoginDialogPresenterImpl(this, manager);
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

        RxBus.getInstance().register(mPresenter);
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
        mPresenter.requestLogin(mPhoneStr, password);
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

        RxBus.getInstance().unregister(mPresenter);
    }

    @Override
    public void showLoading() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(int code, String msg) {
        switch (code){
            case IAccountManager.PASSWORD_ERROR:
                showPwError();
                break;
            case IAccountManager.SMS_SERVER_FAIL:
                showServerError();
                break;
        }
    }

    @Override
    public void showLoginSuccess() {
        dismiss();
        mPbLoading.setVisibility(View.GONE);
        mBtnConfirm.setVisibility(View.GONE);
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.login_suc));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        Toast.makeText(getContext(), getContext().getString(R.string.login_suc), Toast.LENGTH_SHORT).show();
    }
}
