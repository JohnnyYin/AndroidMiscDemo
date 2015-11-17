package com.johnnyyin.temp;

import android.util.Log;

/**
 * Created by Johnny on 15/11/11.
 */
public class TestSingleton {
    private volatile static TestSingleton ourInstance;

    public static TestSingleton getInstance() {
        Log.e("SSS", "getInstance:" + Thread.currentThread().getId());
        if (ourInstance == null) {
            Log.e("SSS", "getInstance1:" + Thread.currentThread().getId());
            synchronized (TestSingleton.class) {
                Log.e("SSS", "getInstance2:" + Thread.currentThread().getId());
                if (ourInstance == null) {
                    Log.e("SSS", "getInstance3:" + Thread.currentThread().getId());
                    ourInstance = new TestSingleton();
                }
            }
        }
        return ourInstance;
    }

    private TestSingleton() {
        Log.e("SSS", "new instance:" + Thread.currentThread().getId());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
