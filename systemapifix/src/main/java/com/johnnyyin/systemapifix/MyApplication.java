package com.johnnyyin.systemapifix;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by Johnny on 15/11/28.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Class s = null;
//        try {
//            s = Class.forName("android.media.MediaPlayer", true, MainActivity.class.getClassLoader());
//            Log.e("SS", s.getClassLoader() + "");
//            Field classLoader = Class.class.getDeclaredField("classLoader");
//            classLoader.setAccessible(true);
//            classLoader.set(s, MainActivity.class.getClassLoader());
//        } catch (Throwable e) {
//            e.printStackTrace();
//            Log.e("SS", "" + Log.getStackTraceString(e));
//        }
    }
}
