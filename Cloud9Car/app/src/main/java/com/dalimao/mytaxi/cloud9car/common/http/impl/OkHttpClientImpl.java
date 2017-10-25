package com.dalimao.mytaxi.cloud9car.common.http.impl;

import com.dalimao.mytaxi.cloud9car.common.http.IHttpClient;
import com.dalimao.mytaxi.cloud9car.common.http.IRequest;
import com.dalimao.mytaxi.cloud9car.common.http.IRespone;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zouxiaobang on 10/15/17.
 */

public class OkHttpClientImpl implements IHttpClient {
    OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();

    @Override
    public IRespone get(IRequest request, boolean forceCache) {
        /**
         * 解析业务逻辑
         */

        //设置请求方法
        request.setMethod(IRequest.GET);
        //设置请求头
        Request.Builder builder = new Request.Builder();
        Map<String, String> header = request.getHeader();
        for (String key: header.keySet()){
            builder.header(key, header.get(key));
        }

        //设置url
        String url = request.getUrl();
        builder.url(url).get();
        //组装Request
        Request okResquest = builder.build();
        //返回respone
        return execute(okResquest, forceCache);
    }

    @Override
    public IRespone post(IRequest request, boolean forceCache) {
        /**
         * 解析业务逻辑
         */

        Request.Builder builder = new Request.Builder();
        //设置请求方法
        request.setMethod(IRequest.POST);
        //设置方法体
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, request.getBody().toString());
        builder.post(body);
        //设置请求头
        Map<String, String> header = request.getHeader();
        for (String key: header.keySet()){
            builder.addHeader(key, header.get(key));
        }
        //设置url
        String url = request.getUrl();
        builder.url(url);
        //组装respone
        Request okRequest = builder.build();
        return execute(okRequest, forceCache);
    }


    /**
     * 请求执行过程
     * @param okResquest
     * @param forceCache
     * @return
     */
    private IRespone execute(Request okResquest, boolean forceCache) {
        BaseRespone commonResponse = new BaseRespone();

        try {
            Response response = mOkHttpClient.newCall(okResquest).execute();
            commonResponse.setData(response.body().string());
            commonResponse.setCode(BaseRespone.STATE_OK);
        } catch (IOException e) {
            commonResponse.setCode(BaseRespone.STATE_UNKNOWN_ERROR);
            commonResponse.setData(e.getMessage());
        }
        return commonResponse;
    }
}
