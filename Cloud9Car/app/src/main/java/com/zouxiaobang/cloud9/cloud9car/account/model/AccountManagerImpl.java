package com.zouxiaobang.cloud9.cloud9car.account.model;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.zouxiaobang.cloud9.cloud9car.C9Application;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.Account;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.RegisterResponse;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.SmsCodeResponse;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.UserExistsResponse;
import com.zouxiaobang.cloud9.cloud9car.common.databus.RxBus;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRespone;
import com.zouxiaobang.cloud9.cloud9car.common.storage.SharedPreferenceDao;
import com.zouxiaobang.cloud9.cloud9car.common.utils.DevUtil;

import rx.functions.Func1;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class AccountManagerImpl implements IAccountManager {
    private static final String TAG = "AccountManagerImpl";


    /**
     * 网络请求库
     */
    private IHttpClient mClient;
    /**
     * 数据存储
     */
    private SharedPreferenceDao mSharedPreferenceDao;

    public AccountManagerImpl(IHttpClient client, SharedPreferenceDao dao) {
        this.mClient = client;
        this.mSharedPreferenceDao = dao;
    }


    @Override
    public void fetchSMSCode(final String phone) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                //创建url
                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IRespone respone = mClient.get(request, false);
                Log.d(TAG, "run: fetch sms code" + respone.getCode() + " : " + respone.getData());
                /** {"code":200,"msg":"code has send"} **/

                SmsCodeResponse codeResponse = new SmsCodeResponse();
                if (respone.getCode() == BaseBizResponse.STATE_OK) {
                    BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(),
                            BaseBizResponse.class);
                    Log.d(TAG, "call: code = " + bizResponse.getCode());
                    if (bizResponse.getCode() == BaseBizResponse.STATE_OK) {
                        codeResponse.setCode(SMS_SEND_SUCCESS);
                    } else {
                        codeResponse.setCode(SMS_SEND_FAIL);
                    }
                } else {
                    codeResponse.setCode(SMS_SERVER_FAIL);
                }

                return codeResponse;
            }
        });
    }

    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                //创建url
                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("code", smsCode);
                IRespone respone = mClient.get(request, false);
                Log.d(TAG, "run check sms code: " + respone.getCode() + " : " + respone.getData());
                /** {"code":200,"data":{},"msg":"succ SMS code"} **/

                SmsCodeResponse codeResponse = new SmsCodeResponse();
                if (respone.getCode() == BaseBizResponse.STATE_OK) {
                    BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(),
                            BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_OK) {
                        codeResponse.setCode(SMS_CHECK_SUCCESS);
                    } else {
                        codeResponse.setCode(SMS_CHECK_FAIL);
                    }
                } else {
                    codeResponse.setCode(SMS_SERVER_FAIL);
                }

                return codeResponse;
            }
        });
    }

    @Override
    public void checkUserExists(final String phone) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                //创建url
                String url = API.Config.getDomain() + API.CHECK_USER_EXISTS;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IRespone response = mClient.get(request, false);
                Log.d(TAG, "run check user exists: " + response.getCode() + " : " + response.getData());
                /** {"code":200,"data":{},"msg":"succ SMS code"} **/

                UserExistsResponse userResponse = new UserExistsResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    BaseBizResponse bizResponse = new Gson().fromJson(response.getData(),
                            BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_USER_EXISTS) {
                        userResponse.setCode(USER_EXISTS);
                    } else if (bizResponse.getCode() == BaseBizResponse.STATE_USER_NOT_EXISTS) {
                        userResponse.setCode(USER_NOT_EXISTS);
                    }
                } else {
                    userResponse.setCode(SMS_SERVER_FAIL);
                }

                return userResponse;
            }
        });
    }

    @Override
    public void register(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                //获取url
                String url = API.Config.getDomain() + API.REGISTER;
                //创建Request
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                request.setBody("uid", DevUtil.UUID(C9Application.getInstance()));
                //执行过程
                IRespone respone = mClient.post(request, false);
                Log.d(TAG, "run register: " + respone.getData());

                //获取数据
                RegisterResponse registerResponse = new RegisterResponse();
                if (respone.getCode() == BaseRespone.STATE_OK) {
                    BaseBizResponse bizResponse
                            = new Gson().fromJson(respone.getData(), BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_OK) {
                        registerResponse.setCode(REGISTER_SUCCESS);
                    } else {
                        registerResponse.setCode(SMS_SERVER_FAIL);
                    }
                } else {
                    registerResponse.setCode(SMS_SERVER_FAIL);
                }

                return registerResponse;
            }
        });
    }

    @Override
    public void login(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                //获取url
                String url = API.Config.getDomain() + API.LOGIN;
                //创建Request
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                //执行过程
                IRespone respone = mClient.post(request, false);
                Log.d(TAG, "run login: " + respone.getData());

                //获取数据
                LoginResponse loginResponse = new LoginResponse();
                if (respone.getCode() == BaseRespone.STATE_OK) {
                    loginResponse
                            = new Gson().fromJson(respone.getData(), LoginResponse.class);
                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK) {
                        // 10/17/17 保存登录信息
                        Account account = loginResponse.getData();
                        SharedPreferenceDao dao
                                = new SharedPreferenceDao(C9Application.getInstance(),
                                SharedPreferenceDao.FILE_ACCOUNT);
                        dao.save(SharedPreferenceDao.KEY_ACCOUNT, account);
                        loginResponse.setCode(LOGIN_SUCCESS);
                    } else if (loginResponse.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        loginResponse.setCode(PASSWORD_ERROR);
                    } else {
                        loginResponse.setCode(SMS_SERVER_FAIL);
                    }
                } else {
                    loginResponse.setCode(SMS_SERVER_FAIL);
                }

                return loginResponse;
            }
        });
    }

    @Override
    public void loginByToken() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                //获取本地登录信息
                SharedPreferenceDao dao
                        = new SharedPreferenceDao(C9Application.getInstance(),
                        SharedPreferenceDao.FILE_ACCOUNT);
                Account account = (Account) dao.get(SharedPreferenceDao.KEY_ACCOUNT, Account.class);

                //登录是否过期
                boolean tokenValid = false;
                //检查token是否过期
                if (account != null) {
                    if (account.getExpired() > System.currentTimeMillis()) {
                        //token有效
                        tokenValid = true;
                    }
                }

                Log.i(TAG, "checkLoginState: tokenvalid = " + tokenValid);
                LoginResponse loginResponse = new LoginResponse();
                if (!tokenValid) {
                    loginResponse.setCode(TOKEN_INVALID);
                    return loginResponse;
                }
                // 请求网络，完成自动登录
                //获取url
                String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                //创建Request
                IRequest request = new BaseRequest(url);
                request.setBody("token", account.getToken());
                //执行请求
                IRespone respone = mClient.post(request, false);
                Log.d(TAG, "run: " + respone.getData());
                //处理Response
                if (respone.getCode() == BaseBizResponse.STATE_OK) {
                    loginResponse
                            = new Gson().fromJson(respone.getData(), LoginResponse.class);

                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK) {
                        //存储Token
                        account = loginResponse.getData();
                        dao = new SharedPreferenceDao(C9Application.getInstance(),
                                SharedPreferenceDao.FILE_ACCOUNT);
                        dao.save(SharedPreferenceDao.KEY_ACCOUNT, account);

                        loginResponse.setCode(LOGIN_SUCCESS);
                    } else if (loginResponse.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {
                        loginResponse.setCode(TOKEN_INVALID);
                    } else {
                        loginResponse.setCode(SMS_SERVER_FAIL);
                    }
                } else {
                    loginResponse.setCode(SMS_SERVER_FAIL);
                }

                return loginResponse;
            }
        });
    }
}
