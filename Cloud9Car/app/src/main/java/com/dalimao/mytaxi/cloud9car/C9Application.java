package com.dalimao.mytaxi.cloud9car;

import android.app.Application;

/**
 * Created by zouxiaobang on 10/17/17.
 */

public class C9Application extends Application {
    private static C9Application instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static C9Application getInstance(){
        return instance;
    }
}
