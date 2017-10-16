package com.zouxiaobang.cloud9.cloud9car.common.http.api;

/**
 * Created by zouxiaobang on 10/15/17.
 */

public class API {
    public static final String TEST_GET = "/get?uid=${uid}";
    public static final String TEST_POST = "/post";


    /**
     * 配置域名信息
     */
    public static class Config{
        private static final String TEST_DOMAIN = "http://httpbin.org";
        private static final String RELEASE_DOMAIN = "http://httpbin.org";
        private static String domain = TEST_DOMAIN;

        public static void setDebug(boolean debug){
            domain = debug? TEST_DOMAIN : RELEASE_DOMAIN;
        }

        public static String getDomain(){
            return domain;
        }

        public static String getAppId(){
            return "";
        }

        public static String getAppKey(){
            return "";
        }
    }
}
