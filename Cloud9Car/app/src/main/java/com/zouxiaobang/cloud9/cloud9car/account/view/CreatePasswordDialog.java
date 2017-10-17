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

import com.google.gson.Gson;
import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.OkHttpClientImpl;
import com.zouxiaobang.cloud9.cloud9car.common.utils.DevUtil;

import java.lang.ref.SoftReference;

/**
 * Created by zouxiaobang on 10/16/17.
 * 创建密码和修改密码的对话框
 */

public class CreatePasswordDialog extends Dialog {

    private static final String TAG = "CreatePasswordDialog";
    private static final int REGISTER_SUCCESS = 1;
    private static final int REGISTER_FAIL = -1;
    private static final int SERVER_FAIL = 100;
    private TextView mTvTitle;
    private TextView mTvPhone;
    private EditText mEtPw;
    private EditText mEtPw1;
    private Button mBtnConfirm;
    private ProgressBar mPbLoading;
    private TextView mTvTips;

    private IHttpClient mClient;
    private String mPhoneStr;
    private MyHandler mHandler;

    public CreatePasswordDialog(@NonNull Context context, String phone) {
        super(context);

        this.mPhoneStr = phone;
        mClient = new OkHttpClientImpl();
        mHandler = new MyHandler(this);
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

        mTvTitle = (TextView) findViewById(R.id.dialog_title);
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

        if (checkPassword(pw, pw1)){
            //将手机号和密码保存到网络
            new Thread(){
                @Override
                public void run() {
                    //获取url
                    String url = API.Config.getDomain() + API.REGISTER;
                    //创建Request
                    IRequest request = new BaseRequest(url);
                    request.setBody("phone", phone);
                    request.setBody("password", pw);
                    request.setBody("uid", DevUtil.UUID(getContext()));
                    //执行过程
                    IRespone respone = mClient.post(request, false);
                    Log.d(TAG, "run: " + respone.getData());
                    //获取数据
                    if (respone.getCode() == BaseRespone.STATE_OK){
                        BaseBizResponse bizResponse
                                = new Gson().fromJson(respone.getData(), BaseBizResponse.class);
                        if (bizResponse.getCode() == BaseBizResponse.STATE_OK){
                            mHandler.sendEmptyMessage(REGISTER_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(REGISTER_FAIL);
                        }
                    } else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                }
            }.start();
        }
    }

    private boolean checkPassword(String pw, String pw1) {
        if (TextUtils.isEmpty(pw)){
            mTvTips.setVisibility(View.VISIBLE);
            mTvTips.setText(getContext().getString(R.string.pw_is_null));
            mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
            return false;
        }
        if (!pw.equals(pw1)){
            mTvTips.setVisibility(View.VISIBLE);
            mTvTips.setText(getContext().getString(R.string.pw_is_not_equals));
            mTvTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
            return false;
        }
        return true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    static class MyHandler extends Handler{
        private SoftReference<CreatePasswordDialog> mCreatePasswordDialogSoftReference;

        public MyHandler(CreatePasswordDialog dialog){
            mCreatePasswordDialogSoftReference = new SoftReference<CreatePasswordDialog>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            CreatePasswordDialog dialog = mCreatePasswordDialogSoftReference.get();
            if (dialog == null)
                return;

            //  10/16/17 UI事件响应
            switch (msg.what){
                case REGISTER_SUCCESS:
                    dialog.showRegisterSuccess();
                    break;
                case REGISTER_FAIL:
                    break;
                case SERVER_FAIL:
                    break;
            }
        }
    }

    private void showRegisterSuccess() {
        mPbLoading.setVisibility(View.VISIBLE);
        mBtnConfirm.setVisibility(View.GONE);
        mTvTips.setVisibility(View.VISIBLE);
        mTvTips.setText(getContext().getString(R.string.register_suc_and_login));
        mTvTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));

        // TODO: 10/16/17 请求网络，完成自动登录

    }
}
