package com.zouxiaobang.cloud9.cloud9car.account.model;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.zouxiaobang.cloud9.cloud9car.C9Application;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.Account;
import com.zouxiaobang.cloud9.cloud9car.account.model.response.LoginResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;
import com.zouxiaobang.cloud9.cloud9car.common.http.biz.BaseBizResponse;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.impl.BaseRespone;
import com.zouxiaobang.cloud9.cloud9car.common.storage.SharedPreferenceDao;
import com.zouxiaobang.cloud9.cloud9car.common.utils.DevUtil;

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
    private Handler mHandler;

    public AccountManagerImpl(IHttpClient client, SharedPreferenceDao dao){
        this.mClient = client;
        this.mSharedPreferenceDao = dao;
    }


    @Override
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void fetchSMSCode(final String phone) {
        new Thread(){
            @Override
            public void run() {
                //创建url
                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IRespone respone = mClient.get(request, false);
                Log.d(TAG, "run: " + respone.getCode() + " : " + respone.getData());
                /** {"code":200,"msg":"code has send"} **/

                if (respone.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(),
                            BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(SMS_SEND_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        new Thread(){
            @Override
            public void run() {
                //创建url
                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("code", smsCode);
                IRespone respone = mClient.get(request, false);
                Log.d(TAG, "run: " + respone.getCode() + " : " + respone.getData());
                /** {"code":200,"data":{},"msg":"succ SMS code"} **/

                if (respone.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(),
                            BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(SMS_CHECK_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void checkUserExists(final String phone) {
        new Thread(){
            @Override
            public void run() {
                //创建url
                String url = API.Config.getDomain() + API.CHECK_USER_EXISTS;
                //创建Request对象
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IRespone respone = mClient.get(request, false);
                Log.d(TAG, "run: " + respone.getCode() + " : " + respone.getData());
                /** {"code":200,"data":{},"msg":"succ SMS code"} **/

                if (respone.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizResponse = new Gson().fromJson(respone.getData(),
                            BaseBizResponse.class);
                    if (bizResponse.getCode() == BaseBizResponse.STATE_USER_EXISTS){
                        mHandler.sendEmptyMessage(USER_EXISTS);
                    } else if (bizResponse.getCode() == BaseBizResponse.STATE_USER_NOT_EXISTS){
                        mHandler.sendEmptyMessage(USER_NOT_EXISTS);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void register(final String phone, final String password) {
        new Thread(){
            @Override
            public void run() {
                //获取url
                String url = API.Config.getDomain() + API.REGISTER;
                //创建Request
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                request.setBody("uid", DevUtil.UUID(C9Application.getInstance()));
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
                        mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void login(final String phone, final String password) {
        new Thread(){
            @Override
            public void run() {
                //获取url
                String url = API.Config.getDomain() + API.LOGIN;
                //创建Request
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                //执行过程
                IRespone respone = mClient.post(request, false);
                Log.d(TAG, "run: " + respone.getData());

                //获取数据
                if (respone.getCode() == BaseRespone.STATE_OK){
                    LoginResponse loginResponse
                            = new Gson().fromJson(respone.getData(), LoginResponse.class);
                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK){
                        // 10/17/17 保存登录信息
                        Account account = loginResponse.getData();
                        SharedPreferenceDao dao
                                = new SharedPreferenceDao(C9Application.getInstance(),
                                SharedPreferenceDao.FILE_ACCOUNT);
                        dao.save(SharedPreferenceDao.KEY_ACCOUNT, account);
                        mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                    } else if (loginResponse.getCode() == BaseBizResponse.STATE_PW_ERR){
                        mHandler.sendEmptyMessage(PASSWORD_ERROR);
                    } else {
                        mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void loginByToken() {
        //获取本地登录信息
        SharedPreferenceDao dao
                = new SharedPreferenceDao(C9Application.getInstance(),
                SharedPreferenceDao.FILE_ACCOUNT);
        final Account account = (Account) dao.get(SharedPreferenceDao.KEY_ACCOUNT, Account.class);

        //登录是否过期
        boolean tokenValid = false;
        //检查token是否过期
        if (account != null){
            if (account.getExpired() > System.currentTimeMillis()){
                //token有效
                tokenValid = true;
            }
        }

        Log.i(TAG, "checkLoginState: tokenvalid = " + tokenValid);
        if (!tokenValid) {
            mHandler.sendEmptyMessage(TOKEN_INVALID);
        } else {
            // 请求网络，完成自动登录
            new Thread(){
                @Override
                public void run() {
                    //获取url
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    //创建Request
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.getToken());
                    //执行请求
                    IRespone respone = mClient.post(request, false);
                    Log.d(TAG, "run: " + respone.getData());
                    //处理Response
                    if (respone.getCode() == BaseBizResponse.STATE_OK){
                        LoginResponse loginResponse
                                = new Gson().fromJson(respone.getData(), LoginResponse.class);

                        if (loginResponse.getCode() == BaseBizResponse.STATE_OK){
                            //存储Token
                            Account account = loginResponse.getData();
                            SharedPreferenceDao dao
                                    = new SharedPreferenceDao(C9Application.getInstance(),
                                    SharedPreferenceDao.FILE_ACCOUNT);
                            dao.save(SharedPreferenceDao.KEY_ACCOUNT, account);

                            mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                        } else if (loginResponse.getCode() == BaseBizResponse.STATE_TOKEN_INVALID){
                            mHandler.sendEmptyMessage(TOKEN_INVALID);
                        } else {
                            mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                        }
                    } else {
                        mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                    }
                }
            }.start();
        }
    }
}
