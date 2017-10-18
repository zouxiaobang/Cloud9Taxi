package com.zouxiaobang.cloud9.cloud9car;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zouxiaobang on 10/18/17.
 */

@RunWith(AndroidJUnit4.class)
public class TestRxJavaAndroid {
    @Test
    public void testMap(){
        String name = "zouxiaobang";

        Observable.just(name)
                .subscribeOn(Schedulers.io())
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

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        System.out.println("receive User call in thread : "
                                + Thread.currentThread().getName());
                    }
                });
    }

    class User {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
