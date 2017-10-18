package com.zouxiaobang.cloud9.cloud9car.common.databus;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zouxiaobang on 10/18/17.
 */

public class RxBus {
    private static final String TAG = "RxBus";
    private static RxBus mInstance ;

    private Set<DataBusSubscriber> mSubscriberSet;

    /**
     * 注册 DataBusSubscriber
     * @param subscriber
     */
    public synchronized void register(DataBusSubscriber subscriber){
        mSubscriberSet.add(subscriber);
    }

    /**
     * 注销 DataBusSubscriber
     * @param subscriber
     */
    public synchronized void unregister(DataBusSubscriber subscriber){
        mSubscriberSet.remove(subscriber);
    }

    private RxBus(){
        mSubscriberSet = new HashSet<>();
    }

    public static RxBus getInstance(){
        if (mInstance == null){
            synchronized (RxBus.class){
                if (mInstance == null){
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 包装处理过程
     * @param func
     */
    public void chainProcess(Func1 func){
        Observable.just("")
                .subscribeOn(Schedulers.io())       //数据处理在IO层
                .map(func)                          //数据处理过程
                .observeOn(AndroidSchedulers.mainThread())  //在主线程中进行观察
                .subscribe(new Action1() {
                    @Override
                    public void call(Object data) {
                        Log.d(TAG, "call: chain process start");
                        for (DataBusSubscriber subscriber: mSubscriberSet){
                            subscriber.onEvent(data);
                        }
                    }
                });
    }
}
