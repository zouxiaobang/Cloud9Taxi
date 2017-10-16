package com.zouxiaobang.cloud9.cloud9car;

/**
 * Created by zouxiaobang on 10/14/17.
 */
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestOkHttp {

    /**
     * 测试Get
     */
    @Test
    public void testGet(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://httpbin.org/get")
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println("testGet: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试Post
     */
    @Test
    public void testPost(){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, "{\"name\",\"zouxiaobang\"}");
        Request request = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println("testPost: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试拦截器
     */
    @Test
    public void testInterceptor(){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                long start = System.currentTimeMillis();
                Request request = chain.request();
                Response response = chain.proceed(request);
                long end = System.currentTimeMillis();
                System.out.println("testInterceptor: " + (end - start));

                return response;
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        FormBody body = new FormBody.Builder()
                .add("name", "zouxiaobang")
                .build();
        Request request = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println("testInterceptor: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试缓存
     */
    @Test
    public void testCache(){
        Cache cache = new Cache(new File("cache.cache"), 1024*1024);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        FormBody body = new FormBody.Builder()
                .add("name", "zouxiaobang")
                .build();
        Request request = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Response responseCache = response.cacheResponse();
            Response responseNet = response.networkResponse();
            if (responseCache != null){
                System.out.println("testCache: cache");
            }
            if (responseNet != null){
                System.out.println("testCache: net work");
            }
            System.out.println("testCache: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
