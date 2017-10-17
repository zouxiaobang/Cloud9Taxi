package com.zouxiaobang.cloud9.cloud9car.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zouxiaobang.cloud9.cloud9car.R;
import com.zouxiaobang.cloud9.cloud9car.common.utils.FormatUtil;

/**
 * Created by zouxiaobang on 10/16/17.
 */

public class PhoneInputDialog extends Dialog {
    private View mRoot;
    private EditText mPhone;
    private Button mNext;

    public PhoneInputDialog(@NonNull Context context) {
        this(context, R.style.Dialog);
    }

    public PhoneInputDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.dialog_phone_input, null);
        setContentView(mRoot);
        initListener();
    }

    private void initListener() {
        mNext = (Button) mRoot.findViewById(R.id.btn_next);
        mNext.setEnabled(false);
        mPhone = (EditText) mRoot.findViewById(R.id.phone);
        //手机输入过程中检查手机号码是否合法
        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                check();
            }
        });
        //按钮注册监听
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                String phone = mPhone.getText().toString();
                // 10/16/17 显示输入验证码的输入框
                SmsCodeDialog dialog = new SmsCodeDialog(getContext(), phone);
                dialog.show();
            }
        });
        //关闭按钮注册监听事件
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneInputDialog.this.dismiss();
            }
        });
    }

    private void check() {
        String phone = mPhone.getText().toString();
        boolean legal = FormatUtil.checkMobile(phone);
        mNext.setEnabled(legal);
    }
}
