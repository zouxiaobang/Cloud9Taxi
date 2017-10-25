package com.dalimao.mytaxi.cloud9car.common.http.impl;

import com.google.gson.Gson;
import com.dalimao.mytaxi.cloud9car.common.http.IRequest;
import com.dalimao.mytaxi.cloud9car.common.http.api.API;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zouxiaobang on 10/15/17.
 */

public class BaseRequest implements IRequest {
    private String mMethod = POST;
    private Map<String, String> mHeader;
    private Map<String, String> mBody;
    private String mUrl;

    public BaseRequest(String url){
        this.mUrl = url;
        mHeader = new HashMap<>();
        mBody = new HashMap<>();
        mHeader.put("X-Bmob-Application-Id", API.Config.getAppId());
        mHeader.put("X-Bmob-REST-API-Key", API.Config.getAppKey());
    }

    @Override
    public void setMethod(String method) {
        this.mMethod = method;
    }

    @Override
    public void setHeader(String key, String value) {
        mHeader.put(key, value);
    }

    @Override
    public void setBody(String key, String value) {
        mBody.put(key, value);
    }

    @Override
    public String getUrl() {
        //如果请求方法为GET，则开始组装url
        if (GET.equals(mMethod)){
            for (String key: mBody.keySet()){
                mUrl = mUrl.replace("${" + key + "}", mBody.get(key));
            }
        }
        return mUrl;
    }

    @Override
    public Map<String, String> getHeader() {

        return mHeader;
    }

    @Override
    public Object getBody() {
        if (mBody != null){
            return new Gson().toJson(this.mBody, HashMap.class);
        } else {
            return "{}";
        }

    }
}
