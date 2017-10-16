package com.zouxiaobang.cloud9.cloud9car.common.http.impl;

import com.zouxiaobang.cloud9.cloud9car.common.http.IHttpClient;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRequest;
import com.zouxiaobang.cloud9.cloud9car.common.http.IRespone;
import com.zouxiaobang.cloud9.cloud9car.common.http.api.API;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by zouxiaobang on 10/15/17.
 */
public class OkHttpClientImplTest {
    IHttpClient mIHttpClient ;

    @Before
    public void setUp() throws Exception {
        mIHttpClient = new OkHttpClientImpl();
        API.Config.setDebug(true);
    }

    @Test
    public void get() throws Exception {
        //url
        String url = API.Config.getDomain() + API.GET_SMS_CODE;
        //request对象
        IRequest request = new BaseRequest(url);
        request.setBody("phone", "123456");
        request.setHeader("testHeader", "test header");
        //测试GET
        IRespone respone = mIHttpClient.get(request,false);
        System.out.println("test get: " + respone.getCode());
        System.out.println("test get: " + respone.getData());
    }

    @Test
    public void post() throws Exception {
        //获取url
        String url = API.Config.getDomain() + API.TEST_GET;
        //创建request对象
        IRequest request = new BaseRequest(url);
        request.setBody("uid", "123456");
        request.setBody("name", "zouxiaobang");
        request.setHeader("testHeader", "test header");
        //测试POST
        IRespone respone = mIHttpClient.post(request, false);

        System.out.println("test post: " + respone.getCode());
        System.out.println("test post: " + respone.getData());
    }

}