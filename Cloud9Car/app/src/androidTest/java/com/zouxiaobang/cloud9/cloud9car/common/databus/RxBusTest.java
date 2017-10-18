package com.zouxiaobang.cloud9.cloud9car.common.databus;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.functions.Func1;

/**
 * Created by zouxiaobang on 10/18/17.
 */

public class RxBusTest {
    private static final String TAG = "RxBusTest";
    private Presenter mPresenter;

    @Before
    public void setUp(){
        mPresenter = new Presenter(new Manager());
        RxBus.getInstance().register(mPresenter);
    }

    @After
    public void tearDown(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RxBus.getInstance().unregister(mPresenter);
    }

    @Test
    public void testGetUser(){
        mPresenter.getUser();
    }

    @Test
    public void testGetOrder(){
        mPresenter.getOrder();
    }




    class Presenter implements DataBusSubscriber{
        private Manager mManager;
        public Presenter(Manager manager){
            this.mManager = manager;
        }

        public void getUser(){
            mManager.getUser();
        }

        public void getOrder(){
            mManager.getOrder();
        }

        @Override
        public void onEvent(Object object) {
            if (object instanceof User){
                Log.d(TAG, "onEvent: get user in thread: " + Thread.currentThread().getName());
            } else if (object instanceof Order){
                Log.d(TAG, "onEvent: get order in thread: " + Thread.currentThread().getName());
            } else {
                Log.d(TAG, "onEvent: get data in thread: " + Thread.currentThread().getName());
            }
        }
    }


    /**
     * 模拟Model
     */
    class Manager{
        public void getUser(){
            RxBus.getInstance().chainProcess(new Func1() {
                @Override
                public Object call(Object o) {
                    Log.d(TAG, "getUser: chainProcess getUser start in thread: "
                            + Thread.currentThread().getName());

                    User user = new User();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //把User数据返回到Presenter中
                    return user;
                }
            });


        }

        public void getOrder(){
            RxBus.getInstance().chainProcess(new Func1() {
                @Override
                public Object call(Object o) {
                    Log.d(TAG, "getOrder: chainProcess getUser start in thread: "
                            + Thread.currentThread().getName());

                    Order order = new Order();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    return order;
                }
            });
        }
    }


    class  User{

    }

    class Order{

    }
}
