package com.dalimao.mytaxi.cloud9car.common.http;


/**
 * Created by zouxiaobang on 10/15/17.
 */

public interface IHttpClient {
    public IRespone get(IRequest request, boolean forceCache);
    public IRespone post(IRequest request, boolean forceCache);
}
