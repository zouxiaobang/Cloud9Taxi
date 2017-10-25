package com.dalimao.mytaxi.cloud9car.common.databus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zouxiaobang on 10/18/17.
 */

public class RxBus {
    private static final String TAG = "RxBus";
    private static RxBus mInstance;

    private Set<Object> mSubscriberSet;

    /**
     * 注册 DataBusSubscriber
     *
     * @param subscriber
     */
    public synchronized void register(Object subscriber) {
        mSubscriberSet.add(subscriber);
    }

    /**
     * 注销 DataBusSubscriber
     *
     * @param subscriber
     */
    public synchronized void unregister(Object subscriber) {
        mSubscriberSet.remove(subscriber);
    }

    private RxBus() {
        mSubscriberSet = new HashSet<>();
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 包装处理过程
     *
     * @param func
     */
    public void chainProcess(Func1 func) {
        Observable.just("")
                .subscribeOn(Schedulers.io())       //数据处理在IO层
                .map(func)                          //数据处理过程
                .observeOn(AndroidSchedulers.mainThread())  //在主线程中进行观察
//                .subscribe(new Action1() {
//                    @Override
//                    public void call(Object data) {
//                        Log.d(TAG, "call: chain process start");
//                        for (Object subscriber: mSubscriberSet){
//                            callByAnntiationMethod(subscriber, data);
//                        }
//                    }
//                });
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object data) {
                        if (data == null) {
                            return;
                        }
                        send(data);
                    }
                });
    }

    /**
     * 发送数据
     * @param data
     */
    public void send(Object data) {
        for (Object subscriber : mSubscriberSet) {
            callByAnntiationMethod(subscriber, data);
        }
    }

    private void callByAnntiationMethod(Object target, Object data) {
        Method[] methodArray = target.getClass().getDeclaredMethods();
        for (int i = 0; i < methodArray.length; i++) {
            try {
                if (methodArray[i].isAnnotationPresent(RegisterBus.class)) {
                    // 被 @RegisterBus 修饰的方法
                    Class paramType = methodArray[i].getParameterTypes()[0];
                    if (data.getClass().getName().equals(paramType.getName())) {
                        // 参数类型和 data 一样，调用此方法
                        methodArray[i].invoke(target, new Object[]{data});
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
