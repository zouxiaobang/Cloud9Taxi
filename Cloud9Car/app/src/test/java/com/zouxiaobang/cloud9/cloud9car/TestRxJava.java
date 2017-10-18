package com.zouxiaobang.cloud9.cloud9car;

/**
 * Created by zouxiaobang on 10/18/17.
 */
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.UserDataHandler;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TestRxJava {
    @Before
    public void setUp(){
        Thread.currentThread().setName("currentThread");
    }

    @Test
    public void testSubscribe(){
        //观察者/订阅者
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted in thread: " + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError in thread: " + Thread.currentThread().getName());
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext in thread: " + Thread.currentThread().getName());
                System.out.println(s);
            }
        };

        //被观察者
        Observable observable = Observable.create(new Observable.OnSubscribe<Subscriber>(){
            @Override
            public void call(Subscriber subscriber) {
                //发生的事件
                System.out.println("call in thread: " + Thread.currentThread().getName());
                subscriber.onStart();
                subscriber.onNext("hello world");
                subscriber.onCompleted();
            }
        });

        //订阅
//        observable.subscribe(subscriber);
        observable.subscribeOn(Schedulers.io())     //UI线程
                .observeOn(Schedulers.newThread())  //子线程
                .subscribe(subscriber);
    }

    @Test
    public void testMap(){
        String name = "zouxiaobang";

        Observable.just(name)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<String, User>() {
                    @Override
                    public User call(String name) {
                        User user = new User();
                        user.setName(name);
                        System.out.println("process User call in thread : "
                                + Thread.currentThread().getName());
                        return user;
                    }
                })

                .subscribeOn(Schedulers.newThread())
                .map(new Func1<User, Object>() {
                    @Override
                    public Object call(User user) {
                        System.out.println("process User call in thread : "
                                + Thread.currentThread().getName());
                        return user;
                    }
                })

                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        System.out.println("receive User call in thread : "
                                + Thread.currentThread().getName());
                    }
                });
    }
}
