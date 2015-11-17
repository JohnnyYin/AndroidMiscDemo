package com.johnnyyin.temp;

import android.app.Application;

/**
 * Created by Johnny on 15/11/3.
 */
public class MyApplication extends Application {
    public static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
