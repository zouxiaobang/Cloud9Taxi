package com.dalimao.mytaxi.cloud9car.common.storage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by zouxiaobang on 10/17/17.
 * SharedPreference数据访问对象
 */

public class SharedPreferenceDao {
    private static final String TAG = "SharedPreferenceDao";
    public static final String FILE_ACCOUNT = "FILE_ACCOUNT";
    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private SharedPreferences mSharedPreferences;

    public SharedPreferenceDao(Application application, String fileName){
        mSharedPreferences = application.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void save(String key, String value){
        mSharedPreferences.edit().putString(key, value).commit();
    }

    public String get(String key){
        return mSharedPreferences.getString(key, null);
    }

    public void save(String key, Object o){
        String value = new Gson().toJson(o);
        save(key, value);
    }

    public Object get(String key, Class clz){
        String value = get(key);
        try {
            if (value != null){
                Object o = new Gson().fromJson(value, clz);
                return o;
            }
        } catch (Exception e){
            Log.e(TAG, "get: " + e.getMessage());
        }
        return null;
    }
}
